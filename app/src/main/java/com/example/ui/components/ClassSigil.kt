package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.CharacterClass
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ClassSigil(
    characterClass: CharacterClass,
    modifier: Modifier = Modifier,
    size: Dp = 112.dp,
) {
    val accent = characterClass.accentColor
    val classLabel = stringResource(id = characterClass.labelRes)
    val sigilDescription = stringResource(id = R.string.cd_class_sigil, classLabel)

    val infiniteTransition = rememberInfiniteTransition(label = "sigil_aura")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "sigil_pulse",
    )
    val slowRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sigil_rotation",
    )

    Box(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = sigilDescription },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = this.size.minDimension / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Subtle radial aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.34f * pulse),
                        accent.copy(alpha = 0.08f),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )

            // Outer rotating rune ring
            rotate(degrees = slowRotation, pivot = center) {
                val runeRadius = radius * 0.84f
                drawCircle(
                    color = accent.copy(alpha = 0.35f),
                    radius = runeRadius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx()),
                )
                for (i in 0 until 8) {
                    val angle = (i * 45.0) * PI / 180.0
                    val dotPos = Offset(
                        x = center.x + (runeRadius * cos(angle)).toFloat(),
                        y = center.y + (runeRadius * sin(angle)).toFloat(),
                    )
                    drawCircle(
                        color = accent.copy(alpha = 0.75f),
                        radius = 2.dp.toPx(),
                        center = dotPos,
                    )
                }
            }

            // Inner shield medallion disc
            drawCircle(
                color = accent.copy(alpha = 0.14f),
                radius = radius * 0.68f,
                center = center,
            )
            drawCircle(
                color = accent.copy(alpha = 0.75f),
                radius = radius * 0.68f,
                center = center,
                style = Stroke(width = 2.dp.toPx()),
            )

            val emblemRadius = radius * 0.42f
            when (characterClass) {
                CharacterClass.MAGE -> drawMageSigil(center, emblemRadius, accent)
                CharacterClass.ROGUE -> drawRogueSigil(center, emblemRadius, accent)
                CharacterClass.WARRIOR -> drawWarriorSigil(center, emblemRadius, accent)
                CharacterClass.PALADIN -> drawPaladinSigil(center, emblemRadius, accent)
                CharacterClass.RANGER -> drawRangerSigil(center, emblemRadius, accent)
                CharacterClass.NECROMANCER -> drawNecromancerSigil(center, emblemRadius, accent)
                CharacterClass.DRUID -> drawDruidSigil(center, emblemRadius, accent)
            }
        }
    }
}

private fun DrawScope.drawMageSigil(center: Offset, r: Float, color: Color) {
    val stroke = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    val starPath = Path()
    for (i in 0 until 8) {
        val angle = (i * 45.0 - 90.0) * PI / 180.0
        val dist = if (i % 2 == 0) r else r * 0.36f
        val x = center.x + (dist * cos(angle)).toFloat()
        val y = center.y + (dist * sin(angle)).toFloat()
        if (i == 0) starPath.moveTo(x, y) else starPath.lineTo(x, y)
    }
    starPath.close()
    drawPath(path = starPath, color = color.copy(alpha = 0.25f))
    drawPath(path = starPath, color = color, style = stroke)
    drawCircle(color = color, radius = r * 0.16f, center = center)
}

private fun DrawScope.drawRogueSigil(center: Offset, r: Float, color: Color) {
    val stroke = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Crossed twin daggers
    val blade1 = Path().apply {
        moveTo(center.x - r * 0.72f, center.y + r * 0.72f)
        lineTo(center.x + r * 0.78f, center.y - r * 0.78f)
    }
    val blade2 = Path().apply {
        moveTo(center.x + r * 0.72f, center.y + r * 0.72f)
        lineTo(center.x - r * 0.78f, center.y - r * 0.78f)
    }
    drawPath(blade1, color = color, style = stroke)
    drawPath(blade2, color = color, style = stroke)
    // Crossguards
    drawLine(
        color = color,
        start = Offset(center.x - r * 0.65f, center.y + r * 0.22f),
        end = Offset(center.x - r * 0.22f, center.y + r * 0.65f),
        strokeWidth = 2.6.dp.toPx(),
        cap = StrokeCap.Round,
    )
    drawLine(
        color = color,
        start = Offset(center.x + r * 0.65f, center.y + r * 0.22f),
        end = Offset(center.x + r * 0.22f, center.y + r * 0.65f),
        strokeWidth = 2.6.dp.toPx(),
        cap = StrokeCap.Round,
    )
    drawCircle(
        color = color,
        radius = r * 0.22f,
        center = center,
        style = Stroke(width = 2.dp.toPx()),
    )
}

private fun DrawScope.drawWarriorSigil(center: Offset, r: Float, color: Color) {
    val stroke = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    val shield = Path().apply {
        moveTo(center.x - r * 0.72f, center.y - r * 0.68f)
        lineTo(center.x + r * 0.72f, center.y - r * 0.68f)
        lineTo(center.x + r * 0.72f, center.y + r * 0.06f)
        lineTo(center.x, center.y + r * 0.92f)
        lineTo(center.x - r * 0.72f, center.y + r * 0.06f)
        close()
    }
    drawPath(shield, color = color.copy(alpha = 0.2f))
    drawPath(shield, color = color, style = stroke)
    // Central sword ridge
    drawLine(
        color = color,
        start = Offset(center.x, center.y - r * 0.92f),
        end = Offset(center.x, center.y + r * 0.65f),
        strokeWidth = 2.8.dp.toPx(),
        cap = StrokeCap.Round,
    )
    drawLine(
        color = color,
        start = Offset(center.x - r * 0.36f, center.y - r * 0.32f),
        end = Offset(center.x + r * 0.36f, center.y - r * 0.32f),
        strokeWidth = 2.6.dp.toPx(),
        cap = StrokeCap.Round,
    )
}

