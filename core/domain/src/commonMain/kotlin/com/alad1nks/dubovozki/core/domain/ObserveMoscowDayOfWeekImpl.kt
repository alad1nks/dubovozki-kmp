package com.alad1nks.dubovozki.core.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DayOfWeek

internal class ObserveMoscowDayOfWeekImpl(
    private val getMoscowDayOfWeek: GetMoscowDayOfWeek,
) : ObserveMoscowDayOfWeek {
    override fun invoke(): Flow<DayOfWeek> =
        flow {
            while (true) {
                emit(getMoscowDayOfWeek())
                delay(DAY_CHECK_INTERVAL_MILLIS)
            }
        }.distinctUntilChanged()

    private companion object {
        const val DAY_CHECK_INTERVAL_MILLIS = 60_000L
    }
}
