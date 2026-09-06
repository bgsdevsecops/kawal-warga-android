package id.myindo.platform.kawalwarga.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
 * Custom Material 3 Typography system defined in the Theme file using Serif fonts
 * for an authoritative, formal, and distinguished civic governance aesthetic.
 */
val CivicSerifTypography = Typography(
    // Display: Prominent metric numbers, annual budget summaries, citizen counters
    displayLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 52.sp,
        lineHeight = 60.sp,
        letterSpacing = (-0.5).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.3).sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.2).sp
    ),

    // Headline: Section headers, official letter banners, dialog titles
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.4).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.15).sp
    ),

    // Title: Card titles, citizen names, administrative form section headers
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.1).sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.5.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.05.sp
    ),

    // Body: Readable instructions, letter contents, bylaws, and descriptions
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 23.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.25.sp
    ),

    // Label: Buttons, navigation tabs, status badges, timestamps, form hints
    labelLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.5.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.15.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.4.sp
    )
)

/**
 * Primary Material 3 Theme for 'Kawal Warga'.
 * Configures the civic color schemes, typography hierarchy, and geometric shapes.
 */
@Composable
fun KawalWargaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve civic brand colors by default
    typography: Typography = CivicSerifTypography,
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
            typography = typography,
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

