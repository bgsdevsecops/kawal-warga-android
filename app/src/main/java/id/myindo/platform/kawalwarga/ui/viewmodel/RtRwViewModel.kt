package id.myindo.platform.kawalwarga.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.myindo.platform.kawalwarga.data.AppDatabase
import id.myindo.platform.kawalwarga.data.RtRwRepository
import id.myindo.platform.kawalwarga.data.model.Announcement
import id.myindo.platform.kawalwarga.data.model.Citizen
import id.myindo.platform.kawalwarga.data.model.DuesPayment
import id.myindo.platform.kawalwarga.data.model.LetterRequest
import id.myindo.platform.kawalwarga.data.model.RondaSchedule
import id.myindo.platform.kawalwarga.data.model.SecurityReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MainTab(val title: String) {
    BERANDA("Beranda"),
    WARGA("Daftar Warga"),
    SURAT("Surat Pengantar"),
    KEAMANAN("Keamanan"),
    IURAN("Iuran Warga")
}

class RtRwViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RtRwRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = RtRwRepository(
            database.citizenDao(),
            database.letterRequestDao(),
            database.securityReportDao(),
            database.duesPaymentDao(),
            database.announcementDao()
        )
    }

    // Active Navigation Tab
    private val _currentTab = MutableStateFlow(MainTab.BERANDA)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
    }

    // Toast/Snackbar notification event
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    // ----------------------------------------------------
    // CITIZENS STATE & FILTERS
    // ----------------------------------------------------
    val allCitizens: StateFlow<List<Citizen>> = repository.allCitizens
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _citizenSearchQuery = MutableStateFlow("")
    val citizenSearchQuery: StateFlow<String> = _citizenSearchQuery.asStateFlow()

    private val _citizenRtFilter = MutableStateFlow("Semua RT")
    val citizenRtFilter: StateFlow<String> = _citizenRtFilter.asStateFlow()

    private val _citizenStatusFilter = MutableStateFlow("Semua Status")
    val citizenStatusFilter: StateFlow<String> = _citizenStatusFilter.asStateFlow()

    fun updateCitizenSearch(query: String) { _citizenSearchQuery.value = query }
    fun updateCitizenRtFilter(rt: String) { _citizenRtFilter.value = rt }
    fun updateCitizenStatusFilter(status: String) { _citizenStatusFilter.value = status }

    val filteredCitizens: StateFlow<List<Citizen>> = combine(
        allCitizens,
        _citizenSearchQuery,
        _citizenRtFilter,
        _citizenStatusFilter
    ) { citizens, query, rt, status ->
        citizens.filter { citizen ->
            val matchesQuery = query.isBlank() ||
                    citizen.fullName.contains(query, ignoreCase = true) ||
                    citizen.nik.contains(query) ||
                    citizen.address.contains(query, ignoreCase = true) ||
                    citizen.phone.contains(query)

            val matchesRt = rt == "Semua RT" || "RT ${citizen.rt}" == rt || citizen.rt == rt
            val matchesStatus = status == "Semua Status" || citizen.residenceStatus == status

            matchesQuery && matchesRt && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ----------------------------------------------------
    // LETTERS STATE & FILTERS
    // ----------------------------------------------------
    val allLetters: StateFlow<List<LetterRequest>> = repository.allLetterRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _letterStatusFilter = MutableStateFlow("Semua")
    val letterStatusFilter: StateFlow<String> = _letterStatusFilter.asStateFlow()

    fun updateLetterStatusFilter(status: String) { _letterStatusFilter.value = status }

    val filteredLetters: StateFlow<List<LetterRequest>> = combine(
        allLetters,
        _letterStatusFilter
    ) { letters, filter ->
        if (filter == "Semua") letters else letters.filter { it.status.equals(filter, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ----------------------------------------------------
    // SECURITY & SISKAMLING STATE
    // ----------------------------------------------------
    val allSecurityReports: StateFlow<List<SecurityReport>> = repository.allSecurityReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _securityStatusFilter = MutableStateFlow("Semua")
    val securityStatusFilter: StateFlow<String> = _securityStatusFilter.asStateFlow()

    private val _securitySubTab = MutableStateFlow("Laporan") // "Laporan" or "Jadwal Ronda"
    val securitySubTab: StateFlow<String> = _securitySubTab.asStateFlow()

    fun updateSecurityStatusFilter(status: String) { _securityStatusFilter.value = status }
    fun updateSecuritySubTab(tab: String) { _securitySubTab.value = tab }

    val filteredSecurityReports: StateFlow<List<SecurityReport>> = combine(
        allSecurityReports,
        _securityStatusFilter
    ) { reports, filter ->
        if (filter == "Semua") reports else reports.filter { it.status.equals(filter, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rondaSchedules: List<RondaSchedule> = repository.getRondaSchedules()

    // ----------------------------------------------------
    // DUES / IURAN STATE
    // ----------------------------------------------------
    val allDues: StateFlow<List<DuesPayment>> = repository.allDuesPayments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _duesStatusFilter = MutableStateFlow("Semua")
    val duesStatusFilter: StateFlow<String> = _duesStatusFilter.asStateFlow()

    private val _duesMonthFilter = MutableStateFlow("September")
    val duesMonthFilter: StateFlow<String> = _duesMonthFilter.asStateFlow()

    fun updateDuesStatusFilter(status: String) { _duesStatusFilter.value = status }
    fun updateDuesMonthFilter(month: String) { _duesMonthFilter.value = month }

    val filteredDues: StateFlow<List<DuesPayment>> = combine(
        allDues,
        _duesStatusFilter,
        _duesMonthFilter
    ) { duesList, statusFilter, monthFilter ->
        duesList.filter { due ->
            val matchesMonth = monthFilter == "Semua Bulan" || due.month.equals(monthFilter, ignoreCase = true)
            val matchesStatus = statusFilter == "Semua" || due.paymentStatus.equals(statusFilter, ignoreCase = true)
            matchesMonth && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Announcements
    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ----------------------------------------------------
    // DIALOG & ACTION STATES
    // ----------------------------------------------------
    // Citizen Dialogs
    var selectedCitizenForDetail = MutableStateFlow<Citizen?>(null)
        private set
    var editingCitizen = MutableStateFlow<Citizen?>(null)
        private set
    var isAddingCitizen = MutableStateFlow(false)
        private set

    fun openCitizenDetail(citizen: Citizen) { selectedCitizenForDetail.value = citizen }
    fun closeCitizenDetail() { selectedCitizenForDetail.value = null }

    fun openAddCitizen() {
        editingCitizen.value = null
        isAddingCitizen.value = true
    }
    fun openEditCitizen(citizen: Citizen) {
        editingCitizen.value = citizen
        isAddingCitizen.value = true
    }
    fun closeCitizenForm() {
        isAddingCitizen.value = false
        editingCitizen.value = null
    }

    fun saveCitizen(citizen: Citizen) {
        viewModelScope.launch {
            if (citizen.id == 0L) {
                repository.insertCitizen(citizen)
                showMessage("Warga baru berhasil ditambahkan")
            } else {
                repository.updateCitizen(citizen)
                showMessage("Data warga berhasil diperbarui")
                if (selectedCitizenForDetail.value?.id == citizen.id) {
                    selectedCitizenForDetail.value = citizen
                }
            }
            closeCitizenForm()
        }
    }

    fun deleteCitizen(citizen: Citizen) {
        viewModelScope.launch {
            repository.deleteCitizen(citizen)
            showMessage("Data warga ${citizen.fullName} telah dihapus")
            if (selectedCitizenForDetail.value?.id == citizen.id) {
                closeCitizenDetail()
            }
        }
    }

    // Letter Dialogs
    var isAddingLetter = MutableStateFlow(false)
        private set
    var previewLetter = MutableStateFlow<LetterRequest?>(null)
        private set

    fun openAddLetter(preselectedCitizen: Citizen? = null) {
        isAddingLetter.value = true
    }
    fun closeAddLetter() { isAddingLetter.value = false }

    fun openLetterPreview(letter: LetterRequest) { previewLetter.value = letter }
    fun closeLetterPreview() { previewLetter.value = null }

    fun submitLetterRequest(
        citizen: Citizen,
        letterType: String,
        purpose: String,
        notes: String
    ) {
        viewModelScope.launch {
            val randomNum = (10..99).random()
            val monthRom = getRomanMonth()
            val request = LetterRequest(
                letterNumber = "$randomNum/SP-RT${citizen.rt}/RW${citizen.rw}/$monthRom/2026",
                citizenId = citizen.id,
                citizenName = citizen.fullName,
                citizenNik = citizen.nik,
                citizenAddress = citizen.address,
                rt = citizen.rt,
                rw = citizen.rw,
                letterType = letterType,
                purpose = purpose,
                notes = notes,
                status = "Diajukan",
                requestDate = System.currentTimeMillis()
            )
            repository.insertLetterRequest(request)
            showMessage("Pengajuan ${letterType} berhasil dikirim")
            closeAddLetter()
        }
    }

    fun updateLetterStatus(letter: LetterRequest, newStatus: String, rejectionReason: String? = null) {
        viewModelScope.launch {
            val updated = letter.copy(
                status = newStatus,
                rejectionReason = rejectionReason,
                processedDate = if (newStatus == "Diproses" || newStatus == "Disetujui") System.currentTimeMillis() else letter.processedDate,
                approvedDate = if (newStatus == "Disetujui") System.currentTimeMillis() else letter.approvedDate
            )
            repository.updateLetterRequest(updated)
            showMessage("Status surat diperbarui menjadi: $newStatus")
            if (previewLetter.value?.id == letter.id) {
                previewLetter.value = updated
            }
        }
    }

    // Security Dialogs
    var isAddingSecurityReport = MutableStateFlow(false)
        private set
    var selectedSecurityReport = MutableStateFlow<SecurityReport?>(null)
        private set
    var isSosAlertActive = MutableStateFlow(false)
        private set

    fun openAddSecurityReport() { isAddingSecurityReport.value = true }
    fun closeAddSecurityReport() { isAddingSecurityReport.value = false }

    fun openSecurityReportDetail(report: SecurityReport) { selectedSecurityReport.value = report }
    fun closeSecurityReportDetail() { selectedSecurityReport.value = null }

    fun triggerPanicSos(category: String, location: String) {
        viewModelScope.launch {
            val report = SecurityReport(
                reporterName = "Warga RT 02 (Tombol SOS Darurat)",
                reporterPhone = "Panggilan Cepat",
                category = "Bencana / Darurat",
                urgency = "Darurat",
                location = location.ifBlank { "Lingkungan RT 02 / RW 05" },
                description = "SINYAL PANIK DARURAT DIAKTIFKAN: $category. Petugas keamanan dan warga terdekat dimohon segera merapat!",
                status = "Menunggu Respon",
                isPanicAlert = true,
                timestamp = System.currentTimeMillis()
            )
            repository.insertSecurityReport(report)
            isSosAlertActive.value = true
            showMessage("ALERT DARURAT DIAKTIFKAN! Petugas ronda & warga telah dinotifikasi.")
        }
    }

    fun dismissSosAlert() { isSosAlertActive.value = false }

    fun submitSecurityReport(
        reporterName: String,
        phone: String,
        category: String,
        urgency: String,
        location: String,
        description: String
    ) {
        viewModelScope.launch {
            val report = SecurityReport(
                reporterName = reporterName,
                reporterPhone = phone,
                category = category,
                urgency = urgency,
                location = location,
                description = description,
                status = "Menunggu Respon",
                timestamp = System.currentTimeMillis()
            )
            repository.insertSecurityReport(report)
            showMessage("Laporan keamanan berhasil dikirim ke pengurus RT")
            closeAddSecurityReport()
        }
    }

    fun updateSecurityReportStatus(report: SecurityReport, newStatus: String, note: String?) {
        viewModelScope.launch {
            val updated = report.copy(
                status = newStatus,
                responseNote = note,
                resolvedTimestamp = if (newStatus == "Selesai") System.currentTimeMillis() else null
            )
            repository.updateSecurityReport(updated)
            showMessage("Status laporan berhasil diubah ke: $newStatus")
            if (selectedSecurityReport.value?.id == report.id) {
                selectedSecurityReport.value = updated
            }
        }
    }

    // Dues Payment Dialogs & Receipts
    var activePaymentDue = MutableStateFlow<DuesPayment?>(null)
        private set
    var isAddingDuesRecord = MutableStateFlow(false)
        private set
    var receiptPayment = MutableStateFlow<DuesPayment?>(null)
        private set

    fun openPaymentFlow(due: DuesPayment) { activePaymentDue.value = due }
    fun closePaymentFlow() { activePaymentDue.value = null }

    fun openAddDuesRecord() { isAddingDuesRecord.value = true }
    fun closeAddDuesRecord() { isAddingDuesRecord.value = false }

    fun openReceipt(due: DuesPayment) { receiptPayment.value = due }
    fun closeReceipt() { receiptPayment.value = null }

    fun confirmPayment(due: DuesPayment, method: String) {
        viewModelScope.launch {
            val updated = due.copy(
                paymentStatus = "Lunas",
                paymentMethod = method,
                paymentDate = System.currentTimeMillis()
            )
            repository.updateDuesPayment(updated)
            showMessage("Pembayaran iuran ${due.month} ${due.year} berhasil diverifikasi!")
            closePaymentFlow()
            openReceipt(updated)
        }
    }

    fun createDuesInvoice(
        citizen: Citizen,
        month: String,
        year: Int,
        amountKebersihan: Double,
        amountKeamanan: Double,
        amountKasRt: Double,
        amountSosial: Double,
        status: String
    ) {
        viewModelScope.launch {
            val total = amountKebersihan + amountKeamanan + amountKasRt + amountSosial
            val invNum = "INV-${year}${String.format("%02d", getMonthIndex(month))}-${(100..999).random()}"
            val due = DuesPayment(
                invoiceNumber = invNum,
                citizenId = citizen.id,
                citizenName = citizen.fullName,
                houseNumber = citizen.address,
                rt = citizen.rt,
                month = month,
                year = year,
                amountKebersihan = amountKebersihan,
                amountKeamanan = amountKeamanan,
                amountKasRt = amountKasRt,
                amountSosial = amountSosial,
                totalAmount = total,
                paymentStatus = status,
                paymentDate = if (status == "Lunas") System.currentTimeMillis() else null
            )
            repository.insertDuesPayment(due)
            showMessage("Tagihan iuran untuk ${citizen.fullName} berhasil dibuat")
            closeAddDuesRecord()
        }
    }

    // Helper utilities
    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("in", "ID"))
        return sdf.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
        return sdf.format(Date(timestamp))
    }

    private fun getRomanMonth(): String {
        val month = SimpleDateFormat("MM", Locale.getDefault()).format(Date()).toIntOrNull() ?: 9
        return when (month) {
            1 -> "I"; 2 -> "II"; 3 -> "III"; 4 -> "IV"; 5 -> "V"; 6 -> "VI"
            7 -> "VII"; 8 -> "VIII"; 9 -> "IX"; 10 -> "X"; 11 -> "XI"; 12 -> "XII"
            else -> "IX"
        }
    }

    private fun getMonthIndex(month: String): Int {
        val months = listOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        val idx = months.indexOf(month)
        return if (idx >= 0) idx + 1 else 9
    }
}
