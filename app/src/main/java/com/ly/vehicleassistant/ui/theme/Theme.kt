package com.ly.vehicleassistant.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = RoadGreen,
    onPrimary = WarmCanvas,
    primaryContainer = RoadGreenLight,
    onPrimaryContainer = RoadGreenDark,
    secondary = AttentionAmber,
    secondaryContainer = AttentionAmberLight,
    background = WarmCanvas,
    onBackground = Ink,
    surface = WarmCanvas,
    onSurface = Ink,
    surfaceVariant = WarmSurface,
    onSurfaceVariant = MutedInk,
    outlineVariant = SoftLine,
)

@Composable
fun VehicleassistantTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
