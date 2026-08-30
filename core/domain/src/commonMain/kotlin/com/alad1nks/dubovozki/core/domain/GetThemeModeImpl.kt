package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.data.repository.SettingsRepository
import com.alad1nks.dubovozki.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow

internal class GetThemeModeImpl(
    private val settingsRepository: SettingsRepository,
) : GetThemeMode {
    override fun invoke(): Flow<ThemeMode> = settingsRepository.getThemeMode()
}
