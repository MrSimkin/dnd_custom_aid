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
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContent
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ScenePayload
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.Revision
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlin.uuid.Uuid

private enum class DesktopStageContentSection(val label: String) {
    PLACES_SHOPS("Stage • Lugares / Tiendas"),
    SCENES("Aventura / Escenas"),
}

@Composable
internal fun DesktopStageAndSceneManagerScreen(
    controller: DesktopPlaceManagerController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var section by remember { mutableStateOf(DesktopStageContentSection.PLACES_SHOPS) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DesktopStageContentSection.entries.forEach { candidate ->
                if (candidate == section) {
                    Button(onClick = { section = candidate }) { Text(candidate.label) }
                } else {
                    TextButton(onClick = { section = candidate }) { Text(candidate.label) }
                }
            }
        }
        Divider()
        when (section) {
            DesktopStageContentSection.PLACES_SHOPS -> DesktopPlaceManagerScreen(
                controller = controller,
                activeCampaign = activeCampaign,
                onQaEvent = onQaEvent,
            )
            DesktopStageContentSection.SCENES -> DesktopSceneManagerScreen(
                controller = controller.sceneManagerController,
                activeCampaign = activeCampaign,
                onQaEvent = onQaEvent,
            )
        }
    }
}

class DesktopSceneManagerController(
    database: AppDatabase,
    private val nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    private val campaignRepository = CampaignRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val reusableContent = ReusableContentRepository(database)
    private val scenes = SceneContentRepository(database, reusableContent)

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

    fun personalScenes(ownerAccountId: Uuid): List<SceneContent> =
        reusableContent.listPersonal(ownerAccountId, ReusableContentFamily.SCENE)
            .mapNotNull { scenes.scene(it.identity.id) }

    fun campaignScenes(campaignId: Uuid): List<SceneContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.SCENE)
            .mapNotNull { scenes.scene(it.identity.id) }

    fun scene(id: Uuid): SceneContent? = scenes.scene(id)

    fun createPersonal(ownerAccountId: Uuid, displayName: String): SceneContent =
        scenes.createPersonal(
            ownerAccountId = ownerAccountId,
            rawDisplayName = displayName,
            payload = ScenePayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun createCampaign(campaignId: Uuid, displayName: String): SceneContent =
        scenes.createCampaign(
            campaignId = campaignId,
            rawDisplayName = displayName,
            payload = ScenePayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun update(
        id: Uuid,
        expectedRevision: Revision,
        displayName: String,
        payload: ScenePayload,
    ): RevisionDecision = scenes.update(
        id = id,
        expectedRevision = expectedRevision,
        rawDisplayName = displayName,
        payload = payload,
        updatedAtEpochSeconds = nowEpochSeconds(),
    )

    fun copyPersonalToCampaign(sourceId: Uuid, campaignId: Uuid): SceneContent =
        scenes.copyPersonalToCampaign(
            sourceId = sourceId,
            campaignId = campaignId,
            copiedAtEpochSeconds = nowEpochSeconds(),
        )
}

@Composable
internal fun DesktopSceneManagerScreen(
    controller: DesktopSceneManagerController,
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
    val personalScenes = remember(refreshVersion, ownerAccountId) {
        ownerAccountId?.let(controller::personalScenes).orEmpty()
    }
    val campaignScenes = remember(refreshVersion, activeCampaign?.id) {
        activeCampaign?.id?.let(controller::campaignScenes).orEmpty()
    }
    val selected = remember(refreshVersion, selectedId) {
        selectedId?.let(controller::scene)
    }
    var draft by remember(selected?.item?.identity?.id, selected?.item?.identity?.revision?.value) {
        mutableStateOf(selected?.let(SceneDraft::from))
    }

    fun refresh(selectId: Uuid? = selectedId) {
        selectedId = selectId
        refreshVersion += 1
    }

    val normalizedQuery = query.trim().lowercase()
    val visibleScenes = (personalScenes + campaignScenes)
        .distinctBy { it.item.identity.id }
        .filter { content ->
            normalizedQuery.isEmpty() || buildList {
                add(content.item.displayName)
                add(content.payload.purpose)
                add(content.payload.notes)
                addAll(content.payload.possibleNextScenes)
                addAll(content.payload.references)
            }.any { it.lowercase().contains(normalizedQuery) }
        }
        .sortedWith(compareBy({ sceneScopeSortKey(it) }, { it.item.displayName.lowercase() }))

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Adventure / Scene Spine", style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
            Text(
                "Orientación ligera: propósito, posibles escenas siguientes y referencias. No es un motor de quests ni de estado vivo.",
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
                label = { Text("Nueva escena") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Button(
                enabled = newName.isNotBlank() && ownerAccountId != null,
                onClick = {
                    runCatching { controller.createPersonal(requireNotNull(ownerAccountId), newName) }
                        .onSuccess { created ->
                            newName = ""
                            statusMessage = "Escena Personal creada."
                            onQaEvent("Scene Manager: escena Personal creada ${created.item.identity.id}")
                            refresh(created.item.identity.id)
                        }
                        .onFailure { statusMessage = it.message ?: "No se pudo crear la escena Personal." }
                },
            ) { Text("Crear Personal") }
            Button(
                enabled = newName.isNotBlank() && activeCampaign != null,
                onClick = {
                    runCatching { controller.createCampaign(requireNotNull(activeCampaign).id, newName) }
                        .onSuccess { created ->
                            newName = ""
                            statusMessage = "Escena creada en ${activeCampaign?.name}."
                            onQaEvent("Scene Manager: escena de campaña creada ${created.item.identity.id}")
                            refresh(created.item.identity.id)
                        }
                        .onFailure { statusMessage = it.message ?: "No se pudo crear la escena de campaña." }
                },
            ) { Text("Crear en campaña") }
        }

        if (ownerAccountId == null) {
            Text(
                "Biblioteca Personal no disponible: Desktop no puede identificar de forma unívoca una cuenta DM local. " +
                    "Las escenas de campaña local siguen disponibles.",
                style = MaterialTheme.typography.caption,
            )
        }
        statusMessage?.let { Text(it, style = MaterialTheme.typography.caption) }

        Divider()

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SceneList(
                scenes = visibleScenes,
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
                    Text("Selecciona una escena para abrirla.", style = MaterialTheme.typography.h6)
                    Text("Personal: ${personalScenes.size} • Campaña activa: ${campaignScenes.size}")
                }
            } else {
                SceneEditor(
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
                                onQaEvent("Scene Manager: escena actualizada ${selected.item.identity.id}")
                                refresh(selected.item.identity.id)
                            }
                            is RevisionDecision.Stale -> {
                                statusMessage = "La escena cambió desde que fue abierta. Se recargó la versión actual."
                                refresh(selected.item.identity.id)
                            }
                            is RevisionDecision.Deleted -> {
                                statusMessage = "La escena fue eliminada y no puede ser sobrescrita."
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
                                    onQaEvent("Scene Manager: escena copiada a campaña ${copied.item.identity.id}")
                                    refresh(copied.item.identity.id)
                                }
                                .onFailure { statusMessage = it.message ?: "No se pudo copiar la escena." }
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun SceneList(
    scenes: List<SceneContent>,
    selectedId: Uuid?,
    onSelect: (Uuid) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item {
            Text("Escenas (${scenes.size})", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
        }
        if (scenes.isEmpty()) {
            item { Text("No hay escenas que coincidan con el filtro.", style = MaterialTheme.typography.caption) }
        }
        items(scenes, key = { it.item.identity.id.toString() }) { content ->
            val selected = content.item.identity.id == selectedId
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(content.item.identity.id) },
                elevation = if (selected) 6.dp else 1.dp,
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(content.item.displayName, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    Text(sceneScopeLabel(content), style = MaterialTheme.typography.caption)
                    if (content.payload.purpose.isNotBlank()) {
                        Text(content.payload.purpose, style = MaterialTheme.typography.caption)
                    }
                }
            }
        }
    }
}

@Composable
private fun SceneEditor(
    content: SceneContent,
    draft: SceneDraft,
    activeCampaign: Campaign?,
    onDraftChange: (SceneDraft) -> Unit,
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
                    Text("Editor de Escena", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
                    Text(
                        "${sceneScopeLabel(content)} • revisión ${content.item.identity.revision.value}",
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

        item { SceneField("Título", draft.displayName, true) { onDraftChange(draft.copy(displayName = it)) } }
        item { SceneField("Propósito", draft.purpose, false, 4) { onDraftChange(draft.copy(purpose = it)) } }
        item {
            SceneField(
                "Posibles escenas siguientes (una por línea)",
                draft.possibleNextScenes,
                false,
                5,
            ) { onDraftChange(draft.copy(possibleNextScenes = it)) }
        }
        item {
            SceneField(
                "Referencias / enlaces de preparación (una por línea)",
                draft.references,
                false,
                5,
            ) { onDraftChange(draft.copy(references = it)) }
        }
        item { SceneField("Notas DM", draft.notes, false, 5) { onDraftChange(draft.copy(notes = it)) } }
    }
}

@Composable
private fun SceneField(
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

private data class SceneDraft(
    val displayName: String,
    val purpose: String,
    val possibleNextScenes: String,
    val references: String,
    val notes: String,
) {
    fun toPayload(): ScenePayload = ScenePayload(
        purpose = purpose,
        possibleNextScenes = parseSceneLines(possibleNextScenes),
        references = parseSceneLines(references),
        notes = notes,
    )

    companion object {
        fun from(content: SceneContent): SceneDraft = SceneDraft(
            displayName = content.item.displayName,
            purpose = content.payload.purpose,
            possibleNextScenes = content.payload.possibleNextScenes.joinToString("\n"),
            references = content.payload.references.joinToString("\n"),
            notes = content.payload.notes,
        )
    }
}

private fun parseSceneLines(raw: String): List<String> =
    raw.lines().map(String::trim).filter(String::isNotEmpty)

private fun sceneScopeSortKey(content: SceneContent): Int = when (content.item.identity.scope) {
    is ContentScope.Personal -> 0
    is ContentScope.Campaign -> 1
    else -> 2
}

private fun sceneScopeLabel(content: SceneContent): String = when (content.item.identity.scope) {
    is ContentScope.Personal -> "Personal"
    is ContentScope.Campaign -> "Campaña"
    is ContentScope.Official -> "Oficial / SRD"
    ContentScope.System -> "Sistema"
}
