package com.alad1nks.dubovozki.core.data.di

import com.alad1nks.dubovozki.core.data.repository.BusScheduleRepository
import com.alad1nks.dubovozki.core.data.repository.ServicesRepository
import com.alad1nks.dubovozki.core.data.repository.ServicesScheduleRepository
import com.alad1nks.dubovozki.core.data.repository.SettingsRepository
import org.koin.dsl.module

val DataModule =
    module {
        single { BusScheduleRepository(get(), get()) }
        single { ServicesRepository(get(), get()) }
        single { ServicesScheduleRepository(get(), get()) }
        single { SettingsRepository(get()) }
    }
