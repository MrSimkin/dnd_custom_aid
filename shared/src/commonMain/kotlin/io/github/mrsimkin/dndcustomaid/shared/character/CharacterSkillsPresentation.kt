package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

/** Unified Habilidades presentation row. Structural editing remains owned by each canonical source. */
data class CharacterSkillPresentation(
    val builtInKey: SkillKey? = null,
    val customSkillId: Uuid? = null,
    val label: String,
    val ability: CharacterAbilityReference,
    val training: SkillTraining,
    val adjustment: Int,
    val manualOrder: Int,
) {
    val isCustom: Boolean
        get() = customSkillId != null
}

fun characterSkillSpanishLabel(key: SkillKey): String = when (key) {
    SkillKey.ACROBATICS -> "Acrobacias"
    SkillKey.ANIMAL_HANDLING -> "Trato con animales"
    SkillKey.ARCANA -> "Conocimiento Arcano"
    SkillKey.ATHLETICS -> "Atletismo"
    SkillKey.DECEPTION -> "Engaño"
    SkillKey.HISTORY -> "Historia"
    SkillKey.INSIGHT -> "Perspicacia"
    SkillKey.INTIMIDATION -> "Intimidación"
    SkillKey.INVESTIGATION -> "Investigación"
    SkillKey.MEDICINE -> "Medicina"
    SkillKey.NATURE -> "Naturaleza"
    SkillKey.PERCEPTION -> "Percepción"
    SkillKey.PERFORMANCE -> "Interpretación"
    SkillKey.PERSUASION -> "Persuasión"
    SkillKey.RELIGION -> "Religión"
    SkillKey.SLEIGHT_OF_HAND -> "Juego de manos"
    SkillKey.STEALTH -> "Sigilo"
    SkillKey.SURVIVAL -> "Supervivencia"
}

/**
 * Merges the 18 built-in skills and successor custom skills without creating a second source of
 * truth. `Por habilidades` consumes the alphabetical projection; `Por característica` can group
 * these same rows by [ability].
 */
fun presentCharacterSkills(
    builtInSkills: List<CharacterSkill>,
    customSkills: List<CharacterCustomSkill>,
    successorState: CharacterSuccessorState,
    order: CharacterPresentationOrder = CharacterPresentationOrder.ALPHABETICAL,
): List<CharacterSkillPresentation> {
    val builtIn = SkillKey.entries.mapIndexed { index, key ->
        val state = builtInSkills.firstOrNull { it.key == key }
            ?: CharacterSkill(key = key, adjustment = 0, training = SkillTraining.NONE)
        CharacterSkillPresentation(
            builtInKey = key,
            label = characterSkillSpanishLabel(key),
            ability = CharacterAbilityReference.builtIn(key.ability),
            training = state.training,
            adjustment = state.adjustment,
            manualOrder = index,
        )
    }
    val custom = customSkills.map { skill ->
        CharacterSkillPresentation(
            customSkillId = skill.id,
            label = skill.name.trim(),
            ability = skill.abilityReference(successorState),
            training = skill.training,
            adjustment = skill.adjustment,
            manualOrder = SkillKey.entries.size + skill.sortOrder,
        )
    }
    val merged = builtIn + custom
    return when (order) {
        CharacterPresentationOrder.MANUAL -> merged.sortedWith(
            compareBy<CharacterSkillPresentation> { it.manualOrder }
                .thenBy { it.builtInKey?.name ?: it.customSkillId.toString() },
        )
        CharacterPresentationOrder.ALPHABETICAL -> merged.sortedWith(
            compareBy<CharacterSkillPresentation> { normalizeCharacterSearchText(it.label) }
                .thenBy { it.manualOrder }
                .thenBy { it.builtInKey?.name ?: it.customSkillId.toString() },
        )
    }
}

fun characterAbilityReferenceAbbreviation(
    reference: CharacterAbilityReference,
    successorState: CharacterSuccessorState,
): String = when (reference.builtIn) {
    CharacterAbility.STRENGTH -> "FUE"
    CharacterAbility.DEXTERITY -> "DES"
    CharacterAbility.CONSTITUTION -> "CON"
    CharacterAbility.INTELLIGENCE -> "INT"
    CharacterAbility.WISDOM -> "SAB"
    CharacterAbility.CHARISMA -> "CAR"
    null -> reference.customAttributeId
        ?.let { id -> successorState.customAttributes.firstOrNull { it.id == id }?.abbreviation }
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: "—"
}
