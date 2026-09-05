package id.myindo.platform.kawalwarga.core.sync

import id.myindo.platform.kawalwarga.core.database.dao.PendingActionDao
import id.myindo.platform.kawalwarga.core.network.RemoteDataSource
import id.myindo.platform.kawalwarga.data.dao.*
import id.myindo.platform.kawalwarga.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

enum class SyncState {
    IDLE,
    SYNCING,
    SYNCED,
    OFFLINE,
    ERROR
}

class SyncManager(
    private val remoteDataSource: RemoteDataSource,
    private val citizenDao: CitizenDao,
    private val letterRequestDao: LetterRequestDao,
    private val securityReportDao: SecurityReportDao,
    private val duesPaymentDao: DuesPaymentDao,
    private val announcementDao: AnnouncementDao,
    private val pendingActionDao: PendingActionDao
) {
    private val _syncState = MutableStateFlow(SyncState.IDLE)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private val _lastSynced = MutableStateFlow<Long>(System.currentTimeMillis())
    val lastSynced: StateFlow<Long> = _lastSynced.asStateFlow()

    suspend fun syncAll(): Boolean = withContext(Dispatchers.IO) {
        try {
            _syncState.value = SyncState.SYNCING

            // 1. Fetch remote announcements and cache
            val announcementsRes = remoteDataSource.getAnnouncements()
            announcementsRes.data.forEach { ann ->
                announcementDao.insertAnnouncement(
                    Announcement(
                        title = ann.title,
                        content = ann.content,
                        category = ann.category,
                        date = ann.publishedDate,
                        priority = ann.priority
                    )
                )
            }

            // 2. Fetch scoped residents and cache
            val residentsRes = remoteDataSource.getScopedResidents()
            val citizens = residentsRes.data.map { res ->
                Citizen(
                    nik = res.nikMasked,
                    noKk = res.householdKkMasked.ifBlank { "327501090112****" },
                    fullName = res.fullName,
                    gender = res.gender,
                    birthPlace = res.birthPlace,
                    birthDate = res.birthDate,
                    religion = res.religion,
                    familyRole = res.relation,
                    address = res.address,
                    rt = res.rt,
                    rw = res.rw,
                    phone = res.phoneMasked,
                    occupation = res.occupation,
                    residenceStatus = res.residencyStatus,
                    notes = res.notes
                )
            }
            citizenDao.insertCitizens(citizens)

            // 3. Fetch letters and cache
            val lettersRes = remoteDataSource.getLetters()
            val letters = lettersRes.data.map { let ->
                LetterRequest(
                    letterNumber = let.letterNumber,
                    citizenId = 1,
                    citizenName = let.applicantName,
                    citizenNik = let.applicantNikMasked,
                    citizenAddress = let.applicantAddress,
                    rt = let.rt,
                    rw = let.rw,
                    letterType = let.typeName,
                    purpose = let.purpose,
                    notes = let.notes,
                    status = let.status.label,
                    rejectionReason = let.rejectionReason,
                    requestDate = let.requestDate,
                    processedDate = let.processedDate,
                    approvedDate = let.approvedDate,
                    approverName = let.approverName ?: "Ketua RT"
                )
            }
            letterRequestDao.insertLetterRequests(letters)

            // 4. Fetch security reports and cache
            val reportsRes = remoteDataSource.getSecurityReports()
            val reports = reportsRes.data.map { rep ->
                SecurityReport(
                    reporterName = rep.reporterName,
                    reporterPhone = rep.reporterPhoneMasked,
                    category = rep.category,
                    urgency = rep.urgency,
                    location = rep.location,
                    description = rep.description,
                    status = rep.status.label,
                    responseNote = rep.internalNotes,
                    timestamp = rep.timestamp,
                    resolvedTimestamp = rep.resolvedTimestamp,
                    isPanicAlert = rep.isPanicAlert
                )
            }
            securityReportDao.insertReports(reports)

            // 5. Fetch bills and cache
            val duesRes = remoteDataSource.getDuesBills()
            val dues = duesRes.data.map { due ->
                DuesPayment(
                    invoiceNumber = due.invoiceNumber,
                    citizenId = 1,
                    citizenName = due.residentName,
                    houseNumber = due.address,
                    rt = due.rt,
                    month = due.month,
                    year = due.year,
                    amountKebersihan = due.breakdown["Kebersihan"] ?: 35000.0,
                    amountKeamanan = due.breakdown["Keamanan"] ?: 50000.0,
                    amountKasRt = due.breakdown["Kas RT"] ?: 25000.0,
                    amountSosial = due.breakdown["Sosial"] ?: 15000.0,
                    totalAmount = due.totalAmount,
                    paymentStatus = due.status.label,
                    paymentMethod = due.paymentMethod ?: "Transfer Bank Manual",
                    paymentDate = due.paymentDate,
                    collectorName = due.verifiedBy ?: "Bendahara RT"
                )
            }
            duesPaymentDao.insertDues(dues)

            _lastSynced.value = System.currentTimeMillis()
            _syncState.value = SyncState.SYNCED
            true
        } catch (e: Exception) {
            _syncState.value = SyncState.ERROR
            false
        }
    }
}
