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

    override fun getThemeModeCode(): Flow<String?> {
        return appPreferences.getString(THEME_MODE)
    }

    override fun getBusScheduleCache(): Flow<String?> {
        return appPreferences.getString(BUS_SCHEDULE_CACHE)
    }

    override fun getServicesCache(): Flow<String?> {
        return appPreferences.getString(SERVICES_CACHE)
    }

    override fun getServicesScheduleCache(): Flow<String?> {
        return appPreferences.getString(SERVICES_SCHEDULE_CACHE)
    }

    override suspend fun setLanguageCode(value: String) {
        appPreferences.setString(LANGUAGE, value)
    }

    override suspend fun setThemeModeCode(value: String) {
        appPreferences.setString(THEME_MODE, value)
    }

    override suspend fun setBusScheduleCache(value: String) {
        appPreferences.setString(BUS_SCHEDULE_CACHE, value)
    }

    override suspend fun setServicesCache(value: String) {
        appPreferences.setString(SERVICES_CACHE, value)
    }

    override suspend fun setServicesScheduleCache(value: String) {
        appPreferences.setString(SERVICES_SCHEDULE_CACHE, value)
    }

    private companion object {
        const val DARK_THEME = "dark_theme"
        const val LANGUAGE = "language"
        const val THEME_MODE = "theme_mode"
        const val BUS_SCHEDULE_CACHE = "bus_schedule_cache_v1"
        const val SERVICES_CACHE = "services_cache_v1"
        const val SERVICES_SCHEDULE_CACHE = "services_schedule_cache_v1"
    }
}
