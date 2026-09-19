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

/** Strategy 1 / Hybrid / Run 2. Experimental QA only; not product rendering. */
class DesktopPcSheetHybridStrategyRun2Test {
    @Test
    fun generatesNativeGeometryFormLayerProof() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val bytes = resource(TEMPLATE).use { it.readBytes() }
        val composite = File(proofDir, "hybrid-strategy1-run2-composite.pdf")

        Loader.loadPDF(bytes).use { doc ->
            retainPages(doc)
            val fonts = Fonts(doc)
            val layers = LayerUtility(doc)
            listOf(Kind.MAIN, Kind.NARRATIVE, Kind.SPELLS).forEachIndexed { i, kind ->
                val page = doc.getPage(i)
                val form = overlay(doc, page, fonts, kind)
                layers.appendFormAsLayer(page, form, AffineTransform(), "Hybrid Run 2 - ${kind.name}")
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
                val png = File(proofDir, "hybrid-strategy1-run2-composite-page-${index + 1}.png")
                assertTrue(ImageIO.write(image, "png", png))
            }
        }

        val overlayOnly = File(proofDir, "hybrid-strategy1-run2-overlay-only.pdf")
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
                Kind.MAIN -> {
                    // First attack rule. Native PDF-point geometry from the authoritative template.
                    onRule(s, fonts.regular, 219.5f, 444.689f, "Bastón de fresno", 9.25f)
                    onRule(s, fonts.regular, 359f, 444.689f, "5 ft", 9f)
                    onRule(s, fonts.regular, 416f, 444.689f, "+3", 9f)
                    onRule(s, fonts.regular, 463f, 444.689f, "1d6 contundente", 9f)
                    // First printed Lanzamiento de Conjuros oval: fill almost all interior.
                    glyphInRect(
                        s, fonts.symbol, 0xE206, Bounds(240f, 0f, 1360f, 1600f),
                        TopRect(80.646f, 442.688f, 14.064f, 17.872f), .35f, .45f,
                    )
                }
                Kind.NARRATIVE -> {
                    // Previous draft started this story block near x=193.5; real rules start ~216.96.
                    val lines = listOf(
                        "Aster abandonó temporalmente los archivos de Liria después de hallar referencias",
                        "a una cámara sellada bajo el Valle del Viento. Viaja con un pequeño grupo",
                        "de aventureros para reconstruir la ruta y comprobar si el códice sobrevivió.",
                        "Esta línea prueba un tamaño mayor sin invadir la regla siguiente.",
                    )
                    listOf(387.996f, 407.839f, 427.681f, 447.524f).zip(lines).forEach { (y, t) ->
                        onRule(s, fonts.regular, 219f, y, t, 9.25f)
                    }
                }
                Kind.SPELLS -> {
                    // Level-1 ESPACIOS box; deliberately much larger than the prior 7.5 pt value.
                    centered(s, fonts.semibold, TopRect(56.98f, 291.55f, 27.49f, 27.49f), "4", 15f, -.35f)
                    // Escudo printed square: fit frozen v8 CHECK to actual container.
                    glyphInRect(
                        s, fonts.symbol, 0xE211, Bounds(285f, 400f, 1350f, 1306f),
                        TopRect(28.205f, 327.187f, 9.669f, 12.287f), .75f, 1.05f, .25f,
                    )
                    // Text starts after the square and sits on the actual printed rule.
                    onRule(s, fonts.regular, 42f, 347.030f, "Escudo", 9.25f)
                }
            }
        }
        return form
    }

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
        val width = font.getStringWidth(text) / 1000f * size
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
        b: Bounds,
        r: TopRect,
        insetX: Float,
        insetY: Float,
        opticalY: Float = 0f,
    ) {
        val targetW = r.width - insetX * 2f
        val targetH = r.height - insetY * 2f
        val sx = targetW * 1000f / b.width
        val sy = targetH * 1000f / b.height
        val left = r.x + insetX
        val bottom = H - (r.top + r.height) + insetY + opticalY
        val ox = left - (b.x0 / 1000f) * sx
        val oy = bottom - (b.y0 / 1000f) * sy
        s.beginText()
        s.setFont(font, 1f)
        s.setTextMatrix(Matrix(sx, 0f, 0f, sy, ox, oy))
        s.showText(String(Character.toChars(cp)))
        s.endText()
    }

    private fun assertHybridFontsEmbedded(doc: PDDocument) {
        val fonts = buildList {
            repeat(doc.numberOfPages) { pageIndex ->
                addAll(fontsIn(doc.getPage(pageIndex).resources))
            }
        }.distinctBy { it.cosObject }
        val hybrid = fonts.filter { font ->
            font.name.contains("FiraSans", ignoreCase = true) ||
                font.name.contains("ParaHojadePJSymbols", ignoreCase = true) ||
                font.name.contains("Para Hoja de PJ", ignoreCase = true)
        }
        assertTrue(
            hybrid.size >= 3,
            "Expected Fira regular/semibold and Para Hoja de PJ v8 in hybrid overlay resources; found: " +
                fonts.joinToString { it.name },
        )
        assertTrue(
            hybrid.all { it.isEmbedded },
            "Hybrid overlay fonts must be embedded after save/reload; non-embedded: " +
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

    private data class TopRect(val x: Float, val top: Float, val width: Float, val height: Float)
    private data class Bounds(val x0: Float, val y0: Float, val x1: Float, val y1: Float) {
        val width get() = x1 - x0
        val height get() = y1 - y0
    }
    private enum class Kind { MAIN, NARRATIVE, SPELLS }

    private companion object {
        const val W = 612f
        const val H = 792f
        const val TEMPLATE = "character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf"

        fun resource(path: String): InputStream = requireNotNull(
            DesktopPcSheetHybridStrategyRun2Test::class.java.classLoader.getResourceAsStream(path),
        ) { "Missing test resource: $path" }

        fun font(doc: PDDocument, path: String): PDFont = resource(path).use { PDType0Font.load(doc, it, false) }
    }
}
