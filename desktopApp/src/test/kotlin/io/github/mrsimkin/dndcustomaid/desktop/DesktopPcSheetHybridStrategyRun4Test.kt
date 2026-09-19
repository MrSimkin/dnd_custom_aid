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

/** Strategy 1 / Hybrid / Run 4. Generalization QA only; not product rendering. */
class DesktopPcSheetHybridStrategyRun4Test {
    @Test
    fun generalizesNativeGeometryAcrossRepeatedStructures() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val bytes = resource(TEMPLATE).use { it.readBytes() }
        val composite = File(proofDir, "hybrid-strategy1-run4-composite.pdf")

        Loader.loadPDF(bytes).use { doc ->
            retainPages(doc)
            val fonts = Fonts(doc)
            val layers = LayerUtility(doc)
            listOf(Kind.MAIN, Kind.NARRATIVE, Kind.SPELLS).forEachIndexed { i, kind ->
                val page = doc.getPage(i)
                val form = overlay(doc, page, fonts, kind)
                layers.appendFormAsLayer(page, form, AffineTransform(), "Hybrid Run 4 - ${kind.name}")
            }
            doc.save(composite)
        }

        Loader.loadPDF(composite).use { doc ->
            assertEquals(3, doc.numberOfPages)
            assertNotNull(doc.documentCatalog.ocProperties)
            assertHybridFontsEmbedded(doc)
            val renderer = PDFRenderer(doc)
            repeat(3) { index ->
                val image = renderer.renderImageWithDPI(index, 300f, ImageType.RGB)
                val png = File(proofDir, "hybrid-strategy1-run4-composite-page-${index + 1}.png")
                assertTrue(ImageIO.write(image, "png", png))
            }
        }

