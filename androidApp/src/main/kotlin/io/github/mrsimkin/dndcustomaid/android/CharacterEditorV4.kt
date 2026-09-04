package io.github.mrsimkin.dndcustomaid.android

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSavingThrow
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSlot
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility
import io.github.mrsimkin.dndcustomaid.shared.character.abilityModifierForScore
import io.github.mrsimkin.dndcustomaid.shared.character.standardProficiencyBonusForLevel
import io.github.mrsimkin.dndcustomaid.shared.character.suggestedCharacterModules
import io.github.mrsimkin.dndcustomaid.shared.character.visibleCharacterModules
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

private val classNamesV4 = listOf(
    "Artífice",
    "Bárbaro",
    "Bardo",
    "Brujo",
    "Clérigo",
    "Druida",
    "Explorador",
    "Guerrero",
    "Hechicero",
    "Mago",
    "Monje",
    "Paladín",
    "Pícaro",
)

@Composable
internal fun CharacterEditorScreenV4(
    characterId: Uuid,
    repository: CharacterRepository,
    closureRepository: CharacterClosureRepository,
    navigationPreferenceStore: CharacterNavigationPreferenceStore,
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
    onOpenApplicationSettings: () -> Unit,
    onBack: () -> Unit,
) {
    var stored by remember(characterId) {
        mutableStateOf(requireNotNull(repository.character(characterId)))
    }
    var closureState by remember(characterId) {
        mutableStateOf(closureRepository.state(characterId))
    }
    var draft by rememberSaveable(
        characterId.toString(),
        stateSaver = CharacterEditorDraftV4.Saver,
    ) {
        mutableStateOf(CharacterEditorDraftV4.from(stored))
    }
    var combatDraftJson by rememberSaveable(characterId.toString()) {
        mutableStateOf(combatEntriesToJsonV4(stored.combatEntries))
    }
    var equipmentDraftJson by rememberSaveable(characterId.toString()) {
        mutableStateOf(
            equipmentDraftToJsonV4(
                CharacterEquipmentDraftV4(
                    items = stored.inventoryItems,
                    currencies = stored.currencies,
                    inventoryUsage = closureState.inventoryUsage,
                ),
            ),
        )
    }
    var backgroundDraftJson by rememberSaveable(characterId.toString(), "background") {
        mutableStateOf(characterBackgroundToJsonV4(stored.background))
    }
    var traitsDraftJson by rememberSaveable(characterId.toString(), "traits") {
        mutableStateOf(characterTraitsToJsonV4(stored.traits))
    }
    var spellcastingDraftJson by rememberSaveable(characterId.toString(), "spellcasting") {
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
    var h1ModuleDraftJson by rememberSaveable(characterId.toString(), "h1-modules") {
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
    var savedMessage by rememberSaveable(characterId.toString()) { mutableStateOf<String?>(null) }
    var selectedTabName by rememberSaveable(characterId.toString(), "selected-tab") {
        mutableStateOf(
            navigationPreferenceStore.loadLastTabName(characterId)
                ?: CharacterTabV4.OVERVIEW.name,
        )
    }
    var confirmBlankNumbers by rememberSaveable(characterId.toString()) { mutableStateOf(false) }
    var showPcSettings by rememberSaveable(characterId.toString(), "pc-settings") { mutableStateOf(false) }
    var showSupercompact by rememberSaveable(characterId.toString(), "supercompact") { mutableStateOf(false) }
    var confirmDisableSpellcasting by rememberSaveable(characterId.toString(), "disable-spellcasting") { mutableStateOf(false) }
    var confirmUnsavedLeave by rememberSaveable(characterId.toString(), "unsaved-leave") { mutableStateOf(false) }
    var leaveAfterSave by rememberSaveable(characterId.toString(), "leave-after-save") { mutableStateOf(false) }

    val combatEntries = remember(combatDraftJson) { combatEntriesFromJsonV4(combatDraftJson) }
    val equipmentDraft = remember(equipmentDraftJson) { equipmentDraftFromJsonV4(equipmentDraftJson) }
    val backgroundDraft = remember(backgroundDraftJson) { characterBackgroundFromJsonV4(backgroundDraftJson) }
    val traitsDraft = remember(traitsDraftJson) { characterTraitsFromJsonV4(traitsDraftJson) }
    val spellcastingDraft = remember(spellcastingDraftJson) { characterSpellcastingDraftFromJsonV4(spellcastingDraftJson) }
    val notesDraft = remember(notesDraftJson) { characterNotesDraftFromJsonV4(notesDraftJson) }
    val h1ModuleDraft = remember(h1ModuleDraftJson) { characterH1ModuleDraftFromJsonV4(h1ModuleDraftJson) }
    val settingsSheet = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: stored
    val suggestedModules = suggestedCharacterModules(settingsSheet.classes)
    val visibleModules = visibleCharacterModules(settingsSheet.classes, closureState.moduleOverrides)
    val selectedTab = resolvedCharacterTabV4(
        savedTabName = selectedTabName,
        spellcasterEnabled = stored.spellcasterEnabled,
        visibleModules = visibleModules,
    )
    LaunchedEffect(characterId, selectedTab.name) {
        if (selectedTabName != selectedTab.name) {
            selectedTabName = selectedTab.name
        }
        navigationPreferenceStore.saveLastTabName(characterId, selectedTab.name)
    }
    val savable = draft.toSheetOrNull(stored, blankRequiredAsZero = true) != null
    val storedDraftJson = remember(stored) { CharacterEditorDraftV4.from(stored).toJson() }
    val storedCombatDraftJson = remember(stored) { combatEntriesToJsonV4(stored.combatEntries) }
    val storedEquipmentDraftJson = remember(stored, closureState.inventoryUsage) {
        equipmentDraftToJsonV4(
            CharacterEquipmentDraftV4(
                items = stored.inventoryItems,
                currencies = stored.currencies,
                inventoryUsage = closureState.inventoryUsage,
            ),
        )
    }
    val storedBackgroundDraftJson = remember(stored) { characterBackgroundToJsonV4(stored.background) }
    val storedTraitsDraftJson = remember(stored) { characterTraitsToJsonV4(stored.traits) }
    val storedSpellcastingDraftJson = remember(stored) {
        characterSpellcastingDraftToJsonV4(
            CharacterSpellcastingDraftV4(
                sources = stored.spellcastingSources,
                spells = stored.spells,
            ),
        )
    }
    val storedNotesDraftJson = remember(stored) {
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
                companions = stored.companions,
            ),
        )
    }
    val hasUnsavedChanges =
        draft.toJson() != storedDraftJson ||
            combatDraftJson != storedCombatDraftJson ||
            equipmentDraftJson != storedEquipmentDraftJson ||
            backgroundDraftJson != storedBackgroundDraftJson ||
            traitsDraftJson != storedTraitsDraftJson ||
            spellcastingDraftJson != storedSpellcastingDraftJson ||
            notesDraftJson != storedNotesDraftJson ||
            h1ModuleDraftJson != storedH1ModuleDraftJson

    fun requestBack() {
        if (hasUnsavedChanges) {
            confirmUnsavedLeave = true
        } else {
            onBack()
        }
    }

    BackHandler(enabled = showSupercompact) {
        showSupercompact = false
    }
    BackHandler(enabled = !showSupercompact && showPcSettings) {
        showPcSettings = false
        selectedTabName = resolvedCharacterTabV4(
            savedTabName = selectedTabName,
            spellcasterEnabled = stored.spellcasterEnabled,
            visibleModules = visibleModules,
        ).name
    }
    BackHandler(
        enabled = !showSupercompact && !showPcSettings && !confirmUnsavedLeave && !confirmBlankNumbers && !confirmDisableSpellcasting,
    ) {
        requestBack()
    }

    fun updateDraft(updated: CharacterEditorDraftV4) {
        draft = updated
        savedMessage = null
    }

    fun updateCombatEntries(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry>) {
        combatDraftJson = combatEntriesToJsonV4(updated)
        savedMessage = null
    }

    fun updateEquipmentItems(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem>) {
        equipmentDraftJson = equipmentDraftToJsonV4(equipmentDraft.copy(items = updated))
        savedMessage = null
    }

    fun updateCurrencies(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency>) {
        equipmentDraftJson = equipmentDraftToJsonV4(equipmentDraft.copy(currencies = updated))
        savedMessage = null
    }

    fun updateEquipmentDraft(updated: CharacterEquipmentDraftV4) {
        equipmentDraftJson = equipmentDraftToJsonV4(updated)
        savedMessage = null
    }

    fun updateBackground(updated: io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground) {
        backgroundDraftJson = characterBackgroundToJsonV4(updated)
        savedMessage = null
    }

    fun updateTraits(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait>) {
        traitsDraftJson = characterTraitsToJsonV4(updated)
        savedMessage = null
    }

    fun updateSpellcasting(updated: CharacterSpellcastingDraftV4) {
        spellcastingDraftJson = characterSpellcastingDraftToJsonV4(updated)
        savedMessage = null
    }

    fun updateNotes(updated: CharacterNotesDraftV4) {
        notesDraftJson = characterNotesDraftToJsonV4(updated)
        savedMessage = null
    }

    fun updateH1Modules(updated: CharacterH1ModuleDraftV4) {
        h1ModuleDraftJson = characterH1ModuleDraftToJsonV4(updated)
        savedMessage = null
    }

    fun persist(candidate: CharacterSheet) {
        val shouldLeaveAfterPersist = leaveAfterSave
        val equipment = equipmentDraftFromJsonV4(equipmentDraftJson)
        val spellcasting = characterSpellcastingDraftFromJsonV4(spellcastingDraftJson)
        val notes = characterNotesDraftFromJsonV4(notesDraftJson)
        val h1Modules = characterH1ModuleDraftFromJsonV4(h1ModuleDraftJson)
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
            classOptions = h1Modules.classOptions,
            forms = h1Modules.forms,
            companions = h1Modules.companions,
        )
        stored = repository.saveCharacter(integrated)
        val liveTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id }
        val liveSpellIds = stored.spells.mapTo(mutableSetOf()) { it.id }
        val liveClassOptionIds = stored.classOptions.mapTo(mutableSetOf()) { it.id }
        val liveFormIds = stored.forms.mapTo(mutableSetOf()) { it.id }
        val liveCompanionIds = stored.companions.mapTo(mutableSetOf()) { it.id }
        val prunedQuickAccess = closureState.quickAccess
            .filter { reference ->
                when (reference.kind) {
                    CharacterQuickAccessKind.TRAIT -> reference.targetId in liveTraitIds
                    CharacterQuickAccessKind.SPELL -> reference.targetId in liveSpellIds
                    CharacterQuickAccessKind.CLASS_OPTION -> reference.targetId in liveClassOptionIds
                    CharacterQuickAccessKind.FORM -> reference.targetId in liveFormIds
                    CharacterQuickAccessKind.COMPANION -> reference.targetId in liveCompanionIds
                    else -> true
                }
            }
            .mapIndexed { index, reference -> reference.copy(sortOrder = index) }
        closureState = closureRepository.saveState(
            characterId,
            closureState.copy(
                inventoryUsage = equipment.inventoryUsage,
                quickAccess = prunedQuickAccess,
            ),
        )
        draft = CharacterEditorDraftV4.from(stored)
        combatDraftJson = combatEntriesToJsonV4(stored.combatEntries)
        equipmentDraftJson = equipmentDraftToJsonV4(
            CharacterEquipmentDraftV4(
                items = stored.inventoryItems,
                currencies = stored.currencies,
                inventoryUsage = closureState.inventoryUsage,
            ),
        )
        backgroundDraftJson = characterBackgroundToJsonV4(stored.background)
        traitsDraftJson = characterTraitsToJsonV4(stored.traits)
        spellcastingDraftJson = characterSpellcastingDraftToJsonV4(
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
        h1ModuleDraftJson = characterH1ModuleDraftToJsonV4(
            CharacterH1ModuleDraftV4(
                classOptions = stored.classOptions,
                forms = stored.forms,
                companions = stored.companions,
            ),
        )
        leaveAfterSave = false
        savedMessage = "Guardado"
        if (shouldLeaveAfterPersist) {
            onBack()
        }
    }

    fun save() {
        if (draft.missingRequiredNumberLabels().isNotEmpty()) {
            confirmBlankNumbers = true
            return
        }
        val candidate = draft.toSheetOrNull(stored) ?: return
        persist(candidate)
    }

    fun saveBlankNumbersAsZero() {
        val candidate = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: return
        confirmBlankNumbers = false
        persist(candidate)
    }

    fun persistSpellcasterEnabled(enabled: Boolean) {
        if (enabled == stored.spellcasterEnabled) return
        stored = repository.saveCharacter(stored.copy(spellcasterEnabled = enabled))
        if (!enabled && selectedTabName == CharacterTabV4.SPELLS.name) {
            selectedTabName = CharacterTabV4.OVERVIEW.name
        }
        savedMessage = "Guardado"
    }

    fun persistStatus(status: CharacterStatus) {
        if (status == stored.status && status == draft.status) return
        stored = repository.saveCharacter(stored.copy(status = status))
        draft = draft.copy(status = status)
        savedMessage = "Guardado"
    }

    fun persistClosureState(updated: CharacterClosureState) {
        if (updated == closureState) return
        closureState = closureRepository.saveState(characterId, updated)
        savedMessage = "Guardado"
    }


    fun persistOperationalSheet(updated: CharacterSheet) {
        if (updated == stored) return
        stored = repository.saveCharacter(updated)
        savedMessage = "Guardado"
    }

    fun persistCombatOperationalSheet(updated: CharacterSheet) {
        if (updated == stored) return
        val previous = stored
        stored = repository.saveCharacter(updated)
        if (stored.currentHp != previous.currentHp || stored.tempHp != previous.tempHp) {
            draft = draft.copy(
                currentHp = stored.currentHp.toString(),
                tempHp = stored.tempHp.toString(),
            )
        }
        savedMessage = "Guardado"
    }

    if (showSupercompact) {
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
            onBack = {
                showPcSettings = false
                selectedTabName = resolvedCharacterTabV4(
                    savedTabName = selectedTabName,
                    spellcasterEnabled = stored.spellcasterEnabled,
                    visibleModules = visibleModules,
                ).name
            },
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
        Scaffold { scaffoldPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
            ) {
                val navigationPresentation = characterNavigationPresentationForWidthV4(maxWidth.value)
                val wide = maxWidth >= 700.dp
                CharacterAdaptiveShellV4(
                    navigationPresentation = navigationPresentation,
                    selectedTab = selectedTab,
                    spellcasterEnabled = stored.spellcasterEnabled,
                    visibleModules = visibleModules,
                    onSelect = { selectedTabName = it.name },
                    header = {
                        EditorHeaderV4(
                            characterName = draft.name,
                            stored = stored,
                            savedMessage = savedMessage,
                            hasUnsavedChanges = hasUnsavedChanges,
                            savable = savable,
                            onBack = ::requestBack,
                            onSave = ::save,
                            onOpenSettings = { showPcSettings = true },
                        )
                    },
                ) {
                    when (selectedTab) {
                        CharacterTabV4.OVERVIEW -> OverviewTabV4(
                            draft = draft,
                            stored = stored,
                            closureState = closureState,
                            wide = wide,
                            onDraftChange = ::updateDraft,
                            onClosureStateChange = ::persistClosureState,
                        )
                        CharacterTabV4.SKILLS -> SkillsTabV4(
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
                        CharacterTabV4.COMBAT -> CharacterCombatTabV4(
                            armorClass = draft.armorClass,
                            initiative = draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(),
                            speed = draft.speed,
                            sheet = stored,
                            closureState = closureState,
                            persistedEntryIds = stored.combatEntries.mapTo(mutableSetOf()) { it.id },
                            entries = combatEntries,
                            onEntriesChange = ::updateCombatEntries,
                            onOperationalSheetChange = ::persistCombatOperationalSheet,
                            onClosureStateChange = ::persistClosureState,
                            hapticsEnabled = closureState.hapticsEnabled,
                            wide = wide,
                        )
                        CharacterTabV4.MANAGEMENT -> CharacterManagementTabV4(
                            sheet = stored,
                            closureState = closureState,
                            onSheetChange = ::persistOperationalSheet,
                            onClosureStateChange = ::persistClosureState,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.EQUIPMENT -> CharacterEquipmentClosureTabV4(
                            draft = equipmentDraft,
                            onDraftChange = ::updateEquipmentDraft,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.BACKGROUND -> CharacterBackgroundTabV4(
                            background = backgroundDraft,
                            onBackgroundChange = ::updateBackground,
                            wide = wide,
                        )
                        CharacterTabV4.TRAITS -> CharacterTraitsClosureTabV4(
                            traits = traitsDraft,
                            closureState = closureState,
                            persistedTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id },
                            onTraitsChange = ::updateTraits,
                            onClosureStateChange = ::persistClosureState,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.SPELLS -> CharacterSpellsTabV4(
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
                            closureState = closureState,
                            persistedSpellIds = stored.spells.mapTo(mutableSetOf()) { it.id },
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
                            onClosureStateChange = ::persistClosureState,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.ARTIFICER -> CharacterArtificeModuleV4(
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
                        CharacterTabV4.TECHNIQUES -> CharacterTechniquesModuleV4(
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
                        CharacterTabV4.METAMAGIC -> CharacterMetamagicModuleV4(
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
                        CharacterTabV4.PACTS -> CharacterPactsModuleV4(
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
                        CharacterTabV4.COMPANIONS -> CharacterCompanionsModuleV4(
                            companions = h1ModuleDraft.companions,
                            classes = settingsSheet.classes,
                            closureState = closureState,
                            persistedCompanionIds = stored.companions.mapTo(mutableSetOf()) { it.id },
                            onCompanionsChange = { updated ->
                                updateH1Modules(h1ModuleDraft.copy(companions = updated))
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
                        )
                    }
                }
            }
        }
    }

    if (confirmDisableSpellcasting) {
        AlertDialog(
            onDismissRequest = { confirmDisableSpellcasting = false },
            title = { Text("Ocultar funciones de conjuros") },
            text = {
                Text("Lanzamiento de Conjuros y la pestaña Conjuros se ocultarán. Los datos de conjuros, fuentes, preparación y espacios se conservarán.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        persistSpellcasterEnabled(false)
                        confirmDisableSpellcasting = false
                    },
                ) { Text("Ocultar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDisableSpellcasting = false }) { Text("Cancelar") }
            },
        )
    }

    if (confirmBlankNumbers) {
        val missing = draft.missingRequiredNumberLabels()
        AlertDialog(
            onDismissRequest = {
                confirmBlankNumbers = false
                leaveAfterSave = false
            },
            title = { Text("Guardar campos vacíos como 0") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Hay campos numéricos requeridos vacíos. Si continúas, se guardarán como 0.")
                    if (missing.isNotEmpty()) {
                        val shown = missing.take(8).joinToString(", ")
                        Text(
                            if (missing.size > 8) "$shown…" else shown,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = ::saveBlankNumbersAsZero) { Text("Guardar con 0") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirmBlankNumbers = false
                        leaveAfterSave = false
                    },
                ) { Text("Cancelar") }
            },
        )
    }

    if (confirmUnsavedLeave) {
        CharacterUnsavedChangesDialogV4(
            onSave = {
                confirmUnsavedLeave = false
                leaveAfterSave = true
                save()
            },
            onDiscard = {
                confirmUnsavedLeave = false
                leaveAfterSave = false
                onBack()
            },
            onKeepEditing = {
                confirmUnsavedLeave = false
                leaveAfterSave = false
            },
            saveEnabled = savable,
        )
    }
}

@Composable
private fun EditorHeaderV4(
    characterName: String,
    stored: CharacterSheet,
    savedMessage: String?,
    hasUnsavedChanges: Boolean,
    savable: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StableBackIconButton(onClick = onBack, contentDescription = "Volver a personajes")
        Column(modifier = Modifier.weight(1f)) {
            Text(
                characterName.ifBlank { "Ficha de personaje" },
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
            )
            Text(
                when {
                    hasUnsavedChanges -> "Cambios sin guardar"
                    savedMessage != null -> savedMessage
                    else -> "Guardado: ${formatSavedAtV4(stored.updatedAtEpochSeconds)}"
                },
                style = if (hasUnsavedChanges) {
                    MaterialTheme.typography.labelMedium
                } else {
                    MaterialTheme.typography.labelSmall
                },
                maxLines = 1,
            )
        }
        StableSettingsIconButton(onClick = onOpenSettings)
        Button(onClick = onSave, enabled = savable) { Text("Guardar") }
    }
}

@Composable
private fun OverviewTabV4(
    draft: CharacterEditorDraftV4,
    stored: CharacterSheet,
    closureState: CharacterClosureState,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = if (wide) 10.dp else 5.dp,
            end = if (wide) 10.dp else 5.dp,
            top = 5.dp,
            bottom = 150.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        item {
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
            CharacterClassIdentityCardV4(
                classes = draft.classes,
                onClassesChange = { onDraftChange(draft.copy(classes = it)) },
            )
        }
        item {
            AbilitiesCardV4(draft, onDraftChange)
        }
        item {
            CombatCardV4(draft, wide, onDraftChange)
        }
        if (stored.spellcasterEnabled) {
            item {
                QuickMagicCardV4(draft, onDraftChange)
            }
        }
    }
}

@Composable
private fun IdentityCardV4(
    draft: CharacterEditorDraftV4,
    stored: CharacterSheet,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    SectionCardV4("Personaje") {
        OutlinedTextField(
            value = draft.name,
            onValueChange = { onDraftChange(draft.copy(name = it)) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Text(
            "Nivel total ${draft.totalLevel()} · Último guardado ${formatSavedAtV4(stored.updatedAtEpochSeconds)}",
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun StatusSelectorV4(
    status: CharacterStatus,
    onStatusChange: (CharacterStatus) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(statusLabelV4(status), maxLines = 1)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            CharacterStatus.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(statusLabelV4(option)) },
                    onClick = {
                        onStatusChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun ClassesCardV4(
    classes: List<ClassLevelDraftV4>,
    onClassesChange: (List<ClassLevelDraftV4>) -> Unit,
) {
    SectionCardV4("Clases y Dados de Golpe") {
        if (classes.isEmpty()) {
            Text("Sin clases registradas.", style = MaterialTheme.typography.bodySmall)
        }
        classes.forEach { classDraft ->
            ClassRowV4(
                draft = classDraft,
                onChange = { changed ->
                    onClassesChange(classes.map { if (it.id == changed.id) changed else it })
                },
                onRemove = { onClassesChange(classes.filterNot { it.id == classDraft.id }) },
            )
        }
        TextButton(
            onClick = {
                onClassesChange(
                    classes + ClassLevelDraftV4(
                        id = Uuid.random(),
                        name = "",
                        level = "1",
                        hitDieSides = "8",
                        hitDiceRemaining = "1",
                    ),
                )
            },
        ) {
            Text("+ Añadir clase")
        }
    }
}

@Composable
private fun ClassRowV4(
    draft: ClassLevelDraftV4,
    onChange: (ClassLevelDraftV4) -> Unit,
    onRemove: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 3.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Top,
        ) {
            ClassSelectorV4(
                draft = draft,
                onChange = onChange,
                modifier = Modifier.weight(1f),
            )
            CompactIntFieldV4(
                label = "Nv.",
                value = draft.level,
                onValueChange = { onChange(draft.copy(level = it)) },
                modifier = Modifier.width(44.dp),
            )
            CompactIntFieldV4(
                label = "DG",
                value = draft.hitDiceRemaining,
                onValueChange = { onChange(draft.copy(hitDiceRemaining = it)) },
                modifier = Modifier.width(44.dp),
            )
            HitDieSelectorV4(
                value = draft.hitDieSides,
                onValueChange = { onChange(draft.copy(hitDieSides = it)) },
            )
            Column {
                CompactFieldLabelV4("")
                StableRemoveIconButton(
                    onClick = onRemove,
                    contentDescription = "Eliminar clase ${draft.name.ifBlank { "sin nombre" }}",
                )
            }
        }
    }
}

@Composable
private fun ClassSelectorV4(
    draft: ClassLevelDraftV4,
    onChange: (ClassLevelDraftV4) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    var customMode by remember(draft.id, draft.name) {
        mutableStateOf(draft.name.isNotBlank() && draft.name !in classNamesV4)
    }

    Column(modifier = modifier) {
        CompactFieldLabelV4("Clase")
        if (customMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompactTextFieldV4(
                    value = draft.name,
                    onValueChange = { onChange(draft.withManualName(it)) },
                    modifier = Modifier.weight(1f),
                )
                StableDropdownIconButton(
                    onClick = {
                        customMode = false
                        expanded = true
                    },
                    contentDescription = "Abrir lista de clases",
                )
            }
        } else {
            Box {
                CompactMenuSurfaceV4(
                    text = draft.name.ifBlank { "Elegir" },
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    classNamesV4.forEach { className ->
                        DropdownMenuItem(
                            text = { Text(className) },
                            onClick = {
                                onChange(draft.withManualName(className))
                                customMode = false
                                expanded = false
                            },
                        )
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Otro") },
                        onClick = {
                            onChange(draft.withManualName(if (draft.name in classNamesV4) "" else draft.name))
                            customMode = true
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun HitDieSelectorV4(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val commonDice = listOf("4", "6", "8", "10", "12")
    var expanded by remember { mutableStateOf(false) }
    var customMode by remember(value) { mutableStateOf(value.isNotBlank() && value !in commonDice) }

    Column(modifier = Modifier.width(78.dp)) {
        CompactFieldLabelV4("Tipo")
        if (customMode) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CompactIntInputV4(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                )
                StableDropdownIconButton(
                    onClick = {
                        customMode = false
                        expanded = true
                    },
                    contentDescription = "Abrir lista de dados de golpe",
                )
            }
        } else {
            Box {
                CompactMenuSurfaceV4(
                    text = if (value.isBlank()) "d?" else "d$value",
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    commonDice.forEach { sides ->
                        DropdownMenuItem(
                            text = { Text("d$sides", maxLines = 1) },
                            onClick = {
                                onValueChange(sides)
                                customMode = false
                                expanded = false
                            },
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Otro") },
                        onClick = {
                            customMode = true
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AbilitiesCardV4(
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    SectionCardV4("Características") {
        AbilitiesRowV4(draft, onDraftChange)
    }
}

@Composable
private fun AbilitiesRowV4(
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        CharacterAbility.entries.forEach { ability ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(abilityAbbreviationV4(ability), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                CompactIntInputV4(
                    value = draft.abilityValue(ability),
                    onValueChange = { onDraftChange(draft.withAbilityValue(ability, it)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    draft.abilityModifier(ability)?.let(::formatSignedV4) ?: "—",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun CombatCardV4(
    draft: CharacterEditorDraftV4,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    SectionCardV4("Referencia de combate") {
        if (wide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Referencia", style = MaterialTheme.typography.labelMedium)
                    CombatExplicitRowV4(
                        draft,
                        labels = Triple("CA", "Iniciativa", "Velocidad"),
                        values = Triple(draft.armorClass, draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(), draft.speed),
                        onFirst = { onDraftChange(draft.copy(armorClass = it)) },
                        onSecondAdjustment = { onDraftChange(draft.copy(initiativeAdjustment = it)) },
                        onThird = { onDraftChange(draft.copy(speed = it)) },
                        secondAdjustment = draft.initiativeAdjustment,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Puntos de golpe", style = MaterialTheme.typography.labelMedium)
                    TripleExplicitFieldsV4(
                        "PG actuales", draft.currentHp, { onDraftChange(draft.copy(currentHp = it)) },
                        "PG máximos", draft.maxHp, { onDraftChange(draft.copy(maxHp = it)) },
                        "PG temporales", draft.tempHp, { onDraftChange(draft.copy(tempHp = it)) },
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Referencia secundaria", style = MaterialTheme.typography.labelMedium)
                    SecondaryCombatRowV4(draft, onDraftChange)
                }
            }
        } else {
            CombatExplicitRowV4(
                draft,
                labels = Triple("CA", "Iniciativa", "Velocidad"),
                values = Triple(draft.armorClass, draft.initiativeTotal()?.let(::formatSignedV4).orEmpty(), draft.speed),
                onFirst = { onDraftChange(draft.copy(armorClass = it)) },
                onSecondAdjustment = { onDraftChange(draft.copy(initiativeAdjustment = it)) },
                onThird = { onDraftChange(draft.copy(speed = it)) },
                secondAdjustment = draft.initiativeAdjustment,
            )
            TripleExplicitFieldsV4(
                "PG actuales", draft.currentHp, { onDraftChange(draft.copy(currentHp = it)) },
                "PG máximos", draft.maxHp, { onDraftChange(draft.copy(maxHp = it)) },
                "PG temporales", draft.tempHp, { onDraftChange(draft.copy(tempHp = it)) },
            )
            SecondaryCombatRowV4(draft, onDraftChange)
        }
    }
}

@Composable
private fun CombatExplicitRowV4(
    draft: CharacterEditorDraftV4,
    labels: Triple<String, String, String>,
    values: Triple<String, String, String>,
    onFirst: (String) -> Unit,
    onSecondAdjustment: (String) -> Unit,
    onThird: (String) -> Unit,
    secondAdjustment: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompactIntFieldV4(labels.first, values.first, onFirst, Modifier.weight(1f))
        DerivedValueCellV4(
            label = labels.second,
            total = values.second,
            adjustment = secondAdjustment,
            breakdownLines = listOf(
                "Destreza ${draft.abilityModifier(CharacterAbility.DEXTERITY)?.let(::formatSignedV4) ?: "—"}",
            ),
            onAdjustmentChange = onSecondAdjustment,
            modifier = Modifier.weight(1f),
        )
        SpeedFieldV4(
            label = labels.third,
            value = values.third,
            onValueChange = onThird,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SpeedFieldV4(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dialogOpen by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        CompactFieldLabelV4(label)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 38.dp)
                .clickable { dialogOpen = true },
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(formatSpeedV4(value), style = MaterialTheme.typography.bodyMedium, maxLines = 1)
            }
        }
    }

    if (dialogOpen) {
        var pending by remember(dialogOpen, value) { mutableStateOf(value) }
        CharacterImeSafeEditorDialog(
            title = label,
            onCancel = { dialogOpen = false },
            onSave = {
                onValueChange(pending)
                dialogOpen = false
            },
        ) {
            Text("Valor canónico en pies", style = MaterialTheme.typography.labelMedium)
            CompactIntInputV4(
                value = pending,
                onValueChange = { pending = it },
                modifier = Modifier.width(110.dp),
                placeholder = "0",
            )
            Text(
                "Vista: ${formatSpeedV4(pending)}",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun TripleExplicitFieldsV4(
    firstLabel: String,
    firstValue: String,
    onFirst: (String) -> Unit,
    secondLabel: String,
    secondValue: String,
    onSecond: (String) -> Unit,
    thirdLabel: String,
    thirdValue: String,
    onThird: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        CompactIntFieldV4(firstLabel, firstValue, onFirst, Modifier.weight(1f), signed = true)
        CompactIntFieldV4(secondLabel, secondValue, onSecond, Modifier.weight(1f))
        CompactIntFieldV4(thirdLabel, thirdValue, onThird, Modifier.weight(1f), signed = true)
    }
}

@Composable
private fun SecondaryCombatRowV4(
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DerivedValueCellV4(
            label = "Bono competencia",
            total = draft.finalProficiencyBonus()?.let(::formatSignedV4).orEmpty(),
            adjustment = draft.proficiencyBonusAdjustment,
            breakdownLines = listOf(
                "Nivel total ${draft.totalLevel()}",
                "Bono estándar ${formatSignedV4(draft.standardProficiencyBonus())}",
            ),
            onAdjustmentChange = { onDraftChange(draft.copy(proficiencyBonusAdjustment = it)) },
            modifier = Modifier.weight(1f),
        )
        DerivedValueCellV4(
            label = "Percepción pasiva",
            total = draft.passivePerceptionTotal()?.toString().orEmpty(),
            adjustment = draft.passivePerceptionAdjustment,
            breakdownLines = listOf(
                "Percepción ${draft.skillTotal(SkillKey.PERCEPTION)?.let(::formatSignedV4) ?: "—"}",
                "Base pasiva +10",
            ),
            onAdjustmentChange = { onDraftChange(draft.copy(passivePerceptionAdjustment = it)) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun QuickMagicCardV4(
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    var configureSlots by remember { mutableStateOf(false) }

    SectionCardV4("Lanzamiento de Conjuros") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.Top,
        ) {
            CompactIntFieldV4(
                label = "CD conjuros",
                value = draft.spellSaveDc,
                onValueChange = { onDraftChange(draft.copy(spellSaveDc = it)) },
                modifier = Modifier.weight(1f),
            )
            CompactIntFieldV4(
                label = "Ataque mágico",
                value = draft.spellAttackModifier,
                onValueChange = { onDraftChange(draft.copy(spellAttackModifier = it)) },
                modifier = Modifier.weight(1f),
                signed = true,
            )
            SpellcastingAbilitySelectorV4(
                ability = draft.spellcastingAbility,
                onChange = { onDraftChange(draft.copy(spellcastingAbility = it)) },
                modifier = Modifier.weight(1f),
            )
        }

        val activeSlots = draft.spellSlots.filter { (it.total.toIntOrNull() ?: 0) > 0 }
        if (activeSlots.isEmpty()) {
            Text("Sin espacios de conjuro configurados.", style = MaterialTheme.typography.bodySmall)
        } else {
            activeSlots.forEach { slot ->
                SpellSlotRowV4(
                    slot = slot,
                    onSpentChange = { spent ->
                        onDraftChange(draft.withSpellSlot(slot.copy(spent = spent)))
                    },
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            TextButton(onClick = { configureSlots = true }) {
                Text("Configurar espacios")
            }
            TextButton(
                onClick = {
                    onDraftChange(
                        draft.copy(
                            spellSlots = draft.spellSlots.map { it.copy(spent = 0) },
                        ),
                    )
                },
                enabled = activeSlots.any { it.spent > 0 },
            ) {
                Text("Restaurar espacios")
            }
        }
    }

    if (configureSlots) {
        var pendingTotals by remember(configureSlots) {
            mutableStateOf(
                (1..9).map { level ->
                    draft.spellSlotFor(level).total
                },
            )
        }
        CharacterImeSafeEditorDialog(
            title = "Configurar espacios",
            onCancel = { configureSlots = false },
            onSave = {
                val updated = (1..9).map { level ->
                    val old = draft.spellSlotFor(level)
                    val total = pendingTotals[level - 1].toIntOrNull()?.coerceAtLeast(0) ?: 0
                    old.copy(total = total.toString(), spent = old.spent.coerceIn(0, total))
                }
                onDraftChange(draft.copy(spellSlots = updated))
                configureSlots = false
            },
        ) {
            (1..9).forEachIndexed { index, level ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Nivel $level", modifier = Modifier.weight(1f))
                    CompactIntInputV4(
                        value = pendingTotals[index],
                        onValueChange = { value ->
                            pendingTotals = pendingTotals.mapIndexed { itemIndex, existing ->
                                if (itemIndex == index) value else existing
                            }
                        },
                        modifier = Modifier.width(70.dp),
                        placeholder = "0",
                    )
                }
            }
        }
    }
}

@Composable
private fun SpellcastingAbilitySelectorV4(
    ability: SpellcastingAbility,
    onChange: (SpellcastingAbility) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        CompactFieldLabelV4("Aptitud mágica")
        Box {
            CompactMenuSurfaceV4(
                text = spellcastingAbilityLabelV4(ability),
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                SpellcastingAbility.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(spellcastingAbilityLabelV4(option)) },
                        onClick = {
                            onChange(option)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun SpellSlotRowV4(
    slot: SpellSlotDraftV4,
    onSpentChange: (Int) -> Unit,
) {
    val total = slot.total.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val spent = slot.spent.coerceIn(0, total)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text("Nivel ${slot.level}", modifier = Modifier.width(55.dp), style = MaterialTheme.typography.labelMedium)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            (0 until total).toList().chunked(8).forEach { indices ->
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    indices.forEach { index ->
                        val isSpent = index < spent
                        val borderColor = if (isSpent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        Surface(
                            modifier = Modifier
                                .size(26.dp)
                                .clickable {
                                    val newSpent = if (isSpent) index else index + 1
                                    onSpentChange(newSpent.coerceIn(0, total))
                                },
                            shape = CircleShape,
                            border = BorderStroke(1.5.dp, borderColor),
                            color = if (isSpent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        ) {}
                    }
                }
            }
        }
        Text("$spent/$total", style = MaterialTheme.typography.labelSmall, maxLines = 1)
    }
}

@Composable
private fun DerivedValueCellV4(
    label: String,
    total: String,
    adjustment: String,
    breakdownLines: List<String>,
    onAdjustmentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        CompactFieldLabelV4(label)
        DerivedTotalControlV4(
            total = total,
            adjustment = adjustment,
            dialogTitle = label,
            breakdownLines = breakdownLines,
            onAdjustmentChange = onAdjustmentChange,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DerivedTotalControlV4(
    total: String,
    adjustment: String,
    dialogTitle: String,
    breakdownLines: List<String>,
    onAdjustmentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var dialogOpen by remember { mutableStateOf(false) }
    val adjustmentValue = parseOptionalAdjustmentV4(adjustment) ?: 0

    Surface(
        modifier = modifier
            .heightIn(min = 38.dp)
            .clickable { dialogOpen = true },
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(total.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                if (adjustmentValue != 0) {
                    Text(
                        "*",
                        modifier = Modifier.padding(start = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                    )
                }
            }
        }
    }

    if (dialogOpen) {
        var pendingAdjustment by remember(dialogOpen, adjustment) { mutableStateOf(adjustment) }
        CharacterImeSafeEditorDialog(
            title = dialogTitle,
            onCancel = { dialogOpen = false },
            onSave = {
                onAdjustmentChange(pendingAdjustment)
                dialogOpen = false
            },
        ) {
            breakdownLines.forEach { line ->
                Text(line, style = MaterialTheme.typography.bodyMedium)
            }
            Text("Ajuste adicional", style = MaterialTheme.typography.labelMedium)
            CompactIntInputV4(
                value = pendingAdjustment,
                onValueChange = { pendingAdjustment = it },
                modifier = Modifier.width(110.dp),
                signed = true,
                placeholder = "0",
            )
            Text("Total actual ${total.ifBlank { "—" }}", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun SkillsTabV4(
    draft: CharacterEditorDraftV4,
    closureState: CharacterClosureState,
    calculationSheet: CharacterSheet,
    wide: Boolean,
    skillLayoutChoice: SkillLayoutChoice,
    onSkillLayoutChange: (SkillLayoutChoice) -> Unit,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding(),
        contentPadding = PaddingValues(
            start = if (wide) 10.dp else 5.dp,
            end = if (wide) 10.dp else 5.dp,
            top = 5.dp,
            bottom = 170.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        item {
            SkillViewSelectorV4(skillLayoutChoice, onSkillLayoutChange)
        }
        item {
            CharacterPassiveSkillsCardV4(calculationSheet)
        }
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
    }
}

@Composable
private fun SkillViewSelectorV4(
    current: SkillLayoutChoice,
    onChange: (SkillLayoutChoice) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            SkillLayoutChoice.entries.forEach { choice ->
                val selected = choice == current
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onChange(choice) },
                    color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                ) {
                    Text(
                        choice.label,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        style = if (selected) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelMedium,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

@Composable
private fun SavesCardV4(
    draft: CharacterEditorDraftV4,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    SectionCardV4("Tiradas de salvación") {
        Text(
            "Marca competencia cuando corresponda. Toca el total para ver el cálculo y editar Ajuste adicional.",
            style = MaterialTheme.typography.labelSmall,
        )
        val columns = if (wide) 3 else 2
        CharacterAbility.entries.chunked(columns).forEach { abilities ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.Top,
            ) {
                abilities.forEach { ability ->
                    SaveRowV4(
                        ability = ability,
                        draft = draft,
                        onDraftChange = onDraftChange,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - abilities.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun SaveRowV4(
    ability: CharacterAbility,
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    modifier: Modifier = Modifier,
) {
    val save = draft.saveFor(ability)
    val abilityModifier = draft.abilityModifier(ability)
    val proficiency = draft.finalProficiencyBonus()
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(abilityAbbreviationV4(ability), style = MaterialTheme.typography.labelMedium)
            DerivedTotalControlV4(
                total = draft.savingThrowTotal(ability)?.let(::formatSignedV4).orEmpty(),
                adjustment = save.adjustment,
                dialogTitle = "Salvación ${abilityAbbreviationV4(ability)}",
                breakdownLines = listOf(
                    "${abilityAbbreviationV4(ability)} ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                    if (save.proficient) {
                        "Competencia ${proficiency?.let(::formatSignedV4) ?: "—"}"
                    } else {
                        "Sin competencia +0"
                    },
                ),
                onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },
                modifier = Modifier.weight(1f),
            )
            CharacterD20RollButtonV4(
                label = "Salvación ${abilityAbbreviationV4(ability)}",
                modifier = draft.savingThrowTotal(ability),
            )
            SaveProficiencyToggleV4(
                proficient = save.proficient,
                onToggle = {
                    onDraftChange(draft.withSave(save.copy(proficient = !save.proficient)))
                },
            )
        }
    }
}

@Composable
private fun SaveProficiencyToggleV4(
    proficient: Boolean,
    onToggle: () -> Unit,
) {
    val color = if (proficient) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    Surface(
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = onToggle),
        shape = CircleShape,
        border = BorderStroke(1.5.dp, color),
        color = if (proficient) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
    ) {
        if (proficient) {
            Canvas(modifier = Modifier.padding(9.dp)) {
                val stroke = 2.2.dp.toPx()
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.12f, size.height * 0.55f),
                    end = Offset(size.width * 0.42f, size.height * 0.82f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.42f, size.height * 0.82f),
                    end = Offset(size.width * 0.9f, size.height * 0.18f),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
private fun SkillsListCardV4(
    draft: CharacterEditorDraftV4,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    SectionCardV4("Habilidades") {
        Text(
            "El control cuadrado indica sin competencia, competencia o pericia. Toca el total para ver el cálculo y editar Ajuste adicional.",
            style = MaterialTheme.typography.labelSmall,
        )
        if (wide) {
            val midpoint = (draft.skills.size + 1) / 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    draft.skills.take(midpoint).forEachIndexed { index, skill ->
                        SkillRowV4(skill, draft, onDraftChange)
                        if (index < midpoint - 1) HorizontalDivider()
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    val second = draft.skills.drop(midpoint)
                    second.forEachIndexed { index, skill ->
                        SkillRowV4(skill, draft, onDraftChange)
                        if (index < second.lastIndex) HorizontalDivider()
                    }
                }
            }
        } else {
            draft.skills.forEachIndexed { index, skill ->
                SkillRowV4(skill, draft, onDraftChange)
                if (index < draft.skills.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
private fun SkillRowV4(
    skill: SkillDraftV4,
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    val abilityModifier = draft.abilityModifier(skill.key.ability)
    val proficiency = draft.finalProficiencyBonus()
    val proficiencyContribution = proficiency?.let {
        when (skill.training) {
            SkillTraining.NONE -> 0
            SkillTraining.PROFICIENT -> it
            SkillTraining.EXPERTISE -> it * 2
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${skillLabelV4(skill.key)} (${abilityAbbreviationV4(skill.key.ability)})",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
        )
        DerivedTotalControlV4(
            total = draft.skillTotal(skill.key)?.let(::formatSignedV4).orEmpty(),
            adjustment = skill.adjustment,
            dialogTitle = skillLabelV4(skill.key),
            breakdownLines = listOf(
                "${abilityAbbreviationV4(skill.key.ability)} ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                "${trainingLabelV4(skill.training)} ${proficiencyContribution?.let(::formatSignedV4) ?: "—"}",
            ),
            onAdjustmentChange = { onDraftChange(draft.withSkill(skill.copy(adjustment = it))) },
            modifier = Modifier.width(58.dp),
        )
        CharacterD20RollButtonV4(
            label = skillLabelV4(skill.key),
            modifier = draft.skillTotal(skill.key),
        )
        TrainingSelectorV4(
            training = skill.training,
            onTrainingChange = { onDraftChange(draft.withSkill(skill.copy(training = it))) },
        )
    }
}

@Composable
private fun TrainingSelectorV4(
    training: SkillTraining,
    onTrainingChange: (SkillTraining) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier
                .width(44.dp)
                .heightIn(min = 38.dp),
            contentPadding = PaddingValues(0.dp),
        ) {
            TrainingGlyphV4(training)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SkillTraining.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(trainingLabelV4(option)) },
                    onClick = {
                        onTrainingChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun TrainingGlyphV4(training: SkillTraining) {
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(modifier = Modifier.size(20.dp)) {
        val border = 1.5.dp.toPx()
        drawRect(color = color, style = Stroke(width = border))
        if (training != SkillTraining.NONE) {
            fun check(offsetY: Float) {
                val stroke = 1.8.dp.toPx()
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.12f, size.height * (0.48f + offsetY)),
                    end = Offset(size.width * 0.4f, size.height * (0.72f + offsetY)),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.4f, size.height * (0.72f + offsetY)),
                    end = Offset(size.width * 0.88f, size.height * (0.22f + offsetY)),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round,
                )
            }
            when (training) {
                SkillTraining.PROFICIENT -> check(0f)
                SkillTraining.EXPERTISE -> {
                    check(-0.12f)
                    check(0.12f)
                }
                SkillTraining.NONE -> Unit
            }
        }
    }
}

@Composable
private fun AbilityGroupsCardV4(
    draft: CharacterEditorDraftV4,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    SectionCardV4("Características, salvaciones y habilidades") {
        val columns = if (wide) 3 else 2
        CharacterAbility.entries.chunked(columns).forEach { rowAbilities ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.Top,
            ) {
                rowAbilities.forEach { ability ->
                    AbilityGroupV4(
                        ability = ability,
                        draft = draft,
                        onDraftChange = onDraftChange,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - rowAbilities.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun AbilityGroupV4(
    ability: CharacterAbility,
    draft: CharacterEditorDraftV4,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    modifier: Modifier = Modifier,
) {
    val save = draft.saveFor(ability)
    val abilityModifier = draft.abilityModifier(ability)
    val proficiency = draft.finalProficiencyBonus()
    val abbreviation = abilityAbbreviationV4(ability)

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(abbreviation, modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, maxLines = 1)
                CompactIntInputV4(
                    value = draft.abilityValue(ability),
                    onValueChange = { onDraftChange(draft.withAbilityValue(ability, it)) },
                    modifier = Modifier.width(52.dp),
                )
                Text(
                    "Mod ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Salv.", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                DerivedTotalControlV4(
                    total = draft.savingThrowTotal(ability)?.let(::formatSignedV4).orEmpty(),
                    adjustment = save.adjustment,
                    dialogTitle = "Salvación $abbreviation",
                    breakdownLines = listOf(
                        "$abbreviation ${abilityModifier?.let(::formatSignedV4) ?: "—"}",
                        if (save.proficient) {
                            "Competencia ${proficiency?.let(::formatSignedV4) ?: "—"}"
                        } else {
                            "Sin competencia +0"
                        },
                    ),
                    onAdjustmentChange = { onDraftChange(draft.withSave(save.copy(adjustment = it))) },
                    modifier = Modifier.weight(1f),
                )
                CharacterD20RollButtonV4(
                    label = "Salvación $abbreviation",
                    modifier = draft.savingThrowTotal(ability),
                )
                SaveProficiencyToggleV4(
                    proficient = save.proficient,
                    onToggle = {
                        onDraftChange(draft.withSave(save.copy(proficient = !save.proficient)))
                    },
                )
            }
            val relatedSkills = draft.skills.filter { it.key.ability == ability }
            if (relatedSkills.isEmpty()) {
                Text("Sin habilidades asociadas", style = MaterialTheme.typography.labelSmall)
            } else {
                relatedSkills.forEach { skill ->
                    SkillRowV4(skill, draft, onDraftChange)
                }
            }
        }
    }
}

@Composable
private fun SectionCardV4(
    title: String,
    content: @Composable () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            content()
        }
    }
}

@Composable
private fun CompactIntFieldV4(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    signed: Boolean = false,
    allowBlank: Boolean = true,
) {
    Column(modifier = modifier) {
        CompactFieldLabelV4(label)
        CompactIntInputV4(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            signed = signed,
            allowBlank = allowBlank,
        )
    }
}

@Composable
private fun CompactIntInputV4(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    signed: Boolean = false,
    allowBlank: Boolean = true,
    placeholder: String = "",
) {
    Surface(
        modifier = modifier.heightIn(min = 38.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (value.isBlank() && placeholder.isNotBlank()) {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = { raw ->
                    val cleaned = sanitizeIntInputV4(raw, signed)
                    if (allowBlank || cleaned.isNotBlank()) onValueChange(cleaned)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

@Composable
private fun CompactTextFieldV4(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.heightIn(min = 38.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 6.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            )
        }
    }
}

private fun sanitizeIntInputV4(raw: String, signed: Boolean): String {
    if (!signed) return raw.filter(Char::isDigit)
    if (raw.isBlank()) return ""
    val sign = raw.firstOrNull()?.takeIf { it == '+' || it == '-' }?.toString().orEmpty()
    val digits = raw.drop(if (sign.isEmpty()) 0 else 1).filter(Char::isDigit)
    return sign + digits
}

private fun isMissingNumericTokenV4(value: String): Boolean {
    val trimmed = value.trim()
    return trimmed.isEmpty() || trimmed == "+" || trimmed == "-"
}

private fun parseOptionalAdjustmentV4(value: String): Int? {
    if (isMissingNumericTokenV4(value)) return 0
    return value.trim().toIntOrNull()
}

internal data class ClassLevelDraftV4(
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

private data class SaveDraftV4(
    val ability: CharacterAbility,
    val proficient: Boolean,
    val adjustment: String,
)

private data class SkillDraftV4(
    val key: SkillKey,
    val adjustment: String,
    val training: SkillTraining,
)

private data class SpellSlotDraftV4(
    val level: Int,
    val total: String,
    val spent: Int,
)

private data class CharacterEditorDraftV4(
    val name: String,
    val status: CharacterStatus,
    val strength: String,
    val dexterity: String,
    val constitution: String,
    val intelligence: String,
    val wisdom: String,
    val charisma: String,
    val armorClass: String,
    val maxHp: String,
    val currentHp: String,
    val tempHp: String,
    val initiativeAdjustment: String,
    val speed: String,
    val proficiencyBonusAdjustment: String,
    val passivePerceptionAdjustment: String,
    val spellSaveDc: String,
    val spellAttackModifier: String,
    val spellcastingAbility: SpellcastingAbility,
    val spellSlots: List<SpellSlotDraftV4>,
    val classes: List<ClassLevelDraftV4>,
    val saves: List<SaveDraftV4>,
    val skills: List<SkillDraftV4>,
) {
    fun abilityValue(ability: CharacterAbility): String = when (ability) {
        CharacterAbility.STRENGTH -> strength
        CharacterAbility.DEXTERITY -> dexterity
        CharacterAbility.CONSTITUTION -> constitution
        CharacterAbility.INTELLIGENCE -> intelligence
        CharacterAbility.WISDOM -> wisdom
        CharacterAbility.CHARISMA -> charisma
    }

    fun withAbilityValue(ability: CharacterAbility, value: String): CharacterEditorDraftV4 = when (ability) {
        CharacterAbility.STRENGTH -> copy(strength = value)
        CharacterAbility.DEXTERITY -> copy(dexterity = value)
        CharacterAbility.CONSTITUTION -> copy(constitution = value)
        CharacterAbility.INTELLIGENCE -> copy(intelligence = value)
        CharacterAbility.WISDOM -> copy(wisdom = value)
        CharacterAbility.CHARISMA -> copy(charisma = value)
    }

    fun abilityModifier(ability: CharacterAbility): Int? = abilityValue(ability).toIntOrNull()?.let(::abilityModifierForScore)

    fun totalLevel(): Int = classes.sumOf { it.level.toIntOrNull() ?: 0 }

    fun standardProficiencyBonus(): Int = standardProficiencyBonusForLevel(totalLevel())

    fun finalProficiencyBonus(): Int? {
        val adjustment = parseOptionalAdjustmentV4(proficiencyBonusAdjustment) ?: return null
        return standardProficiencyBonus() + adjustment
    }

    fun saveFor(ability: CharacterAbility): SaveDraftV4 =
        saves.firstOrNull { it.ability == ability } ?: SaveDraftV4(ability, false, "0")

    fun withSave(changed: SaveDraftV4): CharacterEditorDraftV4 = copy(
        saves = CharacterAbility.entries.map { ability ->
            if (ability == changed.ability) changed else saveFor(ability)
        },
    )

    fun withSkill(changed: SkillDraftV4): CharacterEditorDraftV4 = copy(
        skills = SkillKey.entries.map { key ->
            if (key == changed.key) changed else skills.firstOrNull { it.key == key }
                ?: SkillDraftV4(key, "0", SkillTraining.NONE)
        },
    )

    fun spellSlotFor(level: Int): SpellSlotDraftV4 =
        spellSlots.firstOrNull { it.level == level } ?: SpellSlotDraftV4(level, "0", 0)

    fun withSpellSlot(changed: SpellSlotDraftV4): CharacterEditorDraftV4 = copy(
        spellSlots = (1..9).map { level ->
            if (level == changed.level) changed else spellSlotFor(level)
        },
    )

    fun savingThrowTotal(ability: CharacterAbility): Int? {
        val modifier = abilityModifier(ability) ?: return null
        val proficiency = finalProficiencyBonus() ?: return null
        val save = saveFor(ability)
        val adjustment = parseOptionalAdjustmentV4(save.adjustment) ?: return null
        return modifier + (if (save.proficient) proficiency else 0) + adjustment
    }

    fun skillTotal(key: SkillKey): Int? {
        val modifier = abilityModifier(key.ability) ?: return null
        val proficiency = finalProficiencyBonus() ?: return null
        val skill = skills.firstOrNull { it.key == key } ?: return null
        val adjustment = parseOptionalAdjustmentV4(skill.adjustment) ?: return null
        val contribution = when (skill.training) {
            SkillTraining.NONE -> 0
            SkillTraining.PROFICIENT -> proficiency
            SkillTraining.EXPERTISE -> proficiency * 2
        }
        return modifier + contribution + adjustment
    }

    fun initiativeTotal(): Int? {
        val dexterityModifier = abilityModifier(CharacterAbility.DEXTERITY) ?: return null
        val adjustment = parseOptionalAdjustmentV4(initiativeAdjustment) ?: return null
        return dexterityModifier + adjustment
    }

    fun passivePerceptionTotal(): Int? {
        val perception = skillTotal(SkillKey.PERCEPTION) ?: return null
        val adjustment = parseOptionalAdjustmentV4(passivePerceptionAdjustment) ?: return null
        return 10 + perception + adjustment
    }

    fun missingRequiredNumberLabels(): List<String> = buildList {
        listOf(
            "FUE" to strength,
            "DES" to dexterity,
            "CON" to constitution,
            "INT" to intelligence,
            "SAB" to wisdom,
            "CAR" to charisma,
            "CA" to armorClass,
            "PG máximos" to maxHp,
            "PG actuales" to currentHp,
            "PG temporales" to tempHp,
            "Velocidad" to speed,
        ).forEach { (label, value) ->
            if (isMissingNumericTokenV4(value)) add(label)
        }
        classes.forEachIndexed { index, classDraft ->
            if (isMissingNumericTokenV4(classDraft.level)) add("Nv. clase ${index + 1}")
            if (isMissingNumericTokenV4(classDraft.hitDiceRemaining)) add("DG clase ${index + 1}")
            if (isMissingNumericTokenV4(classDraft.hitDieSides)) add("Tipo DG clase ${index + 1}")
        }
    }

    fun toSheetOrNull(
        original: CharacterSheet,
        blankRequiredAsZero: Boolean = false,
    ): CharacterSheet? {
        fun parsedRequired(value: String): Int? {
            if (isMissingNumericTokenV4(value)) return if (blankRequiredAsZero) 0 else null
            return value.trim().toIntOrNull()
        }
        fun parsedAdjustment(value: String): Int? = parseOptionalAdjustmentV4(value)
        fun parsedOptionalInt(value: String): Int? {
            if (isMissingNumericTokenV4(value)) return null
            return value.trim().toIntOrNull()
        }

        val normalizedName = name.trim().takeIf { it.isNotEmpty() } ?: return null
        val parsedClasses = classes.mapIndexed { index, classDraft ->
            CharacterClassLevel(
                id = classDraft.id,
                name = classDraft.name.trim().takeIf { it.isNotEmpty() } ?: return null,
                level = parsedRequired(classDraft.level)?.takeIf { it >= 0 } ?: return null,
                hitDieSides = parsedRequired(classDraft.hitDieSides)?.takeIf { it >= 0 } ?: return null,
                hitDiceRemaining = parsedRequired(classDraft.hitDiceRemaining)?.takeIf { it >= 0 } ?: return null,
                sortOrder = index,
                rulesFamily = classDraft.rulesFamily,
                source = classDraft.source,
                catalogKey = classDraft.catalogKey,
                subclassName = classDraft.subclassName?.trim()?.takeIf { it.isNotEmpty() },
                subclassSource = classDraft.subclassSource,
                subclassCatalogKey = classDraft.subclassCatalogKey,
                subclassRulesFamily = classDraft.subclassRulesFamily,
            )
        }
        val parsedSaves = CharacterAbility.entries.map { ability ->
            val save = saveFor(ability)
            CharacterSavingThrow(
                ability = ability,
                proficient = save.proficient,
                adjustment = parsedAdjustment(save.adjustment) ?: return null,
            )
        }
        val parsedSkills = SkillKey.entries.map { key ->
            val skill = skills.firstOrNull { it.key == key } ?: return null
            CharacterSkill(
                key = key,
                adjustment = parsedAdjustment(skill.adjustment) ?: return null,
                training = skill.training,
            )
        }
        val parsedSpellSlots = (1..9).mapNotNull { level ->
            val slot = spellSlotFor(level)
            val total = if (isMissingNumericTokenV4(slot.total)) 0 else slot.total.toIntOrNull() ?: return null
            if (total <= 0) {
                null
            } else {
                CharacterSpellSlot(
                    level = level,
                    totalSlots = total,
                    spentSlots = slot.spent.coerceIn(0, total),
                )
            }
        }
        val spellDc = parsedOptionalInt(spellSaveDc)
        if (spellSaveDc.isNotBlank() && spellDc == null) return null
        val spellAttack = parsedOptionalInt(spellAttackModifier)
        if (spellAttackModifier.isNotBlank() && spellAttack == null) return null
        val proficiencyAdjustment = parsedAdjustment(proficiencyBonusAdjustment) ?: return null
        val finalProficiency = standardProficiencyBonusForLevel(parsedClasses.sumOf { it.level }) + proficiencyAdjustment

        return original.copy(
            name = normalizedName,
            status = status,
            strength = parsedRequired(strength) ?: return null,
            dexterity = parsedRequired(dexterity) ?: return null,
            constitution = parsedRequired(constitution) ?: return null,
            intelligence = parsedRequired(intelligence) ?: return null,
            wisdom = parsedRequired(wisdom) ?: return null,
            charisma = parsedRequired(charisma) ?: return null,
            armorClass = parsedRequired(armorClass) ?: return null,
            maxHp = parsedRequired(maxHp) ?: return null,
            currentHp = parsedRequired(currentHp) ?: return null,
            tempHp = parsedRequired(tempHp) ?: return null,
            initiativeAdjustment = parsedAdjustment(initiativeAdjustment) ?: return null,
            speed = parsedRequired(speed) ?: return null,
            proficiencyBonus = finalProficiency,
            proficiencyBonusAdjustment = proficiencyAdjustment,
            savingThrows = parsedSaves,
            passivePerceptionAdjustment = parsedAdjustment(passivePerceptionAdjustment) ?: return null,
            spellSaveDc = spellDc,
            spellAttackModifier = spellAttack,
            spellcastingAbility = spellcastingAbility,
            spellSlots = parsedSpellSlots,
            classes = parsedClasses,
            skills = parsedSkills,
        )
    }

    fun toJson(): String = JSONObject().apply {
        put("name", name)
        put("status", status.name)
        put("strength", strength)
        put("dexterity", dexterity)
        put("constitution", constitution)
        put("intelligence", intelligence)
        put("wisdom", wisdom)
        put("charisma", charisma)
        put("armorClass", armorClass)
        put("maxHp", maxHp)
        put("currentHp", currentHp)
        put("tempHp", tempHp)
        put("initiativeAdjustment", initiativeAdjustment)
        put("speed", speed)
        put("proficiencyBonusAdjustment", proficiencyBonusAdjustment)
        put("passivePerceptionAdjustment", passivePerceptionAdjustment)
        put("spellSaveDc", spellSaveDc)
        put("spellAttackModifier", spellAttackModifier)
        put("spellcastingAbility", spellcastingAbility.name)
        put("spellSlots", JSONArray().apply {
            spellSlots.forEach { item ->
                put(JSONObject().apply {
                    put("level", item.level)
                    put("total", item.total)
                    put("spent", item.spent)
                })
            }
        })
        put("classes", JSONArray().apply {
            classes.forEach { item ->
                put(JSONObject().apply {
                    put("id", item.id.toString())
                    put("name", item.name)
                    put("level", item.level)
                    put("die", item.hitDieSides)
                    put("remaining", item.hitDiceRemaining)
                    put("rulesFamily", item.rulesFamily.name)
                    put("source", item.source ?: JSONObject.NULL)
                    put("catalogKey", item.catalogKey ?: JSONObject.NULL)
                    put("subclassName", item.subclassName ?: JSONObject.NULL)
                    put("subclassSource", item.subclassSource ?: JSONObject.NULL)
                    put("subclassCatalogKey", item.subclassCatalogKey ?: JSONObject.NULL)
                    put("subclassRulesFamily", item.subclassRulesFamily.name)
                })
            }
        })
        put("saves", JSONArray().apply {
            saves.forEach { item ->
                put(JSONObject().apply {
                    put("ability", item.ability.name)
                    put("proficient", item.proficient)
                    put("adjustment", item.adjustment)
                })
            }
        })
        put("skills", JSONArray().apply {
            skills.forEach { item ->
                put(JSONObject().apply {
                    put("key", item.key.name)
                    put("training", item.training.name)
                    put("adjustment", item.adjustment)
                })
            }
        })
    }.toString()

    companion object {
        val Saver: Saver<CharacterEditorDraftV4, String> = Saver(
            save = { it.toJson() },
            restore = { fromJson(it) },
        )

        fun from(sheet: CharacterSheet) = CharacterEditorDraftV4(
            name = sheet.name,
            status = sheet.status,
            strength = sheet.strength.toString(),
            dexterity = sheet.dexterity.toString(),
            constitution = sheet.constitution.toString(),
            intelligence = sheet.intelligence.toString(),
            wisdom = sheet.wisdom.toString(),
            charisma = sheet.charisma.toString(),
            armorClass = sheet.armorClass.toString(),
            maxHp = sheet.maxHp.toString(),
            currentHp = sheet.currentHp.toString(),
            tempHp = sheet.tempHp.toString(),
            initiativeAdjustment = sheet.initiativeAdjustment.toString(),
            speed = sheet.speed.toString(),
            proficiencyBonusAdjustment = sheet.proficiencyBonusAdjustment.toString(),
            passivePerceptionAdjustment = sheet.passivePerceptionAdjustment.toString(),
            spellSaveDc = sheet.spellSaveDc?.toString().orEmpty(),
            spellAttackModifier = sheet.spellAttackModifier?.toString().orEmpty(),
            spellcastingAbility = sheet.spellcastingAbility,
            spellSlots = (1..9).map { level ->
                val stored = sheet.spellSlots.firstOrNull { it.level == level }
                SpellSlotDraftV4(
                    level = level,
                    total = stored?.totalSlots?.toString() ?: "0",
                    spent = stored?.spentSlots ?: 0,
                )
            },
            classes = sheet.classes.map { classLevel ->
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
            saves = CharacterAbility.entries.map { ability ->
                val save = sheet.savingThrow(ability)
                SaveDraftV4(ability, save.proficient, save.adjustment.toString())
            },
            skills = SkillKey.entries.map { key ->
                val skill = sheet.skill(key)
                SkillDraftV4(key, skill.adjustment.toString(), skill.training)
            },
        )

        private fun fromJson(raw: String): CharacterEditorDraftV4? = runCatching {
            val json = JSONObject(raw)
            val classesJson = json.getJSONArray("classes")
            val classes = buildList {
                for (index in 0 until classesJson.length()) {
                    val item = classesJson.getJSONObject(index)
                    fun optionalString(key: String): String? =
                        if (item.has(key) && !item.isNull(key)) item.getString(key) else null
                    val rulesFamily = runCatching {
                        CharacterRulesFamily.valueOf(
                            item.optString("rulesFamily", CharacterRulesFamily.UNSPECIFIED.name),
                        )
                    }.getOrDefault(CharacterRulesFamily.UNSPECIFIED)
                    val subclassRulesFamily = runCatching {
                        CharacterRulesFamily.valueOf(
                            item.optString("subclassRulesFamily", rulesFamily.name),
                        )
                    }.getOrDefault(rulesFamily)
                    add(
                        ClassLevelDraftV4(
                            id = Uuid.parse(item.getString("id")),
                            name = item.getString("name"),
                            level = item.getString("level"),
                            hitDieSides = item.getString("die"),
                            hitDiceRemaining = item.getString("remaining"),
                            rulesFamily = rulesFamily,
                            source = optionalString("source"),
                            catalogKey = optionalString("catalogKey"),
                            subclassName = optionalString("subclassName"),
                            subclassSource = optionalString("subclassSource"),
                            subclassCatalogKey = optionalString("subclassCatalogKey"),
                            subclassRulesFamily = subclassRulesFamily,
                        ),
                    )
                }
            }
            val savesJson = json.getJSONArray("saves")
            val saves = buildList {
                for (index in 0 until savesJson.length()) {
                    val item = savesJson.getJSONObject(index)
                    add(
                        SaveDraftV4(
                            ability = CharacterAbility.valueOf(item.getString("ability")),
                            proficient = item.getBoolean("proficient"),
                            adjustment = item.getString("adjustment"),
                        ),
                    )
                }
            }
            val skillsJson = json.getJSONArray("skills")
            val skills = buildList {
                for (index in 0 until skillsJson.length()) {
                    val item = skillsJson.getJSONObject(index)
                    add(
                        SkillDraftV4(
                            key = SkillKey.valueOf(item.getString("key")),
                            adjustment = item.getString("adjustment"),
                            training = SkillTraining.valueOf(item.getString("training")),
                        ),
                    )
                }
            }
            val proficiencyAdjustment = if (json.has("proficiencyBonusAdjustment")) {
                json.getString("proficiencyBonusAdjustment")
            } else {
                val legacyFinal = json.optString("proficiencyBonus", "2").toIntOrNull() ?: 2
                val totalLevel = classes.sumOf { it.level.toIntOrNull() ?: 0 }
                (legacyFinal - standardProficiencyBonusForLevel(totalLevel)).toString()
            }
            val slots = if (json.has("spellSlots")) {
                val array = json.getJSONArray("spellSlots")
                val restored = mutableMapOf<Int, SpellSlotDraftV4>()
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    val level = item.getInt("level")
                    restored[level] = SpellSlotDraftV4(
                        level = level,
                        total = item.getString("total"),
                        spent = item.getInt("spent"),
                    )
                }
                (1..9).map { level -> restored[level] ?: SpellSlotDraftV4(level, "0", 0) }
            } else {
                (1..9).map { level -> SpellSlotDraftV4(level, "0", 0) }
            }
            CharacterEditorDraftV4(
                name = json.getString("name"),
                status = CharacterStatus.valueOf(json.getString("status")),
                strength = json.getString("strength"),
                dexterity = json.getString("dexterity"),
                constitution = json.getString("constitution"),
                intelligence = json.getString("intelligence"),
                wisdom = json.getString("wisdom"),
                charisma = json.getString("charisma"),
                armorClass = json.getString("armorClass"),
                maxHp = json.getString("maxHp"),
                currentHp = json.getString("currentHp"),
                tempHp = json.getString("tempHp"),
                initiativeAdjustment = json.getString("initiativeAdjustment"),
                speed = json.getString("speed"),
                proficiencyBonusAdjustment = proficiencyAdjustment,
                passivePerceptionAdjustment = json.getString("passivePerceptionAdjustment"),
                spellSaveDc = json.getString("spellSaveDc"),
                spellAttackModifier = json.optString("spellAttackModifier", ""),
                spellcastingAbility = runCatching {
                    SpellcastingAbility.valueOf(json.optString("spellcastingAbility", SpellcastingAbility.NONE.name))
                }.getOrDefault(SpellcastingAbility.NONE),
                spellSlots = slots,
                classes = classes,
                saves = saves,
                skills = skills,
            )
        }.getOrNull()
    }
}

private fun abilityAbbreviationV4(ability: CharacterAbility): String = when (ability) {
    CharacterAbility.STRENGTH -> "FUE"
    CharacterAbility.DEXTERITY -> "DES"
    CharacterAbility.CONSTITUTION -> "CON"
    CharacterAbility.INTELLIGENCE -> "INT"
    CharacterAbility.WISDOM -> "SAB"
    CharacterAbility.CHARISMA -> "CAR"
}

private fun spellcastingAbilityLabelV4(ability: SpellcastingAbility): String = when (ability) {
    SpellcastingAbility.STRENGTH -> "FUE"
    SpellcastingAbility.DEXTERITY -> "DES"
    SpellcastingAbility.CONSTITUTION -> "CON"
    SpellcastingAbility.INTELLIGENCE -> "INT"
    SpellcastingAbility.WISDOM -> "SAB"
    SpellcastingAbility.CHARISMA -> "CAR"
    SpellcastingAbility.OTHER -> "Otro"
    SpellcastingAbility.NONE -> "Ninguna"
}

private fun skillLabelV4(key: SkillKey): String = when (key) {
    SkillKey.ACROBATICS -> "Acrobacias"
    SkillKey.ANIMAL_HANDLING -> "Trato con Animales"
    SkillKey.ARCANA -> "Arcanos"
    SkillKey.ATHLETICS -> "Atletismo"
    SkillKey.DECEPTION -> "Engaño"
    SkillKey.HISTORY -> "Historia"
    SkillKey.INSIGHT -> "Perspicacia"
    SkillKey.INTIMIDATION -> "Intimidación"
    SkillKey.INVESTIGATION -> "Investigación"
    SkillKey.MEDICINE -> "Medicina"
    SkillKey.NATURE -> "Naturaleza"
    SkillKey.PERCEPTION -> "Percepción"
    SkillKey.PERFORMANCE -> "Interpretación"
    SkillKey.PERSUASION -> "Persuasión"
    SkillKey.RELIGION -> "Religión"
    SkillKey.SLEIGHT_OF_HAND -> "Juego de Manos"
    SkillKey.STEALTH -> "Sigilo"
    SkillKey.SURVIVAL -> "Supervivencia"
}

private fun trainingLabelV4(training: SkillTraining): String = when (training) {
    SkillTraining.NONE -> "Sin competencia"
    SkillTraining.PROFICIENT -> "Competente"
    SkillTraining.EXPERTISE -> "Pericia"
}

private fun statusLabelV4(status: CharacterStatus): String = when (status) {
    CharacterStatus.ACTIVE -> "Activo"
    CharacterStatus.INACTIVE -> "Inactivo"
    CharacterStatus.RETIRED -> "Retirado"
    CharacterStatus.DEAD -> "Muerto"
}

private fun formatSpeedV4(raw: String): String {
    val feet = raw.trim().toIntOrNull() ?: return raw.ifBlank { "—" }
    val metricTenths = feet * 3
    val wholeMeters = metricTenths / 10
    val remainder = kotlin.math.abs(metricTenths % 10)
    val metric = if (remainder == 0) {
        wholeMeters.toString()
    } else {
        "$wholeMeters,$remainder"
    }
    return "$feet ft ($metric m)"
}

private fun formatSignedV4(value: Int): String = if (value >= 0) "+$value" else value.toString()

private fun formatSavedAtV4(epochSeconds: Long): String = runCatching {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).format(formatter)
}.getOrElse { "—" }
