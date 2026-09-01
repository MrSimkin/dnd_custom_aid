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

class CharacterNextBuildFoundationTest {
    @Test
    fun newCharacterStartsWithEmptyNextBuildDomainsAndCasterOff() = withRepositories { campaigns, characters ->
        val campaign = campaigns.createCampaign("Terramore")
        val created = characters.createCharacter(campaign.id, "Sin magia")

        assertFalse(created.spellcasterEnabled)
        assertEquals(CharacterBackground(), created.background)
        assertTrue(created.traits.isEmpty())
        assertTrue(created.spellcastingSources.isEmpty())
        assertTrue(created.spells.isEmpty())
        assertEquals("", created.generalNotes)
        assertTrue(created.noteCards.isEmpty())
        assertTrue(created.spellSlots.isEmpty())
    }

    @Test
    fun nextBuildDomainsRoundTripWithMultiSourcePreparedStateAndSoftClassUnlink() =
        withRepositories { campaigns, characters ->
            val campaign = campaigns.createCampaign("Terramore")
            val created = characters.createCharacter(campaign.id, "Multiclase")
            val wizardClassId = Uuid.random()
            val wizardSourceId = Uuid.random()
            val featSourceId = Uuid.random()
            val spellId = Uuid.random()

            val saved = characters.saveCharacter(
                created.copy(
                    classes = listOf(
                        CharacterClassLevel(
                            id = wizardClassId,
                            name = "Mago",
                            level = 3,
                            hitDieSides = 6,
                            hitDiceRemaining = 3,
                            sortOrder = 0,
                        ),
                    ),
                    spellcasterEnabled = true,
                    background = CharacterBackground(
                        name = "Erudito",
                        summary = "Investigador de ruinas.",
                        personalityTraits = "Curioso",
                        ideals = "Conocimiento",
                        bonds = "Biblioteca de casa",
                        flaws = "No sabe cuándo detenerse",
                        story = "Una historia deliberadamente más larga.",
                    ),
                    traits = listOf(
                        CharacterTrait(
                            id = Uuid.random(),
                            name = "Recuperación arcana",
                            source = "Mago",
                            type = CharacterTraitType.CLASS,
                            description = "Recupera energía mágica.",
                            notes = "Manual",
                            maxUses = 2,
                            spentUses = 1,
                            recovery = "Descanso largo",
                            activation = CharacterActivationType.ACTION,
                            sortOrder = 0,
                        ),
                        CharacterTrait(
                            id = Uuid.random(),
                            name = "Ojo del cronista",
                            source = "Campaña",
                            type = CharacterTraitType.OTHER,
                            description = "Rasgo homebrew.",
                            notes = null,
                            maxUses = null,
                            spentUses = 0,
                            recovery = null,
                            activation = CharacterActivationType.PASSIVE,
                            sortOrder = 1,
                        ),
                    ),
                    spellcastingSources = listOf(
                        CharacterSpellcastingSource(
                            id = wizardSourceId,
                            name = "Mago",
                            linkedClassId = wizardClassId,
                            sortOrder = 0,
                        ),
                        CharacterSpellcastingSource(
                            id = featSourceId,
                            name = "Dote: Iniciado en la Magia",
                            linkedClassId = null,
                            sortOrder = 1,
                        ),
                    ),
                    spells = listOf(
                        CharacterSpell(
                            id = spellId,
                            name = "Detectar magia",
                            level = 1,
                            castingTime = "1 acción",
                            rangeText = "Personal",
                            verbal = true,
                            somatic = true,
                            material = false,
                            materialText = null,
                            duration = "10 minutos",
                            concentration = true,
                            ritual = true,
                            description = "Detecta magia cercana.",
                            notes = "Referencia de prueba",
                            sortOrder = 0,
                            sourceAssociations = listOf(
                                CharacterSpellSourceAssociation(wizardSourceId, prepared = true),
                                CharacterSpellSourceAssociation(featSourceId, prepared = false),
                            ),
                        ),
                        CharacterSpell(
                            id = Uuid.random(),
                            name = "Luz",
                            level = 0,
                            castingTime = "1 acción",
                            rangeText = "Toque",
                            verbal = true,
                            somatic = false,
                            material = true,
                            materialText = "Una luciérnaga",
                            duration = "1 hora",
                            concentration = false,
                            ritual = false,
                            description = "Produce luz.",
                            notes = null,
                            sortOrder = 0,
                            sourceAssociations = listOf(
                                CharacterSpellSourceAssociation(wizardSourceId, prepared = false),
                            ),
                        ),
                    ),
                    generalNotes = "Notas generales de campaña.",
                    noteCards = listOf(
                        CharacterNote(Uuid.random(), "NPCs", "Alguien sospechoso.", 0),
                        CharacterNote(Uuid.random(), "Objetivos", "Volver a la torre.", 1),
                    ),
                    spellSlots = listOf(CharacterSpellSlot(level = 1, totalSlots = 4, spentSlots = 2)),
                ),
            )

            assertTrue(saved.spellcasterEnabled)
            assertEquals("Erudito", saved.background.name)
            assertEquals(listOf("Recuperación arcana", "Ojo del cronista"), saved.traits.map { it.name })
            assertEquals(listOf("Mago", "Dote: Iniciado en la Magia"), saved.spellcastingSources.map { it.name })
            assertEquals(wizardClassId, saved.spellcastingSources.first().linkedClassId)
            assertEquals(listOf("Luz", "Detectar magia"), saved.spells.map { it.name })
            val detect = saved.spells.single { it.id == spellId }
            assertEquals(
                mapOf(wizardSourceId to true, featSourceId to false),
                detect.sourceAssociations.associate { it.sourceId to it.prepared },
            )
            assertEquals("Notas generales de campaña.", saved.generalNotes)
            assertEquals(listOf("NPCs", "Objetivos"), saved.noteCards.map { it.title })
            assertEquals(2, saved.spellSlots.single().spentSlots)

            val hidden = characters.saveCharacter(saved.copy(spellcasterEnabled = false))
            assertFalse(hidden.spellcasterEnabled)
            assertEquals(2, hidden.spellcastingSources.size)
            assertEquals(2, hidden.spells.size)
            assertEquals(2, hidden.spellSlots.single().spentSlots)

            val classRemoved = characters.saveCharacter(hidden.copy(classes = emptyList()))
            assertNull(classRemoved.spellcastingSources.first { it.id == wizardSourceId }.linkedClassId)
            assertEquals(2, classRemoved.spellcastingSources.size)
            assertEquals(2, classRemoved.spells.size)
            assertEquals(2, classRemoved.spells.single { it.id == spellId }.sourceAssociations.size)
        }

