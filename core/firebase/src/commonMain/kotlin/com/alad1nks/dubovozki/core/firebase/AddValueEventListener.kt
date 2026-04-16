package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.MutableStateFlow

internal expect inline fun <reified T> MutableStateFlow<Data<T>>.addValueEventListener(pathString: String)
