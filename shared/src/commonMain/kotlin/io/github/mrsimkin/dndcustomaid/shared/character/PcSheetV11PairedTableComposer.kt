package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

/** Isolated V11 P2/C2 content: ordinary object/quantity pairs and real treasure records. */
enum class PcSheetV11PairedTableKind { EQUIPMENT, TREASURE }

data class PcSheetV11PairedTableRow(
    val sourceKey: String,
    val label: String,
    val amount: Int,
    val sourceId: Uuid?,
)

data class PcSheetV11PairedTableData(
    val kind: PcSheetV11PairedTableKind,
    val rows: List<PcSheetV11PairedTableRow>,
) {
    init {
        require(rows.map { it.sourceKey }.distinct().size == rows.size) {
            "Repeated persisted source identities require explicit resolution."
        }
    }
}

object PcSheetV11PairedTableSources {
    fun equipment(data: PcSheetV11SemanticData): PcSheetV11PairedTableData =
        PcSheetV11PairedTableData(
            PcSheetV11PairedTableKind.EQUIPMENT,
            data.ordinaryEquipment.map { item ->
                PcSheetV11PairedTableRow(
                    "equipment:" + item.sourceId,
                    item.name,
                    item.quantity,
                    item.sourceId,
                )
            },
        )

    fun treasure(data: PcSheetV11SemanticData): PcSheetV11PairedTableData =
        PcSheetV11PairedTableData(
            PcSheetV11PairedTableKind.TREASURE,
            data.currencies.map { coin ->
                PcSheetV11PairedTableRow(
                    "currency:" + coin.key,
                    coin.name,
                    coin.amount,
                    null, // Currency identity is the original string key.
                )
            },
        )
}

/**
 * Paper geometry for source-backed EQUIPO/TESORO amounts.
 * The original P2 has a frozen 36pt amount slot; do not shrink the font.
 * Too-wide amounts move with their entire source row to C2, where the
 * pair's amount column expands ONLY when genuinely needed.
 */
object PcSheetV11PairedAmountRouting {
    const val BASE_AMOUNT_WIDTH_PT = 36f
    const val AMOUNT_GAP_PT = 6f
    const val TEXT_INSET_PT = 2f

    fun p2Eligible(
        source: PcSheetV11PairedTableData,
        areaPt: PcSheetV11RectPt,
        gutterPt: Float,
        width: PcSheetV11TextWidth,
    ): PcSheetV11PairedTableData {
        val pairWidth = (areaPt.widthPt - gutterPt) / 2f
        val nameWidth = pairWidth - BASE_AMOUNT_WIDTH_PT - AMOUNT_GAP_PT
        require(nameWidth > TEXT_INSET_PT)
        return source.copy(rows = source.rows.filter { row ->
            val labelWidth = width.measurePt(pcSheetV11PrintableText(row.label))
            val amountWidth = width.measurePt(row.amount.toString())
            require(labelWidth.isFinite() && amountWidth.isFinite() &&
                labelWidth >= 0f && amountWidth >= 0f)
            !row.label.contains('\n') && !row.label.contains('\r') &&
                labelWidth <= nameWidth - TEXT_INSET_PT &&
                amountWidth <= BASE_AMOUNT_WIDTH_PT - TEXT_INSET_PT
        })
    }

    fun c2AmountWidth(
        source: PcSheetV11PairedTableData,
        width: PcSheetV11TextWidth,
    ): Float {
        val widest = source.rows.maxOfOrNull { row ->
            width.measurePt(row.amount.toString()).also {
                require(it.isFinite() && it >= 0f)
            }
        } ?: 0f
        return maxOf(BASE_AMOUNT_WIDTH_PT, widest + 2f * TEXT_INSET_PT)
    }
}

/** Geometric cells are NOT drawings; source labels can continue, amounts may not duplicate. */
data class PcSheetV11PairedTableCell(
    val sourceKey: String,
    val localPageIndex: Int,
    val pairIndex: Int,
    val rowIndex: Int,
    val rectPt: PcSheetV11RectPt,
    val sourceText: String,
    val printableText: String,
)

data class PcSheetV11PairedTablePlaced(
    val data: PcSheetV11PairedTableData,
    val labelFlow: PcSheetV11FlowResult,
    val labels: List<PcSheetV11PairedTableCell>,
    val quantities: List<PcSheetV11PairedTableCell>,
    val pairs: List<PcSheetV11RectPt>,
) {
    val localPageCount: Int get() = labelFlow.localPageCount

    fun verify() {
        labelFlow.verifyLosslessAgainst(data.rows.map { PcSheetV11FlowItem(it.sourceKey, it.label) })
        require(pairs.size == 2)
        require(quantities.map { it.sourceKey } == data.rows.map { it.sourceKey }) {
            "Each real amount must occur once, in original record order."
        }
        val occupied = mutableSetOf<Triple<Int, Int, Int>>()
        labels.forEach { label ->
            require(pairs[label.pairIndex - 1].contains(label.rectPt))
            require(occupied.add(Triple(label.localPageIndex, label.pairIndex, label.rowIndex))) {
                "Two label lines share the same physical cell."
            }
        }
        quantities.forEach { qty ->
            require(pairs[qty.pairIndex - 1].contains(qty.rectPt))
            require(labels.any { label ->
                label.sourceKey == qty.sourceKey && label.localPageIndex == qty.localPageIndex &&
                    label.pairIndex == qty.pairIndex && label.rowIndex == qty.rowIndex
            }) { "Quantity must be aligned to its own first label line." }
        }
    }
}

