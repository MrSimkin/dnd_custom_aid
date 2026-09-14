from pathlib import Path


def replace_once(path: str, old: str, new: str) -> None:
    target = Path(path)
    text = target.read_text(encoding="utf-8")
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"Expected exactly one match in {path}, found {count}: {old[:120]!r}")
    target.write_text(text.replace(old, new, 1), encoding="utf-8")


def insert_before_once(path: str, marker: str, addition: str) -> None:
    replace_once(path, marker, addition + marker)


DICE = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterDiceRollSuccessorV4.kt"
EDITOR = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt"
PREFS = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt"
OPS = "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterCombatDiceOperations.kt"
TEST = "shared/src/commonTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterCombatDiceOperationsTest.kt"
SCAFFOLD = ".github/workflows/scaffold-check.yml"
GUARD = "scripts/check_player_dice_t2.py"

# Shared, reusable arbitrary-die roll primitive. Damage rolling is routed through it too.
insert_before_once(
    OPS,
    "data class CharacterDamageRolledComponent(\n",
    '''data class CharacterResolvedDiceExpressionRoll(\n    val expression: CharacterDiceExpression,\n    val diceResults: List<Int>,\n) {\n    val diceTotal: Int\n        get() = diceResults.sum()\n\n    val total: Int\n        get() = diceTotal + expression.modifier\n}\n\nfun resolveCharacterDiceExpressionRoll(\n    expression: CharacterDiceExpression,\n    dieRoller: (sides: Int) -> Int,\n): CharacterResolvedDiceExpressionRoll {\n    val results = List(expression.count) {\n        dieRoller(expression.sides).also { result ->\n            require(result in 1..expression.sides) {\n                "Die roller returned $result for d${expression.sides}."\n            }\n        }\n    }\n    return CharacterResolvedDiceExpressionRoll(expression = expression, diceResults = results)\n}\n\n''',
)
replace_once(
    OPS,
    '''                if (expression == null) {\n                    CharacterDamageRolledComponent(component)\n                } else {\n                    val results = List(expression.count) {\n                        dieRoller(expression.sides).also { result ->\n                            require(result in 1..expression.sides) {\n                                "Die roller returned $result for d${expression.sides}."\n                            }\n                        }\n                    }\n                    CharacterDamageRolledComponent(\n                        component = component,\n                        diceResults = results,\n                        numericValue = results.sum() + expression.modifier,\n                    )\n                }\n''',
    '''                if (expression == null) {\n                    CharacterDamageRolledComponent(component)\n                } else {\n                    val roll = resolveCharacterDiceExpressionRoll(expression, dieRoller)\n                    CharacterDamageRolledComponent(\n                        component = component,\n                        diceResults = roll.diceResults,\n                        numericValue = roll.total,\n                    )\n                }\n''',
)

insert_before_once(
    TEST,
    '''    @Test\n    fun malformedDiceComponentRemainsNonNumericAtRollTime() {\n''',
    '''    @Test\n    fun arbitraryDiceExpressionRollUsesSidesAndSignedModifier() {\n        val expression = requireNotNull(parseCharacterDiceExpression("1d12-2"))\n        val result = resolveCharacterDiceExpressionRoll(expression) { sides ->\n            assertEquals(12, sides)\n            9\n        }\n\n        assertEquals(listOf(9), result.diceResults)\n        assertEquals(9, result.diceTotal)\n        assertEquals(7, result.total)\n    }\n\n''',
)

# Character editor already owns UiPreferences persistence. Pass that existing mutation seam to Dice.
replace_once(
    EDITOR,
    '''                        CharacterTabV4.DICE -> CharacterDiceRollSuccessorTabV4(\n                            sheet = overviewProjectionSheet,\n                            closureState = closureState,\n                            combatEntries = combatEntries,\n                            successorState = successorState.copy(combatDamage = combatDamageProfiles),\n                        )\n''',
    '''                        CharacterTabV4.DICE -> CharacterDiceRollSuccessorTabV4(\n                            sheet = overviewProjectionSheet,\n                            closureState = closureState,\n                            combatEntries = combatEntries,\n                            successorState = successorState.copy(combatDamage = combatDamageProfiles),\n                            preferences = preferences,\n                            onPreferencesChange = onPreferencesChange,\n                        )\n''',
)

