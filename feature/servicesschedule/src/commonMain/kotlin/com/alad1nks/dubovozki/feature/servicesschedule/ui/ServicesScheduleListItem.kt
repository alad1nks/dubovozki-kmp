package com.alad1nks.dubovozki.feature.servicesschedule.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alad1nks.dubovozki.resources.AppResource
import kotlinx.datetime.DayOfWeek
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ServicesScheduleListItem(
    day: DayOfWeek,
    time: String,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    todayColor: Color = MaterialTheme.colorScheme.primary,
    defaultColor: Color = MaterialTheme.colorScheme.secondary,
    todayTextColor: Color = MaterialTheme.colorScheme.onPrimary,
    defaultTextColor: Color = MaterialTheme.colorScheme.onSecondary,
) {
    ListItem(
        headlineContent = { Text(text = time) },
        modifier = modifier,
        leadingContent = {
            Box(
                modifier =
                    Modifier
                        .drawBehind {
                            drawCircle(
                                color = if (isToday) todayColor else defaultColor,
                                radius = 32.dp.toPx(),
                            )
                        },
            ) {
                Box(modifier = Modifier.padding(32.dp))

                Text(
                    text = day.text,
                    color = if (isToday) todayTextColor else defaultTextColor,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        },
    )
}

private val DayOfWeek.text: String @Composable get() =
    when (this) {
        DayOfWeek.MONDAY -> stringResource(AppResource.String.services_schedule_day_of_week_monday)
        DayOfWeek.TUESDAY -> stringResource(AppResource.String.services_schedule_day_of_week_tuesday)
        DayOfWeek.WEDNESDAY -> stringResource(AppResource.String.services_schedule_day_of_week_wednesday)
        DayOfWeek.THURSDAY -> stringResource(AppResource.String.services_schedule_day_of_week_thursday)
        DayOfWeek.FRIDAY -> stringResource(AppResource.String.services_schedule_day_of_week_friday)
        DayOfWeek.SATURDAY -> stringResource(AppResource.String.services_schedule_day_of_week_saturday)
        DayOfWeek.SUNDAY -> stringResource(AppResource.String.services_schedule_day_of_week_sunday)
    }

@Preview
@Composable
private fun ServiceScheduleListItemTodayPreview() {
    ServicesScheduleListItem(
        day = DayOfWeek.MONDAY,
        time = "09:30-18:00",
        isToday = true,
    )
}

@Preview
@Composable
private fun ServiceScheduleListItemDefaultPreview() {
    ServicesScheduleListItem(
        day = DayOfWeek.MONDAY,
        time = "09:30-18:00",
        isToday = false,
    )
}
