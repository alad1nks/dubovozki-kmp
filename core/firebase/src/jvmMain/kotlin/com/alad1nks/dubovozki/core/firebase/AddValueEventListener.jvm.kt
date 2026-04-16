package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.FirebaseDatabaseReference.DATABASE_URL
import com.alad1nks.dubovozki.core.model.Data
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
internal actual inline fun <reified T> MutableStateFlow<Data<T>>.addValueEventListener(pathString: String) {
    GlobalScope.launch {
        try {
            val data: T =
                client.get("$DATABASE_URL/$pathString.json")
                    .body()

            value = Data.Success(data)
        } catch (e: Exception) {
            value = Data.Error(e.message)
        }
    }
}

private val client =
    HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
