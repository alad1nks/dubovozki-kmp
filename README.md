![Дубовозки](https://github.com/alad1nks/dubovozki-kmp/blob/main/.readme/wide-icon.png)

Multi-platform application with bus schedule for HSE Students

Keep track of the current bus schedule for the Dubki dormitory using convenient app "Дубовозки". No need to search for
the right cell in the old schedule file Just open it and instantly find out the next bus departure time.

<img height="800" src="https://github.com/alad1nks/dubovozki-kmp/blob/main/.readme/screenshot_1.png" width="360"/>
<img height="800" src="https://github.com/alad1nks/dubovozki-kmp/blob/main/.readme/screenshot_2.png" width="360"/>
<img height="800" src="https://github.com/alad1nks/dubovozki-kmp/blob/main/.readme/screenshot_3.png" width="360"/>

---

This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop (JVM).

* [/composeApp](./composeApp) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain)
    folder is the appropriate location.

* [/androidApp](./androidApp) contains Android applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your Android app.

* [/iosApp](./iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :androidApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :androidApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
    ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.
