package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class PcSheetV11AttributeSkillBudgetTest {
    @Test
    fun normalPaperGeometryRemainsExactlyTheSame() {
        val counts = listOf(1, 3, 0, 5, 5, 4)
        val placed = PcSheetV11AttributeSkillBudget.plan(counts, 577f, 110f)
        assertEquals(counts, placed)
        assertEquals(38f, PcSheetV11AttributeSkillBudget.heightPt(0))
        assertEquals(81.5f, PcSheetV11AttributeSkillBudget.heightPt(5))
        assertTrue(577f - placed.sumOf {
            PcSheetV11AttributeSkillBudget.heightPt(it).toDouble()
        }.toFloat() > 110f)
    }

    @Test
    fun denseSixAbilitySourcesKeepAllAbilitiesInBoundsAndDeferOnlySkills() {
        val counts = List(6) { 12 }
        val planned = PcSheetV11AttributeSkillBudget.plan(counts, 577f, 110f)
        assertEquals(6, planned.size)
        assertTrue(planned.all { it in 0..6 })
        assertTrue(planned.all { it > 0 })
        assertTrue(planned.sum() < 36,
            "Crowded source skills must not pull mandatory abilities off P1.")
        assertTrue(577f - planned.sumOf {
            PcSheetV11AttributeSkillBudget.heightPt(it).toDouble()
        }.toFloat() > 110f)
        assertEquals(planned, PcSheetV11AttributeSkillBudget.plan(counts, 577f, 110f))
        assertEquals(listOf(6, 6, 6, 6, 6, 6),
            PcSheetV11AttributeSkillBudget.plan(List(6) { 6 }, 700f, 110f))
    }

    @Test
    fun impossibleGeometryFailsExplicitlyInsteadOfMovingAttributesOutOfBounds() {
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11AttributeSkillBudget.plan(List(6) { 8 }, 330f, 110f)
        }
    }
}
