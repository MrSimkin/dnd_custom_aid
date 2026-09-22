package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBaseLayoutMode
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetCustomStatisticsPresentation
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExtendedPageKind
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import java.awt.Color
import java.io.InputStream
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode
import org.apache.pdfbox.text.PDFTextStripper

/**
 * Finishes the two owner-facing D-0074 visual contracts that deliberately sit outside the frozen
 * family renderers:
 *
 * 1. originating-section continuation cues when a real matching Extended page exists; and
 * 2. APP_MODIFIED_SHEET presentation of custom statistics.
 *
 * Frozen family renderers remain the visual source of truth. App Modified reuses the already
 * approved family-native Custom Statistics page grammar, relocates a copy into the base-sheet
 * sequence immediately after MAIN, and relabels that copy as a modified-sheet page. The combined
 * mode keeps the original Extended copy as well.
 */
internal class DesktopPcSheetVisualCompletionRenderer(
    private val document: PDDocument,
    private val resourceLoader: (String) -> InputStream? = { path ->
        DesktopPcSheetVisualCompletionRenderer::class.java.classLoader.getResourceAsStream(path)
    },
) {
    private val primitives by lazy {
        DesktopPdfRenderingPrimitives(
            DesktopPdfFontRegistry(document, resourceLoader = resourceLoader),
        )
    }

    fun apply(
        requestedPlan: PcSheetPdfRenderPlan,
        renderedPlan: PcSheetPdfRenderPlan,
        sourceBytes: ByteArray,
    ) {
        val basePageCount = renderedPlan.basePages.size
        require(document.numberOfPages >= basePageCount) {
            "Rendered PC sheet has fewer pages than its canonical base-page plan."
        }

        val extendedKinds = detectExtendedKinds(basePageCount).toMutableSet()
        val statsPageIndexes = customStatisticsPageIndexes(basePageCount)

        if (
            requestedPlan.baseLayoutMode == PcSheetBaseLayoutMode.APP_MODIFIED &&
            requestedPlan.request.customStatisticsPresentation ==
            PcSheetCustomStatisticsPresentation.APP_MODIFIED_SHEET
        ) {
            extendedKinds.remove(PcSheetExtendedPageKind.CUSTOM_STATISTICS)
        }

        // Paint cues while base-page indexes still match the canonical family plan. App Modified
        // pages are inserted only afterwards, so moving them cannot redirect a cue to the wrong page.
        drawContinuationCues(requestedPlan, extendedKinds)

        if (requestedPlan.baseLayoutMode == PcSheetBaseLayoutMode.APP_MODIFIED) {
            require(statsPageIndexes.isNotEmpty()) {
                "App Modified Sheet requires a rendered family-native Custom Statistics page."
            }

            drawAppModifiedOriginCue(requestedPlan)
            val modifiedPages = when (requestedPlan.request.customStatisticsPresentation) {
                PcSheetCustomStatisticsPresentation.APP_MODIFIED_SHEET ->
                    movePagesAfterMain(statsPageIndexes)
                PcSheetCustomStatisticsPresentation.MODIFIED_SHEET_AND_COMPLETE_EXTENDED_PAGE ->
                    duplicatePagesAfterMain(statsPageIndexes, sourceBytes)
                PcSheetCustomStatisticsPresentation.EXTENDED_PAGE ->
                    error("APP_MODIFIED layout cannot use EXTENDED_PAGE-only presentation.")
            }
            modifiedPages.forEachIndexed { index, page ->
                relabelAsModifiedSheet(
                    page = page,
                    family = requestedPlan.request.visualFamily,
                    index = index + 1,
                    total = modifiedPages.size,
                )
            }
        }

    }

    private fun detectExtendedKinds(basePageCount: Int): Set<PcSheetExtendedPageKind> {
        val kinds = mutableSetOf<PcSheetExtendedPageKind>()
        val layerNames = document.documentCatalog.ocProperties
            ?.getGroupNames()
            ?.map { it.uppercase() }
            .orEmpty()

        fun hasLayer(prefix: String): Boolean = layerNames.any { it.startsWith(prefix) }

        if (hasLayer("V1X STATS") || hasLayer("V2X ATTR") || hasLayer("V2X ABILITY")) {
            kinds += PcSheetExtendedPageKind.CUSTOM_STATISTICS
        }
        if (hasLayer("V1X TRAITS") || hasLayer("V2X TRAITS")) {
            kinds += PcSheetExtendedPageKind.TRAITS_AND_FEATURES
        }
        if (hasLayer("V1X RESOURCES") || hasLayer("V2X RESOURCES")) {
            kinds += PcSheetExtendedPageKind.RESOURCES_AND_OPTIONS
        }
        if (hasLayer("V1X INVENTORY") || hasLayer("V2X INVENTORY")) {
            kinds += PcSheetExtendedPageKind.INVENTORY_AND_EQUIPMENT
        }
        if (hasLayer("V1X SPELLS") || hasLayer("V2X SPELLS")) {
            kinds += PcSheetExtendedPageKind.SPELLS
        }
        if (hasLayer("V1X NOTES") || hasLayer("V2X NOTES")) {
            kinds += PcSheetExtendedPageKind.NOTES
        }

        for (pageIndex in basePageCount until document.numberOfPages) {
            val text = normalized(pageText(pageIndex))
            if ("ESTADISTICAS PERSONALIZADAS" in text) {
                kinds += PcSheetExtendedPageKind.CUSTOM_STATISTICS
            }
            if ("RASGOS Y CARACTERISTICAS" in text || "RASGOS Y ATRIBUTOS" in text) {
                kinds += PcSheetExtendedPageKind.TRAITS_AND_FEATURES
            }
            if ("RECURSOS Y OPCIONES" in text) {
                kinds += PcSheetExtendedPageKind.RESOURCES_AND_OPTIONS
            }
            if ("INVENTARIO / EQUIPO" in text || "EQUIPO - CONTINUACION" in text) {
                kinds += PcSheetExtendedPageKind.INVENTORY_AND_EQUIPMENT
            }
            if ("CONJUROS" in text) {
                kinds += PcSheetExtendedPageKind.SPELLS
            }
            if (
                "NOTAS DE CAMPANA" in text ||
                "EXTENSION / NOTAS" in text ||
                text.lineSequence().any { it.trim() == "NOTAS" }
            ) {
                kinds += PcSheetExtendedPageKind.NOTES
            }
        }
        return kinds
    }

    private fun customStatisticsPageIndexes(basePageCount: Int): List<Int> =
        (basePageCount until document.numberOfPages).filter { pageIndex ->
            "ESTADISTICAS PERSONALIZADAS" in normalized(pageText(pageIndex))
        }

    private fun pageText(pageIndex: Int): String =
        PDFTextStripper().apply {
            startPage = pageIndex + 1
            endPage = pageIndex + 1
        }.getText(document)

    private fun normalized(raw: String): String =
        raw.uppercase()
            .replace('Á', 'A')
            .replace('É', 'E')
            .replace('Í', 'I')
            .replace('Ó', 'O')
            .replace('Ú', 'U')
            .replace('Ü', 'U')

    private fun movePagesAfterMain(pageIndexes: List<Int>): List<PDPage> {
        val pages = pageIndexes.map(document::getPage)
        insertAfterMain(pages)
        return pages
    }

    private fun duplicatePagesAfterMain(
        pageIndexes: List<Int>,
        sourceBytes: ByteArray,
    ): List<PDPage> {
        val imported = Loader.loadPDF(sourceBytes).use { source ->
            pageIndexes.map { pageIndex ->
                document.importPage(source.getPage(pageIndex))
            }
        }
        insertAfterMain(imported)
        return imported
    }

    private fun insertAfterMain(pages: List<PDPage>) {
        if (pages.isEmpty()) return
        val anchor = document.getPage(1)
        pages.forEach { page ->
            document.pages.remove(page)
            document.pages.insertBefore(page, anchor)
        }
    }

    private fun drawAppModifiedOriginCue(plan: PcSheetPdfRenderPlan) {
        val main = document.getPage(0)
        val rect = when (plan.request.visualFamily) {
            PcSheetVisualFamily.CLASSIC_DND_STYLE -> PdfRect(28f, 72f, 224f, 19f)
            PcSheetVisualFamily.CUSTOM_V1 -> PdfRect(28f, 42f, 268f, 19f)
            PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
            PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY,
            -> PdfRect(28f, 42f, 268f, 19f)
        }
        drawCue(
            main,
            rect,
            "ESTADÍSTICAS PERSONALIZADAS - HOJA MODIFICADA SIGUIENTE",
            emphasized = true,
        )
    }

    private fun drawContinuationCues(
        plan: PcSheetPdfRenderPlan,
        kinds: Set<PcSheetExtendedPageKind>,
    ) {
        kinds.sortedBy { it.ordinal }.forEach { kind ->
            cuePlacements(plan.request.visualFamily, kind).forEach { placement ->
                if (placement.pageIndex < document.numberOfPages) {
                    drawCue(
                        document.getPage(placement.pageIndex),
                        placement.rect,
                        "${kindLabel(kind)} - CONTINÚA EN EXTENSIÓN",
                    )
                }
            }
        }
    }

    private fun cuePlacements(
        family: PcSheetVisualFamily,
        kind: PcSheetExtendedPageKind,
    ): List<CuePlacement> = when (family) {
        PcSheetVisualFamily.CLASSIC_DND_STYLE -> when (kind) {
            PcSheetExtendedPageKind.CUSTOM_STATISTICS ->
                listOf(CuePlacement(0, PdfRect(28f, 96f, 220f, 17f)))
            PcSheetExtendedPageKind.TRAITS_AND_FEATURES ->
                listOf(CuePlacement(0, PdfRect(372f, 202f, 204f, 17f)))
            PcSheetExtendedPageKind.RESOURCES_AND_OPTIONS ->
                listOf(CuePlacement(0, PdfRect(372f, 548f, 204f, 17f)))
            PcSheetExtendedPageKind.INVENTORY_AND_EQUIPMENT ->
                listOf(CuePlacement(1, PdfRect(372f, 376f, 204f, 17f)))
            PcSheetExtendedPageKind.SPELLS ->
                listOf(CuePlacement(2, PdfRect(372f, 48f, 204f, 17f)))
            PcSheetExtendedPageKind.NOTES ->
                listOf(CuePlacement(1, PdfRect(28f, 78f, 212f, 17f)))
        }

        PcSheetVisualFamily.CUSTOM_V1 -> when (kind) {
            PcSheetExtendedPageKind.CUSTOM_STATISTICS ->
                listOf(CuePlacement(0, PdfRect(28f, 66f, 248f, 17f)))
            PcSheetExtendedPageKind.TRAITS_AND_FEATURES ->
                listOf(
                    CuePlacement(0, PdfRect(338f, 66f, 246f, 17f)),
                    CuePlacement(2, PdfRect(338f, 44f, 246f, 17f)),
                )
            PcSheetExtendedPageKind.RESOURCES_AND_OPTIONS ->
                listOf(CuePlacement(0, PdfRect(338f, 44f, 246f, 17f)))
            PcSheetExtendedPageKind.INVENTORY_AND_EQUIPMENT ->
                listOf(CuePlacement(1, PdfRect(338f, 44f, 246f, 17f)))
            PcSheetExtendedPageKind.SPELLS ->
                listOf(CuePlacement(3, PdfRect(338f, 44f, 246f, 17f)))
            PcSheetExtendedPageKind.NOTES ->
                listOf(CuePlacement(4, PdfRect(338f, 44f, 246f, 17f)))
        }

        PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
        PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY,
        -> when (kind) {
            PcSheetExtendedPageKind.CUSTOM_STATISTICS ->
                listOf(CuePlacement(0, PdfRect(28f, 66f, 248f, 17f)))
            PcSheetExtendedPageKind.TRAITS_AND_FEATURES ->
                // The frozen v2 trait block ends immediately above the lower spell/treasure band.
                // Keep the cue at that boundary instead of borrowing space from OTROS.
                listOf(CuePlacement(0, PdfRect(338f, 205f, 246f, 17f)))
            PcSheetExtendedPageKind.RESOURCES_AND_OPTIONS ->
                listOf(CuePlacement(0, PdfRect(338f, 44f, 246f, 17f)))
            PcSheetExtendedPageKind.INVENTORY_AND_EQUIPMENT ->
                listOf(CuePlacement(1, PdfRect(338f, 44f, 246f, 17f)))
            PcSheetExtendedPageKind.SPELLS ->
                listOf(CuePlacement(2, PdfRect(338f, 44f, 246f, 17f)))
            PcSheetExtendedPageKind.NOTES ->
                listOf(CuePlacement(3, PdfRect(338f, 44f, 246f, 17f)))
        }
    }

    private fun kindLabel(kind: PcSheetExtendedPageKind): String = when (kind) {
        PcSheetExtendedPageKind.CUSTOM_STATISTICS -> "ESTADÍSTICAS"
        PcSheetExtendedPageKind.TRAITS_AND_FEATURES -> "RASGOS"
        PcSheetExtendedPageKind.RESOURCES_AND_OPTIONS -> "RECURSOS"
        PcSheetExtendedPageKind.INVENTORY_AND_EQUIPMENT -> "INVENTARIO"
        PcSheetExtendedPageKind.SPELLS -> "CONJUROS"
        PcSheetExtendedPageKind.NOTES -> "NOTAS"
    }

    private fun relabelAsModifiedSheet(
        page: PDPage,
        family: PcSheetVisualFamily,
        index: Int,
        total: Int,
    ) {
        val label = buildString {
            append("HOJA MODIFICADA - ESTADÍSTICAS PERSONALIZADAS")
            if (total > 1) append(" ").append(index).append("/").append(total)
        }
        // Cover the complete source footer, not only its center. Classic Extended pages carry both
        // an EXTENSIÓN label and a historical page number there; neither belongs on a promoted
        // App-Modified base page.
        val footerCover = PdfRect(24f, 8f, 564f, 34f)
        val footerLabel = PdfRect(24f, 10f, 564f, 18f)
        PDPageContentStream(document, page, AppendMode.APPEND, true, true).use { stream ->
            stream.saveGraphicsState()
            stream.setNonStrokingColor(Color.WHITE)
            stream.addRect(
                footerCover.x,
                footerCover.y,
                footerCover.width,
                footerCover.height,
            )
            stream.fill()

            if (family == PcSheetVisualFamily.CLASSIC_DND_STYLE) {
                // Classic also identifies continuation pages in the upper-right header. Replace
                // only that small tag while retaining the approved title, frame and geometry.
                val headerTag = PdfRect(500f, 742f, 78f, 18f)
                stream.addRect(headerTag.x, headerTag.y, headerTag.width, headerTag.height)
                stream.fill()
            }
            stream.restoreGraphicsState()

            if (family == PcSheetVisualFamily.CLASSIC_DND_STYLE) {
                primitives.drawTextBox(
                    stream,
                    PdfTextBoxSpec(
                        rect = PdfRect(500f, 742f, 78f, 18f),
                        text = "MODIFICADA",
                        role = PdfTypographyRole.OPTIONAL_DECORATIVE,
                        preferredSizePt = 7.2f,
                        minimumSizePt = 6.2f,
                        horizontalAlignment = PdfHorizontalAlignment.RIGHT,
                        verticalAlignment = PdfVerticalAlignment.CENTER,
                        wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                        maximumLines = 1,
                        horizontalPaddingPt = 2f,
                        verticalPaddingPt = 1f,
                    ),
                )
            }

            primitives.drawTextBox(
                stream,
                PdfTextBoxSpec(
                    rect = footerLabel,
                    text = label,
                    role = PdfTypographyRole.BODY,
                    preferredSizePt = 7.2f,
                    minimumSizePt = 6.2f,
                    horizontalAlignment = PdfHorizontalAlignment.CENTER,
                    verticalAlignment = PdfVerticalAlignment.CENTER,
                    wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                    maximumLines = 1,
                    horizontalPaddingPt = 3f,
                    verticalPaddingPt = 1f,
                ),
            )
        }
    }

private fun drawCue(
        page: PDPage,
        rect: PdfRect,
        text: String,
        emphasized: Boolean = false,
    ) {
        // Owner correction 2026-09-22: continuation hints must behave like marginal annotations,
        // not opaque UI boxes pasted over a paper sheet. Never erase the frozen sheet beneath them.
        PDPageContentStream(document, page, AppendMode.APPEND, true, true).use { stream ->
            primitives.drawTextBox(
                stream,
                PdfTextBoxSpec(
                    rect = rect,
                    text = text,
                    role = if (emphasized) {
                        PdfTypographyRole.OPTIONAL_DECORATIVE
                    } else {
                        PdfTypographyRole.BODY
                    },
                    preferredSizePt = if (emphasized) 6.2f else 5.8f,
                    minimumSizePt = 5.2f,
                    horizontalAlignment = PdfHorizontalAlignment.RIGHT,
                    verticalAlignment = PdfVerticalAlignment.CENTER,
                    wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                    maximumLines = 1,
                    horizontalPaddingPt = 2f,
                    verticalPaddingPt = 1f,
                ),
            )
        }
    }

    private data class CuePlacement(
        val pageIndex: Int,
        val rect: PdfRect,
    )
}
