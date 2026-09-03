from pathlib import Path

PATH = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEquipmentClosureV4.kt')
text = PATH.read_text()


def once(old: str, new: str) -> None:
    global text
    count = text.count(old)
    if count != 1:
        raise SystemExit(f'expected exactly one match, found {count}: {old[:120]!r}')
    text = text.replace(old, new, 1)


# F3 is additive UI-only work. Abort if F2 structure is no longer the expected green baseline.
if 'internal fun CharacterEquipmentClosureTabV4(' not in text or 'private fun EquipmentEditorF2(' not in text:
    raise SystemExit('F2 equipment surface not found')
if 'EquipmentEditorPanelF3' in text:
    raise SystemExit('F3 appears to be already integrated')

once(
    'import androidx.compose.foundation.clickable\n',
    'import androidx.compose.foundation.clickable\nimport androidx.compose.foundation.rememberScrollState\nimport androidx.compose.foundation.verticalScroll\n',
)
once(
    'import androidx.compose.foundation.layout.fillMaxSize\n',
    'import androidx.compose.foundation.layout.fillMaxHeight\nimport androidx.compose.foundation.layout.fillMaxSize\n',
)
once(
    'import androidx.compose.foundation.layout.width\n',
    'import androidx.compose.foundation.layout.width\nimport androidx.compose.foundation.layout.widthIn\n',
)

# Make the selected object visually stable in the master list.
text = text.replace(
    '                special = false,\n                usageFor = ::usageFor,',
    '                special = false,\n                selectedId = editingId,\n                usageFor = ::usageFor,',
    1,
)
text = text.replace(
    '                special = true,\n                usageFor = ::usageFor,',
    '                special = true,\n                selectedId = editingId,\n                usageFor = ::usageFor,',
    1,
)
once(
    '    special: Boolean,\n    usageFor: (CharacterInventoryItem) -> CharacterInventoryUsage,\n',
    '    special: Boolean,\n    selectedId: String?,\n    usageFor: (CharacterInventoryItem) -> CharacterInventoryUsage,\n',
)
once(
    '                                    canReorder = canReorder,\n                                    special = special,\n',
    '                                    canReorder = canReorder,\n                                    special = special,\n                                    selected = selectedId == item.id.toString(),\n',
)
once(
    '    canReorder: Boolean,\n    special: Boolean,\n    onEdit: () -> Unit,\n',
    '    canReorder: Boolean,\n    special: Boolean,\n    selected: Boolean,\n    onEdit: () -> Unit,\n',
)

start_dense = text.index('private fun EquipmentDenseItemF2(')
end_dense = text.index('@Composable\nprivate fun EquipmentEditorF2(', start_dense)
dense = text[start_dense:end_dense]
old_border = '            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),\n'
if dense.count(old_border) != 1:
    raise SystemExit('dense item border guard mismatch')
dense = dense.replace(
    old_border,
    '            border = BorderStroke(\n                1.dp,\n                if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,\n            ),\n',
    1,
)
text = text[:start_dense] + dense + text[end_dense:]

# Compute editor validity once and use the same apply operation for phone dialog and tablet detail pane.
anchor = '    LazyColumn(\n        modifier = Modifier.fillMaxSize().imePadding().navigationBarsPadding(),\n'
if text.count(anchor) != 1:
    raise SystemExit('top-level equipment LazyColumn guard mismatch')
