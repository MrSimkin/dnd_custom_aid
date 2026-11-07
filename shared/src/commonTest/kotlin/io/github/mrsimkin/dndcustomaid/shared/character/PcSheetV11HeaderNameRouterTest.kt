package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PcSheetV11HeaderNameRouterTest {
    private val width = PcSheetV11TextWidth { text -> text.length.toFloat() }

    @Test
    fun ordinaryHeaderNameRemainsIdenticalAndNeedsNoNewPage() {
        val actual = PcSheetV11HeaderNameRouter.route("Aldren Vale", 100f, width)
        assertEquals("Aldren Vale", actual.paperName)
        assertNull(actual.continuation)
    }

    @Test
    fun longSourceNameIsOnlyCutInTheHeader() {
        val name = "Aldren de los Siete Reinos y los Incontables Sellos del Alba"
        val actual = PcSheetV11HeaderNameRouter.route(name, 40f, width)
        assertTrue(actual.paperName.startsWith("Aldren"))
        assertTrue(name.startsWith(actual.paperName), "Only cut, with nothing added.")
        assertTrue(width.measurePt(actual.paperName) <= 40f)
        // C463 (owner 2026-11-07, «solo quiero el nombre en la cabecera»).
        assertNull(actual.continuation)
    }

    @Test
    fun persistedCrLfAndTabsNeverReachOneLineFont() {
        val rawName = "Ilyra\r\nQuill\tCrónica"
        val strictWidth = PcSheetV11TextWidth { text ->
            require(text.none { it == '\r' || it == '\n' || it == '\t' }) {
                "A fixed PDF header cannot measure physical hard breaks."
            }
            text.length.toFloat()
        }
        // A saved break or tab is a space on the one-line header.
        val routed = PcSheetV11HeaderNameRouter.route(rawName, 100f, strictWidth)
        assertEquals("Ilyra Quill    Crónica", routed.paperName)
        assertNull(routed.continuation)

        val constrained = PcSheetV11HeaderNameRouter.route(rawName, 30f, strictWidth)
        assertTrue(constrained.paperName.startsWith("Ilyra"))
        assertTrue(strictWidth.measurePt(constrained.paperName) <= 30f)
        assertNull(constrained.continuation)
    }

}
