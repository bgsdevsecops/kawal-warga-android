package id.myindo.platform.kawalwarga.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import id.myindo.platform.kawalwarga.data.model.Announcement
import id.myindo.platform.kawalwarga.data.model.Citizen
import id.myindo.platform.kawalwarga.data.model.DuesPayment
import id.myindo.platform.kawalwarga.data.model.LetterRequest
import id.myindo.platform.kawalwarga.data.model.SecurityReport
import kotlinx.coroutines.flow.Flow

@Dao
interface CitizenDao {
    @Query("SELECT * FROM citizens ORDER BY fullName ASC")
    fun getAllCitizens(): Flow<List<Citizen>>

    @Query("SELECT * FROM citizens WHERE id = :id LIMIT 1")
    suspend fun getCitizenById(id: Long): Citizen?

    @Query("SELECT * FROM citizens WHERE noKk = :noKk ORDER BY familyRole ASC")
    fun getFamilyMembers(noKk: String): Flow<List<Citizen>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitizen(citizen: Citizen): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitizens(citizens: List<Citizen>)

    @Update
    suspend fun updateCitizen(citizen: Citizen)

    @Delete
    suspend fun deleteCitizen(citizen: Citizen)
}

@Dao
interface LetterRequestDao {
    @Query("SELECT * FROM letter_requests ORDER BY requestDate DESC")
    fun getAllLetterRequests(): Flow<List<LetterRequest>>

    @Query("SELECT * FROM letter_requests WHERE id = :id LIMIT 1")
    suspend fun getLetterRequestById(id: Long): LetterRequest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetterRequest(request: LetterRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLetterRequests(requests: List<LetterRequest>)

    @Update
    suspend fun updateLetterRequest(request: LetterRequest)

    @Delete
    suspend fun deleteLetterRequest(request: LetterRequest)
}

@Dao
interface SecurityReportDao {
    @Query("SELECT * FROM security_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<SecurityReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: SecurityReport): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReports(reports: List<SecurityReport>)

    @Update
    suspend fun updateReport(report: SecurityReport)

    @Delete
    suspend fun deleteReport(report: SecurityReport)
}

@Dao
interface DuesPaymentDao {
    @Query("SELECT * FROM dues_payments ORDER BY year DESC, id DESC")
    fun getAllDues(): Flow<List<DuesPayment>>

    @Query("SELECT * FROM dues_payments WHERE month = :month AND year = :year")
    fun getDuesByMonthYear(month: String, year: Int): Flow<List<DuesPayment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDue(due: DuesPayment): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDues(dues: List<DuesPayment>)

    @Update
    suspend fun updateDue(due: DuesPayment)

    @Delete
    suspend fun deleteDue(due: DuesPayment)
}

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY date DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<Announcement>)

    @Delete
    suspend fun deleteAnnouncement(announcement: Announcement)
}
