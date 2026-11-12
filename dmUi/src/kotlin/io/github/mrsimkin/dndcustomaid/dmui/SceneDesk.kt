package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContent
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.NoteContextKind
import io.github.mrsimkin.dndcustomaid.shared.content.NpcContent
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContent
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceKind
import io.github.mrsimkin.dndcustomaid.shared.content.SceneContent
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContent
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.NpcGrouping
import io.github.mrsimkin.dndcustomaid.shared.dm.SceneEntry
import io.github.mrsimkin.dndcustomaid.shared.dm.SceneEntryKind
import io.github.mrsimkin.dndcustomaid.shared.dm.SceneFilter
import io.github.mrsimkin.dndcustomaid.shared.dm.SceneIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt
import kotlin.uuid.Uuid

/*
 * Base para DM · Mesa de Escena (approved design DM_UX_APROBADA.md §4; P15, P35, P36; D-0069 §5-9).
 *
 * Left: «Buscar» (filters by type, 📌 pinned and 🕘 recent, NPCs by idea or context, groupings) and
 * «Recorrer» (by zone, by function, by adventure, 🗺 map). Centre: the open sheet with its path,
 * paper reference, parts for players or DM only, a shop's list and «PNJ aquí». Right: what is
 * related. Opening a sheet never moves the group.
 */

private enum class SceneMode(val label: String) { SEARCH("Buscar"), WALK("Recorrer") }

private enum class WalkMode(val label: String) { ZONE("Por zona"), FUNCTION("Por función"), ADVENTURE("Por aventura"), MAP("🗺 Mapa") }

/** Per-campaign memory of the desk: the open sheet, the pinned ones and the recent ones. */
internal class SceneDeskPreferences(private val store: DmKeyValueStore, private val campaignId: Uuid) {
    private fun ids(key: String) = store.getString("${key}_$campaignId").orEmpty().split(',').mapNotNull { runCatching { Uuid.parse(it) }.getOrNull() }

    var openId: Uuid?
        get() = store.getString("scene_open_$campaignId")?.let { runCatching { Uuid.parse(it) }.getOrNull() }
        set(value) = store.putString("scene_open_$campaignId", value?.toString())

    var openName: String?
        get() = store.getString("scene_open_name_$campaignId")
        set(value) = store.putString("scene_open_name_$campaignId", value)

    fun pinned(): List<Uuid> = ids("scene_pins")

    fun togglePin(id: Uuid) {
        val now = pinned()
        store.putString("scene_pins_$campaignId", (if (id in now) now - id else now + id).joinToString(","))
    }

    fun recent(): List<Uuid> = ids("scene_recent")

    fun pushRecent(id: Uuid) {
        store.putString("scene_recent_$campaignId", (listOf(id) + recent().filter { it != id }).take(8).joinToString(","))
    }
}

