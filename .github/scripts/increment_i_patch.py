from pathlib import Path


def replace_once(path: str, old: str, new: str) -> None:
    file_path = Path(path)
    text = file_path.read_text(encoding="utf-8")
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{path}: expected exactly one match, found {count}")
    file_path.write_text(text.replace(old, new, 1), encoding="utf-8")


replace_once(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt",
    '''                    CharacterTabV4.SPELLS -> CharacterSpellsTabV4(
                        draft = spellcastingDraft,
                        classOptions = draft.classes.map { SpellSourceClassOptionV4(it.id, it.name) },
                        onDraftChange = ::updateSpellcasting,
                        wide = wide,
                    )''',
    '''                    CharacterTabV4.SPELLS -> CharacterSpellsTabV4(
                        draft = spellcastingDraft,
                        slotStates = draft.spellSlots.map { slot ->
                            val total = slot.total.toIntOrNull()?.coerceAtLeast(0) ?: 0
                            CharacterSpellSlotUiV4(
                                level = slot.level,
                                total = total,
                                spent = slot.spent.coerceIn(0, total),
                            )
                        },
                        classOptions = draft.classes.map { SpellSourceClassOptionV4(it.id, it.name) },
                        onDraftChange = ::updateSpellcasting,
                        onSlotSpentChange = { level, spent ->
                            val slot = draft.spellSlotFor(level)
                            val total = slot.total.toIntOrNull()?.coerceAtLeast(0) ?: 0
                            updateDraft(
                                draft.withSpellSlot(
                                    slot.copy(spent = spent.coerceIn(0, total)),
                                ),
                            )
                        },
                        wide = wide,
                    )''',
)

replace_once(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellsTabV4.kt",
    '''internal fun CharacterSpellsTabV4(
    draft: CharacterSpellcastingDraftV4,
    classOptions: List<SpellSourceClassOptionV4>,
    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,
    wide: Boolean,
) {''',
    '''internal fun CharacterSpellsTabV4(
    draft: CharacterSpellcastingDraftV4,
    slotStates: List<CharacterSpellSlotUiV4>,
    classOptions: List<SpellSourceClassOptionV4>,
    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,
    onSlotSpentChange: (Int, Int) -> Unit,
    wide: Boolean,
) {''',
)

replace_once(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellsTabV4.kt",
    '''        CharacterSpellListV4(
            draft = draft,
            selectedSourceId = selectedSource?.id,
            onDraftChange = onDraftChange,
            wide = wide,
        )''',
    '''        CharacterSpellListV4(
            draft = draft,
            slotStates = slotStates,
            selectedSourceId = selectedSource?.id,
            onDraftChange = onDraftChange,
            onSlotSpentChange = onSlotSpentChange,
            wide = wide,
        )''',
)

replace_once(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListV4.kt",
    '''internal fun CharacterSpellListV4(
    draft: CharacterSpellcastingDraftV4,
    selectedSourceId: Uuid?,
    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,
    wide: Boolean,
) {''',
    '''internal fun CharacterSpellListV4(
    draft: CharacterSpellcastingDraftV4,
    slotStates: List<CharacterSpellSlotUiV4>,
    selectedSourceId: Uuid?,
    onDraftChange: (CharacterSpellcastingDraftV4) -> Unit,
    onSlotSpentChange: (Int, Int) -> Unit,
    wide: Boolean,
) {''',
)

replace_once(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListV4.kt",
    '''    val collapsed = parseLevelSetV4(collapsedLevels)

    fun updateSpells(updated: List<CharacterSpell>) {''',
    '''    val collapsed = parseLevelSetV4(collapsedLevels)
    val slotByLevel = remember(slotStates) { slotStates.associateBy { it.level } }

    fun updateSpells(updated: List<CharacterSpell>) {''',
)

replace_once(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListV4.kt",
    '''                SpellLevelSectionV4(
                    level = level,
                    spells = levelSpells,
                    totalInView = allLevelCount,
                    collapsed = isCollapsed,
                    searchActive = search.isNotBlank(),
                    sourceById = sourceById,
                    selectedSourceId = selectedSourceId,
                    onToggleCollapsed = {''',
    '''                SpellLevelSectionV4(
                    level = level,
                    spells = levelSpells,
                    totalInView = allLevelCount,
                    collapsed = isCollapsed,
                    searchActive = search.isNotBlank(),
                    slot = slotByLevel[level],
                    sourceById = sourceById,
                    selectedSourceId = selectedSourceId,
                    onSlotSpentChange = { spent -> onSlotSpentChange(level, spent) },
                    onToggleCollapsed = {''',
)

replace_once(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListV4.kt",
    '''private fun SpellLevelSectionV4(
    level: Int,
    spells: List<CharacterSpell>,
    totalInView: Int,
    collapsed: Boolean,
    searchActive: Boolean,
    sourceById: Map<Uuid, io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource>,
    selectedSourceId: Uuid?,
    onToggleCollapsed: () -> Unit,''',
    '''private fun SpellLevelSectionV4(
    level: Int,
    spells: List<CharacterSpell>,
    totalInView: Int,
    collapsed: Boolean,
    searchActive: Boolean,
    slot: CharacterSpellSlotUiV4?,
    sourceById: Map<Uuid, io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingSource>,
    selectedSourceId: Uuid?,
    onSlotSpentChange: (Int) -> Unit,
    onToggleCollapsed: () -> Unit,''',
)

replace_once(
    "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListV4.kt",
    '''            TextButton(
                onClick = onToggleCollapsed,
                enabled = spells.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(spellLevelLabelV4(level), style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                    Text(
                        if (searchActive) "${spells.size}/$totalInView" else totalInView.toString(),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    if (spells.isNotEmpty()) {
                        Text(if (collapsed) "  Mostrar" else "  Ocultar", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }''',
    '''            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = onToggleCollapsed,
                    enabled = spells.isNotEmpty(),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(spellLevelLabelV4(level), style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                        Text(
                            if (searchActive) "${spells.size}/$totalInView" else totalInView.toString(),
                            style = MaterialTheme.typography.labelMedium,
                        )
                        if (spells.isNotEmpty()) {
                            Text(if (collapsed) "  Mostrar" else "  Ocultar", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
                if (level > 0 && slot != null && slot.total > 0) {
                    CompactSpellSlotHeaderV4(
                        slot = slot,
                        onSpentChange = onSlotSpentChange,
                    )
                }
            }''',
)

print("Increment I asserted source patches applied successfully")
