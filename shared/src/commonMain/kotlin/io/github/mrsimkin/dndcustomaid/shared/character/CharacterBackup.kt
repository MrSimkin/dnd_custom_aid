package io.github.mrsimkin.dndcustomaid.shared.character

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.uuid.Uuid

const val CHARACTER_BACKUP_FORMAT = "dnd-custom-aid.character-backup"
const val CHARACTER_BACKUP_VERSION = 1

/**
 * Versioned, app-owned character backup. This is intentionally not a third-party import schema.
 * The payload joins the two existing authoritative character persistence aggregates rather than
 * introducing a second character model.
 */
@Serializable
data class CharacterBackupDocument(
    val format: String = CHARACTER_BACKUP_FORMAT,
    val version: Int = CHARACTER_BACKUP_VERSION,
    val exportedAtEpochSeconds: Long,
    val character: CharacterSheet,
    val closureState: CharacterClosureState,
)

enum class CharacterBackupErrorCode {
    EMPTY_INPUT,
    MALFORMED_JSON,
    WRONG_FORMAT,
    UNSUPPORTED_VERSION,
    INVALID_PAYLOAD,
}

data class CharacterBackupError(
    val code: CharacterBackupErrorCode,
    val message: String,
)

sealed interface CharacterBackupDecodeResult {
    data class Success(val document: CharacterBackupDocument) : CharacterBackupDecodeResult
    data class Failure(val error: CharacterBackupError) : CharacterBackupDecodeResult
}

object CharacterBackupCodec {
    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        explicitNulls = true
        ignoreUnknownKeys = true
    }

    fun encode(document: CharacterBackupDocument): String {
        val validation = characterBackupValidationMessage(document)
        require(validation == null) { validation ?: "Invalid character backup." }
        return json.encodeToString(document)
    }

    fun decode(raw: String): CharacterBackupDecodeResult {
        if (raw.isBlank()) {
            return CharacterBackupDecodeResult.Failure(
                CharacterBackupError(CharacterBackupErrorCode.EMPTY_INPUT, "El respaldo está vacío."),
            )
        }

        val root = try {
            json.parseToJsonElement(raw).jsonObject
        } catch (_: Exception) {
            return CharacterBackupDecodeResult.Failure(
                CharacterBackupError(CharacterBackupErrorCode.MALFORMED_JSON, "El archivo no contiene JSON válido."),
            )
        }

        val format = root["format"]?.jsonPrimitive?.content
        if (format != CHARACTER_BACKUP_FORMAT) {
            return CharacterBackupDecodeResult.Failure(
                CharacterBackupError(CharacterBackupErrorCode.WRONG_FORMAT, "El archivo no es un respaldo de personaje de D&D Custom Aid."),
            )
        }

        val version = root["version"]?.jsonPrimitive?.content?.toIntOrNull()
        if (version != CHARACTER_BACKUP_VERSION) {
            return CharacterBackupDecodeResult.Failure(
                CharacterBackupError(
                    CharacterBackupErrorCode.UNSUPPORTED_VERSION,
                    "Versión de respaldo no compatible: ${version ?: "desconocida"}.",
                ),
            )
        }

        val document = try {
            json.decodeFromString<CharacterBackupDocument>(raw)
        } catch (_: SerializationException) {
            return CharacterBackupDecodeResult.Failure(
                CharacterBackupError(CharacterBackupErrorCode.INVALID_PAYLOAD, "El respaldo está incompleto o contiene datos inválidos."),
            )
        } catch (_: IllegalArgumentException) {
            return CharacterBackupDecodeResult.Failure(
                CharacterBackupError(CharacterBackupErrorCode.INVALID_PAYLOAD, "El respaldo contiene valores que no se pueden interpretar."),
            )
        }

        val validation = characterBackupValidationMessage(document)
        return if (validation == null) {
            CharacterBackupDecodeResult.Success(document)
        } else {
            CharacterBackupDecodeResult.Failure(
                CharacterBackupError(CharacterBackupErrorCode.INVALID_PAYLOAD, validation),
            )
        }
    }
}

/**
 * Fully remapped import candidate. V1 import semantics are deliberately "restore as a new copy":
 * no original database identity is reused, so an import cannot overwrite a local character or
 * collide with child-row primary keys from the source backup.
 */
