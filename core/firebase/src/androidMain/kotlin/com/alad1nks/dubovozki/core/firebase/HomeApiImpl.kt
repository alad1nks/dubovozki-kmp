package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.FirebaseDatabaseReference.databaseReference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
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

    private inline fun <reified T> MutableStateFlow<T>.addItemsEventListener(pathString: String) {
        val postListener =
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val post = dataSnapshot.getValue<T>()

                    post?.let { value = post }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            }

        databaseReference.child(pathString).addValueEventListener(postListener)
    }
}
