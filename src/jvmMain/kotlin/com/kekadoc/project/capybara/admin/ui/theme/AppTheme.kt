package com.kekadoc.project.capybara.admin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColors = darkColors(
    primary = AppThemeColors.PrimaryDark,
    secondary = AppThemeColors.SecondaryDark,
)

private val LightColors = lightColors(
    primary = AppThemeColors.PrimaryLight,
    secondary = AppThemeColors.SecondaryLight,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when {
        darkTheme -> DarkColors
        else -> LightColors
    }
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = (view.context as Activity).window
//            window.statusBarColor = colorScheme.primary.toArgb()
//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
//        }
//    }

    MaterialTheme(
        colors = LightColors,
        typography = Typography,
        content = content
    )
}