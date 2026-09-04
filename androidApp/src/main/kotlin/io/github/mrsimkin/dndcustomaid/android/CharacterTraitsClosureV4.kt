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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LinearProgressIndicator
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
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_TRAIT_FAVORITE_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterActivationType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCollectionQuery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitGrouping
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitType
import io.github.mrsimkin.dndcustomaid.shared.character.characterTraitSourceFilterKey
import io.github.mrsimkin.dndcustomaid.shared.character.characterTraitTypeDisplayLabel
import io.github.mrsimkin.dndcustomaid.shared.character.characterTraitTypeFilterKey
import io.github.mrsimkin.dndcustomaid.shared.character.characterTraitUsageMeter
import io.github.mrsimkin.dndcustomaid.shared.character.duplicateCharacterTrait
import io.github.mrsimkin.dndcustomaid.shared.character.groupCharacterTraits
import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess
import io.github.mrsimkin.dndcustomaid.shared.character.moveCharacterTraitManual
import io.github.mrsimkin.dndcustomaid.shared.character.presentCharacterTraits
import io.github.mrsimkin.dndcustomaid.shared.character.withQuickAccess
import kotlin.math.abs
import kotlin.uuid.Uuid

private const val TRAIT_FILTER_SEPARATOR_G1 = "\u001E"

