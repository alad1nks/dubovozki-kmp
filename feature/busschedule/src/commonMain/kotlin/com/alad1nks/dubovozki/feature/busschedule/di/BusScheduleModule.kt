package com.alad1nks.dubovozki.feature.busschedule.di

import com.alad1nks.dubovozki.feature.busschedule.ui.BusScheduleViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val BusScheduleModule =
    module {
        viewModel {
            BusScheduleViewModel(get(), get(), get())
        }
    }
