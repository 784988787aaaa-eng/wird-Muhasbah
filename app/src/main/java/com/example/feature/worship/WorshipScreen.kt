package com.example.feature.worship

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.core.common.ArabicDateFormatter
import com.example.core.designsystem.components.WirdCard
import com.example.core.designsystem.components.WirdCompletionIndicator
import com.example.core.designsystem.components.WirdFilterChipRow
import com.example.core.designsystem.components.WirdPrimaryButton
import com.example.core.designsystem.theme.AmiriFontFamily
import com.example.core.designsystem.theme.LocalReduceMotion
import com.example.core.designsystem.theme.WirdColorTokens
import com.example.core.designsystem.theme.WirdDimensionTokens
import com.example.domain.model.ActivityCategory
import com.example.domain.model.ActivityDefinition
import com.example.domain.model.AuthenticDhikrItem
import com.example.domain.model.DailyActivityRecord
import com.example.domain.model.TasbihCounterState

enum class WorshipSubTab(val key: String, val arabicTitle: String) {
    TASBIH_AND_ADHKAR("TASBIH", "المسبحة والأذكار"),
    GOOD_DEEDS_AND_QURAN("DEEDS", "أعمال الخير والقرآن")
}

/**
 * Combines Screen 4 («المسبحة والأذكار») and Screen 5 («أعمال الخير») of the Visual Reference Poster
 * under the «عباداتي» navigation tab.
 */
