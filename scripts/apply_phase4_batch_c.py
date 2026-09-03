from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]


def read(path: str) -> str:
    return (ROOT / path).read_text(encoding="utf-8")


def write(path: str, text: str) -> None:
    (ROOT / path).write_text(text, encoding="utf-8")


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, found {count}")
    return text.replace(old, new, 1)


def sub_once(text: str, pattern: str, replacement: str, label: str, flags: int = 0) -> str:
    updated, count = re.subn(pattern, replacement, text, count=1, flags=flags)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one regex match, found {count}")
    return updated


def patch_main_activity() -> None:
    path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt"
    text = read(path)
    text = replace_once(
        text,
        "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository\n",
        "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository\n"
        "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository\n",
        "MainActivity closure import",
    )
    text = replace_once(
        text,
        "    private val characterRepository by lazy { CharacterRepository(database) }\n",
        "    private val characterRepository by lazy { CharacterRepository(database) }\n"
        "    private val characterClosureRepository by lazy { CharacterClosureRepository(database) }\n",
        "MainActivity closure repository",
    )
    text = replace_once(
        text,
        "                    characterRepository = characterRepository,\n                    preferences = preferences,",
        "                    characterRepository = characterRepository,\n"
        "                    characterClosureRepository = characterClosureRepository,\n"
        "                    preferences = preferences,",
        "MainActivity app repository argument",
    )
    text = replace_once(
        text,
        "    characterRepository: CharacterRepository,\n    preferences: UiPreferences,",
        "    characterRepository: CharacterRepository,\n"
        "    characterClosureRepository: CharacterClosureRepository,\n"
        "    preferences: UiPreferences,",
        "MainActivity app signature",
    )
    text = replace_once(
        text,
        "                    repository = characterRepository,\n                    preferences = preferences,\n                    onPreferencesChange = onPreferencesChange,\n                    onBack = {",
        "                    repository = characterRepository,\n"
        "                    closureRepository = characterClosureRepository,\n"
        "                    preferences = preferences,\n"
        "                    onPreferencesChange = onPreferencesChange,\n"
        "                    onOpenApplicationSettings = { showSettings = true },\n"
        "                    onBack = {",
        "MainActivity editor wiring",
    )
    write(path, text)


