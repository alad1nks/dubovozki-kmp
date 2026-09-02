package com.alad1nks.dubovozki.feature.busschedule.reminder

internal enum class BusReminderMethod {
    ALARM,
    NOTIFICATION,
}

internal data class BusReminderRequest(
    val busId: Int,
    val departureEpochMillis: Long,
    val minutesBeforeDeparture: Int,
    val method: BusReminderMethod,
    val notificationTitle: String,
    val notificationBody: String,
    val alarmLabel: String,
) {
    val triggerAtEpochMillis: Long
        get() = departureEpochMillis - minutesBeforeDeparture * MILLIS_PER_MINUTE
}

internal sealed interface BusReminderResult {
    data class Scheduled(val method: BusReminderMethod) : BusReminderResult

    data object PermissionDenied : BusReminderResult

    data object TooLate : BusReminderResult

    data object Unsupported : BusReminderResult

    data object Failed : BusReminderResult
}

internal data class BusReminderLauncher(
    val supportedMethods: Set<BusReminderMethod>,
    val launch: (BusReminderRequest) -> Unit,
)

internal fun maxReminderMinutes(
    departureEpochMillis: Long,
    nowEpochMillis: Long,
): Int =
    ((departureEpochMillis - nowEpochMillis).coerceAtLeast(0L) / MILLIS_PER_MINUTE)
        .coerceAtMost(Int.MAX_VALUE.toLong())
        .toInt()

internal fun parseReminderMinutes(
    value: String,
    maxMinutes: Int,
): Int? = value.toIntOrNull()?.takeIf { it in 0..maxMinutes }

internal const val MILLIS_PER_MINUTE = 60_000L
