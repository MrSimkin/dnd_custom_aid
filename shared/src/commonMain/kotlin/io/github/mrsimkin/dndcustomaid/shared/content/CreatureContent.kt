package io.github.mrsimkin.dndcustomaid.shared.content

import kotlinx.serialization.Serializable

@Serializable
data class CreatureAbilityScores(
    val strength: Int = 10,
    val dexterity: Int = 10,
    val constitution: Int = 10,
    val intelligence: Int = 10,
    val wisdom: Int = 10,
    val charisma: Int = 10,
) {
    init {
        require(strength > 0) { "Creature Strength must be positive." }
        require(dexterity > 0) { "Creature Dexterity must be positive." }
        require(constitution > 0) { "Creature Constitution must be positive." }
        require(intelligence > 0) { "Creature Intelligence must be positive." }
        require(wisdom > 0) { "Creature Wisdom must be positive." }
        require(charisma > 0) { "Creature Charisma must be positive." }
    }
}

@Serializable
data class CreaturePayload(
    val size: String = "",
    val creatureType: String = "",
    val alignment: String = "",
    val armorClass: Int? = null,
    val armorClassDetails: String = "",
    val hitPoints: Int? = null,
    val hitDice: String = "",
    val speed: String = "",
    val abilityScores: CreatureAbilityScores = CreatureAbilityScores(),
    val savingThrows: String = "",
    val skills: String = "",
    val damageVulnerabilities: String = "",
    val damageResistances: String = "",
    val damageImmunities: String = "",
    val conditionImmunities: String = "",
    val senses: String = "",
    val languages: String = "",
    val challengeRating: String = "",
    val proficiencyBonus: Int? = null,
    val traits: String = "",
    val actions: String = "",
    val bonusActions: String = "",
    val reactions: String = "",
    val legendaryActions: String = "",
    val lairActions: String = "",
    val tactics: String = "",
    val notes: String = "",
) {
    init {
        require(armorClass == null || armorClass > 0) { "Creature Armor Class must be positive when present." }
        require(hitPoints == null || hitPoints > 0) { "Creature hit points must be positive when present." }
        require(proficiencyBonus == null || proficiencyBonus >= 0) {
            "Creature proficiency bonus must not be negative when present."
        }
    }
}

@Serializable
data class CreatureContent(
    val item: ReusableContentItem,
    val payload: CreaturePayload,
) {
    init {
        require(item.family == ReusableContentFamily.CREATURE) {
            "CreatureContent must wrap CREATURE reusable content."
        }
    }
}
