package com.trippool.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Built from docs/DESIGN.md section 2. No dynamic color (wallpaper colors must not replace the palette).
//
// ASSUMPTION: DESIGN.md names only some Material slots. The slots it does not name (secondary, tertiary,
// the *Container slots, inverse*, errorContainer) are mapped to the nearest documented token so that no
// default Material purple leaks in. Review these mappings together with the palette.
//
// surfaceTint is transparent so surfaces never pick up an accent tint (DESIGN.md section 6: flat).

private val LightColorScheme = lightColorScheme(
    primary = LightAccent,
    onPrimary = LightOnAccent,
    primaryContainer = LightRaised,
    onPrimaryContainer = LightTextPrimary,
    secondary = LightTextSecondary,
    onSecondary = LightBackground,
    secondaryContainer = LightRaised,
    onSecondaryContainer = LightTextPrimary,
    tertiary = LightTextSecondary,
    onTertiary = LightBackground,
    tertiaryContainer = LightRaised,
    onTertiaryContainer = LightTextPrimary,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightBackground,
    onSurface = LightTextPrimary,
    surfaceVariant = LightRaised,
    onSurfaceVariant = LightTextSecondary,
    surfaceTint = Color.Transparent,
    inverseSurface = LightTextPrimary,
    inverseOnSurface = LightBackground,
    inversePrimary = DarkAccent,
    error = LightError,
    onError = LightBackground,
    errorContainer = LightRaised,
    onErrorContainer = LightError,
    outline = Outline,
    outlineVariant = LightDivider,
    surfaceBright = LightBackground,
    surfaceDim = LightRaised,
    surfaceContainerLowest = LightBackground,
    surfaceContainerLow = LightBackground,
    surfaceContainer = LightRaised,
    surfaceContainerHigh = LightRaised,
    surfaceContainerHighest = LightRaised
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkOnAccent,
    primaryContainer = DarkRaised,
    onPrimaryContainer = DarkTextPrimary,
    secondary = DarkTextSecondary,
    onSecondary = DarkBackground,
    secondaryContainer = DarkRaised,
    onSecondaryContainer = DarkTextPrimary,
    tertiary = DarkTextSecondary,
    onTertiary = DarkBackground,
    tertiaryContainer = DarkRaised,
    onTertiaryContainer = DarkTextPrimary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkBackground,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkRaised,
    onSurfaceVariant = DarkTextSecondary,
    surfaceTint = Color.Transparent,
    inverseSurface = DarkTextPrimary,
    inverseOnSurface = DarkBackground,
    inversePrimary = LightAccent,
    error = DarkError,
    onError = DarkBackground,
    errorContainer = DarkRaised,
    onErrorContainer = DarkError,
    outline = Outline,
    outlineVariant = DarkDivider,
    surfaceBright = DarkRaised,
    surfaceDim = DarkBackground,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkBackground,
    surfaceContainer = DarkRaised,
    surfaceContainerHigh = DarkRaised,
    surfaceContainerHighest = DarkRaised
)

@Composable
fun TrippoolTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
