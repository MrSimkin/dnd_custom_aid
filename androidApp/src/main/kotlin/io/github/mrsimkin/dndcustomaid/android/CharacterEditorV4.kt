package io.github.mrsimkin.dndcustomaid.android

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatDamageProfile
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterModuleKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSavingThrow
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellSlot
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingProfile
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomAttribute
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCustomSkill
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSkillPresentation
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.character.characterProperNameInput
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.SkillTraining
import io.github.mrsimkin.dndcustomaid.shared.character.SpellcastingAbility
import io.github.mrsimkin.dndcustomaid.shared.character.abilityModifierForScore
import io.github.mrsimkin.dndcustomaid.shared.character.characterAbilityReferenceAbbreviation
import io.github.mrsimkin.dndcustomaid.shared.character.customSavingThrowTotal
import io.github.mrsimkin.dndcustomaid.shared.character.customSkillTotal
import io.github.mrsimkin.dndcustomaid.shared.character.generalSpellcastingRows
import io.github.mrsimkin.dndcustomaid.shared.character.presentCharacterSkills
import io.github.mrsimkin.dndcustomaid.shared.character.isCharacterStructuralEditingEnabled
import io.github.mrsimkin.dndcustomaid.shared.character.mergeCharacterOperationalClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.mergeCharacterOperationalState
import io.github.mrsimkin.dndcustomaid.shared.character.setCharacterHitPoints
import io.github.mrsimkin.dndcustomaid.shared.character.standardProficiencyBonusForLevel
import io.github.mrsimkin.dndcustomaid.shared.character.suggestedCharacterModules
import io.github.mrsimkin.dndcustomaid.shared.character.visibleCharacterModules
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.uuid.Uuid
import org.json.JSONArray
import org.json.JSONObject

