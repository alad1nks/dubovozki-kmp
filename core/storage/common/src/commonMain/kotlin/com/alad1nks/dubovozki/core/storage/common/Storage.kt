package com.alad1nks.dubovozki.core.storage.common

import kotlinx.coroutines.flow.Flow

interface Storage {
    fun getDarkTheme(): Flow<Boolean?>

    suspend fun setDarkTheme(value: Boolean)
}
