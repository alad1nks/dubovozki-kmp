package com.alad1nks.dubovozki.core.data.repository

import com.alad1nks.dubovozki.core.model.Language
import com.alad1nks.dubovozki.core.model.ThemeMode
import com.alad1nks.dubovozki.core.storage.common.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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

    fun getThemeMode(): Flow<ThemeMode> {
        return combine(
            storage.getThemeModeCode(),
            storage.getDarkTheme(),
        ) { themeModeCode, legacyDarkTheme ->
            ThemeMode.entries.firstOrNull { it.code == themeModeCode }
                ?: legacyDarkTheme?.let { if (it) ThemeMode.DARK else ThemeMode.LIGHT }
                ?: ThemeMode.SYSTEM
        }
    }

    suspend fun setLanguage(value: Language) {
        val languageCode = value.code ?: "system"

        storage.setLanguageCode(languageCode)
    }

    suspend fun setThemeMode(value: ThemeMode) {
        storage.setThemeModeCode(value.code)
    }
}
