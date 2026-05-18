package com.smartwordgame.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection

private val SmartWordGameColorScheme = lightColorScheme(
    primary = BrightBlue,
    onPrimary = White,
    secondary = BrightOrange,
    onSecondary = White,
    tertiary = BrightGreen,
    onTertiary = White,
    error = SoftRed,
    onError = White,
    background = WarmWhite,
    onBackground = DarkText,
    surface = White,
    onSurface = DarkText,
    surfaceVariant = WarmWhite,
    onSurfaceVariant = DarkText,
    outline = BrightBlue
)

@Composable
fun SmartWordGameTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = SmartWordGameColorScheme,
            typography = SmartWordGameTypography,
            content = content
        )
    }
}
