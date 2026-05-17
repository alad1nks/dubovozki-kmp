package com.alad1nks.dubovozki.core.data.repository

import com.alad1nks.dubovozki.core.firebase.ServicesScheduleApi
import com.alad1nks.dubovozki.core.firebase.model.ServicesScheduleResponse
import com.alad1nks.dubovozki.core.firebase.model.ServicesScheduleResponse.ServiceScheduleItemResponse
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.ServicesSchedule
import com.alad1nks.dubovozki.core.model.ServicesScheduleItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServicesScheduleRepository(
    private val servicesScheduleApi: ServicesScheduleApi,
) {
    fun getLinenRoomSchedule(): Flow<Data<ServicesSchedule>> {
        return servicesScheduleApi.getLinenRoomSchedule()
            .map { data ->
                when (data) {
                    is Data.Initial -> Data.Initial()
                    is Data.Error -> Data.Error(data.message)
                    is Data.Success -> Data.Success(data.value.toDomainServicesSchedule())
                }
            }
    }

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
            val day = serviceScheduleItem.day ?: return@mapNotNull null
            val time = serviceScheduleItem.time ?: return@mapNotNull null

            ServicesScheduleItem(
                day = day,
                time = time,
            )
        }
    }
}
