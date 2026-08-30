package io.github.mrsimkin.dndcustomaid.shared.db

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.Test
import kotlin.test.assertEquals

class DatabaseSmokeTest {
    @Test
    fun schemaCanBeCreatedAndQueried() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)

        database.appMetaQueries.upsert("scaffold", "ok")

        assertEquals("ok", database.appMetaQueries.findValueByKey("scaffold").executeAsOne())
        driver.close()
    }
}
