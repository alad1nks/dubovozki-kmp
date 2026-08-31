package com.alad1nks.dubovozki.core.domain

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

interface MoscowTimeProvider {
    fun now(): LocalDateTime

    fun observe(): Flow<LocalDateTime> =
        flow {
            while (true) {
                val current = now()
                emit(current)
                val millisecondOfDay =
                    current.hour * 60 * 60 * 1_000L +
                        current.minute * 60 * 1_000L +
                        current.second * 1_000L +
                        current.nanosecond / 1_000_000L
                delay(MINUTE_MILLIS - (millisecondOfDay % MINUTE_MILLIS))
            }
        }
}

internal class SystemMoscowTimeProvider : MoscowTimeProvider {
    @OptIn(ExperimentalTime::class)
    override fun now(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3"))
}

private const val MINUTE_MILLIS = 60_000L
