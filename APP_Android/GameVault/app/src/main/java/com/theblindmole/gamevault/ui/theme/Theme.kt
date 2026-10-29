package com.theblindmole.gamevault.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.theblindmole.gamevault.domain.model.ThemeMode

private val DarkColors = darkColorScheme(
    primary = SkyBlue,
    onPrimary = Ink,
    primaryContainer = Steel,
    onPrimaryContainer = Mist,
    inversePrimary = Steel,
    secondary = Mist,
    onSecondary = Ink,
    secondaryContainer = Steel,
    onSecondaryContainer = Mist,
    tertiary = Mist,
    onTertiary = Ink,
    tertiaryContainer = Steel,
    onTertiaryContainer = Mist,
    background = Ink,
    onBackground = Mist,
    surface = Ink,
    onSurface = Mist,
    surfaceVariant = Navy,
    onSurfaceVariant = MistMuted,
    surfaceTint = SkyBlue,
    inverseSurface = Mist,
    inverseOnSurface = Ink,
    error = ErrorRed,
    onError = Ink,
    errorContainer = ErrorContainerDark,
    onErrorContainer = Mist,
    outline = SteelMist,
    outlineVariant = Steel,
    scrim = Ink,
    surfaceBright = Steel,
    surfaceDim = Ink,
    surfaceContainerLowest = Ink,
    surfaceContainerLow = InkNavy,
    surfaceContainer = Navy,
    surfaceContainerHigh = NavySteel,
    surfaceContainerHighest = Steel,
)

private val LightColors = lightColorScheme(
    primary = Steel,
    onPrimary = MistLightest,
    primaryContainer = SkyBlue,
    onPrimaryContainer = Ink,
    inversePrimary = SkyBlue,
    secondary = Navy,
    onSecondary = MistLightest,
    secondaryContainer = Mist,
    onSecondaryContainer = Ink,
    tertiary = Navy,
    onTertiary = MistLightest,
    tertiaryContainer = Mist,
    onTertiaryContainer = Ink,
    background = MistSoft,
    onBackground = Ink,
    surface = MistSoft,
    onSurface = Ink,
    surfaceVariant = Mist,
    onSurfaceVariant = Steel,
    surfaceTint = Steel,
    inverseSurface = Navy,
    inverseOnSurface = Mist,
    error = ErrorRedDeep,
    onError = MistLightest,
    errorContainer = ErrorContainerLight,
    onErrorContainer = Ink,
    outline = Steel,
    outlineVariant = Mist,
    scrim = Ink,
    surfaceBright = MistLightest,
    surfaceDim = Mist,
    surfaceContainerLowest = MistLightest,
    surfaceContainerLow = MistLighter,
    surfaceContainer = MistPale,
    surfaceContainerHigh = MistLight,
    surfaceContainerHighest = Mist,
)

@Composable
fun ThemeMode.isDarkTheme(): Boolean = when (this) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

@Composable
fun GameVaultTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
