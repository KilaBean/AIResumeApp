package com.example.airesume.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
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
    // Explicitly set container colors to White to prevent light blue derivations
    primaryContainer = White,
    onPrimaryContainer = PrimaryBlue, // Content on primaryContainer should contrast well

    secondary = SecondaryBlue,
    onSecondary = OnSecondary,
    // Explicitly set container colors to White
    secondaryContainer = White,
    onSecondaryContainer = SecondaryBlue,

    tertiary = TertiaryBlue,
    onTertiary = OnTertiary,
    // Explicitly set container colors to White
    tertiaryContainer = White,
    onTertiaryContainer = TertiaryBlue,

    background = White, // Base screen background
    onBackground = Gray10, // Content on the background

    surface = White,    // Default surface for cards, sheets, etc.
    onSurface = Gray10, // Content on surfaces

    surfaceVariant = Gray95, // A very light gray for subtle variations, or use White
    onSurfaceVariant = Gray50, // Content on surface variants

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline = Gray80, // For outlined components
    // outlineVariant = Gray90, // Optional: for lighter outlines

    // Add more if your design system uses them
    // scrim = Color(0x99000000), // Default scrim (for modals) usually fine
    // inversePrimary = Color(0xFFBBDEFB), // Often a lighter version of primary
    // inverseSurface = Gray20,
    // inverseOnSurface = White
)

@Composable
fun AIResumeTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // This theme is always light

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar color to your primary blue
            window.statusBarColor = colorScheme.primary.toArgb()
            // Set status bar icons to light (white) because the status bar background is dark (PrimaryBlue)
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = false // false means light icons on dark background
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming Typography is defined elsewhere in your theme package
        content = content
    )
}