@Composable
fun WorshipScreen(
    initialSubTab: WorshipSubTab = WorshipSubTab.TASBIH_AND_ADHKAR,
    tasbihState: TasbihCounterState,
    authenticAdhkar: List<AuthenticDhikrItem>,
    activityRecords: List<DailyActivityRecord>,
    useEasternDigits: Boolean,
    onIncrementTasbih: () -> Unit,
    onResetTasbih: () -> Unit,
    onCycleTasbihDhikr: () -> Unit,
    onSelectDhikrForTasbih: (AuthenticDhikrItem) -> Unit,
    onToggleActivity: (String) -> Unit,
    onAddCustomActivity: (String, String, ActivityCategory) -> Unit,
    onUpdateCustomActivity: (ActivityDefinition, String, String, ActivityCategory) -> Unit,
    onDeleteCustomActivity: (ActivityDefinition) -> Unit,
    onReorderActivity: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSubTab by remember(initialSubTab) { mutableStateOf(initialSubTab) }
    var selectedCategory by remember { mutableStateOf(ActivityCategory.ALL) }
    var isResetConfirmVisible by remember { mutableStateOf(false) }
    var selectedDhikrForReader by remember { mutableStateOf<AuthenticDhikrItem?>(null) }
    var showAddOrEditDialog by remember { mutableStateOf(false) }
    var editingDefinition by remember { mutableStateOf<ActivityDefinition?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("worship_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = WirdDimensionTokens.MaxContentWidth)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top Title + Sub-tab Switcher
            Text(
                text = if (selectedSubTab == WorshipSubTab.TASBIH_AND_ADHKAR) {
                    "المسبحة والأذكار"
                } else {
                    "أعمال الخير والقرآن"
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            WirdFilterChipRow(
                options = WorshipSubTab.entries.map { it.key to it.arabicTitle },
                selectedKey = selectedSubTab.key,
                onSelect = { key ->
                    selectedSubTab = WorshipSubTab.entries.first { it.key == key }
                },
                tagPrefix = "worship_subtab"
            )

            if (selectedSubTab == WorshipSubTab.TASBIH_AND_ADHKAR) {
                TasbihAndAdhkarContent(
                    tasbihState = tasbihState,
                    authenticAdhkar = authenticAdhkar,
                    activityRecords = activityRecords,
                    useEasternDigits = useEasternDigits,
                    isResetConfirmVisible = isResetConfirmVisible,
                    onIncrementTasbih = onIncrementTasbih,
                    onRequestReset = { isResetConfirmVisible = true },
                    onConfirmReset = {
                        onResetTasbih()
                        isResetConfirmVisible = false
                    },
                    onCancelReset = { isResetConfirmVisible = false },
                    onCycleDhikr = onCycleTasbihDhikr,
                    onSelectDhikrToRead = { selectedDhikrForReader = it },
                    onSelectDhikrForCounter = onSelectDhikrForTasbih,
                    onToggleAdhkarRecord = onToggleActivity
                )
            } else {
                GoodDeedsAndQuranContent(
                    activityRecords = activityRecords,
                    selectedCategory = selectedCategory,
                    onSelectCategory = { selectedCategory = it },
                    onToggleActivity = onToggleActivity,
                    onOpenAddCustomModal = {
                        editingDefinition = null
                        showAddOrEditDialog = true
                    },
                    onEditCustomActivity = { def ->
                        editingDefinition = def
                        showAddOrEditDialog = true
                    },
                    onDeleteCustomActivity = onDeleteCustomActivity,
                    onReorderActivity = onReorderActivity
                )
            }
        }
    }

    // Authentic Dhikr Reader Dialog
    val readerDhikr = selectedDhikrForReader
    if (readerDhikr != null) {
        AuthenticDhikrReaderDialog(
            dhikr = readerDhikr,
            onDismiss = { selectedDhikrForReader = null },
            onStartInTasbih = { item ->
                onSelectDhikrForTasbih(item)
                selectedDhikrForReader = null
            }
        )
    }

    // Add / Edit Custom Personal Deed Dialog
    if (showAddOrEditDialog) {
        CustomDeedEditorDialog(
            existing = editingDefinition,
            onDismiss = { showAddOrEditDialog = false },
            onSave = { title, subtitle, category ->
                val currentEditing = editingDefinition
                if (currentEditing == null) {
                    onAddCustomActivity(title, subtitle, category)
                } else {
                    onUpdateCustomActivity(currentEditing, title, subtitle, category)
                }
                showAddOrEditDialog = false
            }
        )
    }
}

@Composable
private fun TasbihAndAdhkarContent(
    tasbihState: TasbihCounterState,
    authenticAdhkar: List<AuthenticDhikrItem>,
    activityRecords: List<DailyActivityRecord>,
    useEasternDigits: Boolean,
    isResetConfirmVisible: Boolean,
    onIncrementTasbih: () -> Unit,
    onRequestReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onCancelReset: () -> Unit,
    onCycleDhikr: () -> Unit,
    onSelectDhikrToRead: (AuthenticDhikrItem) -> Unit,
    onSelectDhikrForCounter: (AuthenticDhikrItem) -> Unit,
    onToggleAdhkarRecord: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val reduceMotion = LocalReduceMotion.current

    val cycleTarget = tasbihState.targetCycle.coerceAtLeast(1)
    val cycleStep = tasbihState.currentCount % cycleTarget
    val progress = when {
        tasbihState.currentCount == 0 -> 0f
        cycleStep == 0 -> 1f
        else -> cycleStep.toFloat() / cycleTarget.toFloat()
    }
    val animatedSweep by animateFloatAsState(
        targetValue = progress * 360f,
        animationSpec = tween(
            durationMillis = if (reduceMotion) 0 else WirdDimensionTokens.MotionStandardMs,
            easing = FastOutSlowInEasing
        ),
        label = "tasbih_sweep"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("tasbih_lazy_column"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Electronic Tasbih Ring Card (Matches Screen 4 top half in Reference Poster)
        item {
            WirdCard(
                modifier = Modifier.testTag("electronic_tasbih_card")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Interactive Circular Tasbih Ring
                    val ringPrimary = MaterialTheme.colorScheme.primary
                    val ringSecondary = MaterialTheme.colorScheme.secondary
                    val ringTrack = MaterialTheme.colorScheme.surfaceVariant

                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .clip(CircleShape)
                            .clickable(
                                role = Role.Button,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onIncrementTasbih()
                                }
                            )
                            .semantics {
                                contentDescription = "حلقة التسبيح، العدد الحالي ${tasbihState.currentCount}"
                            }
                            .testTag("tasbih_ring_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            val strokeWidth = 14.dp.toPx()
                            val diameter = size.minDimension - strokeWidth
                            val topLeft = Offset(
                                (size.width - diameter) / 2f,
                                (size.height - diameter) / 2f
                            )
                            val arcSize = Size(diameter, diameter)

                            // Soft sage radial glow inside ring
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        ringSecondary.copy(alpha = 0.14f),
                                        Color.Transparent
                                    )
                                ),
                                radius = diameter / 2f
                            )

                            // Track ring
                            drawArc(
                                color = ringTrack,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )

                            // Active progress arc
                            if (animatedSweep > 0f) {
                                drawArc(
                                    brush = Brush.sweepGradient(
                                        colors = listOf(ringPrimary, ringSecondary, ringPrimary)
                                    ),
                                    startAngle = -90f,
                                    sweepAngle = animatedSweep,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            AnimatedContent(
                                targetState = tasbihState.currentCount,
                                transitionSpec = {
                                    fadeIn(tween(120)) togetherWith fadeOut(tween(100))
                                },
                                label = "tasbih_count_anim"
                            ) { count ->
                                Text(
                                    text = ArabicDateFormatter.formatNumber(count, useEasternDigits),
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.testTag("tasbih_count_text")
                                )
                            }

                            Text(
                                text = tasbihState.activeDhikrTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("tasbih_active_dhikr_title")
                            )

                            Text(
                                text = "الدورة: ${ArabicDateFormatter.formatNumber(cycleTarget, useEasternDigits)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Action pills row underneath ring: [+1] | [إعادة] | [تبديل الذكر] (Matches Reference Screen 4)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onIncrementTasbih()
                                }
                                .testTag("tasbih_plus_one_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "+1",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .height(48.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { onRequestReset() }
                                .testTag("tasbih_reset_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 18.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "إعادة",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "إعادة",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable { onCycleDhikr() }
                                .testTag("tasbih_switch_dhikr_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "تبديل الذكر",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Reset Confirmation Inline Box
                    AnimatedVisibility(visible = isResetConfirmVisible) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tasbih_reset_confirm_box")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "تصفير العدد الحالي؟",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(
                                        onClick = onCancelReset,
                                        modifier = Modifier.testTag("tasbih_cancel_reset_button")
                                    ) {
                                        Text("إلغاء")
                                    }
                                    Button(
                                        onClick = onConfirmReset,
                                        modifier = Modifier.testTag("tasbih_confirm_reset_button")
                                    ) {
                                        Text("تصفير")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Daily Morning / Evening Adhkar Quick Checklist
        val adhkarCheckItems = activityRecords.filter {
            it.definition.category == ActivityCategory.ADHKAR
        }
        if (adhkarCheckItems.isNotEmpty()) {
            item {
                Text(
                    text = "متابعة أوراد الأذكار اليومية",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            itemsIndexed(adhkarCheckItems, key = { _, it -> it.definition.code }) { _, record ->
                WirdCard(
                    onClick = { onToggleAdhkarRecord(record.definition.code) },
                    modifier = Modifier.testTag("adhkar_daily_item_${record.definition.code}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconForActivityKey(record.definition.iconKey),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = record.definition.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = record.definition.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        WirdCompletionIndicator(
                            isCompleted = record.isCompleted,
                            completedLabel = "تم ✓",
                            uncompletedLabel = "سجّل",
                            onClick = { onToggleAdhkarRecord(record.definition.code) }
                        )
                    }
                }
            }
        }

        // 3. Selected Authentic Adhkar Catalog («أذكار مختارة» — Matches Screen 4 bottom half)
        item {
            Text(
                text = "أذكار مختارة وموثقة",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        itemsIndexed(authenticAdhkar, key = { _, it -> it.id }) { _, dhikr ->
            WirdCard(
                onClick = { onSelectDhikrToRead(dhikr) },
                modifier = Modifier.testTag("selected_dhikr_card_${dhikr.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = dhikr.categoryTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = dhikr.shortText,
                                fontFamily = AmiriFontFamily,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = dhikr.documentedSource,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSelectDhikrForCounter(dhikr) }
                            .testTag("start_dhikr_counter_${dhikr.id}")
                    ) {
                        Text(
                            text = "+${ArabicDateFormatter.formatNumber(dhikr.recommendedCount, useEasternDigits)}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoodDeedsAndQuranContent(
    activityRecords: List<DailyActivityRecord>,
    selectedCategory: ActivityCategory,
    onSelectCategory: (ActivityCategory) -> Unit,
    onToggleActivity: (String) -> Unit,
    onOpenAddCustomModal: () -> Unit,
    onEditCustomActivity: (ActivityDefinition) -> Unit,
    onDeleteCustomActivity: (ActivityDefinition) -> Unit,
    onReorderActivity: (Int, Int) -> Unit
) {
    val filterCategories = listOf(
        ActivityCategory.ALL,
        ActivityCategory.SOCIAL,
        ActivityCategory.FAMILY,
        ActivityCategory.SELF,
        ActivityCategory.QURAN
    )

    val filteredRecords = remember(activityRecords, selectedCategory) {
        if (selectedCategory == ActivityCategory.ALL) {
            activityRecords
        } else {
            activityRecords.filter { it.definition.category == selectedCategory }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("good_deeds_lazy_column"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Filter Chips Bar (Matches Screen 5: الكل | الاجتماعي | الأسرة | الذات | القرآن)
        item {
            WirdFilterChipRow(
                options = filterCategories.map { it.key to it.arabicLabel },
                selectedKey = selectedCategory.key,
                onSelect = { key -> onSelectCategory(ActivityCategory.fromKey(key)) },
                tagPrefix = "deeds_category_filter"
            )
        }

        // Add Custom Personal Deed Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "قائمة الأعمال الاختيارية (${filteredRecords.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = onOpenAddCustomModal,
                    modifier = Modifier.testTag("open_add_custom_deed_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة عمل شخصي")
                }
            }
        }

        // Deeds Cards List (Matches Screen 5 of Visual Reference Poster)
        itemsIndexed(filteredRecords, key = { _, it -> it.definition.code }) { index, record ->
            val def = record.definition
            val isGoldAccent = def.code == "sadaqah" || def.category == ActivityCategory.FAMILY

            WirdCard(
                onClick = { onToggleActivity(def.code) },
                modifier = Modifier.testTag("good_deed_card_${def.code}")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isGoldAccent) WirdColorTokens.WarmGoldContainer
                                        else MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconForActivityKey(def.iconKey),
                                    contentDescription = null,
                                    tint = if (isGoldAccent) WirdColorTokens.WarmGoldAccent
                                    else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = def.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = def.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        WirdCompletionIndicator(
                            isCompleted = record.isCompleted,
                            completedLabel = "تم ✓",
                            uncompletedLabel = "سجّل",
                            onClick = { onToggleActivity(def.code) },
                            tag = "toggle_deed_${def.code}"
                        )
                    }

                    // Custom deed management & reordering controls
                    if (def.isCustom || selectedCategory == ActivityCategory.ALL) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedCategory == ActivityCategory.ALL) {
                                IconButton(
                                    onClick = { if (index > 0) onReorderActivity(index, index - 1) },
                                    enabled = index > 0,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("move_up_${def.code}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = "تحريك لأعلى",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        if (index < filteredRecords.size - 1) {
                                            onReorderActivity(index, index + 1)
                                        }
                                    },
                                    enabled = index < filteredRecords.size - 1,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("move_down_${def.code}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = "تحريك لأسفل",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            if (def.isCustom) {
                                IconButton(
                                    onClick = { onEditCustomActivity(def) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("edit_custom_deed_${def.code}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "تعديل العمل الشخصي",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { onDeleteCustomActivity(def) },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("delete_custom_deed_${def.code}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "حذف العمل الشخصي",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(16.dp)
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

@Composable
private fun AuthenticDhikrReaderDialog(
    dhikr: AuthenticDhikrItem,
    onDismiss: () -> Unit,
    onStartInTasbih: (AuthenticDhikrItem) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        WirdCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("authentic_dhikr_reader_dialog")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dhikr.categoryTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dhikr.fullArabicText,
                        fontFamily = AmiriFontFamily,
                        fontSize = 22.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Text(
                    text = "المصدر الموثق: ${dhikr.documentedSource}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = dhikr.documentedNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WirdPrimaryButton(
                        text = "البدء في المسبحة (${dhikr.recommendedCount})",
                        onClick = { onStartInTasbih(dhikr) },
                        modifier = Modifier.weight(1f),
                        tag = "dialog_start_tasbih_button"
                    )
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("إغلاق")
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomDeedEditorDialog(
    existing: ActivityDefinition?,
    onDismiss: () -> Unit,
    onSave: (String, String, ActivityCategory) -> Unit
) {
    var title by remember(existing) { mutableStateOf(existing?.title ?: "") }
    var subtitle by remember(existing) { mutableStateOf(existing?.subtitle ?: "") }
    var category by remember(existing) {
        mutableStateOf(existing?.category ?: ActivityCategory.SELF)
    }

    val selectableCategories = listOf(
        ActivityCategory.SELF,
        ActivityCategory.FAMILY,
        ActivityCategory.SOCIAL,
        ActivityCategory.QURAN,
        ActivityCategory.ADHKAR
    )

    Dialog(onDismissRequest = onDismiss) {
        WirdCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("custom_deed_editor_dialog")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (existing == null) "إضافة عمل شخصي جديد" else "تعديل العمل الشخصي",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("اسم العمل (مثال: ورد تدبر، زيارة مريض)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_deed_title_field")
                )

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("وصف مختصر (اختياري)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_deed_subtitle_field")
                )

                Text(
                    text = "التصنيف:",
                    style = MaterialTheme.typography.labelLarge
                )
                WirdFilterChipRow(
                    options = selectableCategories.map { it.key to it.arabicLabel },
                    selectedKey = category.key,
                    onSelect = { key -> category = ActivityCategory.fromKey(key) },
                    tagPrefix = "custom_deed_cat"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WirdPrimaryButton(
                        text = "حفظ",
                        enabled = title.isNotBlank(),
                        onClick = { onSave(title, subtitle, category) },
                        modifier = Modifier.weight(1f),
                        tag = "save_custom_deed_button"
                    )
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.heightIn(min = 48.dp)
                    ) {
                        Text("إلغاء")
                    }
                }
            }
        }
    }
}

fun iconForActivityKey(key: String): ImageVector {
    return when (key) {
        "mosque" -> Icons.Default.Mosque
        "family_heart" -> Icons.Default.Favorite
        "volunteer" -> Icons.Default.VolunteerActivism
        "charity_box" -> Icons.Default.VolunteerActivism
        "quran_book" -> Icons.Default.MenuBook
        "morning_sun" -> Icons.Default.WbSunny
        "evening_moon" -> Icons.Default.NightsStay
        "self_leaf" -> Icons.Default.Spa
        else -> Icons.Default.Star
    }
}
