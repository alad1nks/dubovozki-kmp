package com.alad1nks.dubovozki.feature.settings.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import com.alad1nks.dubovozki.core.model.Language
import com.alad1nks.dubovozki.core.model.ThemeMode
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.alad1nks.dubovozki.feature.settings.model.SettingsUiState
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class SettingsScreenScreenshotTest {
    private lateinit var previousLocale: Locale

    @BeforeTest
    fun useStableLocale() {
        previousLocale = Locale.getDefault()
        Locale.setDefault(Locale.ENGLISH)
    }

    @AfterTest
    fun restoreLocale() {
        Locale.setDefault(previousLocale)
    }

    @Test
    fun loading() = capture("settings_loading.png", SettingsUiState.Loading)

    @Test
    fun systemPreferences() =
        capture(
            "settings_system.png",
            SettingsUiState.Content(ThemeMode.SYSTEM, Language.SYSTEM),
        )

    @Test
    fun explicitLightRussianPreferences() =
        capture(
            "settings_light_russian.png",
            SettingsUiState.Content(ThemeMode.LIGHT, Language.RUSSIAN),
        )

    @Test
    fun darkEnglishPreferences() =
        capture(
            fileName = "settings_dark_english.png",
            uiState = SettingsUiState.Content(ThemeMode.DARK, Language.ENGLISH),
            darkTheme = true,
        )

    private fun capture(
        fileName: String,
        uiState: SettingsUiState,
        darkTheme: Boolean = false,
    ) = runDesktopComposeUiTest(width = SCREEN_WIDTH, height = SCREEN_HEIGHT) {
        setContent {
            AppTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SettingsScreen(
                        uiState = uiState,
                        onThemeModeSelect = {},
                        onLanguageSelect = {},
                    )
                }
            }
        }

        waitForIdle()
        onRoot().captureRoboImage(fileName, roborazziOptions)
    }

    private companion object {
        const val SCREEN_WIDTH = 390
        const val SCREEN_HEIGHT = 844

        val roborazziOptions =
            RoborazziOptions(
                compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.01f),
            )
    }
}
