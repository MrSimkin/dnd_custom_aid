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
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContent
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipant
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterParticipantReadiness
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterPayload
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlin.uuid.Uuid

internal data class DesktopEncounterFilters(
    val query: String = "",
    val tag: String = "",
)

internal data class DesktopEncounterParticipantSource(
    val id: Uuid,
    val family: ReusableContentFamily,
    val displayName: String,
)

internal fun filterDesktopEncounters(
    encounters: List<EncounterContent>,
    filters: DesktopEncounterFilters,
): List<EncounterContent> {
    val query = filters.query.trim().lowercase()
    val tag = filters.tag.trim().lowercase()

    return encounters
        .distinctBy { it.item.identity.id }
        .filter { content ->
            tag.isEmpty() || content.payload.tags.any { it.lowercase().contains(tag) }
        }
        .filter { content ->
            query.isEmpty() || buildList {
                add(content.item.displayName)
                add(content.payload.summary)
                add(content.payload.environment)
                add(content.payload.context)
                add(content.payload.dmGuidance)
                add(content.payload.notes)
                addAll(content.payload.tags)
                content.payload.participants.forEach { participant ->
                    add(participant.label)
                    add(participant.condition)
                    add(participant.overrides)
                    add(participant.notes)
                    add(participant.readiness.name)
                }
            }.any { it.lowercase().contains(query) }
        }
        .sortedWith(compareBy({ encounterScopeSortKey(it) }, { it.item.displayName.lowercase() }))
}

