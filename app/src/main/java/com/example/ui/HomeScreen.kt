package com.example.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import kotlin.math.cos
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
    onAddCustomDeed: (String, String) -> Unit,
    onDeleteCustomDeed: (com.example.data.TemplateItemEntity) -> Unit,
    onCycleDarkMode: () -> Unit,
    onSetDarkMode: (DarkModePreference) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                ExtendedFloatingActionButton(
                    onClick = { onOpenCounter(null) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("open_counter_fab")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TouchApp,
                        contentDescription = "فتح العداد الإلكتروني"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val countBadge = if (uiState.settings.counterValue > 0) {
                        "العداد (${ArabicDateUtils.toArabicDigits(uiState.settings.counterValue)})"
                    } else {
                        "العداد الإلكتروني"
                    }
                    Text(
                        text = countBadge,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
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
                    bottom = 104.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Sleek Compact Header + Persona Theme Switcher
                item {
                    CompactWirdHeader(
                        gregorianDateText = uiState.gregorianDateText,
                        hijriDateText = uiState.hijriDateText,
                        greetingText = uiState.greetingText,
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

                // 2. Sleek Compact Progress + Motivational & Flash Reminder Card
                item {
                    CompactProgressAndMotivationCard(
                        daySummary = uiState.todaySummary,
                        motivation = uiState.currentMotivation,
                        recentFeedback = uiState.overlay.recentStatusBanner,
                        intensityLabel = uiState.intensityEnum.arabicLabel,
                        ageLabel = uiState.ageGroupEnum.arabicLabel,
                        onCycleMotivation = onCycleMotivation,
                        onSendFlashNotification = triggerFlashNotification
                    )
                }

                // 3. Compact Daily Schedule Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "سجلات وِرد اليوم",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "• نقرة واحدة للإنجاز أو التصحيح",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

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
                            Text("تخصيص / إضافة", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                // 4. Interactive Daily Accountability Items (Compact spacing so all records fit easily)
                val todayItems = uiState.todaySummary?.items.orEmpty()
                items(todayItems, key = { it.id }) { item ->
                    if (item.isFivePrayers) {
                        FivePrayersExpandableCard(
                            item = item,
                            isExpanded = uiState.overlay.isFivePrayersExpanded,
                            onToggleExpand = onToggleFivePrayersExpanded,
                            onToggleAll = { onToggleDailyItem(item) },
                            onToggleSinglePrayer = { prayer -> onToggleSinglePrayer(item, prayer) },
                            onSetAllPrayers = { completed -> onSetAllFivePrayers(item, completed) }
                        )
                    } else {
                        DailyDeedRowCard(
                            item = item,
                            onToggle = { onToggleDailyItem(item) }
                        )
                    }
                }

                // 5. Suggested Adhkar Section (3 static verified suggestions at a time)
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

            // Floating Electronic Counter Panel (bottom-aligned overlay without leaving Home Screen)
            ElectronicCounterPanel(
                visible = uiState.overlay.isCounterOpen,
                counterValue = uiState.settings.counterValue,
                activeDhikrTitle = uiState.settings.activeDhikrTitle,
                isResetConfirmVisible = uiState.overlay.isResetConfirmVisible,
                onIncrement = onIncrementCounter,
                onRequestReset = onRequestResetCounter,
                onConfirmReset = onConfirmResetCounter,
                onCancelReset = onCancelResetCounter,
                onClose = onCloseCounter,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
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

    // Customization Modal Dialog
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
            onAddCustomDeed = onAddCustomDeed,
            onDeleteCustomDeed = onDeleteCustomDeed,
            onDismiss = onCloseCustomization
        )
    }

    // History Records Modal Dialog
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
    currentGender: GenderGroup,
    currentAge: AgeGroup,
    darkModePref: DarkModePreference,
    onSelectPersona: (GenderGroup, AgeGroup) -> Unit,
    onOpenHistory: () -> Unit,
    onOpenCustomization: () -> Unit,
    onCycleDarkMode: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_header_section"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Compact Emblem, App Title & Dates
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

        // Sleek 1-Line Adaptive Persona & Color Theme Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("persona_theme_selector_row"),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isFemaleSelected = MotivationalLibrary.isFemale(currentGender)
            val isBoySelected = currentGender == GenderGroup.BOYS
            val isMaleSelected = !isFemaleSelected && !isBoySelected

            FilterChip(
                selected = isMaleSelected,
                onClick = { onSelectPersona(GenderGroup.YOUNG_MEN, AgeGroup.YOUNG_ADULTS) },
                label = { Text("شاب / رجل 🌿", style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier
                    .height(28.dp)
                    .testTag("persona_chip_male")
            )

            FilterChip(
                selected = isFemaleSelected,
                onClick = { onSelectPersona(GenderGroup.YOUNG_WOMEN, currentAge) },
                label = { Text("فتاة / أنثى 🌸", style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .height(28.dp)
                    .testTag("persona_chip_female")
            )

            FilterChip(
                selected = isBoySelected,
                onClick = { onSelectPersona(GenderGroup.BOYS, AgeGroup.YOUTH) },
                label = { Text("فتى / ناشئ 🌊", style = MaterialTheme.typography.labelSmall) },
                modifier = Modifier
                    .height(28.dp)
                    .testTag("persona_chip_boy")
            )
        }
    }
}

@Composable
private fun CompactProgressAndMotivationCard(
    daySummary: DailyDaySummary?,
    motivation: MotivationalMessage,
    recentFeedback: String?,
    intensityLabel: String,
    ageLabel: String,
    onCycleMotivation: () -> Unit,
    onSendFlashNotification: () -> Unit
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_progress_card"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Compact Top Row: Progress Summary + Percentage Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "إنجاز اليوم: ${ArabicDateUtils.toArabicDigits(completed)}/${ArabicDateUtils.toArabicDigits(total)}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.testTag("progress_summary_text")
                    )
                    Text(
                        text = "($intensityLabel • $ageLabel)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${ArabicDateUtils.toArabicDigits(percent)}٪",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                            .testTag("progress_percent_text")
                    )
                }
            }

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("daily_progress_bar"),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
            )

            // Integrated Motivational & Gentle Nudge Strip
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCycleMotivation() }
                    .testTag("motivational_card")
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
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
                                imageVector = Icons.Outlined.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = motivation.title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = motivation.kind.arabicBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onSendFlashNotification,
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("send_flash_notification_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "إرسال إشعار تذكير خاطف بالله",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            TextButton(
                                onClick = onCycleMotivation,
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .height(26.dp)
                                    .testTag("next_motivation_button")
                            ) {
                                Text("إلهام آخر ✨", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    Text(
                        text = motivation.text,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("motivation_body_text")
                    )

                    if (!recentFeedback.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.testTag("recent_feedback_banner")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = recentFeedback,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FivePrayersExpandableCard(
    item: DailyRecordItemEntity,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleAll: () -> Unit,
    onToggleSinglePrayer: (FivePrayer) -> Unit,
    onSetAllPrayers: (Boolean) -> Unit
) {
    val completedPrayers = item.completedPrayers
    val completedCount = completedPrayers.size
    val totalPrayers = FivePrayer.entries.size

    val containerColor by animateColorAsState(
        targetValue = if (item.isCompleted) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "five_prayers_card_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (item.isCompleted || isExpanded) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        label = "five_prayers_card_border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onToggleExpand)
            .semantics {
                contentDescription = "الصلوات الخمس، مسجل $completedCount من $totalPrayers، اضغط لفتح جدول الصلوات الخمس وتصحيح السجل"
            }
            .testTag("checklist_item_${item.templateCode}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (item.isCompleted || isExpanded) 1.5.dp else 1.dp, borderColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Compact Main Card Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (item.isCompleted) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mosque,
                            contentDescription = null,
                            tint = if (item.isCompleted) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "إخفاء جدول الصلوات" else "عرض جدول الصلوات الخمس",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        val subSummary = if (completedCount == 0) {
                            "اضغط لعرض جدول الصلوات: الفجر، الظهر، العصر، المغرب، العشاء"
                        } else {
                            "المسجّل: ${completedPrayers.joinToString("، ") { it.arabicName }}"
                        }
                        Text(
                            text = subSummary,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = if (item.isCompleted) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else if (completedCount > 0) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        val badgeLabel = when {
                            item.isCompleted -> "٥ / ٥ • تم التسجيل"
                            completedCount > 0 -> "${ArabicDateUtils.toArabicDigits(completedCount)} من ٥ مسجّلة"
                            else -> "جدول ٥ صلوات"
                        }
                        Text(
                            text = badgeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (item.isCompleted) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else if (completedCount > 0) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                .testTag("checklist_status_${item.templateCode}")
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable(
                                role = Role.Checkbox,
                                onClick = onToggleAll
                            )
                            .testTag("checklist_checkbox_${item.templateCode}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (item.isCompleted) {
                                Icons.Filled.CheckCircle
                            } else {
                                Icons.Outlined.RadioButtonUnchecked
                            },
                            contentDescription = if (item.isCompleted) {
                                "إلغاء علامة الصلوات الخمس"
                            } else {
                                "تسجيل الصلوات الخمس كاملة"
                            },
                            tint = if (item.isCompleted) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Expandable PrayersTable Component
            AnimatedVisibility(visible = isExpanded) {
                PrayersTable(
                    item = item,
                    onToggleSinglePrayer = onToggleSinglePrayer,
                    onSetAllPrayers = onSetAllPrayers
                )
            }
        }
    }
}

/**
 * Dedicated `PrayersTable` component displaying the 5 daily prayers
 * (الفجر، الظهر، العصر، المغرب، العشاء) in an organized list with Checkboxes
 * for recording completion and correcting each prayer individually.
 */
@Composable
fun PrayersTable(
    item: DailyRecordItemEntity,
    onToggleSinglePrayer: (FivePrayer) -> Unit,
    onSetAllPrayers: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("five_prayers_schedule_table"),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "جدول الصلوات الخمس (تسجيل وتصحيح كل صلاة):",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            TextButton(
                onClick = { onSetAllPrayers(!item.isCompleted) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                modifier = Modifier
                    .height(28.dp)
                    .testTag("five_prayers_toggle_all_button")
            ) {
                Text(
                    text = if (item.isCompleted) "إلغاء تحديد الكل" else "تسجيل الخمس كاملة",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        FivePrayer.entries.forEach { prayer ->
            val isPrayerChecked = item.isPrayerCompleted(prayer)
            val rowScale by animateFloatAsState(
                targetValue = if (isPrayerChecked) 1.01f else 1.0f,
                animationSpec = spring(),
                label = "prayer_row_scale_${prayer.code}"
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(rowScale)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(
                        role = Role.Checkbox,
                        onClick = { onToggleSinglePrayer(prayer) }
                    )
                    .testTag("prayer_row_${prayer.code}"),
                shape = RoundedCornerShape(10.dp),
                color = if (isPrayerChecked) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                } else {
                    MaterialTheme.colorScheme.surface
                },
                border = BorderStroke(
                    1.dp,
                    if (isPrayerChecked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = isPrayerChecked,
                            onCheckedChange = { onToggleSinglePrayer(prayer) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("prayer_checkbox_${prayer.code}")
                        )

                        Text(
                            text = "صلاة ${prayer.arabicName}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (isPrayerChecked) FontWeight.Bold else FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = if (isPrayerChecked) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isPrayerChecked) "تم التسجيل ✨" else "اضغط للتسجيل / التصحيح",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isPrayerChecked) {
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .testTag("prayer_status_${prayer.code}")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyDeedRowCard(
    item: DailyRecordItemEntity,
    onToggle: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (item.isCompleted) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "deed_card_bg"
    )

    val borderColor by animateColorAsState(
        targetValue = if (item.isCompleted) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.outlineVariant
        },
        label = "deed_card_border"
    )

    val cardScale by animateFloatAsState(
        targetValue = if (item.isCompleted) 1.01f else 1.0f,
        animationSpec = spring(),
        label = "deed_card_scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clip(RoundedCornerShape(14.dp))
            .clickable(
                role = Role.Checkbox,
                onClick = onToggle
            )
            .semantics {
                contentDescription = if (item.isCompleted) {
                    "${item.title}، تم التسجيل"
                } else {
                    "${item.title}، غير مسجل بعد"
                }
            }
            .testTag("checklist_item_${item.templateCode}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (item.isCompleted) 1.5.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (item.isCompleted) {
                                MaterialTheme.colorScheme.primary
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
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(19.dp)
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

            Spacer(modifier = Modifier.width(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = if (item.isCompleted) {
                        MaterialTheme.colorScheme.secondaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (item.isCompleted) "تم التسجيل ✨" else "غير مسجّل",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (item.isCompleted) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("checklist_status_${item.templateCode}")
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("checklist_checkbox_${item.templateCode}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (item.isCompleted) {
                            Icons.Filled.CheckCircle
                        } else {
                            Icons.Outlined.RadioButtonUnchecked
                        },
                        contentDescription = if (item.isCompleted) {
                            "إلغاء علامة ${item.title}"
                        } else {
                            "تسجيل إنجاز ${item.title}"
                        },
                        tint = if (item.isCompleted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        modifier = Modifier.size(24.dp)
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
