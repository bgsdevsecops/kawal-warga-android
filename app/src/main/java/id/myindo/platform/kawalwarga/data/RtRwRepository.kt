package id.myindo.platform.kawalwarga.data

import id.myindo.platform.kawalwarga.data.dao.AnnouncementDao
import id.myindo.platform.kawalwarga.data.dao.CitizenDao
import id.myindo.platform.kawalwarga.data.dao.DuesPaymentDao
import id.myindo.platform.kawalwarga.data.dao.LetterRequestDao
import id.myindo.platform.kawalwarga.data.dao.SecurityReportDao
import id.myindo.platform.kawalwarga.data.model.Announcement
import id.myindo.platform.kawalwarga.data.model.Citizen
import id.myindo.platform.kawalwarga.data.model.DuesPayment
import id.myindo.platform.kawalwarga.data.model.LetterRequest
import id.myindo.platform.kawalwarga.data.model.RondaSchedule
import id.myindo.platform.kawalwarga.data.model.SecurityReport
import kotlinx.coroutines.flow.Flow

class RtRwRepository(
    private val citizenDao: CitizenDao,
    private val letterRequestDao: LetterRequestDao,
    private val securityReportDao: SecurityReportDao,
    private val duesPaymentDao: DuesPaymentDao,
    private val announcementDao: AnnouncementDao
) {
    // Citizens
    val allCitizens: Flow<List<Citizen>> = citizenDao.getAllCitizens()
    suspend fun getCitizenById(id: Long): Citizen? = citizenDao.getCitizenById(id)
    fun getFamilyMembers(noKk: String): Flow<List<Citizen>> = citizenDao.getFamilyMembers(noKk)
    suspend fun insertCitizen(citizen: Citizen): Long = citizenDao.insertCitizen(citizen)
    suspend fun updateCitizen(citizen: Citizen) = citizenDao.updateCitizen(citizen)
    suspend fun deleteCitizen(citizen: Citizen) = citizenDao.deleteCitizen(citizen)

    // Letters
    val allLetterRequests: Flow<List<LetterRequest>> = letterRequestDao.getAllLetterRequests()
    suspend fun getLetterRequestById(id: Long): LetterRequest? = letterRequestDao.getLetterRequestById(id)
    suspend fun insertLetterRequest(request: LetterRequest): Long = letterRequestDao.insertLetterRequest(request)
    suspend fun updateLetterRequest(request: LetterRequest) = letterRequestDao.updateLetterRequest(request)
    suspend fun deleteLetterRequest(request: LetterRequest) = letterRequestDao.deleteLetterRequest(request)

    // Security Reports
    val allSecurityReports: Flow<List<SecurityReport>> = securityReportDao.getAllReports()
    suspend fun insertSecurityReport(report: SecurityReport): Long = securityReportDao.insertReport(report)
    suspend fun updateSecurityReport(report: SecurityReport) = securityReportDao.updateReport(report)
    suspend fun deleteSecurityReport(report: SecurityReport) = securityReportDao.deleteReport(report)

    // Dues Payments
    val allDuesPayments: Flow<List<DuesPayment>> = duesPaymentDao.getAllDues()
    fun getDuesByMonthYear(month: String, year: Int): Flow<List<DuesPayment>> = duesPaymentDao.getDuesByMonthYear(month, year)
    suspend fun insertDuesPayment(due: DuesPayment): Long = duesPaymentDao.insertDue(due)
    suspend fun updateDuesPayment(due: DuesPayment) = duesPaymentDao.updateDue(due)
    suspend fun deleteDuesPayment(due: DuesPayment) = duesPaymentDao.deleteDue(due)

    // Announcements
    val allAnnouncements: Flow<List<Announcement>> = announcementDao.getAllAnnouncements()
    suspend fun insertAnnouncement(announcement: Announcement): Long = announcementDao.insertAnnouncement(announcement)
    suspend fun deleteAnnouncement(announcement: Announcement) = announcementDao.deleteAnnouncement(announcement)

    // Static / Roster Ronda Schedule data
    fun getRondaSchedules(): List<RondaSchedule> {
        return listOf(
            RondaSchedule(
                day = "Senin",
                rt = "RT 02",
                location = "Pos Ronda Utama Blok A",
                timeRange = "22:00 - 04:00 WIB",
                patrolOfficers = listOf("Pak Bambang", "Dimas Arya", "Pak Yudi", "Pak Joko"),
                coordinator = "Bpk. Bambang (081233445566)"
            ),
            RondaSchedule(
                day = "Selasa",
                rt = "RT 02",
                location = "Pos Ronda Melati Blok B",
                timeRange = "22:00 - 04:00 WIB",
                patrolOfficers = listOf("Budi Santoso", "Pak Hendra", "Pak Wawan", "Pak Doni"),
                coordinator = "Bpk. Hendra (081344556677)"
            ),
            RondaSchedule(
                day = "Rabu",
                rt = "RT 02",
                location = "Pos Ronda Utama Blok A",
                timeRange = "22:00 - 04:00 WIB",
                patrolOfficers = listOf("Agus Prasetyo", "Pak Rudi", "Pak Eko", "Pak Arif"),
                coordinator = "Bpk. Rudi (081555667788)"
            ),
            RondaSchedule(
                day = "Kamis",
                rt = "RT 02",
                location = "Pos Ronda Kenanga Blok C",
                timeRange = "22:00 - 04:00 WIB",
                patrolOfficers = listOf("Pak Sugeng", "Pak Anton", "Pak Rian", "Pak Fahri"),
                coordinator = "Bpk. Sugeng (081766778899)"
            ),
            RondaSchedule(
                day = "Jumat",
                rt = "RT 02",
                location = "Pos Ronda Utama Blok A",
                timeRange = "22:00 - 04:00 WIB",
                patrolOfficers = listOf("H. Sutrisno", "Pak Gunawan", "Pak Dani", "Pak Bayu"),
                coordinator = "H. Sutrisno (Ketua RT)"
            ),
            RondaSchedule(
                day = "Sabtu (Malam Minggu)",
                rt = "RT 02",
                location = "Keliling Gabungan Blok A, B, C, D",
                timeRange = "21:30 - 04:30 WIB",
                patrolOfficers = listOf("Regu Satpam Lingkungan", "Karang Taruna RT 02", "Pak Budi", "Pak Agus"),
                coordinator = "Komandan Regu Satpam (081122334455)"
            ),
            RondaSchedule(
                day = "Minggu",
                rt = "RT 02",
                location = "Pos Ronda Utama Blok A",
                timeRange = "22:00 - 04:00 WIB",
                patrolOfficers = listOf("Pak Heru", "Pak Dedi", "Pak Taufik", "Pak Surya"),
                coordinator = "Bpk. Heru (081988776655)"
            )
        )
    }
}
