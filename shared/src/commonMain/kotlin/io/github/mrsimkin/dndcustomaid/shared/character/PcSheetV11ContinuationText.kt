package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * C341 (owner 2026-10-30): every printed reference between physical pages says
 * «CONTINÚA EN PÁG. X» where content leaves a page and «VIENE DE PÁG. Y» where
 * it arrives. One grammar for footers, module bands, cartelas and headers, so
 * the two renderers cannot drift apart. Page numbers are the bare physical
 * numbers printed in the footer slot (C340).
 */
object PcSheetV11ContinuationText {
    fun continuesOn(page: Int): String {
        require(page > 0)
        return "CONTINÚA EN PÁG. $page"
    }

    fun comesFrom(page: Int): String = requireNotNull(comesFrom(listOf(page)))

    fun comesFrom(pages: Collection<Int>): String? {
        val sorted = pages.distinct().sorted()
        require(sorted.all { it > 0 })
        return when (sorted.size) {
            0 -> null
            1 -> "VIENE DE PÁG. " + sorted.single()
            else -> "VIENE DE PÁG. " + sorted.dropLast(1).joinToString(", ") + " Y " + sorted.last()
        }
    }

    /** Module names grouped by their first real destination page, in source order. */
    fun moduleLinks(destinations: List<Pair<String, Int>>): String? =
        destinations.groupBy({ it.second }, { it.first }).entries.joinToString("   ·   ") { (page, modules) ->
            val names = modules.distinct()
            names.joinToString(", ") +
                (if (names.size == 1) " CONTINÚA EN PÁG. " else " CONTINÚAN EN PÁG. ") + page
        }.takeIf { it.isNotEmpty() }

    /**
     * Caption at the top of a column that continues an earlier source: the real
     * identity plus where it comes from, or «CONTINUACIÓN» when it comes from
     * the preceding column of the same page.
     */
    fun arrived(identity: String, fromPage: Int?): String =
        identity + " / " + (fromPage?.let { comesFrom(it) } ?: "CONTINUACIÓN")
}
