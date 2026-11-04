package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * The approved golden share of one P4 page: trucos 16 rows, levels 1..3 17/17/16, level 4 8
 * and levels 5..9 4 each, real spells included. Low levels get far more writing room.
 */
object PcSheetV11QuickSpellShare {
    private val goldenRows = listOf(16, 17, 17, 16, 8, 4, 4, 4, 4, 4)

    /** The golden columns: levels that fill one column together, so none is split without need. */
    private val goldenColumns = listOf(0..1, 2..3, 4..9)

    /**
     * Blank paper rows per level 0..9; every level keeps at least one, however many spells it has.
     * Real spells beyond a level's share take blank rows from the other levels of its own column
     * first, so the other columns and the level starts stay where the golden has them.
     */
    fun paperRows(realRows: (Int) -> Int): Map<Int, Int> {
        val paper = (0..9).associateWith { level -> maxOf(1, goldenRows[level] - realRows(level)) }.toMutableMap()
        goldenColumns.forEach { column ->
            val target = column.sumOf { goldenRows[it] }
            var excess = column.sumOf { realRows(it) + paper.getValue(it) } - target
            while (excess > 0) {
                val level = column.filter { paper.getValue(it) > 1 }
                    .maxWithOrNull(compareBy({ paper.getValue(it) }, { it })) ?: break
                paper[level] = paper.getValue(level) - 1
                excess--
            }
        }
        return paper
    }

    /** Real spells can exceed the share: one row less for the level with the most paper, highest on a tie. */
    fun takeOneRow(paper: Map<Int, Int>): Map<Int, Int> {
        val level = paper.entries.filter { it.value > 1 }
            .maxWithOrNull(compareBy({ it.value }, { it.key }))?.key
            ?: error("No P4 level has a blank row left to give.")
        return paper + (level to paper.getValue(level) - 1)
    }
}
