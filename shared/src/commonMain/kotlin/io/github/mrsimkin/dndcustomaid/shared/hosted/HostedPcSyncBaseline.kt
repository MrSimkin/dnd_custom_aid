package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDecodeResult
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDocument
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

data class HostedPcSyncBaseline(
    val pcId: Uuid,
    val revision: Long,
    val snapshot: CharacterBackupDocument,
) {
    init {
        require(revision >= 0) { "Hosted PC baseline revision must not be negative." }
        require(snapshot.character.id == pcId) { "Hosted PC baseline identity does not match its snapshot." }
        require(snapshot.exportedAtEpochSeconds == 0L) {
            "Hosted PC baseline snapshot must be normalized before persistence."
        }
    }
}

/**
 * Stores the last hosted PC state that this client actually reconciled at a known revision.
 *
 * Revision metadata alone cannot tell whether an older local PC was edited while offline. Keeping
 * the normalized last-synchronized snapshot lets reconciliation distinguish a clean old copy from
 * concurrent local changes without destructively overwriting either side.
 */
class HostedPcSyncBaselineRepository(
    private val database: AppDatabase,
) {
    fun baseline(pcId: Uuid): HostedPcSyncBaseline? =
        database.hostedPcSyncBaselineQueries.selectBaseline(pcId.toString()) {
                storedPcId, revision, snapshotJson ->
            val decoded = CharacterBackupCodec.decode(snapshotJson)
            val document = requireNotNull((decoded as? CharacterBackupDecodeResult.Success)?.document) {
                "Stored hosted PC baseline snapshot is invalid."
            }
            HostedPcSyncBaseline(
                pcId = Uuid.parse(storedPcId),
                revision = revision,
                snapshot = document,
            )
        }.executeAsOneOrNull()

    fun put(pcId: Uuid, revision: Long, snapshot: CharacterBackupDocument): HostedPcSyncBaseline {
        require(revision >= 0) { "Hosted PC baseline revision must not be negative." }
        require(snapshot.character.id == pcId) { "Hosted PC baseline identity does not match its snapshot." }
        val normalized = normalizeHostedPcSnapshot(snapshot)
        database.hostedPcSyncBaselineQueries.upsertBaseline(
            pc_id = pcId.toString(),
            revision = revision,
            snapshot_json = CharacterBackupCodec.encode(normalized),
        )
        return requireNotNull(baseline(pcId))
    }

    fun delete(pcId: Uuid) {
        database.hostedPcSyncBaselineQueries.deleteBaseline(pcId.toString())
    }

    fun matches(
        pcId: Uuid,
        revision: Long,
        currentSnapshot: CharacterBackupDocument,
    ): Boolean {
        val stored = baseline(pcId) ?: return false
        return stored.revision == revision && stored.snapshot == normalizeHostedPcSnapshot(currentSnapshot)
    }
}

internal fun normalizeHostedPcSnapshot(document: CharacterBackupDocument): CharacterBackupDocument =
    document.copy(exportedAtEpochSeconds = 0)
