package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterProvenanceBackupTest {
    @Test
    fun backupExportAndRestoreAsCopyRemapOwnedOriginsAndTraitRelationships() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val provenance = CharacterProvenanceRepository(database)
            val backups = CharacterBackupRepository(database)
            val sourceCampaign = campaigns.createCampaign("Origen P7")
            val destinationCampaign = campaigns.createCampaign("Destino P7")
            val base = characters.createCharacter(sourceCampaign.id, "Proveniencia")

            val classId = Uuid.random()
            val classTraitId = Uuid.random()
            val subclassTraitId = Uuid.random()
            val raceTraitId = Uuid.random()
            val subraceTraitId = Uuid.random()
            val backgroundTraitId = Uuid.random()
            val source = characters.saveCharacter(
                base.copy(
                    classes = listOf(
                        CharacterClassLevel(
                            id = classId,
                            name = "Mago",
                            level = 5,
                            hitDieSides = 6,
                            hitDiceRemaining = 5,
                            sortOrder = 0,
                            catalogKey = "wizard",
                            subclassName = "Evocación",
                            subclassCatalogKey = "evocation",
                        ),
                    ),
                    background = CharacterBackground(
                        name = "Sabio",
                        race = "Elfo",
                    ),
                    traits = listOf(
                        trait(classTraitId, "Disciplina arcana", "Mago", CharacterTraitType.CLASS, 0),
                        trait(subclassTraitId, "Esculpir conjuros", "Evocación (Mago)", CharacterTraitType.CLASS, 1),
                        trait(raceTraitId, "Linaje feérico", "Elfo", CharacterTraitType.SPECIES_RACE, 2),
                        trait(subraceTraitId, "Herencia lunar", "Elfo lunar", CharacterTraitType.SPECIES_RACE, 3),
                        trait(backgroundTraitId, "Investigador", "Sabio", CharacterTraitType.BACKGROUND, 4),
                    ),
                ),
            )

            val initial = provenance.state(source.id)
            val sourceSpecies = requireNotNull(initial.speciesIdentity)
            val sourceSubrace = CharacterOwnedSubraceIdentity(
                id = Uuid.random(),
                parentSpeciesId = sourceSpecies.id,
                name = "Elfo lunar",
                definitionKey = "custom:elfo-lunar",
            )
            provenance.saveState(
                source.id,
                initial.copy(
                    subraceIdentity = sourceSubrace,
                    traitProvenance = initial.traitProvenance.map { item ->
                        if (item.traitId == subraceTraitId) {
                            item.copy(
                                kind = CharacterProvenanceKind.SUBRACE,
                                targetId = sourceSubrace.id,
                                freeText = null,
                                legacyText = "Elfo lunar (Elfo)",
                            )
                        } else {
                            item
                        }
                    },
                ),
            )

            val exported = backups.exportCharacter(source.id, 100)
            val exportedSubclass = exported.successorState.subclassIdentities.single()
            val exportedSpecies = requireNotNull(exported.successorState.speciesIdentity)
            val exportedSubrace = requireNotNull(exported.successorState.subraceIdentity)
            val exportedBackground = requireNotNull(exported.successorState.backgroundIdentity)
            assertEquals(exportedSpecies.id, exportedSubrace.parentSpeciesId)
            assertEquals("custom:elfo-lunar", exportedSubrace.definitionKey)
            assertEquals(5, exported.successorState.traitProvenance.size)

            val imported = backups.importAsCopy(exported, destinationCampaign.id, 200)
            val importedClass = imported.character.classes.single()
            val importedSubclass = imported.successorState.subclassIdentities.single()
            val importedSpecies = requireNotNull(imported.successorState.speciesIdentity)
            val importedSubrace = requireNotNull(imported.successorState.subraceIdentity)
            val importedBackground = requireNotNull(imported.successorState.backgroundIdentity)

            assertNotEquals(classId, importedClass.id)
            assertNotEquals(exportedSubclass.id, importedSubclass.id)
            assertNotEquals(exportedSpecies.id, importedSpecies.id)
            assertNotEquals(exportedSubrace.id, importedSubrace.id)
            assertNotEquals(exportedBackground.id, importedBackground.id)
            assertEquals(importedClass.id, importedSubclass.parentClassId)
            assertEquals(importedSpecies.id, importedSubrace.parentSpeciesId)
            assertEquals("custom:elfo-lunar", importedSubrace.definitionKey)

            val importedTraitIds = imported.character.traits.map { it.id }.toSet()
            assertTrue(imported.successorState.traitProvenance.all { it.traitId in importedTraitIds })

            val classProvenance = imported.successorState.traitProvenance
                .single { it.kind == CharacterProvenanceKind.CLASS }
            val subclassProvenance = imported.successorState.traitProvenance
                .single { it.kind == CharacterProvenanceKind.SUBCLASS }
            val raceProvenance = imported.successorState.traitProvenance
                .single { it.kind == CharacterProvenanceKind.SPECIES_RACE }
            val subraceProvenance = imported.successorState.traitProvenance
                .single { it.kind == CharacterProvenanceKind.SUBRACE }
            val backgroundProvenance = imported.successorState.traitProvenance
                .single { it.kind == CharacterProvenanceKind.BACKGROUND }

            assertEquals(importedClass.id, classProvenance.targetId)
            assertEquals(importedSubclass.id, subclassProvenance.targetId)
            assertEquals(importedSpecies.id, raceProvenance.targetId)
            assertEquals(importedSubrace.id, subraceProvenance.targetId)
            assertEquals(importedBackground.id, backgroundProvenance.targetId)

            val resolutions = imported.successorState.traitProvenance.map {
                resolveCharacterTraitProvenance(it, imported.character, imported.successorState)
            }
            assertTrue(resolutions.all { it.status == CharacterProvenanceResolutionStatus.RESOLVED })
            assertTrue(resolutions.any { it.label == "Evocación" && it.parentLabel == "Mago" })
            assertTrue(resolutions.any { it.label == "Elfo lunar" && it.parentLabel == "Elfo" })
        }
    }

    private fun trait(
        id: Uuid,
        name: String,
        source: String,
        type: CharacterTraitType,
        order: Int,
    ) = CharacterTrait(
        id = id,
        name = name,
        source = source,
        type = type,
        description = "",
        notes = null,
        maxUses = null,
        spentUses = 0,
        recovery = null,
        activation = null,
        sortOrder = order,
    )

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
