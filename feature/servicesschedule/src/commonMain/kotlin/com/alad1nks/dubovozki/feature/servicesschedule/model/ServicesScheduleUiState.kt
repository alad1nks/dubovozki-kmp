package com.alad1nks.dubovozki.feature.servicesschedule.model

internal sealed interface ServicesScheduleUiState {
    data object Loading : ServicesScheduleUiState

    data class Error(
        val message: String?,
    ) : ServicesScheduleUiState

    data class Content(
        val firstBuildingSchedule: List<ServicesScheduleItemUi>,
        val secondBuildingSchedule: List<ServicesScheduleItemUi>,
        val thirdBuildingSchedule: List<ServicesScheduleItemUi>,
        val updatedAtEpochMillis: Long?,
        val isStale: Boolean,
    ) : ServicesScheduleUiState
}
