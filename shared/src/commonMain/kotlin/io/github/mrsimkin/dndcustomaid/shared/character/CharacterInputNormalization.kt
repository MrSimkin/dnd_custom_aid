package io.github.mrsimkin.dndcustomaid.shared.character

fun normalizeCharacterUnsignedIntegerInput(
    raw: String,
    maxDigits: Int? = null,
): String {
    require(maxDigits == null || maxDigits > 0) { "maxDigits must be positive when provided." }
    val digits = raw.filter(Char::isDigit)
    if (digits.isEmpty()) return ""

    if (maxDigits == 1) {
        return digits.last().toString()
    }

    val normalized = digits.trimStart('0').ifEmpty { "0" }
    return maxDigits?.let(normalized::take) ?: normalized
}

fun normalizeCharacterSignedIntegerInput(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return ""

    val sign = trimmed.firstOrNull()
        ?.takeIf { it == '+' || it == '-' }
        ?.toString()
        .orEmpty()
    val digits = trimmed.drop(if (sign.isEmpty()) 0 else 1).filter(Char::isDigit)
    if (digits.isEmpty()) return sign

    val normalizedDigits = digits.trimStart('0').ifEmpty { "0" }
    return sign + normalizedDigits
}
