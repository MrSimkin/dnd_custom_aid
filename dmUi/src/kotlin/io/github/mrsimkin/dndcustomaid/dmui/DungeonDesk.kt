package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NoteContextKind
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContent
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.ActivityDeclaration
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatDocument
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.DmDungeonRepository
import io.github.mrsimkin.dndcustomaid.shared.dm.DungeonActivity
import io.github.mrsimkin.dndcustomaid.shared.dm.DungeonClock
import io.github.mrsimkin.dndcustomaid.shared.dm.DungeonDocument
import io.github.mrsimkin.dndcustomaid.shared.dm.DungeonEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.DungeonMapZone
import io.github.mrsimkin.dndcustomaid.shared.dm.DungeonRules
import io.github.mrsimkin.dndcustomaid.shared.dm.PartyGroup
import io.github.mrsimkin.dndcustomaid.shared.dm.SceneIndex
import io.github.mrsimkin.dndcustomaid.shared.dm.ZoneMark
import io.github.mrsimkin.dndcustomaid.shared.dm.dungeonActivities
import io.github.mrsimkin.dndcustomaid.shared.dm.dungeonMap
import io.github.mrsimkin.dndcustomaid.shared.dm.dungeons
import kotlin.math.roundToInt
import kotlin.uuid.Uuid

/*
 * Base para DM · Mesa de Dungeon (approved design DM_UX_APROBADA.md §5; P16-P18, P20-P23).
 *
 * Left: the zone diagram (P23), with state marks, the group (G) and the creatures (🔴) placed by
 * hand; nothing moves by itself. Right: the Dungeon Turn (one tap per character, the previous
 * Activity only as reference, the group's advance only as information), the open zones as papers in
 * tabs (P18), the automatic log and the clocks (P20). A successful ambush proposes a surprise round
 * and opens the Mesa de Combate (P22).
 */

private sealed interface DungeonTab {
    data object Turn : DungeonTab
    data object Log : DungeonTab
    data object Clocks : DungeonTab
    data class Zone(val id: Uuid) : DungeonTab
}

/** What the turn closing asks the DM about. */
private data class AmbushQuestion(val ambusherIds: List<Uuid>)

