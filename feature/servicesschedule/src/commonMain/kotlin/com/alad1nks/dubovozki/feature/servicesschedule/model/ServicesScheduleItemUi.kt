package com.alad1nks.dubovozki.feature.servicesschedule.model

import kotlinx.datetime.DayOfWeek

internal data class ServicesScheduleItemUi(
    val day: DayOfWeek,
    val time: String,
    val isToday: Boolean,
)
