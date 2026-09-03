package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

enum class CharacterDefenseType {
    RESISTANCE,
    IMMUNITY,
    VULNERABILITY,
}

enum class CharacterMovementType {
    FLY,
    SWIM,
    CLIMB,
    BURROW,
    OTHER,
}

enum class CharacterProgressMode {
    MILESTONE,
    EXPERIENCE,
}

enum class CharacterRecoveryCadence {
    NONE,
    SHORT_REST,
    LONG_REST,
    SHORT_OR_LONG_REST,
    MANUAL,
}

enum class CharacterRecoveryAmountMode {
    NONE,
    TO_MAX,
    FIXED,
}

enum class CharacterConsumableKind {
    NONE,
    CONSUMABLE,
    AMMUNITION,
}

enum class CharacterModuleOverrideMode {
    AUTO,
    FORCE_SHOW,
    FORCE_HIDE,
}

enum class CharacterQuickAccessKind {
    COMBAT_ENTRY,
    TRAIT,
    SPELL,
    RESOURCE,
    CLASS_OPTION,
    FORM,
    COMPANION,
    CUSTOM_SKILL,
    TEMPORARY_EFFECT,
    OTHER,
}

data class CharacterCondition(
    val id: Uuid,
    val name: String,
    val source: String? = null,
    val notes: String? = null,
    val sortOrder: Int = 0,
)

data class CharacterDefense(
    val id: Uuid,
    val type: CharacterDefenseType,
    val name: String,
    val source: String? = null,
    val notes: String? = null,
    val sortOrder: Int = 0,
)

data class CharacterMovement(
    val id: Uuid,
    val type: CharacterMovementType,
    val name: String,
    val speedFeet: Int? = null,
    val notes: String? = null,
    val sortOrder: Int = 0,
)

data class CharacterSense(
    val id: Uuid,
    val name: String,
    val rangeFeet: Int? = null,
    val notes: String? = null,
    val sortOrder: Int = 0,
)

data class CharacterConcentration(
    val spellId: Uuid? = null,
    val name: String,
    val notes: String? = null,
)

data class CharacterResourceRecovery(
    val resourceId: Uuid,
    val cadence: CharacterRecoveryCadence = CharacterRecoveryCadence.NONE,
    val amountMode: CharacterRecoveryAmountMode = CharacterRecoveryAmountMode.NONE,
    val fixedAmount: Int? = null,
    val notes: String? = null,
)

data class CharacterInventoryUsage(
    val itemId: Uuid,
    val kind: CharacterConsumableKind = CharacterConsumableKind.NONE,
    val quickUseAmount: Int = 1,
)

data class CharacterReconciliationCheckpoint(
    val id: Uuid,
    val createdAtEpochSeconds: Long,
    val characterUpdatedAtEpochSeconds: Long,
    val label: String? = null,
    val notes: String? = null,
)

data class CharacterCustomSkill(
    val id: Uuid,
    val name: String,
    val ability: CharacterAbility,
    val training: SkillTraining = SkillTraining.NONE,
    val adjustment: Int = 0,
    val source: String? = null,
    val notes: String? = null,
    val sortOrder: Int = 0,
)

data class CharacterTemporaryEffect(
    val id: Uuid,
    val name: String,
    val summary: String = "",
    val durationText: String? = null,
    val source: String? = null,
    val notes: String? = null,
    val active: Boolean = true,
    val sortOrder: Int = 0,
)

data class CharacterModuleOverride(
    val module: CharacterModuleKind,
    val mode: CharacterModuleOverrideMode,
)

data class CharacterQuickAccessRef(
    val kind: CharacterQuickAccessKind,
    val targetId: Uuid,
    val sortOrder: Int = 0,
)

data class CharacterClosureState(
    val exhaustionLevel: Int = 0,
    val concentration: CharacterConcentration? = null,
    val portraitRef: String? = null,
    val tokenRef: String? = null,
    val progressMode: CharacterProgressMode = CharacterProgressMode.MILESTONE,
    val experiencePoints: Int = 0,
    val milestoneProgress: String = "",
    val tableModeEnabled: Boolean = false,
    val hapticsEnabled: Boolean = true,
    val conditions: List<CharacterCondition> = emptyList(),
    val defenses: List<CharacterDefense> = emptyList(),
    val movements: List<CharacterMovement> = emptyList(),
    val senses: List<CharacterSense> = emptyList(),
    val resourceRecovery: List<CharacterResourceRecovery> = emptyList(),
    val inventoryUsage: List<CharacterInventoryUsage> = emptyList(),
    val reconciliationCheckpoints: List<CharacterReconciliationCheckpoint> = emptyList(),
    val customSkills: List<CharacterCustomSkill> = emptyList(),
    val temporaryEffects: List<CharacterTemporaryEffect> = emptyList(),
    val moduleOverrides: List<CharacterModuleOverride> = emptyList(),
    val quickAccess: List<CharacterQuickAccessRef> = emptyList(),
)
