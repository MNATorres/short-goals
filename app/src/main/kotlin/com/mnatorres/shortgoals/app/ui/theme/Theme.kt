package com.mnatorres.shortgoals.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TableroColors = darkColorScheme(
    primary = Amber,
    onPrimary = OnAmber,
    secondary = TextMuted,
    onSecondary = Background,
    background = Background,
    onBackground = TextPrimary,
    surface = Panel,
    onSurface = TextPrimary,
    surfaceVariant = PanelBorder,
    onSurfaceVariant = TextMuted,
    outline = ControlOutline,
    outlineVariant = PanelBorder,
)

@Composable
fun ShortGoalsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TableroColors,
        typography = TableroTypography,
        content = content,
    )
}
