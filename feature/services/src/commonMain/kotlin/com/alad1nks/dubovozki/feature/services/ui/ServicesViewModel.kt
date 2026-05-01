package com.alad1nks.dubovozki.feature.services.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alad1nks.dubovozki.core.domain.GetServices
import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.feature.services.model.ServicesUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class ServicesViewModel(
    getServices: GetServices,
) : ViewModel() {
    val uiState: StateFlow<ServicesUiState> =
        getServices()
            .map { services ->
                when (services) {
                    is Data.Success ->
                        ServicesUiState.Content(
                            contactLink = services.value.contactLink,
                            donutLink = services.value.donutLink,
                        )
                    is Data.Initial,
                    is Data.Error,
                    -> ServicesUiState.Loading
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ServicesUiState.Loading,
            )
}
