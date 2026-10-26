package com.example.feature.settings

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.components.WirdCard
import com.example.core.designsystem.components.WirdFilterChipRow
import com.example.core.designsystem.components.WirdPrimaryButton
import com.example.core.designsystem.theme.WirdDimensionTokens
import com.example.domain.model.MessageToneStyle
import com.example.domain.model.ThemeModePreference
import com.example.domain.model.UserPreferences

/**
 * Matches Screen 7 of the Visual Reference Poster («الإعدادات / حسابي»):
 * - Header: «الإعدادات»
 * - Profile Card: «معلوماتي — تعديل الملف الشخصي المحلي»
 * - Settings List Rows:
 *   1. التنبيهات (opens RemindersScreen)
 *   2. المظهر (فاتح / داكن / تلقائي + أسلوب الرسائل + إظهار/إخفاء البطاقات)
 *   3. اللغة والتقويم (التاريخ الهجري والأرقام العربية/المشرقية)
 *   4. الخصوصية (بيانات محلية 100% + مسح السجل المحلي بتأكيد آمن)
 *   5. مساعدة ودعم
 *   6. عن التطبيق
 * - Bottom Quick Dark Mode Toggle Card («الوضع الداكن»)
 */
@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    onUpdateDisplayName: (String) -> Unit,
    onUpdateThemeMode: (ThemeModePreference) -> Unit,
    onUpdateMessageTone: (MessageToneStyle) -> Unit,
    onUpdateCardVisibility: (Boolean?, Boolean?, Boolean?) -> Unit,
    onUpdateHijriAndDigits: (Boolean?, Boolean?) -> Unit,
    onOpenReminders: () -> Unit,
    onClearAllLocalData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedSection by remember { mutableStateOf<String?>(null) }
    var nameDraft by remember(preferences.displayName) { mutableStateOf(preferences.displayName) }
    var showClearConfirm by remember { mutableStateOf(false) }

    val toggleSection: (String) -> Unit = { key ->
        expandedSection = if (expandedSection == key) null else key
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = WirdDimensionTokens.MaxContentWidth)
                .testTag("settings_lazy_column"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "الإعدادات",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }

            // 1. Profile Card («معلوماتي — تعديل الملف الشخصي»)
            item {
                WirdCard(
                    onClick = { toggleSection("PROFILE") },
                    modifier = Modifier.testTag("settings_profile_card")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = if (preferences.displayName.isNotBlank()) {
                                            "معلوماتي (${preferences.displayName})"
                                        } else {
                                            "معلوماتي"
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "تعديل الاسم الشخصي المحفوظ محليًا",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        AnimatedVisibility(visible = expandedSection == "PROFILE") {
                            Column(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = nameDraft,
                                    onValueChange = { nameDraft = it },
                                    label = { Text("الاسم أو الكنية في ترحيب الصفحة الرئيسية") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("settings_name_input")
                                )
                                WirdPrimaryButton(
                                    text = "حفظ الاسم",
                                    onClick = {
                                        onUpdateDisplayName(nameDraft)
                                        expandedSection = null
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    tag = "settings_save_name_button"
                                )
                            }
                        }
                    }
                }
            }

            // 2. Main Settings List Card (Matches Screen 7 of Reference Poster)
            item {
                WirdCard(contentPadding = PaddingValues(vertical = 6.dp)) {
                    // Row A: التنبيهات
                    SettingsMenuRow(
                        icon = Icons.Outlined.Notifications,
                        title = "التنبيهات",
                        subtitle = if (preferences.quietModeAllRemindersOff) "وضع السكون مفعّل" else "إدارة تذكيرات الصلاة والأذكار",
                        onClick = onOpenReminders,
                        tag = "settings_row_reminders"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Row B: المظهر وتخصيص البطاقات
                    SettingsMenuRow(
                        icon = Icons.Outlined.Palette,
                        title = "المظهر وتخصيص الواجهة",
                        subtitle = "${preferences.themeMode.arabicLabel} • نبرة ${preferences.messageTone.arabicLabel}",
                        onClick = { toggleSection("APPEARANCE") },
                        tag = "settings_row_appearance"
                    )
                    AnimatedVisibility(visible = expandedSection == "APPEARANCE") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("نظام الألوان:", style = MaterialTheme.typography.labelLarge)
                            WirdFilterChipRow(
                                options = ThemeModePreference.entries.map { it.name to it.arabicLabel },
                                selectedKey = preferences.themeMode.name,
                                onSelect = { onUpdateThemeMode(ThemeModePreference.valueOf(it)) },
                                tagPrefix = "settings_theme_chip"
                            )

                            Text("أسلوب ونبرة «رسالة لقلبك»:", style = MaterialTheme.typography.labelLarge)
                            WirdFilterChipRow(
                                options = MessageToneStyle.entries.map { it.name to it.arabicLabel },
                                selectedKey = preferences.messageTone.name,
                                onSelect = { onUpdateMessageTone(MessageToneStyle.valueOf(it)) },
                                tagPrefix = "settings_tone_chip"
                            )

                            Text("إظهار أو إخفاء بطاقات الصفحة الرئيسية:", style = MaterialTheme.typography.labelLarge)
                            ToggleSettingRow(
                                label = "بطاقة «رسالة لقلبك»",
                                checked = preferences.showHeartMessageCard,
                                onCheckedChange = { onUpdateCardVisibility(it, null, null) },
                                tag = "toggle_show_heart_card"
                            )
                            ToggleSettingRow(
                                label = "بطاقة «مساحة الخير»",
                                checked = preferences.showGoodnessSpaceCard,
                                onCheckedChange = { onUpdateCardVisibility(null, it, null) },
                                tag = "toggle_show_goodness_card"
                            )
                            ToggleSettingRow(
                                label = "بطاقة «لمحات من رحلتك»",
                                checked = preferences.showJourneyGlimpseCard,
                                onCheckedChange = { onUpdateCardVisibility(null, null, it) },
                                tag = "toggle_show_journey_card"
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Row C: اللغة والتقويم
                    SettingsMenuRow(
                        icon = Icons.Outlined.Language,
                        title = "اللغة والتقويم",
                        subtitle = "العربية (RTL) • خيارات التاريخ الهجري والأرقام",
                        onClick = { toggleSection("LANGUAGE") },
                        tag = "settings_row_language"
                    )
                    AnimatedVisibility(visible = expandedSection == "LANGUAGE") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ToggleSettingRow(
                                label = "عرض التاريخ الهجري (تقويم أم القرى)",
                                checked = preferences.showHijriDate,
                                onCheckedChange = { onUpdateHijriAndDigits(it, null) },
                                tag = "toggle_show_hijri"
                            )
                            ToggleSettingRow(
                                label = "استخدام الأرقام العربية المشرقية (١٢٣)",
                                checked = preferences.useEasternArabicDigits,
                                onCheckedChange = { onUpdateHijriAndDigits(null, it) },
                                tag = "toggle_eastern_digits"
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Row D: الخصوصية
                    SettingsMenuRow(
                        icon = Icons.Outlined.Lock,
                        title = "الخصوصية",
                        subtitle = "سجلك خاص ومحفوظ محليًا على جهازك فقط",
                        onClick = { toggleSection("PRIVACY") },
                        tag = "settings_row_privacy"
                    )
                    AnimatedVisibility(visible = expandedSection == "PRIVACY") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "تطبيق «وِرد» لا يجمع أي بيانات شخصية، ولا يشارك سجلات صلواتك أو أذكارك مع أي خادم خارجي. جميع البيانات مخزنة محليًا داخل جهازك.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!showClearConfirm) {
                                OutlinedButton(
                                    onClick = { showClearConfirm = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("request_clear_data_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DeleteSweep,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "مسح جميع السجلات اليومية المحفوظة",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "هل أنت متأكد من مسح جميع سجلات الصلوات والأعمال السابقة؟ لا يمكن التراجع عن هذا الإجراء.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                onClearAllLocalData()
                                                showClearConfirm = false
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.error
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("confirm_clear_data_button")
                                        ) {
                                            Text("نعم، مسح السجلات")
                                        }
                                        OutlinedButton(
                                            onClick = { showClearConfirm = false },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("إلغاء")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Row E: مساعدة ودعم
                    SettingsMenuRow(
                        icon = Icons.Outlined.HelpOutline,
                        title = "مساعدة ودعم",
                        subtitle = "إرشادات استخدام التطبيق وفلسفة الرفق",
                        onClick = { toggleSection("HELP") },
                        tag = "settings_row_help"
                    )
                    AnimatedVisibility(visible = expandedSection == "HELP") {
                        Text(
                            text = "صُمم «وِرد» ليكون رفيقًا هادئًا يعينك على متابعة ما تختاره من الصلوات والأذكار وأعمال الخير دون نقاط ضاغطة أو مقارنة. يمكنك دائمًا تصحيح أي سجل سابق من شاشة «سجلّي»، أو تخصيص أعمالك من شاشة «عباداتي».",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Row F: عن التطبيق
                    SettingsMenuRow(
                        icon = Icons.Outlined.Info,
                        title = "عن التطبيق",
                        subtitle = "وِرد — الإصدار 1.0",
                        onClick = { toggleSection("ABOUT") },
                        tag = "settings_row_about"
                    )
                    AnimatedVisibility(visible = expandedSection == "ABOUT") {
                        Text(
                            text = "«وِرد… رفيقك إلى الخير، برفقٍ لا يثقل قلبك».\nتطبيق عربي أصيل مبني بـ Kotlin وJetpack Compose وفق أعلى معايير الخصوصية والهدوء البصري.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            // 3. Quick Dark Mode Switch Card (Matches bottom of Screen 7 in Reference Poster)
            item {
                val isDarkSelected = preferences.themeMode == ThemeModePreference.DARK
                WirdCard(
                    modifier = Modifier.testTag("settings_dark_mode_quick_card")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DarkMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "الوضع الداكن",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Switch(
                            checked = isDarkSelected,
                            onCheckedChange = { enabled ->
                                onUpdateThemeMode(
                                    if (enabled) ThemeModePreference.DARK else ThemeModePreference.LIGHT
                                )
                            },
                            modifier = Modifier.testTag("dark_mode_quick_switch")
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun SettingsMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(tag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ToggleSettingRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    tag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(tag)
        )
    }
}
