package com.alad1nks.dubovozki.shared.ui

import androidx.lifecycle.ViewModel
import com.alad1nks.dubovozki.core.domain.GetLanguage
import com.alad1nks.dubovozki.core.domain.GetThemeMode

internal class MainViewModel(
    getThemeMode: GetThemeMode,
    getLanguage: GetLanguage,
) : ViewModel() {
    val themeMode = getThemeMode()
    val language = getLanguage()
}