# Move presentation ownership out of Application Settings while preserving the same persisted field/key.
replace_once(
    PREFS,
    '''                item {\n                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {\n                        SettingSelector(\n                            label = "Resultados de dados",\n                            value = preferences.diceResultMode.label,\n                            options = DiceResultModeChoice.entries,\n                            optionLabel = { it.label },\n                            onSelect = { onPreferencesChange(preferences.copy(diceResultMode = it)) },\n                        )\n                        Text(\n                            "Compacto prioriza densidad. Dados visibles destaca los d20; ambos conservan la misma matemática.",\n                            style = MaterialTheme.typography.bodySmall,\n                            color = MaterialTheme.colorScheme.onSurfaceVariant,\n                        )\n                    }\n                }\n''',
    "",
)
replace_once(
    PREFS,
    '''                    "${preferences.themeChoice.label} · ${preferences.fontChoice.label} · Texto ${preferences.fontScalePercent}% · Espacios ${preferences.spacingScalePercent}% · Ayuda ${preferences.helpMode.label} · Dados ${preferences.diceResultMode.label}",\n''',
    '''                    "${preferences.themeChoice.label} · ${preferences.fontChoice.label} · Texto ${preferences.fontScalePercent}% · Espacios ${preferences.spacingScalePercent}% · Ayuda ${preferences.helpMode.label}",\n''',
)

# Dice surface imports for die silhouettes and horizontally scrollable damage-result visuals.
replace_once(DICE, "import androidx.compose.foundation.BorderStroke\n", "import androidx.compose.foundation.BorderStroke\nimport androidx.compose.foundation.Canvas\nimport androidx.compose.foundation.horizontalScroll\nimport androidx.compose.foundation.rememberScrollState\n")
replace_once(DICE, "import androidx.compose.foundation.layout.padding\n", "import androidx.compose.foundation.layout.padding\nimport androidx.compose.foundation.layout.size\n")
replace_once(DICE, "import androidx.compose.ui.Alignment\n", "import androidx.compose.ui.Alignment\nimport androidx.compose.ui.geometry.Offset\nimport androidx.compose.ui.graphics.Path\nimport androidx.compose.ui.graphics.drawscope.Stroke\n")
replace_once(DICE, "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageComponentKind\n", "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageComponentKind\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageRollResult\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterDiceExpression\n")
replace_once(DICE, "import io.github.mrsimkin.dndcustomaid.shared.character.resolveCharacterD20Roll\n", "import io.github.mrsimkin.dndcustomaid.shared.character.resolveCharacterD20Roll\nimport io.github.mrsimkin.dndcustomaid.shared.character.resolveCharacterDiceExpressionRoll\n")

# Explicit preference callback; no new store or key.
replace_once(
    DICE,
    '''internal fun CharacterDiceRollSuccessorTabV4(\n    sheet: CharacterSheet,\n    closureState: CharacterClosureState,\n    combatEntries: List<CharacterCombatEntry>,\n    successorState: CharacterSuccessorState,\n) {\n''',
    '''internal fun CharacterDiceRollSuccessorTabV4(\n    sheet: CharacterSheet,\n    closureState: CharacterClosureState,\n    combatEntries: List<CharacterCombatEntry>,\n    successorState: CharacterSuccessorState,\n    preferences: UiPreferences,\n    onPreferencesChange: (UiPreferences) -> Unit,\n) {\n''',
)

