package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterSuccessorRepositoryTest {
    @Test
    fun newCharacterUsesEmptySuccessorDefaults() = withRepositories { campaigns, characters, successor ->
        val campaign = campaigns.createCampaign("Sucesor")
        val character = characters.createCharacter(campaign.id, "Vanya")

        assertEquals(CharacterSuccessorState(), successor.state(character.id))
    }

    @Test
    fun legacySpellcastingAndDamageProjectWithoutChangingDisplayedValues() =
        withRepositories { campaigns, characters, successor ->
            val campaign = campaigns.createCampaign("Compatibilidad")
            val created = characters.createCharacter(campaign.id, "Maga")
            val sourceId = Uuid.random()
            val attackId = Uuid.random()
            val saved = characters.saveCharacter(
                created.copy(
                    intelligence = 16,
                    spellcasterEnabled = true,
                    spellcastingAbility = SpellcastingAbility.INTELLIGENCE,
                    spellSaveDc = 17,
                    spellAttackModifier = 9,
                    spellcastingSources = listOf(
                        CharacterSpellcastingSource(sourceId, "Mago", null, 0),
                    ),
                    combatEntries = listOf(
                        CharacterCombatEntry(
                            id = attackId,
                            name = "Espada",
                            type = CharacterCombatEntryType.ATTACK,
                            attackModifier = 6,
                            damageEffect = "1d8 cortante + 4",
                            rangeText = "5 pies",
                            notes = null,
                            sortOrder = 0,
                        ),
                    ),
                ),
            )

            val state = successor.state(saved.id)
            val casting = state.spellcastingProfiles.single()
            assertEquals(CharacterAbility.INTELLIGENCE, casting.ability.builtIn)
            assertEquals(17, saved.spellSaveDc(casting, state))
            assertEquals(9, saved.spellAttackModifier(casting, state))

            val damage = state.combatDamage.single()
            assertEquals(attackId, damage.combatEntryId)
            assertEquals(CharacterDamageComponentKind.TEXT, damage.components.single().kind)
            assertEquals("1d8 cortante + 4", damage.components.single().expression)
        }

    @Test
    fun successorDomainsRoundTripWithCustomAbilityCastingMarkersAndResourcePlacement() =
        withRepositories { campaigns, characters, successor ->
            val campaign = campaigns.createCampaign("Sucesor")
            val created = characters.createCharacter(campaign.id, "Investigadora")
            val sourceId = Uuid.random()
            val attackId = Uuid.random()
            val resourceId = Uuid.random()
            val core = characters.saveCharacter(
                created.copy(
                    spellcasterEnabled = true,
                    spellcastingSources = listOf(CharacterSpellcastingSource(sourceId, "Pacto extraño", null, 0)),
                    combatEntries = listOf(
                        CharacterCombatEntry(
                            id = attackId,
                            name = "Daga ritual",
                            type = CharacterCombatEntryType.ATTACK,
                            attackModifier = 5,
                            damageEffect = "legacy",
                            rangeText = null,
                            notes = null,
                            sortOrder = 0,
                        ),
                    ),
                    resources = listOf(
                        CharacterResource(
                            id = resourceId,
                            name = "Munición",
                            currentValue = 12,
                            maxValue = 20,
                            recovery = null,
                            source = null,
                            notes = null,
                            pinned = true,
                            sortOrder = 0,
                        ),
                    ),
                ),
            )
            val attributeId = Uuid.random()
            val markerBinaryId = Uuid.random()
            val markerCounterId = Uuid.random()

            val savedState = successor.saveState(
                core.id,
                CharacterSuccessorState(
                    customAttributes = listOf(
                        CharacterCustomAttribute(
                            id = attributeId,
                            name = "Cordura",
                            abbreviation = "COR",
                            score = 18,
                            savingThrowEnabled = true,
                            savingThrowProficient = true,
                            savingThrowAdjustment = 1,
                        ),
                    ),
                    spellcastingProfiles = listOf(
                        CharacterSpellcastingProfile(
                            sourceId = sourceId,
                            ability = CharacterAbilityReference.custom(attributeId),
                            saveDcAdjustment = 2,
                            spellAttackAdjustment = -1,
                        ),
                    ),
                    combatDamage = listOf(
                        CharacterCombatDamageProfile(
                            attackId,
                            listOf(
                                CharacterDamageComponent(CharacterDamageComponentKind.DICE, "1d6", "cortante"),
                                CharacterDamageComponent(CharacterDamageComponentKind.DICE, "1d4", "perforante"),
                                CharacterDamageComponent(CharacterDamageComponentKind.FLAT, "+4"),
                            ),
                        ),
                    ),
                    customMarkers = listOf(
                        CharacterCustomMarker(
                            id = markerBinaryId,
                            name = "Inspiración alternativa",
                            valueKind = CharacterTrackableValueKind.BINARY,
                            currentValue = 1,
                        ),
                        CharacterCustomMarker(
                            id = markerCounterId,
                            name = "Puntos de estrés",
                            valueKind = CharacterTrackableValueKind.CURRENT_MAX,
                            currentValue = 3,
                            maxValue = 8,
                            recovery = CharacterTrackableRecovery(
                                cadence = CharacterRecoveryCadence.LONG_REST,
                                amountMode = CharacterRecoveryAmountMode.TO_MAX,
                            ),
                        ),
                    ),
                    resourceConfigurations = listOf(
                        CharacterResourceSuccessorConfiguration(
                            resourceId = resourceId,
                            valueKind = CharacterTrackableValueKind.CURRENT_MAX,
                            placements = setOf(
                                CharacterResourcePlacement.MANAGEMENT,
                                CharacterResourcePlacement.EQUIPMENT,
                                CharacterResourcePlacement.GENERAL,
                            ),
                        ),
                    ),
                ),
            )

            assertEquals("Cordura", savedState.customAttributes.single().name)
            assertEquals(4, savedState.customAttributes.single().modifier)
            assertTrue(savedState.customAttributes.single().savingThrowEnabled)
            assertEquals(attributeId, savedState.spellcastingProfiles.single().ability.customAttributeId)
            assertEquals(16, core.spellSaveDc(savedState.spellcastingProfiles.single(), savedState))
            assertEquals(5, core.spellAttackModifier(savedState.spellcastingProfiles.single(), savedState))
            assertEquals(listOf("1d6", "1d4", "+4"), savedState.combatDamage.single().components.map { it.expression })
            assertEquals(listOf("Inspiración alternativa", "Puntos de estrés"), savedState.customMarkers.map { it.name })
            assertEquals(3, savedState.customMarkers[1].currentValue)
            assertEquals(
                setOf(
                    CharacterResourcePlacement.GENERAL,
                    CharacterResourcePlacement.MANAGEMENT,
                    CharacterResourcePlacement.EQUIPMENT,
                ),
                savedState.resourceConfigurations.single().placements,
            )

            val reopened = successor.state(core.id)
            assertEquals(savedState, reopened)
        }

    @Test
    fun backgroundImagesRoundTripAsAppOwnedPayloads() = withRepositories { campaigns, characters, successor ->
        val campaign = campaigns.createCampaign("Imágenes")
        val character = characters.createCharacter(campaign.id, "Retrato")
        val image = CharacterBackgroundImage(
            id = Uuid.random(),
            slot = CharacterBackgroundImageSlot.PRIMARY,
            mimeType = "image/png",
            encodedData = "cG5nLXBheWxvYWQ=",
            originalName = "retrato.png",
        )

        val saved = successor.saveState(
            character.id,
            successor.state(character.id).copy(backgroundImages = listOf(image)),
        )
        val reopened = successor.state(character.id)

        assertEquals(listOf(image), saved.backgroundImages)
        assertEquals(listOf(image), reopened.backgroundImages)
    }

    @Test
    fun successorSaveRejectsDanglingReferences() = withRepositories { campaigns, characters, successor ->
        val campaign = campaigns.createCampaign("Validación")
        val character = characters.createCharacter(campaign.id, "Manual")

        assertFailsWith<IllegalArgumentException> {
            successor.saveState(
                character.id,
                CharacterSuccessorState(
                    spellcastingProfiles = listOf(CharacterSpellcastingProfile(Uuid.random())),
                ),
            )
        }
    }

    @Test
    fun migrationNineAddsSuccessorTablesWithoutLosingExistingCharacterRow() {
        val file = File.createTempFile("dnd-custom-aid-schema9", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val characterId = "00000000-0000-0000-0000-000000000091"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("PRAGMA foreign_keys=ON")
                    statement.executeUpdate("CREATE TABLE character (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    statement.executeUpdate("INSERT INTO character(id, name) VALUES ('$characterId', 'Conservado')")
                    statement.executeUpdate("CREATE TABLE character_spell_source (id TEXT NOT NULL PRIMARY KEY)")
                    statement.executeUpdate("CREATE TABLE character_combat_entry (id TEXT NOT NULL PRIMARY KEY)")
                    statement.executeUpdate("CREATE TABLE character_resource (id TEXT NOT NULL PRIMARY KEY)")
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(
                driver = driver,
                oldVersion = 9,
                newVersion = 13,
            )
            driver.close()

            DriverManager.getConnection(jdbcUrl).use { connection ->
                val preservedName = connection.createStatement().use { statement ->
                    statement.executeQuery("SELECT name FROM character WHERE id = '$characterId'").use { result ->
                        result.next()
                        result.getString(1)
                    }
                }
                assertEquals("Conservado", preservedName)

                val successorTables = setOf(
                    "character_custom_attribute",
                    "character_spell_source_casting",
                    "character_combat_damage_component",
                    "character_custom_marker",
                    "character_resource_successor_config",
                )
                val found = connection.createStatement().use { statement ->
                    statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table'").use { result ->
                        buildSet {
                            while (result.next()) add(result.getString(1))
                        }
                    }
                }
                assertTrue(found.containsAll(successorTables))
            }
        } finally {
            file.delete()
        }
    }

    private fun withRepositories(
        block: (
            CampaignRepository,
            CharacterRepository,
            CharacterSuccessorRepository,
        ) -> Unit,
    ) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            block(CampaignRepository(database), CharacterRepository(database), CharacterSuccessorRepository(database))
        } finally {
            driver.close()
        }
    }
}
