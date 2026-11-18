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
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContent
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceKind
import io.github.mrsimkin.dndcustomaid.shared.content.PlacePayload
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

private enum class DesktopManagersHubSection(val label: String) {
    CREATURES_NPCS_HOMEBREW("Criaturas / PNJ / Homebrew"),
    PLACES_SHOPS("Escenarios / Lugares / Tiendas"),
    DUNGEONS_ZONES("Dungeons"),
    ENCOUNTERS("Encuentros"),
    PACKAGES("Paquetes"),
}

@Composable
fun DesktopManagersHubScreen(
    creatureController: DesktopCreatureManagerController,
    npcController: DesktopNpcManagerController,
    homebrewRuleController: DesktopHomebrewRuleManagerController,
    placeController: DesktopPlaceManagerController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var section by remember { mutableStateOf(DesktopManagersHubSection.CREATURES_NPCS_HOMEBREW) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DesktopManagersHubSection.entries.forEach { candidate ->
                if (candidate == section) {
                    Button(onClick = { section = candidate }) { Text(candidate.label) }
                } else {
                    TextButton(onClick = { section = candidate }) { Text(candidate.label) }
                }
            }
        }
        HorizontalDivider()
        when (section) {
            DesktopManagersHubSection.CREATURES_NPCS_HOMEBREW -> DesktopAuthoringManagersScreen(
                creatureController = creatureController,
                npcController = npcController,
                homebrewRuleController = homebrewRuleController,
                activeCampaign = activeCampaign,
                onQaEvent = onQaEvent,
            )
            DesktopManagersHubSection.PLACES_SHOPS -> DesktopStageAndSceneManagerScreen(
                controller = placeController,
                activeCampaign = activeCampaign,
                onQaEvent = onQaEvent,
            )
            DesktopManagersHubSection.DUNGEONS_ZONES -> DesktopZoneManagerScreen(
                controller = placeController.zoneManagerController,
                activeCampaign = activeCampaign,
                onQaEvent = onQaEvent,
            )
            DesktopManagersHubSection.ENCOUNTERS -> DesktopEncounterManagerScreen(
                controller = placeController.encounterManagerController,
                activeCampaign = activeCampaign,
                onQaEvent = onQaEvent,
            )
            DesktopManagersHubSection.PACKAGES -> DesktopPackageManagerScreen(
                controller = placeController.packageManagerController,
                activeCampaign = activeCampaign,
                onQaEvent = onQaEvent,
            )
        }
    }
}

class DesktopPlaceManagerController(
    database: AppDatabase,
    private val nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    private val campaignRepository = CampaignRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val reusableContent = ReusableContentRepository(database)
    private val places = PlaceContentRepository(database, reusableContent)
    internal val sceneManagerController = DesktopSceneManagerController(database, nowEpochSeconds)
    internal val zoneManagerController = DesktopZoneManagerController(database, nowEpochSeconds)
    internal val packageManagerController = DesktopPackageManagerController(database, nowEpochSeconds)
    internal val encounterManagerController = DesktopEncounterManagerController(database, nowEpochSeconds)

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

    fun personalPlaces(ownerAccountId: Uuid): List<PlaceContent> =
        reusableContent.listPersonal(ownerAccountId, ReusableContentFamily.PLACE)
            .mapNotNull { places.place(it.identity.id) }

    fun campaignPlaces(campaignId: Uuid): List<PlaceContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.PLACE)
            .mapNotNull { places.place(it.identity.id) }

    fun place(id: Uuid): PlaceContent? = places.place(id)

    fun createPersonal(
        ownerAccountId: Uuid,
        displayName: String,
        kind: PlaceKind = PlaceKind.PLACE,
    ): PlaceContent = places.createPersonal(
        ownerAccountId = ownerAccountId,
        rawDisplayName = displayName,
        payload = PlacePayload(kind = kind),
        nowEpochSeconds = nowEpochSeconds(),
    )

    fun createCampaign(
        campaignId: Uuid,
        displayName: String,
        kind: PlaceKind = PlaceKind.PLACE,
    ): PlaceContent = places.createCampaign(
        campaignId = campaignId,
        rawDisplayName = displayName,
        payload = PlacePayload(kind = kind),
        nowEpochSeconds = nowEpochSeconds(),
    )

    fun update(
        id: Uuid,
        expectedRevision: Revision,
        displayName: String,
        payload: PlacePayload,
    ): RevisionDecision = places.update(
        id = id,
        expectedRevision = expectedRevision,
        rawDisplayName = displayName,
        payload = payload,
        updatedAtEpochSeconds = nowEpochSeconds(),
    )

    fun copyPersonalToCampaign(sourceId: Uuid, campaignId: Uuid): PlaceContent =
        places.copyPersonalToCampaign(
            sourceId = sourceId,
            campaignId = campaignId,
            copiedAtEpochSeconds = nowEpochSeconds(),
        )
}

