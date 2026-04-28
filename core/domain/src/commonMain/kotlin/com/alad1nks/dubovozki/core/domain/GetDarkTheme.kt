package com.alad1nks.dubovozki.core.domain

import kotlinx.coroutines.flow.Flow

interface GetDarkTheme {
    operator fun invoke(): Flow<Boolean>
}
