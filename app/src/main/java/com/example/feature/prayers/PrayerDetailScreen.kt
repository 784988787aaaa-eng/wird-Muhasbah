package com.example.feature.prayers

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.components.MosqueBotanicalBackdrop
import com.example.core.designsystem.components.WirdCard
import com.example.core.designsystem.components.WirdFilterChipRow
import com.example.core.designsystem.components.WirdPrimaryButton
import com.example.core.designsystem.components.WirdSecondaryButton
import com.example.core.designsystem.theme.AmiriFontFamily
import com.example.core.designsystem.theme.WirdDimensionTokens
import com.example.domain.model.AuthenticDhikrItem
import com.example.domain.model.PrayerDayLog
import com.example.domain.model.PrayerPerformanceMode

/**
 * Matches Screen 3 of the Visual Reference Poster («تفاصيل الصلاة»):
 * - Header with Mosque & Botanical backdrop, Prayer Title (e.g. «صلاة الظهر»), and time hint («12:32»)
 * - Status badge («✓ تم تأكيد الصلاة»)
 * - Performance mode selector: جماعة | منفردًا | دون تحديد الكيفية
 * - Primary CTA button («تسجيل أداء الصلاة» / «حفظ التعديلات») + Undo/Unrecord button
 * - Options («الاختيارات»):
 *   1. أذكار بعد الصلاة (expands documented post-prayer adhkar + checkbox)
 *   2. ملاحظات وخواطر الصلاة (personal note saved locally)
 */
@Composable
fun PrayerDetailScreen(
    prayerLog: PrayerDayLog,
    postPrayerAdhkarList: List<AuthenticDhikrItem>,
    onSavePrayer: (
        isCompleted: Boolean,
        mode: PrayerPerformanceMode,
        postPrayerAdhkarDone: Boolean,
        note: String
    ) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    var selectedMode by remember(prayerLog) { mutableStateOf(prayerLog.performanceMode) }
    var postAdhkarDone by remember(prayerLog) { mutableStateOf(prayerLog.postPrayerAdhkarCompleted) }
    var noteText by remember(prayerLog) { mutableStateOf(prayerLog.personalNote) }
    var showAdhkarReader by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("prayer_detail_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = WirdDimensionTokens.MaxContentWidth)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Architectural & Botanical Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f))
            ) {
                MosqueBotanicalBackdrop(modifier = Modifier.fillMaxSize())

                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("prayer_detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "رجوع",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "صلاة ${prayerLog.prayerType.arabicName}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("prayer_detail_title")
                    )
                    Text(
                        text = prayerLog.prayerType.defaultTimeHint,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Confirmation Status Pill (Matches "✓ تم تأكيد الصلاة" card in Reference Screen 3)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (prayerLog.isCompleted) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                    },
                    border = BorderStroke(
                        1.dp,
                        if (prayerLog.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prayer_status_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (prayerLog.isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "تم تأكيد الصلاة (${prayerLog.performanceMode.arabicLabel})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "لم يتم تسجيل أداء هذه الصلاة بعد",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Performance Mode Selection (جماعة | منفردًا | دون تحديد)
                WirdCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "كيفية الأداء (اختياري):",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        WirdFilterChipRow(
                            options = PrayerPerformanceMode.entries.map { it.name to it.arabicLabel },
                            selectedKey = selectedMode.name,
                            onSelect = { key ->
                                selectedMode = PrayerPerformanceMode.valueOf(key)
                            },
                            tagPrefix = "prayer_mode_chip"
                        )
                    }
                }

                // Primary Save / Confirm Action Button
                WirdPrimaryButton(
                    text = if (prayerLog.isCompleted) "تحديث وحفظ تفاصيل الصلاة" else "تأكيد وتسجيل الصلاة",
                    leadingIcon = Icons.Default.Check,
                    onClick = {
                        onSavePrayer(true, selectedMode, postAdhkarDone, noteText)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    tag = "save_prayer_button"
                )

                // Undo / Unrecord option if already recorded (Correction support per Blueprint 7.1)
                if (prayerLog.isCompleted) {
                    WirdSecondaryButton(
                        text = "إلغاء تسجيل هذه الصلاة (تصحيح)",
                        leadingIcon = Icons.Default.Undo,
                        onClick = {
                            onSavePrayer(false, PrayerPerformanceMode.UNSPECIFIED, false, noteText)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        tag = "undo_prayer_button"
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Options Section («الاختيارات»)
                Text(
                    text = "الاختيارات المساندة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Option 1: Post-Prayer Adhkar («أذكار بعد الصلاة»)
                WirdCard(
                    onClick = { showAdhkarReader = !showAdhkarReader },
                    modifier = Modifier.testTag("post_prayer_adhkar_card")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "أذكار بعد الصلاة",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "اضغط لعرض الأذكار المأثورة دبر الصلاة المكتوبة",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Checkbox(
                                checked = postAdhkarDone,
                                onCheckedChange = { postAdhkarDone = it },
                                modifier = Modifier.testTag("post_prayer_adhkar_checkbox")
                            )
                        }

                        AnimatedVisibility(visible = showAdhkarReader) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                postPrayerAdhkarList.forEach { dhikr ->
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = dhikr.fullArabicText,
                                                fontFamily = AmiriFontFamily,
                                                fontSize = 18.sp,
                                                lineHeight = 30.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            Text(
                                                text = "${dhikr.documentedSource} • ${dhikr.documentedNote}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Option 2: Personal Note («ملاحظات»)
                WirdCard(modifier = Modifier.testTag("prayer_note_card")) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "ملاحظات",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "خاطرة شخصية أو دعاء خاص بك (يُحفظ محليًا فقط)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            placeholder = { Text("اكتب ملاحظتك الاختيارية هنا...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("prayer_note_input"),
                            minLines = 2,
                            maxLines = 4
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    onSavePrayer(
                                        prayerLog.isCompleted,
                                        selectedMode,
                                        postAdhkarDone,
                                        noteText
                                    )
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("save_note_only_button")
                            ) {
                                Text("حفظ الملاحظة")
                            }
                        }
                    }
                }
            }
        }
    }
}
