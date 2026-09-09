package io.github.mrsimkin.dndcustomaid.android

/** Keeps a draft suitable for an optional signed integer field without forcing premature parsing. */
internal fun sanitizeSignedIntegerInputV4(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) return ""

    val negative = trimmed.startsWith('-')
    val positive = trimmed.startsWith('+')
    val digits = trimmed.drop(if (negative || positive) 1 else 0).filter(Char::isDigit)
    if (digits.isEmpty()) return if (negative) "-" else if (positive) "+" else ""
    return when {
        negative -> "-$digits"
        positive -> "+$digits"
        else -> digits
    }
}
