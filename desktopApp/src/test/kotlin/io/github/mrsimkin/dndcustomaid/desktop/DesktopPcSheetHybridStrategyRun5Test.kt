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
 * Strategy 1 / Hybrid / Run 5.
 *
 * Explicitly tests InDesign-like section isolation: each logical page section is rendered as an
 * independent Form XObject / Optional Content Group with its own geometry and typography.
 * This remains QA-only code, not product rendering.
 */
class DesktopPcSheetHybridStrategyRun5Test {
    @Test
    fun isolatesPageSectionsAndFixesSpellSemantics() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val templateBytes = resource(TEMPLATE).use { it.readBytes() }
        val composite = File(proofDir, "hybrid-strategy1-run5-composite.pdf")

        Loader.loadPDF(templateBytes).use { doc ->
            retainPages(doc)
            val fonts = Fonts(doc)
            val layers = LayerUtility(doc)

            val main = doc.getPage(0)
            appendSection(doc, layers, main, fonts, "Run5 MAIN - Identification") { s ->
                drawIdentification(s, fonts)
            }
            appendSection(doc, layers, main, fonts, "Run5 MAIN - Defense") { s ->
                drawDefense(s, fonts)
            }
            appendSection(doc, layers, main, fonts, "Run5 MAIN - Abilities") { s ->
                drawAbilities(s, fonts)
            }
            appendSection(doc, layers, main, fonts, "Run5 MAIN - Skills") { s ->
                drawSkills(s, fonts)
            }
            appendSection(doc, layers, main, fonts, "Run5 MAIN - Attacks regression") { s ->
                drawAttacks(s, fonts)
            }
            appendSection(doc, layers, main, fonts, "Run5 MAIN - Traits row 1") { s ->
                drawTraits(s, fonts)
            }

            val narrative = doc.getPage(1)
            appendSection(doc, layers, narrative, fonts, "Run5 PAGE2 - Background fields") { s ->
                drawBackgroundFields(s, fonts)
            }

            val spells = doc.getPage(2)
            appendSection(doc, layers, spells, fonts, "Run5 SPELLS - Cantrips") { s ->
                drawCantrips(s, fonts)
            }
            appendSection(doc, layers, spells, fonts, "Run5 SPELLS - Level 1") { s ->
                drawLevelOneSpells(s, fonts)
            }

            doc.save(composite)
        }

        Loader.loadPDF(composite).use { doc ->
            assertEquals(3, doc.numberOfPages)
            val oc = assertNotNull(doc.documentCatalog.ocProperties)
            assertTrue(oc.getGroupNames().count() >= 9, "Run 5 must preserve section-level OCG isolation.")
            assertHybridFontsEmbedded(doc)

            val renderer = PDFRenderer(doc)
            repeat(3) { index ->
                val image = renderer.renderImageWithDPI(index, 300f, ImageType.RGB)
                val png = File(proofDir, "hybrid-strategy1-run5-composite-page-${index + 1}.png")
                assertTrue(ImageIO.write(image, "png", png))
            }
        }

        val overlayOnly = File(proofDir, "hybrid-strategy1-run5-overlay-only.pdf")
        PDDocument().use { doc ->
            val fonts = Fonts(doc)
            repeat(3) { doc.addPage(PDPage(PDRectangle(W, H))) }
            val layers = LayerUtility(doc)

            appendSection(doc, layers, doc.getPage(0), fonts, "Run5 MAIN - Identification") { drawIdentification(it, fonts) }
            appendSection(doc, layers, doc.getPage(0), fonts, "Run5 MAIN - Defense") { drawDefense(it, fonts) }
            appendSection(doc, layers, doc.getPage(0), fonts, "Run5 MAIN - Abilities") { drawAbilities(it, fonts) }
            appendSection(doc, layers, doc.getPage(0), fonts, "Run5 MAIN - Skills") { drawSkills(it, fonts) }
            appendSection(doc, layers, doc.getPage(0), fonts, "Run5 MAIN - Attacks regression") { drawAttacks(it, fonts) }
            appendSection(doc, layers, doc.getPage(0), fonts, "Run5 MAIN - Traits row 1") { drawTraits(it, fonts) }
            appendSection(doc, layers, doc.getPage(1), fonts, "Run5 PAGE2 - Background fields") { drawBackgroundFields(it, fonts) }
            appendSection(doc, layers, doc.getPage(2), fonts, "Run5 SPELLS - Cantrips") { drawCantrips(it, fonts) }
            appendSection(doc, layers, doc.getPage(2), fonts, "Run5 SPELLS - Level 1") { drawLevelOneSpells(it, fonts) }

            doc.save(overlayOnly)
        }

