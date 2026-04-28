package com.alad1nks.dubovozki.feature.settings.model

internal sealed interface SettingsUiState {
    object Loading : SettingsUiState

    data class Content(
        val darkTheme: Boolean,
    ) : SettingsUiState
}
