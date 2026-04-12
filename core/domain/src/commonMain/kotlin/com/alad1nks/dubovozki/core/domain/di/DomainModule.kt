package com.alad1nks.dubovozki.core.domain.di

import com.alad1nks.dubovozki.core.domain.GetHomeItems
import org.koin.dsl.module

val DomainModule =
    module {
        factory { GetHomeItems(get()) }
    }
