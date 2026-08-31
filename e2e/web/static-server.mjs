import { createReadStream, existsSync } from "node:fs";
import { createServer } from "node:http";
import { extname, join, normalize } from "node:path";

const roots = [process.argv[2], process.argv[4]].filter(Boolean).map((root) => normalize(root));
const port = Number(process.argv[3] ?? 9080);
const contentTypes = {
  ".html": "text/html; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".mjs": "text/javascript; charset=utf-8",
  ".wasm": "application/wasm",
  ".json": "application/json; charset=utf-8",
};

createServer((request, response) => {
  const requestedPath = decodeURIComponent(new URL(request.url ?? "/", "http://localhost").pathname);
  const relativePath = requestedPath === "/" ? "index.html" : requestedPath.replace(/^\/+/, "");
  const resolveFile = (path) =>
    roots
      .map((root) => normalize(join(root, path)))
      .find((candidate, index) => candidate.startsWith(roots[index]) && existsSync(candidate));
  const filePath = resolveFile(relativePath) ?? resolveFile("index.html");

  if (!filePath) {
    response.statusCode = 404;
    response.end("Not found");
    return;
  }

  response.setHeader("Content-Type", contentTypes[extname(filePath)] ?? "application/octet-stream");
  createReadStream(filePath)
    .on("error", () => {
      response.statusCode = 404;
      response.end("Not found");
    })
    .pipe(response);
}).listen(port, "127.0.0.1", () => {
  console.log(`Serving ${roots.join(", ")} on http://127.0.0.1:${port}`);
});
