package com.alad1nks.dubovozki.feature.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

@Composable
fun isTablet(): Boolean {
    return LocalWindowInfo.current.containerDpSize.width >= 600.dp
}
