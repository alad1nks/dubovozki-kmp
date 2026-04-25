package com.alad1nks.dubovozki.feature.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightExtendedColorScheme =
    ExtendedColorScheme(
        odintsovo = Color.Black,
        slavyanskyBulvar = light_green,
        molodyozhnaya = light_blue,
    )

private val DarkExtendedColorScheme =
    ExtendedColorScheme(
        odintsovo = Color.White,
        slavyanskyBulvar = dark_green,
        molodyozhnaya = dark_blue,
    )

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val extendedColorScheme = if (darkTheme) DarkExtendedColorScheme else LightExtendedColorScheme

    CompositionLocalProvider(
        LocalExtendedColorScheme provides extendedColorScheme,
    ) {
        MaterialTheme(
            content = content,
        )
    }
}
