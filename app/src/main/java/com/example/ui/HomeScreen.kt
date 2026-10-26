package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AgeGroup
import com.example.data.ArabicDateUtils
import com.example.data.DailyDaySummary
import com.example.data.DailyRecordItemEntity
import com.example.data.DarkModePreference
import com.example.data.FivePrayer
import com.example.data.GenderGroup
import com.example.data.MotivationalLibrary
import com.example.data.MotivationalMessage
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.CompletedTileBgDark
import com.example.ui.theme.CompletedTileBgLight
import com.example.ui.theme.CompletedTileBorder
import com.example.ui.theme.EmeraldBrightAccent
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.SlateMutedTextDark
import com.example.ui.theme.SlateMutedTextLight
import com.example.ui.theme.UncompletedTileBgDark
import com.example.ui.theme.UncompletedTileBgLight
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun WirdHomeScreen(
    uiState: WirdUiState,
    onToggleDailyItem: (DailyRecordItemEntity) -> Unit,
    onToggleFivePrayersExpanded: () -> Unit = {},
    onToggleSinglePrayer: (DailyRecordItemEntity, FivePrayer) -> Unit = { _, _ -> },
    onSetAllFivePrayers: (DailyRecordItemEntity, Boolean) -> Unit = { _, _ -> },
    onCycleMotivation: () -> Unit = {},
    onFlashNotificationSent: (String, String) -> Unit = { _, _ -> },
    onOpenCounter: (String?) -> Unit,
    onCloseCounter: () -> Unit,
    onIncrementCounter: () -> Unit,
    onCycleCounterDhikr: () -> Unit = {},
    onRequestResetCounter: () -> Unit,
    onConfirmResetCounter: () -> Unit,
    onCancelResetCounter: () -> Unit,
    onCycleAdhkar: () -> Unit,
    onSelectDhikr: (com.example.data.SuggestedDhikr) -> Unit,
    onCloseDhikrReader: () -> Unit,
    onOpenCustomization: () -> Unit,
    onCloseCustomization: () -> Unit,
    onOpenHistory: () -> Unit,
    onCloseHistory: () -> Unit,
    onApplyPreset: (AgeGroup, GenderGroup, com.example.data.WirdIntensity) -> Unit,
    onDismissOnboarding: () -> Unit,
    onToggleTemplateItem: (com.example.data.TemplateItemEntity) -> Unit,
    onMoveTemplateItem: (com.example.data.TemplateItemEntity, Boolean) -> Unit,
    onReorderTemplateItems: (Int, Int) -> Unit = { _, _ -> },
    onAddCustomDeed: (String, String) -> Unit,
    onDeleteCustomDeed: (com.example.data.TemplateItemEntity) -> Unit,
    onCycleDarkMode: () -> Unit,
    onSetDarkMode: (DarkModePreference) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val systemDark = isSystemInDarkTheme()
    val isDark = when (uiState.darkModeEnum) {
        DarkModePreference.SYSTEM -> systemDark
        DarkModePreference.DARK -> true
        DarkModePreference.LIGHT -> false
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        val sent = NotificationHelper.sendFlashDhikrNotification(
            context = context,
            genderGroup = uiState.genderGroupEnum,
            index = uiState.overlay.motivationIndex
        )
        onFlashNotificationSent(sent.first, sent.second)
    }

    val triggerFlashNotification = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !NotificationHelper.hasNotificationPermission(context)
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            val sent = NotificationHelper.sendFlashDhikrNotification(
                context = context,
                genderGroup = uiState.genderGroupEnum,
                index = uiState.overlay.motivationIndex
            )
            onFlashNotificationSent(sent.first, sent.second)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            if (!uiState.overlay.isCounterOpen) {
                FloatingTasbihMiniFab(
                    counterValue = uiState.settings.counterValue,
                    onClick = { onOpenCounter(null) },
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
                    .testTag("home_lazy_column"),
                contentPadding = PaddingValues(
                    start = 14.dp,
                    end = 14.dp,
                    top = 8.dp,
                    bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Sleek Header + Persona Switcher + Compact Progress Bar
                item {
                    CompactWirdHeader(
                        gregorianDateText = uiState.gregorianDateText,
                        hijriDateText = uiState.hijriDateText,
                        greetingText = uiState.greetingText,
                        daySummary = uiState.todaySummary,
                        currentGender = uiState.genderGroupEnum,
                        currentAge = uiState.ageGroupEnum,
                        darkModePref = uiState.darkModeEnum,
                        onSelectPersona = { gender, age ->
                            onApplyPreset(age, gender, uiState.intensityEnum)
                        },
                        onOpenHistory = onOpenHistory,
                        onOpenCustomization = onOpenCustomization,
                        onCycleDarkMode = onCycleDarkMode
                    )
                }

                // 2. Directive [3]: Swipeable Glassmorphic Mindful Inspiration Card (<= 70.dp)
                item {
                    SwipeableMindfulInspirationCard(
                        motivation = uiState.currentMotivation,
                        isDark = isDark,
                        onSwipeOrTapNext = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onCycleMotivation()
                        },
                        onSendFlashNotification = triggerFlashNotification
                    )
                }

                // 3. Daily Schedule Section Header (Zero-Clutter)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "سجلات وِرد اليوم",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        TextButton(
                            onClick = onOpenCustomization,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("customize_schedule_inline_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("تخصيص", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // 4. Interactive Daily Items: Bento Prayer Bar + Zero-Clutter Whole-Row Clickable Deeds
                val todayItems = uiState.todaySummary?.items.orEmpty()
                val activePrayer = FivePrayer.activePrayerForHour(uiState.currentHourOfDay)

                items(todayItems, key = { it.id }) { item ->
                    if (item.isFivePrayers) {
                        PrayersTable(
                            item = item,
                            activePrayer = activePrayer,
                            isDark = isDark,
                            onToggleSinglePrayer = { prayer ->
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onToggleSinglePrayer(item, prayer)
                            },
                            onSetAllPrayers = { completed ->
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onSetAllFivePrayers(item, completed)
                            }
                        )
                    } else {
                        DailyDeedRowCard(
                            item = item,
                            isDark = isDark,
                            onToggle = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onToggleDailyItem(item)
                            }
                        )
                    }
                }

                // 5. Suggested Adhkar Section
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    SuggestedAdhkarSection(
                        suggestions = uiState.visibleAdhkarSuggestions,
                        onSelectDhikr = onSelectDhikr,
                        onOpenInCounter = { dhikr -> onOpenCounter(dhikr.categoryTitle) },
                        onCycleSuggestions = onCycleAdhkar
                    )
                }
            }

            // Visual Celebration Particle Burst when completing deeds or prayers
            if (uiState.overlay.celebrationBurstId > 0) {
                CelebrationSparkleOverlay(
                    burstId = uiState.overlay.celebrationBurstId,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Directive [4]: Half-Screen Bottom Sheet Modal with Circular Progress Ring
            ElectronicCounterPanel(
                visible = uiState.overlay.isCounterOpen,
                counterValue = uiState.settings.counterValue,
                activeDhikrTitle = uiState.settings.activeDhikrTitle,
                isResetConfirmVisible = uiState.overlay.isResetConfirmVisible,
                onIncrement = onIncrementCounter,
                onCycleDhikr = onCycleCounterDhikr,
                onRequestReset = onRequestResetCounter,
                onConfirmReset = onConfirmResetCounter,
                onCancelReset = onCancelResetCounter,
                onClose = onCloseCounter
            )
        }
    }

    // Dhikr Reader Dialog
    val activeDhikr = uiState.overlay.selectedDhikr
    if (activeDhikr != null) {
        DhikrReaderDialog(
            dhikr = activeDhikr,
            onDismiss = onCloseDhikrReader,
            onUseInCounter = { dhikr ->
                onOpenCounter(dhikr.categoryTitle)
            }
        )
    }

    // Customization Sheet Dialog
    if (uiState.overlay.isCustomizationOpen) {
        CustomizationSheetDialog(
            currentAge = uiState.ageGroupEnum,
            currentGender = uiState.genderGroupEnum,
            currentIntensity = uiState.intensityEnum,
            currentDarkMode = uiState.darkModeEnum,
            templateItems = uiState.templateItems,
            onApplyPreset = onApplyPreset,
            onChangeDarkMode = onSetDarkMode,
            onToggleTemplateItem = onToggleTemplateItem,
            onMoveTemplateItem = onMoveTemplateItem,
            onReorderTemplateItems = onReorderTemplateItems,
            onAddCustomDeed = onAddCustomDeed,
            onDeleteCustomDeed = onDeleteCustomDeed,
            onDismiss = onCloseCustomization
        )
    }

    // History Records Sheet Dialog
    if (uiState.overlay.isHistoryOpen) {
        HistoryRecordsSheetDialog(
            historySummaries = uiState.historySummaries,
            todayDateKey = uiState.todayDateKey,
            onDismiss = onCloseHistory
        )
    }
}

