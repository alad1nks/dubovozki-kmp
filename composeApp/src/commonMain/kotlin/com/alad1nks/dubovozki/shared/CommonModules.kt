package com.alad1nks.dubovozki.shared

import com.alad1nks.dubovozki.core.data.di.DataModule
import com.alad1nks.dubovozki.core.domain.di.DomainModule
import com.alad1nks.dubovozki.core.firebase.di.FirebaseModule
import com.alad1nks.dubovozki.core.storage.common.di.StorageCommonModule
import com.alad1nks.dubovozki.feature.busschedule.di.BusScheduleModule
import com.alad1nks.dubovozki.feature.services.di.ServicesModule
import com.alad1nks.dubovozki.feature.servicesschedule.di.ServicesScheduleModule
import com.alad1nks.dubovozki.feature.settings.di.SettingsModule
import com.alad1nks.dubovozki.shared.di.SharedModule
import org.koin.core.module.Module

val CommonModules: List<Module> get() =
    listOf(
        BusScheduleModule,
        DataModule,
        DomainModule,
        FirebaseModule,
        ServicesModule,
        ServicesScheduleModule,
        SettingsModule,
        SharedModule,
        StorageCommonModule,
    )
