package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Three-letter compact attribute key used when the owning attribute is not structurally obvious.
 */
fun pcSheetCompactAttributeKey(abbreviation: String): String =
    foldAttributeKey(abbreviation.trim()).take(3)

/**
 * Integrated source-style attribute title.
 *
 * When the compact key represents the first letters of the semantic name (ignoring common Spanish
 * accents), preserve the complete identity by replacing only that prefix:
 *
 * Éter + ETE -> ETEr
 * Fortuna + FOR -> FORtuna
 *
 * If the abbreviation is not actually a prefix of the semantic name, keep the complete name rather
 * than inventing a misleading merged label or falling back to a generic "KEY · Name" construction.
 */
fun pcSheetIntegratedAttributeTitle(
    name: String,
    abbreviation: String,
): String {
    val cleanName = name.trim()
    val key = pcSheetCompactAttributeKey(abbreviation)
    if (cleanName.isEmpty() || key.isEmpty()) return cleanName

    val prefixLength = minOf(key.length, cleanName.length)
    val namePrefix = cleanName.take(prefixLength)
    return if (foldAttributeKey(namePrefix) == foldAttributeKey(key)) {
        key + cleanName.drop(prefixLength)
    } else {
        cleanName
    }
}

/**
 * Append an owning-attribute key only when the visual structure does not already expose that owner.
 */
fun pcSheetContextualSkillIdentity(
    skillName: String,
    attributeKey: String,
    ownerAttributeStructurallyVisible: Boolean,
): String {
    val cleanSkill = skillName.trim()
    val key = pcSheetCompactAttributeKey(attributeKey)
    return when {
        cleanSkill.isEmpty() -> cleanSkill
        ownerAttributeStructurallyVisible || key.isEmpty() -> cleanSkill
        else -> "$cleanSkill ($key)"
    }
}

private fun foldAttributeKey(value: String): String =
    value.uppercase().map { char ->
        when (char) {
            'Á', 'À', 'Ä', 'Â' -> 'A'
            'É', 'È', 'Ë', 'Ê' -> 'E'
            'Í', 'Ì', 'Ï', 'Î' -> 'I'
            'Ó', 'Ò', 'Ö', 'Ô' -> 'O'
            'Ú', 'Ù', 'Ü', 'Û' -> 'U'
            else -> char
        }
    }.joinToString("")
