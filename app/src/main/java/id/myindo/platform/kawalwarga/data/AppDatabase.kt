package id.myindo.platform.kawalwarga.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.myindo.platform.kawalwarga.core.database.dao.PendingActionDao
import id.myindo.platform.kawalwarga.core.database.entity.PendingActionEntity
import id.myindo.platform.kawalwarga.data.dao.AnnouncementDao
import id.myindo.platform.kawalwarga.data.dao.CitizenDao
import id.myindo.platform.kawalwarga.data.dao.DuesPaymentDao
import id.myindo.platform.kawalwarga.data.dao.LetterRequestDao
import id.myindo.platform.kawalwarga.data.dao.SecurityReportDao
import id.myindo.platform.kawalwarga.data.model.Announcement
import id.myindo.platform.kawalwarga.data.model.Citizen
import id.myindo.platform.kawalwarga.data.model.DuesPayment
import id.myindo.platform.kawalwarga.data.model.LetterRequest
import id.myindo.platform.kawalwarga.data.model.SecurityReport

/**
 * Room Database serving as a local cache and offline pending commands queue.
 * Remote backend (Django) is the single source of truth.
 */
@Database(
    entities = [
        Citizen::class,
        LetterRequest::class,
        SecurityReport::class,
        DuesPayment::class,
        Announcement::class,
        PendingActionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun citizenDao(): CitizenDao
    abstract fun letterRequestDao(): LetterRequestDao
    abstract fun securityReportDao(): SecurityReportDao
    abstract fun duesPaymentDao(): DuesPaymentDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun pendingActionDao(): PendingActionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kawal_warga_cache.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
