# План покрытия проекта E2E-тестами

## Назначение документа

Этот документ — спецификация для ИИ-агента или разработчика, который будет внедрять E2E-тестирование в проект
«Дубовозки». Он описывает текущий контекст, целевую архитектуру тестов, этапы работ, матрицу запуска и полный
каталог тест-кейсов.

Работу следует выполнять итеративно. Каждый этап должен завершаться рабочим, проверяемым состоянием репозитория.
Не следует пытаться добавить все платформы и сценарии одним большим изменением.

## Цель

Построить детерминированное E2E-покрытие общего Compose Multiplatform UI и проверить реальные entry points на
Android, iOS, Desktop/JVM и Web без зависимости PR-проверок от production Firebase, текущего времени и внешних
приложений.

Покрытие считается внедрённым, когда:

- автоматизированы все тест-кейсы с приоритетами P0 и P1;
- P0-сценарии проходят на реальных entry points всех четырёх платформ;
- PR-проверки используют управляемые данные и не обращаются к production Firebase;
- ошибки тестов сопровождаются достаточными диагностическими артефактами;
- тесты воспроизводимы локально документированными командами;
- нестабильные тесты не маскируются бесконечными retry.

## Контекст проекта

«Дубовозки» — Kotlin Multiplatform-приложение с общим Compose UI и targets Android, iOS ARM64, Desktop/JVM и
Kotlin/JS browser.

Основные пользовательские области:

- расписание автобусов с фильтрами по станции и дню, двумя направлениями и расчётом ближайшего рейса;
- список сервисов с внешними ссылками;
- расписание кастелянной для трёх корпусов;
- выбор темы и языка;
- адаптивная навигация: bottom bar при ширине меньше `600.dp`, navigation rail при ширине от `600.dp`.

Значимые технические особенности:

- общий UI и навигация находятся в `composeApp/commonMain` и feature-модулях;
- приложение создаёт Koin composition root непосредственно внутри `App`;
- Android, iOS и Web используют Firebase listeners;
- Desktop/JVM выполняет одиночное REST-чтение Firebase и повторяет его только после refresh;
- Android, iOS и Desktop сохраняют настройки и cache через DataStore;
- Web использует browser `localStorage`;
- локализации: русский default, английский и казахский;
- названия языков сейчас объявлены только в default resources, поэтому `en` и `kk` используют fallback;
- на момент составления плана в проекте есть 12 common unit-тестов, но нет UI/E2E-тестов;
- в UI почти нет стабильных `testTag`;
- текущий PR workflow выполняет сборку и lint, но не запускает тесты.

Перед изменениями агент должен повторно проверить актуальное состояние файлов и не считать этот снимок вечным.

## Обязательные ограничения

При реализации необходимо соблюдать `AGENTS.md` в корне проекта и следующие правила:

- не менять production Firebase URL, paths и DTO schema;
- не создавать и не коммитить Firebase secrets или конфигурационные заглушки;
- не менять identifiers, namespaces, signing, release settings, target matrix, версии зависимостей и CI release flow
  без отдельного обоснованного запроса;
- emulator/fake backend должен подключаться только через test-only конфигурацию;
- тесты не должны изменять production Firebase;
- внешние ссылки нельзя реально открывать в автоматических тестах: URI нужно перехватывать;
- время Москвы должно быть управляемым, а не зависеть от времени запуска CI;
- существующие unit-тесты не заменять E2E-тестами;
- общую UI-логику не дублировать между платформами без необходимости;
- после Kotlin-изменений всегда запускать `gradlew.bat ktlintCheck` и релевантные test/build tasks.

## Целевая стратегия

### 1. Shared application E2E

Основную массу сценариев следует написать один раз с Compose Multiplatform UI Test API. Тест должен поднимать
полный UI приложения, навигацию, ViewModel, domain и data layers. Подменяются только внешние границы:

- Firebase/API;
- persistent storage;
- московские часы;
- системный URI handler;
- при необходимости platform environment.

JVM — основной быстрый раннер для PR. Критическое подмножество той же suite запускается на Android emulator и
iOS Simulator.

