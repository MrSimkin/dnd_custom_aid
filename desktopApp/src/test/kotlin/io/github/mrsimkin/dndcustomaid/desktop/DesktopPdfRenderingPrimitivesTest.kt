package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper

class DesktopPdfRenderingPrimitivesTest {
    @Test
    fun generatesMetricDrivenPrimitiveQaArtifactWithBundledFontsMarkersAndPortraitModes() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val pdf = File(proofDir, "renderer-primitives-qa.pdf")

        PDDocument().use { document ->
            val fonts = DesktopPdfFontRegistry(document)
            val primitives = DesktopPdfRenderingPrimitives(fonts)

            val pageOne = PDPage(PDRectangle.LETTER)
            document.addPage(pageOne)
            PDPageContentStream(document, pageOne).use { stream ->
                drawPrimitiveQaPageOne(stream, primitives)
            }

            val pageTwo = PDPage(PDRectangle.LETTER)
            document.addPage(pageTwo)
            PDPageContentStream(document, pageTwo).use { stream ->
                drawPrimitiveQaPageTwo(document, stream, primitives)
            }

            document.save(pdf)
        }

        assertTrue(pdf.length() > 0L)
        Loader.loadPDF(pdf).use { document ->
            assertEquals(2, document.numberOfPages)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("Alineación métrica"))
            assertTrue(extracted.contains("Características"))
            assertTrue(extracted.contains("overflow detectado"))
            assertTrue(extracted.contains("Tabla repetible"))
            assertTrue(extracted.contains("Retrato: Fit vs Crop"))
            assertTrue(extracted.contains("Aster Vale"))

