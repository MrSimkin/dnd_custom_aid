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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterNote
import kotlin.math.abs
import kotlin.uuid.Uuid

@Composable
internal fun CharacterNotesTabV4(
    draft: CharacterNotesDraftV4,
    onDraftChange: (CharacterNotesDraftV4) -> Unit,
    wide: Boolean,
) {
    var editorOpen by rememberSaveable("note-editor-open") { mutableStateOf(false) }
    var editingId by rememberSaveable("note-editor-id") { mutableStateOf<String?>(null) }
    var editorTitle by rememberSaveable("note-editor-title") { mutableStateOf("") }
    var editorContent by rememberSaveable("note-editor-content") { mutableStateOf("") }
    var deleteId by rememberSaveable("note-delete-id") { mutableStateOf<String?>(null) }

    fun normalize(cards: List<CharacterNote>): List<CharacterNote> =
        cards.mapIndexed { index, note -> note.copy(sortOrder = index) }

    fun beginAdd() {
        editingId = null
        editorTitle = ""
        editorContent = ""
        editorOpen = true
    }

    fun beginEdit(note: CharacterNote) {
        editingId = note.id.toString()
        editorTitle = note.title
        editorContent = note.content
        editorOpen = true
    }

    fun move(index: Int, offset: Int): Boolean {
        val target = index + offset
        if (target !in draft.cards.indices) return false
        val reordered = draft.cards.toMutableList()
        val item = reordered.removeAt(index)
        reordered.add(target, item)
        onDraftChange(draft.copy(cards = normalize(reordered)))
        return true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = if (wide) 14.dp else 5.dp,
            end = if (wide) 14.dp else 5.dp,
            top = 7.dp,
            bottom = 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "general-notes") {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (wide) 12.dp else 7.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text("Notas generales", style = MaterialTheme.typography.titleSmall)
                    Text(
                        "Espacio libre para cualquier información que quieras conservar en la ficha.",
                        style = MaterialTheme.typography.labelSmall,
                    )
                    OutlinedTextField(
                        value = draft.generalNotes,
                        onValueChange = { onDraftChange(draft.copy(generalNotes = it)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = if (wide) 220.dp else 180.dp,
                                max = if (wide) 380.dp else 300.dp,
                            ),
                        placeholder = { Text("Escribe aquí…") },
                        minLines = if (wide) 9 else 7,
                        maxLines = if (wide) 18 else 14,
                        supportingText = {
                            if (draft.generalNotes.length > 400) {
                                Text("↕ Texto largo: desliza dentro del campo para recorrerlo.")
                            }
                        },
                    )
                }
            }
        }

        item(key = "titled-notes") {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (wide) 12.dp else 7.dp, vertical = 7.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Notas con título", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Tarjetas opcionales para separar referencias concretas.",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                        TextButton(onClick = ::beginAdd) { Text("+ Añadir") }
                    }

                    if (draft.cards.isEmpty()) {
                        Text(
                            "Sin notas con título. Puedes usar solo Notas generales si no necesitas separarlas.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    } else if (wide) {
                        draft.cards.chunked(2).forEach { rowCards ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                verticalAlignment = Alignment.Top,
                            ) {
                                rowCards.forEach { note ->
                                    val index = draft.cards.indexOfFirst { it.id == note.id }
                                    CharacterNoteCardV4(
                                        note = note,
                                        onEdit = { beginEdit(note) },
                                        onDelete = { deleteId = note.id.toString() },
                                        onMove = { offset -> move(index, offset) },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                repeat(2 - rowCards.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    } else {
                        draft.cards.forEachIndexed { index, note ->
                            CharacterNoteCardV4(
                                note = note,
                                onEdit = { beginEdit(note) },
                                onDelete = { deleteId = note.id.toString() },
                                onMove = { offset -> move(index, offset) },
                            )
                        }
                    }
                }
            }
        }
    }

    if (editorOpen) {
        CharacterNoteEditorDialogV4(
            title = if (editingId == null) "Añadir nota" else "Editar nota",
            noteTitle = editorTitle,
            content = editorContent,
            valid = editorTitle.trim().isNotEmpty(),
            onTitleChange = { editorTitle = it },
            onContentChange = { editorContent = it },
            onDismiss = { editorOpen = false },
            onApply = {
                val existing = editingId?.let { id -> draft.cards.firstOrNull { it.id.toString() == id } }
                val note = CharacterNote(
                    id = existing?.id ?: Uuid.random(),
                    title = editorTitle.trim(),
                    content = editorContent,
                    sortOrder = existing?.sortOrder ?: draft.cards.size,
                )
                val updated = if (existing == null) {
                    draft.cards + note
                } else {
                    draft.cards.map { item -> if (item.id == existing.id) note else item }
                }
                onDraftChange(draft.copy(cards = normalize(updated)))
                editorOpen = false
            },
        )
    }

    deleteId?.let { id ->
        val target = draft.cards.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            AlertDialog(
                onDismissRequest = { deleteId = null },
                title = { Text("Eliminar nota") },
                text = { Text("¿Eliminar «${target.title}»? El cambio se hará persistente al guardar la ficha.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDraftChange(draft.copy(cards = normalize(draft.cards.filterNot { it.id == target.id })))
                            deleteId = null
                        },
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { deleteId = null }) { Text("Cancelar") }
                },
            )
        }
    }
}

@Composable
private fun CharacterNoteCardV4(
    note: CharacterNote,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMove: (Int) -> Boolean,
    modifier: Modifier = Modifier,
) {
    var accumulatedDrag by remember(note.id) { mutableStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }

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
                    modifier = Modifier.pointerInput(note.id) {
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
                contentDescription = "Mantén pulsado y arrastra para reordenar ${note.title}",
                )
                Text(
                    note.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                note.content.ifBlank { "Sin contenido" },
                style = if (note.content.isBlank()) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

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
private fun CharacterNoteEditorDialogV4(
    title: String,
    noteTitle: String,
    content: String,
    valid: Boolean,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(title) },
        text = {
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 540.dp)
                    .imePadding()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = onTitleChange,
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                }
                item {
                    OutlinedTextField(
                        value = content,
                        onValueChange = onContentChange,
                        label = { Text("Contenido") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 220.dp, max = 340.dp),
                        minLines = 8,
                        maxLines = 16,
                        supportingText = {
                            if (content.length > 400) {
                                Text("↕ Texto largo: desliza dentro del campo para recorrerlo.")
                            }
                        },
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
