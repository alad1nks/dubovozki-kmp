package com.alad1nks.dubovozki.feature.services.ui

import androidx.lifecycle.ViewModel
import com.alad1nks.dubovozki.core.domain.GetHomeItems
import kotlinx.coroutines.flow.StateFlow

internal class ServicesViewModel(
    getHomeItems: GetHomeItems,
) : ViewModel() {
    val items: StateFlow<List<String>> = getHomeItems()
}
