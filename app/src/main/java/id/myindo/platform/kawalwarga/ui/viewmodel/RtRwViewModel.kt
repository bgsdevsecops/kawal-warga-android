package id.myindo.platform.kawalwarga.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import id.myindo.platform.kawalwarga.core.auth.AuthRepository
import id.myindo.platform.kawalwarga.core.auth.AuthState
import id.myindo.platform.kawalwarga.core.auth.TokenStorage
import id.myindo.platform.kawalwarga.core.model.*
import id.myindo.platform.kawalwarga.core.network.RemoteDataSource
import id.myindo.platform.kawalwarga.core.sync.SyncState
import id.myindo.platform.kawalwarga.data.AppDatabase
import id.myindo.platform.kawalwarga.data.RtRwRepository
import id.myindo.platform.kawalwarga.data.model.Announcement
import id.myindo.platform.kawalwarga.data.model.Citizen
import id.myindo.platform.kawalwarga.data.model.DuesPayment
import id.myindo.platform.kawalwarga.data.model.LetterRequest
import id.myindo.platform.kawalwarga.data.model.SecurityReport
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

enum class MainTab(val title: String) {
    BERANDA("Beranda"),
    WARGA("Warga"),
    SURAT("Surat"),
    KEAMANAN("Keamanan"),
    IURAN("Iuran")
}

class RtRwViewModel(application: Application) : AndroidViewModel(application) {
    val tokenStorage = TokenStorage(application)
    val authRepository = AuthRepository(tokenStorage)
    private val remoteDataSource = RemoteDataSource(authRepository)
    val repository: RtRwRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = RtRwRepository(
            citizenDao = database.citizenDao(),
            letterRequestDao = database.letterRequestDao(),
            securityReportDao = database.securityReportDao(),
            duesPaymentDao = database.duesPaymentDao(),
            announcementDao = database.announcementDao(),
            pendingActionDao = database.pendingActionDao(),
            authRepository = authRepository,
            remoteDataSource = remoteDataSource
        )

