package com.alad1nks.dubovozki.shared.di

import com.alad1nks.dubovozki.shared.ui.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

internal val SharedModule =
    module {
        viewModel {
            MainViewModel(get(), get())
        }
    }
