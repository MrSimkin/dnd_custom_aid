package io.github.mrsimkin.dndcustomaid.shared.content

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDocument
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSnapshotCodec
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSnapshotDocument
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

/*
 * Owner, 2026-11-18 («D: copia de seguridad y exportar la campaña»): everything prepared lived in
 * a single file on the owner's computer, and the App could load a package but never save one.
 *
 * A copy holds what the campaign has, exactly as it is stored: every DM object with its content
 * and links, and every PC with its sheet. Restoring never overwrites: it adds the objects to the
 * open campaign as new ones, so a copy can also be used to move a campaign to another computer.
 */

const val CAMPAIGN_ARCHIVE_FORMAT: String = "dnd-custom-aid.campaign-archive"
const val CAMPAIGN_ARCHIVE_VERSION: Int = 1

@Serializable
data class CampaignArchive(
    val format: String = CAMPAIGN_ARCHIVE_FORMAT,
    val version: Int = CAMPAIGN_ARCHIVE_VERSION,
    val campaignName: String,
    val savedAtEpochSeconds: Long,
    /** Every DM object of the campaign, as it is stored. */
    val content: List<DmContentSnapshotDocument> = emptyList(),
    /** Every PC of the campaign with its sheet. */
    val characters: List<CharacterBackupDocument> = emptyList(),
) {
    /** What the copy holds, in the owner's words. */
    fun spanishSummary(): String {
        val families = content.groupingBy { it.content.family }.eachCount()
        val parts = buildList {
            families.forEach { (family, count) -> add("$count ${spanishFamily(family, count)}") }
            if (characters.isNotEmpty()) add("${characters.size} ${if (characters.size == 1) "personaje" else "personajes"}")
        }
        return if (parts.isEmpty()) "La campaña está vacía." else parts.joinToString(", ")
    }
}

/** What a restore did. */
data class CampaignArchiveReport(
    val restored: Int,
    val characters: Int,
    val problems: List<String>,
) {
    fun spanishSummary(): String = buildString {
        append(if (restored == 0 && characters == 0) "No había nada que recuperar." else "Recuperado: $restored objeto(s)")
        if (characters > 0) append(" y $characters personaje(s)")
        if (restored > 0 || characters > 0) append(".")
        if (problems.isNotEmpty()) append(" ${problems.size} no se pudieron recuperar: ${problems.take(3).joinToString("; ")}.")
    }
}

private val archiveJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    explicitNulls = false
    prettyPrint = true
}

fun parseCampaignArchive(text: String): CampaignArchive {
    val archive = archiveJson.decodeFromString(CampaignArchive.serializer(), text)
    require(archive.format == CAMPAIGN_ARCHIVE_FORMAT) { "Ese archivo no es una copia de seguridad de la App." }
    require(archive.version <= CAMPAIGN_ARCHIVE_VERSION) {
        "Esa copia viene de una versión más nueva de la App (${archive.version}). Actualiza antes de recuperarla."
    }
    return archive
}

fun writeCampaignArchive(archive: CampaignArchive): String =
    archiveJson.encodeToString(CampaignArchive.serializer(), archive)