@Composable
internal fun DungeonDesk(
    campaign: Campaign,
    database: AppDatabase,
    version: Int,
    onChanged: () -> Unit,
    onStartCombat: (CombatDocument) -> Unit,
) {
    val repository = remember { DmDungeonRepository(database) }
    val index = remember(campaign.id, version) { SceneIndex.load(database, campaign.id) }
    val activities = remember(campaign.id, version) { dungeonActivities(database, campaign.id) }
    val pcs = remember(campaign.id, version) { CharacterRepository(database).listCharacters(campaign.id).sortedBy { it.name.lowercase() } }
    val names = pcs.associate { it.id to it.name }
    val deskContext = LocalDeskContext.current
    var docs by remember(campaign.id, version) { mutableStateOf(repository.list(campaign.id).filter { !it.ended }) }
    var currentId by remember(campaign.id) { mutableStateOf<Uuid?>(null) }
    var tab by remember { mutableStateOf<DungeonTab>(DungeonTab.Turn) }
    var openZones by remember { mutableStateOf(listOf<Uuid>()) }
    var moving by remember { mutableStateOf<Uuid?>(null) }
    var starting by remember { mutableStateOf(false) }
    var splitting by remember { mutableStateOf(false) }
    var interrupting by remember { mutableStateOf(false) }
    var ambush by remember { mutableStateOf<AmbushQuestion?>(null) }
    var filledClocks by remember { mutableStateOf<List<DungeonClock>>(emptyList()) }
    var addingCreatures by remember { mutableStateOf(false) }
    var confirmEnd by remember { mutableStateOf(false) }

    val doc = docs.firstOrNull { it.id == currentId } ?: docs.firstOrNull()

    fun save(next: DungeonDocument) {
        val saved = repository.save(next, nowEpochSeconds())
        docs = repository.list(campaign.id).filter { !it.ended }
        currentId = saved.id
        onChanged()
    }

    fun openZone(id: Uuid) {
        if (id !in openZones) openZones = openZones + id
        tab = DungeonTab.Zone(id)
        deskContext.value = DeskContext(NoteContextKind.ZONE, id, index.nameOf(id).orEmpty())
    }

    fun zoneNames(): Map<Uuid, String> = index.zones.associate { it.item.identity.id to it.item.displayName }

    fun startCombat(encounterIds: List<Uuid>, surpriseCharacterIds: List<Uuid>, zoneName: String) {
        onStartCombat(combatFromDungeon(database, campaign, zoneName.ifBlank { doc?.dungeon ?: "Dungeon" }, encounterIds, surpriseCharacterIds))
    }

    if (doc == null || starting) {
        StartDungeon(
            index = index,
            hasOpen = doc != null,
            onStart = { dungeon, startZone ->
                starting = false
                save(DungeonEngine.new(campaign.id, dungeon, pcs.map { it.id to it.name }, startZone, nowEpochSeconds()))
            },
            onCancel = { starting = false },
        )
        return
    }
    deskContext.value = deskContext.value ?: DeskContext(NoteContextKind.DUNGEON, doc.id, doc.dungeon)
    val map = remember(index, doc.dungeon) { index.dungeonMap(doc.dungeon) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Wraps to a second line when a side panel narrows the desk.
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            docs.forEach { d ->
                FilterChip(selected = d.id == doc.id, onClick = { currentId = d.id; openZones = emptyList(); tab = DungeonTab.Turn }, label = { Text(d.dungeon) })
            }
            TextButton(onClick = { starting = true }) { Text("＋ Otra") }
            Text("Turno ${doc.turn}", style = MaterialTheme.typography.titleMedium, maxLines = 1, modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 8.dp))
            Button(onClick = {
                val closed = DungeonEngine.closeTurn(doc, activities, names, zoneNames())
                save(closed.document)
                if (closed.clocksFilled.isNotEmpty()) filledClocks = closed.clocksFilled
                if (closed.ambusherIds.isNotEmpty()) ambush = AmbushQuestion(closed.ambusherIds)
                tab = DungeonTab.Turn
            }) { Text("Cerrar turno") }
            OutlinedButton(onClick = { splitting = true }) { Text("Separar grupo") }
            OutlinedButton(onClick = { interrupting = true }) { Text("Interrumpido por combate") }
            TextButton(onClick = { confirmEnd = true }) { Text("Terminar") }
        }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(1.25f).fillMaxHeight().padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DungeonMapView(
                    map = map,
                    doc = doc,
                    moving = moving,
                    onZone = { zoneId ->
                        val marker = moving
                        if (marker != null) {
                            save(
                                if (doc.groups.any { it.id == marker }) DungeonEngine.moveGroup(doc, marker, zoneId)
                                else DungeonEngine.moveCreatures(doc, marker, zoneId),
                            )
                            moving = null
                        } else {
                            openZone(zoneId)
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                )
                MarkerTray(
                    doc = doc,
                    zoneNames = zoneNames(),
                    moving = moving,
                    onSelect = { moving = if (moving == it) null else it },
                    onCount = { id, count -> save(DungeonEngine.setCreatureCount(doc, id, count)) },
                    onAddCreatures = { addingCreatures = true },
                    onMerge = { groupId -> doc.groups.firstOrNull { it.id != groupId }?.let { save(DungeonEngine.merge(doc, groupId, it.id)) } },
                )
            }
            Surface(tonalElevation = 1.dp, modifier = Modifier.weight(1f).fillMaxHeight()) {
                Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        FilterChip(selected = tab == DungeonTab.Turn, onClick = { tab = DungeonTab.Turn }, label = { Text("Turno") })
                        openZones.forEach { id ->
                            FilterChip(
                                selected = tab == DungeonTab.Zone(id),
                                onClick = { openZone(id) },
                                label = { Text(index.nameOf(id).orEmpty(), maxLines = 1, overflow = TextOverflow.Ellipsis) },
                                trailingIcon = {
                                    Text("✕", modifier = Modifier.clickable {
                                        // Closing a paper never marks the zone (§5.2).
                                        openZones = openZones - id
                                        if (tab == DungeonTab.Zone(id)) tab = DungeonTab.Turn
                                    })
                                },
                            )
                        }
                        FilterChip(selected = tab == DungeonTab.Log, onClick = { tab = DungeonTab.Log }, label = { Text("Registro") })
                        FilterChip(selected = tab == DungeonTab.Clocks, onClick = { tab = DungeonTab.Clocks }, label = { Text("⏱ Relojes") })
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (val t = tab) {
                            DungeonTab.Turn -> TurnPanel(doc, activities, pcs.map { it.id to it.name }, zoneNames(), onChange = ::save)
                            DungeonTab.Log -> LogPanel(doc)
                            DungeonTab.Clocks -> DungeonClocks(doc, onChange = ::save)
                            is DungeonTab.Zone -> index.zone(t.id)?.let { zone ->
                                ZonePaper(index, database, zone, doc, onChange = ::save, onPrepare = { encounterId -> startCombat(listOf(encounterId), emptyList(), zone.item.displayName) })
                            }
                        }
                    }
                }
            }
        }
    }

    if (splitting) {
        SplitDialog(doc, names, onDismiss = { splitting = false }) { groupId, ids, name ->
            save(DungeonEngine.split(doc, groupId, ids, name))
            splitting = false
        }
    }
    if (interrupting) {
        InterruptDialog(doc, names, onDismiss = { interrupting = false }) { completed ->
            save(DungeonEngine.interruptByCombat(doc, activities, names, zoneNames(), completed))
            interrupting = false
            val zoneId = doc.groups.firstOrNull()?.zoneId
            startCombat(zoneId?.let { index.links(it).encounterIds }.orEmpty(), emptyList(), zoneId?.let(index::nameOf).orEmpty())
        }
    }
    ambush?.let { question ->
        AmbushDialog(question, names, onDismiss = { ambush = null }) { surpriseIds ->
            ambush = null
            val zoneId = doc.groups.firstOrNull { g -> g.characterIds.any { it in question.ambusherIds } }?.zoneId
            startCombat(zoneId?.let { index.links(it).encounterIds }.orEmpty(), surpriseIds, zoneId?.let(index::nameOf).orEmpty())
        }
    }
    if (filledClocks.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { filledClocks = emptyList() },
            title = { Text("⏱ Reloj lleno") },
            text = { Text("${filledClocks.joinToString(", ") { it.name }} se llenó. La App no aplica nada: decide tú qué pasa.") },
            confirmButton = { TextButton(onClick = { filledClocks = emptyList() }) { Text("Entendido") } },
        )
    }
    if (addingCreatures) {
        AddCreaturesDialog(index, doc, onDismiss = { addingCreatures = false }) { label, count, zoneId ->
            save(DungeonEngine.addCreatures(doc, label, count, zoneId))
            addingCreatures = false
        }
    }
    if (confirmEnd) {
        AlertDialog(
            onDismissRequest = { confirmEnd = false },
            title = { Text("¿Terminar la exploración de ${doc.dungeon}?") },
            text = { Text("El registro queda guardado en este aparato. Las marcas de las zonas no se aplican a las fichas.") },
            confirmButton = { Button(onClick = { confirmEnd = false; save(DungeonEngine.end(doc)) }) { Text("Terminar") } },
            dismissButton = { TextButton(onClick = { confirmEnd = false }) { Text("Cancelar") } },
        )
    }
}

