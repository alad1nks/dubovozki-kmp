package com.alad1nks.dubovozki.core.firebase

import kotlinx.coroutines.flow.StateFlow

interface HomeApi {
    fun getItems(): StateFlow<List<String>>
}
