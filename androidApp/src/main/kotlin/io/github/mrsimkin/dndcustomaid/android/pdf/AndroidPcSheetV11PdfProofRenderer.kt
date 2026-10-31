package io.github.mrsimkin.dndcustomaid.android.pdf

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ContinuationText
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowItem
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableComposer
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedAmountRouting
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTablePlaced
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11MeasuredFlowPlacer
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TextFlow
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TextColumn
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11FlowSourceMapper
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ColumnGrid
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11CompactFieldRouter
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11RectPt
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ResolvedFont
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SemanticData
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Typeface
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility
import io.github.mrsimkin.dndcustomaid.shared.character.pcSheetV11PrintableText
import java.io.OutputStream
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject
import com.tom_roush.harmony.awt.AWTColor as Color

/**
 * FIRST actual app-code V11 PDF output path: a bounded INTERNAL proof renderer.
 * Never expose as a production/owner-approved V11 layout: the full P1/P2/P3/C2/CN3
 * visual composition, marks, logo, paper rules and portrait are not yet implemented.
 * This PDF is deliberately stamped as an internal layout test.
 *
 * G3 acceptance is separate from proving that local Android/Desktop code writes PDF bytes.
 * The four existing exported sheet families continue to use their original renderers.
 */
internal class AndroidPcSheetV11PdfProofRenderer {
    fun render(
        data: PcSheetV11SemanticData,
        target: OutputStream,
        portraitBytes: ByteArray? = null,
    ) {
        PDDocument().use { pdf ->
            val measurer = AndroidPcSheetV11FontMeasurer(pdf)
            val body = measurer.resolve(PcSheetV11Typeface.BODY, 9f)
            measurer.resolve(PcSheetV11Typeface.LABEL, 12f)
            val bodyPdfFont = measurer.font(PcSheetV11Typeface.BODY)
            val headerPdfFont = measurer.font(PcSheetV11Typeface.LABEL)
            // C285 (golden): names in EB Garamond 12 Bold. A character that font has
            // no glyph for (accents, dashes) is drawn from the later EB Garamond Bold
            // of the same project, so no letter is ever dropped.
            measurer.resolve(PcSheetV11Typeface.HERO, 12f)
            measurer.resolve(PcSheetV11Typeface.BODY_BOLD, 9f)
            val boldPdfFont = measurer.font(PcSheetV11Typeface.BODY_BOLD)
            measurer.resolve(PcSheetV11Typeface.HERO_FALLBACK, 12f)
            val heroPdfFonts = listOf(measurer.font(PcSheetV11Typeface.HERO),
                measurer.font(PcSheetV11Typeface.HERO_FALLBACK))
            // C325: real sheet states use the owner's Para Hoja de PJ Symbols v8 through
            // the semantic PUA aliases of its frozen GUIDE.md. Ink boxes are the frozen
            // v8 outline bounds (2048 units/em; the outlines start at x=0, their glyf
            // header xMin is stale) so each mark is centred in its paper box.
            measurer.resolve(PcSheetV11Typeface.SYMBOLS, 7f)
            val symbolPdfFont = measurer.font(PcSheetV11Typeface.SYMBOLS)
            val v8InkBoxes = mapOf(
                0xE108 to floatArrayOf(0f, 422f, 1042f, 1296f), // PROFICIENT_CHECK
                0xE109 to floatArrayOf(0f, 299f, 829f, 1354f), // EXPERTISE_DOUBLE_CHECK
                0xE10E to floatArrayOf(0f, 218f, 1370f, 1520f), // INSPIRATION_ACTIVE
                0xE10F to floatArrayOf(0f, 80f, 1440f, 1520f), // ATTUNED_ACTIVE
            )
            fun drawStateGlyph(
                stream: PDPageContentStream, codePoint: Int, boxX: Float, boxY: Float,
                box: Float, ink: Color = Color(38, 75, 104),
            ) {
                val ink0 = requireNotNull(v8InkBoxes[codePoint]) { "Unmapped v8 state glyph" }
                val inkW = ink0[2] - ink0[0]
                val inkH = ink0[3] - ink0[1]
                val pt = (box - 1.6f) * 2048f / maxOf(inkW, inkH)
                val unit = pt / 2048f
                stream.saveGraphicsState()
                stream.setNonStrokingColor(ink)
                stream.beginText()
                stream.setFont(symbolPdfFont, pt)
                stream.newLineAtOffset(boxX + (box - inkW * unit) / 2f - ink0[0] * unit,
                    boxY + (box - inkH * unit) / 2f - ink0[1] * unit)
                stream.showText(String(Character.toChars(codePoint)))
                stream.endText()
                stream.restoreGraphicsState()
            }
            fun trainingGlyph(training: io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining) =
                when (training) {
                    io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.NONE -> null
                    io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.PROFICIENT -> 0xE108
                    io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.EXPERTISE -> 0xE109
                }
            fun heroRuns(text: String): List<Pair<Int, String>> {
                val runs = mutableListOf<Pair<Int, StringBuilder>>()
                var i = 0
                while (i < text.length) {
                    val glyph = String(Character.toChars(text.codePointAt(i)))
                    val fontIndex = if (runCatching { heroPdfFonts[0].encode(glyph) }.isSuccess) 0 else 1
                    if (runs.isNotEmpty() && runs.last().first == fontIndex) runs.last().second.append(glyph)
                    else runs += fontIndex to StringBuilder(glyph)
                    i += glyph.length
                }
                return runs.map { it.first to it.second.toString() }
            }
            fun heroWidthPt(text: String, sizePt: Float): Float =
                heroRuns(pcSheetV11PrintableText(text)).sumOf { (fontIndex, run) ->
                    (heroPdfFonts[fontIndex].getStringWidth(run) * sizePt / 1000f).toDouble()
                }.toFloat()
            fun drawHero(stream: PDPageContentStream, text: String, x: Float, y: Float, sizePt: Float) {
                var cursor = x
                heroRuns(pcSheetV11PrintableText(text)).forEach { (fontIndex, run) ->
                    stream.beginText()
                    stream.setFont(heroPdfFonts[fontIndex], sizePt)
                    stream.newLineAtOffset(cursor, y)
                    stream.showText(run)
                    stream.endText()
                    cursor += heroPdfFonts[fontIndex].getStringWidth(run) * sizePt / 1000f
                }
            }
            val source = data.source.sheet
            val headerNameRoute =
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11HeaderNameRouter.route(
                    sourceName = source.name,
                    availableWidthPt = 398.43f, // C286: golden name x161.57..560.
                    // C285: measured in the golden name face and size (Garamond 13.4 pt).
                    width = io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TextWidth {
                        heroWidthPt(it, 13.4f)
                    },
                )
            val texts = PcSheetV11FlowSourceMapper.from(data)
            // C314: titled notes get one more paper row after the title; the title is
            // drawn 6.47 pt lower, so title-to-text is the golden 16.73 pt.
            val titledNoteKeys = data.digitalNotes.filter {
                it.sourceId != null && !it.title.isNullOrBlank() && it.content.isNotEmpty()
            }.map { "digital-note:" + it.sourceId }.toSet()
            val pageArea = PcSheetV11RectPt(42f, 76f, 528f, 626f)
            // Physical page roles are registered exactly when a PDFBox page is
            // created. Footers and C2 backlinks are resolved only when the final
            // document length is known: no section-local page guesses.
            val physicalSectionKeys = mutableListOf<String>()
            // A mixed C2 can now carry real P2 trait fragments alongside P1
            // sources. Track actual physical pages; never infer a P2 backlink
            // from a generic C2 heading or a section-local ordinal.
            val mixedC2TraitPages = mutableSetOf<Int>()
            // P1 must link to mixed C2 ONLY when that physical section
            // actually contains P1-owned source lines, not P2-only traits.
            val mixedC2P1Pages = mutableSetOf<Int>()
            // C246: first physical page of each mixed-C2 module heading.
            val mixedC2HeadingPages = mutableMapOf<String, Int>()
            // C258: first physical page of each continued P3 profile field.
            val p3FieldHeadingPages = mutableMapOf<String, Int>()
            // C247: custom attributes drawn as the golden C2 attribute row.
            val c2AttributeCards = mutableSetOf<String>()
            // C259: C2 action table rows (per reserved line: three column texts),
            // resource paper tracks (track line offset, spent, total) and the
            // real P/E training letter of each linked custom skill box.
            val c2ActionRows = mutableMapOf<String, List<List<String>>>()
            // Golden ATAQUES-grammar columns inside a mixed C2 column (C323: golden 261.36 pt).
            val c2ActionColumn = (532.63f - 9.92f) / 2f
            val c2ActionX = listOf(0f, 104f, 158f)
            val c2ActionWidths = listOf(100f, 50f, c2ActionColumn - 158f - 4f)
            val c2ResourceTracks = mutableMapOf<String, Triple<Int, Int, Int>>()
            val c2SkillTraining = mutableMapOf<String, String>()
            // C240: spellbook card heading length and inline NIVEL label per card.
            val bookHeadingEnds = mutableMapOf<String, Int>()
            val bookInlineLevels = mutableMapOf<String, String>()
            // Q14b: source offset and uppercase text of each card's real school line.
            val bookSchools = mutableMapOf<String, Pair<Int, String>>()

            fun draw(stream: PDPageContentStream, text: String, x: Float, y: Float, label: Boolean) {
                if (text.isEmpty()) return
                // C291: golden value ink #24394b for body text; labels keep their ink.
                if (!label) {
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(36, 57, 75))
                }
                stream.beginText()
                stream.setFont(if (label) headerPdfFont else bodyPdfFont, if (label) 12f else 9f)
                stream.newLineAtOffset(x, y)
                stream.showText(pcSheetV11PrintableText(text))
                stream.endText()
                if (!label) stream.restoreGraphicsState()
            }

            // C232 step 2: the approved golden alternates two blue-mist tones on every
            // ruled row (#E7F2F7 / #F0F7FA) with a thin line at the bottom of each row.
            fun fillBand(stream: PDPageContentStream, x: Float, y: Float, w: Float, h: Float, index: Int) {
                stream.saveGraphicsState()
                stream.setNonStrokingColor(if (index % 2 == 0) Color(231, 242, 247) else Color(240, 247, 250))
                stream.addRect(x, y, w, h)
                stream.fill()
                stream.setStrokingColor(Color(148, 176, 193))
                stream.setLineWidth(0.32f)
                stream.moveTo(x, y + 0.2f)
                stream.lineTo(x + w, y + 0.2f)
                stream.stroke()
                stream.restoreGraphicsState()
            }

            // C232: in the approved golden the P1 field LABELS are small (about 6-7pt)
            // and the VALUES carry the visual weight; the old 12pt labels inverted it.
            fun drawP1Label(stream: PDPageContentStream, text: String, x: Float, y: Float, pt: Float = 7f) {
                if (text.isEmpty()) return
                stream.beginText()
                stream.setFont(headerPdfFont, pt)
                stream.newLineAtOffset(x, y)
                stream.showText(pcSheetV11PrintableText(text))
                stream.endText()
            }
            // C264: golden P1 figures and labels in Barlow at the measured golden
            // size and ink (values #1d384f, labels #657d90).
            fun p1Ink(stream: PDPageContentStream, text: String, x: Float, y: Float,
                      pt: Float, label: Boolean = false) {
                if (text.isEmpty()) return
                stream.saveGraphicsState()
                stream.setNonStrokingColor(if (label) Color(101, 125, 144) else Color(29, 56, 79))
                drawP1Label(stream, text, x, y, pt)
                stream.restoreGraphicsState()
            }
            fun p1InkWidth(text: String, pt: Float): Float =
                headerPdfFont.getStringWidth(pcSheetV11PrintableText(text)) * pt / 1000f
            // C300: the golden prints attribute numbers in Noto Sans Bold.
            fun p1BoldWidth(text: String, pt: Float): Float =
                boldPdfFont.getStringWidth(pcSheetV11PrintableText(text)) * pt / 1000f
            fun p1Bold(stream: PDPageContentStream, text: String, x: Float, y: Float,
                       pt: Float, ink: Color) {
                if (text.isEmpty()) return
                stream.saveGraphicsState()
                stream.setNonStrokingColor(ink)
                stream.beginText()
                stream.setFont(boldPdfFont, pt)
                stream.newLineAtOffset(x, y)
                stream.showText(pcSheetV11PrintableText(text))
                stream.endText()
                stream.restoreGraphicsState()
            }
            // C265: golden cartela diamonds, one at each end of a title band;
            // titles start 16 pt inside the band, clear of the left diamond.
            fun drawCartelaDiamonds(stream: PDPageContentStream, left: Float, right: Float,
                                    bottom: Float, top: Float) {
                val h = minOf(6.2f, top - bottom - 4f)
                val w = h * 0.57f
                val mid = (bottom + top) / 2f
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(82, 124, 150))
                stream.setNonStrokingColor(Color.WHITE)
                stream.setLineWidth(0.48f)
                listOf(left + 6f, right - 6f).forEach { cx ->
                    stream.moveTo(cx, mid + h / 2f)
                    stream.lineTo(cx + w / 2f, mid)
                    stream.lineTo(cx, mid - h / 2f)
                    stream.lineTo(cx - w / 2f, mid)
                    stream.closePath()
                    stream.fillAndStroke()
                }
                stream.restoreGraphicsState()
            }
            // C295: golden C2 cartela — diamonds at both ends, title 16 pt in, and
            // "VIENE P. n" before the right diamond; a longer title keeps 5 pt.
            // C334: a long title steps down (9.1 to 7.1 pt) to keep the golden
            // cartela - diamonds, 16 pt inset and its origin - before falling back.
            // C341: the origin reads «VIENE DE PÁG. n» (owner 2026-10-30).
            fun c2CartelaTitlePt(text: String, widthPt: Float): Float? =
                listOf(9.1f, 8.6f, 8.1f, 7.6f, 7.1f).firstOrNull { pt ->
                    16f + headerPdfFont.getStringWidth(pcSheetV11PrintableText(text)) * pt / 1000f +
                        8f + headerPdfFont.getStringWidth(
                            pcSheetV11PrintableText(PcSheetV11ContinuationText.comesFrom(99))
                        ) * 6f / 1000f + 13f <= widthPt
                }
            fun c2CartelaGoldenFits(text: String, widthPt: Float): Boolean =
                c2CartelaTitlePt(text, widthPt) != null
            // Owner's original D&D sheet includes its genuine logotype in its top-left.
            // Embed that real graphic from the already-versioned asset; clip to LOGO only,
            // so no original-sheet boxes or lettering leak into this V11 page.
            val originalSheetPixels = requireNotNull(AndroidPcSheetAssetLoader.open(
                "character-sheets/templates/renders/original/page-1.png",
            )?.use { it.readBytes() }) {
                "The approved owner-logo source image is missing; never substitute text."
            }
            val ownerLogo = PDImageXObject.createFromByteArray(
                pdf, originalSheetPixels, "owner-dnd-logo-original",
            )
            require(ownerLogo.width == 1224 && ownerLogo.height == 1584) {
                "Unexpected original-logo raster dimensions; refuse an incorrect crop."
            }

            fun drawFrameAndHeader(
                stream: PDPageContentStream,
                title: String,
                lastHandwrittenPage: Boolean = false,
            ) {
                stream.saveGraphicsState()
                // Exterior V11 double frame: exact real PDF vectors from ALL
                // EIGHT immutable approved US-Letter golden documents. These
                // are stable on every physical page (including C2 and NOTAS).
                // PyMuPDF round-trip bounds: outer x26.362..585.638,
                // y24.945..767.055; inner x30.614..581.386,
                // y28.913..763.087. Preserve all content placements.
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.95f)
                stream.addRect(26.3622f, 24.9449f, 559.2756f, 742.1102f)
                stream.stroke()
                stream.setStrokingColor(Color(191, 211, 222))
                stream.setLineWidth(0.38f)
                stream.addRect(30.6142f, 28.9134f, 550.7716f, 734.1732f)
                stream.stroke()
                // C311: golden header rule colour and width, x39.69..572.32 (the golden
                // sits it at y709.8; the app keeps 717, above its C-N3 cartela strip).
                stream.setStrokingColor(Color(82, 124, 150))
                stream.setLineWidth(0.55f)
                stream.moveTo(39.69f, 717f)
                stream.lineTo(572.32f, 717f)
                stream.stroke()
                // C337: the golden closes EVERY page (final NOTAS sheet included) with
                // this #527c96 0.55 pt foot rule; it used to exist only under the
                // P1/P2 module links. Owner 2026-10-30: no page number on the final sheet.
                stream.moveTo(39.69f, 45.07f)
                stream.lineTo(572.32f, 45.07f)
                stream.stroke()
                stream.restoreGraphicsState()

                stream.saveGraphicsState()
                // Native template coordinates: logo x≈54–398, y≈50–184 in
                // the 1224×1584 pixel original. Clip only the logo region.
                // C326 (owner 2026-10-30, «puedes usar ese o uno más pequeño»): the
                // logo stays inside the frames instead of rising over the outer frame.
                // C327 (owner: the golden shield belonged to its stand-in logo and may
                // go): the logo takes that space, x57..147 × y719.5..754.5 (90×35 pt,
                // 344×134 px at 0.2616 pt/px), clear of the corner bracket and diamond.
                val scale = 0.2616f
                stream.addRect(57f, 719.5f, 344f * scale, 134f * scale)
                stream.clip()
                // PDFBox exposes no endPath(); stroke closes the clipped path.
                stream.stroke()
                stream.drawImage(
                    ownerLogo,
                    57f - 54f * scale,
                    719.5f + 184f * scale - ownerLogo.height * scale,
                    ownerLogo.width * scale,
                    ownerLogo.height * scale,
                )
                stream.restoreGraphicsState()

