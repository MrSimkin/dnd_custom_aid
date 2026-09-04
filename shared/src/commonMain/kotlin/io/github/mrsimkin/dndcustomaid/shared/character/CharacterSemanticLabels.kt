package io.github.mrsimkin.dndcustomaid.shared.character

fun characterRulesFamilyBadgeLabel(value: CharacterRulesFamily): String = when (value) {
    CharacterRulesFamily.DND_5E -> "5e"
    CharacterRulesFamily.DND_5_5E -> "5.5e"
    CharacterRulesFamily.CUSTOM -> "Custom"
    CharacterRulesFamily.UNSPECIFIED -> "Sin especificar"
}

fun characterSourceBadgeLabel(source: String?): String? = source
    ?.trim()
    ?.takeIf { it.isNotEmpty() }
    ?.let { "Fuente · $it" }
