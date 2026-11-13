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

/** What happened when the App de PC tried to reopen the remembered account at start. */
internal enum class DesktopRestoreOutcome {
    /** Nothing remembered: the App asks for the account. */
    NONE,

    /** The remembered account is open again. */
    RESTORED,

    /** The account is remembered but the server could not be reached: work continues locally. */
    OFFLINE,

    /** The remembered account expired or was closed elsewhere: the App asks for it again. */
    EXPIRED,
}

/** Descope refused the remembered login (expired, revoked): it must be asked for again. */
internal class DesktopLoginExpiredException(message: String) : IllegalStateException(message)

/**
 * Desktop-only Descope email-OTP adapter.
 *
 * Shared remains provider-neutral. The session token lives in memory only. Since owner decision
 * D2 (2026-11-13) the refresh token and the typed email are remembered between launches in
 * [vault] (DPAPI on Windows); they are never written to DesktopPreferencesStore or diagnostics.
 */
internal class DesktopHostedAuthController(
    private val projectId: String = DesktopHostedDevelopmentEnvironment.DESCOPE_PROJECT_ID,
    private val descopeBaseUrl: String = DesktopHostedDevelopmentEnvironment.DESCOPE_API_BASE_URL,
    private val httpClient: HttpClient = createHostedHttpClient(),
    private val vault: DesktopCredentialVault = SessionOnlyCredentialVault,
) : HostedAccessTokenProvider {
    private var session: DesktopHostedSession? = null
    private var pendingEmail: String? = null

    /** The email of the open (or remembered) account, shown in the account menu. */
    var accountEmail: String? = null
        private set

    val canRemember: Boolean get() = vault.canRemember

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
        val opened = parseSession(body)
        session = opened
        accountEmail = email
        pendingEmail = null
        phase = DesktopHostedAuthPhase.AUTHENTICATED
        remember(opened)
    }

    /**
     * Reopens the remembered account at start. A network failure keeps the account (the App works
     * locally and renews it on the next call); only a refusal from Descope forgets it.
     */
    suspend fun restore(): DesktopRestoreOutcome {
        val remembered = vault.load() ?: return DesktopRestoreOutcome.NONE
        accountEmail = remembered.email
        val pending = DesktopHostedSession(sessionJwt = "", refreshJwt = remembered.refreshJwt)
        session = pending
        phase = DesktopHostedAuthPhase.AUTHENTICATED
        return try {
            refreshSession(pending)
            DesktopRestoreOutcome.RESTORED
        } catch (expired: DesktopLoginExpiredException) {
            forget()
            DesktopRestoreOutcome.EXPIRED
        } catch (offline: Exception) {
            DesktopRestoreOutcome.OFFLINE
        }
    }

    override suspend fun accessToken(): String? {
        val current = session ?: return null
        if (!jwtExpiresSoon(current.sessionJwt)) {
            return current.sessionJwt
        }

        return try {
            refreshSession(current).sessionJwt
        } catch (expired: DesktopLoginExpiredException) {
            forget()
            null
        } catch (offline: Exception) {
            // No connection: keep the account; the hosted call fails and the App says it is offline.
            null
        }
    }

    suspend fun currentHostedAccount(): HostedAccount = hostedApiClient.currentAccount()

    /**
     * «Cerrar sesión» / «Cambiar usuario»: forgets the account on this computer and, when the
     * server is reachable, closes it at Descope too. Local campaigns and data stay.
     */
    suspend fun signOut() {
        val refreshJwt = session?.refreshJwt
        forget()
        if (refreshJwt != null) {
            runCatching {
                httpClient.post("${descopeBaseUrl.trimEnd('/')}/v1/auth/logout") {
                    bearerAuth("$projectId:$refreshJwt")
                    contentType(ContentType.Application.Json)
                }
            }
        }
    }

    /** Cancels a pending code without touching an open account. */
    fun cancelPendingCode() {
        pendingEmail = null
        if (session == null) phase = DesktopHostedAuthPhase.SIGNED_OUT
    }

    fun close() {
        session = null
        hostedApiClient.close()
        httpClient.close()
    }

    private suspend fun refreshSession(current: DesktopHostedSession): DesktopHostedSession {
        val response = httpClient.post("${descopeBaseUrl.trimEnd('/')}/v1/auth/refresh") {
            bearerAuth("$projectId:${current.refreshJwt}")
            contentType(ContentType.Application.Json)
        }
        val body = response.bodyAsText()
        val status = response.status.value
        if (status == 400 || status == 401 || status == 403) {
            throw DesktopLoginExpiredException("La cuenta recordada ya no es válida. Vuelve a entrar.")
        }
        requireSuccess(status, body, "La sesión alojada no pudo renovarse.")
        val renewed = parseSession(body, fallbackRefreshJwt = current.refreshJwt)
        session = renewed
        if (renewed.refreshJwt != current.refreshJwt) remember(renewed)
        return renewed
    }

    private fun remember(opened: DesktopHostedSession) {
        val email = accountEmail ?: return
        runCatching { vault.save(RememberedLogin(email = email, refreshJwt = opened.refreshJwt)) }
    }

    private fun forget() {
        session = null
        pendingEmail = null
        accountEmail = null
        phase = DesktopHostedAuthPhase.SIGNED_OUT
        runCatching { vault.clear() }
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
