package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
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
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class HostedCampaignAdministrationClientTest {
    @Test
    fun membersUsesBearerTokenAndParsesLifecycleRoster() = runBlocking {
        val campaignId = Uuid.random()
        val dmId = Uuid.random()
        val playerId = Uuid.random()
        val api = HostedCampaignAdministrationClient(
            baseUrl = "https://example.test/",
            accessTokens = adminTokenProvider(" token-123 "),
            httpClient = adminHttpClient { request ->
                assertEquals(HttpMethod.Get, request.method)
                assertEquals("/v1/campaigns/$campaignId/members", request.url.encodedPath)
                assertEquals("Bearer token-123", request.headers[HttpHeaders.Authorization])
                adminRespondJson(
                    """{"campaignId":"$campaignId","campaignRevision":8,"members":[{"userId":"$dmId","displayName":"DM","role":"DM","status":"ACTIVE"},{"userId":"$playerId","displayName":"Player","role":"PLAYER","status":"KICKED"}]}""",
                )
            },
        )

        val roster = api.members(campaignId)

        assertEquals(campaignId, roster.campaignId)
        assertEquals(8, roster.campaignRevision)
        assertEquals(2, roster.members.size)
        assertEquals(CampaignRole.DM, roster.members[0].role)
        assertEquals(CampaignMembershipStatus.ACTIVE, roster.members[0].status)
        assertEquals(playerId, roster.members[1].userId)
        assertEquals(CampaignMembershipStatus.KICKED, roster.members[1].status)
        api.close()
    }

    @Test
    fun moderateMemberEncodesExplicitActionAndParsesResult() = runBlocking {
        val campaignId = Uuid.random()
        val playerId = Uuid.random()
        var requestBody = ""
        val api = HostedCampaignAdministrationClient(
            baseUrl = "https://example.test",
            accessTokens = adminTokenProvider("token"),
            httpClient = adminHttpClient { request ->
                assertEquals(HttpMethod.Post, request.method)
                assertEquals(
                    "/v1/campaigns/$campaignId/members/$playerId/moderation",
                    request.url.encodedPath,
                )
                assertEquals("Bearer token", request.headers[HttpHeaders.Authorization])
                requestBody = assertIs<TextContent>(request.body).text
                adminRespondJson(
                    """{"member":{"userId":"$playerId","displayName":"Player","role":"PLAYER","status":"KICKED"},"campaignRevision":9,"applied":true}""",
                )
            },
        )

        val result = api.moderateMember(
            campaignId = campaignId,
            userId = playerId,
            action = HostedCampaignModerationAction.LIFT_BAN,
        )

        assertTrue(requestBody.contains("\"action\":\"LIFT_BAN\""))
        assertEquals(playerId, result.member.userId)
        assertEquals(CampaignMembershipStatus.KICKED, result.member.status)
        assertEquals(9, result.campaignRevision)
        assertTrue(result.applied)
        api.close()
    }

    @Test
    fun forbiddenAdministrationResponseIsTypedAndNonTransient() = runBlocking {
        val api = HostedCampaignAdministrationClient(
            baseUrl = "https://example.test",
            accessTokens = adminTokenProvider("token"),
            httpClient = adminHttpClient {
                adminRespondJson(
                    """{"code":"FORBIDDEN","message":"The authenticated account is not allowed to perform this operation."}""",
                    HttpStatusCode.Forbidden,
                )
            },
        )

        val error = assertFailsWith<HostedApiException> {
            api.members(Uuid.random())
        }

        assertEquals(403, error.statusCode)
        assertEquals(HostedApiErrorCode.FORBIDDEN, error.code)
        assertFalse(error.isTransient)
        api.close()
    }

    @Test
    fun missingTokenFailsBeforeAdministrationNetworkRequest() = runBlocking {
        var requests = 0
        val api = HostedCampaignAdministrationClient(
            baseUrl = "https://example.test",
            accessTokens = adminTokenProvider("   "),
            httpClient = adminHttpClient {
                requests += 1
                adminRespondJson("{}")
            },
        )

        assertFailsWith<HostedAuthenticationUnavailableException> {
            api.members(Uuid.random())
        }
        assertEquals(0, requests)
        api.close()
    }
}

private fun adminTokenProvider(value: String?): HostedAccessTokenProvider = object : HostedAccessTokenProvider {
    override suspend fun accessToken(): String? = value
}

private fun adminHttpClient(handler: io.ktor.client.engine.mock.MockRequestHandler): HttpClient =
    HttpClient(MockEngine(handler)) {
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

private fun io.ktor.client.engine.mock.MockRequestHandleScope.adminRespondJson(
    body: String,
    status: HttpStatusCode = HttpStatusCode.OK,
) = respond(
    content = body,
    status = status,
    headers = headersOf(HttpHeaders.ContentType, "application/json; charset=utf-8"),
)
