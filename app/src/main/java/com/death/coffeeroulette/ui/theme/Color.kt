package com.death.coffeeroulette.ui.theme

import androidx.compose.ui.graphics.Color

// 앱 기본 색상
val DarkBackground = Color(0xFF1A1A2E)
val DarkSurface = Color(0xFF16213E)
val AccentRed = Color(0xFFE94560)
val AccentGold = Color(0xFFFFD700)
val TextWhite = Color(0xFFF5F5F5)

// 폭발 효과 색상
val ExplosionOrange = Color(0xFFFF6600)
val ExplosionRed = Color(0xFFFF0000)
val ExplosionYellow = Color(0xFFFFCC00)
val ExplosionWhite = Color(0xFFFFFFCC)

// 서부 배경 색상
val WesternSky = Color(0xFFE8A87C)
val WesternSand = Color(0xFFD2B48C)
val WesternDirt = Color(0xFF8B7355)
val WesternBuilding = Color(0xFF8B6914)
val WesternBuildingDark = Color(0xFF6B4F12)
val SunsetOrange = Color(0xFFFF7043)
val SunsetPink = Color(0xFFFF8A80)

// 리볼버 색상
val RevolverGray = Color(0xFF4A4A4A)
val RevolverDarkGray = Color(0xFF333333)
val RevolverBrown = Color(0xFF8B4513)

// 사우스파크 캐릭터 복장 색상 팔레트 (최대 12명)
data class CharacterOutfit(
    val hatColor: Color,
    val shirtColor: Color,
    val pantsColor: Color,
    val skinColor: Color = Color(0xFFFFDBAC),
    val hairColor: Color = Color(0xFF3D2B1F)
)

val characterOutfits = listOf(
    CharacterOutfit(Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFF424242)),       // 빨간모자, 파란셔츠
    CharacterOutfit(Color(0xFF43A047), Color(0xFFFF8F00), Color(0xFF5D4037)),       // 초록모자, 주황셔츠
    CharacterOutfit(Color(0xFF1565C0), Color(0xFFD32F2F), Color(0xFF37474F)),       // 파란모자, 빨간셔츠
    CharacterOutfit(Color(0xFFFF8F00), Color(0xFF6A1B9A), Color(0xFF263238)),       // 주황모자, 보라셔츠
    CharacterOutfit(Color(0xFF6A1B9A), Color(0xFF00897B), Color(0xFF4E342E)),       // 보라모자, 청록셔츠
    CharacterOutfit(Color(0xFF00897B), Color(0xFFE53935), Color(0xFF455A64)),       // 청록모자, 빨간셔츠
    CharacterOutfit(Color(0xFFD81B60), Color(0xFF1565C0), Color(0xFF3E2723)),       // 분홍모자, 파란셔츠
    CharacterOutfit(Color(0xFF558B2F), Color(0xFFEF6C00), Color(0xFF37474F)),       // 올리브모자, 오렌지셔츠
    CharacterOutfit(Color(0xFF4527A0), Color(0xFF2E7D32), Color(0xFF424242)),       // 남색모자, 초록셔츠
    CharacterOutfit(Color(0xFFBF360C), Color(0xFF0277BD), Color(0xFF3E2723)),       // 갈색모자, 하늘셔츠
    CharacterOutfit(Color(0xFF00838F), Color(0xFFC62828), Color(0xFF263238)),       // 시안모자, 진빨간셔츠
    CharacterOutfit(Color(0xFF9E9D24), Color(0xFF283593), Color(0xFF4E342E)),       // 겨자모자, 진파란셔츠
)
