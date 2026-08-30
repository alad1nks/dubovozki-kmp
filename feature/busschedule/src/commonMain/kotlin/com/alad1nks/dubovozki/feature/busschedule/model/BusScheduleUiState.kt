package com.alad1nks.dubovozki.feature.busschedule.model

internal sealed interface BusScheduleUiState {
    data object Loading : BusScheduleUiState

    data class Error(
        val message: String?,
    ) : BusScheduleUiState

    data class Content(
        val moscowBusList: List<BusUi>,
        val dubkiBusList: List<BusUi>,
        val firstMoscowBusIndex: Int?,
        val firstDubkiBusIndex: Int?,
        val updatedAtEpochMillis: Long?,
        val isStale: Boolean,
    ) : BusScheduleUiState
}
