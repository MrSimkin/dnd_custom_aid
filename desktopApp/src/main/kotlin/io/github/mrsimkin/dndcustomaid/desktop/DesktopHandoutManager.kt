package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContent
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutKind
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutPayload
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutVisibility
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.uuid.Uuid

/** Base para DM, package 4: the DM's handouts, maps and NPC portraits in the App de PC. */
internal class DesktopHandoutManagerController(
    private val database: AppDatabase,
    val blobs: MediaBlobStore,
) {
    private val campaignRepository = CampaignRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val handouts = HandoutContentRepository(database)

    fun personalOwnerAccountId(): Uuid? = campaignRepository.listCampaigns()
        .flatMap { spine.memberships(it.id) }
        .filter { it.role == CampaignRole.DM && it.status == CampaignMembershipStatus.ACTIVE }
        .map { it.accountId }
        .distinct()
        .singleOrNull()

    fun campaignHandouts(campaignId: Uuid): List<HandoutContent> = handouts.listCampaign(campaignId)

    fun personalHandouts(ownerAccountId: Uuid): List<HandoutContent> = handouts.listPersonal(ownerAccountId)

    fun handout(id: Uuid): HandoutContent? = handouts.handout(id)

    /** Imports an image file: prepares it, stores it by checksum and creates its handout. */
    fun import(file: File, campaignId: Uuid?, ownerAccountId: Uuid?): HandoutContent {
        val prepared = prepareHandoutImage(file.readBytes(), file.name)
        val sha = blobs.write(prepared.bytes)
        val payload = HandoutPayload(
            mediaType = prepared.mediaType,
            sizeBytes = prepared.bytes.size.toLong(),
            sha256 = sha,
            width = prepared.width,
            height = prepared.height,
        )
        val name = file.nameWithoutExtension.ifBlank { "Handout" }
        return when {
            campaignId != null -> handouts.createCampaign(campaignId, name, payload, nowEpochSeconds())
            ownerAccountId != null -> handouts.createPersonal(ownerAccountId, name, payload, nowEpochSeconds())
            else -> throw HandoutImageException("Elige una campaña activa o entra con tu cuenta de DM.")
        }
    }

    fun update(current: HandoutContent, name: String, payload: HandoutPayload): RevisionDecision =
        handouts.update(current.item.identity.id, current.item.identity.revision, name, payload, nowEpochSeconds())

    fun delete(current: HandoutContent): RevisionDecision =
        handouts.tombstone(current.item.identity.id, current.item.identity.revision, nowEpochSeconds())

    fun preview(sha256: String): ImageBitmap? = blobs.read(sha256)?.let { bytes ->
        runCatching { org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap() }.getOrNull()
    }

    private fun nowEpochSeconds(): Long = System.currentTimeMillis() / 1000
}

internal fun HandoutKind.spanishLabel(): String = when (this) {
    HandoutKind.HANDOUT -> "Handout"
    HandoutKind.MAP -> "Mapa"
    HandoutKind.NPC_PORTRAIT -> "Retrato de PNJ"
}

