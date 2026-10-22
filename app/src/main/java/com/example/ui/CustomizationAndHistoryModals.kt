package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AgeGroup
import com.example.data.ArabicDateUtils
import com.example.data.DailyDaySummary
import com.example.data.DarkModePreference
import com.example.data.GenderGroup
import com.example.data.TemplateItemEntity
import com.example.data.WirdIntensity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InitialOnboardingCard(
    currentAge: AgeGroup,
    currentGender: GenderGroup,
    currentIntensity: WirdIntensity,
    onApplyPreset: (AgeGroup, GenderGroup, WirdIntensity) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAge by remember(currentAge) { mutableStateOf(currentAge) }
    var selectedGender by remember(currentGender) { mutableStateOf(currentGender) }
    var selectedIntensity by remember(currentIntensity) { mutableStateOf(currentIntensity) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("onboarding_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "إعداد قالب وِردك اليومي",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("dismiss_onboarding_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "إخفاء بطاقة الإعداد الأولي"
                    )
                }
            }

            Text(
                text = "اختر القالب الأنسب لوقتك؛ هذه المستويات تعبّر عن عدد الأعمال في الجدول اليومي، ولا تعبّر عن قيمة المستخدم أو صلاحه.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Age Group Selector
            Text(
                text = "الفئة العمرية:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AgeGroup.entries.forEach { age ->
                    FilterChip(
                        selected = selectedAge == age,
                        onClick = {
                            selectedAge = age
                            onApplyPreset(age, selectedGender, selectedIntensity)
                        },
                        label = { Text(age.arabicLabel) },
                        modifier = Modifier.testTag("onboarding_age_${age.name}")
                    )
                }
            }

            // Optional Gender/Group Selector
            Text(
                text = "تخصيص القالب (اختياري):",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                GenderGroup.entries.forEach { gender ->
                    FilterChip(
                        selected = selectedGender == gender,
                        onClick = {
                            selectedGender = gender
                            onApplyPreset(selectedAge, gender, selectedIntensity)
                        },
                        label = { Text(gender.arabicLabel) },
                        modifier = Modifier.testTag("onboarding_gender_${gender.name}")
                    )
                }
            }

            // Intensity Level Selector
            Text(
                text = "مستوى كثافة الورد:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                WirdIntensity.entries.forEach { intensity ->
                    FilterChip(
                        selected = selectedIntensity == intensity,
                        onClick = {
                            selectedIntensity = intensity
                            onApplyPreset(selectedAge, selectedGender, intensity)
                        },
                        label = { Text("${intensity.arabicLabel} (${intensity.badgeText})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("onboarding_intensity_${intensity.name}")
                    )
                }
            }

            Button(
                onClick = { onApplyPreset(selectedAge, selectedGender, selectedIntensity) },
                modifier = Modifier
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize()
                    .testTag("complete_onboarding_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("اعتماد القالب والبدء")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomizationSheetDialog(
    currentAge: AgeGroup,
    currentGender: GenderGroup,
    currentIntensity: WirdIntensity,
    currentDarkMode: DarkModePreference,
    templateItems: List<TemplateItemEntity>,
    onApplyPreset: (AgeGroup, GenderGroup, WirdIntensity) -> Unit,
    onChangeDarkMode: (DarkModePreference) -> Unit,
    onToggleTemplateItem: (TemplateItemEntity) -> Unit,
    onMoveTemplateItem: (TemplateItemEntity, Boolean) -> Unit,
    onAddCustomDeed: (String, String) -> Unit,
    onDeleteCustomDeed: (TemplateItemEntity) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    var selectedAge by remember(currentAge) { mutableStateOf(currentAge) }
    var selectedGender by remember(currentGender) { mutableStateOf(currentGender) }
    var selectedIntensity by remember(currentIntensity) { mutableStateOf(currentIntensity) }
    var customDeedTitle by remember { mutableStateOf("") }
    var customDeedSubtitle by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 560.dp)
                .fillMaxHeight(0.90f)
                .testTag("customization_sheet"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "تخصيص قالب الورد والأعمال",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "تُطبّق التعديلات بأمان دون تغيير سجلات الأيام السابقة.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("close_customization_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق الإعدادات"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Section 1: Theme Preference
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "المظهر والوضع الداكن",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    DarkModePreference.entries.forEach { mode ->
                                        FilterChip(
                                            selected = currentDarkMode == mode,
                                            onClick = { onChangeDarkMode(mode) },
                                            label = { Text(mode.arabicLabel) },
                                            modifier = Modifier.testTag("theme_mode_${mode.name}")
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section 2: Age, Gender & Intensity Presets
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "قوالب الفئة ومستوى الكثافة",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "هذه المستويات تعبر عن عدد الأعمال، ولا تعبر عن قيمة المستخدم أو صلاحه.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text("الفئة العمرية:", style = MaterialTheme.typography.labelLarge)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AgeGroup.entries.forEach { age ->
                                        FilterChip(
                                            selected = selectedAge == age,
                                            onClick = {
                                                selectedAge = age
                                                onApplyPreset(age, selectedGender, selectedIntensity)
                                            },
                                            label = { Text(age.arabicLabel) },
                                            modifier = Modifier.testTag("preset_age_${age.name}")
                                        )
                                    }
                                }

                                Text("تخصيص الفئة (اختياري):", style = MaterialTheme.typography.labelLarge)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    GenderGroup.entries.forEach { gender ->
                                        FilterChip(
                                            selected = selectedGender == gender,
                                            onClick = {
                                                selectedGender = gender
                                                onApplyPreset(selectedAge, gender, selectedIntensity)
                                            },
                                            label = { Text(gender.arabicLabel) },
                                            modifier = Modifier.testTag("preset_gender_${gender.name}")
                                        )
                                    }
                                }

                                Text("مستوى كثافة الورد:", style = MaterialTheme.typography.labelLarge)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    WirdIntensity.entries.forEach { intensity ->
                                        FilterChip(
                                            selected = selectedIntensity == intensity,
                                            onClick = {
                                                selectedIntensity = intensity
                                                onApplyPreset(selectedAge, selectedGender, intensity)
                                            },
                                            label = { Text("${intensity.arabicLabel} (${intensity.badgeText})") },
                                            modifier = Modifier.testTag("preset_intensity_${intensity.name}")
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section 3: Add Custom Personal Deed
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "إضافة عمل شخصي إلى الجدول",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                OutlinedTextField(
                                    value = customDeedTitle,
                                    onValueChange = { customDeedTitle = it },
                                    label = { Text("اسم العمل (مثال: صلة رحم، ذكر قبل النوم)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("custom_deed_title_input")
                                )
                                OutlinedTextField(
                                    value = customDeedSubtitle,
                                    onValueChange = { customDeedSubtitle = it },
                                    label = { Text("وصف مختصر (اختياري)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("custom_deed_subtitle_input")
                                )
                                Button(
                                    onClick = {
                                        if (customDeedTitle.isNotBlank()) {
                                            onAddCustomDeed(customDeedTitle, customDeedSubtitle)
                                            customDeedTitle = ""
                                            customDeedSubtitle = ""
                                        }
                                    },
                                    enabled = customDeedTitle.isNotBlank(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .minimumInteractiveComponentSize()
                                        .testTag("add_custom_deed_submit_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("إضافة العمل إلى القائمة")
                                }
                            }
                        }
                    }

                    // Section 4: Manage & Reorder Template Items
                    item {
                        Text(
                            text = "تفعيل وإخفاء وترتيب الأعمال (${templateItems.count { it.isEnabled }} مفعّلة):",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(templateItems, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("template_item_row_${item.code}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onMoveTemplateItem(item, true) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("template_up_${item.code}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowUpward,
                                            contentDescription = "تحريك لأعلى",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onMoveTemplateItem(item, false) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("template_down_${item.code}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDownward,
                                            contentDescription = "تحريك لأسفل",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    if (item.isCustom) {
                                        IconButton(
                                            onClick = { onDeleteCustomDeed(item) },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .testTag("template_delete_${item.code}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "حذف العمل الشخصي",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))
                                    Switch(
                                        checked = item.isEnabled,
                                        onCheckedChange = { onToggleTemplateItem(item) },
                                        modifier = Modifier.testTag("template_toggle_${item.code}")
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
fun HistoryRecordsSheetDialog(
    historySummaries: List<DailyDaySummary>,
    todayDateKey: String,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .widthIn(max = 560.dp)
                .fillMaxHeight(0.85f)
                .testTag("history_sheet"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "سجل الأيام المحفوظة",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "يُحفظ كل يوم بسجله المستقل دون مسح أو تعديل الأيام السابقة.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("close_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق سجل الأيام"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(historySummaries, key = { it.record.dateKey }) { daySummary ->
                        val localDate = ArabicDateUtils.parseDateKey(daySummary.record.dateKey)
                        val gregText = ArabicDateUtils.formatGregorianArabic(localDate)
                        val hijriText = ArabicDateUtils.formatHijriArabic(localDate)
                        val isToday = daySummary.record.dateKey == todayDateKey

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("history_day_card_${daySummary.record.dateKey}"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isToday) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = if (isToday) "اليوم • $gregText" else gregText,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (hijriText != null) {
                                            Text(
                                                text = "$hijriText • ${daySummary.record.templateSummary}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "${ArabicDateUtils.toArabicDigits(daySummary.completedCount)} من ${ArabicDateUtils.toArabicDigits(daySummary.totalCount)} مسجّلة",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                LinearProgressIndicator(
                                    progress = { daySummary.progressFraction },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                val recordedTitles = daySummary.items.mapNotNull { item ->
                                    when {
                                        item.isFivePrayers && item.completedPrayersCount in 1..4 -> {
                                            val names = item.completedPrayers.joinToString("، ") { it.arabicName }
                                            "${item.title} ($names)"
                                        }
                                        item.isCompleted -> item.title
                                        else -> null
                                    }
                                }
                                if (recordedTitles.isNotEmpty()) {
                                    Text(
                                        text = "الأعمال المسجّلة: ${recordedTitles.joinToString("، ")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                } else {
                                    Text(
                                        text = "لا توجد علامات مسجّلة في هذا السجل (عدم التسجيل لا يعني عدم الأداء).",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
