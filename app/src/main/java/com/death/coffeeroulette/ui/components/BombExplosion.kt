package com.death.coffeeroulette.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.cos
import kotlin.math.sin

/**
 * 폭탄 폭발 시각효과를 Canvas에 그리는 유틸리티
 * - 중앙에서 퍼져나가는 폭발 원
 * - 불꽃 파티클
 * - 연기 효과
 */
fun DrawScope.drawBombExplosion(
    center: Offset,
    progress: Float,   // 0f ~ 1f 애니메이션 진행률
    maxRadius: Float
) {
    val currentRadius = maxRadius * progress

    // === 외부 폭발 링 ===
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFCC00).copy(alpha = (1f - progress) * 0.8f),
                Color(0xFFFF6600).copy(alpha = (1f - progress) * 0.6f),
                Color(0xFFFF0000).copy(alpha = (1f - progress) * 0.3f),
                Color.Transparent
            ),
            center = center,
            radius = currentRadius
        ),
        center = center,
        radius = currentRadius
    )

    // === 중간 폭발 링 ===
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = (1f - progress) * 0.9f),
                Color(0xFFFFCC00).copy(alpha = (1f - progress) * 0.7f),
                Color(0xFFFF6600).copy(alpha = (1f - progress) * 0.4f),
                Color.Transparent
            ),
            center = center,
            radius = currentRadius * 0.6f
        ),
        center = center,
        radius = currentRadius * 0.6f
    )

    // === 내부 빛 ===
    drawCircle(
        color = Color.White.copy(alpha = (1f - progress) * 0.8f),
        center = center,
        radius = currentRadius * 0.2f
    )

    // === 불꽃 파티클 ===
    val particleCount = 12
    for (i in 0 until particleCount) {
        val angle = Math.toRadians((i * 360.0 / particleCount) + (progress * 30))
        val dist = currentRadius * (0.5f + progress * 0.5f)
        val px = center.x + (dist * cos(angle)).toFloat()
        val py = center.y + (dist * sin(angle)).toFloat()
        val particleAlpha = (1f - progress) * 0.8f
        val particleRadius = maxRadius * 0.05f * (1f - progress * 0.5f)

        drawCircle(
            color = if (i % 2 == 0) Color(0xFFFF6600).copy(alpha = particleAlpha)
                    else Color(0xFFFFCC00).copy(alpha = particleAlpha),
            center = Offset(px, py),
            radius = particleRadius
        )
    }

    // === 연기 파티클 ===
    val smokeCount = 8
    for (i in 0 until smokeCount) {
        val angle = Math.toRadians((i * 360.0 / smokeCount) + 15.0)
        val dist = currentRadius * 0.8f
        val sx = center.x + (dist * cos(angle)).toFloat()
        val sy = center.y + (dist * sin(angle)).toFloat() - (progress * 30f)
        val smokeAlpha = (1f - progress) * 0.3f

        drawCircle(
            color = Color(0xFF555555).copy(alpha = smokeAlpha),
            center = Offset(sx, sy),
            radius = maxRadius * 0.08f * (0.5f + progress)
        )
    }
}

/**
 * 반복적으로 반짝이는 폭발을 그림 (인트로용)
 */
fun DrawScope.drawPulsingExplosion(
    center: Offset,
    pulseProgress: Float,  // 0f ~ 1f 반복
    baseRadius: Float
) {
    val pulse = 0.8f + 0.2f * sin(pulseProgress * Math.PI.toFloat() * 2)
    val radius = baseRadius * pulse

    // 외부 글로우
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF6600).copy(alpha = 0.4f),
                Color(0xFFFF0000).copy(alpha = 0.2f),
                Color.Transparent
            ),
            center = center,
            radius = radius * 1.5f
        ),
        center = center,
        radius = radius * 1.5f
    )

    // 메인 폭발
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFCC00).copy(alpha = 0.8f * pulse),
                Color(0xFFFF6600).copy(alpha = 0.6f * pulse),
                Color(0xFFFF0000).copy(alpha = 0.3f),
            ),
            center = center,
            radius = radius
        ),
        center = center,
        radius = radius
    )

    // 중심 밝은 빛
    drawCircle(
        color = Color.White.copy(alpha = 0.7f * pulse),
        center = center,
        radius = radius * 0.3f
    )
}
