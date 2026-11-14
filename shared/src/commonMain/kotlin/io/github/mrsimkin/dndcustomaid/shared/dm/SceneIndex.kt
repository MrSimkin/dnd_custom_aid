package io.github.mrsimkin.dndcustomaid.shared.dm

import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContent
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContent
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContent
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceKind
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContent
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContent
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/*
 * Base para DM, package 6: what the Mesa de Escena reads (approved design §4; P15, P35, P36).
 *
 * One index per campaign, built once per desk. It answers the desk's questions:
 * - searches by idea or context (role, appearance, places, scenes, groups, linked names);
 * - lists «PNJ aquí», the zone › place path and the rest of a zone;
 * - walks by zone, by function and by adventure;
 * - groups NPCs.
 * Links chosen in the App de PC come first; free texts are still searched.
 */

enum class SceneEntryKind(val label: String) {
    NPC("PNJ"),
    SHOP("Tienda"),
    PLACE("Lugar"),
    ZONE("Zona"),
    SCENE("Escena"),
}

/** Type filters of the search (§4): Todo, PNJ, Tienda, Lugar (places and zones) and Escena. */
enum class SceneFilter(val label: String) {
    ALL("Todo"),
    NPC("PNJ"),
    SHOP("Tienda"),
    PLACE("Lugar"),
    SCENE("Escena");

    fun accepts(kind: SceneEntryKind): Boolean = when (this) {
        ALL -> true
        NPC -> kind == SceneEntryKind.NPC
        SHOP -> kind == SceneEntryKind.SHOP
        PLACE -> kind == SceneEntryKind.PLACE || kind == SceneEntryKind.ZONE || kind == SceneEntryKind.SHOP
        SCENE -> kind == SceneEntryKind.SCENE
    }
}

/** How NPCs can be grouped (P36). */
enum class NpcGrouping(val label: String) {
    NONE("Sin agrupar"),
    WHERE("Zona o lugar"),
    SCENE("Escena"),
    GROUP("Grupo o facción"),
    ROLE("Papel"),
}

data class SceneEntry(
    val id: Uuid,
    val kind: SceneEntryKind,
    val name: String,
    /** First line under the name: the NPC's role, the place's summary, the scene's purpose. */
    val headline: String,
    /** Something to recognize it by: an NPC's first impression, a shop's highlights. */
    val detail: String,
    /** Where it is: «Zona › Lugar» or the NPC's places. */
    val where: String,
    internal val searchText: String,
    internal val nameText: String,
)

