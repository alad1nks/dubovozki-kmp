package com.alad1nks.dubovozki.core.firebase.di

import com.alad1nks.dubovozki.core.firebase.HomeApi
import com.alad1nks.dubovozki.core.firebase.HomeApiImpl
import org.koin.dsl.module

actual val FirebaseModule =
    module {
        single<HomeApi> { HomeApiImpl() }
    }
