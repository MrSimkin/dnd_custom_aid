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
import kotlin.uuid.Uuid
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

    private fun assertAllPhysicalBookReferences(
        pdf: org.apache.pdfbox.pdmodel.PDDocument,
        names: List<String>,
    ) {
        val pageTexts = (1..pdf.numberOfPages).map { page ->
            PDFTextStripper().apply {
                startPage = page
                endPage = page
            }.getText(pdf)
        }
        val rows = pageTexts.filter { it.contains("Índice del Libro de Conjuros") }
            .flatMap { page ->
                page.lineSequence().mapNotNull { line ->
                    val match = Regex("""NIVEL\s+\d+\s*\|\s*(.+?)\s*\|\s*PÁGINA\s+(\d+)""")
                        .find(line)
                    match?.let { it.groupValues[1] to it.groupValues[2].toInt() }
                }
            }
        assertEquals(names.size, rows.size,
            "The physical book index must list every genuine spell exactly once.")
        assertEquals(names.sorted(), rows.map { it.first }.sorted())
        rows.forEach { (name, physicalPage) ->
            assertTrue(physicalPage in 1..pdf.numberOfPages)
            assertTrue(pageTexts[physicalPage - 1].contains(name),
                "Index sends real spell $name to the wrong PDF page $physicalPage.")
        }
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
            assertTrue(printed.contains("C2 - Habilidades, acciones y recursos"))
            assertTrue(printed.contains("Reserva 1:"))
            assertTrue(printed.contains("Reserva 10:"))
            assertTrue(printed.contains("RECURSOS Y MUNICIONES"))
            assertFalse(printed.contains("C2 - Recursos y municiones"),
                "Mixed C2 must not spend a dedicated physical page per module.")
            assertTrue(printed.contains("RESTANTE 0/7"),
                "Source resource balance zero is a real value, not absence.")
            assertTrue(printed.contains("RECUPERA"),
                "Mara's persisted resource recovery must survive PDF generation.")
            assertTrue(printed.contains("P2 - Equipo y tesoro"))
            val pagesByPhysicalNumber = (1..pdf.numberOfPages).map { number ->
                PDFTextStripper().apply {
                    startPage = number
                    endPage = number
                }.getText(pdf)
            }
            assertTrue(pagesByPhysicalNumber.first().contains(
                "HOJA 1 / " + pdf.numberOfPages,
            ), "The footer must resolve physical page count only after final pagination.")
            fun realHeading(page: String, heading: String) =
                page.lineSequence().any { line -> line.trim().startsWith(heading) }
            val p1Physical = pagesByPhysicalNumber.indexOfFirst {
                realHeading(it, "P1 - Personaje")
            } + 1
            val p2Physical = pagesByPhysicalNumber.indexOfFirst {
                realHeading(it, "P2 - Equipo y tesoro")
            } + 1
            val mixedC2Physical = pagesByPhysicalNumber.indexOfFirst {
                realHeading(it, "C2 - Habilidades, acciones y recursos")
            } + 1
            val p2C2Physical = pagesByPhysicalNumber.indexOfFirst {
                realHeading(it, "C2 - Equipo y tesoro") ||
                    realHeading(it, "C2 - Rasgos / continuación") ||
                    realHeading(it, "C2 - Equipo especial / continuación")
            } + 1
            assertTrue(p1Physical > 0 && p2Physical > 0 &&
                mixedC2Physical > 0 && p2C2Physical > 0)
            assertTrue(pagesByPhysicalNumber[p1Physical - 1].contains(
                "CONTINÚA EN C2 PÁG. " + mixedC2Physical,
            ), "P1 must point to C2 with skills/actions/resources.")
            assertTrue(pagesByPhysicalNumber[mixedC2Physical - 1].contains(
                "VIENE DE P1 PÁG. " + p1Physical,
            ), "Mixed C2 must link to the true physical P1, never P2.")
            assertFalse(pagesByPhysicalNumber[mixedC2Physical - 1].contains(
                "VIENE DE P2 PÁG."),
                "A continuation of P1 must not claim P2 as its parent.")
            assertTrue(pagesByPhysicalNumber[p2Physical - 1].contains(
                "CONTINÚA EN C2 PÁG. " + p2C2Physical,
            ), "P2 must point to its OWN first continuation, not mixed P1 C2.")
            assertTrue(pagesByPhysicalNumber[p2C2Physical - 1].contains(
                "VIENE DE P2 PÁG. " + p2Physical,
            ), "The P2-owned C2 must link to the actual physical P2.")
            assertFalse(pagesByPhysicalNumber.last().contains("HOJA "),
                "The mandatory final handwritten page has no digital footer.")
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
            assertAllPhysicalBookReferences(pdf, generated.semantic.spells.map { it.spell.name })
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
            assertTrue(p1.contains("HABILIDADES BAJO ATRIBUTO"),
                "Variant A must place matching built-in skills physically under attributes.")
            assertTrue(p1.contains("ATHLETICS"),
                "Athletics should remain grouped beneath STR.")
            assertTrue(p1.contains("ACROBATICS"),
                "Acrobatics should remain grouped beneath DEX.")
            assertFalse(p1.contains("Habilidad ATHLETICS"),
                "Variant A must not duplicate grouped skills as free-floating prose.")
            assertTrue(p1.contains("Espada larga"))
            assertTrue(p1.contains("Ballesta ligera"))
            assertTrue(p1.contains("LANZAMIENTO DE CONJUROS"),
                "The P1 spellcasting area is mandatory even without real spells.")
            assertTrue(p1.contains("ESPACIOS"))
            assertTrue(p1.contains("GASTADOS"))
            assertTrue(printed.contains("C2 - Habilidades, acciones y recursos"))
            assertTrue(printed.contains("Second Wind"))
            assertTrue(printed.contains("RECURSOS Y MUNICIONES"))
            assertTrue(printed.contains("Action Surge"))
            assertTrue(printed.contains("MUNICIÓN Virotes"))
            assertTrue(printed.contains("CANTIDAD 20"))
            assertTrue(printed.contains("MARCAS:"))
            val resourcePageText = (1..pdf.numberOfPages).map { physicalPage ->
                PDFTextStripper().apply {
                    startPage = physicalPage
                    endPage = physicalPage
                }.getText(pdf)
            }.firstOrNull { it.contains("C2 - Habilidades, acciones y recursos") }
            assertTrue(resourcePageText != null,
                "The actual export must contain a C2 resource page.")
            assertTrue(resourcePageText.contains("NOTAS"),
                "Unused C2 space must be available for manuscript notes without adding pages.")
            assertFalse(p1.contains("ESPACIOS REALES NIVEL 10"))
            assertFalse(printed.contains("P4 - Conjuros"))
            assertFalse(printed.contains("Libro de conjuros"))
            assertFalse(printed.contains("Índice del Libro de Conjuros"),
                "Non-caster must not receive a fabricated spellbook index.")
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
            assertFalse(p1.contains("HABILIDADES BAJO ATRIBUTO"),
                "Variant B must remain distinct from the attribute-grouped A layout.")
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
            assertTrue(printed.contains("Índice del Libro de Conjuros"))
            assertAllPhysicalBookReferences(pdf, generated.semantic.spells.map { it.spell.name })
            assertTrue(printed.contains("LANZAMIENTO"))
            assertTrue(printed.contains("COMPONENTES"))
            assertTrue(printed.contains("DURACIÓN"))
            assertTrue(printed.contains("DESCRIPCIÓN:"))
            // Index page references must point at the REAL physical PDF page,
            // not local section offsets or guesses.
            val bookIndexText = (1..pdf.numberOfPages).asSequence().map { number ->
                PDFTextStripper().apply { startPage = number; endPage = number }
                    .getText(pdf)
            }.first { it.contains("Índice del Libro de Conjuros") }
            val fireBoltLine = bookIndexText.lineSequence()
                .first { it.contains("Fire Bolt") && it.contains("PÁGINA") }
            val targetPhysicalPage = requireNotNull(
                Regex("""PÁGINA\s+(\d+)""").find(fireBoltLine),
            ).groupValues[1].toInt()
            assertTrue(targetPhysicalPage in 1..pdf.numberOfPages)
            val targetContent = PDFTextStripper().apply {
                startPage = targetPhysicalPage
                endPage = targetPhysicalPage
            }.getText(pdf)
            assertTrue(targetContent.contains("Fire Bolt"))
            assertTrue(targetContent.contains("DESCRIPCIÓN:"))
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

    @Test
    fun ammunitionAboveTwentyUsesExplicitFractionAndNeverInventsTwentyCheckboxes() {
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val ammoId = original.permanent.closure.inventoryUsage
            .single { it.kind ==
                io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind.AMMUNITION
            }.itemId
        val amended = original.copy(
            permanent = original.permanent.copy(
                sheet = original.permanent.sheet.copy(
                    inventoryItems = original.permanent.sheet.inventoryItems.map {
                        if (it.id == ammoId) it.copy(quantity = 41) else it
                    },
                ),
            ),
        )
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A,
                PcSheetExportStateSelection.PERMANENT),
            amended,
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val text = PDFTextStripper().getText(pdf)
            assertTrue(text.contains("C2 - Habilidades, acciones y recursos"))
            assertTrue(text.contains("MUNICIÓN Virotes"))
            assertTrue(text.contains("CANTIDAD 41"))
            assertTrue(text.contains("MARCAS: ____ / 41"),
                "Large ammunition quantities must be tracked with ____ / X.")
            assertFalse(text.contains("MARCAS: ____ / 20"))
        }
    }

    @Test
    fun nonAmmunitionInventoryIsNeverPromotedToTrackedAmmunitionFromItsName() {
        val fixture = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val untagged = fixture.copy(
            permanent = fixture.permanent.copy(
                closure = fixture.permanent.closure.copy(
                    inventoryUsage = fixture.permanent.closure.inventoryUsage.map {
                        it.copy(kind =
                            io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind.NONE)
                    },
                ),
            ),
        )
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A,
                PcSheetExportStateSelection.PERMANENT),
            untagged,
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val text = PDFTextStripper().getText(pdf)
            assertTrue(text.contains("Virotes"),
                "The original equipment still belongs in the ordinary inventory.")
            assertFalse(text.contains("MUNICIÓN Virotes"),
                "Only persisted AMMUNITION classification may create marks.")
            assertTrue(text.contains("Second Wind"), "Resources must not be removed.")
        }
    }

    @Test
    fun overlongRealSpellCardContinuesLosslesslyAfterPhysicalIndexPage() {
        val source = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val real = source.permanent.sheet.spells
        val longDescription = "COMIENZO-EXTENSO " +
            ("El umbral contiene marcas originales y una duración verificable. ").repeat(240) +
            " FIN-EXTENSO-VERIFICABLE"
        val adjusted = source.copy(
            permanent = source.permanent.copy(
                sheet = source.permanent.sheet.copy(
                    spells = real.mapIndexed { index, item ->
                        if (index == 0) item.copy(description = longDescription) else item
                    },
                ),
            ),
        )
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(
                PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = true,
            ),
            adjusted,
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val sections = (1..pdf.numberOfPages).map { physical ->
                PDFTextStripper().apply {
                    startPage = physical
                    endPage = physical
                }.getText(pdf)
            }
            assertTrue(sections.any { it.contains("Índice del Libro de Conjuros") })
            assertTrue(sections.count {
                it.contains("Libro de conjuros /") &&
                    !it.contains("Índice del Libro de Conjuros")
            } >= 2, "A single taller-than-page card must span actual PDF book pages.")
            assertTrue(sections.any { it.contains("COMIENZO-EXTENSO") })
            assertTrue(sections.any { it.contains("FIN-EXTENSO-VERIFICABLE") },
                "The final spell text cannot be omitted when the card overflows.")
            assertTrue(sections.last().contains("NOTAS - escritura manual"),
                "Handwritten notes must remain the physical last page.")
        }
    }

    @Test
    fun disabledOptionalSpellbookNeverEmitsBookOrPhysicalIndex() {
        val source = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(
                PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = false,
            ),
            source,
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val text = PDFTextStripper().getText(pdf)
            assertTrue(text.contains("P4 - Conjuros"),
                "P4 remains conditional on real spells, not on the optional book.")
            assertFalse(text.contains("Índice del Libro de Conjuros"))
            assertFalse(text.contains("Libro de conjuros"))
            assertTrue(text.contains("NOTAS - escritura manual"))
        }
    }

    @Test
    fun digitalNotesHaveDedicatedPagesAndDisappearWhenSourceNotesAreAbsent() {
        val actual = sources("03_mara_siete_umbrales_custom_extended.json")
        val saved = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = false),
            actual,
        )
        Loader.loadPDF(saved.bytes).use { pdf ->
            val pages = (1..pdf.numberOfPages).map { n ->
                PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
            }
            val notePageNumbers = pages.indices.filter {
                pages[it].contains("C-N3 - Notas digitales")
            }
            assertTrue(notePageNumbers.isNotEmpty())
            assertTrue(notePageNumbers.any { pages[it].contains("Nota 1") })
            assertTrue(notePageNumbers.any { pages[it].contains("Nota 9") })
            assertTrue(pages.filterIndexed { idx, _ -> idx !in notePageNumbers }
                .none { it.contains("Nota 1 — Hipótesis") },
                "Digital notes must not leak into mechanical C2 or the last manuscript page.")
            assertTrue(pages.last().contains("NOTAS - escritura manual"))
            assertFalse(pages.last().contains("C-N3"))
        }
        val withoutNotes = actual.copy(
            permanent = actual.permanent.copy(
                sheet = actual.permanent.sheet.copy(
                    generalNotes = "",
                    noteCards = emptyList(),
                ),
            ),
        )
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = false),
            withoutNotes,
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val printed = PDFTextStripper().getText(pdf)
            assertFalse(printed.contains("C-N3 - Notas digitales"),
                "Do not create phantom digital-notes pages when the source is empty.")
            val last = PDFTextStripper().apply {
                startPage = pdf.numberOfPages
                endPage = pdf.numberOfPages
            }.getText(pdf)
            assertTrue(last.contains("NOTAS - escritura manual"),
                "The final manuscript page must remain even without digital n
    @Test
    fun overflowingOrdinaryInventoryUsesFullHeightC2WithoutRepeatingAnyAmount() {
        val existing = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val original = existing.permanent.sheet.inventoryItems.first { !it.special }
        val added = (0 until 100).map { index ->
            original.copy(
                id = Uuid.parse("0c000000-0000-4000-8000-" +
                    (index + 1).toString().padStart(12, '0')),
                name = "MATERIAL INVENTARIADO " + (index + 1).toString().padStart(3, '0') +
                    " con nombre descriptivo largo para verificar continuidad de la tabla",
                quantity = 7000 + index,
                sortOrder = 2000 + index,
                special = false,
                notes = null,
            )
        }
        val fixture = existing.copy(
            permanent = existing.permanent.copy(
                sheet = existing.permanent.sheet.copy(
                    inventoryItems = existing.permanent.sheet.inventoryItems + added,
                ),
            ),
        )
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(
                PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT,
            ), fixture,
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val physicalTexts = (1..pdf.numberOfPages).map { physical ->
                PDFTextStripper().apply {
                    startPage = physical
                    endPage = physical
                }.getText(pdf)
            }
            val continuation = physicalTexts.filter {
                it.contains("C2 - Equipo y tesoro / EQUIPO")
            }
            assertTrue(continuation.isNotEmpty(),
                "Overlong equipment must create a physical full-height C2 paired table.")
            assertTrue(continuation.any { it.contains("MATERIAL INVENTARIADO") })
            val all = physicalTexts.joinToString("\n")
            for (index in 0 until 100) {
                val sourceName = "MATERIAL INVENTARIADO " +
                    (index + 1).toString().padStart(3, '0')
                assertTrue(all.contains(sourceName),
                    "The original inventory item name must survive P2/C2: $sourceName")
                val number = (7000 + index).toString()
                assertEquals(1, Regex("(?<![0-9])" + number + "(?![0-9])")
                    .findAll(all).count(),
                    "One inventory amount must print exactly once, even when the " +
                        "corresponding name begins on P2 and continues in C2.")
            }
            assertTrue(physicalTexts.last().contains("NOTAS - escritura manual"))
        }
    }
otes.")
        }
    }

}
