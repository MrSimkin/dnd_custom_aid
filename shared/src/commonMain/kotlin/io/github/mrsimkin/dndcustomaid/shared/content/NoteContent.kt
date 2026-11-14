package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/*
 * Base para DM, package 7: live notes (P21; approved design §5.4).
 *
 * One kind of note, stuck by itself to where it was written (a zone, a place, a scene, an NPC, a
 * dungeon or the campaign) and changed with one tap. No tags or folders. Notes are always saved, as
 * soon as they are written, and travel to the App de PC with the DM content to be sorted later.
 */

@Serializable
enum class NoteContextKind(val label: String) {
    CAMPAIGN("Campaña"),
    ZONE("Zona"),
    PLACE("Lugar"),
    SCENE("Escena"),
    NPC("PNJ"),
    DUNGEON("Dungeon"),
    COMBAT("Combate"),
}

@Serializable
data class NotePayload(
    val text: String,
    val contextKind: NoteContextKind = NoteContextKind.CAMPAIGN,
    val contextId: Uuid? = null,
    /** The context's name when the note was written, so it reads well even if that object is gone. */
    val contextLabel: String = "",
)

@Serializable
data class NoteContent(
    val item: ReusableContentItem,
    val payload: NotePayload,
) {
    init {
        require(item.family == ReusableContentFamily.NOTE) { "NoteContent must wrap NOTE reusable content." }
    }
}

class NoteContentRepository(
    private val database: AppDatabase,
    private val reusableContent: ReusableContentRepository = ReusableContentRepository(database),
) {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    /** Saves a new note at once; its name is its first words. */
    fun create(campaignId: Uuid, payload: NotePayload, nowEpochSeconds: Long, id: Uuid = Uuid.random()): NoteContent {
        require(payload.text.isNotBlank()) { "A note needs some text." }
        var result: NoteContent? = null
        database.transaction {
            val item = reusableContent.createCampaign(campaignId, ReusableContentFamily.NOTE, titleOf(payload.text), nowEpochSeconds, id)
            insertPayload(item.identity.id, payload)
            result = NoteContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun note(id: Uuid, includeDeleted: Boolean = false): NoteContent? {
        val item = reusableContent.content(id, includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.NOTE) return null
        return payload(id)?.let { NoteContent(item, it) }
    }

    /** Newest first. */
    fun listCampaign(campaignId: Uuid): List<NoteContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.NOTE).mapNotNull { note(it.identity.id) }
            .sortedByDescending { it.item.createdAtEpochSeconds }

    /** Text edits and «cambiar de contexto» both go through here. */
    fun update(id: Uuid, expectedRevision: Revision, payload: NotePayload, updatedAtEpochSeconds: Long): RevisionDecision {
        require(payload.text.isNotBlank()) { "A note needs some text." }
        return reusableContent.mutateContent(id, expectedRevision, updatedAtEpochSeconds) { current ->
            require(current.family == ReusableContentFamily.NOTE) { "Note updates require NOTE reusable content." }
            database.reusableContentQueries.updateReusableContentName(titleOf(payload.text), updatedAtEpochSeconds, id.toString())
            updatePayloadRow(id, payload)
        }
    }

    fun tombstone(id: Uuid, expectedRevision: Revision, deletedAtEpochSeconds: Long): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) { "Note must exist before it can be deleted." }
        require(current.family == ReusableContentFamily.NOTE) { "Note deletion requires NOTE reusable content." }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    internal fun payload(id: Uuid): NotePayload? =
        database.notePayloadQueries.selectNotePayloadByContentId(id.toString()).executeAsOneOrNull()
            ?.let { runCatching { json.decodeFromString(NotePayload.serializer(), it) }.getOrNull() }

    internal fun insertPayload(id: Uuid, payload: NotePayload) {
        database.notePayloadQueries.insertNotePayload(id.toString(), json.encodeToString(NotePayload.serializer(), payload))
    }

    internal fun updatePayloadRow(id: Uuid, payload: NotePayload) {
        database.notePayloadQueries.updateNotePayload(json.encodeToString(NotePayload.serializer(), payload), id.toString())
    }

    private fun titleOf(text: String): String {
        val firstLine = text.trim().lineSequence().first().trim()
        return (if (firstLine.length > 60) firstLine.take(57) + "…" else firstLine).ifBlank { "Nota" }
    }
}
