package com.alad1nks.dubovozki.core.storage.common

import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    fun getBoolean(key: String): Flow<Boolean?>

    suspend fun setBoolean(key: String, value: Boolean)
}
