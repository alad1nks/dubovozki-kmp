import { defineConfig, devices } from "@playwright/test";
import path from "node:path";

const repositoryRoot = path.resolve(__dirname, "../..");
const gradleCommand = process.platform === "win32" ? "gradlew.bat" : "./gradlew";
const managedWebPort = "9080";
const isCi = Boolean(process.env.CI);
const webCommand = process.env.E2E_WEB_RELEASE
  ? `${gradleCommand} :composeApp:jsBrowserProductionWebpack && node e2e/web/static-server.mjs composeApp/build/kotlin-webpack/js/productionExecutable ${managedWebPort} composeApp/build/processedResources/js/main`
  : `${gradleCommand} :composeApp:jsBrowserDevelopmentRun --no-configuration-cache`;
const managedWebServers = process.env.E2E_EXTERNAL_SERVERS
  ? undefined
  : [
      {
        command: "firebase emulators:start --project demo-dubovozki --only database",
        cwd: repositoryRoot,
        url: "http://127.0.0.1:4400/emulators",
        reuseExistingServer: !process.env.CI,
        timeout: 120_000,
      },
      {
        command: webCommand,
        cwd: repositoryRoot,
        ...(process.env.E2E_WEB_RELEASE
          ? {}
          : { env: { DUBOVOZKI_E2E_WEB_PORT: managedWebPort } }),
        url: `http://127.0.0.1:${managedWebPort}`,
        reuseExistingServer: !process.env.CI,
        timeout: process.env.E2E_WEB_RELEASE ? 1_200_000 : 600_000,
      },
    ];

export default defineConfig({
  testDir: "./tests",
  fullyParallel: false,
  // Every project seeds and mutates the same Firebase Emulator namespace.
  workers: 1,
  forbidOnly: isCi,
  failOnFlakyTests: isCi,
  retries: isCi ? 1 : 0,
  reporter: isCi ? [["line"], ["html", { open: "never" }]] : "list",
  outputDir: "test-results",
  use: {
    baseURL: process.env.E2E_BASE_URL ?? `http://127.0.0.1:${managedWebPort}`,
    trace: "retain-on-failure",
    screenshot: "only-on-failure",
    video: isCi ? "on" : "retain-on-failure",
  },
  webServer: managedWebServers,
  projects: [
    { name: "chromium", use: { ...devices["Desktop Chrome"] } },
    { name: "firefox", use: { ...devices["Desktop Firefox"] } },
    { name: "webkit", use: { ...devices["Desktop Safari"] } },
    {
      name: "mobile-chromium",
      use: { ...devices["Pixel 7"], viewport: { width: 599, height: 900 } },
    },
  ],
});
