package com.example.core.designsystem.theme

import androidx.compose.ui.graphics.Color

/**
 * Central Design System Color Tokens for «وِرد» (Wird)
 * Built directly from the approved Visual Reference Poster and Wird_Blueprint_AR:
 * - Forest Green (#245B46 / #2E7D32)
 * - Sage Green (#4CAF7F / #8BAA91)
 * - Soft Sage Tint (#EBF5EC)
 * - Warm Ivory Background (#F7F5EE / #F7F9F7)
 * - Warm Pure White Surface (#FFFFFF)
 * - Deep Forest Text (#183B32) & Muted Sage-Gray Text (#64736A)
 */
object WirdColorTokens {
    // Light Mode Palette
    val ForestGreenPrimary = Color(0xFF245B46)
    val ForestGreenAction = Color(0xFF2E7D32)
    val SageGreenAccent = Color(0xFF4CAF7F)
    val SageGreenMuted = Color(0xFF8BAA91)
    val SoftSageContainer = Color(0xFFEBF5EC)
    val SoftSageChip = Color(0xFFE2EFE4)

    val WarmIvoryBackground = Color(0xFFF7F5EE)
    val SoftMintBackground = Color(0xFFF3F7F4)
    val WarmWhiteSurface = Color(0xFFFFFFFF)
    val SubtleSurfaceVariant = Color(0xFFEEF3EF)

    val TextPrimaryLight = Color(0xFF183B32)
    val TextSecondaryLight = Color(0xFF64736A)
    val BorderSubtleLight = Color(0xFFDCE6DF)
    val BorderFocusedLight = Color(0xFF8BAA91)

    // Warm Amber / Sand Accents for Charity & Heart Icons (as seen in reference poster)
    val WarmGoldAccent = Color(0xFFC89D54)
    val WarmGoldContainer = Color(0xFFFBF5E8)
    val WarmCoralHeart = Color(0xFFD98A6C)
    val WarmCoralContainer = Color(0xFFFAF0EC)

    // Dark Mode Palette (Serene Forest Night — matches the Dark Mode screen in reference poster)
    val DarkForestBackground = Color(0xFF0E1D17)
    val DarkForestSurface = Color(0xFF162A22)
    val DarkForestSurfaceVariant = Color(0xFF1F382E)
    val DarkSagePrimary = Color(0xFF68C393)
    val DarkSageOnPrimary = Color(0xFF08261B)
    val DarkSageContainer = Color(0xFF214235)
    val DarkSageOnContainer = Color(0xFFD8F3E3)

    val TextPrimaryDark = Color(0xFFECF4EF)
    val TextSecondaryDark = Color(0xFFA5B9AF)
    val BorderSubtleDark = Color(0xFF2A463B)

    // Semantic Status Colors
    val CompletedGreenLight = Color(0xFF2E7D32)
    val CompletedBgLight = Color(0xFFEBF5EC)
    val CompletedGreenDark = Color(0xFF68C393)
    val CompletedBgDark = Color(0xFF1F3D30)
}
