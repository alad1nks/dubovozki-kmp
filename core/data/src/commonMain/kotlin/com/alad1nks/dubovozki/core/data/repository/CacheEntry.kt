package com.alad1nks.dubovozki.core.data.repository

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class CacheEntry<T>(
    val value: T,
    val updatedAtEpochMillis: Long,
)

internal val cacheJson =
    Json {
        ignoreUnknownKeys = true
    }

internal inline fun <reified T> String.decodeCacheEntry(): CacheEntry<T>? =
    runCatching { cacheJson.decodeFromString<CacheEntry<T>>(this) }.getOrNull()

internal inline fun <reified T> CacheEntry<T>.encode(): String = cacheJson.encodeToString(this)
