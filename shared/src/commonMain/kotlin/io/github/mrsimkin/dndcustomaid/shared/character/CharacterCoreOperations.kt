package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

/** Pure, permissive helpers for Batch E UI projections and quick operations. */
val CharacterSheet.passiveInsight: Int
    get() = 10 + skillTotal(SkillKey.INSIGHT)

val CharacterSheet.passiveInvestigation: Int
    get() = 10 + skillTotal(SkillKey.INVESTIGATION)

fun CharacterSheet.customSkillTotal(skill: CharacterCustomSkill): Int {
    val proficiencyContribution = when (skill.training) {
        SkillTraining.NONE -> 0
        SkillTraining.PROFICIENT -> finalProficiencyBonus
        SkillTraining.EXPERTISE -> finalProficiencyBonus * 2
    }
    return abilityModifier(skill.ability) + proficiencyContribution + skill.adjustment
}

/**
 * Apply an exact current/max HP correction while preserving the canonical invariant that current HP
 * can never exceed maximum HP. This is an administrative/set operation, not damage or healing.
 */
fun setCharacterHitPoints(
    sheet: CharacterSheet,
    currentHp: Int,
    maxHp: Int,
): CharacterSheet {
    val normalizedMax = maxHp.coerceAtLeast(0)
    return sheet.copy(
        maxHp = normalizedMax,
        currentHp = currentHp.coerceIn(0, normalizedMax),
    )
}

/** Exact current-HP correction against the character's existing maximum. */
fun setCharacterCurrentHp(sheet: CharacterSheet, currentHp: Int): CharacterSheet =
    setCharacterHitPoints(sheet = sheet, currentHp = currentHp, maxHp = sheet.maxHp)

/**
 * Exact maximum-HP correction. Increasing the maximum does not heal; reducing it clamps current HP.
 */
fun setCharacterMaxHp(sheet: CharacterSheet, maxHp: Int): CharacterSheet =
    setCharacterHitPoints(sheet = sheet, currentHp = sheet.currentHp, maxHp = maxHp)

fun applyCharacterDamage(sheet: CharacterSheet, amount: Int): CharacterSheet {
    require(amount >= 0) { "Damage amount must not be negative." }
    if (amount == 0) return sheet

    val absorbedByTemp = minOf(sheet.tempHp.coerceAtLeast(0), amount)
    val remainingDamage = amount - absorbedByTemp
    return sheet.copy(
        tempHp = (sheet.tempHp - absorbedByTemp).coerceAtLeast(0),
        currentHp = (sheet.currentHp - remainingDamage).coerceAtLeast(0),
    )
}

fun applyCharacterHealing(sheet: CharacterSheet, amount: Int): CharacterSheet {
    require(amount >= 0) { "Healing amount must not be negative." }
    if (amount == 0) return sheet
    return sheet.copy(currentHp = (sheet.currentHp + amount).coerceAtMost(sheet.maxHp.coerceAtLeast(0)))
}

fun setCharacterTemporaryHp(sheet: CharacterSheet, amount: Int): CharacterSheet {
    require(amount >= 0) { "Temporary HP must not be negative." }
    return sheet.copy(tempHp = amount)
}

/** Which canonical HP projection(s) actually changed across one operation. */
enum class CharacterHpChangeImpact {
    NONE,
    HIT_POINTS,
    TEMPORARY_HP,
    BOTH,
}

fun characterHpChangeImpact(
    beforeCurrentHp: Int,
    beforeMaxHp: Int,
    beforeTempHp: Int,
    afterCurrentHp: Int,
    afterMaxHp: Int,
    afterTempHp: Int,
): CharacterHpChangeImpact {
    val hitPointsChanged = beforeCurrentHp != afterCurrentHp || beforeMaxHp != afterMaxHp
    val temporaryHpChanged = beforeTempHp != afterTempHp
    return when {
        hitPointsChanged && temporaryHpChanged -> CharacterHpChangeImpact.BOTH
        hitPointsChanged -> CharacterHpChangeImpact.HIT_POINTS
        temporaryHpChanged -> CharacterHpChangeImpact.TEMPORARY_HP
        else -> CharacterHpChangeImpact.NONE
    }
}

