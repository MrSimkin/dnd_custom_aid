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
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRuleContent
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRuleContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRuleLifecycle
import io.github.mrsimkin.dndcustomaid.shared.content.HomebrewRulePayload
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

private enum class DesktopAuthoringManagerSection(val label: String) {
    CREATURES_AND_NPCS("Criaturas / PNJ"),
    HOMEBREW_RULES("Homebrew / Reglas"),
}

@Composable
fun DesktopAuthoringManagersScreen(
    creatureController: DesktopCreatureManagerController,
    npcController: DesktopNpcManagerController,
    homebrewRuleController: DesktopHomebrewRuleManagerController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var section by remember { mutableStateOf(DesktopAuthoringManagerSection.CREATURES_AND_NPCS) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DesktopAuthoringManagerSection.entries.forEach { candidate ->
                if (candidate == section) {
                    Button(onClick = { section = candidate }) { Text(candidate.label) }
                } else {
                    TextButton(onClick = { section = candidate }) { Text(candidate.label) }
                }
            }
        }
        Divider()
        when (section) {
            DesktopAuthoringManagerSection.CREATURES_AND_NPCS -> DesktopManagersScreen(
                creatureController = creatureController,
                npcController = npcController,
                activeCampaign = activeCampaign,
                onQaEvent = onQaEvent,
            )
            DesktopAuthoringManagerSection.HOMEBREW_RULES -> HomebrewRuleManagerScreen(
                controller = homebrewRuleController,
                activeCampaign = activeCampaign,
                onQaEvent = onQaEvent,
            )
        }
    }
}

