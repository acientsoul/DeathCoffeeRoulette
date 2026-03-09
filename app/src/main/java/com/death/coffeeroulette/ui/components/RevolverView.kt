package com.death.coffeeroulette.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.death.coffeeroulette.ui.theme.RevolverBrown
import com.death.coffeeroulette.ui.theme.RevolverDarkGray
import com.death.coffeeroulette.ui.theme.RevolverGray
import kotlin.math.cos
import kotlin.math.sin

/**
 * 리볼버를 Canvas에 그리는 유틸리티
 * - 서부극 스타일 6연발 리볼버
 * - 회전 가능 (rotationDegrees)
 * - 발사 효과 (isFiring)
 */
fun DrawScope.drawRevolver(
    center: Offset,
    scale: Float = 1f,
    rotationDegrees: Float = 0f,
    isFiring: Boolean = false
) {
    rotate(degrees = rotationDegrees, pivot = center) {
        val s = scale

        // === 그립 (손잡이) ===
        val gripPath = Path().apply {
            moveTo(center.x - 5f * s, center.y + 5f * s)
            lineTo(center.x - 12f * s, center.y + 35f * s)
            lineTo(center.x + 5f * s, center.y + 38f * s)
            lineTo(center.x + 8f * s, center.y + 5f * s)
            close()
        }
        drawPath(gripPath, color = RevolverBrown, style = Fill)
        drawPath(gripPath, color = Color.Black, style = Stroke(width = 1.5f * s))

        // 그립 라인 디테일
        drawLine(
            color = Color.Black.copy(alpha = 0.3f),
            start = Offset(center.x - 3f * s, center.y + 10f * s),
            end = Offset(center.x - 8f * s, center.y + 32f * s),
            strokeWidth = 1f * s
        )

        // === 프레임 (본체) ===
        drawRect(
            color = RevolverGray,
            topLeft = Offset(center.x - 8f * s, center.y - 8f * s),
            size = Size(25f * s, 16f * s)
        )
        drawRect(
            color = Color.Black,
            topLeft = Offset(center.x - 8f * s, center.y - 8f * s),
            size = Size(25f * s, 16f * s),
            style = Stroke(width = 1.5f * s)
        )

        // === 실린더 (탄창) ===
        drawCircle(
            color = RevolverDarkGray,
            radius = 12f * s,
            center = Offset(center.x + 5f * s, center.y)
        )
        drawCircle(
            color = Color.Black,
            radius = 12f * s,
            center = Offset(center.x + 5f * s, center.y),
            style = Stroke(width = 1.5f * s)
        )

        // 탄창 구멍 (6개)
        for (i in 0 until 6) {
            val angle = Math.toRadians((i * 60).toDouble())
            val holeCenter = Offset(
                center.x + 5f * s + (7f * s * cos(angle)).toFloat(),
                center.y + (7f * s * sin(angle)).toFloat()
            )
            drawCircle(
                color = Color(0xFF2A2A2A),
                radius = 3f * s,
                center = holeCenter
            )
        }

        // === 총열 (배럴) ===
        drawRect(
            color = RevolverGray,
            topLeft = Offset(center.x + 15f * s, center.y - 5f * s),
            size = Size(40f * s, 10f * s)
        )
        drawRect(
            color = Color.Black,
            topLeft = Offset(center.x + 15f * s, center.y - 5f * s),
            size = Size(40f * s, 10f * s),
            style = Stroke(width = 1.5f * s)
        )

        // 총구
        drawCircle(
            color = Color(0xFF1A1A1A),
            radius = 4f * s,
            center = Offset(center.x + 56f * s, center.y)
        )

        // === 해머 ===
        drawRect(
            color = RevolverGray,
            topLeft = Offset(center.x - 10f * s, center.y - 14f * s),
            size = Size(8f * s, 8f * s)
        )

        // === 트리거 ===
        drawRect(
            color = RevolverGray,
            topLeft = Offset(center.x + 2f * s, center.y + 6f * s),
            size = Size(3f * s, 10f * s)
        )

        // === 트리거 가드 ===
        drawArc(
            color = RevolverGray,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(center.x - 5f * s, center.y + 4f * s),
            size = Size(18f * s, 16f * s),
            style = Stroke(width = 2f * s)
        )

        // === 발사 효과 ===
        if (isFiring) {
            // 총구 화염
            val muzzleCenter = Offset(center.x + 58f * s, center.y)

            // 외부 화염 (노란색)
            val flamePath = Path().apply {
                moveTo(muzzleCenter.x, muzzleCenter.y - 15f * s)
                lineTo(muzzleCenter.x + 25f * s, muzzleCenter.y - 5f * s)
                lineTo(muzzleCenter.x + 35f * s, muzzleCenter.y)
                lineTo(muzzleCenter.x + 25f * s, muzzleCenter.y + 5f * s)
                lineTo(muzzleCenter.x, muzzleCenter.y + 15f * s)
                lineTo(muzzleCenter.x + 10f * s, muzzleCenter.y)
                close()
            }
            drawPath(flamePath, color = Color(0xFFFFCC00).copy(alpha = 0.8f))

            // 내부 화염 (주황)
            val innerFlamePath = Path().apply {
                moveTo(muzzleCenter.x, muzzleCenter.y - 8f * s)
                lineTo(muzzleCenter.x + 18f * s, muzzleCenter.y - 3f * s)
                lineTo(muzzleCenter.x + 22f * s, muzzleCenter.y)
                lineTo(muzzleCenter.x + 18f * s, muzzleCenter.y + 3f * s)
                lineTo(muzzleCenter.x, muzzleCenter.y + 8f * s)
                lineTo(muzzleCenter.x + 5f * s, muzzleCenter.y)
                close()
            }
            drawPath(innerFlamePath, color = Color(0xFFFF6600).copy(alpha = 0.9f))

            // 중심 화염 (흰색)
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = 5f * s,
                center = muzzleCenter
            )
        }
    }
}
