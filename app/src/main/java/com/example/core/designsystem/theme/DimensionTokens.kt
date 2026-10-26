package com.example.core.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object WirdDimensionTokens {
    val SpaceXSmall = 4.dp
    val SpaceSmall = 8.dp
    val SpaceMedium = 12.dp
    val SpaceLarge = 16.dp
    val SpaceXLarge = 20.dp
    val SpaceXXLarge = 24.dp

    val CardCornerRadius = 20.dp
    val ButtonCornerRadius = 16.dp
    val ChipCornerRadius = 12.dp
    val PillCornerRadius = 999.dp

    val MinTouchTarget = 48.dp
    val MaxContentWidth = 600.dp

    // Motion durations (150-250ms per Blueprint)
    const val MotionFastMs = 150
    const val MotionStandardMs = 220
}

val WirdShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
