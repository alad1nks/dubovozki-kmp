package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.model.Language
import kotlinx.coroutines.flow.Flow

interface GetLanguage {
    operator fun invoke(): Flow<Language>
}
