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
                is Data.Success ->
                    Data.Success(
                        value =
                            data.value.filterBusSchedule(
                                stationFilter = stationFilter,
                                dayOfWeekFilter = dayOfWeekFilter,
                                currentDayOfWeek = getMoscowDayOfWeek(),
                            ),
                        updatedAtEpochMillis = data.updatedAtEpochMillis,
                        isStale = data.isStale,
                    )
            }
        }
    }

    override fun refresh() = busScheduleRepository.refresh()
}

internal fun List<Bus>.filterBusSchedule(
    stationFilter: StationFilter,
    dayOfWeekFilter: DayOfWeekFilter,
    currentDayOfWeek: DayOfWeek,
): BusSchedule {
    val allowedStations =
        when (stationFilter) {
            StationFilter.ALL -> Bus.Station.entries
            StationFilter.ODINTSOVO -> listOf(Bus.Station.ODINTSOVO)
            StationFilter.SLAVYANSKY_BULVAR -> listOf(Bus.Station.SLAVYANSKY_BULVAR)
            StationFilter.MOLODYOZHNAYA -> listOf(Bus.Station.MOLODYOZHNAYA)
        }
    val allowedDay =
        when (dayOfWeekFilter) {
            DayOfWeekFilter.TODAY -> currentDayOfWeek.toBusDayOfWeek()
            DayOfWeekFilter.TOMORROW -> currentDayOfWeek.tomorrow().toBusDayOfWeek()
            DayOfWeekFilter.WEEKDAYS -> Bus.DayOfWeek.WEEKDAYS
            DayOfWeekFilter.SATURDAY -> Bus.DayOfWeek.SATURDAY
            DayOfWeekFilter.SUNDAY -> Bus.DayOfWeek.SUNDAY
        }
    val filteredBusList =
        filter { bus ->
            bus.station in allowedStations && bus.dayOfWeek == allowedDay
        }

    return BusSchedule(
        toMoscow = filteredBusList.filter { it.direction == Bus.Direction.MOSCOW },
        toDubki = filteredBusList.filter { it.direction == Bus.Direction.DUBKI },
    )
}

private fun DayOfWeek.toBusDayOfWeek(): Bus.DayOfWeek =
    when (this) {
        DayOfWeek.MONDAY -> Bus.DayOfWeek.MONDAY
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        -> Bus.DayOfWeek.WEEKDAYS
        DayOfWeek.SATURDAY -> Bus.DayOfWeek.SATURDAY
        DayOfWeek.SUNDAY -> Bus.DayOfWeek.SUNDAY
    }

private fun DayOfWeek.tomorrow(): DayOfWeek =
    when (this) {
        DayOfWeek.MONDAY -> DayOfWeek.TUESDAY
        DayOfWeek.TUESDAY -> DayOfWeek.WEDNESDAY
        DayOfWeek.WEDNESDAY -> DayOfWeek.THURSDAY
        DayOfWeek.THURSDAY -> DayOfWeek.FRIDAY
        DayOfWeek.FRIDAY -> DayOfWeek.SATURDAY
        DayOfWeek.SATURDAY -> DayOfWeek.SUNDAY
        DayOfWeek.SUNDAY -> DayOfWeek.MONDAY
    }
