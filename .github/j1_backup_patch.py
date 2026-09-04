from pathlib import Path


def replace_once(path: str, old: str, new: str, label: str) -> None:
    p = Path(path)
    text = p.read_text()
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected one anchor, found {count}")
    p.write_text(text.replace(old, new, 1))


# Serialization dependency/plugin. Kotlin 2.4.10 + kotlinx.serialization-json 1.11.0.
replace_once(
    "gradle/libs.versions.toml",
    'sqldelight = "2.3.2"\n',
    'sqldelight = "2.3.2"\nkotlinx-serialization = "1.11.0"\n',
    "serialization version",
)
replace_once(
    "gradle/libs.versions.toml",
    'sqldelight-sqlite-driver = { module = "app.cash.sqldelight:sqlite-driver", version.ref = "sqldelight" }\n',
    'sqldelight-sqlite-driver = { module = "app.cash.sqldelight:sqlite-driver", version.ref = "sqldelight" }\n'
    'kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }\n',
    "serialization library",
)
replace_once(
    "gradle/libs.versions.toml",
    'kotlinMultiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }\n',
    'kotlinMultiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }\n'
    'kotlinSerialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }\n',
    "serialization plugin",
)
replace_once(
    "shared/build.gradle.kts",
    '    alias(libs.plugins.kotlinMultiplatform)\n',
    '    alias(libs.plugins.kotlinMultiplatform)\n    alias(libs.plugins.kotlinSerialization)\n',
    "shared serialization plugin",
)
replace_once(
    "shared/build.gradle.kts",
    '            implementation(libs.sqldelight.runtime)\n',
    '            implementation(libs.sqldelight.runtime)\n            implementation(libs.kotlinx.serialization.json)\n',
    "shared serialization dependency",
)

# Make the two authoritative backup aggregates serializable without introducing parallel DTO models.
for path in [
    "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterSheet.kt",
    "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClosureDomain.kt",
]:
    p = Path(path)
    text = p.read_text()
    if "import kotlinx.serialization.Serializable" in text:
        raise SystemExit(f"{path}: Serializable import already present")
    text = text.replace(
        "package io.github.mrsimkin.dndcustomaid.shared.character\n\n",
        "package io.github.mrsimkin.dndcustomaid.shared.character\n\nimport kotlinx.serialization.Serializable\n",
        1,
    )
    lines = []
    for line in text.splitlines():
        if line.startswith("enum class ") or line.startswith("data class "):
            lines.append("@Serializable")
        lines.append(line)
    p.write_text("\n".join(lines) + "\n")

catalog_path = "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterClassCatalog.kt"
replace_once(
    catalog_path,
    "package io.github.mrsimkin.dndcustomaid.shared.character\n\nenum class CharacterModuleKind",
    "package io.github.mrsimkin.dndcustomaid.shared.character\n\nimport kotlinx.serialization.Serializable\n\n@Serializable\nenum class CharacterModuleKind",
    "module kind serialization",
)

backup_code = r'''package io.github.mrsimkin.dndcustomaid.shared.character

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
'''
Path("shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackup.kt").write_text(backup_code)

