@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.alad1nks.dubovozki.feature.busschedule.reminder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.launch
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Clock

@Composable
internal actual fun rememberBusReminderLauncher(
    onResult: (BusReminderResult) -> Unit,
): BusReminderLauncher {
    val coroutineScope = rememberCoroutineScope()
    val currentOnResult = rememberUpdatedState(onResult)

    return remember(coroutineScope) {
        BusReminderLauncher(
            supportedMethods = setOf(BusReminderMethod.NOTIFICATION),
            launch = { request ->
                coroutineScope.launch {
                    currentOnResult.value(scheduleNotification(request))
                }
            },
        )
    }
}

private suspend fun scheduleNotification(request: BusReminderRequest): BusReminderResult {
    val delayMillis = request.triggerAtEpochMillis - Clock.System.now().toEpochMilliseconds()
    if (delayMillis <= 0) return BusReminderResult.TooLate
    if (request.method != BusReminderMethod.NOTIFICATION) return BusReminderResult.Unsupported

    val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    val authorized =
        suspendCoroutine { continuation ->
            notificationCenter.requestAuthorizationWithOptions(
                options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound,
            ) { granted, error ->
                continuation.resume(granted && error == null)
            }
        }
    if (!authorized) return BusReminderResult.PermissionDenied

    val content =
        UNMutableNotificationContent().apply {
            setTitle(request.notificationTitle)
            setBody(request.notificationBody)
            setSound(UNNotificationSound.defaultSound)
        }
    val trigger =
        UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = delayMillis / 1_000.0,
            repeats = false,
        )
    val notificationRequest =
        UNNotificationRequest.requestWithIdentifier(
            identifier = "bus-${request.busId}-${request.departureEpochMillis}",
            content = content,
            trigger = trigger,
        )

    return suspendCoroutine { continuation ->
        notificationCenter.addNotificationRequest(notificationRequest) { error ->
            continuation.resume(
                if (error == null) {
                    BusReminderResult.Scheduled(BusReminderMethod.NOTIFICATION)
                } else {
                    BusReminderResult.Failed
                },
            )
        }
    }
}
