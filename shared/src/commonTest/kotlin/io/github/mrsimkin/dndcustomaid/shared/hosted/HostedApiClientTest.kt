package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class HostedApiClientTest {
    @Test
    fun currentAccountUsesBearerTokenAndParsesIdentity() = runBlocking {
        val accountId = Uuid.random()
        val client = hostedClient { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("/v1/me", request.url.encodedPath)
            assertEquals("Bearer token-123", request.headers[HttpHeaders.Authorization])
            respondJson(
                """{"account":{"id":"$accountId","displayName":"Simkin"}}""",
            )
        }
        val api = HostedApiClient(
            baseUrl = "https://example.test/",
            accessTokens = tokenProvider(" token-123 "),
            httpClient = client,
        )

        val account = api.currentAccount()

        assertEquals(accountId, account.id)
        assertEquals("Simkin", account.displayName)
        api.close()
    }

    @Test
    fun campaignsParsesRoleAndRevision() = runBlocking {
        val campaignId = Uuid.random()
        val api = HostedApiClient(
            baseUrl = "https://example.test",
            accessTokens = tokenProvider("token"),
            httpClient = hostedClient { request ->
                assertEquals("/v1/campaigns", request.url.encodedPath)
                respondJson(
                    """{"campaigns":[{"id":"$campaignId","name":"Terramore","role":"DM","revision":7}]}""",
                )
            },
        )

        val campaigns = api.campaigns()

        assertEquals(1, campaigns.size)
        assertEquals(campaignId, campaigns.single().id)
        assertEquals(CampaignRole.DM, campaigns.single().role)
        assertEquals(7, campaigns.single().revision)
        api.close()
    }

    @Test
    fun createCampaignPreservesRetryIdentitiesAndTrimsName() = runBlocking {
        val mutationId = Uuid.random()
        val campaignId = Uuid.random()
        var bodyText = ""
        val api = HostedApiClient(
            baseUrl = "https://example.test",
            accessTokens = tokenProvider("token"),
            httpClient = hostedClient { request ->
                assertEquals(HttpMethod.Post, request.method)
                assertEquals("/v1/campaigns", request.url.encodedPath)
                assertEquals(ContentType.Application.Json.withCharset(Charsets.UTF_8), request.body.contentType)
                bodyText = request.body.toByteArray().decodeToString()
                respondJson(
                    """{"campaign":{"id":"$campaignId","name":"Terramore","role":"DM","revision":0},"created":true}""",
                    HttpStatusCode.Created,
                )
            },
        )

        val created = api.createCampaign(
            mutationId = mutationId,
            campaignId = campaignId,
            name = "  Terramore  ",
        )

        assertTrue(created.created)
        assertEquals(campaignId, created.campaign.id)
        assertTrue(bodyText.contains("\"mutationId\":\"$mutationId\""))
        assertTrue(bodyText.contains("\"campaignId\":\"$campaignId\""))
        assertTrue(bodyText.contains("\"name\":\"Terramore\""))
        api.close()
    }

    @Test
    fun mutationReuseConflictIsTypedAndNotTransient() = runBlocking {
        val api = HostedApiClient(
            baseUrl = "https://example.test",
            accessTokens = tokenProvider("token"),
            httpClient = hostedClient {
                respondJson(
                    """{"code":"CONFLICT_MUTATION_REUSE","message":"Mutation reused."}""",
                    HttpStatusCode.Conflict,
                )
            },
        )

        val error = assertFailsWith<HostedApiException> {
            api.createCampaign(Uuid.random(), Uuid.random(), "Terramore")
        }

        assertEquals(409, error.statusCode)
        assertEquals(HostedApiErrorCode.CONFLICT_MUTATION_REUSE, error.code)
        assertFalse(error.isTransient)
        api.close()
    }

    @Test
    fun serviceUnavailableIsClassifiedAsTransient() = runBlocking {
        val api = HostedApiClient(
            baseUrl = "https://example.test",
            accessTokens = tokenProvider("token"),
            httpClient = hostedClient {
                respondJson(
                    """{"code":"TRANSIENT_FAILURE","message":"Try later."}""",
                    HttpStatusCode.ServiceUnavailable,
                )
            },
        )

        val error = assertFailsWith<HostedApiException> { api.campaigns() }

        assertTrue(error.isTransient)
        assertEquals(HostedApiErrorCode.TRANSIENT_FAILURE, error.code)
        api.close()
    }

    @Test
    fun missingTokenFailsBeforeNetworkRequest() = runBlocking {
        var requests = 0
        val api = HostedApiClient(
            baseUrl = "https://example.test",
            accessTokens = tokenProvider("   "),
            httpClient = hostedClient {
                requests += 1
                respondJson("{}")
            },
        )

        assertFailsWith<HostedAuthenticationUnavailableException> { api.currentAccount() }
        assertEquals(0, requests)
        api.close()
    }

    @Test
    fun invalidBaseUrlIsRejectedAtConstruction() {
        assertFailsWith<IllegalArgumentException> {
            HostedApiClient(
                baseUrl = "example.test",
                accessTokens = tokenProvider("token"),
                httpClient = hostedClient { respondJson("{}") },
            )
        }
    }
}

private fun tokenProvider(value: String?): HostedAccessTokenProvider = object : HostedAccessTokenProvider {
    override suspend fun accessToken(): String? = value
}

private fun hostedClient(handler: io.ktor.client.engine.mock.MockRequestHandler): HttpClient = HttpClient(MockEngine(handler)) {
    expectSuccess = false
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            },
        )
    }
}

private fun io.ktor.client.engine.mock.MockRequestHandleScope.respondJson(
    body: String,
    status: HttpStatusCode = HttpStatusCode.OK,
) = respond(
    content = body,
    status = status,
    headers = headersOf(HttpHeaders.ContentType, "application/json; charset=utf-8"),
)
