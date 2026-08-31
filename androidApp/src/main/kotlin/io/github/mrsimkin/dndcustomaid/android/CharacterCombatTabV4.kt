package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import kotlin.uuid.Uuid

@Composable
internal fun CharacterCombatTabV4(
    armorClass: String,
    initiative: String,
    speed: String,
    currentHp: String,
    maxHp: String,
    tempHp: String,
    entries: List<CharacterCombatEntry>,
    onEntriesChange: (List<CharacterCombatEntry>) -> Unit,
    wide: Boolean,
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

    fun beginAdd() {
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
        editingId = entry.id.toString()
        editorName = entry.name
        editorType = entry.type.name
        editorAttackModifier = entry.attackModifier?.toString().orEmpty()
        editorDamageEffect = entry.damageEffect
        editorRange = entry.rangeText.orEmpty()
        editorNotes = entry.notes.orEmpty()
        editorOpen = true
    }

    fun move(index: Int, offset: Int) {
        val target = index + offset
        if (target !in entries.indices) return
        val reordered = entries.toMutableList()
        val item = reordered.removeAt(index)
        reordered.add(target, item)
        onEntriesChange(reordered.mapIndexed { order, entry -> entry.copy(sortOrder = order) })
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
            bottom = 170.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        item {
            CombatQuickReferenceCardV4(
                armorClass = armorClass,
                initiative = initiative,
                speed = speed,
                currentHp = currentHp,
                maxHp = maxHp,
                tempHp = tempHp,
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
                        TextButton(onClick = ::beginAdd) { Text("+ Añadir") }
                    }
                    if (entries.isEmpty()) {
                        Text(
                            "Sin ataques o acciones registrados. Puedes añadir armas, acciones, reacciones o referencias resumidas de conjuros y otros efectos.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    } else {
                        entries.forEachIndexed { index, entry ->
                            CombatEntryCardV4(
                                entry = entry,
                                canMoveUp = index > 0,
                                canMoveDown = index < entries.lastIndex,
                                onEdit = { beginEdit(entry) },
                                onMoveUp = { move(index, -1) },
                                onMoveDown = { move(index, 1) },
                                onDelete = { deleteId = entry.id.toString() },
                            )
                        }
                    }
                }
            }
        }
    }

    if (editorOpen) {
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

    deleteId?.let { id ->
        val target = entries.firstOrNull { it.id.toString() == id }
        if (target != null) {
            AlertDialog(
                onDismissRequest = { deleteId = null },
                title = { Text("Eliminar ataque o acción") },
                text = { Text("¿Eliminar «${target.name}»? Esta acción se aplicará al guardar la ficha.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEntriesChange(
                                entries.filterNot { it.id == target.id }
                                    .mapIndexed { order, item -> item.copy(sortOrder = order) },
                            )
                            deleteId = null
                        },
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { deleteId = null }) { Text("Cancelar") }
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
                verticalAlignment = Alignment.Top,
            ) {
                ReadOnlyReferenceV4("CA", armorClass, Modifier.weight(1f))
                ReadOnlyReferenceV4("Iniciativa", initiative.ifBlank { "—" }, Modifier.weight(1f))
                ReadOnlyReferenceV4("Velocidad", speed, Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Top,
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
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onEdit: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(entry.name, style = MaterialTheme.typography.labelLarge)
                    Text(combatEntryTypeLabelV4(entry.type), style = MaterialTheme.typography.labelSmall)
                }
                entry.attackModifier?.let {
                    Text("Ataque ${formatSignedCombatV4(it)}", style = MaterialTheme.typography.labelMedium)
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
                TextButton(onClick = onMoveUp, enabled = canMoveUp, contentPadding = PaddingValues(horizontal = 7.dp)) {
                    Text("↑")
                }
                TextButton(onClick = onMoveDown, enabled = canMoveDown, contentPadding = PaddingValues(horizontal = 7.dp)) {
                    Text("↓")
                }
                TextButton(onClick = onEdit, contentPadding = PaddingValues(horizontal = 7.dp)) {
                    Text("Editar")
                }
                TextButton(onClick = onDelete, contentPadding = PaddingValues(horizontal = 7.dp)) {
                    Text("Eliminar")
                }
            }
        }
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 480.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
                item {
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
                }
                item {
                    OutlinedTextField(
                        value = attackModifier,
                        onValueChange = onAttackModifierChange,
                        label = { Text("Modificador de ataque (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
                item {
                    OutlinedTextField(
                        value = damageEffect,
                        onValueChange = onDamageEffectChange,
                        label = { Text("Daño / efecto") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
                    )
                }
                item {
                    OutlinedTextField(
                        value = range,
                        onValueChange = onRangeChange,
                        label = { Text("Alcance (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = onNotesChange,
                        label = { Text("Notas (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 5,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onApply, enabled = valid) { Text("Aplicar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
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

private fun formatSignedCombatV4(value: Int): String = if (value >= 0) "+$value" else value.toString()
