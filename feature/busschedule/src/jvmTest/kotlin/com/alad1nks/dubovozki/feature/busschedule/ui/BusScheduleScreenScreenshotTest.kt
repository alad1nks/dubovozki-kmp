package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.feature.busschedule.model.BusScheduleTopAppBarUiState
import com.alad1nks.dubovozki.feature.busschedule.model.BusScheduleUiState
import com.alad1nks.dubovozki.feature.busschedule.model.BusUi
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTestApi::class)
class BusScheduleScreenScreenshotTest {
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
    fun loading() = capture("bus_loading.png", BusScheduleUiState.Loading)

    @Test
    fun error() = capture("bus_error.png", BusScheduleUiState.Error("offline"))

    @Test
    fun content() = capture("bus_content.png", contentState)

    @Test
    fun empty() =
        capture(
            "bus_empty.png",
            contentState.copy(
                moscowBusList = emptyList(),
                firstMoscowBusIndex = null,
            ),
        )

    @Test
    fun staleContent() =
        capture(
            "bus_stale.png",
            contentState.copy(isStale = true),
        )

    private fun capture(
        fileName: String,
        uiState: BusScheduleUiState,
        topAppBarUiState: BusScheduleTopAppBarUiState = BusScheduleTopAppBarUiState(),
    ) = runDesktopComposeUiTest(width = SCREEN_WIDTH, height = SCREEN_HEIGHT) {
        setContent {
            AppTheme(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BusScheduleScreen(
                        topAppBarUiState = topAppBarUiState,
                        uiState = uiState,
                        onStationFilterSelect = {},
                        onStationFilterSpinnerClick = {},
                        onStationFilterSpinnerDismissRequest = {},
                        onDayOfWeekFilterSelect = {},
                        onDayOfWeekFilterSpinnerClick = {},
                        onDayOfWeekFilterSpinnerDismissRequest = {},
                        onResetFilters = {},
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
            BusScheduleUiState.Content(
                moscowBusList =
                    listOf(
                        BusUi(1, "09:00", 5 * 60_000, Bus.Station.ODINTSOVO),
                        BusUi(2, "10:00", 65 * 60_000, Bus.Station.SLAVYANSKY_BULVAR),
                        BusUi(3, "11:30", null, Bus.Station.MOLODYOZHNAYA),
                    ),
                dubkiBusList =
                    listOf(
                        BusUi(4, "09:10", 15 * 60_000, Bus.Station.ODINTSOVO),
                        BusUi(5, "10:10", 75 * 60_000, Bus.Station.MOLODYOZHNAYA),
                    ),
                firstMoscowBusIndex = 0,
                firstDubkiBusIndex = 0,
                updatedAtEpochMillis = 1_788_163_200_000,
                isStale = false,
            )
    }
}
