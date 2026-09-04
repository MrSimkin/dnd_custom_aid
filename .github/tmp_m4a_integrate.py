from pathlib import Path

path = Path('androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt')
text = path.read_text(encoding='utf-8')


def replace_once(old: str, new: str, label: str) -> None:
    global text
    count = text.count(old)
    assert count == 1, f'{label}: expected exactly one match, found {count}'
    text = text.replace(old, new, 1)


replace_once(
'''    var h1ModuleDraftJson by rememberSaveable(characterId.toString(), "h1-modules") {
        mutableStateOf(
            characterH1ModuleDraftToJsonV4(
                CharacterH1ModuleDraftV4(
                    classOptions = stored.classOptions,
                    forms = stored.forms,
                    companions = stored.companions,
                ),
            ),
        )
    }
''',
'''    var h1ModuleDraftJson by rememberSaveable(characterId.toString(), "h1-modules") {
        mutableStateOf(
            characterH1ModuleDraftToJsonV4(
                CharacterH1ModuleDraftV4(
                    classOptions = stored.classOptions,
                    forms = stored.forms,
                    companions = stored.companions,
                ),
            ),
        )
    }
    var proficiencyDraftJson by rememberSaveable(characterId.toString(), "proficiencies") {
        mutableStateOf(characterProficienciesToJsonV4(stored.proficiencies))
    }
''',
'proficiency draft state',
)

replace_once(
'''    val notesDraft = remember(notesDraftJson) { characterNotesDraftFromJsonV4(notesDraftJson) }
    val h1ModuleDraft = remember(h1ModuleDraftJson) { characterH1ModuleDraftFromJsonV4(h1ModuleDraftJson) }
    val settingsSheet = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: stored
''',
'''    val notesDraft = remember(notesDraftJson) { characterNotesDraftFromJsonV4(notesDraftJson) }
    val h1ModuleDraft = remember(h1ModuleDraftJson) { characterH1ModuleDraftFromJsonV4(h1ModuleDraftJson) }
    val proficiencyDraft = remember(proficiencyDraftJson) { characterProficienciesFromJsonV4(proficiencyDraftJson) }
    val settingsSheet = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: stored
''',
'proficiency draft decode',
)

replace_once(
'''    val storedH1ModuleDraftJson = remember(stored) {
        characterH1ModuleDraftToJsonV4(
            CharacterH1ModuleDraftV4(
                classOptions = stored.classOptions,
                forms = stored.forms,
                companions = stored.companions,
            ),
        )
    }
    val hasUnsavedChanges =
''',
'''    val storedH1ModuleDraftJson = remember(stored) {
        characterH1ModuleDraftToJsonV4(
            CharacterH1ModuleDraftV4(
                classOptions = stored.classOptions,
                forms = stored.forms,
                companions = stored.companions,
            ),
        )
    }
    val storedProficiencyDraftJson = remember(stored) {
        characterProficienciesToJsonV4(stored.proficiencies)
    }
    val hasUnsavedChanges =
''',
'persisted proficiency comparison',
)

replace_once(
'''            spellcastingDraftJson != storedSpellcastingDraftJson ||
            notesDraftJson != storedNotesDraftJson ||
            h1ModuleDraftJson != storedH1ModuleDraftJson
''',
'''            spellcastingDraftJson != storedSpellcastingDraftJson ||
            notesDraftJson != storedNotesDraftJson ||
            h1ModuleDraftJson != storedH1ModuleDraftJson ||
            proficiencyDraftJson != storedProficiencyDraftJson
''',
'proficiency dirty state',
)

replace_once(
'''    fun updateH1Modules(updated: CharacterH1ModuleDraftV4) {
        if (!structuralEditingEnabled) return
        h1ModuleDraftJson = characterH1ModuleDraftToJsonV4(updated)
        savedMessage = null
    }

    fun persist(candidate: CharacterSheet) {
''',
'''    fun updateH1Modules(updated: CharacterH1ModuleDraftV4) {
        if (!structuralEditingEnabled) return
        h1ModuleDraftJson = characterH1ModuleDraftToJsonV4(updated)
        savedMessage = null
    }

    fun updateProficiencies(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency>) {
        if (!structuralEditingEnabled) return
        proficiencyDraftJson = characterProficienciesToJsonV4(updated)
        savedMessage = null
    }

    fun persist(candidate: CharacterSheet) {
''',
'proficiency structural updater',
)

