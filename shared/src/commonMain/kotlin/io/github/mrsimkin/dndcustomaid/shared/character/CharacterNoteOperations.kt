package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

fun normalizeCharacterNotes(notes: List<CharacterNote>): List<CharacterNote> =
    notes.mapIndexed { index, note -> note.copy(sortOrder = index) }

fun moveCharacterNoteManual(
    notes: List<CharacterNote>,
    noteId: Uuid,
    offset: Int,
): List<CharacterNote> {
    if (offset == 0 || notes.size < 2) return normalizeCharacterNotes(notes)

    val ordered = normalizeCharacterNotes(notes).toMutableList()
    val currentIndex = ordered.indexOfFirst { it.id == noteId }
    if (currentIndex < 0) return ordered

    val targetIndex = currentIndex + offset
    if (targetIndex !in ordered.indices) return ordered

    val moved = ordered.removeAt(currentIndex)
    ordered.add(targetIndex, moved)
    return normalizeCharacterNotes(ordered)
}

fun nextCharacterNoteSortOrder(notes: List<CharacterNote>): Int =
    notes.size

fun duplicateCharacterNote(
    source: CharacterNote,
    newId: Uuid,
    sortOrder: Int,
): CharacterNote = source.copy(
    id = newId,
    title = "${source.title} (copia)",
    sortOrder = sortOrder,
)
