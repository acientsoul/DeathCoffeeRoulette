package com.death.coffeeroulette.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.death.coffeeroulette.ui.components.drawBombExplosion
import com.death.coffeeroulette.ui.components.drawPulsingExplosion
import com.death.coffeeroulette.ui.theme.*
import kotlinx.coroutines.delay

/**
 * 인트로 화면
 * - 폭탄 폭발 시각효과 안에 제목이 반짝이며 등장
 * - 탭하면 멤버 선택 화면으로 이동
 */
@Composable
fun IntroScreen(onStartClick: () -> Unit) {

    // 초기 폭발 애니메이션
    var showInitialExplosion by remember { mutableStateOf(true) }
    val explosionProgress = remember { Animatable(0f) }

    // 반복 펄스 애니메이션
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAnim"
    )

    // 제목 반짝임
    val titleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleFlash"
    )

    // 제목 크기 펄스
    val titleScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleScale"
    )

    // 부제목 표시
    var showSubtitle by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // 초기 폭발 애니메이션 실행
        explosionProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1500, easing = FastOutSlowInEasing)
        )
        showInitialExplosion = false
        delay(300)
        showSubtitle = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onStartClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // 배경 폭발 효과
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)

            // 어두운 배경
            drawRect(color = DarkBackground)

            if (showInitialExplosion) {
                // 초기 대 폭발
                drawBombExplosion(
                    center = center,
                    progress = explosionProgress.value,
                    maxRadius = size.minDimension * 0.8f
                )
            } else {
                // 반복 펄스 폭발 (뒤에서 반짝임)
                drawPulsingExplosion(
                    center = center,
                    pulseProgress = pulseProgress,
                    baseRadius = size.minDimension * 0.35f
                )
            }
        }

        // 제목 텍스트
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "☠️",
                fontSize = (60 * titleScale).sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "죽음의",
                style = TextStyle(
                    fontSize = (28 * titleScale).sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentGold.copy(alpha = titleAlpha),
                    shadow = Shadow(
                        color = ExplosionOrange,
                        offset = Offset(2f, 2f),
                        blurRadius = 8f
                    ),
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = "커피 룰렛",
                style = TextStyle(
                    fontSize = (46 * titleScale).sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White.copy(alpha = titleAlpha),
                    shadow = Shadow(
                        color = AccentRed,
                        offset = Offset(3f, 3f),
                        blurRadius = 12f
                    ),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "☕ DEATH COFFEE ROULETTE ☕",
                style = TextStyle(
                    fontSize = (14 * titleScale).sp,
                    fontWeight = FontWeight.Medium,
                    color = AccentGold.copy(alpha = titleAlpha * 0.8f),
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(bottom = 40.dp)
            )

            if (showSubtitle) {
                Text(
                    text = "화면을 터치하여 시작",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = titleAlpha * 0.6f),
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
