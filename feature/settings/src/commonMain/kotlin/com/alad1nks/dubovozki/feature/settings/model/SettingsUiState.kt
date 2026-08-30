package com.alad1nks.dubovozki.feature.settings.model

import com.alad1nks.dubovozki.core.model.Language
import com.alad1nks.dubovozki.core.model.ThemeMode

internal sealed interface SettingsUiState {
    object Loading : SettingsUiState

    data class Content(
        val themeMode: ThemeMode,
        val language: Language,
    ) : SettingsUiState
}
