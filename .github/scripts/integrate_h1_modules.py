from pathlib import Path

PATH = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
text = PATH.read_text()


def replace_once(old: str, new: str, label: str) -> None:
    global text
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one match, found {count}")
    text = text.replace(old, new, 1)


replace_once(
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind\n",
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleKind\nimport io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind\n",
    "module-kind import",
)
replace_once(
    "import io.github.mrsimkin.dndcustomaid.shared.character.suggestedCharacterModules\n",
    "import io.github.mrsimkin.dndcustomaid.shared.character.suggestedCharacterModules\nimport io.github.mrsimkin.dndcustomaid.shared.character.visibleCharacterModules\n",
    "visible-modules import",
)

replace_once(
    '''    var notesDraftJson by rememberSaveable(characterId.toString(), "notes") {
        mutableStateOf(
            characterNotesDraftToJsonV4(
                CharacterNotesDraftV4(
                    generalNotes = stored.generalNotes,
                    cards = stored.noteCards,
                ),
            ),
        )
    }
    var savedMessage''',
    '''    var notesDraftJson by rememberSaveable(characterId.toString(), "notes") {
        mutableStateOf(
            characterNotesDraftToJsonV4(
                CharacterNotesDraftV4(
                    generalNotes = stored.generalNotes,
                    cards = stored.noteCards,
                ),
            ),
        )
    }
    var h1ModuleDraftJson by rememberSaveable(characterId.toString(), "h1-modules") {
        mutableStateOf(
            characterH1ModuleDraftToJsonV4(
                CharacterH1ModuleDraftV4(
                    classOptions = stored.classOptions,
                    forms = stored.forms,
                ),
            ),
        )
    }
    var savedMessage''',
    "H1 draft state",
)

replace_once(
    '''    val selectedTab = resolvedCharacterTabV4(
        savedTabName = selectedTabName,
        spellcasterEnabled = stored.spellcasterEnabled,
    )
    val combatEntries''',
    '''    val combatEntries''',
    "remove early selected tab",
)

replace_once(
    '''    val notesDraft = remember(notesDraftJson) { characterNotesDraftFromJsonV4(notesDraftJson) }
    val settingsSheet = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: stored
    val suggestedModules = suggestedCharacterModules(settingsSheet.classes)
    val savable''',
    '''    val notesDraft = remember(notesDraftJson) { characterNotesDraftFromJsonV4(notesDraftJson) }
    val h1ModuleDraft = remember(h1ModuleDraftJson) { characterH1ModuleDraftFromJsonV4(h1ModuleDraftJson) }
    val settingsSheet = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: stored
    val suggestedModules = suggestedCharacterModules(settingsSheet.classes)
    val visibleModules = visibleCharacterModules(settingsSheet.classes, closureState.moduleOverrides)
    val selectedTab = resolvedCharacterTabV4(
        savedTabName = selectedTabName,
        spellcasterEnabled = stored.spellcasterEnabled,
        visibleModules = visibleModules,
    )
    val savable''',
    "H1 decoded draft and visible modules",
)

replace_once(
    '''    val storedNotesDraftJson = remember(stored) {
        characterNotesDraftToJsonV4(
            CharacterNotesDraftV4(
                generalNotes = stored.generalNotes,
                cards = stored.noteCards,
            ),
        )
    }
    val hasUnsavedChanges =''',
    '''    val storedNotesDraftJson = remember(stored) {
        characterNotesDraftToJsonV4(
            CharacterNotesDraftV4(
                generalNotes = stored.generalNotes,
                cards = stored.noteCards,
            ),
        )
    }
    val storedH1ModuleDraftJson = remember(stored) {
        characterH1ModuleDraftToJsonV4(
            CharacterH1ModuleDraftV4(
                classOptions = stored.classOptions,
                forms = stored.forms,
            ),
        )
    }
    val hasUnsavedChanges =''',
    "stored H1 draft",
)

replace_once(
    '''            traitsDraftJson != storedTraitsDraftJson ||
            spellcastingDraftJson != storedSpellcastingDraftJson ||
            notesDraftJson != storedNotesDraftJson''',
    '''            traitsDraftJson != storedTraitsDraftJson ||
            spellcastingDraftJson != storedSpellcastingDraftJson ||
            notesDraftJson != storedNotesDraftJson ||
            h1ModuleDraftJson != storedH1ModuleDraftJson''',
    "H1 dirty state",
)

replace_once(
    '''    BackHandler(enabled = !showSupercompact && showPcSettings) {
        showPcSettings = false
    }''',
    '''    BackHandler(enabled = !showSupercompact && showPcSettings) {
        showPcSettings = false
        selectedTabName = resolvedCharacterTabV4(
            savedTabName = selectedTabName,
            spellcasterEnabled = stored.spellcasterEnabled,
            visibleModules = visibleModules,
        ).name
    }''',
    "PC settings back resolution",
)

replace_once(
    '''    fun updateNotes(updated: CharacterNotesDraftV4) {
        notesDraftJson = characterNotesDraftToJsonV4(updated)
        savedMessage = null
    }

    fun persist(candidate: CharacterSheet) {''',
    '''    fun updateNotes(updated: CharacterNotesDraftV4) {
        notesDraftJson = characterNotesDraftToJsonV4(updated)
        savedMessage = null
    }

    fun updateH1Modules(updated: CharacterH1ModuleDraftV4) {
        h1ModuleDraftJson = characterH1ModuleDraftToJsonV4(updated)
        savedMessage = null
    }

    fun persist(candidate: CharacterSheet) {''',
    "H1 updater",
)

