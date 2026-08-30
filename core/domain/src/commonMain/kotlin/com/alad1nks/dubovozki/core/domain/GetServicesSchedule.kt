package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.ServicesSchedule
import com.alad1nks.dubovozki.core.model.ServicesScheduleType
import kotlinx.coroutines.flow.Flow

interface GetServicesSchedule {
    operator fun invoke(servicesScheduleType: ServicesScheduleType): Flow<Data<ServicesSchedule>>

    fun refresh(servicesScheduleType: ServicesScheduleType)
}
