package io.github.mrsimkin.dndcustomaid.shared.character

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull

/**
 * Read-only PDF/export repair for text that was valid UTF-8 before its bytes were once decoded as
 * Windows-1252/Latin-1. This never writes back to character persistence.
 *
 * Repair is deliberately conservative: only whitespace-delimited tokens containing recognizable
 * mojibake markers are candidates, the token must be reversible to bytes, UTF-8 decoding must
 * succeed, and the repaired candidate must reduce the marker count.
 */
internal fun repairLikelyUtf8MojibakeForPdf(text: String): String {
    if (!text.any(::isMojibakeMarker)) return text

    val pieces = Regex("""(\s+)""").split(text)
    if (pieces.size == 1) return repairMojibakeToken(text)

    val whitespace = Regex("""\s+""").findAll(text).map { it.value }.toList()
    return buildString {
        pieces.forEachIndexed { index, piece ->
            append(repairMojibakeToken(piece))
            if (index < whitespace.size) append(whitespace[index])
        }
    }
}

private fun repairMojibakeToken(token: String): String {
    if (!token.any(::isMojibakeMarker)) return token

    var current = token
    repeat(2) {
        val candidate = decodeWindows1252AsUtf8(current) ?: return current
        if (mojibakeScore(candidate) >= mojibakeScore(current) || '\uFFFD' in candidate) return current
        current = candidate
        if (!current.any(::isMojibakeMarker)) return current
    }
    return current
}

private fun decodeWindows1252AsUtf8(value: String): String? {
    val bytes = ByteArray(value.length)
    value.forEachIndexed { index, ch ->
        val byteValue = windows1252Byte(ch) ?: return null
        bytes[index] = byteValue.toByte()
    }
    return try {
        bytes.decodeToString(throwOnInvalidSequence = true)
    } catch (_: IllegalArgumentException) {
        null
    }
}

private fun windows1252Byte(ch: Char): Int? = when (ch.code) {
    in 0x0000..0x007F -> ch.code
    in 0x00A0..0x00FF -> ch.code
    in 0x0080..0x009F -> ch.code
    0x20AC -> 0x80
    0x201A -> 0x82
    0x0192 -> 0x83
    0x201E -> 0x84
    0x2026 -> 0x85
    0x2020 -> 0x86
    0x2021 -> 0x87
    0x02C6 -> 0x88
    0x2030 -> 0x89
    0x0160 -> 0x8A
    0x2039 -> 0x8B
    0x0152 -> 0x8C
    0x017D -> 0x8E
    0x2018 -> 0x91
    0x2019 -> 0x92
    0x201C -> 0x93
    0x201D -> 0x94
    0x2022 -> 0x95
    0x2013 -> 0x96
    0x2014 -> 0x97
    0x02DC -> 0x98
    0x2122 -> 0x99
    0x0161 -> 0x9A
    0x203A -> 0x9B
    0x0153 -> 0x9C
    0x017E -> 0x9E
    0x0178 -> 0x9F
    else -> null
}

private fun isMojibakeMarker(ch: Char): Boolean =
    ch == 'Ã' ||
        ch == 'Â' ||
        ch == 'â' ||
        ch == 'ð' ||
        ch == 'ï' ||
        ch.code in 0x0080..0x009F ||
        ch == '\uFFFD'

private fun mojibakeScore(value: String): Int =
    value.count(::isMojibakeMarker)

private val pdfTextRepairJson = Json {
    encodeDefaults = true
    explicitNulls = true
}

internal fun PcSheetExportAggregate.repairTextForPdfExport(): PcSheetExportAggregate =
    copy(
        sheet = repairSerializableText(sheet, CharacterSheet.serializer()),
        closure = repairSerializableText(closure, CharacterClosureState.serializer()),
        successor = repairSerializableText(successor, CharacterSuccessorState.serializer()),
    )

private fun <T> repairSerializableText(value: T, serializer: KSerializer<T>): T {
    val encoded = pdfTextRepairJson.encodeToJsonElement(serializer, value)
    return pdfTextRepairJson.decodeFromJsonElement(serializer, repairJsonText(encoded))
}

private fun repairJsonText(element: JsonElement): JsonElement = when (element) {
    is JsonObject -> JsonObject(element.mapValues { (_, value) -> repairJsonText(value) })
    is JsonArray -> JsonArray(element.map(::repairJsonText))
    is JsonPrimitive -> if (element.isString) {
        JsonPrimitive(repairLikelyUtf8MojibakeForPdf(element.content))
    } else {
        // Preserve primitive numeric/boolean/null representation exactly.
        element
    }
}
