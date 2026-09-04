from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one anchor, found {count}")
    return text.replace(old, new, 1)


def patch(path_str: str, transforms):
    path = Path(path_str)
    text = path.read_text()
    for old, new, label in transforms:
        text = replace_once(text, old, new, label)
    path.write_text(text)


# Editor boundary: derive one policy, guard structural-only draft stores, and route mixed surfaces.
path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
text = path.read_text()
text = replace_once(
    text,
    "import io.github.mrsimkin.dndcustomaid.shared.character.abilityModifierForScore\n",
    "import io.github.mrsimkin.dndcustomaid.shared.character.abilityModifierForScore\nimport io.github.mrsimkin.dndcustomaid.shared.character.isCharacterStructuralEditingEnabled\n",
    "editor policy import",
)
text = replace_once(
    text,
    "    val visibleModules = visibleCharacterModules(settingsSheet.classes, closureState.moduleOverrides)\n",
    "    val visibleModules = visibleCharacterModules(settingsSheet.classes, closureState.moduleOverrides)\n    val structuralEditingEnabled = isCharacterStructuralEditingEnabled(closureState.tableModeEnabled)\n",
    "editor structural policy",
)
for function_anchor, label in [
    ("    fun updateCombatEntries(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry>) {\n", "combat write guard"),
    ("    fun updateBackground(updated: io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground) {\n", "background write guard"),
    ("    fun updateSpellcasting(updated: CharacterSpellcastingDraftV4) {\n", "spellcasting write guard"),
    ("    fun updateNotes(updated: CharacterNotesDraftV4) {\n", "notes write guard"),
    ("    fun updateH1Modules(updated: CharacterH1ModuleDraftV4) {\n", "module write guard"),
]:
    text = replace_once(
        text,
        function_anchor,
        function_anchor + "        if (!structuralEditingEnabled) return\n",
        label,
    )
