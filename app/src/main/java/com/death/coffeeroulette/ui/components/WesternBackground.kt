package com.death.coffeeroulette.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.death.coffeeroulette.ui.theme.*

/**
 * OK목장의 결투 풍 서부극 배경을 Canvas에 그림
 */
fun DrawScope.drawWesternBackground() {
    val w = size.width
    val h = size.height

    // === 하늘 (석양 그라데이션) ===
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1A0533),    // 상단 어두운 보라
                Color(0xFF4A1942),    // 중간 보라
                Color(0xFFCD4631),    // 석양 빨강
                Color(0xFFFF8C42),    // 석양 주황
                Color(0xFFFFD166),    // 석양 노랑
            ),
            startY = 0f,
            endY = h * 0.65f
        ),
        size = Size(w, h * 0.65f)
    )

    // === 해 ===
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFFFCC),
                Color(0xFFFFD700),
                Color(0xFFFF8C00).copy(alpha = 0f)
            ),
            center = Offset(w * 0.5f, h * 0.42f),
            radius = w * 0.15f
        ),
        center = Offset(w * 0.5f, h * 0.42f),
        radius = w * 0.15f
    )
    drawCircle(
        color = Color(0xFFFFF176),
        center = Offset(w * 0.5f, h * 0.42f),
        radius = w * 0.05f
    )

    // === 산 실루엣 (뒤) ===
    val mountainBack = Path().apply {
        moveTo(0f, h * 0.55f)
        lineTo(w * 0.15f, h * 0.38f)
        lineTo(w * 0.3f, h * 0.48f)
        lineTo(w * 0.45f, h * 0.35f)
        lineTo(w * 0.6f, h * 0.45f)
        lineTo(w * 0.75f, h * 0.3f)
        lineTo(w * 0.9f, h * 0.42f)
        lineTo(w, h * 0.5f)
        lineTo(w, h * 0.65f)
        lineTo(0f, h * 0.65f)
        close()
    }
    drawPath(mountainBack, color = Color(0xFF2D1B2E).copy(alpha = 0.6f))

    // === 산 실루엣 (앞) ===
    val mountainFront = Path().apply {
        moveTo(0f, h * 0.58f)
        lineTo(w * 0.1f, h * 0.48f)
        lineTo(w * 0.25f, h * 0.55f)
        lineTo(w * 0.4f, h * 0.45f)
        lineTo(w * 0.55f, h * 0.52f)
        lineTo(w * 0.7f, h * 0.42f)
        lineTo(w * 0.85f, h * 0.5f)
        lineTo(w, h * 0.55f)
        lineTo(w, h * 0.65f)
        lineTo(0f, h * 0.65f)
        close()
    }
    drawPath(mountainFront, color = Color(0xFF3D2B1F).copy(alpha = 0.8f))

    // === 바닥 (사막 모래) ===
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFC49A6C),
                Color(0xFFB8860B),
                Color(0xFF8B7355),
            ),
            startY = h * 0.6f,
            endY = h
        ),
        topLeft = Offset(0f, h * 0.6f),
        size = Size(w, h * 0.4f)
    )

    // === 왼쪽 건물 (살룬 실루엣) ===
    drawWesternBuilding(
        topLeft = Offset(w * 0.02f, h * 0.3f),
        buildingWidth = w * 0.12f,
        buildingHeight = h * 0.32f,
        color = Color(0xFF1A1A1A).copy(alpha = 0.7f)
    )

    // === 오른쪽 건물 ===
    drawWesternBuilding(
        topLeft = Offset(w * 0.86f, h * 0.35f),
        buildingWidth = w * 0.12f,
        buildingHeight = h * 0.28f,
        color = Color(0xFF1A1A1A).copy(alpha = 0.7f)
    )

    // === 선인장 ===
    drawCactus(Offset(w * 0.18f, h * 0.58f), scale = 0.8f)
    drawCactus(Offset(w * 0.82f, h * 0.56f), scale = 0.6f)

    // === 먼지 바람 효과 ===
    for (i in 0..5) {
        drawCircle(
            color = Color(0xFFC49A6C).copy(alpha = 0.1f),
            radius = w * 0.03f + (i * 5f),
            center = Offset(w * (0.2f + i * 0.12f), h * 0.7f)
        )
    }
}

private fun DrawScope.drawWesternBuilding(
    topLeft: Offset,
    buildingWidth: Float,
    buildingHeight: Float,
    color: Color
) {
    // 건물 본체
    drawRect(color = color, topLeft = topLeft, size = Size(buildingWidth, buildingHeight))

    // 지붕 (삼각형)
    val roofPath = Path().apply {
        moveTo(topLeft.x - buildingWidth * 0.1f, topLeft.y)
        lineTo(topLeft.x + buildingWidth * 0.5f, topLeft.y - buildingHeight * 0.15f)
        lineTo(topLeft.x + buildingWidth * 1.1f, topLeft.y)
        close()
    }
    drawPath(roofPath, color = color)

    // 창문
    val windowColor = Color(0xFFFFD700).copy(alpha = 0.3f)
    drawRect(
        color = windowColor,
        topLeft = Offset(topLeft.x + buildingWidth * 0.2f, topLeft.y + buildingHeight * 0.2f),
        size = Size(buildingWidth * 0.25f, buildingHeight * 0.15f)
    )
    drawRect(
        color = windowColor,
        topLeft = Offset(topLeft.x + buildingWidth * 0.55f, topLeft.y + buildingHeight * 0.2f),
        size = Size(buildingWidth * 0.25f, buildingHeight * 0.15f)
    )

    // 문
    drawRect(
        color = Color(0xFF4A3000).copy(alpha = 0.5f),
        topLeft = Offset(topLeft.x + buildingWidth * 0.3f, topLeft.y + buildingHeight * 0.55f),
        size = Size(buildingWidth * 0.4f, buildingHeight * 0.45f)
    )
}

private fun DrawScope.drawCactus(position: Offset, scale: Float = 1f) {
    val color = Color(0xFF2E5B2E).copy(alpha = 0.7f)
    val s = scale

    // 몸체
    drawRect(
        color = color,
        topLeft = Offset(position.x - 4f * s, position.y - 40f * s),
        size = Size(8f * s, 40f * s)
    )

    // 왼쪽 팔
    drawRect(
        color = color,
        topLeft = Offset(position.x - 16f * s, position.y - 30f * s),
        size = Size(12f * s, 6f * s)
    )
    drawRect(
        color = color,
        topLeft = Offset(position.x - 16f * s, position.y - 40f * s),
        size = Size(6f * s, 16f * s)
    )

    // 오른쪽 팔
    drawRect(
        color = color,
        topLeft = Offset(position.x + 4f * s, position.y - 25f * s),
        size = Size(12f * s, 6f * s)
    )
    drawRect(
        color = color,
        topLeft = Offset(position.x + 12f * s, position.y - 35f * s),
        size = Size(6f * s, 16f * s)
    )
}
