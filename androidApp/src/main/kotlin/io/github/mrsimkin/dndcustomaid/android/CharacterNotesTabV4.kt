package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_NOTE_EMPTY_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_NOTE_WITH_CONTENT_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCollectionQuery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterNote
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder
import io.github.mrsimkin.dndcustomaid.shared.character.duplicateCharacterNote
import io.github.mrsimkin.dndcustomaid.shared.character.moveCharacterNoteManual
import io.github.mrsimkin.dndcustomaid.shared.character.nextCharacterNoteSortOrder
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterNotes
import io.github.mrsimkin.dndcustomaid.shared.character.presentCharacterNotes
import kotlin.uuid.Uuid

private const val NOTE_FILTER_SEPARATOR_G3 = "\u001E"

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CharacterNotesTabV4(
    draft: CharacterNotesDraftV4,
    onDraftChange: (CharacterNotesDraftV4) -> Unit,
    structuralEditingEnabled: Boolean,
    wide: Boolean,
    hapticsEnabled: Boolean = true,
) {
    var searchText by rememberSaveable("note-search") { mutableStateOf("") }
    var activeFiltersText by rememberSaveable("note-filters") { mutableStateOf("") }
    var orderName by rememberSaveable("note-order") { mutableStateOf(CharacterPresentationOrder.MANUAL.name) }
    var editorOpen by rememberSaveable("note-editor-open") { mutableStateOf(false) }
    var editingId by rememberSaveable("note-editor-id") { mutableStateOf<String?>(null) }
    var editorTitle by rememberSaveable("note-editor-title") { mutableStateOf("") }
    var editorContent by rememberSaveable("note-editor-content") { mutableStateOf("") }
    var deleteId by rememberSaveable("note-delete-id") { mutableStateOf<String?>(null) }
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)
    val listState = rememberLazyListState()
    val order = runCatching { CharacterPresentationOrder.valueOf(orderName) }
        .getOrDefault(CharacterPresentationOrder.MANUAL)
    val activeFilters = activeFiltersText.split(NOTE_FILTER_SEPARATOR_G3).filter { it.isNotBlank() }.toSet()
    val query = CharacterCollectionQuery(searchText = searchText, activeFilterKeys = activeFilters)
    val visibleCards = presentCharacterNotes(draft.cards, order, query)
    val canReorder = structuralEditingEnabled && order == CharacterPresentationOrder.MANUAL &&
        query.searchText.isBlank() && query.activeFilterKeys.isEmpty()
    val noteColumns = constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)
    val filters = listOf(
        CharacterFilterOptionV4(
            key = CHARACTER_NOTE_WITH_CONTENT_FILTER_KEY,
            label = "Con contenido",
            count = draft.cards.count { it.content.isNotBlank() },
        ),
        CharacterFilterOptionV4(
            key = CHARACTER_NOTE_EMPTY_FILTER_KEY,
            label = "Sin contenido",
            count = draft.cards.count { it.content.isBlank() },
        ),
    )

    fun updateQuery(updated: CharacterCollectionQuery) {
        searchText = updated.searchText
        activeFiltersText = updated.activeFilterKeys.sorted().joinToString(NOTE_FILTER_SEPARATOR_G3)
    }

    fun beginAdd() {
        if (!structuralEditingEnabled) return
        editingId = null
        editorTitle = ""
        editorContent = ""
        editorOpen = true
    }

    fun beginEdit(note: CharacterNote) {
        if (!structuralEditingEnabled) return
        editingId = note.id.toString()
        editorTitle = note.title
        editorContent = note.content
        editorOpen = true
    }

    fun move(noteId: Uuid, offset: Int): Boolean {
        if (!canReorder) return false
        val before = normalizeCharacterNotes(draft.cards)
        val moved = moveCharacterNoteManual(draft.cards, noteId, offset)
        if (moved == before) return false
        onDraftChange(draft.copy(cards = moved))
        return true
    }

    fun duplicate(note: CharacterNote) {
        if (!structuralEditingEnabled) return
        val copied = duplicateCharacterNote(
            source = note,
            newId = Uuid.random(),
            sortOrder = nextCharacterNoteSortOrder(draft.cards),
        )
        onDraftChange(draft.copy(cards = normalizeCharacterNotes(draft.cards + copied)))
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = appSpacingV4(if (wide) 14.dp else 5.dp),
            end = appSpacingV4(if (wide) 14.dp else 5.dp),
            top = appSpacingV4(7.dp),
            bottom = appSpacingV4(88.dp),
        ),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
    ) {
        item(key = "general-notes") {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = appSpacingV4(if (wide) 12.dp else 7.dp),
                            vertical = appSpacingV4(8.dp),
                        ),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                ) {
                    Text("Notas generales", style = MaterialTheme.typography.titleSmall)
                    CharacterHelpV4("Espacio libre para cualquier información que quieras conservar en la ficha.")
                    OutlinedTextField(
                        value = draft.generalNotes,
                        onValueChange = { onDraftChange(draft.copy(generalNotes = it)) },
                        enabled = structuralEditingEnabled,
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

        stickyHeader(key = "titled-notes-tools") {
            CharacterCollectionToolbarV4(
                itemCount = visibleCards.size,
                query = query,
                onQueryChange = ::updateQuery,
                order = order,
                onOrderChange = { orderName = it.name },
                filters = filters,
                searchLabel = "Buscar notas",
                collapsibleSearch = true,
                showItemCount = false,
                compactOrderControl = true,
                contextContent = {
                    Text(
                        "Notas con título",
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                onAdd = if (structuralEditingEnabled) ::beginAdd else null,
            )
        }

        item(key = "titled-notes-help") {
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp))) {
                CharacterHelpV4("Tarjetas opcionales para separar referencias concretas. La búsqueda revisa título y contenido.")
                if (!canReorder && visibleCards.isNotEmpty()) {
                    Text(
                        if (order == CharacterPresentationOrder.ALPHABETICAL) {
                            "A–Z es solo una vista. Vuelve a Manual para arrastrar sin perder el orden guardado."
                        } else {
                            "Limpia búsqueda y filtros para reordenar manualmente."
                        },
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }

        if (draft.cards.isEmpty()) {
            item(key = "titled-notes-empty") {
                CharacterUsefulEmptyState(
                    title = "Sin notas con título",
                    message = "Puedes usar solo Notas generales o añadir una tarjeta para una referencia concreta.",
                    onAdd = if (structuralEditingEnabled) ::beginAdd else null,
                    addLabel = "Añadir nota",
                )
            }
        } else if (visibleCards.isEmpty()) {
            item(key = "titled-notes-no-results") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "No hay notas que coincidan con la búsqueda y filtros actuales.",
                        modifier = Modifier.padding(appSpacingV4(10.dp)),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        } else {
            visibleCards.chunked(noteColumns).forEachIndexed { rowIndex, rowCards ->
                item(key = "note-row-${rowCards.first().id}") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                        verticalAlignment = Alignment.Top,
                    ) {
                        rowCards.forEachIndexed { columnIndex, note ->
                            val gridIndex = rowIndex * noteColumns + columnIndex
                            CharacterNoteCardV4(
                                note = note,
                                gridIndex = gridIndex,
                                gridItemCount = visibleCards.size,
                                gridColumns = noteColumns,
                                reorderEnabled = canReorder && visibleCards.size > 1,
                                onEdit = { beginEdit(note) },
                                onDuplicate = { duplicate(note) },
                                onDelete = { deleteId = note.id.toString() },
                                onMove = { offset -> move(note.id, offset) },
                                structuralEditingEnabled = structuralEditingEnabled,
                                onHaptic = haptic,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(noteColumns - rowCards.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
    }

    if (editorOpen && structuralEditingEnabled) {
        CharacterNoteEditorDialogV4(
            title = if (editingId == null) "Añadir nota" else "Editar nota",
            noteTitle = editorTitle,
            content = editorContent,
            valid = editorTitle.trim().isNotEmpty(),
            onTitleChange = { editorTitle = it },
            onContentChange = { editorContent = it },
            onDismiss = { editorOpen = false },
            onSave = {
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
                onDraftChange(draft.copy(cards = normalizeCharacterNotes(updated)))
                editorOpen = false
            },
        )
    }

    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->
        val target = draft.cards.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.title,
                itemTypeLabel = "nota",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    onDraftChange(draft.copy(cards = normalizeCharacterNotes(draft.cards.filterNot { it.id == target.id })))
                    haptic(CharacterHapticEventV4.DESTRUCTIVE)
                    deleteId = null
                },
            )
        }
    }
}

@Composable
private fun CharacterNoteCardV4(
    note: CharacterNote,
    gridIndex: Int,
    gridItemCount: Int,
    gridColumns: Int,
    reorderEnabled: Boolean,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onMove: (Int) -> Boolean,
    structuralEditingEnabled: Boolean,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dragState by remember(note.id) { mutableStateOf(CharacterDragVisualStateV4()) }
    val reorderModifier = if (gridColumns > 1) {
        Modifier.characterMeasuredGridReorderDragV4(
            enabled = reorderEnabled,
            onHaptic = onHaptic,
            onMove = { rowDelta, columnDelta ->
                val currentRow = gridIndex / gridColumns
                val currentColumn = gridIndex % gridColumns
                val targetRow = currentRow + rowDelta
                val targetColumn = currentColumn + columnDelta
                val targetIndex = targetRow * gridColumns + targetColumn
                val targetValid =
                    targetRow >= 0 &&
                        targetColumn in 0 until gridColumns &&
                        targetIndex in 0 until gridItemCount
                if (targetValid) onMove(targetIndex - gridIndex) else false
            },
            onVisualStateChange = { dragState = it },
        )
    } else {
        Modifier.characterMeasuredReorderDragV4(
            enabled = reorderEnabled,
            onHaptic = onHaptic,
            onMove = onMove,
            onVisualStateChange = { dragState = it },
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        CharacterDropIndicatorV4(visible = dragState.showDropBefore)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .then(reorderModifier)
                .characterDragFeedbackV4(dragState)
                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(5.dp)),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        note.title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (structuralEditingEnabled) {
                        StableDuplicateIconButton(onClick = onDuplicate, contentDescription = "Duplicar ${note.title}")
                        StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${note.title}")
                    }
                }
                Text(
                    note.content.ifBlank { "Sin contenido" },
                    style = if (note.content.isBlank()) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        CharacterDropIndicatorV4(visible = dragState.showDropAfter)
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
    onSave: () -> Unit,
) {
    CharacterImeSafeEditorDialog(
        title = title,
        onCancel = onDismiss,
        onSave = onSave,
        saveLabel = "Guardar",
        saveEnabled = valid,
    ) {
        OutlinedTextField(
            value = noteTitle,
            onValueChange = onTitleChange,
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        CharacterInlineValidationMessage(
            if (noteTitle.isNotEmpty() && noteTitle.trim().isEmpty()) "Escribe un título para guardar la nota." else null,
        )
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
