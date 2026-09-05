package id.myindo.platform.kawalwarga.data

import id.myindo.platform.kawalwarga.core.auth.AuthRepository
import id.myindo.platform.kawalwarga.core.database.dao.PendingActionDao
import id.myindo.platform.kawalwarga.core.database.entity.PendingActionEntity
import id.myindo.platform.kawalwarga.core.model.*
import id.myindo.platform.kawalwarga.core.network.RemoteDataSource
import id.myindo.platform.kawalwarga.core.sync.SyncManager
import id.myindo.platform.kawalwarga.data.dao.*
import id.myindo.platform.kawalwarga.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class RtRwRepository(
    private val citizenDao: CitizenDao,
    private val letterRequestDao: LetterRequestDao,
    private val securityReportDao: SecurityReportDao,
    private val duesPaymentDao: DuesPaymentDao,
    private val announcementDao: AnnouncementDao,
    private val pendingActionDao: PendingActionDao,
    val authRepository: AuthRepository,
    val remoteDataSource: RemoteDataSource
) {
    val syncManager = SyncManager(
        remoteDataSource = remoteDataSource,
        citizenDao = citizenDao,
        letterRequestDao = letterRequestDao,
        securityReportDao = securityReportDao,
        duesPaymentDao = duesPaymentDao,
        announcementDao = announcementDao,
        pendingActionDao = pendingActionDao
    )

    // Dynamic Ronda State from backend
    private val _rondaSchedules = MutableStateFlow<List<RondaShift>>(emptyList())
    val rondaSchedules: Flow<List<RondaShift>> = _rondaSchedules.asStateFlow()

    // Dynamic Letter Types from backend
    private val _letterTypes = MutableStateFlow<List<LetterType>>(emptyList())
    val letterTypes: Flow<List<LetterType>> = _letterTypes.asStateFlow()

    // Household for Warga self view
    private val _myHousehold = MutableStateFlow<Household?>(null)
    val myHousehold: Flow<Household?> = _myHousehold.asStateFlow()

    // Active SOS Alerts
    private val _activeSosAlerts = MutableStateFlow<List<SosAlertItem>>(emptyList())
    val activeSosAlerts: Flow<List<SosAlertItem>> = _activeSosAlerts.asStateFlow()

    // Citizens (Cached Flow)
    val allCitizens: Flow<List<Citizen>> = citizenDao.getAllCitizens()
    suspend fun getCitizenById(id: Long): Citizen? = citizenDao.getCitizenById(id)
    fun getFamilyMembers(noKk: String): Flow<List<Citizen>> = citizenDao.getFamilyMembers(noKk)
    suspend fun insertCitizen(citizen: Citizen): Long = citizenDao.insertCitizen(citizen)
    suspend fun updateCitizen(citizen: Citizen) = citizenDao.updateCitizen(citizen)
    suspend fun deleteCitizen(citizen: Citizen) = citizenDao.deleteCitizen(citizen)

    // Letters (Cached Flow)
    val allLetterRequests: Flow<List<LetterRequest>> = letterRequestDao.getAllLetterRequests()
    suspend fun getLetterRequestById(id: Long): LetterRequest? = letterRequestDao.getLetterRequestById(id)
    suspend fun updateLetterRequest(letter: LetterRequest) = letterRequestDao.updateLetterRequest(letter)

    suspend fun submitLetter(typeCode: String, purpose: String, notes: String): LetterItem {
        val res = remoteDataSource.submitLetter(typeCode, purpose, notes)
        // Refresh local cache
        val item = res.data
        letterRequestDao.insertLetterRequest(
            LetterRequest(
                letterNumber = item.letterNumber,
                citizenId = 1,
                citizenName = item.applicantName,
                citizenNik = item.applicantNikMasked,
                citizenAddress = item.applicantAddress,
                rt = item.rt,
                rw = item.rw,
                letterType = item.typeName,
                purpose = item.purpose,
                notes = item.notes,
                status = item.status.label,
                requestDate = item.requestDate
            )
        )
        return item
    }

    suspend fun transitionLetter(letterId: String, action: String, reason: String? = null): LetterItem {
        val res = remoteDataSource.transitionLetter(letterId, action, reason)
        // Refresh all letters in cache
        syncManager.syncAll()
        return res.data
    }

    // Security Reports (Cached Flow)
    val allSecurityReports: Flow<List<SecurityReport>> = securityReportDao.getAllReports()
    suspend fun updateSecurityReport(report: SecurityReport) = securityReportDao.updateReport(report)

    suspend fun submitSecurityReport(
        category: String,
        urgency: String,
        location: String,
        description: String
    ): IncidentReportItem {
        val res = remoteDataSource.submitSecurityReport(category, urgency, location, description)
        val rep = res.data
        securityReportDao.insertReport(
            SecurityReport(
                reporterName = rep.reporterName,
                reporterPhone = rep.reporterPhoneMasked,
                category = rep.category,
                urgency = rep.urgency,
                location = rep.location,
                description = rep.description,
                status = rep.status.label,
                timestamp = rep.timestamp
            )
        )
        return rep
    }

    suspend fun transitionSecurityReport(reportId: String, action: String, note: String? = null): IncidentReportItem {
        val res = remoteDataSource.transitionSecurityReport(reportId, action, note)
        syncManager.syncAll()
        return res.data
    }

    // SOS Panic
    suspend fun triggerSos(emergencyType: String, location: String): SosAlertItem {
        val res = remoteDataSource.triggerSos(emergencyType, location)
        val sos = res.data
        refreshSosAlerts()
        return sos
    }

    suspend fun refreshSosAlerts() {
        val res = remoteDataSource.getActiveSosAlerts()
        _activeSosAlerts.value = res.data
    }

    suspend fun transitionSos(sosId: String, action: String): SosAlertItem {
        val res = remoteDataSource.transitionSos(sosId, action)
        refreshSosAlerts()
        return res.data
    }

    // Dues Payments (Cached Flow)
    val allDuesPayments: Flow<List<DuesPayment>> = duesPaymentDao.getAllDues()
    fun getDuesByMonthYear(month: String, year: Int): Flow<List<DuesPayment>> = duesPaymentDao.getDuesByMonthYear(month, year)
    suspend fun updateDuesPayment(due: DuesPayment) = duesPaymentDao.updateDue(due)
    suspend fun insertDuesPayment(due: DuesPayment): Long = duesPaymentDao.insertDue(due)

    suspend fun uploadPaymentProof(billId: String, paymentMethod: String): DuesBillItem {
        val res = remoteDataSource.uploadPaymentProof(billId, paymentMethod)
        syncManager.syncAll()
        return res.data
    }

    suspend fun verifyPayment(billId: String, action: String): DuesBillItem {
        val res = remoteDataSource.verifyPayment(billId, action)
        syncManager.syncAll()
        return res.data
    }

    suspend fun recordCashPayment(billId: String, notes: String?): DuesBillItem {
        val res = remoteDataSource.recordCashPayment(billId, notes)
        syncManager.syncAll()
        return res.data
    }

    // Announcements (Cached Flow)
    val allAnnouncements: Flow<List<Announcement>> = announcementDao.getAllAnnouncements()
    suspend fun insertAnnouncement(announcement: Announcement): Long = announcementDao.insertAnnouncement(announcement)
    suspend fun deleteAnnouncement(announcement: Announcement) = announcementDao.deleteAnnouncement(announcement)

    // Ronda
    suspend fun refreshRonda() {
        val res = remoteDataSource.getRondaSchedules()
        _rondaSchedules.value = res.data
    }

    suspend fun checkInRonda(rondaId: String): RondaShift {
        val res = remoteDataSource.checkInRonda(rondaId)
        refreshRonda()
        return res.data
    }

    suspend fun refreshLetterTypes() {
        val res = remoteDataSource.getLetterTypes()
        _letterTypes.value = res.data
    }

    suspend fun refreshMyHousehold() {
        val res = remoteDataSource.getMyHousehold()
        _myHousehold.value = res.data
    }

    suspend fun initialBootstrap() {
        authRepository.checkCurrentSession()
        refreshLetterTypes()
        refreshRonda()
        refreshMyHousehold()
        refreshSosAlerts()
        syncManager.syncAll()
    }
}
