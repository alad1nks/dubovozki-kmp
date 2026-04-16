package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.FirebaseDatabaseReference.databaseReference
import com.alad1nks.dubovozki.core.model.Data
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.MutableStateFlow

internal actual inline fun <reified T> MutableStateFlow<Data<T>>.addValueEventListener(pathString: String) {
    val postListener =
        object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue<T>()

                data?.let { value = Data.Success(data) }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

    databaseReference.child(pathString).addValueEventListener(postListener)
}
