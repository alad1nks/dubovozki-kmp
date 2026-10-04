import assert from "node:assert/strict";
import { createServer } from "node:http";
import { once } from "node:events";
import { test } from "node:test";
import { waitForFirebase } from "./wait-for-firebase.mjs";

async function serverFor(t, handler) {
  const server = createServer(handler);
  server.listen(0, "127.0.0.1");
  await once(server, "listening");
  t.after(() => {
    server.closeAllConnections();
    server.close();
  });
  return { server, url: `http://127.0.0.1:${server.address().port}/.json?ns=demo-test` };
}

test("waits for the database HTTP endpoint to become ready", async (t) => {
  let requests = 0;
  const { url } = await serverFor(t, (request, response) => {
    assert.equal(request.method, "GET");
    assert.equal(request.url, "/.json?ns=demo-test");
    response.writeHead(++requests < 3 ? 503 : 200);
    response.end("null");
  });
  await waitForFirebase(url, { timeoutMs: 2_000, intervalMs: 10 });
  assert.equal(requests, 3);
});

test("retries a refused connection while the database starts", async (t) => {
  const { server, url } = await serverFor(t, (_, response) => response.end("null"));
  const port = server.address().port;
  await new Promise((resolve) => server.close(resolve));
  const restart = setTimeout(() => server.listen(port, "127.0.0.1"), 100);
  t.after(() => clearTimeout(restart));
  await waitForFirebase(url, { timeoutMs: 2_000, intervalMs: 10 });
});

test("fails when the database never becomes ready", async (t) => {
  const { url } = await serverFor(t, (_, response) => {
    response.writeHead(503);
    response.end();
  });
  await assert.rejects(waitForFirebase(url, { timeoutMs: 100, intervalMs: 10 }), {
    message: /did not become ready/,
  });
});

test("bounds the wait even if the database accepts but never responds", async (t) => {
  const { url } = await serverFor(t, () => {});
  await assert.rejects(waitForFirebase(url, { timeoutMs: 100, intervalMs: 10 }), /did not become ready/);
});
