package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertFalse

class CharacterC2BackupPreferenceTest {
    @Test
    fun inspirationVisibilitySurvivesOwnFormatExportAndImport() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val successor = CharacterSuccessorRepository(database)
            val backups = CharacterBackupRepository(database)
            val sourceCampaign = campaigns.createCampaign("Origen C2")
            val destinationCampaign = campaigns.createCampaign("Destino C2")
            val character = characters.createCharacter(sourceCampaign.id, "Preferencias")
            val state = successor.state(character.id)

            successor.saveState(
                character.id,
                state.copy(
                    preferences = state.preferences.copy(inspirationVisible = false),
                ),
            )

            val exported = backups.exportCharacter(character.id, exportedAtEpochSeconds = 100)
            val imported = backups.importAsCopy(
                document = exported,
                destinationCampaignId = destinationCampaign.id,
                importedAtEpochSeconds = 200,
            )

            assertFalse(imported.successorState.preferences.inspirationVisible)
        } finally {
            driver.close()
        }
    }
}
