package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.content.NoteContent
import io.github.mrsimkin.dndcustomaid.shared.content.NoteContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NoteContextKind
import io.github.mrsimkin.dndcustomaid.shared.content.NotePayload
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.DmDungeonRepository
import io.github.mrsimkin.dndcustomaid.shared.dm.DungeonClock
import io.github.mrsimkin.dndcustomaid.shared.dm.DungeonEngine
import kotlin.uuid.Uuid

/*
 * Base para DM · tools of the rail that open as a side panel over any desk (§1, §5.4):
 * - 📝 Notas (P21): one kind of note, stuck by itself to what is open (a zone, a place, a scene, an
 *   NPC…), changed with one tap; always saved; a view gathers them with their context.
 * - ⏱ Relojes (P20): the dungeon's clocks plus clocks of a place or of the campaign.
 */

/** Where the DM is looking: what a new note sticks to. */
data class DeskContext(val kind: NoteContextKind, val id: Uuid?, val label: String)

internal val LocalDeskContext = staticCompositionLocalOf<MutableState<DeskContext?>> { mutableStateOf(null) }

@Composable
internal fun NotesPanel(campaign: Campaign, database: AppDatabase, version: Int, onChanged: () -> Unit) {
    val repo = remember { NoteContentRepository(database) }
    val context = LocalDeskContext.current.value
    var notes by remember(campaign.id, version) { mutableStateOf(repo.listCampaign(campaign.id)) }
    var text by remember { mutableStateOf("") }
    var stickToCampaign by remember(context) { mutableStateOf(context == null) }
    var editing by remember { mutableStateOf<NoteContent?>(null) }

    fun refresh() {
        notes = repo.listCampaign(campaign.id)
        onChanged()
    }

    Column(modifier = Modifier.fillMaxSize().dmScroll().padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        DmTextField(value = text, onValueChange = { text = it }, label = { Text("Nota") }, minLines = 3, modifier = Modifier.fillMaxWidth())
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Se pega a:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.CenterVertically))
            context?.let { FilterChip(selected = !stickToCampaign, onClick = { stickToCampaign = false }, label = { Text("${it.kind.label}: ${it.label}") }) }
            FilterChip(selected = stickToCampaign, onClick = { stickToCampaign = true }, label = { Text("Campaña") })
        }
        Button(enabled = text.isNotBlank(), onClick = {
            val target = if (stickToCampaign || context == null) DeskContext(NoteContextKind.CAMPAIGN, null, campaign.name) else context
            repo.create(campaign.id, NotePayload(text.trim(), target.kind, target.id, target.label), nowEpochSeconds())
            text = ""
            refresh()
        }) { Text("Guardar nota") }
        Text("Se guarda al momento y viaja a la App de PC al sincronizar.", style = MaterialTheme.typography.bodySmall)
        HorizontalDivider()
        if (notes.isEmpty()) Text("Todavía no hay notas.", style = MaterialTheme.typography.bodySmall)
        notes.groupBy { "${it.payload.contextKind.label}: ${it.payload.contextLabel.ifBlank { campaign.name }}" }.forEach { (where, group) ->
            Text(where, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 6.dp))
            group.forEach { note ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(note.payload.text, style = MaterialTheme.typography.bodyMedium)
                        Row {
                            // «Se cambia con un toque»: move the note to what is open now, or to the campaign.
                            if (context != null && (note.payload.contextId != context.id)) {
                                TextButton(onClick = {
                                    repo.update(note.item.identity.id, note.item.identity.revision, note.payload.copy(contextKind = context.kind, contextId = context.id, contextLabel = context.label), nowEpochSeconds())
                                    refresh()
                                }) { Text("Pegar a ${context.label}") }
                            }
                            if (note.payload.contextKind != NoteContextKind.CAMPAIGN) {
                                TextButton(onClick = {
                                    repo.update(note.item.identity.id, note.item.identity.revision, note.payload.copy(contextKind = NoteContextKind.CAMPAIGN, contextId = null, contextLabel = campaign.name), nowEpochSeconds())
                                    refresh()
                                }) { Text("A la campaña") }
                            }
                            TextButton(onClick = { editing = note }) { Text("Editar") }
                        }
                    }
                }
            }
        }
    }

    editing?.let { note ->
        var draft by remember(note.item.identity.id) { mutableStateOf(note.payload.text) }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { editing = null },
            title = { Text("Editar nota") },
            text = { DmTextField(value = draft, onValueChange = { draft = it }, minLines = 4, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                Button(enabled = draft.isNotBlank(), onClick = {
                    repo.update(note.item.identity.id, note.item.identity.revision, note.payload.copy(text = draft.trim()), nowEpochSeconds())
                    editing = null
                    refresh()
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { editing = null }) { Text("Cancelar") } },
        )
    }
}

