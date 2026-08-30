package com.alad1nks.dubovozki.core.domain

import com.alad1nks.dubovozki.core.model.Data
import com.alad1nks.dubovozki.core.model.Services
import kotlinx.coroutines.flow.Flow

interface GetServices {
    operator fun invoke(): Flow<Data<Services>>

    fun refresh()
}
