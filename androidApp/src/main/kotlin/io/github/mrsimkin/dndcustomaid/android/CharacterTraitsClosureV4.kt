package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCollectionQuery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourcePlacement
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourceSuccessorConfiguration
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CharacterTraitsClosureTabV4(
    traits: List<CharacterTrait>,
    classes: List<CharacterClassLevel>,
    background: CharacterBackground,
    closureState: CharacterClosureState,
    persistedTraitIds: Set<Uuid>,
    resources: List<CharacterResource>,
    onTraitsChange: (List<CharacterTrait>) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    onResourceValueChange: (Uuid, Int) -> Unit,
    structuralEditingEnabled: Boolean,
    wide: Boolean,
    hapticsEnabled: Boolean,
) {
    var searchText by rememberSaveable { mutableStateOf("") }
    var activeFiltersText by rememberSaveable { mutableStateOf("") }
    var groupingName by rememberSaveable { mutableStateOf(CharacterTraitGrouping.TYPE.name) }
    var reorderMode by rememberSaveable("trait-linear-reorder") { mutableStateOf(false) }

    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorName by rememberSaveable { mutableStateOf("") }
    var editorSource by rememberSaveable { mutableStateOf("") }
    var editorCustomOrigin by rememberSaveable("trait-custom-origin") { mutableStateOf(false) }
    var editorTypeName by rememberSaveable { mutableStateOf(CharacterTraitType.OTHER.name) }
    var editorDescription by rememberSaveable { mutableStateOf("") }
    var editorNotes by rememberSaveable { mutableStateOf("") }
    var editorMaxUses by rememberSaveable { mutableStateOf("") }
    var editorSpentUses by rememberSaveable { mutableStateOf("0") }
    var editorRecovery by rememberSaveable { mutableStateOf("") }
    var editorActivationName by rememberSaveable { mutableStateOf("") }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }

    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)
    val settingsContext = LocalCharacterPcSettingsContextV4.current
    val resourceConfigurations = settingsContext?.successorState?.resourceConfigurations.orEmpty().associateBy { it.resourceId }
    val traitResources = resources
        .filter { resource -> CharacterResourcePlacement.TRAITS in (resourceConfigurations[resource.id]?.placements ?: emptySet()) }
        .sortedBy { it.sortOrder }
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
    val reorderAvailable = structuralEditingEnabled && query.searchText.isBlank() && query.activeFilterKeys.isEmpty()
    val canReorder = reorderAvailable && reorderMode

    fun updateQuery(updated: CharacterCollectionQuery) {
        reorderMode = false
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
        editorCustomOrigin = false
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
        val originOptions = traitOriginOptionsG5(trait.type, classes, background)
        editorCustomOrigin = originOptions.isNotEmpty() && trait.source.isNotBlank() &&
            originOptions.none { it.label.equals(trait.source.trim(), ignoreCase = true) }
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
            start = appSpacingV4(if (wide) 10.dp else 5.dp),
            end = appSpacingV4(if (wide) 10.dp else 5.dp),
            top = appSpacingV4(5.dp),
            bottom = appSpacingV4(88.dp),
        ),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
    ) {
        stickyHeader(key = "traits-tools") {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = appSpacingV4(7.dp),
                        vertical = appSpacingV4(6.dp),
                    ),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Rasgos", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Clase, raza, trasfondo, dotes, dones / bendiciones y contenido personalizado.",
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
                        onGroupingChange = {
                            reorderMode = false
                            groupingName = it.name
                        },
                    )
                    if (reorderAvailable && visibleTraits.size > 1) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            CharacterLinearReorderModeControlV4(
                                active = reorderMode,
                                onToggle = { reorderMode = !reorderMode },
                            )
                        }
                        if (reorderMode) {
                            CharacterHelpV4(
                                "Reordenación lineal: los rasgos pasan temporalmente a una columna para usar el arrastre vertical estable. Pulsa Listo para volver a tus columnas.",
                            )
                        }
                    } else if (visibleTraits.isNotEmpty()) {
                        Text(
                            "Limpia búsqueda y filtros para activar Reordenar.",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }

        if (traitResources.isNotEmpty()) {
            item(key = "traits-resources") {
                TraitsResourcesCardG2(
                    resources = traitResources,
                    configurations = resourceConfigurations,
                    onResourceValueChange = { resourceId, value ->
                        onResourceValueChange(resourceId, value)
                        haptic(CharacterHapticEventV4.RESOURCE)
                    },
                )
            }
        }

        if (traits.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Sin rasgos registrados. La app no crea rasgos automáticamente desde otras secciones.",
                        modifier = Modifier.padding(appSpacingV4(10.dp)),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        } else if (visibleTraits.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "No hay rasgos que coincidan con la búsqueda y filtros actuales.",
                        modifier = Modifier.padding(appSpacingV4(10.dp)),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        } else {
            groups.forEach { group ->
                item(key = "trait-group-${group.key}") {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(5.dp)),
                            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                        ) {
                            if (grouping != CharacterTraitGrouping.NONE) {
                                Text(
                                    "${group.label} (${group.traits.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                            val columns = if (reorderMode) {
                                1
                            } else {
                                constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)
                            }
                            group.traits.chunked(columns).forEachIndexed { rowIndex, rowTraits ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                                    verticalAlignment = Alignment.Top,
                                ) {
                                    rowTraits.forEachIndexed { columnIndex, trait ->
                                        TraitCardG1(
                                            trait = trait,
                                            gridIndex = rowIndex * columns + columnIndex,
                                            gridItemCount = group.traits.size,
                                            gridColumns = columns,
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
            originOptionsForType = { originType ->
                traitOriginOptionsG5(traitTypeForOriginG2(originType), classes, background)
            },
            customOriginSelected = editorCustomOrigin,
            description = editorDescription,
            notes = editorNotes,
            maxUses = editorMaxUses,
            spentUses = editorSpentUses,
            recovery = editorRecovery,
            activation = selectedActivation,
            valid = valid,
            onNameChange = { editorName = it },
            onSourceChange = { editorSource = it },
            onCustomOriginSelectedChange = { editorCustomOrigin = it },
            onTypeChange = {
                editorTypeName = it.name
                editorSource = ""
                editorCustomOrigin = false
            },
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

private fun traitOriginOptionsG5(
    type: CharacterTraitType,
    classes: List<CharacterClassLevel>,
    background: CharacterBackground,
): List<CharacterOriginOptionV4> = when (type) {
    CharacterTraitType.CLASS -> classes
        .sortedBy { it.sortOrder }
        .mapNotNull { classLevel ->
            classLevel.name.trim().takeIf(String::isNotEmpty)?.let { name ->
                CharacterOriginOptionV4("class:${classLevel.id}", name)
            }
        }
        .distinctBy { it.label.lowercase() }
    CharacterTraitType.SPECIES_RACE -> background.race.trim().takeIf(String::isNotEmpty)
        ?.let { listOf(CharacterOriginOptionV4("race", it)) }
        .orEmpty()
    CharacterTraitType.BACKGROUND -> background.name.trim().takeIf(String::isNotEmpty)
        ?.let { listOf(CharacterOriginOptionV4("background", it)) }
        .orEmpty()
    CharacterTraitType.FEAT,
    CharacterTraitType.GIFT_BLESSING,
    CharacterTraitType.OTHER,
    -> emptyList()
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
    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp))) {
        Text("Agrupar", style = MaterialTheme.typography.labelSmall)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
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
    gridIndex: Int,
    gridItemCount: Int,
    gridColumns: Int,
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
    var dragState by remember(trait.id) { mutableStateOf(CharacterDragVisualStateV4()) }
    val reorderModifier = if (gridColumns > 1) {
        Modifier.characterMeasuredGridReorderDragV4(
            enabled = canReorder,
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
            enabled = canReorder,
            onHaptic = onHaptic,
            onMove = onMove,
            onVisualStateChange = { dragState = it },
        )
    }
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
                .then(reorderModifier)
                .characterDragFeedbackV4(dragState)
                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(5.dp)),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
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
                    StableFavoriteIconButton(
                        selected = favorite,
                        onClick = { onFavoriteChange(!favorite) },
                        enabled = favoriteEnabled,
                    )
                }

                Text(
                    trait.description.ifBlank { "Sin descripción" },
                    style = if (trait.description.isBlank()) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                meter?.let { usage ->
                    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp))) {
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
                        StableDuplicateIconButton(onClick = onDuplicate, contentDescription = "Duplicar ${trait.name}")
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
    originOptionsForType: (CharacterOriginTypeV4) -> List<CharacterOriginOptionV4>,
    customOriginSelected: Boolean,
    description: String,
    notes: String,
    maxUses: String,
    spentUses: String,
    recovery: String,
    activation: CharacterActivationType?,
    valid: Boolean,
    onNameChange: (String) -> Unit,
    onSourceChange: (String) -> Unit,
    onCustomOriginSelectedChange: (Boolean) -> Unit,
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
        val originType = traitOriginTypeG2(type)
        val originOptions = originOptionsForType(originType)
        val originKey = originOptions.firstOrNull { option ->
            option.label.equals(source.trim(), ignoreCase = true)
        }?.key
        CharacterProvenanceRowV4(
            originType = originType,
            originKey = originKey,
            customOrigin = source,
            optionsForType = originOptionsForType,
            onOriginTypeChange = { onTypeChange(traitTypeForOriginG2(it)) },
            onOriginKeyChange = { key ->
                originOptionsForType(originType)
                    .firstOrNull { it.key == key }
                    ?.let { onSourceChange(it.label) }
            },
            onCustomOriginChange = onSourceChange,
            allowCustomOriginOption = true,
            customOriginSelected = customOriginSelected || (source.isNotBlank() && originKey == null),
            onCustomOriginSelectedChange = onCustomOriginSelectedChange,
            allowedTypes = listOf(
                CharacterOriginTypeV4.CLASS,
                CharacterOriginTypeV4.RACE,
                CharacterOriginTypeV4.BACKGROUND,
                CharacterOriginTypeV4.FEAT,
                CharacterOriginTypeV4.GIFT,
                CharacterOriginTypeV4.OTHER,
            ),
        )
        CharacterHelpV4(
            "Tipo de origen describe de dónde nace el rasgo; Origen específico identifica la clase, raza, trasfondo, dote, don/bendición o creación concreta. Ambos se guardan en el mismo rasgo, no como fuentes paralelas.",
        )
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
        Row(horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp))) {
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

@Composable
private fun TraitsResourcesCardG2(
    resources: List<CharacterResource>,
    configurations: Map<Uuid, CharacterResourceSuccessorConfiguration>,
    onResourceValueChange: (Uuid, Int) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(4.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Text("Recursos de Rasgos", style = MaterialTheme.typography.titleSmall)
            resources.forEach { resource ->
                val configuration = configurations[resource.id] ?: return@forEach
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        resource.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    when (configuration.valueKind) {
                        CharacterTrackableValueKind.BINARY -> {
                            val active = resource.currentValue > 0
                            TraitResourceChipG2(if (active) "Activo" else "Inactivo", active) {
                                onResourceValueChange(resource.id, if (active) 0 else 1)
                            }
                        }
                        CharacterTrackableValueKind.COUNTER,
                        CharacterTrackableValueKind.CURRENT_MAX,
                        -> {
                            val maximum = if (configuration.valueKind == CharacterTrackableValueKind.CURRENT_MAX) resource.maxValue else null
                            TraitResourceStepG2("−", resource.currentValue > 0) {
                                onResourceValueChange(resource.id, (resource.currentValue - 1).coerceAtLeast(0))
                            }
                            Text(
                                maximum?.let { "${resource.currentValue}/$it" } ?: resource.currentValue.toString(),
                                style = MaterialTheme.typography.labelLarge,
                            )
                            TraitResourceStepG2("+", maximum?.let { resource.currentValue < it } ?: true) {
                                val next = resource.currentValue + 1
                                onResourceValueChange(resource.id, maximum?.let { next.coerceAtMost(it) } ?: next)
                            }
                        }
                    }
                }
            }
            CharacterHelpV4("Estos controles operan el mismo recurso canónico que Gestión, General o Equipo; la pestaña solo cambia dónde se proyecta.")
        }
    }
}

