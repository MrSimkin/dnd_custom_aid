package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterSuccessorLegacyRewriteTest {
    @Test
    fun successorExtensionsSurviveLegacyCoreAndClosureDeleteReinsertSaves() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val closure = CharacterClosureRepository(database)
            val successor = CharacterSuccessorRepository(database)
            val campaign = campaigns.createCampaign("Compatibilidad")
            val base = characters.createCharacter(campaign.id, "Persistente")

            val sourceId = Uuid.random()
            val combatId = Uuid.random()
            val resourceId = Uuid.random()
            val skillId = Uuid.random()
            val attributeId = Uuid.random()

            val sheet = characters.saveCharacter(
                base.copy(
                    combatEntries = listOf(
                        CharacterCombatEntry(
                            id = combatId,
                            name = "Golpe arcano",
                            type = CharacterCombatEntryType.ATTACK,
                            attackModifier = 7,
                            damageEffect = "legacy",
                            rangeText = "5 pies",
                            notes = null,
                            sortOrder = 0,
                        ),
                    ),
                    spellcastingSources = listOf(
                        CharacterSpellcastingSource(
                            id = sourceId,
                            name = "Fuente propia",
                            linkedClassId = null,
                            sortOrder = 0,
                        ),
                    ),
                    resources = listOf(
                        CharacterResource(
                            id = resourceId,
                            name = "Impulso",
                            currentValue = 2,
                            maxValue = 4,
                            sortOrder = 0,
                        ),
                    ),
                ),
            )
            closure.saveState(
                sheet.id,
                CharacterClosureState(
                    customSkills = listOf(
                        CharacterCustomSkill(
                            id = skillId,
                            name = "Sincronía",
                            ability = CharacterAbility.CHARISMA,
                            training = SkillTraining.PROFICIENT,
                            sortOrder = 0,
                        ),
                    ),
                ),
            )

            val savedSuccessor = successor.saveState(
                sheet.id,
                successor.state(sheet.id).copy(
                    customAttributes = listOf(
                        CharacterCustomAttribute(
                            id = attributeId,
                            name = "Suerte",
                            abbreviation = "SUE",
                            score = 18,
                            savingThrowEnabled = true,
                            savingThrowProficient = true,
                        ),
                    ),
                    customSkillAbilities = listOf(
                        CharacterCustomSkillAbilityConfiguration(
                            customSkillId = skillId,
                            ability = CharacterAbilityReference.custom(attributeId),
                        ),
                    ),
                    spellcastingProfiles = listOf(
                        CharacterSpellcastingProfile(
                            sourceId = sourceId,
                            ability = CharacterAbilityReference.custom(attributeId),
                            saveDcAdjustment = 1,
                            spellAttackAdjustment = 2,
                        ),
                    ),
                    combatDamage = listOf(
                        CharacterCombatDamageProfile(
                            combatEntryId = combatId,
                            components = listOf(
                                CharacterDamageComponent(CharacterDamageComponentKind.DICE, "2d6", "fuerza"),
                            ),
                        ),
                    ),
                    resourceConfigurations = listOf(
                        CharacterResourceSuccessorConfiguration(
                            resourceId = resourceId,
                            valueKind = CharacterTrackableValueKind.CURRENT_MAX,
                            placements = setOf(CharacterResourcePlacement.GENERAL, CharacterResourcePlacement.EQUIPMENT),
                        ),
                    ),
                ),
            )

            // Both legacy repositories rebuild child tables with delete/reinsert using the same IDs.
            characters.saveCharacter(assertNotNull(characters.character(sheet.id)).copy(name = "Persistente 2"))
            val currentClosure = closure.state(sheet.id)
            closure.saveState(sheet.id, currentClosure.copy(tableModeEnabled = true))

            val reopened = successor.state(sheet.id)
            assertEquals(savedSuccessor.customAttributes, reopened.customAttributes)
            assertEquals(savedSuccessor.customSkillAbilities, reopened.customSkillAbilities)
            assertEquals(savedSuccessor.spellcastingProfiles, reopened.spellcastingProfiles)
            assertEquals(savedSuccessor.combatDamage, reopened.combatDamage)
            assertEquals(savedSuccessor.resourceConfigurations, reopened.resourceConfigurations)
        } finally {
            driver.close()
        }
    }

    @Test
    fun migrationElevenPreservesExtensionRowsAndRemovesChildCascadeOwnership() {
        val file = File.createTempFile("dnd-custom-aid-schema11", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val characterId = "00000000-0000-0000-0000-000000000201"
        val skillId = "00000000-0000-0000-0000-000000000202"
        val sourceId = "00000000-0000-0000-0000-000000000203"
        val combatId = "00000000-0000-0000-0000-000000000204"
        val resourceId = "00000000-0000-0000-0000-000000000205"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { s ->
                    s.executeUpdate("PRAGMA foreign_keys=ON")
                    s.executeUpdate("CREATE TABLE character (id TEXT NOT NULL PRIMARY KEY)")
                    s.executeUpdate("CREATE TABLE character_custom_skill (id TEXT NOT NULL PRIMARY KEY)")
                    s.executeUpdate("CREATE TABLE character_spell_source (id TEXT NOT NULL PRIMARY KEY)")
                    s.executeUpdate("CREATE TABLE character_combat_entry (id TEXT NOT NULL PRIMARY KEY)")
                    s.executeUpdate("CREATE TABLE character_resource (id TEXT NOT NULL PRIMARY KEY)")
                    s.executeUpdate("INSERT INTO character(id) VALUES ('$characterId')")
                    s.executeUpdate("INSERT INTO character_custom_skill(id) VALUES ('$skillId')")
                    s.executeUpdate("INSERT INTO character_spell_source(id) VALUES ('$sourceId')")
                    s.executeUpdate("INSERT INTO character_combat_entry(id) VALUES ('$combatId')")
                    s.executeUpdate("INSERT INTO character_resource(id) VALUES ('$resourceId')")

                    s.executeUpdate("CREATE TABLE character_custom_skill_ability (custom_skill_id TEXT NOT NULL PRIMARY KEY REFERENCES character_custom_skill(id) ON DELETE CASCADE, character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE, ability_kind TEXT NOT NULL DEFAULT 'BUILT_IN', ability_value TEXT)")
                    s.executeUpdate("CREATE TABLE character_spell_source_casting (source_id TEXT NOT NULL PRIMARY KEY REFERENCES character_spell_source(id) ON DELETE CASCADE, character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE, ability_kind TEXT NOT NULL DEFAULT 'NONE', ability_value TEXT, save_dc_adjustment INTEGER NOT NULL DEFAULT 0, spell_attack_adjustment INTEGER NOT NULL DEFAULT 0, legacy_save_dc_override INTEGER, legacy_spell_attack_override INTEGER)")
                    s.executeUpdate("CREATE TABLE character_combat_damage_component (combat_entry_id TEXT NOT NULL REFERENCES character_combat_entry(id) ON DELETE CASCADE, character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE, component_order INTEGER NOT NULL, component_kind TEXT NOT NULL, expression TEXT NOT NULL, type_text TEXT, PRIMARY KEY(combat_entry_id, component_order))")
                    s.executeUpdate("CREATE TABLE character_resource_successor_config (resource_id TEXT NOT NULL PRIMARY KEY REFERENCES character_resource(id) ON DELETE CASCADE, character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE, value_kind TEXT NOT NULL DEFAULT 'CURRENT_MAX', placements TEXT NOT NULL DEFAULT 'MANAGEMENT')")

                    s.executeUpdate("INSERT INTO character_custom_skill_ability VALUES ('$skillId', '$characterId', 'BUILT_IN', 'WISDOM')")
                    s.executeUpdate("INSERT INTO character_spell_source_casting VALUES ('$sourceId', '$characterId', 'BUILT_IN', 'INTELLIGENCE', 1, 2, NULL, NULL)")
                    s.executeUpdate("INSERT INTO character_combat_damage_component VALUES ('$combatId', '$characterId', 0, 'DICE', '1d8', 'fuerza')")
                    s.executeUpdate("INSERT INTO character_resource_successor_config VALUES ('$resourceId', '$characterId', 'CURRENT_MAX', 'GENERAL,EQUIPMENT')")
                }
            }

            val migrationDriver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(
                driver = migrationDriver,
                oldVersion = 11,
                newVersion = AppDatabase.Schema.version,
            )
            migrationDriver.close()

            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { s ->
                    s.executeUpdate("PRAGMA foreign_keys=ON")
                    s.executeUpdate("DELETE FROM character_custom_skill WHERE id = '$skillId'")
                    s.executeUpdate("DELETE FROM character_spell_source WHERE id = '$sourceId'")
                    s.executeUpdate("DELETE FROM character_combat_entry WHERE id = '$combatId'")
                    s.executeUpdate("DELETE FROM character_resource WHERE id = '$resourceId'")

                    fun count(table: String): Int = s.executeQuery("SELECT COUNT(*) FROM $table WHERE character_id = '$characterId'").use { result ->
                        result.next()
                        result.getInt(1)
                    }
                    assertEquals(1, count("character_custom_skill_ability"))
                    assertEquals(1, count("character_spell_source_casting"))
                    assertEquals(1, count("character_combat_damage_component"))
                    assertEquals(1, count("character_resource_successor_config"))
                }
            }
        } finally {
            file.delete()
        }
    }

    @Test
    fun generalizedDiceCalculationsUseCustomAttributesAndOptionalCustomSaves() {
        val campaignId = Uuid.random()
        val characterId = Uuid.random()
        val attributeId = Uuid.random()
        val skillId = Uuid.random()
        val base = CharacterSheet(
            id = characterId,
            campaignId = campaignId,
            name = "Dados",
            status = CharacterStatus.ACTIVE,
            updatedAtEpochSeconds = 0,
            strength = 10,
            dexterity = 10,
            constitution = 10,
            intelligence = 10,
            wisdom = 10,
            charisma = 10,
            armorClass = 10,
            maxHp = 1,
            currentHp = 1,
            tempHp = 0,
            initiativeAdjustment = 0,
            speed = 30,
            proficiencyBonus = 3,
            savingThrows = CharacterAbility.entries.map { CharacterSavingThrow(it, false, 0) },
            passivePerceptionAdjustment = 0,
            spellSaveDc = null,
            classes = emptyList(),
            skills = SkillKey.entries.map { CharacterSkill(it, 0, SkillTraining.NONE) },
            proficiencyBonusAdjustment = 1,
        )
        val attribute = CharacterCustomAttribute(
            id = attributeId,
            name = "Suerte",
            abbreviation = "SUE",
            score = 18,
            savingThrowEnabled = true,
            savingThrowProficient = true,
            savingThrowAdjustment = 1,
        )
        val skill = CharacterCustomSkill(
            id = skillId,
            name = "Destino",
            ability = CharacterAbility.STRENGTH,
            training = SkillTraining.EXPERTISE,
            adjustment = -1,
        )
        val state = CharacterSuccessorState(
            customAttributes = listOf(attribute),
            customSkillAbilities = listOf(
                CharacterCustomSkillAbilityConfiguration(skillId, CharacterAbilityReference.custom(attributeId)),
            ),
        )

        // Level 0 standard proficiency is 2; +1 adjustment gives final proficiency 3.
        assertEquals(4, base.abilityModifier(CharacterAbilityReference.custom(attributeId), state))
        assertEquals(9, base.customSkillTotal(skill, state)) // +4 ability +6 expertise -1 adjustment
        assertEquals(8, base.customSavingThrowTotal(attribute)) // +4 ability +3 proficiency +1 adjustment
        assertTrue(attribute.savingThrowEnabled)
    }
}
