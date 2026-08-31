package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.uuid.Uuid

class CharacterRepositoryTest {
    @Test
    fun newCharacterBelongsToCampaignAndStartsWithCompleteSkillSet() = withRepositories { campaigns, characters ->
        val campaign = campaigns.createCampaign("Terramore")
        val character = characters.createCharacter(campaign.id, "  Liora  ")

        assertEquals("Liora", character.name)
        assertEquals(campaign.id, character.campaignId)
        assertEquals(CharacterStatus.ACTIVE, character.status)
        assertEquals(SkillKey.entries.toSet(), character.skills.map { it.key }.toSet())
        assertEquals(18, character.skills.size)
    }

    @Test
    fun characterNameMustNotBeBlank() = withRepositories { campaigns, characters ->
        val campaign = campaigns.createCampaign("Terramore")

        assertFailsWith<IllegalArgumentException> {
            characters.createCharacter(campaign.id, "  \n\t")
        }
    }

    @Test
    fun multiclassLevelsAndHitDicePersistIndependently() = withRepositories { campaigns, characters ->
        val campaign = campaigns.createCampaign("Terramore")
        val character = characters.createCharacter(campaign.id, "Vanya")

        val saved = characters.saveCharacter(
            character.copy(
                classes = listOf(
                    CharacterClassLevel(Uuid.random(), "Fighter", 3, 10, 2, 0),
                    CharacterClassLevel(Uuid.random(), "Wizard", 5, 6, 4, 1),
                ),
            ),
        )

        assertEquals(8, saved.totalLevel)
        assertEquals(listOf("Fighter", "Wizard"), saved.classes.map { it.name })
        assertEquals(listOf(10, 6), saved.classes.map { it.hitDieSides })
        assertEquals(listOf(2, 4), saved.classes.map { it.hitDiceRemaining })
    }

    @Test
    fun finalMechanicalValuesAreStoredWithoutRulesEnforcement() = withRepositories { campaigns, characters ->
        val campaign = campaigns.createCampaign("Terramore")
        val character = characters.createCharacter(campaign.id, "Gifted Hero")
        val athletics = character.skills.first { it.key == SkillKey.ATHLETICS }

        val saved = characters.saveCharacter(
            character.copy(
                strength = 30,
                armorClass = 27,
                proficiencyBonus = 9,
                strengthSave = 14,
                skills = character.skills.map { skill ->
                    if (skill.key == SkillKey.ATHLETICS) {
                        athletics.copy(modifier = 17, training = SkillTraining.PROFICIENT)
                    } else {
                        skill
                    }
                },
            ),
        )

        assertEquals(30, saved.strength)
        assertEquals(27, saved.armorClass)
        assertEquals(9, saved.proficiencyBonus)
        assertEquals(14, saved.strengthSave)
        assertEquals(17, saved.skills.first { it.key == SkillKey.ATHLETICS }.modifier)
    }

    @Test
    fun charactersRemainIsolatedByCampaign() = withRepositories { campaigns, characters ->
        val firstCampaign = campaigns.createCampaign("First")
        val secondCampaign = campaigns.createCampaign("Second")
        val first = characters.createCharacter(firstCampaign.id, "Same Name")
        val second = characters.createCharacter(secondCampaign.id, "Same Name")

        assertEquals(listOf(first.id), characters.listCharacters(firstCampaign.id).map { it.id })
        assertEquals(listOf(second.id), characters.listCharacters(secondCampaign.id).map { it.id })
    }

    @Test
    fun characterDataSurvivesDatabaseReopen() {
        val file = File.createTempFile("dnd-custom-aid-character", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"

        try {
            val firstDriver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.create(firstDriver)
            val firstDatabase = AppDatabase(firstDriver)
            val campaigns = CampaignRepository(firstDatabase)
            val characters = CharacterRepository(firstDatabase)
            val campaign = campaigns.createCampaign("Persistent")
            val created = characters.createCharacter(campaign.id, "Persistent Hero")
            val saved = characters.saveCharacter(
                created.copy(
                    currentHp = 42,
                    tempHp = 7,
                    classes = listOf(CharacterClassLevel(Uuid.random(), "Paladin", 6, 10, 3, 0)),
                ),
            )
            firstDriver.close()

            val secondDriver = JdbcSqliteDriver(jdbcUrl)
            val secondRepository = CharacterRepository(AppDatabase(secondDriver))
            val reopened = secondRepository.character(saved.id)

            assertNotNull(reopened)
            assertEquals(42, reopened.currentHp)
            assertEquals(7, reopened.tempHp)
            assertEquals("Paladin", reopened.classes.single().name)
            assertEquals(3, reopened.classes.single().hitDiceRemaining)
            assertEquals(18, reopened.skills.size)
            secondDriver.close()
        } finally {
            file.delete()
        }
    }

    private fun withRepositories(block: (CampaignRepository, CharacterRepository) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)

        try {
            block(CampaignRepository(database), CharacterRepository(database))
        } finally {
            driver.close()
        }
    }
}
