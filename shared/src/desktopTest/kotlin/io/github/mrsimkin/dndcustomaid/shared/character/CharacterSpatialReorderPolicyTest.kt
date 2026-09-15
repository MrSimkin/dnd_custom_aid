package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals

class CharacterSpatialReorderPolicyTest {
    @Test
    fun preview_moves_forward_and_backward_without_mutating_input() {
        val original = listOf("a", "b", "c", "d")

        assertEquals(listOf("a", "c", "d", "b"), previewCharacterReorder(original, "b", 3))
        assertEquals(listOf("d", "a", "b", "c"), previewCharacterReorder(original, "d", 0))
        assertEquals(listOf("a", "b", "c", "d"), original)
    }

    @Test
    fun nearest_slot_uses_real_two_dimensional_geometry() {
        val order = listOf("a", "b", "c", "d")
        val slots = listOf(
            CharacterReorderSlot("a", 50f, 50f),
            CharacterReorderSlot("b", 150f, 50f),
            CharacterReorderSlot("c", 50f, 150f),
            CharacterReorderSlot("d", 150f, 150f),
        )

        assertEquals(3, nearestCharacterReorderIndex(order, "a", 142f, 144f, slots))
        assertEquals(2, nearestCharacterReorderIndex(order, "b", 46f, 139f, slots))
    }

    @Test
    fun nearest_slot_ignores_unmeasured_lazy_items() {
        val order = listOf("a", "b", "c", "d")
        val rendered = listOf(
            CharacterReorderSlot("b", 50f, 100f),
            CharacterReorderSlot("c", 50f, 200f),
        )

        assertEquals(2, nearestCharacterReorderIndex(order, "b", 52f, 190f, rendered))
    }

    @Test
    fun subset_merge_preserves_non_subset_positions() {
        val all = listOf("ordinary-a", "special-x", "ordinary-b", "special-y", "ordinary-c")

        assertEquals(
            listOf("ordinary-c", "special-x", "ordinary-a", "special-y", "ordinary-b"),
            mergeCharacterReorderedSubset(
                allIds = all,
                reorderedSubsetIds = listOf("ordinary-c", "ordinary-a", "ordinary-b"),
            ),
        )
        assertEquals(all, all)
    }

    @Test
    fun invalid_or_incomplete_subset_merge_is_a_no_op() {
        val all = listOf("a", "x", "b", "y")

        assertEquals(all, mergeCharacterReorderedSubset(all, emptyList()))
        assertEquals(all, mergeCharacterReorderedSubset(all, listOf("a", "missing")))
        assertEquals(all, mergeCharacterReorderedSubset(all, listOf("a", "a")))
    }

    @Test
    fun invalid_drag_or_target_is_a_no_op() {
        val order = listOf("a", "b", "c")

        assertEquals(order, previewCharacterReorder(order, "missing", 1))
        assertEquals(order, previewCharacterReorder(order, "b", -1))
        assertEquals(order, previewCharacterReorder(order, "b", 9))
        assertEquals(order, previewCharacterReorder(order, "b", 1))
        assertEquals(-1, nearestCharacterReorderIndex(order, "missing", 0f, 0f, emptyList()))
    }
}
