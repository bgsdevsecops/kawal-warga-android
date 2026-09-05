package id.myindo.platform.kawalwarga.core.auth

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object KeycloakConfig {
    const val REALM = "htz-auth"
    const val CLIENT_ID = "kawal-warga-android"
    const val REDIRECT_URI = "id.myindo.platform.kawalwarga://oauth2redirect"
    const val AUTH_URL = "https://auth.myindo.platform/realms/htz-auth/protocol/openid-connect/auth"
    const val TOKEN_URL = "https://auth.myindo.platform/realms/htz-auth/protocol/openid-connect/token"
    const val USERINFO_URL = "https://auth.myindo.platform/realms/htz-auth/protocol/openid-connect/userinfo"
    const val LOGOUT_URL = "https://auth.myindo.platform/realms/htz-auth/protocol/openid-connect/logout"
    const val SCOPES = "openid profile email offline_access"

    fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val codeVerifier = ByteArray(32)
        secureRandom.nextBytes(codeVerifier)
        return Base64.encodeToString(codeVerifier, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    fun generateCodeChallenge(codeVerifier: String): String {
        val bytes = codeVerifier.toByteArray(Charsets.US_ASCII)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        val digest = messageDigest.digest()
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}
