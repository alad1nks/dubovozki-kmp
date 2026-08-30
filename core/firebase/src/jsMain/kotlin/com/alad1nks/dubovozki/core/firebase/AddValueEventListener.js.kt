package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.FirebaseDatabaseReference.database
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json

private val json =
    Json {
        ignoreUnknownKeys = true
    }

internal actual inline fun <reified T> MutableStateFlow<Data<T>>.addValueEventListener(
    pathString: String,
): FirebaseListenerRegistration {
    val ref = FirebaseDatabase.ref(database, pathString)

    val unsubscribe =
        FirebaseDatabase.onValue(
            ref = ref,
            callback = { snapshot ->
                try {
                    val snapshotValue = snapshot.`val`()
                    val snapshotValueString = JSON.stringify(snapshotValue)
                    value = Data.Success(json.decodeFromString<T>(snapshotValueString))
                } catch (e: Exception) {
                    value = Data.Error(e.message ?: "Parse error")
                }
            },
            cancelCallback = { error -> value = Data.Error(error.message as? String) },
        )
    return FirebaseListenerRegistration(unsubscribe)
}
