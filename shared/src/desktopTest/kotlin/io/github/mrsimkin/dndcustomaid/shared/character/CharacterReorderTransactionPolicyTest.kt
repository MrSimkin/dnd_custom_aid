package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CharacterReorderTransactionPolicyTest {
    @Test
    fun complete_reorder_accepts_only_same_unique_id_set() {
        val canonical = listOf("a", "b", "c")

        assertTrue(isValidCharacterReorderResult(canonical, listOf("c", "a", "b")))
        assertFalse(isValidCharacterReorderResult(canonical, listOf("a", "b")))
        assertFalse(isValidCharacterReorderResult(canonical, listOf("a", "b", "missing")))
        assertFalse(isValidCharacterReorderResult(canonical, listOf("a", "a", "c")))
        assertFalse(isValidCharacterReorderResult(listOf("a", "a", "c"), listOf("a", "c", "a")))
    }

    @Test
    fun complete_reorder_applies_atomically_or_preserves_canonical_order() {
        val canonical = listOf("a", "b", "c", "d")

        assertEquals(
            listOf("d", "a", "b", "c"),
            applyCharacterReorderResult(canonical, listOf("d", "a", "b", "c")),
        )
        assertEquals(canonical, applyCharacterReorderResult(canonical, listOf("a", "b", "d")))
        assertEquals(canonical, canonical)
    }

    @Test
    fun semantic_step_is_bounded_and_uses_same_final_order_shape() {
        val canonical = listOf("a", "b", "c", "d")

        assertEquals(listOf("a", "c", "b", "d"), moveCharacterReorderSemanticStep(canonical, "b", 1))
        assertEquals(listOf("b", "a", "c", "d"), moveCharacterReorderSemanticStep(canonical, "b", -1))
        assertEquals(canonical, moveCharacterReorderSemanticStep(canonical, "a", -1))
        assertEquals(canonical, moveCharacterReorderSemanticStep(canonical, "d", 1))
        assertEquals(canonical, moveCharacterReorderSemanticStep(canonical, "missing", 1))
        assertEquals(canonical, moveCharacterReorderSemanticStep(canonical, "b", 0))
    }

    @Test
    fun subset_result_changes_only_subset_positions() {
        val all = listOf("ordinary-a", "special-x", "ordinary-b", "special-y", "ordinary-c")
        val ordinary = listOf("ordinary-a", "ordinary-b", "ordinary-c")

        assertEquals(
            listOf("ordinary-c", "special-x", "ordinary-a", "special-y", "ordinary-b"),
            applyCharacterReorderedSubsetResult(
                allIds = all,
                currentSubsetIds = ordinary,
                proposedSubsetIds = listOf("ordinary-c", "ordinary-a", "ordinary-b"),
            ),
        )
    }

    @Test
    fun subset_result_rejects_incomplete_duplicate_foreign_or_nonexistent_subset() {
        val all = listOf("a", "x", "b", "y", "c")
        val subset = listOf("a", "b", "c")

        assertEquals(all, applyCharacterReorderedSubsetResult(all, subset, listOf("c", "a")))
        assertEquals(all, applyCharacterReorderedSubsetResult(all, subset, listOf("c", "a", "a")))
        assertEquals(all, applyCharacterReorderedSubsetResult(all, subset, listOf("c", "a", "foreign")))
        assertEquals(
            all,
            applyCharacterReorderedSubsetResult(
                allIds = all,
                currentSubsetIds = listOf("a", "b", "not-in-all"),
                proposedSubsetIds = listOf("b", "a", "not-in-all"),
            ),
        )
    }
}
