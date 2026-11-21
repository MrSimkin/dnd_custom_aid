package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSnapshotCodec
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import kotlin.uuid.Uuid

/*
 * The DM's trash (owner, 2026-11-18: «borrar, con papelera»). Until now nothing of Preparar could
 * be deleted from the App at all, so a wrong import stayed forever.
 *
 * Deleting leaves a tombstone, which is what travels to the other devices and must never be
 * resurrected (AGENTS §3). So restoring does not undo the tombstone: it puts the same content back
 * as a new object of the campaign, keeping its name, its payload and its links, and re-pointing at
 * it whatever used to point at the deleted one.
 */

/** Something that was deleted and can still be brought back. */
data class TrashedContent(
    val item: ReusableContentItem,
    val deletedAtEpochSeconds: Long,
)

class ContentTrash(
    private val database: AppDatabase,
    private val reusableContent: ReusableContentRepository = ReusableContentRepository(database),
    private val codec: DmContentSnapshotCodec = DmContentSnapshotCodec(database, reusableContent),
    private val links: ContentLinksRepository = ContentLinksRepository(database),
) {
    /** What was deleted in this scope, newest first. */
    fun list(scope: ContentScope, family: ReusableContentFamily? = null): List<TrashedContent> =
        reusableContent.listDeleted(scope, family).mapNotNull { item ->
            item.deletedAtEpochSeconds?.let { TrashedContent(item, it) }
        }

    /**
     * Puts the deleted object back as a new one of the same scope, with its content and its links,
     * and makes whatever pointed at the deleted one point at this copy instead.
     */
    fun restore(id: Uuid, nowEpochSeconds: Long, newId: Uuid = Uuid.random()): ReusableContentItem {
        require(nowEpochSeconds >= 0) { "Restore time must not be negative." }
        val deleted = requireNotNull(reusableContent.content(id, includeDeleted = true)) {
            "Lo que quieres recuperar ya no está en este aparato."
        }
        require(deleted.deletedAtEpochSeconds != null) { "Ese objeto no está en la papelera." }
        val document = requireNotNull(codec.export(id)) {
            "No se pudo leer lo que tenía el objeto borrado."
        }

        database.transaction {
            codec.applyHostedState(
                document.copy(
                    content = document.content.copy(
                        id = newId,
                        createdAtEpochSeconds = nowEpochSeconds,
                        updatedAtEpochSeconds = nowEpochSeconds,
                    ),
                ),
                hostedDeletedAtEpochSeconds = null,
            )
            repoint(from = id, to = newId)
        }
        return requireNotNull(reusableContent.content(newId))
    }

    /**
     * «Vaciar la papelera» (owner's H): this device forgets what those objects held. The tombstone
     * stays, so the other devices still learn about the deletion and nothing comes back by itself.
     */
    fun empty(scope: ContentScope): Int {
        val waiting = list(scope)
        database.transaction {
            waiting.forEach { entry ->
                database.reusableContentQueries.forgetReusableContent(entry.item.identity.id.toString())
            }
        }
        return waiting.size
    }

    /** Everything that mentioned the deleted object now mentions the copy. */
    private fun repoint(from: Uuid, to: Uuid) {
        links.all().forEach { (owner, value) ->
            if (owner == from) return@forEach
            val moved = value.copy(
                placeIds = value.placeIds.map { if (it == from) to else it }.distinct(),
                zoneIds = value.zoneIds.map { if (it == from) to else it }.distinct(),
                sceneIds = value.sceneIds.map { if (it == from) to else it }.distinct(),
                npcIds = value.npcIds.map { if (it == from) to else it }.distinct(),
                encounterIds = value.encounterIds.map { if (it == from) to else it }.distinct(),
                portraitHandoutId = value.portraitHandoutId?.let { if (it == from) to else it },
                mapHandoutId = value.mapHandoutId?.let { if (it == from) to else it },
                dungeonId = value.dungeonId?.let { if (it == from) to else it },
            )
            if (moved != value) links.save(owner, moved)
        }
    }
}
