package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalStudioAccent = staticCompositionLocalOf { StudioGold }

private fun buildStudioColorScheme(accentTheme: String) = when (accentTheme) {
    "CYAN" -> darkColorScheme(
        primary = StudioCyan,
        onPrimary = StudioButtonText,
        primaryContainer = Color(0xFF0284C7),
        onPrimaryContainer = Color(0xFFE0F2FE),
        secondary = StudioGold,
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
    "EMERALD" -> darkColorScheme(
        primary = StudioEmerald,
        onPrimary = StudioButtonText,
        primaryContainer = Color(0xFF059669),
        onPrimaryContainer = Color(0xFFD1FAE5),
        secondary = StudioCyan,
        onSecondary = StudioButtonText,
        tertiary = StudioGold,
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
    else -> darkColorScheme(
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
}

fun accentColorForTheme(accentTheme: String): Color = when (accentTheme) {
    "CYAN" -> StudioCyan
    "EMERALD" -> StudioEmerald
    else -> StudioGold
}

@Composable
fun NovaHubTheme(
    accentTheme: String = "GOLD",
    content: @Composable () -> Unit
) {
    val accentColor = accentColorForTheme(accentTheme)
    val colorScheme = buildStudioColorScheme(accentTheme)

    CompositionLocalProvider(LocalStudioAccent provides accentColor) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
