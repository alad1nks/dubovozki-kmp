# E2E-тестирование

E2E-набор проверяет общий Compose UI и реальные entry points Android, iOS, Web и Desktop. PR-проверки работают
только с in-process fake API, локальным REST server или Firebase Realtime Database Emulator и никогда не изменяют
production Firebase.

## Архитектура

Основной быстрый набор находится в `composeApp/commonTest`. Он поднимает настоящий `App`, Navigation, ViewModel,
domain и data layers. Через override-модули Koin заменяются только внешние границы: Firebase API, storage,
московские часы и URI handler. Каждый тест получает новый Koin application и новый state driver.

Платформенные проверки дополняют shared suite:

| Платформа | Runner | Что проверяется |
|---|---|---|
| JVM | Compose Multiplatform UI Test | Все P0/P1 состояния, навигация, фильтры, clock, cache, locale и theme |
| Android | Instrumentation против `MainActivity` | Firebase SDK + Emulator, Back, realtime, DataStore и relaunch |
| iOS | Shared simulator suite + XCUITest | SwiftUI shell, Compose controller, Back/gesture, realtime и relaunch |
| Web | Playwright | Browser entry, accessibility bridge, realtime, reload/localStorage и 599/600 px |
| Desktop | JVM application test | Actual platform module, первое/повторное REST-чтение и DataStore |

`TestTags` — единый каталог селекторов. Android/JVM используют Compose test tags; iOS и JS включают
accessibility bridge только при `--e2e` или `?e2e=true`. Playwright и XCUITest взаимодействуют с accessibility
actions, а не с координатами. Production accessibility tree не получает тестовые labels.

Московское время задаётся через `MoscowTimeProvider`. Fake provider публикует изменения как Flow, поэтому
переходы до/во время/после рейса и через границу минуты выполняются без `sleep`.

## Fixtures

Version-controlled Firebase fixtures находятся в `e2e/fixtures/firebase`, clock fixtures — в
`E2ETestFixtures.kt`. Набор содержит happy, empty, partial-invalid, error/no-cache, cached-offline,
cached-then-fresh и realtime состояния. `firebase.json` подключает только emulator rules из
`e2e/firebase.rules.json`; эти открытые rules не используются production-сборкой.

Android, iOS и Web направляются на Emulator только явной тестовой конфигурацией. Desktop принимает override URL
только для loopback HTTP endpoint. Любой внешний URI перехватывается и сравнивается с ожидаемым значением.

## Локальный запуск

Обязательные общие проверки из корня репозитория:

```shell
./gradlew ktlintCheck test :composeApp:jvmTest
```

На Windows используйте `gradlew.bat`. Desktop entry test входит в `:composeApp:jvmTest`; отдельно его можно
запустить так:

```shell
./gradlew :composeApp:jvmTest --tests '*DesktopEntryPointE2ETest*'
```

Web Chromium самостоятельно запускает Firebase Emulator и development Web server на `127.0.0.1:9080`:

```shell
cd e2e/web
npm ci
npx playwright install chromium
npm run test:chromium
```

Полная browser-матрица:

```shell
npx playwright install chromium firefox webkit
npm test
```

Для проверки production Web bundle задайте `E2E_WEB_RELEASE=true` перед `npm run test:chromium`.

Android требует установленный emulator, игнорируемый `androidApp/google-services.json` и Firebase Emulator,
запущенный с project id из этого файла:

```shell
npx --prefix e2e/web firebase emulators:start --project <project-id> --only database
./gradlew :androidApp:connectedDebugAndroidTest
```

Не создавайте заглушку Firebase config. В CI файл декодируется из repository secret. iOS проверяется на macOS:

```shell
./gradlew :composeApp:iosSimulatorArm64Test
xcodebuild test -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -destination 'platform=iOS Simulator,id=<simulator-udid>'
```

## Покрытие каталога

| IDs | Автоматизация |
|---|---|
| E2E-001–006 | Shared navigation tests, exact 599/600 Playwright viewports, Android Back и iOS Back/gesture |
| E2E-010–015 | Shared direction click/swipe, все station/day filters, Sunday→Monday и combined filters |
| E2E-016–019 | Observable clock: before/at/after, minute transition, go-to-next и end-of-day |
| E2E-020–026 | Empty/reset, retry, cache/offline/fresh, refresh с фильтрами, platform realtime и invalid DTO |
| E2E-030–036 | Независимый linen entry, точные/ошибочные URI, unsupported links, retry/cache/realtime |
| E2E-040–046 | Detail/back, три корпуса, current-day semantics, per-building empty, retry/cache/realtime |
| E2E-050–055 | Applied Light/Dark tags, browser System theme, ru/en/kk/System, defaults, relaunch/storage |
| E2E-056–057 | Локализованные заголовки/fallback и доступность длинных kk actions на phone/tablet |
| E2E-060–063 | Actual Android/iOS/Web entry points и nightly Firefox/WebKit/mobile projects |
| E2E-064–066 | Desktop REST refresh, platform persistence и keyboard/accessibility actions без координат |
| E2E-067 | Отдельный weekly read-only production schema workflow |

P2 visual baseline snapshots не блокируют PR: Playwright сохраняет screenshot/video/trace при сбое, а
функциональные assertions остаются источником результата.

## CI

| Trigger | Набор |
|---|---|
| Pull request | ktlint, unit, shared JVM, Chromium, Android API 24 P0, iOS compile |
| Push в `main` | PR-набор, Android API 24/35 и существующая release APK-сборка |
| Nightly | Android API 24/35, iOS shared/XCUITest, все Web projects, Desktop Windows/macOS/Linux |
| `release/*` | Shared P0 и Chromium против production Web bundle до release build/publish jobs |
| Weekly/manual | Read-only проверка трёх production Firebase paths и schema |

При падении jobs загружают test reports, screenshots, video, Playwright trace и emulator logs. В PR нет
безусловного retry. В CI Playwright допускает один retry, включает `failOnFlakyTests`, поэтому прошедший только со
второй попытки тест всё равно делает job красным.

## Ограничения локального окружения

- iOS Simulator и XCUITest запускаются только на macOS; Windows может проверить JVM, JS и Android compilation.
- Полный Android test требует emulator и настоящий локальный Firebase config, который не коммитится.
- Firebase Emulator и static Web server слушают только loopback interface.
- Production smoke выполняет только GET и не содержит credentials или операций записи.
