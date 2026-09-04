package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

fun normalizeCharacterProficiencies(
    proficiencies: List<CharacterProficiency>,
): List<CharacterProficiency> = proficiencies
    .sortedWith(compareBy<CharacterProficiency> { it.sortOrder }.thenBy { it.id.toString() })
    .mapIndexed { index, proficiency -> proficiency.copy(sortOrder = index) }

fun moveCharacterProficiencyManual(
    proficiencies: List<CharacterProficiency>,
    proficiencyId: Uuid,
    offset: Int,
): List<CharacterProficiency> {
    val normalized = normalizeCharacterProficiencies(proficiencies)
    val index = normalized.indexOfFirst { it.id == proficiencyId }
    val target = index + offset
    if (index < 0 || target !in normalized.indices) return normalized

    val reordered = normalized.toMutableList()
    val moved = reordered.removeAt(index)
    reordered.add(target, moved)
    return reordered.mapIndexed { order, proficiency -> proficiency.copy(sortOrder = order) }
}
