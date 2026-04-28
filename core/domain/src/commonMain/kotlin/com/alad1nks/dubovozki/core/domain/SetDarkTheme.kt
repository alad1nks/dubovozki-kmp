package com.alad1nks.dubovozki.core.domain

interface SetDarkTheme {
    suspend operator fun invoke(value: Boolean)
}
