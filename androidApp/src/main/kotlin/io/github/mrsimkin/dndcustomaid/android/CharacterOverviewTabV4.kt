package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.characterProperNameInput
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility
// Refactor F3: the PC editor's Resumen tab (identity, abilities, combat, quick magic), moved out of CharacterEditorV4.kt.

@Composable
internal fun OverviewTabV4(
    draft: CharacterEditorDraftV4,
    stored: CharacterSheet,
    projectionSheet: CharacterSheet,
    closureState: CharacterClosureState,
    wide: Boolean,
    structuralEditingEnabled: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    onOperationalSheetChange: (CharacterSheet) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
) {
    // T8_TABLE_MODE_AFFORDANCES_V4: structural references become visibly read-only; live state stays operational.
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = if (wide) 10.dp else 5.dp,
            end = if (wide) 10.dp else 5.dp,
            top = 5.dp,
            bottom = 150.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        item {
            IdentityCardV4(draft, stored, onDraftChange, structuralEditingEnabled)
        }
        item {
            CharacterClassIdentityCardV4(
                classes = draft.classes,
                onClassesChange = { onDraftChange(draft.copy(classes = it)) },
                structuralEditingEnabled = structuralEditingEnabled,
            )
        }
        item {
            AbilitiesCardV4(draft, onDraftChange, structuralEditingEnabled)
        }
        item {
            CombatCardV4(
                draft = draft,
                stored = stored,
                wide = wide,
                structuralEditingEnabled = structuralEditingEnabled,
                hapticsEnabled = closureState.hapticsEnabled,
                onDraftChange = onDraftChange,
                onOperationalSheetChange = onOperationalSheetChange,
            )
        }
        item {
            CharacterGeneralSuccessorCardsV4(
                projectionSheet = projectionSheet,
                onInspirationChange = { enabled ->
                    onOperationalSheetChange(stored.copy(inspiration = enabled))
                },
                onResourceValueChange = { resourceId, value ->
                    onOperationalSheetChange(
                        stored.copy(
                            resources = stored.resources.map { resource ->
                                if (resource.id == resourceId) resource.copy(currentValue = value) else resource
                            },
                        ),
                    )
                },
            )
        }
        item {
            CharacterGeneralClosureCardsV4(
                state = closureState,
                onStateChange = onClosureStateChange,
                wide = wide,
                structuralEditingEnabled = structuralEditingEnabled,
            )
        }
    }
}

