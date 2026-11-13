package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.dmui.handCursor
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.DungeonContent
import io.github.mrsimkin.dndcustomaid.shared.content.DungeonContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.DungeonPayload
import io.github.mrsimkin.dndcustomaid.shared.content.DungeonPlanZone
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutKind
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentItem
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ZoneContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlin.math.roundToInt
import kotlin.uuid.Uuid

/** Size of a zone box on the plan. */
private val ZONE_WIDTH = 150.dp
private val ZONE_HEIGHT = 46.dp

/** The App de PC side of the dungeon editor (redesign R3, approved mockup D5). */
internal class DesktopDungeonEditorController(
    private val database: AppDatabase,
    private val mediaBlobs: MediaBlobStore,
    private val now: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    private val dungeons = DungeonContentRepository(database)
    private val zones = ZoneContentRepository(database)
    private val reusable = ReusableContentRepository(database)
    private val links = ContentLinksRepository(database)
    private val handouts = HandoutContentRepository(database)

    /** The campaign's dungeons; names typed in older zones become dungeons here, once. */
    fun dungeons(campaignId: Uuid): List<DungeonContent> {
        dungeons.adoptLegacyNames(campaignId, now())
        return dungeons.listCampaign(campaignId)
    }

    fun create(campaignId: Uuid, name: String): DungeonContent = dungeons.create(campaignId, name, DungeonPayload(), now())

    fun reload(id: Uuid): DungeonContent? = dungeons.dungeon(id)

    fun save(dungeon: DungeonContent, name: String, payload: DungeonPayload): Boolean =
        dungeons.update(dungeon.item.identity.id, dungeon.item.identity.revision, name, payload, now()) is RevisionDecision.Accepted

    fun zones(campaignId: Uuid, dungeon: DungeonContent): List<DungeonPlanZone> = dungeons.zonesOf(campaignId, dungeon)

    fun addZone(campaignId: Uuid, dungeon: DungeonContent, existing: Int, x: Double, y: Double): Uuid =
        dungeons.addZone(campaignId, dungeon, "Zona ${existing + 1}", x, y, now()).item.identity.id

    fun renameZone(zone: ReusableContentItem, name: String): Boolean {
        if (name.isBlank()) return false
        val payload = zones.zone(zone.identity.id)?.payload ?: return false
        return zones.update(zone.identity.id, zone.revisionNow(), name, payload, now()) is RevisionDecision.Accepted
    }

    fun deleteZone(zone: ReusableContentItem, neighbors: List<Uuid>) {
        neighbors.forEach { dungeons.disconnect(zone.identity.id, it) }
        zones.tombstone(zone.identity.id, zone.revisionNow(), now())
    }

    fun move(zoneId: Uuid, x: Double, y: Double) = dungeons.moveZone(zoneId, x, y)

    fun connect(from: Uuid, to: Uuid) = dungeons.connect(from, to)

    fun disconnect(a: Uuid, b: Uuid) = dungeons.disconnect(a, b)

    fun setSecret(a: Uuid, b: Uuid, secret: Boolean) = dungeons.setSecret(a, b, secret)

    fun encounters(campaignId: Uuid): List<ReusableContentItem> =
        reusable.listCampaign(campaignId, ReusableContentFamily.ENCOUNTER).sortedBy { it.displayName.lowercase() }

    fun setEncounter(zoneId: Uuid, encounterId: Uuid?) {
        val current = links.links(zoneId)
        links.save(zoneId, current.copy(encounterIds = listOfNotNull(encounterId)))
    }

    fun mapHandouts(campaignId: Uuid): List<ReusableContentItem> =
        handouts.listCampaign(campaignId).filter { it.payload.kind == HandoutKind.MAP }.map { it.item }

    fun image(handoutId: Uuid?): ImageBitmap? {
        val payload = handoutId?.let { handouts.handout(it)?.payload } ?: return null
        val bytes = mediaBlobs.read(payload.sha256) ?: return null
        return runCatching { org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()
    }

    fun nameOf(id: Uuid): String? = reusable.content(id)?.displayName

    private fun ReusableContentItem.revisionNow() =
        reusable.content(identity.id, includeDeleted = true)?.identity?.revision ?: identity.revision
}

/**
 * «Preparar → Dungeons»: the list of the campaign's dungeons on the left, the plan in the centre
 * and the chosen zone on the right. Zones are dragged to place them; dragging from a zone's ● onto
 * another zone makes a passage. Playing it (turns, marks, the group) stays in the Mesa de Dungeon.
 */
@Composable
internal fun DesktopDungeonEditorScreen(
    controller: DesktopDungeonEditorController,
    activeCampaign: Campaign?,
    onOpenZone: (Uuid) -> Unit,
    onQaEvent: (String) -> Unit,
) {
    val campaign = activeCampaign ?: run {
        Text("Primero abre una campaña: los dungeons son de la campaña abierta.")
        return
    }
    var version by remember { mutableStateOf(0) }
    val all = remember(campaign.id, version) { controller.dungeons(campaign.id) }
    var selectedId by remember(campaign.id) { mutableStateOf(all.firstOrNull()?.item?.identity?.id) }
    var creating by remember { mutableStateOf(false) }
    var help by remember { mutableStateOf(false) }
    val selected = all.firstOrNull { it.item.identity.id == selectedId } ?: all.firstOrNull()

    if (creating) {
        NameDialog(
            title = "Nuevo dungeon",
            initial = "",
            confirm = "Crear",
            onDismiss = { creating = false },
            onConfirm = { name ->
                val created = controller.create(campaign.id, name)
                selectedId = created.item.identity.id
                creating = false
                version++
                onQaEvent("Dungeon creado: ${created.item.displayName}")
            },
        )
    }

    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.width(190.dp).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Tus dungeons", style = MaterialTheme.typography.caption)
            all.forEach { dungeon ->
                val isSelected = dungeon.item.identity.id == selected?.item?.identity?.id
                Surface(
                    color = if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.16f) else MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth().handCursor().clickable { selectedId = dungeon.item.identity.id },
                ) {
                    Text(
                        dungeon.item.displayName,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    )
                }
            }
            TextButton(onClick = { creating = true }, modifier = Modifier.handCursor()) { Text("＋ Nuevo dungeon") }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { help = !help }, modifier = Modifier.handCursor()) { Text("? ¿Cómo se usa?") }
        }
        Divider(Modifier.fillMaxHeight().width(1.dp))
        Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (help) {
                Surface(color = MaterialTheme.colors.secondary.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp)) {
                    Text(
                        "1) «＋ Nuevo dungeon» lo crea. 2) «＋ Zona» agrega una caja: arrástrala para ubicarla. " +
                            "3) Para unir dos zonas, arrastra desde el ● de una hasta la otra. " +
                            "4) Elige una zona para ver sus pasajes, marcar uno como secreto o abrir su ficha completa.",
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(10.dp),
                    )
                }
            }
            if (selected == null) {
                Column(Modifier.padding(top = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Aún no hay dungeons en esta campaña.", style = MaterialTheme.typography.h6)
                    Text("Crea uno y agrégale zonas en el plano.")
                    Button(onClick = { creating = true }) { Text("＋ Nuevo dungeon") }
                }
            } else {
                DungeonPlanEditor(
                    controller = controller,
                    campaignId = campaign.id,
                    dungeon = selected,
                    onChanged = { version++ },
                    onOpenZone = onOpenZone,
                )
            }
        }
    }
}

