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

// --- Primary Civic Identity (Deep Civic Teal) ---
val CivicTealPrimary = Color(0xFF00685E)
val CivicTealOnPrimary = Color(0xFFFFFFFF)
val CivicTealPrimaryContainer = Color(0xFFE2F7F4)
val CivicTealOnPrimaryContainer = Color(0xFF00201D)
val CivicTealInversePrimary = Color(0xFF80D5C7)

// --- Secondary Identity (Community Emerald / Gotong Royong & Siskamling) ---
val CivicEmeraldSecondary = Color(0xFF1B6D45)
val CivicEmeraldOnSecondary = Color(0xFFFFFFFF)
val CivicEmeraldSecondaryContainer = Color(0xFFD6F5E3)
val CivicEmeraldOnSecondaryContainer = Color(0xFF002111)

// --- Tertiary Identity (Administrative Seal & Official Notice Amber/Gold) ---
val CivicAmberTertiary = Color(0xFFB45309)
val CivicAmberOnTertiary = Color(0xFFFFFFFF)
val CivicAmberTertiaryContainer = Color(0xFFFEF3C7)
val CivicAmberOnTertiaryContainer = Color(0xFF451A03)

// --- Light Theme Neutral & Slate Surfaces ---
val CivicSlateBackground = Color(0xFFF8FAFC) // Slate 50
val CivicSlateSurface = Color(0xFFFFFFFF)
val CivicSlateSurfaceVariant = Color(0xFFF1F5F9) // Slate 100
val CivicSlateSurfaceContainerLowest = Color(0xFFFFFFFF)
val CivicSlateSurfaceContainerLow = Color(0xFFF8FAFC)
val CivicSlateSurfaceContainer = Color(0xFFF1F5F9)
val CivicSlateSurfaceContainerHigh = Color(0xFFE2E8F0) // Slate 200
val CivicSlateSurfaceContainerHighest = Color(0xFFCBD5E1) // Slate 300
val CivicSlateBorder = Color(0xFFE2E8F0)
val CivicSlateOutlineVariant = Color(0xFFF1F5F9)

val CivicTextPrimary = Color(0xFF0F172A) // Slate 900 - Crisp high contrast
val CivicTextSecondary = Color(0xFF475569) // Slate 600 - Secondary metadata
val CivicTextMuted = Color(0xFF64748B) // Slate 500 - Timestamp & hints

// --- Error & Emergency Identity (SOS & Siskamling Alerts) ---
val CivicError = Color(0xFFBA1A1A)
val CivicOnError = Color(0xFFFFFFFF)
val CivicErrorContainer = Color(0xFFFFDAD6)
val CivicOnErrorContainer = Color(0xFF410002)
val CivicEmergency = Color(0xFFDC2626)
val CivicEmergencyBg = Color(0xFFFEF2F2)

// --- Dark Theme Palette (Institutional Midnight Slate) ---
val CivicDarkPrimary = Color(0xFF80D5C7)
val CivicDarkOnPrimary = Color(0xFF003731)
val CivicDarkPrimaryContainer = Color(0xFF004F47)
val CivicDarkOnPrimaryContainer = Color(0xFF9CF2E3)

val CivicDarkSecondary = Color(0xFF8BCE9D)
val CivicDarkOnSecondary = Color(0xFF00391C)
val CivicDarkSecondaryContainer = Color(0xFF06522E)
val CivicDarkOnSecondaryContainer = Color(0xFFD6F5E3)

val CivicDarkTertiary = Color(0xFFFFB77C)
val CivicDarkOnTertiary = Color(0xFF552500)
val CivicDarkTertiaryContainer = Color(0xFF783700)
val CivicDarkOnTertiaryContainer = Color(0xFFFFDCC4)

val CivicDarkBackground = Color(0xFF0B131E) // Deep midnight slate
val CivicDarkSurface = Color(0xFF131D2B)
val CivicDarkSurfaceVariant = Color(0xFF1E293B)
val CivicDarkSurfaceContainerLowest = Color(0xFF070D16)
val CivicDarkSurfaceContainerLow = Color(0xFF0F1724)
val CivicDarkSurfaceContainer = Color(0xFF131D2B)
val CivicDarkSurfaceContainerHigh = Color(0xFF1E2A3A)
val CivicDarkSurfaceContainerHighest = Color(0xFF283649)
val CivicDarkBorder = Color(0xFF334155)
val CivicDarkTextPrimary = Color(0xFFF1F5F9)
val CivicDarkTextSecondary = Color(0xFF94A3B8)
val CivicDarkTextMuted = Color(0xFF64748B)

// --- Civic Administration Status Tokens ---
val StatusApprovedBg = Color(0xFFDCFCE7)
val StatusApprovedText = Color(0xFF14532D)
val StatusPendingBg = Color(0xFFFEF3C7)
val StatusPendingText = Color(0xFF92400E)
val StatusProgressBg = Color(0xFFE0F2FE)
val StatusProgressText = Color(0xFF0369A1)
val StatusRejectedBg = Color(0xFFFEE2E2)
val StatusRejectedText = Color(0xFF991B1B)
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

