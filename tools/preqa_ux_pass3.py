from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]


def read(rel):
    return (ROOT / rel).read_text(encoding="utf-8")


def write(rel, text):
    (ROOT / rel).write_text(text, encoding="utf-8")


def replace_once(text, old, new, label):
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly 1 match, found {count}")
    return text.replace(old, new, 1)


# Review identity.
gradle_path = "androidApp/build.gradle.kts"
s = read(gradle_path)
s = replace_once(s, 'versionCode = 40200', 'versionCode = 40300', "versionCode")
s = replace_once(s, 'versionName = "0.4.0-preqa.2"', 'versionName = "0.4.0-preqa.3"', "versionName")
write(gradle_path, s)

# Dedicated dice navigation immediately after Combat.
nav_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterNavigationV4.kt"
s = read(nav_path)
s = replace_once(
    s,
    '    COMBAT("Combate"),\n    MANAGEMENT("Gestión"),',
    '    COMBAT("Combate"),\n    DICE("Dados"),\n    MANAGEMENT("Gestión"),',
    "dice enum",
)
s = replace_once(
    s,
    '    add(CharacterTabV4.COMBAT)\n    add(CharacterTabV4.MANAGEMENT)',
    '    add(CharacterTabV4.COMBAT)\n    add(CharacterTabV4.DICE)\n    add(CharacterTabV4.MANAGEMENT)',
    "dice visible order",
)
write(nav_path, s)

adaptive_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterAdaptiveShellV4.kt"
s = read(adaptive_path)
s = replace_once(
    s,
    '    CharacterTabV4.COMBAT -> "CO"\n    CharacterTabV4.MANAGEMENT -> "GE"',
    '    CharacterTabV4.COMBAT -> "CO"\n    CharacterTabV4.DICE -> "TD"\n    CharacterTabV4.MANAGEMENT -> "GE"',
    "dice rail mark",
)
write(adaptive_path, s)

# Remove all existing d20 buttons from the large editor and wire the single dice tab.
editor_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt"
s = read(editor_path)
pattern = re.compile(
    r'\n(?P<indent>[ \t]*)CharacterD20RollButtonV4\(\n'
    r'(?P=indent)    label = [^\n]+,\n'
    r'(?P=indent)    modifier = [^\n]+,\n'
    r'(?P=indent)\)\n'
)
s, removed_editor = pattern.subn("\n", s)
if removed_editor != 3:
    raise RuntimeError(f"embedded editor d20 controls: expected 3, removed {removed_editor}")

dice_call = '''                        CharacterTabV4.DICE -> CharacterDiceRollTabV4(
                            sheet = settingsSheet,
                            closureState = closureState,
                            combatEntries = combatDraft,
                        )
'''
s = replace_once(
    s,
    '                        CharacterTabV4.MANAGEMENT -> CharacterManagementTabV4(\n',
    dice_call + '                        CharacterTabV4.MANAGEMENT -> CharacterManagementTabV4(\n',
    "dice tab wiring",
)
write(editor_path, s)

# Remove remaining row-level dice controls.
custom_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCustomSkillsV4.kt"
s = read(custom_path)
s = replace_once(
    s,
    '            CharacterD20RollButtonV4(label = skill.name, modifier = total)\n',
    '',
    "custom skill d20",
)
write(custom_path, s)

combat_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatTabV4.kt"
s = read(combat_path)
s = replace_once(
    s,
    'import androidx.compose.foundation.layout.Arrangement\n',
    'import androidx.compose.foundation.layout.Arrangement\nimport androidx.compose.foundation.layout.Box\n',
    "combat Box import",
)
s = replace_once(
    s,
    '''                    entry.attackModifier?.let { modifierValue ->
                        CharacterD20RollButtonV4(label = entry.name, modifier = modifierValue)
                    }
''',
    '',
    "combat embedded d20",
)

# Keep the operational combat card fixed while only attacks/actions scroll.
old_combat_shell = '''    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = if (wide) 10.dp else 5.dp,
            end = if (wide) 10.dp else 5.dp,
            top = 5.dp,
            bottom = 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        item {
            CharacterCombatOperationalCardV4(
                armorClass = armorClass,
                initiative = initiative,
                speed = speed,
                sheet = sheet,
                onSheetChange = onOperationalSheetChange,
                hapticsEnabled = hapticsEnabled,
            )
        }
        item {
'''
new_combat_shell = '''    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (wide) 10.dp else 5.dp,
                    end = if (wide) 10.dp else 5.dp,
                    top = 5.dp,
                ),
        ) {
            CharacterCombatOperationalCardV4(
                armorClass = armorClass,
                initiative = initiative,
                speed = speed,
                sheet = sheet,
                onSheetChange = onOperationalSheetChange,
                hapticsEnabled = hapticsEnabled,
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = if (wide) 10.dp else 5.dp,
                end = if (wide) 10.dp else 5.dp,
                top = 0.dp,
                bottom = 88.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            item {
'''
s = replace_once(s, old_combat_shell, new_combat_shell, "combat fixed operational shell")
s = replace_once(
    s,
    '''    }

    if (editorOpen && structuralEditingEnabled) {
''',
    '''        }
    }

    if (editorOpen && structuralEditingEnabled) {
''',
    "combat shell closing",
)
write(combat_path, s)

# Replace the old per-row dialog implementation with one dedicated tab.
old_d20_path = ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterD20RollUiV4.kt"
if not old_d20_path.exists():
    raise RuntimeError("expected old CharacterD20RollUiV4.kt to exist")
old_d20_path.unlink()

dice_path = ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterDiceRollTabV4.kt"
if dice_path.exists():
    raise RuntimeError("CharacterDiceRollTabV4.kt already exists")

dice_source = r'''package io.github.mrsimkin.dndcustomaid.android

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
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.characterD20Roll
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
) {
    val targets = characterDiceTargetsV4(sheet, closureState, combatEntries)
    var selectedKey by rememberSaveable(sheet.id.toString()) { mutableStateOf<String?>(null) }
    var dieResult by rememberSaveable(sheet.id.toString(), "dice-result") { mutableStateOf<Int?>(null) }
    val selected = targets.firstOrNull { it.key == selectedKey } ?: targets.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("Tirada de dados", style = MaterialTheme.typography.titleMedium)
            if (target == null) {
                Text("No hay tiradas disponibles para esta ficha.", style = MaterialTheme.typography.bodySmall)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
        add(
            CharacterDiceTargetV4(
                key = "custom-${skill.id}",
                group = "Habilidades personalizadas",
                label = skill.name,
                context = listOfNotNull(
                    "Habilidad personalizada",
                    abilityAbbreviationDiceV4(skill.ability),
                    skill.source?.trim()?.takeIf { it.isNotEmpty() },
                ).joinToString(" · "),
                modifier = sheet.customSkillTotal(skill),
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
'''
dice_path.write_text(dice_source, encoding="utf-8")

# Fail closed if any embedded d20 component reference survives.
remaining = []
for path in (ROOT / "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android").glob("*.kt"):
    if "CharacterD20RollButtonV4" in path.read_text(encoding="utf-8"):
        remaining.append(path.name)
if remaining:
    raise RuntimeError(f"embedded d20 references still present: {remaining}")

print("Pass 03 transformation applied successfully.")
