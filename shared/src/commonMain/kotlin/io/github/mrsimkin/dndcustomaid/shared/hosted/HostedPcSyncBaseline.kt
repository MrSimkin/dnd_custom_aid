package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDocument
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

data class HostedPcSyncBaseline(
    val pcId: Uuid,
    val revision: Revision,
    val snapshot: CharacterBackupDocument,
)

class HostedPcSyncBaselineRepository(
    private val database: AppDatabase,
    private val json: Json = hostedWireJson(),
) {
    fun baseline(pcId: Uuid): HostedPcSyncBaseline? =
        database.pcSyncBaselineQueries.selectBaseline(
            pc_id = pcId.toString(),
            mapper = { storedPcId, revision, snapshotJson ->
                HostedPcSyncBaseline(
                    pcId = Uuid.parse(storedPcId),
                    revision = Revision(revision),
                    snapshot = json.decodeFromString(snapshotJson),
                )
            },
        ).executeAsOneOrNull()

    fun record(
        pcId: Uuid,
        revision: Revision,
        snapshot: CharacterBackupDocument,
    ) {
        require(snapshot.character.id == pcId) {
            "PC sync baseline identity must match its character snapshot."
        }
        val normalized = normalizePcSyncSnapshot(snapshot)
        database.pcSyncBaselineQueries.upsertBaseline(
            pc_id = pcId.toString(),
            revision = revision.value,
            snapshot_json = json.encodeToString(normalized),
        )
    }

    fun delete(pcId: Uuid) {
        database.pcSyncBaselineQueries.deleteBaseline(pcId.toString())
    }
}

internal fun normalizePcSyncSnapshot(document: CharacterBackupDocument): CharacterBackupDocument =
    document.copy(exportedAtEpochSeconds = 0)
