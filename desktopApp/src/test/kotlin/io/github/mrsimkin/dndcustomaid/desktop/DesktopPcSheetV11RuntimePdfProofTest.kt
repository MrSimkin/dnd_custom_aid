package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDecodeResult
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportAggregate
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportStateSelection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant
import java.io.File
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPortraitFitMode
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Integration proof from the actual Desktop app export service, NOT synthetic
 * ReportLab documents. This does not approve the PDF's still-incomplete V11 artwork.
 */
class DesktopPcSheetV11RuntimePdfProofTest {
    private fun sources(fixtureName: String): PcSheetExportSources {
        val current = File(System.getProperty("user.dir")).absoluteFile
        val dir = generateSequence(current) { it.parentFile }
            .map { File(it, "qa/pc-sheet/fixtures") }
            .firstOrNull { it.isDirectory }
            ?: error("Real PC QA backup fixtures not mounted")
        val raw = File(dir, fixtureName).readText()
        val doc = (CharacterBackupCodec.decode(raw) as CharacterBackupDecodeResult.Success).document
        return PcSheetExportSources(
            permanent = PcSheetExportAggregate(
                sheet = doc.character, closure = doc.closureState, successor = doc.successorState,
            ),
        )
    }

    private fun preserveRealApplicationPdf(bytes: ByteArray, outputName: String) {
        val root = File(
            System.getProperty("pcSheetProofDir")
                ?: File("build/pc-sheet-proofs").absolutePath,
        )
        root.mkdirs()
        File(root, outputName).writeBytes(bytes)
    }

    @Test
    fun realMaraApplicationServiceWritesMultiPageLetterPdfFromOwnRenderer() {
        val real = sources("03_mara_siete_umbrales_custom_extended.json")
        val generated = DesktopPcSheetExportService().generateV11Proof(
            request = PcSheetV11ExportRequest(
                PcSheetV11Variant.B, PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = true,
            ),
            sources = real,
        )
        preserveRealApplicationPdf(generated.bytes, "V11-INTERNAL-PROOF-Mara-B.pdf")
        assertTrue(generated.bytes.size > 1000)
        assertTrue(generated.bytes.copyOfRange(0, 5).decodeToString() == "%PDF-")
        assertEquals(real.permanent.sheet.id, generated.semantic.characterId)
        Loader.loadPDF(generated.bytes).use { pdf ->
            assertTrue(pdf.numberOfPages >= 7, "Mara must flow beyond mandatory P1/P2/P3/Notes pages.")
            assertTrue(pdf.numberOfPages < 23,
                "C2 must fill full continuation pages rather than repeating small P2 modules.")
            pdf.pages.forEach { page ->
                assertEquals(PDRectangle.LETTER.width, page.mediaBox.width)
                assertEquals(PDRectangle.LETTER.height, page.mediaBox.height)
                val logo = page.resources.xObjectNames
                    .map { page.resources.getXObject(it) }
                    .filterIsInstance<PDImageXObject>()
                    .singleOrNull { it.width == 1224 && it.height == 1584 }
                assertTrue(logo != null,
                    "Each physical page must contain the genuine owner-sheet logo image.")
            }
            val printed = PDFTextStripper().getText(pdf)
            assertTrue(printed.contains("Mara"), "This must render actual character data.")
            assertFalse(printed.contains("Personaje: Mara"),
                "The character name belongs in the page header, not repeated in P1.")
            val p1Text = PDFTextStripper().apply {
                startPage = 1
                endPage = 1
            }.getText(pdf)
            assertTrue(p1Text.contains("SALV."),
                "P1 must print the six physical saving throw annotations.")
            generated.semantic.attributes.take(6).forEach { ability ->
                assertTrue(p1Text.contains(ability.name),
                    "Missing visual P1 standard ability: " + ability.name)
            }
            assertTrue(printed.contains("P2 - Equipo y tesoro"))
            assertTrue(printed.contains("EQUIPO"))
            assertTrue(printed.contains("TESORO"))
            assertTrue(printed.contains("EQUIPO ESPECIAL"))
            assertTrue(printed.contains("C2 - Rasgos"))
            assertTrue(printed.contains("C2 - Equipo especial"))
            assertTrue(printed.contains("RASGOS"))
            assertTrue(printed.contains("Cobre"))
            assertTrue(printed.contains("Oro"))
            assertTrue(printed.contains("P3 - Trasfondo"))
            assertTrue(printed.contains("Libro de conjuros"))
            assertTrue(printed.contains("NOTAS - escritura manual"))
            assertTrue(printed.contains("PRUEBA INTERNA V11 - NO APROBADA"))
        }
    }

