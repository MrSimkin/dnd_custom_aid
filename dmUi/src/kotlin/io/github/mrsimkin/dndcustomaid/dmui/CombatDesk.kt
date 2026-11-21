package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContent
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PackageContent
import io.github.mrsimkin.dndcustomaid.shared.content.PackageContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatDocument
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatStatus
import io.github.mrsimkin.dndcustomaid.shared.dm.Combatant
import io.github.mrsimkin.dndcustomaid.shared.dm.DmCombatRepository
import kotlin.random.Random
import kotlin.uuid.Uuid
/*
 * Base para DM · Mesa de Combate (approved design DM_UX_APROBADA.md §3: P11-P14, P19, P22, P32-P34;
 * D-0068 §13, D-0069 §10-11).
 *
 * Step 1 Preparar (three columns) → step 2 Iniciativa (a window, once) → step 3 En curso (order,
 * the creature you touch, the guide) · step 4 the full creature editor, from preparation or live.
 * Every change is saved at once on the device and can be undone; nothing is applied automatically.
 */

/** Values that belong to this combatant only (P32: «lo propio va en naranja»). */
internal val OwnValueColor = Color(0xFFE8730C)

internal fun d20(): Int = Random.nextInt(1, 21)

/** «Goblin 3» → «Goblin». */
internal fun stemOf(label: String) = label.replace(Regex("""\s+\d+$"""), "")

/** Everything the combat desk reads from the campaign, loaded once per desk. */
internal class CombatSources(private val database: AppDatabase, private val campaignId: Uuid, accountId: Uuid?) {
    /** QA 0.7.0-preqa.2 (C1, D6): an SRD monster becomes the campaign's creature the first time it is used. */
    fun adopt(monster: io.github.mrsimkin.dndcustomaid.shared.dm.SrdMonster): CreatureContent =
        io.github.mrsimkin.dndcustomaid.shared.dm.adoptSrdMonster(database, campaignId, monster, nowEpochSeconds())

    private val reusable = ReusableContentRepository(database)
    private val creatureRepo = CreatureContentRepository(database, reusable)
    val creatures: List<CreatureContent> = (
        reusable.listCampaign(campaignId, ReusableContentFamily.CREATURE) +
            (accountId?.let { reusable.listPersonal(it, ReusableContentFamily.CREATURE) } ?: emptyList())
        ).mapNotNull { runCatching { creatureRepo.creature(it.identity.id) }.getOrNull() }
    val creaturesById = creatures.associateBy { it.item.identity.id }
    val encounters = EncounterContentRepository(database, reusable).let { repo ->
        reusable.listCampaign(campaignId, ReusableContentFamily.ENCOUNTER).mapNotNull { runCatching { repo.encounter(it.identity.id) }.getOrNull() }
    }
    val encountersById = encounters.associateBy { it.item.identity.id }
    val packages: List<PackageContent> = PackageContentRepository(database, reusable).let { repo ->
        repo.listCampaign(campaignId) + (accountId?.let(repo::listPersonal) ?: emptyList())
    }
    val packagesById = packages.associateBy { it.item.identity.id }
    val pcs = CharacterRepository(database).listCharacters(campaignId)

    fun base(c: Combatant): CreaturePayload? = c.sourceContentId?.let(creaturesById::get)?.payload
}

internal enum class AddMode { ENCOUNTER, BESTIARY, CUSTOM }

