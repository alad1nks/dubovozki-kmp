package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.model.ThemeMode

interface SetThemeMode {
    suspend operator fun invoke(value: ThemeMode)
}
