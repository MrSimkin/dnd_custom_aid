package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_FORM_FAVORITE_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCollectionQuery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterForm
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.characterFormSourceFilterKey
import io.github.mrsimkin.dndcustomaid.shared.character.duplicateCharacterForm
import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess
import io.github.mrsimkin.dndcustomaid.shared.character.moveCharacterFormManual
import io.github.mrsimkin.dndcustomaid.shared.character.nextCharacterFormSortOrder
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterFormOrders
import io.github.mrsimkin.dndcustomaid.shared.character.presentCharacterForms
import io.github.mrsimkin.dndcustomaid.shared.character.withQuickAccess
import kotlin.math.abs
import kotlin.uuid.Uuid

private const val FORM_FILTER_SEPARATOR_H1 = "\u001E"

@Composable
internal fun CharacterFormsModuleV4(
    forms: List<CharacterForm>,
    closureState: CharacterClosureState,
    persistedFormIds: Set<Uuid>,
    onFormsChange: (List<CharacterForm>) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
    hapticsEnabled: Boolean,
) {
    var searchText by rememberSaveable("h1-forms-search") { mutableStateOf("") }
    var activeFiltersText by rememberSaveable("h1-forms-filters") { mutableStateOf("") }
    var orderName by rememberSaveable("h1-forms-order") { mutableStateOf(CharacterPresentationOrder.MANUAL.name) }

    var editorOpen by rememberSaveable("h1-form-editor-open") { mutableStateOf(false) }
    var editingId by rememberSaveable("h1-form-editor-id") { mutableStateOf<String?>(null) }
    var editorName by rememberSaveable("h1-form-name") { mutableStateOf("") }
    var editorSource by rememberSaveable("h1-form-source") { mutableStateOf("") }
    var editorCr by rememberSaveable("h1-form-cr") { mutableStateOf("") }
    var editorAc by rememberSaveable("h1-form-ac") { mutableStateOf("") }
    var editorHp by rememberSaveable("h1-form-hp") { mutableStateOf("") }
    var editorMovement by rememberSaveable("h1-form-movement") { mutableStateOf("") }
    var editorSenses by rememberSaveable("h1-form-senses") { mutableStateOf("") }
    var editorActions by rememberSaveable("h1-form-actions") { mutableStateOf("") }
    var editorNotes by rememberSaveable("h1-form-notes") { mutableStateOf("") }
    var deleteId by rememberSaveable("h1-form-delete") { mutableStateOf<String?>(null) }

    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)
    val order = runCatching { CharacterPresentationOrder.valueOf(orderName) }
        .getOrDefault(CharacterPresentationOrder.MANUAL)
    val activeFilters = decodeFormFilterSetH1(activeFiltersText)
    val query = CharacterCollectionQuery(searchText = searchText, activeFilterKeys = activeFilters)
    val visible = presentCharacterForms(
        forms = forms,
        order = order,
        query = query,
        isFavorite = { form -> closureState.hasQuickAccess(CharacterQuickAccessKind.FORM, form.id) },
    )
    val canReorder = order == CharacterPresentationOrder.MANUAL &&
        query.searchText.isBlank() && query.activeFilterKeys.isEmpty()
    val selectedEditingId = editingId?.takeIf { editorOpen }

    fun updateQuery(updated: CharacterCollectionQuery) {
        searchText = updated.searchText
        activeFiltersText = encodeFormFilterSetH1(updated.activeFilterKeys)
    }

    fun updateForms(updated: List<CharacterForm>) {
        onFormsChange(normalizeCharacterFormOrders(updated))
    }

    fun resetEditor() {
        editingId = null
        editorName = ""
        editorSource = ""
        editorCr = ""
        editorAc = ""
        editorHp = ""
        editorMovement = ""
        editorSenses = ""
        editorActions = ""
        editorNotes = ""
    }

    fun beginAdd() {
        resetEditor()
        editorOpen = true
    }

    fun beginEdit(form: CharacterForm) {
        editingId = form.id.toString()
        editorName = form.name
        editorSource = form.source.orEmpty()
        editorCr = form.challengeRatingText.orEmpty()
        editorAc = form.armorClass?.toString().orEmpty()
        editorHp = form.hitPoints?.toString().orEmpty()
        editorMovement = form.movement.orEmpty()
        editorSenses = form.senses.orEmpty()
        editorActions = form.actionSummary
        editorNotes = form.notes.orEmpty()
        editorOpen = true
    }

    fun duplicate(form: CharacterForm) {
        val copied = duplicateCharacterForm(
            source = form,
            newId = Uuid.random(),
            sortOrder = nextCharacterFormSortOrder(forms),
        )
        updateForms(forms + copied)
    }

    val parsedAc = editorAc.trim().takeIf(String::isNotEmpty)?.toIntOrNull()
    val parsedHp = editorHp.trim().takeIf(String::isNotEmpty)?.toIntOrNull()
    val editorCanSave = editorName.trim().isNotEmpty() &&
        (editorAc.isBlank() || parsedAc != null) &&
        (editorHp.isBlank() || parsedHp != null) &&
        (parsedAc == null || parsedAc >= 0) &&
        (parsedHp == null || parsedHp >= 0)

    fun applyEditor() {
        if (!editorCanSave) return
        val existing = editingId?.let { id -> forms.firstOrNull { it.id.toString() == id } }
        val edited = CharacterForm(
            id = existing?.id ?: Uuid.random(),
            name = editorName.trim(),
            source = editorSource.trim().takeIf(String::isNotEmpty),
            challengeRatingText = editorCr.trim().takeIf(String::isNotEmpty),
            armorClass = parsedAc,
            hitPoints = parsedHp,
            movement = editorMovement.trim().takeIf(String::isNotEmpty),
            senses = editorSenses.trim().takeIf(String::isNotEmpty),
            actionSummary = editorActions.trim(),
            notes = editorNotes.trim().takeIf(String::isNotEmpty),
            pinned = existing?.pinned ?: false,
            sortOrder = existing?.sortOrder ?: nextCharacterFormSortOrder(forms),
        )
        val updated = if (existing == null) {
            forms + edited
        } else {
            forms.map { form -> if (form.id == existing.id) edited else form }
        }
        updateForms(updated)
        editorOpen = false
    }

    val collection: @Composable (Modifier) -> Unit = { modifier ->
        FormsCollectionH1(
            modifier = modifier,
            forms = forms,
            visible = visible,
            closureState = closureState,
            persistedFormIds = persistedFormIds,
            query = query,
            order = order,
            canReorder = canReorder,
            selectedEditingId = selectedEditingId,
            onQueryChange = ::updateQuery,
            onOrderChange = { orderName = it.name },
            onAdd = ::beginAdd,
            onEdit = ::beginEdit,
            onDuplicate = ::duplicate,
            onDelete = { deleteId = it.id.toString() },
            onMove = { form, offset ->
                if (!canReorder) {
                    false
                } else {
                    val before = normalizeCharacterFormOrders(forms)
                    val moved = moveCharacterFormManual(forms, form.id, offset)
                    if (moved == before) {
                        false
                    } else {
                        onFormsChange(moved)
                        true
                    }
                }
            },
            onFavoriteChange = { form, enabled ->
                onClosureStateChange(
                    closureState.withQuickAccess(CharacterQuickAccessKind.FORM, form.id, enabled),
                )
            },
            onHaptic = haptic,
        )
    }

    if (wide) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            collection(Modifier.weight(1f))
            Surface(
                modifier = Modifier
                    .width(400.dp)
                    .fillMaxHeight()
                    .padding(top = 5.dp, end = 8.dp, bottom = 8.dp),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                if (editorOpen) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    if (editingId == null) "Añadir forma" else "Editar forma",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                TextButton(onClick = { editorOpen = false }) { Text("Cerrar") }
                            }
                        }
                        item {
                            FormEditorFieldsH1(
                                name = editorName,
                                source = editorSource,
                                cr = editorCr,
                                armorClass = editorAc,
                                hitPoints = editorHp,
                                movement = editorMovement,
                                senses = editorSenses,
                                actions = editorActions,
                                notes = editorNotes,
                                validationMessage = formValidationMessageH1(editorName, editorAc, parsedAc, editorHp, parsedHp),
                                onNameChange = { editorName = it },
                                onSourceChange = { editorSource = it },
                                onCrChange = { editorCr = it },
                                onArmorClassChange = { editorAc = nonNegativeIntegerInputH1(it) },
                                onHitPointsChange = { editorHp = nonNegativeIntegerInputH1(it) },
                                onMovementChange = { editorMovement = it },
                                onSensesChange = { editorSenses = it },
                                onActionsChange = { editorActions = it },
                                onNotesChange = { editorNotes = it },
                            )
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                OutlinedButton(onClick = { editorOpen = false }) { Text("Cancelar") }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(onClick = ::applyEditor, enabled = editorCanSave) { Text("Aplicar") }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("Editor de forma", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Selecciona una forma de la biblioteca o añade una nueva. La ficha base no se modifica al abrirla.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        TextButton(onClick = ::beginAdd) { Text("+ Añadir forma") }
                    }
                }
            }
        }
    } else {
        collection(Modifier.fillMaxSize())
        if (editorOpen) {
            CharacterImeSafeEditorDialog(
                title = if (editingId == null) "Añadir forma" else "Editar forma",
                onCancel = { editorOpen = false },
                onSave = ::applyEditor,
                saveLabel = "Aplicar",
                saveEnabled = editorCanSave,
            ) {
                FormEditorFieldsH1(
                    name = editorName,
                    source = editorSource,
                    cr = editorCr,
                    armorClass = editorAc,
                    hitPoints = editorHp,
                    movement = editorMovement,
                    senses = editorSenses,
                    actions = editorActions,
                    notes = editorNotes,
                    validationMessage = formValidationMessageH1(editorName, editorAc, parsedAc, editorHp, parsedHp),
                    onNameChange = { editorName = it },
                    onSourceChange = { editorSource = it },
                    onCrChange = { editorCr = it },
                    onArmorClassChange = { editorAc = nonNegativeIntegerInputH1(it) },
                    onHitPointsChange = { editorHp = nonNegativeIntegerInputH1(it) },
                    onMovementChange = { editorMovement = it },
                    onSensesChange = { editorSenses = it },
                    onActionsChange = { editorActions = it },
                    onNotesChange = { editorNotes = it },
                )
            }
        }
    }

    deleteId?.let { id ->
        val target = forms.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "forma",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    updateForms(forms.filterNot { it.id == target.id })
                    if (editingId == target.id.toString()) editorOpen = false
                    deleteId = null
                    haptic(CharacterHapticEventV4.DESTRUCTIVE)
                },
            )
        }
    }
}

