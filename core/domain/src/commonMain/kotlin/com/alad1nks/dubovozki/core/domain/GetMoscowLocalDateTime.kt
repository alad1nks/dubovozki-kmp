package com.alad1nks.dubovozki.core.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

internal class GetMoscowLocalDateTime(
    private val timeProvider: MoscowTimeProvider,
) {
    operator fun invoke(): LocalDateTime = timeProvider.now()

    fun observe(): Flow<LocalDateTime> = timeProvider.observe()
}
