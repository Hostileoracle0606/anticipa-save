package com.sentinelcloud.mobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = SentinelPrimary,
    onPrimary = SentinelOnPrimary,
    primaryContainer = SentinelPrimary.copy(alpha = 0.14f),
    onPrimaryContainer = SentinelOnPrimary,
    secondary = SentinelSecondary,
    onSecondary = SentinelOnSecondary,
    secondaryContainer = SentinelPrimaryBright.copy(alpha = 0.18f),
    onSecondaryContainer = SentinelOnSecondary,
    background = SentinelBackground,
    onBackground = Color(0xFF0E1F3D),
    surface = SentinelSurface,
    onSurface = Color(0xFF11213E),
    surfaceVariant = SentinelSurfaceVariant,
    onSurfaceVariant = SentinelMuted,
    outline = SentinelOutline,
    outlineVariant = SentinelSurfaceVariant,
    error = SentinelError,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = SentinelDarkPrimary,
    onPrimary = Color(0xFF061125),
    primaryContainer = SentinelDarkPrimary.copy(alpha = 0.2f),
    onPrimaryContainer = Color(0xFF02060D),
    secondary = SentinelDarkSecondary,
    onSecondary = Color(0xFF001C2F),
    secondaryContainer = SentinelDarkSurfaceVariant,
    onSecondaryContainer = Color(0xFFDEF6FF),
    background = SentinelDarkBackground,
    onBackground = Color(0xFFE8EDFF),
    surface = SentinelDarkSurface,
    onSurface = Color(0xFFE8EDFF),
    surfaceVariant = SentinelDarkSurfaceVariant,
    onSurfaceVariant = SentinelDarkMuted,
    outline = SentinelDarkOutline,
    outlineVariant = SentinelDarkOutline.copy(alpha = 0.6f),
    error = SentinelError,
    onError = Color.White
)

@Composable
fun SentinelCloudTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}

