package io.github.mrsimkin.dndcustomaid.shared.character

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
    fun nonPrimitiveHeaderValuesReturnControlledFailures() {
        assertEquals(
            CharacterBackupErrorCode.WRONG_FORMAT,
            assertIs<CharacterBackupDecodeResult.Failure>(
                CharacterBackupCodec.decode("{\"format\":{},\"version\":1}"),
            ).error.code,
        )
        assertEquals(
            CharacterBackupErrorCode.UNSUPPORTED_VERSION,
            assertIs<CharacterBackupDecodeResult.Failure>(
                CharacterBackupCodec.decode("{\"format\":\"$CHARACTER_BACKUP_FORMAT\",\"version\":{}}"),
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
