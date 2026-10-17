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
        val actual = PcSheetV11HeaderNameRouter.route(name, 22f, width)
        assertTrue(actual.paperName.startsWith("Aldren"))
        assertTrue(actual.paperName.endsWith(" / VER C2"))
        assertTrue(width.measurePt(actual.paperName) <= 22f)
        val full = assertNotNull(actual.continuation)
        assertEquals("p1:character-full-name", full.sourceKey)
        assertEquals("NOMBRE COMPLETO DEL PERSONAJE: " + name, full.text)
        assertTrue(full.keepTogetherIfFits)
    }
}
