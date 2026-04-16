package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.model.BusScheduleResponse
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class BusScheduleApiImpl : BusScheduleApi {
    private val buses =
        MutableStateFlow<Data<BusScheduleResponse>>(Data.Initial())
            .apply { addValueEventListener("busSchedule") }

    override fun getBusSchedule(): StateFlow<Data<BusScheduleResponse>> {
        return buses.asStateFlow()
    }
}
