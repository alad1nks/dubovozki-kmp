package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.FirebaseDatabaseReference.DATABASE_URL
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class HomeApiImpl : HomeApi {
    private val client =
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }
    private val items: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList<String>())
            .apply { addItemsEventListener("home") }

    override fun getItems(): StateFlow<List<String>> {
        return items.asStateFlow()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun MutableStateFlow<List<String>>.addItemsEventListener(path: String) {
        GlobalScope.launch {
            try {
                val response: List<String> =
                    client.get("$DATABASE_URL/$path.json")
                        .body()

                value = response
            } catch (e: Exception) {
                println("Error: $e")
            }
        }
    }
}
