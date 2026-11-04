package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PcSheetV11QuickSpellShareTest {
    @Test
    fun anEmptyListGetsTheGoldenRowsOfOnePage() {
        val paper = PcSheetV11QuickSpellShare.paperRows { 0 }
        assertEquals(listOf(16, 17, 17, 16, 8, 4, 4, 4, 4, 4), (0..9).map { paper.getValue(it) })
    }

    @Test
    fun realSpellsCountInsideTheShareAndEveryLevelKeepsAWritingRow() {
        val real = mapOf(0 to 3, 1 to 20, 5 to 4, 9 to 9)
        val paper = PcSheetV11QuickSpellShare.paperRows { real[it] ?: 0 }
        assertEquals(13, paper.getValue(0), "Trucos: 16 rows with 3 real spells.")
        assertEquals(1, paper.getValue(1), "More real spells than the share still leave one row.")
        assertEquals(1, paper.getValue(5))
        assertEquals(1, paper.getValue(9))
        assertTrue(paper.values.all { it >= 1 })
    }

    @Test
    fun rowsAreTakenFromTheLevelWithTheMostPaperAndTheHighestOnATie() {
        val first = PcSheetV11QuickSpellShare.paperRows { 0 }
        val second = PcSheetV11QuickSpellShare.takeOneRow(first)
        assertEquals(16, second.getValue(2), "Level 2 ties level 1 at 17 and is the higher.")
        assertEquals(17, second.getValue(1))
        var paper = first
        repeat(first.values.sumOf { it - 1 }) { paper = PcSheetV11QuickSpellShare.takeOneRow(paper) }
        assertTrue(paper.values.all { it == 1 }, "It ends at one row per level.")
        assertFailsWith<IllegalStateException> { PcSheetV11QuickSpellShare.takeOneRow(paper) }
    }
}
