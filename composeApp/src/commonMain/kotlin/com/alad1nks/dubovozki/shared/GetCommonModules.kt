package com.alad1nks.dubovozki.shared

import com.alad1nks.dubovozki.core.data.di.DataModule
import com.alad1nks.dubovozki.core.domain.di.DomainModule
import com.alad1nks.dubovozki.core.firebase.di.FirebaseModule
import com.alad1nks.dubovozki.feature.busschedule.di.BusScheduleModule
import com.alad1nks.dubovozki.feature.services.di.ServicesModule
import com.alad1nks.dubovozki.feature.settings.di.SettingsModule
import org.koin.core.module.Module

fun getCommonModules(): List<Module> {
    return listOf(
        BusScheduleModule,
        DataModule,
        DomainModule,
        FirebaseModule,
        ServicesModule,
        SettingsModule,
    )
}
