package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class HostedPcAuthorityAdministrationClientTest {
    @Test
    fun setAuthoritySendsFullReplacementAndParsesCanonicalResult() = runBlocking {
        val campaignId = Uuid.random()
        val pcId = Uuid.random()
        val ownerId = Uuid.random()
        val controllerId = Uuid.random()
        var requestBody = ""

        val client = HostedPcAuthorityAdministrationClient(
            baseUrl = "https://example.test/",
            accessTokens = authorityTokenProvider(" token-123 "),
            httpClient = authorityHttpClient { request ->
                assertEquals(HttpMethod.Put, request.method)
                assertEquals("/v1/pcs/$pcId/authority", request.url.encodedPath)
                assertEquals("Bearer token-123", request.headers[HttpHeaders.Authorization])
                requestBody = assertIs<TextContent>(request.body).text
                authorityRespondJson(
                    """{"authority":{"pcId":"$pcId","campaignId":"$campaignId","ownerUserId":"$ownerId","controllerUserId":"$controllerId"},"applied":true}""",
                )
            },
        )

        val result = client.setAuthority(
            campaignId = campaignId,
            pcId = pcId,
            ownerUserId = ownerId,
            controllerUserId = controllerId,
        )

        assertTrue(requestBody.contains("\"campaignId\":\"$campaignId\""))
        assertTrue(requestBody.contains("\"ownerUserId\":\"$ownerId\""))
        assertTrue(requestBody.contains("\"controllerUserId\":\"$controllerId\""))
        assertEquals(pcId, result.authority.characterId)
        assertEquals(ownerId, result.authority.ownerAccountId)
        assertEquals(controllerId, result.authority.controllerAccountId)
        assertTrue(result.applied)
        client.close()
    }

    @Test
    fun setAuthoritySendsExplicitNullsForUnassignment() = runBlocking {
        val campaignId = Uuid.random()
        val pcId = Uuid.random()
        var requestBody = ""

        val client = HostedPcAuthorityAdministrationClient(
            baseUrl = "https://example.test",
            accessTokens = authorityTokenProvider("token"),
            httpClient = authorityHttpClient { request ->
                requestBody = assertIs<TextContent>(request.body).text
                authorityRespondJson(
                    """{"authority":{"pcId":"$pcId","campaignId":"$campaignId","ownerUserId":null,"controllerUserId":null},"applied":false}""",
                )
            },
        )

        val result = client.setAuthority(campaignId, pcId, null, null)

        assertTrue(requestBody.contains("\"ownerUserId\":null"))
        assertTrue(requestBody.contains("\"controllerUserId\":null"))
        assertNull(result.authority.ownerAccountId)
        assertNull(result.authority.controllerAccountId)
        assertFalse(result.applied)
        client.close()
    }

    @Test
    fun mismatchedHostedIdentityIsRejected() = runBlocking {
        val campaignId = Uuid.random()
        val pcId = Uuid.random()
        val otherPcId = Uuid.random()
        val client = HostedPcAuthorityAdministrationClient(
            baseUrl = "https://example.test",
            accessTokens = authorityTokenProvider("token"),
            httpClient = authorityHttpClient {
                authorityRespondJson(
                    """{"authority":{"pcId":"$otherPcId","campaignId":"$campaignId","ownerUserId":null,"controllerUserId":null},"applied":true}""",
                )
            },
        )

        assertFailsWith<IllegalArgumentException> {
            client.setAuthority(campaignId, pcId, null, null)
        }
        client.close()
    }

    @Test
    fun forbiddenAuthorityResponseIsTypedAndNonTransient() = runBlocking {
        val client = HostedPcAuthorityAdministrationClient(
            baseUrl = "https://example.test",
            accessTokens = authorityTokenProvider("token"),
            httpClient = authorityHttpClient {
                authorityRespondJson(
                    """{"code":"FORBIDDEN","message":"The authenticated account is not allowed to perform this operation."}""",
                    HttpStatusCode.Forbidden,
                )
            },
        )

        val error = assertFailsWith<HostedApiException> {
            client.setAuthority(Uuid.random(), Uuid.random(), null, null)
        }

        assertEquals(HostedApiErrorCode.FORBIDDEN, error.code)
        assertFalse(error.isTransient)
        client.close()
    }

    @Test
    fun missingTokenFailsBeforeAuthorityNetworkRequest() = runBlocking {
        var requests = 0
        val client = HostedPcAuthorityAdministrationClient(
            baseUrl = "https://example.test",
            accessTokens = authorityTokenProvider("   "),
            httpClient = authorityHttpClient {
                requests += 1
                authorityRespondJson("{}")
            },
        )

        assertFailsWith<HostedAuthenticationUnavailableException> {
            client.setAuthority(Uuid.random(), Uuid.random(), null, null)
        }
        assertEquals(0, requests)
        client.close()
    }
}

private fun authorityTokenProvider(value: String?): HostedAccessTokenProvider = object : HostedAccessTokenProvider {
    override suspend fun accessToken(): String? = value
}

private fun authorityHttpClient(handler: io.ktor.client.engine.mock.MockRequestHandler): HttpClient =
    HttpClient(MockEngine(handler)) {
        expectSuccess = false
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = true
                },
            )
        }
    }

private fun io.ktor.client.engine.mock.MockRequestHandleScope.authorityRespondJson(
    body: String,
    status: HttpStatusCode = HttpStatusCode.OK,
) = respond(
    content = body,
    status = status,
    headers = headersOf(HttpHeaders.ContentType, "application/json; charset=utf-8"),
)
