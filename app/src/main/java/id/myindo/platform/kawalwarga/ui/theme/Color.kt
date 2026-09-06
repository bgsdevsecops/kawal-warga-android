package id.myindo.platform.kawalwarga.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Material 3 Civic Color Palette for 'Kawal Warga'
 *
 * Inspired by official public administrative portals, modern smart city dashboards,
 * and Indonesian civic community services (RT/RW / Kelurahan).
 * Colors emphasize public trust, community solidarity (Gotong Royong), crisp clarity,
 * and high legibility across all demographic age groups.
 */

// --- Primary Civic Identity (Sovereign Deep Emerald Teal) ---
val CivicTealPrimary = Color(0xFF0D5447)
val CivicTealOnPrimary = Color(0xFFFFFFFF)
val CivicTealPrimaryContainer = Color(0xFFE4F3EF)
val CivicTealOnPrimaryContainer = Color(0xFF042B24)
val CivicTealInversePrimary = Color(0xFF6ED8C2)

// --- Secondary Identity (Rich Jade / Gotong Royong & Siskamling) ---
val CivicEmeraldSecondary = Color(0xFF136449)
val CivicEmeraldOnSecondary = Color(0xFFFFFFFF)
val CivicEmeraldSecondaryContainer = Color(0xFFDCF3E7)
val CivicEmeraldOnSecondaryContainer = Color(0xFF042D1E)

// --- Tertiary Identity (Refined Antique Gold / Heritage Bronze) ---
val CivicAmberTertiary = Color(0xFF9A6711)
val CivicAmberOnTertiary = Color(0xFFFFFFFF)
val CivicAmberTertiaryContainer = Color(0xFFFBF1D9)
val CivicAmberOnTertiaryContainer = Color(0xFF382302)

// --- Light Theme Neutral & Cashmere Slate Surfaces ---
val CivicSlateBackground = Color(0xFFF7FAF8) // Refined cashmere/silk tint
val CivicSlateSurface = Color(0xFFFFFFFF)
val CivicSlateSurfaceVariant = Color(0xFFEEF4F1) // Soft sage tint
val CivicSlateSurfaceContainerLowest = Color(0xFFFFFFFF)
val CivicSlateSurfaceContainerLow = Color(0xFFF7FAF8)
val CivicSlateSurfaceContainer = Color(0xFFEEF4F1)
val CivicSlateSurfaceContainerHigh = Color(0xFFE2ECE7)
val CivicSlateSurfaceContainerHighest = Color(0xFFD6E3DD)
val CivicSlateBorder = Color(0xFFDFE7E3) // Hairline crisp border
val CivicSlateOutlineVariant = Color(0xFFEBF1EE)

val CivicTextPrimary = Color(0xFF111917) // Charcoal Black - High contrast luxury
val CivicTextSecondary = Color(0xFF455651) // Deep Muted Slate
val CivicTextMuted = Color(0xFF687C76) // Subtle caption & timestamp

// --- Error & Emergency Identity (Dignified Ruby & Crimson) ---
val CivicError = Color(0xFFB91C1C)
val CivicOnError = Color(0xFFFFFFFF)
val CivicErrorContainer = Color(0xFFFEE2E2)
val CivicOnErrorContainer = Color(0xFF450A0A)
val CivicEmergency = Color(0xFFDC2626)
val CivicEmergencyBg = Color(0xFFFEF2F2)

// --- Dark Theme Palette (Obsidian Forest & Luminous Emerald) ---
val CivicDarkPrimary = Color(0xFF6FD2BE)
val CivicDarkOnPrimary = Color(0xFF04382F)
val CivicDarkPrimaryContainer = Color(0xFF084B3F)
val CivicDarkOnPrimaryContainer = Color(0xFF8CF0DB)

val CivicDarkSecondary = Color(0xFF7DD5A7)
val CivicDarkOnSecondary = Color(0xFF043820)
val CivicDarkSecondaryContainer = Color(0xFF0A4E2F)
val CivicDarkOnSecondaryContainer = Color(0xFF9DF0C4)

