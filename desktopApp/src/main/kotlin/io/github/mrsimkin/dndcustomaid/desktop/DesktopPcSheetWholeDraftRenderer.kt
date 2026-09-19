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
        val ordinary = inventory.filterNot { it.special }

        ordinary.take(V1_EQUIPMENT_RULE_Y.size * V1_EQUIPMENT_COLUMNS.size)
            .forEachIndexed { index, item ->
                val column = index % V1_EQUIPMENT_COLUMNS.size
                val row = index / V1_EQUIPMENT_COLUMNS.size
                val field = V1_EQUIPMENT_COLUMNS[column]
                drawOnRule(
                    stream, primitives,
                    field.first, field.second, V1_EQUIPMENT_RULE_Y[row],
                    inventoryLine(item), 7.4f,
                )
            }

        val currenciesByKey = sheet.currencies.associateBy { it.key.lowercase() }
        V1_CURRENCY_FIELDS.forEach { field ->
            currenciesByKey[field.key]?.let { currency ->
                drawTableText(
                    stream, primitives,
                    field.x, field.y, field.width, 30f,
                    currency.amount.toString(), 10f, centered = true,
                )
            }
        }

        val valuables = plan.snapshot.aggregate.successor.preferences.valuablesText
            .split(';')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map(::parseValuable)
        valuables.take(V1_VALUABLE_RULE_Y.size).forEachIndexed { index, valuable ->
            val ruleY = V1_VALUABLE_RULE_Y[index]
            drawOnRule(
                stream, primitives,
                815f, 220f, ruleY,
                valuable.label, 6.7f,
            )
            valuable.valuePo?.let { value ->
                drawTableText(
                    stream, primitives,
                    1070f, ruleY - 29f, 105f, 26f,
                    value, 7.5f, centered = true,
                )
            }
        }

        positionedSpecialItems(inventory.filter { it.special }, V1_SPECIAL_RULE_Y.size)
            .forEach { (rowIndex, item) ->
                val ruleY = V1_SPECIAL_RULE_Y[rowIndex]
                if (item.equipped || item.attuned) {
                    markerPx(
                        stream, primitives, 234.5f, ruleY - 16.5f, 14f,
                        PdfMarkerKind.CHECK, PdfSymbolFamily.V1_DERIVED,
                    )
                }
                drawOnRule(stream, primitives, 255f, 165f, ruleY, item.name, 7.2f)
                val detail = listOfNotNull(item.description, item.notes).joinToString(" - ")
                drawOnRule(stream, primitives, 430f, 605f, ruleY, detail, 6.8f)
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
            .joinToString(" · ") { trait -> trait.name + ": " + trait.description }
        val notes = notesText(plan)

        drawRuledText(
            stream, primitives, 50f, 312f, V1_BACKGROUND_RULE_Y,
            listOf(background.name, background.summary).filter { it.isNotBlank() }.joinToString(" - "),
            7.1f, 42,
        )
        drawRuledText(stream, primitives, 50f, 312f, V1_PERSONALITY_RULE_Y, background.personalityTraits, 7f, 42)
        drawRuledText(stream, primitives, 50f, 312f, V1_IDEALS_RULE_Y, background.ideals, 7f, 42)
        drawRuledText(stream, primitives, 50f, 312f, V1_BONDS_RULE_Y, background.bonds, 7f, 42)
        drawRuledText(stream, primitives, 50f, 312f, V1_FLAWS_RULE_Y, background.flaws, 7f, 42)

        drawRuledText(stream, primitives, 385f, 650f, V1_TRAITS_RULE_Y, traitText, 6.8f, 92)
        drawRuledText(stream, primitives, 385f, 650f, V1_STORY_RULE_Y, background.story, 7f, 92)
        drawRuledText(stream, primitives, 385f, 650f, V1_NARRATIVE_NOTES_RULE_Y, notes, 6.8f, 92)
    }

    private fun drawV2EquipmentAndNarrative(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        plan: PcSheetPdfRenderPlan,
    ) {
        val sheet = plan.snapshot.aggregate.sheet
        val background = sheet.background
        val inventory = sheet.inventoryItems.sortedBy { it.sortOrder }

        inventory.filterNot { it.special }
            .take(V2_EQUIPMENT_RULE_Y.size)
            .forEachIndexed { index, item ->
                drawOnRule(
                    stream, primitives,
                    25f, 490f, V2_EQUIPMENT_RULE_Y[index],
                    inventoryLine(item), 6.9f,
                )
            }

        drawRuledText(
            stream, primitives, 535f, 525f, V2_BACKGROUND_RULE_Y,
            listOf(background.name, background.summary).filter { it.isNotBlank() }.joinToString(" - "),
            7f, 76,
        )
        drawRuledText(stream, primitives, 535f, 525f, V2_BONDS_RULE_Y, background.bonds, 6.9f, 76)
        drawRuledText(stream, primitives, 535f, 525f, V2_IDEALS_RULE_Y, background.ideals, 6.9f, 76)
        drawRuledText(stream, primitives, 535f, 525f, V2_STORY_RULE_Y, background.story, 6.9f, 76)

        positionedSpecialItems(inventory.filter { it.special }, V2_SPECIAL_RULE_Y.size)
            .forEach { (rowIndex, item) ->
                val ruleY = V2_SPECIAL_RULE_Y[rowIndex]
                if (item.equipped || item.attuned) {
                    markerPx(
                        stream, primitives, 183.5f, ruleY - 15f, 14f,
                        PdfMarkerKind.CHECK, PdfSymbolFamily.V3_DERIVED,
                    )
                }
                drawOnRule(stream, primitives, 180f, 340f, ruleY, item.name, 6.9f)
                val detail = listOfNotNull(item.description, item.notes).joinToString(" - ")
                drawOnRule(stream, primitives, 535f, 525f, ruleY, detail, 6.6f)
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
                            stream, primitives, block.checkX, rowY + 10f, 13f,
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
        val isV1 = plan.request.visualFamily == PcSheetVisualFamily.CUSTOM_V1
        val rules = if (isV1) V1_NOTES_RULE_Y else V2_NOTES_RULE_Y
        val lines = wrapForRules(text, if (isV1) 68 else 72)
        val leftCount = minOf(rules.size, (lines.size + 1) / 2)
        val rightCount = minOf(rules.size, lines.size - leftCount)
        lines.take(leftCount).forEachIndexed { index, line ->
            drawOnRule(
                stream, primitives,
                if (isV1) 50f else 25f,
                if (isV1) 485f else 510f,
                rules[index], line, 7f,
            )
        }
        lines.drop(leftCount).take(rightCount).forEachIndexed { index, line ->
            drawOnRule(
                stream, primitives,
                if (isV1) 555f else 550f,
                if (isV1) 485f else 510f,
                rules[index], line, 7f,
            )
        }
        drawNotesDoodles(stream, isV1)
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

    private fun parseValuable(raw: String): ValuableEntry {
        val match = Regex("""^(.*?)\s*\((\d+)\s*po\)\s*$""", RegexOption.IGNORE_CASE).matchEntire(raw)
        return if (match != null) {
            ValuableEntry(
                label = match.groupValues[1].trim(),
                valuePo = match.groupValues[2],
            )
        } else {
            ValuableEntry(label = raw.trim(), valuePo = null)
        }
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

    private fun drawRuledText(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        xPx: Float,
        widthPx: Float,
        ruleYPx: List<Float>,
        text: String,
        sizePt: Float,
        maxChars: Int,
    ) {
        wrapForRules(text, maxChars)
            .take(ruleYPx.size)
            .forEachIndexed { index, line ->
                drawOnRule(stream, primitives, xPx, widthPx, ruleYPx[index], line, sizePt)
            }
    }

    private fun drawOnRule(
        stream: PDPageContentStream,
        primitives: DesktopPdfRenderingPrimitives,
        xPx: Float,
        widthPx: Float,
        ruleYPx: Float,
        text: String,
        sizePt: Float,
    ) {
        primitives.drawTextBox(
            stream,
            PdfTextBoxSpec(
                rect = rectPx(xPx, ruleYPx - 29f, widthPx, 26f),
                text = text,
                role = PdfTypographyRole.COMPACT_TABLE,
                preferredSizePt = sizePt,
                minimumSizePt = 5.8f,
                horizontalAlignment = PdfHorizontalAlignment.LEFT,
                verticalAlignment = PdfVerticalAlignment.BOTTOM,
                wrapPolicy = PdfWrapPolicy.SINGLE_LINE,
                maximumLines = 1,
                horizontalPaddingPt = 1f,
                verticalPaddingPt = 0.5f,
            ),
        )
    }

    private fun wrapForRules(text: String, maxChars: Int): List<String> {
        val paragraphs = text
            .replace("\r\n", "\n")
            .split(Regex("\\n+"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        val result = mutableListOf<String>()
        paragraphs.forEach { paragraph ->
            var current = ""
            paragraph.split(Regex("\\s+")).forEach { word ->
                val candidate = if (current.isEmpty()) word else current + " " + word
                if (candidate.length <= maxChars || current.isEmpty()) {
                    current = candidate
                } else {
                    result += current
                    current = word
                }
            }
            if (current.isNotEmpty()) result += current
        }
        return result
    }

    private fun drawNotesDoodles(
        stream: PDPageContentStream,
        isV1: Boolean,
    ) {
        val scale = PX_TO_PT
        val firstCenterX = 265f * scale
        val firstCenterY = PAGE_HEIGHT_PT - 1060f * scale
        val secondX = 720f * scale
        val secondY = PAGE_HEIGHT_PT - 1115f * scale

        stream.saveGraphicsState()
        stream.setStrokingColor(java.awt.Color.BLACK)
        stream.setLineWidth(0.8f)

        val radius = 42f * scale
        stream.addRect(firstCenterX - radius, firstCenterY - radius, radius * 2f, radius * 2f)
        stream.stroke()
        stream.moveTo(firstCenterX, firstCenterY + radius)
        stream.lineTo(firstCenterX + radius * 0.55f, firstCenterY - radius)
        stream.lineTo(firstCenterX - radius * 0.55f, firstCenterY - radius)
        stream.closePath()
        stream.stroke()

        val w = 170f * scale
        val h = 95f * scale
        stream.addRect(secondX, secondY, w, h)
        stream.stroke()
        stream.moveTo(secondX + 45f * scale, secondY)
        stream.lineTo(secondX + 45f * scale, secondY + h)
        stream.moveTo(secondX + 105f * scale, secondY + 35f * scale)
        stream.lineTo(secondX + w, secondY + 35f * scale)
        stream.stroke()

        if (!isV1) {
            stream.moveTo(secondX + w, secondY + h)
            stream.lineTo(secondX + w + 60f * scale, secondY + h + 40f * scale)
            stream.stroke()
        }
        stream.restoreGraphicsState()
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

    private data class ValuableEntry(
        val label: String,
        val valuePo: String?,
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

        val V1_EQUIPMENT_COLUMNS = listOf(55f to 220f, 305f to 220f, 555f to 225f)
        val V1_EQUIPMENT_RULE_Y = listOf(
            217f, 257f, 297f, 336f, 376f, 416f, 456f, 495f, 535f,
            575f, 614f, 654f, 694f, 733f, 773f, 813f, 852f, 892f,
        )
        val V1_VALUABLE_RULE_Y = listOf(614f, 654f, 694f, 733f, 773f, 813f, 852f, 892f)
        val V1_SPECIAL_RULE_Y = listOf(
            1045f, 1085f, 1124f, 1164f, 1204f, 1244f, 1283f,
            1323f, 1363f, 1402f, 1442f, 1482f, 1527f,
        )

        val V1_BACKGROUND_RULE_Y = listOf(219f, 259f, 299f, 338f, 378f, 418f)
        val V1_PERSONALITY_RULE_Y = listOf(497f, 537f, 575f, 614f, 654f, 694f)
        val V1_IDEALS_RULE_Y = listOf(775f, 815f, 852f, 892f, 932f, 971f)
        val V1_BONDS_RULE_Y = listOf(1053f, 1092f, 1130f, 1170f, 1210f, 1249f)
        val V1_FLAWS_RULE_Y = listOf(1331f, 1370f, 1408f, 1450f, 1487f, 1529f)
        val V1_TRAITS_RULE_Y = listOf(219f, 259f, 299f, 338f, 378f, 418f, 458f, 497f, 537f, 577f, 616f, 656f)
        val V1_STORY_RULE_Y = listOf(775f, 815f, 854f, 894f, 934f, 973f, 1013f, 1053f, 1092f)
        val V1_NARRATIVE_NOTES_RULE_Y = listOf(1212f, 1251f, 1291f, 1331f, 1370f, 1410f, 1450f, 1489f, 1529f)

        val V2_EQUIPMENT_RULE_Y = listOf(
            229f, 263f, 297f, 331f, 365f, 399f, 433f, 467f, 501f, 535f, 569f, 603f,
            637f, 671f, 705f, 739f, 773f, 807f, 841f, 875f, 909f, 943f, 977f,
        )
        val V2_BACKGROUND_RULE_Y = listOf(229f, 263f, 297f)
        val V2_BONDS_RULE_Y = listOf(365f, 399f, 433f)
        val V2_IDEALS_RULE_Y = listOf(501f, 535f, 569f)
        val V2_STORY_RULE_Y = listOf(603f, 637f, 671f, 705f, 739f, 773f, 807f, 841f, 875f, 909f, 943f, 977f)
        val V2_SPECIAL_RULE_Y = listOf(
            1085f, 1119f, 1153f, 1187f, 1221f, 1255f, 1289f,
            1323f, 1357f, 1391f, 1425f, 1459f, 1493f, 1527f,
        )

        val V1_NOTES_RULE_Y = listOf(
            219f, 259f, 299f, 338f, 378f, 418f, 458f, 497f, 537f,
            577f, 616f, 656f, 696f, 735f, 775f, 815f, 854f,
        )
        val V2_NOTES_RULE_Y = listOf(
            208f, 242f, 276f, 310f, 344f, 378f, 412f, 446f, 480f, 514f,
            548f, 582f, 616f, 650f, 684f, 718f, 755f, 789f, 823f, 857f,
        )

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
