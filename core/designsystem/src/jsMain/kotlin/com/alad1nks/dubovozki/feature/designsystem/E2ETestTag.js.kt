package com.alad1nks.dubovozki.feature.designsystem

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

actual fun Modifier.e2eTestTag(tag: String): Modifier {
    val tagged = testTag(tag)
    val location = js("window.location")
    return if ((location.search as String).contains("e2e=true")) {
        tagged.semantics { contentDescription = tag }
    } else {
        tagged
    }
}
