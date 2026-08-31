# E2E implementation handoff

This file is a temporary checkpoint for continuing the implementation in another Codex task.
Delete it together with `docs/e2e-testing-plan.md` before the final pull request.

## Goal

Complete the full plan in `docs/e2e-testing-plan.md`, verify it, remove both plan and handoff files, push the
branch, and create a pull request into `main`.

## Current branch and baseline

- Branch: `codex/e2e-testing`.
- Baseline commit: `b759095 docs: add e2e testing plan (#51)`.
- The original worktree was clean and on `main`.
- Local Gradle requires `JAVA_HOME=C:\Users\al1ks\.jdks\corretto-18.0.2`.
- Gradle/network commands need sandbox escalation in this environment.

## Implemented

- Centralized stable test tags and accessibility-friendly selectors across navigation, bus schedule, services,
  service schedule, settings, loading/error/retry/offline states, and deterministic list items.
- `App` supports module overrides and an isolated Koin application while preserving production defaults.
- Moscow time is injectable through `MoscowTimeProvider`; production still uses the system clock in UTC+3.
- Desktop Firebase REST URL can only be overridden with a localhost HTTP endpoint via
  `DUBOVOZKI_FIREBASE_DATABASE_URL`.
- Web Firebase supports emulator mode only with `?e2e=true`.
- Android Firebase supports emulator mode only through explicit test JVM properties.
- Version-controlled Firebase/state fixtures and clock fixtures were added.
- Shared Compose application E2E harness uses real ViewModels/domain/data and fake API/storage/time/URI boundaries.
- Twelve shared scenarios cover launch/navigation, bus filters, empty/error/retry/invalid data, services links,
  service schedule, settings persistence/localization, and loading.
- Per-test Koin isolation fixed state leakage. The latest full JVM run passed 11/12 tests; the remaining failure was
  an incorrect absent-node assertion in the detail navigation test and has been fixed but not rerun yet.
- Android actual-`MainActivity` emulator smoke and dependencies were added but have not been compiled yet.

## Important testing detail

On Desktop, Compose pointer injection runs navigation callbacks off the lifecycle main thread. The shared tests use
the local `click(tag)` helper to execute clicks on the UI thread. Do not replace it with raw `performClick()` for
navigation actions without re-verifying Desktop.

## Next work

1. Rerun `:composeApp:jvmTest`; fix any remaining compile/test failures.
2. Compile Android tests and validate the Android instrumented source set configuration.
3. Add Playwright Web smoke with Chromium PR and Firefox/WebKit/mobile nightly projects, emulator seeding,
   `localStorage` reset/reload, URI interception, trace/screenshots, and bounded diagnostic retry.
4. Add iOS simulator/shared suite plus an XCUITest smoke for the SwiftUI shell and emulator launch argument.
5. Add Desktop real-entry smoke using localhost REST plus persistence/refresh checks.
6. Rewrite CI into PR, main, nightly, and release-smoke matrices with emulator startup and artifact uploads.
7. Document local commands and fixture/test matrix in `README.md` or a permanent E2E guide.
8. Run `ktlintCheck`, `test`, `:composeApp:jvmTest`, relevant JS/Android builds, and any available platform checks.
9. Remove `docs/e2e-testing-plan.md` and this file, commit, push, and open a PR against `main`.

## Known local limitations

- iOS simulator and XCUITest must be verified on macOS CI; this host is Windows.
- Android instrumentation requires an emulator and Firebase Emulator process.
- No Firebase secret/config placeholder has been created; existing CI secrets must remain the source of platform
  configs.
