package com.example.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.core.designsystem.components.MosqueBotanicalBackdrop
import com.example.core.designsystem.components.WirdCard
import com.example.core.designsystem.components.WirdPrimaryButton
import com.example.core.designsystem.components.WirdSecondaryButton
import com.example.core.designsystem.components.WirdThreeLeafLogo

/**
 * Matches Screen 1 of the Visual Reference Poster («شاشة البداية»):
 * - Serene botanical & mosque horizon backdrop
 * - Three-leaf «وِرد» emblem
 * - "بداية أجمل .. مع كل وردة"
 * - Optional name greeting input & "ابدأ الآن" button
 */
@Composable
fun WelcomeOnboardingScreen(
    initialName: String,
    onStartNow: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var nameInput by remember(initialName) { mutableStateOf(initialName) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    )
                )
            )
            .testTag("welcome_onboarding_screen"),
        contentAlignment = Alignment.Center
    ) {
        MosqueBotanicalBackdrop(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 460.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            WirdThreeLeafLogo(
                leafSize = 84.dp,
                showTitle = true
            )

            Text(
                text = "بداية أجمل ..\nمع كل وردة",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                text = "وِرد… رفيقك إلى الخير، برفقٍ لا يثقل قلبك.\nجميع سجلاتك محفوظة محليًا على جهازك بخصوصية تامة.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "اسم أو كنية للتحية (اختياري):",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        placeholder = { Text("مثال: عبد الله، مريم...") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_name_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            WirdPrimaryButton(
                text = "ابدأ الآن",
                onClick = { onStartNow(nameInput) },
                modifier = Modifier.fillMaxWidth(),
                tag = "onboarding_start_now_button"
            )

            TextButton(
                onClick = { onStartNow("") },
                modifier = Modifier.testTag("onboarding_skip_button")
            ) {
                Text(
                    text = "تجاوز الإعداد والبدء مباشرة",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Matches the "مرحبًا بك مجددًا" screen in the bottom row of the Visual Reference Poster:
 * Welcomes the user back after an absence without any blame or guilt, offering:
 * - «أبدأ يومي»
 * - «أراجع سجلي»
 */
@Composable
fun ReturnAfterPauseBannerCard(
    onStartMyDay: () -> Unit,
    onReviewMyHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    WirdCard(
        modifier = modifier.testTag("return_after_pause_card"),
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WirdThreeLeafLogo(leafSize = 36.dp, showTitle = false)
            Text(
                text = "مرحبًا بك مجددًا",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "تذكر.. الله معك دائمًا. كل يوم جديد هو صفحة مشرقة، ابدأ بما يتيسر لك اليوم بطمأنينة.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WirdPrimaryButton(
                    text = "أبدأ يومي",
                    onClick = onStartMyDay,
                    modifier = Modifier.weight(1f),
                    tag = "return_start_day_button"
                )
                WirdSecondaryButton(
                    text = "أراجع سجلي",
                    onClick = onReviewMyHistory,
                    modifier = Modifier.weight(1f),
                    tag = "return_review_history_button"
                )
            }
        }
    }
}
