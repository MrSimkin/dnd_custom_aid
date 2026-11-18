package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorRepository
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportAggregate
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportSources
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetExportStateSelection
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetPortraitFitMode
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11ExportRequest
import io.github.mrsimkin.dndcustomaid.shared.character.PcSheetV11Variant
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedCampaignMember
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedMutationType
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedOutboxRepository
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcDmCorrectionService
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedPcSyncBaselineRepository
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedRetryState
import io.github.mrsimkin.dndcustomaid.shared.hosted.PcDmCorrectionPatch
import io.github.mrsimkin.dndcustomaid.shared.hosted.QueuedPcDmCorrection
import io.github.mrsimkin.dndcustomaid.shared.spine.AccountIdentity
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.PcAuthority
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid
internal data class DesktopPcSyncAudit(
    val revision: Revision,
    val deletedAtEpochSeconds: Long?,
    val baselineRevision: Revision?,
    val pendingRetryState: HostedRetryState?,
    val pendingAttemptCount: Long?,
    val pendingErrorCode: String?,
)

internal data class DesktopPcDetails(
    val character: CharacterSheet,
    val closure: CharacterClosureState,
    val successor: CharacterSuccessorState,
    val authority: PcAuthority?,
    val owner: AccountIdentity?,
    val controller: AccountIdentity?,
    val sync: DesktopPcSyncAudit,
)

/** The options of the V11 sheet export (the only sheet since C473). */
internal data class DesktopPcSheetExportOptions(
    val stateSelection: PcSheetExportStateSelection = PcSheetExportStateSelection.PERMANENT,
    val portraitFitMode: PcSheetPortraitFitMode = PcSheetPortraitFitMode.CROP_TO_FILL,
    val includeSpellDescriptions: Boolean = false,
)

internal fun desktopPcSheetExportSources(details: DesktopPcDetails): PcSheetExportSources =
    PcSheetExportSources(
        permanent = PcSheetExportAggregate(
            sheet = details.character,
            closure = details.closure,
            successor = details.successor,
        ),
    )

internal fun eligiblePcAuthorityMembers(
    members: List<HostedCampaignMember>,
): List<HostedCampaignMember> = members
    .filter { it.status == CampaignMembershipStatus.ACTIVE }
    .distinctBy { it.userId }
    .sortedWith(
        compareBy<HostedCampaignMember> {
            it.displayName?.trim()?.lowercase().orEmpty()
        }.thenBy { it.userId.toString() },
    )

internal fun filterDesktopPcs(
    characters: List<CharacterSheet>,
    rawQuery: String,
): List<CharacterSheet> {
    val query = rawQuery.trim().lowercase()
    return characters
        .filter { sheet ->
            query.isEmpty() || buildList {
                add(sheet.name)
                add(sheet.status.name)
                add(sheet.background.name)
                add(sheet.background.race)
                addAll(sheet.classes.map { it.name })
                addAll(sheet.classes.mapNotNull { it.subclassName })
                addAll(sheet.traits.map { it.name })
                addAll(sheet.spells.map { it.name })
            }.any { it.lowercase().contains(query) }
        }
        .sortedBy { it.name.lowercase() }
}

