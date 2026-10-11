package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class PcSheetV11PairedTableComposerTest {
    private val width = PcSheetV11TextWidth { it.length * 5f }
    private val area = PcSheetV11RectPt(36f, 100f, 540f, 420f)

    private fun place(table: PcSheetV11PairedTableData): PcSheetV11PairedTablePlaced =
        PcSheetV11PairedTableComposer.place(
            data = table, areaPt = area, gutterPt = 12f,
            quantityWidthPt = 36f, labelQuantityGapPt = 6f,
            lineHeightPt = 12f, measuredWidth = width,
        )

    @Test
    fun realZeroTreasureIsPrintedRatherThanOmittedOrInvented() {
        val table = PcSheetV11PairedTableData(
            PcSheetV11PairedTableKind.TREASURE,
            listOf(
                PcSheetV11PairedTableRow("currency:cp", "Cobre", 0, null),
                PcSheetV11PairedTableRow("currency:gp", "Oro", 125, null),
                PcSheetV11PairedTableRow("currency:pp", "Platino", 0, null),
            ),
        )
        val placed = place(table)
        assertEquals(3, placed.quantities.size)
        assertEquals(listOf("0", "125", "0"), placed.quantities.map { it.printableText })
        assertEquals(table.rows.map { it.label }, placed.labels.map { it.sourceText })
        assertEquals(table.rows.map { it.sourceKey }, placed.quantities.map { it.sourceKey })
        assertEquals(1, placed.localPageCount)
        assertEquals(2, placed.pairs.size)
        placed.verify()
    }

    @Test
    fun hugeEquipmentNamesSplitWithoutReplicatingAmountsAcrossColumnsOrPages() {
        val id = Uuid.parse("60000000-0000-0000-0000-000000000001")
        val name = ("Bolsa de dimensiones sin pérdidas — ").repeat(35)
        val table = PcSheetV11PairedTableData(
            PcSheetV11PairedTableKind.EQUIPMENT,
            listOf(PcSheetV11PairedTableRow("equipment:" + id, name, 7, id)),
        )
        val placed = PcSheetV11PairedTableComposer.place(
            table, PcSheetV11RectPt(30f, 80f, 400f, 72f), 10f, 28f, 5f, 12f, width,
        )
        assertTrue(placed.localPageCount >= 2)
        assertEquals(name, placed.labelFlow.fragments.joinToString("") { it.text })
        assertEquals(name, placed.labels.joinToString("") { it.sourceText })
        assertEquals(1, placed.quantities.size)
        assertEquals("7", placed.quantities.single().printableText)
        assertTrue(placed.labels.map { it.pairIndex }.distinct().size == 2)
        assertTrue(placed.labels.all { it.rectPt.rightPt <= placed.pairs[it.pairIndex - 1].rightPt })
        placed.verify()
    }

    @Test
    fun hundredsOfDistinctRowsRemainOrderedAndDeterministicWithNoDuplicateSourceIds() {
        val rows = (1..386).map { index ->
            PcSheetV11PairedTableRow(
                "equipment:item-" + index,
                "Utensilio personalizado número " + index + " — descripción de identidad extensa",
                index - 1,
                null,
            )
        }
        val table = PcSheetV11PairedTableData(PcSheetV11PairedTableKind.EQUIPMENT, rows)
        val first = place(table)
        val second = place(table)
        assertEquals(first, second)
        assertTrue(first.localPageCount >= 5)
        assertEquals(rows.map { it.sourceKey }, first.quantities.map { it.sourceKey })
        assertEquals(rows.map { it.amount.toString() }, first.quantities.map { it.sourceText })
        assertTrue(first.labels.all { it.rectPt.leftPt >= area.leftPt })
        assertTrue(first.labels.all { it.rectPt.rightPt <= area.rightPt })
        first.verify()
    }

    @Test
    fun newlinesInPersistedLabelsArePreservedInSourceAndNotPrintedAsControlCharacters() {
        val rows = listOf(PcSheetV11PairedTableRow("equipment:1", "Mapa\r\nFragmentado\n", 0, null))
        val placed = place(PcSheetV11PairedTableData(PcSheetV11PairedTableKind.EQUIPMENT, rows))
        assertEquals(rows.single().label, placed.labels.joinToString("") { it.sourceText })
        assertTrue(placed.labels.any { it.sourceText.contains('\n') })
        assertFalse(placed.labels.any { it.printableText.contains('\r') || it.printableText.contains('\n') })
        assertEquals("0", placed.quantities.single().printableText)
    }

    @Test
    fun absentRecordsRemainAbsentAndInvalidMeasuresFailExplicitly() {
        val none = place(PcSheetV11PairedTableData(PcSheetV11PairedTableKind.TREASURE, emptyList()))
        assertEquals(0, none.localPageCount)
        assertTrue(none.labels.isEmpty() && none.quantities.isEmpty())
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11PairedTableData(
                PcSheetV11PairedTableKind.EQUIPMENT,
                listOf(
                    PcSheetV11PairedTableRow("duplicate", "A", 1, null),
                    PcSheetV11PairedTableRow("duplicate", "B", 2, null),
                ),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            place(PcSheetV11PairedTableData(
                PcSheetV11PairedTableKind.TREASURE,
                listOf(PcSheetV11PairedTableRow("coin:missing", " ", 0, null)),
            ))
        }
        val largeNumber = PcSheetV11PairedTableData(
            PcSheetV11PairedTableKind.TREASURE,
            listOf(PcSheetV11PairedTableRow("coin:1", "Platino", 123456789, null)),
        )
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11PairedTableComposer.place(
                largeNumber, area, 12f, 10f, 6f, 12f, width,
            )
        }
    }
}
