package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDocument
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.PcAuthority
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.SyncMetadata
import kotlin.uuid.Uuid

private const val PC_SYNC_TYPE = "PC"

data class QueuedHostedPcSnapshot(
    val character: CharacterSheet,
    val mutation: HostedOutboxMutation,
)

class HostedPcSnapshotQueueService(
    private val database: AppDatabase,
    private val backups: CharacterBackupRepository = CharacterBackupRepository(database),
    private val spine: IntegratedSpineRepository = IntegratedSpineRepository(database),
    private val outbox: HostedOutboxRepository = HostedOutboxRepository(database),
) {
    fun queueCurrentSnapshot(
        characterId: Uuid,
        createdAtEpochSeconds: Long,
        exportedAtEpochSeconds: Long = createdAtEpochSeconds,
        mutationId: Uuid = Uuid.random(),
    ): QueuedHostedPcSnapshot {
        require(createdAtEpochSeconds >= 0) { "PC snapshot queue time must not be negative." }
        require(exportedAtEpochSeconds >= 0) { "PC snapshot export time must not be negative." }

        return database.transactionWithResult {
            val document = backups.exportCharacter(characterId, exportedAtEpochSeconds)
            val metadata = spine.syncMetadata(PC_SYNC_TYPE, characterId)
            require(!metadata.isDeleted) { "A locally tombstoned PC cannot be queued as active current state." }
            val mutation = outbox.enqueuePcSnapshot(
                campaignId = document.character.campaignId,
                pcId = document.character.id,
                expectedRevision = metadata.revision.value,
                snapshot = document,
                createdAtEpochSeconds = createdAtEpochSeconds,
                mutationId = mutationId,
            )
            QueuedHostedPcSnapshot(
                character = document.character,
                mutation = mutation,
            )
        }
    }
}

enum class HostedPcPullConflictReason {
    LOCAL_REVISION_AHEAD,
    LOCAL_TOMBSTONE,
    LOCAL_UNSENT_CHANGES,
    LOCAL_BASELINE_UNKNOWN,
}

data class HostedPcPullConflict(
    val pcId: Uuid,
    val reason: HostedPcPullConflictReason,
    val localRevision: Revision,
    val hostedRevision: Revision,
)

data class HostedPcPullResult(
    val hostedCount: Int,
    val appliedPcIds: List<Uuid>,
    val tombstonedPcIds: List<Uuid>,
    val unchangedPcIds: List<Uuid>,
    val conflicts: List<HostedPcPullConflict>,
) {
    init {
        require(hostedCount >= 0)
        require(
            appliedPcIds.size + tombstonedPcIds.size + unchangedPcIds.size + conflicts.size == hostedCount,
        ) { "Every hosted PC must have exactly one reconciliation outcome." }
    }
}