backup_test = r'''package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterBackupTest {
    @Test
    fun richBackupRoundTripsThroughVersionedJson() {
        val document = richDocument()
        val encoded = CharacterBackupCodec.encode(document)
        val decoded = assertIs<CharacterBackupDecodeResult.Success>(CharacterBackupCodec.decode(encoded)).document

        assertEquals(document, decoded)
        assertTrue(encoded.contains(CHARACTER_BACKUP_FORMAT))
        assertTrue(encoded.contains("\"version\""))
        assertTrue(encoded.contains("Mesa de prueba"))
        assertTrue(encoded.contains("Recuperación arcana"))
    }

    @Test
    fun malformedWrongFormatAndUnsupportedVersionFailWithoutProducingPayload() {
        assertEquals(
            CharacterBackupErrorCode.EMPTY_INPUT,
            assertIs<CharacterBackupDecodeResult.Failure>(CharacterBackupCodec.decode("  ")).error.code,
        )
        assertEquals(
            CharacterBackupErrorCode.MALFORMED_JSON,
            assertIs<CharacterBackupDecodeResult.Failure>(CharacterBackupCodec.decode("{nope")).error.code,
        )
        assertEquals(
            CharacterBackupErrorCode.WRONG_FORMAT,
            assertIs<CharacterBackupDecodeResult.Failure>(
                CharacterBackupCodec.decode("{\"format\":\"otro\",\"version\":1}"),
            ).error.code,
        )
        assertEquals(
            CharacterBackupErrorCode.UNSUPPORTED_VERSION,
            assertIs<CharacterBackupDecodeResult.Failure>(
                CharacterBackupCodec.decode("{\"format\":\"$CHARACTER_BACKUP_FORMAT\",\"version\":999}"),
            ).error.code,
        )
    }

    @Test
    fun importPlanRestoresAsNewCopyAndRemapsInternalReferences() {
        val document = richDocument()
        val destinationCampaign = id("90000000-0000-0000-0000-000000000001")
        val targetCharacter = id("90000000-0000-0000-0000-000000000002")
        var next = 1000L
        val plan = prepareCharacterBackupImport(
            document = document,
            destinationCampaignId = destinationCampaign,
            targetCharacterId = targetCharacter,
            idFactory = {
                next += 1
                Uuid.parse("00000000-0000-0000-0000-${next.toString().padStart(12, '0')}")
            },
        )

        assertEquals(document.character.id, plan.sourceCharacterId)
        assertEquals(document.character.campaignId, plan.sourceCampaignId)
        assertEquals(targetCharacter, plan.character.id)
        assertEquals(destinationCampaign, plan.character.campaignId)
        assertNotEquals(document.character.classes.single().id, plan.character.classes.single().id)
        assertNotEquals(document.character.spells.single().id, plan.character.spells.single().id)
        assertNotEquals(document.closureState.conditions.single().id, plan.closureState.conditions.single().id)

        val importedClass = plan.character.classes.single()
        val importedSource = plan.character.spellcastingSources.single()
        val importedSpell = plan.character.spells.single()
        assertEquals(importedClass.id, importedSource.linkedClassId)
        assertEquals(importedSource.id, importedSpell.sourceAssociations.single().sourceId)
        assertEquals(importedSpell.id, plan.closureState.concentration?.spellId)
        assertEquals(plan.character.resources.single().id, plan.closureState.resourceRecovery.single().resourceId)
        assertEquals(plan.character.inventoryItems.single().id, plan.closureState.inventoryUsage.single().itemId)
        assertEquals(importedSpell.id, plan.closureState.quickAccess.first { it.kind == CharacterQuickAccessKind.SPELL }.targetId)
        assertEquals(plan.closureState.customSkills.single().id, plan.closureState.quickAccess.first { it.kind == CharacterQuickAccessKind.CUSTOM_SKILL }.targetId)
        assertEquals(plan.closureState.temporaryEffects.single().id, plan.closureState.quickAccess.first { it.kind == CharacterQuickAccessKind.TEMPORARY_EFFECT }.targetId)
    }

    @Test
    fun decodedBackupRejectsDanglingSoftReferencesBeforeAnyRepositoryWrite() {
        val valid = CharacterBackupCodec.encode(richDocument())
        val missing = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"
        val altered = valid.replace(
            "\"resourceId\": \"00000000-0000-0000-0000-000000000009\"",
            "\"resourceId\": \"$missing\"",
        )
        val result = CharacterBackupCodec.decode(altered)
        val failure = assertIs<CharacterBackupDecodeResult.Failure>(result)
        assertEquals(CharacterBackupErrorCode.INVALID_PAYLOAD, failure.error.code)
    }

    private fun richDocument(): CharacterBackupDocument {
        val campaignId = id("00000000-0000-0000-0000-000000000001")
        val characterId = id("00000000-0000-0000-0000-000000000002")
        val classId = id("00000000-0000-0000-0000-000000000003")
        val combatId = id("00000000-0000-0000-0000-000000000004")
        val inventoryId = id("00000000-0000-0000-0000-000000000005")
        val traitId = id("00000000-0000-0000-0000-000000000006")
        val noteId = id("00000000-0000-0000-0000-000000000007")
        val spellSourceId = id("00000000-0000-0000-0000-000000000008")
        val resourceId = id("00000000-0000-0000-0000-000000000009")
        val spellId = id("00000000-0000-0000-0000-000000000010")
        val proficiencyId = id("00000000-0000-0000-0000-000000000011")
        val masteryId = id("00000000-0000-0000-0000-000000000012")
        val optionId = id("00000000-0000-0000-0000-000000000013")
        val formId = id("00000000-0000-0000-0000-000000000014")
        val companionId = id("00000000-0000-0000-0000-000000000015")
        val conditionId = id("00000000-0000-0000-0000-000000000016")
        val defenseId = id("00000000-0000-0000-0000-000000000017")
        val movementId = id("00000000-0000-0000-0000-000000000018")
        val senseId = id("00000000-0000-0000-0000-000000000019")
        val checkpointId = id("00000000-0000-0000-0000-000000000020")
        val customSkillId = id("00000000-0000-0000-0000-000000000021")
        val effectId = id("00000000-0000-0000-0000-000000000022")

        val sheet = CharacterSheet(
            id = characterId,
            campaignId = campaignId,
            name = "Mesa de prueba",
            status = CharacterStatus.ACTIVE,
            updatedAtEpochSeconds = 1_777_000_000,
            strength = 16,
            dexterity = 14,
            constitution = 15,
            intelligence = 18,
            wisdom = 12,
            charisma = 10,
            armorClass = 18,
            maxHp = 45,
            currentHp = 31,
            tempHp = 4,
            initiativeAdjustment = 1,
            speed = 30,
            proficiencyBonus = 4,
            savingThrows = CharacterAbility.entries.map { CharacterSavingThrow(it, it == CharacterAbility.INTELLIGENCE, if (it == CharacterAbility.WISDOM) 1 else 0) },
            passivePerceptionAdjustment = 2,
            spellSaveDc = 16,
            classes = listOf(
                CharacterClassLevel(
                    id = classId,
                    name = "Artífice",
                    level = 9,
                    hitDieSides = 8,
                    hitDiceRemaining = 5,
                    sortOrder = 0,
                    rulesFamily = CharacterRulesFamily.DND_5_5E,
                    source = "Eberron",
                    catalogKey = "artificer-2025",
                    subclassName = "Battle Smith",
                    subclassSource = "Eberron",
                    subclassCatalogKey = "artificer-battle-smith-2025",
                ),
            ),
            skills = SkillKey.entries.map { CharacterSkill(it, if (it == SkillKey.ARCANA) 2 else 0, if (it == SkillKey.ARCANA) SkillTraining.EXPERTISE else SkillTraining.NONE) },
            proficiencyBonusAdjustment = 0,
            spellAttackModifier = 8,
            spellcastingAbility = SpellcastingAbility.INTELLIGENCE,
            spellSlots = listOf(CharacterSpellSlot(1, 4, 2), CharacterSpellSlot(2, 3, 1)),
            combatEntries = listOf(CharacterCombatEntry(combatId, "Martillo", CharacterCombatEntryType.ATTACK, 8, "1d8+4 fuerza", "5 pies", "Prueba", 0, true)),
            inventoryItems = listOf(CharacterInventoryItem(inventoryId, "Carga arcana", 7, 0.2, true, "Munición", 0, true, "Carga reutilizable", "Cinturón", true)),
            currencies = listOf(CharacterCurrency("gp", "PO", 123, 0, true), CharacterCurrency("gem", "Gemas", 4, 1, false)),
            spellcasterEnabled = true,
            background = CharacterBackground("Erudito", "Investigador", "Humano", "Onatar", "Curioso", "Conocimiento", "Taller", "Obsesivo", "Historia larga"),
            traits = listOf(CharacterTrait(traitId, "Destello", "Clase", CharacterTraitType.CLASS, "Efecto", "Nota", 3, 1, "Largo", CharacterActivationType.REACTION, 0, true)),
            spellcastingSources = listOf(CharacterSpellcastingSource(spellSourceId, "Artífice", classId, 0)),
            spells = listOf(CharacterSpell(spellId, "Ayuda", 2, "1 acción", "30 pies", true, true, true, "perla", "8 horas", false, false, "Descripción", "Notas", 0, listOf(CharacterSpellSourceAssociation(spellSourceId, true)), true)),
            generalNotes = "Notas generales",
            noteCards = listOf(CharacterNote(noteId, "Plan", "Contenido", 0)),
            inspiration = true,
            deathSaveSuccesses = 1,
            deathSaveFailures = 0,
            proficiencies = listOf(CharacterProficiency(proficiencyId, CharacterProficiencyType.TOOL, "Herramientas de ladrón", "Clase", "nota", 0)),
            weaponMasteries = listOf(CharacterWeaponMastery(masteryId, "Martillo", "Topple", "Clase", null, 0)),
            resources = listOf(CharacterResource(resourceId, "Recuperación arcana", 2, 5, "Descanso largo", "Clase", "nota", true, 0)),
            classOptions = listOf(CharacterClassOption(optionId, classId, CharacterClassOptionKind.ARTIFICER_DEVICE, "Defensor", "Clase", "1 carga", "Efecto", "nota", true, true, 0)),
            forms = listOf(CharacterForm(formId, "Armadura de prueba", "Clase", "2", 17, 30, "30 pies", "visión", "acción", "nota", true, 0)),
            companions = listOf(CharacterCompanion(companionId, classId, "Defensor de acero", "Constructo", "Clase", 15, 45, 40, 3, "40 pies", "INT 4", "Percepción", "Mordisco", "nota", true, 0)),
        )

        val closure = CharacterClosureState(
            exhaustionLevel = 1,
            concentration = CharacterConcentration(spellId, "Ayuda", "Concentración de prueba"),
            portraitRef = "portrait://local/example",
            tokenRef = "token://local/example",
            progressMode = CharacterProgressMode.EXPERIENCE,
            experiencePoints = 50_000,
            milestoneProgress = "",
            tableModeEnabled = true,
            hapticsEnabled = false,
            conditions = listOf(CharacterCondition(conditionId, "Envenenado", "sesión", "nota", 0)),
            defenses = listOf(CharacterDefense(defenseId, CharacterDefenseType.RESISTANCE, "Fuego", "objeto", null, 0)),
            movements = listOf(CharacterMovement(movementId, CharacterMovementType.FLY, "Vuelo", 30, "temporal", 0)),
            senses = listOf(CharacterSense(senseId, "Visión en la oscuridad", 60, null, 0)),
            resourceRecovery = listOf(CharacterResourceRecovery(resourceId, CharacterRecoveryCadence.LONG_REST, CharacterRecoveryAmountMode.TO_MAX, null, "recuperar")),
            inventoryUsage = listOf(CharacterInventoryUsage(inventoryId, CharacterConsumableKind.AMMUNITION, 1, CharacterInventoryCarryState.CARRIED)),
            reconciliationCheckpoints = listOf(CharacterReconciliationCheckpoint(checkpointId, 1_776_000_000, 1_775_999_999, "Fin sesión", "Todo cuadrado")),
            customSkills = listOf(CharacterCustomSkill(customSkillId, "Ingeniería", CharacterAbility.INTELLIGENCE, SkillTraining.PROFICIENT, 1, "homebrew", "nota", 0)),
            temporaryEffects = listOf(CharacterTemporaryEffect(effectId, "Bendición", "+1d4", "10 min", "Clérigo", "nota", true, 0)),
            moduleOverrides = listOf(CharacterModuleOverride(CharacterModuleKind.ARTIFICER, CharacterModuleOverrideMode.FORCE_SHOW)),
            quickAccess = listOf(
                CharacterQuickAccessRef(CharacterQuickAccessKind.COMBAT_ENTRY, combatId, 0),
                CharacterQuickAccessRef(CharacterQuickAccessKind.TRAIT, traitId, 1),
                CharacterQuickAccessRef(CharacterQuickAccessKind.SPELL, spellId, 2),
                CharacterQuickAccessRef(CharacterQuickAccessKind.RESOURCE, resourceId, 3),
                CharacterQuickAccessRef(CharacterQuickAccessKind.CLASS_OPTION, optionId, 4),
                CharacterQuickAccessRef(CharacterQuickAccessKind.FORM, formId, 5),
                CharacterQuickAccessRef(CharacterQuickAccessKind.COMPANION, companionId, 6),
                CharacterQuickAccessRef(CharacterQuickAccessKind.CUSTOM_SKILL, customSkillId, 7),
                CharacterQuickAccessRef(CharacterQuickAccessKind.TEMPORARY_EFFECT, effectId, 8),
            ),
        )
        return CharacterBackupDocument(CHARACTER_BACKUP_FORMAT, CHARACTER_BACKUP_VERSION, 1_778_000_000, sheet, closure)
    }

    private fun id(value: String): Uuid = Uuid.parse(value)
}
'''
Path("shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackupTest.kt").write_text(backup_test)