        assertTrue(composite.length() > 0)
        assertTrue(overlayOnly.length() > 0)
    }

    private fun appendSection(
        doc: PDDocument,
        layers: LayerUtility,
        page: PDPage,
        fonts: Fonts,
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
        // Independent handwritten identification layer.
        textAboveRule(s, fonts.handwritten, Rule(310f, 440f, 53f), "Mago 5 / Pícaro 2", 10.5f, 9f, 2.2f)
        textAboveRule(s, fonts.handwritten, Rule(310f, 440f, 73f), "Elfo Alto", 10.5f, 9f, 2.2f)
        textAboveRule(s, fonts.handwritten, Rule(310f, 440f, 93f), "Neutral Bueno", 10.5f, 9f, 2.2f)
        textAboveRule(s, fonts.handwritten, Rule(310f, 440f, 112.5f), "24.000 PX", 10.5f, 9f, 2.2f)

        centered(s, fonts.handwritten, TopRect(455f, 190f, 132f, 28f), "Aster Vale", 14f, 0f)
    }

    private fun drawDefense(s: PDFormContentStream, fonts: Fonts) {
        centered(s, fonts.semibold, TopRect(30f, 135f, 55f, 65f), "16", 22f, -1f)
        centeredAboveRule(s, fonts.semibold, Rule(98.5f, 158f, 138.5f), "+3", 10.5f, 2.5f)
        centeredAboveRule(s, fonts.semibold, Rule(98.5f, 158f, 168.2f), "12", 10.5f, 2.5f)
        centeredAboveRule(s, fonts.semibold, Rule(98.5f, 158f, 198.0f), "+2", 10.5f, 2.5f)
        centeredAboveRule(s, fonts.semibold, Rule(98.5f, 158f, 226.6f), "-1", 10.5f, 2.5f)
    }

    private fun drawAbilities(s: PDFormContentStream, fonts: Fonts) {
        ABILITIES.forEach { a ->
            centered(s, fonts.semibold, TopRect(a.scoreX - 28f, 265.5f, 56f, 31f), a.score, 18f, -0.5f)
            centered(s, fonts.semibold, TopRect(a.modX - 16f, 285.0f, 32f, 20f), a.mod, 11.5f, -0.2f)
        }
    }

    private fun drawSkills(s: PDFormContentStream, fonts: Fonts) {
        SKILL_COLUMNS.forEach { column ->
            column.rows.forEachIndexed { index, row ->
                val centerY = column.firstCenterY + index * 15f
                if (row.mark != null) {
                    val rect = TopRect(column.checkCenterX - 4.2f, centerY - 4.2f, 8.4f, 8.4f)
                    glyphInRect(
                        s,
                        fonts.symbol,
                        if (row.mark == Mark.DOUBLE) 0xE212 else 0xE211,
                        rect,
                        0.55f,
                        0.55f,
                        opticalX = 0.35f,
                    )
                }
                centered(
                    s,
                    fonts.semibold,
                    TopRect(column.valueCenterX - 12f, centerY - 6f, 24f, 12f),
                    row.value,
                    8.8f,
                    -0.3f,
                )
            }
        }
    }

    private fun drawAttacks(s: PDFormContentStream, fonts: Fonts) {
        ATTACK_RULE_Y.zip(ATTACKS).forEach { (ruleY, row) ->
            textAboveRule(s, fonts.regular, Rule(215.291f, 354.189f, ruleY), row.name, 9.25f, 8.5f, 2.5f, 2.2f)
            textAboveRule(s, fonts.regular, Rule(357.024f, 413.717f, ruleY), row.range, 9f, 8.5f, 2.5f, 1.5f)
            textAboveRule(s, fonts.regular, Rule(416.551f, 461.905f, ruleY), row.attack, 9f, 8.5f, 2.5f, 1.5f)
            textAboveRule(s, fonts.regular, Rule(460.968f, 583.803f, ruleY), row.damage, 9f, 8.5f, 2.5f, 2.0f)
        }
        // Owner rule: "ESPACIOS GASTADOS" remains intentionally empty.
    }

    private fun drawTraits(s: PDFormContentStream, fonts: Fonts) {
        val y = 661.5f
        listOf(
            Rule(26f, 188.5f, y) to "Visión en la oscuridad",
            Rule(212.5f, 375f, y) to "Recuperación Arcana",
            Rule(399f, 561.5f, y) to "Ataque furtivo 1d6",
        ).forEach { (rule, text) ->
            textAboveRule(s, fonts.regular, rule, text, 9.25f, 8.5f, 2.5f, 2.0f)
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

    private fun drawCantrips(s: PDFormContentStream, fonts: Fonts) {
        // D&D semantic rule: cantrips are always prepared, therefore no preparation check is rendered.
        val names = listOf("Luz", "Mano de mago", "Rayo de fuego", "Prestidigitación")
        CANTRIP_RULE_Y.zip(names).forEach { (ruleY, name) ->
            textAboveRule(
                s,
                fonts.regular,
                Rule(39.5f, 203.95f, ruleY),
                name,
                9.25f,
                8.75f,
                3.2f,
                2.0f,
            )
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
                glyphInRect(
                    s,
                    fonts.symbol,
                    0xE211,
                    square,
                    0.6f,
                    0.6f,
                    opticalX = 0.65f,
                )
            }
            val actualRuleY = square.top + square.height
            textAboveRule(
                s,
                fonts.regular,
                Rule(39.543f, 203.952f, actualRuleY),
                spell.name,
                9.25f,
                8.75f,
                3.4f,
                2.0f,
            )
        }

        // Intentionally NOTHING is drawn in "ESPACIOS GASTADOS".
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
        val ruleBottomY = H - rule.topY
        val baseline = ruleBottomY + clearance - descent

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
        assertTrue(hybrid.size >= 4, "Run 5 must embed Fira regular/semibold, Kalam and v8 symbols.")
        assertTrue(
            hybrid.all { it.isEmbedded },
            "Hybrid fonts must be embedded; non-embedded: " + hybrid.filterNot { it.isEmbedded }.joinToString { it.name },
        )
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
    private data class SkillColumn(
        val checkCenterX: Float,
        val valueCenterX: Float,
        val firstCenterY: Float,
        val rows: List<SkillRow>,
    )
    private data class SkillRow(val value: String, val mark: Mark?)
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

        val SKILL_COLUMNS = listOf(
            SkillColumn(27.5f, 87.5f, 330.5f, listOf(SkillRow("+0", null))),
            SkillColumn(
                122.5f, 182.5f, 330.5f,
                listOf(SkillRow("+6", Mark.SINGLE), SkillRow("+9", Mark.DOUBLE), SkillRow("+6", Mark.SINGLE)),
            ),
            SkillColumn(219f, 277.5f, 330.5f, emptyList()),
            SkillColumn(
                314f, 372.5f, 330.5f,
                listOf(
                    SkillRow("+7", Mark.SINGLE),
                    SkillRow("+7", Mark.SINGLE),
                    SkillRow("+8", Mark.SINGLE),
                    SkillRow("+4", null),
                    SkillRow("+4", null),
                ),
            ),
            SkillColumn(
                409f, 467.5f, 330.5f,
                listOf(
                    SkillRow("+1", null),
                    SkillRow("+2", null),
                    SkillRow("+1", null),
                    SkillRow("+1", null),
                    SkillRow("+1", null),
                ),
            ),
            SkillColumn(
                505f, 585f, 330.5f,
                listOf(SkillRow("-1", null), SkillRow("-1", null), SkillRow("-1", null), SkillRow("-1", null)),
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

        val CANTRIP_RULE_Y = listOf(129.8f, 148.7f, 167.5f, 186.4f)

        val LEVEL1_SQUARES = listOf(
            TopRect(28.205f, 327.187f, 9.669f, 12.287f),
            TopRect(28.205f, 347.030f, 9.669f, 12.287f),
            TopRect(28.205f, 366.872f, 9.669f, 12.287f),
            TopRect(28.205f, 386.715f, 9.669f, 12.287f),
            TopRect(28.205f, 406.557f, 9.669f, 12.287f),
        )

        fun resource(path: String): InputStream = requireNotNull(
            DesktopPcSheetHybridStrategyRun5Test::class.java.classLoader.getResourceAsStream(path),
        ) { "Missing test resource: $path" }

        fun font(doc: PDDocument, path: String): PDFont = resource(path).use { PDType0Font.load(doc, it, false) }
    }
}
