package com.alad1nks.dubovozki.feature.busschedule.model

import com.alad1nks.dubovozki.core.model.Bus

data class BusUi(
    val id: Int,
    val dayTime: String,
    val timeDifference: Int?,
    val station: Bus.Station,
)