prelude = '''    val parsedEditorQuantity = editorQuantity.toIntOrNull()\n    val parsedEditorWeight = editorWeight.trim().replace(',', '.').takeIf { it.isNotEmpty() }?.toDoubleOrNull()\n    val parsedEditorKind = runCatching { CharacterConsumableKind.valueOf(editorKindName) }\n        .getOrDefault(CharacterConsumableKind.NONE)\n    val parsedEditorCarry = runCatching { CharacterInventoryCarryState.valueOf(editorCarryName) }\n        .getOrDefault(CharacterInventoryCarryState.CARRIED)\n    val parsedEditorQuickUse = editorQuickUse.toIntOrNull()\n    val editorValid = editorName.trim().isNotEmpty() &&\n        parsedEditorQuantity != null && parsedEditorQuantity >= 0 &&\n        (editorWeight.isBlank() || (parsedEditorWeight != null && parsedEditorWeight >= 0.0)) &&\n        (parsedEditorKind == CharacterConsumableKind.NONE || (parsedEditorQuickUse != null && parsedEditorQuickUse > 0))\n\n    fun applyEditor() {\n        if (!editorValid) return\n        val existing = editingId?.let { id -> draft.items.firstOrNull { it.id.toString() == id } }\n        val id = existing?.id ?: Uuid.random()\n        val item = CharacterInventoryItem(\n            id = id,\n            name = editorName.trim(),\n            quantity = parsedEditorQuantity ?: 0,\n            weightLb = parsedEditorWeight,\n            equipped = editorEquipped,\n            notes = editorNotes.trim().takeIf { it.isNotEmpty() },\n            sortOrder = existing?.sortOrder ?: draft.items.size,\n            special = editorSpecial,\n            description = if (editorSpecial) editorDescription.trim().takeIf { it.isNotEmpty() } else null,\n            location = editorLocation.trim().takeIf { it.isNotEmpty() },\n            attuned = editorSpecial && editorAttuned,\n        )\n        val usage = CharacterInventoryUsage(\n            itemId = id,\n            kind = parsedEditorKind,\n            quickUseAmount = if (parsedEditorKind == CharacterConsumableKind.NONE) 1 else (parsedEditorQuickUse ?: 1),\n            carryState = if (editorEquipped) CharacterInventoryCarryState.CARRIED else parsedEditorCarry,\n        )\n        val updatedItems = if (existing == null) {\n            draft.items + item\n        } else {\n            draft.items.map { if (it.id == id) item else it }\n        }.mapIndexed { order, current -> current.copy(sortOrder = order) }\n        onDraftChange(\n            draft.copy(\n                items = updatedItems,\n                inventoryUsage = updateUsage(usage),\n            ),\n        )\n        editorOpen = false\n    }\n\n    Row(\n        modifier = Modifier.fillMaxSize().imePadding().navigationBarsPadding(),\n        horizontalArrangement = Arrangement.spacedBy(if (wide) 8.dp else 0.dp),\n    ) {\n        LazyColumn(\n            modifier = if (wide) Modifier.weight(1f).fillMaxHeight() else Modifier.fillMaxSize(),\n'''
text = text.replace(anchor, prelude, 1)

# Replace the previous modal-only editor block. The LazyColumn closing brace immediately before this marker
# now closes only the list; this replacement completes the Row and adds the wide detail pane.
start = text.index('    if (editorOpen) {\n')
end = text.index('    if (confirmSpecialRemoval) {\n', start)
old_editor_block = text[start:end]
if 'EquipmentEditorF2(' not in old_editor_block or 'val quantity = editorQuantity.toIntOrNull()' not in old_editor_block:
    raise SystemExit('modal editor block guard mismatch')
