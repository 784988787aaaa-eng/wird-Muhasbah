package com.example.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.core.common.ArabicDateFormatter
import com.example.core.designsystem.components.WirdCard
import com.example.core.designsystem.components.WirdCompletionIndicator
import com.example.core.designsystem.components.WirdThreeLeafLogo
import com.example.core.designsystem.theme.AmiriFontFamily
import com.example.core.designsystem.theme.LocalReduceMotion
import com.example.core.designsystem.theme.WirdColorTokens
import com.example.core.designsystem.theme.WirdDimensionTokens
import com.example.domain.model.DailyActivityRecord
import com.example.domain.model.DailyPrayersSummary
import com.example.domain.model.HeartMessage
import com.example.domain.model.JourneyWeeklyGlimpse
import com.example.domain.model.PrayerPerformanceMode
import com.example.domain.model.PrayerType
import com.example.domain.model.UserPreferences
import com.example.feature.onboarding.ReturnAfterPauseBannerCard

/**
 * HomeScreen («الصفحة الرئيسية») — Built directly to match Screen 2 of the Visual Reference Poster
 * and Section 6 of Wird_Blueprint_AR:
 * 1. رأس اليوم (Day Header: Greeting, Local Date, Optional Hijri with source, Settings button, Three-leaf badge)
 * 2. بطاقة صلوات يومك + مؤشر اليوم الهادئ (5 prayers row + progress bar + calm descriptive note)
 * 3. رسالة لقلبك (Heart Message card with non-repeating "رسالة أخرى" button)
 * 4. مساحة الخير (1-3 optional good deeds / adhkar / Quran suggestions with undoable completion)
 * 5. لمحات من رحلتك (Non-judgmental weekly summary)
 */