data class CharacterBackupImportPlan(
    val sourceCharacterId: Uuid,
    val sourceCampaignId: Uuid,
    val character: CharacterSheet,
    val closureState: CharacterClosureState,
)

fun prepareCharacterBackupImport(
    document: CharacterBackupDocument,
    destinationCampaignId: Uuid,
    targetCharacterId: Uuid = Uuid.random(),
    idFactory: () -> Uuid = { Uuid.random() },
): CharacterBackupImportPlan {
    val validation = characterBackupValidationMessage(document)
    require(validation == null) { validation ?: "Invalid character backup." }

    val source = document.character
    fun remap(ids: List<Uuid>): Map<Uuid, Uuid> = ids.associateWith { idFactory() }

    val classIds = remap(source.classes.map { it.id })
    val combatIds = remap(source.combatEntries.map { it.id })
    val inventoryIds = remap(source.inventoryItems.map { it.id })
    val traitIds = remap(source.traits.map { it.id })
    val noteIds = remap(source.noteCards.map { it.id })
    val spellSourceIds = remap(source.spellcastingSources.map { it.id })
    val spellIds = remap(source.spells.map { it.id })
    val proficiencyIds = remap(source.proficiencies.map { it.id })
    val masteryIds = remap(source.weaponMasteries.map { it.id })
    val resourceIds = remap(source.resources.map { it.id })
    val optionIds = remap(source.classOptions.map { it.id })
    val formIds = remap(source.forms.map { it.id })
    val companionIds = remap(source.companions.map { it.id })

    val closure = document.closureState
    val conditionIds = remap(closure.conditions.map { it.id })
    val defenseIds = remap(closure.defenses.map { it.id })
    val movementIds = remap(closure.movements.map { it.id })
    val senseIds = remap(closure.senses.map { it.id })
    val checkpointIds = remap(closure.reconciliationCheckpoints.map { it.id })
    val customSkillIds = remap(closure.customSkills.map { it.id })
    val effectIds = remap(closure.temporaryEffects.map { it.id })
    val otherQuickAccessIds = mutableMapOf<Uuid, Uuid>()

    val importedSheet = source.copy(
        id = targetCharacterId,
        campaignId = destinationCampaignId,
        classes = source.classes.map { it.copy(id = classIds.getValue(it.id)) },
        combatEntries = source.combatEntries.map { it.copy(id = combatIds.getValue(it.id)) },
        inventoryItems = source.inventoryItems.map { it.copy(id = inventoryIds.getValue(it.id)) },
        traits = source.traits.map { it.copy(id = traitIds.getValue(it.id)) },
        noteCards = source.noteCards.map { it.copy(id = noteIds.getValue(it.id)) },
        spellcastingSources = source.spellcastingSources.map { item ->
            item.copy(
                id = spellSourceIds.getValue(item.id),
                linkedClassId = item.linkedClassId?.let(classIds::get),
            )
        },
        spells = source.spells.map { spell ->
            spell.copy(
                id = spellIds.getValue(spell.id),
                sourceAssociations = spell.sourceAssociations.map { association ->
                    association.copy(sourceId = spellSourceIds.getValue(association.sourceId))
                },
            )
        },
        proficiencies = source.proficiencies.map { it.copy(id = proficiencyIds.getValue(it.id)) },
        weaponMasteries = source.weaponMasteries.map { it.copy(id = masteryIds.getValue(it.id)) },
        resources = source.resources.map { it.copy(id = resourceIds.getValue(it.id)) },
        classOptions = source.classOptions.map { item ->
            item.copy(
                id = optionIds.getValue(item.id),
                linkedClassId = item.linkedClassId?.let(classIds::get),
            )
        },
        forms = source.forms.map { it.copy(id = formIds.getValue(it.id)) },
        companions = source.companions.map { item ->
            item.copy(
                id = companionIds.getValue(item.id),
                linkedClassId = item.linkedClassId?.let(classIds::get),
            )
        },
    )

    fun remapQuickAccess(reference: CharacterQuickAccessRef): CharacterQuickAccessRef {
        val mapped = when (reference.kind) {
            CharacterQuickAccessKind.COMBAT_ENTRY -> combatIds.getValue(reference.targetId)
            CharacterQuickAccessKind.TRAIT -> traitIds.getValue(reference.targetId)
            CharacterQuickAccessKind.SPELL -> spellIds.getValue(reference.targetId)
            CharacterQuickAccessKind.RESOURCE -> resourceIds.getValue(reference.targetId)
            CharacterQuickAccessKind.CLASS_OPTION -> optionIds.getValue(reference.targetId)
            CharacterQuickAccessKind.FORM -> formIds.getValue(reference.targetId)
            CharacterQuickAccessKind.COMPANION -> companionIds.getValue(reference.targetId)
            CharacterQuickAccessKind.CUSTOM_SKILL -> customSkillIds.getValue(reference.targetId)
            CharacterQuickAccessKind.TEMPORARY_EFFECT -> effectIds.getValue(reference.targetId)
            CharacterQuickAccessKind.OTHER -> otherQuickAccessIds.getOrPut(reference.targetId, idFactory)
        }
        return reference.copy(targetId = mapped)
    }

    val importedClosure = closure.copy(
        concentration = closure.concentration?.let { value ->
            value.copy(spellId = value.spellId?.let(spellIds::get))
        },
        conditions = closure.conditions.map { it.copy(id = conditionIds.getValue(it.id)) },
        defenses = closure.defenses.map { it.copy(id = defenseIds.getValue(it.id)) },
        movements = closure.movements.map { it.copy(id = movementIds.getValue(it.id)) },
        senses = closure.senses.map { it.copy(id = senseIds.getValue(it.id)) },
        resourceRecovery = closure.resourceRecovery.map { it.copy(resourceId = resourceIds.getValue(it.resourceId)) },
        inventoryUsage = closure.inventoryUsage.map { it.copy(itemId = inventoryIds.getValue(it.itemId)) },
        reconciliationCheckpoints = closure.reconciliationCheckpoints.map { it.copy(id = checkpointIds.getValue(it.id)) },
        customSkills = closure.customSkills.map { it.copy(id = customSkillIds.getValue(it.id)) },
        temporaryEffects = closure.temporaryEffects.map { it.copy(id = effectIds.getValue(it.id)) },
        quickAccess = closure.quickAccess.map(::remapQuickAccess),
    )

    return CharacterBackupImportPlan(
        sourceCharacterId = source.id,
        sourceCampaignId = source.campaignId,
        character = importedSheet,
        closureState = importedClosure,
    )
}

