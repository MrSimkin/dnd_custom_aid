from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def read(rel: str) -> str:
    return (ROOT / rel).read_text(encoding="utf-8")


def write(rel: str, text: str) -> None:
    (ROOT / rel).write_text(text, encoding="utf-8")


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly 1 match, found {count}")
    return text.replace(old, new, 1)


def replace_section(text: str, start_marker: str, end_marker: str, replacement: str, label: str) -> str:
    start_count = text.count(start_marker)
    end_count = text.count(end_marker)
    if start_count != 1 or end_count != 1:
        raise RuntimeError(
            f"{label}: expected unique markers, found start={start_count}, end={end_count}"
        )
    start = text.index(start_marker)
    end = text.index(end_marker, start)
    return text[:start] + replacement + text[end:]


# Distinct owner-review identity for this pass.
gradle_path = "androidApp/build.gradle.kts"
s = read(gradle_path)
s = replace_once(s, "versionCode = 40300", "versionCode = 40400", "versionCode")
s = replace_once(s, 'versionName = "0.4.0-preqa.3"', 'versionName = "0.4.0-preqa.4"', "versionName")
write(gradle_path, s)

# Habilidades: keep the view selector + passive references fixed; scroll only the detailed sheet.
editor_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt"
s = read(editor_path)
skills_start = "@Composable\nprivate fun SkillsTabV4("
skills_end = "\n@Composable\nprivate fun SkillViewSelectorV4("
skills_replacement = '''@Composable
private fun SkillsTabV4(
    draft: CharacterEditorDraftV4,
    closureState: CharacterClosureState,
    calculationSheet: CharacterSheet,
    proficiencies: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency>,
    structuralEditingEnabled: Boolean,
    wide: Boolean,
    skillLayoutChoice: SkillLayoutChoice,
    onSkillLayoutChange: (SkillLayoutChoice) -> Unit,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    onProficienciesChange: (List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency>) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = appSpacingV4(if (wide) 10.dp else 5.dp),
                    end = appSpacingV4(if (wide) 10.dp else 5.dp),
                    top = appSpacingV4(5.dp),
                ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        ) {
            SkillViewSelectorV4(skillLayoutChoice, onSkillLayoutChange)
            CharacterPassiveSkillsCardV4(calculationSheet)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = appSpacingV4(if (wide) 10.dp else 5.dp),
                end = appSpacingV4(if (wide) 10.dp else 5.dp),
                top = 0.dp,
                bottom = appSpacingV4(170.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
        ) {
            when (skillLayoutChoice) {
                SkillLayoutChoice.BY_SKILLS -> {
                    item { AbilitiesCardV4(draft, onDraftChange) }
                    item { SavesCardV4(draft, wide, onDraftChange) }
                    item { SkillsListCardV4(draft, wide, onDraftChange) }
                    item {
                        CharacterCustomSkillsCardV4(
                            skills = closureState.customSkills,
                            calculationSheet = calculationSheet,
                            layoutChoice = skillLayoutChoice,
                            onSkillsChange = { onClosureStateChange(closureState.copy(customSkills = it)) },
                        )
                    }
                }
                SkillLayoutChoice.BY_ATTRIBUTE -> {
                    item { AbilityGroupsCardV4(draft, wide, onDraftChange) }
                    item {
                        CharacterCustomSkillsCardV4(
                            skills = closureState.customSkills,
                            calculationSheet = calculationSheet,
                            layoutChoice = skillLayoutChoice,
                            onSkillsChange = { onClosureStateChange(closureState.copy(customSkills = it)) },
                        )
                    }
                }
            }
            item {
                CharacterProficienciesCardV4(
                    proficiencies = proficiencies,
                    structuralEditingEnabled = structuralEditingEnabled,
                    onProficienciesChange = onProficienciesChange,
                )
            }
        }
    }
}
'''
s = replace_section(s, skills_start, skills_end, skills_replacement, "SkillsTabV4")
s = replace_once(
    s,
    'modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),',
    'modifier = Modifier.padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(8.dp)),',
    "skill selector spacing",
)
write(editor_path, s)

