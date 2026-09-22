package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPortraitFitMode
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject

/**
 * Platform-owned portrait byte -> PDF overlay boundary.
 *
 * The shared render plan deliberately carries only the stable portrait reference, local-availability
 * fact and selected fit mode. Desktop resolves local bytes at export time and overlays them into the
 * already-approved family frame without changing the canonical character model or requiring network
 * access. Invalid/unreadable local bytes degrade to a blank portrait rather than blocking export.
 */
internal class DesktopPortraitRenderer(
    private val document: PDDocument,
) {
    private val primitives = DesktopPdfRenderingPrimitives(DesktopPdfFontRegistry(document))

    fun overlay(
        plan: PcSheetPdfRenderPlan,
        bytes: ByteArray,
    ): Boolean {
        val target = targetFor(plan.request.visualFamily)
        if (target.pageIndex !in 0 until document.numberOfPages) return false

        val image = runCatching {
            PDImageXObject.createFromByteArray(document, bytes, "pc-portrait")
        }.getOrNull() ?: return false

        val page = document.getPage(target.pageIndex)
        PDPageContentStream(document, page, AppendMode.APPEND, true, true).use { stream ->
            stream.saveGraphicsState()
            target.clipPolygon?.let { polygon ->
                val first = polygon.first()
                stream.moveTo(first.x, first.y)
                polygon.drop(1).forEach { point -> stream.lineTo(point.x, point.y) }
                stream.closePath()
                stream.clip()
            }

            primitives.drawImage(
                stream = stream,
                image = image,
                rect = target.rect,
                mode = when (plan.snapshot.portrait.fitMode) {
                    PcSheetPortraitFitMode.CROP_TO_FILL -> PdfImageFitMode.CROP_FILL
                    PcSheetPortraitFitMode.FIT_ENTIRE_IMAGE -> PdfImageFitMode.FIT_ENTIRE
                },
            )
            stream.restoreGraphicsState()
        }
        return true
    }

    private fun targetFor(family: PcSheetVisualFamily): PortraitTarget = when (family) {
        PcSheetVisualFamily.CLASSIC_DND_STYLE -> PortraitTarget(
            pageIndex = 1,
            rect = rectFromTop(39f, 139f, 196f, 144f),
        )

        PcSheetVisualFamily.CUSTOM_V1 -> PortraitTarget(
            pageIndex = 0,
            rect = rectFromTop(457f, 31f, 128f, 131f),
        )

        PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE,
        PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY,
        -> {
            val polygonTopCoordinates = listOf(
                TopPoint(222f, 29f),
                TopPoint(310f, 29f),
                TopPoint(321f, 36f),
                TopPoint(347f, 111f),
                TopPoint(340f, 116f),
                TopPoint(190f, 116f),
                TopPoint(184f, 111f),
                TopPoint(210f, 36f),
            )
            PortraitTarget(
                pageIndex = 0,
                rect = rectFromTop(184f, 29f, 163f, 87f),
                clipPolygon = polygonTopCoordinates.map(::toPdfPoint),
            )
        }
    }

    private fun rectFromTop(
        x: Float,
        top: Float,
        width: Float,
        height: Float,
    ): PdfRect = PdfRect(
        x = x,
        y = PAGE_HEIGHT - top - height,
        width = width,
        height = height,
    )

    private fun toPdfPoint(point: TopPoint): PdfPoint =
        PdfPoint(point.x, PAGE_HEIGHT - point.top)

    private data class PortraitTarget(
        val pageIndex: Int,
        val rect: PdfRect,
        val clipPolygon: List<PdfPoint>? = null,
    )

    private data class TopPoint(
        val x: Float,
        val top: Float,
    )

    private data class PdfPoint(
        val x: Float,
        val y: Float,
    )

    private companion object {
        const val PAGE_HEIGHT = 792f
    }
}
