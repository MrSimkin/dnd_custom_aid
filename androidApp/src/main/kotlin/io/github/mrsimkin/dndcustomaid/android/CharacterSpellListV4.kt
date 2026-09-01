package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
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
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpell
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSourceAssociation
import kotlin.math.abs
import kotlin.uuid.Uuid

@Composable
internal fun CharacterSpellListV4(
    draft: CharacterSpellcastingDraftV4,
    selectedSourceId: Uuid?,
    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,
    wide: Boolean,
) {
    var search by rememberSaveable("spell-search") { mutableStateOf("") }
    var collapsedLevels by rememberSaveable("spell-collapsed-levels") { mutableStateOf("") }
    var editorOpen by rememberSaveable("spell-editor-open") { mutableStateOf(false) }
    var editingSpellId by rememberSaveable("spell-editor-id") { mutableStateOf<String?>(null) }
    var deleteSpellId by rememberSaveable("spell-delete-id") { mutableStateOf<String?>(null) }

    val sourceById = remember(draft.sources) { draft.sources.associateBy { it.id } }
    val normalizedSearch = search.trim().lowercase()
    val sourceFiltered = draft.spells.filter { spell ->
        selectedSourceId == null || spell.sourceAssociations.any { it.sourceId == selectedSourceId }
    }
    val visibleSpells = sourceFiltered.filter { spell ->
        normalizedSearch.isBlank() || spellMatchesSearchV4(spell, normalizedSearch)
    }
    val collapsed = parseLevelSetV4(collapsedLevels)

    fun updateSpells(updated: List<CharacterSpell>) {
        onDraftChange(draft.copy(spells = updated))
    }

    fun beginAdd() {
        editingSpellId = null
        editorOpen = true
    }

    fun beginEdit(spell: CharacterSpell) {
        editingSpellId = spell.id.toString()
        editorOpen = true
    }

    fun togglePrepared(spell: CharacterSpell, sourceId: Uuid, prepared: Boolean) {
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

    fun moveSpell(spell: CharacterSpell, offset: Int): Boolean {
        if (search.isNotBlank()) return false
        val levelOrdered = draft.spells
            .filter { it.level == spell.level }
            .sortedWith(compareBy<CharacterSpell> { it.sortOrder }.thenBy { it.name.lowercase() })
            .toMutableList()
        val visibleIds = levelOrdered
            .filter { candidate ->
                selectedSourceId == null || candidate.sourceAssociations.any { it.sourceId == selectedSourceId }
            }
            .map { it.id }
        val visibleIndex = visibleIds.indexOf(spell.id)
        val targetVisibleIndex = visibleIndex + offset
        if (visibleIndex < 0 || targetVisibleIndex !in visibleIds.indices) return false

        val currentIndex = levelOrdered.indexOfFirst { it.id == spell.id }
        val targetId = visibleIds[targetVisibleIndex]
        val targetIndex = levelOrdered.indexOfFirst { it.id == targetId }
        if (currentIndex < 0 || targetIndex < 0) return false

        val moved = levelOrdered.removeAt(currentIndex)
        val insertionIndex = if (currentIndex < targetIndex) targetIndex else targetIndex
        levelOrdered.add(insertionIndex.coerceIn(0, levelOrdered.size), moved)
        val sortOrders = levelOrdered.mapIndexed { index, item -> item.id to index }.toMap()
        updateSpells(
            draft.spells.map { candidate ->
                if (candidate.level == spell.level) {
                    candidate.copy(sortOrder = sortOrders[candidate.id] ?: candidate.sortOrder)
                } else {
                    candidate
                }
            },
        )
        return true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = if (wide) 10.dp else 5.dp,
            end = if (wide) 10.dp else 5.dp,
            top = 7.dp,
            bottom = 150.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        item(key = "spell-tools") {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        OutlinedTextField(
                            value = search,
                            onValueChange = { search = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Buscar conjuros") },
                            singleLine = true,
                        )
                        Button(onClick = ::beginAdd) { Text("Añadir") }
                    }
                    if (search.isNotBlank()) {
                        Text(
                            "La búsqueda filtra esta vista. El reordenamiento se habilita al limpiar la búsqueda.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }

        for (level in 0..9) {
            val levelSpells = visibleSpells
                .filter { it.level == level }
                .sortedWith(compareBy<CharacterSpell> { it.sortOrder }.thenBy { it.name.lowercase() })
            val allLevelCount = sourceFiltered.count { it.level == level }
            val isCollapsed = level in collapsed || levelSpells.isEmpty()
            item(key = "spell-level-$level") {
                SpellLevelSectionV4(
                    level = level,
                    spells = levelSpells,
                    totalInView = allLevelCount,
                    collapsed = isCollapsed,
                    searchActive = search.isNotBlank(),
                    sourceById = sourceById,
                    selectedSourceId = selectedSourceId,
                    onToggleCollapsed = {
                        if (levelSpells.isNotEmpty()) {
                            collapsedLevels = encodeLevelSetV4(
                                if (level in collapsed) collapsed - level else collapsed + level,
                            )
                        }
                    },
                    onPreparedChange = ::togglePrepared,
                    onMove = ::moveSpell,
                    onEdit = ::beginEdit,
                    onDelete = { deleteSpellId = it.id.toString() },
                )
            }
        }
    }

    if (editorOpen) {
        val existing = editingSpellId?.let { id -> draft.spells.firstOrNull { it.id.toString() == id } }
        SpellEditorDialogV4(
            spell = existing,
            sources = draft.sources,
            selectedSourceId = selectedSourceId,
            onCancel = { editorOpen = false },
            onApply = { edited ->
                val normalized = if (existing == null) {
                    val nextOrder = draft.spells.filter { it.level == edited.level }.maxOfOrNull { it.sortOrder }?.plus(1) ?: 0
                    edited.copy(sortOrder = nextOrder)
                } else if (existing.level == edited.level) {
                    edited.copy(sortOrder = existing.sortOrder)
                } else {
                    val nextOrder = draft.spells.filter { it.level == edited.level && it.id != existing.id }
                        .maxOfOrNull { it.sortOrder }?.plus(1) ?: 0
                    edited.copy(sortOrder = nextOrder)
                }
                val updated = if (existing == null) {
                    draft.spells + normalized
                } else {
                    draft.spells.map { if (it.id == existing.id) normalized else it }
                }
                updateSpells(normalizeSpellOrdersV4(updated))
                editorOpen = false
            },
        )
    }

    deleteSpellId?.let { id ->
        val target = draft.spells.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteSpellId = null
        } else {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("Eliminar conjuro") },
                text = { Text("¿Eliminar «${target.name}» de la ficha? Esta acción elimina el conjuro conceptual y todas sus asociaciones de fuente.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            updateSpells(normalizeSpellOrdersV4(draft.spells.filterNot { it.id == target.id }))
                            deleteSpellId = null
                        },
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { deleteSpellId = null }) { Text("Cancelar") }
                },
            )
        }
    }
}

