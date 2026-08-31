import org.gradle.api.tasks.testing.Test
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "com.alad1nks.dubovozki.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }

        androidResources {
            enable = true
        }

        withHostTest {
            isIncludeAndroidResources = true
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    sourceSets {
        androidMain.dependencies {
            implementation(projects.core.storage.datastore)
        }
        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material.icons.extended)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.navigation.compose)

            implementation(projects.core.data)
            implementation(projects.core.designsystem)
            implementation(projects.core.domain)
            implementation(projects.core.firebase)
            implementation(projects.core.model)
            implementation(projects.core.navigation)
            implementation(projects.core.storage.common)
            implementation(projects.feature.busschedule)
            implementation(projects.feature.services)
            implementation(projects.feature.servicesschedule)
            implementation(projects.feature.settings)
            implementation(projects.resources)

            implementation(project.dependencies.platform(libs.koin.bom))
        }
        commonTest.dependencies {
            implementation(libs.compose.ui.test)
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.datetime)
        }
        iosMain.dependencies {
            implementation(projects.core.storage.datastore)
        }
        jsMain.dependencies {
            implementation(projects.core.storage.js)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)

            implementation(projects.core.storage.datastore)
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.ui.tooling)
}

compose.desktop {
    application {
        mainClass = "com.alad1nks.dubovozki.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.alad1nks.dubovozki"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<Test>().configureEach {
    // Compose Navigation keeps platform lifecycle state in the process. A fresh fork guarantees
    // that every application E2E starts from an empty lifecycle, Koin, and dispatcher state.
    forkEvery = 1
}
