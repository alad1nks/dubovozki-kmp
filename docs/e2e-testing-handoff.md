# E2E implementation handoff

This is a temporary checkpoint for continuing after context compaction. Keep one active writer only.
Delete this file together with `docs/e2e-testing-plan.md` before the final pull request.

## Goal

Complete `docs/e2e-testing-plan.md`, verify the implementation, remove the plan and this handoff, push
`codex/e2e-testing`, and create a pull request into `main`. Do not finish before the PR URL exists.

## Repository state

- Branch: `codex/e2e-testing`.
- Baseline: `b759095 docs: add e2e testing plan (#51)`.
- First checkpoint: `399da15 test: checkpoint multiplatform e2e foundation`.
- Local Gradle needs `JAVA_HOME=C:\Users\al1ks\.jdks\corretto-18.0.2` and
  `ANDROID_HOME=C:\Users\al1ks\AppData\Local\Android\Sdk`.
- Gradle, Git writes, push, and PR creation need sandbox escalation here.

## Implemented

- Production composition supports isolated Koin module overrides; centralized stable test selectors cover the app.
- Injectable Moscow clock, fake Firebase/storage/URI boundaries, versioned fixtures, and 22 shared Compose E2E tests.
- Android actual-`MainActivity` instrumented smoke and Firebase emulator switching.
- Web Playwright project with Chromium/Firefox/WebKit/mobile profiles, exact 599/600 viewports, artifacts,
  Firebase Emulator seeding, storage reset/reload, URI interception, and accessibility-label selectors.
- JS runtime fixes for Firebase initialization and Moscow time formatting. Chromium P0 smoke passes locally.
- iOS Firebase emulator launch mode, XCUITest target/scheme, and shell navigation smoke.
- Desktop actual platform entry test with localhost REST and real DataStore; it passes locally.
- Firebase emulator config and emulator-only open rules under `e2e/`.

## Latest verification

The latest `:composeApp:jvmTest` ran 22 tests. The Desktop entry test passed; three shared tests fail:

1. `everyStationAndDayFilterUsesTheExpectedDomainData`: selection assertion races; wait for the filtered card.
2. `controlledClockCoversBeforeAtAfterAndEndOfDay`: the fake clock is not observable, so changing it does not
   update the ViewModel immediately. Add an observable clock flow/use case and consume it in the ViewModel.
3. `cachedServicesAndScheduleStayAvailableOfflineAndUpdateRealtime`: empty service schedule has no stable tag.
   Add a dedicated empty-state tag and assert that instead of `COMMON_ERROR`.

Android compilation originally found a missing test dependency; `projects.core.designsystem` has now been added but
must be rechecked. A full Android build remains blocked locally by the intentionally absent private
`androidApp/google-services.json`; do not create a placeholder. iOS must be validated on macOS CI.

## Remaining work

1. Fix the three JVM failures and rerun the suite.
2. Recheck Android test compilation without the private Google Services processing task.
3. Run the full local Playwright Chromium suite and fix any remaining navigation/persistence failures.
4. Add/finish PR, main, nightly, and release-smoke CI jobs with emulator seeding and artifact uploads.
5. Add permanent E2E documentation and README link; map all required P0/P1 cases honestly.
6. Run formatting, unit/JVM/JS/Android checks available on Windows.
7. Remove generated debug logs, `docs/e2e-testing-plan.md`, and this handoff.
8. Final commit, push, create the PR into `main`, then mark the goal complete.

## Guardrails

- Do not create or commit Firebase secret/config placeholders.
- Do not run another task against this worktree concurrently.
- Generated `firebase-debug.log` and `database-debug.log` must not be committed.
