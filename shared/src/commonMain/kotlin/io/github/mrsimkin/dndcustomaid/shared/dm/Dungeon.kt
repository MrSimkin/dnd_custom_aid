package io.github.mrsimkin.dndcustomaid.shared.dm

import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContent
import io.github.mrsimkin.dndcustomaid.shared.content.ZonePassage
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/*
 * Base para DM, package 7: Mesa de Dungeon (approved design §5; P16-P18, P20-P23; beta rule notes
 * docs/discovery/2026-09-10_DUNGEON_TURNS_BETA_SOURCE_NOTES.md; D-0068).
 *
 * One live document per dungeon being explored, kept on the DM's device like a combat. The App
 * keeps track and never adjudicates: it does not compute zones or movement (advance is shown only
 * as information), does not apply consequences when a clock fills, and nothing moves by itself.
 */

/** How far the group may move in one Dungeon Turn; the slowest Activity sets it (shown, never applied). */
@Serializable
enum class DungeonAdvance(val label: String, val zones: Int) {
    FAST("Avance rápido", 3),
    NORMAL("Avance normal", 2),
    REDUCED("Avance reducido", 1),
    NONE("Sin avance", 0),
}

/** An Activity of the Dungeon Turn (P16, P17). The list is DM rules content: adjustable in the App de PC. */
@Serializable
data class DungeonActivity(
    val key: String,
    val name: String,
    val advance: DungeonAdvance,
    val cost: String = "",
    val benefit: String = "",
    /** Additions the owner approved on top of the beta (P17), shown with «*». */
    val starred: Boolean = false,
    /** «Buscar»: the player names a focus. */
    val needsFocus: Boolean = false,
    /** «Buscar a fondo» costs no advance instead of reduced advance. */
    val thoroughAdvance: DungeonAdvance? = null,
    /** «Ayudar» takes the advance of the helped Activity. */
    val helps: Boolean = false,
    /** «Emboscar»: a successful ambush may open combat with a surprise round (P22). */
    val ambush: Boolean = false,
) {
    val displayName: String
        get() = if (starred) "$name *" else name
}

object DungeonRules {
    /** The 15 Activities of the beta draft plus «Exploración normal *» and «Ocultarse *» (P17). */
    val betaActivities: List<DungeonActivity> = listOf(
        DungeonActivity("avanzar", "Avanzar", DungeonAdvance.FAST, "Sin otra Actividad exigente", "Avanza hasta 3 zonas."),
        DungeonActivity("exploracion_normal", "Exploración normal", DungeonAdvance.NORMAL, "Sin precaución especial", "Avanza hasta 2 zonas.", starred = true),
        DungeonActivity("ayudar", "Ayudar", DungeonAdvance.NORMAL, "Mismo costo que la Actividad ayudada", "Da ventaja a una prueba de otra criatura.", helps = true),
        DungeonActivity("buscar", "Buscar", DungeonAdvance.REDUCED, "Avance reducido (o sin avance si es a fondo) + prueba", "Encuentra indicios según el foco elegido.", needsFocus = true, thoroughAdvance = DungeonAdvance.NONE),
        DungeonActivity("descansar", "Descansar", DungeonAdvance.NONE, "Sin avance", "6 turnos seguidos: descanso corto; 48: descanso largo."),
        DungeonActivity("emboscar", "Emboscar", DungeonAdvance.NONE, "Sin avance + prueba", "Ventaja en iniciativa o empezar oculto; si sale bien, ronda sorpresa.", ambush = true),
        DungeonActivity("examinar", "Examinar objeto", DungeonAdvance.NONE, "Sin avance + prueba", "Información sobre un objeto o indicio."),
        DungeonActivity("interactuar", "Interactuar", DungeonAdvance.NONE, "Sin avance si exige tiempo o cuidado", "Manipula un objeto o el entorno."),
        DungeonActivity("conjuro", "Lanzar un conjuro", DungeonAdvance.NONE, "Sin avance + gasto normal", "El conjuro produce sus efectos."),
        DungeonActivity("mecanismo", "Manipular mecanismo", DungeonAdvance.NONE, "Sin avance + prueba", "Abre, bloquea, altera o desarma un mecanismo o trampa."),
        DungeonActivity("guardia", "Montar guardia", DungeonAdvance.REDUCED, "Avance reducido + prueba", "Alerta temprana o no ser sorprendido."),
        DungeonActivity("silencio", "Moverse en silencio", DungeonAdvance.REDUCED, "Avance reducido + prueba de Sigilo", "Evita ser detectado."),
        DungeonActivity("ocultarse", "Ocultarse", DungeonAdvance.NONE, "Sin avance + prueba de Sigilo", "Queda oculto en el lugar.", starred = true),
        DungeonActivity("defensa", "Preparar defensa", DungeonAdvance.NONE, "Sin avance + materiales", "Monta una defensa o trampa improvisada."),
        DungeonActivity("aliento", "Recuperar aliento", DungeonAdvance.NONE, "Sin avance + 1 Dado de Golpe", "Recupera PG sin descanso corto (beta)."),
        DungeonActivity("registrar", "Registrar", DungeonAdvance.NONE, "Sin avance", "Recoge objetos evidentes y botín visible."),
        DungeonActivity("heridas", "Tratar heridas", DungeonAdvance.NONE, "Sin avance + prueba si corresponde", "Estabiliza, diagnostica o ayuda a Recuperar aliento (beta)."),
    )

