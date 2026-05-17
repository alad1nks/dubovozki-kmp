package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.model.ServicesScheduleResponse
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.StateFlow

interface ServicesScheduleApi {
    fun getLinenRoomSchedule(): StateFlow<Data<ServicesScheduleResponse>>
}
