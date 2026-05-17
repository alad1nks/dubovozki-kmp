package com.alad1nks.dubovozki.core.domain

import kotlinx.datetime.DayOfWeek

internal class GetMoscowDayOfWeekImpl(
    private val getMoscowLocalDateTime: GetMoscowLocalDateTime,
) : GetMoscowDayOfWeek {
    override fun invoke(): DayOfWeek {
        return getMoscowLocalDateTime().dayOfWeek
    }
}
