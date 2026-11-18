package io.github.mrsimkin.dndcustomaid.shared.character

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/*
 * Agent check of 0.7.0-preqa.4: text copied from a PDF (a PC story from the Campus Celeste set) can
 * carry control characters such as the page break U+000C. The sheet's fonts have no glyph for them
 * and «Guardar PDF…» failed with «No glyph for U+000C». The sheet is printed from a copy of the PC
 * where those characters are replaced; the PC itself and the V11 layout do not change.
 */

private val printableJson = Json { encodeDefaults = true }

/**
 * The text as the sheet can print it: page and vertical breaks become line breaks and other control
 * characters go. Line breaks, carriage returns and tabs stay: the V11 sheet already lays them out.
 */
fun printableSheetText(text: String): String {
    fun unprintable(c: Char) = (c < ' ' && c != '\n' && c != '\r' && c != '\t') || c == '\u007F'
    if (text.none(::unprintable)) return text
    val out = StringBuilder(text.length)
    for (c in text) {
        when {
            c == '\u000C' || c == '\u000B' -> out.append('\n')
            unprintable(c) -> Unit
            else -> out.append(c)
        }
    }
    return out.toString()
}

/** A copy of the PC with every text made printable. */
fun CharacterSheet.printable(): CharacterSheet {
    val encoded = printableJson.encodeToJsonElement(CharacterSheet.serializer(), this)
    val cleaned = encoded.mapStrings(::printableSheetText)
    return if (cleaned == encoded) this else printableJson.decodeFromJsonElement(CharacterSheet.serializer(), cleaned)
}

/** The export sources with printable PCs. */
fun PcSheetExportSources.printable(): PcSheetExportSources = copy(
    permanent = permanent.copy(sheet = permanent.sheet.printable()),
    currentSnapshot = currentSnapshot?.let { it.copy(sheet = it.sheet.printable()) },
)

private fun JsonElement.mapStrings(transform: (String) -> String): JsonElement = when (this) {
    is JsonObject -> JsonObject(mapValues { (_, value) -> value.mapStrings(transform) })
    is JsonArray -> JsonArray(map { it.mapStrings(transform) })
    is JsonPrimitive -> if (isString) JsonPrimitive(transform(content)) else this
}
