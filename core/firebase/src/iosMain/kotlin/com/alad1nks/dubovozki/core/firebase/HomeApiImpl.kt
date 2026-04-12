package com.alad1nks.dubovozki.core.firebase

import cocoapods.FirebaseDatabase.FIRDataEventType
import com.alad1nks.dubovozki.core.firebase.FirebaseDatabaseReference.database
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class HomeApiImpl : HomeApi {
    private val items: MutableStateFlow<List<String>> =
        MutableStateFlow(emptyList<String>())
            .apply { addItemsEventListener("home") }

    override fun getItems(): StateFlow<List<String>> {
        return items.asStateFlow()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun MutableStateFlow<List<String>>.addItemsEventListener(path: String) {
        database.child(path).observeEventType(
            eventType = FIRDataEventType.FIRDataEventTypeValue,
        ) { snapshot ->
            val list =
                when (val data = snapshot?.value) {
                    is List<*> -> data.filterIsInstance<String>()
                    is Map<*, *> -> data.values.filterIsInstance<String>()
                    else -> emptyList()
                }

            value = list
        }
    }
}
