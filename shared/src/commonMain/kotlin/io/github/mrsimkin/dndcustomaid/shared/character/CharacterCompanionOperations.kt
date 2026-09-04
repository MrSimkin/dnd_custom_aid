package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

const val CHARACTER_COMPANION_FAVORITE_FILTER_KEY: String = "favorite"
const val CHARACTER_COMPANION_ACTIVE_FILTER_KEY: String = "active"
private const val CHARACTER_COMPANION_SOURCE_FILTER_PREFIX = "source:"
private const val CHARACTER_COMPANION_KIND_FILTER_PREFIX = "kind:"
private const val CHARACTER_COMPANION_EMPTY_FILTER_KEY = "_none"

fun characterCompanionSourceFilterKey(source: String?): String =
    "$CHARACTER_COMPANION_SOURCE_FILTER_PREFIX${normalizeCharacterSearchText(source.orEmpty()).ifBlank { CHARACTER_COMPANION_EMPTY_FILTER_KEY }}"

fun characterCompanionKindFilterKey(kind: String): String =
    "$CHARACTER_COMPANION_KIND_FILTER_PREFIX${normalizeCharacterSearchText(kind).ifBlank { CHARACTER_COMPANION_EMPTY_FILTER_KEY }}"

fun presentCharacterCompanions(
    companions: List<CharacterCompanion>,
    order: CharacterPresentationOrder = CharacterPresentationOrder.MANUAL,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
    isFavorite: (CharacterCompanion) -> Boolean = { false },
): List<CharacterCompanion> = presentCharacterCollection(
    items = companions,
    order = order,
    manualOrder = CharacterCompanion::sortOrder,
    label = CharacterCompanion::name,
    stableKey = { it.id.toString() },
    query = query,
    searchableText = { companion ->
        listOf(
            companion.name,
            companion.kind,
            companion.source,
            companion.speed,
            companion.abilitySummary,
            companion.sensesProficiencies,
            companion.traitsActions,
            companion.notes,
        )
    },
    filterMatches = { companion, filters ->
        characterCompanionFilterMatches(companion, filters, isFavorite(companion))
    },
)

fun characterCompanionFilterMatches(
    companion: CharacterCompanion,
    activeFilters: Set<String>,
    favorite: Boolean = false,
): Boolean {
    if (activeFilters.isEmpty()) return true
    if (CHARACTER_COMPANION_FAVORITE_FILTER_KEY in activeFilters && !favorite) return false
    if (CHARACTER_COMPANION_ACTIVE_FILTER_KEY in activeFilters && !companion.active) return false

    val sourceFilters = activeFilters.filterTo(mutableSetOf()) {
        it.startsWith(CHARACTER_COMPANION_SOURCE_FILTER_PREFIX)
    }
    if (sourceFilters.isNotEmpty() && characterCompanionSourceFilterKey(companion.source) !in sourceFilters) return false

    val kindFilters = activeFilters.filterTo(mutableSetOf()) {
        it.startsWith(CHARACTER_COMPANION_KIND_FILTER_PREFIX)
    }
    if (kindFilters.isNotEmpty() && characterCompanionKindFilterKey(companion.kind) !in kindFilters) return false
    return true
}

fun moveCharacterCompanionManual(
    companions: List<CharacterCompanion>,
    companionId: Uuid,
    offset: Int,
): List<CharacterCompanion> {
    val ordered = companions.sortedWith(
        compareBy<CharacterCompanion> { it.sortOrder }
            .thenBy { it.id.toString() },
    )
    if (offset == 0 || ordered.size < 2) return normalizeCharacterCompanionOrders(ordered)
    val index = ordered.indexOfFirst { it.id == companionId }
    val target = index + offset
    if (index < 0 || target !in ordered.indices) return normalizeCharacterCompanionOrders(ordered)

    val mutable = ordered.toMutableList()
    val moved = mutable.removeAt(index)
    mutable.add(target, moved)
    return mutable.mapIndexed { newIndex, companion -> companion.copy(sortOrder = newIndex) }
}

fun normalizeCharacterCompanionOrders(companions: List<CharacterCompanion>): List<CharacterCompanion> =
    companions.sortedWith(
        compareBy<CharacterCompanion> { it.sortOrder }
            .thenBy { it.id.toString() },
    ).mapIndexed { index, companion -> companion.copy(sortOrder = index) }

fun duplicateCharacterCompanion(
    source: CharacterCompanion,
    newId: Uuid,
    sortOrder: Int,
): CharacterCompanion = source.copy(
    id = newId,
    name = source.name.trim().let { if (it.isEmpty()) "Copia" else "$it (copia)" },
    sortOrder = sortOrder,
)

fun nextCharacterCompanionSortOrder(companions: List<CharacterCompanion>): Int =
    companions.maxOfOrNull(CharacterCompanion::sortOrder)?.plus(1) ?: 0
