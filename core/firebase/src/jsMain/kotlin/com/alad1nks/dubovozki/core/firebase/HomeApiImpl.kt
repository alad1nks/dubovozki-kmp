package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.FirebaseDatabaseReference.database
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

    private fun MutableStateFlow<List<String>>.addItemsEventListener(path: String) {
        val ref = FirebaseDatabase.ref(database, path)

        FirebaseDatabase.onValue(ref) { snapshot ->
            val data = snapshot.`val`()

            val list = mutableListOf<String>()

            if (data is Array<String>) {
                data.forEach { item -> list.add(item) }
            }

            value = list.toList()
        }
    }
}
