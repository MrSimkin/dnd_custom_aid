package io.github.mrsimkin.dndcustomaid.shared.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class AndroidDatabaseFactory(
    private val context: Context,
) {
    fun create(): AppDatabase = AppDatabase(
        driver = AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = context,
            name = DATABASE_NAME,
        ),
    )

    private companion object {
        const val DATABASE_NAME = "dnd_custom_aid.db"
    }
}
