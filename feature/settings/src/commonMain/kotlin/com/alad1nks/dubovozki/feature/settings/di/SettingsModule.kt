package com.alad1nks.dubovozki.feature.settings.di

import com.alad1nks.dubovozki.feature.settings.ui.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val SettingsModule =
    module {
        viewModel {
            SettingsViewModel()
        }
    }