# Custom Throw state is independent from the ordinary d20 mode.
replace_once(
    DICE,
    '''    var customModifierText by rememberSaveable(sheet.id.toString(), "dice-custom-modifier") { mutableStateOf("0") }\n    var firstDie by rememberSaveable(sheet.id.toString(), "dice-first") { mutableStateOf<Int?>(null) }\n''',
    '''    var customModifierText by rememberSaveable(sheet.id.toString(), "dice-custom-modifier") { mutableStateOf("0") }\n    var customDieChoice by rememberSaveable(sheet.id.toString(), "dice-custom-die-choice") { mutableStateOf("20") }\n    var customSidesText by rememberSaveable(sheet.id.toString(), "dice-custom-sides") { mutableStateOf("") }\n    var customDieResult by rememberSaveable(sheet.id.toString(), "dice-custom-result") { mutableStateOf<Int?>(null) }\n    var firstDie by rememberSaveable(sheet.id.toString(), "dice-first") { mutableStateOf<Int?>(null) }\n''',
)
replace_once(
    DICE,
    '''    var damageResultText by rememberSaveable(sheet.id.toString(), "damage-result") { mutableStateOf<String?>(null) }\n''',
    '''    var damageResultText by rememberSaveable(sheet.id.toString(), "damage-result") { mutableStateOf<String?>(null) }\n    var damageVisualsText by rememberSaveable(sheet.id.toString(), "damage-result-visuals") { mutableStateOf("") }\n''',
)

replace_once(
    DICE,
    '''    val customModifier = customModifierText.trim().toIntOrNull()\n    val effectiveModifier = if (selected?.category == CharacterDiceTargetCategory.CUSTOM) customModifier else selected?.modifier\n    val resolvedRoll: CharacterResolvedD20Roll? = if (firstDie != null && effectiveModifier != null) {\n        runCatching {\n            resolveCharacterD20Roll(\n                firstDie = requireNotNull(firstDie),\n                secondDie = secondDie,\n                mode = mode,\n                modifier = effectiveModifier,\n            )\n        }.getOrNull()\n    } else {\n        null\n    }\n''',
    '''    val customModifier = customModifierText.trim().toIntOrNull()\n    val customSides = if (customDieChoice == CUSTOM_DIE_OTHER_V4) {\n        customSidesText.toIntOrNull()?.takeIf { it in 2..1000 }\n    } else {\n        customDieChoice.toIntOrNull()?.takeIf { it in STANDARD_DIE_SIDES_RESULT_V4 }\n    }\n    val customExpression = if (customSides != null && customModifier != null) {\n        CharacterDiceExpression(count = 1, sides = customSides, modifier = customModifier)\n    } else {\n        null\n    }\n    val effectiveModifier = selected?.takeUnless { it.category == CharacterDiceTargetCategory.CUSTOM }?.modifier\n    val resolvedRoll: CharacterResolvedD20Roll? = if (firstDie != null && effectiveModifier != null) {\n        runCatching {\n            resolveCharacterD20Roll(\n                firstDie = requireNotNull(firstDie),\n                secondDie = secondDie,\n                mode = mode,\n                modifier = effectiveModifier,\n            )\n        }.getOrNull()\n    } else {\n        null\n    }\n''',
)

# Dice-tab-owned presentation selector.
replace_once(
    DICE,
    '''                Text("Tirada", style = MaterialTheme.typography.titleMedium)\n\n                Row(\n''',
    '''                Row(\n                    modifier = Modifier.fillMaxWidth(),\n                    horizontalArrangement = Arrangement.SpaceBetween,\n                    verticalAlignment = Alignment.CenterVertically,\n                ) {\n                    Text("Tirada", style = MaterialTheme.typography.titleMedium)\n                    CharacterDiceResultModeSelectorV4(\n                        selected = preferences.diceResultMode,\n                        onSelect = { selectedMode ->\n                            onPreferencesChange(preferences.copy(diceResultMode = selectedMode))\n                        },\n                    )\n                }\n\n                if (category != CharacterDiceTargetCategory.CUSTOM) Row(\n''',
)

