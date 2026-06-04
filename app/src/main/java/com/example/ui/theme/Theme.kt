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
    primary = ProfessionalPrimaryDark,
    onPrimary = ProfessionalOnPrimaryDark,
    primaryContainer = ProfessionalPrimaryContainerDark,
    onPrimaryContainer = ProfessionalOnPrimaryContainerDark,
    secondary = ProfessionalSecondaryDark,
    onSecondary = ProfessionalOnSecondaryDark,
    secondaryContainer = ProfessionalSecondaryContainerDark,
    onSecondaryContainer = ProfessionalOnSecondaryContainerDark,
    background = ProfessionalBackgroundDark,
    onBackground = ProfessionalOnBackgroundDark,
    surface = ProfessionalSurfaceDark,
    onSurface = ProfessionalOnSurfaceDark,
    outline = ProfessionalOutlineDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ProfessionalPrimary,
    onPrimary = ProfessionalOnPrimary,
    primaryContainer = ProfessionalPrimaryContainer,
    onPrimaryContainer = ProfessionalOnPrimaryContainer,
    secondary = ProfessionalSecondary,
    onSecondary = ProfessionalOnSecondary,
    secondaryContainer = ProfessionalSecondaryContainer,
    onSecondaryContainer = ProfessionalOnSecondaryContainer,
    background = ProfessionalBackground,
    onBackground = ProfessionalOnBackground,
    surface = ProfessionalSurface,
    onSurface = ProfessionalOnSurface,
    outline = ProfessionalOutline
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set to false by default to ensure custom Professional theme is applied
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
