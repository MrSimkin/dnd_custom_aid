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

    @Test
    fun approvedGoldenPdfArchiveIsAvailableForPhysicalP1Review() {
        // Reuse the already approved ZIP from Git; never regenerate a mockup.
        // The CI proof artifact deliberately includes the immutable reference
        // for a real PDF-page comparison with the App's native V11 exports.
        val root = File(System.getProperty("user.dir")).absoluteFile
        val archive = generateSequence(root) { it.parentFile }
            .map { File(it, "docs/pc-sheet-v11/dnd_custom_aid_V11_Carta_revision.zip") }
            .firstOrNull { it.isFile }
            ?: error("Frozen approved V11 golden archive is missing from the checkout")
        val bytes = archive.readBytes()
        val digest = java.security.MessageDigest.getInstance("SHA-256")
            .digest(bytes).joinToString("") { "%02x".format(it.toInt() and 0xff) }
        assertEquals(
            "35a1c6ea6257279799bfa97e64f024a413a7b29e2a45232e4088eeaa1d364e6a",
            digest, "Never inspect or publish an altered golden ZIP.",
        )
        val pdfCount = java.util.zip.ZipFile(archive).use { zip ->
            zip.entries().asSequence().count { !it.isDirectory &&
                it.name.lowercase().endsWith(".pdf") }
        }
        assertEquals(8, pdfCount, "Approved physical V11 reference must contain eight PDFs.")
        val proofDir = File(
            System.getProperty("pcSheetProofDir")
                ?: File("build/pc-sheet-proofs").absolutePath,
        )
        proofDir.mkdirs()
        File(proofDir, "V11-GOLDEN-approved-reference.zip").writeBytes(bytes)
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
    fun p4PrintsBothIntMaxSpellSlotCountersOnOneLineWithoutClippingOrFakeSpentBoxes() {
        // C220: the existing P1 oversized-slot real-persistence scenario
        // was a noncaster (no P4). Exercise the SAME Int boundary in
        // genuine Mara with populated spells and active P4.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        val extreme = mara.copy(
            permanent = mara.permanent.copy(
                sheet = mara.permanent.sheet.copy(
                    spellSlots = mara.permanent.sheet.spellSlots.map { slot ->
                        if (slot.level == 1) slot.copy(
                            totalSlots = 2147483647,
                            spentSlots = 2147483646,
                        ) else slot
                    },
                ),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ), extreme,
            ).bytes
            preserveRealApplicationPdf(bytes,
                "V11-INTERNAL-PROOF-P4-intmax-real-spell-slots-" + variant + ".pdf")
            Loader.loadPDF(bytes).use { pdf ->
                val p4Text = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply {
                        startPage = page
                        endPage = page
                    }.getText(pdf)
                }.filter { it.contains("P4 - Conjuros") }.joinToString("\n")
                assertTrue(p4Text.contains("ESP.2147483647 G.2147483646"),
                    "The real maximum-size total and spent count must share one " +
                        "untruncated physical P4 level row, $variant")
                assertEquals(1,
                    Regex("""ESP\.2147483647\s+G\.2147483646""")
                        .findAll(p4Text).count(),
                    "The true huge counters must not duplicate in the P4 module.")
                assertTrue(p4Text.contains("ESP.3 G.1"),
                    "Ordinary real N2 must keep its prior source-backed counter.")
            }
        }
    }

    @Test
    fun p4UsesRealPerLevelTotalAndSpentSlotsWithoutMakingUpHigherLevelCounts() {
        // D-0076 §§3.25–3.26: P4 must repeat source-backed P1 total
        // and spent counts on the SAME paper line as each level.
        // Mara has N1=4/2, N2=3/1, N3=3/2, N4=3/0, N5=2/1.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ), mara,
            ).bytes
            Loader.loadPDF(bytes).use { pdf ->
                val p4Pages = (1..pdf.numberOfPages).map { physical ->
                    PDFTextStripper().apply {
                        startPage = physical
                        endPage = physical
                    }.getText(pdf)
                }.filter { it.contains("P4 - Conjuros") }
                assertTrue(p4Pages.isNotEmpty())
                val ink = p4Pages.joinToString("\n")
                listOf("ESP.4 G.2", "ESP.3 G.1", "ESP.3 G.2",
                    "ESP.3 G.0", "ESP.2 G.1").forEach { real ->
                    assertTrue(ink.contains(real),
                        "P4 must print the REAL persisted source meter $real, $variant")
                }
                // Higher levels do not acquire fictitious total/spent
                // numbers just because the paper has blank slot squares.
                assertEquals(5, Regex("""ESP\.\d+ G\.\d+""").findAll(ink).count(),
                    "P4 must not synthesize slot totals for absent levels.")
            }
        }
    }

    @Test
    fun c337EveryPageHasTheGoldenFootRuleAndTheFinalNotasSheetHasNoPageNumberAB() {
        // Owner 2026-10-30: «permite la línea, evita/elimina el número de página».
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources("03_mara_siete_umbrales_custom_extended.json"),
            ).bytes
            Loader.loadPDF(bytes).use { pdf ->
                val footRule = Regex("""(?:^|\s)39\.69\d*\s+45\.07\d*\s+m\s+572\.32\d*\s+45\.07\d*\s+l""")
                for (index in 0 until pdf.numberOfPages) {
                    val ops = pdf.getPage(index).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                    assertTrue(footRule.containsMatchIn(ops), "Golden foot rule on page ${index + 1} ($variant).")
                }
                val last = PDFTextStripper().apply {
                    startPage = pdf.numberOfPages
                    endPage = pdf.numberOfPages
                }.getText(pdf)
                assertFalse(Regex("""\d+\s*/\s*\d+""").containsMatchIn(last),
                    "The final NOTAS sheet prints no page number ($variant).")
                // C339: no page carries the golden «NN / NN» counter.
                for (page in 1..pdf.numberOfPages) {
                    val text = PDFTextStripper().apply { startPage = page; endPage = page }.getText(pdf)
                    assertFalse(Regex("""\b\d{2} / \d{2}\b""").containsMatchIn(text),
                        "Page $page prints no page-number counter ($variant).")
                }
            }
        }
    }

    @Test
    fun c325RealStatesUseOwnerParaHojaDePjSymbolsV8GlyphsAB() {
        // Contract «Estados: Para Hoja de PJ Symbols v8 como prioridad»: training,
        // inspiration and ACTIVO are v8 PUA aliases, only where the source has them.
        val v8 = setOf(0xE108, 0xE109, 0xE10E, 0xE10F)
        for ((fixture, realStates) in listOf(
            "03_mara_siete_umbrales_custom_extended.json" to true,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to false,
        )) for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            ).bytes
            Loader.loadPDF(bytes).use { pdf ->
                val glyphs = mutableListOf<Triple<Int, Int, String>>()
                for (physical in 1..pdf.numberOfPages) {
                    object : PDFTextStripper() {
                        override fun writeString(
                            text: String,
                            textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                        ) {
                            textPositions.forEach {
                                val cp = it.unicode.codePointAt(0)
                                if (cp in 0xE000..0xF8FF) glyphs += Triple(physical, cp, it.font.name)
                            }
                        }
                    }.apply { startPage = physical; endPage = physical }.getText(pdf)
                }
                assertTrue(glyphs.all { it.second in v8 && it.third.endsWith("ParaHojadePJSymbolsV8-Regular") },
                    "Only the four v8 state aliases, in the owner font ($fixture $variant).")
                val p1 = glyphs.filter { it.first == 1 }.map { it.second }
                if (variant == PcSheetV11Variant.A) {
                    assertTrue(0xE108 in p1 && 0xE109 in p1,
                        "A skills print the v8 check / stacked double check ($fixture).")
                } else {
                    // C329: B boxes carry the same marks; the legend adds one of each.
                    assertTrue(p1.count { it == 0xE108 } > 1 && p1.count { it == 0xE109 } >= 1,
                        "B skills print the v8 check / stacked double check ($fixture).")
                }
                assertEquals(if (realStates) 1 else 0, p1.count { it == 0xE10E },
                    "INSPIRACIÓN star only when the sheet really has it ($fixture $variant).")
                // The App has no persisted ACTIVO field yet (semantic active = null), so
                // the v8 diamond must never be invented; the squares stay for pencil.
                assertFalse(glyphs.any { it.second == 0xE10F },
                    "ACTIVO diamond without a real App field ($fixture $variant).")
            }
        }
    }

    @Test
    fun g3P3StoryUsesOwnerApprovedGoldenRhythmSevenPointOnSeventeenPointRowsAB() {
        // Owner decision 2026-10-26 (option B): HISTORIA Y CRÓNICA prints Fira
        // 7 pt on 17 pt rows, baseline 4 pt above each row foot, like the golden.
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), original,
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val story = mutableListOf<Pair<Float, Float>>() // (font size, baseline)
                val storyStarts = mutableListOf<Float>()
                val fields = mutableListOf<Float>()
                val stripper = object : PDFTextStripper() {
                    override fun writeString(
                        text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                    ) {
                        val first = textPositions.firstOrNull() ?: return
                        val baseline = 792f - first.yDirAdj
                        if (baseline in 61f..436f) { // C321 golden story rows
                            story += first.fontSizeInPt to baseline
                            storyStarts += first.xDirAdj
                        }
                        else if (baseline in 437f..670f && first.fontSizeInPt < 10f) {
                            fields += first.fontSize // exact Tf size (fontSizeInPt is an integer)
                        }
                    }
                }
                stripper.startPage = 3
                stripper.endPage = 3
                stripper.getText(pdf)
                val p3 = PDFTextStripper().apply { startPage = 3; endPage = 3 }.getText(pdf)
                assertTrue(p3.replace(Regex("\\s+"), " ").contains("Mara creció entre dos calendarios"))
                assertTrue(story.size >= 5, "Mara's real story must print on P3 ($variant).")
                assertTrue(story.all { kotlin.math.abs(it.first - 7f) < 0.01f },
                    "Every P3 story line must use the approved 7 pt size ($variant).")
                val baselines = story.map { it.second }.distinct().sortedDescending()
                baselines.zipWithNext().forEach { (upper, lower) ->
                    assertEquals(17f, upper - lower, 0.05f,
                        "P3 story lines must sit on consecutive 17 pt rows ($variant).")
                }
                // Row foot rules start at the 449 pt area top: 449 - 17k; baseline is 4 pt above.
                baselines.forEach { baseline ->
                    val foot = baseline - 4f
                    assertEquals(0f, ((435.97f - foot) % 17f).let { minOf(it, 17f - it) }, 0.05f,
                        "P3 story baseline must be 4 pt above its own row foot ($variant).")
                }
                // Golden inner margin: every story line starts 4 pt inside its band
                // (left band 47..300; Mara's story fits the left column).
                assertTrue(storyStarts.all { kotlin.math.abs(it - 43.69f) < 0.3f }, // C305 golden x43.7
                    "P3 story text keeps the golden 4 pt inner margin ($variant): $storyStarts")
                assertTrue(fields.any { kotlin.math.abs(it - 7.3f) < 0.01f } &&
                    fields.all { kotlin.math.abs(it - 7.3f) < 0.01f || kotlin.math.abs(it - 7f) < 0.01f ||
                        kotlin.math.abs(it - 9.1f) < 0.01f /* C306 golden band title */ },
                    "Profile fields use the golden 7.3 pt values (C284); Historia keeps 7 pt: " + fields.distinct())
            }
        }
    }

    /** Words of page 1 as (text, x, baseline y in PDF coordinates). */
    private fun page1Words(pdf: org.apache.pdfbox.pdmodel.PDDocument):
        List<Triple<String, Float, Float>> {
        val words = mutableListOf<Triple<String, Float, Float>>()
        val stripper = object : PDFTextStripper() {
            override fun writeString(
                text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
            ) {
                val word = StringBuilder()
                var wx = 0f
                var wy = 0f
                fun flush() {
                    if (word.isNotEmpty()) words += Triple(word.toString(), wx, wy)
                    word.clear()
                }
                for (position in textPositions) {
                    val ch = position.unicode
                    if (ch.isBlank()) flush() else {
                        if (word.isEmpty()) {
                            wx = position.xDirAdj
                            wy = 792f - position.yDirAdj
                        }
                        word.append(ch)
                    }
                }
                flush()
            }
        }
        stripper.startPage = 1
        stripper.endPage = 1
        stripper.getText(pdf)
        return words
    }

    @Test
    fun g3P1SpellSlotsUse3x3GridWithRealTotalsAndSpentAndNeverInventAbsentLevelsAB() {
        // P115/P116 (owner 2026-10-26: no character has 7+ slots, so only
        // the 3x3 levels 1..9 apply and levels 10..12 are never drawn).
        // Mara persists real slots for N1..N5 only (N4 spent is a real 0).
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        val real = mara.permanent.sheet.spellSlots.associateBy { it.level }
        assertTrue(real.keys.containsAll(listOf(1, 2, 3, 4, 5)) &&
            real.keys.none { it in 6..9 }, "Fixture must have real N1-N5 and no N6-N9.")
        assertEquals(0, real.getValue(4).spentSlots, "Fixture needs a REAL spent zero.")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                mara,
            ).bytes
            preserveRealApplicationPdf(bytes,
                "V11-INTERNAL-PROOF-P1-spell-slot-grid-3x3-" + variant.name + ".pdf")
            Loader.loadPDF(bytes).use { pdf ->
                val words = page1Words(pdf)
                fun near(word: Triple<String, Float, Float>, x: Float, y: Float) =
                    kotlin.math.abs(word.second - x) <= 2.5f &&
                        kotlin.math.abs(word.third - y) <= 2.5f
                // C250/C255: golden cells; the paper boxes ARE the real totals, never
                // pre-marked, with the real "gastados/total" count (absent = no box).
                val native = pdf.getPage(0).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                val boxes = Regex(
                    """(?m)(?:^|\s)([\d.]+)\s+([\d.]+)\s+8(?:\.0)?\s+8(?:\.0)?\s+re(?:\s|$)""",
                ).findAll(native).map { it.groupValues[1].toFloat() to it.groupValues[2].toFloat() }.toList()
                for (level in 1..9) {
                    val cellX = 314f + ((level - 1) % 3) * 85f
                    val cellY = 222f - ((level - 1) / 3) * 24f
                    assertTrue(words.any { it.first == "N$level" && near(it, cellX + 5f, cellY + 9f) },
                        "P1 must print N$level at its fixed 3x3 grid cell, $variant")
                    val inCell = words.filter {
                        it.third in cellY..cellY + 24f &&
                            it.second > cellX + 15f && it.second < cellX + 84f
                    }
                    val cellBoxes = boxes.count { (x, y) ->
                        x >= cellX + 20f && x + 8f <= cellX + 84f && y == cellY + 8f
                    }
                    val slot = real[level]
                    if (slot == null) {
                        assertTrue(inCell.isEmpty() && cellBoxes == 0,
                            "P1 must not invent total/spent for absent level $level: $inCell")
                    } else {
                        assertEquals(slot.totalSlots, cellBoxes,
                            "P1 N$level must draw the real total ${slot.totalSlots}")
                        assertTrue(inCell.none { w -> 'X' in w.first },
                            "C255: P1 N$level boxes are never pre-marked: $inCell")
                        assertTrue(inCell.any {
                            it.first == "${slot.spentSlots}/${slot.totalSlots}" && it.second > cellX + 60f
                        }, "P1 N$level must print the real gastados/total: $inCell")
                    }
                }
                assertTrue(words.none { it.first == "N10" || it.first == "N11" ||
                    it.first == "N12" }, "Levels 10..12 need real epic rules and are absent.")
            }
        }
    }

    @Test
    fun realP1SpellcastingUsesMeasuredClippedBlueHeaderWithoutMovingSourcePanel() {
        // Frozen approved P1 gold: x270.425..572.315,
        // top-origin y549.354..572.315, 8-segment clipped blue banner.
        // Genuine App traits end top-origin y491 and spells begin y511.
        // The 258x18pt band fits inside that EXISTING free strip;
        // panel/source slots must retain all data and original geometry.
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val result = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            )
            Loader.loadPDF(result.bytes).use { pdf ->
                val p1 = pdf.getPage(0)
                val ops = p1.contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                val clipped = Regex(
                    """(?s)(?:^|\s)316\s+282\s+m\s+566\s+282\s+l\s+570\s+286\s+l""" +
                        """.*?\s+h\s+B(?:\s|$)"""
                )
                assertEquals(1, clipped.findAll(ops).count(),
                    "Expected one genuine clipped P1 casting cartela ($fixture $variant).")
                // PDFBox operators prove the original genuine spell-data
                // panel starts at x312/y154 and is still 258x127pt.
                assertTrue(Regex(
                    """(?:^|\s)312\s+154\s+258\s+127\s+re(?:\s|$)"""
                ).containsMatchIn(ops), "Casting source panel must not move.")
                // Verify actual embedded-font glyph origin. The first
                // review exposed an L touching the clipped top-left border.
                val titleOrigins = mutableListOf<Pair<Float, Float>>()
                val inspector = object : PDFTextStripper() {
                    override fun writeString(
                        text: String,
                        textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                    ) {
                        val at = text.indexOf("LANZAMIENTO")
                        if (at >= 0 && textPositions.size > at) {
                            val p = textPositions[at]
                            titleOrigins += p.xDirAdj to p.yDirAdj
                        }
                        super.writeString(text, textPositions)
                    }
                }
                inspector.startPage = 1
                inspector.endPage = 1
                inspector.getText(pdf)
                assertEquals(1, titleOrigins.size,
                    "Exactly one P1 casting heading required.")
                val title = titleOrigins.single()
                // C265: golden inset 16 pt, clear of the left cartela diamond.
                assertTrue(kotlin.math.abs(title.first - 328f) < 0.3f &&
                    kotlin.math.abs(title.second - 506f) < 0.3f,
                    "Title must clear the clipped left/top frame with " +
                        "16pt left inset and 2pt upper ink clearance.")
                val text = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                for (label in listOf(
                    "LANZAMIENTO DE CONJUROS", "APTITUD", "CD SALVACIÓN",
                    "ATAQUE MÁGICO", "APTITUD MÁGICA", "RASGOS Y ATRIBUTOS",
                    "N1", "N9",
                )) {
                    assertTrue(text.contains(label),
                        "Original physical casting/traits source lost: $label")
                }
            }
        }
    }

    @Test
    fun realP1AttacksUsesClippedBlueHeaderWithinTheOriginalGrid() {
        // Golden x270..572 top-origin y331..344 has a blue table header;
        // actual App attack table is x312..570 top-origin y301..400.
        // A distinct 23pt ATAQUES title cannot fit in the 12pt inter-module
        // gap without reflow. This test closes ONLY the existing header's
        // clipped blue treatment, protecting ALL attack label/data text.
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val real = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ), sources(fixture),
            )
            Loader.loadPDF(real.bytes).use { pdf ->
                val p1 = pdf.getPage(0)
                val ops = p1.contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                // PDFBox emits 8 exact polygon segments and B fill/stroke.
                // Distinguish P1 attacks from all C2/P2/P3/P4 bands.
                val header = Regex(
                    """(?s)(?:^|\s)315\s+479\s+m\s+567\s+479\s+l\s+570\s+482\s+l""" +
                        """.*?\s+h\s+B(?:\s|$)"""
                )
                assertEquals(1, header.findAll(ops).count(),
                    "Exactly one physical clipped ATTACK TABLE header must " +
                        "appear inside x312..570 and y301..313 ($fixture $variant).")
                // The narrow hit-bonus column remains 36pt and the original
                // attack rows continue under the untouched baseline.
                for (columnX in listOf(433, 469)) {
                    assertTrue(Regex(
                        """(?m)(?:^|\s)""" + columnX +
                            """\s+392\s+m\s+""" + columnX +
                            """\s+491\s+l"""
                    ).containsMatchIn(ops),
                        "Attacks columns must remain at exact original x=$columnX.")
                }
                val text = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                for (label in listOf(
                    "ARMA / ATAQUE / CONJURO", "BON.", "DAÑO / TIPO",
                    "COMBATE Y SUPERVIVENCIA",
                )) {
                    assertTrue(text.contains(label),
                        "Real attack header or combat source lost: $label.")
                }
            }
        }
    }

    @Test
    fun p1GoldenThreeThreeTwoCombatKeepsAllEightRealSourcesWithoutInventingInspiration() {
        // Check 4 real A/B PDFs; the independent CI PDF oracle verifies
        // exact 3+3+2 clipped physical cells and source label placement.
        for (fixture in listOf(
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json",
            "03_mara_siete_umbrales_custom_extended.json",
        )) for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources(fixture))
            Loader.loadPDF(proof.bytes).use { pdf ->
                val p1 = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                listOf("CA", "INICIATIVA", "VELOCIDAD", "COMPETENCIA",
                    "PV ACTUALES", "PV MÁXIMOS", "INSPIRACIÓN", "DADOS DE GOLPE").forEach {
                    assertTrue(p1.contains(it), "Lost real P1 combat source: " + it)
                }
                // C256 (owner 2026-10-28): golden INSPIRACIÓN with the real sheet
                // state; the real temporary hit points ride inside PV ACTUALES.
                val sheet = sources(fixture).permanent.sheet
                assertTrue(p1.contains("TEMP " + sheet.tempHp),
                    "Real temporary hit points must stay on P1 inside PV ACTUALES.")
                assertFalse(p1.contains("PV TEMP"), "PV TEMP no longer replaces INSPIRACIÓN.")
            }
        }
    }

    @Test
    fun actualP1CombatHasFrozenClippedBlueTitleWithoutReflowingSourceCards() {
        // Frozen golden B P1: pale-blue 8-segment stroked+filled combat banner
        // x270.425..572.315/y180.850..203.811 (top-origin). Our source-backed
        // real-App right column is narrower x312..570; source panel begins at
        // y201 top-origin, so use the EXISTING clear 22pt strip at y176..198.
        // Assert actual emitted operators from the Kotlin Desktop App service.
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val result = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ), sources(fixture),
            )
            Loader.loadPDF(result.bytes).use { pdf ->
                val operators = pdf.getPage(0).contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                val clippedBand = Regex(
                    """(?s)(?:^|\s)317\s+594\s+m\s+565\s+594\s+l\s+570\s+599\s+l""" +
                        """.*?\s+h\s+B(?:\s|$)"""
                )
                assertTrue(clippedBand.containsMatchIn(operators),
                    "Real V11 P1 requires distinct clipped filled+stroked " +
                        "combat title within the original clear band ($fixture $variant).")
                assertTrue(Regex("""(?m)/F\d+\s+11\s+Tf""")
                    .containsMatchIn(operators),
                    "Combat heading must use embedded Noto Sans Display Condensed 11pt.")
                val text = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                assertTrue(text.contains("COMBATE Y SUPERVIVENCIA"))
                for (sourceLabel in listOf("CA", "INICIATIVA", "VELOCIDAD",
                    "COMPETENCIA", "PV ACTUALES", "PV MÁXIMOS", "INSPIRACIÓN", "DADOS DE GOLPE")) {
                    assertTrue(text.contains(sourceLabel),
                        "Source combat field lost after title repair: $sourceLabel")
                }
                assertTrue(text.contains("ARMA / ATAQUE / CONJURO"),
                    "Original P1 attack module must retain its source records.")
            }
        }
    }

    @Test
    fun realP1SavingLabelUsesMeasuredGoldenMicroOffsetWithoutMovingBoxes() {
        // Independently measure the generated PDF text, not source constants.
        // Frozen B reference: label right x=202.68, checkbox left x=220.54,
        // gap 17.85pt. Before fix: real App x=249.83 versus box x=270,
        // gap 20.17pt; label was also 2.1pt lower relative to FUERZA.
        val cases = listOf(
            Triple("02_ilyra_quill_srd5_2_1_evoker_wizard.json",
                PcSheetV11Variant.B, 237.9f),
            Triple("03_mara_siete_umbrales_custom_extended.json",
                PcSheetV11Variant.B, 235.9f),
            Triple("03_mara_siete_umbrales_custom_extended.json",
                PcSheetV11Variant.A, 237.9f),
        )
        for ((fixture, variant, expectedBaselineY) in cases) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ), sources(fixture),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val savingLabels = mutableListOf<Pair<Float, Float>>()
                val stripper = object : PDFTextStripper() {
                    override fun writeString(
                        text: String,
                        textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                    ) {
                        val at = text.indexOf("SALVACIÓN")
                        if (at >= 0 && textPositions.size > at) {
                            val position = textPositions[at]
                            savingLabels += position.xDirAdj to position.yDirAdj
                        }
                        super.writeString(text, textPositions)
                    }
                }
                stripper.startPage = 1
                stripper.endPage = 1
                stripper.getText(pdf)
                val expectedRows = when {
                    fixture.startsWith("02_") -> 6
                    variant == PcSheetV11Variant.A -> 7 // Mara: grouped A skills reserve row height.
                    else -> 9 // Mara B: all three custom attributes fit P1.
                }
                assertEquals(expectedRows, savingLabels.count {
                    kotlin.math.abs(it.first - 217.3f) < 0.3f
                }, "Every printed P1 saving label must be physically at " +
                    "the approved checkbox-relative x position ($fixture, $variant).")
                val first = savingLabels.first { kotlin.math.abs(it.first - 217.3f) < 0.3f }
                assertTrue(kotlin.math.abs(first.second - expectedBaselineY) < 0.35f,
                    "Saving label baseline must move 2.1pt upward " +
                    "relative to the original attribute row ($fixture, $variant).")
                assertPhysicalP1SavingBoxes(pdf, expectedRows)
            }
        }
    }

    @Test
    fun physicalP4GoldenPaperHasClippedReferenceCardsAndNineWritableSlotMeters() {
        // Inspect the actual Desktop App service output, not a Python mock.
        // A printed slot meter is blank handwriting, not an invented spell
        // capacity or a persisted spell state. All actual spells remain.
        for (fixture in listOf(
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json",
            "03_mara_siete_umbrales_custom_extended.json",
        )) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(PcSheetV11Variant.B,
                    PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val p4 = (1..pdf.numberOfPages).firstOrNull { number ->
                    PDFTextStripper().apply {
                        startPage = number
                        endPage = number
                    }.getText(pdf).contains("P4 - Conjuros")
                } ?: error("Real spellcaster lost physical P4")
                val text = PDFTextStripper().apply {
                    startPage = p4
                    endPage = p4
                }.getText(pdf)
                assertTrue(text.contains("REFERENCIA MÁGICA"))
                assertTrue(text.contains("APTITUD"))
                assertTrue(text.contains("CD SALVACIÓN"))
                assertTrue(text.contains("ATAQUE MÁGICO"))
                for (level in 1..9) {
                    assertTrue(text.contains("NIVEL $level"),
                        "The approved P4 still reserves every normal level 1..9.")
                }
                assertFalse(text.contains("NIVEL 10"),
                    "Do not fabricate epic spell levels.")
                val operators = pdf.getPage(p4 - 1).contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                // C308: golden geometry - cartela x39.69..572.32, cards 39.69 + 178.58 i,
                // 174.33 pt wide, all with 6 pt cuts. Compared with 0.02 pt tolerance.
                // C322: golden P4 cartela y672.66 and cards y640.91 (all values toleranced).
                fun clippedPath(y0: Float, y1: Float, a: Float, b: Float, c: Float) = Regex(
                    """(?m)(?:^|\s)([-\d.]+)\s+([-\d.]+)\s+m\s+([-\d.]+)\s+([-\d.]+)\s+l\s+([-\d.]+)\s+([-\d.]+)\s+l"""
                ).findAll(operators).any { m ->
                    val v = m.groupValues.drop(1).map { it.toFloat() }
                    kotlin.math.abs(v[0] - a) < 0.02f && kotlin.math.abs(v[1] - y0) < 0.02f &&
                        kotlin.math.abs(v[2] - b) < 0.02f && kotlin.math.abs(v[3] - y0) < 0.02f &&
                        kotlin.math.abs(v[4] - c) < 0.02f && kotlin.math.abs(v[5] - y1) < 0.02f
                }
                assertTrue(clippedPath(672.66f, 678.66f, 45.69f, 566.32f, 572.32f),
                    "P4 magical-reference cartela must be physically clipped.")
                for (index in 0..2) {
                    val x = 39.69f + index * 178.58f
                    assertTrue(clippedPath(640.91f, 646.91f, x + 6f, x + 174.33f - 6f, x + 174.33f),
                        "Each real P4 magic-source card needs 6pt clipped corners.")
                }
                val paperSlots = Regex(
                    // C309: golden P4 columns move the three slot boxes (right - 33/22/11).
                    """(?m)(?:^|\s)(?:174\.5\d*|185\.5\d*|196\.5\d*|356\.9\d*|367\.9\d*|378\.9\d*|539\.3\d*|550\.3\d*|561\.3\d*)""" +
                        """\s+[\d.]+\s+7\s+7\s+re(?:\s|$)"""
                ).findAll(operators).count()
                assertEquals(27, paperSlots,
                    "Exactly three BLANK genuine vector slot boxes for each " +
                        "of nine spell levels. No 0-level or fabricated 10+.") 
                assertTrue(Regex("""(?m)/F\d+\s+5\.9\d*\s+Tf""")
                    .findAll(operators).count() >= 9,
                    "P4 must use genuine embedded Noto Sans Display 5.9 pt (golden ESP.) on blank meters.")
                // C269: golden P4 card labels are 6.65 pt.
                assertTrue(Regex("""(?m)/F\d+\s+6\.65\d*\s+Tf""")
                    .findAll(operators).count() >= 3,
                    "P4 three source card labels use embedded Noto Sans Display typography.")
                assertTrue(PDFTextStripper().getText(pdf).contains("NOTAS"),
                    "Golden P4 improvements must not erase trailing handwritten notes.")
            }
        }
    }

    @Test
    fun actualIlyraP3UsesTwoClippedBlueNarrativeBandsAndBarlowFieldLabels() {
        // Approved P3 narrative appearance must come from the real App PDF.
        // No narrative text, source IDs, lines or page geometry may change.
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT),
            sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json"),
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val page = pdf.getPage(2)
            val printed = PDFTextStripper().apply {
                startPage = 3; endPage = 3
            }.getText(pdf)
            assertTrue(printed.contains("TRASFONDO Y PERFIL NARRATIVO"))
            assertTrue(printed.contains("HISTORIA Y CRÓNICA"))
            listOf("TRASFONDO", "RELIGIÓN / FE", "RASGOS DE PERSONALIDAD",
                "IDEALES", "VÍNCULOS", "DEFECTOS").forEach { label ->
                assertTrue(printed.contains(label),
                    "Persisted P3 narrative cartela missing: " + label)
            }
            assertTrue(printed.contains("Ilyra dejó un archivo académico"),
                "Source-backed story must remain full and legible.")
            val operators = page.contents.use {
                it.readBytes().toString(Charsets.ISO_8859_1)
            }
            // C306/C321: golden P3 cartelas x39.69..572.32 with 6 pt cuts, at the
            // golden y672.66 and y437.95 (0.02 pt tolerance on PDFBox floats).
            val p3Paths = Regex(
                """(?:^|\s)([-\d.]+)\s+([-\d.]+)\s+m\s+([-\d.]+)\s+([-\d.]+)\s+l\s+([-\d.]+)\s+([-\d.]+)\s+l"""
            ).findAll(operators).map { m -> m.groupValues.drop(1).map { it.toFloat() } }.toList()
            for (y in listOf(672.66f, 437.95f)) {
                assertTrue(p3Paths.any { v ->
                    kotlin.math.abs(v[0] - 45.69f) < 0.02f && kotlin.math.abs(v[1] - y) < 0.02f &&
                        kotlin.math.abs(v[2] - 566.32f) < 0.02f && kotlin.math.abs(v[4] - 572.32f) < 0.02f &&
                        kotlin.math.abs(v[5] - (y + 6f)) < 0.02f
                },
                    "Each original-height P3 narrative heading requires " +
                        "a clipped, filled-and-stroked blue physical cartela at y=" + y)
            }
            assertTrue(Regex("""(?m)/F\d+\s+7\s+Tf""").containsMatchIn(operators),
                "Actual P3 field labels must use embedded Noto Sans Display Condensed 7 pt (golden).")
        }
    }

    @Test
    fun genuineP2KeepsAllFourClippedBlueCartelasOutsideSourceTables() {
        // Concrete golden-vs-real-PDF regression: all P2 titles occupy the
        // previously reserved 14.5pt strips. Inspect emitted PDFBox vectors,
        // not a layout mockup or literal square bracket typography.
        // The App fixture's default export options differ from the CI
        // four-proof matrix (portrait/book/narrative options). Do not
        // hardcode an unrelated page count; check real P1-P4 boundaries here
        // and compare before/after full artifact counts independently.
        for (fixture in listOf(
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json",
            "03_mara_siete_umbrales_custom_extended.json",
        )) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(PcSheetV11Variant.B,
                    PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                assertTrue(pdf.numberOfPages >= 4,
                    "P2 heading decoration cannot erase primary source pages.")
                val text = PDFTextStripper().apply {
                    startPage = 2
                    endPage = 2
                }.getText(pdf)
                for (title in listOf("EQUIPO", "TESORO", "RASGOS", "EQUIPO ESPECIAL",
                    "UBICACIÓN", "NOMBRE", "DESCRIPCIÓN", "ACTIVO")) {
                    assertTrue(text.contains(title),
                        "Actual PDF P2 lost existing title or table field: " + title)
                }
                val stream = pdf.getPage(1).contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                // C320: golden P2 cartelas (v11_extremo p2), 5 pt corner cut, 0.02 pt tolerance.
                val bands = listOf(
                    Triple(44.69f, 672.66f, 293.49f),  // equipment, left upper
                    Triple(314.26f, 672.66f, 567.32f), // traits, right upper
                    Triple(44.69f, 429.16f, 293.49f),  // treasure, left middle
                    Triple(44.69f, 293.38f, 567.32f),  // special equipment, full width
                )
                val polygonStarts = Regex(
                    """(?:^|\s)([-\d.]+)\s+([-\d.]+)\s+m\s+([-\d.]+)\s+([-\d.]+)\s+l\s+([-\d.]+)\s+([-\d.]+)\s+l"""
                ).findAll(stream).map { m -> m.groupValues.drop(1).map { it.toFloat() } }.toList()
                for ((x, y, xr) in bands) {
                    assertTrue(polygonStarts.any { v ->
                        kotlin.math.abs(v[0] - x) < 0.02f && kotlin.math.abs(v[1] - y) < 0.02f &&
                            kotlin.math.abs(v[2] - xr) < 0.02f && kotlin.math.abs(v[4] - (xr + 5f)) < 0.02f
                    }, "Real P2 must contain a clipped filled/stroked blue " +
                        "cartela outside measured source cells: " + x + "," + y)
                }
                // C265: golden P2 cartela titles are 9.1 pt.
                assertTrue(Regex("""(?m)/F\d+\s+9\.1\d*\s+Tf""")
                    .findAll(stream).count() >= 4,
                    "P2 headings must all use genuine embedded NotoSansDisplay 9.1pt.")
                val all = PDFTextStripper().getText(pdf)
                assertTrue(all.contains("NOTAS"))
                assertTrue(all.contains("P3 - Trasfondo"))
                assertTrue(all.contains("P4 - Conjuros"),
                    "P2 cartelas must not shift spell/narrative/C2 pagination.")
            }
        }
    }

    @Test
    fun g3RealP2RasgosHeadingMatchesApprovedGoldenWithoutChangingActualTraits() {
        // G3 P207: measured approved P2 reads RASGOS Y ATRIBUTOS.
        // Its existing C2c cartela is source-free and must not move content.
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant,
                    PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val p2 = PDFTextStripper().apply {
                    startPage = 2
                    endPage = 2
                }.getText(pdf)
                assertTrue(p2.contains("RASGOS Y ATRIBUTOS"),
                    "Approved P2 source cartela is missing its complete heading.")
                assertTrue(p2.contains("EQUIPO ESPECIAL"))
                assertTrue(p2.contains("TESORO"))
                val ops = pdf.getPage(1).contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                // C320: golden RASGOS cartela x309.26..572.32 at y672.66 (5 pt cut).
                assertTrue(Regex(
                    """(?:^|\s)([-\d.]+)\s+([-\d.]+)\s+m\s+([-\d.]+)\s+([-\d.]+)\s+l"""
                ).findAll(ops).any { m ->
                    val v = m.groupValues.drop(1).map { it.toFloat() }
                    kotlin.math.abs(v[0] - 314.26f) < 0.02f && kotlin.math.abs(v[1] - 672.66f) < 0.02f &&
                        kotlin.math.abs(v[2] - 567.32f) < 0.02f
                },
                    "Retain the existing blue Rasgos C2c polygon, not a floating title.")
                assertTrue(pdf.numberOfPages >= 4)
            }
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
            // New owner Rasgos rule: compact identity-only records no longer
            // NEED to spill Ilyra's long original descriptions into mixed C2.
            // Preserve genuine P1 resources/attacks, but never demand a
            // redundant C2 merely to preserve a historical test expectation.
            listOf(
                "ATAQUES, NOTAS Y ESPACIOS REALES",
                "RECURSOS Y MUNICIONES",
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
            // C323: golden C2 columns 39.69..301.04 and 310.96..572.32 (3 pt corner cut).
            val clippedGroupBands = Regex(
                """(?m)(?:^|\s)([0-9.]+)\s+[0-9.]+\s+m\s+([0-9.]+)\s+[0-9.]+\s+l\s+([0-9.]+)\s+[0-9.]+\s+l"""
            ).findAll(commands).count { m ->
                val (a, b, c) = m.destructured
                listOf(Triple(42.69f, 298.04f, 301.04f), Triple(313.96f, 569.32f, 572.32f)).any { (x0, x1, x2) ->
                    kotlin.math.abs(a.toFloat() - x0) < 0.05f && kotlin.math.abs(b.toFloat() - x1) < 0.05f &&
                        kotlin.math.abs(c.toFloat() - x2) < 0.05f
                }
            }
            assertTrue(clippedGroupBands >= 2,
                "All ACTUAL Ilyra mixed C2 source groups require " +
                    "separate clipped physical blue cartelas: found " + clippedGroupBands)
            // C269: golden C2 cartela titles are 9.1 pt.
            assertTrue(Regex("""(?m)/F\d+\s+9\.1\d*\s+Tf""").containsMatchIn(commands),
                "C2 source cartela labels must use embedded 9pt Noto Sans Display Condensed " +
                    "rather than the Noto Sans value font.")
            assertTrue(Regex("""(?m)(?:^|\s)B(?:\s|$)""")
                .containsMatchIn(commands),
                "Source-backed mixed C2 requires a distinct clipped blue " +
                    "NOTAS heading above a complete physical manuscript grid.")
            assertTrue(text.contains("VIENE DE P1 PÁG. 1"),
                "C2 cartelas cannot disturb genuine physical P1 backlinks.")
            if (text.contains("RASGOS / CONTINUACIÓN P2")) {
                assertTrue(text.contains("P2 PÁG. 2"),
                    "Only a C2 that actually contains deferred P2 Rasgos may cite P2.")
            }
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
            // C259: a real balance of zero prints all seven boxes and "7/7" spent.
            assertTrue(printed.contains("Reserva 5: Sello") && printed.contains("7/7"),
                "Source resource balance zero is a real value, not absence.")
            assertTrue(printed.contains("Recupera:"),
                "Mara's persisted resource recovery must survive PDF generation.")
            assertTrue(printed.contains("P2 - Equipo y tesoro"))
            val pagesByPhysicalNumber = (1..pdf.numberOfPages).map { number ->
                PDFTextStripper().apply {
                    startPage = number
                    endPage = number
                }.getText(pdf)
            }
            // C339 (owner 2026-10-30): no page prints a page-number counter.
            assertTrue(pagesByPhysicalNumber.none {
                Regex("""\b\d{2} / \d{2}\b""").containsMatchIn(it)
            }, "No physical page prints a page-number counter.")
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
            assertFalse(Regex("""\d{2} / \d{2}""").containsMatchIn(pagesByPhysicalNumber.last()),
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
            // With the corrected identity-only Rasgos contract, a P2 list
            // may fully fit and does NOT require a redundant trait C2.
            // If there is actual remaining trait content, its C2 label and
            // source backlink are checked above.
            assertTrue(real.permanent.sheet.traits.isNotEmpty())
            assertTrue(printed.contains("RASGOS"),
                "The owner-approved identification area must still exist.")
            assertTrue(printed.contains("C2 - Equipo especial"))
            val specialContinuation = pagesByPhysicalNumber.filter {
                realHeading(it, "C2 - Equipo especial / continuación")
            }
            assertTrue(specialContinuation.isNotEmpty())
            assertTrue(specialContinuation.all { it.contains("UBICACIÓN") &&
                it.contains("NOMBRE") && it.contains("DESCRIPCIÓN") &&
                it.contains("ACTIVO") },
                "Every physical special C2 must preserve the approved four columns.")
            // D-0076 §3.21: P2 shows the first real entries in taller rows;
            // only the remaining source suffix continues on C2, never twice.
            assertTrue(physicalP2.replace(Regex("\\s+"), " ")
                .contains("Astrolabio de cobre"),
                "Mara's first special entry must be printed on P2.")
            assertFalse(specialContinuation.joinToString("\n")
                .contains("Astrolabio de cobre"),
                "An entry printed on P2 must not be repeated on C2.")
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
                    "VELOCIDAD", "COMPETENCIA", "PV ACTUALES", "PV MÁXIMOS", "INSPIRACIÓN", "DADOS DE GOLPE")) {
                    assertTrue(combat.contains(field),
                        "Persisted combat field outside dedicated physical panel: " + field)
                }
                assertFalse(combat.contains("ARMA / ATAQUE / CONJURO"),
                    "The combat card panel may not overlap the attack table below.")
                val split = PDFTextStripperByArea()
                split.addRegion("left",Rectangle2D.Float(40f,185f,261f,510f))
                split.addRegion("attacks",Rectangle2D.Float(309f,294f,264f,107f))
                split.addRegion("spells",Rectangle2D.Float(309f,499f,264f,142f))
                split.extractRegions(page)
                assertTrue(split.getTextForRegion("left").contains("FUERZA"))
                assertFalse(split.getTextForRegion("left").contains("ARMA / ATAQUE / CONJURO"))
                assertTrue(split.getTextForRegion("attacks").contains("ARMA / ATAQUE / CONJURO"))
                assertTrue(split.getTextForRegion("spells").contains("LANZAMIENTO DE CONJUROS"))
                val p1MechanicalRegions = PDFTextStripperByArea().apply {
                    addRegion("traits", Rectangle2D.Float(309f, 408f, 264f, 87f))
                    addRegion("ammo", Rectangle2D.Float(309f, 641f, 264f, 53f))
                    extractRegions(page)
                }
                assertTrue(p1MechanicalRegions.getTextForRegion("traits")
                    .contains("RASGOS Y ATRIBUTOS"), "P1 real two-column trait panel absent.")
                assertTrue(p1MechanicalRegions.getTextForRegion("ammo")
                    .contains("MUNICIONES"), "P1 ammunition paper band absent.")
                assertFalse(p1MechanicalRegions.getTextForRegion("traits")
                    .contains("ARMA / ATAQUE / CONJURO"), "P1 traits overlap attacks.")
                assertFalse(p1MechanicalRegions.getTextForRegion("ammo")
                    .contains("N7"), "P1 ammunition overlaps spell slots.")
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
            assertTrue(p1.contains("ARMA / ATAQUE / CONJURO"))
            assertTrue(p1.contains("COMBATE Y SUPERVIVENCIA"))
            assertTrue(p1.contains("INSPIRACIÓN"))
            assertTrue(p1.contains("INICIATIVA"))
            assertTrue(p1.contains("COMPETENCIA"))
            assertTrue(p1.contains("JUGADOR"))
            assertFalse(p1.contains("HABILIDADES BAJO ATRIBUTO"),
                "C232: the stray caption is gone; skills stay physically under their attributes.")
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
            assertTrue(Regex("MUNICIONES(?! A P\\.)(?! /)").containsMatchIn(p1), "Golden MUNICIONES title.")
            assertTrue(p1.contains("Virotes"),
                "Persisted classified ammunition must be present physically on Aldren P1.")
            assertTrue(p1.contains("RASGOS Y ATRIBUTOS"))
            assertTrue(p1.contains("APTITUD MÁGICA"))
            assertTrue(p1.contains("N9"))
            assertTrue(printed.contains("C2 - Habilidades, acciones y recursos"))
            assertTrue(printed.contains("Second Wind"))
            assertTrue(printed.contains("RECURSOS Y MUNICIONES"))
            assertTrue(printed.contains("Action Surge"))
            assertTrue(printed.contains("MUNICIÓN Virotes"))
            assertTrue(printed.contains("CANTIDAD 20"))
            // C257: the golden title is MUNICIONES; the 20 marks are paper boxes.
            assertTrue(Regex("MUNICIONES(?! A P\\.)(?! /)").containsMatchIn(printed))
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
            assertTrue(p1.contains("HABILIDADES") && p1.contains("\uE109") && p1.contains("PERICIA"), // C310/C329 cartela
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
            assertTrue(p1.contains("APTITUD MÁGICA"))
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
            val pageTexts = (1..pdf.numberOfPages).map { physical ->
                PDFTextStripper().apply {
                    startPage = physical
                    endPage = physical
                }.getText(pdf)
            }
            // D-0076 §3.21: the long persisted special object now gets a
            // taller P2 row instead of a dedicated C2 page.
            assertTrue(pageTexts[1].contains("EQUIPO ESPECIAL") &&
                pageTexts[1].contains("Spellbook"),
                "Ilyra's long persisted special object must be printed on P2.")
            assertFalse(pageTexts.any { it.contains("C2 - Equipo especial") },
                "A special object that fits P2 must not get a C2 page.")
            assertTrue(printed.contains("Índice del Libro de Conjuros"))
            assertAllPhysicalBookReferences(pdf, generated.semantic.spells.map { it.spell.name })
            assertTrue(printed.contains("LANZAMIENTO DE CONJUROS"),
                "P1 casting and its separate spell references remain intact.")
            // C240: golden card without a DESCRIPCIÓN label; the real text stays.
            assertTrue(printed.replace(Regex("\\s+"), " ")
                .contains("Cantrip de evocación; ataque de conjuro de frío"),
                "Essential spellbook cards keep their complete descriptions.")
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
            assertTrue(targetContent.contains("NIVEL 0"))
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
                if (variant == PcSheetV11Variant.A) {
                    // C218 approved P1/C2 signed-ink grammar. These are
                    // genuine, persisted Mara numbers, NOT computed test
                    // fixtures or a guessed training proficiency state.
                    val actualC2 = pages.filter { it.contains(
                        "C2 - Habilidades, acciones y recursos",
                    ) }.joinToString("\n")
                    // C247: golden C2 attribute row — score box, modifier oval,
                    // name, SALVACIÓN and the real save value, in reading order.
                    val flat = actualC2.replace(Regex("\\s+"), " ")
                    assertTrue(flat.contains("13 +1 Cordura SALVACIÓN +0"),
                        "C2 Cordura must show the real score, modifier and save with a plus.")
                    assertTrue(flat.contains("18 +4 Éter SALVACIÓN +8"),
                        "C2 Éter must keep both real nonnegative signed values.")
                    assertTrue(flat.contains("11 +0 Renombre SALVACIÓN sin valor"),
                        "A missing source save must remain explicitly unresolved.")
                    assertTrue(actualC2.contains("+9") && actualC2.contains("+8"),
                        "C2 linked true skill totals must retain golden plus signs.")
                }
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
                        // V11: a skill detached from its P1 ability requires
                        // the REAL custom abbreviation, exactly three uppercase
                        // letters. A full ability name in parentheses is not
                        // the approved separate-context paper grammar.
                        val sourceAbbreviation = real.permanent.successor
                            .customAttributes.first { it.name == attribute }
                            .abbreviation.uppercase().take(3)
                        assertEquals(3, sourceAbbreviation.length)
                        assertTrue(pages.any { text -> text.contains("C2 - ") &&
                            text.contains(key + " (" + sourceAbbreviation + ")") },
                            "Detached C2 skill must print source-backed (XXX), " +
                                "not a full name or invented attribute.")
                        assertFalse(full.contains(key + " (" + attribute + ")"),
                            "A full custom attribute name is not a V11 (XXX) suffix.")
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
    fun g3OversizedRealCustomAttributeShowsItsIdentityOnEveryC2ContinuationColumnAB() {
        // A real Mara attribute + 140 explicitly linked skills is larger
        // than one physical C2 column. The original score row stays unique;
        // each continuation column must identify the same persisted source
        // without reprinting or inventing attribute values.
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        val prototype = original.permanent.successor.customAttributes.first {
            it.name == "Fortuna"
        }
        val templateSkill = original.permanent.closure.customSkills.first {
            it.name == "Contratos arcanos"
        }
        val relation = original.permanent.successor.customSkillAbilities.first {
            it.customSkillId == templateSkill.id
        }
        val attribute = prototype.copy(
            id = Uuid.parse("b1100000-0000-4000-8000-000000000001"),
            name = "QAMEGAATRIBUTO",
            abbreviation = "QMA",
            sortOrder = 2000,
        )
        val skills = (1..140).map { number ->
            templateSkill.copy(
                id = Uuid.parse("b1200000-0000-4000-8000-" +
                    number.toString().padStart(12, '0')),
                name = "QAMEGASKILL" + number.toString().padStart(3, '0'),
                sortOrder = 2000 + number,
            )
        }
        val changed = original.copy(
            permanent = original.permanent.copy(
                closure = original.permanent.closure.copy(
                    customSkills = original.permanent.closure.customSkills + skills,
                ),
                successor = original.permanent.successor.copy(
                    customAttributes = original.permanent.successor.customAttributes +
                        attribute,
                    customSkillAbilities = original.permanent.successor.customSkillAbilities +
                        skills.map { skill ->
                            relation.copy(
                                customSkillId = skill.id,
                                ability = io.github.mrsimkin.dndcustomaid.shared.character
                                    .CharacterAbilityReference.custom(attribute.id),
                            )
                        },
                ),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ),
                changed,
            ).bytes
            preserveRealApplicationPdf(bytes,
                "V11-INTERNAL-PROOF-C2-oversized-custom-attribute-" + variant + ".pdf")
            Loader.loadPDF(bytes).use { pdf ->
                val texts = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply {
                        startPage = page
                        endPage = page
                    }.getText(pdf)
                }
                val c2 = (0 until pdf.numberOfPages).filter { index ->
                    texts[index].contains("C2 - Habilidades, acciones y recursos")
                }.flatMap { pageIndex ->
                    val region = PDFTextStripperByArea().apply {
                        addRegion("left-body",
                            Rectangle2D.Float(40f, 90f, 262f, 630f))
                        addRegion("right-body",
                            Rectangle2D.Float(310f, 90f, 262f, 630f))
                        addRegion("left-context",
                            Rectangle2D.Float(40f, 75f, 262f, 16f))
                        addRegion("right-context",
                            Rectangle2D.Float(310f, 75f, 262f, 16f))
                        extractRegions(pdf.getPage(pageIndex))
                    }
                    listOf(
                        Triple(pageIndex + 1,
                            region.getTextForRegion("left-body"),
                            region.getTextForRegion("left-context")),
                        Triple(pageIndex + 1,
                            region.getTextForRegion("right-body"),
                            region.getTextForRegion("right-context")),
                    )
                }
                val active = c2.filter { (_, body, _) ->
                    body.contains("QAMEGASKILL")
                }
                assertTrue(active.size >= 3,
                    "Oversized group must span at least three physical C2 columns.")
                // C247: the golden attribute row prints the name once; continuation
                // columns carry it only in their context band.
                val originalScoreRows = c2.sumOf { (_, body, _) ->
                    Regex("QAMEGAATRIBUTO").findAll(body).count()
                }
                assertTrue(c2.any { (_, body, _) ->
                    Regex("16\\s+\\+3\\s+QAMEGAATRIBUTO\\s+SALVACIÓN").containsMatchIn(body)
                }, "The source attribute row keeps its real score and modifier.")
                assertEquals(1, originalScoreRows,
                    "Original custom attribute score must be printed only once.")
                val first = c2.indexOfFirst { it.second.contains("QAMEGAATRIBUTO") }
                assertTrue(first >= 0, "Source custom attribute row missing.")
                val subsequent = c2.drop(first + 1).filter { (_, body, _) ->
                    body.contains("QAMEGASKILL")
                }
                assertTrue(subsequent.size >= 2)
                subsequent.forEach { (_, _, header) ->
                    assertTrue(header.contains("CONTINÚA ATRIBUTO: QAMEGAATRIBUTO"),
                        "Continued skills require the true source attribute in " +
                            "the free per-column header band.")
                }
                val combined = texts.joinToString("\n")
                skills.forEach { skill ->
                    assertEquals(1, Regex(Regex.escape(skill.name)).findAll(combined).count(),
                        "Linked source skill is missing or repeated: " + skill.name)
                }
                assertTrue(texts.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(texts.last().contains("QAMEGASKILL"),
                    "Digital skill continuation cannot occupy the paper-only last page.")
                for (page in pdf.pages) {
                    assertEquals(PDRectangle.LETTER.width, page.mediaBox.width)
                    assertEquals(PDRectangle.LETTER.height, page.mediaBox.height)
                }
            }
        }
    }

    @Test
    fun g3CustomAttributesKeepLinkedSkillsWithinFittingPhysicalC2ColumnsAB() {
        // Real Mara source + source-typed UUID replicas create multiple C2
        // column boundaries. IDs and ability links are persisted only in this
        // test snapshot, never written back to Mara's saved backup.
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        val attrTemplate = original.permanent.successor.customAttributes.first {
            it.name == "Fortuna"
        }
        val skillTemplate = original.permanent.closure.customSkills.first {
            it.name == "Contratos arcanos"
        }
        val relation = original.permanent.successor.customSkillAbilities.first {
            it.customSkillId == skillTemplate.id
        }
        val copies = (1..72).map { index ->
            val number = index.toString().padStart(3, '0')
            val attr = attrTemplate.copy(
                id = Uuid.parse("a1100000-0000-4000-8000-" +
                    index.toString().padStart(12, '0')),
                name = "QAATR" + number +
                    if (index % 3 == 0) " extensible adicional para dos renglones medidos"
                    else "",
                abbreviation = "Q" + number.takeLast(2),
                sortOrder = 1000 + index,
            )
            val skill = skillTemplate.copy(
                id = Uuid.parse("a1200000-0000-4000-8000-" +
                    index.toString().padStart(12, '0')),
                name = "QASKILL" + number,
                sortOrder = 1000 + index,
            )
            Triple(attr, skill, relation.copy(
                customSkillId = skill.id,
                ability = io.github.mrsimkin.dndcustomaid.shared.character
                    .CharacterAbilityReference.custom(attr.id),
            ))
        }
        val source = original.copy(
            permanent = original.permanent.copy(
                closure = original.permanent.closure.copy(
                    customSkills = original.permanent.closure.customSkills +
                        copies.map { it.second },
                ),
                successor = original.permanent.successor.copy(
                    customAttributes = original.permanent.successor.customAttributes +
                        copies.map { it.first },
                    customSkillAbilities = original.permanent.successor.customSkillAbilities +
                        copies.map { it.third },
                ),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ), source,
            ).bytes
            preserveRealApplicationPdf(bytes,
                "V11-INTERNAL-PROOF-C2-grouped-custom-attributes-" + variant + ".pdf")
            Loader.loadPDF(bytes).use { pdf ->
                val pageTexts = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply {
                        startPage = page
                        endPage = page
                    }.getText(pdf)
                }
                val c2Areas = (0 until pdf.numberOfPages).filter { index ->
                    pageTexts[index].contains("C2 - Habilidades, acciones y recursos")
                }.flatMap { index ->
                    val stripper = PDFTextStripperByArea().apply {
                        addRegion("left", Rectangle2D.Float(38f, 85f, 270f, 635f))
                        addRegion("right", Rectangle2D.Float(306f, 85f, 270f, 635f))
                        extractRegions(pdf.getPage(index))
                    }
                    listOf(
                        (index + 1) to stripper.getTextForRegion("left"),
                        (index + 1) to stripper.getTextForRegion("right"),
                    )
                }
                assertTrue(c2Areas.size >= 4,
                    "Stress input must reach multiple C2 physical columns.")
                val first = pageTexts.first()
                val fullText = pageTexts.joinToString("\n")
                var checkedGroups = 0
                for ((attr, skill, _) in copies) {
                    val key = attr.name.substringBefore(' ')
                    if (first.contains(key)) continue // this attr is already on P1
                    val a = c2Areas.withIndex().filter { it.value.second.contains(key) }
                    val s = c2Areas.withIndex().filter {
                        it.value.second.contains(skill.name)
                    }
                    assertEquals(1, a.size, "One custom attribute ID must print once: " + key)
                    assertEquals(1, s.size, "One linked skill ID must print once: " + skill.name)
                    assertEquals(a.single().index, s.single().index,
                        "C2 cannot strand a fitting attribute and its linked skill " +
                            "in different physical columns: " + key)
                    assertTrue(fullText.contains(skill.name))
                    checkedGroups++
                }
                assertTrue(checkedGroups >= 60)
                assertTrue(pageTexts.last().lineSequence().any { it.trim() == "NOTAS" })
                for (page in pdf.pages) {
                    assertEquals(PDRectangle.LETTER.width, page.mediaBox.width)
                    assertEquals(PDRectangle.LETTER.height, page.mediaBox.height)
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
            // V11 detached-skill rule: use persisted custom abbreviations,
            // not the full attribute names that are already present in P1.
            assertTrue(c2.contains("Contratos arcanos (FOR)"))
            assertTrue(c2.contains("Sincronía (COR)"))
            assertTrue(c2.contains("Manipulación de éter (ETE)"))
            assertFalse(c2.contains("Contratos arcanos (Fortuna)") ||
                c2.contains("Sincronía (Cordura)") ||
                c2.contains("Manipulación de éter (Éter)"))
        }
    }

    @Test
    fun g3IlyraGenuineSingleOrdinaryAndSpecialC2ShareOnePhysicalPageAB() {
        // Real Ilyra source: exactly one leftover normal object and one
        // deferred Spellbook with location, notes and true active mark.
        // The former app emitted two mostly-empty dedicated C2 pages.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant,
                    PcSheetExportStateSelection.PERMANENT), original,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-C2-ordinary-plus-special-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply {
                        startPage = page
                        endPage = page
                    }.getText(pdf)
                }
                // C211 R1 (owner 2026-10-26): the leftover ordinary object now fits
                // P2 wrapped over several lines, so only the special item reaches C2.
                fun flat(s: String) = s.replace(Regex("\\s+"), " ")
                assertTrue(flat(pages[1]).contains("Suministros de calígrafo"),
                    "C211: P2 is filled before C2, long names wrap on P2.")
                assertFalse(pages.any { it.contains("C2 - Equipo y tesoro / EQUIPO") },
                    "No ordinary object is left for C2.")
                // D-0076 §3.21: the Spellbook (location, notes, ACTIVO) now gets a
                // taller P2 row, so Ilyra needs no equipment C2 at all.
                assertFalse(pages.any { it.contains("C2 - Equipo especial / continuación") },
                    "A special item that fits P2 in a taller row has no C2 page.")
                val p2 = flat(pages[1])
                for (required in listOf(
                    "EQUIPO ESPECIAL", "UBICACIÓN", "NOMBRE", "DESCRIPCIÓN",
                    "ACTIVO", "Spellbook", "Mochila", "Evocation Savant", "NOTAS",
                )) {
                    assertTrue(p2.contains(required),
                        "Missing real P2 special source/column: $required")
                }
                assertEquals(1, Regex("Suministros de calígrafo")
                    .findAll(flat(pages.joinToString("\n"))).count())
                // Once as the class trait identity in RASGOS, once in the
                // Spellbook notes: the special entry is never duplicated.
                assertEquals(2, Regex("Evocation Savant")
                    .findAll(flat(pages.joinToString("\n"))).count())
                assertFalse(pages[1].contains("CONTINÚA EN C2"),
                    "P2 must not point to a C2 that no longer exists.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
            }
        }
    }

    @Test
    fun actualIlyraTraitsShareMeasuredC2WithResourcesInsteadOfWastePage() {
        val source = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val generated = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT), source,
        )
        Loader.loadPDF(generated.bytes).use { pdf ->
            val pages = (1..pdf.numberOfPages).map { n ->
                PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
            }
            val mixed = pages.first { it.contains("C2 - Habilidades, acciones y recursos") }
            assertTrue(mixed.contains("RECURSOS Y MUNICIONES"),
                "Combining real modules must not delete existing P1 resources.")
            // The owner prohibited full Rasgo descriptions on paper.
            // This reduces their heights; a short summary belongs in P1/P2
            // and MUST NOT create a redundant C2 merely to satisfy an old
            // description-overflow test. Check actual source traits survive.
            val p1AndP2AndC2 = pages.filter {
                it.contains("P1 - Personaje") || it.contains("P2 - Equipo y tesoro") ||
                    it.contains("C2 - Habilidades, acciones y recursos")
            }.joinToString("\n")
            for (trait in source.permanent.sheet.traits) {
                assertTrue(p1AndP2AndC2.contains(trait.name),
                    "Real Rasgo identity disappeared from the paper: " + trait.name)
            }
            assertFalse(pages.any { it.contains("C2 - Rasgos / continuación") },
                "No dedicated C2 page may be fabricated just to print descriptions.")
            if (mixed.contains("RASGOS / CONTINUACIÓN P2")) {
                assertTrue(mixed.contains("P2 PÁG. 2"),
                    "True mixed P2 Rasgo continuation needs genuine backlink.")
            } else {
                assertFalse(mixed.contains("VIENE DE P2 PÁG."),
                    "A P1-only mixed C2 must not fabricate P2 provenance.")
            }
            assertTrue(pages.last().trim().endsWith("NOTAS"))
        }
    }


    @Test
    fun g3LongRealCharacterNameKeepsMeasuredOneLineHeaderAndFullC2SourceForAB() {
        // Authentic Aldren fields; mutate only the name with unique ordered
        // technical tokens. No invented name is attributed to the owner.
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val tokens = (1..64).map { "NOMBREQA" + it.toString().padStart(2, '0') }
        val fullName = "Aldren " + tokens.joinToString(" ") + " FINNOMBREQA"
        val source = original.copy(
            permanent = original.permanent.copy(
                sheet = original.permanent.sheet.copy(name = fullName),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                source,
            )
            preserveRealApplicationPdf(
                proof.bytes, "V11-INTERNAL-PROOF-P1-long-character-name-" + variant + ".pdf",
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { number ->
                    PDFTextStripper().apply {
                        startPage = number
                        endPage = number
                    }.getText(pdf)
                }
                val c2PageIndices = pages.indices.filter {
                    pages[it].contains("C2 - Habilidades, acciones y recursos")
                }
                assertTrue(c2PageIndices.isNotEmpty())
                // Count persisted data within the actual C2 BODY, not its
                // repeated character-name HEADER (which legitimately displays
                // the same initial name tokens on every physical page).
                val c2Text = c2PageIndices.joinToString("\n") { index ->
                    val area = PDFTextStripperByArea().apply {
                        addRegion("c2-body", Rectangle2D.Float(38f, 84f, 536f, 629f))
                        extractRegions(pdf.getPage(index))
                    }
                    area.getTextForRegion("c2-body")
                }
                assertTrue(c2Text.contains("NOMBRE COMPLETO DEL PERSONAJE:"),
                    "The full saved source name must have a real C2 home.")
                tokens.forEach { token ->
                    assertEquals(1, Regex(Regex.escape(token)).findAll(c2Text).count(),
                        "Each source name token must be preserved exactly once: " + token)
                }
                assertTrue(c2Text.contains("FINNOMBREQA"))
                pages.forEachIndexed { index, text ->
                    assertTrue(text.contains("VER C2"),
                        "Every physical page needs the same navigable one-line name: " +
                            (index + 1))
                }
                assertTrue(pages.first().contains("CONTINÚA EN C2 PÁG."),
                    "P1 must link to the genuine page containing the full name.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(pages.last().contains("NOMBRE COMPLETO DEL PERSONAJE:"))
            }
        }
    }


    @Test
    fun g3RealCharacterNameHardBreakIsNavigableAndLosslessInBothV11Variants() {
        // Actual Aldren backup with only its pasted character name modified.
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val rawName = "QACABECERA\r\nQANOMBRECOMPLETO\tDELPC"
        val input = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(name = rawName),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                input,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-P1-character-name-hard-break-" + variant + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply {
                        startPage = page
                        endPage = page
                    }.getText(pdf)
                }
                pages.forEach { page ->
                    assertTrue(page.contains("VER C2"),
                        "Every physical header must navigate to the unmodified name.")
                }
                val c2Body = pdf.pages.mapNotNull { page ->
                    val area = PDFTextStripperByArea().apply {
                        addRegion("body", Rectangle2D.Float(38f, 84f, 536f, 629f))
                        extractRegions(page)
                    }
                    area.getTextForRegion("body")
                        .takeIf { it.contains("NOMBRE COMPLETO DEL PERSONAJE:") }
                }.joinToString("\n")
                assertEquals(1, Regex("QACABECERA").findAll(c2Body).count())
                assertEquals(1, Regex("QANOMBRECOMPLETO").findAll(c2Body).count())
                assertEquals(1, Regex("DELPC").findAll(c2Body).count())
                assertTrue(pages.first().contains("CONTINÚA EN C2 PÁG."),
                    "P1 must link to its actual C2 source body.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                assertTrue(pdf.numberOfPages >= 4)
            }
        }
    }


    @Test
    fun g3P1RealFourFiveSlotPaperMarksAreTrueBoxesAB() {
        // Ilyra has FOUR real persisted level-one slots. Exercise also a
        // genuine persisted FIVE-slot value at level two, altering only its
        // counts in an otherwise untouched source backup.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val slots = original.permanent.sheet.spellSlots
        assertEquals(4, slots.single { it.level == 1 }.totalSlots)
        assertEquals(3, slots.single { it.level == 2 }.totalSlots)
        val changed = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(spellSlots = slots.map { slot ->
                if (slot.level == 2) slot.copy(totalSlots = 5, spentSlots = 3)
                else slot
            }),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val result = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                changed,
            )
            preserveRealApplicationPdf(result.bytes,
                "V11-INTERNAL-PROOF-P1-real-four-five-slot-boxes-" + variant.name + ".pdf")
            Loader.loadPDF(result.bytes).use { pdf ->
                val p1 = pdf.getPage(0)
                assertEquals(612f, p1.mediaBox.width)
                assertEquals(792f, p1.mediaBox.height)
                val content = p1.contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                fun actualSquare(x: Int): Int = Regex(
                    """(?m)(?:^|\s)\Q$x\E(?:\.0)?\s+230(?:\.0)?\s+8(?:\.0)?\s+8(?:\.0)?\s+re(?:\s|$)""",
                ).findAll(content).count()
                // N1=4: 334,346,358,370; N2=5: 419..459 step 10 (C255); y=230pt.
                for (x in listOf(334, 346, 358, 370, 419, 429, 439, 449, 459)) {
                    assertEquals(1, actualSquare(x),
                        "P1 must draw one true usable square for source x=$x.")
                }
                assertEquals(0, actualSquare(382), "Do not invent fifth box for N1=4.")
                assertEquals(0, actualSquare(469), "Do not invent sixth box for N2=5.")
                val pageText = PDFTextStripper().apply {
                    startPage = 1; endPage = 1
                }.getText(pdf)
                assertTrue(pageText.contains("APTITUD MÁGICA"))
                assertFalse(pageText.contains("GASTADOS"),
                    "C250: golden P1 slot boxes carry the counts; no separate header.")
                assertTrue(PDFTextStripper().getText(pdf).contains("NOTAS"))
            }
        }
    }


    @Test
    fun g3P1SixPersistedSpellSlotsFitWithoutShrinkingPaperBoxesAB() {
        // C227/C250: real Ilyra backup, with its first-level total explicitly
        // increased from four to six and spent count to four. This exercises
        // the real PDF renderer, not a visual mock or fabricated source ID.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val slots = original.permanent.sheet.spellSlots
        assertEquals(4, slots.single { it.level == 1 }.totalSlots)
        val six = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(spellSlots = slots.map {
                if (it.level == 1) it.copy(totalSlots = 6, spentSlots = 4) else it
            }),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val result = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                six,
            )
            preserveRealApplicationPdf(result.bytes,
                "V11-INTERNAL-PROOF-P1-six-real-spell-slots-" + variant.name + ".pdf")
            Loader.loadPDF(result.bytes).use { pdf ->
                val p1 = pdf.getPage(0)
                assertEquals(612f, p1.mediaBox.width)
                assertEquals(792f, p1.mediaBox.height)
                val native = p1.contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                fun squareAt(x: Int, y: Int): Int = Regex(
                    """(?m)(?:^|\s)\Q$x\E(?:\.0)?\s+\Q$y\E(?:\.0)?\s+8(?:\.0)?\s+8(?:\.0)?\s+re(?:\s|$)""",
                ).findAll(native).count()
                // N1: six real 6.5pt squares, step 8.3 (C255), so the real
                // "4/6" count fits the cell; no seventh.
                val small = Regex(
                    """(?m)(?:^|\s)([\d.]+)\s+([\d.]+)\s+6\.5\s+6\.5\s+re(?:\s|$)""",
                ).findAll(native).map { it.groupValues[1].toFloat() to it.groupValues[2].toFloat() }.toList()
                assertEquals(6, small.size, "Six genuine N1 squares and no seventh.")
                small.sortedBy { it.first }.forEachIndexed { n, (x, y) ->
                    assertTrue(kotlin.math.abs(x - (334f + n * 8.3f)) < 0.05f &&
                        kotlin.math.abs(y - 230.75f) < 0.05f, "N1 square $n at ($x, $y).")
                }
                // Unchanged real N2=3: five-point gaps are preserved.
                for (x in listOf(419, 431, 443)) {
                    assertEquals(1, squareAt(x, 230))
                }
                assertEquals(0, squareAt(453, 230),
                    "Existing 1..5 spacing must never switch to six-slot spacing.")
                val p1Text = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                assertTrue(p1Text.contains("APTITUD MÁGICA"))
                assertTrue(PDFTextStripper().getText(pdf).contains("NOTAS"))
            }
        }
    }

    @Test
    fun g3OversizedPersistedSpellSlotCountersUseP1C2WithoutClippingOrInventingValues() {
        // A genuine Aldren backup supplies every unchanged field; only the
        // explicit persisted slot counters are stressed at the Int boundary.
        // The fixed 3x3 P1 cells cannot print ten-digit counters legibly.
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val source = original.copy(
            permanent = original.permanent.copy(
                sheet = original.permanent.sheet.copy(
                    spellSlots = listOf(
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSlot(
                            level = 1, totalSlots = 2147483647, spentSlots = 2147483646,
                        ),
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSlot(
                            level = 2, totalSlots = 3, spentSlots = 1,
                        ),
                    ),
                ),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source,
            )
            preserveRealApplicationPdf(
                proof.bytes, "V11-INTERNAL-PROOF-P1-long-spell-slots-" + variant + ".pdf",
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { number ->
                    PDFTextStripper().apply {
                        startPage = number
                        endPage = number
                    }.getText(pdf)
                }
                val p1 = pages.first()
                val c2 = pages.filter {
                    it.contains("C2 - Habilidades, acciones y recursos")
                }.joinToString("\n")
                assertTrue(p1.contains("VER C2"),
                    "Oversized slot quantities need a visible P1 continuation cue.")
                assertFalse(p1.contains("2147483647") || p1.contains("2147483646"),
                    "Oversized values cannot spill into adjacent P1 cells.")
                assertTrue(p1.contains("N2"),
                    "A normal, independent slot level remains on P1.")
                assertTrue(c2.contains("ESPACIOS REALES NIVEL 1"))
                assertEquals(1, Regex("2147483647").findAll(c2).count())
                assertEquals(1, Regex("2147483646").findAll(c2).count())
                assertFalse(c2.contains("ESPACIOS REALES NIVEL 2"),
                    "Normal slot counters must not be moved unnecessarily.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
            }
        }
    }

    @Test
    fun g3ExtremeRealMulticlassHitDiceContinueInC2WithoutP1CrashOrDataLoss() {
        // Build a discriminating stress case from a genuine Aldren backup;
        // mutate only real class rows, preserving stable unique source IDs.
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val first = original.permanent.sheet.classes.first()
        val classes = (1..16).map { n ->
            first.copy(
                id = if (n == 1) first.id else
                    Uuid.parse("00000000-0000-4000-8000-" + n.toString().padStart(12, '0')),
                name = "Clase " + n,
                level = 1,
                hitDieSides = 100 + n,
                hitDiceRemaining = n,
                sortOrder = n,
            )
        }
        val source = original.copy(
            permanent = original.permanent.copy(
                sheet = original.permanent.sheet.copy(classes = classes),
            ),
        )
        val expectedDice = classes.joinToString(" / ") {
            it.hitDiceRemaining.toString() + "d" + it.hitDieSides
        }
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source,
            )
            preserveRealApplicationPdf(
                proof.bytes, "V11-INTERNAL-PROOF-P1-multiclass-dice-" + variant + ".pdf",
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { number ->
                    PDFTextStripper().apply {
                        startPage = number
                        endPage = number
                    }.getText(pdf)
                }
                assertTrue(pages.first().contains("DADOS"))
                assertTrue(pages.first().contains("VER C2"),
                    "Fixed P1 combat cell must use a physical C2 navigation marker.")
                assertFalse(pages.first().contains(expectedDice),
                    "Do not overflow a fixed P1 paper cell.")
                val c2Pages = pages.filter {
                    it.contains("C2 - Habilidades, acciones y recursos")
                }
                assertTrue(c2Pages.isNotEmpty(), "Real source must continue in C2.")
                val c2Text = c2Pages.joinToString("\n")
                assertTrue(c2Text.contains("DADOS DE GOLPE: 1d101"))
                classes.forEach { row ->
                    val label = row.hitDiceRemaining.toString() + "d" + row.hitDieSides
                    assertEquals(1, Regex(Regex.escape(label)).findAll(c2Text).count(),
                        "Every persisted class hit die must appear once in C2: " + label)
                }
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(pages.last().contains("DADOS:"))
            }
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
    fun g3P3ThreeLongPersistedParagraphsKeepOrderAcrossPhysicalPagesAB() {
        // P304 needs actual paragraph boundaries, not only a single
        // giant repeated sentence. The fixture is derived from a real App
        // snapshot and leaves persisted source untouched.
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val paragraphs = listOf(
            "P3-PARRAFO-UNO-INICIO " +
                "RASTROUNO testimonio escrito sin cambio de identidad. ".repeat(190) +
                " P3-PARRAFO-UNO-FIN",
            "P3-PARRAFO-DOS-INICIO " +
                "RASTRODOS trayectoria cronologica del personaje original. ".repeat(190) +
                " P3-PARRAFO-DOS-FIN",
            "P3-PARRAFO-TRES-INICIO " +
                "RASTROTRES cierre narrativo con detalles persistidos completos. ".repeat(190) +
                " P3-PARRAFO-TRES-FIN",
        )
        val story = paragraphs.joinToString("\n\n")
        val updated = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(
                background = original.permanent.sheet.background.copy(story = story),
            ),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val request = PcSheetV11ExportRequest(
                variant, PcSheetExportStateSelection.PERMANENT,
            )
            val semantic = io.github.mrsimkin.dndcustomaid.shared.character
                .PcSheetV11SemanticMapper.map(request, updated)
            val mapped = io.github.mrsimkin.dndcustomaid.shared.character
                .PcSheetV11FlowSourceMapper.from(semantic)
            assertEquals(story, mapped.narrative.single {
                it.sourceKey == "narrative:story"
            }.text, "The actual source must retain every paragraph boundary.")
            val proof = DesktopPcSheetExportService().generateV11Proof(request, updated)
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-P3-three-paragraphs-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { physical ->
                    PDFTextStripper().apply {
                        startPage = physical
                        endPage = physical
                    }.getText(pdf)
                }
                val continuation = pages.filter {
                    it.contains("P3 - Narrativa / continuación")
                }
                assertTrue(continuation.size >= 3,
                    "Three long source paragraphs must span genuine P3 pages.")
                assertTrue(continuation.drop(1).all {
                    it.contains("HISTORIA Y CRÓNICA / CONTINUACIÓN")
                })
                val allText = pages.joinToString("\n")
                val orderedMarkers = listOf(
                    "P3-PARRAFO-UNO-INICIO", "P3-PARRAFO-UNO-FIN",
                    "P3-PARRAFO-DOS-INICIO", "P3-PARRAFO-DOS-FIN",
                    "P3-PARRAFO-TRES-INICIO", "P3-PARRAFO-TRES-FIN",
                )
                val positions = orderedMarkers.map { marker ->
                    assertEquals(1, Regex(Regex.escape(marker)).findAll(allText).count(),
                        "Original paragraph marker omitted or duplicated: " + marker)
                    allText.indexOf(marker)
                }
                assertTrue(positions.zipWithNext().all { (left, right) -> left < right },
                    "Printed P3 source paragraphs must keep their genuine order.")
                for (word in listOf("RASTROUNO", "RASTRODOS", "RASTROTRES")) {
                    assertEquals(190, Regex("\\b" + word + "\\b")
                        .findAll(allText).count(),
                        "Every real narrative sentence must survive unchanged: " + word)
                }
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                assertTrue(orderedMarkers.none { it in pages.last() },
                    "No digital narrative may continue on final paper-only NOTAS.")
            }
        }
    }

    @Test
    fun g3P3ExtremeStoryContinuationKeepsRealSourceHeadingOnEveryLaterPageAB() {
        // A real source-backed history can occupy SEVERAL physical P3
        // continuation sheets. Heading context must survive page turns,
        // with no duplicate story content, fake narrative or extra sheet.
        val actual = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val story = "P3LONG-CONTINUATION-START " +
            "Viaje registrado con fechas y testigos verificables. ".repeat(500) +
            " P3LONG-CONTINUATION-END"
        val patched = actual.copy(permanent = actual.permanent.copy(
            sheet = actual.permanent.sheet.copy(
                background = actual.permanent.sheet.background.copy(story = story),
            ),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                patched,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-P3-long-story-heading-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { physical ->
                    PDFTextStripper().apply {
                        startPage = physical; endPage = physical
                    }.getText(pdf)
                }
                val continuation = pages.filter {
                    it.contains("P3 - Narrativa / continuación")
                }
                assertTrue(continuation.size >= 2,
                    "Stress fixture must exercise at least TWO P3 continuation sheets.")
                assertTrue(continuation.drop(1).all {
                    it.contains("HISTORIA Y CRÓNICA / CONTINUACIÓN")
                }, "Every further P3 physical page must name its original story.")
                val combined = pages.joinToString("\n")
                assertEquals(1, Regex("P3LONG-CONTINUATION-START").findAll(combined).count())
                assertEquals(1, Regex("P3LONG-CONTINUATION-END").findAll(combined).count())
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" },
                    "The final page remains exclusively paper notes.")
                assertFalse(pages.last().contains("P3LONG-CONTINUATION-END"))
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
    fun g3DenseRealCantripsKeepLevelZeroContextAcrossPhysicalColumnsAB() {
        // The old dense regression exercises only level 1. Cantrips must retain
        // TRUCOS / NIVEL 0 context even when their true heading lives in another
        // physical column or page; never replace it with an invented level 1.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val base = original.permanent.sheet.spells.first {
            it.sourceAssociations.isNotEmpty()
        }
        val stressSpells = (1..175).map { number ->
            base.copy(
                id = Uuid.parse(
                    "f5000000-0000-4000-8000-" +
                        number.toString().padStart(12, '0'),
                ),
                name = "QAP4TRUCO" + number.toString().padStart(3, '0'),
                level = 0,
                sortOrder = number,
            )
        }
        val input = original.copy(
            permanent = original.permanent.copy(
                sheet = original.permanent.sheet.copy(spells = stressSpells),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ),
                input,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-P4-dense-cantrips-" + variant + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { index ->
                    PDFTextStripper().apply {
                        startPage = index
                        endPage = index
                    }.getText(pdf)
                }
                val firstP4 = pages.indexOfFirst { it.contains("P4 - Conjuros /") }
                assertTrue(firstP4 >= 0, "Real cantrips must produce a P4.")
                val p4Pages = pages.filter { it.contains("P4 - Conjuros /") }
                assertTrue(p4Pages.size >= 2,
                    "175 cantrips must exercise physical P4 page continuation.")
                assertTrue(p4Pages.first().contains("TRUCOS 0"),
                    "Source level 0 must retain the approved cantrip heading.")
                val strips = PDFTextStripperByArea().apply {
                    // C322: the continuation band sits just above the golden column top (y630.43).
                    addRegion("column2", Rectangle2D.Float(222f, 148f, 170f, 16f))
                    addRegion("column3", Rectangle2D.Float(404f, 148f, 170f, 16f))
                    extractRegions(pdf.getPage(firstP4))
                }
                for (region in listOf("column2", "column3")) {
                    assertTrue(
                        strips.getTextForRegion(region)
                            .contains("TRUCOS 0 / CONTINUACIÓN"),
                        "Real P4 cantrips must retain level 0 in each physical column.")
                }
                assertTrue(p4Pages.drop(1).any {
                    it.contains("QAP4TRUCO") &&
                        it.contains("TRUCOS 0 / CONTINUACIÓN")
                }, "A later P4 page must identify continued level-zero spells.")
                val allP4 = p4Pages.joinToString("\n")
                stressSpells.forEach { spell ->
                    assertEquals(1, Regex(Regex.escape(spell.name)).findAll(allP4).count(),
                        "Every source-backed cantrip must be printed exactly once.")
                }
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(pages.last().contains("QAP4TRUCO"),
                    "Cantrip overflow must never use the final handwritten page.")
                assertFalse(allP4.contains("NIVEL 1 / CONTINUACIÓN"),
                    "Do not relabel persisted cantrips as level-one spells.")
                for (page in pdf.pages) {
                    assertEquals(PDRectangle.LETTER.width, page.mediaBox.width)
                    assertEquals(PDRectangle.LETTER.height, page.mediaBox.height)
                }
            }
        }
    }

    @Test
    fun g3DenseRealLevelOneSpellsKeepLevelContextOnP4ContinuationPagesAB() {
        // Start from authentic Ilyra, changing only the persisted spell rows.
        // Dense single-level input exposes continuations without source headings;
        // do not fabricate spell levels or give ownership to a synthetic name.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val base = original.permanent.sheet.spells.first {
            it.sourceAssociations.isNotEmpty()
        }
        val stressSpells = (1..175).map { number ->
            base.copy(
                id = Uuid.parse(
                    "f4000000-0000-4000-8000-" +
                        number.toString().padStart(12, '0'),
                ),
                name = "QAP4UNO" + number.toString().padStart(3, '0'),
                level = 1,
                sortOrder = number,
            )
        }
        val actual = original.copy(
            permanent = original.permanent.copy(
                sheet = original.permanent.sheet.copy(spells = stressSpells),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ),
                actual,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-P4-dense-single-level-" + variant + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pageTexts = (1..pdf.numberOfPages).map { physical ->
                    PDFTextStripper().apply {
                        startPage = physical
                        endPage = physical
                    }.getText(pdf)
                }
                val p4Pages = pageTexts.filter { it.contains("P4 - Conjuros /") }
                assertTrue(p4Pages.size >= 2,
                    "This stress fixture must actually exercise P4 page continuation.")
                assertTrue(p4Pages.first().contains("NIVEL 1"))
                // The real three-column first P4 page begins mid-level 1
                // in BOTH column 2 and 3. Extract only their previously
                // unused top strips; body text cannot satisfy this assertion.
                val firstP4Physical = pageTexts.indexOfFirst {
                    it.contains("P4 - Conjuros /")
                }
                assertTrue(firstP4Physical >= 0)
                val area = PDFTextStripperByArea().apply {
                    // C322: the continuation band sits just above the golden column top (y630.43).
                    addRegion("column2-context",
                        Rectangle2D.Float(222f, 148f, 170f, 16f))
                    addRegion("column3-context",
                        Rectangle2D.Float(404f, 148f, 170f, 16f))
                    extractRegions(pdf.getPage(firstP4Physical))
                }
                for (region in listOf("column2-context", "column3-context")) {
                    assertTrue(area.getTextForRegion(region)
                        .contains("NIVEL 1 / CONTINUACIÓN"),
                        "A new P4 physical column must identify the source level.")
                }
                val pageWithContinuedLevel = p4Pages.drop(1).filter {
                    it.contains("QAP4UNO") && !it.contains(Regex("""(?m)^NIVEL 1\\s*$"""))
                }
                assertTrue(pageWithContinuedLevel.isNotEmpty(),
                    "P4 continuation must contain actual level-1 source entries.")
                pageWithContinuedLevel.forEach { page ->
                    assertTrue(page.contains("NIVEL 1 / CONTINUACIÓN"),
                        "Continued source rows must retain their true level label.")
                }
                val allP4 = p4Pages.joinToString("\n")
                stressSpells.forEach { spell ->
                    assertEquals(1, Regex(Regex.escape(spell.name)).findAll(allP4).count(),
                        "Every real persisted spell must remain exactly once.")
                }
                assertTrue(pageTexts.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(pageTexts.last().contains("QAP4UNO"))
                for (page in pdf.pages) {
                    assertEquals(PDRectangle.LETTER.width, page.mediaBox.width)
                    assertEquals(PDRectangle.LETTER.height, page.mediaBox.height)
                }
            }
        }
    }

    @Test
    fun g3SameNameDistinctSpellSourcesKeepSourceIdentityAndPreparationOnP4AB() {
        // Real Ilyra backup, with only one genuine spell's associations and
        // two source names changed to form this discriminating technical case.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val sheet = original.permanent.sheet
        val selected = sheet.spells.first { it.sourceAssociations.isNotEmpty() }
        val existingAssociation = selected.sourceAssociations.first()
        val realSource = sheet.spellcastingSources.first {
            it.id == existingAssociation.sourceId
        }
        val secondId = Uuid.parse("abcdef12-0000-4000-8000-000000000173")
        val fixtureName = "QA FUENTE HOMÓNIMA"
        val second = realSource.copy(
            id = secondId, name = fixtureName, sortOrder = realSource.sortOrder + 1,
        )
        val altered = original.copy(
            permanent = original.permanent.copy(
                sheet = sheet.copy(
                    spellcastingSources = sheet.spellcastingSources.map {
                        if (it.id == realSource.id) it.copy(name = fixtureName) else it
                    } + second,
                    spells = sheet.spells.map { spell ->
                        if (spell.id == selected.id) spell.copy(
                            name = "QA CONJURO DE DOS ORÍGENES",
                            sourceAssociations = listOf(
                                existingAssociation.copy(prepared = true),
                                io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSourceAssociation(
                                    sourceId = secondId, prepared = false,
                                ),
                            ),
                        ) else spell
                    },
                ),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                altered,
            )
            preserveRealApplicationPdf(
                proof.bytes, "V11-INTERNAL-PROOF-P4-same-name-distinct-sources-" + variant + ".pdf",
            )
            val target = proof.semantic.spells.first {
                it.spell.id == selected.id
            }
            assertEquals(2, target.sources.size)
            assertEquals(listOf(true, false), target.sources.map { it.prepared })
            val tags = io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpellSourceLabels
                .forSpells(proof.semantic.spells)
            val label1 = tags.getValue(realSource.id)
            val label2 = tags.getValue(secondId)
            assertTrue(label1.startsWith(fixtureName + " (#"))
            assertTrue(label2.startsWith(fixtureName + " (#"))
            assertTrue(label1 != label2)
            Loader.loadPDF(proof.bytes).use { pdf ->
                val p4Text = (1..pdf.numberOfPages).map { index ->
                    PDFTextStripper().apply {
                        startPage = index; endPage = index
                    }.getText(pdf)
                }.filter { it.contains("P4 - Conjuros") }
                    .joinToString(" ").replace(Regex("\\s+"), " ")
                assertTrue(p4Text.contains("QA CONJURO DE DOS ORÍGENES"))
                // C258 (owner 2026-10-28): one row per source, each with its own
                // label and no printed preparation mark.
                assertEquals(2, Regex("QA CONJURO DE DOS ORÍGENES").findAll(p4Text).count(),
                    "The spell must print one row per real source UUID.")
                assertTrue(p4Text.contains(label1) && p4Text.contains(label2),
                    "Each real source keeps its own label on its own row.")
                assertFalse(p4Text.contains("[X]") || p4Text.contains("[ ] "),
                    "C258: preparation is never printed as a mark.")
                assertTrue(PDFTextStripper().getText(pdf).contains("NOTAS"))
                assertEquals(PDRectangle.LETTER.width, pdf.getPage(0).mediaBox.width)
                assertEquals(PDRectangle.LETTER.height, pdf.getPage(0).mediaBox.height)
            }
        }
    }


    @Test
    fun g3P4MultipleRealSourceIdsNeverClaimUnknownDcAndAttackAsUniversalAB() {
        // An Ilyra spell and its actual source remain genuine.
        // Add a real persisted second source ID, deliberately without
        // a casting profile; null source DC/attack must not be presented
        // as the same numbers as a known profile. No layout specification
        // for future per-source cards is invented by this test.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val sheet = original.permanent.sheet
        val selected = sheet.spells.first { it.sourceAssociations.isNotEmpty() }
        val primary = sheet.spellcastingSources.first {
            it.id == selected.sourceAssociations.first().sourceId
        }
        val addedId = Uuid.parse("abcdef21-0000-4000-8000-000000000173")
        val added = primary.copy(
            id = addedId, name = "QAORIGENSINPERFIL",
            linkedClassId = null,
            originKind =
                io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingOriginKind.OTHER,
            originReferenceId = null,
            sortOrder = primary.sortOrder + 1,
        )
        val stressed = original.copy(
            permanent = original.permanent.copy(
                sheet = sheet.copy(
                    spellcastingSources = sheet.spellcastingSources + added,
                    spells = sheet.spells.map { spell ->
                        if (spell.id == selected.id) spell.copy(
                            sourceAssociations = spell.sourceAssociations.mapIndexed {
                                index, association ->
                                if (index == 0) association.copy(prepared = true)
                                else association
                            } + io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSourceAssociation(
                                sourceId = addedId, prepared = false,
                            ),
                        ) else spell
                    },
                ),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ), stressed,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-P4-conflicting-magic-reference-" +
                    variant.name + ".pdf")
            val actual = proof.semantic.spells.first { it.spell.id == selected.id }
            assertEquals(2, actual.sources.size)
            assertEquals(listOf(true, false), actual.sources.map { it.prepared })
            val varying = io.github.mrsimkin.dndcustomaid.shared.character
                .PcSheetV11SpellSourceLabels.conflictingReferenceFields(
                    proof.semantic.spells,
                )
            assertTrue("CD SALVACIÓN" in varying || "ATAQUE MÁGICO" in varying,
                "The synthetic persisted second source must ACTUALLY differ.")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val content = PDFTextStripper().getText(pdf)
                val p4 = (1..pdf.numberOfPages).map { physical ->
                    PDFTextStripper().apply {
                        startPage = physical
                        endPage = physical
                    }.getText(pdf)
                }.filter { it.contains("P4 - Conjuros") }.joinToString("\n")
                // C231 (owner rule): sources are labelled by origin TYPE, so the
                // added OTHER source reads "Otro" and its name is not printed.
                assertEquals(4, proof.semantic.spells.flatMap { it.sources }
                    .distinctBy { it.sourceId }.size)
                assertFalse(p4.contains("QAORIGENSINPERFIL"),
                    "C231: only the class keeps its name; other sources show their type.")
                assertTrue(p4.contains("Otro"),
                    "The additional persisted source must survive on its own row.")
                assertTrue(showsPerSource(
                    p4.substringAfter("REFERENCIA MÁGICA").substringBefore("NIVEL"),
                    proof, "CD SALVACIÓN"),
                    "C231: the CD card lists every source by its type label.")
                assertFalse(p4.contains("[X]") || p4.contains("[ ] "),
                    "C258: no source acquires a printed preparation mark.")
                assertTrue(content.contains("NOTAS"))
            }
        }
    }




    /**
     * C230: P4 lists every real source with its value, either on one line
     * ("Mago 15 / Hechicero 17") or, for long names, one source per line.
     */
    private fun showsPerSource(
        text: String, proof: DesktopPcSheetV11PdfProof, field: String,
    ): Boolean {
        val parts = requireNotNull(io.github.mrsimkin.dndcustomaid.shared.character
            .PcSheetV11SpellSourceLabels.perSourceParts(
                proof.semantic.spells, field,
            ) { id -> proof.semantic.attributes.firstOrNull {
                it.reference.customAttributeId == id
            }?.abbreviation })
        return text.contains(parts.joinToString(" / ")) || parts.all { text.contains(it) }
    }

    @Test
    fun g3CustomMaraSourceAbilityUsesExistingTruthfulP1AndP4CueAB() {
        // This is the owner's existing persisted Mara backup: global OTHER,
        // genuine custom attribute and genuine associated spell source.
        // No source data or layout is modified to manufacture a disagreement.
        val real = sources("03_mara_siete_umbrales_custom_extended.json")
        assertEquals(
            io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility.OTHER,
            real.permanent.sheet.spellcastingAbility,
        )
        val projected = real.permanent.successor.spellcastingProfiles
        assertTrue(projected.any { it.ability.customAttributeId != null },
            "Mara must really have a configured source-specific custom ability.")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val result = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                real,
            )
            val labels = io.github.mrsimkin.dndcustomaid.shared.character
                .PcSheetV11SpellSourceLabels
            assertTrue("APTITUD" in labels.contradictoryOverallFields(
                result.semantic.spells, real.permanent.sheet.spellcastingAbility,
                real.permanent.sheet.spellSaveDc,
                real.permanent.sheet.spellAttackModifier,
            ))
            assertTrue(result.semantic.spells.flatMap { it.sources }.any {
                it.castingAbility.customAttributeId != null
            }, "A cue requires a genuine source association, not only global OTHER.")
            preserveRealApplicationPdf(result.bytes,
                "V11-INTERNAL-PROOF-P1-P4-Mara-custom-aptitude-" + variant.name + ".pdf")
            Loader.loadPDF(result.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply { startPage = page; endPage = page }
                        .getText(pdf)
                }
                val p1 = pages.first()
                assertEquals(0, Regex("SEGÚN FUENTE").findAll(p1).count(),
                    "C231: P1 lists the real source value when it fits its card.")
                assertTrue(p1.contains("Cronomancia umbral"),
                    "C231: the single class source keeps its class name on P1.")
                val p4 = pages.filter { it.contains("P4 - Conjuros") }
                assertTrue(p4.isNotEmpty())
                val ref = p4.first().substringAfter("REFERENCIA MÁGICA")
                    .substringBefore("NIVEL")
                assertEquals(0, Regex("SEGÚN FUENTE").findAll(ref).count(),
                    "C230: P4 prints real per-source values instead of the cue.")
                for (field in listOf("APTITUD", "CD SALVACIÓN")) {
                    assertTrue(showsPerSource(ref, result, field),
                        "C230: P4 $field must list every real source value.")
                }
                assertTrue(ref.contains("+8"),
                    "Unaffected real spell attack value must remain source accurate.")
                assertTrue(pages.last().contains("NOTAS"))
            }
        }
    }


    @Test
    fun g3IlyraConfiguredCastingSourceKeepsMetricsVisibleWhenOverallBlankAB() {
        // Source = genuine Ilyra persisted wizard profile. Only two optional
        // overall reference numbers become absent; the profile still computes
        // the real source-specific CD/attack from INT and proficiency.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        assertEquals(15, original.permanent.sheet.spellSaveDc)
        assertEquals(7, original.permanent.sheet.spellAttackModifier)
        val changed = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(
                spellSaveDc = null, spellAttackModifier = null,
            ),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val result = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                changed,
            )
            val sources = result.semantic.spells.flatMap { it.sources }
                .distinctBy { it.sourceId }
            assertTrue(sources.isNotEmpty())
            assertTrue(sources.all { it.saveDc == 15 && it.spellAttackModifier == 7 },
                "The genuine source profile must retain its derivable metrics.")
            val labels = io.github.mrsimkin.dndcustomaid.shared.character
                .PcSheetV11SpellSourceLabels
            assertEquals(setOf("CD SALVACIÓN", "ATAQUE MÁGICO"),
                labels.contradictoryOverallFields(
                    result.semantic.spells, changed.permanent.sheet.spellcastingAbility,
                    changed.permanent.sheet.spellSaveDc,
                    changed.permanent.sheet.spellAttackModifier,
                ))
            preserveRealApplicationPdf(result.bytes,
                "V11-INTERNAL-PROOF-P1-P4-Ilyra-source-metrics-without-overall-" +
                    variant.name + ".pdf")
            Loader.loadPDF(result.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply { startPage = page; endPage = page }.getText(pdf)
                }
                val p1 = pages.first()
                // C256: both missing global metrics list their genuine sources.
                assertEquals(0, Regex("SEGÚN FUENTE").findAll(p1).count(),
                    "P1 must list real sources when they fit its golden card.")
                assertTrue(showsPerSource(p1, result, "CD SALVACIÓN") &&
                    showsPerSource(p1, result, "ATAQUE MÁGICO"),
                    "Both missing global metrics must refer to genuine source.")
                assertTrue(p1.contains("INT"))
                val p4 = pages.filter { it.contains("P4 - Conjuros") }
                assertTrue(p4.isNotEmpty())
                val ref = p4.first().substringAfter("REFERENCIA MÁGICA")
                    .substringBefore("NIVEL")
                assertEquals(0, Regex("SEGÚN FUENTE").findAll(ref).count())
                for (field in listOf("CD SALVACIÓN", "ATAQUE MÁGICO")) {
                    assertTrue(showsPerSource(ref, result, field),
                        "C230: P4 $field must list the real source value.")
                }
                assertTrue(ref.contains("INT"),
                    "Aptitude must remain the genuinely common built-in ability.")
                assertTrue(pages.last().contains("NOTAS"))
            }
        }
    }

    @Test
    fun g3P4UniformPersistedCastingAbilitiesCannotClaimGlobalIntAB() {
        // Genuine Ilyra backup: leave spell UUIDs, preparation, global CD/attack
        // intact and alter only the explicit per-source casting aptitudes.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val sheet = original.permanent.sheet
        assertEquals(
            io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility.INTELLIGENCE,
            sheet.spellcastingAbility,
        )
        // Real Ilyra INT 18 (+4), WIS 13 (+1). Offset by +3 in both
        // profile adjustments so C224 isolates APTITUD, not CD/attack.
        assertEquals(18, sheet.intelligence)
        assertEquals(13, sheet.wisdom)
        val successor = original.permanent.successor
        assertTrue(successor.spellcastingProfiles.isNotEmpty())
        val changed = original.copy(permanent = original.permanent.copy(
            successor = successor.copy(
                spellcastingProfiles = successor.spellcastingProfiles.map {
                    it.copy(
                        ability = io.github.mrsimkin.dndcustomaid.shared.character
                            .CharacterAbilityReference.builtIn(
                                io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility.WISDOM),
                        saveDcAdjustment = it.saveDcAdjustment + 3,
                        spellAttackAdjustment = it.spellAttackAdjustment + 3,
                    )
                },
            ),
        ))
        val labels = io.github.mrsimkin.dndcustomaid.shared.character
            .PcSheetV11SpellSourceLabels
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val result = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                changed,
            )
            assertTrue(labels.conflictingReferenceFields(result.semantic.spells).isEmpty(),
                "All associated genuine source profiles should be uniformly WIS.")
            assertEquals(setOf("APTITUD"), labels.globalAbilityDisagreement(
                result.semantic.spells, sheet.spellcastingAbility,
            ))
            val associated = result.semantic.spells.flatMap { it.sources }
                .distinctBy { it.sourceId }
            assertTrue(associated.isNotEmpty())
            assertTrue(associated.all {
                it.castingAbility.builtIn ==
                    io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility.WISDOM
            }, "Associated sources must retain their genuine UUID and WIS aptitude.")
            preserveRealApplicationPdf(result.bytes,
                "V11-INTERNAL-PROOF-P4-uniform-source-aptitude-" + variant.name + ".pdf")
            Loader.loadPDF(result.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply { startPage = page; endPage = page }
                        .getText(pdf)
                }
                val p4 = pages.filter { it.contains("P4 - Conjuros") }
                assertTrue(p4.isNotEmpty())
                val p1 = pages.first()
                // C256: the contradictory aptitude lists each real source on P1.
                assertEquals(0, Regex("SEGÚN FUENTE").findAll(p1).count(),
                    "P1 must list real sources when they fit its golden card.")
                assertTrue(showsPerSource(p1, result, "APTITUD"),
                    "P1 must guard contradictory aptitude without masking DC/attack.")
                assertTrue(p1.contains("15"))
                assertTrue(p1.contains("+7"))
                val reference = p4.first().substringAfter("REFERENCIA MÁGICA")
                    .substringBefore("NIVEL")
                assertEquals(0, Regex("SEGÚN FUENTE").findAll(reference).count(),
                    "C230: the false global APTITUD card lists the real source aptitude.")
                assertTrue(showsPerSource(reference, result, "APTITUD"))
                assertFalse(reference.contains("\nINT\n"))
                assertTrue(reference.contains("CD SALVACIÓN"))
                assertTrue(reference.contains("ATAQUE MÁGICO"))
                assertTrue(p4.joinToString("\n").contains("Mago") &&
                    !p4.joinToString("\n").contains("[X]"),
                    "Source identity cannot disappear (C258: no printed mark).")
                assertTrue(pages.last().contains("NOTAS"))
            }
        }
    }

    @Test
    fun g3P4UniformRealSourceProfilesDoNotPrintContradictoryOverallNumbersAB() {
        // Real Ilyra backup: leave global 15/+7 and every spell/UUID untouched.
        // Adjust ALL persisted source profiles equally, so C221's per-source
        // disagreement set remains empty, but source-level values are explicit.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val sheet = original.permanent.sheet
        assertEquals(15, sheet.spellSaveDc)
        assertEquals(7, sheet.spellAttackModifier)
        val successor = original.permanent.successor
        assertTrue(successor.spellcastingProfiles.isNotEmpty())
        val adjusted = original.copy(permanent = original.permanent.copy(
            successor = successor.copy(
                spellcastingProfiles = successor.spellcastingProfiles.map {
                    it.copy(
                        saveDcAdjustment = it.saveDcAdjustment + 2,
                        spellAttackAdjustment = it.spellAttackAdjustment - 2,
                    )
                },
            ),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val result = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                adjusted,
            )
            val labels = io.github.mrsimkin.dndcustomaid.shared.character
                .PcSheetV11SpellSourceLabels
            assertTrue(labels.conflictingReferenceFields(result.semantic.spells).isEmpty(),
                "Every real associated source received the same explicit adjustment.")
            assertEquals(setOf("CD SALVACIÓN", "ATAQUE MÁGICO"),
                labels.globalReferenceDisagreements(
                    result.semantic.spells, sheet.spellSaveDc, sheet.spellAttackModifier,
                ))
            val associated = result.semantic.spells.flatMap { it.sources }
                .distinctBy { it.sourceId }
            assertTrue(associated.isNotEmpty())
            assertTrue(associated.all { it.saveDc == 17 && it.spellAttackModifier == 5 },
                "The displayed guard must be backed by real changed source-profile math.")
            preserveRealApplicationPdf(result.bytes,
                "V11-INTERNAL-PROOF-P4-uniform-profile-global-disagreement-" +
                    variant.name + ".pdf")
            Loader.loadPDF(result.bytes).use { pdf ->
                val p4 = (1..pdf.numberOfPages).map { index ->
                    PDFTextStripper().apply {
                        startPage = index
                        endPage = index
                    }.getText(pdf)
                }.filter { it.contains("P4 - Conjuros") }
                assertTrue(p4.isNotEmpty())
                val p1 = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                // C256 (owner 2026-10-28): the guarded DC and attack now list each
                // real source on P1 (smaller line or two lines) instead of the cue.
                assertEquals(0, Regex("SEGÚN FUENTE").findAll(p1).count(),
                    "P1 must list real sources when they fit its golden card.")
                assertTrue(showsPerSource(p1, result, "CD SALVACIÓN") &&
                    showsPerSource(p1, result, "ATAQUE MÁGICO"),
                    "P1 must guard DC and attack independently of true INT aptitude.")
                assertTrue(p1.contains("INT"))
                val reference = p4.first().substringAfter("REFERENCIA MÁGICA")
                    .substringBefore("NIVEL")
                assertEquals(0, Regex("SEGÚN FUENTE").findAll(reference).count(),
                    "C230: the two inconsistent overall P4 cards list real source values.")
                for (field in listOf("CD SALVACIÓN", "ATAQUE MÁGICO")) {
                    assertTrue(showsPerSource(reference, result, field))
                }
                assertTrue(reference.contains("INT"),
                    "The unaffected casting-ability card must be preserved.")
                assertFalse(reference.contains("\n15\n"))
                assertFalse(reference.contains("\n+7\n"))
                assertTrue(p4.joinToString("\n").contains("Mago") &&
                    !p4.joinToString("\n").contains("[X]"),
                    "A truthful reference keeps the source row (C258: no printed mark).")
                assertTrue(PDFTextStripper().getText(pdf).contains("NOTAS"))
            }
        }
    }

    @Test
    fun g3P4RealSpellSourceWithCrLfAndTabKeepsAllOriginAndPreparationAB() {
        // Independent regression: a persisted spellcasting source is free-form
        // and may contain actual line breaks/tabs. Old P4 inline-right sizing
        // measured CR/LF with PDFBox and could abort export before writing PDF.
        // Only ONE spell refers to this fresh source ID: all tokens must
        // survive EXACTLY ONCE and no unrelated casting source is edited.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val sheet = original.permanent.sheet
        val spell = sheet.spells.first { it.sourceAssociations.isNotEmpty() }
        val source = sheet.spellcastingSources.first {
            it.id == spell.sourceAssociations.first().sourceId
        }
        val newId = Uuid.parse("abcdef13-0000-4000-8000-000000000173")
        val literalName = "QAP4SOURCEFIRST\r\nQAP4SOURCESECOND\tQAP4SOURCETHIRD"
        val stressed = original.copy(permanent = original.permanent.copy(
            sheet = sheet.copy(
                spellcastingSources = sheet.spellcastingSources +
                    source.copy(id = newId, name = literalName,
                        sortOrder = source.sortOrder + 1),
                spells = sheet.spells.map {
                    if (it.id == spell.id) it.copy(
                        sourceAssociations = listOf(
                            it.sourceAssociations.first().copy(
                                sourceId = newId, prepared = true,
                            ),
                        ),
                    ) else it
                },
            ),
        ))
        assertEquals(literalName, stressed.permanent.sheet.spellcastingSources
            .first { it.id == newId }.name)
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val real = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = false,
                ), stressed,
            )
            val matching = real.semantic.spells.first { it.spell.id == spell.id }
            assertEquals(1, matching.sources.size)
            assertEquals(newId, matching.sources.single().sourceId)
            assertEquals(true, matching.sources.single().prepared)
            preserveRealApplicationPdf(real.bytes,
                "V11-INTERNAL-PROOF-P4-source-physical-break-" +
                    variant.name + ".pdf")
            Loader.loadPDF(real.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { index ->
                    PDFTextStripper().apply {
                        startPage = index; endPage = index
                    }.getText(pdf)
                }
                val p4 = pages.filter { it.contains("P4 - Conjuros") }
                    .joinToString("\n")
                for (token in listOf(
                    "QAP4SOURCEFIRST", "QAP4SOURCESECOND", "QAP4SOURCETHIRD",
                )) {
                    assertEquals(1, Regex(token).findAll(p4).count(),
                        "Real CR/LF/TAB source token must survive once in P4 $variant: $token")
                }
                assertTrue(p4.contains(spell.name),
                    "The original UUID-backed spell cannot be dropped.")
                assertFalse(p4.contains("[X]") || p4.contains("[ ] "),
                    "C258: preparation is never printed as a mark.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" },
                    "Physical final handwritten page must remain last.")
            }
        }
    }


    @Test
    fun g3P4SpellNameWithPhysicalBreaksKeepsTheRealSpellAndOptionalBookAB() {
        // Real Ilyra backup and its actual UUID/source associations. A legal
        // persisted spell name contains CRLF and TAB. Previously P4's
        // right-lane selection measured this complete name with PDFBox even
        // before falling back to multiline flow. No other spell is altered.
        val source = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val originalSpells = source.permanent.sheet.spells
        val target = originalSpells.first { it.sourceAssociations.isNotEmpty() }
        val multilineName = "QAP4SPELLFIRST\r\nQAP4SPELLSECOND\tQAP4SPELLTHIRD"
        val stressed = source.copy(permanent = source.permanent.copy(
            sheet = source.permanent.sheet.copy(spells = originalSpells.map {
                if (it.id == target.id) it.copy(name = multilineName) else it
            }),
        ))
        assertEquals(multilineName, stressed.permanent.sheet.spells
            .first { it.id == target.id }.name)
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val real = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = true,
                ), stressed,
            )
            val semantic = real.semantic.spells.first { it.spell.id == target.id }
            assertEquals(multilineName, semantic.spell.name)
            assertEquals(target.sourceAssociations.map { it.sourceId },
                semantic.sources.map { it.sourceId })
            assertEquals(target.sourceAssociations.map { it.prepared },
                semantic.sources.map { it.prepared })
            assertEquals(originalSpells, source.permanent.sheet.spells,
                "Export must not mutate the original source backup.")
            preserveRealApplicationPdf(real.bytes,
                "V11-INTERNAL-PROOF-P4-spell-name-break-" + variant.name + ".pdf")
            Loader.loadPDF(real.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply {
                        startPage = page
                        endPage = page
                    }.getText(pdf)
                }
                val p4 = pages.filter { it.contains("P4 - Conjuros") }
                    .joinToString("\n")
                val tokens = listOf("QAP4SPELLFIRST", "QAP4SPELLSECOND",
                    "QAP4SPELLTHIRD")
                tokens.forEach { token ->
                    assertEquals(1, Regex(token).findAll(p4).count(),
                        "P4 must print every real spell-name fragment exactly " +
                            "once in variant $variant: $token")
                }
                assertTrue(p4.contains("P4 - Conjuros"))
                // Optional annex must still follow P4 and the real spell ID:
                // the book INDEX and CARD use source text, not a fabricated
                // single-line replacement just to make the PDF export work.
                val indexPage = pages.indexOfFirst {
                    it.contains("Índice del Libro de Conjuros")
                }
                val bookPage = pages.indexOfFirst { it.contains("Libro de conjuros") &&
                    !it.contains("Índice del Libro de Conjuros") }
                assertTrue(indexPage >= 0 && bookPage > indexPage)
                val book = pages.drop(bookPage).dropLast(1).joinToString("\n")
                tokens.forEach { token ->
                    assertTrue(book.contains(token),
                        "Spellbook must retain authentic multiline name token $token")
                }
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" },
                    "NOTAS remains the last physical page, not a book continuation.")
            }
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
            val firstP4 = (1..pdf.numberOfPages).firstOrNull { physical ->
                PDFTextStripper().apply {
                    startPage = physical
                    endPage = physical
                }.getText(pdf).contains("P4 - Conjuros")
            } ?: error("Real Ilyra export must retain printed P4.")
            val p4 = pdf.getPage(firstP4 - 1)
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
            assertTrue(region.getTextForRegion("real-right-origins").contains("Mago"),
                "A short real casting source must be printed in the golden right lane.")
            val p4Text = PDFTextStripper().apply {
                startPage = firstP4; endPage = firstP4
            }.getText(pdf)
            assertTrue(p4Text.contains("Trasfondo") && p4Text.contains("Raza"),
                "C231: non-class origins are labelled by type, not by long names.")
            assertFalse(p4Text.contains("Magic Initiate") ||
                p4Text.contains("Elven Lineage"),
                "C231: specific origin names are not printed on P4.")
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
            // 10 real level groups had 8 / 6 handwritten rows each.
            // Golden P4's previously unused third-column tail now fits
            // 7 additional Ilyra / 8 additional Mara BLANK vector rows.
            // Keep the physical square count exact; do not weaken this
            // assertion or conflate empty writing with a digital spell.
            Triple("02_ilyra_quill_srd5_2_1_evoker_wizard.json",
                PcSheetV11Variant.B, 68),
            // C258: Ilyra's two-source spells take two rows (91 -> 89 blank rows);
            // Mara's source label now fits beside each spell without the [X]
            // mark, so her rows are single-line and leave 89 blank rows (was 68).
            // C261: the golden P4 rhythm (6.8 pt on 15.7 pt rows) leaves 62 / 65.
            // C322: golden P4 columns (y630.43..64.92, 15.73 pt rows) leave 68 / 71.
            Triple("03_mara_siete_umbrales_custom_extended.json",
                PcSheetV11Variant.B, 71),
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
                    // C317: golden 8.22 pt spell boxes plus the 7 pt level-meter squares.
                    marks += Regex("""(?m)(?:^|\s)(?:7 7|8\.22\d* 8\.22\d*) re(?:\s|$)""")
                        .findAll(stream).count()
                }
                assertTrue(paperRows >= 1, "Actual spell P4 page must be present.")
                // Preparation marks remain one per real spell/manuscript
                // row. The approved handwritten spend tracker contributes
                // exactly three DISTINCT blank squares for levels 1..9.
                // C258: one paper box per spell-source row.
                val preparationAndPaper = expectedBlankRows +
                    generated.semantic.spells.sumOf { spell ->
                        maxOf(1, spell.sources.distinctBy { it.sourceId }.size)
                    }
                val blankLevelMeters = 9 * 3
                assertEquals(preparationAndPaper + blankLevelMeters, marks,
                    "One golden 8.22 pt preparation square per source/blank row AND " +
                        "exactly 27 distinct unfilled level-meter squares.")
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
            val firstP4 = (1..pdf.numberOfPages).firstOrNull { physical ->
                PDFTextStripper().apply {
                    startPage = physical
                    endPage = physical
                }.getText(pdf).contains("P4 - Conjuros")
            } ?: error("Real caster must retain an actual printed P4.")
            val p4 = PDFTextStripper().apply {
                startPage = firstP4
                endPage = firstP4
            }.getText(pdf)
            assertTrue(p4.contains("TRUCOS 0"))
            assertTrue(p4.contains("NIVEL 9"))
            assertFalse(p4.contains("___"),
                "No fake underscore glyphs: all P4 empty writing uses real vector rules.")
            assertTrue(p4.contains("Mago") && !p4.contains("[X]"),
                "Source-backed spell rows keep their source (C258: no printed mark).")
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
    fun g3P1AmmunitionSourceNameWithLineBreakOrTabDefersToC2WithoutLosingQuantityAB() {
        val source = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val usage = source.permanent.closure.inventoryUsage.single {
            it.kind == io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind.AMMUNITION
        }
        val template = source.permanent.sheet.inventoryItems.first { it.id == usage.itemId }
        val names = listOf(
            "QAAMMOLINESTART\nQAAMMOLINEEND",
            "QAAMMOTABSTART\tQAAMMOTABEND",
            "QASHORTAMMO",
        )
        val quantities = listOf(7, 9, 4)
        val ids = (1..3).map { number ->
            Uuid.parse("a9000000-0000-4000-8000-" +
                number.toString().padStart(12, '0'))
        }
        val customItems = names.mapIndexed { index, name ->
            template.copy(
                id = ids[index],
                name = name,
                quantity = quantities[index],
                sortOrder = index + 1,
            )
        }
        val stressed = source.copy(permanent = source.permanent.copy(
            sheet = source.permanent.sheet.copy(
                inventoryItems = source.permanent.sheet.inventoryItems
                    .filterNot { it.id == usage.itemId } + customItems,
            ),
            closure = source.permanent.closure.copy(
                inventoryUsage = source.permanent.closure.inventoryUsage
                    .filterNot { it.itemId == usage.itemId } +
                    ids.map { id -> usage.copy(itemId = id) },
            ),
        ))
        val hardNames = listOf("QAAMMOLINESTART", "QAAMMOLINEEND",
            "QAAMMOTABSTART", "QAAMMOTABEND")
        val sourceItems = stressed.permanent.sheet.inventoryItems.toList()
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = false,
                ), stressed,
            )
            assertEquals(sourceItems, stressed.permanent.sheet.inventoryItems,
                "Paper export must not mutate original inventory entries.")
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-P1-ammo-source-break-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply {
                        startPage = page
                        endPage = page
                    }.getText(pdf)
                }
                val p1 = pages.first()
                val c2 = pages.filter { it.contains("C2 - Habilidades, acciones y recursos") }
                    .joinToString("\n")
                hardNames.forEach { marker ->
                    assertFalse(p1.contains(marker),
                        "One-line P1 ammunition cannot contain " + marker)
                    assertTrue(c2.contains(marker),
                        "Persisted source item must survive as C2 ammunition: " + marker)
                }
                assertTrue(c2.contains("CANTIDAD 7"))
                assertTrue(c2.contains("CANTIDAD 9"))
                assertTrue(p1.contains("QASHORTAMMO"),
                    "Eligible ammo should still use a real P1 row.")
                assertTrue(c2.contains("MUNICIÓN QASHORTAMMO"),
                    "C2 retains the real source summary; this is not a duplicate P1 marks row.")
                assertFalse(c2.contains("MARCAS: ____ / 4"),
                    "A P1-tracked item must not get a second marks track on C2.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                hardNames.forEach { marker ->
                    assertFalse(pages.last().contains(marker),
                        "Final manuscript page may not absorb digital inventory.")
                }
            }
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
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(
                variant,
                PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = true,
            ),
            adjusted,
        )
        preserveRealApplicationPdf(proof.bytes,
            "V11-INTERNAL-PROOF-BK05-long-card-" + variant.name + ".pdf")
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
            val realBookPages = sections.filter {
                it.contains("Libro de conjuros /") &&
                    !it.contains("Índice del Libro de Conjuros")
            }
            assertTrue(realBookPages.joinToString("\n").contains("CONTINUACIÓN: Fire Bolt"),
                "An oversized source-backed card must identify the real spell in its next column.")
            val continuationMarks = realBookPages.sumOf { pageText ->
                Regex("CONTINUACIÓN: Fire Bolt").findAll(pageText).count()
            }
            assertTrue(continuationMarks >= 2,
                "A multi-page card must label more than one physical continuation.")
            assertTrue(realBookPages.first().contains("Fire Bolt"),
                "The real source spell name must still appear at its first occurrence.")
            assertTrue(sections.last().lineSequence().any { it.trim() == "NOTAS" },
                "Handwritten notes must remain the physical last page.")
        }
        }
    }


    @Test
    fun g3Bk04MediumRealSpellCardMovesWholeToNextPhysicalBookColumn() {
        // D-0076 §3.48: if a complete medium card cannot fit below the
        // previous card but DOES fit an empty column, move it intact. Actual
        // embedded PDFBox Fira 9pt metrics choose the test line count.
        val backup = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val request = PcSheetV11ExportRequest(
                variant, PcSheetExportStateSelection.PERMANENT,
                includeSpellDescriptions = true,
            )
            val originalSemantic = io.github.mrsimkin.dndcustomaid.shared.character
                .PcSheetV11SemanticMapper.map(request, backup)
            val firstTwo = originalSemantic.spells.take(2)
            assertEquals(2, firstTwo.size)
            val firstId = firstTwo[0].spell.id
            val secondId = firstTwo[1].spell.id
            val contentHeight = 626f
            // C260: the book uses the golden Libro rhythm (Fira 6.7 pt on 10.5 pt rows).
            val font = (contentHeight / 10.5f).toInt()
            assertTrue(font > 22, "A real Letter book column must have usable rows.")
            // C240: the golden card puts NIVEL on the name line and drops the
            // DESCRIPCIÓN label, so the first card needs two more real lines to
            // leave a remainder (8 lines) too small for the 9-line second card.
            val firstDescription = (1..(font - 9)).joinToString("\n") { n ->
                "BK04_PREVIA_" + n.toString().padStart(3, '0')
            }
            val secondDescription = (1..8).joinToString("\n") { n ->
                "BK04_SEGUNDA_" + n.toString().padStart(3, '0')
            }
            val editedSpells = backup.permanent.sheet.spells
                .filter { it.id == firstId || it.id == secondId }
                .map { spell ->
                    when (spell.id) {
                        firstId -> spell.copy(description = firstDescription)
                        secondId -> spell.copy(description = secondDescription)
                        else -> spell
                    }
                }
            assertEquals(2, editedSpells.size)
            val edited = backup.copy(permanent = backup.permanent.copy(
                sheet = backup.permanent.sheet.copy(spells = editedSpells),
            ))
            val semantic = io.github.mrsimkin.dndcustomaid.shared.character
                .PcSheetV11SemanticMapper.map(request, edited)
            assertEquals(listOf(firstId, secondId),
                semantic.spells.map { it.spell.id })
            // Assert actual embedded-font pagination, not synthetic characters.
            org.apache.pdfbox.pdmodel.PDDocument().use { temporary ->
                val body = DesktopPcSheetV11FontMeasurer(temporary).resolve(
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Typeface.BODY,
                    6.7f, // C260: golden Libro rhythm 6.7 / 10.5 pt.
                )
                val cards = semantic.spells.map { entry ->
                    val spell = entry.spell
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowItem(
                        "book:" + spell.id,
                        spell.name + "\n" + spell.description,
                        keepTogetherIfFits = true,
                    )
                }
                val measured = io.github.mrsimkin.dndcustomaid.shared.character
                    .PcSheetV11MeasuredFlowPlacer.place(
                        io.github.mrsimkin.dndcustomaid.shared.character
                            .PcSheetV11FlowSection.SPELLBOOK,
                        cards,
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11RectPt(
                            42f, 76f, 528f, contentHeight,
                        ),
                        2, 12f, 10.5f, body.width,
                    )
                val before = measured.sourceFlow.fragments.filter {
                    it.sourceKey == "book:" + firstId
                }
                val second = measured.sourceFlow.fragments.filter {
                    it.sourceKey == "book:" + secondId
                }
                assertEquals(1, before.size)
                assertEquals(1, before.single().columnIndex)
                assertEquals(1, second.size, "BK04 medium card must remain whole.")
                assertEquals(2, second.single().columnIndex)
                assertEquals(1, second.single().localPageIndex)
                assertEquals(0, second.single().startLineIndexInColumn)
                assertFalse(second.single().continuedFromPrevious)
                assertFalse(second.single().continuesOnNext)
                assertTrue(before.single().lineRanges.size < measured.lineCapacity)
                measured.sourceFlow.verifyLosslessAgainst(cards)
                measured.verifyGeometry()
            }
            val proof = DesktopPcSheetExportService().generateV11Proof(request, edited)
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-BK04-whole-next-column-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                assertAllPhysicalBookReferences(pdf, semantic.spells.map { it.spell.name })
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply {
                        startPage = n
                        endPage = n
                    }.getText(pdf)
                }
                val actualBookPage = pages.indexOfFirst {
                    it.contains("Libro de conjuros /") &&
                        !it.contains("Índice del Libro de Conjuros")
                }
                assertTrue(actualBookPage >= 0)
                assertEquals(1, pages.count {
                    it.contains("Libro de conjuros /") &&
                        !it.contains("Índice del Libro de Conjuros")
                }, "Two medium source-backed cards should fit on one 2-column page.")
                val byArea = PDFTextStripperByArea()
                byArea.addRegion("left", Rectangle2D.Float(42f, 88f, 258f, 630f))
                byArea.addRegion("right", Rectangle2D.Float(312f, 88f, 258f, 630f))
                byArea.extractRegions(pdf.getPage(actualBookPage))
                val left = byArea.getTextForRegion("left")
                val right = byArea.getTextForRegion("right")
                assertTrue(left.contains(firstTwo[0].spell.name))
                assertFalse(left.contains(firstTwo[1].spell.name),
                    "BK04 forbids placing ANY of the second card in old column.")
                assertTrue(right.contains(firstTwo[1].spell.name))
                assertTrue(right.contains("BK04_SEGUNDA_001"))
                assertTrue(right.contains("BK04_SEGUNDA_008"))
                assertFalse(right.contains("CONTINUACIÓN:"),
                    "A moved whole card must NOT be falsely marked as a split continuation.")
                assertFalse(left.contains("BK04_SEGUNDA_"))
                for (index in 1..8) {
                    val marker = "BK04_SEGUNDA_" + index.toString().padStart(3, '0')
                    assertEquals(1, Regex(Regex.escape(marker))
                        .findAll(pages[actualBookPage]).count(),
                        "Source row lost or duplicated in physical A/B export.")
                }
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
            }
        }
    }

    @Test
    fun g3Bk06EssentialSpellbookCardsNeverPrintUnapprovedTechnicalMetadata() {
        // D-0076 §3.47 chose essential cards (name, level, real school if
        // available, full description), expressly rejecting technical fields.
        // The school is printed only from CharacterSpell.school (Q14b).
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val marker = "BK06_DESCRIPCION_REAL_INTEGRA_V11"
        val modified = original.copy(
            permanent = original.permanent.copy(
                sheet = original.permanent.sheet.copy(
                    spells = original.permanent.sheet.spells.mapIndexed { index, spell ->
                        if (index != 0) spell else spell.copy(
                            castingTime = "BK06_TIEMPO_NO_IMPRIMIR",
                            rangeText = "BK06_ALCANCE_NO_IMPRIMIR",
                            materialText = "BK06_MATERIAL_NO_IMPRIMIR",
                            duration = "BK06_DURACION_NO_IMPRIMIR",
                            notes = "BK06_NOTAS_NO_IMPRIMIR",
                            description = marker + " \n" +
                                ("Párrafo de la descripción del conjuro sin cortes. ").repeat(16) +
                                "\n BK06_DESCRIPCION_FIN",
                        )
                    },
                ),
            ),
        )
        val forbidden = listOf(
            "BK06_TIEMPO_NO_IMPRIMIR", "BK06_ALCANCE_NO_IMPRIMIR",
            "BK06_MATERIAL_NO_IMPRIMIR", "BK06_DURACION_NO_IMPRIMIR",
            "BK06_NOTAS_NO_IMPRIMIR",
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = true),
                modified,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-BK06-essential-spellbook-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { physical ->
                    PDFTextStripper().apply {
                        startPage = physical
                        endPage = physical
                    }.getText(pdf)
                }
                val book = pages.filter {
                    it.contains("Libro de conjuros /") &&
                        !it.contains("Índice del Libro de Conjuros")
                }.joinToString("\n")
                // C240: the golden card has no DESCRIPCIÓN label; NIVEL shares the
                // name line, right-aligned (checked by position below).
                assertFalse(book.contains("DESCRIPCIÓN:"), "Golden book cards have no label line.")
                assertTrue(book.contains(marker))
                val levels = mutableListOf<Pair<Float, Float>>() // (x, baseline) of NIVEL labels
                val names = mutableMapOf<String, Float>() // first word baseline of each card
                val firstBookPage = pages.indexOfFirst {
                    it.contains("Libro de conjuros /") && !it.contains("Índice del Libro de Conjuros")
                } + 1
                object : PDFTextStripper() {
                    override fun writeString(
                        text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                    ) {
                        val first = textPositions.firstOrNull() ?: return
                        val y = 792f - first.yDirAdj
                        if (text.trim().startsWith("NIVEL ")) levels += first.xDirAdj to y
                        if (text.trim() == "Fire Bolt") names["Fire Bolt"] = y
                    }
                }.apply { startPage = firstBookPage; endPage = firstBookPage }.getText(pdf)
                val fireBolt = requireNotNull(names["Fire Bolt"]) { "Fire Bolt card name line ($variant)." }
                assertTrue(levels.any { (x, y) -> kotlin.math.abs(y - fireBolt) < 0.1f && x > 200f },
                    "NIVEL is right-aligned on the Fire Bolt name line ($variant): $levels")
                assertTrue(book.contains("BK06_DESCRIPCION_FIN"),
                    "The whole real spell description must survive the book reflow.")
                assertTrue(book.contains("Fire Bolt"), "Each card still shows its real name.")
                assertTrue(book.contains("NIVEL 0"), "Level is still source-backed.")
                forbidden.forEach { sentinel ->
                    assertFalse(book.contains(sentinel),
                        "The optional essential card may not include: " + sentinel)
                }
                assertFalse(book.contains("FUENTE Mago"),
                    "Spellcasting associations belong to the separate quick list.")
                assertFalse(book.contains("COMPONENTES "),
                    "The owner rejected the full technical card.")
                assertFalse(book.contains("LANZAMIENTO "),
                    "The owner rejected the casting-time row in spellbook cards.")
                assertFalse(book.contains("DURACIÓN "),
                    "The owner rejected the duration row in spellbook cards.")
                assertFalse(book.contains("ESCUELA "),
                    "The golden school line has no ESCUELA label.")
                val p4 = pages.filter { it.contains("P4 - Conjuros /") }
                    .joinToString("\n")
                assertTrue(p4.contains("Fire Bolt"),
                    "Removing extra book metadata must not change the quick list.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
            }
        }
    }

    @Test
    fun g3Bk06LibroCardPrintsTheRealSchoolUnderTheNameOnlyWhenTheAppHasOneAB() {
        // Q14b (owner, 2026-10-29): the golden book card shows the school on its
        // own line under the name, uppercase, 7 pt condensed in label grey. Only
        // a school stored in the App is printed; no school, no line.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val withSchool = original.copy(
            permanent = original.permanent.copy(
                sheet = original.permanent.sheet.copy(
                    spells = original.permanent.sheet.spells.map { spell ->
                        if (spell.name == "Fire Bolt") spell.copy(school = " Evocación ")
                        else spell.copy(school = null)
                    },
                ),
            ),
        )
        val schools = listOf("ABJURACIÓN", "ADIVINACIÓN", "CONJURACIÓN", "ENCANTAMIENTO",
            "EVOCACIÓN", "ILUSIÓN", "NIGROMANCIA", "TRANSMUTACIÓN")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            for ((label, source) in listOf("with-school" to withSchool, "without-school" to original)) {
                val proof = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(
                        variant, PcSheetExportStateSelection.PERMANENT,
                        includeSpellDescriptions = true),
                    source,
                )
                preserveRealApplicationPdf(proof.bytes,
                    "V11-INTERNAL-PROOF-BK06-school-" + label + "-" + variant.name + ".pdf")
                Loader.loadPDF(proof.bytes).use { pdf ->
                    val pages = (1..pdf.numberOfPages).map { physical ->
                        PDFTextStripper().apply {
                            startPage = physical
                            endPage = physical
                        }.getText(pdf)
                    }
                    val bookPages = pages.indices.filter {
                        pages[it].contains("Libro de conjuros /") &&
                            !pages[it].contains("Índice del Libro de Conjuros")
                    }
                    assertTrue(bookPages.isNotEmpty(), "Ilyra prints the Libro ($variant, $label).")
                    val book = bookPages.joinToString("\n") { pages[it] }
                    if (source === original) {
                        schools.forEach { school ->
                            assertFalse(book.contains(school),
                                "No App school, no school line ($variant): $school")
                        }
                        return@use
                    }
                    schools.filter { it != "EVOCACIÓN" }.forEach { school ->
                        assertFalse(book.contains(school), "Only real schools print ($variant): $school")
                    }
                    assertEquals(1, Regex("EVOCACIÓN").findAll(book).count(),
                        "Fire Bolt's real school prints once, uppercase ($variant).")
                    var name: Pair<Float, Float>? = null
                    var school: Triple<Float, Float, org.apache.pdfbox.text.TextPosition>? = null
                    val bookPage = bookPages.first { pages[it].contains("Fire Bolt") } + 1
                    object : PDFTextStripper() {
                        override fun writeString(
                            text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                        ) {
                            val first = textPositions.firstOrNull() ?: return
                            val y = 792f - first.yDirAdj
                            if (text.trim() == "Fire Bolt") name = first.xDirAdj to y
                            if (text.trim() == "EVOCACIÓN") school = Triple(first.xDirAdj, y, first)
                        }
                    }.apply { startPage = bookPage; endPage = bookPage }.getText(pdf)
                    val (nameX, nameY) = requireNotNull(name) { "Fire Bolt name line ($variant)." }
                    val (schoolX, schoolY, glyph) = requireNotNull(school) { "School line ($variant)." }
                    assertTrue(kotlin.math.abs(schoolX - nameX) < 0.5f,
                        "The school starts under the name ($variant): $schoolX vs $nameX")
                    assertTrue(kotlin.math.abs(nameY - schoolY - 12.75f) < 0.3f,
                        "Golden school baseline 12.75 pt under the name (C288, $variant): $nameY / $schoolY")
                    assertTrue(kotlin.math.abs(glyph.fontSizeInPt - 7f) < 0.05f,
                        "Golden school size 7 pt ($variant): " + glyph.fontSizeInPt)
                    assertTrue(glyph.font.name.contains("NotoSansDisplay"),
                        "Golden condensed bold face ($variant): " + glyph.font.name)
                }
            }
        }
    }

    @Test
    fun g3HeaderNameUsesGoldenGaramondAndNeverDropsAccentedLettersAB() {
        // C285 (owner 2026-10-29 «se agregan las fuentes»): the golden header name is
        // EB Garamond 12 Bold 13.4 pt in ink #1d384f. That font has no accented
        // letters (the golden lost the «á» of «mágico»); those come from the later
        // EB Garamond Bold, so the whole real name is printed.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val name = "Ñandú Élmira — Óscar"
        val renamed = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(name = name)))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            for ((label, source, expected) in listOf(
                Triple("accented", renamed, name), Triple("plain", original, original.permanent.sheet.name))) {
                val proof = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source)
                preserveRealApplicationPdf(proof.bytes,
                    "V11-INTERNAL-PROOF-C285-header-name-" + label + "-" + variant.name + ".pdf")
                Loader.loadPDF(proof.bytes).use { pdf ->
                    val runs = mutableListOf<org.apache.pdfbox.text.TextPosition>()
                    object : PDFTextStripper() {
                        override fun writeString(
                            text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                        ) {
                            runs += textPositions.filter { it.yDirAdj in 50f..62f && it.font.name.contains("Garamond") }
                        }
                    }.apply { startPage = 2; endPage = 2 }.getText(pdf)
                    val printed = runs.sortedBy { it.xDirAdj }.joinToString("") { it.unicode }
                    assertEquals(expected.replace(" ", ""), printed.replace(" ", ""),
                        "The whole real name prints in the header ($variant, $label).")
                    assertTrue(runs.all { kotlin.math.abs(it.fontSize - 13.4f) < 0.05f },
                        "Golden 13.4 pt name ($variant, $label): " + runs.map { it.fontSize }.distinct())
                    val faces = runs.map { it.font.name.substringAfter('+') }.toSet()
                    assertTrue("EBGaramond12-Bold" in faces, "Golden Garamond face ($variant): $faces")
                    if (label == "plain") assertEquals(setOf("EBGaramond12-Bold"), faces,
                        "A name the golden face can draw uses only it ($variant).")
                    else assertEquals(setOf("EBGaramond12-Bold", "EBGaramond-Bold"), faces,
                        "Only missing letters come from the fallback ($variant).")
                }
            }
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
    fun g3P2PairedAmountsAreRightAlignedLikeTheGoldenAB() {
        // C242: golden Equipo/Tesoro amounts end 3 pt inside their 36 pt quantity
        // cells (left pair 129..165, right pair 264..300 on P2).
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), original,
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val amounts = mutableListOf<Pair<Float, Float>>() // (x, end)
                object : PDFTextStripper() {
                    override fun writeString(
                        text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                    ) {
                        var start = 0
                        for (i in 1..textPositions.size) {
                            val split = i == textPositions.size ||
                                textPositions[i].unicode.isBlank() ||
                                textPositions[i].xDirAdj - (textPositions[i - 1].xDirAdj +
                                    textPositions[i - 1].widthDirAdj) > 2f
                            if (!split) continue
                            val part = textPositions.subList(start, i).filter { !it.unicode.isBlank() }
                            start = i
                            if (part.isEmpty()) continue
                            val word = part.joinToString("") { it.unicode }
                            val x = part.first().xDirAdj
                            val y = 792f - part.first().yDirAdj
                            if (word.all { it.isDigit() } && y in 318f..672f &&
                                (x in 125f..163f || x in 260f..298f)) {
                                amounts += x to part.last().xDirAdj + part.last().widthDirAdj
                            }
                        }
                    }
                }.apply { startPage = 2; endPage = 2 }.getText(pdf)
                assertTrue(amounts.size >= 8, "Mara P2 prints real amounts ($variant): $amounts")
                amounts.forEach { (x, end) ->
                    // C320: golden P2 table x39.69..298.49 (amount edges 3 pt inside).
                    val edge = if (x < 200f) 160.09f else 295.49f
                    assertEquals(edge, end, 0.6f, "P2 amount at $x is right-aligned ($variant).")
                }
            }
        }
    }

    @Test
    fun g3VariantBSkillValuesAreRightAlignedInTheirGoldenSubcolumns() {
        // C241: golden variant B HABILIDADES prints each value at the right of its
        // subcolumn band (bands 47..167 and 176..295; value ends 3 pt inside).
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B, PcSheetExportStateSelection.PERMANENT),
            original,
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val runs = mutableListOf<Triple<String, Float, Float>>() // text, x, end
            object : PDFTextStripper() {
                override fun writeString(
                    text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                ) {
                    var start = 0
                    for (i in 1..textPositions.size) {
                        val split = i == textPositions.size ||
                            textPositions[i].xDirAdj - (textPositions[i - 1].xDirAdj +
                                textPositions[i - 1].widthDirAdj) > 2f
                        if (!split) continue
                        val part = textPositions.subList(start, i)
                        val y = 792f - part.first().yDirAdj
                        if (y in 220f..316f && part.first().fontSizeInPt == 7f) {
                            runs += Triple(part.joinToString("") { it.unicode }.trim(),
                                part.first().xDirAdj, part.last().xDirAdj + part.last().widthDirAdj)
                        }
                        start = i
                    }
                }
            }.apply { startPage = 1; endPage = 1 }.getText(pdf)
            val values = runs.filter { Regex("^[+-]\\d+( [PE])?$").matches(it.first) }
            assertTrue(values.size >= 10, "Mara B prints real skill values on P1: $runs")
            values.forEach { (text, _, end) ->
                assertTrue(kotlin.math.abs(end - 164f) < 0.6f || kotlin.math.abs(end - 292f) < 0.6f,
                    "B skill value '$text' ends at the golden subcolumn edge, not at $end.")
            }
        }
    }

    @Test
    fun g3DigitalNotesUseGoldenNotasCartelaAndInsetCardsAB() {
        // C238: golden C-N3 has a NOTAS cartela ("solo notas digitales") and each
        // note in its own card; text starts 6 pt inside the 42 pt card edge.
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), original,
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).filter { number ->
                    PDFTextStripper().apply { startPage = number; endPage = number }
                        .getText(pdf).contains("C-N3 - Notas digitales")
                }
                assertTrue(pages.isNotEmpty(), "Mara has real digital notes ($variant).")
                pages.forEach { number ->
                    val starts = mutableListOf<Float>()
                    var text = ""
                    val stripper = object : PDFTextStripper() {
                        override fun writeString(
                            value: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                        ) {
                            text += value + "\n"
                            val first = textPositions.firstOrNull() ?: return
                            val y = 792f - first.yDirAdj
                            // C260: golden C-N3 body text is 7 pt.
                            if (y in 80f..700f && first.fontSizeInPt == 7f &&
                                first.font.name.contains("NotoSans-Regular")) starts += first.xDirAdj
                        }
                    }
                    stripper.startPage = number
                    stripper.endPage = number
                    stripper.getText(pdf)
                    assertTrue(text.contains("NOTAS") && text.contains("solo notas digitales"),
                        "C-N3 page $number keeps the golden NOTAS cartela ($variant).")
                    assertTrue(starts.isNotEmpty() && starts.all { kotlin.math.abs(it - 48f) < 0.3f },
                        "Note text starts 6 pt inside its card on page $number ($variant): $starts")
                }
            }
        }
    }

    @Test
    fun g3DeferredFittingAttacksUseGoldenC2AttackTableRowsAB() {
        // Golden C2 ATAQUES: attacks 7 and 8 fit the approved three-column row, so
        // they print as table rows (attack / bonus / damage) instead of a "|" record.
        val source = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val template = source.permanent.sheet.combatEntries.first {
            it.type == io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ATTACK
        }
        val attacks = (1..8).map { number ->
            template.copy(
                id = Uuid.parse("a7100000-0000-4000-8000-" + number.toString().padStart(12, '0')),
                name = "QAROW" + number,
                attackModifier = number,
                rangeText = null,
                damageEffect = "QADMG" + number,
                notes = if (number == 8) "QANOTA8" else null,
                sortOrder = number,
                pinned = false,
            )
        }
        val stressed = source.copy(permanent = source.permanent.copy(
            sheet = source.permanent.sheet.copy(combatEntries = attacks),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = false), stressed,
            ).bytes
            preserveRealApplicationPdf(bytes,
                "V11-INTERNAL-PROOF-C2-attack-table-rows-" + variant.name + ".pdf")
            Loader.loadPDF(bytes).use { pdf ->
                val pageTexts = (1..pdf.numberOfPages).map { number ->
                    PDFTextStripper().apply { startPage = number; endPage = number }.getText(pdf)
                }
                val c2Index = pageTexts.indexOfFirst {
                    it.contains("C2 - Habilidades, acciones y recursos")
                }
                assertTrue(c2Index > 0, "Attacks 7 and 8 must continue on C2 ($variant).")
                data class Word(val text: String, val x: Float, val y: Float, val end: Float)
                fun pageWords(page: Int): List<Word> {
                    val words = mutableListOf<Word>()
                    val stripper = object : PDFTextStripper() {
                        override fun writeString(
                            text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                        ) {
                            // Split on real spaces and on column gaps between separate glyph runs.
                            val word = StringBuilder()
                            var x = 0f
                            var y = 0f
                            var lastEnd = Float.NEGATIVE_INFINITY
                            fun flush() {
                                if (word.isNotEmpty()) words += Word(word.toString(), x, y, lastEnd)
                                word.clear()
                            }
                            for (position in textPositions) {
                                if (position.unicode.isBlank() || position.xDirAdj - lastEnd > 2f) flush()
                                if (!position.unicode.isBlank()) {
                                    if (word.isEmpty()) {
                                        x = position.xDirAdj
                                        y = 792f - position.yDirAdj
                                    }
                                    word.append(position.unicode)
                                    lastEnd = position.xDirAdj + position.widthDirAdj
                                }
                            }
                            flush()
                        }
                    }
                    stripper.startPage = page
                    stripper.endPage = page
                    stripper.getText(pdf)
                    return words
                }
                // C237: in both tables the label starts 4 pt inside the row, the bonus
                // ends 4 pt before the bonus column edge (121 + 36) and the damage
                // ends 4 pt before the row edge (258), as in the golden.
                fun assertGoldenRow(words: List<Word>, number: Int, where: String) {
                    val name = words.single { it.text == "QAROW$number" }
                    val bonus = words.single {
                        it.text == "+$number" && kotlin.math.abs(it.y - name.y) < 0.1f &&
                            it.x > name.x && it.x < name.x + 160f
                    }
                    val damage = words.single { it.text == "QADMG$number" }
                    val rowLeft = name.x - 4f
                    assertEquals(name.y, damage.y, 0.1f, "$where row $number stays on one line ($variant).")
                    assertEquals(rowLeft + 153f, bonus.end, 0.6f,
                        "$where bonus is right-aligned in the golden bonus column ($variant).")
                    assertEquals(rowLeft + 254f, damage.end, 0.6f,
                        "$where damage is right-aligned in the golden damage column ($variant).")
                }
                val p1Words = pageWords(1)
                (1..6).forEach { assertGoldenRow(p1Words, it, "P1") }
                val c2Words = pageWords(c2Index + 1)
                (7..8).forEach { assertGoldenRow(c2Words, it, "C2") }
                val all = pageTexts.joinToString("\n")
                assertFalse(Regex("(?<!DEL )ATAQUE QAROW").containsMatchIn(all) || all.contains("| DAÑO QADMG"),
                    "Fitting deferred attacks are not '|' records.")
                assertTrue(all.contains("NOTAS DEL ATAQUE QAROW8: QANOTA8"),
                    "A table row keeps its real notes on its own line ($variant).")
                (1..8).forEach { number ->
                    assertEquals(1, Regex("QAROW$number\\b").findAll(all).count() -
                        (if (number == 8) 1 else 0), "Each attack prints once: QAROW$number")
                }
            }
        }
    }

    @Test
    fun g3SourceOrderP1AttackSelectionUsesAllSixFittingRowsWithoutLosingDeferredAttacks() {
        val source = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val template = source.permanent.sheet.combatEntries.first {
            it.type == io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ATTACK
        }
        // Four oversized originals appear BEFORE six ordinary-sized entries.
        // The old take(6).filter(fits) printed just two rows on P1, leaving
        // four printable rows blank and needlessly deferring rows 7..10.
        val attacks = (1..10).map { number ->
            template.copy(
                id = Uuid.parse("a7000000-0000-4000-8000-" +
                    number.toString().padStart(12, '0')),
                name = if (number <= 4) {
                    "QALONG" + number +
                        " ATAQUE DE FASE SUPEREXTENDIDA IMPOSIBLE EN UNA CELDA"
                } else "QASHORT" + number,
                attackModifier = 5,
                rangeText = null,
                damageEffect = "1d6+2",
                notes = null,
                sortOrder = number,
                pinned = false,
            )
        }
        val stressed = source.copy(permanent = source.permanent.copy(
            sheet = source.permanent.sheet.copy(combatEntries = attacks),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = false,
                ), stressed,
            ).bytes
            preserveRealApplicationPdf(bytes,
                "V11-INTERNAL-PROOF-P1-attack-fit-order-" + variant.name + ".pdf")
            Loader.loadPDF(bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { number ->
                    PDFTextStripper().apply {
                        startPage = number
                        endPage = number
                    }.getText(pdf)
                }
                val p1 = pages.first()
                val all = pages.joinToString("\n")
                val c2 = pages.filter {
                    it.contains("C2 - Habilidades, acciones y recursos")
                }.joinToString("\n")
                (1..4).forEach { number ->
                    val key = "QALONG" + number
                    assertFalse(p1.contains(key),
                        "Overlong source attacks cannot overflow P1 cells.")
                    assertTrue(c2.contains(key),
                        "Every deferred source attack must remain intact in C2.")
                    assertEquals(1, Regex(Regex.escape(key)).findAll(all).count())
                }
                (5..10).forEach { number ->
                    val key = "QASHORT" + number
                    assertTrue(p1.contains(key),
                        "Six later real attacks which fit must fill six P1 rows: " + key)
                    assertFalse(c2.contains(key),
                        "An attack printed on P1 must never duplicate to C2: " + key)
                    assertEquals(1, Regex(Regex.escape(key)).findAll(all).count())
                }
                assertTrue(p1.indexOf("QASHORT5") < p1.indexOf("QASHORT10"),
                    "Selected attacks must retain source order.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(pages.last().contains("QALONG") ||
                    pages.last().contains("QASHORT"))
            }
        }
    }

    @Test
    fun g3P1AttacksWithPersistedLineBreaksAndTabsAreRoutedLosslesslyToRealC2AB() {
        val source = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val template = source.permanent.sheet.combatEntries.first {
            it.type == io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ATTACK
        }
        // Test a genuinely allowed source name, range and damage with
        // independent hard breaks, not a synthetic PDF or a preformatted label.
        val attacks = listOf(
            template.copy(
                id = Uuid.parse("a8000000-0000-4000-8000-000000000001"),
                name = "QAATTACKNAMESTART\nQAATTACKNAMEEND",
                rangeText = "5 ft",
                damageEffect = "1d8+2",
                notes = null,
                sortOrder = 1,
            ),
            template.copy(
                id = Uuid.parse("a8000000-0000-4000-8000-000000000002"),
                name = "QAATTACKRANGE",
                rangeText = "QAALCANCEINICIO\r\nQAALCANCEFINAL",
                damageEffect = "1d6+2",
                notes = null,
                sortOrder = 2,
            ),
            template.copy(
                id = Uuid.parse("a8000000-0000-4000-8000-000000000003"),
                name = "QAATTACKDAMAGE",
                rangeText = "10 ft",
                damageEffect = "QADANOINICIO\tQADANOFINAL",
                notes = null,
                sortOrder = 3,
            ),
            template.copy(
                id = Uuid.parse("a8000000-0000-4000-8000-000000000004"),
                name = "QASAFEP1ROW",
                rangeText = null,
                damageEffect = "1d4+1",
                notes = null,
                sortOrder = 4,
            ),
        )
        val stressed = source.copy(permanent = source.permanent.copy(
            sheet = source.permanent.sheet.copy(combatEntries = attacks),
        ))
        val deferredMarkers = listOf(
            "QAATTACKNAMESTART", "QAATTACKNAMEEND",
            "QAATTACKRANGE", "QAALCANCEINICIO", "QAALCANCEFINAL",
            "QAATTACKDAMAGE", "QADANOINICIO", "QADANOFINAL",
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val before = stressed.permanent.sheet.combatEntries.toList()
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = false,
                ), stressed,
            ).bytes
            assertEquals(before, stressed.permanent.sheet.combatEntries,
                "Export must not mutate any persisted attack source.")
            preserveRealApplicationPdf(bytes,
                "V11-INTERNAL-PROOF-P1-attack-multiline-" + variant.name + ".pdf")
            Loader.loadPDF(bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply {
                        startPage = page
                        endPage = page
                    }.getText(pdf)
                }
                val p1 = pages.first()
                val full = pages.joinToString("\n")
                val c2 = pages.filter {
                    it.contains("C2 - Habilidades, acciones y recursos")
                }.joinToString("\n")
                assertTrue(p1.contains("QASAFEP1ROW"),
                    "Ordinary source attacks should still use P1's real rows.")
                assertFalse(c2.contains("QASAFEP1ROW"),
                    "A fitting attack may not be repeated in C2.")
                deferredMarkers.forEach { marker ->
                    assertFalse(p1.contains(marker),
                        "Multiline source $marker cannot occupy a single physical P1 row.")
                    assertTrue(c2.contains(marker),
                        "Full source attack $marker must survive on measured C2.")
                    assertEquals(1, Regex(Regex.escape(marker)).findAll(full).count(),
                        "The source segment $marker must appear exactly once.")
                }
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                deferredMarkers.forEach { marker ->
                    assertFalse(pages.last().contains(marker),
                        "Final manuscript NOTAS is never digital overflow.")
                }
            }
        }
    }

    @Test
    fun g3RealSixMandatoryAttributesSurviveDenseCustomSkillsWithLosslessC2Continuation() {
        val base = sources("03_mara_siete_umbrales_custom_extended.json")
        val sample = requireNotNull(base.permanent.closure.customSkills.firstOrNull())
        val abilities = listOf(
            io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility.STRENGTH,
            io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility.DEXTERITY,
            io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility.CONSTITUTION,
            io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility.INTELLIGENCE,
            io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility.WISDOM,
            io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility.CHARISMA,
        )
        // Technical stress fixture only: genuine backup plus source-backed
        // custom skills with real UUIDs. They are not the owner's game data.
        val extra = abilities.flatMapIndexed { attrIndex, ability ->
            (1..9).map { within ->
                val serial = attrIndex * 9 + within
                sample.copy(
                    id = Uuid.parse("90000000-0000-4000-8000-" +
                        serial.toString().padStart(12, '0')),
                    ability = ability,
                    name = "QASKILL" + serial.toString().padStart(3, '0'),
                    sortOrder = 1000 + serial,
                )
            }
        }
        val stressed = base.copy(permanent = base.permanent.copy(
            closure = base.permanent.closure.copy(
                customSkills = base.permanent.closure.customSkills + extra,
            ),
        ))
        assertEquals(extra.size, stressed.permanent.closure.customSkills.size -
            base.permanent.closure.customSkills.size)
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                stressed,
            ).bytes
            preserveRealApplicationPdf(bytes,
                "V11-INTERNAL-PROOF-dense-six-attributes-" + variant.name + ".pdf")
            Loader.loadPDF(bytes).use { pdf ->
                val textByPage = (1..pdf.numberOfPages).map { number ->
                    PDFTextStripper().apply {
                        startPage = number
                        endPage = number
                    }.getText(pdf)
                }
                val all = textByPage.joinToString("\n")
                for (attributeName in listOf(
                    "FUERZA", "DESTREZA", "CONSTITUCIÓN",
                    "INTELIGENCIA", "SABIDURÍA", "CARISMA",
                )) {
                    assertTrue(textByPage.first().contains(attributeName),
                        "Every mandatory base attribute stays on P1: " + attributeName)
                }
                extra.forEach { skill ->
                    assertEquals(1, Regex(Regex.escape(skill.name)).findAll(all).count(),
                        "Printed source skill must appear exactly once: " + skill.name)
                }
                assertTrue(textByPage.any { it.contains("C2 - Habilidades, acciones y recursos") },
                    "Crowded real skills must continue into measured two-column C2.")
                assertTrue(textByPage.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(textByPage.last().contains("QASKILL"))
                assertTrue((1..pdf.numberOfPages).all { index ->
                    val media = pdf.getPage(index - 1).mediaBox
                    media.width == 612f && media.height == 792f
                })
            }
        }
    }

    @Test
    fun g3RealTabbedNarrativeAndDigitalNotesRenderOnBothVariantsWithoutSourceMutation() {
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val story = "P3_TAB_START\tP3_TAB_MIDDLE\tP3_TAB_END"
        val notes = "CN3_TAB_START\tCN3_TAB_END"
        val patched = original.copy(
            permanent = original.permanent.copy(
                sheet = original.permanent.sheet.copy(
                    background = original.permanent.sheet.background.copy(story = story),
                    generalNotes = notes,
                ),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val request = PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT)
            val semantic = io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SemanticMapper
                .map(request, patched)
            val mapped = io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSourceMapper
                .from(semantic)
            assertEquals(story, mapped.narrative.single {
                it.sourceKey == "narrative:story"
            }.text, "Tab-expanded paper output must retain persisted narrative provenance.")
            assertEquals(notes, semantic.digitalNotes.first { it.sourceId == null }.content)
            val proof = DesktopPcSheetExportService().generateV11Proof(request, patched)
            // Preserve the G3 App-generated tabbed fixture for independent
            // physical PDF inspection in the CI proof artifact.
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-source-tabs-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { physical ->
                    PDFTextStripper().apply {
                        startPage = physical
                        endPage = physical
                    }.getText(pdf)
                }
                assertTrue(pages[2].contains("P3_TAB_START"))
                assertTrue(pages[2].contains("P3_TAB_MIDDLE"))
                assertTrue(pages[2].contains("P3_TAB_END"))
                val digitalPages = pages.filter { it.contains("C-N3 - Notas digitales") }
                assertTrue(digitalPages.any {
                    it.contains("CN3_TAB_START") && it.contains("CN3_TAB_END")
                }, "Tabs must not make genuine C-N3 PDFBox rendering fail.")
                assertFalse(pages.last().contains("CN3_TAB_START"))
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
            }
        }
    }


    @Test
    fun g3RealTabbedTraitsCustomSkillsAndSpellSourcesRenderWithoutMutatingSavedText() {
        // These were previously missed by the four direct PDFBox showText calls
        // (P1 Rasgos, A grouped skill, B independent skill, and P4 source strip).
        // Use a genuine Mara snapshot; modify ONLY the string source fields.
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        val sheet = original.permanent.sheet
        val closure = original.permanent.closure
        val traitName = "TRAITTAB\tALFA"
        val traitOrigin = "ORIGENTAB\tBETA"
        val skillName = "SKILLTAB\tALFA"
        val sourceName = "SRC7\tONE"
        val altered = original.copy(
            permanent = original.permanent.copy(
                sheet = sheet.copy(
                    traits = sheet.traits.mapIndexed { index, trait ->
                        if (index == 0) trait.copy(name = traitName, source = traitOrigin)
                        else trait
                    },
                    spellcastingSources = sheet.spellcastingSources.mapIndexed { index, entry ->
                        if (index == 0) entry.copy(name = sourceName) else entry
                    },
                    spells = sheet.spells.mapIndexed { index, spell ->
                        if (index == 0) spell.copy(
                            name = "QA7\tZ",
                            description = "BOOK_TAB_START " +
                                ("Descripción de conjuro original y continuación. ").repeat(195) +
                                " BOOK_TAB_FINISH",
                        ) else spell
                    },
                ),
                closure = closure.copy(
                    customSkills = closure.customSkills.mapIndexed { index, skill ->
                        if (index == 0) skill.copy(name = skillName) else skill
                    },
                ),
            ),
        )
        assertEquals(traitName, altered.permanent.sheet.traits.first().name)
        assertEquals(traitOrigin, altered.permanent.sheet.traits.first().source)
        assertEquals(skillName, altered.permanent.closure.customSkills.first().name)
        assertEquals(sourceName, altered.permanent.sheet.spellcastingSources.first().name)
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val result = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant,
                    PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = true,
                ),
                altered,
            )
            preserveRealApplicationPdf(
                result.bytes,
                "V11-INTERNAL-PROOF-source-tabs-mechanical-" + variant.name + ".pdf",
            )
            Loader.loadPDF(result.bytes).use { pdf ->
                val pageTexts = (1..pdf.numberOfPages).map { physical ->
                    PDFTextStripper().apply {
                        startPage = physical
                        endPage = physical
                    }.getText(pdf)
                }
                val entire = pageTexts.joinToString("\n")
                assertTrue(entire.contains("TRAITTAB"),
                    "A source-backed mechanical trait may not disappear.")
                assertTrue(entire.contains("ORIGENTAB"),
                    "The actual trait origin may not be discarded.")
                assertTrue(entire.contains("SKILLTAB"),
                    "A custom skill's persisted name must survive V11 flow.")
                assertTrue(pageTexts.any {
                    it.contains("P4 - Conjuros") && it.contains("SRC7")
                }, "The genuine spell origin must survive its compact P4 row.")
                assertTrue(pageTexts.first().contains("TRAITTAB"),
                    "P1 summary must show the first small mechanical trait.")
                val bookPages = pageTexts.filter { it.contains("Libro de conjuros /") &&
                    !it.contains("Índice del Libro de Conjuros") }
                assertTrue(bookPages.any { it.contains("BOOK_TAB_START") })
                assertTrue(bookPages.any { it.contains("BOOK_TAB_FINISH") })
                assertTrue(bookPages.any { it.contains("CONTINUACIÓN: QA7") },
                    "A continued source-backed spell name containing a tab needs a safe PDF heading.")
                assertTrue(pageTexts.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(pageTexts.last().contains("TRAITTAB") ||
                    pageTexts.last().contains("SRC7"))
            }
        }
        // PDF rendering has no ownership of the model and cannot rewrite input.
        assertEquals(skillName, altered.permanent.closure.customSkills.first().name)
        assertEquals(sourceName, altered.permanent.sheet.spellcastingSources.first().name)
    }

    @Test
    fun g3ShortDigitalNoteReservesHandwritingOnlyBelowMeasuredSourceAB() {
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val without = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(
                generalNotes = "", noteCards = emptyList(),
            ),
        ))
        val shortText = "CN3_MANUSCRIPT_SOURCE_UNCHANGED"
        val with = without.copy(permanent = without.permanent.copy(
            sheet = without.permanent.sheet.copy(generalNotes = shortText),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val request = PcSheetV11ExportRequest(
                variant, PcSheetExportStateSelection.PERMANENT,
            )
            val before = DesktopPcSheetExportService().generateV11Proof(request, without)
            val after = DesktopPcSheetExportService().generateV11Proof(request, with)
            preserveRealApplicationPdf(after.bytes,
                "V11-INTERNAL-PROOF-CN3-manuscript-" + variant.name + ".pdf")
            Loader.loadPDF(before.bytes).use { emptyPdf ->
                Loader.loadPDF(after.bytes).use { pdf ->
                    // A secondary manuscript reserve must not create a page.
                    // Only the one source-backed C-N3 page is new.
                    assertEquals(emptyPdf.numberOfPages + 1, pdf.numberOfPages)
                    val texts = (1..pdf.numberOfPages).map { physical ->
                        PDFTextStripper().apply {
                            startPage = physical; endPage = physical
                        }.getText(pdf)
                    }
                    val cn3 = texts.indices.filter {
                        texts[it].contains("C-N3 - Notas digitales")
                    }
                    assertEquals(1, cn3.size)
                    assertEquals(1, Regex(shortText).findAll(texts.joinToString("\n")).count())
                    assertFalse(texts.last().contains(shortText))
                    assertTrue(texts.last().lineSequence().any { it.trim() == "NOTAS" })
                    val page = pdf.getPage(cn3.single())
                    val raw = page.contents.use {
                        it.readBytes().toString(Charsets.ISO_8859_1)
                    }
                    // A genuine PDFBox vector line at the printable-floor row.
                    // Text-only tests would not prove a writable paper reserve.
                    val firstRule = Regex(
                        """(?m)(?:^|\s)42(?:\.0)?\s+92(?:\.0)?\s+m\s+570(?:\.0)?\s+92(?:\.0)?\s+l\s+S(?:\s|$)"""
                    )
                    assertTrue(firstRule.containsMatchIn(raw),
                        "C-N3 must use genuine ruled-paper PDF vectors, not fake underscores.")
                }
            }
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
    fun g3C2ModuleCartelasShowTheirRealBasePageAB() {
        // C248: golden C2 cartelas carry "VIENE P. n" with the module's real base
        // page: P1 for character/skills/attacks/resources, P2 for traits.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), mara,
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }
                val c2Pages = pages.indices.filter {
                    pages[it].contains("C2 - Habilidades, acciones y recursos")
                }
                val c2 = c2Pages.joinToString("\n") { pages[it] }
                // Text runs (text, x, baseline) of the C2 pages, split at column gaps.
                val runs = mutableListOf<Triple<String, Float, Float>>()
                c2Pages.forEach { index ->
                    object : PDFTextStripper() {
                        override fun writeString(
                            text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                        ) {
                            var start = 0
                            for (i in 1..textPositions.size) {
                                val split = i == textPositions.size ||
                                    textPositions[i].xDirAdj - (textPositions[i - 1].xDirAdj +
                                        textPositions[i - 1].widthDirAdj) > 6f
                                if (!split) continue
                                val part = textPositions.subList(start, i)
                                runs += Triple(part.joinToString("") { it.unicode }.trim(),
                                    part.first().xDirAdj, 792f - part.first().yDirAdj)
                                start = i
                            }
                        }
                    }.apply { startPage = index + 1; endPage = index + 1 }.getText(pdf)
                }
                // The label shares its heading baseline, at the right of the same column.
                fun originOf(heading: String): String? {
                    val head = runs.firstOrNull { it.first.startsWith(heading) } ?: return null
                    return runs.firstOrNull {
                        it.first.startsWith("VIENE P. ") && kotlin.math.abs(it.third - head.third) < 0.6f &&
                            it.second > head.second && it.second < head.second + 258f
                    }?.first
                }
                assertTrue(c2.contains("RECURSOS Y MUNICIONES"), "Mara C2 has resources ($variant).")
                listOf("ATAQUES, NOTAS Y ESPACIOS REALES", "RECURSOS Y MUNICIONES").forEach {
                    assertEquals("VIENE P. 1", originOf(it), "$it comes from P1 ($variant).")
                }
                assertEquals("VIENE P. 2", originOf("RASGOS / CONTINUACIÓN P2"),
                    "P2 traits continuation comes from P2 ($variant).")
            }
        }
    }

    @Test
    fun g3P1SpellcastingUsesGoldenReferenceCardsAndShadedSlotGridAB() {
        // C250: golden P1 LANZAMIENTO: three clipped reference cards with small
        // labels and centered values, then a 3x3 grid of shaded level cells whose
        // paper boxes are the real totals, never pre-marked, with gastados/total (C255).
        val ilyra = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val slots = ilyra.permanent.sheet.spellSlots.filter { it.level in 1..9 }
        assertTrue(slots.any { it.spentSlots > 0 }, "Ilyra must really have spent slots.")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), ilyra,
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val page = pdf.getPage(0)
                val native = page.contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                fun rects(w: String, h: String) = Regex(
                    """(?m)(?:^|\s)([\d.]+)\s+([\d.]+)\s+\Q$w\E(?:\.0)?\s+\Q$h\E(?:\.0)?\s+re(?:\s|$)""",
                ).findAll(native).map { it.groupValues[1].toFloat() to it.groupValues[2].toFloat() }.toList()
                // Nine shaded 84x24 level cells in three columns and rows.
                val cells = rects("84", "24")
                assertEquals(9, cells.size, "Golden 3x3 level grid ($variant).")
                assertEquals(setOf(314f, 399f, 484f), cells.map { it.first }.toSet())
                assertEquals(setOf(222f, 198f, 174f), cells.map { it.second }.toSet())
                // Every 8pt box lies inside its level cell; totals are the real ones.
                val boxes = rects("8", "8").filter { (x, y) -> x in 312f..570f && y in 174f..246f }
                assertEquals(slots.filter { it.totalSlots in 1..6 }.sumOf { it.totalSlots }, boxes.size,
                    "One paper box per real slot ($variant).")
                slots.filter { it.totalSlots in 1..6 }.forEach { s ->
                    val cx = 314f + ((s.level - 1) % 3) * 85f
                    val cy = 222f - ((s.level - 1) / 3) * 24f
                    assertEquals(s.totalSlots, boxes.count { (x, y) ->
                        x >= cx + 20f && x + 8f <= cx + 84f && y == cy + 8f
                    }, "N${s.level} boxes inside their cell ($variant).")
                }
                // C255: no pre-marked X; each level shows its real gastados/total
                // count at the right of its cell. Runs are split at column gaps.
                val marks = mutableListOf<Pair<Float, Float>>()
                val labels = mutableListOf<Triple<String, Float, Float>>()
                object : PDFTextStripper() {
                    override fun writeString(
                        text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                    ) {
                        textPositions.forEach {
                            val y = 792f - it.yDirAdj
                            if (it.unicode == "X" && it.xDirAdj > 312f && y in 174f..246f) {
                                marks += it.xDirAdj to y
                            }
                        }
                        var start = 0
                        for (i in 1..textPositions.size) {
                            val split = i == textPositions.size ||
                                textPositions[i].xDirAdj - (textPositions[i - 1].xDirAdj +
                                    textPositions[i - 1].widthDirAdj) > 6f
                            if (!split) continue
                            val part = textPositions.subList(start, i)
                            labels += Triple(part.joinToString("") { it.unicode }.trim(),
                                part.first().xDirAdj, 792f - part.first().yDirAdj)
                            start = i
                        }
                    }
                }.apply { startPage = 1; endPage = 1 }.getText(pdf)
                assertEquals(0, marks.size, "C255: slot boxes are never pre-marked ($variant).")
                slots.filter { it.totalSlots in 1..6 }.forEach { s ->
                    val cx = 314f + ((s.level - 1) % 3) * 85f
                    val cy = 222f - ((s.level - 1) / 3) * 24f
                    assertTrue(labels.any {
                        it.first == "${s.spentSlots}/${s.totalSlots}" &&
                            it.second in cx + 60f..cx + 84f && it.third in cy..cy + 24f
                    }, "N${s.level} real gastados/total at the right of its cell ($variant).")
                }
                // Card labels in the golden order and small, values centered below.
                val cardLabels = listOf("APTITUD MÁGICA", "CD SALVACIÓN", "ATAQUE MÁGICO")
                cardLabels.forEachIndexed { i, label ->
                    val run = labels.firstOrNull { it.first.startsWith(label) && it.third in 265f..275f }
                    assertTrue(run != null && kotlin.math.abs(run.second - (319f + i * 85f)) < 0.6f,
                        "$label at the top-left of its golden card ($variant): $run")
                }
                val text = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
                assertFalse(text.contains("GASTADOS"), "No separate counter header ($variant).")
                (1..9).forEach { assertTrue(text.contains("N$it"), "N$it label ($variant).") }
            }
        }
    }

    @Test
    fun g3P1FooterNamesEachContinuedModuleWithItsRealC2PageAB() {
        // C246: golden P1 footer "ATRIBUTOS Y HABILIDADES A P. 4 · ATAQUES A P. 4 ·
        // MUNICIONES A P. 4" — every continued module with its real physical page.
        val headings = mapOf(
            "PERSONAJE" to "PERSONAJE, HABILIDADES Y ACCIONES / CONTINUACIÓN",
            "HABILIDADES" to "HABILIDADES Y REFERENCIAS",
            "ATAQUES" to "ATAQUES, NOTAS Y ESPACIOS REALES",
            "RECURSOS Y MUNICIONES" to "RECURSOS Y MUNICIONES",
        )
        for (fixture in listOf("03_mara_siete_umbrales_custom_extended.json",
            "01_aldren_vale_srd5_1_champion_fighter.json")) {
            for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
                val proof = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                    sources(fixture),
                )
                Loader.loadPDF(proof.bytes).use { pdf ->
                    val pages = (1..pdf.numberOfPages).map { n ->
                        PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                    }
                    val footer = pages.first().lineSequence().firstOrNull { it.contains(" A P. ") }
                    assertTrue(footer != null, "P1 footer lists continued modules ($fixture $variant).")
                    val links = Regex("""([A-ZÁÉÍÓÚ ]+?) A P\. (\d+)""").findAll(footer!!)
                        .map { it.groupValues[1].trim().removePrefix("· ").trim() to it.groupValues[2].toInt() }
                        .toList()
                    assertTrue(links.isNotEmpty())
                    links.forEach { (module, page) ->
                        val heading = requireNotNull(headings[module]) { "Unknown P1 module $module" }
                        val first = pages.indexOfFirst {
                            it.contains("C2 - Habilidades, acciones y recursos") && it.contains(heading)
                        } + 1
                        assertEquals(first, page,
                            "$module must point to its first real C2 page ($fixture $variant).")
                    }
                }
            }
        }
    }

    @Test
    fun g3P2FooterNamesEachContinuedModuleWithItsRealC2PageAB() {
        // C251: golden P2 footer "EQUIPO A P. 4 · TESORO A P. 5 · EQUIPO ESPECIAL
        // A P. 5": every continued P2 module with its first real physical page.
        val sections = mapOf(
            "RASGOS" to listOf("C2 - Rasgos / continuación", "RASGOS / CONTINUACIÓN P2"),
            "EQUIPO Y TESORO" to listOf("C2 - Equipo y tesoro / EQUIPO Y TESORO"),
            "EQUIPO" to listOf("C2 - Equipo y tesoro / EQUIPO"),
            "TESORO" to listOf("C2 - Equipo y tesoro / TESORO"),
            "EQUIPO ESPECIAL" to listOf("C2 - Equipo especial / continuación"),
        )
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), mara,
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }
                val footer = pages[1].lineSequence().firstOrNull { it.contains(" A P. ") }
                assertTrue(footer != null, "P2 footer lists continued modules ($variant).")
                assertFalse(pages[1].contains("OTROS C2"), "Old P2 footer grammar ($variant).")
                val links = Regex("""([A-ZÁÉÍÓÚ ]+?) A P\. (\d+)""").findAll(footer!!)
                    .map { it.groupValues[1].trim() to it.groupValues[2].toInt() }.toList()
                assertEquals(listOf("RASGOS", "EQUIPO", "EQUIPO ESPECIAL"), links.map { it.first },
                    "Mara P2 continues traits, equipment and special equipment ($variant).")
                links.forEach { (module, page) ->
                    val titles = requireNotNull(sections[module]) { "Unknown P2 module $module" }
                    val first = pages.indexOfFirst { text ->
                        titles.any { title ->
                            text.lineSequence().any { it.trim() == title || it.trim().startsWith("$title ") }
                        }
                    } + 1
                    assertEquals(first, page, "$module must point to its first real C2 page ($variant).")
                }
            }
        }
    }

    @Test
    fun g3P3FooterNamesEachContinuedFlowWithItsRealPageAB() {
        // C253: golden P3 footer "HISTORIA A P. 6" on the module-link band. Real
        // Aldren with a long persisted story, then also a long persisted ideal.
        val actual = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val story = "C253STORY " + "Viaje registrado con fechas y testigos verificables. ".repeat(300)
        val longStory = actual.copy(permanent = actual.permanent.copy(
            sheet = actual.permanent.sheet.copy(
                background = actual.permanent.sheet.background.copy(story = story),
            ),
        ))
        val both = longStory.copy(permanent = longStory.permanent.copy(
            sheet = longStory.permanent.sheet.copy(
                background = longStory.permanent.sheet.background.copy(
                    ideals = "C253IDEAL " + "Proteger a quien no puede defenderse. ".repeat(40),
                ),
            ),
        ))
        for ((fixture, expected) in listOf(
            actual to emptyList(), longStory to listOf("HISTORIA"),
            both to listOf("IDEALES", "HISTORIA"),
        )) {
            for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
                val proof = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), fixture,
                )
                Loader.loadPDF(proof.bytes).use { pdf ->
                    val pages = (1..pdf.numberOfPages).map { n ->
                        PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                    }
                    val p3 = pages.indexOfFirst { it.contains("P3 - Trasfondo") }
                    assertTrue(p3 >= 0)
                    val footer = pages[p3].lineSequence().firstOrNull { it.contains(" A P. ") }
                    val links = footer?.let { line ->
                        Regex("""([A-ZÁÉÍÓÚ ]+?) A P\. (\d+)""").findAll(line)
                            .map { it.groupValues[1].trim() to it.groupValues[2].toInt() }.toList()
                    }.orEmpty()
                    assertEquals(expected, links.map { it.first }, "P3 links ($variant): $footer")
                    links.forEach { (module, page) ->
                        val first = pages.indexOfFirst { text ->
                            text.contains("P3 - Narrativa / continuación") && when (module) {
                                "HISTORIA" -> text.contains("HISTORIA Y CRÓNICA / CONTINUACIÓN")
                                else -> text.contains("IDEALES / CONTINUACIÓN")
                            }
                        } + 1
                        assertEquals(first, page, "$module must point to its first real page ($variant).")
                    }
                    if (links.isNotEmpty()) {
                        val native = pdf.getPage(p3).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                        assertTrue(Regex("""(?m)(?:^|\s)39\.69\s+43\.94\s+532\.62\s+13\.04\s+re(?:\s|$)""") /* C290 golden band */
                            .containsMatchIn(native), "P3 golden footer band ($variant).")
                    }
                }
            }
        }
    }

    @Test
    fun g3ModuleLinksUseGoldenShadedFooterBandOnP1AndP2AB() {
        // C252: golden P1/P2 module links sit on a shaded 13pt band across the page
        // area, in small Barlow ink (6.1pt), not in body-size Fira.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), mara,
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                for (index in 0..1) {
                    val native = pdf.getPage(index).contents.use {
                        it.readBytes().toString(Charsets.ISO_8859_1)
                    }
                    assertTrue(Regex("""(?m)(?:^|\s)39\.69\s+43\.94\s+532\.62\s+13\.04\s+re(?:\s|$)""") /* C290 golden band */
                        .containsMatchIn(native), "P${index + 1} golden footer band ($variant).")
                    val sizes = mutableListOf<Pair<Float, Float>>()
                    object : PDFTextStripper() {
                        override fun writeString(
                            text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                        ) {
                            if (text.contains(" A P. ")) {
                                textPositions.forEach { sizes += it.fontSizeInPt to 792f - it.yDirAdj }
                            }
                        }
                    }.apply { startPage = index + 1; endPage = index + 1 }.getText(pdf)
                    assertTrue(sizes.isNotEmpty(), "P${index + 1} module links ($variant).")
                    // PDFBox reports fontSizeInPt in whole points (6.1pt reads 6.0).
                    assertTrue(sizes.all { kotlin.math.abs(it.first - 6.1f) < 0.2f &&
                        kotlin.math.abs(it.second - 47.34f) < 0.6f }, // C290: golden band baseline
                        "P${index + 1} links in 6.1pt ink on the band baseline ($variant): ${sizes.distinct().take(4)}")
                }
            }
        }
    }

    @Test
    fun g3P1AmmunitionMarksFollowTheRealQuantityForZeroOneFiveTwentyAndMoreAB() {
        // P117: up to 20 rounds print exactly that many golden paper boxes
        // (two rows of ten, groups of five); more than 20 print the
        // "MARCAS: ____ / n" counter; a real 0 prints "0" and no box.
        val aldren = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val ammoId = aldren.permanent.closure.inventoryUsage.single {
            it.kind == io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind.AMMUNITION
        }.itemId
        val boxes = Regex(
            """(?:^|\s)(4[1-9]\d(?:\.\d+)?|5[0-5]\d(?:\.\d+)?)\s+(?:119|106)\s+7\.94\s+7\.94\s+re(?:\s|$)"""
        )
        for (quantity in listOf(0, 1, 5, 20, 21, 35)) {
            val fixture = aldren.copy(permanent = aldren.permanent.copy(
                sheet = aldren.permanent.sheet.copy(inventoryItems =
                    aldren.permanent.sheet.inventoryItems.map {
                        if (it.id == ammoId) it.copy(quantity = quantity) else it
                    }),
            ))
            for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
                val proof = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), fixture,
                )
                Loader.loadPDF(proof.bytes).use { pdf ->
                    val ops = pdf.getPage(0).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                    val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
                    val printed = boxes.findAll(ops).count()
                    assertTrue(p1.contains("Virotes"), "Real ammunition on P1 ($quantity, $variant).")
                    when {
                        quantity == 0 -> {
                            assertEquals(0, printed, "A real 0 draws no box ($variant).")
                            assertTrue(Regex("(?m)^Virotes 0\\b|Virotes\\s+0\\b").containsMatchIn(p1),
                                "A real 0 prints 0 ($variant): $p1")
                        }
                        quantity <= 20 -> assertEquals(quantity, printed,
                            "$quantity rounds draw $quantity boxes ($variant).")
                        else -> {
                            assertEquals(0, printed, "More than 20 never draws boxes ($variant).")
                            assertTrue(p1.contains("MARCAS: ____ / $quantity"),
                                "More than 20 prints the counter ($quantity, $variant).")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun g3EveryPageCarriesTheLogoAndCharacterNameHeaderWithoutP1DuplicateAB() {
        // GLB03: every physical page has the owner's logo image and the
        // character name in the same header line; P1 never repeats the name.
        for (fixture in listOf("03_mara_siete_umbrales_custom_extended.json",
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json")) {
            val source = sources(fixture)
            val name = source.permanent.sheet.name
            for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
                val proof = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source,
                )
                Loader.loadPDF(proof.bytes).use { pdf ->
                    (1..pdf.numberOfPages).forEach { number ->
                        val page = pdf.getPage(number - 1)
                        val images = page.resources.xObjectNames.count {
                            page.resources.isImageXObject(it)
                        }
                        assertTrue(images >= 1, "Page $number carries the logo image ($fixture $variant).")
                        val headerLine = mutableListOf<Pair<String, Float>>()
                        object : PDFTextStripper() {
                            override fun writeString(
                                text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                            ) {
                                val first = textPositions.firstOrNull() ?: return
                                if (first.yDirAdj < 80f) headerLine += text.trim() to first.yDirAdj
                            }
                        }.apply { startPage = number; endPage = number }.getText(pdf)
                        assertTrue(headerLine.any { it.first == name },
                            "Page $number header names the character ($fixture $variant): $headerLine")
                    }
                    val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
                    assertEquals(1, Regex(Regex.escape(name)).findAll(p1).count(),
                        "P1 never repeats the character name ($fixture $variant).")
                }
            }
        }
    }

    @Test
    fun g3PhysicalModuleOrderAndLetterSizeHoldOnEveryPageAB() {
        // BK02 / GLB01: with real digital notes AND the optional Spellbook, the
        // physical order is P1, P2, P3, C2, P4, C-N3, book index, book, and the
        // manuscript NOTAS sheet last; every physical page is US Letter.
        val ilyra = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val template = sources("03_mara_siete_umbrales_custom_extended.json")
            .permanent.sheet.noteCards.first()
        val withNotes = ilyra.copy(permanent = ilyra.permanent.copy(
            sheet = ilyra.permanent.sheet.copy(noteCards = listOf(
                template.copy(title = "ORDENQA", content = "ORDENQA " + "nota real ".repeat(40)),
            )),
        ))
        for (fixture in listOf(withNotes, sources("03_mara_siete_umbrales_custom_extended.json"),
            sources("01_aldren_vale_srd5_1_champion_fighter.json"))) {
            for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
                val proof = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT,
                        includeSpellDescriptions = fixture === withNotes), fixture,
                )
                Loader.loadPDF(proof.bytes).use { pdf ->
                    (0 until pdf.numberOfPages).forEach { index ->
                        val box = pdf.getPage(index).mediaBox
                        assertEquals(612f, box.width, "Page ${index + 1} width ($variant).")
                        assertEquals(792f, box.height, "Page ${index + 1} height ($variant).")
                    }
                    val pages = (1..pdf.numberOfPages).map { n ->
                        PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                    }
                    fun first(title: String) = pages.indexOfFirst { it.contains(title) }
                    fun last(title: String) = pages.indexOfLast { it.contains(title) }
                    val order = listOf("P1 - ", "P2 - Equipo y tesoro", "P3 - Trasfondo", "C2 - ",
                        "P4 - Conjuros", "C-N3 - Notas digitales", "Índice del Libro de Conjuros",
                        "Libro de conjuros /").filter { first(it) >= 0 }
                    order.zipWithNext().forEach { (a, b) ->
                        assertTrue(last(a) < first(b), "$a must come before $b ($variant).")
                    }
                    if (fixture === withNotes) {
                        assertTrue(listOf("C-N3 - Notas digitales", "Índice del Libro de Conjuros",
                            "Libro de conjuros /").all { first(it) >= 0 },
                            "Fixture carries C-N3 and the Spellbook ($variant).")
                    }
                    assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" } &&
                        order.none { pages.last().contains(it) },
                        "The manuscript NOTAS sheet is the last page ($variant).")
                }
            }
        }
    }

    @Test
    fun g3DigitalNotesNeverMixWithC2SpellbookOrFinalSheetWhenBothExistAB() {
        // CN303: a character with real digital notes AND the optional Spellbook.
        // Note text must live only on C-N3 pages: never on C2, the book index,
        // the book cards or the final manuscript NOTAS sheet.
        val ilyra = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val template = sources("03_mara_siete_umbrales_custom_extended.json")
            .permanent.sheet.noteCards.first()
        val notes = (1..2).map { n ->
            template.copy(
                id = Uuid.parse("c3030000-0000-4000-8000-00000000000$n"),
                title = "CN303TITULO$n",
                content = "CN303INICIO$n " + "contenido real de la nota ".repeat(30) + "CN303FIN$n",
                sortOrder = n,
            )
        }
        val both = ilyra.copy(permanent = ilyra.permanent.copy(
            sheet = ilyra.permanent.sheet.copy(noteCards = notes),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = true), both,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-CN3-with-spellbook-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }
                val cn3 = pages.indices.filter { pages[it].contains("C-N3 - Notas digitales") }
                val book = pages.indices.filter {
                    pages[it].contains("Libro de conjuros /") ||
                        pages[it].contains("Índice del Libro de Conjuros")
                }
                assertTrue(cn3.isNotEmpty() && book.isNotEmpty(),
                    "Fixture must carry both C-N3 and the Spellbook ($variant).")
                assertTrue(cn3.intersect(book.toSet()).isEmpty(), "C-N3 and Libro are distinct pages.")
                val markers = listOf("CN303TITULO1", "CN303INICIO1", "CN303FIN1",
                    "CN303TITULO2", "CN303INICIO2", "CN303FIN2")
                markers.forEach { marker ->
                    val where = pages.indices.filter { pages[it].contains(marker) }
                    assertEquals(1, where.size, "$marker prints exactly once ($variant).")
                    assertTrue(where.single() in cn3, "$marker only on a C-N3 page ($variant).")
                }
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(pages.last().contains("CN303"), "The final sheet has no digital note.")
            }
        }
    }

    @Test
    fun g3VeryLongSourceNoteTitleKeepsStableContinuationIdInRealPdfAB() {
        val fixture = sources("03_mara_siete_umbrales_custom_extended.json")
        val original = fixture.permanent.sheet.noteCards.first()
        val longName = "TÍTULO EXTENSO DE NOTA " + "persistida ".repeat(115)
        val originalContent = "CN3-LARGE-NAME-START " +
            (1..440).joinToString(" ") { "CN3-LARGE-NAME-WORD" + it } +
            " CN3-LARGE-NAME-END"
        val patched = fixture.copy(permanent = fixture.permanent.copy(
            sheet = fixture.permanent.sheet.copy(
                generalNotes = "",
                noteCards = listOf(original.copy(title = longName, content = originalContent)),
            ),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val request = PcSheetV11ExportRequest(
                variant, PcSheetExportStateSelection.PERMANENT,
            )
            val semantic = io.github.mrsimkin.dndcustomaid.shared.character
                .PcSheetV11SemanticMapper.map(request, patched)
            val sourceId = requireNotNull(semantic.digitalNotes.single().sourceId)
            val proof = DesktopPcSheetExportService().generateV11Proof(request, patched)
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-CN3-long-source-title-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { physical ->
                    PDFTextStripper().apply {
                        startPage = physical; endPage = physical
                    }.getText(pdf)
                }
                val joined = pages.joinToString("\n")
                assertEquals(1, Regex("CN3-LARGE-NAME-START").findAll(joined).count())
                assertEquals(1, Regex("CN3-LARGE-NAME-END").findAll(joined).count())
                assertTrue(pages.any { it.contains("CONTINÚA ID: " + sourceId) },
                    "A long real note title cannot erase its UUID in the continuation.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(pages.last().contains("CN3-LARGE-NAME-END"))
            }
        }
    }

    @Test
    fun g3RealMaraC_N3SourceAnchorsAndLongNoteContinuationKeepIdentityAB() {
        // The frozen V11 contract requires genuine, exclusively digital
        // C-N3 pages. Nine original titled Mara cards + general notes were
        // previously printed without a visible record boundary on the page.
        val fixture = sources("03_mara_siete_umbrales_custom_extended.json")
        val cards = fixture.permanent.sheet.noteCards
        val note = cards.first()
        val longText = "CN3ACTUALSTART " +
            (1..440).joinToString(" ") { "CN3REALCONTENTWORD" + it } +
            " CN3ACTUALFINISH"
        val stressed = fixture.copy(permanent = fixture.permanent.copy(
            sheet = fixture.permanent.sheet.copy(
                generalNotes = "",
                noteCards = listOf(note.copy(content = longText)),
            ),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val regular = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant,
                    PcSheetExportStateSelection.PERMANENT), fixture,
            )
            preserveRealApplicationPdf(regular.bytes,
                "V11-INTERNAL-PROOF-CN3-separated-Mara-" + variant.name + ".pdf")
            Loader.loadPDF(regular.bytes).use { pdf ->
                val texts = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }
                        .getText(pdf)
                }
                // C314: with the golden note spacing Mara's nine long cards may use
                // more than one C-N3 page; every card start is still anchored once.
                val noteIndices = texts.indices.filter {
                    texts[it].contains("C-N3 - Notas digitales")
                }
                assertTrue(noteIndices.isNotEmpty() && noteIndices.first() > 0)
                val noteText = noteIndices.joinToString("\n") { texts[it] }
                assertTrue(noteText.contains("Nota 1") && noteText.contains("Nota 9"))
                val startBandRects = noteIndices.sumOf { index ->
                    val raw = pdf.getPage(index).contents.use {
                        it.readBytes().toString(Charsets.ISO_8859_1)
                    }
                    Regex(
                        // C296: the golden note start is the thin rule under its title
                        // (x48.19..561.26), not a blue band.
                        """(?m)(?:^|\s)48\.19\d*\s+[-\d.]+\s+m\s+561\.26\d*\s+[-\d.]+\s+l(?:\s|$)"""
                    ).findAll(raw).count()
                }
                // C333: the untitled general note has no title, so no title rule.
                assertEquals(fixture.permanent.sheet.noteCards.size,
                    startBandRects, "One source-backed title rule per genuine titled note.")
                assertTrue(texts.last().lineSequence().any { it.trim() == "NOTAS" })
            }
            val long = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant,
                    PcSheetExportStateSelection.PERMANENT), stressed,
            )
            preserveRealApplicationPdf(long.bytes,
                "V11-INTERNAL-PROOF-CN3-long-note-continuation-" + variant.name + ".pdf")
            Loader.loadPDF(long.bytes).use { pdf ->
                val texts = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }
                        .getText(pdf)
                }
                val cn3 = texts.filter {
                    it.contains("C-N3 - Notas digitales")
                }
                assertTrue(cn3.size >= 2,
                    "Long genuine digital note must span multiple C-N3 physical pages.")
                assertTrue(cn3.drop(1).all {
                    it.contains("CONTINÚA: " + note.title)
                }, "A physical continuation page must identify the original note.")
                val all = cn3.joinToString("\n")
                assertEquals(1, Regex("CN3ACTUALSTART").findAll(all).count())
                assertEquals(1, Regex("CN3ACTUALFINISH").findAll(all).count())
                assertEquals(1, Regex("CN3REALCONTENTWORD440").findAll(all).count())
                assertTrue(texts.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(texts.last().contains("CN3REALCONTENTWORD"))
                assertEquals(longText, stressed.permanent.sheet.noteCards.single().content)
            }
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
                // the Notes manuscript grid. Include each source glyph's first
                // pixels: the PDFBox labels start at x=44 and x=314.
                // Never count page metadata.
                addRegion("equipment-left",
                    Rectangle2D.Float(40f, 95f, 222f, 380f))
                addRegion("equipment-right",
                    Rectangle2D.Float(310f, 95f, 222f, 380f))
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
            // Verify genuine stroked AND filled clipped cartela path, not
            // just a background image or the literal EQUIPO heading text.
            // Its whole 702..716.5pt band stays below the header rule y=717.
            val commands = page.contents.use {
                it.readBytes().toString(Charsets.ISO_8859_1)
            }
            val clippedCartela = Regex(
                """(?s)(?:^|\s)47\s+702\s+m\s+565\s+702\s+l\s+570\s+707\s+l.*?\s+B(?:\s|$)"""
            )
            assertTrue(clippedCartela.containsMatchIn(commands),
                "A real beveled blue C2 EQUIPO cartela must be filled and stroked " +
                    "without crossing the fixed page header.")
            val filledOutlines = Regex("""(?m)(?:^|\s)B(?:\s|$)""")
                .findAll(commands).count()
            assertTrue(filledOutlines >= 2,
                "Real C2 EQUIPO and manuscript NOTAS must each draw " +
                    "their own filled-and-stroked clipped blue cartela.")
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
    fun g3P2C2InventoryPrintsContiguousSourceOrderAcrossThePhysicalBoundaryAB() {
        // Source-backed Aldren equipment deliberately interleaves one short,
        // one genuinely non-fitting P2 identity and one later short identity.
        // Filtering all fitting rows caused the later item to leap onto P2,
        // ahead of its persisted predecessor in C2.
        val real = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val template = real.permanent.sheet.inventoryItems.first { !it.special }
        val items = listOf(
            template.copy(
                id = Uuid.parse("c2000000-0000-4000-8000-000000000001"),
                name = "QAPRIMERO", quantity = 7131, sortOrder = 1,
                special = false, notes = null,
            ),
            template.copy(
                id = Uuid.parse("c2000000-0000-4000-8000-000000000002"),
                name = "QALARGO " + "identidad original completa ".repeat(12),
                quantity = 7132, sortOrder = 2,
                special = false, notes = null,
            ),
            template.copy(
                id = Uuid.parse("c2000000-0000-4000-8000-000000000003"),
                name = "QADESPUES", quantity = 7133, sortOrder = 3,
                special = false, notes = null,
            ),
        )
        val altered = real.copy(
            permanent = real.permanent.copy(
                sheet = real.permanent.sheet.copy(inventoryItems = items),
                closure = real.permanent.closure.copy(inventoryUsage = emptyList()),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ),
                altered,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-P2-C2-source-order-" + variant + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { number ->
                    PDFTextStripper().apply {
                        startPage = number
                        endPage = number
                    }.getText(pdf)
                }
                val p2 = pages.first { it.contains("P2 - Equipo y tesoro") }
                val c2 = pages.filter { it.contains("C2 - Equipo y tesoro / EQUIPO") }
                    .joinToString("\n")
                assertTrue(p2.contains("QAPRIMERO") && p2.contains("7131"))
                // C211 R1: the long identity is no longer deferred; it starts on P2
                // (wrapped, continuation lines indented). Source order must hold in
                // the physical reading sequence P2 -> C2, every amount exactly once.
                assertTrue(p2.contains("QALARGO"),
                    "C211: a long label starts on P2 instead of emptying it.")
                val sequence = (listOf(p2) + c2).joinToString("\n")
                assertTrue(sequence.indexOf("QAPRIMERO") < sequence.indexOf("QALARGO") &&
                    sequence.indexOf("QALARGO") < sequence.indexOf("QADESPUES"),
                    "A later short identity must not leap ahead of its long predecessor.")
                for (amount in listOf("7131", "7132", "7133")) {
                    assertEquals(1, Regex("(?<![0-9])" + amount + "(?![0-9])")
                        .findAll(pages.joinToString("\n")).count(),
                        "Each real amount prints exactly once: $amount")
                }
                assertFalse(c2.contains("7131") || c2.contains("QAPRIMERO"))
                assertEquals(1, pages.joinToString("\n").split("QADESPUES").size - 1)
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                for (page in pdf.pages) {
                    assertEquals(PDRectangle.LETTER.width, page.mediaBox.width)
                    assertEquals(PDRectangle.LETTER.height, page.mediaBox.height)
                }
            }
        }
    }

    @Test
    fun g3OversizedSpecialEquipmentKeepsSourceIdentityOnEveryC2PageAB() {
        // The approved four-column C2 must not orphan an oversized
        // item's continued description when its original NOMBRE/UBICACIÓN
        // fields were printed on an earlier physical page.
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        val template = original.permanent.sheet.inventoryItems.first { it.special }
        val label = "QAC2ANCLA"
        val longDescription = "QAC2INICIO " +
            ("Descripción auténtica del equipo con texto extenso y legible. ")
                .repeat(175) + " QAC2FINAL"
        val huge = template.copy(
            id = Uuid.parse("c3000000-0000-4000-8000-000000000001"),
            name = label,
            description = longDescription,
            notes = null,
            special = true,
            sortOrder = 5000,
        )
        val changed = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(
                inventoryItems = original.permanent.sheet.inventoryItems
                    .filterNot { it.special } + huge,
            ),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ), changed,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-C2-long-special-item-" + variant + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { pageIndex ->
                    PDFTextStripper().apply {
                        startPage = pageIndex
                        endPage = pageIndex
                    }.getText(pdf)
                }
                val specialPages = pages.filter {
                    it.contains("C2 - Equipo especial / continuación")
                }
                assertTrue(specialPages.size >= 2,
                    "The real source description must cross a physical C2 page.")
                assertTrue(specialPages.first().contains(label) &&
                    specialPages.first().contains("QAC2INICIO"))
                assertFalse(specialPages.first().contains("CONTINÚA EQUIPO: " + label),
                    "The first appearance is the genuine source row, not a repeat.")
                specialPages.drop(1).forEach { page ->
                    assertTrue(page.contains("CONTINÚA EQUIPO: " + label),
                        "A new physical C2 page must retain the source identity.")
                    assertTrue(page.contains("UBICACIÓN") && page.contains("NOMBRE") &&
                        page.contains("DESCRIPCIÓN") && page.contains("ACTIVO"),
                        "The approved four columns must remain physically present.")
                }
                assertEquals(1, Regex("QAC2INICIO").findAll(
                    pages.joinToString("\n")).count())
                assertEquals(1, Regex("QAC2FINAL").findAll(
                    pages.joinToString("\n")).count())
                assertTrue(specialPages.last().contains("QAC2FINAL"),
                    "The complete final source suffix must survive measured pagination.")
                assertFalse(pages.last().contains("QAC2FINAL"))
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                for (page in pdf.pages) {
                    assertEquals(PDRectangle.LETTER.width, page.mediaBox.width)
                    assertEquals(PDRectangle.LETTER.height, page.mediaBox.height)
                }
            }
        }
    }

    @Test
    fun g3C2SpecialEquipmentMultilineLongNameUsesRealIdContinuationAB() {
        // Persisted names may contain real line breaks. They are rendered in
        // the original four-column table, but a single-line continuation
        // header must use the TRUE ID rather than passing CR/LF to the font.
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        val template = original.permanent.sheet.inventoryItems.first { it.special }
        val id = Uuid.parse("c3000000-0000-4000-8000-000000000002")
        val longName = "QANOMBREINICIO\n" +
            "Denominación extensa original comprobable ".repeat(18) +
            "\nQANOMBREFINAL"
        val item = template.copy(
            id = id,
            name = longName,
            description = "QADESCINICIO " +
                "Detalle fuente persistido para continuidad especial. ".repeat(175) +
                " QADESCFINAL",
            notes = null,
            special = true,
            sortOrder = 5001,
        )
        val input = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(
                inventoryItems = original.permanent.sheet.inventoryItems
                    .filterNot { it.special } + item,
            ),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ), input,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-C2-special-multiline-id-" + variant + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply {
                        startPage = n
                        endPage = n
                    }.getText(pdf)
                }
                val c2 = pages.filter {
                    it.contains("C2 - Equipo especial / continuación")
                }
                assertTrue(c2.size >= 2,
                    "Persisted oversized description must span physical C2 pages.")
                val allText = pages.joinToString("\n")
                assertEquals(1, Regex("QANOMBREINICIO").findAll(allText).count())
                assertEquals(1, Regex("QANOMBREFINAL").findAll(allText).count())
                assertEquals(1, Regex("QADESCINICIO").findAll(allText).count())
                assertEquals(1, Regex("QADESCFINAL").findAll(allText).count())
                assertTrue(c2.first().contains("QANOMBREINICIO"))
                assertFalse(c2.first().contains("CONTINÚA ID: " + id))
                c2.drop(1).forEach { page ->
                    assertTrue(page.contains("CONTINÚA ID: " + id),
                        "Each continued real C2 page must retain the source UUID.")
                    assertFalse(page.contains("CONTINÚA EQUIPO: QANOMBREINICIO"))
                    assertTrue(page.contains("UBICACIÓN") &&
                        page.contains("NOMBRE") && page.contains("DESCRIPCIÓN") &&
                        page.contains("ACTIVO"))
                }
                assertTrue(c2.last().contains("QADESCFINAL"))
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(pages.last().contains("QADESCINICIO"))
                for (page in pdf.pages) {
                    assertEquals(PDRectangle.LETTER.width, page.mediaBox.width)
                    assertEquals(PDRectangle.LETTER.height, page.mediaBox.height)
                }
            }
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
            fun flat(s: String) = s.replace(Regex("\\s+"), " ")
            val p2 = physicalTexts.first { it.contains("P2 - Equipo y tesoro") }
            // C211 R1: P2 is filled first; long names wrap there, the rest continues in C2.
            assertTrue(flat(p2).contains("MATERIAL INVENTARIADO 001"),
                "C211: the first long object starts on P2 instead of leaving it empty.")
            assertTrue(flat(continuation.joinToString("\n")).contains("MATERIAL INVENTARIADO 100"),
                "The last long object continues in the C2 table.")
            val all = flat(physicalTexts.joinToString("\n"))
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
                // Treasure keeps an independent genuine paired module, even
                // when it shares a physical C2 page with ordinary Equipment.
                it.contains("C2 - Equipo y tesoro") &&
                    it.contains("TESORO / CANTIDAD")
            }
            assertTrue(treasureC2.isNotEmpty(),
                "Real treasure must remain a distinct C2 paired module even on shared paper.")
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


    @Test
    fun g2G3ApprovedRasgosAreIdentityOnlyOnceAcrossP1P2AndC2() {
        // Gate IDs RSG01..RSG06, P201/P207, P302, C201:
        // print name + real type + source, never description/notes.
        // The App's full CharacterTrait remains unchanged/persisted.
        val origin = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val original = origin.permanent.sheet.traits.first()
        val rawTraits = (0 until 48).map { index ->
            original.copy(
                id = Uuid.random(),
                name = "RSGQ" + index.toString().padStart(3, '0'),
                type = io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType.CLASS,
                source = "PícaroQA",
                description = "DESC_RSG_NEVER_PRINT_" + index,
                notes = "NOTES_RSG_NEVER_PRINT_" + index,
                sortOrder = index,
            )
        }
        val edited = origin.copy(
            permanent = origin.permanent.copy(
                sheet = origin.permanent.sheet.copy(traits = rawTraits),
            ),
        )
        assertEquals(rawTraits.size, edited.permanent.sheet.traits.size)
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val request = PcSheetV11ExportRequest(
                variant, PcSheetExportStateSelection.PERMANENT,
            )
            val semantic = io.github.mrsimkin.dndcustomaid.shared.character
                .PcSheetV11SemanticMapper.map(request, edited)
            val shortForms = io.github.mrsimkin.dndcustomaid.shared.character
                .PcSheetV11FlowSourceMapper.from(semantic).traitDescriptions
            assertEquals(48, shortForms.size)
            shortForms.forEachIndexed { index, row ->
                assertEquals("RSGQ" + index.toString().padStart(3, '0') +
                    " - Rasgo de Clase: PícaroQA", row.text)
                assertFalse(row.text.contains("DESC_RSG_NEVER_PRINT"))
                assertFalse(row.text.contains("NOTES_RSG_NEVER_PRINT"))
            }
            val result = DesktopPcSheetExportService().generateV11Proof(
                request, edited,
            )
            Loader.loadPDF(result.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply {
                        startPage = page
                        endPage = page
                    }.getText(pdf)
                }
                val whole = pages.joinToString("\n")
                assertTrue(pages.first().contains("RASGOS Y ATRIBUTOS"),
                    "P1 must retain the paper trait summary area.")
                assertTrue(pages.any { it.contains("P2 - Equipo y tesoro") },
                    "P2 remains the continuation/trait area.")
                assertTrue(pages.any { it.contains("C2") && it.contains("RASGOS") },
                    "48 real trait identities must overflow to C2, not disappear.")
                rawTraits.forEachIndexed { index, trait ->
                    val key = "RSGQ" + index.toString().padStart(3, '0')
                    assertEquals(1, Regex(Regex.escape(key)).findAll(whole).count(),
                        "Trait ID " + trait.id + " was duplicated or lost ($variant).")
                }
                assertFalse(whole.contains("DESC_RSG_NEVER_PRINT"),
                    "Rasgo descriptions are NEVER permitted on V11 paper.")
                assertFalse(whole.contains("NOTES_RSG_NEVER_PRINT"),
                    "Rasgo notes are NEVER permitted on V11 paper.")
                assertTrue(whole.contains("Rasgo de Clase"),
                    "The real category must be visible, not only the trait name.")
                assertTrue(whole.contains("PícaroQA"),
                    "The real original trait source must be visible.")
                val narrative = pages.first { it.contains("P3 - Trasfondo") }
                assertFalse(narrative.contains("RSGQ"),
                    "V11 P3 is narrative-only; no mechanical trait overflow.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" },
                    "Overflow must preserve the final manuscript page.")
                assertEquals(48, edited.permanent.sheet.traits.size,
                    "PDF projection must not mutate stored Rasgo cards.")
            }
        }
    }

    @Test
    fun g3RealP1RasgosUsesClippedBlueCartelaInsideOriginalHeaderStrip() {
        // P1 title is cosmetic only; no moved or truncated source identity.
        // Owner C2c / G2 RSG01-RSG05 / P112. Verified on REAL App PDFBox.
        for ((name, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources(name),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val ops = pdf.getPage(0).contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                val polygon = Regex(
                    """(?s)(?:^|\s)315\s+368\s+m\s+567\s+368\s+l\s+""" +
                        """570\s+371\s+l.*?312\s+371\s+l\s+h\s+B(?:\s|$)"""
                )
                assertEquals(1, polygon.findAll(ops).count(),
                    "Approved P1 Rasgos title requires ONE genuine " +
                        "eight-segment clipped blue band ($name $variant).")
                assertTrue(Regex(
                    """(?:^|\s)312\s+301\s+258\s+81\s+re(?:\s|$)"""
                ).containsMatchIn(ops),
                    "Original source-backed two-column Rasgos panel must not move.")
                // C265: golden cartela title size 9.1 pt.
                assertTrue(Regex("""(?m)/F\d+\s+9\.1\d*\s+Tf""").containsMatchIn(ops),
                    "Rasgos title must use genuine embedded 9.1pt Noto Sans Display heading font.")
                val page1 = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                assertTrue(page1.contains("RASGOS Y ATRIBUTOS"),
                    "Printed header text must remain readable.")
            }
        }
    }
    @Test
    fun g3RealP1AttacksHaveSubtleAlternatingBandsWithoutLosingPrintedAttackFields() {
        // G3 local check P111 / global G4 stays BLOCKED. Test real PDFBox
        // produced by the App service, not a renderer-source text assertion.
        for ((name, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources(name),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val content = pdf.getPage(0).contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                // Four light body bands inside the original P1 attacks panel,
                // not a giant color rectangle covering the source-backed text.
                val bands = Regex(
                    """(?:^|\s)313\s+(?:467|443|419|395)\s+256\s+12\s+re(?:\s|$)"""
                )
                assertEquals(4, bands.findAll(content).count(),
                    "P1 attack band geometry missing on REAL App PDF: $name $variant")
                val p1 = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                assertTrue(p1.contains("ARMA / ATAQUE / CONJURO"))
                assertTrue(p1.contains("BON."))
                assertTrue(p1.contains("DAÑO / TIPO"))
            }
        }
    }

    @Test
    fun g3RealP1VariantBSkillsHeaderHasApprovedBlueClippedCartelaWithoutMovingSkillRows() {
        // Golden V11 variant B: heading band belongs to the free strip
        // between last Attribute and fixed Skills rectangle. This test
        // parses genuine App-generated PDF operators, not source text only.
        for (fixture in listOf(
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json",
            "03_mara_siete_umbrales_custom_extended.json",
        )) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    PcSheetV11Variant.B,
                    PcSheetExportStateSelection.PERMANENT,
                ),
                sources(fixture),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val ops = pdf.getPage(0).contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                val clippedHeader = Regex(
                    """(?s)(?:^|\s)46\s+330\s+m\s+296\s+330\s+l\s+""" +
                        """300\s+334\s+l.*?42\s+334\s+l\s+h\s+B(?:\s|$)"""
                )
                assertEquals(1, clippedHeader.findAll(ops).count(),
                    "Golden C2c HABILIDADES header missing ($fixture).")
                assertTrue(Regex(
                    """(?:^|\s)42\s+105\s+258\s+223\s+re(?:\s|$)"""
                ).containsMatchIn(ops),
                    "Original B skills frame must not move.")
                val page1 = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                assertTrue(page1.contains("HABILIDADES"))
                // C329: the B legend prints the v8 marks with their meaning.
                assertTrue(page1.contains("\uE108") && page1.contains("PERICIA"))
                assertTrue(page1.contains("\uE109"))
                assertTrue(page1.contains("Acrobacias"))
            }
        }
    }

    @Test
    fun g3RealP1BothVariantsHaveGoldenAttributesClippedCartelaWithoutLosingScores() {
        // Golden V11 C2c: title belongs to the reserved strip between
        // portrait and first genuine attribute row. Inspects actual PDFBox
        // bytes created through the Desktop App's export service.
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val ops = pdf.getPage(0).contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                val titlePolygon = Regex(
                    """(?s)(?:^|\s)46\s+579\s+m\s+296\s+579\s+l\s+""" +
                        """300\s+583\s+l.*?42\s+583\s+l\s+h\s+B(?:\s|$)"""
                )
                assertEquals(1, titlePolygon.findAll(ops).count(),
                    "Genuine P1 missing 8-sided ATRIBUTOS cartela ($fixture $variant).")
                val p1 = PDFTextStripper().apply {
                    startPage = 1
                    endPage = 1
                }.getText(pdf)
                assertTrue(p1.contains("ATRIBUTOS"), "Approved header text missing.")
                assertTrue(p1.contains("FUERZA"), "Real first ability lost.")
                assertTrue(p1.contains("DESTREZA"), "Real second ability lost.")
                assertTrue(p1.contains("SALVACIÓN"), "Real saves must remain.")
            }
        }
    }

    @Test
    fun g3RealP1WithoutAmmunitionHasOnePaperEntryWithTwentyEmptyBoxes() {
        // Owner D-0076 §3.39: one logical manual row, no invented ammo
        // names or quantity. Ilyra has no persisted AMMUNITION usage.
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(
                PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT,
            ),
            sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json"),
        )
        Loader.loadPDF(proof.bytes).use { pdf ->
            val ops = pdf.getPage(0).contents.use {
                it.readBytes().toString(Charsets.ISO_8859_1)
            }
            // C257: two golden rows of ten 7.94pt squares (5 + 5 per row),
            // fully inside the original reserved P1 ammunition rectangle.
            val marks = Regex(
                """(?:^|\s)(4[1-9]\d(?:\.\d+)?|5[0-5]\d(?:\.\d+)?)\s+""" +
                    """(?:119|106)\s+7\.94\s+7\.94\s+re(?:\s|$)"""
            )
            assertEquals(20, marks.findAll(ops).count(),
                "Empty ammo must show 20 independent paper-only marks.")
            val columns = marks.findAll(ops).map { it.groupValues[1].toFloat() }.distinct().sorted().toList()
            assertEquals(10, columns.size, "Ten golden columns, shared by both rows.")
            columns.forEachIndexed { c, x ->
                assertTrue(kotlin.math.abs(x - (419.8f + c * 13.75f + (c / 5) * 2.83f)) < 0.05f,
                    "Golden mark column $c at $x.")
            }
            assertTrue(Regex(
                """(?:^|\s)312\s+102\s+258\s+46\s+re(?:\s|$)"""
            ).containsMatchIn(ops), "Original P1 ammo panel must not move.")
            val page1 = PDFTextStripper().apply {
                startPage = 1
                endPage = 1
            }.getText(pdf)
            assertTrue(Regex("MUNICIONES(?! A P\\.)(?! /)").containsMatchIn(page1), "Golden MUNICIONES title.")
            assertFalse(page1.contains("____________"),
                "Empty ammo name must be a real writable vector baseline, not fake printed underscores.")
            assertTrue(Regex(
                """(?:^|\s)316\s+120\s+m\s+410\s+120\s+l\s+S(?:\s|$)"""
            ).containsMatchIn(ops), "Exactly one measured blank ammo-name vector rule is required.")
            assertFalse(page1.contains("MARCAS: ____ / 20"),
                "Paper-only marks do not imply 20 units of ammo.")
        }
    }






    @Test
    fun g3C2EquipoAndTesoroOverflowKeepIndependentPhysicalNavigationInBothVariants() {
        // C206: two DIFFERENT persisted P2 data streams must not be chained as
        // a single C2 continuation just because their page headings share words.
        // Real Aldren backup + distinct stress IDs, currencies and quantities.
        val existing = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val equipmentTemplate = existing.permanent.sheet.inventoryItems.first { !it.special }
        val coinTemplate = existing.permanent.sheet.currencies.first()
        val extraEquipment = (1..82).map { n ->
            equipmentTemplate.copy(
                id = Uuid.parse("c2060001-0000-4000-8000-" +
                    n.toString().padStart(12, '0')),
                name = "EQUIPO-C206-" + n.toString().padStart(3, '0') +
                    " objeto de continuidad con denominacion autentica y extensa",
                quantity = 11000 + n,
                special = false,
                notes = null,
                sortOrder = 2000 + n,
            )
        }
        val extraCoins = (1..55).map { n ->
            coinTemplate.copy(
                key = "c206-moneda-" + n,
                name = "TESORO-C206-" + n.toString().padStart(3, '0') +
                    " denominacion persistida con leyenda mas extensa de lo normal",
                amount = if (n == 1) 0 else 21000 + n,
                sortOrder = 2500 + n,
                isDefault = false,
            )
        }
        val source = existing.copy(
            permanent = existing.permanent.copy(
                sheet = existing.permanent.sheet.copy(
                    inventoryItems = existing.permanent.sheet.inventoryItems + extraEquipment,
                    currencies = existing.permanent.sheet.currencies + extraCoins,
                ),
            ),
        )
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant,
                    PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = false),
                source,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-C206-both-paired-overflow-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply {
                        startPage = n
                        endPage = n
                    }.getText(pdf)
                }
                val equipmentPages = pages.indices.filter {
                    pages[it].contains("C2 - Equipo y tesoro / EQUIPO")
                }.map { it + 1 }
                val treasurePages = pages.indices.filter {
                    pages[it].contains("C2 - Equipo y tesoro / TESORO")
                }.map { it + 1 }
                assertTrue(equipmentPages.size >= 2,
                    "Stress ordinary equipment must span multiple physical C2 pages.")
                // C207: real C206 source creates a large EMPTY LOWER-RIGHT
                // pair on the SECOND equipment C2 page, while LEFT source
                // labels/amounts still run to the printable floor. That
                // reserved paper must carry a COMPLETE genuine NOTES grid,
                // not dozens of visually misleading empty EQUIPO rows.
                // C268: in the golden 18.4 pt rows the stress equipment spans
                // three pages; the LAST one holds the empty right pair.
                val secondEquipment = pdf.getPage(equipmentPages.last() - 1)
                val manuscript = PDFTextStripperByArea().apply {
                    // C211 R1: wrapped P2 rows shift where the right pair ends, so
                    // inspect the WHOLE right/left pair height below the header.
                    addRegion("unused-right-pair",
                        Rectangle2D.Float(312f, 120f, 258f, 588f))
                    addRegion("occupied-left-pair",
                        Rectangle2D.Float(42f, 120f, 258f, 588f))
                    extractRegions(secondEquipment)
                }
                assertTrue(manuscript.getTextForRegion("unused-right-pair")
                    .contains("NOTAS"),
                    "Real C2 unused right table cells require a paper NOTES cartela.")
                // C268: NOTES may sit BELOW the left equipment (bottom grid),
                // never among its rows: inspect the left pair down to its last row.
                var lastLeftRowTop = 120f
                object : PDFTextStripper() {
                    override fun writeString(
                        text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                    ) {
                        val first = textPositions.firstOrNull() ?: return
                        if (first.xDirAdj < 300f && text.contains("EQUIPO-C206")) {
                            lastLeftRowTop = maxOf(lastLeftRowTop, first.yDirAdj)
                        }
                    }
                }.apply {
                    startPage = equipmentPages.last(); endPage = equipmentPages.last()
                }.getText(pdf)
                val leftRows = PDFTextStripperByArea().apply {
                    addRegion("left-rows",
                        Rectangle2D.Float(42f, 120f, 258f, lastLeftRowTop - 120f + 4f))
                    extractRegions(secondEquipment)
                }.getTextForRegion("left-rows")
                assertTrue(leftRows.contains("EQUIPO-C206"))
                assertFalse(leftRows.contains("NOTAS"),
                    "Handwriting reserve must never replace LEFT persisted equipment.")
                val secondContent = secondEquipment.contents.use {
                    it.readBytes().toString(Charsets.ISO_8859_1)
                }
                // C268: with the bottom NOTES grid in use, the right grid starts
                // just above it; it still spans the whole free right pair.
                assertTrue(Regex(
                    """(?m)(?:^|\s)313(?:\.0)?\s+([\d.]+)\s+m\s+313(?:\.0)?\s+([\d.]+)\s+l"""
                ).findAll(secondContent).any { it.groupValues[2].toFloat() - it.groupValues[1].toFloat() > 150f },
                    "Right C2 NOTES grid must contain real full-height PDF vectors.")
                assertTrue(treasurePages.isNotEmpty(),
                    "The independent treasure stream must also overflow to C2.")
                assertTrue(equipmentPages.last() < treasurePages.first())
                assertTrue(pages[1].contains("CONTINÚA EN C2 PÁG. " +
                    equipmentPages.first()))
                assertTrue(pages[1].contains("TESORO A P. " +
                    treasurePages.first()),
                    "P2 must link independently to the physical TESORO start.")
                val p2Footer = PDFTextStripperByArea().apply {
                    addRegion("module-forward-links",
                        Rectangle2D.Float(42f, 734f, 528f, 16f)) // C290: golden band y43.94..56.98
                    extractRegions(pdf.getPage(1))
                }.getTextForRegion("module-forward-links")
                assertTrue(p2Footer.contains("EQUIPO A P. " + equipmentPages.first() +
                    "   ·   TESORO A P. " + treasurePages.first()),
                    "Every distinct P2 module needs a visible source-backed forward cue.")

                for (physical in equipmentPages) {
                    val footer = pages[physical - 1]
                    if (physical == equipmentPages.first()) {
                        assertTrue(footer.contains("VIENE DE P2 PÁG. 2"))
                        assertFalse(footer.contains("ANT. "))
                    }
                    if (physical == equipmentPages.last()) {
                        assertFalse(footer.contains("SIG. " + treasurePages.first()),
                            "Equipo cannot link to the next DIFFERENT table.")
                        assertFalse(footer.contains("SIG. "),
                            "Last Equipo page must not point to Tesoro.")
                    } else {
                        assertTrue(footer.contains("SIG. " + (physical + 1)))
                    }
                }
                val firstTreasure = pages[treasurePages.first() - 1]
                assertTrue(firstTreasure.contains("VIENE DE P2 PÁG. 2"),
                    "Independent Tesoro must link to its real P2 parent.")
                assertFalse(firstTreasure.contains("ANT. " + equipmentPages.last()),
                    "First Tesoro must not claim the last Equipo page as a parent.")
                assertFalse(firstTreasure.contains("ANT. "),
                    "Treasure begins a new module, not a continuation of equipment.")
                // C211 R1: names can now wrap on P2; compare whitespace-normalized text.
                val whole = pages.joinToString("\n").replace(Regex("\\s+"), " ")
                extraEquipment.forEach { item ->
                    assertEquals(1, Regex(Regex.escape(item.name.substring(0, 16)))
                        .findAll(whole).count(), "Missing/repeated equipment " + item.id)
                    assertEquals(1, Regex("""(?<!\d)""" + item.quantity +
                        """(?!\d)""").findAll(whole).count(),
                        "Equipment amount must not duplicate: " + item.id)
                }
                extraCoins.forEach { coin ->
                    assertEquals(1, Regex(Regex.escape(coin.name.substring(0, 16)))
                        .findAll(whole).count(), "Missing/repeated coin " + coin.key)
                    if (coin.amount != 0) {
                        assertEquals(1, Regex("""(?<!\d)""" + coin.amount +
                            """(?!\d)""").findAll(whole).count(),
                            "Currency amount must not duplicate: " + coin.key)
                    }
                }
                assertTrue(whole.contains("TESORO-C206-001"))
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" },
                    "The final page must remain handwritten-only.")
            }
        }
    }

    @Test
    fun g3OnlyP2TraitsInMixedC2NeverInventAP1Backlink() {
        // This case is outside the ordinary Aldren/Ilyra/Mara page distribution:
        // P1 sources fit on P1, while hundreds of real source-backed trait IDs
        // exhaust P1 and P2, forcing a P2-only mixed C2.
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val trait = original.permanent.sheet.traits.first()
        val syntheticTraits = (1..118).map { number ->
            trait.copy(
                id = Uuid.parse("ca270000-0000-4000-8000-" +
                    number.toString().padStart(12, '0')),
                name = "Rasgo de navegacion " + number.toString().padStart(3, '0'),
                source = "Fuente comprobable",
                description = "Texto digital fuera de Rasgos",
                notes = "Nota digital que no aparece en Rasgos",
                sortOrder = number,
            )
        }
        val base = original.permanent.sheet
        val adjusted = original.copy(permanent = original.permanent.copy(
            sheet = base.copy(
                traits = syntheticTraits,
                combatEntries = emptyList(),
                resources = emptyList(),
                inventoryItems = emptyList(),
                currencies = emptyList(),
            ),
            closure = original.permanent.closure.copy(inventoryUsage = emptyList()),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant,
                    PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = false),
                adjusted,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-P2-only-traits-C2-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { number ->
                    PDFTextStripper().apply {
                        startPage = number
                        endPage = number
                    }.getText(pdf)
                }
                val mixedPage = pages.indexOfFirst {
                    it.contains("C2 - Habilidades, acciones y recursos")
                } + 1
                assertTrue(mixedPage > 3, "P2-only trait overflow must reach C2.")
                assertFalse(pages[0].contains("CONTINÚA EN C2"),
                    "P1 must not advertise a C2 page containing ONLY P2 traits.")
                assertTrue(pages[1].contains("CONTINÚA EN C2 PÁG. " + mixedPage),
                    "P2 must link to the real first physical traits continuation.")
                assertTrue(pages[mixedPage - 1].contains("VIENE DE P2 PÁG. 2"),
                    "P2-only mixed C2 must identify its actual base page.")
                assertFalse(pages[mixedPage - 1].contains("VIENE DE P1"),
                    "P2-only mixed C2 must never falsely claim a P1 source.")
                assertTrue(pages.joinToString("\n").contains("Rasgo de navegacion 118"),
                    "The final original trait identity must survive P1/P2/C2.")
                val last = pages.last()
                assertTrue(last.lineSequence().any { it.trim() == "NOTAS" })
            }
        }
    }

    @Test
    fun g3C209RealMaraABPrintsC2BeforeOptionalQuickSpellsAsFrozenGolden() {
        // Frozen approved V11 extreme A/B and D-0076 §3.30 place
        // P1/P2 C2 continuations after the narrative base, before P4.
        // Prove the ACTUAL Desktop service's physical pages and P2 backlink,
        // not a source-only Kotlin role list or synthetic golden PDF.
        val source = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val request = PcSheetV11ExportRequest(
                variant, PcSheetExportStateSelection.PERMANENT,
            )
            val proof = DesktopPcSheetExportService().generateV11Proof(request, source)
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-C209-golden-order-" + variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { i ->
                    PDFTextStripper().apply {
                        startPage = i
                        endPage = i
                    }.getText(pdf)
                }
                val c2 = pages.indices.filter {
                    pages[it].contains("C2 - ") &&
                        !pages[it].contains("C-N3 - Notas digitales")
                }.map { it + 1 }
                val spells = pages.indices.filter {
                    pages[it].contains("P4 - Conjuros")
                }.map { it + 1 }
                assertTrue(c2.isNotEmpty() && spells.isNotEmpty(),
                    "The genuine Mara snapshot must exercise both C2 and P4.")
                assertEquals(4, c2.first(),
                    "C2 must immediately follow P1/P2/P3 in this scenario.")
                assertTrue(c2.last() < spells.first(),
                    "All real C2 overflow belongs before optional P4.")
                assertEquals(c2.last() + 1, spells.first(),
                    "No unrelated or fabricated page between C2 and P4.")
                // C259: P2 points to the first C2 page that really holds its traits.
                val p2TraitsPage = pages.indexOfFirst { it.contains("RASGOS / CONTINUACIÓN P2") } + 1
                assertTrue(p2TraitsPage in c2 &&
                    pages[1].contains("CONTINÚA EN C2 PÁG. " + p2TraitsPage),
                    "P2 must use the NEW final physical C2 destination.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(pages.last().contains("P4 - Conjuros") ||
                    pages.last().contains("C2 - "),
                    "Final manuscript page must remain separate from all digital content.")
            }
        }
    }

    @Test
    fun g3C210RealMaraABPreservesSourceGroupNameAtC2ColumnBreak() {
        // RED on prior genuine C209 A/B app PDFs: right-hand source text
        // starts with RECURSO but its true blue group cartela stayed left.
        // This is only a paper navigation label, never another source record.
        val source = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                ), source,
            )
            preserveRealApplicationPdf(proof.bytes,
                "V11-INTERNAL-PROOF-C210-source-group-continuation-" +
                    variant.name + ".pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { number ->
                    PDFTextStripper().apply {
                        startPage = number
                        endPage = number
                    }.getText(pdf)
                }
                // C259: Mara's golden resource boxes and action table take one
                // more real C2 page; C314: the golden note spacing gives her nine
                // long notes a second C-N3 page (11). The header itself adds none.
                assertEquals(11, pages.size,
                    "A source-only C2 header must not create or remove pages.")
                val c2Indices = pages.indices.filter {
                    pages[it].contains("C2 - Habilidades, acciones y recursos")
                }
                assertTrue(c2Indices.isNotEmpty(), "Mara's real mixed C2 was lost.")
                // C259: the continued resource group may now open a column on the
                // next C2 page; it must still carry its label in that column's
                // RESERVED header band, never as an anonymous paragraph.
                val reservedHeaders = c2Indices.flatMap { index ->
                    listOf(Rectangle2D.Float(40f, 73f, 262f, 34f),
                        Rectangle2D.Float(310f, 73f, 260f, 34f)).map { band ->
                        PDFTextStripperByArea().apply {
                            addRegion("band", band)
                            extractRegions(pdf.getPage(index))
                        }.getTextForRegion("band")
                    }
                }
                assertTrue(
                    reservedHeaders.any { it.contains("CONTINÚA: RECURSOS Y MUNICIONES") },
                    "A continued real resource group requires its label in " +
                        "the RESERVED right C2 header, not an anonymous paragraph.",
                )
                val c2Text = c2Indices.joinToString("\n") { pages[it] }
                assertTrue(c2Text.contains("Reserva 3"),
                    "Group context may not remove real Mara resource records.")
                assertEquals(1,
                    Regex(Regex.escape("CONTINÚA: RECURSOS Y MUNICIONES"))
                        .findAll(c2Text).count(),
                    "A physical C2 source-group header must not duplicate itself.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
            }
        }
    }

    @Test
    fun g3MixedC2ActualAldrenIlyraMaraNavigationKeepsTrueP1AndP2Origins() {
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant,
                    PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { physical ->
                    PDFTextStripper().apply {
                        startPage = physical
                        endPage = physical
                    }.getText(pdf)
                }
                val mixedPhysical = pages.indexOfFirst {
                    it.contains("C2 - Habilidades, acciones y recursos")
                } + 1
                assertTrue(mixedPhysical > 0)
                assertTrue(pages[0].contains("CONTINÚA EN C2 PÁG. " + mixedPhysical))
                assertTrue(pages[mixedPhysical - 1].contains("VIENE DE P1 PÁG. 1"))
                if (fixture.contains("mara")) {
                    // C259: P2 points to the first mixed C2 page with its traits.
                    val p2Physical = pages.indexOfFirst { it.contains("RASGOS / CONTINUACIÓN P2") } + 1
                    assertTrue(p2Physical >= mixedPhysical)
                    assertTrue(pages[1].contains("CONTINÚA EN C2 PÁG. " + p2Physical))
                    assertTrue(pages[p2Physical - 1].contains("P2 PÁG. 2"))
                    val ordinaryC2 = pages.indexOfFirst {
                        it.contains("C2 - Equipo y tesoro / EQUIPO")
                    } + 1
                    val specialC2 = pages.indexOfFirst {
                        it.contains("C2 - Equipo especial / continuación")
                    } + 1
                    assertTrue(ordinaryC2 > mixedPhysical && specialC2 > ordinaryC2)
                    assertTrue(pages[1].contains("EQUIPO A P. " + ordinaryC2),
                        "Mara P2 must lead to its separate ordinary-equipment C2.")
                    assertTrue(pages[1].contains("EQUIPO ESPECIAL A P. " + specialC2),
                        "Mara P2 must lead to its separate special-equipment C2.")

                }
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
            }
        }
    }

    @Test
    fun g3RealP2PairedTableHeadersIdentifyObjectAndDenominationWithoutMovingSourceCells() {
        // Golden V11 P2 reserves right-aligned legend in each clipped
        // blue header: Equipo's OBJETO/CANTIDAD, Tesoro's denomination/amount.
        // The previous genuine native PDF omitted OBJETO and DENOMINACIÓN.
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant,
                    PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                val p2 = PDFTextStripper().apply { startPage = 2; endPage = 2 }
                    .getText(pdf)
                assertTrue(p2.contains("OBJETO / CANTIDAD"),
                    "The real P2 object/quantity subcolumns must be identified.")
                assertTrue(p2.contains("denominación / cantidad"),
                    "The real P2 denomination/quantity subcolumns must be identified.")
                assertTrue(p2.contains("RASGOS Y ATRIBUTOS"))
                assertTrue(p2.contains("EQUIPO ESPECIAL"))
                val headerAreas = PDFTextStripperByArea().apply {
                    // C320: golden cartela legends (baselines y680.7 and y437.2).
                    addRegion("equipment", Rectangle2D.Float(39f, 100f, 260f, 20f))
                    addRegion("treasure", Rectangle2D.Float(39f, 344f, 260f, 20f))
                    extractRegions(pdf.getPage(1))
                }
                assertTrue(headerAreas.getTextForRegion("equipment")
                    .contains("OBJETO / CANTIDAD"))
                assertTrue(headerAreas.getTextForRegion("treasure")
                    .contains("denominación / cantidad"))
            }
        }
    }

    @Test
    fun g3RealP2EmptyEquipmentAndTreasureUseGoldenWritingRhythmWithoutSyntheticRows() {
        // Physical pre-fix PDFs: P2 both modules used 12.42pt line spacing.
        // Golden near-empty P2 has a wider ruled rhythm for actual pencil use.
        // This fixture represents true ABSENCE, not persisted zero currency.
        val source = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val missingBoth = source.copy(permanent = source.permanent.copy(
            sheet = source.permanent.sheet.copy(
                inventoryItems = emptyList(),
                currencies = emptyList(),
            ),
            closure = source.permanent.closure.copy(inventoryUsage = emptyList()),
        ))
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A,
                PcSheetExportStateSelection.PERMANENT),
            missingBoth,
        )
        preserveRealApplicationPdf(proof.bytes,
            "V11-INTERNAL-PROOF-P2-empty-equipment-treasure-A.pdf")
        Loader.loadPDF(proof.bytes).use { pdf ->
            val p2 = PDFTextStripper().apply { startPage = 2; endPage = 2 }
                .getText(pdf)
            assertTrue(p2.contains("EQUIPO") && p2.contains("TESORO") &&
                p2.contains("RASGOS Y ATRIBUTOS") &&
                p2.contains("EQUIPO ESPECIAL"))
            assertFalse(p2.contains("Cobre"),
                "An absent currency must not be invented as a zero balance.")
            assertFalse(p2.contains("Ballesta"),
                "Absent equipment must not be invented in paper writing rows.")
            val ops = pdf.getPage(1).contents.use {
                it.readBytes().toString(Charsets.ISO_8859_1)
            }
            // Look ONLY at the PDFBox full-width ruled lines of each left P2
            // table. Underlying digital labels are unaffected by this test.
            // C320: golden P2 tables x39.69..298.49; EQUIPO 460.63..670.68, TESORO 327.97..427.18.
            val rules = Regex(
                """(?:^|\s)39\.69\d*\s+([0-9]+(?:\.[0-9]+)?)\s+m\s+298\.4\d*\s+\1\s+l"""
            ).findAll(ops).map { it.groupValues[1].toFloat() }.toList()
            val equipment = rules.filter { it > 460.63f && it < 670.68f }.sortedDescending()
            val treasure = rules.filter { it > 327.97f && it < 427.18f }.sortedDescending()
            assertEquals(12, equipment.size,
                "Empty Equipo must reserve twelve generously spaced interior rules.")
            // C320: the golden TESORO is five 19.84 pt rows, i.e. four interior rules.
            assertEquals(4, treasure.size,
                "Empty Tesoro must reserve the golden five pencil rows (four interior rules).")
            equipment.zipWithNext { before, after ->
                assertTrue(kotlin.math.abs(before - after - 16.15f) < 0.02f,
                    "Equipo handwritten rhythm must be the golden 16.16 pt (C320).")
            }
            treasure.zipWithNext { before, after ->
                assertTrue(kotlin.math.abs(before - after - 19.84f) < 0.02f,
                    "Tesoro handwritten rhythm must be the golden 19.84 pt (C320).")
            }
            assertTrue(pdf.numberOfPages >= 4)
            val last = PDFTextStripper().apply {
                startPage = pdf.numberOfPages
                endPage = pdf.numberOfPages
            }.getText(pdf)
            assertTrue(last.contains("NOTAS"),
                "The physical final manuscript sheet must remain present.")
        }
    }


    @Test
    fun g3RealExtremeEquipmentAndTreasureAmountsMoveWholeToC2ForBothVariants() {
        // Preserve a genuine Mara backup; append only two discriminating stress rows.
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        val baseline = original.permanent.sheet
        val template = baseline.inventoryItems.first { !it.special }
        val extraItem = template.copy(
            id = Uuid.parse("40000000-0000-4000-8000-000000000043"),
            name = "EQUIPO_QA_CANTIDAD_COMPLETA",
            quantity = Int.MAX_VALUE,
            sortOrder = (baseline.inventoryItems.maxOfOrNull { it.sortOrder } ?: 0) + 1,
            special = false,
            weightLb = null,
        )
        val extraCoin = io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency(
            key = "qa-wide-amount",
            name = "TESORO_QA_CANTIDAD_COMPLETA",
            amount = 1987654321,
            sortOrder = (baseline.currencies.maxOfOrNull { it.sortOrder } ?: 0) + 1,
            isDefault = false,
        )
        val stressed = original.copy(permanent = original.permanent.copy(
            sheet = baseline.copy(
                inventoryItems = baseline.inventoryItems + extraItem,
                currencies = baseline.currencies + extraCoin,
            ),
        ))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val pdfBytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                stressed,
            ).bytes
            preserveRealApplicationPdf(
                pdfBytes, "V11-INTERNAL-PROOF-P2-C2-large-amounts-" + variant + ".pdf",
            )
            Loader.loadPDF(pdfBytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply {
                        startPage = page
                        endPage = page
                    }.getText(pdf)
                }
                val entire = pages.joinToString("\n")
                val equipment = "2147483647"
                val treasure = "1987654321"
                assertFalse(pages[1].contains(equipment) || pages[1].contains(treasure),
                    "P2 cannot print source numbers outside its fixed 36pt amount cells.")
                assertEquals(1, Regex(Regex.escape(equipment)).findAll(entire).count())
                assertEquals(1, Regex(Regex.escape(treasure)).findAll(entire).count())
                val equipmentC2 = pages.filter { it.contains("C2 - Equipo y tesoro / EQUIPO") }
                val treasureC2 = pages.filter { it.contains("C2 - Equipo y tesoro / TESORO") }
                assertTrue(equipmentC2.any { it.contains("EQUIPO_QA_CANTIDAD_COMPLETA") &&
                    it.contains(equipment) }, "An intact equipment amount needs its own C2 pair.")
                assertTrue(treasureC2.any { it.contains("TESORO_QA_CANTIDAD_COMPLETA") &&
                    it.contains(treasure) }, "An intact treasure amount needs its own C2 pair.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                assertFalse(pages.last().contains(equipment) || pages.last().contains(treasure))
            }
        }
    }

    @Test
    fun g3RealP2CustomCurrenciesWithPhysicalBreaksPreserveZeroOrderAndWholeC2AmountsAB() {
        // A different approved source family than ammunition: real CUSTOM
        // treasure denominations must still use the frozen P2 pair grammar.
        // The first multiline denomination ends the contiguous P2 prefix;
        // subsequent short currency rows stay in C2 in original source order.
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val template = original.permanent.sheet.currencies.first()
        val currencies = listOf(
            template.copy(
                key = "qa-currency-zero", name = "QACOINZERO", amount = 0,
                sortOrder = 1, isDefault = false,
            ),
            template.copy(
                key = "qa-currency-line",
                name = "QACOINLFSTART\r\nQACOINLFEND",
                amount = 37, sortOrder = 2, isDefault = false,
            ),
            template.copy(
                key = "qa-currency-tab",
                name = "QACOINTABSTART\tQACOINTABEND",
                amount = 41, sortOrder = 3, isDefault = false,
            ),
            template.copy(
                key = "qa-currency-tail", name = "QACOINSHORTTAIL",
                amount = 73, sortOrder = 4, isDefault = false,
            ),
        )
        val stressed = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(currencies = currencies),
        ))
        val expected = currencies.toList()
        val deferred = listOf("QACOINLFSTART", "QACOINLFEND",
            "QACOINTABSTART", "QACOINTABEND", "QACOINSHORTTAIL")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val pdfBytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(
                    variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = false,
                ), stressed,
            ).bytes
            assertEquals(expected, stressed.permanent.sheet.currencies,
                "Printing must never rewrite persisted custom currency labels or amounts.")
            preserveRealApplicationPdf(pdfBytes,
                "V11-INTERNAL-PROOF-P2-currency-source-break-" + variant.name + ".pdf")
            Loader.loadPDF(pdfBytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply {
                        startPage = page
                        endPage = page
                    }.getText(pdf)
                }
                val p2 = pages[1]
                // C211 R1: Aldren ordinary objects now fit P2 (wrapped), so only the
                // deferred currencies reach C2, on their own TESORO page.
                val combined = pages.filter {
                    it.contains("C2 - Equipo y tesoro / TESORO")
                }
                assertEquals(1, combined.size,
                    "The deferred currencies use one physical C2 page.")
                assertTrue(combined.single().contains("TESORO / CANTIDAD"))
                assertFalse(pages.any { it.contains("C2 - Equipo y tesoro / EQUIPO") },
                    "No ordinary object is left for C2.")
                assertTrue(combined.single().contains("VIENE DE P2 PÁG. 2"))
                val c2 = combined.joinToString("\n")
                val full = pages.joinToString("\n")
                assertTrue(p2.contains("QACOINZERO"),
                    "Actual saved zero-denomination row must remain in P2.")
                assertFalse(c2.contains("QACOINZERO"),
                    "Already-printed P2 currency must not appear again in C2.")
                deferred.forEach { label ->
                    assertFalse(p2.contains(label),
                        "Single-row P2 cannot print deferred source label " + label)
                    assertTrue(c2.contains(label),
                        "Complete currency label must remain visible in C2: " + label)
                    assertEquals(1, Regex(Regex.escape(label)).findAll(full).count(),
                        "Currency source segment printed more than once: " + label)
                }
                assertTrue(c2.indexOf("QACOINLFSTART") < c2.indexOf("QACOINTABSTART"))
                assertTrue(c2.indexOf("QACOINTABSTART") < c2.indexOf("QACOINSHORTTAIL"))
                for (amount in listOf(37, 41, 73)) {
                    assertEquals(1, Regex("""(?<!\d)""" + amount + """(?!\d)""")
                        .findAll(full).count(),
                        "Saved custom currency quantity must print exactly once: " + amount)
                }
                assertTrue(combined.single().lineSequence().any {
                    it.trim() == "NOTAS"
                }, "Unused combined C2 must remain useful manuscript paper.")
                assertEquals(7, pages.size,
                    "Compacting two actually short C2 modules must save exactly one page.")
                assertTrue(pages.last().lineSequence().any { it.trim() == "NOTAS" })
                deferred.forEach { label ->
                    assertFalse(pages.last().contains(label),
                        "Digital currency must not migrate into manuscript NOTAS.")
                }
            }
        }
    }

    @Test
    fun g3RealP2RecordedZeroCurrencyIsNotTreatedAsAbsentPaperEntry() {
        // Persisted zero IS a real value; no default currency may be generated.
        val source = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val onlyZeroCopper = source.copy(permanent = source.permanent.copy(
            sheet = source.permanent.sheet.copy(
                currencies = source.permanent.sheet.currencies.filter { it.key == "cp" }
                    .map { it.copy(amount = 0) },
            ),
        ))
        val bytes = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B,
                PcSheetExportStateSelection.PERMANENT),
            onlyZeroCopper,
        ).bytes
        Loader.loadPDF(bytes).use { pdf ->
            val p2 = PDFTextStripper().apply { startPage = 2; endPage = 2 }
                .getText(pdf)
            assertTrue(p2.contains("Cobre"))
            assertFalse(p2.contains("Electrum"))
            assertFalse(p2.contains("Platino"))
            val rowRegion = PDFTextStripperByArea().apply {
                // C320: golden TESORO first row (top y364.8).
                addRegion("recorded-zero", Rectangle2D.Float(39f, 362f, 135f, 22f))
                extractRegions(pdf.getPage(1))
            }.getTextForRegion("recorded-zero")
            assertTrue(rowRegion.contains("Cobre") && rowRegion.contains("0"),
                "Persisted currency amount 0 must print in its authentic P2 row.")
        }
    }

    @Test
    fun g3RealP2NoSpecialEquipmentHasRecognizedLocationsFreeRowsAndBlankActivoBoxes() {
        // P210: no persisted special items, as distinct from deferred C2 items.
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val empty = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(
                inventoryItems = original.permanent.sheet.inventoryItems.filterNot { it.special },
            ),
        ))
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A,
                PcSheetExportStateSelection.PERMANENT),
            empty,
        )
        preserveRealApplicationPdf(proof.bytes, "V11-INTERNAL-PROOF-Aldren-no-special-A.pdf")
        Loader.loadPDF(proof.bytes).use { pdf ->
            val p2 = PDFTextStripper().apply { startPage = 2; endPage = 2 }
                .getText(pdf)
            for (location in listOf("Cuello", "Cuerpo", "Mano izquierda", "Mano derecha")) {
                assertTrue(p2.contains(location),
                    "Missing source-recognized paper location: " + location)
            }
            assertFalse(p2.contains("Cota de malla"),
                "Do not invent previously removed special objects.")
            val ops = pdf.getPage(1).contents.use {
                it.readBytes().toString(Charsets.ISO_8859_1)
            }
            // C320: golden ESPECIAL table from y291.4 moves the eight rows down 21.6 pt.
            val activoRows = listOf(256.4f, 231.4f, 206.4f, 181.4f, 156.4f, 131.4f, 106.4f, 81.4f)
            val paperMarks = Regex("""(?:^|\s)542\.5\s+([0-9.]+)\s+9(?:\.0+)?\s+9(?:\.0+)?\s+re(?:\s|$)""")
                .findAll(ops).map { it.groupValues[1].toFloat() }
                .count { y -> activoRows.any { kotlin.math.abs(it - y) < 0.02f } }
            assertEquals(8, paperMarks,
                "Eight genuine 9pt ACTIVO squares must be printed in P2.")
            assertTrue(p2.contains("UBICACIÓN") && p2.contains("NOMBRE") &&
                p2.contains("DESCRIPCIÓN") && p2.contains("ACTIVO"))
            assertTrue(pdf.numberOfPages >= 4)
            val lastText = PDFTextStripper().apply {
                startPage = pdf.numberOfPages; endPage = pdf.numberOfPages
            }.getText(pdf)
            assertTrue(lastText.contains("NOTAS"),
                "P210 must not remove the final manuscript-only sheet.")
        }
    }

}
