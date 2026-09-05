package com.alad1nks.dubovozki.core.data.repository

import com.alad1nks.dubovozki.core.firebase.BusScheduleApi
import com.alad1nks.dubovozki.core.firebase.ServicesApi
import com.alad1nks.dubovozki.core.firebase.ServicesScheduleApi
import com.alad1nks.dubovozki.core.firebase.model.BusScheduleResponse
import com.alad1nks.dubovozki.core.firebase.model.ServicesResponse
import com.alad1nks.dubovozki.core.firebase.model.ServicesScheduleResponse
import com.alad1nks.dubovozki.core.firebase.model.ServicesScheduleResponse.ServiceScheduleItemResponse
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.ServicesSchedule
import com.alad1nks.dubovozki.core.model.ServicesScheduleItem
import com.alad1nks.dubovozki.core.model.ThemeMode
import com.alad1nks.dubovozki.core.storage.common.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
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
    fun cachedScheduleIsOnlyMarkedStaleAfterNetworkFailure() =
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

            val results = repository.getBusList().take(2).toList()
            val validatingCache =
                assertIs<Data.Success<List<com.alad1nks.dubovozki.core.model.Bus>>>(results.first())
            val offlineCache =
                assertIs<Data.Success<List<com.alad1nks.dubovozki.core.model.Bus>>>(results.last())

            assertFalse(validatingCache.isStale)
            assertTrue(offlineCache.isStale)
            assertEquals(123L, offlineCache.updatedAtEpochMillis)
            assertEquals(1, offlineCache.value.size)
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
    fun invalidAndDuplicateLinenRowsAreSkippedInEveryBuilding() =
        runTest {
            val rows =
                listOf(
                    ServiceScheduleItemResponse(1, "10:00–12:00"),
                    ServiceScheduleItemResponse(0, "invalid"),
                    ServiceScheduleItemResponse(8, "invalid"),
                    ServiceScheduleItemResponse(null, "invalid"),
                    ServiceScheduleItemResponse(2, null),
                    ServiceScheduleItemResponse(3, " "),
                    ServiceScheduleItemResponse(1, "10:00–12:00"),
                    ServiceScheduleItemResponse(7, "Closed"),
                )
            val repository =
                ServicesScheduleRepository(
                    FakeServicesScheduleApi(Data.Success(ServicesScheduleResponse(rows, rows, rows))),
                    FakeStorage(),
                )

            val result = assertIs<Data.Success<ServicesSchedule>>(repository.getLinenRoomSchedule().first())
            val expected = listOf(ServicesScheduleItem(1, "10:00–12:00"), ServicesScheduleItem(7, "Closed"))
            assertEquals(expected, result.value.firstBuildingSchedule)
            assertEquals(expected, result.value.secondBuildingSchedule)
            assertEquals(expected, result.value.thirdBuildingSchedule)
        }

    @Test
    fun invalidCachedLinenDaysDoNotReachTheUiWhenOffline() =
        runTest {
            val cached =
                ServicesScheduleResponse(
                    firstBuilding = listOf(ServiceScheduleItemResponse(-1, "invalid")),
                    secondBuilding = listOf(ServiceScheduleItemResponse(2, "11:00–13:00")),
                )
            val repository =
                ServicesScheduleRepository(
                    FakeServicesScheduleApi(Data.Error("offline")),
                    FakeStorage(servicesScheduleCache = CacheEntry(cached, 123L).encode()),
                )

            val result =
                assertIs<Data.Success<ServicesSchedule>>(repository.getLinenRoomSchedule().take(2).toList().last())
            assertTrue(result.isStale)
            assertTrue(result.value.firstBuildingSchedule.isEmpty())
            assertEquals(listOf(ServicesScheduleItem(2, "11:00–13:00")), result.value.secondBuildingSchedule)
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

private class FakeServicesScheduleApi(initial: Data<ServicesScheduleResponse>) : ServicesScheduleApi {
    private val state = MutableStateFlow(initial)

    override fun getLinenRoomSchedule(): StateFlow<Data<ServicesScheduleResponse>> = state

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
