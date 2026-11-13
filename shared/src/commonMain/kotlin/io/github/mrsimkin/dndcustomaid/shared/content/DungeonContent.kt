package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/*
 * Redesign R3 (owner observation P5, decision D5, 2026-11-13): a dungeon is its own object.
 *
 * Before, a dungeon existed only as the same «Dungeon» name typed in each zone, so there was no
 * dungeon to create and connecting zones meant opening each one. Now the DM creates a dungeon,
 * adds zones to it on a plan and drags passages between them. The zones keep the dungeon's name in
 * their links (so the Mesa de Dungeon of older builds keeps working) and also its id.
 */

@Serializable
data class DungeonPayload(
    /** What the dungeon is, for the DM, in one or two lines. */
    val summary: String = "",
    /** What the players may be told or shown. */
    val playerSafeText: String = "",
    /** An optional drawn plan shown behind the zones (a Map handout). */
    val backgroundHandoutId: Uuid? = null,
)

@Serializable
data class DungeonContent(
    val item: ReusableContentItem,
    val payload: DungeonPayload,
) {
    init {
        require(item.family == ReusableContentFamily.DUNGEON) { "DungeonContent must wrap DUNGEON reusable content." }
    }
}

/** A zone of a dungeon as the plan shows it. */
data class DungeonPlanZone(
    val zone: ReusableContentItem,
    val links: ContentLinks,
)

