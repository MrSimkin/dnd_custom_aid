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
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureAbilityScores
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContent
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NpcPayload
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

private enum class DesktopManagerSection(val label: String) {
    CREATURES("Criaturas / Monstruos"),
    NPCS("PNJ"),
}

@Composable
fun DesktopManagersScreen(
    creatureController: DesktopCreatureManagerController,
    npcController: DesktopNpcManagerController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var section by remember { mutableStateOf(DesktopManagerSection.CREATURES) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DesktopManagerSection.entries.forEach { candidate ->
                if (candidate == section) {
                    Button(onClick = { section = candidate }) { Text(candidate.label) }
                } else {
                    TextButton(onClick = { section = candidate }) { Text(candidate.label) }
                }
            }
        }
        HorizontalDivider()
        when (section) {
            DesktopManagerSection.CREATURES -> CreatureManagerScreen(
                controller = creatureController,
                activeCampaign = activeCampaign,
                onQaEvent = onQaEvent,
            )
            DesktopManagerSection.NPCS -> NpcManagerScreen(
                controller = npcController,
                activeCampaign = activeCampaign,
                onQaEvent = onQaEvent,
            )
        }
    }
}

class DesktopNpcManagerController(
    database: AppDatabase,
    private val nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    private val campaignRepository = CampaignRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val reusableContent = ReusableContentRepository(database)
    private val npcs = NpcContentRepository(database, reusableContent)

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

    fun personalNpcs(ownerAccountId: Uuid): List<NpcContent> =
        reusableContent.listPersonal(ownerAccountId, ReusableContentFamily.NPC)
            .mapNotNull { npcs.npc(it.identity.id) }

    fun campaignNpcs(campaignId: Uuid): List<NpcContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.NPC)
            .mapNotNull { npcs.npc(it.identity.id) }

    fun npc(id: Uuid): NpcContent? = npcs.npc(id)

    fun createPersonal(ownerAccountId: Uuid, displayName: String): NpcContent =
        npcs.createPersonal(
            ownerAccountId = ownerAccountId,
            rawDisplayName = displayName,
            payload = NpcPayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun createCampaign(campaignId: Uuid, displayName: String): NpcContent =
        npcs.createCampaign(
            campaignId = campaignId,
            rawDisplayName = displayName,
            payload = NpcPayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun update(
        id: Uuid,
        expectedRevision: Revision,
        displayName: String,
        payload: NpcPayload,
    ): RevisionDecision = npcs.update(
        id = id,
        expectedRevision = expectedRevision,
        rawDisplayName = displayName,
        payload = payload,
        updatedAtEpochSeconds = nowEpochSeconds(),
    )

    fun copyPersonalToCampaign(sourceId: Uuid, campaignId: Uuid): NpcContent =
        npcs.copyPersonalToCampaign(
            sourceId = sourceId,
            campaignId = campaignId,
            copiedAtEpochSeconds = nowEpochSeconds(),
        )
}

@Composable
internal fun NpcManagerScreen(
    controller: DesktopNpcManagerController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var refreshVersion by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf<Uuid?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val ownerAccountId = remember(refreshVersion, activeCampaign?.id) {
        controller.personalOwnerAccountId()
    }
    val personalNpcs = remember(refreshVersion, ownerAccountId) {
        ownerAccountId?.let(controller::personalNpcs).orEmpty()
    }
    val campaignNpcs = remember(refreshVersion, activeCampaign?.id) {
        activeCampaign?.id?.let(controller::campaignNpcs).orEmpty()
    }
    val selected = remember(refreshVersion, selectedId) {
        selectedId?.let(controller::npc)
    }
    // Owner point 10: the card first, the complete preparation only when asked for.
    var openBlock by remember(selected?.item?.identity?.id) { mutableStateOf<String?>(null) }
    var showAll by remember(selected?.item?.identity?.id) { mutableStateOf(false) }
    var draft by remember(selected?.item?.identity?.id, selected?.item?.identity?.revision?.value) {
        mutableStateOf(selected?.let(NpcDraft::from))
    }

    fun refresh(selectId: Uuid? = selectedId) {
        selectedId = selectId
        refreshVersion += 1
    }

    val normalizedQuery = query.trim().lowercase()
    val visibleNpcs = (personalNpcs + campaignNpcs)
        .distinctBy { it.item.identity.id }
        .filter { content ->
            normalizedQuery.isEmpty() || listOf(
                content.item.displayName,
                content.payload.conceptRole,
                content.payload.appearanceFirstImpression,
                content.payload.personalityManner,
                content.payload.relationshipContext,
                content.payload.affiliations,
                content.payload.places,
            ).any { it.lowercase().contains(normalizedQuery) }
        }
        .sortedWith(compareBy({ npcScopeSortKey(it) }, { it.item.displayName.lowercase() }))

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
                label = { Text("Nuevo PNJ") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Button(
                enabled = newName.isNotBlank() && activeCampaign != null,
                onClick = {
                    runCatching { controller.createCampaign(requireNotNull(activeCampaign).id, newName) }
                        .onSuccess { created ->
                            newName = ""
                            statusMessage = "PNJ creado en ${activeCampaign?.name}."
                            onQaEvent("NPC Manager: PNJ de campaña creado ${created.item.identity.id}")
                            refresh(created.item.identity.id)
                        }
                        .onFailure { statusMessage = it.message ?: "No se pudo crear el PNJ de campaña." }
                },
            ) { Text("＋ Crear") }
        }

        statusMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NpcList(
                    searching = query.isNotBlank(),
                npcs = visibleNpcs,
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
                    Text("Selecciona un PNJ para abrirlo.", style = MaterialTheme.typography.titleLarge)
                    Text("${campaignNpcs.size} en esta campaña.")
                }
            } else {
                NpcEditor(
                    content = selected,
                    draft = requireNotNull(draft),
                    activeCampaign = activeCampaign,
                    onDraftChange = { draft = it },
                    onSave = {
                        val currentDraft = requireNotNull(draft)
                        runCatching { currentDraft.toPayload() }
                            .onSuccess { payload ->
                                when (val decision = controller.update(
                                    id = selected.item.identity.id,
                                    expectedRevision = selected.item.identity.revision,
                                    displayName = currentDraft.displayName,
                                    payload = payload,
                                )) {
                                    is RevisionDecision.Accepted -> {
                                        statusMessage = "Cambios guardados."
                                        onQaEvent("NPC Manager: PNJ actualizado ${selected.item.identity.id}")
                                        refresh(selected.item.identity.id)
                                    }
                                    is RevisionDecision.Stale -> {
                                        statusMessage = "El PNJ cambió desde que fue abierto. Se recargó la versión actual."
                                        refresh(selected.item.identity.id)
                                    }
                                    is RevisionDecision.Deleted -> {
                                        statusMessage = "El PNJ fue eliminado y no puede ser sobrescrito."
                                        refresh(null)
                                    }
                                }
                            }
                            .onFailure { statusMessage = it.message ?: "Datos de PNJ inválidos." }
                    },
                    onCopyToCampaign = {
                        val campaign = activeCampaign
                        if (campaign == null) {
                            statusMessage = "Selecciona una campaña activa antes de copiar."
                        } else {
                            runCatching { controller.copyPersonalToCampaign(selected.item.identity.id, campaign.id) }
                                .onSuccess { copied ->
                                    statusMessage = "Copia independiente creada en ${campaign.name}."
                                    onQaEvent("NPC Manager: PNJ copiado a campaña ${copied.item.identity.id}")
                                    refresh(copied.item.identity.id)
                                }
                                .onFailure { statusMessage = it.message ?: "No se pudo copiar el PNJ." }
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    openBlock = openBlock,
                    onOpenBlock = { openBlock = it },
                    showAll = showAll,
                    onShowAll = { showAll = it },
                )
            }
        }
    }
}

