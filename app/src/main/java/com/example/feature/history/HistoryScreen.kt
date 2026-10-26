package com.example.feature.history

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.common.ArabicDateFormatter
import com.example.core.designsystem.components.WirdCard
import com.example.core.designsystem.components.WirdCompletionIndicator
import com.example.core.designsystem.components.WirdEmptyStateCard
import com.example.core.designsystem.components.WirdFilterChipRow
import com.example.core.designsystem.theme.WirdDimensionTokens
import com.example.domain.model.DayHistorySummary
import com.example.domain.model.HistoryPeriodFilter
import com.example.domain.model.PrayerType
import com.example.feature.worship.iconForActivityKey
import java.time.LocalDate

/**
 * Matches Screen 6 of the Visual Reference Poster («سجل أعمالي / السجل اليومي»):
 * - Header: «سجل أعمالي» + full Arabic date of the selected day
 * - Period filter: يوم | أسبوع | شهر
 * - Horizontal Calendar Date Strip (17 مايو، 16 مايو، 15 مايو...)
 * - «ملخص اليوم» card with 3 descriptive columns: (صلوات X/5 | أعمال خير X/Y | أذكار X/Z)
 * - «تفاصيل اليوم» list showing recorded & unrecorded items with full support for correcting/editing past or current days
 * - Gentle empty state («لم تقم بأي أعمال بعد.. ابدأ بعملك الأول») when nothing is recorded
 */
