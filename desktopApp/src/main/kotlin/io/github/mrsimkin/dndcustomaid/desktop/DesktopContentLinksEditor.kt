package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinks
import io.github.mrsimkin.dndcustomaid.shared.content.ContentLinksRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutKind
import io.github.mrsimkin.dndcustomaid.shared.content.MapPin
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PlaceKind
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentItem
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ShopItem
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope
import kotlin.math.roundToInt
import kotlin.uuid.Uuid

/*
 * Base para DM, package 6 in the App de PC (P35, P36): links chosen from lists instead of typed.
 *
 * One section, added to the NPC, place/shop, zone, scene and map editors. Every change is saved at
 * once (it is its own small record) and travels to the tablet with the next sync. The free texts of
 * each editor stay as they are; the Mesa de Escena searches both.
 */

internal class DesktopContentLinksController(
    private val database: AppDatabase,
    private val mediaBlobs: MediaBlobStore,
) {
    private val reusable = ReusableContentRepository(database)
    private val links = ContentLinksRepository(database)

    fun links(id: Uuid): ContentLinks = links.links(id)

    fun save(id: Uuid, value: ContentLinks) = links.save(id, value)

    /** Content of one family in the same library (the campaign, or the personal library). */
    fun candidates(scope: ContentScope, family: ReusableContentFamily): List<ReusableContentItem> = when (scope) {
        is ContentScope.Campaign -> reusable.listCampaign(scope.campaignId, family)
        is ContentScope.Personal -> reusable.listPersonal(scope.ownerAccountId, family)
        else -> emptyList()
    }.sortedBy { it.displayName.lowercase() }

    fun handouts(scope: ContentScope, kind: HandoutKind): List<ReusableContentItem> {
        val repo = HandoutContentRepository(database)
        val all = when (scope) {
            is ContentScope.Campaign -> repo.listCampaign(scope.campaignId)
            is ContentScope.Personal -> repo.listPersonal(scope.ownerAccountId)
            else -> emptyList()
        }
        return all.filter { it.payload.kind == kind }.map { it.item }.sortedBy { it.displayName.lowercase() }
    }

    /** Every group or faction already used in this library, to choose instead of retyping. */
    fun knownGroups(scope: ContentScope): List<String> =
        candidates(scope, ReusableContentFamily.NPC).flatMap { links.links(it.identity.id).groups }
            .distinctBy { it.lowercase() }.sortedBy { it.lowercase() }

    fun adventures(scope: ContentScope): List<String> =
        candidates(scope, ReusableContentFamily.SCENE).map { links.links(it.identity.id).adventure }
            .filter { it.isNotBlank() }.distinct().sorted()

    fun isShop(id: Uuid): Boolean = PlaceContentRepository(database).place(id)?.payload?.kind == PlaceKind.SHOP

    fun mapImage(handoutId: Uuid): ImageBitmap? {
        val sha = HandoutContentRepository(database).handout(handoutId)?.payload?.sha256 ?: return null
        val bytes = mediaBlobs.read(sha) ?: return null
        return runCatching { org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()
    }
}

internal val LocalDesktopContentLinks = staticCompositionLocalOf<DesktopContentLinksController?> { null }

@Composable
internal fun ProvideDesktopContentLinks(controller: DesktopContentLinksController, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDesktopContentLinks provides controller, content = content)
}

