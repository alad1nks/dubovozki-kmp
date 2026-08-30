package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.model.ServicesResponse
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.StateFlow

interface ServicesApi {
    fun getServices(): StateFlow<Data<ServicesResponse>>

    fun refresh()
}
