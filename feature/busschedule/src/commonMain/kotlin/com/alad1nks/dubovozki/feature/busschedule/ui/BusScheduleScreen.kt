package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.alad1nks.dubovozki.feature.busschedule.model.BusScheduleUiState

@Composable
internal fun BusScheduleRoute(
    viewModel: BusScheduleViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    BusScheduleScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun BusScheduleScreen(
    uiState: BusScheduleUiState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        if (uiState is BusScheduleUiState.Data) {
            items(uiState.moscowBuses) { bus ->
                BusListItem(
                    dayTime = bus.dayTime,
                    timeDifference = bus.timeDifference,
                    station = bus.station,
                )
            }
        }
    }
}
