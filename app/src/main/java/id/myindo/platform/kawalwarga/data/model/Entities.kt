package id.myindo.platform.kawalwarga.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citizens")
data class Citizen(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nik: String,
    val noKk: String,
    val fullName: String,
    val gender: String, // "Laki-laki" | "Perempuan"
    val birthPlace: String,
    val birthDate: String, // "DD/MM/YYYY"
    val religion: String, // "Islam", "Kristen", "Katolik", "Hindu", "Buddha", "Konghucu"
    val familyRole: String, // "Kepala Keluarga", "Istri", "Anak", "Orang Tua", "Famili Lain"
    val address: String, // "Jl. Mawar No. 12"
    val rt: String = "02",
    val rw: String = "05",
    val phone: String,
    val occupation: String,
    val residenceStatus: String, // "Warga Tetap", "Kontrak", "Kos"
    val emergencyContact: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "letter_requests")
data class LetterRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val letterNumber: String = "DRAF",
    val citizenId: Long,
    val citizenName: String,
    val citizenNik: String,
    val citizenAddress: String,
    val rt: String = "02",
    val rw: String = "05",
    val letterType: String,
    val purpose: String,
    val notes: String = "",
    val status: String = "Diajukan", // "Diajukan", "Diproses", "Disetujui", "Ditolak"
    val rejectionReason: String? = null,
    val requestDate: Long = System.currentTimeMillis(),
    val processedDate: Long? = null,
    val approvedDate: Long? = null,
    val approverName: String = "Bpk. H. Sutrisno (Ketua RT 02)"
)

@Entity(tableName = "security_reports")
data class SecurityReport(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val reporterName: String,
    val reporterPhone: String,
    val category: String, // "Keamanan / Kriminal", "Penerangan / Lampu Padam", "Ketertiban Lingkungan", "Sampah / Kebersihan", "Kerusakan Sarana Fasum", "Bencana / Darurat"
    val urgency: String, // "Darurat", "Penting", "Normal"
    val location: String,
    val description: String,
    val status: String = "Menunggu Respon", // "Menunggu Respon", "Sedang Ditangani", "Selesai"
    val responseNote: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val resolvedTimestamp: Long? = null,
    val isPanicAlert: Boolean = false
)

@Entity(tableName = "dues_payments")
data class DuesPayment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val invoiceNumber: String,
    val citizenId: Long,
    val citizenName: String,
    val houseNumber: String,
    val rt: String = "02",
    val month: String, // "Januari", "Februari", etc.
    val year: Int = 2026,
    val amountKebersihan: Double = 35000.0,
    val amountKeamanan: Double = 50000.0,
    val amountKasRt: Double = 25000.0,
    val amountSosial: Double = 15000.0,
    val totalAmount: Double = 125000.0,
    val paymentStatus: String = "Belum Bayar", // "Belum Bayar", "Lunas"
    val paymentMethod: String = "QRIS", // "QRIS", "Transfer BCA", "Transfer Mandiri", "Transfer BRI", "Tunai"
    val paymentDate: Long? = null,
    val collectorName: String = "Ibu Hj. Aminah (Bendahara RT)"
)

@Entity(tableName = "announcements")
data class Announcement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val category: String, // "Kerja Bakti", "Posyandu", "Rapat RT", "Keamanan", "Info Umum"
    val date: Long = System.currentTimeMillis(),
    val priority: String = "Info" // "Penting", "Info"
)

data class RondaSchedule(
    val day: String,
    val rt: String,
    val location: String,
    val timeRange: String,
    val patrolOfficers: List<String>,
    val coordinator: String
)
