package com.alad1nks.dubovozki.core.data.repository

import com.alad1nks.dubovozki.core.firebase.ServicesApi
import com.alad1nks.dubovozki.core.firebase.model.ServicesResponse
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.Services
import com.alad1nks.dubovozki.core.storage.common.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ServicesRepository(
    private val servicesApi: ServicesApi,
    private val storage: Storage,
) {
    fun getServices(): Flow<Data<Services>> {
        return flow {
            var cached = storage.getServicesCache().first()?.decodeCacheEntry<ServicesResponse>()
            cached?.let {
                emit(Data.Success(it.value.toDomainServices(), it.updatedAtEpochMillis, isStale = true))
            }

            servicesApi.getServices().collect { data ->
                when (data) {
                    is Data.Initial -> if (cached == null) emit(Data.Initial())
                    is Data.Error -> {
                        val cachedValue = cached
                        if (cachedValue == null) {
                            emit(Data.Error(data.message))
                        } else {
                            emit(
                                Data.Success(
                                    cachedValue.value.toDomainServices(),
                                    cachedValue.updatedAtEpochMillis,
                                    isStale = true,
                                ),
                            )
                        }
                    }
                    is Data.Success -> {
                        val entry = CacheEntry(data.value, Clock.System.now().toEpochMilliseconds())
                        cached = entry
                        runCatching { storage.setServicesCache(entry.encode()) }
                        emit(Data.Success(data.value.toDomainServices(), entry.updatedAtEpochMillis))
                    }
                }
            }
        }
    }

    fun refresh() = servicesApi.refresh()

    private fun ServicesResponse.toDomainServices(): Services {
        return Services(
            contactLink = contactLink?.takeIf { it.isSupportedUri() },
            donutLink = donutLink?.takeIf { it.isSupportedUri() },
        )
    }

    private fun String.isSupportedUri(): Boolean =
        startsWith("https://") || startsWith("http://") || startsWith("mailto:") || startsWith("tg:")
}
