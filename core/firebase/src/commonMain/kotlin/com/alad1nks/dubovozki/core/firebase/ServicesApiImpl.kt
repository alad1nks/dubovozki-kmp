package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.model.ServicesResponse
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ServicesApiImpl : ServicesApi {
    private val services = MutableStateFlow<Data<ServicesResponse>>(Data.Initial())
    private var registration: FirebaseListenerRegistration? = null

    init {
        listen()
    }

    override fun getServices(): StateFlow<Data<ServicesResponse>> {
        return services.asStateFlow()
    }

    override fun refresh() {
        listen()
    }

    private fun listen() {
        registration?.remove()
        services.value = Data.Initial()
        registration = services.addValueEventListener("services")
    }
}
