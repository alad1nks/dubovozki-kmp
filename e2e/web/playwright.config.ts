import { defineConfig, devices } from "@playwright/test";
import path from "node:path";

const repositoryRoot = path.resolve(__dirname, "../..");
const gradleCommand = process.platform === "win32" ? "gradlew.bat" : "./gradlew";
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
        command: `${gradleCommand} :composeApp:jsBrowserDevelopmentRun --no-configuration-cache`,
        cwd: repositoryRoot,
        url: "http://127.0.0.1:8080",
        reuseExistingServer: !process.env.CI,
        timeout: 180_000,
      },
    ];

export default defineConfig({
  testDir: "./tests",
  fullyParallel: false,
  forbidOnly: Boolean(process.env.CI),
  failOnFlakyTests: Boolean(process.env.CI),
  retries: process.env.CI ? 1 : 0,
  reporter: process.env.CI ? [["line"], ["html", { open: "never" }]] : "list",
  outputDir: "test-results",
  use: {
    baseURL: process.env.E2E_BASE_URL ?? "http://127.0.0.1:8080",
    trace: "retain-on-failure",
    screenshot: "only-on-failure",
    video: "retain-on-failure",
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
