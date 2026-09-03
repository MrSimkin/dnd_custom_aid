package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Presentation-only ordering for character collections.
 *
 * ALPHABETICAL never rewrites a domain object's stored manual sort order. Consumers must persist
 * manual reorder operations separately and use this helper only when projecting the collection.
 */
enum class CharacterPresentationOrder {
    MANUAL,
    ALPHABETICAL,
}

data class CharacterCollectionQuery(
    val searchText: String = "",
    val activeFilterKeys: Set<String> = emptySet(),
) {
    val normalizedSearchText: String
        get() = normalizeCharacterSearchText(searchText)

    fun toggleFilter(key: String): CharacterCollectionQuery {
        val normalizedKey = key.trim()
        if (normalizedKey.isEmpty()) return this
        return copy(
            activeFilterKeys = if (normalizedKey in activeFilterKeys) {
                activeFilterKeys - normalizedKey
            } else {
                activeFilterKeys + normalizedKey
            },
        )
    }

    fun clear(): CharacterCollectionQuery = CharacterCollectionQuery()
}

fun normalizeCharacterSearchText(value: String): String = buildString(value.length) {
    value.trim().lowercase().forEach { character ->
        append(
            when (character) {
                'á', 'à', 'ä', 'â' -> 'a'
                'é', 'è', 'ë', 'ê' -> 'e'
                'í', 'ì', 'ï', 'î' -> 'i'
                'ó', 'ò', 'ö', 'ô' -> 'o'
                'ú', 'ù', 'ü', 'û' -> 'u'
                else -> character
            },
        )
    }
}

fun characterSearchMatches(
    normalizedQuery: String,
    vararg candidateTexts: String?,
): Boolean {
    if (normalizedQuery.isBlank()) return true
    return candidateTexts.any { candidate ->
        candidate != null && normalizeCharacterSearchText(candidate).contains(normalizedQuery)
    }
}

fun <T> presentCharacterCollection(
    items: List<T>,
    order: CharacterPresentationOrder,
    manualOrder: (T) -> Int,
    label: (T) -> String,
    stableKey: (T) -> String,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
    searchableText: (T) -> Iterable<String?> = { item -> listOf(label(item)) },
    filterMatches: (T, Set<String>) -> Boolean = { _, activeFilters -> activeFilters.isEmpty() },
): List<T> {
    val normalizedQuery = query.normalizedSearchText
    val filtered = items.filter { item ->
        val searchMatches = normalizedQuery.isBlank() || searchableText(item).any { candidate ->
            candidate != null && normalizeCharacterSearchText(candidate).contains(normalizedQuery)
        }
        searchMatches && filterMatches(item, query.activeFilterKeys)
    }

    return when (order) {
        CharacterPresentationOrder.MANUAL -> filtered.sortedWith(
            compareBy<T> { manualOrder(it) }
                .thenBy { stableKey(it) },
        )

        CharacterPresentationOrder.ALPHABETICAL -> filtered.sortedWith(
            compareBy<T> { normalizeCharacterSearchText(label(it)) }
                .thenBy { manualOrder(it) }
                .thenBy { stableKey(it) },
        )
    }
}
