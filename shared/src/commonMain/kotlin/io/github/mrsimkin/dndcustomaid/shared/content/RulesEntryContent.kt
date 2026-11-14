package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/*
 * Redesign R4/R5 (owner observation G4, decisions D8, D9 and D13, 2026-11-13).
 *
 * Rules come from three separate origins, always offered in this order: the SRD (only the SRD),
 * the DM's homebrew (this family: their manuals, their spell tome, their gods, their legendary
 * weapons) and custom text typed on the sheet. One kind of entry covers every family of rules so
 * the library, the reader and the pickers of the PC editor all speak the same shape.
 */

@Serializable
enum class RulesEntryKind(val label: String, val plural: String) {
    SPECIES("Especie", "Especies"),
    SUBSPECIES("Subespecie o semirraza", "Subespecies y semirrazas"),
    CLASS("Clase", "Clases"),
    SUBCLASS("Subclase", "Subclases"),
    BACKGROUND("Trasfondo", "Trasfondos"),
    FEAT("Dote", "Dotes"),
    SPELL("Conjuro", "Conjuros"),
    WEAPON("Arma", "Armas"),
    ARMOR("Armadura", "Armaduras"),
    ITEM("Objeto", "Objetos"),
    MAGIC_ITEM("Objeto mágico", "Objetos mágicos"),
    LEGENDARY_ITEM("Arma u objeto de leyenda", "Armas y objetos de leyenda"),
    DEITY("Dios o fe", "Dioses y fe"),
    RULE("Regla", "Reglas"),
}

/** Where a rules entry comes from. The SRD and the homebrew are separate sources (D13). */
@Serializable
enum class RulesOrigin(val label: String) {
    SRD("SRD"),
    HOMEBREW("Homebrew"),
    CUSTOM("Custom"),
}

@Serializable
data class RulesEntryPayload(
    val kind: RulesEntryKind,
    /** «5e», «5.5e», or empty when it applies to both. */
    val edition: String = "",
    /** The class of a subclass, or the main species of a subspecies or half-race. */
    val parent: String = "",
    /** One line for lists and pickers. */
    val summary: String = "",
    /** The full text, as written in the source. */
    val body: String = "",
    /** Short data a picker or the sheet can use: nivel, escuela, daño, propiedades, precio, CA… */
    val fields: Map<String, String> = emptyMap(),
    /** The document it came from, e.g. «Manual Simkin 1.4, p. 120». */
    val source: String = "",
    val tags: List<String> = emptyList(),
)

@Serializable
data class RulesEntryContent(
    val item: ReusableContentItem,
    val payload: RulesEntryPayload,
) {
    init {
        require(item.family == ReusableContentFamily.RULES_ENTRY) { "RulesEntryContent must wrap RULES_ENTRY reusable content." }
    }
}

/** The DM's homebrew rules library: personal (every campaign of the DM) or of one campaign. */
class RulesEntryContentRepository(
    private val database: AppDatabase,
    private val reusableContent: ReusableContentRepository = ReusableContentRepository(database),
) {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun createPersonal(ownerAccountId: Uuid, name: String, payload: RulesEntryPayload, nowEpochSeconds: Long, id: Uuid = Uuid.random()): RulesEntryContent {
        require(name.isNotBlank()) { "A rules entry needs a name." }
        var result: RulesEntryContent? = null
        database.transaction {
            val item = reusableContent.createPersonal(ownerAccountId, ReusableContentFamily.RULES_ENTRY, name.trim(), nowEpochSeconds, id)
            insertPayload(item.identity.id, payload)
            result = RulesEntryContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun createCampaign(campaignId: Uuid, name: String, payload: RulesEntryPayload, nowEpochSeconds: Long, id: Uuid = Uuid.random()): RulesEntryContent {
        require(name.isNotBlank()) { "A rules entry needs a name." }
        var result: RulesEntryContent? = null
        database.transaction {
            val item = reusableContent.createCampaign(campaignId, ReusableContentFamily.RULES_ENTRY, name.trim(), nowEpochSeconds, id)
            insertPayload(item.identity.id, payload)
            result = RulesEntryContent(item, payload)
        }
        return checkNotNull(result)
    }

    fun entry(id: Uuid, includeDeleted: Boolean = false): RulesEntryContent? {
        val item = reusableContent.content(id, includeDeleted) ?: return null
        if (item.family != ReusableContentFamily.RULES_ENTRY) return null
        return payload(id)?.let { RulesEntryContent(item, it) }
    }

    fun listPersonal(ownerAccountId: Uuid, kind: RulesEntryKind? = null): List<RulesEntryContent> =
        reusableContent.listPersonal(ownerAccountId, ReusableContentFamily.RULES_ENTRY)
            .mapNotNull { entry(it.identity.id) }
            .filter { kind == null || it.payload.kind == kind }
            .sortedBy { it.item.displayName.lowercase() }

    fun listCampaign(campaignId: Uuid, kind: RulesEntryKind? = null): List<RulesEntryContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.RULES_ENTRY)
            .mapNotNull { entry(it.identity.id) }
            .filter { kind == null || it.payload.kind == kind }
            .sortedBy { it.item.displayName.lowercase() }

    fun update(id: Uuid, expectedRevision: Revision, name: String, payload: RulesEntryPayload, nowEpochSeconds: Long): RevisionDecision {
        require(name.isNotBlank()) { "A rules entry needs a name." }
        return reusableContent.mutateContent(id, expectedRevision, nowEpochSeconds) { current ->
            require(current.family == ReusableContentFamily.RULES_ENTRY) { "Rules entry updates require RULES_ENTRY reusable content." }
            database.reusableContentQueries.updateReusableContentName(name.trim(), nowEpochSeconds, id.toString())
            updatePayloadRow(id, payload)
        }
    }

    fun tombstone(id: Uuid, expectedRevision: Revision, deletedAtEpochSeconds: Long): RevisionDecision {
        val current = requireNotNull(reusableContent.content(id, includeDeleted = true)) { "Rules entry must exist before it can be deleted." }
        require(current.family == ReusableContentFamily.RULES_ENTRY) { "Rules entry deletion requires RULES_ENTRY reusable content." }
        return reusableContent.tombstone(id, expectedRevision, deletedAtEpochSeconds)
    }

    internal fun payload(id: Uuid): RulesEntryPayload? =
        database.rulesEntryPayloadQueries.selectRulesEntryPayloadByContentId(id.toString()).executeAsOneOrNull()
            ?.let { runCatching { json.decodeFromString(RulesEntryPayload.serializer(), it) }.getOrNull() }

    internal fun insertPayload(id: Uuid, payload: RulesEntryPayload) {
        database.rulesEntryPayloadQueries.insertRulesEntryPayload(id.toString(), json.encodeToString(RulesEntryPayload.serializer(), payload))
    }

    internal fun updatePayloadRow(id: Uuid, payload: RulesEntryPayload) {
        if (payload(id) == null) {
            insertPayload(id, payload)
        } else {
            database.rulesEntryPayloadQueries.updateRulesEntryPayload(json.encodeToString(RulesEntryPayload.serializer(), payload), id.toString())
        }
    }
}