        val overlayOnly = File(proofDir, "hybrid-strategy1-run4-overlay-only.pdf")
        PDDocument().use { doc ->
            val fonts = Fonts(doc)
            Kind.entries.forEach { kind ->
                val page = PDPage(PDRectangle(W, H))
                doc.addPage(page)
                val form = overlay(doc, page, fonts, kind)
                PDPageContentStream(doc, page).use { it.drawForm(form) }
            }
            doc.save(overlayOnly)
        }
        assertTrue(composite.length() > 0)
        assertTrue(overlayOnly.length() > 0)
    }

    private fun overlay(doc: PDDocument, page: PDPage, fonts: Fonts, kind: Kind): PDFormXObject {
        val form = PDFormXObject(doc).apply {
            resources = PDResources()
            setBBox(PDRectangle(page.cropBox.width, page.cropBox.height))
        }
        PDFormContentStream(form).use { s ->
            s.setNonStrokingColor(Color.BLACK)
            when (kind) {
                Kind.MAIN -> drawMainGeneralization(s, fonts)
                Kind.NARRATIVE -> drawNarrativeGeneralization(s, fonts)
                Kind.SPELLS -> drawSpellGeneralization(s, fonts)
            }
        }
        return form
    }

    private fun drawMainGeneralization(s: PDFormContentStream, fonts: Fonts) {
        val attacks = listOf(
            AttackRow("Bastón de fresno", "5 ft", "+3", "1d6 contundente"),
            AttackRow("Rayo de fuego", "120 ft", "+7", "2d10 fuego"),
            AttackRow("Daga de plata", "20/60 ft", "+6", "1d4+3 perforante"),
            AttackRow("Arco corto", "80/320 ft", "+6", "1d6+3 perforante"),
            AttackRow("Orbe cromático", "90 ft", "+7", "3d8 variable"),
        )
        ATTACK_RULE_Y.zip(attacks).forEach { (ruleY, row) ->
            fitOnRule(s, fonts.regular, RuleAnchor(215.291f, 354.189f, ruleY), row.name, 9.25f, 8.5f, 2.2f)
            fitOnRule(s, fonts.regular, RuleAnchor(357.024f, 413.717f, ruleY), row.range, 9.0f, 8.5f, 1.5f)
            fitOnRule(s, fonts.regular, RuleAnchor(416.551f, 461.905f, ruleY), row.attack, 9.0f, 8.5f, 1.5f)
            fitOnRule(s, fonts.regular, RuleAnchor(460.968f, 583.803f, ruleY), row.damage, 9.0f, 8.5f, 2.0f)
        }

        // First-level spell slots: two spent of four. Targets are authoritative source C-glyph bboxes.
        FIRST_SLOT_OVALS.take(2).forEach { target ->
            glyphInRect(s, fonts.symbol, 0xE206, target, 0.9f, 1.0f)
        }
    }

    private fun drawNarrativeGeneralization(s: PDFormContentStream, fonts: Fonts) {
        val story =
            "Aster abandonó temporalmente los archivos de Liria después de hallar referencias a una cámara sellada " +
                "bajo el Valle del Viento. Viaja con un pequeño grupo de aventureros para reconstruir la ruta, " +
                "comparar los símbolos del mapa con las ruinas y comprobar si el códice asociado sobrevivió. " +
                "Anota cada desvío importante, registra las piedras marcadas y mantiene una copia separada del " +
                "alfabeto parcial para no depender de un único cuaderno durante la expedición."

        val anchor = RuleSeries(
            startX = 215.291f,
            endX = 583.795f,
            ruleY = NARRATIVE_RULE_Y,
            leftPadding = 2.0f,
        )
        val lines = wrapByWidth(fonts.regular, story, 9.25f, anchor.usableWidth)
        assertTrue(lines.size <= anchor.ruleY.size, "Run-4 narrative fixture must fit its authoritative rules.")
        anchor.ruleY.zip(lines).forEach { (ruleY, line) ->
            fitOnRule(
                s, fonts.regular,
                RuleAnchor(anchor.startX, anchor.endX, ruleY),
                line, 9.25f, 9.25f, anchor.leftPadding,
            )
        }
    }

    private fun drawSpellGeneralization(s: PDFormContentStream, fonts: Fonts) {
        centered(s, fonts.semibold, TopRect(56.98f, 291.55f, 27.49f, 27.49f), "4", 15f, -.35f)

        val spells = listOf(
            SpellRow("Escudo", true),
            SpellRow("Misil mágico", true),
            SpellRow("Detectar magia", false),
            SpellRow("Caída de pluma", true),
            SpellRow("Armadura de mago", false),
        )
        LEVEL1_SQUARES.zip(spells).forEachIndexed { index, (square, spell) ->
            if (spell.prepared) {
                glyphInRect(s, fonts.symbol, 0xE211, square, 0.6f, 0.6f)
            }
            val ruleY = LEVEL1_TEXT_RULE_Y[index]
            fitOnRule(
                s, fonts.regular,
                RuleAnchor(39.543f, 203.952f, ruleY),
                spell.name, 9.25f, 8.75f, 2.0f,
            )
        }

        // Repeated marker fitting: two of the four level-1 spent-slot ovals.
        FIRST_SLOT_OVALS.take(2).forEach { target ->
            glyphInRect(s, fonts.symbol, 0xE206, target, 0.9f, 1.0f)
        }
    }

    private fun fitOnRule(
        s: PDFormContentStream,
        font: PDFont,
        anchor: RuleAnchor,
        text: String,
        preferredSize: Float,
        minimumSize: Float,
        leftPadding: Float,
    ) {
        val available = anchor.endX - anchor.startX - leftPadding - 1.0f
        var size = preferredSize
        while (size > minimumSize && textWidth(font, text, size) > available) {
            size -= 0.25f
        }
        require(textWidth(font, text, size) <= available + 0.05f) {
            "Text does not fit readable rule width: '$text'"
        }
        onRule(s, font, anchor.startX + leftPadding, anchor.ruleY, text, size)
    }

    private fun wrapByWidth(font: PDFont, text: String, size: Float, maxWidth: Float): List<String> {
        val out = mutableListOf<String>()
        var current = ""
        text.trim().split(Regex("\\s+")).forEach { word ->
            val candidate = if (current.isBlank()) word else "$current $word"
            if (textWidth(font, candidate, size) <= maxWidth) {
                current = candidate
            } else {
                if (current.isNotBlank()) out += current
                current = word
            }
        }
        if (current.isNotBlank()) out += current
        return out
    }

    private fun textWidth(font: PDFont, text: String, size: Float): Float =
        font.getStringWidth(text) / 1000f * size

    private fun onRule(s: PDFormContentStream, font: PDFont, x: Float, ruleTopY: Float, text: String, size: Float) {
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(x, H - (ruleTopY - 3f))
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

        require(normalizedWidth > 0f)
        require(normalizedHeight > 0f)

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

    private fun assertHybridFontsEmbedded(doc: PDDocument) {
        val fonts = buildList {
            repeat(doc.numberOfPages) { pageIndex -> addAll(fontsIn(doc.getPage(pageIndex).resources)) }
        }.distinctBy { it.cosObject }
        val hybrid = fonts.filter { font ->
            font.name.contains("FiraSans", ignoreCase = true) ||
                font.name.contains("ParaHojadePJSymbols", ignoreCase = true) ||
                font.name.contains("Para Hoja de PJ", ignoreCase = true)
        }
        assertTrue(
            hybrid.size >= 3,
            "Expected Fira regular/semibold and Para Hoja de PJ v8; found: " + fonts.joinToString { it.name },
        )
        assertTrue(
            hybrid.all { it.isEmbedded },
            "Hybrid overlay fonts must be embedded; non-embedded: " +
                hybrid.filterNot { it.isEmbedded }.joinToString { it.name },
        )
    }

    private fun fontsIn(resources: PDResources?): List<PDFont> {
        if (resources == null) return emptyList()
        val direct = resources.fontNames.mapNotNull { name -> resources.getFont(name) }
        val nested = resources.xObjectNames.flatMap { name ->
            when (val xObject: PDXObject? = resources.getXObject(name)) {
                is PDFormXObject -> fontsIn(xObject.resources)
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
        val symbol = font(doc, "fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf")
    }

    private data class RuleAnchor(val startX: Float, val endX: Float, val ruleY: Float)
    private data class RuleSeries(
        val startX: Float,
        val endX: Float,
        val ruleY: List<Float>,
        val leftPadding: Float,
    ) {
        val usableWidth: Float get() = endX - startX - leftPadding - 1.0f
    }
    private data class TopRect(val x: Float, val top: Float, val width: Float, val height: Float)
    private data class AttackRow(val name: String, val range: String, val attack: String, val damage: String)
    private data class SpellRow(val name: String, val prepared: Boolean)
    private enum class Kind { MAIN, NARRATIVE, SPELLS }

    private companion object {
        const val W = 612f
        const val H = 792f
        const val TEMPLATE = "character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf"

        val ATTACK_RULE_Y = listOf(444.689f, 464.531f, 484.374f, 504.217f, 524.059f)
        val NARRATIVE_RULE_Y = listOf(
            387.996f, 407.839f, 427.681f, 447.524f, 467.366f,
            487.209f, 507.051f, 526.894f, 546.736f,
        )
        val FIRST_SLOT_OVALS = listOf(
            TopRect(80.646f, 442.688f, 14.064f, 17.872f),
            TopRect(95.318f, 442.688f, 14.064f, 17.872f),
            TopRect(109.990f, 442.688f, 14.064f, 17.872f),
            TopRect(124.662f, 442.688f, 14.064f, 17.872f),
        )
        val LEVEL1_SQUARES = listOf(
            TopRect(28.205f, 327.187f, 9.669f, 12.287f),
            TopRect(28.205f, 347.030f, 9.669f, 12.287f),
            TopRect(28.205f, 366.872f, 9.669f, 12.287f),
            TopRect(28.205f, 386.715f, 9.669f, 12.287f),
            TopRect(28.205f, 406.557f, 9.669f, 12.287f),
        )
        val LEVEL1_TEXT_RULE_Y = listOf(347.030f, 366.872f, 386.715f, 406.557f, 426.400f)

        fun resource(path: String): InputStream = requireNotNull(
            DesktopPcSheetHybridStrategyRun4Test::class.java.classLoader.getResourceAsStream(path),
        ) { "Missing test resource: $path" }

        fun font(doc: PDDocument, path: String): PDFont = resource(path).use { PDType0Font.load(doc, it, false) }
    }
}