replace_once(
    '''        val spellcasting = characterSpellcastingDraftFromJsonV4(spellcastingDraftJson)
        val notes = characterNotesDraftFromJsonV4(notesDraftJson)
        val integrated = candidate.copy(''',
    '''        val spellcasting = characterSpellcastingDraftFromJsonV4(spellcastingDraftJson)
        val notes = characterNotesDraftFromJsonV4(notesDraftJson)
        val h1Modules = characterH1ModuleDraftFromJsonV4(h1ModuleDraftJson)
        val integrated = candidate.copy(''',
    "parse H1 on save",
)

replace_once(
    '''            generalNotes = notes.generalNotes,
            noteCards = notes.cards,
        )''',
    '''            generalNotes = notes.generalNotes,
            noteCards = notes.cards,
            classOptions = h1Modules.classOptions,
            forms = h1Modules.forms,
        )''',
    "merge H1 on save",
)

replace_once(
    '''        val liveTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id }
        val liveSpellIds = stored.spells.mapTo(mutableSetOf()) { it.id }
        val prunedQuickAccess = closureState.quickAccess
            .filter { reference ->
                when (reference.kind) {
                    CharacterQuickAccessKind.TRAIT -> reference.targetId in liveTraitIds
                    CharacterQuickAccessKind.SPELL -> reference.targetId in liveSpellIds
                    else -> true
                }
            }''',
    '''        val liveTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id }
        val liveSpellIds = stored.spells.mapTo(mutableSetOf()) { it.id }
        val liveClassOptionIds = stored.classOptions.mapTo(mutableSetOf()) { it.id }
        val liveFormIds = stored.forms.mapTo(mutableSetOf()) { it.id }
        val prunedQuickAccess = closureState.quickAccess
            .filter { reference ->
                when (reference.kind) {
                    CharacterQuickAccessKind.TRAIT -> reference.targetId in liveTraitIds
                    CharacterQuickAccessKind.SPELL -> reference.targetId in liveSpellIds
                    CharacterQuickAccessKind.CLASS_OPTION -> reference.targetId in liveClassOptionIds
                    CharacterQuickAccessKind.FORM -> reference.targetId in liveFormIds
                    else -> true
                }
            }''',
    "H1 Quick Access pruning",
)

replace_once(
    '''        notesDraftJson = characterNotesDraftToJsonV4(
            CharacterNotesDraftV4(
                generalNotes = stored.generalNotes,
                cards = stored.noteCards,
            ),
        )
        leaveAfterSave''',
    '''        notesDraftJson = characterNotesDraftToJsonV4(
            CharacterNotesDraftV4(
                generalNotes = stored.generalNotes,
                cards = stored.noteCards,
            ),
        )
        h1ModuleDraftJson = characterH1ModuleDraftToJsonV4(
            CharacterH1ModuleDraftV4(
                classOptions = stored.classOptions,
                forms = stored.forms,
            ),
        )
        leaveAfterSave''',
    "reset H1 after save",
)

replace_once(
    '''            suggestedModules = suggestedModules,
            onBack = { showPcSettings = false },''',
    '''            suggestedModules = suggestedModules,
            onBack = {
                showPcSettings = false
                selectedTabName = resolvedCharacterTabV4(
                    savedTabName = selectedTabName,
                    spellcasterEnabled = stored.spellcasterEnabled,
                    visibleModules = visibleModules,
                ).name
            },''',
    "PC settings explicit back resolution",
)

replace_once(
    '''                    CharacterTopTabStripV4(
                        selectedTab = selectedTab,
                        spellcasterEnabled = stored.spellcasterEnabled,
                        onSelect = { selectedTabName = it.name },
                    )''',
    '''                    CharacterTopTabStripV4(
                        selectedTab = selectedTab,
                        spellcasterEnabled = stored.spellcasterEnabled,
                        visibleModules = visibleModules,
                        onSelect = { selectedTabName = it.name },
                    )''',
    "top strip modules",
)

replace_once(
    '''                        CharacterTabV4.NOTES -> CharacterNotesTabV4(
                            draft = notesDraft,
                            onDraftChange = ::updateNotes,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )''',
    '''                        CharacterTabV4.ARTIFICER -> CharacterArtificeModuleV4(
                            options = h1ModuleDraft.classOptions,
                            classes = settingsSheet.classes,
                            closureState = closureState,
                            persistedOptionIds = stored.classOptions.mapTo(mutableSetOf()) { it.id },
                            onOptionsChange = { updated ->
                                updateH1Modules(h1ModuleDraft.copy(classOptions = updated))
                            },
                            onClosureStateChange = ::persistClosureState,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.FORMS -> CharacterFormsModuleV4(
                            forms = h1ModuleDraft.forms,
                            closureState = closureState,
                            persistedFormIds = stored.forms.mapTo(mutableSetOf()) { it.id },
                            onFormsChange = { updated ->
                                updateH1Modules(h1ModuleDraft.copy(forms = updated))
                            },
                            onClosureStateChange = ::persistClosureState,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.NOTES -> CharacterNotesTabV4(
                            draft = notesDraft,
                            onDraftChange = ::updateNotes,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )''',
    "H1 module surfaces",
)

PATH.write_text(text)
