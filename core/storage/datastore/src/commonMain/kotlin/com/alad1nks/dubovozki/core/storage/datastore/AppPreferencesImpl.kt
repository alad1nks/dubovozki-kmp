package com.alad1nks.dubovozki.core.storage.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.alad1nks.dubovozki.core.storage.common.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class AppPreferencesImpl(
    private val dataStore: DataStore<Preferences>,
) : AppPreferences {
    override fun getBoolean(key: String): Flow<Boolean?> {
        return dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)]
        }
    }

    override suspend fun setBoolean(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }
}
