package com.alad1nks.dubovozki.core.domain

import kotlinx.datetime.DayOfWeek

interface GetMoscowDayOfWeek {
    operator fun invoke(): DayOfWeek
}