@Composable
internal fun SceneDesk(campaign: Campaign, database: AppDatabase, mediaBlobs: MediaBlobStore, version: Int) {
    val platform = LocalDmPlatform.current
    val prefs = remember(campaign.id) { SceneDeskPreferences(platform.store, campaign.id) }
    val deskContext = LocalDeskContext.current
    val index = remember(campaign.id, version) { SceneIndex.load(database, campaign.id) }
    var mode by remember { mutableStateOf(SceneMode.SEARCH) }
    var walk by remember { mutableStateOf(WalkMode.ZONE) }
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf(SceneFilter.ALL) }
    var grouping by remember { mutableStateOf(NpcGrouping.NONE) }
    var openId by remember(campaign.id) { mutableStateOf(prefs.openId?.takeIf { index.kindOf(it) != null }) }
    var mapId by remember(campaign.id) { mutableStateOf<Uuid?>(null) }
    var pins by remember(campaign.id) { mutableStateOf(prefs.pinned()) }
    var recent by remember(campaign.id) { mutableStateOf(prefs.recent()) }
    var sheetHistory by remember(campaign.id) { mutableStateOf(listOf<Uuid>()) }

    fun show(id: Uuid) {
        if (index.kindOf(id) == null) return
        openId = id
        mapId = null
        prefs.openId = id
        prefs.openName = index.nameOf(id)
        prefs.pushRecent(id)
        recent = prefs.recent()
        // A note written now sticks to this sheet (P21).
        val kind = when (index.kindOf(id)) {
            SceneEntryKind.NPC -> NoteContextKind.NPC
            SceneEntryKind.ZONE -> NoteContextKind.ZONE
            SceneEntryKind.SCENE -> NoteContextKind.SCENE
            else -> NoteContextKind.PLACE
        }
        deskContext.value = DeskContext(kind, id, index.nameOf(id).orEmpty())
    }

    /** Opens a sheet and remembers the one that was open, for «← Atrás». */
    fun open(id: Uuid) {
        val current = openId
        if (current != null && current != id) sheetHistory = (sheetHistory + current).takeLast(30)
        show(id)
    }

    fun sheetBack() {
        val previous = sheetHistory.lastOrNull() ?: return
        sheetHistory = sheetHistory.dropLast(1)
        show(previous)
    }

    fun togglePin(id: Uuid) {
        prefs.togglePin(id)
        pins = prefs.pinned()
    }

    if (index.entries.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Todavía no hay PNJ, lugares, zonas ni escenas en esta campaña.", style = MaterialTheme.typography.titleMedium)
            Text("Se preparan en la App de PC (Gestores) y llegan al sincronizar.", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(horizontal = 12.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SceneMode.entries.forEach { m -> FilterChip(selected = mode == m, onClick = { mode = m }, label = { Text(m.label) }) }
            }
            when (mode) {
                SceneMode.SEARCH -> SearchColumn(
                    index = index,
                    query = query,
                    onQuery = { query = it },
                    filter = filter,
                    onFilter = { filter = it },
                    grouping = grouping,
                    onGrouping = { grouping = it },
                    pins = pins,
                    recent = recent,
                    openId = openId,
                    mediaBlobs = mediaBlobs,
                    onOpen = ::open,
                    onTogglePin = ::togglePin,
                )
                SceneMode.WALK -> WalkColumn(
                    index = index,
                    walk = walk,
                    onWalk = { walk = it },
                    openId = openId,
                    onOpen = ::open,
                    onMap = { mapId = it },
                )
            }
        }
        Box(modifier = Modifier.weight(1.4f).fillMaxHeight()) {
            val shownMap = mapId
            val shown = openId
            when {
                shownMap != null -> MapView(index, shownMap, mediaBlobs, onOpen = ::open, onClose = { mapId = null })
                shown != null -> EntrySheet(
                    index, shown, shown in pins, mediaBlobs,
                    onOpen = ::open,
                    onTogglePin = { togglePin(shown) },
                    onMap = { mapId = it },
                    onBack = if (sheetHistory.isNotEmpty()) ::sheetBack else null,
                )
                else -> Text(
                    "Busca o recorre a la izquierda y toca una ficha para abrirla aquí. Abrir una ficha no mueve al grupo.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(24.dp),
                )
            }
        }
        Surface(tonalElevation = 1.dp, modifier = Modifier.weight(1f).fillMaxHeight()) {
            openId?.let { RelatedColumn(index, it, onOpen = ::open) }
        }
    }
}

// ---------------------------------------------------------------- Buscar

