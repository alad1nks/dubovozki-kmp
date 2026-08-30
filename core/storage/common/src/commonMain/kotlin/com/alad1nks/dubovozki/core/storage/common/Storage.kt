package com.alad1nks.dubovozki.core.storage.common

import kotlinx.coroutines.flow.Flow

interface Storage {
    fun getDarkTheme(): Flow<Boolean?>

    fun getLanguageCode(): Flow<String?>

    fun getThemeModeCode(): Flow<String?>

    fun getBusScheduleCache(): Flow<String?>

    fun getServicesCache(): Flow<String?>

    fun getServicesScheduleCache(): Flow<String?>

    suspend fun setLanguageCode(value: String)

    suspend fun setThemeModeCode(value: String)

    suspend fun setBusScheduleCache(value: String)

    suspend fun setServicesCache(value: String)

    suspend fun setServicesScheduleCache(value: String)
}
