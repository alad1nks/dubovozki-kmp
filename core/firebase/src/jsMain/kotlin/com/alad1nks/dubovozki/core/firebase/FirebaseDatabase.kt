package com.alad1nks.dubovozki.core.firebase

@JsModule("firebase/database")
@JsNonModule
internal external object FirebaseDatabase {
    fun getDatabase(app: dynamic): dynamic

    fun ref(db: dynamic, path: String): dynamic

    fun onValue(
        ref: dynamic,
        callback: (dynamic) -> Unit,
        cancelCallback: (dynamic) -> Unit,
    ): () -> Unit
}
