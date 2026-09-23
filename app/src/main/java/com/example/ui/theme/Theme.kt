package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = VeilDarkOnSurface,
    onPrimary = VeilDarkBackground,
    primaryContainer = VeilDarkSurfaceContainerHigh,
    onPrimaryContainer = VeilDarkOnSurface,
    secondary = VeilSecondary,
    onSecondary = VeilOnSecondary,
    background = VeilDarkBackground,
    surface = VeilDarkSurface,
    onSurface = VeilDarkOnSurface,
    surfaceVariant = VeilDarkSurfaceContainer,
    outline = VeilOutline,
    outlineVariant = VeilDarkGlassBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = VeilPrimary,
    onPrimary = VeilOnPrimary,
    primaryContainer = VeilPrimaryContainer,
    onPrimaryContainer = VeilOnPrimaryContainer,
    secondary = VeilSecondary,
    onSecondary = VeilOnSecondary,
    secondaryContainer = VeilSecondaryContainer,
    onSecondaryContainer = VeilOnSecondaryContainer,
    background = VeilSurface,
    surface = VeilSurface,
    onSurface = VeilOnSurface,
    surfaceVariant = VeilSurfaceContainerHighest,
    outline = VeilOutline,
    outlineVariant = VeilOutlineVariant
  )

@Composable
fun VeilTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // For Veil, we use our custom crystalline glass aesthetic as the primary brand palette
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

// Backwards compatibility alias for template references
@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  VeilTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
