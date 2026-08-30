package com.alad1nks.dubovozki.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alad1nks.dubovozki.core.domain.GetLanguage
import com.alad1nks.dubovozki.core.domain.GetThemeMode
import com.alad1nks.dubovozki.core.domain.SetLanguage
import com.alad1nks.dubovozki.core.domain.SetThemeMode
import com.alad1nks.dubovozki.core.model.Language
import com.alad1nks.dubovozki.core.model.ThemeMode
import com.alad1nks.dubovozki.feature.settings.model.SettingsUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    getThemeMode: GetThemeMode,
    getLanguage: GetLanguage,
    private val setThemeMode: SetThemeMode,
    private val setLanguage: SetLanguage,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> =
        combine(
            getThemeMode(),
            getLanguage(),
        ) { themeMode, language ->
            SettingsUiState.Content(
                themeMode = themeMode,
                language = language,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = SettingsUiState.Loading,
            )

    fun selectThemeMode(value: ThemeMode) {
        viewModelScope.launch {
            setThemeMode(value)
        }
    }

    fun selectLanguage(value: Language) {
        viewModelScope.launch {
            setLanguage(value)
        }
    }
}
