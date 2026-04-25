package com.smartbite.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SmartBiteColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = White,
    secondary = DarkOrange,
    onSecondary = White,
    background = White,
    onBackground = TextDark,
    surface = White,
    onSurface = TextDark,
    error = ErrorRed,
    onError = White
)

@Composable
fun SmartBiteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SmartBiteColorScheme,
        typography = SmartBiteTypography,
        content = content
    )
}
