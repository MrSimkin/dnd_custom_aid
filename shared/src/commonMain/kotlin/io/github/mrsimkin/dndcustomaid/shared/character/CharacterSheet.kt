package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

enum class CharacterStatus {
    ACTIVE,
    INACTIVE,
    RETIRED,
    DEAD,
}

enum class SkillKey {
    ACROBATICS,
    ANIMAL_HANDLING,
    ARCANA,
    ATHLETICS,
    DECEPTION,
    HISTORY,
    INSIGHT,
    INTIMIDATION,
    INVESTIGATION,
    MEDICINE,
    NATURE,
    PERCEPTION,
    PERFORMANCE,
    PERSUASION,
    RELIGION,
    SLEIGHT_OF_HAND,
    STEALTH,
    SURVIVAL,
}

enum class SkillTraining {
    NONE,
    PROFICIENT,
    EXPERTISE,
}

data class CharacterSkill(
    val key: SkillKey,
    val modifier: Int,
    val training: SkillTraining,
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
    val initiativeModifier: Int,
    val speed: Int,
    val proficiencyBonus: Int,
    val strengthSave: Int,
    val dexteritySave: Int,
    val constitutionSave: Int,
    val intelligenceSave: Int,
    val wisdomSave: Int,
    val charismaSave: Int,
    val passivePerception: Int,
    val spellSaveDc: Int?,
    val classes: List<CharacterClassLevel>,
    val skills: List<CharacterSkill>,
) {
    val totalLevel: Int
        get() = classes.sumOf { it.level }
}
