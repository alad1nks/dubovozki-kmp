package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.data.repository.SettingsRepository
import com.alad1nks.dubovozki.core.model.Language

internal class SetLanguageImpl(
    private val settingsRepository: SettingsRepository,
) : SetLanguage {
    override suspend fun invoke(value: Language) {
        settingsRepository.setLanguage(value)
    }
}