@Composable
private fun DungeonPlanEditor(
    controller: DesktopDungeonEditorController,
    campaignId: Uuid,
    dungeon: DungeonContent,
    onChanged: () -> Unit,
    onOpenZone: (Uuid) -> Unit,
) {
    var planVersion by remember { mutableStateOf(0) }
    val members = remember(dungeon.item.identity.id, dungeon.item.identity.revision, planVersion) { controller.zones(campaignId, dungeon) }
    var selectedZoneId by remember(dungeon.item.identity.id) { mutableStateOf<Uuid?>(null) }
    var renamingDungeon by remember { mutableStateOf(false) }
    var choosingBackground by remember { mutableStateOf(false) }
    val background = remember(dungeon.payload.backgroundHandoutId) { controller.image(dungeon.payload.backgroundHandoutId) }

    // Positions while dragging, so the plan follows the mouse before the drop is saved.
    var dragPositions by remember(dungeon.item.identity.id) { mutableStateOf(mapOf<Uuid, Pair<Double, Double>>()) }
    var linkFrom by remember { mutableStateOf<Uuid?>(null) }
    var linkPointer by remember { mutableStateOf<Offset?>(null) }

    fun refresh() {
        planVersion++
    }

    val positions: Map<Uuid, Pair<Double, Double>> = members.mapIndexed { index, member ->
        val id = member.zone.identity.id
        id to (dragPositions[id] ?: Pair(
            member.links.diagramX ?: (0.1 + (index % 4) * 0.22),
            member.links.diagramY ?: (0.12 + (index / 4) * 0.22).coerceAtMost(0.9),
        ))
    }.toMap()
    val ids = members.map { it.zone.identity.id }.toSet()
    // Every passage once: (a, b, secret).
    val passages = members.flatMap { member ->
        member.links.passages.filter { it.toZoneId in ids }.map { Triple(member.zone.identity.id, it.toZoneId, it.secret) }
    }.distinctBy { (a, b, _) -> if (a.toString() < b.toString()) a to b else b to a }

    if (renamingDungeon) {
        NameDialog(
            title = "Nombre del dungeon",
            initial = dungeon.item.displayName,
            confirm = "Guardar",
            onDismiss = { renamingDungeon = false },
            onConfirm = { name ->
                controller.save(dungeon, name, dungeon.payload)
                renamingDungeon = false
                onChanged()
            },
        )
    }
    if (choosingBackground) {
        val maps = controller.mapHandouts(campaignId)
        AlertDialog(
            onDismissRequest = { choosingBackground = false },
            title = { Text("Plano de fondo") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (maps.isEmpty()) Text("No hay imágenes de tipo Mapa en «Handouts y mapas».")
                    maps.forEach { map ->
                        TextButton(onClick = {
                            controller.save(dungeon, dungeon.item.displayName, dungeon.payload.copy(backgroundHandoutId = map.identity.id))
                            choosingBackground = false
                            onChanged()
                        }) { Text(map.displayName) }
                    }
                    if (dungeon.payload.backgroundHandoutId != null) {
                        TextButton(onClick = {
                            controller.save(dungeon, dungeon.item.displayName, dungeon.payload.copy(backgroundHandoutId = null))
                            choosingBackground = false
                            onChanged()
                        }) { Text("Quitar el plano de fondo") }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { choosingBackground = false }) { Text("Cerrar") } },
        )
    }

    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(dungeon.item.displayName, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                TextButton(onClick = { renamingDungeon = true }, modifier = Modifier.handCursor()) { Text("Cambiar nombre") }
                Spacer(Modifier.weight(1f))
                Button(onClick = {
                    val spot = members.size
                    val id = controller.addZone(campaignId, dungeon, members.size, 0.1 + (spot % 4) * 0.22, (0.12 + (spot / 4) * 0.22).coerceAtMost(0.9))
                    selectedZoneId = id
                    refresh()
                }, modifier = Modifier.handCursor().testTag("add-zone")) { Text("＋ Zona") }
                OutlinedButton(onClick = { choosingBackground = true }, modifier = Modifier.handCursor()) { Text("Plano de fondo") }
            }
            BoxWithConstraints(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                    .testTag("dungeon-plan"),
            ) {
                val density = LocalDensity.current
                val widthPx = with(density) { maxWidth.toPx() }
                val heightPx = with(density) { maxHeight.toPx() }
                val zoneW = with(density) { ZONE_WIDTH.toPx() }
                val zoneH = with(density) { ZONE_HEIGHT.toPx() }
                fun topLeft(id: Uuid): Offset {
                    val (x, y) = positions.getValue(id)
                    return Offset((x * (widthPx - zoneW)).toFloat(), (y * (heightPx - zoneH)).toFloat())
                }
                fun center(id: Uuid): Offset = topLeft(id) + Offset(zoneW / 2, zoneH / 2)
                fun zoneAt(point: Offset): Uuid? = ids.firstOrNull { id ->
                    val tl = topLeft(id)
                    point.x in tl.x..(tl.x + zoneW) && point.y in tl.y..(tl.y + zoneH)
                }
                val lineColor = MaterialTheme.colors.onSurface.copy(alpha = 0.55f)
                val accent = MaterialTheme.colors.primary

                if (background != null) {
                    Image(
                        bitmap = background,
                        contentDescription = "Plano de fondo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().alpha(0.35f),
                    )
                }
                Canvas(Modifier.fillMaxSize()) {
                    passages.forEach { (a, b, secret) ->
                        drawLine(
                            color = lineColor,
                            start = center(a),
                            end = center(b),
                            strokeWidth = 3f,
                            pathEffect = if (secret) PathEffect.dashPathEffect(floatArrayOf(12f, 9f)) else null,
                        )
                    }
                    val from = linkFrom
                    val pointer = linkPointer
                    if (from != null && pointer != null) {
                        drawLine(color = accent, start = center(from), end = pointer, strokeWidth = 3f)
                    }
                }
                if (members.isEmpty()) {
                    Text(
                        "Este dungeon aún no tiene zonas. Usa «＋ Zona».",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.body2,
                    )
                }
                members.forEach { member ->
                    val id = member.zone.identity.id
                    val tl = topLeft(id)
                    val isSelected = id == selectedZoneId
                    Box(
                        Modifier
                            .offset { IntOffset(tl.x.roundToInt(), tl.y.roundToInt()) }
                            .size(ZONE_WIDTH, ZONE_HEIGHT)
                            .background(MaterialTheme.colors.surface, RoundedCornerShape(8.dp))
                            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) accent else lineColor, RoundedCornerShape(8.dp))
                            .handCursor()
                            .clickable { selectedZoneId = id }
                            .pointerInput(id, widthPx, heightPx, positions[id]) {
                                detectDragGestures(
                                    onDragStart = { selectedZoneId = id },
                                    onDragEnd = {
                                        dragPositions[id]?.let { (x, y) -> controller.move(id, x, y) }
                                        dragPositions = dragPositions - id
                                        refresh()
                                    },
                                    onDragCancel = { dragPositions = dragPositions - id },
                                ) { change, amount ->
                                    change.consume()
                                    val (x, y) = dragPositions[id] ?: positions.getValue(id)
                                    val nx = (x + amount.x / (widthPx - zoneW)).coerceIn(0.0, 1.0)
                                    val ny = (y + amount.y / (heightPx - zoneH)).coerceIn(0.0, 1.0)
                                    dragPositions = dragPositions + (id to (nx to ny))
                                }
                            }
                            .testTag("zone-${member.zone.displayName}"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            member.zone.displayName,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(horizontal = 18.dp),
                        )
                        // The ● to drag a passage from this zone to another.
                        Box(
                            Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 4.dp)
                                .size(14.dp)
                                .background(accent, CircleShape)
                                .handCursor()
                                .pointerInput(id, widthPx, heightPx, positions[id]) {
                                    detectDragGestures(
                                        onDragStart = { start ->
                                            linkFrom = id
                                            linkPointer = topLeft(id) + Offset(zoneW - 18f, zoneH / 2) + start
                                        },
                                        onDragEnd = {
                                            val target = linkPointer?.let(::zoneAt)
                                            if (target != null && target != id) {
                                                controller.connect(id, target)
                                                refresh()
                                            }
                                            linkFrom = null
                                            linkPointer = null
                                        },
                                        onDragCancel = { linkFrom = null; linkPointer = null },
                                    ) { change, amount ->
                                        change.consume()
                                        linkPointer = linkPointer?.plus(amount)
                                    }
                                }
                                .testTag("link-${member.zone.displayName}"),
                        )
                    }
                }
            }
        }
        val zone = members.firstOrNull { it.zone.identity.id == selectedZoneId }
        Column(
            Modifier.width(250.dp).fillMaxHeight().verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (zone == null) {
                Text("Elige una zona en el plano para ver sus pasajes y su encuentro.", style = MaterialTheme.typography.body2)
            } else {
                ZonePanel(
                    controller = controller,
                    campaignId = campaignId,
                    zone = zone,
                    passages = passages.filter { (a, b, _) -> a == zone.zone.identity.id || b == zone.zone.identity.id },
                    onChanged = ::refresh,
                    onDeleted = { selectedZoneId = null; refresh() },
                    onOpenZone = onOpenZone,
                )
            }
        }
    }
}