@Composable
private fun NpcList(
    npcs: List<NpcContent>,
    searching: Boolean,
    selectedId: Uuid?,
    onSelect: (Uuid) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item {
            Text("PNJ (${npcs.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        if (npcs.isEmpty()) {
            item { EmptyList(searching = searching, nothingYet = "Aún no hay PNJ en esta campaña.", howToCreate = "Escribe un nombre en «Nuevo PNJ» y toca «＋ Crear».") }
        }
        items(npcs, key = { it.item.identity.id.toString() }) { content ->
            val selected = content.item.identity.id == selectedId
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(content.item.identity.id) },
                elevation = androidx.compose.material3.CardDefaults.cardElevation(if (selected) 6.dp else 1.dp),
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(content.item.displayName, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    npcScopeLabel(content).takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    val stage = if (content.payload.hasDevelopedDetails()) "Desarrollado" else "Rápido"
                    val combat = if (content.payload.combatMechanics != null) " • combate" else ""
                    Text("$stage$combat", style = MaterialTheme.typography.bodySmall)
                    if (content.payload.conceptRole.isNotBlank()) {
                        Text(content.payload.conceptRole, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun NpcEditor(
    content: NpcContent,
    draft: NpcDraft,
    activeCampaign: Campaign?,
    onDraftChange: (NpcDraft) -> Unit,
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
                    Text("Editor de PNJ", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    npcScopeLabel(content).takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                }
                Button(onClick = onSave) { Text("Guardar") }
                if (content.item.identity.scope is ContentScope.Personal) {
                    Button(onClick = onCopyToCampaign, enabled = activeCampaign != null) { Text("Usar en campaña") }
                }
            }
        }

        // Owner points 6 and 10: the PNJ opens as the card you read at the table; «Editar» turns
        // one block into its fields, and «Ver todo» opens the complete preparation below.
        item {
            SheetBlock(
                title = "Quién es",
                editing = openBlock == "quien",
                onEdit = { onOpenBlock("quien") },
                onDone = { onOpenBlock(null) },
            ) {
                if (openBlock == "quien") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NpcEditorTextField("Nombre", draft.displayName) { onDraftChange(draft.copy(displayName = it)) }
                        NpcEditorTextField("Concepto / rol", draft.conceptRole) { onDraftChange(draft.copy(conceptRole = it)) }
                        NpcEditorTextField("Apariencia / primera impresión", draft.appearanceFirstImpression, lines = 3) { onDraftChange(draft.copy(appearanceFirstImpression = it)) }
                        NpcEditorTextField("Personalidad / manera", draft.personalityManner, lines = 3) { onDraftChange(draft.copy(personalityManner = it)) }
                    }
                } else {
                    Text(draft.displayName.ifBlank { "Sin nombre" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    SheetLine("Concepto", draft.conceptRole)
                    SheetLine("Primera impresión", draft.appearanceFirstImpression)
                    SheetLine("Manera", draft.personalityManner)
                    if (listOf(draft.conceptRole, draft.appearanceFirstImpression, draft.personalityManner).all { it.isBlank() }) {
                        SheetEmpty("Todavía sin descripción. Toca «Editar» para escribirla.")
                    }
                }
            }
        }
        item {
            SheetBlock(
                title = "Qué quiere en la mesa",
                editing = openBlock == "quiere",
                onEdit = { onOpenBlock("quiere") },
                onDone = { onOpenBlock(null) },
            ) {
                if (openBlock == "quiere") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NpcEditorTextField("Quiere / teme / necesita", draft.wantsFearsNeeds, lines = 3) { onDraftChange(draft.copy(wantsFearsNeeds = it)) }
                        NpcEditorTextField("Qué puede ofrecer", draft.canOffer, lines = 3) { onDraftChange(draft.copy(canOffer = it)) }
                        NpcEditorTextField("Límites / negativas", draft.limitsRefusals, lines = 3) { onDraftChange(draft.copy(limitsRefusals = it)) }
                        NpcEditorTextField("Relación / contexto", draft.relationshipContext, lines = 3) { onDraftChange(draft.copy(relationshipContext = it)) }
                    }
                } else {
                    SheetLine("Quiere, teme, necesita", draft.wantsFearsNeeds)
                    SheetLine("Puede ofrecer", draft.canOffer)
                    SheetLine("No hará", draft.limitsRefusals)
                    SheetLine("Relación con el grupo", draft.relationshipContext)
                    if (listOf(draft.wantsFearsNeeds, draft.canOffer, draft.limitsRefusals, draft.relationshipContext).all { it.isBlank() }) {
                        SheetEmpty("Sin intenciones anotadas. Es lo que más sirve en la mesa.")
                    }
                }
            }
        }
        item {
            SheetBlock(
                title = "Secretos y guía para ti",
                editing = openBlock == "secretos",
                onEdit = { onOpenBlock("secretos") },
                onDone = { onOpenBlock(null) },
            ) {
                if (openBlock == "secretos") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        NpcEditorTextField("Secretos", draft.secrets, lines = 4) { onDraftChange(draft.copy(secrets = it)) }
                        NpcEditorTextField("Guía para el DM", draft.dmGuidance, lines = 4) { onDraftChange(draft.copy(dmGuidance = it)) }
                        NpcEditorTextField("Notas", draft.notes, lines = 4) { onDraftChange(draft.copy(notes = it)) }
                    }
                } else {
                    SheetLine("Secretos", draft.secrets)
                    SheetLine("Guía", draft.dmGuidance)
                    SheetLine("Notas", draft.notes)
                    if (listOf(draft.secrets, draft.dmGuidance, draft.notes).all { it.isBlank() }) {
                        SheetEmpty("Sin secretos ni notas.")
                    }
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

        item { NpcSectionTitle("PNJ rápido") }
        item { NpcEditorTextField("Nombre", draft.displayName) { onDraftChange(draft.copy(displayName = it)) } }
        item { NpcEditorTextField("Concepto / rol", draft.conceptRole) { onDraftChange(draft.copy(conceptRole = it)) } }
        item { NpcEditorTextField("Apariencia / primera impresión", draft.appearanceFirstImpression, lines = 3) { onDraftChange(draft.copy(appearanceFirstImpression = it)) } }
        item { NpcEditorTextField("Personalidad / manera", draft.personalityManner, lines = 3) { onDraftChange(draft.copy(personalityManner = it)) } }
        item { NpcEditorTextField("Quiere / teme / necesita", draft.wantsFearsNeeds, lines = 3) { onDraftChange(draft.copy(wantsFearsNeeds = it)) } }
        item { NpcEditorTextField("Qué puede ofrecer", draft.canOffer, lines = 3) { onDraftChange(draft.copy(canOffer = it)) } }
        item { NpcEditorTextField("Límites / negativas", draft.limitsRefusals, lines = 3) { onDraftChange(draft.copy(limitsRefusals = it)) } }
        item { NpcEditorTextField("Relación / contexto", draft.relationshipContext, lines = 3) { onDraftChange(draft.copy(relationshipContext = it)) } }
        item { NpcEditorTextField("Notas", draft.notes, lines = 4) { onDraftChange(draft.copy(notes = it)) } }

        item { NpcSectionTitle("Desarrollo opcional") }
        item { NpcEditorTextField("Identidad / detalles", draft.identityDetails, lines = 3) { onDraftChange(draft.copy(identityDetails = it)) } }
        item { NpcEditorTextField("Voz / manierismos", draft.voiceMannerisms, lines = 3) { onDraftChange(draft.copy(voiceMannerisms = it)) } }
        item { NpcEditorTextField("Motivaciones", draft.motivations, lines = 3) { onDraftChange(draft.copy(motivations = it)) } }
        item { NpcEditorTextField("Valores / creencias", draft.valuesBeliefs, lines = 3) { onDraftChange(draft.copy(valuesBeliefs = it)) } }
        item { NpcEditorTextField("Relaciones", draft.relationships, lines = 4) { onDraftChange(draft.copy(relationships = it)) } }
        item { NpcEditorTextField("Historia", draft.history, lines = 4) { onDraftChange(draft.copy(history = it)) } }
        item { NpcEditorTextField("Secretos", draft.secrets, lines = 4) { onDraftChange(draft.copy(secrets = it)) } }
        item { NpcEditorTextField("Conocimiento", draft.knowledge, lines = 4) { onDraftChange(draft.copy(knowledge = it)) } }
        item { NpcEditorTextField("Objetivos", draft.goals, lines = 3) { onDraftChange(draft.copy(goals = it)) } }
        item { NpcEditorTextField("Recursos", draft.resources, lines = 3) { onDraftChange(draft.copy(resources = it)) } }
        item { NpcEditorTextField("Afiliaciones", draft.affiliations, lines = 3) { onDraftChange(draft.copy(affiliations = it)) } }
        item { NpcEditorTextField("Lugares", draft.places, lines = 3) { onDraftChange(draft.copy(places = it)) } }
        item { NpcEditorTextField("Aventura / escena", draft.adventureSceneLinks, lines = 3) { onDraftChange(draft.copy(adventureSceneLinks = it)) } }
        item { NpcEditorTextField("Guía para el DM", draft.dmGuidance, lines = 4) { onDraftChange(draft.copy(dmGuidance = it)) } }
        item { DesktopContentLinksSection(content.item) }

        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NpcSectionTitle("Mecánicas de combate opcionales")
                if (draft.combat == null) {
                    Button(onClick = { onDraftChange(draft.copy(combat = NpcCombatDraft())) }) {
                        Text("Agregar combate")
                    }
                } else {
                    TextButton(onClick = { onDraftChange(draft.copy(combat = null)) }) {
                        Text("Quitar combate")
                    }
                }
            }
        }

        draft.combat?.let { combat ->
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NpcEditorTextField("Tamaño", combat.size, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(size = it))) }
                    NpcEditorTextField("Tipo", combat.creatureType, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(creatureType = it))) }
                    NpcEditorTextField("Alineamiento", combat.alignment, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(alignment = it))) }
                    NpcEditorTextField("CR", combat.challengeRating, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(challengeRating = it))) }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NpcEditorTextField("CA", combat.armorClass, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(armorClass = it))) }
                    NpcEditorTextField("Detalle CA", combat.armorClassDetails, Modifier.weight(2f), true) { onDraftChange(draft.copy(combat = combat.copy(armorClassDetails = it))) }
                    NpcEditorTextField("PG", combat.hitPoints, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(hitPoints = it))) }
                    NpcEditorTextField("Dados de golpe", combat.hitDice, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(hitDice = it))) }
                }
            }
            item { NpcEditorTextField("Velocidad", combat.speed) { onDraftChange(draft.copy(combat = combat.copy(speed = it))) } }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    NpcEditorTextField("FUE", combat.strength, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(strength = it))) }
                    NpcEditorTextField("DES", combat.dexterity, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(dexterity = it))) }
                    NpcEditorTextField("CON", combat.constitution, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(constitution = it))) }
                    NpcEditorTextField("INT", combat.intelligence, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(intelligence = it))) }
                    NpcEditorTextField("SAB", combat.wisdom, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(wisdom = it))) }
                    NpcEditorTextField("CAR", combat.charisma, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(charisma = it))) }
                    NpcEditorTextField("PB", combat.proficiencyBonus, Modifier.weight(1f), true) { onDraftChange(draft.copy(combat = combat.copy(proficiencyBonus = it))) }
                }
            }
            item { NpcEditorTextField("Tiradas de salvación", combat.savingThrows) { onDraftChange(draft.copy(combat = combat.copy(savingThrows = it))) } }
            item { NpcEditorTextField("Habilidades", combat.skills) { onDraftChange(draft.copy(combat = combat.copy(skills = it))) } }
            item { NpcEditorTextField("Vulnerabilidades al daño", combat.damageVulnerabilities) { onDraftChange(draft.copy(combat = combat.copy(damageVulnerabilities = it))) } }
            item { NpcEditorTextField("Resistencias al daño", combat.damageResistances) { onDraftChange(draft.copy(combat = combat.copy(damageResistances = it))) } }
            item { NpcEditorTextField("Inmunidades al daño", combat.damageImmunities) { onDraftChange(draft.copy(combat = combat.copy(damageImmunities = it))) } }
            item { NpcEditorTextField("Inmunidades a condiciones", combat.conditionImmunities) { onDraftChange(draft.copy(combat = combat.copy(conditionImmunities = it))) } }
            item { NpcEditorTextField("Sentidos", combat.senses) { onDraftChange(draft.copy(combat = combat.copy(senses = it))) } }
            item { NpcEditorTextField("Idiomas", combat.languages) { onDraftChange(draft.copy(combat = combat.copy(languages = it))) } }
            item { NpcEditorTextField("Rasgos", combat.traits, lines = 5) { onDraftChange(draft.copy(combat = combat.copy(traits = it))) } }
            item { NpcEditorTextField("Acciones", combat.actions, lines = 7) { onDraftChange(draft.copy(combat = combat.copy(actions = it))) } }
            item { NpcEditorTextField("Acciones adicionales", combat.bonusActions, lines = 4) { onDraftChange(draft.copy(combat = combat.copy(bonusActions = it))) } }
            item { NpcEditorTextField("Reacciones", combat.reactions, lines = 4) { onDraftChange(draft.copy(combat = combat.copy(reactions = it))) } }
            item { NpcEditorTextField("Acciones legendarias", combat.legendaryActions, lines = 5) { onDraftChange(draft.copy(combat = combat.copy(legendaryActions = it))) } }
            item { NpcEditorTextField("Acciones de guarida", combat.lairActions, lines = 5) { onDraftChange(draft.copy(combat = combat.copy(lairActions = it))) } }
            item { NpcEditorTextField("Tácticas", combat.tactics, lines = 5) { onDraftChange(draft.copy(combat = combat.copy(tactics = it))) } }
            item { NpcEditorTextField("Notas de combate", combat.notes, lines = 5) { onDraftChange(draft.copy(combat = combat.copy(notes = it))) } }
        }

        item { Button(onClick = onSave) { Text("Guardar cambios") } }
    }
}

