from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one anchor, found {count}")
    return text.replace(old, new, 1)


def replace_count(text: str, old: str, new: str, expected: int, label: str) -> str:
    count = text.count(old)
    if count != expected:
        raise SystemExit(f"{label}: expected {expected} anchors, found {count}")
    return text.replace(old, new)


def patch(path_str: str, transforms):
    path = Path(path_str)
    text = path.read_text()
    for transform in transforms:
        if len(transform) == 3:
            old, new, label = transform
            text = replace_once(text, old, new, label)
        else:
            old, new, expected, label = transform
            text = replace_count(text, old, new, expected, label)
    path.write_text(text)


# Artífice: structural reference module, presentation remains usable.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterArtificeModuleV4.kt",
    [
        (
            "import io.github.mrsimkin.dndcustomaid.shared.character.isArtificeCharacterOption\n",
            "import io.github.mrsimkin.dndcustomaid.shared.character.isArtificeCharacterOption\nimport io.github.mrsimkin.dndcustomaid.shared.character.isCharacterStructuralEditingEnabled\n",
            "artifice policy import",
        ),
        (
            "    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n",
            "    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n    val structuralEditingEnabled = isCharacterStructuralEditingEnabled(closureState.tableModeEnabled)\n",
            "artifice policy value",
        ),
        (
            "    val canReorder = order == CharacterPresentationOrder.MANUAL &&\n",
            "    val canReorder = structuralEditingEnabled && order == CharacterPresentationOrder.MANUAL &&\n",
            "artifice reorder policy",
        ),
        (
            "    fun beginAdd() {\n        resetEditor()\n",
            "    fun beginAdd() {\n        if (!structuralEditingEnabled) return\n        resetEditor()\n",
            "artifice add guard",
        ),
        (
            "    fun beginEdit(option: CharacterClassOption) {\n        editingId = option.id.toString()\n",
            "    fun beginEdit(option: CharacterClassOption) {\n        if (!structuralEditingEnabled) return\n        editingId = option.id.toString()\n",
            "artifice edit guard",
        ),
        (
            "    fun duplicate(option: CharacterClassOption) {\n        val copied = duplicateCharacterClassOption(\n",
            "    fun duplicate(option: CharacterClassOption) {\n        if (!structuralEditingEnabled) return\n        val copied = duplicateCharacterClassOption(\n",
            "artifice duplicate guard",
        ),
        (
            "            canReorder = canReorder,\n            selectedEditingId = selectedEditingId,\n",
            "            canReorder = canReorder,\n            structuralEditingEnabled = structuralEditingEnabled,\n            selectedEditingId = selectedEditingId,\n",
            "artifice collection policy",
        ),
        (
            "                if (editorOpen) {\n",
            "                if (editorOpen && structuralEditingEnabled) {\n",
            "artifice wide editor suppression",
        ),
        (
            "                        TextButton(onClick = ::beginAdd) { Text(\"+ Añadir registro\") }\n",
            "                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir registro\") }\n",
            "artifice side add lock",
        ),
        (
            "        if (editorOpen) {\n",
            "        if (editorOpen && structuralEditingEnabled) {\n",
            "artifice phone editor suppression",
        ),
        (
            "    deleteId?.let { id ->\n",
            "    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->\n",
            "artifice delete suppression",
        ),
        (
            "    canReorder: Boolean,\n    selectedEditingId: String?,\n",
            "    canReorder: Boolean,\n    structuralEditingEnabled: Boolean,\n    selectedEditingId: String?,\n",
            "artifice collection signature",
        ),
        (
            "                        TextButton(onClick = onAdd) { Text(\"+ Añadir\") }\n",
            "                        TextButton(onClick = onAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "artifice collection add lock",
        ),
        (
            "                    onAdd = onAdd,\n                    addLabel = \"Añadir registro\",\n",
            "                    onAdd = if (structuralEditingEnabled) onAdd else null,\n                    addLabel = \"Añadir registro\",\n",
            "artifice empty add lock",
        ),
        (
            "                favoriteEnabled = option.id in persistedOptionIds,\n                reorderEnabled = canReorder && artificeOptions.size > 1,\n",
            "                favoriteEnabled = structuralEditingEnabled && option.id in persistedOptionIds,\n                reorderEnabled = canReorder && artificeOptions.size > 1,\n                structuralEditingEnabled = structuralEditingEnabled,\n",
            "artifice row policy wiring",
        ),
        (
            "    reorderEnabled: Boolean,\n    selected: Boolean,\n",
            "    reorderEnabled: Boolean,\n    structuralEditingEnabled: Boolean,\n    selected: Boolean,\n",
            "artifice row signature",
        ),
        (
            "                .characterDragFeedbackV4(dragState)\n                .clickable(onClick = onEdit),\n",
            "                .characterDragFeedbackV4(dragState)\n                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),\n",
            "artifice row click lock",
        ),
        (
            "                        StableRemoveIconButton(\n                            onClick = onDelete,\n                            contentDescription = \"Eliminar ${option.name}\",\n                        )\n",
            "                        if (structuralEditingEnabled) {\n                            StableRemoveIconButton(\n                                onClick = onDelete,\n                                contentDescription = \"Eliminar ${option.name}\",\n                            )\n                        }\n",
            "artifice delete affordance",
        ),
        (
            "                    TextButton(\n                        onClick = onDuplicate,\n                        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),\n                    ) { Text(\"Duplicar\") }\n",
            "                    if (structuralEditingEnabled) {\n                        TextButton(\n                            onClick = onDuplicate,\n                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),\n                        ) { Text(\"Duplicar\") }\n                    }\n",
            "artifice duplicate affordance",
        ),
    ],
)


