package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * V11-only bundled typefaces. Independent of the frozen legacy PDF typography theme.
 * Filenames are asset-relative on Android and classpath-relative on Desktop.
 * This is a technical typography selection; runtime visual acceptance is still required.
 */
enum class PcSheetV11Typeface(val assetPath: String) {
    // Owner 2026-10-29 «se agregan las fuentes»: the exact golden V11 files.
    LABEL("fonts/pdf/text/NotoSansDisplay-CondensedBold.ttf"),
    BODY("fonts/pdf/text/NotoSans-Regular.ttf"),
    BODY_BOLD("fonts/pdf/text/NotoSans-Bold.ttf"),
    /** Golden names and titles; it lacks accented letters, see [HERO_FALLBACK]. */
    HERO("fonts/pdf/text/EBGaramond12-Bold.ttf"),
    /** Same EB Garamond project, used only for characters [HERO] cannot draw. */
    HERO_FALLBACK("fonts/pdf/text/EBGaramond-Bold.ttf"),
}

/**
 * Font metrics from the actual PDF backend and its embedded font, never a synthetic
 * character-width approximation. Descent is a positive distance below baseline.
 */
data class PcSheetV11FontMetricsPt(
    val fontSizePt: Float,
    val ascentPt: Float,
    val descentPt: Float,
    val leadingPt: Float,
) {
    init {
        require(fontSizePt.isFinite() && fontSizePt > 0f)
        require(ascentPt.isFinite() && ascentPt > 0f)
        require(descentPt.isFinite() && descentPt > 0f)
        require(leadingPt.isFinite() && leadingPt >= 0f)
        require(lineHeightPt.isFinite() && lineHeightPt > 0f)
    }

    val lineHeightPt: Float get() = ascentPt + descentPt + leadingPt

    /** Offset from each reserved PDF-line cell bottom to the glyph baseline. */
    val baselineOffsetPt: Float get() = descentPt + leadingPt / 2f

    fun baselineIn(cell: PcSheetV11RectPt): Float {
        require(cell.heightPt + 0.001f >= lineHeightPt) {
            "Text line cell is smaller than the actual font metrics."
        }
        return cell.bottomPt + baselineOffsetPt + (cell.heightPt - lineHeightPt) / 2f
    }
}

/**
 * A concrete typeface measured by the PDF backend, passed to the existing shared placer.
 * The font and width function originate from the SAME loaded font instance.
 */
data class PcSheetV11ResolvedFont(
    val typeface: PcSheetV11Typeface,
    val metrics: PcSheetV11FontMetricsPt,
    val width: PcSheetV11TextWidth,
) {
    fun place(
        section: PcSheetV11FlowSection,
        items: List<PcSheetV11FlowItem>,
        areaPt: PcSheetV11RectPt,
        columnsPerPage: Int,
        gutterPt: Float,
    ): PcSheetV11PlacedFlow =
        PcSheetV11MeasuredFlowPlacer.place(
            section = section,
            items = items,
            areaPt = areaPt,
            columnsPerPage = columnsPerPage,
            gutterPt = gutterPt,
            lineHeightPt = metrics.lineHeightPt,
            textWidth = width,
        )
}

/**
 * Matches the PDFBox font-width unit convention, used on both platforms.
 * Width units are taken from the concrete PDType0Font, not computed from character counts.
 */
fun pcSheetV11FontWidthPt(pdfWidthUnits: Float, fontSizePt: Float): Float {
    require(pdfWidthUnits.isFinite() && pdfWidthUnits >= 0f)
    require(fontSizePt.isFinite() && fontSizePt > 0f)
    val width = pdfWidthUnits / 1000f * fontSizePt
    require(width.isFinite() && width >= 0f)
    return width
}
