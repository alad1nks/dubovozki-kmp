package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.alad1nks.dubovozki.feature.designsystem.theme.LocalExtendedColorScheme
import com.alad1nks.dubovozki.resources.AppResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.abs

@Composable
internal fun BusListItem(
    dayTime: String,
    timeDifference: Int?,
    station: Bus.Station,
    modifier: Modifier = Modifier,
    departedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onLongClick: (() -> Unit)? = null,
) {
    val colorScheme = LocalExtendedColorScheme.current
    val stationColor =
        when (station) {
            Bus.Station.ODINTSOVO -> colorScheme.busScheduleStationOdintsovo
            Bus.Station.SLAVYANSKY_BULVAR -> colorScheme.busScheduleStationSlavyanskyBulvar
            Bus.Station.MOLODYOZHNAYA -> colorScheme.busScheduleStationMolodyozhnaya
        }
    val isDeparted = (timeDifference ?: 0) < 0

    val color = if (isDeparted) departedColor else stationColor
    val dividerColor = if (isDeparted) departedColor else MaterialTheme.colorScheme.outlineVariant
    val reminderActionLabel = stringResource(AppResource.String.bus_reminder_long_press_action)

    Surface(
        modifier =
            modifier
                .then(
                    if (onLongClick != null) {
                        Modifier.combinedClickable(
                            onClick = {},
                            onLongClickLabel = reminderActionLabel,
                            onLongClick = onLongClick,
                        )
                    } else {
                        Modifier
                    },
                )
                .semantics(mergeDescendants = true) {},
    ) {
        Column {
            Row(
                modifier =
                    Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = dayTime,
                    color = color,
                    style = MaterialTheme.typography.headlineMedium,
                )

                Spacer(modifier = Modifier.width(16.dp))

                TimeDifference(
                    timeDifference = timeDifference,
                    departedColor = departedColor,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = station.text,
                    color = color,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            HorizontalDivider(color = dividerColor)
        }
    }
}

@Composable
private fun TimeDifference(
    timeDifference: Int?,
    departedColor: Color,
    modifier: Modifier = Modifier,
) {
    if (timeDifference == null) return

    val hours = abs(timeDifference) / 1.hours
    val minutes = abs(timeDifference) / 1.minutes % 60
    val text = timeDifferenceText(timeDifference)

    val soon = (hours == 0 && minutes in 0..10 && timeDifference >= 0)

    val finalModifier =
        if (soon) {
            modifier
                .padding(vertical = 4.dp)
                .background(color = MaterialTheme.colorScheme.error, shape = MaterialTheme.shapes.small)
                .padding(vertical = 4.dp, horizontal = 12.dp)
        } else {
            modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        }

    val color =
        when {
            timeDifference < 0 -> departedColor
            soon -> MaterialTheme.colorScheme.onError
            else -> LocalExtendedColorScheme.current.busScheduleStationOdintsovo
        }

    Text(
        text = text,
        modifier = finalModifier,
        color = color,
        style = MaterialTheme.typography.bodySmall,
    )
}

@Composable
internal fun timeDifferenceText(timeDifference: Int): String {
    if (timeDifference == 0) return stringResource(AppResource.String.bus_schedule_now)

    val absoluteDifference = abs(timeDifference)
    val hours = absoluteDifference / 1.hours
    val minutes = absoluteDifference / 1.minutes % 60
    val parts =
        buildList {
            if (hours > 0) {
                add(
                    pluralStringResource(
                        AppResource.Plural.bus_schedule_duration_hours,
                        hours,
                        hours,
                    ),
                )
            }
            if (minutes > 0 || hours == 0) {
                add(
                    pluralStringResource(
                        AppResource.Plural.bus_schedule_duration_minutes,
                        minutes,
                        minutes,
                    ),
                )
            }
        }
    val duration = parts.joinToString(separator = " ")

    return if (timeDifference > 0) {
        stringResource(AppResource.String.bus_schedule_time_until, duration)
    } else {
        stringResource(AppResource.String.bus_schedule_time_ago, duration)
    }
}

private val Bus.Station.text: String @Composable get() =
    when (this) {
        Bus.Station.ODINTSOVO -> stringResource(AppResource.String.bus_schedule_station_odintsovo)
        Bus.Station.SLAVYANSKY_BULVAR -> stringResource(AppResource.String.bus_schedule_station_slavyansky_bulvar)
        Bus.Station.MOLODYOZHNAYA -> stringResource(AppResource.String.bus_schedule_station_molodyozhnaya)
    }

private val Int.minutes: Int get() = this * 1000 * 60
private val Int.hours: Int get() = this * 1000 * 60 * 60

@Preview
@Composable
private fun BusListItemPreview() {
    AppTheme {
        BusListItem(
            dayTime = "08:30",
            timeDifference = null,
            station = Bus.Station.MOLODYOZHNAYA,
        )
    }
}

@Preview
@Composable
private fun BusListItemSoonPreview() {
    AppTheme {
        BusListItem(
            dayTime = "08:30",
            timeDifference = 60000,
            station = Bus.Station.MOLODYOZHNAYA,
        )
    }
}

@Preview
@Composable
private fun BusListItemLaterPreview() {
    AppTheme {
        BusListItem(
            dayTime = "08:30",
            timeDifference = 60000000,
            station = Bus.Station.MOLODYOZHNAYA,
        )
    }
}

@Preview
@Composable
private fun BusListItemDepartedPreview() {
    AppTheme {
        BusListItem(
            dayTime = "08:30",
            timeDifference = -60000,
            station = Bus.Station.MOLODYOZHNAYA,
        )
    }
}

@Preview
@Composable
private fun BusListItemDepartedLongAgoPreview() {
    AppTheme {
        BusListItem(
            dayTime = "08:30",
            timeDifference = -60000000,
            station = Bus.Station.MOLODYOZHNAYA,
        )
    }
}

@Preview
@Composable
private fun BusListItemDarkPreview() {
    AppTheme(
        darkTheme = true,
    ) {
        BusListItem(
            dayTime = "08:30",
            timeDifference = null,
            station = Bus.Station.MOLODYOZHNAYA,
        )
    }
}

@Preview
@Composable
private fun BusListItemSoonDarkPreview() {
    AppTheme(
        darkTheme = true,
    ) {
        BusListItem(
            dayTime = "08:30",
            timeDifference = 60000,
            station = Bus.Station.MOLODYOZHNAYA,
        )
    }
}

@Preview
@Composable
private fun BusListItemLaterDarkPreview() {
    AppTheme(
        darkTheme = true,
    ) {
        BusListItem(
            dayTime = "08:30",
            timeDifference = 60000000,
            station = Bus.Station.MOLODYOZHNAYA,
        )
    }
}

@Preview
@Composable
private fun BusListItemDepartedDarkPreview() {
    AppTheme(
        darkTheme = true,
    ) {
        BusListItem(
            dayTime = "08:30",
            timeDifference = -60000,
            station = Bus.Station.MOLODYOZHNAYA,
        )
    }
}

@Preview
@Composable
private fun BusListItemDepartedLongAgoDarkPreview() {
    AppTheme(
        darkTheme = true,
    ) {
        BusListItem(
            dayTime = "08:30",
            timeDifference = -60000000,
            station = Bus.Station.MOLODYOZHNAYA,
        )
    }
}
