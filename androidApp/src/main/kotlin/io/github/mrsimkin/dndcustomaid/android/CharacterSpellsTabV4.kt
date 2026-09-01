package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource
import kotlin.math.abs
import kotlin.uuid.Uuid

internal data class SpellSourceClassOptionV4(
    val id: Uuid,
    val name: String,
)

@Composable
internal fun CharacterSpellsTabV4(
    draft: CharacterSpellcastingDraftV4,
    classOptions: List<SpellSourceClassOptionV4>,
    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,
    wide: Boolean,
) {
    var selectedSourceId by rememberSaveable("spell-source-selection") { mutableStateOf<String?>(null) }
    var managerOpen by rememberSaveable("spell-source-manager") { mutableStateOf(false) }
    var editorOpen by rememberSaveable("spell-source-editor") { mutableStateOf(false) }
    var editingSourceId by rememberSaveable("spell-source-edit-id") { mutableStateOf<String?>(null) }
    var editorName by rememberSaveable("spell-source-edit-name") { mutableStateOf("") }
    var editorLinkedClassId by rememberSaveable("spell-source-edit-class") { mutableStateOf<String?>(null) }
    var deleteSourceId by rememberSaveable("spell-source-delete-id") { mutableStateOf<String?>(null) }

    val selectedSource = selectedSourceId?.let { selectedId ->
        draft.sources.firstOrNull { it.id.toString() == selectedId }
    }
    fun updateSources(updated: List<CharacterSpellcastingSource>) {
        onDraftChange(
            draft.copy(
                sources = updated.mapIndexed { index, source -> source.copy(sortOrder = index) },
            ),
        )
    }

    fun beginAddSource() {
        editingSourceId = null
        editorName = ""
        editorLinkedClassId = null
        managerOpen = false
        editorOpen = true
    }

    fun beginEditSource(source: CharacterSpellcastingSource) {
        editingSourceId = source.id.toString()
        editorName = source.name
        editorLinkedClassId = source.linkedClassId?.toString()
        managerOpen = false
        editorOpen = true
    }

    fun requestDeleteSource(source: CharacterSpellcastingSource) {
        deleteSourceId = source.id.toString()
        managerOpen = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (wide) 10.dp else 5.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (selectedSource == null) {
                    Button(onClick = { selectedSourceId = null }) { Text("Todos", maxLines = 1) }
                } else {
                    OutlinedButton(onClick = { selectedSourceId = null }) { Text("Todos", maxLines = 1) }
                }
                draft.sources.forEach { source ->
                    if (selectedSource?.id == source.id) {
                        Button(onClick = { selectedSourceId = source.id.toString() }) {
                            Text(source.name, maxLines = 1)
                        }
                    } else {
                        OutlinedButton(onClick = { selectedSourceId = source.id.toString() }) {
                            Text(source.name, maxLines = 1)
                        }
                    }
                }
            }
            StableSettingsIconButton(
                onClick = { managerOpen = true },
                contentDescription = "Gestionar fuentes de conjuros",
            )
        }

        HorizontalDivider()

        CharacterSpellListV4(
    draft = draft,
    selectedSourceId = selectedSource?.id,
    onDraftChange = onDraftChange,
    wide = wide,
)
    }

    if (managerOpen) {
        SourceManagerDialogV4(
            sources = draft.sources,
            classOptions = classOptions,
            onMove = { index, offset ->
                val target = index + offset
                if (target !in draft.sources.indices) {
                    false
                } else {
                    val reordered = draft.sources.toMutableList()
                    val item = reordered.removeAt(index)
                    reordered.add(target, item)
                    updateSources(reordered)
                    true
                }
            },
            onAdd = ::beginAddSource,
            onEdit = ::beginEditSource,
            onDelete = ::requestDeleteSource,
            onDismiss = { managerOpen = false },
        )
    }

    if (editorOpen) {
        SourceEditorDialogV4(
            title = if (editingSourceId == null) "Añadir fuente" else "Editar fuente",
            name = editorName,
            linkedClassId = editorLinkedClassId,
            classOptions = classOptions,
            onNameChange = { editorName = it },
            onLinkedClassChange = { editorLinkedClassId = it },
            onCancel = {
                editorOpen = false
                managerOpen = true
            },
            onApply = {
                val existing = editingSourceId?.let { id ->
                    draft.sources.firstOrNull { it.id.toString() == id }
                }
                val linkedClass = editorLinkedClassId?.let { id ->
                    classOptions.firstOrNull { it.id.toString() == id }?.id
                }
                val source = CharacterSpellcastingSource(
                    id = existing?.id ?: Uuid.random(),
                    name = editorName.trim(),
                    linkedClassId = linkedClass,
                    sortOrder = existing?.sortOrder ?: draft.sources.size,
                )
                val updated = if (existing == null) {
                    draft.sources + source
                } else {
                    draft.sources.map { if (it.id == existing.id) source else it }
                }
                updateSources(updated)
                editorOpen = false
                managerOpen = true
            },
        )
    }

    deleteSourceId?.let { id ->
        val target = draft.sources.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteSourceId = null
        } else {
            val associationCount = draft.spells.count { spell ->
                spell.sourceAssociations.any { it.sourceId == target.id }
            }
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Eliminar fuente") },
                text = {
                    Text(
                        if (associationCount > 0) {
                            "«${target.name}» está asociada a $associationCount conjuros. Se eliminará esta fuente y solo sus asociaciones. Los conjuros conceptuales permanecerán en la ficha y conservarán sus otras fuentes."
                        } else {
                            "¿Eliminar la fuente «${target.name}»?"
                        },
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val remainingSources = draft.sources
                                .filterNot { it.id == target.id }
                                .mapIndexed { index, source -> source.copy(sortOrder = index) }
                            val remainingSpells = draft.spells.map { spell ->
                                spell.copy(
                                    sourceAssociations = spell.sourceAssociations.filterNot {
                                        it.sourceId == target.id
                                    },
                                )
                            }
                            onDraftChange(
                                draft.copy(
                                    sources = remainingSources,
                                    spells = remainingSpells,
                                ),
                            )
                            if (selectedSourceId == target.id.toString()) {
                                selectedSourceId = null
                            }
                            deleteSourceId = null
                            managerOpen = true
                        },
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            deleteSourceId = null
                            managerOpen = true
                        },
                    ) { Text("Cancelar") }
                },
            )
        }
    }
}

