package com.alad1nks.dubovozki.e2e

import com.alad1nks.dubovozki.core.domain.MoscowTimeProvider
import com.alad1nks.dubovozki.core.firebase.BusScheduleApi
import com.alad1nks.dubovozki.core.firebase.ServicesApi
import com.alad1nks.dubovozki.core.firebase.ServicesScheduleApi
import com.alad1nks.dubovozki.core.firebase.model.BusScheduleResponse
import com.alad1nks.dubovozki.core.firebase.model.ServicesResponse
import com.alad1nks.dubovozki.core.firebase.model.ServicesScheduleResponse
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.storage.common.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime

internal class E2ETestDriver(
    initialBus: Data<BusScheduleResponse> = Data.Success(E2EFixtures.happyBusSchedule),
    initialServices: Data<ServicesResponse> = Data.Success(E2EFixtures.happyServices),
    initialServiceSchedule: Data<ServicesScheduleResponse> = Data.Success(E2EFixtures.happyServiceSchedule),
) {
    val busApi = FakeBusScheduleApi(initialBus)
    val servicesApi = FakeServicesApi(initialServices)
    val serviceScheduleApi = FakeServicesScheduleApi(initialServiceSchedule)
    val preferences = InMemoryAppPreferences()
    val time = MutableMoscowTimeProvider(E2EClockFixtures.beforeDeparture)
}

internal class FakeBusScheduleApi(initial: Data<BusScheduleResponse>) : BusScheduleApi {
    private val state = MutableStateFlow(initial)
    private val refreshStates = ArrayDeque<Data<BusScheduleResponse>>()
    var refreshCount = 0
        private set

    override fun getBusSchedule() = state

    override fun refresh() {
        refreshCount += 1
        refreshStates.removeFirstOrNull()?.let { state.value = it }
    }

    fun emit(value: Data<BusScheduleResponse>) {
        state.value = value
    }

    fun onRefresh(vararg values: Data<BusScheduleResponse>) {
        refreshStates.addAll(values)
    }
}

internal class FakeServicesApi(initial: Data<ServicesResponse>) : ServicesApi {
    private val state = MutableStateFlow(initial)
    private val refreshStates = ArrayDeque<Data<ServicesResponse>>()

    override fun getServices() = state

    override fun refresh() {
        refreshStates.removeFirstOrNull()?.let { state.value = it }
    }

    fun emit(value: Data<ServicesResponse>) {
        state.value = value
    }

    fun onRefresh(vararg values: Data<ServicesResponse>) {
        refreshStates.addAll(values)
    }
}

internal class FakeServicesScheduleApi(initial: Data<ServicesScheduleResponse>) : ServicesScheduleApi {
    private val state = MutableStateFlow(initial)
    private val refreshStates = ArrayDeque<Data<ServicesScheduleResponse>>()

    override fun getLinenRoomSchedule() = state

    override fun refresh() {
        refreshStates.removeFirstOrNull()?.let { state.value = it }
    }

    fun emit(value: Data<ServicesScheduleResponse>) {
        state.value = value
    }

    fun onRefresh(vararg values: Data<ServicesScheduleResponse>) {
        refreshStates.addAll(values)
    }
}

internal class InMemoryAppPreferences : AppPreferences {
    private val strings = mutableMapOf<String, MutableStateFlow<String?>>()
    private val booleans = mutableMapOf<String, MutableStateFlow<Boolean?>>()

    override fun getString(key: String): Flow<String?> = strings.getOrPut(key) { MutableStateFlow(null) }

    override fun getBoolean(key: String): Flow<Boolean?> = booleans.getOrPut(key) { MutableStateFlow(null) }

    override suspend fun setString(key: String, value: String) {
        strings.getOrPut(key) { MutableStateFlow(null) }.value = value
    }

    override suspend fun setBoolean(key: String, value: Boolean) {
        booleans.getOrPut(key) { MutableStateFlow(null) }.value = value
    }

    fun clear() {
        strings.values.forEach { it.value = null }
        booleans.values.forEach { it.value = null }
    }

    fun seedString(
        key: String,
        value: String,
    ) {
        strings.getOrPut(key) { MutableStateFlow(null) }.value = value
    }

    fun stringValue(key: String): String? = strings[key]?.value
}

internal class MutableMoscowTimeProvider(initial: LocalDateTime) : MoscowTimeProvider {
    private val current = MutableStateFlow(initial)

    override fun now(): LocalDateTime = current.value

    fun set(value: LocalDateTime) {
        current.value = value
    }
}

internal object E2EClockFixtures {
    val beforeDeparture = LocalDateTime(2026, 8, 31, 8, 55)
    val atDeparture = LocalDateTime(2026, 8, 31, 9, 0)
    val afterDeparture = LocalDateTime(2026, 8, 31, 9, 1)
    val afterLastDeparture = LocalDateTime(2026, 8, 31, 23, 30)
    val sundayBeforeMidnight = LocalDateTime(2026, 9, 6, 23, 59)
    val mondayAfterMidnight = LocalDateTime(2026, 9, 7, 0, 1)
}

internal object E2EFixtures {
    val happyBusSchedule =
        BusScheduleResponse(
            revision = "happy-v1",
            busList =
                listOf(
                    bus(1, 2, 9, 0, "msk", "odn"),
                    bus(2, 2, 9, 10, "dbk", "odn"),
                    bus(3, 2, 10, 0, "msk", "slv"),
                    bus(4, 2, 10, 10, "dbk", "mld"),
                    bus(5, 3, 9, 0, "msk", "odn"),
                    bus(6, 3, 9, 10, "dbk", "slv"),
                    bus(7, 7, 11, 0, "msk", "mld"),
                    bus(8, 7, 11, 10, "dbk", "mld"),
                    bus(9, 1, 12, 0, "msk", "odn"),
                    bus(10, 1, 12, 10, "dbk", "odn"),
                ),
        )

    val partialInvalidBusSchedule =
        BusScheduleResponse(
            revision = "partial-invalid-v1",
            busList =
                listOf(
                    bus(101, 2, 9, 0, "msk", "odn"),
                    BusScheduleResponse.Bus(dayOfWeek = 2, dayTime = 1, direction = "msk", station = "odn"),
                    BusScheduleResponse.Bus(id = 102, dayOfWeek = 2, direction = "msk", station = "odn"),
                    BusScheduleResponse.Bus(103, 2, 1, "00:00", "bad", "odn"),
                ),
        )

    val emptyBusSchedule = BusScheduleResponse(revision = "empty-v1", busList = emptyList())

    val happyServices =
        ServicesResponse(
            contactLink = "https://t.me/dubki_contact",
            donutLink = "https://example.test/donate",
        )
    val emptyServices = ServicesResponse()

    val happyServiceSchedule =
        ServicesScheduleResponse(
            firstBuilding = listOf(schedule(1, "10:00–12:00"), schedule(3, "18:00–20:00")),
            secondBuilding = listOf(schedule(2, "11:00–13:00")),
            thirdBuilding = listOf(schedule(5, "17:00–19:00")),
        )

    val oneEmptyBuildingServiceSchedule =
        happyServiceSchedule.copy(secondBuilding = emptyList())

    private fun bus(
        id: Int,
        day: Int,
        hour: Int,
        minute: Int,
        direction: String,
        station: String,
    ) = BusScheduleResponse.Bus(
        id = id,
        dayOfWeek = day,
        dayTime = (hour * 60 + minute) * 60_000,
        dayTimeString = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}",
        direction = direction,
        station = station,
    )

    private fun schedule(day: Int, time: String) =
        ServicesScheduleResponse.ServiceScheduleItemResponse(day, time)
}
