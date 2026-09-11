package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.math.hypot

/** Geometry-only slot representation used by the P6 pick-up-and-move reorder engine. */
data class CharacterReorderSlot(
    val id: String,
    val centerX: Float,
    val centerY: Float,
)

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
        val distance = hypot(
            (visualCenterX - slot.centerX).toDouble(),
            (visualCenterY - slot.centerY).toDouble(),
        ).toFloat()
        if (distance < bestDistance) {
            bestDistance = distance
            bestIndex = index
        }
    }
    return bestIndex
}

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
