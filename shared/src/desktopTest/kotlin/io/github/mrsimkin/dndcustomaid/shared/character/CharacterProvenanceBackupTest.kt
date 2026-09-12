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
            val backups = CharacterBackupRepository(database)
            val sourceCampaign = campaigns.createCampaign("Origen P7")
            val destinationCampaign = campaigns.createCampaign("Destino P7")
            val base = characters.createCharacter(sourceCampaign.id, "Proveniencia")

            val classId = Uuid.random()
            val classTraitId = Uuid.random()
            val subclassTraitId = Uuid.random()
            val raceTraitId = Uuid.random()
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
                        trait(backgroundTraitId, "Investigador", "Sabio", CharacterTraitType.BACKGROUND, 3),
                    ),
                ),
            )

            val exported = backups.exportCharacter(source.id, 100)
            val exportedSubclass = exported.successorState.subclassIdentities.single()
            val exportedSpecies = requireNotNull(exported.successorState.speciesIdentity)
            val exportedBackground = requireNotNull(exported.successorState.backgroundIdentity)
            assertEquals(4, exported.successorState.traitProvenance.size)

            val imported = backups.importAsCopy(exported, destinationCampaign.id, 200)
            val importedClass = imported.character.classes.single()
            val importedSubclass = imported.successorState.subclassIdentities.single()
            val importedSpecies = requireNotNull(imported.successorState.speciesIdentity)
            val importedBackground = requireNotNull(imported.successorState.backgroundIdentity)

            assertNotEquals(classId, importedClass.id)
            assertNotEquals(exportedSubclass.id, importedSubclass.id)
            assertNotEquals(exportedSpecies.id, importedSpecies.id)
            assertNotEquals(exportedBackground.id, importedBackground.id)
            assertEquals(importedClass.id, importedSubclass.parentClassId)

            val importedTraitIds = imported.character.traits.map { it.id }.toSet()
            assertTrue(imported.successorState.traitProvenance.all { it.traitId in importedTraitIds })

            val classProvenance = imported.successorState.traitProvenance
                .single { it.kind == CharacterProvenanceKind.CLASS }
            val subclassProvenance = imported.successorState.traitProvenance
                .single { it.kind == CharacterProvenanceKind.SUBCLASS }
            val raceProvenance = imported.successorState.traitProvenance
                .single { it.kind == CharacterProvenanceKind.SPECIES_RACE }
            val backgroundProvenance = imported.successorState.traitProvenance
                .single { it.kind == CharacterProvenanceKind.BACKGROUND }

            assertEquals(importedClass.id, classProvenance.targetId)
            assertEquals(importedSubclass.id, subclassProvenance.targetId)
            assertEquals(importedSpecies.id, raceProvenance.targetId)
            assertEquals(importedBackground.id, backgroundProvenance.targetId)

            val resolutions = imported.successorState.traitProvenance.map {
                resolveCharacterTraitProvenance(it, imported.character, imported.successorState)
            }
            assertTrue(resolutions.all { it.status == CharacterProvenanceResolutionStatus.RESOLVED })
            assertTrue(resolutions.any { it.label == "Evocación" && it.parentLabel == "Mago" })
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
