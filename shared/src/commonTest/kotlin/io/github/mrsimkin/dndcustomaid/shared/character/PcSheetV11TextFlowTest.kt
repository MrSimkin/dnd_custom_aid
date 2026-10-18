package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PcSheetV11TextFlowTest {
    private val monospace = PcSheetV11TextWidth { text -> text.length.toFloat() }

    private fun geometry(width: Float = 8f, lines: Int = 2): PcSheetV11TextColumn =
        PcSheetV11TextColumn(widthPt = width, heightPt = lines * 10f, lineHeightPt = 10f)

    @Test
    fun multilineSourceIsPreservedExactlyAcrossPhysicalColumnBoundaries() {
        val original = "abc def ghi jkl\nSegundo renglón\r\n\nfinal\t  "
        val items = listOf(PcSheetV11FlowItem("note:1", original))
        val result = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.DIGITAL_NOTES, items, 2,
            geometry(width = 8f), monospace,
        )
        assertTrue(result.fragments.size > 2)
        result.verifyLosslessAgainst(items)
        assertEquals(original, result.fragments.joinToString("") { it.text })
        assertEquals(1, result.fragments.first().localPageIndex)
        assertTrue(result.fragments.all { it.columnIndex in 1..2 })
        assertTrue(result.fragments.all { it.text.isNotEmpty() })
        assertFalse(result.fragments.first().continuedFromPrevious)
        assertFalse(result.fragments.last().continuesOnNext)
    }

    @Test
    fun differentItemsUseExactSourceKeysAndKeepTogetherOnlyWhenWholeItemFits() {
        val items = listOf(
            PcSheetV11FlowItem("trait:1", "one\ntwo\n", keepTogetherIfFits = true),
            PcSheetV11FlowItem("trait:2", "alpha\nbeta", keepTogetherIfFits = true),
        )
        val result = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.TRAITS, items, 2, geometry(width = 10f), monospace,
        )
        assertEquals(2, result.fragments.size)
        assertEquals(listOf("trait:1", "trait:2"), result.fragments.map { it.sourceKey })
        assertEquals(listOf(1, 2), result.fragments.map { it.columnIndex })
        assertEquals(1, result.localPageCount)
        assertFalse(result.fragments.any { it.continuedFromPrevious || it.continuesOnNext })
        result.verifyLosslessAgainst(items)
    }

    @Test
    fun manyVeryLongEntriesPaginateWithoutLossOrInfiniteLoop() {
        val items = (1..386).map { n ->
            PcSheetV11FlowItem(
                sourceKey = "spell:$n",
                text = ("Descripción muy extensa #$n: " + "conjuro repetitivo ").repeat(3),
                keepTogetherIfFits = true,
            )
        }
        val first = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.SPELLBOOK, items, 2, geometry(width = 22f, lines = 5), monospace,
        )
        val second = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.SPELLBOOK, items, 2, geometry(width = 22f, lines = 5), monospace,
        )
        assertEquals(first, second)
        assertEquals(386, first.fragments.map { it.sourceKey }.distinct().size)
        assertTrue(first.localPageCount > 20)
        assertTrue(first.fragments.any { it.continuesOnNext })
        assertTrue(first.fragments.all { it.lineRanges.size <= 5 })
        first.verifyLosslessAgainst(items)
    }

    @Test
    fun hugeUnbrokenWordSplitsIntoMeasuredLinesInsteadOfThrowingLikeOldPrototype() {
        val text = "Z".repeat(1001)
        val input = listOf(PcSheetV11FlowItem("long-word", text, keepTogetherIfFits = true))
        val result = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.SPELLBOOK, input, 2,
            geometry(width = 13f, lines = 3), monospace,
        )
        assertEquals(text, result.fragments.joinToString("") { it.text })
        assertTrue(result.fragments.size >= 25)
        assertTrue(result.localPageCount >= 13)
        assertTrue(result.fragments.all { it.text.length <= 39 })
        result.verifyLosslessAgainst(input)
    }

    @Test
    fun surrogatePairsAndCrLfAreNeverBisected() {
        val original = "🔮🔮🔮\r\n🙂\nñ🙂🔮"
        val unicodeWidth = PcSheetV11TextWidth { content ->
            content.count { !it.isLowSurrogate() }.toFloat()
        }
        val items = listOf(PcSheetV11FlowItem("emoji", original))
        val result = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.NARRATIVE, items, 1,
            geometry(width = 2f, lines = 2), unicodeWidth,
        )
        result.verifyLosslessAgainst(items)
        assertEquals(original, result.fragments.joinToString("") { it.text })
        result.fragments.flatMap { it.lineRanges }.forEach { range ->
            val before = original[range.startOffset]
            val last = original[range.endExclusive - 1]
            assertFalse(before.isLowSurrogate())
            assertFalse(last.isHighSurrogate())
            assertFalse(last == '\r' && range.endExclusive < original.length &&
                original[range.endExclusive] == '\n')
        }
    }

    @Test
    fun emptyAndBlankInputNeverAllocateSpuriousPages() {
        val result = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.DIGITAL_NOTES,
            listOf(PcSheetV11FlowItem("empty", "")), 2, geometry(), monospace,
        )
        assertEquals(0, result.localPageCount)
        assertTrue(result.fragments.isEmpty())

        val whitespace = listOf(PcSheetV11FlowItem("whitespace", "   \n"))
        val blank = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.DIGITAL_NOTES, whitespace, 2, geometry(), monospace,
        )
        assertEquals("   \n", blank.fragments.single().text)
    }


    @Test
    fun sourceTabsUsePrintableWidthWithoutChangingSourceOffsetsOrCharacters() {
        val original = "A\tB"
        val items = listOf(PcSheetV11FlowItem("narrative:story", original))
        val placed = PcSheetV11MeasuredFlowPlacer.place(
            PcSheetV11FlowSection.NARRATIVE, items,
            PcSheetV11RectPt(42f, 76f, 5f, 30f),
            1, 0f, 10f, monospace,
        )
        assertEquals(original, placed.cells.joinToString("") { it.sourceText })
        assertEquals(listOf("A    ", "B"), placed.cells.map { it.drawableText })
        assertEquals(listOf(0 to 2, 2 to 3),
            placed.cells.map { it.sourceStartOffset to it.sourceEndExclusive })
        assertEquals(1, placed.sourceFlow.localPageCount)
        placed.sourceFlow.verifyLosslessAgainst(items)
        placed.verifyGeometry()
    }

    @Test
    fun customAttributeAndSkillsStayInSamePhysicalColumnWhenGroupFits() {
        // Reproduce a real C2 boundary: three occupied lines precede a
        // one-line attribute and its one-line linked skill in a 4-line column.
        // Old per-item flow stranded the attribute in column 1.
        val items = listOf(
            PcSheetV11FlowItem("prior", "a\nb\nc"),
            PcSheetV11FlowItem("attribute:real-id", "Fortuna 16",
                keepTogetherIfFits = true, groupKey = "attr:real-id"),
            PcSheetV11FlowItem("attribute-skill:real-skill", "Contratos",
                keepTogetherIfFits = true, groupKey = "attr:real-id"),
        )
        val groupFlow = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.TRAITS, items, 2,
            geometry(width = 12f, lines = 4), monospace,
        )
        groupFlow.verifyLosslessAgainst(items)
        val placements = groupFlow.fragments.associateBy { it.sourceKey }
        val attribute = placements.getValue("attribute:real-id")
        val skill = placements.getValue("attribute-skill:real-skill")
        assertEquals(2, attribute.columnIndex)
        assertEquals(attribute.columnIndex, skill.columnIndex)
        assertEquals(attribute.localPageIndex, skill.localPageIndex)
        assertEquals(listOf("prior", "attribute:real-id", "attribute-skill:real-skill"),
            groupFlow.fragments.map { it.sourceKey })

        // The default path must stay unchanged for unrelated V11 sections.
        val ungrouped = items.map { it.copy(groupKey = null) }
        val original = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.TRAITS, ungrouped, 2,
            geometry(width = 12f, lines = 4), monospace,
        )
        assertEquals(1, original.fragments.single {
            it.sourceKey == "attribute:real-id"
        }.columnIndex)
        assertEquals(2, original.fragments.single {
            it.sourceKey == "attribute-skill:real-skill"
        }.columnIndex)
    }

    @Test
    fun oversizedC2AttributeGroupStillPaginatesWithoutLosingIdsOrText() {
        val items = listOf(
            PcSheetV11FlowItem("attribute:big", "A".repeat(40),
                groupKey = "attr:big", keepTogetherIfFits = true),
            PcSheetV11FlowItem("attribute-skill:big", "B".repeat(40),
                groupKey = "attr:big", keepTogetherIfFits = true),
        )
        val flow = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.TRAITS, items, 2,
            geometry(width = 10f, lines = 3), monospace,
        )
        flow.verifyLosslessAgainst(items)
        assertTrue(flow.localPageCount > 1)
        assertEquals(items.map { it.text },
            items.map { item -> flow.fragments
                .filter { it.sourceKey == item.sourceKey }
                .joinToString("") { it.text } })
    }

    @Test
    fun invalidGeometryAndUnrenderableGlyphFailClosed() {
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11TextColumn(10f, 9f, 10f)
        }
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11TextColumn(Float.NaN, 30f, 10f)
        }
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11TextFlow.paginate(
                PcSheetV11FlowSection.NARRATIVE,
                listOf(PcSheetV11FlowItem("tiny", "W")),
                2, geometry(width = 1f),
                PcSheetV11TextWidth { 2f },
            )
        }
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11TextFlow.paginate(
                PcSheetV11FlowSection.NARRATIVE,
                listOf(PcSheetV11FlowItem("x", "one"), PcSheetV11FlowItem("x", "two")),
                2, geometry(), monospace,
            )
        }
    }
}
