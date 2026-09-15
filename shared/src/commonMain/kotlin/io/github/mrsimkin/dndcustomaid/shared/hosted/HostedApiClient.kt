package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

interface HostedAccessTokenProvider {
    suspend fun accessToken(): String?
}

@Serializable
data class HostedAccount(
    val id: Uuid,
    val displayName: String? = null,
)

@Serializable
data class HostedCampaign(
    val id: Uuid,
    val name: String,
    val role: CampaignRole,
    val revision: Long,
) {
    init {
        require(name.isNotBlank()) { "Hosted campaign name must not be blank." }
        require(revision >= 0) { "Hosted campaign revision must not be negative." }
    }
}

@Serializable
data class HostedCampaignMembershipState(
    val campaignId: Uuid,
    val name: String,
    val role: CampaignRole,
    val status: CampaignMembershipStatus,
    val revision: Long,
    val deletedAtEpochSeconds: Long? = null,
) {
    init {
        require(name.isNotBlank()) { "Hosted campaign name must not be blank." }
        require(revision >= 0) { "Hosted campaign revision must not be negative." }
        require(deletedAtEpochSeconds == null || deletedAtEpochSeconds >= 0) {
            "Hosted campaign deletion timestamp must not be negative."
        }
    }
}

@Serializable
enum class HostedApiErrorCode {
    UNAUTHENTICATED,
    FORBIDDEN,
    NOT_FOUND,
    VALIDATION_FAILED,
    CONFLICT_STALE_REVISION,
    CONFLICT_MUTATION_REUSE,
    GONE,
    TRANSIENT_FAILURE,
    INTERNAL_ERROR,
}

@Serializable
data class HostedApiErrorBody(
    val code: HostedApiErrorCode,
    val message: String,
)

class HostedApiException(
    val statusCode: Int,
    val code: HostedApiErrorCode,
    override val message: String,
) : Exception(message) {
    val isTransient: Boolean
        get() = code == HostedApiErrorCode.TRANSIENT_FAILURE || statusCode == 408 || statusCode == 429 || statusCode >= 500
}

class HostedAuthenticationUnavailableException : Exception("No hosted access token is available.")

@Serializable
private data class MeResponse(
    val account: HostedAccount,
)

@Serializable
private data class CampaignsResponse(
    val campaigns: List<HostedCampaign>,
)

@Serializable
private data class CampaignMembershipsResponse(
    val memberships: List<HostedCampaignMembershipState>,
)

@Serializable
private data class CreateCampaignRequest(
    val mutationId: Uuid,
    val campaignId: Uuid,
    val name: String,
)

@Serializable
private data class CreateCampaignResponse(
    val campaign: HostedCampaign,
    val created: Boolean,
)

data class HostedCampaignCreation(
    val campaign: HostedCampaign,
    val created: Boolean,
)

class HostedApiClient(
    baseUrl: String,
    private val accessTokens: HostedAccessTokenProvider,
    private val httpClient: HttpClient = createHostedHttpClient(),
) {
    private val apiBaseUrl = normalizeBaseUrl(baseUrl)

    suspend fun currentAccount(): HostedAccount = authenticatedGet<MeResponse>("/v1/me").account

    suspend fun campaigns(): List<HostedCampaign> = authenticatedGet<CampaignsResponse>("/v1/campaigns").campaigns

    suspend fun campaignMemberships(): List<HostedCampaignMembershipState> =
        authenticatedGet<CampaignMembershipsResponse>("/v1/campaign-memberships").memberships

    suspend fun createCampaign(
        mutationId: Uuid,
        campaignId: Uuid,
        name: String,
    ): HostedCampaignCreation {
        val normalizedName = name.trim()
        require(normalizedName.isNotEmpty()) { "Campaign name must not be blank." }

        val token = requireAccessToken()
        val response = httpClient.post("$apiBaseUrl/v1/campaigns") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(
                CreateCampaignRequest(
                    mutationId = mutationId,
                    campaignId = campaignId,
                    name = normalizedName,
                ),
            )
        }
        ensureSuccess(response.status.value, response)
        val body = response.body<CreateCampaignResponse>()
        return HostedCampaignCreation(body.campaign, body.created)
    }

    fun close() {
        httpClient.close()
    }

    private suspend inline fun <reified T> authenticatedGet(path: String): T {
        val token = requireAccessToken()
        val response = httpClient.get("$apiBaseUrl$path") {
            bearerAuth(token)
        }
        ensureSuccess(response.status.value, response)
        return response.body()
    }

    private suspend fun requireAccessToken(): String {
        val token = accessTokens.accessToken()?.trim()
        if (token.isNullOrEmpty()) {
            throw HostedAuthenticationUnavailableException()
        }
        return token
    }

    private suspend fun ensureSuccess(statusCode: Int, response: io.ktor.client.statement.HttpResponse) {
        if (statusCode in 200..299) {
            return
        }

        val error = try {
            response.body<HostedApiErrorBody>()
        } catch (_: Exception) {
            HostedApiErrorBody(
                code = if (statusCode >= 500) HostedApiErrorCode.TRANSIENT_FAILURE else HostedApiErrorCode.INTERNAL_ERROR,
                message = "Hosted API request failed with HTTP $statusCode.",
            )
        }
        throw HostedApiException(statusCode, error.code, error.message)
    }

    private fun normalizeBaseUrl(value: String): String {
        val normalized = value.trim().trimEnd('/')
        require(normalized.startsWith("https://") || normalized.startsWith("http://")) {
            "Hosted API base URL must use http or https."
        }
        return normalized
    }
}

expect fun createHostedHttpClient(): HttpClient
