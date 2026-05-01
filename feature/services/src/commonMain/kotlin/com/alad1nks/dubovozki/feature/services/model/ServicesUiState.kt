package com.alad1nks.dubovozki.feature.services.model

internal sealed interface ServicesUiState {
    object Loading : ServicesUiState

    data class Content(
        val contactLink: String,
        val donutLink: String,
    ) : ServicesUiState
}