text = replace_once(
    text,
    "    fun updateDraft(updated: CharacterEditorDraftV4) {\n        draft = updated\n        savedMessage = null\n    }\n\n",
    "    fun updateDraft(updated: CharacterEditorDraftV4) {\n        draft = updated\n        savedMessage = null\n    }\n\n    fun updateStructuralDraft(updated: CharacterEditorDraftV4) {\n        if (!structuralEditingEnabled) return\n        updateDraft(updated)\n    }\n\n",
    "structural draft wrapper",
)
text = replace_once(
    text,
    "    fun persistClosureState(updated: CharacterClosureState) {\n        if (updated == closureState) return\n        closureState = closureRepository.saveState(characterId, updated)\n        savedMessage = \"Guardado\"\n    }\n\n",
    "    fun persistClosureState(updated: CharacterClosureState) {\n        if (updated == closureState) return\n        closureState = closureRepository.saveState(characterId, updated)\n        savedMessage = \"Guardado\"\n    }\n\n    fun persistStructuralClosureState(updated: CharacterClosureState) {\n        if (!structuralEditingEnabled) return\n        persistClosureState(updated)\n    }\n\n",
    "structural closure wrapper",
)
text = replace_once(
    text,
    "                            savable = savable,\n                            onBack = ::requestBack,\n",
    "                            savable = savable,\n                            tableModeEnabled = closureState.tableModeEnabled,\n                            onBack = ::requestBack,\n",
    "header table flag",
)
text = replace_once(
    text,
    "                            onDraftChange = ::updateDraft,\n                            onClosureStateChange = ::persistClosureState,\n",
    "                            onDraftChange = ::updateStructuralDraft,\n                            onClosureStateChange = ::persistStructuralClosureState,\n",
    "overview structural callbacks",
)
text = replace_once(
    text,
    "                            onDraftChange = ::updateDraft,\n                            onClosureStateChange = ::persistClosureState,\n                        )\n                        CharacterTabV4.COMBAT",
    "                            onDraftChange = ::updateStructuralDraft,\n                            onClosureStateChange = ::persistStructuralClosureState,\n                        )\n                        CharacterTabV4.COMBAT",
    "skills structural callbacks",
)
text = replace_once(
    text,
    "                            onEntriesChange = ::updateCombatEntries,\n                            onOperationalSheetChange = ::persistCombatOperationalSheet,\n                            onClosureStateChange = ::persistClosureState,\n                            hapticsEnabled = closureState.hapticsEnabled,\n",
    "                            onEntriesChange = ::updateCombatEntries,\n                            onOperationalSheetChange = ::persistCombatOperationalSheet,\n                            onClosureStateChange = ::persistStructuralClosureState,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            hapticsEnabled = closureState.hapticsEnabled,\n",
    "combat policy wiring",
)
text = replace_once(
    text,
    "                            onClosureStateChange = ::persistClosureState,\n                            wide = wide,\n                            hapticsEnabled = closureState.hapticsEnabled,\n                        )\n                        CharacterTabV4.EQUIPMENT",
    "                            onClosureStateChange = ::persistClosureState,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            wide = wide,\n                            hapticsEnabled = closureState.hapticsEnabled,\n                        )\n                        CharacterTabV4.EQUIPMENT",
    "management policy wiring",
)
text = replace_once(
    text,
    "                            onDraftChange = ::updateEquipmentDraft,\n                            wide = wide,\n",
    "                            onDraftChange = ::updateEquipmentDraft,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            wide = wide,\n",
    "equipment policy wiring",
)
text = replace_once(
    text,
    "                            onBackgroundChange = ::updateBackground,\n",
    "                            onBackgroundChange = ::updateBackground,\n",
    "background callback anchor",
)
text = replace_once(
    text,
    "                            onTraitsChange = ::updateTraits,\n                            onClosureStateChange = ::persistClosureState,\n                            wide = wide,\n",
    "                            onTraitsChange = ::updateTraits,\n                            onClosureStateChange = ::persistStructuralClosureState,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            wide = wide,\n",
    "traits policy wiring",
)
text = replace_once(
    text,
    "                            onDraftChange = ::updateSpellcasting,\n                            onSlotSpentChange = { level, spent ->\n",
    "                            onDraftChange = ::updateSpellcasting,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            onSlotSpentChange = { level, spent ->\n",
    "spells policy wiring",
)
text = replace_once(
    text,
    "                            onClosureStateChange = ::persistClosureState,\n                            wide = wide,\n                            hapticsEnabled = closureState.hapticsEnabled,\n                        )\n                        CharacterTabV4.ARTIFICER",
    "                            onClosureStateChange = ::persistStructuralClosureState,\n                            wide = wide,\n                            hapticsEnabled = closureState.hapticsEnabled,\n                        )\n                        CharacterTabV4.ARTIFICER",
    "spells closure structural guard",
)
text = replace_once(
    text,
    "    savable: Boolean,\n    onBack: () -> Unit,\n",
    "    savable: Boolean,\n    tableModeEnabled: Boolean,\n    onBack: () -> Unit,\n",
    "header signature",
)
text = replace_once(
    text,
    "            )\n        }\n        StableSettingsIconButton(onClick = onOpenSettings)\n",
    "            )\n            if (tableModeEnabled) {\n                Text(\n                    \"Modo Mesa · edición estructural bloqueada\",\n                    style = MaterialTheme.typography.labelSmall,\n                    maxLines = 1,\n                )\n            }\n        }\n        StableSettingsIconButton(onClick = onOpenSettings)\n",
    "header table notice",
)
path.write_text(text)


