package io.github.mrsimkin.dndcustomaid.shared.dm

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * The SRD with its layout (QA 0.7.0-preqa.2, A5, L1, L2): every page and every catalog entry as a
 * light Markdown made from the official PDF by scripts/build_srd_layout.py, so the App can show
 * headings, tables, bold labels and lists instead of raw lines.
 */
@Serializable
data class SrdLayoutPage(val page: Int, val md: String)

@Serializable
data class SrdLayout(
    val format: String,
    val formatVersion: Int,
    val version: String,
    val attribution: String = "",
    val pages: List<SrdLayoutPage> = emptyList(),
    val entries: Map<String, String> = emptyMap(),
) {
    /** «5e» for SRD 5.1, «5.5e» for SRD 5.2.1, as in [RulesOption.edition]. */
    val edition: String get() = if (version == "5.1") "5e" else "5.5e"

    fun page(number: Int): String? = pages.firstOrNull { it.page == number }?.md

    /**
     * The formatted text of one catalog entry. Agent check of 0.7.0-preqa.4: an entry stops where a
     * heading with the name of another entry of the same kind starts («Bola de fuego» carried «Bola de
     * fuego de explosión retardada» after it).
     */
    fun entry(kind: String, name: String): String? {
        val md = entries["$kind|$name"]?.takeIf { it.isNotBlank() } ?: return null
        val lines = md.lines()
        val cut = lines.indexOfFirst { line ->
            line.startsWith("#") && line.trimStart('#').trim().let { title ->
                title != name && (entries.containsKey("$kind|$title") || title.startsWith("$name "))
            }
        }
        return if (cut <= 0) md else lines.take(cut).joinToString("\n").trimEnd().takeIf { it.isNotBlank() }
    }
}

val SRD_LAYOUT_FILES: List<String> = listOf("srd_5_1_es.layout.json", "srd_5_2_1_es.layout.json")

private val layoutJson = Json { ignoreUnknownKeys = true }

fun parseSrdLayout(text: String): SrdLayout = layoutJson.decodeFromString(SrdLayout.serializer(), text)

/** A piece of a line; bold for headings' run-in labels such as «**Alcance:** 45 m». */
data class SrdSpan(val text: String, val bold: Boolean = false)

sealed interface SrdBlock {
    data class Heading(val level: Int, val text: String) : SrdBlock
    data class Paragraph(val spans: List<SrdSpan>) : SrdBlock
    data class Bullet(val spans: List<SrdSpan>) : SrdBlock
    data class Table(val header: List<String>?, val rows: List<List<String>>) : SrdBlock
}

/** Splits the light Markdown into blocks the screens draw. Unknown syntax stays as plain text. */
fun srdBlocks(md: String): List<SrdBlock> {
    val blocks = mutableListOf<SrdBlock>()
    val lines = md.lines()
    var i = 0
    while (i < lines.size) {
        val line = lines[i].trimEnd()
        when {
            line.isBlank() -> i++
            line.startsWith("|") -> {
                val tableLines = mutableListOf<String>()
                while (i < lines.size && lines[i].trimStart().startsWith("|")) {
                    tableLines += lines[i].trim()
                    i++
                }
                blocks += table(tableLines)
            }
            line.startsWith("#") -> {
                val level = line.takeWhile { it == '#' }.length
                blocks += SrdBlock.Heading(level.coerceIn(1, 4), line.drop(level).trim())
                i++
            }
            line.startsWith("• ") -> {
                blocks += SrdBlock.Bullet(srdSpans(line.removePrefix("• ")))
                i++
            }
            else -> {
                val previous = blocks.lastOrNull()
                val previousSpans = (previous as? SrdBlock.Paragraph)?.spans ?: (previous as? SrdBlock.Bullet)?.spans
                val previousText = previousSpans?.joinToString("") { it.text }?.trimEnd().orEmpty()
                if (previousSpans != null && continuesSentence(previousText, line)) {
                    // Agent check of 0.7.0-preqa.4: the PDF layout breaks a sentence after a run-in
                    // label («**Resistencia al daño.** Tienes resistencia al daño» / «contundente…»)
                    // and inside hyphenated words («mante‑» / «ner»); both read as one paragraph.
                    val hyphenated = previousText.lastOrNull() in HYPHENS
                    val joined = trimTrailing(previousSpans, if (hyphenated) 1 else 0) +
                        srdSpans(if (hyphenated) line.trimStart() else " " + line.trimStart())
                    blocks[blocks.lastIndex] = if (previous is SrdBlock.Bullet) SrdBlock.Bullet(joined) else SrdBlock.Paragraph(joined)
                } else {
                    blocks += SrdBlock.Paragraph(srdSpans(line))
                }
                i++
            }
        }
    }
    return blocks
}

private val HYPHENS = setOf('-', '‐', '‑', '­')

/** A line that starts in lowercase after a line with no closing punctuation continues it. */
private fun continuesSentence(previousText: String, line: String): Boolean {
    val first = line.trimStart().firstOrNull() ?: return false
    val last = previousText.lastOrNull() ?: return false
    return first.isLowerCase() && last !in ".:;!?»”)]"
}

/** The spans without their last [drop] characters (a line-end hyphen), and without trailing spaces. */
private fun trimTrailing(spans: List<SrdSpan>, drop: Int): List<SrdSpan> {
    if (spans.isEmpty()) return spans
    val last = spans.last()
    val text = last.text.trimEnd().dropLast(drop)
    return spans.dropLast(1) + last.copy(text = text)
}

private fun table(lines: List<String>): SrdBlock.Table {
    fun cells(line: String) = line.trim().removePrefix("|").removeSuffix("|").split("|").map { it.trim() }
    val separator = lines.indexOfFirst { it.replace("|", "").replace("-", "").isBlank() && it.contains("---") }
    return if (separator == 1) {
        SrdBlock.Table(cells(lines[0]), lines.drop(2).map(::cells))
    } else {
        SrdBlock.Table(null, lines.filterIndexed { index, _ -> index != separator }.map(::cells))
    }
}

/** «**Alcance:** 45 m» → bold «Alcance:», then « 45 m». */
fun srdSpans(text: String): List<SrdSpan> {
    val out = mutableListOf<SrdSpan>()
    var rest = text
    while (rest.isNotEmpty()) {
        val start = rest.indexOf("**")
        if (start < 0) {
            out += SrdSpan(rest)
            break
        }
        val end = rest.indexOf("**", start + 2)
        if (end < 0) {
            out += SrdSpan(rest)
            break
        }
        if (start > 0) out += SrdSpan(rest.substring(0, start))
        out += SrdSpan(rest.substring(start + 2, end), bold = true)
        rest = rest.substring(end + 2)
    }
    return out
}
