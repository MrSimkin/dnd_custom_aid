package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterD20Mode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageComponentKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDiceTarget
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDiceTargetCategory
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResolvedD20Roll
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.character.characterDamageSummary
import io.github.mrsimkin.dndcustomaid.shared.character.characterDiceTargets
import io.github.mrsimkin.dndcustomaid.shared.character.combatDamageComponents
import io.github.mrsimkin.dndcustomaid.shared.character.parseCharacterDiceExpression
import io.github.mrsimkin.dndcustomaid.shared.character.resolveCharacterD20Roll
import io.github.mrsimkin.dndcustomaid.shared.character.resolveCharacterDamageRoll
import kotlin.random.Random

@Composable
internal fun CharacterDiceRollSuccessorTabV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    combatEntries: List<CharacterCombatEntry>,
    successorState: CharacterSuccessorState,
) {
    val targets = characterDiceTargets(sheet, closureState, successorState, combatEntries)
    val availableCategories = CharacterDiceTargetCategory.entries.filter { category ->
        targets.any { it.category == category }
    }
    var modeName by rememberSaveable(sheet.id.toString(), "dice-mode") {
        mutableStateOf(CharacterD20Mode.NORMAL.name)
    }
    var categoryName by rememberSaveable(sheet.id.toString(), "dice-category") {
        mutableStateOf(
            availableCategories.firstOrNull { it == CharacterDiceTargetCategory.SKILL }?.name
                ?: availableCategories.firstOrNull()?.name
                ?: CharacterDiceTargetCategory.CUSTOM.name,
        )
    }
    var selectedKey by rememberSaveable(sheet.id.toString(), "dice-target") { mutableStateOf<String?>(null) }
    var customModifierText by rememberSaveable(sheet.id.toString(), "dice-custom-modifier") { mutableStateOf("0") }
    var firstDie by rememberSaveable(sheet.id.toString(), "dice-first") { mutableStateOf<Int?>(null) }
    var secondDie by rememberSaveable(sheet.id.toString(), "dice-second") { mutableStateOf<Int?>(null) }
    var damageResultText by rememberSaveable(sheet.id.toString(), "damage-result") { mutableStateOf<String?>(null) }

    val mode = runCatching { CharacterD20Mode.valueOf(modeName) }.getOrDefault(CharacterD20Mode.NORMAL)
    val requestedCategory = runCatching { CharacterDiceTargetCategory.valueOf(categoryName) }.getOrNull()
    val category = requestedCategory?.takeIf { it in availableCategories }
        ?: availableCategories.firstOrNull()
        ?: CharacterDiceTargetCategory.CUSTOM
    val categoryTargets = targets.filter { it.category == category }
    val selected = categoryTargets.firstOrNull { it.key == selectedKey } ?: categoryTargets.firstOrNull()
    val customModifier = customModifierText.trim().toIntOrNull()
    val effectiveModifier = if (selected?.category == CharacterDiceTargetCategory.CUSTOM) customModifier else selected?.modifier
    val resolvedRoll: CharacterResolvedD20Roll? = if (firstDie != null && effectiveModifier != null) {
        runCatching {
            resolveCharacterD20Roll(
                firstDie = requireNotNull(firstDie),
                secondDie = secondDie,
                mode = mode,
                modifier = effectiveModifier,
            )
        }.getOrNull()
    } else {
        null
    }
    val attackEntry = selected?.takeIf { it.category == CharacterDiceTargetCategory.ATTACK }?.let { target ->
        combatEntries.firstOrNull { "attack-${it.id}" == target.key }
    }
    val attackDamage = attackEntry?.let { successorState.combatDamageComponents(it) }.orEmpty()
    val canRollDamage = attackDamage.any { component ->
        when (component.kind) {
            CharacterDamageComponentKind.DICE -> parseCharacterDiceExpression(component.expression) != null
            CharacterDamageComponentKind.FLAT -> component.expression.trim().toIntOrNull() != null
            CharacterDamageComponentKind.TEXT -> false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().navigationBarsPadding().padding(appSpacingV4(6.dp)),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(
                    horizontal = appSpacingV4(8.dp),
                    vertical = appSpacingV4(7.dp),
                ),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
            ) {
                Text("Tirada", style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                ) {
                    CharacterD20Mode.entries.forEach { option ->
                        if (option == mode) {
                            Button(
                                onClick = { modeName = option.name },
                                modifier = Modifier.weight(1f),
                            ) { Text(characterD20ModeLabelV4(option), maxLines = 1) }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    modeName = option.name
                                    firstDie = null
                                    secondDie = null
                                },
                                modifier = Modifier.weight(1f),
                            ) { Text(characterD20ModeLabelV4(option), maxLines = 1) }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CharacterDiceCategorySelectorV4(
                        category = category,
                        categories = availableCategories,
                        onCategoryChange = { updated ->
                            categoryName = updated.name
                            selectedKey = null
                            firstDie = null
                            secondDie = null
                            damageResultText = null
                        },
                        modifier = Modifier.weight(1f),
                    )
                    CharacterDiceTargetSelectorV4(
                        selected = selected,
                        targets = categoryTargets,
                        onTargetChange = { updated ->
                            selectedKey = updated.key
                            firstDie = null
                            secondDie = null
                            damageResultText = null
                        },
                        modifier = Modifier.weight(1.35f),
                    )
                }

                if (selected?.category == CharacterDiceTargetCategory.CUSTOM) {
                    OutlinedTextField(
                        value = customModifierText,
                        onValueChange = { customModifierText = sanitizeSignedIntV4(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Modificador manual") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                    )
                } else if (selected != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(selected.label, style = MaterialTheme.typography.titleSmall)
                            Text(
                                selected.context,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(formatSignedDiceSuccessorV4(selected.modifier), style = MaterialTheme.typography.titleMedium)
                    }
                }

                Button(
                    onClick = {
                        firstDie = Random.nextInt(1, 21)
                        secondDie = if (mode == CharacterD20Mode.NORMAL) null else Random.nextInt(1, 21)
                        damageResultText = null
                    },
                    enabled = selected != null && effectiveModifier != null,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (firstDie == null) "Tirar" else "Otra tirada")
                }
            }
        }

        resolvedRoll?.let { roll ->
            CharacterD20ResultCardV4(roll)
        }

        if (attackEntry != null && attackDamage.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = appSpacingV4(8.dp),
                        vertical = appSpacingV4(7.dp),
                    ),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                ) {
                    Text("Daño · ${attackEntry.name}", style = MaterialTheme.typography.titleSmall)
                    Text(characterDamageSummary(attackDamage), style = MaterialTheme.typography.bodyMedium)
                    Button(
                        onClick = {
                            val result = resolveCharacterDamageRoll(attackDamage) { sides ->
                                Random.nextInt(1, sides + 1)
                            }
                            damageResultText = buildCharacterDamageResultTextV4(result)
                        },
                        enabled = canRollDamage,
                    ) { Text("Tirar daño") }
                    if (!canRollDamage) {
                        Text(
                            "Este ataque solo contiene texto/efectos; no hay componentes numéricos que tirar.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    damageResultText?.let { result ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                        ) {
                            Text(
                                result,
                                modifier = Modifier.padding(appSpacingV4(7.dp)),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        }

        Text(
            "La app calcula la tirada seleccionada y muestra su descomposición. No decide legalidad, críticos ni otros efectos de reglas.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CharacterD20ResultCardV4(roll: CharacterResolvedD20Roll) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = appSpacingV4(8.dp),
                vertical = appSpacingV4(7.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Text("Resultado ${roll.total}", style = MaterialTheme.typography.titleLarge)
            Text(
                when (roll.mode) {
                    CharacterD20Mode.NORMAL ->
                        "d20 ${roll.firstDie} ${formatDiceOperationSuccessorV4(roll.modifier)} = ${roll.total}"
                    CharacterD20Mode.ADVANTAGE,
                    CharacterD20Mode.DISADVANTAGE,
                    -> "d20 ${roll.firstDie} / d20 ${roll.secondDie} → ${roll.chosenDie} ${formatDiceOperationSuccessorV4(roll.modifier)} = ${roll.total}"
                },
                style = MaterialTheme.typography.bodyMedium,
            )
            when (roll.chosenDie) {
                20 -> Text("Resultado natural: 20", style = MaterialTheme.typography.labelSmall)
                1 -> Text("Resultado natural: 1", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun CharacterDiceCategorySelectorV4(
    category: CharacterDiceTargetCategory,
    categories: List<CharacterDiceTargetCategory>,
    onCategoryChange: (CharacterDiceTargetCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(characterDiceCategoryLabelV4(category), maxLines = 1)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            categories.forEach { option ->
                DropdownMenuItem(
                    text = { Text(characterDiceCategoryLabelV4(option)) },
                    onClick = {
                        onCategoryChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun CharacterDiceTargetSelectorV4(
    selected: CharacterDiceTarget?,
    targets: List<CharacterDiceTarget>,
    onTargetChange: (CharacterDiceTarget) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            enabled = targets.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(selected?.label ?: "Seleccionar", maxLines = 1)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            targets.forEach { target ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(target.label)
                            Text(target.context, style = MaterialTheme.typography.labelSmall)
                        }
                    },
                    onClick = {
                        onTargetChange(target)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun characterD20ModeLabelV4(mode: CharacterD20Mode): String = when (mode) {
    CharacterD20Mode.NORMAL -> "Normal"
    CharacterD20Mode.ADVANTAGE -> "Ventaja"
    CharacterD20Mode.DISADVANTAGE -> "Desventaja"
}

private fun characterDiceCategoryLabelV4(category: CharacterDiceTargetCategory): String = when (category) {
    CharacterDiceTargetCategory.ABILITY -> "Características"
    CharacterDiceTargetCategory.SAVE -> "Salvaciones"
    CharacterDiceTargetCategory.SKILL -> "Habilidades"
    CharacterDiceTargetCategory.ATTACK -> "Ataques"
    CharacterDiceTargetCategory.SPELL_ATTACK -> "Ataques mágicos"
    CharacterDiceTargetCategory.CUSTOM -> "Personalizada"
}

private fun formatSignedDiceSuccessorV4(value: Int): String = if (value >= 0) "+$value" else value.toString()

private fun formatDiceOperationSuccessorV4(value: Int): String =
    if (value >= 0) "+ $value" else "− ${-value}"

private fun buildCharacterDamageResultTextV4(
    result: io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageRollResult,
): String {
    val parts = result.components.mapNotNull { rolled ->
        val type = rolled.component.typeText?.trim()?.takeIf { it.isNotEmpty() }?.let { " $it" }.orEmpty()
        when (rolled.component.kind) {
            CharacterDamageComponentKind.DICE -> if (rolled.numericValue == null) {
                rolled.component.expression.takeIf { it.isNotBlank() }
            } else {
                "${rolled.component.expression} [${rolled.diceResults.joinToString(" + ")}] = ${rolled.numericValue}$type"
            }
            CharacterDamageComponentKind.FLAT -> rolled.numericValue?.let { "${formatSignedDiceSuccessorV4(it)}$type" }
            CharacterDamageComponentKind.TEXT -> rolled.component.expression.takeIf { it.isNotBlank() }
        }
    }
    return buildString {
        append(parts.joinToString(" · "))
        if (result.hasNumericDamage) {
            if (isNotEmpty()) append("\n")
            append("Total numérico: ${result.numericTotal}")
        }
    }
}
