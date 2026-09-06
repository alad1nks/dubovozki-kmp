package com.alad1nks.dubovozki.feature.busschedule.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.AlarmClock
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Composable
internal actual fun rememberBusReminderLauncher(
    onResult: (BusReminderResult) -> Unit,
): BusReminderLauncher {
    val context = LocalContext.current.applicationContext
    val currentOnResult by rememberUpdatedState(onResult)
    var pendingNotification by remember { mutableStateOf<BusReminderRequest?>(null) }
    val exactAlarmPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val request = pendingNotification
            pendingNotification = null
            currentOnResult(
                when {
                    request == null -> BusReminderResult.Failed
                    request.triggerAtEpochMillis <= Clock.System.now().toEpochMilliseconds() ->
                        BusReminderResult.TooLate
                    canScheduleExactNotifications(context) -> scheduleExactNotification(context, request)
                    else -> BusReminderResult.PermissionDenied
                },
            )
        }
    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            val request = pendingNotification
            when {
                !granted -> {
                    pendingNotification = null
                    currentOnResult(BusReminderResult.PermissionDenied)
                }
                request == null -> currentOnResult(BusReminderResult.Failed)
                needsExactAlarmPermission(context) -> {
                    exactAlarmPermissionLauncher.launch(exactAlarmPermissionIntent(context))
                }
                else -> {
                    pendingNotification = null
                    currentOnResult(scheduleExactNotification(context, request))
                }
            }
        }

    return BusReminderLauncher(
        supportedMethods = BusReminderMethod.entries.toSet(),
        launch = { request ->
            if (request.triggerAtEpochMillis <= Clock.System.now().toEpochMilliseconds()) {
                currentOnResult(BusReminderResult.TooLate)
            } else {
                when (request.method) {
                    BusReminderMethod.ALARM -> currentOnResult(openSystemAlarm(context, request))
                    BusReminderMethod.NOTIFICATION -> {
                        if (needsNotificationPermission(context)) {
                            pendingNotification = request
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else if (needsExactAlarmPermission(context)) {
                            pendingNotification = request
                            exactAlarmPermissionLauncher.launch(exactAlarmPermissionIntent(context))
                        } else {
                            currentOnResult(scheduleExactNotification(context, request))
                        }
                    }
                }
            }
        },
    )
}

private fun needsNotificationPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

private fun needsExactAlarmPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !canScheduleExactNotifications(context)

private fun canScheduleExactNotifications(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()

private fun exactAlarmPermissionIntent(context: Context): Intent =
    Intent(
        Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
        Uri.parse("package:${context.packageName}"),
    )

@OptIn(ExperimentalTime::class)
private fun openSystemAlarm(
    context: Context,
    request: BusReminderRequest,
): BusReminderResult {
    val triggerTime =
        Instant.fromEpochMilliseconds(request.triggerAtEpochMillis)
            .toLocalDateTime(TimeZone.currentSystemDefault())
    val intent =
        Intent(AlarmClock.ACTION_SET_ALARM)
            .putExtra(AlarmClock.EXTRA_HOUR, triggerTime.hour)
            .putExtra(AlarmClock.EXTRA_MINUTES, triggerTime.minute)
            .putExtra(AlarmClock.EXTRA_MESSAGE, request.alarmLabel)
            .putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    return runCatching {
        context.startActivity(intent)
        BusReminderResult.Scheduled(BusReminderMethod.ALARM)
    }.getOrDefault(BusReminderResult.Unsupported)
}

private fun scheduleExactNotification(
    context: Context,
    request: BusReminderRequest,
): BusReminderResult =
    runCatching {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val receiverIntent =
            Intent(context, BusReminderReceiver::class.java)
                .putExtra(BusReminderReceiver.EXTRA_TITLE, request.notificationTitle)
                .putExtra(BusReminderReceiver.EXTRA_BODY, request.notificationBody)
                .putExtra(BusReminderReceiver.EXTRA_NOTIFICATION_ID, request.notificationId)
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                request.notificationId,
                receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            request.triggerAtEpochMillis,
            pendingIntent,
        )
        BusReminderResult.Scheduled(BusReminderMethod.NOTIFICATION)
    }.getOrDefault(BusReminderResult.Failed)

private val BusReminderRequest.notificationId: Int
    get() = 31 * busId + departureEpochMillis.hashCode()
