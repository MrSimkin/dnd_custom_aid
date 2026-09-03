package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

const val CHARACTER_SPELL_FAVORITE_FILTER_KEY: String = "favorite"
const val CHARACTER_SPELL_PREPARED_FILTER_KEY: String = "prepared"
const val CHARACTER_SPELL_CONCENTRATION_FILTER_KEY: String = "concentration"
const val CHARACTER_SPELL_RITUAL_FILTER_KEY: String = "ritual"
const val CHARACTER_SPELL_VERBAL_FILTER_KEY: String = "verbal"
const val CHARACTER_SPELL_SOMATIC_FILTER_KEY: String = "somatic"
const val CHARACTER_SPELL_MATERIAL_FILTER_KEY: String = "material"

fun presentCharacterSpellLevel(
    spells: List<CharacterSpell>,
    level: Int,
    selectedSourceId: Uuid? = null,
    order: CharacterPresentationOrder = CharacterPresentationOrder.MANUAL,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
    sourceName: (Uuid) -> String? = { null },
    isFavorite: (CharacterSpell) -> Boolean = { false },
): List<CharacterSpell> = presentCharacterCollection(
    items = spells.filter { spell ->
        spell.level == level && spellVisibleForSource(spell, selectedSourceId)
    },
    order = order,
    manualOrder = CharacterSpell::sortOrder,
    label = CharacterSpell::name,
    stableKey = { it.id.toString() },
    query = query,
    searchableText = { spell ->
        buildList {
            add(spell.name)
            add(spell.castingTime)
            add(spell.rangeText)
            add(spell.materialText)
            add(spell.duration)
            add(spell.description)
            add(spell.notes)
            spell.sourceAssociations.forEach { association -> add(sourceName(association.sourceId)) }
        }
    },
    filterMatches = { spell, filters ->
        characterSpellFilterMatches(
            spell = spell,
            activeFilters = filters,
            selectedSourceId = selectedSourceId,
            favorite = isFavorite(spell),
        )
    },
)

fun characterSpellFilterMatches(
    spell: CharacterSpell,
    activeFilters: Set<String>,
    selectedSourceId: Uuid? = null,
    favorite: Boolean = false,
): Boolean {
    if (activeFilters.isEmpty()) return true
    if (CHARACTER_SPELL_FAVORITE_FILTER_KEY in activeFilters && !favorite) return false
    if (CHARACTER_SPELL_PREPARED_FILTER_KEY in activeFilters && !spellPreparedForView(spell, selectedSourceId)) return false
    if (CHARACTER_SPELL_CONCENTRATION_FILTER_KEY in activeFilters && !spell.concentration) return false
    if (CHARACTER_SPELL_RITUAL_FILTER_KEY in activeFilters && !spell.ritual) return false
    if (CHARACTER_SPELL_VERBAL_FILTER_KEY in activeFilters && !spell.verbal) return false
    if (CHARACTER_SPELL_SOMATIC_FILTER_KEY in activeFilters && !spell.somatic) return false
    if (CHARACTER_SPELL_MATERIAL_FILTER_KEY in activeFilters && !spell.material) return false
    return true
}

fun spellPreparedForView(
    spell: CharacterSpell,
    selectedSourceId: Uuid?,
): Boolean = if (selectedSourceId == null) {
    spell.sourceAssociations.any { it.prepared }
} else {
    spell.sourceAssociations.firstOrNull { it.sourceId == selectedSourceId }?.prepared == true
}

fun spellVisibleForSource(
    spell: CharacterSpell,
    selectedSourceId: Uuid?,
): Boolean = selectedSourceId == null || spell.sourceAssociations.any { it.sourceId == selectedSourceId }

fun moveCharacterSpellManual(
    spells: List<CharacterSpell>,
    spellId: Uuid,
    offset: Int,
    selectedSourceId: Uuid? = null,
): List<CharacterSpell> {
    if (offset == 0) return normalizeCharacterSpellOrders(spells)
    val selected = spells.firstOrNull { it.id == spellId } ?: return normalizeCharacterSpellOrders(spells)
    val level = selected.level
    val levelOrdered = spells
        .filter { it.level == level }
        .sortedWith(compareBy<CharacterSpell> { it.sortOrder }.thenBy { it.id.toString() })
    if (levelOrdered.size < 2) return normalizeCharacterSpellOrders(spells)

    val visiblePositions = levelOrdered.indices.filter { index ->
        spellVisibleForSource(levelOrdered[index], selectedSourceId)
    }
    val visibleItems = visiblePositions.map(levelOrdered::get).toMutableList()
    val visibleIndex = visibleItems.indexOfFirst { it.id == spellId }
    val target = visibleIndex + offset
    if (visibleIndex < 0 || target !in visibleItems.indices) return normalizeCharacterSpellOrders(spells)

    val moved = visibleItems.removeAt(visibleIndex)
    visibleItems.add(target, moved)
    val replacementByPosition = visiblePositions.zip(visibleItems).toMap()
    val reorderedLevel = levelOrdered.mapIndexed { index, spell -> replacementByPosition[index] ?: spell }
    val levelOrderById = reorderedLevel.mapIndexed { index, spell -> spell.id to index }.toMap()

    return spells.map { spell ->
        if (spell.level == level) spell.copy(sortOrder = levelOrderById.getValue(spell.id)) else spell
    }
}

fun normalizeCharacterSpellOrders(spells: List<CharacterSpell>): List<CharacterSpell> {
    val orderById = spells
        .groupBy(CharacterSpell::level)
        .flatMap { (_, levelSpells) ->
            levelSpells
                .sortedWith(compareBy<CharacterSpell> { it.sortOrder }.thenBy { it.id.toString() })
                .mapIndexed { index, spell -> spell.id to index }
        }
        .toMap()
    return spells.map { spell -> spell.copy(sortOrder = orderById.getValue(spell.id)) }
}

fun duplicateCharacterSpell(
    source: CharacterSpell,
    newId: Uuid,
    sortOrder: Int,
): CharacterSpell = source.copy(
    id = newId,
    name = source.name.trim().let { if (it.isEmpty()) "Copia" else "$it (copia)" },
    sortOrder = sortOrder,
)

fun nextCharacterSpellSortOrder(
    spells: List<CharacterSpell>,
    level: Int,
    excludingId: Uuid? = null,
): Int = spells
    .asSequence()
    .filter { it.level == level && it.id != excludingId }
    .maxOfOrNull(CharacterSpell::sortOrder)
    ?.plus(1)
    ?: 0
