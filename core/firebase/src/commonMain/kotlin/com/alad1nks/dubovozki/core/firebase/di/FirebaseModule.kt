package com.alad1nks.dubovozki.core.firebase.di

import com.alad1nks.dubovozki.core.firebase.BusScheduleApi
import com.alad1nks.dubovozki.core.firebase.BusScheduleApiImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val FirebaseModule: Module =
    module {
        single<BusScheduleApi> { BusScheduleApiImpl() }
    }
