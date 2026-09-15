package io.github.mrsimkin.dndcustomaid.shared.hosted

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_BACKUP_FORMAT
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_BACKUP_VERSION
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class HostedWireJsonTest {
    @Test
    fun characterSnapshotWireJsonIncludesRequiredDefaultEnvelopeFields() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try {
            val database = AppDatabase(driver)
            val campaign = CampaignRepository(database).createCampaign("Wire Contract")
            val character = CharacterRepository(database).createCharacter(campaign.id, "Wire PC")
            val snapshot = CharacterBackupRepository(database).exportCharacter(
                characterId = character.id,
                exportedAtEpochSeconds = 100,
            )

            val root = hostedWireJson()
                .parseToJsonElement(hostedWireJson().encodeToString(snapshot))
                .jsonObject

            assertEquals(CHARACTER_BACKUP_FORMAT, root.getValue("format").jsonPrimitive.content)
            assertEquals(CHARACTER_BACKUP_VERSION.toString(), root.getValue("version").jsonPrimitive.content)
        } finally {
            driver.close()
        }
    }
}
