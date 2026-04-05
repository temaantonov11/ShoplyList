package com.example.shoplylist.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Orange500,
    onPrimary = Black900,
    secondary = Orange700,
    onSecondary = White,
    background = Black900,
    onBackground = White,
    surface = Black900,
    onSurface = White,
    onError = White
)


private val LightColorScheme = lightColorScheme(
    primary = OrangeLight500,
    onPrimary = White900,
    secondary = OrangeLight700,
    onSecondary = Black,
    background = White900,
    onBackground = Black,
    surface = White900,
    onSurface = Black,
    onError = White
)


@Composable
fun ShoplyListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}