passive_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterPassiveSkillsV4.kt"
s = read(passive_path)
s = replace_once(
    s,
    'modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 7.dp),',
    'modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(7.dp)),',
    "passive skills spacing",
)
write(passive_path, s)

# Gestión: pin the operational state; conditions/resources/rest/effects/checkpoints remain scrollable.
management_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterManagementTabV4.kt"
s = read(management_path)
management_function_start = s.index("internal fun CharacterManagementTabV4(")
management_list_start = s.index("    LazyColumn(", management_function_start)
management_list_end_marker = "\n\n    if (conditionEditorOpen)"
management_list_end = s.index(management_list_end_marker, management_list_start)
management_replacement = '''    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = appSpacingV4(if (wide) 14.dp else 6.dp),
                    end = appSpacingV4(if (wide) 14.dp else 6.dp),
                    top = appSpacingV4(8.dp),
                ),
        ) {
            OperationalStateCardV4(
                sheet = sheet,
                onSheetChange = onSheetChange,
                onHaptic = haptic,
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = appSpacingV4(if (wide) 14.dp else 6.dp),
                end = appSpacingV4(if (wide) 14.dp else 6.dp),
                top = 0.dp,
                bottom = appSpacingV4(88.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(10.dp)),
        ) {
            item(key = "management-state") {
                ManagementPairV4(
                    wide = wide,
                    first = {
                        ConditionsExhaustionCardV4(
                            state = closureState,
                            onExhaustionChange = { level ->
                                onClosureStateChange(closureState.copy(exhaustionLevel = level.coerceAtLeast(0)))
                            },
                            onAddCondition = {
                                editingConditionId = null
                                conditionEditorOpen = true
                            },
                            onEditCondition = { condition ->
                                editingConditionId = condition.id.toString()
                                conditionEditorOpen = true
                            },
                            onDeleteCondition = { condition -> deletingConditionId = condition.id.toString() },
                        )
                    },
                    second = {
                        ConcentrationCardV4(
                            concentration = closureState.concentration,
                            onEdit = { concentrationEditorOpen = true },
                            onClear = {
                                onClosureStateChange(closureState.copy(concentration = null))
                            },
                        )
                    },
                )
            }

            item(key = "management-resources") {
                ResourcesCardV4(
                    resources = sheet.resources,
                    favoriteResourceIds = closureState.quickAccess
                        .filter { it.kind == CharacterQuickAccessKind.RESOURCE }
                        .mapTo(mutableSetOf()) { it.targetId },
                    structuralEditingEnabled = structuralEditingEnabled,
                    onAdd = {
                        editingResourceId = null
                        resourceEditorOpen = true
                    },
                    onEdit = { resource ->
                        editingResourceId = resource.id.toString()
                        resourceEditorOpen = true
                    },
                    onDelete = { resource -> deletingResourceId = resource.id.toString() },
                    onFavoriteChange = { resource, favorite ->
                        if (structuralEditingEnabled) {
                            onClosureStateChange(
                                closureState.copy(
                                    quickAccess = setCharacterQuickAccessFavorite(
                                        quickAccess = closureState.quickAccess,
                                        kind = CharacterQuickAccessKind.RESOURCE,
                                        targetId = resource.id,
                                        favorite = favorite,
                                    ),
                                ),
                            )
                        }
                    },
                    onAdjust = { resource, delta ->
                        val maximum = resource.maxValue
                        val changed = (resource.currentValue + delta).coerceAtLeast(0).let { value ->
                            maximum?.let { value.coerceAtMost(it) } ?: value
                        }
                        if (changed != resource.currentValue) {
                            haptic(CharacterHapticEventV4.RESOURCE)
                            onSheetChange(
                                sheet.copy(
                                    resources = sheet.resources.map {
                                        if (it.id == resource.id) it.copy(currentValue = changed) else it
                                    },
                                ),
                            )
                        }
                    },
                )
            }

            item(key = "management-rest") {
                RestAssistantCardV4(
                    onShortRest = { restKindName = CharacterRestKind.SHORT.name },
                    onLongRest = { restKindName = CharacterRestKind.LONG.name },
                )
            }

            item(key = "management-effects") {
                TemporaryEffectsCardV4(
                    effects = closureState.temporaryEffects,
                    onAdd = {
                        editingEffectId = null
                        effectEditorOpen = true
                    },
                    onEdit = { effect ->
                        editingEffectId = effect.id.toString()
                        effectEditorOpen = true
                    },
                    onDelete = { effect -> deletingEffectId = effect.id.toString() },
                    onToggle = { effect, active ->
                        onClosureStateChange(
                            closureState.copy(
                                temporaryEffects = closureState.temporaryEffects.map {
                                    if (it.id == effect.id) it.copy(active = active) else it
                                },
                            ),
                        )
                    },
                )
            }

            item(key = "management-checkpoints") {
                ReconciliationCardV4(
                    checkpoints = closureState.reconciliationCheckpoints,
                    onAdd = { checkpointEditorOpen = true },
                )
            }
        }
    }'''
