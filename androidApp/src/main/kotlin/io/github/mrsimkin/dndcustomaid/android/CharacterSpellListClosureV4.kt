package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_SPELL_CONCENTRATION_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_SPELL_FAVORITE_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_SPELL_MATERIAL_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_SPELL_PREPARED_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_SPELL_RITUAL_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_SPELL_SOMATIC_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_SPELL_VERBAL_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCollectionQuery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSourceAssociation
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource
import io.github.mrsimkin.dndcustomaid.shared.character.duplicateCharacterSpell
import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess
import io.github.mrsimkin.dndcustomaid.shared.character.moveCharacterSpellManual
import io.github.mrsimkin.dndcustomaid.shared.character.nextCharacterSpellSortOrder
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterSpellOrders
import io.github.mrsimkin.dndcustomaid.shared.character.presentCharacterSpellLevel
import io.github.mrsimkin.dndcustomaid.shared.character.spellPreparedForView
import io.github.mrsimkin.dndcustomaid.shared.character.spellVisibleForSource
import io.github.mrsimkin.dndcustomaid.shared.character.withQuickAccess
import kotlin.math.abs
import kotlin.uuid.Uuid

private const val SPELL_FILTER_SEPARATOR_G2 = "\u001E"

@Composable
internal fun CharacterSpellListClosureV4(
    draft: CharacterSpellcastingDraftV4,
    slotStates: List<CharacterSpellSlotUiV4>,
    selectedSourceId: Uuid?,
    closureState: CharacterClosureState,
    persistedSpellIds: Set<Uuid>,
    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,
    structuralEditingEnabled: Boolean,
    onSlotSpentChange: (Int, Int) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
    hapticsEnabled: Boolean,
) {
    var searchText by rememberSaveable("spell-g2-search") { mutableStateOf("") }
    var activeFiltersText by rememberSaveable("spell-g2-filters") { mutableStateOf("") }
    var orderName by rememberSaveable("spell-g2-order") { mutableStateOf(CharacterPresentationOrder.MANUAL.name) }
    var collapsedLevelsText by rememberSaveable("spell-g2-collapsed") { mutableStateOf("") }

    var editorOpen by rememberSaveable("spell-g2-editor-open") { mutableStateOf(false) }
    var editingSpellId by rememberSaveable("spell-g2-editor-id") { mutableStateOf<String?>(null) }
    var editorName by rememberSaveable("spell-g2-name") { mutableStateOf("") }
    var editorLevel by rememberSaveable("spell-g2-level") { mutableStateOf("0") }
    var editorCastingTime by rememberSaveable("spell-g2-casting") { mutableStateOf("") }
    var editorRangeText by rememberSaveable("spell-g2-range") { mutableStateOf("") }
    var editorVerbal by rememberSaveable("spell-g2-v") { mutableStateOf(false) }
    var editorSomatic by rememberSaveable("spell-g2-s") { mutableStateOf(false) }
    var editorMaterial by rememberSaveable("spell-g2-m") { mutableStateOf(false) }
    var editorMaterialText by rememberSaveable("spell-g2-material") { mutableStateOf("") }
    var editorDuration by rememberSaveable("spell-g2-duration") { mutableStateOf("") }
    var editorConcentration by rememberSaveable("spell-g2-concentration") { mutableStateOf(false) }
    var editorRitual by rememberSaveable("spell-g2-ritual") { mutableStateOf(false) }
    var editorDescription by rememberSaveable("spell-g2-description") { mutableStateOf("") }
    var editorNotes by rememberSaveable("spell-g2-notes") { mutableStateOf("") }
    var editorAssociatedSourceIds by rememberSaveable("spell-g2-sources") { mutableStateOf("") }
    var editorPreparedSourceIds by rememberSaveable("spell-g2-prepared") { mutableStateOf("") }
    var deleteSpellId by rememberSaveable("spell-g2-delete") { mutableStateOf<String?>(null) }

    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)
    val order = runCatching { CharacterPresentationOrder.valueOf(orderName) }
        .getOrDefault(CharacterPresentationOrder.MANUAL)
    val activeFilters = editorIdSetG2(activeFiltersText, SPELL_FILTER_SEPARATOR_G2)
    val query = CharacterCollectionQuery(searchText = searchText, activeFilterKeys = activeFilters)
    val sourceById = remember(draft.sources) { draft.sources.associateBy { it.id } }
    val slotByLevel = remember(slotStates) { slotStates.associateBy { it.level } }
    val collapsedLevels = parseSpellLevelSetG2(collapsedLevelsText)
    val canReorder = structuralEditingEnabled && order == CharacterPresentationOrder.MANUAL &&
        query.searchText.isBlank() && query.activeFilterKeys.isEmpty()

    val visibleByLevel = (0..9).associateWith { level ->
        presentCharacterSpellLevel(
            spells = draft.spells,
            level = level,
            selectedSourceId = selectedSourceId,
            order = order,
            query = query,
            sourceName = { id -> sourceById[id]?.name },
            isFavorite = { spell -> closureState.hasQuickAccess(CharacterQuickAccessKind.SPELL, spell.id) },
        )
    }
    val visibleCount = visibleByLevel.values.sumOf { it.size }

    fun updateQuery(updated: CharacterCollectionQuery) {
        searchText = updated.searchText
        activeFiltersText = updated.activeFilterKeys.sorted().joinToString(SPELL_FILTER_SEPARATOR_G2)
    }

    fun updateSpells(updated: List<CharacterSpell>) {
        onDraftChange(draft.copy(spells = normalizeCharacterSpellOrders(updated)))
    }

    fun resetEditor() {
        editingSpellId = null
        editorName = ""
        editorLevel = "0"
        editorCastingTime = ""
        editorRangeText = ""
        editorVerbal = false
        editorSomatic = false
        editorMaterial = false
        editorMaterialText = ""
        editorDuration = ""
        editorConcentration = false
        editorRitual = false
        editorDescription = ""
        editorNotes = ""
        val initialSource = selectedSourceId?.toString().orEmpty()
        editorAssociatedSourceIds = initialSource
        editorPreparedSourceIds = ""
    }

    fun beginAdd() {
        resetEditor()
        editorOpen = true
    }

    fun beginEdit(spell: CharacterSpell) {
        editingSpellId = spell.id.toString()
        editorName = spell.name
        editorLevel = spell.level.toString()
        editorCastingTime = spell.castingTime
        editorRangeText = spell.rangeText
        editorVerbal = spell.verbal
        editorSomatic = spell.somatic
        editorMaterial = spell.material
        editorMaterialText = spell.materialText.orEmpty()
        editorDuration = spell.duration
        editorConcentration = spell.concentration
        editorRitual = spell.ritual
        editorDescription = spell.description
        editorNotes = spell.notes.orEmpty()
        editorAssociatedSourceIds = spell.sourceAssociations
            .map { it.sourceId.toString() }
            .sorted()
            .joinToString(",")
        editorPreparedSourceIds = spell.sourceAssociations
            .filter { it.prepared }
            .map { it.sourceId.toString() }
            .sorted()
            .joinToString(",")
        editorOpen = true
    }

    fun duplicate(spell: CharacterSpell) {
        val duplicated = duplicateCharacterSpell(
            source = spell,
            newId = Uuid.random(),
            sortOrder = nextCharacterSpellSortOrder(draft.spells, spell.level),
        )
        updateSpells(draft.spells + duplicated)
    }

    fun togglePrepared(spell: CharacterSpell, prepared: Boolean) {
        val sourceId = selectedSourceId ?: return
        updateSpells(
            draft.spells.map { candidate ->
                if (candidate.id != spell.id) {
                    candidate
                } else {
                    candidate.copy(
                        sourceAssociations = candidate.sourceAssociations.map { association ->
                            if (association.sourceId == sourceId) association.copy(prepared = prepared) else association
                        },
                    )
                }
            },
        )
    }

    val validEditorSourceKeys = draft.sources.mapTo(mutableSetOf()) { it.id.toString() }
    val editorAssociated = parseSpellIdSetG2(editorAssociatedSourceIds).intersect(validEditorSourceKeys)
    val editorPrepared = parseSpellIdSetG2(editorPreparedSourceIds).intersect(validEditorSourceKeys)
    val parsedEditorLevel = editorLevel.trim().toIntOrNull()
    val editorCanSave = editorName.trim().isNotEmpty() &&
        parsedEditorLevel != null && parsedEditorLevel in 0..9 && editorAssociated.isNotEmpty()

    fun applyEditor() {
        if (!editorCanSave) return
        val existing = editingSpellId?.let { id -> draft.spells.firstOrNull { it.id.toString() == id } }
        val associations = draft.sources.mapNotNull { source ->
            val key = source.id.toString()
            if (key in editorAssociated) {
                CharacterSpellSourceAssociation(source.id, prepared = key in editorPrepared)
            } else {
                null
            }
        }
        val level = requireNotNull(parsedEditorLevel)
        val sortOrder = when {
            existing == null -> nextCharacterSpellSortOrder(draft.spells, level)
            existing.level == level -> existing.sortOrder
            else -> nextCharacterSpellSortOrder(draft.spells, level, excludingId = existing.id)
        }
        val edited = CharacterSpell(
            id = existing?.id ?: Uuid.random(),
            name = editorName.trim(),
            level = level,
            castingTime = editorCastingTime.trim(),
            rangeText = editorRangeText.trim(),
            verbal = editorVerbal,
            somatic = editorSomatic,
            material = editorMaterial,
            materialText = editorMaterialText.trim().takeIf { editorMaterial && it.isNotEmpty() },
            duration = editorDuration.trim(),
            concentration = editorConcentration,
            ritual = editorRitual,
            description = editorDescription.trim(),
            notes = editorNotes.trim().takeIf { it.isNotEmpty() },
            sortOrder = sortOrder,
            sourceAssociations = associations,
            pinned = existing?.pinned ?: false,
        )
        val updated = if (existing == null) {
            draft.spells + edited
        } else {
            draft.spells.map { if (it.id == existing.id) edited else it }
        }
        updateSpells(updated)
        editorOpen = false
    }

    val collection: @Composable (Modifier) -> Unit = { modifier ->
        SpellCollectionG2(
            modifier = modifier,
            draft = draft,
            selectedSourceId = selectedSourceId,
            closureState = closureState,
            persistedSpellIds = persistedSpellIds,
            query = query,
            order = order,
            collapsedLevels = collapsedLevels,
            visibleByLevel = visibleByLevel,
            visibleCount = visibleCount,
            slotByLevel = slotByLevel,
            sourceById = sourceById,
            canReorder = canReorder,
            structuralEditingEnabled = structuralEditingEnabled,
            selectedEditingId = editingSpellId?.takeIf { editorOpen },
            onQueryChange = ::updateQuery,
            onOrderChange = { orderName = it.name },
            onCollapsedLevelsChange = { collapsedLevelsText = encodeSpellLevelSetG2(it) },
            onAdd = ::beginAdd,
            onEdit = ::beginEdit,
            onDuplicate = ::duplicate,
            onDelete = { deleteSpellId = it.id.toString() },
            onPreparedChange = ::togglePrepared,
            onMove = { spell, offset ->
                if (!canReorder) {
                    false
                } else {
                    val before = normalizeCharacterSpellOrders(draft.spells)
                    val moved = moveCharacterSpellManual(
                        spells = draft.spells,
                        spellId = spell.id,
                        offset = offset,
                        selectedSourceId = selectedSourceId,
                    )
                    if (moved == before) {
                        false
                    } else {
                        onDraftChange(draft.copy(spells = moved))
                        true
                    }
                }
            },
            onFavoriteChange = { spell, enabled ->
                onClosureStateChange(
                    closureState.withQuickAccess(
                        CharacterQuickAccessKind.SPELL,
                        spell.id,
                        enabled,
                    ),
                )
            },
            onSlotSpentChange = onSlotSpentChange,
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
                    .width(390.dp)
                    .fillMaxHeight()
                    .padding(top = 5.dp, end = 8.dp, bottom = 8.dp),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                if (editorOpen && structuralEditingEnabled) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                if (editingSpellId == null) "Añadir conjuro" else "Editar conjuro",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            TextButton(onClick = { editorOpen = false }) { Text("Cerrar") }
                        }
                        SpellEditorFieldsG2(
                            sources = draft.sources,
                            name = editorName,
                            level = editorLevel,
                            castingTime = editorCastingTime,
                            rangeText = editorRangeText,
                            verbal = editorVerbal,
                            somatic = editorSomatic,
                            material = editorMaterial,
                            materialText = editorMaterialText,
                            duration = editorDuration,
                            concentration = editorConcentration,
                            ritual = editorRitual,
                            description = editorDescription,
                            notes = editorNotes,
                            associatedSourceIds = editorAssociated,
                            preparedSourceIds = editorPrepared,
                            validationMessage = spellEditorValidationG2(editorName, parsedEditorLevel, editorAssociated),
                            onNameChange = { editorName = it },
                            onLevelChange = { editorLevel = spellLevelInputG2(it) },
                            onCastingTimeChange = { editorCastingTime = it },
                            onRangeTextChange = { editorRangeText = it },
                            onVerbalChange = { editorVerbal = it },
                            onSomaticChange = { editorSomatic = it },
                            onMaterialChange = { editorMaterial = it },
                            onMaterialTextChange = { editorMaterialText = it },
                            onDurationChange = { editorDuration = it },
                            onConcentrationChange = { editorConcentration = it },
                            onRitualChange = { editorRitual = it },
                            onDescriptionChange = { editorDescription = it },
                            onNotesChange = { editorNotes = it },
                            onAssociationChange = { sourceId, included ->
                                val key = sourceId.toString()
                                val nextAssociated = editorAssociated.toMutableSet()
                                val nextPrepared = editorPrepared.toMutableSet()
                                if (included) nextAssociated += key else {
                                    nextAssociated -= key
                                    nextPrepared -= key
                                }
                                editorAssociatedSourceIds = nextAssociated.sorted().joinToString(",")
                                editorPreparedSourceIds = nextPrepared.sorted().joinToString(",")
                            },
                            onPreparedChange = { sourceId, prepared ->
                                val key = sourceId.toString()
                                val next = editorPrepared.toMutableSet()
                                if (prepared) next += key else next -= key
                                editorPreparedSourceIds = next.sorted().joinToString(",")
                            },
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedButton(onClick = { editorOpen = false }) { Text("Cancelar") }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(onClick = ::applyEditor, enabled = editorCanSave) { Text("Aplicar") }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Editor de conjuro", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Selecciona un conjuro de la lista o añade uno nuevo. La lista conserva su búsqueda, filtros y posición mientras editas.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text("+ Añadir conjuro") }
                    }
                }
            }
        }
    } else {
        collection(Modifier.fillMaxSize())
        if (editorOpen && structuralEditingEnabled) {
            CharacterImeSafeEditorDialog(
                title = if (editingSpellId == null) "Añadir conjuro" else "Editar conjuro",
                onCancel = { editorOpen = false },
                onSave = ::applyEditor,
                saveEnabled = editorCanSave,
            ) {
                SpellEditorFieldsG2(
                    sources = draft.sources,
                    name = editorName,
                    level = editorLevel,
                    castingTime = editorCastingTime,
                    rangeText = editorRangeText,
                    verbal = editorVerbal,
                    somatic = editorSomatic,
                    material = editorMaterial,
                    materialText = editorMaterialText,
                    duration = editorDuration,
                    concentration = editorConcentration,
                    ritual = editorRitual,
                    description = editorDescription,
                    notes = editorNotes,
                    associatedSourceIds = editorAssociated,
                    preparedSourceIds = editorPrepared,
                    validationMessage = spellEditorValidationG2(editorName, parsedEditorLevel, editorAssociated),
                    onNameChange = { editorName = it },
                    onLevelChange = { editorLevel = spellLevelInputG2(it) },
                    onCastingTimeChange = { editorCastingTime = it },
                    onRangeTextChange = { editorRangeText = it },
                    onVerbalChange = { editorVerbal = it },
                    onSomaticChange = { editorSomatic = it },
                    onMaterialChange = { editorMaterial = it },
                    onMaterialTextChange = { editorMaterialText = it },
                    onDurationChange = { editorDuration = it },
                    onConcentrationChange = { editorConcentration = it },
                    onRitualChange = { editorRitual = it },
                    onDescriptionChange = { editorDescription = it },
                    onNotesChange = { editorNotes = it },
                    onAssociationChange = { sourceId, included ->
                        val key = sourceId.toString()
                        val nextAssociated = editorAssociated.toMutableSet()
                        val nextPrepared = editorPrepared.toMutableSet()
                        if (included) nextAssociated += key else {
                            nextAssociated -= key
                            nextPrepared -= key
                        }
                        editorAssociatedSourceIds = nextAssociated.sorted().joinToString(",")
                        editorPreparedSourceIds = nextPrepared.sorted().joinToString(",")
                    },
                    onPreparedChange = { sourceId, prepared ->
                        val key = sourceId.toString()
                        val next = editorPrepared.toMutableSet()
                        if (prepared) next += key else next -= key
                        editorPreparedSourceIds = next.sorted().joinToString(",")
                    },
                )
            }
        }
    }

    deleteSpellId?.takeIf { structuralEditingEnabled }?.let { id ->
        val target = draft.spells.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteSpellId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "conjuro",
                onDismissRequest = { deleteSpellId = null },
                onConfirm = {
                    updateSpells(draft.spells.filterNot { it.id == target.id })
                    if (editingSpellId == target.id.toString()) editorOpen = false
                    deleteSpellId = null
                    haptic(CharacterHapticEventV4.DESTRUCTIVE)
                },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpellCollectionG2(
    modifier: Modifier,
    draft: CharacterSpellcastingDraftV4,
    selectedSourceId: Uuid?,
    closureState: CharacterClosureState,
    persistedSpellIds: Set<Uuid>,
    query: CharacterCollectionQuery,
    order: CharacterPresentationOrder,
    collapsedLevels: Set<Int>,
    visibleByLevel: Map<Int, List<CharacterSpell>>,
    visibleCount: Int,
    slotByLevel: Map<Int, CharacterSpellSlotUiV4>,
    sourceById: Map<Uuid, CharacterSpellcastingSource>,
    canReorder: Boolean,
    structuralEditingEnabled: Boolean,
    selectedEditingId: String?,
    onQueryChange: (CharacterCollectionQuery) -> Unit,
    onOrderChange: (CharacterPresentationOrder) -> Unit,
    onCollapsedLevelsChange: (Set<Int>) -> Unit,
    onAdd: () -> Unit,
    onEdit: (CharacterSpell) -> Unit,
    onDuplicate: (CharacterSpell) -> Unit,
    onDelete: (CharacterSpell) -> Unit,
    onPreparedChange: (CharacterSpell, Boolean) -> Unit,
    onMove: (CharacterSpell, Int) -> Boolean,
    onFavoriteChange: (CharacterSpell, Boolean) -> Unit,
    onSlotSpentChange: (Int, Int) -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 6.dp, end = 6.dp, top = 5.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        item(key = "spell-g2-tools") {
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
                            Text("Conjuros", style = MaterialTheme.typography.titleSmall)
                            Text(
                                if (selectedSourceId == null) "Todos los conjuros conceptuales" else "Vista filtrada por fuente",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                        TextButton(onClick = onAdd, enabled = structuralEditingEnabled) { Text("+ Añadir") }
                    }
                    CharacterCollectionToolbarV4(
                        itemCount = visibleCount,
                        query = query,
                        onQueryChange = onQueryChange,
                        order = order,
                        onOrderChange = onOrderChange,
                        filters = spellFiltersG2(selectedSourceId),
                        searchLabel = "Buscar conjuros",
                    )
                    if (!canReorder && visibleCount > 0) {
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

        if (draft.spells.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Sin conjuros registrados. Crea al menos una fuente y añade el primer conjuro.",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        } else if (visibleCount == 0) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "No hay conjuros que coincidan con esta fuente, búsqueda y filtros.",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        for (level in 0..9) {
            val levelSpells = visibleByLevel[level].orEmpty()
            val sourceLevelCount = draft.spells.count { spell ->
                spell.level == level && spellVisibleForSource(spell, selectedSourceId)
            }
            val slot = slotByLevel[level]
            if (sourceLevelCount == 0 && (slot == null || slot.total <= 0)) continue
            val collapsed = level in collapsedLevels

            stickyHeader(key = "spell-g2-level-header-$level") {
                SpellLevelStickyHeaderG2(
                    level = level,
                    shownCount = levelSpells.size,
                    sourceCount = sourceLevelCount,
                    collapsed = collapsed,
                    slot = slot,
                    queryActive = query.searchText.isNotBlank() || query.activeFilterKeys.isNotEmpty(),
                    onToggleCollapsed = {
                        onCollapsedLevelsChange(
                            if (collapsed) collapsedLevels - level else collapsedLevels + level,
                        )
                    },
                    onSlotSpentChange = { spent -> onSlotSpentChange(level, spent) },
                )
            }

            if (!collapsed) {
                items(
                    count = levelSpells.size,
                    key = { index -> "spell-g2-${levelSpells[index].id}" },
                ) { index ->
                    val spell = levelSpells[index]
                    SpellRowG2(
                        spell = spell,
                        sourceById = sourceById,
                        selectedSourceId = selectedSourceId,
                        favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.SPELL, spell.id),
                        favoriteEnabled = spell.id in persistedSpellIds,
                        reorderEnabled = canReorder && sourceLevelCount > 1,
                        structuralEditingEnabled = structuralEditingEnabled,
                        selected = selectedEditingId == spell.id.toString(),
                        onPreparedChange = { onPreparedChange(spell, it) },
                        onFavoriteChange = { onFavoriteChange(spell, it) },
                        onMove = { offset -> onMove(spell, offset) },
                        onEdit = { onEdit(spell) },
                        onDuplicate = { onDuplicate(spell) },
                        onDelete = { onDelete(spell) },
                        onHaptic = onHaptic,
                    )
                }
            }
        }
    }
}

@Composable
private fun SpellLevelStickyHeaderG2(
    level: Int,
    shownCount: Int,
    sourceCount: Int,
    collapsed: Boolean,
    slot: CharacterSpellSlotUiV4?,
    queryActive: Boolean,
    onToggleCollapsed: () -> Unit,
    onSlotSpentChange: (Int) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            TextButton(
                onClick = onToggleCollapsed,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(spellLevelLabelG2(level), style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                    Text(
                        if (queryActive) "$shownCount/$sourceCount" else sourceCount.toString(),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(if (collapsed) "  Mostrar" else "  Ocultar", style = MaterialTheme.typography.labelSmall)
                }
            }
            if (level > 0 && slot != null && slot.total > 0) {
                CompactSpellSlotHeaderV4(slot = slot, onSpentChange = onSlotSpentChange)
            }
        }
    }
}

