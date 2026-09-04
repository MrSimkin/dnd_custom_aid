package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_CLASS_OPTION_ACTIVE_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_CLASS_OPTION_FAVORITE_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_PACT_CHOICE_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CHARACTER_PACT_INVOCATION_FILTER_KEY
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOption
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassOptionKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCollectionQuery
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterPresentationOrder
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.characterClassOptionKindDisplayLabel
import io.github.mrsimkin.dndcustomaid.shared.character.characterClassOptionSourceFilterKey
import io.github.mrsimkin.dndcustomaid.shared.character.duplicateCharacterClassOption
import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess
import io.github.mrsimkin.dndcustomaid.shared.character.moveCharacterMetamagicOptionManual
import io.github.mrsimkin.dndcustomaid.shared.character.moveCharacterPactOptionManual
import io.github.mrsimkin.dndcustomaid.shared.character.moveCharacterTechniqueOptionManual
import io.github.mrsimkin.dndcustomaid.shared.character.nextCharacterClassOptionSortOrder
import io.github.mrsimkin.dndcustomaid.shared.character.normalizeCharacterClassOptionOrders
import io.github.mrsimkin.dndcustomaid.shared.character.presentCharacterMetamagicOptions
import io.github.mrsimkin.dndcustomaid.shared.character.presentCharacterPactOptions
import io.github.mrsimkin.dndcustomaid.shared.character.presentCharacterTechniqueOptions
import io.github.mrsimkin.dndcustomaid.shared.character.suggestedCharacterModules
import io.github.mrsimkin.dndcustomaid.shared.character.withQuickAccess
import kotlin.math.abs
import kotlin.uuid.Uuid

private const val H2_FILTER_SEPARATOR = "\u001E"

private data class H2ModuleConfig(
    val stateKey: String,
    val title: String,
    val singularLabel: String,
    val description: String,
    val emptyMessage: String,
    val searchLabel: String,
    val moduleKind: CharacterModuleKind,
    val allowedKinds: List<CharacterClassOptionKind>,
    val present: (
        List<CharacterClassOption>,
        CharacterPresentationOrder,
        CharacterCollectionQuery,
        (CharacterClassOption) -> Boolean,
    ) -> List<CharacterClassOption>,
    val move: (List<CharacterClassOption>, Uuid, Int) -> List<CharacterClassOption>,
)

private val techniquesConfigH2 = H2ModuleConfig(
    stateKey = "techniques",
    title = "Técnicas",
    singularLabel = "técnica",
    description = "Maniobras, disparos, runas, florituras y otras técnicas elegidas. Sus recursos se controlan en Gestión.",
    emptyMessage = "Añade una maniobra, disparo, runa, floritura u otra técnica que quieras consultar rápidamente.",
    searchLabel = "Buscar en Técnicas",
    moduleKind = CharacterModuleKind.TECHNIQUES,
    allowedKinds = listOf(CharacterClassOptionKind.TECHNIQUE),
    present = { options, order, query, favorite ->
        presentCharacterTechniqueOptions(options, order, query, favorite)
    },
    move = ::moveCharacterTechniqueOptionManual,
)

private val metamagicConfigH2 = H2ModuleConfig(
    stateKey = "metamagic",
    title = "Metamagia",
    singularLabel = "opción de Metamagia",
    description = "Opciones de Metamagia conocidas y su referencia. Los Puntos de Hechicería siguen siendo un recurso de Gestión.",
    emptyMessage = "Añade las opciones de Metamagia conocidas por el personaje.",
    searchLabel = "Buscar en Metamagia",
    moduleKind = CharacterModuleKind.METAMAGIC,
    allowedKinds = listOf(CharacterClassOptionKind.METAMAGIC),
    present = { options, order, query, favorite ->
        presentCharacterMetamagicOptions(options, order, query, favorite)
    },
    move = ::moveCharacterMetamagicOptionManual,
)

private val pactsConfigH2 = H2ModuleConfig(
    stateKey = "pacts",
    title = "Pactos",
    singularLabel = "registro de Pacto",
    description = "Elecciones de pacto e Invocaciones. Los conjuros y espacios siguen siendo datos de Conjuros.",
    emptyMessage = "Añade una elección de pacto o Invocación persistente. Los conjuros reales no se duplican aquí.",
    searchLabel = "Buscar en Pactos",
    moduleKind = CharacterModuleKind.PACTS,
    allowedKinds = listOf(CharacterClassOptionKind.PACT_CHOICE, CharacterClassOptionKind.INVOCATION),
    present = { options, order, query, favorite ->
        presentCharacterPactOptions(options, order, query, favorite)
    },
    move = ::moveCharacterPactOptionManual,
)

