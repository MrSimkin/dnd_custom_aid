package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * V11 G2 geometric contract in PDF points, bottom-left origin.
 * Coordinates describe exact available cells, not glyph baselines or typography.
 * The official V11 visual measurements must be supplied by the eventual compositor.
 */
data class PcSheetV11RectPt(
    val leftPt: Float,
    val bottomPt: Float,
    val widthPt: Float,
    val heightPt: Float,
) {
    init {
        require(leftPt.isFinite() && leftPt >= 0f)
        require(bottomPt.isFinite() && bottomPt >= 0f)
        require(widthPt.isFinite() && widthPt > 0f)
        require(heightPt.isFinite() && heightPt > 0f)
        require(rightPt.isFinite() && topPt.isFinite())
    }

    val rightPt: Float get() = leftPt + widthPt
    val topPt: Float get() = bottomPt + heightPt

    fun contains(other: PcSheetV11RectPt, tolerancePt: Float = 0.001f): Boolean =
        other.leftPt >= leftPt - tolerancePt &&
            other.bottomPt >= bottomPt - tolerancePt &&
            other.rightPt <= rightPt + tolerancePt &&
            other.topPt <= topPt + tolerancePt
}

object PcSheetV11Paper {
    const val LETTER_WIDTH_PT: Float = 612f
    const val LETTER_HEIGHT_PT: Float = 792f

    val letterBounds: PcSheetV11RectPt =
        PcSheetV11RectPt(0f, 0f, LETTER_WIDTH_PT, LETTER_HEIGHT_PT)
}

/** One precise shared geometric partition for two-column C2 or three-column spell lists. */
object PcSheetV11ColumnGrid {
    fun split(
        area: PcSheetV11RectPt,
        columns: Int,
        gutterPt: Float,
    ): List<PcSheetV11RectPt> {
        require(columns in 1..3) { "The V11 geometry supports 1–3 columns." }
        require(gutterPt.isFinite() && gutterPt >= 0f) { "Gutter must be nonnegative and finite." }
        require(PcSheetV11Paper.letterBounds.contains(area)) {
            "V11 content must remain within US Letter page bounds."
        }
        val totalGutter = gutterPt * (columns - 1)
        val columnWidth = (area.widthPt - totalGutter) / columns
        require(columnWidth.isFinite() && columnWidth > 0f) {
            "Columns and gutters must leave positive content width."
        }
        return List(columns) { index ->
            PcSheetV11RectPt(
                leftPt = area.leftPt + index * (columnWidth + gutterPt),
                bottomPt = area.bottomPt,
                widthPt = columnWidth,
                heightPt = area.heightPt,
            ).also { require(area.contains(it)) }
        }
    }
}

/**
 * One whole line cell, with its original source offsets. This is a reservation
 * for a future PDF backend: the actual font metric decides its baseline.
 */
data class PcSheetV11PlacedLineCell(
    val section: PcSheetV11FlowSection,
    val sourceKey: String,
    /** 1-based LOCAL page number within the measured section, not global PDF numbering. */
    val localPageIndex: Int,
    val columnIndex: Int,
    /** 0-based row index, shared by every item occupying a given page + column. */
    val rowIndex: Int,
    val sourceStartOffset: Int,
    val sourceEndExclusive: Int,
    val sourceText: String,
    val cellPt: PcSheetV11RectPt,
) {
    /** Explicit paragraph delimiters are metadata; never pass CR/LF to a PDF showText call. */
    val drawableText: String
        get() = sourceText.trimEnd('\r', '\n')
}

data class PcSheetV11PlacedFlow(
    val sourceFlow: PcSheetV11FlowResult,
    val columnsPt: List<PcSheetV11RectPt>,
    val lineHeightPt: Float,
    val lineCapacity: Int,
    val cells: List<PcSheetV11PlacedLineCell>,
) {
    fun verifyGeometry() {
        require(columnsPt.size == sourceFlow.columnCount)
        val fragments = sourceFlow.fragments
        require(cells.size == fragments.sumOf { it.lineRanges.size })
        val occupied = mutableSetOf<Triple<Int, Int, Int>>()
        cells.forEach { cell ->
            require(cell.localPageIndex in 1..sourceFlow.localPageCount)
            require(cell.columnIndex in columnsPt.indices.map { it + 1 })
            require(cell.rowIndex in 0 until lineCapacity)
            require(columnsPt[cell.columnIndex - 1].contains(cell.cellPt)) {
                "V11 text cell crosses its assigned column."
            }
            require(PcSheetV11Paper.letterBounds.contains(cell.cellPt)) {
                "V11 text cell crosses the letter page."
            }
            require(occupied.add(Triple(cell.localPageIndex, cell.columnIndex, cell.rowIndex))) {
                "Two different V11 text lines occupy the same physical cell."
            }
        }
    }
}

/**
 * From measured text + caller-supplied actual content rectangle to unambiguous
 * PDF-point cell reservations. It cannot draw, trim or substitute glyphs.
 */
object PcSheetV11MeasuredFlowPlacer {
    fun place(
        section: PcSheetV11FlowSection,
        items: List<PcSheetV11FlowItem>,
        areaPt: PcSheetV11RectPt,
        columnsPerPage: Int,
        gutterPt: Float,
        lineHeightPt: Float,
        textWidth: PcSheetV11TextWidth,
    ): PcSheetV11PlacedFlow {
        val columns = PcSheetV11ColumnGrid.split(areaPt, columnsPerPage, gutterPt)
        val geometry = PcSheetV11TextColumn(
            widthPt = columns.first().widthPt,
            heightPt = areaPt.heightPt,
            lineHeightPt = lineHeightPt,
        )
        val flow = PcSheetV11TextFlow.paginate(
            section = section,
            items = items,
            columnsPerPage = columnsPerPage,
            column = geometry,
            textWidth = textWidth,
        )
        val sourcesByKey = items.associate { it.sourceKey to it.text }
        val cells = buildList {
            flow.fragments.forEach { fragment ->
                val column = columns[fragment.columnIndex - 1]
                fragment.lineRanges.forEachIndexed { localRow, range ->
                    val row = fragment.startLineIndexInColumn + localRow
                    require(row in 0 until geometry.lineCapacity) {
                        "V11 page overflow while placing a text fragment."
                    }
                    add(
                        PcSheetV11PlacedLineCell(
                            section = section,
                            sourceKey = fragment.sourceKey,
                            localPageIndex = fragment.localPageIndex,
                            columnIndex = fragment.columnIndex,
                            rowIndex = row,
                            sourceStartOffset = range.startOffset,
                            sourceEndExclusive = range.endExclusive,
                            sourceText = sourcesByKey.getValue(fragment.sourceKey)
                                .substring(range.startOffset, range.endExclusive),
                            cellPt = PcSheetV11RectPt(
                                leftPt = column.leftPt,
                                bottomPt = column.topPt - (row + 1) * geometry.lineHeightPt,
                                widthPt = column.widthPt,
                                heightPt = geometry.lineHeightPt,
                            ),
                        ),
                    )
                }
            }
        }
        return PcSheetV11PlacedFlow(
            sourceFlow = flow,
            columnsPt = columns,
            lineHeightPt = lineHeightPt,
            lineCapacity = geometry.lineCapacity,
            cells = cells,
        ).also { it.verifyGeometry() }
    }
}
