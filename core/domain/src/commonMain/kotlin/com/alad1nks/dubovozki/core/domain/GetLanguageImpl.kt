package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.data.repository.SettingsRepository
import com.alad1nks.dubovozki.core.model.Language
import kotlinx.coroutines.flow.Flow

internal class GetLanguageImpl(
    private val settingsRepository: SettingsRepository,
) : GetLanguage {
    override fun invoke(): Flow<Language> {
        return settingsRepository.getLanguage()
    }
}