# Reset all dice-result channels when target/category changes.
replace_once(
    DICE,
    '''                            firstDie = null\n                            secondDie = null\n                            damageResultText = null\n''',
    '''                            firstDie = null\n                            secondDie = null\n                            customDieResult = null\n                            damageResultText = null\n                            damageVisualsText = ""\n''',
)
replace_once(
    DICE,
    '''                            firstDie = null\n                            secondDie = null\n                            damageResultText = null\n                        },\n                        modifier = Modifier.weight(1.35f),\n''',
    '''                            firstDie = null\n                            secondDie = null\n                            customDieResult = null\n                            damageResultText = null\n                            damageVisualsText = ""\n                        },\n                        modifier = Modifier.weight(1.35f),\n''',
)

# Replace d20-only custom modifier field with die selector, Otro/custom sides and signed modifier control.
replace_once(
    DICE,
    '''                if (selected?.category == CharacterDiceTargetCategory.CUSTOM) {\n                    CharacterCompactOutlinedTextFieldV4(\n                        value = customModifierText,\n                        onValueChange = { customModifierText = sanitizeSignedIntegerInputV4(it) },\n                        modifier = Modifier.fillMaxWidth(),\n                        label = { Text("Modificador manual") },\n                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),\n                        singleLine = true,\n                    )\n                } else if (selected != null) {\n''',
    '''                if (selected?.category == CharacterDiceTargetCategory.CUSTOM) {\n                    Row(\n                        modifier = Modifier.fillMaxWidth(),\n                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),\n                        verticalAlignment = Alignment.Top,\n                    ) {\n                        CharacterCustomDieSelectorV4(\n                            selected = customDieChoice,\n                            onSelect = { selectedDie ->\n                                customDieChoice = selectedDie\n                                customDieResult = null\n                            },\n                            modifier = Modifier.weight(1f),\n                        )\n                        CharacterSignedModifierEditorV4(\n                            value = customModifierText,\n                            onValueChange = { updated ->\n                                customModifierText = sanitizeSignedIntegerInputV4(updated)\n                                customDieResult = null\n                            },\n                            modifier = Modifier.weight(1.25f),\n                        )\n                    }\n                    if (customDieChoice == CUSTOM_DIE_OTHER_V4) {\n                        CharacterCompactOutlinedTextFieldV4(\n                            value = customSidesText,\n                            onValueChange = { value ->\n                                customSidesText = value.filter(Char::isDigit).take(4)\n                                customDieResult = null\n                            },\n                            modifier = Modifier.fillMaxWidth(),\n                            label = { Text("Caras del dado · 2–1000") },\n                            prefix = { Text("d") },\n                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),\n                            singleLine = true,\n                        )\n                    }\n                } else if (selected != null) {\n''',
)

# Roll either the ordinary d20 mode or the arbitrary custom expression through shared domain logic.
replace_once(
    DICE,
    '''                Button(\n                    onClick = {\n                        firstDie = Random.nextInt(1, 21)\n                        secondDie = if (mode == CharacterD20Mode.NORMAL) null else Random.nextInt(1, 21)\n                        damageResultText = null\n                    },\n                    enabled = selected != null && effectiveModifier != null,\n                    modifier = Modifier.fillMaxWidth(),\n                ) {\n                    Text(if (firstDie == null) "Tirar" else "Otra tirada")\n                }\n''',
    '''                Button(\n                    onClick = {\n                        if (selected?.category == CharacterDiceTargetCategory.CUSTOM) {\n                            val expression = requireNotNull(customExpression)\n                            val result = resolveCharacterDiceExpressionRoll(expression) { sides ->\n                                Random.nextInt(1, sides + 1)\n                            }\n                            customDieResult = result.diceResults.single()\n                            firstDie = null\n                            secondDie = null\n                        } else {\n                            firstDie = Random.nextInt(1, 21)\n                            secondDie = if (mode == CharacterD20Mode.NORMAL) null else Random.nextInt(1, 21)\n                            customDieResult = null\n                        }\n                        damageResultText = null\n                        damageVisualsText = ""\n                    },\n                    enabled = selected != null && if (selected.category == CharacterDiceTargetCategory.CUSTOM) {\n                        customExpression != null\n                    } else {\n                        effectiveModifier != null\n                    },\n                    modifier = Modifier.fillMaxWidth(),\n                ) {\n                    val customLabel = customSides?.let { "Tirar d$it" } ?: "Tirar dado"\n                    Text(\n                        when {\n                            selected?.category == CharacterDiceTargetCategory.CUSTOM && customDieResult == null -> customLabel\n                            selected?.category == CharacterDiceTargetCategory.CUSTOM -> "Otra tirada"\n                            firstDie == null -> "Tirar"\n                            else -> "Otra tirada"\n                        },\n                    )\n                }\n''',
)

