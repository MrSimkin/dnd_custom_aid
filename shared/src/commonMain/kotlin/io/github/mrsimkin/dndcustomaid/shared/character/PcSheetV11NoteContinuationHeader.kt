package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * A C-N3 continuation must carry its real source identity even when a long
 * persisted note title cannot fit the fixed approved V11 page-header band.
 * Header text is navigation only; the actual note body is never abbreviated.
 */
object PcSheetV11NoteContinuationHeader {
    fun choose(
        baseTitle: String,
        sourceTitle: String?,
        stableId: String,
        fits: (String) -> Boolean,
    ): String {
        require(stableId.isNotBlank())
        val printableTitle = sourceTitle?.takeIf {
            it.isNotBlank() && it.none { c -> c == '\r' || c == '\n' || c == '\t' }
        }
        val candidates = buildList {
            if (printableTitle != null) add("$" + "{baseTitle} / CONTINÚA: $" + "{printableTitle}")
            add("$" + "{baseTitle} / CONTINÚA ID: $" + "{stableId}")
            add("C-N3 / CONTINÚA ID: $" + "{stableId}")
            add("CONTINÚA ID: $" + "{stableId}")
        }
        return candidates.firstOrNull(fits)
            ?: error("C-N3 continuation cannot display its genuine note identifier")
    }
}
