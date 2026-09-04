package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Presentation helpers for the campaign character list.
 *
 * These helpers project existing authoritative CharacterSheet data only. They do not create or
 * persist a separate character-summary authority.
 */
fun characterListClassSummary(classes: List<CharacterClassLevel>): String {
    if (classes.isEmpty()) return "Sin clase registrada"

    return classes
        .sortedWith(compareBy<CharacterClassLevel> { it.sortOrder }.thenBy { it.id.toString() })
        .joinToString(" / ") { classLevel ->
            buildString {
                append(classLevel.name.trim().ifBlank { "Clase" })
                append(' ')
                append(classLevel.level)
                classLevel.subclassName
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }
                    ?.let { subclass ->
                        append(" (")
                        append(subclass)
                        append(')')
                    }
            }
        }
}

fun characterListFreshnessLabel(
    updatedAtEpochSeconds: Long,
    nowEpochSeconds: Long,
): String {
    val elapsedSeconds = (nowEpochSeconds - updatedAtEpochSeconds).coerceAtLeast(0L)

    return when {
        elapsedSeconds < 60L -> "Actualizado ahora"
        elapsedSeconds < 3_600L -> "Actualizado hace ${elapsedSeconds / 60L} min"
        elapsedSeconds < 86_400L -> "Actualizado hace ${elapsedSeconds / 3_600L} h"
        else -> "Actualizado hace ${elapsedSeconds / 86_400L} d"
    }
}