@Composable
private fun FormsCollectionH1(
    modifier: Modifier,
    forms: List<CharacterForm>,
    visible: List<CharacterForm>,
    closureState: CharacterClosureState,
    persistedFormIds: Set<Uuid>,
    query: CharacterCollectionQuery,
    order: CharacterPresentationOrder,
    canReorder: Boolean,
    selectedEditingId: String?,
    onQueryChange: (CharacterCollectionQuery) -> Unit,
    onOrderChange: (CharacterPresentationOrder) -> Unit,
    onAdd: () -> Unit,
    onEdit: (CharacterForm) -> Unit,
    onDuplicate: (CharacterForm) -> Unit,
    onDelete: (CharacterForm) -> Unit,
    onMove: (CharacterForm, Int) -> Boolean,
    onFavoriteChange: (CharacterForm, Boolean) -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
) {
    val sourceFilters = forms
        .groupBy { form -> characterFormSourceFilterKey(form.source) }
        .map { (key, matching) ->
            CharacterFilterOptionV4(
                key = key,
                label = matching.firstNotNullOfOrNull { it.source?.trim()?.takeIf(String::isNotEmpty) } ?: "Sin fuente",
                count = matching.size,
            )
        }
        .sortedBy { it.label.lowercase() }
    val filters = listOf(
        CharacterFilterOptionV4(
            key = CHARACTER_FORM_FAVORITE_FILTER_KEY,
            label = "Favoritos",
            count = forms.count { closureState.hasQuickAccess(CharacterQuickAccessKind.FORM, it.id) },
        ),
    ) + sourceFilters

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 6.dp, end = 6.dp, top = 5.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        item(key = "h1-forms-tools") {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Formas", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Biblioteca de transformaciones y formas alternativas. Consultarlas no cambia automáticamente la ficha base.",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                        TextButton(onClick = onAdd) { Text("+ Añadir") }
                    }
                    CharacterCollectionToolbarV4(
                        itemCount = visible.size,
                        query = query,
                        onQueryChange = onQueryChange,
                        order = order,
                        onOrderChange = onOrderChange,
                        filters = filters,
                        searchLabel = "Buscar formas",
                    )
                    if (!canReorder && visible.isNotEmpty()) {
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
        }

        if (forms.isEmpty()) {
            item {
                CharacterUsefulEmptyState(
                    title = "Sin formas registradas",
                    message = "Añade una forma o transformación que quieras consultar rápidamente. No necesitas copiar un bloque completo de criatura.",
                    onAdd = onAdd,
                    addLabel = "Añadir forma",
                )
            }
        } else if (visible.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "No hay formas que coincidan con esta búsqueda y filtros.",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        items(
            count = visible.size,
            key = { index -> "h1-form-${visible[index].id}" },
        ) { index ->
            val form = visible[index]
            FormRowH1(
                form = form,
                favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.FORM, form.id),
                favoriteEnabled = form.id in persistedFormIds,
                reorderEnabled = canReorder && forms.size > 1,
                selected = selectedEditingId == form.id.toString(),
                onFavoriteChange = { onFavoriteChange(form, it) },
                onMove = { offset -> onMove(form, offset) },
                onEdit = { onEdit(form) },
                onDuplicate = { onDuplicate(form) },
                onDelete = { onDelete(form) },
                onHaptic = onHaptic,
            )
        }
    }
}

