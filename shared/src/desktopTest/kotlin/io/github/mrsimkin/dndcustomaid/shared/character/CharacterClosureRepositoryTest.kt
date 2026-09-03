package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterClosureRepositoryTest {
    @Test
    fun newCharacterUsesSafeClosureDefaultsWithoutEagerSettingsRow() = withRepositories { campaigns, characters, closure ->
        val campaign = campaigns.createCampaign("Cierre")
        val character = characters.createCharacter(campaign.id, "Vanya")

        assertEquals(CharacterClosureState(), closure.state(character.id))
    }

    @Test
    fun closureDomainsRoundTripAndExtensionMetadataSurvivesCoreChildRewrites() =
        withRepositories { campaigns, characters, closure ->
            val campaign = campaigns.createCampaign("Cierre")
            val created = characters.createCharacter(campaign.id, "Inventora")
            val itemId = Uuid.random()
            val resourceId = Uuid.random()
            val spellId = Uuid.random()
            val formId = Uuid.random()

            val core = characters.saveCharacter(
                created.copy(
                    currentHp = 9,
                    inventoryItems = listOf(
                        CharacterInventoryItem(
                            id = itemId,
                            name = "Flechas",
                            quantity = 20,
                            weightLb = 0.05,
                            equipped = true,
                            notes = null,
                            sortOrder = 0,
                            special = false,
                            description = null,
                            location = "Carcaj",
                            attuned = false,
                        ),
                    ),
                    resources = listOf(
                        CharacterResource(
                            id = resourceId,
                            name = "Dados de superioridad",
                            currentValue = 2,
                            maxValue = 4,
                            recovery = "Descanso corto",
                            source = "Battle Master",
                            pinned = true,
                        ),
                    ),
                    spellcasterEnabled = true,
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
                            description = "Referencia",
                            notes = null,
                            sortOrder = 0,
                        ),
                    ),
                    forms = listOf(
                        CharacterForm(
                            id = formId,
                            name = "Lobo",
                            source = "Druida",
                            challengeRatingText = "1/4",
                            armorClass = 13,
                            hitPoints = 11,
                            movement = "40 ft",
                            senses = "Percepción",
                            actionSummary = "Mordisco",
                        ),
                    ),
                ),
            )

            val conditionId = Uuid.random()
            val defenseId = Uuid.random()
            val movementId = Uuid.random()
            val senseId = Uuid.random()
            val checkpointId = Uuid.random()
            val customSkillId = Uuid.random()
            val effectId = Uuid.random()

            val saved = closure.saveState(
                core.id,
                CharacterClosureState(
                    exhaustionLevel = 2,
                    concentration = CharacterConcentration(spellId, "Detectar magia", "Mantener"),
                    portraitRef = "content://portrait/test",
                    tokenRef = "content://token/test",
                    progressMode = CharacterProgressMode.EXPERIENCE,
                    experiencePoints = 12345,
                    milestoneProgress = "",
                    tableModeEnabled = true,
                    hapticsEnabled = false,
                    conditions = listOf(
                        CharacterCondition(conditionId, "Envenenado", "Campaña", "Temporal"),
                    ),
                    defenses = listOf(
                        CharacterDefense(defenseId, CharacterDefenseType.RESISTANCE, "Fuego", "Raza"),
                    ),
                    movements = listOf(
                        CharacterMovement(movementId, CharacterMovementType.FLY, "Vuelo", 30, "Alas"),
                    ),
                    senses = listOf(
                        CharacterSense(senseId, "Visión en la oscuridad", 60, null),
                    ),
                    resourceRecovery = listOf(
                        CharacterResourceRecovery(
                            resourceId,
                            CharacterRecoveryCadence.SHORT_REST,
                            CharacterRecoveryAmountMode.TO_MAX,
                        ),
                    ),
                    inventoryUsage = listOf(
                        CharacterInventoryUsage(itemId, CharacterConsumableKind.AMMUNITION, 1),
                    ),
                    reconciliationCheckpoints = listOf(
                        CharacterReconciliationCheckpoint(checkpointId, 1000, core.updatedAtEpochSeconds, "Fin de sesión", "Papel reconciliado"),
                    ),
                    customSkills = listOf(
                        CharacterCustomSkill(
                            customSkillId,
                            "Ingeniería arcana",
                            CharacterAbility.INTELLIGENCE,
                            SkillTraining.EXPERTISE,
                            adjustment = 1,
                            source = "Homebrew",
                        ),
                    ),
                    temporaryEffects = listOf(
                        CharacterTemporaryEffect(effectId, "Bendecido", "+1d4", "1 minuto", "Clérigo"),
                    ),
                    moduleOverrides = listOf(
                        CharacterModuleOverride(CharacterModuleKind.FORMS, CharacterModuleOverrideMode.FORCE_HIDE),
                    ),
                    quickAccess = listOf(
                        CharacterQuickAccessRef(CharacterQuickAccessKind.RESOURCE, resourceId, 0),
                        CharacterQuickAccessRef(CharacterQuickAccessKind.FORM, formId, 1),
                    ),
                ),
            )

            assertEquals(2, saved.exhaustionLevel)
            assertEquals(spellId, saved.concentration?.spellId)
            assertEquals("Detectar magia", saved.concentration?.name)
            assertEquals("content://portrait/test", saved.portraitRef)
            assertEquals(CharacterProgressMode.EXPERIENCE, saved.progressMode)
            assertEquals(12345, saved.experiencePoints)
            assertTrue(saved.tableModeEnabled)
            assertFalse(saved.hapticsEnabled)
            assertEquals("Envenenado", saved.conditions.single().name)
            assertEquals(CharacterDefenseType.RESISTANCE, saved.defenses.single().type)
            assertEquals(30, saved.movements.single().speedFeet)
            assertEquals(60, saved.senses.single().rangeFeet)
            assertEquals(resourceId, saved.resourceRecovery.single().resourceId)
            assertEquals(CharacterConsumableKind.AMMUNITION, saved.inventoryUsage.single().kind)
            assertEquals("Fin de sesión", saved.reconciliationCheckpoints.single().label)
            assertEquals(CharacterAbility.INTELLIGENCE, saved.customSkills.single().ability)
            assertEquals(SkillTraining.EXPERTISE, saved.customSkills.single().training)
            assertEquals("Bendecido", saved.temporaryEffects.single().name)
            assertEquals(CharacterModuleOverrideMode.FORCE_HIDE, saved.moduleOverrides.single().mode)
            assertEquals(listOf(resourceId, formId), saved.quickAccess.map { it.targetId })

            // Core save rewrites inventory/resources/spells using delete + reinsert with stable IDs.
            // Schema-7 metadata must survive that ordinary save path.
            val rewrittenCore = characters.saveCharacter(characters.character(core.id)!!.copy(currentHp = 7))
            val afterRewrite = closure.state(core.id)
            assertEquals(7, rewrittenCore.currentHp)
            assertEquals(resourceId, afterRewrite.resourceRecovery.single().resourceId)
            assertEquals(itemId, afterRewrite.inventoryUsage.single().itemId)
            assertEquals(spellId, afterRewrite.concentration?.spellId)
            assertEquals(CharacterModuleOverrideMode.FORCE_HIDE, afterRewrite.moduleOverrides.single().mode)
            assertEquals("Lobo", characters.character(core.id)!!.forms.single().name)

            // When the referenced durable rows genuinely disappear, soft metadata is ignored rather
            // than corrupting the character. Concentration keeps its human-readable identity.
            characters.saveCharacter(
                characters.character(core.id)!!.copy(
                    resources = emptyList(),
                    inventoryItems = emptyList(),
                    spells = emptyList(),
                ),
            )
            val afterDeletion = closure.state(core.id)
            assertTrue(afterDeletion.resourceRecovery.isEmpty())
            assertTrue(afterDeletion.inventoryUsage.isEmpty())
            assertNull(afterDeletion.concentration?.spellId)
            assertEquals("Detectar magia", afterDeletion.concentration?.name)
            assertEquals("Lobo", characters.character(core.id)!!.forms.single().name)
        }

    @Test
    fun saveNormalizesDanglingReferencesAndKeepsCustomQuickAccessPermissive() =
        withRepositories { campaigns, characters, closure ->
            val campaign = campaigns.createCampaign("Cierre")
            val character = characters.createCharacter(campaign.id, "Manual")
            val missing = Uuid.random()
            val quickTarget = Uuid.random()

            val saved = closure.saveState(
                character.id,
                CharacterClosureState(
                    concentration = CharacterConcentration(missing, "Efecto externo"),
                    resourceRecovery = listOf(
                        CharacterResourceRecovery(missing, CharacterRecoveryCadence.LONG_REST, CharacterRecoveryAmountMode.TO_MAX),
                    ),
                    inventoryUsage = listOf(
                        CharacterInventoryUsage(missing, CharacterConsumableKind.CONSUMABLE, 1),
                    ),
                    quickAccess = listOf(CharacterQuickAccessRef(CharacterQuickAccessKind.OTHER, quickTarget)),
                ),
            )

            assertNull(saved.concentration?.spellId)
            assertEquals("Efecto externo", saved.concentration?.name)
            assertTrue(saved.resourceRecovery.isEmpty())
            assertTrue(saved.inventoryUsage.isEmpty())
            assertEquals(quickTarget, saved.quickAccess.single().targetId)
        }

    @Test
    fun migrationSevenIsAdditiveAndPreservesExistingCharacterRows() {
        val file = File.createTempFile("dnd-custom-aid-schema7", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val characterId = "00000000-0000-0000-0000-000000000077"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("PRAGMA foreign_keys=ON")
                    statement.executeUpdate("CREATE TABLE character (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    statement.executeUpdate("INSERT INTO character(id, name) VALUES ('$characterId', 'Conservado')")
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(
                driver = driver,
                oldVersion = 7,
                newVersion = AppDatabase.Schema.version,
            )
            driver.close()

            DriverManager.getConnection(jdbcUrl).use { connection ->
                val preservedName = connection.createStatement().use { statement ->
                    statement.executeQuery("SELECT name FROM character WHERE id = '$characterId'").use { result ->
                        result.next()
                        result.getString(1)
                    }
                }
                val closureTableCount = connection.createStatement().use { statement ->
                    statement.executeQuery(
                        "SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name = 'character_closure_settings'",
                    ).use { result ->
                        result.next()
                        result.getInt(1)
                    }
                }
                val conditionTableCount = connection.createStatement().use { statement ->
                    statement.executeQuery(
                        "SELECT COUNT(*) FROM sqlite_master WHERE type = 'table' AND name = 'character_condition'",
                    ).use { result ->
                        result.next()
                        result.getInt(1)
                    }
                }

                assertEquals("Conservado", preservedName)
                assertEquals(1, closureTableCount)
                assertEquals(1, conditionTableCount)
            }
        } finally {
            file.delete()
        }
    }

    private fun withRepositories(
        block: (
            CampaignRepository,
            CharacterRepository,
            CharacterClosureRepository,
        ) -> Unit,
    ) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            block(CampaignRepository(database), CharacterRepository(database), CharacterClosureRepository(database))
        } finally {
            driver.close()
        }
    }
}
