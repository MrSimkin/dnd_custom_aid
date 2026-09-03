package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

enum class CharacterTraitGrouping {
    NONE,
    TYPE,
    SOURCE,
}

const val CHARACTER_TRAIT_FAVORITE_FILTER_KEY: String = "favorite"
private const val CHARACTER_TRAIT_TYPE_FILTER_PREFIX = "type:"
private const val CHARACTER_TRAIT_SOURCE_FILTER_PREFIX = "source:"
private const val CHARACTER_TRAIT_EMPTY_SOURCE_KEY = "_none"

data class CharacterTraitGroup(
    val key: String,
    val label: String,
    val traits: List<CharacterTrait>,
)

data class CharacterTraitUsageMeter(
    val remaining: Int,
    val max: Int,
    val spent: Int,
) {
    val remainingFraction: Float
        get() = if (max <= 0) 0f else remaining.toFloat() / max.toFloat()
}

fun characterTraitTypeFilterKey(type: CharacterTraitType): String =
    "$CHARACTER_TRAIT_TYPE_FILTER_PREFIX${type.name}"

fun characterTraitSourceFilterKey(source: String): String =
    "$CHARACTER_TRAIT_SOURCE_FILTER_PREFIX${normalizedCharacterTraitSourceKey(source)}"

fun presentCharacterTraits(
    traits: List<CharacterTrait>,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
    isFavorite: (CharacterTrait) -> Boolean = { false },
): List<CharacterTrait> = presentCharacterCollection(
    items = traits,
    order = CharacterPresentationOrder.MANUAL,
    manualOrder = CharacterTrait::sortOrder,
    label = CharacterTrait::name,
    stableKey = { it.id.toString() },
    query = query,
    searchableText = { trait ->
        listOf(
            trait.name,
            trait.source,
            trait.description,
            trait.notes,
            trait.recovery,
            characterTraitTypeSearchText(trait.type),
            trait.activation?.name,
        )
    },
    filterMatches = { trait, activeFilters ->
        characterTraitFilterMatches(trait, activeFilters, isFavorite(trait))
    },
)

fun characterTraitFilterMatches(
    trait: CharacterTrait,
    activeFilters: Set<String>,
    favorite: Boolean = false,
): Boolean {
    if (activeFilters.isEmpty()) return true

    val typeFilters = activeFilters.filterTo(mutableSetOf()) { it.startsWith(CHARACTER_TRAIT_TYPE_FILTER_PREFIX) }
    if (typeFilters.isNotEmpty() && characterTraitTypeFilterKey(trait.type) !in typeFilters) return false

    val sourceFilters = activeFilters.filterTo(mutableSetOf()) { it.startsWith(CHARACTER_TRAIT_SOURCE_FILTER_PREFIX) }
    if (sourceFilters.isNotEmpty() && characterTraitSourceFilterKey(trait.source) !in sourceFilters) return false

    if (CHARACTER_TRAIT_FAVORITE_FILTER_KEY in activeFilters && !favorite) return false
    return true
}

fun groupCharacterTraits(
    traits: List<CharacterTrait>,
    grouping: CharacterTraitGrouping,
): List<CharacterTraitGroup> {
    val ordered = traits.sortedWith(
        compareBy<CharacterTrait> { it.sortOrder }
            .thenBy { it.id.toString() },
    )
    if (ordered.isEmpty()) return emptyList()

    return when (grouping) {
        CharacterTraitGrouping.NONE -> listOf(
            CharacterTraitGroup(
                key = "all",
                label = "Todos",
                traits = ordered,
            ),
        )

        CharacterTraitGrouping.TYPE -> CharacterTraitType.entries.mapNotNull { type ->
            val matching = ordered.filter { it.type == type }
            matching.takeIf { it.isNotEmpty() }?.let {
                CharacterTraitGroup(
                    key = characterTraitTypeFilterKey(type),
                    label = characterTraitTypeDisplayLabel(type),
                    traits = it,
                )
            }
        }

        CharacterTraitGrouping.SOURCE -> ordered
            .groupBy { normalizedCharacterTraitSourceKey(it.source) }
            .map { (sourceKey, matching) ->
                val label = matching.firstNotNullOfOrNull { trait -> trait.source.trim().takeIf(String::isNotEmpty) }
                    ?: "Sin fuente"
                CharacterTraitGroup(
                    key = "$CHARACTER_TRAIT_SOURCE_FILTER_PREFIX$sourceKey",
                    label = label,
                    traits = matching,
                )
            }
            .sortedWith(
                compareBy<CharacterTraitGroup> { normalizeCharacterSearchText(it.label) }
                    .thenBy { it.key },
            )
    }
}

