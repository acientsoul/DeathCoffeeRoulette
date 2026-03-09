package com.death.coffeeroulette.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.death.coffeeroulette.game.Member
import com.death.coffeeroulette.game.RouletteViewModel
import com.death.coffeeroulette.kakao.KakaoAuthManager
import com.death.coffeeroulette.ui.theme.*

/**
 * 멤버 선택 화면
 * - 카카오톡 친구 목록 불러오기
 * - 수동 이름 입력
 * - 참가/비참가 토글
 * - 멤버 삭제
 */
@Composable
fun MemberScreen(
    viewModel: RouletteViewModel,
    onStartGame: () -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var isKakaoLoggedIn by remember { mutableStateOf(false) }
    var isLoadingFriends by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // 로그인 상태 확인
    LaunchedEffect(Unit) {
        KakaoAuthManager.checkLoginStatus { loggedIn ->
            isKakaoLoggedIn = loggedIn
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // === 상단 타이틀 ===
        Text(
            text = "☕ 참가자 선택",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = AccentGold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            textAlign = TextAlign.Center
        )

        // === 카카오톡 친구 불러오기 ===
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "카카오톡에서 불러오기",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (!isKakaoLoggedIn) {
                    Button(
                        onClick = {
                            KakaoAuthManager.login(context) { success, error ->
                                isKakaoLoggedIn = success
                                if (!success) errorMessage = error ?: "로그인 실패"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFEE500)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "💬 카카오 로그인",
                            color = Color(0xFF191919),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            isLoadingFriends = true
                            errorMessage = null
                            KakaoAuthManager.getFriends { friends, error ->
                                isLoadingFriends = false
                                if (friends != null) {
                                    viewModel.addMembersFromKakao(friends)
                                } else {
                                    errorMessage = error ?: "친구 목록 조회 실패"
                                }
                            }
                        },
                        enabled = !isLoadingFriends,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFEE500)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isLoadingFriends) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color(0xFF191919),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            "👥 친구 목록 불러오기",
                            color = Color(0xFF191919),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = AccentRed,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // === 수동 입력 ===
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    placeholder = {
                        Text("이름 직접 입력", color = Color.White.copy(alpha = 0.4f))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = AccentGold,
                        focusedBorderColor = AccentGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (nameInput.isNotBlank()) {
                                viewModel.addMember(nameInput)
                                nameInput = ""
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            viewModel.addMember(nameInput)
                            nameInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("추가", fontWeight = FontWeight.Bold)
                }
            }
        }

        // === 참가자 수 표시 ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "전체 ${viewModel.allMembers.size}명 / 참가 ${viewModel.participants.size}명",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            if (viewModel.allMembers.isNotEmpty()) {
                TextButton(onClick = { viewModel.allMembers.clear() }) {
                    Text("전체 삭제", color = AccentRed, fontSize = 13.sp)
                }
            }
        }

        // === 멤버 리스트 ===
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(
                items = viewModel.allMembers.toList(),
                key = { _, member -> member.id }
            ) { index, member ->
                MemberItem(
                    member = member,
                    index = index,
                    onToggle = { viewModel.toggleParticipation(member.id) },
                    onRemove = { viewModel.removeMember(member.id) }
                )
            }
        }

        // === 게임 시작 버튼 ===
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onStartGame,
            enabled = viewModel.participants.size >= 2,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentRed,
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (viewModel.participants.size >= 2) "🔫 룰렛 시작!" else "2명 이상 필요합니다",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MemberItem(
    member: Member,
    index: Int,
    onToggle: () -> Unit,
    onRemove: () -> Unit
) {
    val outfit = characterOutfits[member.outfitIndex % characterOutfits.size]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (member.isParticipating) DarkSurface
                else DarkSurface.copy(alpha = 0.4f)
            )
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 복장 색 미리보기
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(outfit.hatColor)
                .border(2.dp, outfit.shirtColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${index + 1}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 이름
        Text(
            text = member.name,
            color = if (member.isParticipating) Color.White else Color.White.copy(alpha = 0.4f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        // 참가 상태
        Text(
            text = if (member.isParticipating) "참가" else "불참",
            color = if (member.isParticipating) Color(0xFF4CAF50) else Color.Gray,
            fontSize = 13.sp,
            modifier = Modifier.padding(end = 8.dp)
        )

        // 삭제 버튼
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
        ) {
            Text("✕", color = AccentRed, fontSize = 18.sp)
        }
    }
}