new_editor_block = '''        if (wide) {\n            EquipmentEditorPanelF3(\n                editorOpen = editorOpen,\n                title = if (editingId == null) "Añadir objeto" else "Editar objeto",\n                name = editorName,\n                quantity = editorQuantity,\n                weight = editorWeight,\n                equipped = editorEquipped,\n                notes = editorNotes,\n                special = editorSpecial,\n                description = editorDescription,\n                location = editorLocation,\n                attuned = editorAttuned,\n                kind = parsedEditorKind,\n                quickUse = editorQuickUse,\n                carryState = if (editorEquipped) CharacterInventoryCarryState.CARRIED else parsedEditorCarry,\n                valid = editorValid,\n                onBeginAdd = ::beginAdd,\n                onNameChange = { editorName = it },\n                onQuantityChange = { editorQuantity = sanitizeUnsignedF2(it) },\n                onWeightChange = { editorWeight = sanitizeDecimalF2(it) },\n                onEquippedChange = { equipped ->\n                    editorEquipped = equipped\n                    if (equipped) editorCarryName = CharacterInventoryCarryState.CARRIED.name\n                },\n                onNotesChange = { editorNotes = it },\n                onSpecialChange = { requested ->\n                    if (!requested && editorSpecial && (editorDescription.isNotBlank() || editorAttuned)) {\n                        confirmSpecialRemoval = true\n                    } else {\n                        editorSpecial = requested\n                    }\n                },\n                onDescriptionChange = { editorDescription = it },\n                onLocationChange = { editorLocation = it },\n                onAttunedChange = { editorAttuned = it },\n                onKindChange = { editorKindName = it.name },\n                onQuickUseChange = { editorQuickUse = sanitizeUnsignedF2(it) },\n                onCarryStateChange = { editorCarryName = it.name },\n                onDismiss = {\n                    editorOpen = false\n                    editingId = null\n                },\n                onApply = ::applyEditor,\n            )\n        }\n    }\n\n    if (editorOpen && !wide) {\n        EquipmentEditorF2(\n            title = if (editingId == null) "Añadir objeto" else "Editar objeto",\n            name = editorName,\n            quantity = editorQuantity,\n            weight = editorWeight,\n            equipped = editorEquipped,\n            notes = editorNotes,\n            special = editorSpecial,\n            description = editorDescription,\n            location = editorLocation,\n            attuned = editorAttuned,\n            kind = parsedEditorKind,\n            quickUse = editorQuickUse,\n            carryState = if (editorEquipped) CharacterInventoryCarryState.CARRIED else parsedEditorCarry,\n            valid = editorValid,\n            onNameChange = { editorName = it },\n            onQuantityChange = { editorQuantity = sanitizeUnsignedF2(it) },\n            onWeightChange = { editorWeight = sanitizeDecimalF2(it) },\n            onEquippedChange = { equipped ->\n                editorEquipped = equipped\n                if (equipped) editorCarryName = CharacterInventoryCarryState.CARRIED.name\n            },\n            onNotesChange = { editorNotes = it },\n            onSpecialChange = { requested ->\n                if (!requested && editorSpecial && (editorDescription.isNotBlank() || editorAttuned)) {\n                    confirmSpecialRemoval = true\n                } else {\n                    editorSpecial = requested\n                }\n            },\n            onDescriptionChange = { editorDescription = it },\n            onLocationChange = { editorLocation = it },\n            onAttunedChange = { editorAttuned = it },\n            onKindChange = { editorKindName = it.name },\n            onQuickUseChange = { editorQuickUse = sanitizeUnsignedF2(it) },\n            onCarryStateChange = { editorCarryName = it.name },\n            onDismiss = { editorOpen = false },\n            onApply = ::applyEditor,\n        )\n    }\n\n'''
text = text[:start] + new_editor_block + text[end:]

# If the selected object is deleted, close the detail pane without disturbing list/query state.
delete_anchor = '''                    onDraftChange(\n                        draft.copy(\n                            items = draft.items.filterNot { it.id == target.id }\n                                .mapIndexed { order, item -> item.copy(sortOrder = order) },\n                            inventoryUsage = draft.inventoryUsage.filterNot { it.itemId == target.id },\n                        ),\n                    )\n                    deleteId = null\n'''
if text.count(delete_anchor) != 1:
    raise SystemExit('delete selection guard mismatch')
text = text.replace(
    delete_anchor,
    delete_anchor.replace(
        '                    deleteId = null\n',
        '''                    if (editingId == target.id.toString()) {\n                        editorOpen = false\n                        editingId = null\n                    }\n                    deleteId = null\n''',
    ),
    1,
)

# Add the wide-only detail pane before the existing phone dialog component.
panel_anchor = '@Composable\nprivate fun EquipmentEditorF2(\n'
if text.count(panel_anchor) != 1:
    raise SystemExit('EquipmentEditorF2 insertion guard mismatch')
