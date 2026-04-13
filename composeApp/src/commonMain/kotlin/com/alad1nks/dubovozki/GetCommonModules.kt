package com.alad1nks.dubovozki

import com.alad1nks.dubovozki.core.data.di.DataModule
import com.alad1nks.dubovozki.core.domain.di.DomainModule
import com.alad1nks.dubovozki.core.firebase.di.FirebaseModule
import com.alad1nks.dubovozki.feature.collections.di.CollectionsModule
import com.alad1nks.dubovozki.feature.home.di.HomeModule
import org.koin.core.module.Module

fun getCommonModules(): List<Module> {
    return listOf(
        CollectionsModule,
        DataModule,
        DomainModule,
        FirebaseModule,
        HomeModule,
    )
}
