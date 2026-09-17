package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorState
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
    private val closure = CharacterClosureRepository(database)
    private val successor = CharacterSuccessorRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val baselines = HostedPcSyncBaselineRepository(database)
    private val outbox = HostedOutboxRepository(database)
    private val corrections = HostedPcDmCorrectionService(database)

    fun campaignPcs(campaignId: Uuid): List<CharacterSheet> = characters.listCharacters(campaignId)

    fun activeDmAccountId(campaignId: Uuid): Uuid? =
        spine.memberships(campaignId)
            .asSequence()
            .filter { it.role == CampaignRole.DM && it.status == CampaignMembershipStatus.ACTIVE }
            .map { it.accountId }
            .distinct()
            .toList()
            .singleOrNull()

    fun details(characterId: Uuid): DesktopPcDetails? {
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
fun DesktopPcManagerScreen(
    controller: DesktopPcManagerController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var refreshVersion by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<Uuid?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var correctionMode by remember { mutableStateOf(false) }
    var correctionReason by remember { mutableStateOf("") }

    if (activeCampaign == null) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("PC Manager / Auditoría", style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
            Text("Selecciona una campaña activa para inspeccionar sus personajes jugadores.")
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

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("PC Manager / Auditoría", style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
            Text(
                "Inspección DM de los mismos registros canónicos usados por Player. " +
                    "Propiedad, control, datos locales y sincronización permanecen separados.",
            )
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar por nombre, estado, clase, rasgo o conjuro") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        statusMessage?.let { Text(it, style = MaterialTheme.typography.caption) }
        Divider()

        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LazyColumn(
                modifier = Modifier.width(300.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                item {
                    Text(
                        "Personajes de " + activeCampaign.name + " (" + visiblePcs.size + ")",
                        style = MaterialTheme.typography.subtitle1,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (visiblePcs.isEmpty()) item { Text("No hay personajes que coincidan con el filtro.") }
                items(visiblePcs, key = { it.id.toString() }) { character ->
                    val selected = character.id == selectedId
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectedId = character.id
                            correctionMode = false
                            correctionReason = ""
                            statusMessage = null
                        },
                        elevation = if (selected) 6.dp else 1.dp,
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(character.name, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                            Text(
                                pcStatusLabel(character.status) + " • nivel " + character.totalLevel,
                                style = MaterialTheme.typography.caption,
                            )
                            Text(
                                "Actualizado localmente: " + formatPcEpoch(character.updatedAtEpochSeconds),
                                style = MaterialTheme.typography.caption,
                            )
                        }
                    }
                }
            }

            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))

            if (details == null) {
                Column(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Selecciona un personaje para auditarlo.", style = MaterialTheme.typography.h6)
                    Text("PC locales en campaña: " + pcs.size)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(details.character.name, style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
                                Text(
                                    pcStatusLabel(details.character.status) + " • nivel " + details.character.totalLevel +
                                        " • PC " + details.character.id.toString().take(8) + "…",
                                    style = MaterialTheme.typography.caption,
                                )
                            }
                            if (!correctionMode) {
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
                        PcAuditSection("Autoridad del PC") {
                            PcAuditLine("Propietario", pcAccountLabel(details.owner, details.authority?.ownerAccountId))
                            PcAuditLine("Controlador", pcAccountLabel(details.controller, details.authority?.controllerAccountId))
                            PcAuditLine(
                                "Regla",
                                "El rol DM no otorga propiedad ni control. Una corrección DM no modifica estos campos.",
                            )
                        }
                    }

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
                                            statusMessage = "Corrección DM registrada y snapshot encolado: " +
                                                result.mutation.mutationId.toString().take(8) + "…"
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

                    item {
                        PcAuditSection("Historial / reconciliación") {
                            val checkpoints = details.closure.reconciliationCheckpoints
                                .sortedByDescending { it.createdAtEpochSeconds }
                            if (checkpoints.isEmpty()) {
                                Text("Sin checkpoints de reconciliación registrados.", style = MaterialTheme.typography.caption)
                            } else {
                                checkpoints.forEach { checkpoint ->
                                    Text(
                                        checkpoint.label?.takeIf { it.isNotBlank() } ?: "Checkpoint",
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(formatPcEpoch(checkpoint.createdAtEpochSeconds), style = MaterialTheme.typography.caption)
                                    checkpoint.notes?.takeIf { it.isNotBlank() }?.let {
                                        Text(it, style = MaterialTheme.typography.body2)
                                    }
                                    Divider()
                                }
                            }
                        }
                    }

                    item { PcCoreInspection(details.character) }
                    item { PcCollectionsInspection(details.character) }
                    item { PcClosureInspection(details.closure) }
                    item { PcSuccessorInspection(details.successor) }
                }
            }
        }
    }
}

@Composable
private fun PcCorrectionEditor(
    draft: PcCorrectionDraft,
    reason: String,
    onDraftChange: (PcCorrectionDraft) -> Unit,
    onReasonChange: (String) -> Unit,
    onApply: () -> Unit,
) {
    PcAuditSection("Corrección DM — núcleo del PC") {
        Text(
            "Acción explícita y auditada: modifica campos nucleares, agrega un checkpoint de reconciliación " +
                "y encola el snapshot. Las colecciones complejas permanecen sólo en inspección en este corte.",
            style = MaterialTheme.typography.body2,
        )
        PcCorrectionField("Nombre", draft.name) { onDraftChange(draft.copy(name = it)) }
        Text("Estado", style = MaterialTheme.typography.subtitle2, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CharacterStatus.entries.forEach { status ->
                if (draft.status == status) {
                    Button(onClick = { onDraftChange(draft.copy(status = status)) }) { Text(pcStatusLabel(status)) }
                } else {
                    TextButton(onClick = { onDraftChange(draft.copy(status = status)) }) { Text(pcStatusLabel(status)) }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CompactField("FUE", draft.strength) { onDraftChange(draft.copy(strength = it)) }
            CompactField("DES", draft.dexterity) { onDraftChange(draft.copy(dexterity = it)) }
            CompactField("CON", draft.constitution) { onDraftChange(draft.copy(constitution = it)) }
            CompactField("INT", draft.intelligence) { onDraftChange(draft.copy(intelligence = it)) }
            CompactField("SAB", draft.wisdom) { onDraftChange(draft.copy(wisdom = it)) }
            CompactField("CAR", draft.charisma) { onDraftChange(draft.copy(charisma = it)) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CompactField("CA", draft.armorClass) { onDraftChange(draft.copy(armorClass = it)) }
            CompactField("PG máx.", draft.maxHp) { onDraftChange(draft.copy(maxHp = it)) }
            CompactField("PG actual", draft.currentHp) { onDraftChange(draft.copy(currentHp = it)) }
            CompactField("PG temp.", draft.tempHp) { onDraftChange(draft.copy(tempHp = it)) }
            CompactField("Velocidad", draft.speed) { onDraftChange(draft.copy(speed = it)) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CompactField("Aj. iniciativa", draft.initiativeAdjustment) {
                onDraftChange(draft.copy(initiativeAdjustment = it))
            }
            CompactField("Aj. competencia", draft.proficiencyBonusAdjustment) {
                onDraftChange(draft.copy(proficiencyBonusAdjustment = it))
            }
            CompactField("Aj. Percepción", draft.passivePerceptionAdjustment) {
                onDraftChange(draft.copy(passivePerceptionAdjustment = it))
            }
        }
        if (draft.inspiration) {
            Button(onClick = { onDraftChange(draft.copy(inspiration = false)) }) { Text("Inspiración: sí") }
        } else {
            TextButton(onClick = { onDraftChange(draft.copy(inspiration = true)) }) { Text("Inspiración: no") }
        }
        PcCorrectionField("Notas generales", draft.generalNotes, false, 4) {
            onDraftChange(draft.copy(generalNotes = it))
        }
        PcCorrectionField("Motivo de la corrección (obligatorio)", reason, false, 3, onReasonChange)
        Button(
            enabled = draft.name.isNotBlank() && reason.isNotBlank(),
            onClick = onApply,
        ) { Text("Aplicar corrección DM y encolar sync") }
    }
}

@Composable
private fun PcCoreInspection(sheet: CharacterSheet) {
    PcAuditSection("Ficha canónica — núcleo") {
        PcAuditLine("Nombre", sheet.name)
        PcAuditLine("Estado", pcStatusLabel(sheet.status))
        PcAuditLine(
            "Atributos",
            "FUE " + sheet.strength + " · DES " + sheet.dexterity + " · CON " + sheet.constitution +
                " · INT " + sheet.intelligence + " · SAB " + sheet.wisdom + " · CAR " + sheet.charisma,
        )
        PcAuditLine(
            "Combate",
            "CA " + sheet.armorClass + " · PG " + sheet.currentHp + "/" + sheet.maxHp +
                " (+" + sheet.tempHp + " temp.) · Iniciativa " + signed(sheet.initiativeModifier) +
                " · Velocidad " + sheet.speed,
        )
        PcAuditLine(
            "Competencia",
            "base " + sheet.standardProficiencyBonus + " · ajuste " + signed(sheet.proficiencyBonusAdjustment) +
                " · final " + signed(sheet.finalProficiencyBonus),
        )
        PcAuditLine("Percepción Pasiva", sheet.passivePerception.toString())
        PcAuditLine("Inspiración", if (sheet.inspiration) "sí" else "no")
        PcAuditLine(
            "Salvaciones de muerte",
            sheet.deathSaveSuccesses.toString() + " éxitos / " + sheet.deathSaveFailures + " fallos",
        )
        PcAuditLine("Notas generales", sheet.generalNotes.ifBlank { "—" })
        PcAuditLine(
            "Background",
            listOf(sheet.background.name, sheet.background.race, sheet.background.religionFaith)
                .filter { it.isNotBlank() }.joinToString(" · ").ifBlank { "—" },
        )
        PcAuditLine(
            "Clases",
            sheet.classes.joinToString(" · ") {
                it.name + " " + it.level + (it.subclassName?.takeIf(String::isNotBlank)?.let { sub -> " / " + sub } ?: "")
            }.ifBlank { "—" },
        )
        PcAuditLine(
            "Tiradas de salvación",
            sheet.savingThrows.joinToString(" · ") {
                it.ability.name + ": " + signed(sheet.savingThrowTotal(it.ability)) +
                    (if (it.proficient) " P" else "")
            },
        )
        PcAuditLine(
            "Habilidades",
            sheet.skills.joinToString(" · ") {
                it.key.name + ": " + signed(sheet.skillTotal(it.key)) + " (" + it.training.name + ")"
            },
        )
    }
}

@Composable
private fun PcCollectionsInspection(sheet: CharacterSheet) {
    PcAuditSection("Ficha canónica — colecciones") {
        PcAuditLine("Acciones / ataques", listSummary(sheet.combatEntries) { it.name + " [" + it.type.name + "]" })
        PcAuditLine("Inventario", listSummary(sheet.inventoryItems) { it.name + " ×" + it.quantity })
        PcAuditLine("Monedas", listSummary(sheet.currencies) { it.name + ": " + it.amount })
        PcAuditLine("Rasgos", listSummary(sheet.traits) { it.name + " [" + it.type.name + "]" })
        PcAuditLine("Fuentes mágicas", listSummary(sheet.spellcastingSources) { it.name })
        PcAuditLine("Conjuros", listSummary(sheet.spells) { it.name + " (nivel " + it.level + ")" })
        PcAuditLine(
            "Slots",
            listSummary(sheet.spellSlots) { "N" + it.level + ": " + (it.totalSlots - it.spentSlots) + "/" + it.totalSlots },
        )
        PcAuditLine("Notas tituladas", listSummary(sheet.noteCards) { it.title })
        PcAuditLine("Competencias", listSummary(sheet.proficiencies) { it.type.name + ": " + it.name })
        PcAuditLine("Maestrías", listSummary(sheet.weaponMasteries) { it.weaponName + ": " + it.masteryName })
        PcAuditLine(
            "Recursos",
            listSummary(sheet.resources) {
                it.name + ": " + it.currentValue + (it.maxValue?.let { max -> "/" + max } ?: "")
            },
        )
        PcAuditLine("Opciones de clase", listSummary(sheet.classOptions) { it.kind.name + ": " + it.name })
        PcAuditLine("Formas", listSummary(sheet.forms) { it.name })
        PcAuditLine("Compañeros", listSummary(sheet.companions) { it.name + " (" + it.kind + ")" })
    }
}

@Composable
private fun PcClosureInspection(state: CharacterClosureState) {
    PcAuditSection("Estado extendido / cierre") {
        PcAuditLine("Agotamiento", state.exhaustionLevel.toString())
        PcAuditLine(
            "Concentración",
            state.concentration?.let { it.name + (it.notes?.let { note -> " · " + note } ?: "") } ?: "—",
        )
        PcAuditLine(
            "Progreso",
            state.progressMode.name + " · XP " + state.experiencePoints + " · " + state.milestoneProgress,
        )
        PcAuditLine("Condiciones", listSummary(state.conditions) { it.name })
        PcAuditLine("Defensas", listSummary(state.defenses) { it.type.name + ": " + it.name })
        PcAuditLine("Movimientos", listSummary(state.movements) { it.name + ": " + (it.speedFeet?.toString() ?: "—") })
        PcAuditLine("Sentidos", listSummary(state.senses) { it.name + ": " + (it.rangeFeet?.toString() ?: "—") })
        PcAuditLine(
            "Recuperación de recursos",
            listSummary(state.resourceRecovery) {
                it.resourceId.toString().take(8) + "… " + it.cadence.name + "/" + it.amountMode.name
            },
        )
        PcAuditLine(
            "Uso de inventario",
            listSummary(state.inventoryUsage) {
                it.itemId.toString().take(8) + "… " + it.kind.name + "/" + it.carryState.name
            },
        )
        PcAuditLine(
            "Habilidades personalizadas",
            listSummary(state.customSkills) { it.name + " (" + it.ability.name + "/" + it.training.name + ")" },
        )
        PcAuditLine(
            "Efectos temporales",
            listSummary(state.temporaryEffects) { it.name + if (it.active) " [activo]" else "" },
        )
        PcAuditLine("Overrides de módulo", listSummary(state.moduleOverrides) { it.module.name + ": " + it.mode.name })
        PcAuditLine(
            "Accesos rápidos",
            listSummary(state.quickAccess) { it.kind.name + ":" + it.targetId.toString().take(8) + "…" },
        )
        PcAuditLine("Retrato", state.portraitRef ?: "—")
        PcAuditLine("Token", state.tokenRef ?: "—")
    }
}

@Composable
private fun PcSuccessorInspection(state: CharacterSuccessorState) {
    PcAuditSection("Estado sucesor / identidad y extensiones") {
        PcAuditLine(
            "Atributos personalizados",
            listSummary(state.customAttributes) { it.name + " (" + it.abbreviation + ")=" + it.score },
        )
        PcAuditLine(
            "Habilidades custom configuradas",
            state.customSkillAbilities.size.toString(),
        )
        PcAuditLine("Perfiles de lanzamiento", state.spellcastingProfiles.size.toString())
        PcAuditLine("Perfiles de daño", state.combatDamage.size.toString())
        PcAuditLine(
            "Marcadores",
            listSummary(state.customMarkers) {
                it.name + ": " + it.currentValue + (it.maxValue?.let { max -> "/" + max } ?: "")
            },
        )
        PcAuditLine("Configuraciones de recursos", state.resourceConfigurations.size.toString())
        PcAuditLine("Especie / raza", state.speciesIdentity?.name ?: "—")
        PcAuditLine("Subraza", state.subraceIdentity?.name ?: "—")
        PcAuditLine("Background canónico", state.backgroundIdentity?.name ?: "—")
        PcAuditLine("Subclases con identidad", state.subclassIdentities.size.toString())
        PcAuditLine("Relaciones de procedencia de rasgos", state.traitProvenance.size.toString())
        PcAuditLine(
            "Imágenes de background",
            listSummary(state.backgroundImages) { it.slot.name + ": " + (it.originalName ?: it.mimeType) },
        )
        PcAuditLine(
            "Preferencias",
            "inspiración visible=" + state.preferences.inspirationVisible +
                "; pestañas=" + state.preferences.tabOrder.size +
                "; valores='" + state.preferences.valuablesText.take(80) + "'",
        )
    }
}

@Composable
private fun PcAuditSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = 1.dp) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun PcAuditLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Text(label, style = MaterialTheme.typography.caption, fontWeight = FontWeight.Bold)
        Text(value.ifBlank { "—" }, style = MaterialTheme.typography.body2)
    }
}

@Composable
private fun PcCorrectionField(
    label: String,
    value: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else minLines,
    )
}

@Composable
private fun CompactField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.width(112.dp),
    )
}

