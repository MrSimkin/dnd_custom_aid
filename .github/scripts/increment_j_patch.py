from pathlib import Path


def replace_once(path: str, old: str, new: str) -> None:
    file_path = Path(path)
    text = file_path.read_text(encoding="utf-8")
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{path}: expected exactly one match, found {count}")
    file_path.write_text(text.replace(old, new, 1), encoding="utf-8")


path = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt"

replace_once(
    path,
    '''    var spellcastingDraftJson by rememberSaveable(characterId.toString(), "spellcasting") {
        mutableStateOf(
            characterSpellcastingDraftToJsonV4(
                CharacterSpellcastingDraftV4(
                    sources = stored.spellcastingSources,
                    spells = stored.spells,
                ),
            ),
        )
    }
    var savedMessage by rememberSaveable(characterId.toString()) { mutableStateOf<String?>(null) }''',
    '''    var spellcastingDraftJson by rememberSaveable(characterId.toString(), "spellcasting") {
        mutableStateOf(
            characterSpellcastingDraftToJsonV4(
                CharacterSpellcastingDraftV4(
                    sources = stored.spellcastingSources,
                    spells = stored.spells,
                ),
            ),
        )
    }
    var notesDraftJson by rememberSaveable(characterId.toString(), "notes") {
        mutableStateOf(
            characterNotesDraftToJsonV4(
                CharacterNotesDraftV4(
                    generalNotes = stored.generalNotes,
                    cards = stored.noteCards,
                ),
            ),
        )
    }
    var savedMessage by rememberSaveable(characterId.toString()) { mutableStateOf<String?>(null) }''',
)

replace_once(
    path,
    '''    val traitsDraft = remember(traitsDraftJson) { characterTraitsFromJsonV4(traitsDraftJson) }
    val spellcastingDraft = remember(spellcastingDraftJson) { characterSpellcastingDraftFromJsonV4(spellcastingDraftJson) }
    val savable = draft.toSheetOrNull(stored, blankRequiredAsZero = true) != null''',
    '''    val traitsDraft = remember(traitsDraftJson) { characterTraitsFromJsonV4(traitsDraftJson) }
    val spellcastingDraft = remember(spellcastingDraftJson) { characterSpellcastingDraftFromJsonV4(spellcastingDraftJson) }
    val notesDraft = remember(notesDraftJson) { characterNotesDraftFromJsonV4(notesDraftJson) }
    val savable = draft.toSheetOrNull(stored, blankRequiredAsZero = true) != null''',
)

replace_once(
    path,
    '''    fun updateSpellcasting(updated: CharacterSpellcastingDraftV4) {
        spellcastingDraftJson = characterSpellcastingDraftToJsonV4(updated)
        savedMessage = null
    }

    fun persist(candidate: CharacterSheet) {''',
    '''    fun updateSpellcasting(updated: CharacterSpellcastingDraftV4) {
        spellcastingDraftJson = characterSpellcastingDraftToJsonV4(updated)
        savedMessage = null
    }

    fun updateNotes(updated: CharacterNotesDraftV4) {
        notesDraftJson = characterNotesDraftToJsonV4(updated)
        savedMessage = null
    }

    fun persist(candidate: CharacterSheet) {''',
)

replace_once(
    path,
    '''    fun persist(candidate: CharacterSheet) {
        val equipment = equipmentDraftFromJsonV4(equipmentDraftJson)
        val spellcasting = characterSpellcastingDraftFromJsonV4(spellcastingDraftJson)
        val integrated = candidate.copy(
            combatEntries = combatEntriesFromJsonV4(combatDraftJson),
            inventoryItems = equipment.items,
            currencies = equipment.currencies,
            background = characterBackgroundFromJsonV4(backgroundDraftJson),
            traits = characterTraitsFromJsonV4(traitsDraftJson),
            spellcastingSources = spellcasting.sources,
            spells = spellcasting.spells,
        )''',
    '''    fun persist(candidate: CharacterSheet) {
        val equipment = equipmentDraftFromJsonV4(equipmentDraftJson)
        val spellcasting = characterSpellcastingDraftFromJsonV4(spellcastingDraftJson)
        val notes = characterNotesDraftFromJsonV4(notesDraftJson)
        val integrated = candidate.copy(
            combatEntries = combatEntriesFromJsonV4(combatDraftJson),
            inventoryItems = equipment.items,
            currencies = equipment.currencies,
            background = characterBackgroundFromJsonV4(backgroundDraftJson),
            traits = characterTraitsFromJsonV4(traitsDraftJson),
            spellcastingSources = spellcasting.sources,
            spells = spellcasting.spells,
            generalNotes = notes.generalNotes,
            noteCards = notes.cards,
        )''',
)

replace_once(
    path,
    '''        spellcastingDraftJson = characterSpellcastingDraftToJsonV4(
            CharacterSpellcastingDraftV4(
                sources = stored.spellcastingSources,
                spells = stored.spells,
            ),
        )
        savedMessage = "Guardado"''',
    '''        spellcastingDraftJson = characterSpellcastingDraftToJsonV4(
            CharacterSpellcastingDraftV4(
                sources = stored.spellcastingSources,
                spells = stored.spells,
            ),
        )
        notesDraftJson = characterNotesDraftToJsonV4(
            CharacterNotesDraftV4(
                generalNotes = stored.generalNotes,
                cards = stored.noteCards,
            ),
        )
        savedMessage = "Guardado"''',
)

replace_once(
    path,
    '''                    CharacterTabV4.NOTES -> CharacterDomainShellV4(
                        title = "Notas",
                        description = "La navegación está lista. El editor persistente de Notas se incorpora en el Incremento J.",
                    )''',
    '''                    CharacterTabV4.NOTES -> CharacterNotesTabV4(
                        draft = notesDraft,
                        onDraftChange = ::updateNotes,
                        wide = wide,
                    )''',
)

print("Increment J asserted editor patches applied successfully")
