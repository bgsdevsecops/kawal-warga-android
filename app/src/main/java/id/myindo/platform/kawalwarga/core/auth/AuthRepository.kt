package id.myindo.platform.kawalwarga.core.auth

import id.myindo.platform.kawalwarga.core.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Authenticating : AuthState()
    data class Authenticated(val bootstrap: UserBootstrap) : AuthState()
    object Unlinked : AuthState()
    object PendingVerification : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthRepository(
    private val tokenStorage: TokenStorage
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _activeContext = MutableStateFlow<UserContext?>(null)
    val activeContext: StateFlow<UserContext?> = _activeContext.asStateFlow()

    // Default multi-role bootstrap data adhering to PRD specifications
    private val sampleContexts = listOf(
        UserContext(
            contextId = "ctx-warga-01",
            role = Role.WARGA,
            scopeType = "RT",
            rtNumber = "02",
            rwNumber = "05",
            label = "Warga · RT 02 / RW 05",
            isDefault = true
        ),
        UserContext(
            contextId = "ctx-rt-01",
            role = Role.KETUA_RT,
            scopeType = "RT",
            rtNumber = "02",
            rwNumber = "05",
            label = "Ketua RT · RT 02 / RW 05"
        ),
        UserContext(
            contextId = "ctx-bendahara-01",
            role = Role.BENDAHARA,
            scopeType = "RT",
            rtNumber = "02",
            rwNumber = "05",
            label = "Bendahara · RT 02 / RW 05"
        ),
        UserContext(
            contextId = "ctx-keamanan-01",
            role = Role.PETUGAS_KEAMANAN,
            scopeType = "RW",
            rtNumber = "02",
            rwNumber = "05",
            label = "Petugas Keamanan · RW 05"
        )
    )

    private val defaultCommunity = CommunityInfo(
        communityId = "comm-rw05-rt02",
        name = "Paguyuban Warga RW 05 Sukamaju",
        subdistrict = "Sukamaju",
        district = "Cilodong",
        city = "Depok",
        rt = "02",
        rw = "05"
    )

    private val defaultProfile = UserProfile(
        userId = "usr-8899-budi",
        username = "budi.santoso",
        email = "budi.santoso@warga.id",
        fullName = "Budi Santoso, S.T.",
        phone = "081287654321"
    )

    init {
        checkCurrentSession()
    }

    fun checkCurrentSession() {
        val token = tokenStorage.getAccessToken()
        if (token != null) {
            val savedCtxId = tokenStorage.getActiveContextId()
            val ctx = sampleContexts.find { it.contextId == savedCtxId } ?: sampleContexts.first()
            _activeContext.value = ctx
            _authState.value = AuthState.Authenticated(
                UserBootstrap(
                    user = defaultProfile,
                    defaultContext = ctx,
                    availableContexts = sampleContexts,
                    permissions = getPermissionsForRole(ctx.role),
                    features = mapOf("letters" to true, "security" to true, "dues" to true, "sos" to true),
                    community = defaultCommunity
                )
            )
        } else {
            // Auto login to default authenticated state so app is immediately usable in container/emulator
            loginMockSuccess()
        }
    }

    fun loginMockSuccess(role: Role = Role.WARGA) {
        _authState.value = AuthState.Authenticating
        tokenStorage.saveTokens("dummy_access_token_jwt", "dummy_refresh_token_jwt")
        val ctx = sampleContexts.find { it.role == role } ?: sampleContexts.first()
        tokenStorage.saveActiveContextId(ctx.contextId)
        _activeContext.value = ctx
        _authState.value = AuthState.Authenticated(
            UserBootstrap(
                user = defaultProfile,
                defaultContext = ctx,
                availableContexts = sampleContexts,
                permissions = getPermissionsForRole(ctx.role),
                features = mapOf("letters" to true, "security" to true, "dues" to true, "sos" to true),
                community = defaultCommunity
            )
        )
    }

    fun switchContext(contextId: String) {
        val newCtx = sampleContexts.find { it.contextId == contextId } ?: return
        tokenStorage.saveActiveContextId(newCtx.contextId)
        _activeContext.value = newCtx
        val current = _authState.value
        if (current is AuthState.Authenticated) {
            _authState.value = current.copy(
                bootstrap = current.bootstrap.copy(
                    defaultContext = newCtx,
                    permissions = getPermissionsForRole(newCtx.role)
                )
            )
        }
    }

    fun logout() {
        tokenStorage.clear()
        _activeContext.value = null
        _authState.value = AuthState.Unauthenticated
    }

    private fun getPermissionsForRole(role: Role): List<String> {
        return when (role) {
            Role.WARGA -> listOf(
                "citizen:read_self",
                "letter:create",
                "letter:read_self",
                "dues:read_self",
                "dues:upload_proof",
                "report:create",
                "report:read_self",
                "sos:trigger",
                "ronda:view"
            )
            Role.KETUA_RT -> listOf(
                "citizen:read_scoped",
                "citizen:manage",
                "letter:review",
                "letter:approve",
                "letter:reject",
                "report:review",
                "report:resolve",
                "sos:monitor",
                "dues:read_scoped",
                "announcement:publish",
                "ronda:manage"
            )
            Role.KETUA_RW -> listOf(
                "citizen:read_scoped",
                "letter:review_rw",
                "report:review",
                "sos:monitor",
                "dues:read_rw",
                "announcement:publish"
            )
            Role.SEKRETARIS -> listOf(
                "citizen:read_scoped",
                "citizen:manage",
                "letter:review",
                "announcement:publish"
            )
            Role.BENDAHARA -> listOf(
                "dues:read_scoped",
                "dues:verify_payment",
                "dues:record_cash",
                "dues:create_bill",
                "announcement:publish"
            )
            Role.PETUGAS_KEAMANAN -> listOf(
                "sos:respond",
                "report:handle",
                "report:resolve",
                "ronda:checkin",
                "ronda:checkout",
                "sos:monitor"
            )
        }
    }
}
