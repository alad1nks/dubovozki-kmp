package com.alad1nks.dubovozki.core.storage.common

import kotlinx.coroutines.flow.Flow

internal class StorageImpl(
    private val appPreferences: AppPreferences,
) : Storage {
    override fun getDarkTheme(): Flow<Boolean?> {
        return appPreferences.getBoolean(DARK_THEME)
    }

    override fun getLanguageCode(): Flow<String?> {
        return appPreferences.getString(LANGUAGE)
    }

    override suspend fun setDarkTheme(value: Boolean) {
        appPreferences.setBoolean(DARK_THEME, value)
    }

    override suspend fun setLanguageCode(value: String) {
        appPreferences.setString(LANGUAGE, value)
    }

    private companion object {
        const val DARK_THEME = "dark_theme"
        const val LANGUAGE = "language"
    }
}