    /** Search focuses of «Buscar» in the beta. */
    val searchFocuses: List<String> = listOf("Peligros físicos", "Señales sobrenaturales", "Secretos", "Rastros", "Objetos ocultos")
}

/** One-tap marks of a zone (P18; design §5.2). */
@Serializable
enum class ZoneMark(val label: String, val icon: String) {
    EXPLORED("Explorada", "✓"),
    LOOTED("Saqueada", "💰"),
    CLEARED("Despejada", "🧹"),
    DANGER("Peligro activo", "⚠"),
    BLOCKED("Bloqueada", "🔒"),
    SECRET("Secreto hallado", "🗝"),
}

/** The party or one of its parts after «Separar grupo» (P16). */
@Serializable
data class PartyGroup(
    val id: Uuid,
    val name: String,
    val zoneId: Uuid? = null,
    val characterIds: List<Uuid> = emptyList(),
)

/** Creatures placed by hand on the map (🔴 with their count; P23). */
@Serializable
data class CreatureMarker(
    val id: Uuid,
    val label: String,
    val count: Int = 1,
    val zoneId: Uuid? = null,
)

/** One character's Activity for the current turn. */
@Serializable
data class ActivityDeclaration(
    val characterId: Uuid,
    val activityKey: String,
    val focus: String = "",
    val thorough: Boolean = false,
    val helpsCharacterId: Uuid? = null,
    val note: String = "",
)

@Serializable
data class GroupTurnRecord(
    val groupName: String,
    val zoneName: String = "",
    val advance: DungeonAdvance,
    /** «Nombre: Actividad (foco)», in party order. */
    val activities: List<String>,
)

/** The automatic Dungeon Turn log (P16). */
@Serializable
data class DungeonTurnRecord(
    val turn: Int,
    val groups: List<GroupTurnRecord>,
    val interruptedByCombat: Boolean = false,
    /** With an interruption: who had completed their Activity. */
    val completed: List<String> = emptyList(),
    val clocksFilled: List<String> = emptyList(),
)

/** A clock (P20): name and segments; +1 per Dungeon Turn when it says so, or by hand. Warns when full. */
@Serializable
data class DungeonClock(
    val id: Uuid,
    val name: String,
    val segments: Int,
    val filled: Int = 0,
    val perTurn: Boolean = true,
) {
    init {
        require(name.isNotBlank()) { "A clock needs a name." }
        require(segments in 2..24) { "A clock has between 2 and 24 segments." }
        require(filled in 0..segments) { "A clock cannot be over-filled." }
    }

    val isFull: Boolean
        get() = filled >= segments
}

@Serializable
data class DungeonDocument(
    val format: String = "dnd-custom-aid.dm-dungeon",
    val version: Int = 1,
    val id: Uuid,
    val campaignId: Uuid,
    /** The dungeon being explored: the «Dungeon» name its zones share (set in the App de PC). */
    val dungeon: String,
    val turn: Int = 1,
    val zoneMarks: Map<String, List<ZoneMark>> = emptyMap(),
    val groups: List<PartyGroup> = emptyList(),
    val creatures: List<CreatureMarker> = emptyList(),
    val declarations: List<ActivityDeclaration> = emptyList(),
    /** Last turn's Activity per character: shown as reference, never carried over (P16). */
    val previous: List<ActivityDeclaration> = emptyList(),
    val log: List<DungeonTurnRecord> = emptyList(),
    val clocks: List<DungeonClock> = emptyList(),
    val ended: Boolean = false,
    val createdAtEpochSeconds: Long,
    val updatedAtEpochSeconds: Long,
) {
    fun marks(zoneId: Uuid): List<ZoneMark> = zoneMarks[zoneId.toString()].orEmpty()

    fun declaration(characterId: Uuid): ActivityDeclaration? = declarations.firstOrNull { it.characterId == characterId }

    fun previousOf(characterId: Uuid): ActivityDeclaration? = previous.firstOrNull { it.characterId == characterId }

    fun groupOf(characterId: Uuid): PartyGroup? = groups.firstOrNull { characterId in it.characterIds }
}