@Composable
private fun ZonePanel(
    controller: DesktopDungeonEditorController,
    campaignId: Uuid,
    zone: DungeonPlanZone,
    passages: List<Triple<Uuid, Uuid, Boolean>>,
    onChanged: () -> Unit,
    onDeleted: () -> Unit,
    onOpenZone: (Uuid) -> Unit,
) {
    val id = zone.zone.identity.id
    var name by remember(id, zone.zone.displayName) { mutableStateOf(zone.zone.displayName) }
    var confirmDelete by remember(id) { mutableStateOf(false) }
    val encounters = remember(campaignId) { controller.encounters(campaignId) }

    Text("Zona elegida", style = MaterialTheme.typography.caption)
    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        singleLine = true,
        label = { Text("Nombre") },
        modifier = Modifier.fillMaxWidth(),
    )
    if (name.trim() != zone.zone.displayName && name.isNotBlank()) {
        Button(onClick = { if (controller.renameZone(zone.zone, name.trim())) onChanged() }) { Text("Guardar nombre") }
    }
    Text("Pasajes", style = MaterialTheme.typography.caption)
    if (passages.isEmpty()) Text("Sin pasajes. Arrastra desde el ● hasta otra zona.", style = MaterialTheme.typography.body2)
    passages.forEach { (a, b, secret) ->
        val other = if (a == id) b else a
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("→ ${controller.nameOf(other) ?: "Zona"}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.body2)
            Checkbox(checked = secret, onCheckedChange = { controller.setSecret(a, b, it); onChanged() })
            Text("secreto", style = MaterialTheme.typography.caption)
            TextButton(onClick = { controller.disconnect(a, b); onChanged() }) { Text("✕") }
        }
    }
    Text("Encuentro", style = MaterialTheme.typography.caption)
    val current = zone.links.encounterIds.firstOrNull()
    if (encounters.isEmpty()) {
        Text("Aún no hay encuentros: créalos en «Preparar → Encuentros».", style = MaterialTheme.typography.body2)
    } else {
        Column {
            encounters.forEach { encounter ->
                val chosen = encounter.identity.id == current
                TextButton(onClick = { controller.setEncounter(id, if (chosen) null else encounter.identity.id); onChanged() }) {
                    Text((if (chosen) "● " else "○ ") + encounter.displayName)
                }
            }
        }
    }
    Divider()
    Button(onClick = { onOpenZone(id) }, modifier = Modifier.fillMaxWidth()) { Text("Abrir ficha completa") }
    if (!confirmDelete) {
        TextButton(onClick = { confirmDelete = true }) { Text("Quitar zona") }
    } else {
        Text("¿Quitar «${zone.zone.displayName}» y sus pasajes?", style = MaterialTheme.typography.body2)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                controller.deleteZone(zone.zone, passages.map { (a, b, _) -> if (a == id) b else a })
                onDeleted()
            }) { Text("Quitar") }
            TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") }
        }
    }
}

@Composable
private fun NameDialog(
    title: String,
    initial: String,
    confirm: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, singleLine = true, label = { Text("Nombre") })
        },
        confirmButton = {
            Button(enabled = name.isNotBlank(), onClick = { onConfirm(name.trim()) }) { Text(confirm) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}
