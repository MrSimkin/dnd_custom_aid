package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertEquals

class HostedPcSyncBaselineMigrationTest {
    @Test
    fun migrationFromSchema17AddsDurablePcSyncBaselineWithoutTouchingCharacters() {
        val file = File.createTempFile("dnd-custom-aid-pc-baseline", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val characterId = "00000000-0000-0000-0000-000000000099"

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
                oldVersion = 17,
                newVersion = 18,
            )
            val database = AppDatabase(driver)
            database.pcSyncBaselineQueries.upsertBaseline(
                pc_id = characterId,
                revision = 7,
                snapshot_json = "{\"fixture\":true}",
            )

            val stored = database.pcSyncBaselineQueries.selectBaseline(characterId) {
                    pcId, revision, snapshotJson -> Triple(pcId, revision, snapshotJson)
            }.executeAsOne()

            assertEquals(characterId, stored.first)
            assertEquals(7L, stored.second)
            assertEquals("{\"fixture\":true}", stored.third)
            assertEquals(1L, database.characterQueries.countCharacters().executeAsOne())
            driver.close()
        } finally {
            file.delete()
        }
    }
}
