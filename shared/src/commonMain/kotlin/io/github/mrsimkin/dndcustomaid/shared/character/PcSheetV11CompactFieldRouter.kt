package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * An oversized source value must never abort P1 or clip within a fixed paper cell.
 * Keep the unchanged value in a measured C2 entry with a stable source identity.
 * The short P1 marker is navigation, not an invented character statistic.
 */
data class PcSheetV11CompactFieldRoute(
    val paperValue: String,
    val continuation: PcSheetV11FlowItem?,
)

object PcSheetV11CompactFieldRouter {
    const val CONTINUATION_MARKER: String = "VER C2"

    fun route(
        sourceKey: String,
        label: String,
        sourceValue: String,
        availableWidthPt: Float,
        width: PcSheetV11TextWidth,
    ): PcSheetV11CompactFieldRoute {
        require(sourceKey.isNotBlank() && label.isNotBlank())
        require(availableWidthPt.isFinite() && availableWidthPt > 0f)
        val valueWidth = width.measurePt(pcSheetV11PrintableText(sourceValue))
        require(valueWidth.isFinite() && valueWidth >= 0f)
        if (sourceValue.isEmpty() || valueWidth <= availableWidthPt) {
            return PcSheetV11CompactFieldRoute(sourceValue, null)
        }
        require(width.measurePt(CONTINUATION_MARKER) <= availableWidthPt) {
            "Cannot fit the V11 paper continuation marker."
        }
        return PcSheetV11CompactFieldRoute(
            CONTINUATION_MARKER,
            PcSheetV11FlowItem(
                sourceKey = sourceKey,
                text = "$label: $sourceValue",
                keepTogetherIfFits = true,
            ),
        )
    }
}
