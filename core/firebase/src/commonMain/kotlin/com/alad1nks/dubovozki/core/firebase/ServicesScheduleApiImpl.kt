package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.model.ServicesScheduleResponse
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ServicesScheduleApiImpl : ServicesScheduleApi {
    private val linenRoomSchedule = MutableStateFlow<Data<ServicesScheduleResponse>>(Data.Initial())
    private var registration: FirebaseListenerRegistration? = null

    init {
        listen()
    }

    override fun getLinenRoomSchedule(): StateFlow<Data<ServicesScheduleResponse>> {
        return linenRoomSchedule.asStateFlow()
    }

    override fun refresh() {
        listen()
    }

    private fun listen() {
        registration?.remove()
        linenRoomSchedule.value = Data.Initial()
        registration = linenRoomSchedule.addValueEventListener("servicesSchedule/linenRoom")
    }
}
