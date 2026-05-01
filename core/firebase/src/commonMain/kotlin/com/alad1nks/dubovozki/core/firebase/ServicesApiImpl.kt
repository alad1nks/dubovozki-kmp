package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.model.ServicesResponse
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class ServicesApiImpl : ServicesApi {
    private val services =
        MutableStateFlow<Data<ServicesResponse>>(Data.Initial())
            .apply { addValueEventListener("services") }

    override fun getServices(): StateFlow<Data<ServicesResponse>> {
        return services.asStateFlow()
    }
}
