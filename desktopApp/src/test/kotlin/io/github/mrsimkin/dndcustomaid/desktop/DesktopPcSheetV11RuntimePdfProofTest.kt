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
import org.apache.pdfbox.text.PDFTextStripperByArea
import java.awt.geom.Rectangle2D
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

    private fun assertPhysicalP1SavingBoxes(
        pdf: org.apache.pdfbox.pdmodel.PDDocument,
        expectedRows: Int,
    ) {
        val operators = pdf.getPage(0).contents.use {
            it.readBytes().toString(Charsets.ISO_8859_1)
        }
        // Must be genuine 7pt VECTOR squares in the P1 left saving band,
        // not plain "[ ]" glyphs or unrelated equipment/P4 marks.
        val boxes = Regex("""(?m)(?:^|\s)270\s+[0-9.]+\s+7\s+7\s+re(?:\s|$)""")
            .findAll(operators).count()
        assertEquals(expectedRows, boxes,
            "Each actually printed P1 standard/custom saving row must get " +
                "exactly one blank vector paper checkbox.")
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
    fun mixedC2UsesPhysicalBlueGroupCartelasWithoutChangingSourceOrPagination() {
        // This is the genuine Desktop App exporter, not a synthetic layout.
        // Mixed C2 already has measured two-column source cells; the approved
        // blue interior bands must occupy THOSE cells, without new pages.
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT),
            sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json"),
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val mixedPage = (1..pdf.numberOfPages).firstOrNull { physical ->
                PDFTextStripper().apply {
                    startPage = physical
                    endPage = physical
                }.getText(pdf).contains("C2 - Habilidades, acciones y recursos")
            } ?: error("Source-backed mixed C2 disappeared")
            val page = pdf.getPage(mixedPage - 1)
            val text = PDFTextStripper().apply {
                startPage = mixedPage
                endPage = mixedPage
            }.getText(pdf)
            listOf(
                "ATAQUES, NOTAS Y ESPACIOS REALES",
                "RECURSOS Y MUNICIONES",
                "RASGOS / CONTINUACIÓN P2",
                "Potent Cantrip",
                "Arcane Recovery",
            ).forEach { label ->
                assertTrue(text.contains(label),
                    "C2 visual cartelas must not remove persisted text: " + label)
            }
            val commands = page.contents.use {
                it.readBytes().toString(Charsets.ISO_8859_1)
            }
            // Only a complete 258pt cartela in either measured C2 column
            // qualifies. The frame, manuscript grid, or a text glyph cannot
            // substitute for a physically FILLED interior heading band.
            val filledGroupBands = Regex(
                """(?m)(?:^|\s)(?:42|312)\s+[0-9.]+\s+258(?:\.0+)?\s+[0-9.]+\s+re\s+f(?:\s|$)"""
            ).findAll(commands).count()
            assertTrue(filledGroupBands >= 3,
                "Every Ilyra source heading needs an actual filled " +
                    "two-column C2 cartela: found " + filledGroupBands)
            assertTrue(text.contains("VIENE DE P1 PÁG. 1 / P2 PÁG. 2"),
                "C2 cartelas cannot disturb final physical source backlinks.")
        }
    }

    @Test
    fun variantBBottomPaperSpaceHasCompleteRuledRowsWithoutOverprintingSkills() {
        // Gold B's B-skill panel contains manuscript bands BELOW real
        // source-backed rows. Verify genuine vector operators in the PDF
        // produced by the application, not geometry of a mock document.
        for (fixture in listOf(
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json",
            "03_mara_siete_umbrales_custom_extended.json",
        )) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(PcSheetV11Variant.B,
                    PcSheetExportStateSelection.PERMANENT), sources(fixture),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val first = pdf.getPage(0)
                val commands = first.contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                for (y in listOf(205, 189, 173, 157, 141, 125)) {
                    for ((left,right) in listOf(47 to 167,176 to 295)) {
                        val line = Regex(
                            """(?m)(?:^|\s)""" + left + """\s+""" + y +
                                """\s+m\s+""" + right + """\s+""" + y +
                                """\s+l(?:\s|$)"""
                        )
                        assertEquals(1, line.findAll(commands).count(),
                            "Real B skills paper row y=$y x=$left..$right " +
                                "must be whole, unique and inside its own column.")
                        val bandWidth = right - left
                        val filledBand = Regex(
                            """(?m)(?:^|\s)""" + left + """\s+""" + (y-1) +
                                """\s+""" + bandWidth +
                                """\s+14\s+re(?:\s|$)"""
                        )
                        assertEquals(1, filledBand.findAll(commands).count(),
                            "Each writable B row must retain its complete " +
                                "inside-border pale-blue band without clipping.")
                    }
                }
                val blankText = PDFTextStripperByArea().apply {
                    // PDFBox uses top-origin region coordinates. The
                    // manuscript rows run PDF y=125..218.
                    addRegion("unused-B-paper",
                        Rectangle2D.Float(43f, 574f, 255f, 92f))
                    extractRegions(first)
                }.getTextForRegion("unused-B-paper")
                assertTrue(blankText.isBlank(),
                    "Source text cannot overlap the manuscript-only B panel.")
                val pageText = PDFTextStripper().apply {
                    startPage = 1; endPage = 1
                }.getText(pdf)
                assertTrue(pageText.contains("Acrobacias") &&
                    pageText.contains("Supervivencia"),
                    "B manuscript rules cannot remove real built-in skills.")
            }
        }
    }

    @Test
    fun variantBIndependentSkillsHaveRealBlankVectorPaperMarksInBothFixtures() {
        // Physical PDF proof from the actual Desktop App, not a synthetic
        // model. The golden B uses a separate TWO-column skills list, with one
        // writable square for every printed, source-backed built-in skill.
        for (fixture in listOf(
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json",
            "03_mara_siete_umbrales_custom_extended.json",
        )) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(PcSheetV11Variant.B,
                    PcSheetExportStateSelection.PERMANENT), sources(fixture),
            )
            Loader.loadPDF(generated.bytes).use { pdf ->
                val p1 = pdf.getPage(0)
                val commands = p1.contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                val firstColumnMarks = Regex(
                    """(?m)(?:^|\s)49(?:\.0+)?\s+[-0-9.]+\s+7\s+7\s+re(?:\s|$)"""
                ).findAll(commands).count()
                val secondColumnMarks = Regex(
                    """(?m)(?:^|\s)175(?:\.0+)?\s+[-0-9.]+\s+7\s+7\s+re(?:\s|$)"""
                ).findAll(commands).count()
                val standardNames = generated.semantic.skills.filter {
                    it.builtInKey != null
                }
                assertEquals(18, standardNames.size,
                    "Both SRD and custom fixture must preserve the 18 actual standard skills.")
                assertEquals(standardNames.size,
                    firstColumnMarks + secondColumnMarks,
                    "Each real B skill printed on P1 must have exactly one vector mark.")
                assertTrue(firstColumnMarks > 0 && secondColumnMarks > 0,
                    "B paper marks must occupy BOTH physical skill subcolumns.")
                val pageText = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                assertTrue(pageText.contains("HABILIDADES"))
                assertTrue(pageText.contains("Acrobacias"))
                assertTrue(pageText.contains("Supervivencia"))
                assertFalse(pageText.contains("STRENGTH"),
                    "Paper-only B marks must not revert approved Spanish P1 labels.")
            }
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
            val leftSavingRegion = PDFTextStripperByArea().apply {
                addRegion("standard-abilities",
                    Rectangle2D.Float(41f, 185f, 260f, 510f))
                extractRegions(pdf.getPage(0))
            }.getTextForRegion("standard-abilities")
            assertEquals(minOf(9, generated.semantic.attributes.size),
                Regex("SALVACIÓN").findAll(leftSavingRegion).count(),
                "Every P1 standard and fitting custom ability saving throw " +
                    "must occupy the LEFT vertical attribute band, not magic DC.")
            assertPhysicalP1SavingBoxes(pdf, minOf(9, generated.semantic.attributes.size))
            // Semantic enum IDs remain unmodified in source, while their
            // physically exported V11 P1 labels must follow the frozen Spanish
            // design. Both obligations are asserted, not substituted.
            val standardVisuals = listOf(
                "STRENGTH" to "FUERZA",
                "DEXTERITY" to "DESTREZA",
                "CONSTITUTION" to "CONSTITUCIÓN",
                "INTELLIGENCE" to "INTELIGENCIA",
                "WISDOM" to "SABIDURÍA",
                "CHARISMA" to "CARISMA",
            )
            generated.semantic.attributes.take(6)
                .zip(standardVisuals).forEach { (ability, expected) ->
                    assertEquals(expected.first, ability.name,
                        "V11 must not rewrite the persisted built-in ability source ID.")
                    assertTrue(p1Text.contains(expected.second),
                        "Missing owner-approved P1 standard ability: " + expected.second)
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
                (realHeading(it, "C2 - Habilidades, acciones y recursos") &&
                    it.contains("RASGOS / CONTINUACIÓN P2")) ||
                    realHeading(it, "C2 - Equipo y tesoro") ||
                    realHeading(it, "C2 - Equipo especial / continuación")
            } + 1
            assertTrue(p1Physical > 0 && p2Physical > 0 &&
                mixedC2Physical > 0 && p2C2Physical > 0)
            assertEquals(1, pagesByPhysicalNumber.count {
                realHeading(it, "P1 - Personaje")
            }, "P1 is exactly one physical page; its overflow belongs in C2.")
            val physicalMixedC2 = pagesByPhysicalNumber.filter {
                realHeading(it, "C2 - Habilidades, acciones y recursos")
            }.joinToString("\n")
            assertTrue(physicalMixedC2.contains("Cartografía imposible"),
                "Mara's overflowed custom skill must not be lost when P1 stays single.")
            assertTrue(physicalMixedC2.contains("Técnica 2"),
                "Mara's overflowed real combat action must remain in C2.")
            assertTrue(pagesByPhysicalNumber[p1Physical - 1].contains(
                "CONTINÚA EN C2 PÁG. " + mixedC2Physical,
            ), "P1 must point to C2 with skills/actions/resources.")
            assertTrue(pagesByPhysicalNumber[mixedC2Physical - 1].contains(
                "VIENE DE P1 PÁG. " + p1Physical,
            ), "Mixed C2 must link to the true physical P1, never P2.")
            if (pagesByPhysicalNumber[mixedC2Physical - 1]
                    .contains("RASGOS / CONTINUACIÓN P2")) {
                assertTrue(pagesByPhysicalNumber[mixedC2Physical - 1]
                    .contains("P2 PÁG. " + p2Physical),
                    "A mixed page with genuine P2 traits needs both source backlinks.")
            } else {
                assertFalse(pagesByPhysicalNumber[mixedC2Physical - 1]
                    .contains("VIENE DE P2 PÁG."),
                    "A P1-only mixed page must never invent a P2 provenance.")
            }
            assertTrue(pagesByPhysicalNumber[p2Physical - 1].contains(
                "CONTINÚA EN C2 PÁG. " + p2C2Physical,
            ), "P2 must point to its OWN first continuation, not mixed P1 C2.")
            assertTrue(pagesByPhysicalNumber[p2C2Physical - 1].contains(
                "P2 PÁG. " + p2Physical,
            ), "The first P2-owned C2 (dedicated OR mixed) must link to physical P2.")
            assertFalse(pagesByPhysicalNumber.last().contains("HOJA "),
                "The mandatory final handwritten page has no digital footer.")
            assertTrue(printed.contains("EQUIPO"))
            assertTrue(printed.contains("TESORO"))
            assertTrue(printed.contains("EQUIPO ESPECIAL"))
            val physicalP2 = pagesByPhysicalNumber[p2Physical - 1]
            for (column in listOf("UBICACIÓN", "NOMBRE", "DESCRIPCIÓN", "ACTIVO")) {
                assertTrue(physicalP2.contains(column),
                    "Golden P2 special equipment must use real labelled columns: " +
                        column)
            }
            assertTrue(printed.contains("RASGOS / CONTINUACIÓN P2"),
                "Deferred P2 traits must remain in the shared C2 manuscript flow.")
            assertTrue(printed.contains("C2 - Equipo especial"))
            val specialContinuation = pagesByPhysicalNumber.filter {
                realHeading(it, "C2 - Equipo especial / continuación")
            }
            assertTrue(specialContinuation.isNotEmpty())
            assertTrue(specialContinuation.all { it.contains("UBICACIÓN") &&
                it.contains("NOMBRE") && it.contains("DESCRIPCIÓN") &&
                it.contains("ACTIVO") },
                "Every physical special C2 must preserve the approved four columns.")
            assertTrue(specialContinuation.joinToString("\n")
                .contains("Astrolabio de cobre"),
                "Mara's deferred original special equipment must be present.")
            assertTrue(printed.contains("RASGOS"))
            assertTrue(printed.contains("Cobre"))
            assertTrue(printed.contains("Oro"))
            assertTrue(printed.contains("P3 - Trasfondo"))
            assertTrue(printed.contains("Libro de conjuros"))
            val p4Pages = (1..pdf.numberOfPages).map { physicalPage ->
                PDFTextStripper().apply {
                    startPage = physicalPage
                    endPage = physicalPage
                }.getText(pdf)
            }.filter { it.contains("P4 - Conjuros") }
            assertTrue(p4Pages.isNotEmpty(), "Real caster must have a P4.")
            assertTrue(p4Pages.first().contains("REFERENCIA MÁGICA"))
            assertTrue(p4Pages.first().contains("APTITUD"))
            assertTrue(p4Pages.first().contains("CD SALVACIÓN"))
            assertTrue(p4Pages.first().contains("ATAQUE MÁGICO"))
            assertFalse(p4Pages.joinToString("\n").contains("___"),
                "Blank P4 paper rows must be real vector lines, never dark underscore text.")
            val vectorSquares = (0 until pdf.numberOfPages).sumOf { physical ->
                val pageText = PDFTextStripper().apply {
                    startPage = physical + 1
                    endPage = physical + 1
                }.getText(pdf)
                if (!pageText.contains("P4 - Conjuros")) return@sumOf 0
                val contents = pdf.getPage(physical).contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                Regex("""(?m)(?:^|\s)7 7 re(?:\s|$)""").findAll(contents).count()
            }
            assertTrue(vectorSquares >= 20,
                "P4 paper-only checkbox marks must be real 7pt PDF rectangles.")
            assertAllPhysicalBookReferences(pdf, generated.semantic.spells.map { it.spell.name })
            assertTrue(printed.lineSequence().any { it.trim() == "NOTAS" })
            assertTrue(printed.contains("PRUEBA INTERNA V11 - NO APROBADA"))
        }
    }

    @Test
    fun physicalP1IdentityFieldsUsePersistedClassAndRaceWithBlankAlignment() {
        val fixture = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val sheet = fixture.permanent.sheet
        val updated = fixture.copy(permanent = fixture.permanent.copy(
            sheet = sheet.copy(
                background = sheet.background.copy(race = "Elfo de los bosques"),
            ),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val output = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                updated,
            )
            Loader.loadPDF(output.bytes).use { pdf ->
                val p1 = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                assertTrue(p1.contains("CLASE / NIVEL"),
                    "The P1 class belongs to an explicitly labelled physical field.")
                assertTrue(p1.contains("RAZA"))
                assertTrue(p1.contains("Elfo de los bosques"),
                    "Persisted race must be on the P1 field, not only in narrative.")
                assertTrue(p1.contains("ALINEAMIENTO"),
                    "The physical sheet reserves alignment for manual writing.")
                assertFalse(p1.contains("Estado seleccionado:"),
                    "Internal export-state diagnostics must not occupy character fields.")
                assertFalse(p1.contains("Variante:"),
                    "A/B is a layout choice, not a character attribute.")
                assertTrue(p1.contains(sheet.classes.first().name),
                    "The genuine class must remain visible in its identity cartela.")
            }
        }
    }

    @Test
    fun physicalP1UpperIdentityAndCombatAreSeparatePrintRegionsForBothVariants() {
        // Assert physical PDF coordinates, not just the presence of words
        // somewhere in a document whose pages may otherwise look different.
        val cases = listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )
        for ((fixtureName, variant) in cases) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources(fixtureName),
            ).bytes
            Loader.loadPDF(bytes).use { pdf ->
                val page = pdf.getPage(0)
                assertEquals(PDRectangle.LETTER.width, page.mediaBox.width)
                assertEquals(PDRectangle.LETTER.height, page.mediaBox.height)
                val regions = PDFTextStripperByArea()
                regions.addRegion("portrait", Rectangle2D.Float(40f, 76f, 111f, 125f))
                regions.addRegion("identity", Rectangle2D.Float(152f, 76f, 418f, 88f))
                regions.addRegion("attributes", Rectangle2D.Float(40f, 185f, 263f, 125f))
                regions.addRegion("combat", Rectangle2D.Float(309f, 185f, 264f, 111f))
                regions.extractRegions(page)
                val portrait = regions.getTextForRegion("portrait")
                val identity = regions.getTextForRegion("identity")
                val attributes = regions.getTextForRegion("attributes")
                val combat = regions.getTextForRegion("combat")
                assertTrue(portrait.contains("RETRATO"),
                    "Golden P1 top-left portrait moved out of its physical band: " + fixtureName)
                for (field in listOf("CLASE / NIVEL", "JUGADOR", "RAZA", "ALINEAMIENTO")) {
                    assertTrue(identity.contains(field),
                        "Golden P1 upper identity field absent at actual PDF coordinates: " + field)
                }
                assertFalse(identity.contains("FUERZA"),
                    "Attribute blocks must be physically BELOW the top identity band.")
                assertTrue(attributes.contains("FUERZA") && attributes.contains("DESTREZA"),
                    "First standard attribute labels must occupy the measured upper P1 ability band.")
                val p1Labels = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                for (approved in listOf(
                    "FUERZA", "DESTREZA", "CONSTITUCIÓN", "INTELIGENCIA",
                    "SABIDURÍA", "CARISMA",
                )) {
                    assertTrue(p1Labels.contains(approved),
                        "Persisted standard attribute must carry its golden P1 label: " + approved)
                }
                for (field in listOf("COMBATE Y SUPERVIVENCIA", "CA", "INICIATIVA",
                    "VELOCIDAD", "COMPETENCIA", "PV ACTUALES", "PV MÁXIMOS", "PV TEMP", "DADOS")) {
                    assertTrue(combat.contains(field),
                        "Persisted combat field outside dedicated physical panel: " + field)
                }
                assertFalse(combat.contains("ATAQUE / RANGO"),
                    "The combat card panel may not overlap the attack table below.")
                val split = PDFTextStripperByArea()
                split.addRegion("left",Rectangle2D.Float(40f,185f,261f,510f))
                split.addRegion("attacks",Rectangle2D.Float(309f,294f,264f,107f))
                split.addRegion("spells",Rectangle2D.Float(309f,499f,264f,142f))
                split.extractRegions(page)
                assertTrue(split.getTextForRegion("left").contains("FUERZA"))
                assertFalse(split.getTextForRegion("left").contains("ATAQUE / RANGO"))
                assertTrue(split.getTextForRegion("attacks").contains("ATAQUE / RANGO"))
                assertTrue(split.getTextForRegion("spells").contains("LANZAMIENTO DE CONJUROS"))
                val p1MechanicalRegions = PDFTextStripperByArea().apply {
                    addRegion("traits", Rectangle2D.Float(309f, 408f, 264f, 87f))
                    addRegion("ammo", Rectangle2D.Float(309f, 641f, 264f, 53f))
                    extractRegions(page)
                }
                assertTrue(p1MechanicalRegions.getTextForRegion("traits")
                    .contains("RASGOS Y ATRIBUTOS"), "P1 real two-column trait panel absent.")
                assertTrue(p1MechanicalRegions.getTextForRegion("ammo")
                    .contains("MUNICIONES / MARCAS"), "P1 ammunition paper band absent.")
                assertFalse(p1MechanicalRegions.getTextForRegion("traits")
                    .contains("ATAQUE / RANGO"), "P1 traits overlap attacks.")
                assertFalse(p1MechanicalRegions.getTextForRegion("ammo")
                    .contains("ESPACIOS"), "P1 ammunition overlaps spell slots.")
                if(variant==PcSheetV11Variant.B){
                    val skillsByColumn=PDFTextStripperByArea().apply{
                        addRegion("first",Rectangle2D.Float(47f,471f,121f,118f))
                        addRegion("second",Rectangle2D.Float(173f,471f,123f,118f))
                        extractRegions(page)
                    }
                    assertTrue(skillsByColumn.getTextForRegion("first").contains("Acrobacias"),
                        "B skills first subcolumn must contain real Acrobatics.")
                    assertTrue(skillsByColumn.getTextForRegion("second").contains("Supervivencia"),
                        "B skills second subcolumn must contain real Survival.")
                }
            }
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
            val p2 = PDFTextStripper().apply {
                startPage = 2
                endPage = 2
            }.getText(pdf)
            for (heading in listOf("UBICACIÓN", "NOMBRE", "DESCRIPCIÓN", "ACTIVO")) {
                assertTrue(p2.contains(heading),
                    "Golden special equipment must expose a physical column " + heading)
            }
            assertTrue(p2.contains("Cota de malla"),
                "Short persisted special equipment names belong in P2 rows.")
            assertTrue(p2.contains("Cuerpo"),
                "The item's real physical location must appear in the same table.")
            assertTrue(p2.contains("CA base 16."),
                "The real description must remain visible, not vanish into layout.")
            assertTrue(p1.contains("ATAQUE / RANGO"))
            assertTrue(p1.contains("COMBATE Y SUPERVIVENCIA"))
            assertTrue(p1.contains("PV TEMP"))
            assertTrue(p1.contains("INICIATIVA"))
            assertTrue(p1.contains("COMPETENCIA"))
            assertTrue(p1.contains("JUGADOR"))
            assertTrue(p1.contains("HABILIDADES BAJO ATRIBUTO"),
                "Variant A must place matching built-in skills physically under attributes.")
            assertTrue(p1.contains("Atletismo"),
                "Athletics should remain grouped beneath STR.")
            assertTrue(p1.contains("Acrobacias"),
                "Acrobatics should remain grouped beneath DEX.")
            assertFalse(p1.contains("Habilidad Atletismo"),
                "Variant A must not duplicate grouped skills as free-floating prose.")
            assertTrue(p1.contains("Espada larga"))
            assertTrue(p1.contains("Ballesta ligera"))
            assertTrue(p1.contains("LANZAMIENTO DE CONJUROS"),
                "The P1 spellcasting area is mandatory even without real spells.")
            assertTrue(p1.contains("MUNICIONES / MARCAS:"))
            assertTrue(p1.contains("Virotes"),
                "Persisted classified ammunition must be present physically on Aldren P1.")
            assertTrue(p1.contains("RASGOS Y ATRIBUTOS"))
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
            assertTrue(printed.lineSequence().any { it.trim() == "NOTAS" })
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
            assertTrue(p1.contains("HABILIDADES  P=COMPETENCIA  E=PERICIA"),
                "B skills must occupy a named physical module, not identity prose.")
            assertTrue(p1.contains("Acrobacias (DES)"),
                "Persisted B skills must appear with their actual ability abbreviation.")
            assertFalse(p1.contains("Habilidad Acrobacias"),
                "B built-in skills must no longer print as diagnostic identity prose.")
            assertTrue(p1.contains("Daga"), "P1 real attack grid lost Ilyra's dagger.")
            assertTrue(p1.contains("Fire Bolt"), "P1 real attack grid lost her ranged cantrip.")
            assertTrue(p1.contains("LANZAMIENTO DE CONJUROS"))
            assertTrue(p1.contains("COMBATE Y SUPERVIVENCIA"))
            assertTrue(p1.contains("INICIATIVA"))
            assertTrue(p1.contains("PV MÁXIMOS"))
            assertTrue(p1.contains("JUGADOR"))
            assertTrue(p1.contains("GASTADOS"))
            for (level in 0..9) {
                assertTrue(printed.contains("NIVEL " + level),
                    "P4 must reserve all non-epic level groups 0–9, including empty ones.")
            }
            assertTrue(printed.contains("Fire Bolt"),
                "Genuine Ilyra spell missing in the real exported P4.")
            val p4Pages = (1..pdf.numberOfPages).map { physicalPage ->
                PDFTextStripper().apply {
                    startPage = physicalPage
                    endPage = physicalPage
                }.getText(pdf)
            }.filter { it.contains("P4 - Conjuros") }
            assertTrue(p4Pages.isNotEmpty(), "Real caster must have a P4.")
            assertTrue(p4Pages.first().contains("REFERENCIA MÁGICA"))
            assertTrue(p4Pages.first().contains("APTITUD"))
            assertTrue(p4Pages.first().contains("CD SALVACIÓN"))
            assertTrue(p4Pages.first().contains("ATAQUE MÁGICO"))
            assertFalse(p4Pages.joinToString("\n").contains("___"),
                "Blank P4 paper rows must be real vector lines, never dark underscore text.")
            val vectorSquares = (0 until pdf.numberOfPages).sumOf { physical ->
                val pageText = PDFTextStripper().apply {
                    startPage = physical + 1
                    endPage = physical + 1
                }.getText(pdf)
                if (!pageText.contains("P4 - Conjuros")) return@sumOf 0
                val contents = pdf.getPage(physical).contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                Regex("""(?m)(?:^|\s)7 7 re(?:\s|$)""").findAll(contents).count()
            }
            assertTrue(vectorSquares >= 20,
                "P4 paper-only checkbox marks must be real 7pt PDF rectangles.")
            assertTrue(printed.contains("Libro de conjuros"))
            val c2SpecialText = (1..pdf.numberOfPages).map { physical ->
                PDFTextStripper().apply {
                    startPage = physical
                    endPage = physical
                }.getText(pdf)
            }.filter { it.contains("C2 - Equipo especial / continuación") }
            assertTrue(c2SpecialText.isNotEmpty())
            assertTrue(c2SpecialText.all { it.contains("UBICACIÓN") &&
                it.contains("DESCRIPCIÓN") && it.contains("ACTIVO") })
            assertTrue(c2SpecialText.joinToString("\n").contains("Spellbook"),
                "Ilyra's long persisted special object must survive C2 table layout.")
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
            assertTrue(notesOnly.lineSequence().any { it.trim() == "NOTAS" })
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
    fun physicalC2KeepsPersistedCustomAttributeSkillsTogetherWithoutDuplicateDetachedCopies() {
        // Mara has 4 persisted extra abilities and 3 explicitly linked skills.
        // This is a real app-PDF regression, independent of the string-only mapper.
        val real = sources("03_mara_siete_umbrales_custom_extended.json")
        val linked = listOf(
            "Fortuna" to "Contratos arcanos",
            "Cordura" to "Sincronía",
            "Éter" to "Manipulación de éter",
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val pdfBytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                real,
            ).bytes
            // Retain the full real-Mara A App-generated PDF for P1/C2
            // independent font, box, position and duplication inspection.
            if (variant == PcSheetV11Variant.A) {
                preserveRealApplicationPdf(pdfBytes, "V11-INTERNAL-PROOF-Mara-A.pdf")
            }
            Loader.loadPDF(pdfBytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { physical ->
                    PDFTextStripper().apply {
                        startPage = physical
                        endPage = physical
                    }.getText(pdf)
                }
                val full = pages.joinToString("\n")
                for ((attribute, skill) in linked) {
                    val key = "Habilidad " + skill
                    assertTrue(full.contains(attribute),
                        "Custom attribute must survive in C2: " + attribute)
                    // Variant A prints a fitting skill directly under its
                    // attribute, without the C2-only "Habilidad" prefix.
                    // Test the source name exactly once across BOTH routes.
                    assertTrue(full.contains(skill),
                        "Persisted dependent custom skill must survive P1 or C2: " + skill)
                    assertEquals(1, Regex(Regex.escape(skill)).findAll(full).count(),
                        "Linked skill must appear exactly once in the real PDF: " + skill)
                    if (variant == PcSheetV11Variant.B) {
                        assertTrue(pages[0].contains(attribute),
                            "Golden B requires fitting custom attribute on physical P1: " +
                                attribute)
                        assertTrue(pages.any { text -> text.contains("C2 - ") &&
                            text.contains(key + " (" + attribute + ")") },
                            "The C2 skill must preserve its P1 custom ability ID relation.")
                    } else if (pages[0].contains(attribute)) {
                        assertTrue(pages[0].contains(skill),
                            "Fitting A custom abilities must keep their associated skill below.")
                        assertFalse(pages.drop(1).joinToString("\n").contains(key),
                            "An A skill already printed in P1 must not repeat in C2.")
                    } else {
                        assertTrue(pages.any { text -> text.contains("C2 - ") &&
                            text.contains(attribute) && text.contains(key) },
                            "Unfitted A source attribute and skill must survive together C2.")
                    }
                }
            }
        }
    }


    @Test
    fun measuredVariantAPlacesOnlyCompleteCustomAttributeAndSkillGroupsOnP1() {
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        // Preserve the real Mara base character, two genuine custom attributes,
        // and their two actual successor-linked custom skills. This 6+2 case
        // isolates geometric capacity without inventing fictitious attr states.
        val attrs = original.permanent.successor.customAttributes.take(2)
        val skills = original.permanent.closure.customSkills.filter {
            it.name == "Contratos arcanos" || it.name == "Sincronía"
        }
        val ids = skills.map { it.id }.toSet()
        val reduced = original.copy(
            permanent = original.permanent.copy(
                closure = original.permanent.closure.copy(customSkills = skills),
                successor = original.permanent.successor.copy(
                    customAttributes = attrs,
                    customSkillAbilities = original.permanent.successor.customSkillAbilities
                        .filter { it.customSkillId in ids },
                ),
            ),
        )
        val bytes = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A,
                PcSheetExportStateSelection.PERMANENT), reduced,
        ).bytes
        Loader.loadPDF(bytes).use { pdf ->
            val first = PDFTextStripper().apply {
                startPage = 1
                endPage = 1
            }.getText(pdf)
            assertTrue(first.contains("Fortuna") && first.contains("Cordura"),
                "A's genuine 6+2 attribute rows must fit only with measured group heights.")
            assertPhysicalP1SavingBoxes(pdf, 8)
            assertTrue(first.contains("Contratos arcanos") && first.contains("Sincronía"),
                "A custom-linked skills must stay physically beneath their P1 attributes.")
            val c2 = (2..pdf.numberOfPages).map { physical ->
                PDFTextStripper().apply {
                    startPage = physical
                    endPage = physical
                }.getText(pdf)
            }.filter { it.contains("C2 - Habilidades, acciones y recursos") }
                .joinToString("\n")
            assertFalse(c2.contains("Habilidad Contratos arcanos") ||
                c2.contains("Habilidad Sincronía"),
                "No source custom skill already printed on P1 may be duplicated in C2.")
        }
    }

    @Test
    fun realMaraBPhysicallyPrintsThreeFittingCustomAttributesAndDefersTheFourth() {
        val real = sources("03_mara_siete_umbrales_custom_extended.json")
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT), real,
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
            for (name in listOf("Fortuna", "Cordura", "Éter")) {
                assertTrue(p1.contains(name), "Golden B P1 custom attribute absent: " + name)
            }
            assertFalse(p1.contains("Renombre"),
                "A fourth custom attribute must not intrude on B's skills panel.")
            val c2 = (2..pdf.numberOfPages).map { physical ->
                PDFTextStripper().apply {
                    startPage = physical
                    endPage = physical
                }.getText(pdf)
            }.filter { it.contains("C2 - Habilidades, acciones y recursos") }
                .joinToString("\n")
            assertTrue(c2.contains("Renombre"), "Unfitted fourth ability must survive C2.")
            for (name in listOf("Fortuna", "Cordura", "Éter")) {
                assertFalse(Regex(Regex.escape(name) + """\s+\d+\s+/\s+mod""")
                    .containsMatchIn(c2),
                    "Already-printed custom attribute cannot repeat as a C2 score row.")
            }
            assertTrue(c2.contains("Contratos arcanos (Fortuna)"))
            assertTrue(c2.contains("Sincronía (Cordura)"))
            assertTrue(c2.contains("Manipulación de éter (Éter)"))
        }
    }

    @Test
    fun actualIlyraTraitsShareMeasuredC2WithResourcesInsteadOfWastePage() {
        val generated = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT),
            sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json"),
        )
        Loader.loadPDF(generated.bytes).use { pdf ->
            val pages = (1..pdf.numberOfPages).map { n ->
                PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
            }
            val mixed = pages.first { it.contains("C2 - Habilidades, acciones y recursos") }
            assertTrue(mixed.contains("RASGOS / CONTINUACIÓN P2"),
                "A short genuine P2 trait remainder must fit the existing mixed C2.")
            // The character also names Potent Cantrip in its attack notes
            // and P1 summary; count FULL source trait headings instead of
            // falsely equating every legitimate cross-reference to a duplicate.
            for (trait in listOf(
                "Potent Cantrip (Evoker",
                "Ability Score Improvement (Mago",
                "Memorize Spell (Mago",
            )) {
                assertEquals(1, Regex(Regex.escape(trait)).findAll(pages.joinToString("\n")).count(),
                    "Persisted trait source record is missing or duplicated: " + trait)
            }
            assertTrue(mixed.contains("RECURSOS Y MUNICIONES"),
                "Combining modules must not delete existing P1 resources.")
            assertFalse(pages.any { it.contains("C2 - Rasgos / continuación") },
                "A small P2 trait remainder may not occupy a redundant full C2 sheet.")
            assertTrue(pages[1].contains("CONTINÚA EN C2 PÁG. "),
                "The real P2 must point to its first physical continuation.")
            assertTrue(mixed.contains("P2 PÁG. 2"),
                "A source-mixed C2 must carry a verifiable physical P2 backlink.")
            assertTrue(pages.last().trim().endsWith("NOTAS"))
        }
    }

    @Test
    fun actualP3UsesPairedRuledNarrativeFieldsAndTwoStoryColumns() {
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val real = sources(fixture)
            val pdfBytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                real,
            ).bytes
            Loader.loadPDF(pdfBytes).use { pdf ->
                val p3 = pdf.getPage(2)
                val regions = PDFTextStripperByArea().apply {
                    addRegion("profile-left", Rectangle2D.Float(40f, 108f, 265f, 210f))
                    addRegion("profile-right", Rectangle2D.Float(309f, 108f, 265f, 210f))
                    addRegion("story", Rectangle2D.Float(40f, 308f, 534f, 400f))
                    extractRegions(p3)
                }
                val left = regions.getTextForRegion("profile-left")
                val right = regions.getTextForRegion("profile-right")
                val story = regions.getTextForRegion("story")
                for (label in listOf("TRASFONDO", "RASGOS DE PERSONALIDAD", "VÍNCULOS")) {
                    assertTrue(left.contains(label), "Golden P3 left field missing: " + label)
                }
                for (label in listOf("RELIGIÓN / FE", "IDEALES", "DEFECTOS")) {
                    assertTrue(right.contains(label), "Golden P3 right field missing: " + label)
                }
                assertTrue(story.contains("HISTORIA Y CRÓNICA"),
                    "Narrative story needs its own two-column physical region.")
                assertFalse(story.contains("RASGOS MECÁNICOS"),
                    "Mechanical traits belong to P1/P2/C2, never P3.")
                val text = PDFTextStripper().apply {
                    startPage = 3
                    endPage = 3
                }.getText(pdf)
                assertTrue(text.contains(real.permanent.sheet.background.name),
                    "Persisted source background name must survive in P3.")
            }
        }
    }

    @Test
    fun longRealHistoryContinuesInNarrativePagesWithoutBreakingFinalHandwriting() {
        val source = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val story = "INICIO-HISTORIA-P3 " +
            ("Crónica persistida sin pérdidas ni párrafos de relleno. ").repeat(120) +
            " FIN-HISTORIA-P3"
        val patched = source.copy(
            permanent = source.permanent.copy(
                sheet = source.permanent.sheet.copy(
                    background = source.permanent.sheet.background.copy(story = story),
                ),
            ),
        )
        val pdfBytes = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A,
                PcSheetExportStateSelection.PERMANENT),
            patched,
        ).bytes
        Loader.loadPDF(pdfBytes).use { pdf ->
            val pages = (1..pdf.numberOfPages).map { physical ->
                PDFTextStripper().apply {
                    startPage = physical
                    endPage = physical
                }.getText(pdf)
            }
            assertTrue(pages[2].contains("INICIO-HISTORIA-P3"))
            assertTrue(pages.any { it.contains("P3 - Narrativa / continuación") },
                "Overflowed real history must continue on narrative-only pages.")
            assertTrue(pages.any { it.contains("FIN-HISTORIA-P3") },
                "The tail of the real history must survive the measured pagination.")
            assertTrue(pages.last().trim().endsWith("NOTAS"),
                "The last physical page must remain paper-only, never narrative.")
        }
    }

    @Test
    fun mixedC2FillsUsableRightColumnEvenWhenLeftSourceColumnIsLong() {
        // In Mara the left-hand custom/action stream almost fills its column;
        // the right resource column still has a valid manuscript NOTAS area.
        val real = sources("03_mara_siete_umbrales_custom_extended.json")
        val bytes = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT), real,
        ).bytes
        Loader.loadPDF(bytes).use { pdf ->
            val mixed = (0 until pdf.numberOfPages).firstOrNull { index ->
                PDFTextStripper().apply {
                    startPage = index + 1
                    endPage = index + 1
                }.getText(pdf).contains("C2 - Habilidades, acciones y recursos")
            } ?: error("Mara's mixed C2 source page vanished.")
            val rightLowerPaper = PDFTextStripperByArea().apply {
                // A newly EMPTY right C2 column may place its manuscript
                // NOTAS heading near the top, not in the old bottom-only band.
                addRegion("right-notes", Rectangle2D.Float(310f, 84f, 263f, 635f))
                extractRegions(pdf.getPage(mixed))
            }.getTextForRegion("right-notes")
            assertTrue(rightLowerPaper.contains("NOTAS"),
                "A full LEFT source column must not suppress unused RIGHT C2 paper.")
            assertTrue(PDFTextStripper().getText(pdf).contains("Contratos arcanos"),
                "Mixed C2 manuscript fill may not erase real custom skill sources.")
        }
    }


    @Test
    fun realIlyraP4AlignsOnlyFittingSpellOriginsWithoutLosingLongSources() {
        val generated = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT),
            sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json"),
        )
        Loader.loadPDF(generated.bytes).use { pdf ->
            val p4 = pdf.getPage(3)
            val region = PDFTextStripperByArea().apply {
                // Left P4 168pt column: real source origin is visibly
                // right-aligned; spell name remains in a separate left lane.
                addRegion("spell-names", Rectangle2D.Float(43f, 180f, 100f, 140f))
                addRegion("real-right-origins",
                    Rectangle2D.Float(153f, 180f, 60f, 140f))
                extractRegions(p4)
            }
            assertTrue(region.getTextForRegion("spell-names").contains("Fire Bolt"),
                "Persisted real spell name must remain on the left.")
            assertTrue(region.getTextForRegion("real-right-origins").contains("[X] Mago"),
                "A short real casting source must be printed in the golden right lane.")
            val p4Text = PDFTextStripper().apply {
                startPage = 4; endPage = 4
            }.getText(pdf)
            assertTrue(p4Text.contains("Magic Initiate") &&
                p4Text.contains("Elven Lineage"),
                "Long genuine multi-word origins must remain in the measured flow.")
            for (name in listOf("Fire Bolt", "Light", "Mage Hand", "Fireball")) {
                assertEquals(1, Regex(Regex.escape(name) + """(?=\s|$)""")
                    .findAll(p4Text).count(),
                    "No source spell may disappear or duplicate when origins move.")
            }
        }
    }

    @Test
    fun genuineSpellRowsAndBlankPaperRowsHaveOnePhysicalSquareEach() {
        for ((fixture, variant, expectedBlankRows) in listOf(
            Triple("02_ilyra_quill_srd5_2_1_evoker_wizard.json",
                PcSheetV11Variant.B, 80),
            Triple("03_mara_siete_umbrales_custom_extended.json",
                PcSheetV11Variant.B, 60),
        )) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            )
            Loader.loadPDF(generated.bytes).use { pdf ->
                var marks = 0
                var paperRows = 0
                for (index in 0 until pdf.numberOfPages) {
                    val p4Text = PDFTextStripper().apply {
                        startPage = index + 1
                        endPage = index + 1
                    }.getText(pdf)
                    if (!p4Text.contains("P4 - Conjuros")) continue
                    paperRows += 1
                    assertFalse(p4Text.contains("______________________"),
                        "Golden P4 blank writing rows use vector rules, not underscore glyphs.")
                    val stream = pdf.getPage(index).contents.use {
                        it.readBytes().toString(Charsets.ISO_8859_1)
                    }
                    marks += Regex("""(?m)(?:^|\s)7 7 re(?:\s|$)""")
                        .findAll(stream).count()
                }
                assertTrue(paperRows >= 1, "Actual spell P4 page must be present.")
                assertEquals(expectedBlankRows + generated.semantic.spells.size, marks,
                    "Every source-backed real spell and every fixture-backed manuscript " +
                        "P4 row must retain exactly one physical 7pt vector square.")
            }
        }
    }

    @Test
    fun paperOnlySpellRowsUseVectorMarksInsteadOfLiteralBrackets() {
        val real = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val bytes = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT),
            real,
        ).bytes
        Loader.loadPDF(bytes).use { pdf ->
            val p4 = PDFTextStripper().apply {
                startPage = 4
                endPage = 4
            }.getText(pdf)
            assertTrue(p4.contains("TRUCOS / NIVEL 0"))
            assertTrue(p4.contains("NIVEL 9"))
            assertFalse(p4.contains("___"),
                "No fake underscore glyphs: all P4 empty writing uses real vector rules.")
            assertTrue(p4.contains("[X] Mago"),
                "Source-backed real spell preparation state must be preserved.")
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
            val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
            assertTrue(p1.contains("MARCAS: ____ / 41"),
                "The genuine over-20 fraction must be in P1 rather than only in C2.")
            assertEquals(1, Regex("MARCAS: ____ / 41").findAll(text).count(),
                "One source item must not create duplicate physical mark tracks.")
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
            assertTrue(sections.last().lineSequence().any { it.trim() == "NOTAS" },
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
            assertTrue(text.lineSequence().any { it.trim() == "NOTAS" })
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
            assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
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
            assertTrue(last.lineSequence().any { it.trim() == "NOTAS" },
                "The final manuscript page must remain even without digital notes.")
        }
    }

    @Test
    fun realMaraSinglePageC2EquipmentUsesBothSourceBackedPairs() {
        // The owner-approved C2 keeps two item/quantity pairs on the same
        // manuscript sheet, even when all persisted rows fit one tall pair.
        // This PDF is generated by the actual Desktop export service.
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(
                PcSheetV11Variant.B, PcSheetExportStateSelection.PERMANENT,
            ), sources("03_mara_siete_umbrales_custom_extended.json"),
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val pageNumber = (1..pdf.numberOfPages).first { physical ->
                PDFTextStripper().apply {
                    startPage = physical
                    endPage = physical
                }.getText(pdf).contains("C2 - Equipo y tesoro / EQUIPO")
            }
            val page = pdf.getPage(pageNumber - 1)
            val regions = PDFTextStripperByArea().apply {
                // Top-origin page coords, below the real App header and above
                // the Notes manuscript grid. Never count page metadata.
                addRegion("equipment-left",
                    Rectangle2D.Float(45f, 95f, 217f, 380f))
                addRegion("equipment-right",
                    Rectangle2D.Float(315f, 95f, 217f, 380f))
                extractRegions(page)
            }
            val left = regions.getTextForRegion("equipment-left")
            val right = regions.getTextForRegion("equipment-right")
            val persistedNameParts = listOf(
                "Frasco", "Cuerda", "Llave", "Cuaderno", "Cristal", "Astrolabio",
            )
            assertTrue(persistedNameParts.any { left.contains(it) },
                "Original left paired equipment field may not disappear.")
            assertTrue(persistedNameParts.any { right.contains(it) },
                "The unused right C2 paired column must receive authentic Mara " +
                    "inventory without inventing content or adding a page.")
            val printed = PDFTextStripper().apply {
                startPage = pageNumber
                endPage = pageNumber
            }.getText(pdf)
            assertTrue(printed.contains("VIENE DE P2 PÁG. 2"))
            assertTrue(printed.lineSequence().any { it.trim() == "NOTAS" },
                "Balancing must preserve handwriting on the same physical C2.")
        }
    }

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
            assertTrue(continuation.joinToString("\n")
                .contains("MATERIAL INVENTARIADO 001"),
                "The complete long source name belongs together on the C2 page.")
            val p2 = physicalTexts.first { it.contains("P2 - Equipo y tesoro") }
            assertFalse(p2.contains("MATERIAL INVENTARIADO"),
                "P2 may not print a partial long name and orphan its identity.")
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
            assertTrue(physicalTexts.last().lineSequence().any { it.trim() == "NOTAS" })
        }
    }
    @Test
    fun sparseTreasureContinuationKeepsWritingGridBelowRealPairedRows() {
        val fixture = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val template = fixture.permanent.sheet.currencies.first()
        val extra = (0 until 25).map { index ->
            template.copy(
                key = "c2-notes-proof-" + index,
                name = "DENOMINACIÓN C2 " + (index + 1).toString().padStart(3, '0'),
                amount = 9300 + index,
                sortOrder = 2200 + index,
                isDefault = false,
            )
        }
        val expanded = fixture.copy(
            permanent = fixture.permanent.copy(
                sheet = fixture.permanent.sheet.copy(
                    currencies = fixture.permanent.sheet.currencies + extra,
                ),
            ),
        )
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(
                PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT,
            ), expanded,
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val pages = (1..pdf.numberOfPages).map { page ->
                PDFTextStripper().apply {
                    startPage = page
                    endPage = page
                }.getText(pdf)
            }
            val treasureC2 = pages.filter {
                it.contains("C2 - Equipo y tesoro / TESORO")
            }
            assertTrue(treasureC2.isNotEmpty(),
                "Real treasure must overflow P2 into its own paired C2 continuation.")
            assertTrue(treasureC2.last().contains("DENOMINACIÓN C2"))
            assertTrue(treasureC2.last().lineSequence().any { it.trim() == "NOTAS" },
                "Unused measured C2 space must contain a manuscript NOTES grid.")
            assertFalse(treasureC2.last().contains("C-N3 - Notas digitales"),
                "Manuscript filler must never be confused with digital C-N3 notes.")
            assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" },
                "A filled C2 must not replace the obligatory final handwritten page.")
        }
    }

    @Test
    fun overflowingTreasureAlsoGetsFullHeightC2WithoutDuplicateAmountsOrLostZeros() {
        val existing = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val template = existing.permanent.sheet.currencies.first()
        val supplemental = (0 until 45).map { index ->
            template.copy(
                key = "qa-treasure-" + index,
                name = "MONEDA QA " + (index + 1).toString().padStart(3, '0') +
                    " de denominación especial para continuación completa",
                amount = if (index == 0) 0 else 8200 + index,
                sortOrder = 1500 + index,
                isDefault = false,
            )
        }
        val modified = existing.copy(
            permanent = existing.permanent.copy(
                sheet = existing.permanent.sheet.copy(
                    currencies = existing.permanent.sheet.currencies + supplemental,
                ),
            ),
        )
        val output = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(
                PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT,
            ),
            modified,
        )
        Loader.loadPDF(output.bytes).use { pdf ->
            val pages = (1..pdf.numberOfPages).map { page ->
                PDFTextStripper().apply {
                    startPage = page
                    endPage = page
                }.getText(pdf)
            }
            assertTrue(pages.any {
                it.contains("C2 - Equipo y tesoro / TESORO")
            }, "Overflow currencies must move into true full-height C2 paired tables.")
            val printed = pages.joinToString("\n")
            supplemental.forEach { coin ->
                assertTrue(printed.contains(coin.name.substring(0, 13)),
                    "Treasure denomination disappeared: " + coin.key)
                if (coin.amount > 0) {
                    assertEquals(1, Regex("(?<![0-9])" + coin.amount + "(?![0-9])")
                        .findAll(printed).count(),
                        "A currency amount must never repeat across P2 and its C2 continuation.")
                }
            }
            assertTrue(printed.contains("MONEDA QA 001"),
                "Even a currency with a genuine zero must remain visible.")
            assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
        }
    }

}