class DesktopEncounterManagerController(
    database: AppDatabase,
    private val nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    private val campaignRepository = CampaignRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val reusableContent = ReusableContentRepository(database)
    private val encounters = EncounterContentRepository(database, reusableContent)

    fun personalOwnerAccountId(): Uuid? {
        val activeCampaignId = campaignRepository.activeCampaign()?.id
        val campaignIds = buildList {
            activeCampaignId?.let(::add)
            campaignRepository.listCampaigns().forEach { campaign ->
                if (campaign.id != activeCampaignId) add(campaign.id)
            }
        }

        return campaignIds
            .flatMap(spine::memberships)
            .asSequence()
            .filter { membership ->
                membership.role == CampaignRole.DM &&
                    membership.status == CampaignMembershipStatus.ACTIVE
            }
            .map { it.accountId }
            .distinct()
            .toList()
            .singleOrNull()
    }

    fun personalEncounters(ownerAccountId: Uuid): List<EncounterContent> =
        reusableContent.listPersonal(ownerAccountId, ReusableContentFamily.ENCOUNTER)
            .mapNotNull { encounters.encounter(it.identity.id) }

    fun campaignEncounters(campaignId: Uuid): List<EncounterContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.ENCOUNTER)
            .mapNotNull { encounters.encounter(it.identity.id) }

    fun encounter(id: Uuid): EncounterContent? = encounters.encounter(id)

    fun participantSources(scope: ContentScope): List<DesktopEncounterParticipantSource> {
        val items = when (scope) {
            is ContentScope.Personal ->
                reusableContent.listPersonal(scope.ownerAccountId, ReusableContentFamily.CREATURE) +
                    reusableContent.listPersonal(scope.ownerAccountId, ReusableContentFamily.NPC)
            is ContentScope.Campaign ->
                reusableContent.listCampaign(scope.campaignId, ReusableContentFamily.CREATURE) +
                    reusableContent.listCampaign(scope.campaignId, ReusableContentFamily.NPC)
            is ContentScope.Official, ContentScope.System -> emptyList()
        }
        return items
            .map { item ->
                DesktopEncounterParticipantSource(
                    id = item.identity.id,
                    family = item.family,
                    displayName = item.displayName,
                )
            }
            .sortedWith(compareBy({ encounterFamilySortKey(it.family) }, { it.displayName.lowercase() }))
    }

    fun createPersonal(ownerAccountId: Uuid, displayName: String): EncounterContent =
        encounters.createPersonal(
            ownerAccountId = ownerAccountId,
            rawDisplayName = displayName,
            payload = EncounterPayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun createCampaign(campaignId: Uuid, displayName: String): EncounterContent =
        encounters.createCampaign(
            campaignId = campaignId,
            rawDisplayName = displayName,
            payload = EncounterPayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun update(
        id: Uuid,
        expectedRevision: Revision,
        displayName: String,
        payload: EncounterPayload,
    ): RevisionDecision = encounters.update(
        id = id,
        expectedRevision = expectedRevision,
        rawDisplayName = displayName,
        payload = payload,
        updatedAtEpochSeconds = nowEpochSeconds(),
    )

    fun copyPersonalToCampaign(sourceId: Uuid, campaignId: Uuid): EncounterContent =
        encounters.copyPersonalToCampaign(
            sourceId = sourceId,
            campaignId = campaignId,
            copiedAtEpochSeconds = nowEpochSeconds(),
        )
}

@Composable
internal fun DesktopEncounterManagerScreen(
    controller: DesktopEncounterManagerController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var refreshVersion by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    var tagFilter by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<Uuid?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val ownerAccountId = remember(refreshVersion, activeCampaign?.id) {
        controller.personalOwnerAccountId()
    }
    val personalEncounters = remember(refreshVersion, ownerAccountId) {
        ownerAccountId?.let(controller::personalEncounters).orEmpty()
    }
    val campaignEncounters = remember(refreshVersion, activeCampaign?.id) {
        activeCampaign?.id?.let(controller::campaignEncounters).orEmpty()
    }
    val selected = remember(refreshVersion, selectedId) {
        selectedId?.let(controller::encounter)
    }
    val participantSources = remember(refreshVersion, selected?.item?.identity?.id, selected?.item?.identity?.revision?.value) {
        selected?.let { controller.participantSources(it.item.identity.scope) }.orEmpty()
    }
    var draft by remember(selected?.item?.identity?.id, selected?.item?.identity?.revision?.value) {
        mutableStateOf(selected?.let(EncounterDraft::from))
    }

    fun refresh(selectId: Uuid? = selectedId) {
        selectedId = selectId
        refreshVersion += 1
    }

    val visibleEncounters = filterDesktopEncounters(
        encounters = personalEncounters + campaignEncounters,
        filters = DesktopEncounterFilters(query = query, tag = tagFilter),
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Gestor de Encuentros", style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
            Text(
                "Prepara encuentros reutilizables sin convertirlos en combate en vivo. " +
                    "Las anulaciones pertenecen al encuentro y no modifican las criaturas o PNJ de origen.",
                style = MaterialTheme.typography.body1,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = tagFilter,
                onValueChange = { tagFilter = it },
                label = { Text("Etiqueta") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Nuevo encuentro") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Button(
                enabled = newName.isNotBlank() && ownerAccountId != null,
                onClick = {
                    runCatching { controller.createPersonal(requireNotNull(ownerAccountId), newName) }
                        .onSuccess { created ->
                            newName = ""
                            statusMessage = "Encuentro Personal creado."
                            onQaEvent("Encounter Manager: encuentro Personal creado ${created.item.identity.id}")
                            refresh(created.item.identity.id)
                        }
                        .onFailure { statusMessage = it.message ?: "No se pudo crear el encuentro Personal." }
                },
            ) { Text("Crear Personal") }
            Button(
                enabled = newName.isNotBlank() && activeCampaign != null,
                onClick = {
                    runCatching { controller.createCampaign(requireNotNull(activeCampaign).id, newName) }
                        .onSuccess { created ->
                            newName = ""
                            statusMessage = "Encuentro creado en ${activeCampaign?.name}."
                            onQaEvent("Encounter Manager: encuentro de campaña creado ${created.item.identity.id}")
                            refresh(created.item.identity.id)
                        }
                        .onFailure { statusMessage = it.message ?: "No se pudo crear el encuentro de campaña." }
                },
            ) { Text("Crear en campaña") }
        }

        if (ownerAccountId == null) {
            Text(
                "Biblioteca Personal no disponible: Desktop no puede identificar de forma unívoca una cuenta DM local. " +
                    "Los encuentros de campaña siguen disponibles.",
                style = MaterialTheme.typography.caption,
            )
        }
        statusMessage?.let { Text(it, style = MaterialTheme.typography.caption) }

        Divider()

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            EncounterList(
                encounters = visibleEncounters,
                selectedId = selectedId,
                onSelect = {
                    selectedId = it
                    statusMessage = null
                },
                modifier = Modifier.width(330.dp).fillMaxHeight(),
            )
            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))
            if (selected == null || draft == null) {
                Column(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Selecciona un encuentro para abrirlo.", style = MaterialTheme.typography.h6)
                    Text("Personal: ${personalEncounters.size} • Campaña activa: ${campaignEncounters.size}")
                }
            } else {
                EncounterEditor(
                    content = selected,
                    draft = requireNotNull(draft),
                    participantSources = participantSources,
                    activeCampaign = activeCampaign,
                    onDraftChange = { draft = it },
                    onSave = {
                        val currentDraft = requireNotNull(draft)
                        runCatching {
                            controller.update(
                                id = selected.item.identity.id,
                                expectedRevision = selected.item.identity.revision,
                                displayName = currentDraft.displayName,
                                payload = currentDraft.toPayload(),
                            )
                        }.onSuccess { decision ->
                            when (decision) {
                                is RevisionDecision.Accepted -> {
                                    statusMessage = "Cambios guardados (revisión ${decision.nextRevision.value})."
                                    onQaEvent("Encounter Manager: encuentro actualizado ${selected.item.identity.id}")
                                    refresh(selected.item.identity.id)
                                }
                                is RevisionDecision.Stale -> {
                                    statusMessage = "El encuentro cambió desde que fue abierto. Se recargó la versión actual."
                                    refresh(selected.item.identity.id)
                                }
                                is RevisionDecision.Deleted -> {
                                    statusMessage = "El encuentro fue eliminado y no puede ser sobrescrito."
                                    refresh(null)
                                }
                            }
                        }.onFailure { statusMessage = it.message ?: "No se pudo guardar el encuentro." }
                    },
                    onCopyToCampaign = {
                        val campaign = activeCampaign
                        if (campaign == null) {
                            statusMessage = "Selecciona una campaña activa antes de copiar."
                        } else {
                            runCatching { controller.copyPersonalToCampaign(selected.item.identity.id, campaign.id) }
                                .onSuccess { copied ->
                                    statusMessage = "Copia independiente creada en ${campaign.name}; las dependencias Personal se copiaron y reconectaron."
                                    onQaEvent("Encounter Manager: encuentro copiado a campaña ${copied.item.identity.id}")
                                    refresh(copied.item.identity.id)
                                }
                                .onFailure { statusMessage = it.message ?: "No se pudo copiar el encuentro." }
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun EncounterList(
    encounters: List<EncounterContent>,
    selectedId: Uuid?,
    onSelect: (Uuid) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item {
            Text("Encuentros (${encounters.size})", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
        }
        if (encounters.isEmpty()) {
            item { Text("No hay encuentros que coincidan con el filtro.", style = MaterialTheme.typography.caption) }
        }
        items(encounters, key = { it.item.identity.id.toString() }) { content ->
            val selected = content.item.identity.id == selectedId
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(content.item.identity.id) },
                elevation = if (selected) 6.dp else 1.dp,
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(content.item.displayName, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    Text(
                        "${encounterScopeLabel(content.item.identity.scope)} • ${content.payload.participants.size} participantes/grupos",
                        style = MaterialTheme.typography.caption,
                    )
                    if (content.payload.environment.isNotBlank()) {
                        Text(content.payload.environment, style = MaterialTheme.typography.caption)
                    }
                }
            }
        }
    }
}

@Composable
private fun EncounterEditor(
    content: EncounterContent,
    draft: EncounterDraft,
    participantSources: List<DesktopEncounterParticipantSource>,
    activeCampaign: Campaign?,
    onDraftChange: (EncounterDraft) -> Unit,
    onSave: () -> Unit,
    onCopyToCampaign: () -> Unit,
    modifier: Modifier,
) {
    val sourceLabels = participantSources.associate { it.id to it.displayName }
    val validParticipants = draft.participants.all { it.sourceContentId != null || it.label.isNotBlank() }

    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Editor de Encuentro", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
                    Text(
                        "${encounterScopeLabel(content.item.identity.scope)} • revisión ${content.item.identity.revision.value}",
                        style = MaterialTheme.typography.caption,
                    )
                    content.item.identity.provenance?.let { provenance ->
                        Text(
                            "Copia de ${provenance.sourceObjectId.toString().take(8)}… • independiente desde la copia",
                            style = MaterialTheme.typography.caption,
                        )
                    }
                }
                if (content.item.identity.scope is ContentScope.Personal && activeCampaign != null) {
                    Button(onClick = onCopyToCampaign) { Text("Copiar a campaña") }
                }
                Button(
                    onClick = onSave,
                    enabled = draft.displayName.isNotBlank() && validParticipants,
                ) { Text("Guardar") }
            }
        }

        item { EncounterField("Nombre", draft.displayName, true) { onDraftChange(draft.copy(displayName = it)) } }
        item { EncounterField("Resumen", draft.summary, false, 3) { onDraftChange(draft.copy(summary = it)) } }
        item { EncounterField("Entorno", draft.environment, false, 3) { onDraftChange(draft.copy(environment = it)) } }
        item { EncounterField("Contexto", draft.context, false, 4) { onDraftChange(draft.copy(context = it)) } }
        item { EncounterField("Guía DM", draft.dmGuidance, false, 4) { onDraftChange(draft.copy(dmGuidance = it)) } }
        item { EncounterField("Etiquetas (coma o línea)", draft.tags, false, 2) { onDraftChange(draft.copy(tags = it)) } }
        item { EncounterField("Notas", draft.notes, false, 4) { onDraftChange(draft.copy(notes = it)) } }

        item {
            Divider()
            Text("Participantes / grupos", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
            Text(
                "Añade grupos libres o referencias a criaturas/PNJ del mismo ámbito. Las anulaciones siguientes sólo pertenecen a este encuentro.",
                style = MaterialTheme.typography.caption,
            )
            Button(
                onClick = {
                    onDraftChange(
                        draft.copy(
                            participants = draft.participants + EncounterParticipantDraft(label = "Nuevo participante"),
                        ),
                    )
                },
            ) { Text("Añadir grupo libre") }
        }

        if (participantSources.isNotEmpty()) {
            item {
                Text("Añadir desde biblioteca", style = MaterialTheme.typography.subtitle2, fontWeight = FontWeight.Bold)
            }
            items(participantSources, key = { "source-${it.id}" }) { source ->
                TextButton(
                    onClick = {
                        onDraftChange(
                            draft.copy(
                                participants = draft.participants + EncounterParticipantDraft(
                                    sourceContentId = source.id,
                                    label = source.displayName,
                                ),
                            ),
                        )
                    },
                ) {
                    Text("+ ${encounterFamilyLabel(source.family)}: ${source.displayName}")
                }
            }
        }

        itemsIndexed(draft.participants) { index, participant ->
            EncounterParticipantEditor(
                index = index,
                participant = participant,
                sourceLabel = participant.sourceContentId?.let { sourceLabels[it] },
                onChange = { changed ->
                    onDraftChange(
                        draft.copy(
                            participants = draft.participants.mapIndexed { candidateIndex, candidate ->
                                if (candidateIndex == index) changed else candidate
                            },
                        ),
                    )
                },
                onRemove = {
                    onDraftChange(
                        draft.copy(participants = draft.participants.filterIndexed { candidateIndex, _ -> candidateIndex != index }),
                    )
                },
            )
        }
    }
}

