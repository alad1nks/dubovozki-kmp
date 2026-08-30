package com.alad1nks.dubovozki.core.data.repository

import com.alad1nks.dubovozki.core.firebase.BusScheduleApi
import com.alad1nks.dubovozki.core.firebase.model.BusScheduleResponse
import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.storage.common.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class BusScheduleRepository(
    private val busScheduleApi: BusScheduleApi,
    private val storage: Storage,
) {
    fun getBusList(): Flow<Data<List<Bus>>> {
        return flow {
            var cached = storage.getBusScheduleCache().first()?.decodeCacheEntry<BusScheduleResponse>()
            cached?.let {
                emit(
                    Data.Success(
                        value = it.value.toDomainBusList(),
                        updatedAtEpochMillis = it.updatedAtEpochMillis,
                        isStale = true,
                    ),
                )
            }

            busScheduleApi.getBusSchedule().collect { data ->
                when (data) {
                    is Data.Initial -> if (cached == null) emit(Data.Initial())
                    is Data.Error -> {
                        val cachedValue = cached
                        if (cachedValue == null) {
                            emit(Data.Error(data.message))
                        } else {
                            emit(
                                Data.Success(
                                    value = cachedValue.value.toDomainBusList(),
                                    updatedAtEpochMillis = cachedValue.updatedAtEpochMillis,
                                    isStale = true,
                                ),
                            )
                        }
                    }
                    is Data.Success -> {
                        val entry =
                            CacheEntry(
                                value = data.value,
                                updatedAtEpochMillis = Clock.System.now().toEpochMilliseconds(),
                            )
                        cached = entry
                        runCatching { storage.setBusScheduleCache(entry.encode()) }
                        emit(
                            Data.Success(
                                value = data.value.toDomainBusList(),
                                updatedAtEpochMillis = entry.updatedAtEpochMillis,
                            ),
                        )
                    }
                }
            }
        }
    }

    fun refresh() = busScheduleApi.refresh()

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
