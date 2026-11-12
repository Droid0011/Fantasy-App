package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val FantasyDarkColorScheme = darkColorScheme(
    primary = GildedGold,
    onPrimary = DeepBronzeInk,
    primaryContainer = ObsidianElevated,
    onPrimaryContainer = GildedGoldBright,
    secondary = MoonlightSilver,
    onSecondary = ObsidianNight,
    secondaryContainer = ObsidianCard,
    onSecondaryContainer = MoonlightSilver,
    tertiary = ArcaneAmethyst,
    onTertiary = ObsidianNight,
    background = ObsidianNight,
    onBackground = ParchmentWhite,
    surface = ObsidianSurface,
    onSurface = ParchmentWhite,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = MutedStone,
    outline = BronzeBorder,
    outlineVariant = ObsidianElevated,
)

private val FantasyLightColorScheme = lightColorScheme(
    primary = AntiqueBronze,
    onPrimary = ParchmentSurface,
    primaryContainer = ParchmentCard,
    onPrimaryContainer = CharcoalInk,
    secondary = SlateInk,
    onSecondary = ParchmentSurface,
    secondaryContainer = ParchmentCard,
    onSecondaryContainer = CharcoalInk,
    tertiary = ArcaneAmethyst,
    onTertiary = ParchmentSurface,
    background = ParchmentCream,
    onBackground = CharcoalInk,
    surface = ParchmentSurface,
    onSurface = CharcoalInk,
    surfaceVariant = ParchmentCard,
    onSurfaceVariant = SlateInk,
    outline = WarmSandBorder,
    outlineVariant = ParchmentCard,
)

private val FantasyShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun FantasyCreatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) FantasyDarkColorScheme else FantasyLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FantasyTypography,
        shapes = FantasyShapes,
        content = content,
    )
}
