package com.alad1nks.dubovozki.core.storage.datastore.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.alad1nks.dubovozki.core.storage.common.AppPreferences
import com.alad1nks.dubovozki.core.storage.datastore.AppPreferencesImpl
import com.alad1nks.dubovozki.core.storage.datastore.PreferenceDataStoreProduceFile
import org.koin.dsl.module

val StorageDataStoreModule =
    module {
        single { PreferenceDataStoreFactory.createWithPath { PreferenceDataStoreProduceFile } }
        single<AppPreferences> { AppPreferencesImpl(get()) }
    }
