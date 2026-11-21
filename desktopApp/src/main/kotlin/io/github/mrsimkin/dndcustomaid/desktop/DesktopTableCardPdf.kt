package io.github.mrsimkin.dndcustomaid.desktop

import io.github.mrsimkin.dndcustomaid.shared.content.CreatureAbilityScores
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.pdmodel.font.Standard14Fonts
import java.io.File

/*
 * «Imprimir la ficha de mesa» (owner, 2026-11-18, «J»: the table card, one per sheet, «pero
 * necesito formato para el stat-block si aplica»).
 *
 * Deliberately plain: the same words the card shows on screen, on paper, so the DM can run the
 * table without a screen. A creature, or a PNJ with combat numbers, also prints its stat block in
 * the SRD's order, so it reads like the blocks the DM already knows.
 */

internal data class TableCardStatBlock(
    val line: String,
    val armorClass: String,
    val hitPoints: String,
    val speed: String,
    val abilities: CreatureAbilityScores,
    val savingThrows: String = "",
    val skills: String = "",
    val senses: String = "",
    val languages: String = "",
    val challenge: String = "",
    val traits: String = "",
    val actions: String = "",
    val reactions: String = "",
)

internal data class TableCard(
    val title: String,
    val subtitle: String,
    /** Label and text, in the order of the card on screen. Empty texts are not printed. */
    val blocks: List<Pair<String, String>>,
    val stat: TableCardStatBlock? = null,
)

private const val MARGIN = 54f
private val PAGE = PDRectangle.A4

/** Writes the card to [file]: one object per sheet, more sheets only when the text needs them. */
internal fun writeTableCardPdf(card: TableCard, file: File) = writeTableCardsPdf(listOf(card), file)

/**
 * Owner's J (2026-11-20): several cards at once, «ficha de mesa, una por hoja». Each card starts
 * on its own sheet and runs on if its text needs more, so a scene and everything in it print in
 * the order the DM reads them.
 */
internal fun writeTableCardsPdf(cards: List<TableCard>, file: File) {
    require(cards.isNotEmpty()) { "No hay nada que imprimir." }
    PDDocument().use { pdf ->
        val title = PDType1Font(Standard14Fonts.FontName.TIMES_BOLD)
        val label = PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD)
        val body = PDType1Font(Standard14Fonts.FontName.HELVETICA)
        val italic = PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE)

        var page = PDPage(PAGE)
        pdf.addPage(page)
        var stream = PDPageContentStream(pdf, page)
        var y = PAGE.height - MARGIN

        fun newPage() {
            stream.close()
            page = PDPage(PAGE)
            pdf.addPage(page)
            stream = PDPageContentStream(pdf, page)
            y = PAGE.height - MARGIN
        }

        fun line(text: String, font: PDFont, size: Float, gap: Float = 4f) {
            if (text.isBlank()) return
            wrap(text, font, size, PAGE.width - MARGIN * 2).forEach { piece ->
                if (y < MARGIN + size) newPage()
                stream.beginText()
                stream.setFont(font, size)
                stream.newLineAtOffset(MARGIN, y)
                stream.showText(sanitize(piece))
                stream.endText()
                y -= size + 2f
            }
            y -= gap
        }

        fun rule() {
            if (y < MARGIN + 12f) newPage()
            stream.moveTo(MARGIN, y + 6f)
            stream.lineTo(PAGE.width - MARGIN, y + 6f)
            stream.stroke()
            y -= 8f
        }

        cards.forEachIndexed { index, card ->
        // Each card starts on its own sheet (the first one is already open).
        if (index > 0) newPage()
        line(card.title, title, 20f, gap = 2f)
        line(card.subtitle, italic, 10f, gap = 8f)
        rule()

        card.blocks.filter { it.second.isNotBlank() }.forEach { (name, text) ->
            line(name.uppercase(), label, 9f, gap = 1f)
            line(text, body, 11f, gap = 8f)
        }

        card.stat?.let { stat ->
            rule()
            line("BLOQUE DE COMBATE", label, 9f, gap = 2f)
            line(stat.line, italic, 10f, gap = 6f)
            line("Clase de Armadura  ${stat.armorClass}", body, 11f, gap = 1f)
            line("Puntos de golpe  ${stat.hitPoints}", body, 11f, gap = 1f)
            line("Velocidad  ${stat.speed}", body, 11f, gap = 6f)
            line(abilityLine(stat.abilities), body, 11f, gap = 6f)
            listOf(
                "Salvaciones" to stat.savingThrows,
                "Habilidades" to stat.skills,
                "Sentidos" to stat.senses,
                "Idiomas" to stat.languages,
                "Valor de desafío" to stat.challenge,
            ).filter { it.second.isNotBlank() }.forEach { (name, text) ->
                line("$name  $text", body, 11f, gap = 1f)
            }
            y -= 6f
            listOf("Rasgos" to stat.traits, "Acciones" to stat.actions, "Reacciones" to stat.reactions)
                .filter { it.second.isNotBlank() }
                .forEach { (name, text) ->
                    line(name.uppercase(), label, 9f, gap = 1f)
                    line(text, body, 11f, gap = 8f)
                }
        }
        }

        stream.close()
        pdf.save(file)
    }
}

private fun abilityLine(scores: CreatureAbilityScores): String =
    listOf(
        "FUE" to scores.strength,
        "DES" to scores.dexterity,
        "CON" to scores.constitution,
        "INT" to scores.intelligence,
        "SAB" to scores.wisdom,
        "CAR" to scores.charisma,
    ).joinToString("    ") { (name, value) ->
        val modifier = Math.floorDiv(value - 10, 2)
        "$name $value (${if (modifier >= 0) "+" else ""}$modifier)"
    }

/** Cuts the text into lines that fit, keeping the blank lines the DM wrote. */
private fun wrap(text: String, font: PDFont, size: Float, width: Float): List<String> {
    val lines = mutableListOf<String>()
    text.split("\n").forEach { paragraph ->
        if (paragraph.isBlank()) {
            lines += ""
            return@forEach
        }
        var current = StringBuilder()
        paragraph.trim().split(" ").forEach { word ->
            val candidate = if (current.isEmpty()) word else "$current $word"
            val fits = font.getStringWidth(sanitize(candidate)) / 1000f * size <= width
            if (fits) {
                current = StringBuilder(candidate)
            } else {
                if (current.isNotEmpty()) lines += current.toString()
                current = StringBuilder(word)
            }
        }
        if (current.isNotEmpty()) lines += current.toString()
    }
    return lines
}

/** The standard fonts have no glyph for some characters the DM may paste. */
private fun sanitize(text: String): String = text
    .replace(' ', ' ')
    .replace("—", "-")
    .replace("–", "-")
    .replace("’", "'")
    .replace("“", "\"")
    .replace("”", "\"")
    .replace("…", "...")
    .filter { it.code in 32..255 || it == '\n' }
