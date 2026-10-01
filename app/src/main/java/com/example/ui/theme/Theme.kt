package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Permanent Sleek Dark Stadium Palette
private val DarkColorScheme = darkColorScheme(
    primary = PitchAccentMint,
    onPrimary = Color.Black,
    primaryContainer = PitchGreenDark,
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = ChampionGold,
    onSecondary = Color.Black,
    secondaryContainer = GoldSurface,
    onSecondaryContainer = ChampionGoldBright,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC8E6C9),
    outline = Color(0xFF3B5640)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
