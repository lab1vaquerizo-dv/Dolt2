package com.example.dolt2.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DoltBlue,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = BlueSurface,
    onPrimaryContainer = BlueOnSurface,
    secondary = DoltOrange,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    background = androidx.compose.ui.graphics.Color(0xFFFDFCFF),
    surface = androidx.compose.ui.graphics.Color(0xFFFDFCFF),
    onSurface = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF41484D),
    error = androidx.compose.ui.graphics.Color(0xFFBA1A1A),
    onError = androidx.compose.ui.graphics.Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = DoltBlueDark,
    onPrimary = androidx.compose.ui.graphics.Color(0xFF003355),
    primaryContainer = BlueSurfaceDark,
    onPrimaryContainer = BlueOnSurfaceDark,
    secondary = DoltOrangeDark,
    onSecondary = androidx.compose.ui.graphics.Color(0xFF5F1600),
    background = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    surface = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
    onSurface = androidx.compose.ui.graphics.Color(0xFFE2E2E6),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFC1C7CE),
    error = androidx.compose.ui.graphics.Color(0xFFFFB4AB),
    onError = androidx.compose.ui.graphics.Color(0xFF690005)
)

@Composable
fun DoltTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primaryContainer.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}