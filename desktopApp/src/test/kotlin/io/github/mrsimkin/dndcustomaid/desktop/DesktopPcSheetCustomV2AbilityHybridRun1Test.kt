package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.Color
import java.awt.geom.AffineTransform
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.apache.pdfbox.Loader
import org.apache.pdfbox.multipdf.LayerUtility
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDFormContentStream
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.graphics.PDXObject
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.util.Matrix

/**
 * Strategy 1 / Hybrid / Custom v2 per-Attribute audited run series.
 *
 * Reuses the owner-approved Custom-v1 section-isolated architecture and source-measured geometry
 * discipline. QA-only code; not product rendering.
 */
class DesktopPcSheetCustomV2AbilityHybridRun1Test {
    @Test
    fun rendersCompletePerAbilityCustomV2HybridDraft() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val templateBytes = resource(TEMPLATE).use { it.readBytes() }
        val composite = File(proofDir, "hybrid-custom-v2-ability-run1-composite.pdf")

        Loader.loadPDF(templateBytes).use { doc ->
            // v2 pages 1 and 2 are alternative first pages. This run uses page 2 / per Ability.
            doc.removePage(0)
            val fonts = Fonts(doc)
            val layers = LayerUtility(doc)
            renderV2AbilityHybrid(doc, layers, fonts)
            doc.save(composite)
        }

        Loader.loadPDF(composite).use { doc ->
            assertEquals(4, doc.numberOfPages)
            val oc = assertNotNull(doc.documentCatalog.ocProperties)
            assertTrue(oc.getGroupNames().count() >= 25, "v2 per-Ability Run 1 must stay section-isolated.")
            assertHybridFontsEmbedded(doc)
            val renderer = PDFRenderer(doc)
            repeat(4) { index ->
                val image = renderer.renderImageWithDPI(index, 300f, ImageType.RGB)
                val png = File(proofDir, "hybrid-custom-v2-ability-run1-composite-page-${index + 1}.png")
                assertTrue(ImageIO.write(image, "png", png))
            }
        }

        val overlayOnly = File(proofDir, "hybrid-custom-v2-ability-run1-overlay-only.pdf")
        PDDocument().use { doc ->
            repeat(4) { doc.addPage(PDPage(PDRectangle(W, H))) }
            val fonts = Fonts(doc)
            val layers = LayerUtility(doc)
            renderV2AbilityHybrid(doc, layers, fonts)
            doc.save(overlayOnly)
        }

