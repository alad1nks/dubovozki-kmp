package com.alad1nks.dubovozki.core.model

sealed interface Data<T> {
    class Initial<T> : Data<T>

    data class Success<T>(
        val value: T,
    ) : Data<T>

    data class Error<T>(
        val message: String?,
    ) : Data<T>
}
