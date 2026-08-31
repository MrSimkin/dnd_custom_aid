package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.uuid.Uuid

class CharacterRepositoryTest {
    @Test
    fun newCharacterBelongsToCampaignAndStartsWithCompleteDerivedState() = withRepositories { campaigns, characters ->
        val campaign = campaigns.createCampaign("Terramore")
        val character = characters.createCharacter(campaign.id, "  Liora  ")

        assertEquals("Liora", character.name)
        assertEquals(campaign.id, character.campaignId)
        assertEquals(CharacterStatus.ACTIVE, character.status)
        assertEquals(SkillKey.entries.toSet(), character.skills.map { it.key }.toSet())
        assertEquals(CharacterAbility.entries.toSet(), character.savingThrows.map { it.ability }.toSet())
        assertEquals(18, character.skills.size)
        assertEquals(6, character.savingThrows.size)
        assertEquals(0, character.initiativeModifier)
        assertEquals(10, character.passivePerception)
    }

    @Test
    fun abilityModifierUsesFloorSemanticsBelowTen() {
        assertEquals(5, abilityModifierForScore(20))
        assertEquals(0, abilityModifierForScore(10))
        assertEquals(-1, abilityModifierForScore(9))
        assertEquals(-1, abilityModifierForScore(8))
        assertEquals(-2, abilityModifierForScore(7))
        assertEquals(-5, abilityModifierForScore(1))
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
                    CharacterClassLevel(Uuid.random(), "Guerrero", 3, 10, 2, 0),
                    CharacterClassLevel(Uuid.random(), "Mago", 5, 6, 4, 1),
                ),
            ),
        )

        assertEquals(8, saved.totalLevel)
        assertEquals(listOf("Guerrero", "Mago"), saved.classes.map { it.name })
        assertEquals(listOf(10, 6), saved.classes.map { it.hitDieSides })
        assertEquals(listOf(2, 4), saved.classes.map { it.hitDiceRemaining })
    }

    @Test
    fun standardArithmeticIsAutomaticWhileAdjustmentsRemainPermissive() = withRepositories { campaigns, characters ->
        val campaign = campaigns.createCampaign("Terramore")
        val character = characters.createCharacter(campaign.id, "Gifted Hero")

        val saved = characters.saveCharacter(
            character.copy(
                strength = 30,
                dexterity = 14,
                wisdom = 12,
                armorClass = 27,
                proficiencyBonus = 9,
                initiativeAdjustment = 4,
                passivePerceptionAdjustment = -3,
                savingThrows = character.savingThrows.map { save ->
                    if (save.ability == CharacterAbility.STRENGTH) {
                        save.copy(proficient = true, adjustment = -5)
                    } else {
                        save
                    }
                },
                skills = character.skills.map { skill ->
                    when (skill.key) {
                        SkillKey.ATHLETICS -> skill.copy(
                            adjustment = -2,
                            training = SkillTraining.PROFICIENT,
                        )
                        SkillKey.PERCEPTION -> skill.copy(
                            adjustment = 7,
                            training = SkillTraining.EXPERTISE,
                        )
                        else -> skill
                    }
                },
            ),
        )

        assertEquals(10, saved.abilityModifier(CharacterAbility.STRENGTH))
        assertEquals(17, saved.skillTotal(SkillKey.ATHLETICS))
        assertEquals(14, saved.savingThrowTotal(CharacterAbility.STRENGTH))
        assertEquals(6, saved.initiativeModifier)
        assertEquals(33, saved.passivePerception)
        assertEquals(27, saved.armorClass)
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
                    initiativeAdjustment = 3,
                    classes = listOf(CharacterClassLevel(Uuid.random(), "Paladín", 6, 10, 3, 0)),
                    savingThrows = created.savingThrows.map { save ->
                        if (save.ability == CharacterAbility.CHARISMA) save.copy(proficient = true, adjustment = 2) else save
                    },
                ),
            )
            firstDriver.close()

            val secondDriver = JdbcSqliteDriver(jdbcUrl)
            val secondRepository = CharacterRepository(AppDatabase(secondDriver))
            val reopened = secondRepository.character(saved.id)

            assertNotNull(reopened)
            assertEquals(42, reopened.currentHp)
            assertEquals(7, reopened.tempHp)
            assertEquals(3, reopened.initiativeAdjustment)
            assertEquals("Paladín", reopened.classes.single().name)
            assertEquals(3, reopened.classes.single().hitDiceRemaining)
            assertEquals(true, reopened.savingThrow(CharacterAbility.CHARISMA).proficient)
            assertEquals(2, reopened.savingThrow(CharacterAbility.CHARISMA).adjustment)
            assertEquals(18, reopened.skills.size)
            secondDriver.close()
        } finally {
            file.delete()
        }
    }

    @Test
    fun phase3CampaignDatabaseMigratesWithoutLosingCampaigns() {
        val file = File.createTempFile("dnd-custom-aid-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate(
                        "CREATE TABLE campaign (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)",
                    )
                    statement.executeUpdate(
                        "CREATE TABLE app_state (singleton INTEGER NOT NULL PRIMARY KEY CHECK (singleton = 1), active_campaign_id TEXT REFERENCES campaign(id))",
                    )
                    statement.executeUpdate(
                        "INSERT INTO campaign(id, name) VALUES ('00000000-0000-0000-0000-000000000001', 'Campaña existente')",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(
                driver = driver,
                oldVersion = 1,
                newVersion = AppDatabase.Schema.version,
            )
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val existingCampaign = campaigns.listCampaigns().single()

            assertEquals("Campaña existente", existingCampaign.name)
            assertEquals(0, characters.listCharacters(existingCampaign.id).size)
            assertEquals("Nuevo personaje", characters.createCharacter(existingCampaign.id, "Nuevo personaje").name)
            driver.close()
        } finally {
            file.delete()
        }
    }

    @Test
    fun v3FinalTotalsMigrateToAdjustmentsWithoutChangingDisplayedNumbers() {
        val file = File.createTempFile("dnd-custom-aid-v3-migration", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val characterId = "00000000-0000-0000-0000-000000000010"
        val campaignId = "00000000-0000-0000-0000-000000000001"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { s ->
                    s.executeUpdate("PRAGMA foreign_keys=ON")
                    s.executeUpdate("CREATE TABLE campaign (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    s.executeUpdate("CREATE TABLE app_state (singleton INTEGER NOT NULL PRIMARY KEY CHECK (singleton = 1), active_campaign_id TEXT REFERENCES campaign(id))")
                    s.executeUpdate("INSERT INTO campaign(id, name) VALUES ('$campaignId', 'V3')")
                    s.executeUpdate(
                        """CREATE TABLE character (
                            id TEXT NOT NULL PRIMARY KEY, campaign_id TEXT NOT NULL REFERENCES campaign(id) ON DELETE CASCADE,
                            name TEXT NOT NULL, status TEXT NOT NULL, updated_at_epoch_seconds INTEGER NOT NULL DEFAULT 0,
                            strength INTEGER NOT NULL DEFAULT 10, dexterity INTEGER NOT NULL DEFAULT 10,
                            constitution INTEGER NOT NULL DEFAULT 10, intelligence INTEGER NOT NULL DEFAULT 10,
                            wisdom INTEGER NOT NULL DEFAULT 10, charisma INTEGER NOT NULL DEFAULT 10,
                            armor_class INTEGER NOT NULL DEFAULT 10, max_hp INTEGER NOT NULL DEFAULT 1,
                            current_hp INTEGER NOT NULL DEFAULT 1, temp_hp INTEGER NOT NULL DEFAULT 0,
                            initiative_modifier INTEGER NOT NULL DEFAULT 0, speed INTEGER NOT NULL DEFAULT 30,
                            proficiency_bonus INTEGER NOT NULL DEFAULT 2, strength_save INTEGER NOT NULL DEFAULT 0,
                            dexterity_save INTEGER NOT NULL DEFAULT 0, constitution_save INTEGER NOT NULL DEFAULT 0,
                            intelligence_save INTEGER NOT NULL DEFAULT 0, wisdom_save INTEGER NOT NULL DEFAULT 0,
                            charisma_save INTEGER NOT NULL DEFAULT 0, passive_perception INTEGER NOT NULL DEFAULT 10,
                            spell_save_dc INTEGER
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        """CREATE TABLE character_class (
                            id TEXT NOT NULL PRIMARY KEY, character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                            name TEXT NOT NULL, level INTEGER NOT NULL, hit_die_sides INTEGER NOT NULL,
                            hit_dice_remaining INTEGER NOT NULL, sort_order INTEGER NOT NULL, UNIQUE(character_id, sort_order)
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        """CREATE TABLE character_skill (
                            character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                            skill_key TEXT NOT NULL, modifier INTEGER NOT NULL, training TEXT NOT NULL,
                            PRIMARY KEY(character_id, skill_key)
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        """INSERT INTO character(
                            id,campaign_id,name,status,strength,dexterity,constitution,intelligence,wisdom,charisma,
                            armor_class,max_hp,current_hp,temp_hp,initiative_modifier,speed,proficiency_bonus,
                            strength_save,dexterity_save,constitution_save,intelligence_save,wisdom_save,charisma_save,
                            passive_perception,spell_save_dc
                        ) VALUES (
                            '$characterId','$campaignId','Migrado','ACTIVE',16,14,15,13,12,8,
                            17,38,31,4,7,30,3,6,2,5,1,1,-1,13,14
                        )""".trimIndent(),
                    )
                    s.executeUpdate("INSERT INTO character_skill(character_id,skill_key,modifier,training) VALUES ('$characterId','ATHLETICS',19,'PROFICIENT')")
                    s.executeUpdate("INSERT INTO character_skill(character_id,skill_key,modifier,training) VALUES ('$characterId','PERCEPTION',3,'PROFICIENT')")
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver, oldVersion = 2, newVersion = AppDatabase.Schema.version)
            val migrated = CharacterRepository(AppDatabase(driver)).character(Uuid.parse(characterId))

            assertNotNull(migrated)
            assertEquals(7, migrated.initiativeModifier)
            assertEquals(19, migrated.skillTotal(SkillKey.ATHLETICS))
            assertEquals(3, migrated.skillTotal(SkillKey.PERCEPTION))
            assertEquals(13, migrated.passivePerception)
            assertEquals(6, migrated.savingThrowTotal(CharacterAbility.STRENGTH))
            assertEquals(2, migrated.savingThrowTotal(CharacterAbility.DEXTERITY))
            assertFalse(migrated.savingThrow(CharacterAbility.STRENGTH).proficient)
            driver.close()
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