fun characterHpChangeImpact(before: CharacterSheet, after: CharacterSheet): CharacterHpChangeImpact =
    characterHpChangeImpact(
        beforeCurrentHp = before.currentHp,
        beforeMaxHp = before.maxHp,
        beforeTempHp = before.tempHp,
        afterCurrentHp = after.currentHp,
        afterMaxHp = after.maxHp,
        afterTempHp = after.tempHp,
    )

fun CharacterClosureState.hasQuickAccess(kind: CharacterQuickAccessKind, targetId: Uuid): Boolean =
    quickAccess.any { it.kind == kind && it.targetId == targetId }

fun CharacterClosureState.withQuickAccess(
    kind: CharacterQuickAccessKind,
    targetId: Uuid,
    enabled: Boolean,
): CharacterClosureState {
    val remaining = quickAccess.filterNot { it.kind == kind && it.targetId == targetId }
    val updated = if (enabled) {
        remaining + CharacterQuickAccessRef(kind, targetId, sortOrder = remaining.size)
    } else {
        remaining
    }
    return copy(quickAccess = updated.mapIndexed { index, item -> item.copy(sortOrder = index) })
}

data class CharacterD20Roll(
    val dieResult: Int,
    val modifier: Int,
) {
    init {
        require(dieResult in 1..20) { "A d20 result must be between 1 and 20." }
    }

    val total: Int
        get() = dieResult + modifier
}

fun characterD20Roll(dieResult: Int, modifier: Int = 0): CharacterD20Roll =
    CharacterD20Roll(dieResult = dieResult, modifier = modifier)

fun suggestedHitDieSides(classLevel: CharacterClassLevel): Int? {
    val catalogName = CharacterClassCatalog.byKey(classLevel.catalogKey)?.nameEs
    return suggestedHitDieSidesForClassName(catalogName ?: classLevel.name)
}

fun suggestedHitDieSidesForClassName(rawName: String): Int? = when (normalizeCharacterSearchText(rawName)) {
    "artifice", "artificer" -> 8
    "barbaro", "barbarian" -> 12
    "bardo", "bard" -> 8
    "brujo", "warlock" -> 8
    "clerigo", "cleric" -> 8
    "druida", "druid" -> 8
    "explorador", "ranger" -> 10
    "guerrero", "fighter" -> 10
    "hechicero", "sorcerer" -> 6
    "mago", "wizard" -> 6
    "monje", "monk" -> 8
    "paladin" -> 10
    "picaro", "rogue" -> 8
    else -> null
}

data class CharacterClassIdentityPresentation(
    val primary: String,
    val secondary: String?,
)

fun presentCharacterClassIdentity(classLevel: CharacterClassLevel): CharacterClassIdentityPresentation {
    val subclass = classLevel.subclassName?.trim()?.takeIf { it.isNotEmpty() }
    val primary = buildString {
        append(classLevel.name.trim())
        if (subclass != null) append(" · $subclass")
        append(" · Nv. ${classLevel.level}")
    }
    val source = classLevel.subclassSource?.takeIf { !it.isNullOrBlank() }
        ?: classLevel.source?.takeIf { !it.isNullOrBlank() }
    val rules = when (classLevel.subclassRulesFamily.takeIf { subclass != null } ?: classLevel.rulesFamily) {
        CharacterRulesFamily.DND_5E -> "D&D 5e"
        CharacterRulesFamily.DND_5_5E -> "D&D 5.5e"
        CharacterRulesFamily.CUSTOM -> "Custom"
        CharacterRulesFamily.UNSPECIFIED -> null
    }
    val secondary = listOfNotNull(rules, source).joinToString(" · ").takeIf { it.isNotEmpty() }
    return CharacterClassIdentityPresentation(primary = primary, secondary = secondary)
}
