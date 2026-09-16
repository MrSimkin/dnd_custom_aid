package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import kotlin.uuid.Uuid

private const val PC_CONFLICT_SYNC_OBJECT_TYPE = "PC"

/**
 * Explicit conflict-resolution queue used only after a user has chosen to keep the local PC over
 * a reviewed newer hosted revision.
 *
 * Normal synchronization must keep using [HostedPcSnapshotQueueService]. This service deliberately
 * requires the reviewed hosted revision to be newer than the local sync revision, and records that
 * hosted revision as the outbox compare-and-swap expectation. The server therefore still rejects
 * the mutation if another client advances the PC again before delivery.
 */
class HostedPcConflictResolutionQueueService(
    private val database: AppDatabase,
    private val backups: CharacterBackupRepository = CharacterBackupRepository(database),
    private val spine: IntegratedSpineRepository = IntegratedSpineRepository(database),
    private val outbox: HostedOutboxRepository = HostedOutboxRepository(database),
) {
    fun queueLocalSnapshotAgainstHostedRevision(
        characterId: Uuid,
        expectedHostedRevision: Revision,
        createdAtEpochSeconds: Long,
        exportedAtEpochSeconds: Long = createdAtEpochSeconds,
        mutationId: Uuid = Uuid.random(),
    ): QueuedHostedPcSnapshot {
        require(createdAtEpochSeconds >= 0) { "PC conflict-resolution queue time must not be negative." }
        require(exportedAtEpochSeconds >= 0) { "PC conflict-resolution export time must not be negative." }

        return database.transactionWithResult {
            val document = backups.exportCharacter(characterId, exportedAtEpochSeconds)
            val metadata = spine.syncMetadata(PC_CONFLICT_SYNC_OBJECT_TYPE, characterId)
            require(!metadata.isDeleted) {
                "A locally tombstoned PC cannot be uploaded as active conflict resolution."
            }
            require(expectedHostedRevision > metadata.revision) {
                "Explicit keep-local conflict resolution requires a reviewed hosted revision newer than the local sync revision."
            }

            val mutation = outbox.enqueuePcSnapshot(
                campaignId = document.character.campaignId,
                pcId = document.character.id,
                expectedRevision = expectedHostedRevision.value,
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
