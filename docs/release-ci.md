# Релизы Android и iOS в CI

Workflow [Release](../.github/workflows/release.yml) запускается при каждом push в `release/**`,
включая `release/1.0` и `release/ios/1.0`. Сначала проходят общие P0-проверки Desktop и Chromium.
После их успеха независимо запускаются:

- `build`: существующие unit-тесты, подписанный Android AAB, артефакт `app-release`.
  Затем `post-build` сохраняет AAB в `alad1nks/alad1nks.github.io`, каталог `dubovozki`.
- `ios-release`: shared iOS-тесты, Xcode Release archive для устройства ARM64, подписанный IPA
  и загрузка в App Store Connect для TestFlight. Runner — `macos-26` с установленными Xcode и CocoaPods.

Ошибка одной платформы не отменяет другую. Общий workflow считается успешным только при успехе всех jobs.
Push в обычную ветку и PR в `main` не запускают release workflow.

## Создание релиза кнопкой в GitHub

После merge workflow в `main` откройте **Actions → Create release → Run workflow**,
оставьте **Use workflow from: main** и нажмите **Run workflow**. Вводить номер версии не нужно.
Workflow:

1. Получает актуальный `main` и все release-ветки из GitHub.
2. Находит максимальную числовую версию среди веток строго вида `release/X.Y` и увеличивает minor на один:
   `release/2.3 → release/2.4`, `release/2.9 → release/2.10`, `release/3.0 → release/3.1`.
   Это последняя существующая релизная ветка, независимо от результата её сборки и даты push.
   Вложенные ветки, суффиксы вроде `-beta` и версии `X.Y.Z` при вычислении не учитываются.
3. Создаёт новую ветку **из актуального `main`**, добавляя ровно один коммит
   `chore: bump release version to X.Y`. Коммит меняет Android `versionName` и iOS `MARKETING_VERSION` на `X.Y`,
   а Android `versionCode` — на максимум среди `main` и всех числовых release-веток плюс один.
   Изменения из предыдущей release-ветки в новый релиз не переносятся. Сам `main` не меняется.
4. Отправляет ветку в GitHub и явно запускает **Release** на ней: общие проверки, Android AAB и iOS TestFlight.
   Ссылка на ветку и список release-запусков появляются в Summary.

Если release-веток ещё нет, за основу берётся большая пользовательская версия Android/iOS из `main`
и также увеличивается minor. Если рассчитанная версия оказалась ниже версии в `main`, workflow остановится
до изменений: сначала согласуйте версии и release-ветки. Автоматического перехода на новый major нет.
Не удаляйте числовые release-ветки, если хотите сохранять историю нумерации: workflow не сверяется
с Google Play или App Store Connect и учитывает только существующие ветки.