class DesktopPcManagerController(
    database: AppDatabase,
    private val nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    private val characters = CharacterRepository(database)
    private val players = io.github.mrsimkin.dndcustomaid.shared.character.CharacterPlayerRepository(database)

    /** Redesign A13: who plays each PC, even before that person has an account. */
    fun player(characterId: Uuid): String? = players.player(characterId)

    fun setPlayer(characterId: Uuid, name: String?) = players.setPlayer(characterId, name)
    private val closure = CharacterClosureRepository(database)
    private val successor = CharacterSuccessorRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val baselines = HostedPcSyncBaselineRepository(database)
    private val outbox = HostedOutboxRepository(database)
    private val corrections = HostedPcDmCorrectionService(database)

    fun campaignPcs(campaignId: Uuid): List<CharacterSheet> = characters.listCharacters(campaignId)

    /**
     * Owner, 2026-11-18: the step-by-step new PC also lives on Windows. The wizard's answers fill
     * the sheet exactly as on the tablet; with no answers the PC starts empty.
     */
    fun createCharacter(
        campaignId: Uuid,
        name: String,
        build: io.github.mrsimkin.dndcustomaid.shared.dm.CharacterBuild?,
    ): CharacterSheet {
        val created = characters.createCharacter(campaignId, name)
        return build?.let { characters.saveCharacter(io.github.mrsimkin.dndcustomaid.shared.dm.buildCharacter(created, it)) } ?: created
    }

    fun activeDmAccountId(campaignId: Uuid): Uuid? =
        spine.memberships(campaignId)
            .asSequence()
            .filter { it.role == CampaignRole.DM && it.status == CampaignMembershipStatus.ACTIVE }
            .map { it.accountId }
            .distinct()
            .toList()
            .singleOrNull()

    internal fun details(characterId: Uuid): DesktopPcDetails? {
        val character = characters.character(characterId) ?: return null
        val authority = spine.pcAuthority(characterId)
        val metadata = spine.syncMetadata("PC", characterId)
        val baseline = baselines.baseline(characterId)
        val pending = outbox.allMutations().firstOrNull {
            it.type == HostedMutationType.PC_SNAPSHOT_PUT && it.objectId == characterId
        }
        return DesktopPcDetails(
            character = character,
            closure = closure.state(characterId),
            successor = successor.state(characterId),
            authority = authority,
            owner = authority?.ownerAccountId?.let(spine::account),
            controller = authority?.controllerAccountId?.let(spine::account),
            sync = DesktopPcSyncAudit(
                revision = metadata.revision,
                deletedAtEpochSeconds = metadata.deletedAtEpochSeconds,
                baselineRevision = baseline?.revision,
                pendingRetryState = pending?.retryState,
                pendingAttemptCount = pending?.attemptCount,
                pendingErrorCode = pending?.lastErrorCode,
            ),
        )
    }

    fun correctAsDm(
        characterId: Uuid,
        dmAccountId: Uuid,
        patch: PcDmCorrectionPatch,
        reason: String,
    ): QueuedPcDmCorrection = corrections.correct(
        characterId = characterId,
        dmAccountId = dmAccountId,
        patch = patch,
        rawReason = reason,
        correctedAtEpochSeconds = nowEpochSeconds(),
    )
}

