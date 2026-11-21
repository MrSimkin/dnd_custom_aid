package io.github.mrsimkin.dndcustomaid.shared.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File
import java.sql.DriverManager

class DesktopDatabaseHandle internal constructor(
    val database: AppDatabase,
    private val driver: SqlDriver,
) : AutoCloseable {
    override fun close() {
        driver.close()
    }
}

class DesktopDatabaseFactory(
    private val databaseFile: File = defaultDatabaseFile(),
) {
    fun create(): DesktopDatabaseHandle {
        databaseFile.parentFile?.mkdirs()
        val jdbcUrl = "jdbc:sqlite:${databaseFile.absolutePath}"

        if (databaseFile.exists()) {
            normalizeLegacyUnversionedDatabase(jdbcUrl)
        }

        val driver = JdbcSqliteDriver(
            url = jdbcUrl,
            schema = AppDatabase.Schema,
        )

        return DesktopDatabaseHandle(
            database = AppDatabase(driver),
            driver = driver,
        )
    }

    private fun normalizeLegacyUnversionedDatabase(jdbcUrl: String) {
        DriverManager.getConnection(jdbcUrl).use { connection ->
            val version = connection.createStatement().use { statement ->
                statement.executeQuery("PRAGMA user_version").use { result ->
                    if (result.next()) result.getLong(1) else 0L
                }
            }
            if (version != 0L) return

            val userTables = connection.createStatement().use { statement ->
                statement.executeQuery(
                    "SELECT name FROM sqlite_master WHERE type = 'table' AND name NOT LIKE 'sqlite_%'",
                ).use { result ->
                    buildSet {
                        while (result.next()) add(result.getString(1))
                    }
                }
            }
            if (userTables.isEmpty()) return

            require(LEGACY_WAVE5_SENTINEL_TABLES.all(userTables::contains)) {
                "Unversioned Desktop database does not match the verified pre-Wave-6 schema; refusing automatic migration."
            }

            connection.createStatement().use { statement ->
                statement.execute("PRAGMA user_version = $LEGACY_WAVE5_SCHEMA_VERSION")
            }
        }
    }

    companion object {
        private const val APP_DIRECTORY = ".dnd_custom_aid"
        private const val DATABASE_NAME = "dnd_custom_aid.db"
        private const val LEGACY_WAVE5_SCHEMA_VERSION = 18L
        private val LEGACY_WAVE5_SENTINEL_TABLES = setOf(
            "app_account",
            "object_sync_state",
            "hosted_outbox",
            "pc_sync_baseline",
        )

        /** Java property that points a test run at its own data folder (AGENTS §6.4, refactor F5). */
        const val DATA_DIR_PROPERTY = "dndcustomaid.dataDir"

        /** Environment variable with the same purpose, for the packaged App (no JVM options there). */
        const val DATA_DIR_ENV = "DND_CUSTOM_AID_DATA_DIR"

        /**
         * The folder with the database, the images, the remembered account and the preferences.
         * Normally `~/.dnd_custom_aid`; a test run sets [DATA_DIR_PROPERTY] or [DATA_DIR_ENV] so it
         * never touches the owner's real data.
         */
        fun defaultDataDirectory(
            property: String? = System.getProperty(DATA_DIR_PROPERTY),
            environment: String? = System.getenv(DATA_DIR_ENV),
            home: String = System.getProperty("user.home"),
        ): File = (property?.takeIf { it.isNotBlank() } ?: environment?.takeIf { it.isNotBlank() })
            ?.let { File(it).absoluteFile }
            ?: File(home, APP_DIRECTORY)

        /** True when this run uses a test folder instead of the normal one. */
        fun usesTestDataDirectory(): Boolean =
            !System.getProperty(DATA_DIR_PROPERTY).isNullOrBlank() || !System.getenv(DATA_DIR_ENV).isNullOrBlank()

        fun defaultDatabaseFile(): File = File(defaultDataDirectory(), DATABASE_NAME)
    }
}