Официальная документация:
[Testing Compose Multiplatform UI](https://kotlinlang.org/docs/multiplatform/compose-test.html).

### 2. Проверка реальных платформенных entry points

| Платформа | Рекомендуемый уровень | Назначение |
|---|---|---|
| Android | Instrumented Compose tests против actual `MainActivity` | Запуск приложения, Firebase SDK, DataStore, системный Back |
| iOS | Shared Compose tests и короткий XCUITest smoke | SwiftUI shell, Firebase initialization, Compose controller, Back |
| Web | Playwright | Реальный browser entry point, reload, `localStorage`, viewports и внешние URI |
| Desktop | JVM Compose/JUnit application tests | Реальное окно, DataStore и одиночное REST-чтение |

Для Web начать с Chromium в PR. Firefox и WebKit запускать nightly. Перед массовым написанием Web-тестов провести
короткий spike и подтвердить, что Compose semantics/test tags доступны стабильным Playwright locators. Если нет,
добавить accessibility/test bridge; не использовать координаты как основной способ взаимодействия.

Официальная документация: [Playwright browsers](https://playwright.dev/docs/browsers).

### 3. Управляемый backend

Для интеграционных проверок Android, iOS и Web использовать Firebase Realtime Database Emulator с demo project.
Baseline следует импортировать перед suite, а состояние базы очищать или пересоздавать между изолированными
сценариями.

Для Desktop необходимо сделать REST base URL настраиваемым через test-only environment, не изменяя production
значение по умолчанию.

Ошибки сети, stale cache и точные последовательности состояний удобнее проверять через in-process fake API, потому
что emulator не должен становиться единственным механизмом моделирования отказов.

Официальная документация:
[Firebase Realtime Database Emulator](https://firebase.google.com/docs/emulator-suite/connect_rtdb).

## План реализации

### Этап 0. Зафиксировать baseline

1. Проверить текущую ветку, чистоту worktree и актуальность `main`.
2. Запустить существующие `ktlintCheck` и `test`.
3. Зафиксировать список доступных test tasks для JVM, Android, iOS и JS.
4. Проверить текущие CI workflows и наличие Firebase secrets только по именам, не выводя значения.
5. Зафиксировать ожидаемое production-поведение Desktop: одно REST-чтение и повторное чтение после refresh.

Результат этапа: baseline-команды проходят либо существующие проблемы явно документированы до начала изменений.

### Этап 1. Сделать composition root тестируемым

1. Позволить `App` принимать конфигурацию или список override-модулей, сохранив текущий production default.
2. Добавить test-only реализации API, storage, clock и URI handler.
3. Обеспечить полный сброс состояния между тестами.
4. Не использовать глобальные singleton-состояния, которые переживают отдельный тест.
5. Сделать московское время фиксируемым и продвигаемым виртуально.
6. Предусмотреть test-only endpoint для Firebase Emulator и Desktop REST.

Предпочтительная граница: feature ViewModel и domain/data остаются настоящими; fake должен находиться как можно
ближе к внешнему API.

Результат этапа: приложение можно запустить в памяти с заранее заданными данными, временем и storage.

### Этап 2. Добавить семантику и стабильные селекторы

Создать централизованный набор test IDs в общем коде. Идентификаторы не должны зависеть от локализованного текста.

Минимальный набор:

```text
app.content
nav.services
nav.schedule
nav.settings
bus.filter.station
bus.filter.day
bus.refresh
bus.tab.moscow
bus.tab.dubki
bus.next.card
bus.next.go
bus.empty
bus.reset_filters
services.refresh
services.linen
services.contact
services.donate
services.links_unavailable
service_schedule.back
service_schedule.refresh
service_schedule.building.1
service_schedule.building.2
service_schedule.building.3
settings.theme
settings.language
common.loading
common.error
common.retry
common.offline
```

Для элементов списков использовать детерминированные IDs на основе domain id, но не позиции. Одновременно добавить
правильные accessibility roles, selected-state и content descriptions. Test tags не должны подменять доступность.

Результат этапа: ключевые элементы находятся по стабильным семантическим locators на поддерживаемых платформах.

### Этап 3. Создать fixtures и state driver

Подготовить version-controlled test data, совместимые с реальной Firebase schema:

| Fixture | Содержимое |
|---|---|
| `happy` | Обе стороны, все станции, будни, суббота и воскресенье, ссылки и три расписания корпусов |
| `empty` | Пустые расписания и отсутствующие ссылки |
| `partial-invalid` | Корректные записи вместе с DTO без id, времени, станции, направления или дня |
| `error-no-cache` | Ошибка первого запроса при пустом storage |
| `cached-offline` | Валидный cache, затем ошибка API |
| `cached-then-fresh` | Старый cache, затем отличающийся успешный ответ |
| `realtime-update` | Начальное значение и последующая управляемая мутация |

Добавить clock fixtures:

- за несколько минут до рейса;
- ровно во время рейса;
- через минуту после рейса;
- после последнего рейса дня;
- воскресенье перед полуночью;
- понедельник после полуночи.

State driver должен уметь перевести fake API через `Initial`, `Success`, `Error` и повторный `Success` без sleep.

Результат этапа: каждый сценарий запускается с известным seed и не зависит от порядка других тестов.

### Этап 4. Реализовать shared E2E-suite

1. Добавить Compose UI testing dependency в version catalog и нужные source sets.
2. Создать application test harness в `composeApp/commonTest`.
3. Сначала автоматизировать P0 happy path и навигацию.
4. Затем покрыть loading/error/cache/realtime states.
5. После этого добавить settings, localization, adaptive и accessibility проверки.
6. Параметризовать однотипные фильтры, языки и fixture states, не превращая отчёт в один нечитаемый тест.

Результат этапа: все P0/P1 shared-сценарии проходят на JVM.

### Этап 5. Подключить платформенные suite

#### Android

- настроить instrumented test source set и `AndroidJUnitRunner`;
- запускать actual `MainActivity`;
- направлять Firebase SDK на emulator через test-only configuration;
- проверить DataStore после kill/relaunch;
- выполнить P0 на минимальном поддерживаемом API и актуальном API nightly.

#### iOS

- запускать shared UI suite на `iosSimulatorArm64`;
- добавить короткий XCUITest против SwiftUI shell;
- проверить Firebase initialization и возврат из Compose detail screen;
- persistence проверять на iOS Simulator.

#### Web

- добавить Playwright configuration и webServer command;
- Chromium smoke выполнять в PR;
- Firefox, WebKit и mobile viewports выполнять nightly;
- очищать `localStorage` до теста и проверять reload отдельно;
- перехватывать navigation/external URI вместо реального открытия.

#### Desktop

- запускать application test с реальным platform module;
- направлять REST на test endpoint;
- проверить первое чтение, ручной refresh и DataStore persistence;
- полную OS-матрицу Windows/macOS/Linux выполнять nightly, если позволяет CI.

Результат этапа: каждый реальный entry point проходит P0 smoke.

### Этап 6. Встроить тесты в CI

| Trigger | Обязательные проверки |
|---|---|
| PR | Unit tests, shared JVM E2E, Chromium smoke, Android P0 smoke, `ktlintCheck` |
| Push в `main` | PR-набор и расширенный Android набор |
| Nightly | Полный Android matrix, iOS Simulator, Chromium/Firefox/WebKit, Desktop OS matrix, visual tests |
| Release branch | P0 против release-сборки, без публикации в рамках тестового job |
| Отдельный scheduled smoke | Только read-only проверка production schema и доступности данных |

При падении сохранять:

- screenshot;
- Playwright trace или platform test report;
- логи приложения и backend emulator;
- имя fixture и фиксированное время;
- версии OS/device/browser.

Не применять безусловный retry ко всей suite. Допустим один ограниченный retry только для сбора диагностики, при
этом flaky test должен оставаться видимым в отчёте и получать отдельную задачу на исправление.

## Каталог тест-кейсов

Приоритеты:

- **P0** — критический пользовательский маршрут, блокирует release;
- **P1** — обязательная регрессия, должна быть автоматизирована;
- **P2** — расширенное, визуальное или production-smoke покрытие.

### Запуск и навигация

| ID | Приоритет | Сценарий | Ожидаемый результат |
|---|---|---|---|
| E2E-001 | P0 | Чистый запуск приложения | Открыто расписание, выбран соответствующий navigation item, загрузка не вызывает crash |
| E2E-002 | P0 | Расписание → Сервисы → Настройки | Каждый экран открывается, выбранный navigation item корректен |
| E2E-003 | P0 | Открыть расписание кастелянной и нажать Back | Top-level навигация скрыта на detail screen, возврат ведёт в Сервисы |
| E2E-004 | P1 | Повторно выбирать top-level destinations | Экраны не дублируются в back stack, состояние destination восстанавливается |
| E2E-005 | P1 | Запустить при ширине 599 dp и 600 dp | При 599 dp показан bottom bar, при 600 dp — navigation rail |
| E2E-006 | P1 | Использовать системный Back/gesture | Android/iOS возвращаются по стеку и не закрываются преждевременно |

### Расписание автобусов

| ID | Приоритет | Сценарий | Ожидаемый результат |
|---|---|---|---|
| E2E-010 | P0 | Открыть `happy` fixture | Выбраны Сегодня и Все станции, показаны рейсы в Москву и карточка ближайшего рейса |
| E2E-011 | P0 | Нажать и swipe между В Москву/В Дубки | Список содержит рейсы только выбранного направления |
| E2E-012 | P0 | Поочерёдно выбрать три станции | Каждый список содержит только выбранную станцию |
| E2E-013 | P0 | Выбрать Сегодня, Завтра, Будни, Субботу, Воскресенье | Для каждого фильтра показано соответствующее расписание |
| E2E-014 | P1 | Выбрать Завтра в воскресенье | Используется расписание понедельника |
| E2E-015 | P0 | Совместить station и day filters | Оба фильтра применены одновременно в обоих направлениях |
| E2E-016 | P1 | Зафиксировать время до, во время и после рейса | Корректно показаны тексты «через», «сейчас» и «назад» |
| E2E-017 | P1 | Продвинуть clock через границу минуты | Ближайший рейс и relative time обновлены без перезапуска |
| E2E-018 | P0 | Нажать К ближайшему | Список прокручен к первому ещё не ушедшему рейсу |
| E2E-019 | P1 | Установить время после последнего рейса | Показано сообщение, что сегодня рейсов больше нет |
| E2E-020 | P0 | Получить пустой результат и сбросить фильтры | Показан empty-state; reset возвращает Сегодня и Все станции |
| E2E-021 | P0 | Ошибка без cache, затем Retry после восстановления API | Сначала error-state, затем актуальное расписание |
| E2E-022 | P0 | Cache и ошибка сети | Показаны сохранённые данные, offline banner и время cache |
| E2E-023 | P1 | Cache, затем свежий успешный ответ | Cache доступен сразу, затем заменён свежими данными без пустого экрана |
| E2E-024 | P1 | Нажать Refresh при выбранных фильтрах | Данные и updated-at обновлены, фильтры сохранены |
| E2E-025 | P1 | Изменить Firebase после открытия экрана | Android/iOS/Web обновили список без ручного refresh |
| E2E-026 | P1 | Загрузить `partial-invalid` | Некорректные записи пропущены, валидные показаны, crash отсутствует |

### Сервисы

| ID | Приоритет | Сценарий | Ожидаемый результат |
|---|---|---|---|
| E2E-030 | P0 | Открыть Сервисы | Пункт расписания кастелянной доступен независимо от внешних ссылок |
| E2E-031 | P1 | Нажать корректные contact/donation links | URI передан test handler с точным значением |
| E2E-032 | P1 | URI handler выбрасывает ошибку | Показан snackbar об ошибке открытия ссылки |
| E2E-033 | P1 | Links равны null или имеют неподдерживаемую схему | Ссылки скрыты, показано сообщение о недоступности контактов |
| E2E-034 | P0 | Ошибка загрузки и Retry | Error-state заменён корректным содержимым |
| E2E-035 | P1 | Cache и offline | Сохранённые ссылки доступны вместе с offline banner |
| E2E-036 | P1 | Refresh или realtime update меняет links | Список ссылок обновлён без перезапуска приложения |

### Расписание кастелянной

| ID | Приоритет | Сценарий | Ожидаемый результат |
|---|---|---|---|
| E2E-040 | P0 | Открыть detail из Сервисов | Корректный заголовок и расписание первого корпуса |
| E2E-041 | P0 | Переключить корпуса 1/2/3 | Для каждого корпуса показан только его набор записей |
| E2E-042 | P1 | Зафиксировать текущий московский день | Текущий день выделен, список прокручен к нему |
| E2E-043 | P1 | Один корпус имеет пустое расписание | Только этот корпус показывает empty-state, остальные продолжают работать |
| E2E-044 | P0 | Ошибка без cache и Retry | После Retry отображается расписание |
| E2E-045 | P1 | Cache и offline | Доступно сохранённое расписание с offline banner и временем cache |
| E2E-046 | P1 | Refresh/realtime update при выбранном корпусе | Данные обновлены, выбранный корпус сохранён |

### Настройки, локализация и оформление

| ID | Приоритет | Сценарий | Ожидаемый результат |
|---|---|---|---|
| E2E-050 | P0 | Переключить Light и Dark | Цветовая схема сразу меняется на всех экранах |
| E2E-051 | P1 | Выбрать System и изменить системную тему | Приложение следует системной теме |
| E2E-052 | P0 | Выбрать ru, en, kk и System | Текущий экран и навигация локализуются без перезапуска процесса |
| E2E-053 | P0 | Изменить тему/язык и полностью перезапустить приложение | Оба значения восстановлены из platform storage |
| E2E-054 | P1 | Запустить после очистки storage | Используются System theme и System language |
| E2E-055 | P1 | Сменить язык на экране Сервисов или Настроек | Compose content пересоздан, текущий top-level destination сохранён |
| E2E-056 | P1 | Пройти основные экраны на ru/en/kk | Все ключевые строки локализованы, текущий fallback названий языков зафиксирован |
| E2E-057 | P1 | Проверить длинные строки на phone/tablet | Важные действия не обрезаны и остаются доступными |
| E2E-058 | P2 | Light/Dark × phone/tablet × ru/en/kk | Visual snapshots не содержат layout-регрессий |

### Платформенные и эксплуатационные проверки

| ID | Приоритет | Сценарий | Ожидаемый результат |
|---|---|---|---|
| E2E-060 | P0 | Android actual app с Firebase Emulator | Пройден launch → schedule → services → settings |
| E2E-061 | P0 | Запуск iOS SwiftUI shell | Firebase инициализирован, Compose UI показан без crash |
| E2E-062 | P0 | Web в Chromium и reload | Основной маршрут работает, настройки восстановлены из `localStorage` |
| E2E-063 | P1 | Web в Firefox/WebKit и mobile viewport | Smoke проходит, mobile layout использует bottom navigation |
| E2E-064 | P1 | Desktop startup и Refresh | Первое REST-чтение успешно, Refresh выполняет повторный запрос |
| E2E-065 | P1 | Kill/relaunch с platform storage | Cache, тема и язык восстановлены на каждой платформе |
| E2E-066 | P1 | Управление keyboard/accessibility actions | Navigation, tabs, dropdown, Refresh и Back имеют роли и доступны без координат |
| E2E-067 | P2 | Read-only production smoke | Три Firebase paths доступны и соответствуют ожидаемой schema |

## Порядок автоматизации

Рекомендуемый порядок, уменьшающий риск большой незавершённой ветки:

1. `E2E-001`, `E2E-002`, `E2E-010`, `E2E-011` на JVM.
2. Все bus schedule P0.
3. Services и service schedule P0.
4. Settings P0 и persistence с fake storage.
5. Loading/error/cache/realtime P1.
6. Android P0 smoke.
7. Web Chromium P0 smoke.
8. iOS и Desktop P0 smoke.
9. Остальные P1, nightly matrix и P2.

Каждый пункт желательно оформлять отдельным небольшим pull request или логически завершённой серией commits.

## Проверка каждой итерации

Минимум после изменения общего Kotlin-кода:

```shell
./gradlew ktlintCheck
./gradlew test
./gradlew :composeApp:jvmTest
```

Дополнительно запускать задачи затронутых платформ. Имена новых E2E-задач должны быть добавлены в README или этот
документ после их появления. Если платформа не проверена локально, это нужно явно указать в описании pull request.

## Что не следует делать

- Не строить E2E только на production Firebase.
- Не проверять время ожиданием реальных минут.
- Не искать элементы только по русскому тексту.
- Не использовать screen coordinates как основной selector.
- Не смешивать все состояния backend в один длинный зависимый тест.
- Не сохранять состояние одного теста для следующего.
- Не открывать Telegram, браузер или почтовый клиент во время CI.
- Не превращать screenshot tests в замену функциональным assertions.
- Не считать build-only job доказательством работоспособности пользовательского сценария.

