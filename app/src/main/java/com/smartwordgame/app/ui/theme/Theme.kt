package com.smartwordgame.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection

private val SmartWordGameColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = White,
    primaryContainer = SkyBlue.copy(alpha = 0.15f),
    onPrimaryContainer = StrongBlue,
    secondary = Orange,
    onSecondary = White,
    secondaryContainer = Orange.copy(alpha = 0.15f),
    onSecondaryContainer = DarkText,
    tertiary = SuccessGreen,
    onTertiary = White,
    tertiaryContainer = SuccessGreen.copy(alpha = 0.15f),
    onTertiaryContainer = DarkText,
    error = ErrorRed,
    onError = White,
    background = SoftCream,
    onBackground = DarkText,
    surface = White,
    onSurface = DarkText,
    surfaceVariant = SoftCream,
    onSurfaceVariant = SecondaryText,
    outline = SkyBlue
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