def patch_character_editor() -> None:
    path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt"
    text = read(path)

    text = replace_once(
        text,
        "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel\n",
        "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel\n"
        "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository\n"
        "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState\n",
        "editor closure imports",
    )
    text = replace_once(
        text,
        "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository\n",
        "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository\n"
        "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily\n",
        "editor rules import",
    )
    text = replace_once(
        text,
        "import io.github.mrsimkin.dndcustomaid.shared.character.standardProficiencyBonusForLevel\n",
        "import io.github.mrsimkin.dndcustomaid.shared.character.standardProficiencyBonusForLevel\n"
        "import io.github.mrsimkin.dndcustomaid.shared.character.suggestedCharacterModules\n",
        "editor module helper import",
    )

    text = replace_once(
        text,
        "internal fun CharacterEditorScreenV4(\n    characterId: Uuid,\n    repository: CharacterRepository,\n    preferences: UiPreferences,\n    onPreferencesChange: (UiPreferences) -> Unit,\n    onBack: () -> Unit,\n)",
        "internal fun CharacterEditorScreenV4(\n"
        "    characterId: Uuid,\n"
        "    repository: CharacterRepository,\n"
        "    closureRepository: CharacterClosureRepository,\n"
        "    preferences: UiPreferences,\n"
        "    onPreferencesChange: (UiPreferences) -> Unit,\n"
        "    onOpenApplicationSettings: () -> Unit,\n"
        "    onBack: () -> Unit,\n"
        ")",
        "editor signature",
    )
    text = replace_once(
        text,
        "    var stored by remember(characterId) {\n        mutableStateOf(requireNotNull(repository.character(characterId)))\n    }\n",
        "    var stored by remember(characterId) {\n"
        "        mutableStateOf(requireNotNull(repository.character(characterId)))\n"
        "    }\n"
        "    var closureState by remember(characterId) {\n"
        "        mutableStateOf(closureRepository.state(characterId))\n"
        "    }\n",
        "editor closure state",
    )
    text = replace_once(
        text,
        "    var showPcSettings by rememberSaveable(characterId.toString(), \"pc-settings\") { mutableStateOf(false) }\n",
        "    var showPcSettings by rememberSaveable(characterId.toString(), \"pc-settings\") { mutableStateOf(false) }\n"
        "    var showSupercompact by rememberSaveable(characterId.toString(), \"supercompact\") { mutableStateOf(false) }\n",
        "editor supercompact state",
    )
    text = replace_once(
        text,
        "    val savable = draft.toSheetOrNull(stored, blankRequiredAsZero = true) != null\n",
        "    val settingsSheet = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: stored\n"
        "    val suggestedModules = suggestedCharacterModules(settingsSheet.classes)\n"
        "    val savable = draft.toSheetOrNull(stored, blankRequiredAsZero = true) != null\n",
        "editor settings projection",
    )

    text = replace_once(
        text,
        "    BackHandler(enabled = showPcSettings) {\n        showPcSettings = false\n    }\n    BackHandler(\n        enabled = !showPcSettings && !confirmUnsavedLeave && !confirmBlankNumbers && !confirmDisableSpellcasting,\n    ) {\n        requestBack()\n    }",
        "    BackHandler(enabled = showSupercompact) {\n"
        "        showSupercompact = false\n"
        "    }\n"
        "    BackHandler(enabled = !showSupercompact && showPcSettings) {\n"
        "        showPcSettings = false\n"
        "    }\n"
        "    BackHandler(\n"
        "        enabled = !showSupercompact && !showPcSettings && !confirmUnsavedLeave && !confirmBlankNumbers && !confirmDisableSpellcasting,\n"
        "    ) {\n"
        "        requestBack()\n"
        "    }",
        "editor back hierarchy",
    )

    spellcaster_block = """    fun persistSpellcasterEnabled(enabled: Boolean) {
        if (enabled == stored.spellcasterEnabled) return
        stored = repository.saveCharacter(stored.copy(spellcasterEnabled = enabled))
        if (!enabled && selectedTabName == CharacterTabV4.SPELLS.name) {
            selectedTabName = CharacterTabV4.OVERVIEW.name
        }
        savedMessage = \"Guardado\"
    }
"""
    extended_block = spellcaster_block + """
    fun persistStatus(status: CharacterStatus) {
        if (status == stored.status && status == draft.status) return
        stored = repository.saveCharacter(stored.copy(status = status))
        draft = draft.copy(status = status)
        savedMessage = \"Guardado\"
    }

    fun persistClosureState(updated: CharacterClosureState) {
        if (updated == closureState) return
        closureState = closureRepository.saveState(characterId, updated)
        savedMessage = \"Guardado\"
    }
"""
    text = replace_once(text, spellcaster_block, extended_block, "editor settings persistence")

    old_settings = """    if (showPcSettings) {
        CharacterPcSettingsV4(
            characterName = draft.name,
            spellcasterEnabled = stored.spellcasterEnabled,
            onBack = { showPcSettings = false },
            onSpellcasterEnabledChange = { enabled ->
                if (!enabled && stored.hasMeaningfulSpellcastingDataV4()) {
                    confirmDisableSpellcasting = true
                } else {
                    persistSpellcasterEnabled(enabled)
                }
            },
        )
    } else {
"""
    new_settings = """    if (showSupercompact) {
        CharacterSupercompactV4(
            sheet = settingsSheet,
            onBack = { showSupercompact = false },
        )
    } else if (showPcSettings) {
        CharacterPcSettingsClosureV4(
            characterName = draft.name,
            status = draft.status,
            spellcasterEnabled = stored.spellcasterEnabled,
            closureState = closureState,
            suggestedModules = suggestedModules,
            onBack = { showPcSettings = false },
            onStatusChange = ::persistStatus,
            onSpellcasterEnabledChange = { enabled ->
                if (!enabled && stored.hasMeaningfulSpellcastingDataV4()) {
                    confirmDisableSpellcasting = true
                } else {
                    persistSpellcasterEnabled(enabled)
                }
            },
            onClosureStateChange = ::persistClosureState,
            onOpenSupercompact = { showSupercompact = true },
            onOpenApplicationSettings = onOpenApplicationSettings,
        )
    } else {
"""
    text = replace_once(text, old_settings, new_settings, "editor settings render")

    text = replace_once(
        text,
        "                            entries = combatEntries,\n                            onEntriesChange = ::updateCombatEntries,",
        "                            entries = combatEntries,\n"
        "                            onEntriesChange = ::updateCombatEntries,\n"
        "                            hapticsEnabled = closureState.hapticsEnabled,",
        "editor combat haptics",
    )
    text = replace_once(
        text,
        "                            draft = notesDraft,\n                            onDraftChange = ::updateNotes,\n                            wide = wide,",
        "                            draft = notesDraft,\n"
        "                            onDraftChange = ::updateNotes,\n"
        "                            wide = wide,\n"
        "                            hapticsEnabled = closureState.hapticsEnabled,",
        "editor notes haptics",
    )

    old_identity = """        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = draft.name,
                onValueChange = { onDraftChange(draft.copy(name = it)) },
                label = { Text(\"Nombre\") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            StatusSelectorV4(
                status = draft.status,
                onStatusChange = { onDraftChange(draft.copy(status = it)) },
            )
        }
"""
    new_identity = """        OutlinedTextField(
            value = draft.name,
            onValueChange = { onDraftChange(draft.copy(name = it)) },
            label = { Text(\"Nombre\") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
"""
    text = replace_once(text, old_identity, new_identity, "editor move lifecycle status")

    old_class_draft = """private data class ClassLevelDraftV4(
    val id: Uuid,
    val name: String,
    val level: String,
    val hitDieSides: String,
    val hitDiceRemaining: String,
)
"""
    new_class_draft = """private data class ClassLevelDraftV4(
    val id: Uuid,
    val name: String,
    val level: String,
    val hitDieSides: String,
    val hitDiceRemaining: String,
    val rulesFamily: CharacterRulesFamily = CharacterRulesFamily.UNSPECIFIED,
    val source: String? = null,
    val catalogKey: String? = null,
    val subclassName: String? = null,
    val subclassSource: String? = null,
    val subclassCatalogKey: String? = null,
    val subclassRulesFamily: CharacterRulesFamily = CharacterRulesFamily.UNSPECIFIED,
) {
    fun withManualName(value: String): ClassLevelDraftV4 = if (value == name) {
        copy(name = value)
    } else {
        copy(
            name = value,
            rulesFamily = CharacterRulesFamily.UNSPECIFIED,
            source = null,
            catalogKey = null,
            subclassName = null,
            subclassSource = null,
            subclassCatalogKey = null,
            subclassRulesFamily = CharacterRulesFamily.UNSPECIFIED,
        )
    }
}
"""
    text = replace_once(text, old_class_draft, new_class_draft, "editor class draft metadata")

    text = text.replace("onChange(draft.copy(name = it))", "onChange(draft.withManualName(it))")
    text = text.replace("onChange(draft.copy(name = className))", "onChange(draft.withManualName(className))")
    text = text.replace(
        "onChange(draft.copy(name = if (draft.name in classNamesV4) \"\" else draft.name))",
        "onChange(draft.withManualName(if (draft.name in classNamesV4) \"\" else draft.name))",
    )

    text = replace_once(
        text,
        "                hitDiceRemaining = parsedRequired(classDraft.hitDiceRemaining)?.takeIf { it >= 0 } ?: return null,\n                sortOrder = index,",
        "                hitDiceRemaining = parsedRequired(classDraft.hitDiceRemaining)?.takeIf { it >= 0 } ?: return null,\n"
        "                sortOrder = index,\n"
        "                rulesFamily = classDraft.rulesFamily,\n"
        "                source = classDraft.source,\n"
        "                catalogKey = classDraft.catalogKey,\n"
        "                subclassName = classDraft.subclassName?.trim()?.takeIf { it.isNotEmpty() },\n"
        "                subclassSource = classDraft.subclassSource,\n"
        "                subclassCatalogKey = classDraft.subclassCatalogKey,\n"
        "                subclassRulesFamily = classDraft.subclassRulesFamily,",
        "editor class to sheet metadata",
    )

    text = replace_once(
        text,
        "                    put(\"remaining\", item.hitDiceRemaining)\n",
        "                    put(\"remaining\", item.hitDiceRemaining)\n"
        "                    put(\"rulesFamily\", item.rulesFamily.name)\n"
        "                    put(\"source\", item.source ?: JSONObject.NULL)\n"
        "                    put(\"catalogKey\", item.catalogKey ?: JSONObject.NULL)\n"
        "                    put(\"subclassName\", item.subclassName ?: JSONObject.NULL)\n"
        "                    put(\"subclassSource\", item.subclassSource ?: JSONObject.NULL)\n"
        "                    put(\"subclassCatalogKey\", item.subclassCatalogKey ?: JSONObject.NULL)\n"
        "                    put(\"subclassRulesFamily\", item.subclassRulesFamily.name)\n",
        "editor class draft json metadata",
    )

    old_from_sheet = """            classes = sheet.classes.map {
                ClassLevelDraftV4(it.id, it.name, it.level.toString(), it.hitDieSides.toString(), it.hitDiceRemaining.toString())
            },
"""
    new_from_sheet = """            classes = sheet.classes.map { classLevel ->
                ClassLevelDraftV4(
                    id = classLevel.id,
                    name = classLevel.name,
                    level = classLevel.level.toString(),
                    hitDieSides = classLevel.hitDieSides.toString(),
                    hitDiceRemaining = classLevel.hitDiceRemaining.toString(),
                    rulesFamily = classLevel.rulesFamily,
                    source = classLevel.source,
                    catalogKey = classLevel.catalogKey,
                    subclassName = classLevel.subclassName,
                    subclassSource = classLevel.subclassSource,
                    subclassCatalogKey = classLevel.subclassCatalogKey,
                    subclassRulesFamily = classLevel.subclassRulesFamily,
                )
            },
"""
    text = replace_once(text, old_from_sheet, new_from_sheet, "editor class from sheet metadata")

    old_from_json = """                for (index in 0 until classesJson.length()) {
                    val item = classesJson.getJSONObject(index)
                    add(
                        ClassLevelDraftV4(
                            id = Uuid.parse(item.getString(\"id\")),
                            name = item.getString(\"name\"),
                            level = item.getString(\"level\"),
                            hitDieSides = item.getString(\"die\"),
                            hitDiceRemaining = item.getString(\"remaining\"),
                        ),
                    )
                }
"""
    new_from_json = """                for (index in 0 until classesJson.length()) {
                    val item = classesJson.getJSONObject(index)
                    fun optionalString(key: String): String? =
                        if (item.has(key) && !item.isNull(key)) item.getString(key) else null
                    val rulesFamily = runCatching {
                        CharacterRulesFamily.valueOf(
                            item.optString(\"rulesFamily\", CharacterRulesFamily.UNSPECIFIED.name),
                        )
                    }.getOrDefault(CharacterRulesFamily.UNSPECIFIED)
                    val subclassRulesFamily = runCatching {
                        CharacterRulesFamily.valueOf(
                            item.optString(\"subclassRulesFamily\", rulesFamily.name),
                        )
                    }.getOrDefault(rulesFamily)
                    add(
                        ClassLevelDraftV4(
                            id = Uuid.parse(item.getString(\"id\")),
                            name = item.getString(\"name\"),
                            level = item.getString(\"level\"),
                            hitDieSides = item.getString(\"die\"),
                            hitDiceRemaining = item.getString(\"remaining\"),
                            rulesFamily = rulesFamily,
                            source = optionalString(\"source\"),
                            catalogKey = optionalString(\"catalogKey\"),
                            subclassName = optionalString(\"subclassName\"),
                            subclassSource = optionalString(\"subclassSource\"),
                            subclassCatalogKey = optionalString(\"subclassCatalogKey\"),
                            subclassRulesFamily = subclassRulesFamily,
                        ),
                    )
                }
"""
    text = replace_once(text, old_from_json, new_from_json, "editor class restore metadata")

    write(path, text)


