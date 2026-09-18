package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.spine.PcAuthority
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlin.uuid.Uuid

@Serializable
private data class HostedPcAuthorityState(
    val pcId: Uuid,
    val campaignId: Uuid,
    val ownerUserId: Uuid? = null,
    val controllerUserId: Uuid? = null,
)

@Serializable
private data class PutPcAuthorityResponse(
    val authority: HostedPcAuthorityState,
    val applied: Boolean,
)

data class HostedPcAuthorityUpdate(
    val campaignId: Uuid,
    val authority: PcAuthority,
    val applied: Boolean,
)

/**
 * Provider-neutral client for explicit DM PC ownership/controller administration.
 *
 * Authority is intentionally independent from the PC snapshot revision. The backend validates
 * active campaign membership for every non-null owner/controller target and returns the canonical
 * resulting authority state used for local convergence.
 */
class HostedPcAuthorityAdministrationClient(
    baseUrl: String,
    private val accessTokens: HostedAccessTokenProvider,
    private val httpClient: HttpClient = createHostedHttpClient(),
) {
    private val apiBaseUrl = normalizeBaseUrl(baseUrl)

    suspend fun setAuthority(
        campaignId: Uuid,
        pcId: Uuid,
        ownerUserId: Uuid?,
        controllerUserId: Uuid?,
    ): HostedPcAuthorityUpdate {
        val token = requireAccessToken()
        val response = httpClient.put("$apiBaseUrl/v1/pcs/$pcId/authority") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            // hostedWireJson intentionally omits nullable null properties globally.
            // Authority uses full replacement semantics, so explicit JsonNull values are required
            // to distinguish "unassign" from a malformed omitted field.
            setBody(
                buildJsonObject {
                    put("campaignId", JsonPrimitive(campaignId.toString()))
                    put("ownerUserId", ownerUserId?.let { JsonPrimitive(it.toString()) } ?: JsonNull)
                    put(
                        "controllerUserId",
                        controllerUserId?.let { JsonPrimitive(it.toString()) } ?: JsonNull,
                    )
                },
            )
        }
        ensureSuccess(response)
        val body = response.body<PutPcAuthorityResponse>()
        require(body.authority.pcId == pcId) {
            "Hosted PC authority response changed PC identity."
        }
        require(body.authority.campaignId == campaignId) {
            "Hosted PC authority response changed campaign identity."
        }
        return HostedPcAuthorityUpdate(
            campaignId = body.authority.campaignId,
            authority = PcAuthority(
                characterId = body.authority.pcId,
                ownerAccountId = body.authority.ownerUserId,
                controllerAccountId = body.authority.controllerUserId,
            ),
            applied = body.applied,
        )
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
