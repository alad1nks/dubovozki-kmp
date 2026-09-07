@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.alad1nks.dubovozki.feature.busschedule.reminder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierGregorian
import platform.Foundation.NSDateComponents
import platform.Foundation.NSTimeZone
import platform.Foundation.timeZoneForSecondsFromGMT
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Clock
import kotlin.time.Instant

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
    if (request.triggerAtEpochMillis <= Clock.System.now().toEpochMilliseconds()) return BusReminderResult.TooLate
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

    // Permission UI may stay open until after the reminder is due.
    val trigger = createBusReminderTrigger(request.triggerAtEpochMillis) ?: return BusReminderResult.TooLate

    val content =
        UNMutableNotificationContent().apply {
            setTitle(request.notificationTitle)
            setBody(request.notificationBody)
            setSound(UNNotificationSound.defaultSound)
        }
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

internal fun createBusReminderTrigger(
    triggerAtEpochMillis: Long,
    nowEpochMillis: Long = Clock.System.now().toEpochMilliseconds(),
): UNCalendarNotificationTrigger? {
    if (triggerAtEpochMillis <= nowEpochMillis) return null
    val time = Instant.fromEpochMilliseconds(triggerAtEpochMillis).toLocalDateTime(TimeZone.UTC)
    val components =
        NSDateComponents().apply {
            calendar = NSCalendar.calendarWithIdentifier(NSCalendarIdentifierGregorian)
            timeZone = NSTimeZone.timeZoneForSecondsFromGMT(0)
            year = time.year.toLong()
            month = time.month.ordinal.toLong() + 1
            day = time.day.toLong()
            hour = time.hour.toLong()
            minute = time.minute.toLong()
            second = time.second.toLong()
        }
    // An absolute UTC date cannot drift by time spent granting permission or changing time zones.
    return UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(components, repeats = false)
}
