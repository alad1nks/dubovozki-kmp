package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetDarkThemeImpl(
    private val settingsRepository: SettingsRepository,
) : GetDarkTheme {
    override fun invoke(): Flow<Boolean> {
        return settingsRepository.getDarkTheme().map { it == true }
    }
}
