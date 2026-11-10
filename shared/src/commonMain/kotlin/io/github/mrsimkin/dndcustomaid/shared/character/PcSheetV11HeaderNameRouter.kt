package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Preserve the owner's one-line V11 logo/name header without overprinting its
 * fixed physical edge. C463 (owner 2026-11-07, «solo quiero el nombre en la cabecera»):
 * a long name is cut where it stops fitting and is printed nowhere else.
 */
data class PcSheetV11HeaderNameRoute(
    val paperName: String,
    val continuation: PcSheetV11FlowItem?,
)

object PcSheetV11HeaderNameRouter {
    fun route(
        sourceName: String,
        availableWidthPt: Float,
        width: PcSheetV11TextWidth,
        routedWidthPt: Float = availableWidthPt,
    ): PcSheetV11HeaderNameRoute {
        require(availableWidthPt.isFinite() && availableWidthPt > 0f)
        // The header is physically one line: do not send persisted CR/LF
        // to the embedded PDF font measurer. Preserve the source in C2.
        val hasHardBreak = sourceName.any { it == '\r' || it == '\n' }
        val printable = pcSheetV11PrintableText(sourceName)
            .replace("\r\n", " ").replace('\r', ' ').replace('\n', ' ')
        val completeWidth = width.measurePt(printable)
        require(completeWidth.isFinite() && completeWidth >= 0f)
        if (completeWidth <= availableWidthPt && !hasHardBreak) {
            return PcSheetV11HeaderNameRoute(printable, null)
        }
        var longestPrefixEnd = 0
        for (end in 1..printable.length) {
            // All text width decisions use the actual embedded Barlow font.
            val candidate = printable.substring(0, end).trimEnd()
            if (width.measurePt(candidate) > routedWidthPt) break
            longestPrefixEnd = end
        }
        val prefix = printable.substring(0, longestPrefixEnd).trimEnd()
        // Owner 2026-11-06, «cortado hasta donde sea que quepa»: the name alone, no notice.
        val paperName = prefix
        require(width.measurePt(paperName) <= routedWidthPt)
        return PcSheetV11HeaderNameRoute(paperName = paperName, continuation = null)
    }
}
