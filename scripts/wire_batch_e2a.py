from pathlib import Path


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected exactly one match, found {count}")
    return text.replace(old, new, 1)

editor_path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
editor = editor_path.read_text(encoding="utf-8")

editor = replace_once(
    editor,
    """                        CharacterTabV4.OVERVIEW -> OverviewTabV4(
                            draft = draft,
                            stored = stored,
                            wide = wide,
                            onDraftChange = ::updateDraft,
                        )
""",
    """                        CharacterTabV4.OVERVIEW -> OverviewTabV4(
                            draft = draft,
                            stored = stored,
                            closureState = closureState,
                            wide = wide,
                            onDraftChange = ::updateDraft,
                            onClosureStateChange = ::persistClosureState,
                        )
""",
    "overview wiring",
)

editor = replace_once(
    editor,
    """                        CharacterTabV4.SKILLS -> SkillsTabV4(
                            draft = draft,
                            wide = wide,
                            skillLayoutChoice = preferences.skillLayoutChoice,
                            onSkillLayoutChange = {
                                onPreferencesChange(preferences.copy(skillLayoutChoice = it))
                            },
                            onDraftChange = ::updateDraft,
                        )
""",
    """                        CharacterTabV4.SKILLS -> SkillsTabV4(
                            draft = draft,
                            closureState = closureState,
                            calculationSheet = settingsSheet,
                            wide = wide,
                            skillLayoutChoice = preferences.skillLayoutChoice,
                            onSkillLayoutChange = {
                                onPreferencesChange(preferences.copy(skillLayoutChoice = it))
                            },
                            onDraftChange = ::updateDraft,
                            onClosureStateChange = ::persistClosureState,
                        )
""",
    "skills wiring",
)

editor = replace_once(
    editor,
    """private fun OverviewTabV4(
    draft: CharacterEditorDraftV4,
    stored: CharacterSheet,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
""",
    """private fun OverviewTabV4(
    draft: CharacterEditorDraftV4,
    stored: CharacterSheet,
    closureState: CharacterClosureState,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
) {
""",
    "overview signature",
)

editor = replace_once(
    editor,
    """        item {
            IdentityCardV4(draft, stored, onDraftChange)
        }
        item {
            ClassesCardV4(
""",
    """        item {
            IdentityCardV4(draft, stored, onDraftChange)
        }
        item {
            CharacterGeneralClosureCardsV4(
                state = closureState,
                onStateChange = onClosureStateChange,
                wide = wide,
            )
        }
        item {
            ClassesCardV4(
""",
    "overview closure cards",
)

editor = replace_once(
    editor,
    """private fun SkillsTabV4(
    draft: CharacterEditorDraftV4,
    wide: Boolean,
    skillLayoutChoice: SkillLayoutChoice,
    onSkillLayoutChange: (SkillLayoutChoice) -> Unit,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
""",
    """private fun SkillsTabV4(
    draft: CharacterEditorDraftV4,
    closureState: CharacterClosureState,
    calculationSheet: CharacterSheet,
    wide: Boolean,
    skillLayoutChoice: SkillLayoutChoice,
    onSkillLayoutChange: (SkillLayoutChoice) -> Unit,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
) {
""",
    "skills signature",
)

editor = replace_once(
    editor,
    """        item {
            SkillViewSelectorV4(skillLayoutChoice, onSkillLayoutChange)
        }
        when (skillLayoutChoice) {
""",
    """        item {
            SkillViewSelectorV4(skillLayoutChoice, onSkillLayoutChange)
        }
        item {
            CharacterPassiveSkillsCardV4(calculationSheet)
        }
        when (skillLayoutChoice) {
""",
    "passive skills insertion",
)

editor = replace_once(
    editor,
    """                item { SkillsListCardV4(draft, wide, onDraftChange) }
            }
            SkillLayoutChoice.BY_ATTRIBUTE -> {
                item { AbilityGroupsCardV4(draft, wide, onDraftChange) }
            }
""",
    """                item { SkillsListCardV4(draft, wide, onDraftChange) }
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
""",
    "custom skills insertion",
)

editor_path.write_text(editor, encoding="utf-8")

general_path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterGeneralClosureV4.kt")
general = general_path.read_text(encoding="utf-8")
general = replace_once(
    general,
    """internal fun CharacterGeneralClosureCardsV4(
    state: CharacterClosureState,
    onStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
) {
    CharacterMediaCardV4(state = state, onStateChange = onStateChange, wide = wide)
    CharacterDefensesSensesMovementCardV4(state = state, onStateChange = onStateChange, wide = wide)
}
""",
    """internal fun CharacterGeneralClosureCardsV4(
    state: CharacterClosureState,
    onStateChange: (CharacterClosureState) -> Unit,
    wide: Boolean,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        CharacterMediaCardV4(state = state, onStateChange = onStateChange, wide = wide)
        CharacterDefensesSensesMovementCardV4(state = state, onStateChange = onStateChange, wide = wide)
    }
}
""",
    "general cards vertical container",
)
general_path.write_text(general, encoding="utf-8")

print("Batch E2a wiring applied.")
