package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedAccessTokenProvider
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedAccount
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiClient
import io.github.mrsimkin.dndcustomaid.shared.hosted.createHostedHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Base64

internal object DesktopHostedDevelopmentEnvironment {
    const val DESCOPE_PROJECT_ID = "P3JNKAUazZAxRXF4uM7nKzaAiy7Y"
    const val DESCOPE_API_BASE_URL = "https://api.descope.com"
    const val HOSTED_API_BASE_URL = "https://dnd-custom-aid-api.mrsimkin-dev.workers.dev"
}

internal data class DesktopHostedSession(
    val sessionJwt: String,
    val refreshJwt: String,
)

internal enum class DesktopHostedAuthPhase {
    SIGNED_OUT,
    OTP_SENT,
    AUTHENTICATED,
}

/**
 * Desktop-only Descope email-OTP adapter.
 *
 * Shared remains provider-neutral. Session and refresh JWTs deliberately remain in memory only;
 * they are never persisted through DesktopPreferencesStore or exposed through diagnostics.
 */
internal class DesktopHostedAuthController(
    private val projectId: String = DesktopHostedDevelopmentEnvironment.DESCOPE_PROJECT_ID,
    private val descopeBaseUrl: String = DesktopHostedDevelopmentEnvironment.DESCOPE_API_BASE_URL,
    private val httpClient: HttpClient = createHostedHttpClient(),
) : HostedAccessTokenProvider {
    private var session: DesktopHostedSession? = null
    private var pendingEmail: String? = null

    private val hostedApiClient = HostedApiClient(
        baseUrl = DesktopHostedDevelopmentEnvironment.HOSTED_API_BASE_URL,
        accessTokens = this,
    )

    var phase: DesktopHostedAuthPhase = DesktopHostedAuthPhase.SIGNED_OUT
        private set

    fun pendingLoginEmail(): String? = pendingEmail

    fun hasSession(): Boolean = session != null

    suspend fun sendEmailOtp(email: String) {
        val normalized = email.trim()
        require(normalized.isNotEmpty()) { "Email must not be blank." }

        val response = httpClient.post("${descopeBaseUrl.trimEnd('/')}/v1/auth/otp/signup-in/email") {
            bearerAuth(projectId)
            contentType(ContentType.Application.Json)
            setBody("{\"loginId\":${jsonString(normalized)}}")
        }
        requireSuccess(response.status.value, response.bodyAsText(), "No se pudo enviar el código de acceso.")
        pendingEmail = normalized
        phase = DesktopHostedAuthPhase.OTP_SENT
    }

    suspend fun verifyEmailOtp(code: String) {
        val email = pendingEmail ?: error("No email OTP challenge is pending.")
        val normalizedCode = code.trim()
        require(normalizedCode.isNotEmpty()) { "OTP code must not be blank." }

        val response = httpClient.post("${descopeBaseUrl.trimEnd('/')}/v1/auth/otp/verify/email") {
            bearerAuth(projectId)
            contentType(ContentType.Application.Json)
            setBody("{\"loginId\":${jsonString(email)},\"code\":${jsonString(normalizedCode)}}")
        }
        val body = response.bodyAsText()
        requireSuccess(response.status.value, body, "No se pudo verificar el código de acceso.")
        session = parseSession(body)
        pendingEmail = null
        phase = DesktopHostedAuthPhase.AUTHENTICATED
    }

    override suspend fun accessToken(): String? {
        val current = session ?: return null
        if (!jwtExpiresSoon(current.sessionJwt)) {
            return current.sessionJwt
        }

        return runCatching { refreshSession(current) }
            .onFailure { clearSession() }
            .getOrNull()
            ?.sessionJwt
    }

    suspend fun currentHostedAccount(): HostedAccount = hostedApiClient.currentAccount()

    fun signOut() {
        clearSession()
    }

    fun close() {
        clearSession()
        hostedApiClient.close()
        httpClient.close()
    }

    private suspend fun refreshSession(current: DesktopHostedSession): DesktopHostedSession {
        val response = httpClient.post("${descopeBaseUrl.trimEnd('/')}/v1/auth/refresh") {
            bearerAuth("$projectId:${current.refreshJwt}")
            contentType(ContentType.Application.Json)
        }
        val body = response.bodyAsText()
        requireSuccess(response.status.value, body, "La sesión alojada expiró y no pudo renovarse.")
        return parseSession(body, fallbackRefreshJwt = current.refreshJwt).also { session = it }
    }

    private fun clearSession() {
        session = null
        pendingEmail = null
        phase = DesktopHostedAuthPhase.SIGNED_OUT
    }
}

internal fun jwtExpiresSoon(jwt: String, nowEpochSeconds: Long = System.currentTimeMillis() / 1000L): Boolean {
    val parts = jwt.split('.')
    if (parts.size < 2) return true
    val payload = runCatching {
        val padded = parts[1] + "=".repeat((4 - parts[1].length % 4) % 4)
        String(Base64.getUrlDecoder().decode(padded), Charsets.UTF_8)
    }.getOrNull() ?: return true
    val expiry = runCatching {
        Json.parseToJsonElement(payload).jsonObject["exp"]?.jsonPrimitive?.content?.toLong()
    }.getOrNull() ?: return true
    return expiry <= nowEpochSeconds + 60L
}

private fun parseSession(body: String, fallbackRefreshJwt: String? = null): DesktopHostedSession {
    val json = runCatching { Json.parseToJsonElement(body).jsonObject }
        .getOrElse { throw IllegalStateException("Descope returned an unreadable authentication response.") }
    val sessionJwt = json.stringValue("sessionJwt")
        ?: throw IllegalStateException("Descope authentication response did not include a session token.")
    val refreshJwt = json.stringValue("refreshJwt") ?: fallbackRefreshJwt
        ?: throw IllegalStateException("Descope authentication response did not include a refresh token.")
    return DesktopHostedSession(sessionJwt = sessionJwt, refreshJwt = refreshJwt)
}

private fun JsonObject.stringValue(name: String): String? =
    get(name)?.jsonPrimitive?.content?.takeIf { it.isNotBlank() }

private fun jsonString(value: String): String = buildString {
    append('"')
    value.forEach { char ->
        when (char) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> append(char)
        }
    }
    append('"')
}

private fun requireSuccess(statusCode: Int, body: String, fallbackMessage: String) {
    if (statusCode in 200..299) return
    val providerMessage = runCatching {
        val json = Json.parseToJsonElement(body).jsonObject
        json.stringValue("errorDescription")
            ?: json.stringValue("errorMessage")
            ?: json.stringValue("message")
    }.getOrNull()
    throw IllegalStateException(providerMessage ?: "$fallbackMessage (HTTP $statusCode)")
}