class DesktopHomebrewRuleManagerController(
    database: AppDatabase,
    private val nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    private val campaignRepository = CampaignRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val reusableContent = ReusableContentRepository(database)
    private val rules = HomebrewRuleContentRepository(database, reusableContent)

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

    fun personalRules(ownerAccountId: Uuid): List<HomebrewRuleContent> =
        reusableContent.listPersonal(ownerAccountId, ReusableContentFamily.HOMEBREW_RULE)
            .mapNotNull { rules.rule(it.identity.id) }

    fun campaignRules(campaignId: Uuid): List<HomebrewRuleContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.HOMEBREW_RULE)
            .mapNotNull { rules.rule(it.identity.id) }

    fun rule(id: Uuid): HomebrewRuleContent? = rules.rule(id)

    fun createPersonal(ownerAccountId: Uuid, displayName: String): HomebrewRuleContent =
        rules.createPersonal(
            ownerAccountId = ownerAccountId,
            rawDisplayName = displayName,
            payload = HomebrewRulePayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun createCampaign(campaignId: Uuid, displayName: String): HomebrewRuleContent =
        rules.createCampaign(
            campaignId = campaignId,
            rawDisplayName = displayName,
            payload = HomebrewRulePayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun update(
        id: Uuid,
        expectedRevision: Revision,
        displayName: String,
        payload: HomebrewRulePayload,
    ): RevisionDecision = rules.update(
        id = id,
        expectedRevision = expectedRevision,
        rawDisplayName = displayName,
        payload = payload,
        updatedAtEpochSeconds = nowEpochSeconds(),
    )

    fun copyPersonalToCampaign(sourceId: Uuid, campaignId: Uuid): HomebrewRuleContent =
        rules.copyPersonalToCampaign(
            sourceId = sourceId,
            campaignId = campaignId,
            copiedAtEpochSeconds = nowEpochSeconds(),
        )
}

@Composable
private fun HomebrewRuleManagerScreen(
    controller: DesktopHomebrewRuleManagerController,
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
    val personalRules = remember(refreshVersion, ownerAccountId) {
        ownerAccountId?.let(controller::personalRules).orEmpty()
    }
    val campaignRules = remember(refreshVersion, activeCampaign?.id) {
        activeCampaign?.id?.let(controller::campaignRules).orEmpty()
    }
    val selected = remember(refreshVersion, selectedId) {
        selectedId?.let(controller::rule)
    }
    var draft by remember(selected?.item?.identity?.id, selected?.item?.identity?.revision?.value) {
        mutableStateOf(selected?.let(HomebrewRuleDraft::from))
    }

    fun refresh(selectId: Uuid? = selectedId) {
        selectedId = selectId
        refreshVersion += 1
    }

    val normalizedQuery = query.trim().lowercase()
    val visibleRules = (personalRules + campaignRules)
        .distinctBy { it.item.identity.id }
        .filter { content ->
            normalizedQuery.isEmpty() || buildList {
                add(content.item.displayName)
                add(content.payload.summary)
                add(content.payload.body)
                add(content.payload.category)
                add(content.payload.rationale)
                add(content.payload.notes)
                addAll(content.payload.examples)
                addAll(content.payload.relatedReferences)
                addAll(content.payload.tags)
            }.any { it.lowercase().contains(normalizedQuery) }
        }
        .sortedWith(compareBy({ homebrewScopeSortKey(it) }, { it.item.displayName.lowercase() }))

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Gestor de Homebrew y Reglas", style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
            Text(
                "Reglas ligeras locales con ciclo Borrador / Activa / Retirada. Las copias de campaña son independientes.",
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
                label = { Text("Nueva regla") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Button(
                enabled = newName.isNotBlank() && ownerAccountId != null,
                onClick = {
                    runCatching { controller.createPersonal(requireNotNull(ownerAccountId), newName) }
                        .onSuccess { created ->
                            newName = ""
                            statusMessage = "Regla Personal creada."
                            onQaEvent("Homebrew Manager: regla Personal creada ${created.item.identity.id}")
                            refresh(created.item.identity.id)
                        }
                        .onFailure { statusMessage = it.message ?: "No se pudo crear la regla Personal." }
                },
            ) { Text("Crear Personal") }
            Button(
                enabled = newName.isNotBlank() && activeCampaign != null,
                onClick = {
                    runCatching { controller.createCampaign(requireNotNull(activeCampaign).id, newName) }
                        .onSuccess { created ->
                            newName = ""
                            statusMessage = "Regla creada en ${activeCampaign?.name}."
                            onQaEvent("Homebrew Manager: regla de campaña creada ${created.item.identity.id}")
                            refresh(created.item.identity.id)
                        }
                        .onFailure { statusMessage = it.message ?: "No se pudo crear la regla de campaña." }
                },
            ) { Text("Crear en campaña") }
        }

        if (ownerAccountId == null) {
            Text(
                "Biblioteca Personal no disponible: Desktop no puede identificar de forma unívoca una cuenta DM local. " +
                    "El contenido de campaña local sigue disponible.",
                style = MaterialTheme.typography.caption,
            )
        }
        statusMessage?.let { Text(it, style = MaterialTheme.typography.caption) }

        Divider()

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HomebrewRuleList(
                rules = visibleRules,
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
                    Text("Selecciona una regla para abrirla.", style = MaterialTheme.typography.h6)
                    Text("Personal: ${personalRules.size} • Campaña activa: ${campaignRules.size}")
                }
            } else {
                HomebrewRuleEditor(
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
                                onQaEvent("Homebrew Manager: regla actualizada ${selected.item.identity.id}")
                                refresh(selected.item.identity.id)
                            }
                            is RevisionDecision.Stale -> {
                                statusMessage = "La regla cambió desde que fue abierta. Se recargó la versión actual."
                                refresh(selected.item.identity.id)
                            }
                            is RevisionDecision.Deleted -> {
                                statusMessage = "La regla fue eliminada y no puede ser sobrescrita."
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
                                    onQaEvent("Homebrew Manager: regla copiada a campaña ${copied.item.identity.id}")
                                    refresh(copied.item.identity.id)
                                }
                                .onFailure { statusMessage = it.message ?: "No se pudo copiar la regla." }
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun HomebrewRuleList(
    rules: List<HomebrewRuleContent>,
    selectedId: Uuid?,
    onSelect: (Uuid) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item {
            Text("Reglas (${rules.size})", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
        }
        if (rules.isEmpty()) {
            item { Text("No hay reglas que coincidan con el filtro.", style = MaterialTheme.typography.caption) }
        }
        items(rules, key = { it.item.identity.id.toString() }) { content ->
            val selected = content.item.identity.id == selectedId
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(content.item.identity.id) },
                elevation = if (selected) 6.dp else 1.dp,
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(content.item.displayName, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    Text(homebrewScopeLabel(content), style = MaterialTheme.typography.caption)
                    Text(homebrewLifecycleLabel(content.payload.lifecycle), style = MaterialTheme.typography.caption)
                    if (content.payload.category.isNotBlank()) {
                        Text(content.payload.category, style = MaterialTheme.typography.caption)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomebrewRuleEditor(
    content: HomebrewRuleContent,
    draft: HomebrewRuleDraft,
    activeCampaign: Campaign?,
    onDraftChange: (HomebrewRuleDraft) -> Unit,
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
                    Text("Editor de Homebrew / Regla", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
                    Text(
                        "${homebrewScopeLabel(content)} • revisión ${content.item.identity.revision.value}",
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

        item {
            HomebrewField("Nombre", draft.displayName, true) { onDraftChange(draft.copy(displayName = it)) }
        }
        item {
            Text("Estado", style = MaterialTheme.typography.subtitle2, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                HomebrewRuleLifecycle.entries.forEach { lifecycle ->
                    if (draft.lifecycle == lifecycle) {
                        Button(onClick = { onDraftChange(draft.copy(lifecycle = lifecycle)) }) {
                            Text(homebrewLifecycleLabel(lifecycle))
                        }
                    } else {
                        TextButton(onClick = { onDraftChange(draft.copy(lifecycle = lifecycle)) }) {
                            Text(homebrewLifecycleLabel(lifecycle))
                        }
                    }
                }
            }
        }
        item { HomebrewField("Resumen / regla concisa", draft.summary, false, 3) { onDraftChange(draft.copy(summary = it)) } }
        item { HomebrewField("Texto de regla / explicación", draft.body, false, 6) { onDraftChange(draft.copy(body = it)) } }
        item { HomebrewField("Categoría", draft.category, true) { onDraftChange(draft.copy(category = it)) } }
        item { HomebrewField("Racional", draft.rationale, false, 4) { onDraftChange(draft.copy(rationale = it)) } }
        item {
            HomebrewField("Ejemplos (uno por línea)", draft.examples, false, 4) {
                onDraftChange(draft.copy(examples = it))
            }
        }
        item {
            HomebrewField("Referencias relacionadas (una por línea)", draft.relatedReferences, false, 4) {
                onDraftChange(draft.copy(relatedReferences = it))
            }
        }
        item {
            HomebrewField("Etiquetas (coma o línea)", draft.tags, false, 3) {
                onDraftChange(draft.copy(tags = it))
            }
        }
        item { HomebrewField("Notas DM", draft.notes, false, 4) { onDraftChange(draft.copy(notes = it)) } }
    }
}

@Composable
private fun HomebrewField(
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

private data class HomebrewRuleDraft(
    val displayName: String,
    val summary: String,
    val body: String,
    val category: String,
    val rationale: String,
    val examples: String,
    val relatedReferences: String,
    val tags: String,
    val lifecycle: HomebrewRuleLifecycle,
    val notes: String,
) {
    fun toPayload(): HomebrewRulePayload = HomebrewRulePayload(
        summary = summary,
        body = body,
        category = category,
        rationale = rationale,
        examples = parseLines(examples),
        relatedReferences = parseLines(relatedReferences),
        tags = tags.split(',', '\n').map(String::trim).filter(String::isNotEmpty),
        lifecycle = lifecycle,
        notes = notes,
    )

    companion object {
        fun from(content: HomebrewRuleContent): HomebrewRuleDraft = HomebrewRuleDraft(
            displayName = content.item.displayName,
            summary = content.payload.summary,
            body = content.payload.body,
            category = content.payload.category,
            rationale = content.payload.rationale,
            examples = content.payload.examples.joinToString("\n"),
            relatedReferences = content.payload.relatedReferences.joinToString("\n"),
            tags = content.payload.tags.joinToString(", "),
            lifecycle = content.payload.lifecycle,
            notes = content.payload.notes,
        )
    }
}

private fun parseLines(raw: String): List<String> =
    raw.lines().map(String::trim).filter(String::isNotEmpty)

private fun homebrewScopeSortKey(content: HomebrewRuleContent): Int = when (content.item.identity.scope) {
    is ContentScope.Personal -> 0
    is ContentScope.Campaign -> 1
    else -> 2
}

private fun homebrewScopeLabel(content: HomebrewRuleContent): String = homebrewScopeLabel(content.item.identity.scope)

private fun homebrewScopeLabel(scope: ContentScope): String = when (scope) {
    is ContentScope.Personal -> "Personal"
    is ContentScope.Campaign -> "Campaña"
    is ContentScope.Official -> "Oficial / SRD"
    ContentScope.System -> "Sistema"
}

private fun homebrewLifecycleLabel(lifecycle: HomebrewRuleLifecycle): String = when (lifecycle) {
    HomebrewRuleLifecycle.DRAFT -> "Borrador"
    HomebrewRuleLifecycle.ACTIVE -> "Activa"
    HomebrewRuleLifecycle.RETIRED -> "Retirada"
}