        viewModelScope.launch {
            repository.initialBootstrap()
        }
    }

    // ----------------------------------------------------
    // AUTH & CONTEXT SWITCHER STATE
    // ----------------------------------------------------
    val authState: StateFlow<AuthState> = authRepository.authState
    val activeContext: StateFlow<UserContext?> = authRepository.activeContext

    fun switchContext(contextId: String) {
        authRepository.switchContext(contextId)
        val newContext = authRepository.activeContext.value
        showMessage("Context dialihkan ke: ${newContext?.label ?: contextId}")
        viewModelScope.launch {
            repository.syncManager.syncAll()
        }
    }

    fun logout() {
        authRepository.logout()
        showMessage("Anda telah logout dari aplikasi")
    }

    fun login(role: Role = Role.WARGA) {
        authRepository.loginMockSuccess(role)
        viewModelScope.launch {
            repository.initialBootstrap()
        }
    }

    // ----------------------------------------------------
    // NAVIGATION & SYNC STATE
    // ----------------------------------------------------
    private val _currentTab = MutableStateFlow(MainTab.BERANDA)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    fun setTab(tab: MainTab) {
        _currentTab.value = tab
    }

    val syncState: StateFlow<SyncState> = repository.syncManager.syncState
    val lastSynced: StateFlow<Long> = repository.syncManager.lastSynced

    fun syncNow() {
        viewModelScope.launch {
            val success = repository.syncManager.syncAll()
            if (success) {
                showMessage("Data berhasil disinkronkan dengan server")
            } else {
                showMessage("Gagal sinkronisasi data. Periksa koneksi.")
            }
        }
    }

    // Toast/Snackbar message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    // ----------------------------------------------------
    // CITIZENS / RESIDENTS
    // ----------------------------------------------------
    val allCitizens: StateFlow<List<Citizen>> = repository.allCitizens
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val myHousehold: StateFlow<Household?> = repository.myHousehold
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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
    ) { citizens, query, rtFilter, statusFilter ->
        citizens.filter { citizen ->
            val matchesQuery = query.isBlank() ||
                    citizen.fullName.contains(query, ignoreCase = true) ||
                    citizen.address.contains(query, ignoreCase = true) ||
                    citizen.nik.contains(query, ignoreCase = true)
            val matchesRt = rtFilter == "Semua RT" || citizen.rt == rtFilter.replace("RT ", "")
            val matchesStatus = statusFilter == "Semua Status" || citizen.residenceStatus.equals(statusFilter, ignoreCase = true)
            matchesQuery && matchesRt && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var selectedCitizenForDetail = MutableStateFlow<Citizen?>(null)
        private set
    var editingCitizen = MutableStateFlow<Citizen?>(null)
        private set
    var isAddingCitizen = MutableStateFlow(false)
        private set
    var isCorrectionRequestOpen = MutableStateFlow(false)
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

    fun openCorrectionRequest() { isCorrectionRequestOpen.value = true }
    fun closeCorrectionRequest() { isCorrectionRequestOpen.value = false }

    fun submitCorrectionRequest(field: String, requestedValue: String, reason: String) {
        viewModelScope.launch {
            showMessage("Permohonan koreksi data '$field' telah dikirim ke Pengurus RT untuk diverifikasi.")
            closeCorrectionRequest()
        }
    }

    fun saveCitizen(citizen: Citizen) {
        viewModelScope.launch {
            if (citizen.id == 0L) {
                repository.insertCitizen(citizen)
                showMessage("Data warga berhasil ditambahkan ke sistem RT")
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

    // ----------------------------------------------------
    // LETTERS (SURAT PENGANTAR)
    // ----------------------------------------------------
    val allLetters: StateFlow<List<LetterRequest>> = repository.allLetterRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val letterTypes: StateFlow<List<LetterType>> = repository.letterTypes
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

    var isAddingLetter = MutableStateFlow(false)
        private set
    var previewLetter = MutableStateFlow<LetterRequest?>(null)
        private set

    fun openAddLetter() { isAddingLetter.value = true }
    fun closeAddLetter() { isAddingLetter.value = false }

    fun openLetterPreview(letter: LetterRequest) { previewLetter.value = letter }
    fun closeLetterPreview() { previewLetter.value = null }

    fun submitLetterRequest(
        typeCode: String,
        purpose: String,
        notes: String
    ) {
        viewModelScope.launch {
            try {
                val item = repository.submitLetter(typeCode, purpose, notes)
                showMessage("Pengajuan ${item.typeName} berhasil dikirim ke server. No: ${item.letterNumber}")
                closeAddLetter()
            } catch (e: Exception) {
                showMessage("Gagal mengirim pengajuan surat: ${e.message}")
            }
        }
    }

    fun transitionLetter(letter: LetterRequest, action: String, reason: String? = null) {
        viewModelScope.launch {
            try {
                // Find public id if matching, otherwise simulate transition on cache
                val updatedStatus = when (action.uppercase()) {
                    "PROCESS" -> "Diproses"
                    "APPROVE" -> "Disetujui"
                    "REJECT" -> "Ditolak"
                    "CANCEL" -> "Dibatalkan"
                    else -> letter.status
                }
                val updated = letter.copy(
                    status = updatedStatus,
                    rejectionReason = reason,
                    processedDate = if (updatedStatus == "Diproses" || updatedStatus == "Disetujui") System.currentTimeMillis() else letter.processedDate,
                    approvedDate = if (updatedStatus == "Disetujui") System.currentTimeMillis() else letter.approvedDate
                )
                repository.updateLetterRequest(updated)
                showMessage("Status surat berhasil diperbarui: $updatedStatus")
                if (previewLetter.value?.id == letter.id) {
                    previewLetter.value = updated
                }
            } catch (e: Exception) {
                showMessage("Gagal memperbarui status surat: ${e.message}")
            }
        }
    }

    // ----------------------------------------------------
    // SECURITY & SOS ALERTS & RONDA
    // ----------------------------------------------------
    val allSecurityReports: StateFlow<List<SecurityReport>> = repository.allSecurityReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rondaSchedules: StateFlow<List<RondaShift>> = repository.rondaSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeSosAlerts: StateFlow<List<SosAlertItem>> = repository.activeSosAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
            try {
                val sos = repository.triggerSos(category, location)
                isSosAlertActive.value = true
                showMessage("SOS Terkirim! Receipt dikonfirmasi server (ID: ${sos.publicId.take(8)}). Petugas keamanan menuju lokasi.")
            } catch (e: Exception) {
                showMessage("SOS belum terkirim karena tidak ada koneksi. Gunakan panggilan darurat langsung.")
            }
        }
    }

    fun dismissSosAlert() {
        isSosAlertActive.value = false
    }

    fun submitSecurityReport(
        category: String,
        urgency: String,
        location: String,
        description: String
    ) {
        viewModelScope.launch {
            try {
                repository.submitSecurityReport(category, urgency, location, description)
                showMessage("Laporan keamanan berhasil dikirim ke server")
                closeAddSecurityReport()
            } catch (e: Exception) {
                showMessage("Gagal mengirim laporan: ${e.message}")
            }
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

    fun checkInRonda(rondaShift: RondaShift) {
        viewModelScope.launch {
            try {
                repository.checkInRonda(rondaShift.publicId)
                showMessage("Presensi ronda malam berhasil dicatat di server!")
            } catch (e: Exception) {
                showMessage("Gagal presensi ronda: ${e.message}")
            }
        }
    }

    // ----------------------------------------------------
    // DUES / IURAN
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

    fun uploadPaymentProof(due: DuesPayment, paymentMethod: String) {
        viewModelScope.launch {
            try {
                val updated = due.copy(
                    paymentStatus = "Menunggu Verifikasi",
                    paymentMethod = paymentMethod,
                    paymentDate = System.currentTimeMillis()
                )
                repository.updateDuesPayment(updated)
                showMessage("Bukti transfer berhasil diunggah. Menunggu verifikasi Bendahara.")
                closePaymentFlow()
            } catch (e: Exception) {
                showMessage("Gagal mengunggah bukti: ${e.message}")
            }
        }
    }

    fun verifyPayment(due: DuesPayment, isApproved: Boolean) {
        viewModelScope.launch {
            val status = if (isApproved) "Lunas" else "Belum Bayar"
            val updated = due.copy(
                paymentStatus = status,
                paymentDate = if (isApproved) System.currentTimeMillis() else null,
                collectorName = if (isApproved) "Ibu Hj. Aminah (Bendahara RT)" else due.collectorName
            )
            repository.updateDuesPayment(updated)
            showMessage(if (isApproved) "Pembayaran ${due.month} ${due.year} berhasil diverifikasi LUNAS" else "Bukti pembayaran ditolak")
            if (activePaymentDue.value?.id == due.id) {
                closePaymentFlow()
            }
        }
    }

    fun recordCashPayment(
        citizenName: String,
        houseNumber: String,
        rt: String,
        month: String,
        year: Int,
        amount: Double
    ) {
        viewModelScope.launch {
            val invNum = "INV-${year}09-${(100..999).random()}"
            val due = DuesPayment(
                invoiceNumber = invNum,
                citizenId = 1,
                citizenName = citizenName,
                houseNumber = houseNumber,
                rt = rt,
                month = month,
                year = year,
                totalAmount = amount,
                paymentStatus = "Lunas",
                paymentMethod = "Tunai (Cash)",
                paymentDate = System.currentTimeMillis(),
                collectorName = "Dicatat Bendahara RT (Tunai)"
            )
            repository.insertDuesPayment(due)
            showMessage("Pembayaran tunai $citizenName berhasil dicatat LUNAS")
            closeAddDuesRecord()
            openReceipt(due)
        }
    }

    // ----------------------------------------------------
    // ANNOUNCEMENTS
    // ----------------------------------------------------
    val allAnnouncements: StateFlow<List<Announcement>> = repository.allAnnouncements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ----------------------------------------------------
    // HELPERS
    // ----------------------------------------------------
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
}
