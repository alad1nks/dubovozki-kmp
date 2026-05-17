package com.alad1nks.dubovozki.core.firebase.di

import com.alad1nks.dubovozki.core.firebase.BusScheduleApi
import com.alad1nks.dubovozki.core.firebase.BusScheduleApiImpl
import com.alad1nks.dubovozki.core.firebase.ServicesApi
import com.alad1nks.dubovozki.core.firebase.ServicesApiImpl
import com.alad1nks.dubovozki.core.firebase.ServicesScheduleApi
import com.alad1nks.dubovozki.core.firebase.ServicesScheduleApiImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val FirebaseModule: Module =
    module {
        single<BusScheduleApi> { BusScheduleApiImpl() }
        single<ServicesApi> { ServicesApiImpl() }
        single<ServicesScheduleApi> { ServicesScheduleApiImpl() }
    }