@Composable
internal fun CharacterTechniquesModuleV4(
    options: List<CharacterClassOption>,
    classes: List<CharacterClassLevel>,
    closureState: CharacterClosureState,
    persistedOptionIds: Set<Uuid>,
    onOptionsChange: (List<CharacterClassOption>) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
    hapticsEnabled: Boolean,
) = CharacterClassOptionModuleH2(
    config = techniquesConfigH2,
    options = options,
    classes = classes,
    closureState = closureState,
    persistedOptionIds = persistedOptionIds,
    onOptionsChange = onOptionsChange,
    onClosureStateChange = onClosureStateChange,
    wide = wide,
    hapticsEnabled = hapticsEnabled,
)

@Composable
internal fun CharacterMetamagicModuleV4(
    options: List<CharacterClassOption>,
    classes: List<CharacterClassLevel>,
    closureState: CharacterClosureState,
    persistedOptionIds: Set<Uuid>,
    onOptionsChange: (List<CharacterClassOption>) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
    hapticsEnabled: Boolean,
) = CharacterClassOptionModuleH2(
    config = metamagicConfigH2,
    options = options,
    classes = classes,
    closureState = closureState,
    persistedOptionIds = persistedOptionIds,
    onOptionsChange = onOptionsChange,
    onClosureStateChange = onClosureStateChange,
    wide = wide,
    hapticsEnabled = hapticsEnabled,
)

@Composable
internal fun CharacterPactsModuleV4(
    options: List<CharacterClassOption>,
    classes: List<CharacterClassLevel>,
    closureState: CharacterClosureState,
    persistedOptionIds: Set<Uuid>,
    onOptionsChange: (List<CharacterClassOption>) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
    hapticsEnabled: Boolean,
) = CharacterClassOptionModuleH2(
    config = pactsConfigH2,
    options = options,
    classes = classes,
    closureState = closureState,
    persistedOptionIds = persistedOptionIds,
    onOptionsChange = onOptionsChange,
    onClosureStateChange = onClosureStateChange,
    wide = wide,
    hapticsEnabled = hapticsEnabled,
)

