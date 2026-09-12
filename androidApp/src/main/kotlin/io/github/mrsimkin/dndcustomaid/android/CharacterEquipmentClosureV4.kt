package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCollectionQuery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterConsumableKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryCarryState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryFilterKey
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryUsage
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResource
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourcePlacement
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResourceSuccessorConfiguration
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrackableValueKind
import io.github.mrsimkin.dndcustomaid.shared.character.carriedInventoryWeightLb
import io.github.mrsimkin.dndcustomaid.shared.character.consumeInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.duplicateInventoryItem
import io.github.mrsimkin.dndcustomaid.shared.character.duplicateInventoryUsage
import io.github.mrsimkin.dndcustomaid.shared.character.effectiveInventoryCarryState
import io.github.mrsimkin.dndcustomaid.shared.character.mergeCharacterReorderedSubset
import io.github.mrsimkin.dndcustomaid.shared.character.presentCharacterInventorySection
import io.github.mrsimkin.dndcustomaid.shared.character.withInventoryUsage
import java.util.Locale
import kotlin.math.abs
import kotlin.uuid.Uuid

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CharacterEquipmentClosureTabV4(
    draft: CharacterEquipmentDraftV4,
    onDraftChange: (CharacterEquipmentDraftV4) -> Unit,
    onOperationalItemsChange: (List<CharacterInventoryItem>) -> Unit,
    armorClass: Int,
    resources: List<CharacterResource>,
    onResourceValueChange: (Uuid, Int) -> Unit,
    structuralEditingEnabled: Boolean,
    wide: Boolean,
    hapticsEnabled: Boolean,
) {
    var ordinaryOrderName by rememberSaveable { mutableStateOf(CharacterPresentationOrder.MANUAL.name) }
    var specialOrderName by rememberSaveable { mutableStateOf(CharacterPresentationOrder.MANUAL.name) }
    var searchText by rememberSaveable { mutableStateOf("") }
    var activeFiltersText by rememberSaveable { mutableStateOf("") }
    var ordinaryCollapsed by rememberSaveable { mutableStateOf(false) }
    var specialCollapsed by rememberSaveable { mutableStateOf(false) }

    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var editorName by rememberSaveable { mutableStateOf("") }
    var editorQuantity by rememberSaveable { mutableStateOf("1") }
    var editorWeight by rememberSaveable { mutableStateOf("") }
    var editorEquipped by rememberSaveable { mutableStateOf(false) }
    var editorNotes by rememberSaveable { mutableStateOf("") }
    var editorSpecial by rememberSaveable { mutableStateOf(false) }
    var editorDescription by rememberSaveable { mutableStateOf("") }
    var editorLocation by rememberSaveable { mutableStateOf("") }
    var editorAttuned by rememberSaveable { mutableStateOf(false) }
    var editorKindName by rememberSaveable { mutableStateOf(CharacterConsumableKind.NONE.name) }
    var editorQuickUse by rememberSaveable { mutableStateOf("1") }
    var editorCarryName by rememberSaveable { mutableStateOf(CharacterInventoryCarryState.CARRIED.name) }
    var confirmSpecialRemoval by rememberSaveable { mutableStateOf(false) }
    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }

    var addCurrencyOpen by rememberSaveable { mutableStateOf(false) }
    var customCurrencyName by rememberSaveable { mutableStateOf("") }
    var customCurrencyAmount by rememberSaveable { mutableStateOf("0") }

    val ordinaryOrder = runCatching { CharacterPresentationOrder.valueOf(ordinaryOrderName) }
        .getOrDefault(CharacterPresentationOrder.MANUAL)
    val specialOrder = runCatching { CharacterPresentationOrder.valueOf(specialOrderName) }
        .getOrDefault(CharacterPresentationOrder.MANUAL)
    val activeFilters = activeFiltersText.split('|').filter { it.isNotBlank() }.toSet()
    val query = CharacterCollectionQuery(searchText = searchText, activeFilterKeys = activeFilters)
    val usageById = remember(draft.inventoryUsage) { draft.inventoryUsage.associateBy { it.itemId } }
    fun usageFor(item: CharacterInventoryItem): CharacterInventoryUsage =
        usageById[item.id] ?: CharacterInventoryUsage(item.id)

    val ordinaryVisible = presentCharacterInventorySection(
        draft.items,
        special = false,
        order = ordinaryOrder,
        query = query,
        usageFor = ::usageFor,
    )
    val specialVisible = presentCharacterInventorySection(
        draft.items,
        special = true,
        order = specialOrder,
        query = query,
        usageFor = ::usageFor,
    )
    val carriedWeight = carriedInventoryWeightLb(draft.items, ::usageFor)
    val attunedCount = draft.items.count { it.special && it.attuned }
    val reorderAvailableOrdinary =
        structuralEditingEnabled && ordinaryOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()
    val reorderAvailableSpecial =
        structuralEditingEnabled && specialOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()
    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)
    val settingsContext = LocalCharacterPcSettingsContextV4.current
    val successorState = settingsContext?.successorState
    val resourceConfigurations = successorState?.resourceConfigurations.orEmpty().associateBy { it.resourceId }
    val equipmentResources = resources
        .filter { resource -> CharacterResourcePlacement.EQUIPMENT in (resourceConfigurations[resource.id]?.placements ?: emptySet()) }
        .sortedBy { it.sortOrder }
    val equippedItems = draft.items.filter { it.equipped }.sortedBy { it.sortOrder }
    val sideEditorVisible = wide && editorOpen && structuralEditingEnabled &&
        characterLayoutContextV4().formFactor == CharacterFormFactorV4.TABLET_LANDSCAPE

    fun updateQuery(updated: CharacterCollectionQuery) {
        searchText = updated.searchText
        activeFiltersText = updated.activeFilterKeys.sorted().joinToString("|")
    }

    fun updateUsage(usage: CharacterInventoryUsage): List<CharacterInventoryUsage> =
        CharacterClosureState(inventoryUsage = draft.inventoryUsage)
            .withInventoryUsage(usage)
            .inventoryUsage

    fun beginAdd() {
        if (!structuralEditingEnabled) return
        editingId = null
        editorName = ""
        editorQuantity = "1"
        editorWeight = ""
        editorEquipped = false
        editorNotes = ""
        editorSpecial = false
        editorDescription = ""
        editorLocation = ""
        editorAttuned = false
        editorKindName = CharacterConsumableKind.NONE.name
        editorQuickUse = "1"
        editorCarryName = CharacterInventoryCarryState.CARRIED.name
        editorOpen = true
    }

    fun beginEdit(item: CharacterInventoryItem) {
        if (!structuralEditingEnabled) return
        val usage = usageFor(item)
        editingId = item.id.toString()
        editorName = item.name
        editorQuantity = item.quantity.toString()
        editorWeight = item.weightLb?.let(::formatDecimalInputF2).orEmpty()
        editorEquipped = item.equipped
        editorNotes = item.notes.orEmpty()
        editorSpecial = item.special
        editorDescription = item.description.orEmpty()
        editorLocation = item.location.orEmpty()
        editorAttuned = item.attuned
        editorKindName = usage.kind.name
        editorQuickUse = usage.quickUseAmount.toString()
        editorCarryName = effectiveInventoryCarryState(item, usage).name
        editorOpen = true
    }

    fun duplicate(item: CharacterInventoryItem) {
        if (!structuralEditingEnabled) return
        val newId = Uuid.random()
        val newItem = duplicateInventoryItem(item, newId, draft.items.size)
        val newUsage = duplicateInventoryUsage(usageFor(item), newId)
        onDraftChange(
            draft.copy(
                items = draft.items + newItem,
                inventoryUsage = updateUsage(newUsage),
            ),
        )
    }

    val parsedEditorQuantity = editorQuantity.toIntOrNull()
    val parsedEditorWeight = editorWeight.trim().replace(',', '.').takeIf { it.isNotEmpty() }?.toDoubleOrNull()
    val parsedEditorKind = runCatching { CharacterConsumableKind.valueOf(editorKindName) }
        .getOrDefault(CharacterConsumableKind.NONE)
    val parsedEditorCarry = runCatching { CharacterInventoryCarryState.valueOf(editorCarryName) }
        .getOrDefault(CharacterInventoryCarryState.CARRIED)
    val parsedEditorQuickUse = editorQuickUse.toIntOrNull()
    val editorValid = editorName.trim().isNotEmpty() &&
        parsedEditorQuantity != null && parsedEditorQuantity >= 0 &&
        (editorWeight.isBlank() || (parsedEditorWeight != null && parsedEditorWeight >= 0.0)) &&
        (parsedEditorKind == CharacterConsumableKind.NONE || (parsedEditorQuickUse != null && parsedEditorQuickUse > 0))

    fun applyEditor() {
        if (!editorValid) return
        val existing = editingId?.let { id -> draft.items.firstOrNull { it.id.toString() == id } }
        val id = existing?.id ?: Uuid.random()
        val item = CharacterInventoryItem(
            id = id,
            name = editorName.trim(),
            quantity = parsedEditorQuantity,
            weightLb = parsedEditorWeight,
            equipped = editorEquipped,
            notes = editorNotes.trim().takeIf { it.isNotEmpty() },
            sortOrder = existing?.sortOrder ?: draft.items.size,
            special = editorSpecial,
            description = if (editorSpecial) editorDescription.trim().takeIf { it.isNotEmpty() } else null,
            location = editorLocation.trim().takeIf { it.isNotEmpty() },
            attuned = editorSpecial && editorAttuned,
        )
        val usage = CharacterInventoryUsage(
            itemId = id,
            kind = parsedEditorKind,
            quickUseAmount = if (parsedEditorKind == CharacterConsumableKind.NONE) 1 else (parsedEditorQuickUse ?: 1),
            carryState = if (editorEquipped) CharacterInventoryCarryState.CARRIED else parsedEditorCarry,
        )
        val updatedItems = if (existing == null) {
            draft.items + item
        } else {
            draft.items.map { if (it.id == id) item else it }
        }.mapIndexed { order, current -> current.copy(sortOrder = order) }
        onDraftChange(
            draft.copy(
                items = updatedItems,
                inventoryUsage = updateUsage(usage),
            ),
        )
        editorOpen = false
    }

    fun commitSectionOrder(committedIds: List<String>) {
        if (!structuralEditingEnabled || committedIds.isEmpty()) return
        val orderedItems = draft.items.sortedWith(
            compareBy<CharacterInventoryItem> { it.sortOrder }.thenBy { it.id.toString() },
        )
        val byId = orderedItems.associateBy { it.id.toString() }
        val mergedIds = mergeCharacterReorderedSubset(
            allIds = orderedItems.map { it.id.toString() },
            reorderedSubsetIds = committedIds,
        )
        if (mergedIds == orderedItems.map { it.id.toString() }) return
        val merged = mergedIds.mapNotNull(byId::get).mapIndexed { order, current -> current.copy(sortOrder = order) }
        if (merged.size == orderedItems.size) onDraftChange(draft.copy(items = merged))
    }

    val ordinaryCanonicalIds = draft.items
        .filterNot { it.special }
        .sortedWith(compareBy<CharacterInventoryItem> { it.sortOrder }.thenBy { it.id.toString() })
        .map { it.id.toString() }
    val specialCanonicalIds = draft.items
        .filter { it.special }
        .sortedWith(compareBy<CharacterInventoryItem> { it.sortOrder }.thenBy { it.id.toString() })
        .map { it.id.toString() }
    val equipmentListState = rememberLazyListState()
    val ordinaryReorderState = rememberCharacterSpatialReorderStateV4(
        canonicalOrder = ordinaryCanonicalIds,
        onCommitOrder = ::commitSectionOrder,
        onHaptic = haptic,
        autoScrollBy = { delta -> equipmentListState.scrollBy(delta) },
    )
    val specialReorderState = rememberCharacterSpatialReorderStateV4(
        canonicalOrder = specialCanonicalIds,
        onCommitOrder = ::commitSectionOrder,
        onHaptic = haptic,
        autoScrollBy = { delta -> equipmentListState.scrollBy(delta) },
    )
    CharacterSpatialReorderAutoScrollEffectV4(ordinaryReorderState)
    CharacterSpatialReorderAutoScrollEffectV4(specialReorderState)

    Row(
        modifier = Modifier.fillMaxSize().imePadding().navigationBarsPadding(),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(if (sideEditorVisible) 8.dp else 0.dp)),
    ) {
        val listModifier = if (sideEditorVisible) Modifier.weight(1f).fillMaxHeight() else Modifier.fillMaxSize()
        LazyColumn(
            state = equipmentListState,
            modifier = listModifier.characterSpatialReorderViewportV4(ordinaryReorderState, specialReorderState),
            contentPadding = PaddingValues(
                start = appSpacingV4(if (wide) 10.dp else 5.dp),
                end = appSpacingV4(if (wide) 10.dp else 5.dp),
                top = appSpacingV4(5.dp),
                bottom = appSpacingV4(92.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        ) {
            stickyHeader(key = "equipment-tools") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(
                            horizontal = appSpacingV4(7.dp),
                            vertical = appSpacingV4(6.dp),
                        ),
                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text("Equipo", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "${draft.items.size} objetos · ${formatWeightDualF2(carriedWeight)} transportados · $attunedCount sintonizados",
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                            TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text("+ Añadir") }
                        }
                        CharacterCollectionToolbarV4(
                            itemCount = ordinaryVisible.size + specialVisible.size,
                            query = query,
                            onQueryChange = ::updateQuery,
                            filters = equipmentFiltersF2(draft, ::usageFor),
                            searchLabel = "Buscar equipo",
                        )
                    }
                }
            }

            item(key = "equipment-ordinary") {
                CharacterEquipmentSpatialSectionV4(
                    title = "Objetos",
                    items = ordinaryVisible,
                    order = ordinaryOrder,
                    onOrderChange = { ordinaryOrderName = it.name },
                    collapsed = ordinaryCollapsed,
                    onCollapsedChange = { ordinaryCollapsed = it },
                    reorderAvailable = reorderAvailableOrdinary,
                    reorderState = ordinaryReorderState,
                    queryActive = !query.isEmptyF2(),
                    wide = wide,
                    special = false,
                    selectedId = editingId,
                    usageFor = ::usageFor,
                    onEdit = ::beginEdit,
                    onQuickUse = { _, usage ->
                        onOperationalItemsChange(consumeInventoryItem(draft.items, usage))
                        haptic(CharacterHapticEventV4.RESOURCE)
                    },
                    onDuplicate = ::duplicate,
                    onDelete = { deleteId = it.id.toString() },
                    structuralEditingEnabled = structuralEditingEnabled,
                )
            }

            item(key = "equipment-special") {
                CharacterEquipmentSpatialSectionV4(
                    title = "Equipo especial",
                    items = specialVisible,
                    order = specialOrder,
                    onOrderChange = { specialOrderName = it.name },
                    collapsed = specialCollapsed,
                    onCollapsedChange = { specialCollapsed = it },
                    reorderAvailable = reorderAvailableSpecial,
                    reorderState = specialReorderState,
                    queryActive = !query.isEmptyF2(),
                    wide = wide,
                    special = true,
                    selectedId = editingId,
                    usageFor = ::usageFor,
                    onEdit = ::beginEdit,
                    onQuickUse = { _, usage ->
                        onOperationalItemsChange(consumeInventoryItem(draft.items, usage))
                        haptic(CharacterHapticEventV4.RESOURCE)
                    },
                    onDuplicate = ::duplicate,
                    onDelete = { deleteId = it.id.toString() },
                    structuralEditingEnabled = structuralEditingEnabled,
                )
            }

            item(key = "equipment-defenses") {
                EquipmentDefensesAndResourcesG1(
                    armorClass = armorClass,
                    equippedItems = equippedItems,
                    resources = equipmentResources,
                    configurations = resourceConfigurations,
                    onResourceValueChange = { resourceId, value ->
                        onResourceValueChange(resourceId, value)
                        haptic(CharacterHapticEventV4.RESOURCE)
                    },
                )
            }

            item(key = "equipment-currencies") {
                CompactCurrenciesF2(
                    currencies = draft.currencies,
                    wide = wide,
                    structuralEditingEnabled = structuralEditingEnabled,
                    onCurrenciesChange = { onDraftChange(draft.copy(currencies = it)) },
                    onAddCurrency = {
                        customCurrencyName = ""
                        customCurrencyAmount = "0"
                        addCurrencyOpen = true
                    },
                )
            }

            item(key = "equipment-valuables") {
                EquipmentValuablesG1(
                    value = successorState?.preferences?.valuablesText.orEmpty(),
                    editingEnabled = structuralEditingEnabled && settingsContext != null,
                    onValueChange = { updated ->
                        settingsContext?.let { context ->
                            val current = context.successorState
                            context.onSuccessorStateChange(
                                current.copy(preferences = current.preferences.copy(valuablesText = updated)),
                            )
                        }
                    },
                )
            }
        }

        if (sideEditorVisible) {
            EquipmentEditorPanelF3(
                editorOpen = editorOpen,
                title = if (editingId == null) "Añadir objeto" else "Editar objeto",
                name = editorName,
                quantity = editorQuantity,
                weight = editorWeight,
                equipped = editorEquipped,
                notes = editorNotes,
                special = editorSpecial,
                description = editorDescription,
                location = editorLocation,
                attuned = editorAttuned,
                kind = parsedEditorKind,
                quickUse = editorQuickUse,
                carryState = if (editorEquipped) CharacterInventoryCarryState.CARRIED else parsedEditorCarry,
                valid = editorValid,
                onBeginAdd = ::beginAdd,
                onNameChange = { editorName = it },
                onQuantityChange = { editorQuantity = sanitizeUnsignedF2(it) },
                onWeightChange = { editorWeight = sanitizeDecimalF2(it) },
                onEquippedChange = { equipped ->
                    editorEquipped = equipped
                    if (equipped) editorCarryName = CharacterInventoryCarryState.CARRIED.name
                },
                onNotesChange = { editorNotes = it },
                onSpecialChange = { requested ->
                    if (!requested && editorSpecial && (editorDescription.isNotBlank() || editorAttuned)) {
                        confirmSpecialRemoval = true
                    } else {
                        editorSpecial = requested
                    }
                },
                onDescriptionChange = { editorDescription = it },
                onLocationChange = { editorLocation = it },
                onAttunedChange = { editorAttuned = it },
                onKindChange = { editorKindName = it.name },
                onQuickUseChange = { editorQuickUse = sanitizeUnsignedF2(it) },
                onCarryStateChange = { editorCarryName = it.name },
                onDismiss = {
                    editorOpen = false
                    editingId = null
                },
                onApply = ::applyEditor,
            )
        }
    }

    if (editorOpen && !sideEditorVisible && structuralEditingEnabled) {
        EquipmentEditorF2(
            title = if (editingId == null) "Añadir objeto" else "Editar objeto",
            name = editorName,
            quantity = editorQuantity,
            weight = editorWeight,
            equipped = editorEquipped,
            notes = editorNotes,
            special = editorSpecial,
            description = editorDescription,
            location = editorLocation,
            attuned = editorAttuned,
            kind = parsedEditorKind,
            quickUse = editorQuickUse,
            carryState = if (editorEquipped) CharacterInventoryCarryState.CARRIED else parsedEditorCarry,
            valid = editorValid,
            onNameChange = { editorName = it },
            onQuantityChange = { editorQuantity = sanitizeUnsignedF2(it) },
            onWeightChange = { editorWeight = sanitizeDecimalF2(it) },
            onEquippedChange = { equipped ->
                editorEquipped = equipped
                if (equipped) editorCarryName = CharacterInventoryCarryState.CARRIED.name
            },
            onNotesChange = { editorNotes = it },
            onSpecialChange = { requested ->
                if (!requested && editorSpecial && (editorDescription.isNotBlank() || editorAttuned)) {
                    confirmSpecialRemoval = true
                } else {
                    editorSpecial = requested
                }
            },
            onDescriptionChange = { editorDescription = it },
            onLocationChange = { editorLocation = it },
            onAttunedChange = { editorAttuned = it },
            onKindChange = { editorKindName = it.name },
            onQuickUseChange = { editorQuickUse = sanitizeUnsignedF2(it) },
            onCarryStateChange = { editorCarryName = it.name },
            onDismiss = { editorOpen = false },
            onApply = ::applyEditor,
        )
    }

    if (confirmSpecialRemoval) {
        CharacterConfirmationDialog(
            title = "Convertir en equipo normal",
            message = "Al continuar se eliminarán solo la descripción especial y el estado de Sintonización. El contenedor / ubicación se conservará.",
            onDismissRequest = { confirmSpecialRemoval = false },
            onConfirm = {
                editorSpecial = false
                editorDescription = ""
                editorAttuned = false
                confirmSpecialRemoval = false
            },
            confirmLabel = "Continuar",
            destructive = true,
        )
    }

    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->
        val target = draft.items.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "objeto",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    onDraftChange(
                        draft.copy(
                            items = draft.items.filterNot { it.id == target.id }
                                .mapIndexed { order, item -> item.copy(sortOrder = order) },
                            inventoryUsage = draft.inventoryUsage.filterNot { it.itemId == target.id },
                        ),
                    )
                    if (editingId == target.id.toString()) {
                        editorOpen = false
                        editingId = null
                    }
                    deleteId = null
                },
            )
        }
    }

    if (addCurrencyOpen && structuralEditingEnabled) {
        val amount = customCurrencyAmount.toIntOrNull()
        CharacterImeSafeEditorDialog(
            title = "Añadir moneda",
            onCancel = { addCurrencyOpen = false },
            onSave = {
                val name = customCurrencyName.trim()
                if (name.isNotEmpty() && amount != null) {
                    onDraftChange(
                        draft.copy(
                            currencies = draft.currencies + CharacterCurrency(
                                key = "custom:${Uuid.random()}",
                                name = name,
                                amount = amount,
                                sortOrder = draft.currencies.size,
                                isDefault = false,
                            ),
                        ),
                    )
                    addCurrencyOpen = false
                }
            },
            saveLabel = "Añadir",
            saveEnabled = customCurrencyName.trim().isNotEmpty() && amount != null,
        ) {
            OutlinedTextField(
                value = customCurrencyName,
                onValueChange = { customCurrencyName = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            OutlinedTextField(
                value = customCurrencyAmount,
                onValueChange = { customCurrencyAmount = sanitizeSignedF2(it) },
                label = { Text("Cantidad") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            CharacterInlineValidationMessage(
                when {
                    customCurrencyName.trim().isEmpty() -> "El nombre no puede quedar vacío."
                    amount == null -> "La cantidad debe ser un número entero."
                    else -> null
                },
            )
        }
    }
}

private fun CharacterCollectionQuery.isEmptyF2(): Boolean =
    searchText.isBlank() && activeFilterKeys.isEmpty()

private fun equipmentFiltersF2(
    draft: CharacterEquipmentDraftV4,
    usageFor: (CharacterInventoryItem) -> CharacterInventoryUsage,
): List<CharacterFilterOptionV4> = listOf(
    CharacterFilterOptionV4(CharacterInventoryFilterKey.CARRIED.key, "Transportado"),
    CharacterFilterOptionV4(CharacterInventoryFilterKey.STORED.key, "Guardado"),
    CharacterFilterOptionV4(CharacterInventoryFilterKey.EQUIPPED.key, "Equipado"),
    CharacterFilterOptionV4(CharacterInventoryFilterKey.SPECIAL.key, "Especial"),
    CharacterFilterOptionV4(CharacterInventoryFilterKey.CONSUMABLE.key, "Consumible"),
    CharacterFilterOptionV4(CharacterInventoryFilterKey.AMMUNITION.key, "Munición"),
    CharacterFilterOptionV4(CharacterInventoryFilterKey.LOCATED.key, "Con ubicación"),
).map { option ->
    val count = draft.items.count { item ->
        when (option.key) {
            CharacterInventoryFilterKey.CARRIED.key ->
                effectiveInventoryCarryState(item, usageFor(item)) == CharacterInventoryCarryState.CARRIED
            CharacterInventoryFilterKey.STORED.key ->
                effectiveInventoryCarryState(item, usageFor(item)) == CharacterInventoryCarryState.STORED
            CharacterInventoryFilterKey.EQUIPPED.key -> item.equipped
            CharacterInventoryFilterKey.SPECIAL.key -> item.special
            CharacterInventoryFilterKey.CONSUMABLE.key -> usageFor(item).kind == CharacterConsumableKind.CONSUMABLE
            CharacterInventoryFilterKey.AMMUNITION.key -> usageFor(item).kind == CharacterConsumableKind.AMMUNITION
            CharacterInventoryFilterKey.LOCATED.key -> !item.location.isNullOrBlank()
            else -> false
        }
    }
    option.copy(count = count)
}

@Composable
private fun EquipmentEditorPanelF3(
    editorOpen: Boolean,
    title: String,
    name: String,
    quantity: String,
    weight: String,
    equipped: Boolean,
    notes: String,
    special: Boolean,
    description: String,
    location: String,
    attuned: Boolean,
    kind: CharacterConsumableKind,
    quickUse: String,
    carryState: CharacterInventoryCarryState,
    valid: Boolean,
    onBeginAdd: () -> Unit,
    onNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onEquippedChange: (Boolean) -> Unit,
    onNotesChange: (String) -> Unit,
    onSpecialChange: (Boolean) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onAttunedChange: (Boolean) -> Unit,
    onKindChange: (CharacterConsumableKind) -> Unit,
    onQuickUseChange: (String) -> Unit,
    onCarryStateChange: (CharacterInventoryCarryState) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    Card(
        modifier = Modifier
            .widthIn(min = 280.dp, max = 360.dp)
            .fillMaxHeight(),
    ) {
        if (!editorOpen) {
            Column(
                modifier = Modifier.fillMaxSize().padding(appSpacingV4(14.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Editor de equipo", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Selecciona un objeto de la lista para editarlo sin perder tu posición, búsqueda ni filtros.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Button(onClick = onBeginAdd, modifier = Modifier.padding(top = appSpacingV4(10.dp))) {
                    Text("+ Añadir objeto")
                }
            }
            return@Card
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = appSpacingV4(10.dp), vertical = appSpacingV4(8.dp))) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                TextButton(onClick = onDismiss) { Text("Cerrar") }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp))) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = onQuantityChange,
                        label = { Text("Cantidad") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = onWeightChange,
                        label = { Text("Peso/u. lb") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = equipped, onCheckedChange = onEquippedChange)
                    Text("Equipado")
                    Checkbox(checked = special, onCheckedChange = onSpecialChange)
                    Text("Especial")
                }
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = { Text("Contenedor / ubicación") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                EnumDropdownF2(
                    label = "Disponibilidad",
                    current = if (equipped) "Transportado (equipado)" else carryLabelF2(carryState),
                    options = CharacterInventoryCarryState.entries.map { it.name to carryLabelF2(it) },
                    enabled = !equipped,
                    onSelect = { onCarryStateChange(CharacterInventoryCarryState.valueOf(it)) },
                )
                EnumDropdownF2(
                    label = "Tipo de consumo",
                    current = consumableLabelF2(kind),
                    options = CharacterConsumableKind.entries.map { it.name to consumableLabelF2(it) },
                    onSelect = { onKindChange(CharacterConsumableKind.valueOf(it)) },
                )
                if (kind != CharacterConsumableKind.NONE) {
                    OutlinedTextField(
                        value = quickUse,
                        onValueChange = onQuickUseChange,
                        label = { Text("Descuento por uso") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                }
                if (special) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = attuned, onCheckedChange = onAttunedChange)
                        Text("Sintonizado")
                    }
                    OutlinedTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Descripción especial") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = characterCompactTextAreaMinLinesV4(2),
                        maxLines = 5,
                    )
                }
                OutlinedTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    label = { Text("Notas") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = characterCompactTextAreaMinLinesV4(2),
                    maxLines = 4,
                )
                CharacterInlineValidationMessage(
                    when {
                        name.trim().isEmpty() -> "El nombre no puede quedar vacío."
                        quantity.toIntOrNull() == null || quantity.toInt() < 0 -> "La cantidad debe ser un entero igual o mayor que 0."
                        weight.isNotBlank() && (weight.replace(',', '.').toDoubleOrNull() == null || weight.replace(',', '.').toDouble() < 0.0) -> "El peso debe ser un número igual o mayor que 0."
                        kind != CharacterConsumableKind.NONE && (quickUse.toIntOrNull() == null || quickUse.toInt() <= 0) -> "El uso rápido debe consumir al menos 1 unidad."
                        else -> null
                    },
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = appSpacingV4(6.dp)),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
                Button(onClick = onApply, enabled = valid) { Text("Aplicar") }
            }
        }
    }
}

