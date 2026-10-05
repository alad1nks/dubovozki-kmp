import { setTimeout as delay } from "node:timers/promises";
import { pathToFileURL } from "node:url";

// The Emulator Hub can respond before the database process accepts connections.
export async function waitForFirebase(url, { timeoutMs = 120_000, intervalMs = 500 } = {}) {
  const deadline = performance.now() + timeoutMs;
  let lastError;
  while (performance.now() < deadline) {
    try {
      const response = await fetch(url, {
        signal: AbortSignal.timeout(Math.max(1, Math.min(2_000, Math.ceil(deadline - performance.now())))),
      });
      await response.arrayBuffer();
      if (response.ok) return;
      lastError = new Error(`HTTP ${response.status}`);
    } catch (error) {
      lastError = error;
    }
    await delay(Math.max(0, Math.min(intervalMs, deadline - performance.now())));
  }
  throw new Error(`Firebase database did not become ready within ${timeoutMs}ms`, { cause: lastError });
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  const projectId = process.argv[2];
  if (!projectId) throw new Error("Usage: node e2e/wait-for-firebase.mjs <project-id>");
  await waitForFirebase(`http://127.0.0.1:9000/.json?ns=${encodeURIComponent(projectId)}`);
  console.log("Firebase database is ready for fixture seeding.");
}
