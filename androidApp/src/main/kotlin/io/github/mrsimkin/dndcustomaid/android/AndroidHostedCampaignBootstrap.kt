package io.github.mrsimkin.dndcustomaid.android

import com.descope.Descope
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDocument
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiClient
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiErrorCode
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedApiException
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedAuthenticationUnavailableException
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignBootstrapService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignCreationService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedMutationType
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedOutboxDeliveryService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedOutboxRepository
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcSnapshotPullService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcSnapshotQueueService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedRetryState
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import kotlinx.coroutines.CancellationException
import kotlin.uuid.Uuid

private const val PC_SYNC_OBJECT_TYPE = "PC"

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
        val hostedPcCount: Int,
        val appliedPcCount: Int,
        val unchangedPcCount: Int,
        val tombstonedPcCount: Int,
        val pcConflictCount: Int,
        val queuedPcSnapshotCount: Int,
    ) : AndroidHostedCampaignBootstrapOutcome

    data object NoRememberedSession : AndroidHostedCampaignBootstrapOutcome

    data class Failure(
        val message: String,
    ) : AndroidHostedCampaignBootstrapOutcome
}

/**
 * Android platform adapter for the existing shared hosted campaign/PC services.
 *
 * Reconciliation, durable outbox state and mutation delivery semantics stay in Shared. This class
 * only supplies the real DEV API/token edge and sequences the already-defined local-first services.
 */
internal class AndroidHostedCampaignBootstrapController(
    private val database: AppDatabase,
    private val apiClient: HostedApiClient = HostedApiClient(
        baseUrl = HostedDevelopmentEnvironment.API_BASE_URL,
        accessTokens = DescopeHostedAccessTokenProvider(),
    ),
) {
    private val outbox = HostedOutboxRepository(database)
    private val characters = CharacterRepository(database)
    private val backups = CharacterBackupRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val campaignCreation = HostedCampaignCreationService(
        database = database,
        outbox = outbox,
    )
    private val pcQueue = HostedPcSnapshotQueueService(
        database = database,
        backups = backups,
        spine = spine,
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
     * Synchronizes the current local-first Player state in a bounded sequence:
     *
     * 1. retry already-durable mutations;
     * 2. refresh account/campaign membership state;
     * 3. pull hosted PC snapshots for non-conflicted active campaigns;
     * 4. queue only new or actually-different equal-revision local PC snapshots;
     * 5. deliver newly queued PC snapshots;
     * 6. pull PCs once more to confirm/read back the resulting hosted state.
     *
     * A failed network/provider step never deletes local campaign/PC data or its durable outbox.
     */
    suspend fun refresh(): AndroidHostedCampaignBootstrapOutcome {
        if (!hasRememberedSession()) {
            return AndroidHostedCampaignBootstrapOutcome.NoRememberedSession
        }

        return try {
            val now = currentEpochSeconds()
            val firstDelivery = delivery.deliverReady(
                attemptedAtEpochSeconds = now,
            )
            val campaignResult = bootstrap.refresh()

            val activeHostedCampaignIds = apiClient.campaigns()
                .mapTo(mutableSetOf()) { it.id }
            val eligibleCampaignIds = campaignResult.appliedCampaignIds
                .filterTo(mutableSetOf()) { it in activeHostedCampaignIds }

            val appliedPcIds = mutableSetOf<Uuid>()
            var queuedPcSnapshotCount = 0

            for (campaignId in eligibleCampaignIds.sortedBy(Uuid::toString)) {
                val remoteBefore = apiClient.campaignPcs(campaignId)
                val initialPull = HostedPcSnapshotPullService(
                    database = database,
                    snapshotsProvider = { requestedCampaignId ->
                        require(requestedCampaignId == campaignId) {
                            "Cached hosted PC snapshot scope changed during reconciliation."
                        }
                        remoteBefore
                    },
                ).refreshCampaign(campaignId)
                appliedPcIds += initialPull.appliedPcIds

                val remoteById = remoteBefore.associateBy { it.id }
                val pendingPcIds = outbox.allMutations()
                    .asSequence()
                    .filter { it.type == HostedMutationType.PC_SNAPSHOT_PUT }
                    .mapTo(mutableSetOf()) { it.objectId }

                for (character in characters.listCharacters(campaignId)) {
                    if (character.id in pendingPcIds) continue

                    val metadata = spine.syncMetadata(PC_SYNC_OBJECT_TYPE, character.id)
                    if (metadata.isDeleted) continue

                    val remote = remoteById[character.id]
                    if (remote?.deletedAtEpochSeconds != null) continue
                    if (remote != null && metadata.revision.value != remote.revision) continue

                    val localSnapshot = backups.exportCharacter(
                        characterId = character.id,
                        exportedAtEpochSeconds = now,
                    )
                    val shouldQueue = remote == null ||
                        normalizedSnapshot(localSnapshot) != normalizedSnapshot(remote.snapshot)

                    if (shouldQueue) {
                        pcQueue.queueCurrentSnapshot(
                            characterId = character.id,
                            createdAtEpochSeconds = now,
                            exportedAtEpochSeconds = now,
                        )
                        pendingPcIds += character.id
                        queuedPcSnapshotCount += 1
                    }
                }
            }

            val secondDelivery = delivery.deliverReady(
                attemptedAtEpochSeconds = currentEpochSeconds(),
            )

            var hostedPcCount = 0
            var unchangedPcCount = 0
            var tombstonedPcCount = 0
            var pcConflictCount = 0

            for (campaignId in eligibleCampaignIds.sortedBy(Uuid::toString)) {
                val finalPull = HostedPcSnapshotPullService(
                    database = database,
                    api = apiClient,
                ).refreshCampaign(campaignId)
                hostedPcCount += finalPull.hostedCount
                appliedPcIds += finalPull.appliedPcIds
                unchangedPcCount += finalPull.unchangedPcIds.size
                tombstonedPcCount += finalPull.tombstonedPcIds.size
                pcConflictCount += finalPull.conflicts.size
            }

            AndroidHostedCampaignBootstrapOutcome.Success(
                hostedCampaignCount = campaignResult.hostedCampaignCount,
                appliedCampaignCount = campaignResult.appliedCampaignIds.size,
                conflictCount = campaignResult.conflicts.size,
                acknowledgedMutationCount = firstDelivery.acknowledged + secondDelivery.acknowledged,
                retryableMutationCount = firstDelivery.retryableFailures + secondDelivery.retryableFailures,
                blockedMutationCount = firstDelivery.blockedFailures + secondDelivery.blockedFailures,
                hostedPcCount = hostedPcCount,
                appliedPcCount = appliedPcIds.size,
                unchangedPcCount = unchangedPcCount,
                tombstonedPcCount = tombstonedPcCount,
                pcConflictCount = pcConflictCount,
                queuedPcSnapshotCount = queuedPcSnapshotCount,
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

private fun normalizedSnapshot(document: CharacterBackupDocument): CharacterBackupDocument =
    document.copy(exportedAtEpochSeconds = 0)

private fun currentEpochSeconds(): Long = System.currentTimeMillis() / 1_000L