@Composable
private fun NpcSectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun NpcEditorTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    singleLine: Boolean = false,
    lines: Int = 1,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = singleLine,
        minLines = if (singleLine) 1 else lines,
    )
}

private data class NpcDraft(
    val displayName: String,
    val conceptRole: String,
    val appearanceFirstImpression: String,
    val personalityManner: String,
    val wantsFearsNeeds: String,
    val canOffer: String,
    val limitsRefusals: String,
    val relationshipContext: String,
    val identityDetails: String,
    val voiceMannerisms: String,
    val motivations: String,
    val valuesBeliefs: String,
    val relationships: String,
    val history: String,
    val secrets: String,
    val knowledge: String,
    val goals: String,
    val resources: String,
    val affiliations: String,
    val places: String,
    val adventureSceneLinks: String,
    val dmGuidance: String,
    val notes: String,
    val combat: NpcCombatDraft?,
) {
    fun toPayload(): NpcPayload {
        require(displayName.trim().isNotEmpty()) { "El nombre del PNJ no puede quedar vacío." }
        return NpcPayload(
            conceptRole = conceptRole.trim(),
            appearanceFirstImpression = appearanceFirstImpression.trim(),
            personalityManner = personalityManner.trim(),
            wantsFearsNeeds = wantsFearsNeeds.trim(),
            canOffer = canOffer.trim(),
            limitsRefusals = limitsRefusals.trim(),
            relationshipContext = relationshipContext.trim(),
            identityDetails = identityDetails.trim(),
            voiceMannerisms = voiceMannerisms.trim(),
            motivations = motivations.trim(),
            valuesBeliefs = valuesBeliefs.trim(),
            relationships = relationships.trim(),
            history = history.trim(),
            secrets = secrets.trim(),
            knowledge = knowledge.trim(),
            goals = goals.trim(),
            resources = resources.trim(),
            affiliations = affiliations.trim(),
            places = places.trim(),
            adventureSceneLinks = adventureSceneLinks.trim(),
            dmGuidance = dmGuidance.trim(),
            combatMechanics = combat?.toPayload(),
            notes = notes.trim(),
        )
    }

    companion object {
        fun from(content: NpcContent): NpcDraft = content.payload.let { payload ->
            NpcDraft(
                displayName = content.item.displayName,
                conceptRole = payload.conceptRole,
                appearanceFirstImpression = payload.appearanceFirstImpression,
                personalityManner = payload.personalityManner,
                wantsFearsNeeds = payload.wantsFearsNeeds,
                canOffer = payload.canOffer,
                limitsRefusals = payload.limitsRefusals,
                relationshipContext = payload.relationshipContext,
                identityDetails = payload.identityDetails,
                voiceMannerisms = payload.voiceMannerisms,
                motivations = payload.motivations,
                valuesBeliefs = payload.valuesBeliefs,
                relationships = payload.relationships,
                history = payload.history,
                secrets = payload.secrets,
                knowledge = payload.knowledge,
                goals = payload.goals,
                resources = payload.resources,
                affiliations = payload.affiliations,
                places = payload.places,
                adventureSceneLinks = payload.adventureSceneLinks,
                dmGuidance = payload.dmGuidance,
                notes = payload.notes,
                combat = payload.combatMechanics?.let(NpcCombatDraft::from),
            )
        }
    }
}

