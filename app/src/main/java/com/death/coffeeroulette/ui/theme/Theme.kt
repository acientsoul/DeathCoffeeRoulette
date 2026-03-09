package com.death.coffeeroulette.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentRed,
    secondary = AccentGold,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = TextWhite,
    onSecondary = DarkBackground,
    onBackground = TextWhite,
    onSurface = TextWhite,
)

@Composable
fun DeathCoffeeRouletteTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
