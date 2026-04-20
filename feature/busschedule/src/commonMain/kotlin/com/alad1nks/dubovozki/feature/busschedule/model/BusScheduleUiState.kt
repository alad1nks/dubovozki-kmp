package com.alad1nks.dubovozki.feature.busschedule.model

sealed interface BusScheduleUiState {
    object Loading : BusScheduleUiState

    data class Data(
        val moscowBuses: List<BusUi>,
        val dubkiBuses: List<BusUi>,
        val showError: Boolean,
    ) : BusScheduleUiState
}
