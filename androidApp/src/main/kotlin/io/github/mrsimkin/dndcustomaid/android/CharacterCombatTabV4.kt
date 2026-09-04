package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess
import io.github.mrsimkin.dndcustomaid.shared.character.withQuickAccess
import kotlin.math.abs
import kotlin.uuid.Uuid

@Composable
internal fun CharacterCombatTabV4(
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
    structuralEditingEnabled: Boolean,
    wide: Boolean,
    hapticsEnabled: Boolean = true,
) {
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorName by rememberSaveable { mutableStateOf("") }
    var editorType by rememberSaveable { mutableStateOf(CharacterCombatEntryType.ATTACK.name) }
    var editorAttackModifier by rememberSaveable { mutableStateOf("") }
    var editorDamageEffect by rememberSaveable { mutableStateOf("") }
    var editorRange by rememberSaveable { mutableStateOf("") }
    var editorNotes by rememberSaveable { mutableStateOf("") }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)

    fun beginAdd() {
        if (!structuralEditingEnabled) return
        editingId = null
        editorName = ""
        editorType = CharacterCombatEntryType.ATTACK.name
        editorAttackModifier = ""
        editorDamageEffect = ""
        editorRange = ""
        editorNotes = ""
        editorOpen = true
    }

    fun beginEdit(entry: CharacterCombatEntry) {
        if (!structuralEditingEnabled) return
        editingId = entry.id.toString()
        editorName = entry.name
        editorType = entry.type.name
        editorAttackModifier = entry.attackModifier?.toString().orEmpty()
        editorDamageEffect = entry.damageEffect
        editorRange = entry.rangeText.orEmpty()
        editorNotes = entry.notes.orEmpty()
        editorOpen = true
    }

    fun move(index: Int, offset: Int): Boolean {
        if (!structuralEditingEnabled) return false
        val target = index + offset
        if (target !in entries.indices) return false
        val reordered = entries.toMutableList()
        val item = reordered.removeAt(index)
        reordered.add(target, item)
        onEntriesChange(reordered.mapIndexed { order, entry -> entry.copy(sortOrder = order) })
        return true
    }

    LazyColumn(
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 7.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Ataques y acciones", style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text("+ Añadir") }
                    }
                    if (entries.isEmpty()) {
                        Text(
                            "Sin ataques o acciones registrados. Puedes añadir armas, acciones, reacciones o referencias resumidas de conjuros y otros efectos.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    } else {
                        val columns = if (wide) 2 else 1
                        entries.chunked(columns).forEach { rowEntries ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                rowEntries.forEach { entry ->
                                    val index = entries.indexOfFirst { it.id == entry.id }
                                    CombatEntryCardV4(
                                        entry = entry,
                                        favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, entry.id),
                                        favoriteEnabled = structuralEditingEnabled && entry.id in persistedEntryIds,
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
                                        structuralEditingEnabled = structuralEditingEnabled,
                                        onHaptic = haptic,
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                repeat(columns - rowEntries.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (editorOpen && structuralEditingEnabled) {
        val selectedType = runCatching { CharacterCombatEntryType.valueOf(editorType) }
            .getOrDefault(CharacterCombatEntryType.ATTACK)
        val parsedAttack = editorAttackModifier.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
        val attackValid = editorAttackModifier.trim().isEmpty() || parsedAttack != null
        val valid = editorName.trim().isNotEmpty() && attackValid

        CombatEntryEditorDialogV4(
            title = if (editingId == null) "Añadir ataque o acción" else "Editar ataque o acción",
            name = editorName,
            type = selectedType,
            attackModifier = editorAttackModifier,
            damageEffect = editorDamageEffect,
            range = editorRange,
            notes = editorNotes,
            valid = valid,
            onNameChange = { editorName = it },
            onTypeChange = { editorType = it.name },
            onAttackModifierChange = { editorAttackModifier = sanitizeSignedIntV4(it) },
            onDamageEffectChange = { editorDamageEffect = it },
            onRangeChange = { editorRange = it },
            onNotesChange = { editorNotes = it },
            onDismiss = { editorOpen = false },
            onApply = {
                val existing = editingId?.let { id -> entries.firstOrNull { it.id.toString() == id } }
                val entry = CharacterCombatEntry(
                    id = existing?.id ?: Uuid.random(),
                    name = editorName.trim(),
                    type = selectedType,
                    attackModifier = parsedAttack,
                    damageEffect = editorDamageEffect,
                    rangeText = editorRange.trim().takeIf { it.isNotEmpty() },
                    notes = editorNotes.trim().takeIf { it.isNotEmpty() },
                    sortOrder = existing?.sortOrder ?: entries.size,
                )
                val updated = if (existing == null) {
                    entries + entry
                } else {
                    entries.map { if (it.id == existing.id) entry else it }
                }
                onEntriesChange(updated.mapIndexed { order, item -> item.copy(sortOrder = order) })
                editorOpen = false
            },
        )
    }

    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->
        val target = entries.firstOrNull { it.id.toString() == id }
        if (target != null) {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "ataque o acción",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    onEntriesChange(
                        entries.filterNot { it.id == target.id }
                            .mapIndexed { order, item -> item.copy(sortOrder = order) },
                    )
                    deleteId = null
                },
            )
        } else {
            deleteId = null
        }
    }
}

@Composable
private fun CombatQuickReferenceCardV4(
    armorClass: String,
    initiative: String,
    speed: String,
    currentHp: String,
    maxHp: String,
    tempHp: String,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 7.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text("Referencia rápida", style = MaterialTheme.typography.titleSmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ReadOnlyReferenceV4("CA", armorClass, Modifier.weight(1f))
                ReadOnlyReferenceV4("Iniciativa", initiative.ifBlank { "—" }, Modifier.weight(1f))
                ReadOnlyReferenceV4("Velocidad", formatSpeedCombatV4(speed), Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ReadOnlyReferenceV4("PG actuales", currentHp, Modifier.weight(1f))
                ReadOnlyReferenceV4("PG máximos", maxHp, Modifier.weight(1f))
                ReadOnlyReferenceV4("PG temporales", tempHp, Modifier.weight(1f))
            }
            Text(
                "Estos valores son referencias de la misma ficha; no son copias independientes.",
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun ReadOnlyReferenceV4(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        CompactFieldLabelV4(label)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 38.dp),
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
private fun CombatEntryCardV4(
    entry: CharacterCombatEntry,
    favorite: Boolean,
    favoriteEnabled: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onMove: (Int) -> Boolean,
    onDelete: () -> Unit,
    structuralEditingEnabled: Boolean,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    modifier: Modifier = Modifier,
) {
    var accumulatedDrag by remember(entry.id) { mutableStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }
    val dragState = CharacterDragVisualStateV4(
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
        CharacterDropIndicatorV4(visible = dragState.showDropBefore)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .characterDragFeedbackV4(dragState)
                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StableDragHandle(
                        modifier = Modifier.pointerInput(entry.id) {
                            detectDragGesturesAfterLongPress(
                            onDragStart = {
                                accumulatedDrag = 0f
                                dragging = true
                                onHaptic(CharacterHapticEventV4.DRAG_PICKUP)
                            },
                            onDragEnd = {
                                if (dragging) onHaptic(CharacterHapticEventV4.DRAG_DROP)
                                accumulatedDrag = 0f
                                dragging = false
                            },
                                onDragCancel = {
                                    accumulatedDrag = 0f
                                    dragging = false
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    accumulatedDrag += dragAmount.y
                                    while (abs(accumulatedDrag) >= reorderStepPx) {
                                        val direction = if (accumulatedDrag > 0f) 1 else -1
                                    if (onMove(direction)) {
                                        onHaptic(CharacterHapticEventV4.DRAG_STEP)
                                        accumulatedDrag -= direction * reorderStepPx
                                        } else {
                                            accumulatedDrag = 0f
                                            break
                                        }
                                    }
                                },
                            )
                        },
                        active = dragging,
                        contentDescription = "Mantén pulsado y arrastra para reordenar ${entry.name}",
                    )
                    Column(modifier = Modifier.weight(1f)) {
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
                }
                if (entry.damageEffect.isNotBlank()) {
                    Text(entry.damageEffect, style = MaterialTheme.typography.bodySmall)
                }
                entry.rangeText?.takeIf { it.isNotBlank() }?.let {
                    Text("Alcance: $it", style = MaterialTheme.typography.labelSmall)
                }
                entry.notes?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    if (structuralEditingEnabled) {
                        StableRemoveIconButton(
                            onClick = onDelete,
                            contentDescription = "Eliminar ${entry.name}",
                        )
                    }
                }
            }
        }
        CharacterDropIndicatorV4(visible = dragState.showDropAfter)
    }
}

@Composable
private fun CombatEntryEditorDialogV4(
    title: String,
    name: String,
    type: CharacterCombatEntryType,
    attackModifier: String,
    damageEffect: String,
    range: String,
    notes: String,
    valid: Boolean,
    onNameChange: (String) -> Unit,
    onTypeChange: (CharacterCombatEntryType) -> Unit,
    onAttackModifierChange: (String) -> Unit,
    onDamageEffectChange: (String) -> Unit,
    onRangeChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    var typeMenuOpen by rememberSaveable { mutableStateOf(false) }

    CharacterImeSafeEditorDialog(
        title = title,
        onCancel = onDismiss,
        onSave = onApply,
        saveEnabled = valid,
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Column {
            Text("Tipo", style = MaterialTheme.typography.labelSmall)
            androidx.compose.foundation.layout.Box {
                OutlinedButton(
                    onClick = { typeMenuOpen = true },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(combatEntryTypeLabelV4(type))
                }
                DropdownMenu(
                    expanded = typeMenuOpen,
                    onDismissRequest = { typeMenuOpen = false },
                ) {
                    CharacterCombatEntryType.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(combatEntryTypeLabelV4(option)) },
                            onClick = {
                                onTypeChange(option)
                                typeMenuOpen = false
                            },
                        )
                    }
                }
            }
        }
        OutlinedTextField(
            value = attackModifier,
            onValueChange = onAttackModifierChange,
            label = { Text("Modificador de ataque (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        OutlinedTextField(
            value = damageEffect,
            onValueChange = onDamageEffectChange,
            label = { Text("Daño / efecto") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
        )
        OutlinedTextField(
            value = range,
            onValueChange = onRangeChange,
            label = { Text("Alcance (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("Notas (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 5,
        )
        CharacterInlineValidationMessage(
            when {
                name.trim().isEmpty() -> "El nombre no puede quedar vacío."
                attackModifier.trim().isNotEmpty() && attackModifier.toIntOrNull() == null -> "El modificador de ataque debe ser un número entero."
                else -> null
            },
        )
    }
}

private fun combatEntryTypeLabelV4(type: CharacterCombatEntryType): String = when (type) {
    CharacterCombatEntryType.ATTACK -> "Ataque"
    CharacterCombatEntryType.ACTION -> "Acción"
    CharacterCombatEntryType.BONUS_ACTION -> "Acción adicional"
    CharacterCombatEntryType.REACTION -> "Reacción"
    CharacterCombatEntryType.OTHER -> "Otro"
}

private fun sanitizeSignedIntV4(raw: String): String {
    if (raw.isBlank()) return ""
    val sign = raw.firstOrNull()?.takeIf { it == '+' || it == '-' }?.toString().orEmpty()
    val digits = raw.drop(if (sign.isEmpty()) 0 else 1).filter(Char::isDigit)
    return sign + digits
}

private fun formatSpeedCombatV4(raw: String): String {
    val feet = raw.trim().toIntOrNull() ?: return raw.ifBlank { "—" }
    val metricTenths = feet * 3
    val wholeMeters = metricTenths / 10
    val remainder = abs(metricTenths % 10)
    val metric = if (remainder == 0) {
        wholeMeters.toString()
    } else {
        "$wholeMeters,$remainder"
    }
    return "$feet ft ($metric m)"
}

private fun formatSignedCombatV4(value: Int): String = if (value >= 0) "+$value" else value.toString()
