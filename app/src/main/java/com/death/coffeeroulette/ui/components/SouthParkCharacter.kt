package com.death.coffeeroulette.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.death.coffeeroulette.ui.theme.CharacterOutfit
import com.death.coffeeroulette.ui.theme.characterOutfits

/**
 * 사우스파크 풍 캐릭터를 Canvas에 그리는 유틸리티
 * - 둥근 머리, 단순한 몸체
 * - 각 캐릭터마다 다른 복장(모자, 셔츠, 바지 색상)
 * - 당첨 시 눈이 X로 변경
 */
fun DrawScope.drawSouthParkCharacter(
    center: Offset,
    scale: Float = 1f,
    outfitIndex: Int = 0,
    name: String = "",
    isEliminated: Boolean = false
) {
    val outfit = characterOutfits[outfitIndex % characterOutfits.size]
    val s = scale

    // === 다리 (바지) ===
    val legWidth = 10f * s
    val legHeight = 25f * s
    val legY = center.y + 25f * s

    // 왼쪽 다리
    drawRect(
        color = outfit.pantsColor,
        topLeft = Offset(center.x - 12f * s, legY),
        size = Size(legWidth, legHeight)
    )
    // 오른쪽 다리
    drawRect(
        color = outfit.pantsColor,
        topLeft = Offset(center.x + 2f * s, legY),
        size = Size(legWidth, legHeight)
    )

    // 신발
    drawRect(
        color = Color(0xFF1A1A1A),
        topLeft = Offset(center.x - 14f * s, legY + legHeight),
        size = Size(14f * s, 5f * s)
    )
    drawRect(
        color = Color(0xFF1A1A1A),
        topLeft = Offset(center.x, legY + legHeight),
        size = Size(14f * s, 5f * s)
    )

    // === 몸체 (셔츠) ===
    val bodyTop = center.y - 5f * s
    val bodyHeight = 32f * s
    drawRect(
        color = outfit.shirtColor,
        topLeft = Offset(center.x - 18f * s, bodyTop),
        size = Size(36f * s, bodyHeight)
    )

    // 팔 (셔츠 색상)
    drawRect(
        color = outfit.shirtColor,
        topLeft = Offset(center.x - 28f * s, bodyTop + 2f * s),
        size = Size(12f * s, 22f * s)
    )
    drawRect(
        color = outfit.shirtColor,
        topLeft = Offset(center.x + 16f * s, bodyTop + 2f * s),
        size = Size(12f * s, 22f * s)
    )

    // 손
    drawCircle(
        color = outfit.skinColor,
        radius = 5f * s,
        center = Offset(center.x - 28f * s, bodyTop + 26f * s)
    )
    drawCircle(
        color = outfit.skinColor,
        radius = 5f * s,
        center = Offset(center.x + 28f * s, bodyTop + 26f * s)
    )

    // === 머리 ===
    val headCenter = Offset(center.x, center.y - 22f * s)
    val headRadius = 20f * s

    // 머리 (피부색 원)
    drawCircle(
        color = outfit.skinColor,
        radius = headRadius,
        center = headCenter
    )

    // === 눈 ===
    if (isEliminated) {
        // X 눈 (당첨자)
        val eyeSize = 6f * s
        val leftEye = Offset(headCenter.x - 7f * s, headCenter.y - 2f * s)
        val rightEye = Offset(headCenter.x + 7f * s, headCenter.y - 2f * s)

        // 왼쪽 X
        drawLine(Color.Black, Offset(leftEye.x - eyeSize, leftEye.y - eyeSize), Offset(leftEye.x + eyeSize, leftEye.y + eyeSize), strokeWidth = 3f * s)
        drawLine(Color.Black, Offset(leftEye.x + eyeSize, leftEye.y - eyeSize), Offset(leftEye.x - eyeSize, leftEye.y + eyeSize), strokeWidth = 3f * s)

        // 오른쪽 X
        drawLine(Color.Black, Offset(rightEye.x - eyeSize, rightEye.y - eyeSize), Offset(rightEye.x + eyeSize, rightEye.y + eyeSize), strokeWidth = 3f * s)
        drawLine(Color.Black, Offset(rightEye.x + eyeSize, rightEye.y - eyeSize), Offset(rightEye.x - eyeSize, rightEye.y + eyeSize), strokeWidth = 3f * s)
    } else {
        // 일반 눈 (흰 원 + 검은 점)
        drawCircle(Color.White, radius = 7f * s, center = Offset(headCenter.x - 7f * s, headCenter.y - 2f * s))
        drawCircle(Color.White, radius = 7f * s, center = Offset(headCenter.x + 7f * s, headCenter.y - 2f * s))
        drawCircle(Color.Black, radius = 3f * s, center = Offset(headCenter.x - 6f * s, headCenter.y - 2f * s))
        drawCircle(Color.Black, radius = 3f * s, center = Offset(headCenter.x + 6f * s, headCenter.y - 2f * s))
    }

    // 입
    if (isEliminated) {
        // 당첨: 놀란 O 입
        drawCircle(Color.Black, radius = 4f * s, center = Offset(headCenter.x, headCenter.y + 10f * s))
        drawCircle(Color(0xFF8B0000), radius = 3f * s, center = Offset(headCenter.x, headCenter.y + 10f * s))
    } else {
        // 일반: 작은 미소
        drawArc(
            color = Color.Black,
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(headCenter.x - 5f * s, headCenter.y + 6f * s),
            size = Size(10f * s, 6f * s),
            style = Stroke(width = 2f * s)
        )
    }

    // === 모자 ===
    // 모자 챙
    drawRect(
        color = outfit.hatColor,
        topLeft = Offset(headCenter.x - 22f * s, headCenter.y - headRadius - 3f * s),
        size = Size(44f * s, 6f * s)
    )
    // 모자 윗부분
    drawRect(
        color = outfit.hatColor,
        topLeft = Offset(headCenter.x - 16f * s, headCenter.y - headRadius - 18f * s),
        size = Size(32f * s, 18f * s)
    )
    // 모자 밴드
    drawRect(
        color = Color.Black.copy(alpha = 0.3f),
        topLeft = Offset(headCenter.x - 16f * s, headCenter.y - headRadius - 6f * s),
        size = Size(32f * s, 4f * s)
    )
}
