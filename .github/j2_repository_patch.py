from pathlib import Path

repository_code = r'''package io.github.mrsimkin.dndcustomaid.shared.character

import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.uuid.Uuid

/**
 * Repository-level bridge for the app-owned character backup format.
 *
 * V1 import semantics are deliberately restore-as-copy: importing never targets an existing
 * character row and therefore cannot silently overwrite one. The entire restore is wrapped in a
 * database transaction so a stricter persistence invariant cannot leave a placeholder behind.
 */
class CharacterBackupRepository(
    private val database: AppDatabase,
) {
    private val characters = CharacterRepository(database)
    private val closure = CharacterClosureRepository(database)

    fun exportCharacter(
        characterId: Uuid,
        exportedAtEpochSeconds: Long,
    ): CharacterBackupDocument {
        require(exportedAtEpochSeconds >= 0) { "Backup export time must not be negative." }
        val character = requireNotNull(characters.character(characterId)) {
            "Character must already exist locally."
        }
        val document = CharacterBackupDocument(
            exportedAtEpochSeconds = exportedAtEpochSeconds,
            character = character,
            closureState = closure.state(characterId),
        )
        val validation = characterBackupValidationMessage(document)
        require(validation == null) { validation ?: "Invalid character backup." }
        return document
    }

    fun importAsCopy(
        document: CharacterBackupDocument,
        destinationCampaignId: Uuid,
        importedAtEpochSeconds: Long,
        idFactory: () -> Uuid = { Uuid.random() },
    ): CharacterBackupImportResult {
        require(importedAtEpochSeconds >= 0) { "Backup import time must not be negative." }
        val validation = characterBackupValidationMessage(document)
        require(validation == null) { validation ?: "Invalid character backup." }

        var result: CharacterBackupImportResult? = null
        database.transaction {
            // Creating the row first lets the existing authoritative repositories enforce all of
            // their normal local persistence rules. The outer transaction guarantees rollback if
            // either aggregate rejects the candidate afterwards.
            val placeholder = characters.createCharacter(
                campaignId = destinationCampaignId,
                rawName = document.character.name,
            )
            val plan = prepareCharacterBackupImport(
                document = document,
                destinationCampaignId = destinationCampaignId,
                targetCharacterId = placeholder.id,
                idFactory = idFactory,
            )
            val savedCharacter = characters.saveCharacter(plan.character)
            val importCheckpoint = CharacterReconciliationCheckpoint(
                id = idFactory(),
                createdAtEpochSeconds = importedAtEpochSeconds,
                characterUpdatedAtEpochSeconds = savedCharacter.updatedAtEpochSeconds,
                label = "Importado desde respaldo",
                notes = "Respaldo v${document.version} · personaje de origen ${document.character.id}",
            )
            val savedClosure = closure.saveState(
                savedCharacter.id,
                plan.closureState.copy(
                    reconciliationCheckpoints = plan.closureState.reconciliationCheckpoints + importCheckpoint,
                ),
            )
            result = CharacterBackupImportResult(
                sourceCharacterId = plan.sourceCharacterId,
                sourceCampaignId = plan.sourceCampaignId,
                character = savedCharacter,
                closureState = savedClosure,
                importCheckpoint = requireNotNull(
                    savedClosure.reconciliationCheckpoints.firstOrNull { it.id == importCheckpoint.id },
                ),
            )
        }
        return requireNotNull(result)
    }
}

data class CharacterBackupImportResult(
    val sourceCharacterId: Uuid,
    val sourceCampaignId: Uuid,
    val character: CharacterSheet,
    val closureState: CharacterClosureState,
    val importCheckpoint: CharacterReconciliationCheckpoint,
)
'''
Path('shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackupRepository.kt').write_text(repository_code)

