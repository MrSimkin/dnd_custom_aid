package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class CharacterM4ProficiencyTest {
    @Test
    fun proficiencyManualOrderNormalizesAndMovesWithoutChangingIdentity() {
        val first = CharacterProficiency(
            id = Uuid.random(),
            type = CharacterProficiencyType.LANGUAGE,
            name = "Enano",
            sortOrder = 8,
        )
        val second = CharacterProficiency(
            id = Uuid.random(),
            type = CharacterProficiencyType.TOOL,
            name = "Herramientas de ladrón",
            sortOrder = 2,
        )

        val normalized = normalizeCharacterProficiencies(listOf(first, second))
        assertEquals(listOf(second.id, first.id), normalized.map { it.id })
        assertEquals(listOf(0, 1), normalized.map { it.sortOrder })

        val moved = moveCharacterProficiencyManual(normalized, first.id, -1)
        assertEquals(listOf(first.id, second.id), moved.map { it.id })
        assertEquals(listOf(0, 1), moved.map { it.sortOrder })
    }

    @Test
    fun proficiencyKindsSourceNotesAndManualOrderRoundTripThroughRepository() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        try {
            val database = AppDatabase(driver)
            val campaigns = CampaignRepository(database)
            val characters = CharacterRepository(database)
            val campaign = campaigns.createCampaign("M4a")
            val original = characters.createCharacter(campaign.id, "Competente")

            val entries = listOf(
                CharacterProficiency(Uuid.random(), CharacterProficiencyType.LANGUAGE, "Dracónico", "Trasfondo", "Leído y hablado", 0),
                CharacterProficiency(Uuid.random(), CharacterProficiencyType.TOOL, "Herramientas de ladrón", "Clase", null, 1),
                CharacterProficiency(Uuid.random(), CharacterProficiencyType.WEAPON, "Ballesta de mano", null, "Entrenamiento", 2),
                CharacterProficiency(Uuid.random(), CharacterProficiencyType.ARMOR, "Armadura media", "Dote", null, 3),
                CharacterProficiency(Uuid.random(), CharacterProficiencyType.OTHER, "Vehículos terrestres", null, null, 4),
            )

            characters.saveCharacter(original.copy(proficiencies = entries))
            val reopened = requireNotNull(characters.character(original.id))

            assertEquals(entries.map { it.id }, reopened.proficiencies.map { it.id })
            assertEquals(CharacterProficiencyType.entries.toSet(), reopened.proficiencies.map { it.type }.toSet())
            assertEquals("Trasfondo", reopened.proficiencies.first().source)
            assertEquals("Leído y hablado", reopened.proficiencies.first().notes)
            assertEquals(listOf(0, 1, 2, 3, 4), reopened.proficiencies.map { it.sortOrder })
        } finally {
            driver.close()
        }
    }
}