Дополнительных секретов для **Create release** не требуется: используется встроенный `GITHUB_TOKEN`
с `contents: write` и `actions: write`. Настройки организации и rulesets должны разрешать GitHub Actions
создавать `release/**` и запускать workflows. Push от `GITHUB_TOKEN` не вызывает следующий push-workflow,
поэтому сборка запускается через `workflow_dispatch`.
Это [документированное поведение GitHub](https://docs.github.com/en/actions/how-tos/write-workflows/choose-when-workflows-run/trigger-a-workflow).

Создание релизов сериализовано; публикация новой ветки требует, чтобы её ещё не существовало на сервере.
Уже существующая ветка не перезаписывается даже при конкурентном создании вне workflow.
Если push прошёл, а запуск Release упал, используйте **Re-run jobs** в том же запуске Create release:
ветка находится по `Release-Workflow-Run` в коммите и повторно используется. Если в неё уже внесены новые
коммиты, скрипт остановится; запустите **Actions → Release → Run workflow** и выберите эту release-ветку.
Новый запуск через кнопку Create release создаёт следующую версию. Повторная отправка Release может
создать ещё один запуск сборки той же ветки, если предыдущая отправка успела выполниться.

Workflow **Release** также доступен для ручного запуска на существующей `release/**`-ветке;
если выбрать `main`, платформенные сборки будут пропущены. Обычные push в `release/**` продолжают работать.

## Секреты GitHub Actions

Добавьте **Repository secrets** в репозитории `alad1nks/dubovozki-kmp`:
**Settings → Secrets and variables → Actions → New repository secret**.
Workflow не использует GitHub Environment: секреты, добавленные только в environment, ему недоступны.

### iOS / TestFlight

| Точное имя | Содержимое и источник |
|---|---|
| `FIREBASE_CONFIG_IOS` | Base64 настоящего `GoogleService-Info.plist` для iOS-приложения из Firebase Console. Этот секрет уже используется другими iOS workflows. |
| `IOS_DISTRIBUTION_CERTIFICATE_BASE64` | Base64 файла `.p12`, экспортированного из Keychain Access: сертификат **Apple Distribution вместе с приватным ключом**. |
| `IOS_DISTRIBUTION_CERTIFICATE_PASSWORD` | Непустой пароль, заданный при экспорте этого `.p12`. Обычный текст. |
| `IOS_PROVISIONING_PROFILE_BASE64` | Base64 файла `.mobileprovision` типа **App Store Connect**, выпущенного для App ID `com.alad1nks.dubovozki` и сертификата выше. |
| `APPLE_TEAM_ID` | Team ID из Apple Developer → Membership details. Обычный текст; не Issuer ID и не числовой Apple ID приложения. |
| `APP_STORE_CONNECT_API_KEY_ID` | Key ID командного API-ключа App Store Connect. Обычный текст. |
| `APP_STORE_CONNECT_API_ISSUER_ID` | Issuer ID команды из App Store Connect → Users and Access → Integrations → App Store Connect API. Обычный текст. |
| `APP_STORE_CONNECT_API_KEY_BASE64` | Base64 приватного файла `AuthKey_<KEY_ID>.p8` для этого API-ключа. |

Дополнительный секрет пароля временного keychain не нужен: CI генерирует случайный пароль на каждый запуск.
Название и UUID профиля извлекаются из `.mobileprovision`, отдельно передавать их не нужно.
API-ключ используется для загрузки; подпись выполняется сертификатом и профилем.

### Существующие Android-секреты

| Точное имя | Содержимое |
|---|---|
| `FIREBASE_CONFIG_ANDROID` | Base64 `androidApp/google-services.json` из Firebase. |
| `KEYSTORE_BASE64` | Base64 release keystore `.jks`. |
| `KEY_ALIAS` | Alias ключа в keystore. |
| `KEY_PASSWORD` | Пароль ключа. |
| `KEYSTORE_PASSWORD` | Пароль keystore. |
| `ACCESS_TOKEN` | GitHub token с правом записи в `alad1nks/alad1nks.github.io` для сохранения AAB. Для fine-grained PAT: доступ к этому репозиторию и `Contents: Read and write`. |

Эти секреты и существующий способ доставки Android AAB сохраняются.

## Первичная настройка Apple

1. Нужна действующая подписка Apple Developer Program и принятые соглашения в App Store Connect.
   Зарегистрируйте explicit App ID `com.alad1nks.dubovozki` и создайте с ним приложение в App Store Connect.
   Этот bundle ID уже задан в `iosApp/Configuration/Config.xcconfig`; workflow его не меняет.
2. Создайте или возьмите действующий **Apple Distribution** certificate в Apple Developer →
   Certificates, Identifiers & Profiles. На Mac, где есть соответствующий приватный ключ,
   экспортируйте сертификат с ключом из Keychain Access в защищённый паролем `.p12`.
   Один `.cer` без приватного ключа для CI не подходит.
3. В Profiles создайте distribution-профиль **App Store Connect**, выбрав App ID и тот же сертификат.
   Скачайте `.mobileprovision`. Development, Ad Hoc, Enterprise и wildcard-профили здесь не подходят.
4. В App Store Connect → Users and Access → Integrations → App Store Connect API запросите API-доступ,
   если он ещё не включён, и создайте **Team API key** с ролью **Developer** (достаточно для загрузки сборок).
   Создать командный ключ может Account Holder или Admin. Сохраните Key ID, Issuer ID и `.p8`;
   скачать приватный ключ повторно нельзя. Individual API key для этого workflow не используется.
5. Закодируйте файлы и сохраните все восемь iOS-секретов в GitHub. Значение Firebase `BUNDLE_ID`, профиль
   и Apple-приложение должны соответствовать `com.alad1nks.dubovozki` и одной команде.
6. В App Store Connect → приложение → TestFlight создайте группу Internal Testing, добавьте тестировщиков
   и включите **Enable automatic distribution**, чтобы обработанные сборки автоматически попадали группе.
   Заполните необходимые сведения об экспортном соответствии (encryption/export compliance).
   Если Apple показывает Missing Compliance, ответьте на вопросы для сборки; CI не делает декларацию за владельца.
   Для external testing отдельно нужны сведения о бета-тестировании и, при необходимости, Beta App Review.

Официальные инструкции: [подпись в GitHub Actions](https://docs.github.com/en/actions/how-tos/deploy/deploy-to-third-party-platforms/sign-xcode-applications),
[API-ключи Apple](https://developer.apple.com/help/app-store-connect/get-started/app-store-connect-api/),
[загрузка сборок](https://developer.apple.com/help/app-store-connect/manage-builds/upload-builds/),
[внутренние тестировщики](https://developer.apple.com/help/app-store-connect/test-a-beta-version/add-internal-testers/).

### Как получить Base64

На macOS (результат копируется в буфер обмена; повторите для каждого файла):

```shell
base64 -i /path/to/distribution.p12 | tr -d '\n' | pbcopy
base64 -i /path/to/app-store.mobileprovision | tr -d '\n' | pbcopy
base64 -i /path/to/AuthKey_KEYID.p8 | tr -d '\n' | pbcopy
base64 -i /path/to/GoogleService-Info.plist | tr -d '\n' | pbcopy
```

На Windows PowerShell:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes('C:\path\distribution.p12')) | Set-Clipboard
```

Повторите с путями остальных файлов. Вставляйте результат как значение соответствующего секрета целиком,
без кавычек. Пароль, Team ID, Key ID и Issuer ID кодировать не нужно.
Не добавляйте исходные файлы или их Base64 в Git.

## Версии, повторные запуски и результат

- Пользовательская версия берётся из `MARKETING_VERSION` в `iosApp/Configuration/Config.xcconfig`
  (в `main` сейчас `1.0`). Create release обновляет её вместе с Android `versionName` в новом коммите.
  При ручном создании ветки обновите версии самостоятельно: одно имя ветки значения в коде не меняет.
- `CFBundleVersion` задаётся только при CI-сборке по формуле
  `(github.run_number / 100 + 1).(github.run_number % 100).github.run_attempt`, с целочисленным делением.
  Например, запуск 123 → `2.23.1`, его повтор → `2.23.2`, запуск 124 → `2.24.1`.
  Это укладывается в ограничения Apple на длину компонентов и не повторяет номер при Re-run jobs.
  Скрипт поддерживает run number до 999899 и attempt до 99; при превышении останавливается до подписи.
- Перед первой загрузкой проверьте, что вычисленный номер выше ранее загруженных сборок той же версии.
  После более нового релиза повторяйте последний запуск; повтор старого может быть отклонён Apple
  как более низкий номер. Одновременные релизы могут загрузиться не по порядку: повторите актуальный запуск,
  если Apple отклонила его номер. Уже успешно загруженные версии не удаляются.
- CI создаёт архив штатной схемой `iosApp` и вызывает существующий `embedAndSignAppleFrameworkForXcode`.
  Настройки manual signing и пути/линковка `ComposeApp.framework` передаются в `xcodebuild` только для CI.
- IPA, dSYM и логи сохраняются на 14 дней в артефакте `ios-release-<run_number>-<run_attempt>`.
  При ошибке сохраняются доступные диагностические файлы. Ключи подписи, API-ключ и временный keychain
  удаляются при завершении скрипта; они не входят в артефакт. Runner одноразовый.
- Зелёный шаг загрузки означает, что App Store Connect принял файл. Обработка Apple асинхронная:
  проверьте статус в TestFlight. Это не отправка приложения на публичный App Store Review.

## Проверка после merge

1. Убедитесь, что секреты настроены, и запустите **Actions → Create release → Run workflow** из `main`.
2. Проверьте новую ветку и коммит версий, затем откройте Actions → Release.
   После `p0-e2e` должны стартовать оба платформенных job.
3. Проверьте Android `app-release` и `post-build`, затем iOS `ios-release-*` и шаг загрузки.
4. В App Store Connect проверьте новую версию/номер сборки в TestFlight, завершение обработки Apple
   и доступность для настроенной группы тестировщиков.

Сам PR проверяется без выполнения release workflow. Полную подпись и доставку в TestFlight можно проверить
только на macOS с действующими Apple-секретами; локальные проверки на Windows этого не подтверждают.
