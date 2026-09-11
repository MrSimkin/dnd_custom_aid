package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Validate a complete final reorder result before it is applied to structural draft state.
 *
 * Reorder may change position only: it must preserve the exact stable-id set, contain no
 * duplicates, and retain the same cardinality. Invalid proposals are rejected rather than
 * partially repaired so callers cannot silently lose or duplicate character-owned entities.
 */
fun isValidCharacterReorderResult(
    canonicalIds: List<String>,
    proposedIds: List<String>,
): Boolean {
    if (canonicalIds.size != proposedIds.size) return false
    if (canonicalIds.distinct().size != canonicalIds.size) return false
    if (proposedIds.distinct().size != proposedIds.size) return false
    return canonicalIds.toSet() == proposedIds.toSet()
}

/** Apply a complete final stable-id order, or preserve canonical order when the proposal is invalid. */
fun applyCharacterReorderResult(
    canonicalIds: List<String>,
    proposedIds: List<String>,
): List<String> =
    if (isValidCharacterReorderResult(canonicalIds, proposedIds)) proposedIds.toList() else canonicalIds

/**
 * Accessibility/keyboard semantic equivalent of one direct-drag placement step.
 *
 * This is intentionally a final-order transformation rather than a gesture API. Visual drag and
 * semantic actions therefore converge on the same structural-draft transaction contract.
 */
fun moveCharacterReorderSemanticStep(
    canonicalIds: List<String>,
    id: String,
    delta: Int,
): List<String> {
    if (delta == 0 || canonicalIds.isEmpty()) return canonicalIds
    val sourceIndex = canonicalIds.indexOf(id)
    if (sourceIndex < 0) return canonicalIds
    val targetIndex = (sourceIndex + delta).coerceIn(0, canonicalIds.lastIndex)
    return previewCharacterReorder(canonicalIds, id, targetIndex)
}

/**
 * Apply a complete reordered subset while preserving all non-subset positions.
 *
 * The proposal must contain exactly the ids belonging to the current subset. This wraps the pure
 * subset merge with an explicit validity boundary suitable for one successful-drop transaction.
 */
fun applyCharacterReorderedSubsetResult(
    allIds: List<String>,
    currentSubsetIds: List<String>,
    proposedSubsetIds: List<String>,
): List<String> {
    if (!isValidCharacterReorderResult(currentSubsetIds, proposedSubsetIds)) return allIds
    if (currentSubsetIds.any { it !in allIds }) return allIds
    return mergeCharacterReorderedSubset(allIds, proposedSubsetIds)
}
