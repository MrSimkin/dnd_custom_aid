from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, found {count}")
    return text.replace(old, new, 1)

base = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")

d20_path = base / "CharacterD20RollUiV4.kt"
if d20_path.exists():
    raise RuntimeError("CharacterD20RollUiV4.kt already exists; refusing to overwrite")
d20_path.write_text(r'''package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.characterD20Roll
import kotlin.random.Random

@Composable
internal fun CharacterD20RollButtonV4(
    label: String,
    modifier: Int?,
    compactLabel: String = "d20",
) {
    if (modifier == null) return
    var dieResult by remember(label, modifier) { mutableStateOf<Int?>(null) }

    OutlinedButton(
        onClick = { dieResult = Random.nextInt(1, 21) },
        modifier = Modifier.heightIn(min = 36.dp),
        contentPadding = PaddingValues(horizontal = 7.dp, vertical = 0.dp),
    ) {
        Text(compactLabel, style = MaterialTheme.typography.labelMedium)
    }

    dieResult?.let { result ->
        val roll = characterD20Roll(result, modifier)
        AlertDialog(
            onDismissRequest = { dieResult = null },
            title = { Text("Tirada: $label") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("d20 ${roll.dieResult}", style = MaterialTheme.typography.titleMedium)
                        Text(
                            if (roll.modifier >= 0) "+ ${roll.modifier}" else "− ${-roll.modifier}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text("= ${roll.total}", style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        "Tirada simple de conveniencia. La app no interpreta ventaja/desventaja, críticos, daño, legalidad ni efectos de reglas.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { dieResult = Random.nextInt(1, 21) }) { Text("Tirar otra vez") }
            },
            dismissButton = {
                TextButton(onClick = { dieResult = null }) { Text("Cerrar") }
            },
        )
    }
}
''', encoding="utf-8")

ops_path = base / "CharacterCombatOperationalV4.kt"
if ops_path.exists():
    raise RuntimeError("CharacterCombatOperationalV4.kt already exists; refusing to overwrite")
