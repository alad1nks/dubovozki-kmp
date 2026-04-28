package com.alad1nks.dubovozki.shared

import com.alad1nks.dubovozki.core.storage.js.di.StorageJsModule
import org.koin.core.module.Module

actual val PlatformModules: List<Module> get() =
    listOf(
        StorageJsModule,
    )
