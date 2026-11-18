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
    PLACES_SHOPS("Lugares y tiendas"),
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
        HorizontalDivider()
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
    // Owner point 10: the card first, the complete preparation only when asked for.
    var openBlock by remember(selected?.item?.identity?.id) { mutableStateOf<String?>(null) }
    var showAll by remember(selected?.item?.identity?.id) { mutableStateOf(false) }
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
            ) { Text("＋ Crear") }
        }

        statusMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall) }

        HorizontalDivider()

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
            VerticalDivider()
            if (selected == null || draft == null) {
                Column(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Selecciona una escena para abrirla.", style = MaterialTheme.typography.titleLarge)
                    Text("${campaignScenes.size} en esta campaña.")
                }
            } else {
                SceneEditor(
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
            Text("Escenas (${scenes.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        if (scenes.isEmpty()) {
            item { Text("No hay escenas que coincidan con el filtro.", style = MaterialTheme.typography.bodySmall) }
        }
        items(scenes, key = { it.item.identity.id.toString() }) { content ->
            val selected = content.item.identity.id == selectedId
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(content.item.identity.id) },
                elevation = androidx.compose.material3.CardDefaults.cardElevation(if (selected) 6.dp else 1.dp),
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(content.item.displayName, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    sceneScopeLabel(content).takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    if (content.payload.purpose.isNotBlank()) {
                        Text(content.payload.purpose, style = MaterialTheme.typography.bodySmall)
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
                    Text("Editor de Escena", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                if (content.item.identity.scope is ContentScope.Personal && activeCampaign != null) {
                    Button(onClick = onCopyToCampaign) { Text("Traer a esta campaña") }
                }
                Button(onClick = onSave, enabled = draft.displayName.isNotBlank()) { Text("Guardar") }
            }
        }

        // Owner points 6 and 10: what you will read at the table first, the whole preparation after.
        item {
            val readAloud = draft.sections[io.github.mrsimkin.dndcustomaid.shared.content.SceneSection.READ_ALOUD_SHORT.name].orEmpty()
                .ifBlank { draft.sections[io.github.mrsimkin.dndcustomaid.shared.content.SceneSection.READ_ALOUD.name].orEmpty() }
            SheetBlock(
                title = "En la mesa",
                editing = openBlock == "mesa",
                onEdit = { onOpenBlock("mesa") },
                onDone = { onOpenBlock(null) },
            ) {
                if (openBlock == "mesa") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SceneField("Título", draft.displayName, true) { onDraftChange(draft.copy(displayName = it)) }
                        SceneField("Para qué sirve (una o dos líneas)", draft.purpose, false, 2) { onDraftChange(draft.copy(purpose = it)) }
                        SceneField(
                            "Para leer en voz alta · 30 s",
                            draft.sections[io.github.mrsimkin.dndcustomaid.shared.content.SceneSection.READ_ALOUD_SHORT.name].orEmpty(),
                            false,
                            3,
                        ) { text ->
                            onDraftChange(draft.copy(sections = draft.sections + (io.github.mrsimkin.dndcustomaid.shared.content.SceneSection.READ_ALOUD_SHORT.name to text)))
                        }
                    }
                } else {
                    Text(draft.displayName.ifBlank { "Sin título" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    SheetLine("Para qué sirve", draft.purpose)
                    SheetLine("Para leer en voz alta", readAloud)
                    if (draft.purpose.isBlank() && readAloud.isBlank()) SheetEmpty("Todavía sin nada que leer en la mesa. Toca «Editar».")
                }
            }
        }
        item {
            val truth = draft.sections[io.github.mrsimkin.dndcustomaid.shared.content.SceneSection.TRUTH.name].orEmpty()
            val beats = draft.sections[io.github.mrsimkin.dndcustomaid.shared.content.SceneSection.BEATS.name].orEmpty()
            val next = draft.sections[io.github.mrsimkin.dndcustomaid.shared.content.SceneSection.NEXT.name].orEmpty()
            SheetBlock(
                title = "Qué pasa de verdad",
                editing = openBlock == "verdad",
                onEdit = { onOpenBlock("verdad") },
                onDone = { onOpenBlock(null) },
            ) {
                if (openBlock == "verdad") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            io.github.mrsimkin.dndcustomaid.shared.content.SceneSection.TRUTH,
                            io.github.mrsimkin.dndcustomaid.shared.content.SceneSection.BEATS,
                            io.github.mrsimkin.dndcustomaid.shared.content.SceneSection.NEXT,
                        ).forEach { section ->
                            SceneField(section.label, draft.sections[section.name].orEmpty(), false, 3) { text ->
                                onDraftChange(draft.copy(sections = draft.sections + (section.name to text)))
                            }
                        }
                    }
                } else {
                    SheetLine("Qué está pasando", truth)
                    SheetLine("Momentos", beats)
                    SheetLine("Conecta con", next)
                    if (listOf(truth, beats, next).all { it.isBlank() }) SheetEmpty("Sin la trama anotada.")
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

        item { SceneField("Título", draft.displayName, true) { onDraftChange(draft.copy(displayName = it)) } }
        item { SceneField("Para qué sirve (una o dos líneas)", draft.purpose, false, 2) { onDraftChange(draft.copy(purpose = it)) } }
        // QA 0.7.0-preqa.2 (R4): every part of the scene has its own space.
        io.github.mrsimkin.dndcustomaid.shared.content.SceneSection.entries.forEach { section ->
            item(key = "section-${section.name}") {
                SceneField(
                    section.label + if (section.forPlayers) " · para los jugadores" else "",
                    draft.sections[section.name].orEmpty(),
                    false,
                    3,
                ) { text -> onDraftChange(draft.copy(sections = draft.sections + (section.name to text))) }
            }
        }
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
        item { DesktopContentLinksSection(content.item) }
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
    val sections: Map<String, String> = emptyMap(),
) {
    fun toPayload(): ScenePayload = ScenePayload(
        purpose = purpose,
        possibleNextScenes = parseSceneLines(possibleNextScenes),
        references = parseSceneLines(references),
        notes = notes,
        sections = sections.mapValues { it.value.trim() }.filterValues { it.isNotEmpty() },
    )

    companion object {
        fun from(content: SceneContent): SceneDraft = SceneDraft(
            displayName = content.item.displayName,
            purpose = content.payload.purpose,
            possibleNextScenes = content.payload.possibleNextScenes.joinToString("\n"),
            references = content.payload.references.joinToString("\n"),
            notes = content.payload.notes,
            sections = content.payload.sections,
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
    is ContentScope.Personal -> "De tu biblioteca anterior"
    is ContentScope.Campaign -> ""
    is ContentScope.Official -> "Oficial / SRD"
    ContentScope.System -> "Sistema"
}
