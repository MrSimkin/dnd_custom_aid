package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_COMPANION_ACTIVE_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_COMPANION_FAVORITE_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCollectionQuery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCompanion
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.applyCharacterReorderResult
import io.github.mrsimkin.dndcustomaid.shared.character.characterCompanionKindFilterKey
import io.github.mrsimkin.dndcustomaid.shared.character.characterCompanionSourceFilterKey
import io.github.mrsimkin.dndcustomaid.shared.character.duplicateCharacterCompanion
import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess
import io.github.mrsimkin.dndcustomaid.shared.character.isCharacterStructuralEditingEnabled
import io.github.mrsimkin.dndcustomaid.shared.character.nextCharacterCompanionSortOrder
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterCompanionOrders
import io.github.mrsimkin.dndcustomaid.shared.character.presentCharacterCompanions
import io.github.mrsimkin.dndcustomaid.shared.character.suggestedCharacterModules
import io.github.mrsimkin.dndcustomaid.shared.character.withQuickAccess
import kotlin.uuid.Uuid

private const val H3_FILTER_SEPARATOR = "\u001E"

@Composable
internal fun CharacterCompanionsModuleV4(
    companions: List<CharacterCompanion>,
    classes: List<CharacterClassLevel>,
    closureState: CharacterClosureState,
    persistedCompanionIds: Set<Uuid>,
    onCompanionsChange: (List<CharacterCompanion>) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
    hapticsEnabled: Boolean,
) {
    var searchText by rememberSaveable("h3-companions-search") { mutableStateOf("") }
    var filtersText by rememberSaveable("h3-companions-filters") { mutableStateOf("") }
    var orderName by rememberSaveable("h3-companions-order") {
        mutableStateOf(CharacterPresentationOrder.MANUAL.name)
    }
    var editorOpen by rememberSaveable("h3-companions-editor-open") { mutableStateOf(false) }
    var editingId by rememberSaveable("h3-companions-editor-id") { mutableStateOf<String?>(null) }
    var editorName by rememberSaveable("h3-companions-name") { mutableStateOf("") }
    var editorLinkedClassId by rememberSaveable("h3-companions-class") { mutableStateOf("") }
    var editorKind by rememberSaveable("h3-companions-kind") { mutableStateOf("") }
    var editorSource by rememberSaveable("h3-companions-source") { mutableStateOf("") }
    var editorArmorClass by rememberSaveable("h3-companions-ac") { mutableStateOf("") }
    var editorMaxHp by rememberSaveable("h3-companions-max-hp") { mutableStateOf("") }
    var editorCurrentHp by rememberSaveable("h3-companions-current-hp") { mutableStateOf("") }
    var editorTempHp by rememberSaveable("h3-companions-temp-hp") { mutableStateOf("0") }
    var editorSpeed by rememberSaveable("h3-companions-speed") { mutableStateOf("") }
    var editorAbilitySummary by rememberSaveable("h3-companions-abilities") { mutableStateOf("") }
    var editorSensesProficiencies by rememberSaveable("h3-companions-senses") { mutableStateOf("") }
    var editorTraitsActions by rememberSaveable("h3-companions-traits") { mutableStateOf("") }
    var editorNotes by rememberSaveable("h3-companions-notes") { mutableStateOf("") }
    var editorActive by rememberSaveable("h3-companions-active") { mutableStateOf(true) }
    var deleteId by rememberSaveable("h3-companions-delete") { mutableStateOf<String?>(null) }

    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)
    val structuralEditingEnabled = isCharacterStructuralEditingEnabled(closureState.tableModeEnabled)
    val order = runCatching { CharacterPresentationOrder.valueOf(orderName) }
        .getOrDefault(CharacterPresentationOrder.MANUAL)
    val activeFilters = decodeFilterSetH3(filtersText)
    val query = CharacterCollectionQuery(searchText = searchText, activeFilterKeys = activeFilters)
    val favoritePredicate: (CharacterCompanion) -> Boolean = { companion ->
        closureState.hasQuickAccess(CharacterQuickAccessKind.COMPANION, companion.id)
    }
    val visible = presentCharacterCompanions(companions, order, query, favoritePredicate)
    val canReorder = structuralEditingEnabled && order == CharacterPresentationOrder.MANUAL &&
        query.searchText.isBlank() && query.activeFilterKeys.isEmpty()
    val selectedEditingId = editingId?.takeIf { editorOpen }

    fun updateQuery(updated: CharacterCollectionQuery) {
        searchText = updated.searchText
        filtersText = encodeFilterSetH3(updated.activeFilterKeys)
    }

    fun updateCompanions(updated: List<CharacterCompanion>) {
        onCompanionsChange(normalizeCharacterCompanionOrders(updated))
    }

    fun suggestedClassId(): Uuid? = classes.firstOrNull { classLevel ->
        CharacterModuleKind.COMPANIONS in suggestedCharacterModules(listOf(classLevel))
    }?.id

    fun resetEditor() {
        editingId = null
        editorName = ""
        editorLinkedClassId = suggestedClassId()?.toString().orEmpty()
        editorKind = ""
        editorSource = ""
        editorArmorClass = ""
        editorMaxHp = ""
        editorCurrentHp = ""
        editorTempHp = "0"
        editorSpeed = ""
        editorAbilitySummary = ""
        editorSensesProficiencies = ""
        editorTraitsActions = ""
        editorNotes = ""
        editorActive = true
    }

    fun beginAdd() {
        if (!structuralEditingEnabled) return
        resetEditor()
        editorOpen = true
    }

    fun beginEdit(companion: CharacterCompanion) {
        if (!structuralEditingEnabled) return
        editingId = companion.id.toString()
        editorName = companion.name
        editorLinkedClassId = companion.linkedClassId?.toString().orEmpty()
        editorKind = companion.kind
        editorSource = companion.source.orEmpty()
        editorArmorClass = companion.armorClass?.toString().orEmpty()
        editorMaxHp = companion.maxHp?.toString().orEmpty()
        editorCurrentHp = companion.currentHp?.toString().orEmpty()
        editorTempHp = companion.tempHp.toString()
        editorSpeed = companion.speed.orEmpty()
        editorAbilitySummary = companion.abilitySummary.orEmpty()
        editorSensesProficiencies = companion.sensesProficiencies.orEmpty()
        editorTraitsActions = companion.traitsActions
        editorNotes = companion.notes.orEmpty()
        editorActive = companion.active
        editorOpen = true
    }

    fun duplicate(companion: CharacterCompanion) {
        if (!structuralEditingEnabled) return
        updateCompanions(
            companions + duplicateCharacterCompanion(
                source = companion,
                newId = Uuid.random(),
                sortOrder = nextCharacterCompanionSortOrder(companions),
            ),
        )
    }

    val validClassIds = classes.mapTo(mutableSetOf()) { it.id.toString() }
    val parsedLinkedClassId = editorLinkedClassId
        .takeIf { it in validClassIds }
        ?.let(Uuid::parse)
    val armorClassValid = editorArmorClass.isBlank() || editorArmorClass.toIntOrNull()?.let { it >= 0 } == true
    val maxHpValid = editorMaxHp.isBlank() || editorMaxHp.toIntOrNull()?.let { it >= 0 } == true
    val currentHpValid = editorCurrentHp.isBlank() || editorCurrentHp.toIntOrNull()?.let { it >= 0 } == true
    val tempHpValid = editorTempHp.isBlank() || editorTempHp.toIntOrNull()?.let { it >= 0 } == true
    val editorCanSave = editorName.trim().isNotEmpty() &&
        armorClassValid && maxHpValid && currentHpValid && tempHpValid

    fun optionalNonNegative(raw: String): Int? = raw.trim().takeIf(String::isNotEmpty)?.toIntOrNull()

    fun applyEditor() {
        if (!editorCanSave) return
        val existing = editingId?.let { id -> companions.firstOrNull { it.id.toString() == id } }
        val edited = CharacterCompanion(
            id = existing?.id ?: Uuid.random(),
            linkedClassId = parsedLinkedClassId,
            name = editorName.trim(),
            kind = editorKind.trim(),
            source = editorSource.trim().takeIf(String::isNotEmpty),
            armorClass = optionalNonNegative(editorArmorClass),
            maxHp = optionalNonNegative(editorMaxHp),
            currentHp = optionalNonNegative(editorCurrentHp),
            tempHp = editorTempHp.trim().takeIf(String::isNotEmpty)?.toIntOrNull() ?: 0,
            speed = editorSpeed.trim().takeIf(String::isNotEmpty),
            abilitySummary = editorAbilitySummary.trim().takeIf(String::isNotEmpty),
            sensesProficiencies = editorSensesProficiencies.trim().takeIf(String::isNotEmpty),
            traitsActions = editorTraitsActions.trim(),
            notes = editorNotes.trim().takeIf(String::isNotEmpty),
            active = editorActive,
            sortOrder = existing?.sortOrder ?: nextCharacterCompanionSortOrder(companions),
        )
        val updated = if (existing == null) {
            companions + edited
        } else {
            companions.map { companion -> if (companion.id == existing.id) edited else companion }
        }
        updateCompanions(updated)
        editorOpen = false
    }

    fun commitCompanionReorder(proposedIds: List<String>) {
        if (!canReorder) return
        val normalized = normalizeCharacterCompanionOrders(companions)
        val canonicalIds = normalized.map { it.id.toString() }
        val finalIds = applyCharacterReorderResult(canonicalIds, proposedIds)
        if (finalIds == canonicalIds) return
        val byId = normalized.associateBy { it.id.toString() }
        val reordered = finalIds.mapNotNull(byId::get)
        if (reordered.size != normalized.size) return
        onCompanionsChange(reordered.mapIndexed { index, companion -> companion.copy(sortOrder = index) })
    }

    val collection: @Composable (Modifier) -> Unit = { modifier ->
        CompanionCollectionH3(
            modifier = modifier,
            companions = companions,
            classes = classes,
            closureState = closureState,
            persistedCompanionIds = persistedCompanionIds,
            visible = visible,
            query = query,
            order = order,
            canReorder = canReorder,
            structuralEditingEnabled = structuralEditingEnabled,
            selectedEditingId = selectedEditingId,
            onQueryChange = ::updateQuery,
            onOrderChange = { orderName = it.name },
            onAdd = ::beginAdd,
            onEdit = ::beginEdit,
            onDuplicate = ::duplicate,
            onDelete = { deleteId = it.id.toString() },
            onCommitReorder = ::commitCompanionReorder,
            onFavoriteChange = { companion, enabled ->
                onClosureStateChange(
                    closureState.withQuickAccess(
                        CharacterQuickAccessKind.COMPANION,
                        companion.id,
                        enabled,
                    ),
                )
            },
            onHaptic = haptic,
        )
    }

    val sideEditorVisible = wide && editorOpen && structuralEditingEnabled &&
        characterLayoutContextV4().formFactor == CharacterFormFactorV4.TABLET_LANDSCAPE

    if (sideEditorVisible) {
        Row(
            modifier = Modifier.fillMaxSize().imePadding().navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
        ) {
            collection(Modifier.weight(1f))
            Surface(
                modifier = Modifier.width(420.dp).fillMaxHeight().padding(
                    top = appSpacingV4(5.dp),
                    end = appSpacingV4(8.dp),
                    bottom = appSpacingV4(8.dp),
                ),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                if (editorOpen && structuralEditingEnabled) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(appSpacingV4(10.dp)),
                        verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    if (editingId == null) "Añadir compañero" else "Editar compañero",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                TextButton(onClick = { editorOpen = false }) { Text("Cerrar") }
                            }
                        }
                        item {
                            CompanionEditorFieldsH3(
                                classes = classes,
                                name = editorName,
                                linkedClassId = parsedLinkedClassId,
                                kind = editorKind,
                                source = editorSource,
                                armorClass = editorArmorClass,
                                maxHp = editorMaxHp,
                                currentHp = editorCurrentHp,
                                tempHp = editorTempHp,
                                speed = editorSpeed,
                                abilitySummary = editorAbilitySummary,
                                sensesProficiencies = editorSensesProficiencies,
                                traitsActions = editorTraitsActions,
                                notes = editorNotes,
                                active = editorActive,
                                armorClassValid = armorClassValid,
                                maxHpValid = maxHpValid,
                                currentHpValid = currentHpValid,
                                tempHpValid = tempHpValid,
                                onNameChange = { editorName = it },
                                onLinkedClassIdChange = { editorLinkedClassId = it?.toString().orEmpty() },
                                onKindChange = { editorKind = it },
                                onSourceChange = { editorSource = it },
                                onArmorClassChange = { editorArmorClass = sanitizeUnsignedIntH3(it) },
                                onMaxHpChange = { editorMaxHp = sanitizeUnsignedIntH3(it) },
                                onCurrentHpChange = { editorCurrentHp = sanitizeUnsignedIntH3(it) },
                                onTempHpChange = { editorTempHp = sanitizeUnsignedIntH3(it) },
                                onSpeedChange = { editorSpeed = it },
                                onAbilitySummaryChange = { editorAbilitySummary = it },
                                onSensesProficienciesChange = { editorSensesProficiencies = it },
                                onTraitsActionsChange = { editorTraitsActions = it },
                                onNotesChange = { editorNotes = it },
                                onActiveChange = { editorActive = it },
                            )
                        }
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                OutlinedButton(onClick = { editorOpen = false }) { Text("Cancelar") }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(onClick = ::applyEditor, enabled = editorCanSave) { Text("Aplicar") }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(appSpacingV4(16.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("Compañeros", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Selecciona un compañero para editarlo.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text("+ Añadir") }
                    }
                }
            }
        }
    } else {
        collection(Modifier.fillMaxSize())
        if (editorOpen && structuralEditingEnabled) {
            CharacterImeSafeEditorDialog(
                title = if (editingId == null) "Añadir compañero" else "Editar compañero",
                onCancel = { editorOpen = false },
                onSave = ::applyEditor,
                saveLabel = "Aplicar",
                saveEnabled = editorCanSave,
            ) {
                CompanionEditorFieldsH3(
                    classes = classes,
                    name = editorName,
                    linkedClassId = parsedLinkedClassId,
                    kind = editorKind,
                    source = editorSource,
                    armorClass = editorArmorClass,
                    maxHp = editorMaxHp,
                    currentHp = editorCurrentHp,
                    tempHp = editorTempHp,
                    speed = editorSpeed,
                    abilitySummary = editorAbilitySummary,
                    sensesProficiencies = editorSensesProficiencies,
                    traitsActions = editorTraitsActions,
                    notes = editorNotes,
                    active = editorActive,
                    armorClassValid = armorClassValid,
                    maxHpValid = maxHpValid,
                    currentHpValid = currentHpValid,
                    tempHpValid = tempHpValid,
                    onNameChange = { editorName = it },
                    onLinkedClassIdChange = { editorLinkedClassId = it?.toString().orEmpty() },
                    onKindChange = { editorKind = it },
                    onSourceChange = { editorSource = it },
                    onArmorClassChange = { editorArmorClass = sanitizeUnsignedIntH3(it) },
                    onMaxHpChange = { editorMaxHp = sanitizeUnsignedIntH3(it) },
                    onCurrentHpChange = { editorCurrentHp = sanitizeUnsignedIntH3(it) },
                    onTempHpChange = { editorTempHp = sanitizeUnsignedIntH3(it) },
                    onSpeedChange = { editorSpeed = it },
                    onAbilitySummaryChange = { editorAbilitySummary = it },
                    onSensesProficienciesChange = { editorSensesProficiencies = it },
                    onTraitsActionsChange = { editorTraitsActions = it },
                    onNotesChange = { editorNotes = it },
                    onActiveChange = { editorActive = it },
                )
            }
        }
    }

    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->
        val target = companions.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = "compañero",
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    updateCompanions(companions.filterNot { it.id == target.id })
                    if (editingId == target.id.toString()) editorOpen = false
                    deleteId = null
                    haptic(CharacterHapticEventV4.DESTRUCTIVE)
                },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CompanionCollectionH3(
    modifier: Modifier,
    companions: List<CharacterCompanion>,
    classes: List<CharacterClassLevel>,
    closureState: CharacterClosureState,
    persistedCompanionIds: Set<Uuid>,
    visible: List<CharacterCompanion>,
    query: CharacterCollectionQuery,
    order: CharacterPresentationOrder,
    canReorder: Boolean,
    structuralEditingEnabled: Boolean,
    selectedEditingId: String?,
    onQueryChange: (CharacterCollectionQuery) -> Unit,
    onOrderChange: (CharacterPresentationOrder) -> Unit,
    onAdd: () -> Unit,
    onEdit: (CharacterCompanion) -> Unit,
    onDuplicate: (CharacterCompanion) -> Unit,
    onDelete: (CharacterCompanion) -> Unit,
    onCommitReorder: (List<String>) -> Unit,
    onFavoriteChange: (CharacterCompanion, Boolean) -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
) {
    val classById = remember(classes) { classes.associateBy { it.id } }
    val favoriteCount = companions.count { companion ->
        closureState.hasQuickAccess(CharacterQuickAccessKind.COMPANION, companion.id)
    }
    val sourceFilters = companions
        .mapNotNull(CharacterCompanion::source)
        .map(String::trim)
        .filter(String::isNotEmpty)
        .distinctBy { it.lowercase() }
        .sortedBy { it.lowercase() }
        .map { source ->
            val key = characterCompanionSourceFilterKey(source)
            CharacterFilterOptionV4(
                key = key,
                label = source,
                count = companions.count { characterCompanionSourceFilterKey(it.source) == key },
            )
        }
    val kindFilters = companions
        .map(CharacterCompanion::kind)
        .map(String::trim)
        .filter(String::isNotEmpty)
        .distinctBy { it.lowercase() }
        .sortedBy { it.lowercase() }
        .map { kind ->
            val key = characterCompanionKindFilterKey(kind)
            CharacterFilterOptionV4(
                key = key,
                label = kind,
                count = companions.count { characterCompanionKindFilterKey(it.kind) == key },
            )
        }
    val filters = buildList {
        add(CharacterFilterOptionV4(CHARACTER_COMPANION_ACTIVE_FILTER_KEY, "Activos", companions.count(CharacterCompanion::active)))
        add(CharacterFilterOptionV4(CHARACTER_COMPANION_FAVORITE_FILTER_KEY, "Favoritos", favoriteCount))
        addAll(kindFilters)
        addAll(sourceFilters)
    }

    val listState = rememberLazyListState()

    val keepCollectionToolsSticky =

        characterLayoutContextV4().verticalSpace == CharacterVerticalSpaceV4.COMFORTABLE
    val reorderCoordinator = rememberCharacterReorderCoordinatorV4()
    val normalizedCompanions = normalizeCharacterCompanionOrders(companions)
    val canonicalIds = normalizedCompanions.map { it.id.toString() }
    val companionById = companions.associateBy { it.id.toString() }
    val sessionEnabled = canReorder && canonicalIds.size > 1
    val reorderSession = rememberCharacterReorderSessionV4(
        sessionKey = "companions",
        canonicalOrder = canonicalIds,
        enabled = sessionEnabled,
        coordinator = reorderCoordinator,
        onCommitOrder = onCommitReorder,
        onHaptic = onHaptic,
        autoScrollBy = { delta -> listState.scrollBy(delta) },
    )
    CharacterReorderSessionAutoScrollEffectV4(reorderSession)

    val layoutCompanions = if (canReorder) {
        reorderSession.previewOrder.mapNotNull(companionById::get)
    } else {
        visible
    }

    CharacterReorderOverlayHostV4(
        session = reorderSession,
        modifier = modifier,
        liftedContent = { draggedId ->
            companionById[draggedId]?.let { companion ->
                CompanionRowH3(
                    companion = companion,
                    linkedClass = companion.linkedClassId?.let(classById::get),
                    favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.COMPANION, companion.id),
                    favoriteEnabled = false,
                    reorderSession = null,
                    structuralEditingEnabled = false,
                    selected = selectedEditingId == companion.id.toString(),
                    onFavoriteChange = {},
                    onEdit = {},
                    onDuplicate = {},
                    onDelete = {},
                    lifted = true,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        },
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .characterReorderSessionViewportV4(reorderSession),
            contentPadding = PaddingValues(
                start = appSpacingV4(6.dp),
                end = appSpacingV4(6.dp),
                top = appSpacingV4(5.dp),
                bottom = appSpacingV4(88.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        ) {
            characterAdaptiveStickyHeaderV4(sticky = keepCollectionToolsSticky, key = "h3-companions-tools") {
                CharacterCollectionToolbarV4(
                    itemCount = visible.size,
                    query = query,
                    onQueryChange = onQueryChange,
                    order = order,
                    onOrderChange = onOrderChange,
                    filters = filters,
                    searchLabel = "Buscar en Compañeros",
                    collapsibleSearch = true,
                    showItemCount = false,
                    compactOrderControl = true,
                    contextContent = {
                        Text(
                            "Compañeros",
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onAdd = if (structuralEditingEnabled) onAdd else null,
                )
            }

            item(key = "h3-companions-help") {
                Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp))) {
                    CharacterHelpV4("Entidades persistentes del personaje. El combate del DM mantiene su propio estado de encuentro.")
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

            if (companions.isEmpty()) {
                item {
                    CharacterUsefulEmptyState(
                        title = "Sin compañeros",
                        message = "Añade una bestia, constructo, espíritu, familiar u otro compañero persistente que merezca referencia propia.",
                        onAdd = if (structuralEditingEnabled) onAdd else null,
                        addLabel = "Añadir compañero",
                    )
                }
            } else if (visible.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "No hay compañeros que coincidan con esta búsqueda y filtros.",
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            items(count = layoutCompanions.size, key = { index -> "h3-companion-${layoutCompanions[index].id}" }) { index ->
                val companion = layoutCompanions[index]
                CompanionRowH3(
                    companion = companion,
                    linkedClass = companion.linkedClassId?.let(classById::get),
                    favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.COMPANION, companion.id),
                    favoriteEnabled = structuralEditingEnabled && companion.id in persistedCompanionIds,
                    reorderSession = reorderSession.takeIf { sessionEnabled },
                    structuralEditingEnabled = structuralEditingEnabled,
                    selected = selectedEditingId == companion.id.toString(),
                    onFavoriteChange = { onFavoriteChange(companion, it) },
                    onEdit = { onEdit(companion) },
                    onDuplicate = { onDuplicate(companion) },
                    onDelete = { onDelete(companion) },
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun CompanionRowH3(
    companion: CharacterCompanion,
    linkedClass: CharacterClassLevel?,
    favorite: Boolean,
    favoriteEnabled: Boolean,
    reorderSession: CharacterReorderSessionV4?,
    structuralEditingEnabled: Boolean,
    selected: Boolean,
    onFavoriteChange: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    lifted: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val id = companion.id.toString()
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
        modifier = modifier.then(geometryModifier),
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
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .then(pickupModifier)
                    .clickable(
                        enabled = structuralEditingEnabled && !lifted && !activePlaceholder,
                        onClick = onEdit,
                    ),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
            ) {
                Text(
                    companion.name.ifBlank { "Compañero sin nombre" },
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp))) {
                    companion.kind.takeIf(String::isNotBlank)?.let { ModuleBadgeH1(it) }
                    ModuleBadgeH1(if (companion.active) "Activo" else "Inactivo")
                }
                val provenance = listOfNotNull(
                    linkedClass?.let { classLevel ->
                        buildString {
                            append(classLevel.name)
                            classLevel.subclassName?.takeIf(String::isNotBlank)?.let { append(" · $it") }
                        }
                    },
                    companion.source?.takeIf(String::isNotBlank),
                ).joinToString(" · ")
                if (provenance.isNotBlank()) {
                    Text(provenance, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                val reference = buildList {
                    companion.armorClass?.let { add("CA $it") }
                    companion.currentHp?.let { current ->
                        add(companion.maxHp?.let { max -> "PG $current/$max" } ?: "PG $current")
                    } ?: companion.maxHp?.let { add("PG máx. $it") }
                    if (companion.tempHp > 0) add("PG temp. ${companion.tempHp}")
                    companion.speed?.takeIf(String::isNotBlank)?.let { add(it) }
                }.joinToString(" · ")
                if (reference.isNotBlank()) {
                    Text(reference, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
                if (companion.traitsActions.isNotBlank()) {
                    Text(companion.traitsActions, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                StableFavoriteIconButton(
                    selected = favorite,
                    onClick = { onFavoriteChange(!favorite) },
                    enabled = !lifted && structuralEditingEnabled && favoriteEnabled,
                    contentDescription = if (favorite) "Quitar ${companion.name} de Favoritos" else "Añadir ${companion.name} a Favoritos",
                )
                if (structuralEditingEnabled && !lifted) {
                    StableDuplicateIconButton(
                        onClick = onDuplicate,
                        contentDescription = "Duplicar ${companion.name}",
                    )
                    StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${companion.name}")
                }
            }
        }
    }
}

@Composable
private fun CompanionEditorFieldsH3(
    classes: List<CharacterClassLevel>,
    name: String,
    linkedClassId: Uuid?,
    kind: String,
    source: String,
    armorClass: String,
    maxHp: String,
    currentHp: String,
    tempHp: String,
    speed: String,
    abilitySummary: String,
    sensesProficiencies: String,
    traitsActions: String,
    notes: String,
    active: Boolean,
    armorClassValid: Boolean,
    maxHpValid: Boolean,
    currentHpValid: Boolean,
    tempHpValid: Boolean,
    onNameChange: (String) -> Unit,
    onLinkedClassIdChange: (Uuid?) -> Unit,
    onKindChange: (String) -> Unit,
    onSourceChange: (String) -> Unit,
    onArmorClassChange: (String) -> Unit,
    onMaxHpChange: (String) -> Unit,
    onCurrentHpChange: (String) -> Unit,
    onTempHpChange: (String) -> Unit,
    onSpeedChange: (String) -> Unit,
    onAbilitySummaryChange: (String) -> Unit,
    onSensesProficienciesChange: (String) -> Unit,
    onTraitsActionsChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Nombre") },
        singleLine = true,
    )
    CharacterInlineValidationMessage(
        if (name.isNotEmpty() && name.trim().isEmpty()) "Escribe un nombre para guardar el compañero." else null,
    )

    CompanionLinkedClassSelectorH3(
        classes = classes,
        selectedClassId = linkedClassId,
        onSelectedClassIdChange = onLinkedClassIdChange,
    )

    OutlinedTextField(
        value = kind,
        onValueChange = onKindChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Tipo / clase de compañero") },
        supportingText = { Text("Libre: bestia, constructo, espíritu, familiar, etc.") },
        singleLine = true,
    )
    OutlinedTextField(
        value = source,
        onValueChange = onSourceChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Fuente / procedencia") },
        singleLine = true,
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp))) {
        CompanionNumericFieldH3(
            value = armorClass,
            onValueChange = onArmorClassChange,
            label = "CA",
            valid = armorClassValid,
            modifier = Modifier.weight(1f),
        )
        CompanionNumericFieldH3(
            value = maxHp,
            onValueChange = onMaxHpChange,
            label = "PG máx.",
            valid = maxHpValid,
            modifier = Modifier.weight(1f),
        )
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp))) {
        CompanionNumericFieldH3(
            value = currentHp,
            onValueChange = onCurrentHpChange,
            label = "PG actuales",
            valid = currentHpValid,
            modifier = Modifier.weight(1f),
        )
        CompanionNumericFieldH3(
            value = tempHp,
            onValueChange = onTempHpChange,
            label = "PG temporales",
            valid = tempHpValid,
            modifier = Modifier.weight(1f),
        )
    }

    OutlinedTextField(
        value = speed,
        onValueChange = onSpeedChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Velocidad / movimiento") },
        singleLine = true,
    )
    OutlinedTextField(
        value = abilitySummary,
        onValueChange = onAbilitySummaryChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Características / estadísticas") },
        minLines = characterCompactTextAreaMinLinesV4(2),
        maxLines = 6,
    )
    OutlinedTextField(
        value = sensesProficiencies,
        onValueChange = onSensesProficienciesChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Sentidos / competencias") },
        minLines = characterCompactTextAreaMinLinesV4(2),
        maxLines = 6,
    )
    OutlinedTextField(
        value = traitsActions,
        onValueChange = onTraitsActionsChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Rasgos / acciones") },
        minLines = characterCompactTextAreaMinLinesV4(2),
        maxLines = 9,
    )
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Notas") },
        minLines = characterCompactTextAreaMinLinesV4(2),
        maxLines = 7,
    )
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = active, onCheckedChange = onActiveChange)
        Text("Activo / disponible")
    }
}

@Composable
private fun CompanionNumericFieldH3(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    valid: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = !valid,
        )
        CharacterInlineValidationMessage(if (!valid) "Usa un entero no negativo." else null)
    }
}

