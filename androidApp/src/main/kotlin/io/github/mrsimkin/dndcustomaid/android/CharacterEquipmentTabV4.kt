package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import kotlin.math.abs
import kotlin.uuid.Uuid

private val equipmentLocationsV4 = listOf(
    "Cabeza",
    "Rostro",
    "Cuello",
    "Mano izquierda",
    "Mano derecha",
    "Brazo izquierdo",
    "Brazo derecho",
    "Pecho",
    "Piernas",
    "Pies",
)

@Composable
internal fun CharacterEquipmentTabV4(
    items: List<CharacterInventoryItem>,
    currencies: List<CharacterCurrency>,
    onItemsChange: (List<CharacterInventoryItem>) -> Unit,
    onCurrenciesChange: (List<CharacterCurrency>) -> Unit,
    wide: Boolean,
) {
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorName by rememberSaveable { mutableStateOf("") }
    var editorQuantity by rememberSaveable { mutableStateOf("1") }
    var editorWeight by rememberSaveable { mutableStateOf("") }
    var editorEquipped by rememberSaveable { mutableStateOf(false) }
    var editorNotes by rememberSaveable { mutableStateOf("") }
    var editorSpecial by rememberSaveable { mutableStateOf(false) }
    var editorDescription by rememberSaveable { mutableStateOf("") }
    var editorLocation by rememberSaveable { mutableStateOf("") }
    var editorAttuned by rememberSaveable { mutableStateOf(false) }
    var deleteItemId by rememberSaveable { mutableStateOf<String?>(null) }
    var confirmSpecialRemoval by rememberSaveable { mutableStateOf(false) }
    var addCurrencyOpen by rememberSaveable { mutableStateOf(false) }
    var customCurrencyName by rememberSaveable { mutableStateOf("") }
    var customCurrencyAmount by rememberSaveable { mutableStateOf("0") }

    fun beginAdd() {
        editingId = null
        editorName = ""
        editorQuantity = "1"
        editorWeight = ""
        editorEquipped = false
        editorNotes = ""
        editorSpecial = false
        editorDescription = ""
        editorLocation = ""
        editorAttuned = false
        editorOpen = true
    }

    fun beginEdit(item: CharacterInventoryItem) {
        editingId = item.id.toString()
        editorName = item.name
        editorQuantity = item.quantity.toString()
        editorWeight = item.weightLb?.let(::formatDecimalInputV4).orEmpty()
        editorEquipped = item.equipped
        editorNotes = item.notes.orEmpty()
        editorSpecial = item.special
        editorDescription = item.description.orEmpty()
        editorLocation = item.location.orEmpty()
        editorAttuned = item.attuned
        editorOpen = true
    }

    fun moveItemWithinSection(
        sectionItems: List<CharacterInventoryItem>,
        item: CharacterInventoryItem,
        offset: Int,
    ): Boolean {
        val index = sectionItems.indexOfFirst { it.id == item.id }
        val target = index + offset
        if (index < 0 || target !in sectionItems.indices) return false

        val reorderedSection = sectionItems.toMutableList()
        val moved = reorderedSection.removeAt(index)
        reorderedSection.add(target, moved)
        val iterator = reorderedSection.iterator()
        val merged = items.map { current ->
            if (current.special == item.special) iterator.next() else current
        }
        onItemsChange(merged.mapIndexed { order, current -> current.copy(sortOrder = order) })
        return true
    }

    val carriedLb = items.sumOf { it.carriedWeightLb }
    val attunedCount = items.count { it.special && it.attuned }
    val ordinaryItems = items.filterNot { it.special }
    val specialItems = items.filter { it.special }

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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 7.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("Equipo", style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Peso transportado", style = MaterialTheme.typography.labelSmall)
                            Text(formatWeightDualV4(carriedLb), style = MaterialTheme.typography.bodyMedium)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Sintonización", style = MaterialTheme.typography.labelSmall)
                            Text("Sintonizados: $attunedCount", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    TextButton(onClick = ::beginAdd) { Text("+ Añadir objeto") }

                    if (items.isEmpty()) {
                        Text("Sin objetos registrados.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        if (ordinaryItems.isNotEmpty()) {
                            EquipmentSectionV4(
                                title = "Objetos",
                                sectionItems = ordinaryItems,
                                wide = wide,
                                special = false,
                                onEdit = ::beginEdit,
                                onMove = { item, offset -> moveItemWithinSection(ordinaryItems, item, offset) },
                                onDelete = { deleteItemId = it.id.toString() },
                            )
                        }
                        if (ordinaryItems.isNotEmpty() && specialItems.isNotEmpty()) {
                            HorizontalDivider()
                        }
                        if (specialItems.isNotEmpty()) {
                            EquipmentSectionV4(
                                title = "Equipo especial",
                                sectionItems = specialItems,
                                wide = wide,
                                special = true,
                                onEdit = ::beginEdit,
                                onMove = { item, offset -> moveItemWithinSection(specialItems, item, offset) },
                                onDelete = { deleteItemId = it.id.toString() },
                            )
                        }
                    }
                }
            }
        }

        item {
            CurrencyCardV4(
                currencies = currencies,
                onCurrenciesChange = onCurrenciesChange,
                onAddCurrency = {
                    customCurrencyName = ""
                    customCurrencyAmount = "0"
                    addCurrencyOpen = true
                },
                wide = wide,
            )
        }
    }

    if (editorOpen) {
        val quantity = editorQuantity.toIntOrNull()
        val weight = editorWeight.trim().replace(',', '.').takeIf { it.isNotEmpty() }?.toDoubleOrNull()
        val quantityValid = quantity != null && quantity >= 0
        val weightValid = editorWeight.isBlank() || (weight != null && weight >= 0.0)
        val valid = editorName.trim().isNotEmpty() && quantityValid && weightValid

        EquipmentItemEditorDialogV4(
            title = if (editingId == null) "Añadir objeto" else "Editar objeto",
            name = editorName,
            quantity = editorQuantity,
            weight = editorWeight,
            equipped = editorEquipped,
            notes = editorNotes,
            special = editorSpecial,
            description = editorDescription,
            location = editorLocation,
            attuned = editorAttuned,
            valid = valid,
            onNameChange = { editorName = it },
            onQuantityChange = { editorQuantity = sanitizeUnsignedIntEquipmentV4(it) },
            onWeightChange = { editorWeight = sanitizeDecimalEquipmentV4(it) },
            onEquippedChange = { editorEquipped = it },
            onNotesChange = { editorNotes = it },
            onSpecialChange = { requested ->
                if (!requested && editorSpecial && (
                        editorDescription.isNotBlank() || editorLocation.isNotBlank() || editorAttuned
                    )
                ) {
                    confirmSpecialRemoval = true
                } else {
                    editorSpecial = requested
                }
            },
            onDescriptionChange = { editorDescription = it },
            onLocationChange = { editorLocation = it },
            onAttunedChange = { editorAttuned = it },
            onDismiss = { editorOpen = false },
            onApply = {
                val existing = editingId?.let { id -> items.firstOrNull { it.id.toString() == id } }
                val item = CharacterInventoryItem(
                    id = existing?.id ?: Uuid.random(),
                    name = editorName.trim(),
                    quantity = quantity ?: 0,
                    weightLb = weight,
                    equipped = editorEquipped,
                    notes = editorNotes.trim().takeIf { it.isNotEmpty() },
                    sortOrder = existing?.sortOrder ?: items.size,
                    special = editorSpecial,
                    description = if (editorSpecial) editorDescription.trim().takeIf { it.isNotEmpty() } else null,
                    location = if (editorSpecial) editorLocation.trim().takeIf { it.isNotEmpty() } else null,
                    attuned = editorSpecial && editorAttuned,
                )
                val updated = if (existing == null) {
                    items + item
                } else {
                    items.map { if (it.id == existing.id) item else it }
                }
                onItemsChange(updated.mapIndexed { order, current -> current.copy(sortOrder = order) })
                editorOpen = false
            },
        )
    }

    if (confirmSpecialRemoval) {
        AlertDialog(
            onDismissRequest = { confirmSpecialRemoval = false },
            title = { Text("Convertir en equipo normal") },
            text = { Text("Al continuar se eliminarán la descripción especial, ubicación y estado de Sintonización de este objeto.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        editorSpecial = false
                        editorDescription = ""
                        editorLocation = ""
                        editorAttuned = false
                        confirmSpecialRemoval = false
                    },
                ) { Text("Continuar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmSpecialRemoval = false }) { Text("Cancelar") }
            },
        )
    }

    deleteItemId?.let { id ->
        val target = items.firstOrNull { it.id.toString() == id }
        if (target != null) {
            AlertDialog(
                onDismissRequest = { deleteItemId = null },
                title = { Text("Eliminar objeto") },
                text = { Text("¿Eliminar «${target.name}»? Esta acción se aplicará al guardar la ficha.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onItemsChange(
                                items.filterNot { it.id == target.id }
                                    .mapIndexed { order, current -> current.copy(sortOrder = order) },
                            )
                            deleteItemId = null
                        },
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { deleteItemId = null }) { Text("Cancelar") }
                },
            )
        } else {
            deleteItemId = null
        }
    }

    if (addCurrencyOpen) {
        val amount = customCurrencyAmount.toIntOrNull()
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Añadir moneda") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .heightIn(max = 320.dp)
                        .imePadding()
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(bottom = 72.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    item {
                        OutlinedTextField(
                            value = customCurrencyName,
                            onValueChange = { customCurrencyName = it },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = customCurrencyAmount,
                            onValueChange = { customCurrencyAmount = sanitizeSignedIntEquipmentV4(it) },
                            label = { Text("Cantidad") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = customCurrencyName.trim()
                        if (name.isNotEmpty() && amount != null) {
                            onCurrenciesChange(
                                currencies + CharacterCurrency(
                                    key = "custom:${Uuid.random()}",
                                    name = name,
                                    amount = amount,
                                    sortOrder = currencies.size,
                                    isDefault = false,
                                ),
                            )
                            addCurrencyOpen = false
                        }
                    },
                    enabled = customCurrencyName.trim().isNotEmpty() && amount != null,
                ) { Text("Añadir") }
            },
            dismissButton = {
                TextButton(onClick = { addCurrencyOpen = false }) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun EquipmentSectionV4(
    title: String,
    sectionItems: List<CharacterInventoryItem>,
    wide: Boolean,
    special: Boolean,
    onEdit: (CharacterInventoryItem) -> Unit,
    onMove: (CharacterInventoryItem, Int) -> Boolean,
    onDelete: (CharacterInventoryItem) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = if (special) 2.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            val columns = if (wide) 2 else 1
            sectionItems.chunked(columns).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    rowItems.forEach { item ->
                        EquipmentItemCardV4(
                            item = item,
                            onEdit = { onEdit(item) },
                            onMove = { offset -> onMove(item, offset) },
                            onDelete = { onDelete(item) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun EquipmentItemCardV4(
    item: CharacterInventoryItem,
    onEdit: () -> Unit,
    onMove: (Int) -> Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var accumulatedDrag by remember(item.id) { mutableStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StableDragHandle(
                    modifier = Modifier.pointerInput(item.id) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { accumulatedDrag = 0f; dragging = true },
                            onDragEnd = { accumulatedDrag = 0f; dragging = false },
                            onDragCancel = { accumulatedDrag = 0f; dragging = false },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                accumulatedDrag += dragAmount.y
                                while (abs(accumulatedDrag) >= reorderStepPx) {
                                    val direction = if (accumulatedDrag > 0f) 1 else -1
                                    if (onMove(direction)) {
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
                contentDescription = "Mantén pulsado y arrastra para reordenar ${item.name}",
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, style = MaterialTheme.typography.labelLarge)
                    val flags = buildList {
                        if (item.equipped) add("Equipado")
                        if (item.special && item.attuned) add("Sintonizado")
                    }
                    if (flags.isNotEmpty()) {
                        Text(flags.joinToString(" · "), style = MaterialTheme.typography.labelSmall)
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("×${item.quantity}", style = MaterialTheme.typography.labelMedium)
                    item.weightLb?.let {
                        Text("${formatWeightDualV4(it)} / u.", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
            if (item.special && !item.location.isNullOrBlank()) {
                Text("Ubicación: ${item.location}", style = MaterialTheme.typography.labelSmall)
            }
            item.description?.takeIf { item.special && it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.bodySmall)
            }
            item.notes?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.labelSmall)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                StableEditIconButton(
                    onClick = onEdit,
                    contentDescription = "Editar ${item.name}",
                )
                StableRemoveIconButton(
                    onClick = onDelete,
                    contentDescription = "Eliminar ${item.name}",
                )
            }
        }
    }
}

@Composable
private fun EquipmentItemEditorDialogV4(
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
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    var locationMenuOpen by rememberSaveable { mutableStateOf(false) }
    var customLocation by rememberSaveable(location) {
        mutableStateOf(location.isNotBlank() && location !in equipmentLocationsV4)
    }
    val focusManager = LocalFocusManager.current

    AlertDialog(
        modifier = Modifier
            .imePadding()
            .navigationBarsPadding(),
        onDismissRequest = { focusManager.clearFocus() },
        title = { Text(title) },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 520.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
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
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
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
                            supportingText = {
                                weight.trim().replace(',', '.').toDoubleOrNull()?.takeIf { it >= 0.0 }?.let {
                                    Text(formatWeightDualV4(it))
                                }
                            },
                        )
                    }
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = equipped, onCheckedChange = onEquippedChange)
                        Text("Equipado")
                        Checkbox(checked = special, onCheckedChange = onSpecialChange)
                        Text("Equipo especial")
                    }
                }
                if (special) {
                    item {
                        Column {
                            Text("Ubicación", style = MaterialTheme.typography.labelSmall)
                            if (customLocation) {
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = location,
                                        onValueChange = onLocationChange,
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Ubicación personalizada") },
                                        singleLine = true,
                                    )
                                    TextButton(
                                        onClick = {
                                            customLocation = false
                                            locationMenuOpen = true
                                        },
                                    ) { Text("Lista") }
                                }
                            } else {
                                Box {
                                    OutlinedButton(
                                        onClick = { locationMenuOpen = true },
                                        modifier = Modifier.fillMaxWidth(),
                                    ) { Text(location.ifBlank { "Sin ubicación" }) }
                                    DropdownMenu(
                                        expanded = locationMenuOpen,
                                        onDismissRequest = { locationMenuOpen = false },
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Sin ubicación") },
                                            onClick = {
                                                onLocationChange("")
                                                customLocation = false
                                                locationMenuOpen = false
                                            },
                                        )
                                        equipmentLocationsV4.forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option) },
                                                onClick = {
                                                    onLocationChange(option)
                                                    customLocation = false
                                                    locationMenuOpen = false
                                                },
                                            )
                                        }
                                        DropdownMenuItem(
                                            text = { Text("Otro") },
                                            onClick = {
                                                onLocationChange("")
                                                customLocation = true
                                                locationMenuOpen = false
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = attuned, onCheckedChange = onAttunedChange)
                            Text("Sintonizado")
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = onDescriptionChange,
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 6,
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = onNotesChange,
                        label = { Text("Notas") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4,
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

@Composable
private fun CurrencyCardV4(
    currencies: List<CharacterCurrency>,
    onCurrenciesChange: (List<CharacterCurrency>) -> Unit,
    onAddCurrency: () -> Unit,
    wide: Boolean,
) {
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
                Text("Monedas", style = MaterialTheme.typography.titleSmall)
                TextButton(onClick = onAddCurrency) { Text("+ Añadir") }
            }
            val columns = if (wide) 3 else 2
            currencies.chunked(columns).forEach { rowCurrencies ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    rowCurrencies.forEach { currency ->
                        CurrencyCellV4(
                            currency = currency,
                            currencies = currencies,
                            onCurrenciesChange = onCurrenciesChange,
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(columns - rowCurrencies.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CurrencyCellV4(
    currency: CharacterCurrency,
    currencies: List<CharacterCurrency>,
    onCurrenciesChange: (List<CharacterCurrency>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by rememberSaveable(currency.key, currency.amount) { mutableStateOf(currency.amount.toString()) }
    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            value = sanitizeSignedIntEquipmentV4(raw)
            value.toIntOrNull()?.let { parsed ->
                onCurrenciesChange(
                    currencies.map { existing ->
                        if (existing.key == currency.key) existing.copy(amount = parsed) else existing
                    },
                )
            }
        },
        modifier = modifier.fillMaxWidth(),
        label = { Text(currency.name, maxLines = 1) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = if (currency.isDefault) {
            null
        } else {
            {
                StableRemoveIconButton(
                    onClick = { onCurrenciesChange(currencies.filterNot { it.key == currency.key }) },
                    contentDescription = "Eliminar moneda ${currency.name}",
                )
            }
        },
    )
}

private fun sanitizeUnsignedIntEquipmentV4(raw: String): String = raw.filter(Char::isDigit)

private fun sanitizeSignedIntEquipmentV4(raw: String): String {
    if (raw.isBlank()) return ""
    val sign = raw.firstOrNull()?.takeIf { it == '+' || it == '-' }?.toString().orEmpty()
    val digits = raw.drop(if (sign.isEmpty()) 0 else 1).filter(Char::isDigit)
    return sign + digits
}

private fun sanitizeDecimalEquipmentV4(raw: String): String {
    val normalized = raw.replace(',', '.')
    var separatorUsed = false
    return buildString {
        normalized.forEach { char ->
            when {
                char.isDigit() -> append(char)
                char == '.' && !separatorUsed -> {
                    append('.')
                    separatorUsed = true
                }
            }
        }
    }
}

private fun formatDecimalInputV4(value: Double): String = formatCompactDecimalV4(value, decimalComma = false)

private fun formatWeightDualV4(lb: Double): String =
    "${formatCompactDecimalV4(lb)} lb (${formatCompactDecimalV4(lb * 0.5)} kg)"

private fun formatCompactDecimalV4(value: Double, decimalComma: Boolean = true): String {
    val rounded = when {
        abs(value - value.toLong()) < 0.000001 -> value.toLong().toString()
        else -> {
            val raw = "%.3f".format(java.util.Locale.US, value)
            raw.trimEnd('0').trimEnd('.')
        }
    }
    return if (decimalComma) rounded.replace('.', ',') else rounded
}
