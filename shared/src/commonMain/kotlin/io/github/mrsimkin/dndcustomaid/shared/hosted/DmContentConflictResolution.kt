package io.github.mrsimkin.dndcustomaid.shared.hosted

import io.github.mrsimkin.dndcustomaid.shared.content.ContentTrash
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentItem
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/*
 * Owner's B (2026-11-20), the rule proposed on 2026-11-14 and chosen now:
 *
 * - changed on one device only: that change wins, with no question;
 * - changed on both: nothing is overwritten, the DM sees the two versions and chooses;
 * - deleted on one and edited on the other: asked as well.
 *
 * The sync already refused to overwrite. What was missing was somewhere to decide: the other
 * device's version is kept here so the DM can look at it later, without having to sync again.
 */

/** What the other device had, next to what this one has. */
data class DmContentDifference(
    val contentId: Uuid,
    val reason: DmContentSyncConflictReason,
    val seenAtEpochSeconds: Long,
    /** This device's object, or null when it was deleted here. */
    val mine: ReusableContentItem?,
    /** How this device's version reads, field by field. */
    val mineLines: List<Pair<String, String>>,
    /** How the other device's version reads. */
    val theirsLines: List<Pair<String, String>>,
    val theirsName: String,
    val theirsDeleted: Boolean,
) {
    /** The fields where the two disagree. */
    val differingFields: List<String>
        get() {
            val mineMap = mineLines.toMap()
            val theirsMap = theirsLines.toMap()
            return (mineMap.keys + theirsMap.keys).sorted()
                .filter { mineMap[it].orEmpty() != theirsMap[it].orEmpty() }
        }
}

/** What the DM chose for one difference. */
enum class DmContentChoice {
    /** Keep this device's version; the other one is replaced on the next sync. */
    MINE,
    /** Keep the other device's version; what was here is replaced. */
    THEIRS,
    /** Keep the other device's as the object, and this device's as a new one beside it. */
    BOTH,
}

