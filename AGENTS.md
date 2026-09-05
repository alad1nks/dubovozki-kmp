# AGENTS.md

## Проект и модули

«Дубовозки» — Kotlin Multiplatform-приложение с расписанием автобусов и сервисов общежития Дубки.
Общие UI и логика поддерживают Android, iOS, Desktop/JVM и браузерный Kotlin/JS.

- `androidApp` — Android application и `MainActivity`; использует `composeApp`.
- `iosApp` — SwiftUI/Xcode shell: настройка Firebase и показ `ComposeUIViewController`.
- `composeApp` — `App`, composition root Koin, общий `AppNavHost` и entry points iOS/JVM/JS.
- `feature:{busschedule,services,servicesschedule,settings}` — feature UI, UiState/ViewModel, navigation и DI.
- `core:model` — общие модели; `core:firebase` — Firebase API/DTO и platform access.
- `core:data` — repositories и DTO mapping; `core:domain` — use cases и бизнес-правила.
- `core:navigation` — базовый `Destination`; `core:designsystem` — тема и Compose-компоненты.
- `core:storage:common` — storage API; `core:storage:datastore` — Android/iOS/JVM DataStore;
  `core:storage:js` — browser `localStorage`.
- `resources` — общие Compose Resources и facade `AppResource`.

Полный список модулей — в `settings.gradle.kts`; версии — в `gradle/libs.versions.toml`.
Актуальное поведение и контракты — в `docs/behavior.md`, запуск и границы тестов — в `docs/e2e-testing.md`.

## Стек и targets

- Kotlin Multiplatform, Compose Multiplatform/Material 3, AndroidX Lifecycle ViewModel и type-safe Navigation Compose.
- Koin DI, coroutines/StateFlow, kotlinx-datetime, kotlinx-serialization.
- Firebase Realtime Database: Android SDK, iOS CocoaPods, JS npm SDK, JVM Ktor/CIO REST.
- Targets: Android (min 24, compile/target 37, JVM target 11), `iosArm64`, `iosSimulatorArm64`, JVM Desktop
  и JS browser. `iosX64` и Wasm не настроены.
- CI устанавливает Temurin JDK 17 для запуска wrapper. Сам Gradle daemon использует JetBrains JDK 21
  по `gradle/gradle-daemon-jvm.properties`. JVM target 11 относится к Android bytecode.
  Версии сверяй с version catalog, Gradle wrapper и daemon criteria.

## Архитектура и KMP

- Сохраняй границы: feature UI зависит от domain/model/navigation/resources и иногда designsystem; domain —
  от data/model; data — от firebase/model/storage.
- Firebase DTO и platform access держи в `core:firebase`, mapping — в `core:data`, бизнес-правила —
  в `core:domain`.
- В feature navigation destination связывает Koin ViewModel с одноимённым UI `*Route`. ViewModel преобразует
  domain flows в feature `UiState`; UI получает state и callbacks.
- Регистрируй зависимости в Koin-модуле владельца. `CommonModules` собирает общие модули;
  `PlatformModules` добавляет DataStore или JS storage. Koin запускается в `App`.
- Route-типы находятся в `feature/*/navigation` и помечены `@Serializable`; общий граф — в `AppNavHost`.
- Переносимый код размещай в `commonMain`, platform API/entry points — в platform source sets. Существующие
  `expect`/`actual`: `PlatformModules`, `LocalAppLocale`, Firebase listener, путь DataStore, `e2eTestTag` и
  `rememberBusReminderLauncher`. При изменении
  контракта обновляй все actual-реализации.
- `core:storage:datastore` не имеет JS target, `core:storage:js` имеет только JS. Остальные KMP-модули
  объявляют Android, обе iOS ARM64 цели, JVM и JS.

## Compose UI и ресурсы

- Общий Compose UI находится в `commonMain`. Composable с ViewModel собирает state, нижележащий screen
  принимает данные/callbacks; локальный UI-state хранится через `remember`.
- Используй `AppTheme`, `MaterialTheme.colorScheme` и `core:designsystem`. `isTablet()` переключает bottom bar
  на navigation rail при ширине `>= 600.dp`.
- Локализованный shared UI использует `stringResource(AppResource.String.*)`. В `resources/...` русский
  default лежит в `values`, английский — в `values-en`, казахский — в `values-kk`.
- При добавлении общей строки добавь default resource, нужные переводы и обнови `AppResource.kt`. Не редактируй
  generated accessors. Сейчас language-name keys есть только в default XML; остальные локали используют fallback.
- Android `app_name` хранится отдельно в `androidApp/src/main/res/values*`.

## Команды

Запускай из корня (`gradlew.bat` на Windows):

```sh
./gradlew ktlintCheck
./gradlew test

./gradlew :androidApp:assembleDebug
./gradlew :androidApp:testDebugUnitTest

./gradlew :composeApp:run
./gradlew :composeApp:jvmJar
./gradlew :composeApp:jvmTest

./gradlew :composeApp:jsBrowserDevelopmentRun
./gradlew :composeApp:jsBrowserProductionWebpack
./gradlew :composeApp:jsBrowserTest

./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
./gradlew :composeApp:linkReleaseFrameworkIosArm64
./gradlew :composeApp:iosSimulatorArm64Test
```

Android запускается из Android Studio. iOS запускается из `iosApp` в Xcode; Xcode вызывает
`:composeApp:embedAndSignAppleFrameworkForXcode`. Unit-тесты находятся в `core/{data,domain}/src/commonTest` и
`feature/busschedule/src/commonTest`; shared E2E — в `composeApp/src/commonTest`, Desktop integration — в
`composeApp/src/jvmTest`. Есть Android instrumentation, iOS XCUITest, Playwright и Roborazzi-тесты всех четырёх feature.
`test` не заменяет `jvmTest`, browser E2E и native instrumentation. Требования и команды для каждого набора описаны
в `docs/e2e-testing.md`.

## Проверка изменений

- Всегда запускай `./gradlew ktlintCheck`. `.editorconfig` задаёт Kotlin official style, UTF-8/LF, final newline,
  max line length 120 и trailing commas.
- Запускай test и build task затронутой платформы. Для изменений `commonMain`, source sets, DI, navigation,
  resources или `expect`/`actual` проверяй затронутые targets; непроверенные явно укажи.
- Для UI проверь затронутые локали и темы; для adaptive UI — ширину ниже и выше 600 dp.

## Ограничения и изменения только по запросу

- Для Firebase нужны игнорируемые Git файлы: `androidApp/google-services.json`,
  `iosApp/iosApp/GoogleService-Info.plist`, `composeApp/src/jsMain/resources/firebaseConfig.js`. Не создавай
  заглушки и не коммить секреты.
- JVM Firebase делает одно REST-чтение при создании API и повторное после refresh; Android/iOS/JS используют
  listeners. Их realtime-семантика различается. Offline banner появляется после `Data.Error` при наличии cache;
  отключение сети само по себе не гарантирует error callback Firebase SDK.
- `composeApp/webpack.config.d/watch.js` — workaround для KT-80582; не удаляй его без отдельной проверки.
- Без отдельного запроса не меняй identifiers/namespaces, signing/release settings, Firebase URL/path/DTO schema,
  storage keys/file name, target matrix, версии Gradle/plugins/dependencies, Xcode/CocoaPods/SPM и CI workflows.
- Не выполняй release signing, публикацию или загрузку артефактов без прямого запроса.
