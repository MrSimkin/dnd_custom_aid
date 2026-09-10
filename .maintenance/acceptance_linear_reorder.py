from pathlib import Path

def replace_once(path: str, old: str, new: str) -> None:
    p = Path(path)
    text = p.read_text()
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{path}: expected one match, found {count}: {old[:120]!r}")
    p.write_text(text.replace(old, new, 1))

collection = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCollectionPrimitivesV4.kt"
replace_once(
    collection,
    '''@Composable
private fun CharacterCompactSearchV4(''',
    '''@Composable
internal fun CharacterLinearReorderModeControlV4(
    active: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CharacterToolbarChipV4(
        text = if (active) "Listo" else "Reordenar",
        selected = active,
        onClick = onToggle,
        modifier = modifier,
    )
}

@Composable
private fun CharacterCompactSearchV4(''',
)

notes = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterNotesTabV4.kt"
replace_once(
    notes,
    '''    var orderName by rememberSaveable("note-order") { mutableStateOf(CharacterPresentationOrder.MANUAL.name) }
    var editorOpen''',
    '''    var orderName by rememberSaveable("note-order") { mutableStateOf(CharacterPresentationOrder.MANUAL.name) }
    var reorderMode by rememberSaveable("note-linear-reorder") { mutableStateOf(false) }
    var editorOpen''',
)
replace_once(
    notes,
    '''    val visibleCards = presentCharacterNotes(draft.cards, order, query)
    val canReorder = structuralEditingEnabled && order == CharacterPresentationOrder.MANUAL &&
        query.searchText.isBlank() && query.activeFilterKeys.isEmpty()
    val noteColumns = constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)''',
    '''    val visibleCards = presentCharacterNotes(draft.cards, order, query)
    val reorderAvailable = structuralEditingEnabled && order == CharacterPresentationOrder.MANUAL &&
        query.searchText.isBlank() && query.activeFilterKeys.isEmpty()
    val canReorder = reorderAvailable && reorderMode
    val noteColumns = if (reorderMode) 1 else constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)''',
)
replace_once(
    notes,
    '''    fun updateQuery(updated: CharacterCollectionQuery) {
        searchText = updated.searchText
        activeFiltersText = updated.activeFilterKeys.sorted().joinToString(NOTE_FILTER_SEPARATOR_G3)
    }''',
    '''    fun updateQuery(updated: CharacterCollectionQuery) {
        reorderMode = false
        searchText = updated.searchText
        activeFiltersText = updated.activeFilterKeys.sorted().joinToString(NOTE_FILTER_SEPARATOR_G3)
    }''',
)
replace_once(
    notes,
    '''                order = order,
                onOrderChange = { orderName = it.name },''',
    '''                order = order,
                onOrderChange = {
                    reorderMode = false
                    orderName = it.name
                },''',
)
replace_once(
    notes,
    '''            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp))) {
                CharacterHelpV4("Tarjetas opcionales para separar referencias concretas. La búsqueda revisa título y contenido.")
                if (!canReorder && visibleCards.isNotEmpty()) {
                    Text(
                        if (order == CharacterPresentationOrder.ALPHABETICAL) {
                            "A–Z es solo una vista. Vuelve a Manual para arrastrar sin perder el orden guardado."
                        } else {
                            "Limpia búsqueda y filtros para reordenar manualmente."
                        },
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }''',
    '''            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp))) {
                CharacterHelpV4("Tarjetas opcionales para separar referencias concretas. La búsqueda revisa título y contenido.")
                if (reorderAvailable && visibleCards.size > 1) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        CharacterLinearReorderModeControlV4(
                            active = reorderMode,
                            onToggle = { reorderMode = !reorderMode },
                        )
                    }
                    if (reorderMode) {
                        CharacterHelpV4(
                            "Reordenación lineal: las notas pasan temporalmente a una columna para usar el arrastre vertical estable. Pulsa Listo para volver a tus columnas.",
                        )
                    }
                } else if (visibleCards.isNotEmpty()) {
                    Text(
                        if (order == CharacterPresentationOrder.ALPHABETICAL) {
                            "A–Z es solo una vista. Vuelve a Manual para reordenar sin perder el orden guardado."
                        } else {
                            "Limpia búsqueda y filtros para activar Reordenar."
                        },
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }''',
)

