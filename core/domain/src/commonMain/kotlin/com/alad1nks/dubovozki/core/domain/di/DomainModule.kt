package com.alad1nks.dubovozki.core.domain.di

import com.alad1nks.dubovozki.core.domain.GetBusSchedule
import com.alad1nks.dubovozki.core.domain.GetBusScheduleImpl
import com.alad1nks.dubovozki.core.domain.GetMoscowDayOfWeek
import org.koin.dsl.module

val DomainModule =
    module {
        factory<GetBusSchedule> { GetBusScheduleImpl(get(), get()) }
        factory { GetMoscowDayOfWeek() }
    }
