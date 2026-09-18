package io.github.mrsimkin.dndcustomaid.desktop

import java.awt.Color
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject

/**
 * Reusable PDF rendering primitives for PC-sheet export.
 *
 * This layer deliberately owns geometry/metrics mechanics only. Family-specific page mappings and
 * product semantics remain elsewhere. The bundled font mappings below are QA candidates, not
 * owner-approved typography for Custom v1/v2/Classic.
 */
internal enum class PdfTypographyRole {
    CHARACTER_NAME,
    HANDWRITTEN_NAME,
    PRIMARY_VALUE,
    SECONDARY_VALUE,
    BODY,
    COMPACT_TABLE,
    NUMERIC_COMPACT,
    NOTE_TEXT,
    SPELL_NAME,
    OPTIONAL_DECORATIVE,
}

internal data class PdfTypographyTheme(
    val id: String,
    val resourcesByRole: Map<PdfTypographyRole, String>,
) {
    init {
        require(PdfTypographyRole.entries.all { it in resourcesByRole }) {
            "Every PDF typography role must resolve to an explicit font resource."
        }
    }

    fun resourcePath(role: PdfTypographyRole): String = requireNotNull(resourcesByRole[role])
}

internal val PRIMITIVE_QA_TYPOGRAPHY_THEME = PdfTypographyTheme(
    id = "primitive-qa-owner-directed-candidates-v2",
    resourcesByRole = mapOf(
        PdfTypographyRole.CHARACTER_NAME to "fonts/pdf/text/FiraSans-SemiBold.ttf",
        PdfTypographyRole.HANDWRITTEN_NAME to "fonts/pdf/text/Kalam-Bold.ttf",
        PdfTypographyRole.PRIMARY_VALUE to "fonts/pdf/text/FiraSans-SemiBold.ttf",
        PdfTypographyRole.SECONDARY_VALUE to "fonts/pdf/text/FiraSans-Regular.ttf",
        PdfTypographyRole.BODY to "fonts/pdf/text/FiraSans-Regular.ttf",
        PdfTypographyRole.COMPACT_TABLE to "fonts/pdf/text/BarlowCondensed-Bold.ttf",
        PdfTypographyRole.NUMERIC_COMPACT to "fonts/pdf/text/BarlowCondensed-Bold.ttf",
        PdfTypographyRole.NOTE_TEXT to "fonts/pdf/text/FiraSans-Regular.ttf",
        PdfTypographyRole.SPELL_NAME to "fonts/pdf/text/FiraSans-SemiBold.ttf",
        PdfTypographyRole.OPTIONAL_DECORATIVE to "fonts/pdf/text/BarlowCondensed-Bold.ttf",
    ),
)

internal class DesktopPdfFontRegistry(
    private val document: PDDocument,
    private val theme: PdfTypographyTheme = PRIMITIVE_QA_TYPOGRAPHY_THEME,
    private val resourceLoader: (String) -> InputStream? = { path ->
        DesktopPdfFontRegistry::class.java.classLoader.getResourceAsStream(path)
    },
) {
    private val loadedFonts = mutableMapOf<String, PDFont>()

    fun font(role: PdfTypographyRole): PDFont {
        val path = theme.resourcePath(role)
        return loadedFonts.getOrPut(path) { loadRequired(path) }
    }

    private fun loadRequired(path: String): PDFont {
        val input = resourceLoader(path) ?: error("Required PDF font resource is unavailable: $path")
        return input.use { PDType0Font.load(document, it, true) }
    }
}

internal data class PdfRect(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
) {
    init {
        require(width >= 0f)
        require(height >= 0f)
    }

    val right: Float get() = x + width
    val top: Float get() = y + height
}

internal enum class PdfHorizontalAlignment {
    LEFT,
    CENTER,
    RIGHT,
}

internal enum class PdfVerticalAlignment {
    TOP,
    CENTER,
    BOTTOM,
}

internal enum class PdfWrapPolicy {
    SINGLE_LINE,
    WORD_WRAP,
}

internal enum class PdfFontSizeMode {
    ADAPTIVE_TO_FIT,
    FIXED,
}

