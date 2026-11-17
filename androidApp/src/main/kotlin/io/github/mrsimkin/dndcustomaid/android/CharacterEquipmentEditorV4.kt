package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryCarryState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourceSuccessorConfiguration
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind
import io.github.mrsimkin.dndcustomaid.shared.dm.toItemPick
import kotlin.uuid.Uuid
// Refactor F3: the item editor and the money, defences and resources blocks of the PC's Equipo tab, moved out of CharacterEquipmentClosureV4.kt.

@Composable
internal fun EquipmentEditorPanelF3(
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
            .widthIn(min = 280.dp, max = 360.dp)
            .fillMaxHeight(),
    ) {
        if (!editorOpen) {
            Column(
                modifier = Modifier.fillMaxSize().padding(appSpacingV4(14.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Editor de equipo", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Selecciona un objeto para editarlo.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Button(onClick = onBeginAdd, modifier = Modifier.padding(top = appSpacingV4(10.dp))) {
                    Text("+ Añadir objeto")
                }
            }
            return@Card
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = appSpacingV4(10.dp), vertical = appSpacingV4(8.dp))) {
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
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
            ) {
                RulesPickButton(
                    kinds = listOf(
                        io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.WEAPON,
                        io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.ARMOR,
                        io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.ITEM,
                        io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.MAGIC_ITEM,
                        io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.LEGENDARY_ITEM,
                    ),
                    onPick = { option ->
                        val item = option.toItemPick()
                        onNameChange(item.name)
                        item.weightLb?.let { onWeightChange(if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()) }
                        if (item.special) onSpecialChange(true)
                        onDescriptionChange(item.description)
                        onNotesChange(item.notes)
                    },
                )
                CharacterCompactOutlinedTextFieldV4(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp))) {
                    CharacterCompactOutlinedTextFieldV4(
                        value = quantity,
                        onValueChange = onQuantityChange,
                        label = { Text("Cantidad") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    CharacterCompactOutlinedTextFieldV4(
                        value = weight,
                        onValueChange = onWeightChange,
                        label = { Text("Peso/u. lb") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )
                }
                CharacterResponsiveCheckboxGroupV4 {
                    CharacterCompactCheckboxItemV4(
                        checked = equipped,
                        onCheckedChange = onEquippedChange,
                        label = "Equipado",
                    )
                    CharacterCompactCheckboxItemV4(
                        checked = special,
                        onCheckedChange = onSpecialChange,
                        label = "Especial",
                    )
                }
                CharacterCompactOutlinedTextFieldV4(
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
                    label = "Tipo de consumo",
                    current = consumableLabelF2(kind),
                    options = CharacterConsumableKind.entries.map { it.name to consumableLabelF2(it) },
                    onSelect = { onKindChange(CharacterConsumableKind.valueOf(it)) },
                )
                if (kind != CharacterConsumableKind.NONE) {
                    CharacterCompactOutlinedTextFieldV4(
                        value = quickUse,
                        onValueChange = onQuickUseChange,
                        label = { Text("Descuento por uso") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
                if (special) {
                    CharacterCompactCheckboxItemV4(
                        checked = attuned,
                        onCheckedChange = onAttunedChange,
                        label = "Sintonizado",
                    )
                    CharacterCompactOutlinedTextFieldV4(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Descripción especial") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = characterCompactTextAreaMinLinesV4(2),
                        maxLines = 5,
                    )
                }
                CharacterCompactOutlinedTextFieldV4(
                    value = notes,
                    onValueChange = onNotesChange,
                    label = { Text("Notas") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = characterCompactTextAreaMinLinesV4(2),
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
                modifier = Modifier.fillMaxWidth().padding(top = appSpacingV4(6.dp)),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
                Button(onClick = onApply, enabled = valid) { Text("Aplicar") }
            }
        }
    }
}

@Composable
internal fun EquipmentEditorF2(
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
    CharacterImeSafeEditorDialog(
        title = title,
        onCancel = onDismiss,
        onSave = onApply,
        saveEnabled = valid,
    ) {
        RulesPickButton(
            kinds = listOf(
                io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.WEAPON,
                io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.ARMOR,
                io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.ITEM,
                io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.MAGIC_ITEM,
                io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.LEGENDARY_ITEM,
            ),
            onPick = { option ->
                val item = option.toItemPick()
                onNameChange(item.name)
                item.weightLb?.let { onWeightChange(if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()) }
                if (item.special) onSpecialChange(true)
                onDescriptionChange(item.description)
                onNotesChange(item.notes)
            },
        )
        CharacterCompactOutlinedTextFieldV4(value = name, onValueChange = onNameChange, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Row(horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp))) {
            CharacterCompactOutlinedTextFieldV4(
                value = quantity,
                onValueChange = onQuantityChange,
                label = { Text("Cantidad") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            CharacterCompactOutlinedTextFieldV4(
                value = weight,
                onValueChange = onWeightChange,
                label = { Text("Peso/u. lb") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }
        CharacterResponsiveCheckboxGroupV4 {
            CharacterCompactCheckboxItemV4(
                checked = equipped,
                onCheckedChange = onEquippedChange,
                label = "Equipado",
            )
            CharacterCompactCheckboxItemV4(
                checked = special,
                onCheckedChange = onSpecialChange,
                label = "Equipo especial",
            )
        }
        CharacterCompactOutlinedTextFieldV4(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Contenedor / ubicación (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("Ej.: Mochila, cinturón, carcaj, Bag of Holding, cofre, posada o ubicación corporal.") },
        )
        EnumDropdownF2(
            label = "Disponibilidad",
            current = if (equipped) "Transportado (equipado)" else carryLabelF2(carryState),
            options = CharacterInventoryCarryState.entries.map { it.name to carryLabelF2(it) },
            enabled = !equipped,
            onSelect = { onCarryStateChange(CharacterInventoryCarryState.valueOf(it)) },
        )
        EnumDropdownF2(
            label = "Tipo de consumo",
            current = consumableLabelF2(kind),
            options = CharacterConsumableKind.entries.map { it.name to consumableLabelF2(it) },
            onSelect = { onKindChange(CharacterConsumableKind.valueOf(it)) },
        )
        if (kind != CharacterConsumableKind.NONE) {
            CharacterCompactOutlinedTextFieldV4(
                value = quickUse,
                onValueChange = onQuickUseChange,
                label = { Text("Descuento por uso") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        if (special) {
            CharacterCompactCheckboxItemV4(
                checked = attuned,
                onCheckedChange = onAttunedChange,
                label = "Sintonizado",
            )
            CharacterCompactOutlinedTextFieldV4(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción especial") },
                modifier = Modifier.fillMaxWidth(),
                minLines = characterCompactTextAreaMinLinesV4(2),
                maxLines = 5,
            )
        }
        CharacterCompactOutlinedTextFieldV4(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = characterCompactTextAreaMinLinesV4(2),
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
}

@Composable
internal fun EnumDropdownF2(
    label: String,
    current: String,
    options: List<Pair<String, String>>,
    enabled: Boolean = true,
    onSelect: (String) -> Unit,
) {
    var expanded by rememberSaveable(label) { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        androidx.compose.foundation.layout.Box {
            OutlinedButton(
                onClick = { if (enabled) expanded = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            ) { Text(current) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (key, text) ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            onSelect(key)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
internal fun EquipmentDefensesAndResourcesG1(
    armorClass: Int,
    equippedItems: List<CharacterInventoryItem>,
    resources: List<CharacterResource>,
    configurations: Map<Uuid, CharacterResourceSuccessorConfiguration>,
    onResourceValueChange: (Uuid, Int) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(4.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Text("Defensas y recursos", style = MaterialTheme.typography.titleSmall)
            val equippedText = equippedItems.joinToString(", ") { item ->
                if (item.quantity > 1) "${item.name} ×${item.quantity}" else item.name
            }.ifBlank { "—" }
            Text(
                "CA $armorClass · Equipado: $equippedText",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            CharacterHelpV4(
                "Equipo proyecta la misma CA y los mismos objetos marcados como Equipado que General/Defensas. La ficha aún no clasifica armadura y escudo como categorías estructuradas separadas.",
            )
            if (resources.isNotEmpty()) {
                Text("Recursos de Equipo", style = MaterialTheme.typography.labelMedium)
                resources.forEach { resource ->
                    val configuration = configurations[resource.id] ?: return@forEach
                    EquipmentResourceRowG1(
                        resource = resource,
                        configuration = configuration,
                        onValueChange = { value -> onResourceValueChange(resource.id, value) },
                    )
                }
            }
        }
    }
}

@Composable
internal fun EquipmentResourceRowG1(
    resource: CharacterResource,
    configuration: CharacterResourceSuccessorConfiguration,
    onValueChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            resource.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when (configuration.valueKind) {
            CharacterTrackableValueKind.BINARY -> {
                val active = resource.currentValue > 0
                EquipmentToggleG1(
                    label = if (active) "Activo" else "Inactivo",
                    selected = active,
                    onClick = { onValueChange(if (active) 0 else 1) },
                )
            }
            CharacterTrackableValueKind.COUNTER,
            CharacterTrackableValueKind.CURRENT_MAX,
            -> {
                val maximum = if (configuration.valueKind == CharacterTrackableValueKind.CURRENT_MAX) resource.maxValue else null
                EquipmentStepG1("−", enabled = resource.currentValue > 0) {
                    onValueChange((resource.currentValue - 1).coerceAtLeast(0))
                }
                Text(
                    maximum?.let { "${resource.currentValue}/$it" } ?: resource.currentValue.toString(),
                    style = MaterialTheme.typography.labelLarge,
                )
                EquipmentStepG1("+", enabled = maximum?.let { resource.currentValue < it } ?: true) {
                    val next = resource.currentValue + 1
                    onValueChange(maximum?.let { next.coerceAtMost(it) } ?: next)
                }
            }
        }
    }
}

@Composable
internal fun EquipmentToggleG1(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.heightIn(min = 30.dp).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(3.dp)),
            contentAlignment = Alignment.Center,
        ) { Text(label, style = MaterialTheme.typography.labelSmall) }
    }
}

@Composable
internal fun EquipmentStepG1(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.heightIn(min = 30.dp).clickable(enabled = enabled, onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(3.dp)),
            contentAlignment = Alignment.Center,
        ) { Text(label, style = MaterialTheme.typography.labelLarge) }
    }
}

@Composable
internal fun EquipmentValuablesG1(
    value: String,
    editingEnabled: Boolean,
    onValueChange: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(4.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Text("Gemas / arte", style = MaterialTheme.typography.titleSmall)
            CharacterCompactOutlinedTextFieldV4(
                value = value,
                onValueChange = { if (editingEnabled) onValueChange(it) },
                readOnly = !editingEnabled,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Gemas, obras de arte, joyas u otros valores") },
                minLines = 1,
                maxLines = 4,
            )
        }
    }
}

@Composable
internal fun CompactCurrenciesF2(
    currencies: List<CharacterCurrency>,
    wide: Boolean,
    structuralEditingEnabled: Boolean,
    onCurrenciesChange: (List<CharacterCurrency>) -> Unit,
    onAddCurrency: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(4.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Monedas", style = MaterialTheme.typography.titleSmall)
                TextButton(onClick = onAddCurrency, enabled = structuralEditingEnabled) { Text("+ Añadir") }
            }
            if (currencies.isEmpty()) {
                Text("Sin monedas registradas.", style = MaterialTheme.typography.bodySmall)
            } else {
                val columns = if (wide) 6 else 3
                currencies.chunked(columns).forEach { rowCurrencies ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                        verticalAlignment = Alignment.Top,
                    ) {
                        rowCurrencies.forEach { currency ->
                            CompactCurrencyCellF2(
                                currency = currency,
                                onAmountChange = { amount ->
                                    onCurrenciesChange(
                                        currencies.map { if (it.key == currency.key) it.copy(amount = amount) else it },
                                    )
                                },
                                onDelete = if (currency.isDefault || !structuralEditingEnabled) null else {
                                    { onCurrenciesChange(currencies.filterNot { it.key == currency.key }) }
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(columns - rowCurrencies.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
internal fun CompactCurrencyCellF2(
    currency: CharacterCurrency,
    onAmountChange: (Int) -> Unit,
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable(currency.key, currency.amount) { mutableStateOf(currency.amount.toString()) }
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(3.dp)),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(currency.name, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                BasicTextField(
                    value = text,
                    onValueChange = { raw ->
                        text = sanitizeSignedF2(raw)
                        text.toIntOrNull()?.let(onAmountChange)
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 26.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            if (onDelete != null) {
                StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar moneda ${currency.name}")
            }
        }
    }
}