/** A combat opened from the dungeon: the PCs, the zone's encounters and, after an ambush, its surprise actors. */
internal fun combatFromDungeon(database: AppDatabase, campaign: Campaign, name: String, encounterIds: List<Uuid>, surpriseCharacterIds: List<Uuid>): CombatDocument {
    val reusable = ReusableContentRepository(database)
    val creatureRepo = CreatureContentRepository(database, reusable)
    val creatures = reusable.listCampaign(campaign.id, ReusableContentFamily.CREATURE)
        .mapNotNull { runCatching { creatureRepo.creature(it.identity.id) }.getOrNull() }
        .associateBy { it.item.identity.id }
    val encounters = EncounterContentRepository(database, reusable)
    var doc = CombatEngine.addPcs(CombatEngine.new(campaign.id, name, nowEpochSeconds()), CharacterRepository(database).listCharacters(campaign.id))
    encounterIds.mapNotNull { runCatching { encounters.encounter(it) }.getOrNull() }.forEach { doc = CombatEngine.addEncounter(doc, it, creatures) }
    val actors = doc.combatants.filter { it.characterId in surpriseCharacterIds }.map { it.id }
    return CombatEngine.setSurpriseActors(doc, actors)
}

// ---------------------------------------------------------------- Empezar

@Composable
private fun StartDungeon(index: SceneIndex, hasOpen: Boolean, onStart: (String, Uuid?) -> Unit, onCancel: () -> Unit) {
    val dungeons = index.dungeons()
    var chosen by remember { mutableStateOf(dungeons.firstOrNull()) }
    var startZone by remember { mutableStateOf<Uuid?>(null) }
    Column(modifier = Modifier.fillMaxSize().dmScroll().padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(if (hasOpen) "Otro dungeon" else "Empezar a explorar", style = MaterialTheme.typography.titleLarge)
        if (dungeons.isEmpty()) {
            Text("Esta campaña aún no tiene dungeons con zonas.", style = MaterialTheme.typography.bodyMedium)
            Text(
                "En la App de PC, «Preparar → Dungeons» crea un dungeon, le agrega zonas en un plano y las une con pasajes; llega aquí al sincronizar. " +
                    "También puedes empezar ahora sin mapa: el turno, el registro y los relojes funcionan igual.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Text("Dungeon", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            dungeons.forEach { d -> FilterChip(selected = chosen == d, onClick = { chosen = d; startZone = null }, label = { Text(d) }) }
            FilterChip(selected = chosen == null, onClick = { chosen = null; startZone = null }, label = { Text("Sin mapa") })
        }
        val zones = chosen?.let { index.dungeonMap(it) }.orEmpty()
        if (zones.isNotEmpty()) {
            Text("El grupo entra por", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                zones.forEach { z -> FilterChip(selected = startZone == z.zone.item.identity.id, onClick = { startZone = z.zone.item.identity.id }, label = { Text(z.zone.item.displayName) }) }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onStart(chosen ?: "Dungeon", startZone) }) { Text("Empezar ▸") }
            if (hasOpen) TextButton(onClick = onCancel) { Text("Cancelar") }
        }
    }
}