internal data class PdfTextBoxSpec(
    val rect: PdfRect,
    val text: String,
    val role: PdfTypographyRole,
    val preferredSizePt: Float,
    val minimumSizePt: Float,
    val fontSizeMode: PdfFontSizeMode = PdfFontSizeMode.ADAPTIVE_TO_FIT,
    val horizontalAlignment: PdfHorizontalAlignment = PdfHorizontalAlignment.LEFT,
    val verticalAlignment: PdfVerticalAlignment = PdfVerticalAlignment.CENTER,
    val wrapPolicy: PdfWrapPolicy = PdfWrapPolicy.SINGLE_LINE,
    val maximumLines: Int = Int.MAX_VALUE,
    val horizontalPaddingPt: Float = 2f,
    val verticalPaddingPt: Float = 1f,
    val lineHeightMultiplier: Float = 1.12f,
) {
    init {
        require(preferredSizePt > 0f)
        require(minimumSizePt > 0f)
        require(preferredSizePt >= minimumSizePt)
        if (fontSizeMode == PdfFontSizeMode.FIXED) {
            require(kotlin.math.abs(preferredSizePt - minimumSizePt) < 0.001f) {
                "FIXED font-size boxes must use the same preferred and minimum size."
            }
        }
        require(maximumLines > 0)
        require(horizontalPaddingPt >= 0f)
        require(verticalPaddingPt >= 0f)
        require(lineHeightMultiplier >= 1f)
    }
}

internal data class PdfTextLayoutResult(
    val renderedLines: List<String>,
    val overflowText: String?,
    val fontSizePt: Float,
    val lineAdvancePt: Float,
) {
    val hasOverflow: Boolean get() = !overflowText.isNullOrBlank()
}

internal enum class PdfMarkerKind {
    CIRCLE_OUTLINE,
    CIRCLE_FILLED,
    DOUBLE_CIRCLE,
    SQUARE_OUTLINE,
    SQUARE_FILLED,
    CHECK,
    DOUBLE_CHECK,
    CROSS,
    DIAMOND_OUTLINE,
    DIAMOND_FILLED,
}

internal enum class PdfImageFitMode {
    FIT_ENTIRE,
    CROP_FILL,
}

internal data class PdfImagePlacementResult(
    val drawX: Float,
    val drawY: Float,
    val drawWidth: Float,
    val drawHeight: Float,
    val cropped: Boolean,
)

