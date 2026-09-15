package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterProvenanceContractTest {
    @Test
    fun soleOwnedStructuredSourceIsEligibleForAutoSelection() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val provenance = CharacterProvenanceRepository(database)
            val campaign = campaigns.createCampaign("Auto selección")
            val classId = Uuid.random()
            val saved = characters.saveCharacter(
                characters.createCharacter(campaign.id, "Fuente única").copy(
                    classes = listOf(
                        CharacterClassLevel(
                            id = classId,
                            name = "Mago",
                            level = 1,
                            hitDieSides = 6,
                            hitDiceRemaining = 1,
                            sortOrder = 0,
                        ),
                    ),
                ),
            )

            val state = provenance.state(saved.id)
            val sheet = requireNotNull(characters.character(saved.id))

            assertEquals(
                classId,
                soleCharacterProvenanceTargetOrNull(
                    sheet,
                    state,
                    CharacterProvenanceKind.CLASS,
                ),
            )
            assertNull(
                soleCharacterProvenanceTargetOrNull(
                    sheet,
                    state,
                    CharacterProvenanceKind.SUBCLASS,
                ),
            )
        }
    }

    @Test
    fun catalogDefinitionDoesNotCreateCharacterOwnership() {
        withDatabase { database ->
            assertTrue(CharacterClassCatalog.classes.any { it.nameEs == "Mago" })

            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val provenance = CharacterProvenanceRepository(database)
            val campaign = campaigns.createCampaign("Catálogo no es ownership")
            val traitId = Uuid.random()
            val saved = characters.saveCharacter(
                characters.createCharacter(campaign.id, "Sin clase Mago").copy(
                    traits = listOf(
                        CharacterTrait(
                            id = traitId,
                            name = "Texto legado",
                            source = "Mago",
                            type = CharacterTraitType.CLASS,
                            description = "",
                            notes = null,
                            maxUses = null,
                            spentUses = 0,
                            recovery = null,
                            activation = null,
                            sortOrder = 0,
                        ),
                    ),
                ),
            )

            val state = provenance.state(saved.id)
            val sheet = requireNotNull(characters.character(saved.id))
            val migrated = state.traitProvenance.single()

            assertTrue(characterProvenanceOptions(sheet, state, CharacterProvenanceKind.CLASS).isEmpty())
            assertNull(migrated.targetId)
            assertEquals("Mago", migrated.legacyText)
            assertEquals(
                CharacterProvenanceResolutionStatus.UNRESOLVED,
                resolveCharacterTraitProvenance(migrated, sheet, state).status,
            )
        }
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
