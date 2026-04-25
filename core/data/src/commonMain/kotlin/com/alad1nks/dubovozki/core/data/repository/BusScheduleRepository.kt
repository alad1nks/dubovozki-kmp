package com.alad1nks.dubovozki.core.data.repository

import com.alad1nks.dubovozki.core.firebase.BusScheduleApi
import com.alad1nks.dubovozki.core.firebase.model.BusScheduleResponse
import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BusScheduleRepository(
    private val busScheduleApi: BusScheduleApi,
) {
    fun getBusList(): Flow<Data<List<Bus>>> {
        return busScheduleApi.getBusSchedule()
            .map { data ->
                when (data) {
                    is Data.Initial -> Data.Initial()
                    is Data.Error -> Data.Error(data.message)
                    is Data.Success -> Data.Success(data.value.toDomainBusList())
                }
            }
    }

    private fun BusScheduleResponse.toDomainBusList(): List<Bus> {
        return busList?.mapNotNull { bus ->
            val id = bus.id ?: return@mapNotNull null
            val dayOfWeek = bus.dayOfWeek?.toDomainDayOfWeek() ?: return@mapNotNull null
            val dayTime = bus.dayTime ?: return@mapNotNull null
            val dayTimeString = bus.dayTimeString ?: return@mapNotNull null
            val station = bus.station?.toDomainStation() ?: return@mapNotNull null
            val direction = bus.direction?.toDomainDirection() ?: return@mapNotNull null

            Bus(
                id = id,
                dayOfWeek = dayOfWeek,
                dayTime = dayTime,
                dayTimeString = dayTimeString,
                station = station,
                direction = direction,
            )
        } ?: emptyList()
    }

    private fun Int.toDomainDayOfWeek(): Bus.DayOfWeek? {
        return when (this) {
            2 -> Bus.DayOfWeek.MONDAY
            3 -> Bus.DayOfWeek.WEEKDAYS
            7 -> Bus.DayOfWeek.SATURDAY
            1 -> Bus.DayOfWeek.SUNDAY
            else -> null
        }
    }

    private fun String.toDomainStation(): Bus.Station? {
        return when (this) {
            "odn" -> Bus.Station.ODINTSOVO
            "slv" -> Bus.Station.SLAVYANSKY_BULVAR
            "mld" -> Bus.Station.MOLODYOZHNAYA
            else -> null
        }
    }

    private fun String.toDomainDirection(): Bus.Direction? {
        return when (this) {
            "msk" -> Bus.Direction.MOSCOW
            "dbk" -> Bus.Direction.DUBKI
            else -> null
        }
    }
}
