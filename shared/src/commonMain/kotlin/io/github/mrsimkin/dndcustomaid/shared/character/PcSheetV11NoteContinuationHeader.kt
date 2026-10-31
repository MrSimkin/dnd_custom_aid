package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * A C-N3 continuation must carry its real source identity even when a long
 * persisted note title cannot fit the fixed approved V11 page-header band.
 * Header text is navigation only; the actual note body is never abbreviated.
 * C341: the continued note names the page it comes from («VIENE DE PÁG. n»).
 */
object PcSheetV11NoteContinuationHeader {
    fun choose(
        baseTitle: String,
        sourceTitle: String?,
        stableId: String,
        fromPage: Int,
        fits: (String) -> Boolean,
    ): String {
        require(stableId.isNotBlank())
        val printableTitle = sourceTitle?.takeIf {
            it.isNotBlank() && it.none { c -> c == '\r' || c == '\n' || c == '\t' }
        }
        val from = " / " + PcSheetV11ContinuationText.comesFrom(fromPage)
        val candidates = buildList {
            if (printableTitle != null) add(baseTitle + " / " + printableTitle + from)
            add(baseTitle + " / ID: " + stableId + from)
            add("C-N3 / ID: " + stableId + from)
            add("ID: " + stableId + from)
            add("ID: " + stableId)
        }
        return candidates.firstOrNull(fits)
            ?: error("C-N3 continuation cannot display its genuine note identifier")
    }
}