# Combat: lock entry structure/Favorites while preserving operational HP/death-save/d20 controls.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatTabV4.kt",
    [
        (
            "    onClosureStateChange: (CharacterClosureState) -> Unit,\n    wide: Boolean,\n",
            "    onClosureStateChange: (CharacterClosureState) -> Unit,\n    structuralEditingEnabled: Boolean,\n    wide: Boolean,\n",
            "combat signature",
        ),
        (
            "    fun beginAdd() {\n",
            "    fun beginAdd() {\n        if (!structuralEditingEnabled) return\n",
            "combat add guard",
        ),
        (
            "    fun beginEdit(entry: CharacterCombatEntry) {\n",
            "    fun beginEdit(entry: CharacterCombatEntry) {\n        if (!structuralEditingEnabled) return\n",
            "combat edit guard",
        ),
        (
            "    fun move(index: Int, offset: Int): Boolean {\n",
            "    fun move(index: Int, offset: Int): Boolean {\n        if (!structuralEditingEnabled) return false\n",
            "combat reorder guard",
        ),
        (
            "                        TextButton(onClick = ::beginAdd) { Text(\"+ Añadir\") }\n",
            "                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "combat add affordance",
        ),
        (
            "                                        favoriteEnabled = entry.id in persistedEntryIds,\n",
            "                                        favoriteEnabled = structuralEditingEnabled && entry.id in persistedEntryIds,\n",
            "combat favorite lock",
        ),
        (
            "                                        onHaptic = haptic,\n                                        modifier = Modifier.weight(1f),\n",
            "                                        structuralEditingEnabled = structuralEditingEnabled,\n                                        onHaptic = haptic,\n                                        modifier = Modifier.weight(1f),\n",
            "combat card policy",
        ),
        (
            "    onDelete: () -> Unit,\n    onHaptic: (CharacterHapticEventV4) -> Unit,\n",
            "    onDelete: () -> Unit,\n    structuralEditingEnabled: Boolean,\n    onHaptic: (CharacterHapticEventV4) -> Unit,\n",
            "combat card signature",
        ),
        (
            "                .characterDragFeedbackV4(dragState)\n                .clickable(onClick = onEdit),\n",
            "                .characterDragFeedbackV4(dragState)\n                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),\n",
            "combat edit clickable",
        ),
        (
            "                    StableRemoveIconButton(\n                        onClick = onDelete,\n                        contentDescription = \"Eliminar ${entry.name}\",\n                    )\n",
            "                    if (structuralEditingEnabled) {\n                        StableRemoveIconButton(\n                            onClick = onDelete,\n                            contentDescription = \"Eliminar ${entry.name}\",\n                        )\n                    }\n",
            "combat remove affordance",
        ),
        (
            "    if (editorOpen) {\n",
            "    if (editorOpen && structuralEditingEnabled) {\n",
            "combat editor suppression",
        ),
        (
            "    deleteId?.let { id ->\n",
            "    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->\n",
            "combat delete suppression",
        ),
    ],
)


