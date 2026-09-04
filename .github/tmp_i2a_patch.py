from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one anchor, found {count}")
    return text.replace(old, new, 1)


def replace_first_with_count(text: str, old: str, new: str, expected_count: int, label: str) -> str:
    count = text.count(old)
    if count != expected_count:
        raise SystemExit(f"{label}: expected {expected_count} anchors, found {count}")
    return text.replace(old, new, 1)


super_path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSupercompactV4.kt")
super_text = super_path.read_text()

common_signature = "    closureState: CharacterClosureState,\n    onSheetChange: (CharacterSheet) -> Unit,\n"
common_signature_live = "    closureState: CharacterClosureState,\n    liveControlsEnabled: Boolean,\n    onSheetChange: (CharacterSheet) -> Unit,\n"
super_text = replace_first_with_count(
    super_text,
    common_signature,
    common_signature_live,
    expected_count=2,
    label="main Supercompact signature",
)

super_text = replace_once(
    super_text,
    '                item(key = "supercompact-operational") {\n                    SupercompactGridV4(tiles = tiles, columns = columns)\n                }\n',
    '                if (!liveControlsEnabled) {\n                    item(key = "supercompact-pending-warning") {\n                        Card(modifier = Modifier.fillMaxWidth()) {\n                            Text(\n                                "Hay cambios estructurales sin guardar. Guarda o descártalos para habilitar PG, recursos y espacios desde esta vista.",\n                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),\n                                style = MaterialTheme.typography.bodySmall,\n                            )\n                        }\n                    }\n                }\n\n                item(key = "supercompact-operational") {\n                    SupercompactGridV4(tiles = tiles, columns = columns)\n                }\n',
    "pending warning",
)
super_text = replace_once(
    super_text,
    '                    SupercompactHpControlsV4(\n                        sheet = sheet,\n                        onSheetChange = onSheetChange,\n                    )\n',
    '                    SupercompactHpControlsV4(\n                        sheet = sheet,\n                        liveControlsEnabled = liveControlsEnabled,\n                        onSheetChange = onSheetChange,\n                    )\n',
    "hp invocation",
)
super_text = replace_once(
    super_text,
    '                        SupercompactSpellSlotsV4(\n                            sheet = sheet,\n                            onSheetChange = onSheetChange,\n                        )\n',
    '                        SupercompactSpellSlotsV4(\n                            sheet = sheet,\n                            liveControlsEnabled = liveControlsEnabled,\n                            onSheetChange = onSheetChange,\n                        )\n',
    "slots invocation",
)
super_text = replace_once(
    super_text,
    '                                            closureState = closureState,\n                                            onSheetChange = onSheetChange,\n',
    '                                            closureState = closureState,\n                                            liveControlsEnabled = liveControlsEnabled,\n                                            onSheetChange = onSheetChange,\n',
    "favorite invocation",
)
super_text = replace_once(
    super_text,
    'private fun SupercompactHpControlsV4(\n    sheet: CharacterSheet,\n    onSheetChange: (CharacterSheet) -> Unit,\n',
    'private fun SupercompactHpControlsV4(\n    sheet: CharacterSheet,\n    liveControlsEnabled: Boolean,\n    onSheetChange: (CharacterSheet) -> Unit,\n',
    "hp signature",
)
super_text = replace_once(
    super_text,
    '                enabled = sheet.currentHp > 0 || sheet.tempHp > 0,\n',
    '                enabled = liveControlsEnabled && (sheet.currentHp > 0 || sheet.tempHp > 0),\n',
    "hp damage enabled",
)
super_text = replace_once(
    super_text,
    '                enabled = sheet.currentHp < sheet.maxHp,\n',
    '                enabled = liveControlsEnabled && sheet.currentHp < sheet.maxHp,\n',
    "hp healing enabled",
)
super_text = replace_once(
    super_text,
    'private fun SupercompactSpellSlotsV4(\n    sheet: CharacterSheet,\n    onSheetChange: (CharacterSheet) -> Unit,\n',
    'private fun SupercompactSpellSlotsV4(\n    sheet: CharacterSheet,\n    liveControlsEnabled: Boolean,\n    onSheetChange: (CharacterSheet) -> Unit,\n',
    "slots signature",
)
super_text = replace_once(
    super_text,
    '                            enabled = available > 0,\n',
    '                            enabled = liveControlsEnabled && available > 0,\n',
    "slot spend enabled",
)
super_text = replace_once(
    super_text,
    '                            enabled = slot.spentSlots > 0,\n',
    '                            enabled = liveControlsEnabled && slot.spentSlots > 0,\n',
    "slot recover enabled",
)
# Only the Favorite helper still has the original common signature after the first replacement.
super_text = replace_once(
    super_text,
    common_signature + "    modifier: Modifier = Modifier,\n",
    common_signature_live + "    modifier: Modifier = Modifier,\n",
    "favorite signature",
)
super_text = replace_once(
    super_text,
    '                        enabled = resource.currentValue > 0,\n',
    '                        enabled = liveControlsEnabled && resource.currentValue > 0,\n',
    "resource spend enabled",
)
super_text = replace_once(
    super_text,
    '                        enabled = resource.maxValue == null || resource.currentValue < resource.maxValue,\n',
    '                        enabled = liveControlsEnabled && (resource.maxValue == null || resource.currentValue < resource.maxValue),\n',
    "resource recover enabled",
)
super_path.write_text(super_text)