panel = r'''@Composable
private fun EquipmentEditorPanelF3(
    editorOpen: Boolean,
    title: String,
    name: String,
    quantity: String,
    weight: String,
    equipped: Boolean,
    notes: String,
    special: Boolean,
    description: String,
    location: String,
    attuned: Boolean,
    kind: CharacterConsumableKind,
    quickUse: String,
    carryState: CharacterInventoryCarryState,
    valid: Boolean,
    onBeginAdd: () -> Unit,
    onNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onEquippedChange: (Boolean) -> Unit,
    onNotesChange: (String) -> Unit,
    onSpecialChange: (Boolean) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onAttunedChange: (Boolean) -> Unit,
    onKindChange: (CharacterConsumableKind) -> Unit,
    onQuickUseChange: (String) -> Unit,
    onCarryStateChange: (CharacterInventoryCarryState) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    Card(
        modifier = Modifier
            .widthIn(min = 320.dp, max = 440.dp)
            .fillMaxHeight(),
    ) {
        if (!editorOpen) {
            Column(
                modifier = Modifier.fillMaxSize().padding(14.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Editor de equipo", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Selecciona un objeto de la lista para editarlo sin perder tu posición, búsqueda ni filtros.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Button(onClick = onBeginAdd, modifier = Modifier.padding(top = 10.dp)) {
                    Text("+ Añadir objeto")
                }
            }
            return@Card
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) { Text("Cerrar") }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = onQuantityChange,
                        label = { Text("Cantidad") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = onWeightChange,
                        label = { Text("Peso/u. lb") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = equipped, onCheckedChange = onEquippedChange)
                    Text("Equipado")
                    Checkbox(checked = special, onCheckedChange = onSpecialChange)
                    Text("Especial")
                }
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = { Text("Contenedor / ubicación") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                EnumDropdownF2(
                    label = "Disponibilidad",
                    current = if (equipped) "Transportado (equipado)" else carryLabelF2(carryState),
                    options = CharacterInventoryCarryState.entries.map { it.name to carryLabelF2(it) },
                    enabled = !equipped,
                    onSelect = { onCarryStateChange(CharacterInventoryCarryState.valueOf(it)) },
                )
                EnumDropdownF2(
                    label = "Uso de cantidad",
                    current = consumableLabelF2(kind),
                    options = CharacterConsumableKind.entries.map { it.name to consumableLabelF2(it) },
                    onSelect = { onKindChange(CharacterConsumableKind.valueOf(it)) },
                )
                if (kind != CharacterConsumableKind.NONE) {
                    OutlinedTextField(
                        value = quickUse,
                        onValueChange = onQuickUseChange,
                        label = { Text("Cantidad por uso rápido") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
                if (special) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = attuned, onCheckedChange = onAttunedChange)
                        Text("Sintonizado")
                    }
                    OutlinedTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Descripción especial") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 5,
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    label = { Text("Notas") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                )
                CharacterInlineValidationMessage(
                    when {
                        name.trim().isEmpty() -> "El nombre no puede quedar vacío."
                        quantity.toIntOrNull() == null || quantity.toInt() < 0 -> "La cantidad debe ser un entero igual o mayor que 0."
                        weight.isNotBlank() && (weight.replace(',', '.').toDoubleOrNull() == null || weight.replace(',', '.').toDouble() < 0.0) -> "El peso debe ser un número igual o mayor que 0."
                        kind != CharacterConsumableKind.NONE && (quickUse.toIntOrNull() == null || quickUse.toInt() <= 0) -> "El uso rápido debe consumir al menos 1 unidad."
                        else -> null
                    },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
                Button(onClick = onApply, enabled = valid) { Text("Aplicar") }
            }
        }
    }
}

'''
text = text.replace(panel_anchor, panel + panel_anchor, 1)

PATH.write_text(text)
