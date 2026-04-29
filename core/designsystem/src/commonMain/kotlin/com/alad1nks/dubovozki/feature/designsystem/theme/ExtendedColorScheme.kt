package com.alad1nks.dubovozki.feature.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColorScheme(
    val busScheduleStationOdintsovo: Color = Color.Unspecified,
    val busScheduleStationSlavyanskyBulvar: Color = Color.Unspecified,
    val busScheduleStationMolodyozhnaya: Color = Color.Unspecified,
)

val LocalExtendedColorScheme = staticCompositionLocalOf { ExtendedColorScheme() }