fun moveCharacterTraitManual(
    traits: List<CharacterTrait>,
    traitId: Uuid,
    offset: Int,
    grouping: CharacterTraitGrouping = CharacterTraitGrouping.NONE,
): List<CharacterTrait> {
    if (offset == 0 || traits.size < 2) return normalizeCharacterTraitOrder(traits)

    val ordered = traits.sortedWith(
        compareBy<CharacterTrait> { it.sortOrder }
            .thenBy { it.id.toString() },
    )
    val selected = ordered.firstOrNull { it.id == traitId } ?: return normalizeCharacterTraitOrder(ordered)
    val groupKey = characterTraitGroupingKey(selected, grouping)
    val groupPositions = ordered.indices.filter { index ->
        characterTraitGroupingKey(ordered[index], grouping) == groupKey
    }
    val groupItems = groupPositions.map(ordered::get).toMutableList()
    val indexInGroup = groupItems.indexOfFirst { it.id == traitId }
    val target = indexInGroup + offset
    if (indexInGroup < 0 || target !in groupItems.indices) return normalizeCharacterTraitOrder(ordered)

    val moved = groupItems.removeAt(indexInGroup)
    groupItems.add(target, moved)
    val replacementByPosition = groupPositions.zip(groupItems).toMap()
    return ordered.mapIndexed { index, trait -> replacementByPosition[index] ?: trait }
        .mapIndexed { index, trait -> trait.copy(sortOrder = index) }
}

fun duplicateCharacterTrait(
    source: CharacterTrait,
    newId: Uuid,
    sortOrder: Int,
): CharacterTrait = source.copy(
    id = newId,
    name = source.name.trim().let { if (it.isEmpty()) "Copia" else "$it (copia)" },
    sortOrder = sortOrder,
)

fun characterTraitUsageMeter(trait: CharacterTrait): CharacterTraitUsageMeter? {
    val max = trait.maxUses?.takeIf { it > 0 } ?: return null
    val spent = trait.spentUses.coerceIn(0, max)
    return CharacterTraitUsageMeter(
        remaining = max - spent,
        max = max,
        spent = spent,
    )
}

private fun normalizeCharacterTraitOrder(traits: List<CharacterTrait>): List<CharacterTrait> =
    traits.sortedWith(
        compareBy<CharacterTrait> { it.sortOrder }
            .thenBy { it.id.toString() },
    ).mapIndexed { index, trait -> trait.copy(sortOrder = index) }

private fun characterTraitGroupingKey(
    trait: CharacterTrait,
    grouping: CharacterTraitGrouping,
): String = when (grouping) {
    CharacterTraitGrouping.NONE -> "all"
    CharacterTraitGrouping.TYPE -> characterTraitTypeFilterKey(trait.type)
    CharacterTraitGrouping.SOURCE -> characterTraitSourceFilterKey(trait.source)
}

private fun normalizedCharacterTraitSourceKey(source: String): String =
    normalizeCharacterSearchText(source).ifBlank { CHARACTER_TRAIT_EMPTY_SOURCE_KEY }

private fun characterTraitTypeSearchText(type: CharacterTraitType): String =
    "${type.name} ${characterTraitTypeDisplayLabel(type)}"

fun characterTraitTypeDisplayLabel(type: CharacterTraitType): String = when (type) {
    CharacterTraitType.CLASS -> "Clase"
    CharacterTraitType.SPECIES_RACE -> "Especie / raza"
    CharacterTraitType.BACKGROUND -> "Trasfondo"
    CharacterTraitType.FEAT -> "Dote"
    CharacterTraitType.GIFT_BLESSING -> "Don / bendición"
    CharacterTraitType.OTHER -> "Otro"
}
