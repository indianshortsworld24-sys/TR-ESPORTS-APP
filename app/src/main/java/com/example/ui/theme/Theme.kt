package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsportsDarkColorScheme = darkColorScheme(
    primary = PrimaryRed,
    onPrimary = Color.White,
    secondary = AccentGold,
    onSecondary = Color.Black,
    tertiary = LiveGreen,
    background = BackgroundDark,
    onBackground = TextWhite,
    surface = SurfaceDark,
    onSurface = TextWhite,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = TextGray,
    outline = BorderGray
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for the esports vibe
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsportsDarkColorScheme,
        typography = Typography,
        content = content
    )
}
