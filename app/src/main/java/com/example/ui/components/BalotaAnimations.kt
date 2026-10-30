package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Custom 3D Golden Game Star drawn with Canvas for rich depth and specular highlights.
 */
@Composable
fun GoldenGameStar(
    isEarned: Boolean,
    starIndex: Int,
    targetEarnedCount: Int,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val scale = remember { Animatable(0f) }
    val rotation = remember { Animatable(-30f) }
    val glowAlpha = remember { Animatable(0f) }

    LaunchedEffect(isEarned) {
        if (isEarned) {
            // Staggered pop-in for each star
            delay(starIndex * 260L + 150L)
            glowAlpha.animateTo(1f, tween(150))
            rotation.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
            scale.animateTo(
                targetValue = 1.25f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            scale.animateTo(1f, spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium))
        } else {
            scale.snapTo(1f)
            rotation.snapTo(0f)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "star_idle")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(if (isEarned) scale.value else 0.85f)
            .rotate(if (isEarned) rotation.value else 0f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h / 2f)
            val outerRadius = w * 0.46f
            val innerRadius = outerRadius * 0.44f

            // Generate 5-pointed star path
            val path = Path()
            val points = 5
            val angleStep = (2 * PI / points).toFloat()
            val halfStep = angleStep / 2f
            val startAngle = -PI.toFloat() / 2f

            for (i in 0 until points) {
                val outerAngle = startAngle + i * angleStep
                val outerX = center.x + outerRadius * cos(outerAngle)
                val outerY = center.y + outerRadius * sin(outerAngle)
                if (i == 0) path.moveTo(outerX, outerY) else path.lineTo(outerX, outerY)

                val innerAngle = outerAngle + halfStep
                val innerX = center.x + innerRadius * cos(innerAngle)
                val innerY = center.y + innerRadius * sin(innerAngle)
                path.lineTo(innerX, innerY)
            }
            path.close()

            if (isEarned) {
                // Outer warm halo / glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x99FFA000),
                            Color(0x33FFD54F),
                            Color(0x00FFD54F)
                        ),
                        center = center,
                        radius = outerRadius * 1.35f
                    ),
                    radius = outerRadius * 1.35f,
                    center = center
                )

                // Drop shadow
                val shadowPath = Path()
                shadowPath.addPath(path, Offset(0f, w * 0.04f))
                drawPath(shadowPath, color = Color(0x33000000), style = Fill)

                // Rich golden 3D gradient body
                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF176), // Bright top gold
                            Color(0xFFFFB300), // Amber gold
                            Color(0xFFF57C00)  // Deep rich base
                        ),
                        startY = 0f,
                        endY = h
                    ),
                    style = Fill
                )

                // Golden stroke border
                drawPath(
                    path = path,
                    color = Color(0xFFE65100),
                    style = Stroke(width = w * 0.045f, cap = StrokeCap.Round)
                )

                // Specular highlight on top point
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f),
                    radius = outerRadius * 0.15f,
                    center = Offset(center.x - outerRadius * 0.18f, center.y - outerRadius * 0.28f)
                )
            } else {
                // Empty Star - elegant soft slate cream with dotted/thin gold rim
                drawPath(
                    path = path,
                    color = Color(0xFFE0E0E0),
                    style = Fill
                )
                drawPath(
                    path = path,
                    color = Color(0xFFBDBDBD),
                    style = Stroke(width = w * 0.04f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

/**
 * Three-star rating layout with staggered animations and celebratory feel.
 */
@Composable
fun BalotaAnimatedStarRating(
    earnedStars: Int,
    modifier: Modifier = Modifier,
    starSize: Dp = 68.dp
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 3) {
            val isEarned = i < earnedStars
            GoldenGameStar(
                isEarned = isEarned,
                starIndex = i,
                targetEarnedCount = earnedStars,
                size = if (i == 1) starSize + 8.dp else starSize // Center star is slightly larger for trophy balance!
            )
        }
    }
}

/**
 * Joyful Confetti particle system in Algerian & Balota festive colors.
 */
data class ConfettiParticle(
    val initialX: Float,
    val initialY: Float,
    val size: Float,
    val color: Color,
    val speedY: Float,
    val speedX: Float,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ConfettiCelebration(
    modifier: Modifier = Modifier,
    particleCount: Int = 45
) {
    val confettiColors = listOf(
        Color(0xFF2E7D32), // Emerald Algerian Green
        Color(0xFFFFB300), // Balota Gold
        Color(0xFFD32F2F), // Festive Red
        Color(0xFF039BE5), // Sky Blue
        Color(0xFFFFFFFF), // Pure White
        Color(0xFFFFA000)  // Warm Amber
    )

    val particles = remember {
        List(particleCount) {
            ConfettiParticle(
                initialX = Random.nextFloat(),
                initialY = Random.nextFloat() * -0.6f,
                size = Random.nextFloat() * 14f + 8f,
                color = confettiColors.random(),
                speedY = Random.nextFloat() * 0.4f + 0.35f,
                speedX = (Random.nextFloat() - 0.5f) * 0.2f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "confetti_fall")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "confetti_anim"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        for (p in particles) {
            val currentY = ((p.initialY + progress * p.speedY) % 1.2f) * h
            val currentX = ((p.initialX + progress * p.speedX + sin(progress * 4 * PI + p.initialX).toFloat() * 0.05f) % 1f) * w
            val currentRot = progress * p.rotationSpeed

            if (currentY in -50f..h + 50f) {
                if (p.isCircle) {
                    drawCircle(
                        color = p.color,
                        radius = p.size * 0.45f,
                        center = Offset(currentX, currentY)
                    )
                } else {
                    // Confetti Ribbon
                    val rectWidth = p.size
                    val rectHeight = p.size * 0.5f
                    drawRect(
                        color = p.color,
                        topLeft = Offset(currentX - rectWidth / 2, currentY - rectHeight / 2),
                        size = androidx.compose.ui.geometry.Size(rectWidth, rectHeight)
                    )
                }
            }
        }
    }
}
