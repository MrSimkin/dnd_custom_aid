from pathlib import Path

ROOT = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android')


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f'{label}: expected 1 occurrence, found {count}')
    return text.replace(old, new, 1)


# Proper sort icon.
p = ROOT / 'IconControls.kt'
text = p.read_text(encoding='utf-8')
anchor = '''@Composable
internal fun StableAddIcon(
'''
if 'internal fun StableSortIconButton(' not in text:
    sort_icon = '''@Composable
internal fun StableSortIconButton(
    onClick: () -> Unit,
    contentDescription: String = "Ordenar",
) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Canvas(
            modifier = Modifier
                .size(22.dp)
                .semantics { this.contentDescription = contentDescription },
        ) {
            val stroke = 1.9.dp.toPx()
            drawLine(color, Offset(size.width * 0.12f, size.height * 0.28f), Offset(size.width * 0.58f, size.height * 0.28f), stroke, StrokeCap.Round)
            drawLine(color, Offset(size.width * 0.12f, size.height * 0.50f), Offset(size.width * 0.46f, size.height * 0.50f), stroke, StrokeCap.Round)
            drawLine(color, Offset(size.width * 0.12f, size.height * 0.72f), Offset(size.width * 0.34f, size.height * 0.72f), stroke, StrokeCap.Round)
            drawLine(color, Offset(size.width * 0.76f, size.height * 0.22f), Offset(size.width * 0.76f, size.height * 0.76f), stroke, StrokeCap.Round)
            drawLine(color, Offset(size.width * 0.63f, size.height * 0.64f), Offset(size.width * 0.76f, size.height * 0.78f), stroke, StrokeCap.Round)
            drawLine(color, Offset(size.width * 0.89f, size.height * 0.64f), Offset(size.width * 0.76f, size.height * 0.78f), stroke, StrokeCap.Round)
        }
    }
}

'''
    text = replace_once(text, anchor, sort_icon + anchor, 'sort icon anchor')
p.write_text(text, encoding='utf-8')


