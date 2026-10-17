package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Variant A must retain all six base attributes when many persisted skills
 * refer to them. Extra skills continue in measured C2 rather than failing P1.
 * For ordinary skill counts the physical paper allocation is unchanged.
 */
object PcSheetV11AttributeSkillBudget {
    const val MAX_ROWS_PER_ATTRIBUTE = 6
    const val MIN_ATTRIBUTE_HEIGHT_PT = 38f
    const val SKILL_LINE_HEIGHT_PT = 10.5f
    const val HEADER_AND_FIRST_ROW_PT = 29f

    fun heightPt(visibleRows: Int): Float {
        require(visibleRows in 0..MAX_ROWS_PER_ATTRIBUTE)
        return maxOf(MIN_ATTRIBUTE_HEIGHT_PT,
            HEADER_AND_FIRST_ROW_PT + visibleRows * SKILL_LINE_HEIGHT_PT)
    }

    /**
     * Counts are ordered by the six mandatory source attributes.
     * Reduce the most crowded ability first; later-attribute ties break
     * deterministically. Full source IDs and unprinted skills still flow C2.
     */
    fun plan(counts: List<Int>, areaTopPt: Float, safeBottomPt: Float): List<Int> {
        require(counts.size == 6 && counts.all { it >= 0 })
        require(areaTopPt.isFinite() && safeBottomPt.isFinite())
        require(areaTopPt - safeBottomPt > 6f * MIN_ATTRIBUTE_HEIGHT_PT + 1f)
        val rows = counts.map { it.coerceAtMost(MAX_ROWS_PER_ATTRIBUTE) }.toMutableList()
        val available = areaTopPt - safeBottomPt - 1f
        fun total(): Float = rows.sumOf { heightPt(it).toDouble() }.toFloat()
        while (total() > available) {
            val crowded = rows.indices.filter { rows[it] > 0 }
                .maxWithOrNull(compareBy<Int> { rows[it] }.thenBy { it })
                ?: error("Six mandatory attributes cannot fit at their minimum height.")
            rows[crowded] -= 1
        }
        check(total() <= available)
        return rows
    }
}
