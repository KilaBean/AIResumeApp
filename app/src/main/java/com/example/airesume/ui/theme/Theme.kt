package com.example.airesume.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Gray10,
    secondary = PurpleGrey80,
    onSecondary = Gray10,
    tertiary = Pink80,
    onTertiary = Gray10,

    background = Gray10,
    onBackground = Gray90,
    surface = Gray20,
    onSurface = Gray90,

    // Optional: Add more Material 3 specific colors for a complete theme
    // error = Color(0xFFCF6679),
    // onError = Color(0xFFFFFFFF),
    // surfaceVariant = Gray30,
    // onSurfaceVariant = Gray70,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    onPrimary = Gray99,
    secondary = PurpleGrey40,
    onSecondary = Gray99,
    tertiary = Pink40,
    onTertiary = Gray99,

    background = Gray99,
    onBackground = Gray10,
    surface = Gray95,
    onSurface = Gray10,

    // Optional: Add more Material 3 specific colors for a complete theme
    // error = Color(0xFFB00020),
    // onError = Color(0xFFFFFFFF),
    // surfaceVariant = Gray80,
    // onSurfaceVariant = Gray40,
)

@Composable
fun AIResumeTheme(
    // Keep this to respect the device's system-wide light/dark mode toggle
    darkTheme: Boolean = isSystemInDarkTheme(),
    // CHANGED: Set dynamicColor to false to explicitly prevent using Material You system colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // This block will now always be skipped because dynamicColor is false
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // If darkTheme (from isSystemInDarkTheme()) is true, use our custom DarkColorScheme
        darkTheme -> DarkColorScheme
        // Otherwise (darkTheme is false), use our custom LightColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            // Adjusts status bar icons based on the brightness of the status bar color
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming Typography is defined elsewhere in your theme package
        // shapes = Shapes, // Uncomment this line if you have a custom 'Shapes' object defined
        content = content
    )
}