# Traits: use meters remain operational; collection structure/Favorites lock.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterTraitsClosureV4.kt",
    [
        (
            "    onClosureStateChange: (CharacterClosureState) -> Unit,\n    wide: Boolean,\n",
            "    onClosureStateChange: (CharacterClosureState) -> Unit,\n    structuralEditingEnabled: Boolean,\n    wide: Boolean,\n",
            "traits signature",
        ),
        (
            "    val canReorder = query.searchText.isBlank() && query.activeFilterKeys.isEmpty()\n",
            "    val canReorder = structuralEditingEnabled && query.searchText.isBlank() && query.activeFilterKeys.isEmpty()\n",
            "traits reorder policy",
        ),
        (
            "    fun beginAdd() {\n",
            "    fun beginAdd() {\n        if (!structuralEditingEnabled) return\n",
            "traits add guard",
        ),
        (
            "    fun beginEdit(trait: CharacterTrait) {\n",
            "    fun beginEdit(trait: CharacterTrait) {\n        if (!structuralEditingEnabled) return\n",
            "traits edit guard",
        ),
        (
            "    fun duplicate(trait: CharacterTrait) {\n",
            "    fun duplicate(trait: CharacterTrait) {\n        if (!structuralEditingEnabled) return\n",
            "traits duplicate guard",
        ),
        (
            "                        TextButton(onClick = ::beginAdd) { Text(\"+ Añadir\") }\n",
            "                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "traits add affordance",
        ),
        (
            "                                            favoriteEnabled = trait.id in persistedTraitIds,\n",
            "                                            favoriteEnabled = structuralEditingEnabled && trait.id in persistedTraitIds,\n",
            "traits favorite lock",
        ),
        (
            "                                            canReorder = canReorder,\n",
            "                                            canReorder = canReorder,\n                                            structuralEditingEnabled = structuralEditingEnabled,\n",
            "traits card policy",
        ),
        (
            "    canReorder: Boolean,\n    onFavoriteChange: (Boolean) -> Unit,\n",
            "    canReorder: Boolean,\n    structuralEditingEnabled: Boolean,\n    onFavoriteChange: (Boolean) -> Unit,\n",
            "trait card signature",
        ),
        (
            "                .characterDragFeedbackV4(dragState)\n                .clickable(onClick = onEdit),\n",
            "                .characterDragFeedbackV4(dragState)\n                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),\n",
            "trait edit clickable",
        ),
        (
            "                    TextButton(\n                        onClick = onDuplicate,\n                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),\n                    ) { Text(\"Duplicar\") }\n                    StableRemoveIconButton(onClick = onDelete, contentDescription = \"Eliminar ${trait.name}\")\n",
            "                    if (structuralEditingEnabled) {\n                        TextButton(\n                            onClick = onDuplicate,\n                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),\n                        ) { Text(\"Duplicar\") }\n                        StableRemoveIconButton(onClick = onDelete, contentDescription = \"Eliminar ${trait.name}\")\n                    }\n",
            "trait structural actions",
        ),
        (
            "    if (editorOpen) {\n",
            "    if (editorOpen && structuralEditingEnabled) {\n",
            "trait editor suppression",
        ),
        (
            "    deleteId?.let { id ->\n",
            "    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->\n",
            "trait delete suppression",
        ),
    ],
)


