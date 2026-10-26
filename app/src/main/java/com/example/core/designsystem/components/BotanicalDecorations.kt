package com.example.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.designsystem.theme.AmiriFontFamily
import com.example.core.designsystem.theme.WirdColorTokens

/**
 * Draws the three-leaf botanical emblem of «وِرد» from the visual reference poster.
 */
@Composable
fun WirdThreeLeafLogo(
    modifier: Modifier = Modifier,
    leafSize: Dp = 52.dp,
    showTitle: Boolean = true,
    primaryLeafColor: Color = MaterialTheme.colorScheme.primary,
    secondaryLeafColor: Color = MaterialTheme.colorScheme.secondary
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(modifier = Modifier.size(leafSize)) {
            drawThreeLeaves(
                primaryColor = primaryLeafColor,
                secondaryColor = secondaryLeafColor
            )
        }
        if (showTitle) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "وِرد",
                fontFamily = AmiriFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

fun DrawScope.drawThreeLeaves(
    primaryColor: Color,
    secondaryColor: Color
) {
    val w = size.width
    val h = size.height

    // Center upright leaf
    val centerLeaf = Path().apply {
        moveTo(w * 0.50f, h * 0.08f)
        cubicTo(w * 0.68f, h * 0.30f, w * 0.66f, h * 0.64f, w * 0.50f, h * 0.86f)
        cubicTo(w * 0.34f, h * 0.64f, w * 0.32f, h * 0.30f, w * 0.50f, h * 0.08f)
        close()
    }
    drawPath(path = centerLeaf, color = primaryColor, style = Fill)

    // Left angled leaf
    val leftLeaf = Path().apply {
        moveTo(w * 0.14f, h * 0.30f)
        cubicTo(w * 0.36f, h * 0.32f, w * 0.46f, h * 0.56f, w * 0.46f, h * 0.86f)
        cubicTo(w * 0.24f, h * 0.78f, w * 0.15f, h * 0.54f, w * 0.14f, h * 0.30f)
        close()
    }
    drawPath(path = leftLeaf, color = secondaryColor, style = Fill)

    // Right angled leaf
    val rightLeaf = Path().apply {
        moveTo(w * 0.86f, h * 0.30f)
        cubicTo(w * 0.64f, h * 0.32f, w * 0.54f, h * 0.56f, w * 0.54f, h * 0.86f)
        cubicTo(w * 0.76f, h * 0.78f, w * 0.85f, h * 0.54f, w * 0.86f, h * 0.30f)
        close()
    }
    drawPath(path = rightLeaf, color = secondaryColor, style = Fill)
}

/**
 * Subtle Mosque & Botanical Silhouette backdrop for the Prayer Detail Header and Welcome Screen,
 * matching the serene architectural illustration in the visual reference without using static raster images.
 */
@Composable
fun MosqueBotanicalBackdrop(
    modifier: Modifier = Modifier,
    tintColor: Color = WirdColorTokens.SageGreenMuted.copy(alpha = 0.14f)
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Soft horizon hills
            val hillPath = Path().apply {
                moveTo(0f, h * 0.78f)
                quadraticTo(w * 0.30f, h * 0.66f, w * 0.65f, h * 0.76f)
                quadraticTo(w * 0.85f, h * 0.82f, w, h * 0.72f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(hillPath, color = tintColor)

            // Central Mosque Dome
            val domeCenterX = w * 0.5f
            val domeBaseY = h * 0.76f
            val domeRadius = w * 0.16f
            drawArc(
                color = tintColor.copy(alpha = (tintColor.alpha * 1.25f).coerceAtMost(0.28f)),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(domeCenterX - domeRadius, domeBaseY - domeRadius),
                size = Size(domeRadius * 2f, domeRadius * 2f)
            )

            // Left slender Minaret
            val minX = domeCenterX - domeRadius * 1.45f
            val minWidth = w * 0.035f
            val minTop = domeBaseY - domeRadius * 1.55f
            drawRect(
                color = tintColor.copy(alpha = (tintColor.alpha * 1.3f).coerceAtMost(0.30f)),
                topLeft = Offset(minX, minTop),
                size = Size(minWidth, domeBaseY - minTop)
            )
            // Right slender Minaret
            val rightMinX = domeCenterX + domeRadius * 1.25f
            drawRect(
                color = tintColor.copy(alpha = (tintColor.alpha * 1.3f).coerceAtMost(0.30f)),
                topLeft = Offset(rightMinX, minTop * 1.05f),
                size = Size(minWidth, domeBaseY - minTop * 1.05f)
            )

            // Corner botanical leaves (top-left)
            val cornerLeaf = Path().apply {
                moveTo(w * 0.06f, h * 0.08f)
                quadraticTo(w * 0.18f, h * 0.10f, w * 0.20f, h * 0.26f)
                quadraticTo(w * 0.08f, h * 0.22f, w * 0.06f, h * 0.08f)
                close()
            }
            drawPath(cornerLeaf, color = tintColor)
        }
    }
}
