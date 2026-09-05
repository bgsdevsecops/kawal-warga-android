package id.myindo.platform.kawalwarga.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_actions")
data class PendingActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val actionId: String, // UUID Idempotency-Key
    val actionType: String, // "SUBMIT_LETTER", "SUBMIT_REPORT", "UPLOAD_PROOF"
    val endpoint: String,
    val payloadJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val status: String = "PENDING", // "PENDING", "SYNCING", "FAILED"
    val retryCount: Int = 0,
    val lastError: String? = null
)
