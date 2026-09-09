package io.github.mrsimkin.dndcustomaid.shared.character

import kotlin.uuid.Uuid

enum class CharacterD20Mode {
    NORMAL,
    ADVANTAGE,
    DISADVANTAGE,
}

data class CharacterResolvedD20Roll(
    val mode: CharacterD20Mode,
    val firstDie: Int,
    val secondDie: Int?,
    val chosenDie: Int,
    val modifier: Int,
) {
    val total: Int
        get() = chosenDie + modifier
}

fun resolveCharacterD20Roll(
    firstDie: Int,
    secondDie: Int? = null,
    mode: CharacterD20Mode = CharacterD20Mode.NORMAL,
    modifier: Int = 0,
): CharacterResolvedD20Roll {
    require(firstDie in 1..20) { "A d20 result must be between 1 and 20." }
    if (secondDie != null) require(secondDie in 1..20) { "A d20 result must be between 1 and 20." }
    val chosen = when (mode) {
        CharacterD20Mode.NORMAL -> firstDie
        CharacterD20Mode.ADVANTAGE -> maxOf(firstDie, requireNotNull(secondDie) { "Advantage requires two d20 results." })
        CharacterD20Mode.DISADVANTAGE -> minOf(firstDie, requireNotNull(secondDie) { "Disadvantage requires two d20 results." })
    }
    return CharacterResolvedD20Roll(
        mode = mode,
        firstDie = firstDie,
        secondDie = if (mode == CharacterD20Mode.NORMAL) null else secondDie,
        chosenDie = chosen,
        modifier = modifier,
    )
}

enum class CharacterDiceTargetCategory {
    ABILITY,
    SAVE,
    SKILL,
    ATTACK,
    SPELL_ATTACK,
    CUSTOM,
}

data class CharacterDiceTarget(
    val key: String,
    val category: CharacterDiceTargetCategory,
    val label: String,
    val context: String,
    val modifier: Int,
)

