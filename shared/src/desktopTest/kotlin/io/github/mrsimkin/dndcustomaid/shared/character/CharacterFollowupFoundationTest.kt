package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterFollowupFoundationTest {
    @Test
    fun proficiencyBonusUsesApprovedLevelZeroDefaultAndNormalProgression() {
        assertEquals(2, standardProficiencyBonusForLevel(0))
        assertEquals(2, standardProficiencyBonusForLevel(4))
        assertEquals(3, standardProficiencyBonusForLevel(5))
        assertEquals(4, standardProficiencyBonusForLevel(9))
        assertEquals(5, standardProficiencyBonusForLevel(13))
        assertEquals(6, standardProficiencyBonusForLevel(17))
        assertEquals(6, standardProficiencyBonusForLevel(25))
    }

    @Test
    fun newCharacterStartsWithItsOwnFiveDefaultCurrencies() = withRepositories { campaigns, characters ->
        val campaign = campaigns.createCampaign("Terramore")
        val first = characters.createCharacter(campaign.id, "Primero")
        val second = characters.createCharacter(campaign.id, "Segundo")

        assertEquals(listOf("Cobre", "Plata", "Electro", "Oro", "Platino"), first.currencies.map { it.name })
        assertEquals(listOf("Cobre", "Plata", "Electro", "Oro", "Platino"), second.currencies.map { it.name })
        assertTrue(first.currencies.all { it.isDefault && it.amount == 0 })
    }

    @Test
    fun followupCharacterDomainsPersistWithManualOrderAndPerUnitWeight() = withRepositories { campaigns, characters ->
        val campaign = campaigns.createCampaign("Terramore")
        val created = characters.createCharacter(campaign.id, "Vanya")
        val customCurrency = CharacterCurrency(
            key = Uuid.random().toString(),
            name = "Diamante Astral",
            amount = 3,
            sortOrder = 5,
            isDefault = false,
        )

        val saved = characters.saveCharacter(
            created.copy(
                classes = listOf(
                    CharacterClassLevel(Uuid.random(), "Mago", 0, 6, 0, 0),
                ),
                proficiencyBonus = 5,
                spellSaveDc = 15,
                spellAttackModifier = 7,
                spellcastingAbility = SpellcastingAbility.INTELLIGENCE,
                spellSlots = listOf(
                    CharacterSpellSlot(level = 1, totalSlots = 4, spentSlots = 2),
                    CharacterSpellSlot(level = 3, totalSlots = 2, spentSlots = 1),
                ),
                combatEntries = listOf(
                    CharacterCombatEntry(
                        id = Uuid.random(),
                        name = "Bola de fuego resumida",
                        type = CharacterCombatEntryType.ACTION,
                        attackModifier = null,
                        damageEffect = "8d6 fuego; salvación DES",
                        rangeText = "150 ft",
                        notes = "Referencia rápida",
                        sortOrder = 0,
                    ),
                    CharacterCombatEntry(
                        id = Uuid.random(),
                        name = "Bastón",
                        type = CharacterCombatEntryType.ATTACK,
                        attackModifier = 4,
                        damageEffect = "1d6+1 contundente",
                        rangeText = "5 ft",
                        notes = null,
                        sortOrder = 1,
                    ),
                ),
                inventoryItems = listOf(
                    CharacterInventoryItem(
                        id = Uuid.random(),
                        name = "Flecha",
                        quantity = 20,
                        weightLb = 0.05,
                        equipped = false,
                        notes = null,
                        sortOrder = 0,
                        special = false,
                        description = null,
                        location = null,
                        attuned = false,
                    ),
                    CharacterInventoryItem(
                        id = Uuid.random(),
                        name = "Amuleto de prueba",
                        quantity = 1,
                        weightLb = 2.0,
                        equipped = true,
                        notes = "Homebrew",
                        sortOrder = 1,
                        special = true,
                        description = "Descripción larga del objeto mágico.",
                        location = "Cuello",
                        attuned = true,
                    ),
                ),
                currencies = created.currencies + customCurrency,
            ),
        )

        assertEquals(0, saved.totalLevel)
        assertEquals(2, saved.standardProficiencyBonus)
        assertEquals(3, saved.proficiencyBonusAdjustment)
        assertEquals(5, saved.finalProficiencyBonus)
        assertEquals(15, saved.spellSaveDc)
        assertEquals(7, saved.spellAttackModifier)
        assertEquals(SpellcastingAbility.INTELLIGENCE, saved.spellcastingAbility)
        assertEquals(listOf(1, 3), saved.spellSlots.map { it.level })
        assertEquals(listOf("Bola de fuego resumida", "Bastón"), saved.combatEntries.map { it.name })
        assertEquals(listOf("Flecha", "Amuleto de prueba"), saved.inventoryItems.map { it.name })
        assertEquals(3.0, saved.carriedWeightLb, 0.000001)
        assertEquals(1, saved.attunedItemCount)
        assertEquals("Cuello", saved.inventoryItems.last().location)
        assertEquals("Diamante Astral", saved.currencies.last().name)

        val other = characters.createCharacter(campaign.id, "Otro")
        assertFalse(other.currencies.any { it.name == "Diamante Astral" })
    }

    @Test
    fun v4MigrationPreservesProficiencyAndSpellDcAndSeedsCurrency() {
        val file = File.createTempFile("dnd-custom-aid-v4-followup", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000000001"
        val characterId = "00000000-0000-0000-0000-000000000010"
        val classId = "00000000-0000-0000-0000-000000000020"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { s ->
                    s.executeUpdate("PRAGMA foreign_keys=ON")
                    s.executeUpdate("CREATE TABLE campaign (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    s.executeUpdate("CREATE TABLE app_state (singleton INTEGER NOT NULL PRIMARY KEY CHECK (singleton = 1), active_campaign_id TEXT REFERENCES campaign(id))")
                    s.executeUpdate("INSERT INTO campaign(id, name) VALUES ('$campaignId', 'V4')")
                    s.executeUpdate(
                        """CREATE TABLE character (
                            id TEXT NOT NULL PRIMARY KEY,
                            campaign_id TEXT NOT NULL REFERENCES campaign(id) ON DELETE CASCADE,
                            name TEXT NOT NULL,
                            status TEXT NOT NULL,
                            updated_at_epoch_seconds INTEGER NOT NULL DEFAULT 0,
                            strength INTEGER NOT NULL DEFAULT 10,
                            dexterity INTEGER NOT NULL DEFAULT 10,
                            constitution INTEGER NOT NULL DEFAULT 10,
                            intelligence INTEGER NOT NULL DEFAULT 10,
                            wisdom INTEGER NOT NULL DEFAULT 10,
                            charisma INTEGER NOT NULL DEFAULT 10,
                            armor_class INTEGER NOT NULL DEFAULT 10,
                            max_hp INTEGER NOT NULL DEFAULT 1,
                            current_hp INTEGER NOT NULL DEFAULT 1,
                            temp_hp INTEGER NOT NULL DEFAULT 0,
                            initiative_modifier INTEGER NOT NULL DEFAULT 0,
                            speed INTEGER NOT NULL DEFAULT 30,
                            proficiency_bonus INTEGER NOT NULL DEFAULT 2,
                            strength_save INTEGER NOT NULL DEFAULT 0,
                            dexterity_save INTEGER NOT NULL DEFAULT 0,
                            constitution_save INTEGER NOT NULL DEFAULT 0,
                            intelligence_save INTEGER NOT NULL DEFAULT 0,
                            wisdom_save INTEGER NOT NULL DEFAULT 0,
                            charisma_save INTEGER NOT NULL DEFAULT 0,
                            passive_perception INTEGER NOT NULL DEFAULT 10,
                            spell_save_dc INTEGER,
                            initiative_adjustment INTEGER NOT NULL DEFAULT 0,
                            passive_perception_adjustment INTEGER NOT NULL DEFAULT 0
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        """CREATE TABLE character_class (
                            id TEXT NOT NULL PRIMARY KEY,
                            character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                            name TEXT NOT NULL,
                            level INTEGER NOT NULL,
                            hit_die_sides INTEGER NOT NULL,
                            hit_dice_remaining INTEGER NOT NULL,
                            sort_order INTEGER NOT NULL,
                            UNIQUE(character_id, sort_order)
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        """CREATE TABLE character_save (
                            character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                            ability_key TEXT NOT NULL,
                            proficient INTEGER NOT NULL DEFAULT 0,
                            adjustment INTEGER NOT NULL DEFAULT 0,
                            PRIMARY KEY(character_id, ability_key)
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        """CREATE TABLE character_skill (
                            character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                            skill_key TEXT NOT NULL,
                            modifier INTEGER NOT NULL DEFAULT 0,
                            training TEXT NOT NULL,
                            adjustment INTEGER NOT NULL DEFAULT 0,
                            PRIMARY KEY(character_id, skill_key)
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        """INSERT INTO character(
                            id,campaign_id,name,status,proficiency_bonus,spell_save_dc
                        ) VALUES ('$characterId','$campaignId','Migrado','ACTIVE',4,16)""".trimIndent(),
                    )
                    s.executeUpdate(
                        """INSERT INTO character_class(
                            id,character_id,name,level,hit_die_sides,hit_dice_remaining,sort_order
                        ) VALUES ('$classId','$characterId','Mago',4,6,4,0)""".trimIndent(),
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(
                driver = driver,
                oldVersion = 3,
                newVersion = AppDatabase.Schema.version,
            )
            val migrated = CharacterRepository(AppDatabase(driver)).character(Uuid.parse(characterId))

            assertNotNull(migrated)
            assertEquals(4, migrated.totalLevel)
            assertEquals(2, migrated.standardProficiencyBonus)
            assertEquals(2, migrated.proficiencyBonusAdjustment)
            assertEquals(4, migrated.finalProficiencyBonus)
            assertEquals(16, migrated.spellSaveDc)
            assertEquals(5, migrated.currencies.size)
            assertTrue(migrated.currencies.all { it.isDefault && it.amount == 0 })
            assertTrue(migrated.spellSlots.isEmpty())
            assertTrue(migrated.combatEntries.isEmpty())
            assertTrue(migrated.inventoryItems.isEmpty())
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
