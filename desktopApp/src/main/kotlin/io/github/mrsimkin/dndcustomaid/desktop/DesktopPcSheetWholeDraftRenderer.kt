package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBaseLayoutMode
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBasePageRole
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetVisualFamily
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode

/**
 * First whole-sheet development draft for the owner's Custom v1/v2 families.
 *
 * The MAIN page deliberately reuses the existing populated template proof while the other source
 * pages are populated through the owner-approved measured rendering primitives. This is a review
 * artifact, not a visually approved export implementation. Classic, Extended-page pagination,
 * portraits and Spellbook description pages remain outside this first-draft slice.
 */
internal class DesktopPcSheetWholeDraftRenderer(
    private val resourceLoader: (String) -> InputStream? = { resourcePath ->
        DesktopPcSheetWholeDraftRenderer::class.java.classLoader.getResourceAsStream(resourcePath)
    },
) {
    fun renderDraft(
        plan: PcSheetPdfRenderPlan,
        output: OutputStream,
    ) {
        require(plan.baseLayoutMode == PcSheetBaseLayoutMode.FAITHFUL) {
            "Whole-sheet Custom draft currently supports faithful template layouts only."
        }
        require(
            plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V1 ||
                plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V2_PER_ATTRIBUTE ||
                plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V2_PER_ABILITY,
        ) {
            "Whole-sheet draft currently supports the owner's Custom v1/v2 families only."
        }

        val templatePath = requireNotNull(plan.basePages.first().sourceTemplatePath)
        require(plan.basePages.all { it.sourceTemplatePath == templatePath }) {
            "A Custom draft must resolve all base pages from one authoritative source template."
        }
        val classpathPath = templatePath.removePrefix("assets/")
        val templateBytes = resourceLoader(classpathPath)?.use { it.readBytes() }
            ?: error("PC sheet template resource is unavailable: $classpathPath")

        val mainProofBytes = ByteArrayOutputStream().use { buffer ->
            DesktopPcSheetTemplateProofRenderer(resourceLoader).renderMainPage(plan, buffer)
            buffer.toByteArray()
        }

        Loader.loadPDF(mainProofBytes).use { mainProof ->
            Loader.loadPDF(templateBytes).use { sourceTemplate ->
                PDDocument().use { draft ->
                    draft.importPage(mainProof.getPage(0))
                    val primitives = DesktopPdfRenderingPrimitives(DesktopPdfFontRegistry(draft))

                    plan.basePages.drop(1).forEach { pagePlan ->
                        val sourcePageNumber = requireNotNull(pagePlan.sourcePageNumber)
                        val sourceIndex = sourcePageNumber - 1
                        require(sourceIndex in 0 until sourceTemplate.numberOfPages) {
                            "Template page $sourcePageNumber does not exist in $templatePath."
                        }
                        val page = draft.importPage(sourceTemplate.getPage(sourceIndex))
                        PDPageContentStream(draft, page, AppendMode.APPEND, true, true).use { stream ->
                            drawBasePage(
                                stream = stream,
                                primitives = primitives,
                                role = pagePlan.role,
                                plan = plan,
                            )
                        }
                    }
                    draft.save(output)
                }
            }
        }
    }

    private fun drawBasePage(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        role: PcSheetBasePageRole,
        plan: PcSheetPdfRenderPlan,
    ) {
        when (role) {
            PcSheetBasePageRole.EQUIPMENT -> drawV1Equipment(stream, primitives, plan)
            PcSheetBasePageRole.NARRATIVE -> drawV1Narrative(stream, primitives, plan)
            PcSheetBasePageRole.EQUIPMENT_AND_NARRATIVE -> drawV2EquipmentAndNarrative(stream, primitives, plan)
            PcSheetBasePageRole.SPELL_LIST -> drawSpellList(stream, primitives, plan)
            PcSheetBasePageRole.NOTES -> drawNotes(stream, primitives, plan)
            PcSheetBasePageRole.MAIN -> error("MAIN is imported from the populated proof before draft pages.")
        }
    }

    private fun drawV1Equipment(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        val inventory = sheet.inventoryItems.sortedBy { it.sortOrder }

        inventory.take(9).forEachIndexed { index, item ->
            val y = 215f + index * 70f
            drawTableText(stream, primitives, 55f, y, 725f, 30f, inventoryLine(item), 7.2f)
        }

        val currenciesByKey = sheet.currencies.associateBy { it.key.lowercase() }
        V1_CURRENCY_FIELDS.forEach { field ->
            currenciesByKey[field.key]?.let { currency ->
                drawTableText(
                    stream, primitives,
                    field.x, field.y, field.width, 28f,
                    currency.amount.toString(), 8f, centered = true,
                )
            }
        }

        val valuables = plan.snapshot.aggregate.successor.preferences.valuablesText
            .split(';')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        valuables.take(V1_VALUABLE_ROW_Y.size).forEachIndexed { index, valuable ->
            drawBlock(
                stream, primitives,
                910f, V1_VALUABLE_ROW_Y[index], 175f, 36f,
                valuable, 6.2f, 2,
            )
        }

        positionedSpecialItems(inventory.filter { it.special }, V1_SPECIAL_ROW_Y.size)
            .forEach { (rowIndex, item) ->
                val y = V1_SPECIAL_ROW_Y[rowIndex]
                if (item.equipped || item.attuned) {
                    markerPx(
                        stream, primitives, 234.5f, y + 13.5f, 10f,
                        PdfMarkerKind.CHECK, PdfSymbolFamily.V1_DERIVED,
                    )
                }
                drawTableText(stream, primitives, 255f, y, 205f, 30f, item.name, 6.7f)
                val detail = listOfNotNull(item.description, item.notes).joinToString(" - ")
                drawTableText(stream, primitives, 485f, y, 550f, 30f, detail, 6.5f)
            }
    }

    private fun drawV1Narrative(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        val background = sheet.background
        val traitText = sheet.traits
            .sortedBy { it.sortOrder }
            .joinToString("\n") { trait -> "${trait.name}: ${trait.description}" }
        val notes = notesText(plan)

        drawBlock(
            stream, primitives, 50f, 210f, 315f, 120f,
            listOf(background.name, background.summary).filter { it.isNotBlank() }.joinToString(" - "),
            7.4f, 4,
        )
        drawBlock(stream, primitives, 50f, 465f, 315f, 205f, background.personalityTraits, 7.2f, 7)
        drawBlock(stream, primitives, 50f, 745f, 315f, 190f, background.ideals, 7.2f, 6)
        drawBlock(stream, primitives, 50f, 1020f, 315f, 190f, background.bonds, 7.2f, 6)
        drawBlock(stream, primitives, 50f, 1300f, 315f, 190f, background.flaws, 7.2f, 6)
        drawBlock(stream, primitives, 390f, 215f, 650f, 445f, traitText, 6.8f, 14)
        drawBlock(stream, primitives, 390f, 755f, 650f, 335f, background.story, 7f, 11)
        drawBlock(stream, primitives, 390f, 1190f, 650f, 300f, notes, 6.8f, 10)
    }

    private fun drawV2EquipmentAndNarrative(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        val background = sheet.background
        val inventory = sheet.inventoryItems.sortedBy { it.sortOrder }

        inventory.take(20).forEachIndexed { index, item ->
            val y = 195f + index * 34f
            drawTableText(stream, primitives, 25f, y, 485f, 28f, inventoryLine(item), 6.4f)
        }

        drawBlock(
            stream, primitives, 535f, 205f, 525f, 70f,
            listOf(background.name, background.summary).filter { it.isNotBlank() }.joinToString(" - "),
            7f, 3,
        )
        drawBlock(stream, primitives, 535f, 335f, 525f, 85f, background.bonds, 6.8f, 3)
        drawBlock(stream, primitives, 535f, 480f, 525f, 80f, background.ideals, 6.8f, 3)
        drawBlock(stream, primitives, 535f, 620f, 525f, 335f, background.story, 6.8f, 12)

        positionedSpecialItems(inventory.filter { it.special }, V2_SPECIAL_ROW_Y.size)
            .forEach { (rowIndex, item) ->
                val y = V2_SPECIAL_ROW_Y[rowIndex]
                if (item.equipped || item.attuned) {
                    markerPx(
                        stream, primitives, 183.5f, y + 15f, 10f,
                        PdfMarkerKind.CHECK, PdfSymbolFamily.V3_DERIVED,
                    )
                }
                drawTableText(stream, primitives, 180f, y, 390f, 28f, item.name, 6.4f)
                val detail = listOfNotNull(item.description, item.notes).joinToString(" - ")
                drawTableText(stream, primitives, 600f, y, 570f, 28f, detail, 6.2f)
            }
    }

    private fun drawSpellList(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        val slots = sheet.spellSlots.associateBy { it.level }
        val spellsByLevel = sheet.spells.groupBy { it.level }

        spellBlocks(plan).forEach { block ->
            val slot = slots[block.level]
            if (block.level > 0 && slot != null) {
                drawTableText(
                    stream, primitives,
                    block.headerTotalX, block.headerY, 52f, 28f,
                    slot.totalSlots.toString(), 7.5f, centered = true,
                )
                repeat(slot.spentSlots.coerceAtMost(4)) { index ->
                    markerPx(
                        stream,
                        primitives,
                        block.headerSpentX + index * 24f,
                        block.headerY + 14f,
                        13f,
                        PdfMarkerKind.CIRCLE_FILLED,
                        symbolFamily(plan),
                    )
                }
            }

            spellsByLevel[block.level].orEmpty()
                .sortedWith(compareBy<CharacterSpell> { it.sortOrder }.thenBy { it.name.lowercase() })
                .take(block.maxRows)
                .forEachIndexed { index, spell ->
                    val rowY = block.firstRowY + index * block.rowStep
                    if (spell.sourceAssociations.any { it.prepared }) {
                        markerPx(
                            stream, primitives, block.checkX, rowY + 10f, 10f,
                            PdfMarkerKind.CHECK, symbolFamily(plan),
                        )
                    }
                    drawTableText(
                        stream, primitives,
                        block.textX, rowY, block.textWidth, 24f,
                        spell.name, 6.5f,
                    )
                }
        }
    }

    private fun drawNotes(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val text = notesText(plan)
        if (text.isBlank()) return
        val midpoint = splitNearMiddle(text)
        drawBlock(stream, primitives, 50f, 200f, 510f, 760f, midpoint.first, 7f, 25)
        drawBlock(stream, primitives, 610f, 200f, 510f, 760f, midpoint.second, 7f, 25)
    }

    private fun notesText(plan: PcSheetPdfRenderPlan): String {
        val sheet = plan.snapshot.aggregate.sheet
        return buildList {
            sheet.generalNotes.trim().takeIf { it.isNotEmpty() }?.let(::add)
            sheet.noteCards.sortedBy { it.sortOrder }.forEach { card ->
                val body = card.content.trim()
                if (body.isNotEmpty()) add("${card.title}: $body")
            }
        }.joinToString("\n\n")
    }

    private fun inventoryLine(item: CharacterInventoryItem): String = buildString {
        if (item.quantity > 1) append(item.quantity).append(" x ")
        append(item.name)
        item.location?.takeIf { it.isNotBlank() }?.let { append(" - ").append(it) }
        item.weightLb?.let { append(" - ").append(it).append(" lb") }
    }

    private fun positionedSpecialItems(
        items: List<CharacterInventoryItem>,
        rowCount: Int,
    ): List<Pair<Int, CharacterInventoryItem>> {
        val available = (0 until rowCount).toMutableSet()
        val positioned = mutableListOf<Pair<Int, CharacterInventoryItem>>()
        items.take(rowCount).forEach { item ->
            val preferred = specialLocationRow(item.location)
                ?.takeIf { it in available }
            val fallback = available
                .filter { it >= SPECIAL_LOCATION_LABELS.size }
                .minOrNull()
                ?: available.minOrNull()
            val row = preferred ?: fallback ?: return@forEach
            available.remove(row)
            positioned += row to item
        }
        return positioned.sortedBy { it.first }
    }

    private fun specialLocationRow(location: String?): Int? {
        val normalized = location
            ?.lowercase()
            ?.replace('á', 'a')
            ?.replace('é', 'e')
            ?.replace('í', 'i')
            ?.replace('ó', 'o')
            ?.replace('ú', 'u')
            ?.replace(Regex("\\s+"), " ")
            ?.trim()
            .orEmpty()
        return SPECIAL_LOCATION_LABELS.indexOf(normalized).takeIf { it >= 0 }
    }

    private fun splitNearMiddle(text: String): Pair<String, String> {
        val normalized = text.trim()
        if (normalized.length < 300) return normalized to ""
        val pivot = normalized.length / 2
        val split = normalized.indexOf("\n\n", startIndex = pivot)
            .takeIf { it >= 0 }
            ?: normalized.indexOf('\n', startIndex = pivot).takeIf { it >= 0 }
            ?: pivot
        return normalized.substring(0, split).trim() to normalized.substring(split).trim()
    }

    private fun drawTableText(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        xPx: Float,
        topYPx: Float,
        widthPx: Float,
        heightPx: Float,
        text: String,
        sizePt: Float,
        centered: Boolean = false,
    ) {
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = rectPx(xPx, topYPx, widthPx, heightPx),
                text = text,
                role = PdfTypographyRole.COMPACT_TABLE,
                preferredSizePt = sizePt,
                minimumSizePt = sizePt,
                horizontalAlignment = if (centered) PdfHorizontalAlignment.CENTER else PdfHorizontalAlignment.LEFT,
                verticalAlignment = PdfVerticalAlignment.CENTER,
                wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                maximumLines = 1,
                fontSizeMode = PdfFontSizeMode.FIXED,
                horizontalPaddingPt = 1f,
                verticalPaddingPt = 0f,
            ),
        )
    }

    private fun drawBlock(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        xPx: Float,
        topYPx: Float,
        widthPx: Float,
        heightPx: Float,
        text: String,
        preferredSizePt: Float,
        maxLines: Int,
    ) {
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = rectPx(xPx, topYPx, widthPx, heightPx),
                text = text,
                role = PdfTypographyRole.NOTE_TEXT,
                preferredSizePt = preferredSizePt,
                minimumSizePt = 6f,
                horizontalAlignment = PdfHorizontalAlignment.LEFT,
                verticalAlignment = PdfVerticalAlignment.TOP,
                wrapPolicy = PdfWrapPolicy.WORD_WRAP,
                maximumLines = maxLines,
                horizontalPaddingPt = 2f,
                verticalPaddingPt = 1f,
            ),
        )
    }

    private fun markerPx(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        centerXPx: Float,
        centerYPx: Float,
        sizePx: Float,
        kind: PdfMarkerKind,
        family: PdfSymbolFamily,
    ) {
        primitives.drawMarker(
            stream = stream,
            centerX = centerXPx * PX_TO_PT,
            centerY = PAGE_HEIGHT_PT - centerYPx * PX_TO_PT,
            sizePt = sizePx * PX_TO_PT,
            kind = kind,
            lineWidthPt = 0.8f,
            family = family,
        )
    }

    private fun symbolFamily(plan: PcSheetPdfRenderPlan): PdfSymbolFamily =
        if (plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V1) {
            PdfSymbolFamily.V1_DERIVED
        } else {
            PdfSymbolFamily.V3_DERIVED
        }

    private fun spellBlocks(plan: PcSheetPdfRenderPlan): List<SpellBlock> =
        if (plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V1) {
            V1_SPELL_BLOCKS
        } else {
            V2_SPELL_BLOCKS
        }

    private fun rectPx(
        xPx: Float,
        topYPx: Float,
        widthPx: Float,
        heightPx: Float,
    ): PdfRect = PdfRect(
        x = xPx * PX_TO_PT,
        y = PAGE_HEIGHT_PT - (topYPx + heightPx) * PX_TO_PT,
        width = widthPx * PX_TO_PT,
        height = heightPx * PX_TO_PT,
    )

    private data class CurrencyField(
        val key: String,
        val x: Float,
        val y: Float,
        val width: Float,
    )

    private data class SpellBlock(
        val level: Int,
        val headerTotalX: Float,
        val headerSpentX: Float,
        val headerY: Float,
        val checkX: Float,
        val textX: Float,
        val textWidth: Float,
        val firstRowY: Float,
        val rowStep: Float,
        val maxRows: Int,
    )

    private companion object {
        const val PAGE_HEIGHT_PT = 792f
        const val PX_TO_PT = 0.5f

        val V1_CURRENCY_FIELDS = listOf(
            CurrencyField("pt", 1050f, 188f, 130f),
            CurrencyField("po", 1050f, 228f, 130f),
            CurrencyField("pp", 1050f, 268f, 130f),
            CurrencyField("pc", 1050f, 308f, 130f),
            CurrencyField("pe", 1050f, 348f, 130f),
        )

        val V1_VALUABLE_ROW_Y = listOf(615f, 655f, 695f, 735f, 775f, 815f, 855f)
        val V1_SPECIAL_ROW_Y = List(13) { index -> 1015f + index * 40f }
        val V2_SPECIAL_ROW_Y = List(13) { index -> 1055f + index * 34f }

        val SPECIAL_LOCATION_LABELS = listOf(
            "cabeza",
            "rostro",
            "cuello",
            "mano izquierda",
            "mano derecha",
            "brazo izquierdo",
            "brazo derecho",
            "pecho",
            "piernas",
            "pies",
        )

        val V1_SPELL_BLOCKS = listOf(
            SpellBlock(0, 0f, 0f, 0f, 55f, 72f, 285f, 225f, 40f, 8),
            SpellBlock(1, 115f, 194f, 596.5f, 64.5f, 77f, 330f, 655.5f, 40f, 10),
            SpellBlock(2, 115f, 194f, 1089.5f, 64.5f, 77f, 330f, 1148.5f, 40f, 9),
            SpellBlock(3, 489f, 568f, 177f, 439f, 451f, 330f, 235.5f, 40f, 10),
            SpellBlock(4, 489f, 568f, 655.5f, 439f, 451f, 330f, 713.5f, 40f, 10),
            SpellBlock(5, 489f, 568f, 1127.5f, 439f, 451f, 330f, 1186.5f, 40f, 8),
            SpellBlock(6, 875f, 953f, 177f, 824.5f, 837f, 330f, 235.5f, 40f, 8),
            SpellBlock(7, 875f, 953f, 574f, 824.5f, 837f, 330f, 632.5f, 40f, 6),
            SpellBlock(8, 875f, 953f, 931f, 824.5f, 837f, 330f, 989.5f, 40f, 6),
            SpellBlock(9, 875f, 953f, 1248.5f, 824.5f, 837f, 330f, 1307.5f, 40f, 5),
        )

        val V2_SPELL_BLOCKS = listOf(
            SpellBlock(0, 0f, 0f, 0f, 55f, 72f, 285f, 225f, 34f, 8),
            SpellBlock(1, 86.5f, 165f, 642f, 36.5f, 49f, 358f, 691.5f, 34f, 10),
            SpellBlock(2, 86.5f, 165f, 1106.5f, 36.5f, 49f, 358f, 1156.5f, 34f, 9),
            SpellBlock(3, 478f, 557f, 177f, 427.5f, 440f, 358f, 226.5f, 34f, 10),
            SpellBlock(4, 478f, 557f, 642f, 427.5f, 440f, 358f, 691.5f, 34f, 10),
            SpellBlock(5, 478f, 557f, 1106.5f, 427.5f, 440f, 358f, 1156.5f, 34f, 8),
            SpellBlock(6, 869f, 948f, 177f, 818.5f, 831f, 358f, 226.5f, 34f, 8),
            SpellBlock(7, 869f, 948f, 545.5f, 818.5f, 831f, 358f, 594.5f, 34f, 6),
            SpellBlock(8, 869f, 948f, 908.5f, 818.5f, 831f, 358f, 957.5f, 34f, 6),
            SpellBlock(9, 869f, 948f, 1242.5f, 818.5f, 831f, 358f, 1292.5f, 34f, 5),
        )
    }
}