# Rebuild only the collection-toolbar function from the current shared primitive.
p = ROOT / 'CharacterCollectionPrimitivesV4.kt'
text = p.read_text(encoding='utf-8')
start = text.index('@Composable\ninternal fun CharacterCollectionToolbarV4(')
new_toolbar = '''@Composable
internal fun CharacterCollectionToolbarV4(
    itemCount: Int,
    query: CharacterCollectionQuery,
    onQueryChange: (CharacterCollectionQuery) -> Unit,
    order: CharacterPresentationOrder? = null,
    onOrderChange: ((CharacterPresentationOrder) -> Unit)? = null,
    filters: List<CharacterFilterOptionV4> = emptyList(),
    searchLabel: String = "Buscar",
    collapsibleSearch: Boolean = false,
    showItemCount: Boolean = true,
    compactOrderControl: Boolean = false,
    modifier: Modifier = Modifier,
    contextContent: (@Composable () -> Unit)? = null,
    onAdd: (() -> Unit)? = null,
) {
    var orderMenuOpen by remember { mutableStateOf(false) }
    var filterMenuOpen by remember { mutableStateOf(false) }
    var searchExpanded by rememberSaveable { mutableStateOf(false) }
    val activeFilterCount = query.activeFilterKeys.size

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = appSpacingV4(5.dp), vertical = appSpacingV4(4.dp)),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (contextContent != null && (!collapsibleSearch || !searchExpanded)) {
                if (collapsibleSearch) {
                    Box(modifier = Modifier.weight(1f)) { contextContent() }
                } else {
                    contextContent()
                }
            }

            if (collapsibleSearch) {
                if (searchExpanded) {
                    CharacterCompactSearchV4(
                        value = query.searchText,
                        onValueChange = { onQueryChange(query.copy(searchText = it)) },
                        label = searchLabel,
                        modifier = Modifier.weight(1f),
                    )
                    CharacterToolbarChipV4(
                        text = "Cerrar",
                        selected = false,
                        onClick = { searchExpanded = false },
                    )
                } else {
                    CharacterToolbarChipV4(
                        text = if (query.searchText.isBlank()) "Buscar" else "Buscar •",
                        selected = query.searchText.isNotBlank(),
                        onClick = { searchExpanded = true },
                    )
                }
            } else {
                CharacterCompactSearchV4(
                    value = query.searchText,
                    onValueChange = { onQueryChange(query.copy(searchText = it)) },
                    label = searchLabel,
                    modifier = Modifier.weight(1f),
                )
            }

            if (!collapsibleSearch || !searchExpanded) {
                if (showItemCount) {
                    Text(itemCount.toString(), style = MaterialTheme.typography.labelMedium, maxLines = 1)
                }

                if (order != null && onOrderChange != null) {
                    Box {
                        if (compactOrderControl) {
                            StableSortIconButton(
                                onClick = { orderMenuOpen = true },
                                contentDescription = if (order == CharacterPresentationOrder.MANUAL) {
                                    "Orden actual: Manual"
                                } else {
                                    "Orden actual: A–Z"
                                },
                            )
                        } else {
                            CharacterToolbarChipV4(
                                text = if (order == CharacterPresentationOrder.MANUAL) "Manual" else "A–Z",
                                selected = order != CharacterPresentationOrder.MANUAL,
                                onClick = { orderMenuOpen = true },
                            )
                        }
                        DropdownMenu(
                            expanded = orderMenuOpen,
                            onDismissRequest = { orderMenuOpen = false },
                        ) {
                            CharacterPresentationOrder.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(if (option == CharacterPresentationOrder.MANUAL) "Manual" else "A–Z") },
                                    onClick = {
                                        onOrderChange(option)
                                        orderMenuOpen = false
                                    },
                                )
                            }
                        }
                    }
                }

                if (filters.isNotEmpty()) {
                    Box {
                        CharacterToolbarChipV4(
                            text = if (activeFilterCount == 0) "Filtros" else "Filtros $activeFilterCount",
                            selected = activeFilterCount > 0,
                            onClick = { filterMenuOpen = true },
                        )
                        DropdownMenu(
                            expanded = filterMenuOpen,
                            onDismissRequest = { filterMenuOpen = false },
                        ) {
                            filters.forEach { filter ->
                                val active = filter.key in query.activeFilterKeys
                                val countSuffix = filter.count?.let { " ($it)" }.orEmpty()
                                DropdownMenuItem(
                                    text = { Text("${if (active) "✓ " else ""}${filter.label}$countSuffix") },
                                    onClick = { onQueryChange(query.toggleFilter(filter.key)) },
                                )
                            }
                        }
                    }
                }

                if (onAdd != null) {
                    CharacterToolbarChipV4(text = "+", selected = false, onClick = onAdd)
                }
            }
        }
    }
}
'''
text = text[:start] + new_toolbar
p.write_text(text, encoding='utf-8')


p = ROOT / 'CharacterSpellListClosureV4.kt'
text = p.read_text(encoding='utf-8')
text = replace_once(
    text,
    '''            collapsibleSearch = true,
            showItemCount = false,
            modifier = Modifier
''',
    '''            collapsibleSearch = true,
            showItemCount = false,
            compactOrderControl = true,
            modifier = Modifier
''',
    'spell toolbar compact sort opt-in',
)

