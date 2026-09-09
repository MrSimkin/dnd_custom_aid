package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CharacterPcConfigurationRepositoryTest {
    @Test
    fun inspirationVisibilityDefaultsVisibleAndPersistsPerCharacter() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val configuration = CharacterPcConfigurationRepository(database)
            val campaign = campaigns.createCampaign("Configuración")
            val first = characters.createCharacter(campaign.id, "Primera")
            val second = characters.createCharacter(campaign.id, "Segunda")

            assertTrue(configuration.configuration(first.id).inspirationVisible)
            assertTrue(configuration.configuration(second.id).inspirationVisible)

            configuration.saveConfiguration(
                first.id,
                CharacterPcConfiguration(inspirationVisible = false),
            )

            assertFalse(configuration.configuration(first.id).inspirationVisible)
            assertTrue(configuration.configuration(second.id).inspirationVisible)
        } finally {
            driver.close()
        }
    }

    @Test
    fun migrationFromSchema11CreatesCharacterConfigurationTable() {
        val file = File.createTempFile("dnd-custom-aid-c2-settings", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val characterId = "00000000-0000-0000-0000-0000000000c2"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("CREATE TABLE character (id TEXT NOT NULL PRIMARY KEY)")
                    statement.executeUpdate("INSERT INTO character(id) VALUES ('$characterId')")
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(
                driver = driver,
                oldVersion = 11,
                newVersion = AppDatabase.Schema.version,
            )
            val database = AppDatabase(driver)

            database.characterPcConfigurationQueries.upsertCharacterPcConfiguration(
                character_id = characterId,
                inspiration_visible = 0,
            )
            val visible = database.characterPcConfigurationQueries
                .selectCharacterPcConfiguration(characterId) { _, inspirationVisible -> inspirationVisible != 0L }
                .executeAsOne()

            assertFalse(visible)
            driver.close()
        } finally {
            file.delete()
        }
    }
}
