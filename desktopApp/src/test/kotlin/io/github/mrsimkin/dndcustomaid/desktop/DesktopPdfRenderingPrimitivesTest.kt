package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.Color
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper

class DesktopPdfRenderingPrimitivesTest {
    @Test
    fun generatesMetricDrivenPrimitiveQaArtifactWithBundledFontsAndVectorMarkers() {
        val proofDir = File(requireNotNull(System.getProperty("pcSheetProofDir"))).apply { mkdirs() }
        val pdf = File(proofDir, "renderer-primitives-qa.pdf")

        PDDocument().use { document ->
            val page = PDPage(PDRectangle.LETTER)
            document.addPage(page)
            val fonts = DesktopPdfFontRegistry(document)
            val primitives = DesktopPdfRenderingPrimitives(fonts)

            PDPageContentStream(document, page).use { stream ->
                drawPrimitiveQaPage(stream, primitives)
            }
            document.save(pdf)
        }

        assertTrue(pdf.length() > 0L)
        Loader.loadPDF(pdf).use { document ->
            assertTrue(document.numberOfPages == 1)
            val extracted = PDFTextStripper().getText(document)
            assertTrue(extracted.contains("Alineación métrica"))
            assertTrue(extracted.contains("Características"))
            assertTrue(extracted.contains("Tabla repetible"))

            val png = File(proofDir, "renderer-primitives-qa.png")
            val image = PDFRenderer(document).renderImageWithDPI(0, 144f, ImageType.RGB)
            assertTrue(ImageIO.write(image, "png", png))
            assertTrue(png.length() > 0L)
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
                assertTrue(result.renderedLines.size >= 1)
            }
        }
    }

    private fun drawPrimitiveQaPage(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
    ) {
        heading(
            stream,
            primitives,
            PdfRect(36f, 742f, 540f, 28f),
            "Primitive QA — PC Sheet PDF renderer",
            PdfTypographyRole.CHARACTER_NAME,
            16f,
        )
        heading(
            stream,
            primitives,
            PdfRect(36f, 714f, 540f, 20f),
            "PDFBox 3 foundation / bundled-font candidates / no visual-family approval implied",
            PdfTypographyRole.COMPACT_TABLE,
            8.5f,
        )

        heading(stream, primitives, PdfRect(36f, 682f, 540f, 18f), "Alineación métrica", PdfTypographyRole.PRIMARY_VALUE, 10f)
        val alignRects = listOf(
            PdfRect(36f, 632f, 168f, 44f),
            PdfRect(222f, 632f, 168f, 44f),
            PdfRect(408f, 632f, 168f, 44f),
        )
        alignRects.forEach { primitives.drawGrid(stream, it, 1, 1, 0.35f) }
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                alignRects[0], "Izquierda +12", PdfTypographyRole.NUMERIC_COMPACT, 12f, 8f,
                PdfHorizontalAlignment.LEFT, PdfVerticalAlignment.CENTER,
            ),
        )
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                alignRects[1], "Centro −3", PdfTypographyRole.PRIMARY_VALUE, 12f, 8f,
                PdfHorizontalAlignment.CENTER, PdfVerticalAlignment.CENTER,
            ),
        )
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                alignRects[2], "Derecha 18", PdfTypographyRole.NUMERIC_COMPACT, 12f, 8f,
                PdfHorizontalAlignment.RIGHT, PdfVerticalAlignment.CENTER,
            ),
        )

        heading(stream, primitives, PdfRect(36f, 602f, 540f, 18f), "Roles tipográficos y fitting", PdfTypographyRole.PRIMARY_VALUE, 10f)
        primitives.drawGrid(stream, PdfRect(36f, 546f, 540f, 50f), 3, 1, 0.35f)
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                PdfRect(36f, 546f, 180f, 50f), "Aster Vale", PdfTypographyRole.CHARACTER_NAME, 15f, 10f,
                PdfHorizontalAlignment.CENTER, PdfVerticalAlignment.CENTER,
            ),
        )
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                PdfRect(216f, 546f, 180f, 50f), "+7 / CD 15", PdfTypographyRole.PRIMARY_VALUE, 13f, 8f,
                PdfHorizontalAlignment.CENTER, PdfVerticalAlignment.CENTER,
            ),
        )
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                PdfRect(396f, 546f, 180f, 50f), "Mago 5 / Pícaro 2", PdfTypographyRole.COMPACT_TABLE, 11f, 7f,
                PdfHorizontalAlignment.CENTER, PdfVerticalAlignment.CENTER,
            ),
        )

        heading(stream, primitives, PdfRect(36f, 516f, 540f, 18f), "Wrapping y piso de legibilidad", PdfTypographyRole.PRIMARY_VALUE, 10f)
        val narrativeRect = PdfRect(36f, 434f, 342f, 74f)
        primitives.drawGrid(stream, narrativeRect, 1, 1, 0.35f)
        primitives.drawTextBox(
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
        val denseRect = PdfRect(396f, 434f, 180f, 74f)
        primitives.drawGrid(stream, denseRect, 1, 1, 0.35f)
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = denseRect,
                text = "Caso denso: contenido deliberadamente largo para forzar la detección explícita de overflow sin truncado silencioso.",
                role = PdfTypographyRole.COMPACT_TABLE,
                preferredSizePt = 9f,
                minimumSizePt = 7f,
                wrapPolicy = PdfWrapPolicy.WORD_WRAP,
                maximumLines = 3,
                horizontalPaddingPt = 4f,
                verticalPaddingPt = 4f,
            ),
        )

        heading(stream, primitives, PdfRect(36f, 404f, 540f, 18f), "Marcadores vectoriales — candidatos", PdfTypographyRole.PRIMARY_VALUE, 10f)
        val kinds = PdfMarkerKind.entries
        kinds.forEachIndexed { index, kind ->
            val x = 52f + index * 57f
            primitives.drawMarker(stream, x, 374f, 13f, kind, 0.9f)
            primitives.drawTextBox(
                stream,
                PdfTextBoxSpec(
                    rect = PdfRect(x - 25f, 344f, 50f, 22f),
                    text = (index + 1).toString(),
                    role = PdfTypographyRole.NUMERIC_COMPACT,
                    preferredSizePt = 7f,
                    minimumSizePt = 6f,
                    horizontalAlignment = PdfHorizontalAlignment.CENTER,
                    verticalAlignment = PdfVerticalAlignment.TOP,
                ),
            )
        }

        heading(stream, primitives, PdfRect(36f, 316f, 540f, 18f), "Tabla repetible", PdfTypographyRole.PRIMARY_VALUE, 10f)
        val table = PdfRect(36f, 196f, 540f, 112f)
        primitives.drawGrid(stream, table, columns = 4, rows = 5, lineWidthPt = 0.4f)
        val labels = listOf("Objeto", "Cantidad", "Ubicación", "Notas")
        labels.forEachIndexed { index, label ->
            primitives.drawTextBox(
                stream,
                PdfTextBoxSpec(
                    rect = PdfRect(36f + index * 135f, 285.6f, 135f, 22.4f),
                    text = label,
                    role = PdfTypographyRole.COMPACT_TABLE,
                    preferredSizePt = 8f,
                    minimumSizePt = 6.5f,
                    horizontalAlignment = PdfHorizontalAlignment.CENTER,
                    verticalAlignment = PdfVerticalAlignment.CENTER,
                ),
            )
        }
        val rows = listOf(
            listOf("Cuerda de seda", "1", "Mochila", "50 pies"),
            listOf("Poción de curación", "2", "Cinturón", "Uso rápido"),
            listOf("Libro de conjuros", "1", "Mochila", "Protegido"),
            listOf("Dagas", "3", "Arnés", "20/60 pies"),
        )
        rows.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, value ->
                primitives.drawTextBox(
                    stream,
                    PdfTextBoxSpec(
                        rect = PdfRect(
                            36f + columnIndex * 135f,
                            196f + (3 - rowIndex) * 22.4f,
                            135f,
                            22.4f,
                        ),
                        text = value,
                        role = PdfTypographyRole.COMPACT_TABLE,
                        preferredSizePt = 7.5f,
                        minimumSizePt = 6f,
                        horizontalAlignment = if (columnIndex == 1) PdfHorizontalAlignment.CENTER else PdfHorizontalAlignment.LEFT,
                        verticalAlignment = PdfVerticalAlignment.CENTER,
                        horizontalPaddingPt = 4f,
                    ),
                )
            }
        }

        heading(
            stream,
            primitives,
            PdfRect(36f, 154f, 540f, 30f),
            "Objetivo del artefacto: aprobar/rechazar primitivas por parámetro antes de remapear páginas completas.",
            PdfTypographyRole.BODY,
            8.5f,
        )

        stream.saveGraphicsState()
        stream.setStrokingColor(Color.BLACK)
        stream.setLineWidth(0.25f)
        stream.moveTo(36f, 142f)
        stream.lineTo(576f, 142f)
        stream.stroke()
        stream.restoreGraphicsState()

        heading(
            stream,
            primitives,
            PdfRect(36f, 112f, 540f, 24f),
            "CHECK QA: mapping / X-Y / familia / tamaño / wrapping / marker / spacing / overflow / consistencia",
            PdfTypographyRole.COMPACT_TABLE,
            7.6f,
        )
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