fun characterDiceTargets(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    successorState: CharacterSuccessorState,
    combatEntries: List<CharacterCombatEntry> = sheet.combatEntries,
): List<CharacterDiceTarget> = buildList {
    CharacterAbility.entries.forEach { ability ->
        add(
            CharacterDiceTarget(
                key = "ability-${ability.name}",
                category = CharacterDiceTargetCategory.ABILITY,
                label = characterAbilitySpanishLabel(ability),
                context = "Prueba de característica · ${characterAbilityAbbreviation(ability)}",
                modifier = sheet.abilityModifier(ability),
            ),
        )
    }

    successorState.customAttributes.sortedBy { it.sortOrder }.forEach { attribute ->
        add(
            CharacterDiceTarget(
                key = "custom-ability-${attribute.id}",
                category = CharacterDiceTargetCategory.ABILITY,
                label = attribute.name,
                context = "Prueba de característica · ${attribute.abbreviation}",
                modifier = attribute.modifier,
            ),
        )
    }

    CharacterAbility.entries.forEach { ability ->
        add(
            CharacterDiceTarget(
                key = "save-${ability.name}",
                category = CharacterDiceTargetCategory.SAVE,
                label = "Salvación de ${characterAbilitySpanishLabel(ability)}",
                context = "Tirada de salvación · ${characterAbilityAbbreviation(ability)}",
                modifier = sheet.savingThrowTotal(ability),
            ),
        )
    }

    successorState.customAttributes.sortedBy { it.sortOrder }.forEach { attribute ->
        sheet.customSavingThrowTotal(attribute)?.let { modifier ->
            add(
                CharacterDiceTarget(
                    key = "custom-save-${attribute.id}",
                    category = CharacterDiceTargetCategory.SAVE,
                    label = "Salvación de ${attribute.name}",
                    context = "Tirada de salvación · ${attribute.abbreviation}",
                    modifier = modifier,
                ),
            )
        }
    }

    val skillRows = presentCharacterSkills(
        builtInSkills = sheet.skills,
        customSkills = closureState.customSkills,
        successorState = successorState,
    )
    skillRows.forEach { row ->
        val modifier = row.builtInKey?.let(sheet::skillTotal)
            ?: row.customSkillId
                ?.let { id -> closureState.customSkills.firstOrNull { it.id == id } }
                ?.let { sheet.customSkillTotal(it, successorState) }
            ?: return@forEach
        add(
            CharacterDiceTarget(
                key = row.builtInKey?.let { "skill-${it.name}" } ?: "custom-skill-${row.customSkillId}",
                category = CharacterDiceTargetCategory.SKILL,
                label = row.label,
                context = "Habilidad · ${characterAbilityReferenceAbbreviation(row.ability, successorState)}",
                modifier = modifier,
            ),
        )
    }

    combatEntries
        .filter { it.attackModifier != null }
        .sortedBy { it.sortOrder }
        .forEach { entry ->
            add(
                CharacterDiceTarget(
                    key = "attack-${entry.id}",
                    category = CharacterDiceTargetCategory.ATTACK,
                    label = entry.name,
                    context = characterCombatEntryTypeSpanishLabel(entry.type),
                    modifier = requireNotNull(entry.attackModifier),
                ),
            )
        }

    sheet.spellcastingSources.sortedBy { it.sortOrder }.forEach { source ->
        val profile = successorState.spellcastingProfiles.firstOrNull { it.sourceId == source.id }
            ?: return@forEach
        val modifier = sheet.spellAttackModifier(profile, successorState) ?: return@forEach
        add(
            CharacterDiceTarget(
                key = "spell-attack-${source.id}",
                category = CharacterDiceTargetCategory.SPELL_ATTACK,
                label = source.name,
                context = "Ataque mágico · ${characterAbilityReferenceAbbreviation(profile.ability, successorState)}",
                modifier = modifier,
            ),
        )
    }

    add(
        CharacterDiceTarget(
            key = "custom-roll",
            category = CharacterDiceTargetCategory.CUSTOM,
            label = "Tirada personalizada",
            context = "Modificador manual",
            modifier = 0,
        ),
    )
}.sortedWith(
    compareBy<CharacterDiceTarget> { it.category.ordinal }
        .thenBy { normalizeCharacterSearchText(it.label) }
        .thenBy { it.key },
)

fun characterAbilitySpanishLabel(ability: CharacterAbility): String = when (ability) {
    CharacterAbility.STRENGTH -> "Fuerza"
    CharacterAbility.DEXTERITY -> "Destreza"
    CharacterAbility.CONSTITUTION -> "Constitución"
    CharacterAbility.INTELLIGENCE -> "Inteligencia"
    CharacterAbility.WISDOM -> "Sabiduría"
    CharacterAbility.CHARISMA -> "Carisma"
}

fun characterAbilityAbbreviation(ability: CharacterAbility): String = when (ability) {
    CharacterAbility.STRENGTH -> "FUE"
    CharacterAbility.DEXTERITY -> "DES"
    CharacterAbility.CONSTITUTION -> "CON"
    CharacterAbility.INTELLIGENCE -> "INT"
    CharacterAbility.WISDOM -> "SAB"
    CharacterAbility.CHARISMA -> "CAR"
}

fun characterCombatEntryTypeSpanishLabel(type: CharacterCombatEntryType): String = when (type) {
    CharacterCombatEntryType.ATTACK -> "Ataque"
    CharacterCombatEntryType.ACTION -> "Acción"
    CharacterCombatEntryType.BONUS_ACTION -> "Acción adicional"
    CharacterCombatEntryType.REACTION -> "Reacción"
    CharacterCombatEntryType.OTHER -> "Otra acción"
}

data class CharacterDiceExpression(
    val count: Int,
    val sides: Int,
)