ops_path.write_text(r'''package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterDamage
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterHealing
import io.github.mrsimkin.dndcustomaid.shared.character.setCharacterTemporaryHp
import kotlin.math.abs

private enum class CharacterHpOperationV4 {
    DAMAGE,
    HEAL,
    TEMP_HP,
}

@Composable
internal fun CharacterCombatOperationalCardV4(
    armorClass: String,
    initiative: String,
    speed: String,
    sheet: CharacterSheet,
    onSheetChange: (CharacterSheet) -> Unit,
    hapticsEnabled: Boolean,
) {
    var operationName by rememberSaveable { mutableStateOf<String?>(null) }
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("Referencia rápida", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OperationalReferenceV4("CA", armorClass, Modifier.weight(1f))
                OperationalReferenceV4("Iniciativa", initiative.ifBlank { "—" }, Modifier.weight(1f))
                OperationalReferenceV4("Velocidad", formatSpeedOperationalV4(speed), Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OperationalReferenceV4("PG actuales", sheet.currentHp.toString(), Modifier.weight(1f))
                OperationalReferenceV4("PG máximos", sheet.maxHp.toString(), Modifier.weight(1f))
                OperationalReferenceV4("PG temporales", sheet.tempHp.toString(), Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                OutlinedButton(
                    onClick = { operationName = CharacterHpOperationV4.DAMAGE.name },
                    modifier = Modifier.weight(1f),
                ) { Text("Daño") }
                OutlinedButton(
                    onClick = { operationName = CharacterHpOperationV4.HEAL.name },
                    modifier = Modifier.weight(1f),
                ) { Text("Curar") }
                OutlinedButton(
                    onClick = { operationName = CharacterHpOperationV4.TEMP_HP.name },
                    modifier = Modifier.weight(1f),
                ) { Text("PG temp.") }
            }

            if (sheet.currentHp <= 0) {
                Text("Salvaciones de muerte", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DeathSaveCounterV4(
                        label = "Éxitos",
                        value = sheet.deathSaveSuccesses,
                        onChange = { value ->
                            haptic(CharacterHapticEventV4.RESOURCE)
                            onSheetChange(sheet.copy(deathSaveSuccesses = value.coerceIn(0, 3)))
                        },
                        modifier = Modifier.weight(1f),
                    )
                    DeathSaveCounterV4(
                        label = "Fallos",
                        value = sheet.deathSaveFailures,
                        onChange = { value ->
                            haptic(CharacterHapticEventV4.RESOURCE)
                            onSheetChange(sheet.copy(deathSaveFailures = value.coerceIn(0, 3)))
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
                Text(
                    "La app solo registra el estado. No decide estabilización, muerte, reinicios ni otras consecuencias de reglas.",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }

    operationName?.let { raw ->
        val operation = runCatching { CharacterHpOperationV4.valueOf(raw) }.getOrDefault(CharacterHpOperationV4.DAMAGE)
        HpOperationDialogV4(
            operation = operation,
            onDismiss = { operationName = null },
            onApply = { amount ->
                val updated = when (operation) {
                    CharacterHpOperationV4.DAMAGE -> applyCharacterDamage(sheet, amount)
                    CharacterHpOperationV4.HEAL -> applyCharacterHealing(sheet, amount)
                    CharacterHpOperationV4.TEMP_HP -> setCharacterTemporaryHp(sheet, amount)
                }
                if (updated != sheet) {
                    haptic(CharacterHapticEventV4.RESOURCE)
                    onSheetChange(updated)
                }
                operationName = null
            },
        )
    }
}

@Composable
private fun OperationalReferenceV4(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        CompactFieldLabelV4(label)
        Surface(
            modifier = Modifier.fillMaxWidth().heightIn(min = 38.dp),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(value.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            }
        }
    }
}

@Composable
private fun DeathSaveCounterV4(
    label: String,
    value: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = { onChange(value - 1) }, enabled = value > 0) { Text("−") }
            Text("${value.coerceIn(0, 3)}/3", style = MaterialTheme.typography.titleSmall)
            OutlinedButton(onClick = { onChange(value + 1) }, enabled = value < 3) { Text("+") }
        }
    }
}

@Composable
private fun HpOperationDialogV4(
    operation: CharacterHpOperationV4,
    onDismiss: () -> Unit,
    onApply: (Int) -> Unit,
) {
    var amountText by rememberSaveable(operation.name) { mutableStateOf(if (operation == CharacterHpOperationV4.TEMP_HP) "0" else "") }
    val amount = amountText.toIntOrNull()
    val valid = amount != null && amount >= 0 && (operation == CharacterHpOperationV4.TEMP_HP || amount > 0)
    val title = when (operation) {
        CharacterHpOperationV4.DAMAGE -> "Recibir daño"
        CharacterHpOperationV4.HEAL -> "Recibir curación"
        CharacterHpOperationV4.TEMP_HP -> "Establecer PG temporales"
    }

    CharacterImeSafeEditorDialog(
        title = title,
        onCancel = onDismiss,
        onSave = { amount?.let(onApply) },
        saveEnabled = valid,
        supportingText = when (operation) {
            CharacterHpOperationV4.DAMAGE -> "El daño consume primero los PG temporales y luego reduce los PG actuales, sin bajar de 0."
            CharacterHpOperationV4.HEAL -> "La curación no supera los PG máximos guardados."
            CharacterHpOperationV4.TEMP_HP -> "Escribe el valor exacto que deseas registrar; 0 los elimina."
        },
    ) {
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it.filter(Char::isDigit) },
            label = { Text(if (operation == CharacterHpOperationV4.TEMP_HP) "PG temporales" else "Cantidad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        CharacterInlineValidationMessage(
            if (amountText.isNotBlank() && !valid) {
                if (operation == CharacterHpOperationV4.TEMP_HP) "Escribe un número igual o mayor que 0." else "Escribe una cantidad mayor que 0."
            } else null,
        )
    }
}

private fun formatSpeedOperationalV4(raw: String): String {
    val feet = raw.trim().toIntOrNull() ?: return raw.ifBlank { "—" }
    val metricTenths = feet * 3
    val wholeMeters = metricTenths / 10
    val remainder = abs(metricTenths % 10)
    val metric = if (remainder == 0) wholeMeters.toString() else "$wholeMeters,$remainder"
    return "$feet ft ($metric m)"
}
''', encoding="utf-8")

