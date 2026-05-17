package com.alad1nks.dubovozki.core.firebase.model

import kotlinx.serialization.Serializable

@Serializable
data class ServicesScheduleResponse(
    val firstBuilding: List<ServiceScheduleItemResponse>? = null,
    val secondBuilding: List<ServiceScheduleItemResponse>? = null,
    val thirdBuilding: List<ServiceScheduleItemResponse>? = null,
) {
    @Serializable
    data class ServiceScheduleItemResponse(
        val day: Int? = null,
        val time: String? = null,
    )
}
