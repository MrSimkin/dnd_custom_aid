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


# Editor transition/save safety and read-only surface wiring.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt",
    [
        (
            "    fun save() {\n        if (draft.missingRequiredNumberLabels().isNotEmpty()) {\n",
            "    fun save() {\n        if (!structuralEditingEnabled) return\n        if (draft.missingRequiredNumberLabels().isNotEmpty()) {\n",
            "table save guard",
        ),
        (
            "    fun saveBlankNumbersAsZero() {\n        val candidate = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: return\n",
            "    fun saveBlankNumbersAsZero() {\n        if (!structuralEditingEnabled) return\n        val candidate = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: return\n",
            "blank-number save guard",
        ),
        (
            "    fun persistClosureState(updated: CharacterClosureState) {\n        if (updated == closureState) return\n",
            "    fun persistClosureState(updated: CharacterClosureState) {\n        if (!closureState.tableModeEnabled && updated.tableModeEnabled && hasUnsavedChanges) return\n        if (updated == closureState) return\n",
            "table transition guard",
        ),
        (
            "            closureState = closureState,\n            suggestedModules = suggestedModules,\n            onBack = {\n",
            "            closureState = closureState,\n            suggestedModules = suggestedModules,\n            tableModeCanEnable = !hasUnsavedChanges || closureState.tableModeEnabled,\n            onBack = {\n",
            "pc settings transition policy",
        ),
        (
            "                        CharacterTabV4.BACKGROUND -> CharacterBackgroundTabV4(\n                            background = backgroundDraft,\n                            onBackgroundChange = ::updateBackground,\n                            wide = wide,\n",
            "                        CharacterTabV4.BACKGROUND -> CharacterBackgroundTabV4(\n                            background = backgroundDraft,\n                            onBackgroundChange = ::updateBackground,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            wide = wide,\n",
            "background read-only wiring",
        ),
        (
            "                        CharacterTabV4.NOTES -> CharacterNotesTabV4(\n                            draft = notesDraft,\n                            onDraftChange = ::updateNotes,\n                            wide = wide,\n",
            "                        CharacterTabV4.NOTES -> CharacterNotesTabV4(\n                            draft = notesDraft,\n                            onDraftChange = ::updateNotes,\n                            structuralEditingEnabled = structuralEditingEnabled,\n                            wide = wide,\n",
            "notes read-only wiring",
        ),
        (
            "        Button(onClick = onSave, enabled = savable) { Text(\"Guardar\") }\n",
            "        Button(onClick = onSave, enabled = savable && !tableModeEnabled) { Text(\"Guardar\") }\n",
            "header save lock",
        ),
    ],
)


# PC Settings: do not enable Table mode over an existing dirty structural draft.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterPcSettingsClosureV4.kt",
    [
        (
            "    closureState: CharacterClosureState,\n    suggestedModules: Set<CharacterModuleKind>,\n    onBack: () -> Unit,\n",
            "    closureState: CharacterClosureState,\n    suggestedModules: Set<CharacterModuleKind>,\n    tableModeCanEnable: Boolean,\n    onBack: () -> Unit,\n",
            "pc settings signature",
        ),
        (
            "                                title = \"Modo mesa / solo lectura\",\n                                description = \"Reduce cambios estructurales accidentales durante el uso en mesa. Su aplicación completa se integra en la superficie adaptativa.\",\n                                checked = closureState.tableModeEnabled,\n                                onCheckedChange = { enabled ->\n",
            "                                title = \"Modo mesa / solo lectura\",\n                                description = if (!tableModeCanEnable && !closureState.tableModeEnabled) {\n                                    \"Guarda o descarta los cambios estructurales pendientes antes de activar Modo Mesa.\"\n                                } else {\n                                    \"Bloquea la edición estructural durante el uso en mesa y conserva los controles operativos intencionales.\"\n                                },\n                                checked = closureState.tableModeEnabled,\n                                enabled = closureState.tableModeEnabled || tableModeCanEnable,\n                                onCheckedChange = { enabled ->\n",
            "table setting truth and enablement",
        ),
        (
            "private fun BooleanSettingCardClosureV4(\n    title: String,\n    description: String,\n    checked: Boolean,\n    onCheckedChange: (Boolean) -> Unit,\n",
            "private fun BooleanSettingCardClosureV4(\n    title: String,\n    description: String,\n    checked: Boolean,\n    enabled: Boolean = true,\n    onCheckedChange: (Boolean) -> Unit,\n",
            "boolean setting enabled parameter",
        ),
        (
            "            Switch(checked = checked, onCheckedChange = onCheckedChange)\n",
            "            Switch(checked = checked, enabled = enabled, onCheckedChange = onCheckedChange)\n",
            "boolean switch enablement",
        ),
    ],
)