private data class NpcCombatDraft(
    val size: String = "",
    val creatureType: String = "",
    val alignment: String = "",
    val armorClass: String = "",
    val armorClassDetails: String = "",
    val hitPoints: String = "",
    val hitDice: String = "",
    val speed: String = "",
    val strength: String = "10",
    val dexterity: String = "10",
    val constitution: String = "10",
    val intelligence: String = "10",
    val wisdom: String = "10",
    val charisma: String = "10",
    val savingThrows: String = "",
    val skills: String = "",
    val damageVulnerabilities: String = "",
    val damageResistances: String = "",
    val damageImmunities: String = "",
    val conditionImmunities: String = "",
    val senses: String = "",
    val languages: String = "",
    val challengeRating: String = "",
    val proficiencyBonus: String = "",
    val traits: String = "",
    val actions: String = "",
    val bonusActions: String = "",
    val reactions: String = "",
    val legendaryActions: String = "",
    val lairActions: String = "",
    val tactics: String = "",
    val notes: String = "",
) {
    fun toPayload(): CreaturePayload = CreaturePayload(
        size = size.trim(),
        creatureType = creatureType.trim(),
        alignment = alignment.trim(),
        armorClass = armorClass.npcOptionalPositiveInt("CA"),
        armorClassDetails = armorClassDetails.trim(),
        hitPoints = hitPoints.npcOptionalPositiveInt("PG"),
        hitDice = hitDice.trim(),
        speed = speed.trim(),
        abilityScores = CreatureAbilityScores(
            strength = strength.npcRequiredPositiveInt("FUE"),
            dexterity = dexterity.npcRequiredPositiveInt("DES"),
            constitution = constitution.npcRequiredPositiveInt("CON"),
            intelligence = intelligence.npcRequiredPositiveInt("INT"),
            wisdom = wisdom.npcRequiredPositiveInt("SAB"),
            charisma = charisma.npcRequiredPositiveInt("CAR"),
        ),
        savingThrows = savingThrows.trim(),
        skills = skills.trim(),
        damageVulnerabilities = damageVulnerabilities.trim(),
        damageResistances = damageResistances.trim(),
        damageImmunities = damageImmunities.trim(),
        conditionImmunities = conditionImmunities.trim(),
        senses = senses.trim(),
        languages = languages.trim(),
        challengeRating = challengeRating.trim(),
        proficiencyBonus = proficiencyBonus.npcOptionalNonNegativeInt("PB"),
        traits = traits.trim(),
        actions = actions.trim(),
        bonusActions = bonusActions.trim(),
        reactions = reactions.trim(),
        legendaryActions = legendaryActions.trim(),
        lairActions = lairActions.trim(),
        tactics = tactics.trim(),
        notes = notes.trim(),
    )

    companion object {
        fun from(payload: CreaturePayload): NpcCombatDraft = NpcCombatDraft(
            size = payload.size,
            creatureType = payload.creatureType,
            alignment = payload.alignment,
            armorClass = payload.armorClass?.toString().orEmpty(),
            armorClassDetails = payload.armorClassDetails,
            hitPoints = payload.hitPoints?.toString().orEmpty(),
            hitDice = payload.hitDice,
            speed = payload.speed,
            strength = payload.abilityScores.strength.toString(),
            dexterity = payload.abilityScores.dexterity.toString(),
            constitution = payload.abilityScores.constitution.toString(),
            intelligence = payload.abilityScores.intelligence.toString(),
            wisdom = payload.abilityScores.wisdom.toString(),
            charisma = payload.abilityScores.charisma.toString(),
            savingThrows = payload.savingThrows,
            skills = payload.skills,
            damageVulnerabilities = payload.damageVulnerabilities,
            damageResistances = payload.damageResistances,
            damageImmunities = payload.damageImmunities,
            conditionImmunities = payload.conditionImmunities,
            senses = payload.senses,
            languages = payload.languages,
            challengeRating = payload.challengeRating,
            proficiencyBonus = payload.proficiencyBonus?.toString().orEmpty(),
            traits = payload.traits,
            actions = payload.actions,
            bonusActions = payload.bonusActions,
            reactions = payload.reactions,
            legendaryActions = payload.legendaryActions,
            lairActions = payload.lairActions,
            tactics = payload.tactics,
            notes = payload.notes,
        )
    }
}

