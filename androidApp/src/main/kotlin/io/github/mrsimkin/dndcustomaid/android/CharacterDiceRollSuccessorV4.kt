package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterD20Mode
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageComponentKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageRollResult
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDiceExpression
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
import io.github.mrsimkin.dndcustomaid.shared.character.resolveCharacterDiceExpressionRoll
import io.github.mrsimkin.dndcustomaid.shared.character.resolveCharacterDamageRoll
import kotlin.random.Random

@Composable
internal fun CharacterDiceRollSuccessorTabV4(
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    combatEntries: List<CharacterCombatEntry>,
    successorState: CharacterSuccessorState,
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
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
    var customDieChoice by rememberSaveable(sheet.id.toString(), "dice-custom-die-choice") { mutableStateOf("20") }
    var customSidesText by rememberSaveable(sheet.id.toString(), "dice-custom-sides") { mutableStateOf("") }
    var customDieResult by rememberSaveable(sheet.id.toString(), "dice-custom-result") { mutableStateOf<Int?>(null) }
    var firstDie by rememberSaveable(sheet.id.toString(), "dice-first") { mutableStateOf<Int?>(null) }
    var secondDie by rememberSaveable(sheet.id.toString(), "dice-second") { mutableStateOf<Int?>(null) }
    var damageResultText by rememberSaveable(sheet.id.toString(), "damage-result") { mutableStateOf<String?>(null) }
    var damageVisualsText by rememberSaveable(sheet.id.toString(), "damage-result-visuals") { mutableStateOf("") }

    val mode = runCatching { CharacterD20Mode.valueOf(modeName) }.getOrDefault(CharacterD20Mode.NORMAL)
    val requestedCategory = runCatching { CharacterDiceTargetCategory.valueOf(categoryName) }.getOrNull()
    val category = requestedCategory?.takeIf { it in availableCategories }
        ?: availableCategories.firstOrNull()
        ?: CharacterDiceTargetCategory.CUSTOM
    val categoryTargets = targets.filter { it.category == category }
    val selected = categoryTargets.firstOrNull { it.key == selectedKey } ?: categoryTargets.firstOrNull()
    val customModifier = customModifierText.trim().toIntOrNull()
    val customSides = if (customDieChoice == CUSTOM_DIE_OTHER_V4) {
        customSidesText.toIntOrNull()?.takeIf { it in 2..1000 }
    } else {
        customDieChoice.toIntOrNull()?.takeIf { it in STANDARD_DIE_SIDES_RESULT_V4 }
    }
    val customExpression = if (customSides != null && customModifier != null) {
        CharacterDiceExpression(count = 1, sides = customSides, modifier = customModifier)
    } else {
        null
    }
    val effectiveModifier = selected?.takeUnless { it.category == CharacterDiceTargetCategory.CUSTOM }?.modifier
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Tirada", style = MaterialTheme.typography.titleMedium)
                    CharacterDiceResultModeSelectorV4(
                        selected = preferences.diceResultMode,
                        onSelect = { selectedMode ->
                            onPreferencesChange(preferences.copy(diceResultMode = selectedMode))
                        },
                    )
                }

                if (category != CharacterDiceTargetCategory.CUSTOM) Row(
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
                            customDieResult = null
                            damageResultText = null
                            damageVisualsText = ""
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
                            customDieResult = null
                            damageResultText = null
                            damageVisualsText = ""
                        },
                        modifier = Modifier.weight(1.35f),
                    )
                }

                if (selected?.category == CharacterDiceTargetCategory.CUSTOM) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                        verticalAlignment = Alignment.Top,
                    ) {
                        CharacterCustomDieSelectorV4(
                            selected = customDieChoice,
                            onSelect = { selectedDie ->
                                customDieChoice = selectedDie
                                customDieResult = null
                            },
                            modifier = Modifier.weight(1f),
                        )
                        CharacterSignedModifierEditorV4(
                            value = customModifierText,
                            onValueChange = { updated ->
                                customModifierText = sanitizeSignedIntegerInputV4(updated)
                                customDieResult = null
                            },
                            modifier = Modifier.weight(1.25f),
                        )
                    }
                    if (customDieChoice == CUSTOM_DIE_OTHER_V4) {
                        CharacterCompactOutlinedTextFieldV4(
                            value = customSidesText,
                            onValueChange = { value ->
                                customSidesText = value.filter(Char::isDigit).take(4)
                                customDieResult = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Caras del dado · 2–1000") },
                            prefix = { Text("d") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                        )
                    }
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
                        if (selected?.category == CharacterDiceTargetCategory.CUSTOM) {
                            val expression = requireNotNull(customExpression)
                            val result = resolveCharacterDiceExpressionRoll(expression) { sides ->
                                Random.nextInt(1, sides + 1)
                            }
                            customDieResult = result.diceResults.single()
                            firstDie = null
                            secondDie = null
                        } else {
                            firstDie = Random.nextInt(1, 21)
                            secondDie = if (mode == CharacterD20Mode.NORMAL) null else Random.nextInt(1, 21)
                            customDieResult = null
                        }
                        damageResultText = null
                        damageVisualsText = ""
                    },
                    enabled = selected != null && if (selected.category == CharacterDiceTargetCategory.CUSTOM) {
                        customExpression != null
                    } else {
                        effectiveModifier != null
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val customLabel = customSides?.let { "Tirar d$it" } ?: "Tirar dado"
                    Text(
                        when {
                            selected?.category == CharacterDiceTargetCategory.CUSTOM && customDieResult == null -> customLabel
                            selected?.category == CharacterDiceTargetCategory.CUSTOM -> "Otra tirada"
                            firstDie == null -> "Tirar"
                            else -> "Otra tirada"
                        },
                    )
                }
            }
        }

        resolvedRoll?.let { roll ->
            if (preferences.diceResultMode == DiceResultModeChoice.VISIBLE_DICE) {
                CharacterVisibleDiceResultCardV4(roll)
            } else {
                CharacterD20ResultCardV4(roll)
            }
        }

        if (selected?.category == CharacterDiceTargetCategory.CUSTOM) {
            val raw = customDieResult
            val expression = customExpression
            if (raw != null && expression != null) {
                CharacterCustomDiceResultCardV4(
                    expression = expression,
                    rawResult = raw,
                    visibleDie = preferences.diceResultMode == DiceResultModeChoice.VISIBLE_DICE,
                )
            }
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
                            damageVisualsText = encodeCharacterDamageVisualsV4(result)
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
                            Column(
                                modifier = Modifier.padding(appSpacingV4(7.dp)),
                                verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                            ) {
                                if (preferences.diceResultMode == DiceResultModeChoice.VISIBLE_DICE) {
                                    CharacterDamageDiceVisualsV4(damageVisualsText)
                                }
                                Text(result, style = MaterialTheme.typography.bodyMedium)
                            }
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

private val STANDARD_DIE_SIDES_RESULT_V4 = listOf(4, 6, 8, 10, 12, 20)
private const val CUSTOM_DIE_OTHER_V4 = "OTHER"

private data class CharacterDamageVisualTokenV4(
    val sides: Int?,
    val value: Int,
)

@Composable
private fun CharacterDiceResultModeSelectorV4(
    selected: DiceResultModeChoice,
    onSelect: (DiceResultModeChoice) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected.label, maxLines = 1)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DiceResultModeChoice.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun CharacterCustomDieSelectorV4(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val label = selected.toIntOrNull()?.takeIf { it in STANDARD_DIE_SIDES_RESULT_V4 }?.let { "d$it" } ?: "Otro…"
    Column(modifier = modifier) {
        Text("Dado", style = MaterialTheme.typography.labelSmall)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(label, maxLines = 1)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                STANDARD_DIE_SIDES_RESULT_V4.forEach { sides ->
                    DropdownMenuItem(
                        text = { Text("d$sides") },
                        onClick = {
                            onSelect(sides.toString())
                            expanded = false
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text("Otro…") },
                    onClick = {
                        onSelect(CUSTOM_DIE_OTHER_V4)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun CharacterSignedModifierEditorV4(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val parsed = value.trim().toIntOrNull()
    val sign = if ((parsed ?: 0) < 0 || value.trim().startsWith("-")) "−" else "+"
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.Bottom,
    ) {
        OutlinedButton(
            onClick = { onValueChange(toggleCharacterSignedModifierV4(value)) },
            modifier = Modifier.weight(0.55f),
        ) {
            Text(sign)
        }
        CharacterCompactOutlinedTextFieldV4(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            label = { Text("Mod.") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
        )
    }
}

private fun toggleCharacterSignedModifierV4(value: String): String {
    val trimmed = value.trim()
    val magnitude = trimmed.removePrefix("+").removePrefix("-").ifBlank { "0" }
    return if (trimmed.startsWith("-")) magnitude else "-$magnitude"
}

@Composable
private fun CharacterCustomDiceResultCardV4(
    expression: CharacterDiceExpression,
    rawResult: Int,
    visibleDie: Boolean,
) {
    val total = rawResult + expression.modifier
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = appSpacingV4(8.dp),
                vertical = appSpacingV4(7.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
            horizontalAlignment = if (visibleDie) Alignment.CenterHorizontally else Alignment.Start,
        ) {
            if (visibleDie) {
                CharacterDieResultVisualV4(sides = expression.sides, value = rawResult)
            }
            Text("Resultado $total", style = MaterialTheme.typography.titleLarge)
            Text(
                "d${expression.sides} $rawResult ${formatDiceOperationSuccessorV4(expression.modifier)} = $total",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun CharacterDamageDiceVisualsV4(encoded: String) {
    val tokens = remember(encoded) { decodeCharacterDamageVisualsV4(encoded) }
    if (tokens.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tokens.forEachIndexed { index, token ->
            CharacterDieResultVisualV4(
                sides = token.sides,
                value = token.value,
                labelOverride = if (token.sides == null) "plano" else null,
            )
            if (index < tokens.lastIndex) Text("+", style = MaterialTheme.typography.titleSmall)
        }
    }
}

private fun encodeCharacterDamageVisualsV4(result: CharacterDamageRollResult): String = result.components
    .flatMap { rolled ->
        when (rolled.component.kind) {
            CharacterDamageComponentKind.DICE -> {
                val sides = parseCharacterDiceExpression(rolled.component.expression)?.sides
                if (sides == null) emptyList() else rolled.diceResults.map { CharacterDamageVisualTokenV4(sides, it) }
            }
            CharacterDamageComponentKind.FLAT -> rolled.numericValue?.let { listOf(CharacterDamageVisualTokenV4(null, it)) }.orEmpty()
            CharacterDamageComponentKind.TEXT -> emptyList()
        }
    }
    .joinToString(";") { token -> "${token.sides ?: 0}:${token.value}" }

private fun decodeCharacterDamageVisualsV4(encoded: String): List<CharacterDamageVisualTokenV4> = encoded
    .split(';')
    .mapNotNull { raw ->
        val parts = raw.split(':', limit = 2)
        if (parts.size != 2) return@mapNotNull null
        val encodedSides = parts[0].toIntOrNull() ?: return@mapNotNull null
        val value = parts[1].toIntOrNull() ?: return@mapNotNull null
        CharacterDamageVisualTokenV4(sides = encodedSides.takeUnless { it == 0 }, value = value)
    }

@Composable
private fun CharacterDieResultVisualV4(
    sides: Int?,
    value: Int,
    chosen: Boolean = false,
    labelOverride: String? = null,
) {
    val outlineColor = if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val fillColor = if (chosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(labelOverride ?: sides?.let { "d$it" } ?: "resultado", style = MaterialTheme.typography.labelSmall)
        Box(modifier = Modifier.size(74.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.fillMaxSize().padding(3.dp)) {
                val path = characterDieSilhouettePathV4(sides, size.width, size.height)
                if (path == null) {
                    drawCircle(color = fillColor, radius = size.minDimension * 0.43f)
                    drawCircle(
                        color = outlineColor,
                        radius = size.minDimension * 0.43f,
                        style = Stroke(width = if (chosen) 4f else 2.5f),
                    )
                } else {
                    drawPath(path = path, color = fillColor)
                    drawPath(path = path, color = outlineColor, style = Stroke(width = if (chosen) 4f else 2.5f))
                    if (sides == 20) {
                        drawLine(
                            color = outlineColor,
                            start = Offset(size.width * 0.22f, size.height * 0.34f),
                            end = Offset(size.width * 0.78f, size.height * 0.34f),
                            strokeWidth = 1.5f,
                        )
                        drawLine(
                            color = outlineColor,
                            start = Offset(size.width * 0.22f, size.height * 0.34f),
                            end = Offset(size.width * 0.50f, size.height * 0.82f),
                            strokeWidth = 1.5f,
                        )
                        drawLine(
                            color = outlineColor,
                            start = Offset(size.width * 0.78f, size.height * 0.34f),
                            end = Offset(size.width * 0.50f, size.height * 0.82f),
                            strokeWidth = 1.5f,
                        )
                    }
                }
            }
            Text(value.toString(), style = MaterialTheme.typography.headlineSmall)
        }
        if (chosen) Text("elegido", style = MaterialTheme.typography.labelSmall)
    }
}

private fun characterDieSilhouettePathV4(sides: Int?, width: Float, height: Float): Path? {
    val points: List<Offset> = when (sides) {
        4 -> listOf(
            Offset(width * 0.50f, height * 0.07f),
            Offset(width * 0.93f, height * 0.88f),
            Offset(width * 0.07f, height * 0.88f),
        )
        6 -> listOf(
            Offset(width * 0.12f, height * 0.12f),
            Offset(width * 0.88f, height * 0.12f),
            Offset(width * 0.88f, height * 0.88f),
            Offset(width * 0.12f, height * 0.88f),
        )
        8 -> listOf(
            Offset(width * 0.50f, height * 0.05f),
            Offset(width * 0.92f, height * 0.50f),
            Offset(width * 0.50f, height * 0.95f),
            Offset(width * 0.08f, height * 0.50f),
        )
        10 -> listOf(
            Offset(width * 0.50f, height * 0.04f),
            Offset(width * 0.91f, height * 0.34f),
            Offset(width * 0.76f, height * 0.84f),
            Offset(width * 0.50f, height * 0.97f),
            Offset(width * 0.24f, height * 0.84f),
            Offset(width * 0.09f, height * 0.34f),
        )
        12 -> listOf(
            Offset(width * 0.50f, height * 0.05f),
            Offset(width * 0.93f, height * 0.36f),
            Offset(width * 0.77f, height * 0.90f),
            Offset(width * 0.23f, height * 0.90f),
            Offset(width * 0.07f, height * 0.36f),
        )
        20 -> listOf(
            Offset(width * 0.50f, height * 0.04f),
            Offset(width * 0.88f, height * 0.25f),
            Offset(width * 0.88f, height * 0.73f),
            Offset(width * 0.50f, height * 0.96f),
            Offset(width * 0.12f, height * 0.73f),
            Offset(width * 0.12f, height * 0.25f),
        )
        else -> return null
    }
    return Path().apply {
        moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { point -> lineTo(point.x, point.y) }
        close()
    }
}

@Composable
private fun CharacterVisibleDiceResultCardV4(roll: CharacterResolvedD20Roll) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = appSpacingV4(8.dp),
                vertical = appSpacingV4(7.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        ) {
            Text("Dados", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp), Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CharacterVisibleD20V4(
                    value = roll.firstDie,
                    chosen = roll.chosenDie == roll.firstDie && (roll.secondDie == null || roll.firstDie != roll.secondDie),
                )
                roll.secondDie?.let { second ->
                    CharacterVisibleD20V4(
                        value = second,
                        chosen = roll.chosenDie == second && (roll.firstDie != second || roll.mode != CharacterD20Mode.NORMAL),
                    )
                }
            }
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
private fun CharacterVisibleD20V4(value: Int, chosen: Boolean) {
    CharacterDieResultVisualV4(sides = 20, value = value, chosen = chosen)
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