# Notes: visible read-only behavior, while preserving scrolling/reading.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterNotesTabV4.kt",
    [
        (
            "    draft: CharacterNotesDraftV4,\n    onDraftChange: (CharacterNotesDraftV4) -> Unit,\n    wide: Boolean,\n",
            "    draft: CharacterNotesDraftV4,\n    onDraftChange: (CharacterNotesDraftV4) -> Unit,\n    structuralEditingEnabled: Boolean,\n    wide: Boolean,\n",
            "notes signature",
        ),
        (
            "    fun beginAdd() {\n        editingId = null\n",
            "    fun beginAdd() {\n        if (!structuralEditingEnabled) return\n        editingId = null\n",
            "notes add guard",
        ),
        (
            "    fun beginEdit(note: CharacterNote) {\n        editingId = note.id.toString()\n",
            "    fun beginEdit(note: CharacterNote) {\n        if (!structuralEditingEnabled) return\n        editingId = note.id.toString()\n",
            "notes edit guard",
        ),
        (
            "    fun move(index: Int, offset: Int): Boolean {\n        val note = draft.cards.getOrNull(index) ?: return false\n",
            "    fun move(index: Int, offset: Int): Boolean {\n        if (!structuralEditingEnabled) return false\n        val note = draft.cards.getOrNull(index) ?: return false\n",
            "notes reorder guard",
        ),
        (
            "    fun duplicate(note: CharacterNote) {\n        val copied = duplicateCharacterNote(\n",
            "    fun duplicate(note: CharacterNote) {\n        if (!structuralEditingEnabled) return\n        val copied = duplicateCharacterNote(\n",
            "notes duplicate guard",
        ),
        (
            "                        value = draft.generalNotes,\n                        onValueChange = { onDraftChange(draft.copy(generalNotes = it)) },\n",
            "                        value = draft.generalNotes,\n                        onValueChange = { onDraftChange(draft.copy(generalNotes = it)) },\n                        enabled = structuralEditingEnabled,\n",
            "general notes field lock",
        ),
        (
            "                        TextButton(onClick = ::beginAdd) { Text(\"+ Añadir\") }\n",
            "                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "notes add affordance",
        ),
        (
            "                            onAdd = ::beginAdd,\n                            addLabel = \"Añadir nota\",\n",
            "                            onAdd = if (structuralEditingEnabled) ::beginAdd else null,\n                            addLabel = \"Añadir nota\",\n",
            "notes empty-state add lock",
        ),
        (
            "                                        onMove = { offset -> move(index, offset) },\n                                        onHaptic = haptic,\n",
            "                                        onMove = { offset -> move(index, offset) },\n                                        structuralEditingEnabled = structuralEditingEnabled,\n                                        onHaptic = haptic,\n",
            "wide note card policy",
        ),
        (
            "                                onMove = { offset -> move(index, offset) },\n                                onHaptic = haptic,\n",
            "                                onMove = { offset -> move(index, offset) },\n                                structuralEditingEnabled = structuralEditingEnabled,\n                                onHaptic = haptic,\n",
            "phone note card policy",
        ),
        (
            "    if (editorOpen) {\n",
            "    if (editorOpen && structuralEditingEnabled) {\n",
            "note editor suppression",
        ),
        (
            "    deleteId?.let { id ->\n",
            "    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->\n",
            "note delete suppression",
        ),
        (
            "    onDelete: () -> Unit,\n    onMove: (Int) -> Boolean,\n    onHaptic: (CharacterHapticEventV4) -> Unit,\n",
            "    onDelete: () -> Unit,\n    onMove: (Int) -> Boolean,\n    structuralEditingEnabled: Boolean,\n    onHaptic: (CharacterHapticEventV4) -> Unit,\n",
            "note card signature",
        ),
        (
            "                .characterDragFeedbackV4(dragState)\n                .clickable(onClick = onEdit),\n",
            "                .characterDragFeedbackV4(dragState)\n                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),\n",
            "note edit clickable",
        ),
        (
            "                    StableDragHandle(\n                        modifier = Modifier.pointerInput(note.id) {\n",
            "                    if (structuralEditingEnabled) {\n                        StableDragHandle(\n                            modifier = Modifier.pointerInput(note.id) {\n",
            "note drag handle opening",
        ),
        (
            "                        active = dragging,\n                        contentDescription = \"Mantén pulsado y arrastra para reordenar ${note.title}\",\n                    )\n                    Text(\n",
            "                            active = dragging,\n                            contentDescription = \"Mantén pulsado y arrastra para reordenar ${note.title}\",\n                        )\n                    }\n                    Text(\n",
            "note drag handle closing",
        ),
        (
            "                    StableRemoveIconButton(\n                        onClick = onDelete,\n                        contentDescription = \"Eliminar ${note.title}\",\n                    )\n",
            "                    if (structuralEditingEnabled) {\n                        StableRemoveIconButton(\n                            onClick = onDelete,\n                            contentDescription = \"Eliminar ${note.title}\",\n                        )\n                    }\n",
            "note delete affordance",
        ),
        (
            "                Row(\n                    modifier = Modifier.fillMaxWidth(),\n                    horizontalArrangement = Arrangement.End,\n                ) {\n                    TextButton(\n                        onClick = onDuplicate,\n                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),\n                    ) {\n                        Text(\"Duplicar\")\n                    }\n                }\n",
            "                if (structuralEditingEnabled) {\n                    Row(\n                        modifier = Modifier.fillMaxWidth(),\n                        horizontalArrangement = Arrangement.End,\n                    ) {\n                        TextButton(\n                            onClick = onDuplicate,\n                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),\n                        ) {\n                            Text(\"Duplicar\")\n                        }\n                    }\n                }\n",
            "note duplicate affordance",
        ),
    ],
)


