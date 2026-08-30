package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.data.repository.ServicesScheduleRepository
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.ServicesSchedule
import com.alad1nks.dubovozki.core.model.ServicesScheduleType
import kotlinx.coroutines.flow.Flow

internal class GetServicesScheduleImpl(
    private val servicesScheduleRepository: ServicesScheduleRepository,
) : GetServicesSchedule {
    override fun invoke(servicesScheduleType: ServicesScheduleType): Flow<Data<ServicesSchedule>> {
        return when (servicesScheduleType) {
            ServicesScheduleType.LINEN_ROOM -> servicesScheduleRepository.getLinenRoomSchedule()
        }
    }

    override fun refresh(servicesScheduleType: ServicesScheduleType) {
        when (servicesScheduleType) {
            ServicesScheduleType.LINEN_ROOM -> servicesScheduleRepository.refresh()
        }
    }
}
