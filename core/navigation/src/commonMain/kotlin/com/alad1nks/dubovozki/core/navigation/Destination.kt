package com.alad1nks.dubovozki.core.navigation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer

interface Destination

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : Destination> T.serialName(): String =
    serializer<T>().descriptor.serialName