# Background: fields become visibly read-only; collapse/expand remains presentation-only.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterBackgroundTabV4.kt",
    [
        (
            "    background: CharacterBackground,\n    onBackgroundChange: (CharacterBackground) -> Unit,\n    wide: Boolean,\n",
            "    background: CharacterBackground,\n    onBackgroundChange: (CharacterBackground) -> Unit,\n    structuralEditingEnabled: Boolean,\n    wide: Boolean,\n",
            "background signature",
        ),
        (
            "    fun beginEdit(field: BackgroundNarrativeFieldV4) {\n        editingFieldName = field.name\n",
            "    fun beginEdit(field: BackgroundNarrativeFieldV4) {\n        if (!structuralEditingEnabled) return\n        editingFieldName = field.name\n",
            "background narrative guard",
        ),
        (
            "                        onValueChange = { onBackgroundChange(background.copy(name = it)) },\n                        modifier = Modifier.fillMaxWidth(),\n",
            "                        onValueChange = { onBackgroundChange(background.copy(name = it)) },\n                        enabled = structuralEditingEnabled,\n                        modifier = Modifier.fillMaxWidth(),\n",
            "background name lock",
        ),
        (
            "                                onValueChange = { onBackgroundChange(background.copy(race = it)) },\n                                modifier = Modifier.weight(1f),\n",
            "                                onValueChange = { onBackgroundChange(background.copy(race = it)) },\n                                enabled = structuralEditingEnabled,\n                                modifier = Modifier.weight(1f),\n",
            "wide race lock",
        ),
        (
            "                                onValueChange = { onBackgroundChange(background.copy(religionFaith = it)) },\n                                modifier = Modifier.weight(1f),\n",
            "                                onValueChange = { onBackgroundChange(background.copy(religionFaith = it)) },\n                                enabled = structuralEditingEnabled,\n                                modifier = Modifier.weight(1f),\n",
            "wide faith lock",
        ),
        (
            "                            onValueChange = { onBackgroundChange(background.copy(race = it)) },\n                            modifier = Modifier.fillMaxWidth(),\n",
            "                            onValueChange = { onBackgroundChange(background.copy(race = it)) },\n                            enabled = structuralEditingEnabled,\n                            modifier = Modifier.fillMaxWidth(),\n",
            "phone race lock",
        ),
        (
            "                            onValueChange = { onBackgroundChange(background.copy(religionFaith = it)) },\n                            modifier = Modifier.fillMaxWidth(),\n",
            "                            onValueChange = { onBackgroundChange(background.copy(religionFaith = it)) },\n                            enabled = structuralEditingEnabled,\n                            modifier = Modifier.fillMaxWidth(),\n",
            "phone faith lock",
        ),
        (
            "                        onValueChange = { onBackgroundChange(background.copy(summary = it)) },\n                        modifier = Modifier.fillMaxWidth(),\n",
            "                        onValueChange = { onBackgroundChange(background.copy(summary = it)) },\n                        enabled = structuralEditingEnabled,\n                        modifier = Modifier.fillMaxWidth(),\n",
            "background summary lock",
        ),
        (
            "                                        onEdit = { beginEdit(field) },\n                                        modifier = Modifier.weight(1f),\n",
            "                                        onEdit = { beginEdit(field) },\n                                        editingEnabled = structuralEditingEnabled,\n                                        modifier = Modifier.weight(1f),\n",
            "wide narrative card policy",
        ),
        (
            "                                onEdit = { beginEdit(field) },\n                            )\n",
            "                                onEdit = { beginEdit(field) },\n                                editingEnabled = structuralEditingEnabled,\n                            )\n",
            "phone narrative card policy",
        ),
        (
            "                                } else if (background.story.isBlank()) {\n                                    \"Añadir\"\n                                } else {\n",
            "                                } else if (background.story.isBlank() && structuralEditingEnabled) {\n                                    \"Añadir\"\n                                } else {\n",
            "story presentation label",
        ),
        (
            "                            onValueChange = { onBackgroundChange(background.copy(story = it)) },\n                            modifier = Modifier\n",
            "                            onValueChange = { onBackgroundChange(background.copy(story = it)) },\n                            enabled = structuralEditingEnabled,\n                            modifier = Modifier\n",
            "story field lock",
        ),
        (
            "                                    if (background.story.isBlank()) \"Toca para añadir\" else \"Toca para expandir y editar\",\n",
            "                                    when {\n                                        !structuralEditingEnabled -> \"Toca para expandir · Modo Mesa solo lectura\"\n                                        background.story.isBlank() -> \"Toca para añadir\"\n                                        else -> \"Toca para expandir y editar\"\n                                    },\n",
            "story readonly hint",
        ),
        (
            "    if (editingField != null) {\n",
            "    if (editingField != null && structuralEditingEnabled) {\n",
            "background editor suppression",
        ),
        (
            "    onEdit: () -> Unit,\n    modifier: Modifier = Modifier,\n",
            "    onEdit: () -> Unit,\n    editingEnabled: Boolean,\n    modifier: Modifier = Modifier,\n",
            "narrative card signature",
        ),
        (
            "            .fillMaxWidth()\n            .clickable(onClick = onEdit),\n",
            "            .fillMaxWidth()\n            .clickable(enabled = editingEnabled, onClick = onEdit),\n",
            "narrative clickable lock",
        ),
        (
            "            Text(\"Toca para editar\", style = MaterialTheme.typography.labelSmall)\n",
            "            Text(\n                if (editingEnabled) \"Toca para editar\" else \"Modo Mesa · solo lectura\",\n                style = MaterialTheme.typography.labelSmall,\n            )\n",
            "narrative readonly hint",
        ),
    ],
)

print("I2b2a guarded patch applied")
