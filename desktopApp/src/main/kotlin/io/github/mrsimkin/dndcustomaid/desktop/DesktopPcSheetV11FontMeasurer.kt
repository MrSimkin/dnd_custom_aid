package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FontMetricsPt
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ResolvedFont
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TextWidth
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Typeface
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetV11FontWidthPt
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetV11PrintableText
import java.io.InputStream
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDType0Font

/**
 * Isolated V11 PDFBox font measurement. Fonts are loaded from the SAME packaged TTF
 * that a future renderer must draw with, and scoped to the supplied PDF document.
 * Does not alter legacy PDF primitives or their typography theme.
 *
 * A missing font, missing ascent/descent, invalid glyph or unknown measurement
 * must fail visibly. No silent substitution or character-count approximation.
 */
internal class DesktopPcSheetV11FontMeasurer(
    private val document: PDDocument,
    private val resourceLoader: (String) -> InputStream? = { path -> DesktopPcSheetV11FontMeasurer::class.java.classLoader.getResourceAsStream(path) },
) {
    private val loaded = mutableMapOf<PcSheetV11Typeface, PDType0Font>()

    /** Same loaded PDF font that measured the shared V11 layout. */
    fun font(typeface: PcSheetV11Typeface): PDType0Font =
        checkNotNull(loaded[typeface]) { "Resolve this V11 font before drawing it." }

    fun resolve(
        typeface: PcSheetV11Typeface,
        fontSizePt: Float,
        lineHeightMultiplier: Float = 1.15f,
    ): PcSheetV11ResolvedFont {
        require(fontSizePt.isFinite() && fontSizePt > 0f) { "V11 font size must be finite and positive." }
        require(lineHeightMultiplier.isFinite() && lineHeightMultiplier >= 1f) {
            "V11 line spacing must not clip the embedded font."
        }
        val font = loaded.getOrPut(typeface) {
            val stream = resourceLoader(typeface.assetPath)
                ?: error("Missing V11 packaged font: ${typeface.assetPath}")
            stream.use { PDType0Font.load(document, it, true) }
        }
        val descriptor = requireNotNull(font.fontDescriptor) {
            "Embedded V11 typeface ${typeface.assetPath} has no metrics."
        }
        val ascent = descriptor.ascent / 1000f * fontSizePt
        val descent = -descriptor.descent / 1000f * fontSizePt
        require(ascent.isFinite() && ascent > 0f && descent.isFinite() && descent > 0f) {
            "Invalid embedded V11 font ascent/descent for ${typeface.assetPath}"
        }
        val metrics = PcSheetV11FontMetricsPt(
            fontSizePt = fontSizePt,
            ascentPt = ascent,
            descentPt = descent,
            leadingPt = (ascent + descent) * (lineHeightMultiplier - 1f),
        )
        return PcSheetV11ResolvedFont(
            typeface = typeface,
            metrics = metrics,
            width = PcSheetV11TextWidth { text ->
                if (text.isEmpty()) 0f
                else pcSheetV11FontWidthPt(font.getStringWidth(pcSheetV11PrintableText(text)), fontSizePt)
            },
        )
    }
}
