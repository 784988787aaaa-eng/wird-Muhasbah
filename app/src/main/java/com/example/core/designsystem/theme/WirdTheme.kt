package com.example.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val WirdLightColorScheme = lightColorScheme(
    primary = WirdColorTokens.ForestGreenPrimary,
    onPrimary = WirdColorTokens.WarmWhiteSurface,
    primaryContainer = WirdColorTokens.SoftSageContainer,
    onPrimaryContainer = WirdColorTokens.TextPrimaryLight,
    secondary = WirdColorTokens.SageGreenAccent,
    onSecondary = WirdColorTokens.WarmWhiteSurface,
    secondaryContainer = WirdColorTokens.SoftSageChip,
    onSecondaryContainer = WirdColorTokens.TextPrimaryLight,
    tertiary = WirdColorTokens.WarmGoldAccent,
    onTertiary = WirdColorTokens.WarmWhiteSurface,
    tertiaryContainer = WirdColorTokens.WarmGoldContainer,
    onTertiaryContainer = WirdColorTokens.TextPrimaryLight,
    background = WirdColorTokens.WarmIvoryBackground,
    onBackground = WirdColorTokens.TextPrimaryLight,
    surface = WirdColorTokens.WarmWhiteSurface,
    onSurface = WirdColorTokens.TextPrimaryLight,
    surfaceVariant = WirdColorTokens.SubtleSurfaceVariant,
    onSurfaceVariant = WirdColorTokens.TextSecondaryLight,
    outline = WirdColorTokens.BorderFocusedLight,
    outlineVariant = WirdColorTokens.BorderSubtleLight
)

private val WirdDarkColorScheme = darkColorScheme(
    primary = WirdColorTokens.DarkSagePrimary,
    onPrimary = WirdColorTokens.DarkSageOnPrimary,
    primaryContainer = WirdColorTokens.DarkSageContainer,
    onPrimaryContainer = WirdColorTokens.DarkSageOnContainer,
    secondary = WirdColorTokens.SageGreenMuted,
    onSecondary = WirdColorTokens.DarkSageOnPrimary,
    secondaryContainer = WirdColorTokens.DarkForestSurfaceVariant,
    onSecondaryContainer = WirdColorTokens.TextPrimaryDark,
    tertiary = WirdColorTokens.WarmGoldAccent,
    onTertiary = WirdColorTokens.DarkSageOnPrimary,
    tertiaryContainer = WirdColorTokens.DarkForestSurfaceVariant,
    onTertiaryContainer = WirdColorTokens.TextPrimaryDark,
    background = WirdColorTokens.DarkForestBackground,
    onBackground = WirdColorTokens.TextPrimaryDark,
    surface = WirdColorTokens.DarkForestSurface,
    onSurface = WirdColorTokens.TextPrimaryDark,
    surfaceVariant = WirdColorTokens.DarkForestSurfaceVariant,
    onSurfaceVariant = WirdColorTokens.TextSecondaryDark,
    outline = WirdColorTokens.SageGreenMuted,
    outlineVariant = WirdColorTokens.BorderSubtleDark
)

val LocalReduceMotion = staticCompositionLocalOf { false }

@Composable
fun WirdTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) WirdDarkColorScheme else WirdLightColorScheme

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Rtl,
        LocalReduceMotion provides reduceMotion
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = WirdTypography,
            shapes = WirdShapes,
            content = content
        )
    }
}
