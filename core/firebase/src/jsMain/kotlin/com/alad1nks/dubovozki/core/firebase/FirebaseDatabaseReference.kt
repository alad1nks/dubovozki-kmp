package com.alad1nks.dubovozki.core.firebase

internal object FirebaseDatabaseReference {
    private val firebaseConfig: dynamic
        get() = js("window.firebaseConfig")

    private val app = FirebaseApp.initializeApp(firebaseConfig)

    val database = FirebaseDatabase.getDatabase(app)

    init {
        val location = js("window.location")
        if ((location.search as String).contains("e2e=true")) {
            FirebaseDatabase.connectDatabaseEmulator(database, location.hostname as String, 9000)
        }
    }
}
