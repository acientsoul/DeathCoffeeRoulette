package com.death.coffeeroulette.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.death.coffeeroulette.game.Member
import com.death.coffeeroulette.game.RouletteViewModel
import com.death.coffeeroulette.ui.components.drawRevolver
import com.death.coffeeroulette.ui.components.drawSouthParkCharacter
import com.death.coffeeroulette.ui.components.drawWesternBackground
import com.death.coffeeroulette.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * 게임 화면
 * - OK목장의 결투 배경
 * - 가운데 리볼버, 원형으로 참가자 배치
 * - 리볼버가 회전하며 참가자를 겨눔
 * - 랜덤 선택 후 발사
 */
@Composable
fun GameScreen(viewModel: RouletteViewModel) {

    val participants = viewModel.participants
    val currentTarget by viewModel.currentTargetIndex
    val isSpinning by viewModel.isSpinning
    val isFiring by viewModel.isFiring
    val winner by viewModel.winner

    // 스핀 시작 트리거
    LaunchedEffect(Unit) {
        viewModel.startGame()
    }

    // 리볼버 회전 각도 계산
    val targetAngle = if (participants.isNotEmpty()) {
        (currentTarget.toFloat() / participants.size) * 360f - 90f
    } else 0f

    // 부드러운 각도 애니메이션
    val animatedAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = tween(
            durationMillis = if (isSpinning) 150 else 500,
            easing = if (isSpinning) LinearEasing else FastOutSlowInEasing
        ),
        label = "revolverAngle"
    )

    // 발사 시 화면 흔들림
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isFiring) {
        if (isFiring) {
            repeat(5) {
                shakeOffset.animateTo(10f, tween(30))
                shakeOffset.animateTo(-10f, tween(30))
            }
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = shakeOffset.value.dp)
        ) {
            val w = size.width
            val h = size.height
            val centerX = w / 2f
            val centerY = h / 2f

            // === 서부 배경 ===
            drawWesternBackground()

            // === 참가자 원형 배치 ===
            val radius = minOf(w, h) * 0.32f
            val characterScale = when {
                participants.size <= 4 -> 1.2f
                participants.size <= 6 -> 1.0f
                participants.size <= 8 -> 0.85f
                else -> 0.7f
            }

            participants.forEachIndexed { index, member ->
                val angle = (index.toFloat() / participants.size) * 2f * PI.toFloat() - PI.toFloat() / 2f
                val charX = centerX + radius * cos(angle)
                val charY = centerY + radius * sin(angle)

                val isCurrentTarget = index == currentTarget
                val isWinner = winner?.id == member.id

                // 현재 타겟 하이라이트
                if (isCurrentTarget && isSpinning) {
                    drawCircle(
                        color = AccentRed.copy(alpha = 0.3f),
                        radius = 45f * characterScale,
                        center = Offset(charX, charY)
                    )
                }

                // 당첨자 강조
                if (isWinner && isFiring) {
                    drawCircle(
                        color = ExplosionRed.copy(alpha = 0.5f),
                        radius = 55f * characterScale,
                        center = Offset(charX, charY)
                    )
                }

                // 사우스파크 캐릭터 그리기
                drawSouthParkCharacter(
                    center = Offset(charX, charY),
                    scale = characterScale,
                    outfitIndex = member.outfitIndex,
                    name = member.name,
                    isEliminated = isWinner && isFiring
                )

                // 이름 표시
                drawCharacterName(
                    name = member.name,
                    position = Offset(charX, charY + 60f * characterScale),
                    isTarget = isCurrentTarget && isSpinning,
                    isWinner = isWinner && isFiring
                )
            }

            // === 리볼버 (중앙) ===
            drawRevolver(
                center = Offset(centerX, centerY),
                scale = when {
                    participants.size <= 4 -> 1.5f
                    participants.size <= 6 -> 1.2f
                    else -> 1.0f
                },
                rotationDegrees = animatedAngle,
                isFiring = isFiring
            )
        }

        // === 상단 텍스트 ===
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when {
                    isFiring && winner != null -> ""
                    isSpinning -> "☠️ 누가 쏠까..."
                    else -> "준비 중..."
                },
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    ),
                    textAlign = TextAlign.Center
                )
            )
        }

        // === 당첨 말풍선 ===
        if (isFiring && winner != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                SpeechBubble(winnerName = winner!!.name)
            }
        }
    }
}

@Composable
private fun SpeechBubble(winnerName: String) {
    // 반짝임 애니메이션
    val infiniteTransition = rememberInfiniteTransition(label = "bubble")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bubbleAlpha"
    )

    Canvas(
        modifier = Modifier
            .width(280.dp)
            .height(80.dp)
    ) {
        val w = size.width
        val h = size.height

        // 말풍선 배경
        drawRoundRect(
            color = Color.White.copy(alpha = alpha),
            topLeft = Offset(0f, 0f),
            size = size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
        )

        // 말풍선 꼬리 (삼각형)
        val tailPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(w / 2f - 15f, h)
            lineTo(w / 2f, h + 20f)
            lineTo(w / 2f + 15f, h)
            close()
        }
        drawPath(tailPath, color = Color.White.copy(alpha = alpha))
    }

    // 텍스트는 Canvas 위에 오버레이
    Column(
        modifier = Modifier
            .width(280.dp)
            .height(80.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "☕ 당첨 ☕",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF333333)
        )
        Text(
            text = winnerName,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AccentRed
        )
    }
}

/**
 * 캐릭터 이름을 Canvas에 그리기 (nativeCanvas 사용)
 */
private fun DrawScope.drawCharacterName(
    name: String,
    position: Offset,
    isTarget: Boolean,
    isWinner: Boolean
) {
    val paint = android.graphics.Paint().apply {
        color = when {
            isWinner -> android.graphics.Color.RED
            isTarget -> android.graphics.Color.YELLOW
            else -> android.graphics.Color.WHITE
        }
        textSize = if (isWinner || isTarget) 32f else 26f
        textAlign = android.graphics.Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
        setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
    }

    drawContext.canvas.nativeCanvas.drawText(
        name,
        position.x,
        position.y,
        paint
    )
}
