package com.alad1nks.dubovozki.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime

internal class GetMoscowLocalTimeImpl(
    private val getMoscowLocalDateTime: GetMoscowLocalDateTime,
) : GetMoscowLocalTime {
    override operator fun invoke(): LocalTime {
        return getMoscowLocalDateTime().time
    }

    override fun observe(): Flow<LocalTime> = getMoscowLocalDateTime.observe().map { it.time }
}
