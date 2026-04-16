package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.lifecycle.ViewModel
import com.alad1nks.dubovozki.core.domain.GetBusSchedule
import com.alad1nks.dubovozki.core.model.DayOfWeekFilter
import com.alad1nks.dubovozki.core.model.StationFilter

internal class BusScheduleViewModel(
    private val getBusSchedule: GetBusSchedule,
) : ViewModel() {
    val busSchedule = getBusSchedule(StationFilter.ALL, DayOfWeekFilter.TODAY)
}
