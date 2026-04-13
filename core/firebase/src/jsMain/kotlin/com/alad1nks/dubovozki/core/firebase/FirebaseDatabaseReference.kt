package com.alad1nks.dubovozki.core.firebase

internal object FirebaseDatabaseReference {
    val firebaseConfig: dynamic
        get() = js("window.firebaseConfig")

    val app = FirebaseApp.initializeApp(firebaseConfig)

    val database = FirebaseDatabase.getDatabase(app)
}