@Composable
private fun CharacterClassOptionModuleH2(
    config: H2ModuleConfig,
    options: List<CharacterClassOption>,
    classes: List<CharacterClassLevel>,
    closureState: CharacterClosureState,
    persistedOptionIds: Set<Uuid>,
    onOptionsChange: (List<CharacterClassOption>) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
    hapticsEnabled: Boolean,
) {
    var searchText by rememberSaveable("h2-${config.stateKey}-search") { mutableStateOf("") }
    var filtersText by rememberSaveable("h2-${config.stateKey}-filters") { mutableStateOf("") }
    var orderName by rememberSaveable("h2-${config.stateKey}-order") {
        mutableStateOf(CharacterPresentationOrder.MANUAL.name)
    }
    var editorOpen by rememberSaveable("h2-${config.stateKey}-editor-open") { mutableStateOf(false) }
    var editingId by rememberSaveable("h2-${config.stateKey}-editor-id") { mutableStateOf<String?>(null) }
    var editorName by rememberSaveable("h2-${config.stateKey}-name") { mutableStateOf("") }
    var editorKindName by rememberSaveable("h2-${config.stateKey}-kind") {
        mutableStateOf(config.allowedKinds.first().name)
    }
    var editorLinkedClassId by rememberSaveable("h2-${config.stateKey}-class") { mutableStateOf("") }
    var editorSource by rememberSaveable("h2-${config.stateKey}-source") { mutableStateOf("") }
    var editorCost by rememberSaveable("h2-${config.stateKey}-cost") { mutableStateOf("") }
    var editorEffect by rememberSaveable("h2-${config.stateKey}-effect") { mutableStateOf("") }
    var editorNotes by rememberSaveable("h2-${config.stateKey}-notes") { mutableStateOf("") }
    var editorActive by rememberSaveable("h2-${config.stateKey}-active") { mutableStateOf(true) }
    var deleteId by rememberSaveable("h2-${config.stateKey}-delete") { mutableStateOf<String?>(null) }

    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)
    val order = runCatching { CharacterPresentationOrder.valueOf(orderName) }
        .getOrDefault(CharacterPresentationOrder.MANUAL)
    val activeFilters = decodeFilterSetH2(filtersText)
    val query = CharacterCollectionQuery(searchText = searchText, activeFilterKeys = activeFilters)
    val favoritePredicate: (CharacterClassOption) -> Boolean = { option ->
        closureState.hasQuickAccess(CharacterQuickAccessKind.CLASS_OPTION, option.id)
    }
    val visible = config.present(options, order, query, favoritePredicate)
    val ownedOptions = options.filter { it.kind in config.allowedKinds }
    val canReorder = order == CharacterPresentationOrder.MANUAL &&
        query.searchText.isBlank() && query.activeFilterKeys.isEmpty()
    val selectedEditingId = editingId?.takeIf { editorOpen }

    fun updateQuery(updated: CharacterCollectionQuery) {
        searchText = updated.searchText
        filtersText = encodeFilterSetH2(updated.activeFilterKeys)
    }

    fun updateOptions(updated: List<CharacterClassOption>) {
        onOptionsChange(normalizeCharacterClassOptionOrders(updated))
    }

    fun suggestedClassId(): Uuid? = classes.firstOrNull { classLevel ->
        config.moduleKind in suggestedCharacterModules(listOf(classLevel))
    }?.id

    fun resetEditor() {
        editingId = null
        editorName = ""
        editorKindName = config.allowedKinds.first().name
        editorLinkedClassId = suggestedClassId()?.toString().orEmpty()
        editorSource = ""
        editorCost = ""
        editorEffect = ""
        editorNotes = ""
        editorActive = true
    }

    fun beginAdd() {
        resetEditor()
        editorOpen = true
    }

    fun beginEdit(option: CharacterClassOption) {
        editingId = option.id.toString()
        editorName = option.name
        editorKindName = option.kind.name
        editorLinkedClassId = option.linkedClassId?.toString().orEmpty()
        editorSource = option.source.orEmpty()
        editorCost = option.costText.orEmpty()
        editorEffect = option.effectSummary
        editorNotes = option.notes.orEmpty()
        editorActive = option.active
        editorOpen = true
    }

    fun duplicate(option: CharacterClassOption) {
        updateOptions(
            options + duplicateCharacterClassOption(
                source = option,
                newId = Uuid.random(),
                sortOrder = nextCharacterClassOptionSortOrder(options),
            ),
        )
    }

    val editorKind = runCatching { CharacterClassOptionKind.valueOf(editorKindName) }
        .getOrDefault(config.allowedKinds.first())
        .takeIf { it in config.allowedKinds }
        ?: config.allowedKinds.first()
    val validClassIds = classes.mapTo(mutableSetOf()) { it.id.toString() }
    val parsedLinkedClassId = editorLinkedClassId
        .takeIf { it in validClassIds }
        ?.let(Uuid::parse)
    val editorCanSave = editorName.trim().isNotEmpty()

    fun applyEditor() {
        if (!editorCanSave) return
        val existing = editingId?.let { id -> options.firstOrNull { it.id.toString() == id } }
        val edited = CharacterClassOption(
            id = existing?.id ?: Uuid.random(),
            linkedClassId = parsedLinkedClassId,
            kind = editorKind,
            name = editorName.trim(),
            source = editorSource.trim().takeIf(String::isNotEmpty),
            costText = editorCost.trim().takeIf(String::isNotEmpty),
            effectSummary = editorEffect.trim(),
            notes = editorNotes.trim().takeIf(String::isNotEmpty),
            active = editorActive,
            pinned = existing?.pinned ?: false,
            sortOrder = existing?.sortOrder ?: nextCharacterClassOptionSortOrder(options),
        )
        val updated = if (existing == null) {
            options + edited
        } else {
            options.map { option -> if (option.id == existing.id) edited else option }
        }
        updateOptions(updated)
        editorOpen = false
    }

    val collection: @Composable (Modifier) -> Unit = { modifier ->
        ClassOptionCollectionH2(
            modifier = modifier,
            config = config,
            options = options,
            ownedOptions = ownedOptions,
            visible = visible,
            classes = classes,
            closureState = closureState,
            persistedOptionIds = persistedOptionIds,
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
            onMove = { option, offset ->
                if (!canReorder) {
                    false
                } else {
                    val before = normalizeCharacterClassOptionOrders(options)
                    val moved = config.move(options, option.id, offset)
                    if (moved == before) {
                        false
                    } else {
                        onOptionsChange(moved)
                        true
                    }
                }
            },
            onFavoriteChange = { option, enabled ->
                onClosureStateChange(
                    closureState.withQuickAccess(
                        CharacterQuickAccessKind.CLASS_OPTION,
                        option.id,
                        enabled,
                    ),
                )
            },
            onHaptic = haptic,
        )
    }

    if (wide) {
        Row(
            modifier = Modifier.fillMaxSize().imePadding().navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            collection(Modifier.weight(1f))
            Surface(
                modifier = Modifier.width(400.dp).fillMaxHeight().padding(top = 5.dp, end = 8.dp, bottom = 8.dp),
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
                                    if (editingId == null) "Añadir ${config.singularLabel}" else "Editar ${config.singularLabel}",
                                    style = MaterialTheme.typography.titleMedium,
                                )
                                TextButton(onClick = { editorOpen = false }) { Text("Cerrar") }
                            }
                        }
                        item {
                            ClassOptionEditorFieldsH2(
                                config = config,
                                classes = classes,
                                name = editorName,
                                kind = editorKind,
                                linkedClassId = parsedLinkedClassId,
                                source = editorSource,
                                cost = editorCost,
                                effect = editorEffect,
                                notes = editorNotes,
                                active = editorActive,
                                onNameChange = { editorName = it },
                                onKindChange = { editorKindName = it.name },
                                onLinkedClassIdChange = { editorLinkedClassId = it?.toString().orEmpty() },
                                onSourceChange = { editorSource = it },
                                onCostChange = { editorCost = it },
                                onEffectChange = { editorEffect = it },
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
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(config.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Selecciona un registro para editarlo. La lista conserva búsqueda, filtros y orden.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        TextButton(onClick = ::beginAdd) { Text("+ Añadir") }
                    }
                }
            }
        }
    } else {
        collection(Modifier.fillMaxSize())
        if (editorOpen) {
            CharacterImeSafeEditorDialog(
                title = if (editingId == null) "Añadir ${config.singularLabel}" else "Editar ${config.singularLabel}",
                onCancel = { editorOpen = false },
                onSave = ::applyEditor,
                saveLabel = "Aplicar",
                saveEnabled = editorCanSave,
            ) {
                ClassOptionEditorFieldsH2(
                    config = config,
                    classes = classes,
                    name = editorName,
                    kind = editorKind,
                    linkedClassId = parsedLinkedClassId,
                    source = editorSource,
                    cost = editorCost,
                    effect = editorEffect,
                    notes = editorNotes,
                    active = editorActive,
                    onNameChange = { editorName = it },
                    onKindChange = { editorKindName = it.name },
                    onLinkedClassIdChange = { editorLinkedClassId = it?.toString().orEmpty() },
                    onSourceChange = { editorSource = it },
                    onCostChange = { editorCost = it },
                    onEffectChange = { editorEffect = it },
                    onNotesChange = { editorNotes = it },
                    onActiveChange = { editorActive = it },
                )
            }
        }
    }

    deleteId?.let { id ->
        val target = options.firstOrNull { it.id.toString() == id }
        if (target == null) {
            deleteId = null
        } else {
            CharacterNamedDeleteConfirmationDialog(
                itemName = target.name,
                itemTypeLabel = config.singularLabel,
                onDismissRequest = { deleteId = null },
                onConfirm = {
                    updateOptions(options.filterNot { it.id == target.id })
                    if (editingId == target.id.toString()) editorOpen = false
                    deleteId = null
                    haptic(CharacterHapticEventV4.DESTRUCTIVE)
                },
            )
        }
    }
}

