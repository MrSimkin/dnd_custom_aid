package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PcSheetV11CompactFieldRouterTest {
    private val width = PcSheetV11TextWidth { it.length.toFloat() }

    @Test
    fun shortAndEmptyCombatValuesRemainInP1WithoutFakeC2() {
        val short = PcSheetV11CompactFieldRouter.route(
            "p1-combat:CA", "CA", "18", 10f, width,
        )
        assertEquals("18", short.paperValue)
        assertNull(short.continuation)
        val blank = PcSheetV11CompactFieldRouter.route(
            "p1-combat:DADOS", "DADOS", "", 10f, width,
        )
        assertEquals("", blank.paperValue)
        assertNull(blank.continuation)
    }

    @Test
    fun longMulticlassDiceKeepEveryCharacterInSourceBackedC2() {
        val source = (1..16).joinToString(" / ") { "${it}d${100 + it}" }
        val result = PcSheetV11CompactFieldRouter.route(
            "p1-combat:DADOS", "DADOS", source, 24f, width,
        )
        assertEquals("VER C2", result.paperValue)
        val item = requireNotNull(result.continuation)
        assertEquals("p1-combat:DADOS", item.sourceKey)
        assertEquals("DADOS: $source", item.text)
        assertTrue(item.keepTogetherIfFits)
        val flowed = PcSheetV11TextFlow.paginate(
            PcSheetV11FlowSection.TRAITS,
            listOf(item), 2, PcSheetV11TextColumn(24f, 40f, 10f), width,
        )
        flowed.verifyLosslessAgainst(listOf(item))
        assertEquals(item.text, flowed.fragments.joinToString("") { it.text })
    }

    @Test
    fun impossiblePaperMarkerFailsExplicitlyWithoutTruncating() {
        assertFailsWith<IllegalArgumentException> {
            PcSheetV11CompactFieldRouter.route(
                "p1-combat:DADOS", "DADOS", "very long source", 3f, width,
            )
        }
    }
}
