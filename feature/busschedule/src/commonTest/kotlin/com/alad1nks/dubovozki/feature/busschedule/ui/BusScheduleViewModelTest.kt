package com.alad1nks.dubovozki.feature.busschedule.ui

import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.feature.busschedule.model.BusUi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BusScheduleViewModelTest {
    @Test
    fun firstUpcomingBusIsSelected() {
        val buses = listOf(bus(1, -60_000), bus(2, 0), bus(3, 60_000))

        assertEquals(1, buses.findFirstBusIndex())
    }

    @Test
    fun noUpcomingBusReturnsNullInsteadOfStartOfDay() {
        val buses = listOf(bus(1, -120_000), bus(2, -60_000))

        assertNull(buses.findFirstBusIndex())
    }

    @Test
    fun scheduleWithoutRelativeTimesStartsAtFirstBus() {
        val buses = listOf(bus(1, null), bus(2, null))

        assertEquals(0, buses.findFirstBusIndex())
    }

    private fun bus(
        id: Int,
        timeDifference: Int?,
    ) =
        BusUi(
            id = id,
            dayTime = "08:00",
            timeDifference = timeDifference,
            station = Bus.Station.ODINTSOVO,
        )
}
