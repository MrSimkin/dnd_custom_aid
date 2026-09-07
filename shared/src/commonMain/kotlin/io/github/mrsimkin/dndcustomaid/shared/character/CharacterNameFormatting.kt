package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Keeps the first visible character of a character name uppercase while preserving
 * the rest of the user's text exactly as entered. Leading whitespace is tolerated
 * during editing instead of being destructively trimmed.
 */
fun characterProperNameInput(value: String): String {
    val firstVisibleIndex = value.indexOfFirst { !it.isWhitespace() }
    if (firstVisibleIndex < 0) return value
    val current = value[firstVisibleIndex]
    val upper = current.uppercaseChar()
    if (current == upper) return value
    return buildString(value.length) {
        append(value, 0, firstVisibleIndex)
        append(upper)
        append(value, firstVisibleIndex + 1, value.length)
    }
}