private data class PcCorrectionDraft(
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
    val inspiration: Boolean,
    val generalNotes: String,
) {
    fun toPatch(): PcDmCorrectionPatch {
        fun parse(label: String, raw: String): Int =
            requireNotNull(raw.trim().toIntOrNull()) { label + " debe ser un número entero." }

        require(name.trim().isNotEmpty()) { "El nombre del PC no puede quedar vacío." }
        return PcDmCorrectionPatch(
            name = name,
            status = status,
            strength = parse("FUE", strength),
            dexterity = parse("DES", dexterity),
            constitution = parse("CON", constitution),
            intelligence = parse("INT", intelligence),
            wisdom = parse("SAB", wisdom),
            charisma = parse("CAR", charisma),
            armorClass = parse("CA", armorClass),
            maxHp = parse("PG máximos", maxHp),
            currentHp = parse("PG actuales", currentHp),
            tempHp = parse("PG temporales", tempHp),
            initiativeAdjustment = parse("Ajuste de iniciativa", initiativeAdjustment),
            speed = parse("Velocidad", speed),
            proficiencyBonusAdjustment = parse("Ajuste de competencia", proficiencyBonusAdjustment),
            passivePerceptionAdjustment = parse("Ajuste de Percepción Pasiva", passivePerceptionAdjustment),
            inspiration = inspiration,
            generalNotes = generalNotes,
        )
    }

    companion object {
        fun from(sheet: CharacterSheet): PcCorrectionDraft = PcCorrectionDraft(
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
            inspiration = sheet.inspiration,
            generalNotes = sheet.generalNotes,
        )
    }
}

private fun pcAccountLabel(account: AccountIdentity?, id: Uuid?): String =
    account?.displayName?.trim()?.takeIf { it.isNotEmpty() }
        ?: id?.let { "Cuenta " + it.toString().take(8) + "…" }
        ?: "Sin asignar"

private fun pcStatusLabel(status: CharacterStatus): String = when (status) {
    CharacterStatus.ACTIVE -> "Activo"
    CharacterStatus.INACTIVE -> "Inactivo"
    CharacterStatus.RETIRED -> "Retirado"
    CharacterStatus.DEAD -> "Muerto"
}

private fun formatPcEpoch(epochSeconds: Long): String =
    runCatching {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .format(Instant.ofEpochSecond(epochSeconds).atZone(ZoneId.systemDefault()))
    }.getOrElse { epochSeconds.toString() }

private fun signed(value: Int): String = if (value >= 0) "+" + value else value.toString()

private fun <T> listSummary(items: List<T>, render: (T) -> String): String =
    items.joinToString(" · ", transform = render).ifBlank { "—" }
