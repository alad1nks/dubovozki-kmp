package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alad1nks.dubovozki.core.domain.GetBusSchedule
import com.alad1nks.dubovozki.core.domain.GetMoscowLocalTime
import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.DayOfWeekFilter
import com.alad1nks.dubovozki.core.model.StationFilter
import com.alad1nks.dubovozki.feature.busschedule.model.BusScheduleTopAppBarUiState
import com.alad1nks.dubovozki.feature.busschedule.model.BusScheduleUiState
import com.alad1nks.dubovozki.feature.busschedule.model.BusUi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
internal class BusScheduleViewModel(
    private val getBusSchedule: GetBusSchedule,
    private val getMoscowLocalTime: GetMoscowLocalTime,
) : ViewModel() {
    private val stationFilterSpinnerExpanded = MutableStateFlow(false)
    private val selectedStationFilter = MutableStateFlow(StationFilter.ALL)
    private val dayOfWeekFilterSpinnerExpanded = MutableStateFlow(false)
    private val selectedDayOfWeekFilter = MutableStateFlow(DayOfWeekFilter.TODAY)

    private val currentTime =
        flow {
            val startTime = getMoscowLocalTime().toMillisecondOfDay()
            emit(startTime)

            val startDelay = MINUTE - (startTime % MINUTE)
            delay(startDelay)

            while (true) {
                emit(getMoscowLocalTime().toMillisecondOfDay())
                delay(MINUTE)
            }
        }

    val topAppBarUiState: StateFlow<BusScheduleTopAppBarUiState> =
        combine(
            stationFilterSpinnerExpanded,
            selectedStationFilter,
            dayOfWeekFilterSpinnerExpanded,
            selectedDayOfWeekFilter,
        ) { stationFilterSpinnerExpanded,
            selectedStationFilter,
            dayOfWeekFilterSpinnerExpanded,
            selectedDayOfWeekFilter,
            ->
            BusScheduleTopAppBarUiState(
                stationFilterSpinnerExpanded = stationFilterSpinnerExpanded,
                selectedStationFilter = selectedStationFilter,
                dayOfWeekFilterSpinnerExpanded = dayOfWeekFilterSpinnerExpanded,
                selectedDayOfWeekFilter = selectedDayOfWeekFilter,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = BusScheduleTopAppBarUiState(),
            )

    val uiState: StateFlow<BusScheduleUiState> =
        combine(
            selectedStationFilter,
            selectedDayOfWeekFilter,
        ) { stationFilter, dayOfWeekFilter ->
            stationFilter to dayOfWeekFilter
        }
            .flatMapLatest { (stationFilter, dayOfWeekFilter) ->
                val busSchedule =
                    getBusSchedule(
                        stationFilter = stationFilter,
                        dayOfWeekFilter = dayOfWeekFilter,
                    )

                if (dayOfWeekFilter == DayOfWeekFilter.TODAY) {
                    return@flatMapLatest busSchedule.combine(currentTime) { busSchedule, currentTime ->
                        busSchedule to currentTime
                    }
                }
                busSchedule.map { it to null }
            }
            .mapLatest { (busSchedule, currentTime) ->
                when (busSchedule) {
                    is Data.Success -> {
                        val moscowBusList = busSchedule.value.toMoscow.map { it.toBusUi(currentTime) }
                        val dubkiBusList = busSchedule.value.toDubki.map { it.toBusUi(currentTime) }

                        BusScheduleUiState.Content(
                            moscowBusList = moscowBusList,
                            dubkiBusList = dubkiBusList,
                            firstMoscowBusIndex = moscowBusList.findFirstBusIndex(),
                            firstDubkiBusIndex = dubkiBusList.findFirstBusIndex(),
                            updatedAtEpochMillis = busSchedule.updatedAtEpochMillis,
                            isStale = busSchedule.isStale,
                        )
                    }
                    is Data.Initial -> BusScheduleUiState.Loading
                    is Data.Error -> BusScheduleUiState.Error(busSchedule.message)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = BusScheduleUiState.Loading,
            )

    fun selectStationFilter(station: StationFilter) {
        selectedStationFilter.value = station
        hideStationFilterSpinner()
    }

    fun expandStationFilterSpinner() {
        stationFilterSpinnerExpanded.value = true
    }

    fun hideStationFilterSpinner() {
        stationFilterSpinnerExpanded.value = false
    }

    fun selectDayOfWeekFilter(day: DayOfWeekFilter) {
        selectedDayOfWeekFilter.value = day
        hideDayOfWeekFilterSpinner()
    }

    fun expandDayOfWeekFilterSpinner() {
        dayOfWeekFilterSpinnerExpanded.value = true
    }

    fun hideDayOfWeekFilterSpinner() {
        dayOfWeekFilterSpinnerExpanded.value = false
    }

    fun refresh() {
        getBusSchedule.refresh()
    }

    fun resetFilters() {
        selectedStationFilter.value = StationFilter.ALL
        selectedDayOfWeekFilter.value = DayOfWeekFilter.TODAY
        hideStationFilterSpinner()
        hideDayOfWeekFilterSpinner()
    }

    private fun Bus.toBusUi(currentTime: Int?): BusUi {
        return BusUi(
            id = id,
            dayTime = dayTimeString,
            timeDifference = currentTime?.let { dayTime - it },
            station = station,
        )
    }

    companion object {
        private const val MINUTE = 60_000L
    }
}

internal fun List<BusUi>.findFirstBusIndex(): Int? {
    return indexOfFirst {
        val timeDifference = it.timeDifference ?: return 0
        timeDifference >= 0
    }.takeIf { it >= 0 }
}
