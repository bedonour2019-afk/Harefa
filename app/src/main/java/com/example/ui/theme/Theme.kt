package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

private val LightColorScheme = lightColorScheme(
    primary = PitchGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = PitchGreenDark,
    secondary = ChampionGoldDark,
    onSecondary = Color.White,
    secondaryContainer = GoldSurfaceLight,
    onSecondaryContainer = ChampionGoldDark,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF2E4633),
    outline = Color(0xFFB0C9B3)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
