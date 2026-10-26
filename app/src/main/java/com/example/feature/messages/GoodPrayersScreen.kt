package com.example.feature.messages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.components.WirdCard
import com.example.core.designsystem.components.WirdFilterChipRow
import com.example.core.designsystem.theme.AmiriFontFamily
import com.example.core.designsystem.theme.WirdColorTokens
import com.example.core.designsystem.theme.WirdDimensionTokens
import com.example.domain.model.ContentSourceType
import com.example.domain.model.HeartMessage

/**
 * «دعوات طيبة» Screen (Tab 4):
 * Displays curated short messages, Quranic verses, authentic Prophetic Hadiths, and general Duas,
 * strictly distinguishing religious texts (with documented attribution) from general encouraging reflections.
 */
@Composable
fun GoodPrayersScreen(
    messages: List<HeartMessage>,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filterOptions = listOf(
        "ALL" to "الكل",
        ContentSourceType.QURAN_VERSE.name to "آيات قرآنية",
        ContentSourceType.AUTHENTIC_HADITH.name to "أحاديث موثقة",
        ContentSourceType.GENERAL_DUA.name to "أدعية ورسائل عامة"
    )

    val filteredList = remember(messages, selectedFilter) {
        when (selectedFilter) {
            "ALL" -> messages
            ContentSourceType.GENERAL_DUA.name -> messages.filter {
                it.sourceType == ContentSourceType.GENERAL_DUA ||
                    it.sourceType == ContentSourceType.GENTLE_REFLECTION
            }
            else -> messages.filter { it.sourceType.name == selectedFilter }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("good_prayers_screen"),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = WirdDimensionTokens.MaxContentWidth),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "دعوات طيبة ورسائل للقلب",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "نصوص قرآنية ونبوية موثقة المصدر، مع دعوات وخواطر إيمانية عامة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                WirdFilterChipRow(
                    options = filterOptions,
                    selectedKey = selectedFilter,
                    onSelect = { selectedFilter = it },
                    tagPrefix = "messages_source_filter"
                )
            }

            items(filteredList, key = { it.id }) { msg ->
                val isSacredText = msg.sourceType == ContentSourceType.QURAN_VERSE ||
                    msg.sourceType == ContentSourceType.AUTHENTIC_HADITH

                WirdCard(
                    modifier = Modifier.testTag("message_card_${msg.id}")
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
                            Text(
                                text = msg.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSacredText) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    WirdColorTokens.WarmGoldContainer
                                }
                            ) {
                                Text(
                                    text = msg.sourceType.arabicBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSacredText) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        WirdColorTokens.TextPrimaryLight
                                    },
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg.text,
                                fontFamily = AmiriFontFamily,
                                fontSize = 20.sp,
                                lineHeight = 34.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "المصدر: ${msg.documentedAttribution}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = msg.momentCategory.arabicLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
