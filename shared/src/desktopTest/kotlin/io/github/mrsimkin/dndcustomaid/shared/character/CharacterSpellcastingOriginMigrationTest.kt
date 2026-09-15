package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CharacterSpellcastingOriginMigrationTest {
    @Test
    fun migrationThirteenClassifiesLegacySpellSourcesWithoutLosingNames() {
        val file = File.createTempFile("dnd-custom-aid-spell-origin", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate(
                        """CREATE TABLE character_spell_source (
                            id TEXT NOT NULL PRIMARY KEY,
                            character_id TEXT NOT NULL,
                            name TEXT NOT NULL,
                            linked_class_id TEXT,
                            sort_order INTEGER NOT NULL,
                            UNIQUE(character_id, sort_order)
                        )""".trimIndent(),
                    )
                    statement.executeUpdate(
                        "INSERT INTO character_spell_source VALUES ('class-source', 'pc', 'Paladín', 'class-paladin', 0)",
                    )
                    statement.executeUpdate(
                        "INSERT INTO character_spell_source VALUES ('custom-source', 'pc', 'Objeto heredado', NULL, 1)",
                    )
                }
            }

            val driver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.migrate(driver = driver, oldVersion = 13, newVersion = AppDatabase.Schema.version)
            DriverManager.getConnection(jdbcUrl).use { jdbc ->
                jdbc.createStatement().use { statement ->
                    statement.executeQuery(
                        "SELECT name, origin_kind, origin_reference_id FROM character_spell_source ORDER BY sort_order",
                    ).use { rows ->
                        rows.next()
                        assertEquals("Paladín", rows.getString("name"))
                        assertEquals("CLASS", rows.getString("origin_kind"))
                        assertNull(rows.getString("origin_reference_id"))
                        rows.next()
                        assertEquals("Objeto heredado", rows.getString("name"))
                        assertEquals("OTHER", rows.getString("origin_kind"))
                        assertNull(rows.getString("origin_reference_id"))
                    }
                }
            }
            driver.close()
        } finally {
            file.delete()
        }
    }
}
