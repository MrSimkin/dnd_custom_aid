package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class HostedCampaignMember(
    val userId: Uuid,
    val displayName: String? = null,
    val role: CampaignRole,
    val status: CampaignMembershipStatus,
)

/*
 * Owner, 2026-11-18 («A: invitar jugadores»): the DM holds one live code per campaign, with its
 * expiry; a player joins with it. The code travels as six letters, and the same six letters make
 * the link and the QR the DM can show.
 */

@Serializable
data class HostedCampaignInvitation(
    val code: String,
    val campaignId: Uuid,
    val campaignName: String,
    val expiresAtEpochSeconds: Long,
) {
    /** The link that carries the same code, for sending it instead of dictating it. */
    fun link(baseUrl: String): String = "${baseUrl.trimEnd('/')}/unirme/$code"
}

@Serializable
data class HostedJoinedCampaign(
    val campaignId: Uuid,
    val campaignName: String,
    val role: CampaignRole,
    val alreadyMember: Boolean = false,
)

@Serializable
private data class CampaignInvitationResponse(val invitation: HostedCampaignInvitation? = null)

@Serializable
private data class CreateInvitationRequest(val daysValid: Int)

@Serializable
private data class RevokeInvitationResponse(val revoked: Boolean)

@Serializable
private data class JoinCampaignResponse(val joined: HostedJoinedCampaign)

@Serializable
enum class HostedCampaignModerationAction {
    KICK,
    BAN,
    LIFT_BAN,
}

data class HostedCampaignMemberRoster(
    val campaignId: Uuid,
    val campaignRevision: Long,
    val members: List<HostedCampaignMember>,
) {
    init {
        require(campaignRevision >= 0) { "Hosted campaign revision must not be negative." }
    }
}

data class HostedCampaignModerationResult(
    val member: HostedCampaignMember,
    val campaignRevision: Long,
    val applied: Boolean,
) {
    init {
        require(campaignRevision >= 0) { "Hosted campaign revision must not be negative." }
    }
}

@Serializable
private data class CampaignMembersResponse(
    val campaignId: Uuid,
    val campaignRevision: Long,
    val members: List<HostedCampaignMember>,
)

@Serializable
private data class ModerateCampaignMemberRequest(
    val action: HostedCampaignModerationAction,
)

@Serializable
private data class ModerateCampaignMemberResponse(
    val member: HostedCampaignMember,
    val campaignRevision: Long,
    val applied: Boolean,
)

/**
 * Provider-neutral native client for the bounded Campaign Administration membership API.
 *
 * Authentication remains a platform concern supplied through HostedAccessTokenProvider. The same
 * client can therefore be consumed later by Desktop once its provider/session edge exists without
 * teaching Shared about Descope or storing provider credentials in domain code.
 */
class HostedCampaignAdministrationClient(
    baseUrl: String,
    private val accessTokens: HostedAccessTokenProvider,
    private val httpClient: HttpClient = createHostedHttpClient(),
) {
    private val apiBaseUrl = normalizeBaseUrl(baseUrl)

    suspend fun members(campaignId: Uuid): HostedCampaignMemberRoster {
        val token = requireAccessToken()
        val response = httpClient.get("$apiBaseUrl/v1/campaigns/$campaignId/members") {
            bearerAuth(token)
        }
        ensureSuccess(response)
        val body = response.body<CampaignMembersResponse>()
        return HostedCampaignMemberRoster(
            campaignId = body.campaignId,
            campaignRevision = body.campaignRevision,
            members = body.members,
        )
    }

    suspend fun moderateMember(
        campaignId: Uuid,
        userId: Uuid,
        action: HostedCampaignModerationAction,
    ): HostedCampaignModerationResult {
        val token = requireAccessToken()
        val response = httpClient.post(
            "$apiBaseUrl/v1/campaigns/$campaignId/members/$userId/moderation",
        ) {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(ModerateCampaignMemberRequest(action))
        }
        ensureSuccess(response)
        val body = response.body<ModerateCampaignMemberResponse>()
        return HostedCampaignModerationResult(
            member = body.member,
            campaignRevision = body.campaignRevision,
            applied = body.applied,
        )
    }

    /** The live invitation of the campaign, or null when there is none. */
    suspend fun invitation(campaignId: Uuid): HostedCampaignInvitation? {
        val response = httpClient.get("$apiBaseUrl/v1/campaigns/$campaignId/invitation") {
            bearerAuth(requireAccessToken())
        }
        ensureSuccess(response)
        return response.body<CampaignInvitationResponse>().invitation
    }

    /** Makes a new code and stops the previous one. */
    suspend fun createInvitation(campaignId: Uuid, daysValid: Int = 7): HostedCampaignInvitation {
        require(daysValid in 1..30) { "Una invitación vale entre 1 y 30 días." }
        val response = httpClient.post("$apiBaseUrl/v1/campaigns/$campaignId/invitation") {
            bearerAuth(requireAccessToken())
            contentType(ContentType.Application.Json)
            setBody(CreateInvitationRequest(daysValid))
        }
        ensureSuccess(response)
        return requireNotNull(response.body<CampaignInvitationResponse>().invitation)
    }

    /** Stops the live code without making a new one. */
    suspend fun revokeInvitation(campaignId: Uuid): Boolean {
        val response = httpClient.delete("$apiBaseUrl/v1/campaigns/$campaignId/invitation") {
            bearerAuth(requireAccessToken())
        }
        ensureSuccess(response)
        return response.body<RevokeInvitationResponse>().revoked
    }

    /** A player joins with the six letters. */
    suspend fun joinWithCode(code: String): HostedJoinedCampaign {
        val normalized = code.trim().uppercase()
        require(Regex("^[A-Z]{6}$").matches(normalized)) { "El código son seis letras." }
        val response = httpClient.post("$apiBaseUrl/v1/invitations/$normalized") {
            bearerAuth(requireAccessToken())
        }
        ensureSuccess(response)
        return response.body<JoinCampaignResponse>().joined
    }

    fun close() {
        httpClient.close()
    }

    private suspend fun requireAccessToken(): String {
        val token = accessTokens.accessToken()?.trim()
        if (token.isNullOrEmpty()) {
            throw HostedAuthenticationUnavailableException()
        }
        return token
    }

    private suspend fun ensureSuccess(response: HttpResponse) {
        val statusCode = response.status.value
        if (statusCode in 200..299) {
            return
        }

        val error = try {
            response.body<HostedApiErrorBody>()
        } catch (_: Exception) {
            HostedApiErrorBody(
                code = if (statusCode >= 500) {
                    HostedApiErrorCode.TRANSIENT_FAILURE
                } else {
                    HostedApiErrorCode.INTERNAL_ERROR
                },
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
