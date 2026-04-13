package com.alad1nks.dubovozki.feature.services.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
internal fun ServicesRoute(
    viewModel: ServicesViewModel,
    modifier: Modifier = Modifier,
) {
    val items by viewModel.items.collectAsState()

    ServicesScreen(
        items = items,
        modifier = modifier,
    )
}

@Composable
private fun ServicesScreen(
    items: List<String>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items.forEach { item ->
            item {
                Text(
                    text = item,
                )
            }
        }
    }
}
