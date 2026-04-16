package com.alad1nks.dubovozki.core.data.di

import com.alad1nks.dubovozki.core.data.repository.BusScheduleRepository
import org.koin.dsl.module

val DataModule =
    module {
        single { BusScheduleRepository(get()) }
    }