@Composable
fun HomeScreen(
    gregorianDateText: String,
    hijriDateText: String?,
    greetingText: String,
    preferences: UserPreferences,
    prayersSummary: DailyPrayersSummary,
    activeHeartMessage: HeartMessage,
    goodnessSuggestions: List<DailyActivityRecord>,
    allActivitiesCompletedCount: Int,
    allActivitiesTotalCount: Int,
    weeklyGlimpse: JourneyWeeklyGlimpse,
    showReturnAfterPauseBanner: Boolean,
    statusFeedbackMessage: String?,
    onOpenPrayerDetail: (PrayerType) -> Unit,
    onQuickTogglePrayer: (PrayerType) -> Unit,
    onNextHeartMessage: () -> Unit,
    onToggleActivity: (String) -> Unit,
    onOpenAllGoodDeeds: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onAcknowledgeReturnBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reduceMotion = LocalReduceMotion.current
    val useEastern = preferences.useEasternArabicDigits

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = WirdDimensionTokens.MaxContentWidth)
                .testTag("home_lazy_column"),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Day Header («رأس اليوم»)
            item {
                HomeDayHeader(
                    displayName = preferences.displayName,
                    greetingText = greetingText,
                    gregorianDateText = gregorianDateText,
                    hijriDateText = if (preferences.showHijriDate) hijriDateText else null,
                    onOpenSettings = onOpenSettings
                )
            }

            // Optional Return After Pause gentle welcome card
            if (showReturnAfterPauseBanner) {
                item {
                    ReturnAfterPauseBannerCard(
                        onStartMyDay = onAcknowledgeReturnBanner,
                        onReviewMyHistory = {
                            onAcknowledgeReturnBanner()
                            onOpenHistory()
                        }
                    )
                }
            }

            // Gentle status feedback banner after saving/updating a prayer or deed
            if (!statusFeedbackMessage.isNullOrBlank()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("calm_feedback_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = statusFeedbackMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // 2. Daily Prayers Card + Calm Daily Indicator («صلوات اليوم»)
            item {
                DailyPrayersCard(
                    prayersSummary = prayersSummary,
                    useEasternDigits = useEastern,
                    onSelectPrayerForDetail = onOpenPrayerDetail,
                    onQuickTogglePrayer = onQuickTogglePrayer
                )
            }

            // 3. Heart Message Card («رسالة لقلبك / رسالة اليوم»)
            if (preferences.showHeartMessageCard) {
                item {
                    HeartMessageCard(
                        message = activeHeartMessage,
                        reduceMotion = reduceMotion,
                        onNextMessage = onNextHeartMessage
                    )
                }
            }

            // 4. Goodness Space Card («مساحة الخير / أعمال الخير»)
            if (preferences.showGoodnessSpaceCard) {
                item {
                    GoodnessSpaceCard(
                        suggestions = goodnessSuggestions,
                        completedCount = allActivitiesCompletedCount,
                        totalCount = allActivitiesTotalCount,
                        useEasternDigits = useEastern,
                        onToggleActivity = onToggleActivity,
                        onOpenAllGoodDeeds = onOpenAllGoodDeeds
                    )
                }
            }

            // 5. Journey Glimpse Card («لمحات من رحلتك»)
            if (preferences.showJourneyGlimpseCard) {
                item {
                    JourneyGlimpseCard(
                        weeklyGlimpse = weeklyGlimpse,
                        useEasternDigits = useEastern,
                        onOpenHistory = onOpenHistory
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeDayHeader(
    displayName: String,
    greetingText: String,
    gregorianDateText: String,
    hijriDateText: String?,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("home_header_section"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Right side (in RTL): Greeting & Date
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "السلام عليك • $greetingText",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.testTag("daily_greeting_text")
            )
            Text(
                text = if (displayName.isNotBlank()) "أهلًا بك، $displayName" else "أهلًا بك",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = gregorianDateText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("gregorian_date_text")
            )
            if (hijriDateText != null) {
                Text(
                    text = hijriDateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("hijri_date_text")
                )
            }
        }

        // Left side (in RTL): Soft sage botanical emblem & Settings button (matching Reference Screen 2)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .size(48.dp)
                    .testTag("wird_emblem")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    WirdThreeLeafLogo(leafSize = 28.dp, showTitle = false)
                }
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("home_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "الإعدادات",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DailyPrayersCard(
    prayersSummary: DailyPrayersSummary,
    useEasternDigits: Boolean,
    onSelectPrayerForDetail: (PrayerType) -> Unit,
    onQuickTogglePrayer: (PrayerType) -> Unit
) {
    val completed = prayersSummary.completedCount
    val total = prayersSummary.totalCount
    val fraction = (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = WirdDimensionTokens.MotionStandardMs),
        label = "prayers_progress"
    )

    WirdCard(
        modifier = Modifier.testTag("daily_prayers_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "صلوات اليوم",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "اضغط على الصلاة للتسجيل أو التفاصيل",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 5 Circular Prayer Badges Row (Matching Reference Poster Screen 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("five_prayers_row"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                prayersSummary.orderedLogs.forEach { log ->
                    val prayer = log.prayerType
                    val isCompleted = log.isCompleted
                    val circleBg by animateColorAsState(
                        targetValue = if (isCompleted) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
                        },
                        animationSpec = tween(WirdDimensionTokens.MotionFastMs),
                        label = "prayer_circle_bg_${prayer.code}"
                    )
                    val iconTint by animateColorAsState(
                        targetValue = if (isCompleted) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        animationSpec = tween(WirdDimensionTokens.MotionFastMs),
                        label = "prayer_icon_tint_${prayer.code}"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(
                                role = Role.Button,
                                onClick = { onSelectPrayerForDetail(prayer) }
                            )
                            .padding(vertical = 6.dp)
                            .semantics {
                                contentDescription = "صلاة ${prayer.arabicName}، ${log.status.arabicLabel}"
                            }
                            .testTag("prayer_item_${prayer.code}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(circleBg)
                                .border(
                                    width = if (isCompleted) 0.dp else 1.dp,
                                    color = if (isCompleted) Color.Transparent else MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "تم الأداء",
                                    tint = iconTint,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Mosque,
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = prayer.arabicName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCompleted) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            maxLines = 1
                        )

                        // Non-color status text badge underneath (Accessibility requirement: don't rely on color alone)
                        Text(
                            text = when {
                                !isCompleted -> "سجّل"
                                log.performanceMode == PrayerPerformanceMode.JAMAAH -> "جماعة ✓"
                                log.performanceMode == PrayerPerformanceMode.INDIVIDUAL -> "منفردًا ✓"
                                else -> "تم ✓"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isCompleted) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onQuickTogglePrayer(prayer) }
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                .testTag("prayer_quick_toggle_${prayer.code}")
                        )
                    }
                }
            }

            // Calm Daily Indicator Bar («مؤشر اليوم الهادئ»: 3/5 + "أنت في الطريق .. استمر")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_prayer_indicator"),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(999.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${ArabicDateFormatter.formatNumber(completed, useEasternDigits)}/${ArabicDateFormatter.formatNumber(total, useEasternDigits)}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("prayers_count_ratio_text")
                    )
                }

                Text(
                    text = prayersSummary.calmDescriptiveLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun HeartMessageCard(
    message: HeartMessage,
    reduceMotion: Boolean,
    onNextMessage: () -> Unit
) {
    WirdCard(
        modifier = Modifier.testTag("heart_message_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
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
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(WirdColorTokens.WarmCoralContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = WirdColorTokens.WarmCoralHeart,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "رسالة لقلبك",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${message.sourceType.arabicBadge} • ${message.documentedAttribution}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TextButton(
                    onClick = onNextMessage,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("next_heart_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "رسالة أخرى",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "رسالة أخرى",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            if (reduceMotion) {
                Text(
                    text = message.text,
                    fontFamily = AmiriFontFamily,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("heart_message_text")
                )
            } else {
                AnimatedContent(
                    targetState = message,
                    transitionSpec = {
                        fadeIn(tween(WirdDimensionTokens.MotionStandardMs)) togetherWith
                            fadeOut(tween(WirdDimensionTokens.MotionFastMs))
                    },
                    label = "heart_message_anim"
                ) { targetMsg ->
                    Text(
                        text = targetMsg.text,
                        fontFamily = AmiriFontFamily,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("heart_message_text")
                    )
                }
            }
        }
    }
}

@Composable
private fun GoodnessSpaceCard(
    suggestions: List<DailyActivityRecord>,
    completedCount: Int,
    totalCount: Int,
    useEasternDigits: Boolean,
    onToggleActivity: (String) -> Unit,
    onOpenAllGoodDeeds: () -> Unit
) {
    val fraction = if (totalCount == 0) 0f else (completedCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)

    WirdCard(
        modifier = Modifier.testTag("goodness_space_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "مساحة الخير",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "اقتراحات اختيارية للذكر والقرآن وأعمال الخير",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Spa,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${ArabicDateFormatter.formatNumber(completedCount, useEasternDigits)}/${ArabicDateFormatter.formatNumber(totalCount, useEasternDigits)}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.testTag("goodness_ratio_text")
                        )
                    }
                }
            }

            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            suggestions.forEach { record ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (record.isCompleted) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    },
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (record.isCompleted) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onToggleActivity(record.definition.code) }
                        .testTag("home_goodness_item_${record.definition.code}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = record.definition.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = record.definition.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        WirdCompletionIndicator(
                            isCompleted = record.isCompleted,
                            completedLabel = "تم ✓",
                            uncompletedLabel = "سجّل",
                            onClick = { onToggleActivity(record.definition.code) },
                            tag = "home_goodness_toggle_${record.definition.code}"
                        )
                    }
                }
            }

            TextButton(
                onClick = onOpenAllGoodDeeds,
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("open_all_good_deeds_button")
            ) {
                Text(
                    text = "استعراض جميع العبادات وأعمال الخير",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun JourneyGlimpseCard(
    weeklyGlimpse: JourneyWeeklyGlimpse,
    useEasternDigits: Boolean,
    onOpenHistory: () -> Unit
) {
    WirdCard(
        modifier = Modifier.testTag("journey_glimpse_card"),
        onClick = onOpenHistory
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
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
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "لمحات من رحلتك",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "سجلّي",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = weeklyGlimpse.descriptiveSummaryText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlimpseStatPill(
                    label = "أيام نشطة",
                    value = ArabicDateFormatter.formatNumber(weeklyGlimpse.activeDaysInWeek, useEasternDigits),
                    modifier = Modifier.weight(1f)
                )
                GlimpseStatPill(
                    label = "صلوات مسجلة",
                    value = ArabicDateFormatter.formatNumber(weeklyGlimpse.totalPrayersRecordedInWeek, useEasternDigits),
                    modifier = Modifier.weight(1f)
                )
                GlimpseStatPill(
                    label = "أذكار وأعمال خير",
                    value = ArabicDateFormatter.formatNumber(weeklyGlimpse.totalDeedsRecordedInWeek, useEasternDigits),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GlimpseStatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
