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
    fun longSourceNameRetainsAReadablePrefixAndCompleteC2Text() {
        val name = "Aldren de los Siete Reinos y los Incontables Sellos del Alba"
        val actual = PcSheetV11HeaderNameRouter.route(name, 40f, width)
        assertTrue(actual.paperName.startsWith("Aldren"))
        assertTrue(name.startsWith(actual.paperName), "Only cut, with nothing added.")
        assertTrue(width.measurePt(actual.paperName) <= 40f)
        val full = assertNotNull(actual.continuation)
        assertEquals("p1:character-full-name", full.sourceKey)
        assertEquals("NOMBRE COMPLETO DEL PERSONAJE: " + name, full.text)
        assertTrue(full.keepTogetherIfFits)
    }

    @Test
    fun persistedCrLfAndTabsNeverReachOneLineFontButSurviveInC2() {
        val rawName = "Ilyra\r\nQuill\tCrónica"
        val strictWidth = PcSheetV11TextWidth { text ->
            require(text.none { it == '\r' || it == '\n' || it == '\t' }) {
                "A fixed PDF header cannot measure physical hard breaks."
            }
            text.length.toFloat()
        }
        // Even a fitting single-line preview cannot erase original breaks.
        val routed = PcSheetV11HeaderNameRouter.route(rawName, 100f, strictWidth)
        assertEquals("Ilyra Quill    Crónica", routed.paperName)
        val full = assertNotNull(routed.continuation)
        assertEquals("p1:character-full-name", full.sourceKey)
        assertEquals("NOMBRE COMPLETO DEL PERSONAJE: " + rawName, full.text)

        val constrained = PcSheetV11HeaderNameRouter.route(rawName, 30f, strictWidth)
        assertTrue(constrained.paperName.startsWith("Ilyra"))
        assertTrue(strictWidth.measurePt(constrained.paperName) <= 30f)
        assertEquals(full.text, assertNotNull(constrained.continuation).text)
    }

}