@Composable
fun HistoryScreen(
    todayDate: LocalDate,
    selectedDateKey: String,
    summariesByDate: Map<String, DayHistorySummary>,
    useEasternDigits: Boolean,
    onSelectDateKey: (String) -> Unit,
    onTogglePrayerOnDate: (String, PrayerType) -> Unit,
    onOpenPrayerDetailOnDate: (String, PrayerType) -> Unit,
    onToggleActivityOnDate: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPeriod by remember { mutableStateOf(HistoryPeriodFilter.DAY) }

    val daysCountForStrip = when (selectedPeriod) {
        HistoryPeriodFilter.DAY -> 7
        HistoryPeriodFilter.WEEK -> 7
        HistoryPeriodFilter.MONTH -> 30
    }
    val dateWindow = remember(todayDate, daysCountForStrip) {
        ArabicDateFormatter.recentDateWindow(todayDate, daysCountForStrip).reversed()
    }

    val selectedLocalDate = remember(selectedDateKey, todayDate) {
        ArabicDateFormatter.parseDateKey(selectedDateKey, todayDate)
    }
    val activeDaySummary = summariesByDate[selectedDateKey] ?: DayHistorySummary(
        dateKey = selectedDateKey,
        prayersCompleted = 0,
        prayersTotal = 5,
        goodDeedsCompleted = 0,
        goodDeedsTotal = 5,
        adhkarCompleted = 0,
        adhkarTotal = 2
    )

    // Period aggregate for Week / Month view
    val periodSummaries = remember(dateWindow, summariesByDate) {
        dateWindow.mapNotNull { summariesByDate[ArabicDateFormatter.toDateKey(it)] }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("history_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = WirdDimensionTokens.MaxContentWidth)
                .testTag("history_lazy_column"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header («سجل أعمالي» + Date)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "سجل أعمالي",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = ArabicDateFormatter.formatGregorianArabic(selectedLocalDate, useEasternDigits),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("history_selected_date_label")
                    )
                }
            }

            // 2. Period Filter (يوم | أسبوع | شهر)
            item {
                WirdFilterChipRow(
                    options = HistoryPeriodFilter.entries.map { it.name to it.arabicLabel },
                    selectedKey = selectedPeriod.name,
                    onSelect = { key -> selectedPeriod = HistoryPeriodFilter.valueOf(key) },
                    tagPrefix = "history_period_filter"
                )
            }

            // 3. Horizontal Calendar Strip (Matches Screen 6: 17 مايو، 16 مايو...)
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_calendar_strip"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dateWindow, key = { ArabicDateFormatter.toDateKey(it) }) { date ->
                        val dKey = ArabicDateFormatter.toDateKey(date)
                        val isSelected = dKey == selectedDateKey
                        val summaryForTile = summariesByDate[dKey]
                        val hasActivity = summaryForTile?.hasAnyRecordedAction == true

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                color = when {
                                    isSelected -> MaterialTheme.colorScheme.primary
                                    hasActivity -> MaterialTheme.colorScheme.secondary
                                    else -> MaterialTheme.colorScheme.outlineVariant
                                }
                            ),
                            modifier = Modifier
                                .width(62.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectDateKey(dKey) }
                                .testTag("history_date_tile_$dKey")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = ArabicDateFormatter.formatNumber(date.dayOfMonth, useEasternDigits),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                Text(
                                    text = ArabicDateFormatter.formatMonthNameArabic(date),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Period Aggregate Banner when Week or Month is selected
            if (selectedPeriod != HistoryPeriodFilter.DAY) {
                item {
                    val activeDaysCount = periodSummaries.count { it.hasAnyRecordedAction }
                    val totalPrayersInPeriod = periodSummaries.sumOf { it.prayersCompleted }
                    val totalDeedsInPeriod = periodSummaries.sumOf { it.goodDeedsCompleted + it.adhkarCompleted }
                    val periodTitle = if (selectedPeriod == HistoryPeriodFilter.WEEK) "ملخص آخر 7 أيام" else "ملخص آخر 30 يومًا"

                    WirdCard(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        modifier = Modifier.testTag("history_period_summary_card")
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = periodTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "سجّلت في هذه الفترة $totalPrayersInPeriod صلاة و$totalDeedsInPeriod من الأذكار وأعمال الخير خلال $activeDaysCount أيام.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // 4. Day Summary Card («ملخص اليوم» — Matches Screen 6: صلوات 5/5 | أعمال خير 3/5 | أذكار 1/3)
            item {
                WirdCard(
                    modifier = Modifier.testTag("history_day_summary_card")
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "ملخص اليوم",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SummaryMetricColumn(
                                title = "صلوات",
                                completed = activeDaySummary.prayersCompleted,
                                total = activeDaySummary.prayersTotal,
                                useEasternDigits = useEasternDigits,
                                tag = "summary_metric_prayers"
                            )
                            SummaryMetricColumn(
                                title = "أعمال خير",
                                completed = activeDaySummary.goodDeedsCompleted,
                                total = activeDaySummary.goodDeedsTotal,
                                useEasternDigits = useEasternDigits,
                                tag = "summary_metric_deeds"
                            )
                            SummaryMetricColumn(
                                title = "أذكار",
                                completed = activeDaySummary.adhkarCompleted,
                                total = activeDaySummary.adhkarTotal,
                                useEasternDigits = useEasternDigits,
                                tag = "summary_metric_adhkar"
                            )
                        }
                    }
                }
            }

            // 5. Empty State or Day Details + Correction Controls («تفاصيل اليوم»)
            if (!activeDaySummary.hasAnyRecordedAction) {
                item {
                    WirdEmptyStateCard(
                        title = "لم تقم بتسجيل أعمال في هذا اليوم",
                        subtitle = "سيظهر سجلّك هنا عندما تبدأ بالتسجيل، أو يمكنك تصحيح وتسجيل ما أديته في هذا اليوم من القائمة أدناه.",
                        tag = "history_empty_day_card"
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تفاصيل اليوم (اضغط للتسجيل أو التصحيح)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Five Prayers on Selected Date (Allows correcting any prayer on that date)
            item {
                WirdCard(modifier = Modifier.testTag("history_prayers_correction_card")) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "الصلوات الخمس",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PrayerType.entries.forEach { prayerType ->
                                val log = activeDaySummary.prayerLogs.firstOrNull { it.prayerType == prayerType }
                                val completed = log?.isCompleted == true
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            onTogglePrayerOnDate(selectedDateKey, prayerType)
                                        }
                                        .padding(vertical = 6.dp)
                                        .testTag("history_prayer_toggle_${prayerType.code}")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (completed) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (completed) Icons.Default.Check else Icons.Default.Mosque,
                                            contentDescription = prayerType.arabicName,
                                            tint = if (completed) MaterialTheme.colorScheme.onPrimary
                                            else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Text(
                                        text = prayerType.arabicName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (completed) FontWeight.Bold else FontWeight.Normal,
                                        color = if (completed) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Activities & Good Deeds on Selected Date
            items(
                items = activeDaySummary.activityRecords,
                key = { "${selectedDateKey}_${it.definition.code}" }
            ) { record ->
                WirdCard(
                    onClick = { onToggleActivityOnDate(selectedDateKey, record.definition.code) },
                    modifier = Modifier.testTag("history_detail_row_${record.definition.code}")
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = iconForActivityKey(record.definition.iconKey),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = record.definition.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = record.definition.category.arabicLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        WirdCompletionIndicator(
                            isCompleted = record.isCompleted,
                            completedLabel = "تم ✓",
                            uncompletedLabel = "سجّل",
                            onClick = { onToggleActivityOnDate(selectedDateKey, record.definition.code) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryMetricColumn(
    title: String,
    completed: Int,
    total: Int,
    useEasternDigits: Boolean,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.testTag(tag)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${ArabicDateFormatter.formatNumber(completed, useEasternDigits)}/${ArabicDateFormatter.formatNumber(total, useEasternDigits)}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = if (completed > 0) "تم ✓" else "هادئ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
