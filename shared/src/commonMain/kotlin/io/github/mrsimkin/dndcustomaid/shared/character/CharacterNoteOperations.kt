package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

const val CHARACTER_NOTE_WITH_CONTENT_FILTER_KEY = "note-content:present"
const val CHARACTER_NOTE_EMPTY_FILTER_KEY = "note-content:empty"
private val CHARACTER_NOTE_CONTENT_FILTER_KEYS = setOf(
    CHARACTER_NOTE_WITH_CONTENT_FILTER_KEY,
    CHARACTER_NOTE_EMPTY_FILTER_KEY,
)

fun presentCharacterNotes(
    notes: List<CharacterNote>,
    order: CharacterPresentationOrder = CharacterPresentationOrder.MANUAL,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
): List<CharacterNote> = presentCharacterCollection(
    items = notes,
    order = order,
    manualOrder = CharacterNote::sortOrder,
    label = CharacterNote::title,
    stableKey = { it.id.toString() },
    query = query,
    searchableText = { note -> listOf(note.title, note.content) },
    filterMatches = { note, activeFilters ->
        val contentFilters = activeFilters.intersect(CHARACTER_NOTE_CONTENT_FILTER_KEYS)
        contentFilters.isEmpty() ||
            (CHARACTER_NOTE_WITH_CONTENT_FILTER_KEY in contentFilters && note.content.isNotBlank()) ||
            (CHARACTER_NOTE_EMPTY_FILTER_KEY in contentFilters && note.content.isBlank())
    },
)

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