class HostedPcSnapshotPullService(
    private val database: AppDatabase,
    private val backups: CharacterBackupRepository = CharacterBackupRepository(database),
    private val spine: IntegratedSpineRepository = IntegratedSpineRepository(database),
    private val baselines: HostedPcSyncBaselineRepository = HostedPcSyncBaselineRepository(database),
    private val snapshotsProvider: suspend (Uuid) -> List<HostedPcSnapshot>,
) {
    constructor(
        database: AppDatabase,
        api: HostedApiClient,
    ) : this(
        database = database,
        snapshotsProvider = api::campaignPcs,
    )

    suspend fun refreshCampaign(campaignId: Uuid): HostedPcPullResult {
        val hosted = snapshotsProvider(campaignId)
        require(hosted.map { it.id }.distinct().size == hosted.size) {
            "Hosted PC response contains duplicate PC identities."
        }
        require(hosted.all { it.campaignId == campaignId }) {
            "Hosted PC response escaped the requested campaign scope."
        }

        val applied = mutableListOf<Uuid>()
        val tombstoned = mutableListOf<Uuid>()
        val unchanged = mutableListOf<Uuid>()
        val conflicts = mutableListOf<HostedPcPullConflict>()

        database.transaction {
            hosted.sortedBy { it.id.toString() }.forEach { remote ->
                val hostedRevision = Revision(remote.revision)
                val localMetadata = spine.syncMetadata(PC_SYNC_TYPE, remote.id)
                val localSnapshot = localSnapshot(remote.id)

                if (remote.deletedAtEpochSeconds != null) {
                    val conflictReason = localConflictAgainstNewerHostedState(
                        pcId = remote.id,
                        localMetadata = localMetadata,
                        hostedRevision = hostedRevision,
                        localSnapshot = localSnapshot,
                    )
                    if (conflictReason != null) {
                        conflicts += HostedPcPullConflict(
                            pcId = remote.id,
                            reason = conflictReason,
                            localRevision = localMetadata.revision,
                            hostedRevision = hostedRevision,
                        )
                        return@forEach
                    }

                    spine.putSyncMetadata(
                        objectType = PC_SYNC_TYPE,
                        objectId = remote.id,
                        metadata = SyncMetadata(
                            revision = hostedRevision,
                            deletedAtEpochSeconds = remote.deletedAtEpochSeconds,
                        ),
                    )
                    baselines.delete(remote.id)
                    // Hosted deletion revokes the synchronized current state but deliberately keeps
                    // local character data for recovery/audit instead of destructively deleting it.
                    tombstoned += remote.id
                    return@forEach
                }

                if (localMetadata.isDeleted) {
                    conflicts += HostedPcPullConflict(
                        pcId = remote.id,
                        reason = HostedPcPullConflictReason.LOCAL_TOMBSTONE,
                        localRevision = localMetadata.revision,
                        hostedRevision = hostedRevision,
                    )
                    return@forEach
                }

                if (localMetadata.revision > hostedRevision) {
                    conflicts += HostedPcPullConflict(
                        pcId = remote.id,
                        reason = HostedPcPullConflictReason.LOCAL_REVISION_AHEAD,
                        localRevision = localMetadata.revision,
                        hostedRevision = hostedRevision,
                    )
                    return@forEach
                }

                if (localSnapshot != null && localMetadata.revision == hostedRevision) {
                    // Equal revision means the server has no newer authoritative mutation. Preserve
                    // any local edit, but remember exactly what the server looked like at this
                    // revision so a later server-newer pull can detect concurrent local changes.
                    baselines.put(remote.id, remote.revision, remote.snapshot)
                    applyAuthorityWhenLocallyResolvable(remote)
                    unchanged += remote.id
                    return@forEach
                }

                val conflictReason = localConflictAgainstNewerHostedState(
                    pcId = remote.id,
                    localMetadata = localMetadata,
                    hostedRevision = hostedRevision,
                    localSnapshot = localSnapshot,
                )
                if (conflictReason != null) {
                    conflicts += HostedPcPullConflict(
                        pcId = remote.id,
                        reason = conflictReason,
                        localRevision = localMetadata.revision,
                        hostedRevision = hostedRevision,
                    )
                    return@forEach
                }

                backups.applyCurrentState(remote.snapshot)
                applyAuthorityWhenLocallyResolvable(remote)
                spine.putSyncMetadata(
                    objectType = PC_SYNC_TYPE,
                    objectId = remote.id,
                    metadata = SyncMetadata(revision = hostedRevision),
                )
                baselines.put(remote.id, remote.revision, remote.snapshot)
                applied += remote.id
            }
        }

        return HostedPcPullResult(
            hostedCount = hosted.size,
            appliedPcIds = applied,
            tombstonedPcIds = tombstoned,
            unchangedPcIds = unchanged,
            conflicts = conflicts,
        )
    }

    private fun localConflictAgainstNewerHostedState(
        pcId: Uuid,
        localMetadata: SyncMetadata,
        hostedRevision: Revision,
        localSnapshot: CharacterBackupDocument?,
    ): HostedPcPullConflictReason? {
        if (localMetadata.revision > hostedRevision) {
            return HostedPcPullConflictReason.LOCAL_REVISION_AHEAD
        }
        if (localSnapshot == null || localMetadata.revision >= hostedRevision) {
            return null
        }

        val baseline = baselines.baseline(pcId)
            ?: return HostedPcPullConflictReason.LOCAL_BASELINE_UNKNOWN
        if (baseline.revision != localMetadata.revision.value) {
            return HostedPcPullConflictReason.LOCAL_BASELINE_UNKNOWN
        }
        if (baseline.snapshot != normalizeHostedPcSnapshot(localSnapshot)) {
            return HostedPcPullConflictReason.LOCAL_UNSENT_CHANGES
        }
        return null
    }

    private fun localSnapshot(characterId: Uuid): CharacterBackupDocument? =
        try {
            backups.exportCharacter(characterId, exportedAtEpochSeconds = 0)
        } catch (_: IllegalArgumentException) {
            null
        }

    private fun applyAuthorityWhenLocallyResolvable(remote: HostedPcSnapshot) {
        val referencedAccounts = listOfNotNull(remote.ownerUserId, remote.controllerUserId).distinct()
        if (referencedAccounts.any { spine.account(it) == null }) {
            return
        }
        try {
            spine.setPcAuthority(
                PcAuthority(
                    characterId = remote.id,
                    ownerAccountId = remote.ownerUserId,
                    controllerAccountId = remote.controllerUserId,
                ),
            )
        } catch (_: IllegalArgumentException) {
            // A DM may pull PCs owned by campaign members whose local membership roster has not
            // been hydrated yet. Snapshot state is still safe to reconcile; authority is retried
            // once those account/membership records exist locally.
        }
    }
}
