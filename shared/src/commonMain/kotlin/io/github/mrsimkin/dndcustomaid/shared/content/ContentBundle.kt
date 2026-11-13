package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPlayerRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/*
 * Redesign R7 (owner decisions D10 to D14, 2026-11-13): a content bundle loads a whole prepared
 * campaign at once (places and shops, NPCs, scenes, dungeons with their zones and passages,
 * encounters, handouts, notes, homebrew rules and the party), so the QA runs on the DM's real
 * material. The bundle is a plain JSON file kept by the DM; it never ships with the App.
 *
 * Every object gets a stable id from the campaign and its key: loading the same bundle twice, or
 * on two devices, adds nothing new.
 */

@Serializable
data class BundleShopItem(val name: String, val price: String = "", val note: String = "", val highlighted: Boolean = false)

@Serializable
data class BundlePlace(
    val key: String,
    val name: String,
    val payload: PlacePayload = PlacePayload(),
    /** The district or bigger place it belongs to. */
    val parentKey: String? = null,
    val shopItems: List<BundleShopItem> = emptyList(),
    /** Who works or lives there. */
    val npcKeys: List<String> = emptyList(),
)

@Serializable
data class BundleNpc(
    val key: String,
    val name: String,
    val payload: NpcPayload = NpcPayload(),
    val groups: List<String> = emptyList(),
    val placeKeys: List<String> = emptyList(),
)

@Serializable
data class BundleZone(
    val key: String,
    val name: String,
    val payload: ZonePayload = ZonePayload(),
    val x: Double? = null,
    val y: Double? = null,
    val encounterKeys: List<String> = emptyList(),
)

@Serializable
data class BundlePassage(val from: String, val to: String, val secret: Boolean = false)

@Serializable
data class BundleDungeon(
    val key: String,
    val name: String,
    val payload: DungeonPayload = DungeonPayload(),
    val zones: List<BundleZone> = emptyList(),
    val passages: List<BundlePassage> = emptyList(),
)

@Serializable
data class BundleScene(
    val key: String,
    val name: String,
    val payload: ScenePayload = ScenePayload(),
    val adventure: String = "",
    val order: Int? = null,
    val detour: Boolean = false,
    val placeKeys: List<String> = emptyList(),
    val npcKeys: List<String> = emptyList(),
    val nextKeys: List<String> = emptyList(),
)

@Serializable
data class BundleEncounter(val key: String, val name: String, val payload: EncounterPayload = EncounterPayload())

@Serializable
data class BundleHandout(
    val key: String,
    val name: String,
    /** The image file, relative to the bundle file. */
    val file: String,
    val kind: HandoutKind = HandoutKind.HANDOUT,
    val visibility: HandoutVisibility = HandoutVisibility.DM_ONLY,
    val caption: String = "",
    val notes: String = "",
)

@Serializable
data class BundleNote(val key: String, val text: String)

@Serializable
data class BundleRule(val key: String, val name: String, val payload: RulesEntryPayload)

@Serializable
data class BundleClass(val name: String, val level: Int, val subclass: String? = null, val hitDie: Int = 8, val edition: String = "")

@Serializable
data class BundleCharacter(
    val key: String,
    val name: String,
    val player: String? = null,
    val species: String = "",
    val background: String = "",
    val classes: List<BundleClass> = emptyList(),
    val story: String = "",
)

@Serializable
data class ContentBundle(
    val format: String = "dnd-custom-aid.content-bundle",
    val version: Int = 1,
    val title: String = "",
    val places: List<BundlePlace> = emptyList(),
    val npcs: List<BundleNpc> = emptyList(),
    val dungeons: List<BundleDungeon> = emptyList(),
    val scenes: List<BundleScene> = emptyList(),
    val encounters: List<BundleEncounter> = emptyList(),
    val handouts: List<BundleHandout> = emptyList(),
    val notes: List<BundleNote> = emptyList(),
    val rules: List<BundleRule> = emptyList(),
    val characters: List<BundleCharacter> = emptyList(),
)

/** What a load did, per family. */
data class ContentBundleReport(
    val created: Map<String, Int>,
    val alreadyThere: Int,
    val problems: List<String>,
) {
    fun spanishSummary(): String {
        val parts = created.filterValues { it > 0 }.map { (family, count) -> "$count $family" }
        return buildString {
            append(if (parts.isEmpty()) "No había nada nuevo que cargar." else "Cargado: ${parts.joinToString(", ")}.")
            if (alreadyThere > 0) append(" $alreadyThere ya estaban.")
            if (problems.isNotEmpty()) append(" ${problems.size} no se pudieron cargar: ${problems.take(3).joinToString("; ")}.")
        }
    }
}