traits = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterTraitsClosureV4.kt"
replace_once(
    traits,
    '''    var groupingName by rememberSaveable { mutableStateOf(CharacterTraitGrouping.TYPE.name) }

    var editorOpen''',
    '''    var groupingName by rememberSaveable { mutableStateOf(CharacterTraitGrouping.TYPE.name) }
    var reorderMode by rememberSaveable("trait-linear-reorder") { mutableStateOf(false) }

    var editorOpen''',
)
replace_once(
    traits,
    '''    val groups = groupCharacterTraits(visibleTraits, grouping)
    val canReorder = structuralEditingEnabled && query.searchText.isBlank() && query.activeFilterKeys.isEmpty()

    fun updateQuery(updated: CharacterCollectionQuery) {
        searchText = updated.searchText
        activeFiltersText = updated.activeFilterKeys.sorted().joinToString(TRAIT_FILTER_SEPARATOR_G1)
    }''',
    '''    val groups = groupCharacterTraits(visibleTraits, grouping)
    val reorderAvailable = structuralEditingEnabled && query.searchText.isBlank() && query.activeFilterKeys.isEmpty()
    val canReorder = reorderAvailable && reorderMode

    fun updateQuery(updated: CharacterCollectionQuery) {
        reorderMode = false
        searchText = updated.searchText
        activeFiltersText = updated.activeFilterKeys.sorted().joinToString(TRAIT_FILTER_SEPARATOR_G1)
    }''',
)
replace_once(
    traits,
    '''                    TraitGroupingControlsG1(
                        grouping = grouping,
                        onGroupingChange = { groupingName = it.name },
                    )
                    if (!canReorder && visibleTraits.isNotEmpty()) {
                        Text(
                            "Limpia búsqueda y filtros para reordenar manualmente.",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }''',
    '''                    TraitGroupingControlsG1(
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
                    }''',
)
replace_once(
    traits,
    '''                            val columns = constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)''',
    '''                            val columns = if (reorderMode) {
                                1
                            } else {
                                constrainedCardColumnsV4(wide = wide, phoneMax = 2, wideMax = 4)
                            }''',
)

