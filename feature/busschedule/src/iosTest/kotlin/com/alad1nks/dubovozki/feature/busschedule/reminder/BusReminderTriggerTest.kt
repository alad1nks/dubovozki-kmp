@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.alad1nks.dubovozki.feature.busschedule.reminder

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Instant

class BusReminderTriggerTest {
    @Test
    fun permissionWaitDoesNotMoveTheDepartureReminder() {
        val deadline = Instant.parse("2026-09-07T12:30:00Z").toEpochMilliseconds()
        val beforePermission = assertNotNull(createBusReminderTrigger(deadline, deadline - 120_000))
        val afterPermission = assertNotNull(createBusReminderTrigger(deadline, deadline - 30_000))

        assertEquals(beforePermission.dateComponents, afterPermission.dateComponents)
        assertEquals(2026L, afterPermission.dateComponents.year)
        assertEquals(9L, afterPermission.dateComponents.month)
        assertEquals(7L, afterPermission.dateComponents.day)
        assertEquals(12L, afterPermission.dateComponents.hour)
        assertEquals(30L, afterPermission.dateComponents.minute)
        assertEquals(0L, afterPermission.dateComponents.second)
        assertFalse(afterPermission.repeats)
    }

    @Test
    fun permissionGrantedAfterDeadlineDoesNotScheduleALateReminder() {
        assertNull(createBusReminderTrigger(triggerAtEpochMillis = 60_000, nowEpochMillis = 60_000))
        assertNull(createBusReminderTrigger(triggerAtEpochMillis = 60_000, nowEpochMillis = 90_000))
    }
}
