package com.alad1nks.dubovozki.core.firebase

@JsModule("firebase/app")
@JsNonModule
internal external object FirebaseApp {
    fun initializeApp(config: dynamic): dynamic
}
