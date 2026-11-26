package com.trippool.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Type tokens from docs/DESIGN.md section 3.

// ASSUMPTION: the IBM Plex font files are not in res/font yet (a human adds them, see DESIGN.md section 3),
// so the system fonts are used for now. When the files are added, change only these two lines.
val UiFontFamily: FontFamily = FontFamily.Default
val CodeFontFamily: FontFamily = FontFamily.Monospace

// Mapping of the DESIGN.md styles to Material slots (also written in DESIGN.md section 3):
//   Title -> titleLarge (top app bar titles) and headlineSmall (dialog titles),
//   Title large -> headlineMedium (trip name), Body -> bodyLarge, Body small -> bodyMedium,
//   Label -> labelLarge, Caption -> bodySmall

// DESIGN.md "Title": 20sp / 26, SemiBold.
private val TitleStyle = TextStyle(
    fontFamily = UiFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    letterSpacing = 0.sp
)

val Typography = Typography(
    headlineMedium = TextStyle(
        fontFamily = UiFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TitleStyle,
    titleLarge = TitleStyle,
    bodyLarge = TextStyle(
        fontFamily = UiFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = UiFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = UiFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp
    ),
    bodySmall = TextStyle(
        fontFamily = UiFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp
    )
)

// Join code only (DESIGN.md "Code" style): 22sp / 32, Medium, mono, letter spacing 0.5sp.
val CodeTextStyle = TextStyle(
    fontFamily = CodeFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 22.sp,
    lineHeight = 32.sp,
    letterSpacing = 0.5.sp
)

// Counts such as "12 of 40" use tabular figures so they don't jitter while syncing (DESIGN.md section 3).
val CountTextStyle = TextStyle(
    fontFamily = UiFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp,
    fontFeatureSettings = "tnum"
)
