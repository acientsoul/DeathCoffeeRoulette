package com.death.coffeeroulette.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.death.coffeeroulette.game.Member
import com.death.coffeeroulette.game.RouletteViewModel
import com.death.coffeeroulette.ui.components.drawSouthParkCharacter
import com.death.coffeeroulette.ui.components.drawWesternBackground
import com.death.coffeeroulette.ui.theme.*

/**
 * 결과 화면
 * - 당첨자 발표 (사우스파크 캐릭터 + X 눈)
 * - 상단에 "당첨 : 이름" 말풍선
 * - 뒤로가기 버튼 → 게임 재시작
 */
@Composable
fun ResultScreen(
    viewModel: RouletteViewModel,
    onBack: () -> Unit
) {
    val winner = viewModel.winner.value

    // 텍스트 반짝임
    val infiniteTransition = rememberInfiniteTransition(label = "result")
    val textAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "resultFlash"
    )

    // 캐릭터 바운스
    val bounceY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // === 배경 ===
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawWesternBackground()

            // 어두운 오버레이
            drawRect(
                color = Color.Black.copy(alpha = 0.4f)
            )

            // 중앙 스포트라이트
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(size.width / 2f, size.height * 0.5f),
                    radius = size.minDimension * 0.4f
                ),
                center = Offset(size.width / 2f, size.height * 0.5f),
                radius = size.minDimension * 0.4f
            )
        }

        // === 당첨자 캐릭터 (크게) ===
        if (winner != null) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = bounceY.dp)
            ) {
                drawSouthParkCharacter(
                    center = Offset(size.width / 2f, size.height * 0.5f),
                    scale = 3.5f,
                    outfitIndex = winner.outfitIndex,
                    name = winner.name,
                    isEliminated = true
                )

                // 이름 표시
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.RED
                    textSize = 50f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                    isAntiAlias = true
                    setShadowLayer(6f, 3f, 3f, android.graphics.Color.BLACK)
                }
                drawContext.canvas.nativeCanvas.drawText(
                    winner.name,
                    size.width / 2f,
                    size.height * 0.5f + 230f,
                    paint
                )
            }
        }

        // === 상단 당첨 말풍선 ===
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 말풍선 배경
            Box(
                modifier = Modifier
                    .background(
                        Color.White.copy(alpha = textAlpha * 0.95f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "☕ 당첨 ☕",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF555555),
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = winner?.name ?: "???",
                        style = TextStyle(
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentRed,
                            textAlign = TextAlign.Center,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.2f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                }
            }

            // 말풍선 꼬리
            Canvas(modifier = Modifier.size(30.dp)) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width / 2f, size.height)
                    lineTo(size.width, 0f)
                    close()
                }
                drawPath(path, color = Color.White.copy(alpha = textAlpha * 0.95f))
            }
        }

        // === 하단 메시지 ===
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "오늘의 커피는 ${winner?.name ?: "???"} 님이 쏩니다! ☕🎉",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = AccentGold.copy(alpha = textAlpha),
                    textAlign = TextAlign.Center,
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(1f, 1f),
                        blurRadius = 3f
                    )
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // 뒤로가기 (재시작) 버튼
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentRed
                ),
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "🔄 다시 하기",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
