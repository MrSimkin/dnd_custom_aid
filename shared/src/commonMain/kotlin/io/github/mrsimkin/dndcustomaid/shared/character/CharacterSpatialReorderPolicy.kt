package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.math.hypot

/** Geometry-only slot representation used by the P6 pick-up-and-move reorder engine. */
data class CharacterReorderSlot(
    val id: String,
    val centerX: Float,
    val centerY: Float,
)

private fun characterReorderSlotDistance(
    firstX: Float,
    firstY: Float,
    secondX: Float,
    secondY: Float,
): Float = hypot(
    (firstX - secondX).toDouble(),
    (firstY - secondY).toDouble(),
).toFloat()

/**
 * Return the index of the rendered slot whose center is nearest to the lifted item's visual center.
 * Missing/non-rendered ids are deliberately ignored; lazy-list callers must expose only currently
 * measured slots so stale off-screen geometry cannot attract the drag target.
 */
fun nearestCharacterReorderIndex(
    order: List<String>,
    draggedId: String,
    visualCenterX: Float,
    visualCenterY: Float,
    renderedSlots: List<CharacterReorderSlot>,
): Int {
    val currentIndex = order.indexOf(draggedId)
    if (currentIndex < 0) return -1
    val slotById = renderedSlots.associateBy { it.id }
    var bestIndex = currentIndex
    var bestDistance = Float.POSITIVE_INFINITY
    order.forEachIndexed { index, id ->
        val slot = slotById[id] ?: return@forEachIndexed
        val distance = characterReorderSlotDistance(
            visualCenterX,
            visualCenterY,
            slot.centerX,
            slot.centerY,
        )
        if (distance < bestDistance) {
            bestDistance = distance
            bestIndex = index
        }
    }
    return bestIndex
}

/**
 * Resolve a drag target against drag-start geometry with a small geometric hysteresis.
 *
 * The caller supplies the canonical order captured when pickup began and a stable slot snapshot.
 * The currently accepted target is retained until a different slot is not merely closer, but
 * closer by [hysteresisFraction] of the distance between the two slot centers. This deadband keeps
 * a pointer hovering near a slot boundary from making the preview chatter between two positions.
 *
 * Critically, callers must not substitute animated preview-layout geometry for [stableSlots]. The
 * preview may move around the lifted card, but those movements are feedback, not fresh drag input.
 */
fun stableCharacterReorderTargetIndex(
    canonicalOrder: List<String>,
    draggedId: String,
    visualCenterX: Float,
    visualCenterY: Float,
    stableSlots: List<CharacterReorderSlot>,
    currentTargetIndex: Int = canonicalOrder.indexOf(draggedId),
    hysteresisFraction: Float = 0.12f,
): Int {
    val sourceIndex = canonicalOrder.indexOf(draggedId)
    if (sourceIndex < 0) return -1
    val candidateIndex = nearestCharacterReorderIndex(
        order = canonicalOrder,
        draggedId = draggedId,
        visualCenterX = visualCenterX,
        visualCenterY = visualCenterY,
        renderedSlots = stableSlots,
    )
    if (candidateIndex < 0) return sourceIndex
    if (currentTargetIndex !in canonicalOrder.indices || candidateIndex == currentTargetIndex) {
        return candidateIndex
    }

    val slotsById = stableSlots.associateBy { it.id }
    val currentSlot = slotsById[canonicalOrder[currentTargetIndex]] ?: return candidateIndex
    val candidateSlot = slotsById[canonicalOrder[candidateIndex]] ?: return candidateIndex
    val currentDistance = characterReorderSlotDistance(
        visualCenterX,
        visualCenterY,
        currentSlot.centerX,
        currentSlot.centerY,
    )
    val candidateDistance = characterReorderSlotDistance(
        visualCenterX,
        visualCenterY,
        candidateSlot.centerX,
        candidateSlot.centerY,
    )
    val slotSeparation = characterReorderSlotDistance(
        currentSlot.centerX,
        currentSlot.centerY,
        candidateSlot.centerX,
        candidateSlot.centerY,
    )
    val deadband = slotSeparation * hysteresisFraction.coerceIn(0f, 0.45f)
    return if (candidateDistance + deadband < currentDistance) candidateIndex else currentTargetIndex
}

/**
 * Translate a drag-start slot snapshot after a real viewport scroll.
 *
 * Compose scroll APIs report positive consumption when content moves toward the top, so callers
 * pass `-consumedScrollY` here. Layout animation/recomposition must not call this helper.
 */
fun translateCharacterReorderSlotsY(
    slots: List<CharacterReorderSlot>,
    deltaY: Float,
): List<CharacterReorderSlot> =
    if (deltaY == 0f) slots else slots.map { it.copy(centerY = it.centerY + deltaY) }

/** Move one stable id to a preview slot without mutating the canonical input list. */
fun previewCharacterReorder(
    order: List<String>,
    draggedId: String,
    targetIndex: Int,
): List<String> {
    val sourceIndex = order.indexOf(draggedId)
    if (sourceIndex < 0 || targetIndex !in order.indices || sourceIndex == targetIndex) return order
    val reordered = order.toMutableList()
    reordered.removeAt(sourceIndex)
    reordered.add(targetIndex.coerceIn(0, reordered.size), draggedId)
    return reordered
}

/**
 * Replace the relative order of one complete subset while preserving every non-subset position.
 * Useful for collections such as ordinary/special Equipment that share one persisted sort domain
 * but reorder independently on screen. Invalid/incomplete subsets are rejected as a no-op.
 */
fun mergeCharacterReorderedSubset(
    allIds: List<String>,
    reorderedSubsetIds: List<String>,
): List<String> {
    if (allIds.distinct().size != allIds.size || reorderedSubsetIds.distinct().size != reorderedSubsetIds.size) return allIds
    val subset = reorderedSubsetIds.toSet()
    if (subset.isEmpty()) return allIds
    val currentSubset = allIds.filter { it in subset }
    if (currentSubset.size != reorderedSubsetIds.size || currentSubset.toSet() != subset) return allIds
    val iterator = reorderedSubsetIds.iterator()
    return allIds.map { id -> if (id in subset) iterator.next() else id }
}
