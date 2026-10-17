package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PcSheetV11MeasuredFlowPlacerTest {
    private val measuredFont = PcSheetV11TextWidth { text ->
        // Deliberate fixture-only font, not a claim of true embedded font metrics.
        text.length * 5f
    }

    @Test
    fun letterBoundsAndPhysicalTwoAndThreeColumnGridNeverCrossPageEdges() {
        assertEquals(612f, PcSheetV11Paper.LETTER_WIDTH_PT)
        assertEquals(792f, PcSheetV11Paper.LETTER_HEIGHT_PT)
        val available = PcSheetV11RectPt(36f, 60f, 540f, 660f)
        val two = PcSheetV11ColumnGrid.split(available, 2, 12f)
        assertEquals(2, two.size)
        assertEquals(264f, two[0].widthPt)
        assertEquals(36f, two[0].leftPt)
        assertEquals(312f, two[1].leftPt)
        assertEquals(576f, two[1].rightPt)
        val three = PcSheetV11ColumnGrid.split(available, 3, 9f)
        assertEquals(3, three.size)
        assertEquals(174f, three[0].widthPt)
        assertEquals(219f, three[1].leftPt)
        assertEquals(402f, three[2].leftPt)
        assertEquals(576f, three[2].rightPt)
        assertTrue((two + three).all { available.contains(it) })
        assertTrue((two + three).all { PcSheetV11Paper.letterBounds.contains(it) })
    }

    @Test
    fun adjacentItemsUseSeparatePhysicalRowsWithoutOverlaps() {
        val input = listOf(
            PcSheetV11FlowItem("story:1", "uno\ndos\n"),
            PcSheetV11FlowItem("story:2", "tres\ncuatro"),
            PcSheetV11FlowItem("story:3", "cinco"),
        )
        val measured = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.NARRATIVE, input,
            PcSheetV11RectPt(36f, 60f, 540f, 660f),
            columnsPerPage = 2, gutterPt = 12f, lineHeightPt = 12f,
            textWidth = measuredFont,
        )
        assertEquals(5, measured.cells.size)
        assertEquals(listOf(0, 1, 2, 3, 4), measured.cells.map { it.rowIndex })
        assertTrue(measured.cells.all { it.columnIndex == 1 && it.localPageIndex == 1 })
        assertEquals(listOf(708f, 696f, 684f, 672f, 660f),
            measured.cells.map { it.cellPt.bottomPt })
        assertEquals("uno", measured.cells.first().drawableText)
        assertEquals("uno\n", measured.cells.first().sourceText)
        measured.sourceFlow.verifyLosslessAgainst(input)
        measured.verifyGeometry()
    }

    @Test
    fun narrativeCrossesColumnsAndPagesWithContiguousSourceOffsets() {
        val original = ("Historia extensa de Mara — capítulo 1.\n").repeat(110)
        val items = listOf(PcSheetV11FlowItem("story:Mara", original))
        val argsArea = PcSheetV11RectPt(45f, 90f, 510f, 600f)
        val first = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.NARRATIVE, items,
            argsArea, 2, 10f, 12f, measuredFont,
        )
        val second = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.NARRATIVE, items,
            argsArea, 2, 10f, 12f, measuredFont,
        )
        assertEquals(first, second)
        assertTrue(first.sourceFlow.localPageCount >= 2)
        assertTrue(first.cells.any { it.localPageIndex >= 2 })
        assertTrue(first.cells.all { it.cellPt.bottomPt >= argsArea.bottomPt })
        assertEquals(original, first.sourceFlow.fragments.joinToString("") { it.text })
        first.sourceFlow.verifyLosslessAgainst(items)
        first.verifyGeometry()
    }

    @Test
    fun bk04MeasuredBookKeepsMediumCardWholeAtStartOfNextColumn() {
        // A real geometric two-column layout (source-driven, no fake physical
        // page numbers). An 8-line column has 2 rows left after the first
        // card; the following 4-line card must move WHOLE to the next column.
        val first = PcSheetV11FlowItem(
            "book:first", "PREVIA 1\nPREVIA 2\nPREVIA 3\nPREVIA 4\nPREVIA 5\nPREVIA 6",
            keepTogetherIfFits = true,
        )
        val medium = PcSheetV11FlowItem(
            "book:medium", "CONJURO REAL\nNIVEL 1\nDESCRIPCIÓN:\nCIERRE REAL",
            keepTogetherIfFits = true,
        )
        val huge = PcSheetV11FlowItem(
            "book:huge", ("CONTENIDO COMPLETO\n").repeat(11),
            keepTogetherIfFits = true,
        )
        val items = listOf(first, medium, huge)
        val placed = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.SPELLBOOK, items,
            PcSheetV11RectPt(42f, 76f, 528f, 80f),
            2, 12f, 10f, measuredFont,
        )
        assertEquals(8, placed.lineCapacity)
        val firstSpan = placed.sourceFlow.fragments.filter { it.sourceKey == first.sourceKey }
        val mediumSpan = placed.sourceFlow.fragments.filter { it.sourceKey == medium.sourceKey }
        assertEquals(1, firstSpan.size)
        assertEquals(1, mediumSpan.size,
            "A card shorter than one whole column must not be fragmented.")
        assertEquals(1, mediumSpan.single().localPageIndex)
        assertEquals(2, mediumSpan.single().columnIndex)
        assertEquals(0, mediumSpan.single().startLineIndexInColumn)
        assertEquals(medium.text, mediumSpan.single().text)
        assertFalse(mediumSpan.single().continuedFromPrevious)
        assertFalse(mediumSpan.single().continuesOnNext)
        assertEquals(1, firstSpan.single().columnIndex)
        assertEquals(6, firstSpan.single().lineRanges.size)
        assertEquals(0, placed.cells.first {
            it.sourceKey == medium.sourceKey
        }.rowIndex)
        val hugeSpans = placed.sourceFlow.fragments.filter { it.sourceKey == huge.sourceKey }
        assertTrue(hugeSpans.size > 1,
            "A card LONGER than an entire column is allowed to split.")
        assertTrue(hugeSpans.last().continuedFromPrevious)
        assertEquals(huge.text, hugeSpans.joinToString("") { it.text })
        placed.sourceFlow.verifyLosslessAgainst(items)
        placed.verifyGeometry()
    }

    @Test
    fun bk05MeasuredBookSplitsOnlyOversizedCardAcrossColumnsAndPagesLosslessly() {
        // D-0076 §3.48 / BK05: an oversized essential spellbook card MUST
        // split across columns and physical pages without invented source
        // data, skipped UTF-16 offsets, overlap, or forced reduced font.
        val tall = PcSheetV11FlowItem(
            "book:oversized", "CONJURO REAL\\nNIVEL 9\\nDESCRIPCIÓN:\\n" +
                (1..15).joinToString("\\n") {
                    "PARRAFO_" + it.toString().padStart(3, '0')
                },
            keepTogetherIfFits = true,
        )
        val later = PcSheetV11FlowItem(
            "book:next", "OTRO CONJURO\\nDESCRIPCIÓN REAL",
            keepTogetherIfFits = true,
        )
        val items = listOf(tall, later)
        val area = PcSheetV11RectPt(42f, 76f, 528f, 60f)
        val placed = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.SPELLBOOK, items,
            area, 2, 12f, 10f, measuredFont,
        )
        assertEquals(6, placed.lineCapacity)
        val fragments = placed.sourceFlow.fragments.filter {
            it.sourceKey == tall.sourceKey
        }
        assertEquals(3, fragments.size, "18 lines must span three 6-line columns.")
        assertEquals(listOf(1 to 1, 1 to 2, 2 to 1),
            fragments.map { it.localPageIndex to it.columnIndex })
        assertEquals(listOf(0, 0, 0),
            fragments.map { it.startLineIndexInColumn })
        assertEquals(listOf(false, true, true),
            fragments.map { it.continuedFromPrevious })
        assertEquals(listOf(true, true, false),
            fragments.map { it.continuesOnNext })
        fragments.zipWithNext().forEach { (previous, current) ->
            assertEquals(previous.endExclusive, current.startOffset)
        }
        assertEquals(0, fragments.first().startOffset)
        assertEquals(tall.text.length, fragments.last().endExclusive)
        assertEquals(tall.text, fragments.joinToString("") { it.text })
        assertEquals(18, fragments.sumOf { it.lineRanges.size })
        assertEquals(2, placed.sourceFlow.localPageCount)
        val nextSpan = placed.sourceFlow.fragments.filter {
            it.sourceKey == later.sourceKey
        }
        assertEquals(1, nextSpan.size)
        assertEquals(2, nextSpan.single().localPageIndex)
        assertEquals(2, nextSpan.single().columnIndex)
        assertFalse(nextSpan.single().continuedFromPrevious)
        assertFalse(nextSpan.single().continuesOnNext)
        assertTrue(placed.cells.all {
            area.contains(it.cellPt) &&
                PcSheetV11Paper.letterBounds.contains(it.cellPt)
        })
        placed.sourceFlow.verifyLosslessAgainst(items)
        placed.verifyGeometry()
    }

    @Test
    fun spellThreeColumnsAreMeasuredNotAssumedAndDoNotOverlap() {
        val original = "a".repeat(600)
        val input = listOf(PcSheetV11FlowItem("spell:long", original))
        val area = PcSheetV11RectPt(45f, 100f, 300f, 240f)
        val measured = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.SPELLBOOK, input,
            area, 3, 15f, 20f, measuredFont,
        )
        assertEquals(90f, measured.columnsPt[0].widthPt)
        assertTrue(measured.cells.map { it.columnIndex }.distinct().size >= 2)
        assertTrue(measured.cells.all { area.contains(it.cellPt) })
        assertTrue(measured.cells.all { it.cellPt.widthPt == 90f })
        assertEquals(original, measured.sourceFlow.fragments.joinToString("") { it.text })
        measured.verifyGeometry()
    }

    @Test
    fun explicitBlankLinesReserveCellsWithoutPrintingLineBreakControlCharacters() {
        val input = listOf(PcSheetV11FlowItem("notes:1", "a\r\n\nb"))
        val result = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.DIGITAL_NOTES, input,
            PcSheetV11RectPt(20f, 100f, 200f, 120f),
            1, 0f, 10f, measuredFont,
        )
        assertEquals(listOf("a\r\n", "\n", "b"), result.cells.map { it.sourceText })
        assertEquals(listOf("a", "", "b"), result.cells.map { it.drawableText })
        assertEquals(listOf(0, 1, 2), result.cells.map { it.rowIndex })
        assertFalse(result.cells.any { it.drawableText.contains('\n') || it.drawableText.contains('\r') })
    }

    @Test
    fun illegalCoordinatesAndUnrenderableInputStopRatherThanClipping() {
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11RectPt(611f, 0f, 20f, 10f).also {
                PcSheetV11ColumnGrid.split(it, 1, 0f)
            }
        }
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11ColumnGrid.split(PcSheetV11RectPt(0f, 0f, 30f, 50f), 3, 20f)
        }
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11MeasuredFlowPlacer.place(
                PcSheetV11FlowSection.NARRATIVE,
                listOf(PcSheetV11FlowItem("x", "a")),
                PcSheetV11RectPt(10f, 10f, 10f, 5f),
                2, 1f, 8f, measuredFont,
            )
        }
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11MeasuredFlowPlacer.place(
                PcSheetV11FlowSection.NARRATIVE,
                listOf(PcSheetV11FlowItem("x", "W")),
                PcSheetV11RectPt(10f, 10f, 12f, 40f),
                1, 0f, 10f, PcSheetV11TextWidth { 20f },
            )
        }
    }
}
