package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// [5] Color System Refactoring
// Light Theme: Off-White (#F8FAFC), Pure White Cards (#FFFFFF), Royal Emerald (#059669)
// Dark Spiritual Mode: Deep Night (#0B0F17), Deep Olive-Slate Cards (#1E2621), 8% Borders
// ============================================================================

// 1. Royal Emerald Palette (Light)
val EmeraldPrimaryLight = Color(0xFF059669)
val EmeraldAccentLight = Color(0xFF10B981)
val EmeraldOnPrimaryLight = Color(0xFFFFFFFF)
val EmeraldContainerLight = Color(0xFFECFDF5)
val EmeraldOnContainerLight = Color(0xFF064E3B)

val GoldSecondaryLight = Color(0xFFD97706)
val GoldOnSecondaryLight = Color(0xFFFFFFFF)
val GoldContainerLight = Color(0xFFFEF3C7)
val GoldOnContainerLight = Color(0xFF78350F)

val SlateTertiaryLight = Color(0xFF0D9488)
val SlateOnTertiaryLight = Color(0xFFFFFFFF)
val SlateContainerLight = Color(0xFFCCFBF1)
val SlateOnTertiaryLightContainer = Color(0xFF134E4A)

val OffWhiteBackgroundLight = Color(0xFFF8FAFC)
val SlateOnBackgroundLight = Color(0xFF0F172A)
val PureWhiteSurfaceLight = Color(0xFFFFFFFF)
val SlateOnSurfaceLight = Color(0xFF0F172A)
val TileUncompletedLight = Color(0xFFF1F5F9)
val SlateMutedTextLight = Color(0xFF64748B)
val SubtleOutlineLight = Color(0xFFE2E8F0)
val SubtleOutlineVariantLight = Color(0xFFF1F5F9)

// 2. Dark Spiritual Mode ("سواد الليل والزيتون العميق")
val EmeraldPrimaryDark = Color(0xFF10B981)
val EmeraldOnPrimaryDark = Color(0xFF022C22)
val EmeraldContainerDark = Color(0xFF064E3B)
val EmeraldOnContainerDark = Color(0xFFD1FAE5)

val GoldSecondaryDark = Color(0xFFFBBF24)
val GoldOnSecondaryDark = Color(0xFF451A03)
val GoldContainerDark = Color(0xFF3F2E0E)
val GoldOnContainerDark = Color(0xFFFDE68A)

val SlateTertiaryDark = Color(0xFF2DD4BF)
val SlateOnTertiaryDark = Color(0xFF042F2E)
val SlateContainerDark = Color(0xFF115E59)
val SlateOnTertiaryContainerDark = Color(0xFFCCFBF1)

val DeepNightBackgroundDark = Color(0xFF0B0F17)
val NightOnBackgroundDark = Color(0xFFF1F5F9)
val DeepOliveSurfaceDark = Color(0xFF1E2621)
val NightOnSurfaceDark = Color(0xFFF1F5F9)
val DeepOliveVariantDark = Color(0xFF161D19)
val NightMutedTextDark = Color(0xFF94A3B8)
val SubtleBorderDark = Color(0x14FFFFFF) // 8% white border

// 3. Soft Rose Palette (For Female Persona Option)
val RosePrimaryLight = Color(0xFFBE185D)
val RoseOnPrimaryLight = Color(0xFFFFFFFF)
val RoseContainerLight = Color(0xFFFDF2F8)
val RoseOnContainerLight = Color(0xFF831843)
val RoseSecondaryLight = Color(0xFFD97706)
val RoseBackgroundLight = Color(0xFFFCFAFB)
val RoseSurfaceVariantLight = Color(0xFFF8F0F4)

val RosePrimaryDark = Color(0xFFF472B6)
val RoseOnPrimaryDark = Color(0xFF500724)
val RoseContainerDark = Color(0xFF4A152C)
val RoseOnContainerDark = Color(0xFFFCE7F3)
val RoseSurfaceDark = Color(0xFF231C21)

// 4. Sky Teal Palette (For Youth/Boys Persona Option)
val TealPrimaryLight = Color(0xFF0284C7)
val TealOnPrimaryLight = Color(0xFFFFFFFF)
val TealContainerLight = Color(0xFFF0F9FF)
val TealOnContainerLight = Color(0xFF0C4A6E)

val TealPrimaryDark = Color(0xFF38BDF8)
val TealOnPrimaryDark = Color(0xFF082F49)
val TealContainerDark = Color(0xFF0C4A6E)
val TealOnContainerDark = Color(0xFFE0F2FE)
val TealSurfaceDark = Color(0xFF1A2329)

// 5. Semantic Bento Prayer Bar & Zero-Clutter Indicator Tokens
val EmeraldBrightAccent = EmeraldAccentLight
val CompletedTileBgLight = EmeraldContainerLight
val CompletedTileBgDark = EmeraldContainerDark
val CompletedTileBorder = EmeraldAccentLight
val UncompletedTileBgLight = TileUncompletedLight
val UncompletedTileBgDark = DeepOliveVariantDark
val SlateMutedTextDark = NightMutedTextDark
