package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PcSheetV11ContinuationTextTest {
    @Test
    fun leavingAndArrivingUseTheOwnerWording() {
        assertEquals("CONTINÚA EN PÁG. 5", PcSheetV11ContinuationText.continuesOn(5))
        assertEquals("VIENE DE PÁG. 2", PcSheetV11ContinuationText.comesFrom(2))
        assertEquals("VIENE DE PÁG. 2 Y 4", PcSheetV11ContinuationText.comesFrom(listOf(4, 2, 4)))
        assertEquals("VIENE DE PÁG. 1, 2 Y 4", PcSheetV11ContinuationText.comesFrom(listOf(1, 4, 2)))
        assertNull(PcSheetV11ContinuationText.comesFrom(emptyList()))
    }

    @Test
    fun moduleLinksGroupModulesThatContinueOnTheSamePage() {
        assertEquals(
            "PERSONAJE, HABILIDADES CONTINÚAN EN PÁG. 4   ·   ATAQUES CONTINÚA EN PÁG. 5",
            PcSheetV11ContinuationText.moduleLinks(
                listOf("PERSONAJE" to 4, "HABILIDADES" to 4, "ATAQUES" to 5),
            ),
        )
        assertNull(PcSheetV11ContinuationText.moduleLinks(emptyList()))
    }

    @Test
    fun columnCaptionSaysWhereTheSourceComesFrom() {
        assertEquals("RECURSOS Y MUNICIONES / VIENE DE PÁG. 4",
            PcSheetV11ContinuationText.arrived("RECURSOS Y MUNICIONES", 4))
        assertEquals("NIVEL 2 / CONTINUACIÓN", PcSheetV11ContinuationText.arrived("NIVEL 2", null))
    }
}