        assertTrue(composite.length() > 0)
        assertTrue(overlayOnly.length() > 0)
    }

    private data class V2SkillRow(
        val checkbox: TopRect,
        val valueRule: Rule,
        val value: String,
        val mark: Mark?,
    )

    private data class V2AbilityBlock(
        val scoreRect: TopRect,
        val modRect: TopRect,
        val score: String,
        val mod: String,
        val rows: List<V2SkillRow>,
    )

    private data class V2SpellBlock(
        val level: Int,
        val total: Int?,
        val totalRect: TopRect?,
        val checkX: Float?,
        val firstCheckTop: Float?,
        val textStartX: Float,
        val textEndX: Float,
        val firstRuleY: Float,
        val rowStep: Float,
        val maxRows: Int,
        val spells: List<SpellRow>,
    )

    private data class V2AbilityCentralRow(
        val checkbox: TopRect,
        val valueRule: Rule,
        val value: String,
        val mark: Mark?,
    )

    private fun renderV2AbilityHybrid(doc: PDDocument, layers: LayerUtility, fonts: Fonts) {
        val main = doc.getPage(0)
        val combined = doc.getPage(1)
        val spells = doc.getPage(2)
        val notes = doc.getPage(3)

        appendSection(doc, layers, main, "V2B MAIN - Identification") { drawV2Identification(it, fonts) }
        appendSection(doc, layers, main, "V2B MAIN - Portrait") { drawV2AbilityPortrait(it, fonts) }
        appendSection(doc, layers, main, "V2B MAIN - Core Stats") { drawV2AbilityCoreStats(it, fonts) }
        appendSection(doc, layers, main, "V2B MAIN - Attributes") { drawV2AbilityScores(it, fonts) }
        appendSection(doc, layers, main, "V2B MAIN - Saving Throws") { drawV2AbilitySavingThrows(it, fonts) }
        appendSection(doc, layers, main, "V2B MAIN - Skills") { drawV2AbilitySkills(it, fonts) }
        appendSection(doc, layers, main, "V2B MAIN - Attacks") { drawV2Attacks(it, fonts) }
        appendSection(doc, layers, main, "V2B MAIN - Traits") { drawV2Traits(it, fonts) }
        appendSection(doc, layers, main, "V2B MAIN - Spell Slots") { drawV2MainSpellSlots(it, fonts) }
        appendSection(doc, layers, main, "V2B MAIN - Spellcasting Summary") { drawV2SpellSummary(it, fonts) }
        appendSection(doc, layers, main, "V2B MAIN - Treasure") { drawV2Treasure(it, fonts) }
        appendSection(doc, layers, main, "V2B MAIN - Other") { drawV2Other(it, fonts) }

        // Shared v2 pages are intentionally byte-for-byte rendering logic from the approved
        // per-Attribute baseline. Any visual difference on these pages is a regression.
        appendSection(doc, layers, combined, "V2 COMMON - Equipment") { drawV2CombinedEquipment(it, fonts) }
        appendSection(doc, layers, combined, "V2 COMMON - Background") { drawV2CombinedBackground(it, fonts) }
        appendSection(doc, layers, combined, "V2 COMMON - Special Equipment") { drawV2CombinedSpecialEquipment(it, fonts) }

        V2_SPELL_BLOCKS.forEach { block ->
            appendSection(doc, layers, spells, "V2 COMMON - Spells level ${block.level}") {
                drawV2SpellBlock(it, fonts, block)
            }
        }

        appendSection(doc, layers, notes, "V2 COMMON - Notes left") { drawV2NotesLeft(it, fonts) }
        appendSection(doc, layers, notes, "V2 COMMON - Notes right") { drawV2NotesRight(it, fonts) }
        appendSection(doc, layers, notes, "V2 COMMON - Notes doodles") { drawV2NotesDoodles(it) }
    }

    private fun drawV2AbilityPortrait(s: PDFormContentStream, fonts: Fonts) {
        // Page-2 portrait is taller than the per-Attribute portrait. Keep the QA figure in the
        // upper field and reserve the lower decorative band for the handwritten name.
        val cx = 267f
        val headCy = H - 54f
        val headR = 9f
        val shoulderY = H - 79f
        val hipY = H - 116f
        val feetY = H - 153f

        s.saveGraphicsState()
        s.setStrokingColor(Color.BLACK)
        s.setLineWidth(1.2f)
        circlePath(s, cx, headCy, headR)
        s.stroke()
        s.moveTo(cx, headCy - headR)
        s.lineTo(cx, hipY)
        s.moveTo(cx, shoulderY)
        s.lineTo(cx - 24f, shoulderY - 18f)
        s.moveTo(cx, shoulderY)
        s.lineTo(cx + 24f, shoulderY - 18f)
        s.moveTo(cx, hipY)
        s.lineTo(cx - 20f, feetY)
        s.moveTo(cx, hipY)
        s.lineTo(cx + 20f, feetY)
        s.stroke()
        s.restoreGraphicsState()

        // Match the approved per-Attribute optical placement inside the decorative name banner.
        // Page 2's banner begins ~42.5 pt lower, so the handwritten name follows that source anchor.
        centered(s, fonts.handwritten, TopRect(205f, 177f, 124f, 22f), "Aster Vale", 13.5f, -0.2f)
    }

    private fun drawV2AbilityCoreStats(s: PDFormContentStream, fonts: Fonts) {
        // Page-2 proficiency/inspiration are in the upper-left band.
        // Calibrated against the approved per-Attribute value-to-label optical offsets.
        centered(s, fonts.semibold, TopRect(9.5f, 96f, 78f, 31f), "+3", 14f, -0.3f)
        glyphInRect(s, fonts.symbol, 0xE20E, TopRect(111f, 102.5f, 18f, 18f), 1f, 1f)

        // Right-side combat geometry is shared with the approved per-Attribute page.
        centered(s, fonts.semibold, TopRect(359f, 99f, 53f, 39f), "16", 20f, -0.8f)
        centered(s, fonts.semibold, TopRect(359f, 144f, 53f, 34f), "+4", 15f, -0.4f)
        centered(s, fonts.semibold, TopRect(359f, 184f, 53f, 31f), "30", 15f, -0.4f)
        centered(s, fonts.semibold, TopRect(445f, 98f, 68f, 29f), "4d6 / 1d8", 10.5f, -0.2f)
        centered(s, fonts.semibold, TopRect(530f, 98f, 63f, 29f), "34", 16f, -0.4f)
        centered(s, fonts.semibold, TopRect(444f, 145f, 149f, 62f), "27", 22f, -0.7f)
        // Armor Class is intentionally rendered once. Run 1 accidentally overprinted a second
        // smaller "16", producing the visibly heavy/doubled value noted in owner review.
    }

    private fun drawV2AbilityScores(s: PDFormContentStream, fonts: Fonts) {
        V2_PER_ABILITY_SCORE_BLOCKS.forEach { block ->
            centered(s, fonts.semibold, block.scoreRect, block.score, 17f, -0.5f)
            centered(s, fonts.semibold, block.modRect, block.mod, 15.5f, -0.2f)
        }
    }

    private fun drawV2AbilitySavingThrows(s: PDFormContentStream, fonts: Fonts) {
        V2_PER_ABILITY_SAVES.forEach { row ->
            row.mark?.let {
                glyphInRect(
                    s,
                    fonts.symbol,
                    if (it == Mark.DOUBLE) 0xE212 else 0xE211,
                    row.checkbox,
                    0.55f,
                    0.55f,
                    opticalX = if (it == Mark.DOUBLE) 2.0f else 1.65f,
                    opticalY = -0.6f,
                )
            }
            centeredAboveRule(s, fonts.semibold, row.valueRule, row.value, 8.8f, 1.8f)
        }
    }

    private fun drawV2AbilitySkills(s: PDFormContentStream, fonts: Fonts) {
        V2_PER_ABILITY_SKILLS.forEach { row ->
            row.mark?.let {
                glyphInRect(
                    s,
                    fonts.symbol,
                    if (it == Mark.DOUBLE) 0xE212 else 0xE211,
                    row.checkbox,
                    0.55f,
                    0.55f,
                    opticalX = if (it == Mark.DOUBLE) 2.0f else 1.65f,
                    opticalY = -0.6f,
                )
            }
            centeredAboveRule(s, fonts.semibold, row.valueRule, row.value, 8.8f, 1.8f)
        }
    }

    private fun renderV2AttributeHybrid(doc: PDDocument, layers: LayerUtility, fonts: Fonts) {
        val main = doc.getPage(0)
        val combined = doc.getPage(1)
        val spells = doc.getPage(2)
        val notes = doc.getPage(3)

        appendSection(doc, layers, main, "V2A MAIN - Identification") { drawV2Identification(it, fonts) }
        appendSection(doc, layers, main, "V2A MAIN - Portrait") { drawV2Portrait(it, fonts) }
        appendSection(doc, layers, main, "V2A MAIN - Core Stats") { drawV2CoreStats(it, fonts) }
        V2_ABILITY_BLOCKS.forEachIndexed { index, block ->
            appendSection(doc, layers, main, "V2A MAIN - Attribute ${index + 1}") {
                drawV2AbilityBlock(it, fonts, block)
            }
        }
        appendSection(doc, layers, main, "V2A MAIN - Attacks") { drawV2Attacks(it, fonts) }
        appendSection(doc, layers, main, "V2A MAIN - Traits") { drawV2Traits(it, fonts) }
        appendSection(doc, layers, main, "V2A MAIN - Spell Slots") { drawV2MainSpellSlots(it, fonts) }
        appendSection(doc, layers, main, "V2A MAIN - Spellcasting Summary") { drawV2SpellSummary(it, fonts) }
        appendSection(doc, layers, main, "V2A MAIN - Treasure") { drawV2Treasure(it, fonts) }
        appendSection(doc, layers, main, "V2A MAIN - Other") { drawV2Other(it, fonts) }

        appendSection(doc, layers, combined, "V2 COMMON - Equipment") { drawV2CombinedEquipment(it, fonts) }
        appendSection(doc, layers, combined, "V2 COMMON - Background") { drawV2CombinedBackground(it, fonts) }
        appendSection(doc, layers, combined, "V2 COMMON - Special Equipment") { drawV2CombinedSpecialEquipment(it, fonts) }

        V2_SPELL_BLOCKS.forEach { block ->
            appendSection(doc, layers, spells, "V2 COMMON - Spells level ${block.level}") {
                drawV2SpellBlock(it, fonts, block)
            }
        }

        appendSection(doc, layers, notes, "V2 COMMON - Notes left") { drawV2NotesLeft(it, fonts) }
        appendSection(doc, layers, notes, "V2 COMMON - Notes right") { drawV2NotesRight(it, fonts) }
        appendSection(doc, layers, notes, "V2 COMMON - Notes doodles") { drawV2NotesDoodles(it) }
    }

    private fun drawV2Identification(s: PDFormContentStream, fonts: Fonts) {
        textAboveRule(s, fonts.handwritten, Rule(442.5f, 598f, 46.5f), "Mago 5 / Pícaro 2", 10.5f, 9f, 2.2f, 2f)
        textAboveRule(s, fonts.handwritten, Rule(388.5f, 598f, 67.5f), "Elfo Alto", 10.5f, 9f, 0.7f, 2f)
        textAboveRule(s, fonts.handwritten, Rule(445.5f, 598f, 89f), "Neutral Bueno", 10.5f, 9f, 2.2f, 2f)
    }

    private fun drawV2Portrait(s: PDFormContentStream, fonts: Fonts) {
        // QA portrait stays well inside the owner frame.
        val cx = 267f
        val top = 34f
        val bottom = 119f
        val headCy = H - 54f
        val headR = 9f

        s.saveGraphicsState()
        s.setStrokingColor(Color.BLACK)
        s.setLineWidth(1.2f)
        circlePath(s, cx, headCy, headR)
        s.stroke()
        val shoulderY = H - 76f
        val hipY = H - 101f
        s.moveTo(cx, headCy - headR)
        s.lineTo(cx, hipY)
        s.moveTo(cx, shoulderY)
        s.lineTo(cx - 22f, shoulderY - 17f)
        s.moveTo(cx, shoulderY)
        s.lineTo(cx + 22f, shoulderY - 17f)
        s.moveTo(cx, hipY)
        s.lineTo(cx - 18f, H - bottom)
        s.moveTo(cx, hipY)
        s.lineTo(cx + 18f, H - bottom)
        s.stroke()
        s.restoreGraphicsState()

        centered(s, fonts.handwritten, TopRect(205f, 134.5f, 124f, 22f), "Aster Vale", 13.5f, -0.2f)
    }

    private fun drawV2CoreStats(s: PDFormContentStream, fonts: Fonts) {
        centered(s, fonts.semibold, TopRect(359f, 99f, 53f, 39f), "16", 20f, -0.8f)
        centered(s, fonts.semibold, TopRect(359f, 144f, 53f, 34f), "+4", 15f, -0.4f)
        centered(s, fonts.semibold, TopRect(359f, 184f, 53f, 31f), "30", 15f, -0.4f)
        centered(s, fonts.semibold, TopRect(445f, 98f, 68f, 29f), "4d6 / 1d8", 10.5f, -0.2f)
        centered(s, fonts.semibold, TopRect(530f, 98f, 63f, 29f), "34", 16f, -0.4f)
        centered(s, fonts.semibold, TopRect(444f, 145f, 149f, 62f), "27", 22f, -0.7f)
        centered(s, fonts.semibold, TopRect(189f, 190f, 59f, 30f), "+3", 14f, -0.3f)
        glyphInRect(s, fonts.symbol, 0xE20E, TopRect(281f, 196f, 18f, 18f), 1f, 1f)

        // Per-v2 visual QA: armor components remain populated as calibration data.
        centered(s, fonts.semibold, TopRect(360f, 105f, 52f, 26f), "16", 16f, -0.5f)
    }

    private fun drawV2AbilityBlock(s: PDFormContentStream, fonts: Fonts, block: V2AbilityBlock) {
        centered(s, fonts.semibold, block.scoreRect, block.score, 17f, -0.5f)
        centered(s, fonts.semibold, block.modRect, block.mod, 15.5f, -0.2f)
        block.rows.forEach { row ->
            row.mark?.let {
                glyphInRect(
                    s,
                    fonts.symbol,
                    if (it == Mark.DOUBLE) 0xE212 else 0xE211,
                    row.checkbox,
                    0.55f,
                    0.55f,
                    opticalX = if (it == Mark.DOUBLE) 2.0f else 1.65f,
                    opticalY = -0.6f,
                )
            }
            centeredAboveRule(s, fonts.semibold, row.valueRule, row.value, 8.8f, 1.8f)
        }
    }

    private fun drawV2Attacks(s: PDFormContentStream, fonts: Fonts) {
        V2_ATTACK_RULE_Y.zip(ATTACKS).forEach { (y, row) ->
            textAboveRule(s, fonts.regular, Rule(184.5f, 408f, y), row.name, 9.25f, 8.5f, 2.5f, 2f)
            centeredAboveRule(s, fonts.semibold, Rule(411.5f, 453.5f, y), row.attack, 9f, 2.3f)
            textAboveRule(s, fonts.regular, Rule(456.5f, 598f, y), row.damage, 9f, 8.5f, 2.5f, 2f)
        }
    }

    private fun drawV2Traits(s: PDFormContentStream, fonts: Fonts) {
        V2_TRAIT_RULE_Y.forEachIndexed { rowIndex, y ->
            val left = V2_TRAITS.getOrNull(rowIndex * 2)
            val right = V2_TRAITS.getOrNull(rowIndex * 2 + 1)
            left?.let { textAboveRule(s, fonts.regular, Rule(184.5f, 388f, y), it, 9.25f, 8.5f, 2.5f, 2f) }
            right?.let { textAboveRule(s, fonts.regular, Rule(394.5f, 598f, y), it, 9.25f, 8.5f, 2.5f, 2f) }
        }
    }

    private fun drawV2MainSpellSlots(s: PDFormContentStream, fonts: Fonts) {
        V2_SLOT_RULE_Y.forEachIndexed { index, y ->
            centeredAboveRule(s, fonts.semibold, Rule(28.5f, 56.5f, y), V2_SLOT_TOTALS[index].toString(), 12f, 2.2f)
            // ESPACIOS GASTADOS intentionally stays empty.
        }
    }

    private fun drawV2SpellSummary(s: PDFormContentStream, fonts: Fonts) {
        centered(s, fonts.semibold, TopRect(118f, 600f, 44f, 29f), "15", 15f, -0.3f)
        centered(s, fonts.semibold, TopRect(118f, 660f, 44f, 29f), "+7", 15f, -0.3f)
        centered(s, fonts.semibold, TopRect(118f, 719f, 44f, 29f), "INT", 13.5f, -0.3f)
    }

    private fun drawV2Treasure(s: PDFormContentStream, fonts: Fonts) {
        V2_COIN_RULE_Y.zip(listOf("4", "137", "48", "19")).forEach { (y, value) ->
            centeredAboveRule(s, fonts.semibold, Rule(233f, 343f, y), value, 10.5f, 2.2f)
        }
        V2_OBJECT_RULE_Y.zip(listOf("Broche élfico", "Gema lunar", "Mapa sellado", "Tubo de plata")).forEach { (y, value) ->
            textAboveRule(s, fonts.regular, Rule(184.5f, 343f, y), value, 9.25f, 8.5f, 2.5f, 2f)
        }
    }

    private fun drawV2Other(s: PDFormContentStream, fonts: Fonts) {
        V2_OTHER_RULE_Y.zip(listOf(
            "2 virotes de plata",
            "Mapa de la torre",
            "Llave con sello azul",
            "Carta de Maestra Elenya",
        )).forEach { (y, value) ->
            textAboveRule(s, fonts.regular, Rule(349f, 598f, y), value, 9.25f, 8.5f, 2.5f, 2f)
        }
    }

    private fun drawV2CombinedEquipment(s: PDFormContentStream, fonts: Fonts) {
        V2_EQUIPMENT_RULE_Y.forEachIndexed { index, y ->
            V2_EQUIPMENT_ITEMS.getOrNull(index * 2)?.let {
                textAboveRule(s, fonts.regular, Rule(14f, 149.5f, y), it, 9.25f, 8.5f, 2.5f, 2f)
            }
            V2_EQUIPMENT_ITEMS.getOrNull(index * 2 + 1)?.let {
                textAboveRule(s, fonts.regular, Rule(156f, 291.5f, y), it, 9.25f, 8.5f, 2.5f, 2f)
            }
        }
    }

    private fun drawV2CombinedBackground(s: PDFormContentStream, fonts: Fonts) {
        drawRuledParagraph(s, fonts.regular, V2_BACKGROUND_RULES, "Sabio de la Academia de Liria", 9.25f, 2.6f, 2f)
        drawRuledParagraph(
            s, fonts.regular, V2_BONDS_RULES,
            "Prometió devolver a la Academia un códice perdido y proteger a sus compañeros durante la búsqueda.",
            9.25f, 2.6f, 2f,
        )
        drawRuledParagraph(
            s, fonts.regular, V2_IDEALS_RULES,
            "Conocimiento y responsabilidad. La información peligrosa debe estudiarse antes de compartirse.",
            9.25f, 2.6f, 2f,
        )
        drawRuledParagraph(
            s, fonts.regular, V2_STORY_RULES,
            "Aster abandonó temporalmente los archivos de Liria tras encontrar referencias a una cámara sellada bajo el Valle del Viento. Viaja con un pequeño grupo para reconstruir la ruta, comparar los sellos encontrados y recuperar un códice perdido antes de que otra expedición alcance las ruinas.",
            9.25f, 2.8f, 2f,
        )
    }

    private fun drawV2CombinedSpecialEquipment(s: PDFormContentStream, fonts: Fonts) {
        V2_SPECIAL_RULE_Y.forEachIndexed { index, y ->
            val entry = V2_SPECIAL_EQUIPMENT.getOrNull(index) ?: return@forEachIndexed
            if (entry.checked) {
                glyphInRect(
                    s,
                    fonts.symbol,
                    0xE211,
                    TopRect(87.5f, V2_SPECIAL_CHECK_TOP[index], 8.5f, 9f),
                    0.5f,
                    0.5f,
                    opticalX = 1.75f,
                    opticalY = -0.7f,
                )
            }
            textAboveRule(s, fonts.regular, Rule(99f, 297f, y), entry.name, 9.25f, 8.5f, 2.5f, 2f)
            textAboveRule(s, fonts.regular, Rule(303f, 596f, y), entry.description, 9.25f, 8.5f, 2.5f, 2f)
        }
    }

    private fun drawV2SpellBlock(s: PDFormContentStream, fonts: Fonts, block: V2SpellBlock) {
        block.total?.let { total ->
            block.totalRect?.let { centered(s, fonts.semibold, it, total.toString(), 14.5f, -0.3f) }
        }
        block.spells.take(block.maxRows).forEachIndexed { index, spell ->
            val ruleY = block.firstRuleY + index * block.rowStep
            if (block.level > 0 && spell.prepared && block.checkX != null && block.firstCheckTop != null) {
                glyphInRect(
                    s,
                    fonts.symbol,
                    0xE211,
                    TopRect(block.checkX, block.firstCheckTop + index * block.rowStep, 8.5f, 8.5f),
                    0.5f,
                    0.5f,
                    opticalX = 1.7f,
                    opticalY = -0.6f,
                )
            }
            textAboveRule(
                s,
                fonts.regular,
                Rule(block.textStartX, block.textEndX, ruleY),
                spell.name,
                9.25f,
                8.75f,
                3f,
                2f,
            )
        }
        // Durable semantic: ESPACIOS GASTADOS remains blank.
    }

    private fun drawV2NotesLeft(s: PDFormContentStream, fonts: Fonts) {
        drawRuledParagraph(s, fonts.regular, V2_NOTES_LEFT_RULES, V2_NOTES_LEFT_TEXT, 9.25f, 2.8f, 2f)
    }

    private fun drawV2NotesRight(s: PDFormContentStream, fonts: Fonts) {
        drawRuledParagraph(s, fonts.regular, V2_NOTES_RIGHT_RULES, V2_NOTES_RIGHT_TEXT, 9.25f, 2.8f, 2f)
    }

    private fun drawV2NotesDoodles(s: PDFormContentStream) {
        s.saveGraphicsState()
        s.setStrokingColor(Color.BLACK)
        s.setLineWidth(0.9f)
        s.addRect(92f, 212f, 48f, 48f)
        s.stroke()
        s.moveTo(116f, 256f)
        s.lineTo(134f, 212f)
        s.lineTo(98f, 212f)
        s.closePath()
        s.stroke()
        s.addRect(352f, 215f, 78f, 42f)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun renderAllHybrid(doc: PDDocument, layers: LayerUtility, fonts: Fonts) {
        val main = doc.getPage(0)
        val equipment = doc.getPage(1)
        val narrative = doc.getPage(2)
        val spells = doc.getPage(3)
        val notes = doc.getPage(4)

        appendSection(doc, layers, main, "Run7 MAIN - Identification") { drawIdentification(it, fonts) }
        appendSection(doc, layers, main, "Run7 MAIN - Portrait") { drawPortraitStickFigure(it) }
        appendSection(doc, layers, main, "Run7 MAIN - Defense") { drawDefense(it, fonts) }
        appendSection(doc, layers, main, "Run7 MAIN - Core Stats") { drawCoreStats(it, fonts) }
        appendSection(doc, layers, main, "Run7 MAIN - Abilities") { drawAbilities(it, fonts) }
        SKILL_SECTIONS.forEach { section ->
            appendSection(doc, layers, main, "Run7 MAIN - Skills ${section.name}") {
                drawSkillSection(it, fonts, section)
            }
        }
        appendSection(doc, layers, main, "Run7 MAIN - Spellcasting Summary") { drawSpellcastingSummary(it, fonts) }
        appendSection(doc, layers, main, "Run7 MAIN - Attacks") { drawAttacks(it, fonts) }
        appendSection(doc, layers, main, "Run7 MAIN - Traits") { drawTraits(it, fonts) }

        appendSection(doc, layers, equipment, "Run7 EQUIPMENT - Ordinary") { drawEquipment(it, fonts) }
        appendSection(doc, layers, equipment, "Run7 EQUIPMENT - Coins") { drawCurrencies(it, fonts) }
        appendSection(doc, layers, equipment, "Run7 EQUIPMENT - Valuables") { drawValuables(it, fonts) }
        appendSection(doc, layers, equipment, "Run7 EQUIPMENT - Special") { drawSpecialEquipment(it, fonts) }

        appendSection(doc, layers, narrative, "Run7 NARRATIVE - Background fields") { drawBackgroundFields(it, fonts) }
        appendSection(doc, layers, narrative, "Run7 NARRATIVE - Personality") { drawPersonality(it, fonts) }
        appendSection(doc, layers, narrative, "Run7 NARRATIVE - Other Traits") { drawOtherTraits(it, fonts) }
        appendSection(doc, layers, narrative, "Run7 NARRATIVE - Story") { drawStory(it, fonts) }
        appendSection(doc, layers, narrative, "Run7 NARRATIVE - Notes") { drawNarrativeNotes(it, fonts) }

        appendSection(doc, layers, spells, "Run7 SPELLS - Cantrips") { drawCantrips(it, fonts) }
        appendSection(doc, layers, spells, "Run7 SPELLS - Level 1") { drawLevelOneSpells(it, fonts) }
        SPELL_LEVELS.filter { it.level >= 2 }.forEach { geometry ->
            appendSection(doc, layers, spells, "Run7 SPELLS - Level ${geometry.level}") {
                drawSpellLevel(it, fonts, geometry)
            }
        }

        appendSection(doc, layers, notes, "Run7 NOTES - Text") { drawNotesPage(it, fonts) }
        appendSection(doc, layers, notes, "Run7 NOTES - Doodles") { drawNotesDoodles(it) }
    }

    private fun appendSection(
        doc: PDDocument,
        layers: LayerUtility,
        page: PDPage,
        layerName: String,
        draw: (PDFormContentStream) -> Unit,
    ) {
        val form = PDFormXObject(doc).apply {
            resources = PDResources()
            setBBox(PDRectangle(page.cropBox.width, page.cropBox.height))
        }
        PDFormContentStream(form).use { stream ->
            stream.setNonStrokingColor(Color.BLACK)
            draw(stream)
        }
        layers.appendFormAsLayer(page, form, AffineTransform(), layerName)
    }

    private fun drawIdentification(s: PDFormContentStream, fonts: Fonts) {
        textAboveRule(s, fonts.handwritten, Rule(307.417f, 442.063f, 53.508f), "Mago 5 / Pícaro 2", 10.5f, 9f, 2.2f, 2f)
        textAboveRule(s, fonts.handwritten, Rule(307.417f, 442.063f, 73.350f), "Elfo Alto", 10.5f, 9f, 2.2f, 2f)
        textAboveRule(s, fonts.handwritten, Rule(307.417f, 442.063f, 93.193f), "Neutral Bueno", 10.5f, 9f, 2.2f, 2f)

        centeredAboveRule(s, fonts.handwritten, Rule(134.504f, 233.717f, 113.035f), "23.000 PX", 10.5f, 2.2f)
        centeredAboveRule(s, fonts.handwritten, Rule(341.433f, 440.646f, 113.035f), "34.000 PX", 10.5f, 2.2f)

        centered(s, fonts.handwritten, TopRect(455f, 190f, 132f, 28f), "Aster Vale", 14f, 0f)
    }

    private fun drawDefense(s: PDFormContentStream, fonts: Fonts) {
        centered(s, fonts.semibold, TopRect(30f, 135f, 55f, 65f), "16", 22f, -1f)
        centeredAboveRule(s, fonts.semibold, Rule(98.5f, 158f, 138.5f), "+3", 10.5f, 2.5f)
        centeredAboveRule(s, fonts.semibold, Rule(98.5f, 158f, 168.2f), "12", 10.5f, 2.5f)
        centeredAboveRule(s, fonts.semibold, Rule(98.5f, 158f, 198.0f), "+2", 10.5f, 2.5f)
        centeredAboveRule(s, fonts.semibold, Rule(98.5f, 158f, 226.6f), "-1", 10.5f, 2.5f)
    }

    private fun drawCoreStats(s: PDFormContentStream, fonts: Fonts) {
        centered(s, fonts.semibold, TopRect(171f, 119f, 57.5f, 38f), "+4", 16f, -0.4f)
        centered(s, fonts.semibold, TopRect(242f, 119f, 57.5f, 38f), "+3", 16f, -0.4f)
        centered(s, fonts.semibold, TopRect(312.5f, 119f, 57.5f, 38f), "34", 17f, -0.4f)
        centered(s, fonts.semibold, TopRect(383.5f, 151f, 57.5f, 45f), "27", 21f, -0.7f)
        centered(s, fonts.semibold, TopRect(171f, 188f, 57.5f, 37f), "30", 16f, -0.4f)
        glyphInRect(s, fonts.symbol, 0xE20E, TopRect(260f, 195f, 21f, 21f), 1.0f, 1.0f)
        centered(s, fonts.semibold, TopRect(312f, 188f, 59f, 37f), "4d6 / 1d8", 10.5f, -0.2f)
    }

    private fun drawAbilities(s: PDFormContentStream, fonts: Fonts) {
        ABILITIES.forEach { a ->
            centered(s, fonts.semibold, TopRect(a.scoreX - 28f, 265.5f, 56f, 31f), a.score, 18f, -0.5f)
            centered(s, fonts.semibold, TopRect(a.modX - 16f, 285.0f, 32f, 20f), a.mod, 11.5f, -0.2f)
        }
    }

    private fun drawSkillSection(s: PDFormContentStream, fonts: Fonts, section: SkillSection) {
        section.rows.forEach { row ->
            if (row.mark != null) {
                glyphInRect(
                    s,
                    fonts.symbol,
                    if (row.mark == Mark.DOUBLE) 0xE212 else 0xE211,
                    row.checkbox,
                    0.6f,
                    0.6f,
                    opticalX = 0.65f,
                )
            }
            centeredAboveRule(s, fonts.semibold, row.valueRule, row.value, 8.8f, 2.0f)
        }
    }

    private fun drawSpellcastingSummary(s: PDFormContentStream, fonts: Fonts) {
        centered(s, fonts.semibold, TopRect(147f, 421f, 55f, 39f), "15", 16f, -0.5f)
        centered(s, fonts.semibold, TopRect(147f, 496f, 55f, 39f), "+7", 16f, -0.5f)
        centered(s, fonts.semibold, TopRect(147f, 568f, 55f, 39f), "INT", 14f, -0.4f)
    }

    private fun drawAttacks(s: PDFormContentStream, fonts: Fonts) {
        ATTACK_RULE_Y.zip(ATTACKS).forEach { (ruleY, row) ->
            textAboveRule(s, fonts.regular, Rule(215.291f, 354.189f, ruleY), row.name, 9.25f, 8.5f, 2.5f, 2.2f)
            textAboveRule(s, fonts.regular, Rule(357.024f, 413.717f, ruleY), row.range, 9f, 8.5f, 2.5f, 1.5f)
            textAboveRule(s, fonts.regular, Rule(416.551f, 461.905f, ruleY), row.attack, 9f, 8.5f, 2.5f, 1.5f)
            textAboveRule(s, fonts.regular, Rule(460.968f, 583.803f, ruleY), row.damage, 9f, 8.5f, 2.5f, 2.0f)
        }
    }

    private fun drawTraits(s: PDFormContentStream, fonts: Fonts) {
        val rows = listOf(
            661.5f to listOf("Visión en la oscuridad", "Recuperación Arcana", "Ataque furtivo 1d6"),
            681.5f to listOf("Trance", "Acción astuta", "Erudito arcano"),
        )
        rows.forEach { (y, names) ->
            TRAIT_COLUMNS.zip(names).forEach { (rule, text) ->
                textAboveRule(s, fonts.regular, rule.copy(topY = y), text, 9.25f, 8.5f, 2.5f, 2.0f)
            }
        }
    }

    private fun drawBackgroundFields(s: PDFormContentStream, fonts: Fonts) {
        drawRuledParagraph(s, fonts.regular, BACKGROUND_RULES, "Sabio de la Academia de Liria", 9.25f, 2.6f, 2f)
        drawRuledParagraph(s, fonts.regular, IDEALS_RULES, "Conocimiento. La información debe conservarse, contrastarse y compartirse con responsabilidad.", 9.25f, 2.6f, 2f)
        drawRuledParagraph(s, fonts.regular, BONDS_RULES, "Prometió devolver a la Academia un códice perdido y proteger a sus compañeros durante la búsqueda.", 9.25f, 2.6f, 2f)
        drawRuledParagraph(s, fonts.regular, FLAWS_RULES, "Subestima los riesgos cuando aparece una oportunidad de estudiar magia desconocida.", 9.25f, 2.6f, 2f)
    }

    private fun drawPersonality(s: PDFormContentStream, fonts: Fonts) {
        drawRuledParagraph(
            s, fonts.regular, PERSONALITY_RULES,
            "Curiosidad académica: toma notas de todo fenómeno extraño y formula preguntas incluso en situaciones incómodas.",
            9.25f, 2.6f, 2f,
        )
    }

    private fun drawOtherTraits(s: PDFormContentStream, fonts: Fonts) {
        OTHER_TRAIT_RULES.zip(OTHER_TRAITS).forEach { (rule, trait) ->
            textAboveRule(s, fonts.regular, rule, trait, 9.25f, 8.5f, 2.5f, 2f)
        }
    }

    private fun drawNarrativeNotes(s: PDFormContentStream, fonts: Fonts) {
        drawRuledParagraph(
            s, fonts.regular, NARRATIVE_NOTES_RULES,
            "Contactar a Maestra Elenya al regresar a Liria. No entregar el mapa original a terceros. Preparar tinta resistente al agua antes de entrar en las ruinas. Revisar el corredor norte antes de acampar. La puerta con sello azul responde al mismo patrón visto en la torre. Mantener una copia separada del alfabeto parcial.",
            9.25f, 2.8f, 2f,
        )
    }

    private fun drawRuledParagraph(
        s: PDFormContentStream,
        font: PDFont,
        rules: List<Rule>,
        text: String,
        size: Float,
        clearance: Float,
        leftPadding: Float,
    ) {
        if (rules.isEmpty() || text.isBlank()) return
        val width = rules.first().endX - rules.first().startX - leftPadding - 1f
        val lines = wrapByWidth(font, text, size, width)
        rules.zip(lines.take(rules.size)).forEach { (rule, line) ->
            textAboveRule(s, font, rule, line, size, size, clearance, leftPadding)
        }
    }

    private fun drawStory(s: PDFormContentStream, fonts: Fonts) {
        val story =
            "Aster abandonó temporalmente los archivos de Liria después de hallar referencias a una cámara sellada " +
                "bajo el Valle del Viento. Viaja con un pequeño grupo para reconstruir la ruta y comprobar el códice."
        val lines = wrapByWidth(fonts.regular, story, 9.25f, 365f)
        STORY_RULE_Y.zip(lines.take(STORY_RULE_Y.size)).forEach { (y, line) ->
            textAboveRule(s, fonts.regular, Rule(215.291f, 583.795f, y), line, 9.25f, 9f, 2.8f, 2.0f)
        }
    }

    private fun drawCantrips(s: PDFormContentStream, fonts: Fonts) {
        val names = listOf("Luz", "Mano de mago", "Rayo de fuego", "Prestidigitación")
        CANTRIP_RULE_Y.zip(names).forEach { (ruleY, name) ->
            textAboveRule(s, fonts.regular, Rule(39.5f, 203.95f, ruleY), name, 9.25f, 8.75f, 3.2f, 2.0f)
        }
    }

    private fun drawLevelOneSpells(s: PDFormContentStream, fonts: Fonts) {
        centered(s, fonts.semibold, TopRect(56.98f, 291.55f, 27.49f, 27.49f), "4", 15f, -0.35f)
        val spells = listOf(
            SpellRow("Escudo", true),
            SpellRow("Misil mágico", true),
            SpellRow("Detectar magia", false),
            SpellRow("Caída de pluma", true),
            SpellRow("Armadura de mago", false),
        )
        LEVEL1_SQUARES.zip(spells).forEach { (square, spell) ->
            if (spell.prepared) {
                glyphInRect(s, fonts.symbol, 0xE211, square, 0.6f, 0.6f, opticalX = 0.65f)
            }
            textAboveRule(
                s,
                fonts.regular,
                Rule(39.543f, 203.952f, square.top + square.height),
                spell.name,
                9.25f,
                8.75f,
                3.4f,
                2.0f,
            )
        }
    }

    private fun drawPortraitStickFigure(s: PDFormContentStream) {
        // Measured against the actual portrait interior, not the earlier estimated box.
        // Keep a generous optical margin so the QA portrait never touches/covers the frame.
        val left = 462f
        val right = 582f
        val top = 42f
        val bottom = 158f
        val cx = (left + right) / 2f

        s.saveGraphicsState()
        s.setStrokingColor(Color.BLACK)
        s.setLineWidth(1.2f)

        val headCy = H - (top + 24f)
        val headR = 10f
        circlePath(s, cx, headCy, headR)
        s.stroke()

        val neckY = headCy - headR
        val hipY = H - (top + 76f)
        val shoulderY = H - (top + 47f)
        s.moveTo(cx, neckY)
        s.lineTo(cx, hipY)
        s.moveTo(cx, shoulderY)
        s.lineTo(cx - 22f, shoulderY - 17f)
        s.moveTo(cx, shoulderY)
        s.lineTo(cx + 22f, shoulderY - 17f)
        s.moveTo(cx, hipY)
        s.lineTo(cx - 20f, H - bottom)
        s.moveTo(cx, hipY)
        s.lineTo(cx + 20f, H - bottom)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun circlePath(s: PDFormContentStream, cx: Float, cy: Float, radius: Float) {
        val k = radius * 0.5522848f
        s.moveTo(cx + radius, cy)
        s.curveTo(cx + radius, cy + k, cx + k, cy + radius, cx, cy + radius)
        s.curveTo(cx - k, cy + radius, cx - radius, cy + k, cx - radius, cy)
        s.curveTo(cx - radius, cy - k, cx - k, cy - radius, cx, cy - radius)
        s.curveTo(cx + k, cy - radius, cx + radius, cy - k, cx + radius, cy)
        s.closePath()
    }

    private fun drawEquipment(s: PDFormContentStream, fonts: Fonts) {
        EQUIPMENT_RULES.zip(EQUIPMENT_ITEMS).forEach { (rule, item) ->
            textAboveRule(s, fonts.regular, rule, item, 9.25f, 8.5f, 2.5f, 1.5f)
        }
    }

    private fun drawCurrencies(s: PDFormContentStream, fonts: Fonts) {
        CURRENCY_VALUES.forEachIndexed { index, value ->
            centered(s, fonts.semibold, TopRect(535f, 88f + index * 20f, 55f, 18f), value, 10.5f, -0.2f)
        }
    }

    private fun drawValuables(s: PDFormContentStream, fonts: Fonts) {
        VALUABLE_RULE_Y.zip(VALUABLES).forEach { (y, entry) ->
            textAboveRule(s, fonts.regular, Rule(453.402f, 546.945f, y), entry.first, 9.0f, 8.5f, 2.4f, 1.5f)
            centeredAboveRule(s, fonts.semibold, Rule(549.779f, 583.795f, y), entry.second, 9.5f, 2.4f)
        }
    }

    private fun drawSpecialEquipment(s: PDFormContentStream, fonts: Fonts) {
        SPECIAL_RULE_Y.zip(SPECIAL_EQUIPMENT).forEachIndexed { index, (y, entry) ->
            if (entry.checked) {
                glyphInRect(
                    s,
                    fonts.symbol,
                    0xE211,
                    TopRect(113.244f, SPECIAL_CHECK_TOP[index], 9.669f, 12.287f),
                    0.6f,
                    0.6f,
                )
            }
            textAboveRule(s, fonts.regular, Rule(127.5f, 210f, y), entry.name, 9.0f, 8.5f, 2.4f, 1.5f)
            textAboveRule(s, fonts.regular, Rule(240.803f, 583.795f, y), entry.description, 9.0f, 8.5f, 2.4f, 1.5f)
        }
    }

    private fun drawSpellLevel(s: PDFormContentStream, fonts: Fonts, g: SpellLevelGeometry) {
        centered(s, fonts.semibold, g.totalRect, g.total.toString(), 15f, -0.35f)
        g.spells.take(g.maxRows).forEachIndexed { index, spell ->
            val square = TopRect(g.squareX, g.firstSquareTop + index * g.rowStep, 9.669f, 12.287f)
            if (spell.prepared) {
                glyphInRect(s, fonts.symbol, 0xE211, square, 0.6f, 0.6f, opticalX = 0.65f)
            }
            textAboveRule(
                s, fonts.regular,
                Rule(g.textX, g.textX + 164.409f, square.top + square.height),
                spell.name, 9.25f, 8.75f, 3.4f, 2f,
            )
        }
        // ESPACIOS GASTADOS intentionally remains empty.
    }

    private fun drawNotesPage(s: PDFormContentStream, fonts: Fonts) {
        drawRuledParagraph(s, fonts.regular, NOTES_LEFT_RULES, NOTES_LEFT_TEXT, 9.25f, 2.8f, 2f)
        drawRuledParagraph(s, fonts.regular, NOTES_RIGHT_RULES, NOTES_RIGHT_TEXT, 9.25f, 2.8f, 2f)
    }

    private fun drawNotesDoodles(s: PDFormContentStream) {
        s.saveGraphicsState()
        s.setStrokingColor(Color.BLACK)
        s.setLineWidth(0.9f)

        s.addRect(98f, 221f, 43f, 43f)
        s.stroke()
        s.moveTo(119.5f, 261f)
        s.lineTo(134f, 221f)
        s.lineTo(105f, 221f)
        s.closePath()
        s.stroke()

        s.addRect(320f, 216f, 76f, 43f)
        s.stroke()
        s.moveTo(342f, 216f)
        s.lineTo(342f, 259f)
        s.moveTo(368f, 232f)
        s.lineTo(396f, 232f)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun textAboveRule(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        text: String,
        preferredSize: Float,
        minimumSize: Float,
        clearance: Float,
        leftPadding: Float = 0f,
    ) {
        val available = rule.endX - rule.startX - leftPadding - 1f
        var size = preferredSize
        while (size > minimumSize && textWidth(font, text, size) > available) size -= 0.25f
        require(textWidth(font, text, size) <= available + 0.05f) { "Text does not fit rule: '$text'" }
        val descriptor = requireNotNull(font.fontDescriptor)
        val descent = descriptor.descent / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rule.startX + leftPadding, baseline)
        s.showText(text)
        s.endText()
    }

    private fun centeredAboveRule(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        text: String,
        size: Float,
        clearance: Float,
    ) {
        val width = textWidth(font, text, size)
        val descriptor = requireNotNull(font.fontDescriptor)
        val descent = descriptor.descent / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rule.startX + (rule.endX - rule.startX - width) / 2f, baseline)
        s.showText(text)
        s.endText()
    }

    private fun centered(
        s: PDFormContentStream,
        font: PDFont,
        r: TopRect,
        text: String,
        size: Float,
        opticalY: Float,
    ) {
        val width = textWidth(font, text, size)
        val ascent = (font.fontDescriptor?.ascent?.takeIf { it > 0 } ?: 750f) / 1000f * size
        val descent = abs(font.fontDescriptor?.descent?.takeIf { it < 0 } ?: -250f) / 1000f * size
        val bottom = H - (r.top + r.height)
        val baseline = bottom + (r.height - ascent - descent) / 2f + descent + opticalY
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(r.x + (r.width - width) / 2f, baseline)
        s.showText(text)
        s.endText()
    }

    private fun glyphInRect(
        s: PDFormContentStream,
        font: PDFont,
        cp: Int,
        r: TopRect,
        insetX: Float,
        insetY: Float,
        opticalX: Float = 0f,
        opticalY: Float = 0f,
    ) {
        val glyph = String(Character.toChars(cp))
        val normalizedWidth = font.getStringWidth(glyph) / 1000f
        val descriptor = requireNotNull(font.fontDescriptor)
        val normalizedAscent = descriptor.ascent / 1000f
        val normalizedDescent = descriptor.descent / 1000f
        val normalizedHeight = normalizedAscent - normalizedDescent
        require(normalizedWidth > 0f && normalizedHeight > 0f)

        val targetWidth = r.width - insetX * 2f
        val targetHeight = r.height - insetY * 2f
        val scaleX = targetWidth / normalizedWidth
        val scaleY = targetHeight / normalizedHeight
        val left = r.x + insetX + opticalX
        val targetBottom = H - (r.top + r.height) + insetY + opticalY
        val baseline = targetBottom - normalizedDescent * scaleY

        s.beginText()
        s.setFont(font, 1f)
        s.setTextMatrix(Matrix(scaleX, 0f, 0f, scaleY, left, baseline))
        s.showText(glyph)
        s.endText()
    }

    private fun wrapByWidth(font: PDFont, text: String, size: Float, maxWidth: Float): List<String> {
        val out = mutableListOf<String>()
        var current = ""
        text.trim().split(Regex("\\s+")).forEach { word ->
            val candidate = if (current.isBlank()) word else "$current $word"
            if (textWidth(font, candidate, size) <= maxWidth) current = candidate
            else {
                if (current.isNotBlank()) out += current
                current = word
            }
        }
        if (current.isNotBlank()) out += current
        return out
    }

    private fun textWidth(font: PDFont, text: String, size: Float): Float =
        font.getStringWidth(text) / 1000f * size

    private fun assertHybridFontsEmbedded(doc: PDDocument) {
        val fonts = buildList {
            repeat(doc.numberOfPages) { pageIndex -> addAll(fontsIn(doc.getPage(pageIndex).resources)) }
        }.distinctBy { it.cosObject }
        val hybrid = fonts.filter { font ->
            font.name.contains("FiraSans", ignoreCase = true) ||
                font.name.contains("Kalam", ignoreCase = true) ||
                font.name.contains("ParaHojadePJSymbols", ignoreCase = true) ||
                font.name.contains("Para Hoja de PJ", ignoreCase = true)
        }
        assertTrue(hybrid.size >= 4)
        assertTrue(hybrid.all { it.isEmbedded })
    }

    private fun fontsIn(resources: PDResources?): List<PDFont> {
        if (resources == null) return emptyList()
        val direct = resources.fontNames.mapNotNull { resources.getFont(it) }
        val nested = resources.xObjectNames.flatMap { name ->
            when (val x: PDXObject? = resources.getXObject(name)) {
                is PDFormXObject -> fontsIn(x.resources)
                else -> emptyList()
            }
        }
        return direct + nested
    }

    private class Fonts(doc: PDDocument) {
        val regular = font(doc, "fonts/pdf/text/FiraSans-Regular.ttf")
        val semibold = font(doc, "fonts/pdf/text/FiraSans-SemiBold.ttf")
        val handwritten = font(doc, "fonts/pdf/text/Kalam-Bold.ttf")
        val symbol = font(doc, "fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf")
    }

    private data class Rule(val startX: Float, val endX: Float, val topY: Float)
    private data class TopRect(val x: Float, val top: Float, val width: Float, val height: Float)
    private data class AbilityPlacement(val scoreX: Float, val modX: Float, val score: String, val mod: String)
    private data class SkillRow(val checkbox: TopRect, val valueRule: Rule, val value: String, val mark: Mark?)
    private data class SkillSection(val name: String, val rows: List<SkillRow>)
    private enum class Mark { SINGLE, DOUBLE }
    private data class AttackRow(val name: String, val range: String, val attack: String, val damage: String)
    private data class SpellRow(val name: String, val prepared: Boolean)
    private data class SpecialEquipmentEntry(val name: String, val description: String, val checked: Boolean)
    private data class SpellLevelGeometry(
        val level: Int,
        val total: Int,
        val totalRect: TopRect,
        val squareX: Float,
        val firstSquareTop: Float,
        val textX: Float,
        val rowStep: Float,
        val maxRows: Int,
        val spells: List<SpellRow>,
    )

    private val V2_PER_ABILITY_SCORE_BLOCKS = listOf(
        V2AbilityBlock(TopRect(14f, 167f, 58.5f, 28f), TopRect(49.5f, 186.5f, 44f, 22.5f), "10", "+0", emptyList()),
        V2AbilityBlock(TopRect(14f, 238.5f, 58.5f, 28f), TopRect(49.5f, 258f, 44f, 22.5f), "16", "+3", emptyList()),
        V2AbilityBlock(TopRect(14f, 311f, 58.5f, 28f), TopRect(49.5f, 330.25f, 44f, 22.5f), "14", "+2", emptyList()),
        V2AbilityBlock(TopRect(14f, 383f, 58.5f, 28f), TopRect(49.5f, 402.75f, 44f, 22.5f), "18", "+4", emptyList()),
        V2AbilityBlock(TopRect(14f, 455.5f, 58.5f, 28f), TopRect(49.5f, 475f, 44f, 22.5f), "12", "+1", emptyList()),
        V2AbilityBlock(TopRect(14f, 527.75f, 58.5f, 28f), TopRect(49.5f, 547.25f, 44f, 22.5f), "8", "-1", emptyList()),
    )

    private val V2_PER_ABILITY_SAVES = listOf(
        V2AbilityCentralRow(TopRect(98f, 171.5f, 8.5f, 9f), Rule(161.5f, 178f, 183f), "+0", null),
        V2AbilityCentralRow(TopRect(98f, 186.5f, 8.5f, 9f), Rule(161.5f, 178f, 198f), "+6", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 201f, 8.5f, 9f), Rule(161.5f, 178f, 212.5f), "+2", null),
        V2AbilityCentralRow(TopRect(98f, 216f, 8.5f, 9f), Rule(161.5f, 178f, 227.5f), "+7", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 230.5f, 8.5f, 9f), Rule(161.5f, 178f, 242f), "+1", null),
        V2AbilityCentralRow(TopRect(98f, 245.5f, 8.5f, 9f), Rule(161.5f, 178f, 257f), "-1", null),
    )

    private val V2_PER_ABILITY_SKILLS = listOf(
        V2AbilityCentralRow(TopRect(98f, 302f, 8.5f, 9f), Rule(161.5f, 178f, 313.5f), "+6", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 317f, 8.5f, 9f), Rule(161.5f, 178f, 328.5f), "+0", null),
        V2AbilityCentralRow(TopRect(98f, 332f, 8.5f, 9f), Rule(161.5f, 178f, 343.5f), "+7", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 347f, 8.5f, 9f), Rule(161.5f, 178f, 358.5f), "-1", null),
        V2AbilityCentralRow(TopRect(98f, 362f, 8.5f, 9f), Rule(161.5f, 178f, 373.5f), "-1", null),
        V2AbilityCentralRow(TopRect(98f, 377f, 8.5f, 9f), Rule(161.5f, 178f, 388.5f), "-1", null),
        V2AbilityCentralRow(TopRect(98f, 392f, 8.5f, 9f), Rule(161.5f, 178f, 403.5f), "+7", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 407f, 8.5f, 9f), Rule(161.5f, 178f, 418.5f), "+9", Mark.DOUBLE),
        V2AbilityCentralRow(TopRect(98f, 421.5f, 8.5f, 9f), Rule(161.5f, 178f, 433f), "+7", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 436.5f, 8.5f, 9f), Rule(161.5f, 178f, 448f), "+4", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 451.5f, 8.5f, 9f), Rule(161.5f, 178f, 463f), "+4", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 466.5f, 8.5f, 9f), Rule(161.5f, 178f, 478f), "+4", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 481.5f, 8.5f, 9f), Rule(161.5f, 178f, 493f), "+4", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 496.5f, 8.5f, 9f), Rule(161.5f, 178f, 508f), "+2", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 511.5f, 8.5f, 9f), Rule(161.5f, 178f, 523f), "+7", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 526.5f, 8.5f, 9f), Rule(161.5f, 178f, 538f), "+6", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 541.5f, 8.5f, 9f), Rule(161.5f, 178f, 553f), "+4", Mark.SINGLE),
        V2AbilityCentralRow(TopRect(98f, 556.5f, 8.5f, 9f), Rule(161.5f, 178f, 568f), "+1", null),
    )

    private val V2_ABILITY_BLOCKS = listOf(
        V2AbilityBlock(
            TopRect(14f, 121.5f, 58.5f, 28f), TopRect(49.5f, 139f, 44f, 22.5f), "10", "+0",
            listOf(
                V2SkillRow(TopRect(98.5f, 125f, 8.5f, 9f), Rule(159f, 178.5f, 134.5f), "+0", Mark.SINGLE),
                V2SkillRow(TopRect(98.5f, 137f, 8.5f, 9f), Rule(159f, 178.5f, 146f), "+3", Mark.SINGLE),
            ),
        ),
        V2AbilityBlock(
            TopRect(14f, 201.5f, 58.5f, 28f), TopRect(49.5f, 219f, 44f, 22.5f), "16", "+3",
            listOf(
                V2SkillRow(TopRect(98.5f, 193.5f, 8.5f, 9f), Rule(159f, 178.5f, 203f), "+6", Mark.SINGLE),
                V2SkillRow(TopRect(98.5f, 205f, 8.5f, 9f), Rule(159f, 178.5f, 214.5f), "+6", Mark.SINGLE),
                V2SkillRow(TopRect(98.5f, 216.5f, 8.5f, 9f), Rule(159f, 178.5f, 226f), "+9", Mark.DOUBLE),
                V2SkillRow(TopRect(98.5f, 228f, 8.5f, 9f), Rule(159f, 178.5f, 237.5f), "+6", Mark.SINGLE),
            ),
        ),
        V2AbilityBlock(
            TopRect(14f, 281.5f, 58.5f, 28f), TopRect(49.5f, 299f, 44f, 22.5f), "14", "+2",
            listOf(
                V2SkillRow(TopRect(98.5f, 290.5f, 8.5f, 9f), Rule(159f, 178.5f, 300f), "+2", null),
            ),
        ),
        V2AbilityBlock(
            TopRect(14f, 361f, 58.5f, 28f), TopRect(49.5f, 378.5f, 44f, 22.5f), "18", "+4",
            listOf(
                V2SkillRow(TopRect(98.5f, 342f, 8.5f, 9f), Rule(159f, 178.5f, 351.5f), "+7", Mark.SINGLE),
                V2SkillRow(TopRect(98.5f, 353.5f, 8.5f, 9f), Rule(159f, 178.5f, 363f), "+7", Mark.SINGLE),
                V2SkillRow(TopRect(98.5f, 365f, 8.5f, 9f), Rule(159f, 178.5f, 374f), "+7", Mark.SINGLE),
                V2SkillRow(TopRect(98.5f, 376f, 8.5f, 9f), Rule(159f, 178.5f, 385.5f), "+4", null),
                V2SkillRow(TopRect(98.5f, 387.5f, 8.5f, 9f), Rule(159f, 178.5f, 397f), "+4", null),
                V2SkillRow(TopRect(98.5f, 399f, 8.5f, 9f), Rule(159f, 178.5f, 408.5f), "+4", null),
            ),
        ),
        V2AbilityBlock(
            TopRect(14f, 441f, 58.5f, 28f), TopRect(49.5f, 458.5f, 44f, 22.5f), "12", "+1",
            listOf(
                V2SkillRow(TopRect(98.5f, 422f, 8.5f, 9f), Rule(159f, 178.5f, 431.5f), "+1", null),
                V2SkillRow(TopRect(98.5f, 434f, 8.5f, 9f), Rule(159f, 178.5f, 442.5f), "+4", Mark.SINGLE),
                V2SkillRow(TopRect(98.5f, 444.5f, 8.5f, 9f), Rule(159f, 178.5f, 454f), "+1", null),
                V2SkillRow(TopRect(98.5f, 456f, 8.5f, 9f), Rule(159f, 178.5f, 465.5f), "+1", null),
                V2SkillRow(TopRect(98.5f, 468f, 8.5f, 9f), Rule(159f, 178.5f, 477f), "+1", null),
                V2SkillRow(TopRect(98.5f, 479f, 8.5f, 9f), Rule(159f, 178.5f, 488.5f), "+1", null),
            ),
        ),
        V2AbilityBlock(
            TopRect(14f, 520.5f, 58.5f, 28f), TopRect(49.5f, 538.5f, 44f, 22.5f), "8", "-1",
            listOf(
                V2SkillRow(TopRect(98.5f, 507.5f, 8.5f, 9f), Rule(159f, 178.5f, 517f), "-1", null),
                V2SkillRow(TopRect(98.5f, 519f, 8.5f, 9f), Rule(159f, 178.5f, 528f), "-1", null),
                V2SkillRow(TopRect(98.5f, 530f, 8.5f, 9f), Rule(159f, 178.5f, 539.5f), "-1", null),
                V2SkillRow(TopRect(98.5f, 541.5f, 8.5f, 9f), Rule(159f, 178.5f, 551f), "-1", null),
                V2SkillRow(TopRect(98.5f, 553f, 8.5f, 9f), Rule(159f, 178.5f, 562.5f), "-1", null),
            ),
        ),
    )

    private val V2_ATTACK_RULE_Y = listOf(264.5f, 281.5f, 298.5f, 315.5f, 332.5f, 349.5f, 366.5f, 383.5f)
    private val V2_TRAIT_RULE_Y = listOf(437.5f, 454.5f, 471.5f, 488.5f, 505.5f, 522.5f, 539.5f, 556.5f, 573.5f)
    private val V2_TRAITS = listOf(
        "Visión en la oscuridad", "Recuperación Arcana",
        "Trance", "Ataque furtivo 1d6",
        "Acción astuta", "Erudito arcano",
        "Paso feérico", "Herramientas de ladrón",
        "Afinidad ritual", "Alerta académica",
        "Memoria de archivo", "Observador",
    )
    private val V2_SLOT_RULE_Y = listOf(627.5f, 644.5f, 661.5f, 678.5f, 695.5f, 712.5f, 729.5f, 746.5f, 763.5f)
    private val V2_SLOT_TOTALS = listOf(4, 3, 3, 3, 2, 1, 1, 1, 1)
    private val V2_COIN_RULE_Y = listOf(627.5f, 644.5f, 661.5f, 678.5f)
    private val V2_OBJECT_RULE_Y = listOf(712.5f, 729.5f, 746.5f, 763.5f)
    private val V2_OTHER_RULE_Y = listOf(712.5f, 729.5f, 746.5f, 763.5f)

    private val V2_EQUIPMENT_RULE_Y = List(23) { 114.5f + it * 17f }
    private val V2_EQUIPMENT_ITEMS = listOf(
        "Mochila de expedición", "Libro de conjuros",
        "Pociones de curación x3", "Componentes arcanos",
        "Raciones x5", "Odre",
        "Capa gris", "Mapa del Valle",
        "Tiza x8", "Pergaminos x6",
        "Tinta azul x2", "Lupa de latón",
        "Martillo pequeño", "Clavos de hierro x12",
        "Linterna cubierta", "Aceite x4",
        "Manta", "Cuerda de seda",
        "Espejo de acero", "Campanillas x4",
        "Viales vacíos x6", "Daga de plata",
        "Arco corto", "Carcaj",
        "Cuaderno de campo", "Pluma fina",
        "Tubo de mapas", "Sellos de cera",
        "Guantes finos", "Piedra de afilar",
    )
    private val V2_BACKGROUND_RULES = listOf(114.5f, 131.5f, 148.5f).map { Rule(297.5f, 597.5f, it) }
    private val V2_BONDS_RULES = listOf(182.5f, 199.5f, 216.5f).map { Rule(297.5f, 597.5f, it) }
    private val V2_IDEALS_RULES = listOf(250.5f, 267.5f, 284.5f).map { Rule(297.5f, 597.5f, it) }
    private val V2_STORY_RULES = List(11) { Rule(297.5f, 597.5f, 318.5f + it * 17f) }
    private val V2_SPECIAL_RULE_Y = List(14) { 542.5f + it * 17f }
    private val V2_SPECIAL_CHECK_TOP = List(14) { 530.5f + it * 17f }
    private val V2_SPECIAL_EQUIPMENT = listOf(
        SpecialEquipmentEntry("Diadema del Archivo", "Marca ceremonial de acceso.", true),
        SpecialEquipmentEntry("Monóculo rúnico", "Ayuda a inspeccionar glifos finos.", true),
        SpecialEquipmentEntry("Amuleto de Liria", "Recuerdo de la Academia.", true),
        SpecialEquipmentEntry("Anillo académico", "Sello para archivos restringidos.", true),
        SpecialEquipmentEntry("Bastón de fresno", "Foco arcano y arma.", true),
        SpecialEquipmentEntry("Brazal de cobre", "Conserva una carga menor.", true),
        SpecialEquipmentEntry("Brazal de plata", "Pareja del brazal de cobre.", false),
        SpecialEquipmentEntry("Chaleco de placas", "Protección ligera bajo la ropa.", true),
        SpecialEquipmentEntry("Grebas de marcha", "Refuerzo para largas caminatas.", false),
        SpecialEquipmentEntry("Botas de senda", "Suela reforzada.", true),
        SpecialEquipmentEntry("Daga de plata", "Hoja ligera tratada.", true),
        SpecialEquipmentEntry("Guante del escriba", "Estabiliza la mano.", false),
    )

    private val V2_SPELL_BLOCKS = listOf(
        V2SpellBlock(0, null, null, null, null, 14f, 203.5f, 126.5f, 17f, 11,
            listOf(SpellRow("Luz", true), SpellRow("Mano de mago", true), SpellRow("Rayo de fuego", true), SpellRow("Prestidigitación", true), SpellRow("Mensaje", true))),
        V2SpellBlock(1, 4, TopRect(48f, 314f, 23f, 26f), 14f, 346.5f, 25.5f, 203.5f, 358f, 17f, 11,
            listOf(SpellRow("Escudo", true), SpellRow("Misil mágico", true), SpellRow("Detectar magia", false), SpellRow("Caída de pluma", true), SpellRow("Armadura de mago", false))),
        V2SpellBlock(2, 3, TopRect(48f, 546f, 23f, 26f), 14f, 579f, 25.5f, 203.5f, 590.5f, 17f, 11,
            listOf(SpellRow("Imagen múltiple", true), SpellRow("Paso brumoso", true), SpellRow("Invisibilidad", false), SpellRow("Levitar", true))),
        V2SpellBlock(3, 3, TopRect(243.5f, 81f, 23f, 26f), 209.5f, 114f, 221f, 399f, 125.5f, 17f, 11,
            listOf(SpellRow("Contrahechizo", true), SpellRow("Bola de fuego", true), SpellRow("Volar", false), SpellRow("Patrón hipnótico", true))),
        V2SpellBlock(4, 3, TopRect(243.5f, 314f, 23f, 26f), 209.5f, 346.5f, 221f, 399f, 358f, 17f, 11,
            listOf(SpellRow("Puerta dimensional", true), SpellRow("Invisibilidad superior", false), SpellRow("Ojo arcano", true), SpellRow("Polimorfar", false))),
        V2SpellBlock(5, 2, TopRect(243.5f, 546f, 23f, 26f), 209.5f, 579f, 221f, 399f, 590.5f, 17f, 11,
            listOf(SpellRow("Muro de fuerza", true), SpellRow("Telequinesis", false), SpellRow("Cono de frío", true))),
        V2SpellBlock(6, 2, TopRect(439f, 81f, 23f, 26f), 405f, 114f, 416.5f, 594.5f, 125.5f, 17f, 8,
            listOf(SpellRow("Desintegrar", true), SpellRow("Globo de invulnerabilidad", false), SpellRow("Visión verdadera", true))),
        V2SpellBlock(7, 1, TopRect(439f, 266f, 23f, 26f), 405f, 298f, 416.5f, 594.5f, 310f, 17f, 8,
            listOf(SpellRow("Teletransportar", false), SpellRow("Jaula de fuerza", true), SpellRow("Simulacro", false))),
        V2SpellBlock(8, 1, TopRect(439f, 447f, 23f, 26f), 405f, 479.5f, 416.5f, 594.5f, 491.5f, 17f, 7,
            listOf(SpellRow("Laberinto", true), SpellRow("Mente en blanco", false), SpellRow("Semiplano", true))),
        V2SpellBlock(9, 1, TopRect(439f, 614f, 23f, 26f), 405f, 647f, 416.5f, 594.5f, 658.5f, 17f, 7,
            listOf(SpellRow("Deseo", true), SpellRow("Detener el tiempo", false), SpellRow("Prisión", true))),
    )

    private val V2_NOTES_RULE_Y = List(20) { 104f + it * 17f }
    private val V2_NOTES_LEFT_RULES = V2_NOTES_RULE_Y.map { Rule(14f, 302.5f, it) }
    private val V2_NOTES_RIGHT_RULES = V2_NOTES_RULE_Y.map { Rule(309f, 597.5f, it) }
    private val V2_NOTES_LEFT_TEXT =
        "Contactar a Maestra Elenya al regresar a Liria. No entregar el mapa original a terceros. Preparar tinta resistente al agua. Revisar el corredor norte antes de acampar. La puerta con sello azul responde al mismo patrón visto en la torre. Mantener una copia separada del alfabeto parcial. Registrar cada piedra marcada y comprobar las distancias."
    private val V2_NOTES_RIGHT_TEXT =
        "Pista: el sello azul aparece también en las monedas halladas en la torre. Comparar el alfabeto de la puerta norte con las notas del profesor Vael. Ruta: entrada oeste, cámara de columnas, escalera rota, galería azul y archivo inferior. Materiales: tinta, tiza, viales, espejo, cuerda y una linterna adicional."

    private companion object {
        const val W = 612f
        const val H = 792f
        const val TEMPLATE = "character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf"

        val ABILITIES = listOf(
            AbilityPlacement(58f, 85f, "10", "+0"),
            AbilityPlacement(154.25f, 184.25f, "16", "+3"),
            AbilityPlacement(250.75f, 280.5f, "14", "+2"),
            AbilityPlacement(347.25f, 376.75f, "18", "+4"),
            AbilityPlacement(445.5f, 473.25f, "12", "+1"),
            AbilityPlacement(539.75f, 569.5f, "8", "-1"),
        )

        val SKILL_SECTIONS = listOf(
            SkillSection(
                "STR",
                listOf(
                    SkillRow(TopRect(23.035f, 324.386f, 9.669f, 12.287f), Rule(85.398f, 108.075f, 336.972f), "+0", null),
                ),
            ),
            SkillSection(
                "DEX",
                listOf(
                    SkillRow(TopRect(119.413f, 324.353f, 9.669f, 12.286f), Rule(181.776f, 204.453f, 336.972f), "+6", Mark.SINGLE),
                    SkillRow(TopRect(119.413f, 338.526f, 9.669f, 12.287f), Rule(181.776f, 204.453f, 351.146f), "+9", Mark.DOUBLE),
                    SkillRow(TopRect(119.413f, 352.699f, 9.669f, 12.287f), Rule(181.776f, 204.453f, 365.319f), "+6", Mark.SINGLE),
                ),
            ),
            SkillSection(
                "INT",
                listOf(
                    SkillRow(TopRect(312.169f, 324.353f, 9.669f, 12.286f), Rule(374.531f, 397.208f, 336.972f), "+7", Mark.SINGLE),
                    SkillRow(TopRect(312.169f, 338.526f, 9.669f, 12.287f), Rule(374.531f, 397.208f, 351.146f), "+7", Mark.SINGLE),
                    SkillRow(TopRect(312.169f, 352.699f, 9.669f, 12.287f), Rule(374.531f, 397.208f, 365.319f), "+7", Mark.SINGLE),
                    SkillRow(TopRect(312.169f, 366.872f, 9.669f, 12.287f), Rule(374.531f, 397.208f, 379.492f), "+4", null),
                    SkillRow(TopRect(312.169f, 381.045f, 9.669f, 12.287f), Rule(374.531f, 397.208f, 393.665f), "+4", null),
                ),
            ),
            SkillSection(
                "WIS",
                listOf(
                    SkillRow(TopRect(408.547f, 324.353f, 9.669f, 12.286f), Rule(470.909f, 493.586f, 336.972f), "+1", null),
                    SkillRow(TopRect(408.547f, 338.526f, 9.669f, 12.287f), Rule(470.909f, 493.586f, 351.146f), "+2", null),
                    SkillRow(TopRect(408.547f, 352.699f, 9.669f, 12.287f), Rule(470.909f, 493.586f, 365.319f), "+1", null),
                    SkillRow(TopRect(408.547f, 366.872f, 9.669f, 12.287f), Rule(470.909f, 493.586f, 379.492f), "+1", null),
                    SkillRow(TopRect(408.547f, 381.045f, 9.669f, 12.287f), Rule(470.909f, 493.586f, 393.665f), "+1", null),
                ),
            ),
            SkillSection(
                "CHA",
                listOf(
                    SkillRow(TopRect(504.925f, 324.353f, 9.669f, 12.286f), Rule(567.287f, 589.964f, 336.972f), "-1", null),
                    SkillRow(TopRect(504.925f, 338.526f, 9.669f, 12.287f), Rule(567.287f, 589.964f, 351.146f), "-1", null),
                    SkillRow(TopRect(504.925f, 352.699f, 9.669f, 12.287f), Rule(567.287f, 589.964f, 365.319f), "-1", null),
                    SkillRow(TopRect(504.925f, 366.872f, 9.669f, 12.287f), Rule(567.287f, 589.964f, 379.492f), "-1", null),
                ),
            ),
        )

        val ATTACK_RULE_Y = listOf(444.689f, 464.531f, 484.374f, 504.217f, 524.059f)
        val ATTACKS = listOf(
            AttackRow("Bastón de fresno", "5 ft", "+3", "1d6 contundente"),
            AttackRow("Rayo de fuego", "120 ft", "+7", "2d10 fuego"),
            AttackRow("Daga de plata", "20/60 ft", "+6", "1d4+3 perforante"),
            AttackRow("Arco corto", "80/320 ft", "+6", "1d6+3 perforante"),
            AttackRow("Orbe cromático", "90 ft", "+7", "3d8 variable"),
        )

        val TRAIT_COLUMNS = listOf(
            Rule(26f, 188.5f, 0f),
            Rule(212.5f, 375f, 0f),
            Rule(399f, 561.5f, 0f),
        )

        val BACKGROUND_RULES = listOf(109.5f, 129.5f, 149.5f, 169f, 189f, 209f).map { Rule(25f, 181f, it) }
        val PERSONALITY_RULES = listOf(248.5f, 268.5f, 287.5f, 307f, 327f, 347f).map { Rule(25f, 181f, it) }
        val IDEALS_RULES = listOf(387.5f, 407.5f, 426f, 446f, 466f, 485.5f).map { Rule(25f, 181f, it) }
        val BONDS_RULES = listOf(526.5f, 546f, 565f, 585f, 605f, 624.5f).map { Rule(25f, 181f, it) }
        val FLAWS_RULES = listOf(665.5f, 685f, 704f, 725f, 743.5f, 764.5f).map { Rule(25f, 181f, it) }
        val STORY_RULE_Y = listOf(387.996f, 407.839f, 427.681f, 447.524f)
        val NARRATIVE_NOTES_RULES = listOf(606f, 625.5f, 645.5f, 665.5f, 685f, 705f, 725f, 744.5f, 764.5f)
            .map { Rule(215.291f, 583.795f, it) }

        val OTHER_TRAIT_Y = listOf(109.5f, 129.5f, 149.5f, 169f, 189f, 209f, 229f, 248.5f, 268.5f, 288.5f, 308f, 328f)
        val OTHER_TRAIT_RULES = OTHER_TRAIT_Y.flatMap { y ->
            listOf(Rule(215.291f, 396.708f, y), Rule(402.378f, 583.795f, y))
        }
        val OTHER_TRAITS = listOf(
            "Visión en la oscuridad", "Trance",
            "Recuperación Arcana", "Ataque furtivo 1d6",
            "Acción astuta", "Erudito arcano",
            "Paso feérico", "Lenguas élficas",
            "Herramientas de ladrón", "Alerta académica",
            "Memoria de archivo", "Afinidad ritual",
            "Cartografía", "Contacto de Academia",
            "Observador", "Código de campo",
            "Disciplina de estudio", "Improvisador",
        )

        val EQUIPMENT_Y = listOf(108.5f, 128.5f, 148.5f, 168f, 188f, 208f, 228f, 247.5f, 267.5f, 287.5f, 307f, 327f, 347f, 366.5f, 386.5f, 406.5f, 426f, 446f)
        val EQUIPMENT_COLS = listOf(27.5f to 137.5f, 169.937f to 300.331f, 311.669f to 442.063f)
        val EQUIPMENT_RULES = EQUIPMENT_Y.flatMap { y -> EQUIPMENT_COLS.map { (a,b) -> Rule(a,b,y) } }
        val EQUIPMENT_ITEMS = listOf(
            "Mochila de expedición", "Libro de conjuros", "3 x Pociones",
            "Componentes arcanos", "5 x Raciones", "Odre",
            "Capa gris", "Mapa del Valle", "8 x Tiza",
            "6 x Pergaminos", "2 x Tinta azul", "Lupa de latón",
            "Martillo pequeño", "12 x Clavos", "Linterna cubierta",
            "4 x Aceite", "Manta", "Cuerda de seda",
            "Espejo de acero", "4 x Campanillas", "6 x Viales",
        )
        val CURRENCY_VALUES = listOf("4", "137", "48", "19", "2")
        val VALUABLE_RULE_Y = listOf(307f, 327f, 347f, 366.5f)
        val VALUABLES = listOf(
            "Broche élfico antiguo" to "75",
            "Gema lunar tallada" to "120",
            "Láminas de plata" to "45",
            "Mosaico con sello azul" to "25",
        )
        val SPECIAL_RULE_Y = listOf(522.5f, 542.5f, 562f, 582f, 602f, 622f, 641.5f, 661.5f, 681.5f, 701f, 721f, 741f)
        val SPECIAL_CHECK_TOP = listOf(
            508.770f, 528.612f, 548.455f, 568.297f, 588.140f, 607.982f,
            627.825f, 647.667f, 667.510f, 687.352f, 707.195f, 727.037f,
        )
        val SPECIAL_EQUIPMENT = listOf(
            SpecialEquipmentEntry("Diadema del Archivo", "Marca ceremonial de acceso.", true),
            SpecialEquipmentEntry("Monóculo rúnico", "Ayuda a inspeccionar glifos finos.", true),
            SpecialEquipmentEntry("Amuleto de Liria", "Recuerdo de la Academia.", true),
            SpecialEquipmentEntry("Anillo Academia", "Sello de acceso a archivos restringidos.", true),
            SpecialEquipmentEntry("Bastón de fresno", "Foco arcano y arma improvisada.", true),
            SpecialEquipmentEntry("Brazal de cobre", "Conserva una carga menor.", true),
            SpecialEquipmentEntry("Brazal de plata", "Pareja del brazal de cobre.", true),
            SpecialEquipmentEntry("Chaleco de placas", "Protección ligera bajo la ropa.", true),
            SpecialEquipmentEntry("Grebas caminante", "Refuerzo para largas marchas.", true),
            SpecialEquipmentEntry("Botas de senda", "Suela reforzada para terreno irregular.", true),
            SpecialEquipmentEntry("Daga de plata", "Hoja ligera tratada con plata.", true),
            SpecialEquipmentEntry("Guante de escriba", "Protege y estabiliza la mano.", true),
        )

        val SPELL_LEVELS = listOf(
            SpellLevelGeometry(2, 3, TopRect(56.98f, 538.05f, 27.49f, 27.49f), 28.205f, 573.687f, 39.543f, 20f, 9,
                listOf(SpellRow("Imagen múltiple", true), SpellRow("Paso brumoso", true), SpellRow("Invisibilidad", false), SpellRow("Levitar", true))),
            SpellLevelGeometry(3, 3, TopRect(243.98f, 81.8f, 27.49f, 27.49f), 215.455f, 117.187f, 226.543f, 20f, 10,
                listOf(SpellRow("Contrahechizo", true), SpellRow("Bola de fuego", true), SpellRow("Volar", false), SpellRow("Patrón hipnótico", true))),
            SpellLevelGeometry(4, 3, TopRect(243.98f, 321.05f, 27.49f, 27.49f), 215.455f, 356.187f, 226.543f, 20f, 10,
                listOf(SpellRow("Puerta dimensional", true), SpellRow("Invisibilidad superior", false), SpellRow("Ojo arcano", true), SpellRow("Polimorfar", false))),
            SpellLevelGeometry(5, 2, TopRect(243.98f, 557.05f, 27.49f, 27.49f), 215.455f, 592.687f, 226.543f, 20f, 8,
                listOf(SpellRow("Muro de fuerza", true), SpellRow("Telequinesis", false), SpellRow("Cono de frío", true))),
            SpellLevelGeometry(6, 2, TopRect(437.0f, 81.8f, 27.49f, 27.49f), 408.205f, 117.187f, 419.543f, 20f, 8,
                listOf(SpellRow("Desintegrar", true), SpellRow("Globo de invulnerabilidad", false), SpellRow("Visión verdadera", true))),
            SpellLevelGeometry(7, 1, TopRect(437.0f, 280.3f, 27.49f, 27.49f), 408.205f, 315.687f, 419.543f, 20f, 6,
                listOf(SpellRow("Teletransportar", false), SpellRow("Jaula de fuerza", true), SpellRow("Simulacro", false))),
            SpellLevelGeometry(8, 1, TopRect(437.0f, 458.8f, 27.49f, 27.49f), 408.205f, 494.187f, 419.543f, 20f, 6,
                listOf(SpellRow("Laberinto", true), SpellRow("Mente en blanco", false), SpellRow("Semiplano", true))),
            SpellLevelGeometry(9, 1, TopRect(437.0f, 617.55f, 27.49f, 27.49f), 408.205f, 653.187f, 419.543f, 20f, 5,
                listOf(SpellRow("Deseo", true), SpellRow("Detener el tiempo", false), SpellRow("Prisión", true))),
        )

        val NOTES_Y = listOf(109.5f, 129.5f, 149.5f, 169f, 189f, 209f, 229f, 248.5f, 268.5f, 288.5f, 308f, 328f, 348f, 367.5f, 387.5f, 407.5f, 427f)
        val NOTES_LEFT_RULES = NOTES_Y.map { Rule(25f, 267.5f, it) }
        val NOTES_RIGHT_RULES = NOTES_Y.map { Rule(311.669f, 583.795f, it) }
        const val NOTES_LEFT_TEXT =
            "Contactar a Maestra Elenya al regresar a Liria. No entregar el mapa original a terceros. Preparar tinta resistente al agua antes de entrar en las ruinas. Revisar el corredor norte antes de acampar. La puerta con sello azul responde al mismo patrón visto en la torre. Mantener una copia separada del alfabeto parcial. Registrar la posición de cada piedra marcada y comprobar si las distancias forman una secuencia."
        const val NOTES_RIGHT_TEXT =
            "Pista: el sello azul aparece también en las monedas halladas en la torre. Pendiente: comparar el alfabeto de la puerta norte con las notas del profesor Vael. Ruta: entrada oeste, cámara de columnas, escalera rota, galería azul y archivo inferior. Materiales: tinta, tiza, tres viales, espejo, cuerda, clavos y una linterna adicional."

        val CANTRIP_RULE_Y = listOf(129.8f, 148.7f, 167.5f, 186.4f)

        val LEVEL1_SQUARES = listOf(
            TopRect(28.205f, 327.187f, 9.669f, 12.287f),
            TopRect(28.205f, 347.030f, 9.669f, 12.287f),
            TopRect(28.205f, 366.872f, 9.669f, 12.287f),
            TopRect(28.205f, 386.715f, 9.669f, 12.287f),
            TopRect(28.205f, 406.557f, 9.669f, 12.287f),
        )

        fun resource(path: String): InputStream = requireNotNull(
            DesktopPcSheetCustomV2AbilityHybridRun1Test::class.java.classLoader.getResourceAsStream(path),
        ) { "Missing test resource: $path" }

        fun font(doc: PDDocument, path: String): PDFont = resource(path).use { PDType0Font.load(doc, it, false) }
    }
}
