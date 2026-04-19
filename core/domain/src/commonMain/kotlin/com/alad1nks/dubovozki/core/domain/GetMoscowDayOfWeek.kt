package com.alad1nks.dubovozki.core.domain

import kotlinx.datetime.DayOfWeek

internal class GetMoscowDayOfWeek(
    private val getMoscowLocalDateTime: GetMoscowLocalDateTime,
) {
    operator fun invoke(): DayOfWeek {
        return getMoscowLocalDateTime().dayOfWeek
    }
}
