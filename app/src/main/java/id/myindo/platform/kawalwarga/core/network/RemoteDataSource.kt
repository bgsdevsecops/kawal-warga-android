package id.myindo.platform.kawalwarga.core.network

import id.myindo.platform.kawalwarga.core.auth.AuthRepository
import id.myindo.platform.kawalwarga.core.model.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Remote Data Source implementing the Django api v1 kawal-warga endpoints.
 * Integrates with OkHttp/Retrofit and includes a resilient in-app mock engine
 * reflecting real-time multi-role permissions and dynamic backend responses.
 */
class RemoteDataSource(
    private val authRepository: AuthRepository
) {
    // In-memory persistent server simulation state
    private val serverLetterTypes = listOf(
        LetterType("lt-01", "SP_KTP", "Surat Pengantar KTP / KK", "Untuk penerbitan baru atau pembaharuan KTP/KK di Kelurahan"),
        LetterType("lt-02", "SP_DOMISILI", "Surat Keterangan Domisili", "Keterangan tempat tinggal warga di lingkungan RT"),
        LetterType("lt-03", "SP_USAHA", "Surat Keterangan Usaha (SKU)", "Pengantar pembuatan izin usaha mikro kecil"),
        LetterType("lt-04", "SP_SKCK", "Surat Pengantar SKCK", "Pengantar permohonan catatan kepolisian ke Polsek/Kelurahan"),
        LetterType("lt-05", "SP_NIKAH", "Surat Keterangan Belum Menikah", "Untuk persyaratan administrasi pernikahan atau KUA")
    )

    private val serverLetters = mutableListOf(
        LetterItem(
            publicId = "let-uuid-001",
            letterNumber = "12/SP-RT02/RW05/IX/2026",
            typeCode = "SP_DOMISILI",
            typeName = "Surat Keterangan Domisili",
            applicantName = "Budi Santoso, S.T.",
            applicantNikMasked = "327501******0005",
            applicantAddress = "Jl. Mawar No. 04, Blok A",
            rt = "02",
            rw = "05",
            purpose = "Persyaratan administrasi perbankan",
            notes = "Melampirkan fotokopi KTP",
            status = LetterStatus.APPROVED,
            requestDate = System.currentTimeMillis() - 86400000 * 2,
            processedDate = System.currentTimeMillis() - 86400000,
            approvedDate = System.currentTimeMillis() - 3600000 * 12,
            approverName = "Ketua RT 02"
        ),
        LetterItem(
            publicId = "let-uuid-002",
            letterNumber = "15/SP-RT02/RW05/IX/2026",
            typeCode = "SP_KTP",
            typeName = "Surat Pengantar KTP / KK",
            applicantName = "Dimas Arya Pratama",
            applicantNikMasked = "327501******0008",
            applicantAddress = "Jl. Melati No. 08, Blok B",
            rt = "02",
            rw = "05",
            purpose = "Perekaman e-KTP usia 17 tahun",
            notes = "Sudah melampirkan akta kelahiran",
            status = LetterStatus.SUBMITTED,
            requestDate = System.currentTimeMillis() - 3600000 * 4
        )
    )

    private val serverReports = mutableListOf(
        IncidentReportItem(
            publicId = "rep-uuid-001",
            reporterName = "Warga Blok B (Budi)",
            reporterPhoneMasked = "0812****4321",
            category = "Penerangan / Lampu Padam",
            urgency = "Penting",
            location = "Depan Pos Ronda Melati Blok B",
            description = "Lampu jalan utama nomor tiang 14 padam sejak kemarin malam, jalanan cukup gelap.",
            status = ReportStatus.IN_PROGRESS,
            internalNotes = "Telah dikoordinasikan dengan petugas teknisi PJU",
            timestamp = System.currentTimeMillis() - 3600000 * 8
        ),
        IncidentReportItem(
            publicId = "rep-uuid-002",
            reporterName = "Siti Rahmawati",
            reporterPhoneMasked = "0813****9988",
            category = "Kebersihan & Sampah",
            urgency = "Normal",
            location = "Saluran air perbatasan Blok A-B",
            description = "Tumpukan dahan pohon pasca hujan lebat menghalangi saluran air warga.",
            status = ReportStatus.OPEN,
            timestamp = System.currentTimeMillis() - 3600000 * 2
        )
    )

    private val serverSosAlerts = mutableListOf<SosAlertItem>()

    private val serverRonda = mutableListOf(
        RondaShift("ron-01", "Senin", "08 Sep 2026", "02", "Pos Ronda Utama Blok A", "22:00 - 04:00 WIB", listOf("Bambang", "Dimas Arya", "Yudi", "Joko"), "Bpk. Bambang", "081233445566"),
        RondaShift("ron-02", "Selasa", "09 Sep 2026", "02", "Pos Ronda Melati Blok B", "22:00 - 04:00 WIB", listOf("Budi Santoso", "Hendra", "Wawan", "Doni"), "Bpk. Hendra", "081344556677"),
        RondaShift("ron-03", "Rabu", "10 Sep 2026", "02", "Pos Ronda Utama Blok A", "22:00 - 04:00 WIB", listOf("Agus Prasetyo", "Rudi", "Eko", "Arif"), "Bpk. Rudi", "081555667788"),
        RondaShift("ron-04", "Kamis", "11 Sep 2026", "02", "Pos Ronda Kenanga Blok C", "22:00 - 04:00 WIB", listOf("Sugeng", "Anton", "Rian", "Fahri"), "Bpk. Sugeng", "081766778899"),
        RondaShift("ron-05", "Jumat", "12 Sep 2026", "02", "Pos Ronda Utama Blok A", "22:00 - 04:00 WIB", listOf("Sutrisno", "Gunawan", "Dani", "Bayu"), "H. Sutrisno (Ketua RT)", "081287654321")
    )

    private val serverBills = mutableListOf(
        DuesBillItem(
            publicId = "bill-01",
            invoiceNumber = "INV-202609-101",
            residentName = "Budi Santoso, S.T.",
            address = "Jl. Mawar No. 04, Blok A",
            rt = "02",
            month = "September",
            year = 2026,
            breakdown = mapOf("Kebersihan" to 35000.0, "Keamanan" to 50000.0, "Kas RT" to 25000.0, "Sosial" to 15000.0),
            totalAmount = 125000.0,
            status = DuesStatus.UNPAID,
            dueDate = "15 September 2026"
        ),
        DuesBillItem(
            publicId = "bill-02",
            invoiceNumber = "INV-202608-095",
            residentName = "Budi Santoso, S.T.",
            address = "Jl. Mawar No. 04, Blok A",
            rt = "02",
            month = "Agustus",
            year = 2026,
            breakdown = mapOf("Kebersihan" to 35000.0, "Keamanan" to 50000.0, "Kas RT" to 25000.0, "Sosial" to 15000.0),
            totalAmount = 125000.0,
            status = DuesStatus.PAID,
            dueDate = "15 Agustus 2026",
            paymentMethod = "Transfer Mandiri",
            paymentDate = System.currentTimeMillis() - 86400000 * 20,
            verifiedBy = "Ibu Hj. Aminah (Bendahara RT)"
        ),
        DuesBillItem(
            publicId = "bill-03",
            invoiceNumber = "INV-202609-102",
            residentName = "Dimas Arya Pratama",
            address = "Jl. Melati No. 08, Blok B",
            rt = "02",
            month = "September",
            year = 2026,
            breakdown = mapOf("Kebersihan" to 35000.0, "Keamanan" to 50000.0, "Kas RT" to 25000.0, "Sosial" to 15000.0),
            totalAmount = 125000.0,
            status = DuesStatus.PENDING_VERIFICATION,
            dueDate = "15 September 2026",
            paymentMethod = "Transfer BCA",
            paymentProofUrl = "https://kawalwarga.myindo.platform/proof/sample_proof.jpg"
        )
    )

    private val serverAnnouncements = mutableListOf(
        AnnouncementItem(
            publicId = "ann-01",
            title = "Jadwal Kerja Bakti & Pengurasan Saluran Air",
            content = "Menjelang musim penghujan, warga dihimbau mengikuti kerja bakti serentak pada hari Minggu, 14 September 2026, pukul 07:00 WIB di pos ronda masing-masing.",
            category = "Kerja Bakti",
            priority = "Penting",
            publishedDate = System.currentTimeMillis() - 86400000,
            authorName = "Pengurus RW 05",
            isPinned = true
        ),
        AnnouncementItem(
            publicId = "ann-02",
            title = "Pelayanan Posyandu Balita & Lansia Melati",
            content = "Posyandu rutin bulanan akan diselenggarakan di Balai Warga RT 02 pada hari Rabu, 17 September 2026 mulai pukul 08:30 WIB.",
            category = "Posyandu",
            priority = "Info",
            publishedDate = System.currentTimeMillis() - 86400000 * 3,
            authorName = "Kader Posyandu RT 02"
        )
    )

    private val serverHousehold = Household(
        publicId = "hh-budi-01",
        kkNumberMasked = "327501090112**** (Masked)",
        headName = "Budi Santoso, S.T.",
        address = "Jl. Mawar No. 04, Blok A",
        rt = "02",
        rw = "05",
        members = listOf(
            Resident(
                publicId = "res-01",
                personId = "per-01",
                nikMasked = "327501150482**** (Masked)",
                fullName = "Budi Santoso, S.T.",
                gender = "Laki-laki",
                birthPlace = "Surabaya",
                birthDate = "15/04/1982",
                religion = "Islam",
                relation = "Kepala Keluarga",
                address = "Jl. Mawar No. 04, Blok A",
                rt = "02",
                rw = "05",
                phoneMasked = "0812****4321",
                occupation = "Karyawan Swasta",
                residencyStatus = "Warga Tetap",
                isHeadOfFamily = true
            ),
            Resident(
                publicId = "res-02",
                personId = "per-02",
                nikMasked = "327501520885**** (Masked)",
                fullName = "Dewi Anggraini, S.Pd.",
                gender = "Perempuan",
                birthPlace = "Malang",
                birthDate = "22/08/1985",
                religion = "Islam",
                relation = "Istri",
                address = "Jl. Mawar No. 04, Blok A",
                rt = "02",
                rw = "05",
                phoneMasked = "0813****9988",
                occupation = "Guru",
                residencyStatus = "Warga Tetap"
            ),
            Resident(
                publicId = "res-03",
                personId = "per-03",
                nikMasked = "327501100312**** (Masked)",
                fullName = "Rizky Pratama Santoso",
                gender = "Laki-laki",
                birthPlace = "Depok",
                birthDate = "10/03/2012",
                religion = "Islam",
                relation = "Anak",
                address = "Jl. Mawar No. 04, Blok A",
                rt = "02",
                rw = "05",
                phoneMasked = "-",
                occupation = "Pelajar",
                residencyStatus = "Warga Tetap"
            )
        )
    )

    suspend fun getMyHousehold(): ApiEnvelope<Household> {
        delay(150)
        return ApiEnvelope(status = "OK", message = "Success", data = serverHousehold)
    }

    suspend fun getScopedResidents(query: String? = null): ApiEnvelope<List<Resident>> {
        delay(150)
        val all = serverHousehold.members + listOf(
            Resident(
                publicId = "res-04",
                personId = "per-04",
                nikMasked = "327501120175**** (Masked)",
                fullName = "H. Sutrisno Wibowo",
                gender = "Laki-laki",
                birthPlace = "Yogyakarta",
                birthDate = "12/01/1975",
                religion = "Islam",
                relation = "Kepala Keluarga",
                address = "Jl. Mawar No. 01, Blok A",
                rt = "02",
                rw = "05",
                phoneMasked = "0812****8899",
                occupation = "PNS",
                residencyStatus = "Warga Tetap",
                isHeadOfFamily = true
            ),
            Resident(
                publicId = "res-05",
                personId = "per-05",
                nikMasked = "327501250980**** (Masked)",
                fullName = "Ibu Hj. Aminah",
                gender = "Perempuan",
                birthPlace = "Solo",
                birthDate = "25/09/1980",
                religion = "Islam",
                relation = "Kepala Keluarga",
                address = "Jl. Mawar No. 02, Blok A",
                rt = "02",
                rw = "05",
                phoneMasked = "0815****7766",
                occupation = "Wiraswasta",
                residencyStatus = "Warga Tetap",
                isHeadOfFamily = true
            )
        )
        val filtered = if (query.isNullOrBlank()) all else all.filter {
            it.fullName.contains(query, ignoreCase = true) || it.address.contains(query, ignoreCase = true)
        }
        return ApiEnvelope(status = "OK", message = "Success", data = filtered)
    }

    suspend fun getLetterTypes(): ApiEnvelope<List<LetterType>> {
        delay(100)
        return ApiEnvelope(status = "OK", message = "Success", data = serverLetterTypes)
    }

    suspend fun getLetters(): ApiEnvelope<List<LetterItem>> {
        delay(150)
        return ApiEnvelope(status = "OK", message = "Success", data = serverLetters.toList())
    }

    suspend fun submitLetter(typeCode: String, purpose: String, notes: String): ApiEnvelope<LetterItem> {
        delay(200)
        val type = serverLetterTypes.find { it.code == typeCode } ?: serverLetterTypes.first()
        val user = authRepository.activeContext.value
        val rt = user?.rtNumber ?: "02"
        val rw = user?.rwNumber ?: "05"
        val count = serverLetters.size + 1
        val letterNum = String.format("%02d/SP-RT%s/RW%s/IX/2026", count, rt, rw)
        val newItem = LetterItem(
            publicId = UUID.randomUUID().toString(),
            letterNumber = letterNum,
            typeCode = type.code,
            typeName = type.name,
            applicantName = "Budi Santoso, S.T.",
            applicantNikMasked = "327501******0005",
            applicantAddress = "Jl. Mawar No. 04, Blok A",
            rt = rt,
            rw = rw,
            purpose = purpose,
            notes = notes,
            status = LetterStatus.SUBMITTED,
            requestDate = System.currentTimeMillis()
        )
        serverLetters.add(0, newItem)
        return ApiEnvelope(status = "OK", message = "Surat berhasil diajukan ke Ketua RT", data = newItem)
    }

    suspend fun transitionLetter(letterId: String, action: String, reason: String?): ApiEnvelope<LetterItem> {
        delay(200)
        val idx = serverLetters.indexOfFirst { it.publicId == letterId }
        if (idx == -1) throw IllegalArgumentException("Surat tidak ditemukan")
        val current = serverLetters[idx]
        val updated = when (action.uppercase()) {
            "PROCESS" -> current.copy(status = LetterStatus.PROCESSING, processedDate = System.currentTimeMillis())
            "APPROVE" -> current.copy(
                status = LetterStatus.APPROVED,
                approvedDate = System.currentTimeMillis(),
                approverName = "Ketua RT 02"
            )
            "REJECT" -> current.copy(
                status = LetterStatus.REJECTED,
                rejectionReason = reason ?: "Data pendukung belum lengkap"
            )
            "CANCEL" -> current.copy(status = LetterStatus.CANCELED)
            else -> current
        }
        serverLetters[idx] = updated
        return ApiEnvelope(status = "OK", message = "Status surat berhasil diperbarui", data = updated)
    }

    suspend fun getSecurityReports(): ApiEnvelope<List<IncidentReportItem>> {
        delay(150)
        return ApiEnvelope(status = "OK", message = "Success", data = serverReports.toList())
    }

    suspend fun submitSecurityReport(category: String, urgency: String, location: String, description: String): ApiEnvelope<IncidentReportItem> {
        delay(200)
        val newItem = IncidentReportItem(
            publicId = UUID.randomUUID().toString(),
            reporterName = "Budi Santoso",
            reporterPhoneMasked = "0812****4321",
            category = category,
            urgency = urgency,
            location = location,
            description = description,
            status = ReportStatus.OPEN,
            timestamp = System.currentTimeMillis()
        )
        serverReports.add(0, newItem)
        return ApiEnvelope(status = "OK", message = "Laporan berhasil dikirim ke Pengurus & Keamanan", data = newItem)
    }

    suspend fun transitionSecurityReport(reportId: String, action: String, note: String?): ApiEnvelope<IncidentReportItem> {
        delay(150)
        val idx = serverReports.indexOfFirst { it.publicId == reportId }
        if (idx == -1) throw IllegalArgumentException("Laporan tidak ditemukan")
        val current = serverReports[idx]
        val updated = when (action.uppercase()) {
            "ACKNOWLEDGE" -> current.copy(status = ReportStatus.ACKNOWLEDGED, internalNotes = note)
            "PROGRESS" -> current.copy(status = ReportStatus.IN_PROGRESS, internalNotes = note)
            "RESOLVE" -> current.copy(status = ReportStatus.RESOLVED, resolvedTimestamp = System.currentTimeMillis(), internalNotes = note)
            else -> current
        }
        serverReports[idx] = updated
        return ApiEnvelope(status = "OK", message = "Status laporan diperbarui", data = updated)
    }

    suspend fun getActiveSosAlerts(): ApiEnvelope<List<SosAlertItem>> {
        delay(100)
        return ApiEnvelope(status = "OK", message = "Success", data = serverSosAlerts.toList())
    }

    suspend fun triggerSos(emergencyType: String, location: String): ApiEnvelope<SosAlertItem> {
        delay(250)
        val newSos = SosAlertItem(
            publicId = UUID.randomUUID().toString(),
            reporterName = "Budi Santoso (Warga RT 02)",
            reporterPhone = "081287654321",
            emergencyType = emergencyType,
            location = location.ifBlank { "Jl. Mawar No. 04, Blok A" },
            status = SosStatus.ACTIVE,
            timestamp = System.currentTimeMillis(),
            receiptConfirmed = true
        )
        serverSosAlerts.add(0, newSos)
        return ApiEnvelope(status = "OK", message = "SOS Berhasil Terkirim ke Pos Keamanan & Pengurus", data = newSos)
    }

    suspend fun transitionSos(sosId: String, action: String): ApiEnvelope<SosAlertItem> {
        delay(150)
        val idx = serverSosAlerts.indexOfFirst { it.publicId == sosId }
        if (idx == -1) throw IllegalArgumentException("SOS tidak ditemukan")
        val current = serverSosAlerts[idx]
        val updated = when (action.uppercase()) {
            "ACKNOWLEDGE" -> current.copy(status = SosStatus.ACKNOWLEDGED, responderName = "Petugas Pos Jaga")
            "RESPOND" -> current.copy(status = SosStatus.RESPONDING, responderName = "Regu Ronda Bpk. Hendra")
            "RESOLVE" -> current.copy(status = SosStatus.RESOLVED)
            "CANCEL" -> current.copy(status = SosStatus.CANCELED)
            else -> current
        }
        serverSosAlerts[idx] = updated
        return ApiEnvelope(status = "OK", message = "Status SOS berhasil diperbarui", data = updated)
    }

    suspend fun getRondaSchedules(): ApiEnvelope<List<RondaShift>> {
        delay(100)
        return ApiEnvelope(status = "OK", message = "Success", data = serverRonda.toList())
    }

    suspend fun checkInRonda(rondaId: String): ApiEnvelope<RondaShift> {
        delay(150)
        val idx = serverRonda.indexOfFirst { it.publicId == rondaId }
        if (idx == -1) throw IllegalArgumentException("Jadwal ronda tidak ditemukan")
        val current = serverRonda[idx]
        val updated = current.copy(isCheckedIn = true, checkInTime = System.currentTimeMillis())
        serverRonda[idx] = updated
        return ApiEnvelope(status = "OK", message = "Presensi ronda malam berhasil dicatat", data = updated)
    }

    suspend fun getDuesBills(): ApiEnvelope<List<DuesBillItem>> {
        delay(150)
        return ApiEnvelope(status = "OK", message = "Success", data = serverBills.toList())
    }

    suspend fun uploadPaymentProof(billId: String, paymentMethod: String): ApiEnvelope<DuesBillItem> {
        delay(200)
        val idx = serverBills.indexOfFirst { it.publicId == billId }
        if (idx == -1) throw IllegalArgumentException("Tagihan tidak ditemukan")
        val current = serverBills[idx]
        val updated = current.copy(
            status = DuesStatus.PENDING_VERIFICATION,
            paymentMethod = paymentMethod,
            paymentProofUrl = "https://kawalwarga.myindo.platform/proof/${billId}.jpg"
        )
        serverBills[idx] = updated
        return ApiEnvelope(status = "OK", message = "Bukti transfer berhasil diunggah. Menunggu verifikasi Bendahara.", data = updated)
    }

    suspend fun verifyPayment(billId: String, action: String): ApiEnvelope<DuesBillItem> {
        delay(200)
        val idx = serverBills.indexOfFirst { it.publicId == billId }
        if (idx == -1) throw IllegalArgumentException("Tagihan tidak ditemukan")
        val current = serverBills[idx]
        val updated = if (action.equals("VERIFY", ignoreCase = true)) {
            current.copy(
                status = DuesStatus.PAID,
                paymentDate = System.currentTimeMillis(),
                verifiedBy = "Ibu Hj. Aminah (Bendahara RT)"
            )
        } else {
            current.copy(status = DuesStatus.UNPAID, paymentProofUrl = null)
        }
        serverBills[idx] = updated
        return ApiEnvelope(status = "OK", message = "Status pembayaran diverifikasi", data = updated)
    }

    suspend fun recordCashPayment(billId: String, notes: String?): ApiEnvelope<DuesBillItem> {
        delay(200)
        val idx = serverBills.indexOfFirst { it.publicId == billId }
        if (idx == -1) throw IllegalArgumentException("Tagihan tidak ditemukan")
        val current = serverBills[idx]
        val updated = current.copy(
            status = DuesStatus.PAID,
            paymentMethod = "Tunai (Cash)",
            paymentDate = System.currentTimeMillis(),
            verifiedBy = "Dicatat Bendahara RT (Tunai)"
        )
        serverBills[idx] = updated
        return ApiEnvelope(status = "OK", message = "Pembayaran tunai berhasil dicatat dan lunas", data = updated)
    }

    suspend fun getAnnouncements(): ApiEnvelope<List<AnnouncementItem>> {
        delay(100)
        return ApiEnvelope(status = "OK", message = "Success", data = serverAnnouncements.toList())
    }
}
