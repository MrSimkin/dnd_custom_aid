package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import com.descope.Descope
import com.descope.session.DescopeSession
import com.descope.types.DeliveryMethod
import com.descope.types.RevokeType
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedAccessTokenProvider
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedAccount
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiClient

internal object HostedDevelopmentEnvironment {
    const val DESCOPE_PROJECT_ID = "P3JNKAUazZAxRXF4uM7nKzaAiy7Y"
    const val API_BASE_URL = "https://dnd-custom-aid-api.mrsimkin-dev.workers.dev"
}

internal fun initializeHostedDevelopmentAuthentication(context: Context) {
    Descope.setup(
        context.applicationContext,
        projectId = HostedDevelopmentEnvironment.DESCOPE_PROJECT_ID,
    )
}

/**
 * Android platform adapter for the provider-neutral shared hosted-client seam.
 *
 * Descope owns persisted session/refresh-token storage. The shared layer only sees
 * the short-lived session JWT it needs for an Authorization: Bearer request.
 */
internal class DescopeHostedAccessTokenProvider : HostedAccessTokenProvider {
    override suspend fun accessToken(): String? {
        val current = Descope.sessionManager.session ?: return null
        if (current.refreshToken.isExpired) {
            Descope.sessionManager.clearSession()
            return null
        }

        Descope.sessionManager.refreshSessionIfNeeded()
        return Descope.sessionManager.session?.sessionJwt
    }
}

/**
 * Narrow DEV authentication boundary for the first real Android <-> hosted proof.
 *
 * Email OTP is deliberately used here because the DEV Descope project already has
 * that method enabled and verified. This is not a final product-login UX decision.
 */
internal class AndroidHostedAuthController(
    private val apiClient: HostedApiClient = HostedApiClient(
        baseUrl = HostedDevelopmentEnvironment.API_BASE_URL,
        accessTokens = DescopeHostedAccessTokenProvider(),
    ),
) {
    fun hasRememberedSession(): Boolean =
        Descope.sessionManager.session?.refreshToken?.isExpired == false

    suspend fun sendEmailOtp(email: String): String {
        val normalized = email.trim()
        require(normalized.isNotEmpty()) { "Email must not be blank." }
        return Descope.otp.signUpOrIn(DeliveryMethod.Email, normalized)
    }

    suspend fun verifyEmailOtp(email: String, code: String) {
        val normalizedEmail = email.trim()
        val normalizedCode = code.trim()
        require(normalizedEmail.isNotEmpty()) { "Email must not be blank." }
        require(normalizedCode.isNotEmpty()) { "OTP code must not be blank." }

        val response = Descope.otp.verify(
            DeliveryMethod.Email,
            normalizedEmail,
            normalizedCode,
        )
        Descope.sessionManager.manageSession(DescopeSession(response))
    }

    suspend fun currentHostedAccount(): HostedAccount = apiClient.currentAccount()

    suspend fun signOut() {
        val refreshJwt = Descope.sessionManager.session?.refreshJwt
        Descope.sessionManager.clearSession()
        if (refreshJwt != null) {
            runCatching {
                Descope.auth.revokeSessions(RevokeType.CurrentSession, refreshJwt)
            }
        }
    }

    fun close() {
        apiClient.close()
    }
}