val CivicDarkTertiary = Color(0xFFF3C77D)
val CivicDarkOnTertiary = Color(0xFF482D00)
val CivicDarkTertiaryContainer = Color(0xFF674200)
val CivicDarkOnTertiaryContainer = Color(0xFFFFDF9E)

val CivicDarkBackground = Color(0xFF0D1412) // Obsidian Forest
val CivicDarkSurface = Color(0xFF141F1C)
val CivicDarkSurfaceVariant = Color(0xFF1B2A26)
val CivicDarkSurfaceContainerLowest = Color(0xFF080D0B)
val CivicDarkSurfaceContainerLow = Color(0xFF101917)
val CivicDarkSurfaceContainer = Color(0xFF141F1C)
val CivicDarkSurfaceContainerHigh = Color(0xFF1C2B27)
val CivicDarkSurfaceContainerHighest = Color(0xFF243631)
val CivicDarkBorder = Color(0xFF283A35)
val CivicDarkTextPrimary = Color(0xFFF1F6F4)
val CivicDarkTextSecondary = Color(0xFF9FB6AF)
val CivicDarkTextMuted = Color(0xFF6E867F)

// --- Civic Administration Status Tokens (Refined Pastel + High-Contrast Ink) ---
val StatusApprovedBg = Color(0xFFE2F7EB)
val StatusApprovedText = Color(0xFF0E5A35)
val StatusPendingBg = Color(0xFFFEF3D6)
val StatusPendingText = Color(0xFF875200)
val StatusProgressBg = Color(0xFFE0F1FD)
val StatusProgressText = Color(0xFF035D96)
val StatusRejectedBg = Color(0xFFFDECEB)
val StatusRejectedText = Color(0xFF8C1D18)
val StatusEmergency = CivicEmergency

// --- Legacy & Aliased Tokens for Seamless Backward Compatibility ---
val TealPrimary = CivicTealPrimary
val TealOnPrimary = CivicTealOnPrimary
val TealPrimaryContainer = CivicTealPrimaryContainer
val TealOnPrimaryContainer = CivicTealOnPrimaryContainer

val EmeraldSecondary = CivicEmeraldSecondary
val EmeraldSecondaryContainer = CivicEmeraldSecondaryContainer
val EmeraldOnSecondaryContainer = CivicEmeraldOnSecondaryContainer

val AmberTertiary = CivicAmberTertiary
val AmberTertiaryContainer = CivicAmberTertiaryContainer
val AmberOnTertiaryContainer = CivicAmberOnTertiaryContainer

val SlateBackground = CivicSlateBackground
val SlateSurface = CivicSlateSurface
val SlateSurfaceVariant = CivicSlateSurfaceVariant
val SlateBorder = CivicSlateBorder
val SlateTextPrimary = CivicTextPrimary
val SlateTextSecondary = CivicTextSecondary
val SlateTextMuted = CivicTextMuted

val TealDarkPrimary = CivicDarkPrimary
val TealDarkOnPrimary = CivicDarkOnPrimary
val TealDarkPrimaryContainer = CivicDarkPrimaryContainer
val DarkBackground = CivicDarkBackground
val DarkSurface = CivicDarkSurface
val DarkSurfaceVariant = CivicDarkSurfaceVariant

/**
 * Extended Civic Color Palette for specialized administration components
 * such as status tags, verified stamps, and emergency banners.
 */
@Immutable
data class CivicExtendedColors(
    val statusApprovedBg: Color = StatusApprovedBg,
    val statusApprovedText: Color = StatusApprovedText,
    val statusPendingBg: Color = StatusPendingBg,
    val statusPendingText: Color = StatusPendingText,
    val statusProgressBg: Color = StatusProgressBg,
    val statusProgressText: Color = StatusProgressText,
    val statusRejectedBg: Color = StatusRejectedBg,
    val statusRejectedText: Color = StatusRejectedText,
    val emergency: Color = StatusEmergency,
    val emergencyBg: Color = CivicEmergencyBg,
    val borderSubtle: Color = CivicSlateBorder,
    val verifiedBadge: Color = CivicEmeraldSecondary
)

val LocalCivicExtendedColors = staticCompositionLocalOf { CivicExtendedColors() }

