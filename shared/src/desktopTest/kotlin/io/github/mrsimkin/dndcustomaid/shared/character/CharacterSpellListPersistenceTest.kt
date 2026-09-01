package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterSpellListPersistenceTest {
    @Test
    fun spellDetailsMultiSourcePreparedOrderUpdateAndDeletionPersist() =
        withRepositories { campaigns, characters ->
            val campaign = campaigns.createCampaign("Conjuros H")
            val created = characters.createCharacter(campaign.id, "Teúrgo")
            val wizardSourceId = Uuid.random()
            val clericSourceId = Uuid.random()
            val detectMagicId = Uuid.random()
            val shieldId = Uuid.random()

            val initial = characters.saveCharacter(
                created.copy(
                    spellcasterEnabled = true,
                    spellcastingSources = listOf(
                        CharacterSpellcastingSource(wizardSourceId, "Mago", null, 0),
                        CharacterSpellcastingSource(clericSourceId, "Clérigo", null, 1),
                    ),
                    spells = listOf(
                        CharacterSpell(
                            id = detectMagicId,
                            name = "Detectar magia",
                            level = 1,
                            castingTime = "1 acción",
                            rangeText = "Personal",
                            verbal = true,
                            somatic = true,
                            material = true,
                            materialText = "una pizca de polvo",
                            duration = "10 minutos",
                            concentration = true,
                            ritual = true,
                            description = "Percibes magia cercana.",
                            notes = "Referencia manual",
                            sortOrder = 0,
                            sourceAssociations = listOf(
                                CharacterSpellSourceAssociation(wizardSourceId, prepared = true),
                                CharacterSpellSourceAssociation(clericSourceId, prepared = false),
                            ),
                        ),
                        CharacterSpell(
                            id = shieldId,
                            name = "Escudo",
                            level = 1,
                            castingTime = "1 reacción",
                            rangeText = "Personal",
                            verbal = true,
                            somatic = true,
                            material = false,
                            materialText = null,
                            duration = "1 asalto",
                            concentration = false,
                            ritual = false,
                            description = "Barrera defensiva.",
                            notes = null,
                            sortOrder = 1,
                            sourceAssociations = listOf(
                                CharacterSpellSourceAssociation(wizardSourceId, prepared = false),
                            ),
                        ),
                    ),
                ),
            )

            assertEquals(listOf(detectMagicId, shieldId), initial.spells.map { it.id })
            val detectMagic = initial.spells.first()
            assertEquals("Detectar magia", detectMagic.name)
            assertEquals(1, detectMagic.level)
            assertEquals("1 acción", detectMagic.castingTime)
            assertEquals("Personal", detectMagic.rangeText)
            assertTrue(detectMagic.verbal)
            assertTrue(detectMagic.somatic)
            assertTrue(detectMagic.material)
            assertEquals("una pizca de polvo", detectMagic.materialText)
            assertEquals("10 minutos", detectMagic.duration)
            assertTrue(detectMagic.concentration)
            assertTrue(detectMagic.ritual)
            assertEquals("Percibes magia cercana.", detectMagic.description)
            assertEquals("Referencia manual", detectMagic.notes)
            assertEquals(
                listOf(
                    CharacterSpellSourceAssociation(wizardSourceId, prepared = true),
                    CharacterSpellSourceAssociation(clericSourceId, prepared = false),
                ),
                detectMagic.sourceAssociations,
            )

            val updated = characters.saveCharacter(
                initial.copy(
                    spells = listOf(
                        initial.spells.single { it.id == shieldId }.copy(sortOrder = 0),
                        initial.spells.single { it.id == detectMagicId }.copy(
                            castingTime = "1 acción o ritual",
                            notes = null,
                            sortOrder = 1,
                            sourceAssociations = listOf(
                                CharacterSpellSourceAssociation(wizardSourceId, prepared = false),
                                CharacterSpellSourceAssociation(clericSourceId, prepared = true),
                            ),
                        ),
                    ),
                ),
            )

            assertEquals(listOf(shieldId, detectMagicId), updated.spells.map { it.id })
            assertEquals(listOf(0, 1), updated.spells.map { it.sortOrder })
            val updatedDetectMagic = updated.spells.single { it.id == detectMagicId }
            assertEquals("1 acción o ritual", updatedDetectMagic.castingTime)
            assertNull(updatedDetectMagic.notes)
            assertFalse(updatedDetectMagic.sourceAssociations.single { it.sourceId == wizardSourceId }.prepared)
            assertTrue(updatedDetectMagic.sourceAssociations.single { it.sourceId == clericSourceId }.prepared)

            val afterDeletion = characters.saveCharacter(
                updated.copy(spells = updated.spells.filterNot { it.id == detectMagicId }),
            )

            assertEquals(listOf(shieldId), afterDeletion.spells.map { it.id })
            assertEquals(0, afterDeletion.spells.single().sortOrder)
            assertEquals(
                listOf(CharacterSpellSourceAssociation(wizardSourceId, prepared = false)),
                afterDeletion.spells.single().sourceAssociations,
            )
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