@Composable
private fun CompactWirdHeader(
    gregorianDateText: String,
    hijriDateText: String?,
    greetingText: String,
    daySummary: DailyDaySummary?,
    currentGender: GenderGroup,
    currentAge: AgeGroup,
    darkModePref: DarkModePreference,
    onSelectPersona: (GenderGroup, AgeGroup) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenCustomization: () -> Unit,
    onCycleDarkMode: () -> Unit
) {
    val completed = daySummary?.completedCount ?: 0
    val total = daySummary?.totalCount ?: 0
    val fraction = daySummary?.progressFraction ?: 0f
    val percent = daySummary?.progressPercent ?: 0
    val animatedProgress by animateFloatAsState(
        targetValue = fraction,
        animationSpec = spring(),
        label = "daily_progress_anim"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_header_section"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emblem, App Title & Dates
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("wird_emblem")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "وِرد",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "وِرد محاسبة",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "• $greetingText",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag("daily_greeting_text")
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (hijriDateText != null) {
                            Text(
                                text = hijriDateText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.testTag("hijri_date_text")
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = gregorianDateText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag("gregorian_date_text")
                        )
                    }
                }
            }

            // Compact Action Icons
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = onOpenHistory,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("history_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = "سجل الأيام السابقة",
                        modifier = Modifier.size(18.dp)
                    )
                }

                FilledTonalIconButton(
                    onClick = onOpenCustomization,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("customization_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = "إعدادات التخصيص",
                        modifier = Modifier.size(18.dp)
                    )
                }

                FilledTonalIconButton(
                    onClick = onCycleDarkMode,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (darkModePref == DarkModePreference.DARK) {
                            Icons.Outlined.LightMode
                        } else {
                            Icons.Outlined.DarkMode
                        },
                        contentDescription = "تبديل المظهر (${darkModePref.arabicLabel})",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Sleek 1-Line Persona & Progress Strip (Zero-Clutter: no system banner text!)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("daily_progress_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.testTag("persona_theme_selector_row"),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isFemaleSelected = MotivationalLibrary.isFemale(currentGender)
                        val isBoySelected = currentGender == GenderGroup.BOYS
                        val isMaleSelected = !isFemaleSelected && !isBoySelected

                        FilterChip(
                            selected = isMaleSelected,
                            onClick = { onSelectPersona(GenderGroup.YOUNG_MEN, AgeGroup.YOUNG_ADULTS) },
                            label = { Text("شاب / رجل", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier
                                .height(26.dp)
                                .testTag("persona_chip_male")
                        )

                        FilterChip(
                            selected = isFemaleSelected,
                            onClick = { onSelectPersona(GenderGroup.YOUNG_WOMEN, currentAge) },
                            label = { Text("فتاة / أنثى", style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier
                                .height(26.dp)
                                .testTag("persona_chip_female")
                        )

                        FilterChip(
                            selected = isBoySelected,
                            onClick = { onSelectPersona(GenderGroup.BOYS, AgeGroup.YOUTH) },
                            label = { Text("فتى / ناشئ", style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier
                                .height(26.dp)
                                .testTag("persona_chip_boy")
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "${ArabicDateUtils.toArabicDigits(completed)}/${ArabicDateUtils.toArabicDigits(total)}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("progress_summary_text")
                        )
                        Text(
                            text = "${ArabicDateUtils.toArabicDigits(percent)}٪",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.testTag("progress_percent_text")
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .testTag("daily_progress_bar"),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}

/**
 * Directive [3]: Mindful UX Copy Card — Translucent white glassmorphic card (<= 70.dp height)
 * with horizontal swipe gesture to cycle/dismiss smoothly.
 */
@Composable
private fun SwipeableMindfulInspirationCard(
    motivation: MotivationalMessage,
    isDark: Boolean,
    onSwipeOrTapNext: () -> Unit,
    onSendFlashNotification: () -> Unit
) {
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    val glassBg = if (isDark) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
    } else {
        Color.White.copy(alpha = 0.90f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 68.dp)
            .offset { IntOffset(dragOffsetX.roundToInt(), 0) }
            .pointerInput(motivation.id) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (abs(dragOffsetX) > 48f) {
                            onSwipeOrTapNext()
                        }
                        dragOffsetX = 0f
                    },
                    onDragCancel = { dragOffsetX = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        dragOffsetX = (dragOffsetX + dragAmount).coerceIn(-140f, 140f)
                    }
                )
            }
            .clip(RoundedCornerShape(14.dp))
            .clickable { onSwipeOrTapNext() }
            .testTag("motivational_card"),
        shape = RoundedCornerShape(14.dp),
        color = glassBg,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )

                AnimatedContent(
                    targetState = motivation.text,
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(180)) },
                    label = "mindful_copy_anim"
                ) { text ->
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("motivation_body_text")
                    )
                }
            }

            IconButton(
                onClick = onSendFlashNotification,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("send_flash_notification_button")
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = "إرسال إشعار تذكير خاطف",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Directive [2]: Horizontal Bento Prayer Bar (`PrayersTable`)
 * - Total height <= 90.dp
 * - Top header: "صلوات اليوم" on the right, "[ X / 5 ]" on the left
 * - 5 equal horizontal tiles in one row: [ فجر ] [ ظهر ] [ عصر ] [ مغرب ] [ عشاء ]
 * - Breathing Dot above the current active prayer
 * - Single tap on any tile toggles completion immediately with haptic feedback
 */
@Composable
fun PrayersTable(
    item: DailyRecordItemEntity,
    activePrayer: FivePrayer,
    isDark: Boolean,
    onToggleSinglePrayer: (FivePrayer) -> Unit,
    onSetAllPrayers: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = item.completedPrayersCount
    val infiniteTransition = rememberInfiniteTransition(label = "breathing_dot_transition")
    val breathingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_dot_alpha"
    )
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_dot_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 90.dp)
            .testTag("checklist_item_${item.templateCode}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (item.isCompleted) CompletedTileBorder else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("five_prayers_schedule_table"),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Top Row: "صلوات اليوم" + "[ X / 5 ]"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "صلوات اليوم",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (item.isCompleted) {
                        if (isDark) CompletedTileBgDark else CompletedTileBgLight
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSetAllPrayers(!item.isCompleted) }
                        .testTag("five_prayers_toggle_all_button")
                ) {
                    Text(
                        text = "[ ${ArabicDateUtils.toArabicDigits(completedCount)} / ٥ ]",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isCompleted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            // 5 Equal Horizontal Bento Tiles: [ فجر ] [ ظهر ] [ عصر ] [ مغرب ] [ عشاء ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FivePrayer.entries.forEach { prayer ->
                    val isCompleted = item.isPrayerCompleted(prayer)
                    val isActiveNow = prayer == activePrayer

                    val tileBg by animateColorAsState(
                        targetValue = when {
                            isCompleted && isDark -> CompletedTileBgDark
                            isCompleted && !isDark -> CompletedTileBgLight
                            !isCompleted && isDark -> UncompletedTileBgDark
                            else -> UncompletedTileBgLight
                        },
                        label = "bento_tile_bg_${prayer.code}"
                    )

                    val tileTextColor by animateColorAsState(
                        targetValue = when {
                            isCompleted && isDark -> Color(0xFFA7F3D0)
                            isCompleted && !isDark -> EmeraldPrimaryLight
                            isDark -> SlateMutedTextDark
                            else -> SlateMutedTextLight
                        },
                        label = "bento_tile_text_${prayer.code}"
                    )

                    val tileScale by animateFloatAsState(
                        targetValue = if (isCompleted) 1.02f else 1.0f,
                        animationSpec = spring(),
                        label = "bento_tile_scale_${prayer.code}"
                    )

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .scale(tileScale)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(
                                role = Role.Checkbox,
                                onClick = { onToggleSinglePrayer(prayer) }
                            )
                            .semantics {
                                contentDescription = "صلاة ${prayer.arabicName}"
                            }
                            .testTag("prayer_row_${prayer.code}"),
                        shape = RoundedCornerShape(10.dp),
                        color = tileBg,
                        border = BorderStroke(
                            width = if (isCompleted || isActiveNow) 1.dp else 0.5.dp,
                            color = when {
                                isCompleted -> CompletedTileBorder
                                isActiveNow -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.65f)
                                else -> Color.Transparent
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 2.dp, vertical = 3.dp)
                                .testTag("prayer_checkbox_${prayer.code}"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Breathing dot for current active prayer or subtle spacer
                            if (isActiveNow) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .scale(breathingScale)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCompleted) EmeraldBrightAccent.copy(alpha = breathingAlpha)
                                            else MaterialTheme.colorScheme.secondary.copy(alpha = breathingAlpha)
                                        )
                                        .testTag("breathing_dot_${prayer.code}")
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = prayer.shortTileLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isCompleted || isActiveNow) FontWeight.Bold else FontWeight.Medium,
                                    color = tileTextColor,
                                    maxLines = 1
                                )
                                if (isCompleted) {
                                    Text(
                                        text = "✓",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (isDark) EmeraldBrightAccent else EmeraldPrimaryLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Directive [1]: Zero-Clutter Deed Row
 * - Whole Row is Clickable
 * - No "غير مسجل" text badge
 * - Soft hollow gray circle when uncompleted -> glowing Emerald Green circle with white (✓) when completed
 */
@Composable
private fun DailyDeedRowCard(
    item: DailyRecordItemEntity,
    isDark: Boolean,
    onToggle: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (item.isCompleted) {
            if (isDark) CompletedTileBgDark.copy(alpha = 0.45f) else CompletedTileBgLight.copy(alpha = 0.65f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "deed_card_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (item.isCompleted) {
            CompletedTileBorder
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        label = "deed_card_border"
    )

    val indicatorScale by animateFloatAsState(
        targetValue = if (item.isCompleted) 1.08f else 1.0f,
        animationSpec = spring(),
        label = "deed_indicator_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                role = Role.Checkbox,
                onClick = onToggle
            )
            .semantics {
                contentDescription = item.title
            }
            .testTag("checklist_item_${item.templateCode}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (item.isCompleted) 1.2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.isCompleted) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconForDeedKey(item.iconKey),
                        contentDescription = null,
                        tint = if (item.isCompleted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (item.isCompleted) FontWeight.Bold else FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Soft Hollow Gray Circle -> Glowing Emerald Circle with White Checkmark (✓)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .scale(indicatorScale)
                    .then(
                        if (item.isCompleted) {
                            Modifier
                                .shadow(elevation = 4.dp, shape = CircleShape, spotColor = EmeraldBrightAccent)
                                .clip(CircleShape)
                                .background(EmeraldBrightAccent)
                        } else {
                            Modifier
                                .clip(CircleShape)
                                .border(
                                    width = 1.8.dp,
                                    color = if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1),
                                    shape = CircleShape
                                )
                        }
                    )
                    .testTag("checklist_checkbox_${item.templateCode}"),
                contentAlignment = Alignment.Center
            ) {
                if (item.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CelebrationSparkleOverlay(
    burstId: Int,
    modifier: Modifier = Modifier
) {
    val progress = remember(burstId) { Animatable(0f) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    LaunchedEffect(burstId) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
        )
    }

    if (progress.value < 1f) {
        Canvas(modifier = modifier.testTag("celebration_sparkle_canvas")) {
            val center = Offset(size.width * 0.5f, size.height * 0.28f)
            val maxRadius = size.minDimension * 0.42f
            val p = progress.value
            val alpha = (1f - p).coerceIn(0f, 1f)

            val particleCount = 16
            for (i in 0 until particleCount) {
                val angle = (i * (360.0 / particleCount)) * (Math.PI / 180.0)
                val dist = maxRadius * p
                val x = center.x + (cos(angle) * dist).toFloat()
                val y = center.y + (sin(angle) * dist).toFloat()
                val radius = (10f * (1f - p * 0.6f)).coerceAtLeast(2f)
                drawCircle(
                    color = if (i % 2 == 0) secondaryColor.copy(alpha = alpha) else primaryColor.copy(alpha = alpha),
                    radius = radius,
                    center = Offset(x, y)
                )
            }
        }
    }
}

private fun iconForDeedKey(key: String): ImageVector {
    return when (key) {
        "mosque" -> Icons.Default.Mosque
        "wb_sunny" -> Icons.Default.WbSunny
        "nights_stay" -> Icons.Default.NightsStay
        "spa" -> Icons.Default.Spa
        "favorite" -> Icons.Default.Favorite
        "auto_awesome" -> Icons.Default.AutoAwesome
        "groups" -> Icons.Default.Groups
        "bedtime" -> Icons.Default.Bedtime
        "light_mode" -> Icons.Default.LightMode
        "volunteer_activism" -> Icons.Default.VolunteerActivism
        "dark_mode" -> Icons.Default.DarkMode
        else -> Icons.Default.Star
    }
}
