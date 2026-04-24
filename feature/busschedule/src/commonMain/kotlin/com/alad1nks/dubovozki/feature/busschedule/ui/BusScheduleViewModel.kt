package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alad1nks.dubovozki.core.domain.GetBusSchedule
import com.alad1nks.dubovozki.core.domain.GetMoscowLocalTime
import com.alad1nks.dubovozki.core.model.Bus
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.DayOfWeekFilter
import com.alad1nks.dubovozki.core.model.StationFilter
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
    private val stationFilter = MutableStateFlow(StationFilter.ALL)
    private val dayOfWeekFilter = MutableStateFlow(DayOfWeekFilter.TODAY)

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

    val uiState: StateFlow<BusScheduleUiState> =
        combine(
            stationFilter,
            dayOfWeekFilter,
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
                        BusScheduleUiState.Content(
                            moscowBusList = busSchedule.value.toMoscow.map { it.toBusUi(currentTime) },
                            dubkiBusList = busSchedule.value.toDubki.map { it.toBusUi(currentTime) },
                            showError = false,
                        )
                    }
                    is Data.Initial,
                    is Data.Error,
                    -> BusScheduleUiState.Loading
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = BusScheduleUiState.Loading,
            )

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
