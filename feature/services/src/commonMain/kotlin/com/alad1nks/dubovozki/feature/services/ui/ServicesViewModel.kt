package com.alad1nks.dubovozki.feature.services.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class ServicesViewModel : ViewModel() {
    val items: StateFlow<List<String>> = MutableStateFlow(emptyList())
}