# Render custom roll and ordinary d20 through the same die-specific visual language.
replace_once(
    DICE,
    '''        resolvedRoll?.let { roll ->\n            if (LocalUiPreferencesV4.current.diceResultMode == DiceResultModeChoice.VISIBLE_DICE) {\n                CharacterVisibleDiceResultCardV4(roll)\n            } else {\n                CharacterD20ResultCardV4(roll)\n            }\n        }\n\n''',
    '''        resolvedRoll?.let { roll ->\n            if (preferences.diceResultMode == DiceResultModeChoice.VISIBLE_DICE) {\n                CharacterVisibleDiceResultCardV4(roll)\n            } else {\n                CharacterD20ResultCardV4(roll)\n            }\n        }\n\n        if (selected?.category == CharacterDiceTargetCategory.CUSTOM) {\n            val raw = customDieResult\n            val expression = customExpression\n            if (raw != null && expression != null) {\n                CharacterCustomDiceResultCardV4(\n                    expression = expression,\n                    rawResult = raw,\n                    visibleDie = preferences.diceResultMode == DiceResultModeChoice.VISIBLE_DICE,\n                )\n            }\n        }\n\n''',
)

# Preserve textual damage breakdown, and also persist enough compact visual tokens to redraw die identity after rotation.
replace_once(
    DICE,
    '''                            val result = resolveCharacterDamageRoll(attackDamage) { sides ->\n                                Random.nextInt(1, sides + 1)\n                            }\n                            damageResultText = buildCharacterDamageResultTextV4(result)\n''',
    '''                            val result = resolveCharacterDamageRoll(attackDamage) { sides ->\n                                Random.nextInt(1, sides + 1)\n                            }\n                            damageResultText = buildCharacterDamageResultTextV4(result)\n                            damageVisualsText = encodeCharacterDamageVisualsV4(result)\n''',
)
replace_once(
    DICE,
    '''                            Text(\n                                result,\n                                modifier = Modifier.padding(appSpacingV4(7.dp)),\n                                style = MaterialTheme.typography.bodyMedium,\n                            )\n''',
    '''                            Column(\n                                modifier = Modifier.padding(appSpacingV4(7.dp)),\n                                verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),\n                            ) {\n                                if (preferences.diceResultMode == DiceResultModeChoice.VISIBLE_DICE) {\n                                    CharacterDamageDiceVisualsV4(damageVisualsText)\n                                }\n                                Text(result, style = MaterialTheme.typography.bodyMedium)\n                            }\n''',
)