s = s[:management_list_start] + management_replacement + s[management_list_end:]
s = replace_once(
    s,
    '''        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
''',
    '''        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(10.dp)),
            verticalAlignment = Alignment.Top,
''',
    "management wide pair spacing",
)
s = replace_once(
    s,
    'Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {',
    'Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(appSpacingV4(10.dp))) {',
    "management compact pair spacing",
)
s = replace_once(
    s,
    '''            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),''',
    '''            modifier = Modifier.fillMaxWidth().padding(
                horizontal = appSpacingV4(10.dp),
                vertical = appSpacingV4(9.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),''',
    "management card spacing",
)
write(management_path, s)

# Equipo: keep summary/search/filter tools sticky while sections/currencies scroll.
equipment_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEquipmentClosureV4.kt"
s = read(equipment_path)
s = replace_once(
    s,
    'import androidx.compose.foundation.BorderStroke\n',
    'import androidx.compose.foundation.BorderStroke\nimport androidx.compose.foundation.ExperimentalFoundationApi\n',
    "equipment experimental import",
)
s = replace_once(
    s,
    '@Composable\ninternal fun CharacterEquipmentClosureTabV4(',
    '@OptIn(ExperimentalFoundationApi::class)\n@Composable\ninternal fun CharacterEquipmentClosureTabV4(',
    "equipment opt in",
)
lazy_anchor = s.index("        LazyColumn(")
header_anchor = s.index("        item {\n            Card(modifier = Modifier.fillMaxWidth()) {", lazy_anchor)
s = s[:header_anchor] + s[header_anchor:].replace(
    "        item {\n            Card(modifier = Modifier.fillMaxWidth()) {",
    '        stickyHeader(key = "equipment-tools") {\n            Card(modifier = Modifier.fillMaxWidth()) {',
    1,
)
s = replace_once(
    s,
    'horizontalArrangement = Arrangement.spacedBy(if (wide) 8.dp else 0.dp),',
    'horizontalArrangement = Arrangement.spacedBy(appSpacingV4(if (wide) 8.dp else 0.dp)),',
    "equipment main gap",
)
s = replace_once(
    s,
    '''        contentPadding = PaddingValues(
            start = if (wide) 10.dp else 5.dp,
            end = if (wide) 10.dp else 5.dp,
            top = 5.dp,
            bottom = 92.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(5.dp),''',
    '''        contentPadding = PaddingValues(
            start = appSpacingV4(if (wide) 10.dp else 5.dp),
            end = appSpacingV4(if (wide) 10.dp else 5.dp),
            top = appSpacingV4(5.dp),
            bottom = appSpacingV4(92.dp),
        ),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),''',
    "equipment list spacing",
)
s = replace_once(
    s,
    '''                    modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),''',
    '''                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = appSpacingV4(7.dp),
                        vertical = appSpacingV4(6.dp),
                    ),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),''',
    "equipment tools spacing",
)
write(equipment_path, s)

