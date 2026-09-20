package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.apache.pdfbox.Loader
import org.apache.pdfbox.multipdf.LayerUtility
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDFormContentStream
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.util.Matrix

/**
 * Custom-v1 Extended Run 4.
 *
 * Run 3 is intentionally preserved as rejected historical evidence. Run 4 changes page 6 only
 * and decomposes the Custom Statistics construction into five explicit PDF layers:
 *
 * 1. immutable source structure;
 * 2. bounded cleanup masks;
 * 3. labels;
 * 4. values/modifiers;
 * 5. approved v8 proficiency/expertise symbols.
 *
 * The five-stage diagnostic proof makes each layer independently reviewable. Regression guards
 * protect the top borders of the authentic Attribute score boxes from every later layer.
 */
class DesktopPcSheetCustomV1ExtendedFamilyRun4Test {\n    private var layerSerial = 0

    @Test
    fun rendersLayeredCustomStatisticsWithoutClippingSourceGeometry() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }

        // Preserve Run 3 exactly as the rejected/superseded predecessor and reuse its untouched pages.
        DesktopPcSheetCustomV1ExtendedFamilyRun3Test().rendersCustomV1ExtensionsFromActualSourcePageGrammar()
        val run3 = File(proofDir, "custom-v1-complete-family-extended-run3.pdf")
        assertTrue(run3.isFile)

        val run3Renders = Loader.loadPDF(run3).use { doc ->
            val renderer = PDFRenderer(doc)
            (0 until doc.numberOfPages).map {
                renderer.renderImageWithDPI(it, BASELINE_COMPARE_DPI, ImageType.RGB)
            }
        }

        val sourceBytes = resource(TEMPLATE).use { it.readBytes() }
        val diagnostics = File(proofDir, "custom-v1-extended-run4-layer-diagnostics.pdf")
        val diagnosticImages = renderDiagnostics(diagnostics, sourceBytes, proofDir)

        // Critical regression: later layers may not alter the source top borders of score boxes.
        val structureOnly = diagnosticImages.first()
        diagnosticImages.drop(1).forEachIndexed { index, candidate ->
            assertProtectedScoreTopBordersEqual(
                expected = structureOnly,
                actual = candidate,
                message = "Run-4 layer stage ${index + 2} changed an immutable Attribute score-box top border.",
            )
        }

        val output = File(proofDir, "custom-v1-complete-family-extended-run4.pdf")
        Loader.loadPDF(run3).use { doc ->
            Loader.loadPDF(sourceBytes).use { source ->
                val oldPage6 = doc.getPage(5)
                val replacement = PDPage(PDRectangle(W, H))
                doc.pages.insertBefore(replacement, oldPage6)
                doc.pages.remove(oldPage6)

                val resources = LayerResources.load(doc, source)
                renderCustomStatisticsPage(doc, replacement, resources, LayerStage.MARKERS)
            }
            doc.save(output)
        }

        Loader.loadPDF(output).use { doc ->
            assertEquals(11, doc.numberOfPages)
            assertFalse(doc.isEncrypted)
            val renderer = PDFRenderer(doc)

            repeat(11) { index ->
                val image = renderer.renderImageWithDPI(index, 220f, ImageType.RGB)
                assertTrue(
                    ImageIO.write(
                        image,
                        "png",
                        File(proofDir, "custom-v1-complete-family-extended-run4-page-${index + 1}.png"),
                    ),
                )
            }

            // Run 4 is page-6-only. Frozen base and existing pages 7-11 must stay pixel-identical.
            val protectedPages = (0..4) + (6..10)
            protectedPages.forEach { index ->
                val actual = renderer.renderImageWithDPI(index, BASELINE_COMPARE_DPI, ImageType.RGB)
                assertImagesEqual(
                    run3Renders[index],
                    actual,
                    "Run 4 changed protected page ${index + 1}; only page 6 may change.",
                )
            }
        }

        assertTrue(output.length() > 20_000L)
        assertTrue(diagnostics.length() > 10_000L)
    }

    private fun renderDiagnostics(
        output: File,
        sourceBytes: ByteArray,
        proofDir: File,
    ): List<BufferedImage> {
        PDDocument().use { doc ->
            Loader.loadPDF(sourceBytes).use { source ->
                val resources = LayerResources.load(doc, source)
                LayerStage.entries.forEach { stage ->
                    val page = PDPage(PDRectangle(W, H)).also(doc::addPage)
                    renderCustomStatisticsPage(doc, page, resources, stage)
                }
            }
            doc.save(output)
        }

        return Loader.loadPDF(output).use { doc ->
            val renderer = PDFRenderer(doc)
            LayerStage.entries.mapIndexed { index, stage ->
                renderer.renderImageWithDPI(index, DIAGNOSTIC_DPI, ImageType.RGB).also { image ->
                    assertTrue(
                        ImageIO.write(
                            image,
                            "png",
                            File(proofDir, "custom-v1-extended-run4-layer-${index + 1}-${stage.slug}.png"),
                        ),
                    )
                }
            }
        }
    }

    private fun renderCustomStatisticsPage(
        doc: PDDocument,
        page: PDPage,
        resources: LayerResources,
        stage: LayerStage,
    ) {
        appendLayer(doc, page, "Run4 1 - immutable source structure") { s ->
            drawSourceStructure(s, resources.mainSource)
        }
        if (stage >= LayerStage.CLEANUP) {
            appendLayer(doc, page, "Run4 2 - bounded cleanup") { s ->
                drawCleanupLayer(s)
            }
        }
        if (stage >= LayerStage.LABELS) {
            appendLayer(doc, page, "Run4 3 - labels") { s ->
                drawLabelLayer(s, resources)
            }
        }
        if (stage >= LayerStage.VALUES) {
            appendLayer(doc, page, "Run4 4 - values and modifiers") { s ->
                drawValueLayer(s, resources)
            }
        }
        if (stage >= LayerStage.MARKERS) {
            appendLayer(doc, page, "Run4 5 - proficiency symbols") { s ->
                drawMarkerLayer(s, resources.symbol)
            }
        }
    }

    private fun drawSourceStructure(
        s: PDFormContentStream,
        mainSource: PDFormXObject,
    ) {
        // Source logo with a generous surrounding crop.
        drawSourceCrop(s, mainSource, 20f, 18f, 170f, 74f)

        // Authentic WIS/INT five-row blocks are the structural source. Crucially, the crop begins
        // above the Attribute header/score structure instead of on its visible edge.
        COLUMNS.forEachIndexed { index, column ->
            val sourceX = if (index % 2 == 0) 408.0f else 311.7f
            drawTranslatedSourceCrop(
                s = s,
                form = mainSource,
                sourceX = sourceX,
                sourceTop = SOURCE_BLOCK_TOP_WITH_MARGIN,
                width = column.width,
                height = SOURCE_BLOCK_HEIGHT_WITH_MARGIN,
                targetX = column.x,
                targetTop = SOURCE_BLOCK_TOP_WITH_MARGIN,
            )
        }

        // Non-problematic page grammar stays independent from the Attribute reconstruction.
        definitionBands(s)
        notesBands(s)
    }

    private fun drawCleanupLayer(s: PDFormContentStream) {
        COLUMNS.forEachIndexed { index, column ->
            val bg = if (column.gray) SOURCE_GRAY else Color.WHITE
            val scoreX = SCORE_X[index]
            val modX = MOD_X[index]

            // Title cleanup is deliberately bounded above the score-box top border.
            fill(s, column.x + 8f, 245f, max(8f, column.width - 16f), 16f, bg)

            // Clear only source glyph interiors; leave every authentic border untouched.
            fill(s, scoreX - 18f, 271f, 36f, 17f, bg)
            fill(s, modX - 10f, 289f, 20f, 10f, bg)
            fill(s, column.x + column.width - 23f, 308f, 18f, 9f, bg)

            SKILL_ROW_TOPS.forEach { rowTop ->
                fill(s, column.x + 12f, rowTop + 0.5f, max(8f, column.width - 43f), 10f, bg)
                fill(s, column.x + column.width - 23f, rowTop + 0.5f, 18f, 9f, bg)
            }
        }
    }

    private fun drawLabelLayer(
        s: PDFormContentStream,
        resources: LayerResources,
    ) {
        centeredText(s, resources.heading, 215f, 55f, 365f, 28f, "Estadísticas Personalizadas", 18f)
        centeredText(
            s,
            resources.fira,
            215f,
            87f,
            365f,
            14f,
            "ATRIBUTOS PERSONALIZADOS Y HABILIDADES VINCULADAS",
            7.5f,
        )
        centeredText(s, resources.heading, 24f, 116f, 564f, 24f, "Definiciones", 16f)
        centeredText(s, resources.heading, 24f, 428f, 564f, 26f, "Notas de Estadísticas Personalizadas", 18f)

        COLUMNS.forEach { column ->
            // Attribute headings retain the source decorative font instead of a generated-value font.
            centeredText(
                s,
                resources.heading,
                column.x + 2f,
                243f,
                column.width - 4f,
                22f,
                column.title,
                18.5f,
            )

            column.skills.forEachIndexed { rowIndex, skill ->
                compressedSourceMatchedLabel(
                    s = s,
                    font = resources.fira,
                    x = column.x + 11.874f,
                    baselineTop = SKILL_ROW_TOPS[rowIndex] + 9.142f,
                    value = skill.first,
                    maximumWidth = column.width - 42f,
                )
            }
        }
    }

    private fun drawValueLayer(
        s: PDFormContentStream,
        resources: LayerResources,
    ) {
        leftText(s, resources.fira, 27f, 144f, "Honor: reputación, deber y prestigio.", 8.2f)
        leftText(s, resources.fira, 217f, 144f, "Resolución: autocontrol bajo presión.", 8.2f)
        leftText(s, resources.fira, 404f, 144f, "Suerte: azar favorable y oportunidades.", 8.2f)

        leftText(s, resources.fira, 47f, 476f, "Honor: reputación y deber.", 8.4f)
        leftText(s, resources.fira, 47f, 496f, "Prestigio y posición social.", 8.4f)
        leftText(s, resources.fira, 379f, 476f, "Resolución: presión, miedo o dolor.", 8.4f)
        leftText(s, resources.fira, 690f - 286f, 476f, "Suerte: azar favorable y oportunidades.", 8.4f)
        leftText(s, resources.fira, 47f, 536f, "Cada habilidad conserva su Atributo.", 8.2f)

        COLUMNS.forEachIndexed { index, column ->
            // Numbers/modifiers use the source GillSans subset, which safely contains the numeric
            // repertoire and preserves the source-sheet value language.
            centeredText(
                s,
                resources.gill,
                SCORE_X[index] - 18f,
                271f,
                36f,
                17f,
                column.score,
                17f,
            )
            centeredText(
                s,
                resources.gill,
                MOD_X[index] - 10f,
                289f,
                20f,
                10f,
                column.modifier,
                10.6f,
            )
            centeredText(
                s,
                resources.gill,
                column.x + column.width - 23f,
                308f,
                18f,
                9f,
                column.save,
                8.2f,
            )

            column.skills.forEachIndexed { rowIndex, skill ->
                centeredText(
                    s,
                    resources.gill,
                    column.x + column.width - 23f,
                    SKILL_ROW_TOPS[rowIndex] + 0.5f,
                    18f,
                    9f,
                    skill.second,
                    7.6f,
                )
            }
        }
    }

    private fun drawMarkerLayer(
        s: PDFormContentStream,
        symbolFont: PDFont,
    ) {
        COLUMNS.forEachIndexed { index, column ->
            if (index < 3) {
                approvedV8Marker(
                    s,
                    symbolFont,
                    centerX = column.x + 5.3f,
                    centerTop = 316.4f,
                    size = 8f,
                    expertise = false,
                )
            }

            column.training.forEachIndexed { rowIndex, training ->
                when (training) {
                    Training.NONE -> Unit
                    Training.PROFICIENT -> approvedV8Marker(
                        s,
                        symbolFont,
                        column.x + 5.3f,
                        SKILL_ROW_TOPS[rowIndex] + 6.2f,
                        7.2f,
                        expertise = false,
                    )
                    Training.EXPERTISE -> approvedV8Marker(
                        s,
                        symbolFont,
                        column.x + 5.3f,
                        SKILL_ROW_TOPS[rowIndex] + 6.2f,
                        7.2f,
                        expertise = true,
                    )
                }
            }
        }
    }

    private fun appendLayer(
        doc: PDDocument,
        page: PDPage,
        name: String,
        draw: (PDFormContentStream) -> Unit,
    ) {
        val form = PDFormXObject(doc).apply {
            resources = PDResources()
            setBBox(PDRectangle(W, H))
        }
        PDFormContentStream(form).use { stream ->
            stream.setNonStrokingColor(Color.BLACK)
            draw(stream)
        }
        LayerUtility(doc).appendFormAsLayer(page, form, AffineTransform(), \"$name #${++layerSerial}\")
    }

    private fun definitionBands(s: PDFormContentStream) {
        val cols = listOf(25f to 181f, 215.291f to 396.708f, 402.378f to 583.795f)
        cols.forEach { (a, b) -> sourceBands(s, a, b, 160f, 4, 20f) }
    }

    private fun notesBands(s: PDFormContentStream) {
        val cols = listOf(25f to 181f, 215.291f to 396.708f, 402.378f to 583.795f)
        cols.forEach { (a, b) -> sourceBands(s, a, b, 468f, 12, 20f) }
    }

    private fun sourceBands(
        s: PDFormContentStream,
        x1: Float,
        x2: Float,
        top: Float,
        rows: Int,
        step: Float,
    ) {
        repeat(rows) { i ->
            val rowTop = top + i * step
            if (i % 2 == 0) fill(s, x1, rowTop - step + 1f, x2 - x1, step - 1f, SOURCE_GRAY)
            s.saveGraphicsState()
            s.setStrokingColor(Color.BLACK)
            s.setLineWidth(0.65f)
            s.moveTo(x1, H - rowTop)
            s.lineTo(x2, H - rowTop)
            s.stroke()
            s.restoreGraphicsState()
        }
    }

    private fun drawSourceCrop(
        s: PDFormContentStream,
        form: PDFormXObject,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
    ) {
        s.saveGraphicsState()
        s.addRect(x, H - top - height, width, height)
        s.clip()
        s.drawForm(form)
        s.restoreGraphicsState()
    }

    private fun drawTranslatedSourceCrop(
        s: PDFormContentStream,
        form: PDFormXObject,
        sourceX: Float,
        sourceTop: Float,
        width: Float,
        height: Float,
        targetX: Float,
        targetTop: Float,
    ) {
        s.saveGraphicsState()
        s.addRect(targetX, H - targetTop - height, width, height)
        s.clip()
        s.transform(Matrix.getTranslateInstance(targetX - sourceX, sourceTop - targetTop))
        s.drawForm(form)
        s.restoreGraphicsState()
    }

    private fun fill(
        s: PDFormContentStream,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        color: Color,
    ) {
        s.saveGraphicsState()
        s.setNonStrokingColor(color)
        s.addRect(x, H - top - height, width, height)
        s.fill()
        s.restoreGraphicsState()
    }

    private fun centeredText(
        s: PDFormContentStream,
        font: PDFont,
        x: Float,
        top: Float,
        width: Float,
        height: Float,
        value: String,
        size: Float,
    ) {
        val textWidth = font.getStringWidth(value) / 1000f * size
        val descriptor = requireNotNull(font.fontDescriptor)
        val ascent = descriptor.ascent / 1000f * size
        val descent = descriptor.descent / 1000f * size
        val boxY = H - top - height
        val baseline = boxY + (height - (ascent - descent)) / 2f - descent
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, size)
        s.newLineAtOffset(x + (width - textWidth) / 2f, baseline)
        s.showText(value)
        s.endText()
    }

    private fun leftText(
        s: PDFormContentStream,
        font: PDFont,
        x: Float,
        baselineTop: Float,
        value: String,
        size: Float,
    ) {
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, size)
        s.newLineAtOffset(x, H - baselineTop)
        s.showText(value)
        s.endText()
    }

    private fun compressedSourceMatchedLabel(
        s: PDFormContentStream,
        font: PDFont,
        x: Float,
        baselineTop: Float,
        value: String,
        maximumWidth: Float,
    ) {
        val size = 10f
        val scale = 60f
        val width = font.getStringWidth(value) / 1000f * size * (scale / 100f)
        require(width <= maximumWidth + 0.1f) {
            "Run-4 source-matched skill label exceeds authentic source row width: '$value' ($width > $maximumWidth)"
        }
        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, size)
        s.setHorizontalScaling(scale)
        s.newLineAtOffset(x, H - baselineTop)
        s.showText(value)
        s.endText()
    }

    private fun approvedV8Marker(
        s: PDFormContentStream,
        font: PDFont,
        centerX: Float,
        centerTop: Float,
        size: Float,
        expertise: Boolean,
    ) {
        val glyph = if (expertise) {
            V8Glyph(0xE212, 390, 289, 1237, 1358)
        } else {
            V8Glyph(0xE211, 285, 400, 1350, 1306)
        }
        val designWidth = (glyph.xMax - glyph.xMin).toFloat()
        val designHeight = (glyph.yMax - glyph.yMin).toFloat()
        val fontSize = size * SYMBOL_UNITS_PER_EM / max(designWidth, designHeight)
        val centerY = H - centerTop
        val originX = centerX - ((glyph.xMin + glyph.xMax) / 2f / SYMBOL_UNITS_PER_EM) * fontSize
        val originY = centerY - ((glyph.yMin + glyph.yMax) / 2f / SYMBOL_UNITS_PER_EM) * fontSize

        s.beginText()
        s.setNonStrokingColor(Color.BLACK)
        s.setFont(font, fontSize)
        s.newLineAtOffset(originX, originY)
        s.showText(String(Character.toChars(glyph.codePoint)))
        s.endText()
    }

    private fun assertProtectedScoreTopBordersEqual(
        expected: BufferedImage,
        actual: BufferedImage,
        message: String,
    ) {
        SCORE_X.forEachIndexed { index, center ->
            assertRegionImagesEqual(
                expected,
                actual,
                leftPt = center - 31f,
                topPt = SCORE_BORDER_PROTECTED_TOP,
                widthPt = 62f,
                heightPt = SCORE_BORDER_PROTECTED_HEIGHT,
                dpi = DIAGNOSTIC_DPI,
                message = "$message Column ${index + 1}.",
            )
        }
    }

    private fun assertRegionImagesEqual(
        expected: BufferedImage,
        actual: BufferedImage,
        leftPt: Float,
        topPt: Float,
        widthPt: Float,
        heightPt: Float,
        dpi: Float,
        message: String,
    ) {
        val scale = dpi / 72f
        val x0 = (leftPt * scale).toInt().coerceAtLeast(0)
        val y0 = (topPt * scale).toInt().coerceAtLeast(0)
        val x1 = ((leftPt + widthPt) * scale).toInt().coerceAtMost(expected.width - 1)
        val y1 = ((topPt + heightPt) * scale).toInt().coerceAtMost(expected.height - 1)

        for (y in y0..y1) {
            for (x in x0..x1) {
                assertEquals(expected.getRGB(x, y), actual.getRGB(x, y), "$message First differing pixel: ($x,$y)")
            }
        }
    }

    private fun assertImagesEqual(expected: BufferedImage, actual: BufferedImage, message: String) {
        assertEquals(expected.width, actual.width, message)
        assertEquals(expected.height, actual.height, message)
        val a = IntArray(expected.width)
        val b = IntArray(expected.width)
        repeat(expected.height) { y ->
            expected.getRGB(0, y, expected.width, 1, a, 0, expected.width)
            actual.getRGB(0, y, actual.width, 1, b, 0, actual.width)
            assertTrue(a.contentEquals(b), "$message First differing raster row: $y")
        }
    }

    private fun loadEmbeddedSourceFont(target: PDDocument, source: PDDocument, token: String): PDFont {
        source.pages.forEach { page ->
            val resources: PDResources = page.resources ?: return@forEach
            resources.fontNames.forEach { name ->
                val font = resources.getFont(name)
                if (font.name.contains(token, ignoreCase = true)) {
                    val descriptor = requireNotNull(font.fontDescriptor)
                    val stream = descriptor.fontFile2 ?: descriptor.fontFile ?: descriptor.fontFile3
                    requireNotNull(stream) { "Source font $token is not embedded." }
                    val bytes = stream.createInputStream().use { it.readBytes() }
                    return PDType0Font.load(target, ByteArrayInputStream(bytes), false)
                }
            }
        }
        error("Embedded source font not found: $token")
    }

    private fun loadResourceFont(doc: PDDocument, path: String): PDFont =
        resource(path).use { PDType0Font.load(doc, it, true) }

    private fun resource(path: String): InputStream =
        requireNotNull(DesktopPcSheetCustomV1ExtendedFamilyRun4Test::class.java.classLoader.getResourceAsStream(path)) {
            "Missing test resource: $path"
        }

    private data class LayerResources(
        val mainSource: PDFormXObject,
        val heading: PDFont,
        val gill: PDFont,
        val fira: PDFont,
        val symbol: PDFont,
    ) {
        companion object {
            fun load(doc: PDDocument, source: PDDocument): LayerResources {
                val owner = DesktopPcSheetCustomV1ExtendedFamilyRun4Test()
                return LayerResources(
                    mainSource = LayerUtility(doc).importPageAsForm(source, 0),
                    heading = owner.loadEmbeddedSourceFont(doc, source, "EnchantedLand"),
                    gill = owner.loadEmbeddedSourceFont(doc, source, "GillSansMT"),
                    fira = owner.loadResourceFont(doc, FIRA_RESOURCE),
                    symbol = owner.loadResourceFont(doc, SYMBOL_RESOURCE),
                )
            }
        }
    }

    private data class V8Glyph(
        val codePoint: Int,
        val xMin: Int,
        val yMin: Int,
        val xMax: Int,
        val yMax: Int,
    )

    private data class AttributeColumn(
        val x: Float,
        val width: Float,
        val gray: Boolean,
        val title: String,
        val score: String,
        val modifier: String,
        val save: String,
        val skills: List<Pair<String, String>>,
        val training: List<Training>,
    )

    private enum class Training { NONE, PROFICIENT, EXPERTISE }

    private enum class LayerStage(val slug: String) {
        STRUCTURE("structure"),
        CLEANUP("cleanup"),
        LABELS("labels"),
        VALUES("values"),
        MARKERS("markers"),
    }

    private companion object {
        const val W = 612f
        const val H = 792f
        const val BASELINE_COMPARE_DPI = 120f
        const val DIAGNOSTIC_DPI = 220f
        const val SYMBOL_UNITS_PER_EM = 2048f
        const val TEMPLATE = "character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf"
        const val FIRA_RESOURCE = "fonts/pdf/text/FiraSans-Regular.ttf"
        const val SYMBOL_RESOURCE = "fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf"

        const val SOURCE_BLOCK_TOP_WITH_MARGIN = 232f
        const val SOURCE_BLOCK_HEIGHT_WITH_MARGIN = 174f
        const val SCORE_BORDER_PROTECTED_TOP = 264f
        const val SCORE_BORDER_PROTECTED_HEIGHT = 5f

        val SOURCE_GRAY = Color(211, 210, 210)
        val SCORE_X = listOf(58f, 154.25f, 250.75f, 347.25f, 445.5f, 539.75f)
        val MOD_X = listOf(85f, 184.25f, 280.5f, 376.75f, 473.25f, 569.5f)
        val SKILL_ROW_TOPS = listOf(324.1f, 338.2f, 352.4f, 366.6f, 380.8f)

        val COLUMNS = listOf(
            AttributeColumn(
                22.5f, 96.4f, false, "Honor", "15", "+2", "+5",
                listOf(
                    "Etiqueta cortesana" to "+5",
                    "Reputación" to "+5",
                    "Deber" to "+2",
                    "Protocolo" to "+5",
                ),
                listOf(Training.PROFICIENT, Training.PROFICIENT, Training.NONE, Training.PROFICIENT),
            ),
            AttributeColumn(
                118.9f, 96.4f, true, "Resolución", "12", "+1", "+1",
                listOf(
                    "Concentración" to "+4",
                    "Resistir miedo" to "+4",
                    "Autocontrol" to "+1",
                ),
                listOf(Training.PROFICIENT, Training.PROFICIENT, Training.NONE),
            ),
            AttributeColumn(
                215.3f, 96.4f, false, "Suerte", "18", "+4", "+7",
                listOf(
                    "Lectura de fortuna" to "+7",
                    "Escapismo" to "+10",
                    "Azar" to "+4",
                ),
                listOf(Training.PROFICIENT, Training.EXPERTISE, Training.NONE),
            ),
            AttributeColumn(
                311.7f, 96.4f, true, "INTeligencia", "18", "+4", "+7",
                listOf(
                    "Cifras antiguas" to "+7",
                    "Análisis de runas" to "+7",
                ),
                listOf(Training.PROFICIENT, Training.PROFICIENT),
            ),
            AttributeColumn(
                408.0f, 96.4f, false, "SABiduría", "12", "+1", "+1",
                listOf(
                    "Cartografía" to "+4",
                    "Orientación astral" to "+1",
                ),
                listOf(Training.PROFICIENT, Training.NONE),
            ),
            AttributeColumn(
                504.4f, 85.5f, true, "DEStreza", "16", "+3", "+6",
                listOf(
                    "Acrobacia aérea" to "+6",
                    "Cerrajería fina" to "+9",
                ),
                listOf(Training.PROFICIENT, Training.EXPERTISE),
            ),
        )
    }
}
