package com.alad1nks.dubovozki.feature.servicesschedule.di

import com.alad1nks.dubovozki.core.model.ServicesScheduleType
import com.alad1nks.dubovozki.feature.servicesschedule.ui.ServicesScheduleViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ServicesScheduleModule =
    module {
        viewModel { (servicesScheduleType: ServicesScheduleType) ->
            ServicesScheduleViewModel(
                servicesScheduleType = servicesScheduleType,
                getServicesSchedule = get(),
                getMoscowDayOfWeek = get(),
            )
        }
    }
