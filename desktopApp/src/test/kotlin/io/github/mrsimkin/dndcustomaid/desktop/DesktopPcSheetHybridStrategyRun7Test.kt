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
 * Strategy 1 / Hybrid / Run 7.
 *
 * Extends the Run-5 section-isolated architecture. Known local geometry defects are repaired from
 * measured source-PDF geometry while new independently layered fields are added.
 * QA-only code; not product rendering.
 */
class DesktopPcSheetHybridStrategyRun7Test {
    @Test
    fun rendersCompleteFivePageAllHybridCustomV1Draft() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val templateBytes = resource(TEMPLATE).use { it.readBytes() }
        val composite = File(proofDir, "hybrid-strategy1-run7-composite.pdf")

        Loader.loadPDF(templateBytes).use { doc ->
            val fonts = Fonts(doc)
            val layers = LayerUtility(doc)
            renderAllHybrid(doc, layers, fonts)
            doc.save(composite)
        }

        Loader.loadPDF(composite).use { doc ->
            assertEquals(5, doc.numberOfPages)
            val oc = assertNotNull(doc.documentCatalog.ocProperties)
            assertTrue(oc.getGroupNames().count() >= 34, "Run 7 must keep the complete draft section-isolated.")
            assertHybridFontsEmbedded(doc)
            val renderer = PDFRenderer(doc)
            repeat(5) { index ->
                val image = renderer.renderImageWithDPI(index, 300f, ImageType.RGB)
                val png = File(proofDir, "hybrid-strategy1-run7-composite-page-${index + 1}.png")
                assertTrue(ImageIO.write(image, "png", png))
            }
        }

        val overlayOnly = File(proofDir, "hybrid-strategy1-run7-overlay-only.pdf")
        PDDocument().use { doc ->
            repeat(5) { doc.addPage(PDPage(PDRectangle(W, H))) }
            val fonts = Fonts(doc)
            val layers = LayerUtility(doc)
            renderAllHybrid(doc, layers, fonts)
            doc.save(overlayOnly)
        }

        assertTrue(composite.length() > 0)
        assertTrue(overlayOnly.length() > 0)
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
            textAboveRule(s, fonts.regular, Rule(407.5f, 522f, y), entry.first, 9.0f, 8.5f, 2.4f, 1.5f)
            centeredAboveRule(s, fonts.semibold, Rule(535f, 589.5f, y), entry.second, 9.5f, 2.4f)
        }
    }

    private fun drawSpecialEquipment(s: PDFormContentStream, fonts: Fonts) {
        SPECIAL_RULE_Y.zip(SPECIAL_EQUIPMENT).forEach { (y, entry) ->
            if (entry.checked) {
                glyphInRect(s, fonts.symbol, 0xE211, TopRect(112.4f, y - 12.287f, 9.669f, 12.287f), 0.6f, 0.6f, opticalX = 0.65f)
            }
            textAboveRule(s, fonts.regular, Rule(127.5f, 210f, y), entry.name, 9.0f, 8.5f, 2.4f, 1.5f)
            textAboveRule(s, fonts.regular, Rule(215f, 517.5f, y), entry.description, 9.0f, 8.5f, 2.4f, 1.5f)
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

    private companion object {
        const val W = 612f
        const val H = 792f
        const val TEMPLATE = "character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf"

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
            .map { Rule(192.5f, 517.5f, it) }

        val OTHER_TRAIT_Y = listOf(109.5f, 129.5f, 149.5f, 169f, 189f, 209f, 229f, 248.5f, 268.5f, 288.5f, 308f, 328f)
        val OTHER_TRAIT_RULES = OTHER_TRAIT_Y.flatMap { y ->
            listOf(Rule(192.5f, 352.5f, y), Rule(365f, 517.5f, y))
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
        val EQUIPMENT_COLS = listOf(27.5f to 137.5f, 152.5f to 262.5f, 277.5f to 390f)
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
        val NOTES_RIGHT_RULES = NOTES_Y.map { Rule(277.5f, 520f, it) }
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
            DesktopPcSheetHybridStrategyRun7Test::class.java.classLoader.getResourceAsStream(path),
        ) { "Missing test resource: $path" }

        fun font(doc: PDDocument, path: String): PDFont = resource(path).use { PDType0Font.load(doc, it, false) }
    }
}