@Composable
internal fun CharacterEditorScreenV4(
    characterId: Uuid,
    repository: CharacterRepository,
    backupRepository: CharacterBackupRepository,
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
    val pcSettingsContext = LocalCharacterPcSettingsContextV4.current
    val successorState = pcSettingsContext?.successorState ?: CharacterSuccessorState()
    var draft by rememberSaveable(
        characterId.toString(),
        stateSaver = CharacterEditorDraftV4.Saver,
    ) {
        mutableStateOf(CharacterEditorDraftV4.from(stored))
    }
    var combatDraftJson by rememberSaveable(characterId.toString()) {
        mutableStateOf(combatEntriesToJsonV4(stored.combatEntries))
    }
    var combatDamageDraftJson by rememberSaveable(characterId.toString(), "combat-damage") {
        mutableStateOf(characterCombatDamageProfilesToJsonV4(successorState.combatDamage))
    }
    var spellcastingProfilesDraftJson by rememberSaveable(characterId.toString(), "spellcasting-profiles") {
        mutableStateOf(characterSpellcastingProfilesToJsonV4(successorState.spellcastingProfiles))
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
    var traitProvenanceDraftJson by rememberSaveable(characterId.toString(), "trait-provenance-p7") {
        mutableStateOf(characterTraitProvenanceToJsonP7V4(successorState.traitProvenance))
    }
    var canonicalOriginsDraftJson by rememberSaveable(characterId.toString(), "canonical-origins-p7") {
        mutableStateOf(
            characterCanonicalOriginsDraftToJsonP7V4(
                CharacterCanonicalOriginsDraftP7V4.from(successorState),
            ),
        )
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
    var proficiencyDraftJson by rememberSaveable(characterId.toString(), "proficiencies") {
        mutableStateOf(characterProficienciesToJsonV4(stored.proficiencies))
    }
    var savedMessage by rememberSaveable(characterId.toString()) { mutableStateOf<String?>(null) }
    var selectedTabName by rememberSaveable(characterId.toString(), "selected-tab") {
        mutableStateOf(
            navigationPreferenceStore.loadLastTabName(characterId)
                ?: CharacterTabV4.OVERVIEW.name,
        )
    }
    var confirmBlankNumbers by rememberSaveable(characterId.toString()) { mutableStateOf(false) }
    var confirmTableModeTransition by rememberSaveable(characterId.toString(), "table-mode-transition") { mutableStateOf(false) }
    var activateTableModeAfterBlankSave by rememberSaveable(characterId.toString(), "table-mode-after-blank-save") { mutableStateOf(false) }
    var showPcSettings by rememberSaveable(characterId.toString(), "pc-settings") { mutableStateOf(false) }
    var showSupercompact by rememberSaveable(characterId.toString(), "supercompact") { mutableStateOf(false) }
    var confirmDisableSpellcasting by rememberSaveable(characterId.toString(), "disable-spellcasting") { mutableStateOf(false) }
    var confirmUnsavedLeave by rememberSaveable(characterId.toString(), "unsaved-leave") { mutableStateOf(false) }
    var leaveAfterSave by rememberSaveable(characterId.toString(), "leave-after-save") { mutableStateOf(false) }
    var backupExportMessage by rememberSaveable(characterId.toString(), "backup-export-message") { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val backupExportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        if (uri != null) {
            val result = runCatching {
                val document = backupRepository.exportCharacter(
                    characterId = characterId,
                    exportedAtEpochSeconds = System.currentTimeMillis() / 1000L,
                )
                val encoded = CharacterBackupCodec.encode(document)
                val output = requireNotNull(context.contentResolver.openOutputStream(uri, "wt"))
                output.bufferedWriter(Charsets.UTF_8).use { writer -> writer.write(encoded) }
            }
            backupExportMessage = if (result.isSuccess) {
                "Respaldo exportado correctamente."
            } else {
                "No se pudo escribir el respaldo en el archivo seleccionado."
            }
        }
    }

    val combatEntries = remember(combatDraftJson) { combatEntriesFromJsonV4(combatDraftJson) }
    val combatDamageProfiles = remember(combatDamageDraftJson) {
        characterCombatDamageProfilesFromJsonV4(combatDamageDraftJson)
    }
    val spellcastingProfiles = remember(spellcastingProfilesDraftJson) {
        characterSpellcastingProfilesFromJsonV4(spellcastingProfilesDraftJson)
    }
    val canonicalOriginsDraft = remember(canonicalOriginsDraftJson) {
        characterCanonicalOriginsDraftFromJsonP7V4(canonicalOriginsDraftJson)
    }
    val provenanceDraftSuccessorState = remember(successorState, canonicalOriginsDraft) {
        canonicalOriginsDraft.projectOnto(successorState)
    }
    val projectedSuccessorState = remember(provenanceDraftSuccessorState, spellcastingProfiles) {
        provenanceDraftSuccessorState.copy(spellcastingProfiles = spellcastingProfiles)
    }
    val equipmentDraft = remember(equipmentDraftJson) { equipmentDraftFromJsonV4(equipmentDraftJson) }
    val backgroundDraft = remember(backgroundDraftJson) { characterBackgroundFromJsonV4(backgroundDraftJson) }
    val traitsDraft = remember(traitsDraftJson) { characterTraitsFromJsonV4(traitsDraftJson) }
    val traitProvenanceDraft = remember(traitProvenanceDraftJson) {
        characterTraitProvenanceFromJsonP7V4(traitProvenanceDraftJson)
    }
    val spellcastingDraft = remember(spellcastingDraftJson) { characterSpellcastingDraftFromJsonV4(spellcastingDraftJson) }
    val notesDraft = remember(notesDraftJson) { characterNotesDraftFromJsonV4(notesDraftJson) }
    val h1ModuleDraft = remember(h1ModuleDraftJson) { characterH1ModuleDraftFromJsonV4(h1ModuleDraftJson) }
    val proficiencyDraft = remember(proficiencyDraftJson) { characterProficienciesFromJsonV4(proficiencyDraftJson) }
    val settingsSheet = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: stored
    val overviewProjectionSheet = settingsSheet.copy(
        background = backgroundDraft,
        inventoryItems = equipmentDraft.items,
        proficiencies = proficiencyDraft,
        spellcastingSources = spellcastingDraft.sources,
        spells = spellcastingDraft.spells,
    )
    val suggestedModules = suggestedCharacterModules(settingsSheet.classes)
    val visibleModules = visibleCharacterModules(settingsSheet.classes, closureState.moduleOverrides)
    val structuralEditingEnabled = isCharacterStructuralEditingEnabled(closureState.tableModeEnabled)
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
    val storedCombatDamageDraftJson = remember(successorState.combatDamage) {
        characterCombatDamageProfilesToJsonV4(successorState.combatDamage)
    }
    val storedSpellcastingProfilesDraftJson = remember(successorState.spellcastingProfiles) {
        characterSpellcastingProfilesToJsonV4(successorState.spellcastingProfiles)
    }
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
    val storedTraitProvenanceDraftJson = remember(successorState.traitProvenance) {
        characterTraitProvenanceToJsonP7V4(successorState.traitProvenance)
    }
    val storedCanonicalOriginsDraftJson = remember(
        successorState.speciesIdentity,
        successorState.subraceIdentity,
        successorState.backgroundIdentity,
    ) {
        characterCanonicalOriginsDraftToJsonP7V4(
            CharacterCanonicalOriginsDraftP7V4.from(successorState),
        )
    }
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
    val storedProficiencyDraftJson = remember(stored) {
        characterProficienciesToJsonV4(stored.proficiencies)
    }
    val hasUnsavedChanges =
        draft.toJson() != storedDraftJson ||
            combatDraftJson != storedCombatDraftJson ||
            combatDamageDraftJson != storedCombatDamageDraftJson ||
            spellcastingProfilesDraftJson != storedSpellcastingProfilesDraftJson ||
            equipmentDraftJson != storedEquipmentDraftJson ||
            backgroundDraftJson != storedBackgroundDraftJson ||
            traitsDraftJson != storedTraitsDraftJson ||
            traitProvenanceDraftJson != storedTraitProvenanceDraftJson ||
            canonicalOriginsDraftJson != storedCanonicalOriginsDraftJson ||
            spellcastingDraftJson != storedSpellcastingDraftJson ||
            notesDraftJson != storedNotesDraftJson ||
            h1ModuleDraftJson != storedH1ModuleDraftJson ||
            proficiencyDraftJson != storedProficiencyDraftJson
    val tableModePendingChanges = buildList {
        val persistedDraft = CharacterEditorDraftV4.from(stored)
        fun addScalar(label: String, before: String, after: String) {
            if (before != after) add("$label: ${before.ifBlank { "—" }} → ${after.ifBlank { "—" }}")
        }
        addScalar("Nombre", persistedDraft.name, draft.name)
        addScalar("Estado", persistedDraft.status.name, draft.status.name)
        addScalar("FUE", persistedDraft.strength, draft.strength)
        addScalar("DES", persistedDraft.dexterity, draft.dexterity)
        addScalar("CON", persistedDraft.constitution, draft.constitution)
        addScalar("INT", persistedDraft.intelligence, draft.intelligence)
        addScalar("SAB", persistedDraft.wisdom, draft.wisdom)
        addScalar("CAR", persistedDraft.charisma, draft.charisma)
        addScalar("CA", persistedDraft.armorClass, draft.armorClass)
        addScalar("PG máximos", persistedDraft.maxHp, draft.maxHp)
        addScalar("PG actuales", persistedDraft.currentHp, draft.currentHp)
        addScalar("PG temporales", persistedDraft.tempHp, draft.tempHp)
        addScalar("Ajuste iniciativa", persistedDraft.initiativeAdjustment, draft.initiativeAdjustment)
        addScalar("Velocidad", persistedDraft.speed, draft.speed)
        addScalar("Ajuste competencia", persistedDraft.proficiencyBonusAdjustment, draft.proficiencyBonusAdjustment)
        addScalar("Ajuste Percepción pasiva", persistedDraft.passivePerceptionAdjustment, draft.passivePerceptionAdjustment)
        addScalar("CD de conjuros", persistedDraft.spellSaveDc, draft.spellSaveDc)
        addScalar("Ataque de conjuros", persistedDraft.spellAttackModifier, draft.spellAttackModifier)
        addScalar("Característica de conjuros", persistedDraft.spellcastingAbility.name, draft.spellcastingAbility.name)
        if (persistedDraft.classes != draft.classes) add("Clases y niveles: cambios pendientes")
        if (persistedDraft.saves != draft.saves) add("Tiradas de salvación: cambios pendientes")
        if (persistedDraft.skills != draft.skills) add("Habilidades: cambios pendientes")
        if (persistedDraft.spellSlots.map { it.level to it.total } != draft.spellSlots.map { it.level to it.total }) add("Espacios de conjuro: configuración pendiente")
        if (combatDraftJson != storedCombatDraftJson) add("Combate: acciones / ataques pendientes")
        if (combatDamageDraftJson != storedCombatDamageDraftJson) add("Combate: daño estructurado pendiente")
        if (spellcastingProfilesDraftJson != storedSpellcastingProfilesDraftJson) add("Conjuros: perfiles de lanzamiento pendientes")
        if (equipmentDraftJson != storedEquipmentDraftJson) add("Equipo y monedas: cambios pendientes")
        if (backgroundDraftJson != storedBackgroundDraftJson) add("Trasfondo: cambios pendientes")
        if (traitsDraftJson != storedTraitsDraftJson) add("Rasgos: cambios pendientes")
        if (traitProvenanceDraftJson != storedTraitProvenanceDraftJson) add("Rasgos: procedencia pendiente")
        if (canonicalOriginsDraftJson != storedCanonicalOriginsDraftJson) add("Identidad de raza / trasfondo: cambios pendientes")
        if (spellcastingDraftJson != storedSpellcastingDraftJson) add("Conjuros: fuentes o conjuros pendientes")
        if (notesDraftJson != storedNotesDraftJson) add("Notas: cambios pendientes")
        if (h1ModuleDraftJson != storedH1ModuleDraftJson) add("Módulos de clase / formas / compañeros: cambios pendientes")
        if (proficiencyDraftJson != storedProficiencyDraftJson) add("Competencias: cambios pendientes")
    }.distinct()

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
    BackHandler(enabled = !showSupercompact && showPcSettings && !confirmTableModeTransition) {
        showPcSettings = false
        selectedTabName = resolvedCharacterTabV4(
            savedTabName = selectedTabName,
            spellcasterEnabled = stored.spellcasterEnabled,
            visibleModules = visibleModules,
        ).name
    }
    BackHandler(
        enabled = !showSupercompact && !showPcSettings && !confirmUnsavedLeave && !confirmBlankNumbers && !confirmDisableSpellcasting && !confirmTableModeTransition,
    ) {
        requestBack()
    }

    fun updateDraft(updated: CharacterEditorDraftV4) {
        draft = updated
        savedMessage = null
    }

    fun updateStructuralDraft(updated: CharacterEditorDraftV4) {
        if (!structuralEditingEnabled) return
        updateDraft(updated)
    }

    fun updateCombatEntries(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatEntry>) {
        if (!structuralEditingEnabled) return
        combatDraftJson = combatEntriesToJsonV4(updated)
        savedMessage = null
    }

    fun updateCombatDamageProfiles(updated: List<CharacterCombatDamageProfile>) {
        if (!structuralEditingEnabled) return
        combatDamageDraftJson = characterCombatDamageProfilesToJsonV4(updated)
        savedMessage = null
    }

    fun updateSpellcastingProfiles(updated: List<CharacterSpellcastingProfile>) {
        if (!structuralEditingEnabled) return
        spellcastingProfilesDraftJson = characterSpellcastingProfilesToJsonV4(updated)
        savedMessage = null
    }

    fun updateEquipmentItems(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterInventoryItem>) {
        if (!structuralEditingEnabled) return
        equipmentDraftJson = equipmentDraftToJsonV4(equipmentDraft.copy(items = updated))
        savedMessage = null
    }

    fun updateCurrencies(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterCurrency>) {
        if (!structuralEditingEnabled) return
        equipmentDraftJson = equipmentDraftToJsonV4(equipmentDraft.copy(currencies = updated))
        savedMessage = null
    }

    fun updateEquipmentDraft(updated: CharacterEquipmentDraftV4) {
        if (!structuralEditingEnabled) return
        equipmentDraftJson = equipmentDraftToJsonV4(updated)
        savedMessage = null
    }

    fun updateBackground(updated: io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackground) {
        if (!structuralEditingEnabled) return
        backgroundDraftJson = characterBackgroundToJsonV4(updated)
        savedMessage = null
    }

    fun updateCanonicalOrigins(updated: CharacterCanonicalOriginsDraftP7V4) {
        if (!structuralEditingEnabled) return
        canonicalOriginsDraftJson = characterCanonicalOriginsDraftToJsonP7V4(updated)
        savedMessage = null
    }

    fun updateTraits(updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTrait>) {
        if (!structuralEditingEnabled) return
        traitsDraftJson = characterTraitsToJsonV4(updated)
        savedMessage = null
    }

    fun updateTraitProvenance(
        updated: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterTraitProvenance>,
    ) {
        if (!structuralEditingEnabled) return
        traitProvenanceDraftJson = characterTraitProvenanceToJsonP7V4(updated)
        savedMessage = null
    }

    fun updateSpellcasting(updated: CharacterSpellcastingDraftV4) {
        if (!structuralEditingEnabled) return
        spellcastingDraftJson = characterSpellcastingDraftToJsonV4(updated)
        savedMessage = null
    }

    fun updateNotes(updated: CharacterNotesDraftV4) {
        if (!structuralEditingEnabled) return
        notesDraftJson = characterNotesDraftToJsonV4(updated)
        savedMessage = null
    }

    fun updateH1Modules(updated: CharacterH1ModuleDraftV4) {
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
        val shouldLeaveAfterPersist = leaveAfterSave
        val equipment = equipmentDraftFromJsonV4(equipmentDraftJson)
        val spellcasting = characterSpellcastingDraftFromJsonV4(spellcastingDraftJson)
        val notes = characterNotesDraftFromJsonV4(notesDraftJson)
        val h1Modules = characterH1ModuleDraftFromJsonV4(h1ModuleDraftJson)
        val proficiencies = characterProficienciesFromJsonV4(proficiencyDraftJson)
        val normalizedCandidate = setCharacterHitPoints(
            sheet = candidate,
            currentHp = candidate.currentHp,
            maxHp = candidate.maxHp,
        ).copy(tempHp = candidate.tempHp.coerceAtLeast(0))
        val integrated = normalizedCandidate.copy(
            combatEntries = combatEntriesFromJsonV4(combatDraftJson),
            inventoryItems = equipment.items,
            currencies = equipment.currencies,
            background = characterBackgroundFromJsonV4(backgroundDraftJson),
            traits = characterTraitsFromJsonV4(traitsDraftJson),
            spellcastingSources = spellcasting.sources,
            spells = spellcasting.spells,
            generalNotes = notes.generalNotes,
            noteCards = notes.cards,
            proficiencies = proficiencies,
            classOptions = h1Modules.classOptions,
            forms = h1Modules.forms,
            companions = h1Modules.companions,
        )
        stored = repository.saveCharacter(integrated)
        val liveCombatEntryIds = stored.combatEntries.mapTo(mutableSetOf()) { it.id }
        val savedDamageProfiles = characterCombatDamageProfilesFromJsonV4(combatDamageDraftJson)
            .filter { it.combatEntryId in liveCombatEntryIds }
        val liveSpellSourceIds = stored.spellcastingSources.mapTo(mutableSetOf()) { it.id }
        val savedSpellcastingProfiles = characterSpellcastingProfilesFromJsonV4(spellcastingProfilesDraftJson)
            .filter { it.sourceId in liveSpellSourceIds }
        val liveTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id }
        val savedCanonicalOrigins = characterCanonicalOriginsDraftFromJsonP7V4(canonicalOriginsDraftJson).normalized()
        val provenanceSaveState = savedCanonicalOrigins.projectOnto(successorState)
        val savedTraitProvenance = refreshResolvedTraitProvenanceLabelsP7V4(
            items = characterTraitProvenanceFromJsonP7V4(traitProvenanceDraftJson)
                .filter { it.traitId in liveTraitIds },
            classes = stored.classes,
            successorState = provenanceSaveState,
        )
        pcSettingsContext?.onSuccessorStateChange?.invoke(
            provenanceSaveState.copy(
                combatDamage = savedDamageProfiles,
                spellcastingProfiles = savedSpellcastingProfiles,
                traitProvenance = savedTraitProvenance,
            ),
        )
        combatDamageDraftJson = characterCombatDamageProfilesToJsonV4(savedDamageProfiles)
        spellcastingProfilesDraftJson = characterSpellcastingProfilesToJsonV4(savedSpellcastingProfiles)
        canonicalOriginsDraftJson = characterCanonicalOriginsDraftToJsonP7V4(savedCanonicalOrigins)
        traitProvenanceDraftJson = characterTraitProvenanceToJsonP7V4(savedTraitProvenance)
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
        combatDamageDraftJson = characterCombatDamageProfilesToJsonV4(savedDamageProfiles)
        equipmentDraftJson = equipmentDraftToJsonV4(
            CharacterEquipmentDraftV4(
                items = stored.inventoryItems,
                currencies = stored.currencies,
                inventoryUsage = closureState.inventoryUsage,
            ),
        )
        backgroundDraftJson = characterBackgroundToJsonV4(stored.background)
        traitsDraftJson = characterTraitsToJsonV4(stored.traits)
        traitProvenanceDraftJson = characterTraitProvenanceToJsonP7V4(savedTraitProvenance)
        canonicalOriginsDraftJson = characterCanonicalOriginsDraftToJsonP7V4(savedCanonicalOrigins)
        spellcastingDraftJson = characterSpellcastingDraftToJsonV4(
            CharacterSpellcastingDraftV4(
                sources = stored.spellcastingSources,
                spells = stored.spells,
            ),
        )
        spellcastingProfilesDraftJson = characterSpellcastingProfilesToJsonV4(savedSpellcastingProfiles)
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
        proficiencyDraftJson = characterProficienciesToJsonV4(stored.proficiencies)
        leaveAfterSave = false
        savedMessage = "Guardado"
        if (shouldLeaveAfterPersist) {
            onBack()
        }
    }

    fun enableTableModeNow() {
        if (closureState.tableModeEnabled) return
        closureState = closureRepository.saveState(
            characterId,
            closureState.copy(tableModeEnabled = true),
        )
        savedMessage = "Guardado"
    }

    fun saveAndActivateTableMode() {
        if (!structuralEditingEnabled) return
        if (draft.missingRequiredNumberLabels().isNotEmpty()) {
            activateTableModeAfterBlankSave = true
            confirmBlankNumbers = true
            return
        }
        val candidate = draft.toSheetOrNull(stored) ?: return
        persist(candidate)
        enableTableModeNow()
    }

    fun discardDraftsAndActivateTableMode() {
        draft = CharacterEditorDraftV4.from(stored)
        combatDraftJson = storedCombatDraftJson
        combatDamageDraftJson = storedCombatDamageDraftJson
        spellcastingProfilesDraftJson = storedSpellcastingProfilesDraftJson
        equipmentDraftJson = storedEquipmentDraftJson
        backgroundDraftJson = storedBackgroundDraftJson
        traitsDraftJson = storedTraitsDraftJson
        traitProvenanceDraftJson = storedTraitProvenanceDraftJson
        canonicalOriginsDraftJson = storedCanonicalOriginsDraftJson
        spellcastingDraftJson = storedSpellcastingDraftJson
        notesDraftJson = storedNotesDraftJson
        h1ModuleDraftJson = storedH1ModuleDraftJson
        proficiencyDraftJson = storedProficiencyDraftJson
        enableTableModeNow()
    }

    fun save() {
        if (!structuralEditingEnabled) return
        if (draft.missingRequiredNumberLabels().isNotEmpty()) {
            confirmBlankNumbers = true
            return
        }
        val candidate = draft.toSheetOrNull(stored) ?: return
        persist(candidate)
    }

    fun saveBlankNumbersAsZero() {
        if (!structuralEditingEnabled) return
        val candidate = draft.toSheetOrNull(stored, blankRequiredAsZero = true) ?: return
        confirmBlankNumbers = false
        persist(candidate)
        if (activateTableModeAfterBlankSave) {
            activateTableModeAfterBlankSave = false
            enableTableModeNow()
        }
    }

    fun persistSpellcasterEnabled(enabled: Boolean) {
        if (!structuralEditingEnabled) return
        if (enabled == stored.spellcasterEnabled) return
        stored = repository.saveCharacter(stored.copy(spellcasterEnabled = enabled))
        if (!enabled && selectedTabName == CharacterTabV4.SPELLS.name) {
            selectedTabName = CharacterTabV4.OVERVIEW.name
        }
        savedMessage = "Guardado"
    }

    fun persistStatus(status: CharacterStatus) {
        if (!structuralEditingEnabled) return
        if (status == stored.status && status == draft.status) return
        stored = repository.saveCharacter(stored.copy(status = status))
        draft = draft.copy(status = status)
        savedMessage = "Guardado"
    }

    fun persistClosureState(updated: CharacterClosureState) {
        if (!closureState.tableModeEnabled && updated.tableModeEnabled && hasUnsavedChanges) {
            confirmTableModeTransition = true
            return
        }
        val effective = if (closureState.tableModeEnabled) {
            mergeCharacterOperationalClosureState(closureState, updated)
        } else {
            updated
        }
        if (effective == closureState) return
        closureState = closureRepository.saveState(characterId, effective)
        savedMessage = "Guardado"
    }

    fun persistStructuralClosureState(updated: CharacterClosureState) {
        if (!structuralEditingEnabled) return
        persistClosureState(updated)
    }


    fun syncOperationalDraftsFromStored() {
        val persistedSlots = stored.spellSlots.associateBy { it.level }
        draft = draft.copy(
            maxHp = stored.maxHp.toString(),
            currentHp = stored.currentHp.toString(),
            tempHp = stored.tempHp.toString(),
            spellSlots = draft.spellSlots.map { slot ->
                slot.copy(spent = persistedSlots[slot.level]?.spentSlots ?: 0)
            },
        )
        val currentEquipment = equipmentDraftFromJsonV4(equipmentDraftJson)
        val persistedItems = stored.inventoryItems.associateBy { it.id }
        equipmentDraftJson = equipmentDraftToJsonV4(
            currentEquipment.copy(
                items = currentEquipment.items.map { item ->
                    persistedItems[item.id]?.let { persisted -> item.copy(quantity = persisted.quantity) } ?: item
                },
            ),
        )
        val persistedTraits = stored.traits.associateBy { it.id }
        traitsDraftJson = characterTraitsToJsonV4(
            characterTraitsFromJsonV4(traitsDraftJson).map { trait ->
                persistedTraits[trait.id]?.let { persisted -> trait.copy(spentUses = persisted.spentUses) } ?: trait
            },
        )
    }

    fun persistOperationalSheet(updated: CharacterSheet) {
        val effective = mergeCharacterOperationalState(stored, updated)
        if (effective == stored) return
        stored = repository.saveCharacter(effective)
        syncOperationalDraftsFromStored()
        savedMessage = "Guardado"
    }

    fun persistGeneralHitPointsFromDraft() {
        val currentHp = draft.currentHp.trim().toIntOrNull() ?: return
        val maxHp = draft.maxHp.trim().toIntOrNull() ?: return
        val tempHp = draft.tempHp.trim().toIntOrNull() ?: return
        val updated = setCharacterHitPoints(
            sheet = stored,
            currentHp = currentHp,
            maxHp = maxHp,
        ).copy(tempHp = tempHp.coerceAtLeast(0))
        persistOperationalSheet(updated)
    }

    fun persistStructuralSheet(updated: CharacterSheet) {
        if (!structuralEditingEnabled || updated == stored) return
        stored = repository.saveCharacter(updated)
        savedMessage = "Guardado"
    }

    fun persistCombatOperationalSheet(updated: CharacterSheet) {
        persistOperationalSheet(updated)
    }

    fun persistSupercompactSheet(updated: CharacterSheet) {
        persistOperationalSheet(updated)
    }

    if (showSupercompact) {
        CharacterSupercompactV4(
            sheet = stored,
            closureState = closureState,
            liveControlsEnabled = !hasUnsavedChanges,
            onSheetChange = ::persistSupercompactSheet,
            onBack = { showSupercompact = false },
        )
    } else if (showPcSettings) {
        CharacterPcSettingsClosureV4(
            characterName = draft.name,
            status = draft.status,
            spellcasterEnabled = stored.spellcasterEnabled,
            closureState = closureState,
            suggestedModules = suggestedModules,
            tableModeCanEnable = !hasUnsavedChanges || closureState.tableModeEnabled,
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
            backupExportEnabled = !hasUnsavedChanges,
            onExportBackup = {
                val safeBase = stored.name.trim()
                    .ifBlank { "personaje" }
                    .replace(Regex("[^\\p{L}\\p{N}._-]+"), "_")
                    .trim('_')
                    .take(48)
                    .ifBlank { "personaje" }
                backupExportLauncher.launch("${safeBase}_respaldo_dnd-custom-aid.json")
            },
            onOpenApplicationSettings = onOpenApplicationSettings,
        )
    } else {
        Scaffold { scaffoldPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
            ) {
                val layoutContext = characterLayoutContextForAvailableSizeV4(
                    availableWidthDp = maxWidth.value.toInt(),
                    availableHeightDp = maxHeight.value.toInt(),
                )
                val navigationPresentation = characterNavigationPresentationForLayoutV4(layoutContext)
                val wide = layoutContext.isTablet
                CharacterAdaptiveShellV4(
                    layoutContext = layoutContext,
                    navigationPresentation = navigationPresentation,
                    selectedTab = selectedTab,
                    spellcasterEnabled = stored.spellcasterEnabled,
                    visibleModules = visibleModules,
                    onSelect = { targetTab ->
                        if (selectedTab == CharacterTabV4.OVERVIEW && targetTab != CharacterTabV4.OVERVIEW) {
                            persistGeneralHitPointsFromDraft()
                        }
                        selectedTabName = targetTab.name
                    },
                    header = {
                        EditorHeaderV4(
                            characterName = draft.name,
                            stored = stored,
                            savedMessage = savedMessage,
                            hasUnsavedChanges = hasUnsavedChanges,
                            savable = savable,
                            tableModeEnabled = closureState.tableModeEnabled,
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
                            projectionSheet = overviewProjectionSheet,
                            closureState = closureState,
                            wide = wide,
                            onDraftChange = ::updateStructuralDraft,
                            onOperationalSheetChange = ::persistOperationalSheet,
                            onClosureStateChange = ::persistStructuralClosureState,
                        )
                        CharacterTabV4.SKILLS -> SkillsTabV4(
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
                        CharacterTabV4.COMBAT -> CharacterCombatSuccessorTabV4(
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
                        CharacterTabV4.MANAGEMENT -> CharacterManagementSuccessorTabV4(
                            sheet = stored,
                            generalDraftSheet = settingsSheet,
                            closureState = closureState,
                            onSheetChange = ::persistOperationalSheet,
                            onStructuralSheetChange = ::persistStructuralSheet,
                            onClosureStateChange = ::persistClosureState,
                            structuralEditingEnabled = structuralEditingEnabled,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.EQUIPMENT -> CharacterEquipmentClosureTabV4(
                            draft = equipmentDraft,
                            onDraftChange = ::updateEquipmentDraft,
                            onOperationalItemsChange = { updatedItems ->
                                persistOperationalSheet(stored.copy(inventoryItems = updatedItems))
                            },
                            armorClass = stored.armorClass,
                            resources = stored.resources,
                            onResourceValueChange = { resourceId, value ->
                                stored.resources.firstOrNull { it.id == resourceId }?.let { resource ->
                                    val normalized = resource.maxValue?.let { value.coerceIn(0, it) } ?: value.coerceAtLeast(0)
                                    if (normalized != resource.currentValue) {
                                        persistOperationalSheet(
                                            stored.copy(
                                                resources = stored.resources.map { item ->
                                                    if (item.id == resourceId) item.copy(currentValue = normalized) else item
                                                },
                                            ),
                                        )
                                    }
                                }
                            },
                            structuralEditingEnabled = structuralEditingEnabled,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.BACKGROUND -> CharacterBackgroundTabV4(
                            background = backgroundDraft,
                            canonicalOrigins = canonicalOriginsDraft,
                            onBackgroundChange = ::updateBackground,
                            onCanonicalOriginsChange = ::updateCanonicalOrigins,
                            structuralEditingEnabled = structuralEditingEnabled,
                            wide = wide,
                        )
                        CharacterTabV4.TRAITS -> CharacterTraitsClosureTabV4(
                            traits = traitsDraft,
                            classes = settingsSheet.classes,
                            background = backgroundDraft,
                            successorState = provenanceDraftSuccessorState,
                            traitProvenance = traitProvenanceDraft,
                            closureState = closureState,
                            persistedTraitIds = stored.traits.mapTo(mutableSetOf()) { it.id },
                            resources = stored.resources,
                            onTraitsChange = ::updateTraits,
                            onTraitProvenanceChange = ::updateTraitProvenance,
                            onSpentUsesChange = { traitId, spentUses ->
                                persistOperationalSheet(
                                    stored.copy(
                                        traits = stored.traits.map { trait ->
                                            if (trait.id == traitId) trait.copy(spentUses = spentUses) else trait
                                        },
                                    ),
                                )
                            },
                            onClosureStateChange = ::persistStructuralClosureState,
                            onResourceValueChange = { resourceId, value ->
                                stored.resources.firstOrNull { it.id == resourceId }?.let { resource ->
                                    val normalized = resource.maxValue?.let { value.coerceIn(0, it) } ?: value.coerceAtLeast(0)
                                    if (normalized != resource.currentValue) {
                                        persistOperationalSheet(
                                            stored.copy(
                                                resources = stored.resources.map { item ->
                                                    if (item.id == resourceId) item.copy(currentValue = normalized) else item
                                                },
                                            ),
                                        )
                                    }
                                }
                            },
                            structuralEditingEnabled = structuralEditingEnabled,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.SPELLS -> CharacterSpellsTabV4(
                            draft = spellcastingDraft,
                            spellcastingRows = overviewProjectionSheet.generalSpellcastingRows(projectedSuccessorState),
                            successorState = projectedSuccessorState,
                            projectionSheet = overviewProjectionSheet,
                            spellcastingProfiles = spellcastingProfiles,
                            slotStates = draft.spellSlots.map { slot ->
                                val total = slot.total.toIntOrNull()?.coerceAtLeast(0) ?: 0
                                CharacterSpellSlotUiV4(
                                    level = slot.level,
                                    total = total,
                                    spent = slot.spent.coerceIn(0, total),
                                )
                            },
                            classOptions = draft.classes.map { SpellSourceClassOptionV4(it.id, it.name) },
                            traits = traitsDraft,
                            inventoryItems = equipmentDraft.items,
                            background = backgroundDraft,
                            closureState = closureState,
                            persistedSpellIds = stored.spells.mapTo(mutableSetOf()) { it.id },
                            onDraftChange = ::updateSpellcasting,
                            onSpellcastingProfilesChange = ::updateSpellcastingProfiles,
                            structuralEditingEnabled = structuralEditingEnabled,
                            onSlotSpentChange = { level, spent ->
                                val persistedSlot = stored.spellSlots.firstOrNull { it.level == level }
                                if (persistedSlot != null) {
                                    persistOperationalSheet(
                                        stored.copy(
                                            spellSlots = stored.spellSlots.map { slot ->
                                                if (slot.level == level) slot.copy(spentSlots = spent.coerceIn(0, slot.totalSlots.coerceAtLeast(0))) else slot
                                            },
                                        ),
                                    )
                                }
                            },
                            onClosureStateChange = ::persistStructuralClosureState,
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
                            onClosureStateChange = ::persistStructuralClosureState,
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
                            onClosureStateChange = ::persistStructuralClosureState,
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
                            onClosureStateChange = ::persistStructuralClosureState,
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
                            onClosureStateChange = ::persistStructuralClosureState,
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
                            onClosureStateChange = ::persistStructuralClosureState,
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
                            onClosureStateChange = ::persistStructuralClosureState,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.NOTES -> CharacterNotesTabV4(
                            draft = notesDraft,
                            onDraftChange = ::updateNotes,
                            structuralEditingEnabled = structuralEditingEnabled,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                    }
                }
            }
        }
    }

    backupExportMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { backupExportMessage = null },
            title = { Text("Respaldo local") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { backupExportMessage = null }) { Text("Cerrar") }
            },
        )
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

    if (confirmTableModeTransition) {
        AlertDialog(
            onDismissRequest = { confirmTableModeTransition = false },
            title = { Text("Activar Modo Mesa") },
            text = {
                Column(
                    modifier = Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                ) {
                    Text("Hay cambios de edición pendientes. Revísalos antes de entrar en Modo Mesa.")
                    tableModePendingChanges.forEach { change ->
                        Text("• $change", style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmTableModeTransition = false
                        saveAndActivateTableMode()
                    },
                ) { Text("Guardar y activar") }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            confirmTableModeTransition = false
                            discardDraftsAndActivateTableMode()
                        },
                    ) { Text("Descartar y activar") }
                    TextButton(onClick = { confirmTableModeTransition = false }) {
                        Text("Cancelar")
                    }
                }
            },
        )
    }

    if (confirmBlankNumbers) {
        val missing = draft.missingRequiredNumberLabels()
        AlertDialog(
            onDismissRequest = {
                confirmBlankNumbers = false
                leaveAfterSave = false
                activateTableModeAfterBlankSave = false
            },
            title = { Text("Guardar campos vacíos como 0") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp))) {
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
                        activateTableModeAfterBlankSave = false
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
    tableModeEnabled: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 1.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 3.dp, vertical = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StableBackIconButton(onClick = onBack, contentDescription = "Volver a personajes")
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    characterName.ifBlank { "Ficha de personaje" },
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                )
                when {
                    hasUnsavedChanges -> Text("Cambios sin guardar", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    tableModeEnabled -> Text("Modo Mesa", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                    savedMessage != null -> Text(savedMessage, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                }
            }
            StableSettingsIconButton(onClick = onOpenSettings)
            TextButton(
                onClick = onSave,
                enabled = savable && !tableModeEnabled,
                contentPadding = PaddingValues(horizontal = 7.dp, vertical = 2.dp),
            ) { Text("Guardar", style = MaterialTheme.typography.labelMedium) }
        }
    }
}

@Composable
private fun OverviewTabV4(
    draft: CharacterEditorDraftV4,
    stored: CharacterSheet,
    projectionSheet: CharacterSheet,
    closureState: CharacterClosureState,
    wide: Boolean,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    onOperationalSheetChange: (CharacterSheet) -> Unit,
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
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
    ) {
        item {
            IdentityCardV4(draft, stored, onDraftChange)
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
        item {
            CharacterGeneralSuccessorCardsV4(
                projectionSheet = projectionSheet,
                onInspirationChange = { enabled ->
                    onOperationalSheetChange(stored.copy(inspiration = enabled))
                },
                onResourceValueChange = { resourceId, value ->
                    onOperationalSheetChange(
                        stored.copy(
                            resources = stored.resources.map { resource ->
                                if (resource.id == resourceId) resource.copy(currentValue = value) else resource
                            },
                        ),
                    )
                },
            )
        }
        item {
            CharacterGeneralClosureCardsV4(
                state = closureState,
                onStateChange = onClosureStateChange,
                wide = wide,
            )
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
        Text("Nombre", style = MaterialTheme.typography.labelSmall)
        CompactTextFieldV4(
            value = draft.name,
            onValueChange = { onDraftChange(draft.copy(name = characterProperNameInput(it))) },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "Nivel total ${draft.totalLevel()} · Último guardado ${formatSavedAtV4(stored.updatedAtEpochSeconds)}",
            style = MaterialTheme.typography.labelSmall,
        )
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
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
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
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
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
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
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
                .heightIn(min = 34.dp)
                .clickable { dialogOpen = true },
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 3.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(formatCharacterDistanceFeetV4(value), style = MaterialTheme.typography.bodyMedium, maxLines = 1)
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
                "Vista: ${formatCharacterDistanceFeetV4(pending)}",
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
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
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
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
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
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
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
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
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
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
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
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        verticalAlignment = Alignment.Top,
    ) {
        Text("Nivel ${slot.level}", modifier = Modifier.width(55.dp), style = MaterialTheme.typography.labelMedium)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        ) {
            (0 until total).toList().chunked(8).forEach { indices ->
                Row(horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp))) {
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
            .heightIn(min = 34.dp)
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
    proficiencies: List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency>,
    structuralEditingEnabled: Boolean,
    wide: Boolean,
    skillLayoutChoice: SkillLayoutChoice,
    onSkillLayoutChange: (SkillLayoutChoice) -> Unit,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
    onClosureStateChange: (CharacterClosureState) -> Unit,
    onProficienciesChange: (List<io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiency>) -> Unit,
) {
    val successorState = LocalCharacterPcSettingsContextV4.current?.successorState ?: CharacterSuccessorState()
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
                    item {
                        SkillsListCardV4(
                            draft = draft,
                            wide = wide,
                            onDraftChange = onDraftChange,
                            customSkills = closureState.customSkills,
                            calculationSheet = calculationSheet,
                            successorState = successorState,
                        )
                    }
                }
                SkillLayoutChoice.BY_ATTRIBUTE -> {
                    item {
                        AbilityGroupsCardV4(
                            draft = draft,
                            wide = wide,
                            onDraftChange = onDraftChange,
                            customSkills = closureState.customSkills,
                            calculationSheet = calculationSheet,
                            successorState = successorState,
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
                        modifier = Modifier.padding(horizontal = appSpacingV4(6.dp), vertical = appSpacingV4(8.dp)),
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
        CharacterHelpV4(
            "Marca competencia cuando corresponda. Toca el total para ver el cálculo y editar Ajuste adicional.",
        )
        val columns = if (wide) 3 else 2
        CharacterAbility.entries.chunked(columns).forEach { abilities ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
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
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
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
    customSkills: List<CharacterCustomSkill>,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
) {
    val rows = presentCharacterSkills(
        builtInSkills = calculationSheet.skills,
        customSkills = customSkills,
        successorState = successorState,
    )
    SectionCardV4("Habilidades") {
        CharacterHelpV4(
            "Las habilidades estándar se editan aquí. Las personalizadas se configuran en Ajustes del PJ y aparecen en esta misma lista.",
        )
        if (wide) {
            val midpoint = (rows.size + 1) / 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(10.dp)),
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    rows.take(midpoint).forEachIndexed { index, row ->
                        UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange)
                        if (index < midpoint - 1) HorizontalDivider()
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    val second = rows.drop(midpoint)
                    second.forEachIndexed { index, row ->
                        UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange)
                        if (index < second.lastIndex) HorizontalDivider()
                    }
                }
            }
        } else {
            rows.forEachIndexed { index, row ->
                UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange)
                if (index < rows.lastIndex) HorizontalDivider()
            }
        }
    }
}

@Composable
private fun UnifiedSkillRowV4(
    row: CharacterSkillPresentation,
    draft: CharacterEditorDraftV4,
    customSkills: List<CharacterCustomSkill>,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    onDraftChange: (CharacterEditorDraftV4) -> Unit,
) {
    val builtInKey = row.builtInKey
    if (builtInKey != null) {
        val skill = draft.skills.firstOrNull { it.key == builtInKey } ?: return
        SkillRowV4(skill, draft, onDraftChange)
        return
    }
    val customSkill = row.customSkillId?.let { id -> customSkills.firstOrNull { it.id == id } } ?: return
    CustomSkillProjectionRowV4(row, customSkill, calculationSheet, successorState)
}

@Composable
private fun CustomSkillProjectionRowV4(
    row: CharacterSkillPresentation,
    skill: CharacterCustomSkill,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
) {
    val total = calculationSheet.customSkillTotal(skill, successorState)
    val abbreviation = characterAbilityReferenceAbbreviation(row.ability, successorState)
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${row.label} ($abbreviation)",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
            maxLines = 3,
        )
        ReadOnlySkillTotalV4(total)
        ReadOnlyTrainingV4(skill.training)
    }
}

@Composable
private fun ReadOnlySkillTotalV4(total: Int?) {
    Surface(
        modifier = Modifier.width(58.dp).heightIn(min = 34.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Box(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
            Text(total?.let(::formatSignedV4) ?: "—", style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        }
    }
}

@Composable
private fun ReadOnlyTrainingV4(training: SkillTraining) {
    // Match the 48dp layout footprint reserved by the ordinary M3 training selector
    // while keeping the custom projection visually compact and read-only.
    Box(
        modifier = Modifier.width(48.dp).heightIn(min = 48.dp, max = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.width(44.dp).heightIn(min = 34.dp, max = 34.dp),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Box(contentAlignment = Alignment.Center) { TrainingGlyphV4(training) }
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
        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
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
                .heightIn(min = 34.dp),
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
    customSkills: List<CharacterCustomSkill>,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
) {
    val rows = presentCharacterSkills(
        builtInSkills = calculationSheet.skills,
        customSkills = customSkills,
        successorState = successorState,
    )
    SectionCardV4("Características, salvaciones y habilidades") {
        val columns = if (wide) 3 else 2
        CharacterAbility.entries.chunked(columns).forEach { rowAbilities ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                verticalAlignment = Alignment.Top,
            ) {
                rowAbilities.forEach { ability ->
                    AbilityGroupV4(
                        ability = ability,
                        draft = draft,
                        relatedSkills = rows.filter { it.ability.builtIn == ability },
                        customSkills = customSkills,
                        calculationSheet = calculationSheet,
                        successorState = successorState,
                        onDraftChange = onDraftChange,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - rowAbilities.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }

        val customGroups = successorState.customAttributes
            .sortedBy { it.sortOrder }
            .mapNotNull { attribute ->
                val related = rows.filter { it.ability.customAttributeId == attribute.id }
                if (related.isEmpty()) null else attribute to related
            }
        customGroups.chunked(columns).forEach { groups ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
                verticalAlignment = Alignment.Top,
            ) {
                groups.forEach { (attribute, related) ->
                    CustomAttributeAbilityGroupV4(
                        attribute = attribute,
                        relatedSkills = related,
                        customSkills = customSkills,
                        calculationSheet = calculationSheet,
                        successorState = successorState,
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(columns - groups.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun AbilityGroupV4(
    ability: CharacterAbility,
    draft: CharacterEditorDraftV4,
    relatedSkills: List<CharacterSkillPresentation>,
    customSkills: List<CharacterCustomSkill>,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
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
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
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
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
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
                SaveProficiencyToggleV4(
                    proficient = save.proficient,
                    onToggle = {
                        onDraftChange(draft.withSave(save.copy(proficient = !save.proficient)))
                    },
                )
            }
            relatedSkills.forEach { row ->
                UnifiedSkillRowV4(row, draft, customSkills, calculationSheet, successorState, onDraftChange)
            }
        }
    }
}

@Composable
private fun CustomAttributeAbilityGroupV4(
    attribute: CharacterCustomAttribute,
    relatedSkills: List<CharacterSkillPresentation>,
    customSkills: List<CharacterCustomSkill>,
    calculationSheet: CharacterSheet,
    successorState: CharacterSuccessorState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
        ) {
            Text("${attribute.name} (${attribute.abbreviation})", style = MaterialTheme.typography.labelLarge)
            Text(
                "Puntuación ${attribute.score} · Mod ${formatSignedV4(attribute.modifier)}",
                style = MaterialTheme.typography.labelMedium,
            )
            if (attribute.savingThrowEnabled) {
                Text(
                    "Salv. ${calculationSheet.customSavingThrowTotal(attribute)?.let(::formatSignedV4) ?: "—"}",
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            relatedSkills.forEach { row ->
                val customSkill = row.customSkillId?.let { id -> customSkills.firstOrNull { it.id == id } }
                if (customSkill != null) {
                    CustomSkillProjectionRowV4(row, customSkill, calculationSheet, successorState)
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
                .padding(horizontal = 4.dp, vertical = 3.dp),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(3.dp)),
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
        modifier = modifier.heightIn(min = 34.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 4.dp),
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
        modifier = modifier.heightIn(min = 34.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp),
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
    SkillKey.ANIMAL_HANDLING -> "Trato con animales"
    SkillKey.ARCANA -> "Conocimiento Arcano"
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


private fun formatSignedV4(value: Int): String = if (value >= 0) "+$value" else value.toString()

private fun formatSavedAtV4(epochSeconds: Long): String = runCatching {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()).format(formatter)
}.getOrElse { "—" }
