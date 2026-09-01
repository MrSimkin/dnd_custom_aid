package io.github.mrsimkin.dndcustomaid.shared.character

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CharacterNotesPersistenceTest {
    @Test
    fun largeGeneralNotesAndOrderedTitledNotesPersistAcrossReopenEditReorderAndDelete() {
        val file = File.createTempFile("dnd-custom-aid-notes", ".db")
        file.delete()
        val jdbcUrl = "jdbc:sqlite:${file.absolutePath}"
        val firstNoteId = Uuid.random()
        val secondNoteId = Uuid.random()
        val thirdNoteId = Uuid.random()
        val largeGeneralNotes = buildString {
            repeat(350) { index ->
                append("Entrada libre ")
                append(index)
                append(": información extensa sin límite funcional impuesto por la app.\n")
            }
        }
        var characterId: Uuid? = null

        try {
            JdbcSqliteDriver(jdbcUrl).use { driver ->
                AppDatabase.Schema.create(driver)
                val database = AppDatabase(driver)
                val campaigns = CampaignRepository(database)
                val characters = CharacterRepository(database)
                val campaign = campaigns.createCampaign("Notas")
                val created = characters.createCharacter(campaign.id, "Cronista")
                characterId = created.id

                val saved = characters.saveCharacter(
                    created.copy(
                        generalNotes = largeGeneralNotes,
                        noteCards = listOf(
                            CharacterNote(firstNoteId, "PNJ", "Contenido de PNJ\ncon varias líneas.", 0),
                            CharacterNote(secondNoteId, "Lugar", "Una torre en el norte.", 1),
                            CharacterNote(thirdNoteId, "Pendiente", "Recordar hablar con el gremio.", 2),
                        ),
                    ),
                )

                assertEquals(largeGeneralNotes, saved.generalNotes)
                assertEquals(listOf(firstNoteId, secondNoteId, thirdNoteId), saved.noteCards.map { it.id })
                assertEquals(listOf(0, 1, 2), saved.noteCards.map { it.sortOrder })
            }

            JdbcSqliteDriver(jdbcUrl).use { driver ->
                val characters = CharacterRepository(AppDatabase(driver))
                val reopened = characters.character(requireNotNull(characterId))
                assertNotNull(reopened)
                assertEquals(largeGeneralNotes, reopened.generalNotes)
                assertEquals(listOf(firstNoteId, secondNoteId, thirdNoteId), reopened.noteCards.map { it.id })
                assertTrue(reopened.generalNotes.length > 10_000)

                val reorderedAndEdited = characters.saveCharacter(
                    reopened.copy(
                        generalNotes = reopened.generalNotes + "\nCierre de sesión.",
                        noteCards = listOf(
                            reopened.noteCards.single { it.id == thirdNoteId }.copy(sortOrder = 0),
                            reopened.noteCards.single { it.id == firstNoteId }.copy(
                                title = "PNJ importantes",
                                content = "Contenido actualizado y libre.",
                                sortOrder = 1,
                            ),
                            reopened.noteCards.single { it.id == secondNoteId }.copy(sortOrder = 2),
                        ),
                    ),
                )

                assertEquals(listOf(thirdNoteId, firstNoteId, secondNoteId), reorderedAndEdited.noteCards.map { it.id })
                assertEquals(listOf(0, 1, 2), reorderedAndEdited.noteCards.map { it.sortOrder })
                assertEquals("PNJ importantes", reorderedAndEdited.noteCards[1].title)
                assertEquals("Contenido actualizado y libre.", reorderedAndEdited.noteCards[1].content)

                val afterDeletion = characters.saveCharacter(
                    reorderedAndEdited.copy(
                        noteCards = reorderedAndEdited.noteCards.filterNot { it.id == firstNoteId },
                    ),
                )
                assertEquals(listOf(thirdNoteId, secondNoteId), afterDeletion.noteCards.map { it.id })
                assertEquals(listOf(0, 1), afterDeletion.noteCards.map { it.sortOrder })
            }

            JdbcSqliteDriver(jdbcUrl).use { driver ->
                val finalRead = CharacterRepository(AppDatabase(driver)).character(requireNotNull(characterId))
                assertNotNull(finalRead)
                assertTrue(finalRead.generalNotes.endsWith("Cierre de sesión."))
                assertEquals(listOf(thirdNoteId, secondNoteId), finalRead.noteCards.map { it.id })
                assertEquals(listOf("Pendiente", "Lugar"), finalRead.noteCards.map { it.title })
                assertEquals(listOf(0, 1), finalRead.noteCards.map { it.sortOrder })
            }
        } finally {
            file.delete()
        }
    }
}