internal fun characterBackupValidationMessage(document: CharacterBackupDocument): String? {
    if (document.format != CHARACTER_BACKUP_FORMAT) return "El identificador de formato del respaldo no es válido."
    if (document.version != CHARACTER_BACKUP_VERSION) return "La versión del respaldo no es compatible."
    if (document.exportedAtEpochSeconds < 0) return "La fecha del respaldo no es válida."

    val sheet = document.character
    if (sheet.name.isBlank()) return "El personaje del respaldo no tiene nombre."
    if (sheet.deathSaveSuccesses !in 0..3 || sheet.deathSaveFailures !in 0..3) return "Las salvaciones de muerte del respaldo no son válidas."

    fun duplicateMessage(ids: List<Uuid>, label: String): String? =
        if (ids.distinct().size == ids.size) null else "El respaldo contiene identificadores duplicados en $label."

    val idGroups = listOf(
        sheet.classes.map { it.id } to "clases",
        sheet.combatEntries.map { it.id } to "combate",
        sheet.inventoryItems.map { it.id } to "equipo",
        sheet.traits.map { it.id } to "rasgos",
        sheet.noteCards.map { it.id } to "notas",
        sheet.spellcastingSources.map { it.id } to "fuentes de conjuros",
        sheet.spells.map { it.id } to "conjuros",
        sheet.proficiencies.map { it.id } to "competencias",
        sheet.weaponMasteries.map { it.id } to "maestrías",
        sheet.resources.map { it.id } to "recursos",
        sheet.classOptions.map { it.id } to "opciones de clase",
        sheet.forms.map { it.id } to "formas",
        sheet.companions.map { it.id } to "compañeros",
    )
    idGroups.forEach { (ids, label) -> duplicateMessage(ids, label)?.let { return it } }

    if (sheet.currencies.map { it.key }.distinct().size != sheet.currencies.size) return "El respaldo contiene monedas con claves duplicadas."
    if (sheet.spellSlots.map { it.level }.distinct().size != sheet.spellSlots.size) return "El respaldo contiene niveles de espacios de conjuro duplicados."

    val classIds = sheet.classes.mapTo(mutableSetOf()) { it.id }
    val sourceIds = sheet.spellcastingSources.mapTo(mutableSetOf()) { it.id }
    val spellIds = sheet.spells.mapTo(mutableSetOf()) { it.id }
    val inventoryIds = sheet.inventoryItems.mapTo(mutableSetOf()) { it.id }
    val resourceIds = sheet.resources.mapTo(mutableSetOf()) { it.id }

    if (sheet.spellcastingSources.any { it.linkedClassId != null && it.linkedClassId !in classIds }) return "El respaldo contiene una fuente de conjuros vinculada a una clase inexistente."
    if (sheet.classOptions.any { it.linkedClassId != null && it.linkedClassId !in classIds }) return "El respaldo contiene una opción vinculada a una clase inexistente."
    if (sheet.companions.any { it.linkedClassId != null && it.linkedClassId !in classIds }) return "El respaldo contiene un compañero vinculado a una clase inexistente."
    if (sheet.spells.any { spell -> spell.sourceAssociations.map { it.sourceId }.distinct().size != spell.sourceAssociations.size }) return "El respaldo contiene asociaciones de conjuro duplicadas."
    if (sheet.spells.any { spell -> spell.sourceAssociations.any { it.sourceId !in sourceIds } }) return "El respaldo contiene un conjuro vinculado a una fuente inexistente."

    val state = document.closureState
    val closureIdGroups = listOf(
        state.conditions.map { it.id } to "condiciones",
        state.defenses.map { it.id } to "defensas",
        state.movements.map { it.id } to "movimientos",
        state.senses.map { it.id } to "sentidos",
        state.reconciliationCheckpoints.map { it.id } to "puntos de reconciliación",
        state.customSkills.map { it.id } to "habilidades personalizadas",
        state.temporaryEffects.map { it.id } to "efectos temporales",
    )
    closureIdGroups.forEach { (ids, label) -> duplicateMessage(ids, label)?.let { return it } }

    if (state.moduleOverrides.map { it.module }.distinct().size != state.moduleOverrides.size) return "El respaldo contiene configuraciones de módulo duplicadas."
    if (state.resourceRecovery.any { it.resourceId !in resourceIds }) return "El respaldo contiene recuperación para un recurso inexistente."
    if (state.inventoryUsage.any { it.itemId !in inventoryIds }) return "El respaldo contiene uso rápido para un objeto inexistente."
    if (state.concentration?.spellId != null && state.concentration.spellId !in spellIds) return "El respaldo contiene concentración vinculada a un conjuro inexistente."

    val quickTargets = mapOf(
        CharacterQuickAccessKind.COMBAT_ENTRY to sheet.combatEntries.mapTo(mutableSetOf()) { it.id },
        CharacterQuickAccessKind.TRAIT to sheet.traits.mapTo(mutableSetOf()) { it.id },
        CharacterQuickAccessKind.SPELL to spellIds,
        CharacterQuickAccessKind.RESOURCE to resourceIds,
        CharacterQuickAccessKind.CLASS_OPTION to sheet.classOptions.mapTo(mutableSetOf()) { it.id },
        CharacterQuickAccessKind.FORM to sheet.forms.mapTo(mutableSetOf()) { it.id },
        CharacterQuickAccessKind.COMPANION to sheet.companions.mapTo(mutableSetOf()) { it.id },
        CharacterQuickAccessKind.CUSTOM_SKILL to state.customSkills.mapTo(mutableSetOf()) { it.id },
        CharacterQuickAccessKind.TEMPORARY_EFFECT to state.temporaryEffects.mapTo(mutableSetOf()) { it.id },
    )
    if (state.quickAccess.any { ref -> ref.kind != CharacterQuickAccessKind.OTHER && ref.targetId !in quickTargets.getValue(ref.kind) }) {
        return "El respaldo contiene un acceso rápido vinculado a un registro inexistente."
    }

    return null
}