/** Which parts of a bundle to load: the DM's content (Windows) and/or the party (tablet and Windows). */
enum class ContentBundleParts { DM_CONTENT, CHARACTERS, ALL }

private val bundleJson = Json { ignoreUnknownKeys = true }

fun parseContentBundle(json: String): ContentBundle = bundleJson.decodeFromString(ContentBundle.serializer(), json)

/** A stable id from the campaign and a key, the same on every device. */
fun stableContentId(campaignId: Uuid, key: String): Uuid = legacyDungeonId(campaignId, "bundle:$key")

class ContentBundleImporter(
    private val database: AppDatabase,
    private val mediaBlobs: MediaBlobStore?,
    private val nowEpochSeconds: () -> Long,
) {
    private val reusable = ReusableContentRepository(database)
    private val links = ContentLinksRepository(database)

    fun load(
        bundle: ContentBundle,
        campaignId: Uuid,
        parts: ContentBundleParts,
        /** The DM's account: homebrew goes to their personal library when it exists here. */
        ownerAccountId: Uuid?,
        /** Reads an image named in the bundle; null when missing. */
        readFile: (String) -> ByteArray?,
    ): ContentBundleReport {
        val created = linkedMapOf<String, Int>()
        var already = 0
        val problems = mutableListOf<String>()
        val now = nowEpochSeconds()
        fun id(key: String) = stableContentId(campaignId, key)
        fun exists(key: String) = reusable.content(id(key), includeDeleted = true) != null
        fun count(family: String) { created[family] = (created[family] ?: 0) + 1 }
        fun attempt(label: String, block: () -> Unit) {
            runCatching(block).onFailure { problems += "$label (${it.message ?: it::class.simpleName})" }
        }

        if (parts != ContentBundleParts.CHARACTERS) {
            val places = PlaceContentRepository(database, reusable)
            val npcs = NpcContentRepository(database, reusable)
            val scenes = SceneContentRepository(database, reusable)
            val encounters = EncounterContentRepository(database, reusable)
            val dungeons = DungeonContentRepository(database, reusable)
            val handouts = HandoutContentRepository(database, reusable)
            val notes = NoteContentRepository(database, reusable)
            val rules = RulesEntryContentRepository(database, reusable)

            bundle.places.forEach { place ->
                if (exists(place.key)) already++ else attempt(place.name) { places.createCampaign(campaignId, place.name, place.payload, now, id(place.key)); count("lugares") }
            }
            bundle.npcs.forEach { npc ->
                if (exists(npc.key)) already++ else attempt(npc.name) { npcs.createCampaign(campaignId, npc.name, npc.payload, now, id(npc.key)); count("PNJ") }
            }
            bundle.encounters.forEach { encounter ->
                if (exists(encounter.key)) already++ else attempt(encounter.name) { encounters.createCampaign(campaignId, encounter.name, encounter.payload, now, id(encounter.key)); count("encuentros") }
            }
            bundle.scenes.forEach { scene ->
                if (exists(scene.key)) already++ else attempt(scene.name) { scenes.createCampaign(campaignId, scene.name, scene.payload, now, id(scene.key)); count("escenas") }
            }
            bundle.dungeons.forEach { dungeon ->
                if (exists(dungeon.key)) {
                    already++
                    return@forEach
                }
                attempt(dungeon.name) {
                    val created1 = dungeons.create(campaignId, dungeon.name, dungeon.payload, now, id(dungeon.key))
                    count("dungeons")
                    val zones = ZoneContentRepository(database, reusable)
                    dungeon.zones.forEachIndexed { index, zone ->
                        zones.createCampaign(campaignId, zone.name, zone.payload, now, id(zone.key))
                        links.save(
                            id(zone.key),
                            ContentLinks(
                                dungeon = created1.item.displayName,
                                dungeonId = created1.item.identity.id,
                                diagramX = zone.x ?: (0.1 + (index % 4) * 0.25),
                                diagramY = zone.y ?: (0.1 + (index / 4) * 0.25).coerceAtMost(0.95),
                                encounterIds = zone.encounterKeys.map(::id).filter { reusable.content(it) != null },
                            ),
                        )
                        count("zonas")
                    }
                    dungeon.passages.forEach { passage -> dungeons.connect(id(passage.from), id(passage.to), passage.secret) }
                }
            }
            bundle.handouts.forEach { handout ->
                if (exists(handout.key)) {
                    already++
                    return@forEach
                }
                attempt(handout.name) {
                    val store = requireNotNull(mediaBlobs) { "no hay almacén de imágenes" }
                    val bytes = requireNotNull(readFile(handout.file)) { "no se encontró ${handout.file}" }
                    val mediaType = when (handout.file.substringAfterLast('.').lowercase()) {
                        "png" -> "image/png"
                        "webp" -> "image/webp"
                        else -> "image/jpeg"
                    }
                    val sha = store.write(bytes)
                    handouts.createCampaign(
                        campaignId,
                        handout.name,
                        HandoutPayload(
                            kind = handout.kind,
                            mediaType = mediaType,
                            sizeBytes = bytes.size.toLong(),
                            sha256 = sha,
                            visibility = handout.visibility,
                            caption = handout.caption,
                            notes = handout.notes,
                        ),
                        now,
                        id(handout.key),
                    )
                    count("handouts")
                }
            }
            bundle.notes.forEach { note ->
                if (exists(note.key)) already++ else attempt(note.key) {
                    notes.create(campaignId, NotePayload(note.text, NoteContextKind.CAMPAIGN, null, ""), now, id(note.key))
                    count("notas")
                }
            }
            val personalOwner = ownerAccountId?.takeIf { IntegratedSpineRepository(database).account(it) != null }
            bundle.rules.forEach { rule ->
                val ruleId = if (personalOwner != null) stableContentId(personalOwner, rule.key) else id(rule.key)
                if (reusable.content(ruleId, includeDeleted = true) != null) {
                    already++
                } else {
                    attempt(rule.name) {
                        if (personalOwner != null) {
                            rules.createPersonal(personalOwner, rule.name, rule.payload, now, ruleId)
                        } else {
                            rules.createCampaign(campaignId, rule.name, rule.payload, now, ruleId)
                        }
                        count("reglas homebrew")
                    }
                }
            }

            // Links last, once every object exists.
            bundle.places.forEach { place ->
                val current = links.links(id(place.key))
                links.save(
                    id(place.key),
                    current.copy(
                        placeIds = (current.placeIds + listOfNotNull(place.parentKey?.let(::id))).distinct(),
                        npcIds = (current.npcIds + place.npcKeys.map(::id)).distinct(),
                        shopItems = current.shopItems.ifEmpty { place.shopItems.map { ShopItem(it.name, it.price, it.highlighted, it.note) } },
                    ),
                )
            }
            bundle.npcs.forEach { npc ->
                val current = links.links(id(npc.key))
                links.save(
                    id(npc.key),
                    current.copy(
                        groups = (current.groups + npc.groups).distinct(),
                        placeIds = (current.placeIds + npc.placeKeys.map(::id)).distinct(),
                    ),
                )
            }
            bundle.scenes.forEach { scene ->
                val current = links.links(id(scene.key))
                links.save(
                    id(scene.key),
                    current.copy(
                        adventure = current.adventure.ifBlank { scene.adventure },
                        order = current.order ?: scene.order,
                        detour = current.detour || scene.detour,
                        placeIds = (current.placeIds + scene.placeKeys.map(::id)).distinct(),
                        npcIds = (current.npcIds + scene.npcKeys.map(::id)).distinct(),
                        sceneIds = (current.sceneIds + scene.nextKeys.map(::id)).distinct(),
                    ),
                )
            }
        }

        if (parts != ContentBundleParts.DM_CONTENT) {
            val characters = CharacterRepository(database)
            val players = CharacterPlayerRepository(database)
            val existing = characters.listCharacters(campaignId).associateBy { it.name.lowercase() }
            bundle.characters.forEach { pc ->
                if (pc.name.lowercase() in existing) {
                    already++
                    existing.getValue(pc.name.lowercase()).let { found -> if (players.player(found.id) == null) players.setPlayer(found.id, pc.player) }
                    return@forEach
                }
                attempt(pc.name) {
                    val sheet = characters.createCharacter(campaignId, pc.name)
                    val classes = pc.classes.mapIndexed { index, c ->
                        val family = when (c.edition) {
                            "5e" -> CharacterRulesFamily.DND_5E
                            "5.5e" -> CharacterRulesFamily.DND_5_5E
                            else -> CharacterRulesFamily.UNSPECIFIED
                        }
                        CharacterClassLevel(
                            id = Uuid.random(),
                            name = c.name,
                            level = c.level,
                            hitDieSides = c.hitDie,
                            hitDiceRemaining = c.level,
                            sortOrder = index,
                            rulesFamily = family,
                            subclassName = c.subclass,
                            subclassRulesFamily = family,
                        )
                    }
                    characters.saveCharacter(
                        sheet.copy(
                            classes = classes,
                            background = CharacterBackground(name = pc.background, race = pc.species, story = pc.story),
                        ),
                    )
                    players.setPlayer(sheet.id, pc.player)
                    count("PJ")
                }
            }
        }
        return ContentBundleReport(created, already, problems)
    }
}