@Composable
private fun EquipmentEditorF2(
    title: String,
    name: String,
    quantity: String,
    weight: String,
    equipped: Boolean,
    notes: String,
    special: Boolean,
    description: String,
    location: String,
    attuned: Boolean,
    kind: CharacterConsumableKind,
    quickUse: String,
    carryState: CharacterInventoryCarryState,
    valid: Boolean,
    onNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onEquippedChange: (Boolean) -> Unit,
    onNotesChange: (String) -> Unit,
    onSpecialChange: (Boolean) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onAttunedChange: (Boolean) -> Unit,
    onKindChange: (CharacterConsumableKind) -> Unit,
    onQuickUseChange: (String) -> Unit,
    onCarryStateChange: (CharacterInventoryCarryState) -> Unit,
    onDismiss: () -> Unit,
    onApply: () -> Unit,
) {
    CharacterImeSafeEditorDialog(
        title = title,
        onCancel = onDismiss,
        onSave = onApply,
        saveEnabled = valid,
    ) {
        OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Row(horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp))) {
            OutlinedTextField(
                value = quantity,
                onValueChange = onQuantityChange,
                label = { Text("Cantidad") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                label = { Text("Peso/u. lb") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = equipped, onCheckedChange = onEquippedChange)
            Text("Equipado")
            Checkbox(checked = special, onCheckedChange = onSpecialChange)
            Text("Equipo especial")
        }
        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("Contenedor / ubicación (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("Ej.: Mochila, cinturón, carcaj, Bag of Holding, cofre, posada o ubicación corporal.") },
        )
        EnumDropdownF2(
            label = "Disponibilidad",
            current = if (equipped) "Transportado (equipado)" else carryLabelF2(carryState),
            options = CharacterInventoryCarryState.entries.map { it.name to carryLabelF2(it) },
            enabled = !equipped,
            onSelect = { onCarryStateChange(CharacterInventoryCarryState.valueOf(it)) },
        )
        EnumDropdownF2(
            label = "Tipo de consumo",
            current = consumableLabelF2(kind),
            options = CharacterConsumableKind.entries.map { it.name to consumableLabelF2(it) },
            onSelect = { onKindChange(CharacterConsumableKind.valueOf(it)) },
        )
        if (kind != CharacterConsumableKind.NONE) {
            OutlinedTextField(
                value = quickUse,
                onValueChange = onQuickUseChange,
                label = { Text("Descuento por uso") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        if (special) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = attuned, onCheckedChange = onAttunedChange)
                Text("Sintonizado")
            }
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Descripción especial") },
                modifier = Modifier.fillMaxWidth(),
                minLines = characterCompactTextAreaMinLinesV4(2),
                maxLines = 5,
            )
        }
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth(),
            minLines = characterCompactTextAreaMinLinesV4(2),
            maxLines = 4,
        )
        CharacterInlineValidationMessage(
            when {
                name.trim().isEmpty() -> "El nombre no puede quedar vacío."
                quantity.toIntOrNull() == null || quantity.toInt() < 0 -> "La cantidad debe ser un entero igual o mayor que 0."
                weight.isNotBlank() && (weight.replace(',', '.').toDoubleOrNull() == null || weight.replace(',', '.').toDouble() < 0.0) -> "El peso debe ser un número igual o mayor que 0."
                kind != CharacterConsumableKind.NONE && (quickUse.toIntOrNull() == null || quickUse.toInt() <= 0) -> "El uso rápido debe consumir al menos 1 unidad."
                else -> null
            },
        )
    }
}

