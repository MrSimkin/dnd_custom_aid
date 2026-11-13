package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.DungeonActivity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/*
 * Base para DM, package 6 (Mesa de Escena; owner decisions P35, P36).
 *
 * Structured links between DM content, chosen from lists in the App de PC. The free texts of
 * each family stay as they are and are still searched; links add what free text cannot do
 * reliably: «PNJ aquí» on a place, zone or scene, the zone › place path, a shop's priced list,
 * the order of an adventure's scenes and pins on a map.
 *
 * One object per content item, kept in its own table and sent with the payload under "links",
 * so older Apps simply ignore it.
 */

/** Something a shop sells (P15): price as written by the DM, ⭐ to highlight; no stock is kept. */
@Serializable
data class ShopItem(
    val name: String,
    val price: String = "",
    val highlighted: Boolean = false,
    val note: String = "",
)

/** A mark on a map image (P35). x and y go from 0 to 1 over the image; a mark opens its sheet. */
@Serializable
data class MapPin(
    val x: Double,
    val y: Double,
    val targetId: Uuid? = null,
    val label: String = "",
) {
    init {
        require(x in 0.0..1.0 && y in 0.0..1.0) { "Map pins sit inside the image." }
    }
}

/** A passage between two zones of a dungeon diagram (P23); secret ones are shown only to the DM. */
@Serializable
data class ZonePassage(
    val toZoneId: Uuid,
    val secret: Boolean = false,
    val label: String = "",
)

/**
 * Links of one content item. Which fields matter depends on the family:
 * - NPC: [placeIds], [zoneIds], [sceneIds], [groups] (group or faction) and [portraitHandoutId];
 * - PLACE: [zoneIds] (its zone, first one), [npcIds] (who attends), [shopItems] and [mapHandoutId];
 * - ZONE: [mapHandoutId];
 * - SCENE: [adventure], [order], [detour], [sceneIds] (next scenes), [placeIds] and [zoneIds];
 * - HANDOUT (map): [pins].
 * Package 7 (Mesa de Dungeon):
 * - ZONE: [dungeon] (the dungeon it belongs to), [diagramX]/[diagramY] (0-1), [passages], [encounterIds];
 * - HOMEBREW_RULE: [activities], the DM's own list of Dungeon Turn Activities (P17).
 */
@Serializable
data class ContentLinks(
    val placeIds: List<Uuid> = emptyList(),
    val zoneIds: List<Uuid> = emptyList(),
    val sceneIds: List<Uuid> = emptyList(),
    val npcIds: List<Uuid> = emptyList(),
    val groups: List<String> = emptyList(),
    val portraitHandoutId: Uuid? = null,
    val mapHandoutId: Uuid? = null,
    val shopItems: List<ShopItem> = emptyList(),
    val adventure: String = "",
    val order: Int? = null,
    val detour: Boolean = false,
    val pins: List<MapPin> = emptyList(),
    val dungeon: String = "",
    /** The dungeon object this zone belongs to (redesign R3); [dungeon] keeps its name. */
    val dungeonId: Uuid? = null,
    val diagramX: Double? = null,
    val diagramY: Double? = null,
    val passages: List<ZonePassage> = emptyList(),
    val encounterIds: List<Uuid> = emptyList(),
    val activities: List<DungeonActivity> = emptyList(),
) {
    val isEmpty: Boolean
        get() = this == ContentLinks()

    /** Same links, cleaned: no blanks, no repeats, groups trimmed. */
    fun normalized(): ContentLinks = copy(
        placeIds = placeIds.distinct(),
        zoneIds = zoneIds.distinct(),
        sceneIds = sceneIds.distinct(),
        npcIds = npcIds.distinct(),
        groups = groups.map { it.trim() }.filter { it.isNotEmpty() }.distinctBy { it.lowercase() },
        shopItems = shopItems.filter { it.name.isNotBlank() }.map { it.copy(name = it.name.trim(), price = it.price.trim()) },
        adventure = adventure.trim(),
        dungeon = dungeon.trim(),
        diagramX = diagramX?.coerceIn(0.0, 1.0),
        diagramY = diagramY?.coerceIn(0.0, 1.0),
        passages = passages.distinctBy { it.toZoneId },
        encounterIds = encounterIds.distinct(),
        activities = activities.filter { it.name.isNotBlank() && it.key.isNotBlank() }.distinctBy { it.key },
    )
}

class ContentLinksRepository(private val database: AppDatabase) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = false
    }

    fun links(id: Uuid): ContentLinks =
        database.dmContentLinksQueries.selectLinks(id.toString()).executeAsOneOrNull()
            ?.let { runCatching { json.decodeFromString(ContentLinks.serializer(), it) }.getOrNull() }
            ?: ContentLinks()

    /** Every stored link set, for the desk's search and «PNJ aquí» (one read per desk). */
    fun all(): Map<Uuid, ContentLinks> =
        database.dmContentLinksQueries.selectAllLinks().executeAsList().mapNotNull { row ->
            val id = runCatching { Uuid.parse(row.content_id) }.getOrNull() ?: return@mapNotNull null
            val links = runCatching { json.decodeFromString(ContentLinks.serializer(), row.links_json) }.getOrNull() ?: return@mapNotNull null
            id to links
        }.toMap()

    fun save(id: Uuid, links: ContentLinks) {
        val clean = links.normalized()
        database.transaction {
            if (clean.isEmpty) {
                database.dmContentLinksQueries.deleteLinks(id.toString())
                return@transaction
            }
            val encoded = json.encodeToString(ContentLinks.serializer(), clean)
            if (database.dmContentLinksQueries.selectLinks(id.toString()).executeAsOneOrNull() == null) {
                database.dmContentLinksQueries.insertLinks(id.toString(), encoded)
            } else {
                database.dmContentLinksQueries.updateLinks(encoded, id.toString())
            }
        }
    }
}
