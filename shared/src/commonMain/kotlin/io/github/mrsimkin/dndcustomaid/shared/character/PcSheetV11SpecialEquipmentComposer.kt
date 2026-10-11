package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

/**
 * Full-width V11 Equipo Especial is an independent module, never the paired ordinary
 * equipment table. ACTIVO comes only from a documented source, not equipped/attuned.
 */
enum class PcSheetV11SpecialField { NAME, LOCATION, DESCRIPTION, NOTES }

data class PcSheetV11SpecialFieldSource(
    val sourceId: Uuid,
    val field: PcSheetV11SpecialField,
    val text: String,
) {
    val sourceKey: String get() = "special:" + sourceId + ":" + field.name.lowercase()
}

data class PcSheetV11SpecialActiveMark(
    val sourceId: Uuid,
    val active: Boolean?,
    val rectPt: PcSheetV11RectPt,
    val localPageIndex: Int,
)

data class PcSheetV11SpecialEquipmentPlaced(
    val sources: List<PcSheetV11SpecialFieldSource>,
    val body: PcSheetV11PlacedFlow,
    val activeMarks: List<PcSheetV11SpecialActiveMark>,
    val inputIds: List<Uuid>,
    val areaPt: PcSheetV11RectPt,
) {
    val localPageCount: Int get() = body.sourceFlow.localPageCount

    fun verify() {
        body.verifyGeometry()
        body.sourceFlow.verifyLosslessAgainst(sources.map {
            PcSheetV11FlowItem(it.sourceKey, it.text)
        })
        require(activeMarks.map { it.sourceId } == inputIds)
        require(activeMarks.distinctBy { it.sourceId }.size == activeMarks.size)
        require(body.columnsPt.size == 1) { "Special equipment never uses two narrow columns." }
        activeMarks.forEach { mark ->
            require(areaPt.contains(mark.rectPt))
            require(mark.rectPt.leftPt >= body.columnsPt.single().rightPt)
            require(mark.localPageIndex in 1..localPageCount)
        }
    }
}

object PcSheetV11SpecialEquipmentComposer {
    fun place(
        equipment: List<PcSheetV11Equipment>,
        areaPt: PcSheetV11RectPt,
        activeMarkWidthPt: Float,
        activeMarkGapPt: Float,
        lineHeightPt: Float,
        measuredWidth: PcSheetV11TextWidth,
    ): PcSheetV11SpecialEquipmentPlaced {
        require(activeMarkWidthPt.isFinite() && activeMarkWidthPt > 0f)
        require(activeMarkGapPt.isFinite() && activeMarkGapPt >= 0f)
        require(equipment.all { it.special }) {
            "Ordinary equipment must use the two-paired-column table."
        }
        require(equipment.distinctBy { it.sourceId }.size == equipment.size) {
            "Duplicate special equipment IDs are not silently merged."
        }
        require(equipment.all { it.name.isNotBlank() })
        val bodyWidth = areaPt.widthPt - activeMarkWidthPt - activeMarkGapPt
        require(bodyWidth.isFinite() && bodyWidth > 0f)
        val bodyArea = PcSheetV11RectPt(
            areaPt.leftPt, areaPt.bottomPt, bodyWidth, areaPt.heightPt,
        )
        val fields = equipment.flatMap { item ->
            buildList {
                add(PcSheetV11SpecialFieldSource(item.sourceId, PcSheetV11SpecialField.NAME, item.name))
                item.location?.takeIf { it.isNotBlank() }?.let {
                    add(PcSheetV11SpecialFieldSource(item.sourceId, PcSheetV11SpecialField.LOCATION, it))
                }
                item.description?.takeIf { it.isNotBlank() }?.let {
                    add(PcSheetV11SpecialFieldSource(item.sourceId, PcSheetV11SpecialField.DESCRIPTION, it))
                }
                item.notes?.takeIf { it.isNotBlank() }?.let {
                    add(PcSheetV11SpecialFieldSource(item.sourceId, PcSheetV11SpecialField.NOTES, it))
                }
            }
        }
        val placed = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.SPECIAL_EQUIPMENT,
            fields.map { PcSheetV11FlowItem(it.sourceKey, it.text, keepTogetherIfFits = true) },
            bodyArea, columnsPerPage = 1, gutterPt = 0f,
            lineHeightPt = lineHeightPt, textWidth = measuredWidth,
        )
        val marks = equipment.map { item ->
            val nameKey = PcSheetV11SpecialFieldSource(
                item.sourceId, PcSheetV11SpecialField.NAME, item.name,
            ).sourceKey
            val nameCell = requireNotNull(placed.cells.firstOrNull { it.sourceKey == nameKey }) {
                "Special item name did not receive a physical row."
            }
            PcSheetV11SpecialActiveMark(
                sourceId = item.sourceId,
                active = item.active,
                rectPt = PcSheetV11RectPt(
                    areaPt.rightPt - activeMarkWidthPt,
                    nameCell.cellPt.bottomPt,
                    activeMarkWidthPt,
                    lineHeightPt,
                ),
                localPageIndex = nameCell.localPageIndex,
            )
        }
        return PcSheetV11SpecialEquipmentPlaced(
            sources = fields,
            body = placed,
            activeMarks = marks,
            inputIds = equipment.map { it.sourceId },
            areaPt = areaPt,
        ).also { it.verify() }
    }
}
