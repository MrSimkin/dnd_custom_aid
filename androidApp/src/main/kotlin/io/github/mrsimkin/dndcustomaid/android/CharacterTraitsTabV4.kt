package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterActivationType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import kotlin.math.abs
import kotlin.uuid.Uuid

@Composable
internal fun CharacterTraitsTabV4(
    traits: List<CharacterTrait>,
    onTraitsChange: (List<CharacterTrait>) -> Unit,
    wide: Boolean,
) {
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorName by rememberSaveable { mutableStateOf("") }
    var editorSource by rememberSaveable { mutableStateOf("") }
    var editorTypeName by rememberSaveable { mutableStateOf(CharacterTraitType.OTHER.name) }
    var editorDescription by rememberSaveable { mutableStateOf("") }
    var editorNotes by rememberSaveable { mutableStateOf("") }
    var editorMaxUses by rememberSaveable { mutableStateOf("") }
    var editorSpentUses by rememberSaveable { mutableStateOf("0") }
    var editorRecovery by rememberSaveable { mutableStateOf("") }
    var editorActivationName by rememberSaveable { mutableStateOf("") }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }

    fun normalize(updated: List<CharacterTrait>): List<CharacterTrait> =
        updated.mapIndexed { index, trait -> trait.copy(sortOrder = index) }

    fun beginAdd() {
        editingId = null
        editorName = ""
        editorSource = ""
        editorTypeName = CharacterTraitType.OTHER.name
        editorDescription = ""
        editorNotes = ""
        editorMaxUses = ""
        editorSpentUses = "0"
        editorRecovery = ""
        editorActivationName = ""
        editorOpen = true
    }

    fun beginEdit(trait: CharacterTrait) {
        editingId = trait.id.toString()
        editorName = trait.name
        editorSource = trait.source
        editorTypeName = trait.type.name
        editorDescription = trait.description
        editorNotes = trait.notes.orEmpty()
        editorMaxUses = trait.maxUses?.toString().orEmpty()
        editorSpentUses = trait.spentUses.toString()
        editorRecovery = trait.recovery.orEmpty()
        editorActivationName = trait.activation?.name.orEmpty()
        editorOpen = true
    }

    fun move(index: Int, offset: Int): Boolean {
        val target = index + offset
        if (target !in traits.indices) return false
        val reordered = traits.toMutableList()
        val item = reordered.removeAt(index)
        reordered.add(target, item)
        onTraitsChange(normalize(reordered))
        return true
    }

    fun updateSpentUses(trait: CharacterTrait, delta: Int) {
        val max = trait.maxUses ?: return
        val next = (trait.spentUses + delta).coerceIn(0, max)
        if (next == trait.spentUses) return
        onTraitsChange(
            normalize(
                traits.map { item ->
                    if (item.id == trait.id) item.copy(spentUses = next) else item
                },
            ),
        )
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
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 7.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Rasgos", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Rasgos de clase, especie/raza, trasfondo, dotes, dones y contenido personalizado.",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                        TextButton(onClick = ::beginAdd) { Text("+ Añadir") }
                    }

                    if (traits.isEmpty()) {
                        Text(
                            "Sin rasgos registrados. La app no crea rasgos automáticamente desde otras secciones.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    } else {
                        val columns = if (wide) 2 else 1
                        traits.chunked(columns).forEach { rowTraits ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                rowTraits.forEach { trait ->
                                    val index = traits.indexOfFirst { it.id == trait.id }
                                    CharacterTraitCardV4(
                                        trait = trait,
                                        onEdit = { beginEdit(trait) },
                                        onDelete = { deleteId = trait.id.toString() },
                                        onMove = { offset -> move(index, offset) },
                                        onSpendUse = { updateSpentUses(trait, 1) },
                                        onRecoverUse = { updateSpentUses(trait, -1) },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                repeat(columns - rowTraits.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (editorOpen) {
        val selectedType = runCatching { CharacterTraitType.valueOf(editorTypeName) }
            .getOrDefault(CharacterTraitType.OTHER)
        val selectedActivation = editorActivationName.takeIf { it.isNotBlank() }?.let { raw ->
            runCatching { CharacterActivationType.valueOf(raw) }.getOrNull()
        }
        val parsedMaxUses = editorMaxUses.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()
        val parsedSpentUses = editorSpentUses.trim().toIntOrNull()
        val maxUsesValid = editorMaxUses.isBlank() || (parsedMaxUses != null && parsedMaxUses > 0)
        val spentUsesValid = if (editorMaxUses.isBlank()) {
            true
        } else {
            parsedMaxUses != null && parsedSpentUses != null && parsedSpentUses in 0..parsedMaxUses
        }
        val valid = editorName.trim().isNotEmpty() && maxUsesValid && spentUsesValid

        CharacterTraitEditorDialogV4(
            title = if (editingId == null) "Añadir rasgo" else "Editar rasgo",
            name = editorName,
            source = editorSource,
            type = selectedType,
            description = editorDescription,
            notes = editorNotes,
            maxUses = editorMaxUses,
            spentUses = editorSpentUses,
            recovery = editorRecovery,
            activation = selectedActivation,
            valid = valid,
            onNameChange = { editorName = it },
            onSourceChange = { editorSource = it },
            onTypeChange = { editorTypeName = it.name },
            onDescriptionChange = { editorDescription = it },
            onNotesChange = { editorNotes = it },
            onMaxUsesChange = { editorMaxUses = traitUnsignedIntegerTextV4(it) },
            onSpentUsesChange = { editorSpentUses = traitUnsignedIntegerTextV4(it).ifBlank { "0" } },
            onRecoveryChange = { editorRecovery = it },
            onActivationChange = { editorActivationName = it?.name.orEmpty() },
            onDismiss = { editorOpen = false },
            onApply = {
                val existing = editingId?.let { id -> traits.firstOrNull { it.id.toString() == id } }
                val maxUses = parsedMaxUses
                val spentUses = if (maxUses == null) 0 else requireNotNull(parsedSpentUses).coerceIn(0, maxUses)
                val trait = CharacterTrait(
                    id = existing?.id ?: Uuid.random(),
                    name = editorName.trim(),
                    source = editorSource.trim(),
                    type = selectedType,
                    description = editorDescription,
                    notes = editorNotes.trim().takeIf { it.isNotEmpty() },
                    maxUses = maxUses,
                    spentUses = spentUses,
                    recovery = editorRecovery.trim().takeIf { it.isNotEmpty() },
                    activation = selectedActivation,
                    sortOrder = existing?.sortOrder ?: traits.size,
                )
                val updated = if (existing == null) {
                    traits + trait
                } else {
                    traits.map { item -> if (item.id == existing.id) trait else item }
                }
                onTraitsChange(normalize(updated))
                editorOpen = false
            },
        )
    }

    deleteId?.let { id ->
        val target = traits.firstOrNull { it.id.toString() == id }
        if (target != null) {
            AlertDialog(
                onDismissRequest = { deleteId = null },
                title = { Text("Eliminar rasgo") },
                text = { Text("¿Eliminar «${target.name}»? El cambio se hará persistente al guardar la ficha.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onTraitsChange(normalize(traits.filterNot { it.id == target.id }))
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
private fun CharacterTraitCardV4(
    trait: CharacterTrait,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMove: (Int) -> Boolean,
    onSpendUse: () -> Unit,
    onRecoverUse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var accumulatedDrag by remember(trait.id) { mutableStateOf(0f) }
    val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }
    val metadata = buildList {
        trait.source.takeIf { it.isNotBlank() }?.let(::add)
        add(characterTraitTypeLabelV4(trait.type))
        trait.activation?.let { add(characterActivationLabelV4(it)) }
    }.joinToString(" · ")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StableDragHandle(
                    modifier = Modifier.pointerInput(trait.id) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { accumulatedDrag = 0f },
                            onDragEnd = { accumulatedDrag = 0f },
                            onDragCancel = { accumulatedDrag = 0f },
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
                    contentDescription = "Mantén pulsado y arrastra para reordenar ${trait.name}",
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(trait.name, style = MaterialTheme.typography.labelLarge)
                    Text(metadata, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
            }

            Text(
                trait.description.ifBlank { "Sin descripción" },
                style = if (trait.description.isBlank()) {
                    MaterialTheme.typography.labelSmall
                } else {
                    MaterialTheme.typography.bodySmall
                },
                maxLines = 2,
            )

            trait.maxUses?.let { maxUses ->
                val remaining = (maxUses - trait.spentUses).coerceIn(0, maxUses)
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "Usos: $remaining / $maxUses · Gastados ${trait.spentUses.coerceIn(0, maxUses)}",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    trait.recovery?.takeIf { it.isNotBlank() }?.let {
                        Text("Recuperación: $it", style = MaterialTheme.typography.labelSmall)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(
                            onClick = onRecoverUse,
                            enabled = trait.spentUses > 0,
                            contentPadding = PaddingValues(horizontal = 7.dp),
                        ) { Text("Recuperar") }
                        TextButton(
                            onClick = onSpendUse,
                            enabled = trait.spentUses < maxUses,
                            contentPadding = PaddingValues(horizontal = 7.dp),
                        ) { Text("Gastar") }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
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
private fun CharacterTraitEditorDialogV4(
    title: String,
    name: String,
    source: String,
    type: CharacterTraitType,
    description: String,
    notes: String,
    maxUses: String,
    spentUses: String,
    recovery: String,
    activation: CharacterActivationType?,
    valid: Boolean,
    onNameChange: (String) -> Unit,
    onSourceChange: (String) -> Unit,
    onTypeChange: (CharacterTraitType) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onMaxUsesChange: (String) -> Unit,
    onSpentUsesChange: (String) -> Unit,
    onRecoveryChange: (String) -> Unit,
    onActivationChange: (CharacterActivationType?) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    var typeMenuOpen by rememberSaveable { mutableStateOf(false) }
    var activationMenuOpen by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {},
        title = { Text(title) },
        text = {
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 520.dp)
                    .imePadding()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(bottom = 120.dp),
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
                    OutlinedTextField(
                        value = source,
                        onValueChange = onSourceChange,
                        label = { Text("Fuente") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
                item {
                    Column {
                        Text("Tipo", style = MaterialTheme.typography.labelSmall)
                        Box {
                            OutlinedButton(
                                onClick = { typeMenuOpen = true },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(characterTraitTypeLabelV4(type))
                            }
                            DropdownMenu(
                                expanded = typeMenuOpen,
                                onDismissRequest = { typeMenuOpen = false },
                            ) {
                                CharacterTraitType.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(characterTraitTypeLabelV4(option)) },
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
                    Column {
                        Text("Activación (opcional)", style = MaterialTheme.typography.labelSmall)
                        Box {
                            OutlinedButton(
                                onClick = { activationMenuOpen = true },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(activation?.let(::characterActivationLabelV4) ?: "Sin especificar")
                            }
                            DropdownMenu(
                                expanded = activationMenuOpen,
                                onDismissRequest = { activationMenuOpen = false },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Sin especificar") },
                                    onClick = {
                                        onActivationChange(null)
                                        activationMenuOpen = false
                                    },
                                )
                                CharacterActivationType.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(characterActivationLabelV4(option)) },
                                        onClick = {
                                            onActivationChange(option)
                                            activationMenuOpen = false
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 5,
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = onNotesChange,
                        label = { Text("Notas (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                    )
                }
                item {
                    Text("Seguimiento manual de usos", style = MaterialTheme.typography.labelLarge)
                    Text(
                        "Deja Usos máximos vacío para desactivar el seguimiento. La app no restaura usos automáticamente.",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        OutlinedTextField(
                            value = maxUses,
                            onValueChange = onMaxUsesChange,
                            label = { Text("Usos máximos") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                        OutlinedTextField(
                            value = if (maxUses.isBlank()) "" else spentUses,
                            onValueChange = onSpentUsesChange,
                            label = { Text("Usos gastados") },
                            modifier = Modifier.weight(1f),
                            enabled = maxUses.isNotBlank(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = recovery,
                        onValueChange = onRecoveryChange,
                        label = { Text("Recuperación (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
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

private fun traitUnsignedIntegerTextV4(raw: String): String = raw.filter(Char::isDigit)

private fun characterTraitTypeLabelV4(type: CharacterTraitType): String = when (type) {
    CharacterTraitType.CLASS -> "Rasgo de clase"
    CharacterTraitType.SPECIES_RACE -> "Rasgo de especie / raza"
    CharacterTraitType.BACKGROUND -> "Rasgo de trasfondo"
    CharacterTraitType.FEAT -> "Dote"
    CharacterTraitType.GIFT_BLESSING -> "Don / bendición"
    CharacterTraitType.OTHER -> "Otro"
}

private fun characterActivationLabelV4(activation: CharacterActivationType): String = when (activation) {
    CharacterActivationType.PASSIVE -> "Pasivo"
    CharacterActivationType.ACTION -> "Acción"
    CharacterActivationType.BONUS_ACTION -> "Acción adicional"
    CharacterActivationType.REACTION -> "Reacción"
    CharacterActivationType.OTHER -> "Otro"
}
