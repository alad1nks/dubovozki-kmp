import { expect, test, type Page } from "@playwright/test";
import fs from "node:fs/promises";
import path from "node:path";

const fixturePath = path.resolve(__dirname, "../../fixtures/firebase/happy.json");

const byTag = (page: Page, tag: string) => page.getByLabel(tag, { exact: true }).first();

async function openSeededApp(page: Page) {
  await page.addInitScript(() => {
    localStorage.clear();
    localStorage.setItem("language", "en");
    (window as Window & { firebaseConfig?: object }).firebaseConfig = {
      apiKey: "demo-api-key",
      authDomain: "demo-dubovozki.firebaseapp.com",
      databaseURL: "http://127.0.0.1:9000?ns=demo-dubovozki",
      projectId: "demo-dubovozki",
      appId: "demo-app-id",
    };
    (window as Window & { __openedUris?: string[] }).__openedUris = [];
    const originalOpen = window.open;
    window.open = ((url?: string | URL) => {
      (window as Window & { __openedUris?: string[] }).__openedUris?.push(String(url));
      return null;
    }) as typeof originalOpen;
  });
  const fixture = await fs.readFile(fixturePath, "utf8");
  const response = await page.request.put("http://127.0.0.1:9000/.json?ns=demo-dubovozki", {
    data: JSON.parse(fixture),
  });
  expect(response.ok()).toBeTruthy();
  await page.goto("/?e2e=true");
  await expect(byTag(page, "app.content")).toBeVisible();
}

test.beforeEach(async ({ page }) => {
  await openSeededApp(page);
});

test("P0 launch, schedule, services and settings", async ({ page }) => {
  await expect(byTag(page, "nav.schedule")).toBeVisible();
  await byTag(page, "nav.services").click({ force: true });
  await expect(byTag(page, "services.linen")).toBeVisible();
  await byTag(page, "nav.settings").click({ force: true });
  await expect(byTag(page, "settings.language")).toBeVisible();
});

test("settings survive a browser reload", async ({ page }) => {
  await byTag(page, "nav.settings").click({ force: true });
  await byTag(page, "settings.theme").click({ force: true });
  await byTag(page, "settings.theme.dark").click({ force: true });
  await byTag(page, "settings.language").click({ force: true });
  await byTag(page, "settings.language.english").click({ force: true });

  await expect.poll(() => page.evaluate(() => localStorage.length)).toBeGreaterThan(0);
  await page.reload();
  await expect(byTag(page, "app.content")).toBeVisible();
  await byTag(page, "nav.settings").click({ force: true });
  await expect(byTag(page, "settings.language")).toBeVisible();
});

test("599px mobile viewport uses bottom navigation", async ({ page }) => {
  await page.setViewportSize({ width: 599, height: 900 });
  await expect(byTag(page, "app.navigation.bottom")).toBeVisible();
  await expect(byTag(page, "app.navigation.rail")).toHaveCount(0);
});

test("600px viewport uses navigation rail", async ({ page }) => {
  await page.setViewportSize({ width: 600, height: 900 });
  await expect(byTag(page, "app.navigation.rail")).toBeVisible();
  await expect(byTag(page, "app.navigation.bottom")).toHaveCount(0);
});
