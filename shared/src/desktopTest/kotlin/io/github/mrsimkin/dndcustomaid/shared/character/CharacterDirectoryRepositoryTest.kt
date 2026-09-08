package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.uuid.Uuid

class CharacterDirectoryRepositoryTest {
    @Test
    fun globalDirectoryHydratesCharactersAcrossCampaignsFromCanonicalState() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)
        val campaigns = CampaignRepository(database)
        val characters = CharacterRepository(database)
        val directory = CharacterDirectoryRepository(database, characters)

        try {
            val firstCampaign = campaigns.createCampaign("Terramore")
            val secondCampaign = campaigns.createCampaign("Costa de Ceniza")
            val first = characters.createCharacter(firstCampaign.id, "Liora")
            val second = characters.createCharacter(secondCampaign.id, "Vanya")

            characters.saveCharacter(
                first.copy(
                    background = first.background.copy(race = "Elfa"),
                    classes = listOf(
                        CharacterClassLevel(
                            id = Uuid.random(),
                            name = "Maga",
                            level = 5,
                            hitDieSides = 6,
                            hitDiceRemaining = 5,
                            sortOrder = 0,
                        ),
                    ),
                ),
            )

            val listed = directory.listAllCharacters()

            assertEquals(setOf(first.id, second.id), listed.map { it.id }.toSet())
            assertEquals(setOf(firstCampaign.id, secondCampaign.id), listed.map { it.campaignId }.toSet())
            val hydratedFirst = assertNotNull(listed.firstOrNull { it.id == first.id })
            assertEquals("Elfa", hydratedFirst.background.race)
            assertEquals("Maga", hydratedFirst.classes.single().name)
            assertEquals(5, hydratedFirst.classes.single().level)
        } finally {
            driver.close()
        }
    }
}
