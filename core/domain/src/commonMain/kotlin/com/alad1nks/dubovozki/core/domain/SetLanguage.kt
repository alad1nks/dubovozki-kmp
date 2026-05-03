package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.model.Language

interface SetLanguage {
    suspend operator fun invoke(value: Language)
}
