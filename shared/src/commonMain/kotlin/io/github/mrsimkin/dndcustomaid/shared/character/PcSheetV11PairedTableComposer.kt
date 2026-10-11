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
        val flow = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.ORDINARY_EQUIPMENT, originalTextItems, 2, geometry, measuredWidth,
        )
        val originals = data.rows.associateBy { it.sourceKey }
        val firstCell = mutableMapOf<String, PcSheetV11PairedTableCell>()
        val labels = buildList {
            flow.fragments.forEach { fragment ->
                val pair = pairs[fragment.columnIndex - 1]
                val source = originals.getValue(fragment.sourceKey)
                fragment.lineRanges.forEachIndexed { index, range ->
                    val rowNumber = fragment.startLineIndexInColumn + index
                    require(rowNumber in 0 until geometry.lineCapacity)
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
