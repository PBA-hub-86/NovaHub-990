package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val StudioColorScheme = darkColorScheme(
    primary = StudioGold,
    onPrimary = StudioButtonText,
    primaryContainer = StudioGoldDark,
    onPrimaryContainer = StudioGoldLight,
    secondary = StudioCyan,
    onSecondary = StudioButtonText,
    tertiary = StudioEmerald,
    onTertiary = StudioButtonText,
    background = StudioDarkBg,
    onBackground = StudioTextPrimary,
    surface = StudioSurface,
    onSurface = StudioTextPrimary,
    surfaceVariant = StudioSurfaceElevated,
    onSurfaceVariant = StudioTextSecondary,
    outline = StudioCardBorder,
    outlineVariant = StudioCardBorderSubtle
)

@Composable
fun NovaHubTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = StudioColorScheme,
        typography = Typography,
        content = content
    )
}

