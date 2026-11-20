package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/*
 * Owner's J (2026-11-20): «imprimir varias fichas de una vez», in the format the owner chose on
 * 2026-11-18: «primera opción [ficha de mesa, una por hoja], pero necesito formato para el
 * stat-block si aplica».
 *
 * A scene or a place is never run alone: the DM needs the PNJ who are there and the creatures of
 * its encounters on the table too. This gathers them in the order they are read — the scene or the
 * place first, then its places, its PNJ and last the creatures — and the PDF gives each one its
 * own sheet.
 */
internal class TableCardBundle(database: AppDatabase) {
    private val content = ReusableContentRepository(database)
    private val links = ContentLinksRepository(database)
    private val scenes = SceneContentRepository(database, content)
    private val places = PlaceContentRepository(database, content)
    private val npcs = NpcContentRepository(database, content)
    private val encounters = EncounterContentRepository(database, content)
    private val creatures = CreatureContentRepository(database, content)

    /** The scene, the places and PNJ it mentions, and the creatures of its encounters. */
    fun forScene(id: Uuid): List<TableCard> {
        val scene = scenes.scene(id) ?: return emptyList()
        return listOf(sceneTableCard(scene)) + around(id)
    }

    /** The place, the PNJ who are there, and the creatures of its encounters. */
    fun forPlace(id: Uuid): List<TableCard> {
        val place = places.place(id) ?: return emptyList()
        return listOf(placeTableCard(place)) + around(id, includePlaces = false)
    }

    private fun around(id: Uuid, includePlaces: Boolean = true): List<TableCard> {
        val all = links.all()
        val own = all[id] ?: ContentLinks()
        // Whoever says it is here counts as being here, even when the sheet itself does not say so.
        val pointingAtIt = all.filterValues { value ->
            id in value.placeIds || id in value.sceneIds || id in value.zoneIds
        }.keys

        val placeIds = if (includePlaces) own.placeIds.distinct() else emptyList()
        val npcIds = (own.npcIds + pointingAtIt).distinct()
        val encounterIds = own.encounterIds.distinct()
        val creatureIds = encounterIds
            .mapNotNull { runCatching { encounters.encounter(it) }.getOrNull() }
            .flatMap { encounter -> encounter.payload.participants.mapNotNull { it.sourceContentId } }
            .distinct()

        return buildList {
            placeIds.forEach { placeId ->
                runCatching { places.place(placeId) }.getOrNull()?.let { add(placeTableCard(it)) }
            }
            npcIds.forEach { npcId ->
                runCatching { npcs.npc(npcId) }.getOrNull()?.let { add(npcTableCard(it, "")) }
            }
            creatureIds.forEach { creatureId ->
                runCatching { creatures.creature(creatureId) }.getOrNull()?.let { add(creatureTableCard(it)) }
            }
        }
    }
}
