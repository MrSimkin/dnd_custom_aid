package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Preserve the owner's one-line V11 logo/name header without overprinting its
 * fixed physical edge. The source name is never truncated in the document:
 * an unusually long one is printed in full once in the measured P1-owned C2.
 * A short source-backed prefix remains beside the logo on every page.
 */
data class PcSheetV11HeaderNameRoute(
    val paperName: String,
    val continuation: PcSheetV11FlowItem?,
)

object PcSheetV11HeaderNameRouter {
    private const val NAVIGATION_SUFFIX = " / VER C2"

    fun route(
        sourceName: String,
        availableWidthPt: Float,
        width: PcSheetV11TextWidth,
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
        require(width.measurePt(NAVIGATION_SUFFIX) <= availableWidthPt) {
            "The approved V11 physical header cannot fit even the C2 cue."
        }

        var longestPrefixEnd = 0
        for (end in 1..printable.length) {
            // All text width decisions use the actual embedded Barlow font.
            val candidate = printable.substring(0, end).trimEnd() + NAVIGATION_SUFFIX
            if (width.measurePt(candidate) > availableWidthPt) break
            longestPrefixEnd = end
        }
        val prefix = printable.substring(0, longestPrefixEnd).trimEnd()
        val paperName = prefix + NAVIGATION_SUFFIX
        require(width.measurePt(paperName) <= availableWidthPt)
        return PcSheetV11HeaderNameRoute(
            paperName = paperName,
            continuation = PcSheetV11FlowItem(
                sourceKey = "p1:character-full-name",
                text = "NOMBRE COMPLETO DEL PERSONAJE: " + sourceName,
                keepTogetherIfFits = true,
            ),
        )
    }
}