# Equipment: quick-use and currency amounts remain live; object structure and currency add/delete lock.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEquipmentClosureV4.kt",
    [
        (
            "    onDraftChange: (CharacterEquipmentDraftV4) -> Unit,\n    wide: Boolean,\n",
            "    onDraftChange: (CharacterEquipmentDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean,\n    wide: Boolean,\n",
            "equipment signature",
        ),
        (
            "    val canReorderOrdinary = ordinaryOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()\n    val canReorderSpecial = specialOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()\n",
            "    val canReorderOrdinary = structuralEditingEnabled && ordinaryOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()\n    val canReorderSpecial = structuralEditingEnabled && specialOrder == CharacterPresentationOrder.MANUAL && query.isEmptyF2()\n",
            "equipment reorder policy",
        ),
        (
            "    fun beginAdd() {\n",
            "    fun beginAdd() {\n        if (!structuralEditingEnabled) return\n",
            "equipment add guard",
        ),
        (
            "    fun beginEdit(item: CharacterInventoryItem) {\n",
            "    fun beginEdit(item: CharacterInventoryItem) {\n        if (!structuralEditingEnabled) return\n",
            "equipment edit guard",
        ),
        (
            "    fun moveWithinSection(item: CharacterInventoryItem, offset: Int): Boolean {\n",
            "    fun moveWithinSection(item: CharacterInventoryItem, offset: Int): Boolean {\n        if (!structuralEditingEnabled) return false\n",
            "equipment move guard",
        ),
        (
            "    fun duplicate(item: CharacterInventoryItem) {\n",
            "    fun duplicate(item: CharacterInventoryItem) {\n        if (!structuralEditingEnabled) return\n",
            "equipment duplicate guard",
        ),
        (
            "                        TextButton(onClick = ::beginAdd) { Text(\"+ Añadir\") }\n",
            "                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "equipment add affordance",
        ),
        (
            "                onHaptic = haptic,\n            )\n        }\n\n        item {\n            EquipmentSectionF2(\n                title = \"Equipo especial\"",
            "                structuralEditingEnabled = structuralEditingEnabled,\n                onHaptic = haptic,\n            )\n        }\n\n        item {\n            EquipmentSectionF2(\n                title = \"Equipo especial\"",
            "ordinary section policy",
        ),
        (
            "                onHaptic = haptic,\n            )\n        }\n\n        item {\n            CompactCurrenciesF2(\n",
            "                structuralEditingEnabled = structuralEditingEnabled,\n                onHaptic = haptic,\n            )\n        }\n\n        item {\n            CompactCurrenciesF2(\n",
            "special section policy",
        ),
        (
            "                wide = wide,\n                onCurrenciesChange = { onDraftChange(draft.copy(currencies = it)) },\n",
            "                wide = wide,\n                structuralEditingEnabled = structuralEditingEnabled,\n                onCurrenciesChange = { onDraftChange(draft.copy(currencies = it)) },\n",
            "currency policy wiring",
        ),
        (
            "        if (wide) {\n            EquipmentEditorPanelF3(\n",
            "        if (wide && structuralEditingEnabled) {\n            EquipmentEditorPanelF3(\n",
            "wide editor suppression",
        ),
        (
            "    if (editorOpen && !wide) {\n",
            "    if (editorOpen && !wide && structuralEditingEnabled) {\n",
            "phone editor suppression",
        ),
        (
            "    deleteId?.let { id ->\n",
            "    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->\n",
            "equipment delete suppression",
        ),
        (
            "    if (addCurrencyOpen) {\n",
            "    if (addCurrencyOpen && structuralEditingEnabled) {\n",
            "currency add suppression",
        ),
        (
            "    onDelete: (CharacterInventoryItem) -> Unit,\n    onHaptic: (CharacterHapticEventV4) -> Unit,\n",
            "    onDelete: (CharacterInventoryItem) -> Unit,\n    structuralEditingEnabled: Boolean,\n    onHaptic: (CharacterHapticEventV4) -> Unit,\n",
            "equipment section signature",
        ),
        (
            "                                    onDelete = { onDelete(item) },\n                                    onHaptic = onHaptic,\n",
            "                                    onDelete = { onDelete(item) },\n                                    structuralEditingEnabled = structuralEditingEnabled,\n                                    onHaptic = onHaptic,\n",
            "equipment dense policy",
        ),
        (
            "    onDelete: () -> Unit,\n    onHaptic: (CharacterHapticEventV4) -> Unit,\n",
            "    onDelete: () -> Unit,\n    structuralEditingEnabled: Boolean,\n    onHaptic: (CharacterHapticEventV4) -> Unit,\n",
            "dense item signature",
        ),
        (
            "                .characterDragFeedbackV4(dragState)\n                .clickable(onClick = onEdit),\n",
            "                .characterDragFeedbackV4(dragState)\n                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),\n",
            "equipment edit clickable",
        ),
        (
            "                    TextButton(\n                        onClick = onDuplicate,\n                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),\n                    ) { Text(\"Duplicar\") }\n                    StableRemoveIconButton(onClick = onDelete, contentDescription = \"Eliminar ${item.name}\")\n",
            "                    if (structuralEditingEnabled) {\n                        TextButton(\n                            onClick = onDuplicate,\n                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),\n                        ) { Text(\"Duplicar\") }\n                        StableRemoveIconButton(onClick = onDelete, contentDescription = \"Eliminar ${item.name}\")\n                    }\n",
            "equipment structural actions",
        ),
        (
            "    wide: Boolean,\n    onCurrenciesChange: (List<CharacterCurrency>) -> Unit,\n",
            "    wide: Boolean,\n    structuralEditingEnabled: Boolean,\n    onCurrenciesChange: (List<CharacterCurrency>) -> Unit,\n",
            "currencies signature",
        ),
        (
            "                TextButton(onClick = onAddCurrency) { Text(\"+ Añadir\") }\n",
            "                TextButton(onClick = onAddCurrency, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "currency add affordance",
        ),
        (
            "                                onDelete = if (currency.isDefault) null else {\n",
            "                                onDelete = if (currency.isDefault || !structuralEditingEnabled) null else {\n",
            "currency delete lock",
        ),
    ],
)


