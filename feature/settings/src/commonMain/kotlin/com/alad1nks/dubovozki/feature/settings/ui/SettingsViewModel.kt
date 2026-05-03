package com.alad1nks.dubovozki.feature.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alad1nks.dubovozki.core.domain.GetDarkTheme
import com.alad1nks.dubovozki.core.domain.GetLanguage
import com.alad1nks.dubovozki.core.domain.SetDarkTheme
import com.alad1nks.dubovozki.core.domain.SetLanguage
import com.alad1nks.dubovozki.core.model.Language
import com.alad1nks.dubovozki.feature.settings.model.SettingsUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SettingsViewModel(
    getDarkTheme: GetDarkTheme,
    getLanguage: GetLanguage,
    private val setDarkTheme: SetDarkTheme,
    private val setLanguage: SetLanguage,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> =
        combine(
            getDarkTheme(),
            getLanguage(),
        ) { darkTheme, language ->
            SettingsUiState.Content(
                darkTheme = darkTheme,
                language = language,
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

    fun selectLanguage(value: Language) {
        viewModelScope.launch {
            setLanguage(value)
        }
    }
}
