package com.alad1nks.dubovozki.core.firebase.model

import kotlinx.serialization.Serializable

@Serializable
data class ServicesResponse(
    val contactLink: String? = null,
    val donutLink: String? = null,
)
