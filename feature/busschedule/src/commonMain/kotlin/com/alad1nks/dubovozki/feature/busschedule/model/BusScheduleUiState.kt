package com.alad1nks.dubovozki.feature.busschedule.model

sealed interface BusScheduleUiState {
    object Loading : BusScheduleUiState

    data class Content(
        val moscowBusList: List<BusUi>,
        val dubkiBusList: List<BusUi>,
        val showError: Boolean,
    ) : BusScheduleUiState
}