class DungeonContentRepository(
    private val database: AppDatabase,
    private val reusableContent: ReusableContentRepository = ReusableContentRepository(database),
    private val zones: ZoneContentRepository = ZoneContentRepository(database, reusableContent),
    private val linksRepository: ContentLinksRepository = ContentLinksRepository(database),
) {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun create(campaignId: Uuid, name: String, payload: DungeonPayload, nowEpochSeconds: Long, id: Uuid = Uuid.random()): DungeonContent {
        require(name.isNotBlank()) { "A dungeon needs a name." }
        var result: DungeonContent? = null
        database.transaction {
            val item = reusableContent.createCampaign(campaignId, ReusableContentFamily.DUNGEON, name.trim(), nowEpochSeconds, id)
            insertPayload(item.identity.id, payload)
            result = DungeonContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun dungeon(id: Uuid, includeDeleted: Boolean = false): DungeonContent? {
        val item = reusableContent.content(id, includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.DUNGEON) return null
        return DungeonContent(item, payload(id) ?: DungeonPayload())
    }

    fun listCampaign(campaignId: Uuid): List<DungeonContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.DUNGEON)
            .mapNotNull { dungeon(it.identity.id) }
            .sortedBy { it.item.displayName.lowercase() }

    /** The zones of one dungeon: those that carry its id, or (older data) its name. */
    fun zonesOf(campaignId: Uuid, dungeon: DungeonContent): List<DungeonPlanZone> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.ZONE).mapNotNull { zone ->
            val links = linksRepository.links(zone.identity.id)
            val belongs = links.dungeonId == dungeon.item.identity.id ||
                (links.dungeonId == null && links.dungeon.equals(dungeon.item.displayName, ignoreCase = true))
            if (belongs) DungeonPlanZone(zone, links) else null
        }.sortedBy { it.zone.displayName.lowercase() }

    /** Renames the dungeon and the name its zones carry. */
    fun update(id: Uuid, expectedRevision: Revision, name: String, payload: DungeonPayload, nowEpochSeconds: Long): RevisionDecision {
        require(name.isNotBlank()) { "A dungeon needs a name." }
        val current = requireNotNull(dungeon(id)) { "Dungeon must exist before it can change." }
        val campaignId = (current.item.identity.scope as? io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope.Campaign)?.campaignId
        val members = campaignId?.let { zonesOf(it, current) }.orEmpty()
        val decision = reusableContent.mutateContent(id, expectedRevision, nowEpochSeconds) {
            database.reusableContentQueries.updateReusableContentName(name.trim(), nowEpochSeconds, id.toString())
            updatePayloadRow(id, payload)
        }
        if (decision is RevisionDecision.Accepted) {
            members.forEach { member ->
                linksRepository.save(member.zone.identity.id, member.links.copy(dungeon = name.trim(), dungeonId = id))
            }
        }
        return decision
    }

    /** «＋ Zona»: a new zone already inside the dungeon, at a spot on the plan (0 to 1). */
    fun addZone(campaignId: Uuid, dungeon: DungeonContent, name: String, x: Double, y: Double, nowEpochSeconds: Long): ZoneContent {
        val zone = zones.createCampaign(campaignId, name.ifBlank { "Zona nueva" }, ZonePayload(), nowEpochSeconds)
        linksRepository.save(
            zone.item.identity.id,
            ContentLinks(dungeon = dungeon.item.displayName, dungeonId = dungeon.item.identity.id, diagramX = x, diagramY = y),
        )
        return zone
    }

    /** Dragging a zone on the plan. */
    fun moveZone(zoneId: Uuid, x: Double, y: Double) {
        val links = linksRepository.links(zoneId)
        linksRepository.save(zoneId, links.copy(diagramX = x.coerceIn(0.0, 1.0), diagramY = y.coerceIn(0.0, 1.0)))
    }

    /** Dragging from one zone to another: one passage, stored once, shown both ways. */
    fun connect(fromZoneId: Uuid, toZoneId: Uuid, secret: Boolean = false) {
        if (fromZoneId == toZoneId) return
        val back = linksRepository.links(toZoneId)
        if (back.passages.any { it.toZoneId == fromZoneId }) {
            linksRepository.save(toZoneId, back.copy(passages = back.passages.map { if (it.toZoneId == fromZoneId) it.copy(secret = secret) else it }))
            return
        }
        val links = linksRepository.links(fromZoneId)
        linksRepository.save(fromZoneId, links.copy(passages = links.passages.filter { it.toZoneId != toZoneId } + ZonePassage(toZoneId, secret)))
    }

    /** Marks or unmarks a passage as secret, whichever zone stores it. */
    fun setSecret(zoneA: Uuid, zoneB: Uuid, secret: Boolean) {
        listOf(zoneA to zoneB, zoneB to zoneA).forEach { (from, to) ->
            val links = linksRepository.links(from)
            if (links.passages.any { it.toZoneId == to }) {
                linksRepository.save(from, links.copy(passages = links.passages.map { if (it.toZoneId == to) it.copy(secret = secret) else it }))
            }
        }
    }

    /** Removes the passage between two zones, in both directions. */
    fun disconnect(zoneA: Uuid, zoneB: Uuid) {
        listOf(zoneA to zoneB, zoneB to zoneA).forEach { (from, to) ->
            val links = linksRepository.links(from)
            if (links.passages.any { it.toZoneId == to }) {
                linksRepository.save(from, links.copy(passages = links.passages.filter { it.toZoneId != to }))
            }
        }
    }

    /**
     * Older campaigns named their dungeon in each zone. Each such name becomes a dungeon object
     * once, with an id derived from the campaign and the name, so the App de PC and the tablet
     * create the very same object and the sync never doubles it.
     */
    fun adoptLegacyNames(campaignId: Uuid, nowEpochSeconds: Long): List<DungeonContent> {
        val existing = listCampaign(campaignId)
        val created = mutableListOf<DungeonContent>()
        reusableContent.listCampaign(campaignId, ReusableContentFamily.ZONE)
            .map { linksRepository.links(it.identity.id) }
            .filter { it.dungeonId == null && it.dungeon.isNotBlank() }
            .map { it.dungeon.trim() }
            .distinctBy { it.lowercase() }
            .forEach { name ->
                val match = (existing + created).firstOrNull { it.item.displayName.equals(name, ignoreCase = true) }
                val dungeon = match ?: run {
                    val id = legacyDungeonId(campaignId, name)
                    dungeon(id, includeDeleted = true)?.takeIf { !it.item.isDeleted }
                        ?: create(campaignId, name, DungeonPayload(), nowEpochSeconds, id).also { created += it }
                }
                reusableContent.listCampaign(campaignId, ReusableContentFamily.ZONE).forEach { zone ->
                    val links = linksRepository.links(zone.identity.id)
                    if (links.dungeonId == null && links.dungeon.equals(name, ignoreCase = true)) {
                        linksRepository.save(zone.identity.id, links.copy(dungeonId = dungeon.item.identity.id, dungeon = dungeon.item.displayName))
                    }
                }
            }
        return created
    }

    fun tombstone(id: Uuid, expectedRevision: Revision, deletedAtEpochSeconds: Long): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) { "Dungeon must exist before it can be deleted." }
        require(current.family == ReusableContentFamily.DUNGEON) { "Dungeon deletion requires DUNGEON reusable content." }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    internal fun payload(id: Uuid): DungeonPayload? =
        database.dungeonPayloadQueries.selectDungeonPayloadByContentId(id.toString()).executeAsOneOrNull()
            ?.let { runCatching { json.decodeFromString(DungeonPayload.serializer(), it) }.getOrNull() }

    internal fun insertPayload(id: Uuid, payload: DungeonPayload) {
        database.dungeonPayloadQueries.insertDungeonPayload(id.toString(), json.encodeToString(DungeonPayload.serializer(), payload))
    }

    internal fun updatePayloadRow(id: Uuid, payload: DungeonPayload) {
        if (payload(id) == null) {
            insertPayload(id, payload)
        } else {
            database.dungeonPayloadQueries.updateDungeonPayload(json.encodeToString(DungeonPayload.serializer(), payload), id.toString())
        }
    }
}

/** The same id on every device for the dungeon an older campaign named in its zones. */
fun legacyDungeonId(campaignId: Uuid, name: String): Uuid {
    val key = "${campaignId}|${name.trim().lowercase()}"
    fun fnv(seed: ULong): Long {
        var hash = seed
        key.encodeToByteArray().forEach { byte ->
            hash = hash xor byte.toUByte().toULong()
            hash *= 0x100000001b3uL
        }
        return hash.toLong()
    }
    val most = (fnv(0xcbf29ce484222325uL) and -0xf001L) or 0x4000L // version 4 layout
    val least = (fnv(0x84222325cbf29ce4uL) and 0x3fffffffffffffffL) or Long.MIN_VALUE // RFC 4122 variant
    return Uuid.fromLongs(most, least)
}
