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
        assertEquals(9, paper.getValue(0), "Trucos give up rows so that column 1 keeps its golden 33.")
        assertEquals(1, paper.getValue(1), "More real spells than the share still leave one row.")
        assertEquals(1, paper.getValue(5))
        assertEquals(1, paper.getValue(9))
        assertTrue(paper.values.all { it >= 1 })
    }

    @Test
    fun extraRealSpellsTakeRowsFromTheirOwnColumnSoTheOthersKeepTheirShape() {
        // Four real spells at level 5 fill its share: its one paper row comes out of column 3.
        val paper = PcSheetV11QuickSpellShare.paperRows { if (it == 5) 4 else 0 }
        assertEquals(listOf(16, 17), listOf(paper.getValue(0), paper.getValue(1)), "Column 1 is untouched.")
        assertEquals(listOf(17, 16), listOf(paper.getValue(2), paper.getValue(3)), "Column 2 is untouched.")
        assertEquals(1, paper.getValue(5))
        assertEquals(7, paper.getValue(4), "The level with the most paper in the column gives the row.")
        assertEquals(28, (4..9).sumOf { paper.getValue(it) + (if (it == 5) 4 else 0) }, "Column 3 keeps its 28 rows.")
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
