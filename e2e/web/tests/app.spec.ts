import { expect, test, type Page } from "@playwright/test";
import fs from "node:fs/promises";
import path from "node:path";

const fixturePath = path.resolve(__dirname, "../../fixtures/firebase/happy.json");

const tagElements = (page: Page, tag: string) => page.locator(`[aria-label="${tag}"]`);
const byTag = (page: Page, tag: string) => page.locator(`[aria-label="${tag}"]:visible`).first();

async function clickTag(page: Page, tag: string) {
  const element = tagElements(page, tag).last();
  await element.waitFor({ state: "attached" });
  await element.evaluate((node: HTMLElement) => node.click());
}

async function openSeededApp(page: Page) {
  await page.addInitScript(() => {
    if (sessionStorage.getItem("dubovozki.e2e.initialized") !== "true") {
      localStorage.clear();
      localStorage.setItem("language", "en");
      sessionStorage.setItem("dubovozki.e2e.initialized", "true");
    }
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
  await clickTag(page, "nav.services");
  await expect(byTag(page, "services.linen")).toBeVisible();
  await clickTag(page, "nav.settings");
  await expect(byTag(page, "settings.language")).toBeVisible();
});

test("Firebase listener applies a realtime schedule update", async ({ page }) => {
  const response = await page.request.put(
    "http://127.0.0.1:9000/busSchedule.json?ns=demo-dubovozki",
    {
      data: {
        revision: "web-realtime-v2",
        busList: [1, 2, 3, 7].map((dayOfWeek) => ({
          id: 502,
          dayOfWeek,
          dayTime: 43200000,
          dayTimeString: "12:00",
          direction: "msk",
          station: "odn",
        })),
      },
    },
  );
  expect(response.ok()).toBeTruthy();
  await expect(byTag(page, "bus.item.502")).toBeVisible();
});

test("settings survive a browser reload", async ({ page }) => {
  await clickTag(page, "nav.settings");
  await clickTag(page, "settings.theme");
  await clickTag(page, "settings.theme.dark");
  await expect.poll(() => page.evaluate(() => localStorage.getItem("theme_mode"))).toBe("dark");
  await page.reload();
  await expect(byTag(page, "app.content")).toBeVisible();
  await clickTag(page, "nav.settings");
  await clickTag(page, "settings.language");
  await clickTag(page, "settings.language.english");

  await expect.poll(() => page.evaluate(() => localStorage.getItem("language"))).toBe("en");
  await page.reload();
  await expect(byTag(page, "app.content")).toBeVisible();
  await clickTag(page, "nav.settings");
  await expect(byTag(page, "settings.theme.current.dark")).toBeVisible();
  await expect(byTag(page, "settings.language.current.english")).toBeVisible();
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

test("long localized actions remain reachable on phone and tablet", async ({ page }) => {
  await page.evaluate(() => localStorage.setItem("language", "kk"));
  await page.reload();
  await page.setViewportSize({ width: 599, height: 900 });
  await expect(byTag(page, "nav.services")).toBeInViewport();
  await expect(byTag(page, "nav.settings")).toBeInViewport();

  await page.setViewportSize({ width: 600, height: 900 });
  await expect(byTag(page, "app.navigation.rail")).toBeVisible();
  await expect(byTag(page, "nav.settings")).toBeInViewport();
});

test("system theme follows browser color scheme", async ({ page }) => {
  await page.evaluate(() => localStorage.setItem("theme_mode", "system"));
  await page.emulateMedia({ colorScheme: "dark" });
  await page.reload();
  await expect(byTag(page, "app.theme.dark")).toBeVisible();

  await page.emulateMedia({ colorScheme: "light" });
  await expect(byTag(page, "app.theme.light")).toBeVisible();
});