# Formas: structural transformation/reference library.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterFormsModuleV4.kt",
    [
        (
            "import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess\n",
            "import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess\nimport io.github.mrsimkin.dndcustomaid.shared.character.isCharacterStructuralEditingEnabled\n",
            "forms policy import",
        ),
        (
            "    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n",
            "    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n    val structuralEditingEnabled = isCharacterStructuralEditingEnabled(closureState.tableModeEnabled)\n",
            "forms policy value",
        ),
        (
            "    val canReorder = order == CharacterPresentationOrder.MANUAL &&\n",
            "    val canReorder = structuralEditingEnabled && order == CharacterPresentationOrder.MANUAL &&\n",
            "forms reorder policy",
        ),
        (
            "    fun beginAdd() {\n        resetEditor()\n",
            "    fun beginAdd() {\n        if (!structuralEditingEnabled) return\n        resetEditor()\n",
            "forms add guard",
        ),
        (
            "    fun beginEdit(form: CharacterForm) {\n        editingId = form.id.toString()\n",
            "    fun beginEdit(form: CharacterForm) {\n        if (!structuralEditingEnabled) return\n        editingId = form.id.toString()\n",
            "forms edit guard",
        ),
        (
            "    fun duplicate(form: CharacterForm) {\n        val copied = duplicateCharacterForm(\n",
            "    fun duplicate(form: CharacterForm) {\n        if (!structuralEditingEnabled) return\n        val copied = duplicateCharacterForm(\n",
            "forms duplicate guard",
        ),
        (
            "            canReorder = canReorder,\n            selectedEditingId = selectedEditingId,\n",
            "            canReorder = canReorder,\n            structuralEditingEnabled = structuralEditingEnabled,\n            selectedEditingId = selectedEditingId,\n",
            "forms collection policy",
        ),
        (
            "                if (editorOpen) {\n",
            "                if (editorOpen && structuralEditingEnabled) {\n",
            "forms wide editor suppression",
        ),
        (
            "                        TextButton(onClick = ::beginAdd) { Text(\"+ Añadir forma\") }\n",
            "                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir forma\") }\n",
            "forms side add lock",
        ),
        (
            "        if (editorOpen) {\n",
            "        if (editorOpen && structuralEditingEnabled) {\n",
            "forms phone editor suppression",
        ),
        (
            "    deleteId?.let { id ->\n",
            "    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->\n",
            "forms delete suppression",
        ),
        (
            "    canReorder: Boolean,\n    selectedEditingId: String?,\n",
            "    canReorder: Boolean,\n    structuralEditingEnabled: Boolean,\n    selectedEditingId: String?,\n",
            "forms collection signature",
        ),
        (
            "                        TextButton(onClick = onAdd) { Text(\"+ Añadir\") }\n",
            "                        TextButton(onClick = onAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "forms collection add lock",
        ),
        (
            "                    onAdd = onAdd,\n                    addLabel = \"Añadir forma\",\n",
            "                    onAdd = if (structuralEditingEnabled) onAdd else null,\n                    addLabel = \"Añadir forma\",\n",
            "forms empty add lock",
        ),
        (
            "                favoriteEnabled = form.id in persistedFormIds,\n                reorderEnabled = canReorder && forms.size > 1,\n",
            "                favoriteEnabled = structuralEditingEnabled && form.id in persistedFormIds,\n                reorderEnabled = canReorder && forms.size > 1,\n                structuralEditingEnabled = structuralEditingEnabled,\n",
            "forms row policy wiring",
        ),
        (
            "    reorderEnabled: Boolean,\n    selected: Boolean,\n",
            "    reorderEnabled: Boolean,\n    structuralEditingEnabled: Boolean,\n    selected: Boolean,\n",
            "forms row signature",
        ),
        (
            "                .characterDragFeedbackV4(dragState)\n                .clickable(onClick = onEdit),\n",
            "                .characterDragFeedbackV4(dragState)\n                .clickable(enabled = structuralEditingEnabled, onClick = onEdit),\n",
            "forms row click lock",
        ),
        (
            "                        StableRemoveIconButton(\n                            onClick = onDelete,\n                            contentDescription = \"Eliminar ${form.name}\",\n                        )\n",
            "                        if (structuralEditingEnabled) {\n                            StableRemoveIconButton(\n                                onClick = onDelete,\n                                contentDescription = \"Eliminar ${form.name}\",\n                            )\n                        }\n",
            "forms delete affordance",
        ),
        (
            "                    TextButton(\n                        onClick = onDuplicate,\n                        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),\n                    ) { Text(\"Duplicar\") }\n",
            "                    if (structuralEditingEnabled) {\n                        TextButton(\n                            onClick = onDuplicate,\n                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp),\n                        ) { Text(\"Duplicar\") }\n                    }\n",
            "forms duplicate affordance",
        ),
    ],
)