// ---------------------------------------------------------------- Mapa (P23)

@Composable
private fun DungeonMapView(map: List<DungeonMapZone>, doc: DungeonDocument, moving: Uuid?, onZone: (Uuid) -> Unit, modifier: Modifier) {
    if (map.isEmpty()) {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium), contentAlignment = Alignment.Center) {
            Text("Sin mapa: este dungeon no tiene zonas ubicadas. El turno funciona igual.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp))
        }
        return
    }
    val lineColor = MaterialTheme.colorScheme.outline
    val secretColor = MaterialTheme.colorScheme.error
    BoxWithConstraints(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), MaterialTheme.shapes.medium)) {
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        val density = LocalDensity.current
        val boxW = with(density) { 132.dp.toPx() }
        val boxH = with(density) { 64.dp.toPx() }
        fun center(z: DungeonMapZone) = Offset((z.x * (w - boxW) + boxW / 2).toFloat(), (z.y * (h - boxH) + boxH / 2).toFloat())
        val byId = map.associateBy { it.zone.item.identity.id }
        Canvas(modifier = Modifier.fillMaxSize()) {
            map.forEach { z ->
                z.passages.forEach { p ->
                    val other = byId[p.toZoneId] ?: return@forEach
                    if (z.zone.item.identity.id.toString() < p.toZoneId.toString()) {
                        drawLine(
                            color = if (p.secret) secretColor else lineColor,
                            start = center(z),
                            end = center(other),
                            strokeWidth = 4f,
                            pathEffect = if (p.secret) PathEffect.dashPathEffect(floatArrayOf(14f, 10f)) else null,
                        )
                    }
                }
            }
        }
        map.forEach { z ->
            val id = z.zone.item.identity.id
            val marks = doc.marks(id)
            val groupsHere = doc.groups.filter { it.zoneId == id }
            val creaturesHere = doc.creatures.filter { it.zoneId == id }
            Column(
                modifier = Modifier
                    .offset { IntOffset((z.x * (w - boxW)).roundToInt(), (z.y * (h - boxH)).roundToInt()) }
                    .width(132.dp)
                    .height(64.dp)
                    .background(
                        if (marks.contains(ZoneMark.DANGER)) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface,
                        MaterialTheme.shapes.small,
                    )
                    .border(if (moving != null) 2.dp else 1.dp, if (moving != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, MaterialTheme.shapes.small)
                    .handCursor()
                    .clickable { onZone(id) }
                    .padding(horizontal = 6.dp, vertical = 3.dp),
            ) {
                Text(z.zone.item.displayName, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(marks.joinToString(" ") { it.icon }, style = MaterialTheme.typography.labelSmall, maxLines = 1)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    groupsHere.forEach { g -> MarkerBadge("G", MaterialTheme.colorScheme.primary, if (doc.groups.size > 1) g.name.take(1) else null) }
                    creaturesHere.forEach { c -> MarkerBadge("🔴${c.count}", Color(0xFFC62828), null) }
                }
            }
        }
        if (moving != null) {
            Text("Toca una zona para ubicarlo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.TopEnd).padding(6.dp))
        }
    }
}

@Composable
private fun MarkerBadge(text: String, color: Color, suffix: String?) {
    Text(
        text + (suffix?.let { "·$it" } ?: ""),
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.background(color, MaterialTheme.shapes.extraSmall).padding(horizontal = 4.dp),
    )
}

@Composable
private fun MarkerTray(
    doc: DungeonDocument,
    zoneNames: Map<Uuid, String>,
    moving: Uuid?,
    onSelect: (Uuid) -> Unit,
    onCount: (Uuid, Int) -> Unit,
    onAddCreatures: () -> Unit,
    onMerge: (Uuid) -> Unit,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        doc.groups.forEach { g ->
            FilterChip(
                selected = moving == g.id,
                onClick = { onSelect(g.id) },
                label = { Text("G ${g.name} · ${g.zoneId?.let(zoneNames::get) ?: "sin ubicar"}") },
            )
            if (doc.groups.size > 1 && g != doc.groups.first()) TextButton(onClick = { onMerge(g.id) }) { Text("Juntar") }
        }
        doc.creatures.forEach { c ->
            FilterChip(selected = moving == c.id, onClick = { onSelect(c.id) }, label = { Text("🔴 ${c.label} ×${c.count} · ${c.zoneId?.let(zoneNames::get) ?: "sin ubicar"}") })
            TextButton(onClick = { onCount(c.id, c.count - 1) }) { Text("−") }
            TextButton(onClick = { onCount(c.id, c.count + 1) }) { Text("+") }
        }
        TextButton(onClick = onAddCreatures) { Text("＋ Criaturas") }
    }
    Text("Toca un marcador y luego una zona: nada se mueve solo.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

// ---------------------------------------------------------------- Turno (P16, P17)

@Composable
private fun TurnPanel(
    doc: DungeonDocument,
    activities: List<DungeonActivity>,
    pcs: List<Pair<Uuid, String>>,
    zoneNames: Map<Uuid, String>,
    onChange: (DungeonDocument) -> Unit,
) {
    val names = pcs.toMap()
    Column(modifier = Modifier.fillMaxSize().dmScroll(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Turno de Dungeon · 10 minutos", style = MaterialTheme.typography.titleSmall)
        doc.groups.forEach { group ->
            val advance = DungeonEngine.groupAdvance(doc, activities, group)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${group.name} · ${group.zoneId?.let(zoneNames::get) ?: "sin ubicar"}", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
                        // Shown only as information: the App does not move anyone (P16).
                        Text(advance?.let { "${it.label} (hasta ${it.zones})" } ?: "Sin actividades aún", style = MaterialTheme.typography.labelMedium)
                    }
                    group.characterIds.forEach { id -> CharacterTurnRow(doc, activities, id, names, group, onChange) }
                }
            }
        }
        if (pcs.isEmpty()) Text("Esta campaña no tiene PJ en el aparato.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun CharacterTurnRow(
    doc: DungeonDocument,
    activities: List<DungeonActivity>,
    characterId: Uuid,
    names: Map<Uuid, String>,
    group: PartyGroup,
    onChange: (DungeonDocument) -> Unit,
) {
    val current = doc.declaration(characterId)
    val previous = doc.previousOf(characterId)
    val currentActivity = current?.let { DungeonEngine.activity(activities, it.activityKey) }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(names[characterId] ?: "PJ", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            previous?.let { p ->
                // The previous Activity is a reference, never carried over (P16).
                Text("Antes: ${DungeonEngine.activity(activities, p.activityKey)?.displayName ?: "—"}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            activities.forEach { activity ->
                FilterChip(
                    selected = current?.activityKey == activity.key,
                    onClick = {
                        onChange(
                            if (current?.activityKey == activity.key) DungeonEngine.clearDeclaration(doc, characterId)
                            else DungeonEngine.declare(doc, ActivityDeclaration(characterId, activity.key)),
                        )
                    },
                    label = { Text(activity.displayName, style = MaterialTheme.typography.labelSmall) },
                )
            }
        }
        if (current != null && currentActivity != null) {
            if (currentActivity.needsFocus) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Foco:", style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.CenterVertically))
                    DungeonRules.searchFocuses.forEach { focus ->
                        FilterChip(selected = current.focus == focus, onClick = { onChange(DungeonEngine.declare(doc, current.copy(focus = focus))) }, label = { Text(focus, style = MaterialTheme.typography.labelSmall) })
                    }
                    if (currentActivity.thoroughAdvance != null) {
                        FilterChip(selected = current.thorough, onClick = { onChange(DungeonEngine.declare(doc, current.copy(thorough = !current.thorough))) }, label = { Text("A fondo", style = MaterialTheme.typography.labelSmall) })
                    }
                }
            }
            if (currentActivity.helps) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Ayuda a:", style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.CenterVertically))
                    group.characterIds.filter { it != characterId }.forEach { other ->
                        FilterChip(
                            selected = current.helpsCharacterId == other,
                            onClick = { onChange(DungeonEngine.declare(doc, current.copy(helpsCharacterId = other))) },
                            label = { Text(names[other] ?: "PJ", style = MaterialTheme.typography.labelSmall) },
                        )
                    }
                }
            }
            Text("${currentActivity.cost} · ${currentActivity.benefit}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        HorizontalDivider()
    }
}

