package com.alad1nks.dubovozki.feature.designsystem

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import platform.Foundation.NSProcessInfo

actual fun Modifier.e2eTestTag(tag: String): Modifier {
    val tagged = testTag(tag)
    return if (NSProcessInfo.processInfo.arguments.contains("--e2e")) {
        tagged.semantics { contentDescription = tag }
    } else {
        tagged
    }
}
