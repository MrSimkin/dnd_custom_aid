package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CharacterSuccessorPreferencesC2Test {
    @Test
    fun inspirationVisibilityDefaultsVisibleAndPersistsPerCharacter() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val successor = CharacterSuccessorRepository(database)
            val campaign = campaigns.createCampaign("Configuración")
            val first = characters.createCharacter(campaign.id, "Primera")
            val second = characters.createCharacter(campaign.id, "Segunda")

            val firstState = successor.state(first.id)
            assertTrue(firstState.preferences.inspirationVisible)
            assertTrue(successor.state(second.id).preferences.inspirationVisible)

            successor.saveState(
                first.id,
                firstState.copy(
                    preferences = firstState.preferences.copy(inspirationVisible = false),
                ),
            )

            assertFalse(successor.state(first.id).preferences.inspirationVisible)
            assertTrue(successor.state(second.id).preferences.inspirationVisible)
        } finally {
            driver.close()
        }
    }

    @Test
    fun migrationFromSchema11AddsVisibleByDefault() {
        val file = File.createTempFile("dnd-custom-aid-c2-successor-prefs", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val characterId = "00000000-0000-0000-0000-0000000000c2"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("CREATE TABLE character (id TEXT NOT NULL PRIMARY KEY)")
                    statement.executeUpdate(
                        """CREATE TABLE character_successor_preferences (
                            character_id TEXT NOT NULL PRIMARY KEY REFERENCES character(id) ON DELETE CASCADE,
                            valuables_text TEXT NOT NULL DEFAULT '',
                            tab_order TEXT NOT NULL DEFAULT ''
                        )""".trimIndent(),
                    )
                    statement.executeUpdate("INSERT INTO character(id) VALUES ('$characterId')")
                    statement.executeUpdate(
                        "INSERT INTO character_successor_preferences(character_id, valuables_text, tab_order) VALUES ('$characterId', '', '')",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(
                driver = driver,
                oldVersion = 11,
                newVersion = AppDatabase.Schema.version,
            )
            val visible = AppDatabase(driver).characterSuccessorQueries
                .selectSuccessorPreferences(characterId) { _, _, _, inspirationVisible -> inspirationVisible != 0L }
                .executeAsOne()

            assertTrue(visible)
            driver.close()
        } finally {
            file.delete()
        }
    }
}
