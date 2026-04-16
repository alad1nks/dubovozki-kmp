package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.model.BusScheduleResponse
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.StateFlow

interface BusScheduleApi {
    fun getBusSchedule(): StateFlow<Data<BusScheduleResponse>>
}