@Composable
private fun SourceManagerDialogV4(
    sources: List<CharacterSpellcastingSource>,
    classOptions: List<SpellSourceClassOptionV4>,
    onMove: (Int, Int) -> Boolean,
    onAdd: () -> Unit,
    onEdit: (CharacterSpellcastingSource) -> Unit,
    onDelete: (CharacterSpellcastingSource) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fuentes de conjuros") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 500.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                item {
                    Text(
                        "Las fuentes organizan una sola colección de conjuros. Pueden vincularse opcionalmente a una clase o ser completamente personalizadas.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                item {
                    TextButton(onClick = onAdd) { Text("Añadir fuente") }
                }
                if (sources.isEmpty()) {
                    item {
                        Text("Sin fuentes registradas.", style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    items(sources.size) { index ->
                        val source = sources[index]
                        SourceManagerRowV4(
                            source = source,
                            classOptions = classOptions,
                            onMove = { offset -> onMove(index, offset) },
                            onEdit = { onEdit(source) },
                            onDelete = { onDelete(source) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        },
    )
}

@Composable
private fun SourceManagerRowV4(
    source: CharacterSpellcastingSource,
    classOptions: List<SpellSourceClassOptionV4>,
    onMove: (Int) -> Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var accumulatedDrag by remember(source.id) { mutableStateOf(0f) }
    val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }
    val linkedClassName = source.linkedClassId?.let { linkedId ->
        classOptions.firstOrNull { it.id == linkedId }?.name?.ifBlank { "Clase sin nombre" }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StableDragHandle(
                modifier = Modifier.pointerInput(source.id) {
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
                contentDescription = "Mantén pulsado y arrastra para reordenar ${source.name}",
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(source.name, style = MaterialTheme.typography.labelLarge)
                Text(
                    linkedClassName?.let { "Clase vinculada: $it" } ?: "Fuente personalizada / sin clase vinculada",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            TextButton(onClick = onEdit, contentPadding = PaddingValues(horizontal = 6.dp)) {
                Text("Editar")
            }
            TextButton(onClick = onDelete, contentPadding = PaddingValues(horizontal = 6.dp)) {
                Text("Eliminar")
            }
        }
    }
}

@Composable
private fun SourceEditorDialogV4(
    title: String,
    name: String,
    linkedClassId: String?,
    classOptions: List<SpellSourceClassOptionV4>,
    onNameChange: (String) -> Unit,
    onLinkedClassChange: (String?) -> Unit,
    onCancel: () -> Unit,
    onApply: () -> Unit,
) {
    var classMenuOpen by rememberSaveable { mutableStateOf(false) }
    val linkedClassLabel = linkedClassId?.let { id ->
        classOptions.firstOrNull { it.id.toString() == id }?.name?.ifBlank { "Clase sin nombre" }
    } ?: "Sin clase vinculada"

    AlertDialog(
        onDismissRequest = {},
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .imePadding()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Clase vinculada (opcional)", style = MaterialTheme.typography.labelSmall)
                    Box {
                        OutlinedButton(
                            onClick = { classMenuOpen = true },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(linkedClassLabel, maxLines = 1)
                        }
                        DropdownMenu(
                            expanded = classMenuOpen,
                            onDismissRequest = { classMenuOpen = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Sin clase vinculada") },
                                onClick = {
                                    onLinkedClassChange(null)
                                    classMenuOpen = false
                                },
                            )
                            classOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.name.ifBlank { "Clase sin nombre" }) },
                                    onClick = {
                                        onLinkedClassChange(option.id.toString())
                                        classMenuOpen = false
                                    },
                                )
                            }
                        }
                    }
                    Text(
                        "El vínculo es solo una referencia. No crea ni elimina automáticamente fuentes o conjuros.",
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onApply, enabled = name.trim().isNotEmpty()) { Text("Aplicar") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancelar") }
        },
    )
}
