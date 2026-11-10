package io.github.mrsimkin.dndcustomaid.shared.character

/**
 * Approved V11 Rasgos grammar (D-0076 §3.9; owner clarification 2026-10-16).
 *
 * Paper shows ONLY identity: name - category: true source, where available.
 * The source model keeps its complete description/notes for the app; V11 does
 * not copy them to the printed Rasgos module, even on a C2 continuation.
 * Special-equipment descriptions and narrative personality traits are unrelated.
 */
object PcSheetV11TraitSummary {
    fun text(trait: CharacterTrait): String {
        val name = trait.name.trim()
        val category = when (trait.type) {
            CharacterTraitType.CLASS -> "Rasgo de Clase"
            CharacterTraitType.SPECIES_RACE -> "Rasgo de Raza"
            CharacterTraitType.BACKGROUND -> "Rasgo de Trasfondo"
            CharacterTraitType.FEAT -> "Dote"
            CharacterTraitType.GIFT_BLESSING -> "Don / Bendición"
            CharacterTraitType.OTHER -> "Otro"
        }
        val source = trait.source.trim()
        val qualifier = if (source.isEmpty()) category else "$category: $source"
        return if (name.isEmpty()) qualifier else "$name - $qualifier"
    }
}
