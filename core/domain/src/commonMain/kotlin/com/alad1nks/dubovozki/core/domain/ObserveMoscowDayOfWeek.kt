package com.alad1nks.dubovozki.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.DayOfWeek

interface ObserveMoscowDayOfWeek {
    operator fun invoke(): Flow<DayOfWeek>
}
