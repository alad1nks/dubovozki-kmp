package com.alad1nks.dubovozki

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.alad1nks.dubovozki.shared.ui.App

fun main() =
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Дубовозки",
        ) {
            App()
        }
    }
