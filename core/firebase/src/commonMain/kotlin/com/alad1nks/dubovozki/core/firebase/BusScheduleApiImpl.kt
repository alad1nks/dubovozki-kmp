package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.model.BusScheduleResponse
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class BusScheduleApiImpl : BusScheduleApi {
    private val busSchedule = MutableStateFlow<Data<BusScheduleResponse>>(Data.Initial())
    private var registration: FirebaseListenerRegistration? = null

    init {
        listen()
    }

    override fun getBusSchedule(): StateFlow<Data<BusScheduleResponse>> {
        return busSchedule.asStateFlow()
    }

    override fun refresh() {
        listen()
    }

    private fun listen() {
        registration?.remove()
        busSchedule.value = Data.Initial()
        registration = busSchedule.addValueEventListener("busSchedule")
    }
}
