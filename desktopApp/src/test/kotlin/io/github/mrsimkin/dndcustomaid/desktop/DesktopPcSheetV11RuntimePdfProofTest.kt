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
        // Must be genuine VECTOR squares in the P1 left saving band, not plain
        // "[ ]" glyphs or unrelated marks. P104: the 8.78 pt box at x225.84, just
        // left of the saving-throw handwriting line (x238.62..256.62).
        val boxes = Regex("""(?m)(?:^|\s)225\.8[34]\d*\s+[0-9.]+\s+8\.78\d*\s+8\.78\d*\s+re(?:\s|$)""")
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
    fun c360P1FollowsTheGoldenVerticalRhythmInBothVariants() {
        // C356-C359: the golden v11_extremo P1 rhythm on real App PDFs - every
        // cartela foot, the first attribute row's score box, oval and saving box,
        // and B's HABILIDADES cartela 3.12 pt under its last attribute row.
        val pathStart = Regex("""(?m)(?:^|\s)(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)\s+m(?:\s|$)""")
        val rect = Regex("""(?m)(?:^|\s)(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)\s+re(?:\s|$)""")
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            ).bytes
            Loader.loadPDF(bytes).use { pdf ->
                val ops = pdf.getPage(0).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                val starts = pathStart.findAll(ops).map { it.groupValues[1].toFloat() to it.groupValues[2].toFloat() }.toList()
                val rects = rect.findAll(ops).map { m -> m.groupValues.drop(1).map { it.toFloat() } }.toList()
                fun startsAt(x: Float, y: Float) = starts.any { (sx, sy) ->
                    kotlin.math.abs(sx - x) < 0.02f && kotlin.math.abs(sy - y) < 0.02f }
                // Golden cartela feet (chamfered paths start 5 pt in from the left edge).
                assertTrue(startsAt(44.69f, 588.19f), "ATRIBUTOS cartela at y588.19 ($fixture $variant).")
                for (foot in listOf(588.19f, 462.9f, 348.94f, 219.68f, 106.3f)) {
                    assertTrue(startsAt(275.43f, foot), "Right column cartela at y$foot ($fixture $variant).")
                }
                // First attribute row from y586.2: score box (3 pt chamfer), oval, saving box.
                // P104: the first row's box, oval and save box centred on the band's middle line.
                val centre = 586.2f - (if (variant == PcSheetV11Variant.A) 22.82f else 25.09f) / 2f
                assertTrue(startsAt(45.24f, centre - 9.07f), "Score box of the first attribute ($fixture $variant).")
                assertTrue(startsAt(111.9f, centre), "Modifier oval of the first attribute ($fixture $variant).")
                assertTrue(rects.any { (x, y, w, h) -> kotlin.math.abs(x - 225.84f) < 0.02f &&
                    kotlin.math.abs(y - (centre - 4.39f)) < 0.02f && kotlin.math.abs(w - 8.78f) < 0.02f &&
                    kotlin.math.abs(h - 8.78f) < 0.02f }, "Golden saving box of the first attribute ($fixture $variant).")
                val leftCartelaFeet = starts.filter { (sx, sy) -> kotlin.math.abs(sx - 44.69f) < 0.02f && sy < 615f /* not the portrait */ }
                    .map { it.second }.distinct()
                if (variant == PcSheetV11Variant.B) {
                    val p1Text = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
                    val rows = Regex("""(?<!CD )SALVACIÓN""").findAll(p1Text).count() // not the CD SALVACIÓN card
                    val expectedFoot = 586.2f - rows * 25.09f - 3.12f - 22.96f
                    assertTrue(leftCartelaFeet.any { kotlin.math.abs(it - expectedFoot) < 0.05f },
                        "B HABILIDADES 3.12 pt under its $rows golden 25.09 pt rows ($fixture): $leftCartelaFeet")
                    assertTrue(rects.count { (x, _, w, h) -> kotlin.math.abs(x - 39.69f) < 0.02f &&
                        kotlin.math.abs(w - 108.56f) < 0.02f && kotlin.math.abs(h - 14.74f) < 0.02f } >= 9,
                        "B golden 14.74 pt skill rows ($fixture).")
                } else {
                    assertEquals(1, leftCartelaFeet.size, "A has a single left cartela ($fixture).")
                    assertTrue(rects.any { (x, _, w, h) -> kotlin.math.abs(x - 39.69f) < 0.02f &&
                        kotlin.math.abs(w - 220.53f) < 0.02f && kotlin.math.abs(h - 12.47f) < 0.02f },
                        "A golden 12.47 pt skill rows ($fixture).")
                }
            }
        }
    }

    @Test
    fun c368SameInputGivesTheSamePagesBreaksAndTextAB() {
        // C368 / GLB11: the same sources and options give the same physical pages,
        // the same page breaks and the same printed text, page by page.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        val ilyra = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            for ((label, source, book) in listOf(Triple("Mara", mara, false), Triple("Ilyra", ilyra, true))) {
                fun pagesOf(): List<String> {
                    val bytes = DesktopPcSheetExportService().generateV11Proof(
                        PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT,
                            includeSpellDescriptions = book),
                        source,
                    ).bytes
                    return Loader.loadPDF(bytes).use { pdf ->
                        (1..pdf.numberOfPages).map { n ->
                            PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                        }
                    }
                }
                val first = pagesOf()
                assertTrue(first.size >= 5, "$label $variant prints a multi-page sheet.")
                repeat(2) {
                    val again = pagesOf()
                    assertEquals(first.size, again.size, "Same page count ($label $variant).")
                    first.indices.forEach { i ->
                        assertEquals(first[i], again[i], "Same text on page ${i + 1} ($label $variant).")
                    }
                }
            }
        }
    }

    @Test
    fun c373EveryPageKeepsItsWordsInsideThePrintableAreaWithoutOverlapsAB() {
        // C373 / GLB06: on every page of real App PDFs, every printed word lies
        // inside the printable text area (x34..578, y30..767 from the top) and no
        // two words substantively overlap (same rule as the independent audit).
        data class Word(val text: String, val x0: Float, val y0: Float, val x1: Float, val y1: Float)
        val fixtures = listOf(
            Triple("01_aldren_vale_srd5_1_champion_fighter.json", PcSheetV11Variant.A, false),
            Triple("02_ilyra_quill_srd5_2_1_evoker_wizard.json", PcSheetV11Variant.B, true),
            Triple("03_mara_siete_umbrales_custom_extended.json", PcSheetV11Variant.A, false),
            Triple("03_mara_siete_umbrales_custom_extended.json", PcSheetV11Variant.B, true),
        )
        for ((fixture, variant, book) in fixtures) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = book),
                sources(fixture),
            ).bytes
            Loader.loadPDF(bytes).use { pdf ->
                var checked = 0
                for (page in 1..pdf.numberOfPages) {
                    val words = mutableListOf<Word>()
                    object : PDFTextStripper() {
                        override fun writeString(
                            text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                        ) {
                            var current = mutableListOf<org.apache.pdfbox.text.TextPosition>()
                            fun flush() {
                                if (current.isNotEmpty()) {
                                    words += Word(current.joinToString("") { it.unicode },
                                        current.minOf { it.xDirAdj }, current.minOf { it.yDirAdj - it.heightDir },
                                        current.maxOf { it.xDirAdj + it.widthDirAdj }, current.maxOf { it.yDirAdj })
                                }
                                current = mutableListOf()
                            }
                            // A word ends at a blank or where the next glyph jumps away
                            // (backwards, or more than 1.5 pt after the previous glyph).
                            textPositions.forEach { g ->
                                val last = current.lastOrNull()
                                if (g.unicode.isBlank()) flush()
                                else {
                                    if (last != null && (g.xDirAdj < last.xDirAdj ||
                                            g.xDirAdj - (last.xDirAdj + last.widthDirAdj) > 1.5f ||
                                            kotlin.math.abs(g.yDirAdj - last.yDirAdj) > 1f)) flush()
                                    current += g
                                }
                            }
                            flush()
                        }
                    }.apply { startPage = page; endPage = page }.getText(pdf)
                    words.forEach { w ->
                        assertTrue(w.x0 >= 33.99f && w.x1 <= 578.01f && w.y0 >= 29.99f && w.y1 <= 767.01f,
                            "«${w.text}» lies outside the printable area on page $page ($fixture $variant): $w")
                    }
                    val sorted = words.sortedBy { it.y0 }
                    sorted.forEachIndexed { i, a ->
                        for (b in sorted.drop(i + 1)) {
                            if (b.y0 >= a.y1 - 2f) break
                            val dx = minOf(a.x1, b.x1) - maxOf(a.x0, b.x0)
                            val dy = minOf(a.y1, b.y1) - maxOf(a.y0, b.y0)
                            val smaller = minOf((a.x1 - a.x0) * (a.y1 - a.y0), (b.x1 - b.x0) * (b.y1 - b.y0))
                            assertFalse(dx > 2f && dy > 3f && smaller > 0f && dx * dy / smaller > 0.35f,
                                "«${a.text}» and «${b.text}» overlap on page $page ($fixture $variant).")
                        }
                    }
                    checked += words.size
                }
                assertTrue(checked > 500, "The check read real words ($fixture $variant): $checked")
            }
        }
    }

    @Test
    fun c374VariantASkillsSitUnderTheirOwnAttributeWithoutSuffix() {
        // C374 / P106: in variant A every skill printed in P1's left column sits
        // directly under ITS attribute (from the source reference, not the drawing)
        // with no other attribute row in between, and carries no «(XXX)» suffix.
        val printedName = mapOf("STRENGTH" to "FUERZA", "DEXTERITY" to "DESTREZA",
            "CONSTITUTION" to "CONSTITUCIÓN", "INTELLIGENCE" to "INTELIGENCIA",
            "WISDOM" to "SABIDURÍA", "CHARISMA" to "CARISMA")
        for (fixture in listOf("01_aldren_vale_srd5_1_champion_fighter.json",
            "03_mara_siete_umbrales_custom_extended.json")) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            )
            val semantic = generated.semantic
            Loader.loadPDF(generated.bytes).use { pdf ->
                val runs = mutableListOf<Pair<String, Float>>()
                object : PDFTextStripper() {
                    override fun writeString(
                        text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                    ) {
                        val first = textPositions.firstOrNull() ?: return
                        if (first.xDirAdj < 262f && first.yDirAdj > 205f) runs += // v8 state glyphs are not text
                            text.filterNot { it in '\uE000'..'\uF8FF' }.trim() to first.yDirAdj
                    }
                }.apply { startPage = 1; endPage = 1 }.getText(pdf)
                fun rowOf(attribute: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Attribute): Float? {
                    val name = printedName[attribute.name] ?: attribute.name.uppercase()
                    val word = Regex("""(^|\s)""" + Regex.escape(name) + """(\s|$)""")
                    return runs.firstOrNull { word.containsMatchIn(it.first) }?.second
                }
                val attributeRows = semantic.attributes.mapNotNull { a -> rowOf(a)?.let { a to it } }
                assertTrue(attributeRows.size >= 6, "The six standard attributes print on P1 ($fixture).")
                var checked = 0
                for (skill in semantic.skills) {
                    // The sheet prints built-in skills with the App's Spanish label.
                    val label = skill.builtInKey?.let {
                        io.github.mrsimkin.dndcustomaid.shared.character.characterSkillSpanishLabel(it)
                    } ?: skill.name
                    val run = runs.firstOrNull { (text, _) ->
                        text == label || text.startsWith("$label ") || text.startsWith("$label+") ||
                            text.startsWith("$label-") }
                    assertFalse(runs.any { it.first.startsWith("$label (") },
                        "A skill under its attribute carries no «(XXX)» suffix ($fixture): $label")
                    if (run == null) continue
                    val owner = attributeRows.filter { it.second < run.second }.maxByOrNull { it.second }
                    assertEquals(skill.ability, owner?.first?.reference,
                        "«${skill.name}» sits directly under its own attribute ($fixture).")
                    checked++
                }
                assertTrue(checked >= 15, "Most skills are checked on P1 ($fixture): $checked")
            }
        }
    }

    @Test
    fun c375P1AttacksStayASummaryAndTheirNotesContinueWholeInC2AB() {
        // C375 / P119: the P1 ATAQUES box is a summary - at most its five data rows,
        // never an attack's notes - and every note still prints whole in C2.
        fun flat(text: String) = text.replace(Regex("""\s+"""), " ").trim()
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources("03_mara_siete_umbrales_custom_extended.json"),
            )
            val noted = generated.semantic.combatEntries.filter { (it.notes ?: "").trim().length >= 20 }
            assertTrue(noted.size >= 3, "Mara has real attacks with long notes ($variant).")
            Loader.loadPDF(generated.bytes).use { pdf ->
                // Golden ATAQUES rows: under the header (y448.44 from the bottom) and
                // above the RASGOS cartela top (y371.9): top-origin y343.56..420.1.
                val box = PDFTextStripperByArea().apply {
                    addRegion("attacks", java.awt.geom.Rectangle2D.Float(270f, 343.6f, 302f, 76.5f))
                    extractRegions(pdf.getPage(0))
                }.getTextForRegion("attacks")
                val rows = box.lines().filter { it.isNotBlank() }
                assertTrue(rows.size in 1..5, "P1 ATAQUES prints at most its five rows ($variant): $rows")
                val p1Box = flat(box)
                val c2 = flat((2..pdf.numberOfPages).joinToString(" ") { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                })
                noted.forEach { entry ->
                    val note = flat(entry.notes!!)
                    assertFalse(p1Box.contains(note.take(20)),
                        "P1 ATAQUES never prints an attack's notes ($variant): ${entry.name}")
                    assertTrue(c2.contains(note.take(40)),
                        "The notes of ${entry.name} continue in C2 ($variant).")
                }
            }
        }
    }

    @Test
    fun c376P1SkillRowsCarryTheirRealTrainingMarkAndTotalIncludingCustomSkills() {
        // C376 / P108: each skill row printed on P1 A - custom skills included -
        // carries exactly the v8 mark of its real training (U+E108 proficiency,
        // U+E109 expertise, none otherwise) and its real signed total.
        var customChecked = 0
        for (fixture in listOf("01_aldren_vale_srd5_1_champion_fighter.json",
            "03_mara_siete_umbrales_custom_extended.json")) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT),
                sources(fixture),
            )
            Loader.loadPDF(generated.bytes).use { pdf ->
                // Left-column text joined per printed line (baselines within 1 pt).
                val pieces = mutableListOf<Pair<Float, String>>()
                object : PDFTextStripper() {
                    override fun writeString(
                        text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                    ) {
                        textPositions.filter { it.xDirAdj < 262f && it.yDirAdj > 205f }
                            .forEach { pieces += it.yDirAdj to it.unicode }
                    }
                }.apply { startPage = 1; endPage = 1 }.getText(pdf)
                // State marks are drawn centred in their box, off the text baseline.
                val isMark = { s: String -> s.any { it in '\uE000'..'\uF8FF' } }
                val marksAt = pieces.filter { isMark(it.second) }
                val lines = pieces.filterNot { isMark(it.second) }.groupBy { kotlin.math.round(it.first) }
                    .map { (y, group) -> y to group.joinToString("") { it.second } }
                var checked = 0
                for (skill in generated.semantic.skills) {
                    val label = skill.builtInKey?.let {
                        io.github.mrsimkin.dndcustomaid.shared.character.characterSkillSpanishLabel(it)
                    } ?: skill.name
                    val (lineY, line) = lines.firstOrNull { (_, text) ->
                        text.replace(" ", "").startsWith(label.replace(" ", "")) } ?: continue
                    val expected = when (skill.training) {
                        io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.PROFICIENT -> '\uE108'
                        io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.EXPERTISE -> '\uE109'
                        else -> null
                    }
                    val marks = marksAt.filter { kotlin.math.abs(it.first - lineY) < 5f }
                        .joinToString("") { it.second }.filter { it == '\uE108' || it == '\uE109' }
                    assertEquals(expected?.toString() ?: "", marks,
                        "«$label» prints the mark of its real training ($fixture): ${skill.training}")
                    skill.total?.let { total ->
                        val signed = if (total >= 0) "+$total" else total.toString()
                        assertTrue(line.replace(" ", "").endsWith(signed),
                            "«$label» prints its real total $signed ($fixture): $line")
                    }
                    if (skill.customSkillId != null) customChecked++
                    checked++
                }
                assertTrue(checked >= 15, "Most P1 skill rows are checked ($fixture): $checked")
            }
        }
        assertTrue(customChecked >= 2, "Mara's custom skills are among the checked rows: $customChecked")
    }

    @Test
    fun c377EveryAttributePrintsOnceInSourceOrderWithItsRealValuesAB() {
        // C377 / P103: the six standard attributes first and in order, then the
        // custom ones in source order; every attribute prints exactly once in the
        // whole PDF (P1 or C2) as «score modifier name» with its real values.
        val printedName = mapOf("STRENGTH" to "FUERZA", "DEXTERITY" to "DESTREZA",
            "CONSTITUTION" to "CONSTITUCIÓN", "INTELLIGENCE" to "INTELIGENCIA",
            "WISDOM" to "SABIDURÍA", "CHARISMA" to "CARISMA")
        fun signed(n: Int) = if (n >= 0) "+$n" else n.toString()
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), sources(fixture),
            )
            val attributes = generated.semantic.attributes
            assertEquals(listOf("STRENGTH", "DEXTERITY", "CONSTITUTION", "INTELLIGENCE", "WISDOM", "CHARISMA"),
                attributes.take(6).map { it.name }, "Six standard attributes lead the source ($fixture).")
            Loader.loadPDF(generated.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                        .replace(Regex("""\s+"""), " ")
                }
                val p1Order = mutableListOf<Pair<Int, Int>>()
                attributes.forEachIndexed { index, a ->
                    val name = printedName[a.name] ?: a.name.uppercase()
                    val row = Regex(Regex.escape(a.score.toString()) + " " + Regex.escape(signed(a.modifier)) +
                        " " + Regex.escape(name) + """(?=\s|$)""")
                    val hits = pages.mapIndexed { page, text -> page to row.findAll(text).count() }
                    assertEquals(1, hits.sumOf { it.second },
                        "«${a.score} ${signed(a.modifier)} $name» prints exactly once ($fixture $variant).")
                    if (hits.first().second == 1) p1Order += index to (row.find(pages[0])!!.range.first)
                }
                assertTrue(p1Order.size >= 6, "At least the six standard attributes print on P1 ($fixture).")
                assertEquals(p1Order.map { it.first }.sorted(), p1Order.sortedBy { it.second }.map { it.first },
                    "P1 prints its attributes in source order ($fixture $variant).")
                assertEquals((0 until p1Order.size).toList(), p1Order.map { it.first }.sorted(),
                    "P1 holds a leading run of the source attributes; the rest continue in C2 ($fixture $variant).")
            }
        }
    }

    @Test
    fun c378P2OrdinaryEquipmentPrintsOnlyNameAndQuantityAndNoGenericOtrosBoxAB() {
        // C378 / P202 + P212: the P2 EQUIPO table (left half, above EQUIPO ESPECIAL)
        // prints each ordinary item with its real quantity and never its description
        // or notes; no page opens a generic «OTROS» box.
        fun flat(text: String) = text.replace(Regex("""\s+"""), " ").trim()
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.B,
        )) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), sources(fixture),
            )
            Loader.loadPDF(generated.bytes).use { pdf ->
                // The P2 EQUIPO table has two subcolumns (name + right-aligned quantity),
                // split at x168; each is read on its own, in reading order.
                val halves = PDFTextStripperByArea().apply {
                    sortByPosition = true // quantities are drawn in a later pass than the names
                    addRegion("left", java.awt.geom.Rectangle2D.Float(39f, 100f, 129f, 400f))
                    addRegion("right", java.awt.geom.Rectangle2D.Float(168f, 100f, 134f, 400f))
                    extractRegions(pdf.getPage(1))
                }.let { listOf(flat(it.getTextForRegion("left")), flat(it.getTextForRegion("right"))) }
                val table = halves.joinToString(" ")
                // The name's words in order; a quantity may sit between them (wrapped name).
                fun quantityOf(tokens: List<String>, words: List<String>): String? {
                    for (start in tokens.indices) {
                        if (tokens[start] != words[0]) continue
                        var i = start
                        var w = 0
                        val skipped = mutableListOf<String>()
                        while (i < tokens.size && w < words.size) {
                            when {
                                tokens[i] == words[w] -> w++
                                tokens[i].all(Char::isDigit) && skipped.size < 2 -> skipped += tokens[i]
                                else -> break
                            }
                            i++
                        }
                        if (w == words.size) return skipped.firstOrNull() ?: tokens.getOrNull(i)
                    }
                    return null
                }
                var printed = 0
                for (item in generated.semantic.ordinaryEquipment) {
                    for (extra in listOfNotNull(item.description, item.notes).map { flat(it) }.filter { it.length >= 15 }) {
                        assertFalse(table.contains(extra.take(15)),
                            "P2 EQUIPO never prints the description or notes of ${item.name} ($fixture $variant).")
                    }
                    val words = flat(item.name).split(" ")
                    val found = halves.firstNotNullOfOrNull { quantityOf(it.split(" "), words) } ?: continue
                    assertEquals(item.quantity.toString(), found,
                        "«${item.name}» prints with its real quantity ($fixture $variant).")
                    printed++
                }
                assertTrue(printed >= 3, "Real ordinary items print on P2 ($fixture $variant): $printed")
                for (n in 1..pdf.numberOfPages) {
                    val text = PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                    assertFalse(Regex("""(^|\s)OTROS(\s|$)""").containsMatchIn(text),
                        "No generic «OTROS» box on page $n ($fixture $variant).")
                }
            }
        }
    }

    @Test
    fun c379EmptySheetKeepsFourP2ReservesAndItsP3AB() {
        // C379 / P211 + P301: with no equipment, treasure, traits or narrative,
        // P2 still prints its four modules, each with its own writable paper
        // rows, and P3 still exists with both narrative cartelas and paper rows.
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val empty = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(
                inventoryItems = emptyList(), currencies = emptyList(), traits = emptyList(),
                background = io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground(),
            ),
        ))
        val number = """(-?\d+(?:\.\d+)?)"""
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), empty,
            )
            assertTrue(generated.semantic.ordinaryEquipment.isEmpty() && generated.semantic.specialEquipment.isEmpty() &&
                generated.semantic.currencies.isEmpty() && generated.semantic.traits.isEmpty(),
                "The fixture really is empty ($variant).")
            preserveRealApplicationPdf(generated.bytes, "V11-INTERNAL-PROOF-C379-empty-sheet-" + variant.name + ".pdf")
            Loader.loadPDF(generated.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }
                val p2 = pages.indexOfFirst { it.contains("P2 - Equipo y tesoro") }
                val p3 = pages.indexOfFirst { it.contains("P3 - Trasfondo") }
                assertTrue(p2 >= 0 && p3 >= 0, "P2 and P3 exist on an empty sheet ($variant).")
                for (title in listOf("EQUIPO", "TESORO", "RASGOS", "EQUIPO ESPECIAL")) {
                    assertTrue(pages[p2].contains(title), "P2 keeps its «$title» module ($variant).")
                }
                assertTrue(pages[p3].contains("TRASFONDO Y PERFIL NARRATIVO") && pages[p3].contains("HISTORIA Y CRÓNICA"),
                    "P3 keeps both narrative cartelas ($variant).")
                fun rects(page: Int) = Regex("""(?:^|\s)$number\s+$number\s+$number\s+$number\s+re(?:\s|$)""")
                    .findAll(pdf.getPage(page).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) })
                    .map { m -> m.groupValues.drop(1).map { it.toFloat() } }.toList()
                fun near(a: Float, b: Float) = kotlin.math.abs(a - b) < 0.05f
                // Empty P2 tables use the golden manuscript rows: 16.15 pt EQUIPO, 19.84 pt TESORO.
                val p2Rects = rects(p2)
                val equipo = p2Rects.count { (x, y, w, h) -> near(x, 39.69f) && near(w, 126.56f) && (near(h, 16f) || near(h, 16.15f)) && y > 430f }
                val tesoro = p2Rects.count { (x, y, w, h) -> near(x, 39.69f) && near(w, 126.56f) && (near(h, 16f) || near(h, 19.84f)) && y in 276f..427f }
                val rasgos = p2Rects.count { (x, _, w, h) -> near(x, 309.26f) && near(w, 129.54f) && near(h, 16f) }
                val especial = p2Rects.count { (x, _, w, h) -> near(x, 39.69f) && near(w, 532.62f) && near(h, 26.93f) }
                assertTrue(equipo >= 3 && tesoro >= 2 && rasgos >= 3 && especial >= 2,
                    "Each empty P2 module keeps its own writable rows ($variant): " +
                        "equipo=$equipo tesoro=$tesoro rasgos=$rasgos especial=$especial")
                assertTrue(rects(p3).size >= 10, "P3 keeps writable paper rows ($variant).")
            }
        }
    }

    @Test
    fun c380NearlyEmptyNonCasterSheetInventsNoValuesOrMarkersAB() {
        // C380 / GLB10: a nearly empty non-caster (Aldren without equipment, treasure,
        // traits or narrative) gets no P4 or Libro, no spellcasting values or slot
        // counts on P1, no numbers in the empty P2 tables and no epic levels anywhere.
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val empty = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(
                inventoryItems = emptyList(), currencies = emptyList(), traits = emptyList(),
                background = io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground(),
            ),
        ))
        val spellLabels = setOf("LANZAMIENTO", "DE", "CONJUROS", "APTITUD", "MÁGICA", "CD", "SALVACIÓN",
            "ATAQUE", "MÁGICO", "MUNICIONES", "Tipo:") +
            (1..9).map { "N$it" }
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = true), empty,
            )
            assertTrue(generated.semantic.spells.isEmpty() && generated.semantic.spellSlots.isEmpty(),
                "Aldren casts no spells ($variant).")
            Loader.loadPDF(generated.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }
                assertFalse(pages.any { it.contains("P4 - Conjuros") || it.contains("Libro de conjuros") },
                    "No P4 or Libro without real spells ($variant).")
                assertFalse(pages.any { Regex("""NIVEL 1[0-2]\b""").containsMatchIn(it) },
                    "No epic spell level without real epic rules ($variant).")
                fun region(page: Int, x: Float, y: Float, w: Float, h: Float) = PDFTextStripperByArea().apply {
                    addRegion("r", java.awt.geom.Rectangle2D.Float(x, y, w, h))
                    extractRegions(pdf.getPage(page))
                }.getTextForRegion("r").split(Regex("""\s+""")).filter { it.isNotBlank() }
                // P1 LANZAMIENTO DE CONJUROS: its cards and N1..N9 slot rows (y560..700 from the top).
                val spell = region(0, 268f, 560f, 307f, 140f)
                assertTrue(spell.isNotEmpty() && spellLabels.containsAll(spell),
                    "P1 spellcasting prints only its labels, no invented value or slot count ($variant): $spell")
                val p2 = pages.indexOfFirst { it.contains("P2 - Equipo y tesoro") }
                val tables = region(p2, 36f, 100f, 540f, 400f)
                assertFalse(tables.any { token -> token.any(Char::isDigit) },
                    "Empty P2 tables print no invented quantity or amount ($variant): $tables")
            }
        }
    }

    @Test
    fun c382P1PrintsTheChosenStateAndFallsBackToPermanentWithoutInventingAB() {
        // C382 / IN02: PERMANENT vs CURRENT. With a current snapshot, CURRENT prints its
        // HP; PERMANENT ignores it; CURRENT without a snapshot falls back to the
        // permanent HP with the CURRENT_SNAPSHOT_UNAVAILABLE notice, never a made-up value.
        val base = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val permanentHp = base.permanent.sheet.currentHp
        val currentHp = if (permanentHp == 7) 8 else 7
        val withCurrent = base.copy(currentSnapshot = base.permanent.copy(
            sheet = base.permanent.sheet.copy(currentHp = currentHp)))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            for ((label, sourcesAndState, expected) in listOf(
                Triple("current", withCurrent to PcSheetExportStateSelection.CURRENT_SNAPSHOT, currentHp),
                Triple("permanent", withCurrent to PcSheetExportStateSelection.PERMANENT, permanentHp),
                Triple("fallback", base to PcSheetExportStateSelection.CURRENT_SNAPSHOT, permanentHp),
            )) {
                val (src, state) = sourcesAndState
                val generated = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, state), src,
                )
                val notices = generated.semantic.notices.map { it.code }
                if (label == "fallback") {
                    assertEquals(PcSheetExportStateSelection.PERMANENT, generated.semantic.selectedState)
                    assertTrue(io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportNoticeCode.CURRENT_SNAPSHOT_UNAVAILABLE in notices,
                        "The fallback carries its notice ($variant).")
                } else {
                    assertTrue(notices.isEmpty(), "No notice when the chosen state exists ($label $variant).")
                }
                Loader.loadPDF(generated.bytes).use { pdf ->
                    // P1 PV ACTUALES card: the third golden combat row (y520.16 from the bottom).
                    val card = PDFTextStripperByArea().apply {
                        addRegion("pv", java.awt.geom.Rectangle2D.Float(270f, 271.8f, 140f, 31.2f))
                        extractRegions(pdf.getPage(0))
                    }.getTextForRegion("pv").replace(Regex("""\s+"""), " ").trim()
                    val value = Regex("""PV ACTUALES (\d+)""").find(card)?.groupValues?.get(1)
                    assertEquals(expected.toString(), value,
                        "P1 PV ACTUALES prints the $label state's HP ($variant): $card")
                }
            }
        }
    }

    @Test
    fun c383MechanicalTraitsNeverMixWithNarrativeTraitsOrSpecialEquipmentAB() {
        // C383 / RSG06: the P2 RASGOS column holds mechanical traits only - never the
        // narrative personality, ideals, bonds or flaws, nor a special item's
        // description - and P3's narrative never prints a mechanical trait name.
        fun flat(text: String) = text.replace(Regex("""\s+"""), " ").trim()
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources("03_mara_siete_umbrales_custom_extended.json"),
            )
            val semantic = generated.semantic
            val background = semantic.background
            val narrative = listOf(background.personalityTraits, background.ideals, background.bonds,
                background.flaws).map { flat(it) }.filter { it.length >= 20 }
            val special = semantic.specialEquipment.mapNotNull { it.description }.map { flat(it) }
                .filter { it.length >= 20 }
            assertTrue(narrative.size >= 2 && special.isNotEmpty() && semantic.traits.size >= 3,
                "Mara has narrative traits, special descriptions and mechanical traits ($variant).")
            Loader.loadPDF(generated.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }
                val p2 = pages.indexOfFirst { it.contains("P2 - Equipo y tesoro") }
                val rasgos = flat(PDFTextStripperByArea().apply {
                    addRegion("rasgos", java.awt.geom.Rectangle2D.Float(306f, 100f, 270f, 400f))
                    extractRegions(pdf.getPage(p2))
                }.getTextForRegion("rasgos"))
                assertTrue(rasgos.contains("RASGOS"), "The P2 RASGOS column is read ($variant).")
                (narrative + special).forEach { text ->
                    assertFalse(rasgos.contains(text.take(20)),
                        "P2 RASGOS never prints narrative or special-equipment text ($variant): ${text.take(20)}")
                }
                val p3 = flat(pages.filter { it.contains("P3 - Trasfondo") || it.contains("P3 - Narrativa") }
                    .joinToString(" "))
                semantic.traits.map { flat(it.name) }.filter { it.length >= 12 }.forEach { name ->
                    assertFalse(p3.contains(name), "P3 never prints the mechanical trait «$name» ($variant).")
                }
            }
        }
    }

    @Test
    fun c384EveryTraitPrintsOnceAcrossP1P2AndC2WithoutDescriptionsAB() {
        // C384 / P207: mechanical traits flow P1 RASGOS -> P2 RASGOS (two internal
        // columns) -> C2; each trait name prints exactly once across them and the
        // P1/P2 boxes never print a trait description.
        fun flat(text: String) = text.replace(Regex("""\s+"""), " ").trim()
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources("03_mara_siete_umbrales_custom_extended.json"),
            )
            val traits = generated.semantic.traits
            assertTrue(traits.size >= 10, "Mara has many mechanical traits ($variant).")
            Loader.loadPDF(generated.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }
                fun columns(page: Int, vararg boxes: java.awt.geom.Rectangle2D.Float): List<String> =
                    PDFTextStripperByArea().apply {
                        boxes.forEachIndexed { i, box -> addRegion("c$i", box) }
                        extractRegions(pdf.getPage(page))
                    }.let { s -> boxes.indices.map { flat(s.getTextForRegion("c$it")) } }
                // P1 RASGOS rows: x270.43..572.32 split at x424.2, y443..545 from the top.
                val p1 = columns(0, java.awt.geom.Rectangle2D.Float(270f, 443f, 154f, 102f),
                    java.awt.geom.Rectangle2D.Float(424.2f, 443f, 148f, 102f))
                val p2Index = pages.indexOfFirst { it.contains("P2 - Equipo y tesoro") }
                // P2 RASGOS: x309.26..572.32 split at its x440.79 rule, y121..475 from the top.
                val p2 = columns(p2Index, java.awt.geom.Rectangle2D.Float(309f, 121f, 131.5f, 354f),
                    java.awt.geom.Rectangle2D.Float(440.8f, 121f, 131.5f, 354f))
                val c2 = pages.indices.filter { pages[it].contains("C2 - Habilidades, acciones y recursos") }
                    .flatMap { index ->
                        columns(index, java.awt.geom.Rectangle2D.Float(39f, 90f, 263f, 680f),
                            java.awt.geom.Rectangle2D.Float(310.5f, 90f, 263f, 680f))
                    }
                val all = p1 + p2 + c2
                traits.forEach { trait ->
                    val name = Regex(Regex.escape(flat(trait.name)) + """(?=\s|$|—|-)""")
                    assertEquals(1, all.sumOf { name.findAll(it).count() },
                        "«${trait.name}» prints exactly once across P1, P2 and C2 ($variant).")
                    val description = flat(trait.description)
                    if (description.length >= 20) {
                        assertFalse((p1 + p2).any { it.contains(description.take(20)) },
                            "P1/P2 RASGOS never print the description of ${trait.name} ($variant).")
                    }
                }
                assertTrue(p2.all { it.isNotBlank() }, "Both P2 internal trait columns are used ($variant).")
            }
        }
    }

    @Test
    fun c386LongSpellNamesPrintWholeOnceAndTheirLevelContinuesAB() {
        // C386 / P404: 40 level-3 spells with long names - each tagged by a unique
        // token - print exactly once on P4 with every word of the name, and the
        // level that crosses a physical column names its continuation.
        val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val base = original.permanent.sheet.spells.first { it.sourceAssociations.isNotEmpty() }
        val tail = "nombre muy largo para probar el ajuste de columna"
        val spells = (1..40).map { n ->
            base.copy(
                id = Uuid.parse("f6000000-0000-4000-8000-" + n.toString().padStart(12, '0')),
                name = "QAP4L" + n.toString().padStart(3, '0') + " " + tail,
                level = 3, sortOrder = n,
            )
        }
        val input = original.copy(permanent = original.permanent.copy(
            sheet = original.permanent.sheet.copy(spells = spells)))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), input,
            )
            preserveRealApplicationPdf(proof.bytes, "V11-INTERNAL-PROOF-C386-long-spell-names-$variant.pdf")
            Loader.loadPDF(proof.bytes).use { pdf ->
                val p4 = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }.filter { it.contains("P4 - Conjuros /") }
                assertTrue(p4.isNotEmpty(), "Real spells produce P4 ($variant).")
                val tokens = p4.joinToString(" ").split(Regex("""\s+"""))
                for (n in 1..40) {
                    val tag = "QAP4L" + n.toString().padStart(3, '0')
                    val at = tokens.indices.filter { tokens[it] == tag }
                    assertEquals(1, at.size, "$tag prints exactly once on P4 ($variant).")
                    val window = tokens.drop(at.single() + 1).take(14)
                    assertTrue(window.containsAll(tail.split(" ")),
                        "$tag keeps every word of its long name ($variant): $window")
                }
                assertTrue(p4.any { it.contains("NIVEL 3 / CONTINUACIÓN") || it.contains("NIVEL 3 / VIENE DE PÁG.") },
                    "Level 3 names its continuation in the next physical column ($variant).")
            }
        }
    }

    @Test
    fun c393TraitsPrintInManualOrderWithWholeLongNamesAB() {
        // C393 / RSG05 G3: reading P1 RASGOS, P2 RASGOS and C2 column by column in
        // flow order, Mara's traits appear in their manual (semantic) order and each
        // long name prints whole.
        fun flat(text: String) = text.replace(Regex("""\s+"""), " ").trim()
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources("03_mara_siete_umbrales_custom_extended.json"),
            )
            val traits = generated.semantic.traits
            Loader.loadPDF(generated.bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }
                fun columns(page: Int, vararg boxes: java.awt.geom.Rectangle2D.Float): List<String> =
                    PDFTextStripperByArea().apply {
                        boxes.forEachIndexed { i, box -> addRegion("c$i", box) }
                        extractRegions(pdf.getPage(page))
                    }.let { s -> boxes.indices.map { flat(s.getTextForRegion("c$it")) } }
                val p2 = pages.indexOfFirst { it.contains("P2 - Equipo y tesoro") }
                val flow = (columns(0, java.awt.geom.Rectangle2D.Float(270f, 443f, 154f, 102f),
                    java.awt.geom.Rectangle2D.Float(424.2f, 443f, 148f, 102f)) +
                    columns(p2, java.awt.geom.Rectangle2D.Float(309f, 121f, 131.5f, 354f),
                        java.awt.geom.Rectangle2D.Float(440.8f, 121f, 131.5f, 354f)) +
                    pages.indices.filter { pages[it].contains("C2 - Habilidades, acciones y recursos") }
                        .flatMap { i -> columns(i, java.awt.geom.Rectangle2D.Float(39f, 90f, 263f, 680f),
                            java.awt.geom.Rectangle2D.Float(310.5f, 90f, 263f, 680f)) })
                    .joinToString(" ")
                val positions = traits.map { trait ->
                    val at = Regex(Regex.escape(flat(trait.name)) + """(?=\s|$|—|-)""").find(flow)
                    assertTrue(at != null, "«${trait.name}» prints whole ($variant).")
                    at!!.range.first
                }
                assertEquals(positions.sorted(), positions, "Traits print in their manual order ($variant).")
                assertTrue(traits.any { it.name.length >= 15 }, "The fixture has long trait names ($variant).")
            }
        }
    }

    @Test
    fun c393NoPagePrintsAnImageOtherThanTheHeaderLogoAB() {
        // C393 / P306 G3: no background or narrative image is ever printed - every
        // page, P3 included, carries only the header logo image (no portrait given).
        for ((fixture, variant) in listOf(
            "02_ilyra_quill_srd5_2_1_evoker_wizard.json" to PcSheetV11Variant.B,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
        )) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT,
                    includeSpellDescriptions = true), sources(fixture),
            )
            // Images are counted through nested form XObjects too.
            fun images(resources: org.apache.pdfbox.pdmodel.PDResources?): Int =
                resources?.xObjectNames?.sumOf { name ->
                    when (val x = resources.getXObject(name)) {
                        is org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject -> 1
                        is org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject -> images(x.resources)
                        else -> 0
                    }
                } ?: 0
            Loader.loadPDF(generated.bytes).use { pdf ->
                for (index in 0 until pdf.numberOfPages) {
                    assertEquals(1, images(pdf.getPage(index).resources),
                        "Page ${index + 1} carries only the header logo image ($fixture $variant).")
                }
            }
        }
    }

    @Test
    fun c395P2DigitalWordsSitInsideOneRowAndBlankRowsStayWritableAB() {
        // C395 / GLB07 G3: on the real P2, every word inside the EQUIPO, TESORO and
        // RASGOS tables sits inside one row band (baseline and letter tops between
        // its rules), and a sheet with few records keeps whole empty rows to write on.
        val number = """(-?\d+(?:\.\d+)?)"""
        for ((fixture, variant) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to PcSheetV11Variant.A,
            "03_mara_siete_umbrales_custom_extended.json" to PcSheetV11Variant.A,
        )) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), sources(fixture),
            )
            Loader.loadPDF(generated.bytes).use { pdf ->
                val p2 = (1..pdf.numberOfPages).first { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf).contains("P2 - Equipo y tesoro")
                } - 1
                val ops = pdf.getPage(p2).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                fun near(a: Float, b: Float) = kotlin.math.abs(a - b) < 0.05f
                // Golden P2 table blocks (C408, bottom origin): EQUIPO/TESORO x39.69 and x171.92,
                // RASGOS x309.26 and x442.77.
                val rows = Regex("""(?:^|\s)$number\s+$number\s+$number\s+$number\s+re(?:\s|$)""").findAll(ops)
                    .map { m -> m.groupValues.drop(1).map { it.toFloat() } }
                    .filter { (x, _, w, h) -> ((near(x, 39.69f) && near(w, 126.56f)) || (near(x, 171.92f) && near(w, 126.57f)) ||
                        (near(x, 309.26f) && near(w, 129.54f)) || (near(x, 442.77f) && near(w, 129.55f))) &&
                        h in 15.9f..19.9f }.toList()
                assertTrue(rows.size >= 15, "P2 table rows are found ($fixture): ${rows.size}")
                val glyphs = mutableListOf<org.apache.pdfbox.text.TextPosition>()
                object : PDFTextStripper() {
                    override fun writeString(text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>) {
                        glyphs += textPositions.filter { !it.unicode.isBlank() }
                    }
                }.apply { startPage = p2 + 1; endPage = p2 + 1 }.getText(pdf)
                val inTables = glyphs.filter { g ->
                    val x = g.xDirAdj; val base = 792f - g.yDirAdj
                    rows.any { (rx, ry, rw, rh) -> x >= rx && x <= rx + rw && base >= ry - 4f && base <= ry + rh + 4f }
                }
                assertTrue(inTables.size >= 20, "Table text is read ($fixture): ${inTables.size}")
                inTables.forEach { g ->
                    val base = 792f - g.yDirAdj
                    val top = base + g.heightDir
                    assertTrue(rows.any { (rx, ry, rw, rh) -> g.xDirAdj >= rx && g.xDirAdj <= rx + rw &&
                        base >= ry && top <= ry + rh + 0.5f },
                        "«${g.unicode}» at y$base sits inside one P2 row ($fixture $variant).")
                }
                if (fixture.startsWith("01_")) {
                    val empty = rows.count { (rx, ry, rw, rh) -> inTables.none { g ->
                        val base = 792f - g.yDirAdj
                        g.xDirAdj >= rx && g.xDirAdj <= rx + rw && base >= ry && base <= ry + rh } }
                    assertTrue(empty >= 5, "Aldren keeps whole empty P2 rows to write on: $empty")
                }
            }
        }
    }

    @Test
    fun c396EveryC2NotasCartelaHasACompleteGridDownToThePageFootAB() {
        // C396 / C207 G3: every NOTAS cartela on a C2 page sits over a complete
        // manuscript grid - all its vertical rules share one length, all horizontal
        // rules share one length, the pitch is regular and the grid reaches the foot
        // of the page area. Strokes are read with their real colour (C401: the golden
        // #bfd3de inner rules inside a #94b0c1 frame).
        data class Seg(val x0: Float, val y0: Float, val x1: Float, val y1: Float)
        class Strokes(page: org.apache.pdfbox.pdmodel.PDPage) :
            org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine(page) {
            val grid = mutableListOf<Seg>()
            private var start: java.awt.geom.Point2D.Float? = null
            private val pending = mutableListOf<Seg>()
            private var current = java.awt.geom.Point2D.Float()
            override fun appendRectangle(p0: java.awt.geom.Point2D, p1: java.awt.geom.Point2D,
                p2: java.awt.geom.Point2D, p3: java.awt.geom.Point2D) {}
            override fun drawImage(pdImage: org.apache.pdfbox.pdmodel.graphics.image.PDImage) {}
            override fun clip(windingRule: Int) {}
            override fun moveTo(x: Float, y: Float) { current = java.awt.geom.Point2D.Float(x, y); start = current }
            override fun lineTo(x: Float, y: Float) {
                pending += Seg(current.x, current.y, x, y); current = java.awt.geom.Point2D.Float(x, y)
            }
            override fun curveTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
                current = java.awt.geom.Point2D.Float(x3, y3)
            }
            override fun getCurrentPoint(): java.awt.geom.Point2D = current
            override fun closePath() {}
            override fun endPath() { pending.clear() }
            override fun strokePath() {
                val rgb = graphicsState.strokingColor.toRGB()
                if (rgb == java.awt.Color(191, 211, 222).rgb and 0xFFFFFF) grid += pending
                pending.clear()
            }
            override fun fillPath(windingRule: Int) { pending.clear() }
            override fun fillAndStrokePath(windingRule: Int) { pending.clear() }
            override fun shadingFill(shadingName: org.apache.pdfbox.cos.COSName) {}
        }
        var checked = 0
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources("03_mara_siete_umbrales_custom_extended.json"),
            )
            Loader.loadPDF(generated.bytes).use { pdf ->
                for (index in 0 until pdf.numberOfPages) {
                    val text = PDFTextStripper().apply { startPage = index + 1; endPage = index + 1 }.getText(pdf)
                    if (!text.contains("C2 - ")) continue
                    val labels = mutableListOf<Pair<Float, Float>>()
                    object : PDFTextStripper() {
                        override fun writeString(s: String, tp: MutableList<org.apache.pdfbox.text.TextPosition>) {
                            if (s.trim() == "NOTAS") labels += tp.first().xDirAdj to (792f - tp.first().yDirAdj)
                        }
                    }.apply { startPage = index + 1; endPage = index + 1 }.getText(pdf)
                    if (labels.isEmpty()) continue
                    val strokes = Strokes(pdf.getPage(index)).also { it.processPage(pdf.getPage(index)) }.grid
                    for ((labelX, labelY) in labels) {
                        // This cartela's own grid: bounded by the first horizontal rule under it.
                        val first = strokes.filter { s -> kotlin.math.abs(s.y0 - s.y1) < 0.01f && s.y0 < labelY - 1f &&
                            minOf(s.x0, s.x1) <= labelX && maxOf(s.x0, s.x1) > labelX + 50f }.maxByOrNull { it.y0 }
                        val (gx0, gx1) = if (first == null) 0f to 0f
                            else minOf(first.x0, first.x1) to maxOf(first.x0, first.x1)
                        val mine = strokes.filter { s -> maxOf(s.y0, s.y1) < labelY - 1f &&
                            minOf(s.x0, s.x1) >= gx0 - 0.05f && maxOf(s.x0, s.x1) <= gx1 + 0.05f }
                        val vertical = mine.filter { kotlin.math.abs(it.x0 - it.x1) < 0.01f }
                        val horizontal = mine.filter { kotlin.math.abs(it.y0 - it.y1) < 0.01f }
                        // Inner rules only: a three-row grid in a small gap has two horizontals (C443).
                        assertTrue(vertical.size >= 3 && horizontal.size >= 2,
                            "NOTAS at ($labelX, $labelY) has a grid on page ${index + 1} ($variant).")
                        val vLengths = vertical.map { kotlin.math.abs(it.y1 - it.y0) }
                        val hLengths = horizontal.map { kotlin.math.abs(it.x1 - it.x0) }
                        assertTrue(vLengths.max() - vLengths.min() < 0.05f && hLengths.max() - hLengths.min() < 0.05f,
                            "The grid under NOTAS on page ${index + 1} is complete, no cut cells ($variant).")
                        val xs = vertical.map { it.x0 }.distinct().sorted()
                        val steps = xs.zipWithNext().map { (a, b) -> b - a }
                        assertTrue(steps.max() - steps.min() < 0.05f && steps.first() in 15f..16.5f,
                            "Regular golden grid pitch on page ${index + 1} ($variant): $xs")
                        // Whole cells only: no further 16 pt cell fits above the golden C2 foot (C397: y59.53).
                        assertTrue(vertical.minOf { minOf(it.y0, it.y1) } - 59.53f < 16f,
                            "The grid reaches the page-area foot on page ${index + 1} ($variant).")
                        checked++
                    }
                }
            }
        }
        assertTrue(checked >= 2, "Real C2 NOTAS grids are checked: $checked")
    }

    @Test
    fun c420VariantBPanelOrdersSkillsByAttributeWithCustomOnesOnOneRow() {
        // C420: golden B panel - skills by attribute order, custom skills of an attribute
        // printed on P1 included with their three letters, each on one row with its value.
        val generated = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.B, PcSheetExportStateSelection.PERMANENT),
            sources("03_mara_siete_umbrales_custom_extended.json"),
        )
        Loader.loadPDF(generated.bytes).use { pdf ->
            val lines = mutableListOf<Triple<String, Float, Float>>()
            object : PDFTextStripper() {
                override fun writeString(text: String, positions: MutableList<org.apache.pdfbox.text.TextPosition>) {
                    val first = positions.first()
                    if (first.xDirAdj < 262f && 792f - first.yDirAdj < 340f)
                        lines += Triple(text.trim(), first.xDirAdj, 792f - first.yDirAdj)
                }
            }.apply { startPage = 1; endPage = 1 }.getText(pdf)
            val labels = lines.filter { it.first.endsWith(")") }
                .sortedWith(compareBy({ it.second > 150f }, { -it.third }))
            assertEquals("Atletismo (FUE)", labels.first().first, "Strength's skill opens the panel.")
            val letters = labels.map { it.first.substringAfterLast('(').removeSuffix(")") }
            val order = listOf("FUE", "DES", "CON", "INT", "SAB", "CAR", "FOR", "COR", "ETE")
            assertEquals(letters.sortedBy { order.indexOf(it) }, letters, "Skills follow their attribute's order.")
            assertTrue(letters.containsAll(listOf("FOR", "COR", "ETE")), "Custom attributes' skills are in the panel.")
            assertFalse(lines.any { Regex("""^\([A-Z]{3}\)""").containsMatchIn(it.first) },
                "No label wraps its three letters onto a second row: $lines")
        }
    }

    @Test
    fun c424PortraitFitShowsTheWholeImageAndCropFillsWithoutDistortionOnRealRasterAB() {
        // C424 / IN04: a 2:1 image with green side bands. FIT shows the bands with white
        // margins above and below; CROP fills the box and cuts the bands; neither spills.
        val image = BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB)
        image.createGraphics().apply {
            color = Color(0, 200, 0); fillRect(0, 0, 200, 100)
            color = Color(0, 0, 220); fillRect(20, 0, 160, 100)
            dispose()
        }
        val png = ByteArrayOutputStream().use { out -> check(ImageIO.write(image, "png", out)); out.toByteArray() }
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val source = original.copy(permanent = original.permanent.copy(
            closure = original.permanent.closure.copy(portraitRef = "qa:wide")))
        val service = DesktopPcSheetExportService(portraitBytesLoader = { png })
        // The viewport of the portrait box: x44.69..114.06, y631.92..693.46.
        val left = 44.69f; val bottom = 631.92f; val width = 69.37f; val height = 61.54f
        fun colour(raster: BufferedImage, x: Float, y: Float): Color =
            Color(raster.getRGB((x * 4f).toInt(), ((792f - y) * 4f).toInt()))
        fun isBlue(c: Color) = c.blue > 150 && c.green < 80
        fun isGreen(c: Color) = c.green > 150 && c.blue < 80
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            for (fit in listOf(PcSheetPortraitFitMode.FIT_ENTIRE_IMAGE, PcSheetPortraitFitMode.CROP_TO_FILL)) {
                val bytes = service.generateV11Proof(PcSheetV11ExportRequest(variant,
                    PcSheetExportStateSelection.PERMANENT, portraitFitMode = fit), source).bytes
                Loader.loadPDF(bytes).use { pdf ->
                    val raster = org.apache.pdfbox.rendering.PDFRenderer(pdf).renderImageWithDPI(0, 288f)
                    val midY = bottom + height / 2f
                    assertTrue(isBlue(colour(raster, left + width / 2f, midY)), "Centre shows the image ($fit, $variant).")
                    if (fit == PcSheetPortraitFitMode.FIT_ENTIRE_IMAGE) {
                        // Scale 0.3469: band edge at left + 6.94; drawn height 34.69.
                        assertTrue(isGreen(colour(raster, left + 3f, midY)), "FIT keeps the left band ($variant).")
                        assertTrue(isBlue(colour(raster, left + 10f, midY)), "FIT keeps the proportion ($variant).")
                        assertTrue(isGreen(colour(raster, left + width - 3f, midY)), "FIT keeps the right band ($variant).")
                        assertFalse(isBlue(colour(raster, left + width / 2f, bottom + height - 3f)),
                            "FIT leaves a margin above the image ($variant).")
                        assertFalse(isBlue(colour(raster, left + width / 2f, bottom + 3f)),
                            "FIT leaves a margin below the image ($variant).")
                    } else {
                        // Scale 0.6154: the bands fall outside the box.
                        assertTrue(isBlue(colour(raster, left + 3f, midY)), "CROP cuts the left band ($variant).")
                        assertTrue(isBlue(colour(raster, left + width - 3f, midY)), "CROP cuts the right band ($variant).")
                        assertTrue(isBlue(colour(raster, left + width / 2f, bottom + height - 2f)), "CROP fills to the top ($variant).")
                        assertTrue(isBlue(colour(raster, left + width / 2f, bottom + 2f)), "CROP fills to the foot ($variant).")
                    }
                    // Nothing spills out of the viewport, in either mode.
                    assertFalse(isBlue(colour(raster, left - 2f, midY)) || isBlue(colour(raster, left + width + 2f, midY)) ||
                        isBlue(colour(raster, left + width / 2f, bottom - 2f)), "No spill ($fit, $variant).")
                }
            }
        }
    }

    @Test
    fun c424LocalSaveWritesTheRealPdfWithNoConnectionAB() {
        // C424 / IN06: the App's save step writes the real PDF locally; the service takes
        // no account, token or network address. The UI itself is not exercised here.
        val service = DesktopPcSheetExportService()
        val folder = java.nio.file.Files.createTempDirectory("v11-local-save").toFile()
        try {
            for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
                val proof = service.generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                    sources("01_aldren_vale_srd5_1_champion_fighter.json"),
                )
                val saved = service.saveV11Proof(proof, File(folder, "hoja-" + variant.name))
                assertTrue(saved.name.endsWith(".pdf") && saved.isFile, "Saved with its extension ($variant).")
                assertTrue(saved.readBytes().contentEquals(proof.bytes), "The saved file is the generated PDF ($variant).")
                Loader.loadPDF(saved).use { pdf -> assertTrue(pdf.numberOfPages >= 4, "A whole sheet ($variant).") }
                assertEquals(setOf(saved.name), folder.listFiles()!!.filter { it.name.contains(variant.name) }.map { it.name }.toSet(),
                    "No temporary file remains ($variant).")
            }
        } finally {
            folder.deleteRecursively()
        }
    }

    @Test
    fun c424UnderStressIntegrityComesBeforeLegibilityPaperAndPageCountAB() {
        // C424 / GLB09: integrity first - every real record prints whole and once even if
        // pages grow; then legibility - no text under the smallest step; then the writing
        // paper - every P4 level keeps a blank row and the sheet ends on its NOTAS page.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        val base = mara.permanent.sheet.spells.first { it.sourceAssociations.isNotEmpty() }
        val stress = mara.copy(permanent = mara.permanent.copy(sheet = mara.permanent.sheet.copy(
            spells = mara.permanent.sheet.spells + (1..120).map { n -> base.copy(
                id = Uuid.parse("f7300000-0000-4000-8000-" + n.toString().padStart(12, '0')),
                name = "QAGLB" + n.toString().padStart(3, '0'), level = n % 10, sortOrder = 1000 + n) })))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val ordinary = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), mara)
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), stress)
            Loader.loadPDF(proof.bytes).use { pdf ->
                Loader.loadPDF(ordinary.bytes).use { small ->
                    assertTrue(pdf.numberOfPages > small.numberOfPages, "Pages are what gives way ($variant).")
                }
                val text = PDFTextStripper().getText(pdf)
                (1..120).forEach { n ->
                    val name = "QAGLB" + n.toString().padStart(3, '0')
                    assertEquals(1, Regex(name).findAll(text).count(), "$name prints once ($variant).")
                }
                proof.semantic.traits.forEach { trait ->
                    assertTrue(text.replace(Regex("""\s+"""), " ").contains(trait.name.replace(Regex("""\s+"""), " ")),
                        "Trait ${trait.name} survives ($variant).")
                }
                var smallest = 99f
                var smallestAt = ""
                object : PDFTextStripper() {
                    override fun writeString(value: String, positions: MutableList<org.apache.pdfbox.text.TextPosition>) {
                        positions.forEach { if (it.unicode.isNotBlank() && it.fontSize < smallest) { smallest = it.fontSize; smallestAt = value } }
                    }
                }.getText(pdf)
                assertTrue(smallest >= 5.3f, "No text under the smallest step: $smallest in «$smallestAt» ($variant).")
                val lastPage = PDFTextStripper().apply { startPage = pdf.numberOfPages; endPage = pdf.numberOfPages }.getText(pdf)
                assertTrue(lastPage.lineSequence().any { it.trim() == "NOTAS" } && !lastPage.contains("QAGLB"),
                    "The last page stays paper ($variant).")
            }
        }
    }

    @Test
    fun c423ALeftoverTableSharesItsPageWithEquipoEspecialAB() {
        // C423: as in the golden, one table left over from P2 shares its page with every
        // deferred EQUIPO ESPECIAL row that fits, and P2 points both modules to that page.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            Loader.loadPDF(DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), mara,
            ).bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }
                val shared = pages.indexOfFirst { it.contains("C2 - Equipo y tesoro / EQUIPO + EQUIPO ESPECIAL") }
                assertTrue(shared > 0, "The shared page exists ($variant).")
                assertFalse(pages.any { it.contains("C2 - Equipo especial / continuación") },
                    "No separate special page remains ($variant).")
                listOf("EQUIPO", "EQUIPO ESPECIAL", "NOTAS").forEach { title ->
                    assertTrue(pages[shared].lineSequence().any { it.trim().startsWith(title) },
                        "$title cartela on the shared page ($variant).")
                }
                assertTrue(pages[1].contains("EQUIPO, EQUIPO ESPECIAL CONTINÚAN EN PÁG. " + (shared + 1)),
                    "P2 names both modules with the real page ($variant).")
                assertTrue(pages[shared].contains("VIENE DE PÁG. 2"), "It names its origin ($variant).")
                // C428: the golden comparator's label for full-width modules.
                assertTrue(pages[shared].contains("MÓDULOS COMPARTIDOS · MIXTO"), "Mixed C2 label ($variant).")
                assertTrue(pages.any { it.contains("MÓDULOS COMPARTIDOS · DOS COLUMNAS") }, "Two-column C2 label ($variant).")
            }
        }
    }

    @Test
    fun c422P4SharesItsRowsLikeTheGoldenWithMoreRoomForLowLevelsAB() {
        // C422 / P407: the golden share of one P4 page, real spells included - trucos 16,
        // levels 1..3 17/17/16, level 4 8 and levels 5..9 4 each, column by column; a level
        // with more real spells keeps them all and the page stays single.
        val number = """(-?\d+(?:\.\d+)?)"""
        val ilyra = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val base = ilyra.permanent.sheet.spells.first { it.sourceAssociations.isNotEmpty() }
        fun withSpells(vararg levels: Int) = ilyra.copy(permanent = ilyra.permanent.copy(
            sheet = ilyra.permanent.sheet.copy(spells = levels.mapIndexed { i, level -> base.copy(
                id = Uuid.parse("f7200000-0000-4000-8000-" + (i + 1).toString().padStart(12, '0')),
                name = "QAP4R" + i, level = level, sortOrder = i) })))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            fun rows(bytes: ByteArray): Triple<Int, List<Int>, List<Int>> = Loader.loadPDF(bytes).use { pdf ->
                val p4 = (0 until pdf.numberOfPages).filter { i ->
                    PDFTextStripper().apply { startPage = i + 1; endPage = i + 1 }.getText(pdf).contains("P4 - Conjuros /")
                }
                val ops = pdf.getPage(p4.first()).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                val boxes = Regex("""(?:^|\s)$number\s+$number\s+$number\s+$number\s+re(?:\s|$)""")
                    .findAll(ops).map { m -> m.groupValues.drop(1).map { it.toFloat() } }
                    .filter { (_, _, w, h) -> kotlin.math.abs(w - 8.22f) < 0.02f && kotlin.math.abs(h - 8.22f) < 0.02f }.toList()
                val columns = listOf(39.69f, 222.09f, 404.5f)
                val rowCounts = columns.map { c -> boxes.count { (x) -> kotlin.math.abs(x - (c + 2.56f)) < 0.05f } }
                val pageText = PDFTextStripper().apply { startPage = p4.first() + 1; endPage = p4.first() + 1 }.getText(pdf)
                val levelFirstColumn = (1..9).map { pageText.indexOf("NIVEL $it") }
                Triple(p4.size, rowCounts, levelFirstColumn)
            }
            val few = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), withSpells(0, 1, 3),
            )
            val (pages, counts) = rows(few.bytes)
            assertEquals(1, pages, "A short list stays on one P4 page ($variant).")
            assertEquals(listOf(33, 33, 28), counts, "The golden 16+17, 17+16 and 8+4x5 rows per column ($variant).")
            // Level 2 with 25 real spells keeps them all on the single page.
            val busy = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                withSpells(*IntArray(25) { 2 }),
            )
            Loader.loadPDF(busy.bytes).use { pdf ->
                val all = PDFTextStripper().getText(pdf)
                (0 until 25).forEach { assertEquals(1, Regex("QAP4R$it\\b").findAll(all).count(), "Spell $it prints once ($variant).") }
            }
            assertEquals(1, rows(busy.bytes).first, "25 real level 2 spells still fit one P4 page ($variant).")
        }
    }

    @Test
    fun c437HitDicePrintTheLevelTotalNotTheDiceLeftAB() {
        // C437 / P123 (owner 2026-11-05, «Total del nivel»): Guerrero 5 with 3 dice left
        // prints 5d10; a multiclass prints each class total on the same line.
        val aldren = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val fighter = aldren.permanent.sheet.classes.single()
        assertEquals(3, fighter.hitDiceRemaining, "The fixture has spent dice.")
        val multi = aldren.copy(permanent = aldren.permanent.copy(sheet = aldren.permanent.sheet.copy(
            classes = listOf(fighter, fighter.copy(
                id = Uuid.parse("a7700000-0000-4000-8000-000000000001"),
                name = "Mago", level = 3, hitDieSides = 6, hitDiceRemaining = 1, sortOrder = fighter.sortOrder + 1)))))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            fun p1(source: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources) =
                Loader.loadPDF(DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source,
                ).bytes).use { pdf ->
                    PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf).replace(Regex("""\s+"""), " ")
                }
            val single = p1(aldren)
            assertTrue(single.contains("5d10"), "The level total prints ($variant).")
            assertFalse(single.contains("3d10"), "The dice left do not print ($variant).")
            val both = p1(multi)
            assertTrue(both.contains("5d10 / 3d6"), "Each class total on one line ($variant).")
            assertFalse(both.contains("1d6"), "Dice left of the second class do not print ($variant).")
        }
    }

    /** Every printed character with its position, to find state marks in the symbol font. */
    private fun glyphs(pdf: org.apache.pdfbox.pdmodel.PDDocument, page: Int): List<Triple<String, Float, Float>> {
        val found = mutableListOf<Triple<String, Float, Float>>()
        object : PDFTextStripper() {
            init { startPage = page; endPage = page }
            override fun processTextPosition(text: org.apache.pdfbox.text.TextPosition) {
                found += Triple(text.unicode, text.xDirAdj, text.yDirAdj)
                super.processTextPosition(text)
            }
        }.getText(pdf)
        return found
    }

    @Test
    fun c448LevelsTenToTwelvePrintOnlyWhenAClassEnablesEpicLevelsAB() {
        // C448 / P129 (owner 2026-11-06; D-0076 3.11): a fourth row with N10, N11 and N12 if
        // and only if a class has epic levels enabled, in the same height, moving no module.
        val base = sources("03_mara_siete_umbrales_custom_extended.json")
        val sheet = base.permanent.sheet
        val epic = base.copy(permanent = base.permanent.copy(sheet = sheet.copy(
            classes = sheet.classes.mapIndexed { i, c -> if (i == 0) c.copy(epicLevelsEnabled = true) else c })))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            fun proof(source: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources) =
                DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source).bytes
            val on = proof(epic)
            preserveRealApplicationPdf(on, "V11-INTERNAL-PROOF-P1-epic-levels-$variant.pdf")
            fun labelsAndMunitions(bytes: ByteArray): Pair<List<String>, Float> = Loader.loadPDF(bytes).use { pdf ->
                val labels = page1Words(pdf).map { it.first }.filter { Regex("""N\d+""").matches(it) }
                val munitions = page1Words(pdf).first { it.first == "MUNICIONES" }.third
                labels to munitions
            }
            val (withEpic, munitionsOn) = labelsAndMunitions(on)
            val (withoutEpic, munitionsOff) = labelsAndMunitions(proof(base))
            assertEquals((1..12).map { "N$it" }, withEpic.sortedBy { it.drop(1).toInt() }, "All twelve levels ($variant).")
            assertEquals((1..9).map { "N$it" }, withoutEpic.sortedBy { it.drop(1).toInt() }, "Only nine ($variant).")
            assertEquals(munitionsOff, munitionsOn, "No other module moves ($variant).")
            Loader.loadPDF(on).use { pdf ->
                val native = pdf.getPage(0).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                val cells = Regex("""(?m)(?:^|\s)[\d.]+\s+([\d.]+)\s+98\.74\d*\s+14\.24\d*\s+re(?:\s|$)""")
                    .findAll(native).map { it.groupValues[1].toFloat() }.toList()
                assertEquals(12, cells.size, "Twelve cells of a quarter of the height ($variant).")
                assertTrue(kotlin.math.abs(cells.min() - 130.96f) < 0.02f, "The grid foot does not move ($variant).")
                assertTrue(kotlin.math.abs(cells.max() + 56.97f / 4f - 187.93f) < 0.02f, "Its top neither ($variant).")
                // C449 (owner 2026-11-06, «____/____ X/Y, sin casillas»): N10 to N12 have no box,
                // only one line for each number and the slash between them.
                val epicRow = 130.96f..(130.96f + 56.97f / 4f)
                val boxes = Regex("""(?m)(?:^|\s)([\d.]+)\s+([\d.]+)\s+[\d.]+\s+[\d.]+\s+re(?:\s|$)""")
                    .findAll(native).filter { m ->
                        val width = m.value.trim().split(Regex("""\s+"""))[2].toFloat()
                        m.groupValues[1].toFloat() >= 270f && width < 12f && m.groupValues[2].toFloat() in epicRow
                    }.toList()
                assertTrue(boxes.isEmpty(), "No box in N10 to N12 ($variant).")
                val lines = Regex("""(?m)([\d.]+)\s+([\d.]+)\s+m\s+([\d.]+)\s+([\d.]+)\s+l(?:\s|$)""")
                    .findAll(native).map { it.groupValues.drop(1).map(String::toFloat) }
                    .filter { (x0, y0, x1, y1) -> x0 >= 270f && y0 == y1 && y0 in epicRow && x1 - x0 in 25f..31f }.toList()
                assertEquals(6, lines.size, "Two write lines in each of the three cells ($variant).")
                val words = page1Words(pdf)
                listOf(10, 11, 12).forEach { level ->
                    val label = words.first { it.first == "N$level" }
                    val cellX = 270.43f + ((level - 1) % 3) * 101.57f
                    assertEquals(1, words.count { it.first == "/" && kotlin.math.abs(it.third - label.third) < 1f &&
                        it.second in (cellX + 60f)..(cellX + 69f) }, "The slash between both lines of N$level ($variant).")
                }
            }
        }
    }

    @Test
    fun c447TheRestOfTheStorySharesAC2SheetAB() {
        // C447 / P307 (owner 2026-11-06, «Sí, compartida»; golden v11_extremo p6): a HISTORIA
        // module on a C2 sheet with the other modules, 7 pt on 17.01 pt rows, 4.25 pt inside.
        val aldren = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val story = "HISTORIA-INICIO " + "Viaje registrado con fechas y testigos verificables. ".repeat(75) + "HISTORIA-FIN"
        val source = aldren.copy(permanent = aldren.permanent.copy(sheet = aldren.permanent.sheet.copy(
            background = aldren.permanent.sheet.background.copy(story = story))))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source).bytes
            preserveRealApplicationPdf(bytes, "V11-INTERNAL-PROOF-P3-story-shared-C2-$variant.pdf")
            Loader.loadPDF(bytes).use { pdf ->
                val pages = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }
                assertTrue(pages[2].contains("HISTORIA-INICIO"), "The story starts on P3 ($variant).")
                assertFalse(pages.any { it.contains("P3 - Narrativa") }, "No sheet just for the story ($variant).")
                val index = pages.indexOfFirst { it.contains("HISTORIA-FIN") }
                val c2 = pages[index]
                assertTrue(c2.contains("C2 - Habilidades, acciones y recursos"), "It is a C2 sheet ($variant).")
                assertTrue(c2.contains("RECURSOS Y MUNICIONES"), "It shares the sheet with other modules ($variant).")
                assertTrue(Regex("""(?m)^HISTORIA$""").containsMatchIn(c2), "It has its own cartela ($variant).")
                assertTrue(c2.contains("VIENE DE PÁG. 3"), "It names its origin ($variant).")
                assertTrue(pages[2].contains("HISTORIA CONTINÚA EN PÁG. " + (index + 1)), "P3 names the sheet ($variant).")
                val rows = mutableListOf<Triple<Float, Float, Float>>()
                object : PDFTextStripper() {
                    init { startPage = index + 1; endPage = index + 1 }
                    override fun writeString(s: String, tp: MutableList<org.apache.pdfbox.text.TextPosition>) {
                        if (s.startsWith("Viaje") || s.startsWith("fechas") || s.startsWith("testigos") ||
                            s.startsWith("registrado") || s.startsWith("verificables") || s.startsWith("con ")) {
                            rows += Triple(tp.first().fontSize, tp.first().xDirAdj, tp.first().yDirAdj)
                        }
                    }
                }.getText(pdf)
                assertTrue(rows.size >= 3, "Story rows are found ($variant).")
                assertTrue(rows.all { kotlin.math.abs(it.first - 7f) < 0.01f }, "The owner's 7 pt ($variant).")
                val column = rows.filter { kotlin.math.abs(it.second - rows.first().second) < 0.5f }
                column.zipWithNext().forEach { (a, b) ->
                    assertTrue(kotlin.math.abs(kotlin.math.abs(b.third - a.third) - 17.01f) < 0.05f,
                        "The golden 17.01 pt row ($variant): ${a.third} ${b.third}")
                }
            }
        }
    }

    @Test
    fun c445EveryLevelDrawsItsMaximumWithTheRealCountOrASpaceAB() {
        // C445 / P128 (owner 2026-11-06): N1..N9 draw 4, 3, 3, 3, 4, 2, 2, 1, 1 empty boxes
        // for any character. Beside them the real count, or a line to write it.
        assertEquals(listOf(4, 3, 3, 3, 4, 2, 2, 1, 1), (1..9).map { io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpellSlotCapacity.maximum(it) })
        assertEquals(listOf(0, 0, 0), (10..12).map { io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpellSlotCapacity.boxes(it, 3) })
        for ((fixture, name) in listOf(
            "01_aldren_vale_srd5_1_champion_fighter.json" to "non-caster",
            "03_mara_siete_umbrales_custom_extended.json" to "caster",
        )) {
            val source = sources(fixture)
            val saved = source.permanent.sheet.spellSlots.associateBy { it.level }
            for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
                val bytes = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source).bytes
                preserveRealApplicationPdf(bytes, "V11-INTERNAL-PROOF-P1-slot-maximum-$name-$variant.pdf")
                Loader.loadPDF(bytes).use { pdf ->
                    val stream = pdf.getPage(0).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                    val squares = Regex("""(?m)(?:^|\s)([\d.]+)\s+([\d.]+)\s+8\.79\d*\s+8\.79\d*\s+re(?:\s|$)""")
                        .findAll(stream).map { it.groupValues[1].toFloat() to it.groupValues[2].toFloat() }.toList()
                    val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
                    for (level in 1..9) {
                        val cx = 270.43f + ((level - 1) % 3) * 101.57f
                        val cy = 168.94f - ((level - 1) / 3) * 18.99f
                        assertEquals(io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpellSlotCapacity.maximum(level), squares.count { (x, y) ->
                            x >= cx + 34f && x + 8f <= cx + 98.74f && kotlin.math.abs(y - (cy + 5.53f)) < 0.3f
                        }, "N$level of $name ($variant).")
                        saved[level]?.let { s ->
                            assertTrue(p1.contains(s.spentSlots.toString() + "/" + s.totalSlots),
                                "The real count prints for N$level of $name ($variant).")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun c443AnyEntryWithAnAttackBonusIsAnAttackRowAB() {
        // C443 / P127 (owner 2026-11-06, «Sí, a ATAQUES»): Mara's bonus actions join ATAQUES
        // in source order; the entries with no bonus stay in ACCIONES.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), mara)
            Loader.loadPDF(proof.bytes).use { pdf ->
                val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1; sortByPosition = true }.getText(pdf)
                val rows = listOf("Técnica 1 — Corte de fase / 60 pies", "Técnica 3 — Ancla causal / 60 pies",
                    "Técnica 5 — Corte de fase / 60 pies", "Técnica 7 — Ancla causal / 60 pies")
                rows.zipWithNext().forEach { (a, b) ->
                    assertTrue(p1.indexOf(a) in 0 until p1.indexOf(b), "$a before $b, on P1 ($variant).")
                }
                assertTrue(p1.contains("4d6 + 4 daño etérico"), "Its damage prints on the row ($variant).")
                val rest = (2..pdf.numberOfPages).joinToString(" ") { page ->
                    PDFTextStripper().apply { startPage = page; endPage = page }.getText(pdf)
                }.replace(Regex("""\s+"""), " ")
                assertFalse(rest.contains("Bono +8"), "It is not repeated as an action ($variant).")
                assertTrue(rest.contains("Técnica 2 — Pulso inverso"), "No bonus: it stays in ACCIONES ($variant).")
                assertTrue(rest.contains("NOTAS DEL ATAQUE Técnica 3"), "Its notes continue in C2 ($variant).")
            }
        }
    }

    @Test
    fun c441CasesTheReviewCouldNotSeeNowHaveARealPdfAB() {
        // C441: the independent review found no PDF for these decisions. Each one now has
        // a preserved PDF and a check of what the decision asks.
        val aldren = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val sheet = aldren.permanent.sheet
        val fighter = sheet.classes.single()
        val fullText: (ByteArray) -> String = { bytes -> Loader.loadPDF(bytes).use {
            PDFTextStripper().getText(it).replace(Regex("""\s+"""), " ") } }
        fun proof(name: String, variant: PcSheetV11Variant,
                  source: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources): ByteArray {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source).bytes
            preserveRealApplicationPdf(bytes, "V11-INTERNAL-PROOF-$name-$variant.pdf")
            return bytes
        }
        fun with(changed: io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet) =
            aldren.copy(permanent = aldren.permanent.copy(sheet = changed))
        // §3.38: no attacks keep the ATAQUES module with its writable rows.
        val noAttacks = with(sheet.copy(combatEntries = sheet.combatEntries.filter {
            !io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11AttackEntries.isAttackRow(it) }))
        // §3.15: more than 20 marks print «____ / X».
        val ammoIds = aldren.permanent.closure.inventoryUsage.filter {
            it.kind == io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind.AMMUNITION
        }.map { it.itemId }.toSet()
        assertTrue(ammoIds.isNotEmpty(), "The fixture has ammunition.")
        val muchAmmo = with(sheet.copy(inventoryItems = sheet.inventoryItems.map {
            if (it.id in ammoIds) it.copy(quantity = 40) else it }))
        // §3.4 and §3.7: two classes on one line, class by class.
        val twoClasses = with(sheet.copy(classes = listOf(fighter, fighter.copy(
            id = Uuid.parse("a7a00000-0000-4000-8000-000000000001"), name = "Mago", level = 3,
            hitDieSides = 6, hitDiceRemaining = 1, sortOrder = fighter.sortOrder + 1))))
        // §3.35: P4 depends on real spells, not on the class.
        val ilyra = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json").permanent.sheet
        val raceSource = io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource(
            id = Uuid.parse("a7a00000-0000-4000-8000-000000000002"), name = "Don de la sangre",
            linkedClassId = null, sortOrder = 0,
            originKind = io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingOriginKind.RACE)
        val raceSpell = with(sheet.copy(spellcastingSources = listOf(raceSource),
            spells = listOf(ilyra.spells.first().copy(sourceAssociations = listOf(
                io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSourceAssociation(raceSource.id, false))))))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val attacks = Loader.loadPDF(proof("no-attacks", variant, noAttacks)).use {
                PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(it) }
            assertTrue(attacks.contains("ATAQUES") && attacks.contains("BON."), "ATAQUES stays ($variant).")
            assertFalse(attacks.contains("Espada larga"), "No attack is invented or kept on P1 ($variant).")
            assertTrue(fullText(proof("ammunition-over-20", variant, muchAmmo)).contains("____ / 40"),
                "More than 20 marks print «____ / X» ($variant).")
            val classes = fullText(proof("two-classes", variant, twoClasses))
            assertTrue(classes.contains("/ Mago 3"), "Two classes with a slash ($variant).")
            assertTrue(classes.contains("5d10 / 3d6"), "Each class total ($variant).")
            val race = fullText(proof("non-caster-race-spell", variant, raceSpell))
            assertTrue(race.contains("P4 - Conjuros"), "A real spell gives P4, whatever the class ($variant).")
            assertTrue(race.contains("Raza"), "The source reads as its type ($variant).")
            assertFalse(race.contains("Don de la sangre"), "No specific name for a non-class source ($variant).")
        }
    }

    @Test
    fun c441FootersOfP2NameEveryDestinationAB() {
        // C441 / F10 (D-0076 3.33, contract C341): P2's foot names every page its modules
        // continue on, the same pages its module band names.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), mara)
            Loader.loadPDF(proof.bytes).use { pdf ->
                val p2 = PDFTextStripper().apply { startPage = 2; endPage = 2 }.getText(pdf)
                    .replace(Regex("""\s+"""), " ")
                val onBand = Regex("""CONTINÚAN? EN PÁG\. (\d+)""").findAll(p2).map { it.groupValues[1].toInt() }.toSet()
                assertTrue(onBand.size >= 2, "The fixture continues on several pages ($variant): $p2")
                val foot = Regex("""CONTINÚA EN PÁG\. ([\d, ]+ Y \d+)""").find(p2)
                assertTrue(foot != null, "The foot names several pages ($variant).")
                val named = Regex("""\d+""").findAll(foot!!.groupValues[1]).map { it.value.toInt() }.toSet()
                assertEquals(onBand, named, "Foot and band name the same pages ($variant).")
            }
        }
    }

    @Test
    fun c440TheSaveBoxShowsTheRealProficiencyAB() {
        // C440 / F02 (contract 2026-10-30 and 2026-11-01): a proficient save is marked
        // with U+E108 in its box; a save that is not proficient keeps the box empty.
        val aldren = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val proficient = io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility.entries
            .count { aldren.permanent.sheet.savingThrow(it).proficient }
        assertTrue(proficient in 1..5, "The fixture has saves of both kinds.")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), aldren)
            Loader.loadPDF(proof.bytes).use { pdf ->
                val marks = glyphs(pdf, 1).count { (ch, x, _) -> ch == "\uE108" && x in 215f..236f }
                assertEquals(proficient, marks, "One mark per proficient save ($variant).")
            }
        }
    }

    @Test
    fun c446P4LevelHeadersDrawTheSameBoxesAsP1() {
        // C446 / P412 (owner 2026-11-06, «Sí, las mismas»): whatever Mara has saved, P4 draws
        // 4, 3, 3, 3, 4, 2, 2, 1, 1 empty squares, one line per header, none pre-marked.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        val slots = mara.permanent.sheet.spellSlots.associateBy { it.level }
        assertEquals(4, slots.getValue(1).totalSlots)
        assertEquals(2, slots.getValue(5).totalSlots)
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT), mara)
        Loader.loadPDF(proof.bytes).use { pdf ->
            val p4 = (1..pdf.numberOfPages).first { page ->
                PDFTextStripper().apply { startPage = page; endPage = page }.getText(pdf).contains("ESP.4 G.2")
            }
            val stream = pdf.getPage(p4 - 1).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
            val squares = Regex("""(?m)(?:^|\s)([0-9.]+) ([0-9.]+) 8\.22\d* 8\.22\d* re(?:\s|$)""")
                .findAll(stream).toList()
            assertTrue(squares.isNotEmpty())
            val text = PDFTextStripper().apply { startPage = p4; endPage = p4 }.getText(pdf)
            assertTrue(text.contains("ESP.2 G.1"))
            // Each header's squares end 8.22 pt inside its column, on its own band.
            val rights = listOf(207.5f, 389.91f, 572.31f)
            val meterSquares = squares.map { it.groupValues[1].toFloat() to it.groupValues[2].toFloat() }
                .filter { (x, _) -> rights.any { right ->
                    (0..3).any { n -> kotlin.math.abs(x - (right - 16.44f - n * 13.04f)) < 0.03f } } }
            val total = listOf(4, 3, 3, 3, 4, 2, 2, 1, 1).sum()
            // Preparation squares sit at the left of a column, never at these positions.
            assertEquals(total, meterSquares.size, "The maximum squares of N1..N9 on Mara's P4.")
        }
    }

    @Test
    fun c440SourceLabelsAndValuesFollowTheOwnerOnP1AndP4AB() {
        // C440 / F03, F04 (contract 2026-10-26, 2026-10-28): a class source reads as its
        // class, no internal ID is printed, and P4 always keeps the value of each source.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        val className = mara.permanent.sheet.classes.first().name
        val own = mara.permanent.sheet.spellcastingSources.first { it.linkedClassId != null }
        assertTrue(className != own.name, "The fixture's source has a name of its own.")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), mara)
            Loader.loadPDF(proof.bytes).use { pdf ->
                val p4 = (1..pdf.numberOfPages).map { page ->
                    PDFTextStripper().apply { startPage = page; endPage = page }.getText(pdf)
                }.filter { it.contains("P4 - Conjuros") }.joinToString(" ").replace(Regex("\\s+"), " ")
                assertTrue(p4.contains(className), "The class name labels the rows ($variant).")
                assertFalse(p4.contains(own.name), "The source's own name is not printed ($variant).")
                assertFalse(p4.contains("(#"), "No internal ID ($variant).")
                assertFalse(p4.contains("SEGÚN FUENTE"), "P4 keeps the detail ($variant).")
            }
        }
        // Six differing sources still print every value on P4, in the 29.77 pt card.
        val base = mara.permanent.sheet
        val source = base.spellcastingSources.first()
        val extra = (1..5).map { i ->
            source.copy(id = Uuid.parse("a7900000-0000-4000-8000-00000000000" + i), name = "Fuente " + i,
                linkedClassId = null,
                originKind = io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingOriginKind.FEAT,
                sortOrder = source.sortOrder + i)
        }
        val spell = base.spells.first { it.sourceAssociations.isNotEmpty() }
        val many = mara.copy(permanent = mara.permanent.copy(
            sheet = base.copy(
                spellcastingSources = base.spellcastingSources + extra,
                spells = base.spells.map {
                    if (it.id == spell.id) it.copy(sourceAssociations = it.sourceAssociations + extra.map { s ->
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSourceAssociation(s.id, false)
                    }) else it
                },
            ),
            successor = mara.permanent.successor.copy(
                spellcastingProfiles = mara.permanent.successor.spellcastingProfiles + extra.mapIndexed { i, it ->
                    io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingProfile(
                        sourceId = it.id, legacySaveDcOverride = 10 + i)
                },
            ),
        ))
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT), many)
        preserveRealApplicationPdf(proof.bytes, "V11-INTERNAL-PROOF-P4-six-sources-A.pdf")
        Loader.loadPDF(proof.bytes).use { pdf ->
            val p4 = (1..pdf.numberOfPages).map { page ->
                PDFTextStripper().apply { startPage = page; endPage = page }.getText(pdf)
            }.filter { it.contains("P4 - Conjuros") }.joinToString(" ").replace(Regex("\\s+"), " ")
            assertFalse(p4.contains("SEGÚN FUENTE"), "Six sources still print their values.")
            for (dc in 10..14) assertTrue(p4.contains("Dote $dc"), "The value of Dote $dc prints.")
        }
    }

    @Test
    fun c440C2SkillRowsUseTheGoldenRowAB() {
        // C440 / F05 (contract 2026-10-28): box with the real state, name, signed bold value;
        // no «Habilidad» prefix and no training word on the line.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), mara)
            Loader.loadPDF(proof.bytes).use { pdf ->
                val full = PDFTextStripper().getText(pdf).replace(Regex("\\s+"), " ")
                assertFalse(full.contains("Habilidad "), "No «Habilidad» prefix ($variant).")
                assertFalse(Regex(" / [0-9]+ / (Competencia|Pericia|Sin competencia)").containsMatchIn(full),
                    "No prose row ($variant).")
            }
        }
    }

    @Test
    fun c440SkillsFollowTheGoldenOrderAndCustomAttributesAreCapitalised() {
        // C440 / G-a, G-b: golden order within each attribute; SUERTE, CORDURA, HONOR in capitals.
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT), mara)
        Loader.loadPDF(proof.bytes).use { pdf ->
            val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1; sortByPosition = true }.getText(pdf)
            listOf("Medicina", "Percepción", "Perspicacia", "Supervivencia", "Trato con Animales").zipWithNext()
                .forEach { (a, b) -> assertTrue(p1.indexOf(a) in 0 until p1.indexOf(b), "$a before $b") }
            listOf("Engaño", "Interpretación", "Intimidación", "Persuasión").zipWithNext()
                .forEach { (a, b) -> assertTrue(p1.indexOf(a) in 0 until p1.indexOf(b), "$a before $b") }
            assertTrue(p1.contains("CORDURA"), "Custom attribute names print in capitals.")
            assertFalse(p1.contains("Cordura"), "Not in mixed case.")
        }
    }

    @Test
    fun c440AReachOnTheP1RowIsNotRepeatedInC2() {
        // C440 / F06 (D-0076 3.31 «nada se omite, duplica») and P126 (owner 2026-11-06,
        // «Nombre y alcance»): the P1 row prints both, and C2 does not repeat the reach.
        val aldren = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT), aldren)
        Loader.loadPDF(proof.bytes).use { pdf ->
            val full = PDFTextStripper().getText(pdf).replace(Regex("\\s+"), " ")
            val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
            assertTrue(p1.contains("Espada larga / 5 pies"), "The P1 row prints name and reach.")
            assertTrue(p1.contains("Ballesta ligera / 80/320 pies"), "The P1 row prints name and reach.")
            assertTrue(full.contains("80/320"), "The reach prints.")
            assertEquals(1, Regex("80/320").findAll(full).count(), "Once.")
        }
    }

    @Test
    fun c440EveryPageEmbedsOnlyTheLogoImage() {
        // C440 / F14: the owner's old sheet is no longer inside the PDF, clipped or not.
        val proof = DesktopPcSheetExportService().generateV11Proof(
            PcSheetV11ExportRequest(PcSheetV11Variant.A, PcSheetExportStateSelection.PERMANENT),
            sources("01_aldren_vale_srd5_1_champion_fighter.json"))
        Loader.loadPDF(proof.bytes).use { pdf ->
            val resources = pdf.getPage(0).resources
            val image = resources.xObjectNames.map { resources.getXObject(it) }
                .filterIsInstance<org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject>().single()
            assertEquals(344, image.width)
            assertEquals(134, image.height)
        }
    }

    @Test
    fun c438SavedBackgroundImagesLeaveTheSilhouetteOnP1AB() {
        // C438 / P306 (owner 2026-11-05, «No, deja la silueta»): images saved in the Trasfondo
        // tab never reach the PDF. With no assigned portrait, P1 keeps the silhouette.
        val base = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val pixel = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII="
        val saved = listOf(
            io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackgroundImageSlot.PRIMARY to "a7800000-0000-4000-8000-000000000001",
            io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackgroundImageSlot.SECONDARY to "a7800000-0000-4000-8000-000000000002",
        ).map { (slot, id) ->
            io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackgroundImage(Uuid.parse(id), slot, "image/png", pixel)
        }
        val withImages = base.copy(permanent = base.permanent.copy(
            closure = base.permanent.closure.copy(portraitRef = null),
            successor = base.permanent.successor.copy(backgroundImages = saved),
        ))
        fun images(resources: org.apache.pdfbox.pdmodel.PDResources?): Int =
            resources?.xObjectNames?.sumOf { name ->
                when (val x = resources.getXObject(name)) {
                    is org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject -> 1
                    is org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject -> images(x.resources)
                    else -> 0
                }
            } ?: 0
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val generated = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), withImages,
            )
            assertFalse(generated.semantic.portrait.locallyAvailable, "No portrait is taken from them ($variant).")
            preserveRealApplicationPdf(generated.bytes, "V11-INTERNAL-PROOF-background-images-no-portrait-$variant.pdf")
            Loader.loadPDF(generated.bytes).use { pdf ->
                for (index in 0 until pdf.numberOfPages) {
                    assertEquals(1, images(pdf.getPage(index).resources),
                        "Page ${index + 1} carries only the header logo ($variant).")
                }
                val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
                assertTrue(p1.contains("RETRATO"), "P1 keeps its portrait frame ($variant).")
            }
        }
    }

    @Test
    fun c431TitledNotesPrintBeforeTheGeneralNotesAB() {
        // C431 / D-0076 3.28: titled notes hold the main area, general notes follow them.
        val source = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            Loader.loadPDF(DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source,
            ).bytes).use { pdf ->
                val notes = (1..pdf.numberOfPages).map { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                }.filter { it.contains("C-N3 - Notas digitales") }.joinToString("\n").replace(Regex("""\s+"""), " ")
                val general = source.permanent.sheet.generalNotes.replace(Regex("""\s+"""), " ").take(40)
                val lastTitle = source.permanent.sheet.noteCards.maxBy { it.sortOrder }.title
                assertTrue(notes.contains(general) && notes.contains(lastTitle), "Both kinds of note print ($variant).")
                assertTrue(notes.indexOf(lastTitle) < notes.indexOf(general), "Titled notes come first ($variant).")
            }
        }
    }

    @Test
    fun c432TheRaceFieldPrintsTheSubraceWhenOneExistsAB() {
        // C432 / D-0076 3.4: `Elfo — Alto elfo`, from the character's own identities.
        val original = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val species = io.github.mrsimkin.dndcustomaid.shared.character.CharacterOwnedSpeciesIdentity(
            Uuid.parse("a7500000-0000-4000-8000-000000000001"), "Elfo")
        val subrace = io.github.mrsimkin.dndcustomaid.shared.character.CharacterOwnedSubraceIdentity(
            Uuid.parse("a7500000-0000-4000-8000-000000000002"), species.id, "Alto elfo")
        val source = original.copy(permanent = original.permanent.copy(successor = original.permanent.successor.copy(
            speciesIdentity = species, subraceIdentity = subrace)))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            Loader.loadPDF(DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source,
            ).bytes).use { pdf ->
                val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf).replace(Regex("""\s+"""), " ")
                assertTrue(p1.contains("Elfo — Alto elfo"), "Race with its subrace ($variant).")
            }
        }
    }

    @Test
    fun c433ASecondAmmunitionEntryPrintsOnP1WhenOneSubRowHoldsItsMarksAB() {
        // C433 / D-0076 3.15 and the golden: a second entry 34 pt under the first, on its own band;
        // with 11 to 20 marks it continues whole in C2, because its lower sub-row would be hidden.
        val aldren = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val usage = aldren.permanent.closure.inventoryUsage.single {
            it.kind == io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind.AMMUNITION
        }
        val template = aldren.permanent.sheet.inventoryItems.first { it.id == usage.itemId }
        fun withSecond(quantity: Int): io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources {
            val id = Uuid.parse("a7600000-0000-4000-8000-000000000001")
            return aldren.copy(permanent = aldren.permanent.copy(
                sheet = aldren.permanent.sheet.copy(inventoryItems = aldren.permanent.sheet.inventoryItems +
                    template.copy(id = id, name = "QAFLECHAS", quantity = quantity, sortOrder = template.sortOrder + 1)),
                closure = aldren.permanent.closure.copy(inventoryUsage = aldren.permanent.closure.inventoryUsage +
                    usage.copy(itemId = id)),
            ))
        }
        val number = """(-?\d+(?:\.\d+)?)"""
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), withSecond(8)).bytes
            preserveRealApplicationPdf(bytes, "V11-INTERNAL-PROOF-P1-two-ammo-entries-" + variant.name + ".pdf")
            Loader.loadPDF(bytes).use { pdf ->
                val p1 = PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
                assertTrue(p1.contains("Virotes") && p1.contains("QAFLECHAS"), "Both entries on P1 ($variant).")
                val ops = pdf.getPage(0).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                val marks = Regex("""(?:^|\s)$number\s+$number\s+7\.94\s+7\.94\s+re(?:\s|$)""")
                    .findAll(ops).map { it.groupValues[2].toFloat() }.toList()
                assertEquals(8, marks.count { kotlin.math.abs(it - 60.81f) < 0.05f }, "Eight marks on its one sub-row ($variant).")
                assertEquals(1, PDFTextStripper().getText(pdf).split("MUNICIÓN QAFLECHAS").size - 1,
                    "Its details print once in C2 ($variant).")
            }
            Loader.loadPDF(DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), withSecond(15)).bytes).use { pdf ->
                assertFalse(PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf).contains("QAFLECHAS"),
                    "With 15 marks it does not stay on P1 ($variant).")
                assertEquals(1, PDFTextStripper().getText(pdf).split("MUNICIÓN QAFLECHAS").size - 1,
                    "It continues once in C2 ($variant).")
            }
        }
    }

    @Test
    fun c429ALevelTitleNeverStaysAloneAtAColumnFootAB() {
        // C429: every level title has at least one row under it in its own column.
        for (fixture in listOf("02_ilyra_quill_srd5_2_1_evoker_wizard.json", "03_mara_siete_umbrales_custom_extended.json")) {
            for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
                val bytes = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), sources(fixture),
                ).bytes
                Loader.loadPDF(bytes).use { pdf ->
                    (1..pdf.numberOfPages).forEach { n ->
                        val text = PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
                        if (!text.contains("P4 - Conjuros /")) return@forEach
                        val titles = mutableListOf<Pair<Float, Float>>()
                        object : PDFTextStripper() {
                            override fun writeString(value: String, positions: MutableList<org.apache.pdfbox.text.TextPosition>) {
                                val first = positions.first()
                                if (Regex("""^(NIVEL \d+|TRUCOS 0)\b""").containsMatchIn(value.trim()) && !value.contains("/"))
                                    titles += first.xDirAdj to 792f - first.yDirAdj
                            }
                        }.apply { startPage = n; endPage = n }.getText(pdf)
                        assertTrue(titles.isNotEmpty())
                        titles.forEach { (x, y) ->
                            assertTrue(y > 64.92f + 20.69f, "Title at x=$x y=$y has a row below it ($fixture, $variant).")
                        }
                    }
                }
            }
        }
    }

    @Test
    fun c419C2SpecialTableKeepsItsFourColumnsWithoutATitleRowAB() {
        // C419: golden C2 EQUIPO ESPECIAL - rows start right under the cartela, with the
        // three column rules and one paper ACTIVO box per record, and no title row.
        val number = """(-?\d+(?:\.\d+)?)"""
        fun near(a: Float, b: Float) = kotlin.math.abs(a - b) < 0.05f
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources("03_mara_siete_umbrales_custom_extended.json"),
            ).bytes
            Loader.loadPDF(bytes).use { pdf ->
                val pages = (0 until pdf.numberOfPages).filter { i ->
                    PDFTextStripper().apply { startPage = i + 1; endPage = i + 1 }.getText(pdf)
                        .let { it.contains("C2 - Equipo especial") || it.contains("+ EQUIPO ESPECIAL") }
                }
                assertTrue(pages.isNotEmpty(), "Mara has a C2 special page ($variant).")
                pages.forEach { i ->
                    val text = PDFTextStripper().apply { startPage = i + 1; endPage = i + 1 }.getText(pdf)
                    assertFalse(text.contains("UBICACIÓN") || text.contains("DESCRIPCIÓN"),
                        "No title row ($variant).")
                    val ops = pdf.getPage(i).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                    val verticals = Regex("""(?:^|\s)$number\s+$number\s+m\s+$number\s+$number\s+l\s+S""")
                        .findAll(ops).map { m -> m.groupValues.drop(1).map { it.toFloat() } }
                        .filter { (x0, _, x1) -> near(x0, x1) }.toList()
                    for (x in listOf(146.21f, 279.37f, 527.04f)) {
                        assertTrue(verticals.any { (x0, y0, _, y1) -> near(x0, x) && kotlin.math.abs(y1 - y0) >= 26f },
                            "Column rule at $x runs along its rows ($variant).")
                    }
                    val boxes = Regex("""(?:^|\s)$number\s+$number\s+$number\s+$number\s+re(?:\s|$)""")
                        .findAll(ops).map { m -> m.groupValues.drop(1).map { it.toFloat() } }
                        .count { (x, _, w, h) -> near(x, 545.28f) && near(w, 8.79f) && near(h, 8.79f) }
                    assertTrue(boxes >= 1, "One ACTIVO paper box per record ($variant).")
                }
            }
        }
    }

    @Test
    fun c417P4HasThreeRealColumnsWithoutLargeGapsForFewAndManySpellsAB() {
        // C417 / P402: with few or many real spells, every P4 page fills its three golden
        // columns with continuous rows down to the foot; a free column ends in its NOTAS grid.
        val number = """(-?\d+(?:\.\d+)?)"""
        val ilyra = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
        val base = ilyra.permanent.sheet.spells.first { it.sourceAssociations.isNotEmpty() }
        val many = ilyra.copy(permanent = ilyra.permanent.copy(sheet = ilyra.permanent.sheet.copy(
            spells = (1..175).map { n -> base.copy(
                id = Uuid.parse("f7100000-0000-4000-8000-" + n.toString().padStart(12, '0')),
                name = "QAP4COL" + n.toString().padStart(3, '0'), level = 0, sortOrder = n) })))
        val cases = listOf("few" to ilyra, "custom" to sources("03_mara_siete_umbrales_custom_extended.json"), "many" to many)
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            for ((label, source) in cases) {
                val bytes = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source,
                ).bytes
                Loader.loadPDF(bytes).use { pdf ->
                    val p4 = (0 until pdf.numberOfPages).filter { i ->
                        PDFTextStripper().apply { startPage = i + 1; endPage = i + 1 }.getText(pdf)
                            .contains("P4 - Conjuros /")
                    }
                    assertTrue(p4.isNotEmpty(), "A caster has P4 ($label, $variant).")
                    p4.forEach { i ->
                        val ops = pdf.getPage(i).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                        val rects = Regex("""(?:^|\s)$number\s+$number\s+$number\s+$number\s+re(?:\s|$)""")
                            .findAll(ops).map { m -> m.groupValues.drop(1).map { it.toFloat() } }.toList()
                        for (column in listOf(39.69f, 222.09f, 404.5f)) {
                            val bands = rects.filter { (x, _, w, h) ->
                                kotlin.math.abs(x - column) < 0.3f && kotlin.math.abs(w - 167.81f) < 0.3f && h > 5f
                            }.sortedByDescending { (_, y, _, h) -> y + h }
                            assertTrue(bands.size >= 20, "Column $column holds real rows, page ${i + 1} ($label, $variant).")
                            assertTrue(bands.minOf { (_, y) -> y } < 90f, "Column $column reaches the foot ($label, $variant).")
                            bands.zipWithNext().forEach { (upper, lower) ->
                                val gap = upper[1] - (lower[1] + lower[3])
                                // The only taller step is the NOTAS cartela sitting on its grid frame.
                                assertTrue(gap < 3f || (gap < 25f && lower[3] > 60f),
                                    "Gap of $gap pt in column $column, page ${i + 1} ($label, $variant).")
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun c417OverflowPaginatesOnePdfWithoutASecondExportOptionAB() {
        // C417 / IN08: the same request shape serves ordinary and overflowing sheets; the
        // only difference in the one returned PDF is its page count.
        val aldren = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val mara = sources("03_mara_siete_umbrales_custom_extended.json")
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val request = PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT)
            val ordinary = DesktopPcSheetExportService().generateV11Proof(request, aldren)
            val overflowing = DesktopPcSheetExportService().generateV11Proof(request, mara)
            Loader.loadPDF(ordinary.bytes).use { small ->
                Loader.loadPDF(overflowing.bytes).use { large ->
                    assertTrue(large.numberOfPages > small.numberOfPages,
                        "Overflow is handled by automatic pagination ($variant).")
                    listOf(small, large).forEach { pdf ->
                        assertTrue(PDFTextStripper().apply { startPage = 1; endPage = 1 }.getText(pdf)
                            .contains("VARIANTE " + variant.name), "Same chosen variant in both ($variant).")
                        assertTrue(PDFTextStripper().apply {
                            startPage = pdf.numberOfPages; endPage = pdf.numberOfPages
                        }.getText(pdf).lineSequence().any { it.trim() == "NOTAS" },
                            "Both end on the same final NOTAS page ($variant).")
                    }
                }
            }
        }
    }

    @Test
    fun c415LongCustomAttributeNamesWrapInsideTheOwnerRowAB() {
        // C415: a long custom attribute name no longer falls back to inline numbers
        // with outlines drawn over them: it is the owner row, name beside the oval.
        val original = sources("03_mara_siete_umbrales_custom_extended.json")
        val template = original.permanent.successor.customAttributes.first { it.name == "Fortuna" }
        val longName = "QALARGO extensible adicional para dos renglones medidos en la fila"
        val veryLong = "QAMUYLARGO " + List(16) { "palabra$it" }.joinToString(" ")
        val added = listOf(longName, veryLong).mapIndexed { i, name ->
            template.copy(id = Uuid.parse("a1500000-0000-4000-8000-" + (i + 1).toString().padStart(12, '0')),
                name = name, sortOrder = 2000 + i)
        }
        val source = original.copy(permanent = original.permanent.copy(
            successor = original.permanent.successor.copy(
                customAttributes = original.permanent.successor.customAttributes + added)))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source,
            ).bytes
            preserveRealApplicationPdf(bytes, "V11-INTERNAL-PROOF-C2-long-attribute-name-" + variant + ".pdf")
            Loader.loadPDF(bytes).use { pdf ->
                val text = PDFTextStripper().getText(pdf)
                assertFalse(text.contains(" / mod "), "No inline score text remains ($variant).")
                val flat = text.replace(Regex("""\s+"""), " ")
                longName.split(' ').plus(veryLong.split(' ')).forEach { word ->
                    assertTrue(flat.contains(word.uppercase()), "Every word of the saved name prints: $word ($variant).")
                }
                val page = (1..pdf.numberOfPages).first { n ->
                    PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf).contains("QALARGO")
                }
                // Every name line starts beside the oval, never over the box.
                val xs = mutableListOf<Float>()
                object : PDFTextStripper() {
                    override fun writeString(text: String, positions: MutableList<org.apache.pdfbox.text.TextPosition>) {
                        if (text.startsWith("QALARGO")) xs += positions.first().xDirAdj
                    }
                }.apply { startPage = page; endPage = page }.getText(pdf)
                assertEquals(1, xs.size, "The name starts once ($variant).")
                val columnLeft = if (xs.single() < 306f) 39.69f else 310.96f
                assertTrue(kotlin.math.abs(xs.single() - (columnLeft + 76.46f)) < 0.6f,
                    "Name beside the oval at ${xs.single()} ($variant).")
            }
        }
    }

    @Test
    fun c414C2TablesAreGoldenBlocksWithGoldenRowsAndPerRecordSeparatorsAB() {
        // C414: guards the C2 EQUIPO table on real App PDFs - two golden blocks at the
        // C2 margins, 15.87 pt rows, no frame, a short separator per printed record.
        val number = """(-?\d+(?:\.\d+)?)"""
        fun near(a: Float, b: Float) = kotlin.math.abs(a - b) < 0.05f
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            val bytes = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources("03_mara_siete_umbrales_custom_extended.json"),
            ).bytes
            Loader.loadPDF(bytes).use { pdf ->
                val pages = (0 until pdf.numberOfPages).filter { i ->
                    PDFTextStripper().apply { startPage = i + 1; endPage = i + 1 }.getText(pdf)
                        .contains("C2 - Equipo y tesoro")
                }
                assertTrue(pages.isNotEmpty(), "Mara has a C2 equipment page ($variant).")
                pages.forEach { i ->
                    val ops = pdf.getPage(i).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                    val rects = Regex("""(?:^|\s)$number\s+$number\s+$number\s+$number\s+re(?:\s|$)""")
                        .findAll(ops).map { m -> m.groupValues.drop(1).map { it.toFloat() } }.toList()
                    val left = rects.filter { (x, _, w, h) -> near(x, 39.69f) && near(w, 261.35f) && near(h, 15.87f) }
                    val right = rects.filter { (x, _, w, h) -> near(x, 310.96f) && near(w, 261.36f) && near(h, 15.87f) }
                    assertTrue(left.isNotEmpty() && right.isNotEmpty(),
                        "Two golden blocks of 15.87 pt rows on page ${i + 1} ($variant).")
                    assertFalse(rects.any { (x, y, w, h) -> near(x, 39.69f) && near(w, 532.63f) && h > 100f && y + h > 655f },
                        "No frame around the C2 table ($variant).")
                    val separators = Regex("""(?:^|\s)$number\s+$number\s+m\s+$number\s+$number\s+l\s+S""")
                        .findAll(ops).map { m -> m.groupValues.drop(1).map { it.toFloat() } }
                        .filter { (x0, y0, x1, y1) ->
                            near(x0, x1) && kotlin.math.abs(kotlin.math.abs(y1 - y0) - 15.87f) < 0.05f
                        }.toList()
                    assertTrue(separators.isNotEmpty() &&
                        separators.all { (x) -> near(x, 268.87f) || near(x, 540.15f) },
                        "Short separators 32.17 pt inside each block ($variant): $separators")
                    assertFalse(Regex("""(?:^|\s)$number\s+$number\s+m\s+$number\s+$number\s+l\s+S""")
                        .findAll(ops).map { m -> m.groupValues.drop(1).map { it.toFloat() } }
                        .any { (x0, y0, x1, y1) -> near(x0, x1) && (near(x0, 258f) || near(x0, 306f) || near(x0, 528f)) &&
                            kotlin.math.abs(y1 - y0) > 40f },
                        "No long table verticals remain ($variant).")
                }
            }
        }
    }

    @Test
    fun c406GoldenNotesPanelsOnCn3TheLibroIndexAndAContinuedP4AB() {
        // C406: guards C403-C405 on real App PDFs - the C-N3 NOTAS MANUSCRITAS reserve
        // (cartela over banded 18.71 pt rows), the Libro index (cartela + one banded
        // 15.7 pt row per spell) and the NOTAS panel in a free column of a continued P4.
        val number = """(-?\d+(?:\.\d+)?)"""
        fun rects(pdf: org.apache.pdfbox.pdmodel.PDDocument, page: Int) =
            Regex("""(?:^|\s)$number\s+$number\s+$number\s+$number\s+re(?:\s|$)""")
                .findAll(pdf.getPage(page).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) })
                .map { m -> m.groupValues.drop(1).map { it.toFloat() } }.toList()
        fun near(a: Float, b: Float) = kotlin.math.abs(a - b) < 0.05f
        fun pageTexts(pdf: org.apache.pdfbox.pdmodel.PDDocument) = (1..pdf.numberOfPages).map { n ->
            PDFTextStripper().apply { startPage = n; endPage = n }.getText(pdf)
        }
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            // C-N3 (Mara): the last notes page carries the golden reserve.
            DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT),
                sources("03_mara_siete_umbrales_custom_extended.json"),
            ).bytes.let { bytes -> Loader.loadPDF(bytes).use { pdf ->
                val texts = pageTexts(pdf)
                val last = texts.indices.last { texts[it].contains("C-N3 - Notas digitales") }
                assertTrue(texts[last].contains("NOTAS MANUSCRITAS"), "C-N3 reserve cartela ($variant).")
                assertTrue(rects(pdf, last).count { (x, _, w, h) -> near(x, 39.69f) && near(w, 532.63f) && near(h, 18.71f) } >= 2,
                    "C-N3 reserve uses banded 18.71 pt golden rows ($variant).")
            } }
            // Libro index (Ilyra with the Libro): one banded row per spell.
            val ilyra = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT, includeSpellDescriptions = true),
                sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json"),
            )
            Loader.loadPDF(ilyra.bytes).use { pdf ->
                val texts = pageTexts(pdf)
                val index = texts.indices.filter { texts[it].contains("Índice del Libro de Conjuros") }
                assertTrue(index.isNotEmpty() && index.all { texts[it].contains("ÍNDICE DEL LIBRO DE CONJUROS") },
                    "Every index page carries its golden cartela ($variant).")
                val bands = index.sumOf { i -> rects(pdf, i).count { (_, _, _, h) -> near(h, 15.7f) } }
                assertEquals(ilyra.semantic.spellbookEntries!!.size, bands, "One banded 15.7 pt row per spell ($variant).")
            }
            // A continued P4 (175 cantrips): the free last column holds a NOTAS panel.
            val original = sources("02_ilyra_quill_srd5_2_1_evoker_wizard.json")
            val base = original.permanent.sheet.spells.first { it.sourceAssociations.isNotEmpty() }
            val stress = original.copy(permanent = original.permanent.copy(sheet = original.permanent.sheet.copy(
                spells = (1..175).map { n -> base.copy(
                    id = Uuid.parse("f7000000-0000-4000-8000-" + n.toString().padStart(12, '0')),
                    name = "QAP4NOTA" + n.toString().padStart(3, '0'), level = 0, sortOrder = n) })))
            DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), stress,
            ).bytes.let { bytes -> Loader.loadPDF(bytes).use { pdf ->
                val texts = pageTexts(pdf)
                val lastP4 = texts.indices.last { texts[it].contains("P4 - Conjuros /") }
                val column3 = PDFTextStripperByArea().apply {
                    addRegion("c3", java.awt.geom.Rectangle2D.Float(404f, 150f, 170f, 590f))
                    extractRegions(pdf.getPage(lastP4))
                }.getTextForRegion("c3")
                assertTrue(column3.lines().any { it.trim() == "NOTAS" }, "The free P4 column gets its NOTAS cartela ($variant).")
                assertTrue(rects(pdf, lastP4).any { (x, y, w, h) -> near(x, 404.5f) && kotlin.math.abs(w - 167.81f) < 0.1f &&
                    y < 64f && h > 60f }, "Its golden grid frame reaches the page foot ($variant).")
            } }
        }
    }

    @Test
    fun c408P2TablesAreGoldenBlocksWithASeparatorOnlyOnRowsWithARecordAB() {
        // C408: the golden P2 tables are separate blocks with no frame or long vertical
        // rules; a short quantity separator appears only on rows that hold a record.
        val number = """(-?\d+(?:\.\d+)?)"""
        fun separators(ops: String) = Regex("""(?:^|\s)$number\s+$number\s+m\s+$number\s+$number\s+l(?:\s|$)""")
            .findAll(ops).map { m -> m.groupValues.drop(1).map { it.toFloat() } }.filter { (x0, y0, x1, y1) ->
                kotlin.math.abs(x0 - x1) < 0.01f && y1 - y0 in 10f..25f &&
                    listOf(134.61f, 266.85f, 130.81f, 263.05f).any { kotlin.math.abs(it - x0) < 0.05f }
            }.toList()
        val aldren = sources("01_aldren_vale_srd5_1_champion_fighter.json")
        val emptied = aldren.copy(permanent = aldren.permanent.copy(sheet = aldren.permanent.sheet.copy(
            inventoryItems = emptyList(), currencies = emptyList())))
        for (variant in listOf(PcSheetV11Variant.A, PcSheetV11Variant.B)) {
            for ((label, source) in listOf("Mara" to sources("03_mara_siete_umbrales_custom_extended.json"), "vacía" to emptied)) {
                val bytes = DesktopPcSheetExportService().generateV11Proof(
                    PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), source).bytes
                Loader.loadPDF(bytes).use { pdf ->
                    val ops = pdf.getPage(1).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                    val blocks = Regex("""(?:^|\s)$number\s+$number\s+$number\s+$number\s+re(?:\s|$)""").findAll(ops)
                        .map { m -> m.groupValues.drop(1).map { it.toFloat() } }.toList()
                    for ((x, w) in listOf(39.69f to 126.56f, 171.92f to 126.57f, 309.26f to 129.54f, 442.77f to 129.55f)) {
                        assertTrue(blocks.any { (bx, _, bw, _) -> kotlin.math.abs(bx - x) < 0.05f && kotlin.math.abs(bw - w) < 0.05f },
                            "Golden P2 block at x$x ($label $variant).")
                    }
                    if (label == "vacía") {
                        assertEquals(0, separators(ops).size, "An empty table has no separators ($variant).")
                    } else {
                        assertTrue(separators(ops).size >= 8, "Rows with records carry their separator ($variant).")
                        val qtyDigits = PDFTextStripperByArea().apply {
                            addRegion("q", java.awt.geom.Rectangle2D.Float(135f, 121f, 34f, 370f))
                            extractRegions(pdf.getPage(1))
                        }.getTextForRegion("q").split(Regex("""\s+""")).count { it.isNotBlank() && it.all(Char::isDigit) }
                        val leftSeparators = separators(ops).count { (x, _, _, _) -> x < 170f }
                        assertEquals(qtyDigits, leftSeparators, "One separator per left quantity ($variant).")
                    }
                }
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
                var bands = 0
                for (index in 0 until pdf.numberOfPages) {
                    val ops = pdf.getPage(index).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                    assertTrue(footRule.containsMatchIn(ops), "Golden foot rule on page ${index + 1} ($variant).")
                    // C367: the golden header rule at y709.8 (was y717) on every page.
                    assertTrue(Regex("""(?:^|\s)39\.69\d*\s+709\.(?:8|79)\d*\s+m\s+572\.32\d*\s+709\.(?:8|79)\d*\s+l""")
                        .containsMatchIn(ops), "Golden header rule at y709.8 on page ${index + 1} ($variant).")
                    assertFalse(Regex("""(?:^|\s)39\.69\d*\s+717\s+m""").containsMatchIn(ops),
                        "No pre-golden y717 header rule on page ${index + 1} ($variant).")
                    // C364: no stray pale rule under the right half of the footer.
                    assertFalse(Regex("""(?:^|\s)270\.43\d*\s+37\.28\d*\s+m""").containsMatchIn(ops),
                        "No non-golden y37.28 footer rule on page ${index + 1} ($variant).")
                    // C364: as in the golden, the module-link band covers the foot rule.
                    Regex("""(?:^|\s)39\.6\d*\s+43\.9\d*\s+532\.6\d*\s+13\.0\d*\s+re""")
                        .find(ops)?.let { band ->
                            bands++
                            assertTrue(footRule.findAll(ops).all { it.range.first < band.range.first },
                                "The link band is painted over the foot rule on page ${index + 1} ($variant).")
                        }
                }
                assertTrue(bands >= 1, "Mara prints at least one module-link band ($variant).")
                val last = PDFTextStripper().apply {
                    startPage = pdf.numberOfPages
                    endPage = pdf.numberOfPages
                }.getText(pdf)
                assertFalse(Regex("""\d+\s*/\s*\d+""").containsMatchIn(last),
                    "The final NOTAS sheet prints no page number ($variant).")
                // C340: every page but the final NOTAS prints its bare physical
                // number in the golden counter slot; none prints «NN / NN».
                for (page in 1..pdf.numberOfPages) {
                    val text = PDFTextStripper().apply { startPage = page; endPage = page }.getText(pdf)
                    assertFalse(Regex("""\b\d{2} / \d{2}\b""").containsMatchIn(text),
                        "Page $page prints no «NN / NN» counter ($variant).")
                    val slot = PDFTextStripperByArea().apply {
                        addRegion("n", java.awt.geom.Rectangle2D.Float(544f, 750f, 30f, 14f))
                        extractRegions(pdf.getPage(page - 1))
                    }.getTextForRegion("n").trim()
                    if (page < pdf.numberOfPages) {
                        assertEquals(page.toString(), slot, "Page $page prints its bare number ($variant).")
                    } else {
                        assertEquals("", slot, "The final NOTAS sheet prints no page number ($variant).")
                    }
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
                // C250/C255: golden cells; boxes never pre-marked, with the real
                // "gastados/total" count where the sheet has one.
                val native = pdf.getPage(0).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                val boxes = Regex(
                    """(?m)(?:^|\s)([\d.]+)\s+([\d.]+)\s+(8(?:\.0|\.79\d*|\.789\d*)?)\s+(8(?:\.0|\.79\d*|\.789\d*)?)\s+re(?:\s|$)""", // C413
                ).findAll(native).map { it.groupValues[1].toFloat() to it.groupValues[2].toFloat() }.toList()
                for (level in 1..9) {
                    val cellX = 270.43f + ((level - 1) % 3) * 101.57f // C351 golden
                    val cellY = 168.94f - ((level - 1) / 3) * 18.99f // C356 golden
                    assertTrue(words.any { it.first == "N$level" && near(it, cellX + 5.1f, cellY + 5.96f) },
                        "P1 must print N$level at its fixed 3x3 grid cell, $variant")
                    val inCell = words.filter {
                        it.third in cellY..cellY + 18.99f &&
                            it.second > cellX + 15f && it.second < cellX + 98.74f // C351 golden card
                    }
                    val cellBoxes = boxes.count { (x, y) ->
                        x >= cellX + 34f && x + 8f <= cellX + 98.74f && kotlin.math.abs(y - (cellY + 5.7f)) < 0.3f // C413 golden
                    }
                    val slot = real[level]
                    // C445 (owner 2026-11-06): every level draws its maximum, real data or not.
                    assertEquals(io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpellSlotCapacity.maximum(level), cellBoxes,
                        "P1 N$level draws the most boxes the level could have.")
                    if (slot == null) {
                        assertTrue(inCell.isEmpty(),
                            "P1 must not invent total/spent for absent level $level: $inCell")
                    } else {
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
                    """(?s)(?:^|\s)275\.(?:43|429+)\d*\s+219\.(?:68|679+)\d*\s+m\s+567\.(?:32|319+)\d*\s+219\.(?:68|679+)\d*\s+l\s+572\.(?:32|319+)\d*\s+224\.(?:68|679+)\d*\s+l""" /* C356 golden */ +
                        """.*?\s+h\s+B(?:\s|$)"""
                )
                assertEquals(1, clipped.findAll(ops).count(),
                    "Expected one genuine clipped P1 casting cartela ($fixture $variant).")
                // PDFBox operators prove the original genuine spell-data
                // panel starts at x312/y154 and is still 258x127pt.
                assertTrue(Regex(
                    """(?:^|\s)270\.(?:43|429+)\d*\s+168\.(?:94|939+)\d*\s+98\.(?:74|739+)\d*\s+18\.(?:99|989+)\d*\s+re(?:\s|$)""" /* C356: golden level grid cell */
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
                assertTrue(kotlin.math.abs(title.first - 287.43f) < 0.3f && // C351 golden
                    kotlin.math.abs(title.second - 565.52f) < 0.3f, // C356 golden baseline 226.48
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
                    """(?s)(?:^|\s)275\.(?:43|429+)\d*\s+462\.(?:9|89+)\d*\s+m\s+567\.(?:32|319+)\d*\s+462\.(?:9|89+)\d*\s+l\s+572\.(?:32|319+)\d*\s+467\.(?:9|89+)\d*\s+l""" /* C356: golden ATAQUES cartela */ +
                        """.*?\s+h\s+B(?:\s|$)"""
                )
                assertEquals(1, header.findAll(ops).count(),
                    "Exactly one physical clipped ATTACK TABLE header must " +
                        "appear inside x312..570 and y301..313 ($fixture $variant).")
                // The narrow hit-bonus column remains 36pt and the original
                // attack rows continue under the untouched baseline.
                for (columnX in listOf("""454\.(?:58|579+)\d*""", """490\.(?:8|79+)\d*""")) { // C351 golden
                    assertTrue(Regex(
                        """(?m)(?:^|\s)""" + columnX +
                            """\s+374\.(?:74|739+)\d*\s+m\s+""" + columnX + /* C356 golden rows */
                            """\s+448\.(?:44|439+)\d*\s+l"""
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
                    """(?s)(?:^|\s)275\.(?:43|429+)\d*\s+588\.(?:19|189+)\d*\s+m\s+567\.(?:32|319+)\d*\s+588\.(?:19|189+)\d*\s+l\s+572\.(?:32|319+)\d*\s+593\.(?:19|189+)\d*\s+l""" /* C356 golden */ +
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
    fun c371OwnerAttributeRowIsCentredWithARightAlignedSaveGroupAB() {
        // P104 (owner 2026-11-01): score box, modifier oval over its right edge and
        // the name on the row's middle line; on the right the right-aligned
        // «TIRADA DE / SALVACIÓN», the box and an always-printed handwriting line.
        val cases = listOf(
            Triple("02_ilyra_quill_srd5_2_1_evoker_wizard.json", PcSheetV11Variant.B, 6),
            Triple("03_mara_siete_umbrales_custom_extended.json", PcSheetV11Variant.B, 9),
            Triple("03_mara_siete_umbrales_custom_extended.json", PcSheetV11Variant.A, 9),
        )
        val number = """(\d+(?:\.\d+)?)"""
        for ((fixture, variant, expectedRows) in cases) {
            val proof = DesktopPcSheetExportService().generateV11Proof(
                PcSheetV11ExportRequest(variant, PcSheetExportStateSelection.PERMANENT), sources(fixture),
            )
            Loader.loadPDF(proof.bytes).use { pdf ->
                // Right edges (x of the last glyph's end) and baselines of the two label lines.
                val labels = mutableListOf<Triple<String, Float, Float>>()
                object : PDFTextStripper() {
                    override fun writeString(
                        text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                    ) {
                        for (word in listOf("TIRADA DE", "SALVACIÓN")) {
                            var at = text.indexOf(word)
                            while (at >= 0 && at + word.length <= textPositions.size) {
                                if (word == "SALVACIÓN" && at >= 3 && text.substring(at - 3, at) == "CD ") {
                                    at = text.indexOf(word, at + 1); continue
                                }
                                val last = textPositions[at + word.length - 1]
                                labels += Triple(word, last.xDirAdj + last.widthDirAdj, 792f - last.yDirAdj)
                                at = text.indexOf(word, at + 1)
                            }
                        }
                    }
                }.apply { startPage = 1; endPage = 1 }.getText(pdf)
                val firsts = labels.filter { it.first == "TIRADA DE" }
                val seconds = labels.filter { it.first == "SALVACIÓN" && it.second < 230f }
                assertEquals(expectedRows, firsts.size, "One «TIRADA DE» per printed row ($fixture $variant).")
                assertEquals(expectedRows, seconds.size, "One «SALVACIÓN» per printed row ($fixture $variant).")
                (firsts + seconds).forEach { (word, rightEdge, _) ->
                    assertTrue(kotlin.math.abs(rightEdge - 222.84f) < 0.4f,
                        "«$word» is right-aligned 3 pt left of its box ($fixture $variant): $rightEdge")
                }
                firsts.forEach { (_, _, y) ->
                    assertTrue(seconds.any { kotlin.math.abs(y - 6.3f - it.third) < 0.3f },
                        "«SALVACIÓN» sits 6.3 pt under «TIRADA DE» ($fixture $variant).")
                }
                assertPhysicalP1SavingBoxes(pdf, expectedRows)
                val ops = pdf.getPage(0).contents.use { it.readBytes().toString(Charsets.ISO_8859_1) }
                val lines = Regex("""(?:^|\s)238\.6\d*\s+$number\s+m\s+256\.6\d*\s+$number\s+l""").findAll(ops).count()
                assertEquals(expectedRows, lines, "One saving-throw handwriting line per row ($fixture $variant).")
                // The first row: box, oval and save box share the band's middle line.
                val centre = 586.2f - (if (variant == PcSheetV11Variant.A) 22.82f else 25.09f) / 2f
                val starts = Regex("""(?:^|\s)$number\s+$number\s+m(?:\s|$)""").findAll(ops)
                    .map { it.groupValues[1].toFloat() to it.groupValues[2].toFloat() }.toList()
                assertTrue(starts.any { (x, y) -> kotlin.math.abs(x - 45.24f) < 0.02f &&
                    kotlin.math.abs(y - (centre - 9.07f)) < 0.02f }, "Score box centred ($fixture $variant).")
                assertTrue(starts.any { (x, y) -> kotlin.math.abs(x - 111.9f) < 0.02f &&
                    kotlin.math.abs(y - centre) < 0.02f }, "Oval centred over the box edge ($fixture $variant).")
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
                // C362, C446: golden 8.22 pt squares 13.04 pt apart, ending 8.22 pt inside
                // each column; the same boxes per level as P1.
                val meterXs = listOf(207.5f, 389.91f, 572.31f).flatMap { right ->
                    (0..9).map { right - 16.44f - it * 13.04f } }
                val paperSlots = Regex(
                    """(?m)(?:^|\s)([\d.]+)\s+([\d.]+)\s+([\d.]+)\s+([\d.]+)\s+re(?:\s|$)"""
                ).findAll(operators).count { m ->
                    val (x, _, w, h) = m.groupValues.drop(1).map { it.toFloat() }
                    kotlin.math.abs(w - 8.22f) < 0.02f && kotlin.math.abs(h - 8.22f) < 0.02f &&
                        meterXs.any { kotlin.math.abs(it - x) < 0.03f }
                }
                assertEquals((1..9).sumOf { level ->
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpellSlotCapacity.boxes(level, proof.semantic.spellSlots.firstOrNull { it.level == level }?.totalSlots) }, paperSlots,
                    "The maximum BLANK vector boxes of each level. No 0-level or fabricated 10+.") 
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
            // C323: golden C2 columns 39.69..301.04 and 310.96..572.32; C365: golden 5 pt corner cut.
            val clippedGroupBands = Regex(
                """(?m)(?:^|\s)([0-9.]+)\s+[0-9.]+\s+m\s+([0-9.]+)\s+[0-9.]+\s+l\s+([0-9.]+)\s+[0-9.]+\s+l"""
            ).findAll(commands).count { m ->
                val (a, b, c) = m.destructured
                listOf(Triple(44.69f, 296.04f, 301.04f), Triple(315.96f, 567.32f, 572.32f)).any { (x0, x1, x2) ->
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
            assertTrue(text.contains("VIENE DE PÁG. 1"),
                "C2 cartelas cannot disturb genuine physical P1 backlinks.")
            if (text.contains("RASGOS / CONTINUACIÓN P2")) {
                assertTrue(2 in cameFromPages(text),
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
                // C358: the golden B paper - every 14.74 pt row of both subcolumns is
                // a filled row with its 0.32 pt rule, down to the y60 foot reserve.
                val rowRects = Regex(
                    """(?m)(?:^|\s)(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)\s+(\d+(?:\.\d+)?)\s+14\.7[34]\d*\s+re(?:\s|$)"""
                ).findAll(commands).map { m -> m.groupValues.drop(1).map { it.toFloat() } }.toList()
                val leftRows = rowRects.count { (x, _, w) -> kotlin.math.abs(x - 39.69f) < 0.02f && kotlin.math.abs(w - 108.56f) < 0.02f }
                val rightRows = rowRects.count { (x, _, w) -> kotlin.math.abs(x - 151.65f) < 0.02f && kotlin.math.abs(w - 108.57f) < 0.02f }
                assertTrue(leftRows >= 9 && leftRows == rightRows,
                    "Real B skills paper rows must be whole and paired in both subcolumns: $leftRows / $rightRows.")
                assertTrue(rowRects.all { (_, y) -> y >= 60f },
                    "B paper rows stay above the y60 foot reserve.")
                val blankText = PDFTextStripperByArea().apply {
                    // PDFBox uses top-origin region coordinates. The
                    // C358: the three lowest golden rows (PDF y60..104) stay manuscript paper.
                    addRegion("unused-B-paper",
                        Rectangle2D.Float(40f, 688f, 220f, 44f))
                    extractRegions(first)
                }.getTextForRegion("unused-B-paper")
                // C420: Mara's panel holds her custom skills too, down to its foot.
                assertTrue(blankText.isBlank() || fixture.startsWith("03_mara"),
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
                    """(?m)(?:^|\s)43\.2[23]\d*\s+[-0-9.]+\s+8\.5\s+8\.5\s+re(?:\s|$)""" // C350 golden
                ).findAll(commands).count()
                val secondColumnMarks = Regex(
                    """(?m)(?:^|\s)155\.(?:19|2)\d*\s+[-0-9.]+\s+8\.5\s+8\.5\s+re(?:\s|$)""" // C350 golden
                ).findAll(commands).count()
                val standardNames = generated.semantic.skills.filter {
                    it.builtInKey != null
                }
                assertEquals(18, standardNames.size,
                    "Both SRD and custom fixture must preserve the 18 actual standard skills.")
                // C420: custom skills of an attribute printed on P1 join the panel.
                val onP1 = generated.semantic.attributes.take(9).map { it.reference }
                assertEquals(generated.semantic.skills.count { it.builtInKey != null || it.ability in onP1 },
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
                    .singleOrNull { it.width == 344 && it.height == 134 }
                assertTrue(logo != null,
                    "Each physical page must contain the genuine owner logo image.")
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
            // C340 (owner 2026-10-30): bare page number, never «NN / NN».
            assertTrue(pagesByPhysicalNumber.none {
                Regex("""\b\d{2} / \d{2}\b""").containsMatchIn(it)
            }, "No physical page prints a «NN / NN» counter.")
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
            assertTrue((pagesByPhysicalNumber[p1Physical - 1] + physicalMixedC2).replace(Regex("""\s+"""), " ")
                .contains("Cartografía imposible"),
                "Mara's custom skill must not be lost when P1 stays single.")
            assertTrue(physicalMixedC2.contains("Técnica 2"),
                "Mara's overflowed real combat action must remain in C2.")
            assertTrue(pagesByPhysicalNumber[p1Physical - 1].contains(
                "CONTINÚA EN PÁG. " + mixedC2Physical,
            ), "P1 must point to C2 with skills/actions/resources.")
            assertTrue(p1Physical in cameFromPages(pagesByPhysicalNumber[mixedC2Physical - 1]),
                "Mixed C2 must link to the true physical P1, never P2.")
            if (pagesByPhysicalNumber[mixedC2Physical - 1]
                    .contains("RASGOS / CONTINUACIÓN P2")) {
                assertTrue(p2Physical in cameFromPages(pagesByPhysicalNumber[mixedC2Physical - 1]),
                    "A mixed page with genuine P2 traits needs both source backlinks.")
            } else {
                assertFalse(p2Physical in cameFromPages(pagesByPhysicalNumber[mixedC2Physical - 1]),
                    "A P1-only mixed page must never invent a P2 provenance.")
            }
            assertTrue(pagesByPhysicalNumber[p2Physical - 1].contains(
                "CONTINÚA EN PÁG. " + p2C2Physical,
            ), "P2 must point to its OWN first continuation, not mixed P1 C2.")
            assertTrue(p2Physical in cameFromPages(pagesByPhysicalNumber[p2C2Physical - 1]),
                "The first P2-owned C2 (dedicated OR mixed) must link to physical P2.")
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
            // C423: a short deferred table shares its page with EQUIPO ESPECIAL.
            assertTrue(printed.contains("C2 - Equipo especial") || printed.contains("+ EQUIPO ESPECIAL"))
            val specialContinuation = pagesByPhysicalNumber.filter {
                realHeading(it, "C2 - Equipo especial / continuación") || it.contains("+ EQUIPO ESPECIAL")
            }
            assertTrue(specialContinuation.isNotEmpty())
            // C419: the golden C2 table has no title row; c419 guards its four columns.
            assertTrue(specialContinuation.none { it.contains("UBICACIÓN") },
                "The C2 special table has no title row, like the golden.")
            // D-0076 §3.21: P2 shows the first real entries in taller rows;
            // only the remaining source suffix continues on C2, never twice.
            assertTrue(physicalP2.replace(Regex("\\s+"), " ")
                .contains("Astrolabio de cobre"),
                "Mara's first special entry must be printed on P2.")
            // C423: on a shared page, only what follows the EQUIPO ESPECIAL cartela is special.
            assertFalse(specialContinuation.joinToString("\n") { it.substringAfterLast("EQUIPO ESPECIAL") }
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
                // C362: golden 8.22 pt spell boxes and level-meter squares.
                Regex("""(?m)(?:^|\s)8\.22\d* 8\.22\d* re(?:\s|$)""").findAll(contents).count()
            }
            assertTrue(vectorSquares >= 20,
                "P4 paper-only checkbox marks must be real 8.22 pt PDF rectangles.")
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
                regions.addRegion("identity", Rectangle2D.Float(124f, 90f, 450f, 84f)) // C349 golden boxes
                regions.addRegion("attributes", Rectangle2D.Float(40f, 185f, 226f, 125f)) // C351: left column ends at x260.22
                regions.addRegion("combat", Rectangle2D.Float(268f, 178f, 306f, 126f)) // C356 golden y488.98..611.15
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
                split.addRegion("left",Rectangle2D.Float(40f,185f,226f,510f)) // C351: left column ends at x260.22
                split.addRegion("attacks",Rectangle2D.Float(268f,305f,306f,114f)) // C356 golden y374.74..485.86
                split.addRegion("spells",Rectangle2D.Float(268f,548f,306f,114f)) // C356 golden y130.96..242.65
                split.extractRegions(page)
                assertTrue(split.getTextForRegion("left").contains("FUERZA"))
                assertFalse(split.getTextForRegion("left").contains("ARMA / ATAQUE / CONJURO"))
                assertTrue(split.getTextForRegion("attacks").contains("ARMA / ATAQUE / CONJURO"))
                assertTrue(split.getTextForRegion("spells").contains("LANZAMIENTO DE CONJUROS"))
                val p1MechanicalRegions = PDFTextStripperByArea().apply {
                    addRegion("traits", Rectangle2D.Float(268f, 419f, 306f, 128f)) // C356 golden y245.76..371.91
                    addRegion("ammo", Rectangle2D.Float(268f, 662f, 306f, 61f)) // C356 golden y70.3..129.26
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
                        addRegion("first",Rectangle2D.Float(39f,370f,111f,365f)) // C358 golden panel
                        addRegion("second",Rectangle2D.Float(150f,370f,112f,365f))
                        extractRegions(page)
                    }
                    // C420: golden order - by attribute, Charisma's skills last.
                    assertTrue(skillsByColumn.getTextForRegion("first").contains("Atletismo"),
                        "B skills first subcolumn opens with Strength's real Athletics.")
                    assertTrue(skillsByColumn.getTextForRegion("second").contains("Persuasión"),
                        "B skills second subcolumn holds Charisma's real Persuasion.")
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
                // C362: golden 8.22 pt spell boxes and level-meter squares.
                Regex("""(?m)(?:^|\s)8\.22\d* 8\.22\d* re(?:\s|$)""").findAll(contents).count()
            }
            assertTrue(vectorSquares >= 20,
                "P4 paper-only checkbox marks must be real 8.22 pt PDF rectangles.")
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
            preserveRealApplicationPdf(proof.bytes, "V11-INTERNAL-PROOF-portrait-" + fit.name + "-A.pdf")
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
                    // C357: on the golden A rhythm Fortuna, Cordura and Éter fit P1 with
                    // their linked skills; only the fourth extra ability, Renombre, is in C2.
                    val p1Flat = pages.first().replace(Regex("\\s+"), " ")
                    assertTrue(p1Flat.contains("CORDURA") && p1Flat.contains("ÉTER") &&
                        p1Flat.contains("Sincronía") && p1Flat.contains("Manipulación de éter"),
                        "Cordura and Éter print on P1 with their linked skills.")
                    assertFalse(flat.contains("CORDURA SALVACIÓN") || flat.contains("ÉTER SALVACIÓN"),
                        "An attribute printed on P1 is never repeated as a C2 row.")
                    // P104: a missing source save leaves its handwriting line empty.
                    assertTrue(Regex("11 \\+0 RENOMBRE").containsMatchIn(flat) && !flat.contains("sin valor"),
                        "Renombre's C2 row keeps its real score and modifier, with an empty save line.")
                    assertTrue(p1Flat.contains("+9") && p1Flat.contains("+8"),
                        "Linked true skill totals keep golden plus signs on P1 (C357).")
                }
                for ((attribute, skill) in linked) {
                    val key = "Habilidad " + skill
                    assertTrue(full.contains(attribute.uppercase()),
                        "Custom attribute must survive in C2: " + attribute)
                    // Variant A prints a fitting skill directly under its
                    // attribute, without the C2-only "Habilidad" prefix.
                    // Test the source name exactly once across BOTH routes.
                    assertTrue(full.contains(skill),
                        "Persisted dependent custom skill must survive P1 or C2: " + skill)
                    assertEquals(1, Regex(Regex.escape(skill)).findAll(full).count(),
                        "Linked skill must appear exactly once in the real PDF: " + skill)
                    if (variant == PcSheetV11Variant.B) {
                        assertTrue(pages[0].contains(attribute.uppercase()),
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
                        // C420: its skill sits in the golden B panel of P1 with its (XXX).
                        assertTrue(pages.first().replace(Regex("""\s+"""), " ")
                            .contains(skill + " (" + sourceAbbreviation + ")"),
                            "A P1 attribute's skill prints source-backed (XXX) in the B panel, " +
                                "not a full name or invented attribute.")
                        assertFalse(full.contains(key + " (" + attribute + ")"),
                            "A full custom attribute name is not a V11 (XXX) suffix.")
                    } else if (pages[0].contains(attribute.uppercase())) {
                        assertTrue(pages[0].contains(skill),
                            "Fitting A custom abilities must keep their associated skill below.")
                        assertFalse(pages.drop(1).joinToString("\n").contains(key),
                            "An A skill already printed in P1 must not repeat in C2.")
                    } else {
                        assertTrue(pages.any { text -> text.contains("C2 - ") &&
                            text.contains(attribute.uppercase()) && text.contains(skill) },
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
                        // C367: the context band sits under the golden header rule (y709.8).
                        addRegion("left-body",
                            Rectangle2D.Float(40f, 95f, 262f, 625f))
                        addRegion("right-body",
                            Rectangle2D.Float(310f, 95f, 262f, 625f))
                        addRegion("left-context",
                            Rectangle2D.Float(40f, 80f, 262f, 15f))
                        addRegion("right-context",
                            Rectangle2D.Float(310f, 80f, 262f, 15f))
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
                    Regex("16\\s+\\+3\\s+QAMEGAATRIBUTO").containsMatchIn(body) && body.contains("TIRADA DE")
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
                    assertTrue(header.contains("ATRIBUTO: QAMEGAATRIBUTO"),
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
                        addRegion("left", Rectangle2D.Float(38f, 85f, 270f, 650f)) // C397: C2 runs to the golden y59.53
                        addRegion("right", Rectangle2D.Float(306f, 85f, 270f, 650f))
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
            assertTrue(first.contains("FORTUNA") && first.contains("CORDURA"),
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
            for (name in listOf("FORTUNA", "CORDURA", "ÉTER")) {
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
            assertTrue(c2.contains("RENOMBRE"), "Unfitted fourth ability must survive C2.")
            for (name in listOf("FORTUNA", "CORDURA", "ÉTER")) {
                assertFalse(Regex(Regex.escape(name) + """\s+\d+\s+/\s+mod""")
                    .containsMatchIn(c2),
                    "Already-printed custom attribute cannot repeat as a C2 score row.")
            }
            // C420: the golden B panel holds the skills of every attribute printed on P1,
            // with the persisted custom abbreviation, not the full attribute name.
            val p1Flat = p1.replace(Regex("""\s+"""), " ")
            assertTrue(p1Flat.contains("Contratos arcanos (FOR)"))
            assertTrue(p1Flat.contains("Sincronía (COR)"))
            assertTrue(p1Flat.contains("Manipulación de éter (ETE)"))
            assertFalse(c2.contains("Contratos arcanos") || c2.contains("Sincronía") ||
                c2.contains("Manipulación de éter"), "A panel skill never repeats in C2.")
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
                assertFalse(pages[1].contains("CONTINÚA EN PÁG."),
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
                assertTrue(2 in cameFromPages(mixed),
                    "True mixed P2 Rasgo continuation needs genuine backlink.")
            } else {
                assertFalse(2 in cameFromPages(mixed),
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
                assertTrue(pages.first().contains("CONTINÚA EN PÁG."),
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
                assertTrue(pages.first().contains("CONTINÚA EN PÁG."),
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
                fun actualSquare(x: Float, y: Float, side: Float): Int = Regex(
                    """(?m)(?:^|\s)([\d.]+)\s+([\d.]+)\s+([\d.]+)\s+([\d.]+)\s+re(?:\s|$)""",
                ).findAll(content).map { m -> m.groupValues.drop(1).map { it.toFloat() } }.count { (rx, ry, rw, rh) ->
                    kotlin.math.abs(rx - x) < 0.05f && kotlin.math.abs(ry - y) < 0.05f &&
                        kotlin.math.abs(rw - side) < 0.01f && kotlin.math.abs(rh - side) < 0.01f
                }
                // C413: golden first box at cell+34.01. N1=4: 8.79 pt, step 13; N2=5: 8 pt, step 10.
                for (x in listOf(304.44f, 317.44f, 330.44f, 343.44f)) {
                    assertEquals(1, actualSquare(x, 174.47f, 8.79f),
                        "P1 must draw one true usable square for source x=$x.")
                }
                for (x in listOf(406.01f, 416.01f, 426.01f, 436.01f, 446.01f)) {
                    assertEquals(1, actualSquare(x, 174.865f, 8f),
                        "P1 must draw one true usable square for source x=$x.")
                }
                assertEquals(0, actualSquare(356.44f, 174.47f, 8.79f), "Do not invent fifth box for N1=4.")
                assertEquals(0, actualSquare(456.01f, 174.865f, 8f), "Do not invent sixth box for N2=5.")
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
                fun squareAt(x: Float, y: Float): Int = Regex(
                    """(?m)(?:^|\s)([\d.]+)\s+([\d.]+)\s+([\d.]+)\s+([\d.]+)\s+re(?:\s|$)""",
                ).findAll(native).map { m -> m.groupValues.drop(1).map { it.toFloat() } }.count { (rx, ry, rw, rh) ->
                    kotlin.math.abs(rx - x) < 0.05f && kotlin.math.abs(ry - y) < 0.05f &&
                        kotlin.math.abs(rw - 8.79f) < 0.01f && kotlin.math.abs(rh - 8.79f) < 0.01f
                }
                // N1: six real 6.5pt squares, step 8.3 (C255), so the real
                // "4/6" count fits the cell; no seventh.
                val small = Regex(
                    """(?m)(?:^|\s)([\d.]+)\s+([\d.]+)\s+6\.5\s+6\.5\s+re(?:\s|$)""",
                ).findAll(native).map { it.groupValues[1].toFloat() to it.groupValues[2].toFloat() }.toList()
                assertEquals(6, small.size, "Six genuine N1 squares and no seventh.")
                small.sortedBy { it.first }.forEachIndexed { n, (x, y) ->
                    assertTrue(kotlin.math.abs(x - (304.44f + n * 8.3f)) < 0.05f && // C413 golden
                        kotlin.math.abs(y - 175.615f) < 0.05f, "N1 square $n at ($x, $y).") // C356
                }
                // Unchanged real N2=3: the golden 8.79 pt boxes, step 17.01 (C413).
                for (x in listOf(406.01f, 423.02f, 440.03f)) {
                    assertEquals(1, squareAt(x, 174.47f))
                }
                assertEquals(0, squareAt(457.04f, 174.47f),
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
                level = n,
                hitDieSides = 100 + n,
                hitDiceRemaining = 0, // C437: the sheet prints the level, not the dice left.
                sortOrder = n,
            )
        }
        val source = original.copy(
            permanent = original.permanent.copy(
                sheet = original.permanent.sheet.copy(classes = classes),
            ),
        )
        val expectedDice = classes.joinToString(" / ") {
            it.level.toString() + "d" + it.hitDieSides
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
                    val label = row.level.toString() + "d" + row.hitDieSides
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
                // C447: the rest of the story is a C2 module.
                val continuation = pages.filter {
                    it.contains("C2 - Habilidades, acciones y recursos") && it.contains("RASTRO")
                }
                assertTrue(continuation.size >= 3,
                    "Three long source paragraphs must span genuine C2 pages.")
                assertTrue(continuation.drop(1).all {
                    it.contains("HISTORIA / VIENE DE PÁG.")
                })
                assertFalse(pages.any { it.contains("P3 - Narrativa / continuación") })
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
                // C447: the rest of the story is a C2 module.
                val continuation = pages.filter {
                    it.contains("C2 - Habilidades, acciones y recursos") && it.contains("Viaje registrado")
                }
                assertTrue(continuation.size >= 2,
                    "Stress fixture must exercise at least TWO C2 story sheets.")
                assertTrue(continuation.drop(1).all {
                    it.contains("HISTORIA / VIENE DE PÁG.")
                }, "Every further sheet must name the story it continues.")
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
    fun longRealHistoryContinuesInC2WithoutBreakingFinalHandwriting() {
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
            assertTrue(pages.any { it.contains("C2 - Habilidades, acciones y recursos") && it.contains("HISTORIA") },
                "Overflowed real history must continue as a C2 module.")
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
            val mixed = (0 until pdf.numberOfPages).lastOrNull { index ->
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
            // C440 (owner 2026-10-26): same type, same label; no internal ID is printed.
            assertEquals(label1, label2)
            assertFalse(label1.contains("#"))
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
                assertTrue(p1.contains("Cronomante del Umbral"),
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
                    source.copy(id = newId, name = literalName, linkedClassId = null,
                        originKind = io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingOriginKind.CLASS,
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
                    // C440: its row plus the three reference cards, which keep each value.
                    assertEquals(4, Regex(token).findAll(p4).count(),
                        "Real CR/LF/TAB source token must survive on P4 $variant: $token")
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
            // C422: the golden share of the page holds 94 rows, real spells included,
            // which leaves 67 / 70 blank rows.
            Triple("02_ilyra_quill_srd5_2_1_evoker_wizard.json",
                PcSheetV11Variant.B, 67),
            // C258: Ilyra's two-source spells take two rows (91 -> 89 blank rows);
            // Mara's source label now fits beside each spell without the [X]
            // mark, so her rows are single-line and leave 89 blank rows (was 68).
            // C261: the golden P4 rhythm (6.8 pt on 15.7 pt rows) leaves 62 / 65.
            // C322: golden P4 columns (y630.43..64.92, 15.73 pt rows) leave 68 / 71.
            Triple("03_mara_siete_umbrales_custom_extended.json",
                PcSheetV11Variant.B, 70),
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
                    // C317/C362: golden 8.22 pt spell boxes and level-meter squares.
                    marks += Regex("""(?m)(?:^|\s)8\.22\d* 8\.22\d* re(?:\s|$)""")
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
                // C446 (owner 2026-11-06): the same boxes per level as P1.
                val levelMeters = (1..9).sumOf { level ->
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpellSlotCapacity.boxes(level, generated.semantic.spellSlots.firstOrNull { it.level == level }?.totalSlots)
                }
                assertEquals(preparationAndPaper + levelMeters, marks,
                    "One golden 8.22 pt preparation square per source/blank row AND " +
                        "the maximum level-meter squares of each level.")
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
                assertTrue(levels.any { (x, y) -> kotlin.math.abs(y - fireBolt) < 0.7f && x > 200f }, // C425 golden 0.57 pt above
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
                    var description: Float? = null
                    val bookPage = bookPages.first { pages[it].contains("Fire Bolt") } + 1
                    object : PDFTextStripper() {
                        override fun writeString(
                            text: String, textPositions: MutableList<org.apache.pdfbox.text.TextPosition>,
                        ) {
                            val first = textPositions.firstOrNull() ?: return
                            val y = 792f - first.yDirAdj
                            if (text.trim() == "Fire Bolt") name = first.xDirAdj to y
                            if (text.trim() == "EVOCACIÓN") school = Triple(first.xDirAdj, y, first)
                            if (text.startsWith("Cantrip de evocación") && description == null) description = y
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
                    // C363: golden air under the heading band - the description's first
                    // baseline sits 17.01 pt under the school (v11_extremo p10).
                    val descriptionY = requireNotNull(description) { "Fire Bolt description ($variant)." }
                    assertTrue(kotlin.math.abs(schoolY - descriptionY - 17.01f) < 0.3f,
                        "Golden description baseline 17.01 pt under the school (C363, $variant): " +
                            "$schoolY / $descriptionY")
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
                    val edge = if (x < 200f) 160.58f else 292.82f // C409: 5.67 pt inside the golden block
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
                        if (y in 60f..420f && part.first().xDirAdj < 262f && part.first().fontSizeInPt == 7f) { // C358
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
                assertTrue(kotlin.math.abs(end - 144.28f) < 0.6f || kotlin.math.abs(end - 256.25f) < 0.6f, // C350 golden
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
                // C351: P1 uses the golden P1 columns (184.15 + 36.22 of 301.89 pt).
                fun assertGoldenRow(
                    words: List<Word>, number: Int, where: String,
                    labelWidth: Float = 121f, bonusWidth: Float = 36f, tableWidth: Float = 258f,
                    nameInset: Float = 4f, centredBonus: Boolean = false,
                ) {
                    val name = words.single { it.text == "QAROW$number" }
                    val bonus = words.single {
                        it.text == "+$number" && kotlin.math.abs(it.y - name.y) < 0.1f &&
                            it.x > name.x && it.x < name.x + labelWidth + bonusWidth
                    }
                    val damage = words.single { it.text == "QADMG$number" }
                    val rowLeft = name.x - nameInset
                    assertEquals(name.y, damage.y, 0.1f, "$where row $number stays on one line ($variant).")
                    if (centredBonus) {
                        assertEquals(rowLeft + labelWidth + bonusWidth / 2f, (bonus.x + bonus.end) / 2f, 0.6f,
                            "$where bonus is centred in the golden bonus column ($variant).")
                    } else {
                        assertEquals(rowLeft + labelWidth + bonusWidth - 4f, bonus.end, 0.6f,
                            "$where bonus is right-aligned in the golden bonus column ($variant).")
                    }
                    assertEquals(rowLeft + tableWidth - 4f, damage.end, 0.6f,
                        "$where damage is right-aligned in the golden damage column ($variant).")
                }
                val p1Words = pageWords(1)
                (1..5).forEach { assertGoldenRow(p1Words, it, "P1", 184.15f, 36.22f, 301.89f, 4.53f, true) } // C356
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
                // C356: the golden P1 ATAQUES table has five rows; QASHORT10 continues in C2.
                (5..9).forEach { number ->
                    val key = "QASHORT" + number
                    assertTrue(p1.contains(key),
                        "Five later real attacks which fit must fill the five golden P1 rows: " + key)
                    assertFalse(c2.contains(key),
                        "An attack printed on P1 must never duplicate to C2: " + key)
                    assertEquals(1, Regex(Regex.escape(key)).findAll(all).count())
                }
                assertTrue(p1.indexOf("QASHORT5") < p1.indexOf("QASHORT9"),
                    "Selected attacks must retain source order.")
                assertTrue(c2.contains("QASHORT10") && !p1.contains("QASHORT10"),
                    "The sixth fitting attack continues whole in C2.")
                assertEquals(1, Regex("QASHORT10").findAll(all).count())
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
                        if (index == 0) entry.copy(name = sourceName, linkedClassId = null,
                            originKind = io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingOriginKind.CLASS) else entry
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
                    // Genuine PDFBox vector rules across the golden width (C403: banded
                    // 18.71 pt rows down to y59.53). Text-only tests would not prove a
                    // writable paper reserve.
                    val rules = Regex(
                        """(?m)(?:^|\s)39\.69\d*\s+([\d.]+)\s+m\s+572\.32\d*\s+([\d.]+)\s+l\s+S(?:\s|$)"""
                    ).findAll(raw).count { it.groupValues[1] == it.groupValues[2] }
                    assertTrue(rules >= 2,
                        "C-N3 must use genuine ruled-paper PDF vectors, not fake underscores: $rules")
                    assertTrue(texts[cn3.single()].contains("NOTAS MANUSCRITAS"),
                        "The C-N3 reserve carries its golden NOTAS MANUSCRITAS cartela.")
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
                    // C357: skip a column continuation caption («… / CONTINUACIÓN»).
                    val head = runs.firstOrNull { it.first.startsWith(heading) &&
                        !it.first.endsWith("/ CONTINUACIÓN") && !it.first.contains("/ VIENE DE PÁG.") } ?: return null
                    // C365: golden title 6.8 pt and «VIENE P. n» 5.95 pt above the foot.
                    return runs.firstOrNull {
                        it.first.startsWith("VIENE DE PÁG. ") && kotlin.math.abs(it.third - head.third + 0.85f) < 0.3f &&
                            it.second > head.second && it.second < head.second + 258f
                    }?.first?.let { Regex("""^VIENE DE PÁG\. \d+""").find(it)?.value } // C420: a same-baseline cartela may follow
                }
                assertTrue(c2.contains("RECURSOS Y MUNICIONES"), "Mara C2 has resources ($variant).")
                listOf("ATAQUES, NOTAS Y ESPACIOS REALES", "RECURSOS Y MUNICIONES").forEach {
                    assertEquals("VIENE DE PÁG. 1", originOf(it), "$it comes from P1 ($variant).")
                }
                assertEquals("VIENE DE PÁG. 2", originOf("RASGOS / CONTINUACIÓN P2"),
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
                // C351: compare PDFBox-printed floats numerically (98.74 -> 98.739998).
                fun rects(w: String, h: String) = Regex(
                    """(?m)(?:^|\s)([\d.]+)\s+([\d.]+)\s+([\d.]+)\s+([\d.]+)\s+re(?:\s|$)""",
                ).findAll(native).map { m -> m.groupValues.drop(1).map { it.toFloat() } }
                    .filter { (_, _, rw, rh) ->
                        kotlin.math.abs(rw - w.toFloat()) < 0.02f && kotlin.math.abs(rh - h.toFloat()) < 0.02f
                    }.map { (x, y) -> x to y }.toList()
                // Nine shaded level cells (golden 98.74 x 24) in three columns and rows.
                val cells = rects("98.74", "18.99") // C356 golden
                assertEquals(9, cells.size, "Golden 3x3 level grid ($variant).")
                assertEquals(listOf(270.43f, 372.0f, 473.57f), cells.map { it.first }.distinct().sorted()
                    .map { kotlin.math.round(it * 100f) / 100f })
                assertEquals(listOf(130.96f, 149.95f, 168.94f), cells.map { kotlin.math.round(it.second * 100f) / 100f }.distinct().sorted())
                // Every 8pt box lies inside its level cell; totals are the real ones.
                val boxes = (rects("8", "8") + rects("8.79", "8.79")) // C413 golden 8.79 pt
                    .filter { (x, y) -> x in 270f..573f && y in 130f..190f }
                // C445 (owner 2026-11-06): the maximum of each level, real data or not.
                fun expected(level: Int) = io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpellSlotCapacity.boxes(level, slots.firstOrNull { it.level == level }?.totalSlots)
                assertEquals((1..9).sumOf { expected(it) }, boxes.size, "Each level's boxes ($variant).")
                (1..9).forEach { level ->
                    val cx = 270.43f + ((level - 1) % 3) * 101.57f // C351 golden
                    val cy = 168.94f - ((level - 1) / 3) * 18.99f // C356 golden
                    assertEquals(expected(level), boxes.count { (x, y) ->
                        x >= cx + 34f && x + 8f <= cx + 98.74f && kotlin.math.abs(y - (cy + 5.7f)) < 0.3f // C413 golden
                    }, "N$level boxes inside their cell ($variant).")
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
                            if (it.unicode == "X" && it.xDirAdj > 270f && y in 130f..190f) {
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
                    val cx = 270.43f + ((s.level - 1) % 3) * 101.57f // C351 golden
                    val cy = 168.94f - ((s.level - 1) / 3) * 18.99f // C356 golden
                    assertTrue(labels.any {
                        it.first == "${s.spentSlots}/${s.totalSlots}" &&
                            it.second in cx + 60f..cx + 98.74f && it.third in cy..cy + 18.99f // C356
                    }, "N${s.level} real gastados/total at the right of its cell ($variant).")
                }
                // Card labels in the golden order and small, values centered below.
                val cardLabels = listOf("APTITUD MÁGICA", "CD SALVACIÓN", "ATAQUE MÁGICO")
                cardLabels.forEachIndexed { i, label ->
                    val run = labels.firstOrNull { it.first.startsWith(label) && it.third in 202f..212f } // C356
                    assertTrue(run != null && kotlin.math.abs(run.second - (276.09f + i * 101.57f)) < 0.6f, // C356 golden x+5.66
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
                    val footer = pages.first().lineSequence().firstOrNull { moduleLinksOf(it).isNotEmpty() }
                    assertTrue(footer != null, "P1 footer lists continued modules ($fixture $variant).")
                    val links = moduleLinksOf(footer!!)
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
            "EQUIPO ESPECIAL" to listOf("C2 - Equipo especial / continuación",
                "C2 - Equipo y tesoro / EQUIPO + EQUIPO ESPECIAL"),
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
                val footer = pages[1].lineSequence().firstOrNull { moduleLinksOf(it).isNotEmpty() }
                assertTrue(footer != null, "P2 footer lists continued modules ($variant).")
                assertFalse(pages[1].contains("OTROS C2"), "Old P2 footer grammar ($variant).")
                val links = moduleLinksOf(footer!!)
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
                    val footer = pages[p3].lineSequence().firstOrNull { moduleLinksOf(it).isNotEmpty() }
                    val links = footer?.let { moduleLinksOf(it) }.orEmpty()
                    assertEquals(expected, links.map { it.first }, "P3 links ($variant): $footer")
                    links.forEach { (module, page) ->
                        // C447: the story continues as a C2 module with its own cartela.
                        val first = pages.indexOfFirst { text ->
                            when (module) {
                                "HISTORIA" -> text.contains("C2 - Habilidades") &&
                                    Regex("""(?m)^HISTORIA\s*$""").containsMatchIn(text)
                                else -> text.contains("P3 - Narrativa / continuación") &&
                                    text.contains("IDEALES / CONTINUACIÓN")
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
                            if (moduleLinksOf(text).isNotEmpty()) { // C341: the band, not the right link
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
            """(?:^|\s)(4[1-9]\d(?:\.\d+)?|5[0-5]\d(?:\.\d+)?)\s+(?:94\.8[12]\d*|79\.3[67]\d*)\s+7\.94\s+7\.94\s+re(?:\s|$)""" /* C356 golden rows */
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
                assertTrue(pages.any { it.contains("ID: " + sourceId) },
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
                    it.contains(note.title + " / VIENE DE PÁG.")
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
            // C361: the golden 22.96 pt band y662.74..685.7, below the header rule y=717.
            val commands = page.contents.use {
                it.readBytes().toString(Charsets.ISO_8859_1)
            }
            val clippedCartela = Regex(
                """(?s)(?:^|\s)44\.(?:69|689+)\d*\s+662\.(?:74|739+)\d*\s+m\s+567\.(?:32|319+)\d*\s+662\.(?:74|739+)\d*\s+l\s+572\.(?:32|319+)\d*\s+667\.(?:74|739+)\d*\s+l.*?\s+B(?:\s|$)"""
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
            assertTrue(printed.contains("VIENE DE PÁG. 2"))
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
                assertFalse(specialPages.first().contains("EQUIPO: " + label + " / VIENE DE PÁG."),
                    "The first appearance is the genuine source row, not a repeat.")
                specialPages.drop(1).forEach { page ->
                    assertTrue(page.contains("EQUIPO: " + label + " / VIENE DE PÁG."),
                        "A new physical C2 page must retain the source identity.")
                    assertFalse(page.contains("UBICACIÓN"),
                        "C419: no title row on a C2 special page, like the golden.")
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
                assertFalse(c2.first().contains("ID: " + id + " / VIENE DE PÁG."))
                c2.drop(1).forEach { page ->
                    assertTrue(page.contains("ID: " + id + " / VIENE DE PÁG."),
                        "Each continued real C2 page must retain the source UUID.")
                    assertFalse(page.contains("EQUIPO: QANOMBREINICIO"))
                    assertFalse(page.contains("UBICACIÓN"))
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
                    it.lineSequence().any { line -> Regex("""^TESORO(\s|$)""").containsMatchIn(line.trim()) } // C361 golden title
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
                assertTrue(pages.drop(1).any { it.contains("RASGOS") },
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
                    """(?s)(?:^|\s)275\.(?:43|429+)\d*\s+348\.(?:94|939+)\d*\s+m\s+567\.(?:32|319+)\d*\s+348\.(?:94|939+)\d*\s+l\s+""" + /* C356 golden */
                        """572\.(?:32|319+)\d*\s+353\.(?:94|939+)\d*\s+l.*?270\.(?:43|429+)\d*\s+353\.(?:94|939+)\d*\s+l\s+h\s+B(?:\s|$)"""
                )
                assertEquals(1, polygon.findAll(ops).count(),
                    "Approved P1 Rasgos title requires ONE genuine " +
                        "eight-segment clipped blue band ($name $variant).")
                assertTrue(Regex(
                    """(?:^|\s)270\.(?:43|429+)\d*\s+332\.(?:5|49+)\d*\s+148\.(?:11|109+)\d*\s+14\.(?:46|459+)\d*\s+re(?:\s|$)""" /* C356: first golden trait row */
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
                    """(?:^|\s)270\.(?:43|429+)\d*\s+(?:433\.(?:7|69+)\d*|418\.(?:96|959+)\d*|404\.(?:22|219+)\d*|389\.(?:48|479+)\d*|374\.(?:74|739+)\d*)\s+301\.(?:89|889+)\d*\s+14\.(?:74|739+)\d*\s+re(?:\s|$)""" /* C356 golden rows */
                )
                assertEquals(5, bands.findAll(content).count(),
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
                // C358: golden B - ATRIBUTOS and HABILIDADES share the 44.69..255.22
                // chamfered cartela shape; HABILIDADES sits 3.12 pt under the last row.
                val clippedHeader = Regex(
                    """(?s)(?:^|\s)44\.6[89]\d*\s+([\d.]+)\s+m\s+255\.2[12]\d*\s+\1\s+l\s+""" +
                        """260\.2[12]\d*\s+[\d.]+\s+l.*?39\.69\d*\s+[\d.]+\s+l\s+h\s+B(?:\s|$)"""
                )
                assertEquals(2, clippedHeader.findAll(ops).count(),
                    "Golden C2c HABILIDADES header missing ($fixture).")
                val goldenRows = Regex(
                    """(?:^|\s)39\.69\d*\s+[\d.]+\s+108\.5[56]\d*\s+14\.7[34]\d*\s+re(?:\s|$)"""
                ).findAll(ops).count()
                assertTrue(goldenRows >= 9,
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
                    // C350: golden left column x39.69..260.22.
                    """(?s)(?:^|\s)44\.6[89]\d*\s+588\.(?:19|189+)\d*\s+m\s+255\.2[12]\d*\s+588\.(?:19|189+)\d*\s+l\s+""" + /* C357 golden */
                        """260\.2[12]\d*\s+593\.(?:19|189+)\d*\s+l.*?39\.69\d*\s+593\.(?:19|189+)\d*\s+l\s+h\s+B(?:\s|$)"""
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
                    """(?:94\.8[12]\d*|79\.3[67]\d*)\s+7\.94\s+7\.94\s+re(?:\s|$)""" /* C356 golden rows */
            )
            assertEquals(20, marks.findAll(ops).count(),
                "Empty ammo must show 20 independent paper-only marks.")
            val columns = marks.findAll(ops).map { it.groupValues[1].toFloat() }.distinct().sorted().toList()
            assertEquals(10, columns.size, "Ten golden columns, shared by both rows.")
            columns.forEachIndexed { c, x ->
                assertTrue(kotlin.math.abs(x - (422.08f + c * 13.75f + (c / 5) * 2.83f)) < 0.05f, // C351 golden
                    "Golden mark column $c at $x.")
            }
            assertTrue(Regex(
                """(?:^|\s)270\.(?:43|429+)\d*\s+70\.(?:3|29+)\d*\s+301\.(?:89|889+)\d*\s+34\.(?:01|009+)\d*\s+re(?:\s|$)""" /* C356 golden row */
            ).containsMatchIn(ops), "Original P1 ammo panel must not move.")
            val page1 = PDFTextStripper().apply {
                startPage = 1
                endPage = 1
            }.getText(pdf)
            assertTrue(Regex("MUNICIONES(?! A P\\.)(?! /)").containsMatchIn(page1), "Golden MUNICIONES title.")
            assertFalse(page1.contains("____________"),
                "Empty ammo name must be a real writable vector baseline, not fake printed underscores.")
            // C410: golden «Tipo: ___» - the label and a true vector rule.
            assertTrue(page1.contains("Tipo:"), "Golden empty ammunition label.")
            assertTrue(Regex(
                """(?:^|\s)293(?:\.0+)?\s+91\.(?:8|79+)\d*\s+m\s+350\.(?:85|849+)\d*\s+91\.(?:8|79+)\d*\s+l\s+S(?:\s|$)"""
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
                // C401: golden grid rules fill the pair; any vertical rule in the right half counts.
                assertTrue(Regex(
                    """(?m)(?:^|\s)(\d{3}(?:\.\d+)?)\s+([\d.]+)\s+m\s+(\d{3}(?:\.\d+)?)\s+([\d.]+)\s+l"""
                ).findAll(secondContent).any { m ->
                    val (x0, y0, x1, y1) = m.groupValues.drop(1).map { it.toFloat() }
                    x0 > 305f && kotlin.math.abs(x0 - x1) < 0.01f && y1 - y0 > 150f
                },
                    "Right C2 NOTES grid must contain real full-height PDF vectors.")
                assertTrue(treasurePages.isNotEmpty(),
                    "The independent treasure stream must also overflow to C2.")
                assertTrue(equipmentPages.last() < treasurePages.first())
                assertTrue(pages[1].contains("CONTINÚA EN PÁG. " +
                    equipmentPages.first()))
                assertTrue(pages[1].contains("TESORO CONTINÚA EN PÁG. " +
                    treasurePages.first()),
                    "P2 must link independently to the physical TESORO start.")
                val p2Footer = PDFTextStripperByArea().apply {
                    addRegion("module-forward-links",
                        Rectangle2D.Float(42f, 734f, 528f, 16f)) // C290: golden band y43.94..56.98
                    extractRegions(pdf.getPage(1))
                }.getTextForRegion("module-forward-links")
                assertTrue(p2Footer.contains("EQUIPO CONTINÚA EN PÁG. " + equipmentPages.first() +
                    "   ·   TESORO CONTINÚA EN PÁG. " + treasurePages.first()),
                    "Every distinct P2 module needs a visible source-backed forward cue.")

                for (physical in equipmentPages) {
                    val footer = pages[physical - 1]
                    if (physical == equipmentPages.first()) {
                        assertTrue(footer.contains("VIENE DE PÁG. 2"))
                        assertEquals(setOf(2), cameFromPages(footer))
                    }
                    if (physical == equipmentPages.last()) {
                        assertFalse(footer.contains("CONTINÚA EN PÁG. " + treasurePages.first()),
                            "Equipo cannot link to the next DIFFERENT table.")
                        assertFalse(footer.contains("CONTINÚA EN PÁG."),
                            "Last Equipo page must not point to Tesoro.")
                    } else {
                        assertTrue(footer.contains("CONTINÚA EN PÁG. " + (physical + 1)))
                    }
                }
                val firstTreasure = pages[treasurePages.first() - 1]
                assertTrue(firstTreasure.contains("VIENE DE PÁG. 2"),
                    "Independent Tesoro must link to its real P2 parent.")
                assertFalse(equipmentPages.last() in cameFromPages(firstTreasure),
                    "First Tesoro must not claim the last Equipo page as a parent.")
                assertEquals(setOf(2), cameFromPages(firstTreasure),
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
                assertFalse(pages[0].contains("CONTINÚA EN PÁG."),
                    "P1 must not advertise a C2 page containing ONLY P2 traits.")
                assertTrue(pages[1].contains("CONTINÚA EN PÁG. " + mixedPage),
                    "P2 must link to the real first physical traits continuation.")
                assertTrue(pages[mixedPage - 1].contains("VIENE DE PÁG. 2"),
                    "P2-only mixed C2 must identify its actual base page.")
                assertFalse(1 in cameFromPages(pages[mixedPage - 1]),
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
                    pages[1].contains("CONTINÚA EN PÁG. " + p2TraitsPage),
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
                // long notes a second C-N3 page; C423: her equipment and special equipment
                // share a page, as in the golden (10). The header itself adds none.
                assertEquals(10, pages.size,
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
                    reservedHeaders.any { resourceCaption.containsMatchIn(it) },
                    "A continued real resource group requires its label in " +
                        "the RESERVED right C2 header, not an anonymous paragraph.",
                )
                val c2Text = c2Indices.joinToString("\n") { pages[it] }
                assertTrue(c2Text.contains("Reserva 3"),
                    "Group context may not remove real Mara resource records.")
                // C443: the group may restart in more than one column, once each.
                assertTrue(resourceCaption.findAll(c2Text).count() >= 1,
                    "A continued group carries its caption.")
                for (index in c2Indices) {
                    assertTrue(resourceCaption.findAll(pages[index]).count() <= 1,
                        "A physical C2 source-group header must not duplicate itself on a page.")
                }
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
                assertTrue(pages[0].contains("CONTINÚA EN PÁG. " + mixedPhysical))
                assertTrue(pages[mixedPhysical - 1].contains("VIENE DE PÁG. 1"))
                if (fixture.contains("mara")) {
                    // C259: P2 points to the first mixed C2 page with its traits.
                    val p2Physical = pages.indexOfFirst { it.contains("RASGOS / CONTINUACIÓN P2") } + 1
                    assertTrue(p2Physical >= mixedPhysical)
                    assertTrue(pages[1].contains("CONTINÚA EN PÁG. " + p2Physical))
                    assertTrue(2 in cameFromPages(pages[p2Physical - 1]))
                    val ordinaryC2 = pages.indexOfFirst {
                        it.contains("C2 - Equipo y tesoro / EQUIPO")
                    } + 1
                    // C423: as in the golden, her leftover equipment and special equipment
                    // share one C2 page, and P2 names both modules with that page.
                    assertTrue(ordinaryC2 > mixedPhysical)
                    assertTrue(pages[ordinaryC2 - 1].contains("+ EQUIPO ESPECIAL"),
                        "Equipment and special equipment share their C2 page.")
                    assertTrue(pages[1].contains("EQUIPO, EQUIPO ESPECIAL CONTINÚAN EN PÁG. " + ordinaryC2),
                        "Mara P2 must lead to the shared equipment C2.")

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
            // C320: EQUIPO 460.63..670.68, TESORO 327.97..427.18. C408: golden blocks, first
            // block x39.69..166.25, one rule 0.99 pt above each row foot.
            val rules = Regex(
                """(?:^|\s)39\.69\d*\s+([0-9]+(?:\.[0-9]+)?)\s+m\s+166\.2\d*\s+\1\s+l"""
            ).findAll(ops).map { it.groupValues[1].toFloat() }.toList()
            val equipment = rules.filter { it > 460.63f && it < 670.68f }.sortedDescending()
            val treasure = rules.filter { it > 327.97f && it < 427.18f }.sortedDescending()
            assertEquals(13, equipment.size,
                "Empty Equipo must reserve thirteen generously spaced golden rows.")
            // C320: the golden TESORO is five 19.84 pt rows.
            assertEquals(5, treasure.size,
                "Empty Tesoro must reserve the golden five pencil rows.")
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
                assertTrue(combined.single().lineSequence().any { Regex("""^TESORO(\s|$)""").containsMatchIn(it.trim()) }) // C361 golden title
                assertFalse(pages.any { it.contains("C2 - Equipo y tesoro / EQUIPO") },
                    "No ordinary object is left for C2.")
                assertTrue(combined.single().contains("VIENE DE PÁG. 2"))
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
            // C343: the golden v11_extremo p2 ACTIVO boxes - 8.79 pt at x545.28,
            // centred in eight 26.93 pt rows under the 15.02 pt header.
            val activoRows = listOf(258.52f, 231.59f, 204.66f, 177.73f, 150.80f, 123.87f, 96.94f, 70.02f)
            val paperMarks = Regex("""(?:^|\s)([0-9.]+)\s+([0-9.]+)\s+([0-9.]+)\s+([0-9.]+)\s+re(?:\s|$)""")
                .findAll(ops).map { m -> m.groupValues.drop(1).map { it.toFloat() } }
                .count { (x, y, w, h) ->
                    kotlin.math.abs(x - 545.28f) < 0.02f && kotlin.math.abs(w - 8.79f) < 0.02f &&
                        kotlin.math.abs(h - 8.79f) < 0.02f && activoRows.any { kotlin.math.abs(it - y) < 0.02f }
                }
            assertEquals(8, paperMarks,
                "Eight genuine golden 8.79 pt ACTIVO squares must be printed in P2.")
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

// C341 (owner 2026-10-30): pages named by «VIENE DE PÁG. 1, 2 Y 4».
private fun cameFromPages(text: String): Set<Int> =
    Regex("""VIENE DE PÁG\. (\d+(?:(?:, | Y )\d+)*)""").findAll(text)
        .flatMap { match -> Regex("""\d+""").findAll(match.groupValues[1]).map { it.value.toInt() } }
        .toSet()

// C341: module band «A, B CONTINÚAN EN PÁG. 4   ·   C CONTINÚA EN PÁG. 5».
private fun moduleLinksOf(line: String): List<Pair<String, Int>> =
    Regex("""([A-ZÁÉÍÓÚÑ][A-ZÁÉÍÓÚÑ ,]*?) CONTINÚAN? EN PÁG\. (\d+)""").findAll(line)
        .flatMap { match ->
            match.groupValues[1].split(", ").map { it.trim() to match.groupValues[2].toInt() }
        }.toList()

private val resourceCaption =
    Regex("""RECURSOS Y MUNICIONES / (?:VIENE DE PÁG\. \d+|CONTINUACIÓN)""")
