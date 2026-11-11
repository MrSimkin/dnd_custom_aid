package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PackageContent
import io.github.mrsimkin.dndcustomaid.shared.content.PackageContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PackagePayload
import io.github.mrsimkin.dndcustomaid.shared.content.PackagePiece
import io.github.mrsimkin.dndcustomaid.shared.content.PackagePieceKind
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignMembershipStatus
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import io.github.mrsimkin.dndcustomaid.shared.spine.IntegratedSpineRepository
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlin.uuid.Uuid

/** Base para DM, package 5 (P19): packages are assembled in the App de PC and travel to the tablet. */
internal class DesktopPackageManagerController(
    database: AppDatabase,
    private val nowEpochSeconds: () -> Long,
) {
    private val campaignRepository = CampaignRepository(database)
    private val spine = IntegratedSpineRepository(database)
    private val packages = PackageContentRepository(database)

    fun personalOwnerAccountId(): Uuid? = campaignRepository.listCampaigns()
        .flatMap { spine.memberships(it.id) }
        .filter { it.role == CampaignRole.DM && it.status == CampaignMembershipStatus.ACTIVE }
        .map { it.accountId }
        .distinct()
        .singleOrNull()

    fun campaignPackages(campaignId: Uuid) = packages.listCampaign(campaignId)

    fun personalPackages(ownerAccountId: Uuid) = packages.listPersonal(ownerAccountId)

    fun pkg(id: Uuid) = packages.pkg(id)

    fun create(campaignId: Uuid?, ownerAccountId: Uuid?, name: String): PackageContent = when {
        campaignId != null -> packages.createCampaign(campaignId, name, PackagePayload(), nowEpochSeconds())
        ownerAccountId != null -> packages.createPersonal(ownerAccountId, name, PackagePayload(), nowEpochSeconds())
        else -> error("Elige una campaña activa o entra con tu cuenta de DM.")
    }

    fun update(current: PackageContent, name: String, payload: PackagePayload): RevisionDecision =
        packages.update(current.item.identity.id, current.item.identity.revision, name, payload, nowEpochSeconds())

    fun delete(current: PackageContent): RevisionDecision =
        packages.tombstone(current.item.identity.id, current.item.identity.revision, nowEpochSeconds())
}

internal fun PackagePieceKind.spanishLabel(): String = when (this) {
    PackagePieceKind.ACTION -> "Acción"
    PackagePieceKind.TRAIT -> "Rasgo"
    PackagePieceKind.SAVE -> "Salvación"
    PackagePieceKind.SPELL -> "Conjuro"
    PackagePieceKind.REACTION -> "Reacción"
}

@Composable
internal fun DesktopPackageManagerScreen(
    controller: DesktopPackageManagerController,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
) {
    var refresh by remember { mutableStateOf(0) }
    var personal by remember { mutableStateOf(activeCampaign == null) }
    var selectedId by remember { mutableStateOf<Uuid?>(null) }
    var newName by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    val ownerId = remember(refresh) { controller.personalOwnerAccountId() }
    val list = remember(refresh, personal, activeCampaign?.id, ownerId) {
        when {
            personal -> ownerId?.let(controller::personalPackages).orEmpty()
            activeCampaign != null -> controller.campaignPackages(activeCampaign.id)
            else -> emptyList()
        }
    }
    val selected = remember(refresh, selectedId) { selectedId?.let(controller::pkg) }

    Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(modifier = Modifier.width(340.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Paquetes", style = MaterialTheme.typography.h5)
            Text(
                "Piezas y ajustes que la Mesa de Combate aplica de un toque a una criatura o a su grupo. El bestiario no cambia.",
                style = MaterialTheme.typography.caption,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = !personal, enabled = activeCampaign != null, onClick = { personal = false })
                Text(activeCampaign?.let { "Campaña: ${it.name}" } ?: "Sin campaña activa")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = personal, onClick = { personal = true })
                Text("Mi biblioteca personal")
            }
            OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Nombre del paquete nuevo") }, modifier = Modifier.fillMaxWidth())
            Button(enabled = newName.isNotBlank(), onClick = {
                message = runCatching {
                    val created = controller.create(if (personal) null else activeCampaign?.id, if (personal) ownerId else null, newName.trim())
                    selectedId = created.item.identity.id
                    newName = ""
                    refresh += 1
                    onQaEvent("Paquete creado: ${created.item.displayName}")
                    null
                }.getOrElse { it.message }
            }) { Text("Crear paquete") }
            message?.let { Text(it, color = MaterialTheme.colors.error) }
            Divider()
            if (list.isEmpty()) Text("Todavía no hay paquetes aquí.", style = MaterialTheme.typography.body2)
            list.forEach { pkg ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedId = pkg.item.identity.id },
                    elevation = if (pkg.item.identity.id == selectedId) 6.dp else 1.dp,
                ) {
                    Column(Modifier.padding(10.dp)) {
                        Text(pkg.item.displayName, fontWeight = FontWeight.Bold)
                        Text(pkg.payload.summary.ifBlank { "${pkg.payload.pieces.size} pieza(s)" }, style = MaterialTheme.typography.caption)
                    }
                }
            }
        }
        Box(modifier = Modifier.weight(1f).fillMaxSize()) {
            if (selected == null) {
                Text("Elige un paquete o crea uno nuevo.", modifier = Modifier.padding(16.dp))
            } else {
                PackageEditor(controller, selected, onChanged = { refresh += 1 }, onDeleted = { selectedId = null; refresh += 1 })
            }
        }
    }
}

