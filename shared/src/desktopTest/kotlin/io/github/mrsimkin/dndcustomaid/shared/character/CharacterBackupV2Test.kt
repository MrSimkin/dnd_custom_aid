package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterBackupV2Test {
    @Test
    fun versionOneBackupWithoutSuccessorPayloadStillDecodes() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val backups = CharacterBackupRepository(database)
            val campaign = campaigns.createCampaign("Compatibilidad")
            val character = characters.createCharacter(campaign.id, "V1")
            val v2 = CharacterBackupCodec.encode(backups.exportCharacter(character.id, 100))

            val json = Json { ignoreUnknownKeys = true }
            val root = json.parseToJsonElement(v2).let { it as JsonObject }
            val v1Root = JsonObject(
                root.toMutableMap().apply {
                    this["version"] = JsonPrimitive(1)
                    remove("successorState")
                },
            )
            val decoded = assertIs<CharacterBackupDecodeResult.Success>(
                CharacterBackupCodec.decode(v1Root.toString()),
            ).document

            assertEquals(1, decoded.version)
            assertEquals(CharacterSuccessorState(), decoded.successorState)
        }
    }

    @Test
    fun successorBackupImportRemapsEveryRelationalReference() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val closure = CharacterClosureRepository(database)
            val successor = CharacterSuccessorRepository(database)
            val backups = CharacterBackupRepository(database)
            val sourceCampaign = campaigns.createCampaign("Origen")
            val destinationCampaign = campaigns.createCampaign("Destino")
            val base = characters.createCharacter(sourceCampaign.id, "Sucesor")

            val sourceId = Uuid.random()
            val attackId = Uuid.random()
            val resourceId = Uuid.random()
            val core = characters.saveCharacter(
                base.copy(
                    spellcasterEnabled = true,
                    spellcastingSources = listOf(CharacterSpellcastingSource(sourceId, "Pacto", null, 0)),
                    combatEntries = listOf(
                        CharacterCombatEntry(
                            id = attackId,
                            name = "Hoja",
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
                            name = "Cargas",
                            currentValue = 2,
                            maxValue = 5,
                            recovery = "Descanso largo",
                            source = null,
                            notes = null,
                            pinned = true,
                            sortOrder = 0,
                        ),
                    ),
                ),
            )
            val customSkillId = Uuid.random()
            closure.saveState(
                core.id,
                CharacterClosureState(
                    customSkills = listOf(
                        CharacterCustomSkill(
                            id = customSkillId,
                            name = "Ocultismo",
                            ability = CharacterAbility.INTELLIGENCE,
                            training = SkillTraining.PROFICIENT,
                        ),
                    ),
                ),
            )

            val attributeId = Uuid.random()
            val markerId = Uuid.random()
            successor.saveState(
                core.id,
                successor.state(core.id).copy(
                    customAttributes = listOf(
                        CharacterCustomAttribute(
                            id = attributeId,
                            name = "Cordura",
                            abbreviation = "COR",
                            score = 16,
                        ),
                    ),
                    customSkillAbilities = listOf(
                        CharacterCustomSkillAbilityConfiguration(
                            customSkillId = customSkillId,
                            ability = CharacterAbilityReference.custom(attributeId),
                        ),
                    ),
                    spellcastingProfiles = listOf(
                        CharacterSpellcastingProfile(
                            sourceId = sourceId,
                            ability = CharacterAbilityReference.custom(attributeId),
                        ),
                    ),
                    combatDamage = listOf(
                        CharacterCombatDamageProfile(
                            attackId,
                            listOf(CharacterDamageComponent(CharacterDamageComponentKind.DICE, "1d6", "cortante")),
                        ),
                    ),
                    customMarkers = listOf(
                        CharacterCustomMarker(
                            id = markerId,
                            name = "Estrés",
                            valueKind = CharacterTrackableValueKind.CURRENT_MAX,
                            currentValue = 2,
                            maxValue = 6,
                        ),
                    ),
                    resourceConfigurations = listOf(
                        CharacterResourceSuccessorConfiguration(
                            resourceId = resourceId,
                            placements = setOf(
                                CharacterResourcePlacement.MANAGEMENT,
                                CharacterResourcePlacement.EQUIPMENT,
                            ),
                        ),
                    ),
                ),
            )

            val exported = backups.exportCharacter(core.id, 200)
            assertEquals(2, exported.version)
            val imported = backups.importAsCopy(exported, destinationCampaign.id, 300)

            val importedAttribute = imported.successorState.customAttributes.single()
            val importedSkill = imported.closureState.customSkills.single()
            val importedSource = imported.character.spellcastingSources.single()
            val importedAttack = imported.character.combatEntries.single()
            val importedResource = imported.character.resources.single()
            val importedMarker = imported.successorState.customMarkers.single()

            assertNotEquals(attributeId, importedAttribute.id)
            assertNotEquals(customSkillId, importedSkill.id)
            assertNotEquals(sourceId, importedSource.id)
            assertNotEquals(attackId, importedAttack.id)
            assertNotEquals(resourceId, importedResource.id)
            assertNotEquals(markerId, importedMarker.id)

            assertEquals(
                importedAttribute.id,
                imported.successorState.customSkillAbilities.single().ability.customAttributeId,
            )
            assertEquals(
                importedSkill.id,
                imported.successorState.customSkillAbilities.single().customSkillId,
            )
            assertEquals(
                importedAttribute.id,
                imported.successorState.spellcastingProfiles.single().ability.customAttributeId,
            )
            assertEquals(
                importedSource.id,
                imported.successorState.spellcastingProfiles.single().sourceId,
            )
            assertEquals(importedAttack.id, imported.successorState.combatDamage.single().combatEntryId)
            assertEquals(importedResource.id, imported.successorState.resourceConfigurations.single().resourceId)
            assertTrue(CharacterResourcePlacement.EQUIPMENT in imported.successorState.resourceConfigurations.single().placements)
        }
    }

    @Test
    fun backgroundImagePayloadSurvivesBackupCodecAndImportWithFreshIdentity() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val successor = CharacterSuccessorRepository(database)
            val backups = CharacterBackupRepository(database)
            val sourceCampaign = campaigns.createCampaign("Origen imagen")
            val destinationCampaign = campaigns.createCampaign("Destino imagen")
            val character = characters.createCharacter(sourceCampaign.id, "Retrato")
            val image = CharacterBackgroundImage(
                id = Uuid.random(),
                slot = CharacterBackgroundImageSlot.SECONDARY,
                mimeType = "image/png",
                encodedData = "cG9ydGFibGUtaW1hZ2U=",
                originalName = "secundaria.png",
            )
            successor.saveState(
                character.id,
                successor.state(character.id).copy(backgroundImages = listOf(image)),
            )

            val encodedBackup = CharacterBackupCodec.encode(backups.exportCharacter(character.id, 400))
            val decoded = assertIs<CharacterBackupDecodeResult.Success>(CharacterBackupCodec.decode(encodedBackup)).document
            val imported = backups.importAsCopy(decoded, destinationCampaign.id, 500)
            val importedImage = imported.successorState.backgroundImages.single()

            assertNotEquals(image.id, importedImage.id)
            assertEquals(image.slot, importedImage.slot)
            assertEquals(image.mimeType, importedImage.mimeType)
            assertEquals(image.encodedData, importedImage.encodedData)
            assertEquals(image.originalName, importedImage.originalName)
        }
    }

    private fun withDatabase(block: (AppDatabase) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            block(AppDatabase(driver))
        } finally {
            driver.close()
        }
    }
}
