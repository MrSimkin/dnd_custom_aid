package io.github.mrsimkin.dndcustomaid.shared.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import java.io.File
import java.sql.DriverManager
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class DesktopDatabaseFactoryTest {
    @Test
    fun createsPersistentDatabaseAndReopensExistingSchema() {
        val directory = createTempDirectory("dnd-custom-aid-desktop-").toFile()
        val databaseFile = File(directory, "desktop.db")

        try {
            DesktopDatabaseFactory(databaseFile).create().use { firstHandle ->
                val repository = CampaignRepository(firstHandle.database)
                val campaign = repository.createCampaign("Desktop Campaign")
                repository.setActiveCampaign(campaign.id)

                assertTrue(databaseFile.exists())
                assertEquals(campaign, repository.activeCampaign())
            }

            DesktopDatabaseFactory(databaseFile).create().use { secondHandle ->
                val repository = CampaignRepository(secondHandle.database)

                assertEquals(listOf("Desktop Campaign"), repository.listCampaigns().map { it.name })
                assertEquals("Desktop Campaign", repository.activeCampaign()?.name)
            }
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun migratesLegacyUnversionedWave5DatabaseWithoutLosingExistingData() {
        val directory = createTempDirectory("dnd-custom-aid-desktop-legacy-").toFile()
        val databaseFile = File(directory, "desktop.db")
        val jdbcUrl = "jdbc:sqlite:${databaseFile.absolutePath}"
        val campaignName = "Legacy Desktop Campaign"

        try {
            val legacyDriver = JdbcSqliteDriver(jdbcUrl)
            AppDatabase.Schema.create(legacyDriver)
            val legacyDatabase = AppDatabase(legacyDriver)
            CampaignRepository(legacyDatabase).createCampaign(campaignName)
            legacyDriver.close()

            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.execute("DROP TABLE zone_payload")
                    statement.execute("DROP TABLE place_payload")
                    statement.execute("DROP TABLE homebrew_rule_payload")
                    statement.execute("DROP TABLE npc_payload")
                    statement.execute("DROP TABLE creature_payload")
                    statement.execute("DROP TABLE reusable_content")
                    statement.execute("PRAGMA user_version = 0")
                }
            }

            DesktopDatabaseFactory(databaseFile).create().use { migratedHandle ->
                assertEquals(
                    listOf(campaignName),
                    CampaignRepository(migratedHandle.database).listCampaigns().map { it.name },
                )

                val spine = IntegratedSpineRepository(migratedHandle.database)
                val owner = AccountIdentity(Uuid.random())
                spine.upsertAccount(owner)
                val content = ReusableContentRepository(migratedHandle.database, spine).createPersonal(
                    ownerAccountId = owner.id,
                    family = ReusableContentFamily.NPC,
                    rawDisplayName = "Migrated Archivist",
                    nowEpochSeconds = 100,
                )
                assertEquals("Migrated Archivist", content.displayName)
            }
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test
    fun refusesUnknownUnversionedDatabaseInsteadOfGuessingDestructively() {
        val directory = createTempDirectory("dnd-custom-aid-desktop-unknown-").toFile()
        val databaseFile = File(directory, "desktop.db")
        val jdbcUrl = "jdbc:sqlite:${databaseFile.absolutePath}"

        try {
            DriverManager.getConnection(jdbcUrl).use { connection ->
                connection.createStatement().use { statement ->
                    statement.execute("CREATE TABLE unknown_legacy_table (id TEXT NOT NULL PRIMARY KEY)")
                    statement.execute("PRAGMA user_version = 0")
                }
            }

            assertFailsWith<IllegalArgumentException> {
                DesktopDatabaseFactory(databaseFile).create()
            }
        } finally {
            directory.deleteRecursively()
        }
    }
}
