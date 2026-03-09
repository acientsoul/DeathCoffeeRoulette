package com.death.coffeeroulette.game

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

data class Member(
    val id: String,
    val name: String,
    val isParticipating: Boolean = true,
    val outfitIndex: Int = 0
)

enum class GamePhase {
    INTRO,
    MEMBER_SELECT,
    SPINNING,
    RESULT
}

class RouletteViewModel : ViewModel() {

    // === 게임 상태 ===
    val gamePhase = mutableStateOf(GamePhase.INTRO)

    // === 멤버 관리 ===
    val allMembers = mutableStateListOf<Member>()
    val participants: List<Member>
        get() = allMembers.filter { it.isParticipating }

    // === 룰렛 애니메이션 상태 ===
    val currentTargetIndex = mutableStateOf(0)    // 현재 총이 겨누는 인덱스
    val isSpinning = mutableStateOf(false)
    val spinSpeed = mutableStateOf(0f)

    // === 결과 ===
    val winner = mutableStateOf<Member?>(null)
    val isFiring = mutableStateOf(false)

    // 인트로 → 멤버 선택
    fun goToMemberSelect() {
        gamePhase.value = GamePhase.MEMBER_SELECT
    }

    // 멤버 추가 (수동)
    fun addMember(name: String) {
        if (name.isBlank()) return
        val id = "manual_${System.currentTimeMillis()}_${Random.nextInt()}"
        val outfitIdx = allMembers.size
        allMembers.add(Member(id = id, name = name.trim(), outfitIndex = outfitIdx))
    }

    // 카카오 친구 목록에서 멤버 추가
    fun addMembersFromKakao(friends: List<Pair<String, String>>) {
        friends.forEach { (id, name) ->
            if (allMembers.none { it.id == id }) {
                val outfitIdx = allMembers.size
                allMembers.add(Member(id = id, name = name, outfitIndex = outfitIdx))
            }
        }
    }

    // 참가 토글
    fun toggleParticipation(memberId: String) {
        val idx = allMembers.indexOfFirst { it.id == memberId }
        if (idx >= 0) {
            allMembers[idx] = allMembers[idx].copy(isParticipating = !allMembers[idx].isParticipating)
        }
    }

    // 멤버 삭제
    fun removeMember(memberId: String) {
        allMembers.removeAll { it.id == memberId }
    }

    // === 게임 시작 (러시안 룰렛 스핀) ===
    fun startGame() {
        if (participants.size < 2) return

        gamePhase.value = GamePhase.SPINNING
        isSpinning.value = true
        isFiring.value = false
        winner.value = null

        // 당첨자 미리 결정 (SecureRandom 기반)
        val winnerIndex = Random.nextInt(participants.size)

        viewModelScope.launch {
            // 빠르게 돌다가 점점 느려짐
            var speed = 80L  // 초기 간격 (ms) - 빠름
            var currentIdx = 0
            val totalSpins = participants.size * 4 + winnerIndex  // 최소 4바퀴 + 당첨자 위치

            for (i in 0 until totalSpins) {
                currentIdx = i % participants.size
                currentTargetIndex.value = currentIdx
                delay(speed)

                // 점점 느려짐 (후반부에 더 느려짐)
                val progress = i.toFloat() / totalSpins
                speed = when {
                    progress < 0.5f -> 80L
                    progress < 0.7f -> 120L
                    progress < 0.85f -> 200L
                    progress < 0.93f -> 350L
                    else -> 500L
                }
            }

            // 마지막: 당첨자에서 멈춤
            currentTargetIndex.value = winnerIndex
            delay(500)

            // 발사!
            isFiring.value = true
            winner.value = participants[winnerIndex]
            delay(800)

            isSpinning.value = false
            gamePhase.value = GamePhase.RESULT
        }
    }

    // 재시작
    fun restartGame() {
        gamePhase.value = GamePhase.MEMBER_SELECT
        isSpinning.value = false
        isFiring.value = false
        winner.value = null
        currentTargetIndex.value = 0
    }

    // 완전 초기화
    fun resetAll() {
        gamePhase.value = GamePhase.INTRO
        allMembers.clear()
        isSpinning.value = false
        isFiring.value = false
        winner.value = null
        currentTargetIndex.value = 0
    }
}
