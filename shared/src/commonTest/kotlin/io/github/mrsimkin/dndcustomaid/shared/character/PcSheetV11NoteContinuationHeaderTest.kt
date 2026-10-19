package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PcSheetV11NoteContinuationHeaderTest {
    private val id = "12345678-1234-1234-1234-123456789012"
    private val base = "C-N3 - Notas digitales / 99999"

    @Test
    fun normalTitleKeepsOriginalVisibleContext() {
        val name = "Nota 1 — Hipótesis"
        val header = PcSheetV11NoteContinuationHeader.choose(base, name, id) { it.length <= 200 }
        assertEquals(base + " / CONTINÚA: " + name, header)
    }

    @Test
    fun tooLongTitleAndPagePrefixStillKeepGenuineId() {
        val header = PcSheetV11NoteContinuationHeader.choose(
            base, "TÍTULO ".repeat(150), id,
        ) { it.length <= 54 }
        assertEquals("CONTINÚA ID: " + id, header)
        assertTrue(header.contains(id))
    }

    @Test
    fun generalNotesKeepExistingReadableCaption() {
        val header = PcSheetV11NoteContinuationHeader.choose(
            base, "NOTAS GENERALES", "GENERAL",
        ) { it.length <= 200 }
        assertEquals(base + " / CONTINÚA: NOTAS GENERALES", header)
    }

    @Test
    fun sourceControlBreaksUseStableIdRatherThanBrokenHeaderGlyphs() {
        val header = PcSheetV11NoteContinuationHeader.choose(
            base, "nota con salto\nde línea", id,
        ) { it.length <= 200 }
        assertEquals(base + " / CONTINÚA ID: " + id, header)
    }

    @Test
    fun impossiblePhysicalBandFailsInsteadOfDroppingNoteIdentity() {
        assertFailsWith<IllegalStateException> {
            PcSheetV11NoteContinuationHeader.choose(base, "Título", id) { false }
        }
    }
}
