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
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
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
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContent
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZonePayload
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlin.uuid.Uuid

internal data class DesktopZoneFilters(
    val query: String = "",
    val area: String = "",
    val tag: String = "",
)

internal fun filterDesktopZones(
    zones: List<ZoneContent>,
    filters: DesktopZoneFilters,
): List<ZoneContent> {
    val query = filters.query.trim().lowercase()
    val area = filters.area.trim().lowercase()
    val tag = filters.tag.trim().lowercase()

    return zones
        .distinctBy { it.item.identity.id }
        .filter { content ->
            area.isEmpty() || content.payload.area.lowercase().contains(area)
        }
        .filter { content ->
            tag.isEmpty() || content.payload.tags.any { it.lowercase().contains(tag) }
        }
        .filter { content ->
            query.isEmpty() || buildList {
                add(content.item.displayName)
                add(content.payload.summary)
                add(content.payload.area)
                add(content.payload.presentation)
                add(content.payload.space)
                add(content.payload.exploration)
                add(content.payload.encounterBrief)
                add(content.payload.dmGuidance)
                add(content.payload.playerSafeText)
                addAll(content.payload.interactives)
                addAll(content.payload.clues)
                addAll(content.payload.checks)
                addAll(content.payload.consequences)
                addAll(content.payload.paperReferences)
                addAll(content.payload.tags)
            }.any { it.lowercase().contains(query) }
        }
        .sortedWith(compareBy({ zoneScopeSortKey(it) }, { it.item.displayName.lowercase() }))
}