# Rasgos: keep summary/search/filter/grouping tools sticky; trait groups remain scrollable.
traits_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterTraitsClosureV4.kt"
s = read(traits_path)
s = replace_once(
    s,
    'import androidx.compose.foundation.BorderStroke\n',
    'import androidx.compose.foundation.BorderStroke\nimport androidx.compose.foundation.ExperimentalFoundationApi\n',
    "traits experimental import",
)
s = replace_once(
    s,
    '@Composable\ninternal fun CharacterTraitsClosureTabV4(',
    '@OptIn(ExperimentalFoundationApi::class)\n@Composable\ninternal fun CharacterTraitsClosureTabV4(',
    "traits opt in",
)
lazy_anchor = s.index("    LazyColumn(")
header_anchor = s.index("        item {\n            Card(modifier = Modifier.fillMaxWidth()) {", lazy_anchor)
s = s[:header_anchor] + s[header_anchor:].replace(
    "        item {\n            Card(modifier = Modifier.fillMaxWidth()) {",
    '        stickyHeader(key = "traits-tools") {\n            Card(modifier = Modifier.fillMaxWidth()) {',
    1,
)
s = replace_once(
    s,
    '''        contentPadding = PaddingValues(
            start = if (wide) 10.dp else 5.dp,
            end = if (wide) 10.dp else 5.dp,
            top = 5.dp,
            bottom = 88.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(6.dp),''',
    '''        contentPadding = PaddingValues(
            start = appSpacingV4(if (wide) 10.dp else 5.dp),
            end = appSpacingV4(if (wide) 10.dp else 5.dp),
            top = appSpacingV4(5.dp),
            bottom = appSpacingV4(88.dp),
        ),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),''',
    "traits list spacing",
)
s = replace_once(
    s,
    '''                    modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),''',
    '''                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = appSpacingV4(7.dp),
                        vertical = appSpacingV4(6.dp),
                    ),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),''',
    "traits tools spacing",
)
write(traits_path, s)

# Conjuros special case: source selector is already fixed; propagate spacing now without disturbing level sticky behavior.
spells_tab_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellsTabV4.kt"
s = read(spells_tab_path)
s = replace_once(
    s,
    '.padding(horizontal = if (wide) 10.dp else 5.dp, vertical = 5.dp),',
    '.padding(\n                    horizontal = appSpacingV4(if (wide) 10.dp else 5.dp),\n                    vertical = appSpacingV4(5.dp),\n                ),',
    "spell source strip padding",
)
s = replace_once(
    s,
    'horizontalArrangement = Arrangement.spacedBy(4.dp),',
    'horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),',
    "spell source outer gap",
)
s = replace_once(
    s,
    'horizontalArrangement = Arrangement.spacedBy(4.dp),',
    'horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),',
    "spell source row gap",
)
write(spells_tab_path, s)

spell_list_path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListClosureV4.kt"
s = read(spell_list_path)
s = replace_once(
    s,
    'contentPadding = PaddingValues(start = 6.dp, end = 6.dp, top = 5.dp, bottom = 88.dp),\n        verticalArrangement = Arrangement.spacedBy(5.dp),',
    'contentPadding = PaddingValues(\n            start = appSpacingV4(6.dp),\n            end = appSpacingV4(6.dp),\n            top = appSpacingV4(5.dp),\n            bottom = appSpacingV4(88.dp),\n        ),\n        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),',
    "spell collection spacing",
)
s = replace_once(
    s,
    '''                    modifier = Modifier.fillMaxWidth().padding(horizontal = 7.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),''',
    '''                    modifier = Modifier.fillMaxWidth().padding(
                        horizontal = appSpacingV4(7.dp),
                        vertical = appSpacingV4(6.dp),
                    ),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),''',
    "spell tools spacing",
)
write(spell_list_path, s)

print("Pass 04 transformation applied successfully.")
