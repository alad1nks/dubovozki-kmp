package com.alad1nks.dubovozki.feature.services.di

import com.alad1nks.dubovozki.feature.services.ui.ServicesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ServicesModule =
    module {
        viewModel {
            ServicesViewModel(get())
        }
    }