@Composable
private fun FormRowH1(
    form: CharacterForm,
    favorite: Boolean,
    favoriteEnabled: Boolean,
    reorderEnabled: Boolean,
    selected: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    onMove: (Int) -> Boolean,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
) {
    var accumulatedDrag by remember(form.id) { mutableStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }
    val dragState = CharacterDragVisualStateV4(
        active = dragging,
        offsetY = accumulatedDrag,
        showDropBefore = dragging && accumulatedDrag < 0f,
        showDropAfter = dragging && accumulatedDrag > 0f,
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        CharacterDropIndicatorV4(visible = dragState.showDropBefore)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .characterDragFeedbackV4(dragState)
                .clickable(onClick = onEdit),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
            ),
            color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (reorderEnabled) {
                    StableDragHandle(
                        modifier = Modifier.pointerInput(form.id) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    accumulatedDrag = 0f
                                    dragging = true
                                    onHaptic(CharacterHapticEventV4.DRAG_PICKUP)
                                },
                                onDragEnd = {
                                    if (dragging) onHaptic(CharacterHapticEventV4.DRAG_DROP)
                                    accumulatedDrag = 0f
                                    dragging = false
                                },
                                onDragCancel = {
                                    accumulatedDrag = 0f
                                    dragging = false
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    accumulatedDrag += dragAmount.y
                                    while (abs(accumulatedDrag) >= reorderStepPx) {
                                        val direction = if (accumulatedDrag > 0f) 1 else -1
                                        if (onMove(direction)) {
                                            onHaptic(CharacterHapticEventV4.DRAG_STEP)
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
                        contentDescription = "Mantén pulsado y arrastra para reordenar ${form.name}",
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    Text(
                        form.name.ifBlank { "Forma sin nombre" },
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        form.challengeRatingText?.takeIf(String::isNotBlank)?.let { ModuleBadgeH1("CR $it") }
                        form.armorClass?.let { ModuleBadgeH1("CA $it") }
                        form.hitPoints?.let { ModuleBadgeH1("PG $it") }
                    }
                    val reference = listOfNotNull(
                        form.source?.takeIf(String::isNotBlank),
                        form.movement?.takeIf(String::isNotBlank),
                        form.senses?.takeIf(String::isNotBlank),
                    ).joinToString(" · ")
                    if (reference.isNotBlank()) {
                        Text(
                            reference,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (form.actionSummary.isNotBlank()) {
                        Text(
                            form.actionSummary,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { onFavoriteChange(!favorite) },
                            enabled = favoriteEnabled,
                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                        ) { Text(if (favorite) "★" else "☆") }
                        StableRemoveIconButton(
                            onClick = onDelete,
                            contentDescription = "Eliminar ${form.name}",
                        )
                    }
                    TextButton(
                        onClick = onDuplicate,
                        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                    ) { Text("Duplicar") }
                }
            }
        }
        CharacterDropIndicatorV4(visible = dragState.showDropAfter)
    }
}

@Composable
private fun FormEditorFieldsH1(
    name: String,
    source: String,
    cr: String,
    armorClass: String,
    hitPoints: String,
    movement: String,
    senses: String,
    actions: String,
    notes: String,
    validationMessage: String?,
    onNameChange: (String) -> Unit,
    onSourceChange: (String) -> Unit,
    onCrChange: (String) -> Unit,
    onArmorClassChange: (String) -> Unit,
    onHitPointsChange: (String) -> Unit,
    onMovementChange: (String) -> Unit,
    onSensesChange: (String) -> Unit,
    onActionsChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Nombre") },
        singleLine = true,
    )
    CharacterInlineValidationMessage(validationMessage)
    OutlinedTextField(
        value = source,
        onValueChange = onSourceChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Fuente / procedencia") },
        singleLine = true,
    )
    OutlinedTextField(
        value = cr,
        onValueChange = onCrChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("CR / referencia") },
        singleLine = true,
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        OutlinedTextField(
            value = armorClass,
            onValueChange = onArmorClassChange,
            modifier = Modifier.weight(1f),
            label = { Text("CA") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        OutlinedTextField(
            value = hitPoints,
            onValueChange = onHitPointsChange,
            modifier = Modifier.weight(1f),
            label = { Text("PG") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
    }
    OutlinedTextField(
        value = movement,
        onValueChange = onMovementChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Movimiento") },
        singleLine = true,
    )
    OutlinedTextField(
        value = senses,
        onValueChange = onSensesChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Sentidos / percepción") },
        minLines = 2,
        maxLines = 4,
    )
    OutlinedTextField(
        value = actions,
        onValueChange = onActionsChange,
        modifier = Modifier.fillMaxWidth().heightIn(min = 130.dp, max = 280.dp),
        label = { Text("Acciones / referencia rápida") },
        minLines = 4,
        maxLines = 10,
    )
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp, max = 220.dp),
        label = { Text("Notas") },
        minLines = 2,
        maxLines = 8,
    )
}

private fun formValidationMessageH1(
    name: String,
    armorClass: String,
    parsedArmorClass: Int?,
    hitPoints: String,
    parsedHitPoints: Int?,
): String? = when {
    name.isNotEmpty() && name.trim().isEmpty() -> "Escribe un nombre para guardar la forma."
    armorClass.isNotBlank() && parsedArmorClass == null -> "La CA debe quedar vacía o contener un número no negativo."
    parsedArmorClass != null && parsedArmorClass < 0 -> "La CA no puede ser negativa."
    hitPoints.isNotBlank() && parsedHitPoints == null -> "Los PG deben quedar vacíos o contener un número no negativo."
    parsedHitPoints != null && parsedHitPoints < 0 -> "Los PG no pueden ser negativos."
    else -> null
}

private fun nonNegativeIntegerInputH1(raw: String): String = raw.filter(Char::isDigit)

private fun encodeFormFilterSetH1(filters: Set<String>): String =
    filters.sorted().joinToString(FORM_FILTER_SEPARATOR_H1)

private fun decodeFormFilterSetH1(encoded: String): Set<String> =
    encoded.split(FORM_FILTER_SEPARATOR_H1).filter(String::isNotBlank).toSet()
