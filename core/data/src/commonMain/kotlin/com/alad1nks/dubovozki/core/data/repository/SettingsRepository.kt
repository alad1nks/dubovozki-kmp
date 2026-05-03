package com.alad1nks.dubovozki.core.data.repository

import com.alad1nks.dubovozki.core.model.Language
import com.alad1nks.dubovozki.core.storage.common.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepository(
    private val storage: Storage,
) {
    fun getDarkTheme(): Flow<Boolean?> {
        return storage.getDarkTheme()
    }

    fun getLanguage(): Flow<Language> {
        return storage.getLanguageCode().map { languageCode ->
            Language.entries.firstOrNull {
                it.code == languageCode
            } ?: Language.SYSTEM
        }
    }

    suspend fun setDarkTheme(value: Boolean) {
        storage.setDarkTheme(value)
    }

    suspend fun setLanguage(value: Language) {
        val languageCode = value.code ?: "system"

        storage.setLanguageCode(languageCode)
    }
}