    @Test
    fun migrationFromRun180SchemaDerivesCasterVisibilityWithoutInventingSpellDomains() {
        val file = File.createTempFile("dnd-custom-aid-next-build", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val campaignId = "00000000-0000-0000-0000-000000000001"
        val dcId = "00000000-0000-0000-0000-000000000011"
        val attackId = "00000000-0000-0000-0000-000000000012"
        val abilityId = "00000000-0000-0000-0000-000000000013"
        val slotId = "00000000-0000-0000-0000-000000000014"
        val mundaneId = "00000000-0000-0000-0000-000000000015"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { s ->
                    s.executeUpdate("PRAGMA foreign_keys=ON")
                    s.executeUpdate("CREATE TABLE campaign (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    s.executeUpdate("INSERT INTO campaign(id, name) VALUES ('$campaignId', 'Run180')")
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
                            spellcasting_ability TEXT NOT NULL DEFAULT 'NONE'
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

                    s.executeUpdate("INSERT INTO character(id,campaign_id,name,status,spell_save_dc) VALUES ('$dcId','$campaignId','CD','ACTIVE',14)")
                    s.executeUpdate("INSERT INTO character(id,campaign_id,name,status,spell_attack_modifier) VALUES ('$attackId','$campaignId','Ataque','ACTIVE',6)")
                    s.executeUpdate("INSERT INTO character(id,campaign_id,name,status,spellcasting_ability) VALUES ('$abilityId','$campaignId','Aptitud','ACTIVE','WISDOM')")
                    s.executeUpdate("INSERT INTO character(id,campaign_id,name,status) VALUES ('$slotId','$campaignId','Espacios','ACTIVE')")
                    s.executeUpdate("INSERT INTO character_spell_slot(character_id,spell_level,total_slots,spent_slots) VALUES ('$slotId',2,3,1)")
                    s.executeUpdate("INSERT INTO character(id,campaign_id,name,status) VALUES ('$mundaneId','$campaignId','Mundano','ACTIVE')")
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(
                driver = driver,
                oldVersion = 4,
                newVersion = AppDatabase.Schema.version,
            )
            val repository = CharacterRepository(AppDatabase(driver))

            val byId = listOf(dcId, attackId, abilityId, slotId, mundaneId).associateWith { id ->
                assertNotNull(repository.character(Uuid.parse(id)))
            }

            assertTrue(byId.getValue(dcId).spellcasterEnabled)
            assertEquals(14, byId.getValue(dcId).spellSaveDc)
            assertTrue(byId.getValue(attackId).spellcasterEnabled)
            assertEquals(6, byId.getValue(attackId).spellAttackModifier)
            assertTrue(byId.getValue(abilityId).spellcasterEnabled)
            assertEquals(SpellcastingAbility.WISDOM, byId.getValue(abilityId).spellcastingAbility)
            assertTrue(byId.getValue(slotId).spellcasterEnabled)
            assertEquals(CharacterSpellSlot(2, 3, 1), byId.getValue(slotId).spellSlots.single())
            assertFalse(byId.getValue(mundaneId).spellcasterEnabled)

            byId.values.forEach { migrated ->
                assertEquals(CharacterBackground(), migrated.background)
                assertTrue(migrated.traits.isEmpty())
                assertTrue(migrated.spellcastingSources.isEmpty())
                assertTrue(migrated.spells.isEmpty())
                assertEquals("", migrated.generalNotes)
                assertTrue(migrated.noteCards.isEmpty())
            }
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