/** The «Vínculos para la Mesa de Escena» section of an editor. */
@Composable
internal fun DesktopContentLinksSection(item: ReusableContentItem, handoutKind: HandoutKind? = null) {
    val controller = LocalDesktopContentLinks.current ?: return
    val id = item.identity.id
    val scope = item.identity.scope
    var links by remember(id) { mutableStateOf(controller.links(id)) }
    var saved by remember(id) { mutableStateOf(false) }

    fun update(next: ContentLinks) {
        links = next
        controller.save(id, next)
        saved = true
    }

    fun options(family: ReusableContentFamily) = controller.candidates(scope, family).filter { it.identity.id != id }.map { it.identity.id to it.displayName }

    Card(elevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Vínculos para la Mesa de Escena", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (saved) Text("Guardado ✓", style = MaterialTheme.typography.caption)
            }
            Text("Se eligen de una lista y se guardan al momento. Los textos libres siguen sirviendo para buscar.", style = MaterialTheme.typography.caption)
            when (item.family) {
                ReusableContentFamily.NPC -> {
                    LinkPicker("Lugares donde está", options(ReusableContentFamily.PLACE), links.placeIds) { update(links.copy(placeIds = it)) }
                    LinkPicker("Zonas", options(ReusableContentFamily.ZONE), links.zoneIds) { update(links.copy(zoneIds = it)) }
                    LinkPicker("Escenas", options(ReusableContentFamily.SCENE), links.sceneIds) { update(links.copy(sceneIds = it)) }
                    GroupsPicker(links.groups, controller.knownGroups(scope)) { update(links.copy(groups = it)) }
                    SinglePicker(
                        "Retrato",
                        controller.handouts(scope, HandoutKind.NPC_PORTRAIT).map { it.identity.id to it.displayName },
                        links.portraitHandoutId,
                    ) { update(links.copy(portraitHandoutId = it)) }
                }
                ReusableContentFamily.PLACE -> {
                    SinglePicker("Zona a la que pertenece", options(ReusableContentFamily.ZONE), links.zoneIds.firstOrNull()) {
                        update(links.copy(zoneIds = listOfNotNull(it)))
                    }
                    LinkPicker("Quién atiende / PNJ aquí", options(ReusableContentFamily.NPC), links.npcIds) { update(links.copy(npcIds = it)) }
                    if (controller.isShop(id)) ShopItemsEditor(links.shopItems) { update(links.copy(shopItems = it)) }
                    SinglePicker("Mapa", controller.handouts(scope, HandoutKind.MAP).map { it.identity.id to it.displayName }, links.mapHandoutId) {
                        update(links.copy(mapHandoutId = it))
                    }
                }
                ReusableContentFamily.ZONE -> {
                    LinkPicker("PNJ aquí", options(ReusableContentFamily.NPC), links.npcIds) { update(links.copy(npcIds = it)) }
                    SinglePicker("Mapa", controller.handouts(scope, HandoutKind.MAP).map { it.identity.id to it.displayName }, links.mapHandoutId) {
                        update(links.copy(mapHandoutId = it))
                    }
                }
                ReusableContentFamily.SCENE -> {
                    AdventureFields(links, controller.adventures(scope)) { update(it) }
                    LinkPicker("Escenas siguientes", options(ReusableContentFamily.SCENE), links.sceneIds) { update(links.copy(sceneIds = it)) }
                    LinkPicker("Dónde ocurre: lugares", options(ReusableContentFamily.PLACE), links.placeIds) { update(links.copy(placeIds = it)) }
                    LinkPicker("Dónde ocurre: zonas", options(ReusableContentFamily.ZONE), links.zoneIds) { update(links.copy(zoneIds = it)) }
                    LinkPicker("PNJ aquí", options(ReusableContentFamily.NPC), links.npcIds) { update(links.copy(npcIds = it)) }
                }
                ReusableContentFamily.HANDOUT -> if (handoutKind == HandoutKind.MAP) {
                    val targets = listOf(ReusableContentFamily.ZONE, ReusableContentFamily.PLACE, ReusableContentFamily.NPC, ReusableContentFamily.SCENE)
                        .flatMap { family -> controller.candidates(scope, family).map { it.identity.id to it.displayName } }
                    MapPinsEditor(controller.mapImage(id), links.pins, targets) { update(links.copy(pins = it)) }
                } else {
                    Text("Las marcas en el mapa están disponibles cuando el tipo es «Mapa».", style = MaterialTheme.typography.caption)
                }
                else -> Unit
            }
        }
    }
}