                // C286 (golden header, v11_extremo): a pale rule after the logo, the
                // name, and the module group label top right under a corner bracket.
                // Exact golden vectors. C327: the golden shield is not drawn.
                stream.saveGraphicsState()
                fun goldenPath(points: List<Pair<Float, Float>>, close: Boolean) {
                    stream.moveTo(points[0].first, points[0].second)
                    points.drop(1).forEach { (x, y) -> stream.lineTo(x, y) }
                    if (close) stream.closePath()
                }
                fun goldenDiamond(cx: Float, cy: Float, halfW: Float, halfH: Float) {
                    stream.setNonStrokingColor(Color(255, 255, 255))
                    stream.setStrokingColor(Color(82, 124, 150))
                    stream.setLineWidth(0.48f)
                    goldenPath(listOf(cx to cy + halfH, cx + halfW to cy, cx to cy - halfH,
                        cx - halfW to cy), true)
                    stream.fillAndStroke()
                }
                stream.setStrokingColor(Color(191, 211, 222))
                stream.setLineWidth(0.4f)
                goldenPath(listOf(153.07f to 750.33f, 153.07f to 715.46f), false)
                stream.stroke()
                stream.setLineWidth(0.55f)
                // C290: the golden corner brackets, each with its small diamond, at
                // all four corners, and the four diamonds on the outer frame corners.
                stream.setStrokingColor(Color(191, 211, 222))
                stream.setLineWidth(0.55f)
                for ((xEdge, xInner, yEdge, yInner) in listOf(
                    listOf(574.02f, 559.84f, 753.73f, 739.56f), listOf(37.98f, 52.16f, 753.73f, 739.56f),
                    listOf(574.02f, 559.84f, 38.27f, 52.44f), listOf(37.98f, 52.16f, 38.27f, 52.44f))) {
                    stream.setStrokingColor(Color(191, 211, 222))
                    stream.setLineWidth(0.55f)
                    goldenPath(listOf(xInner to yEdge, xEdge to yEdge, xEdge to yInner), false)
                    stream.stroke()
                    goldenDiamond(if (xEdge > 300f) 558.145f else 53.855f,
                        if (yEdge > 300f) 753.45f else 38.55f, 1.375f, 2.41f)
                }
                for ((cx, cy) in listOf(30.05f to 763.655f, 30.05f to 28.345f,
                    581.95f to 763.655f, 581.95f to 28.345f)) {
                    goldenDiamond(cx, cy, 2.02f, 3.545f)
                }
                // Golden character name: EB Garamond 12 Bold 13.4 pt, ink #1d384f.
                stream.setNonStrokingColor(Color(29, 56, 79))
                drawHero(stream, headerNameRoute.paperName, 161.57f, 735.31f, 13.4f)
                // Golden module group label: 7.3 pt #264b68, right edge x569.48.
                val groupLabel = when {
                    lastHandwrittenPage -> "NOTAS"
                    title.startsWith("P1") -> "VARIANTE " + data.request.variant.name
                    title.startsWith("P2") -> "EQUIPO Y TESORO"
                    title.startsWith("P3") -> "TRASFONDO E HISTORIA"
                    title.startsWith("C2") -> "CONTINUACIONES C2"
                    title.startsWith("P4") -> "LISTA RÁPIDA DE CONJUROS"
                    title.startsWith("C-N3") -> "NOTAS C-N3"
                    title.startsWith("Libro") || title.startsWith("Índice del Libro") -> "LIBRO DE CONJUROS"
                    else -> null
                }
                stream.restoreGraphicsState()
                // C286: the page title takes the golden secondary header line (5.9 pt
                // #657d90, right edge x569.48). The last NOTAS sheet shows only its
                // group label, as the golden.
                if (!lastHandwrittenPage) {
                    val printableTitle = pcSheetV11PrintableText(title)
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(101, 125, 144))
                    stream.beginText()
                    stream.setFont(headerPdfFont, 5.9f)
                    stream.newLineAtOffset(569.48f -
                        headerPdfFont.getStringWidth(printableTitle) * 5.9f / 1000f, 724.5f)
                    stream.showText(printableTitle)
                    stream.endText()
                    stream.restoreGraphicsState()
                }
                if (!lastHandwrittenPage) {
                    // C290: the internal-proof mark takes the golden footer note line
                    // (Noto Sans 6.1 pt #657d90 at x42.52).
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(101, 125, 144))
                    stream.beginText()
                    stream.setFont(bodyPdfFont, 6.1f)
                    stream.newLineAtOffset(42.52f, 32.88f)
                    stream.showText("PRUEBA INTERNA V11 - NO APROBADA")
                    stream.endText()
                    stream.restoreGraphicsState()
                }
                // Drawn last so each page's text keeps name, title, ... order.
                stream.saveGraphicsState()
                groupLabel?.let { label ->
                    stream.setNonStrokingColor(Color(38, 75, 104))
                    stream.beginText()
                    stream.setFont(headerPdfFont, 7.3f)
                    stream.newLineAtOffset(569.48f - headerPdfFont.getStringWidth(label) * 7.3f / 1000f, 745.8f)
                    stream.showText(label)
                    stream.endText()
                }
                stream.restoreGraphicsState()
                // C324: golden C2 section label above the shared-module columns.
                if (title.startsWith("C2 - Habilidades, acciones y recursos")) {
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(38, 75, 104))
                    drawP1Label(stream, "MÓDULOS COMPARTIDOS · DOS COLUMNAS", 39.69f, 691.9f, 7.5f)
                    stream.restoreGraphicsState()
                }
                // C298: the golden has no black text; every page starts in its value ink.
                stream.setNonStrokingColor(Color(36, 57, 75))
            }

            // For B only, the golden's 6+2/6+3 uses compact 25pt
            // real-attribute rows above a separate skill panel. A has its own
            // measured skill-under-ability heights and is never force-shrunk.
            val compactBAttributes = data.request.variant ==
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.B &&
                data.attributes.size > 6
            // Source-backed A skills cannot force the SIX mandatory
            // attribute rows beneath P1's approved printable floor. Preserve
            // the original geometry while all rows fit; when they do not,
            // the shared cap defers only excess source skills to mixed C2.
            val aSkillRowCaps = if (data.request.variant ==
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A
            ) {
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11AttributeSkillBudget
                    .plan(
                        data.attributes.take(6).map { attribute ->
                            data.skills.count { it.ability == attribute.reference }
                        },
                        areaTopPt = 577f,
                        safeBottomPt = 110f,
                    ).mapIndexed { index, cap ->
                        data.attributes[index].reference to cap
                    }.toMap()
            } else emptyMap()
            fun p1AttributeHeight(ability: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Attribute): Float {
                val a=data.request.variant ==
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A
                return if(a) io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11AttributeSkillBudget
                    .heightPt(
                        aSkillRowCaps[ability.reference] ?: minOf(6,
                            data.skills.count { it.ability == ability.reference }),
                    )
                    else if(compactBAttributes) 25f else 38f
            }
            // Variant A NEVER uses B's fixed 25pt compression: a skill group
            // is only printed if the entire genuine attribute-height fits.
            // The first unfit attribute and all following ones remain in C2.
            val p1PrintedAttributeCount = when {
                compactBAttributes -> minOf(9, data.attributes.size)
                data.request.variant ==
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A -> {
                    var count = 6
                    var remainingBottom = 577f - data.attributes.take(6)
                        .sumOf { p1AttributeHeight(it).toDouble() }.toFloat()
                    while (count < minOf(9, data.attributes.size)) {
                        val extraHeight = p1AttributeHeight(data.attributes[count])
                        if (remainingBottom - extraHeight <= 110f) break
                        remainingBottom -= extraHeight
                        count++
                    }
                    count
                }
                else -> 6
            }
            fun p1AttributeTop(i:Int):Float = 577f -
                data.attributes.take(i).sumOf { p1AttributeHeight(it).toDouble() }.toFloat()

            // Standard enum IDs stay English in the model; only owner-approved
            // V11 P1 PAPER labels are translated. Custom names remain literal.
            fun approvedAbilityName(name: String): String = when (name) {
                "STRENGTH" -> "FUERZA"
                "DEXTERITY" -> "DESTREZA"
                "CONSTITUTION" -> "CONSTITUCIÓN"
                "INTELLIGENCE" -> "INTELIGENCIA"
                "WISDOM" -> "SABIDURÍA"
                "CHARISMA" -> "CARISMA"
                else -> name
            }
            // C218: the approved V11 paper displays true positive/zero
            // ability and skill modifiers with an explicit plus, as P1 does.
            // This is a display-only formatting of the persisted Int.
            fun signedC2(value: Int): String =
                if (value >= 0) "+" + value else value.toString()

            fun approvedAbilityAbbreviation(raw: String): String = when (raw.uppercase()) {
                "STR" -> "FUE"
                "DEX" -> "DES"
                "CON" -> "CON"
                "INT" -> "INT"
                "WIS" -> "SAB"
                "CHA" -> "CAR"
                else -> raw.uppercase().take(3)
            }
            fun approvedSkillName(skill:
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Skill
            ): String = when (skill.builtInKey?.name) {
                "ACROBATICS" -> "Acrobacias"
                "ANIMAL_HANDLING" -> "Trato con Animales"
                "ARCANA" -> "Conoc. Arcano"
                "ATHLETICS" -> "Atletismo"
                "DECEPTION" -> "Engaño"
                "HISTORY" -> "Historia"
                "INSIGHT" -> "Perspicacia"
                "INTIMIDATION" -> "Intimidación"
                "INVESTIGATION" -> "Investigación"
                "MEDICINE" -> "Medicina"
                "NATURE" -> "Naturaleza"
                "PERCEPTION" -> "Percepción"
                "PERFORMANCE" -> "Interpretación"
                "PERSUASION" -> "Persuasión"
                "RELIGION" -> "Religión"
                "SLEIGHT_OF_HAND" -> "Juego de Manos"
                "STEALTH" -> "Sigilo"
                "SURVIVAL" -> "Supervivencia"
                else -> skill.name
            }
            fun drawP1Attributes(stream:PDPageContentStream) {
                val standards=data.attributes.take(p1PrintedAttributeCount)
                require(standards.size>=6 && p1AttributeTop(p1PrintedAttributeCount) >
                    (if (compactBAttributes) 328f else 110f))
                // Approved V11 P1 C2c: shared A/B Attributes blue header.
                // It fits the genuine 23pt strip BELOW portrait (PDF y605)
                // and ABOVE first source-backed ability row (PDF y577).
                // Decoration cannot move any ability, save or skill row.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.55f)
                stream.moveTo(46f, 579f)
                stream.lineTo(296f, 579f)
                stream.lineTo(300f, 583f)
                stream.lineTo(300f, 598f)
                stream.lineTo(296f, 602f)
                stream.lineTo(46f, 602f)
                stream.lineTo(42f, 598f)
                stream.lineTo(42f, 583f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                // C264: golden cartela titles are 9.1 pt.
                stream.setNonStrokingColor(Color(29, 56, 79)) // C266: golden title ink
                drawCartelaDiamonds(stream, 42f, 300f, 579f, 602f)
                drawP1Label(stream,if(data.request.variant ==
                    io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A)
                    "ATRIBUTOS · A" else "ATRIBUTOS · B",58f,586f,9.1f)
                stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                val k=0.5522848f
                standards.forEachIndexed { i,ability ->
                    val top=p1AttributeTop(i)
                    val bottom=p1AttributeTop(i+1)
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(130,165,186))
                    stream.setLineWidth(0.6f)
                    stream.setNonStrokingColor(if (i % 2 == 0) Color(231,242,247) else Color(240,247,250))
                    stream.addRect(42f,bottom,258f,top-bottom)
                    stream.fill()
                    stream.addRect(42f,bottom,258f,top-bottom)
                    stream.stroke()
                    stream.addRect(46f,top-(if(compactBAttributes) 22f else 27f),
                        47f,if(compactBAttributes) 18f else 22f)
                    stream.stroke()
                    val cx=116f
                    val cy=top-(if(compactBAttributes) 13f else 16f)
                    val rx=23f
                    val ry=if(compactBAttributes) 9f else 11f
                    stream.moveTo(cx+rx,cy)
                    stream.curveTo(cx+rx,cy+ry*k,cx+rx*k,cy+ry,cx,cy+ry)
                    stream.curveTo(cx-rx*k,cy+ry,cx-rx,cy+ry*k,cx-rx,cy)
                    stream.curveTo(cx-rx,cy-ry*k,cx-rx*k,cy-ry,cx,cy-ry)
                    stream.curveTo(cx+rx*k,cy-ry,cx+rx,cy-ry*k,cx+rx,cy)
                    stream.closePath()
                    stream.stroke()
                    stream.restoreGraphicsState()
                    val score=ability.score.toString()
                    val mod=if(ability.modifier>=0)"+"+ability.modifier else ability.modifier.toString()
                    val valueY=top-(if(compactBAttributes) 17f else 20f)
                    // C264: golden score 11 pt, modifier 8.7 pt, name 7.7 pt.
                    // C300: golden Noto Sans Bold - score #1d384f, modifier #264b68.
                    p1Bold(stream,score,69.5f-p1BoldWidth(score,11f)/2f,valueY,11f,Color(29,56,79))
                    p1Bold(stream,mod,cx-p1BoldWidth(mod,8.7f)/2f,valueY,8.7f,Color(38,75,104))
                    p1Ink(stream,approvedAbilityName(ability.name),145f,
                        top-(if(compactBAttributes) 11f else 14f),7.7f)
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(101,125,144))
                    stream.beginText()
                    stream.setFont(headerPdfFont,5.8f)
                    // Golden B: label-to-box gap 17.85pt; previous real App gap 20.18pt.
                    // Move the label only 2.3pt right and 2.1pt upward relative
                    // to its attribute name; leave the 7pt box, value and row intact.
                    val saveY=top-(if(compactBAttributes) 23f else 25f)
                    // Position only the label; saveY remains the untouched
                    // checkbox/value baseline from the verified App PDF.
                    val savingLabelY=saveY+2.1f
                    stream.newLineAtOffset(217.3f,savingLabelY)
                    stream.showText("SALVACIÓN")
                    stream.endText()
                    stream.restoreGraphicsState()
                    // Approved V11 paper saving-throw mark. Always blank:
                    // proficiency/state cannot be inferred from the save total.
                    // Genuine vector 7×7pt square stays between the label
                    // (ending before x250) and its actual value (starts x287+).
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(80, 122, 151))
                    stream.setLineWidth(0.6f)
                    stream.addRect(270f, saveY - 1f, 7f, 7f)
                    stream.stroke()
                    stream.restoreGraphicsState()
                    ability.savingThrowTotal?.let {
                        val value=if(it>=0)"+"+it else it.toString()
                        p1Bold(stream,value,296f-p1BoldWidth(value,7.2f),saveY,7.2f,Color(38,75,104))
                    }
                }
            }

            // Variant A fixes built-in skills below their matching ability box.
            // Any overlong or custom skill is preserved whole in C2, with the
            // three-character ability suffix required outside its attribute.
            val variantA = data.request.variant ==
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant.A
            val skillFont = measurer.resolve(PcSheetV11Typeface.BODY, 7f).let { font ->
                // C283: Noto Sans' 7 pt box is taller than Fira's; B keeps nine skill
                // rows per subcolumn in its 95 pt panel on a 10.5 pt pitch.
                val box = font.metrics.ascentPt + font.metrics.descentPt
                font.copy(metrics = font.metrics.copy(leadingPt = maxOf(0f, 10.5f - box)))
            }
            val groupedSkills = mutableMapOf<Int, MutableList<Pair<Int, io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Skill>>>()
            val printedSkillIndices = mutableSetOf<Int>()
            if (variantA) {
                data.attributes.take(p1PrintedAttributeCount).forEachIndexed { attrIndex, attr ->
                    val fitsHere = mutableListOf<Pair<Int, io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Skill>>()
                    data.skills.forEachIndexed { skillIndex, skill ->
                        if (skill.ability == attr.reference && skillIndex !in printedSkillIndices &&
                            fitsHere.size < (aSkillRowCaps[attr.reference] ?: 6) &&
                            skill.name.isNotBlank()
                        ) {
                            val total = skill.total?.let { if (it >= 0) "+" + it else it.toString() } ?: ""
                            val remaining = 258f - 34f - skillFont.width.measurePt(total) - 9f
                            if (skillFont.width.measurePt(approvedSkillName(skill)) <= remaining) {
                                fitsHere += skillIndex to skill
                                printedSkillIndices += skillIndex
                            }
                        }
                    }
                    groupedSkills[attrIndex] = fitsHere
                }
            }

            fun drawP1GroupedSkills(stream:PDPageContentStream) {
                if(!variantA)return
                groupedSkills.forEach { (attrIndex,rows) ->
                    val top=p1AttributeTop(attrIndex)
                    rows.forEachIndexed { idx,(_,skill) ->
                        val y=top-36f-idx*10.5f
                        require(y>p1AttributeTop(attrIndex+1)+1f)
                        fillBand(stream, 43f, y - 3f, 256f, 10.5f, idx + 1)
                        stream.saveGraphicsState()
                        stream.setStrokingColor(Color(130,165,186))
                        stream.addRect(47f,y-2f,7f,7f)
                        stream.stroke()
                        stream.restoreGraphicsState()
                        fun put(value:String,x:Float){
                            if(value.isNotEmpty()){
                                stream.beginText()
                                stream.setFont(bodyPdfFont,7f)
                                stream.newLineAtOffset(x,y)
                                stream.showText(pcSheetV11PrintableText(value))
                                stream.endText()
                            }
                        }
                        // C325: v8 training grammar - one check, or two stacked checks.
                        trainingGlyph(skill.training)?.let { drawStateGlyph(stream, it, 47f, y - 2f, 7f) }
                        put(approvedSkillName(skill),60f)
                        skill.total?.let {
                            val v=if(it>=0)"+"+it else it.toString()
                            // C300: golden skill bonus, Noto Sans Bold 7 pt #264b68.
                            p1Bold(stream,v,294f-p1BoldWidth(v,7f),y,7f,Color(38,75,104))
                        }
                    }
                }
                // C232 step 3: the golden fills the unused foot of the left column with
                // blank banded manuscript rows (13pt) instead of a stray caption.
                var blankRowTop = p1AttributeTop(p1PrintedAttributeCount) - 3f
                var blankRowIndex = 0
                while (blankRowTop - 13f >= 105f) {
                    fillBand(stream, 42f, blankRowTop - 13f, 258f, 13f, blankRowIndex)
                    blankRowTop -= 13f
                    blankRowIndex += 1
                }
            }

            // Variant B has a physically separate skills area, never a prose
            // list inside the identity field. Source offsets stay attached to
            // their original skills so any measured overflow survives in C2.
            // C241: label and value suffix of each B skill, so a one-line skill
            // can print its value right-aligned in its subcolumn like the golden.
            val bSkillParts = mutableMapOf<String, Pair<String, String>>()
            // C329: B training is the v8 mark in the skill's paper box (as in A),
            // no longer a « P» / « E» suffix; an overflowing B skill keeps it in words.
            val bSkillTraining = mutableMapOf<String,
                io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining>()
            val bSkillEntries = if (!variantA) data.skills.withIndex()
                .filter { it.value.builtInKey != null }
                .map { (index, skill) ->
                    val abbreviation = data.attributes.firstOrNull {
                        it.reference == skill.ability
                    }?.abbreviation?.let { approvedAbilityAbbreviation(it) }
                    val label = approvedSkillName(skill) + (abbreviation?.let { " (" + it + ")" } ?: "")
                    val value = skill.total?.let { " " + (if (it >= 0) "+" else "") + it } ?: ""
                    bSkillParts["skill:" + index] = label to value
                    bSkillTraining["skill:" + index] = skill.training
                    PcSheetV11FlowItem(
                        "skill:" + index,
                        label + value,
                        keepTogetherIfFits = true,
                    )
                }
            else emptyList()
            // Reserve 11pt for each approved B paper skill checkbox. Both
            // physical columns are genuinely measured: x60..167 and x186..293
            // (19pt between them houses the separator and second checkbox).
            // This never adds a fake skill or clips an overlong source label.
            val bSkillsArea = PcSheetV11RectPt(60f,220f,233f,95f)
            val bSkillsPlaced = if(!variantA) PcSheetV11MeasuredFlowPlacer.place(
                PcSheetV11FlowSection.TRAITS,bSkillEntries,bSkillsArea,2,19f,
                skillFont.metrics.lineHeightPt,skillFont.width,
            ) else null
            fun drawP1BIndependentSkills(stream: PDPageContentStream) {
                if (variantA) return
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.55f)
                stream.moveTo(46f, 330f)
                stream.lineTo(296f, 330f)
                stream.lineTo(300f, 334f)
                stream.lineTo(300f, 344f)
                stream.lineTo(296f, 348f)
                stream.lineTo(46f, 348f)
                stream.lineTo(42f, 344f)
                stream.lineTo(42f, 334f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                // C310: golden B cartela - diamonds, HABILIDADES 9.1 pt ink 16 pt in,
                // and the P/E legend as the golden grey 6.2 pt note on the right.
                drawCartelaDiamonds(stream, 42f, 300f, 330f, 348f)
                stream.setNonStrokingColor(Color(29, 56, 79))
                drawP1Label(stream, "HABILIDADES", 58f, 336f, 9.1f)
                // C329: the legend shows the v8 marks themselves.
                val skillsLegend = listOf(0xE108 to "COMPETENCIA", 0xE109 to "PERICIA")
                fun legendWidth(text: String) = bodyPdfFont.getStringWidth(text) * 6.2f / 1000f
                var legendX = 300f - 18.4f - skillsLegend.sumOf { (_, text) ->
                    (6f + 1.5f + legendWidth(text)).toDouble() }.toFloat() - 6f
                stream.setNonStrokingColor(Color(101, 125, 144))
                skillsLegend.forEachIndexed { i, (glyph, text) ->
                    drawStateGlyph(stream, glyph, legendX, 336.2f, 6.4f, Color(101, 125, 144))
                    legendX += 7.5f
                    stream.beginText()
                    stream.setFont(bodyPdfFont, 6.2f)
                    stream.newLineAtOffset(legendX, 337.2f)
                    stream.showText(text)
                    stream.endText()
                    legendX += legendWidth(text) + if (i == 0) 6f else 0f
                }
                stream.setNonStrokingColor(Color(36, 57, 75))
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130,165,186))
                stream.setLineWidth(0.6f)
                stream.addRect(42f,105f,258f,223f)
                stream.stroke()
                stream.moveTo(171f,105f)
                stream.lineTo(171f,319f)
                stream.stroke()
                stream.restoreGraphicsState()
                // The approved B skills cartela remains useful on paper
                // BELOW all real skill records. Its layout placer is confined
                // to y=220..315; this lower region y=125..218 cannot erase,
                // crowd or duplicate any source skill even with long entries.
                // Two separate ruled columns mirror the frozen golden; no
                // unchecked state or digital NOTAS record is invented.
                val blankPaperTop = bSkillsArea.bottomPt - 15f
                val blankPaperFloor = 105f + 20f
                val blankPaperStep = 16f
                var ruleY = blankPaperTop
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130,165,186))
                stream.setLineWidth(0.45f)
                stream.setNonStrokingColor(Color(238,247,251))
                while (ruleY >= blankPaperFloor) {
                    for ((xStart,xEnd) in listOf(47f to 167f,176f to 295f)) {
                        // Alternate pale-blue usable handwriting bands, never
                        // outside the skills frame or across its divider.
                        stream.addRect(xStart,ruleY-1f,xEnd-xStart,14f)
                        stream.fill()
                        stream.moveTo(xStart,ruleY)
                        stream.lineTo(xEnd,ruleY)
                        stream.stroke()
                    }
                    ruleY -= blankPaperStep
                }
                stream.restoreGraphicsState()
                bSkillsPlaced?.cells?.filter{it.localPageIndex==1}?.forEach{cell->
                    if(cell.drawableText.isNotEmpty()){
                        val baseline = skillFont.metrics.baselineIn(cell.cellPt)
                        val bandRow = Math.round((bSkillsArea.topPt - cell.cellPt.topPt) /
                            skillFont.metrics.lineHeightPt)
                        val bandLeft = if (cell.cellPt.leftPt < 170f) 47f else 176f
                        val bandWidth = if (cell.cellPt.leftPt < 170f) 120f else 119f
                        fillBand(stream, bandLeft, cell.cellPt.bottomPt, bandWidth,
                            cell.cellPt.heightPt, bandRow)
                        if (cell.sourceStartOffset == 0) {
                            // One genuine blank 7×7pt square per SOURCE skill,
                            // never one per wrapped fragment. P/E text is only
                            // a source-backed training label, not a checked state.
                            stream.saveGraphicsState()
                            stream.setStrokingColor(Color(130,165,186))
                            stream.setLineWidth(0.6f)
                            stream.addRect(cell.cellPt.leftPt - 11f,
                                baseline - 2f, 7f, 7f)
                            stream.stroke()
                            stream.restoreGraphicsState()
                            bSkillTraining[cell.sourceKey]?.let(::trainingGlyph)?.let {
                                drawStateGlyph(stream, it, cell.cellPt.leftPt - 11f, baseline - 2f, 7f)
                            }
                        }
                        fun ink(text: String, x: Float) {
                            stream.beginText()
                            stream.setFont(bodyPdfFont,7f)
                            stream.newLineAtOffset(x, baseline)
                            stream.showText(pcSheetV11PrintableText(text))
                            stream.endText()
                        }
                        // C241: golden B — value right-aligned 3 pt inside the band.
                        val parts = bSkillParts[cell.sourceKey]
                        val value = parts?.second?.trim().orEmpty()
                        val valueRight = bandLeft + bandWidth - 3f
                        val oneLine = parts != null && value.isNotEmpty() &&
                            cell.sourceStartOffset == 0 &&
                            cell.sourceEndExclusive == parts.first.length + parts.second.length
                        if (oneLine && cell.cellPt.leftPt + skillFont.width.measurePt(parts!!.first) +
                            4f <= valueRight - p1BoldWidth(value, 7f)) {
                            ink(parts.first, cell.cellPt.leftPt)
                            // C302: golden B skill bonus, Noto Sans Bold 7 pt #264b68.
                            p1Bold(stream, value, valueRight - p1BoldWidth(value, 7f), baseline,
                                7f, Color(38, 75, 104))
                        } else {
                            ink(cell.drawableText, cell.cellPt.leftPt)
                        }
                    }
                }
            }
            val bSkillOverflow = bSkillEntries.mapNotNull { item ->
                val consumed = bSkillsPlaced?.cells?.asSequence()
                    ?.filter { it.localPageIndex == 1 && it.sourceKey == item.sourceKey }
                    ?.maxOfOrNull { it.sourceEndExclusive } ?: 0
                require(consumed in 0..item.text.length)
                if (consumed == item.text.length) null
                else item.copy(text = item.text.substring(consumed) +
                    when (bSkillTraining[item.sourceKey]) {
                        io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.PROFICIENT -> " / Competencia"
                        io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.EXPERTISE -> " / Pericia"
                        else -> ""
                    })
            }

            // Custom-attribute skills belong directly to their persisted
            // attribute in mixed C2, in either A or B. Other detached skills
            // retain their conventional three-letter reference.
            val customAttributeReferences = data.attributes.drop(6)
                .map { it.reference }.toSet()
            // C259 (owner 2026-10-28): the sheet is Spanish; internal enum
            // names (PROFICIENT, ACTION...) are never printed.
            fun spanishTraining(training: io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining) =
                when (training) {
                    io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.NONE -> "Sin competencia"
                    io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.PROFICIENT -> "Competencia"
                    io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.EXPERTISE -> "Pericia"
                }
            val detachedSkills = if (variantA) data.skills.withIndex()
                .filter { it.index !in printedSkillIndices &&
                    it.value.ability !in customAttributeReferences }
                .map { (index, skill) ->
                    val abbreviation = data.attributes.firstOrNull {
                        it.reference == skill.ability
                    }?.abbreviation?.let { approvedAbilityAbbreviation(it) }
                    PcSheetV11FlowItem(
                        "detached-skill:" + index,
                        approvedSkillName(skill) + (abbreviation?.let { " (" + it + ")" } ?: "") +
                            " / " + (skill.total?.toString() ?: "sin resolver") +
                            " / " + spanishTraining(skill.training),
                    )
                }
            else emptyList()

            // P1 mandatory mechanics: no invented values for absent spell slots.
            // Real attacks receive three fields (attack/range, narrow bonus, damage).
            // Rows whose actual measured text will not fit move intact into C2.
            val attackRows = data.combatEntries.filter {
                it.type == io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ATTACK
            }
            val attackLabelWidth = 121f
            val attackBonusWidth = 36f
            val attackDamageWidth = 258f - attackLabelWidth - attackBonusWidth
            fun p1AttackLabel(entry:io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry):String {
                val complete=entry.name+(entry.rangeText?.takeIf{it.isNotBlank()}
                    ?.let{" / "+it}?:"")
                return if(body.width.measurePt(complete)<=attackLabelWidth-10f)
                    complete else entry.name
            }
            // Fill the six genuine P1 attack rows with the first SIX source-order
            // entries which physically FIT, not only matches among the first
            // six source entries. All other UUIDs remain deferred to C2.
            fun attackBonus(entry:io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry):String =
                entry.attackModifier?.let{if(it>=0)"+"+it else it.toString()}?:""
            // One physical three-column attack row (P1 and the golden C2 ATAQUES table).
            fun attackFitsTableRow(entry:io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry):Boolean {
                // P1 cells are single physical rows. A measured "width" cannot
                // prove that a source with CR/LF/TAB is safe to print there.
                // Defer the COMPLETE UUID-backed attack to measured C2 rather
                // than flattening, clipping, or dropping a source segment.
                val hasPhysicalBreak = listOfNotNull(
                    entry.name, entry.rangeText, entry.damageEffect,
                ).any { field -> field.any { ch ->
                    ch == '\r' || ch == '\n' || ch == '\t'
                } }
                return !hasPhysicalBreak &&
                    body.width.measurePt(p1AttackLabel(entry))<=attackLabelWidth-10f &&
                    body.width.measurePt(attackBonus(entry))<=attackBonusWidth-8f &&
                    body.width.measurePt(entry.damageEffect)<=attackDamageWidth-10f
            }
            val p1Attacks=attackRows.filter{ attackFitsTableRow(it) }.take(6)
            // Golden ATAQUES row values: bonus and damage right-aligned 4 pt
            // inside their columns (P1 table and C2 continuation rows).
            fun drawAttackValues(
                stream: PDPageContentStream,
                entry: io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry,
                left: Float, y: Float,
                pt: Float = 9f,
            ) {
                // C262: values follow the size of their table (P1 or golden C2).
                // C301: golden bonus in Noto Sans Bold #264b68; damage 0.4 pt smaller.
                fun value(text: String, right: Float, bold: Boolean) {
                    if (text.isEmpty()) return
                    val size = if (bold) pt else pt - 0.4f
                    val font = if (bold) boldPdfFont else bodyPdfFont
                    val printable = pcSheetV11PrintableText(text)
                    stream.saveGraphicsState()
                    if (bold) stream.setNonStrokingColor(Color(38, 75, 104))
                    stream.beginText()
                    stream.setFont(font, size)
                    stream.newLineAtOffset(right - font.getStringWidth(printable) * size / 1000f, y)
                    stream.showText(printable)
                    stream.endText()
                    stream.restoreGraphicsState()
                }
                value(attackBonus(entry), left + attackLabelWidth + attackBonusWidth - 4f, true)
                value(entry.damageEffect, left + 254f, false)
            }
            val p1AttackIds = p1Attacks.map { it.id }.toSet()

            fun drawP1Attacks(stream: PDPageContentStream) {
                val left = 312f
                val top = 491f
                val bottom = 392f
                val b1 = left + attackLabelWidth
                val b2 = b1 + attackBonusWidth
                // Frozen V11 attacks use a clipped blue heading. This narrow
                // source-neutral adaptation fills ONLY the existing 12pt
                // three-column header row (x312..570, top-origin y301..313).
                // There is no 23pt clearance for a separate title cartela;
                // never move real attacks or the P1 traits to fake one.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.55f)
                val headerBottom = top - 12f
                val cut = 3f
                stream.moveTo(left + cut, headerBottom)
                stream.lineTo(left + 258f - cut, headerBottom)
                stream.lineTo(left + 258f, headerBottom + cut)
                stream.lineTo(left + 258f, top - cut)
                stream.lineTo(left + 258f - cut, top)
                stream.lineTo(left + cut, top)
                stream.lineTo(left, top - cut)
                stream.lineTo(left, headerBottom + cut)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                // V11 approved blue-mist table grammar: alternate four VERY light
                // attack-body rows. Keep existing coordinates, table borders,
                // source-backed labels/bonuses/damage and all page breaks intact.
                // Tint only inside the original 7 x 12pt table body, not header.
                stream.saveGraphicsState()
                for (rowIndex in 0..6) {
                    val rowTop = headerBottom - rowIndex * 12f
                    fillBand(stream, left + 1f, rowTop - 12f, 256f, 12f, rowIndex)
                }
                stream.restoreGraphicsState()
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.7f)
                stream.addRect(left, bottom, 258f, top - bottom)
                stream.stroke()
                for (x in listOf(b1, b2)) {
                    stream.moveTo(x, bottom)
                    stream.lineTo(x, top)
                    stream.stroke()
                }
                for (row in 1..7) {
                    val y = top - row * 12f
                    stream.moveTo(left, y)
                    stream.lineTo(left + 258f, y)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
                // C237: golden P1 ATAQUES uses small 7 pt Barlow column labels and
                // right-aligns bonus and damage inside their columns.
                fun labelWidth(text: String) = headerPdfFont.getStringWidth(text) * 6f / 1000f
                // C256: golden ATAQUES column labels; C299: golden 6.2 / 6 pt in #264b68.
                stream.setNonStrokingColor(Color(38, 75, 104))
                drawP1Label(stream, "ARMA / ATAQUE / CONJURO", left + 4f, top - 9f, 6.2f)
                drawP1Label(stream, "BON.", b2 - 4f - labelWidth("BON."), top - 9f, 6f)
                drawP1Label(stream, "DAÑO / TIPO",
                    left + 254f - labelWidth("DAÑO / TIPO"), top - 9f, 6f)
                stream.setNonStrokingColor(Color(36, 57, 75))
                p1Attacks.forEachIndexed { index, entry ->
                    val y = top - 22f - index * 12f
                    // C264: golden P1 attack rows, name 7.4 pt and values 7.1 pt.
                    stream.beginText()
                    stream.setFont(bodyPdfFont, 7.4f)
                    stream.newLineAtOffset(left + 4f, y)
                    stream.showText(pcSheetV11PrintableText(p1AttackLabel(entry)))
                    stream.endText()
                    drawAttackValues(stream, entry, left, y, 7.1f)
                }
                if (attackRows.isEmpty()) {
                    draw(stream, "________________", left + 5f, top - 25f, false)
                }
            }

            // P1 spell-slot cells have fixed physical widths. Preserve every
            // source value without shrinking or overprinting adjacent levels:
            // route outlier counters to the existing measured P1-owned C2.
            val overflowP1SpellSlots = data.spellSlots.filter { slots ->
                slots.level in 1..9 &&
                    (body.width.measurePt(slots.totalSlots.toString()) > 22f ||
                        body.width.measurePt(slots.spentSlots.toString()) > 29f)
            }
            val overflowP1SpellLevels = overflowP1SpellSlots.map { it.level }.toSet()

            fun drawP1Spellcasting(stream:PDPageContentStream) {
                // Approved frozen B P1: LANZAMIENTO uses a filled/stroked
                // 8-segment clipped cartela (302x23pt in the golden).
                // Genuine App P1 has only a 20pt free strip between the
                // traits bottom (top-origin y491) and spell panel (y511).
                // Keep 1pt clearance on each side with a 258x18pt blue cartela at
                // PDF x312..570, y282..300; do not move any source data,
                // spell counters, panels, or page breaks.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231,242,247))
                stream.setStrokingColor(Color(38,75,104))
                stream.setLineWidth(0.55f)
                stream.moveTo(316f,282f)
                stream.lineTo(566f,282f)
                stream.lineTo(570f,286f)
                stream.lineTo(570f,296f)
                stream.lineTo(566f,300f)
                stream.lineTo(316f,300f)
                stream.lineTo(312f,296f)
                stream.lineTo(312f,286f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                // Real rendered P1 review found the first letter touching the
                // left/top stroke. Inset the title 9pt and lower its ink
                // 2pt within the 18pt strip, no change to spell data.
                stream.setNonStrokingColor(Color(29, 56, 79)) // C266: golden title ink
                drawCartelaDiamonds(stream, 312f, 570f, 282f, 300f)
                drawP1Label(stream,"LANZAMIENTO DE CONJUROS",328f,286f,9.1f)
                stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                val actual=data.spellSlots.associateBy{it.level}
                // C250: golden P1 LANZAMIENTO grammar. Three white clipped
                // reference cards (APTITUD MÁGICA / CD SALVACIÓN / ATAQUE
                // MÁGICO) over a 3x3 grid of alternately shaded level cells.
                // The paper boxes ARE the real totals, never pre-marked, with a
                // small gastados/total count (C255); counts that boxes cannot
                // show keep their numbers. Panel bounds are unchanged.
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130,165,186))
                stream.setLineWidth(0.55f)
                stream.addRect(312f,154f,258f,127f)
                stream.stroke()
                stream.restoreGraphicsState()
                fun p1MagicColumn(idx:Int)=314f+idx*85f
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80,122,151))
                stream.setLineWidth(0.55f)
                stream.setNonStrokingColor(Color.WHITE)
                (0..2).forEach{idx->
                    val l=p1MagicColumn(idx)
                    val r=l+84f
                    val b=252f
                    val t=278f
                    val cut=3f
                    stream.moveTo(l+cut,b)
                    stream.lineTo(r-cut,b)
                    stream.lineTo(r,b+cut)
                    stream.lineTo(r,t-cut)
                    stream.lineTo(r-cut,t)
                    stream.lineTo(l+cut,t)
                    stream.lineTo(l,t-cut)
                    stream.lineTo(l,b+cut)
                    stream.closePath()
                    stream.fillAndStroke()
                }
                (1..9).forEach{level->
                    val col=(level-1)%3
                    val row=(level-1)/3
                    stream.setNonStrokingColor(if((col+row)%2==0)
                        Color(231,242,247) else Color(240,247,250))
                    stream.addRect(p1MagicColumn(col),222f-row*24f,84f,24f)
                    stream.fill()
                }
                stream.restoreGraphicsState()
                val magical=when(source.spellcastingAbility){
                    SpellcastingAbility.STRENGTH->"FUE"
                    SpellcastingAbility.DEXTERITY->"DES"
                    SpellcastingAbility.CONSTITUTION->"CON"
                    SpellcastingAbility.INTELLIGENCE->"INT"
                    SpellcastingAbility.WISDOM->"SAB"
                    SpellcastingAbility.CHARISMA->"CAR"
                    SpellcastingAbility.OTHER,SpellcastingAbility.NONE->""
                }
                // C225: P1 and P4 share source-specific truth; do not
                // print contradictory character-wide magical statistics.
                val varyingP1 = io.github.mrsimkin.dndcustomaid.shared.character
                    .PcSheetV11SpellSourceLabels.contradictoryOverallFields(
                        data.spells, source.spellcastingAbility,
                        source.spellSaveDc, source.spellAttackModifier,
                    )
                listOf("APTITUD" to magical,
                    "CD SALVACIÓN" to(source.spellSaveDc?.toString()?:""),
                    "ATAQUE MÁGICO" to (source.spellAttackModifier?.let{
                        if(it>=0)"+"+it else it.toString()
                    }?:"")).forEachIndexed{idx,(label,value)->
                        val x=p1MagicColumn(idx)
                        // C256: golden card ink (labels #657d90, values #1d384f).
                        stream.setNonStrokingColor(Color(101,125,144))
                        drawP1Label(stream,if(label=="APTITUD")"APTITUD MÁGICA" else label,
                            x+5f,270f,6.65f) // C304: golden 6.65 pt
                        stream.setNonStrokingColor(Color(29,56,79))
                        // C231/C256 (owner 2026-10-28, «B y C en ese orden y
                        // mezclando»): each real source with its own value, first
                        // on one line down to 6pt, then on two lines down to 6pt;
                        // only when neither fits, the truthful SEGÚN FUENTE cue.
                        val perSourceParts = if (label in varyingP1)
                            io.github.mrsimkin.dndcustomaid.shared.character
                                .PcSheetV11SpellSourceLabels.perSourceParts(
                                    data.spells, label,
                                ) { id -> data.attributes.firstOrNull {
                                    it.reference.customAttributeId == id
                                }?.abbreviation }
                        else null
                        fun cardInkWidth(text: String, pt: Float): Float =
                            headerPdfFont.getStringWidth(pcSheetV11PrintableText(text)) * pt / 1000f
                        fun cardInk(text: String, pt: Float, baseline: Float) {
                            stream.beginText()
                            stream.setFont(headerPdfFont, pt)
                            stream.newLineAtOffset(x+(84f-cardInkWidth(text, pt))/2f, baseline)
                            stream.showText(pcSheetV11PrintableText(text))
                            stream.endText()
                        }
                        val oneLine = perSourceParts?.joinToString(" / ")
                        val onePt = oneLine?.let { text ->
                            listOf(9f, 8f, 7f, 6.5f, 6f).firstOrNull { cardInkWidth(text, it) <= 81f }
                        }
                        val twoLines = if (perSourceParts != null && onePt == null &&
                            perSourceParts.size >= 2) {
                            listOf(7f, 6.5f, 6f).firstNotNullOfOrNull { pt ->
                                (1 until perSourceParts.size).map { k ->
                                    perSourceParts.take(k).joinToString(" / ") + " /" to
                                        perSourceParts.drop(k).joinToString(" / ")
                                }.filter { (first, second) ->
                                    cardInkWidth(first, pt) <= 81f && cardInkWidth(second, pt) <= 81f
                                }.minByOrNull { (first, second) ->
                                    maxOf(cardInkWidth(first, pt), cardInkWidth(second, pt))
                                }?.let { Triple(it.first, it.second, pt) }
                            }
                        } else null
                        when {
                            oneLine != null && onePt != null -> cardInk(oneLine, onePt, 257f)
                            twoLines != null -> {
                                cardInk(twoLines.first, twoLines.third, 261.5f)
                                cardInk(twoLines.second, twoLines.third,
                                    261.5f - twoLines.third - 0.5f)
                            }
                            label in varyingP1 -> cardInk("SEGÚN FUENTE", 9f, 257f)
                            value.isNotEmpty() -> {
                                require(cardInkWidth(value, 11f) <= 81f) {
                                    "P1 real magic reference must fit its native source card"
                                }
                                // Golden reference values are large centered
                                // Barlow figures, as in the combat cards.
                                cardInk(value, 11f, 257f)
                            }
                        }
                        stream.setNonStrokingColor(Color(0,0,0))
                    }
                (1..9).forEach{level->
                    val x=p1MagicColumn((level-1)%3)
                    val b=222f-((level-1)/3)*24f
                    drawP1Label(stream,"N"+level,x+5f,b+9f)
                    actual[level]?.let{v->
                        if (level in overflowP1SpellLevels) {
                            draw(stream,"VER C2",x+20f,b+8f,false)
                        } else if(v.totalSlots in 1..6 && v.spentSlots in 0..v.totalSlots) {
                            // C255 (owner 2026-10-28, «B, pero sin marcas»): empty
                            // paper boxes, never pre-marked, and a small golden
                            // "gastados/total" count right-aligned in the cell
                            // (Mara N1: 2/4). Six slots use 6.5pt boxes so the
                            // count still fits; never invent a grammar for 7+.
                            val count = v.spentSlots.toString() + "/" + v.totalSlots
                            val countPt = 7f
                            val countX = x + 81f -
                                headerPdfFont.getStringWidth(count) * countPt / 1000f
                            val box = if (v.totalSlots == 6) 6.5f else 8f
                            val markStep = when (v.totalSlots) {
                                6 -> 8.3f
                                5 -> 10f
                                else -> 12f
                            }
                            require(x+20f+(v.totalSlots-1)*markStep+box <= countX-2f) {
                                "P1 spell-slot boxes overlap their real count"
                            }
                            stream.saveGraphicsState()
                            stream.setStrokingColor(Color(82,124,150))
                            stream.setNonStrokingColor(Color.WHITE)
                            stream.setLineWidth(0.48f)
                            repeat(v.totalSlots){n->
                                stream.addRect(x+20f+n*markStep,b+8f+(8f-box)/2f,box,box)
                                stream.fillAndStroke()
                            }
                            stream.setNonStrokingColor(Color(38,75,104))
                            drawP1Label(stream,count,countX,b+9f,countPt)
                            stream.restoreGraphicsState()
                        } else {
                            draw(stream,v.totalSlots.toString(),x+20f,b+8f,false)
                            draw(stream,v.spentSlots.toString(),x+46f,b+8f,false)
                        }
                    }?:run{
                        stream.moveTo(x+20f,b+7f)
                        stream.lineTo(x+78f,b+7f)
                        stream.stroke()
                    }
                }
            }

            // Eight real combat cells are frozen at 121pt of usable value width.
            // Extreme multiclass hit dice (or other source numbers) move WHOLE to
            // the existing measured, bidirectionally referenced P1-owned C2.
            fun signedCombat(v: Int) = if (v >= 0) "+" + v else v.toString()
            val p1HitDice = source.classes.joinToString(" / ") {
                it.hitDiceRemaining.toString() + "d" + it.hitDieSides
            }
            // C256 (owner 2026-10-28, «D 35 pies y 10,5 m»): the saved speed is
            // in feet; print feet and metres (5 pies = 1,5 m).
            val speedTenthsMetres = source.speed * 3
            val p1Speed = source.speed.toString() + " pies · " + speedTenthsMetres / 10 +
                (if (speedTenthsMetres % 10 != 0)
                    "," + kotlin.math.abs(speedTenthsMetres % 10) else "") + " m"
            // C256 (owner 2026-10-28): golden INSPIRACIÓN card (real sheet state,
            // drawn as a paper box) in place of PV TEMP; the real temporary hit
            // points ride inside PV ACTUALES. Golden label DADOS DE GOLPE.
            val p1CombatSourceFields = listOf(
                "CA" to source.armorClass.toString(),
                "INICIATIVA" to signedCombat(source.initiativeModifier),
                "VELOCIDAD" to p1Speed,
                "COMPETENCIA" to signedCombat(source.finalProficiencyBonus),
                "PV ACTUALES" to source.currentHp.toString(),
                "PV MÁXIMOS" to source.maxHp.toString(),
                "INSPIRACIÓN" to "",
                "DADOS DE GOLPE" to p1HitDice,
            )
            val p1CombatRoutes = p1CombatSourceFields.mapIndexed { index, (label, value) ->
                // Six narrow genuine source cards and two wider hit-point
                // cards. Oversized values retain their real source in C2;
                // the speed steps its golden figure down to 6pt first.
                val cardWidth = if (index == 4 || index == 5) 129f else 86f
                PcSheetV11CompactFieldRouter.route(
                    "p1-combat:" + label, label, value,
                    if (label == "VELOCIDAD") (cardWidth - 12f) * 9f / 6f else cardWidth - 12f,
                    body.width,
                )
            }
            // Golden C2 ATAQUES: deferred attacks that fit the approved
            // three-column row print as table rows (attack / bonus / damage),
            // in source order, before their range/notes lines. The rest keep
            // the lossless text record.
            val c2AttackTableRows = attackRows.filter {
                it.id !in p1AttackIds && attackFitsTableRow(it)
            }.associateBy { "combat:row:" + it.id }
            val extraCombatData = buildList {
                addAll(p1CombatRoutes.mapNotNull { it.continuation })
                c2AttackTableRows.forEach { (key, attack) ->
                    add(PcSheetV11FlowItem(key, p1AttackLabel(attack), keepTogetherIfFits = true))
                }
                attackRows.forEach { attack ->
                    if (attack.id !in p1AttackIds &&
                        "combat:row:" + attack.id !in c2AttackTableRows) {
                        add(PcSheetV11FlowItem(
                            "combat:deferred:" + attack.id,
                            "ATAQUE " + attack.name +
                                (attack.rangeText?.let { " | ALCANCE " + it } ?: "") +
                                (attack.attackModifier?.let { " | BONO " + it } ?: "") +
                                " | DAÑO " + attack.damageEffect +
                                (attack.notes?.let { " | NOTAS " + it } ?: ""),
                            keepTogetherIfFits = true,
                        ))
                    } else {
                        if(attack.rangeText?.isNotBlank()==true &&
                            p1AttackLabel(attack)==attack.name) {
                            add(PcSheetV11FlowItem("combat:range:"+attack.id,
                                "ALCANCE "+attack.name+": "+attack.rangeText,
                                keepTogetherIfFits=true))
                        }
                        attack.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                            add(PcSheetV11FlowItem(
                                "combat:notes:" + attack.id,
                                "NOTAS DEL ATAQUE " + attack.name + ": " + notes,
                                keepTogetherIfFits = true,
                            ))
                        }
                    }
                }
                // Values too wide for the frozen 3x3 P1 cells remain complete
                // on C2 and are never mistaken for absent paper slots.
                overflowP1SpellSlots.forEach { slots ->
                    add(PcSheetV11FlowItem(
                        "spellcasting:slots:" + slots.level,
                        "ESPACIOS REALES NIVEL " + slots.level +
                            ": TOTAL " + slots.totalSlots +
                            ", GASTADOS " + slots.spentSlots,
                        keepTogetherIfFits = true,
                    ))
                }
                // High-level slot rows only exist when explicitly persisted.
                data.spellSlots.filter { it.level >= 10 }.forEach { slots ->
                    add(PcSheetV11FlowItem(
                        "spellcasting:epic:" + slots.level,
                        "ESPACIOS REALES NIVEL " + slots.level +
                            ": TOTAL " + slots.totalSlots + ", GASTADOS " + slots.spentSlots,
                    ))
                }
            }



            // P1 is the FIRST physical occurrence of each Rasgo:
            // print ONLY name/category/source, never its description/notes.
            // P2/C2 continue the remaining original IDs without repeats.
            val p1Font = measurer.resolve(PcSheetV11Typeface.BODY, 7f)
            fun p1DrawSmall(stream: PDPageContentStream, content: String, x: Float, y: Float) {
                if (content.isEmpty()) return
                stream.beginText()
                stream.setFont(bodyPdfFont, 7f)
                stream.newLineAtOffset(x, y)
                stream.showText(pcSheetV11PrintableText(content))
                stream.endText()
            }
            // Fit complete identities in 12 genuine 10pt paper rows
            // (six per column). A long entry can use multiple rows; if it
            // cannot fit, start the P2 continuation with that SAME source ID.
            // Always select a source-order PREFIX to avoid reordering Rasgos.
            fun p1TraitWrap(label: String): List<String> {
                if (label.isBlank()) return emptyList()
                val lines = mutableListOf<String>()
                var current = ""
                for (word in label.split(' ').filter { it.isNotEmpty() }) {
                    if (p1Font.width.measurePt(word) > 121f) return emptyList()
                    val candidate = if (current.isEmpty()) word else "${current} $word"
                    if (p1Font.width.measurePt(candidate) <= 121f) {
                        current = candidate
                    } else {
                        if (current.isNotEmpty()) lines += current
                        current = word
                    }
                }
                if (current.isNotEmpty()) lines += current
                return lines
            }
            val p1TraitEntries = buildList<Triple<
                io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait,
                List<String>, Int>>() {
                var slot = 0
                for (trait in data.traits) {
                    val lines = p1TraitWrap(
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TraitSummary.text(trait)
                    )
                    if (lines.isEmpty() || lines.size > 6) break
                    if (slot < 6 && slot + lines.size > 6) slot = 6
                    if (slot + lines.size > 12) break
                    add(Triple(trait, lines, slot))
                    slot += lines.size
                }
            }
            val p1PrintedTraitIds = p1TraitEntries.map { it.first.id }.toSet()
            fun drawP1Traits(stream: PDPageContentStream) {
                val left = 312f
                val width = 258f
                val top = 382f
                repeat(6) { row ->
                    val y = top - 21f - row * 10f
                    for (col in 0..1) {
                        fillBand(stream, left + col * 129f + 1f, y - 3f, 127f, 10f, row)
                    }
                }
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.5f)
                stream.addRect(left, 301f, width, top - 301f)
                stream.stroke()
                stream.moveTo(441f, 301f)
                stream.lineTo(441f, top - 15f)
                stream.stroke()
                repeat(6) { row ->
                    val y = top - 21f - row * 10f
                    for (col in 0..1) {
                        val x = left + col * 129f
                        stream.moveTo(x + 3f, y - 3f)
                        stream.lineTo(x + 126f, y - 3f)
                        stream.stroke()
                    }
                }
                stream.restoreGraphicsState()
                // Owner V11 C2c: Rasgos heading has a clipped pale-blue band,
                // not floating text. Exactly the existing 14pt P1 title strip:
                // PDF x312..570/y368..382; row-1 begins at y361 untouched.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231,242,247))
                stream.setStrokingColor(Color(38,75,104))
                stream.setLineWidth(0.55f)
                stream.moveTo(315f,368f)
                stream.lineTo(567f,368f)
                stream.lineTo(570f,371f)
                stream.lineTo(570f,379f)
                stream.lineTo(567f,382f)
                stream.lineTo(315f,382f)
                stream.lineTo(312f,379f)
                stream.lineTo(312f,371f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                // Golden heading font role: authentic embedded Barlow Condensed,
                // distinct from 7pt Fira source-value rows below.
                stream.setNonStrokingColor(Color(29, 56, 79)) // C266: golden title ink
                drawCartelaDiamonds(stream, 312f, 570f, 368f, 382f)
                stream.beginText()
                stream.setFont(headerPdfFont,9.1f)
                stream.newLineAtOffset(left + 16f,top - 11f)
                stream.showText("RASGOS Y ATRIBUTOS")
                stream.endText()
                stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                p1TraitEntries.forEach { (_, lines, firstSlot) ->
                    lines.forEachIndexed { offset, line ->
                        val slot = firstSlot + offset
                        p1DrawSmall(stream, line, left + 4f + (slot / 6) * 129f,
                            top - 21f - (slot % 6) * 10f)
                    }
                }
            }

            // Explicit AMMUNITION usage is required; names do not imply ammo.
            // Draw up to two P1 rows with groups of five paper-only marks.
            // Overflow and full source details stay in C2; never count marks twice.
            val p1AmmunitionIds = data.source.closure.inventoryUsage
                .filter { it.kind ==
                    io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind.AMMUNITION }
                .map { it.itemId }.toSet()
            val p1AmmoItems = data.source.sheet.inventoryItems
                // The fixed paper mark row is SINGLE-LINE. A source ammo
                // name containing CR/LF/TAB cannot be printed in this slot even
                // when a font-width measurement appears to fit. Its genuine
                // UUID/name/quantity remain in the existing measured C2 route.
                .filter { item ->
                    item.id in p1AmmunitionIds && item.quantity >= 0 &&
                        item.name.none { ch -> ch == '\r' || ch == '\n' || ch == '\t' } &&
                        p1Font.width.measurePt(item.name) <= 68f
                }
                .sortedWith(compareBy<
                    io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem> {
                    it.sortOrder
                }.thenBy { it.id.toString() }).take(1)
            // C257: the golden two-row grammar fits ONE ammunition entry in the
            // fixed P1 band; the next ones continue whole in C2 (owner choice).
            val p1AmmoIds = p1AmmoItems.map { it.id }.toSet()
            fun drawP1Ammunition(stream: PDPageContentStream) {
                // C243/C257: golden MUNICIONES — a clipped blue cartela in the
                // 17 pt header row (131..148) and ONE golden entry band (102..131):
                // the name at the left and its marks as two rows of ten in groups
                // of five (owner 2026-10-28, «dos filas de 10 como el golden»).
                stream.saveGraphicsState()
                fillBand(stream, 312f, 102f, 258f, 29f, 0)
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.55f)
                val cut = 3f
                stream.moveTo(312f + cut, 131f)
                stream.lineTo(570f - cut, 131f)
                stream.lineTo(570f, 131f + cut)
                stream.lineTo(570f, 148f - cut)
                stream.lineTo(570f - cut, 148f)
                stream.lineTo(312f + cut, 148f)
                stream.lineTo(312f, 148f - cut)
                stream.lineTo(312f, 131f + cut)
                stream.closePath()
                stream.fillAndStroke()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.addRect(312f, 102f, 258f, 46f)
                stream.stroke()
                stream.restoreGraphicsState()
                // C257/C265: golden cartela title and diamonds.
                stream.setNonStrokingColor(Color(29, 56, 79)) // C266: golden title ink
                drawCartelaDiamonds(stream, 312f, 570f, 131f, 148f)
                drawP1Label(stream, "MUNICIONES", 328f, 136.5f, 9.1f)
                stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                // Golden mark geometry (7.94 pt boxes, 13.75 pt step, 2.83 pt
                // extra gap between groups), at the golden distance from the
                // right edge of the column.
                fun ammoBoxes(count: Int) {
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(82, 124, 150))
                    stream.setNonStrokingColor(Color.WHITE)
                    stream.setLineWidth(0.48f)
                    repeat(count) { index ->
                        val column = index % 10
                        val x = 419.8f + column * 13.75f + (column / 5) * 2.83f
                        val y = if (index < 10) 119f else 106f
                        require(x + 7.94f <= 570f)
                        stream.addRect(x, y, 7.94f, 7.94f)
                        stream.fillAndStroke()
                    }
                    stream.restoreGraphicsState()
                }
                // D-0076 §3.39: no recorded/displayable ammunition still
                // reserves ONE logical paper entry with a blank name and
                // exactly 20 unmarked squares, grouped 5+5 in two rows.
                // Do not invent a type, a quantity or a second ammunition ID.
                if (p1AmmoItems.isEmpty()) {
                    // Empty ammunition NAME is a writable PAPER baseline,
                    // not black underscore glyphs impersonating a source.
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(130, 165, 186))
                    stream.setLineWidth(0.55f)
                    stream.moveTo(316f, 120f)
                    stream.lineTo(410f, 120f)
                    stream.stroke()
                    stream.restoreGraphicsState()
                    ammoBoxes(20)
                }
                p1AmmoItems.forEach { item ->
                    val baseline = 121f
                    p1DrawSmall(stream, item.name, 316f, baseline)
                    when {
                        item.quantity == 0 -> p1DrawSmall(stream, "0", 419.8f, baseline)
                        item.quantity <= 20 -> ammoBoxes(item.quantity)
                        else -> p1DrawSmall(stream, "MARCAS: ____ / " + item.quantity, 419.8f, baseline)
                    }
                }
            }

            fun drawP1Portrait(stream: PDPageContentStream) {
                // C349: golden v11_extremo p1 portrait x39.69..119.06, y621.92..698.46.
                val frameX = 39.69f
                val frameY = 621.92f
                val frameWidth = 79.37f
                val frameHeight = 76.54f
                val imageX = frameX + 5f
                val imageY = frameY + 10f
                val imageWidth = frameWidth - 10f
                val imageHeight = frameHeight - 15f
                // Frozen V11 golden uses a chamfered portrait frame, not
                // the provisional rectangular proof. Preserve image viewport.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color.WHITE)
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.65f)
                val corner = 5f
                stream.moveTo(frameX + corner, frameY)
                stream.lineTo(frameX + frameWidth - corner, frameY)
                stream.lineTo(frameX + frameWidth, frameY + corner)
                stream.lineTo(frameX + frameWidth, frameY + frameHeight - corner)
                stream.lineTo(frameX + frameWidth - corner, frameY + frameHeight)
                stream.lineTo(frameX + corner, frameY + frameHeight)
                stream.lineTo(frameX, frameY + frameHeight - corner)
                stream.lineTo(frameX, frameY + corner)
                stream.closePath()
                stream.fillAndStroke()
                // C349: golden inner hairline 3.4 pt under the top, 9.07 pt in, #bfd3de 0.35 pt.
                stream.setStrokingColor(Color(191, 211, 222))
                stream.setLineWidth(0.35f)
                stream.moveTo(frameX + 9.07f, frameY + frameHeight - 3.4f)
                stream.lineTo(frameX + frameWidth - 9.07f, frameY + frameHeight - 3.4f)
                stream.stroke()
                stream.restoreGraphicsState()

                if (portraitBytes != null) {
                    // Same data the existing App export services load locally.
                    // FIT preserves the full image; CROP clips its centre without
                    // stretching. This path is embedded in the actual PDF file.
                    val picture = PDImageXObject.createFromByteArray(
                        pdf, portraitBytes, "pc-v11-local-portrait",
                    )
                    require(picture.width > 0 && picture.height > 0)
                    val xRatio = imageWidth / picture.width
                    val yRatio = imageHeight / picture.height
                    val scale = if (data.portrait.fitMode ==
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPortraitFitMode.CROP_TO_FILL
                    ) maxOf(xRatio, yRatio) else minOf(xRatio, yRatio)
                    val drawWidth = picture.width * scale
                    val drawHeight = picture.height * scale
                    val px = imageX + (imageWidth - drawWidth) / 2f
                    val py = imageY + (imageHeight - drawHeight) / 2f
                    stream.saveGraphicsState()
                    stream.addRect(imageX, imageY, imageWidth, imageHeight)
                    stream.clip()
                    stream.stroke()
                    stream.drawImage(picture, px, py, drawWidth, drawHeight)
                    stream.restoreGraphicsState()
                } else {
                    // The original approved paper's absent-portrait symbol is a
                    // pair of PALE FILLED ellipses, never an outline-only avatar.
                    // Normalize its original bounds to the existing V11 viewport.
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(231, 242, 247))
                    stream.setStrokingColor(Color(191, 211, 222))
                    stream.setLineWidth(0.35f)
                    val k = 0.5522848f
                    fun filledOval(cx: Float, cy: Float, rx: Float, ry: Float) {
                        stream.moveTo(cx + rx, cy)
                        stream.curveTo(cx + rx, cy + k * ry, cx + k * rx, cy + ry, cx, cy + ry)
                        stream.curveTo(cx - k * rx, cy + ry, cx - rx, cy + k * ry, cx - rx, cy)
                        stream.curveTo(cx - rx, cy - k * ry, cx - k * rx, cy - ry, cx, cy - ry)
                        stream.curveTo(cx + k * rx, cy - ry, cx + rx, cy - k * ry, cx + rx, cy)
                        stream.closePath()
                        stream.fillAndStroke()
                    }
                    val cx = frameX + frameWidth / 2f
                    // C349: golden silhouette - head 11.34 pt, body 21.26 x 15.03 pt.
                    filledOval(cx, frameY + 55.85f, 11.34f, 11.34f)
                    filledOval(cx, frameY + 24.67f, 21.26f, 15.03f)
                    stream.restoreGraphicsState()
                }
                // Golden's caption is beneath the figure and never covers its head.
                // Keep the label when a real local image is shown as well.
                val portraitCaption = "RETRATO"
                val captionSize = 5.5f // C349: golden 5.5 pt #657d90, 2.84 pt above the foot
                val captionWidth = headerPdfFont.getStringWidth(portraitCaption) /
                    1000f * captionSize
                stream.saveGraphicsState()
                stream.beginText()
                stream.setFont(headerPdfFont, captionSize)
                stream.setNonStrokingColor(Color(101, 125, 144))
                stream.newLineAtOffset(frameX + (frameWidth - captionWidth) / 2f, frameY + 2.84f)
                stream.showText(portraitCaption)
                stream.endText()
                stream.restoreGraphicsState()
            }

            // V11 P1 uses genuine physical identity fields. Alignment is not a
            // persisted CharacterSheet property, so its box stays manuscript-only.
            // The same measured body font decides which source suffix must flow to C2.
            val identityTop = 698.46f // C349: golden identity block top
            val classIdentity = PcSheetV11FlowItem(
                "identity:classes",
                source.classes.joinToString(", ") { it.name + " " + it.level },
            )
            val raceIdentity = PcSheetV11FlowItem("identity:race", source.background.race)
            val identityFieldFrames = listOf(
                // C349: golden v11_extremo p1 boxes - CLASE / NIVEL x127.56..572.32,
                // y664.44..698.46; JUGADOR, RAZA and ALINEAMIENTO y621.92..658.77.
                Triple("CLASE / NIVEL", classIdentity,
                    PcSheetV11RectPt(127.56f, 664.44f, 444.76f, 34.02f)),
                Triple("RAZA", raceIdentity,
                    PcSheetV11RectPt(280.63f, 621.92f, 175.18f, 36.85f)),
            )
            // C349: golden values - CLASE / NIVEL 9.3 pt, the others 7.4 pt.
            val identityFonts = mapOf(
                "CLASE / NIVEL" to measurer.resolve(PcSheetV11Typeface.BODY, 9.3f),
                "RAZA" to measurer.resolve(PcSheetV11Typeface.BODY, 7.4f),
            )
            val identityBaselineLift = mapOf("CLASE / NIVEL" to 8.22f, "RAZA" to 9.92f)
            val identityFields = identityFieldFrames.map { (label, item, frame) ->
                val font = identityFonts.getValue(label)
                val writingArea = PcSheetV11RectPt(
                    frame.leftPt + 5.67f, frame.bottomPt + 1f,
                    frame.widthPt - 11.34f, font.metrics.lineHeightPt + 2f,
                )
                val placed = item.text.takeIf { it.isNotBlank() }?.let {
                    font.place(PcSheetV11FlowSection.TRAITS, listOf(item),
                        writingArea, 1, 0f)
                }
                Triple(label to item, frame, placed)
            }
            fun drawP1IdentityFields(stream: PDPageContentStream) {
                val alignment = PcSheetV11RectPt(461.48f, 621.92f, 110.84f, 36.85f) // C349 golden
                val player = PcSheetV11RectPt(127.56f, 621.92f, 147.4f, 36.85f) // C349 golden
                // C349: golden identity boxes - white, #264b68 0.65 pt edge.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(255, 255, 255))
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.65f)
                (identityFieldFrames.map { it.third } + listOf(player, alignment)).forEach { frame ->
                    // P1 C2c golden uses four INDIVIDUAL chamfered identity
                    // outlines. Stay strictly inside the old 28pt rectangles;
                    // labels, values, source offsets and writing lines remain
                    // at the exact previously verified PDF coordinates.
                    val cut = 4f
                    require(frame.widthPt > cut * 2f && frame.heightPt > cut * 2f)
                    val l = frame.leftPt
                    val r = frame.rightPt
                    val b = frame.bottomPt
                    val t = frame.topPt
                    stream.moveTo(l + cut, b)
                    stream.lineTo(r - cut, b)
                    stream.lineTo(r, b + cut)
                    stream.lineTo(r, t - cut)
                    stream.lineTo(r - cut, t)
                    stream.lineTo(l + cut, t)
                    stream.lineTo(l, t - cut)
                    stream.lineTo(l, b + cut)
                    stream.closePath()
                    stream.fillAndStroke()
                    // C349: golden inner hairline 3.4 pt under the top, 9.07 pt in.
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(191, 211, 222))
                    stream.setLineWidth(0.35f)
                    stream.moveTo(l + 9.07f, t - 3.4f)
                    stream.lineTo(r - 9.07f, t - 3.4f)
                    stream.stroke()
                    stream.restoreGraphicsState()
                }
                stream.restoreGraphicsState()
                identityFields.forEach { (descriptor, _, placed) ->
                    val (label, _) = descriptor
                    val frame = identityFieldFrames.first { it.first == label }.third
                    // C297: golden identity labels, grey #657d90 (CLASE / NIVEL 6.2 pt, others 5.9).
                    stream.setNonStrokingColor(Color(101, 125, 144))
                    // C349: golden label 5.67 pt in and 11.34 pt under the top.
                    drawP1Label(stream, label, frame.leftPt + 5.67f, frame.topPt - 11.34f,
                        if (label == "CLASE / NIVEL") 6.2f else 5.9f)
                    stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                    val font = identityFonts.getValue(label)
                    placed?.cells?.filter { it.localPageIndex == 1 }?.forEach { cell ->
                        if (cell.drawableText.isNotEmpty()) {
                            stream.beginText()
                            stream.setFont(bodyPdfFont, font.metrics.fontSizePt)
                            stream.newLineAtOffset(cell.cellPt.leftPt,
                                frame.bottomPt + identityBaselineLift.getValue(label))
                            stream.showText(pcSheetV11PrintableText(cell.drawableText))
                            stream.endText()
                        }
                    }
                }
                stream.setNonStrokingColor(Color(101, 125, 144))
                drawP1Label(stream, "JUGADOR", player.leftPt + 5.67f, player.topPt - 11.34f, 5.9f)
                stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(148, 176, 193))
                stream.setLineWidth(0.32f)
                stream.moveTo(player.leftPt + 5.67f, player.bottomPt + 7f)
                stream.lineTo(player.rightPt - 5.67f, player.bottomPt + 7f)
                stream.stroke()
                stream.restoreGraphicsState()
                stream.setNonStrokingColor(Color(101, 125, 144))
                drawP1Label(stream, "ALINEAMIENTO", alignment.leftPt + 5.1f, // C349 golden inset
                    alignment.topPt - 11.34f, 5.7f)
                stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                // No persisted alignment exists; a ruled entry is deliberately blank.
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(148, 176, 193))
                stream.setLineWidth(0.32f)
                stream.moveTo(alignment.leftPt + 5.67f, alignment.bottomPt + 7f)
                stream.lineTo(alignment.rightPt - 5.67f, alignment.bottomPt + 7f)
                stream.stroke()
                stream.restoreGraphicsState()
            }
            val identityFieldOverflow = identityFields.mapNotNull { (descriptor, _, placed) ->
                val (_, item) = descriptor
                val consumed = placed?.cells?.asSequence()
                    ?.filter { it.localPageIndex == 1 && it.sourceKey == item.sourceKey }
                    ?.maxOfOrNull { it.sourceEndExclusive } ?: 0
                require(consumed in 0..item.text.length)
                if (consumed == item.text.length) null
                else item.copy(text = item.text.substring(consumed))
            }

            // A separate physical combat dashboard replaces the old prose
            // identity row; values are taken ONLY from the saved character.
            fun drawP1CombatReference(stream: PDPageContentStream) {
                val values = p1CombatSourceFields.mapIndexed { index, field ->
                    field.first to p1CombatRoutes[index].paperValue
                }
                val left = 312f
                val paperTop = 591f
                // Golden P1 has 3+3+2 real cards. This App persists PV TEMP,
                // not INSPIRACIÓN: never invent an absent golden state.
                // First six cards are 86pt wide, bottom two 129pt each.
                val slots = listOf(
                    Triple(0, 0, 0), Triple(1, 1, 0), Triple(2, 2, 0),
                    Triple(3, 0, 1), Triple(6, 1, 1), Triple(7, 2, 1),
                    Triple(4, 0, 2), Triple(5, 1, 2),
                )
                val title = "COMBATE Y SUPERVIVENCIA"
                require(headerPdfFont.getStringWidth(title) * 11f / 1000f <= 240f)
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.65f) // C293: golden cartela edge
                stream.moveTo(317f, 594f)
                stream.lineTo(565f, 594f)
                stream.lineTo(570f, 599f)
                stream.lineTo(570f, 611f)
                stream.lineTo(565f, 616f)
                stream.lineTo(317f, 616f)
                stream.lineTo(312f, 611f)
                stream.lineTo(312f, 599f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                stream.setNonStrokingColor(Color(29, 56, 79)) // C266: golden title ink
                drawCartelaDiamonds(stream, 312f, 570f, 594f, 616f)
                stream.beginText()
                stream.setFont(headerPdfFont, 9.1f)
                stream.newLineAtOffset(328f, 600f)
                stream.showText(title)
                stream.endText()
                stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.55f)
                slots.forEach { (_, col, row) ->
                    val narrow = row < 2
                    val rawWidth = if (narrow) 86f else 129f
                    val rawHeight = if (narrow) 28f else 32f
                    // 2.4pt gutters, all inside the OLD source paper bounds
                    // x312..570 and y503..591: do not touch attack tables.
                    val l = left + col * rawWidth + 1.2f
                    val r = left + (col + 1) * rawWidth - 1.2f
                    val t = paperTop - row * 28f - 1.2f
                    val b = paperTop - row * 28f - rawHeight + 1.2f
                    require(l >= 312f && r <= 570f && b >= 503f && t <= 591f)
                    val cut = 3f
                    stream.setNonStrokingColor(if (row == 1)
                        Color(244, 250, 253) else Color(238, 247, 251))
                    stream.moveTo(l + cut, b)
                    stream.lineTo(r - cut, b)
                    stream.lineTo(r, b + cut)
                    stream.lineTo(r, t - cut)
                    stream.lineTo(r - cut, t)
                    stream.lineTo(l + cut, t)
                    stream.lineTo(l, t - cut)
                    stream.lineTo(l, b + cut)
                    stream.closePath()
                    stream.fillAndStroke()
                }
                stream.restoreGraphicsState()
                slots.forEach { (sourceIndex, col, row) ->
                    val (label, value) = values[sourceIndex]
                    val narrow = row < 2
                    val width = if (narrow) 86f else 129f
                    val top = paperTop - row * 28f
                    val x = left + col * width
                    val labelPt = 6.65f // C316: golden combat card label size
                    require(headerPdfFont.getStringWidth(label) *
                        labelPt / 1000f <= width - 12f) {
                        "P1 true source combat label exceeds its real card: " + label
                    }
                    // C256: golden card ink (labels #657d90, values #1d384f).
                    stream.setNonStrokingColor(Color(101, 125, 144))
                    stream.beginText()
                    stream.setFont(headerPdfFont, labelPt)
                    stream.newLineAtOffset(x + 5f, top - 10f)
                    stream.showText(pcSheetV11PrintableText(label))
                    stream.endText()
                    stream.setNonStrokingColor(Color(29, 56, 79))
                    val valueBaseline = top - (if (narrow) 22f else 24f)
                    if (label == "INSPIRACIÓN") {
                        // Paper box, marked only when the sheet really has it.
                        val bx = x + width / 2f - 4.5f
                        val by = valueBaseline - 1f
                        stream.saveGraphicsState()
                        stream.setStrokingColor(Color(82, 124, 150))
                        stream.setLineWidth(0.48f)
                        stream.setNonStrokingColor(
                            if (source.inspiration) Color(191, 211, 222) else Color.WHITE)
                        stream.addRect(bx, by, 9f, 9f)
                        stream.fillAndStroke()
                        stream.restoreGraphicsState()
                        // C325: v8 INSPIRATION_ACTIVE (filled star), only when real.
                        if (source.inspiration) drawStateGlyph(stream, 0xE10E, bx, by, 9f)
                    } else if (value.isNotEmpty()) {
                        // Golden stat values are large, centered Barlow figures
                        // (~11pt); a longer real value steps down to 6pt.
                        val printable = pcSheetV11PrintableText(value)
                        val valuePt = requireNotNull(listOf(10.7f, 10f, 9f, 8f, 7f, 6f).firstOrNull {
                            headerPdfFont.getStringWidth(printable) * it / 1000f <= width - 12f
                        }) { "P1 combat value must flow into source-backed C2: " + label }
                        val drawn = headerPdfFont.getStringWidth(printable) * valuePt / 1000f
                        stream.beginText()
                        stream.setFont(headerPdfFont, valuePt)
                        stream.newLineAtOffset(x + (width - drawn) / 2f, valueBaseline)
                        stream.showText(printable)
                        stream.endText()
                        if (label == "PV ACTUALES") {
                            val temp = "TEMP " + source.tempHp
                            val tempWidth = headerPdfFont.getStringWidth(temp) * 7f / 1000f
                            require(x + (width + drawn) / 2f + 4f <= x + width - 6f - tempWidth) {
                                "P1 real temporary hit points must fit inside PV ACTUALES"
                            }
                            stream.setNonStrokingColor(Color(101, 125, 144))
                            drawP1Label(stream, temp, x + width - 6f - tempWidth, valueBaseline, 7f)
                        }
                    }
                    stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                }
            }

            // Approved golden P4: source-backed magical aptitude, real save DC,
            // real spell attack; blank manuscript fields when values are absent.
            // This is a physical banner above the three-column level ledger,
            // NOT additional fake spell slots or a generic body-text section.
            fun drawP4MagicReference(stream: PDPageContentStream) {
                // C221: for contradictory source-specific spellcasting
                // profiles never print one global number as a universal
                // value. Per-spell P4 rows continue to print each genuine
                // source identity and independent prepared state.
                // Exact per-source numeric card geometry is still an OWNER
                // decision under D-0076, so do not fabricate it here.
                val labels = io.github.mrsimkin.dndcustomaid.shared.character
                    .PcSheetV11SpellSourceLabels
                val varying = labels.contradictoryOverallFields(
                    data.spells, source.spellcastingAbility,
                    source.spellSaveDc, source.spellAttackModifier,
                )
                val ability = when (source.spellcastingAbility) {
                    SpellcastingAbility.STRENGTH -> "FUE"
                    SpellcastingAbility.DEXTERITY -> "DES"
                    SpellcastingAbility.CONSTITUTION -> "CON"
                    SpellcastingAbility.INTELLIGENCE -> "INT"
                    SpellcastingAbility.WISDOM -> "SAB"
                    SpellcastingAbility.CHARISMA -> "CAR"
                    SpellcastingAbility.OTHER, SpellcastingAbility.NONE -> null
                }
                val attack = source.spellAttackModifier?.let {
                    if (it >= 0) "+" + it else it.toString()
                }
                // C230 (owner decision): a contradictory field prints each real
                // source with its own value ("Mago 15 / Hechicero 17"). If it
                // cannot fit the card even at the smallest legible size, fall
                // back to the previous truthful cue instead of truncating.
                fun perSourceParts(field: String): List<String>? = labels.perSourceParts(
                    data.spells, field,
                ) { id -> data.attributes.firstOrNull {
                    it.reference.customAttributeId == id
                }?.abbreviation }
                fun perSource(field: String): String? =
                    perSourceParts(field)?.joinToString(" / ")
                val fields: List<Pair<String, String?>> = listOf(
                    "APTITUD" to if ("APTITUD" in varying)
                        perSource("APTITUD") ?: "SEGÚN FUENTE" else ability,
                    "CD SALVACIÓN" to if ("CD SALVACIÓN" in varying)
                        perSource("CD SALVACIÓN") ?: "SEGÚN FUENTE"
                    else source.spellSaveDc?.toString(),
                    "ATAQUE MÁGICO" to if ("ATAQUE MÁGICO" in varying)
                        perSource("ATAQUE MÁGICO") ?: "SEGÚN FUENTE" else attack,
                )
                stream.saveGraphicsState()
                // C308: golden cartela edge #264b68 0.65 pt across x39.69..572.32.
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.65f)
                stream.setNonStrokingColor(Color(231, 241, 247))
                // Frozen V11 P4: a genuinely clipped, stroked blue cartela,
                // confined to the original source-free header strip.
                val x0 = 39.69f
                val x1 = 572.32f
                // C322: golden REFERENCIA MÁGICA cartela band (y672.66..695.62).
                val y0 = 672.66f
                val y1 = 695.62f
                val corner = 6f
                stream.moveTo(x0 + corner, y0)
                stream.lineTo(x1 - corner, y0)
                stream.lineTo(x1, y0 + corner)
                stream.lineTo(x1, y1 - corner)
                stream.lineTo(x1 - corner, y1)
                stream.lineTo(x0 + corner, y1)
                stream.lineTo(x0, y1 - corner)
                stream.lineTo(x0, y0 + corner)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                // C269: golden cartela: diamonds, 9.1 pt title in golden ink.
                drawCartelaDiamonds(stream, x0, x1, y0, y1)
                stream.setNonStrokingColor(Color(29, 56, 79))
                drawP1Label(stream, "REFERENCIA MÁGICA", x0 + 16f, 679.5f, 9.1f)
                stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                fields.forEachIndexed { index, (label, value) ->
                    // C308: golden cards x39.69 + 178.58 i, 174.33 pt wide, #264b68 edge.
                    val x = 39.69f + index * 178.58f
                    val cardWidth = 174.33f
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(38, 75, 104))
                    stream.setLineWidth(0.65f)
                    // Golden uses clipped corners on each source-backed card.
                    val cardCut = 6f
                    val cardRight = x + cardWidth
                    // C322: golden cards y640.91..670.68 (29.77 pt).
                    val cardBottom = 640.91f
                    val cardTop = 670.68f
                    stream.moveTo(x + cardCut, cardBottom)
                    stream.lineTo(cardRight - cardCut, cardBottom)
                    stream.lineTo(cardRight, cardBottom + cardCut)
                    stream.lineTo(cardRight, cardTop - cardCut)
                    stream.lineTo(cardRight - cardCut, cardTop)
                    stream.lineTo(x + cardCut, cardTop)
                    stream.lineTo(x, cardTop - cardCut)
                    stream.lineTo(x, cardBottom + cardCut)
                    stream.closePath()
                    stream.stroke()
                    stream.restoreGraphicsState()
                    // C269: golden card label 6.65 pt in golden grey.
                    val fieldLabelPt = 6.65f
                    require(headerPdfFont.getStringWidth(label) *
                        fieldLabelPt / 1000f <= 154f) {
                        "P4 spellcasting card label overflows frozen width: " + label
                    }
                    stream.setNonStrokingColor(Color(101, 125, 144))
                    stream.beginText()
                    stream.setFont(headerPdfFont, fieldLabelPt)
                    stream.newLineAtOffset(x + 7f, 660.2f) // C322 golden label baseline
                    stream.showText(label)
                    stream.endText()
                    stream.setNonStrokingColor(Color(29, 56, 79))
                    fun centeredX(text: String, pt: Float) = x + (cardWidth -
                        headerPdfFont.getStringWidth(pcSheetV11PrintableText(text)) * pt / 1000f) / 2f
                    if (value == null) {
                        stream.moveTo(x + 7f, 645f)
                        stream.lineTo(x + 160f, 645f)
                        stream.stroke()
                    } else {
                        fun fits(text: String, pt: Float) =
                            headerPdfFont.getStringWidth(pcSheetV11PrintableText(text)) *
                                pt / 1000f <= 152f
                        // C269: golden centered 10.7 pt value, stepping down to 7 pt.
                        val size = listOf(10.7f, 10f, 9f, 8f, 7f).firstOrNull { fits(value, it) }
                        // Long source names: one source per line at 7pt. C322: the
                        // golden 29.77 pt card holds two lines (the owner's P1 rule:
                        // two lines, otherwise SEGÚN FUENTE).
                        val stacked = if (size == null && label in varying)
                            perSourceParts(label)?.takeIf { parts ->
                                parts.size <= 2 && parts.all { fits(it, 7f) }
                            } else null
                        if (stacked != null) {
                            stacked.forEachIndexed { line, text ->
                                stream.beginText()
                                stream.setFont(headerPdfFont, 7f)
                                stream.newLineAtOffset(centeredX(text, 7f), 651.5f - line * 7.5f)
                                stream.showText(pcSheetV11PrintableText(text))
                                stream.endText()
                            }
                        } else {
                            val ink = if (size == null) "SEGÚN FUENTE" else value
                            stream.beginText()
                            stream.setFont(headerPdfFont, size ?: 10.7f)
                            stream.newLineAtOffset(centeredX(ink, size ?: 10.7f), 646.6f) // C322 golden
                            stream.showText(pcSheetV11PrintableText(ink))
                            stream.endText()
                        }
                    }
                }
            }

            // The frozen C2 golden frames each writable NOTES block with a
            // pale-blue clipped cartela, not a floating black heading.
            // Use only the 18pt strip that is already reserved above the
            // complete measured grid; never consume a source-text cell.
            fun drawC2ManuscriptCartela(
                stream: PDPageContentStream, x: Float, width: Float, gridTop: Float,
            ) {
                require(width >= 32f)
                val bottom = gridTop + 1f
                val top = gridTop + 17f
                val cut = 4f
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setLineWidth(0.55f)
                stream.moveTo(x + cut, bottom)
                stream.lineTo(x + width - cut, bottom)
                stream.lineTo(x + width, bottom + cut)
                stream.lineTo(x + width, top - cut)
                stream.lineTo(x + width - cut, top)
                stream.lineTo(x + cut, top)
                stream.lineTo(x, top - cut)
                stream.lineTo(x, bottom + cut)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                // C307: golden C2 NOTAS label, 7.6 pt #1d384f.
                stream.setNonStrokingColor(Color(29, 56, 79))
                drawP1Label(stream, "NOTAS", x + 8f, gridTop + 7f, 7.6f)
                stream.setNonStrokingColor(Color(36, 57, 75))
            }

            // C2 leftover paper is handwriting space, not a new digital-notes section.
            // Occupied PDF line rectangles govern whether a complete square grid fits.
            fun drawUnusedC2NotesGrid(
                stream: PDPageContentStream,
                lowestTextBottom: Float,
            ) {
                val gridBottom = 78f
                val maxGridTop = lowestTextBottom - 28f
                val completeRows = ((maxGridTop - gridBottom) / 16f).toInt()
                if (completeRows < 3) return
                val gridTop = gridBottom + completeRows * 16f
                require(gridTop + 18f < lowestTextBottom) {
                    "C2 notes grid must never overlap character content."
                }
                drawC2ManuscriptCartela(stream, 42f, 528f, gridTop)
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.5f)
                repeat(34) { column ->
                    val x = 42f + column * 16f
                    stream.moveTo(x, gridBottom)
                    stream.lineTo(x, gridTop)
                    stream.stroke()
                }
                repeat(completeRows + 1) { row ->
                    val y = gridBottom + row * 16f
                    stream.moveTo(42f, y)
                    stream.lineTo(570f, y)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
            }

            fun fillUnusedC2WithNotes(
                stream: PDPageContentStream,
                cells: List<io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PlacedLineCell>,
            ) {
                // C2 is two independent vertical paper columns. A nearly
                // occupied LEFT column cannot veto useful blank RIGHT paper.
                val columns = PcSheetV11ColumnGrid.split(pageArea, 2, 12f)
                columns.forEachIndexed { index, column ->
                    val lowestTextBottom = cells.filter { it.columnIndex == index + 1 }
                        .minOfOrNull { it.cellPt.bottomPt } ?: column.topPt
                    val gridBottom = 78f
                    val gridSize = 16f
                    val maxGridTop = lowestTextBottom - 28f
                    val completeRows = ((maxGridTop - gridBottom) / gridSize).toInt()
                    if (completeRows < 3) return@forEachIndexed
                    val gridTop = gridBottom + completeRows * gridSize
                    require(gridTop + 18f < lowestTextBottom) {
                        "Independent C2 manuscript notes may not overlap actual source cells."
                    }
                    val completeColumns = (column.widthPt / gridSize).toInt()
                    require(completeColumns >= 2)
                    val gridWidth = completeColumns * gridSize
                    val gridLeft = column.leftPt + (column.widthPt - gridWidth) / 2f
                    drawC2ManuscriptCartela(stream, gridLeft, gridWidth, gridTop)
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(130, 165, 186))
                    stream.setLineWidth(0.5f)
                    for (x in 0..completeColumns) {
                        val px = gridLeft + x * gridSize
                        stream.moveTo(px, gridBottom)
                        stream.lineTo(px, gridTop)
                        stream.stroke()
                    }
                    for (y in 0..completeRows) {
                        val py = gridBottom + y * gridSize
                        stream.moveTo(gridLeft, py)
                        stream.lineTo(gridLeft + gridWidth, py)
                        stream.stroke()
                    }
                    stream.restoreGraphicsState()
                }
            }

            // The approved P3 has ruled handwriting space beneath narrative,
            // never across paragraphs and never on a mechanically extended C2.
            // It reuses ACTUAL placed font baselines, not guessed character counts.
            fun drawP3HandwritingBelowContent(
                stream: PDPageContentStream,
                cells: List<io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PlacedLineCell>,
            ) {
                val usedBottom = cells.minOfOrNull { it.cellPt.bottomPt } ?: 684f
                val writingBottom = 84f
                val availableTop = minOf(usedBottom - 20f, 668f)
                val completeRows = ((availableTop - writingBottom) / 16f).toInt()
                if (completeRows < 3) return
                val writingTop = writingBottom + completeRows * 16f
                require(writingTop + 18f <= usedBottom) {
                    "P3 handwriting lines cannot overlap real printed narrative."
                }
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.5f)
                for (row in 0..completeRows) {
                    val y = writingBottom + row * 16f
                    stream.moveTo(42f, y)
                    stream.lineTo(570f, y)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
            }


            // P3 is the approved physical paired narrative sheet, not generic
            // unlabelled full-width paragraphs. Source strings retain their IDs
            // and unprinted suffixes for measured two-column P3 continuations.
            val p3Narrative = texts.narrative.associateBy { it.sourceKey }
            val p3FieldSpecs = listOf(
                "TRASFONDO" to listOf("background-name", "background-summary"),
                "RELIGIÓN / FE" to listOf("background-faith"),
                "RASGOS DE PERSONALIDAD" to listOf("personality"),
                "IDEALES" to listOf("ideals"),
                "VÍNCULOS" to listOf("bonds"),
                "DEFECTOS" to listOf("flaws"),
            )
            // C284 (golden P3): each field is four banded 17 pt rows (label + three
            // value rows) in 263.5 pt columns; values Noto Sans 7.3 pt, 4.5 pt above
            // each row foot, 4.5 pt inside the column (v11_extremo p3).
            val p3FieldRowPt = 17f
            val p3FieldFont = measurer.resolve(PcSheetV11Typeface.BODY, 7.3f).let { font ->
                font.copy(metrics = font.metrics.copy(leadingPt = maxOf(0f,
                    p3FieldRowPt - font.metrics.ascentPt - font.metrics.descentPt)))
            }
            val p3FieldLayouts = p3FieldSpecs.mapIndexed { index, (heading, keys) ->
                val x = if (index % 2 == 0) 39.685f else 308.835f
                val top = 670.661f - (index / 2) * 4 * p3FieldRowPt
                val bounds = PcSheetV11RectPt(x, top - 4 * p3FieldRowPt, 263.48f, 4 * p3FieldRowPt)
                val bodyArea = PcSheetV11RectPt(x + 4.5f, bounds.bottomPt,
                    263.48f - 9f, 3 * p3FieldRowPt)
                val entries = keys.mapNotNull { p3Narrative["narrative:" + it] }
                val measured = p3FieldFont.place(
                    PcSheetV11FlowSection.NARRATIVE, entries, bodyArea, 1, 0f,
                )
                Triple(heading, bounds, measured)
            }
            val p3StoryItems = listOfNotNull(p3Narrative["narrative:story"])
            // C305: golden HISTORIA columns 39.69..302.88 and 309.12..572.32 (6.24 pt gutter).
            // C321: golden HISTORIA rows 61.8..435.97 (22 rows of 17 pt per column).
            val p3StoryArea = PcSheetV11RectPt(39.69f, 61.8f, 532.63f, 374.17f)
            val p3StoryGutterPt = 6.24f
            // Owner decision 2026-10-26 (option B): HISTORIA Y CRÓNICA and its
            // continuation follow the approved golden rhythm, Fira 7 pt on
            // 17 pt rows with the baseline 4 pt above each row's foot rule.
            val storyRowPt = 17f
            val storyFont = measurer.resolve(PcSheetV11Typeface.BODY, 7f).let { font ->
                font.copy(metrics = font.metrics.copy(leadingPt =
                    storyRowPt - font.metrics.ascentPt - font.metrics.descentPt))
            }
            require(storyFont.metrics.lineHeightPt == storyRowPt)
            // C260 (owner 2026-10-28: «diseño debe llegar a ser igual … al
            // original»): measured golden body sizes and row pitches per module.
            fun goldenRhythm(sizePt: Float, pitchPt: Float): PcSheetV11ResolvedFont =
                measurer.resolve(PcSheetV11Typeface.BODY, sizePt).let { font ->
                    // C283: a golden row tighter than Noto Sans' full ascent+descent
                    // box (special equipment, 7 pt on 9 pt) keeps the golden pitch;
                    // the box is scaled into the row, as the golden generator does.
                    val box = font.metrics.ascentPt + font.metrics.descentPt
                    val fit = minOf(1f, pitchPt / box)
                    font.copy(metrics = font.metrics.copy(
                        ascentPt = font.metrics.ascentPt * fit,
                        descentPt = font.metrics.descentPt * fit,
                        leadingPt = maxOf(0f, pitchPt - box * fit)))
                }
            val bookFont = goldenRhythm(6.7f, 10.5f)
            val notesFont = goldenRhythm(7f, 11.6f)
            val quickFont = goldenRhythm(6.8f, 15.73f) // C322: golden P4 pitch
            // C262: golden C2 lists, 7.2 pt on 15.7 pt rows; the linked-skill
            // paper box keeps >= 11 pt of measured clearance in that size.
            val c2Font = goldenRhythm(7.2f, 15.7f)
            // C263: golden P2 Equipo / Tesoro / Rasgos, 7.1 pt on 16 pt rows.
            val p2Font = goldenRhythm(7.1f, 16f)
            // C267: golden Equipo Especial text is ~7 pt; lines of a tall entry
            // step 9 pt inside the owner's 25 pt rows (D-0076 §3.21).
            val specialFont = goldenRhythm(7f, 9f)
            // C268: golden C2 Equipo / Tesoro continuation tables, 7.2 pt on 18.4 pt.
            val c2TableFont = goldenRhythm(7.2f, 18.4f)
            val c2Inset = " ".repeat((1..16).first {
                c2Font.width.measurePt(" ".repeat(it)) >= 11f
            })
            // The spell glyph keeps >= 11 pt of measured clearance from its 7 pt
            // paper box in the golden P4 size (five spaces sufficed at 9 pt).
            val quickInset = " ".repeat((1..16).first {
                quickFont.width.measurePt(" ".repeat(it)) >= 11f
            })
            fun storyBaseline(cell: PcSheetV11RectPt): Float = cell.bottomPt + 4f
            fun drawStory(stream: PDPageContentStream, text: String, x: Float, y: Float) {
                if (text.isEmpty()) return
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(36, 57, 75)) // C291: golden value ink
                stream.beginText()
                stream.setFont(bodyPdfFont, 7f)
                stream.newLineAtOffset(x, y)
                stream.showText(pcSheetV11PrintableText(text))
                stream.endText()
                stream.restoreGraphicsState()
            }
            // Golden P3: text sits 4 pt inside each banded column (measured on
            // v11_extremo p3). Bands keep p3StoryArea; text uses the inset area.
            val p3TextInsetPt = 4f
            val p3StoryTextArea = PcSheetV11RectPt(p3StoryArea.leftPt + p3TextInsetPt,
                p3StoryArea.bottomPt, p3StoryArea.widthPt - 2 * p3TextInsetPt, p3StoryArea.heightPt)
            val p3Story = storyFont.place(
                PcSheetV11FlowSection.NARRATIVE, p3StoryItems, p3StoryTextArea, 2,
                p3StoryGutterPt + 2 * p3TextInsetPt,
            )
            fun remainingNarrative(
                original: List<PcSheetV11FlowItem>,
                placed: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PlacedFlow,
            ): List<PcSheetV11FlowItem> = original.mapNotNull { item ->
                val consumed = placed.cells.asSequence()
                    .filter { it.localPageIndex == 1 && it.sourceKey == item.sourceKey }
                    .maxOfOrNull { it.sourceEndExclusive } ?: 0
                require(consumed in 0..item.text.length)
                if (consumed == item.text.length) null
                else item.copy(text = item.text.substring(consumed))
            }
            val p3FieldOverflow = buildList {
                p3FieldLayouts.forEachIndexed { index, (heading, _, flow) ->
                    val keys = p3FieldSpecs[index].second
                    val originals = keys.mapNotNull { p3Narrative["narrative:" + it] }
                    val remaining = remainingNarrative(originals, flow)
                    if (remaining.isNotEmpty()) {
                        add(PcSheetV11FlowItem(
                            "p3-field-heading:" + index, heading + " / CONTINUACIÓN",
                            keepTogetherIfFits = true,
                        ))
                        addAll(remaining)
                    }
                }
            }
            // The story continuation keeps its own 7/17 pt rhythm, so it is a
            // separate measured flow after any profile-field continuation.
            val p3StoryOverflow = buildList {
                val remainingStory = remainingNarrative(p3StoryItems, p3Story)
                if (remainingStory.isNotEmpty()) {
                    add(PcSheetV11FlowItem(
                        "p3-story-heading", "HISTORIA Y CRÓNICA / CONTINUACIÓN",
                        keepTogetherIfFits = true,
                    ))
                    addAll(remainingStory)
                }
            }
            fun drawP3Band(
                stream: PDPageContentStream, label: String, y: Float, height: Float = 20f,
            ) {
                stream.saveGraphicsState()
                // C306: golden P3 cartela - #264b68 0.65 pt edge, x39.69..572.32,
                // diamonds at both ends, 9.1 pt title in ink 16 pt inside.
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.65f)
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.moveTo(45.69f, y)
                stream.lineTo(566.32f, y)
                stream.lineTo(572.32f, y + 6f)
                stream.lineTo(572.32f, y + height - 6f)
                stream.lineTo(566.32f, y + height)
                stream.lineTo(45.69f, y + height)
                stream.lineTo(39.69f, y + height - 6f)
                stream.lineTo(39.69f, y + 6f)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                drawCartelaDiamonds(stream, 39.69f, 572.32f, y, y + height)
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(29, 56, 79))
                // C321: golden title baseline 6.84 pt above a 22.96 pt band.
                drawP1Label(stream, label, 55.69f, y + (if (height > 21f) 6.84f else 6.4f), 9.1f)
                stream.restoreGraphicsState()
            }
            fun p3RuledCells(
                stream: PDPageContentStream,
                rect: PcSheetV11RectPt,
                lineHeight: Float,
            ) {
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(238, 247, 251))
                stream.addRect(rect.leftPt, rect.bottomPt,
                    rect.widthPt, rect.heightPt)
                stream.fill()
                // Golden P3 alternating blue-mist handwriting ROWS.
                // Paper-only fills precede existing measured ruled baselines
                // and source glyphs; no added text/rows/pagination.
                val rows = (rect.heightPt / lineHeight).toInt()
                stream.setNonStrokingColor(Color(231, 242, 247))
                for (row in 0 until rows step 2) {
                    val top = rect.topPt - row * lineHeight
                    val bottom = maxOf(rect.bottomPt, top - lineHeight)
                    if (top > bottom) {
                        stream.addRect(rect.leftPt, bottom, rect.widthPt, top-bottom)
                        stream.fill()
                    }
                }
                stream.setStrokingColor(Color(174, 203, 220))
                stream.setLineWidth(0.35f)
                for (row in 1..rows) {
                    val y = rect.topPt - row * lineHeight
                    if (y >= rect.bottomPt) {
                        stream.moveTo(rect.leftPt, y)
                        stream.lineTo(rect.rightPt, y)
                        stream.stroke()
                    }
                }
                stream.restoreGraphicsState()
            }
            fun drawP3FirstPage(stream: PDPageContentStream) {
                drawP3Band(stream, "TRASFONDO Y PERFIL NARRATIVO", 672.66f, 22.96f) // C321 golden
                p3FieldLayouts.forEachIndexed { index, (heading, rect, placed) ->
                    // C284: golden field paper — alternating bands (the two columns
                    // start on opposite shades) with a 0.32 pt rule 1 pt above each foot.
                    for (row in 0 until 4) {
                        val bottom = rect.topPt - (row + 1) * p3FieldRowPt
                        stream.saveGraphicsState()
                        stream.setNonStrokingColor(if ((row + index % 2) % 2 == 0)
                            Color(231, 242, 247) else Color(240, 247, 250))
                        stream.addRect(rect.leftPt, bottom, rect.widthPt, p3FieldRowPt)
                        stream.fill()
                        stream.setStrokingColor(Color(148, 176, 193))
                        stream.setLineWidth(0.32f)
                        stream.moveTo(rect.leftPt, bottom + 1f)
                        stream.lineTo(rect.rightPt, bottom + 1f)
                        stream.stroke()
                        stream.restoreGraphicsState()
                    }
                    // Golden field label: Noto Sans Display Condensed Bold 7 pt, #1d384f.
                    val labelPt = 7f
                    require(headerPdfFont.getStringWidth(heading) * labelPt / 1000f <=
                        rect.widthPt - 9f) {
                        "P3 narrative cartela label cannot fit without clipping."
                    }
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(29, 56, 79))
                    stream.beginText()
                    stream.setFont(headerPdfFont, labelPt)
                    stream.newLineAtOffset(rect.leftPt + 4.5f, rect.topPt - p3FieldRowPt + 4.5f)
                    stream.showText(heading)
                    stream.endText()
                    stream.setNonStrokingColor(Color(36, 57, 75))
                    placed.cells.filter { it.localPageIndex == 1 }.forEach { cell ->
                        if (cell.drawableText.isNotEmpty()) {
                            stream.beginText()
                            stream.setFont(bodyPdfFont, 7.3f)
                            stream.newLineAtOffset(cell.cellPt.leftPt, cell.cellPt.bottomPt + 4.5f)
                            stream.showText(pcSheetV11PrintableText(cell.drawableText))
                            stream.endText()
                        }
                    }
                    stream.restoreGraphicsState()
                    require(p3FieldSpecs[index].first == heading)
                }
                drawP3Band(stream, "HISTORIA Y CRÓNICA", 437.95f, 22.96f) // C321 golden
                val columns = PcSheetV11ColumnGrid.split(p3StoryArea, 2, p3StoryGutterPt)
                columns.forEach { p3RuledCells(stream, it, storyRowPt) }
                p3Story.cells.filter { it.localPageIndex == 1 }.forEach { cell ->
                    drawStory(stream, cell.drawableText, cell.cellPt.leftPt,
                        storyBaseline(cell.cellPt))
                }
            }
            fun drawP3ContinuationPaper(stream: PDPageContentStream, rowPt: Float) {
                drawP3Band(stream, "TRASFONDO, PERFIL E HISTORIA / CONTINUACIÓN", 680f)
                val columns = PcSheetV11ColumnGrid.split(
                    PcSheetV11RectPt(42f, 76f, 528f, 597f), 2, 12f,
                )
                columns.forEach { p3RuledCells(stream, it, rowPt) }
            }

            // The approved P4 golden places a short, genuine spell origin at
            // the far right of its own row. Long associations MUST continue
            // through the existing measured text flow, never be abbreviated.
            // C294: golden P4 source label, Noto Sans Display Condensed Bold 6 pt.
            val p4SourceFont = measurer.resolve(PcSheetV11Typeface.LABEL, 6f)
            val sourceNames =
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11SpellSourceLabels
                    .forSpells(data.spells)
            // C258 (owner 2026-10-28): one P4 row per spell-source association
            // ("Fire Bolt  Mago", "Fire Bolt  Otro"), adjacent in source order,
            // with the source TYPE label only. No [X]/[ ] mark: the sheet does
            // not print preparation; each row keeps its pencil box.
            val p4SpellRows = data.spells.associate { entry ->
                entry.spell.id to (if (entry.sources.isEmpty())
                    listOf("quick-spell:" + entry.spell.id to "")
                else entry.sources.distinctBy { it.sourceId }.map { association ->
                    "quick-spell:" + entry.spell.id + ":" + association.sourceId to
                        sourceNames.getValue(association.sourceId)
                })
            }
            val p4RightSources = data.spells.flatMap { entry ->
                p4SpellRows.getValue(entry.spell.id).map { (key, sourceInfo) ->
                    Triple(entry, key, sourceInfo)
                }
            }.mapNotNull { (entry, key, sourceInfo) ->
                val fullName = quickInset + entry.spell.name
                // 168pt true 3-column text band with a 12pt glyph gutter;
                // no fake slots and no shrinking long spell/source records.
                if (sourceInfo.isNotBlank() &&
                    // Persisted source names may contain actual CR/LF/TAB. A
                    // fixed physical right lane is one row only; measuring
                    // its multi-line content with PDFBox rejects control
                    // glyphs. Route the entire UUID-backed source through
                    // the existing measured/lossless P4 row instead.
                    // Both the spell NAME and its casting SOURCE must be
                    // physically single-line before entering the fixed P4
                    // right lane. Multiline persisted names belong to the
                    // existing lossless three-column text flow, never a
                    // one-line PDFBox width call (CR/LF have no font glyph).
                    fullName.none { it == '\r' || it == '\n' || it == '\t' } &&
                    sourceInfo.none { it == '\r' || it == '\n' || it == '\t' } &&
                    quickFont.width.measurePt(fullName) +
                    p4SourceFont.width.measurePt(sourceInfo) + 12f <= 167.81f // C309 golden column
                ) key to sourceInfo else null
            }.toMap()

                fun drawC2ContinuationCartela(
                    stream: PDPageContentStream,
                    column: PcSheetV11RectPt,
                ) {
                    // Golden C2c: a genuine column-continuation identity is a
                    // small clipped BLUE band, not loose ink above the source.
                    // The 703..716pt header gap is already empty; source rows
                    // start below 702pt. No pagination/content change.
                    val bottom = 703f
                    val top = 716f
                    require(column.topPt <= 702f && top < 717f && bottom > column.topPt)
                    val cut = 2.5f
                    val left = column.leftPt
                    val right = column.rightPt
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(231, 242, 247))
                    stream.setStrokingColor(Color(80, 122, 151))
                    stream.setLineWidth(0.45f)
                    stream.moveTo(left + cut, bottom)
                    stream.lineTo(right - cut, bottom)
                    stream.lineTo(right, bottom + cut)
                    stream.lineTo(right, top - cut)
                    stream.lineTo(right - cut, top)
                    stream.lineTo(left + cut, top)
                    stream.lineTo(left, top - cut)
                    stream.lineTo(left, bottom + cut)
                    stream.closePath()
                    stream.fillAndStroke()
                    stream.restoreGraphicsState()
                }

            // C247: golden C2 "ATRIBUTOS Y HABILIDADES" row inside the item's two
            // measured rows: chamfered score box, modifier oval, Barlow name,
            // SALVACIÓN label + paper box and the real save value on the right.
            fun drawC2AttributeRow(
                stream: PDPageContentStream,
                cell: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PlacedLineCell,
            ) {
                val index = requireNotNull(cell.sourceKey.removePrefix("attribute:").toIntOrNull())
                val attribute = requireNotNull(data.attributes.drop(6).getOrNull(index)) {
                    "C2 custom ability row has no persisted source."
                }
                val left = cell.cellPt.leftPt
                val right = cell.cellPt.rightPt
                val top = cell.cellPt.topPt
                val bottom = top - 2f * body.metrics.lineHeightPt
                fillBand(stream, left, bottom, right - left, top - bottom, 0)
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(80, 122, 151))
                stream.setNonStrokingColor(Color.WHITE)
                stream.setLineWidth(0.55f)
                val boxLeft = left + 3f
                val boxRight = left + 37f
                val boxBottom = bottom + 2.5f
                val boxTop = top - 2.5f
                val cut = 3f
                stream.moveTo(boxLeft + cut, boxBottom)
                stream.lineTo(boxRight - cut, boxBottom)
                stream.lineTo(boxRight, boxBottom + cut)
                stream.lineTo(boxRight, boxTop - cut)
                stream.lineTo(boxRight - cut, boxTop)
                stream.lineTo(boxLeft + cut, boxTop)
                stream.lineTo(boxLeft, boxTop - cut)
                stream.lineTo(boxLeft, boxBottom + cut)
                stream.closePath()
                stream.fillAndStroke()
                val ovalCx = left + 56f
                val ovalCy = (boxTop + boxBottom) / 2f
                val rx = 16f
                val ry = 7f
                val k = 0.5523f
                stream.moveTo(ovalCx + rx, ovalCy)
                stream.curveTo(ovalCx + rx, ovalCy + k * ry, ovalCx + k * rx, ovalCy + ry, ovalCx, ovalCy + ry)
                stream.curveTo(ovalCx - k * rx, ovalCy + ry, ovalCx - rx, ovalCy + k * ry, ovalCx - rx, ovalCy)
                stream.curveTo(ovalCx - rx, ovalCy - k * ry, ovalCx - k * rx, ovalCy - ry, ovalCx, ovalCy - ry)
                stream.curveTo(ovalCx + k * rx, ovalCy - ry, ovalCx + rx, ovalCy - k * ry, ovalCx + rx, ovalCy)
                stream.closePath()
                stream.fillAndStroke()
                val saving = attribute.savingThrowTotal?.let { signedC2(it) } ?: "sin valor"
                // The paper save box always sits 6 pt left of the real value.
                val saveBox = minOf(right - 34f, right - 3f - body.width.measurePt(saving) - 13f)
                stream.addRect(saveBox, ovalCy - 3.5f, 7f, 7f)
                stream.stroke()
                stream.restoreGraphicsState()
                val score = attribute.score.toString()
                val modifier = signedC2(attribute.modifier)
                val valueBaseline = ovalCy - 3f
                // C302: golden C2 attribute numbers in Noto Sans Bold, as on P1.
                p1Bold(stream, score, (boxLeft + boxRight) / 2f - p1BoldWidth(score, 11f) / 2f,
                    valueBaseline, 11f, Color(29, 56, 79))
                p1Bold(stream, modifier, ovalCx - p1BoldWidth(modifier, 8.7f) / 2f, valueBaseline,
                    8.7f, Color(38, 75, 104))
                drawP1Label(stream, attribute.name, left + 78f, top - 10f, 9f)
                drawP1Label(stream, "SALVACIÓN", saveBox - 6f -
                    headerPdfFont.getStringWidth("SALVACIÓN") * 6f / 1000f, bottom + 4f, 6f)
                p1Bold(stream, saving, right - 3f - p1BoldWidth(saving, 7.2f), valueBaseline,
                    7.2f, Color(38, 75, 104))
            }

            fun addSection(
                title: String,
                section: PcSheetV11FlowSection,
                entries: List<PcSheetV11FlowItem>,
                columns: Int,
                alwaysPage: Boolean = false,
                area: PcSheetV11RectPt = pageArea,
                specialMarks: Map<String, Boolean?> = emptyMap(),
                ammunitionTracks: Map<String, Int> = emptyMap(),
                maxPhysicalPages: Int? = null,
                font: PcSheetV11ResolvedFont = body,
                firstPageNumber: Int = 1,
                gutterPt: Float = if (columns == 1) 0f else 12f,
            ) {
                require(maxPhysicalPages == null || maxPhysicalPages > 0)
                if (!alwaysPage && entries.isEmpty()) return
                // C260: a measured section measures AND draws in its own font.
                val body = font
                fun draw(stream: PDPageContentStream, text: String, x: Float, y: Float, label: Boolean) {
                    if (text.isEmpty()) return
                    // C291: golden value ink #24394b for body text; labels keep their ink.
                    if (!label) {
                        stream.saveGraphicsState()
                        stream.setNonStrokingColor(Color(36, 57, 75))
                    }
                    stream.beginText()
                    stream.setFont(if (label) headerPdfFont else bodyPdfFont,
                        if (label) 12f else font.metrics.fontSizePt)
                    stream.newLineAtOffset(x, y)
                    stream.showText(pcSheetV11PrintableText(text))
                    stream.endText()
                    if (!label) stream.restoreGraphicsState()
                }
                val flow = font.place(section, entries, area, columns, gutterPt)
                val pageCount = maxOf(if (alwaysPage) 1 else 0, flow.sourceFlow.localPageCount)
                repeat(minOf(pageCount, maxPhysicalPages ?: pageCount)) { index ->
                    val pageCells = flow.cells.filter {
                        it.localPageIndex == index + 1
                    }
                    // A note card can span physical C-N3 pages. Keep the
                    // persisted title (or stable source ID) visible on its
                    // NEXT page, instead of leaving owner-visible paragraphs
                    // without a note identity. No source text is duplicated.
                    val basicTitle = title + " / " + (index + firstPageNumber)
                    // P3 stories and profile fields can span multiple C2-style
                    // flow pages. The first page carries its original heading;
                    // later physical pages must retain that SAME source-backed
                    // narrative identity in the free header, not merely say P3.
                    // No paragraph, cell, source offset or page boundary changes.
                    val pageTitle = if (title == "P3 - Narrativa / continuación" &&
                        index + firstPageNumber > 1
                    ) {
                        val firstCell = pageCells.minWithOrNull(
                            compareBy({ it.columnIndex }, { it.rowIndex })
                        )
                        val sourcePosition = firstCell?.let { first ->
                            entries.indexOfFirst { it.sourceKey == first.sourceKey }
                        } ?: -1
                        val heading = if (sourcePosition >= 0 &&
                            firstCell?.sourceKey?.startsWith("narrative:") == true
                        ) {
                            entries.take(sourcePosition).lastOrNull {
                                it.sourceKey == "p3-story-heading" ||
                                    it.sourceKey.startsWith("p3-field-heading:")
                            }?.text
                        } else null
                        if (heading == null) basicTitle else {
                            fun fits(value: String): Boolean =
                                headerPdfFont.getStringWidth(
                                    pcSheetV11PrintableText(value)
                                ) * 12f / 1000f <= 524f
                            val full = basicTitle + " / " + heading
                            val short = "P3 / " + (index + 1) + " / " + heading
                            when {
                                fits(full) -> full
                                fits(short) -> short
                                else -> error("P3 continuation must retain its real source heading")
                            }
                        }
                    } else if (title == "C-N3 - Notas digitales" &&
                        pageCells.firstOrNull()?.sourceStartOffset?.let { it > 0 } == true
                    ) {
                        val sourceKey = requireNotNull(pageCells.firstOrNull()).sourceKey
                        val note = data.digitalNotes.firstOrNull {
                            "digital-note:" + (it.sourceId?.toString() ?: "general") ==
                                sourceKey
                        }
                        io.github.mrsimkin.dndcustomaid.shared.character
                            .PcSheetV11NoteContinuationHeader.choose(
                                baseTitle = basicTitle,
                                sourceTitle = note?.title ?: (note?.sourceId?.toString()
                                    ?: "NOTAS GENERALES"),
                                stableId = note?.sourceId?.toString() ?: "GENERAL",
                                fromPage = pdf.numberOfPages,
                                fits = { value ->
                                    headerPdfFont.getStringWidth(
                                        pcSheetV11PrintableText(value)
                                    ) * 12f / 1000f <= 524f
                                },
                            )
                    } else basicTitle
                    val page = PDPage(PDRectangle.LETTER)
                    pdf.addPage(page)
                    physicalSectionKeys += title
                    if (title == "C2 - Habilidades, acciones y recursos") {
                        pageCells.filter { it.sourceKey.startsWith("c2-heading:") }.forEach {
                            mixedC2HeadingPages.putIfAbsent(it.sourceKey, pdf.numberOfPages)
                        }
                    }
                    if (title == "P3 - Narrativa / continuación") {
                        pageCells.filter { it.sourceKey.startsWith("p3-field-heading:") }.forEach {
                            p3FieldHeadingPages.putIfAbsent(it.sourceKey, pdf.numberOfPages)
                        }
                    }
                    PDPageContentStream(pdf, page).use { stream ->
                        drawFrameAndHeader(stream, pageTitle)
                        if (title.startsWith("P4 - ")) drawP4MagicReference(stream)
                        if (title.startsWith("P3 - Narrativa / continuación")) {
                            drawP3ContinuationPaper(stream, font.metrics.lineHeightPt)
                        }
                        if (title.startsWith("P1 - ") && index == 0) {
                            drawP1Attributes(stream)
                            drawP1Attacks(stream)
                            drawP1Spellcasting(stream)
                            drawP1Traits(stream)
                            drawP1Ammunition(stream)
                            drawP1Portrait(stream)
                            drawP1IdentityFields(stream)
                            drawP1CombatReference(stream)
                            drawP1GroupedSkills(stream)
                            drawP1BIndependentSkills(stream)
                        }
                        // A level heading is printed once in the source stream.
                        // When the SAME level crosses a P4 physical COLUMN
                        // (including a new page), each continued column gets
                        // source-backed context in the free 10pt header gap:
                        // above the ledger (top 607) and below cards (bottom 617).
                        // Never move/duplicate real rows or invent level headings.
                        if (title.startsWith("P4 - ")) {
                            flow.columnsPt.forEachIndexed { columnZero, column ->
                                val firstCell = pageCells
                                    .filter { it.columnIndex == columnZero + 1 }
                                    .minByOrNull { it.rowIndex }
                                if (firstCell != null &&
                                    !firstCell.sourceKey.startsWith("spell-level:")) {
                                    val firstIndex = entries.indexOfFirst {
                                        it.sourceKey == firstCell.sourceKey
                                    }
                                    require(firstIndex >= 0)
                                    val level = entries.take(firstIndex)
                                        .lastOrNull {
                                            it.sourceKey.startsWith("spell-level:")
                                        }?.sourceKey?.removePrefix("spell-level:")
                                        ?.toIntOrNull()
                                    if (level != null) {
                                        // C330 golden «TRUCOS 0 / CONTINUACIÓN»; C341: a
                                        // level arriving from another page names it.
                                        val context = PcSheetV11ContinuationText.arrived(
                                            if (level == 0) "TRUCOS 0" else "NIVEL " + level,
                                            if (columnZero == 0 && index > 0) pdf.numberOfPages - 1 else null,
                                        )
                                        require(p4SourceFont.width.measurePt(context) <=
                                            column.widthPt - 4f) {
                                            "P4 physical column continuation label overflows."
                                        }
                                        // True level continues into this physical
                                        // column. The approved P4 grammar uses
                                        // BLUE LEVEL BANDS, not floating labels.
                                        // Reserve only the EXISTING empty 10pt
                                        // gap below cards and above printed ledger;
                                        // no source cell/row/page is relocated.
                                        // C322: the gap between the golden column top
                                        // (y630.43) and the card bottom (y640.91).
                                        val bottom = column.topPt + 0.3f
                                        val top = column.topPt + 9.3f
                                        val left = column.leftPt
                                        val right = left + column.widthPt
                                        val cut = 2f
                                        require(bottom > column.topPt && top < 640.91f)
                                        stream.saveGraphicsState()
                                        stream.setNonStrokingColor(Color(231, 242, 247))
                                        stream.setStrokingColor(Color(80, 122, 151))
                                        stream.setLineWidth(0.45f)
                                        stream.moveTo(left + cut, bottom)
                                        stream.lineTo(right - cut, bottom)
                                        stream.lineTo(right, bottom + cut)
                                        stream.lineTo(right, top - cut)
                                        stream.lineTo(right - cut, top)
                                        stream.lineTo(left + cut, top)
                                        stream.lineTo(left, top - cut)
                                        stream.lineTo(left, bottom + cut)
                                        stream.closePath()
                                        stream.fillAndStroke()
                                        stream.restoreGraphicsState()
                                        stream.beginText()
                                        stream.setFont(bodyPdfFont, 7f)
                                        stream.newLineAtOffset(column.leftPt + 2f, column.topPt + 3f)
                                        stream.showText(pcSheetV11PrintableText(context))
                                        stream.endText()
                                    }
                                }
                            }
                        }
                        if (title == "Libro de conjuros") {
                            // D-0076 §3.48 / BK05: a taller-than-column card
                            // may continue losslessly, but each NEW physical
                            // column must identify the SAME persisted spell.
                            // All fragments start at line 0 in the next column.
                            // Put the source-free sign within the real reserved
                            // 15pt band BELOW header rule y717 and ABOVE flow
                            // top y702: never spend a description line.
                            val namesByKey = entries.associate {
                                it.sourceKey to it.text.substringBefore('\n')
                            }
                            val continuationParts = flow.sourceFlow.fragments.filter {
                                it.localPageIndex == index + 1 &&
                                    it.continuedFromPrevious
                            }
                            val actualColumns = flow.columnsPt
                            continuationParts.forEach { fragment ->
                                require(fragment.startLineIndexInColumn == 0) {
                                    "Book continuation must begin at a genuine column top."
                                }
                                val col = actualColumns[fragment.columnIndex - 1]
                                require(col.topPt == 702f) {
                                    "V11 book continuation label needs the source-free header band."
                                }
                                val realName = namesByKey.getValue(fragment.sourceKey)
                                val titleText = "CONTINUACIÓN: " + realName
                                val fontSize = 7f
                                val width = bodyPdfFont.getStringWidth(pcSheetV11PrintableText(titleText)) *
                                    fontSize / 1000f
                                val printed = if (width <= col.widthPt - 6f) titleText
                                    else "CONTINUACIÓN (VER ÍNDICE)"
                                require(bodyPdfFont.getStringWidth(pcSheetV11PrintableText(printed)) *
                                    fontSize / 1000f <= col.widthPt - 6f)
                                stream.saveGraphicsState()
                                stream.setNonStrokingColor(Color(80, 122, 151))
                                stream.beginText()
                                stream.setFont(bodyPdfFont, fontSize)
                                stream.newLineAtOffset(col.leftPt + 2f, col.topPt + 5f)
                                stream.showText(pcSheetV11PrintableText(printed))
                                stream.endText()
                                stream.restoreGraphicsState()
                            }
                        }
                        if (title == "C2 - Habilidades, acciones y recursos") {
                            // When a GENUINE custom attribute and linked skills
                            // cannot fit a whole column, source-backed context
                            // accompanies each following physical column.
                            // It lives in the reserved 702..717pt header gap:
                            // no duplicated attribute score/ID, no row moved,
                            // no invented state, no new page or reduced font.
                            flow.columnsPt.forEachIndexed { columnZero, column ->
                                val firstCell = pageCells
                                    .filter { it.columnIndex == columnZero + 1 }
                                    .minByOrNull { it.rowIndex }
                                val group = entries.firstOrNull {
                                    it.sourceKey == firstCell?.sourceKey
                                }?.groupKey
                                val attrIndex = group
                                    ?.removePrefix("custom-attribute-group:")
                                    ?.toIntOrNull()
                                // A source group can cross a PHYSICAL C2 column after its blue
                                // cartela was printed in the preceding column. Repeat only
                                // its true group identity in the existing free header band,
                                // never a source row, UUID, count or invented game state.
                                // Custom attributes have their own source-specific caption.
                                if (attrIndex == null && firstCell != null &&
                                    !firstCell.sourceKey.startsWith("c2-heading:")) {
                                    val sourcePosition = entries.indexOfFirst {
                                        it.sourceKey == firstCell.sourceKey
                                    }
                                    val precedingHeading = if (sourcePosition > 0)
                                        entries.take(sourcePosition).lastOrNull {
                                            it.sourceKey.startsWith("c2-heading:")
                                        } else null
                                    if (precedingHeading != null) {
                                        val fontPt = 7f
                                        val shortName = precedingHeading.text
                                            .substringBefore(" / ").substringBefore(",")
                                        // C341: the first column continues the previous
                                        // physical page; a later column the one beside it.
                                        val fromPage = if (columnZero == 0)
                                            pdf.numberOfPages - 1 else null
                                        val context = listOf(
                                            PcSheetV11ContinuationText.arrived(precedingHeading.text, fromPage),
                                            PcSheetV11ContinuationText.arrived(shortName, fromPage),
                                            shortName,
                                        ).firstOrNull { candidate ->
                                            bodyPdfFont.getStringWidth(
                                                pcSheetV11PrintableText(candidate)
                                            ) * fontPt / 1000f <= column.widthPt - 4f
                                        }
                                        requireNotNull(context) {
                                            "C2 source group continuation cannot fit the existing header band."
                                        }
                                        drawC2ContinuationCartela(stream, column)
                                        stream.saveGraphicsState()
                                        stream.setNonStrokingColor(Color(80, 122, 151))
                                        stream.beginText()
                                        stream.setFont(bodyPdfFont, fontPt)
                                        stream.newLineAtOffset(column.leftPt + 2f, 707f)
                                        stream.showText(pcSheetV11PrintableText(context))
                                        stream.endText()
                                        stream.restoreGraphicsState()
                                    }
                                }
                                if (attrIndex != null) {
                                    val original = flow.cells.firstOrNull {
                                        it.sourceKey == "attribute:" + attrIndex
                                    }
                                    if (original != null &&
                                        (original.localPageIndex != index + 1 ||
                                            original.columnIndex != columnZero + 1)
                                    ) {
                                        val attribute = requireNotNull(
                                            data.attributes.drop(6).getOrNull(attrIndex)
                                        ) { "C2 continuation lost its source attribute." }
                                        val fontPt = 7f
                                        fun fits(text: String): Boolean =
                                            bodyPdfFont.getStringWidth(
                                                pcSheetV11PrintableText(text)
                                            ) * fontPt / 1000f <= column.widthPt - 4f
                                        val full = "ATRIBUTO: " + attribute.name
                                        val short = attribute.abbreviation
                                            .takeIf { it.isNotBlank() }
                                            ?.let {
                                                "ATRIBUTO: " +
                                                    approvedAbilityAbbreviation(it)
                                            }
                                        val sourceId = attribute.reference.customAttributeId
                                            ?.toString()?.let { "ID: " + it }
                                        // C341: «VIENE DE PÁG. n» when it leaves a page.
                                        val fromPage = if (columnZero == 0)
                                            pdf.numberOfPages - 1 else null
                                        val identities = listOfNotNull(full, short, sourceId)
                                        val context = (identities.map {
                                            PcSheetV11ContinuationText.arrived(it, fromPage)
                                        } + identities).firstOrNull { fits(it) }
                                        requireNotNull(context) {
                                            "C2 source attribute continuation cannot fit the " +
                                                "existing physical header band."
                                        }
                                        drawC2ContinuationCartela(stream, column)
                                        stream.saveGraphicsState()
                                        stream.setNonStrokingColor(Color(80, 122, 151))
                                        stream.beginText()
                                        stream.setFont(bodyPdfFont, fontPt)
                                        stream.newLineAtOffset(column.leftPt + 2f, 707f)
                                        stream.showText(pcSheetV11PrintableText(context))
                                        stream.endText()
                                        stream.restoreGraphicsState()
                                    }
                                }
                            }
                            val fromP2 = pageCells.any {
                                it.sourceKey.startsWith("p2-trait:") ||
                                    it.sourceKey == "c2-heading:traits"
                            }
                            val fromP1 = pageCells.any {
                                !it.sourceKey.startsWith("p2-trait:") &&
                                    it.sourceKey != "c2-heading:traits"
                            }
                            if (fromP2) mixedC2TraitPages += pdf.numberOfPages
                            if (fromP1) mixedC2P1Pages += pdf.numberOfPages
                        }
                        // Golden P4 uses quiet blue level bands and ruled writing
                        // rows, not free-floating glyphs over a blank page.
                        if (title.startsWith("P4 - ")) {
                            stream.saveGraphicsState()
                            // C330: golden P4 rows - every band #e7f2f7 with a #94b0c1
                            // 0.32 pt rule 0.99 pt above its foot.
                            stream.setStrokingColor(Color(148, 176, 193))
                            stream.setLineWidth(0.32f)
                            pageCells.forEach { cell ->
                                if (cell.sourceKey.startsWith("spell-level:") ||
                                    cell.sourceKey.startsWith("spell-handwritten:") ||
                                    cell.sourceKey.startsWith("quick-spell:")) {
                                    stream.setNonStrokingColor(Color(231, 242, 247))
                                    stream.addRect(cell.cellPt.leftPt, cell.cellPt.bottomPt,
                                        cell.cellPt.widthPt, cell.cellPt.heightPt)
                                    stream.fill()
                                    stream.moveTo(cell.cellPt.leftPt, cell.cellPt.bottomPt + 0.99f)
                                    stream.lineTo(cell.cellPt.rightPt, cell.cellPt.bottomPt + 0.99f)
                                    stream.stroke()
                                }
                            }
                            stream.restoreGraphicsState()
                        }
                        // C-N3 source cards in the genuine Mara PDF were
                        // visibly glued together: the last text line of one
                        // record touched the next card title. A thin blue-mist
                        // START band and top rule give each persisted note its
                        // own physical anchor, without inserting fake text,
                        // changing word coordinates, or consuming extra pages.
                        // A continued record has no new start band: its real
                        // source identity is instead in this page's header.
                        if (title == "C-N3 - Notas digitales") {
                            // C238: golden C-N3 — a NOTAS cartela in the free band
                            // below the header rule (703..716) and each persisted
                            // note framed as its own card (one blank line apart).
                            // C313: golden NOTAS cartela at the golden first-cartela band
                            // (y672.66..695.62, x39.69..572.32); notes start under it at 670.68.
                            run {
                                val l = 39.69f
                                val r = 572.32f
                                val b = 672.66f
                                val t = 695.62f
                                val cut = 6f
                                stream.saveGraphicsState()
                                stream.setNonStrokingColor(Color(231, 242, 247))
                                stream.setStrokingColor(Color(38, 75, 104))
                                stream.setLineWidth(0.65f)
                                stream.moveTo(l + cut, b)
                                stream.lineTo(r - cut, b)
                                stream.lineTo(r, b + cut)
                                stream.lineTo(r, t - cut)
                                stream.lineTo(r - cut, t)
                                stream.lineTo(l + cut, t)
                                stream.lineTo(l, t - cut)
                                stream.lineTo(l, b + cut)
                                stream.closePath()
                                stream.fillAndStroke()
                                stream.restoreGraphicsState()
                                drawCartelaDiamonds(stream, l, r, b, t)
                            }
                            stream.setNonStrokingColor(Color(29, 56, 79))
                            stream.beginText()
                            stream.setFont(headerPdfFont, 9.1f)
                            stream.newLineAtOffset(55.69f, 679.5f)
                            stream.showText("NOTAS")
                            stream.endText()
                            val scope = "solo notas digitales"
                            stream.setNonStrokingColor(Color(101, 125, 144))
                            stream.beginText()
                            stream.setFont(bodyPdfFont, 6.2f)
                            stream.newLineAtOffset(572.32f - 18.4f -
                                bodyPdfFont.getStringWidth(scope) * 6.2f / 1000f, 680.7f)
                            stream.showText(scope)
                            stream.endText()
                            stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                            // C296: golden note card - chamfered #264b68 0.65 pt frame
                            // (x39.69..572.32) with a pale inner rule 3.4 pt under its top.
                            stream.saveGraphicsState()
                            pageCells.filter { it.sourceKey.startsWith("digital-note:") }
                                .groupBy { it.sourceKey }.values.forEach { cells ->
                                    val top = cells.maxOf { it.cellPt.topPt }
                                    val bottom = cells.minOf { it.cellPt.bottomPt } - 3f
                                    val left = 39.69f
                                    val right = 572.32f
                                    val cut = minOf(6.24f, (top - bottom) / 4f)
                                    stream.setStrokingColor(Color(38, 75, 104))
                                    stream.setLineWidth(0.65f)
                                    stream.moveTo(left + cut, top)
                                    stream.lineTo(right - cut, top)
                                    stream.lineTo(right, top - cut)
                                    stream.lineTo(right, bottom + cut)
                                    stream.lineTo(right - cut, bottom)
                                    stream.lineTo(left + cut, bottom)
                                    stream.lineTo(left, bottom + cut)
                                    stream.lineTo(left, top - cut)
                                    stream.closePath()
                                    stream.stroke()
                                    stream.setStrokingColor(Color(191, 211, 222))
                                    stream.setLineWidth(0.35f)
                                    stream.moveTo(left + 9.07f, top - 3.4f)
                                    stream.lineTo(right - 9.08f, top - 3.4f)
                                    stream.stroke()
                                }
                            stream.restoreGraphicsState()
                            val starts = pageCells.filter {
                                it.sourceKey.startsWith("digital-note:") &&
                                    it.sourceStartOffset == 0
                            }
                            // C296: no title band in the golden; a thin rule 6.24 pt
                            // under each note title's baseline instead.
                            stream.saveGraphicsState()
                            stream.setStrokingColor(Color(148, 176, 193))
                            stream.setLineWidth(0.32f)
                            // C333: only a real title gets the rule under it.
                            starts.filter { it.sourceKey in titledNoteKeys }.forEach { cell ->
                                val titleDrop = 6.47f
                                val ruleY = body.metrics.baselineIn(cell.cellPt) - titleDrop - 6.24f
                                stream.moveTo(pageArea.leftPt + 6.19f, ruleY)
                                stream.lineTo(pageArea.rightPt - 8.74f, ruleY)
                                stream.stroke()
                            }
                            stream.restoreGraphicsState()
                        }
                        // CN304: a digital C-N3 record is never replaced by paper
                        // notes. Only unused floor below the LAST measured source
                        // line can carry secondary handwriting rules. No extra
                        // page, caption, fictitious record or pagination offset.
                        if (title == "C-N3 - Notas digitales" && pageCells.isNotEmpty()) {
                            val lastSourceBottom = pageCells.minOf { it.cellPt.bottomPt }
                            val ruleBottom = 92f // above the real page footer
                            val rulePitch = 16f
                            val ruleTopLimit = lastSourceBottom - 24f
                            val fullRules = ((ruleTopLimit - ruleBottom) / rulePitch).toInt() + 1
                            if (fullRules >= 4) {
                                require(ruleBottom + (fullRules - 1) * rulePitch <=
                                    lastSourceBottom - 24f) {
                                    "C-N3 manual reserve overlaps genuine note content."
                                }
                                stream.saveGraphicsState()
                                stream.setStrokingColor(Color(174, 203, 220))
                                stream.setLineWidth(0.35f)
                                repeat(fullRules) { row ->
                                    val y = ruleBottom + row * rulePitch
                                    stream.moveTo(42f, y)
                                    stream.lineTo(570f, y)
                                    stream.stroke()
                                }
                                stream.restoreGraphicsState()
                                // C238: golden "NOTAS MANUSCRITAS" cartela just above
                                // the handwriting rules, inside the reserved 24 pt gap.
                                val topRule = ruleBottom + (fullRules - 1) * rulePitch
                                val band = PcSheetV11RectPt(42f, topRule + 3f, 528f, 13f)
                                require(band.topPt <= lastSourceBottom - 6f)
                                stream.saveGraphicsState()
                                stream.setNonStrokingColor(Color(231, 242, 247))
                                stream.setStrokingColor(Color(80, 122, 151))
                                stream.setLineWidth(0.45f)
                                stream.addRect(band.leftPt, band.bottomPt, band.widthPt, band.heightPt)
                                stream.fillAndStroke()
                                stream.restoreGraphicsState()
                                stream.beginText()
                                stream.setFont(headerPdfFont, 9f)
                                stream.newLineAtOffset(band.leftPt + 6f, band.bottomPt + 3.5f)
                                stream.showText("NOTAS MANUSCRITAS")
                                stream.endText()
                            }
                        }
                        // Mixed C2 must preserve the approved blue interior
                        // cartela language for each genuine source-group heading.
                        // Paint ONLY within its already-measured cell: this
                        // cannot consume paper, move source text, or affect the
                        // two-column paginator and manuscript NOTAS geometry.
                        if (title == "C2 - Habilidades, acciones y recursos") {
                            // C332: golden C2 lists are banded rows (#e7f2f7 / #f0f7fa with
                            // a #94b0c1 0.32 pt rule): one colour per source record, so a
                            // wrapped record reads as one row; its rule closes the record.
                            // Attribute cards and the ATAQUES table keep their own bands.
                            val inkedRecords = pageCells.filter { it.drawableText.isNotBlank() }
                                .map { it.sourceKey }.toSet()
                            val listCells = pageCells.filter { c ->
                                // Blank lines of an inked record (resource tracks) stay in it.
                                c.sourceKey in inkedRecords &&
                                    !c.sourceKey.startsWith("c2-heading:") &&
                                    !c.sourceKey.startsWith("attribute") &&
                                    !c.sourceKey.startsWith("custom-attribute-group:") &&
                                    c.sourceKey != "action-head" &&
                                    c.sourceKey !in c2AttributeCards &&
                                    c.sourceKey !in c2AttackTableRows
                            }
                            val records = listCells.map { it.sourceKey }.distinct()
                            listCells.groupBy { it.sourceKey to it.columnIndex }.forEach { (key, cells) ->
                                val index = records.indexOf(key.first)
                                stream.saveGraphicsState()
                                stream.setNonStrokingColor(
                                    if (index % 2 == 0) Color(231, 242, 247) else Color(240, 247, 250))
                                cells.forEach { c ->
                                    stream.addRect(c.cellPt.leftPt, c.cellPt.bottomPt,
                                        c.cellPt.widthPt, c.cellPt.heightPt)
                                    stream.fill()
                                }
                                val foot = cells.minBy { it.cellPt.bottomPt }.cellPt
                                stream.setStrokingColor(Color(148, 176, 193))
                                stream.setLineWidth(0.32f)
                                stream.moveTo(foot.leftPt, foot.bottomPt + 0.2f)
                                stream.lineTo(foot.rightPt, foot.bottomPt + 0.2f)
                                stream.stroke()
                                stream.restoreGraphicsState()
                            }
                            val groupHeadings = pageCells.filter {
                                it.sourceKey.startsWith("c2-heading:")
                            }
                            stream.saveGraphicsState()
                            stream.setNonStrokingColor(Color(231, 242, 247))
                            stream.setStrokingColor(Color(80, 122, 151))
                            stream.setLineWidth(0.55f)
                            groupHeadings.forEach { heading ->
                                val cell = heading.cellPt
                                val cut = minOf(3f, cell.heightPt / 4f)
                                stream.moveTo(cell.leftPt + cut, cell.bottomPt)
                                stream.lineTo(cell.rightPt - cut, cell.bottomPt)
                                stream.lineTo(cell.rightPt, cell.bottomPt + cut)
                                stream.lineTo(cell.rightPt, cell.topPt - cut)
                                stream.lineTo(cell.rightPt - cut, cell.topPt)
                                stream.lineTo(cell.leftPt + cut, cell.topPt)
                                stream.lineTo(cell.leftPt, cell.topPt - cut)
                                stream.lineTo(cell.leftPt, cell.bottomPt + cut)
                                stream.closePath()
                                stream.fillAndStroke()
                            }
                            stream.restoreGraphicsState()
                            // C248: golden C2 cartelas carry a small "VIENE P. n"
                            // with the real base page of the module, when it fits.
                            groupHeadings.filter { it.sourceStartOffset == 0 }.forEach { heading ->
                                val base = if (heading.sourceKey == "c2-heading:traits")
                                    physicalSectionKeys.indexOf("P2 - Equipo y tesoro") + 1
                                else physicalSectionKeys.indexOfFirst { it.startsWith("P1 - ") } + 1
                                if (base > 0) {
                                    val origin = pcSheetV11PrintableText(
                                        PcSheetV11ContinuationText.comesFrom(base)) // C341
                                    val originWidth = headerPdfFont.getStringWidth(origin) * 6.1f / 1000f
                                    stream.setNonStrokingColor(Color(38, 75, 104)) // C295: golden ink
                                    val inkWidth = headerPdfFont.getStringWidth(
                                        pcSheetV11PrintableText(heading.drawableText)) * 9f / 1000f
                                    if (c2CartelaGoldenFits(heading.drawableText, heading.cellPt.widthPt)) {
                                        drawCartelaDiamonds(stream, heading.cellPt.leftPt,
                                            heading.cellPt.rightPt, heading.cellPt.bottomPt,
                                            heading.cellPt.topPt)
                                        drawP1Label(stream, origin,
                                            heading.cellPt.rightPt - 13f - originWidth,
                                            body.metrics.baselineIn(heading.cellPt), 6.1f)
                                    } else if (5f + inkWidth + 8f + originWidth + 5f <= heading.cellPt.widthPt) {
                                        drawP1Label(stream, origin,
                                            heading.cellPt.rightPt - 5f - originWidth,
                                            body.metrics.baselineIn(heading.cellPt), 6.1f)
                                    }
                                    stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                                }
                            }
                        }
                        // C213: actual persisted custom attribute/skill grouping.
                        // Paint only inside measured source cells; never alter
                        // text, IDs, wrapping, source order or physical pages.
                        if (title == "C2 - Habilidades, acciones y recursos") {
                            val c2GroupKeys = entries.associate { it.sourceKey to it.groupKey }
                            val c2CustomGroups = pageCells.mapNotNull { cell ->
                                val sourceGroup = c2GroupKeys[cell.sourceKey]
                                if (sourceGroup?.startsWith("custom-attribute-group:") == true)
                                    sourceGroup to cell else null
                            }.groupBy { (group, cell) -> group to cell.columnIndex }
                            c2CustomGroups.forEach { (identity, grouped) ->
                                val column = flow.columnsPt[identity.second - 1]
                                val realCells = grouped.map { it.second }
                                require(realCells.all { column.contains(it.cellPt) })
                                val groupTop = realCells.maxOf { it.cellPt.topPt }
                                val groupBottom = realCells.minOf { it.cellPt.bottomPt }
                                require(groupTop - groupBottom >= body.metrics.lineHeightPt - 0.1f)
                                val linkedKeys = entries.filter {
                                    it.groupKey == identity.first &&
                                        it.sourceKey.startsWith("attribute-skill:")
                                }.map { it.sourceKey }
                                stream.saveGraphicsState()
                                // Alternate rows by real skill identity: wrapped
                                // fragments retain their parent band's parity.
                                stream.setNonStrokingColor(Color(231, 242, 247))
                                realCells.filter {
                                    it.sourceKey.startsWith("attribute-skill:") &&
                                        linkedKeys.indexOf(it.sourceKey) % 2 == 0
                                }.forEach { row ->
                                    stream.addRect(row.cellPt.leftPt, row.cellPt.bottomPt,
                                        row.cellPt.widthPt, row.cellPt.heightPt)
                                    stream.fill()
                                }
                                val headings = realCells.filter {
                                    it.sourceKey.startsWith("attribute:")
                                }
                                if (headings.isNotEmpty()) {
                                    val top = headings.maxOf { it.cellPt.topPt }
                                    val bottom = headings.minOf { it.cellPt.bottomPt }
                                    val left = column.leftPt
                                    val right = column.rightPt
                                    val cut = minOf(2.5f, (top - bottom) / 4f)
                                    stream.setNonStrokingColor(Color(231, 242, 247))
                                    stream.setStrokingColor(Color(80, 122, 151))
                                    stream.setLineWidth(0.55f)
                                    stream.moveTo(left + cut, bottom)
                                    stream.lineTo(right - cut, bottom)
                                    stream.lineTo(right, bottom + cut)
                                    stream.lineTo(right, top - cut)
                                    stream.lineTo(right - cut, top)
                                    stream.lineTo(left + cut, top)
                                    stream.lineTo(left, top - cut)
                                    stream.lineTo(left, bottom + cut)
                                    stream.closePath()
                                    stream.fillAndStroke()
                                }
                                // Narrow gutter rail persists when the header
                                // was printed in a previous physical column.
                                val railX = column.leftPt - 3f
                                require(railX >= 31f && railX + 1.5f < column.leftPt) // C323: inside the inner frame (30.61)
                                stream.setNonStrokingColor(Color(80, 122, 151))
                                stream.addRect(railX, groupBottom + 1f, 1.5f,
                                    groupTop - groupBottom - 2f)
                                stream.fill()
                                stream.restoreGraphicsState()
                            }
                        }
                        // C214: bounded source-anchored V11 score/modifier
                        // outlines. Existing printed numbers remain the ONLY
                        // source values; no duplicate score or inferred save
                        // proficiency. Metrics and fragment offsets are the
                        // same as the immutable source text renderer below.
                        if (title == "C2 - Habilidades, acciones y recursos") {
                            pageCells.filter {
                                it.sourceKey.startsWith("attribute:") &&
                                    it.sourceKey !in c2AttributeCards
                            }.forEach { cell ->
                                val index = cell.sourceKey.removePrefix("attribute:").toIntOrNull()
                                val ability = index?.let { data.attributes.drop(6).getOrNull(it) }
                                requireNotNull(ability) {
                                    "C2 custom ability paper card has no persisted source."
                                }
                                // A heading row, a genuinely measured EMPTY spacer line
                                // and a numerical-value row form a two-level
                                // paper card; no synthetic ability values.
                                val prefix = ability.name + "\n\n"
                                val score = ability.score.toString()
                                val modifier = signedC2(ability.modifier)
                                val saving = ability.savingThrowTotal?.let {
                                    signedC2(it)
                                } ?: "sin valor"
                                val scoreStart = prefix.length
                                val modifierStart = scoreStart + score.length + " / mod ".length
                                val savingStart = modifierStart + modifier.length +
                                    " / salvación ".length
                                val exact = prefix + score + " / mod " + modifier +
                                    " / salvación " + saving
                                require(entries.single { it.sourceKey == cell.sourceKey }.text ==
                                    exact) { "C2 ability ink must match its source, not a guess." }
                                fun printedToken(start: Int, value: String): Pair<Float, Float>? {
                                    val end = start + value.length
                                    if (start < cell.sourceStartOffset ||
                                        end > cell.sourceEndExclusive) return null
                                    val before = cell.sourceText.substring(
                                        0, start - cell.sourceStartOffset)
                                    if (before.any { it == '\r' || it == '\n' || it == '\t' } ||
                                        value.any { it == '\r' || it == '\n' || it == '\t' })
                                        return null
                                    val x = cell.cellPt.leftPt + body.width.measurePt(before)
                                    val right = x + body.width.measurePt(value)
                                    if (x < cell.cellPt.leftPt ||
                                        right > cell.cellPt.rightPt - 1f) return null
                                    return x to right
                                }
                                val scoreInk = printedToken(scoreStart, score)
                                val modifierInk = printedToken(modifierStart, modifier)
                                val savingInk = printedToken(savingStart, saving)
                                if (scoreInk != null || modifierInk != null || savingInk != null) {
                                    val baseline = body.metrics.baselineIn(cell.cellPt)
                                    // The EMPTY line immediately before the
                                    // numeric row is a real measured reservation,
                                    // not a guessed blank. It lets full-height
                                    // score/oval ink extend into that RESERVED
                                    // row without touching the title above.
                                    // If a pathological wrapped item continues
                                    // across a physical column here, retain the
                                    // source and use the prior compact outline.
                                    val reserved = pageCells.firstOrNull { prior ->
                                        prior.sourceKey == cell.sourceKey &&
                                            prior.columnIndex == cell.columnIndex &&
                                            prior.rowIndex + 1 == cell.rowIndex &&
                                            prior.drawableText.isEmpty() &&
                                            prior.sourceEndExclusive == scoreStart &&
                                            kotlin.math.abs(prior.cellPt.bottomPt - cell.cellPt.topPt) <= 0.02f
                                    }
                                    val bottom = baseline - 2.7f
                                    val top = baseline + if (reserved != null) 17f else 8.9f
                                    require(bottom > cell.cellPt.bottomPt &&
                                        top < (reserved?.cellPt?.topPt ?: cell.cellPt.topPt)) {
                                        "C2 custom card must stay within its measured rows."
                                    }
                                    stream.saveGraphicsState()
                                    stream.setStrokingColor(Color(80, 122, 151))
                                    stream.setLineWidth(0.45f)
                                    scoreInk?.let { (left, right) ->
                                        // The tight measured box avoids the real
                                        // attribute name and following slash.
                                        stream.addRect(left - 1.25f, bottom,
                                            right - left + 2.5f, top - bottom)
                                        stream.stroke()
                                    }
                                    modifierInk?.let { (left, right) ->
                                        val cx = (left + right) / 2f
                                        val cy = (top + bottom) / 2f
                                        val rx = (right - left) / 2f + 1.25f
                                        val ry = (top - bottom) / 2f
                                        val k = 0.5522848f
                                        stream.moveTo(cx + rx, cy)
                                        stream.curveTo(cx + rx, cy + ry*k,
                                            cx + rx*k, cy + ry, cx, cy + ry)
                                        stream.curveTo(cx - rx*k, cy + ry,
                                            cx - rx, cy + ry*k, cx - rx, cy)
                                        stream.curveTo(cx - rx, cy - ry*k,
                                            cx - rx*k, cy - ry, cx, cy - ry)
                                        stream.curveTo(cx + rx*k, cy - ry,
                                            cx + rx, cy - ry*k, cx + rx, cy)
                                        stream.closePath()
                                        stream.stroke()
                                    }
                                    savingInk?.let { (_, right) ->
                                        val x = right + 4f
                                        if (x + 7f < cell.cellPt.rightPt - 1f) {
                                            // Blank handwriting/proficiency
                                            // square, never an invented state.
                                            stream.addRect(x, baseline - 1.5f, 7f, 7f)
                                            stream.stroke()
                                        }
                                    }
                                    stream.restoreGraphicsState()
                                }
                            }
                        }
                        // BK07: approved V11 spellbook cards are distinct
                        // clipped-corner physical panels, not unframed prose.
                        // The existing measured cell envelopes and source text
                        // remain IMMUTABLE: this paints behind them only and
                        // never changes a page/column, entry offset or font.
                        // A split spell receives one panel per actual fragment,
                        // without repeating its title or description.
                        if (title == "Libro de conjuros") {
                            val originals = entries.associate {
                                it.sourceKey to it.text
                            }
                            val bookParts = pageCells.filter {
                                it.sourceKey.startsWith("book:")
                            }.groupBy { it.columnIndex to it.sourceKey }
                            stream.saveGraphicsState()
                            bookParts.forEach { (identity, partCells) ->
                                val column = flow.columnsPt[identity.first - 1]
                                // C289: golden card frame 8.5 pt outside its text and
                                // 2.83 pt above the heading band (v11_extremo p10).
                                val left = column.leftPt - 8.5f
                                val right = column.rightPt + 8.5f
                                val top = partCells.maxOf { it.cellPt.topPt } + 2.83f
                                val bottom = partCells.minOf { it.cellPt.bottomPt }
                                require(top - bottom >= body.metrics.lineHeightPt - 0.1f)
                                require(left >= 35f && right <= 577f &&
                                    top <= 706f && bottom >= 75.5f) {
                                    "V11 book card cannot escape the approved Letter paper area."
                                }
                                // C331: golden 6.23 pt chamfer and 0.65 pt edge.
                                val cut = minOf(6.23f, (top - bottom) / 4f)
                                stream.setNonStrokingColor(Color.WHITE)
                                stream.setStrokingColor(Color(38, 75, 104))
                                stream.setLineWidth(0.65f)
                                stream.moveTo(left + cut, bottom)
                                stream.lineTo(right - cut, bottom)
                                stream.lineTo(right, bottom + cut)
                                stream.lineTo(right, top - cut)
                                stream.lineTo(right - cut, top)
                                stream.lineTo(left + cut, top)
                                stream.lineTo(left, top - cut)
                                stream.lineTo(left, bottom + cut)
                                stream.closePath()
                                stream.fillAndStroke()
                                // The real title and level occupy the source
                                // prefix before DESCRIPCIÓN, not a fabricated
                                // school or technical spell field. Shade only
                                // those EXISTING measured lines; no duplicate
                                // text and no invented per-card header row.
                                val raw = originals.getValue(identity.second)
                                val descriptionStart = bookHeadingEnds.getValue(identity.second)
                                require(descriptionStart in 1..raw.length) {
                                    "V11 essential book card lost its source heading."
                                }
                                stream.setNonStrokingColor(Color(231, 242, 247))
                                partCells.filter {
                                    it.sourceStartOffset < descriptionStart
                                }.forEach { headingCell ->
                                    stream.addRect(left + 2.83f, headingCell.cellPt.bottomPt,
                                        right - left - 5.66f, headingCell.cellPt.heightPt)
                                    stream.fill()
                                }
                            }
                            stream.restoreGraphicsState()
                            // C240: golden NIVEL label, right-aligned on the name line.
                            pageCells.filter {
                                it.sourceStartOffset == 0 && it.sourceKey in bookInlineLevels
                            }.forEach { cell ->
                                val label = bookInlineLevels.getValue(cell.sourceKey)
                                // C270: golden NIVEL label 6.3 pt, #264b68.
                                stream.setNonStrokingColor(Color(38, 75, 104))
                                drawP1Label(stream, label, cell.cellPt.rightPt - 2f -
                                    headerPdfFont.getStringWidth(label) * 6.3f / 1000f,
                                    cell.cellPt.topPt - 12.76f, 6.3f) // C288: golden name baseline
                                stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                            }
                        }
                        pageCells.forEach { cell ->
                            if (font === storyFont) {
                                // Owner option B: the story continuation keeps 7/17 pt.
                                drawStory(stream, cell.drawableText, cell.cellPt.leftPt,
                                    storyBaseline(cell.cellPt))
                                return@forEach
                            }
                            // Q14b: golden school line — 7 pt condensed bold in the
                            // label grey #657d90, on the card's heading band.
                            if (title == "Libro de conjuros") bookSchools[cell.sourceKey]?.let { (offset, school) ->
                                val printable = pcSheetV11PrintableText(school)
                                if (cell.sourceStartOffset == offset &&
                                    cell.drawableText.trimEnd() == school &&
                                    headerPdfFont.getStringWidth(printable) * 7f / 1000f <=
                                    cell.cellPt.widthPt - 2f) {
                                    stream.saveGraphicsState()
                                    stream.setNonStrokingColor(Color(101, 125, 144))
                                    stream.beginText()
                                    stream.setFont(headerPdfFont, 7f)
                                    // C288: golden school baseline, 25.51 pt under the band top.
                                    stream.newLineAtOffset(cell.cellPt.leftPt,
                                        if (cell.sourceKey in bookInlineLevels) cell.cellPt.topPt - 15.01f
                                        else body.metrics.baselineIn(cell.cellPt) - 0.8f)
                                    stream.showText(printable)
                                    stream.endText()
                                    stream.restoreGraphicsState()
                                    return@forEach
                                }
                            }
                            // C270: golden Libro card name — a larger bold heading
                            // in golden ink (golden: EB Garamond 11.3 pt; the app's
                            // nearest embedded face is Barlow 8.2 pt, which fits the
                            // 10.5 pt row). Only a name wholly on its first line.
                            if (title == "Libro de conjuros" && cell.sourceKey.startsWith("book:") &&
                                cell.sourceStartOffset == 0 && cell.drawableText.isNotBlank() &&
                                cell.sourceText.startsWith(cell.drawableText.trimEnd() + "\n")) {
                                // C287: golden EB Garamond 12 Bold 11.3 pt (accents from
                                // its fallback); a name too wide for its line keeps body text.
                                val namePt = 11.3f
                                val printable = pcSheetV11PrintableText(cell.drawableText.trimEnd())
                                val reserved = bookInlineLevels[cell.sourceKey]?.let {
                                    headerPdfFont.getStringWidth(it) * 6.3f / 1000f + 8f
                                } ?: 2f
                                if (heroWidthPt(printable, namePt) <= cell.cellPt.widthPt - reserved) {
                                    stream.saveGraphicsState()
                                    stream.setNonStrokingColor(Color(29, 56, 79))
                                    // C288: golden name baseline, 12.76 pt under the band top.
                                    drawHero(stream, printable, cell.cellPt.leftPt,
                                        if (cell.sourceKey in bookInlineLevels) cell.cellPt.topPt - 12.76f
                                        else body.metrics.baselineIn(cell.cellPt) - 0.8f, namePt)
                                    stream.restoreGraphicsState()
                                    return@forEach
                                }
                            }
                            // C296: golden note title - label 8 pt #1d384f (first line only).
                            if (title == "C-N3 - Notas digitales" &&
                                cell.sourceKey.startsWith("digital-note:") &&
                                cell.sourceStartOffset == 0 && cell.drawableText.isNotBlank() &&
                                cell.sourceText.startsWith(cell.drawableText.trimEnd() + "\n") &&
                                headerPdfFont.getStringWidth(pcSheetV11PrintableText(
                                    cell.drawableText.trimEnd())) * 8f / 1000f <= cell.cellPt.widthPt) {
                                stream.saveGraphicsState()
                                stream.setNonStrokingColor(Color(29, 56, 79))
                                drawP1Label(stream, cell.drawableText.trimEnd(), cell.cellPt.leftPt,
                                    body.metrics.baselineIn(cell.cellPt) -
                                        (if (cell.sourceKey in titledNoteKeys) 6.47f else 0f), 8f)
                                stream.restoreGraphicsState()
                                return@forEach
                            }
                            if (cell.sourceKey in c2AttributeCards) {
                                if (cell.sourceStartOffset == 0) drawC2AttributeRow(stream, cell)
                                return@forEach
                            }
                            c2AttackTableRows[cell.sourceKey]?.let { attack ->
                                // Golden C2 ATAQUES row: banded, two inner rules,
                                // the same label / bonus / damage columns as P1.
                                val left = cell.cellPt.leftPt
                                val b1 = left + attackLabelWidth
                                val b2 = b1 + attackBonusWidth
                                val rowIndex = c2AttackTableRows.keys.indexOf(cell.sourceKey)
                                fillBand(stream, left, cell.cellPt.bottomPt,
                                    cell.cellPt.widthPt, cell.cellPt.heightPt, rowIndex)
                                stream.saveGraphicsState()
                                stream.setStrokingColor(Color(148, 176, 193))
                                stream.setLineWidth(0.32f)
                                for (x in listOf(b1, b2)) {
                                    stream.moveTo(x, cell.cellPt.bottomPt)
                                    stream.lineTo(x, cell.cellPt.topPt)
                                    stream.stroke()
                                }
                                stream.restoreGraphicsState()
                                val baseline = body.metrics.baselineIn(cell.cellPt)
                                draw(stream, cell.drawableText, left + 4f, baseline, false)
                                drawAttackValues(stream, attack, left, baseline, font.metrics.fontSizePt)
                                return@forEach
                            }
                            // C219: repeat genuinely persisted P1 total/spent
                            // spell slots on P4's physical SAME-LINE level
                            // meter, as approved D-0076 §§3.25–3.26.
                            // Three separate blank squares remain PAPER ONLY:
                            // their number and state never claim digital slots.
                            // For pathological oversized values retain the
                            // prior blank paper meter rather than clipping real
                            // digits or inferring smaller numbers.
                            if (title.startsWith("P4 - ") &&
                                cell.sourceKey.startsWith("spell-level:") &&
                                cell.sourceStartOffset == 0) {
                                val level = cell.sourceKey.removePrefix("spell-level:")
                                    .toIntOrNull()
                                if (level != null && level in 1..9) {
                                    val baseline = body.metrics.baselineIn(cell.cellPt)
                                    val labelPt = 5.9f // C294: golden "ESP." size
                                    val regularX = cell.cellPt.rightPt - 83f
                                    val firstSquareX = cell.cellPt.rightPt - 33f
                                    val actualSlots = data.spellSlots.firstOrNull {
                                        it.level == level
                                    }
                                    val fullActualLabel = actualSlots?.let {
                                        "ESP.${it.totalSlots} G.${it.spentSlots}"
                                    }
                                    // C220: when genuine integers do not fit
                                    // the 45pt meter, reuse the actual FREE
                                    // baseline between "NIVEL X" and its three
                                    // 7pt paper squares. NEVER shrink any font,
                                    // split the approved one-line level header,
                                    // change a number or cover the title.
                                    val trueHeading = entries.single {
                                        it.sourceKey == cell.sourceKey
                                    }.text
                                    val headingInkWidth = bodyPdfFont.getStringWidth(
                                        pcSheetV11PrintableText(trueHeading)
                                    ) * 9f / 1000f
                                    val expandedX = cell.cellPt.leftPt +
                                        headingInkWidth + 8f
                                    val chosen = fullActualLabel?.let { sourceLabel ->
                                        val width = headerPdfFont.getStringWidth(
                                            sourceLabel
                                        ) * labelPt / 1000f
                                        when {
                                            width <= firstSquareX - regularX - 5f ->
                                                regularX to sourceLabel
                                            expandedX < regularX &&
                                                width <= firstSquareX - expandedX - 5f ->
                                                expandedX to sourceLabel
                                            else -> null
                                        }
                                    }
                                    val labelX = chosen?.first ?: regularX
                                    val label = chosen?.second ?: "ESP."
                                    require(headerPdfFont.getStringWidth(label) *
                                        labelPt / 1000f <= firstSquareX - labelX - 5f) {
                                        "P4 true slot label exceeds available paper after safe fitting."
                                    }
                                    require(labelX >= cell.cellPt.leftPt +
                                        headingInkWidth + 7f) {
                                        "P4 true slot label overlaps the real level heading."
                                    }
                                    require(firstSquareX + 2 * 11f + 7f <=
                                        cell.cellPt.rightPt - 2f) {
                                        "P4 handwritten level slots exceed measured column."
                                    }
                                    stream.beginText()
                                    stream.setFont(headerPdfFont, labelPt)
                                    stream.newLineAtOffset(labelX, baseline)
                                    stream.showText(label)
                                    stream.endText()
                                    stream.saveGraphicsState()
                                    stream.setStrokingColor(Color(130, 165, 186))
                                    stream.setLineWidth(0.6f)
                                    // Paper-only meter: a GENUINE blue ruled line,
                                    // NEVER literal underscores printed as fake ink.
                                    if (label == "ESP.") {
                                        stream.moveTo(labelX + 24f, baseline - 1f)
                                        stream.lineTo(firstSquareX - 7f, baseline - 1f)
                                        stream.stroke()
                                    }
                                    repeat(3) { slot ->
                                        stream.addRect(firstSquareX + slot * 11f,
                                            baseline - 1f, 7f, 7f)
                                        stream.stroke()
                                    }
                                    stream.restoreGraphicsState()
                                }
                                // C294: golden level heading, label 7.6 pt #264b68.
                                stream.saveGraphicsState()
                                stream.setNonStrokingColor(Color(38, 75, 104))
                                // C318: golden heading 3.4 pt inside the column.
                                drawP1Label(stream, cell.drawableText.trimEnd(), cell.cellPt.leftPt + 3.4f,
                                    body.metrics.baselineIn(cell.cellPt), 7.6f)
                                stream.restoreGraphicsState()
                                return@forEach
                            }
                            // Golden P4 paper-only rows use REAL square marks.
                            // Persisted spell preparation still uses source-specific
                            // text state; blank handwriting has no magical state.
                            if (title.startsWith("P4 - ") &&
                                cell.sourceKey.startsWith("spell-handwritten:") &&
                                cell.sourceStartOffset == 0 &&
                                cell.drawableText.startsWith("[ ] ")) {
                                val baseline = body.metrics.baselineIn(cell.cellPt)
                                // C317: golden P4 spell box - 8.22 pt, white, #527c96 0.48 pt.
                                stream.saveGraphicsState()
                                stream.setStrokingColor(Color(82, 124, 150))
                                stream.setNonStrokingColor(Color(255, 255, 255))
                                stream.setLineWidth(0.48f)
                                stream.addRect(cell.cellPt.leftPt + 2f, baseline - 1.6f, 8.22f, 8.22f)
                                stream.fillAndStroke()
                                stream.restoreGraphicsState()
                                // Approved V11 P4 uses *blank ruled paper*, not
                                // dark printed underscore glyphs. The measured
                                // source placeholder remains in the flow model
                                // to reserve one genuine row per level. Its
                                // physical vector rule was already drawn above,
                                // and the preparation square was drawn here.
                                // Never replace the paper line with digital text.
                            } else if (title.startsWith("P4 - ") &&
                                cell.sourceKey.startsWith("quick-spell:") &&
                                cell.sourceStartOffset == 0) {
                                // Every real spell-source row reserves its own 7pt
                                // PAPER preparation box for pencil (C258: the sheet
                                // never prints a digital prepared value).
                                val baseline = body.metrics.baselineIn(cell.cellPt)
                                // C317: golden P4 spell box - 8.22 pt, white, #527c96 0.48 pt.
                                stream.saveGraphicsState()
                                stream.setStrokingColor(Color(82, 124, 150))
                                stream.setNonStrokingColor(Color(255, 255, 255))
                                stream.setLineWidth(0.48f)
                                stream.addRect(cell.cellPt.leftPt + 2f, baseline - 1.6f, 8.22f, 8.22f)
                                stream.fillAndStroke()
                                stream.restoreGraphicsState()
                                if (cell.drawableText.isNotEmpty()) {
                                    draw(stream, cell.drawableText, cell.cellPt.leftPt,
                                        baseline, false)
                                }
                                p4RightSources[cell.sourceKey]?.let { realSource ->
                                    val width = p4SourceFont.width.measurePt(realSource)
                                    val x = cell.cellPt.rightPt - width - 3.4f // golden inset
                                    require(x >= cell.cellPt.leftPt +
                                        body.width.measurePt(cell.drawableText) + 10f) {
                                        "Source-backed P4 right label overlaps the spell name."
                                    }
                                    stream.saveGraphicsState()
                                    stream.setNonStrokingColor(Color(38, 75, 104))
                                    stream.beginText()
                                    stream.setFont(headerPdfFont, 6f)
                                    stream.newLineAtOffset(x, baseline)
                                    stream.showText(pcSheetV11PrintableText(realSource))
                                    stream.endText()
                                    stream.restoreGraphicsState()
                                }
                            } else if (
                                title == "C2 - Habilidades, acciones y recursos" &&
                                cell.sourceKey.startsWith("c2-heading:") &&
                                cell.drawableText.isNotEmpty()
                            ) {
                                // Group cartelas use the G2-approved Barlow
                                // label font, NOT the Fira body-value font.
                                // Their literal bytes and reserved cell remain
                                // unchanged. Strict actual embedded-font width
                                // verification prevents clipping or overflow.
                                // C269: golden C2 cartela title 9.1 pt, golden ink.
                                val goldenPt = c2CartelaTitlePt(cell.drawableText, cell.cellPt.widthPt)
                                val fontSize = goldenPt ?: 9.1f
                                val inset = if (goldenPt != null) 16f else 5f
                                stream.setNonStrokingColor(Color(29, 56, 79))
                                val inkWidth = headerPdfFont.getStringWidth(
                                    cell.drawableText,
                                ) * fontSize / 1000f
                                require(inkWidth <= cell.cellPt.widthPt - inset - 1f) {
                                    "C2 source heading cannot fit its real Barlow cartela."
                                }
                                stream.beginText()
                                stream.setFont(headerPdfFont, fontSize)
                                stream.newLineAtOffset(
                                    cell.cellPt.leftPt + inset,
                                    body.metrics.baselineIn(cell.cellPt),
                                )
                                stream.showText(pcSheetV11PrintableText(cell.drawableText))
                                stream.endText()
                                stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                            } else if (title == "C2 - Habilidades, acciones y recursos" &&
                                cell.sourceKey.startsWith("attribute-skill:") &&
                                cell.sourceStartOffset == 0) {
                                // C217: reposition the unique REAL skill total
                                // into the golden's right-value edge. Full
                                // source identity and training stay printed.
                                // Long/wrapped skills keep their prior safe ink.
                                val wholeSource = entries.single {
                                    it.sourceKey == cell.sourceKey
                                }.text
                                val trainingSep = wholeSource.lastIndexOf(" / ")
                                val valueSep = if (trainingSep >= 0)
                                    wholeSource.lastIndexOf(": ", trainingSep) else -1
                                if (cell.sourceEndExclusive == wholeSource.length &&
                                    valueSep > 0 && trainingSep > valueSep + 2) {
                                    val realTotal = wholeSource.substring(
                                        valueSep + 2, trainingSep)
                                    val realTraining = wholeSource.substring(trainingSep + 3)
                                    // C259: training is the P/E letter in the box.
                                    val leftInk = wholeSource.substring(0, valueSep)
                                    // C302: golden C2 skill bonus in Noto Sans Bold #264b68.
                                    val rightX = cell.cellPt.rightPt -
                                        p1BoldWidth(realTotal, body.metrics.fontSizePt) - 4f
                                    val labelEnd = cell.cellPt.leftPt +
                                        body.width.measurePt(leftInk)
                                    if (realTotal.isNotBlank() && realTraining.isNotBlank() &&
                                        labelEnd + 9f <= rightX) {
                                        val baseline = body.metrics.baselineIn(cell.cellPt)
                                        draw(stream, leftInk, cell.cellPt.leftPt, baseline, false)
                                        p1Bold(stream, realTotal, rightX, baseline,
                                            body.metrics.fontSizePt, Color(38, 75, 104))
                                    } else {
                                        draw(stream, cell.drawableText, cell.cellPt.leftPt,
                                            body.metrics.baselineIn(cell.cellPt), false)
                                    }
                                } else if (cell.drawableText.isNotEmpty()) {
                                    draw(stream, cell.drawableText, cell.cellPt.leftPt,
                                        body.metrics.baselineIn(cell.cellPt), false)
                                }
                            } else if (cell.drawableText.isNotEmpty()) {
                                draw(stream, cell.drawableText,
                                    cell.cellPt.leftPt,
                                    body.metrics.baselineIn(cell.cellPt),
                                    false)
                            }
                            // C216: the approved C2 paper uses an EMPTY
                            // manual checkbox for each real linked custom skill,
                            // not a literal "[ ]" glyph or inferred training.
                            // The source flow reserves six measured spaces so
                            // this 7pt square cannot obscure the printed name.
                            // Wrapped continuations never gain a second box.
                            if (title == "C2 - Habilidades, acciones y recursos" &&
                                cell.sourceKey.startsWith("attribute-skill:") &&
                                cell.sourceStartOffset == 0) {
                                require(cell.sourceText.startsWith(c2Inset + "Habilidad ")) {
                                    "C2 linked skill square lacks its measured source inset."
                                }
                                require(body.width.measurePt(c2Inset) >= 11f) {
                                    "C2 paper skill checkbox needs genuine font-measured clearance."
                                }
                                val base = body.metrics.baselineIn(cell.cellPt)
                                stream.saveGraphicsState()
                                stream.setStrokingColor(Color(130, 165, 186))
                                stream.setLineWidth(0.65f)
                                stream.addRect(cell.cellPt.leftPt + 2f, base - 1f, 7f, 7f)
                                stream.stroke()
                                stream.restoreGraphicsState()
                                c2SkillTraining[cell.sourceKey]?.takeIf { it.isNotEmpty() }?.let { letter ->
                                    // C325: the v8 training glyph inside the same 7 pt square.
                                    drawStateGlyph(stream, if (letter == "E") 0xE109 else 0xE108,
                                        cell.cellPt.leftPt + 2f, base - 1f, 7f)
                                }
                            }
                            // C259: golden ACCIONES table header and rows.
                            if (title == "C2 - Habilidades, acciones y recursos" &&
                                cell.sourceKey == "action-head") {
                                val base = body.metrics.baselineIn(cell.cellPt)
                                require(kotlin.math.abs(cell.cellPt.widthPt - c2ActionColumn) < 1f) {
                                    "C2 action table measured for a different column width"
                                }
                                stream.saveGraphicsState()
                                stream.setNonStrokingColor(Color(231, 242, 247))
                                stream.addRect(cell.cellPt.leftPt, cell.cellPt.bottomPt,
                                    cell.cellPt.widthPt, cell.cellPt.heightPt)
                                stream.fill()
                                stream.setNonStrokingColor(Color(38, 75, 104))
                                listOf("ACCIÓN", "TIPO", "ALCANCE / EFECTO").forEachIndexed { i, label ->
                                    drawP1Label(stream, label, cell.cellPt.leftPt + c2ActionX[i] + 2f, base, 6.5f)
                                }
                                stream.restoreGraphicsState()
                            }
                            c2ActionRows[cell.sourceKey]?.let { rows ->
                                val line = cell.sourceStartOffset / 2
                                if (title == "C2 - Habilidades, acciones y recursos" &&
                                    cell.sourceStartOffset % 2 == 0 && line < rows.size) {
                                    val base = body.metrics.baselineIn(cell.cellPt)
                                    rows[line].forEachIndexed { i, text ->
                                        if (text.isNotEmpty()) {
                                            draw(stream, text, cell.cellPt.leftPt + c2ActionX[i] + 2f, base, false)
                                        }
                                    }
                                    if (line == rows.size - 1) {
                                        stream.saveGraphicsState()
                                        stream.setStrokingColor(Color(176, 204, 220))
                                        stream.setLineWidth(0.35f)
                                        stream.moveTo(cell.cellPt.leftPt, cell.cellPt.bottomPt)
                                        stream.lineTo(cell.cellPt.rightPt, cell.cellPt.bottomPt)
                                        stream.stroke()
                                        stream.restoreGraphicsState()
                                    }
                                }
                            }
                            // C259: resource paper track — empty boxes and the real
                            // small golden gastados/total count.
                            c2ResourceTracks[cell.sourceKey]?.let { (offset, spent, total) ->
                                if (title == "C2 - Habilidades, acciones y recursos" &&
                                    cell.sourceStartOffset == offset) {
                                    val base = body.metrics.baselineIn(cell.cellPt)
                                    val count = spent.toString() + "/" + total
                                    val countX = cell.cellPt.rightPt - 4f -
                                        headerPdfFont.getStringWidth(count) * 7f / 1000f
                                    stream.saveGraphicsState()
                                    stream.setStrokingColor(Color(82, 124, 150))
                                    stream.setNonStrokingColor(Color.WHITE)
                                    stream.setLineWidth(0.48f)
                                    repeat(total) { n ->
                                        val px = cell.cellPt.leftPt + 4f + n * 11f + (n / 5) * 3f
                                        require(px + 8f <= countX - 3f) {
                                            "C2 resource boxes overlap their real count"
                                        }
                                        stream.addRect(px, base - 1f, 8f, 8f)
                                        stream.fillAndStroke()
                                    }
                                    stream.setNonStrokingColor(Color(38, 75, 104))
                                    drawP1Label(stream, count, countX, base, 7f)
                                    stream.restoreGraphicsState()
                                }
                            }
                            if (cell.sourceKey in ammunitionTracks &&
                                cell.sourceStartOffset == 0) {
                                val count = ammunitionTracks.getValue(cell.sourceKey)
                                require(count in 0..20)
                                if (count <= 20) {
                                    repeat(count) { mark ->
                                        val px = cell.cellPt.leftPt + 54f + mark * 8f +
                                            (mark / 5) * 4f
                                        require(px + 6f <= cell.cellPt.rightPt) {
                                            "Ammunition track overflows its physical PDF column."
                                        }
                                        stream.addRect(px, cell.cellPt.bottomPt + 2f, 6f, 6f)
                                        stream.stroke()
                                    }
                                }
                            }
                            if (cell.sourceKey in specialMarks && cell.sourceStartOffset == 0) {
                                stream.addRect(557f, cell.cellPt.bottomPt + 1f, 9f, 9f)
                                stream.stroke()
                                if (specialMarks[cell.sourceKey] == true) {
                                    // C325: v8 ATTUNED_ACTIVE (filled diamond).
                                    drawStateGlyph(stream, 0xE10F, 557f, cell.cellPt.bottomPt + 1f, 9f)
                                }
                            }
                        }
                        if (title.startsWith("C2 - ")) {
                            fillUnusedC2WithNotes(stream, pageCells)
                        }
                        // P3 now draws its own two-column ruled writing grammar,
                        // before the source text, including continuation pages.
                    }
                }
            }

            val identity = buildList {
                data.attributes.drop(6).forEachIndexed { index, attribute ->
                    val alreadyPrintedOnP1 = index + 6 < p1PrintedAttributeCount
                    val customC2Group = if (alreadyPrintedOnP1) null else
                        "custom-attribute-group:" + index
                    if (!alreadyPrintedOnP1) {
                        // C247: golden C2 attribute row (score box, modifier oval,
                        // name, SALVACIÓN box and value) when the name fits; the
                        // flow reserves its two measured rows. Otherwise keep the
                        // previous text card.
                        val goldenRow = attribute.name.none { it == '\r' || it == '\n' || it == '\t' } &&
                            headerPdfFont.getStringWidth(pcSheetV11PrintableText(attribute.name)) *
                            9f / 1000f <= 100f
                        if (goldenRow) c2AttributeCards += "attribute:" + index
                        add(PcSheetV11FlowItem(
                            "attribute:" + index,
                            if (goldenRow) attribute.name + "\n " else
                            attribute.name + "\n\n" + attribute.score +
                                " / mod " + signedC2(attribute.modifier) +
                                " / salvación " +
                                (attribute.savingThrowTotal?.let {
                                    signedC2(it)
                                } ?: "sin valor"),
                            keepTogetherIfFits = true,
                            groupKey = customC2Group,
                        ))
                    }
                    data.skills.withIndex()
                        .filter { (skillIndex, skill) -> skill.customSkillId != null &&
                            skill.ability == attribute.reference &&
                            skillIndex !in printedSkillIndices }
                        .forEach { (_, skill) ->
                            c2SkillTraining["attribute-skill:" + skill.customSkillId] = when (skill.training) {
                                io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.PROFICIENT -> "P"
                                io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.EXPERTISE -> "E"
                                io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining.NONE -> ""
                            }
                            add(PcSheetV11FlowItem(
                                "attribute-skill:" + skill.customSkillId,
                                // Six spaces are measurable handwriting
                                // clearance for the genuine 7pt paper box.
                                c2Inset + "Habilidad " + skill.name +
                                    // The source ability is still explicit if
                                    // the full attribute was already placed P1.
                                    (if (alreadyPrintedOnP1) attribute.abbreviation
                                        .takeIf { it.isNotBlank() }
                                        ?.let { " (" + approvedAbilityAbbreviation(it) + ")" }
                                        ?: "" else "") +
                                    ": " + (skill.total?.let {
                                        signedC2(it)
                                    } ?: "sin resolver") +
                                    " / " + spanishTraining(skill.training),
                                keepTogetherIfFits = true,
                                groupKey = customC2Group,
                            ))
                        }
                }
                if (!variantA) {
                    data.skills.forEachIndexed { index, skill ->
                        if (skill.builtInKey == null &&
                            skill.ability !in customAttributeReferences) {
                            val abbreviation = data.attributes.firstOrNull {
                                it.reference == skill.ability
                            }?.abbreviation?.let { approvedAbilityAbbreviation(it) }
                            add(PcSheetV11FlowItem("skill:" + index,
                                "Habilidad " + skill.name +
                                    (abbreviation?.let { " (" + it + ")" } ?: "") +
                                    ": " + (skill.total?.let {
                                        signedC2(it)
                                    } ?: "sin resolver") +
                                    " / " + spanishTraining(skill.training)))
                        }
                    }
                }
                // C259 (owner 2026-10-28): actions as a table with the golden
                // ATAQUES grammar (ACCIÓN · TIPO · ALCANCE / EFECTO). Each row
                // reserves one measured line per wrapped cell line; the drawer
                // prints the three columns on those lines. Notes follow in full
                // width. Nothing is truncated.
                val actionEntries = data.combatEntries
                    .filter { it.id !in p1AttackIds && it.type !=
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ATTACK }
                fun wrapCell(text: String, width: Float): List<String> {
                    val lines = mutableListOf<String>()
                    var current = ""
                    text.split(" ").filter { it.isNotEmpty() }.forEach { word ->
                        var rest = word
                        while (c2Font.width.measurePt(rest) > width) {
                            val cut = (1..rest.length).last { c2Font.width.measurePt(rest.take(it)) <= width }
                            if (current.isNotEmpty()) { lines += current; current = "" }
                            lines += rest.take(cut)
                            rest = rest.drop(cut)
                        }
                        val joined = if (current.isEmpty()) rest else current + " " + rest
                        if (c2Font.width.measurePt(joined) <= width) current = joined
                        else { lines += current; current = rest }
                    }
                    if (current.isNotEmpty()) lines += current
                    return lines.ifEmpty { listOf("") }
                }
                if (actionEntries.isNotEmpty()) {
                    add(PcSheetV11FlowItem("action-head", " ", keepTogetherIfFits = true))
                }
                actionEntries.forEach { entry ->
                    val type = when (entry.type) {
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ACTION -> "Acción"
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.BONUS_ACTION -> "Acción adicional"
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.REACTION -> "Reacción"
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.ATTACK -> "Ataque"
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType.OTHER -> "Otra"
                    }
                    val effect = listOfNotNull(
                        entry.rangeText?.takeIf { it.isNotBlank() },
                        entry.attackModifier?.let { "Bono " + signedC2(it) },
                        entry.damageEffect.takeIf { it.isNotBlank() },
                    ).joinToString(" · ")
                    val columns = listOf(
                        wrapCell(pcSheetV11PrintableText(entry.name), c2ActionWidths[0]),
                        wrapCell(type, c2ActionWidths[1]),
                        wrapCell(pcSheetV11PrintableText(effect), c2ActionWidths[2]),
                    )
                    val rowLines = columns.maxOf { it.size }
                    val key = "action:" + entry.id
                    c2ActionRows[key] = (0 until rowLines).map { line ->
                        columns.map { it.getOrElse(line) { "" } }
                    }
                    add(PcSheetV11FlowItem(key,
                        List(rowLines) { " " }.joinToString("\n") +
                            (entry.notes?.takeIf { it.isNotBlank() }?.let { "\nNotas: " + it } ?: ""),
                        keepTogetherIfFits = true))
                }
            }
            // P1 is one physical sheet, not a repeating P1 sub-document.
            // Measure the complete source first and put EVERY unprinted source
            // suffix into C2; the same real font metrics drive both placements.
            // Keep variable-height mechanics beneath the fixed approved identity
            // cartelas, never underneath their labels or beside the portrait.
            val p1Unprinted=listOfNotNull(headerNameRoute.continuation)+
                identityFieldOverflow+bSkillOverflow+identity
            addSection("P1 - Personaje / combate / habilidades",
                PcSheetV11FlowSection.TRAITS,emptyList(),2,alwaysPage=true,
                area=PcSheetV11RectPt(42f,90f,258f,500f),maxPhysicalPages=1)

            // Approved golden P2 has the EQUIPO/TESORO stack on the LEFT,
            // mechanical RASGOS on the RIGHT, and full-width EQUIPO ESPECIAL
            // below them. The source-backed composers retain measured overflow
            // and forward it to C2, never to narrative P3.
            // C315: golden P2 tables (v11_extremo p2) - EQUIPO 460.63..670.68 and
            // TESORO 327.97..427.18 on the left (x39.69..298.49), RASGOS 318.33..670.68
            // on the right (x309.26..572.32), ESPECIAL from 291.4 down to 60.94.
            // C343: ESPECIAL takes the golden full width x39.69..572.31.
            val equipmentArea = PcSheetV11RectPt(39.69f, 460.63f, 258.8f, 210.05f)
            val treasureArea = PcSheetV11RectPt(39.69f, 327.97f, 258.8f, 99.21f)
            val traitsArea = PcSheetV11RectPt(309.26f, 318.33f, 263.06f, 352.35f)
            val specialArea = PcSheetV11RectPt(39.69f, 60.94f, 532.62f, 230.46f)
            require(equipmentArea.rightPt < traitsArea.leftPt)
            require(equipmentArea.bottomPt > treasureArea.topPt)
            require(traitsArea.bottomPt > specialArea.topPt)
            require(kotlin.math.abs(specialArea.widthPt - 532.62f) < 0.01f)
            val completeEquipment = PcSheetV11PairedTableSources.equipment(data)
            val completeTreasure = PcSheetV11PairedTableSources.treasure(data)
            // P2 has narrow golden-approved paired subcolumns. A name whose
            // real glyphs cannot fit one P2 label row must move WHOLE to the
            // wider C2 table, not split its identity at a page boundary.
            // C2 prints its amount exactly once; P2 retains blank paper rows.
            fun rowsForP2(
                input: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData,
                area: PcSheetV11RectPt,
            ): io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData =
                PcSheetV11PairedAmountRouting.p2Eligible(input, area, 12f, p2Font.width)
            val inventoryPlaced = PcSheetV11PairedTableComposer.place(
                rowsForP2(completeEquipment, equipmentArea),
                equipmentArea, 12f, 36f, 6f, p2Font.metrics.lineHeightPt, p2Font.width,
            )
            val treasurePlaced = PcSheetV11PairedTableComposer.place(
                rowsForP2(completeTreasure, treasureArea),
                treasureArea, 12f, 36f, 6f, p2Font.metrics.lineHeightPt, p2Font.width,
            )
            // P2 contains ONLY source IDs not already printed on P1.
            // G2 flow and mixed C2 extend these summary records losslessly;
            // description/notes still exist in the App source, but NEVER print
            // in Rasgos y Atributos on the V11 paper sheet.
            val traitLines = data.traits
                .filterNot { it.id in p1PrintedTraitIds }
                .map { trait ->
                    PcSheetV11FlowItem("p2-trait:" + trait.id,
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11TraitSummary.text(trait),
                        keepTogetherIfFits = true)
                }
            val traitsPlaced = p2Font.place(PcSheetV11FlowSection.TRAITS, traitLines,
                traitsArea, 2, 12f)
            // V11 golden P2: location / name / description / ACTIVO belong
            // to ONE logical row per special item, not four paragraphs.
            // D-0076 §3.21 (owner): keep the 25 pt writing rhythm when the
            // entry fits and give it more height when its name/description
            // needs several lines. An entry spans whole pitches, so the free
            // rows below stay on the same paper grid. Entries fill P2 in
            // source order; from the first one that no longer fits, the
            // complete source suffix goes to full-width C2 untruncated.
            // C343: golden v11_extremo p2 table - column rules at x146.21, 279.37
            // and 527.04, a 15.02 pt header and eight 26.93 pt rows.
            val specialColumns = listOf(106.52f, 133.16f, 247.67f, 45.27f)
            require(kotlin.math.abs(specialColumns.sum() - specialArea.widthPt) < 0.01f)
            val specialHeaderHeight = 15.02f
            val specialRowHeight = 26.93f
            val specialCapacity = ((specialArea.heightPt - specialHeaderHeight) /
                specialRowHeight + 0.001f).toInt()
            // C343: golden first baseline 11.77 pt under the row top; ubicación,
            // nombre and descripción in 7.1 / 6.9 / 6.6 pt on the golden 9 pt pitch.
            val specialFirstBaseline = 11.77f
            val specialColumnFonts = listOf(
                goldenRhythm(7.1f, 9f), goldenRhythm(6.9f, 9f), goldenRhythm(6.6f, 9f),
            )
            // One pitch keeps its approved two lines; taller bands keep the
            // golden first baseline and a 4 pt foot so ink never meets a rule.
            fun specialLinesFor(pitches: Int): Int = maxOf(2,
                1 + ((pitches * specialRowHeight - specialFirstBaseline - 4f) /
                    specialFont.metrics.lineHeightPt).toInt())
            val specialMaxLines = specialLinesFor(specialCapacity)
            fun specialCellLines(
                raw: String?,
                widthPt: Float,
                font: PcSheetV11ResolvedFont = specialFont,
            ): List<String>? {
                if (raw.isNullOrBlank()) return emptyList()
                val text = raw
                val width = widthPt - 6.8f // C343: golden 3.4 pt inset each side
                // The verified text-flow uses the exact PDF's Fira font width.
                val printable = PcSheetV11TextFlow.paginate(
                    PcSheetV11FlowSection.SPECIAL_EQUIPMENT,
                    listOf(PcSheetV11FlowItem("special-cell", text,
                        keepTogetherIfFits = true)),
                    1,
                    PcSheetV11TextColumn(width,
                        font.metrics.lineHeightPt * specialMaxLines,
                        font.metrics.lineHeightPt),
                    font.width,
                )
                if (printable.localPageCount != 1) return null
                val ranges = printable.fragments.flatMap { it.lineRanges }
                if (ranges.size > specialMaxLines) return null
                return ranges.map { text.substring(it.startOffset, it.endExclusive)
                    .trimEnd('\r', '\n') }
            }
            val specialP2Rows = mutableListOf<Pair<
                io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Equipment,
                List<List<String>>>>()
            val specialP2Pitches = mutableListOf<Int>()
            for (item in data.specialEquipment) {
                val details = listOfNotNull(
                    item.description?.takeIf { it.isNotBlank() },
                    item.notes?.takeIf { it.isNotBlank() }?.let { "NOTAS: " + it },
                ).joinToString(" / ")
                val fields = listOf(item.location, item.name, details)
                val lines = fields.mapIndexed { index, field ->
                    specialCellLines(field, specialColumns[index], specialColumnFonts[index])
                }
                if (lines.any { it == null }) break
                val needed = lines.maxOf { requireNotNull(it).size }
                val pitches = (1..specialCapacity).first { specialLinesFor(it) >= needed }
                if (specialP2Pitches.sum() + pitches > specialCapacity) break
                specialP2Rows += item to lines.map { requireNotNull(it) }
                specialP2Pitches += pitches
            }
            val specialDeferredToC2 = data.specialEquipment.drop(specialP2Rows.size)
            require(specialP2Rows.size + specialDeferredToC2.size ==
                data.specialEquipment.size)
            // One band per real entry, then one-pitch free rows to the foot.
            val specialBands = specialP2Pitches +
                List(specialCapacity - specialP2Pitches.sum()) { 1 }
            val specialBandTops = specialBands.runningFold(
                specialArea.topPt - specialHeaderHeight) { top, pitches ->
                top - pitches * specialRowHeight
            }

            fun grid(
                stream: PDPageContentStream,
                rect: PcSheetV11RectPt,
                twoColumns: Boolean,
                manuscriptRowHeightPt: Float = body.metrics.lineHeightPt,
                alternateP2Rows: Boolean = false,
                // C211 R1: (xLeft, xRight, yLimit) — row rules below yLimit are not
                // drawn across [xLeft, xRight], where a manuscript NOTES grid sits.
                stopRulesBelow: List<Triple<Float, Float, Float>> = emptyList(),
            ) {
                require(manuscriptRowHeightPt.isFinite() && manuscriptRowHeightPt >=
                    body.metrics.lineHeightPt) { "Writing rows cannot be compressed." }
                // Blue mist writing bands and fine rules match the approved V11
                // paper grammar; black default strokes belong only to PREQA.
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(238, 247, 251))
                stream.addRect(rect.leftPt, rect.bottomPt, rect.widthPt, rect.heightPt)
                stream.fill()
                // Golden P2 writes against alternating blue-mist rows, not
                // an undifferentiated blue table. Opt in on P2 ONLY: this
                // shared function also serves C2, whose proven geometry and
                // complete manuscript grids must remain unchanged.
                if (alternateP2Rows) {
                    val complete = (rect.heightPt / manuscriptRowHeightPt).toInt()
                    stream.setNonStrokingColor(Color(231, 242, 247))
                    for (row in 0 until complete step 2) {
                        val top = rect.topPt - row * manuscriptRowHeightPt
                        val bottom = maxOf(rect.bottomPt, top - manuscriptRowHeightPt)
                        if (top > bottom) {
                            stream.addRect(rect.leftPt, bottom, rect.widthPt, top-bottom)
                            stream.fill()
                        }
                    }
                }
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.45f)
                stream.addRect(rect.leftPt, rect.bottomPt, rect.widthPt, rect.heightPt)
                stream.stroke()
                if (twoColumns) {
                    val center = rect.leftPt + rect.widthPt / 2f
                    stream.moveTo(center, rect.bottomPt)
                    stream.lineTo(center, rect.topPt)
                    stream.stroke()
                }
                val cells = (rect.heightPt / manuscriptRowHeightPt).toInt()
                for (line in 1 until cells) {
                    val y = rect.topPt - line * manuscriptRowHeightPt
                    var segments = listOf(rect.leftPt to rect.rightPt)
                    stopRulesBelow.filter { y < it.third }.forEach { (cutLeft, cutRight, _) ->
                        segments = segments.flatMap { (from, to) ->
                            listOf(from to minOf(to, cutLeft), maxOf(from, cutRight) to to)
                                .filter { it.second - it.first > 0.5f }
                        }
                    }
                    segments.forEach { (from, to) ->
                        stream.moveTo(from, y)
                        stream.lineTo(to, y)
                        stream.stroke()
                    }
                }
                stream.restoreGraphicsState()
            }

            // P2 paper grammar in the approved golden: all four source-backed
            // module titles are clipped blue cartelas, not floating headings.
            // The fixed 14.5pt heading strip is already outside every measured
            // equipment/treasure/traits/special table, so no row, ID or page
            // allocation moves to accommodate decoration.
            fun drawP2Cartela(
                stream: PDPageContentStream,
                title: String,
                area: PcSheetV11RectPt,
                columnLegend: String? = null,
            ) {
                // C315: golden cartela, 22.96 pt tall, 1.98 pt above its table.
                val left = if (area.leftPt == 42f && area.widthPt == 528f) 39.69f else area.leftPt
                val right = if (area.leftPt == 42f && area.widthPt == 528f) 572.32f else area.rightPt
                val bottom = area.topPt + 1.98f
                val top = bottom + 22.96f
                val corner = 5f
                require(top < 717f) { "P2 cartela cannot hit the physical page header" }
                require(headerPdfFont.getStringWidth(title) * 11f / 1000f <=
                    area.widthPt - 18f) {
                    "P2 title cannot fit inside its approved source-free cartela: " + title
                }
                stream.saveGraphicsState()
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.setStrokingColor(Color(38, 75, 104))
                stream.setLineWidth(0.65f) // C293: golden cartela edge
                stream.moveTo(left + corner, bottom)
                stream.lineTo(right - corner, bottom)
                stream.lineTo(right, bottom + corner)
                stream.lineTo(right, top - corner)
                stream.lineTo(right - corner, top)
                stream.lineTo(left + corner, top)
                stream.lineTo(left, top - corner)
                stream.lineTo(left, bottom + corner)
                stream.closePath()
                stream.fillAndStroke()
                stream.restoreGraphicsState()
                // C265: golden P2 cartela, 9.1 pt title inside two diamonds.
                stream.setNonStrokingColor(Color(29, 56, 79)) // C266: golden title ink
                drawCartelaDiamonds(stream, left, right, bottom, top)
                stream.beginText()
                stream.setFont(headerPdfFont, 9.1f)
                stream.newLineAtOffset(left + 16f, bottom + 6.84f) // C315 golden baseline
                stream.showText(title)
                stream.endText()
                stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                // Golden P2 requires an explicit meaning for BOTH small
                // internal columns: OBJETO/CANTIDAD or DENOMINACIÓN/CANTIDAD.
                // Occupy the existing blue source-free cartela, never the table.
                // C292 (golden): the legend is Noto Sans 6.2 pt in label grey
                // #657d90, 1.2 pt above the title baseline, 18.4 pt from the right.
                columnLegend?.let { legend ->
                    val legendSize = 6.2f
                    val printable = pcSheetV11PrintableText(legend)
                    val width = bodyPdfFont.getStringWidth(printable) *
                        legendSize / 1000f
                    val titleWidth = headerPdfFont.getStringWidth(title) *
                        9.1f / 1000f
                    val x = right - 18.4f - width
                    require(x > left + 16f + titleWidth + 8f) {
                        "P2 paired-table legend overlaps its approved heading."
                    }
                    stream.setNonStrokingColor(Color(101, 125, 144))
                    stream.beginText()
                    stream.setFont(bodyPdfFont, legendSize)
                    stream.newLineAtOffset(x, bottom + 8.04f) // C315 golden baseline
                    stream.showText(printable)
                    stream.endText()
                    stream.setNonStrokingColor(Color(36, 57, 75)) /* C298 golden ink */
                }
            }

            fun drawPaired(
                stream: PDPageContentStream,
                title: String,
                area: PcSheetV11RectPt,
                placed: PcSheetV11PairedTablePlaced,
                localPage: Int,
                suppressAmounts: Set<String> = emptySet(),
                compactC2: Boolean = false,
                writeC2Notes: Boolean = true,
                tableFont: PcSheetV11ResolvedFont = body,
            ) {
                // C263: a paired table measures and draws in its own font.
                val body = tableFont
                fun draw(stream: PDPageContentStream, text: String, x: Float, y: Float, label: Boolean) {
                    if (text.isEmpty()) return
                    // C291: golden value ink #24394b for body text; labels keep their ink.
                    if (!label) {
                        stream.saveGraphicsState()
                        stream.setNonStrokingColor(Color(36, 57, 75))
                    }
                    stream.beginText()
                    stream.setFont(if (label) headerPdfFont else bodyPdfFont,
                        if (label) 12f else tableFont.metrics.fontSizePt)
                    stream.newLineAtOffset(x, y)
                    stream.showText(pcSheetV11PrintableText(text))
                    stream.endText()
                    if (!label) stream.restoreGraphicsState()
                }
                if (!compactC2) {
                    val ordinary = placed.data.kind ==
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableKind.EQUIPMENT
                    drawP2Cartela(
                        stream, if (ordinary) "EQUIPO" else "TESORO", area,
                        if (ordinary) "OBJETO / CANTIDAD" else "denominación / cantidad", // golden case
                    )
                }
                val pageLabels = placed.labels.filter { it.localPageIndex == localPage }
                val lowestPrintedBottom = pageLabels.minOfOrNull { it.rectPt.bottomPt }
                // Unlike P2, C2's last table page must free genuine unused paper
                // for handwritten NOTES, without drawing empty object/quantity rows
                // underneath. These bounds come from measured source cells.
                val notesUsable = compactC2 && writeC2Notes &&
                    lowestPrintedBottom != null &&
                    ((lowestPrintedBottom - 28f - 78f) / 16f).toInt() >= 3
                val tableBottom = if (notesUsable) requireNotNull(lowestPrintedBottom)
                    else area.bottomPt
                val visibleTable = PcSheetV11RectPt(
                    area.leftPt, tableBottom, area.widthPt, area.topPt - tableBottom,
                )
                // P203/P206: a source-free P2 table is handwriting paper, not
                // a compressed digital-data table. The golden has broader
                // writing lines (Equipo ~16.5pt, Tesoro ~19.5pt). Retain
                // the original real-font line rhythm whenever any data is printed,
                // and never alter C2 pagination or original source identities.
                val manuscriptRowHeight = if (!compactC2 && pageLabels.isEmpty()) {
                    when (placed.data.kind) {
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableKind.EQUIPMENT -> 16.15f // C320 golden 16.16 (13 rows fit 210.05)
                        io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableKind.TREASURE -> 19.84f // C320 golden
                    }
                } else body.metrics.lineHeightPt
                // C211 recovery R1: decide the C207 side NOTES grids BEFORE the table,
                // so the table row rules stop above them instead of running, hidden
                // under a white fill, through the manuscript grid (root cause of the
                // irregular-grid audit failure of 88b1e78).
                data class SideNotes(
                    val pairZero: Int, val gridLeft: Float, val gridWidth: Float,
                    val gridBottom: Float, val gridTop: Float, val rows: Int, val columns: Int,
                )
                // C268: a wholly empty pair also gets NOTES when the bottom
                // NOTES grid is in use; it then stops above that grid.
                val sideNotes = if (compactC2) {
                    val printedBottoms = (1..2).map { pair ->
                        pageLabels.filter { it.pairIndex == pair }
                            .minOfOrNull { it.rectPt.bottomPt }
                    }
                    (0..1).mapNotNull { pairZero ->
                        if (notesUsable && printedBottoms[pairZero] != null) return@mapNotNull null
                        val longerSideBottom = printedBottoms[1 - pairZero]
                            ?: return@mapNotNull null
                        // C268: a pair with NO source row on this page is wholly
                        // unused paper below its column header; it gets NOTES too.
                        val availableFrom = printedBottoms[pairZero] ?: (area.topPt - 18f)
                        // Require a genuine asymmetric source stream; never
                        // replace cells on the longer, still occupied side.
                        if (availableFrom - longerSideBottom < 128f) return@mapNotNull null
                        val pair = placed.pairs[pairZero]
                        val gridPitch = 16f
                        val gridBottom = if (notesUsable) tableBottom + 8f
                            else maxOf(area.bottomPt + 2f, 78f)
                        val completeRows =
                            ((availableFrom - 28f - gridBottom) / gridPitch).toInt()
                        if (completeRows < 4) return@mapNotNull null
                        val gridTop = gridBottom + completeRows * gridPitch
                        require(gridTop + 18f <= availableFrom - 10f) {
                            "C2 paired-table side notes would overlap source cells."
                        }
                        val completeColumns = (pair.widthPt / gridPitch).toInt()
                        require(completeColumns >= 4)
                        val gridWidth = completeColumns * gridPitch
                        val gridLeft = pair.leftPt + (pair.widthPt - gridWidth) / 2f
                        SideNotes(pairZero, gridLeft, gridWidth, gridBottom, gridTop,
                            completeRows, completeColumns)
                    }
                } else emptyList()
                grid(stream, visibleTable, true, manuscriptRowHeight,
                    alternateP2Rows = !compactC2,
                    stopRulesBelow = sideNotes.map { notes ->
                        val pair = placed.pairs[notes.pairZero]
                        Triple(pair.leftPt, pair.rightPt, notes.gridTop + 18f)
                    })
                if (compactC2) {
                    // Approved C2 paper grammar: a real pale-blue cartela
                    // with clipped corners, within the EXISTING 14.5pt
                    // strip between the table and fixed header rule.
                    // The title previously overlapped that rule by 3pt.
                    // Font glyph extents at 11pt stay inside this measured
                    // strip, while all source table cells remain unchanged.
                    val left = area.leftPt
                    val right = area.rightPt
                    val bottom = area.topPt
                    val top = bottom + 14.5f
                    val corner = 5f
                    require(top < 717f) {
                        "C2 cartela cannot cross the fixed physical header rule."
                    }
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(231, 242, 247))
                    stream.setStrokingColor(Color(38, 75, 104))
                    stream.setLineWidth(0.65f) // C293: golden cartela edge
                    stream.moveTo(left + corner, bottom)
                    stream.lineTo(right - corner, bottom)
                    stream.lineTo(right, bottom + corner)
                    stream.lineTo(right, top - corner)
                    stream.lineTo(right - corner, top)
                    stream.lineTo(left + corner, top)
                    stream.lineTo(left, top - corner)
                    stream.lineTo(left, bottom + corner)
                    stream.closePath()
                    stream.fillAndStroke()
                    stream.restoreGraphicsState()
                    // C307: golden C2 cartela title - 9.1 pt ink 16 pt inside, diamonds.
                    drawCartelaDiamonds(stream, left, right, bottom, top)
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(29, 56, 79))
                    stream.beginText()
                    stream.setFont(headerPdfFont, 9.1f)
                    stream.newLineAtOffset(left + 16f, bottom + 4f)
                    stream.showText(title)
                    stream.endText()
                    stream.restoreGraphicsState()
                }
                stream.saveGraphicsState()
                stream.setStrokingColor(Color(130, 165, 186))
                stream.setLineWidth(0.45f)
                placed.pairs.forEach { pair ->
                    // The quantity separator belongs to its ACTUAL measured column.
                    // Preserve the original -42pt separator for normal 36pt rows.
                    val amountWidth = placed.quantities.firstOrNull()?.rectPt?.widthPt
                        ?: PcSheetV11PairedAmountRouting.BASE_AMOUNT_WIDTH_PT
                    val quantitySeparator = pair.rightPt -
                        amountWidth - PcSheetV11PairedAmountRouting.AMOUNT_GAP_PT
                    val separatorBottom = sideNotes.firstOrNull {
                        placed.pairs[it.pairZero] == pair
                    }?.let { it.gridTop + 18f } ?: visibleTable.bottomPt
                    stream.moveTo(quantitySeparator, separatorBottom)
                    stream.lineTo(quantitySeparator, pair.topPt)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
                pageLabels.forEach { cell ->
                    if (cell.printableText.isNotEmpty()) {
                        draw(stream, cell.printableText, cell.rectPt.leftPt + 2f,
                            body.metrics.baselineIn(cell.rectPt), false)
                    }
                }
                placed.quantities.filter {
                    it.localPageIndex == localPage && it.sourceKey !in suppressAmounts
                }.forEach { cell ->
                    // C242: golden paired tables right-align each amount 3 pt
                    // inside its quantity cell (P2 and C2).
                    // C292: golden amounts are Noto Sans Bold in ink #264b68.
                    val amountPt = body.metrics.fontSizePt
                    val amountText = pcSheetV11PrintableText(cell.printableText)
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(38, 75, 104))
                    stream.beginText()
                    stream.setFont(boldPdfFont, amountPt)
                    stream.newLineAtOffset(cell.rectPt.rightPt - 3f -
                        boldPdfFont.getStringWidth(amountText) * amountPt / 1000f,
                        body.metrics.baselineIn(cell.rectPt))
                    stream.showText(amountText)
                    stream.endText()
                    stream.restoreGraphicsState()
                }
                // C207: on a FULL-HEIGHT C2 paired-table page, one real
                // object/amount pair may end hundreds of points above the
                // other. That blank pair is handwriting paper, not dozens
                // of source-free (and unusable) EQUIPO/TESORO rows. A full
                // bottom NOTES grid already takes priority when it fits.
                sideNotes.forEach { notes ->
                    val pair = placed.pairs[notes.pairZero]
                    // Clear ONLY the shorter pair's unused cell fills before
                    // drawing a complete source-free NOTES grid.
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color.WHITE)
                    stream.addRect(pair.leftPt + 0.6f, area.bottomPt + 0.6f,
                        pair.widthPt - 1.2f, notes.gridTop + 18f - area.bottomPt - 0.6f)
                    stream.fill()
                    stream.restoreGraphicsState()
                    drawC2ManuscriptCartela(stream, notes.gridLeft, notes.gridWidth, notes.gridTop)
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(130, 165, 186))
                    stream.setLineWidth(0.5f)
                    for (column in 0..notes.columns) {
                        val x = notes.gridLeft + column * 16f
                        stream.moveTo(x, notes.gridBottom)
                        stream.lineTo(x, notes.gridTop)
                        stream.stroke()
                    }
                    for (row in 0..notes.rows) {
                        val y = notes.gridBottom + row * 16f
                        stream.moveTo(notes.gridLeft, y)
                        stream.lineTo(notes.gridLeft + notes.gridWidth, y)
                        stream.stroke()
                    }
                    stream.restoreGraphicsState()
                }
                if (notesUsable) drawUnusedC2NotesGrid(stream, tableBottom)
            }

            fun drawP2Modules(stream: PDPageContentStream, localPage: Int) {
                drawPaired(stream, "EQUIPO   /   CANTIDAD", equipmentArea, inventoryPlaced, localPage,
                    tableFont = p2Font)
                drawPaired(stream, "TESORO   /   CANTIDAD", treasureArea, treasurePlaced, localPage,
                    tableFont = p2Font)
                drawP2Cartela(stream, "RASGOS Y ATRIBUTOS", traitsArea)
                grid(stream, traitsArea, true, p2Font.metrics.lineHeightPt, alternateP2Rows = true)
                traitsPlaced.cells.filter { it.localPageIndex == localPage }.forEach { cell ->
                    if (cell.drawableText.isNotEmpty()) {
                        stream.beginText()
                        stream.setFont(bodyPdfFont, p2Font.metrics.fontSizePt)
                        stream.newLineAtOffset(cell.cellPt.leftPt + 2f,
                            p2Font.metrics.baselineIn(cell.cellPt))
                        stream.showText(pcSheetV11PrintableText(cell.drawableText))
                        stream.endText()
                    }
                }
                // C303: golden legend on the EQUIPO ESPECIAL cartela.
                drawP2Cartela(stream, "EQUIPO ESPECIAL", specialArea, "ACTIVO: marcar a lápiz")
                // C343: golden table grammar - a filled header, alternating
                // full-width row fills, a 0.32 pt rule 0.99 pt above each row foot
                // and 0.34 pt column rules, all #94b0c1; no outer frame. Paper-only
                // fill is behind genuine values and marks; no new digital rows.
                stream.saveGraphicsState()
                val headerBottom = specialArea.topPt - specialHeaderHeight
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.addRect(specialArea.leftPt, headerBottom,
                    specialArea.widthPt, specialHeaderHeight)
                stream.fill()
                specialBands.indices.forEach { band ->
                    val top = specialBandTops[band]
                    val bottom = maxOf(specialArea.bottomPt, specialBandTops[band + 1])
                    if (top > bottom) {
                        stream.setNonStrokingColor(
                            if (band % 2 == 0) Color(231, 242, 247) else Color(240, 247, 250))
                        stream.addRect(specialArea.leftPt, bottom,
                            specialArea.widthPt, top - bottom)
                        stream.fill()
                    }
                }
                stream.setStrokingColor(Color(148, 176, 193))
                stream.setLineWidth(0.32f)
                for (lineY in listOf(headerBottom) + specialBandTops.drop(1)) {
                    stream.moveTo(specialArea.leftPt, lineY + 0.99f)
                    stream.lineTo(specialArea.rightPt, lineY + 0.99f)
                    stream.stroke()
                }
                stream.setLineWidth(0.34f)
                var boundary = specialArea.leftPt
                specialColumns.dropLast(1).forEach { width ->
                    boundary += width
                    stream.moveTo(boundary, specialArea.bottomPt)
                    stream.lineTo(boundary, specialArea.topPt)
                    stream.stroke()
                }
                stream.restoreGraphicsState()
                val headings = listOf("UBICACIÓN", "NOMBRE", "DESCRIPCIÓN", "ACTIVO")
                val headingInsets = listOf(3.96f, 4.54f, 4.53f, 2.84f) // C343 golden
                var headerX = specialArea.leftPt
                headings.forEachIndexed { index, label ->
                    // C242: golden column labels; C303: 6.8 pt (ACTIVO 5.5) in #264b68.
                    stream.setNonStrokingColor(Color(38, 75, 104))
                    drawP1Label(stream, label, headerX + headingInsets[index], headerBottom + 4.25f,
                        if (label == "ACTIVO") 5.5f else 6.8f)
                    stream.setNonStrokingColor(Color(36, 57, 75))
                    headerX += specialColumns[index]
                }
                val fieldX = listOf(
                    specialArea.leftPt,
                    specialArea.leftPt + specialColumns[0],
                    specialArea.leftPt + specialColumns[0] + specialColumns[1],
                )
                // C343: golden ACTIVO paper box - 8.79 pt, white, #527c96 0.48 pt.
                val activeBox = 8.79f
                val activeX = specialArea.rightPt - specialColumns.last() / 2f - activeBox / 2f
                specialP2Rows.forEachIndexed { rowIndex, (_, columns) ->
                    val rowTop = specialBandTops[rowIndex]
                    columns.forEachIndexed { column, values ->
                        values.forEachIndexed { line, value ->
                            if (value.isNotEmpty()) {
                                // C343: golden ink - ubicación #264b68, the rest #24394b.
                                stream.setNonStrokingColor(
                                    if (column == 0) Color(38, 75, 104) else Color(36, 57, 75))
                                stream.beginText()
                                stream.setFont(bodyPdfFont, specialColumnFonts[column].metrics.fontSizePt)
                                stream.newLineAtOffset(fieldX[column] + 3.4f,
                                    rowTop - specialFirstBaseline - line * specialFont.metrics.lineHeightPt)
                                stream.showText(pcSheetV11PrintableText(value))
                                stream.endText()
                            }
                        }
                    }
                }
                stream.setNonStrokingColor(Color(36, 57, 75))
                // D-0076 §3.42 / P210: only for zero recorded special items,
                // preprint locations used by real QA character fixtures.
                // Leave free rows for arbitrary locations; do not create objects.
                val manualSpecialLocations = if (data.specialEquipment.isEmpty()) {
                    listOf("Cuello", "Cuerpo", "Mano izquierda", "Mano derecha")
                } else emptyList()
                require(manualSpecialLocations.size < specialCapacity)
                repeat(specialBands.size) { rowIndex ->
                    val rowTop = specialBandTops[rowIndex]
                    if (data.specialEquipment.isEmpty()) {
                        manualSpecialLocations.getOrNull(rowIndex)?.let { location ->
                            stream.setNonStrokingColor(Color(38, 75, 104)) // C343 golden ink
                            stream.beginText()
                            stream.setFont(bodyPdfFont, specialColumnFonts[0].metrics.fontSizePt)
                            stream.newLineAtOffset(fieldX[0] + 3.4f, rowTop - specialFirstBaseline)
                            stream.showText(location)
                            stream.endText()
                            stream.setNonStrokingColor(Color(36, 57, 75))
                        }
                    }
                    // Each writable row requires a genuine unmarked ACTIVO square.
                    val activeY = rowTop -
                        specialBands[rowIndex] * specialRowHeight / 2f - activeBox / 2f
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(255, 255, 255))
                    stream.setStrokingColor(Color(82, 124, 150))
                    stream.setLineWidth(0.48f)
                    stream.addRect(activeX, activeY, activeBox, activeBox)
                    stream.fillAndStroke()
                    stream.restoreGraphicsState()
                    if (specialP2Rows.getOrNull(rowIndex)?.first?.active == true) {
                        // C325: v8 ATTUNED_ACTIVE (filled diamond).
                        drawStateGlyph(stream, 0xE10F, activeX, activeY, activeBox)
                    }
                }
            }

            PDPage(PDRectangle.LETTER).also { page ->
                pdf.addPage(page)
                physicalSectionKeys += "P2 - Equipo y tesoro"
                PDPageContentStream(pdf, page).use { stream ->
                    drawFrameAndHeader(stream, "P2 - Equipo y tesoro")
                    drawP2Modules(stream, localPage = 1)
                }
            }

            PDPage(PDRectangle.LETTER).also { page ->
                pdf.addPage(page)
                physicalSectionKeys += "P3 - Trasfondo / Perfil / Historia"
                PDPageContentStream(pdf, page).use { stream ->
                    drawFrameAndHeader(stream, "P3 - Trasfondo / Perfil / Historia")
                    drawP3FirstPage(stream)
                }
            }
            val p3PagesBefore = physicalSectionKeys.size
            // Text 4 pt inside the continuation paper bands (42..300 / 312..570).
            val p3ContinuationTextArea = PcSheetV11RectPt(42f + p3TextInsetPt, 76f,
                528f - 2 * p3TextInsetPt, 597f)
            addSection("P3 - Narrativa / continuación", PcSheetV11FlowSection.NARRATIVE,
                p3FieldOverflow, 2, area = p3ContinuationTextArea,
                gutterPt = 12f + 2 * p3TextInsetPt)
            val p3StoryPagesBefore = physicalSectionKeys.size
            addSection("P3 - Narrativa / continuación", PcSheetV11FlowSection.NARRATIVE,
                p3StoryOverflow, 2, area = p3ContinuationTextArea,
                font = storyFont,
                firstPageNumber = physicalSectionKeys.size - p3PagesBefore + 1,
                gutterPt = 12f + 2 * p3TextInsetPt)
            // C253/C258: golden P3 footer "HISTORIA A P. 6": each continued P3
            // field by its own name (owner 2026-10-28) and the story, with the
            // first real physical page of each, on the module-link band.
            // C341: «IDEALES CONTINÚA EN PÁG. 4 · HISTORIA CONTINÚA EN PÁG. 5».
            val p3ModuleLinks = PcSheetV11ContinuationText.moduleLinks(
                p3FieldSpecs.indices.mapNotNull { index ->
                    p3FieldHeadingPages["p3-field-heading:" + index]?.let {
                        p3FieldSpecs[index].first to it
                    }
                } + listOfNotNull(
                    if (p3StoryOverflow.isNotEmpty()) "HISTORIA" to (p3StoryPagesBefore + 1)
                    else null,
                ),
            )
            if (p3ModuleLinks != null) {
                require(headerPdfFont.getStringWidth(p3ModuleLinks) * 6.1f / 1000f <= 528f) {
                    "All genuine P3 continuation destinations must fit the footer band."
                }
            }


            // Resource balances and ammunition are drawn only from persisted source
            // records. AMMUNITION is the explicit inventory-usage classification;
            // item names never serve as a guess or as fabricated state.
            val allResources = data.source.sheet.resources.sortedWith(
                compareBy<io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource> {
                    it.sortOrder
                }.thenBy { it.id.toString() },
            )
            val ammunitionIds = data.source.closure.inventoryUsage
                .filter {
                    it.kind ==
                        io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind.AMMUNITION
                }.map { it.itemId }.toSet()
            val ammunitionItems = data.source.sheet.inventoryItems
                .filter { it.id in ammunitionIds }
                .sortedWith(
                    compareBy<io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem> {
                        it.sortOrder
                    }.thenBy { it.id.toString() },
                )
            val ammunitionTracks = ammunitionItems.filter {
                it.quantity in 0..20 && it.id !in p1AmmoIds
            }
                .associate { item -> "ammo-track:" + item.id to item.quantity }
            val resourceLines = buildList {
                // C259 (owner 2026-10-28, «A casillas vacias y gastados/total
                // como espacios de conjuro»): name, one empty paper box per unit
                // of the real total with the small golden gastados/total count,
                // then recovery, source and notes. Totals the boxes cannot show
                // keep the truthful "Quedan x de y" text.
                allResources.forEach { resource ->
                    val extras = buildList {
                        resource.recovery?.takeIf { it.isNotBlank() }?.let { add("Recupera: " + it) }
                        resource.source?.takeIf { it.isNotBlank() }?.let { add("Fuente: " + it) }
                        resource.notes?.takeIf { it.isNotBlank() }?.let { add("Notas: " + it) }
                    }
                    val total = resource.maxValue
                    val boxed = total != null && total in 1..20 && resource.currentValue in 0..total
                    val key = "resource:" + resource.id
                    val name = resource.name
                    if (boxed) {
                        c2ResourceTracks[key] = Triple(name.length + 1,
                            total!! - resource.currentValue, total)
                    }
                    add(PcSheetV11FlowItem(
                        sourceKey = key,
                        text = name + "\n" + (if (boxed) " " else
                            "Quedan " + resource.currentValue + (total?.let { " de " + it } ?: "")) +
                            (if (extras.isEmpty()) "" else "\n" + extras.joinToString(" · ")),
                        keepTogetherIfFits = true,
                    ))
                }
                ammunitionItems.forEach { item ->
                    add(PcSheetV11FlowItem(
                        sourceKey = "ammo:" + item.id,
                        text = "MUNICIÓN " + item.name + " | CANTIDAD " + item.quantity +
                            (item.notes?.takeIf { it.isNotBlank() }?.let {
                                " | NOTAS " + it
                            } ?: ""),
                        keepTogetherIfFits = true,
                    ))
                    if (item.id !in p1AmmoIds) {
                        add(PcSheetV11FlowItem(
                            sourceKey = "ammo-track:" + item.id,
                            text = if (item.quantity in 0..20) "MARCAS:" else
                                "MARCAS: ____ / " + item.quantity,
                            keepTogetherIfFits = true,
                        ))
                    }
                }
            }
            fun unprintedSuffix(
                inputs: List<PcSheetV11FlowItem>,
                printedOnP2: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PlacedFlow,
            ): List<PcSheetV11FlowItem> = inputs.mapNotNull { item ->
                val consumed = printedOnP2.cells.asSequence()
                    .filter { it.localPageIndex == 1 && it.sourceKey == item.sourceKey }
                    .maxOfOrNull { it.sourceEndExclusive } ?: 0
                val originalPrefix = printedOnP2.cells
                    .filter { it.localPageIndex == 1 && it.sourceKey == item.sourceKey }
                    .joinToString("") { it.sourceText }
                require(originalPrefix == item.text.substring(0, consumed)) {
                    "P2 must preserve the exact prefix of " + item.sourceKey
                }
                if (consumed == item.text.length) null
                else item.copy(text = item.text.substring(consumed))
            }

            val remainingTraits = unprintedSuffix(traitLines, traitsPlaced)
            // Compact MIXED C2: categories share the same measured two-column
            // page(s). Distinct headings are real rows, not dedicated blank pages.
            // UUID-backed records keep stable independent source keys and order.
            // C262: a module heading keeps with its first real record, so a
            // golden cartela never stands alone at the foot of a column. Only
            // records without their own source group are joined to it.
            fun MutableList<PcSheetV11FlowItem>.addHeaded(
                heading: PcSheetV11FlowItem, items: List<PcSheetV11FlowItem>,
            ) {
                val first = items.firstOrNull()
                if (first != null && first.groupKey == null) {
                    val key = "c2-keep:" + heading.sourceKey
                    add(heading.copy(groupKey = key))
                    add(first.copy(groupKey = key))
                    addAll(items.drop(1))
                } else {
                    add(heading)
                    addAll(items)
                }
            }
            val mixedC2 = buildList {
                if (p1Unprinted.isNotEmpty()) {
                    add(PcSheetV11FlowItem("c2-heading:p1-overflow",
                        "PERSONAJE, HABILIDADES Y ACCIONES / CONTINUACIÓN",
                        keepTogetherIfFits = true))
                    addAll(p1Unprinted)
                }
                if (detachedSkills.isNotEmpty()) {
                    addHeaded(PcSheetV11FlowItem("c2-heading:skills",
                        "HABILIDADES Y REFERENCIAS", keepTogetherIfFits = true), detachedSkills)
                }
                if (extraCombatData.isNotEmpty()) {
                    addHeaded(PcSheetV11FlowItem("c2-heading:combat",
                        "ATAQUES, NOTAS Y ESPACIOS REALES", keepTogetherIfFits = true), extraCombatData)
                }
                if (resourceLines.isNotEmpty()) {
                    addHeaded(PcSheetV11FlowItem("c2-heading:resources",
                        "RECURSOS Y MUNICIONES", keepTogetherIfFits = true), resourceLines)
                }
                if (remainingTraits.isNotEmpty()) {
                    // Source-accurate P2 continuation follows the P1 streams
                    // inside the same TWO-column measured C2. Extra pages are
                    // generated only if actual text requires them.
                    addHeaded(PcSheetV11FlowItem("c2-heading:traits",
                        "RASGOS / CONTINUACIÓN P2", keepTogetherIfFits = true), remainingTraits)
                }
            }
            // C323: golden C2 columns 39.69..301.04 and 310.96..572.32 (9.92 pt gutter).
            addSection("C2 - Habilidades, acciones y recursos",
                PcSheetV11FlowSection.TRAITS,
                mixedC2, columns = 2, ammunitionTracks = ammunitionTracks,
                // C324: content starts at the golden first-cartela top (y685.7).
                area = PcSheetV11RectPt(39.69f, pageArea.bottomPt, 532.63f, 685.7f - pageArea.bottomPt),
                gutterPt = 9.92f, font = c2Font)

            // Reflow the exact P2-unprinted equipment/treasure suffixes into
            // FULL-HEIGHT C2 paired tables. Never redraw quantities already
            // printed on P2; keep every original label code point and source ID.
            fun remainingPairedRows(
                complete: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData,
                placed: PcSheetV11PairedTablePlaced,
            ): io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData {
                val p2Eligible = placed.data.rows.map { it.sourceKey }.toSet()
                val rows = complete.rows.mapNotNull { row ->
                    if (row.sourceKey !in p2Eligible) return@mapNotNull row
                    val prefixParts = placed.labelFlow.fragments.filter {
                        it.localPageIndex == 1 && it.sourceKey == row.sourceKey
                    }.sortedWith(compareBy({ it.columnIndex }, { it.startOffset }))
                    val printedPrefix = prefixParts.joinToString("") { it.text }
                    require(row.label.startsWith(printedPrefix)) {
                        "P2 equipment must preserve each source label prefix exactly."
                    }
                    if (printedPrefix.length == row.label.length) null
                    else row.copy(label = row.label.substring(printedPrefix.length))
                }
                return complete.copy(rows = rows)
            }

            val fullC2PairedArea = PcSheetV11RectPt(42f, 76f, 528f, 626f)
            val pairedRemainders = listOf(
                Triple("EQUIPO", completeEquipment, inventoryPlaced),
                Triple("TESORO", completeTreasure, treasurePlaced),
            ).map { (label, complete, original) ->
                Triple(label, remainingPairedRows(complete, original), original)
            }
            fun placeC2Paired(
                remaining: io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11PairedTableData,
                area: PcSheetV11RectPt,
            ): PcSheetV11PairedTablePlaced = PcSheetV11PairedTableComposer.place(
                remaining, area, 12f,
                PcSheetV11PairedAmountRouting.c2AmountWidth(remaining, c2TableFont.width),
                PcSheetV11PairedAmountRouting.AMOUNT_GAP_PT,
                c2TableFont.metrics.lineHeightPt, c2TableFont.width,
                balanceSinglePageC2 = true,
            )
            data class CompactPairedC2(
                val topArea: PcSheetV11RectPt,
                val bottomArea: PcSheetV11RectPt,
                val equipment: PcSheetV11PairedTablePlaced,
                val treasure: PcSheetV11PairedTablePlaced,
            )
            // C203/C205: if both genuine continuations fit on one physical
            // sheet, keep their separate PAIR grammars but stack the two
            // modules. Formerly even five short equipment items plus three
            // currencies forced two wasteful C2 pages. Never squeeze font,
            // move P2 data, truncate labels, or join separate source IDs.
            // Oversized quantities and dense/multipage tables retain their
            // existing dedicated full-height C2 path without changes.
            val compactPaired = if (pairedRemainders.all { it.second.rows.isNotEmpty() } &&
                pairedRemainders.all { (_, remaining, _) ->
                    PcSheetV11PairedAmountRouting.c2AmountWidth(remaining, c2TableFont.width) <=
                        PcSheetV11PairedAmountRouting.BASE_AMOUNT_WIDTH_PT
                }
            ) {
                val equipmentRemainder = pairedRemainders[0].second
                val treasureRemainder = pairedRemainders[1].second
                val fullEquipment = placeC2Paired(equipmentRemainder, fullC2PairedArea)
                val fullTreasure = placeC2Paired(treasureRemainder, fullC2PairedArea)
                if (fullEquipment.localPageCount == 1 && fullTreasure.localPageCount == 1) {
                    val occupiedEquipment = (fullEquipment.labels.maxOfOrNull {
                        it.rowIndex + 1
                    } ?: 0) * c2TableFont.metrics.lineHeightPt
                    // 14.5pt blue cartela ABOVE each module, plus a 5.5pt
                    // physical gap. The two content rectangles never overlap.
                    val upperHeight = maxOf(70f, occupiedEquipment + 12f)
                    val lowerTop = fullC2PairedArea.topPt - upperHeight - 20f
                    if (lowerTop > fullC2PairedArea.bottomPt + 55f) {
                        val topArea = PcSheetV11RectPt(
                            42f, fullC2PairedArea.topPt - upperHeight, 528f, upperHeight,
                        )
                        val bottomArea = PcSheetV11RectPt(
                            42f, fullC2PairedArea.bottomPt, 528f,
                            lowerTop - fullC2PairedArea.bottomPt,
                        )
                        val top = placeC2Paired(equipmentRemainder, topArea)
                        val bottom = placeC2Paired(treasureRemainder, bottomArea)
                        if (top.localPageCount == 1 && bottom.localPageCount == 1) {
                            CompactPairedC2(topArea, bottomArea, top, bottom)
                        } else null
                    } else null
                } else null
            } else null
            data class DeferredOrdinaryForSpecial(
                val label: String,
                val original: PcSheetV11PairedTablePlaced,
                val expanded: PcSheetV11PairedTablePlaced,
                val upperHeight: Float,
            )
            var deferredOrdinaryForSpecial: DeferredOrdinaryForSpecial? = null
            fun drawSeparatePairedC2(
                label: String,
                original: PcSheetV11PairedTablePlaced,
                expanded: PcSheetV11PairedTablePlaced,
            ) {
                val amountsAlreadyOnP2 = original.quantities.asSequence()
                    .filter { it.localPageIndex == 1 }
                    .map { it.sourceKey }.toSet()
                for (localPage in 1..expanded.localPageCount) {
                    val page = PDPage(PDRectangle.LETTER)
                    pdf.addPage(page)
                    physicalSectionKeys += "C2 - Equipo y tesoro / " + label
                    PDPageContentStream(pdf, page).use { stream ->
                        drawFrameAndHeader(stream,
                            "C2 - Equipo y tesoro / " + label + " / " + localPage)
                        drawPaired(stream, label + " / CANTIDAD",
                            fullC2PairedArea, expanded, localPage, amountsAlreadyOnP2,
                            compactC2 = true, tableFont = c2TableFont)
                    }
                }
            }
            if (compactPaired != null) {
                val page = PDPage(PDRectangle.LETTER)
                pdf.addPage(page)
                physicalSectionKeys += "C2 - Equipo y tesoro / EQUIPO Y TESORO"
                PDPageContentStream(pdf, page).use { stream ->
                    drawFrameAndHeader(stream, "C2 - Equipo y tesoro / EQUIPO Y TESORO")
                    val equipoAmounts = pairedRemainders[0].third.quantities
                        .filter { it.localPageIndex == 1 }
                        .map { it.sourceKey }.toSet()
                    val tesoroAmounts = pairedRemainders[1].third.quantities
                        .filter { it.localPageIndex == 1 }
                        .map { it.sourceKey }.toSet()
                    drawPaired(stream, "EQUIPO / CANTIDAD", compactPaired.topArea,
                        compactPaired.equipment, 1, equipoAmounts,
                        compactC2 = true, tableFont = c2TableFont, writeC2Notes = false)
                    drawPaired(stream, "TESORO / CANTIDAD", compactPaired.bottomArea,
                        compactPaired.treasure, 1, tesoroAmounts, compactC2 = true, tableFont = c2TableFont)
                }
            } else {
                // Original independent page path remains authoritative when the
                // measured physical envelopes do not fit together.
                for ((label, remaining, original) in pairedRemainders) {
                    if (remaining.rows.isEmpty()) continue
                    val expanded = placeC2Paired(remaining, fullC2PairedArea)
                    val occupied = (expanded.labels.maxOfOrNull { it.rowIndex + 1 }
                        ?: 0) * c2TableFont.metrics.lineHeightPt
                    val upperHeight = maxOf(90f, occupied + 18f)
                    // Only ONE short, standard-width ordinary pair may join
                    // ONE deferred special record. The real full-width special
                    // table is measured below before this reservation commits.
                    // Large, multi-page or currency cases remain unchanged.
                    val reserveForSpecial = label == "EQUIPO" &&
                        pairedRemainders.count { it.second.rows.isNotEmpty() } == 1 &&
                        specialDeferredToC2.size == 1 &&
                        PcSheetV11PairedAmountRouting.c2AmountWidth(
                            remaining, c2TableFont.width,
                        ) <= PcSheetV11PairedAmountRouting.BASE_AMOUNT_WIDTH_PT &&
                        expanded.localPageCount == 1 && upperHeight <= 180f
                    if (reserveForSpecial) {
                        deferredOrdinaryForSpecial =
                            DeferredOrdinaryForSpecial(label, original, expanded, upperHeight)
                    } else {
                        drawSeparatePairedC2(label, original, expanded)
                    }
                }
            }

            // The golden C2 extends the SAME full-width four-field grammar as
            // P2. True source-backed rows may span physical pages or become
            // taller for long descriptions, never compress or truncate text.
            // The shared text flow independently verifies all source offsets.
            fun specialC2Lines(raw: String?, widthPt: Float, font: PcSheetV11ResolvedFont): List<String> {
                if (raw.isNullOrBlank()) return emptyList()
                val measured = PcSheetV11TextFlow.paginate(
                    PcSheetV11FlowSection.SPECIAL_EQUIPMENT,
                    listOf(PcSheetV11FlowItem("c2-source", raw,
                        keepTogetherIfFits = true)),
                    1, PcSheetV11TextColumn(widthPt - 6.8f, 626f, // C343 golden inset
                        font.metrics.lineHeightPt),
                    font.width,
                )
                return measured.fragments.flatMap { it.lineRanges }.map { range ->
                    raw.substring(range.startOffset, range.endExclusive)
                        .trimEnd('\r', '\n')
                }
            }
            val specialC2Rows = specialDeferredToC2.map { item ->
                val description = listOfNotNull(
                    item.description?.takeIf { it.isNotBlank() },
                    item.notes?.takeIf { it.isNotBlank() }?.let { "NOTAS: " + it },
                ).joinToString(" / ")
                item to listOf(item.location, item.name, description).mapIndexed {
                    column, value -> specialC2Lines(value, specialColumns[column], specialColumnFonts[column])
                }
            }
            data class SpecialC2Fragment(
                val rowIndex: Int,
                val startLine: Int,
                val endLine: Int,
                val heightPt: Float,
            )
            // C343 golden width; C344: the table starts under the golden C2 cartela
            // band (y662.74..685.7, 1.98 pt above the table), as golden C2 p5.
            val c2SpecialArea = PcSheetV11RectPt(39.69f, 76f, 532.62f, 660.76f - 76f)
            val c2SpecialPages = mutableListOf<List<SpecialC2Fragment>>()
            var currentC2 = mutableListOf<SpecialC2Fragment>()
            var availableC2 = c2SpecialArea.heightPt - specialHeaderHeight
            fun flushC2Special() {
                if (currentC2.isNotEmpty()) c2SpecialPages += currentC2.toList()
                currentC2 = mutableListOf()
                availableC2 = c2SpecialArea.heightPt - specialHeaderHeight
            }
            specialC2Rows.forEachIndexed { rowIndex, (_, fields) ->
                val totalLines = maxOf(1, fields.maxOf { it.size })
                var consumed = 0
                while (consumed < totalLines) {
                    var lines = minOf(totalLines - consumed,
                        ((availableC2 - 8f) / specialFont.metrics.lineHeightPt).toInt())
                    // C343: golden 11.77 pt first baseline and a 4 pt foot.
                    var height = maxOf(specialRowHeight,
                        lines * specialFont.metrics.lineHeightPt + specialFirstBaseline - 5f)
                    while (lines > 1 && height > availableC2) {
                        lines--
                        height = maxOf(specialRowHeight,
                            lines * specialFont.metrics.lineHeightPt + specialFirstBaseline - 5f)
                    }
                    if (lines <= 0 || height > availableC2) {
                        require(currentC2.isNotEmpty()) {
                            "A single real special-equipment line cannot fit C2."
                        }
                        flushC2Special()
                        continue
                    }
                    currentC2 += SpecialC2Fragment(
                        rowIndex, consumed, consumed + lines, height,
                    )
                    availableC2 -= height
                    consumed += lines
                }
            }
            flushC2Special()
            require(c2SpecialPages.flatten().groupBy { it.rowIndex }.size ==
                specialC2Rows.size) {
                "Every deferred special item must occupy at least one physical C2 row."
            }
            // Join a genuinely short special record with a genuinely
            // short ordinary pair only if the already-measured complete
            // special row fits in its own lower full-width rectangle.
            // If not, flush the deferred ordinary page unchanged before the
            // original special continuation pages; never attempt compression.
            val sharedC2 = deferredOrdinaryForSpecial?.takeIf { pending ->
                c2SpecialPages.size == 1 && c2SpecialPages.single().size == 1 &&
                    c2SpecialPages.single().single().startLine == 0 &&
                    specialHeaderHeight +
                        c2SpecialPages.single().sumOf { it.heightPt.toDouble() } <
                        c2SpecialArea.heightPt - pending.upperHeight - 24.94f - 10f
            }
            if (deferredOrdinaryForSpecial != null && sharedC2 == null) {
                val separate = requireNotNull(deferredOrdinaryForSpecial)
                drawSeparatePairedC2(separate.label, separate.original, separate.expanded)
            }
            c2SpecialPages.forEachIndexed { pageIndex, fragments ->
                val shared = sharedC2?.takeIf { pageIndex == 0 }
                val drawingArea = if (shared != null) PcSheetV11RectPt(
                    c2SpecialArea.leftPt, c2SpecialArea.bottomPt, c2SpecialArea.widthPt,
                    c2SpecialArea.heightPt - shared.upperHeight - 24.94f, // C344: cartela gap
                ) else c2SpecialArea
                val page = PDPage(PDRectangle.LETTER)
                pdf.addPage(page)
                physicalSectionKeys += if (shared != null)
                    "C2 - Equipo y tesoro / EQUIPO + EQUIPO ESPECIAL"
                    else "C2 - Equipo especial / continuación"
                PDPageContentStream(pdf, page).use { stream ->
                    if (shared != null) {
                        drawFrameAndHeader(stream,
                            "C2 - Equipo y tesoro / EQUIPO + EQUIPO ESPECIAL")
                        val topArea = PcSheetV11RectPt(
                            42f, c2SpecialArea.topPt - shared.upperHeight,
                            528f, shared.upperHeight,
                        )
                        val topPlaced = placeC2Paired(
                            pairedRemainders.first().second, topArea,
                        )
                        require(topPlaced.localPageCount == 1) {
                            "Measured shared C2 ordinary equipment cannot fit its top panel."
                        }
                        val alreadyP2 = shared.original.quantities
                            .filter { it.localPageIndex == 1 }
                            .map { it.sourceKey }.toSet()
                        drawPaired(stream, "EQUIPO / CANTIDAD", topArea,
                            topPlaced, 1, alreadyP2, compactC2 = true, tableFont = c2TableFont,
                            writeC2Notes = false)
                    } else {
                        drawFrameAndHeader(stream,
                            "C2 - Equipo especial / continuación / " + (pageIndex + 1))
                    }
                    // C344: golden C2 p5 - the full-width EQUIPO ESPECIAL cartela with
                    // its base page before the right diamond («VIENE DE PÁG. n», C341).
                    drawP2Cartela(stream, "EQUIPO ESPECIAL", drawingArea)
                    val specialBase = physicalSectionKeys.indexOf("P2 - Equipo y tesoro") + 1
                    val specialOrigin = pcSheetV11PrintableText(
                        PcSheetV11ContinuationText.comesFrom(specialBase))
                    stream.setNonStrokingColor(Color(38, 75, 104))
                    drawP1Label(stream, specialOrigin, drawingArea.rightPt + 0.01f - 13f -
                        headerPdfFont.getStringWidth(specialOrigin) * 6.1f / 1000f,
                        drawingArea.topPt + 1.98f + 5.99f, 6.1f)
                    stream.setNonStrokingColor(Color(36, 57, 75))
                    // An oversized source row can continue with ONLY its
                    // description: name/location were drawn on the prior
                    // physical page. Identify the SAME record in the free
                    // C2 header strip instead of repeating its data fields.
                    // This uses the approved header grammar and consumes no
                    // row or additional page.
                    val continuedPart = fragments.firstOrNull()
                        ?.takeIf { it.startLine > 0 }
                    if (continuedPart != null) {
                        val item = specialC2Rows[continuedPart.rowIndex].first
                        val x = 192f
                        val fontPt = 7f
                        val available = drawingArea.rightPt - x - 3f
                        fun fits(text: String) =
                            bodyPdfFont.getStringWidth(pcSheetV11PrintableText(text)) *
                                fontPt / 1000f <= available
                        // C341: a continued row always comes from the previous page.
                        val fromPage = pdf.numberOfPages - 1
                        val full = PcSheetV11ContinuationText.arrived("EQUIPO: " + item.name, fromPage)
                        val sourceId = PcSheetV11ContinuationText.arrived("ID: " + item.sourceId, fromPage)
                        val plainId = "ID: " + item.sourceId
                        // The full name is drawn losslessly in its real
                        // measured table cells. Its PAPER navigation label is
                        // single-line: explicit source CR/LF must never be sent
                        // to PDFBox getStringWidth/showText, which reject those
                        // control glyphs. Use the actual persisted UUID instead.
                        val canUseNameInHeader = item.name.none {
                            it == '\n' || it == '\r'
                        }
                        val label = when {
                            canUseNameInHeader && fits(full) -> full
                            fits(sourceId) -> sourceId
                            fits(plainId) -> plainId
                            else -> error("Real C2 special equipment continuation ID " +
                                "does not fit the existing physical header.")
                        }
                        require(headerPdfFont.getStringWidth("EQUIPO ESPECIAL") *
                            12f / 1000f < x - 42f - 10f) {
                            "Special equipment continuation must not overlap the heading."
                        }
                        stream.saveGraphicsState()
                        stream.setNonStrokingColor(Color(80, 122, 151))
                        stream.beginText()
                        stream.setFont(bodyPdfFont, fontPt)
                        stream.newLineAtOffset(x, 708f) // C344: the free header strip, above the cartela
                        stream.showText(pcSheetV11PrintableText(label))
                        stream.endText()
                        stream.restoreGraphicsState()
                    }
                    val printedHeight = specialHeaderHeight +
                        fragments.sumOf { it.heightPt.toDouble() }.toFloat()
                    val tableBottom = drawingArea.topPt - printedHeight
                    require(tableBottom >= drawingArea.bottomPt - 0.1f)
                    // C343: the P2 golden table grammar on C2 - filled header,
                    // alternating row fills, 0.32 pt row rules 0.99 pt above each
                    // foot, 0.34 pt column rules (#94b0c1), no outer frame.
                    val left = drawingArea.leftPt
                    val right = drawingArea.rightPt
                    val headerBottom = drawingArea.topPt - specialHeaderHeight
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(231, 242, 247))
                    stream.addRect(left, headerBottom, right - left, specialHeaderHeight)
                    stream.fill()
                    var fillTop = headerBottom
                    fragments.forEachIndexed { index, part ->
                        stream.setNonStrokingColor(
                            if (index % 2 == 0) Color(231, 242, 247) else Color(240, 247, 250))
                        stream.addRect(left, fillTop - part.heightPt, right - left, part.heightPt)
                        stream.fill()
                        fillTop -= part.heightPt
                    }
                    stream.setStrokingColor(Color(148, 176, 193))
                    stream.setLineWidth(0.32f)
                    stream.moveTo(left, headerBottom + 0.99f)
                    stream.lineTo(right, headerBottom + 0.99f)
                    stream.stroke()
                    stream.setLineWidth(0.34f)
                    var separator = left
                    specialColumns.dropLast(1).forEach { width ->
                        separator += width
                        stream.moveTo(separator, tableBottom)
                        stream.lineTo(separator, drawingArea.topPt)
                        stream.stroke()
                    }
                    stream.restoreGraphicsState()
                    val headingInsets = listOf(3.96f, 4.54f, 4.53f, 2.84f) // C343 golden
                    var headerX = left
                    listOf("UBICACIÓN", "NOMBRE", "DESCRIPCIÓN", "ACTIVO")
                        .forEachIndexed { index, label ->
                            // C242: golden column labels; C303: 6.8 pt (ACTIVO 5.5) in #264b68.
                            stream.setNonStrokingColor(Color(38, 75, 104))
                            drawP1Label(stream, label, headerX + headingInsets[index], headerBottom + 4.25f,
                                if (label == "ACTIVO") 5.5f else 6.8f)
                            stream.setNonStrokingColor(Color(36, 57, 75))
                            headerX += specialColumns[index]
                        }
                    val positions = listOf(left, left + specialColumns[0],
                        left + specialColumns[0] + specialColumns[1])
                    var rowTop = headerBottom
                    fragments.forEach { part ->
                        val (item, fields) = specialC2Rows[part.rowIndex]
                        fields.forEachIndexed { column, allLines ->
                            val shown = allLines.drop(part.startLine)
                                .take(part.endLine - part.startLine)
                            shown.forEachIndexed { index, line ->
                                if (line.isNotEmpty()) {
                                    // C343: golden sizes and ink per column.
                                    stream.setNonStrokingColor(
                                        if (column == 0) Color(38, 75, 104) else Color(36, 57, 75))
                                    stream.beginText()
                                    stream.setFont(bodyPdfFont, specialColumnFonts[column].metrics.fontSizePt)
                                    stream.newLineAtOffset(positions[column] + 3.4f, rowTop -
                                        specialFirstBaseline - index * specialFont.metrics.lineHeightPt)
                                    stream.showText(pcSheetV11PrintableText(line))
                                    stream.endText()
                                }
                            }
                        }
                        stream.setNonStrokingColor(Color(36, 57, 75))
                        if (part.startLine == 0) {
                            // C343: golden ACTIVO paper box, centred in the first pitch.
                            val box = 8.79f
                            val markerX = right - specialColumns.last() / 2f - box / 2f
                            val markerY = rowTop - specialRowHeight / 2f - box / 2f
                            stream.saveGraphicsState()
                            stream.setNonStrokingColor(Color(255, 255, 255))
                            stream.setStrokingColor(Color(82, 124, 150))
                            stream.setLineWidth(0.48f)
                            stream.addRect(markerX, markerY, box, box)
                            stream.fillAndStroke()
                            stream.restoreGraphicsState()
                            if (item.active == true) {
                                // C325: v8 ATTUNED_ACTIVE (filled diamond).
                                drawStateGlyph(stream, 0xE10F, markerX, markerY, box)
                            }
                        }
                        rowTop -= part.heightPt
                        stream.saveGraphicsState()
                        stream.setStrokingColor(Color(148, 176, 193))
                        stream.setLineWidth(0.32f)
                        stream.moveTo(left, rowTop + 0.99f)
                        stream.lineTo(right, rowTop + 0.99f)
                        stream.stroke()
                        stream.restoreGraphicsState()
                    }
                    require(kotlin.math.abs(rowTop - tableBottom) <= 0.5f)
                    drawUnusedC2NotesGrid(stream, tableBottom)
                }
            }

            // C209: frozen owner-approved V11 extreme A/B places source-backed
            // C2 continuation pages after P3 and BEFORE P4. D-0076 §3.30
            // likewise prefers C2 adjacent to its originating base modules.
            // P4's own measured spell text and 3-column layout are untouched;
            // footer/backlinks are resolved from FINAL physical pages.
            if (data.hasQuickSpellList) {
                // Contract: a spell page, when present, reserves visible levels 0–9
                // even if a given level has no real spells; 10–12 require real
                // epic rules data and are not invented. Sources/preparation stay
                // linked to the specific persisted casting-source association.
                fun quickRows(
                    manuscriptRowsPerLevel: Int,
                    extraLastLevel9PaperRows: Int = 0,
                ): List<PcSheetV11FlowItem> = buildList {
                    require(manuscriptRowsPerLevel >= 1 && extraLastLevel9PaperRows >= 0)
                    val levels = (0..9).toList() +
                        data.spells.map { it.spell.level }.filter { it >= 10 }.distinct().sorted()
                    levels.forEach { level ->
                        add(PcSheetV11FlowItem("spell-level:" + level,
                            // C330: golden level titles «TRUCOS 0», «NIVEL 2».
                            if (level == 0) "TRUCOS 0" else "NIVEL " + level,
                            keepTogetherIfFits = true))
                        val spellsHere = data.spells.filter { it.spell.level == level }
                        spellsHere.forEach { entry ->
                            p4SpellRows.getValue(entry.spell.id).forEach { (key, sourceInfo) ->
                                add(PcSheetV11FlowItem(key,
                                    // Five measured spaces separate the actual first
                                    // spell glyph from the 7pt vector box (verified original
                                    // 3-space result overlapped its border by ~1.8pt).
                                    // No source name is abbreviated or trimmed.
                                    quickInset + entry.spell.name +
                                        (if (sourceInfo.isBlank() || key in p4RightSources) ""
                                        else " | " + sourceInfo),
                                    keepTogetherIfFits = true))
                            }
                        }
                        // Rows are PAPER-ONLY: no fabricated spell, source, preparation
                        // or spell slot. Every level is writable, even when populated.
                        val paperRows = (if (level in 0..9)
                            manuscriptRowsPerLevel else 1) +
                            (if (level == 9) extraLastLevel9PaperRows else 0)
                        repeat(paperRows) { row ->
                            add(PcSheetV11FlowItem(
                                "spell-handwritten:" + level + ":" + row,
                                "[ ] ______________________",
                                keepTogetherIfFits = true,
                            ))
                        }
                    }
                }
                // G2/G3: measure the TRUE three-column page before reserving
                // manuscript lines. The original internal proof shortened the
                // page area and stranded most of a Letter sheet as blank paper.
                // Expand paper-only entries, not digital spell data, as far as
                // one physical page permits. Never create a second filler page.
                // C309: golden P4 columns 39.69..207.5, 222.09..389.91, 404.5..572.31.
                // C322: golden P4 rows from y630.43 down to 64.92.
                val p4WritingArea = PcSheetV11RectPt(39.69f, 64.92f, 532.62f, 565.51f)
                val p4GutterPt = 14.59f
                val minimalQuick = quickRows(1)
                val minimalPlacement = quickFont.place(
                    PcSheetV11FlowSection.QUICK_SPELLS,
                    minimalQuick, p4WritingArea, 3, p4GutterPt,
                )
                val quick = if (minimalPlacement.sourceFlow.localPageCount == 1) {
                    var lowest = 1
                    var highest = 20
                    while (lowest < highest) {
                        val candidateRows = (lowest + highest + 1) / 2
                        val candidate = quickFont.place(
                            PcSheetV11FlowSection.QUICK_SPELLS,
                            quickRows(candidateRows), p4WritingArea, 3, p4GutterPt,
                        )
                        if (candidate.sourceFlow.localPageCount == 1) {
                            lowest = candidateRows
                        } else {
                            highest = candidateRows - 1
                        }
                    }
                    val baseRows = quickRows(lowest)
                    val basePlacement = quickFont.place(
                        PcSheetV11FlowSection.QUICK_SPELLS,
                        baseRows, p4WritingArea, 3, p4GutterPt,
                    )
                    require(basePlacement.sourceFlow.localPageCount == 1)
                    // Original golden P4 fills each column with handwriting
                    // paper. Measured genuine App sheets often leave the LAST
                    // column 7-8 writable rows short even when all source
                    // levels are present. Grow ONLY the tail of the final
                    // non-epic level 9, after every real spell/heading.
                    // Never add a P4 filler page or give fake epic levels.
                    val finalLevel9InLastColumn =
                        data.spells.none { it.spell.level >= 10 } &&
                            basePlacement.cells.any { cell ->
                                cell.sourceKey == "spell-handwritten:9:" + (lowest - 1) &&
                                    cell.localPageIndex == 1 && cell.columnIndex == 3
                            }
                    if (finalLevel9InLastColumn) {
                        var extraLow = 0
                        var extraHigh = 42
                        while (extraLow < extraHigh) {
                            val extraCandidate = (extraLow + extraHigh + 1) / 2
                            val candidate = quickFont.place(
                                PcSheetV11FlowSection.QUICK_SPELLS,
                                quickRows(lowest, extraCandidate),
                                p4WritingArea, 3, p4GutterPt,
                            )
                            if (candidate.sourceFlow.localPageCount == 1) {
                                extraLow = extraCandidate
                            } else {
                                extraHigh = extraCandidate - 1
                            }
                        }
                        // Only adds genuine vector WRITING ROWS at the end.
                        // Existing source cells keep identical coordinates,
                        // font, value and physical page number.
                        quickRows(lowest, extraLow)
                    } else baseRows
                } else minimalQuick
                // C261: golden P4 rhythm, Fira 6.8 pt on 15.7 pt rows.
                addSection("P4 - Conjuros", PcSheetV11FlowSection.QUICK_SPELLS,
                    quick, 3, area = p4WritingArea, gutterPt = p4GutterPt, font = quickFont)
            }

            // C238: golden note cards are one blank line apart (paper only).
            // C314: golden note spacing - one more paper row after a note title.
            addSection("C-N3 - Notas digitales", PcSheetV11FlowSection.DIGITAL_NOTES,
                texts.digitalNotes.map { item ->
                    // C333: an untitled note opens with one blank row (a real space
                    // line) so its first line clears the card edge and inner rule.
                    val spaced = if (item.sourceKey in titledNoteKeys)
                        item.text.replaceFirst("\n", "\n\n") else " \n" + item.text
                    item.copy(text = spaced, leadingSeparationLines = 1)
                }, 1,
                // C313: notes start under the golden NOTAS cartela (y670.68).
                area = PcSheetV11RectPt(pageArea.leftPt + 6f, pageArea.bottomPt,
                    pageArea.widthPt - 12f, 670.68f - pageArea.bottomPt),
                font = notesFont)
            // C289: golden Libro columns — text 48.19..292.82 and 319.18..563.81 pt.
            val bookArea = PcSheetV11RectPt(48.19f, pageArea.bottomPt, 515.62f, pageArea.heightPt)
            val bookGutter = 26.36f
            val bookColumnPt = (515.62f - 26.36f) / 2f
            data.spellbookEntries?.takeIf { it.isNotEmpty() }?.let { spells ->
                // The only optional annex, with real two-column cards. All spell
                // content remains source-backed; no fabricated school or rules.
                val cards = spells.map { entry ->
                    val spell = entry.spell
                    // D-0076 §3.47 / BK06: the OWNER approved the essential
                    // spellbook card, not the expanded technical alternative.
                    // A card contains source-backed NAME, LEVEL, SCHOOL (only
                    // when actually available) and the ENTIRE DESCRIPTION.
                    // Q14b: the school comes only from CharacterSpell.school. Never
                    // guess it from class, source, names or description.
                    // Keep time/range/components/duration/preparation/notes
                    // in the App source and other independently contracted
                    // views; they are NOT printed in this optional card.
                    // C240 (golden V11 book card): NIVEL sits right-aligned on the
                    // name line and the description follows without a label. A
                    // name that cannot share its line keeps the previous layout.
                    val levelLabel = "NIVEL " + spell.level
                    val inlineLevel = spell.name.none { it == '\r' || it == '\n' || it == '\t' } &&
                        bookFont.width.measurePt(spell.name) +
                        headerPdfFont.getStringWidth(levelLabel) * 7f / 1000f + 10f <= bookColumnPt
                    // Q14b (golden): the real school, uppercase, on its own line under
                    // the name; a spell without a school gets no line at all.
                    val school = spell.school?.trim()?.takeIf { it.isNotEmpty() }?.uppercase()
                    // C288: golden heading band (31.2 pt): name, school and one more
                    // band row, 3 x 10.5 pt; the name and school sit at golden baselines.
                    val heading = if (inlineLevel) listOfNotNull(spell.name, school, "")
                        else listOfNotNull(spell.name, levelLabel, school, "DESCRIPCIÓN:")
                    if (school != null) {
                        val schoolIndex = if (inlineLevel) 1 else 2
                        bookSchools["book:" + spell.id] =
                            heading.take(schoolIndex).sumOf { it.length + 1 } to school
                    }
                    if (inlineLevel) bookInlineLevels["book:" + spell.id] = levelLabel
                    bookHeadingEnds["book:" + spell.id] = heading.sumOf { it.length + 1 }
                    // C331: the golden card keeps one blank writing row under the
                    // description (its foot sits ~20.5 pt below the last line top).
                    // The flow drops a trailing newline, so the row is one real space.
                    val lines = heading + spell.description + " "
                    PcSheetV11FlowItem(
                        sourceKey = "book:" + spell.id,
                        text = lines.joinToString("\n"),
                        keepTogetherIfFits = true,
                        leadingSeparationLines = 1, // BK08: one REAL measured blank row between cards.
                    )
                }
                val contentFlow = bookFont.place(
                    PcSheetV11FlowSection.SPELLBOOK, cards, bookArea, 2, bookGutter,
                )
                require(contentFlow.sourceFlow.localPageCount > 0)
                val firstPagesByKey = contentFlow.cells.groupBy { it.sourceKey }
                    .mapValues { (_, cells) -> cells.minOf { it.localPageIndex } }
                require(firstPagesByKey.size == cards.size) {
                    "Every real spell must have a distinct physical book entry."
                }
                val pagesBeforeIndex = pdf.numberOfPages
                fun buildIndex(indexPageCount: Int): List<PcSheetV11FlowItem> =
                    spells.map { entry ->
                        val key = "book:" + entry.spell.id
                        val relativePage = firstPagesByKey.getValue(key)
                        PcSheetV11FlowItem(
                            "index:" + entry.spell.id,
                            "NIVEL " + entry.spell.level + " | " + entry.spell.name +
                                " | PÁGINA " +
                                (pagesBeforeIndex + indexPageCount + relativePage),
                            keepTogetherIfFits = true,
                        )
                    }

                // Page numbers are global PHYSICAL pages. If a long index spills
                // onto multiple pages, converge on its measured page count, never
                // guess the next physical page from a fixed section number.
                var indexPageCount = 1
                repeat(8) {
                    val measured = bookFont.place(
                        PcSheetV11FlowSection.SPELLBOOK,
                        buildIndex(indexPageCount),
                        pageArea, 2, 12f,
                    ).sourceFlow.localPageCount
                    require(measured > 0)
                    if (measured == indexPageCount) return@repeat
                    indexPageCount = measured
                }
                val indexEntries = buildIndex(indexPageCount)
                val finalIndexFlow = bookFont.place(
                    PcSheetV11FlowSection.SPELLBOOK, indexEntries, pageArea, 2, 12f,
                )
                require(finalIndexFlow.sourceFlow.localPageCount == indexPageCount) {
                    "Physical spellbook index pagination did not converge."
                }
                require(indexEntries.size == spells.size)
                addSection("Índice del Libro de Conjuros",
                    PcSheetV11FlowSection.SPELLBOOK, indexEntries, 2, font = bookFont)
                require(pdf.numberOfPages == pagesBeforeIndex + indexPageCount)
                addSection("Libro de conjuros",
                    PcSheetV11FlowSection.SPELLBOOK, cards, 2, area = bookArea,
                    gutterPt = bookGutter, font = bookFont)
                require(pdf.numberOfPages ==
                    pagesBeforeIndex + indexPageCount + contentFlow.sourceFlow.localPageCount)
            }

            val finalPage = PDPage(PDRectangle.LETTER)
            pdf.addPage(finalPage)
            physicalSectionKeys += "NOTAS-FINAL"
            PDPageContentStream(pdf, finalPage).use { stream ->
                drawFrameAndHeader(stream, "NOTAS", lastHandwrittenPage = true)
                // Frozen owner-approved V11 golden (7 final NOTAS examples):
                // 17 softly alternating ruled rows per column, followed by a
                // pale blue 34 x 20 COMPLETE grid. All dimensions are physical
                // US-Letter points, measured on the original golden PDF vectors.
                // Do not use inherited black / 1pt PDFBox graphics defaults.
                stream.saveGraphicsState()
                try {
                    val noteLeft = 39.685f
                    val noteRight = 310.787f
                    val ruledWidth = 261.528f
                    for (columnLeft in listOf(noteLeft, noteRight)) {
                        for (row in 0 until 17) {
                            val base = 676.551f - row * 18.993f
                            stream.setNonStrokingColor(
                                if (row % 2 == 0) Color(231, 242, 247)
                                else Color(240, 247, 250),
                            )
                            stream.addRect(columnLeft, base, ruledWidth, 18.993f)
                            stream.fill()
                            stream.setStrokingColor(Color(148, 176, 193))
                            stream.setLineWidth(0.32f)
                            stream.moveTo(columnLeft, base + 1f)
                            stream.lineTo(columnLeft + ruledWidth, base + 1f)
                            stream.stroke()
                        }
                    }
                    val gridLeft = 39.685f
                    val gridBottom = 57.827f
                    val gridWidth = 532.63f
                    val gridHeight = 305.008f
                    stream.setStrokingColor(Color(148, 176, 193))
                    stream.setLineWidth(0.45f)
                    stream.addRect(gridLeft, gridBottom, gridWidth, gridHeight)
                    stream.stroke()
                    stream.setStrokingColor(Color(191, 211, 222))
                    stream.setLineWidth(0.27f)
                    for (column in 1 until 34) {
                        val x = gridLeft + column * gridWidth / 34f
                        stream.moveTo(x, gridBottom)
                        stream.lineTo(x, gridBottom + gridHeight)
                        stream.stroke()
                    }
                    for (row in 1 until 20) {
                        val y = gridBottom + row * gridHeight / 20f
                        stream.moveTo(gridLeft, y)
                        stream.lineTo(gridLeft + gridWidth, y)
                        stream.stroke()
                    }
                } finally {
                    stream.restoreGraphicsState()
                }
            }
            require(pdf.numberOfPages >= 4) { "A V11 proof must include P1/P2/P3 and final Notes." }
            require(physicalSectionKeys.size == pdf.numberOfPages) {
                "Every real PDF page must have exactly one recorded V11 role."
            }
            require(physicalSectionKeys.last() == "NOTAS-FINAL")
            val totalPages = pdf.numberOfPages
            val physicalP1 = physicalSectionKeys.indexOfFirst {
                it.startsWith("P1 - ")
            } + 1
            val physicalP2 = physicalSectionKeys.indexOfFirst {
                it == "P2 - Equipo y tesoro"
            } + 1
            require(physicalP1 > 0 && physicalP2 > 0)
            val physicalP3 = physicalSectionKeys.indexOf("P3 - Trasfondo / Perfil / Historia") + 1
            val p3ContinuationFirst = physicalSectionKeys.indexOf("P3 - Narrativa / continuación")
                .takeIf { it >= 0 }?.plus(1)
            val mixedC2Key = "C2 - Habilidades, acciones y recursos"
            val c2FromP1 = mixedC2P1Pages.minOrNull()
            fun isFromP2(key: String): Boolean =
                key.startsWith("C2 - Equipo y tesoro / ") ||
                    key == "C2 - Rasgos / continuación" ||
                    key == "C2 - Equipo especial / continuación"
            val dedicatedC2FromP2 = physicalSectionKeys.indexOfFirst { isFromP2(it) }
                .takeIf { it >= 0 }?.plus(1)
            val c2FromP2 = listOfNotNull(
                dedicatedC2FromP2, mixedC2TraitPages.minOrNull(),
            ).minOrNull()
            // C206: P2 can have MULTIPLE independent C2 descendants (traits,
            // ordinary equipment, treasure, special equipment). Its existing
            // bottom cue points only to the first. Preserve that established
            // reference and add explicit per-module destinations in the unused
            // footer band, with actual FINAL physical page numbers.
            val p2C2Destinations = buildList {
                mixedC2TraitPages.minOrNull()?.let { add("RASGOS" to it) }
                physicalSectionKeys.forEachIndexed { physicalIndex, section ->
                    val sourceModule = when {
                        section == "C2 - Rasgos / continuación" -> "RASGOS"
                        section == "C2 - Equipo y tesoro / EQUIPO Y TESORO" ->
                            "EQUIPO/TESORO"
                        section.startsWith("C2 - Equipo y tesoro / EQUIPO") -> "EQUIPO"
                        section.startsWith("C2 - Equipo y tesoro / TESORO") -> "TESORO"
                        section == "C2 - Equipo especial / continuación" -> "ESPECIAL"
                        else -> null
                    }
                    if (sourceModule != null) add(sourceModule to (physicalIndex + 1))
                }
            }.distinctBy { it.first }.sortedBy { it.second }
            require(p2C2Destinations.minOfOrNull { it.second } == c2FromP2) {
                "The P2 C2 source modules must agree with the primary backlink."
            }
            // C251: golden P2 footer names EVERY continued module with its real
            // physical page ("EQUIPO A P. 4 · TESORO A P. 5 · EQUIPO ESPECIAL
            // A P. 5"), the same grammar as the P1 footer (C246).
            // C341: «RASGOS CONTINÚA EN PÁG. 5 · EQUIPO CONTINÚA EN PÁG. 6».
            val p2ModuleLinks = PcSheetV11ContinuationText.moduleLinks(
                p2C2Destinations.map { (module, physical) ->
                    when (module) {
                        "EQUIPO/TESORO" -> "EQUIPO Y TESORO"
                        "ESPECIAL" -> "EQUIPO ESPECIAL"
                        else -> module
                    } to physical
                },
            )
            if (p2ModuleLinks != null) {
                require(headerPdfFont.getStringWidth( // C341: measured as drawn, 6.1 pt
                        pcSheetV11PrintableText(p2ModuleLinks)) * 6.1f / 1000f <= 528f) {
                    "All genuine P2 module destinations must fit the reserved footer."
                }
            }
            // C246: golden P1 footer names every continued module and its real
            // physical C2 page (golden "ATRIBUTOS Y HABILIDADES A P. 4 · ATAQUES
            // A P. 4 · MUNICIONES A P. 4"), in the same free band P2 uses.
            val p1ModuleLinks = listOf(
                "c2-heading:p1-overflow" to "PERSONAJE",
                "c2-heading:skills" to "HABILIDADES",
                "c2-heading:combat" to "ATAQUES",
                "c2-heading:resources" to "RECURSOS Y MUNICIONES",
            ).mapNotNull { (key, module) ->
                mixedC2HeadingPages[key]?.let { module to it }
            }.let { PcSheetV11ContinuationText.moduleLinks(it) } // C341
            if (p1ModuleLinks != null) {
                require(headerPdfFont.getStringWidth( // C341: measured as drawn, 6.1 pt
                        pcSheetV11PrintableText(p1ModuleLinks)) * 6.1f / 1000f <= 528f) {
                    "All genuine P1 module destinations must fit the reserved footer."
                }
            }
            // C252: golden P1/P2 module-link footer: a shaded 13pt band across
            // the page area with small Barlow ink (6.1pt), in the free strip
            // between the body (>=76) and the internal watermark (y=46).
            fun drawModuleLinks(stream: PDPageContentStream, links: String) {
                stream.saveGraphicsState()
                // C290: golden band geometry (v11_extremo p1): fill, foot rule, text.
                stream.setNonStrokingColor(Color(231, 242, 247))
                stream.addRect(39.69f, 43.94f, 532.62f, 13.04f)
                stream.fill()
                stream.setStrokingColor(Color(82, 124, 150))
                stream.setLineWidth(0.55f)
                stream.moveTo(39.69f, 45.07f)
                stream.lineTo(572.32f, 45.07f)
                stream.stroke()
                stream.setNonStrokingColor(Color(38, 75, 104))
                drawP1Label(stream, links, 42.52f, 47.34f, 6.1f)
                stream.restoreGraphicsState()
            }
            // A C2 page never points at P2 merely because it happens to be a
            // continuation: derive the correct base-page role from its module.
            for (index in 0 until totalPages - 1) {
                val key = physicalSectionKeys[index]
                val physicalPage = index + 1
                val sectionPages = physicalSectionKeys.indices
                    .filter { physicalSectionKeys[it] == key }
                    .map { it + 1 }
                val position = sectionPages.indexOf(physicalPage)
                require(position >= 0)
                val prev = sectionPages.getOrNull(position - 1)
                val next = sectionPages.getOrNull(position + 1)
                // C341 (owner 2026-10-30): «CONTINÚA EN PÁG. X» where content
                // leaves this page, «VIENE DE PÁG. Y» where it arrives.
                val link = when {
                    key.startsWith("P1 - ") && c2FromP1 != null ->
                        PcSheetV11ContinuationText.continuesOn(c2FromP1)
                    key.startsWith("P2 - ") && c2FromP2 != null ->
                        PcSheetV11ContinuationText.continuesOn(c2FromP2)
                    key == "P3 - Trasfondo / Perfil / Historia" && p3ContinuationFirst != null ->
                        PcSheetV11ContinuationText.continuesOn(p3ContinuationFirst)
                    else -> {
                        // A single mixed C2 physical page may contain P1
                        // continuations, P2 trait continuations, both or neither:
                        // announce each base page only where its FIRST genuine
                        // continuation begins, plus the previous page of the same flow.
                        val origins = when {
                            key == mixedC2Key -> listOfNotNull(
                                physicalP1.takeIf { physicalPage == c2FromP1 },
                                physicalP2.takeIf { physicalPage == mixedC2TraitPages.minOrNull() },
                            )
                            isFromP2(key) && prev == null -> listOf(physicalP2)
                            key == "P3 - Narrativa / continuación" && prev == null -> listOf(physicalP3)
                            else -> emptyList()
                        }
                        listOfNotNull(
                            PcSheetV11ContinuationText.comesFrom(origins + listOfNotNull(prev)),
                            next?.let { PcSheetV11ContinuationText.continuesOn(it) },
                        ).joinToString(" | ")
                    }
                }
                PDPageContentStream(
                    pdf, pdf.getPage(index), PDPageContentStream.AppendMode.APPEND,
                    true, true,
                ).use { stream ->
                    // C290: golden footer line — a pale rule, the approved §3.33
                    // references in 6.1 pt label ink before the page number, and the
                    // golden "01 / 11" page number (7.4 pt, right edge x569.48).
                    stream.saveGraphicsState()
                    stream.setStrokingColor(Color(148, 176, 193))
                    stream.setLineWidth(0.32f)
                    stream.moveTo(270.43f, 37.28f)
                    stream.lineTo(572.32f, 37.28f)
                    stream.stroke()
                    stream.setNonStrokingColor(Color(38, 75, 104))
                    if (link.isNotEmpty()) {
                        val linkWidth = headerPdfFont.getStringWidth(
                            pcSheetV11PrintableText(link)) * 6.1f / 1000f
                        require(539.32f - linkWidth >= 160f) {
                            "V11 footer references would cover the footer note."
                        }
                        drawP1Label(stream, link, 539.32f - linkWidth, 32.6f, 6.1f)
                    }
                    stream.restoreGraphicsState()
                    if (key == "P2 - Equipo y tesoro" &&
                        p2ModuleLinks != null
                    ) {
                        // PDF y=63: below source body (>=76), above the original
                        // watermark y=46 and navigation y=35. No page/layout
                        // mutation; never replace the first legacy cue.
                        drawModuleLinks(stream, p2ModuleLinks)
                    }
                    if (key.startsWith("P1 - ") && p1ModuleLinks != null) {
                        drawModuleLinks(stream, p1ModuleLinks)
                    }
                    if (key == "P3 - Trasfondo / Perfil / Historia" && p3ModuleLinks != null) {
                        drawModuleLinks(stream, p3ModuleLinks)
                    }
                    // C340 (owner 2026-10-30, reverses C339): the page number comes
                    // back so «CONTINÚA EN PÁG. n» / «VIENE DE PÁG. n» resolve, but only
                    // the bare number - no «/ total». Golden counter slot and ink,
                    // unpadded so it reads exactly like the references.
                    val label = physicalPage.toString()
                    stream.saveGraphicsState()
                    stream.setNonStrokingColor(Color(38, 75, 104))
                    drawP1Label(stream, label, 569.48f -
                        headerPdfFont.getStringWidth(label) * 8.2f / 1000f, 32.6f, 8.2f)
                    stream.restoreGraphicsState()
                }
            }
            pdf.save(target)
        }
    }
}
