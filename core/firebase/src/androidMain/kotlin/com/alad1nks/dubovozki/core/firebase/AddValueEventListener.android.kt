package com.alad1nks.dubovozki.core.firebase

import com.alad1nks.dubovozki.core.firebase.FirebaseDatabaseReference.databaseReference
import com.alad1nks.dubovozki.core.model.Data
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow

internal actual inline fun <reified T> MutableStateFlow<Data<T>>.addValueEventListener(
    pathString: String,
): FirebaseListenerRegistration {
    val reference = databaseReference.child(pathString)
    val postListener =
        object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(T::class.java)

                value = data?.let { Data.Success(it) } ?: Data.Error("Snapshot value is null")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                value = Data.Error(databaseError.message)
            }
        }

    reference.addValueEventListener(postListener)
    return FirebaseListenerRegistration { reference.removeEventListener(postListener) }
}
