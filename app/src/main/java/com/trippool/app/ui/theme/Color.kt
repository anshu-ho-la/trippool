package com.trippool.app.ui.theme

import androidx.compose.ui.graphics.Color

// Color tokens from docs/DESIGN.md section 2. Hex values are written only in this file.
// Screens use MaterialTheme.colorScheme, never these values directly.

// Light
val LightBackground = Color(0xFFFCFCFC)
val LightRaised = Color(0xFFF2F1EF)
val LightDivider = Color(0xFFE7E5E4)
val LightTextPrimary = Color(0xFF0C0A09)
val LightTextSecondary = Color(0xFF57534E)
val LightAccent = Color(0xFF0F766E)
val LightOnAccent = Color(0xFFFCFCFC)
val LightError = Color(0xFFB91C1C)

// Dark
val DarkBackground = Color(0xFF0C0A09)
val DarkRaised = Color(0xFF1C1917)
val DarkDivider = Color(0xFF292524)
val DarkTextPrimary = Color(0xFFF5F5F4)
val DarkTextSecondary = Color(0xFFA8A29E)
val DarkAccent = Color(0xFF2DD4BF)
val DarkOnAccent = Color(0xFF0C0A09)
val DarkError = Color(0xFFF87171)

// Same in both themes
val Outline = Color(0xFF78716C)

// Special cases from DESIGN.md: the QR code must be pure black on pure white (section 2),
// and the video duration label is a solid 60% black (section 4).
val QrForeground = Color(0xFF000000)
val QrBackground = Color(0xFFFFFFFF)
val VideoLabelBackground = Color(0x99000000)