private fun NpcPayload.hasDevelopedDetails(): Boolean = listOf(
    identityDetails,
    voiceMannerisms,
    motivations,
    valuesBeliefs,
    relationships,
    history,
    secrets,
    knowledge,
    goals,
    resources,
    affiliations,
    places,
    adventureSceneLinks,
    dmGuidance,
).any { it.isNotBlank() }

private fun npcScopeSortKey(content: NpcContent): Int = when (content.item.identity.scope) {
    is ContentScope.Personal -> 0
    is ContentScope.Campaign -> 1
    else -> 2
}

private fun npcScopeLabel(content: NpcContent): String = npcScopeLabel(content.item.identity.scope)

private fun npcScopeLabel(scope: ContentScope): String = when (scope) {
    is ContentScope.Personal -> "De tu biblioteca anterior"
    is ContentScope.Campaign -> ""
    is ContentScope.Official -> "Oficial / SRD"
    ContentScope.System -> "Sistema"
}

private fun String.npcOptionalPositiveInt(label: String): Int? {
    val normalized = trim()
    if (normalized.isEmpty()) return null
    val value = normalized.toIntOrNull() ?: throw IllegalArgumentException("$label debe ser un número entero.")
    require(value > 0) { "$label debe ser positivo." }
    return value
}

private fun String.npcOptionalNonNegativeInt(label: String): Int? {
    val normalized = trim()
    if (normalized.isEmpty()) return null
    val value = normalized.toIntOrNull() ?: throw IllegalArgumentException("$label debe ser un número entero.")
    require(value >= 0) { "$label no puede ser negativo." }
    return value
}

private fun String.npcRequiredPositiveInt(label: String): Int {
    val value = trim().toIntOrNull() ?: throw IllegalArgumentException("$label debe ser un número entero.")
    require(value > 0) { "$label debe ser positivo." }
    return value
}