@Composable
internal fun CharacterTraitsClosureTabV4(
    traits: List<CharacterTrait>,
    closureState: CharacterClosureState,
    persistedTraitIds: Set<Uuid>,
    onTraitsChange: (List<CharacterTrait>) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    structuralEditingEnabled: Boolean,
    wide: Boolean,
    hapticsEnabled: Boolean,
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var activeFiltersText by rememberSaveable { mutableStateOf("") }
    var groupingName by rememberSaveable { mutableStateOf(CharacterTraitGrouping.TYPE.name) }

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

    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)
    val grouping = runCatching { CharacterTraitGrouping.valueOf(groupingName) }
        .getOrDefault(CharacterTraitGrouping.TYPE)
    val activeFilters = activeFiltersText.split(TRAIT_FILTER_SEPARATOR_G1).filter { it.isNotBlank() }.toSet()
    val query = CharacterCollectionQuery(searchText = searchText, activeFilterKeys = activeFilters)
    val visibleTraits = presentCharacterTraits(
        traits = traits,
        query = query,
        isFavorite = { trait -> closureState.hasQuickAccess(CharacterQuickAccessKind.TRAIT, trait.id) },
    )
    val groups = groupCharacterTraits(visibleTraits, grouping)
    val canReorder = structuralEditingEnabled && query.searchText.isBlank() && query.activeFilterKeys.isEmpty()

    fun updateQuery(updated: CharacterCollectionQuery) {
        searchText = updated.searchText
        activeFiltersText = updated.activeFilterKeys.sorted().joinToString(TRAIT_FILTER_SEPARATOR_G1)
    }

    fun normalize(updated: List<CharacterTrait>): List<CharacterTrait> =
        updated.sortedWith(compareBy<CharacterTrait> { it.sortOrder }.thenBy { it.id.toString() })
            .mapIndexed { index, trait -> trait.copy(sortOrder = index) }

    fun beginAdd() {
        if (!structuralEditingEnabled) return
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
        if (!structuralEditingEnabled) return
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

    fun updateSpentUses(trait: CharacterTrait, delta: Int) {
        val max = trait.maxUses ?: return
        val next = (trait.spentUses + delta).coerceIn(0, max)
        if (next == trait.spentUses) return
        onTraitsChange(
            traits.map { item -> if (item.id == trait.id) item.copy(spentUses = next) else item },
        )
        haptic(CharacterHapticEventV4.RESOURCE)
    }

    fun duplicate(trait: CharacterTrait) {
        if (!structuralEditingEnabled) return
        val duplicated = duplicateCharacterTrait(
            source = trait,
            newId = Uuid.random(),
            sortOrder = traits.size,
        )
        onTraitsChange(normalize(traits + duplicated))
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
            bottom = 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item {
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
                            Text("Rasgos", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Clase, especie/raza, trasfondo, dotes, dones y contenido personalizado.",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text("+ Añadir") }
                    }
                    CharacterCollectionToolbarV4(
                        itemCount = visibleTraits.size,
                        query = query,
                        onQueryChange = ::updateQuery,
                        filters = traitFiltersG1(traits, closureState),
                        searchLabel = "Buscar rasgos",
                    )
                    TraitGroupingControlsG1(
                        grouping = grouping,
                        onGroupingChange = { groupingName = it.name },
                    )
                    if (!canReorder && visibleTraits.isNotEmpty()) {
                        Text(
                            "Limpia búsqueda y filtros para reordenar manualmente.",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }

        if (traits.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Sin rasgos registrados. La app no crea rasgos automáticamente desde otras secciones.",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        } else if (visibleTraits.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "No hay rasgos que coincidan con la búsqueda y filtros actuales.",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        } else {
            groups.forEach { group ->
                item(key = "trait-group-${group.key}") {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 5.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            if (grouping != CharacterTraitGrouping.NONE) {
                                Text(
                                    "${group.label} (${group.traits.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                            val columns = if (wide) 2 else 1
                            group.traits.chunked(columns).forEach { rowTraits ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.Top,
                                ) {
                                    rowTraits.forEach { trait ->
                                        TraitCardG1(
                                            trait = trait,
                                            favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.TRAIT, trait.id),
                                            favoriteEnabled = structuralEditingEnabled && trait.id in persistedTraitIds,
                                            canReorder = canReorder,
                                            structuralEditingEnabled = structuralEditingEnabled,
                                            onFavoriteChange = { enabled ->
                                                onClosureStateChange(
                                                    closureState.withQuickAccess(
                                                        CharacterQuickAccessKind.TRAIT,
                                                        trait.id,
                                                        enabled,
                                                    ),
                                                )
                                            },
                                            onEdit = { beginEdit(trait) },
                                            onMove = { offset ->
                                                val moved = moveCharacterTraitManual(
                                                    traits = traits,
                                                    traitId = trait.id,
                                                    offset = offset,
                                                    grouping = grouping,
                                                )
                                                if (moved == normalize(traits)) {
                                                    false
                                                } else {
                                                    onTraitsChange(moved)
                                                    true
                                                }
                                            },
                                            onSpendUse = { updateSpentUses(trait, 1) },
                                            onRecoverUse = { updateSpentUses(trait, -1) },
                                            onDuplicate = { duplicate(trait) },
                                            onDelete = { deleteId = trait.id.toString() },
                                            onHaptic = haptic,
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
    }

    if (editorOpen && structuralEditingEnabled) {
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

        TraitEditorDialogG1(
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
            onMaxUsesChange = { editorMaxUses = traitUnsignedIntegerG1(it) },
            onSpentUsesChange = { editorSpentUses = traitUnsignedIntegerG1(it).ifBlank { "0" } },
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
                    pinned = existing?.pinned ?: false,
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

    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->
        val target = traits.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "rasgo",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    onTraitsChange(normalize(traits.filterNot { it.id == target.id }))
                    haptic(CharacterHapticEventV4.DESTRUCTIVE)
                    deleteId = null
                },
            )
        }
    }
}

private fun traitFiltersG1(
    traits: List<CharacterTrait>,
    closureState: CharacterClosureState,
): List<CharacterFilterOptionV4> {
    val favoriteCount = traits.count {
        closureState.hasQuickAccess(CharacterQuickAccessKind.TRAIT, it.id)
    }
    val typeFilters = CharacterTraitType.entries.mapNotNull { type ->
        val count = traits.count { it.type == type }
        count.takeIf { it > 0 }?.let {
            CharacterFilterOptionV4(
                key = characterTraitTypeFilterKey(type),
                label = characterTraitTypeDisplayLabel(type),
                count = count,
            )
        }
    }
    val sourceFilters = traits
        .groupBy { characterTraitSourceFilterKey(it.source) }
        .map { (key, matching) ->
            CharacterFilterOptionV4(
                key = key,
                label = matching.firstNotNullOfOrNull { trait -> trait.source.trim().takeIf(String::isNotEmpty) }
                    ?: "Sin fuente",
                count = matching.size,
            )
        }
        .sortedBy { it.label.lowercase() }
    return buildList {
        add(CharacterFilterOptionV4(CHARACTER_TRAIT_FAVORITE_FILTER_KEY, "Favoritos", favoriteCount))
        addAll(typeFilters)
        addAll(sourceFilters)
    }
}

@Composable
private fun TraitGroupingControlsG1(
    grouping: CharacterTraitGrouping,
    onGroupingChange: (CharacterTraitGrouping) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text("Agrupar", style = MaterialTheme.typography.labelSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            CharacterTraitGrouping.entries.forEach { option ->
                val label = when (option) {
                    CharacterTraitGrouping.NONE -> "Sin agrupar"
                    CharacterTraitGrouping.TYPE -> "Tipo"
                    CharacterTraitGrouping.SOURCE -> "Fuente"
                }
                if (grouping == option) {
                    Button(
                        onClick = { onGroupingChange(option) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    ) { Text(label) }
                } else {
                    OutlinedButton(
                        onClick = { onGroupingChange(option) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    ) { Text(label) }
                }
            }
        }
    }
}

@Composable
private fun TraitCardG1(
    trait: CharacterTrait,
    favorite: Boolean,
    favoriteEnabled: Boolean,
    canReorder: Boolean,
    structuralEditingEnabled: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onMove: (Int) -> Boolean,
    onSpendUse: () -> Unit,
    onRecoverUse: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
    modifier: Modifier = Modifier,
) {
    var accumulatedDrag by remember(trait.id) { mutableStateOf(0f) }
    var dragging by remember(trait.id) { mutableStateOf(false) }
    val reorderStepPx = with(LocalDensity.current) { 44.dp.toPx() }
    val dragState = CharacterDragVisualStateV4(
        active = dragging,
        offsetY = accumulatedDrag,
        showDropBefore = dragging && accumulatedDrag < 0f,
        showDropAfter = dragging && accumulatedDrag > 0f,
    )
    val metadata = buildList {
        trait.source.takeIf { it.isNotBlank() }?.let(::add)
        add(characterTraitTypeDisplayLabel(trait.type))
        trait.activation?.let { add(traitActivationLabelG1(it)) }
    }.joinToString(" · ")
    val meter = characterTraitUsageMeter(trait)

    Column(modifier = modifier.fillMaxWidth()) {
        CharacterDropIndicatorV4(visible = dragState.showDropBefore)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .characterDragFeedbackV4(dragState)
                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (canReorder) {
                        StableDragHandle(
                            modifier = Modifier.pointerInput(trait.id) {
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
                            contentDescription = "Mantén pulsado y arrastra para reordenar ${trait.name}",
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            trait.name,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            metadata,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    TextButton(
                        onClick = { onFavoriteChange(!favorite) },
                        enabled = favoriteEnabled,
                        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                    ) { Text(if (favorite) "★" else "☆") }
                }

                Text(
                    trait.description.ifBlank { "Sin descripción" },
                    style = if (trait.description.isBlank()) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                meter?.let { usage ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        LinearProgressIndicator(
                            progress = { usage.remainingFraction },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(
                            "${usage.remaining} / ${usage.max} disponibles" +
                                if (usage.spent > 0) " · ${usage.spent} gastados" else "",
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
                                enabled = usage.spent > 0,
                                contentPadding = PaddingValues(horizontal = 7.dp, vertical = 0.dp),
                            ) { Text("Recuperar") }
                            TextButton(
                                onClick = onSpendUse,
                                enabled = usage.remaining > 0,
                                contentPadding = PaddingValues(horizontal = 7.dp, vertical = 0.dp),
                            ) { Text("Gastar") }
                        }
                    }
                }

                trait.notes?.takeIf { it.isNotBlank() }?.let {
                    Text(it, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (structuralEditingEnabled) {
                        TextButton(
                            onClick = onDuplicate,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                        ) { Text("Duplicar") }
                        StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${trait.name}")
                    }
                }
            }
        }
        CharacterDropIndicatorV4(visible = dragState.showDropAfter)
    }
}

@Composable
private fun TraitEditorDialogG1(
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

    CharacterImeSafeEditorDialog(
        title = title,
        onCancel = onDismiss,
        onSave = onApply,
        saveEnabled = valid,
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = source,
            onValueChange = onSourceChange,
            label = { Text("Fuente") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Column {
            Text("Tipo", style = MaterialTheme.typography.labelSmall)
            androidx.compose.foundation.layout.Box {
                OutlinedButton(onClick = { typeMenuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(characterTraitTypeDisplayLabel(type))
                }
                DropdownMenu(expanded = typeMenuOpen, onDismissRequest = { typeMenuOpen = false }) {
                    CharacterTraitType.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(characterTraitTypeDisplayLabel(option)) },
                            onClick = {
                                onTypeChange(option)
                                typeMenuOpen = false
                            },
                        )
                    }
                }
            }
        }
        Column {
            Text("Activación", style = MaterialTheme.typography.labelSmall)
            androidx.compose.foundation.layout.Box {
                OutlinedButton(onClick = { activationMenuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(activation?.let(::traitActivationLabelG1) ?: "Sin especificar")
                }
                DropdownMenu(expanded = activationMenuOpen, onDismissRequest = { activationMenuOpen = false }) {
                    DropdownMenuItem(
                        text = { Text("Sin especificar") },
                        onClick = {
                            onActivationChange(null)
                            activationMenuOpen = false
                        },
                    )
                    CharacterActivationType.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(traitActivationLabelG1(option)) },
                            onClick = {
                                onActivationChange(option)
                                activationMenuOpen = false
                            },
                        )
                    }
                }
            }
        }
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 7,
        )
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 5,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                value = maxUses,
                onValueChange = onMaxUsesChange,
                label = { Text("Usos máximos") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = spentUses,
                onValueChange = onSpentUsesChange,
                label = { Text("Gastados") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                enabled = maxUses.isNotBlank(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        OutlinedTextField(
            value = recovery,
            onValueChange = onRecoveryChange,
            label = { Text("Recuperación") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        CharacterInlineValidationMessage(
            when {
                name.trim().isEmpty() -> "El nombre no puede quedar vacío."
                maxUses.isNotBlank() && (maxUses.toIntOrNull() == null || maxUses.toInt() <= 0) ->
                    "Los usos máximos deben ser un entero mayor que 0."
                maxUses.isNotBlank() &&
                    (spentUses.toIntOrNull() == null || spentUses.toInt() !in 0..(maxUses.toIntOrNull() ?: 0)) ->
                    "Los usos gastados deben estar entre 0 y el máximo."
                else -> null
            },
        )
    }
}

private fun traitUnsignedIntegerG1(raw: String): String = raw.filter(Char::isDigit)

private fun traitActivationLabelG1(type: CharacterActivationType): String = when (type) {
    CharacterActivationType.PASSIVE -> "Pasivo"
    CharacterActivationType.ACTION -> "Acción"
    CharacterActivationType.BONUS_ACTION -> "Acción adicional"
    CharacterActivationType.REACTION -> "Reacción"
    CharacterActivationType.OTHER -> "Otro"
}
