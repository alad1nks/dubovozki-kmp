package com.alad1nks.dubovozki.core.storage.common

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    fun getString(key: String): Flow<String?>

    fun getBoolean(key: String): Flow<Boolean?>

    suspend fun setString(key: String, value: String)

    suspend fun setBoolean(key: String, value: Boolean)
}