@Composable
internal fun IdentityCardV4(
    draft: CharacterEditorDraftV4,
    stored: CharacterSheet,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    structuralEditingEnabled: Boolean,
) {
    SectionCardV4("Personaje") {
        Text("Nombre", style = MaterialTheme.typography.labelSmall)
        if (structuralEditingEnabled) {
            CompactTextFieldV4(
                value = draft.name,
                onValueChange = { onDraftChange(draft.copy(name = characterProperNameInput(it))) },
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Text(draft.name.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            "Nivel total ${draft.totalLevel()} · Último guardado ${formatSavedAtV4(stored.updatedAtEpochSeconds)}",
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
internal fun AbilitiesCardV4(
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    structuralEditingEnabled: Boolean,
) {
    SectionCardV4("Características") {
        AbilitiesRowV4(draft, onDraftChange, structuralEditingEnabled)
    }
}

@Composable
internal fun AbilitiesRowV4(
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    structuralEditingEnabled: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
    ) {
        CharacterAbility.entries.forEach { ability ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(abilityAbbreviationV4(ability), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                if (structuralEditingEnabled) {
                    CompactIntInputV4(
                        value = draft.abilityValue(ability),
                        onValueChange = { onDraftChange(draft.withAbilityValue(ability, it)) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    Text(
                        draft.abilityValue(ability).ifBlank { "—" },
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                    )
                }
                Text(
                    draft.abilityModifier(ability)?.let(::formatSignedV4) ?: "—",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
internal fun CombatCardV4(
    draft: CharacterEditorDraftV4,
    stored: CharacterSheet,
    wide: Boolean,
    structuralEditingEnabled: Boolean,
    hapticsEnabled: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    onOperationalSheetChange: (CharacterSheet) -> Unit,
) {
    if (!structuralEditingEnabled) {
        SectionCardV4("Referencia de combate") {
            CharacterCombatOperationalCardV4(
                armorClass = draft.armorClass,
                initiative = draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(),
                speed = draft.speed,
                sheet = stored,
                onSheetChange = onOperationalSheetChange,
                hapticsEnabled = hapticsEnabled,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Bono competencia", style = MaterialTheme.typography.labelSmall)
                    Text(draft.finalProficiencyBonus()?.let(::formatSignedV4) ?: "—", style = MaterialTheme.typography.bodyMedium)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Percepción pasiva", style = MaterialTheme.typography.labelSmall)
                    Text(draft.passivePerceptionTotal()?.toString() ?: "—", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        return
    }

    SectionCardV4("Referencia de combate") {
        if (wide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Referencia", style = MaterialTheme.typography.labelMedium)
                    CombatExplicitRowV4(
                        draft,
                        labels = Triple("CA", "Iniciativa", "Velocidad"),
                        values = Triple(draft.armorClass, draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(), draft.speed),
                        onFirst = { onDraftChange(draft.copy(armorClass = it)) },
                        onSecondAdjustment = { onDraftChange(draft.copy(initiativeAdjustment = it)) },
                        onThird = { onDraftChange(draft.copy(speed = it)) },
                        secondAdjustment = draft.initiativeAdjustment,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Puntos de golpe", style = MaterialTheme.typography.labelMedium)
                    TripleExplicitFieldsV4(
                        "PG actuales", draft.currentHp, { onDraftChange(draft.copy(currentHp = it)) },
                        "PG máximos", draft.maxHp, { onDraftChange(draft.copy(maxHp = it)) },
                        "PG temporales", draft.tempHp, { onDraftChange(draft.copy(tempHp = it)) },
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Referencia secundaria", style = MaterialTheme.typography.labelMedium)
                    SecondaryCombatRowV4(draft, onDraftChange)
                }
            }
        } else {
            CombatExplicitRowV4(
                draft,
                labels = Triple("CA", "Iniciativa", "Velocidad"),
                values = Triple(draft.armorClass, draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(), draft.speed),
                onFirst = { onDraftChange(draft.copy(armorClass = it)) },
                onSecondAdjustment = { onDraftChange(draft.copy(initiativeAdjustment = it)) },
                onThird = { onDraftChange(draft.copy(speed = it)) },
                secondAdjustment = draft.initiativeAdjustment,
            )
            TripleExplicitFieldsV4(
                "PG actuales", draft.currentHp, { onDraftChange(draft.copy(currentHp = it)) },
                "PG máximos", draft.maxHp, { onDraftChange(draft.copy(maxHp = it)) },
                "PG temporales", draft.tempHp, { onDraftChange(draft.copy(tempHp = it)) },
            )
            SecondaryCombatRowV4(draft, onDraftChange)
        }
    }
}

@Composable
internal fun CombatExplicitRowV4(
    draft: CharacterEditorDraftV4,
    labels: Triple<String, String, String>,
    values: Triple<String, String, String>,
    onFirst: (String) -> Unit,
    onSecondAdjustment: (String) -> Unit,
    onThird: (String) -> Unit,
    secondAdjustment: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompactIntFieldV4(labels.first, values.first, onFirst, Modifier.weight(1f))
        DerivedValueCellV4(
            label = labels.second,
            total = values.second,
            adjustment = secondAdjustment,
            breakdownLines = listOf(
                "Destreza ${draft.abilityModifier(CharacterAbility.DEXTERITY)?.let(::formatSignedV4) ?: "—"}",
            ),
            onAdjustmentChange = onSecondAdjustment,
            modifier = Modifier.weight(1f),
        )
        SpeedFieldV4(
            label = labels.third,
            value = values.third,
            onValueChange = onThird,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun SpeedFieldV4(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dialogOpen by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        CompactFieldLabelV4(label)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 34.dp)
                .clickable { dialogOpen = true },
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 3.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(formatCharacterDistanceFeetV4(value), style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            }
        }
    }

    if (dialogOpen) {
        var pending by remember(dialogOpen, value) { mutableStateOf(value) }
        CharacterImeSafeEditorDialog(
            title = label,
            onCancel = { dialogOpen = false },
            onSave = {
                onValueChange(pending)
                dialogOpen = false
            },
        ) {
            Text("Valor canónico en pies", style = MaterialTheme.typography.labelMedium)
            CompactIntInputV4(
                value = pending,
                onValueChange = { pending = it },
                modifier = Modifier.width(110.dp),
                placeholder = "0",
            )
            Text(
                "Vista: ${formatCharacterDistanceFeetV4(pending)}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
internal fun TripleExplicitFieldsV4(
    firstLabel: String,
    firstValue: String,
    onFirst: (String) -> Unit,
    secondLabel: String,
    secondValue: String,
    onSecond: (String) -> Unit,
    thirdLabel: String,
    thirdValue: String,
    onThird: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.Top,
    ) {
        CompactIntFieldV4(firstLabel, firstValue, onFirst, Modifier.weight(1f), signed = true)
        CompactIntFieldV4(secondLabel, secondValue, onSecond, Modifier.weight(1f))
        CompactIntFieldV4(thirdLabel, thirdValue, onThird, Modifier.weight(1f), signed = true)
    }
}

@Composable
internal fun SecondaryCombatRowV4(
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DerivedValueCellV4(
            label = "Bono competencia",
            total = draft.finalProficiencyBonus()?.let(::formatSignedV4).orEmpty(),
            adjustment = draft.proficiencyBonusAdjustment,
            breakdownLines = listOf(
                "Nivel total ${draft.totalLevel()}",
                "Bono estándar ${formatSignedV4(draft.standardProficiencyBonus())}",
            ),
            onAdjustmentChange = { onDraftChange(draft.copy(proficiencyBonusAdjustment = it)) },
            modifier = Modifier.weight(1f),
        )
        DerivedValueCellV4(
            label = "Percepción pasiva",
            total = draft.passivePerceptionTotal()?.toString().orEmpty(),
            adjustment = draft.passivePerceptionAdjustment,
            breakdownLines = listOf(
                "Percepción ${draft.skillTotal(SkillKey.PERCEPTION)?.let(::formatSignedV4) ?: "—"}",
                "Base pasiva +10",
            ),
            onAdjustmentChange = { onDraftChange(draft.copy(passivePerceptionAdjustment = it)) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
internal fun QuickMagicCardV4(
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    var configureSlots by remember { mutableStateOf(false) }

    SectionCardV4("Lanzamiento de Conjuros") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
            verticalAlignment = Alignment.Top,
        ) {
            CompactIntFieldV4(
                label = "CD conjuros",
                value = draft.spellSaveDc,
                onValueChange = { onDraftChange(draft.copy(spellSaveDc = it)) },
                modifier = Modifier.weight(1f),
            )
            CompactIntFieldV4(
                label = "Ataque mágico",
                value = draft.spellAttackModifier,
                onValueChange = { onDraftChange(draft.copy(spellAttackModifier = it)) },
                modifier = Modifier.weight(1f),
                signed = true,
            )
            SpellcastingAbilitySelectorV4(
                ability = draft.spellcastingAbility,
                onChange = { onDraftChange(draft.copy(spellcastingAbility = it)) },
                modifier = Modifier.weight(1f),
            )
        }

        val activeSlots = draft.spellSlots.filter { (it.total.toIntOrNull() ?: 0) > 0 }
        if (activeSlots.isEmpty()) {
            Text("Sin espacios de conjuro configurados.", style = MaterialTheme.typography.bodySmall)
        } else {
            activeSlots.forEach { slot ->
                SpellSlotRowV4(
                    slot = slot,
                    onSpentChange = { spent ->
                        onDraftChange(draft.withSpellSlot(slot.copy(spent = spent)))
                    },
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        ) {
            TextButton(onClick = { configureSlots = true }) {
                Text("Configurar espacios")
            }
            TextButton(
                onClick = {
                    onDraftChange(
                        draft.copy(
                            spellSlots = draft.spellSlots.map { it.copy(spent = 0) },
                        ),
                    )
                },
                enabled = activeSlots.any { it.spent > 0 },
            ) {
                Text("Restaurar espacios")
            }
        }
    }

    if (configureSlots) {
        var pendingTotals by remember(configureSlots) {
            mutableStateOf(
                (1..9).map { level ->
                    draft.spellSlotFor(level).total
                },
            )
        }
        CharacterImeSafeEditorDialog(
            title = "Configurar espacios",
            onCancel = { configureSlots = false },
            onSave = {
                val updated = (1..9).map { level ->
                    val old = draft.spellSlotFor(level)
                    val total = pendingTotals[level - 1].toIntOrNull()?.coerceAtLeast(0) ?: 0
                    old.copy(total = total.toString(), spent = old.spent.coerceIn(0, total))
                }
                onDraftChange(draft.copy(spellSlots = updated))
                configureSlots = false
            },
        ) {
            (1..9).forEachIndexed { index, level ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                ) {
                    Text("Nivel $level", modifier = Modifier.weight(1f))
                    CompactIntInputV4(
                        value = pendingTotals[index],
                        onValueChange = { value ->
                            pendingTotals = pendingTotals.mapIndexed { itemIndex, existing ->
                                if (itemIndex == index) value else existing
                            }
                        },
                        modifier = Modifier.width(70.dp),
                        placeholder = "0",
                    )
                }
            }
        }
    }
}

@Composable
internal fun SpellcastingAbilitySelectorV4(
    ability: SpellcastingAbility,
    onChange: (SpellcastingAbility) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        CompactFieldLabelV4("Aptitud mágica")
        Box {
            CompactMenuSurfaceV4(
                text = spellcastingAbilityLabelV4(ability),
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                SpellcastingAbility.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(spellcastingAbilityLabelV4(option)) },
                        onClick = {
                            onChange(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
internal fun SpellSlotRowV4(
    slot: SpellSlotDraftV4,
    onSpentChange: (Int) -> Unit,
) {
    val total = slot.total.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val spent = slot.spent.coerceIn(0, total)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        verticalAlignment = Alignment.Top,
    ) {
        Text("Nivel ${slot.level}", modifier = Modifier.width(55.dp), style = MaterialTheme.typography.labelMedium)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            (0 until total).toList().chunked(8).forEach { indices ->
                Row(horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
                    indices.forEach { index ->
                        val isSpent = index < spent
                        val borderColor = if (isSpent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        Surface(
                            modifier = Modifier
                                .size(26.dp)
                                .clickable {
                                    val newSpent = if (isSpent) index else index + 1
                                    onSpentChange(newSpent.coerceIn(0, total))
                                },
                            shape = CircleShape,
                            border = BorderStroke(1.5.dp, borderColor),
                            color = if (isSpent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        ) {}
                    }
                }
            }
        }
        Text("$spent/$total", style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}

@Composable
internal fun DerivedValueCellV4(
    label: String,
    total: String,
    adjustment: String,
    breakdownLines: List<String>,
    onAdjustmentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        CompactFieldLabelV4(label)
        DerivedTotalControlV4(
            total = total,
            adjustment = adjustment,
            dialogTitle = label,
            breakdownLines = breakdownLines,
            onAdjustmentChange = onAdjustmentChange,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
internal fun DerivedTotalControlV4(
    total: String,
    adjustment: String,
    dialogTitle: String,
    breakdownLines: List<String>,
    onAdjustmentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var dialogOpen by remember { mutableStateOf(false) }
    val adjustmentValue = parseOptionalAdjustmentV4(adjustment) ?: 0

    Surface(
        modifier = modifier
            .heightIn(min = 34.dp)
            .clickable(enabled = enabled) { dialogOpen = true },
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(total.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                if (adjustmentValue != 0) {
                    Text(
                        "*",
                        modifier = Modifier.padding(start = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                    )
                }
            }
        }
    }

    if (enabled && dialogOpen) {
        var pendingAdjustment by remember(dialogOpen, adjustment) { mutableStateOf(adjustment) }
        CharacterImeSafeEditorDialog(
            title = dialogTitle,
            onCancel = { dialogOpen = false },
            onSave = {
                onAdjustmentChange(pendingAdjustment)
                dialogOpen = false
            },
        ) {
            breakdownLines.forEach { line ->
                Text(line, style = MaterialTheme.typography.bodyMedium)
            }
            Text("Ajuste adicional", style = MaterialTheme.typography.labelMedium)
            CompactIntInputV4(
                value = pendingAdjustment,
                onValueChange = { pendingAdjustment = it },
                modifier = Modifier.width(110.dp),
                signed = true,
                placeholder = "0",
            )
            Text("Total actual ${total.ifBlank { "—" }}", style = MaterialTheme.typography.labelMedium)
        }
    }
}
