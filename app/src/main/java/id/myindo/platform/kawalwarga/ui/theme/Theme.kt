package id.myindo.platform.kawalwarga.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext

/**
 * Material 3 Light Color Scheme for 'Kawal Warga'
 * Structured, high-contrast, professional civic administrative aesthetic.
 */
val KawalWargaLightColorScheme = lightColorScheme(
    primary = CivicTealPrimary,
    onPrimary = CivicTealOnPrimary,
    primaryContainer = CivicTealPrimaryContainer,
    onPrimaryContainer = CivicTealOnPrimaryContainer,
    inversePrimary = CivicTealInversePrimary,

    secondary = CivicEmeraldSecondary,
    onSecondary = CivicEmeraldOnSecondary,
    secondaryContainer = CivicEmeraldSecondaryContainer,
    onSecondaryContainer = CivicEmeraldOnSecondaryContainer,

    tertiary = CivicAmberTertiary,
    onTertiary = CivicAmberOnTertiary,
    tertiaryContainer = CivicAmberTertiaryContainer,
    onTertiaryContainer = CivicAmberOnTertiaryContainer,

    background = CivicSlateBackground,
    onBackground = CivicTextPrimary,
    surface = CivicSlateSurface,
    onSurface = CivicTextPrimary,
    surfaceVariant = CivicSlateSurfaceVariant,
    onSurfaceVariant = CivicTextSecondary,

    surfaceContainerLowest = CivicSlateSurfaceContainerLowest,
    surfaceContainerLow = CivicSlateSurfaceContainerLow,
    surfaceContainer = CivicSlateSurfaceContainer,
    surfaceContainerHigh = CivicSlateSurfaceContainerHigh,
    surfaceContainerHighest = CivicSlateSurfaceContainerHighest,

    outline = CivicSlateBorder,
    outlineVariant = CivicSlateOutlineVariant,

    error = CivicError,
    onError = CivicOnError,
    errorContainer = CivicErrorContainer,
    onErrorContainer = CivicOnErrorContainer
)

/**
 * Material 3 Dark Color Scheme for 'Kawal Warga'
 * Midnight slate theme designed for nighttime siskamling surveillance and low-light battery efficiency.
 */
val KawalWargaDarkColorScheme = darkColorScheme(
    primary = CivicDarkPrimary,
    onPrimary = CivicDarkOnPrimary,
    primaryContainer = CivicDarkPrimaryContainer,
    onPrimaryContainer = CivicDarkOnPrimaryContainer,
    inversePrimary = CivicTealPrimary,

    secondary = CivicDarkSecondary,
    onSecondary = CivicDarkOnSecondary,
    secondaryContainer = CivicDarkSecondaryContainer,
    onSecondaryContainer = CivicDarkOnSecondaryContainer,

    tertiary = CivicDarkTertiary,
    onTertiary = CivicDarkOnTertiary,
    tertiaryContainer = CivicDarkTertiaryContainer,
    onTertiaryContainer = CivicDarkOnTertiaryContainer,

    background = CivicDarkBackground,
    onBackground = CivicDarkTextPrimary,
    surface = CivicDarkSurface,
    onSurface = CivicDarkTextPrimary,
    surfaceVariant = CivicDarkSurfaceVariant,
    onSurfaceVariant = CivicDarkTextSecondary,

    surfaceContainerLowest = CivicDarkSurfaceContainerLowest,
    surfaceContainerLow = CivicDarkSurfaceContainerLow,
    surfaceContainer = CivicDarkSurfaceContainer,
    surfaceContainerHigh = CivicDarkSurfaceContainerHigh,
    surfaceContainerHighest = CivicDarkSurfaceContainerHighest,

    outline = CivicDarkBorder,
    outlineVariant = CivicDarkSurfaceVariant,

    error = CivicError,
    onError = CivicOnError,
    errorContainer = CivicErrorContainer,
    onErrorContainer = CivicOnErrorContainer
)

/**
 * Accessor for extended civic administration semantic tokens.
 */
val MaterialTheme.civicColors: CivicExtendedColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCivicExtendedColors.current

/**
 * Primary Material 3 Theme for 'Kawal Warga'.
 * Configures the civic color schemes, typography hierarchy, and geometric shapes.
 */
@Composable
fun KawalWargaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve civic brand colors by default
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> KawalWargaDarkColorScheme
        else -> KawalWargaLightColorScheme
    }

    val extendedColors = CivicExtendedColors()

    CompositionLocalProvider(
        LocalCivicExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = KawalWargaTypography,
            shapes = KawalWargaShapes,
            content = content
        )
    }
}

/**
 * Backward compatibility alias for existing usages and tests.
 */
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    KawalWargaTheme(
        darkTheme = darkTheme,
        dynamicColor = dynamicColor,
        content = content
    )
}

