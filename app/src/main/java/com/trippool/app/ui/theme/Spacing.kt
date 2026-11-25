package com.trippool.app.ui.theme

import androidx.compose.ui.unit.dp

// Spacing tokens from docs/DESIGN.md section 5. Base unit 4dp. Screens use these, not raw dp numbers.
object Spacing {
    val Tiny = 4.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Standard = 16.dp
    val Large = 24.dp
    val XLarge = 32.dp
    val XXLarge = 48.dp

    val ScreenSidePadding = Standard
    val SectionGap = Large
    val MinTouchTarget = XXLarge
}