@Composable
private fun EncounterParticipantEditor(
    index: Int,
    participant: EncounterParticipantDraft,
    sourceLabel: String?,
    onChange: (EncounterParticipantDraft) -> Unit,
    onRemove: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = 1.dp) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Participante ${index + 1}", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                TextButton(onClick = onRemove) { Text("Eliminar") }
            }

            participant.sourceContentId?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Fuente: ${sourceLabel ?: "Referencia no disponible"}", modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            onChange(
                                participant.copy(
                                    sourceContentId = null,
                                    label = participant.label.ifBlank { sourceLabel ?: "Participante" },
                                ),
                            )
                        },
                    ) { Text("Quitar vínculo") }
                }
            }

            EncounterField("Etiqueta", participant.label, true) { onChange(participant.copy(label = it)) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Cantidad: ${participant.quantity}", modifier = Modifier.weight(1f))
                Button(
                    enabled = participant.quantity > 1,
                    onClick = { onChange(participant.copy(quantity = participant.quantity - 1)) },
                ) { Text("−") }
                Button(onClick = { onChange(participant.copy(quantity = participant.quantity + 1)) }) { Text("+") }
            }

            Text("Preparación", style = MaterialTheme.typography.subtitle2, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EncounterParticipantReadiness.entries.forEach { readiness ->
                    if (participant.readiness == readiness) {
                        Button(onClick = { onChange(participant.copy(readiness = readiness)) }) {
                            Text(encounterReadinessLabel(readiness))
                        }
                    } else {
                        TextButton(onClick = { onChange(participant.copy(readiness = readiness)) }) {
                            Text(encounterReadinessLabel(readiness))
                        }
                    }
                }
            }

            EncounterField("Condición", participant.condition, false, 2) { onChange(participant.copy(condition = it)) }
            EncounterField("Anulaciones para este encuentro", participant.overrides, false, 3) { onChange(participant.copy(overrides = it)) }
            EncounterField("Notas", participant.notes, false, 3) { onChange(participant.copy(notes = it)) }
        }
    }
}