# Técnicas / Metamagia / Pactos reuse one H2 structural collection implementation.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterClassOptionModulesV4.kt",
    [
        (
            "import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess\n",
            "import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess\nimport io.github.mrsimkin.dndcustomaid.shared.character.isCharacterStructuralEditingEnabled\n",
            "h2 policy import",
        ),
        (
            "    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n",
            "    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n    val structuralEditingEnabled = isCharacterStructuralEditingEnabled(closureState.tableModeEnabled)\n",
            "h2 policy value",
        ),
        (
            "    val canReorder = order == CharacterPresentationOrder.MANUAL &&\n",
            "    val canReorder = structuralEditingEnabled && order == CharacterPresentationOrder.MANUAL &&\n",
            "h2 reorder policy",
        ),
        (
            "    fun beginAdd() {\n        resetEditor()\n",
            "    fun beginAdd() {\n        if (!structuralEditingEnabled) return\n        resetEditor()\n",
            "h2 add guard",
        ),
        (
            "    fun beginEdit(option: CharacterClassOption) {\n        editingId = option.id.toString()\n",
            "    fun beginEdit(option: CharacterClassOption) {\n        if (!structuralEditingEnabled) return\n        editingId = option.id.toString()\n",
            "h2 edit guard",
        ),
        (
            "    fun duplicate(option: CharacterClassOption) {\n        updateOptions(\n",
            "    fun duplicate(option: CharacterClassOption) {\n        if (!structuralEditingEnabled) return\n        updateOptions(\n",
            "h2 duplicate guard",
        ),
        (
            "            canReorder = canReorder,\n            selectedEditingId = selectedEditingId,\n",
            "            canReorder = canReorder,\n            structuralEditingEnabled = structuralEditingEnabled,\n            selectedEditingId = selectedEditingId,\n",
            "h2 collection policy",
        ),
        (
            "                if (editorOpen) {\n",
            "                if (editorOpen && structuralEditingEnabled) {\n",
            "h2 wide editor suppression",
        ),
        (
            "                        TextButton(onClick = ::beginAdd) { Text(\"+ Añadir\") }\n",
            "                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "h2 side add lock",
        ),
        (
            "        if (editorOpen) {\n",
            "        if (editorOpen && structuralEditingEnabled) {\n",
            "h2 phone editor suppression",
        ),
        (
            "    deleteId?.let { id ->\n",
            "    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->\n",
            "h2 delete suppression",
        ),
        (
            "    canReorder: Boolean,\n    selectedEditingId: String?,\n",
            "    canReorder: Boolean,\n    structuralEditingEnabled: Boolean,\n    selectedEditingId: String?,\n",
            "h2 collection signature",
        ),
        (
            "                        TextButton(onClick = onAdd) { Text(\"+ Añadir\") }\n",
            "                        TextButton(onClick = onAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "h2 collection add lock",
        ),
        (
            "                    onAdd = onAdd,\n                    addLabel = \"Añadir ${config.singularLabel}\",\n",
            "                    onAdd = if (structuralEditingEnabled) onAdd else null,\n                    addLabel = \"Añadir ${config.singularLabel}\",\n",
            "h2 empty add lock",
        ),
        (
            "                favoriteEnabled = option.id in persistedOptionIds,\n                reorderEnabled = canReorder && ownedOptions.size > 1,\n",
            "                favoriteEnabled = structuralEditingEnabled && option.id in persistedOptionIds,\n                reorderEnabled = canReorder && ownedOptions.size > 1,\n                structuralEditingEnabled = structuralEditingEnabled,\n",
            "h2 row policy wiring",
        ),
        (
            "    reorderEnabled: Boolean,\n    selected: Boolean,\n",
            "    reorderEnabled: Boolean,\n    structuralEditingEnabled: Boolean,\n    selected: Boolean,\n",
            "h2 row signature",
        ),
        (
            "            modifier = Modifier.fillMaxWidth().characterDragFeedbackV4(dragState).clickable(onClick = onEdit),\n",
            "            modifier = Modifier.fillMaxWidth().characterDragFeedbackV4(dragState).clickable(enabled = structuralEditingEnabled, onClick = onEdit),\n",
            "h2 row click lock",
        ),
        (
            "                            enabled = favoriteEnabled,\n",
            "                            enabled = structuralEditingEnabled && favoriteEnabled,\n",
            "h2 favorite lock",
        ),
        (
            "                        StableRemoveIconButton(onClick = onDelete, contentDescription = \"Eliminar ${option.name}\")\n",
            "                        if (structuralEditingEnabled) {\n                            StableRemoveIconButton(onClick = onDelete, contentDescription = \"Eliminar ${option.name}\")\n                        }\n",
            "h2 delete affordance",
        ),
        (
            "                    TextButton(onClick = onDuplicate, contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)) {\n                        Text(\"Duplicar\")\n                    }\n",
            "                    if (structuralEditingEnabled) {\n                        TextButton(onClick = onDuplicate, contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)) {\n                            Text(\"Duplicar\")\n                        }\n                    }\n",
            "h2 duplicate affordance",
        ),
    ],
)