test_code = r'''package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterBackupRepositoryTest {
    @Test
    fun repositoryRoundTripRestoresAsIndependentCopyAndAddsReconciliationCheckpoint() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val closure = CharacterClosureRepository(database)
            val backups = CharacterBackupRepository(database)
            val sourceCampaign = campaigns.createCampaign("Origen")
            val destinationCampaign = campaigns.createCampaign("Destino")
            val source = populateSourceCharacter(characters, closure, sourceCampaign.id)

            val exported = backups.exportCharacter(source.id, 1_778_100_000)
            val encoded = CharacterBackupCodec.encode(exported)
            val decoded = assertIs<CharacterBackupDecodeResult.Success>(CharacterBackupCodec.decode(encoded)).document
            val imported = backups.importAsCopy(decoded, destinationCampaign.id, 1_778_100_100)

            assertEquals(source.id, imported.sourceCharacterId)
            assertEquals(sourceCampaign.id, imported.sourceCampaignId)
            assertEquals(destinationCampaign.id, imported.character.campaignId)
            assertEquals(source.name, imported.character.name)
            assertNotEquals(source.id, imported.character.id)
            assertNotEquals(source.classes.single().id, imported.character.classes.single().id)
            assertNotEquals(source.inventoryItems.single().id, imported.character.inventoryItems.single().id)
            assertNotEquals(source.resources.single().id, imported.character.resources.single().id)
            assertNotEquals(source.spells.single().id, imported.character.spells.single().id)

            val importedClass = imported.character.classes.single()
            val importedSpellSource = imported.character.spellcastingSources.single()
            val importedSpell = imported.character.spells.single()
            assertEquals(importedClass.id, importedSpellSource.linkedClassId)
            assertEquals(importedSpellSource.id, importedSpell.sourceAssociations.single().sourceId)
            assertEquals(importedSpell.id, imported.closureState.concentration?.spellId)
            assertEquals(imported.character.resources.single().id, imported.closureState.resourceRecovery.single().resourceId)
            assertEquals(imported.character.inventoryItems.single().id, imported.closureState.inventoryUsage.single().itemId)
            assertEquals(importedSpell.id, imported.closureState.quickAccess.first { it.kind == CharacterQuickAccessKind.SPELL }.targetId)

            assertEquals("Importado desde respaldo", imported.importCheckpoint.label)
            assertEquals(1_778_100_100, imported.importCheckpoint.createdAtEpochSeconds)
            assertEquals(imported.character.updatedAtEpochSeconds, imported.importCheckpoint.characterUpdatedAtEpochSeconds)
            assertTrue(imported.importCheckpoint.notes.orEmpty().contains(source.id.toString()))
            assertEquals(2, imported.closureState.reconciliationCheckpoints.size)

            // Export/import must never mutate the source authority.
            val sourceAfter = assertNotNull(characters.character(source.id))
            assertEquals(source.classes.single().id, sourceAfter.classes.single().id)
            assertEquals(source.resources.single().id, sourceAfter.resources.single().id)
            assertEquals(1, closure.state(source.id).reconciliationCheckpoints.size)
        }
    }

    @Test
    fun importingSameBackupTwiceCreatesTwoIndependentCopiesWithoutCollision() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val closure = CharacterClosureRepository(database)
            val backups = CharacterBackupRepository(database)
            val sourceCampaign = campaigns.createCampaign("Origen")
            val destinationCampaign = campaigns.createCampaign("Destino")
            val source = populateSourceCharacter(characters, closure, sourceCampaign.id)
            val document = backups.exportCharacter(source.id, 1_778_200_000)

            val first = backups.importAsCopy(document, destinationCampaign.id, 1_778_200_100)
            val second = backups.importAsCopy(document, destinationCampaign.id, 1_778_200_200)

            assertNotEquals(first.character.id, second.character.id)
            assertNotEquals(first.character.classes.single().id, second.character.classes.single().id)
            assertNotEquals(first.character.spells.single().id, second.character.spells.single().id)
            assertNotEquals(first.closureState.conditions.single().id, second.closureState.conditions.single().id)
            assertEquals(2, characters.listCharacters(destinationCampaign.id).size)
            assertNotNull(characters.character(source.id))
        }
    }

    @Test
    fun repositoryFailureRollsBackPlaceholderAndAllPartialRows() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val closure = CharacterClosureRepository(database)
            val backups = CharacterBackupRepository(database)
            val sourceCampaign = campaigns.createCampaign("Origen")
            val destinationCampaign = campaigns.createCampaign("Destino")
            val source = populateSourceCharacter(characters, closure, sourceCampaign.id)
            val valid = backups.exportCharacter(source.id, 1_778_300_000)
            // Backup-level reference validation accepts this shape, while CharacterRepository
            // correctly rejects the negative class level. The outer transaction must roll back
            // the placeholder that was already created before saveCharacter throws.
            val stricterFailure = valid.copy(
                character = valid.character.copy(
                    classes = valid.character.classes.map { it.copy(level = -1) },
                ),
            )
            val before = characters.listCharacters(destinationCampaign.id).size

            assertFailsWith<IllegalArgumentException> {
                backups.importAsCopy(stricterFailure, destinationCampaign.id, 1_778_300_100)
            }

            assertEquals(before, characters.listCharacters(destinationCampaign.id).size)
            assertNotNull(characters.character(source.id))
        }
    }

    private fun populateSourceCharacter(
        characters: CharacterRepository,
        closure: CharacterClosureRepository,
        campaignId: Uuid,
    ): CharacterSheet {
        val base = characters.createCharacter(campaignId, "Archivista")
        val classId = Uuid.random()
        val inventoryId = Uuid.random()
        val resourceId = Uuid.random()
        val spellSourceId = Uuid.random()
        val spellId = Uuid.random()
        val optionId = Uuid.random()
        val companionId = Uuid.random()
        val saved = characters.saveCharacter(
            base.copy(
                intelligence = 18,
                armorClass = 17,
                maxHp = 42,
                currentHp = 31,
                spellcasterEnabled = true,
                classes = listOf(
                    CharacterClassLevel(
                        id = classId,
                        name = "Artífice",
                        level = 7,
                        hitDieSides = 8,
                        hitDiceRemaining = 4,
                        sortOrder = 0,
                    ),
                ),
                inventoryItems = listOf(
                    CharacterInventoryItem(
                        id = inventoryId,
                        name = "Carga de prueba",
                        quantity = 6,
                        weightLb = 0.1,
                        equipped = false,
                        category = "Consumible",
                        sortOrder = 0,
                        pinned = true,
                        notes = "Importable",
                        container = "Mochila",
                        visible = true,
                    ),
                ),
                resources = listOf(
                    CharacterResource(
                        id = resourceId,
                        name = "Cargas arcanas",
                        currentValue = 2,
                        maxValue = 5,
                        recovery = "Descanso largo",
                        source = "Clase",
                        notes = null,
                        pinned = true,
                        sortOrder = 0,
                    ),
                ),
                spellcastingSources = listOf(
                    CharacterSpellcastingSource(
                        id = spellSourceId,
                        name = "Artífice",
                        linkedClassId = classId,
                        sortOrder = 0,
                    ),
                ),
                spells = listOf(
                    CharacterSpell(
                        id = spellId,
                        name = "Ayuda",
                        level = 2,
                        castingTime = "1 acción",
                        range = "30 pies",
                        verbal = true,
                        somatic = true,
                        material = false,
                        materialText = null,
                        duration = "8 horas",
                        concentration = false,
                        ritual = false,
                        description = "Prueba de respaldo",
                        notes = null,
                        sortOrder = 0,
                        sourceAssociations = listOf(CharacterSpellSourceAssociation(spellSourceId, true)),
                        pinned = true,
                    ),
                ),
                classOptions = listOf(
                    CharacterClassOption(
                        id = optionId,
                        linkedClassId = classId,
                        kind = CharacterClassOptionKind.ARTIFICER_DEVICE,
                        name = "Dispositivo",
                        source = "Clase",
                        costText = "1 carga",
                        effectSummary = "Efecto de prueba",
                        notes = null,
                        active = true,
                        pinned = true,
                        sortOrder = 0,
                    ),
                ),
                companions = listOf(
                    CharacterCompanion(
                        id = companionId,
                        linkedClassId = classId,
                        name = "Defensor",
                        kind = "Constructo",
                        source = "Clase",
                        armorClass = 15,
                        maxHp = 30,
                        currentHp = 24,
                        tempHp = 0,
                        speed = "40 pies",
                        abilitySummary = null,
                        sensesProficiencies = null,
                        traitsActions = "Mordisco",
                        notes = null,
                        active = true,
                        sortOrder = 0,
                    ),
                ),
            ),
        )

        val conditionId = Uuid.random()
        val checkpointId = Uuid.random()
        val customSkillId = Uuid.random()
        val effectId = Uuid.random()
        closure.saveState(
            saved.id,
            CharacterClosureState(
                concentration = CharacterConcentration(spellId, "Ayuda", "Activa"),
                conditions = listOf(CharacterCondition(conditionId, "Marcado", "Prueba", null, 0)),
                resourceRecovery = listOf(
                    CharacterResourceRecovery(
                        resourceId = resourceId,
                        cadence = CharacterRecoveryCadence.LONG_REST,
                        amountMode = CharacterRecoveryAmountMode.TO_MAX,
                        fixedAmount = null,
                        notes = null,
                    ),
                ),
                inventoryUsage = listOf(
                    CharacterInventoryUsage(
                        itemId = inventoryId,
                        kind = CharacterConsumableKind.AMMUNITION,
                        quickUseAmount = 1,
                        carryState = CharacterInventoryCarryState.CARRIED,
                    ),
                ),
                reconciliationCheckpoints = listOf(
                    CharacterReconciliationCheckpoint(
                        id = checkpointId,
                        createdAtEpochSeconds = 1_778_000_000,
                        characterUpdatedAtEpochSeconds = saved.updatedAtEpochSeconds,
                        label = "Antes del respaldo",
                        notes = "Fuente original",
                    ),
                ),
                customSkills = listOf(
                    CharacterCustomSkill(
                        id = customSkillId,
                        name = "Ingeniería",
                        ability = CharacterAbility.INTELLIGENCE,
                        training = SkillTraining.PROFICIENT,
                        adjustment = 1,
                        source = "Homebrew",
                        notes = null,
                        sortOrder = 0,
                    ),
                ),
                temporaryEffects = listOf(
                    CharacterTemporaryEffect(
                        id = effectId,
                        name = "Bendición",
                        summary = "+1",
                        durationText = "10 min",
                        source = "Aliado",
                        notes = null,
                        active = true,
                        sortOrder = 0,
                    ),
                ),
                quickAccess = listOf(
                    CharacterQuickAccessRef(CharacterQuickAccessKind.SPELL, spellId, 0),
                    CharacterQuickAccessRef(CharacterQuickAccessKind.RESOURCE, resourceId, 1),
                    CharacterQuickAccessRef(CharacterQuickAccessKind.CLASS_OPTION, optionId, 2),
                    CharacterQuickAccessRef(CharacterQuickAccessKind.COMPANION, companionId, 3),
                    CharacterQuickAccessRef(CharacterQuickAccessKind.CUSTOM_SKILL, customSkillId, 4),
                    CharacterQuickAccessRef(CharacterQuickAccessKind.TEMPORARY_EFFECT, effectId, 5),
                ),
            ),
        )
        return assertNotNull(characters.character(saved.id))
    }

    private fun withDatabase(block: (AppDatabase) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            block(AppDatabase(driver))
        } finally {
            driver.close()
        }
    }
}
'''
Path('shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackupRepositoryTest.kt').write_text(test_code)