# Spell card: one compact title/action row, no three-level trailing tower.
start = text.index('@Composable\nprivate fun SpellRowG2(')
end = text.index('\n@Composable\nprivate fun SpellBadgeG2', start)
old_fn = text[start:end]
signature_end = old_fn.index(') {') + 3
signature = old_fn[:signature_end]
body = '''
    var accumulatedDrag by remember(spell.id) { mutableStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
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
                .characterMeasuredReorderDragV4(
                    enabled = reorderEnabled,
                    onHaptic = onHaptic,
                    onMove = onMove,
                    onVisualStateChange = { state ->
                        dragging = state.active
                        accumulatedDrag = state.offsetY
                    },
                )
                .characterDragFeedbackV4(dragState)
                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
            ),
            color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(
                    horizontal = appSpacingV4(5.dp),
                    vertical = appSpacingV4(4.dp),
                ),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
                ) {
                    Text(
                        spell.name.ifBlank { "Conjuro sin nombre" },
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
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
                    StableFavoriteIconButton(
                        selected = favorite,
                        onClick = { onFavoriteChange(!favorite) },
                        enabled = structuralEditingEnabled && favoriteEnabled,
                        contentDescription = if (favorite) "Quitar ${spell.name} de Favoritos" else "Añadir ${spell.name} a Favoritos",
                    )
                    if (structuralEditingEnabled) {
                        StableDuplicateIconButton(onClick = onDuplicate, contentDescription = "Duplicar ${spell.name}")
                        StableRemoveIconButton(onClick = onDelete, contentDescription = "Eliminar ${spell.name}")
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (spell.verbal) SpellBadgeG2("V")
                    if (spell.somatic) SpellBadgeG2("S")
                    if (spell.material) SpellBadgeG2("M")
                    if (spell.concentration) SpellBadgeG2("Concentración", state = true)
                    if (spell.ritual) SpellBadgeG2("Ritual", state = true)
                    if (selectedSourceId != null && selectedAssociation?.prepared == true) SpellBadgeG2("Preparado", state = true)
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
                        sourceById[association.sourceId]?.name?.let { sourceName ->
                            "$sourceName ${if (association.prepared) "✓" else "○"}"
                        }
                    }.joinToString(" · ")
                    if (sourceState.isNotBlank()) {
                        Text(sourceState, style = MaterialTheme.typography.labelSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
        CharacterDropIndicatorV4(visible = dragState.showDropAfter)
    }
}
'''
text = text[:start] + signature + body + text[end:]

# Spell editor: compact short/reference peers, prose stays full width.
start = text.index('@Composable\nprivate fun SpellEditorFieldsG2(')
end = text.index('\nprivate fun spellFiltersG2', start)
old_editor = text[start:end]
signature_end = old_editor.index(') {') + 3
signature = old_editor[:signature_end]
editor_body = '''
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Nombre") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
    CharacterCompactFieldRowV4(
        firstWeight = 0.65f,
        secondWeight = 1.35f,
        first = { fieldModifier ->
            OutlinedTextField(
                value = level,
                onValueChange = onLevelChange,
                label = { Text("Nivel (0-9)") },
                modifier = fieldModifier,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        },
        second = { fieldModifier ->
            OutlinedTextField(
                value = castingTime,
                onValueChange = onCastingTimeChange,
                label = { Text("Tiempo de lanzamiento") },
                modifier = fieldModifier,
                singleLine = true,
            )
        },
    )
    CharacterCompactFieldRowV4(
        first = { fieldModifier ->
            OutlinedTextField(
                value = rangeText,
                onValueChange = onRangeTextChange,
                label = { Text("Alcance") },
                modifier = fieldModifier,
                singleLine = true,
            )
        },
        second = { fieldModifier ->
            OutlinedTextField(
                value = duration,
                onValueChange = onDurationChange,
                label = { Text("Duración") },
                modifier = fieldModifier,
                singleLine = true,
            )
        },
    )
    Text("Fuentes", style = MaterialTheme.typography.titleSmall)
    if (sources.isEmpty()) {
        Text("Crea al menos una fuente antes de guardar un conjuro.", style = MaterialTheme.typography.bodySmall)
    }
    sources.forEach { source ->
        val key = source.id.toString()
        val included = key in associatedSourceIds
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = included, onCheckedChange = { onAssociationChange(source.id, it) })
            Text(source.name, modifier = Modifier.weight(1f))
            Checkbox(
                checked = key in preparedSourceIds,
                enabled = included,
                onCheckedChange = { onPreparedChange(source.id, it) },
            )
            Text("Preparado", style = MaterialTheme.typography.labelSmall)
        }
    }
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
'''
text = text[:start] + signature + editor_body + text[end:]
p.write_text(text, encoding='utf-8')
