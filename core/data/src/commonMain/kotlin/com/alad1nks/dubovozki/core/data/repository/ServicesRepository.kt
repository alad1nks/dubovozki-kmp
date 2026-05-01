package com.alad1nks.dubovozki.core.data.repository

import com.alad1nks.dubovozki.core.firebase.ServicesApi
import com.alad1nks.dubovozki.core.firebase.model.ServicesResponse
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.Services
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ServicesRepository(
    private val servicesApi: ServicesApi,
) {
    fun getServices(): Flow<Data<Services>> {
        return servicesApi.getServices()
            .map { data ->
                when (data) {
                    is Data.Initial -> Data.Initial()
                    is Data.Error -> Data.Error(data.message)
                    is Data.Success -> Data.Success(data.value.toDomainServices())
                }
            }
    }

    private fun ServicesResponse.toDomainServices(): Services {
        return Services(
            contactLink = contactLink ?: "",
            donutLink = donutLink ?: "",
        )
    }
}
