package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.test.Test
import kotlin.test.assertEquals

class PcSheetPdfTextRepairTest {
    @Test
    fun repairsCommonUtf8AsWindows1252MojibakeTokens() {
        assertEquals("Común", repairLikelyUtf8MojibakeForPdf("ComÃºn"))
        assertEquals("Élfico", repairLikelyUtf8MojibakeForPdf("Ã‰lfico"))
        assertEquals("acción", repairLikelyUtf8MojibakeForPdf("acciÃ³n"))
        assertEquals("2–5", repairLikelyUtf8MojibakeForPdf("2â€“5"))
        assertEquals("rasgo — fuente", repairLikelyUtf8MojibakeForPdf("rasgo â€” fuente"))
    }

    @Test
    fun leavesAlreadyCorrectUnicodeUntouched() {
        val clean = "José Peña — acción Élfica; São Paulo"
        assertEquals(clean, repairLikelyUtf8MojibakeForPdf(clean))
    }

    @Test
    fun repairsOnlyCorruptedTokensInsideMixedCleanText() {
        assertEquals(
            "José usa acción Común — 2–5 veces",
            repairLikelyUtf8MojibakeForPdf("José usa acciÃ³n ComÃºn — 2â€“5 veces"),
        )
    }
}
