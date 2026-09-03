package com.alad1nks.dubovozki.feature.busschedule.reminder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BusReminderTest {
    @Test
    fun maximumMinutesNeverExceedsActualRemainingTime() {
        assertEquals(10, maxReminderMinutes(departureEpochMillis = 659_999, nowEpochMillis = 0))
        assertEquals(10, maxReminderMinutes(departureEpochMillis = 600_000, nowEpochMillis = 0))
        assertEquals(0, maxReminderMinutes(departureEpochMillis = 59_999, nowEpochMillis = 0))
        assertEquals(0, maxReminderMinutes(departureEpochMillis = 0, nowEpochMillis = 1))
    }

    @Test
    fun minutesMustBeInsideCurrentAllowedRange() {
        assertEquals(0, parseReminderMinutes("0", maxMinutes = 10))
        assertEquals(10, parseReminderMinutes("10", maxMinutes = 10))
        assertNull(parseReminderMinutes("11", maxMinutes = 10))
        assertNull(parseReminderMinutes("-1", maxMinutes = 10))
        assertNull(parseReminderMinutes("", maxMinutes = 10))
        assertNull(parseReminderMinutes("five", maxMinutes = 10))
    }

    @Test
    fun triggerTimeUsesSelectedLeadTime() {
        val request =
            BusReminderRequest(
                busId = 1,
                departureEpochMillis = 1_000_000,
                minutesBeforeDeparture = 5,
                method = BusReminderMethod.NOTIFICATION,
                notificationTitle = "Bus",
                notificationBody = "Soon",
                alarmLabel = "Bus",
            )

        assertEquals(700_000, request.triggerAtEpochMillis)
    }
}
