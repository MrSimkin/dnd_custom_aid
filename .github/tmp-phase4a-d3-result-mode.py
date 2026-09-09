from pathlib import Path

ui = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt")
text = ui.read_text()


def replace_once(old: str, new: str, label: str) -> None:
    global text
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one match, found {count}")
    text = text.replace(old, new, 1)

replace_once(
    '''internal enum class SkillLayoutChoice(val label: String) {
    BY_SKILLS("Por habilidades"),
    BY_ATTRIBUTE("Por atributo"),
}

internal data class UiPreferences(
''',
    '''internal enum class SkillLayoutChoice(val label: String) {
    BY_SKILLS("Por habilidades"),
    BY_ATTRIBUTE("Por atributo"),
}

internal enum class DiceResultModeChoice(val label: String) {
    COMPACT("Resultado compacto"),
    VISIBLE_DICE("Dados visibles"),
}

internal data class UiPreferences(
''',
    "result-mode-enum",
)

replace_once(
    '''    val spacingScalePercent: Int = 100,
    val helpMode: CharacterHelpModeV4 = CharacterHelpModeV4.ALWAYS_VISIBLE,
)
''',
    '''    val spacingScalePercent: Int = 100,
    val helpMode: CharacterHelpModeV4 = CharacterHelpModeV4.ALWAYS_VISIBLE,
    val diceResultMode: DiceResultModeChoice = DiceResultModeChoice.COMPACT,
)
''',
    "preference-field",
)

replace_once(
    '''        val helpMode = preferences.getString(KEY_HELP_MODE, null)
            ?.let { runCatching { CharacterHelpModeV4.valueOf(it) }.getOrNull() }
            ?: CharacterHelpModeV4.ALWAYS_VISIBLE

        return UiPreferences(
''',
    '''        val helpMode = preferences.getString(KEY_HELP_MODE, null)
            ?.let { runCatching { CharacterHelpModeV4.valueOf(it) }.getOrNull() }
            ?: CharacterHelpModeV4.ALWAYS_VISIBLE
        val diceResultMode = preferences.getString(KEY_DICE_RESULT_MODE, null)
            ?.let { runCatching { DiceResultModeChoice.valueOf(it) }.getOrNull() }
            ?: DiceResultModeChoice.COMPACT

        return UiPreferences(
''',
    "load-result-mode",
)

replace_once(
    '''            spacingScalePercent = spacingScalePercent,
            helpMode = helpMode,
        )
''',
    '''            spacingScalePercent = spacingScalePercent,
            helpMode = helpMode,
            diceResultMode = diceResultMode,
        )
''',
    "return-result-mode",
)

replace_once(
    '''            .putInt(KEY_SPACING_SCALE, value.spacingScalePercent)
            .putString(KEY_HELP_MODE, value.helpMode.name)
            .apply()
''',
    '''            .putInt(KEY_SPACING_SCALE, value.spacingScalePercent)
            .putString(KEY_HELP_MODE, value.helpMode.name)
            .putString(KEY_DICE_RESULT_MODE, value.diceResultMode.name)
            .apply()
''',
    "save-result-mode",
)

replace_once(
    '''        const val KEY_SPACING_SCALE = "spacing_scale_percent"
        const val KEY_HELP_MODE = "help_mode"
    }
''',
    '''        const val KEY_SPACING_SCALE = "spacing_scale_percent"
        const val KEY_HELP_MODE = "help_mode"
        const val KEY_DICE_RESULT_MODE = "dice_result_mode"
    }
''',
    "result-mode-key",
)

replace_once(
    '''                item {
                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                        SettingSelector(
                            label = "Ayuda contextual",
                            value = preferences.helpMode.label,
                            options = CharacterHelpModeV4.entries,
                            optionLabel = { it.label },
                            onSelect = { onPreferencesChange(preferences.copy(helpMode = it)) },
                        )
                        Text(
                            "Controla si las explicaciones aparecen siempre, desde ⓘ, o se ocultan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item {
                    FontChoicePicker(
''',
    '''                item {
                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                        SettingSelector(
                            label = "Ayuda contextual",
                            value = preferences.helpMode.label,
                            options = CharacterHelpModeV4.entries,
                            optionLabel = { it.label },
                            onSelect = { onPreferencesChange(preferences.copy(helpMode = it)) },
                        )
                        Text(
                            "Controla si las explicaciones aparecen siempre, desde ⓘ, o se ocultan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                        SettingSelector(
                            label = "Resultados de dados",
                            value = preferences.diceResultMode.label,
                            options = DiceResultModeChoice.entries,
                            optionLabel = { it.label },
                            onSelect = { onPreferencesChange(preferences.copy(diceResultMode = it)) },
                        )
                        Text(
                            "El resultado compacto prioriza densidad. Dados visibles muestra los d20 obtenidos de forma prominente; ambos conservan la misma descomposición matemática.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                item {
                    FontChoicePicker(
''',
    "settings-result-mode",
)

replace_once(
    '''                    "${preferences.themeChoice.label} · ${preferences.fontChoice.label} · Texto ${preferences.fontScalePercent}% · Espacios ${preferences.spacingScalePercent}% · Ayuda ${preferences.helpMode.label}",
''',
    '''                    "${preferences.themeChoice.label} · ${preferences.fontChoice.label} · Texto ${preferences.fontScalePercent}% · Espacios ${preferences.spacingScalePercent}% · Ayuda ${preferences.helpMode.label} · Dados ${preferences.diceResultMode.label}",
''',
    "preview-result-mode",
)

ui.write_text(text)

dice = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterDiceRollSuccessorV4.kt")
dice_text = dice.read_text()


def dice_replace_once(old: str, new: str, label: str) -> None:
    global dice_text
    count = dice_text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one match, found {count}")
    dice_text = dice_text.replace(old, new, 1)

dice_replace_once(
    '''        resolvedRoll?.let { roll ->
            CharacterD20ResultCardV4(roll)
        }
''',
    '''        resolvedRoll?.let { roll ->
            if (LocalUiPreferencesV4.current.diceResultMode == DiceResultModeChoice.VISIBLE_DICE) {
                CharacterVisibleDiceResultCardV4(roll)
            } else {
                CharacterD20ResultCardV4(roll)
            }
        }
''',
    "mode-switch",
)

insert_before = '''@Composable
private fun CharacterD20ResultCardV4(roll: CharacterResolvedD20Roll) {
'''
visible = '''@Composable
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
    Surface(
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            if (chosen) 2.dp else 1.dp,
            if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        color = if (chosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (chosen) 3.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("d20", style = MaterialTheme.typography.labelSmall)
            Text(value.toString(), style = MaterialTheme.typography.headlineMedium)
            if (chosen) Text("elegido", style = MaterialTheme.typography.labelSmall)
        }
    }
}

'''
if dice_text.count(insert_before) != 1:
    raise SystemExit(f"visible-result-anchor: expected exactly one match, found {dice_text.count(insert_before)}")
dice_text = dice_text.replace(insert_before, visible + insert_before, 1)
dice.write_text(dice_text)
