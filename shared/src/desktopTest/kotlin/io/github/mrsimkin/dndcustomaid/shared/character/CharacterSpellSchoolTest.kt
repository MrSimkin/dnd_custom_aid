package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

/** Q14b (owner, 2026-10-29): the spell school is a real, persisted App field. */
class CharacterSpellSchoolTest {
    @Test
    fun spellSchoolPersistsTrimsAndClearsBlankValues() = withRepositories { campaigns, characters ->
        val campaign = campaigns.createCampaign("Escuelas")
        val created = characters.createCharacter(campaign.id, "Evocadora")
        val boltId = Uuid.random()
        val shieldId = Uuid.random()
        val saved = characters.saveCharacter(
            created.copy(spells = listOf(spell(boltId, "Fire Bolt", "  Evocación  ", 0), spell(shieldId, "Escudo", "   ", 1))),
        )
        assertEquals("Evocación", saved.spells.single { it.id == boltId }.school)
        assertNull(saved.spells.single { it.id == shieldId }.school, "A blank school is no school.")

        val renamed = characters.saveCharacter(
            saved.copy(spells = saved.spells.map { if (it.id == boltId) it.copy(school = "Homebrew: Cronomancia") else it }),
        )
        assertEquals("Homebrew: Cronomancia", renamed.spells.single { it.id == boltId }.school)
    }

    @Test
    fun migrationAddsAnEmptySchoolWithoutLosingExistingSpells() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        try {
            AppDatabase.Schema.create(driver)
            val database = AppDatabase(driver)
            val characters = CharacterRepository(database)
            val created = characters.createCharacter(CampaignRepository(database).createCampaign("Antes").id, "Antigua")
            val spellId = Uuid.random()
            characters.saveCharacter(created.copy(spells = listOf(spell(spellId, "Luz", null, 0))))
            // Rebuild the version-26 shape: character_spell without the school column.
            driver.execute(null, "ALTER TABLE character_spell DROP COLUMN school", 0)
            driver.execute(null, "ALTER TABLE character_class DROP COLUMN epic_levels_enabled", 0)
            // Migrations 36 and 37 (QA 0.7.0-preqa.2) add these columns.
            driver.execute(null, "ALTER TABLE handout_payload DROP COLUMN shared_with_players", 0)
            driver.execute(null, "ALTER TABLE handout_payload DROP COLUMN folder", 0)
            driver.execute(null, "ALTER TABLE handout_payload DROP COLUMN tags_json", 0)
            driver.execute(null, "ALTER TABLE scene_payload DROP COLUMN sections_json", 0)
            driver.execute(null, "ALTER TABLE character_combat_entry DROP COLUMN linked_item_id", 0)
            driver.execute(null, "ALTER TABLE character_combat_entry DROP COLUMN linked_spell_id", 0)
            // Owner B (2026-11-20): migration 39 creates this table.
            driver.execute(null, "DROP TABLE dm_content_conflict", 0)

            AppDatabase.Schema.migrate(driver, oldVersion = 26, newVersion = AppDatabase.Schema.version)

            val reloaded = characters.character(created.id)!!
            assertEquals(listOf(spellId), reloaded.spells.map { it.id })
            assertNull(reloaded.spells.single().school)
            val withSchool = characters.saveCharacter(reloaded.copy(spells = reloaded.spells.map { it.copy(school = "Evocación") }))
            assertEquals("Evocación", withSchool.spells.single().school)
        } finally {
            driver.close()
        }
    }

    @Test
    fun spellsSavedBeforeTheSchoolFieldDecodeWithoutOne() {
        val legacy = Json.encodeToString(CharacterSpell.serializer(), spell(Uuid.random(), "Luz", "x", 0))
            .replace(",\"school\":\"x\"", "")
        val decoded = Json { ignoreUnknownKeys = true }.decodeFromString(CharacterSpell.serializer(), legacy)
        assertNull(decoded.school)
    }

    private fun spell(id: Uuid, name: String, school: String?, sortOrder: Int) = CharacterSpell(
        id = id, name = name, level = 0, castingTime = "", rangeText = "", verbal = false, somatic = false,
        material = false, materialText = null, duration = "", concentration = false, ritual = false,
        description = "Descripción.", notes = null, sortOrder = sortOrder, school = school,
    )

    private fun withRepositories(block: (CampaignRepository, CharacterRepository) -> Unit) {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        val database = AppDatabase(driver)
        try {
            block(CampaignRepository(database), CharacterRepository(database))
        } finally {
            driver.close()
        }
    }
}
