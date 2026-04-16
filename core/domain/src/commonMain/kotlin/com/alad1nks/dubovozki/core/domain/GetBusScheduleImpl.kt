package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.data.repository.BusScheduleRepository
import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.core.model.BusSchedule
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.DayOfWeekFilter
import com.alad1nks.dubovozki.core.model.StationFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek

internal class GetBusScheduleImpl(
    private val busScheduleRepository: BusScheduleRepository,
    private val getMoscowDayOfWeek: GetMoscowDayOfWeek,
) : GetBusSchedule {
    override operator fun invoke(
        stationFilter: StationFilter,
        dayOfWeekFilter: DayOfWeekFilter,
    ): Flow<Data<BusSchedule>> {
        return busScheduleRepository.getBusList().map { data ->
            when (data) {
                is Data.Initial -> Data.Initial()
                is Data.Error -> Data.Error(data.message)
                is Data.Success -> Data.Success(data.value.filter(stationFilter, dayOfWeekFilter))
            }
        }
    }

    private fun List<Bus>.filter(
        stationFilter: StationFilter,
        dayOfWeekFilter: DayOfWeekFilter,
    ): BusSchedule {
        val allowedStations =
            when (stationFilter) {
                StationFilter.ALL -> Bus.Station.entries
                StationFilter.ODINTSOVO -> listOf(Bus.Station.ODINTSOVO)
                StationFilter.SLAVYANKA -> listOf(Bus.Station.SLAVYANKA)
                StationFilter.MOLODYOZHKA -> listOf(Bus.Station.MOLODYOZHKA)
            }

        val allowedDay =
            when (dayOfWeekFilter) {
                DayOfWeekFilter.TODAY -> getMoscowDayOfWeek().toBusDayOfWeek()
                DayOfWeekFilter.TOMORROW -> getMoscowDayOfWeek().tomorrow().toBusDayOfWeek()
                DayOfWeekFilter.WEEKDAY -> Bus.DayOfWeek.WEEKDAY
                DayOfWeekFilter.SATURDAY -> Bus.DayOfWeek.SATURDAY
                DayOfWeekFilter.SUNDAY -> Bus.DayOfWeek.SUNDAY
            }

        val filteredBusList =
            filter { bus ->
                if (bus.station !in allowedStations) return@filter false

                if (bus.dayOfWeek != allowedDay) return@filter false

                true
            }

        return BusSchedule(
            toMoscow = filteredBusList.filter { it.direction == Bus.Direction.MOSCOW },
            toDubki = filteredBusList.filter { it.direction == Bus.Direction.DUBKI },
        )
    }

    private fun DayOfWeek.toBusDayOfWeek(): Bus.DayOfWeek {
        return when (this) {
            DayOfWeek.MONDAY -> Bus.DayOfWeek.MONDAY
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            -> Bus.DayOfWeek.WEEKDAY
            DayOfWeek.SATURDAY -> Bus.DayOfWeek.SATURDAY
            DayOfWeek.SUNDAY -> Bus.DayOfWeek.SUNDAY
        }
    }

    private fun DayOfWeek.tomorrow(): DayOfWeek {
        return when (this) {
            DayOfWeek.MONDAY -> DayOfWeek.TUESDAY
            DayOfWeek.TUESDAY -> DayOfWeek.WEDNESDAY
            DayOfWeek.WEDNESDAY -> DayOfWeek.THURSDAY
            DayOfWeek.THURSDAY -> DayOfWeek.FRIDAY
            DayOfWeek.FRIDAY -> DayOfWeek.SATURDAY
            DayOfWeek.SATURDAY -> DayOfWeek.SUNDAY
            DayOfWeek.SUNDAY -> DayOfWeek.MONDAY
        }
    }
}
