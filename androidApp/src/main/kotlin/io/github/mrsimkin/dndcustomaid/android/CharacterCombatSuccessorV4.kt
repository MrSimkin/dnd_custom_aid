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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatDamageProfile
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntryType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageComponent
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDamageComponentKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.characterCombatEntryTypeSpanishLabel
import io.github.mrsimkin.dndcustomaid.shared.character.characterDamageSummary
import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess
import io.github.mrsimkin.dndcustomaid.shared.character.parseCharacterDiceExpression
import io.github.mrsimkin.dndcustomaid.shared.character.withQuickAccess
import kotlin.math.abs
import kotlin.uuid.Uuid

@Composable
internal fun CharacterCombatSuccessorTabV4(
    armorClass: String,
    initiative: String,
    speed: String,
    sheet: CharacterSheet,
    closureState: CharacterClosureState,
    persistedEntryIds: Set<Uuid>,
    entries: List<CharacterCombatEntry>,
    damageProfiles: List<CharacterCombatDamageProfile>,
    onEntriesChange: (List<CharacterCombatEntry>) -> Unit,
    onDamageProfilesChange: (List<CharacterCombatDamageProfile>) -> Unit,
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
    var editorRange by rememberSaveable { mutableStateOf("") }
    var editorNotes by rememberSaveable { mutableStateOf("") }
    var editorDamageJson by rememberSaveable { mutableStateOf("[]") }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)

    fun damageFor(entryId: Uuid): List<CharacterDamageComponent> =
        damageProfiles.firstOrNull { it.combatEntryId == entryId }?.components.orEmpty()

    fun beginAdd() {
        if (!structuralEditingEnabled) return
        editingId = null
        editorName = ""
        editorType = CharacterCombatEntryType.ATTACK.name
        editorAttackModifier = ""
        editorRange = ""
        editorNotes = ""
        editorDamageJson = "[]"
        editorOpen = true
    }

    fun beginEdit(entry: CharacterCombatEntry) {
        if (!structuralEditingEnabled) return
        editingId = entry.id.toString()
        editorName = entry.name
        editorType = entry.type.name
        editorAttackModifier = entry.attackModifier?.toString().orEmpty()
        editorRange = entry.rangeText.orEmpty()
        editorNotes = entry.notes.orEmpty()
        editorDamageJson = characterDamageComponentsToJsonV4(damageFor(entry.id))
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

    Column(
        modifier = Modifier.fillMaxSize().imePadding().navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(
                start = appSpacingV4(if (wide) 8.dp else 5.dp),
                end = appSpacingV4(if (wide) 8.dp else 5.dp),
                top = appSpacingV4(4.dp),
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

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(if (wide) 8.dp else 5.dp)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Ataques y acciones", style = MaterialTheme.typography.titleSmall)
            TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text("+ Añadir") }
        }

        if (entries.isEmpty()) {
            CharacterUsefulEmptyState(
                title = "Sin ataques o acciones",
                message = "Añade ataques, acciones, reacciones o referencias de combate. El daño puede registrarse por componentes.",
                onAdd = if (structuralEditingEnabled) ::beginAdd else null,
                modifier = Modifier.padding(horizontal = appSpacingV4(if (wide) 8.dp else 5.dp)),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(
                    start = appSpacingV4(if (wide) 8.dp else 5.dp),
                    end = appSpacingV4(if (wide) 8.dp else 5.dp),
                    bottom = appSpacingV4(88.dp),
                ),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
            ) {
                itemsIndexed(entries, key = { _, entry -> entry.id.toString() }) { index, entry ->
                    CharacterCombatSuccessorCardV4(
                        entry = entry,
                        damageComponents = damageFor(entry.id),
                        favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.COMBAT_ENTRY, entry.id),
                        favoriteEnabled = structuralEditingEnabled && entry.id in persistedEntryIds,
                        structuralEditingEnabled = structuralEditingEnabled,
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
                        onDelete = { deleteId = entry.id.toString() },
                        onMove = { offset -> move(index, offset) },
                        onHaptic = haptic,
                    )
                }
            }
        }
    }

    if (editorOpen && structuralEditingEnabled) {
        val selectedType = runCatching { CharacterCombatEntryType.valueOf(editorType) }
            .getOrDefault(CharacterCombatEntryType.ATTACK)
        val parsedAttack = editorAttackModifier.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
        val attackValid = editorAttackModifier.trim().isEmpty() || parsedAttack != null
        val components = characterDamageComponentsFromJsonV4(editorDamageJson)
        val damageValid = components.all(::characterDamageComponentValidV4)
        val valid = editorName.trim().isNotEmpty() && attackValid && damageValid

        CharacterImeSafeEditorDialog(
            title = if (editingId == null) "Añadir ataque o acción" else "Editar ataque o acción",
            onCancel = { editorOpen = false },
            onSave = {
                val existing = editingId?.let { id -> entries.firstOrNull { it.id.toString() == id } }
                val entryId = existing?.id ?: Uuid.random()
                val normalizedComponents = components.map { component ->
                    component.copy(
                        expression = component.expression.trim(),
                        typeText = component.typeText?.trim()?.takeIf { it.isNotEmpty() },
                    )
                }
                val entry = CharacterCombatEntry(
                    id = entryId,
                    name = editorName.trim(),
                    type = selectedType,
                    attackModifier = parsedAttack,
                    damageEffect = characterDamageSummary(normalizedComponents),
                    rangeText = editorRange.trim().takeIf { it.isNotEmpty() },
                    notes = editorNotes.trim().takeIf { it.isNotEmpty() },
                    sortOrder = existing?.sortOrder ?: entries.size,
                    pinned = existing?.pinned ?: false,
                )
                val updatedEntries = if (existing == null) {
                    entries + entry
                } else {
                    entries.map { if (it.id == existing.id) entry else it }
                }.mapIndexed { order, item -> item.copy(sortOrder = order) }
                val updatedDamage = damageProfiles.filterNot { it.combatEntryId == entryId } +
                    CharacterCombatDamageProfile(entryId, normalizedComponents)
                onEntriesChange(updatedEntries)
                onDamageProfilesChange(updatedDamage)
                editorOpen = false
            },
            saveEnabled = valid,
            supportingText = "El daño estructurado se guarda junto con la ficha; el resumen antiguo queda solo como compatibilidad derivada.",
        ) {
            OutlinedTextField(
                value = editorName,
                onValueChange = { editorName = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nombre") },
                singleLine = true,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CharacterCombatTypeSelectorV4(
                    selected = selectedType,
                    onSelected = { editorType = it.name },
                    modifier = Modifier.weight(1f),
                )
                OutlinedTextField(
                    value = editorAttackModifier,
                    onValueChange = { editorAttackModifier = sanitizeSignedIntegerInputV4(it) },
                    modifier = Modifier.weight(1f),
                    label = { Text("Ataque") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = editorRange,
                    onValueChange = { editorRange = it },
                    modifier = Modifier.weight(1.2f),
                    label = { Text("Alcance") },
                    singleLine = true,
                )
            }

            Text("Daño / efecto", style = MaterialTheme.typography.titleSmall)
            components.forEachIndexed { index, component ->
                CharacterDamageComponentEditorRowV4(
                    component = component,
                    onChange = { updated ->
                        editorDamageJson = characterDamageComponentsToJsonV4(
                            components.mapIndexed { componentIndex, current ->
                                if (componentIndex == index) updated else current
                            },
                        )
                    },
                    onRemove = {
                        editorDamageJson = characterDamageComponentsToJsonV4(
                            components.filterIndexed { componentIndex, _ -> componentIndex != index },
                        )
                    },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            ) {
                TextButton(
                    onClick = {
                        editorDamageJson = characterDamageComponentsToJsonV4(
                            components + CharacterDamageComponent(CharacterDamageComponentKind.DICE, ""),
                        )
                    },
                ) { Text("+ Dados") }
                TextButton(
                    onClick = {
                        editorDamageJson = characterDamageComponentsToJsonV4(
                            components + CharacterDamageComponent(CharacterDamageComponentKind.FLAT, ""),
                        )
                    },
                ) { Text("+ Plano") }
                TextButton(
                    onClick = {
                        editorDamageJson = characterDamageComponentsToJsonV4(
                            components + CharacterDamageComponent(CharacterDamageComponentKind.TEXT, ""),
                        )
                    },
                ) { Text("+ Texto") }
            }
            CharacterInlineValidationMessage(
                when {
                    !attackValid -> "El modificador de ataque debe ser un entero o quedar vacío."
                    !damageValid -> "Cada componente debe tener una expresión válida: por ejemplo 1d8, +3 o un texto de efecto."
                    else -> null
                },
            )
            OutlinedTextField(
                value = editorNotes,
                onValueChange = { editorNotes = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Notas") },
                minLines = characterCompactTextAreaMinLinesV4(2),
                maxLines = 5,
            )
        }
    }

    deleteId?.let { id ->
        val target = entries.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "ataque o acción",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    onEntriesChange(
                        entries.filterNot { it.id == target.id }
                            .mapIndexed { order, item -> item.copy(sortOrder = order) },
                    )
                    onDamageProfilesChange(damageProfiles.filterNot { it.combatEntryId == target.id })
                    deleteId = null
                },
            )
        }
    }
}