@Composable
private fun SpellRowG2(
    spell: CharacterSpell,
    sourceById: Map<Uuid, CharacterSpellcastingSource>,
    selectedSourceId: Uuid?,
    favorite: Boolean,
    favoriteEnabled: Boolean,
    reorderEnabled: Boolean,
    structuralEditingEnabled: Boolean,
    selected: Boolean,
    onPreparedChange: (Boolean) -> Unit,
    onFavoriteChange: (Boolean) -> Unit,
    onMove: (Int) -> Boolean,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
) {
    var accumulatedDrag by remember(spell.id) { mutableStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }
    val dragState = CharacterDragVisualStateV4(
        active = dragging,
        offsetY = accumulatedDrag,
        showDropBefore = dragging && accumulatedDrag < 0f,
        showDropAfter = dragging && accumulatedDrag > 0f,
    )
    val selectedAssociation = selectedSourceId?.let { sourceId ->
        spell.sourceAssociations.firstOrNull { it.sourceId == sourceId }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        CharacterDropIndicatorV4(visible = dragState.showDropBefore)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .characterDragFeedbackV4(dragState)
                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),
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
            ) {
                if (reorderEnabled) {
                    StableDragHandle(
                        modifier = Modifier.pointerInput(spell.id, selectedSourceId) {
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
                        contentDescription = "Mantén pulsado y arrastra para reordenar ${spell.name}",
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(spell.name.ifBlank { "Conjuro sin nombre" }, style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (spell.verbal) SpellBadgeG2("V")
                        if (spell.somatic) SpellBadgeG2("S")
                        if (spell.material) SpellBadgeG2("M")
                        if (spell.concentration) SpellBadgeG2("Concentración", state = true)
                        if (spell.ritual) SpellBadgeG2("Ritual", state = true)
                        if (selectedSourceId != null && selectedAssociation?.prepared == true) {
                            SpellBadgeG2("Preparado", state = true)
                        }
                        if (selectedSourceId == null && spell.sourceAssociations.isNotEmpty()) {
                            val preparedCount = spell.sourceAssociations.count { it.prepared }
                            SpellBadgeG2("Preparado $preparedCount/${spell.sourceAssociations.size}", state = true)
                        }
                    }
                    val summary = listOf(spell.castingTime, spell.rangeText, spell.duration)
                        .filter { it.isNotBlank() }
                        .joinToString(" · ")
                    if (summary.isNotBlank()) {
                        Text(summary, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    if (selectedSourceId == null) {
                        val sourceState = spell.sourceAssociations.mapNotNull { association ->
                            sourceById[association.sourceId]?.name?.let { name ->
                                "$name ${if (association.prepared) "✓" else "○"}"
                            }
                        }.joinToString(" · ")
                        if (sourceState.isNotBlank()) {
                            Text(sourceState, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    if (selectedAssociation != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedAssociation.prepared,
                                enabled = structuralEditingEnabled,
                                onCheckedChange = onPreparedChange,
                            )
                            Text("Prep.", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { onFavoriteChange(!favorite) },
                            enabled = structuralEditingEnabled && favoriteEnabled,
                            modifier = Modifier.semantics {
                                contentDescription = if (favorite) {
                                    "Quitar ${spell.name} de Favoritos"
                                } else {
                                    "Añadir ${spell.name} a Favoritos"
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                        ) {
                            Text(if (favorite) "★" else "☆")
                        }
                        if (structuralEditingEnabled) {
                            StableRemoveIconButton(
                                onClick = onDelete,
                                contentDescription = "Eliminar ${spell.name}",
                            )
                        }
                    }
                    if (structuralEditingEnabled) {
                        TextButton(
                            onClick = onDuplicate,
                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                        ) {
                            Text("Duplicar")
                        }
                    }
                }
            }
        }
        CharacterDropIndicatorV4(visible = dragState.showDropAfter)
    }
}

@Composable
private fun SpellBadgeG2(label: String, state: Boolean = false) {
    CharacterSemanticBadgeV4(
        label = label,
        kind = if (state) CharacterSemanticBadgeKindV4.STATE else CharacterSemanticBadgeKindV4.NEUTRAL,
    )
}

@Composable
private fun SpellEditorFieldsG2(
    sources: List<CharacterSpellcastingSource>,
    name: String,
    level: String,
    castingTime: String,
    rangeText: String,
    verbal: Boolean,
    somatic: Boolean,
    material: Boolean,
    materialText: String,
    duration: String,
    concentration: Boolean,
    ritual: Boolean,
    description: String,
    notes: String,
    associatedSourceIds: Set<String>,
    preparedSourceIds: Set<String>,
    validationMessage: String?,
    onNameChange: (String) -> Unit,
    onLevelChange: (String) -> Unit,
    onCastingTimeChange: (String) -> Unit,
    onRangeTextChange: (String) -> Unit,
    onVerbalChange: (Boolean) -> Unit,
    onSomaticChange: (Boolean) -> Unit,
    onMaterialChange: (Boolean) -> Unit,
    onMaterialTextChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onConcentrationChange: (Boolean) -> Unit,
    onRitualChange: (Boolean) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onAssociationChange: (Uuid, Boolean) -> Unit,
    onPreparedChange: (Uuid, Boolean) -> Unit,
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Nombre") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
    OutlinedTextField(
        value = level,
        onValueChange = onLevelChange,
        label = { Text("Nivel (0-9)") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
    Text("Fuentes", style = MaterialTheme.typography.titleSmall)
    if (sources.isEmpty()) {
        Text("Crea al menos una fuente antes de guardar un conjuro.", style = MaterialTheme.typography.bodySmall)
    }
    sources.forEach { source ->
        val key = source.id.toString()
        val included = key in associatedSourceIds
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = included,
                onCheckedChange = { onAssociationChange(source.id, it) },
            )
            Text(source.name, modifier = Modifier.weight(1f))
            Checkbox(
                checked = key in preparedSourceIds,
                enabled = included,
                onCheckedChange = { onPreparedChange(source.id, it) },
            )
            Text("Preparado", style = MaterialTheme.typography.labelSmall)
        }
    }
    OutlinedTextField(
        value = castingTime,
        onValueChange = onCastingTimeChange,
        label = { Text("Tiempo de lanzamiento") },
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = rangeText,
        onValueChange = onRangeTextChange,
        label = { Text("Alcance") },
        modifier = Modifier.fillMaxWidth(),
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(verbal, onVerbalChange); Text("V")
        Checkbox(somatic, onSomaticChange); Text("S")
        Checkbox(material, onMaterialChange); Text("M")
    }
    if (material) {
        OutlinedTextField(
            value = materialText,
            onValueChange = onMaterialTextChange,
            label = { Text("Componente material (opcional)") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
    OutlinedTextField(
        value = duration,
        onValueChange = onDurationChange,
        label = { Text("Duración") },
        modifier = Modifier.fillMaxWidth(),
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(concentration, onConcentrationChange); Text("Concentración")
        Checkbox(ritual, onRitualChange); Text("Ritual")
    }
    OutlinedTextField(
        value = description,
        onValueChange = onDescriptionChange,
        label = { Text("Descripción") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 4,
        maxLines = 10,
    )
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        label = { Text("Notas (opcional)") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        maxLines = 6,
    )
    CharacterInlineValidationMessage(validationMessage)
}

private fun spellFiltersG2(selectedSourceId: Uuid?): List<CharacterFilterOptionV4> = listOf(
    CharacterFilterOptionV4(CHARACTER_SPELL_FAVORITE_FILTER_KEY, "★ Favoritos"),
    CharacterFilterOptionV4(
        CHARACTER_SPELL_PREPARED_FILTER_KEY,
        if (selectedSourceId == null) "Preparado (alguna fuente)" else "Preparado",
    ),
    CharacterFilterOptionV4(CHARACTER_SPELL_CONCENTRATION_FILTER_KEY, "Concentración"),
    CharacterFilterOptionV4(CHARACTER_SPELL_RITUAL_FILTER_KEY, "Ritual"),
    CharacterFilterOptionV4(CHARACTER_SPELL_VERBAL_FILTER_KEY, "V"),
    CharacterFilterOptionV4(CHARACTER_SPELL_SOMATIC_FILTER_KEY, "S"),
    CharacterFilterOptionV4(CHARACTER_SPELL_MATERIAL_FILTER_KEY, "M"),
)

private fun spellEditorValidationG2(
    name: String,
    level: Int?,
    associatedSourceIds: Set<String>,
): String? = when {
    name.trim().isEmpty() -> "El nombre no puede quedar vacío."
    level == null || level !in 0..9 -> "El nivel debe estar entre 0 y 9."
    associatedSourceIds.isEmpty() -> "Selecciona al menos una fuente."
    else -> null
}

private fun spellLevelInputG2(raw: String): String = raw.filter(Char::isDigit).take(1)

private fun spellLevelLabelG2(level: Int): String = if (level == 0) "Trucos" else "Nivel $level"

private fun parseSpellLevelSetG2(raw: String): Set<Int> = raw
    .split(',')
    .mapNotNull { it.toIntOrNull() }
    .filter { it in 0..9 }
    .toSet()

private fun encodeSpellLevelSetG2(levels: Set<Int>): String = levels.sorted().joinToString(",")

private fun parseSpellIdSetG2(raw: String): Set<String> = raw.split(',').filter { it.isNotBlank() }.toSet()

private fun editorIdSetG2(raw: String, separator: String): Set<String> = raw
    .split(separator)
    .filter { it.isNotBlank() }
    .toSet()
