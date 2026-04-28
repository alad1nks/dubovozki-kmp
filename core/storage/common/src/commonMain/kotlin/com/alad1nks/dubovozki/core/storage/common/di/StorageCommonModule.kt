package com.alad1nks.dubovozki.core.storage.common.di

import com.alad1nks.dubovozki.core.storage.common.Storage
import com.alad1nks.dubovozki.core.storage.common.StorageImpl
import org.koin.dsl.module

val StorageCommonModule =
    module {
        single<Storage> { StorageImpl(get()) }
    }
