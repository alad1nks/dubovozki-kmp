package com.alad1nks.dubovozki.core.firebase.model

import kotlinx.serialization.Serializable

@Serializable
data class BusScheduleResponse(
    val busList: List<Bus>? = null,
    val revision: String? = null,
) {
    @Serializable
    data class Bus(
        val id: Int? = null,
        val dayOfWeek: Int? = null,
        val dayTime: Long? = null,
        val dayTimeString: String? = null,
        val direction: String? = null,
        val station: String? = null,
    )
}