/**
 * Two internal pairs, each LABEL | QUANTITY. Text wraps using measured glyph widths.
 * A long label may cross pairs/pages; its quantity is written only on the first line.
 * The caller defines the already-reserved P2/C2 area and actual font sizes; no guessed
 * visual measurements or font substitutions occur here.
 */
object PcSheetV11PairedTableComposer {
    fun place(
        data: PcSheetV11PairedTableData,
        areaPt: PcSheetV11RectPt,
        gutterPt: Float,
        quantityWidthPt: Float,
        labelQuantityGapPt: Float,
        lineHeightPt: Float,
        measuredWidth: PcSheetV11TextWidth,
        balanceSinglePageC2: Boolean = false,
    ): PcSheetV11PairedTablePlaced {
        val pairs = PcSheetV11ColumnGrid.split(areaPt, 2, gutterPt)
        require(quantityWidthPt.isFinite() && quantityWidthPt > 0f)
        require(labelQuantityGapPt.isFinite() && labelQuantityGapPt >= 0f)
        val labelWidthPt = pairs.first().widthPt - quantityWidthPt - labelQuantityGapPt
        require(labelWidthPt.isFinite() && labelWidthPt > 0f)
        require(data.rows.all { it.sourceKey.isNotBlank() && it.label.isNotBlank() }) {
            "Missing source labels require explicit handling, not fabricated display names."
        }
        data.rows.forEach { row ->
            val width = measuredWidth.measurePt(row.amount.toString())
            require(width.isFinite() && width >= 0f && width <= quantityWidthPt) {
                "The real quantity does not fit the measured quantity field."
            }
        }
        val geometry = PcSheetV11TextColumn(labelWidthPt, areaPt.heightPt, lineHeightPt)
        val originalTextItems = data.rows.map {
            PcSheetV11FlowItem(it.sourceKey, it.label, keepTogetherIfFits = true)
        }
        val naturalFlow = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.ORDINARY_EQUIPMENT, originalTextItems, 2, geometry, measuredWidth,
        )
        // The approved C2 equipment/treasure table uses BOTH label/quantity
        // pairs. On a true one-page remainder, packing all source rows in the
        // first tall pair wastes the second pair and manuscript space below.
        // Find the smallest measured line capacity that keeps that SAME page.
        // This option is strictly opt-in: frozen P2 placement is unchanged.
        // No reordering, shortening, duplicated amounts or extra pages.
        val flow = if (balanceSinglePageC2 && naturalFlow.localPageCount == 1) {
            val totalLines = naturalFlow.fragments.sumOf { it.lineRanges.size }
            val target = (totalLines + 1) / 2
            (target until geometry.lineCapacity).firstNotNullOfOrNull { capacity ->
                val trialGeometry = PcSheetV11TextColumn(
                    labelWidthPt,
                    capacity * lineHeightPt + lineHeightPt * 0.001f,
                    lineHeightPt,
                )
                val trial = PcSheetV11TextFlow.paginate(
                    PcSheetV11FlowSection.ORDINARY_EQUIPMENT,
                    originalTextItems, 2, trialGeometry, measuredWidth,
                )
                trial.takeIf { candidate ->
                    candidate.localPageCount == 1 &&
                        candidate.fragments.any { it.columnIndex == 2 }
                }?.let { trialGeometry to it }
            }?.second ?: naturalFlow
        } else naturalFlow
        val placementCapacity = if (flow === naturalFlow)
            geometry.lineCapacity else flow.fragments.maxOfOrNull { fragment ->
                fragment.startLineIndexInColumn + fragment.lineRanges.size
            }?.coerceAtLeast(1) ?: geometry.lineCapacity
        val originals = data.rows.associateBy { it.sourceKey }
        val firstCell = mutableMapOf<String, PcSheetV11PairedTableCell>()
        val labels = buildList {
            flow.fragments.forEach { fragment ->
                val pair = pairs[fragment.columnIndex - 1]
                val source = originals.getValue(fragment.sourceKey)
                fragment.lineRanges.forEachIndexed { index, range ->
                    val rowNumber = fragment.startLineIndexInColumn + index
                    require(rowNumber in 0 until placementCapacity)
                    val raw = source.label.substring(range.startOffset, range.endExclusive)
                    val cell = PcSheetV11PairedTableCell(
                        sourceKey = source.sourceKey,
                        localPageIndex = fragment.localPageIndex,
                        pairIndex = fragment.columnIndex,
                        rowIndex = rowNumber,
                        rectPt = PcSheetV11RectPt(
                            pair.leftPt,
                            pair.topPt - (rowNumber + 1) * lineHeightPt,
                            labelWidthPt,
                            lineHeightPt,
                        ),
                        sourceText = raw,
                        printableText = raw.trimEnd('\r', '\n'),
                    )
                    require(pair.contains(cell.rectPt))
                    if (source.sourceKey !in firstCell) firstCell[source.sourceKey] = cell
                    add(cell)
                }
            }
        }
        val quantities = data.rows.map { source ->
            val first = requireNotNull(firstCell[source.sourceKey]) {
                "Missing first label line for " + source.sourceKey
            }
            val pair = pairs[first.pairIndex - 1]
            val amount = source.amount.toString()
            PcSheetV11PairedTableCell(
                sourceKey = source.sourceKey,
                localPageIndex = first.localPageIndex,
                pairIndex = first.pairIndex,
                rowIndex = first.rowIndex,
                rectPt = PcSheetV11RectPt(
                    pair.rightPt - quantityWidthPt,
                    first.rectPt.bottomPt,
                    quantityWidthPt,
                    lineHeightPt,
                ),
                sourceText = amount,
                printableText = amount,
            )
        }
        return PcSheetV11PairedTablePlaced(data, flow, labels, quantities, pairs)
            .also { it.verify() }
    }
}