# Replace generic rounded d20 tile with the reusable silhouette primitive.
replace_once(
    DICE,
    '''@Composable\nprivate fun CharacterVisibleD20V4(value: Int, chosen: Boolean) {\n    Surface(\n        shape = MaterialTheme.shapes.large,\n        border = BorderStroke(\n            if (chosen) 2.dp else 1.dp,\n            if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,\n        ),\n        color = if (chosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,\n        tonalElevation = if (chosen) 3.dp else 0.dp,\n    ) {\n        Column(\n            modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),\n            horizontalAlignment = Alignment.CenterHorizontally,\n        ) {\n            Text("d20", style = MaterialTheme.typography.labelSmall)\n            Text(value.toString(), style = MaterialTheme.typography.headlineMedium)\n            if (chosen) Text("elegido", style = MaterialTheme.typography.labelSmall)\n        }\n    }\n}\n\n''',
    '''@Composable\nprivate fun CharacterVisibleD20V4(value: Int, chosen: Boolean) {\n    CharacterDieResultVisualV4(sides = 20, value = value, chosen = chosen)\n}\n\n''',
)

# Add T2 helpers before the visible d20 card.
insert_before_once(
    DICE,
    '''@Composable\nprivate fun CharacterVisibleDiceResultCardV4(roll: CharacterResolvedD20Roll) {\n''',
    r'''private val STANDARD_DIE_SIDES_RESULT_V4 = listOf(4, 6, 8, 10, 12, 20)
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

''',
)

# Permanent guard: ownership, Custom Throw, silhouette contract, shared arbitrary die roller.
guard_text = r'''#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
dice = (ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterDiceRollSuccessorV4.kt").read_text(encoding="utf-8")
editor = (ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt").read_text(encoding="utf-8")
prefs = (ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt").read_text(encoding="utf-8")
ops = (ROOT / "shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterCombatDiceOperations.kt").read_text(encoding="utf-8")

required_dice = [
    "STANDARD_DIE_SIDES_RESULT_V4 = listOf(4, 6, 8, 10, 12, 20)",
    "CUSTOM_DIE_OTHER_V4",
    "CharacterCustomDieSelectorV4",
    "Caras del dado · 2–1000",
    "CharacterSignedModifierEditorV4",
    "resolveCharacterDiceExpressionRoll(expression)",
    "CharacterDieResultVisualV4",
    "characterDieSilhouettePathV4",
    "CharacterDamageDiceVisualsV4",
    "CharacterDiceResultModeSelectorV4",
    "onPreferencesChange(preferences.copy(diceResultMode = selectedMode))",
]
for token in required_dice:
    if token not in dice:
        raise SystemExit(f"T2 dice guard: missing {token!r}")

if 'label = "Resultados de dados"' in prefs:
    raise SystemExit("T2 dice guard: dice-result presentation ownership leaked back into Application Settings")
if 'KEY_DICE_RESULT_MODE = "dice_result_mode"' not in prefs:
    raise SystemExit("T2 dice guard: existing dice_result_mode persistence key changed or disappeared")
if "preferences = preferences" not in editor or "onPreferencesChange = onPreferencesChange" not in editor:
    raise SystemExit("T2 dice guard: CharacterEditor no longer passes preference ownership to Dice tab")
if "fun resolveCharacterDiceExpressionRoll(" not in ops:
    raise SystemExit("T2 dice guard: shared arbitrary-die resolver missing")
if "val roll = resolveCharacterDiceExpressionRoll(expression, dieRoller)" not in ops:
    raise SystemExit("T2 dice guard: damage rolling no longer reuses shared arbitrary-die resolver")

print("Player T2 dice guard: OK")
'''
Path(GUARD).write_text(guard_text, encoding="utf-8")

# Wire the permanent guard into normal read-only Scaffold.
replace_once(
    SCAFFOLD,
    '''      - name: Guard Player Table Mode affordances\n        run: python3 scripts/check_player_table_mode_affordances.py\n      - name: Prepare stable CI debug keystore\n''',
    '''      - name: Guard Player Table Mode affordances\n        run: python3 scripts/check_player_table_mode_affordances.py\n      - name: Guard Player T2 dice presentation and Custom Throw\n        run: python3 scripts/check_player_dice_t2.py\n      - name: Prepare stable CI debug keystore\n''',
)

print("Phase 4A T2 bounded migration applied")
