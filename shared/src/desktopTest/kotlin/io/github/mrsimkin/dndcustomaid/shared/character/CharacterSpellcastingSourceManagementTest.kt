package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class CharacterSpellcastingSourceManagementTest {
    @Test
    fun renameReorderDeleteAndClassRemovalPreserveSourceOwnershipRules() =
        withRepositories { campaigns, characters ->
            val campaign = campaigns.createCampaign("Fuentes")
            val created = characters.createCharacter(campaign.id, "Fuente múltiple")
            val classId = Uuid.random()
            val classSourceId = Uuid.random()
            val customSourceId = Uuid.random()
            val spellId = Uuid.random()

            val initial = characters.saveCharacter(
                created.copy(
                    classes = listOf(
                        CharacterClassLevel(
                            id = classId,
                            name = "Mago",
                            level = 4,
                            hitDieSides = 6,
                            hitDiceRemaining = 4,
                            sortOrder = 0,
                        ),
                    ),
                    spellcasterEnabled = true,
                    spellcastingSources = listOf(
                        CharacterSpellcastingSource(
                            id = classSourceId,
                            name = "Mago",
                            linkedClassId = classId,
                            sortOrder = 0,
                        ),
                        CharacterSpellcastingSource(
                            id = customSourceId,
                            name = "Dote",
                            linkedClassId = null,
                            sortOrder = 1,
                        ),
                    ),
                    spells = listOf(
                        CharacterSpell(
                            id = spellId,
                            name = "Detectar magia",
                            level = 1,
                            castingTime = "1 acción",
                            rangeText = "Personal",
                            verbal = true,
                            somatic = true,
                            material = false,
                            materialText = null,
                            duration = "10 minutos",
                            concentration = true,
                            ritual = true,
                            description = "Detecta magia cercana.",
                            notes = null,
                            sortOrder = 0,
                            sourceAssociations = listOf(
                                CharacterSpellSourceAssociation(classSourceId, prepared = true),
                                CharacterSpellSourceAssociation(customSourceId, prepared = false),
                            ),
                        ),
                    ),
                ),
            )

            val renamedAndReordered = characters.saveCharacter(
                initial.copy(
                    spellcastingSources = listOf(
                        initial.spellcastingSources.single { it.id == customSourceId }
                            .copy(name = "Dote: Iniciado", sortOrder = 0),
                        initial.spellcastingSources.single { it.id == classSourceId }
                            .copy(name = "Tradición arcana", sortOrder = 1),
                    ),
                ),
            )

            assertEquals(
                listOf(customSourceId, classSourceId),
                renamedAndReordered.spellcastingSources.map { it.id },
            )
            assertEquals(
                listOf("Dote: Iniciado", "Tradición arcana"),
                renamedAndReordered.spellcastingSources.map { it.name },
            )
            assertEquals(
                listOf(0, 1),
                renamedAndReordered.spellcastingSources.map { it.sortOrder },
            )

            val afterSourceDeletion = characters.saveCharacter(
                renamedAndReordered.copy(
                    spellcastingSources = renamedAndReordered.spellcastingSources
                        .filterNot { it.id == customSourceId },
                    // Deliberately leave the stale custom-source association in the incoming spell.
                    // Repository normalization must remove only that association, not the spell.
                    spells = renamedAndReordered.spells,
                ),
            )

            assertEquals(listOf(classSourceId), afterSourceDeletion.spellcastingSources.map { it.id })
            assertEquals(listOf(spellId), afterSourceDeletion.spells.map { it.id })
            assertEquals(
                listOf(CharacterSpellSourceAssociation(classSourceId, prepared = true)),
                afterSourceDeletion.spells.single().sourceAssociations,
            )

            val afterClassDeletion = characters.saveCharacter(
                afterSourceDeletion.copy(classes = emptyList()),
            )

            assertEquals(listOf(classSourceId), afterClassDeletion.spellcastingSources.map { it.id })
            assertNull(afterClassDeletion.spellcastingSources.single().linkedClassId)
            assertEquals(listOf(spellId), afterClassDeletion.spells.map { it.id })
            assertEquals(
                listOf(CharacterSpellSourceAssociation(classSourceId, prepared = true)),
                afterClassDeletion.spells.single().sourceAssociations,
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
