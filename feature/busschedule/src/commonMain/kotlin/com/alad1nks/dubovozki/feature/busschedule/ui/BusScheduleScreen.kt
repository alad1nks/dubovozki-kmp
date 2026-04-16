package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.alad1nks.dubovozki.core.model.BusSchedule
import com.alad1nks.dubovozki.core.model.Data

@Composable
internal fun BusScheduleRoute(
    viewModel: BusScheduleViewModel,
    modifier: Modifier = Modifier,
) {
    val busSchedule by viewModel.busSchedule.collectAsState(Data.Initial())

    BusScheduleScreen(
        busSchedule = busSchedule,
        modifier = modifier,
    )
}

@Composable
private fun BusScheduleScreen(
    busSchedule: Data<BusSchedule>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        if (busSchedule is Data.Success) {
            items(busSchedule.value.toMoscow) { bus ->
                Text(text = bus.dayTimeString)
            }
        }
    }
}