class CampaignArchiveService(
    private val database: AppDatabase,
    private val codec: DmContentSnapshotCodec = DmContentSnapshotCodec(database),
    private val reusableContent: ReusableContentRepository = ReusableContentRepository(database),
    private val characters: CharacterRepository = CharacterRepository(database),
    private val backups: CharacterBackupRepository = CharacterBackupRepository(database),
) {
    /** Everything the campaign has right now. Deleted objects are not copied. */
    fun save(campaignId: Uuid, nowEpochSeconds: Long): CampaignArchive {
        val campaign = requireNotNull(CampaignRepository(database).campaign(campaignId)) {
            "Esa campaña ya no está en este aparato."
        }
        val content = reusableContent.listCampaign(campaignId)
            .mapNotNull { item -> codec.export(item.identity.id) }
        val sheets = characters.listCharacters(campaignId)
            .mapNotNull { sheet -> runCatching { backups.exportCharacter(sheet.id, nowEpochSeconds) }.getOrNull() }
        return CampaignArchive(
            campaignName = campaign.name,
            savedAtEpochSeconds = nowEpochSeconds,
            content = content,
            characters = sheets,
        )
    }

    /**
     * Puts the copy's objects into [campaignId] as new ones. Nothing that is already there is
     * touched, so recovering twice leaves two copies instead of losing work.
     */
    fun restoreInto(archive: CampaignArchive, campaignId: Uuid, nowEpochSeconds: Long): CampaignArchiveReport {
        require(nowEpochSeconds >= 0) { "Restore time must not be negative." }
        requireNotNull(CampaignRepository(database).campaign(campaignId)) { "Abre una campaña antes de recuperar." }
        val problems = mutableListOf<String>()
        var restored = 0
        var restoredCharacters = 0
        // Links inside the copy point at the old ids; every object gets a new one, and the links
        // are re-pointed so the campaign comes back whole.
        val newIds = archive.content.associate { it.content.id to Uuid.random() }

        database.transaction {
            archive.content.forEach { document ->
                val result = runCatching {
                    codec.applyHostedState(
                        document.copy(
                            content = document.content.copy(
                                id = newIds.getValue(document.content.id),
                                scope = io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentScopeDocument(kind = SCOPE_CAMPAIGN, ref = campaignId),
                                createdAtEpochSeconds = nowEpochSeconds,
                                updatedAtEpochSeconds = nowEpochSeconds,
                            ),
                            payload = repointPayload(document.payload, newIds),
                        ),
                        hostedDeletedAtEpochSeconds = null,
                    )
                }
                if (result.isSuccess) restored += 1 else problems += "${document.content.displayName}: ${result.exceptionOrNull()?.message}"
            }
            archive.characters.forEach { sheet ->
                val result = runCatching { backups.importAsCopy(sheet, campaignId, nowEpochSeconds) }
                if (result.isSuccess) restoredCharacters += 1 else problems += "${sheet.character.name}: ${result.exceptionOrNull()?.message}"
            }
        }
        return CampaignArchiveReport(restored, restoredCharacters, problems)
    }

    /** The links travel inside the payload; they must point at the objects of this copy. */
    private fun repointPayload(
        payload: kotlinx.serialization.json.JsonObject,
        newIds: Map<Uuid, Uuid>,
    ): kotlinx.serialization.json.JsonObject {
        var text = archiveJson.encodeToString(kotlinx.serialization.json.JsonObject.serializer(), payload)
        newIds.forEach { (old, new) -> text = text.replace(old.toString(), new.toString()) }
        return archiveJson.decodeFromString(kotlinx.serialization.json.JsonObject.serializer(), text)
    }
}

private const val SCOPE_CAMPAIGN = "CAMPAIGN"

private fun spanishFamily(family: String, count: Int): String = when (family) {
    "NPC" -> "PNJ"
    "PLACE" -> if (count == 1) "lugar" else "lugares"
    "SCENE" -> if (count == 1) "escena" else "escenas"
    "CREATURE" -> if (count == 1) "criatura" else "criaturas"
    "ENCOUNTER" -> if (count == 1) "encuentro" else "encuentros"
    "ZONE" -> if (count == 1) "zona" else "zonas"
    "DUNGEON" -> if (count == 1) "dungeon" else "dungeons"
    "HANDOUT" -> "handouts"
    "NOTE" -> if (count == 1) "nota" else "notas"
    "PACKAGE" -> if (count == 1) "paquete" else "paquetes"
    "HOMEBREW_RULE" -> if (count == 1) "regla de la casa" else "reglas de la casa"
    "RULES_ENTRY" -> if (count == 1) "regla" else "reglas"
    else -> family.lowercase()
}
