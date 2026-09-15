package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.coroutines.cancellation.CancellationException
import kotlin.uuid.Uuid

data class QueuedHostedCampaignCreation(
    val campaign: Campaign,
    val mutation: HostedOutboxMutation,
)

class HostedCampaignCreationService(
    private val database: AppDatabase,
    private val campaigns: CampaignRepository = CampaignRepository(database),
    private val outbox: HostedOutboxRepository = HostedOutboxRepository(database),
) {
    fun createCampaign(
        rawName: String,
        createdAtEpochSeconds: Long,
        mutationId: Uuid = Uuid.random(),
    ): QueuedHostedCampaignCreation {
        require(createdAtEpochSeconds >= 0) { "Creation timestamp must not be negative." }

        return database.transactionWithResult {
            val campaign = campaigns.createCampaign(rawName)
            val mutation = outbox.enqueueCampaignCreation(
                campaignId = campaign.id,
                rawName = campaign.name,
                createdAtEpochSeconds = createdAtEpochSeconds,
                mutationId = mutationId,
            )
            QueuedHostedCampaignCreation(
                campaign = campaign,
                mutation = mutation,
            )
        }
    }
}

data class HostedOutboxDeliveryReport(
    val selected: Int,
    val acknowledged: Int,
    val retryableFailures: Int,
    val blockedFailures: Int,
) {
    init {
        require(selected >= 0)
        require(acknowledged >= 0)
        require(retryableFailures >= 0)
        require(blockedFailures >= 0)
        require(acknowledged + retryableFailures + blockedFailures == selected) {
            "Every selected hosted mutation must have one delivery outcome."
        }
    }
}

class HostedOutboxDeliveryService(
    private val outbox: HostedOutboxRepository,
    private val campaignCreator: suspend (Uuid, Uuid, String) -> HostedCampaignCreation,
) {
    constructor(
        outbox: HostedOutboxRepository,
        api: HostedApiClient,
    ) : this(
        outbox = outbox,
        campaignCreator = api::createCampaign,
    )

    suspend fun deliverReady(
        attemptedAtEpochSeconds: Long,
        limit: Long = 50,
    ): HostedOutboxDeliveryReport {
        require(attemptedAtEpochSeconds >= 0) { "Attempt timestamp must not be negative." }
        require(limit > 0) { "Delivery limit must be positive." }

        val selected = outbox.readyMutations(limit)
        var acknowledged = 0
        var retryableFailures = 0
        var blockedFailures = 0

        for (mutation in selected) {
            val payload = try {
                when (mutation.type) {
                    HostedMutationType.CAMPAIGN_CREATE -> outbox.campaignCreationPayload(mutation)
                }
            } catch (error: Exception) {
                outbox.recordFailure(
                    mutationId = mutation.mutationId,
                    attemptedAtEpochSeconds = attemptedAtEpochSeconds,
                    retryable = false,
                    errorCode = "LOCAL_MUTATION_INVALID",
                    errorMessage = error.message,
                )
                blockedFailures += 1
                continue
            }

            try {
                val creation = campaignCreator(
                    payload.mutationId,
                    payload.campaignId,
                    payload.name,
                )
                if (creation.campaign.id != payload.campaignId) {
                    outbox.recordFailure(
                        mutationId = mutation.mutationId,
                        attemptedAtEpochSeconds = attemptedAtEpochSeconds,
                        retryable = false,
                        errorCode = "HOSTED_RESPONSE_IDENTITY_MISMATCH",
                        errorMessage = "Hosted campaign identity did not match the queued campaign.",
                    )
                    blockedFailures += 1
                    continue
                }

                // Both a fresh create and an idempotent replay confirm that this exact mutation reached the server.
                outbox.acknowledge(mutation.mutationId)
                acknowledged += 1
            } catch (error: CancellationException) {
                throw error
            } catch (error: HostedAuthenticationUnavailableException) {
                outbox.recordFailure(
                    mutationId = mutation.mutationId,
                    attemptedAtEpochSeconds = attemptedAtEpochSeconds,
                    retryable = true,
                    errorCode = "AUTHENTICATION_UNAVAILABLE",
                    errorMessage = error.message,
                )
                retryableFailures += 1
            } catch (error: HostedApiException) {
                outbox.recordFailure(
                    mutationId = mutation.mutationId,
                    attemptedAtEpochSeconds = attemptedAtEpochSeconds,
                    retryable = error.isTransient,
                    errorCode = error.code.name,
                    errorMessage = error.message,
                )
                if (error.isTransient) {
                    retryableFailures += 1
                } else {
                    blockedFailures += 1
                }
            } catch (error: Exception) {
                // Transport failures outside a typed HTTP response (for example, offline/DNS/socket failures)
                // must preserve the local mutation for a later retry.
                outbox.recordFailure(
                    mutationId = mutation.mutationId,
                    attemptedAtEpochSeconds = attemptedAtEpochSeconds,
                    retryable = true,
                    errorCode = "TRANSPORT_FAILURE",
                    errorMessage = error.message,
                )
                retryableFailures += 1
            }
        }

        return HostedOutboxDeliveryReport(
            selected = selected.size,
            acknowledged = acknowledged,
            retryableFailures = retryableFailures,
            blockedFailures = blockedFailures,
        )
    }
}
