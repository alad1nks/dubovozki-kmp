@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.native.tasks.PodBuildTask
import org.jetbrains.kotlin.gradle.targets.native.tasks.PodGenTask

plugins {
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    android {
        namespace = "com.alad1nks.dubovozki.core.firebase"
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

    iosArm64()
    iosSimulatorArm64()

    jvm()

    js {
        browser()
        binaries.executable()
    }

    cocoapods {
        version = "1.0"
        summary = "Firebase module"
        homepage = "Link to a Kotlin/Native module homepage"

        name = "Firebase"

        ios.deploymentTarget = "16.0"

        pod("FirebaseDatabase")

        framework {
            baseName = "FirebaseDatabase"
            isStatic = false
            transitiveExport = false
        }

        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.firebase.database)

            implementation(project.dependencies.platform(libs.firebase.bom))
        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(project.dependencies.platform(libs.koin.bom))

            implementation(projects.core.model)
        }
        jsMain.dependencies {
            implementation(npm("firebase", "12.11.0"))
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
    }
}

// The Kotlin plugin only raises synthetic Pods to iOS 12, which newer Xcode versions reject.
// Apply the module's minimum to every Pod while preserving dependencies with a higher minimum.
tasks.withType<PodGenTask>().configureEach {
    val deploymentTarget = requireNotNull(kotlin.cocoapods.ios.deploymentTarget)
    inputs.property("minimumIosDeploymentTarget", deploymentTarget)
    doLast {
        val podfile = podfile.get()
        val configurationLoop = "target.build_configurations.each do |config|"
        val deploymentTargetOverride =
            """
            $configurationLoop
                  minimum_target = Gem::Version.new('$deploymentTarget')
                  current_target = Gem::Version.new(config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] || '0')
                  if current_target < minimum_target
                    config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '$deploymentTarget'
                  end
            """.trimIndent()
        val content = podfile.readText()
        check(configurationLoop in content) { "Synthetic Podfile no longer contains the expected configuration loop" }
        podfile.writeText(content.replace(configurationLoop, deploymentTargetOverride))
    }
}

// CocoaPods configures linking only in its own project. Share the simulator build settings
// with the application's test executable, which consumes this module's cinterop transitively.
val iosSimulatorTestPods =
    configurations.create("iosSimulatorTestPods") {
        isCanBeConsumed = true
        isCanBeResolved = false
    }
val simulatorPodBuild = tasks.named<PodBuildTask>("podBuildFirebaseDatabaseIosSimulator")
artifacts.add(iosSimulatorTestPods.name, simulatorPodBuild.flatMap { it.buildSettingsFile }) {
    builtBy(simulatorPodBuild)
}
