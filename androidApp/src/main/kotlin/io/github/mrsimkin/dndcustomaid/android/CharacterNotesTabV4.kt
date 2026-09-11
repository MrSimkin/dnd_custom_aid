package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
    val gridState = rememberLazyGridState()
    val reorderCoordinator = rememberCharacterReorderCoordinatorV4()
    val order = runCatching { CharacterPresentationOrder.valueOf(orderName) }
        .getOrDefault(CharacterPresentationOrder.MANUAL)
    val activeFilters = activeFiltersText.split(NOTE_FILTER_SEPARATOR_G3).filter { it.isNotBlank() }.toSet()
    val query = CharacterCollectionQuery(searchText = searchText, activeFilterKeys = activeFilters)
    val visibleCards = presentCharacterNotes(draft.cards, order, query)
    val reorderAvailable = structuralEditingEnabled && order == CharacterPresentationOrder.MANUAL &&
        query.searchText.isBlank() && query.activeFilterKeys.isEmpty()
    val canReorder = reorderAvailable && visibleCards.size > 1
    val noteColumns = constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)
    val canonicalCards = normalizeCharacterNotes(draft.cards)
    val canonicalIds = canonicalCards.map { it.id.toString() }
    val cardsById = draft.cards.associateBy { it.id.toString() }

    val reorderSession = rememberCharacterReorderSessionV4(
        sessionKey = "notes",
        canonicalOrder = canonicalIds,
        enabled = canReorder,
        coordinator = reorderCoordinator,
        onCommitOrder = { committedIds ->
            val currentById = draft.cards.associateBy { it.id.toString() }
            val reordered = committedIds.mapNotNull(currentById::get)
            if (reordered.size == draft.cards.size) {
                onDraftChange(
                    draft.copy(
                        cards = reordered.mapIndexed { index, note -> note.copy(sortOrder = index) },
                    ),
                )
            }
        },
        onHaptic = haptic,
        autoScrollBy = { delta -> gridState.scrollBy(delta) },
    )
    CharacterReorderSessionAutoScrollEffectV4(reorderSession)

    val layoutCards = if (reorderAvailable) {
        reorderSession.previewOrder.mapNotNull(cardsById::get)
    } else {
        visibleCards
    }
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

    fun duplicate(note: CharacterNote) {
        if (!structuralEditingEnabled) return
        val copied = duplicateCharacterNote(
            source = note,
            newId = Uuid.random(),
            sortOrder = nextCharacterNoteSortOrder(draft.cards),
        )
        onDraftChange(draft.copy(cards = normalizeCharacterNotes(draft.cards + copied)))
    }

    CharacterReorderOverlayHostV4(
        session = reorderSession,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        liftedContent = { draggedId ->
            cardsById[draggedId]?.let { note ->
                CharacterNoteCardV4(
                    note = note,
                    reorderSession = null,
                    onEdit = {},
                    onDuplicate = {},
                    onDelete = {},
                    structuralEditingEnabled = false,
                    lifted = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(noteColumns),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .characterReorderSessionViewportV4(reorderSession),
            contentPadding = PaddingValues(
                start = appSpacingV4(if (wide) 14.dp else 5.dp),
                end = appSpacingV4(if (wide) 14.dp else 5.dp),
                top = appSpacingV4(7.dp),
                bottom = appSpacingV4(88.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        ) {
            item(
                key = "general-notes",
                span = { GridItemSpan(noteColumns) },
            ) {
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
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Escribe aquí…") },
                            minLines = characterCompactTextAreaMinLinesV4(3),
                            maxLines = 10,
                            supportingText = {
                                if (draft.generalNotes.length > 400) {
                                    Text("Texto largo: desliza dentro del campo para recorrerlo.")
                                }
                            },
                        )
                    }
                }
            }

            item(
                key = "titled-notes-tools",
                span = { GridItemSpan(noteColumns) },
            ) {
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

            item(
                key = "titled-notes-help",
                span = { GridItemSpan(noteColumns) },
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp))) {
                    CharacterHelpV4("Tarjetas opcionales para separar referencias concretas. La búsqueda revisa título y contenido.")
                    if (visibleCards.isNotEmpty() && !reorderAvailable) {
                        Text(
                            if (order == CharacterPresentationOrder.ALPHABETICAL) {
                                "A–Z es solo una vista. Vuelve a Manual para reordenar."
                            } else {
                                "Limpia búsqueda y filtros para reordenar."
                            },
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }

            if (draft.cards.isEmpty()) {
                item(
                    key = "titled-notes-empty",
                    span = { GridItemSpan(noteColumns) },
                ) {
                    CharacterUsefulEmptyState(
                        title = "Sin notas con título",
                        message = "Puedes usar solo Notas generales o añadir una tarjeta para una referencia concreta.",
                        onAdd = if (structuralEditingEnabled) ::beginAdd else null,
                        addLabel = "Añadir nota",
                    )
                }
            } else if (visibleCards.isEmpty()) {
                item(
                    key = "titled-notes-no-results",
                    span = { GridItemSpan(noteColumns) },
                ) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "No hay notas que coincidan con la búsqueda y filtros actuales.",
                            modifier = Modifier.padding(appSpacingV4(10.dp)),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            } else {
                layoutCards.forEach { note ->
                    item(key = "note-${note.id}") {
                        CharacterNoteCardV4(
                            note = note,
                            reorderSession = reorderSession.takeIf { canReorder },
                            onEdit = { beginEdit(note) },
                            onDuplicate = { duplicate(note) },
                            onDelete = { deleteId = note.id.toString() },
                            structuralEditingEnabled = structuralEditingEnabled,
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth(),
                        )
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
    reorderSession: CharacterReorderSessionV4?,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    structuralEditingEnabled: Boolean,
    lifted: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val id = note.id.toString()
    val geometryModifier = if (reorderSession != null && !lifted) {
        Modifier
            .characterReorderSessionBoundsV4(reorderSession, id)
            .characterReorderPlaceholderV4(reorderSession, id)
            .characterReorderSessionSemanticsV4(reorderSession, id)
    } else {
        Modifier
    }
    val pickupModifier = if (reorderSession != null && !lifted) {
        Modifier.characterReorderSessionDragHandleV4(reorderSession, id)
    } else {
        Modifier
    }
    val activePlaceholder = reorderSession?.draggedId == id

    Surface(
        modifier = modifier
            .then(geometryModifier)
            .clickable(
                enabled = structuralEditingEnabled && !lifted && !activePlaceholder,
                onClick = onEdit,
            ),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(5.dp)),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(pickupModifier),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            ) {
                Text(
                    note.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    note.content.ifBlank { "Sin contenido" },
                    style = if (note.content.isBlank()) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (structuralEditingEnabled && !lifted) {
                StableDuplicateIconButton(onClick = onDuplicate, contentDescription = "Duplicar ${note.title}")
                StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${note.title}")
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
            modifier = Modifier.fillMaxWidth(),
            minLines = characterCompactTextAreaMinLinesV4(3),
            maxLines = 8,
            supportingText = {
                if (content.length > 400) {
                    Text("Texto largo: desliza dentro del campo para recorrerlo.")
                }
            },
        )
    }
}
