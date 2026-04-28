package com.alad1nks.dubovozki.core.storage.datastore

import okio.Path
import org.koin.core.scope.Scope

expect val Scope.PreferenceDataStoreProduceFile: Path
