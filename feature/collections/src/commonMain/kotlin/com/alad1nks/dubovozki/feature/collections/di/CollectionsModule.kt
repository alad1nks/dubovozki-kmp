package com.alad1nks.dubovozki.feature.collections.di

import com.alad1nks.dubovozki.feature.collections.ui.CollectionsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val CollectionsModule =
    module {
        viewModel {
            CollectionsViewModel()
        }
    }
