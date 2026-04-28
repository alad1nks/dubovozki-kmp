package com.alad1nks.dubovozki.shared.ui

import androidx.lifecycle.ViewModel
import com.alad1nks.dubovozki.core.domain.GetDarkTheme

internal class MainViewModel(
    getDarkTheme: GetDarkTheme,
) : ViewModel() {
    val darkTheme = getDarkTheme()
}