class SceneIndex(
    val npcs: List<NpcContent>,
    val places: List<PlaceContent>,
    val zones: List<ZoneContent>,
    val scenes: List<SceneContent>,
    val handouts: List<HandoutContent>,
    private val links: Map<Uuid, ContentLinks>,
) {
    private val npcById = npcs.associateBy { it.item.identity.id }
    private val placeById = places.associateBy { it.item.identity.id }
    private val zoneById = zones.associateBy { it.item.identity.id }
    private val sceneById = scenes.associateBy { it.item.identity.id }
    private val handoutById = handouts.associateBy { it.item.identity.id }

    fun links(id: Uuid): ContentLinks = links[id] ?: ContentLinks()

    fun npc(id: Uuid) = npcById[id]
    fun place(id: Uuid) = placeById[id]
    fun zone(id: Uuid) = zoneById[id]
    fun scene(id: Uuid) = sceneById[id]
    fun handout(id: Uuid) = handoutById[id]

    fun nameOf(id: Uuid): String? =
        npcById[id]?.item?.displayName ?: placeById[id]?.item?.displayName ?: zoneById[id]?.item?.displayName ?: sceneById[id]?.item?.displayName

    fun kindOf(id: Uuid): SceneEntryKind? = when {
        id in npcById -> SceneEntryKind.NPC
        id in placeById -> if (placeById.getValue(id).payload.kind == PlaceKind.SHOP) SceneEntryKind.SHOP else SceneEntryKind.PLACE
        id in zoneById -> SceneEntryKind.ZONE
        id in sceneById -> SceneEntryKind.SCENE
        else -> null
    }

    /** The zone a place belongs to: its link, or a zone whose name matches the place's «área». */
    fun zoneOf(place: PlaceContent): ZoneContent? =
        links(place.item.identity.id).zoneIds.firstNotNullOfOrNull(zoneById::get)
            ?: place.payload.area.takeIf { it.isNotBlank() }?.let { area -> zones.firstOrNull { normalizeForSearch(it.item.displayName) == normalizeForSearch(area) } }

    fun placesIn(zone: ZoneContent): List<PlaceContent> =
        places.filter { zoneOf(it)?.item?.identity?.id == zone.item.identity.id }.sortedBy { it.item.displayName.lowercase() }

    /** «Zona › Lugar» for a place, the zone's own area for a zone. */
    fun pathOf(id: Uuid): String {
        placeById[id]?.let { place -> return listOfNotNull(zoneOf(place)?.item?.displayName, place.item.displayName).joinToString(" › ") }
        zoneById[id]?.let { zone -> return listOf(zone.payload.area, zone.item.displayName).filter { it.isNotBlank() }.joinToString(" › ") }
        return nameOf(id).orEmpty()
    }

    /**
     * «PNJ aquí» (P36): NPCs linked to this place, zone or scene from either side. A zone also shows
     * the NPCs of its places.
     */
    fun npcsHere(id: Uuid): List<NpcContent> {
        val direct = links(id).npcIds.mapNotNull(npcById::get)
        val linkedFromNpc = npcs.filter { npc ->
            val l = links(npc.item.identity.id)
            id in l.placeIds || id in l.zoneIds || id in l.sceneIds
        }
        val fromPlaces = zoneById[id]?.let { zone -> placesIn(zone).flatMap { npcsHere(it.item.identity.id) } }.orEmpty()
        return (direct + linkedFromNpc + fromPlaces).distinctBy { it.item.identity.id }.sortedBy { it.item.displayName.lowercase() }
    }

    /** Where an NPC is: linked places and zones, otherwise its free text «Lugares». */
    fun whereIs(npc: NpcContent): List<String> {
        val l = links(npc.item.identity.id)
        val linked = l.placeIds.map { pathOf(it) }.filter { it.isNotBlank() } + l.zoneIds.mapNotNull { zoneById[it]?.item?.displayName }
        val reverse = places.filter { npc.item.identity.id in links(it.item.identity.id).npcIds }.map { pathOf(it.item.identity.id) } +
            zones.filter { npc.item.identity.id in links(it.item.identity.id).npcIds }.map { it.item.displayName }
        return (linked + reverse).distinct().ifEmpty { listOfNotNull(npc.payload.places.trim().takeIf { it.isNotEmpty() }) }
    }

    /** Places of an NPC, linked from either side (the NPC's «dónde está» or the place's «quién atiende»). */
    fun placesOf(npc: NpcContent): List<PlaceContent> {
        val id = npc.item.identity.id
        return (links(id).placeIds.mapNotNull(placeById::get) + places.filter { id in links(it.item.identity.id).npcIds }).distinctBy { it.item.identity.id }
    }

    fun zonesOf(npc: NpcContent): List<ZoneContent> {
        val id = npc.item.identity.id
        return (links(id).zoneIds.mapNotNull(zoneById::get) + zones.filter { id in links(it.item.identity.id).npcIds }).distinctBy { it.item.identity.id }
    }

    fun scenesOf(npc: NpcContent): List<SceneContent> {
        val id = npc.item.identity.id
        return (links(id).sceneIds.mapNotNull(sceneById::get) + scenes.filter { id in links(it.item.identity.id).npcIds }).distinctBy { it.item.identity.id }
    }

    fun portraitOf(npc: NpcContent): HandoutContent? = links(npc.item.identity.id).portraitHandoutId?.let(handoutById::get)

    fun mapOf(id: Uuid): HandoutContent? = links(id).mapHandoutId?.let(handoutById::get)

    /** Places and zones that have a map to walk (P35). */
    fun withMaps(): List<Uuid> = (zones.map { it.item.identity.id } + places.map { it.item.identity.id }).filter { mapOf(it) != null }

    fun nextScenes(scene: SceneContent): List<SceneContent> = links(scene.item.identity.id).sceneIds.mapNotNull(sceneById::get)

    /** «Por aventura»: each adventure's scenes in order; scenes without an adventure go last. */
    fun adventures(): List<Pair<String, List<SceneContent>>> =
        scenes.groupBy { links(it.item.identity.id).adventure.ifBlank { "Sin aventura" } }
            .map { (name, list) -> name to list.sortedWith(compareBy({ links(it.item.identity.id).order ?: Int.MAX_VALUE }, { it.item.displayName.lowercase() })) }
            .sortedWith(compareBy({ it.first == "Sin aventura" }, { it.first.lowercase() }))

    fun isDetour(scene: SceneContent): Boolean = links(scene.item.identity.id).detour

    /** «Por función»: places grouped by what they are for («Herrería», «Posada»…). */
    fun byFunction(): List<Pair<String, List<PlaceContent>>> =
        places.groupBy { it.payload.function.trim().ifBlank { if (it.payload.kind == PlaceKind.SHOP) "Tiendas" else "Otros lugares" } }
            .map { (name, list) -> name to list.sortedBy { it.item.displayName.lowercase() } }
            .sortedBy { it.first.lowercase() }

    fun groupNpcs(grouping: NpcGrouping, among: List<NpcContent> = npcs): List<Pair<String, List<NpcContent>>> {
        fun keys(npc: NpcContent): List<String> = when (grouping) {
            NpcGrouping.NONE -> listOf("")
            NpcGrouping.WHERE -> whereIs(npc).ifEmpty { listOf("Sin lugar") }
            NpcGrouping.SCENE -> scenesOf(npc).map { it.item.displayName }.ifEmpty { listOf("Sin escena") }
            NpcGrouping.GROUP -> links(npc.item.identity.id).groups.ifEmpty { listOfNotNull(npc.payload.affiliations.trim().takeIf { it.isNotEmpty() }) }.ifEmpty { listOf("Sin grupo") }
            NpcGrouping.ROLE -> listOf(npc.payload.conceptRole.trim().ifBlank { "Sin papel" })
        }
        return among.flatMap { npc -> keys(npc).map { it to npc } }
            .groupBy({ it.first }, { it.second })
            .map { (key, list) -> key to list.distinctBy { it.item.identity.id }.sortedBy { it.item.displayName.lowercase() } }
            .sortedWith(compareBy({ it.first.startsWith("Sin ") }, { it.first.lowercase() }))
    }

    val entries: List<SceneEntry> by lazy {
        npcs.map(::npcEntry) + places.map(::placeEntry) + zones.map(::zoneEntry) + scenes.map(::sceneEntry)
    }

    fun entry(id: Uuid): SceneEntry? = entries.firstOrNull { it.id == id }

    /**
     * Every word must appear somewhere in the entry (accents and capitals ignored). Name matches
     * come first. With no words, every entry of the filter, by name.
     */
    fun search(query: String, filter: SceneFilter = SceneFilter.ALL): List<SceneEntry> {
        val terms = normalizeForSearch(query).split(' ').filter { it.isNotEmpty() }
        val candidates = entries.filter { filter.accepts(it.kind) }
        if (terms.isEmpty()) return candidates.sortedBy { it.name.lowercase() }
        return candidates.filter { entry -> terms.all { it in entry.searchText } }
            .sortedWith(compareByDescending<SceneEntry> { entry -> terms.count { it in entry.nameText } }.thenBy { it.name.lowercase() })
    }

    private fun npcEntry(npc: NpcContent): SceneEntry {
        val p = npc.payload
        val l = links(npc.item.identity.id)
        val where = whereIs(npc)
        val linkedNames = (l.placeIds + l.zoneIds + l.sceneIds).mapNotNull(::nameOf) + l.groups + where
        return SceneEntry(
            id = npc.item.identity.id,
            kind = SceneEntryKind.NPC,
            name = npc.item.displayName,
            headline = p.conceptRole.ifBlank { "PNJ" },
            detail = firstSentence(p.appearanceFirstImpression.ifBlank { p.personalityManner }),
            where = where.joinToString(" · "),
            searchText = normalizeForSearch(
                listOf(npc.item.displayName, p.conceptRole, p.appearanceFirstImpression, p.personalityManner, p.relationshipContext, p.affiliations, p.places, p.adventureSceneLinks, p.canOffer, p.identityDetails)
                    .plus(linkedNames).joinToString(" "),
            ),
            nameText = normalizeForSearch(npc.item.displayName),
        )
    }

    private fun placeEntry(place: PlaceContent): SceneEntry {
        val p = place.payload
        val l = links(place.item.identity.id)
        val shop = p.kind == PlaceKind.SHOP
        return SceneEntry(
            id = place.item.identity.id,
            kind = if (shop) SceneEntryKind.SHOP else SceneEntryKind.PLACE,
            name = place.item.displayName,
            headline = p.function.ifBlank { p.summary.ifBlank { if (shop) "Tienda" else "Lugar" } },
            detail = if (shop) l.shopItems.filter { it.highlighted }.joinToString(" · ") { "⭐ ${it.name}" } else firstSentence(p.summary),
            where = pathOf(place.item.identity.id),
            searchText = normalizeForSearch(
                (listOf(place.item.displayName, p.summary, p.area, p.function, p.presentation) + p.services + p.tags + l.shopItems.map { it.name } + l.npcIds.mapNotNull(::nameOf) + listOfNotNull(zoneOf(place)?.item?.displayName))
                    .joinToString(" "),
            ),
            nameText = normalizeForSearch(place.item.displayName),
        )
    }

    private fun zoneEntry(zone: ZoneContent): SceneEntry {
        val p = zone.payload
        return SceneEntry(
            id = zone.item.identity.id,
            kind = SceneEntryKind.ZONE,
            name = zone.item.displayName,
            headline = p.summary.ifBlank { "Zona" },
            detail = firstSentence(p.presentation),
            where = p.area,
            searchText = normalizeForSearch((listOf(zone.item.displayName, p.summary, p.area, p.presentation) + p.tags).joinToString(" ")),
            nameText = normalizeForSearch(zone.item.displayName),
        )
    }

    private fun sceneEntry(scene: SceneContent): SceneEntry {
        val l = links(scene.item.identity.id)
        return SceneEntry(
            id = scene.item.identity.id,
            kind = SceneEntryKind.SCENE,
            name = scene.item.displayName,
            headline = scene.payload.purpose.ifBlank { "Escena" },
            detail = listOfNotNull(l.adventure.takeIf { it.isNotBlank() }, l.order?.let { "escena $it" }, if (l.detour) "desvío" else null).joinToString(" · "),
            where = (l.placeIds + l.zoneIds).map(::pathOf).filter { it.isNotBlank() }.joinToString(" · "),
            searchText = normalizeForSearch((listOf(scene.item.displayName, scene.payload.purpose, l.adventure, scene.payload.notes) + (l.placeIds + l.zoneIds + l.npcIds).mapNotNull(::nameOf)).joinToString(" ")),
            nameText = normalizeForSearch(scene.item.displayName),
        )
    }

    companion object {
        /** Loads the campaign's NPCs, places, zones, scenes, images and links. */
        fun load(database: AppDatabase, campaignId: Uuid): SceneIndex {
            val reusable = ReusableContentRepository(database)
            fun ids(family: ReusableContentFamily) = reusable.listCampaign(campaignId, family).map { it.identity.id }
            val npcRepo = NpcContentRepository(database, reusable)
            val placeRepo = PlaceContentRepository(database, reusable)
            val zoneRepo = ZoneContentRepository(database, reusable)
            val sceneRepo = SceneContentRepository(database, reusable)
            return SceneIndex(
                npcs = ids(ReusableContentFamily.NPC).mapNotNull { runCatching { npcRepo.npc(it) }.getOrNull() },
                places = ids(ReusableContentFamily.PLACE).mapNotNull { runCatching { placeRepo.place(it) }.getOrNull() },
                zones = ids(ReusableContentFamily.ZONE).mapNotNull { runCatching { zoneRepo.zone(it) }.getOrNull() },
                scenes = ids(ReusableContentFamily.SCENE).mapNotNull { runCatching { sceneRepo.scene(it) }.getOrNull() },
                handouts = HandoutContentRepository(database, reusable).listCampaign(campaignId),
                links = ContentLinksRepository(database).all(),
            )
        }

        private fun firstSentence(text: String): String {
            val trimmed = text.trim()
            val end = trimmed.indexOfFirst { it == '.' || it == '\n' }
            return (if (end in 1..140) trimmed.substring(0, end) else trimmed.take(140)).trim()
        }
    }
}