def patch_notes() -> None:
    path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterNotesTabV4.kt"
    text = read(path)
    text = replace_once(
        text,
        "    onDraftChange: (CharacterNotesDraftV4) -> Unit,\n    wide: Boolean,\n)",
        "    onDraftChange: (CharacterNotesDraftV4) -> Unit,\n"
        "    wide: Boolean,\n"
        "    hapticsEnabled: Boolean = true,\n"
        ")",
        "notes haptics signature",
    )
    text = replace_once(
        text,
        "    var deleteId by rememberSaveable(\"note-delete-id\") { mutableStateOf<String?>(null) }\n",
        "    var deleteId by rememberSaveable(\"note-delete-id\") { mutableStateOf<String?>(null) }\n"
        "    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n",
        "notes haptic hook",
    )
    text = text.replace(
        "                                        onMove = { offset -> move(index, offset) },\n                                        modifier = Modifier.weight(1f),",
        "                                        onMove = { offset -> move(index, offset) },\n"
        "                                        onHaptic = haptic,\n"
        "                                        modifier = Modifier.weight(1f),",
    )
    text = text.replace(
        "                                onMove = { offset -> move(index, offset) },\n                            )",
        "                                onMove = { offset -> move(index, offset) },\n"
        "                                onHaptic = haptic,\n"
        "                            )",
    )
    text = replace_once(
        text,
        "    onMove: (Int) -> Boolean,\n    modifier: Modifier = Modifier,",
        "    onMove: (Int) -> Boolean,\n"
        "    onHaptic: (CharacterHapticEventV4) -> Unit,\n"
        "    modifier: Modifier = Modifier,",
        "notes card haptic callback",
    )
    text = replace_once(
        text,
        "                                onDragStart = {\n                                    accumulatedDrag = 0f\n                                    dragging = true\n                                },\n                                onDragEnd = {\n                                    accumulatedDrag = 0f\n                                    dragging = false\n                                },",
        "                                onDragStart = {\n"
        "                                    accumulatedDrag = 0f\n"
        "                                    dragging = true\n"
        "                                    onHaptic(CharacterHapticEventV4.DRAG_PICKUP)\n"
        "                                },\n"
        "                                onDragEnd = {\n"
        "                                    if (dragging) onHaptic(CharacterHapticEventV4.DRAG_DROP)\n"
        "                                    accumulatedDrag = 0f\n"
        "                                    dragging = false\n"
        "                                },",
        "notes pickup drop haptics",
    )
    text = replace_once(
        text,
        "                                        if (onMove(direction)) {\n                                            accumulatedDrag -= direction * reorderStepPx",
        "                                        if (onMove(direction)) {\n"
        "                                            onHaptic(CharacterHapticEventV4.DRAG_STEP)\n"
        "                                            accumulatedDrag -= direction * reorderStepPx",
        "notes step haptic",
    )
    write(path, text)


