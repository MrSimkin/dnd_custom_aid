package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CharacterSpellSlotIntegrationTest {
    @Test
    fun sharedSlotStateSurvivesUpdatesAndSpellcasterVisibilityToggle() =
        withRepositories { campaigns, characters ->
            val campaign = campaigns.createCampaign("Espacios compartidos")
            val created = characters.createCharacter(campaign.id, "Teúrgo")

            val quickMagicUpdate = characters.saveCharacter(
                created.copy(
                    spellcasterEnabled = true,
                    spellSlots = listOf(
                        CharacterSpellSlot(level = 1, totalSlots = 4, spentSlots = 1),
                        CharacterSpellSlot(level = 2, totalSlots = 3, spentSlots = 0),
                    ),
                ),
            )

            assertTrue(quickMagicUpdate.spellcasterEnabled)
            assertEquals(CharacterSpellSlot(1, 4, 1), quickMagicUpdate.spellSlots.single { it.level == 1 })

            // Represents Conjuros changing the spent count on the same authoritative slot record.
            val conjurosUpdate = characters.saveCharacter(
                quickMagicUpdate.copy(
                    spellSlots = quickMagicUpdate.spellSlots.map { slot ->
                        if (slot.level == 1) slot.copy(spentSlots = 3) else slot
                    },
                ),
            )

            assertEquals(CharacterSpellSlot(1, 4, 3), conjurosUpdate.spellSlots.single { it.level == 1 })
            assertEquals(CharacterSpellSlot(2, 3, 0), conjurosUpdate.spellSlots.single { it.level == 2 })

            // Represents Quick Magic observing and then mutating the same records again.
            val quickMagicRestore = characters.saveCharacter(
                conjurosUpdate.copy(
                    spellSlots = conjurosUpdate.spellSlots.map { it.copy(spentSlots = 0) },
                ),
            )

            assertTrue(quickMagicRestore.spellSlots.all { it.spentSlots == 0 })

            val hidden = characters.saveCharacter(quickMagicRestore.copy(spellcasterEnabled = false))
            assertFalse(hidden.spellcasterEnabled)
            assertEquals(quickMagicRestore.spellSlots, hidden.spellSlots)

            val restored = characters.saveCharacter(hidden.copy(spellcasterEnabled = true))
            assertTrue(restored.spellcasterEnabled)
            assertEquals(quickMagicRestore.spellSlots, restored.spellSlots)
        }

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
