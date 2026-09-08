package io.github.mrsimkin.dndcustomaid.shared.character

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

/**
 * Extensible reference to either one of the six built-in D&D abilities or a user-defined
 * character attribute. Exactly one target should be present for a concrete reference; both null
 * represents an intentionally unconfigured ability.
 */
@Serializable
data class CharacterAbilityReference(
    val builtIn: CharacterAbility? = null,
    val customAttributeId: Uuid? = null,
) {
    val configured: Boolean
        get() = builtIn != null || customAttributeId != null

    companion object {
        fun builtIn(ability: CharacterAbility): CharacterAbilityReference =
            CharacterAbilityReference(builtIn = ability)

        fun custom(attributeId: Uuid): CharacterAbilityReference =
            CharacterAbilityReference(customAttributeId = attributeId)

        val NONE = CharacterAbilityReference()
    }
}

@Serializable
data class CharacterCustomAttribute(
    val id: Uuid,
    val name: String,
    val abbreviation: String,
    val score: Int = 10,
    val savingThrowEnabled: Boolean = false,
    val savingThrowProficient: Boolean = false,
    val savingThrowAdjustment: Int = 0,
    val notes: String? = null,
    val sortOrder: Int = 0,
) {
    val modifier: Int
        get() = abilityModifierForScore(score)
}

@Serializable
data class CharacterCustomSkillAbilityConfiguration(
    val customSkillId: Uuid,
    val ability: CharacterAbilityReference,
)

@Serializable
data class CharacterSpellcastingProfile(
    val sourceId: Uuid,
    val ability: CharacterAbilityReference = CharacterAbilityReference.NONE,
    val saveDcAdjustment: Int = 0,
    val spellAttackAdjustment: Int = 0,
    /** Compatibility-only escape hatch for pre-successor data whose ability cannot be identified. */
    val legacySaveDcOverride: Int? = null,
    /** Compatibility-only escape hatch for pre-successor data whose ability cannot be identified. */
    val legacySpellAttackOverride: Int? = null,
)

@Serializable
enum class CharacterDamageComponentKind {
    DICE,
    FLAT,
    TEXT,
}

@Serializable
data class CharacterDamageComponent(
    val kind: CharacterDamageComponentKind,
    val expression: String,
    val typeText: String? = null,
)

@Serializable
data class CharacterCombatDamageProfile(
    val combatEntryId: Uuid,
    val components: List<CharacterDamageComponent> = emptyList(),
)

@Serializable
enum class CharacterTrackableValueKind {
    BINARY,
    COUNTER,
    CURRENT_MAX,
}

@Serializable
data class CharacterTrackableRecovery(
    val cadence: CharacterRecoveryCadence = CharacterRecoveryCadence.NONE,
    val amountMode: CharacterRecoveryAmountMode = CharacterRecoveryAmountMode.NONE,
    val fixedAmount: Int? = null,
)

@Serializable
data class CharacterCustomMarker(
    val id: Uuid,
    val name: String,
    val valueKind: CharacterTrackableValueKind = CharacterTrackableValueKind.COUNTER,
    val currentValue: Int = 0,
    val maxValue: Int? = null,
    val recovery: CharacterTrackableRecovery = CharacterTrackableRecovery(),
    val notes: String? = null,
    val sortOrder: Int = 0,
)

@Serializable
enum class CharacterResourcePlacement {
    GENERAL,
    MANAGEMENT,
    EQUIPMENT,
    TRAITS,
}

@Serializable
data class CharacterResourceSuccessorConfiguration(
    val resourceId: Uuid,
    val valueKind: CharacterTrackableValueKind = CharacterTrackableValueKind.CURRENT_MAX,
    val placements: Set<CharacterResourcePlacement> = setOf(CharacterResourcePlacement.MANAGEMENT),
)

@Serializable
data class CharacterSuccessorState(
    val customAttributes: List<CharacterCustomAttribute> = emptyList(),
    val customSkillAbilities: List<CharacterCustomSkillAbilityConfiguration> = emptyList(),
    val spellcastingProfiles: List<CharacterSpellcastingProfile> = emptyList(),
    val combatDamage: List<CharacterCombatDamageProfile> = emptyList(),
    val customMarkers: List<CharacterCustomMarker> = emptyList(),
    val resourceConfigurations: List<CharacterResourceSuccessorConfiguration> = emptyList(),
)

fun CharacterSheet.abilityScore(
    reference: CharacterAbilityReference,
    successorState: CharacterSuccessorState,
): Int? = when {
    reference.builtIn != null -> abilityScore(reference.builtIn)
    reference.customAttributeId != null -> successorState.customAttributes
        .firstOrNull { it.id == reference.customAttributeId }
        ?.score
    else -> null
}

fun CharacterSheet.abilityModifier(
    reference: CharacterAbilityReference,
    successorState: CharacterSuccessorState,
): Int? = abilityScore(reference, successorState)?.let(::abilityModifierForScore)

fun CharacterSheet.spellSaveDc(
    profile: CharacterSpellcastingProfile,
    successorState: CharacterSuccessorState,
): Int? = profile.legacySaveDcOverride ?: abilityModifier(profile.ability, successorState)?.let { modifier ->
    8 + modifier + finalProficiencyBonus + profile.saveDcAdjustment
}

fun CharacterSheet.spellAttackModifier(
    profile: CharacterSpellcastingProfile,
    successorState: CharacterSuccessorState,
): Int? = profile.legacySpellAttackOverride ?: abilityModifier(profile.ability, successorState)?.let { modifier ->
    modifier + finalProficiencyBonus + profile.spellAttackAdjustment
}

fun CharacterCustomSkill.abilityReference(successorState: CharacterSuccessorState): CharacterAbilityReference =
    successorState.customSkillAbilities
        .firstOrNull { it.customSkillId == id }
        ?.ability
        ?: CharacterAbilityReference.builtIn(ability)

internal fun SpellcastingAbility.toBuiltInCharacterAbilityOrNull(): CharacterAbility? = when (this) {
    SpellcastingAbility.STRENGTH -> CharacterAbility.STRENGTH
    SpellcastingAbility.DEXTERITY -> CharacterAbility.DEXTERITY
    SpellcastingAbility.CONSTITUTION -> CharacterAbility.CONSTITUTION
    SpellcastingAbility.INTELLIGENCE -> CharacterAbility.INTELLIGENCE
    SpellcastingAbility.WISDOM -> CharacterAbility.WISDOM
    SpellcastingAbility.CHARISMA -> CharacterAbility.CHARISMA
    SpellcastingAbility.OTHER,
    SpellcastingAbility.NONE,
    -> null
}