replace_once(
'''        val spellcasting = characterSpellcastingDraftFromJsonV4(spellcastingDraftJson)
        val notes = characterNotesDraftFromJsonV4(notesDraftJson)
        val h1Modules = characterH1ModuleDraftFromJsonV4(h1ModuleDraftJson)
        val integrated = candidate.copy(
''',
'''        val spellcasting = characterSpellcastingDraftFromJsonV4(spellcastingDraftJson)
        val notes = characterNotesDraftFromJsonV4(notesDraftJson)
        val h1Modules = characterH1ModuleDraftFromJsonV4(h1ModuleDraftJson)
        val proficiencies = characterProficienciesFromJsonV4(proficiencyDraftJson)
        val integrated = candidate.copy(
''',
'proficiency save decode',
)

replace_once(
'''            noteCards = notes.cards,
            classOptions = h1Modules.classOptions,
''',
'''            noteCards = notes.cards,
            proficiencies = proficiencies,
            classOptions = h1Modules.classOptions,
''',
'proficiency save integration',
)

replace_once(
'''        h1ModuleDraftJson = characterH1ModuleDraftToJsonV4(
            CharacterH1ModuleDraftV4(
                classOptions = stored.classOptions,
                forms = stored.forms,
                companions = stored.companions,
            ),
        )
        leaveAfterSave = false
''',
'''        h1ModuleDraftJson = characterH1ModuleDraftToJsonV4(
            CharacterH1ModuleDraftV4(
                classOptions = stored.classOptions,
                forms = stored.forms,
                companions = stored.companions,
            ),
        )
        proficiencyDraftJson = characterProficienciesToJsonV4(stored.proficiencies)
        leaveAfterSave = false
''',
'proficiency save reset',
)

replace_once(
'''                        CharacterTabV4.SKILLS -> SkillsTabV4(
                            draft = draft,
                            closureState = closureState,
                            calculationSheet = settingsSheet,
                            wide = wide,
                            skillLayoutChoice = preferences.skillLayoutChoice,
                            onSkillLayoutChange = {
                                onPreferencesChange(preferences.copy(skillLayoutChoice = it))
                            },
                            onDraftChange = ::updateStructuralDraft,
                            onClosureStateChange = ::persistStructuralClosureState,
                        )
''',
'''                        CharacterTabV4.SKILLS -> SkillsTabV4(
                            draft = draft,
                            closureState = closureState,
                            calculationSheet = settingsSheet,
                            proficiencies = proficiencyDraft,
                            structuralEditingEnabled = structuralEditingEnabled,
                            wide = wide,
                            skillLayoutChoice = preferences.skillLayoutChoice,
                            onSkillLayoutChange = {
                                onPreferencesChange(preferences.copy(skillLayoutChoice = it))
                            },
                            onDraftChange = ::updateStructuralDraft,
                            onClosureStateChange = ::persistStructuralClosureState,
                            onProficienciesChange = ::updateProficiencies,
                        )
''',
'Habilidades proficiency wiring',
)

replace_once(
'''private fun SkillsTabV4(
    draft: CharacterEditorDraftV4,
    closureState: CharacterClosureState,
    calculationSheet: CharacterSheet,
    wide: Boolean,
    skillLayoutChoice: SkillLayoutChoice,
    onSkillLayoutChange: (SkillLayoutChoice) -> Unit,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
) {
''',
'''private fun SkillsTabV4(
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
''',
'Habilidades signature',
)

replace_once(
'''            SkillLayoutChoice.BY_ATTRIBUTE -> {
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
    }
}
''',
'''            SkillLayoutChoice.BY_ATTRIBUTE -> {
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
''',
'Habilidades proficiency card',
)

assert 'proficiencyDraftJson != storedProficiencyDraftJson' in text
assert 'proficiencies = proficiencies' in text
assert 'CharacterProficienciesCardV4(' in text
assert 'onProficienciesChange = ::updateProficiencies' in text

path.write_text(text, encoding='utf-8')