@Composable
private fun CompanionLinkedClassSelectorH3(
    classes: List<CharacterClassLevel>,
    selectedClassId: Uuid?,
    onSelectedClassIdChange: (Uuid?) -> Unit,
) {
    var expanded by remember(selectedClassId, classes) { mutableStateOf(false) }
    val selected = classes.firstOrNull { it.id == selectedClassId }
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(
                selected?.let { classLevel ->
                    buildString {
                        append("Clase: ${classLevel.name}")
                        classLevel.subclassName?.takeIf(String::isNotBlank)?.let { append(" · $it") }
                    }
                } ?: "Clase vinculada: ninguna",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Sin vínculo de clase") },
                onClick = {
                    onSelectedClassIdChange(null)
                    expanded = false
                },
            )
            classes.forEach { classLevel ->
                DropdownMenuItem(
                    text = {
                        Text(
                            buildString {
                                append(classLevel.name)
                                classLevel.subclassName?.takeIf(String::isNotBlank)?.let { append(" · $it") }
                                append(" · Nv. ${classLevel.level}")
                            },
                        )
                    },
                    onClick = {
                        onSelectedClassIdChange(classLevel.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun sanitizeUnsignedIntH3(raw: String): String = raw.filter(Char::isDigit)

private fun encodeFilterSetH3(filters: Set<String>): String =
    filters.sorted().joinToString(H3_FILTER_SEPARATOR)

private fun decodeFilterSetH3(encoded: String): Set<String> =
    encoded.split(H3_FILTER_SEPARATOR).filter(String::isNotBlank).toSet()
