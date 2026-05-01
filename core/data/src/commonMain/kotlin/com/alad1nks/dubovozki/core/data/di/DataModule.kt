package com.alad1nks.dubovozki.core.data.di

import com.alad1nks.dubovozki.core.data.repository.BusScheduleRepository
import com.alad1nks.dubovozki.core.data.repository.ServicesRepository
import com.alad1nks.dubovozki.core.data.repository.SettingsRepository
import org.koin.dsl.module

val DataModule =
    module {
        single { BusScheduleRepository(get()) }
        single { ServicesRepository(get()) }
        single { SettingsRepository(get()) }
    }
