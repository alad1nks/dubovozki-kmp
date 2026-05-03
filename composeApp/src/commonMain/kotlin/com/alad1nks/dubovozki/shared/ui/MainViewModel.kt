package com.alad1nks.dubovozki.shared.ui

import androidx.lifecycle.ViewModel
import com.alad1nks.dubovozki.core.domain.GetDarkTheme
import com.alad1nks.dubovozki.core.domain.GetLanguage

internal class MainViewModel(
    getDarkTheme: GetDarkTheme,
    getLanguage: GetLanguage,
) : ViewModel() {
    val darkTheme = getDarkTheme()
    val language = getLanguage()
}