@Composable
internal fun DesktopPlaceManagerScreen(
    controller: DesktopPlaceManagerController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var refreshVersion by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    var stageFilters by remember { mutableStateOf(StagePlaceFilters()) }
    var newName by remember { mutableStateOf("") }
    var newKind by remember { mutableStateOf(PlaceKind.PLACE) }
    var selectedId by remember { mutableStateOf<Uuid?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val ownerAccountId = remember(refreshVersion, activeCampaign?.id) {
        controller.personalOwnerAccountId()
    }
    val personalPlaces = remember(refreshVersion, ownerAccountId) {
        ownerAccountId?.let(controller::personalPlaces).orEmpty()
    }
    val campaignPlaces = remember(refreshVersion, activeCampaign?.id) {
        activeCampaign?.id?.let(controller::campaignPlaces).orEmpty()
    }
    val selected = remember(refreshVersion, selectedId) {
        selectedId?.let(controller::place)
    }
    // Owner point 10: the card first, the complete preparation only when asked for.
    var openBlock by remember(selected?.item?.identity?.id) { mutableStateOf<String?>(null) }
    var showAll by remember(selected?.item?.identity?.id) { mutableStateOf(false) }
    var draft by remember(selected?.item?.identity?.id, selected?.item?.identity?.revision?.value) {
        mutableStateOf(selected?.let(PlaceDraft::from))
    }

    fun refresh(selectId: Uuid? = selectedId) {
        selectedId = selectId
        refreshVersion += 1
    }

    val visiblePlaces = filterStagePlaces(
        places = personalPlaces + campaignPlaces,
        filters = stageFilters.copy(query = query),
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
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
                label = { Text("Nuevo lugar / tienda") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            PlaceKind.entries.forEach { kind ->
                if (newKind == kind) {
                    Button(onClick = { newKind = kind }) { Text(placeKindLabel(kind)) }
                } else {
                    TextButton(onClick = { newKind = kind }) { Text(placeKindLabel(kind)) }
                }
            }
            Button(
                enabled = newName.isNotBlank() && activeCampaign != null,
                onClick = {
                    runCatching {
                        controller.createCampaign(requireNotNull(activeCampaign).id, newName, newKind)
                    }.onSuccess { created ->
                        newName = ""
                        statusMessage = "${placeKindLabel(created.payload.kind)} creado en ${activeCampaign?.name}."
                        onQaEvent("Place Manager: contenido de campaña creado ${created.item.identity.id}")
                        refresh(created.item.identity.id)
                    }.onFailure { statusMessage = it.message ?: "No se pudo crear el contenido de campaña." }
                },
            ) { Text("＋ Crear") }
        }

        statusMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }

        StagePlaceFilterControls(
            filters = stageFilters,
            onFiltersChange = { stageFilters = it },
        )

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PlaceList(
                    searching = query.isNotBlank(),
                places = visiblePlaces,
                selectedId = selectedId,
                onSelect = {
                    selectedId = it
                    statusMessage = null
                },
                modifier = Modifier.width(330.dp).fillMaxHeight(),
            )
            VerticalDivider()
            if (selected == null || draft == null) {
                Column(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Selecciona un lugar o tienda para abrirlo.", style = MaterialTheme.typography.titleLarge)
                    Text("${campaignPlaces.size} en esta campaña.")
                }
            } else {
                PlaceEditor(
                    openBlock = openBlock,
                    onOpenBlock = { openBlock = it },
                    showAll = showAll,
                    onShowAll = { showAll = it },
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
                                statusMessage = "Cambios guardados."
                                onQaEvent("Place Manager: contenido actualizado ${selected.item.identity.id}")
                                refresh(selected.item.identity.id)
                            }
                            is RevisionDecision.Stale -> {
                                statusMessage = "El contenido cambió desde que fue abierto. Se recargó la versión actual."
                                refresh(selected.item.identity.id)
                            }
                            is RevisionDecision.Deleted -> {
                                statusMessage = "El contenido fue eliminado y no puede ser sobrescrito."
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
                                    onQaEvent("Place Manager: contenido copiado a campaña ${copied.item.identity.id}")
                                    refresh(copied.item.identity.id)
                                }
                                .onFailure { statusMessage = it.message ?: "No se pudo copiar el contenido." }
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun PlaceList(
    places: List<PlaceContent>,
    searching: Boolean,
    selectedId: Uuid?,
    onSelect: (Uuid) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item {
            Text("Lugares y tiendas (${places.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        if (places.isEmpty()) {
            item { EmptyList(searching = searching, nothingYet = "Aún no hay lugares ni tiendas.", howToCreate = "Escribe un nombre en «Nuevo lugar / tienda» y toca «＋ Crear».") }
        }
        items(places, key = { it.item.identity.id.toString() }) { content ->
            val selected = content.item.identity.id == selectedId
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(content.item.identity.id) },
                elevation = androidx.compose.material3.CardDefaults.cardElevation(if (selected) 6.dp else 1.dp),
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(content.item.displayName, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    Text(listOf(placeKindLabel(content.payload.kind), placeScopeLabel(content)).filter { it.isNotBlank() }.joinToString(" • "), style = MaterialTheme.typography.bodySmall)
                    if (content.payload.area.isNotBlank()) {
                        Text(content.payload.area, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceEditor(
    content: PlaceContent,
    draft: PlaceDraft,
    activeCampaign: Campaign?,
    onDraftChange: (PlaceDraft) -> Unit,
    onSave: () -> Unit,
    onCopyToCampaign: () -> Unit,
    modifier: Modifier,
    openBlock: String?,
    onOpenBlock: (String?) -> Unit,
    showAll: Boolean,
    onShowAll: (Boolean) -> Unit,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Editor de Lugar / Tienda", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "${placeKindLabel(draft.kind)}",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (content.item.identity.scope is ContentScope.Personal && activeCampaign != null) {
                    Button(onClick = onCopyToCampaign) { Text("Traer a esta campaña") }
                }
                Button(onClick = onSave, enabled = draft.displayName.isNotBlank()) { Text("Guardar") }
            }
        }

        // Owner points 6 and 10: the card you read at the table comes first; «Ver todo» opens the
        // complete preparation underneath, with every field as before.
        item {
            SheetBlock(
                title = "En la mesa",
                editing = openBlock == "mesa",
                onEdit = { onOpenBlock("mesa") },
                onDone = { onOpenBlock(null) },
            ) {
                if (openBlock == "mesa") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlaceField("Nombre", draft.displayName, true) { onDraftChange(draft.copy(displayName = it)) }
                        PlaceField("Resumen", draft.summary, false, 3) { onDraftChange(draft.copy(summary = it)) }
                        PlaceField("Presentación / atmósfera", draft.presentation, false, 4) { onDraftChange(draft.copy(presentation = it)) }
                        PlaceField("Ganchos (uno por línea)", draft.hooks, false, 4) { onDraftChange(draft.copy(hooks = it)) }
                    }
                } else {
                    Text(draft.displayName.ifBlank { "Sin nombre" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    SheetLine("Qué es", draft.summary)
                    SheetLine("Al llegar", draft.presentation)
                    SheetLine("Ganchos", draft.hooks)
                    if (listOf(draft.summary, draft.presentation, draft.hooks).all { it.isBlank() }) {
                        SheetEmpty("Todavía sin nada que leer en la mesa. Toca «Editar».")
                    }
                }
            }
        }
        item {
            SheetBlock(
                title = "Para los jugadores y para ti",
                editing = openBlock == "textos",
                onEdit = { onOpenBlock("textos") },
                onDone = { onOpenBlock(null) },
            ) {
                if (openBlock == "textos") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlaceField("Texto seguro para jugadores", draft.playerSafeText, false, 4) { onDraftChange(draft.copy(playerSafeText = it)) }
                        PlaceField("Notas DM", draft.dmNotes, false, 4) { onDraftChange(draft.copy(dmNotes = it)) }
                    }
                } else {
                    SheetLine("Se puede leer en voz alta", draft.playerSafeText)
                    SheetLine("Solo para ti", draft.dmNotes)
                    if (draft.playerSafeText.isBlank() && draft.dmNotes.isBlank()) SheetEmpty("Sin textos preparados.")
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { onShowAll(!showAll) }) {
                    Text(if (showAll) "▾ Ocultar la preparación completa" else "▸ Ver todo (preparación completa)")
                }
                Text(
                    if (showAll) "Todos los campos, como antes." else "Lo de arriba es lo que se ve en la mesa.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        if (!showAll) return@LazyColumn

        item { PlaceField("Nombre", draft.displayName, true) { onDraftChange(draft.copy(displayName = it)) } }
        item {
            Text("Tipo", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                PlaceKind.entries.forEach { kind ->
                    if (draft.kind == kind) {
                        Button(onClick = { onDraftChange(draft.copy(kind = kind)) }) { Text(placeKindLabel(kind)) }
                    } else {
                        TextButton(onClick = { onDraftChange(draft.copy(kind = kind)) }) { Text(placeKindLabel(kind)) }
                    }
                }
            }
        }
        item { PlaceField("Resumen", draft.summary, false, 3) { onDraftChange(draft.copy(summary = it)) } }
        item { PlaceField("Área / ubicación", draft.area, true) { onDraftChange(draft.copy(area = it)) } }
        item { PlaceField("Función", draft.function, false, 3) { onDraftChange(draft.copy(function = it)) } }
        item { PlaceField("Presentación / atmósfera", draft.presentation, false, 4) { onDraftChange(draft.copy(presentation = it)) } }
        item { PlaceField("Servicios (uno por línea)", draft.services, false, 4) { onDraftChange(draft.copy(services = it)) } }
        item { PlaceField("Interacciones (una por línea)", draft.interactives, false, 4) { onDraftChange(draft.copy(interactives = it)) } }
        item { PlaceField("Ganchos (uno por línea)", draft.hooks, false, 4) { onDraftChange(draft.copy(hooks = it)) } }
        item { PlaceField("Texto seguro para jugadores", draft.playerSafeText, false, 4) { onDraftChange(draft.copy(playerSafeText = it)) } }
        item { PlaceField("Notas DM", draft.dmNotes, false, 4) { onDraftChange(draft.copy(dmNotes = it)) } }
        item { PlaceField("Referencias de papel (una por línea)", draft.paperReferences, false, 3) { onDraftChange(draft.copy(paperReferences = it)) } }
        item { PlaceField("Etiquetas (coma o línea)", draft.tags, false, 3) { onDraftChange(draft.copy(tags = it)) } }
        item { DesktopContentLinksSection(content.item) }
    }
}

@Composable
private fun PlaceField(
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

private data class PlaceDraft(
    val displayName: String,
    val kind: PlaceKind,
    val summary: String,
    val area: String,
    val function: String,
    val presentation: String,
    val services: String,
    val interactives: String,
    val hooks: String,
    val playerSafeText: String,
    val dmNotes: String,
    val paperReferences: String,
    val tags: String,
) {
    fun toPayload(): PlacePayload = PlacePayload(
        kind = kind,
        summary = summary,
        area = area,
        function = function,
        presentation = presentation,
        services = parsePlaceLines(services),
        interactives = parsePlaceLines(interactives),
        hooks = parsePlaceLines(hooks),
        playerSafeText = playerSafeText,
        dmNotes = dmNotes,
        paperReferences = parsePlaceLines(paperReferences),
        tags = tags.split(',', '\n').map(String::trim).filter(String::isNotEmpty),
    )

    companion object {
        fun from(content: PlaceContent): PlaceDraft = PlaceDraft(
            displayName = content.item.displayName,
            kind = content.payload.kind,
            summary = content.payload.summary,
            area = content.payload.area,
            function = content.payload.function,
            presentation = content.payload.presentation,
            services = content.payload.services.joinToString("\n"),
            interactives = content.payload.interactives.joinToString("\n"),
            hooks = content.payload.hooks.joinToString("\n"),
            playerSafeText = content.payload.playerSafeText,
            dmNotes = content.payload.dmNotes,
            paperReferences = content.payload.paperReferences.joinToString("\n"),
            tags = content.payload.tags.joinToString(", "),
        )
    }
}

private fun parsePlaceLines(raw: String): List<String> =
    raw.lines().map(String::trim).filter(String::isNotEmpty)

private fun placeScopeSortKey(content: PlaceContent): Int = when (content.item.identity.scope) {
    is ContentScope.Personal -> 0
    is ContentScope.Campaign -> 1
    else -> 2
}

private fun placeScopeLabel(content: PlaceContent): String = placeScopeLabel(content.item.identity.scope)

private fun placeScopeLabel(scope: ContentScope): String = when (scope) {
    is ContentScope.Personal -> "De tu biblioteca anterior"
    is ContentScope.Campaign -> ""
    is ContentScope.Official -> "Oficial / SRD"
    ContentScope.System -> "Sistema"
}

private fun placeKindLabel(kind: PlaceKind): String = when (kind) {
    PlaceKind.PLACE -> "Lugar"
    PlaceKind.SHOP -> "Tienda"
}