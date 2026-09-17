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
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureAbilityScores
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContent
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
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

class DesktopCreatureManagerController(
    database: AppDatabase,
    private val nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    private val campaignRepository = CampaignRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val reusableContent = ReusableContentRepository(database)
    private val creatures = CreatureContentRepository(database, reusableContent)

    fun personalOwnerAccountId(): Uuid? {
        val activeCampaignId = campaignRepository.activeCampaign()?.id
        val campaignIds = buildList {
            activeCampaignId?.let(::add)
            campaignRepository.listCampaigns().forEach { campaign ->
                if (campaign.id != activeCampaignId) add(campaign.id)
            }
        }

        val activeDmIds = campaignIds
            .flatMap(spine::memberships)
            .asSequence()
            .filter { membership ->
                membership.role == CampaignRole.DM &&
                    membership.status == CampaignMembershipStatus.ACTIVE
            }
            .map { it.accountId }
            .distinct()
            .toList()

        return activeDmIds.singleOrNull()
    }

    fun personalCreatures(ownerAccountId: Uuid): List<CreatureContent> =
        reusableContent.listPersonal(ownerAccountId, ReusableContentFamily.CREATURE)
            .mapNotNull { creatures.creature(it.identity.id) }

    fun campaignCreatures(campaignId: Uuid): List<CreatureContent> =
        reusableContent.listCampaign(campaignId, ReusableContentFamily.CREATURE)
            .mapNotNull { creatures.creature(it.identity.id) }

    fun creature(id: Uuid): CreatureContent? = creatures.creature(id)

    fun createPersonal(ownerAccountId: Uuid, displayName: String): CreatureContent =
        creatures.createPersonal(
            ownerAccountId = ownerAccountId,
            rawDisplayName = displayName,
            payload = CreaturePayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun createCampaign(campaignId: Uuid, displayName: String): CreatureContent =
        creatures.createCampaign(
            campaignId = campaignId,
            rawDisplayName = displayName,
            payload = CreaturePayload(),
            nowEpochSeconds = nowEpochSeconds(),
        )

    fun update(
        id: Uuid,
        expectedRevision: Revision,
        displayName: String,
        payload: CreaturePayload,
    ): RevisionDecision = creatures.update(
        id = id,
        expectedRevision = expectedRevision,
        rawDisplayName = displayName,
        payload = payload,
        updatedAtEpochSeconds = nowEpochSeconds(),
    )

    fun copyPersonalToCampaign(sourceId: Uuid, campaignId: Uuid): CreatureContent =
        creatures.copyPersonalToCampaign(
            sourceId = sourceId,
            campaignId = campaignId,
            copiedAtEpochSeconds = nowEpochSeconds(),
        )
}

@Composable
fun CreatureManagerScreen(
    controller: DesktopCreatureManagerController,
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
    val personalCreatures = remember(refreshVersion, ownerAccountId) {
        ownerAccountId?.let(controller::personalCreatures).orEmpty()
    }
    val campaignCreatures = remember(refreshVersion, activeCampaign?.id) {
        activeCampaign?.id?.let(controller::campaignCreatures).orEmpty()
    }
    val selected = remember(refreshVersion, selectedId) {
        selectedId?.let(controller::creature)
    }
    var draft by remember(selected?.item?.identity?.id, selected?.item?.identity?.revision?.value) {
        mutableStateOf(selected?.let(CreatureDraft::from))
    }

    fun refresh(selectId: Uuid? = selectedId) {
        selectedId = selectId
        refreshVersion += 1
    }

    val normalizedQuery = query.trim().lowercase()
    val visibleCreatures = (personalCreatures + campaignCreatures)
        .distinctBy { it.item.identity.id }
        .filter { content ->
            normalizedQuery.isEmpty() || listOf(
                content.item.displayName,
                content.payload.creatureType,
                content.payload.size,
                content.payload.challengeRating,
                content.payload.tagsForSearch(),
            ).any { it.lowercase().contains(normalizedQuery) }
        }
        .sortedWith(compareBy({ scopeSortKey(it) }, { it.item.displayName.lowercase() }))

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Gestor de criaturas / monstruos", style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
            Text(
                "Biblioteca local Personal y de la campaña activa. Los cambios de campaña son independientes del original Personal.",
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
                label = { Text("Nueva criatura") },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Button(
                enabled = newName.isNotBlank() && ownerAccountId != null,
                onClick = {
                    runCatching {
                        controller.createPersonal(requireNotNull(ownerAccountId), newName)
                    }.onSuccess { created ->
                        newName = ""
                        statusMessage = "Criatura Personal creada."
                        onQaEvent("Creature Manager: criatura Personal creada ${created.item.identity.id}")
                        refresh(created.item.identity.id)
                    }.onFailure { error -> statusMessage = error.message ?: "No se pudo crear la criatura Personal." }
                },
            ) { Text("Crear Personal") }
            Button(
                enabled = newName.isNotBlank() && activeCampaign != null,
                onClick = {
                    runCatching {
                        controller.createCampaign(requireNotNull(activeCampaign).id, newName)
                    }.onSuccess { created ->
                        newName = ""
                        statusMessage = "Criatura creada en ${activeCampaign?.name}."
                        onQaEvent("Creature Manager: criatura de campaña creada ${created.item.identity.id}")
                        refresh(created.item.identity.id)
                    }.onFailure { error -> statusMessage = error.message ?: "No se pudo crear la criatura de campaña." }
                },
            ) { Text("Crear en campaña") }
        }

        if (ownerAccountId == null) {
            Text(
                "Biblioteca Personal no disponible: Desktop no puede identificar de forma unívoca una cuenta DM local. " +
                    "Inicia/sincroniza una campaña alojada con tu cuenta DM para establecer esa identidad. " +
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
            CreatureList(
                creatures = visibleCreatures,
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
                    Text("Selecciona una criatura para abrirla.", style = MaterialTheme.typography.h6)
                    Text("Personal: ${personalCreatures.size} • Campaña activa: ${campaignCreatures.size}")
                }
            } else {
                CreatureEditor(
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
                                        statusMessage = "Cambios guardados (revisión ${decision.nextRevision.value})."
                                        onQaEvent("Creature Manager: criatura actualizada ${selected.item.identity.id}")
                                        refresh(selected.item.identity.id)
                                    }
                                    is RevisionDecision.Stale -> {
                                        statusMessage = "La criatura cambió desde que fue abierta. Se recargó la versión actual."
                                        refresh(selected.item.identity.id)
                                    }
                                    is RevisionDecision.Deleted -> {
                                        statusMessage = "La criatura fue eliminada y no puede ser sobrescrita."
                                        refresh(null)
                                    }
                                }
                            }
                            .onFailure { error -> statusMessage = error.message ?: "Datos de criatura inválidos." }
                    },
                    onCopyToCampaign = {
                        val campaign = activeCampaign
                        if (campaign == null) {
                            statusMessage = "Selecciona una campaña activa antes de copiar."
                        } else {
                            runCatching {
                                controller.copyPersonalToCampaign(selected.item.identity.id, campaign.id)
                            }.onSuccess { copied ->
                                statusMessage = "Copia independiente creada en ${campaign.name}."
                                onQaEvent("Creature Manager: criatura copiada a campaña ${copied.item.identity.id}")
                                refresh(copied.item.identity.id)
                            }.onFailure { error -> statusMessage = error.message ?: "No se pudo copiar la criatura." }
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun CreatureList(
    creatures: List<CreatureContent>,
    selectedId: Uuid?,
    onSelect: (Uuid) -> Unit,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item {
            Text("Criaturas (${creatures.size})", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
        }
        if (creatures.isEmpty()) {
            item { Text("No hay criaturas que coincidan con el filtro.", style = MaterialTheme.typography.caption) }
        }
        items(creatures, key = { it.item.identity.id.toString() }) { content ->
            val selected = content.item.identity.id == selectedId
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onSelect(content.item.identity.id) },
                elevation = if (selected) 6.dp else 1.dp,
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(content.item.displayName, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    Text(scopeLabel(content), style = MaterialTheme.typography.caption)
                    val detail = listOf(
                        content.payload.size,
                        content.payload.creatureType,
                        content.payload.challengeRating.takeIf { it.isNotBlank() }?.let { "CR $it" }.orEmpty(),
                    ).filter { it.isNotBlank() }.joinToString(" • ")
                    if (detail.isNotBlank()) Text(detail, style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}

@Composable
private fun CreatureEditor(
    content: CreatureContent,
    draft: CreatureDraft,
    activeCampaign: Campaign?,
    onDraftChange: (CreatureDraft) -> Unit,
    onSave: () -> Unit,
    onCopyToCampaign: () -> Unit,
    modifier: Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Editor de criatura", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
                    Text(scopeLabel(content), style = MaterialTheme.typography.caption)
                    content.item.identity.provenance?.let { provenance ->
                        Text(
                            "Proveniencia: ${provenance.sourceScope.displayLabel()} • ${provenance.sourceObjectId.toString().take(8)}…",
                            style = MaterialTheme.typography.caption,
                        )
                    }
                    Text("Revisión ${content.item.identity.revision.value}", style = MaterialTheme.typography.caption)
                }
                Button(onClick = onSave) { Text("Guardar") }
                if (content.item.identity.scope is ContentScope.Personal) {
                    Button(onClick = onCopyToCampaign, enabled = activeCampaign != null) {
                        Text("Usar en campaña")
                    }
                }
            }
        }

        item { EditorTextField("Nombre", draft.displayName) { onDraftChange(draft.copy(displayName = it)) } }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EditorTextField("Tamaño", draft.size, Modifier.weight(1f)) { onDraftChange(draft.copy(size = it)) }
                EditorTextField("Tipo", draft.creatureType, Modifier.weight(1f)) { onDraftChange(draft.copy(creatureType = it)) }
                EditorTextField("Alineamiento", draft.alignment, Modifier.weight(1f)) { onDraftChange(draft.copy(alignment = it)) }
                EditorTextField("CR", draft.challengeRating, Modifier.weight(1f)) { onDraftChange(draft.copy(challengeRating = it)) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                EditorTextField("CA", draft.armorClass, Modifier.weight(1f), true) { onDraftChange(draft.copy(armorClass = it)) }
                EditorTextField("Detalle CA", draft.armorClassDetails, Modifier.weight(2f)) { onDraftChange(draft.copy(armorClassDetails = it)) }
                EditorTextField("PG", draft.hitPoints, Modifier.weight(1f), true) { onDraftChange(draft.copy(hitPoints = it)) }
                EditorTextField("Dados de golpe", draft.hitDice, Modifier.weight(1f)) { onDraftChange(draft.copy(hitDice = it)) }
            }
        }
        item { EditorTextField("Velocidad", draft.speed) { onDraftChange(draft.copy(speed = it)) } }
        item {
            Text("Características", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                EditorTextField("FUE", draft.strength, Modifier.weight(1f), true) { onDraftChange(draft.copy(strength = it)) }
                EditorTextField("DES", draft.dexterity, Modifier.weight(1f), true) { onDraftChange(draft.copy(dexterity = it)) }
                EditorTextField("CON", draft.constitution, Modifier.weight(1f), true) { onDraftChange(draft.copy(constitution = it)) }
                EditorTextField("INT", draft.intelligence, Modifier.weight(1f), true) { onDraftChange(draft.copy(intelligence = it)) }
                EditorTextField("SAB", draft.wisdom, Modifier.weight(1f), true) { onDraftChange(draft.copy(wisdom = it)) }
                EditorTextField("CAR", draft.charisma, Modifier.weight(1f), true) { onDraftChange(draft.copy(charisma = it)) }
                EditorTextField("PB", draft.proficiencyBonus, Modifier.weight(1f), true) { onDraftChange(draft.copy(proficiencyBonus = it)) }
            }
        }

        item { EditorTextField("Tiradas de salvación", draft.savingThrows) { onDraftChange(draft.copy(savingThrows = it)) } }
        item { EditorTextField("Habilidades", draft.skills) { onDraftChange(draft.copy(skills = it)) } }
        item { EditorTextField("Vulnerabilidades al daño", draft.damageVulnerabilities) { onDraftChange(draft.copy(damageVulnerabilities = it)) } }
        item { EditorTextField("Resistencias al daño", draft.damageResistances) { onDraftChange(draft.copy(damageResistances = it)) } }
        item { EditorTextField("Inmunidades al daño", draft.damageImmunities) { onDraftChange(draft.copy(damageImmunities = it)) } }
        item { EditorTextField("Inmunidades a condiciones", draft.conditionImmunities) { onDraftChange(draft.copy(conditionImmunities = it)) } }
        item { EditorTextField("Sentidos", draft.senses) { onDraftChange(draft.copy(senses = it)) } }
        item { EditorTextField("Idiomas", draft.languages) { onDraftChange(draft.copy(languages = it)) } }
        item { EditorTextField("Rasgos", draft.traits, lines = 5) { onDraftChange(draft.copy(traits = it)) } }
        item { EditorTextField("Acciones", draft.actions, lines = 7) { onDraftChange(draft.copy(actions = it)) } }
        item { EditorTextField("Acciones adicionales", draft.bonusActions, lines = 4) { onDraftChange(draft.copy(bonusActions = it)) } }
        item { EditorTextField("Reacciones", draft.reactions, lines = 4) { onDraftChange(draft.copy(reactions = it)) } }
        item { EditorTextField("Acciones legendarias", draft.legendaryActions, lines = 5) { onDraftChange(draft.copy(legendaryActions = it)) } }
        item { EditorTextField("Acciones de guarida", draft.lairActions, lines = 5) { onDraftChange(draft.copy(lairActions = it)) } }
        item { EditorTextField("Tácticas / guía para el DM", draft.tactics, lines = 5) { onDraftChange(draft.copy(tactics = it)) } }
        item { EditorTextField("Notas", draft.notes, lines = 6) { onDraftChange(draft.copy(notes = it)) } }
        item { Button(onClick = onSave) { Text("Guardar cambios") } }
    }
}

@Composable
private fun EditorTextField(
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

private data class CreatureDraft(
    val displayName: String,
    val size: String,
    val creatureType: String,
    val alignment: String,
    val armorClass: String,
    val armorClassDetails: String,
    val hitPoints: String,
    val hitDice: String,
    val speed: String,
    val strength: String,
    val dexterity: String,
    val constitution: String,
    val intelligence: String,
    val wisdom: String,
    val charisma: String,
    val savingThrows: String,
    val skills: String,
    val damageVulnerabilities: String,
    val damageResistances: String,
    val damageImmunities: String,
    val conditionImmunities: String,
    val senses: String,
    val languages: String,
    val challengeRating: String,
    val proficiencyBonus: String,
    val traits: String,
    val actions: String,
    val bonusActions: String,
    val reactions: String,
    val legendaryActions: String,
    val lairActions: String,
    val tactics: String,
    val notes: String,
) {
    fun toPayload(): CreaturePayload {
        require(displayName.trim().isNotEmpty()) { "El nombre de la criatura no puede quedar vacío." }
        return CreaturePayload(
            size = size.trim(),
            creatureType = creatureType.trim(),
            alignment = alignment.trim(),
            armorClass = armorClass.optionalPositiveInt("CA"),
            armorClassDetails = armorClassDetails.trim(),
            hitPoints = hitPoints.optionalPositiveInt("PG"),
            hitDice = hitDice.trim(),
            speed = speed.trim(),
            abilityScores = CreatureAbilityScores(
                strength = strength.requiredPositiveInt("FUE"),
                dexterity = dexterity.requiredPositiveInt("DES"),
                constitution = constitution.requiredPositiveInt("CON"),
                intelligence = intelligence.requiredPositiveInt("INT"),
                wisdom = wisdom.requiredPositiveInt("SAB"),
                charisma = charisma.requiredPositiveInt("CAR"),
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
            proficiencyBonus = proficiencyBonus.optionalNonNegativeInt("PB"),
            traits = traits.trim(),
            actions = actions.trim(),
            bonusActions = bonusActions.trim(),
            reactions = reactions.trim(),
            legendaryActions = legendaryActions.trim(),
            lairActions = lairActions.trim(),
            tactics = tactics.trim(),
            notes = notes.trim(),
        )
    }

    companion object {
        fun from(content: CreatureContent): CreatureDraft = content.payload.let { payload ->
            CreatureDraft(
                displayName = content.item.displayName,
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
}

private fun String.optionalPositiveInt(label: String): Int? {
    val normalized = trim()
    if (normalized.isEmpty()) return null
    val value = normalized.toIntOrNull() ?: throw IllegalArgumentException("$label debe ser un número entero.")
    require(value > 0) { "$label debe ser positivo." }
    return value
}

private fun String.optionalNonNegativeInt(label: String): Int? {
    val normalized = trim()
    if (normalized.isEmpty()) return null
    val value = normalized.toIntOrNull() ?: throw IllegalArgumentException("$label debe ser un número entero.")
    require(value >= 0) { "$label no puede ser negativo." }
    return value
}

private fun String.requiredPositiveInt(label: String): Int {
    val value = trim().toIntOrNull() ?: throw IllegalArgumentException("$label debe ser un número entero positivo.")
    require(value > 0) { "$label debe ser positivo." }
    return value
}

private fun CreaturePayload.tagsForSearch(): String = listOf(
    savingThrows,
    skills,
    senses,
    languages,
    traits,
    actions,
    tactics,
).joinToString(" ")

private fun scopeSortKey(content: CreatureContent): Int = when (content.item.identity.scope) {
    is ContentScope.Personal -> 0
    is ContentScope.Campaign -> 1
    else -> 2
}

private fun scopeLabel(content: CreatureContent): String = when (val scope = content.item.identity.scope) {
    is ContentScope.Personal -> "Personal"
    is ContentScope.Campaign -> "Campaña • ${scope.campaignId.toString().take(8)}…"
    is ContentScope.Official -> "Oficial • ${scope.sourceKey}"
    ContentScope.System -> "Sistema"
}

private fun ContentScope.displayLabel(): String = when (this) {
    is ContentScope.Personal -> "Personal"
    is ContentScope.Campaign -> "Campaña"
    is ContentScope.Official -> "Oficial ($sourceKey)"
    ContentScope.System -> "Sistema"
}
