package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.model.BusSchedule
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.DayOfWeekFilter
import com.alad1nks.dubovozki.core.model.StationFilter
import kotlinx.coroutines.flow.Flow

interface GetBusSchedule {
    operator fun invoke(
        stationFilter: StationFilter,
        dayOfWeekFilter: DayOfWeekFilter,
    ): Flow<Data<BusSchedule>>
}
