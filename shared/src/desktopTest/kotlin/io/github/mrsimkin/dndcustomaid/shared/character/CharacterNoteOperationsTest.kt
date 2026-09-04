package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.uuid.Uuid

class CharacterNoteOperationsTest {
    @Test
    fun normalizePreservesVisibleSequenceAndRewritesDenseSortOrder() {
        val first = note("Primera", 8)
        val second = note("Segunda", 2)
        val third = note("Tercera", 99)

        val normalized = normalizeCharacterNotes(listOf(first, second, third))

        assertEquals(listOf(first.id, second.id, third.id), normalized.map { it.id })
        assertEquals(listOf(0, 1, 2), normalized.map { it.sortOrder })
    }

    @Test
    fun manualMoveMovesOnlyRequestedNoteAndNormalizesOrder() {
        val first = note("Primera", 0)
        val second = note("Segunda", 1)
        val third = note("Tercera", 2)

        val moved = moveCharacterNoteManual(
            notes = listOf(first, second, third),
            noteId = second.id,
            offset = 1,
        )

        assertEquals(listOf(first.id, third.id, second.id), moved.map { it.id })
        assertEquals(listOf(0, 1, 2), moved.map { it.sortOrder })
    }

    @Test
    fun manualMoveAtBoundaryIsSafeNoOpApartFromNormalization() {
        val first = note("Primera", 4)
        val second = note("Segunda", 9)

        val moved = moveCharacterNoteManual(
            notes = listOf(first, second),
            noteId = first.id,
            offset = -1,
        )

        assertEquals(listOf(first.id, second.id), moved.map { it.id })
        assertEquals(listOf(0, 1), moved.map { it.sortOrder })
    }

    @Test
    fun duplicateGetsFreshIdentityAppendedOrderAndPreservesContent() {
        val source = CharacterNote(
            id = Uuid.random(),
            title = "Aliados",
            content = "Capitana Mira y el gremio del puerto.",
            sortOrder = 1,
        )
        val duplicateId = Uuid.random()

        val duplicate = duplicateCharacterNote(
            source = source,
            newId = duplicateId,
            sortOrder = nextCharacterNoteSortOrder(listOf(note("Otra", 0), source)),
        )

        assertNotEquals(source.id, duplicate.id)
        assertEquals(duplicateId, duplicate.id)
        assertEquals("Aliados (copia)", duplicate.title)
        assertEquals(source.content, duplicate.content)
        assertEquals(2, duplicate.sortOrder)
    }

    private fun note(title: String, sortOrder: Int): CharacterNote = CharacterNote(
        id = Uuid.random(),
        title = title,
        content = "Contenido de $title",
        sortOrder = sortOrder,
    )
}
