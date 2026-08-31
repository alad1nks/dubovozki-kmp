package com.alad1nks.dubovozki.feature.designsystem

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

actual fun Modifier.e2eTestTag(tag: String): Modifier = testTag(tag)