combat_path = base / "CharacterCombatTabV4.kt"
combat = combat_path.read_text(encoding="utf-8")
combat = replace_once(
    combat,
    "import androidx.compose.ui.text.input.KeyboardType\n",
    "import androidx.compose.ui.text.input.KeyboardType\nimport androidx.compose.ui.text.style.TextOverflow\n",
    "combat TextOverflow import",
)
combat = replace_once(
    combat,
    """import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
""",
    """import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess
import io.github.mrsimkin.dndcustomaid.shared.character.withQuickAccess
""",
    "combat domain imports",
)
combat = replace_once(
    combat,
    """internal fun CharacterCombatTabV4(
    armorClass: String,
    initiative: String,
    speed: String,
    currentHp: String,
    maxHp: String,
    tempHp: String,
    entries: List<CharacterCombatEntry>,
    onEntriesChange: (List<CharacterCombatEntry>) -> Unit,
    wide: Boolean,
    hapticsEnabled: Boolean = true,
) {
""",
    """internal fun CharacterCombatTabV4(
    armorClass: String,
    initiative: String,
    speed: String,
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    persistedEntryIds: Set<Uuid>,
    entries: List<CharacterCombatEntry>,
    onEntriesChange: (List<CharacterCombatEntry>) -> Unit,
    onOperationalSheetChange: (CharacterSheet) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
    hapticsEnabled: Boolean = true,
) {
""",
    "combat signature",
)
combat = replace_once(
    combat,
    """        item {
            CombatQuickReferenceCardV4(
                armorClass = armorClass,
                initiative = initiative,
                speed = speed,
                currentHp = currentHp,
                maxHp = maxHp,
                tempHp = tempHp,
            )
        }
""",
    """        item {
            CharacterCombatOperationalCardV4(
                armorClass = armorClass,
                initiative = initiative,
                speed = speed,
                sheet = sheet,
                onSheetChange = onOperationalSheetChange,
                hapticsEnabled = hapticsEnabled,
            )
        }
""",
    "operational card call",
)
combat = replace_once(
    combat,
    """                                    CombatEntryCardV4(
                                        entry = entry,
                                        onEdit = { beginEdit(entry) },
                                        onMove = { offset -> move(index, offset) },
                                        onDelete = { deleteId = entry.id.toString() },
                                        onHaptic = haptic,
                                        modifier = Modifier.weight(1f),
                                    )
""",
    """                                    CombatEntryCardV4(
                                        entry = entry,
                                        favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, entry.id),
                                        favoriteEnabled = entry.id in persistedEntryIds,
                                        onFavoriteChange = { enabled ->
                                            onClosureStateChange(
                                                closureState.withQuickAccess(
                                                    CharacterQuickAccessKind.COMBAT_ENTRY,
                                                    entry.id,
                                                    enabled,
                                                ),
                                            )
                                        },
                                        onEdit = { beginEdit(entry) },
                                        onMove = { offset -> move(index, offset) },
                                        onDelete = { deleteId = entry.id.toString() },
                                        onHaptic = haptic,
                                        modifier = Modifier.weight(1f),
                                    )
""",
    "combat entry call",
)
combat = replace_once(
    combat,
    """private fun CombatEntryCardV4(
    entry: CharacterCombatEntry,
    onEdit: () -> Unit,
    onMove: (Int) -> Boolean,
    onDelete: () -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    modifier: Modifier = Modifier,
) {
""",
    """private fun CombatEntryCardV4(
    entry: CharacterCombatEntry,
    favorite: Boolean,
    favoriteEnabled: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onMove: (Int) -> Boolean,
    onDelete: () -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    modifier: Modifier = Modifier,
) {
""",
    "combat entry signature",
)
combat = replace_once(
    combat,
    """    val dragState = CharacterDragVisualStateV4(
        active = dragging,
        offsetY = accumulatedDrag,
        showDropBefore = dragging && accumulatedDrag < 0f,
        showDropAfter = dragging && accumulatedDrag > 0f,
    )

    Column(modifier = modifier.fillMaxWidth()) {
""",
    """    val dragState = CharacterDragVisualStateV4(
        active = dragging,
        offsetY = accumulatedDrag,
        showDropBefore = dragging && accumulatedDrag < 0f,
        showDropAfter = dragging && accumulatedDrag > 0f,
    )
    val glance = listOfNotNull(
        combatEntryTypeLabelV4(entry.type),
        entry.attackModifier?.let { "Ataque ${formatSignedCombatV4(it)}" },
        entry.damageEffect.trim().takeIf { it.isNotEmpty() },
    ).joinToString(" · ")

    Column(modifier = modifier.fillMaxWidth()) {
""",
    "combat glance state",
)
combat = replace_once(
    combat,
    """                    Column(modifier = Modifier.weight(1f)) {
                        Text(entry.name, style = MaterialTheme.typography.labelLarge)
                        Text(combatEntryTypeLabelV4(entry.type), style = MaterialTheme.typography.labelSmall)
                    }
                    entry.attackModifier?.let {
                        Text("Ataque ${formatSignedCombatV4(it)}", style = MaterialTheme.typography.labelMedium)
                    }
""",
    """                    Column(modifier = Modifier.weight(1f)) {
                        Text(entry.name, style = MaterialTheme.typography.labelLarge)
                        Text(
                            glance,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    entry.attackModifier?.let { modifierValue ->
                        CharacterD20RollButtonV4(label = entry.name, modifier = modifierValue)
                    }
                    TextButton(
                        onClick = { onFavoriteChange(!favorite) },
                        enabled = favoriteEnabled,
                        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                    ) {
                        Text(if (favorite) "★" else "☆")
                    }
""",
    "combat row glance and actions",
)
combat_path.write_text(combat, encoding="utf-8")

