package com.alad1nks.dubovozki.core.firebase

import cocoapods.FirebaseDatabase.FIRDataEventType
import com.alad1nks.dubovozki.core.firebase.FirebaseDatabaseReference.databaseReference
import com.alad1nks.dubovozki.core.model.Data
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalForeignApi::class)
internal actual inline fun <reified T> MutableStateFlow<Data<T>>.addValueEventListener(
    pathString: String,
): FirebaseListenerRegistration {
    val reference = databaseReference.child(pathString)
    val handle =
        reference.observeEventType(
            eventType = FIRDataEventType.FIRDataEventTypeValue,
            withBlock = { snapshot ->
                val snapshotValue = snapshot?.value

                if (snapshotValue == null) {
                    value = Data.Error("Snapshot value is null")
                    return@observeEventType
                }

                val data = parse<T>(snapshotValue)

                if (data == null) {
                    value = Data.Error("Parse error")
                    return@observeEventType
                }

                value = Data.Success(data)
            },
            withCancelBlock = { error -> value = Data.Error(error?.localizedDescription) },
        )
    return FirebaseListenerRegistration { reference.removeObserverWithHandle(handle) }
}