            val renderer = PDFRenderer(document)
            val firstPng = File(proofDir, "renderer-primitives-qa.png")
            val secondPng = File(proofDir, "renderer-primitives-qa-page-2.png")
            assertTrue(ImageIO.write(renderer.renderImageWithDPI(0, 144f, ImageType.RGB), "png", firstPng))
            assertTrue(ImageIO.write(renderer.renderImageWithDPI(1, 144f, ImageType.RGB), "png", secondPng))
            assertTrue(firstPng.length() > 0L)
            assertTrue(secondPng.length() > 0L)
        }
    }

    @Test
    fun reportsOverflowInsteadOfSilentlyTruncatingDenseText() {
        PDDocument().use { document ->
            val page = PDPage(PDRectangle.LETTER)
            document.addPage(page)
            val primitives = DesktopPdfRenderingPrimitives(DesktopPdfFontRegistry(document))
            PDPageContentStream(document, page).use { stream ->
                val result = primitives.drawTextBox(
                    stream,
                    PdfTextBoxSpec(
                        rect = PdfRect(40f, 700f, 110f, 28f),
                        text = "Características extraordinariamente extensas para una caja deliberadamente pequeña",
                        role = PdfTypographyRole.BODY,
                        preferredSizePt = 10f,
                        minimumSizePt = 7f,
                        wrapPolicy = PdfWrapPolicy.WORD_WRAP,
                        maximumLines = 2,
                    ),
                )
                assertTrue(result.hasOverflow)
                assertTrue(result.overflowText!!.isNotBlank())
            }
        }
    }

    @Test
    fun normalWrappedTextFitsWithoutOverflow() {
        PDDocument().use { document ->
            val page = PDPage(PDRectangle.LETTER)
            document.addPage(page)
            val primitives = DesktopPdfRenderingPrimitives(DesktopPdfFontRegistry(document))
            PDPageContentStream(document, page).use { stream ->
                val result = primitives.drawTextBox(
                    stream,
                    PdfTextBoxSpec(
                        rect = PdfRect(40f, 650f, 240f, 55f),
                        text = "Texto narrativo en español con acentos, vínculos e ideales.",
                        role = PdfTypographyRole.NOTE_TEXT,
                        preferredSizePt = 10f,
                        minimumSizePt = 7f,
                        wrapPolicy = PdfWrapPolicy.WORD_WRAP,
                        maximumLines = 4,
                    ),
                )
                assertFalse(result.hasOverflow)
                assertTrue(result.renderedLines.isNotEmpty())
            }
        }
    }

    @Test
    fun fixedRuledLineTextKeepsItsConfiguredSizeAndReportsOverflow() {
        PDDocument().use { document ->
            val page = PDPage(PDRectangle.LETTER)
            document.addPage(page)
            val primitives = DesktopPdfRenderingPrimitives(DesktopPdfFontRegistry(document))
            PDPageContentStream(document, page).use { stream ->
                val result = primitives.drawTextBox(
                    stream,
                    PdfTextBoxSpec(
                        rect = PdfRect(40f, 700f, 95f, 16f),
                        text = "Línea reglada con contenido demasiado extenso",
                        role = PdfTypographyRole.BODY,
                        preferredSizePt = 8f,
                        minimumSizePt = 8f,
                        fontSizeMode = PdfFontSizeMode.FIXED,
                        wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                    ),
                )
                assertTrue(result.hasOverflow)
                assertTrue(kotlin.math.abs(result.fontSizePt - 8f) < 0.001f)
            }
        }
    }

    private fun drawPrimitiveQaPageOne(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
    ) {
        heading(
            stream,
            primitives,
            PdfRect(36f, 744f, 540f, 26f),
            "Primitive QA — PC Sheet PDF renderer",
            PdfTypographyRole.CHARACTER_NAME,
            16f,
        )
        heading(
            stream,
            primitives,
            PdfRect(36f, 718f, 540f, 18f),
            "PDFBox 3 / métricas reales / fuentes candidatas / sin aprobación de familia visual",
            PdfTypographyRole.COMPACT_TABLE,
            8.3f,
        )

        heading(stream, primitives, PdfRect(36f, 688f, 540f, 18f), "Alineación métrica horizontal", PdfTypographyRole.PRIMARY_VALUE, 10f)
        val horizontalRects = listOf(
            PdfRect(36f, 636f, 168f, 44f),
            PdfRect(222f, 636f, 168f, 44f),
            PdfRect(408f, 636f, 168f, 44f),
        )
        horizontalRects.forEach { primitives.drawGrid(stream, it, 1, 1, 0.35f) }
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(horizontalRects[0], "Izquierda +12", PdfTypographyRole.NUMERIC_COMPACT, 12f, 8f, PdfHorizontalAlignment.LEFT),
        )
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(horizontalRects[1], "Centro -3", PdfTypographyRole.PRIMARY_VALUE, 12f, 8f, PdfHorizontalAlignment.CENTER),
        )
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(horizontalRects[2], "Derecha 18", PdfTypographyRole.NUMERIC_COMPACT, 12f, 8f, PdfHorizontalAlignment.RIGHT),
        )

        heading(stream, primitives, PdfRect(36f, 606f, 540f, 18f), "Alineación vertical basada en métricas", PdfTypographyRole.PRIMARY_VALUE, 10f)
        val verticalRects = listOf(
            PdfRect(36f, 552f, 168f, 44f),
            PdfRect(222f, 552f, 168f, 44f),
            PdfRect(408f, 552f, 168f, 44f),
        )
        verticalRects.forEach { primitives.drawGrid(stream, it, 1, 1, 0.35f) }
        val verticalModes = listOf(PdfVerticalAlignment.TOP, PdfVerticalAlignment.CENTER, PdfVerticalAlignment.BOTTOM)
        val verticalLabels = listOf("TOP Ag12", "CENTER Ag12", "BOTTOM Ag12")
        verticalRects.indices.forEach { index ->
            primitives.drawTextBox(
                stream,
                PdfTextBoxSpec(
                    rect = verticalRects[index],
                    text = verticalLabels[index],
                    role = PdfTypographyRole.BODY,
                    preferredSizePt = 10f,
                    minimumSizePt = 8f,
                    horizontalAlignment = PdfHorizontalAlignment.CENTER,
                    verticalAlignment = verticalModes[index],
                    verticalPaddingPt = 4f,
                ),
            )
        }

        heading(stream, primitives, PdfRect(36f, 522f, 540f, 18f), "Roles tipográficos y fitting", PdfTypographyRole.PRIMARY_VALUE, 10f)
        primitives.drawGrid(stream, PdfRect(36f, 468f, 540f, 46f), 3, 1, 0.35f)
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(PdfRect(36f, 468f, 180f, 46f), "Aster Vale", PdfTypographyRole.CHARACTER_NAME, 15f, 10f, PdfHorizontalAlignment.CENTER),
        )
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(PdfRect(216f, 468f, 180f, 46f), "+7 / CD 15", PdfTypographyRole.PRIMARY_VALUE, 13f, 8f, PdfHorizontalAlignment.CENTER),
        )
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(PdfRect(396f, 468f, 180f, 46f), "Mago 5 / Pícaro 2", PdfTypographyRole.COMPACT_TABLE, 11f, 7f, PdfHorizontalAlignment.CENTER),
        )

        heading(stream, primitives, PdfRect(36f, 438f, 540f, 18f), "Wrapping, fitting y overflow explícito", PdfTypographyRole.PRIMARY_VALUE, 10f)
        val narrativeRect = PdfRect(36f, 342f, 342f, 88f)
        primitives.drawGrid(stream, narrativeRect, 1, 1, 0.35f)
        val normalResult = primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = narrativeRect,
                text = "Características, vínculos e ideales: una descripción suficientemente larga para validar saltos de línea medidos con métricas reales y texto español.",
                role = PdfTypographyRole.NOTE_TEXT,
                preferredSizePt = 10f,
                minimumSizePt = 7f,
                wrapPolicy = PdfWrapPolicy.WORD_WRAP,
                maximumLines = 5,
                horizontalAlignment = PdfHorizontalAlignment.LEFT,
                verticalAlignment = PdfVerticalAlignment.TOP,
                horizontalPaddingPt = 5f,
                verticalPaddingPt = 4f,
            ),
        )
        assertFalse(normalResult.hasOverflow)

        val denseRect = PdfRect(396f, 370f, 180f, 60f)
        primitives.drawGrid(stream, denseRect, 1, 1, 0.35f)
        val denseResult = primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = denseRect,
                text = "Caso denso deliberadamente extenso: esta caja debe quedarse sin capacidad y reportar el remanente para una continuación, nunca descartarlo.",
                role = PdfTypographyRole.COMPACT_TABLE,
                preferredSizePt = 9f,
                minimumSizePt = 7f,
                wrapPolicy = PdfWrapPolicy.WORD_WRAP,
                maximumLines = 1,
                horizontalPaddingPt = 4f,
                verticalPaddingPt = 4f,
            ),
        )
        assertTrue(denseResult.hasOverflow)
        heading(
            stream,
            primitives,
            PdfRect(396f, 340f, 180f, 24f),
            "overflow detectado -> continuación requerida",
            PdfTypographyRole.COMPACT_TABLE,
            7f,
        )

        heading(stream, primitives, PdfRect(36f, 308f, 540f, 18f), "Marcadores vectoriales — candidatos", PdfTypographyRole.PRIMARY_VALUE, 10f)
        PdfMarkerKind.entries.forEachIndexed { index, kind ->
            val x = 52f + index * 57f
            primitives.drawMarker(stream, x, 270f, 13f, kind, 0.9f)
            primitives.drawTextBox(
                stream,
                PdfTextBoxSpec(
                    rect = PdfRect(x - 25f, 238f, 50f, 22f),
                    text = (index + 1).toString(),
                    role = PdfTypographyRole.NUMERIC_COMPACT,
                    preferredSizePt = 7f,
                    minimumSizePt = 6f,
                    horizontalAlignment = PdfHorizontalAlignment.CENTER,
                    verticalAlignment = PdfVerticalAlignment.TOP,
                ),
            )
        }

        heading(
            stream,
            primitives,
            PdfRect(36f, 190f, 540f, 30f),
            "Gate: revisar X/Y, métricas, font role, tamaño, wrapping, marker, spacing y overflow por separado.",
            PdfTypographyRole.BODY,
            8.5f,
        )
        heading(
            stream,
            primitives,
            PdfRect(36f, 156f, 540f, 24f),
            "El font de símbolos del owner se compara aparte: su binario privado no vive en el repo público.",
            PdfTypographyRole.COMPACT_TABLE,
            7.5f,
        )
    }

    private fun drawPrimitiveQaPageTwo(
        document: PDDocument,
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
    ) {
        heading(stream, primitives, PdfRect(36f, 744f, 540f, 26f), "Primitive QA — estructuras repetidas y retrato", PdfTypographyRole.CHARACTER_NAME, 15f)

        heading(stream, primitives, PdfRect(36f, 706f, 540f, 18f), "Tabla repetible", PdfTypographyRole.PRIMARY_VALUE, 10f)
        val table = PdfRect(36f, 548f, 540f, 150f)
        primitives.drawGrid(stream, table, columns = 4, rows = 6, lineWidthPt = 0.4f)
        val labels = listOf("Objeto", "Cantidad", "Ubicación", "Notas")
        labels.forEachIndexed { index, label ->
            primitives.drawTextBox(
                stream,
                PdfTextBoxSpec(
                    rect = PdfRect(36f + index * 135f, 673f, 135f, 25f),
                    text = label,
                    role = PdfTypographyRole.COMPACT_TABLE,
                    preferredSizePt = 8f,
                    minimumSizePt = 6.5f,
                    horizontalAlignment = PdfHorizontalAlignment.CENTER,
                ),
            )
        }
        val rows = listOf(
            listOf("Cuerda de seda", "1", "Mochila", "50 pies"),
            listOf("Poción de curación", "2", "Cinturón", "Uso rápido"),
            listOf("Libro de conjuros", "1", "Mochila", "Protegido"),
            listOf("Dagas", "3", "Arnés", "20/60 pies"),
            listOf("Componente muy largo", "12", "Bolsa lateral", "Texto para fitting"),
        )
        rows.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, value ->
                primitives.drawTextBox(
                    stream,
                    PdfTextBoxSpec(
                        rect = PdfRect(36f + columnIndex * 135f, 548f + (4 - rowIndex) * 25f, 135f, 25f),
                        text = value,
                        role = PdfTypographyRole.COMPACT_TABLE,
                        preferredSizePt = 7.5f,
                        minimumSizePt = 6f,
                        horizontalAlignment = if (columnIndex == 1) PdfHorizontalAlignment.CENTER else PdfHorizontalAlignment.LEFT,
                        horizontalPaddingPt = 4f,
                    ),
                )
            }
        }

        heading(stream, primitives, PdfRect(36f, 506f, 540f, 18f), "Retrato: Fit vs Crop", PdfTypographyRole.PRIMARY_VALUE, 10f)
        val source = LosslessFactory.createFromImage(document, syntheticPortrait())
        val fitRect = PdfRect(56f, 286f, 220f, 190f)
        val cropRect = PdfRect(336f, 286f, 220f, 190f)
        primitives.drawGrid(stream, fitRect, 1, 1, 0.5f)
        primitives.drawGrid(stream, cropRect, 1, 1, 0.5f)
        val fit = primitives.drawImage(stream, source, fitRect, PdfImageFitMode.FIT_ENTIRE)
        val crop = primitives.drawImage(stream, source, cropRect, PdfImageFitMode.CROP_FILL)
        assertFalse(fit.cropped)
        assertTrue(crop.cropped)
        assertTrue(crop.drawWidth >= cropRect.width)
        assertTrue(crop.drawHeight >= cropRect.height)

        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = PdfRect(56f, 257f, 220f, 28f),
                text = "Aster Vale",
                role = PdfTypographyRole.HANDWRITTEN_NAME,
                preferredSizePt = 16f,
                minimumSizePt = 12f,
                horizontalAlignment = PdfHorizontalAlignment.CENTER,
                verticalAlignment = PdfVerticalAlignment.CENTER,
            ),
        )
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = PdfRect(336f, 257f, 220f, 28f),
                text = "Aster Vale",
                role = PdfTypographyRole.HANDWRITTEN_NAME,
                preferredSizePt = 16f,
                minimumSizePt = 12f,
                horizontalAlignment = PdfHorizontalAlignment.CENTER,
                verticalAlignment = PdfVerticalAlignment.CENTER,
            ),
        )
        heading(stream, primitives, PdfRect(56f, 238f, 220f, 18f), "FIT ENTIRE — imagen completa", PdfTypographyRole.COMPACT_TABLE, 8f)
        heading(stream, primitives, PdfRect(336f, 238f, 220f, 18f), "CROP FILL — marco completo", PdfTypographyRole.COMPACT_TABLE, 8f)

        heading(
            stream,
            primitives,
            PdfRect(36f, 194f, 540f, 42f),
            "Ambos modos preservan proporción. Crop usa clipping vectorial del marco; Fit conserva la imagen completa.",
            PdfTypographyRole.BODY,
            9f,
        )
        heading(
            stream,
            primitives,
            PdfRect(36f, 150f, 540f, 30f),
            "Tema tipográfico: roles semánticos -> recursos explícitos; cada familia podrá inyectar su propio mapa sin cambiar métricas/layout.",
            PdfTypographyRole.COMPACT_TABLE,
            7.8f,
        )
    }

    private fun syntheticPortrait(): BufferedImage {
        val image = BufferedImage(320, 180, BufferedImage.TYPE_INT_RGB)
        val graphics = image.createGraphics()
        try {
            graphics.color = Color.WHITE
            graphics.fillRect(0, 0, image.width, image.height)
            graphics.color = Color.LIGHT_GRAY
            graphics.fillRect(0, 0, 90, image.height)
            graphics.fillRect(230, 0, 90, image.height)
            graphics.color = Color.BLACK
            graphics.drawRect(0, 0, image.width - 1, image.height - 1)
            graphics.drawOval(115, 25, 90, 90)
            graphics.drawLine(160, 115, 105, 170)
            graphics.drawLine(160, 115, 215, 170)
            graphics.drawLine(30, 25, 290, 155)
            graphics.drawString("PORTRAIT QA", 116, 165)
        } finally {
            graphics.dispose()
        }
        return image
    }

    private fun heading(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        rect: PdfRect,
        text: String,
        role: PdfTypographyRole,
        size: Float,
    ) {
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = rect,
                text = text,
                role = role,
                preferredSizePt = size,
                minimumSizePt = minOf(size, 6.5f),
                horizontalAlignment = PdfHorizontalAlignment.LEFT,
                verticalAlignment = PdfVerticalAlignment.CENTER,
                wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
            ),
        )
    }
}
