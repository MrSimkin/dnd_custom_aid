package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.math.min

/**
 * G2 foundation: measured, lossless line flow shared by Android and Desktop.
 * A real PDF font measurer and final physical-page compositor are NOT implemented here.
 * Geometry is supplied by the caller; no synthetic widths or guessed page positions.
 */
data class PcSheetV11TextColumn(
    val widthPt: Float,
    val heightPt: Float,
    val lineHeightPt: Float,
) {
    init {
        require(widthPt.isFinite() && widthPt > 0f) { "A usable column width is required." }
        require(heightPt.isFinite() && heightPt > 0f) { "A usable column height is required." }
        require(lineHeightPt.isFinite() && lineHeightPt > 0f) { "A usable line height is required." }
        require(lineCapacity > 0) { "A column must fit at least one complete line." }
    }

    val lineCapacity: Int
        get() = (heightPt / lineHeightPt).toInt()
}

/** Measured width in points with the exact font that the future PDF backend uses. */
fun interface PcSheetV11TextWidth {
    fun measurePt(text: String): Float
}

/** Caller must supply a stable, unique source key; never infer one from display text. */
data class PcSheetV11FlowItem(
    val sourceKey: String,
    val text: String,
    val keepTogetherIfFits: Boolean = false,
)

enum class PcSheetV11FlowSection {
    NARRATIVE,
    DIGITAL_NOTES,
    TRAITS,
    ORDINARY_EQUIPMENT,
    SPECIAL_EQUIPMENT,
    QUICK_SPELLS,
    SPELLBOOK,
}

data class PcSheetV11TextLine(
    val startOffset: Int,
    val endExclusive: Int,
) {
    init {
        require(startOffset >= 0 && endExclusive > startOffset)
    }
}

data class PcSheetV11FlowFragment(
    val sourceKey: String,
    /** Index within this flow section only, not the physical PDF page number. */
    val localPageIndex: Int,
    val columnIndex: Int,
    /** 0-based starting line cell within this physical column; prevents consecutive blocks overlapping. */
    val startLineIndexInColumn: Int,
    /** Exact UTF-16 indices into the source item; bounds also cover explicit newlines. */
    val startOffset: Int,
    val endExclusive: Int,
    val text: String,
    val lineRanges: List<PcSheetV11TextLine>,
    val continuedFromPrevious: Boolean,
    val continuesOnNext: Boolean,
)

data class PcSheetV11FlowResult(
    val section: PcSheetV11FlowSection,
    val columnCount: Int,
    val fragments: List<PcSheetV11FlowFragment>,
) {
    val localPageCount: Int get() = fragments.lastOrNull()?.localPageIndex ?: 0

    /** Assert no source bytes were skipped, repeated, normalized, or silently reordered. */
    fun verifyLosslessAgainst(items: List<PcSheetV11FlowItem>) {
        val expected = items.filter { it.text.isNotEmpty() }
        require(fragments.map { it.sourceKey }.distinct().all { key ->
            expected.any { it.sourceKey == key }
        }) { "Unrecognized source key in V11 text flow." }
        expected.forEach { item ->
            val parts = fragments.filter { it.sourceKey == item.sourceKey }
            require(parts.isNotEmpty()) { "Missing V11 flow item: ${item.sourceKey}" }
            require(parts.first().startOffset == 0 && parts.last().endExclusive == item.text.length) {
                "Missing text boundary for ${item.sourceKey}"
            }
            parts.zipWithNext().forEach { (a, b) ->
                require(a.endExclusive == b.startOffset) {
                    "Text gap or duplication in ${item.sourceKey}"
                }
            }
            require(parts.joinToString("") { it.text } == item.text) {
                "Text changed during V11 flow: ${item.sourceKey}"
            }
            parts.forEachIndexed { index, part ->
                require(part.continuedFromPrevious == (index > 0))
                require(part.continuesOnNext == (index < parts.lastIndex))
                require(part.startLineIndexInColumn >= 0) { "Negative column line offset" }
                require(part.lineRanges.first().startOffset == part.startOffset)
                require(part.lineRanges.last().endExclusive == part.endExclusive)
            }
        }
    }
}