# Compañeros are durable character reference state; encounter authority stays outside this module.
patch(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCompanionsModuleV4.kt",
    [
        (
            "import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess\n",
            "import io.github.mrsimkin.dndcustomaid.shared.character.hasQuickAccess\nimport io.github.mrsimkin.dndcustomaid.shared.character.isCharacterStructuralEditingEnabled\n",
            "companions policy import",
        ),
        (
            "    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n",
            "    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n    val structuralEditingEnabled = isCharacterStructuralEditingEnabled(closureState.tableModeEnabled)\n",
            "companions policy value",
        ),
        (
            "    val canReorder = order == CharacterPresentationOrder.MANUAL &&\n",
            "    val canReorder = structuralEditingEnabled && order == CharacterPresentationOrder.MANUAL &&\n",
            "companions reorder policy",
        ),
        (
            "    fun beginAdd() {\n        resetEditor()\n",
            "    fun beginAdd() {\n        if (!structuralEditingEnabled) return\n        resetEditor()\n",
            "companions add guard",
        ),
        (
            "    fun beginEdit(companion: CharacterCompanion) {\n        editingId = companion.id.toString()\n",
            "    fun beginEdit(companion: CharacterCompanion) {\n        if (!structuralEditingEnabled) return\n        editingId = companion.id.toString()\n",
            "companions edit guard",
        ),
        (
            "    fun duplicate(companion: CharacterCompanion) {\n        updateCompanions(\n",
            "    fun duplicate(companion: CharacterCompanion) {\n        if (!structuralEditingEnabled) return\n        updateCompanions(\n",
            "companions duplicate guard",
        ),
        (
            "            canReorder = canReorder,\n            selectedEditingId = selectedEditingId,\n",
            "            canReorder = canReorder,\n            structuralEditingEnabled = structuralEditingEnabled,\n            selectedEditingId = selectedEditingId,\n",
            "companions collection policy",
        ),
        (
            "                if (editorOpen) {\n",
            "                if (editorOpen && structuralEditingEnabled) {\n",
            "companions wide editor suppression",
        ),
        (
            "                        TextButton(onClick = ::beginAdd) { Text(\"+ Añadir\") }\n",
            "                        TextButton(onClick = ::beginAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "companions side add lock",
        ),
        (
            "        if (editorOpen) {\n",
            "        if (editorOpen && structuralEditingEnabled) {\n",
            "companions phone editor suppression",
        ),
        (
            "    deleteId?.let { id ->\n",
            "    deleteId?.takeIf { structuralEditingEnabled }?.let { id ->\n",
            "companions delete suppression",
        ),
        (
            "    canReorder: Boolean,\n    selectedEditingId: String?,\n",
            "    canReorder: Boolean,\n    structuralEditingEnabled: Boolean,\n    selectedEditingId: String?,\n",
            "companions collection signature",
        ),
        (
            "                        TextButton(onClick = onAdd) { Text(\"+ Añadir\") }\n",
            "                        TextButton(onClick = onAdd, enabled = structuralEditingEnabled) { Text(\"+ Añadir\") }\n",
            "companions collection add lock",
        ),
        (
            "                    onAdd = onAdd,\n                    addLabel = \"Añadir compañero\",\n",
            "                    onAdd = if (structuralEditingEnabled) onAdd else null,\n                    addLabel = \"Añadir compañero\",\n",
            "companions empty add lock",
        ),
        (
            "                favoriteEnabled = companion.id in persistedCompanionIds,\n                reorderEnabled = canReorder && companions.size > 1,\n",
            "                favoriteEnabled = structuralEditingEnabled && companion.id in persistedCompanionIds,\n                reorderEnabled = canReorder && companions.size > 1,\n                structuralEditingEnabled = structuralEditingEnabled,\n",
            "companions row policy wiring",
        ),
        (
            "    reorderEnabled: Boolean,\n    selected: Boolean,\n",
            "    reorderEnabled: Boolean,\n    structuralEditingEnabled: Boolean,\n    selected: Boolean,\n",
            "companions row signature",
        ),
        (
            "            modifier = Modifier.fillMaxWidth().characterDragFeedbackV4(dragState).clickable(onClick = onEdit),\n",
            "            modifier = Modifier.fillMaxWidth().characterDragFeedbackV4(dragState).clickable(enabled = structuralEditingEnabled, onClick = onEdit),\n",
            "companions row click lock",
        ),
        (
            "                            enabled = favoriteEnabled,\n",
            "                            enabled = structuralEditingEnabled && favoriteEnabled,\n",
            "companions favorite lock",
        ),
        (
            "                        StableRemoveIconButton(onClick = onDelete, contentDescription = \"Eliminar ${companion.name}\")\n",
            "                        if (structuralEditingEnabled) {\n                            StableRemoveIconButton(onClick = onDelete, contentDescription = \"Eliminar ${companion.name}\")\n                        }\n",
            "companions delete affordance",
        ),
        (
            "                    TextButton(onClick = onDuplicate, contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)) {\n                        Text(\"Duplicar\")\n                    }\n",
            "                    if (structuralEditingEnabled) {\n                        TextButton(onClick = onDuplicate, contentPadding = PaddingValues(horizontal = 5.dp, vertical = 0.dp)) {\n                            Text(\"Duplicar\")\n                        }\n                    }\n",
            "companions duplicate affordance",
        ),
    ],
)

print("I2b2b guarded module patch applied")
