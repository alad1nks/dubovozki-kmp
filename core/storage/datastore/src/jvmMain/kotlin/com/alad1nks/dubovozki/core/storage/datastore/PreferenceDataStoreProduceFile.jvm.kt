package com.alad1nks.dubovozki.core.storage.datastore

import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.scope.Scope
import java.io.File

actual val Scope.PreferenceDataStoreProduceFile: Path
    get() {
        val temporaryDirectory = File(System.getProperty("java.io.tmpdir")).canonicalFile
        val override = System.getProperty("dubovozki.e2e.datastore.path")?.let(::File)?.canonicalFile
        val storageFile =
            override?.takeIf { candidate ->
                candidate.toPath().startsWith(temporaryDirectory.toPath())
            } ?: File(temporaryDirectory, STORAGE_DATA_STORE_FILE_NAME)
        return storageFile.absolutePath.toPath()
    }
