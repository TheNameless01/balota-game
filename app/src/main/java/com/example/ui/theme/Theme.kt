package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BalotaLightColorScheme = lightColorScheme(
    primary = BalotaForestGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4EED8),
    onPrimaryContainer = BalotaForestGreenDark,
    
    secondary = BalotaWarmYellow,
    onSecondary = Color(0xFF3E2723),
    secondaryContainer = Color(0xFFFFF3CD),
    onSecondaryContainer = Color(0xFF5D4037),
    
    tertiary = BalotaSkyBlue,
    onTertiary = Color.White,
    tertiaryContainer = BalotaSkyBlueLight,
    onTertiaryContainer = Color(0xFF01579B),
    
    background = BalotaCreamBackground,
    onBackground = TextPrimary,
    
    surface = BalotaCreamSurface,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFFF0EAE1),
    onSurfaceVariant = TextSecondary,
    
    error = BalotaIncorrectRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BalotaLightColorScheme,
        typography = Typography,
        content = content
    )
}
