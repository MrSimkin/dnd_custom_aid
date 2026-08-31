package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

enum class CharacterStatus {
    ACTIVE,
    INACTIVE,
    RETIRED,
    DEAD,
}

enum class CharacterAbility {
    STRENGTH,
    DEXTERITY,
    CONSTITUTION,
    INTELLIGENCE,
    WISDOM,
    CHARISMA,
}

enum class SkillKey(val ability: CharacterAbility) {
    ACROBATICS(CharacterAbility.DEXTERITY),
    ANIMAL_HANDLING(CharacterAbility.WISDOM),
    ARCANA(CharacterAbility.INTELLIGENCE),
    ATHLETICS(CharacterAbility.STRENGTH),
    DECEPTION(CharacterAbility.CHARISMA),
    HISTORY(CharacterAbility.INTELLIGENCE),
    INSIGHT(CharacterAbility.WISDOM),
    INTIMIDATION(CharacterAbility.CHARISMA),
    INVESTIGATION(CharacterAbility.INTELLIGENCE),
    MEDICINE(CharacterAbility.WISDOM),
    NATURE(CharacterAbility.INTELLIGENCE),
    PERCEPTION(CharacterAbility.WISDOM),
    PERFORMANCE(CharacterAbility.CHARISMA),
    PERSUASION(CharacterAbility.CHARISMA),
    RELIGION(CharacterAbility.INTELLIGENCE),
    SLEIGHT_OF_HAND(CharacterAbility.DEXTERITY),
    STEALTH(CharacterAbility.DEXTERITY),
    SURVIVAL(CharacterAbility.WISDOM),
}

enum class SkillTraining {
    NONE,
    PROFICIENT,
    EXPERTISE,
}

enum class CharacterCombatEntryType {
    ATTACK,
    ACTION,
    BONUS_ACTION,
    REACTION,
    OTHER,
}

enum class SpellcastingAbility {
    STRENGTH,
    DEXTERITY,
    CONSTITUTION,
    INTELLIGENCE,
    WISDOM,
    CHARISMA,
    OTHER,
    NONE,
}

data class CharacterSkill(
    val key: SkillKey,
    val adjustment: Int,
    val training: SkillTraining,
)

data class CharacterSavingThrow(
    val ability: CharacterAbility,
    val proficient: Boolean,
    val adjustment: Int,
)

data class CharacterClassLevel(
    val id: Uuid,
    val name: String,
    val level: Int,
    val hitDieSides: Int,
    val hitDiceRemaining: Int,
    val sortOrder: Int,
)

data class CharacterSpellSlot(
    val level: Int,
    val totalSlots: Int,
    val spentSlots: Int,
)

data class CharacterCombatEntry(
    val id: Uuid,
    val name: String,
    val type: CharacterCombatEntryType,
    val attackModifier: Int?,
    val damageEffect: String,
    val rangeText: String?,
    val notes: String?,
    val sortOrder: Int,
)

data class CharacterInventoryItem(
    val id: Uuid,
    val name: String,
    val quantity: Int,
    val weightLb: Double?,
    val equipped: Boolean,
    val notes: String?,
    val sortOrder: Int,
    val special: Boolean,
    val description: String?,
    val location: String?,
    val attuned: Boolean,
) {
    val carriedWeightLb: Double
        get() = (weightLb ?: 0.0) * quantity
}

data class CharacterCurrency(
    val key: String,
    val name: String,
    val amount: Int,
    val sortOrder: Int,
    val isDefault: Boolean,
)

data class CharacterSheet(
    val id: Uuid,
    val campaignId: Uuid,
    val name: String,
    val status: CharacterStatus,
    val updatedAtEpochSeconds: Long,
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int,
    val armorClass: Int,
    val maxHp: Int,
    val currentHp: Int,
    val tempHp: Int,
    val initiativeAdjustment: Int,
    val speed: Int,
    /**
     * Compatibility snapshot for the current V4 Android editor.
     * Persistence converts it into [proficiencyBonusAdjustment], which is the new authoritative state.
     */
    val proficiencyBonus: Int,
    val savingThrows: List<CharacterSavingThrow>,
    val passivePerceptionAdjustment: Int,
    val spellSaveDc: Int?,
    val classes: List<CharacterClassLevel>,
    val skills: List<CharacterSkill>,
    val proficiencyBonusAdjustment: Int = 0,
    val spellAttackModifier: Int? = null,
    val spellcastingAbility: SpellcastingAbility = SpellcastingAbility.NONE,
    val spellSlots: List<CharacterSpellSlot> = emptyList(),
    val combatEntries: List<CharacterCombatEntry> = emptyList(),
    val inventoryItems: List<CharacterInventoryItem> = emptyList(),
    val currencies: List<CharacterCurrency> = emptyList(),
) {
    val totalLevel: Int
        get() = classes.sumOf { it.level }

    val standardProficiencyBonus: Int
        get() = standardProficiencyBonusForLevel(totalLevel)

    val finalProficiencyBonus: Int
        get() = standardProficiencyBonus + proficiencyBonusAdjustment

    val carriedWeightLb: Double
        get() = inventoryItems.sumOf { it.carriedWeightLb }

    val attunedItemCount: Int
        get() = inventoryItems.count { it.special && it.attuned }

    fun abilityScore(ability: CharacterAbility): Int = when (ability) {
        CharacterAbility.STRENGTH -> strength
        CharacterAbility.DEXTERITY -> dexterity
        CharacterAbility.CONSTITUTION -> constitution
        CharacterAbility.INTELLIGENCE -> intelligence
        CharacterAbility.WISDOM -> wisdom
        CharacterAbility.CHARISMA -> charisma
    }

    fun abilityModifier(ability: CharacterAbility): Int = abilityModifierForScore(abilityScore(ability))

    val initiativeModifier: Int
        get() = abilityModifier(CharacterAbility.DEXTERITY) + initiativeAdjustment

    fun savingThrow(ability: CharacterAbility): CharacterSavingThrow =
        savingThrows.firstOrNull { it.ability == ability }
            ?: CharacterSavingThrow(ability = ability, proficient = false, adjustment = 0)

    fun savingThrowTotal(ability: CharacterAbility): Int {
        val state = savingThrow(ability)
        return abilityModifier(ability) +
            (if (state.proficient) finalProficiencyBonus else 0) +
            state.adjustment
    }

    fun skill(key: SkillKey): CharacterSkill =
        skills.firstOrNull { it.key == key }
            ?: CharacterSkill(key = key, adjustment = 0, training = SkillTraining.NONE)

    fun skillTotal(key: SkillKey): Int {
        val state = skill(key)
        val proficiencyContribution = when (state.training) {
            SkillTraining.NONE -> 0
            SkillTraining.PROFICIENT -> finalProficiencyBonus
            SkillTraining.EXPERTISE -> finalProficiencyBonus * 2
        }
        return abilityModifier(key.ability) + proficiencyContribution + state.adjustment
    }

    val passivePerception: Int
        get() = 10 + skillTotal(SkillKey.PERCEPTION) + passivePerceptionAdjustment
}

fun abilityModifierForScore(score: Int): Int {
    val difference = score - 10
    return if (difference >= 0) {
        difference / 2
    } else {
        -((-difference + 1) / 2)
    }
}

fun standardProficiencyBonusForLevel(totalLevel: Int): Int = when {
    totalLevel <= 4 -> 2
    totalLevel <= 8 -> 3
    totalLevel <= 12 -> 4
    totalLevel <= 16 -> 5
    else -> 6
}
