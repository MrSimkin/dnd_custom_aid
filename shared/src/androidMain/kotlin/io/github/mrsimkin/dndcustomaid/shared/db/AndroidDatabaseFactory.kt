package io.github.mrsimkin.dndcustomaid.shared.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDatabaseFactory(
    private val context: Context,
) {
    fun create(name: String = DATABASE_NAME): AppDatabase {
        require(name.isNotBlank()) { "Android database name must not be blank." }
        require('/' !in name && '\\' !in name) { "Android database name must not contain a path." }
        return AppDatabase(
            driver = AndroidSqliteDriver(
                schema = AppDatabase.Schema,
                context = context,
                name = name,
            ),
        )
    }

    private companion object {
        const val DATABASE_NAME = "dnd_custom_aid.db"
    }
}
