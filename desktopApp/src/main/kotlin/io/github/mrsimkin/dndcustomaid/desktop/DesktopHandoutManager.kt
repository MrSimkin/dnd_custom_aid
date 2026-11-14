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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
                help = "1) «Agregar imagen» elige un archivo; el explorador recuerda tus carpetas. 2) Elige el tipo: Handout, Mapa o Retrato. " +
                    "3) Visibilidad: «Solo DM» o «Se puede mostrar a los jugadores». 4) En un Mapa, un clic pone una marca y puedes arrastrarla para moverla.",
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
            message?.let { Text(it, style = MaterialTheme.typography.body2) }
            Divider()
            if (list.isEmpty()) {
                Text("Todavía no hay imágenes aquí.", style = MaterialTheme.typography.body2)
            }
            list.forEach { handout ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedId = handout.item.identity.id },
                    elevation = if (handout.item.identity.id == selectedId) 6.dp else 1.dp,
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(handout.item.displayName, fontWeight = FontWeight.Bold)
                        Text(
                            "${handout.payload.kind.spanishLabel()} · ${handout.payload.visibility.spanishLabel()}",
                            style = MaterialTheme.typography.caption,
                        )
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
            Text("La imagen no está en este computador todavía.", style = MaterialTheme.typography.body2)
        }
        Text(
            listOfNotNull(
                handout.payload.mediaType,
                if (handout.payload.width != null && handout.payload.height != null) "${handout.payload.width}×${handout.payload.height}" else null,
                "${handout.payload.sizeBytes / 1024} KB",
            ).joinToString(" · "),
            style = MaterialTheme.typography.caption,
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
            androidx.compose.material.Checkbox(
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
        error?.let { Text(it, color = MaterialTheme.colors.error) }
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
