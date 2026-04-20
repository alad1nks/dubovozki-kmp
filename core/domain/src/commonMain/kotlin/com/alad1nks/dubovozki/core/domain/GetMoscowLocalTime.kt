package com.alad1nks.dubovozki.core.domain

import kotlinx.datetime.LocalTime

interface GetMoscowLocalTime {
    operator fun invoke(): LocalTime
}
