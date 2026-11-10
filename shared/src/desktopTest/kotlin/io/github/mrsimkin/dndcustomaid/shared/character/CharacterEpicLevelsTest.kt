package io.github.mrsimkin.dndcustomaid.shared.character

import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json

/** C448 (owner 2026-11-06): «habilitar niveles épicos» is saved per class and defaults to off. */
class CharacterEpicLevelsTest {
    @Test
    fun theOptionIsSavedPerClassAndDefaultsToOff() {
        val dir = Files.createTempDirectory("dnd-epic-")
        val handle = DesktopDatabaseFactory(dir.resolve("epic.db").toFile()).create()
        try {
            val database = handle.database
            val campaign = CampaignRepository(database).createCampaign("Campaña épica")
            val characters = CharacterRepository(database)
            val pc = characters.createCharacter(campaign.id, "Epica")
            val fighter = CharacterClassLevel(id = kotlin.uuid.Uuid.random(), name = "Guerrero", level = 5,
                hitDieSides = 10, hitDiceRemaining = 5, sortOrder = 0)
            val mage = fighter.copy(id = kotlin.uuid.Uuid.random(), name = "Mago", sortOrder = 1, epicLevelsEnabled = true)
            assertFalse(fighter.epicLevelsEnabled, "Off by default.")
            val current = requireNotNull(characters.character(pc.id))
            characters.saveCharacter(current.copy(classes = listOf(fighter, mage)))
            val saved = requireNotNull(characters.character(pc.id)).classes
            assertEquals(listOf(false, true), saved.map { it.epicLevelsEnabled })
        } finally {
            handle.close()
            dir.toFile().deleteRecursively()
        }
    }

    @Test
    fun theMigrationKeepsExistingClassesWithTheOptionOff() {
        val driver = app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver(
            app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver.IN_MEMORY)
        try {
            io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase.Schema.create(driver)
            val database = io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase(driver)
            val characters = CharacterRepository(database)
            val pc = characters.createCharacter(CampaignRepository(database).createCampaign("Antes").id, "Antigua")
            val fighter = CharacterClassLevel(id = kotlin.uuid.Uuid.random(), name = "Guerrero", level = 5,
                hitDieSides = 10, hitDiceRemaining = 5, sortOrder = 0)
            characters.saveCharacter(requireNotNull(characters.character(pc.id)).copy(classes = listOf(fighter)))
            // Rebuild the version-27 shape: character_class without the column.
            driver.execute(null, "ALTER TABLE character_class DROP COLUMN epic_levels_enabled", 0)

            io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase.Schema.migrate(
                driver, oldVersion = 27, newVersion = io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase.Schema.version)

            val reloaded = requireNotNull(characters.character(pc.id)).classes.single()
            assertEquals(fighter.id, reloaded.id)
            assertFalse(reloaded.epicLevelsEnabled)
        } finally {
            driver.close()
        }
    }

    @Test
    fun anOldBackupWithoutTheFieldReadsAsOff() {
        val level = Json { ignoreUnknownKeys = true }.decodeFromString<CharacterClassLevel>(
            """{"id":"a7b00000-0000-4000-8000-000000000001","name":"Guerrero","level":5,"hitDieSides":10,"hitDiceRemaining":5,"sortOrder":0}""")
        assertFalse(level.epicLevelsEnabled)
        val on = level.copy(epicLevelsEnabled = true)
        assertTrue(Json.decodeFromString<CharacterClassLevel>(Json.encodeToString(CharacterClassLevel.serializer(), on)).epicLevelsEnabled)
    }
}