internal fun HandoutVisibility.spanishLabel(): String = when (this) {
    HandoutVisibility.DM_ONLY -> "Solo DM"
    HandoutVisibility.PLAYER_SAFE -> "Se puede mostrar a los jugadores"
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
internal fun DesktopHandoutManagerScreen(
    controller: DesktopHandoutManagerController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var refresh by remember { mutableStateOf(0) }
    var personal by remember { mutableStateOf(activeCampaign == null) }
    var selectedId by remember { mutableStateOf<Uuid?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }
    // QA 0.7.0-preqa.2 (H1): search, filter by type and group by folder, with a thumbnail of each image.
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("TODOS") }

    val ownerId = remember(refresh) { controller.personalOwnerAccountId() }
    val list = remember(refresh, personal, activeCampaign?.id, ownerId) {
        when {
            personal -> ownerId?.let(controller::personalHandouts).orEmpty()
            activeCampaign != null -> controller.campaignHandouts(activeCampaign.id)
            else -> emptyList()
        }
    }
    val selected = remember(refresh, selectedId) { selectedId?.let(controller::handout) }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(
            modifier = Modifier.width(360.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            WorkbenchHeading(
                title = "Handouts y mapas",
                subtitle = "Imágenes para mostrar a los jugadores, mapas con marcas y retratos de PNJ. Viajan a la tablet al sincronizar.",
                help = "1) «Importar imagen…» elige un archivo; el explorador recuerda tus carpetas. 2) Ponle tipo (Handout, Mapa o Retrato), carpeta y etiquetas: " +
                    "así la lista se ordena por carpeta y la búsqueda la encuentra. 3) Visibilidad: «Solo DM» o «Se puede mostrar a los jugadores», y «Enviar a los jugadores». " +
                    "4) En un Mapa, un clic pone una marca y puedes arrastrarla para moverla.",
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = !personal, enabled = activeCampaign != null, onClick = { personal = false })
                Text(activeCampaign?.let { "Campaña: ${it.name}" } ?: "Sin campaña activa")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = personal, onClick = { personal = true })
                Text("Mi biblioteca personal")
            }
            Button(onClick = {
                val file = chooseImageFile(activeCampaign?.id?.toString()) ?: return@Button
                message = try {
                    val created = controller.import(
                        file = file,
                        campaignId = if (personal) null else activeCampaign?.id,
                        ownerAccountId = if (personal) ownerId else null,
                    )
                    selectedId = created.item.identity.id
                    refresh += 1
                    onQaEvent("Handout importado: ${created.item.displayName} (${created.payload.sizeBytes} bytes)")
                    "Imagen importada: ${created.item.displayName}."
                } catch (error: HandoutImageException) {
                    error.message
                } catch (error: Exception) {
                    "No se pudo importar la imagen: ${error.message ?: error::class.simpleName}"
                }
            }) { Text("Importar imagen…") }
            message?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
            HorizontalDivider()
            if (list.isEmpty()) {
                Text("Todavía no hay imágenes aquí.", style = MaterialTheme.typography.bodyMedium)
            }
            if (list.isNotEmpty()) {
                OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Buscar por nombre, carpeta, etiqueta o texto") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                androidx.compose.foundation.layout.FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("TODOS" to "Todos", "MAP" to "Mapas", "HANDOUT" to "Handouts", "NPC_PORTRAIT" to "Retratos", "SENT" to "📤 Enviados", "DM" to "Solo DM").forEach { (key, label) ->
                        if (filter == key) Button(onClick = { filter = key }) { Text(label) } else TextButton(onClick = { filter = key }) { Text(label) }
                    }
                }
            }
            val needle = query.trim().lowercase()
            val shown = list.filter { handout ->
                val p = handout.payload
                val matchesFilter = when (filter) {
                    "TODOS" -> true
                    "SENT" -> p.sharedWithPlayers
                    "DM" -> p.visibility == HandoutVisibility.DM_ONLY
                    else -> p.kind.name == filter
                }
                matchesFilter && (needle.isEmpty() || listOf(handout.item.displayName, p.folder, p.caption, p.notes, p.tags.joinToString(" ")).any { it.lowercase().contains(needle) })
            }
            if (list.isNotEmpty() && shown.isEmpty()) Text("Nada coincide con la búsqueda.", style = MaterialTheme.typography.bodyMedium)
            shown.groupBy { it.payload.folder.trim() }.toSortedMap(compareBy<String> { it.isEmpty() }.thenBy { it.lowercase() }).forEach { (folder, inFolder) ->
                Text("📁 ${folder.ifEmpty { "Sin carpeta" }} · ${inFolder.size}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                inFolder.sortedBy { it.item.displayName.lowercase() }.forEach { handout ->
                    val thumb = remember(handout.payload.sha256) { controller.preview(handout.payload.sha256) }
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { selectedId = handout.item.identity.id },
                        elevation = androidx.compose.material3.CardDefaults.cardElevation(if (handout.item.identity.id == selectedId) 6.dp else 1.dp),
                    ) {
                        Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (thumb != null) {
                                Image(bitmap = thumb, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.width(64.dp).heightIn(max = 64.dp))
                            }
                            Column {
                                Text(handout.item.displayName, fontWeight = FontWeight.Bold)
                                Text(
                                    listOfNotNull(
                                        handout.payload.kind.spanishLabel(),
                                        handout.payload.visibility.spanishLabel(),
                                        "📤 Enviado".takeIf { handout.payload.sharedWithPlayers },
                                    ).joinToString(" · "),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                                handout.payload.caption.takeIf { it.isNotBlank() }?.let { Text(it, style = MaterialTheme.typography.bodySmall, maxLines = 2) }
                                if (handout.payload.tags.isNotEmpty()) Text(handout.payload.tags.joinToString(" ") { "#$it" }, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
            if (selected == null) {
                Text("Elige una imagen de la lista o importa una nueva.", modifier = Modifier.padding(16.dp))
            } else {
                HandoutEditor(
                    controller = controller,
                    handout = selected,
                    onSaved = { refresh += 1; message = "Cambios guardados." },
                    onDelete = { confirmDelete = true },
                )
            }
        }
    }

    if (confirmDelete && selected != null) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("¿Borrar «${selected.item.displayName}»?") },
            text = { Text("Se borra también en la tablet al sincronizar. La imagen queda guardada en este computador.") },
            confirmButton = {
                Button(onClick = {
                    controller.delete(selected)
                    onQaEvent("Handout borrado: ${selected.item.displayName}")
                    selectedId = null
                    confirmDelete = false
                    refresh += 1
                }) { Text("Borrar") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun HandoutEditor(
    controller: DesktopHandoutManagerController,
    handout: HandoutContent,
    onSaved: () -> Unit,
    onDelete: () -> Unit,
) {
    val key = handout.item.identity.id to handout.item.identity.revision
    var name by remember(key) { mutableStateOf(handout.item.displayName) }
    var kind by remember(key) { mutableStateOf(handout.payload.kind) }
    var visibility by remember(key) { mutableStateOf(handout.payload.visibility) }
    var shared by remember(key) { mutableStateOf(handout.payload.sharedWithPlayers) }
    var caption by remember(key) { mutableStateOf(handout.payload.caption) }
    var notes by remember(key) { mutableStateOf(handout.payload.notes) }
    var folder by remember(key) { mutableStateOf(handout.payload.folder) }
    var tags by remember(key) { mutableStateOf(handout.payload.tags.joinToString(", ")) }
    var error by remember(key) { mutableStateOf<String?>(null) }
    val bitmap = remember(handout.payload.sha256) { controller.preview(handout.payload.sha256) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
            )
        } else {
            Text("La imagen no está en este computador todavía.", style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            listOfNotNull(
                handout.payload.mediaType,
                if (handout.payload.width != null && handout.payload.height != null) "${handout.payload.width}×${handout.payload.height}" else null,
                "${handout.payload.sizeBytes / 1024} KB",
            ).joinToString(" · "),
            style = MaterialTheme.typography.bodySmall,
        )
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Text("Tipo", fontWeight = FontWeight.Bold)
        HandoutKind.entries.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = kind == option, onClick = { kind = option })
                Text(option.spanishLabel())
            }
        }
        Text("Quién puede verlo", fontWeight = FontWeight.Bold)
        HandoutVisibility.entries.forEach { option ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = visibility == option, onClick = { visibility = option })
                Text(option.spanishLabel())
            }
        }
        // Redesign A7 (owner observation G2): only a PLAYER_SAFE image can be sent; their App shows it after syncing.
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Checkbox(
                checked = shared && visibility == HandoutVisibility.PLAYER_SAFE,
                enabled = visibility == HandoutVisibility.PLAYER_SAFE,
                onCheckedChange = { shared = it },
            )
            Text(
                if (visibility == HandoutVisibility.PLAYER_SAFE) "Enviar a los jugadores (lo ven en su App al sincronizar)"
                else "Para enviarla a los jugadores, primero márcala «Se puede mostrar a los jugadores».",
            )
        }
        OutlinedTextField(value = caption, onValueChange = { caption = it }, label = { Text("Texto al pie (opcional)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas del DM") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = folder, onValueChange = { folder = it }, label = { Text("Carpeta (por ejemplo «Ciudad» o «La Torre»)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = tags, onValueChange = { tags = it }, label = { Text("Etiquetas, separadas por coma") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (name.isBlank()) {
                    error = "El nombre no puede quedar vacío."
                    return@Button
                }
                val decision = controller.update(
                    handout,
                    name,
                    handout.payload.copy(
                        kind = kind,
                        visibility = visibility,
                        caption = caption.trim(),
                        notes = notes.trim(),
                        sharedWithPlayers = shared && visibility == HandoutVisibility.PLAYER_SAFE,
                        folder = folder.trim(),
                        tags = tags.split(',').map(String::trim).filter(String::isNotEmpty).distinct(),
                    ),
                )
                if (decision is RevisionDecision.Accepted) onSaved() else error = "Otra ventana cambió esta imagen. Vuelve a abrirla."
            }) { Text("Guardar") }
            TextButton(onClick = onDelete) { Text("Borrar") }
        }
        DesktopContentLinksSection(handout.item, handoutKind = kind)
    }
}

private fun chooseImageFile(campaignKey: String? = null): File? = chooseFileWithExplorer(
    title = "Agregar imagen",
    purpose = "handouts",
    campaignKey = campaignKey,
    extensions = listOf("jpg", "jpeg", "png", "webp"),
)