internal class DesktopPdfRenderingPrimitives(
    private val fonts: DesktopPdfFontRegistry,
) {
    fun drawTextBox(
        stream: PDPageContentStream,
        spec: PdfTextBoxSpec,
    ): PdfTextLayoutResult {
        val cleaned = spec.text.replace("\r\n", "\n").trim()
        if (cleaned.isEmpty()) {
            return PdfTextLayoutResult(emptyList(), null, spec.preferredSizePt, spec.preferredSizePt)
        }

        val font = fonts.font(spec.role)
        val availableWidth = max(0f, spec.rect.width - spec.horizontalPaddingPt * 2f)
        val availableHeight = max(0f, spec.rect.height - spec.verticalPaddingPt * 2f)
        val layout = fitLayout(font, cleaned, spec, availableWidth, availableHeight)
        if (layout.renderedLines.isEmpty()) return layout

        val metrics = metrics(font, layout.fontSizePt)
        val lineAdvance = layout.lineAdvancePt
        val blockHeight = metrics.height + (layout.renderedLines.size - 1).coerceAtLeast(0) * lineAdvance
        val contentBottom = spec.rect.y + spec.verticalPaddingPt
        val contentTop = spec.rect.top - spec.verticalPaddingPt
        val blockBottom = when (spec.verticalAlignment) {
            PdfVerticalAlignment.TOP -> contentTop - blockHeight
            PdfVerticalAlignment.CENTER -> contentBottom + (availableHeight - blockHeight) / 2f
            PdfVerticalAlignment.BOTTOM -> contentBottom
        }
        val firstBaseline = blockBottom + blockHeight - metrics.ascent

        stream.saveGraphicsState()
        stream.setNonStrokingColor(Color.BLACK)
        layout.renderedLines.forEachIndexed { index, line ->
            if (line.isEmpty()) return@forEachIndexed
            val lineWidth = textWidth(font, line, layout.fontSizePt)
            val x = when (spec.horizontalAlignment) {
                PdfHorizontalAlignment.LEFT -> spec.rect.x + spec.horizontalPaddingPt
                PdfHorizontalAlignment.CENTER -> spec.rect.x + (spec.rect.width - lineWidth) / 2f
                PdfHorizontalAlignment.RIGHT -> spec.rect.right - spec.horizontalPaddingPt - lineWidth
            }
            val baseline = firstBaseline - index * lineAdvance
            stream.beginText()
            stream.setFont(font, layout.fontSizePt)
            stream.newLineAtOffset(x, baseline)
            stream.showText(line)
            stream.endText()
        }
        stream.restoreGraphicsState()
        return layout
    }

    fun drawMarker(
        stream: PDPageContentStream,
        centerX: Float,
        centerY: Float,
        sizePt: Float,
        kind: PdfMarkerKind,
        lineWidthPt: Float = 0.8f,
    ) {
        require(sizePt > 0f)
        require(lineWidthPt > 0f)
        val half = sizePt / 2f

        stream.saveGraphicsState()
        stream.setStrokingColor(Color.BLACK)
        stream.setNonStrokingColor(Color.BLACK)
        stream.setLineWidth(lineWidthPt)
        when (kind) {
            PdfMarkerKind.CIRCLE_OUTLINE -> circle(stream, centerX, centerY, half, filled = false)
            PdfMarkerKind.CIRCLE_FILLED -> circle(stream, centerX, centerY, half, filled = true)
            PdfMarkerKind.DOUBLE_CIRCLE -> {
                circle(stream, centerX, centerY, half, filled = false)
                circle(stream, centerX, centerY, half * 0.58f, filled = false)
            }
            PdfMarkerKind.SQUARE_OUTLINE -> {
                stream.addRect(centerX - half, centerY - half, sizePt, sizePt)
                stream.stroke()
            }
            PdfMarkerKind.SQUARE_FILLED -> {
                stream.addRect(centerX - half, centerY - half, sizePt, sizePt)
                stream.fill()
            }
            PdfMarkerKind.CHECK -> {
                appTrainingCheck(stream, centerX, centerY, sizePt, 0f)
            }
            PdfMarkerKind.DOUBLE_CHECK -> {
                appTrainingCheck(stream, centerX, centerY, sizePt, -0.12f)
                appTrainingCheck(stream, centerX, centerY, sizePt, 0.12f)
            }
            PdfMarkerKind.CROSS -> {
                stream.moveTo(centerX - half * 0.68f, centerY - half * 0.68f)
                stream.lineTo(centerX + half * 0.68f, centerY + half * 0.68f)
                stream.moveTo(centerX - half * 0.68f, centerY + half * 0.68f)
                stream.lineTo(centerX + half * 0.68f, centerY - half * 0.68f)
                stream.stroke()
            }
            PdfMarkerKind.DIAMOND_OUTLINE,
            PdfMarkerKind.DIAMOND_FILLED,
            -> {
                stream.moveTo(centerX, centerY + half)
                stream.lineTo(centerX + half, centerY)
                stream.lineTo(centerX, centerY - half)
                stream.lineTo(centerX - half, centerY)
                stream.closePath()
                if (kind == PdfMarkerKind.DIAMOND_FILLED) stream.fill() else stream.stroke()
            }
        }
        stream.restoreGraphicsState()
    }

    fun drawImage(
        stream: PDPageContentStream,
        image: PDImageXObject,
        rect: PdfRect,
        mode: PdfImageFitMode,
    ): PdfImagePlacementResult {
        require(rect.width > 0f)
        require(rect.height > 0f)
        require(image.width > 0)
        require(image.height > 0)

        val scaleX = rect.width / image.width.toFloat()
        val scaleY = rect.height / image.height.toFloat()
        val scale = when (mode) {
            PdfImageFitMode.FIT_ENTIRE -> min(scaleX, scaleY)
            PdfImageFitMode.CROP_FILL -> max(scaleX, scaleY)
        }
        val drawWidth = image.width * scale
        val drawHeight = image.height * scale
        val drawX = rect.x + (rect.width - drawWidth) / 2f
        val drawY = rect.y + (rect.height - drawHeight) / 2f

        stream.saveGraphicsState()
        if (mode == PdfImageFitMode.CROP_FILL) {
            stream.addRect(rect.x, rect.y, rect.width, rect.height)
            stream.clip()
        }
        stream.drawImage(image, drawX, drawY, drawWidth, drawHeight)
        stream.restoreGraphicsState()

        return PdfImagePlacementResult(
            drawX = drawX,
            drawY = drawY,
            drawWidth = drawWidth,
            drawHeight = drawHeight,
            cropped = mode == PdfImageFitMode.CROP_FILL,
        )
    }

    fun drawGrid(
        stream: PDPageContentStream,
        rect: PdfRect,
        columns: Int,
        rows: Int,
        lineWidthPt: Float = 0.5f,
    ) {
        require(columns > 0)
        require(rows > 0)
        stream.saveGraphicsState()
        stream.setStrokingColor(Color.BLACK)
        stream.setLineWidth(lineWidthPt)
        stream.addRect(rect.x, rect.y, rect.width, rect.height)
        stream.stroke()

        val columnWidth = rect.width / columns
        for (column in 1 until columns) {
            val x = rect.x + column * columnWidth
            stream.moveTo(x, rect.y)
            stream.lineTo(x, rect.top)
            stream.stroke()
        }
        val rowHeight = rect.height / rows
        for (row in 1 until rows) {
            val y = rect.y + row * rowHeight
            stream.moveTo(rect.x, y)
            stream.lineTo(rect.right, y)
            stream.stroke()
        }
        stream.restoreGraphicsState()
    }

    private fun fitLayout(
        font: PDFont,
        text: String,
        spec: PdfTextBoxSpec,
        availableWidth: Float,
        availableHeight: Float,
    ): PdfTextLayoutResult {
        if (availableWidth <= 0f || availableHeight <= 0f) {
            return PdfTextLayoutResult(emptyList(), text, spec.minimumSizePt, spec.minimumSizePt)
        }

        val minimumCandidateSize = if (spec.fontSizeMode == PdfFontSizeMode.FIXED) {
            spec.preferredSizePt
        } else {
            spec.minimumSizePt
        }
        var size = spec.preferredSizePt
        while (size + SIZE_EPSILON >= minimumCandidateSize) {
            val candidateSize = max(minimumCandidateSize, size)
            val allLines = wrap(font, text, candidateSize, availableWidth, spec.wrapPolicy)
            val lineAdvance = metrics(font, candidateSize).height * spec.lineHeightMultiplier
            val capacity = lineCapacity(font, candidateSize, lineAdvance, availableHeight, spec.maximumLines)
            val widthFits = allLines.all { line -> textWidth(font, line, candidateSize) <= availableWidth + WIDTH_EPSILON }
            if (widthFits && allLines.size <= capacity) {
                return PdfTextLayoutResult(allLines, null, candidateSize, lineAdvance)
            }
            if (candidateSize <= minimumCandidateSize + SIZE_EPSILON) break
            size -= SIZE_STEP_PT
        }

        val finalSize = minimumCandidateSize
        val allLines = wrap(font, text, finalSize, availableWidth, spec.wrapPolicy)
        val lineAdvance = metrics(font, finalSize).height * spec.lineHeightMultiplier
        val capacity = lineCapacity(font, finalSize, lineAdvance, availableHeight, spec.maximumLines)
        if (capacity <= 0) {
            return PdfTextLayoutResult(emptyList(), text, finalSize, lineAdvance)
        }

        if (spec.wrapPolicy == PdfWrapPolicy.SINGLE_LINE &&
            allLines.any { line -> textWidth(font, line, finalSize) > availableWidth + WIDTH_EPSILON }
        ) {
            return PdfTextLayoutResult(emptyList(), text, finalSize, lineAdvance)
        }

        val visible = allLines.take(capacity)
        val overflow = allLines.drop(capacity).joinToString("\n").takeIf { it.isNotBlank() }
        return PdfTextLayoutResult(visible, overflow, finalSize, lineAdvance)
    }

    private fun wrap(
        font: PDFont,
        text: String,
        fontSizePt: Float,
        maxWidthPt: Float,
        policy: PdfWrapPolicy,
    ): List<String> {
        if (policy == PdfWrapPolicy.SINGLE_LINE) {
            return listOf(text.replace(Regex("\\s+"), " ").trim())
        }

        val output = mutableListOf<String>()
        text.split("\n").forEach { rawParagraph ->
            val paragraph = rawParagraph.trim()
            if (paragraph.isEmpty()) {
                output += ""
                return@forEach
            }
            val words = paragraph.split(Regex("\\s+"))
            var current = ""
            words.forEach { word ->
                val pieces = splitLongToken(font, word, fontSizePt, maxWidthPt)
                pieces.forEach { piece ->
                    val candidate = if (current.isEmpty()) piece else "$current $piece"
                    if (textWidth(font, candidate, fontSizePt) <= maxWidthPt + WIDTH_EPSILON) {
                        current = candidate
                    } else {
                        if (current.isNotEmpty()) output += current
                        current = piece
                    }
                }
            }
            if (current.isNotEmpty()) output += current
        }
        return output.ifEmpty { listOf("") }
    }

    private fun splitLongToken(
        font: PDFont,
        token: String,
        fontSizePt: Float,
        maxWidthPt: Float,
    ): List<String> {
        if (textWidth(font, token, fontSizePt) <= maxWidthPt + WIDTH_EPSILON) return listOf(token)
        val pieces = mutableListOf<String>()
        var current = ""
        token.forEach { char ->
            val candidate = current + char
            if (current.isNotEmpty() && textWidth(font, candidate, fontSizePt) > maxWidthPt + WIDTH_EPSILON) {
                pieces += current
                current = char.toString()
            } else {
                current = candidate
            }
        }
        if (current.isNotEmpty()) pieces += current
        return pieces
    }

    private fun lineCapacity(
        font: PDFont,
        fontSizePt: Float,
        lineAdvancePt: Float,
        availableHeight: Float,
        configuredMaximum: Int,
    ): Int {
        val metrics = metrics(font, fontSizePt)
        if (availableHeight + HEIGHT_EPSILON < metrics.height) return 0
        val byHeight = 1 + floor((availableHeight - metrics.height) / lineAdvancePt).toInt().coerceAtLeast(0)
        return min(configuredMaximum, byHeight)
    }

    private fun metrics(font: PDFont, fontSizePt: Float): FontMetrics {
        val descriptor = font.fontDescriptor
        val ascentUnits = descriptor?.ascent?.takeIf { it > 0f } ?: DEFAULT_ASCENT_UNITS
        val descentUnits = descriptor?.descent?.takeIf { it < 0f } ?: DEFAULT_DESCENT_UNITS
        val ascent = ascentUnits / 1000f * fontSizePt
        val descent = abs(descentUnits) / 1000f * fontSizePt
        return FontMetrics(ascent = ascent, descent = descent)
    }

    private fun textWidth(font: PDFont, text: String, fontSizePt: Float): Float =
        if (text.isEmpty()) 0f else font.getStringWidth(text) / 1000f * fontSizePt

    private fun appTrainingCheck(
        stream: PDPageContentStream,
        centerX: Float,
        centerY: Float,
        sizePt: Float,
        offsetY: Float,
    ) {
        val left = centerX - sizePt / 2f
        val bottom = centerY - sizePt / 2f
        fun x(fraction: Float): Float = left + sizePt * fraction
        fun yFromTop(fraction: Float): Float = bottom + sizePt * (1f - fraction)

        stream.moveTo(x(0.12f), yFromTop(0.48f + offsetY))
        stream.lineTo(x(0.40f), yFromTop(0.72f + offsetY))
        stream.lineTo(x(0.88f), yFromTop(0.22f + offsetY))
        stream.stroke()
    }

    private fun circle(
        stream: PDPageContentStream,
        centerX: Float,
        centerY: Float,
        radius: Float,
        filled: Boolean,
    ) {
        val k = radius * CIRCLE_KAPPA
        stream.moveTo(centerX + radius, centerY)
        stream.curveTo(centerX + radius, centerY + k, centerX + k, centerY + radius, centerX, centerY + radius)
        stream.curveTo(centerX - k, centerY + radius, centerX - radius, centerY + k, centerX - radius, centerY)
        stream.curveTo(centerX - radius, centerY - k, centerX - k, centerY - radius, centerX, centerY - radius)
        stream.curveTo(centerX + k, centerY - radius, centerX + radius, centerY - k, centerX + radius, centerY)
        stream.closePath()
        if (filled) stream.fill() else stream.stroke()
    }

    private data class FontMetrics(
        val ascent: Float,
        val descent: Float,
    ) {
        val height: Float get() = ascent + descent
    }

    private companion object {
        const val SIZE_STEP_PT = 0.25f
        const val SIZE_EPSILON = 0.001f
        const val WIDTH_EPSILON = 0.05f
        const val HEIGHT_EPSILON = 0.05f
        const val DEFAULT_ASCENT_UNITS = 800f
        const val DEFAULT_DESCENT_UNITS = -200f
        const val CIRCLE_KAPPA = 0.5522848f
    }
}
