package com.alad1nks.dubovozki.core.domain

import kotlinx.datetime.LocalTime

internal class GetMoscowLocalTimeImpl(
    private val getMoscowLocalDateTime: GetMoscowLocalDateTime,
) : GetMoscowLocalTime {
    override operator fun invoke(): LocalTime {
        return getMoscowLocalDateTime().time
    }
}
