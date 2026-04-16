package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.FirebaseDatabaseReference.database
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json

private val json =
    Json {
        ignoreUnknownKeys = true
    }

internal actual inline fun <reified T> MutableStateFlow<Data<T>>.addValueEventListener(pathString: String) {
    val ref = FirebaseDatabase.ref(database, pathString)

    FirebaseDatabase.onValue(ref) { snapshot ->
        val snapshotValue = snapshot.`val`()
        val snapshotValueString = JSON.stringify(snapshotValue)
        val data = json.decodeFromString<T>(snapshotValueString)

        if (data == null) {
            value = Data.Error("Parse error")
            return@onValue
        }

        value = Data.Success(data)
    }
}
