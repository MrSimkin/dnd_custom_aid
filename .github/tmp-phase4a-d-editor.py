from pathlib import Path

path = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
text = path.read_text()


def replace_once(old: str, new: str, label: str) -> None:
    global text
    count = text.count(old)
    if count != 1:
        raise SystemExit(f"{label}: expected exactly one match, found {count}")
    text = text.replace(old, new, 1)


replace_once(
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState\n",
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState\n"
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatDamageProfile\n",
    "combat-damage-import",
)

replace_once(
    '''    var closureState by remember(characterId) {
        mutableStateOf(closureRepository.state(characterId))
    }
    var draft by rememberSaveable(
''',
    '''    var closureState by remember(characterId) {
        mutableStateOf(closureRepository.state(characterId))
    }
    val pcSettingsContext = LocalCharacterPcSettingsContextV4.current
    val successorState = pcSettingsContext?.successorState ?: CharacterSuccessorState()
    var draft by rememberSaveable(
''',
    "successor-context",
)

replace_once(
    '''    var combatDraftJson by rememberSaveable(characterId.toString()) {
        mutableStateOf(combatEntriesToJsonV4(stored.combatEntries))
    }
    var equipmentDraftJson by rememberSaveable(characterId.toString()) {
''',
    '''    var combatDraftJson by rememberSaveable(characterId.toString()) {
        mutableStateOf(combatEntriesToJsonV4(stored.combatEntries))
    }
    var combatDamageDraftJson by rememberSaveable(characterId.toString(), "combat-damage") {
        mutableStateOf(characterCombatDamageProfilesToJsonV4(successorState.combatDamage))
    }
    var equipmentDraftJson by rememberSaveable(characterId.toString()) {
''',
    "combat-damage-draft",
)

replace_once(
    '''    val combatEntries = remember(combatDraftJson) { combatEntriesFromJsonV4(combatDraftJson) }
    val equipmentDraft = remember(equipmentDraftJson) { equipmentDraftFromJsonV4(equipmentDraftJson) }
''',
    '''    val combatEntries = remember(combatDraftJson) { combatEntriesFromJsonV4(combatDraftJson) }
    val combatDamageProfiles = remember(combatDamageDraftJson) {
        characterCombatDamageProfilesFromJsonV4(combatDamageDraftJson)
    }
    val equipmentDraft = remember(equipmentDraftJson) { equipmentDraftFromJsonV4(equipmentDraftJson) }
''',
    "combat-damage-projection",
)

replace_once(
    '''    val storedCombatDraftJson = remember(stored) { combatEntriesToJsonV4(stored.combatEntries) }
    val storedEquipmentDraftJson = remember(stored, closureState.inventoryUsage) {
''',
    '''    val storedCombatDraftJson = remember(stored) { combatEntriesToJsonV4(stored.combatEntries) }
    val storedCombatDamageDraftJson = remember(successorState.combatDamage) {
        characterCombatDamageProfilesToJsonV4(successorState.combatDamage)
    }
    val storedEquipmentDraftJson = remember(stored, closureState.inventoryUsage) {
''',
    "stored-combat-damage",
)

replace_once(
    '''        draft.toJson() != storedDraftJson ||
            combatDraftJson != storedCombatDraftJson ||
            equipmentDraftJson != storedEquipmentDraftJson ||
''',
    '''        draft.toJson() != storedDraftJson ||
            combatDraftJson != storedCombatDraftJson ||
            combatDamageDraftJson != storedCombatDamageDraftJson ||
            equipmentDraftJson != storedEquipmentDraftJson ||
''',
    "unsaved-combat-damage",
)

