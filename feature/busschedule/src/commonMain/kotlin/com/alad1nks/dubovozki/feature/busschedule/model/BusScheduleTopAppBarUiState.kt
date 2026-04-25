package com.alad1nks.dubovozki.feature.busschedule.model

import com.alad1nks.dubovozki.core.model.DayOfWeekFilter
import com.alad1nks.dubovozki.core.model.StationFilter

internal data class BusScheduleTopAppBarUiState(
    val stationFilterSpinnerExpanded: Boolean = false,
    val selectedStationFilter: StationFilter = StationFilter.ALL,
    val dayOfWeekFilterSpinnerExpanded: Boolean = false,
    val selectedDayOfWeekFilter: DayOfWeekFilter = DayOfWeekFilter.TODAY,
)