@Composable
private fun SearchColumn(
    index: SceneIndex,
    query: String,
    onQuery: (String) -> Unit,
    filter: SceneFilter,
    onFilter: (SceneFilter) -> Unit,
    grouping: NpcGrouping,
    onGrouping: (NpcGrouping) -> Unit,
    pins: List<Uuid>,
    recent: List<Uuid>,
    openId: Uuid?,
    mediaBlobs: MediaBlobStore,
    onOpen: (Uuid) -> Unit,
    onTogglePin: (Uuid) -> Unit,
) {
    DmTextField(value = query, onValueChange = onQuery, label = { Text("Nombre, papel o idea") }, singleLine = true, modifier = Modifier.fillMaxWidth())
    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        SceneFilter.entries.forEach { f -> FilterChip(selected = filter == f, onClick = { onFilter(f) }, label = { Text(f.label) }) }
    }
    if (filter == SceneFilter.NPC) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Agrupar:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.align(Alignment.CenterVertically))
            NpcGrouping.entries.forEach { g -> FilterChip(selected = grouping == g, onClick = { onGrouping(g) }, label = { Text(g.label) }) }
        }
    }
    val results = remember(index, query, filter) { index.search(query, filter) }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxSize()) {
        if (query.isBlank() && filter == SceneFilter.ALL) {
            item { SectionLabel("📌 Fijados") }
            val pinned = pins.mapNotNull(index::entry)
            if (pinned.isEmpty()) item { Hint("Mantén presionada una ficha para fijarla aquí.") }
            items(pinned, key = { "p" + it.id }) { EntryRow(index, it, true, it.id == openId, mediaBlobs, onOpen, onTogglePin) }
            item { SectionLabel("🕘 Recientes") }
            val recents = recent.filter { it !in pins }.mapNotNull(index::entry)
            if (recents.isEmpty()) item { Hint("Las fichas que abras aparecen aquí.") }
            items(recents, key = { "r" + it.id }) { EntryRow(index, it, false, it.id == openId, mediaBlobs, onOpen, onTogglePin) }
            item { Hint("Elige un tipo arriba para ver todo lo de ese tipo.") }
        } else if (results.isEmpty()) {
            item { Hint("Nada coincide con «$query». Prueba con una idea: su oficio, cómo se ve, dónde está.") }
        } else if (filter == SceneFilter.NPC && grouping != NpcGrouping.NONE) {
            val npcs = results.mapNotNull { index.npc(it.id) }
            index.groupNpcs(grouping, npcs).forEach { (group, members) ->
                item(key = "g$group") { SectionLabel(group) }
                items(members, key = { "g$group" + it.item.identity.id }) { npc ->
                    index.entry(npc.item.identity.id)?.let { EntryRow(index, it, it.id in pins, it.id == openId, mediaBlobs, onOpen, onTogglePin) }
                }
            }
        } else {
            items(results, key = { it.id.toString() }) { EntryRow(index, it, it.id in pins, it.id == openId, mediaBlobs, onOpen, onTogglePin) }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EntryRow(
    index: SceneIndex,
    entry: SceneEntry,
    pinned: Boolean,
    selected: Boolean,
    mediaBlobs: MediaBlobStore,
    onOpen: (Uuid) -> Unit,
    onTogglePin: (Uuid) -> Unit,
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth().handCursor().combinedClickable(onClick = { onOpen(entry.id) }, onLongClick = { onTogglePin(entry.id) }),
    ) {
        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (entry.kind == SceneEntryKind.NPC) {
                index.npc(entry.id)?.let { index.portraitOf(it) }?.let { Thumbnail(it, mediaBlobs, 44) }
            }
            Column(modifier = Modifier.weight(1f)) {
                // P36: for an NPC the role comes first, the name after.
                if (entry.kind == SceneEntryKind.NPC) {
                    Text(entry.headline, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(entry.name, style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(entry.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(entry.headline, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
                if (entry.detail.isNotBlank()) Text(entry.detail, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                if (entry.where.isNotBlank()) Text("📍 ${entry.where}", style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(entry.kind.label, style = MaterialTheme.typography.labelSmall)
                if (pinned) Text("📌", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

// ---------------------------------------------------------------- Recorrer

@Composable
private fun WalkColumn(index: SceneIndex, walk: WalkMode, onWalk: (WalkMode) -> Unit, openId: Uuid?, onOpen: (Uuid) -> Unit, onMap: (Uuid) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        WalkMode.entries.forEach { w -> FilterChip(selected = walk == w, onClick = { onWalk(w) }, label = { Text(w.label) }) }
    }
    Column(modifier = Modifier.fillMaxSize().dmScroll(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        when (walk) {
            WalkMode.ZONE -> {
                index.zones.sortedBy { it.item.displayName.lowercase() }.forEach { zone ->
                    WalkLine(zone.item.displayName, zone.payload.summary, 0, zone.item.identity.id == openId, bold = true) { onOpen(zone.item.identity.id) }
                    index.placesIn(zone).forEach { place -> PlaceWalkLine(index, place, openId, onOpen) }
                }
                val loose = index.places.filter { index.zoneOf(it) == null }.sortedBy { it.item.displayName.lowercase() }
                if (loose.isNotEmpty()) {
                    SectionLabel("Sin zona")
                    loose.forEach { place -> PlaceWalkLine(index, place, openId, onOpen) }
                }
                if (index.zones.isEmpty() && loose.isEmpty()) Hint("No hay zonas ni lugares todavía.")
            }
            WalkMode.FUNCTION -> {
                Hint("Por lo que ofrecen: «dónde comprar armadura», «dónde dormir».")
                index.byFunction().forEach { (function, places) ->
                    SectionLabel(function)
                    places.forEach { place -> PlaceWalkLine(index, place, openId, onOpen) }
                }
            }
            WalkMode.ADVENTURE -> {
                index.adventures().forEach { (adventure, scenes) ->
                    SectionLabel(adventure)
                    scenes.forEach { scene ->
                        val detour = index.isDetour(scene)
                        val order = index.links(scene.item.identity.id).order
                        WalkLine(
                            (if (detour) "↳ " else "") + listOfNotNull(order?.let { "$it." }, scene.item.displayName).joinToString(" "),
                            if (detour) "desvío" else scene.payload.purpose,
                            if (detour) 1 else 0,
                            scene.item.identity.id == openId,
                        ) { onOpen(scene.item.identity.id) }
                    }
                }
                if (index.scenes.isEmpty()) Hint("No hay escenas todavía.")
            }
            WalkMode.MAP -> {
                val withMaps = index.withMaps()
                if (withMaps.isEmpty()) Hint("Ningún lugar ni zona tiene mapa. En la App de PC se elige un handout tipo Mapa y se ponen marcas.")
                withMaps.forEach { id ->
                    WalkLine("🗺 ${index.nameOf(id).orEmpty()}", index.mapOf(id)?.item?.displayName.orEmpty(), 0, false, bold = true) { onMap(id) }
                }
            }
        }
    }
}

@Composable
private fun PlaceWalkLine(index: SceneIndex, place: PlaceContent, openId: Uuid?, onOpen: (Uuid) -> Unit) {
    val here = index.npcsHere(place.item.identity.id).size
    WalkLine(
        (if (place.payload.kind == PlaceKind.SHOP) "🛒 " else "") + place.item.displayName,
        listOfNotNull(place.payload.function.takeIf { it.isNotBlank() }, here.takeIf { it > 0 }?.let { "$it PNJ" }).joinToString(" · "),
        1,
        place.item.identity.id == openId,
    ) { onOpen(place.item.identity.id) }
}

@Composable
private fun WalkLine(title: String, subtitle: String, indent: Int, selected: Boolean, bold: Boolean = false, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent, MaterialTheme.shapes.small)
            .handCursor()
            .clickable(onClick = onClick)
            .padding(start = (8 + indent * 18).dp, top = 6.dp, bottom = 6.dp, end = 8.dp),
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        if (subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.bodySmall, maxLines = 1)
    }
}

// ---------------------------------------------------------------- Ficha abierta

@Composable
private fun EntrySheet(
    index: SceneIndex,
    id: Uuid,
    pinned: Boolean,
    mediaBlobs: MediaBlobStore,
    onOpen: (Uuid) -> Unit,
    onTogglePin: () -> Unit,
    onMap: (Uuid) -> Unit,
    onBack: (() -> Unit)?,
) {
    val kind = index.kindOf(id) ?: return
    Column(modifier = Modifier.fillMaxSize().dmScroll().padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (onBack != null) {
            TextButton(onClick = onBack, modifier = Modifier.handCursor()) { Text("← Atrás") }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                val path = index.pathOf(id)
                if (kind != SceneEntryKind.NPC && path.contains("›")) Text(path, style = MaterialTheme.typography.labelMedium)
                Text(index.nameOf(id).orEmpty(), style = MaterialTheme.typography.headlineSmall)
                Text(kind.label, style = MaterialTheme.typography.labelMedium)
            }
            if (index.mapOf(id) != null) TextButton(onClick = { onMap(id) }) { Text("🗺 Mapa") }
            TextButton(onClick = onTogglePin) { Text(if (pinned) "📌 Soltar" else "📌 Fijar") }
        }
        when {
            index.npc(id) != null -> NpcSheet(index, index.npc(id)!!, mediaBlobs, onOpen)
            index.place(id) != null -> PlaceSheet(index, index.place(id)!!)
            index.zone(id) != null -> ZoneSheet(index.zone(id)!!)
            index.scene(id) != null -> SceneSheet(index, index.scene(id)!!, onOpen)
        }
        if (kind != SceneEntryKind.NPC) {
            HorizontalDivider()
            SectionLabel("PNJ aquí")
            val here = index.npcsHere(id)
            if (here.isEmpty()) Hint("Nadie vinculado aquí. En la App de PC se elige de una lista.")
            here.forEach { npc ->
                WalkLine("${npc.payload.conceptRole.ifBlank { "PNJ" }} · ${npc.item.displayName}", firstLine(npc.payload.appearanceFirstImpression), 0, false) { onOpen(npc.item.identity.id) }
            }
        }
    }
}

/** A part of a sheet, marked «para jugadores» (can be read aloud) or «solo DM». */
@Composable
private fun Part(title: String, text: String, forPlayers: Boolean) {
    if (text.isBlank()) return
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                Text(
                    if (forPlayers) "👁 Para jugadores" else "🔒 Solo DM",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (forPlayers) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                )
            }
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun PaperReference(refs: List<String>) {
    if (refs.isEmpty()) return
    Text("📄 En papel: ${refs.joinToString(" · ")}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
}

@Composable
private fun NpcSheet(index: SceneIndex, npc: NpcContent, mediaBlobs: MediaBlobStore, onOpen: (Uuid) -> Unit) {
    val p = npc.payload
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        index.portraitOf(npc)?.let { Thumbnail(it, mediaBlobs, 120) }
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(p.conceptRole.ifBlank { "PNJ" }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            val where = index.whereIs(npc)
            if (where.isNotEmpty()) Text("📍 ${where.joinToString(" · ")}", style = MaterialTheme.typography.bodySmall)
            val groups = index.links(npc.item.identity.id).groups
            if (groups.isNotEmpty()) Text("Grupo: ${groups.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
        }
    }
    Part("Primera impresión", p.appearanceFirstImpression, forPlayers = true)
    Part("Cómo llevarlo", listOf(p.personalityManner, p.voiceMannerisms).filter { it.isNotBlank() }.joinToString("\n"), forPlayers = false)
    Part("Quiere, teme, necesita", p.wantsFearsNeeds, forPlayers = false)
    Part("Qué puede ofrecer", p.canOffer, forPlayers = false)
    Part("Límites y negativas", p.limitsRefusals, forPlayers = false)
    Part("Relación y contexto", p.relationshipContext, forPlayers = false)
    Part("Sabe", p.knowledge, forPlayers = false)
    Part("Secretos", p.secrets, forPlayers = false)
    Part("Guía para el DM", p.dmGuidance, forPlayers = false)
    Part("Motivaciones y objetivos", listOf(p.motivations, p.goals).filter { it.isNotBlank() }.joinToString("\n"), forPlayers = false)
    Part("Historia", p.history, forPlayers = false)
    Part("Notas", p.notes, forPlayers = false)
    val scenes = index.scenesOf(npc)
    if (scenes.isNotEmpty()) {
        SectionLabel("Escenas")
        scenes.forEach { scene -> WalkLine(scene.item.displayName, scene.payload.purpose, 0, false) { onOpen(scene.item.identity.id) } }
    }
}

@Composable
private fun PlaceSheet(index: SceneIndex, place: PlaceContent) {
    val p = place.payload
    PaperReference(p.paperReferences)
    Part("Al llegar", p.playerSafeText.ifBlank { p.presentation }, forPlayers = true)
    if (p.playerSafeText.isNotBlank()) Part("Presentación", p.presentation, forPlayers = false)
    Part("Resumen", p.summary, forPlayers = false)
    val items = index.links(place.item.identity.id).shopItems
    if (p.kind == PlaceKind.SHOP || items.isNotEmpty()) {
        SectionLabel("Qué vende")
        if (items.isEmpty()) {
            Hint(p.services.joinToString(" · ").ifBlank { "Sin lista de artículos todavía." })
        } else {
            items.sortedByDescending { it.highlighted }.forEach { item ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Text((if (item.highlighted) "⭐ " else "") + item.name, style = MaterialTheme.typography.bodyMedium, fontWeight = if (item.highlighted) FontWeight.Bold else FontWeight.Normal, modifier = Modifier.weight(1f))
                    Text(item.price, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
                if (item.note.isNotBlank()) Text(item.note, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 12.dp))
            }
        }
    } else if (p.services.isNotEmpty()) {
        Part("Servicios", p.services.joinToString("\n"), forPlayers = false)
    }
    Part("Para interactuar", p.interactives.joinToString("\n"), forPlayers = false)
    Part("Ganchos", p.hooks.joinToString("\n"), forPlayers = false)
    Part("Notas del DM", p.dmNotes, forPlayers = false)
}

@Composable
private fun ZoneSheet(zone: ZoneContent) {
    val p = zone.payload
    PaperReference(p.paperReferences)
    Part("Presentación", p.playerSafeText.ifBlank { p.presentation }, forPlayers = true)
    Part("Espacio", p.space, forPlayers = false)
    Part("Exploración", p.exploration, forPlayers = false)
    Part("Para interactuar", p.interactives.joinToString("\n"), forPlayers = false)
    Part("Pistas", p.clues.joinToString("\n"), forPlayers = false)
    Part("Pruebas", p.checks.joinToString("\n"), forPlayers = false)
    Part("Consecuencias", p.consequences.joinToString("\n"), forPlayers = false)
    Part("Encuentro", p.encounterBrief, forPlayers = false)
    Part("Ayuda para el DM", p.dmGuidance, forPlayers = false)
}

@Composable
private fun SceneSheet(index: SceneIndex, scene: SceneContent, onOpen: (Uuid) -> Unit) {
    val l = index.links(scene.item.identity.id)
    if (l.adventure.isNotBlank()) Text(listOfNotNull(l.adventure, l.order?.let { "escena $it" }, if (l.detour) "desvío" else null).joinToString(" · "), style = MaterialTheme.typography.bodySmall)
    PaperReference(scene.payload.references)
    Part("Para qué sirve", scene.payload.purpose, forPlayers = false)
    Part("Notas", scene.payload.notes, forPlayers = false)
    val next = index.nextScenes(scene)
    if (next.isNotEmpty() || scene.payload.possibleNextScenes.isNotEmpty()) {
        SectionLabel("Puede seguir")
        next.forEach { WalkLine("→ ${it.item.displayName}", it.payload.purpose, 0, false) { onOpen(it.item.identity.id) } }
        scene.payload.possibleNextScenes.filter { text -> next.none { it.item.displayName.equals(text, ignoreCase = true) } }.forEach { Hint("→ $it") }
    }
}

// ---------------------------------------------------------------- Relacionado

@Composable
private fun RelatedColumn(index: SceneIndex, id: Uuid, onOpen: (Uuid) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().dmScroll().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Relacionado", style = MaterialTheme.typography.titleMedium)
        val place = index.place(id)
        val zone = index.zone(id) ?: place?.let(index::zoneOf)
        val scene = index.scene(id)
        val npc = index.npc(id)

        // Who attends here, ready to play: «cómo llevarlo» and the full sheet one tap away.
        if (npc == null) {
            val attending = (place?.let { index.links(it.item.identity.id).npcIds.mapNotNull(index::npc) }.orEmpty() + index.npcsHere(id)).distinctBy { it.item.identity.id }.take(3)
            attending.forEach { NpcQuickCard(it, onOpen) }
        }
        if (npc != null) {
            val places = index.placesOf(npc)
            val zones = index.zonesOf(npc)
            if (places.isNotEmpty() || zones.isNotEmpty()) SectionLabel("Dónde está")
            places.forEach { p ->
                WalkLine("📍 ${p.item.displayName}", index.pathOf(p.item.identity.id), 0, false) { onOpen(p.item.identity.id) }
            }
            zones.forEach { z ->
                WalkLine("📍 ${z.item.displayName}", z.payload.summary, 0, false) { onOpen(z.item.identity.id) }
            }
            val scenes = index.scenesOf(npc)
            if (scenes.isNotEmpty()) {
                SectionLabel("Aparece en")
                scenes.forEach { s -> WalkLine(s.item.displayName, s.payload.purpose, 0, false) { onOpen(s.item.identity.id) } }
            }
        }
        if (zone != null) {
            SectionLabel(if (place != null) "También en ${zone.item.displayName}" else "En esta zona")
            if (place != null) WalkLine(zone.item.displayName, "la zona", 0, false, bold = true) { onOpen(zone.item.identity.id) }
            index.placesIn(zone).filter { it.item.identity.id != id }.forEach { other ->
                WalkLine((if (other.payload.kind == PlaceKind.SHOP) "🛒 " else "") + other.item.displayName, other.payload.function, 1, false) { onOpen(other.item.identity.id) }
            }
        }
        if (scene != null) {
            val next = index.nextScenes(scene)
            if (next.isNotEmpty()) {
                SectionLabel("Escena siguiente")
                next.forEach { WalkLine("→ ${it.item.displayName}", it.payload.purpose, 0, false) { onOpen(it.item.identity.id) } }
            }
            (index.links(scene.item.identity.id).placeIds + index.links(scene.item.identity.id).zoneIds).forEach { where ->
                WalkLine("📍 ${index.nameOf(where).orEmpty()}", index.pathOf(where), 0, false) { onOpen(where) }
            }
        }
    }
}

@Composable
private fun NpcQuickCard(npc: NpcContent, onOpen: (Uuid) -> Unit) {
    val p = npc.payload
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(p.conceptRole.ifBlank { "PNJ" }, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(npc.item.displayName, style = MaterialTheme.typography.bodyMedium)
            val howTo = listOf(p.personalityManner, p.voiceMannerisms).filter { it.isNotBlank() }.joinToString(" ")
            if (howTo.isNotBlank()) Text("Cómo llevarlo: $howTo", style = MaterialTheme.typography.bodySmall, maxLines = 4)
            if (p.canOffer.isNotBlank()) Text("Ofrece: ${p.canOffer}", style = MaterialTheme.typography.bodySmall, maxLines = 2)
            TextButton(onClick = { onOpen(npc.item.identity.id) }) { Text("Ficha completa ▸") }
        }
    }
}

// ---------------------------------------------------------------- Mapa (P35)

@Composable
private fun MapView(index: SceneIndex, ownerId: Uuid, mediaBlobs: MediaBlobStore, onOpen: (Uuid) -> Unit, onClose: () -> Unit) {
    val handout = index.mapOf(ownerId) ?: return
    val pins = index.links(handout.item.identity.id).pins
    val image = rememberHandoutImage(handout, mediaBlobs, 2048)
    Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🗺 ${index.nameOf(ownerId).orEmpty()}", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            TextButton(onClick = onClose) { Text("Volver a la ficha") }
        }
        if (image == null) {
            Hint("La imagen del mapa todavía no llegó a este aparato. Sincroniza y vuelve a abrirlo.")
            return
        }
        Hint("Toca una marca para abrir su ficha.")
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().aspectRatio(image.width.toFloat() / image.height.coerceAtLeast(1))) {
            val w = constraints.maxWidth.toFloat()
            val h = constraints.maxHeight.toFloat()
            val half = with(LocalDensity.current) { 16.dp.roundToPx() }
            Image(bitmap = image, contentDescription = handout.item.displayName, contentScale = ContentScale.FillBounds, modifier = Modifier.fillMaxSize())
            pins.forEachIndexed { i, pin ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .offset { IntOffset((pin.x * w).roundToInt() - half, (pin.y * h).roundToInt() - half) }
                        .size(32.dp)
                        .background(Color(0xFFC62828), CircleShape)
                        .clickable(enabled = pin.targetId != null) { pin.targetId?.let(onOpen) },
                ) { Text("${i + 1}", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
        pins.forEachIndexed { i, pin ->
            val target = pin.targetId?.let(index::nameOf)
            WalkLine("${i + 1}. ${pin.label.ifBlank { target.orEmpty() }}", target.orEmpty(), 0, false) { pin.targetId?.let(onOpen) }
        }
    }
}

// ---------------------------------------------------------------- Piezas

@Composable
private fun rememberHandoutImage(handout: HandoutContent, mediaBlobs: MediaBlobStore, maxSide: Int): ImageBitmap? {
    val platform = LocalDmPlatform.current
    var image by remember(handout.payload.sha256, maxSide) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(handout.payload.sha256, maxSide) {
        image = withContext(Dispatchers.Default) { mediaBlobs.read(handout.payload.sha256)?.let { platform.decodeImage(it, maxSide) } }
    }
    return image
}

@Composable
private fun Thumbnail(handout: HandoutContent, mediaBlobs: MediaBlobStore, sizeDp: Int) {
    val image = rememberHandoutImage(handout, mediaBlobs, sizeDp * 3)
    Box(
        modifier = Modifier.size(sizeDp.dp).background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small),
        contentAlignment = Alignment.Center,
    ) {
        if (image != null) Image(image, contentDescription = handout.item.displayName, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp, bottom = 2.dp))
}

@Composable
private fun Hint(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

private fun firstLine(text: String): String = text.trim().substringBefore('\n').substringBefore(". ").take(120)

