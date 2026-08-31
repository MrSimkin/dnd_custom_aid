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
    val proficiencyBonus: Int,
    val savingThrows: List<CharacterSavingThrow>,
    val passivePerceptionAdjustment: Int,
    val spellSaveDc: Int?,
    val classes: List<CharacterClassLevel>,
    val skills: List<CharacterSkill>,
) {
    val totalLevel: Int
        get() = classes.sumOf { it.level }

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
            (if (state.proficient) proficiencyBonus else 0) +
            state.adjustment
    }

    fun skill(key: SkillKey): CharacterSkill =
        skills.firstOrNull { it.key == key }
            ?: CharacterSkill(key = key, adjustment = 0, training = SkillTraining.NONE)

    fun skillTotal(key: SkillKey): Int {
        val state = skill(key)
        val proficiencyContribution = when (state.training) {
            SkillTraining.NONE -> 0
            SkillTraining.PROFICIENT -> proficiencyBonus
            SkillTraining.EXPERTISE -> proficiencyBonus * 2
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
