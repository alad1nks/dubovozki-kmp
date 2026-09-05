# Тестирование

Наборы проверяют общий Compose UI, repositories/domain и отдельные платформенные интеграции.
Текущее поведение описано в [behavior.md](behavior.md). Наличие теста или CI job не означает, что он прошёл:
результат конкретного прогона нужно смотреть в отчёте.

## Наборы и границы

| Набор | Исходники | Что проверяет |
|---|---|---|
| Unit | `core/{data,domain}/src/commonTest`, `feature/busschedule/src/commonTest` | Cache/error/mapping, фильтры, тема, индекс будущего рейса и минуты напоминания |
| Shared application | `composeApp/src/commonTest/.../AppE2ETest.kt` | Настоящие App, Navigation, ViewModel, domain/data с fake API, storage, часами и URI handler |
| Desktop integration | `composeApp/src/jvmTest/.../DesktopEntryPointE2ETest.kt` | App с actual platform DI, локальный REST, повторный GET и чтение настроек через DataStore |
| Android instrumentation | `androidApp/src/androidTest/.../AndroidEntryPointE2ETest.kt` | MainActivity, SDK/Emulator realtime, системный Back, повторное создание Activity и настройки |
| iOS XCUITest | `iosApp/iosAppUITests/AppEntryPointUITests.swift` | SwiftUI shell, Compose controller, SDK/Emulator realtime, Back/swipe и terminate/launch |
| Web | `e2e/web/tests/app.spec.ts` | Browser entry, realtime, reload, localStorage, 599/600 px, тема и метаданные языка |
| Visual | `feature/*/src/jvmTest/.../*ScreenshotTest.kt` | Roborazzi-снимки четырёх экранов и диалога напоминания |

Shared suite использует отдельные Koin application и state driver для каждого теста. Fake API публикует
`Initial`, `Success`, `Error`; `MoscowTimeProvider` задаёт фиксированную дату и продвигает часы через Flow.
Это относится к shared suite: native/Web/Desktop smoke не подменяют системные часы. Realtime smoke создаёт рейс
для всех используемых групп дней и проверяет наличие строки, не точный текст относительного времени.

Desktop integration рендерит App in-process: он не запускает `main()` и реальное окно. Android закрывает и снова
создаёт Activity в том же процессе; это не kill/relaunch процесса. Desktop читает настройки через тот же DataStore,
не проверяя запуск нового процесса. Только iOS XCUITest явно выполняет terminate/launch; Web проверяет reload.

`TestTags` — общий каталог селекторов. Android/JVM используют test tags; iOS с `--e2e` и JS с `?e2e=true`
дополнительно помещают их в accessibility descriptions. В обычном запуске descriptions не заменяются тестовыми
метками. Playwright кликает DOM accessibility-элементы, XCUITest — accessibility buttons; shared swipe привязан
к найденному элементу. Это не полная проверка клавиатурного управления или screen reader.

## Fixtures и изоляция

- JSON fixtures: `e2e/fixtures/firebase/{happy,empty,partial-invalid}.json`.
- Shared fixtures, clock и fake state driver: `composeApp/src/commonTest/.../E2ETestFixtures.kt`.
  Они задаются в Kotlin и не загружаются из JSON автоматически.
- `e2e/fixtures/states/*.json` описывают последовательности сценариев; runners не читают эти файлы.
  Эквивалентные состояния задаются вызовами fake API в shared тестах.
- `firebase.json` подключает только открытые Emulator rules из `e2e/firebase.rules.json`.
  Эти rules не являются production-конфигурацией.
- Web использует `demo-dubovozki` и получает demo config через `page.addInitScript`;
  файл `firebaseConfig.js` с заглушкой создавать не нужно. Каждый тест заново записывает happy fixture и очищает
  localStorage первого запуска. Reload в том же тесте сохраняет настройки.
- Все browser projects используют один Emulator namespace, поэтому `workers: 1` предотвращает взаимное
  перезаписывание fixture при `npm test`. Nightly запускает projects на отдельных runners.
- Android требует настоящий SDK config и подключает Emulator через `dubovozki.e2e.firebase.host/port`.
  iOS включает loopback Emulator аргументом `--e2e` после Firebase initialization.
- Desktop принимает URL override только с префиксом `http://127.0.0.1:` или `http://localhost:`;
  DataStore override допускается внутри системной временной директории.
- Shared URI tests перехватывают и сравнивают ссылки. Web устанавливает перехват `window.open`, но отдельного
  browser-теста contact/donation пока нет. Native smoke внешние ссылки не нажимает.

## Локальный запуск

