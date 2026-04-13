package com.example.petsync1.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define primary colors
val PrimaryColor = Color(0xFF05BF93) // Green theme color
val SecondaryColor = Color(0xFF4CAF50) // Lighter green
val BackgroundColor = Color(0xFFFFFFFF) // White background
val TextColor = Color.Black // Default text color
val ErrorColor = Color.Red

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    primaryContainer = Color(0xFFD7F5EE), // Very light green for containers
    onPrimaryContainer = Color(0xFF00382D),
    secondaryContainer = Color(0xFFC8E6C9), // More visible light green for the "Green Box"
    onSecondaryContainer = Color(0xFF003300),
    background = BackgroundColor,
    surface = BackgroundColor,
    surfaceVariant = Color(0xFFE8F5E9),
    onSurfaceVariant = Color(0xFF1B5E20),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextColor,
    onSurface = TextColor,
    error = ErrorColor,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    secondary = Color(0xFF4CAF50),
    primaryContainer = Color(0xFF005142), // Dark green for containers
    onPrimaryContainer = Color(0xFFD7F5EE),
    secondaryContainer = Color(0xFF2E7D32), // Darker green for dark mode dashboard
    onSecondaryContainer = Color(0xFFE8F5E9),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF1B5E20),
    onSurfaceVariant = Color(0xFFC8E6C9),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun PetSync1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
