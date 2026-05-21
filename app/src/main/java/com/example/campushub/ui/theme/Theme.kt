package com.example.campushub.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimary,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = PrimaryBlueDark,
    secondary = AccentOrange,
    onSecondary = OnPrimary,
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = Color(0xFFC2410C),
    tertiary = AccentViolet,
    onTertiary = OnPrimary,
    tertiaryContainer = Color(0xFFEDE9FE),
    onTertiaryContainer = AccentVioletDark,
    background = BackgroundGray,
    onBackground = TextPrimary,
    surface = CardLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = TextSecondary,
    outline = TextTertiary,
    outlineVariant = DividerGray,
    error = ErrorRed,
    onError = OnPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = AccentOrangeLight,
    onSecondary = OnPrimary,
    secondaryContainer = Color(0xFF7C2D12),
    onSecondaryContainer = Color(0xFFFFEDD5),
    tertiary = AccentVioletLight,
    onTertiary = OnPrimary,
    tertiaryContainer = AccentVioletDark,
    onTertiaryContainer = Color(0xFFEDE9FE),
    background = SurfaceDark,
    onBackground = Color(0xFFE2E8F0),
    surface = CardDark,
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFFCA5A5),
    onError = SurfaceDark
)

@Composable
fun CampusHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