equipment = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEquipmentClosureV4.kt"
replace_once(
    equipment,
    '''    var ordinaryCollapsed by rememberSaveable { mutableStateOf(false) }
    var specialCollapsed by rememberSaveable { mutableStateOf(false) }

    var editorOpen''',
    '''    var ordinaryCollapsed by rememberSaveable { mutableStateOf(false) }
    var specialCollapsed by rememberSaveable { mutableStateOf(false) }
    var ordinaryReorderMode by rememberSaveable("equipment-ordinary-linear-reorder") { mutableStateOf(false) }
    var specialReorderMode by rememberSaveable("equipment-special-linear-reorder") { mutableStateOf(false) }

    var editorOpen''',
)
replace_once(
    equipment,
    '''    val canReorderOrdinary = structuralEditingEnabled && ordinaryOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()
    val canReorderSpecial = structuralEditingEnabled && specialOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()''',
    '''    val reorderAvailableOrdinary =
        structuralEditingEnabled && ordinaryOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()
    val reorderAvailableSpecial =
        structuralEditingEnabled && specialOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()''',
)
replace_once(
    equipment,
    '''    fun updateQuery(updated: CharacterCollectionQuery) {
        searchText = updated.searchText
        activeFiltersText = updated.activeFilterKeys.sorted().joinToString("|")
    }''',
    '''    fun updateQuery(updated: CharacterCollectionQuery) {
        ordinaryReorderMode = false
        specialReorderMode = false
        searchText = updated.searchText
        activeFiltersText = updated.activeFilterKeys.sorted().joinToString("|")
    }''',
)
replace_once(
    equipment,
    '''                order = ordinaryOrder,
                onOrderChange = { ordinaryOrderName = it.name },
                collapsed = ordinaryCollapsed,
                onCollapsedChange = { ordinaryCollapsed = it },
                canReorder = canReorderOrdinary,
                queryActive = !query.isEmptyF2(),''',
    '''                order = ordinaryOrder,
                onOrderChange = {
                    ordinaryReorderMode = false
                    ordinaryOrderName = it.name
                },
                collapsed = ordinaryCollapsed,
                onCollapsedChange = { ordinaryCollapsed = it },
                reorderAvailable = reorderAvailableOrdinary,
                reorderMode = ordinaryReorderMode,
                onReorderModeChange = { active ->
                    ordinaryReorderMode = active
                    if (active) specialReorderMode = false
                },
                queryActive = !query.isEmptyF2(),''',
)
replace_once(
    equipment,
    '''                order = specialOrder,
                onOrderChange = { specialOrderName = it.name },
                collapsed = specialCollapsed,
                onCollapsedChange = { specialCollapsed = it },
                canReorder = canReorderSpecial,
                queryActive = !query.isEmptyF2(),''',
    '''                order = specialOrder,
                onOrderChange = {
                    specialReorderMode = false
                    specialOrderName = it.name
                },
                collapsed = specialCollapsed,
                onCollapsedChange = { specialCollapsed = it },
                reorderAvailable = reorderAvailableSpecial,
                reorderMode = specialReorderMode,
                onReorderModeChange = { active ->
                    specialReorderMode = active
                    if (active) ordinaryReorderMode = false
                },
                queryActive = !query.isEmptyF2(),''',
)
replace_once(
    equipment,
    '''    collapsed: Boolean,
    onCollapsedChange: (Boolean) -> Unit,
    canReorder: Boolean,
    queryActive: Boolean,''',
    '''    collapsed: Boolean,
    onCollapsedChange: (Boolean) -> Unit,
    reorderAvailable: Boolean,
    reorderMode: Boolean,
    onReorderModeChange: (Boolean) -> Unit,
    queryActive: Boolean,''',
)
replace_once(
    equipment,
    '''                TextButton(onClick = { onCollapsedChange(!collapsed) }) {
                    Text(if (collapsed) "Mostrar" else "Ocultar")
                }
            }
            if (order == CharacterPresentationOrder.MANUAL && queryActive) {
                Text(
                    "Limpia búsqueda y filtros para reordenar manualmente.",
                    style = MaterialTheme.typography.labelSmall,
                )
            }''',
    '''                if (reorderAvailable && items.size > 1) {
                    CharacterLinearReorderModeControlV4(
                        active = reorderMode,
                        onToggle = { onReorderModeChange(!reorderMode) },
                    )
                }
                TextButton(onClick = { onCollapsedChange(!collapsed) }) {
                    Text(if (collapsed) "Mostrar" else "Ocultar")
                }
            }
            if (reorderMode) {
                CharacterHelpV4(
                    "Reordenación lineal: esta sección pasa temporalmente a una columna para usar el arrastre vertical estable. Pulsa Listo para volver a tus columnas.",
                )
            } else if (order == CharacterPresentationOrder.MANUAL && queryActive) {
                Text(
                    "Limpia búsqueda y filtros para activar Reordenar.",
                    style = MaterialTheme.typography.labelSmall,
                )
            }''',
)
replace_once(
    equipment,
    '''                    val columns = constrainedCardColumnsV4(
                        wide = wide,
                        phoneMax = if (special) 2 else 3,
                        wideMax = if (special) 3 else 5,
                    )''',
    '''                    val columns = if (reorderMode) {
                        1
                    } else {
                        constrainedCardColumnsV4(
                            wide = wide,
                            phoneMax = if (special) 2 else 3,
                            wideMax = if (special) 3 else 5,
                        )
                    }''',
)
replace_once(
    equipment,
    '''                                    canReorder = canReorder,''',
    '''                                    canReorder = reorderAvailable && reorderMode,''',
)

for path in (notes, traits, equipment):
    if "reorderMode" not in Path(path).read_text():
        raise SystemExit(f"{path}: reorder mode missing")