@Composable
private fun EncounterField(
    label: String,
    value: String,
    singleLine: Boolean,
    lines: Int = 1,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = if (singleLine) 1 else lines,
    )
}

private data class EncounterDraft(
    val displayName: String,
    val summary: String,
    val environment: String,
    val context: String,
    val dmGuidance: String,
    val participants: List<EncounterParticipantDraft>,
    val tags: String,
    val notes: String,
) {
    fun toPayload(): EncounterPayload = EncounterPayload(
        summary = summary,
        environment = environment,
        context = context,
        dmGuidance = dmGuidance,
        participants = participants.map(EncounterParticipantDraft::toParticipant),
        tags = tags.split(',', '\n').map(String::trim).filter(String::isNotEmpty),
        notes = notes,
    )

    companion object {
        fun from(content: EncounterContent): EncounterDraft = EncounterDraft(
            displayName = content.item.displayName,
            summary = content.payload.summary,
            environment = content.payload.environment,
            context = content.payload.context,
            dmGuidance = content.payload.dmGuidance,
            participants = content.payload.participants.map(EncounterParticipantDraft::from),
            tags = content.payload.tags.joinToString(", "),
            notes = content.payload.notes,
        )
    }
}

private data class EncounterParticipantDraft(
    val sourceContentId: Uuid? = null,
    val label: String = "",
    val quantity: Int = 1,
    val readiness: EncounterParticipantReadiness = EncounterParticipantReadiness.EXPECTED,
    val condition: String = "",
    val overrides: String = "",
    val notes: String = "",
) {
    fun toParticipant(): EncounterParticipant = EncounterParticipant(
        sourceContentId = sourceContentId,
        label = label.trim(),
        quantity = quantity,
        readiness = readiness,
        condition = condition,
        overrides = overrides,
        notes = notes,
    )

    companion object {
        fun from(participant: EncounterParticipant): EncounterParticipantDraft = EncounterParticipantDraft(
            sourceContentId = participant.sourceContentId,
            label = participant.label,
            quantity = participant.quantity,
            readiness = participant.readiness,
            condition = participant.condition,
            overrides = participant.overrides,
            notes = participant.notes,
        )
    }
}

private fun encounterScopeSortKey(content: EncounterContent): Int = when (content.item.identity.scope) {
    is ContentScope.Personal -> 0
    is ContentScope.Campaign -> 1
    else -> 2
}

private fun encounterScopeLabel(scope: ContentScope): String = when (scope) {
    is ContentScope.Personal -> "Personal"
    is ContentScope.Campaign -> "Campaña"
    is ContentScope.Official -> "Oficial / SRD"
    ContentScope.System -> "Sistema"
}

private fun encounterFamilySortKey(family: ReusableContentFamily): Int = when (family) {
    ReusableContentFamily.CREATURE -> 0
    ReusableContentFamily.NPC -> 1
    else -> 2
}

private fun encounterFamilyLabel(family: ReusableContentFamily): String = when (family) {
    ReusableContentFamily.CREATURE -> "Criatura"
    ReusableContentFamily.NPC -> "PNJ"
    else -> family.name
}

private fun encounterReadinessLabel(readiness: EncounterParticipantReadiness): String = when (readiness) {
    EncounterParticipantReadiness.EXPECTED -> "Esperado"
    EncounterParticipantReadiness.RESERVE -> "Reserva"
    EncounterParticipantReadiness.CONDITIONAL -> "Condicional"
}