@Composable
internal fun DesktopPcManagerScreen(
    controller: DesktopPcManagerController,
    hostedController: DesktopHostedCampaignAdministrationController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
    // «Nuevo personaje» needs the SRD (through the platform) and the DM's homebrew (the database).
    database: AppDatabase? = null,
    platform: io.github.mrsimkin.dndcustomaid.dmui.DmPlatform? = null,
    accountId: Uuid? = null,
    // The two operating-system dialogs; a test without a display replaces them.
    chooseSaveTarget: (String) -> File? = ::choosePcSheetSaveTarget,
    copyFileToClipboard: ((File) -> Unit)? = null,
) {
    var refreshVersion by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<Uuid?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var correctionMode by remember { mutableStateOf(false) }
    var correctionReason by remember { mutableStateOf("") }
    var authorityMode by remember { mutableStateOf(false) }
    var authorityLoading by remember { mutableStateOf(false) }
    var authorityMembers by remember { mutableStateOf<List<HostedCampaignMember>>(emptyList()) }
    var selectedOwnerId by remember { mutableStateOf<Uuid?>(null) }
    var selectedControllerId by remember { mutableStateOf<Uuid?>(null) }
    var exportOptions by remember(selectedId) { mutableStateOf(DesktopPcSheetExportOptions()) }
    var v11ProofVariant by remember(selectedId) { mutableStateOf(PcSheetV11Variant.A) }
    var exportBusy by remember { mutableStateOf(false) }
    var showTechnical by remember(selectedId) { mutableStateOf(false) }
    val exportService = remember { DesktopPcSheetExportService() }
    val deliverToClipboard = copyFileToClipboard ?: exportService::copyFileToClipboard
    val coroutineScope = rememberCoroutineScope()

    if (activeCampaign == null) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Personajes", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Abre una campaña para ver sus personajes.")
        }
        return
    }

    val pcs = remember(refreshVersion, activeCampaign.id) { controller.campaignPcs(activeCampaign.id) }
    val visiblePcs = filterDesktopPcs(pcs, query)
    val details = remember(refreshVersion, selectedId) { selectedId?.let(controller::details) }
    val dmAccountId = remember(refreshVersion, activeCampaign.id) { controller.activeDmAccountId(activeCampaign.id) }
    var correctionDraft by remember(
        details?.character?.id,
        details?.character?.updatedAtEpochSeconds,
        correctionMode,
    ) {
        mutableStateOf(details?.character?.let(PcCorrectionDraft::from))
    }

    fun refresh(selectId: Uuid? = selectedId) {
        selectedId = selectId
        refreshVersion += 1
    }

    var creating by remember { mutableStateOf(false) }
    if (creating && database != null && platform != null) {
        DesktopNewCharacterDialog(
            campaigns = listOf(activeCampaign),
            database = database,
            platform = platform,
            accountId = accountId,
            onDismiss = { creating = false },
            onCreate = { campaignId, name, build ->
                val created = controller.createCharacter(campaignId, name, build)
                creating = false
                statusMessage = "«${created.name}» ya está en la campaña. Abajo puedes seguir ajustando su hoja."
                onQaEvent("pc-created:${created.id}")
                refresh(created.id)
            },
        )
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            WorkbenchHeading(
                title = "Personajes",
                subtitle = "Los PJ de la campaña, quién los juega y su hoja.",
                help = "1) Toca «＋ Nuevo personaje» para crear uno paso a paso, o elige uno de la lista. " +
                    "2) Arriba escribe quién lo juega («Jugador o jugadora») y toca «Guardar». " +
                    "3) Debajo ves su hoja y puedes exportar el PDF. 4) Los PJ que crean los jugadores en su App llegan al sincronizar.",
            )
        }
        if (database != null && platform != null) {
            Button(onClick = { creating = true }) { Text("＋ Nuevo personaje") }
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar por nombre, estado, clase, rasgo o conjuro") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        statusMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        HorizontalDivider()

        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LazyColumn(
                modifier = Modifier.width(300.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                item {
                    Text(
                        "Personajes de " + activeCampaign.name + " (" + visiblePcs.size + ")",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (visiblePcs.isEmpty()) {
                    item {
                        EmptyList(
                            searching = query.isNotBlank(),
                            nothingYet = "Aún no hay personajes en esta campaña.",
                            howToCreate = "Toca «＋ Nuevo personaje» para crear uno paso a paso. Los que crean los jugadores en su App llegan al sincronizar.",
                        )
                    }
                }
                items(visiblePcs, key = { it.id.toString() }) { character ->
                    val selected = character.id == selectedId
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectedId = character.id
                            correctionMode = false
                            correctionReason = ""
                            authorityMode = false
                            authorityMembers = emptyList()
                            selectedOwnerId = null
                            selectedControllerId = null
                            statusMessage = null
                        },
                        elevation = androidx.compose.material3.CardDefaults.cardElevation(if (selected) 6.dp else 1.dp),
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(character.name, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            controller.player(character.id)?.let { Text("Jugador/a: " + it, style = MaterialTheme.typography.bodySmall) }
                            Text(
                                pcStatusLabel(character.status) + " • nivel " + character.totalLevel,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Text(
                                "Cambiado: " + formatPcEpoch(character.updatedAtEpochSeconds),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp))

            if (details == null) {
                Column(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Elige un personaje de la lista para verlo.", style = MaterialTheme.typography.titleLarge)
                    Text("Personajes en esta campaña: " + pcs.size)
                }
            } else {
                val selectedDetails = details

                fun saveV11ProofPdf() {
                    val target = chooseSaveTarget(
                        exportService.suggestedV11ProofFileName(
                            selectedDetails.character.name, v11ProofVariant,
                        ),
                    ) ?: return
                    exportBusy = true
                    statusMessage = "Generando el PDF…"
                    coroutineScope.launch {
                        try {
                            val proof = withContext(Dispatchers.IO) {
                                exportService.generateV11Proof(
                                    PcSheetV11ExportRequest(
                                        variant = v11ProofVariant,
                                        stateSelection = exportOptions.stateSelection,
                                        portraitFitMode = exportOptions.portraitFitMode,
                                        includeSpellDescriptions = exportOptions.includeSpellDescriptions,
                                    ),
                                    desktopPcSheetExportSources(selectedDetails),
                                )
                            }
                            val file = withContext(Dispatchers.IO) {
                                exportService.saveV11Proof(proof, target)
                            }
                            statusMessage = "PDF guardado: " + file.absolutePath +
                                ". Es una prueba interna, NO el diseño visual aprobado."
                            onQaEvent("PC Manager: PDF V11 interno guardado para " + selectedDetails.character.id)
                        } catch (error: Throwable) {
                            statusMessage = error.message ?: "No se pudo generar el PDF de prueba."
                        } finally {
                            exportBusy = false
                        }
                    }
                }

                fun shareV11ProofPdf() {
                    exportBusy = true
                    statusMessage = "Generando el PDF para compartir…"
                    coroutineScope.launch {
                        try {
                            val proof = withContext(Dispatchers.IO) {
                                exportService.generateV11Proof(
                                    PcSheetV11ExportRequest(
                                        variant = v11ProofVariant,
                                        stateSelection = exportOptions.stateSelection,
                                        portraitFitMode = exportOptions.portraitFitMode,
                                        includeSpellDescriptions = exportOptions.includeSpellDescriptions,
                                    ),
                                    desktopPcSheetExportSources(selectedDetails),
                                )
                            }
                            val file = withContext(Dispatchers.IO) {
                                exportService.createShareV11Proof(
                                    proof, selectedDetails.character.name,
                                )
                            }
                            deliverToClipboard(file)
                            statusMessage = "PDF copiado al portapapeles como archivo; pégalo en el correo o el chat."
                            onQaEvent("PC Manager: PDF V11 interno compartido para " + selectedDetails.character.id)
                        } catch (error: Throwable) {
                            statusMessage = error.message ?: "No se pudo compartir el PDF de prueba."
                        } finally {
                            exportBusy = false
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(details.character.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                PcPlayerField(
                                    current = controller.player(details.character.id),
                                    key = details.character.id.toString() + refreshVersion,
                                    onSave = { name ->
                                        controller.setPlayer(details.character.id, name)
                                        statusMessage = if (name.isBlank()) "Jugador/a quitado." else "Jugador/a: " + name.trim()
                                        refresh()
                                    },
                                )
                                Text(
                                    pcStatusLabel(details.character.status) + " • nivel " + details.character.totalLevel,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            if (!correctionMode && !authorityMode) {
                                Button(
                                    enabled = dmAccountId != null &&
                                        details.sync.deletedAtEpochSeconds == null &&
                                        details.sync.pendingRetryState == null,
                                    onClick = {
                                        correctionMode = true
                                        correctionDraft = PcCorrectionDraft.from(details.character)
                                        correctionReason = ""
                                    },
                                ) { Text("Corregir / Editar como DM") }
                            } else {
                                TextButton(onClick = {
                                    correctionMode = false
                                    correctionReason = ""
                                }) { Text("Cancelar corrección") }
                            }
                        }
                    }

                    item {
                        PcAuditSection("Dueño y quién lo juega") {
                            PcAuditLine("Dueño (cuenta)", pcAccountLabel(details.owner, details.authority?.ownerAccountId))
                            PcAuditLine("Lo juega ahora", pcAccountLabel(details.controller, details.authority?.controllerAccountId))
                            Text(
                                "Ser DM no te hace dueño del PJ: el dueño y quien lo juega se eligen aquí.",
                                style = MaterialTheme.typography.bodySmall,
                            )

                            if (!authorityMode) {
                                Button(
                                    enabled = !authorityLoading && details.sync.deletedAtEpochSeconds == null,
                                    onClick = {
                                        correctionMode = false
                                        authorityLoading = true
                                        statusMessage = "Cargando miembros activos de la campaña…"
                                        coroutineScope.launch {
                                            runCatching {
                                                hostedController.roster(activeCampaign.id)
                                            }.onSuccess { roster ->
                                                authorityMembers = eligiblePcAuthorityMembers(roster.members)
                                                selectedOwnerId = details.authority?.ownerAccountId
                                                selectedControllerId = details.authority?.controllerAccountId
                                                authorityMode = true
                                                statusMessage = null
                                            }.onFailure { error ->
                                                statusMessage = desktopHostedErrorMessage(error)
                                            }
                                            authorityLoading = false
                                        }
                                    },
                                ) {
                                    Text(if (authorityLoading) "Cargando…" else "Cambiar dueño o quién lo juega")
                                }
                            } else {
                                PcAuthoritySelector(
                                    label = "Dueño (cuenta)",
                                    members = authorityMembers,
                                    selectedId = selectedOwnerId,
                                    onSelect = { selectedOwnerId = it },
                                )
                                PcAuthoritySelector(
                                    label = "Lo juega ahora",
                                    members = authorityMembers,
                                    selectedId = selectedControllerId,
                                    onSelect = { selectedControllerId = it },
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        enabled = !authorityLoading,
                                        onClick = {
                                            authorityLoading = true
                                            statusMessage = "Guardando autoridad del PC…"
                                            coroutineScope.launch {
                                                runCatching {
                                                    hostedController.setPcAuthority(
                                                        campaignId = activeCampaign.id,
                                                        pcId = details.character.id,
                                                        ownerAccountId = selectedOwnerId,
                                                        controllerAccountId = selectedControllerId,
                                                    )
                                                }.onSuccess { result ->
                                                    val action = if (result.applied) "actualizada" else "sin cambios"
                                                    statusMessage = if (result.applied) "Dueño y jugador guardados." else "No había cambios."
                                                    onQaEvent(
                                                        "PC Manager: autoridad " + result.authority.characterId + " " + action,
                                                    )
                                                    authorityMode = false
                                                    authorityMembers = emptyList()
                                                    refresh(details.character.id)
                                                }.onFailure { error ->
                                                    statusMessage = desktopHostedErrorMessage(error)
                                                }
                                                authorityLoading = false
                                            }
                                        },
                                    ) {
                                        Text(if (authorityLoading) "Guardando…" else "Guardar")
                                    }
                                    TextButton(
                                        enabled = !authorityLoading,
                                        onClick = {
                                            authorityMode = false
                                            authorityMembers = emptyList()
                                            selectedOwnerId = null
                                            selectedControllerId = null
                                            statusMessage = null
                                        },
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                            }
                        }
                    }

                    item {
                        PcSheetPdfExportPanel(
                            busy = exportBusy,
                            v11ProofVariant = v11ProofVariant,
                            onV11VariantChange = { v11ProofVariant = it },
                            onSaveV11Proof = ::saveV11ProofPdf,
                            onShareV11Proof = ::shareV11ProofPdf,
                        )
                    }

                    if (correctionMode && correctionDraft != null) {
                        item {
                            PcCorrectionEditor(
                                draft = requireNotNull(correctionDraft),
                                reason = correctionReason,
                                onDraftChange = { correctionDraft = it },
                                onReasonChange = { correctionReason = it },
                                onApply = {
                                    val dm = dmAccountId
                                    if (dm == null) {
                                        statusMessage = "No existe una cuenta DM activa única para esta campaña."
                                    } else {
                                        runCatching {
                                            controller.correctAsDm(
                                                characterId = details.character.id,
                                                dmAccountId = dm,
                                                patch = requireNotNull(correctionDraft).toPatch(),
                                                reason = correctionReason,
                                            )
                                        }.onSuccess { result ->
                                            statusMessage = "Corrección guardada; llegará a la tablet al sincronizar."
                                            onQaEvent(
                                                "PC Manager: corrección DM " + result.checkpoint.id +
                                                    " sobre " + details.character.id,
                                            )
                                            correctionMode = false
                                            correctionReason = ""
                                            refresh(details.character.id)
                                        }.onFailure { error ->
                                            statusMessage = error.message ?: "No se pudo aplicar la corrección DM."
                                        }
                                    }
                                },
                            )
                        }
                    }

                    item { PcCoreInspection(details.character) }
                    item { PcCollectionsInspection(details.character) }
                    // Refactor F4 (point 6): sync state, history and internal state only for diagnosis, closed by default.
                    item {
                        TextButton(onClick = { showTechnical = !showTechnical }) {
                            Text((if (showTechnical) "▾ " else "▸ ") + "Datos técnicos (para diagnóstico)")
                        }
                    }
                    if (showTechnical) {
                        item {
                            PcAuditSection("Frescura de datos y sincronización") {
                                PcAuditLine("Datos locales", "actualizados " + formatPcEpoch(details.character.updatedAtEpochSeconds))
                                PcAuditLine("Revisión sync local", details.sync.revision.value.toString())
                                PcAuditLine(
                                    "Baseline alojado conocido",
                                    details.sync.baselineRevision?.value?.toString() ?: "sin baseline local",
                                )
                                PcAuditLine(
                                    "Outbox PC",
                                    when (details.sync.pendingRetryState) {
                                        HostedRetryState.READY -> "pendiente de envío"
                                        HostedRetryState.BLOCKED -> "bloqueado; requiere resolución"
                                        null -> "sin mutación pendiente"
                                    },
                                )
                                details.sync.pendingAttemptCount?.let { PcAuditLine("Intentos de outbox", it.toString()) }
                                details.sync.pendingErrorCode?.let { PcAuditLine("Último error sync", it) }
                                details.sync.deletedAtEpochSeconds?.let { PcAuditLine("Tombstone local", formatPcEpoch(it)) }
                            }
                        }

                        item {
                            PcAuditSection("Historial / reconciliación") {
                                val checkpoints = details.closure.reconciliationCheckpoints
                                    .sortedByDescending { it.createdAtEpochSeconds }
                                if (checkpoints.isEmpty()) {
                                    Text("Sin checkpoints de reconciliación registrados.", style = MaterialTheme.typography.bodySmall)
                                } else {
                                    checkpoints.forEach { checkpoint ->
                                        Text(
                                            checkpoint.label?.takeIf { it.isNotBlank() } ?: "Checkpoint",
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(formatPcEpoch(checkpoint.createdAtEpochSeconds), style = MaterialTheme.typography.bodySmall)
                                        checkpoint.notes?.takeIf { it.isNotBlank() }?.let {
                                            Text(it, style = MaterialTheme.typography.bodyMedium)
                                        }
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                        item { PcClosureInspection(details.closure) }
                        item { PcSuccessorInspection(details.successor) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PcAuthoritySelector(
    label: String,
    members: List<HostedCampaignMember>,
    selectedId: Uuid?,
    onSelect: (Uuid?) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (selectedId == null) {
                Button(onClick = { onSelect(null) }) { Text("Sin asignar") }
            } else {
                TextButton(onClick = { onSelect(null) }) { Text("Sin asignar") }
            }
            members.forEach { member ->
                val memberLabel = hostedMemberLabel(member)
                if (selectedId == member.userId) {
                    Button(onClick = { onSelect(member.userId) }) { Text(memberLabel) }
                } else {
                    TextButton(onClick = { onSelect(member.userId) }) { Text(memberLabel) }
                }
            }
        }
        if (members.isEmpty()) {
            Text("No hay miembros activos elegibles.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

/** «Jugador o jugadora» of a PC (redesign A13), saved with «Guardar». */
@androidx.compose.runtime.Composable
private fun PcPlayerField(current: String?, key: String, onSave: (String) -> Unit) {
    var value by remember(key) { mutableStateOf(current.orEmpty()) }
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text("Jugador o jugadora") },
            singleLine = true,
            modifier = Modifier.width(280.dp),
        )
        if (value.trim() != current.orEmpty()) {
            androidx.compose.material3.Button(onClick = { onSave(value) }) { Text("Guardar") }
        }
    }
}
