package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterSuccessorPresentationTest {
    @Test
    fun valuablesTabOrderAndTwoBackgroundImagesRoundTrip() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val successor = CharacterSuccessorRepository(database)
            val campaign = campaigns.createCampaign("Presentación")
            val character = characters.createCharacter(campaign.id, "Liora")
            val primaryId = Uuid.random()
            val secondaryId = Uuid.random()
            val reversedTabs = CharacterSheetTabKey.entries.reversed()

            val saved = successor.saveState(
                character.id,
                successor.state(character.id).copy(
                    preferences = CharacterSuccessorPreferences(
                        valuablesText = "3 rubíes, una estatuilla de plata y 2 obras de arte",
                        tabOrder = reversedTabs,
                    ),
                    backgroundImages = listOf(
                        CharacterBackgroundImage(
                            id = primaryId,
                            slot = CharacterBackgroundImageSlot.PRIMARY,
                            mimeType = "image/jpeg",
                            encodedData = "primary-base64",
                            originalName = "liora.jpg",
                        ),
                        CharacterBackgroundImage(
                            id = secondaryId,
                            slot = CharacterBackgroundImageSlot.SECONDARY,
                            mimeType = "image/webp",
                            encodedData = "secondary-base64",
                            originalName = "token.webp",
                        ),
                    ),
                ),
            )

            assertEquals("3 rubíes, una estatuilla de plata y 2 obras de arte", saved.preferences.valuablesText)
            assertEquals(reversedTabs, saved.preferences.tabOrder)
            assertEquals(listOf(CharacterBackgroundImageSlot.PRIMARY, CharacterBackgroundImageSlot.SECONDARY), saved.backgroundImages.map { it.slot })
            assertEquals(listOf("primary-base64", "secondary-base64"), saved.backgroundImages.map { it.encodedData })

            val reopened = successor.state(character.id)
            assertEquals(saved, reopened)
        }
    }

    @Test
    fun successorPresentationRejectsIncompleteTabOrderAndDuplicateImageSlots() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val successor = CharacterSuccessorRepository(database)
            val campaign = campaigns.createCampaign("Validación")
            val character = characters.createCharacter(campaign.id, "Manual")

            assertFailsWith<IllegalArgumentException> {
                successor.saveState(
                    character.id,
                    successor.state(character.id).copy(
                        preferences = CharacterSuccessorPreferences(
                            tabOrder = listOf(CharacterSheetTabKey.OVERVIEW),
                        ),
                    ),
                )
            }

            assertFailsWith<IllegalArgumentException> {
                successor.saveState(
                    character.id,
                    successor.state(character.id).copy(
                        backgroundImages = listOf(
                            CharacterBackgroundImage(
                                id = Uuid.random(),
                                slot = CharacterBackgroundImageSlot.PRIMARY,
                                mimeType = "image/jpeg",
                                encodedData = "one",
                            ),
                            CharacterBackgroundImage(
                                id = Uuid.random(),
                                slot = CharacterBackgroundImageSlot.PRIMARY,
                                mimeType = "image/png",
                                encodedData = "two",
                            ),
                        ),
                    ),
                )
            }
        }
    }

    @Test
    fun migrationTenAddsPresentationTablesWithoutChangingExistingCharacterRows() {
        val file = File.createTempFile("dnd-custom-aid-schema10", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val characterId = "00000000-0000-0000-0000-000000000101"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("PRAGMA foreign_keys=ON")
                    statement.executeUpdate("CREATE TABLE character (id TEXT NOT NULL PRIMARY KEY, name TEXT NOT NULL)")
                    statement.executeUpdate("INSERT INTO character(id, name) VALUES ('$characterId', 'Conservado 10')")
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            // Start before migration 9 so this synthetic fixture follows the same predecessor chain
            // that creates the successor extension tables migration 11 later rebuilds.
            AppDatabase.Schema.migrate(
                driver = driver,
                oldVersion = 9,
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
                assertEquals("Conservado 10", preservedName)

                val found = connection.createStatement().use { statement ->
                    statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table'").use { result ->
                        buildSet {
                            while (result.next()) add(result.getString(1))
                        }
                    }
                }
                assertTrue("character_successor_preferences" in found)
                assertTrue("character_background_image" in found)
            }
        } finally {
            file.delete()
        }
    }

    @Test
    fun backupImportPreservesPresentationValuesButRemapsImageIdentity() {
        withDatabase { database ->
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val successor = CharacterSuccessorRepository(database)
            val backups = CharacterBackupRepository(database)
            val sourceCampaign = campaigns.createCampaign("Origen")
            val destinationCampaign = campaigns.createCampaign("Destino")
            val character = characters.createCharacter(sourceCampaign.id, "Retrato")
            val originalImageId = Uuid.random()
            val tabOrder = CharacterSheetTabKey.entries.drop(1) + CharacterSheetTabKey.OVERVIEW

            successor.saveState(
                character.id,
                successor.state(character.id).copy(
                    preferences = CharacterSuccessorPreferences(
                        valuablesText = "Corona ceremonial, sin precio estimado",
                        tabOrder = tabOrder,
                    ),
                    backgroundImages = listOf(
                        CharacterBackgroundImage(
                            id = originalImageId,
                            slot = CharacterBackgroundImageSlot.PRIMARY,
                            mimeType = "image/jpeg",
                            encodedData = "owned-image-data",
                            originalName = "portrait.jpg",
                        ),
                    ),
                ),
            )

            val exported = backups.exportCharacter(character.id, 1_000)
            val imported = backups.importAsCopy(exported, destinationCampaign.id, 1_001)
            val importedImage = imported.successorState.backgroundImages.single()

            assertEquals("Corona ceremonial, sin precio estimado", imported.successorState.preferences.valuablesText)
            assertEquals(tabOrder, imported.successorState.preferences.tabOrder)
            assertEquals(CharacterBackgroundImageSlot.PRIMARY, importedImage.slot)
            assertEquals("image/jpeg", importedImage.mimeType)
            assertEquals("owned-image-data", importedImage.encodedData)
            assertEquals("portrait.jpg", importedImage.originalName)
            assertNotEquals(originalImageId, importedImage.id)
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
