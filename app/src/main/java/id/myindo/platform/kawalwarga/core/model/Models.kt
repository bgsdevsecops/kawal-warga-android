package id.myindo.platform.kawalwarga.core.model

/**
 * Roles supported by Kawal Warga single APK multi-role architecture.
 * Determined and authorized exclusively by backend.
 */
enum class Role(val displayName: String) {
    WARGA("Warga"),
    KETUA_RT("Ketua RT"),
    KETUA_RW("Ketua RW"),
    SEKRETARIS("Sekretaris"),
    BENDAHARA("Bendahara"),
    PETUGAS_KEAMANAN("Petugas Keamanan")
}

data class UserContext(
    val contextId: String,
    val role: Role,
    val scopeType: String, // "RT" or "RW"
    val rtPublicId: String? = null,
    val rwPublicId: String? = null,
    val rtNumber: String,
    val rwNumber: String,
    val label: String,
    val isDefault: Boolean = false
)

data class CommunityInfo(
    val communityId: String,
    val name: String,
    val subdistrict: String, // Kelurahan
    val district: String,    // Kecamatan
    val city: String,
    val rt: String,
    val rw: String
)

data class UserProfile(
    val userId: String,
    val username: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val avatarUrl: String? = null
)

data class UserBootstrap(
    val user: UserProfile,
    val defaultContext: UserContext,
    val availableContexts: List<UserContext>,
    val permissions: List<String>,
    val features: Map<String, Boolean>,
    val community: CommunityInfo
)

/**
 * Domain entity for residents. NIK and phone are masked by default for privacy compliance.
 */
data class Resident(
    val publicId: String,
    val personId: String,
    val nikMasked: String,
    val fullName: String,
    val gender: String,
    val birthPlace: String,
    val birthDate: String,
    val religion: String,
    val relation: String,
    val address: String,
    val rt: String,
    val rw: String,
    val phoneMasked: String,
    val occupation: String,
    val residencyStatus: String,
    val isHeadOfFamily: Boolean = false,
    val householdKkMasked: String = "",
    val notes: String = ""
)

data class Household(
    val publicId: String,
    val kkNumberMasked: String,
    val headName: String,
    val address: String,
    val rt: String,
    val rw: String,
    val members: List<Resident>
)

data class LetterType(
    val id: String,
    val code: String,
    val name: String,
    val description: String,
    val requiredAttachments: List<String> = emptyList()
)

enum class LetterStatus(val label: String) {
    DRAFT("Draf"),
    SUBMITTED("Diajukan"),
    PROCESSING("Diproses"),
    APPROVED("Disetujui"),
    REJECTED("Ditolak"),
    CANCELED("Dibatalkan")
}

data class LetterItem(
    val publicId: String,
    val letterNumber: String,
    val typeCode: String,
    val typeName: String,
    val applicantName: String,
    val applicantNikMasked: String,
    val applicantAddress: String,
    val rt: String,
    val rw: String,
    val purpose: String,
    val notes: String = "",
    val status: LetterStatus,
    val rejectionReason: String? = null,
    val requestDate: Long,
    val processedDate: Long? = null,
    val approvedDate: Long? = null,
    val approverName: String? = null,
    val documentUrl: String? = null,
    val syncStatus: String = "SYNCED"
)

enum class ReportStatus(val label: String) {
    OPEN("Menunggu Respon"),
    ACKNOWLEDGED("Diterima"),
    IN_PROGRESS("Sedang Ditangani"),
    RESOLVED("Selesai"),
    CLOSED("Ditutup"),
    REJECTED("Ditolak")
}

data class IncidentReportItem(
    val publicId: String,
    val reporterName: String,
    val reporterPhoneMasked: String,
    val category: String,
    val urgency: String,
    val location: String,
    val description: String,
    val photoUrl: String? = null,
    val status: ReportStatus,
    val internalNotes: String? = null,
    val timestamp: Long,
    val resolvedTimestamp: Long? = null,
    val isPanicAlert: Boolean = false,
    val syncStatus: String = "SYNCED"
)

enum class SosStatus(val label: String) {
    ACTIVE("Aktif - Darurat"),
    ACKNOWLEDGED("Diterima Petugas"),
    RESPONDING("Petugas Menuju Lokasi"),
    RESOLVED("Selesai Ditangani"),
    CANCELED("Dibatalkan / Alarm Palsu")
}

data class SosAlertItem(
    val publicId: String,
    val reporterName: String,
    val reporterPhone: String,
    val emergencyType: String,
    val location: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val status: SosStatus,
    val responderName: String? = null,
    val timestamp: Long,
    val receiptConfirmed: Boolean = true
)

data class RondaShift(
    val publicId: String,
    val dayName: String,
    val dateFormatted: String,
    val rt: String,
    val location: String,
    val timeRange: String,
    val officers: List<String>,
    val coordinatorName: String,
    val coordinatorPhone: String,
    val isCheckedIn: Boolean = false,
    val checkInTime: Long? = null
)

enum class DuesStatus(val label: String) {
    UNPAID("Belum Bayar"),
    PENDING_VERIFICATION("Menunggu Verifikasi"),
    PAID("Lunas"),
    OVERDUE("Jatuh Tempo"),
    CANCELED("Dibatalkan")
}

data class DuesBillItem(
    val publicId: String,
    val invoiceNumber: String,
    val residentName: String,
    val address: String,
    val rt: String,
    val month: String,
    val year: Int,
    val breakdown: Map<String, Double>,
    val totalAmount: Double,
    val status: DuesStatus,
    val dueDate: String,
    val paymentMethod: String? = null,
    val paymentProofUrl: String? = null,
    val paymentDate: Long? = null,
    val verifiedBy: String? = null,
    val syncStatus: String = "SYNCED"
)

data class AnnouncementItem(
    val publicId: String,
    val title: String,
    val content: String,
    val category: String,
    val priority: String, // "Penting" or "Info"
    val publishedDate: Long,
    val authorName: String,
    val scopeType: String = "RT",
    val isPinned: Boolean = false
)
