package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface GetThemeMode {
    operator fun invoke(): Flow<ThemeMode>
}