class DesktopZoneManagerController(
    database: AppDatabase,
    private val nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    private val campaignRepository = CampaignRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val reusableContent = ReusableContentRepository(database)
    private val zones = ZoneContentRepository(database, reusableContent)

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

    fun personalZones(ownerAccountId: Uuid): List<ZoneContent> =
        reusableContent.listPersonal(ownerAccountId, ReusableContentFamily.ZONE)
            .mapNotNull { zones.zone(it.identity.id) }

    fun campaignZones(campaignId: Uuid): List<ZoneContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.ZONE)
            .mapNotNull { zones.zone(it.identity.id) }

    fun zone(id: Uuid): ZoneContent? = zones.zone(id)

    fun createPersonal(ownerAccountId: Uuid, displayName: String): ZoneContent =
        zones.createPersonal(
            ownerAccountId = ownerAccountId,
            rawDisplayName = displayName,
            payload = ZonePayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun createCampaign(campaignId: Uuid, displayName: String): ZoneContent =
        zones.createCampaign(
            campaignId = campaignId,
            rawDisplayName = displayName,
            payload = ZonePayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun update(
        id: Uuid,
        expectedRevision: Revision,
        displayName: String,
        payload: ZonePayload,
    ): RevisionDecision = zones.update(
        id = id,
        expectedRevision = expectedRevision,
        rawDisplayName = displayName,
        payload = payload,
        updatedAtEpochSeconds = nowEpochSeconds(),
    )

    fun copyPersonalToCampaign(sourceId: Uuid, campaignId: Uuid): ZoneContent =
        zones.copyPersonalToCampaign(
            sourceId = sourceId,
            campaignId = campaignId,
            copiedAtEpochSeconds = nowEpochSeconds(),
        )
}

@Composable
internal fun DesktopZoneManagerScreen(
    controller: DesktopZoneManagerController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var refreshVersion by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    var areaFilter by remember { mutableStateOf("") }
    var tagFilter by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<Uuid?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val ownerAccountId = remember(refreshVersion, activeCampaign?.id) {
        controller.personalOwnerAccountId()
    }
    val personalZones = remember(refreshVersion, ownerAccountId) {
        ownerAccountId?.let(controller::personalZones).orEmpty()
    }
    val campaignZones = remember(refreshVersion, activeCampaign?.id) {
        activeCampaign?.id?.let(controller::campaignZones).orEmpty()
    }
    val selected = remember(refreshVersion, selectedId) {
        selectedId?.let(controller::zone)
    }
    var draft by remember(selected?.item?.identity?.id, selected?.item?.identity?.revision?.value) {
        mutableStateOf(selected?.let(ZoneDraft::from))
    }

    fun refresh(selectId: Uuid? = selectedId) {
        selectedId = selectId
        refreshVersion += 1
    }

    val visibleZones = filterDesktopZones(
        zones = personalZones + campaignZones,
        filters = DesktopZoneFilters(query = query, area = areaFilter, tag = tagFilter),
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Gestor de Mazmorras / Zonas", style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
            Text(
                "Prepara cada Zona como un Zone Brief: PRESENTAR, INTERACTUAR y ENCUENTRO. " +
                    "Espacio y exploración conservan la orientación topológica sin convertir el gestor en un VTT.",
                style = MaterialTheme.typography.body1,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Nueva zona") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Button(
                enabled = newName.isNotBlank() && ownerAccountId != null,
                onClick = {
                    runCatching { controller.createPersonal(requireNotNull(ownerAccountId), newName) }
                        .onSuccess { created ->
                            newName = ""
                            statusMessage = "Zona Personal creada."
                            onQaEvent("Zone Manager: zona Personal creada ${created.item.identity.id}")
                            refresh(created.item.identity.id)
                        }
                        .onFailure { statusMessage = it.message ?: "No se pudo crear la zona Personal." }
                },
            ) { Text("Crear Personal") }
            Button(
                enabled = newName.isNotBlank() && activeCampaign != null,
                onClick = {
                    runCatching { controller.createCampaign(requireNotNull(activeCampaign).id, newName) }
                        .onSuccess { created ->
                            newName = ""
                            statusMessage = "Zona creada en ${activeCampaign?.name}."
                            onQaEvent("Zone Manager: zona de campaña creada ${created.item.identity.id}")
                            refresh(created.item.identity.id)
                        }
                        .onFailure { statusMessage = it.message ?: "No se pudo crear la zona de campaña." }
                },
            ) { Text("Crear en campaña") }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            OutlinedTextField(
                value = areaFilter,
                onValueChange = { areaFilter = it },
                label = { Text("Filtrar área") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            OutlinedTextField(
                value = tagFilter,
                onValueChange = { tagFilter = it },
                label = { Text("Filtrar etiqueta") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
        }

        if (ownerAccountId == null) {
            Text(
                "Biblioteca Personal no disponible: Desktop no puede identificar de forma unívoca una cuenta DM local. " +
                    "Las zonas de campaña local siguen disponibles.",
                style = MaterialTheme.typography.caption,
            )
        }
        statusMessage?.let { Text(it, style = MaterialTheme.typography.caption) }

        Divider()

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ZoneList(
                zones = visibleZones,
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
                    Text("Selecciona una zona para abrirla.", style = MaterialTheme.typography.h6)
                    Text("Personal: ${personalZones.size} • Campaña activa: ${campaignZones.size}")
                }
            } else {
                ZoneEditor(
                    content = selected,
                    draft = requireNotNull(draft),
                    activeCampaign = activeCampaign,
                    onDraftChange = { draft = it },
                    onSave = {
                        val currentDraft = requireNotNull(draft)
                        when (val decision = controller.update(
                            id = selected.item.identity.id,
                            expectedRevision = selected.item.identity.revision,
                            displayName = currentDraft.displayName,
                            payload = currentDraft.toPayload(),
                        )) {
                            is RevisionDecision.Accepted -> {
                                statusMessage = "Cambios guardados (revisión ${decision.nextRevision.value})."
                                onQaEvent("Zone Manager: zona actualizada ${selected.item.identity.id}")
                                refresh(selected.item.identity.id)
                            }
                            is RevisionDecision.Stale -> {
                                statusMessage = "La zona cambió desde que fue abierta. Se recargó la versión actual."
                                refresh(selected.item.identity.id)
                            }
                            is RevisionDecision.Deleted -> {
                                statusMessage = "La zona fue eliminada y no puede ser sobrescrita."
                                refresh(null)
                            }
                        }
                    },
                    onCopyToCampaign = {
                        val campaign = activeCampaign
                        if (campaign == null) {
                            statusMessage = "Selecciona una campaña activa antes de copiar."
                        } else {
                            runCatching { controller.copyPersonalToCampaign(selected.item.identity.id, campaign.id) }
                                .onSuccess { copied ->
                                    statusMessage = "Copia independiente creada en ${campaign.name}."
                                    onQaEvent("Zone Manager: zona copiada a campaña ${copied.item.identity.id}")
                                    refresh(copied.item.identity.id)
                                }
                                .onFailure { statusMessage = it.message ?: "No se pudo copiar la zona." }
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun ZoneList(
    zones: List<ZoneContent>,
    selectedId: Uuid?,
    onSelect: (Uuid) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item {
            Text("Zonas (${zones.size})", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
        }
        if (zones.isEmpty()) {
            item { Text("No hay zonas que coincidan con el filtro.", style = MaterialTheme.typography.caption) }
        }
        items(zones, key = { it.item.identity.id.toString() }) { content ->
            val selected = content.item.identity.id == selectedId
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(content.item.identity.id) },
                elevation = if (selected) 6.dp else 1.dp,
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(content.item.displayName, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    Text(zoneScopeLabel(content), style = MaterialTheme.typography.caption)
                    if (content.payload.area.isNotBlank()) {
                        Text(content.payload.area, style = MaterialTheme.typography.caption)
                    }
                    if (content.payload.summary.isNotBlank()) {
                        Text(content.payload.summary, style = MaterialTheme.typography.caption)
                    }
                }
            }
        }
    }
}

@Composable
private fun ZoneEditor(
    content: ZoneContent,
    draft: ZoneDraft,
    activeCampaign: Campaign?,
    onDraftChange: (ZoneDraft) -> Unit,
    onSave: () -> Unit,
    onCopyToCampaign: () -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Editor de Zona", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
                    Text(
                        "${zoneScopeLabel(content)} • revisión ${content.item.identity.revision.value}",
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
                Button(onClick = onSave, enabled = draft.displayName.isNotBlank()) { Text("Guardar") }
            }
        }

        item { ZoneSectionHeading("PRESENTAR") }
        item { ZoneField("Nombre", draft.displayName, true) { onDraftChange(draft.copy(displayName = it)) } }
        item { ZoneField("Resumen", draft.summary, false, 3) { onDraftChange(draft.copy(summary = it)) } }
        item { ZoneField("Área / ubicación", draft.area, true) { onDraftChange(draft.copy(area = it)) } }
        item { ZoneField("Presentación / impresión inicial", draft.presentation, false, 4) { onDraftChange(draft.copy(presentation = it)) } }
        item { ZoneField("Espacio / topología de preparación", draft.space, false, 4) { onDraftChange(draft.copy(space = it)) } }
        item { ZoneField("Texto seguro para jugadores", draft.playerSafeText, false, 4) { onDraftChange(draft.copy(playerSafeText = it)) } }

        item { ZoneSectionHeading("INTERACTUAR") }
        item { ZoneField("Exploración / flujo", draft.exploration, false, 4) { onDraftChange(draft.copy(exploration = it)) } }
        item { ZoneField("Elementos interactivos (uno por línea)", draft.interactives, false, 4) { onDraftChange(draft.copy(interactives = it)) } }
        item { ZoneField("Pistas (una por línea)", draft.clues, false, 4) { onDraftChange(draft.copy(clues = it)) } }
        item { ZoneField("Pruebas / checks (una por línea)", draft.checks, false, 4) { onDraftChange(draft.copy(checks = it)) } }
        item { ZoneField("Consecuencias preparadas (una por línea)", draft.consequences, false, 4) { onDraftChange(draft.copy(consequences = it)) } }

        item { ZoneSectionHeading("ENCUENTRO") }
        item { ZoneField("Resumen del encuentro", draft.encounterBrief, false, 5) { onDraftChange(draft.copy(encounterBrief = it)) } }

        item { ZoneSectionHeading("APOYO DM") }
        item { ZoneField("Guía DM", draft.dmGuidance, false, 5) { onDraftChange(draft.copy(dmGuidance = it)) } }
        item { ZoneField("Referencias de papel (una por línea)", draft.paperReferences, false, 3) { onDraftChange(draft.copy(paperReferences = it)) } }
        item { ZoneField("Etiquetas (coma o línea)", draft.tags, false, 3) { onDraftChange(draft.copy(tags = it)) } }
    }
}

@Composable
private fun ZoneSectionHeading(label: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Divider()
        Text(label, style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ZoneField(
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

private data class ZoneDraft(
    val displayName: String,
    val summary: String,
    val area: String,
    val presentation: String,
    val space: String,
    val exploration: String,
    val interactives: String,
    val clues: String,
    val checks: String,
    val consequences: String,
    val encounterBrief: String,
    val dmGuidance: String,
    val playerSafeText: String,
    val paperReferences: String,
    val tags: String,
) {
    fun toPayload(): ZonePayload = ZonePayload(
        summary = summary,
        area = area,
        presentation = presentation,
        space = space,
        exploration = exploration,
        interactives = parseZoneLines(interactives),
        clues = parseZoneLines(clues),
        checks = parseZoneLines(checks),
        consequences = parseZoneLines(consequences),
        encounterBrief = encounterBrief,
        dmGuidance = dmGuidance,
        playerSafeText = playerSafeText,
        paperReferences = parseZoneLines(paperReferences),
        tags = tags.split(',', '\n').map(String::trim).filter(String::isNotEmpty),
    )

    companion object {
        fun from(content: ZoneContent): ZoneDraft = ZoneDraft(
            displayName = content.item.displayName,
            summary = content.payload.summary,
            area = content.payload.area,
            presentation = content.payload.presentation,
            space = content.payload.space,
            exploration = content.payload.exploration,
            interactives = content.payload.interactives.joinToString("\n"),
            clues = content.payload.clues.joinToString("\n"),
            checks = content.payload.checks.joinToString("\n"),
            consequences = content.payload.consequences.joinToString("\n"),
            encounterBrief = content.payload.encounterBrief,
            dmGuidance = content.payload.dmGuidance,
            playerSafeText = content.payload.playerSafeText,
            paperReferences = content.payload.paperReferences.joinToString("\n"),
            tags = content.payload.tags.joinToString(", "),
        )
    }
}

private fun parseZoneLines(raw: String): List<String> =
    raw.lines().map(String::trim).filter(String::isNotEmpty)

private fun zoneScopeSortKey(content: ZoneContent): Int = when (content.item.identity.scope) {
    is ContentScope.Personal -> 0
    is ContentScope.Campaign -> 1
    else -> 2
}

private fun zoneScopeLabel(content: ZoneContent): String = when (content.item.identity.scope) {
    is ContentScope.Personal -> "Personal"
    is ContentScope.Campaign -> "Campaña"
    is ContentScope.Official -> "Oficial / SRD"
    ContentScope.System -> "Sistema"
}