def patch_combat() -> None:
    path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatTabV4.kt"
    text = read(path)
    text = replace_once(
        text,
        "    entries: List<CharacterCombatEntry>,\n    onEntriesChange: (List<CharacterCombatEntry>) -> Unit,\n    wide: Boolean,\n)",
        "    entries: List<CharacterCombatEntry>,\n"
        "    onEntriesChange: (List<CharacterCombatEntry>) -> Unit,\n"
        "    wide: Boolean,\n"
        "    hapticsEnabled: Boolean = true,\n"
        ")",
        "combat haptics signature",
    )
    text = replace_once(
        text,
        "    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }\n",
        "    var deleteId by rememberSaveable { mutableStateOf<String?>(null) }\n"
        "    val haptic = rememberCharacterHapticHookV4(hapticsEnabled)\n",
        "combat haptic hook",
    )
    text = replace_once(
        text,
        "                                        onMove = { offset -> move(index, offset) },\n                                        onDelete = { deleteId = entry.id.toString() },",
        "                                        onMove = { offset -> move(index, offset) },\n"
        "                                        onDelete = { deleteId = entry.id.toString() },\n"
        "                                        onHaptic = haptic,",
        "combat pass haptic",
    )
    text = replace_once(
        text,
        "    onMove: (Int) -> Boolean,\n    onDelete: () -> Unit,\n    modifier: Modifier = Modifier,",
        "    onMove: (Int) -> Boolean,\n"
        "    onDelete: () -> Unit,\n"
        "    onHaptic: (CharacterHapticEventV4) -> Unit,\n"
        "    modifier: Modifier = Modifier,",
        "combat card haptic callback",
    )
    text = replace_once(
        text,
        "                            onDragStart = { accumulatedDrag = 0f; dragging = true },\n                            onDragEnd = { accumulatedDrag = 0f; dragging = false },",
        "                            onDragStart = {\n"
        "                                accumulatedDrag = 0f\n"
        "                                dragging = true\n"
        "                                onHaptic(CharacterHapticEventV4.DRAG_PICKUP)\n"
        "                            },\n"
        "                            onDragEnd = {\n"
        "                                if (dragging) onHaptic(CharacterHapticEventV4.DRAG_DROP)\n"
        "                                accumulatedDrag = 0f\n"
        "                                dragging = false\n"
        "                            },",
        "combat pickup drop haptics",
    )
    text = replace_once(
        text,
        "                                    if (onMove(direction)) {\n                                        accumulatedDrag -= direction * reorderStepPx",
        "                                    if (onMove(direction)) {\n"
        "                                        onHaptic(CharacterHapticEventV4.DRAG_STEP)\n"
        "                                        accumulatedDrag -= direction * reorderStepPx",
        "combat step haptic",
    )
    write(path, text)


if __name__ == "__main__":
    patch_main_activity()
    patch_character_editor()
    patch_notes()
    patch_combat()
    print("Batch C integration patch applied successfully.")
