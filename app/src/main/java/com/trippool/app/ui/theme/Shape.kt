package com.trippool.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// One radius for the whole app: 8dp (docs/DESIGN.md section 5). Media tiles use 0dp and are set in the gallery code.
private val AppRadius = RoundedCornerShape(8.dp)

val Shapes = Shapes(
    extraSmall = AppRadius,
    small = AppRadius,
    medium = AppRadius,
    large = AppRadius,
    extraLarge = AppRadius
)
