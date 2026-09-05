package id.myindo.platform.kawalwarga.core.database.dao

import androidx.room.*
import id.myindo.platform.kawalwarga.core.database.entity.PendingActionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingActionDao {
    @Query("SELECT * FROM pending_actions WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingActions(): Flow<List<PendingActionEntity>>

    @Query("SELECT COUNT(*) FROM pending_actions WHERE status = 'PENDING'")
    fun getPendingCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: PendingActionEntity): Long

    @Update
    suspend fun updateAction(action: PendingActionEntity)

    @Delete
    suspend fun deleteAction(action: PendingActionEntity)

    @Query("DELETE FROM pending_actions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM pending_actions")
    suspend fun clearAll()
}
