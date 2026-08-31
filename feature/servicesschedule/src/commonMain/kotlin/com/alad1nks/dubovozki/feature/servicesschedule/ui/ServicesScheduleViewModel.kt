package com.alad1nks.dubovozki.feature.servicesschedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alad1nks.dubovozki.core.domain.GetServicesSchedule
import com.alad1nks.dubovozki.core.domain.ObserveMoscowDayOfWeek
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.ServicesScheduleItem
import com.alad1nks.dubovozki.core.model.ServicesScheduleType
import com.alad1nks.dubovozki.feature.servicesschedule.model.ServicesScheduleItemUi
import com.alad1nks.dubovozki.feature.servicesschedule.model.ServicesScheduleUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

internal class ServicesScheduleViewModel(
    val servicesScheduleType: ServicesScheduleType,
    private val getServicesSchedule: GetServicesSchedule,
    private val observeMoscowDayOfWeek: ObserveMoscowDayOfWeek,
) : ViewModel() {
    val uiState: StateFlow<ServicesScheduleUiState> =
        combine(
            getServicesSchedule(servicesScheduleType),
            observeMoscowDayOfWeek(),
        ) { servicesSchedule, todayDayOfWeek ->
            when (servicesSchedule) {
                is Data.Success -> {
                    val servicesScheduleValue = servicesSchedule.value
                    val firstBuildingSchedule = servicesScheduleValue.firstBuildingSchedule
                    val secondBuildingSchedule = servicesScheduleValue.secondBuildingSchedule
                    val thirdBuildingSchedule = servicesScheduleValue.thirdBuildingSchedule

                    val todayDayOfWeekNumber = todayDayOfWeek.isoDayNumber

                    ServicesScheduleUiState.Content(
                        firstBuildingSchedule = firstBuildingSchedule.mapToUi(todayDayOfWeekNumber),
                        secondBuildingSchedule = secondBuildingSchedule.mapToUi(todayDayOfWeekNumber),
                        thirdBuildingSchedule = thirdBuildingSchedule.mapToUi(todayDayOfWeekNumber),
                        updatedAtEpochMillis = servicesSchedule.updatedAtEpochMillis,
                        isStale = servicesSchedule.isStale,
                    )
                }
                is Data.Initial -> ServicesScheduleUiState.Loading
                is Data.Error -> ServicesScheduleUiState.Error(servicesSchedule.message)
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ServicesScheduleUiState.Loading,
            )

    fun refresh() {
        getServicesSchedule.refresh(servicesScheduleType)
    }

    private fun List<ServicesScheduleItem>.mapToUi(
        todayDayOfWeekNumber: Int,
    ): List<ServicesScheduleItemUi> {
        return map {
            ServicesScheduleItemUi(
                day = DayOfWeek(it.day),
                time = it.time,
                isToday = todayDayOfWeekNumber == it.day,
            )
        }
    }
}
