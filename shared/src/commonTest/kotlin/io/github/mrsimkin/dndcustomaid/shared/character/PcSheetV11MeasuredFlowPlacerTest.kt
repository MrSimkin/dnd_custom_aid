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
    fun firstLineExtraMakesATallerBandAndShrinksTheColumnCapacity() {
        // C362: golden P4 level bands are taller than the rows under them.
        val input = listOf(PcSheetV11FlowItem("h1", "A", lineExtrasPt = mapOf(0 to 5f))) +
            (1..3).map { PcSheetV11FlowItem("r$it", "r") } +
            PcSheetV11FlowItem("h2", "B", lineExtrasPt = mapOf(0 to 5f)) +
            (4..8).map { PcSheetV11FlowItem("r$it", "r") }
        val measured = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.QUICK_SPELLS, input,
            PcSheetV11RectPt(36f, 60f, 540f, 100f),
            columnsPerPage = 2, gutterPt = 12f, lineHeightPt = 10f,
            textWidth = measuredFont,
        )
        val first = measured.cells.filter { it.columnIndex == 1 }
        assertEquals(listOf(145f, 135f, 125f, 115f, 100f, 90f, 80f, 70f, 60f),
            first.map { it.cellPt.bottomPt })
        assertEquals(listOf(15f, 10f, 10f, 10f, 15f, 10f, 10f, 10f, 10f),
            first.map { it.cellPt.heightPt })
        // Two 5 pt bands cost one whole line: r8 opens the second column.
        val second = measured.cells.single { it.columnIndex == 2 }
        assertEquals("r8", second.sourceKey)
        assertEquals(150f, second.cellPt.bottomPt)
        measured.sourceFlow.verifyLosslessAgainst(input)

        // A band that no longer fits moves on; a continued fragment never repeats the extra.
        val split = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.QUICK_SPELLS,
            listOf(PcSheetV11FlowItem("a", "a"), PcSheetV11FlowItem("b", "b"),
                PcSheetV11FlowItem("h", "h\nh2\nh3", lineExtrasPt = mapOf(0 to 5f))),
            PcSheetV11RectPt(36f, 60f, 540f, 30f),
            columnsPerPage = 2, gutterPt = 12f, lineHeightPt = 10f,
            textWidth = measuredFont,
        )
        val heading = split.cells.filter { it.sourceKey == "h" }
        assertEquals(listOf(1 to 2, 1 to 2, 2 to 1), heading.map { it.localPageIndex to it.columnIndex })
        assertEquals(listOf(75f, 65f, 80f), heading.map { it.cellPt.bottomPt })
        assertEquals(listOf(15f, 10f, 10f), heading.map { it.cellPt.heightPt })
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11FlowItem("bad", "x", lineExtrasPt = mapOf(0 to -1f))
        }
    }

    @Test
    fun sameInputGivesTheSameBreaksIdsAndPagesEveryTime() {
        // C368 / GLB11: the measured placement is a pure function of its input -
        // groups, separations, line extras, column and page breaks included.
        fun input() = (1..40).flatMap { n ->
            listOf(
                PcSheetV11FlowItem("head:$n", "Heading $n", keepTogetherIfFits = true,
                    groupKey = "g$n", lineExtrasPt = mapOf(0 to 4.96f)),
                PcSheetV11FlowItem("body:$n", "word ".repeat(n % 7 + 3).trim() + "\nsecond line $n",
                    groupKey = "g$n", leadingSeparationLines = n % 2),
            )
        }
        fun place() = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.QUICK_SPELLS, input(),
            PcSheetV11RectPt(36f, 60f, 540f, 200f),
            columnsPerPage = 3, gutterPt = 9f, lineHeightPt = 15.73f,
            textWidth = measuredFont,
        )
        val first = place()
        assertTrue(first.sourceFlow.localPageCount >= 2, "The fixture must break pages.")
        repeat(3) {
            val again = place()
            assertEquals(first.sourceFlow, again.sourceFlow)
            assertEquals(first.cells, again.cells)
        }
    }

    @Test
    fun traitsFlowAcrossColumnsAndPagesWithEachIdOnceAndInOrder() {
        // C391 / RSG04 G2: 60 traits of varied length cross two columns and several
        // local pages; each source key appears in one contiguous run of fragments,
        // in source order, with its text rebuilt exactly once.
        val input = (1..60).map { n ->
            PcSheetV11FlowItem("trait:$n", "Rasgo $n " + "texto ".repeat(n % 9 + 1).trim())
        }
        val measured = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.TRAITS, input, PcSheetV11RectPt(36f, 60f, 540f, 160f),
            columnsPerPage = 2, gutterPt = 12f, lineHeightPt = 16f, textWidth = measuredFont,
        )
        assertTrue(measured.sourceFlow.localPageCount >= 3, "The fixture crosses pages.")
        val keys = measured.sourceFlow.fragments.map { it.sourceKey }
        assertEquals(input.map { it.sourceKey }, keys.distinct())
        keys.distinct().forEach { key ->
            val at = keys.indices.filter { keys[it] == key }
            assertEquals((at.first()..at.last()).toList(), at, "$key is one contiguous run.")
        }
        input.forEach { item ->
            assertEquals(item.text, measured.sourceFlow.fragments.filter { it.sourceKey == item.sourceKey }
                .joinToString("") { it.text })
        }
        measured.sourceFlow.verifyLosslessAgainst(input)
        measured.verifyGeometry()
    }

    @Test
    fun longQuickSpellNamesStayWholeInOneColumnUnderTheirLevelBand() {
        // C392 / P404 G2: under a taller level band, 40 long spell names (two lines
        // each, keep-together) fill three columns over several pages; every name stays
        // whole in one column, once and in source order, with no lost or split text.
        val heading = PcSheetV11FlowItem("spell-level:3", "NIVEL 3", keepTogetherIfFits = true,
            lineExtrasPt = mapOf(0 to 4.96f))
        val spells = (1..40).map { n ->
            PcSheetV11FlowItem("quick-spell:$n", "QAP4L" + n.toString().padStart(3, '0') +
                " nombre largo de conjuro de prueba", keepTogetherIfFits = true)
        }
        val input = listOf(heading) + spells
        val measured = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.QUICK_SPELLS, input, PcSheetV11RectPt(36f, 60f, 540f, 200f),
            columnsPerPage = 3, gutterPt = 9f, lineHeightPt = 15.73f, textWidth = measuredFont,
        )
        assertTrue(measured.sourceFlow.localPageCount >= 2, "The fixture crosses pages.")
        spells.forEach { spell ->
            val parts = measured.sourceFlow.fragments.filter { it.sourceKey == spell.sourceKey }
            assertEquals(1, parts.size, "${spell.sourceKey} stays whole in one column.")
            assertEquals(2, parts.single().lineRanges.size, "${spell.sourceKey} wraps into its two lines.")
            assertEquals(spell.text, parts.single().text)
        }
        assertEquals(input.map { it.sourceKey }, measured.sourceFlow.fragments.map { it.sourceKey })
        measured.sourceFlow.verifyLosslessAgainst(input)
        measured.verifyGeometry()
    }

    @Test
    fun anExtensionPageAppearsOnlyWhenTheContentDoesNotFit() {
        // C391 / C201 G2: a column pair that holds the content uses one local page;
        // a single extra line opens exactly one more page, which holds that line.
        val area = PcSheetV11RectPt(36f, 60f, 540f, 100f)
        fun place(lines: Int) = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.TRAITS, (1..lines).map { PcSheetV11FlowItem("l$it", "línea $it") },
            area, columnsPerPage = 2, gutterPt = 12f, lineHeightPt = 10f, textWidth = measuredFont,
        )
        val full = place(20)
        assertEquals(1, full.sourceFlow.localPageCount, "20 lines fill two 10-line columns on one page.")
        val over = place(21)
        assertEquals(2, over.sourceFlow.localPageCount, "One more line opens one extension page.")
        assertEquals(listOf("l21"), over.cells.filter { it.localPageIndex == 2 }.map { it.sourceKey })
        assertEquals(1, place(1).sourceFlow.localPageCount)
    }

    @Test
    fun lineExtraBeforeAMiddleLineAddsAirOnlyThere() {
        // C363: the golden book card leaves more air before its description.
        val card = PcSheetV11FlowItem("card", "Name\nSchool\nDescription", keepTogetherIfFits = true,
            lineExtrasPt = mapOf(12 to 3f))
        val measured = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.SPELLBOOK, listOf(card, PcSheetV11FlowItem("next", "n")),
            PcSheetV11RectPt(36f, 60f, 540f, 100f),
            columnsPerPage = 2, gutterPt = 12f, lineHeightPt = 10f,
            textWidth = measuredFont,
        )
        assertEquals(listOf(150f, 140f, 127f, 117f), measured.cells.map { it.cellPt.bottomPt })
        assertEquals(listOf(10f, 10f, 13f, 10f), measured.cells.map { it.cellPt.heightPt })
        assertEquals(listOf(0, 1, 2, 3), measured.cells.map { it.rowIndex })
        // A 3 pt extra in a 40 pt column of 10 pt rows leaves room for 3 lines only.
        val tight = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.SPELLBOOK,
            listOf(PcSheetV11FlowItem("a", "a"), card),
            PcSheetV11RectPt(36f, 60f, 540f, 40f),
            columnsPerPage = 2, gutterPt = 12f, lineHeightPt = 10f,
            textWidth = measuredFont,
        )
        assertTrue(tight.cells.filter { it.sourceKey == "card" }.all { it.columnIndex == 2 })
        tight.sourceFlow.verifyLosslessAgainst(listOf(PcSheetV11FlowItem("a", "a"), card))
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
            "book:oversized", "CONJURO REAL\nNIVEL 9\nDESCRIPCIÓN:\n" +
                (1..15).joinToString("\n") {
                    "PARRAFO_" + it.toString().padStart(3, '0')
                },
            keepTogetherIfFits = true,
        )
        val later = PcSheetV11FlowItem(
            "book:next", "OTRO CONJURO\nDESCRIPCIÓN REAL",
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
