package com.alad1nks.dubovozki.feature.services.model

internal sealed interface ServicesUiState {
    object Loading : ServicesUiState

    data class Error(
        val message: String?,
    ) : ServicesUiState

    data class Content(
        val contactLink: String?,
        val donutLink: String?,
        val updatedAtEpochMillis: Long?,
        val isStale: Boolean,
    ) : ServicesUiState
}
