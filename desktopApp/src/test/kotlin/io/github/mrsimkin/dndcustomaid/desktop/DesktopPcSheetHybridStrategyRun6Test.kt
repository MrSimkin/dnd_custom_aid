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
 * Strategy 1 / Hybrid / Run 6.
 *
 * Extends the Run-5 section-isolated architecture. Known local geometry defects are repaired from
 * measured source-PDF geometry while new independently layered fields are added.
 * QA-only code; not product rendering.
 */
class DesktopPcSheetHybridStrategyRun6Test {
    @Test
    fun calibratesSkillColumnsAndExpandsSectionCoverage() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val templateBytes = resource(TEMPLATE).use { it.readBytes() }
        val composite = File(proofDir, "hybrid-strategy1-run6-composite.pdf")

        Loader.loadPDF(templateBytes).use { doc ->
            retainPages(doc)
            val fonts = Fonts(doc)
            val layers = LayerUtility(doc)
            val main = doc.getPage(0)
            val narrative = doc.getPage(1)
            val spells = doc.getPage(2)

            appendSection(doc, layers, main, "Run6 MAIN - Identification") { drawIdentification(it, fonts) }
            appendSection(doc, layers, main, "Run6 MAIN - Defense") { drawDefense(it, fonts) }
            appendSection(doc, layers, main, "Run6 MAIN - Core Stats") { drawCoreStats(it, fonts) }
            appendSection(doc, layers, main, "Run6 MAIN - Abilities") { drawAbilities(it, fonts) }

            SKILL_SECTIONS.forEach { section ->
                appendSection(doc, layers, main, "Run6 MAIN - Skills ${section.name}") {
                    drawSkillSection(it, fonts, section)
                }
            }

            appendSection(doc, layers, main, "Run6 MAIN - Spellcasting Summary") { drawSpellcastingSummary(it, fonts) }
            appendSection(doc, layers, main, "Run6 MAIN - Attacks") { drawAttacks(it, fonts) }
            appendSection(doc, layers, main, "Run6 MAIN - Traits rows 1-2") { drawTraits(it, fonts) }

            appendSection(doc, layers, narrative, "Run6 PAGE2 - Background fields") { drawBackgroundFields(it, fonts) }
            appendSection(doc, layers, narrative, "Run6 PAGE2 - Story") { drawStory(it, fonts) }

            appendSection(doc, layers, spells, "Run6 SPELLS - Cantrips") { drawCantrips(it, fonts) }
            appendSection(doc, layers, spells, "Run6 SPELLS - Level 1") { drawLevelOneSpells(it, fonts) }

            doc.save(composite)
        }

        Loader.loadPDF(composite).use { doc ->
            assertEquals(3, doc.numberOfPages)
            val oc = assertNotNull(doc.documentCatalog.ocProperties)
            assertTrue(oc.getGroupNames().count() >= 16, "Run 6 must preserve fine-grained section isolation.")
            assertHybridFontsEmbedded(doc)
            val renderer = PDFRenderer(doc)
            repeat(3) { index ->
                val image = renderer.renderImageWithDPI(index, 300f, ImageType.RGB)
                val png = File(proofDir, "hybrid-strategy1-run6-composite-page-${index + 1}.png")
                assertTrue(ImageIO.write(image, "png", png))
            }
        }

        val overlayOnly = File(proofDir, "hybrid-strategy1-run6-overlay-only.pdf")
        PDDocument().use { doc ->
            repeat(3) { doc.addPage(PDPage(PDRectangle(W, H))) }
            val fonts = Fonts(doc)
            val layers = LayerUtility(doc)
            val main = doc.getPage(0)
            val narrative = doc.getPage(1)
            val spells = doc.getPage(2)

            appendSection(doc, layers, main, "Run6 MAIN - Identification") { drawIdentification(it, fonts) }
            appendSection(doc, layers, main, "Run6 MAIN - Defense") { drawDefense(it, fonts) }
            appendSection(doc, layers, main, "Run6 MAIN - Core Stats") { drawCoreStats(it, fonts) }
            appendSection(doc, layers, main, "Run6 MAIN - Abilities") { drawAbilities(it, fonts) }
            SKILL_SECTIONS.forEach { section ->
                appendSection(doc, layers, main, "Run6 MAIN - Skills ${section.name}") {
                    drawSkillSection(it, fonts, section)
                }
            }
            appendSection(doc, layers, main, "Run6 MAIN - Spellcasting Summary") { drawSpellcastingSummary(it, fonts) }
            appendSection(doc, layers, main, "Run6 MAIN - Attacks") { drawAttacks(it, fonts) }
            appendSection(doc, layers, main, "Run6 MAIN - Traits rows 1-2") { drawTraits(it, fonts) }
            appendSection(doc, layers, narrative, "Run6 PAGE2 - Background fields") { drawBackgroundFields(it, fonts) }
            appendSection(doc, layers, narrative, "Run6 PAGE2 - Story") { drawStory(it, fonts) }
            appendSection(doc, layers, spells, "Run6 SPELLS - Cantrips") { drawCantrips(it, fonts) }
            appendSection(doc, layers, spells, "Run6 SPELLS - Level 1") { drawLevelOneSpells(it, fonts) }
            doc.save(overlayOnly)
        }

        assertTrue(composite.length() > 0)
        assertTrue(overlayOnly.length() > 0)
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
        listOf(
            Rule(25f, 181f, 109.5f) to "Sabio de la Academia de Liria",
            Rule(25f, 181f, 387.5f) to "Conocimiento y responsabilidad",
            Rule(25f, 181f, 526.5f) to "Recuperar el códice perdido",
            Rule(25f, 181f, 665.5f) to "Subestima riesgos por curiosidad",
        ).forEach { (rule, text) ->
            textAboveRule(s, fonts.regular, rule, text, 9.25f, 8.5f, 2.6f, 2.0f)
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

    private fun retainPages(doc: PDDocument) {
        val keep = setOf(0, 2, 3)
        for (i in doc.numberOfPages - 1 downTo 0) if (i !in keep) doc.removePage(i)
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

        val STORY_RULE_Y = listOf(387.996f, 407.839f, 427.681f, 447.524f)

        val CANTRIP_RULE_Y = listOf(129.8f, 148.7f, 167.5f, 186.4f)

        val LEVEL1_SQUARES = listOf(
            TopRect(28.205f, 327.187f, 9.669f, 12.287f),
            TopRect(28.205f, 347.030f, 9.669f, 12.287f),
            TopRect(28.205f, 366.872f, 9.669f, 12.287f),
            TopRect(28.205f, 386.715f, 9.669f, 12.287f),
            TopRect(28.205f, 406.557f, 9.669f, 12.287f),
        )

        fun resource(path: String): InputStream = requireNotNull(
            DesktopPcSheetHybridStrategyRun6Test::class.java.classLoader.getResourceAsStream(path),
        ) { "Missing test resource: $path" }

        fun font(doc: PDDocument, path: String): PDFont = resource(path).use { PDType0Font.load(doc, it, false) }
    }
}