private data class EditablePiece(val kind: PackagePieceKind, val name: String, val text: String)

@Composable
private fun PackageEditor(controller: DesktopPackageManagerController, pkg: PackageContent, onChanged: () -> Unit, onDeleted: () -> Unit) {
    val key = pkg.item.identity.id to pkg.item.identity.revision
    var name by remember(key) { mutableStateOf(pkg.item.displayName) }
    var summary by remember(key) { mutableStateOf(pkg.payload.summary) }
    // Editable copies: a piece may be blank while typing; it is validated on «Guardar».
    var pieces by remember(key) { mutableStateOf(pkg.payload.pieces.map { EditablePiece(it.kind, it.name, it.text) }) }
    var removals by remember(key) { mutableStateOf(pkg.payload.removals.joinToString("\n")) }
    var hp by remember(key) { mutableStateOf(pkg.payload.hpDelta.toString()) }
    var ac by remember(key) { mutableStateOf(pkg.payload.armorClassDelta.toString()) }
    var attack by remember(key) { mutableStateOf(pkg.payload.attackBonusDelta.toString()) }
    var damage by remember(key) { mutableStateOf(pkg.payload.damageBonusDelta.toString()) }
    var notes by remember(key) { mutableStateOf(pkg.payload.notes) }
    var error by remember(key) { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text("Resumen") }, modifier = Modifier.fillMaxWidth())
        Text("Piezas que suma", fontWeight = FontWeight.Bold)
        pieces.forEachIndexed { index, piece ->
            Card(elevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PackagePieceKind.entries.forEach { kind ->
                            RadioButton(selected = piece.kind == kind, onClick = { pieces = pieces.toMutableList().also { it[index] = piece.copy(kind = kind) } })
                            Text(kind.spanishLabel(), style = MaterialTheme.typography.caption)
                        }
                    }
                    OutlinedTextField(value = piece.name, onValueChange = { v -> pieces = pieces.toMutableList().also { it[index] = piece.copy(name = v) } }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = piece.text, onValueChange = { v -> pieces = pieces.toMutableList().also { it[index] = piece.copy(text = v) } }, label = { Text("Texto") }, modifier = Modifier.fillMaxWidth())
                    TextButton(onClick = { pieces = pieces.filterIndexed { i, _ -> i != index } }) { Text("Quitar pieza") }
                }
            }
        }
        TextButton(onClick = { pieces = pieces + EditablePiece(PackagePieceKind.ACTION, "Nueva pieza", "") }) { Text("Agregar pieza") }
        OutlinedTextField(
            value = removals,
            onValueChange = { removals = it },
            label = { Text("Lo que quita (una línea por cada cosa; se muestra tachado)") },
            modifier = Modifier.fillMaxWidth(),
        )
        Text("Ajustes (se suman al valor de la criatura)", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = hp, onValueChange = { hp = it }, label = { Text("PV") }, modifier = Modifier.width(100.dp))
            OutlinedTextField(value = ac, onValueChange = { ac = it }, label = { Text("CA") }, modifier = Modifier.width(100.dp))
            OutlinedTextField(value = attack, onValueChange = { attack = it }, label = { Text("Bono ataque") }, modifier = Modifier.width(130.dp))
            OutlinedTextField(value = damage, onValueChange = { damage = it }, label = { Text("Bono daño") }, modifier = Modifier.width(130.dp))
        }
        OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Notas") }, modifier = Modifier.fillMaxWidth())
        error?.let { Text(it, color = MaterialTheme.colors.error) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                val numbers = listOf(hp, ac, attack, damage).map { it.trim().ifEmpty { "0" }.toIntOrNull() }
                if (name.isBlank() || numbers.any { it == null } || pieces.any { it.name.isBlank() }) {
                    error = "Revisa el nombre, las piezas y que los ajustes sean números (pueden ser negativos)."
                    return@Button
                }
                val payload = PackagePayload(
                    summary = summary.trim(),
                    pieces = pieces.map { PackagePiece(it.kind, it.name.trim(), it.text.trim()) },
                    removals = removals.lines().map { it.trim() }.filter { it.isNotEmpty() },
                    hpDelta = numbers[0]!!,
                    armorClassDelta = numbers[1]!!,
                    attackBonusDelta = numbers[2]!!,
                    damageBonusDelta = numbers[3]!!,
                    notes = notes.trim(),
                )
                if (controller.update(pkg, name, payload) is RevisionDecision.Accepted) onChanged() else error = "Otra ventana cambió este paquete. Vuelve a abrirlo."
            }) { Text("Guardar") }
            TextButton(onClick = { controller.delete(pkg); onDeleted() }) { Text("Borrar") }
        }
    }
}
