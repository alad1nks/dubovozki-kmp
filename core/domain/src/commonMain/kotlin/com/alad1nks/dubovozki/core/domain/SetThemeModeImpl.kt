package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.data.repository.SettingsRepository
import com.alad1nks.dubovozki.core.model.ThemeMode

internal class SetThemeModeImpl(
    private val settingsRepository: SettingsRepository,
) : SetThemeMode {
    override suspend fun invoke(value: ThemeMode) {
        settingsRepository.setThemeMode(value)
    }
}
