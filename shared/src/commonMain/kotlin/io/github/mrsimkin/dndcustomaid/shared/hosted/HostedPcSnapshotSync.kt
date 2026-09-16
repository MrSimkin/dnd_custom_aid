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
    LOCAL_AND_HOSTED_CHANGED,
    SYNC_BASELINE_MISSING,
    SYNC_BASELINE_REVISION_MISMATCH,
    HOSTED_CHANGED_WITHOUT_REVISION,
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
                val localDocument = backupsDocument(remote.id)
                val baseline = baselines.baseline(remote.id)

                if (remote.deletedAtEpochSeconds != null) {
                    if (localMetadata.revision > hostedRevision) {
                        conflicts += conflict(
                            remote.id,
                            HostedPcPullConflictReason.LOCAL_REVISION_AHEAD,
                            localMetadata.revision,
                            hostedRevision,
                        )
                        return@forEach
                    }

                    if (localMetadata.isDeleted && localMetadata.revision == hostedRevision) {
                        tombstoned += remote.id
                        return@forEach
                    }

                    if (localDocument != null && localMetadata.revision < hostedRevision) {
                        val advanceConflict = conflictForHostedAdvance(
                            pcId = remote.id,
                            localDocument = localDocument,
                            localMetadata = localMetadata,
                            baseline = baseline,
                            hostedRevision = hostedRevision,
                        )
                        if (advanceConflict != null) {
                            conflicts += advanceConflict
                            return@forEach
                        }
                    }

                    spine.putSyncMetadata(
                        objectType = PC_SYNC_TYPE,
                        objectId = remote.id,
                        metadata = SyncMetadata(
                            revision = hostedRevision,
                            deletedAtEpochSeconds = remote.deletedAtEpochSeconds,
                        ),
                    )
                    // Hosted deletion revokes synchronized current state but deliberately keeps
                    // local character data for recovery/audit instead of destructively deleting it.
                    if (localDocument != null) {
                        baselines.record(remote.id, hostedRevision, remote.snapshot)
                    }
                    tombstoned += remote.id
                    return@forEach
                }

                if (localMetadata.isDeleted) {
                    conflicts += conflict(
                        remote.id,
                        HostedPcPullConflictReason.LOCAL_TOMBSTONE,
                        localMetadata.revision,
                        hostedRevision,
                    )
                    return@forEach
                }

                if (localMetadata.revision > hostedRevision) {
                    conflicts += conflict(
                        remote.id,
                        HostedPcPullConflictReason.LOCAL_REVISION_AHEAD,
                        localMetadata.revision,
                        hostedRevision,
                    )
                    return@forEach
                }

                if (localDocument == null) {
                    backups.applyCurrentState(remote.snapshot)
                    applyAuthorityWhenLocallyResolvable(remote)
                    spine.putSyncMetadata(
                        objectType = PC_SYNC_TYPE,
                        objectId = remote.id,
                        metadata = SyncMetadata(revision = hostedRevision),
                    )
                    baselines.record(remote.id, hostedRevision, remote.snapshot)
                    applied += remote.id
                    return@forEach
                }

                if (localMetadata.revision == hostedRevision) {
                    when {
                        baseline == null -> {
                            // Upgrade path for clients that synchronized before durable baselines
                            // existed: the equal-revision hosted snapshot is the authoritative
                            // baseline, while any differing local state remains an unsent local edit.
                            baselines.record(remote.id, hostedRevision, remote.snapshot)
                        }
                        baseline.revision != hostedRevision -> {
                            conflicts += conflict(
                                remote.id,
                                HostedPcPullConflictReason.SYNC_BASELINE_REVISION_MISMATCH,
                                localMetadata.revision,
                                hostedRevision,
                            )
                            return@forEach
                        }
                        baseline.snapshot != normalizePcSyncSnapshot(remote.snapshot) -> {
                            conflicts += conflict(
                                remote.id,
                                HostedPcPullConflictReason.HOSTED_CHANGED_WITHOUT_REVISION,
                                localMetadata.revision,
                                hostedRevision,
                            )
                            return@forEach
                        }
                    }

                    // Equal revision means the server has no newer authoritative mutation. Preserve
                    // the local aggregate so an offline/local edit can be queued normally afterward.
                    applyAuthorityWhenLocallyResolvable(remote)
                    unchanged += remote.id
                    return@forEach
                }

                val advanceConflict = conflictForHostedAdvance(
                    pcId = remote.id,
                    localDocument = localDocument,
                    localMetadata = localMetadata,
                    baseline = baseline,
                    hostedRevision = hostedRevision,
                    hostedDocument = remote.snapshot,
                )
                if (advanceConflict != null) {
                    conflicts += advanceConflict
                    return@forEach
                }

                backups.applyCurrentState(remote.snapshot)
                applyAuthorityWhenLocallyResolvable(remote)
                spine.putSyncMetadata(
                    objectType = PC_SYNC_TYPE,
                    objectId = remote.id,
                    metadata = SyncMetadata(revision = hostedRevision),
                )
                baselines.record(remote.id, hostedRevision, remote.snapshot)
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

    private fun conflictForHostedAdvance(
        pcId: Uuid,
        localDocument: CharacterBackupDocument,
        localMetadata: SyncMetadata,
        baseline: HostedPcSyncBaseline?,
        hostedRevision: Revision,
        hostedDocument: CharacterBackupDocument? = null,
    ): HostedPcPullConflict? {
        if (baseline == null) {
            // A legacy client can safely recover a missing historical baseline when its complete
            // current aggregate is already identical to the newer hosted aggregate. There is no
            // local information to lose in that case; the pull below only advances revision and
            // establishes the durable baseline. If content differs, remain conservative because
            // an empty outbox cannot prove the local copy was never edited offline.
            if (
                hostedDocument != null &&
                normalizePcSyncSnapshot(localDocument) == normalizePcSyncSnapshot(hostedDocument)
            ) {
                return null
            }
            return conflict(
                pcId,
                HostedPcPullConflictReason.SYNC_BASELINE_MISSING,
                localMetadata.revision,
                hostedRevision,
            )
        }
        if (baseline.revision != localMetadata.revision) {
            return conflict(
                pcId,
                HostedPcPullConflictReason.SYNC_BASELINE_REVISION_MISMATCH,
                localMetadata.revision,
                hostedRevision,
            )
        }
        if (normalizePcSyncSnapshot(localDocument) != baseline.snapshot) {
            return conflict(
                pcId,
                HostedPcPullConflictReason.LOCAL_AND_HOSTED_CHANGED,
                localMetadata.revision,
                hostedRevision,
            )
        }
        return null
    }

    private fun conflict(
        pcId: Uuid,
        reason: HostedPcPullConflictReason,
        localRevision: Revision,
        hostedRevision: Revision,
    ): HostedPcPullConflict = HostedPcPullConflict(
        pcId = pcId,
        reason = reason,
        localRevision = localRevision,
        hostedRevision = hostedRevision,
    )

    private fun backupsDocument(characterId: Uuid): CharacterBackupDocument? =
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
