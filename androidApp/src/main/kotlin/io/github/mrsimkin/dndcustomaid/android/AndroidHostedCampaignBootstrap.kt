package io.github.mrsimkin.dndcustomaid.android

import com.descope.Descope
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiClient
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiErrorCode
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiException
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedAuthenticationUnavailableException
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignBootstrapService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignCreationService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedOutboxDeliveryService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedOutboxRepository
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedRetryState
import kotlinx.coroutines.CancellationException
import kotlin.uuid.Uuid

internal enum class AndroidHostedMutationState {
    ACKNOWLEDGED,
    READY,
    BLOCKED,
}

internal data class AndroidQueuedHostedCampaignCreation(
    val campaign: Campaign,
    val mutationId: Uuid,
)

internal sealed interface AndroidHostedCampaignBootstrapOutcome {
    data class Success(
        val hostedCampaignCount: Int,
        val appliedCampaignCount: Int,
        val conflictCount: Int,
        val acknowledgedMutationCount: Int,
        val retryableMutationCount: Int,
        val blockedMutationCount: Int,
    ) : AndroidHostedCampaignBootstrapOutcome

    data object NoRememberedSession : AndroidHostedCampaignBootstrapOutcome

    data class Failure(
        val message: String,
    ) : AndroidHostedCampaignBootstrapOutcome
}

/**
 * Android platform adapter for the existing shared hosted campaign services.
 *
 * Reconciliation, durable outbox state and mutation delivery semantics stay in Shared. This class
 * only supplies the real DEV API/token edge, exposes local-first campaign creation to the Android
 * UI and translates expected hosted failures into bounded owner-facing messages.
 */
internal class AndroidHostedCampaignBootstrapController(
    database: AppDatabase,
    private val apiClient: HostedApiClient = HostedApiClient(
        baseUrl = HostedDevelopmentEnvironment.API_BASE_URL,
        accessTokens = DescopeHostedAccessTokenProvider(),
    ),
) {
    private val outbox = HostedOutboxRepository(database)
    private val campaignCreation = HostedCampaignCreationService(
        database = database,
        outbox = outbox,
    )
    private val delivery = HostedOutboxDeliveryService(
        outbox = outbox,
        api = apiClient,
    )
    private val bootstrap = HostedCampaignBootstrapService(
        database = database,
        api = apiClient,
    )

    fun hasRememberedSession(): Boolean =
        Descope.sessionManager.session?.refreshToken?.isExpired == false

    fun pendingMutationCount(): Int = outbox.allMutations().size

    fun mutationState(mutationId: Uuid): AndroidHostedMutationState {
        val mutation = outbox.mutation(mutationId)
            ?: return AndroidHostedMutationState.ACKNOWLEDGED

        return when (mutation.retryState) {
            HostedRetryState.READY -> AndroidHostedMutationState.READY
            HostedRetryState.BLOCKED -> AndroidHostedMutationState.BLOCKED
        }
    }

    fun createCampaignLocally(rawName: String): AndroidQueuedHostedCampaignCreation {
        val queued = campaignCreation.createCampaign(
            rawName = rawName,
            createdAtEpochSeconds = currentEpochSeconds(),
        )
        return AndroidQueuedHostedCampaignCreation(
            campaign = queued.campaign,
            mutationId = queued.mutation.mutationId,
        )
    }

    /**
     * Flushes ready local mutations first, then reads authoritative hosted campaign membership state.
     *
     * If delivery cannot complete, the shared outbox records retry/block state before the hosted read
     * is attempted. Local campaign creation therefore remains durable even when this method returns a
     * failure because the network or provider is unavailable.
     */
    suspend fun refresh(): AndroidHostedCampaignBootstrapOutcome {
        if (!hasRememberedSession()) {
            return AndroidHostedCampaignBootstrapOutcome.NoRememberedSession
        }

        return try {
            val deliveryReport = delivery.deliverReady(
                attemptedAtEpochSeconds = currentEpochSeconds(),
            )
            val result = bootstrap.refresh()
            AndroidHostedCampaignBootstrapOutcome.Success(
                hostedCampaignCount = result.hostedCampaignCount,
                appliedCampaignCount = result.appliedCampaignIds.size,
                conflictCount = result.conflicts.size,
                acknowledgedMutationCount = deliveryReport.acknowledged,
                retryableMutationCount = deliveryReport.retryableFailures,
                blockedMutationCount = deliveryReport.blockedFailures,
            )
        } catch (_: HostedAuthenticationUnavailableException) {
            AndroidHostedCampaignBootstrapOutcome.NoRememberedSession
        } catch (error: HostedApiException) {
            AndroidHostedCampaignBootstrapOutcome.Failure(
                message = when (error.code) {
                    HostedApiErrorCode.UNAUTHENTICATED ->
                        "La sesión hospedada ya no es válida. Vuelve a autenticarte y reintenta."
                    HostedApiErrorCode.FORBIDDEN ->
                        "La cuenta autenticada no tiene acceso a esta información hospedada."
                    else ->
                        "No se pudo sincronizar con el servidor. Los cambios locales se conservaron para reintentar."
                },
            )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            AndroidHostedCampaignBootstrapOutcome.Failure(
                message = "No se pudo sincronizar con el servidor. Los cambios locales se conservaron para reintentar.",
            )
        }
    }

    fun close() {
        apiClient.close()
    }
}

private fun currentEpochSeconds(): Long = System.currentTimeMillis() / 1_000L
