package com.alad1nks.dubovozki.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alad1nks.dubovozki.core.domain.GetDarkTheme
import com.alad1nks.dubovozki.core.domain.SetDarkTheme
import com.alad1nks.dubovozki.feature.settings.model.SettingsUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    getDarkTheme: GetDarkTheme,
    private val setDarkTheme: SetDarkTheme,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> =
        getDarkTheme()
            .map { darkTheme ->
                SettingsUiState.Content(
                    darkTheme = darkTheme,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState.Loading,
            )

    fun changeDarkTheme(value: Boolean) {
        viewModelScope.launch {
            setDarkTheme(value)
        }
    }
}