@Composable
private fun LogPanel(doc: DungeonDocument) {
    Column(modifier = Modifier.fillMaxSize().dmScroll(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Registro de turnos", style = MaterialTheme.typography.titleSmall)
        if (doc.log.isEmpty()) Text("Se arma solo al cerrar cada turno.", style = MaterialTheme.typography.bodySmall)
        doc.log.reversed().forEach { record ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "Turno ${record.turn}" + if (record.interruptedByCombat) " · interrumpido por combate" else "",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    record.groups.forEach { g ->
                        Text("${g.groupName}${if (g.zoneName.isNotBlank()) " en ${g.zoneName}" else ""} · ${g.advance.label}", style = MaterialTheme.typography.labelMedium)
                        g.activities.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                    }
                    if (record.completed.isNotEmpty()) Text("Completaron: ${record.completed.joinToString(", ")}", style = MaterialTheme.typography.bodySmall)
                    if (record.clocksFilled.isNotEmpty()) Text("⏱ Se llenó: ${record.clocksFilled.joinToString(", ")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Relojes (P20)

@Composable
private fun DungeonClocks(doc: DungeonDocument, onChange: (DungeonDocument) -> Unit) {
    var name by remember { mutableStateOf("") }
    var segments by remember { mutableStateOf(4) }
    var perTurn by remember { mutableStateOf(true) }
    Column(modifier = Modifier.fillMaxSize().dmScroll(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Relojes de ${doc.dungeon}", style = MaterialTheme.typography.titleSmall)
        doc.clocks.forEach { clock ->
            ClockRow(clock, onMinus = { onChange(DungeonEngine.tickClock(doc, clock.id, -1)) }, onPlus = { onChange(DungeonEngine.tickClock(doc, clock.id, 1)) }, onRemove = { onChange(DungeonEngine.removeClock(doc, clock.id)) })
        }
        HorizontalDivider()
        DmTextField(value = name, onValueChange = { name = it }, label = { Text("Reloj nuevo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(4, 6, 8, 10, 12).forEach { n -> FilterChip(selected = segments == n, onClick = { segments = n }, label = { Text("$n") }) }
            FilterChip(selected = perTurn, onClick = { perTurn = !perTurn }, label = { Text("+1 por turno") })
        }
        Button(enabled = name.isNotBlank(), onClick = { onChange(DungeonEngine.addClock(doc, name, segments, perTurn)); name = "" }) { Text("Agregar reloj") }
        Text("Al llenarse avisa; no aplica consecuencias.", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
internal fun ClockRow(clock: DungeonClock, onMinus: () -> Unit, onPlus: () -> Unit, onRemove: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(clock.name + if (clock.perTurn) " · +1 por turno" else "", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(clock.segments) { i ->
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(12.dp)
                            .background(if (i < clock.filled) (if (clock.isFull) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary) else MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.extraSmall),
                    )
                }
            }
        }
        TextButton(onClick = onMinus) { Text("−") }
        TextButton(onClick = onPlus) { Text("+") }
        TextButton(onClick = onRemove) { Text("✕") }
    }
}

// ---------------------------------------------------------------- Ficha de zona (P18)

private enum class ZoneSection(val label: String) {
    PRESENTATION("Presentación"),
    SPACE("Espacio"),
    EXPLORATION("Exploración"),
    FINDINGS("Hallazgos"),
    DM_HELP("Ayuda DM"),
    ENCOUNTER("Encuentro"),
    REFERENCES("Referencias"),
    NPCS("PNJ aquí"),
}

@Composable
private fun ZonePaper(index: SceneIndex, database: AppDatabase, zone: ZoneContent, doc: DungeonDocument, onChange: (DungeonDocument) -> Unit, onPrepare: (Uuid) -> Unit) {
    val p = zone.payload
    val id = zone.item.identity.id
    val encounters = remember(id) {
        val repo = EncounterContentRepository(database)
        ContentLinksRepository(database).links(id).encounterIds.mapNotNull { runCatching { repo.encounter(it) }.getOrNull() }
    }
    val npcs = index.npcsHere(id)
    // Empty sections are hidden (P18).
    val sections = ZoneSection.entries.filter { s ->
        when (s) {
            ZoneSection.PRESENTATION -> (p.playerSafeText + p.presentation + p.summary).isNotBlank()
            ZoneSection.SPACE -> p.space.isNotBlank()
            ZoneSection.EXPLORATION -> p.exploration.isNotBlank()
            ZoneSection.FINDINGS -> (p.interactives + p.clues).isNotEmpty()
            ZoneSection.DM_HELP -> p.dmGuidance.isNotBlank() || (p.checks + p.consequences).isNotEmpty()
            ZoneSection.ENCOUNTER -> p.encounterBrief.isNotBlank() || encounters.isNotEmpty()
            ZoneSection.REFERENCES -> p.paperReferences.isNotEmpty()
            ZoneSection.NPCS -> npcs.isNotEmpty()
        }
    }
    var section by remember(id) { mutableStateOf(sections.firstOrNull() ?: ZoneSection.PRESENTATION) }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(zone.item.displayName, style = MaterialTheme.typography.titleLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            ZoneMark.entries.forEach { mark ->
                FilterChip(selected = mark in doc.marks(id), onClick = { onChange(DungeonEngine.toggleMark(doc, id, mark)) }, label = { Text("${mark.icon} ${mark.label}", style = MaterialTheme.typography.labelSmall) })
            }
        }
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.width(120.dp).fillMaxHeight().dmScroll()) {
                if (sections.isEmpty()) Text("Ficha vacía.", style = MaterialTheme.typography.bodySmall)
                sections.forEach { s ->
                    Text(
                        s.label,
                        fontWeight = if (s == section) FontWeight.Bold else FontWeight.Normal,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (s == section) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent, MaterialTheme.shapes.small)
                            .clickable { section = s }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                    )
                }
            }
            Column(modifier = Modifier.weight(1f).fillMaxHeight().dmScroll().padding(start = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                when (section) {
                    ZoneSection.PRESENTATION -> {
                        PaperPart("Para leer", p.playerSafeText.ifBlank { p.presentation }, forPlayers = true)
                        if (p.playerSafeText.isNotBlank()) PaperPart("Presentación", p.presentation, forPlayers = false)
                        PaperPart("Resumen", p.summary, forPlayers = false)
                    }
                    ZoneSection.SPACE -> PaperPart("Espacio", p.space, forPlayers = false)
                    ZoneSection.EXPLORATION -> PaperPart("Exploración", p.exploration, forPlayers = false)
                    ZoneSection.FINDINGS -> {
                        PaperPart("Para interactuar", p.interactives.joinToString("\n"), forPlayers = false)
                        PaperPart("Pistas", p.clues.joinToString("\n"), forPlayers = false)
                    }
                    ZoneSection.DM_HELP -> {
                        PaperPart("Ayuda para el DM", p.dmGuidance, forPlayers = false)
                        PaperPart("Pruebas", p.checks.joinToString("\n"), forPlayers = false)
                        PaperPart("Consecuencias", p.consequences.joinToString("\n"), forPlayers = false)
                    }
                    ZoneSection.ENCOUNTER -> {
                        PaperPart("Encuentro", p.encounterBrief, forPlayers = false)
                        encounters.forEach { e ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${e.item.displayName} (${e.payload.participants.sumOf { it.quantity }})", modifier = Modifier.weight(1f))
                                Button(onClick = { onPrepare(e.item.identity.id) }) { Text("Preparar ▸ ⚔") }
                            }
                        }
                    }
                    ZoneSection.REFERENCES -> Text("📄 ${p.paperReferences.joinToString(" · ")}", style = MaterialTheme.typography.bodyMedium)
                    ZoneSection.NPCS -> npcs.forEach { npc ->
                        Text("${npc.payload.conceptRole.ifBlank { "PNJ" }} · ${npc.item.displayName}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun PaperPart(title: String, text: String, forPlayers: Boolean) {
    if (text.isBlank()) return
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row {
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

// ---------------------------------------------------------------- Ventanas

@Composable
private fun SplitDialog(doc: DungeonDocument, names: Map<Uuid, String>, onDismiss: () -> Unit, onSplit: (Uuid, List<Uuid>, String) -> Unit) {
    val from = doc.groups.firstOrNull { it.characterIds.size > 1 }
    var chosen by remember { mutableStateOf(setOf<Uuid>()) }
    var name by remember { mutableStateOf("Grupo ${doc.groups.size + 1}") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Separar grupo") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (from == null) {
                    Text("No hay un grupo con más de un PJ.")
                } else {
                    Text("Quiénes van aparte (de ${from.name}). Cada parte resuelve su avance por separado.", style = MaterialTheme.typography.bodySmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        from.characterIds.forEach { id ->
                            FilterChip(selected = id in chosen, onClick = { chosen = if (id in chosen) chosen - id else chosen + id }, label = { Text(names[id] ?: "PJ") })
                        }
                    }
                    DmTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del grupo nuevo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(enabled = from != null && chosen.isNotEmpty() && chosen.size < (from?.characterIds?.size ?: 0), onClick = { from?.let { onSplit(it.id, chosen.toList(), name) } }) { Text("Separar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
private fun InterruptDialog(doc: DungeonDocument, names: Map<Uuid, String>, onDismiss: () -> Unit, onConfirm: (List<Uuid>) -> Unit) {
    var completed by remember { mutableStateOf(setOf<Uuid>()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Interrumpido por combate") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Marca quién alcanzó a completar su actividad. Luego se abre la Mesa de Combate.", style = MaterialTheme.typography.bodySmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    doc.declarations.forEach { d ->
                        FilterChip(selected = d.characterId in completed, onClick = { completed = if (d.characterId in completed) completed - d.characterId else completed + d.characterId }, label = { Text(names[d.characterId] ?: "PJ") })
                    }
                }
                if (doc.declarations.isEmpty()) Text("Nadie había elegido actividad en este turno.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { Button(onClick = { onConfirm(completed.toList()) }) { Text("Preparar combate ▸ ⚔") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

/** P22: the App proposes a surprise round with the ambushers marked; the DM confirms or changes it. */
@Composable
private fun AmbushDialog(question: AmbushQuestion, names: Map<Uuid, String>, onDismiss: () -> Unit, onStart: (List<Uuid>) -> Unit) {
    var marked by remember { mutableStateOf(question.ambusherIds.toSet()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Salió bien la emboscada?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Si salió bien, el combate empieza con una ronda sorpresa: actúan los marcados y los enemigos quedan sorprendidos.", style = MaterialTheme.typography.bodySmall)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    names.forEach { (id, name) ->
                        FilterChip(selected = id in marked, onClick = { marked = if (id in marked) marked - id else marked + id }, label = { Text(name) })
                    }
                }
            }
        },
        confirmButton = {
            Column(horizontalAlignment = Alignment.End) {
                Button(enabled = marked.isNotEmpty(), onClick = { onStart(marked.toList()) }) { Text("Ronda sorpresa ▸ ⚔") }
                TextButton(onClick = { onStart(emptyList()) }) { Text("Empezar sin sorpresa ▸ ⚔") }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("No hubo combate") } },
    )
}

@Composable
private fun AddCreaturesDialog(index: SceneIndex, doc: DungeonDocument, onDismiss: () -> Unit, onAdd: (String, Int, Uuid?) -> Unit) {
    var label by remember { mutableStateOf("") }
    var count by remember { mutableStateOf(1) }
    var zone by remember { mutableStateOf<Uuid?>(doc.groups.firstOrNull()?.zoneId) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Criaturas en el mapa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                DmTextField(value = label, onValueChange = { label = it }, label = { Text("Qué son («Goblins»)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Cantidad: $count", modifier = Modifier.weight(1f))
                    TextButton(onClick = { count = (count - 1).coerceAtLeast(1) }) { Text("−") }
                    TextButton(onClick = { count = (count + 1).coerceAtMost(99) }) { Text("+") }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    index.dungeonMap(doc.dungeon).forEach { z ->
                        FilterChip(selected = zone == z.zone.item.identity.id, onClick = { zone = z.zone.item.identity.id }, label = { Text(z.zone.item.displayName) })
                    }
                }
            }
        },
        confirmButton = { Button(enabled = label.isNotBlank(), onClick = { onAdd(label, count, zone) }) { Text("Poner") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
