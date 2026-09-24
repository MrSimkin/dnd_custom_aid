package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDecodeResult
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportAggregate
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportStateSelection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportPlanner
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper

class DesktopPcSheetRuntimeQaFixtureTest {
    @Test
    fun aldrenFantasySheetRendersWithoutUnroutedOverflow() {
        val document = fixture("01_aldren_vale_srd5_1_champion_fighter.json")
        val plan = PcSheetPdfExportPlanner.plan(
            request = PcSheetPdfExportRequest(
                visualFamily = PcSheetVisualFamily.CLASSIC_DND_STYLE,
                stateSelection = PcSheetExportStateSelection.PERMANENT,
            ),
            sources = PcSheetExportSources(
                permanent = PcSheetExportAggregate(
                    sheet = document.character,
                    closure = document.closureState,
                    successor = document.successorState,
                ),
            ),
        )

        val bytes = ByteArrayOutputStream().use { output ->
            DesktopPcSheetWholeDraftRenderer().renderDraft(plan, output)
            output.toByteArray()
        }

        assertTrue(bytes.size > 20_000)

        Loader.loadPDF(bytes).use { pdf ->
            val extracted = PDFTextStripper().getText(pdf)
            // Compact base previews may truncate, but the canonical detail must survive through
            // the Fantasy continuation/reference routing.
            assertTrue(extracted.contains("Dueling incluido; dos ataques con la acción Atacar."))
            assertTrue(extracted.contains("Munición; recarga."))
            assertTrue(extracted.contains("Recupera 1d10 + 5 PG"))
            assertTrue(extracted.contains("Una acción adicional este turno"))
            assertTrue(extracted.contains("Descanso corto/largo"))
            assertTrue(extracted.contains("A máximo"))
            assertTrue(extracted.contains("Descanso corto o largo"))
        }
    }

    private fun fixture(name: String) = assertIs<CharacterBackupDecodeResult.Success>(
        CharacterBackupCodec.decode(File(fixtureDirectory(), name).readText()),
    ).document

    private fun fixtureDirectory(): File {
        val start = File(System.getProperty("user.dir")).absoluteFile
        return generateSequence(start) { it.parentFile }
            .map { File(it, "qa/pc-sheet/fixtures") }
            .firstOrNull { it.isDirectory }
            ?: error("Could not locate qa/pc-sheet/fixtures from ${start.path}")
    }
}