fun parseCharacterDiceExpression(raw: String): CharacterDiceExpression? {
    val match = Regex("^([0-9]*)[dD]([0-9]+)$").matchEntire(raw.trim()) ?: return null
    val count = match.groupValues[1].takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 1
    val sides = match.groupValues[2].toIntOrNull() ?: return null
    if (count !in 1..100 || sides !in 2..1000) return null
    return CharacterDiceExpression(count = count, sides = sides)
}

data class CharacterDamageRolledComponent(
    val component: CharacterDamageComponent,
    val diceResults: List<Int> = emptyList(),
    val numericValue: Int? = null,
)

data class CharacterDamageRollResult(
    val components: List<CharacterDamageRolledComponent>,
) {
    val numericTotal: Int
        get() = components.sumOf { it.numericValue ?: 0 }

    val hasNumericDamage: Boolean
        get() = components.any { it.numericValue != null }
}

fun resolveCharacterDamageRoll(
    components: List<CharacterDamageComponent>,
    dieRoller: (sides: Int) -> Int,
): CharacterDamageRollResult = CharacterDamageRollResult(
    components = components.map { component ->
        when (component.kind) {
            CharacterDamageComponentKind.DICE -> {
                val expression = parseCharacterDiceExpression(component.expression)
                if (expression == null) {
                    CharacterDamageRolledComponent(component)
                } else {
                    val results = List(expression.count) {
                        dieRoller(expression.sides).also { result ->
                            require(result in 1..expression.sides) {
                                "Die roller returned $result for d${expression.sides}."
                            }
                        }
                    }
                    CharacterDamageRolledComponent(
                        component = component,
                        diceResults = results,
                        numericValue = results.sum(),
                    )
                }
            }
            CharacterDamageComponentKind.FLAT -> CharacterDamageRolledComponent(
                component = component,
                numericValue = component.expression.trim().toIntOrNull(),
            )
            CharacterDamageComponentKind.TEXT -> CharacterDamageRolledComponent(component)
        }
    },
)

fun characterDamageSummary(components: List<CharacterDamageComponent>): String = components
    .mapNotNull(::characterDamageComponentSummary)
    .joinToString(" · ")

fun characterDamageComponentSummary(component: CharacterDamageComponent): String? {
    val expression = component.expression.trim().takeIf { it.isNotEmpty() } ?: return null
    val value = when (component.kind) {
        CharacterDamageComponentKind.DICE -> expression
        CharacterDamageComponentKind.FLAT -> expression.toIntOrNull()?.let { if (it >= 0) "+$it" else it.toString() } ?: expression
        CharacterDamageComponentKind.TEXT -> expression
    }
    val type = component.typeText?.trim()?.takeIf { it.isNotEmpty() }
    return if (type == null || component.kind == CharacterDamageComponentKind.TEXT) value else "$value $type"
}

fun CharacterSuccessorState.combatDamageComponents(
    entry: CharacterCombatEntry,
): List<CharacterDamageComponent> {
    val profile = combatDamage.firstOrNull { it.combatEntryId == entry.id }
    if (profile != null) return profile.components
    return entry.damageEffect.trim().takeIf { it.isNotEmpty() }
        ?.let { listOf(CharacterDamageComponent(CharacterDamageComponentKind.TEXT, it)) }
        .orEmpty()
}

fun CharacterSuccessorState.withCombatDamageComponents(
    combatEntryId: Uuid,
    components: List<CharacterDamageComponent>,
): CharacterSuccessorState {
    val normalized = components.mapNotNull { component ->
        val expression = component.expression.trim()
        if (expression.isEmpty()) null else component.copy(
            expression = expression,
            typeText = component.typeText?.trim()?.takeIf { it.isNotEmpty() },
        )
    }
    return copy(
        combatDamage = combatDamage.filterNot { it.combatEntryId == combatEntryId } +
            CharacterCombatDamageProfile(combatEntryId = combatEntryId, components = normalized),
    )
}
