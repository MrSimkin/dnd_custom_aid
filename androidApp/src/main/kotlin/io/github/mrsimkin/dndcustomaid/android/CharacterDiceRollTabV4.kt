package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbilityReference
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.abilityReference
import io.github.mrsimkin.dndcustomaid.shared.character.characterD20Roll
import io.github.mrsimkin.dndcustomaid.shared.character.customSavingThrowTotal
import io.github.mrsimkin.dndcustomaid.shared.character.customSkillTotal
import kotlin.random.Random

private data class CharacterDiceTargetV4(
    val key: String,
    val group: String,
    val label: String,
    val context: String,
    val modifier: Int,
)

@Composable
internal fun CharacterDiceRollTabV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    combatEntries: List<CharacterCombatEntry>,
    successorState: CharacterSuccessorState = CharacterSuccessorState(),
) {
    val targets = characterDiceTargetsV4(sheet, closureState, successorState, combatEntries)
    var selectedKey by rememberSaveable(sheet.id.toString()) { mutableStateOf<String?>(null) }
    var dieResult by rememberSaveable(sheet.id.toString(), "dice-result") { mutableStateOf<Int?>(null) }
    val selected = targets.firstOrNull { it.key == selectedKey } ?: targets.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        CharacterDiceFixedPanelV4(
            target = selected,
            dieResult = dieResult,
            onRoll = { dieResult = Random.nextInt(1, 21) },
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(start = 6.dp, end = 6.dp, top = 0.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            characterDiceGroupOrderV4.forEach { group ->
                val groupTargets = targets.filter { it.group == group }
                if (groupTargets.isNotEmpty()) {
                    item(key = "dice-header-$group") {
                        Text(
                            group,
                            modifier = Modifier.padding(start = 3.dp, top = 6.dp, bottom = 1.dp),
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                    items(groupTargets, key = { it.key }) { target ->
                        CharacterDiceTargetRowV4(
                            target = target,
                            selected = selected?.key == target.key,
                            onSelect = {
                                selectedKey = target.key
                                dieResult = null
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterDiceFixedPanelV4(
    target: CharacterDiceTargetV4?,
    dieResult: Int?,
    onRoll: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 6.dp, end = 6.dp, top = 5.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 9.dp, vertical = 7.dp),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Text("Tirada de dados", style = MaterialTheme.typography.titleMedium)
            if (target == null) {
                Text("No hay tiradas disponibles para esta ficha.", style = MaterialTheme.typography.bodySmall)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(target.label, style = MaterialTheme.typography.titleSmall)
                        Text(
                            "${target.context} · modificador ${formatDiceModifierV4(target.modifier)}",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    Button(onClick = onRoll) {
                        Text(if (dieResult == null) "Tirar d20" else "Otra tirada")
                    }
                }

                dieResult?.let { raw ->
                    val roll = characterD20Roll(raw, target.modifier)
                    Text(
                        "d20 ${roll.dieResult} ${formatDiceOperationV4(roll.modifier)} = ${roll.total}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    when (roll.dieResult) {
                        20 -> Text("Resultado natural: 20", style = MaterialTheme.typography.labelSmall)
                        1 -> Text("Resultado natural: 1", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Text(
                    "La app muestra la tirada y el modificador seleccionado; no interpreta ventaja/desventaja, críticos, daño, legalidad ni otros efectos de reglas.",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun CharacterDiceTargetRowV4(
    target: CharacterDiceTargetV4,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(if (selected) 2.dp else 1.dp, borderColor),
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(target.label, style = MaterialTheme.typography.bodyMedium)
                Text(target.context, style = MaterialTheme.typography.labelSmall)
            }
            Text(formatDiceModifierV4(target.modifier), style = MaterialTheme.typography.titleSmall)
        }
    }
}

private val characterDiceGroupOrderV4 = listOf(
    "Características",
    "Salvaciones",
    "Habilidades",
    "Habilidades personalizadas",
    "Ataques",
)

private fun characterDiceTargetsV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    successorState: CharacterSuccessorState,
    combatEntries: List<CharacterCombatEntry>,
): List<CharacterDiceTargetV4> = buildList {
    CharacterAbility.entries.forEach { ability ->
        add(
            CharacterDiceTargetV4(
                key = "ability-${ability.name}",
                group = "Características",
                label = abilityLabelDiceV4(ability),
                context = "Prueba de característica · ${abilityAbbreviationDiceV4(ability)}",
                modifier = sheet.abilityModifier(ability),
            ),
        )
    }

    successorState.customAttributes.sortedBy { it.sortOrder }.forEach { attribute ->
        add(
            CharacterDiceTargetV4(
                key = "custom-ability-${attribute.id}",
                group = "Características",
                label = attribute.name,
                context = "Prueba de característica · ${attribute.abbreviation}",
                modifier = attribute.modifier,
            ),
        )
    }

    CharacterAbility.entries.forEach { ability ->
        add(
            CharacterDiceTargetV4(
                key = "save-${ability.name}",
                group = "Salvaciones",
                label = "Salvación de ${abilityLabelDiceV4(ability)}",
                context = "Tirada de salvación · ${abilityAbbreviationDiceV4(ability)}",
                modifier = sheet.savingThrowTotal(ability),
            ),
        )
    }

    successorState.customAttributes.sortedBy { it.sortOrder }.forEach { attribute ->
        sheet.customSavingThrowTotal(attribute)?.let { modifier ->
            add(
                CharacterDiceTargetV4(
                    key = "custom-save-${attribute.id}",
                    group = "Salvaciones",
                    label = "Salvación de ${attribute.name}",
                    context = "Tirada de salvación · ${attribute.abbreviation}",
                    modifier = modifier,
                ),
            )
        }
    }

    SkillKey.entries.forEach { skill ->
        add(
            CharacterDiceTargetV4(
                key = "skill-${skill.name}",
                group = "Habilidades",
                label = skillLabelDiceV4(skill),
                context = "Habilidad · ${abilityAbbreviationDiceV4(skill.ability)}",
                modifier = sheet.skillTotal(skill),
            ),
        )
    }

    closureState.customSkills.sortedBy { it.sortOrder }.forEach { skill ->
        val modifier = sheet.customSkillTotal(skill, successorState) ?: return@forEach
        val abilityReference = skill.abilityReference(successorState)
        add(
            CharacterDiceTargetV4(
                key = "custom-${skill.id}",
                group = "Habilidades personalizadas",
                label = skill.name,
                context = listOfNotNull(
                    "Habilidad personalizada",
                    abilityAbbreviationDiceV4(abilityReference, successorState),
                    skill.source?.trim()?.takeIf { it.isNotEmpty() },
                ).joinToString(" · "),
                modifier = modifier,
            ),
        )
    }

    combatEntries.filter { it.attackModifier != null }.sortedBy { it.sortOrder }.forEach { entry ->
        add(
            CharacterDiceTargetV4(
                key = "attack-${entry.id}",
                group = "Ataques",
                label = entry.name,
                context = listOfNotNull(
                    combatTypeLabelDiceV4(entry.type),
                    entry.damageEffect.trim().takeIf { it.isNotEmpty() },
                    entry.rangeText?.trim()?.takeIf { it.isNotEmpty() }?.let { "Alcance $it" },
                ).joinToString(" · "),
                modifier = requireNotNull(entry.attackModifier),
            ),
        )
    }
}

private fun abilityLabelDiceV4(ability: CharacterAbility): String = when (ability) {
    CharacterAbility.STRENGTH -> "Fuerza"
    CharacterAbility.DEXTERITY -> "Destreza"
    CharacterAbility.CONSTITUTION -> "Constitución"
    CharacterAbility.INTELLIGENCE -> "Inteligencia"
    CharacterAbility.WISDOM -> "Sabiduría"
    CharacterAbility.CHARISMA -> "Carisma"
}

private fun abilityAbbreviationDiceV4(ability: CharacterAbility): String = when (ability) {
    CharacterAbility.STRENGTH -> "FUE"
    CharacterAbility.DEXTERITY -> "DES"
    CharacterAbility.CONSTITUTION -> "CON"
    CharacterAbility.INTELLIGENCE -> "INT"
    CharacterAbility.WISDOM -> "SAB"
    CharacterAbility.CHARISMA -> "CAR"
}

private fun abilityAbbreviationDiceV4(
    reference: CharacterAbilityReference,
    successorState: CharacterSuccessorState,
): String? = when {
    reference.builtIn != null -> abilityAbbreviationDiceV4(reference.builtIn)
    reference.customAttributeId != null -> successorState.customAttributes
        .firstOrNull { it.id == reference.customAttributeId }
        ?.abbreviation
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
    else -> null
}

private fun skillLabelDiceV4(skill: SkillKey): String = when (skill) {
    SkillKey.ACROBATICS -> "Acrobacias"
    SkillKey.ANIMAL_HANDLING -> "Trato con animales"
    SkillKey.ARCANA -> "Arcanos"
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

private fun combatTypeLabelDiceV4(type: CharacterCombatEntryType): String = when (type) {
    CharacterCombatEntryType.ATTACK -> "Ataque"
    CharacterCombatEntryType.ACTION -> "Acción"
    CharacterCombatEntryType.BONUS_ACTION -> "Acción adicional"
    CharacterCombatEntryType.REACTION -> "Reacción"
    CharacterCombatEntryType.OTHER -> "Otra acción"
}

private fun formatDiceModifierV4(value: Int): String = if (value >= 0) "+$value" else value.toString()

private fun formatDiceOperationV4(value: Int): String =
    if (value >= 0) "+ $value" else "− ${-value}"
