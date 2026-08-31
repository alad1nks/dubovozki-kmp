package com.alad1nks.dubovozki.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalTime

interface GetMoscowLocalTime {
    operator fun invoke(): LocalTime

    fun observe(): Flow<LocalTime>
}