@Composable
private fun CharacterCombatSuccessorCardV4(
    entry: CharacterCombatEntry,
    damageComponents: List<CharacterDamageComponent>,
    favorite: Boolean,
    favoriteEnabled: Boolean,
    structuralEditingEnabled: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMove: (Int) -> Boolean,
    onHaptic: (CharacterHapticEventV4) -> Unit,
) {
    var dragging by remember(entry.id) { mutableStateOf(false) }
    var accumulatedDrag by remember(entry.id) { mutableStateOf(0f) }
    val damage = characterDamageSummary(damageComponents).ifBlank { "Sin daño / efecto" }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            if (dragging) 2.dp else 1.dp,
            if (dragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        tonalElevation = if (dragging) 3.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = appSpacingV4(7.dp),
                vertical = appSpacingV4(5.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .characterMeasuredReorderDragV4(
                                enabled = structuralEditingEnabled,
                                onHaptic = onHaptic,
                                onMove = onMove,
                                onVisualStateChange = { state ->
                                    dragging = state.active
                                    accumulatedDrag = state.offsetY
                                },
                            )
                    .clickable(enabled = structuralEditingEnabled, onClick = onEdit),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        buildString {
                            append(entry.name)
                            entry.attackModifier?.let { append(" (${formatSignedCombatSuccessorV4(it)})") }
                        },
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (dragging) Text("Moviendo…", style = MaterialTheme.typography.labelSmall)
                }
                Text(
                    damage,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    listOfNotNull(
                        characterCombatEntryTypeSpanishLabel(entry.type),
                        entry.rangeText?.trim()?.takeIf { it.isNotEmpty() }?.let { "Alcance $it" },
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                entry.notes?.trim()?.takeIf { it.isNotEmpty() }?.let { notes ->
                    Text(
                        notes,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StableFavoriteIconButton(
                    selected = favorite,
                    onClick = { onFavoriteChange(!favorite) },
                    enabled = favoriteEnabled,
                )
                TextButton(onClick = onEdit, enabled = structuralEditingEnabled) { Text("Editar") }
                TextButton(onClick = onDelete, enabled = structuralEditingEnabled) { Text("Eliminar") }
            }
        }
    }
}

@Composable
private fun CharacterCombatTypeSelectorV4(
    selected: CharacterCombatEntryType,
    onSelected: (CharacterCombatEntryType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(characterCombatEntryTypeSpanishLabel(selected), maxLines = 1)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CharacterCombatEntryType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(characterCombatEntryTypeSpanishLabel(type)) },
                    onClick = {
                        onSelected(type)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun CharacterDamageComponentEditorRowV4(
    component: CharacterDamageComponent,
    onChange: (CharacterDamageComponent) -> Unit,
    onRemove: () -> Unit,
) {
    var expanded by remember(component.kind) { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(0.9f)) {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(characterDamageKindLabelV4(component.kind), maxLines = 1)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                CharacterDamageComponentKind.entries.forEach { kind ->
                    DropdownMenuItem(
                        text = { Text(characterDamageKindLabelV4(kind)) },
                        onClick = {
                            onChange(component.copy(kind = kind))
                            expanded = false
                        },
                    )
                }
            }
        }
        OutlinedTextField(
            value = component.expression,
            onValueChange = { onChange(component.copy(expression = it)) },
            modifier = Modifier.weight(1f),
            label = {
                Text(
                    when (component.kind) {
                        CharacterDamageComponentKind.DICE -> "1d8"
                        CharacterDamageComponentKind.FLAT -> "+3"
                        CharacterDamageComponentKind.TEXT -> "Efecto"
                    },
                )
            },
            singleLine = true,
        )
        if (component.kind != CharacterDamageComponentKind.TEXT) {
            OutlinedTextField(
                value = component.typeText.orEmpty(),
                onValueChange = { onChange(component.copy(typeText = it)) },
                modifier = Modifier.weight(1.1f),
                label = { Text("Tipo") },
                singleLine = true,
            )
        }
        TextButton(onClick = onRemove) { Text("×") }
    }
}

private fun characterDamageComponentValidV4(component: CharacterDamageComponent): Boolean = when (component.kind) {
    CharacterDamageComponentKind.DICE -> parseCharacterDiceExpression(component.expression) != null
    CharacterDamageComponentKind.FLAT -> component.expression.trim().toIntOrNull() != null
    CharacterDamageComponentKind.TEXT -> component.expression.isNotBlank()
}

private fun characterDamageKindLabelV4(kind: CharacterDamageComponentKind): String = when (kind) {
    CharacterDamageComponentKind.DICE -> "Dados"
    CharacterDamageComponentKind.FLAT -> "Plano"
    CharacterDamageComponentKind.TEXT -> "Texto"
}

private fun formatSignedCombatSuccessorV4(value: Int): String = if (value >= 0) "+$value" else value.toString()
