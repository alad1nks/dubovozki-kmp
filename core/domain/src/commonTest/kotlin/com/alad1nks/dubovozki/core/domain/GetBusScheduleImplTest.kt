package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.core.model.DayOfWeekFilter
import com.alad1nks.dubovozki.core.model.StationFilter
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetBusScheduleImplTest {
    @Test
    fun tomorrowFromSundayUsesMondaySchedule() {
        val mondayToMoscow = bus(id = 1, day = Bus.DayOfWeek.MONDAY, direction = Bus.Direction.MOSCOW)
        val weekdayToDubki = bus(id = 2, day = Bus.DayOfWeek.WEEKDAYS, direction = Bus.Direction.DUBKI)

        val result =
            listOf(mondayToMoscow, weekdayToDubki).filterBusSchedule(
                stationFilter = StationFilter.ALL,
                dayOfWeekFilter = DayOfWeekFilter.TOMORROW,
                currentDayOfWeek = DayOfWeek.SUNDAY,
            )

        assertEquals(listOf(mondayToMoscow), result.toMoscow)
        assertTrue(result.toDubki.isEmpty())
    }

    @Test
    fun stationAndDirectionFiltersAreAppliedTogether() {
        val odintsovoToMoscow = bus(id = 1, station = Bus.Station.ODINTSOVO)
        val slavyanskyToMoscow = bus(id = 2, station = Bus.Station.SLAVYANSKY_BULVAR)
        val odintsovoToDubki = bus(id = 3, station = Bus.Station.ODINTSOVO, direction = Bus.Direction.DUBKI)

        val result =
            listOf(odintsovoToMoscow, slavyanskyToMoscow, odintsovoToDubki).filterBusSchedule(
                stationFilter = StationFilter.ODINTSOVO,
                dayOfWeekFilter = DayOfWeekFilter.WEEKDAYS,
                currentDayOfWeek = DayOfWeek.TUESDAY,
            )

        assertEquals(listOf(odintsovoToMoscow), result.toMoscow)
        assertEquals(listOf(odintsovoToDubki), result.toDubki)
    }

    @Test
    fun unmatchedFilterReturnsEmptySchedules() {
        val result =
            listOf(bus(id = 1)).filterBusSchedule(
                stationFilter = StationFilter.MOLODYOZHNAYA,
                dayOfWeekFilter = DayOfWeekFilter.SATURDAY,
                currentDayOfWeek = DayOfWeek.SATURDAY,
            )

        assertTrue(result.toMoscow.isEmpty())
        assertTrue(result.toDubki.isEmpty())
    }

    private fun bus(
        id: Int,
        day: Bus.DayOfWeek = Bus.DayOfWeek.WEEKDAYS,
        station: Bus.Station = Bus.Station.ODINTSOVO,
        direction: Bus.Direction = Bus.Direction.MOSCOW,
    ) =
        Bus(
            id = id,
            dayOfWeek = day,
            dayTime = 8 * 60 * 60 * 1000,
            dayTimeString = "08:00",
            station = station,
            direction = direction,
        )
}