@Composable
private fun SpellLevelSectionV4(
    level: Int,
    spells: List<CharacterSpell>,
    totalInView: Int,
    collapsed: Boolean,
    searchActive: Boolean,
    sourceById: Map<Uuid, io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource>,
    selectedSourceId: Uuid?,
    onToggleCollapsed: () -> Unit,
    onPreparedChange: (CharacterSpell, Uuid, Boolean) -> Unit,
    onMove: (CharacterSpell, Int) -> Boolean,
    onEdit: (CharacterSpell) -> Unit,
    onDelete: (CharacterSpell) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            TextButton(
                onClick = onToggleCollapsed,
                enabled = spells.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(spellLevelLabelV4(level), style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                    Text(
                        if (searchActive) "${spells.size}/$totalInView" else totalInView.toString(),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    if (spells.isNotEmpty()) {
                        Text(if (collapsed) "  Mostrar" else "  Ocultar", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            if (!collapsed && spells.isNotEmpty()) {
                HorizontalDivider()
                spells.forEachIndexed { index, spell ->
                    SpellRowV4(
                        spell = spell,
                        sourceById = sourceById,
                        selectedSourceId = selectedSourceId,
                        reorderEnabled = !searchActive && spells.size > 1,
                        onPreparedChange = onPreparedChange,
                        onMove = onMove,
                        onEdit = { onEdit(spell) },
                        onDelete = { onDelete(spell) },
                    )
                    if (index != spells.lastIndex) HorizontalDivider()
                }
            } else if (totalInView == 0) {
                Text(
                    "Sin conjuros",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SpellRowV4(
    spell: CharacterSpell,
    sourceById: Map<Uuid, io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource>,
    selectedSourceId: Uuid?,
    reorderEnabled: Boolean,
    onPreparedChange: (CharacterSpell, Uuid, Boolean) -> Unit,
    onMove: (CharacterSpell, Int) -> Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    var accumulatedDrag by remember(spell.id) { mutableStateOf(0f) }
    val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }
    val selectedAssociation = selectedSourceId?.let { sourceId ->
        spell.sourceAssociations.firstOrNull { it.sourceId == sourceId }
    }

    Row(
        modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (reorderEnabled) {
            StableDragHandle(
                modifier = Modifier.pointerInput(spell.id, selectedSourceId) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { accumulatedDrag = 0f },
                        onDragEnd = { accumulatedDrag = 0f },
                        onDragCancel = { accumulatedDrag = 0f },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            accumulatedDrag += dragAmount.y
                            while (abs(accumulatedDrag) >= reorderStepPx) {
                                val direction = if (accumulatedDrag > 0f) 1 else -1
                                if (onMove(spell, direction)) {
                                    accumulatedDrag -= direction * reorderStepPx
                                } else {
                                    accumulatedDrag = 0f
                                    break
                                }
                            }
                        },
                    )
                },
                contentDescription = "Mantén pulsado y arrastra para reordenar ${spell.name}",
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(spell.name.ifBlank { "Conjuro sin nombre" }, style = MaterialTheme.typography.labelLarge)
            val compact = spellCompactSummaryV4(spell)
            if (compact.isNotBlank()) {
                Text(compact, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            if (selectedSourceId == null) {
                val sourceState = spell.sourceAssociations.mapNotNull { association ->
                    sourceById[association.sourceId]?.name?.let { name -> "$name ${if (association.prepared) "✓" else "○"}" }
                }.joinToString(" · ")
                if (sourceState.isNotBlank()) {
                    Text(sourceState, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        if (selectedAssociation != null && selectedSourceId != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Checkbox(
                    checked = selectedAssociation.prepared,
                    onCheckedChange = { onPreparedChange(spell, selectedSourceId, it) },
                )
                Text("Preparado", style = MaterialTheme.typography.labelSmall)
            }
        }
        TextButton(onClick = onEdit, contentPadding = PaddingValues(horizontal = 5.dp)) { Text("Editar") }
        TextButton(onClick = onDelete, contentPadding = PaddingValues(horizontal = 5.dp)) { Text("Eliminar") }
    }
}

@Composable
private fun SpellEditorDialogV4(
    spell: CharacterSpell?,
    sources: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource>,
    selectedSourceId: Uuid?,
    onCancel: () -> Unit,
    onApply: (CharacterSpell) -> Unit,
) {
    var name by rememberSaveable(spell?.id?.toString(), "spell-name") { mutableStateOf(spell?.name ?: "") }
    var level by rememberSaveable(spell?.id?.toString(), "spell-level") { mutableStateOf(spell?.level?.toString() ?: "0") }
    var castingTime by rememberSaveable(spell?.id?.toString(), "spell-casting") { mutableStateOf(spell?.castingTime ?: "") }
    var rangeText by rememberSaveable(spell?.id?.toString(), "spell-range") { mutableStateOf(spell?.rangeText ?: "") }
    var verbal by rememberSaveable(spell?.id?.toString(), "spell-v") { mutableStateOf(spell?.verbal ?: false) }
    var somatic by rememberSaveable(spell?.id?.toString(), "spell-s") { mutableStateOf(spell?.somatic ?: false) }
    var material by rememberSaveable(spell?.id?.toString(), "spell-m") { mutableStateOf(spell?.material ?: false) }
    var materialText by rememberSaveable(spell?.id?.toString(), "spell-material") { mutableStateOf(spell?.materialText ?: "") }
    var duration by rememberSaveable(spell?.id?.toString(), "spell-duration") { mutableStateOf(spell?.duration ?: "") }
    var concentration by rememberSaveable(spell?.id?.toString(), "spell-concentration") { mutableStateOf(spell?.concentration ?: false) }
    var ritual by rememberSaveable(spell?.id?.toString(), "spell-ritual") { mutableStateOf(spell?.ritual ?: false) }
    var description by rememberSaveable(spell?.id?.toString(), "spell-description") { mutableStateOf(spell?.description ?: "") }
    var notes by rememberSaveable(spell?.id?.toString(), "spell-notes") { mutableStateOf(spell?.notes ?: "") }
    val initialAssociations = spell?.sourceAssociations.orEmpty()
    var associatedSourceIds by rememberSaveable(spell?.id?.toString(), "spell-sources") {
        mutableStateOf(
            initialAssociations.map { it.sourceId.toString() }.toMutableSet().apply {
                if (spell == null && selectedSourceId != null) add(selectedSourceId.toString())
            }.sorted().joinToString(","),
        )
    }
    var preparedSourceIds by rememberSaveable(spell?.id?.toString(), "spell-prepared") {
        mutableStateOf(initialAssociations.filter { it.prepared }.map { it.sourceId.toString() }.sorted().joinToString(","))
    }

    val associated = parseIdSetV4(associatedSourceIds)
    val prepared = parseIdSetV4(preparedSourceIds)
    val parsedLevel = level.trim().toIntOrNull()
    val canSave = name.isNotBlank() && parsedLevel != null && parsedLevel in 0..9 && associated.isNotEmpty()

    AlertDialog(
        onDismissRequest = {},
        title = { Text(if (spell == null) "Añadir conjuro" else "Editar conjuro") },
        text = {
            LazyColumn(
                modifier = Modifier
                    .heightIn(max = 560.dp)
                    .imePadding()
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 36.dp),
            ) {
                item {
                    OutlinedTextField(name, { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
                item {
                    OutlinedTextField(level, { level = it.filter { ch -> ch.isDigit() }.take(1) }, label = { Text("Nivel (0-9)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                }
                item {
                    Text("Fuentes", style = MaterialTheme.typography.titleSmall)
                    if (sources.isEmpty()) {
                        Text("Crea al menos una fuente antes de guardar un conjuro.", style = MaterialTheme.typography.bodySmall)
                    }
                }
                sources.forEach { source ->
                    item(key = "spell-source-edit-${source.id}") {
                        val sourceKey = source.id.toString()
                        val included = sourceKey in associated
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = included,
                                onCheckedChange = { checked ->
                                    val nextAssociated = associated.toMutableSet()
                                    val nextPrepared = prepared.toMutableSet()
                                    if (checked) nextAssociated += sourceKey else {
                                        nextAssociated -= sourceKey
                                        nextPrepared -= sourceKey
                                    }
                                    associatedSourceIds = nextAssociated.sorted().joinToString(",")
                                    preparedSourceIds = nextPrepared.sorted().joinToString(",")
                                },
                            )
                            Text(source.name, modifier = Modifier.weight(1f))
                            Checkbox(
                                checked = sourceKey in prepared,
                                enabled = included,
                                onCheckedChange = { checked ->
                                    val next = prepared.toMutableSet()
                                    if (checked) next += sourceKey else next -= sourceKey
                                    preparedSourceIds = next.sorted().joinToString(",")
                                },
                            )
                            Text("Preparado", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                item {
                    OutlinedTextField(castingTime, { castingTime = it }, label = { Text("Tiempo de lanzamiento") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    OutlinedTextField(rangeText, { rangeText = it }, label = { Text("Alcance") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(verbal, { verbal = it }); Text("V")
                        Checkbox(somatic, { somatic = it }); Text("S")
                        Checkbox(material, { material = it }); Text("M")
                    }
                }
                if (material) {
                    item {
                        OutlinedTextField(materialText, { materialText = it }, label = { Text("Componente material (opcional)") }, modifier = Modifier.fillMaxWidth())
                    }
                }
                item {
                    OutlinedTextField(duration, { duration = it }, label = { Text("Duración") }, modifier = Modifier.fillMaxWidth())
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(concentration, { concentration = it }); Text("Concentración")
                        Checkbox(ritual, { ritual = it }); Text("Ritual")
                    }
                }
                item {
                    OutlinedTextField(description, { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 4)
                }
                item {
                    OutlinedTextField(notes, { notes = it }, label = { Text("Notas (opcional)") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                }
                if (!canSave) {
                    item {
                        Text(
                            when {
                                name.isBlank() -> "El nombre no puede quedar vacío."
                                parsedLevel == null || parsedLevel !in 0..9 -> "El nivel debe estar entre 0 y 9."
                                associated.isEmpty() -> "Selecciona al menos una fuente."
                                else -> "Revisa los datos del conjuro.",
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = {
                    val associations = sources.mapNotNull { source ->
                        val key = source.id.toString()
                        if (key in associated) {
                            CharacterSpellSourceAssociation(source.id, prepared = key in prepared)
                        } else null
                    }
                    onApply(
                        CharacterSpell(
                            id = spell?.id ?: Uuid.random(),
                            name = name.trim(),
                            level = parsedLevel!!,
                            castingTime = castingTime.trim(),
                            rangeText = rangeText.trim(),
                            verbal = verbal,
                            somatic = somatic,
                            material = material,
                            materialText = materialText.trim().ifBlank { null },
                            duration = duration.trim(),
                            concentration = concentration,
                            ritual = ritual,
                            description = description.trim(),
                            notes = notes.trim().ifBlank { null },
                            sortOrder = spell?.sortOrder ?: 0,
                            sourceAssociations = associations,
                        ),
                    )
                },
            ) { Text("Aplicar") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancelar") }
        },
    )
}

private fun spellMatchesSearchV4(spell: CharacterSpell, query: String): Boolean = listOf(
    spell.name,
    spell.castingTime,
    spell.rangeText,
    spell.duration,
    spell.description,
    spell.notes.orEmpty(),
).any { it.lowercase().contains(query) }

private fun spellCompactSummaryV4(spell: CharacterSpell): String {
    val components = buildList {
        if (spell.verbal) add("V")
        if (spell.somatic) add("S")
        if (spell.material) add("M")
    }.joinToString("")
    return listOf(
        spell.castingTime,
        spell.rangeText,
        components,
        spell.duration,
        if (spell.concentration) "Concentración" else "",
        if (spell.ritual) "Ritual" else "",
    ).filter { it.isNotBlank() }.joinToString(" · ")
}

private fun spellLevelLabelV4(level: Int): String = if (level == 0) "Trucos" else "Nivel $level"

private fun parseLevelSetV4(raw: String): Set<Int> = raw.split(',').mapNotNull { it.toIntOrNull() }.toSet()

private fun encodeLevelSetV4(levels: Set<Int>): String = levels.sorted().joinToString(",")

private fun parseIdSetV4(raw: String): Set<String> = raw.split(',').filter { it.isNotBlank() }.toSet()

private fun normalizeSpellOrdersV4(spells: List<CharacterSpell>): List<CharacterSpell> {
    val orderById = (0..9).flatMap { level ->
        spells.filter { it.level == level }
            .sortedWith(compareBy<CharacterSpell> { it.sortOrder }.thenBy { it.name.lowercase() })
            .mapIndexed { index, spell -> spell.id to index }
    }.toMap()
    return spells.map { spell -> spell.copy(sortOrder = orderById[spell.id] ?: spell.sortOrder) }
}
