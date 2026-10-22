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
    tertiary = SageTertiaryDark,
    onTertiary = SageOnTertiaryDark,
    tertiaryContainer = SageContainerDark,
    onTertiaryContainer = SageOnContainerDark,
    background = NightBackgroundDark,
    onBackground = NightOnBackgroundDark,
    surface = NightSurfaceDark,
    onSurface = NightOnSurfaceDark,
    surfaceVariant = NightSurfaceVariantDark,
    onSurfaceVariant = NightOnSurfaceVariantDark,
    outline = NightOutlineDark,
    outlineVariant = NightOutlineVariantDark
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
    tertiary = SageTertiaryLight,
    onTertiary = SageOnTertiaryLight,
    tertiaryContainer = SageContainerLight,
    onTertiaryContainer = SageOnContainerLight,
    background = IvoryBackgroundLight,
    onBackground = IvoryOnBackgroundLight,
    surface = IvorySurfaceLight,
    onSurface = IvoryOnSurfaceLight,
    surfaceVariant = IvorySurfaceVariantLight,
    onSurfaceVariant = IvoryOnSurfaceVariantLight,
    outline = IvoryOutlineLight,
    outlineVariant = IvoryOutlineVariantLight
)

private val RosePinkDarkScheme = darkColorScheme(
    primary = RosePrimaryDark,
    onPrimary = RoseOnPrimaryDark,
    primaryContainer = RoseContainerDark,
    onPrimaryContainer = RoseOnContainerDark,
    secondary = RoseGoldSecondaryDark,
    onSecondary = RoseGoldOnSecondaryDark,
    secondaryContainer = RoseGoldContainerDark,
    onSecondaryContainer = RoseGoldOnContainerDark,
    tertiary = LavenderTertiaryDark,
    onTertiary = LavenderOnTertiaryDark,
    tertiaryContainer = LavenderContainerDark,
    onTertiaryContainer = LavenderOnContainerDark,
    background = BlushBackgroundDark,
    onBackground = BlushOnBackgroundDark,
    surface = BlushSurfaceDark,
    onSurface = BlushOnSurfaceDark,
    surfaceVariant = BlushSurfaceVariantDark,
    onSurfaceVariant = BlushOnSurfaceVariantDark,
    outline = BlushOutlineDark,
    outlineVariant = BlushOutlineVariantDark
)

private val RosePinkLightScheme = lightColorScheme(
    primary = RosePrimaryLight,
    onPrimary = RoseOnPrimaryLight,
    primaryContainer = RoseContainerLight,
    onPrimaryContainer = RoseOnContainerLight,
    secondary = RoseGoldSecondaryLight,
    onSecondary = RoseGoldOnSecondaryLight,
    secondaryContainer = RoseGoldContainerLight,
    onSecondaryContainer = RoseGoldOnContainerLight,
    tertiary = LavenderTertiaryLight,
    onTertiary = LavenderOnTertiaryLight,
    tertiaryContainer = LavenderContainerLight,
    onTertiaryContainer = LavenderOnContainerLight,
    background = BlushBackgroundLight,
    onBackground = BlushOnBackgroundLight,
    surface = BlushSurfaceLight,
    onSurface = BlushOnSurfaceLight,
    surfaceVariant = BlushSurfaceVariantLight,
    onSurfaceVariant = BlushOnSurfaceVariantLight,
    outline = BlushOutlineLight,
    outlineVariant = BlushOutlineVariantLight
)

private val SkyTealDarkScheme = darkColorScheme(
    primary = TealPrimaryDark,
    onPrimary = TealOnPrimaryDark,
    primaryContainer = TealContainerDark,
    onPrimaryContainer = TealOnContainerDark,
    secondary = AmberSecondaryDark,
    onSecondary = AmberOnSecondaryDark,
    secondaryContainer = AmberContainerDark,
    onSecondaryContainer = AmberOnContainerDark,
    tertiary = MintTertiaryDark,
    onTertiary = MintOnTertiaryDark,
    tertiaryContainer = MintContainerDark,
    onTertiaryContainer = MintOnContainerDark,
    background = SkyBackgroundDark,
    onBackground = SkyOnBackgroundDark,
    surface = SkySurfaceDark,
    onSurface = SkyOnSurfaceDark,
    surfaceVariant = SkySurfaceVariantDark,
    onSurfaceVariant = SkyOnSurfaceVariantDark,
    outline = SkyOutlineDark,
    outlineVariant = SkyOutlineVariantDark
)

private val SkyTealLightScheme = lightColorScheme(
    primary = TealPrimaryLight,
    onPrimary = TealOnPrimaryLight,
    primaryContainer = TealContainerLight,
    onPrimaryContainer = TealOnContainerLight,
    secondary = AmberSecondaryLight,
    onSecondary = AmberOnSecondaryLight,
    secondaryContainer = AmberContainerLight,
    onSecondaryContainer = AmberOnContainerLight,
    tertiary = MintTertiaryLight,
    onTertiary = MintOnTertiaryLight,
    tertiaryContainer = MintContainerLight,
    onTertiaryContainer = MintOnContainerLight,
    background = SkyBackgroundLight,
    onBackground = SkyOnBackgroundLight,
    surface = SkySurfaceLight,
    onSurface = SkyOnSurfaceLight,
    surfaceVariant = SkySurfaceVariantLight,
    onSurfaceVariant = SkyOnSurfaceVariantLight,
    outline = SkyOutlineLight,
    outlineVariant = SkyOutlineVariantLight
)

enum class PersonaPaletteStyle(val arabicTitle: String) {
    ROSE_BLUSH("الوردي الزهري الهادئ"),
    SKY_TEAL("السماوي المشرق"),
    ROYAL_EMERALD("الزمردي والذهبي")
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