replace_once(
    '''    fun updateCombatEntries(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry>) {
        if (!structuralEditingEnabled) return
        combatDraftJson = combatEntriesToJsonV4(updated)
        savedMessage = null
    }

    fun updateEquipmentItems''',
    '''    fun updateCombatEntries(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry>) {
        if (!structuralEditingEnabled) return
        combatDraftJson = combatEntriesToJsonV4(updated)
        savedMessage = null
    }

    fun updateCombatDamageProfiles(updated: List<CharacterCombatDamageProfile>) {
        if (!structuralEditingEnabled) return
        combatDamageDraftJson = characterCombatDamageProfilesToJsonV4(updated)
        savedMessage = null
    }

    fun updateEquipmentItems''',
    "update-combat-damage",
)

replace_once(
    '''        stored = repository.saveCharacter(integrated)
        val liveTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id }
''',
    '''        stored = repository.saveCharacter(integrated)
        val liveCombatEntryIds = stored.combatEntries.mapTo(mutableSetOf()) { it.id }
        val savedDamageProfiles = characterCombatDamageProfilesFromJsonV4(combatDamageDraftJson)
            .filter { it.combatEntryId in liveCombatEntryIds }
        pcSettingsContext?.onSuccessorStateChange?.invoke(
            successorState.copy(combatDamage = savedDamageProfiles),
        )
        combatDamageDraftJson = characterCombatDamageProfilesToJsonV4(savedDamageProfiles)
        val liveTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id }
''',
    "persist-combat-damage",
)

replace_once(
    '''        draft = CharacterEditorDraftV4.from(stored)
        combatDraftJson = combatEntriesToJsonV4(stored.combatEntries)
        equipmentDraftJson = equipmentDraftToJsonV4(
''',
    '''        draft = CharacterEditorDraftV4.from(stored)
        combatDraftJson = combatEntriesToJsonV4(stored.combatEntries)
        combatDamageDraftJson = characterCombatDamageProfilesToJsonV4(savedDamageProfiles)
        equipmentDraftJson = equipmentDraftToJsonV4(
''',
    "reset-combat-damage",
)

replace_once(
    '''                        CharacterTabV4.COMBAT -> CharacterCombatTabV4(
                            armorClass = draft.armorClass,
                            initiative = draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(),
                            speed = draft.speed,
                            sheet = stored,
                            closureState = closureState,
                            persistedEntryIds = stored.combatEntries.mapTo(mutableSetOf()) { it.id },
                            entries = combatEntries,
                            onEntriesChange = ::updateCombatEntries,
                            onOperationalSheetChange = ::persistCombatOperationalSheet,
                            onClosureStateChange = ::persistStructuralClosureState,
                            structuralEditingEnabled = structuralEditingEnabled,
                            hapticsEnabled = closureState.hapticsEnabled,
                            wide = wide,
                        )
                        CharacterTabV4.DICE -> CharacterDiceRollTabV4(
                            sheet = settingsSheet,
                            closureState = closureState,
                            combatEntries = combatEntries,
                        )
''',
    '''                        CharacterTabV4.COMBAT -> CharacterCombatSuccessorTabV4(
                            armorClass = draft.armorClass,
                            initiative = draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(),
                            speed = draft.speed,
                            sheet = stored,
                            closureState = closureState,
                            persistedEntryIds = stored.combatEntries.mapTo(mutableSetOf()) { it.id },
                            entries = combatEntries,
                            damageProfiles = combatDamageProfiles,
                            onEntriesChange = ::updateCombatEntries,
                            onDamageProfilesChange = ::updateCombatDamageProfiles,
                            onOperationalSheetChange = ::persistCombatOperationalSheet,
                            onClosureStateChange = ::persistStructuralClosureState,
                            structuralEditingEnabled = structuralEditingEnabled,
                            hapticsEnabled = closureState.hapticsEnabled,
                            wide = wide,
                        )
                        CharacterTabV4.DICE -> CharacterDiceRollSuccessorTabV4(
                            sheet = overviewProjectionSheet,
                            closureState = closureState,
                            combatEntries = combatEntries,
                            successorState = successorState.copy(combatDamage = combatDamageProfiles),
                        )
''',
    "wire-successor-combat-dice",
)

path.write_text(text)
