package com.alad1nks.dubovozki.core.firebase

internal object FirebaseDatabaseReference {
    val databaseUrl: String =
        (
            System.getProperty("dubovozki.e2e.firebase.url")
                ?: System.getenv("DUBOVOZKI_FIREBASE_DATABASE_URL")
        )
            ?.takeIf { it.startsWith("http://127.0.0.1:") || it.startsWith("http://localhost:") }
            ?: "https://alad1nks-dubovozki-default-rtdb.europe-west1.firebasedatabase.app"
}
