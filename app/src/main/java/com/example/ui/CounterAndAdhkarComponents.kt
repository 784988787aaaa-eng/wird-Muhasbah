package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.data.ArabicDateUtils
import com.example.data.SuggestedDhikr
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.EmeraldBrightAccent
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.GoldSecondaryLight
import kotlin.math.cos
import kotlin.math.sin

/**
 * Directive [4]: Circular 56.dp Mini-FAB with Gradient Emerald (#10B981 -> #059669),
 * a refined golden Tasbih bead icon in the center, and a sleek numeric badge on top when count > 0.
 */
@Composable
fun FloatingTasbihMiniFab(
    counterValue: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(EmeraldBrightAccent, EmeraldPrimaryLight)
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.28f), CircleShape)
                .clickable(
                    role = Role.Button,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onClick()
                    }
                )
                .semantics {
                    contentDescription = "فتح المسبحة الإلكترونية"
                }
                .testTag("open_counter_fab"),
            contentAlignment = Alignment.Center
        ) {
            // Golden Tasbih Bead Ring Icon
            Canvas(modifier = Modifier.size(28.dp)) {
                val radius = size.minDimension * 0.36f
                val center = Offset(size.width / 2f, size.height / 2f)
                // Subtle golden cord
                drawCircle(
                    color = Color(0xFFFDE68A).copy(alpha = 0.45f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                // 7 luminous golden beads around the ring
                val beadCount = 7
                for (i in 0 until beadCount) {
                    val angle = (i * 2.0 * Math.PI / beadCount) - (Math.PI / 2.0)
                    val bx = center.x + (radius * cos(angle)).toFloat()
                    val by = center.y + (radius * sin(angle)).toFloat()
                    drawCircle(
                        color = if (i == 0) Color(0xFFFFFBEB) else Color(0xFFFDE68A),
                        radius = if (i == 0) 3.4.dp.toPx() else 2.5.dp.toPx(),
                        center = Offset(bx, by)
                    )
                }
            }
        }

        // Small numeric badge on top when count > 0
        if (counterValue > 0) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = GoldSecondaryLight,
                border = BorderStroke(1.5.dp, Color.White),
                modifier = Modifier
                    .offset(x = 4.dp, y = (-4).dp)
                    .testTag("counter_fab_badge")
            ) {
                Text(
                    text = ArabicDateUtils.toArabicDigits(counterValue),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
fun SuggestedAdhkarSection(
    suggestions: List<SuggestedDhikr>,
    onSelectDhikr: (SuggestedDhikr) -> Unit,
    onOpenInCounter: (SuggestedDhikr) -> Unit,
    onCycleSuggestions: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("suggested_adhkar_section"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "أذكار مقترحة",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            TextButton(
                onClick = onCycleSuggestions,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("cycle_adhkar_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "عرض اقتراحات أخرى",
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "اقتراحات أخرى",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        suggestions.forEach { dhikr ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectDhikr(dhikr) }
                    .testTag("dhikr_card_${dhikr.id}"),
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
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = dhikr.categoryTitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = dhikr.documentedSource,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Text(
                            text = dhikr.shortPreview,
                            fontFamily = AmiriFontFamily,
                            fontSize = 18.sp,
                            lineHeight = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onSelectDhikr(dhikr) },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("read_dhikr_${dhikr.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MenuBook,
                                contentDescription = "قراءة ${dhikr.categoryTitle}",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        FilledTonalButton(
                            onClick = { onOpenInCounter(dhikr) },
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("counter_dhikr_${dhikr.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.TouchApp,
                                contentDescription = "بدء عد ${dhikr.categoryTitle}",
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("عدّ", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DhikrReaderDialog(
    dhikr: SuggestedDhikr,
    onDismiss: () -> Unit,
    onUseInCounter: (SuggestedDhikr) -> Unit
) {
    BackHandler { onDismiss() }
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
                .testTag("dhikr_reader_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = dhikr.categoryTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("close_dhikr_reader_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق نافذة الذكر"
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = dhikr.fullArabicText,
                        fontFamily = AmiriFontFamily,
                        fontSize = 22.sp,
                        lineHeight = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "التوثيق: ${dhikr.documentedSource}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = dhikr.documentedCountNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onUseInCounter(dhikr) },
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("use_dhikr_in_counter_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.TouchApp,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("البدء في المسبحة")
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Text("إغلاق")
                    }
                }
            }
        }
    }
}

/**
 * Directive [4]: Half-Screen Bottom Sheet Modal with a dimmed backdrop and an interactive
 * Circular Progress Ring that fills with emerald light on every tap (+1).
 * Reset and Dhikr-Switch buttons are subtly integrated above the ring.
 */
@Composable
fun ElectronicCounterPanel(
    visible: Boolean,
    counterValue: Int,
    activeDhikrTitle: String,
    isResetConfirmVisible: Boolean,
    reduceMotion: Boolean = false,
    onIncrement: () -> Unit,
    onCycleDhikr: () -> Unit = {},
    onRequestReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onCancelReset: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    if (visible) {
        BackHandler {
            if (isResetConfirmVisible) {
                onCancelReset()
            } else {
                onClose()
            }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = if (reduceMotion) EnterTransition.None else fadeIn(tween(200)),
        exit = if (reduceMotion) ExitTransition.None else fadeOut(tween(180)),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.46f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClose
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Consume clicks inside bottom sheet
                    )
                    .testTag("counter_panel"),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Bottom Sheet Pill Handle
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    // Top Bar: Integrated Dhikr Switcher, Reset, and Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Subtle Dhikr Switcher Pill integrated at top of ring
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .clip(RoundedCornerShape(999.dp))
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onCycleDhikr()
                                }
                                .testTag("counter_cycle_dhikr_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.SwapHoriz,
                                    contentDescription = "تبديل الذكر",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = activeDhikrTitle,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onRequestReset,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("counter_reset_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "تصفير العداد",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("counter_close_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "إغلاق المسبحة",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Reset Confirmation Prompt
                    AnimatedVisibility(visible = isResetConfirmVisible) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("counter_reset_confirm_box")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "تصفير العدد الحالي؟",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TextButton(
                                        onClick = onCancelReset,
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("cancel_reset_button")
                                    ) {
                                        Text("إلغاء")
                                    }
                                    Button(
                                        onClick = onConfirmReset,
                                        modifier = Modifier
                                            .minimumInteractiveComponentSize()
                                            .testTag("confirm_reset_button"),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text("نعم، تصفير")
                                    }
                                }
                            }
                        }
                    }

                    // Interactive Circular Progress Ring (Tap anywhere inside to increment +1)
                    val cycleTarget = 33
                    val cycleStep = counterValue % cycleTarget
                    val rawProgress = when {
                        counterValue == 0 -> 0f
                        cycleStep == 0 -> 1f
                        else -> cycleStep.toFloat() / cycleTarget.toFloat()
                    }
                    val animatedSweep by animateFloatAsState(
                        targetValue = rawProgress * 360f,
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
                        label = "tasbih_ring_sweep"
                    )

                    val ringTrackColor = MaterialTheme.colorScheme.outlineVariant
                    val primaryRingColor = MaterialTheme.colorScheme.primary

                    Box(
                        modifier = Modifier
                            .size(210.dp)
                            .clip(CircleShape)
                            .clickable(
                                role = Role.Button,
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onIncrement()
                                }
                            )
                            .testTag("counter_increment_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                            val strokePx = 12.dp.toPx()
                            val diameter = size.minDimension - strokePx
                            val topLeft = Offset(
                                (size.width - diameter) / 2f,
                                (size.height - diameter) / 2f
                            )
                            val arcSize = Size(diameter, diameter)

                            // Soft inner luminous fill that rises with progress
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        EmeraldBrightAccent.copy(alpha = 0.08f + 0.14f * rawProgress),
                                        Color.Transparent
                                    )
                                ),
                                radius = diameter / 2f
                            )

                            // Background track ring
                            drawArc(
                                color = ringTrackColor,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokePx, cap = StrokeCap.Round)
                            )

                            // Glowing Emerald progress arc
                            if (animatedSweep > 0f) {
                                drawArc(
                                    brush = Brush.sweepGradient(
                                        colors = listOf(
                                            EmeraldPrimaryLight,
                                            EmeraldBrightAccent,
                                            GoldSecondaryLight,
                                            EmeraldPrimaryLight
                                        )
                                    ),
                                    startAngle = -90f,
                                    sweepAngle = animatedSweep,
                                    useCenter = false,
                                    topLeft = topLeft,
                                    size = arcSize,
                                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                                )
                            }
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            if (reduceMotion) {
                                Text(
                                    text = counterValue.toString(),
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = primaryRingColor,
                                    modifier = Modifier
                                        .testTag("counter_value_text")
                                        .semantics {
                                            contentDescription = "العدد الحالي $counterValue"
                                        }
                                )
                            } else {
                                AnimatedContent(
                                    targetState = counterValue,
                                    transitionSpec = {
                                        (slideInVertically { height -> height / 3 } + fadeIn())
                                            .togetherWith(slideOutVertically { height -> -height / 3 } + fadeOut())
                                    },
                                    label = "counter_digit_anim"
                                ) { targetCount ->
                                    Text(
                                        text = targetCount.toString(),
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = primaryRingColor,
                                        modifier = Modifier
                                            .testTag("counter_value_text")
                                            .semantics {
                                                contentDescription = "العدد الحالي $targetCount"
                                            }
                                    )
                                }
                            }

                            val displayedCycle = if (counterValue > 0 && cycleStep == 0) 33 else cycleStep
                            Text(
                                text = "${ArabicDateUtils.toArabicDigits(displayedCycle)} / ٣٣",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "انقر داخل الحلقة للتسبيح",
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
