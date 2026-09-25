package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetBasePageRole
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPdfRenderPlan
import java.awt.Color
import java.awt.geom.AffineTransform
import java.io.InputStream
import kotlin.math.abs
import org.apache.pdfbox.multipdf.LayerUtility
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDFormContentStream
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDResources
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject
import org.apache.pdfbox.util.Matrix

/**
 * Production renderer for the shared owner-approved Custom-v2 base pages.
 *
 * Geometry and typography are promoted from the frozen v2 Run-4/Run-2 source-led proofs rather
 * than the generic PDF primitive QA layout. Labels/structure remain in the owner source template;
 * this renderer only adds real character values in the measured writing areas.
 */
internal class DesktopCustomV2SharedBaseRenderer(
    private val document: PDDocument,
    private val resourceLoader: (String) -> InputStream?,
) {
    private val fonts = Fonts(document, resourceLoader)
    private val layers = LayerUtility(document)

    fun render(page: PDPage, role: PcSheetBasePageRole, plan: PcSheetPdfRenderPlan) {
        when (role) {
            PcSheetBasePageRole.EQUIPMENT_AND_NARRATIVE -> renderEquipmentAndNarrative(page, plan)
            PcSheetBasePageRole.SPELL_LIST -> renderSpells(page, plan)
            PcSheetBasePageRole.NOTES -> renderNotes(page, plan)
            else -> Unit
        }
    }

    private fun renderEquipmentAndNarrative(page: PDPage, plan: PcSheetPdfRenderPlan) {
        append(page, "CustomV2 BASE - Equipment") { drawEquipment(it, plan) }
        append(page, "CustomV2 BASE - Background") { drawBackground(it, plan) }
        append(page, "CustomV2 BASE - Special Equipment") { drawSpecialEquipment(it, plan) }
    }

    private fun renderSpells(page: PDPage, plan: PcSheetPdfRenderPlan) {
        SPELL_BLOCKS.forEach { block ->
            append(page, "CustomV2 BASE - Spells level ${block.level}") { drawSpellBlock(it, plan, block) }
        }
    }

    private fun renderNotes(page: PDPage, plan: PcSheetPdfRenderPlan) {
        append(page, "CustomV2 BASE - Notes") { s ->
            val text = notesText(plan)
            if (text.isBlank()) return@append
            val leftWidth = NOTES_LEFT.first().endX - NOTES_LEFT.first().startX - 3f
            val rightWidth = NOTES_RIGHT.first().endX - NOTES_RIGHT.first().startX - 3f
            val words = text.trim().split(Regex("\\s+"))
            val leftLines = wrapWords(fonts.regular, words, 9.25f, leftWidth)
            val left = leftLines.take(NOTES_LEFT.size)
            left.forEachIndexed { index, line ->
                textAboveRule(s, fonts.regular, NOTES_LEFT[index], line, 9.25f, 8.5f, 2.8f, 2f)
            }
            val consumedWords = left.sumOf { it.split(Regex("\\s+")).size }
            val rightLines = wrapWords(fonts.regular, words.drop(consumedWords), 9.25f, rightWidth)
            rightLines.take(NOTES_RIGHT.size).forEachIndexed { index, line ->
                textAboveRule(s, fonts.regular, NOTES_RIGHT[index], line, 9.25f, 8.5f, 2.8f, 2f)
            }
        }
    }

    private fun drawEquipment(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val ordinary = plan.snapshot.aggregate.sheet.inventoryItems
            .sortedBy { it.sortOrder }
            .filterNot { it.special }
            .take(EQUIPMENT_RULES.size * 2)

        EQUIPMENT_RULES.forEachIndexed { row, y ->
            ordinary.getOrNull(row * 2)?.let { item ->
                textAboveRule(s, fonts.condensed, Rule(14f, 149.5f, y), inventoryLabel(item), 9.25f, 7.0f, 2.5f, 2f)
            }
            ordinary.getOrNull(row * 2 + 1)?.let { item ->
                textAboveRule(s, fonts.condensed, Rule(156f, 291.5f, y), inventoryLabel(item), 9.25f, 7.0f, 2.5f, 2f)
            }
        }
    }

    private fun drawBackground(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val background = plan.snapshot.aggregate.sheet.background
        val backgroundText = listOf(background.name, background.summary)
            .filter { it.isNotBlank() }
            .joinToString(" - ")
        drawRuledParagraph(s, BACKGROUND_RULES, backgroundText, 9.25f, 2.6f, 2f)
        drawRuledParagraph(s, BONDS_RULES, background.bonds, 9.25f, 2.6f, 2f)
        drawRuledParagraph(s, IDEALS_RULES, background.ideals, 9.25f, 2.6f, 2f)
        drawRuledParagraph(s, STORY_RULES, background.story, 9.25f, 2.8f, 2f)
    }

    private fun drawSpecialEquipment(s: PDFormContentStream, plan: PcSheetPdfRenderPlan) {
        val special = plan.snapshot.aggregate.sheet.inventoryItems
            .sortedBy { it.sortOrder }
            .filter { it.special }

        positionedSpecialItems(special, SPECIAL_RULE_Y.size).forEach { (rowIndex, item) ->
            val y = SPECIAL_RULE_Y[rowIndex]
            if (item.equipped || item.attuned) {
                glyphInRect(
                    s,
                    fonts.symbol,
                    CHECK_CP,
                    TopRect(87.5f, SPECIAL_CHECK_TOP[rowIndex], 8.5f, 9f),
                    0.5f,
                    0.5f,
                    opticalX = 1.75f,
                    opticalY = -0.7f,
                )
            }
            // The owner template contains decorative location words in this column. Once a
            // row is populated those words are not data; clear the value cell and render the
            // character's actual location with the same application fill typography used elsewhere.
            clearLocationValueCell(s, y)
            item.location?.trim()?.takeIf { it.isNotEmpty() }?.let { location ->
                textAboveRule(s, fonts.regular, Rule(14f, 94f, y), location, 8.5f, 7.5f, 2.5f, 1f)
            }
            textAboveRule(s, fonts.regular, Rule(99f, 297f, y), item.name, 9.25f, 8.5f, 2.5f, 2f)
            val detail = buildList {
                if (item.attuned) add("Sintonizado")
                item.description?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
                item.notes?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            }.joinToString(" · ")
            textAboveRule(s, fonts.regular, Rule(303f, 596f, y), detail, 9.25f, 8.5f, 2.5f, 2f)
        }
    }

    private fun clearLocationValueCell(s: PDFormContentStream, ruleTop: Float) {
        val ruleBottom = H - ruleTop
        s.saveGraphicsState()
        s.setNonStrokingColor(Color.WHITE)
        // Leave the table's vertical borders intact while covering the decorative source value.
        s.addRect(15f, ruleBottom + 0.7f, 78f, 14.8f)
        s.fill()
        s.setStrokingColor(Color.BLACK)
        s.setLineWidth(0.45f)
        s.moveTo(14f, ruleBottom)
        s.lineTo(94f, ruleBottom)
        s.stroke()
        s.restoreGraphicsState()
    }

    private fun drawSpellBlock(s: PDFormContentStream, plan: PcSheetPdfRenderPlan, block: SpellBlock) {
        if (block.level > 0) {
            plan.snapshot.aggregate.sheet.spellSlots.firstOrNull { it.level == block.level }?.let { slot ->
                block.totalRect?.let { centered(s, fonts.semibold, it, slot.totalSlots.toString(), 14.5f, -0.3f) }
            }
        }
        val spells = plan.snapshot.aggregate.sheet.spells
            .filter { it.level == block.level }
            .sortedWith(compareBy({ it.sortOrder }, { it.name.lowercase() }))
            .take(block.maxRows)
        spells.forEachIndexed { index, spell ->
            val ruleY = block.firstRuleY + index * block.rowStep
            if (block.level > 0 && spell.sourceAssociations.any { it.prepared } && block.checkX != null && block.firstCheckTop != null) {
                glyphInRect(
                    s,
                    fonts.symbol,
                    CHECK_CP,
                    TopRect(block.checkX, block.firstCheckTop + index * block.rowStep, 8.5f, 8.5f),
                    0.5f,
                    0.5f,
                    opticalX = 1.7f,
                    opticalY = -0.6f,
                )
            }
            textAboveRule(
                s,
                fonts.regular,
                Rule(block.textStartX, block.textEndX, ruleY),
                spell.name,
                9.25f,
                8.75f,
                3f,
                2f,
            )
        }
    }

    private fun append(page: PDPage, name: String, draw: (PDFormContentStream) -> Unit) {
        val form = PDFormXObject(document).apply {
            resources = PDResources()
            setBBox(PDRectangle(page.cropBox.width, page.cropBox.height))
        }
        PDFormContentStream(form).use { stream ->
            stream.setNonStrokingColor(Color.BLACK)
            draw(stream)
        }
        layers.appendFormAsLayer(page, form, AffineTransform(), name)
    }

    private fun drawRuledParagraph(
        s: PDFormContentStream,
        rules: List<Rule>,
        text: String,
        size: Float,
        clearance: Float,
        leftPadding: Float,
    ) {
        if (text.isBlank() || rules.isEmpty()) return
        val width = rules.first().endX - rules.first().startX - leftPadding - 1f
        wrapWords(fonts.regular, text.trim().split(Regex("\\s+")), size, width)
            .take(rules.size)
            .forEachIndexed { index, line ->
                textAboveRule(s, fonts.regular, rules[index], line, size, size - 0.75f, clearance, leftPadding)
            }
    }

    private fun textAboveRule(
        s: PDFormContentStream,
        font: PDFont,
        rule: Rule,
        text: String,
        preferredSize: Float,
        minimumSize: Float,
        clearance: Float,
        leftPadding: Float = 0f,
    ) {
        if (text.isBlank()) return
        val available = rule.endX - rule.startX - leftPadding - 1f
        var size = preferredSize
        while (size > minimumSize && textWidth(font, text, size) > available) size -= 0.25f
        if (textWidth(font, text, size) > available + 0.05f) return
        val descent = requireNotNull(font.fontDescriptor).descent / 1000f * size
        val baseline = H - rule.topY + clearance - descent
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rule.startX + leftPadding, baseline)
        s.showText(text)
        s.endText()
    }

    private fun centered(s: PDFormContentStream, font: PDFont, rect: TopRect, text: String, size: Float, opticalY: Float) {
        if (text.isBlank()) return
        val width = textWidth(font, text, size)
        val ascent = (font.fontDescriptor?.ascent?.takeIf { it > 0 } ?: 750f) / 1000f * size
        val descent = abs(font.fontDescriptor?.descent?.takeIf { it < 0 } ?: -250f) / 1000f * size
        val bottom = H - (rect.top + rect.height)
        val baseline = bottom + (rect.height - ascent - descent) / 2f + descent + opticalY
        s.beginText()
        s.setFont(font, size)
        s.newLineAtOffset(rect.x + (rect.width - width) / 2f, baseline)
        s.showText(text)
        s.endText()
    }

    private fun glyphInRect(
        s: PDFormContentStream,
        font: PDFont,
        codePoint: Int,
        rect: TopRect,
        insetX: Float,
        insetY: Float,
        opticalX: Float = 0f,
        opticalY: Float = 0f,
    ) {
        val glyph = String(Character.toChars(codePoint))
        val normalizedWidth = font.getStringWidth(glyph) / 1000f
        val descriptor = requireNotNull(font.fontDescriptor)
        val normalizedAscent = descriptor.ascent / 1000f
        val normalizedDescent = descriptor.descent / 1000f
        val normalizedHeight = normalizedAscent - normalizedDescent
        if (normalizedWidth <= 0f || normalizedHeight <= 0f) return
        val scaleX = (rect.width - insetX * 2f) / normalizedWidth
        val scaleY = (rect.height - insetY * 2f) / normalizedHeight
        val left = rect.x + insetX + opticalX
        val targetBottom = H - (rect.top + rect.height) + insetY + opticalY
        val baseline = targetBottom - normalizedDescent * scaleY
        s.beginText()
        s.setFont(font, 1f)
        s.setTextMatrix(Matrix(scaleX, 0f, 0f, scaleY, left, baseline))
        s.showText(glyph)
        s.endText()
    }

    private fun wrapWords(font: PDFont, words: List<String>, size: Float, maxWidth: Float): List<String> {
        val out = mutableListOf<String>()
        var current = ""
        words.filter { it.isNotBlank() }.forEach { word ->
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (textWidth(font, candidate, size) <= maxWidth) {
                current = candidate
            } else {
                if (current.isNotEmpty()) out += current
                current = word
            }
        }
        if (current.isNotEmpty()) out += current
        return out
    }

    private fun textWidth(font: PDFont, text: String, size: Float): Float =
        font.getStringWidth(text) / 1000f * size

    private fun inventoryLabel(item: io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem): String =
        buildList {
            add(buildString {
                if (item.quantity > 1) append(item.quantity).append(" x ")
                append(item.name)
            })
            item.location?.trim()?.takeIf { it.isNotEmpty() }?.let(::add)
            item.weightLb?.let { weight ->
                add(if (weight % 1.0 == 0.0) "${weight.toInt()} lb" else "$weight lb")
            }
        }.joinToString(" · ")

    private fun positionedSpecialItems(
        items: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem>,
        rowCount: Int,
    ): List<Pair<Int, io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem>> {
        val available = (0 until rowCount).toMutableSet()
        val positioned = mutableListOf<Pair<Int, io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem>>()
        items.take(rowCount).forEach { item ->
            val preferred = specialLocationRow(item.location)?.takeIf { it in available }
            val fallback = available.filter { it >= SPECIAL_LOCATION_LABELS.size }.minOrNull() ?: available.minOrNull()
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

    private fun notesText(plan: PcSheetPdfRenderPlan): String {
        val sheet = plan.snapshot.aggregate.sheet
        return buildList {
            sheet.generalNotes.trim().takeIf { it.isNotEmpty() }?.let(::add)
            sheet.noteCards.sortedBy { it.sortOrder }.forEach { card ->
                card.content.trim().takeIf { it.isNotEmpty() }?.let { body ->
                    add(card.title.trim().takeIf { it.isNotEmpty() }?.let { "$it: $body" } ?: body)
                }
            }
        }.joinToString(" ")
    }

    private class Fonts(document: PDDocument, loader: (String) -> InputStream?) {
        val regular = load(document, loader, "fonts/pdf/text/FiraSans-Regular.ttf")
        val semibold = load(document, loader, "fonts/pdf/text/FiraSans-SemiBold.ttf")
        val condensed = load(document, loader, "fonts/pdf/text/BarlowCondensed-Bold.ttf")
        val symbol = load(document, loader, SYMBOL_FONT)

        companion object {
            private fun load(document: PDDocument, loader: (String) -> InputStream?, path: String): PDFont {
                val input = loader(path) ?: error("Required PDF font unavailable: $path")
                return input.use { PDType0Font.load(document, it, false) }
            }
        }
    }

    private data class Rule(val startX: Float, val endX: Float, val topY: Float)
    private data class TopRect(val x: Float, val top: Float, val width: Float, val height: Float)
    private data class SpellBlock(
        val level: Int,
        val totalRect: TopRect?,
        val checkX: Float?,
        val firstCheckTop: Float?,
        val textStartX: Float,
        val textEndX: Float,
        val firstRuleY: Float,
        val rowStep: Float,
        val maxRows: Int,
    )

    private companion object {
        const val H = 792f
        const val CHECK_CP = 0xE211
        const val SYMBOL_FONT = "fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf"

        val EQUIPMENT_RULES = List(23) { 114.5f + it * 17f }
        val BACKGROUND_RULES = listOf(114.5f, 131.5f, 148.5f).map { Rule(297.5f, 597.5f, it) }
        val BONDS_RULES = listOf(182.5f, 199.5f, 216.5f).map { Rule(297.5f, 597.5f, it) }
        val IDEALS_RULES = listOf(250.5f, 267.5f, 284.5f).map { Rule(297.5f, 597.5f, it) }
        val STORY_RULES = List(11) { Rule(297.5f, 597.5f, 318.5f + it * 17f) }
        val SPECIAL_RULE_Y = List(14) { 542.5f + it * 17f }
        val SPECIAL_CHECK_TOP = List(14) { 530.5f + it * 17f }
        val SPECIAL_LOCATION_LABELS = listOf(
            "cabeza", "rostro", "cuello", "mano izquierda", "mano derecha",
            "brazo izquierdo", "brazo derecho", "pecho", "piernas", "pies",
        )

        val NOTES_Y = List(20) { 104f + it * 17f }
        val NOTES_LEFT = NOTES_Y.map { Rule(14f, 302.5f, it) }
        val NOTES_RIGHT = NOTES_Y.map { Rule(309f, 597.5f, it) }

        val SPELL_BLOCKS = listOf(
            SpellBlock(0, null, null, null, 14f, 203.5f, 126.5f, 17f, 11),
            SpellBlock(1, TopRect(48f, 314f, 23f, 26f), 14f, 346.5f, 25.5f, 203.5f, 358f, 17f, 11),
            SpellBlock(2, TopRect(48f, 546f, 23f, 26f), 14f, 579f, 25.5f, 203.5f, 590.5f, 17f, 11),
            SpellBlock(3, TopRect(243.5f, 81f, 23f, 26f), 209.5f, 114f, 221f, 399f, 125.5f, 17f, 11),
            SpellBlock(4, TopRect(243.5f, 314f, 23f, 26f), 209.5f, 346.5f, 221f, 399f, 358f, 17f, 11),
            SpellBlock(5, TopRect(243.5f, 546f, 23f, 26f), 209.5f, 579f, 221f, 399f, 590.5f, 17f, 11),
            SpellBlock(6, TopRect(439f, 81f, 23f, 26f), 405f, 114f, 416.5f, 594.5f, 125.5f, 17f, 8),
            SpellBlock(7, TopRect(439f, 266f, 23f, 26f), 405f, 298f, 416.5f, 594.5f, 310f, 17f, 8),
            SpellBlock(8, TopRect(439f, 447f, 23f, 26f), 405f, 479.5f, 416.5f, 594.5f, 491.5f, 17f, 7),
            SpellBlock(9, TopRect(439f, 614f, 23f, 26f), 405f, 647f, 416.5f, 594.5f, 658.5f, 17f, 7),
        )
    }
}