/** The result of «Cerrar turno»: the new document, clocks that just filled, and ambushers to ask about (P22). */
data class DungeonTurnClosed(
    val document: DungeonDocument,
    val clocksFilled: List<DungeonClock>,
    val ambusherIds: List<Uuid>,
)

/** Pure operations on a dungeon document; every call returns a new document. */
object DungeonEngine {
    fun new(campaignId: Uuid, dungeon: String, characters: List<Pair<Uuid, String>>, startZoneId: Uuid?, nowEpochSeconds: Long, id: Uuid = Uuid.random()) =
        DungeonDocument(
            id = id,
            campaignId = campaignId,
            dungeon = dungeon.trim().ifBlank { "Dungeon" },
            groups = listOf(PartyGroup(Uuid.random(), "Grupo", startZoneId, characters.map { it.first })),
            createdAtEpochSeconds = nowEpochSeconds,
            updatedAtEpochSeconds = nowEpochSeconds,
        )

    fun activity(activities: List<DungeonActivity>, key: String): DungeonActivity? = activities.firstOrNull { it.key == key }

    fun declare(doc: DungeonDocument, declaration: ActivityDeclaration): DungeonDocument =
        doc.copy(declarations = doc.declarations.filter { it.characterId != declaration.characterId } + declaration)

    fun clearDeclaration(doc: DungeonDocument, characterId: Uuid): DungeonDocument =
        doc.copy(declarations = doc.declarations.filter { it.characterId != characterId })

    /** The advance a declaration costs; «Ayudar» takes the helped one's, «a fondo» its thorough cost. */
    fun advanceOf(doc: DungeonDocument, activities: List<DungeonActivity>, declaration: ActivityDeclaration, seen: Set<Uuid> = emptySet()): DungeonAdvance? {
        val activity = activity(activities, declaration.activityKey) ?: return null
        if (activity.helps) {
            val helped = declaration.helpsCharacterId?.takeIf { it !in seen }?.let(doc::declaration)
            return helped?.let { advanceOf(doc, activities, it, seen + declaration.characterId) } ?: activity.advance
        }
        return if (declaration.thorough) activity.thoroughAdvance ?: activity.advance else activity.advance
    }

    /** The group's advance: the slowest declared Activity of its members (P16). Information only. */
    fun groupAdvance(doc: DungeonDocument, activities: List<DungeonActivity>, group: PartyGroup): DungeonAdvance? =
        group.characterIds.mapNotNull { doc.declaration(it) }.mapNotNull { advanceOf(doc, activities, it) }.maxByOrNull { it.ordinal }

    /** «Separar grupo»: the chosen characters form a new group in the same zone. */
    fun split(doc: DungeonDocument, fromGroupId: Uuid, characterIds: List<Uuid>, name: String): DungeonDocument {
        val from = doc.groups.firstOrNull { it.id == fromGroupId } ?: return doc
        val moving = characterIds.filter { it in from.characterIds }
        if (moving.isEmpty() || moving.size == from.characterIds.size) return doc
        val newGroup = PartyGroup(Uuid.random(), name.trim().ifBlank { "Grupo ${doc.groups.size + 1}" }, from.zoneId, moving)
        return doc.copy(groups = doc.groups.map { if (it.id == fromGroupId) it.copy(characterIds = it.characterIds - moving.toSet()) else it } + newGroup)
    }

    /** Joins a group back into another one. */
    fun merge(doc: DungeonDocument, groupId: Uuid, intoGroupId: Uuid): DungeonDocument {
        val group = doc.groups.firstOrNull { it.id == groupId } ?: return doc
        if (groupId == intoGroupId || doc.groups.none { it.id == intoGroupId }) return doc
        return doc.copy(groups = doc.groups.filter { it.id != groupId }.map { if (it.id == intoGroupId) it.copy(characterIds = it.characterIds + group.characterIds) else it })
    }

    /** Placed by hand (P23): the App never moves anyone by itself. */
    fun moveGroup(doc: DungeonDocument, groupId: Uuid, zoneId: Uuid?): DungeonDocument =
        doc.copy(groups = doc.groups.map { if (it.id == groupId) it.copy(zoneId = zoneId) else it })

