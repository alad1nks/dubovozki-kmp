package com.alad1nks.dubovozki.core.data.repository

import com.alad1nks.dubovozki.core.firebase.ServicesScheduleApi
import com.alad1nks.dubovozki.core.firebase.model.ServicesScheduleResponse
import com.alad1nks.dubovozki.core.firebase.model.ServicesScheduleResponse.ServiceScheduleItemResponse
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.ServicesSchedule
import com.alad1nks.dubovozki.core.model.ServicesScheduleItem
import com.alad1nks.dubovozki.core.storage.common.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ServicesScheduleRepository(
    private val servicesScheduleApi: ServicesScheduleApi,
    private val storage: Storage,
) {
    fun getLinenRoomSchedule(): Flow<Data<ServicesSchedule>> {
        return flow {
            var cached =
                storage.getServicesScheduleCache().first()?.decodeCacheEntry<ServicesScheduleResponse>()
            cached?.let {
                emit(Data.Success(it.value.toDomainServicesSchedule(), it.updatedAtEpochMillis))
            }

            servicesScheduleApi.getLinenRoomSchedule().collect { data ->
                when (data) {
                    is Data.Initial -> if (cached == null) emit(Data.Initial())
                    is Data.Error -> {
                        val cachedValue = cached
                        if (cachedValue == null) {
                            emit(Data.Error(data.message))
                        } else {
                            emit(
                                Data.Success(
                                    cachedValue.value.toDomainServicesSchedule(),
                                    cachedValue.updatedAtEpochMillis,
                                    isStale = true,
                                ),
                            )
                        }
                    }
                    is Data.Success -> {
                        val entry = CacheEntry(data.value, Clock.System.now().toEpochMilliseconds())
                        cached = entry
                        runCatching { storage.setServicesScheduleCache(entry.encode()) }
                        emit(Data.Success(data.value.toDomainServicesSchedule(), entry.updatedAtEpochMillis))
                    }
                }
            }
        }
    }

    fun refresh() = servicesScheduleApi.refresh()

    private fun ServicesScheduleResponse.toDomainServicesSchedule(): ServicesSchedule {
        return ServicesSchedule(
            firstBuildingSchedule = firstBuilding.toDomainServiceScheduleItemList(),
            secondBuildingSchedule = secondBuilding.toDomainServiceScheduleItemList(),
            thirdBuildingSchedule = thirdBuilding.toDomainServiceScheduleItemList(),
        )
    }

    private fun List<ServiceScheduleItemResponse>?.toDomainServiceScheduleItemList(): List<ServicesScheduleItem> {
        if (this == null) return emptyList()

        return mapNotNull { serviceScheduleItem ->
            val day = serviceScheduleItem.day?.takeIf { it in 1..7 } ?: return@mapNotNull null
            val time = serviceScheduleItem.time?.takeIf { it.isNotBlank() } ?: return@mapNotNull null

            ServicesScheduleItem(
                day = day,
                time = time,
            )
        }.distinct()
    }
}
