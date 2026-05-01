package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.data.repository.ServicesRepository
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.Services
import kotlinx.coroutines.flow.Flow

internal class GetServicesImpl(
    private val servicesRepository: ServicesRepository,
) : GetServices {
    override fun invoke(): Flow<Data<Services>> {
        return servicesRepository.getServices()
    }
}