/**
 * Deterministic line/column partitioner. No fallback to undersized type, ellipsis, truncation
 * or fake PDF coordinates. A glyph that cannot fit at the requested font/width fails explicitly.
 */
object PcSheetV11TextFlow {
    fun paginate(
        section: PcSheetV11FlowSection,
        items: List<PcSheetV11FlowItem>,
        columnsPerPage: Int,
        column: PcSheetV11TextColumn,
        textWidth: PcSheetV11TextWidth,
    ): PcSheetV11FlowResult {
        require(columnsPerPage in 1..3) { "V11 supports one, two or three-column text flows." }
        require(items.none { it.sourceKey.isBlank() }) { "Every V11 text item needs a source key." }
        require(items.map { it.sourceKey }.distinct().size == items.size) {
            "V11 source keys cannot repeat within a flow."
        }

        var page = 1
        var col = 1
        var remaining = column.lineCapacity
        fun nextColumn() {
            col += 1
            if (col > columnsPerPage) {
                page += 1
                col = 1
            }
            remaining = column.lineCapacity
        }

        val fragments = mutableListOf<PcSheetV11FlowFragment>()
        items.forEach { item ->
            val lines = splitIntoMeasuredLines(item.text, column.widthPt, textWidth)
            if (lines.isEmpty()) return@forEach
            if (item.keepTogetherIfFits && lines.size <= column.lineCapacity &&
                lines.size > remaining
            ) {
                nextColumn()
            }
            var offset = 0
            while (offset < lines.size) {
                if (remaining == 0) nextColumn()
                val taken = min(remaining, lines.size - offset)
                check(taken > 0) { "V11 text flow made no progress." }
                val slice = lines.subList(offset, offset + taken)
                val start = slice.first().startOffset
                val end = slice.last().endExclusive
                fragments += PcSheetV11FlowFragment(
                    sourceKey = item.sourceKey,
                    localPageIndex = page,
                    columnIndex = col,
                    startLineIndexInColumn = column.lineCapacity - remaining,
                    startOffset = start,
                    endExclusive = end,
                    text = item.text.substring(start, end),
                    lineRanges = slice.toList(),
                    continuedFromPrevious = offset > 0,
                    continuesOnNext = offset + taken < lines.size,
                )
                remaining -= taken
                offset += taken
            }
        }
        return PcSheetV11FlowResult(section, columnsPerPage, fragments).also {
            it.verifyLosslessAgainst(items)
        }
    }

    /**
     * Source offsets include soft-wrap whitespace and explicit CR/LF; the renderer may choose
     * a line-break drawing operation, but must not change the text provenance.
     * Surrogate pairs are never split. Grapheme shaping and real font measuring remain G2/G3 work.
     */
    private fun splitIntoMeasuredLines(
        text: String,
        widthPt: Float,
        width: PcSheetV11TextWidth,
    ): List<PcSheetV11TextLine> {
        if (text.isEmpty()) return emptyList()
        val lines = mutableListOf<PcSheetV11TextLine>()
        var start = 0
        while (start < text.length) {
            var cursor = start
            var lastSpaceBoundary = -1
            var end = -1
            while (cursor < text.length) {
                val ch = text[cursor]
                if (ch == '\n' || ch == '\r') {
                    end = if (ch == '\r' && cursor + 1 < text.length &&
                        text[cursor + 1] == '\n'
                    ) cursor + 2 else cursor + 1
                    break
                }
                val next = if (ch.isHighSurrogate() && cursor + 1 < text.length &&
                    text[cursor + 1].isLowSurrogate()
                ) cursor + 2 else cursor + 1
                val measured = width.measurePt(text.substring(start, next))
                require(measured.isFinite() && measured >= 0f) {
                    "V11 text measurer returned an invalid width."
                }
                if (measured > widthPt) {
                    require(cursor > start) {
                        "V11 glyph cannot fit the chosen font and column width."
                    }
                    end = if (lastSpaceBoundary > start) lastSpaceBoundary else cursor
                    break
                }
                if (ch == ' ' || ch == '\t') lastSpaceBoundary = next
                cursor = next
            }
            if (end < 0) end = text.length
            check(end > start && end <= text.length)
            lines += PcSheetV11TextLine(start, end)
            start = end
        }
        return lines
    }
}
