# E2E implementation handoff

Temporary recovery checkpoint. Keep one active writer only. Delete this file and `docs/e2e-testing-plan.md`
before the final PR.

## Goal and Git

- Complete the plan, push `codex/e2e-testing`, and create a PR into `main`; do not finish before the PR URL exists.
- Baseline: `b759095`.
- Checkpoints: `399da15` (shared foundation), `41345cf` (platform runners).
- Windows JDK: `C:\Users\al1ks\.jdks\corretto-18.0.2`; Android SDK:
  `C:\Users\al1ks\AppData\Local\Android\Sdk`.

## Implemented

- Testable Koin composition root, fake API/storage/URI boundaries, observable Moscow clock, centralized selectors.
- Shared Compose suite now has 23 tests covering P0/P1 navigation, bus filters/click/swipe/time/cache/errors,
  services, service schedule, themes, locales, loading and accessibility semantics.
- Android actual `MainActivity` test: emulator seed, realtime mutation, detail/Back, settings and DataStore relaunch.
- Web Playwright: Firebase Emulator, accessibility actions, realtime, localStorage reload, System theme, exact
  599/600 widths, long kk strings, Chromium/Firefox/WebKit/mobile projects and release-bundle server.
- iOS emulator mode, XCUITest target/scheme, realtime mutation, detail Back/gesture, settings relaunch.
- Desktop actual platform test verifies one REST read, refresh second read, and real DataStore writes.
- CI rewritten/extended for PR, main, nightly, release P0, and weekly read-only production schema smoke.
- Permanent guide added at `docs/e2e-testing.md` and linked from README.

## Latest verification

- `:composeApp:jvmTest` passed all 22 tests before the last coverage expansion.
- After adding swipe/current-theme/localization coverage it ran 23 tests with two assertion-only failures:
  current tags were in the unmerged tree and Russian title matched two nodes. Both assertions are now fixed but
  the suite has not yet been rerun.
- Android instrumented source compiled successfully before the latest realtime/persistence expansion.
- Playwright Chromium settings persistence passed before the latest theme/realtime/locale cases.
- `ktlintFormat` completed successfully after the large edit.
- iOS cannot run on this Windows host and must be validated by macOS CI.

## Remaining work

1. Rerun full JVM suite; fix only real failures.
2. Compile expanded Android test and run full Chromium suite.
3. Validate workflow YAML and production Web bundle/static server; fix CI syntax/path issues.
4. Run `ktlintCheck`, `test`, JVM, JS production webpack, and Android compile checks.
5. Review diff/security: no secrets, generated reports/logs, or node_modules.
6. Delete the plan and this handoff, final commit, push, create PR into `main`, then mark the goal complete.

## Guardrails

- Never create Firebase config placeholders or commit secrets.
- Generated Firebase/Playwright logs and reports are ignored and must stay uncommitted.
- Do not run another task against this worktree concurrently.