@Composable
private fun LinkPicker(title: String, options: List<Pair<Uuid, String>>, selected: List<Uuid>, onChange: (List<Uuid>) -> Unit) {
    val names = options.toMap()
    var open by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, fontWeight = FontWeight.Medium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            selected.forEach { id ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(MaterialTheme.colors.primary.copy(alpha = 0.15f), MaterialTheme.shapes.small).padding(start = 8.dp),
                ) {
                    Text(names[id] ?: "(ya no existe)")
                    TextButton(onClick = { onChange(selected - id) }) { Text("✕") }
                }
            }
            Box {
                TextButton(enabled = options.any { it.first !in selected }, onClick = { open = true; filter = "" }) { Text("＋ Agregar") }
                DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                    if (options.size > 8) {
                        OutlinedTextField(value = filter, onValueChange = { filter = it }, label = { Text("Filtrar") }, singleLine = true, modifier = Modifier.padding(horizontal = 8.dp).width(260.dp))
                    }
                    options.filter { it.first !in selected && it.second.contains(filter.trim(), ignoreCase = true) }.take(40).forEach { (optionId, name) ->
                        DropdownMenuItem(onClick = { open = false; onChange(selected + optionId) }) { Text(name) }
                    }
                }
            }
        }
        if (options.isEmpty()) Text("Todavía no hay fichas de este tipo en esta biblioteca.", style = MaterialTheme.typography.caption)
    }
}

@Composable
private fun SinglePicker(title: String, options: List<Pair<Uuid, String>>, selected: Uuid?, onChange: (Uuid?) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontWeight = FontWeight.Medium)
        Box {
            TextButton(onClick = { open = true }) { Text(selected?.let { id -> options.firstOrNull { it.first == id }?.second ?: "(ya no existe)" } ?: "Elegir…") }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                DropdownMenuItem(onClick = { open = false; onChange(null) }) { Text("Ninguno") }
                options.forEach { (id, name) -> DropdownMenuItem(onClick = { open = false; onChange(id) }) { Text(name) } }
            }
        }
        if (options.isEmpty()) Text("(no hay opciones todavía)", style = MaterialTheme.typography.caption)
    }
}

@Composable
private fun GroupsPicker(groups: List<String>, known: List<String>, onChange: (List<String>) -> Unit) {
    var typed by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Grupo o facción", fontWeight = FontWeight.Medium)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            groups.forEach { group ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.background(MaterialTheme.colors.primary.copy(alpha = 0.15f), MaterialTheme.shapes.small).padding(start = 8.dp),
                ) {
                    Text(group)
                    TextButton(onClick = { onChange(groups - group) }) { Text("✕") }
                }
            }
        }
        val suggestions = known.filter { k -> groups.none { it.equals(k, ignoreCase = true) } }
        if (suggestions.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                suggestions.forEach { TextButton(onClick = { onChange(groups + it) }) { Text("＋ $it") } }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(value = typed, onValueChange = { typed = it }, label = { Text("Grupo nuevo") }, singleLine = true, modifier = Modifier.width(260.dp))
            TextButton(enabled = typed.isNotBlank(), onClick = { onChange(groups + typed.trim()); typed = "" }) { Text("Agregar") }
        }
    }
}

@Composable
private fun AdventureFields(links: ContentLinks, known: List<String>, onChange: (ContentLinks) -> Unit) {
    var adventure by remember(links.adventure) { mutableStateOf(links.adventure) }
    var order by remember(links.order) { mutableStateOf(links.order?.toString().orEmpty()) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = adventure,
                onValueChange = { adventure = it; onChange(links.copy(adventure = it)) },
                label = { Text("Aventura") },
                singleLine = true,
                modifier = Modifier.width(280.dp),
            )
            OutlinedTextField(
                value = order,
                onValueChange = { raw -> order = raw.filter(Char::isDigit).take(3); onChange(links.copy(order = order.toIntOrNull())) },
                label = { Text("Orden") },
                singleLine = true,
                modifier = Modifier.width(100.dp),
            )
            Checkbox(checked = links.detour, onCheckedChange = { onChange(links.copy(detour = it)) })
            Text("Es un desvío")
        }
        val others = known.filter { it != links.adventure }
        if (others.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                others.forEach { name -> TextButton(onClick = { adventure = name; onChange(links.copy(adventure = name)) }) { Text(name) } }
            }
        }
    }
}