editor_path = base / "CharacterEditorV4.kt"
editor = editor_path.read_text(encoding="utf-8")
editor = replace_once(
    editor,
    """    fun persistOperationalSheet(updated: CharacterSheet) {
        if (updated == stored) return
        stored = repository.saveCharacter(updated)
        savedMessage = "Guardado"
    }
""",
    """    fun persistOperationalSheet(updated: CharacterSheet) {
        if (updated == stored) return
        stored = repository.saveCharacter(updated)
        savedMessage = "Guardado"
    }

    fun persistCombatOperationalSheet(updated: CharacterSheet) {
        if (updated == stored) return
        val previous = stored
        stored = repository.saveCharacter(updated)
        if (stored.currentHp != previous.currentHp || stored.tempHp != previous.tempHp) {
            draft = draft.copy(
                currentHp = stored.currentHp.toString(),
                tempHp = stored.tempHp.toString(),
            )
        }
        savedMessage = "Guardado"
    }
""",
    "combat operational persistence",
)
editor = replace_once(
    editor,
    """                        CharacterTabV4.COMBAT -> CharacterCombatTabV4(
                            armorClass = draft.armorClass,
                            initiative = draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(),
                            speed = draft.speed,
                            currentHp = draft.currentHp,
                            maxHp = draft.maxHp,
                            tempHp = draft.tempHp,
                            entries = combatEntries,
                            onEntriesChange = ::updateCombatEntries,
                            hapticsEnabled = closureState.hapticsEnabled,
                            wide = wide,
                        )
""",
    """                        CharacterTabV4.COMBAT -> CharacterCombatTabV4(
                            armorClass = draft.armorClass,
                            initiative = draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(),
                            speed = draft.speed,
                            sheet = stored,
                            closureState = closureState,
                            persistedEntryIds = stored.combatEntries.mapTo(mutableSetOf()) { it.id },
                            entries = combatEntries,
                            onEntriesChange = ::updateCombatEntries,
                            onOperationalSheetChange = ::persistCombatOperationalSheet,
                            onClosureStateChange = ::persistClosureState,
                            hapticsEnabled = closureState.hapticsEnabled,
                            wide = wide,
                        )
""",
    "combat editor call",
)
editor = replace_once(
    editor,
    """            DerivedTotalControlV4(
                total = draft.savingThrowTotal(ability)?.let(::formatSignedV4).orEmpty(),
                adjustment = save.adjustment,
                dialogTitle = "Salvación ${abilityAbbreviationV4(ability)}",
                breakdownLines = listOf(
                    "${abilityAbbreviationV4(ability)} ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                    if (save.proficient) {
                        "Competencia ${proficiency?.let(::formatSignedV4) ?: "—"}"
                    } else {
                        "Sin competencia +0"
                    },
                ),
                onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },
                modifier = Modifier.weight(1f),
            )
            SaveProficiencyToggleV4(
""",
    """            DerivedTotalControlV4(
                total = draft.savingThrowTotal(ability)?.let(::formatSignedV4).orEmpty(),
                adjustment = save.adjustment,
                dialogTitle = "Salvación ${abilityAbbreviationV4(ability)}",
                breakdownLines = listOf(
                    "${abilityAbbreviationV4(ability)} ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                    if (save.proficient) {
                        "Competencia ${proficiency?.let(::formatSignedV4) ?: "—"}"
                    } else {
                        "Sin competencia +0"
                    },
                ),
                onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },
                modifier = Modifier.weight(1f),
            )
            CharacterD20RollButtonV4(
                label = "Salvación ${abilityAbbreviationV4(ability)}",
                modifier = draft.savingThrowTotal(ability),
            )
            SaveProficiencyToggleV4(
""",
    "standard save dice button",
)
editor = replace_once(
    editor,
    """        DerivedTotalControlV4(
            total = draft.skillTotal(skill.key)?.let(::formatSignedV4).orEmpty(),
            adjustment = skill.adjustment,
            dialogTitle = skillLabelV4(skill.key),
            breakdownLines = listOf(
                "${abilityAbbreviationV4(skill.key.ability)} ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                "${trainingLabelV4(skill.training)} ${proficiencyContribution?.let(::formatSignedV4) ?: "—"}",
            ),
            onAdjustmentChange = { onDraftChange(draft.withSkill(skill.copy(adjustment = it))) },
            modifier = Modifier.width(58.dp),
        )
        TrainingSelectorV4(
""",
    """        DerivedTotalControlV4(
            total = draft.skillTotal(skill.key)?.let(::formatSignedV4).orEmpty(),
            adjustment = skill.adjustment,
            dialogTitle = skillLabelV4(skill.key),
            breakdownLines = listOf(
                "${abilityAbbreviationV4(skill.key.ability)} ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                "${trainingLabelV4(skill.training)} ${proficiencyContribution?.let(::formatSignedV4) ?: "—"}",
            ),
            onAdjustmentChange = { onDraftChange(draft.withSkill(skill.copy(adjustment = it))) },
            modifier = Modifier.width(58.dp),
        )
        CharacterD20RollButtonV4(
            label = skillLabelV4(skill.key),
            modifier = draft.skillTotal(skill.key),
        )
        TrainingSelectorV4(
""",
    "standard skill dice button",
)
editor = replace_once(
    editor,
    """                DerivedTotalControlV4(
                    total = draft.savingThrowTotal(ability)?.let(::formatSignedV4).orEmpty(),
                    adjustment = save.adjustment,
                    dialogTitle = "Salvación $abbreviation",
                    breakdownLines = listOf(
                        "$abbreviation ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                        if (save.proficient) {
                            "Competencia ${proficiency?.let(::formatSignedV4) ?: "—"}"
                        } else {
                            "Sin competencia +0"
                        },
                    ),
                    onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },
                    modifier = Modifier.weight(1f),
                )
                SaveProficiencyToggleV4(
""",
    """                DerivedTotalControlV4(
                    total = draft.savingThrowTotal(ability)?.let(::formatSignedV4).orEmpty(),
                    adjustment = save.adjustment,
                    dialogTitle = "Salvación $abbreviation",
                    breakdownLines = listOf(
                        "$abbreviation ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                        if (save.proficient) {
                            "Competencia ${proficiency?.let(::formatSignedV4) ?: "—"}"
                        } else {
                            "Sin competencia +0"
                        },
                    ),
                    onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },
                    modifier = Modifier.weight(1f),
                )
                CharacterD20RollButtonV4(
                    label = "Salvación $abbreviation",
                    modifier = draft.savingThrowTotal(ability),
                )
                SaveProficiencyToggleV4(
""",
    "attribute save dice button",
)
editor_path.write_text(editor, encoding="utf-8")

custom_path = base / "CharacterCustomSkillsV4.kt"
custom = custom_path.read_text(encoding="utf-8")
custom = replace_once(
    custom,
    """            Text(if (total >= 0) "+$total" else total.toString(), style = MaterialTheme.typography.titleSmall)
            TextButton(onClick = onDelete) { Text("Eliminar") }
""",
    """            Text(if (total >= 0) "+$total" else total.toString(), style = MaterialTheme.typography.titleSmall)
            CharacterD20RollButtonV4(label = skill.name, modifier = total)
            TextButton(onClick = onDelete) { Text("Eliminar") }
""",
    "custom skill dice button",
)
custom_path.write_text(custom, encoding="utf-8")

print("Batch E3 combat/favorites/d20 integration applied.")
