package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.FirebaseDatabaseReference.databaseUrl
import com.alad1nks.dubovozki.core.model.Data
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

internal actual inline fun <reified T> MutableStateFlow<Data<T>>.addValueEventListener(
    pathString: String,
): FirebaseListenerRegistration {
    val job =
        firebaseScope.launch {
            try {
                val data: T =
                    client.get("$databaseUrl/$pathString.json")
                        .body()

                value = Data.Success(data)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                value = Data.Error(e.message)
            }
        }
    return FirebaseListenerRegistration { job.cancel() }
}

private val firebaseScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

private val client =
    HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
