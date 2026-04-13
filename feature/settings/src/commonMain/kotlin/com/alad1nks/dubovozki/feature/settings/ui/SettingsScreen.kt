package com.alad1nks.dubovozki.feature.settings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun SettingsRoute(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    SettingsScreen(
        modifier = modifier,
    )
}

@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    )
}