# Management: session state remains live; only resource-definition structure locks.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterManagementTabV4.kt",
    [
        (
            "    onClosureStateChange: (CharacterClosureState) -> Unit,\n    wide: Boolean,\n",
            "    onClosureStateChange: (CharacterClosureState) -> Unit,\n    structuralEditingEnabled: Boolean,\n    wide: Boolean,\n",
            "management signature",
        ),
        (
            "                resources = sheet.resources,\n                onAdd = {\n",
            "                resources = sheet.resources,\n                structuralEditingEnabled = structuralEditingEnabled,\n                onAdd = {\n",
            "resources policy wiring",
        ),
        (
            "    resources: List<CharacterResource>,\n    onAdd: () -> Unit,\n",
            "    resources: List<CharacterResource>,\n    structuralEditingEnabled: Boolean,\n    onAdd: () -> Unit,\n",
            "resources card signature",
        ),
        (
            "                onAdd = onAdd,\n                addLabel = \"Añadir recurso\",\n",
            "                onAdd = if (structuralEditingEnabled) onAdd else null,\n                addLabel = \"Añadir recurso\",\n",
            "resource empty add lock",
        ),
        (
            "                    TextButton(onClick = { onEdit(resource) }, modifier = Modifier.weight(1f)) {\n",
            "                    TextButton(onClick = { onEdit(resource) }, enabled = structuralEditingEnabled, modifier = Modifier.weight(1f)) {\n",
            "resource edit lock",
        ),
        (
            "                    TextButton(onClick = { onDelete(resource) }) { Text(\"Eliminar\") }\n",
            "                    TextButton(onClick = { onDelete(resource) }, enabled = structuralEditingEnabled) { Text(\"Eliminar\") }\n",
            "resource delete lock",
        ),
        (
            "            TextButton(onClick = onAdd) { Text(\"+ Añadir recurso\") }\n",
            "            TextButton(onClick = onAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir recurso\") }\n",
            "resource add lock",
        ),
        (
            "    if (resourceEditorOpen) {\n",
            "    if (resourceEditorOpen && structuralEditingEnabled) {\n",
            "resource editor suppression",
        ),
        (
            "    deletingResourceId?.let { id ->\n",
            "    deletingResourceId?.takeIf { structuralEditingEnabled }?.let { id ->\n",
            "resource delete suppression",
        ),
    ],
)


# Spells/source manager: structural collection/prepared/Favorite actions lock; slot spend/recover remains live.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellsTabV4.kt",
    [
        (
            "    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,\n    onSlotSpentChange: (Int, Int) -> Unit,\n",
            "    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean,\n    onSlotSpentChange: (Int, Int) -> Unit,\n",
            "spells tab signature",
        ),
        (
            "            StableSettingsIconButton(\n                onClick = { managerOpen = true },\n                contentDescription = \"Gestionar fuentes de conjuros\",\n            )\n",
            "            if (structuralEditingEnabled) {\n                StableSettingsIconButton(\n                    onClick = { managerOpen = true },\n                    contentDescription = \"Gestionar fuentes de conjuros\",\n                )\n            }\n",
            "source manager affordance",
        ),
        (
            "            onDraftChange = onDraftChange,\n            onSlotSpentChange = onSlotSpentChange,\n",
            "            onDraftChange = onDraftChange,\n            structuralEditingEnabled = structuralEditingEnabled,\n            onSlotSpentChange = onSlotSpentChange,\n",
            "spell list policy wiring",
        ),
        (
            "    if (managerOpen) {\n",
            "    if (managerOpen && structuralEditingEnabled) {\n",
            "source manager suppression",
        ),
        (
            "    if (editorOpen) {\n",
            "    if (editorOpen && structuralEditingEnabled) {\n",
            "source editor suppression",
        ),
        (
            "    deleteSourceId?.let { id ->\n",
            "    deleteSourceId?.takeIf { structuralEditingEnabled }?.let { id ->\n",
            "source delete suppression",
        ),
    ],
)

patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListClosureV4.kt",
    [
        (
            "    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,\n    onSlotSpentChange: (Int, Int) -> Unit,\n",
            "    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean,\n    onSlotSpentChange: (Int, Int) -> Unit,\n",
            "spell list signature",
        ),
        (
            "    val canReorder = order == CharacterPresentationOrder.MANUAL &&\n",
            "    val canReorder = structuralEditingEnabled && order == CharacterPresentationOrder.MANUAL &&\n",
            "spell reorder policy",
        ),
        (
            "            canReorder = canReorder,\n            selectedEditingId = editingSpellId?.takeIf { editorOpen },\n",
            "            canReorder = canReorder,\n            structuralEditingEnabled = structuralEditingEnabled,\n            selectedEditingId = editingSpellId?.takeIf { editorOpen },\n",
            "spell collection policy",
        ),
        (
            "                if (editorOpen) {\n",
            "                if (editorOpen && structuralEditingEnabled) {\n",
            "wide spell editor suppression",
        ),
        (
            "                        TextButton(onClick = ::beginAdd) { Text(\"+ Añadir conjuro\") }\n",
            "                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir conjuro\") }\n",
            "wide spell add lock",
        ),
        (
            "        if (editorOpen) {\n",
            "        if (editorOpen && structuralEditingEnabled) {\n",
            "phone spell editor suppression",
        ),
        (
            "    deleteSpellId?.let { id ->\n",
            "    deleteSpellId?.takeIf { structuralEditingEnabled }?.let { id ->\n",
            "spell delete suppression",
        ),
        (
            "    canReorder: Boolean,\n    selectedEditingId: String?,\n",
            "    canReorder: Boolean,\n    structuralEditingEnabled: Boolean,\n    selectedEditingId: String?,\n",
            "spell collection signature",
        ),
        (
            "                        TextButton(onClick = onAdd) { Text(\"+ Añadir\") }\n",
            "                        TextButton(onClick = onAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "spell add affordance",
        ),
        (
            "                        reorderEnabled = canReorder && sourceLevelCount > 1,\n                        selected = selectedEditingId == spell.id.toString(),\n",
            "                        reorderEnabled = canReorder && sourceLevelCount > 1,\n                        structuralEditingEnabled = structuralEditingEnabled,\n                        selected = selectedEditingId == spell.id.toString(),\n",
            "spell row policy",
        ),
        (
            "    reorderEnabled: Boolean,\n    selected: Boolean,\n",
            "    reorderEnabled: Boolean,\n    structuralEditingEnabled: Boolean,\n    selected: Boolean,\n",
            "spell row signature",
        ),
        (
            "                .characterDragFeedbackV4(dragState)\n                .clickable(onClick = onEdit),\n",
            "                .characterDragFeedbackV4(dragState)\n                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),\n",
            "spell edit clickable",
        ),
        (
            "                                checked = selectedAssociation.prepared,\n                                onCheckedChange = onPreparedChange,\n",
            "                                checked = selectedAssociation.prepared,\n                                enabled = structuralEditingEnabled,\n                                onCheckedChange = onPreparedChange,\n",
            "prepared lock",
        ),
        (
            "                            enabled = favoriteEnabled,\n",
            "                            enabled = structuralEditingEnabled && favoriteEnabled,\n",
            "spell favorite lock",
        ),
        (
            "                        StableRemoveIconButton(\n                            onClick = onDelete,\n                            contentDescription = \"Eliminar ${spell.name}\",\n                        )\n",
            "                        if (structuralEditingEnabled) {\n                            StableRemoveIconButton(\n                                onClick = onDelete,\n                                contentDescription = \"Eliminar ${spell.name}\",\n                            )\n                        }\n",
            "spell remove affordance",
        ),
        (
            "                    TextButton(\n                        onClick = onDuplicate,\n                        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),\n                    ) {\n                        Text(\"Duplicar\")\n                    }\n",
            "                    if (structuralEditingEnabled) {\n                        TextButton(\n                            onClick = onDuplicate,\n                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),\n                        ) {\n                            Text(\"Duplicar\")\n                        }\n                    }\n",
            "spell duplicate affordance",
        ),
    ],
)

print("I2b1 guarded patch applied")
