package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals

class CharacterBackgroundIdentityMigrationTest {
    @Test
    fun migrationFromPreCorrectionSchemaPreservesBackgroundAndAddsEmptyIdentityFields() {
        val file = File.createTempFile("dnd-custom-aid-background-identity", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val characterId = "00000000-0000-0000-0000-000000000099"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    // This fixture starts at schema version 5, just before migration 5 adds
                    // background identity fields. Keep the additional empty tables that later
                    // migrations legitimately alter so the fixture remains a valid schema-5 shape.
                    statement.executeUpdate("CREATE TABLE character (id TEXT NOT NULL PRIMARY KEY)")
                    statement.executeUpdate("CREATE TABLE character_class (id TEXT NOT NULL PRIMARY KEY)")
                    statement.executeUpdate("CREATE TABLE character_combat_entry (id TEXT NOT NULL PRIMARY KEY)")
                    statement.executeUpdate("CREATE TABLE character_trait (id TEXT NOT NULL PRIMARY KEY)")
                    statement.executeUpdate("CREATE TABLE character_spell (id TEXT NOT NULL PRIMARY KEY)")
                    statement.executeUpdate(
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
                    statement.executeUpdate("INSERT INTO character(id) VALUES ('$characterId')")
                    statement.executeUpdate(
                        """INSERT INTO character_background(
                            character_id, background_name, summary, personality_traits, ideals, bonds, flaws, story
                        ) VALUES (
                            '$characterId', 'Erudito', 'Resumen previo', 'Curioso', 'Conocimiento', 'Biblioteca', 'Obsesivo', 'Historia previa'
                        )""".trimIndent(),
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(
                driver = driver,
                oldVersion = 5,
                newVersion = AppDatabase.Schema.version,
            )
            val values = AppDatabase(driver).characterQueries.selectCharacterBackground(characterId) {
                    _, name, summary, race, religionFaith, personalityTraits, ideals, bonds, flaws, story ->
                listOf(name, summary, race, religionFaith, personalityTraits, ideals, bonds, flaws, story)
            }.executeAsOne()

            assertEquals(
                listOf(
                    "Erudito",
                    "Resumen previo",
                    "",
                    "",
                    "Curioso",
                    "Conocimiento",
                    "Biblioteca",
                    "Obsesivo",
                    "Historia previa",
                ),
                values,
            )
            driver.close()
        } finally {
            file.delete()
        }
    }

    @Test
    fun repositoryRoundTripPersistsRaceAndReligionFaith() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            val campaign = CampaignRepository(database).createCampaign("Identidad")
            val repository = CharacterRepository(database)
            val created = repository.createCharacter(campaign.id, "Vanya")

            val saved = repository.saveCharacter(
                created.copy(
                    background = created.background.copy(
                        race = "Tiefling",
                        religionFaith = "Sin culto formal",
                    ),
                ),
            )
            val reopened = repository.character(saved.id)!!

            assertEquals("Tiefling", reopened.background.race)
            assertEquals("Sin culto formal", reopened.background.religionFaith)
        } finally {
            driver.close()
        }
    }
}
