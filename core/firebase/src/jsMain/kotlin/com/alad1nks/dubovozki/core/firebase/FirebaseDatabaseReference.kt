package com.alad1nks.dubovozki.core.firebase

internal object FirebaseDatabaseReference {
    private val firebaseConfig: dynamic
        get() = js("window.firebaseConfig")

    private val app = FirebaseApp.initializeApp(firebaseConfig)

    val database = FirebaseDatabase.getDatabase(app)
}