editor_path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
editor = editor_path.read_text()
bridge_anchor = (
    '    fun persistCombatOperationalSheet(updated: CharacterSheet) {\n'
    '        if (updated == stored) return\n'
    '        val previous = stored\n'
    '        stored = repository.saveCharacter(updated)\n'
    '        if (stored.currentHp != previous.currentHp || stored.tempHp != previous.tempHp) {\n'
    '            draft = draft.copy(\n'
    '                currentHp = stored.currentHp.toString(),\n'
    '                tempHp = stored.tempHp.toString(),\n'
    '            )\n'
    '        }\n'
    '        savedMessage = "Guardado"\n'
    '    }\n\n'
)
bridge_addition = (
    '    fun persistSupercompactSheet(updated: CharacterSheet) {\n'
    '        if (updated == stored) return\n'
    '        val previous = stored\n'
    '        stored = repository.saveCharacter(updated)\n'
    '        var syncedDraft = draft\n'
    '        if (stored.currentHp != previous.currentHp || stored.tempHp != previous.tempHp) {\n'
    '            syncedDraft = syncedDraft.copy(\n'
    '                currentHp = stored.currentHp.toString(),\n'
    '                tempHp = stored.tempHp.toString(),\n'
    '            )\n'
    '        }\n'
    '        if (stored.spellSlots != previous.spellSlots) {\n'
    '            val persistedByLevel = stored.spellSlots.associateBy { it.level }\n'
    '            syncedDraft = syncedDraft.copy(\n'
    '                spellSlots = syncedDraft.spellSlots.map { slot ->\n'
    '                    val persisted = persistedByLevel[slot.level]\n'
    '                    slot.copy(\n'
    '                        total = persisted?.totalSlots?.toString() ?: "0",\n'
    '                        spent = persisted?.spentSlots ?: 0,\n'
    '                    )\n'
    '                },\n'
    '            )\n'
    '        }\n'
    '        draft = syncedDraft\n'
    '        savedMessage = "Guardado"\n'
    '    }\n\n'
)
editor = replace_once(editor, bridge_anchor, bridge_anchor + bridge_addition, "supercompact persistence bridge")
editor = replace_once(
    editor,
    '        CharacterSupercompactV4(\n            sheet = settingsSheet,\n            onBack = { showSupercompact = false },\n        )\n',
    '        CharacterSupercompactV4(\n            sheet = stored,\n            closureState = closureState,\n            liveControlsEnabled = !hasUnsavedChanges,\n            onSheetChange = ::persistSupercompactSheet,\n            onBack = { showSupercompact = false },\n        )\n',
    "supercompact editor invocation",
)
editor_path.write_text(editor)
