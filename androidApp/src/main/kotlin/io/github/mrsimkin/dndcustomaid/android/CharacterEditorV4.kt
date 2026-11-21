package io.github.mrsimkin.dndcustomaid.android

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.android.pdf.AndroidPcSheetExportOptions
import io.github.mrsimkin.dndcustomaid.android.pdf.AndroidPcSheetExportService
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterCombatDamageProfile
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportAggregate
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterQuickAccessKind
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSpellcastingProfile
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.character.generalSpellcastingRows
import io.github.mrsimkin.dndcustomaid.shared.character.isCharacterStructuralEditingEnabled
import io.github.mrsimkin.dndcustomaid.shared.character.mergeCharacterOperationalClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.mergeCharacterOperationalState
import io.github.mrsimkin.dndcustomaid.shared.character.needsCharacterSpellcastingBootstrap
import io.github.mrsimkin.dndcustomaid.shared.character.reconcileCharacterSpellcastingBootstrap
import io.github.mrsimkin.dndcustomaid.shared.character.setCharacterHitPoints
import io.github.mrsimkin.dndcustomaid.shared.character.suggestedCharacterModules
import io.github.mrsimkin.dndcustomaid.shared.character.visibleCharacterModules
import java.io.File
import kotlin.uuid.Uuid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
private enum class PcSheetAndroidExportActionV4 {
    SAVE,
    SHARE,
}

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
    val storedSpellcastingBootstrap = remember(
        stored.classes,
        stored.spellcastingSources,
        successorState.spellcastingProfiles,
    ) {
        reconcileCharacterSpellcastingBootstrap(
            classes = stored.classes,
            existingSources = stored.spellcastingSources,
            existingProfiles = successorState.spellcastingProfiles,
        )
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
    var combatDamageDraftJson by rememberSaveable(characterId.toString(), "combat-damage") {
        mutableStateOf(characterCombatDamageProfilesToJsonV4(successorState.combatDamage))
    }
    var spellcastingProfilesDraftJson by rememberSaveable(characterId.toString(), "spellcasting-profiles") {
        mutableStateOf(characterSpellcastingProfilesToJsonV4(storedSpellcastingBootstrap.profiles))
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
                    sources = storedSpellcastingBootstrap.sources,
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
    var pcSheetExportBusy by remember(characterId) { mutableStateOf(false) }
    var pcSheetExportMessage by rememberSaveable(characterId.toString(), "pc-sheet-export-message") {
        mutableStateOf<String?>(null)
    }
    var pendingPcSheetSavePath by rememberSaveable(characterId.toString(), "pc-sheet-save-path") {
        mutableStateOf<String?>(null)
    }
    var pendingPcSheetSaveNotice by rememberSaveable(characterId.toString(), "pc-sheet-save-notice") {
        mutableStateOf<String?>(null)
    }
    var pendingPcSheetAction by remember { mutableStateOf<PcSheetAndroidExportActionV4?>(null) }
    var pendingPcSheetOptions by remember { mutableStateOf<AndroidPcSheetExportOptions?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val pcSheetExportService = remember(context) { AndroidPcSheetExportService(context) }
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

    val pcSheetSaveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf"),
    ) { uri ->
        val stagedPath = pendingPcSheetSavePath
        pendingPcSheetSavePath = null
        val staged = stagedPath?.let(::File)
        if (uri == null) {
            staged?.delete()
            pendingPcSheetSaveNotice = null
            pcSheetExportMessage = "Guardado de PDF cancelado."
        } else if (staged == null || !staged.isFile) {
            pendingPcSheetSaveNotice = null
            pcSheetExportMessage = "El PDF temporal ya no está disponible; vuelve a generar la hoja."
        } else {
            pcSheetExportBusy = true
            coroutineScope.launch {
                val result = runCatching {
                    withContext(Dispatchers.IO) {
                        pcSheetExportService.writeToDocument(staged, uri)
                    }
                }
                staged.delete()
                pcSheetExportMessage = if (result.isSuccess) {
                    "PDF guardado correctamente." +
                        pendingPcSheetSaveNotice?.let { " $it" }.orEmpty()
                } else {
                    result.exceptionOrNull()?.message ?: "No se pudo escribir el PDF seleccionado."
                }
                pendingPcSheetSaveNotice = null
                pcSheetExportBusy = false
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
    LaunchedEffect(settingsSheet.classes) {
        val reconciled = reconcileCharacterSpellcastingBootstrap(
            classes = settingsSheet.classes,
            existingSources = spellcastingDraft.sources,
            existingProfiles = spellcastingProfiles,
        )
        if (reconciled.sources != spellcastingDraft.sources) {
            spellcastingDraftJson = characterSpellcastingDraftToJsonV4(
                spellcastingDraft.copy(sources = reconciled.sources),
            )
            savedMessage = null
        }
        if (reconciled.profiles != spellcastingProfiles) {
            spellcastingProfilesDraftJson = characterSpellcastingProfilesToJsonV4(reconciled.profiles)
            savedMessage = null
        }
    }
    val canonicalSpellcastingBootstrapNeeded = remember(settingsSheet.classes, stored.spellcastingSources) {
        needsCharacterSpellcastingBootstrap(settingsSheet.classes, stored.spellcastingSources)
    }
    val effectiveSpellcasterEnabled = stored.spellcasterEnabled || canonicalSpellcastingBootstrapNeeded
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
        spellcasterEnabled = effectiveSpellcasterEnabled,
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
    val storedSpellcastingProfilesDraftJson = remember(storedSpellcastingBootstrap.profiles) {
        characterSpellcastingProfilesToJsonV4(storedSpellcastingBootstrap.profiles)
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
    val storedSpellcastingDraftJson = remember(storedSpellcastingBootstrap.sources, stored.spells) {
        characterSpellcastingDraftToJsonV4(
            CharacterSpellcastingDraftV4(
                sources = storedSpellcastingBootstrap.sources,
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

    fun currentPcSheetExportAggregate(): PcSheetExportAggregate {
        val candidate = requireNotNull(
            draft.toSheetOrNull(stored, blankRequiredAsZero = true),
        ) {
            "Los cambios actuales no forman una ficha exportable."
        }
        val equipment = equipmentDraftFromJsonV4(equipmentDraftJson)
        val spellcasting = characterSpellcastingDraftFromJsonV4(spellcastingDraftJson)
        val notes = characterNotesDraftFromJsonV4(notesDraftJson)
        val modules = characterH1ModuleDraftFromJsonV4(h1ModuleDraftJson)
        val profiles = characterSpellcastingProfilesFromJsonV4(spellcastingProfilesDraftJson)
        val reconciledSpellcasting = reconcileCharacterSpellcastingBootstrap(
            classes = candidate.classes,
            existingSources = spellcasting.sources,
            existingProfiles = profiles,
        )
        val bootstrapNeeded = needsCharacterSpellcastingBootstrap(
            classes = candidate.classes,
            existingSources = stored.spellcastingSources,
        )
        val integratedSheet = setCharacterHitPoints(
            sheet = candidate,
            currentHp = candidate.currentHp,
            maxHp = candidate.maxHp,
        ).copy(
            tempHp = candidate.tempHp.coerceAtLeast(0),
            combatEntries = combatEntriesFromJsonV4(combatDraftJson),
            inventoryItems = equipment.items,
            currencies = equipment.currencies,
            background = characterBackgroundFromJsonV4(backgroundDraftJson),
            traits = characterTraitsFromJsonV4(traitsDraftJson),
            spellcastingSources = reconciledSpellcasting.sources,
            spells = spellcasting.spells,
            spellcasterEnabled = candidate.spellcasterEnabled || bootstrapNeeded,
            generalNotes = notes.generalNotes,
            noteCards = notes.cards,
            proficiencies = characterProficienciesFromJsonV4(proficiencyDraftJson),
            classOptions = modules.classOptions,
            forms = modules.forms,
            companions = modules.companions,
        )
        val canonicalOrigins = characterCanonicalOriginsDraftFromJsonP7V4(canonicalOriginsDraftJson).normalized()
        val provenanceState = canonicalOrigins.projectOnto(successorState)
        val liveTraitIds = integratedSheet.traits.mapTo(mutableSetOf()) { it.id }
        val projectedTraitProvenance = refreshResolvedTraitProvenanceLabelsP7V4(
            items = characterTraitProvenanceFromJsonP7V4(traitProvenanceDraftJson)
                .filter { it.traitId in liveTraitIds },
            classes = integratedSheet.classes,
            successorState = provenanceState,
        )
        val projectedSuccessor = provenanceState.copy(
            combatDamage = characterCombatDamageProfilesFromJsonV4(combatDamageDraftJson),
            spellcastingProfiles = reconciledSpellcasting.profiles,
            traitProvenance = projectedTraitProvenance,
        )
        val projectedClosure = closureState.copy(
            inventoryUsage = equipment.inventoryUsage,
        )
        return PcSheetExportAggregate(
            sheet = integratedSheet,
            closure = projectedClosure,
            successor = projectedSuccessor,
        )
    }

    fun startPcSheetExport(
        action: PcSheetAndroidExportActionV4,
        options: AndroidPcSheetExportOptions,
    ) {
        if (pcSheetExportBusy) return
        val exportingUnsavedChanges = hasUnsavedChanges
        val aggregate = runCatching { currentPcSheetExportAggregate() }
            .getOrElse { error ->
                pcSheetExportMessage = error.message ?: "No se pudo preparar el estado actual para exportar."
                return
            }
        val sources = PcSheetExportSources(permanent = aggregate)
        pcSheetExportBusy = true
        pcSheetExportMessage = "Generando PDF local…"

        coroutineScope.launch {
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    val purpose = if (action == PcSheetAndroidExportActionV4.SAVE) "save" else "share"
                    // C473 (owner 2026-11-10): V11 is the only sheet.
                    run {
                        val proof = pcSheetExportService.generateV11Proof(
                            PcSheetV11ExportRequest(
                                variant = options.variant,
                                stateSelection = options.stateSelection,
                                portraitFitMode = options.portraitFitMode,
                                includeSpellDescriptions = options.includeSpellDescriptions,
                                lightInkNumbers = options.lightInkNumbers,
                            ),
                            sources,
                        )
                        val staged = pcSheetExportService.stageV11Proof(
                            generated = proof,
                            characterName = aggregate.sheet.name,
                            purpose = purpose,
                        )
                        proof.semantic.notices to staged
                    }
                }
            }
            val pair = result.getOrElse { error ->
                pcSheetExportMessage = error.message ?: "No se pudo generar el PDF."
                pcSheetExportBusy = false
                return@launch
            }
            val staged = pair.second
            val notice = pair.first
                .joinToString(" ") { it.message }
                .trim()
                .takeIf { it.isNotEmpty() }
            val unsavedNotice = if (exportingUnsavedChanges) {
                "El PDF usa los cambios visibles sin guardarlos en el personaje."
            } else {
                null
            }
            val combinedNotice = listOfNotNull(unsavedNotice, notice)
                .joinToString(" ").takeIf { it.isNotBlank() }

            when (action) {
                PcSheetAndroidExportActionV4.SAVE -> {
                    pendingPcSheetSavePath = staged.absolutePath
                    pendingPcSheetSaveNotice = combinedNotice
                    pcSheetExportBusy = false
                    pcSheetSaveLauncher.launch(
                        pcSheetExportService.suggestedV11ProofFileName(
                            aggregate.sheet.name, options.variant, options.lightInkNumbers,
                        ),
                    )
                }

                PcSheetAndroidExportActionV4.SHARE -> {
                    val shareResult = runCatching {
                        val uri = pcSheetExportService.shareUri(staged)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(
                            Intent.createChooser(intent, "Compartir hoja de personaje PDF"),
                        )
                    }
                    pcSheetExportMessage = if (shareResult.isSuccess) {
                        "PDF preparado para compartir." +
                            combinedNotice?.let { " " + it }.orEmpty()
                    } else {
                        shareResult.exceptionOrNull()?.message ?: "No se pudo abrir el selector para compartir."
                    }
                    pcSheetExportBusy = false
                }
            }
        }
    }

    fun requestPcSheetExport(
        action: PcSheetAndroidExportActionV4,
        options: AndroidPcSheetExportOptions,
    ) {
        if (pcSheetExportBusy) return
        if (hasUnsavedChanges) {
            pendingPcSheetAction = action
            pendingPcSheetOptions = options
        } else {
            startPcSheetExport(action, options)
        }
    }

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
            spellcasterEnabled = effectiveSpellcasterEnabled,
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
        val bootstrapNeededBeforePersist = needsCharacterSpellcastingBootstrap(
            classes = normalizedCandidate.classes,
            existingSources = stored.spellcastingSources,
        )
        val reconciledSpellcasting = reconcileCharacterSpellcastingBootstrap(
            classes = normalizedCandidate.classes,
            existingSources = spellcasting.sources,
            existingProfiles = characterSpellcastingProfilesFromJsonV4(spellcastingProfilesDraftJson),
        )
        val integrated = normalizedCandidate.copy(
            combatEntries = combatEntriesFromJsonV4(combatDraftJson),
            inventoryItems = equipment.items,
            currencies = equipment.currencies,
            background = characterBackgroundFromJsonV4(backgroundDraftJson),
            traits = characterTraitsFromJsonV4(traitsDraftJson),
            spellcastingSources = reconciledSpellcasting.sources,
            spells = spellcasting.spells,
            spellcasterEnabled = normalizedCandidate.spellcasterEnabled || bootstrapNeededBeforePersist,
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
        val savedSpellcastingProfiles = reconciledSpellcasting.profiles
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
            spellcasterEnabled = effectiveSpellcasterEnabled,
            closureState = closureState,
            suggestedModules = suggestedModules,
            tableModeCanEnable = !hasUnsavedChanges || closureState.tableModeEnabled,
            onBack = {
                showPcSettings = false
                selectedTabName = resolvedCharacterTabV4(
                    savedTabName = selectedTabName,
                    spellcasterEnabled = effectiveSpellcasterEnabled,
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
            pcSheetExportBusy = pcSheetExportBusy,
            pcSheetHasUnsavedChanges = hasUnsavedChanges,
            pcSheetExportMessage = pcSheetExportMessage,
            onSavePcSheetPdf = { options ->
                requestPcSheetExport(PcSheetAndroidExportActionV4.SAVE, options)
            },
            onSharePcSheetPdf = { options ->
                requestPcSheetExport(PcSheetAndroidExportActionV4.SHARE, options)
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
                    spellcasterEnabled = effectiveSpellcasterEnabled,
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
                            structuralEditingEnabled = structuralEditingEnabled,
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
                            preferences = preferences,
                            onPreferencesChange = onPreferencesChange,
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

    if (pendingPcSheetAction != null && pendingPcSheetOptions != null) {
        val missingRequiredNumbers = draft.missingRequiredNumberLabels()
        AlertDialog(
            onDismissRequest = {
                pendingPcSheetAction = null
                pendingPcSheetOptions = null
            },
            title = { Text("Exportar cambios sin guardar") },
            text = {
                Text(
                    buildString {
                        append("El PDF usará los cambios que ves ahora, pero esos cambios no se guardarán en el personaje.")
                        if (missingRequiredNumbers.isNotEmpty()) {
                            append(" Los campos numéricos requeridos vacíos se proyectarán como 0 solo para esta exportación.")
                        }
                    },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val action = pendingPcSheetAction
                        val options = pendingPcSheetOptions
                        pendingPcSheetAction = null
                        pendingPcSheetOptions = null
                        if (action != null && options != null) {
                            startPcSheetExport(action, options)
                        }
                    },
                ) { Text("Exportar sin guardar") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingPcSheetAction = null
                        pendingPcSheetOptions = null
                    },
                ) { Text("Cancelar") }
            },
        )
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