@Composable
internal fun CombatDesk(
    campaign: Campaign,
    database: AppDatabase,
    accountId: Uuid?,
    combatId: Uuid,
    onChanged: () -> Unit,
    onEnded: () -> Unit,
) {
    val platform = LocalDmPlatform.current
    val repository = remember { DmCombatRepository(database) }
    var sourcesVersion by remember { mutableStateOf(0) }
    val sources = remember(campaign.id, accountId, sourcesVersion) { CombatSources(database, campaign.id, accountId) }
    var doc by remember(combatId) { mutableStateOf(repository.combat(combatId)) }
    val undo = remember(combatId) { mutableStateListOf<CombatDocument>() }
    var showInitiative by remember { mutableStateOf(false) }
    var editor by remember { mutableStateOf<Pair<Uuid, Boolean>?>(null) }
    var addMode by remember { mutableStateOf<AddMode?>(null) }
    var confirmClose by remember { mutableStateOf(false) }
    var notice by remember { mutableStateOf<String?>(null) }

    val current = doc ?: run {
        Text("Este combate ya no está en el aparato.", modifier = Modifier.padding(16.dp))
        return
    }
    // Owner observation E1 (0.7.0-preqa.4): a new combat starts on «Elegir encuentro»; the same
    // screen replaces the «Encuentro preparado» pop-up.
    var selecting by remember(combatId) {
        mutableStateOf(
            current.status == CombatStatus.PREPARING && current.encounterId == null &&
                current.combatants.none { it.kind != io.github.mrsimkin.dndcustomaid.shared.dm.CombatantKind.PC },
        )
    }
    val openAdd: (AddMode) -> Unit = { mode -> if (mode == AddMode.ENCOUNTER) selecting = true else addMode = mode }

    fun save(next: CombatDocument) {
        doc = repository.save(next, nowEpochSeconds())
        onChanged()
    }

    /** A change the DM can take back with «Deshacer». */
    fun change(next: CombatDocument) {
        undo.add(current)
        if (undo.size > 40) undo.removeAt(0)
        save(next)
    }

    /** Typing and in-window changes: the window's opening state is the undo point. */
    fun changeQuiet(next: CombatDocument) = save(next)

    fun checkpoint() {
        undo.add(current)
        if (undo.size > 40) undo.removeAt(0)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val acting = current.actingIds.mapNotNull(current::combatant)
            val actingText = if (acting.size > 1) "${stemOf(acting.first().label)} ×${acting.size}" else acting.firstOrNull()?.label
            Text(
                when {
                    selecting -> "Elegir encuentro"
                    else -> null
                } ?: when (current.status) {
                    CombatStatus.PREPARING -> "Paso 1 · Preparar"
                    CombatStatus.ACTIVE -> (if (current.round == 0) "Ronda sorpresa" else "Ronda ${current.round}") + (actingText?.let { " · Actúa $it" } ?: "")
                    CombatStatus.ENDED -> "Terminado"
                },
                style = MaterialTheme.typography.titleMedium,
                // Tablet QA: without these the title broke into one letter per line beside the message.
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            notice?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
            TextButton(enabled = undo.isNotEmpty(), onClick = { save(undo.removeAt(undo.lastIndex)) }) { Text("↶ Deshacer") }
            OutlinedButton(onClick = { confirmClose = true }) {
                Text(if (current.status == CombatStatus.PREPARING) "Borrar combate" else "Terminar combate")
            }
        }
        HorizontalDivider()
        Box(modifier = Modifier.fillMaxSize()) {
            if (selecting && current.status != CombatStatus.ENDED) {
                EncounterSelector(
                    database = database,
                    campaignId = campaign.id,
                    sources = sources,
                    startingCombat = current.combatants.none { it.kind != io.github.mrsimkin.dndcustomaid.shared.dm.CombatantKind.PC },
                    onUse = { draft, creatures ->
                        change(draft.applyTo(current, creatures))
                        sourcesVersion += 1
                        selecting = false
                        notice = "«${draft.name.ifBlank { "Encuentro" }}» está en el combate."
                    },
                    onSkip = { selecting = false },
                    onSaved = { sourcesVersion += 1; onChanged() },
                )
            } else when (current.status) {
                CombatStatus.PREPARING -> PrepareStep(
                    doc = current,
                    sources = sources,
                    onChange = ::change,
                    onEdit = { id, whole -> checkpoint(); editor = id to whole },
                    onAdd = openAdd,
                    onStart = { checkpoint(); showInitiative = true },
                )
                CombatStatus.ACTIVE -> LiveStep(
                    doc = current,
                    sources = sources,
                    onChange = ::change,
                    onChangeQuiet = ::changeQuiet,
                    onEdit = { id, whole -> checkpoint(); editor = id to whole },
                    onAdd = openAdd,
                )
                CombatStatus.ENDED -> Text("Este combate terminó.", modifier = Modifier.padding(16.dp))
            }
        }
    }

    if (showInitiative) {
        InitiativeDialog(
            doc = current,
            onChange = ::changeQuiet,
            onDismiss = { showInitiative = false },
            onConfirm = {
                save(CombatEngine.start(current))
                showInitiative = false
            },
        )
    }

    editor?.let { (id, whole) ->
        if (current.combatant(id) == null) {
            editor = null
        } else {
            CreatureEditorDialog(
                doc = current,
                id = id,
                initialWhole = whole,
                sources = sources,
                onChange = ::changeQuiet,
                onUndo = { if (undo.isNotEmpty()) save(undo.last()) },
                onSaveAsCreature = { name, payload ->
                    CreatureContentRepository(database).createCampaign(campaign.id, name, payload, nowEpochSeconds())
                    sourcesVersion += 1
                    notice = "«$name» quedó en el bestiario de la campaña; llega a la App de PC al sincronizar."
                },
                onClose = { editor = null },
            )
        }
    }

    addMode?.let { mode ->
        AddDialog(
            mode = mode,
            doc = current,
            sources = sources,
            onDismiss = { addMode = null },
            onChange = { change(it); addMode = null },
        )
    }

    if (confirmClose) {
        val preparing = current.status == CombatStatus.PREPARING
        AlertDialog(
            onDismissRequest = { confirmClose = false },
            title = { Text(if (preparing) "¿Borrar este combate?" else "¿Terminar este combate?") },
            text = {
                Text(
                    if (preparing) "Se borra lo preparado en esta mesa. El bestiario y los encuentros no cambian."
                    else "La mesa se cierra. Nada se aplica a las hojas de los PJ.",
                )
            },
            confirmButton = {
                Button(onClick = {
                    confirmClose = false
                    if (preparing) repository.delete(current.id) else repository.save(CombatEngine.end(current), nowEpochSeconds())
                    onEnded()
                }) { Text(if (preparing) "Borrar" else "Terminar") }
            },
            dismissButton = { TextButton(onClick = { confirmClose = false }) { Text("Cancelar") } },
        )
    }
}






