package id.myindo.platform.kawalwarga.core.network

import id.myindo.platform.kawalwarga.core.model.*
import retrofit2.Response
import retrofit2.http.*

interface KawalWargaApi {
    @GET("api/v1/kawal-warga/bootstrap")
    suspend fun getBootstrap(): Response<ApiEnvelope<UserBootstrap>>

    @POST("api/v1/kawal-warga/context/switch")
    suspend fun switchContext(
        @Body request: SwitchContextRequest
    ): Response<ApiEnvelope<UserContext>>

    @GET("api/v1/kawal-warga/household/me")
    suspend fun getMyHousehold(): Response<ApiEnvelope<Household>>

    @GET("api/v1/kawal-warga/residents")
    suspend fun getScopedResidents(
        @Query("rt") rt: String? = null,
        @Query("query") query: String? = null
    ): Response<ApiEnvelope<List<Resident>>>

    @GET("api/v1/kawal-warga/letters/types")
    suspend fun getLetterTypes(): Response<ApiEnvelope<List<LetterType>>>

    @GET("api/v1/kawal-warga/letters")
    suspend fun getLetters(
        @Query("scope") scope: String? = null
    ): Response<ApiEnvelope<List<LetterItem>>>

    @POST("api/v1/kawal-warga/letters")
    suspend fun submitLetter(
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body request: LetterSubmitRequest
    ): Response<ApiEnvelope<LetterItem>>

    @POST("api/v1/kawal-warga/letters/{id}/transition")
    suspend fun transitionLetter(
        @Path("id") letterId: String,
        @Body request: LetterTransitionRequest
    ): Response<ApiEnvelope<LetterItem>>

    @GET("api/v1/kawal-warga/security/reports")
    suspend fun getSecurityReports(): Response<ApiEnvelope<List<IncidentReportItem>>>

    @POST("api/v1/kawal-warga/security/reports")
    suspend fun submitSecurityReport(
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body request: SecurityReportSubmitRequest
    ): Response<ApiEnvelope<IncidentReportItem>>

    @POST("api/v1/kawal-warga/security/reports/{id}/transition")
    suspend fun transitionSecurityReport(
        @Path("id") reportId: String,
        @Body request: SecurityReportTransitionRequest
    ): Response<ApiEnvelope<IncidentReportItem>>

    @GET("api/v1/kawal-warga/security/sos")
    suspend fun getActiveSosAlerts(): Response<ApiEnvelope<List<SosAlertItem>>>

    @POST("api/v1/kawal-warga/security/sos")
    suspend fun triggerSos(
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body request: SosTriggerRequest
    ): Response<ApiEnvelope<SosAlertItem>>

    @POST("api/v1/kawal-warga/security/sos/{id}/transition")
    suspend fun transitionSos(
        @Path("id") sosId: String,
        @Body request: SosTransitionRequest
    ): Response<ApiEnvelope<SosAlertItem>>

    @GET("api/v1/kawal-warga/security/ronda")
    suspend fun getRondaSchedules(): Response<ApiEnvelope<List<RondaShift>>>

    @POST("api/v1/kawal-warga/security/ronda/{id}/checkin")
    suspend fun checkInRonda(
        @Path("id") rondaId: String
    ): Response<ApiEnvelope<RondaShift>>

    @GET("api/v1/kawal-warga/dues/bills")
    suspend fun getDuesBills(
        @Query("scope") scope: String? = null,
        @Query("month") month: String? = null
    ): Response<ApiEnvelope<List<DuesBillItem>>>

    @POST("api/v1/kawal-warga/dues/bills/{id}/proof")
    suspend fun uploadPaymentProof(
        @Path("id") billId: String,
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body request: PaymentProofUploadRequest
    ): Response<ApiEnvelope<DuesBillItem>>

    @POST("api/v1/kawal-warga/dues/bills/{id}/verify")
    suspend fun verifyPayment(
        @Path("id") billId: String,
        @Body request: PaymentVerifyRequest
    ): Response<ApiEnvelope<DuesBillItem>>

    @POST("api/v1/kawal-warga/dues/cash")
    suspend fun recordCashPayment(
        @Header("Idempotency-Key") idempotencyKey: String,
        @Body request: RecordCashRequest
    ): Response<ApiEnvelope<DuesBillItem>>

    @GET("api/v1/kawal-warga/announcements")
    suspend fun getAnnouncements(): Response<ApiEnvelope<List<AnnouncementItem>>>
}
