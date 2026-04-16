package com.alad1nks.dubovozki.core.domain

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class GetMoscowDayOfWeek {
    @OptIn(ExperimentalTime::class)
    operator fun invoke(): DayOfWeek {
        val now = Clock.System.now()
        val moscowZone = TimeZone.of("UTC+3")

        return now.toLocalDateTime(moscowZone).dayOfWeek
    }
}
