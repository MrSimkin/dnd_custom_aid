package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * The approved golden share of one P4 page: trucos 16 rows, levels 1..3 17/17/16, level 4 8
 * and levels 5..9 4 each, real spells included. Low levels get far more writing room.
 */
object PcSheetV11QuickSpellShare {
    private val goldenRows = listOf(16, 17, 17, 16, 8, 4, 4, 4, 4, 4)

    /** Blank paper rows per level 0..9; every level keeps at least one, however many spells it has. */
    fun paperRows(realRows: (Int) -> Int): Map<Int, Int> =
        (0..9).associateWith { level -> maxOf(1, goldenRows[level] - realRows(level)) }

    /** Real spells can exceed the share: one row less for the level with the most paper, highest on a tie. */
    fun takeOneRow(paper: Map<Int, Int>): Map<Int, Int> {
        val level = paper.entries.filter { it.value > 1 }
            .maxWithOrNull(compareBy({ it.value }, { it.key }))?.key
            ?: error("No P4 level has a blank row left to give.")
        return paper + (level to paper.getValue(level) - 1)
    }
}
