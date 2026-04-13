package com.alad1nks.dubovozki.feature.busschedule.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun BusScheduleRoute(
    viewModel: BusScheduleViewModel,
    modifier: Modifier = Modifier,
) {
    BusScheduleScreen(
        modifier = modifier,
    )
}

@Composable
private fun BusScheduleScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    )
}
