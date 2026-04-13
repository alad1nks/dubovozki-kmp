package com.alad1nks.dubovozki.core.firebase

import com.google.firebase.Firebase
import com.google.firebase.database.database

internal object FirebaseDatabaseReference {
    val database = Firebase.database.reference
}
