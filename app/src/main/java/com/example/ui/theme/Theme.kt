package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.AgeGroup
import com.example.data.GenderGroup

private val EmeraldDarkScheme = darkColorScheme(
    primary = EmeraldPrimaryDark,
    onPrimary = EmeraldOnPrimaryDark,
    primaryContainer = EmeraldContainerDark,
    onPrimaryContainer = EmeraldOnContainerDark,
    secondary = GoldSecondaryDark,
    onSecondary = GoldOnSecondaryDark,
    secondaryContainer = GoldContainerDark,
    onSecondaryContainer = GoldOnContainerDark,
    tertiary = SlateTertiaryDark,
    onTertiary = SlateOnTertiaryDark,
    tertiaryContainer = SlateContainerDark,
    onTertiaryContainer = SlateOnTertiaryContainerDark,
    background = DeepNightBackgroundDark,
    onBackground = NightOnBackgroundDark,
    surface = DeepOliveSurfaceDark,
    onSurface = NightOnSurfaceDark,
    surfaceVariant = DeepOliveVariantDark,
    onSurfaceVariant = NightMutedTextDark,
    outline = SubtleBorderDark,
    outlineVariant = SubtleBorderDark
)

private val EmeraldLightScheme = lightColorScheme(
    primary = EmeraldPrimaryLight,
    onPrimary = EmeraldOnPrimaryLight,
    primaryContainer = EmeraldContainerLight,
    onPrimaryContainer = EmeraldOnContainerLight,
    secondary = GoldSecondaryLight,
    onSecondary = GoldOnSecondaryLight,
    secondaryContainer = GoldContainerLight,
    onSecondaryContainer = GoldOnContainerLight,
    tertiary = SlateTertiaryLight,
    onTertiary = SlateOnTertiaryLight,
    tertiaryContainer = SlateContainerLight,
    onTertiaryContainer = SlateOnTertiaryLightContainer,
    background = OffWhiteBackgroundLight,
    onBackground = SlateOnBackgroundLight,
    surface = PureWhiteSurfaceLight,
    onSurface = SlateOnSurfaceLight,
    surfaceVariant = TileUncompletedLight,
    onSurfaceVariant = SlateMutedTextLight,
    outline = SubtleOutlineLight,
    outlineVariant = SubtleOutlineVariantLight
)

private val RosePinkDarkScheme = EmeraldDarkScheme.copy(
    primary = RosePrimaryDark,
    onPrimary = RoseOnPrimaryDark,
    primaryContainer = RoseContainerDark,
    onPrimaryContainer = RoseOnContainerDark,
    surface = RoseSurfaceDark
)

private val RosePinkLightScheme = EmeraldLightScheme.copy(
    primary = RosePrimaryLight,
    onPrimary = RoseOnPrimaryLight,
    primaryContainer = RoseContainerLight,
    onPrimaryContainer = RoseOnContainerLight,
    background = RoseBackgroundLight,
    surfaceVariant = RoseSurfaceVariantLight
)

private val SkyTealDarkScheme = EmeraldDarkScheme.copy(
    primary = TealPrimaryDark,
    onPrimary = TealOnPrimaryDark,
    primaryContainer = TealContainerDark,
    onPrimaryContainer = TealOnContainerDark,
    surface = TealSurfaceDark
)

private val SkyTealLightScheme = EmeraldLightScheme.copy(
    primary = TealPrimaryLight,
    onPrimary = TealOnPrimaryLight,
    primaryContainer = TealContainerLight,
    onPrimaryContainer = TealOnContainerLight
)

enum class PersonaPaletteStyle(val arabicTitle: String) {
    ROSE_BLUSH("الوردي الزهري الهادئ"),
    SKY_TEAL("السماوي المشرق"),
    ROYAL_EMERALD("الزمردي الملكي")
}

fun resolvePersonaPalette(
    genderGroup: GenderGroup,
    ageGroup: AgeGroup
): PersonaPaletteStyle {
    return when {
        genderGroup in setOf(GenderGroup.GIRLS, GenderGroup.YOUNG_WOMEN, GenderGroup.WOMEN) ->
            PersonaPaletteStyle.ROSE_BLUSH
        genderGroup == GenderGroup.BOYS ||
            (ageGroup in setOf(AgeGroup.CHILDREN, AgeGroup.YOUTH) && genderGroup == GenderGroup.UNSPECIFIED) ->
            PersonaPaletteStyle.SKY_TEAL
        else ->
            PersonaPaletteStyle.ROYAL_EMERALD
    }
}

@Composable
fun WirdTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    genderGroup: GenderGroup = GenderGroup.UNSPECIFIED,
    ageGroup: AgeGroup = AgeGroup.YOUNG_ADULTS,
    content: @Composable () -> Unit,
) {
    val style = resolvePersonaPalette(genderGroup, ageGroup)
    val colorScheme: ColorScheme = when (style) {
        PersonaPaletteStyle.ROSE_BLUSH -> if (darkTheme) RosePinkDarkScheme else RosePinkLightScheme
        PersonaPaletteStyle.SKY_TEAL -> if (darkTheme) SkyTealDarkScheme else SkyTealLightScheme
        PersonaPaletteStyle.ROYAL_EMERALD -> if (darkTheme) EmeraldDarkScheme else EmeraldLightScheme
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    WirdTheme(darkTheme = darkTheme, content = content)
}