@Composable
private fun ClassOptionCollectionH2(
    modifier: Modifier,
    config: H2ModuleConfig,
    options: List<CharacterClassOption>,
    ownedOptions: List<CharacterClassOption>,
    visible: List<CharacterClassOption>,
    classes: List<CharacterClassLevel>,
    closureState: CharacterClosureState,
    persistedOptionIds: Set<Uuid>,
    query: CharacterCollectionQuery,
    order: CharacterPresentationOrder,
    canReorder: Boolean,
    selectedEditingId: String?,
    onQueryChange: (CharacterCollectionQuery) -> Unit,
    onOrderChange: (CharacterPresentationOrder) -> Unit,
    onAdd: () -> Unit,
    onEdit: (CharacterClassOption) -> Unit,
    onDuplicate: (CharacterClassOption) -> Unit,
    onDelete: (CharacterClassOption) -> Unit,
    onMove: (CharacterClassOption, Int) -> Boolean,
    onFavoriteChange: (CharacterClassOption, Boolean) -> Unit,
    onHaptic: (CharacterHapticEventV4) -> Unit,
) {
    val classById = remember(classes) { classes.associateBy { it.id } }
    val favoriteCount = ownedOptions.count { option ->
        closureState.hasQuickAccess(CharacterQuickAccessKind.CLASS_OPTION, option.id)
    }
    val sourceFilters = ownedOptions
        .mapNotNull(CharacterClassOption::source)
        .map(String::trim)
        .filter(String::isNotEmpty)
        .distinctBy { it.lowercase() }
        .sortedBy { it.lowercase() }
        .map { source ->
            CharacterFilterOptionV4(
                key = characterClassOptionSourceFilterKey(source),
                label = source,
                count = ownedOptions.count { characterClassOptionSourceFilterKey(it.source) == characterClassOptionSourceFilterKey(source) },
            )
        }
    val filters = buildList {
        if (config.moduleKind == CharacterModuleKind.PACTS) {
            add(
                CharacterFilterOptionV4(
                    CHARACTER_PACT_CHOICE_FILTER_KEY,
                    "Pactos / elecciones",
                    ownedOptions.count { it.kind == CharacterClassOptionKind.PACT_CHOICE },
                ),
            )
            add(
                CharacterFilterOptionV4(
                    CHARACTER_PACT_INVOCATION_FILTER_KEY,
                    "Invocaciones",
                    ownedOptions.count { it.kind == CharacterClassOptionKind.INVOCATION },
                ),
            )
        }
        add(
            CharacterFilterOptionV4(
                CHARACTER_CLASS_OPTION_ACTIVE_FILTER_KEY,
                "Activos",
                ownedOptions.count(CharacterClassOption::active),
            ),
        )
        add(CharacterFilterOptionV4(CHARACTER_CLASS_OPTION_FAVORITE_FILTER_KEY, "Favoritos", favoriteCount))
        addAll(sourceFilters)
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 6.dp, end = 6.dp, top = 5.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        item(key = "h2-${config.stateKey}-tools") {
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
                            Text(config.title, style = MaterialTheme.typography.titleSmall)
                            Text(config.description, style = MaterialTheme.typography.labelSmall)
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
                        searchLabel = config.searchLabel,
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

        if (ownedOptions.isEmpty()) {
            item {
                CharacterUsefulEmptyState(
                    title = "Sin ${config.title.lowercase()}",
                    message = config.emptyMessage,
                    onAdd = onAdd,
                    addLabel = "Añadir ${config.singularLabel}",
                )
            }
        } else if (visible.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "No hay registros que coincidan con esta búsqueda y filtros.",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        items(count = visible.size, key = { index -> "h2-${config.stateKey}-${visible[index].id}" }) { index ->
            val option = visible[index]
            ClassOptionRowH2(
                option = option,
                linkedClass = option.linkedClassId?.let(classById::get),
                favorite = closureState.hasQuickAccess(CharacterQuickAccessKind.CLASS_OPTION, option.id),
                favoriteEnabled = option.id in persistedOptionIds,
                reorderEnabled = canReorder && ownedOptions.size > 1,
                selected = selectedEditingId == option.id.toString(),
                onFavoriteChange = { onFavoriteChange(option, it) },
                onMove = { offset -> onMove(option, offset) },
                onEdit = { onEdit(option) },
                onDuplicate = { onDuplicate(option) },
                onDelete = { onDelete(option) },
                onHaptic = onHaptic,
            )
        }
    }
}

@Composable
private fun ClassOptionRowH2(
    option: CharacterClassOption,
    linkedClass: CharacterClassLevel?,
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
    var accumulatedDrag by remember(option.id) { mutableStateOf(0f) }
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
            modifier = Modifier.fillMaxWidth().characterDragFeedbackV4(dragState).clickable(onClick = onEdit),
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
                        modifier = Modifier.pointerInput(option.id) {
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
                        contentDescription = "Mantén pulsado y arrastra para reordenar ${option.name}",
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        option.name.ifBlank { "Registro sin nombre" },
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ModuleBadgeH1(characterClassOptionKindDisplayLabel(option.kind))
                        ModuleBadgeH1(if (option.active) "Activo" else "Inactivo")
                    }
                    val provenance = listOfNotNull(
                        linkedClass?.let { classLevel ->
                            buildString {
                                append(classLevel.name)
                                classLevel.subclassName?.takeIf(String::isNotBlank)?.let { append(" · $it") }
                            }
                        },
                        option.source?.takeIf(String::isNotBlank),
                    ).joinToString(" · ")
                    if (provenance.isNotBlank()) {
                        Text(provenance, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    option.costText?.takeIf(String::isNotBlank)?.let {
                        Text(it, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    if (option.effectSummary.isNotBlank()) {
                        Text(option.effectSummary, style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = { onFavoriteChange(!favorite) },
                            enabled = favoriteEnabled,
                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
                        ) { Text(if (favorite) "★" else "☆") }
                        StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${option.name}")
                    }
                    TextButton(onClick = onDuplicate, contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)) {
                        Text("Duplicar")
                    }
                }
            }
        }
        CharacterDropIndicatorV4(visible = dragState.showDropAfter)
    }
}

@Composable
private fun ClassOptionEditorFieldsH2(
    config: H2ModuleConfig,
    classes: List<CharacterClassLevel>,
    name: String,
    kind: CharacterClassOptionKind,
    linkedClassId: Uuid?,
    source: String,
    cost: String,
    effect: String,
    notes: String,
    active: Boolean,
    onNameChange: (String) -> Unit,
    onKindChange: (CharacterClassOptionKind) -> Unit,
    onLinkedClassIdChange: (Uuid?) -> Unit,
    onSourceChange: (String) -> Unit,
    onCostChange: (String) -> Unit,
    onEffectChange: (String) -> Unit,
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
        if (name.isNotEmpty() && name.trim().isEmpty()) "Escribe un nombre para guardar el registro." else null,
    )

    if (config.allowedKinds.size > 1) {
        Text("Tipo", style = MaterialTheme.typography.titleSmall)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            config.allowedKinds.forEach { optionKind ->
                if (kind == optionKind) {
                    Button(onClick = { onKindChange(optionKind) }) { Text(characterClassOptionKindDisplayLabel(optionKind)) }
                } else {
                    OutlinedButton(onClick = { onKindChange(optionKind) }) { Text(characterClassOptionKindDisplayLabel(optionKind)) }
                }
            }
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Tipo:", style = MaterialTheme.typography.labelMedium)
            ModuleBadgeH1(characterClassOptionKindDisplayLabel(config.allowedKinds.first()))
        }
    }

    ClassOptionLinkedClassSelectorH2(
        classes = classes,
        selectedClassId = linkedClassId,
        onSelectedClassIdChange = onLinkedClassIdChange,
    )

    OutlinedTextField(
        value = source,
        onValueChange = onSourceChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Fuente / procedencia") },
        singleLine = true,
    )
    OutlinedTextField(
        value = cost,
        onValueChange = onCostChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Coste / referencia") },
        singleLine = true,
    )
    OutlinedTextField(
        value = effect,
        onValueChange = onEffectChange,
        modifier = Modifier.fillMaxWidth().heightIn(min = 110.dp, max = 220.dp),
        label = { Text("Resumen / referencia") },
        minLines = 3,
        maxLines = 8,
    )
    OutlinedTextField(
        value = notes,
        onValueChange = onNotesChange,
        modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp, max = 200.dp),
        label = { Text("Notas") },
        minLines = 2,
        maxLines = 7,
    )
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = active, onCheckedChange = onActiveChange)
        Text("Activo / disponible")
    }
}

@Composable
private fun ClassOptionLinkedClassSelectorH2(
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

private fun encodeFilterSetH2(filters: Set<String>): String =
    filters.sorted().joinToString(H2_FILTER_SEPARATOR)

private fun decodeFilterSetH2(encoded: String): Set<String> =
    encoded.split(H2_FILTER_SEPARATOR).filter(String::isNotBlank).toSet()