    @Test
    fun realAldrenProducesPdfWithNoInventedQuickSpellPageOrSpellbook() {
        val fixture = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val generated = DesktopPcSheetExportService().generateV11Proof(
            request = PcSheetV11ExportRequest(
                PcSheetV11Variant.A,
                PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = true,
            ),
            sources = fixture,
        )
        preserveRealApplicationPdf(generated.bytes, "V11-INTERNAL-PROOF-Aldren-A.pdf")
        assertFalse(generated.semantic.hasQuickSpellList)
        Loader.loadPDF(generated.bytes).use { pdf ->
            assertTrue(pdf.numberOfPages >= 4)
            val printed = PDFTextStripper().getText(pdf)
            assertTrue(printed.contains("Aldren"))
            val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
            assertTrue(p1.contains("ATAQUE / RANGO"))
            assertTrue(p1.contains("Espada larga"))
            assertTrue(p1.contains("Ballesta ligera"))
            assertTrue(p1.contains("LANZAMIENTO DE CONJUROS"),
                "The P1 spellcasting area is mandatory even without real spells.")
            assertTrue(p1.contains("ESPACIOS"))
            assertTrue(p1.contains("GASTADOS"))
            assertFalse(p1.contains("ESPACIOS REALES NIVEL 10"))
            assertFalse(printed.contains("P4 - Conjuros"))
            assertFalse(printed.contains("Libro de conjuros"))
            assertTrue(printed.contains("NOTAS - escritura manual"))
        }
    }
    @Test
    fun realIlyraSpellcasterProducesP4AndHandwrittenGridAsLastPage() {
        val fixture = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val generated = DesktopPcSheetExportService().generateV11Proof(
            request = PcSheetV11ExportRequest(
                PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = true,
            ),
            sources = fixture,
        )
        preserveRealApplicationPdf(generated.bytes, "V11-INTERNAL-PROOF-Ilyra-B.pdf")
        assertTrue(generated.semantic.hasQuickSpellList)
        Loader.loadPDF(generated.bytes).use { pdf ->
            val printed = PDFTextStripper().getText(pdf)
            assertTrue(printed.contains("P4 - Conjuros"))
            val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
            assertTrue(p1.contains("Daga"), "P1 real attack grid lost Ilyra's dagger.")
            assertTrue(p1.contains("Fire Bolt"), "P1 real attack grid lost her ranged cantrip.")
            assertTrue(p1.contains("LANZAMIENTO DE CONJUROS"))
            assertTrue(p1.contains("GASTADOS"))
            for (level in 0..9) {
                assertTrue(printed.contains("NIVEL " + level),
                    "P4 must reserve all non-epic level groups 0–9, including empty ones.")
            }
            assertTrue(printed.contains("Fire Bolt"),
                "Genuine Ilyra spell missing in the real exported P4.")
            assertTrue(printed.contains("Libro de conjuros"))
            val notesOnly = PDFTextStripper().apply {
                startPage = pdf.numberOfPages
                endPage = pdf.numberOfPages
            }.getText(pdf)
            assertTrue(notesOnly.contains("NOTAS - escritura manual"))
            assertFalse(notesOnly.contains("C-N3 - Notas digitales"))
            assertFalse(notesOnly.contains("Libro de conjuros"))
            assertFalse(notesOnly.contains("P4 - Conjuros"))
        }
    }

    @Test
    fun portraitModesEmbedLocallyLoadedImageAndNoImageStillReservesSilhouette() {
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val withPortrait = original.copy(
            permanent = original.permanent.copy(
                closure = original.permanent.closure.copy(portraitRef = "qa:local-portrait"),
            ),
        )
        val image = BufferedImage(72, 110, BufferedImage.TYPE_INT_RGB)
        val graphics = image.createGraphics()
        try {
            graphics.color = Color(55, 95, 137)
            graphics.fillRect(0, 0, image.width, image.height)
            graphics.color = Color(250, 220, 182)
            graphics.fillRect(0, 0, 17, image.height)
        } finally { graphics.dispose() }
        val png = ByteArrayOutputStream().use { buffer ->
            check(ImageIO.write(image, "png", buffer))
            buffer.toByteArray()
        }
        val service = DesktopPcSheetExportService(
            portraitBytesLoader = { ref -> if (ref == "qa:local-portrait") png else null },
        )
        for (fit in listOf(PcSheetPortraitFitMode.CROP_TO_FILL,
            PcSheetPortraitFitMode.FIT_ENTIRE_IMAGE)) {
            val proof = service.generateV11Proof(
                PcSheetV11ExportRequest(PcSheetV11Variant.A,
                    PcSheetExportStateSelection.PERMANENT,
                    portraitFitMode = fit),
                withPortrait,
            )
            assertEquals(fit, proof.semantic.portrait.fitMode)
            assertTrue(proof.semantic.portrait.locallyAvailable,
                "The App's local bytes must inform the exported portrait plan.")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val images = pdf.getPage(0).resources.xObjectNames
                    .map { pdf.getPage(0).resources.getXObject(it) }
                    .filterIsInstance<PDImageXObject>()
                assertTrue(images.any { it.width == 72 && it.height == 110 },
                    "P1 must embed the real local portrait, not a placeholder.")
                val first = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                assertTrue(first.contains("RETRATO"))
            }
        }
        val absent = DesktopPcSheetExportService(
            portraitBytesLoader = { null },
        ).generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A,
                PcSheetExportStateSelection.PERMANENT),
            withPortrait,
        )
        assertFalse(absent.semantic.portrait.locallyAvailable)
        Loader.loadPDF(absent.bytes).use { pdf ->
            val images = pdf.getPage(0).resources.xObjectNames
                .map { pdf.getPage(0).resources.getXObject(it) }
                .filterIsInstance<PDImageXObject>()
            assertFalse(images.any { it.width == 72 && it.height == 110 })
            assertTrue(PDFTextStripper().apply {
                startPage = 1
                endPage = 1
            }.getText(pdf).contains("RETRATO"))
        }
    }

}
