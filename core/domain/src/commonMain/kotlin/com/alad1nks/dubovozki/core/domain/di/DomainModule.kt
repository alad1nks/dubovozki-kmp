package com.alad1nks.dubovozki.core.domain.di

import com.alad1nks.dubovozki.core.domain.GetBusSchedule
import com.alad1nks.dubovozki.core.domain.GetBusScheduleImpl
import com.alad1nks.dubovozki.core.domain.GetDarkTheme
import com.alad1nks.dubovozki.core.domain.GetDarkThemeImpl
import com.alad1nks.dubovozki.core.domain.GetMoscowDayOfWeek
import com.alad1nks.dubovozki.core.domain.GetMoscowLocalDateTime
import com.alad1nks.dubovozki.core.domain.GetMoscowLocalTime
import com.alad1nks.dubovozki.core.domain.GetMoscowLocalTimeImpl
import com.alad1nks.dubovozki.core.domain.SetDarkTheme
import com.alad1nks.dubovozki.core.domain.SetDarkThemeImpl
import org.koin.dsl.module

val DomainModule =
    module {
        factory<GetBusSchedule> { GetBusScheduleImpl(get(), get()) }
        factory<GetDarkTheme> { GetDarkThemeImpl(get()) }
        factory { GetMoscowDayOfWeek(get()) }
        factory { GetMoscowLocalDateTime() }
        factory<GetMoscowLocalTime> { GetMoscowLocalTimeImpl(get()) }
        factory<SetDarkTheme> { SetDarkThemeImpl(get()) }
    }