/** Clocks of a place or of the campaign, kept on this device (dungeon clocks live in their dungeon). */
internal class CampaignClocks(private val store: DmKeyValueStore, private val campaignId: Uuid) {
    data class Owned(val clock: DungeonClock, val owner: String)

    private val key get() = "clocks_$campaignId"

    fun list(): List<Owned> = store.getString(key).orEmpty().lines().mapNotNull { line ->
        val parts = line.split('\t')
        if (parts.size < 5) return@mapNotNull null
        runCatching { Owned(DungeonClock(Uuid.parse(parts[0]), parts[1], parts[2].toInt(), parts[3].toInt(), perTurn = false), parts[4]) }.getOrNull()
    }

    fun save(clocks: List<Owned>) {
        store.putString(key, clocks.joinToString("\n") { (c, owner) -> listOf(c.id, c.name.replace('\t', ' '), c.segments, c.filled, owner.replace('\t', ' ')).joinToString("\t") })
    }
}

@Composable
internal fun ClocksPanel(campaign: Campaign, database: AppDatabase, version: Int, onChanged: () -> Unit) {
    val platform = LocalDmPlatform.current
    val context = LocalDeskContext.current.value
    val dungeonRepo = remember { DmDungeonRepository(database) }
    val campaignClocks = remember(campaign.id) { CampaignClocks(platform.store, campaign.id) }
    var dungeons by remember(campaign.id, version) { mutableStateOf(dungeonRepo.list(campaign.id).filter { !it.ended }) }
    var owned by remember(campaign.id) { mutableStateOf(campaignClocks.list()) }
    var name by remember { mutableStateOf("") }
    var segments by remember { mutableStateOf(4) }
    var toPlace by remember(context) { mutableStateOf(context?.kind == NoteContextKind.PLACE || context?.kind == NoteContextKind.ZONE) }

    fun saveOwned(next: List<CampaignClocks.Owned>) {
        campaignClocks.save(next)
        owned = next
    }

    Column(modifier = Modifier.fillMaxSize().dmScroll().padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        dungeons.forEach { doc ->
            if (doc.clocks.isNotEmpty()) {
                Text("Mazmorra: ${doc.dungeon}", style = MaterialTheme.typography.titleSmall)
                doc.clocks.forEach { clock ->
                    fun change(next: io.github.mrsimkin.dndcustomaid.shared.dm.DungeonDocument) {
                        dungeonRepo.save(next, nowEpochSeconds())
                        dungeons = dungeonRepo.list(campaign.id).filter { !it.ended }
                        onChanged()
                    }
                    ClockRow(clock, onMinus = { change(DungeonEngine.tickClock(doc, clock.id, -1)) }, onPlus = { change(DungeonEngine.tickClock(doc, clock.id, 1)) }, onRemove = { change(DungeonEngine.removeClock(doc, clock.id)) })
                }
            }
        }
        owned.groupBy { it.owner }.forEach { (owner, clocks) ->
            Text(owner, style = MaterialTheme.typography.titleSmall)
            clocks.forEach { item ->
                fun tick(delta: Int) = saveOwned(owned.map { if (it.clock.id == item.clock.id) it.copy(clock = it.clock.copy(filled = (it.clock.filled + delta).coerceIn(0, it.clock.segments))) else it })
                ClockRow(item.clock, onMinus = { tick(-1) }, onPlus = { tick(1) }, onRemove = { saveOwned(owned.filter { it.clock.id != item.clock.id }) })
            }
        }
        if (owned.isEmpty() && dungeons.all { it.clocks.isEmpty() }) Text("Todavía no hay relojes.", style = MaterialTheme.typography.bodySmall)
        HorizontalDivider()
        DmTextField(value = name, onValueChange = { name = it }, label = { Text("Reloj nuevo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(4, 6, 8, 10, 12).forEach { n -> FilterChip(selected = segments == n, onClick = { segments = n }, label = { Text("$n") }) }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Pertenece a:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.CenterVertically))
            if (context != null && context.kind != NoteContextKind.CAMPAIGN && context.kind != NoteContextKind.DUNGEON) {
                FilterChip(selected = toPlace, onClick = { toPlace = true }, label = { Text(context.label) })
            }
            FilterChip(selected = !toPlace, onClick = { toPlace = false }, label = { Text("Campaña") })
        }
        Button(enabled = name.isNotBlank(), onClick = {
            val owner = if (toPlace && context != null) context.label else "Campaña"
            saveOwned(owned + CampaignClocks.Owned(DungeonClock(Uuid.random(), name.trim(), segments, perTurn = false), owner))
            name = ""
        }) { Text("Agregar reloj") }
        Text("Los relojes de una mazmorra avanzan +1 por turno desde la Mesa de Dungeon. Ninguno aplica consecuencias.", style = MaterialTheme.typography.bodySmall)
    }
}