@Composable
private fun TraitResourceChipG2(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.heightIn(min = 30.dp).clickable(onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(3.dp)),
            contentAlignment = Alignment.Center,
        ) { Text(label, style = MaterialTheme.typography.labelSmall) }
    }
}

@Composable
private fun TraitResourceStepG2(label: String, enabled: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.heightIn(min = 30.dp).clickable(enabled = enabled, onClick = onClick),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(3.dp)),
            contentAlignment = Alignment.Center,
        ) { Text(label, style = MaterialTheme.typography.labelLarge) }
    }
}

private fun traitOriginTypeG2(type: CharacterTraitType): CharacterOriginTypeV4 = when (type) {
    CharacterTraitType.CLASS -> CharacterOriginTypeV4.CLASS
    CharacterTraitType.SPECIES_RACE -> CharacterOriginTypeV4.RACE
    CharacterTraitType.BACKGROUND -> CharacterOriginTypeV4.BACKGROUND
    CharacterTraitType.FEAT -> CharacterOriginTypeV4.FEAT
    CharacterTraitType.GIFT_BLESSING -> CharacterOriginTypeV4.GIFT
    CharacterTraitType.OTHER -> CharacterOriginTypeV4.OTHER
}

private fun traitTypeForOriginG2(origin: CharacterOriginTypeV4): CharacterTraitType = when (origin) {
    CharacterOriginTypeV4.CLASS -> CharacterTraitType.CLASS
    CharacterOriginTypeV4.RACE -> CharacterTraitType.SPECIES_RACE
    CharacterOriginTypeV4.BACKGROUND -> CharacterTraitType.BACKGROUND
    CharacterOriginTypeV4.FEAT -> CharacterTraitType.FEAT
    CharacterOriginTypeV4.GIFT -> CharacterTraitType.GIFT_BLESSING
    CharacterOriginTypeV4.PACT,
    CharacterOriginTypeV4.ITEM,
    CharacterOriginTypeV4.OTHER,
    -> CharacterTraitType.OTHER
}

private fun traitUnsignedIntegerG1(raw: String): String = raw.filter(Char::isDigit)

private fun traitActivationLabelG1(type: CharacterActivationType): String = when (type) {
    CharacterActivationType.PASSIVE -> "Pasivo"
    CharacterActivationType.ACTION -> "Acción"
    CharacterActivationType.BONUS_ACTION -> "Acción adicional"
    CharacterActivationType.REACTION -> "Reacción"
    CharacterActivationType.OTHER -> "Otro"
}
