package id.myindo.platform.kawalwarga.core.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiEnvelope<T>(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String,
    @Json(name = "data") val data: T,
    @Json(name = "pagination") val pagination: PaginationInfo? = null
)

@JsonClass(generateAdapter = true)
data class PaginationInfo(
    @Json(name = "page") val page: Int,
    @Json(name = "pageSize") val pageSize: Int,
    @Json(name = "totalItems") val totalItems: Int,
    @Json(name = "totalPages") val totalPages: Int
)

@JsonClass(generateAdapter = true)
data class ErrorEnvelope(
    @Json(name = "status") val status: String,
    @Json(name = "code") val code: String,
    @Json(name = "message") val message: String,
    @Json(name = "errors") val errors: Map<String, List<String>>? = null
)

// Request payloads
@JsonClass(generateAdapter = true)
data class SwitchContextRequest(
    @Json(name = "contextId") val contextId: String
)

@JsonClass(generateAdapter = true)
data class LetterSubmitRequest(
    @Json(name = "typeCode") val typeCode: String,
    @Json(name = "purpose") val purpose: String,
    @Json(name = "notes") val notes: String = "",
    @Json(name = "applicantResidentId") val applicantResidentId: String? = null
)

@JsonClass(generateAdapter = true)
data class LetterTransitionRequest(
    @Json(name = "action") val action: String, // "PROCESS", "APPROVE", "REJECT", "CANCEL"
    @Json(name = "reason") val reason: String? = null
)

@JsonClass(generateAdapter = true)
data class SecurityReportSubmitRequest(
    @Json(name = "category") val category: String,
    @Json(name = "urgency") val urgency: String,
    @Json(name = "location") val location: String,
    @Json(name = "description") val description: String,
    @Json(name = "photoBase64") val photoBase64: String? = null
)

@JsonClass(generateAdapter = true)
data class SecurityReportTransitionRequest(
    @Json(name = "action") val action: String, // "ACKNOWLEDGE", "PROGRESS", "RESOLVE", "CLOSE"
    @Json(name = "note") val note: String? = null
)

@JsonClass(generateAdapter = true)
data class SosTriggerRequest(
    @Json(name = "emergencyType") val emergencyType: String,
    @Json(name = "location") val location: String,
    @Json(name = "latitude") val latitude: Double? = null,
    @Json(name = "longitude") val longitude: Double? = null
)

@JsonClass(generateAdapter = true)
data class SosTransitionRequest(
    @Json(name = "action") val action: String, // "ACKNOWLEDGE", "RESPOND", "RESOLVE", "CANCEL"
    @Json(name = "responderNote") val responderNote: String? = null
)

@JsonClass(generateAdapter = true)
data class PaymentProofUploadRequest(
    @Json(name = "paymentMethod") val paymentMethod: String,
    @Json(name = "proofUrl") val proofUrl: String? = null,
    @Json(name = "notes") val notes: String? = null
)

@JsonClass(generateAdapter = true)
data class PaymentVerifyRequest(
    @Json(name = "action") val action: String, // "VERIFY", "REJECT"
    @Json(name = "note") val note: String? = null
)

@JsonClass(generateAdapter = true)
data class RecordCashRequest(
    @Json(name = "billId") val billId: String,
    @Json(name = "amountReceived") val amountReceived: Double,
    @Json(name = "notes") val notes: String? = null
)
