package com.alad1nks.dubovozki.feature.servicesschedule.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.v2.runDesktopComposeUiTest
import com.alad1nks.dubovozki.core.model.ServicesScheduleType
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.alad1nks.dubovozki.feature.servicesschedule.model.ServicesScheduleItemUi
import com.alad1nks.dubovozki.feature.servicesschedule.model.ServicesScheduleUiState
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage
import kotlinx.datetime.DayOfWeek
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ServicesScheduleScreenScreenshotTest {
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
    fun loading() = capture("service_schedule_loading.png", ServicesScheduleUiState.Loading)

    @Test
    fun error() = capture("service_schedule_error.png", ServicesScheduleUiState.Error("offline"))

    @Test
    fun content() = capture("service_schedule_content.png", contentState)

    @Test
    fun emptyBuilding() =
        capture(
            "service_schedule_empty.png",
            contentState.copy(firstBuildingSchedule = emptyList()),
        )

    @Test
    fun staleContent() =
        capture(
            "service_schedule_stale.png",
            contentState.copy(isStale = true),
        )

    private fun capture(
        fileName: String,
        uiState: ServicesScheduleUiState,
    ) = runDesktopComposeUiTest(width = SCREEN_WIDTH, height = SCREEN_HEIGHT) {
        setContent {
            AppTheme(darkTheme = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ServiceScheduleScreen(
                        uiState = uiState,
                        servicesScheduleType = ServicesScheduleType.LINEN_ROOM,
                        onBackClick = {},
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
            ServicesScheduleUiState.Content(
                firstBuildingSchedule =
                    listOf(
                        ServicesScheduleItemUi(DayOfWeek.MONDAY, "10:00–12:00", isToday = true),
                        ServicesScheduleItemUi(DayOfWeek.WEDNESDAY, "18:00–20:00", isToday = false),
                        ServicesScheduleItemUi(DayOfWeek.FRIDAY, "17:00–19:00", isToday = false),
                    ),
                secondBuildingSchedule =
                    listOf(
                        ServicesScheduleItemUi(DayOfWeek.TUESDAY, "11:00–13:00", isToday = false),
                    ),
                thirdBuildingSchedule =
                    listOf(
                        ServicesScheduleItemUi(DayOfWeek.SATURDAY, "12:00–14:00", isToday = false),
                    ),
                updatedAtEpochMillis = 1_788_163_200_000,
                isStale = false,
            )
    }
}
