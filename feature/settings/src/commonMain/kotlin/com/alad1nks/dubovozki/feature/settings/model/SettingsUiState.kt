package com.alad1nks.dubovozki.feature.settings.model

import com.alad1nks.dubovozki.core.model.Language

internal sealed interface SettingsUiState {
    object Loading : SettingsUiState

    data class Content(
        val darkTheme: Boolean,
        val language: Language,
    ) : SettingsUiState
}
