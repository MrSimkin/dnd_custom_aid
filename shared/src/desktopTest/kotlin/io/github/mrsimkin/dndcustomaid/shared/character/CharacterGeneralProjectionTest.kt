package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class CharacterGeneralProjectionTest {
    @Test
    fun generalProjectsCanonicalLanguagesEquipmentResourcesAndPerSourceCasting() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val successor = CharacterSuccessorRepository(database)
            val campaign = campaigns.createCampaign("General")
            val base = characters.createCharacter(campaign.id, "Proyección")

            val languageId = Uuid.random()
            val toolId = Uuid.random()
            val armorId = Uuid.random()
            val storedItemId = Uuid.random()
            val generalResourceId = Uuid.random()
            val managementResourceId = Uuid.random()
            val sourceId = Uuid.random()

            val sheet = characters.saveCharacter(
                base.copy(
                    intelligence = 16,
                    proficiencies = listOf(
                        CharacterProficiency(languageId, CharacterProficiencyType.LANGUAGE, "Dracónico", sortOrder = 0),
                        CharacterProficiency(toolId, CharacterProficiencyType.TOOL, "Herramientas de ladrón", sortOrder = 1),
                    ),
                    inventoryItems = listOf(
                        CharacterInventoryItem(
                            id = armorId,
                            name = "Armadura equipada",
                            quantity = 1,
                            weightLb = 20.0,
                            equipped = true,
                            notes = null,
                            sortOrder = 0,
                            special = false,
                            description = null,
                            location = null,
                            attuned = false,
                        ),
                        CharacterInventoryItem(
                            id = storedItemId,
                            name = "Objeto guardado",
                            quantity = 1,
                            weightLb = null,
                            equipped = false,
                            notes = null,
                            sortOrder = 1,
                            special = false,
                            description = null,
                            location = null,
                            attuned = false,
                        ),
                    ),
                    resources = listOf(
                        CharacterResource(generalResourceId, "Impulso", 2, 4, sortOrder = 0),
                        CharacterResource(managementResourceId, "Reserva", 1, 3, sortOrder = 1),
                    ),
                    spellcasterEnabled = true,
                    spellcastingSources = listOf(
                        CharacterSpellcastingSource(sourceId, "Grimorio", linkedClassId = null, sortOrder = 0),
                    ),
                ),
            )

            val initial = successor.state(sheet.id)
            val configured = successor.saveState(
                sheet.id,
                initial.copy(
                    resourceConfigurations = initial.resourceConfigurations.map { configuration ->
                        when (configuration.resourceId) {
                            generalResourceId -> configuration.copy(
                                placements = setOf(CharacterResourcePlacement.GENERAL, CharacterResourcePlacement.MANAGEMENT),
                            )
                            else -> configuration
                        }
                    },
                    spellcastingProfiles = listOf(
                        CharacterSpellcastingProfile(
                            sourceId = sourceId,
                            ability = CharacterAbilityReference.builtIn(CharacterAbility.INTELLIGENCE),
                        ),
                    ),
                ),
            )

            assertEquals(listOf("Dracónico"), sheet.generalLanguages().map { it.name })
            assertEquals(listOf("Armadura equipada"), sheet.generalEquippedItemReferences().map { it.name })
            assertEquals(listOf("Impulso"), sheet.generalResources(configured).map { it.resource.name })

            val casting = sheet.generalSpellcastingRows(configured).single()
            assertEquals("Grimorio", casting.source.name)
            assertEquals(13, casting.saveDc)
            assertEquals(5, casting.spellAttackModifier)
        } finally {
            driver.close()
        }
    }
}