    fun addCreatures(doc: DungeonDocument, label: String, count: Int, zoneId: Uuid?): DungeonDocument =
        if (label.isBlank()) doc else doc.copy(creatures = doc.creatures + CreatureMarker(Uuid.random(), label.trim(), count.coerceIn(1, 99), zoneId))

    fun moveCreatures(doc: DungeonDocument, markerId: Uuid, zoneId: Uuid?): DungeonDocument =
        doc.copy(creatures = doc.creatures.map { if (it.id == markerId) it.copy(zoneId = zoneId) else it })

    fun setCreatureCount(doc: DungeonDocument, markerId: Uuid, count: Int): DungeonDocument =
        if (count <= 0) doc.copy(creatures = doc.creatures.filter { it.id != markerId })
        else doc.copy(creatures = doc.creatures.map { if (it.id == markerId) it.copy(count = count.coerceAtMost(99)) else it })

    fun toggleMark(doc: DungeonDocument, zoneId: Uuid, mark: ZoneMark): DungeonDocument {
        val key = zoneId.toString()
        val now = doc.zoneMarks[key].orEmpty()
        val next = if (mark in now) now - mark else now + mark
        return doc.copy(zoneMarks = (doc.zoneMarks - key) + if (next.isEmpty()) emptyMap() else mapOf(key to next.sortedBy { it.ordinal }))
    }

    fun addClock(doc: DungeonDocument, name: String, segments: Int, perTurn: Boolean): DungeonDocument =
        doc.copy(clocks = doc.clocks + DungeonClock(Uuid.random(), name.trim(), segments, perTurn = perTurn))

    fun tickClock(doc: DungeonDocument, clockId: Uuid, delta: Int): DungeonDocument =
        doc.copy(clocks = doc.clocks.map { if (it.id == clockId) it.copy(filled = (it.filled + delta).coerceIn(0, it.segments)) else it })

    fun removeClock(doc: DungeonDocument, clockId: Uuid): DungeonDocument = doc.copy(clocks = doc.clocks.filter { it.id != clockId })

    /**
     * «Cerrar turno»: logs what each group declared and its advance, keeps the declarations only as
     * «anterior», advances «+1 por turno» clocks and reports the ones that just filled (nothing else
     * happens to them) and who ambushed, so the DM can be asked about a surprise round (P22).
     */
    fun closeTurn(
        doc: DungeonDocument,
        activities: List<DungeonActivity>,
        names: Map<Uuid, String>,
        zoneNames: Map<Uuid, String>,
    ): DungeonTurnClosed {
        val ticked = doc.clocks.map { if (it.perTurn && !it.isFull) it.copy(filled = it.filled + 1) else it }
        val filled = ticked.filter { clock -> clock.isFull && doc.clocks.first { it.id == clock.id }.filled < clock.segments }
        val record = DungeonTurnRecord(
            turn = doc.turn,
            groups = doc.groups.map { group -> groupRecord(doc, activities, names, zoneNames, group) },
            clocksFilled = filled.map { it.name },
        )
        val ambushers = doc.declarations.filter { activity(activities, it.activityKey)?.ambush == true }.map { it.characterId }
        val closed = doc.copy(
            turn = doc.turn + 1,
            previous = doc.declarations,
            declarations = emptyList(),
            log = doc.log + record,
            clocks = ticked,
        )
        return DungeonTurnClosed(closed, filled, ambushers)
    }

    /** «Interrumpido por combate»: logs the turn as interrupted and who had completed their Activity. */
    fun interruptByCombat(
        doc: DungeonDocument,
        activities: List<DungeonActivity>,
        names: Map<Uuid, String>,
        zoneNames: Map<Uuid, String>,
        completedIds: List<Uuid>,
    ): DungeonDocument {
        val record = DungeonTurnRecord(
            turn = doc.turn,
            groups = doc.groups.map { group -> groupRecord(doc, activities, names, zoneNames, group) },
            interruptedByCombat = true,
            completed = completedIds.mapNotNull(names::get),
        )
        return doc.copy(turn = doc.turn + 1, previous = doc.declarations, declarations = emptyList(), log = doc.log + record)
    }

    fun end(doc: DungeonDocument): DungeonDocument = doc.copy(ended = true)

