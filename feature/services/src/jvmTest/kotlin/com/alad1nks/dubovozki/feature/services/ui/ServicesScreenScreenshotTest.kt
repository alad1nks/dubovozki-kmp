package com.alad1nks.dubovozki.feature.services.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import androidx.compose.ui.text.font.FontFamily
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.alad1nks.dubovozki.feature.services.model.ServicesUiState
import com.alad1nks.dubovozki.resources.AppResource
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage
import org.jetbrains.compose.resources.Font
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ServicesScreenScreenshotTest {
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
    fun loading() = capture("services_loading.png", ServicesUiState.Loading)

    @Test
    fun error() = capture("services_error.png", ServicesUiState.Error("offline"))

    @Test
    fun content() = capture("services_content.png", contentState)

    @Test
    fun unavailableLinks() =
        capture(
            "services_unavailable_links.png",
            contentState.copy(contactLink = null, donutLink = null),
        )

    @Test
    fun staleContent() =
        capture(
            "services_stale.png",
            contentState.copy(isStale = true),
        )

    private fun capture(
        fileName: String,
        uiState: ServicesUiState,
    ) = runDesktopComposeUiTest(width = SCREEN_WIDTH, height = SCREEN_HEIGHT) {
        setContent {
            AppTheme(
                darkTheme = false,
                fontFamily = FontFamily(Font(AppResource.Font.roboto_variable)),
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ServicesScreen(
                        uiState = uiState,
                        onLinenRoomClick = {},
                        onRefresh = {},
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

        val contentState =
            ServicesUiState.Content(
                contactLink = "https://t.me/dubki_contact",
                donutLink = "https://example.test/donate",
                updatedAtEpochMillis = 1_788_163_200_000,
                isStale = false,
            )
    }
}
