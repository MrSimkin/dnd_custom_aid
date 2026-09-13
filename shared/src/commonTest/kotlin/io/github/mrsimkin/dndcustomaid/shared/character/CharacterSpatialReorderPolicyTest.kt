package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals

class CharacterSpatialReorderPolicyTest {
    private val canonical = listOf("A", "B", "C", "D")
    private val verticalSlots = listOf(
        CharacterReorderSlot("A", centerX = 50f, centerY = 50f),
        CharacterReorderSlot("B", centerX = 50f, centerY = 150f),
        CharacterReorderSlot("C", centerX = 50f, centerY = 250f),
        CharacterReorderSlot("D", centerX = 50f, centerY = 350f),
    )

    @Test
    fun stableTargetBuildsPreviewFromCanonicalOrderInsteadOfChasingPreviewGeometry() {
        val target = stableCharacterReorderTargetIndex(
            canonicalOrder = canonical,
            draggedId = "B",
            visualCenterX = 50f,
            visualCenterY = 255f,
            stableSlots = verticalSlots,
            currentTargetIndex = 1,
        )

        assertEquals(2, target)
        assertEquals(listOf("A", "C", "B", "D"), previewCharacterReorder(canonical, "B", target))

        // Re-evaluating the same pointer against the same drag-start geometry must be idempotent.
        // The preview order itself is deliberately not fed back into target calculation.
        val repeatedTarget = stableCharacterReorderTargetIndex(
            canonicalOrder = canonical,
            draggedId = "B",
            visualCenterX = 50f,
            visualCenterY = 255f,
            stableSlots = verticalSlots,
            currentTargetIndex = target,
        )
        assertEquals(target, repeatedTarget)
        assertEquals(listOf("A", "C", "B", "D"), previewCharacterReorder(canonical, "B", repeatedTarget))
    }

    @Test
    fun hysteresisKeepsCurrentTargetNearBisectorUntilPointerClearlyCrosses() {
        val nearBoundary = stableCharacterReorderTargetIndex(
            canonicalOrder = canonical,
            draggedId = "B",
            visualCenterX = 50f,
            visualCenterY = 201f,
            stableSlots = verticalSlots,
            currentTargetIndex = 1,
        )
        assertEquals(1, nearBoundary)

        val clearlyAcross = stableCharacterReorderTargetIndex(
            canonicalOrder = canonical,
            draggedId = "B",
            visualCenterX = 50f,
            visualCenterY = 220f,
            stableSlots = verticalSlots,
            currentTargetIndex = 1,
        )
        assertEquals(2, clearlyAcross)

        val nearBoundaryFromOtherSide = stableCharacterReorderTargetIndex(
            canonicalOrder = canonical,
            draggedId = "B",
            visualCenterX = 50f,
            visualCenterY = 199f,
            stableSlots = verticalSlots,
            currentTargetIndex = 2,
        )
        assertEquals(2, nearBoundaryFromOtherSide)
    }

    @Test
    fun stableTargetWorksAcrossTwoDimensionalCardSlots() {
        val gridSlots = listOf(
            CharacterReorderSlot("A", centerX = 50f, centerY = 50f),
            CharacterReorderSlot("B", centerX = 150f, centerY = 50f),
            CharacterReorderSlot("C", centerX = 50f, centerY = 150f),
            CharacterReorderSlot("D", centerX = 150f, centerY = 150f),
        )

        val target = stableCharacterReorderTargetIndex(
            canonicalOrder = canonical,
            draggedId = "A",
            visualCenterX = 145f,
            visualCenterY = 145f,
            stableSlots = gridSlots,
            currentTargetIndex = 0,
        )

        assertEquals(3, target)
        assertEquals(listOf("B", "C", "D", "A"), previewCharacterReorder(canonical, "A", target))
    }

    @Test
    fun viewportScrollTranslatesStableTargetsWithoutRebuildingThemFromPreviewLayout() {
        val translated = translateCharacterReorderSlotsY(verticalSlots, deltaY = -40f)

        assertEquals(listOf(10f, 110f, 210f, 310f), translated.map { it.centerY })
        assertEquals(verticalSlots.map { it.id }, translated.map { it.id })
        assertEquals(verticalSlots.map { it.centerX }, translated.map { it.centerX })
    }

    @Test
    fun missingUnrenderedSlotsCannotAttractTarget() {
        val onlyVisible = verticalSlots.filter { it.id == "B" || it.id == "C" }
        val target = stableCharacterReorderTargetIndex(
            canonicalOrder = canonical,
            draggedId = "B",
            visualCenterX = 50f,
            visualCenterY = 900f,
            stableSlots = onlyVisible,
            currentTargetIndex = 1,
        )

        assertEquals(2, target)
    }
}