class DmContentConflictResolution(
    private val database: AppDatabase,
    private val codec: DmContentSnapshotCodec = DmContentSnapshotCodec(database),
    private val content: ReusableContentRepository = ReusableContentRepository(database),
    private val outbox: HostedDmContentOutboxRepository = HostedDmContentOutboxRepository(database),
) {
    private val json = dmContentJson()

    /** Everything waiting for the DM to decide, newest first. */
    fun differences(): List<DmContentDifference> =
        database.dmContentConflictQueries.selectDmContentConflicts().executeAsList().mapNotNull { row ->
            val id = runCatching { Uuid.parse(row.content_id) }.getOrNull() ?: return@mapNotNull null
            val theirs = runCatching {
                json.decodeFromString(DmContentSnapshotDocument.serializer(), row.hosted_snapshot)
            }.getOrNull() ?: return@mapNotNull null
            val mine = content.content(id, includeDeleted = true)
            DmContentDifference(
                contentId = id,
                reason = DmContentSyncConflictReason.entries.firstOrNull { it.name == row.reason }
                    ?: DmContentSyncConflictReason.LOCAL_AND_HOSTED_CHANGED,
                seenAtEpochSeconds = row.seen_at_epoch_seconds,
                mine = mine?.takeIf { it.deletedAtEpochSeconds == null },
                mineLines = codec.export(id)?.let(::readable).orEmpty(),
                theirsLines = readable(theirs),
                theirsName = theirs.content.displayName,
                theirsDeleted = row.hosted_deleted == 1L,
            )
        }

    fun count(): Int = database.dmContentConflictQueries.selectDmContentConflicts().executeAsList().size

    /**
     * Applies what the DM chose. Nothing else of the campaign is touched, and the difference
     * leaves the list.
     */
    fun resolve(contentId: Uuid, choice: DmContentChoice, nowEpochSeconds: Long) {
        val row = database.dmContentConflictQueries.selectDmContentConflict(contentId.toString()).executeAsOneOrNull()
            ?: return
        val theirs = json.decodeFromString(DmContentSnapshotDocument.serializer(), row.hosted_snapshot)

        database.transaction {
            when (choice) {
                DmContentChoice.THEIRS -> takeTheirs(contentId, theirs, row.hosted_revision, row.hosted_deleted == 1L)
                DmContentChoice.MINE -> keepMine(contentId, row.hosted_revision, nowEpochSeconds)
                DmContentChoice.BOTH -> {
                    copyMineBeside(contentId, nowEpochSeconds)
                    takeTheirs(contentId, theirs, row.hosted_revision, row.hosted_deleted == 1L)
                }
            }
            database.dmContentConflictQueries.deleteDmContentConflict(contentId.toString())
        }
    }

    /** The other device's version becomes the object here. */
    private fun takeTheirs(contentId: Uuid, theirs: DmContentSnapshotDocument, revision: Long, deleted: Boolean) {
        forgetQueued(contentId)
        codec.applyHostedState(theirs, hostedDeletedAtEpochSeconds = if (deleted) theirs.content.updatedAtEpochSeconds else null)
        outbox.recordBaseline(contentId, revision, deleted, codec.normalizedJson(theirs))
    }

    /** This device's version is sent as a change on top of the other one. */
    private fun keepMine(contentId: Uuid, hostedRevision: Long, nowEpochSeconds: Long) {
        forgetQueued(contentId)
        val mine = codec.export(contentId)
        if (mine == null) {
            // Deleted here and changed there: the deletion wins, on top of their revision.
            outbox.enqueueDelete(contentId, hostedRevision, nowEpochSeconds)
            return
        }
        outbox.enqueuePut(mine, hostedRevision, nowEpochSeconds)
    }

    /** This device's version stays as a new object, so nothing of the DM's work is lost. */
    private fun copyMineBeside(contentId: Uuid, nowEpochSeconds: Long) {
        val mine = codec.export(contentId) ?: return
        val copy = mine.copy(
            content = mine.content.copy(
                id = Uuid.random(),
                displayName = mine.content.displayName + " (mi versión)",
                createdAtEpochSeconds = nowEpochSeconds,
                updatedAtEpochSeconds = nowEpochSeconds,
            ),
        )
        codec.applyHostedState(copy, hostedDeletedAtEpochSeconds = null)
    }

    /** Whatever was queued for this object stops: the DM just said what should happen to it. */
    private fun forgetQueued(contentId: Uuid) {
        outbox.mutationsFor(contentId).forEach { mutation ->
            database.dmContentSyncQueries.acknowledgeMutation(mutation.mutationId.toString())
        }
    }

    /** The few lines a DM needs to tell two versions apart. */
    private fun readable(document: DmContentSnapshotDocument): List<Pair<String, String>> =
        listOf("Nombre" to document.content.displayName) +
            document.payload.entries
                .filter { (key, _) -> key != "links" }
                .map { (key, value) -> spanishField(key) to value.toString().trim('"').take(400) }
                .filter { it.second.isNotBlank() && it.second != "[]" && it.second != "{}" && it.second != "null" }
                .sortedBy { it.first }
}

private fun spanishField(key: String): String = when (key) {
    "conceptRole" -> "Concepto"
    "appearanceFirstImpression" -> "Apariencia"
    "personalityManner" -> "Personalidad"
    "wantsFearsNeeds" -> "Quiere, teme, necesita"
    "secrets" -> "Secretos"
    "dmGuidance" -> "Guía"
    "notes" -> "Notas"
    "summary" -> "Resumen"
    "presentation" -> "Presentación"
    "purpose" -> "Para qué sirve"
    "sections" -> "Secciones"
    "participants" -> "Criaturas"
    "armorClass" -> "CA"
    "hitPoints" -> "PV"
    "actions" -> "Acciones"
    "text" -> "Texto"
    else -> key
}

/** For tests and for the sync: the Json the snapshots travel in. */
internal fun conflictJson(): Json = dmContentJson()
