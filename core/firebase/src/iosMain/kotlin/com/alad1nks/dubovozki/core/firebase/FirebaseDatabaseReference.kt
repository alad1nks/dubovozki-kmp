package com.alad1nks.dubovozki.core.firebase

import cocoapods.FirebaseDatabase.FIRDatabase
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
internal object FirebaseDatabaseReference {
    private val database = FIRDatabase.database().apply { persistenceEnabled = true }

    val databaseReference = database.reference()
}