Предварительная настройка JDK, SDK и Firebase описана в [README](../README.md#prerequisites).
Все Gradle-команды ниже выполняются из корня. На Windows используйте `.\gradlew.bat` вместо `./gradlew`.
JVM-only проверки не требуют Android Firebase config:

```shell
./gradlew ktlintCheck
./gradlew :core:data:jvmTest :core:domain:jvmTest :feature:busschedule:jvmTest :composeApp:jvmTest
./gradlew :composeApp:jvmJar
```

`test` запускает подходящие host tasks, но не заменяет отдельные JVM, instrumentation, iOS и Web команды.
Для полного host-прогона нужен Android SDK. Если есть настоящий `androidApp/google-services.json`:

```shell
./gradlew test :androidApp:assembleDebug
```

CI запускает host tests без этого файла, исключая только задачу его обработки:

```shell
./gradlew test :composeApp:jvmTest -x :androidApp:processDebugGoogleServices
```

Это исключение допустимо для host tests; оно не проверяет Firebase initialization настоящего Android приложения
и не заменяет конфигурацию для instrumentation или обычного запуска.

### Визуальные тесты

```shell
./gradlew :feature:busschedule:verifyRoborazziJvm :feature:services:verifyRoborazziJvm \
  :feature:servicesschedule:verifyRoborazziJvm :feature:settings:verifyRoborazziJvm
```

В PowerShell запишите команду в одну строку (обратный слеш выше — перенос shell macOS/Linux).
Эталоны находятся в `feature/*/src/jvmTest/snapshots`, viewport — `390×844`, базовая locale — English.
Даже файл `settings_light_russian.png` показывает выбранную настройку Russian при английском окружении рендера;
это не скриншот полной русской локализации. Есть Light/Dark состояния, но нет полной матрицы тем, языков и размеров.

После намеренного изменения UI обновите нужные эталоны задачей `recordRoborazziJvm`, просмотрите PNG и снова
запустите `verifyRoborazziJvm`. Проверка сохраняет HTML-report, JSON result и actual/diff PNG в build-директории.

### Web

```shell
cd e2e/web
npm ci
npx playwright install chromium
npm run test:chromium
```

Playwright запускает Firebase Emulator (database `127.0.0.1:9000`, hub `127.0.0.1:4400`) и Web server
(`127.0.0.1:9080`). Для Emulator нужен `java` в PATH, для wrapper — JDK; установка Node сама по себе недостаточна.
Локально существующие серверы могут переиспользоваться: перед проверкой убедитесь, что они обслуживают текущую
ветку. В CI переиспользование отключено.

Полная browser-матрица — Chromium, Firefox, WebKit и mobile Chromium (Pixel 7, viewport `599×900`):

```shell
npx playwright install chromium firefox webkit
npm test
```

Production bundle, macOS/Linux: `E2E_WEB_RELEASE=true npm run test:chromium`.
PowerShell: `$env:E2E_WEB_RELEASE = 'true'`, затем `npm run test:chromium`;
после прогона удалите переменную: `Remove-Item Env:E2E_WEB_RELEASE`.

`E2E_EXTERNAL_SERVERS=true` отключает управление обоими серверами; `E2E_BASE_URL` меняет адрес страницы,
но не жёстко заданный endpoint Emulator в тестах. Не используйте production URL для E2E.
`jsBrowserTest` — отдельный Kotlin/JS test runner, не Playwright; успешная сборка JS не подтверждает его выполнение.

### Android

Нужны SDK, запущенный Android emulator и настоящий `androidApp/google-services.json`.
Установите `e2e/web` dependencies командой `npm ci`, затем запустите Firebase Emulator в отдельном терминале:

```shell
cd e2e/web
npx firebase emulators:start --config ../../firebase.json --project <project-id> --only database
```

`<project-id>` берётся из `project_info.project_id` настоящего config. Из корня в другом терминале:

```shell
./gradlew :androidApp:connectedDebugAndroidTest
```

Тест сам записывает happy fixture в namespace из `database_url`, указывает SDK адрес `10.0.2.2:9000` и запускает
MainActivity. Cleartext HTTP разрешён debug manifest. Это команда для эмулятора Android, не физического телефона.

### iOS (macOS)

Shared simulator suite с fake API:

```shell
./gradlew :composeApp:iosSimulatorArm64Test
```

Для XCUITest нужны настоящий `GoogleService-Info.plist`, Firebase Emulator и happy fixture. Из корня, после
`npm ci` в `e2e/web`, экспортируйте ID и запустите Emulator в отдельном терминале:

```shell
export E2E_FIREBASE_PROJECT_ID=$(/usr/libexec/PlistBuddy -c 'Print :PROJECT_ID' iosApp/iosApp/GoogleService-Info.plist)
e2e/web/node_modules/.bin/firebase emulators:start --project "$E2E_FIREBASE_PROJECT_ID" --only database
```

В терминале теста также экспортируйте `E2E_FIREBASE_PROJECT_ID` той же командой, затем:

```shell
curl --fail --request PUT --header 'Content-Type: application/json' \
  --data-binary @e2e/fixtures/firebase/happy.json \
  "http://127.0.0.1:9000/.json?ns=$E2E_FIREBASE_PROJECT_ID"
xcodebuild test -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -destination 'platform=iOS Simulator,id=<simulator-udid>'
```

Это подготовка, используемая nightly workflow. XCUITest требует `E2E_FIREBASE_PROJECT_ID` для realtime PUT.
Запуск только `xcodebuild test` без Emulator/seed/переменной не является полным локальным сценарием.

## Соответствие историческому каталогу

| IDs | Реальная автоматизация / ограничение |
|---|---|
| 001–006 | Shared navigation, 599/600 Web, Android Back, iOS Back/swipe; повторная навигация считает узлы, но не проверяет весь saved state |
| 010–015 | Shared launch, direction click/swipe, station/day и combined filters, Sunday→Monday |
| 016–017 | Shared управляемые before/at/after relative-time assertions; unit проверяет индекс ближайшего рейса |
| 018–019 | Кнопка «К ближайшему» и end-of-day карточка удалены в PR #58; unit проверяет отсутствие будущего индекса |
| 020–026 | Shared empty/reset, retry, cached-offline→fresh, сохранение фильтров, invalid DTO; SDK realtime — native/Web smoke |
| 030–036 | Shared независимый linen entry при ошибке ссылок, URI success/failure, скрытие ссылок, retry/cache/realtime |
| 040–046 | Shared detail/back, выбор трёх корпусов, today-tag, пустой корпус, retry/cache/realtime и invalid linen rows |
| 050–055 | Shared theme/locale/defaults, Web System color scheme и reload, native settings; см. ограничения relaunch выше |
| 056–057 | Shared заголовок Настроек на ru/en/kk, Web kk navigation на 599/600, Web title/lang при System ru/en/kk |
| 058 | Roborazzi отдельных состояний; полная Light/Dark × phone/tablet × ru/en/kk матрица отсутствует |
| 060–064 | Android/iOS/Web entry smoke и Desktop platform-module integration; Desktop окно не запускается |
| 065 | Полного kill/relaunch с восстановлением cache, темы и языка на каждой платформе нет |
| 066 | Есть semantics selectors/actions; отдельного полного keyboard/screen-reader сценария нет |
| 067 | Read-only GET трёх production paths с проверкой формы верхнего уровня, без валидации всех полей DTO |

Другие границы покрытия: нет прямых assertions прокрутки к текущему дню/рейсу, полного обхода всех экранов на
трёх языках и фактической доставки напоминаний. Shared fake ошибки не доказывают native SDK callbacks при
permission denial или потере сети. Тесты с числом узлов/selected tag не следует описывать как проверку всей
навигации, всех данных вкладки или полного состояния после перезапуска.

Известное ограничение текущего Kotlin/JS accessibility bridge Compose: после закрытия popup его DOM-дерево может
остаться на пунктах закрытого меню вместо основного экрана. Визуальный экран продолжает отображаться, но следующий
semantics locator недоступен. Последовательное управление несколькими меню без reload поэтому пока не подтверждено.
Web locale-тест начинает с сохранённого русского языка, выбирает System через UI и проверяет title/lang до и после
reload; он не заявляет проверку повторного открытия меню. Общая смена ru/en/kk/System проверяется в shared suite.

## CI и диагностика

| Trigger | Настроенные jobs |
|---|---|
| PR в main/master | ktlint, host tests, shared JVM, Roborazzi verify, Chromium, Android API 24, iOS framework compile |
| Push в main | Host/shared/visual/Chromium, Android API 24/35, затем существующие release APK build/deploy jobs |
| Nightly/manual | Web projects на отдельных runners, Android API 24/35, iOS shared/XCUITest, Desktop Windows/macOS/Linux |
| release/* | JVM suite и Chromium production bundle, затем существующие AAB build/deploy jobs |
| Weekly/manual production smoke | GET трёх публичных Firebase paths и поверхностная проверка JSON |

Nightly не запускает Roborazzi verify. Release job с названием P0 запускает весь `composeApp:jvmTest` и Chromium
файл: отдельного фильтра только по P0 нет. PR iOS compile — проверка сборки, а не выполнение iOS сценариев.
Workflow definitions находятся в `.github/workflows`; branch protection нужно проверять отдельно.

PR/main/nightly/release E2E используют fake, локальный REST или Emulator. Production smoke — отдельный job,
выполняющий только GET. Он не используется для наполнения тестов и не меняет production Firebase.

Playwright сохраняет screenshot/trace при сбое, видео локально — при сбое, в CI — для каждого теста.
В CI допускается один retry с `failOnFlakyTests`: успех только со второй попытки всё равно проваливает job.
Android recorder ждёт старта instrumentation, делит запись на части до 180 секунд и нормализует H.264/30 FPS.
Nightly iOS recorder снимает XCUITest; in-process JVM/shared iOS scenes отдельно не записываются.

Доступные видео и отчёты загружаются с `if: always()` в `Actions → workflow run → Artifacts`.
Web пишет в игнорируемые `test-results`/`playwright-report`; Android/iOS видео — во временную директорию runner.
Артефакты не коммитятся. iOS проверки выполняются только на macOS; на Windows их нужно явно помечать непроверенными.
