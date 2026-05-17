package com.alad1nks.dubovozki.core.domain.di

import com.alad1nks.dubovozki.core.domain.GetBusSchedule
import com.alad1nks.dubovozki.core.domain.GetBusScheduleImpl
import com.alad1nks.dubovozki.core.domain.GetDarkTheme
import com.alad1nks.dubovozki.core.domain.GetDarkThemeImpl
import com.alad1nks.dubovozki.core.domain.GetLanguage
import com.alad1nks.dubovozki.core.domain.GetLanguageImpl
import com.alad1nks.dubovozki.core.domain.GetMoscowDayOfWeek
import com.alad1nks.dubovozki.core.domain.GetMoscowDayOfWeekImpl
import com.alad1nks.dubovozki.core.domain.GetMoscowLocalDateTime
import com.alad1nks.dubovozki.core.domain.GetMoscowLocalTime
import com.alad1nks.dubovozki.core.domain.GetMoscowLocalTimeImpl
import com.alad1nks.dubovozki.core.domain.GetServices
import com.alad1nks.dubovozki.core.domain.GetServicesImpl
import com.alad1nks.dubovozki.core.domain.GetServicesSchedule
import com.alad1nks.dubovozki.core.domain.GetServicesScheduleImpl
import com.alad1nks.dubovozki.core.domain.SetDarkTheme
import com.alad1nks.dubovozki.core.domain.SetDarkThemeImpl
import com.alad1nks.dubovozki.core.domain.SetLanguage
import com.alad1nks.dubovozki.core.domain.SetLanguageImpl
import org.koin.dsl.module

val DomainModule =
    module {
        factory<GetBusSchedule> { GetBusScheduleImpl(get(), get()) }
        factory<GetDarkTheme> { GetDarkThemeImpl(get()) }
        factory<GetLanguage> { GetLanguageImpl(get()) }
        factory<GetMoscowDayOfWeek> { GetMoscowDayOfWeekImpl(get()) }
        factory { GetMoscowLocalDateTime() }
        factory<GetMoscowLocalTime> { GetMoscowLocalTimeImpl(get()) }
        factory<GetServices> { GetServicesImpl(get()) }
        factory<GetServicesSchedule> { GetServicesScheduleImpl(get()) }
        factory<SetDarkTheme> { SetDarkThemeImpl(get()) }
        factory<SetLanguage> { SetLanguageImpl(get()) }
    }
