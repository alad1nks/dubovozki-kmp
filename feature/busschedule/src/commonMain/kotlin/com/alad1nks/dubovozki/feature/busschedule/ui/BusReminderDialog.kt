package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.feature.busschedule.model.BusUi
import com.alad1nks.dubovozki.feature.busschedule.reminder.BusReminderMethod
import com.alad1nks.dubovozki.feature.busschedule.reminder.BusReminderRequest
import com.alad1nks.dubovozki.feature.busschedule.reminder.maxReminderMinutes
import com.alad1nks.dubovozki.feature.busschedule.reminder.parseReminderMinutes
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.alad1nks.dubovozki.feature.designsystem.e2eTestTag
import com.alad1nks.dubovozki.resources.AppResource
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock

@Composable
internal fun BusReminderDialog(
    bus: BusUi,
    departureEpochMillis: Long,
    supportedMethods: Set<BusReminderMethod>,
    onConfirm: (BusReminderRequest) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var nowEpochMillis by remember(departureEpochMillis) {
        mutableLongStateOf(Clock.System.now().toEpochMilliseconds())
    }
    LaunchedEffect(departureEpochMillis) {
        while (true) {
            nowEpochMillis = Clock.System.now().toEpochMilliseconds()
            delay(1_000)
        }
    }

    val maxMinutes = maxReminderMinutes(departureEpochMillis, nowEpochMillis)
    val isDeparted = departureEpochMillis <= nowEpochMillis
    var minutesText by remember(bus.id, departureEpochMillis) {
        mutableStateOf(minOf(DEFAULT_REMINDER_MINUTES, maxMinutes).toString())
    }
    var selectedMethod by remember(bus.id, supportedMethods) {
        mutableStateOf(
            when {
                BusReminderMethod.NOTIFICATION in supportedMethods -> BusReminderMethod.NOTIFICATION
                else -> supportedMethods.firstOrNull()
            },
        )
    }
    val minutes = parseReminderMinutes(minutesText, maxMinutes)
    val station = bus.station.text
    val notificationTitle = stringResource(AppResource.String.bus_reminder_notification_title, bus.dayTime)
    val notificationBody =
        stringResource(
            AppResource.String.bus_reminder_notification_body,
            minutes ?: 0,
            station,
        )
    val alarmLabel = stringResource(AppResource.String.bus_reminder_alarm_label, bus.dayTime, station)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.e2eTestTag(TestTags.BUS_REMINDER_DIALOG),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = stringResource(AppResource.String.bus_reminder_dialog_title))
                Text(
                    text = stringResource(AppResource.String.bus_reminder_departure, bus.dayTime, station),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { value ->
                        if (value.length <= MAX_MINUTES_INPUT_LENGTH && value.all(Char::isDigit)) {
                            minutesText = value
                        }
                    },
                    modifier = Modifier.fillMaxWidth().e2eTestTag(TestTags.BUS_REMINDER_MINUTES),
                    enabled = !isDeparted,
                    isError = minutes == null,
                    label = { Text(stringResource(AppResource.String.bus_reminder_minutes_label)) },
                    supportingText = {
                        Text(
                            if (isDeparted) {
                                stringResource(AppResource.String.bus_reminder_departed)
                            } else if (minutes == null) {
                                stringResource(AppResource.String.bus_reminder_minutes_error, maxMinutes)
                            } else {
                                stringResource(AppResource.String.bus_reminder_minutes_supporting, maxMinutes)
                            },
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(AppResource.String.bus_reminder_method_title),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    BusReminderMethod.entries.forEach { method ->
                        ReminderMethodRow(
                            method = method,
                            selected = selectedMethod == method,
                            enabled = method in supportedMethods && !isDeparted,
                            onSelect = { selectedMethod = method },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalMinutes = minutes ?: return@TextButton
                    val finalMethod = selectedMethod ?: return@TextButton
                    onConfirm(
                        BusReminderRequest(
                            busId = bus.id,
                            departureEpochMillis = departureEpochMillis,
                            minutesBeforeDeparture = finalMinutes,
                            method = finalMethod,
                            notificationTitle = notificationTitle,
                            notificationBody = notificationBody,
                            alarmLabel = alarmLabel,
                        ),
                    )
                },
                modifier = Modifier.e2eTestTag(TestTags.BUS_REMINDER_SET),
                enabled = minutes != null && selectedMethod in supportedMethods && !isDeparted,
            ) {
                Text(text = stringResource(AppResource.String.bus_reminder_set))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = stringResource(AppResource.String.bus_reminder_cancel))
            }
        },
    )
}

@Composable
private fun ReminderMethodRow(
    method: BusReminderMethod,
    selected: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = selected,
                    enabled = enabled,
                    role = Role.RadioButton,
                    onClick = onSelect,
                )
                .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled,
        )
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text =
                    stringResource(
                        when (method) {
                            BusReminderMethod.ALARM -> AppResource.String.bus_reminder_method_alarm
                            BusReminderMethod.NOTIFICATION -> AppResource.String.bus_reminder_method_notification
                        },
                    ),
                color =
                    if (enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    },
                style = MaterialTheme.typography.bodyLarge,
            )
            if (!enabled) {
                Text(
                    text = stringResource(AppResource.String.bus_reminder_method_unavailable),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private val Bus.Station.text: String
    @Composable get() =
        when (this) {
            Bus.Station.ODINTSOVO -> stringResource(AppResource.String.bus_schedule_station_odintsovo)
            Bus.Station.SLAVYANSKY_BULVAR ->
                stringResource(AppResource.String.bus_schedule_station_slavyansky_bulvar)
            Bus.Station.MOLODYOZHNAYA ->
                stringResource(AppResource.String.bus_schedule_station_molodyozhnaya)
        }

private const val DEFAULT_REMINDER_MINUTES = 10
private const val MAX_MINUTES_INPUT_LENGTH = 4
