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

enum class CharacterTraitType {
    CLASS,
    SPECIES_RACE,
    BACKGROUND,
    FEAT,
    GIFT_BLESSING,
    OTHER,
}

enum class CharacterActivationType {
    PASSIVE,
    ACTION,
    BONUS_ACTION,
    REACTION,
    OTHER,
}

enum class CharacterRulesFamily {
    UNSPECIFIED,
    DND_5E,
    DND_5_5E,
    CUSTOM,
}

enum class CharacterProficiencyType {
    LANGUAGE,
    TOOL,
    ARMOR,
    WEAPON,
    OTHER,
}

enum class CharacterClassOptionKind {
    ARTIFICER_PLAN,
    ARTIFICER_DEVICE,
    SUBCLASS_STATE,
    TECHNIQUE,
    METAMAGIC,
    INVOCATION,
    OTHER,
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
    val rulesFamily: CharacterRulesFamily = CharacterRulesFamily.UNSPECIFIED,
    val source: String? = null,
    val catalogKey: String? = null,
    val subclassName: String? = null,
    val subclassSource: String? = null,
    val subclassCatalogKey: String? = null,
    val subclassRulesFamily: CharacterRulesFamily = rulesFamily,
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
    val pinned: Boolean = false,
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

data class CharacterBackground(
    val name: String = "",
    val summary: String = "",
    val race: String = "",
    val religionFaith: String = "",
    val personalityTraits: String = "",
    val ideals: String = "",
    val bonds: String = "",
    val flaws: String = "",
    val story: String = "",
)

data class CharacterTrait(
    val id: Uuid,
    val name: String,
    val source: String,
    val type: CharacterTraitType,
    val description: String,
    val notes: String?,
    val maxUses: Int?,
    val spentUses: Int,
    val recovery: String?,
    val activation: CharacterActivationType?,
    val sortOrder: Int,
    val pinned: Boolean = false,
)

data class CharacterNote(
    val id: Uuid,
    val title: String,
    val content: String,
    val sortOrder: Int,
)

data class CharacterSpellcastingSource(
    val id: Uuid,
    val name: String,
    val linkedClassId: Uuid?,
    val sortOrder: Int,
)

data class CharacterSpellSourceAssociation(
    val sourceId: Uuid,
    val prepared: Boolean,
)

data class CharacterSpell(
    val id: Uuid,
    val name: String,
    val level: Int,
    val castingTime: String,
    val rangeText: String,
    val verbal: Boolean,
    val somatic: Boolean,
    val material: Boolean,
    val materialText: String?,
    val duration: String,
    val concentration: Boolean,
    val ritual: Boolean,
    val description: String,
    val notes: String?,
    val sortOrder: Int,
    val sourceAssociations: List<CharacterSpellSourceAssociation> = emptyList(),
    val pinned: Boolean = false,
)

data class CharacterProficiency(
    val id: Uuid,
    val type: CharacterProficiencyType,
    val name: String,
    val source: String? = null,
    val notes: String? = null,
    val sortOrder: Int = 0,
)

data class CharacterWeaponMastery(
    val id: Uuid,
    val weaponName: String,
    val masteryName: String,
    val source: String? = null,
    val notes: String? = null,
    val sortOrder: Int = 0,
)

data class CharacterResource(
    val id: Uuid,
    val name: String,
    val currentValue: Int,
    val maxValue: Int? = null,
    val recovery: String? = null,
    val source: String? = null,
    val notes: String? = null,
    val pinned: Boolean = true,
    val sortOrder: Int = 0,
)

data class CharacterClassOption(
    val id: Uuid,
    val linkedClassId: Uuid? = null,
    val kind: CharacterClassOptionKind,
    val name: String,
    val source: String? = null,
    val costText: String? = null,
    val effectSummary: String = "",
    val notes: String? = null,
    val active: Boolean = true,
    val pinned: Boolean = false,
    val sortOrder: Int = 0,
)

data class CharacterForm(
    val id: Uuid,
    val name: String,
    val source: String? = null,
    val challengeRatingText: String? = null,
    val armorClass: Int? = null,
    val hitPoints: Int? = null,
    val movement: String? = null,
    val senses: String? = null,
    val actionSummary: String = "",
    val notes: String? = null,
    val pinned: Boolean = false,
    val sortOrder: Int = 0,
)

data class CharacterCompanion(
    val id: Uuid,
    val linkedClassId: Uuid? = null,
    val name: String,
    val kind: String = "",
    val source: String? = null,
    val armorClass: Int? = null,
    val maxHp: Int? = null,
    val currentHp: Int? = null,
    val tempHp: Int = 0,
    val speed: String? = null,
    val abilitySummary: String? = null,
    val sensesProficiencies: String? = null,
    val traitsActions: String = "",
    val notes: String? = null,
    val active: Boolean = true,
    val sortOrder: Int = 0,
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
    val spellcasterEnabled: Boolean = false,
    val background: CharacterBackground = CharacterBackground(),
    val traits: List<CharacterTrait> = emptyList(),
    val spellcastingSources: List<CharacterSpellcastingSource> = emptyList(),
    val spells: List<CharacterSpell> = emptyList(),
    val generalNotes: String = "",
    val noteCards: List<CharacterNote> = emptyList(),
    val inspiration: Boolean = false,
    val deathSaveSuccesses: Int = 0,
    val deathSaveFailures: Int = 0,
    val proficiencies: List<CharacterProficiency> = emptyList(),
    val weaponMasteries: List<CharacterWeaponMastery> = emptyList(),
    val resources: List<CharacterResource> = emptyList(),
    val classOptions: List<CharacterClassOption> = emptyList(),
    val forms: List<CharacterForm> = emptyList(),
    val companions: List<CharacterCompanion> = emptyList(),
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

    val hasUnsavedLiveStateCandidates: Boolean
        get() = currentHp != maxHp || tempHp != 0 || deathSaveSuccesses != 0 || deathSaveFailures != 0

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
