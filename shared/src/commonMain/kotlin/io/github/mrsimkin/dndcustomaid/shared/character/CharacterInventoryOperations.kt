package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

enum class CharacterInventoryFilterKey(val key: String) {
    CARRIED("carried"),
    STORED("stored"),
    EQUIPPED("equipped"),
    SPECIAL("special"),
    CONSUMABLE("consumable"),
    AMMUNITION("ammunition"),
    LOCATED("located"),
}

fun CharacterClosureState.inventoryUsageFor(itemId: Uuid): CharacterInventoryUsage =
    inventoryUsage.firstOrNull { it.itemId == itemId } ?: CharacterInventoryUsage(itemId)

fun CharacterClosureState.withInventoryUsage(usage: CharacterInventoryUsage): CharacterClosureState {
    require(usage.quickUseAmount > 0) { "Quick-use amount must be positive." }
    val remaining = inventoryUsage.filterNot { it.itemId == usage.itemId }
    val isDefault = usage.kind == CharacterConsumableKind.NONE &&
        usage.quickUseAmount == 1 &&
        usage.carryState == CharacterInventoryCarryState.CARRIED
    return copy(inventoryUsage = if (isDefault) remaining else remaining + usage)
}

fun effectiveInventoryCarryState(
    item: CharacterInventoryItem,
    usage: CharacterInventoryUsage,
): CharacterInventoryCarryState = if (item.equipped) {
    CharacterInventoryCarryState.CARRIED
} else {
    usage.carryState
}

fun presentCharacterInventorySection(
    items: List<CharacterInventoryItem>,
    special: Boolean,
    order: CharacterPresentationOrder,
    query: CharacterCollectionQuery = CharacterCollectionQuery(),
    usageFor: (CharacterInventoryItem) -> CharacterInventoryUsage = { CharacterInventoryUsage(it.id) },
): List<CharacterInventoryItem> = presentCharacterCollection(
    items = items.filter { it.special == special },
    order = order,
    manualOrder = CharacterInventoryItem::sortOrder,
    label = CharacterInventoryItem::name,
    stableKey = { it.id.toString() },
    query = query,
    searchableText = { item -> listOf(item.name, item.location, item.notes, item.description) },
    filterMatches = { item, activeFilters -> inventoryFilterMatches(item, usageFor(item), activeFilters) },
)

fun inventoryFilterMatches(
    item: CharacterInventoryItem,
    usage: CharacterInventoryUsage,
    activeFilters: Set<String>,
): Boolean {
    if (activeFilters.isEmpty()) return true

    val active = CharacterInventoryFilterKey.entries.filter { it.key in activeFilters }.toSet()
    if (active.isEmpty()) return true

    val carryFilters = active.intersect(
        setOf(CharacterInventoryFilterKey.CARRIED, CharacterInventoryFilterKey.STORED),
    )
    if (carryFilters.isNotEmpty()) {
        val effectiveCarry = effectiveInventoryCarryState(item, usage)
        val carryMatches =
            (CharacterInventoryFilterKey.CARRIED in carryFilters && effectiveCarry == CharacterInventoryCarryState.CARRIED) ||
                (CharacterInventoryFilterKey.STORED in carryFilters && effectiveCarry == CharacterInventoryCarryState.STORED)
        if (!carryMatches) return false
    }

    val kindFilters = active.intersect(
        setOf(CharacterInventoryFilterKey.CONSUMABLE, CharacterInventoryFilterKey.AMMUNITION),
    )
    if (kindFilters.isNotEmpty()) {
        val kindMatches =
            (CharacterInventoryFilterKey.CONSUMABLE in kindFilters && usage.kind == CharacterConsumableKind.CONSUMABLE) ||
                (CharacterInventoryFilterKey.AMMUNITION in kindFilters && usage.kind == CharacterConsumableKind.AMMUNITION)
        if (!kindMatches) return false
    }

    if (CharacterInventoryFilterKey.EQUIPPED in active && !item.equipped) return false
    if (CharacterInventoryFilterKey.SPECIAL in active && !item.special) return false
    if (CharacterInventoryFilterKey.LOCATED in active && item.location.isNullOrBlank()) return false
    return true
}

fun carriedInventoryWeightLb(
    items: List<CharacterInventoryItem>,
    usageFor: (CharacterInventoryItem) -> CharacterInventoryUsage = { CharacterInventoryUsage(it.id) },
): Double = items
    .filter { effectiveInventoryCarryState(it, usageFor(it)) == CharacterInventoryCarryState.CARRIED }
    .sumOf(CharacterInventoryItem::carriedWeightLb)

fun consumeInventoryItem(
    items: List<CharacterInventoryItem>,
    usage: CharacterInventoryUsage,
): List<CharacterInventoryItem> {
    require(usage.quickUseAmount > 0) { "Quick-use amount must be positive." }
    if (usage.kind == CharacterConsumableKind.NONE) return items
    return items.map { item ->
        if (item.id == usage.itemId) {
            item.copy(quantity = (item.quantity - usage.quickUseAmount).coerceAtLeast(0))
        } else {
            item
        }
    }
}

fun duplicateInventoryItem(
    source: CharacterInventoryItem,
    newId: Uuid,
    sortOrder: Int,
): CharacterInventoryItem = source.copy(
    id = newId,
    name = source.name.trim().let { if (it.isEmpty()) "Copia" else "$it (copia)" },
    sortOrder = sortOrder,
)

fun duplicateInventoryUsage(
    source: CharacterInventoryUsage,
    newItemId: Uuid,
): CharacterInventoryUsage = source.copy(itemId = newItemId)