@Composable
private fun ShopItemsEditor(items: List<ShopItem>, onChange: (List<ShopItem>) -> Unit) {
    fun set(index: Int, change: (ShopItem) -> ShopItem) = onChange(items.mapIndexed { i, item -> if (i == index) change(item) else item })
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Qué vende (sin existencias)", fontWeight = FontWeight.Medium)
        items.forEachIndexed { index, item ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(value = item.name, onValueChange = { v -> set(index) { it.copy(name = v) } }, label = { Text("Artículo") }, singleLine = true, modifier = Modifier.width(240.dp))
                OutlinedTextField(value = item.price, onValueChange = { v -> set(index) { it.copy(price = v) } }, label = { Text("Precio") }, singleLine = true, modifier = Modifier.width(110.dp))
                Checkbox(checked = item.highlighted, onCheckedChange = { v -> set(index) { it.copy(highlighted = v) } })
                Text("⭐")
                OutlinedTextField(value = item.note, onValueChange = { v -> set(index) { it.copy(note = v) } }, label = { Text("Nota") }, singleLine = true, modifier = Modifier.weight(1f))
                TextButton(onClick = { onChange(items.filterIndexed { i, _ -> i != index }) }) { Text("Quitar") }
            }
        }
        TextButton(onClick = { onChange(items + ShopItem(name = "Artículo nuevo")) }) { Text("＋ Agregar artículo") }
    }
}

/** Click the image to drop a mark; each mark then points to the sheet it opens on the tablet. */
@Composable
private fun MapPinsEditor(image: ImageBitmap?, pins: List<MapPin>, targets: List<Pair<Uuid, String>>, onChange: (List<MapPin>) -> Unit) {
    fun set(index: Int, change: (MapPin) -> MapPin) = onChange(pins.mapIndexed { i, pin -> if (i == index) change(pin) else pin })
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Marcas en el mapa", fontWeight = FontWeight.Medium)
        if (image == null) {
            Text("La imagen no está en este computador todavía.", style = MaterialTheme.typography.caption)
            return
        }
        Text("Haz clic en el mapa para poner una marca.", style = MaterialTheme.typography.caption)
        BoxWithConstraints(
            modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth().aspectRatio(image.width.toFloat() / image.height.coerceAtLeast(1)),
        ) {
            val w = constraints.maxWidth.toFloat()
            val h = constraints.maxHeight.toFloat()
            Image(
                bitmap = image,
                contentDescription = "Mapa",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize().pointerInput(pins) {
                    detectTapGestures { tap ->
                        val x = (tap.x / size.width).toDouble().coerceIn(0.0, 1.0)
                        val y = (tap.y / size.height).toDouble().coerceIn(0.0, 1.0)
                        onChange(pins + MapPin(x, y, label = "${pins.size + 1}"))
                    }
                },
            )
            pins.forEachIndexed { index, pin ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .offset { androidx.compose.ui.unit.IntOffset((pin.x * w).roundToInt() - 14, (pin.y * h).roundToInt() - 14) }
                        .size(28.dp)
                        .background(Color(0xFFC62828), CircleShape),
                ) { Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
        pins.forEachIndexed { index, pin ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("${index + 1}.", fontWeight = FontWeight.Bold)
                OutlinedTextField(value = pin.label, onValueChange = { v -> set(index) { it.copy(label = v) } }, label = { Text("Etiqueta") }, singleLine = true, modifier = Modifier.width(200.dp))
                SinglePicker("Abre", targets, pin.targetId) { target -> set(index) { it.copy(targetId = target) } }
                TextButton(onClick = { onChange(pins.filterIndexed { i, _ -> i != index }) }) { Text("Quitar") }
            }
        }
    }
}
