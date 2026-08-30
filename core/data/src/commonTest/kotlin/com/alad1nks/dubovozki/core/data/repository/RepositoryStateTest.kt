package com.alad1nks.dubovozki.core.data.repository

import com.alad1nks.dubovozki.core.firebase.BusScheduleApi
import com.alad1nks.dubovozki.core.firebase.ServicesApi
import com.alad1nks.dubovozki.core.firebase.model.BusScheduleResponse
import com.alad1nks.dubovozki.core.firebase.model.ServicesResponse
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.ThemeMode
import com.alad1nks.dubovozki.core.storage.common.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RepositoryStateTest {
    @Test
    fun networkErrorWithoutCacheRemainsAnError() =
        runTest {
            val repository =
                BusScheduleRepository(
                    busScheduleApi = FakeBusScheduleApi(Data.Error("offline")),
                    storage = FakeStorage(),
                )

            assertIs<Data.Error<List<com.alad1nks.dubovozki.core.model.Bus>>>(repository.getBusList().first())
        }

    @Test
    fun cachedScheduleIsReturnedAsStaleWhileOffline() =
        runTest {
            val response = BusScheduleResponse(busList = listOf(validBusResponse()))
            val storage =
                FakeStorage(
                    busScheduleCache = CacheEntry(response, updatedAtEpochMillis = 123L).encode(),
                )
            val repository =
                BusScheduleRepository(
                    busScheduleApi = FakeBusScheduleApi(Data.Error("offline")),
                    storage = storage,
                )

            val result =
                assertIs<Data.Success<List<com.alad1nks.dubovozki.core.model.Bus>>>(repository.getBusList().first())

            assertTrue(result.isStale)
            assertEquals(123L, result.updatedAtEpochMillis)
            assertEquals(1, result.value.size)
        }

    @Test
    fun successfulEmptyResponseIsContentRatherThanAnError() =
        runTest {
            val repository =
                BusScheduleRepository(
                    busScheduleApi = FakeBusScheduleApi(Data.Success(BusScheduleResponse(busList = emptyList()))),
                    storage = FakeStorage(),
                )

            val result =
                assertIs<Data.Success<List<com.alad1nks.dubovozki.core.model.Bus>>>(repository.getBusList().first())

            assertTrue(result.value.isEmpty())
            assertFalse(result.isStale)
        }

    @Test
    fun malformedExternalLinksAreNotExposedToTheUi() =
        runTest {
            val repository =
                ServicesRepository(
                    servicesApi =
                        FakeServicesApi(
                            Data.Success(
                                ServicesResponse(
                                    contactLink = "javascript:alert(1)",
                                    donutLink = "https://example.com/donate",
                                ),
                            ),
                        ),
                    storage = FakeStorage(),
                )

            val services =
                assertIs<Data.Success<com.alad1nks.dubovozki.core.model.Services>>(repository.getServices().first())

            assertNull(services.value.contactLink)
            assertEquals("https://example.com/donate", services.value.donutLink)
        }

    @Test
    fun legacyThemePreferenceIsMigratedInMemory() =
        runTest {
            val repository = SettingsRepository(FakeStorage(legacyDarkTheme = true))

            assertEquals(ThemeMode.DARK, repository.getThemeMode().first())
        }

    @Test
    fun newThemePreferenceTakesPriorityOverLegacyValue() =
        runTest {
            val repository =
                SettingsRepository(
                    FakeStorage(
                        themeModeCode = ThemeMode.SYSTEM.code,
                        legacyDarkTheme = true,
                    ),
                )

            assertEquals(ThemeMode.SYSTEM, repository.getThemeMode().first())
        }

    private fun validBusResponse() =
        BusScheduleResponse.Bus(
            id = 1,
            dayOfWeek = 3,
            dayTime = 28_800_000,
            dayTimeString = "08:00",
            direction = "msk",
            station = "odn",
        )
}

private class FakeBusScheduleApi(initial: Data<BusScheduleResponse>) : BusScheduleApi {
    private val state = MutableStateFlow(initial)

    override fun getBusSchedule(): StateFlow<Data<BusScheduleResponse>> = state

    override fun refresh() = Unit
}

private class FakeServicesApi(initial: Data<ServicesResponse>) : ServicesApi {
    private val state = MutableStateFlow(initial)

    override fun getServices(): StateFlow<Data<ServicesResponse>> = state

    override fun refresh() = Unit
}

private class FakeStorage(
    private var busScheduleCache: String? = null,
    private var servicesCache: String? = null,
    private var servicesScheduleCache: String? = null,
    private var themeModeCode: String? = null,
    private var legacyDarkTheme: Boolean? = null,
    private var languageCode: String? = null,
) : Storage {
    override fun getDarkTheme(): Flow<Boolean?> = flowOf(legacyDarkTheme)

    override fun getLanguageCode(): Flow<String?> = flowOf(languageCode)

    override fun getThemeModeCode(): Flow<String?> = flowOf(themeModeCode)

    override fun getBusScheduleCache(): Flow<String?> = flowOf(busScheduleCache)

    override fun getServicesCache(): Flow<String?> = flowOf(servicesCache)

    override fun getServicesScheduleCache(): Flow<String?> = flowOf(servicesScheduleCache)

    override suspend fun setLanguageCode(value: String) {
        languageCode = value
    }

    override suspend fun setThemeModeCode(value: String) {
        themeModeCode = value
    }

    override suspend fun setBusScheduleCache(value: String) {
        busScheduleCache = value
    }

    override suspend fun setServicesCache(value: String) {
        servicesCache = value
    }

    override suspend fun setServicesScheduleCache(value: String) {
        servicesScheduleCache = value
    }
}
