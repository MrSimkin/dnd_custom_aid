package io.github.mrsimkin.dndcustomaid.shared.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

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
        val databaseAlreadyExists = databaseFile.exists()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")

        if (!databaseAlreadyExists) {
            AppDatabase.Schema.create(driver)
        }

        return DesktopDatabaseHandle(
            database = AppDatabase(driver),
            driver = driver,
        )
    }

    companion object {
        private const val APP_DIRECTORY = ".dnd_custom_aid"
        private const val DATABASE_NAME = "dnd_custom_aid.db"

        fun defaultDatabaseFile(): File = File(
            File(System.getProperty("user.home"), APP_DIRECTORY),
            DATABASE_NAME,
        )
    }
}
