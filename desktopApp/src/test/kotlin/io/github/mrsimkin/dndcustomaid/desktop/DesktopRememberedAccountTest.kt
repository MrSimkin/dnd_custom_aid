package io.github.mrsimkin.dndcustomaid.desktop

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.Base64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Owner decision D2 (2026-11-13): the App de PC asks for the account once and remembers it. */
class DesktopRememberedAccountTest {
    private fun jwt(expEpochSeconds: Long): String {
        val encoder = Base64.getUrlEncoder().withoutPadding()
        val header = encoder.encodeToString("{\"alg\":\"none\"}".toByteArray())
        val payload = encoder.encodeToString("{\"exp\":$expEpochSeconds}".toByteArray())
        return "$header.$payload.firma"
    }

    private val future = System.currentTimeMillis() / 1000 + 3600

    private fun client(handler: suspend (String) -> Triple<HttpStatusCode, String, Boolean>): HttpClient =
        HttpClient(MockEngine { request ->
            val (status, body, fail) = handler(request.url.encodedPath)
            if (fail) throw IOException("sin red")
            if (status.value in 200..299) {
                respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
            } else {
                respondError(status, body)
            }
        })

    @Test
    fun verifiedCodeIsRememberedAndReopensAtNextStart() = runBlocking {
        val vault = InMemoryCredentialVault()
        val firstRun = DesktopHostedAuthController(
            httpClient = client { path ->
                when {
                    path.endsWith("/otp/signup-in/email") -> Triple(HttpStatusCode.OK, "{}", false)
                    else -> Triple(HttpStatusCode.OK, "{\"sessionJwt\":\"${jwt(future)}\",\"refreshJwt\":\"refresco-1\"}", false)
                }
            },
            vault = vault,
        )
        firstRun.sendEmailOtp("dm@example.test")
        firstRun.verifyEmailOtp("123456")
        assertEquals(RememberedLogin("dm@example.test", "refresco-1"), vault.stored)

        val secondRun = DesktopHostedAuthController(
            httpClient = client { Triple(HttpStatusCode.OK, "{\"sessionJwt\":\"${jwt(future)}\"}", false) },
            vault = vault,
        )
        assertEquals(DesktopRestoreOutcome.RESTORED, secondRun.restore())
        assertEquals("dm@example.test", secondRun.accountEmail)
        assertEquals(DesktopHostedAuthPhase.AUTHENTICATED, secondRun.phase)
    }

    @Test
    fun withoutConnectionTheAccountStaysRemembered() = runBlocking {
        val vault = InMemoryCredentialVault(RememberedLogin("dm@example.test", "refresco-1"))
        val auth = DesktopHostedAuthController(
            httpClient = client { Triple(HttpStatusCode.OK, "", true) },
            vault = vault,
        )
        assertEquals(DesktopRestoreOutcome.OFFLINE, auth.restore())
        assertEquals("dm@example.test", auth.accountEmail)
        assertNull(auth.accessToken())
        assertEquals(RememberedLogin("dm@example.test", "refresco-1"), vault.stored)
    }

    @Test
    fun anExpiredAccountIsForgottenAndAskedAgain() = runBlocking {
        val vault = InMemoryCredentialVault(RememberedLogin("dm@example.test", "vencido"))
        val auth = DesktopHostedAuthController(
            httpClient = client { Triple(HttpStatusCode.Unauthorized, "{\"errorDescription\":\"expired\"}", false) },
            vault = vault,
        )
        assertEquals(DesktopRestoreOutcome.EXPIRED, auth.restore())
        assertNull(vault.stored)
        assertNull(auth.accountEmail)
        assertEquals(DesktopHostedAuthPhase.SIGNED_OUT, auth.phase)
    }

    @Test
    fun signOutForgetsTheAccountOnThisComputer() = runBlocking {
        val vault = InMemoryCredentialVault(RememberedLogin("dm@example.test", "refresco-1"))
        val auth = DesktopHostedAuthController(
            httpClient = client { Triple(HttpStatusCode.OK, "{\"sessionJwt\":\"${jwt(future)}\"}", false) },
            vault = vault,
        )
        auth.restore()
        auth.signOut()
        assertNull(vault.stored)
        assertNull(auth.accountEmail)
        assertTrue(auth.phase == DesktopHostedAuthPhase.SIGNED_OUT)
    }

    @Test
    fun agoReadsLikeTheTablet() {
        assertEquals("hace un momento", ago(5))
        assertEquals("hace 3 min", ago(190))
        assertEquals("hace 2 h", ago(7300))
    }
}