private fun DrawScope.drawPaladinSigil(center: Offset, r: Float, color: Color) {
    val stroke = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Sunburst rays
    for (i in 0 until 8) {
        val angle = (i * 45.0) * PI / 180.0
        val inner = r * 0.48f
        val outer = if (i % 2 == 0) r * 0.96f else r * 0.72f
        drawLine(
            color = color,
            start = Offset(
                center.x + (inner * cos(angle)).toFloat(),
                center.y + (inner * sin(angle)).toFloat(),
            ),
            end = Offset(
                center.x + (outer * cos(angle)).toFloat(),
                center.y + (outer * sin(angle)).toFloat(),
            ),
            strokeWidth = 2.4.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
    val diamond = Path().apply {
        moveTo(center.x, center.y - r * 0.45f)
        lineTo(center.x + r * 0.34f, center.y)
        lineTo(center.x, center.y + r * 0.45f)
        lineTo(center.x - r * 0.34f, center.y)
        close()
    }
    drawPath(diamond, color = color.copy(alpha = 0.25f))
    drawPath(diamond, color = color, style = stroke)
}

private fun DrawScope.drawRangerSigil(center: Offset, r: Float, color: Color) {
    val stroke = Stroke(width = 2.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Bow arc
    val bowPath = Path().apply {
        moveTo(center.x - r * 0.35f, center.y - r * 0.85f)
        quadraticTo(
            center.x + r * 0.65f,
            center.y,
            center.x - r * 0.35f,
            center.y + r * 0.85f,
        )
    }
    drawPath(bowPath, color = color, style = stroke)
    // Bowstring
    drawLine(
        color = color.copy(alpha = 0.65f),
        start = Offset(center.x - r * 0.35f, center.y - r * 0.85f),
        end = Offset(center.x - r * 0.35f, center.y + r * 0.85f),
        strokeWidth = 1.6.dp.toPx(),
    )
    // Arrow
    drawLine(
        color = color,
        start = Offset(center.x - r * 0.75f, center.y),
        end = Offset(center.x + r * 0.85f, center.y),
        strokeWidth = 2.6.dp.toPx(),
        cap = StrokeCap.Round,
    )
    val arrowhead = Path().apply {
        moveTo(center.x + r * 0.52f, center.y - r * 0.24f)
        lineTo(center.x + r * 0.88f, center.y)
        lineTo(center.x + r * 0.52f, center.y + r * 0.24f)
    }
    drawPath(arrowhead, color = color, style = stroke)
}

private fun DrawScope.drawNecromancerSigil(center: Offset, r: Float, color: Color) {
    val stroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Crescent scythe moon
    val crescent = Path().apply {
        moveTo(center.x + r * 0.35f, center.y - r * 0.82f)
        cubicTo(
            center.x - r * 0.82f,
            center.y - r * 0.55f,
            center.x - r * 0.82f,
            center.y + r * 0.55f,
            center.x + r * 0.35f,
            center.y + r * 0.82f,
        )
        cubicTo(
            center.x - r * 0.28f,
            center.y + r * 0.42f,
            center.x - r * 0.28f,
            center.y - r * 0.42f,
            center.x + r * 0.35f,
            center.y - r * 0.82f,
        )
        close()
    }
    drawPath(crescent, color = color.copy(alpha = 0.24f))
    drawPath(crescent, color = color, style = stroke)
    // Soul wisp orb
    drawCircle(
        color = color,
        radius = r * 0.2f,
        center = Offset(center.x + r * 0.35f, center.y),
    )
    drawCircle(
        color = color.copy(alpha = 0.5f),
        radius = r * 0.34f,
        center = Offset(center.x + r * 0.35f, center.y),
        style = Stroke(width = 1.6.dp.toPx()),
    )
}

private fun DrawScope.drawDruidSigil(center: Offset, r: Float, color: Color) {
    val stroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    // Leaf / sprout spire
    val leaf = Path().apply {
        moveTo(center.x, center.y - r * 0.85f)
        cubicTo(
            center.x + r * 0.65f,
            center.y - r * 0.25f,
            center.x + r * 0.55f,
            center.y + r * 0.45f,
            center.x,
            center.y + r * 0.72f,
        )
        cubicTo(
            center.x - r * 0.55f,
            center.y + r * 0.45f,
            center.x - r * 0.65f,
            center.y - r * 0.25f,
            center.x,
            center.y - r * 0.85f,
        )
        close()
    }
    drawPath(leaf, color = color.copy(alpha = 0.22f))
    drawPath(leaf, color = color, style = stroke)
    // Central vein + twin branches
    drawLine(
        color = color,
        start = Offset(center.x, center.y - r * 0.68f),
        end = Offset(center.x, center.y + r * 0.85f),
        strokeWidth = 2.4.dp.toPx(),
        cap = StrokeCap.Round,
    )
    drawLine(
        color = color,
        start = Offset(center.x, center.y + r * 0.1f),
        end = Offset(center.x - r * 0.28f, center.y - r * 0.16f),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round,
    )
    drawLine(
        color = color,
        start = Offset(center.x, center.y - r * 0.14f),
        end = Offset(center.x + r * 0.28f, center.y - r * 0.36f),
        strokeWidth = 2.dp.toPx(),
        cap = StrokeCap.Round,
    )
}