@Composable
private fun EnumDropdownF2(
    label: String,
    current: String,
    options: List<Pair<String, String>>,
    enabled: Boolean = true,
    onSelect: (String) -> Unit,
) {
    var expanded by rememberSaveable(label) { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        androidx.compose.foundation.layout.Box {
            OutlinedButton(
                onClick = { if (enabled) expanded = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
            ) { Text(current) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (key, text) ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            onSelect(key)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun EquipmentDefensesAndResourcesG1(
    armorClass: Int,
    equippedItems: List<CharacterInventoryItem>,
    resources: List<CharacterResource>,
    configurations: Map<Uuid, CharacterResourceSuccessorConfiguration>,
    onResourceValueChange: (Uuid, Int) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(4.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Text("Defensas y recursos", style = MaterialTheme.typography.titleSmall)
            val equippedText = equippedItems.joinToString(", ") { item ->
                if (item.quantity > 1) "${item.name} ×${item.quantity}" else item.name
            }.ifBlank { "—" }
            Text(
                "CA $armorClass · Equipado: $equippedText",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            CharacterHelpV4(
                "Equipo proyecta la misma CA y los mismos objetos marcados como Equipado que General/Defensas. La ficha aún no clasifica armadura y escudo como categorías estructuradas separadas.",
            )
            if (resources.isNotEmpty()) {
                Text("Recursos de Equipo", style = MaterialTheme.typography.labelMedium)
                resources.forEach { resource ->
                    val configuration = configurations[resource.id] ?: return@forEach
                    EquipmentResourceRowG1(
                        resource = resource,
                        configuration = configuration,
                        onValueChange = { value -> onResourceValueChange(resource.id, value) },
                    )
                }
            }
        }
    }
}

@Composable
private fun EquipmentResourceRowG1(
    resource: CharacterResource,
    configuration: CharacterResourceSuccessorConfiguration,
    onValueChange: (Int) -> Unit,
) {
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
                EquipmentToggleG1(
                    label = if (active) "Activo" else "Inactivo",
                    selected = active,
                    onClick = { onValueChange(if (active) 0 else 1) },
                )
            }
            CharacterTrackableValueKind.COUNTER,
            CharacterTrackableValueKind.CURRENT_MAX,
            -> {
                val maximum = if (configuration.valueKind == CharacterTrackableValueKind.CURRENT_MAX) resource.maxValue else null
                EquipmentStepG1("−", enabled = resource.currentValue > 0) {
                    onValueChange((resource.currentValue - 1).coerceAtLeast(0))
                }
                Text(
                    maximum?.let { "${resource.currentValue}/$it" } ?: resource.currentValue.toString(),
                    style = MaterialTheme.typography.labelLarge,
                )
                EquipmentStepG1("+", enabled = maximum?.let { resource.currentValue < it } ?: true) {
                    val next = resource.currentValue + 1
                    onValueChange(maximum?.let { next.coerceAtMost(it) } ?: next)
                }
            }
        }
    }
}

@Composable
private fun EquipmentToggleG1(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
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
private fun EquipmentStepG1(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
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

@Composable
private fun EquipmentValuablesG1(
    value: String,
    editingEnabled: Boolean,
    onValueChange: (String) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(4.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Text("Gemas / arte", style = MaterialTheme.typography.titleSmall)
            OutlinedTextField(
                value = value,
                onValueChange = { if (editingEnabled) onValueChange(it) },
                readOnly = !editingEnabled,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Gemas, obras de arte, joyas u otros valores") },
                minLines = 1,
                maxLines = 4,
            )
        }
    }
}

@Composable
private fun CompactCurrenciesF2(
    currencies: List<CharacterCurrency>,
    wide: Boolean,
    structuralEditingEnabled: Boolean,
    onCurrenciesChange: (List<CharacterCurrency>) -> Unit,
    onAddCurrency: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(4.dp)),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Monedas", style = MaterialTheme.typography.titleSmall)
                TextButton(onClick = onAddCurrency, enabled = structuralEditingEnabled) { Text("+ Añadir") }
            }
            if (currencies.isEmpty()) {
                Text("Sin monedas registradas.", style = MaterialTheme.typography.bodySmall)
            } else {
                val columns = if (wide) 6 else 3
                currencies.chunked(columns).forEach { rowCurrencies ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                        verticalAlignment = Alignment.Top,
                    ) {
                        rowCurrencies.forEach { currency ->
                            CompactCurrencyCellF2(
                                currency = currency,
                                onAmountChange = { amount ->
                                    onCurrenciesChange(
                                        currencies.map { if (it.key == currency.key) it.copy(amount = amount) else it },
                                    )
                                },
                                onDelete = if (currency.isDefault || !structuralEditingEnabled) null else {
                                    { onCurrenciesChange(currencies.filterNot { it.key == currency.key }) }
                                },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(columns - rowCurrencies.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompactCurrencyCellF2(
    currency: CharacterCurrency,
    onAmountChange: (Int) -> Unit,
    onDelete: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable(currency.key, currency.amount) { mutableStateOf(currency.amount.toString()) }
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(3.dp)),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(currency.name, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                BasicTextField(
                    value = text,
                    onValueChange = { raw ->
                        text = sanitizeSignedF2(raw)
                        text.toIntOrNull()?.let(onAmountChange)
                    },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 26.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                )
            }
            if (onDelete != null) {
                StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar moneda ${currency.name}")
            }
        }
    }
}

private fun carryLabelF2(state: CharacterInventoryCarryState): String = when (state) {
    CharacterInventoryCarryState.CARRIED -> "Transportado"
    CharacterInventoryCarryState.STORED -> "Guardado"
}

private fun consumableLabelF2(kind: CharacterConsumableKind): String = when (kind) {
    CharacterConsumableKind.NONE -> "No consume cantidad"
    CharacterConsumableKind.CONSUMABLE -> "Consumible"
    CharacterConsumableKind.AMMUNITION -> "Munición"
}

private fun sanitizeUnsignedF2(raw: String): String = raw.filter(Char::isDigit)

private fun sanitizeSignedF2(raw: String): String {
    if (raw.isBlank()) return ""
    val sign = raw.firstOrNull()?.takeIf { it == '+' || it == '-' }?.toString().orEmpty()
    val digits = raw.drop(if (sign.isEmpty()) 0 else 1).filter(Char::isDigit)
    return sign + digits
}

private fun sanitizeDecimalF2(raw: String): String {
    val normalized = raw.replace(',', '.')
    var separatorUsed = false
    return buildString {
        normalized.forEach { char ->
            when {
                char.isDigit() -> append(char)
                char == '.' && !separatorUsed -> {
                    append('.')
                    separatorUsed = true
                }
            }
        }
    }
}

private fun formatDecimalInputF2(value: Double): String = formatCompactF2(value, decimalComma = false)

private fun formatWeightDualF2(lb: Double): String =
    "${formatCompactF2(lb)} lb (${formatCompactF2(lb * 0.5)} kg)"

private fun formatCompactF2(value: Double, decimalComma: Boolean = true): String {
    val rounded = when {
        abs(value - value.toLong()) < 0.000001 -> value.toLong().toString()
        else -> "%.3f".format(Locale.US, value).trimEnd('0').trimEnd('.')
    }
    return if (decimalComma) rounded.replace('.', ',') else rounded
}