    private fun groupRecord(
        doc: DungeonDocument,
        activities: List<DungeonActivity>,
        names: Map<Uuid, String>,
        zoneNames: Map<Uuid, String>,
        group: PartyGroup,
    ) = GroupTurnRecord(
        groupName = group.name,
        zoneName = group.zoneId?.let(zoneNames::get).orEmpty(),
        advance = groupAdvance(doc, activities, group) ?: DungeonAdvance.NONE,
        activities = group.characterIds.map { id ->
            val name = names[id] ?: "PJ"
            val declaration = doc.declaration(id)
            val activity = declaration?.let { activity(activities, it.activityKey) }
            if (declaration == null || activity == null) {
                "$name: sin actividad"
            } else {
                val detail = listOfNotNull(
                    declaration.focus.takeIf { it.isNotBlank() },
                    if (declaration.thorough) "a fondo" else null,
                    declaration.helpsCharacterId?.let(names::get)?.let { "a $it" },
                ).joinToString(", ")
                "$name: ${activity.displayName}" + if (detail.isNotBlank()) " ($detail)" else ""
            }
        },
    )
}

/** A zone on the dungeon diagram: where it sits (0-1) and its passages. */
data class DungeonMapZone(
    val zone: ZoneContent,
    val x: Double,
    val y: Double,
    val passages: List<ZonePassage>,
)

/** Dungeons of the campaign: the «Dungeon» names its zones share (set in the App de PC). */
fun SceneIndex.dungeons(): List<String> =
    zones.map { links(it.item.identity.id).dungeon }.filter { it.isNotBlank() }.distinctBy { it.lowercase() }.sortedBy { it.lowercase() }

/**
 * The diagram of one dungeon (P23). Zones placed in the App de PC keep their place; the others
 * are laid out in rows so the map is never empty. Passages point both ways.
 */
fun SceneIndex.dungeonMap(dungeon: String): List<DungeonMapZone> {
    val members = zones.filter { links(it.item.identity.id).dungeon.equals(dungeon, ignoreCase = true) }.sortedBy { it.item.displayName.lowercase() }
    val ids = members.map { it.item.identity.id }.toSet()
    val unplaced = members.filter { links(it.item.identity.id).diagramX == null || links(it.item.identity.id).diagramY == null }
    val columns = 4
    return members.map { zone ->
        val l = links(zone.item.identity.id)
        val slot = unplaced.indexOf(zone)
        val x = l.diagramX ?: (0.12 + (slot % columns) * 0.25)
        val y = l.diagramY ?: (0.15 + (slot / columns) * 0.3).coerceAtMost(0.9)
        val outgoing = l.passages.filter { it.toZoneId in ids }
        val incoming = members.flatMap { other ->
            links(other.item.identity.id).passages.filter { it.toZoneId == zone.item.identity.id }.map { it.copy(toZoneId = other.item.identity.id) }
        }
        DungeonMapZone(zone, x, y, (outgoing + incoming).distinctBy { it.toZoneId })
    }
}

/** The campaign's Activity list: a DM rule that defines one (P17), otherwise the beta list. */
fun dungeonActivities(database: AppDatabase, campaignId: Uuid): List<DungeonActivity> {
    val reusable = ReusableContentRepository(database)
    val links = ContentLinksRepository(database)
    return reusable.listCampaign(campaignId, ReusableContentFamily.HOMEBREW_RULE)
        .map { links.links(it.identity.id).activities }
        .firstOrNull { it.isNotEmpty() }
        ?: DungeonRules.betaActivities
}

/** Dungeon explorations of a campaign on this device. */
class DmDungeonRepository(private val database: AppDatabase) {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun list(campaignId: Uuid): List<DungeonDocument> =
        database.dmDungeonQueries.selectDungeonsByCampaign(campaignId.toString()).executeAsList()
            .mapNotNull { runCatching { json.decodeFromString(DungeonDocument.serializer(), it) }.getOrNull() }

    fun dungeon(id: Uuid): DungeonDocument? =
        database.dmDungeonQueries.selectDungeon(id.toString()).executeAsOneOrNull()
            ?.let { runCatching { json.decodeFromString(DungeonDocument.serializer(), it) }.getOrNull() }

    fun save(doc: DungeonDocument, nowEpochSeconds: Long): DungeonDocument {
        val stamped = doc.copy(updatedAtEpochSeconds = maxOf(nowEpochSeconds, doc.createdAtEpochSeconds))
        database.dmDungeonQueries.upsertDungeon(
            id = stamped.id.toString(),
            campaign_id = stamped.campaignId.toString(),
            ended = if (stamped.ended) 1L else 0L,
            updated_at_epoch_seconds = stamped.updatedAtEpochSeconds,
            document_json = json.encodeToString(DungeonDocument.serializer(), stamped),
        )
        return stamped
    }

    fun delete(id: Uuid) {
        database.dmDungeonQueries.deleteDungeon(id.toString())
    }
}
