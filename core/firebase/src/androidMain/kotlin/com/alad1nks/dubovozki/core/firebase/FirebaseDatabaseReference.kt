package com.alad1nks.dubovozki.core.firebase

import com.google.firebase.Firebase
import com.google.firebase.database.database

internal object FirebaseDatabaseReference {
    private val database =
        Firebase.database.apply {
            setPersistenceEnabled(true)
            System.getProperty("dubovozki.e2e.firebase.host")?.let { host ->
                val port = System.getProperty("dubovozki.e2e.firebase.port")?.toIntOrNull() ?: 9000
                useEmulator(host, port)
            }
        }
    val databaseReference = database.reference
}
