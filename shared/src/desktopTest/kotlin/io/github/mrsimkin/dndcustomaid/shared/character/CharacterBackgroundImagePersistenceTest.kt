package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.uuid.Uuid

class CharacterBackgroundImagePersistenceTest {
    @Test
    fun appOwnedBackgroundImagesSurviveDatabaseReopen() {
        val file = File.createTempFile("dnd-custom-aid-background-images", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val primary = image(CharacterBackgroundImageSlot.PRIMARY, "primary-payload", "principal.png")
        val secondary = image(CharacterBackgroundImageSlot.SECONDARY, "secondary-payload", "secundaria.jpg")
        var characterId: Uuid? = null

        try {
            JdbcSqliteDriver(jdbcUrl).also { driver ->
                AppDatabase.Schema.create(driver)
                val database = AppDatabase(driver)
                val campaign = CampaignRepository(database).createCampaign("Imágenes persistentes")
                val character = CharacterRepository(database).createCharacter(campaign.id, "Retrato")
                characterId = character.id
                val successor = CharacterSuccessorRepository(database)
                successor.saveState(
                    character.id,
                    successor.state(character.id).copy(backgroundImages = listOf(primary, secondary)),
                )
                driver.close()
            }

            JdbcSqliteDriver(jdbcUrl).also { driver ->
                val reopened = CharacterSuccessorRepository(AppDatabase(driver)).state(requireNotNull(characterId))
                assertEquals(
                    mapOf(
                        CharacterBackgroundImageSlot.PRIMARY to primary,
                        CharacterBackgroundImageSlot.SECONDARY to secondary,
                    ),
                    reopened.backgroundImages.associateBy { it.slot },
                )
                driver.close()
            }
        } finally {
            file.delete()
        }
    }

    @Test
    fun backupCodecAndRestoreAsCopyCarryBothAppOwnedImages() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val successor = CharacterSuccessorRepository(database)
            val backups = CharacterBackupRepository(database)
            val sourceCampaign = campaigns.createCampaign("Origen imágenes")
            val destinationCampaign = campaigns.createCampaign("Destino imágenes")
            val source = characters.createCharacter(sourceCampaign.id, "Retrato")
            val primary = image(CharacterBackgroundImageSlot.PRIMARY, "primary-payload", "principal.png")
            val secondary = image(CharacterBackgroundImageSlot.SECONDARY, "secondary-payload", "secundaria.jpg")
            successor.saveState(
                source.id,
                successor.state(source.id).copy(backgroundImages = listOf(primary, secondary)),
            )

            val exported = backups.exportCharacter(source.id, 1_778_200_000)
            val encoded = CharacterBackupCodec.encode(exported)
            val decoded = assertIs<CharacterBackupDecodeResult.Success>(CharacterBackupCodec.decode(encoded)).document
            val imported = backups.importAsCopy(decoded, destinationCampaign.id, 1_778_200_100)

            val exportedBySlot = exported.successorState.backgroundImages.associateBy { it.slot }
            val importedBySlot = imported.successorState.backgroundImages.associateBy { it.slot }
            assertEquals(setOf(CharacterBackgroundImageSlot.PRIMARY, CharacterBackgroundImageSlot.SECONDARY), exportedBySlot.keys)
            assertEquals(exportedBySlot.keys, importedBySlot.keys)
            exportedBySlot.forEach { (slot, exportedImage) ->
                val importedImage = requireNotNull(importedBySlot[slot])
                assertNotEquals(exportedImage.id, importedImage.id)
                assertEquals(exportedImage.mimeType, importedImage.mimeType)
                assertEquals(exportedImage.encodedData, importedImage.encodedData)
                assertEquals(exportedImage.originalName, importedImage.originalName)
            }

            val reopenedImport = CharacterSuccessorRepository(database).state(imported.character.id)
            assertEquals(imported.successorState.backgroundImages, reopenedImport.backgroundImages)
        } finally {
            driver.close()
        }
    }

    private fun image(
        slot: CharacterBackgroundImageSlot,
        payload: String,
        originalName: String,
    ) = CharacterBackgroundImage(
        id = Uuid.random(),
        slot = slot,
        mimeType = if (originalName.endsWith(".png")) "image/png" else "image/jpeg",
        encodedData = payload,
        originalName = originalName,
    )
}
