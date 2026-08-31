package com.alad1nks.dubovozki.core.domain.di

import com.alad1nks.dubovozki.core.domain.GetBusSchedule
import com.alad1nks.dubovozki.core.domain.GetBusScheduleImpl
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
import com.alad1nks.dubovozki.core.domain.GetThemeMode
import com.alad1nks.dubovozki.core.domain.GetThemeModeImpl
import com.alad1nks.dubovozki.core.domain.MoscowTimeProvider
import com.alad1nks.dubovozki.core.domain.ObserveMoscowDayOfWeek
import com.alad1nks.dubovozki.core.domain.ObserveMoscowDayOfWeekImpl
import com.alad1nks.dubovozki.core.domain.SetLanguage
import com.alad1nks.dubovozki.core.domain.SetLanguageImpl
import com.alad1nks.dubovozki.core.domain.SetThemeMode
import com.alad1nks.dubovozki.core.domain.SetThemeModeImpl
import com.alad1nks.dubovozki.core.domain.SystemMoscowTimeProvider
import org.koin.dsl.module

val DomainModule =
    module {
        factory<GetBusSchedule> { GetBusScheduleImpl(get(), get()) }
        factory<GetLanguage> { GetLanguageImpl(get()) }
        factory<GetThemeMode> { GetThemeModeImpl(get()) }
        factory<GetMoscowDayOfWeek> { GetMoscowDayOfWeekImpl(get()) }
        factory<ObserveMoscowDayOfWeek> { ObserveMoscowDayOfWeekImpl(get()) }
        single<MoscowTimeProvider> { SystemMoscowTimeProvider() }
        factory { GetMoscowLocalDateTime(get()) }
        factory<GetMoscowLocalTime> { GetMoscowLocalTimeImpl(get()) }
        factory<GetServices> { GetServicesImpl(get()) }
        factory<GetServicesSchedule> { GetServicesScheduleImpl(get()) }
        factory<SetLanguage> { SetLanguageImpl(get()) }
        factory<SetThemeMode> { SetThemeModeImpl(get()) }
    }
