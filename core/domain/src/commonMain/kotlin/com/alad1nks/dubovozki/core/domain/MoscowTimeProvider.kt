package com.alad1nks.dubovozki.core.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun interface MoscowTimeProvider {
    fun now(): LocalDateTime
}

internal class SystemMoscowTimeProvider : MoscowTimeProvider {
    @OptIn(ExperimentalTime::class)
    override fun now(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.of("UTC+3"))
}
