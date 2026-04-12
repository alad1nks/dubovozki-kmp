package com.alad1nks.dubovozki.feature.collections.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun CollectionsRoute(
    viewModel: CollectionsViewModel,
    modifier: Modifier = Modifier,
) {
    CollectionsScreen(
        modifier = modifier,
    )
}

@Composable
private fun CollectionsScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    )
}
