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
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterOwnerLineageMigrationTest {
    @Test
    fun ownerQaSchema5MigratesToCurrentWithoutLosingProtectedCharacterData() {
        val file = File.createTempFile("dnd-custom-aid-owner-lineage", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"

        val campaignId = "50000000-0000-0000-0000-000000000001"
        val characterId = "50000000-0000-0000-0000-000000000002"
        val classId = "50000000-0000-0000-0000-000000000003"
        val combatId = "50000000-0000-0000-0000-000000000004"
        val inventoryId = "50000000-0000-0000-0000-000000000005"
        val traitId = "50000000-0000-0000-0000-000000000006"
        val noteId = "50000000-0000-0000-0000-000000000007"
        val spellSourceId = "50000000-0000-0000-0000-000000000008"
        val spellId = "50000000-0000-0000-0000-000000000009"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { s ->
                    s.executeUpdate("PRAGMA foreign_keys=ON")
                    createOwnerQaSchema5(s)

                    s.executeUpdate("INSERT INTO campaign(id, name) VALUES ('$campaignId', 'Owner QA')")
                    s.executeUpdate("INSERT INTO app_state(singleton, active_campaign_id) VALUES (1, '$campaignId')")
                    s.executeUpdate(
                        """INSERT INTO character(
                            id, campaign_id, name, status, updated_at_epoch_seconds,
                            strength, dexterity, constitution, intelligence, wisdom, charisma,
                            armor_class, max_hp, current_hp, temp_hp, initiative_modifier, speed,
                            proficiency_bonus, strength_save, dexterity_save, constitution_save,
                            intelligence_save, wisdom_save, charisma_save, passive_perception,
                            spell_save_dc, initiative_adjustment, passive_perception_adjustment,
                            proficiency_bonus_adjustment, spell_attack_modifier, spellcasting_ability,
                            spellcaster_enabled, general_notes
                        ) VALUES (
                            '$characterId', '$campaignId', 'Vanya owner QA', 'ACTIVE', 1700000000,
                            10, 14, 12, 18, 13, 8,
                            15, 40, 31, 4, 3, 30,
                            3, 0, 0, 0, 0, 0, 0, 14,
                            16, 1, 2, 0, 8, 'INTELLIGENCE',
                            1, 'Notas owner preservadas'
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        """INSERT INTO character_class(
                            id, character_id, name, level, hit_die_sides, hit_dice_remaining, sort_order
                        ) VALUES ('$classId', '$characterId', 'Mago', 7, 6, 4, 0)""".trimIndent(),
                    )
                    s.executeUpdate(
                        "INSERT INTO character_save(character_id, ability_key, proficient, adjustment) VALUES ('$characterId', 'WISDOM', 1, 2)",
                    )
                    s.executeUpdate(
                        "INSERT INTO character_skill(character_id, skill_key, modifier, training, adjustment) VALUES ('$characterId', 'PERCEPTION', 9, 'EXPERTISE', 1)",
                    )
                    s.executeUpdate(
                        "INSERT INTO character_spell_slot(character_id, spell_level, total_slots, spent_slots) VALUES ('$characterId', 3, 3, 1)",
                    )
                    s.executeUpdate(
                        """INSERT INTO character_combat_entry(
                            id, character_id, name, type, attack_modifier, damage_effect, range_text, notes, sort_order
                        ) VALUES (
                            '$combatId', '$characterId', 'Bastón', 'ATTACK', 5,
                            '1d8+2 contundente', '5 pies', 'Entrada legacy', 0
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        """INSERT INTO character_inventory_item(
                            id, character_id, name, quantity, weight_lb, equipped, notes, sort_order,
                            special, description, location, attuned
                        ) VALUES (
                            '$inventoryId', '$characterId', 'Varita del archivo', 2, 1.5, 1,
                            'Equipo legacy', 0, 1, 'Descripción preservada', 'Mochila', 1
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        """INSERT INTO character_currency(
                            character_id, currency_key, name, amount, sort_order, is_default
                        ) VALUES ('$characterId', 'GP', 'PO', 123, 0, 1)""".trimIndent(),
                    )
                    s.executeUpdate(
                        """INSERT INTO character_background(
                            character_id, background_name, summary, personality_traits,
                            ideals, bonds, flaws, story
                        ) VALUES (
                            '$characterId', 'Erudito', 'Resumen owner', 'Curioso',
                            'Conocimiento', 'Biblioteca', 'Obsesivo', 'Historia owner preservada'
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        """INSERT INTO character_trait(
                            id, character_id, name, source, trait_type, description, notes,
                            max_uses, spent_uses, recovery, activation, sort_order
                        ) VALUES (
                            '$traitId', '$characterId', 'Mente aguda', 'PHB', 'FEAT',
                            'Recuerda detalles', 'Rasgo legacy', 2, 1, 'Descanso largo', 'BONUS_ACTION', 0
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        "INSERT INTO character_note(id, character_id, title, content, sort_order) VALUES ('$noteId', '$characterId', 'Bitácora', 'Nota owner preservada', 0)",
                    )
                    s.executeUpdate(
                        "INSERT INTO character_spell_source(id, character_id, name, linked_class_id, sort_order) VALUES ('$spellSourceId', '$characterId', 'Mago', '$classId', 0)",
                    )
                    s.executeUpdate(
                        """INSERT INTO character_spell(
                            id, character_id, name, spell_level, casting_time, range_text,
                            has_verbal, has_somatic, has_material, material_text, duration,
                            concentration, ritual, description, notes, sort_order
                        ) VALUES (
                            '$spellId', '$characterId', 'Volar', 3, '1 acción', 'Toque',
                            1, 1, 1, 'una pluma', '10 minutos', 1, 0,
                            'Concede vuelo', 'Conjuro legacy', 0
                        )""".trimIndent(),
                    )
                    s.executeUpdate(
                        "INSERT INTO character_spell_source_assoc(spell_id, source_id, prepared) VALUES ('$spellId', '$spellSourceId', 1)",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(
                driver = driver,
                oldVersion = 5,
                newVersion = AppDatabase.Schema.version,
            )
            val database = AppDatabase(driver)

            val campaign = CampaignRepository(database).listCampaigns().single()
            assertEquals("Owner QA", campaign.name)
            assertEquals(campaignId, campaign.id.toString())

            val id = Uuid.parse(characterId)
            val character = assertNotNull(CharacterRepository(database).character(id))
            assertEquals("Vanya owner QA", character.name)
            assertEquals(CharacterStatus.ACTIVE, character.status)
            assertEquals(31, character.currentHp)
            assertEquals(4, character.tempHp)
            assertEquals(1, character.initiativeAdjustment)
            assertEquals(16, character.spellSaveDc)
            assertEquals(8, character.spellAttackModifier)
            assertEquals(SpellcastingAbility.INTELLIGENCE, character.spellcastingAbility)
            assertTrue(character.spellcasterEnabled)
            assertEquals("Notas owner preservadas", character.generalNotes)

            val migratedClass = character.classes.single()
            assertEquals(classId, migratedClass.id.toString())
            assertEquals("Mago", migratedClass.name)
            assertEquals(7, migratedClass.level)
            assertEquals(6, migratedClass.hitDieSides)
            assertEquals(4, migratedClass.hitDiceRemaining)
            assertEquals(CharacterRulesFamily.UNSPECIFIED, migratedClass.rulesFamily)
            assertNull(migratedClass.source)
            assertNull(migratedClass.subclassName)

            val wisdomSave = character.savingThrow(CharacterAbility.WISDOM)
            assertTrue(wisdomSave.proficient)
            assertEquals(2, wisdomSave.adjustment)
            val perception = character.skill(SkillKey.PERCEPTION)
            assertEquals(SkillTraining.EXPERTISE, perception.training)
            assertEquals(1, perception.adjustment)

            assertEquals(CharacterSpellSlot(level = 3, totalSlots = 3, spentSlots = 1), character.spellSlots.single())

            val combat = character.combatEntries.single()
            assertEquals(combatId, combat.id.toString())
            assertEquals("Bastón", combat.name)
            assertEquals(CharacterCombatEntryType.ATTACK, combat.type)
            assertEquals(5, combat.attackModifier)
            assertEquals("1d8+2 contundente", combat.damageEffect)
            assertEquals("5 pies", combat.rangeText)
            assertFalse(combat.pinned)

            val item = character.inventoryItems.single()
            assertEquals(inventoryId, item.id.toString())
            assertEquals("Varita del archivo", item.name)
            assertEquals(2, item.quantity)
            assertEquals(1.5, item.weightLb)
            assertTrue(item.equipped)
            assertTrue(item.special)
            assertEquals("Descripción preservada", item.description)
            assertEquals("Mochila", item.location)
            assertTrue(item.attuned)

            val currency = character.currencies.single()
            assertEquals("GP", currency.key)
            assertEquals("PO", currency.name)
            assertEquals(123, currency.amount)
            assertTrue(currency.isDefault)

            assertEquals("Erudito", character.background.name)
            assertEquals("Resumen owner", character.background.summary)
            assertEquals("", character.background.race)
            assertEquals("", character.background.religionFaith)
            assertEquals("Curioso", character.background.personalityTraits)
            assertEquals("Historia owner preservada", character.background.story)

            val trait = character.traits.single()
            assertEquals(traitId, trait.id.toString())
            assertEquals("Mente aguda", trait.name)
            assertEquals(CharacterTraitType.FEAT, trait.type)
            assertEquals(2, trait.maxUses)
            assertEquals(1, trait.spentUses)
            assertEquals(CharacterActivationType.BONUS_ACTION, trait.activation)
            assertFalse(trait.pinned)

            val note = character.noteCards.single()
            assertEquals(noteId, note.id.toString())
            assertEquals("Bitácora", note.title)
            assertEquals("Nota owner preservada", note.content)

            val source = character.spellcastingSources.single()
            assertEquals(spellSourceId, source.id.toString())
            assertEquals(classId, source.linkedClassId?.toString())
            val spell = character.spells.single()
            assertEquals(spellId, spell.id.toString())
            assertEquals("Volar", spell.name)
            assertEquals(3, spell.level)
            assertEquals("Toque", spell.rangeText)
            assertTrue(spell.concentration)
            assertFalse(spell.ritual)
            assertFalse(spell.pinned)
            assertEquals(spellSourceId, spell.sourceAssociations.single().sourceId.toString())
            assertTrue(spell.sourceAssociations.single().prepared)

            assertFalse(character.inspiration)
            assertEquals(0, character.deathSaveSuccesses)
            assertEquals(0, character.deathSaveFailures)
            assertTrue(character.proficiencies.isEmpty())
            assertTrue(character.weaponMasteries.isEmpty())
            assertTrue(character.resources.isEmpty())
            assertTrue(character.classOptions.isEmpty())
            assertTrue(character.forms.isEmpty())
            assertTrue(character.companions.isEmpty())

            assertEquals(CharacterClosureState(), CharacterClosureRepository(database).state(id))
            driver.close()
        } finally {
            file.delete()
        }
    }

    private fun createOwnerQaSchema5(s: java.sql.Statement) {
        s.executeUpdate("CREATE TABLE campaign (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
        s.executeUpdate(
            "CREATE TABLE app_state (singleton INTEGER NOT NULL PRIMARY KEY CHECK (singleton = 1), active_campaign_id TEXT REFERENCES campaign(id))",
        )
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
                passive_perception_adjustment INTEGER NOT NULL DEFAULT 0,
                proficiency_bonus_adjustment INTEGER NOT NULL DEFAULT 0,
                spell_attack_modifier INTEGER,
                spellcasting_ability TEXT NOT NULL DEFAULT 'NONE',
                spellcaster_enabled INTEGER NOT NULL DEFAULT 0,
                general_notes TEXT NOT NULL DEFAULT ''
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
            """CREATE TABLE character_spell_slot (
                character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                spell_level INTEGER NOT NULL,
                total_slots INTEGER NOT NULL DEFAULT 0,
                spent_slots INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(character_id, spell_level)
            )""".trimIndent(),
        )
        s.executeUpdate(
            """CREATE TABLE character_combat_entry (
                id TEXT NOT NULL PRIMARY KEY,
                character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                attack_modifier INTEGER,
                damage_effect TEXT NOT NULL DEFAULT '',
                range_text TEXT,
                notes TEXT,
                sort_order INTEGER NOT NULL,
                UNIQUE(character_id, sort_order)
            )""".trimIndent(),
        )
        s.executeUpdate(
            """CREATE TABLE character_inventory_item (
                id TEXT NOT NULL PRIMARY KEY,
                character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                name TEXT NOT NULL,
                quantity INTEGER NOT NULL DEFAULT 1,
                weight_lb REAL,
                equipped INTEGER NOT NULL DEFAULT 0,
                notes TEXT,
                sort_order INTEGER NOT NULL,
                special INTEGER NOT NULL DEFAULT 0,
                description TEXT,
                location TEXT,
                attuned INTEGER NOT NULL DEFAULT 0,
                UNIQUE(character_id, sort_order)
            )""".trimIndent(),
        )
        s.executeUpdate(
            """CREATE TABLE character_currency (
                character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                currency_key TEXT NOT NULL,
                name TEXT NOT NULL,
                amount INTEGER NOT NULL DEFAULT 0,
                sort_order INTEGER NOT NULL,
                is_default INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(character_id, currency_key),
                UNIQUE(character_id, sort_order)
            )""".trimIndent(),
        )
        s.executeUpdate(
            """CREATE TABLE character_background (
                character_id TEXT NOT NULL PRIMARY KEY REFERENCES character(id) ON DELETE CASCADE,
                background_name TEXT NOT NULL DEFAULT '',
                summary TEXT NOT NULL DEFAULT '',
                personality_traits TEXT NOT NULL DEFAULT '',
                ideals TEXT NOT NULL DEFAULT '',
                bonds TEXT NOT NULL DEFAULT '',
                flaws TEXT NOT NULL DEFAULT '',
                story TEXT NOT NULL DEFAULT ''
            )""".trimIndent(),
        )
        s.executeUpdate(
            """CREATE TABLE character_trait (
                id TEXT NOT NULL PRIMARY KEY,
                character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                name TEXT NOT NULL,
                source TEXT NOT NULL DEFAULT '',
                trait_type TEXT NOT NULL,
                description TEXT NOT NULL DEFAULT '',
                notes TEXT,
                max_uses INTEGER,
                spent_uses INTEGER NOT NULL DEFAULT 0,
                recovery TEXT,
                activation TEXT,
                sort_order INTEGER NOT NULL,
                UNIQUE(character_id, sort_order)
            )""".trimIndent(),
        )
        s.executeUpdate(
            """CREATE TABLE character_note (
                id TEXT NOT NULL PRIMARY KEY,
                character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                title TEXT NOT NULL,
                content TEXT NOT NULL DEFAULT '',
                sort_order INTEGER NOT NULL,
                UNIQUE(character_id, sort_order)
            )""".trimIndent(),
        )
        s.executeUpdate(
            """CREATE TABLE character_spell_source (
                id TEXT NOT NULL PRIMARY KEY,
                character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                name TEXT NOT NULL,
                linked_class_id TEXT,
                sort_order INTEGER NOT NULL,
                UNIQUE(character_id, sort_order)
            )""".trimIndent(),
        )
        s.executeUpdate(
            """CREATE TABLE character_spell (
                id TEXT NOT NULL PRIMARY KEY,
                character_id TEXT NOT NULL REFERENCES character(id) ON DELETE CASCADE,
                name TEXT NOT NULL,
                spell_level INTEGER NOT NULL,
                casting_time TEXT NOT NULL DEFAULT '',
                range_text TEXT NOT NULL DEFAULT '',
                has_verbal INTEGER NOT NULL DEFAULT 0,
                has_somatic INTEGER NOT NULL DEFAULT 0,
                has_material INTEGER NOT NULL DEFAULT 0,
                material_text TEXT,
                duration TEXT NOT NULL DEFAULT '',
                concentration INTEGER NOT NULL DEFAULT 0,
                ritual INTEGER NOT NULL DEFAULT 0,
                description TEXT NOT NULL DEFAULT '',
                notes TEXT,
                sort_order INTEGER NOT NULL,
                UNIQUE(character_id, spell_level, sort_order)
            )""".trimIndent(),
        )
        s.executeUpdate(
            """CREATE TABLE character_spell_source_assoc (
                spell_id TEXT NOT NULL REFERENCES character_spell(id) ON DELETE CASCADE,
                source_id TEXT NOT NULL REFERENCES character_spell_source(id) ON DELETE CASCADE,
                prepared INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(spell_id, source_id)
            )""".trimIndent(),
        )
    }
}
