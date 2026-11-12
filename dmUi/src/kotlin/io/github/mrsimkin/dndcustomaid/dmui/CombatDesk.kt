package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResolvedD20Roll
import io.github.mrsimkin.dndcustomaid.shared.character.resolveCharacterD20Roll
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureAbilityScores
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContent
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PackageContent
import io.github.mrsimkin.dndcustomaid.shared.content.PackageContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PackagePieceKind
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.COMBAT_CONDITIONS
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatAttack
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatDocument
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatOverrides
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatStatus
import io.github.mrsimkin.dndcustomaid.shared.dm.Combatant
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatantKind
import io.github.mrsimkin.dndcustomaid.shared.dm.DmCombatRepository
import io.github.mrsimkin.dndcustomaid.shared.dm.describe
import io.github.mrsimkin.dndcustomaid.shared.dm.effective
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
private val OwnValueColor = Color(0xFFE8730C)



private fun d20(): Int = Random.nextInt(1, 21)

/** «Goblin 3» → «Goblin». */
private fun stemOf(label: String) = label.replace(Regex("""\s+\d+$"""), "")

/** Everything the combat desk reads from the campaign, loaded once per desk. */
private class CombatSources(database: AppDatabase, campaignId: Uuid, accountId: Uuid?) {
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

private enum class AddMode { ENCOUNTER, BESTIARY, CUSTOM }

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
                when (current.status) {
                    CombatStatus.PREPARING -> "Paso 1 · Preparar"
                    CombatStatus.ACTIVE -> (if (current.round == 0) "Ronda sorpresa" else "Ronda ${current.round}") + (actingText?.let { " · Actúa $it" } ?: "")
                    CombatStatus.ENDED -> "Terminado"
                },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            notice?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
            TextButton(enabled = undo.isNotEmpty(), onClick = { save(undo.removeAt(undo.lastIndex)) }) { Text("↶ Deshacer") }
            OutlinedButton(onClick = { confirmClose = true }) {
                Text(if (current.status == CombatStatus.PREPARING) "Borrar combate" else "Terminar combate")
            }
        }
        HorizontalDivider()
        Box(modifier = Modifier.fillMaxSize()) {
            when (current.status) {
                CombatStatus.PREPARING -> PrepareStep(
                    doc = current,
                    sources = sources,
                    onChange = ::change,
                    onEdit = { id, whole -> checkpoint(); editor = id to whole },
                    onAdd = { addMode = it },
                    onStart = { checkpoint(); showInitiative = true },
                )
                CombatStatus.ACTIVE -> LiveStep(
                    doc = current,
                    sources = sources,
                    onChange = ::change,
                    onChangeQuiet = ::changeQuiet,
                    onEdit = { id, whole -> checkpoint(); editor = id to whole },
                    onAdd = { addMode = it },
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

// ---------------------------------------------------------------- Paso 1 · Preparar

@Composable
private fun PrepareStep(
    doc: CombatDocument,
    sources: CombatSources,
    onChange: (CombatDocument) -> Unit,
    onEdit: (Uuid, Boolean) -> Unit,
    onAdd: (AddMode) -> Unit,
    onStart: () -> Unit,
) {
    var selectedGroup by remember(doc.id) { mutableStateOf<Uuid?>(null) }
    var selectedMember by remember(doc.id) { mutableStateOf<Uuid?>(null) }
    var expanded by remember(doc.id) { mutableStateOf(setOf<Uuid>()) }
    val groups = doc.combatants.filter { !it.reserve }.groupBy { it.groupId }
    val focusGroup = selectedGroup?.takeIf { gid -> doc.combatants.any { it.groupId == gid } }
    val members = doc.combatants.filter { it.groupId == focusGroup }
    val member = selectedMember?.let(doc::combatant)?.takeIf { it.groupId == focusGroup }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight().dmScroll().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("Participantes", style = MaterialTheme.typography.titleMedium)
            if (groups.isEmpty()) Text("Agrega criaturas desde la columna de la derecha.", style = MaterialTheme.typography.bodySmall)
            groups.forEach { (groupId, group) ->
                GroupBlock(
                    group = group,
                    selected = focusGroup == groupId,
                    selectedMemberId = member?.id,
                    expanded = groupId in expanded,
                    onSelectGroup = { selectedGroup = groupId; selectedMember = null },
                    onSelectMember = { selectedGroup = groupId; selectedMember = it },
                    onToggleExpand = { expanded = if (groupId in expanded) expanded - groupId else expanded + groupId },
                    onQuantity = { onChange(CombatEngine.setGroupQuantity(doc, groupId, it)) },
                )
            }
            if (doc.reserve.isNotEmpty()) {
                HorizontalDivider()
                Text("Reserva", style = MaterialTheme.typography.titleSmall)
                doc.reserve.forEach { c ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(c.label, modifier = Modifier.weight(1f))
                        TextButton(onClick = { onChange(CombatEngine.setReserve(doc, c.id, false)) }) { Text("a la mesa") }
                    }
                }
            }
        }
        Column(
            modifier = Modifier.weight(1.25f).fillMaxHeight().dmScroll().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (members.isEmpty()) {
                Text("Toca un participante para ajustarlo.", style = MaterialTheme.typography.bodyMedium)
            } else {
                SelectionPanel(
                    doc = doc,
                    members = members,
                    member = member,
                    sources = sources,
                    onChange = onChange,
                    onEdit = onEdit,
                    onPickMember = { selectedMember = it },
                )
            }
        }
        Column(
            modifier = Modifier.weight(0.9f).fillMaxHeight().dmScroll().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Agregar", style = MaterialTheme.typography.titleMedium)
            OutlinedButton(enabled = sources.encounters.isNotEmpty(), onClick = { onAdd(AddMode.ENCOUNTER) }, modifier = Modifier.fillMaxWidth()) { Text("Encuentro preparado") }
            OutlinedButton(onClick = { onAdd(AddMode.BESTIARY) }, modifier = Modifier.fillMaxWidth()) { Text("Bestiario (nombre o VD)") }
            OutlinedButton(onClick = { onAdd(AddMode.CUSTOM) }, modifier = Modifier.fillMaxWidth()) { Text("Improvisado") }
            if (sources.pcs.any { pc -> doc.combatants.none { it.characterId == pc.id } }) {
                OutlinedButton(onClick = { onChange(CombatEngine.addPcs(doc, sources.pcs)) }, modifier = Modifier.fillMaxWidth()) { Text("PJ que faltan") }
            }
            HorizontalDivider()
            EncounterGuide(doc, sources)
            Spacer(Modifier.height(4.dp))
            Button(enabled = doc.turnOrder.isNotEmpty(), onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text("Empezar combate ▸") }
            Text("Primero se pide la iniciativa, una sola vez.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun GroupBlock(
    group: List<Combatant>,
    selected: Boolean,
    selectedMemberId: Uuid?,
    expanded: Boolean,
    onSelectGroup: () -> Unit,
    onSelectMember: (Uuid) -> Unit,
    onToggleExpand: () -> Unit,
    onQuantity: (Int) -> Unit,
) {
    val first = group.first()
    val colors = if (selected && selectedMemberId == null) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.cardColors()
    }
    Card(onClick = onSelectGroup, colors = colors, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (first.kind != CombatantKind.PC && group.size > 1) {
                    TextButton(onClick = onToggleExpand) { Text(if (expanded) "▾" else "▸") }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        when {
                            first.kind == CombatantKind.PC -> "PJ · ${first.label}"
                            group.size > 1 -> "${stemOf(first.label)} ×${group.size}"
                            else -> first.label
                        } + if (group.any { it.hasOwnChanges }) "  ●" else "",
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Text(
                        listOfNotNull(first.baseArmorClass?.let { "CA $it" }, first.baseMaxHp?.let { "PV $it" }).joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (first.kind != CombatantKind.PC) {
                    TextButton(enabled = group.size > 1, onClick = { onQuantity(group.size - 1) }) { Text("−") }
                    TextButton(enabled = group.size < 50, onClick = { onQuantity(group.size + 1) }) { Text("+") }
                }
            }
            if (expanded && group.size > 1) {
                group.forEach { c ->
                    val mine = c.id == selectedMemberId
                    Text(
                        "${c.label}${if (c.hasOwnChanges) "  ●" else ""}",
                        color = if (c.hasOwnChanges) OwnValueColor else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (mine) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (mine) MaterialTheme.colorScheme.primaryContainer else Color.Transparent, MaterialTheme.shapes.small)
                            .clickable { onSelectMember(c.id) }
                            .padding(start = 36.dp, top = 5.dp, bottom = 5.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectionPanel(
    doc: CombatDocument,
    members: List<Combatant>,
    member: Combatant?,
    sources: CombatSources,
    onChange: (CombatDocument) -> Unit,
    onEdit: (Uuid, Boolean) -> Unit,
    onPickMember: (Uuid?) -> Unit,
) {
    val target = member ?: members.first()
    val whole = member == null && members.size > 1
    var substitute by remember { mutableStateOf(false) }

    if (target.kind == CombatantKind.PC) {
        Text(target.label, style = MaterialTheme.typography.titleLarge)
        Text("La iniciativa del PJ se escribe al empezar. Su hoja la lleva el jugador.", style = MaterialTheme.typography.bodyMedium)
        TextButton(onClick = { onChange(CombatEngine.remove(doc, target.id)) }) { Text("Quitar del combate") }
        return
    }

    Text(if (whole) "${stemOf(target.label)} ×${members.size}" else target.label, style = MaterialTheme.typography.titleLarge)
    sources.base(target)?.let { b ->
        Text(
            listOfNotNull("Bestiario", b.armorClass?.let { "CA $it" }, b.hitPoints?.let { "PV $it" + if (b.hitDice.isNotBlank()) " (${b.hitDice})" else "" }, b.challengeRating.takeIf { it.isNotBlank() }?.let { "VD $it" })
                .joinToString(" · "),
            style = MaterialTheme.typography.bodySmall,
        )
    }
    if (members.size > 1) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Aplicar a:", style = MaterialTheme.typography.bodyMedium)
            FilterChip(selected = whole, onClick = { onPickMember(null) }, label = { Text("los ${members.size}") })
            FilterChip(selected = !whole, onClick = { if (whole) onPickMember(members.first().id) }, label = { Text("uno") })
        }
    }
    Text("Ajustes rápidos", style = MaterialTheme.typography.titleSmall)
    QuickTweaks(doc, target, whole, onChange)
    PackageChips(doc, target, whole, sources, onChange)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Button(onClick = { onEdit(target.id, whole) }) { Text("Editar completa") }
        OutlinedButton(onClick = { onChange(CombatEngine.duplicate(doc, target.id)) }) { Text("Duplicar") }
        if (sources.creatures.isNotEmpty()) OutlinedButton(onClick = { substitute = true }) { Text("Cambiar criatura") }
        OutlinedButton(onClick = {
            val ids = if (whole) members.map { it.id } else listOf(target.id)
            onChange(ids.fold(doc) { acc, id -> CombatEngine.setReserve(acc, id, true) })
        }) { Text("A reserva") }
        OutlinedButton(onClick = { onChange(CombatEngine.resetAll(doc, target.id, whole)) }) { Text("Deshacer todo") }
        TextButton(onClick = {
            val ids = if (whole) members.map { it.id } else listOf(target.id)
            onChange(ids.fold(doc) { acc, id -> CombatEngine.remove(acc, id) })
        }) { Text("Quitar") }
    }
    if (target.hasOwnChanges) Text("● En naranja: lo propio de esta mesa. El bestiario no cambia.", style = MaterialTheme.typography.bodySmall, color = OwnValueColor)

    if (substitute) {
        CreaturePicker(
            title = "Cambiar «${target.label}» por…",
            creatures = sources.creatures,
            withQuantity = false,
            onDismiss = { substitute = false },
            onPick = { creature, _ ->
                val ids = if (whole) members.map { it.id } else listOf(target.id)
                onChange(ids.fold(doc) { acc, id -> CombatEngine.substitute(acc, id, creature) })
                substitute = false
            },
        )
    }
}

/** The five quick tweaks (P19/P32): CA, PV (with 🎲), attack, damage dice and damage bonus. */
@Composable
private fun QuickTweaks(doc: CombatDocument, target: Combatant, whole: Boolean, onChange: (CombatDocument) -> Unit) {
    val o = target.overrides
    fun set(next: CombatOverrides) = onChange(CombatEngine.setOverrides(doc, target.id, next, whole))
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        NumberField(o.armorClass, "CA", 80, own = o.armorClass != null) { set(o.copy(armorClass = it)) }
        NumberField(o.maxHp, "PV máx.", 90, own = o.maxHp != null) { set(o.copy(maxHp = it)) }
        if (target.hitDice.isNotBlank()) {
            TextButton(onClick = {
                val ids = if (whole) doc.combatants.filter { it.groupId == target.groupId }.map { it.id } else listOf(target.id)
                onChange(ids.fold(doc) { acc, id -> CombatEngine.rollHp(acc, id, Random.Default) })
            }) { Text("🎲 PV (${target.hitDice})") }
        }
        NumberField(o.attackBonus, "Ataque", 90, own = o.attackBonus != null) { set(o.copy(attackBonus = it)) }
        TextFieldSmall(o.damageDice.orEmpty(), "Dados daño", 110, own = o.damageDice != null) { set(o.copy(damageDice = it.ifBlank { null })) }
        NumberField(o.damageBonus, "Bono daño", 100, own = o.damageBonus != null) { set(o.copy(damageBonus = it)) }
    }
}

@Composable
private fun PackageChips(doc: CombatDocument, target: Combatant, whole: Boolean, sources: CombatSources, onChange: (CombatDocument) -> Unit) {
    if (sources.packages.isEmpty()) return
    Text("Paquetes", style = MaterialTheme.typography.titleSmall)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        sources.packages.forEach { pkg ->
            val applied = pkg.item.identity.id in target.packageIds
            FilterChip(
                selected = applied,
                onClick = {
                    onChange(
                        if (applied) CombatEngine.removePackage(doc, target.id, pkg, whole)
                        else CombatEngine.applyPackage(doc, target.id, pkg, whole),
                    )
                },
                label = { Text(pkg.item.displayName) },
            )
        }
    }
}

@Composable
private fun EncounterGuide(doc: CombatDocument, sources: CombatSources) {
    val encounter = doc.encounterId?.let(sources.encountersById::get)
    Text("Guía del encuentro", style = MaterialTheme.typography.titleSmall)
    if (encounter == null) {
        Text("Sin encuentro preparado. La guía aparece al cargar uno.", style = MaterialTheme.typography.bodySmall)
        return
    }
    val p = encounter.payload
    Text(encounter.item.displayName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    listOf(p.summary, p.context, p.environment, p.dmGuidance).filter { it.isNotBlank() }.forEach {
        Text(it, style = MaterialTheme.typography.bodySmall)
    }
}

// ---------------------------------------------------------------- Paso 2 · Iniciativa

@Composable
private fun InitiativeDialog(
    doc: CombatDocument,
    onChange: (CombatDocument) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val platform = LocalDmPlatform.current
    val visibleDice = platform.visibleDice
    var lastRoll by remember { mutableStateOf<Pair<String, CharacterResolvedD20Roll>?>(null) }
    val groups = doc.combatants.filter { !it.reserve }.groupBy { it.groupId }
    val missing = doc.turnOrder.count { it.initiative == null }

    fun rollOne(c: Combatant) {
        val die = d20()
        onChange(CombatEngine.rollInitiativeFor(doc, c.id, die))
        lastRoll = c.label to resolveCharacterD20Roll(firstDie = die, modifier = c.dexterityModifier)
    }

    fun rollGroup(group: List<Combatant>) {
        val die = d20()
        onChange(CombatEngine.rollGroupInitiative(doc, group.first().groupId, die))
        lastRoll = "${stemOf(group.first().label)} ×${group.size}" to resolveCharacterD20Roll(firstDie = die, modifier = group.first().dexterityModifier)
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth(0.86f).fillMaxHeight(0.9f)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Paso 2 · Iniciativa", style = MaterialTheme.typography.titleLarge)
                        Text("Los PJ se escriben. Cada grupo elige: una para el grupo o cada uno la suya.", style = MaterialTheme.typography.bodySmall)
                        // P22: after a successful ambush, the DM sees and can change who acts in the surprise round.
                        if (doc.surpriseActorIds.isNotEmpty()) {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text("Ronda sorpresa · actúan:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.CenterVertically))
                                doc.combatants.filter { it.kind == CombatantKind.PC }.forEach { pc ->
                                    val acts = pc.id in doc.surpriseActorIds
                                    FilterChip(
                                        selected = acts,
                                        onClick = { onChange(CombatEngine.setSurpriseActors(doc, if (acts) doc.surpriseActorIds - pc.id else doc.surpriseActorIds + pc.id)) },
                                        label = { Text(pc.label) },
                                    )
                                }
                                TextButton(onClick = { onChange(CombatEngine.setSurpriseActors(doc, emptyList())) }) { Text("Sin sorpresa") }
                            }
                        }
                    }
                    Button(onClick = {
                        onChange(CombatEngine.rollAllCreatureInitiative(doc, ::d20))
                        lastRoll = null
                    }) { Text("🎲 Tirar todas las criaturas") }
                }
                lastRoll?.let { (who, roll) ->
                    Text(who, style = MaterialTheme.typography.labelMedium)
                    platform.D20Result(roll)
                }
                Column(modifier = Modifier.weight(1f).dmScroll(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val pcs = doc.turnOrder.filter { it.kind == CombatantKind.PC }.sortedBy { it.label.lowercase() }
                    if (pcs.isNotEmpty()) {
                        Text("Personajes", style = MaterialTheme.typography.titleSmall)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            pcs.forEach { pc -> NumberField(pc.initiative, pc.label, 150) { onChange(CombatEngine.setInitiative(doc, pc.id, it)) } }
                        }
                    }
                    groups.values.filter { it.first().kind != CombatantKind.PC }.forEach { group ->
                        val first = group.first()
                        val shared = doc.sharesInitiative(first.groupId)
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        if (group.size > 1) "${stemOf(first.label)} ×${group.size}" else first.label,
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.weight(1f),
                                    )
                                    Text("DES ${signed(first.dexterityModifier)}", style = MaterialTheme.typography.bodySmall)
                                    if (group.size > 1) {
                                        FilterChip(selected = shared, onClick = { onChange(CombatEngine.setInitiativeShared(doc, first.groupId, true)) }, label = { Text("Una para el grupo") })
                                        FilterChip(selected = !shared, onClick = { onChange(CombatEngine.setInitiativeShared(doc, first.groupId, false)) }, label = { Text("Cada uno la suya") })
                                    }
                                }
                                if (shared || group.size == 1) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        NumberField(first.initiative, "Iniciativa", 110) { onChange(CombatEngine.setGroupInitiative(doc, first.groupId, it)) }
                                        TextButton(onClick = { if (group.size == 1) rollOne(first) else rollGroup(group) }) { Text("🎲") }
                                        DieNote(first, visibleDice)
                                        if (shared) Text("Actúan juntos en un solo turno.", style = MaterialTheme.typography.bodySmall)
                                    }
                                } else {
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        group.forEach { c ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                NumberField(c.initiative, c.label, 120) { onChange(CombatEngine.setInitiative(doc, c.id, it)) }
                                                TextButton(onClick = { rollOne(c) }) { Text("🎲") }
                                                DieNote(c, visibleDice)
                                            }
                                        }
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        TextButton(onClick = {
                                            onChange(group.fold(doc) { acc, c -> CombatEngine.rollInitiativeFor(acc, c.id, d20()) })
                                            lastRoll = null
                                        }) { Text("Tirar a cada uno") }
                                        TextButton(
                                            enabled = first.initiative != null,
                                            onClick = { onChange(CombatEngine.setGroupInitiative(doc, first.groupId, first.initiative)) },
                                        ) { Text("Mismo número") }
                                    }
                                }
                            }
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (missing > 0) "$missing sin iniciativa: irán al final y se corrigen después." else "Todo listo.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onDismiss) { Text("Volver a preparar") }
                    Button(onClick = onConfirm) { Text(if (doc.surpriseActorIds.isNotEmpty()) "Empezar ronda sorpresa ▸" else "Empezar ronda 1 ▸") }
                }
            }
        }
    }
}

/** The die behind a rolled number, shown only with «Dados visibles» (P34). */
@Composable
private fun DieNote(c: Combatant, visibleDice: Boolean) {
    val die = c.initiativeDie ?: return
    if (!visibleDice) return
    Text("d20 $die ${signed(c.dexterityModifier)}", style = MaterialTheme.typography.bodySmall)
}

// ---------------------------------------------------------------- Paso 3 · En curso

@Composable
private fun LiveStep(
    doc: CombatDocument,
    sources: CombatSources,
    onChange: (CombatDocument) -> Unit,
    onChangeQuiet: (CombatDocument) -> Unit,
    onEdit: (Uuid, Boolean) -> Unit,
    onAdd: (AddMode) -> Unit,
) {
    var selectedId by remember(doc.id) { mutableStateOf<Uuid?>(null) }
    var addMenu by remember { mutableStateOf(false) }
    val selected = selectedId?.let(doc::combatant) ?: doc.activeCombatantId?.let(doc::combatant)

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(0.95f).fillMaxHeight().padding(10.dp)) {
            Text("Orden", style = MaterialTheme.typography.titleMedium)
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                items(doc.turnSlots.flatten(), key = { it.id.toString() }) { c ->
                    OrderRow(
                        c = c,
                        maxHp = c.effective(sources.packagesById).maxHp,
                        acting = c.id in doc.actingIds,
                        selected = selected?.id == c.id,
                        onClick = { selectedId = c.id },
                    )
                }
                if (doc.reserve.isNotEmpty()) {
                    item { Text("Reserva", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 6.dp)) }
                    items(doc.reserve, key = { "r" + it.id.toString() }) { c ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(c.label, modifier = Modifier.weight(1f))
                            TextButton(onClick = { onChange(CombatEngine.setReserve(doc, c.id, false)) }) { Text("a la mesa") }
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { onChange(CombatEngine.nextTurn(doc)); selectedId = null }) { Text("Siguiente turno ▸") }
                Box {
                    OutlinedButton(onClick = { addMenu = true }) { Text("＋ Agregar") }
                    DropdownMenu(expanded = addMenu, onDismissRequest = { addMenu = false }) {
                        if (sources.encounters.isNotEmpty()) DropdownMenuItem(text = { Text("Encuentro preparado") }, onClick = { addMenu = false; onAdd(AddMode.ENCOUNTER) })
                        DropdownMenuItem(text = { Text("Bestiario") }, onClick = { addMenu = false; onAdd(AddMode.BESTIARY) })
                        DropdownMenuItem(text = { Text("Improvisado") }, onClick = { addMenu = false; onAdd(AddMode.CUSTOM) })
                    }
                }
            }
        }
        Column(
            modifier = Modifier.weight(1.15f).fillMaxHeight().dmScroll().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (selected == null) Text("Toca a alguien del orden.") else LiveTarget(doc, selected, sources, onChange, onChangeQuiet, onEdit)
        }
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight().dmScroll().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            GuideColumn(doc, sources)
        }
    }
}

@Composable
private fun OrderRow(c: Combatant, maxHp: Int?, acting: Boolean, selected: Boolean, onClick: () -> Unit) {
    val container = when {
        selected -> MaterialTheme.colorScheme.secondaryContainer
        acting -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(color = container, shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(if (acting) "▶" else " ", modifier = Modifier.width(16.dp), fontWeight = FontWeight.Bold)
            Text(c.initiative?.toString() ?: "—", style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(24.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    c.label + if (c.concentrating) " ◎" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (acting) FontWeight.Bold else FontWeight.Normal,
                    textDecoration = if (c.defeated) TextDecoration.LineThrough else null,
                )
                if (c.kind == CombatantKind.PC) {
                    Text(c.pcHpNote.ifBlank { "PV en papel" }, style = MaterialTheme.typography.bodySmall)
                } else {
                    val hp = c.currentHp
                    if (hp != null && maxHp != null && maxHp > 0) {
                        LinearProgressIndicator(
                            progress = { (hp.toFloat() / maxHp).coerceIn(0f, 1f) },
                            color = if (hp * 3 <= maxHp) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
                        )
                    }
                }
                if (c.conditions.isNotEmpty()) Text(c.conditions.joinToString(", "), style = MaterialTheme.typography.labelSmall)
            }
            if (c.kind != CombatantKind.PC) Text("${c.currentHp ?: "—"}/${maxHp ?: "—"}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun LiveTarget(
    doc: CombatDocument,
    c: Combatant,
    sources: CombatSources,
    onChange: (CombatDocument) -> Unit,
    onChangeQuiet: (CombatDocument) -> Unit,
    onEdit: (Uuid, Boolean) -> Unit,
) {
    val effective = c.effective(sources.packagesById)
    var amount by remember(c.id) { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(c.label, style = MaterialTheme.typography.titleLarge, textDecoration = if (c.defeated) TextDecoration.LineThrough else null)
            Text(listOfNotNull(if (c.kind == CombatantKind.PC) "PJ" else null, effective.armorClass?.let { "CA $it" }).joinToString(" · "), style = MaterialTheme.typography.bodyMedium)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            NumberField(c.initiative, "Iniciativa", 100) {
                onChangeQuiet(if (doc.sharesInitiative(c.groupId)) CombatEngine.setGroupInitiative(doc, c.groupId, it) else CombatEngine.setInitiative(doc, c.id, it))
            }
            if (c.kind != CombatantKind.PC) {
                TextButton(onClick = {
                    onChange(if (doc.sharesInitiative(c.groupId)) CombatEngine.rollGroupInitiative(doc, c.groupId, d20()) else CombatEngine.rollInitiativeFor(doc, c.id, d20()))
                }) { Text("🎲") }
            }
        }
    }
    if (c.kind == CombatantKind.PC) {
        // P11: an optional reference note, never required and never the PC sheet.
        TextFieldSmall(c.pcHpNote, "PV (nota, opcional)", 220) { onChangeQuiet(CombatEngine.setPcHpNote(doc, c.id, it)) }
    } else {
        Text(
            "${c.currentHp ?: "—"} / ${effective.maxHp ?: "—"} PV",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = if (c.currentHp == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(-10, -5, -1).forEach { d ->
                OutlinedButton(onClick = { onChange(CombatEngine.damage(doc, c.id, -d)) }) { Text(d.toString()) }
            }
            listOf(1, 5).forEach { d ->
                OutlinedButton(onClick = { onChange(CombatEngine.heal(doc, c.id, d, effective.maxHp)) }) { Text("+$d") }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextFieldSmall(amount, "Número", 90, numeric = true) { amount = it.filter(Char::isDigit).take(4) }
            TextButton(enabled = amount.isNotEmpty(), onClick = { amount.toIntOrNull()?.let { onChange(CombatEngine.damage(doc, c.id, it)) }; amount = "" }) { Text("Daño") }
            TextButton(enabled = amount.isNotEmpty(), onClick = { amount.toIntOrNull()?.let { onChange(CombatEngine.heal(doc, c.id, it, effective.maxHp)) }; amount = "" }) { Text("Curar") }
            TextButton(enabled = amount.isNotEmpty(), onClick = { amount.toIntOrNull()?.let { onChange(CombatEngine.setCurrentHp(doc, c.id, it)) }; amount = "" }) { Text("Fijar PV") }
        }
    }
    Text("Estados", style = MaterialTheme.typography.titleSmall)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        COMBAT_CONDITIONS.forEach { condition ->
            FilterChip(
                selected = condition in c.conditions,
                onClick = { onChange(CombatEngine.toggleCondition(doc, c.id, condition)) },
                label = { Text(condition) },
            )
        }
        FilterChip(selected = c.concentrating, onClick = { onChange(CombatEngine.setConcentrating(doc, c.id, !c.concentrating)) }, label = { Text("Concentración") })
        FilterChip(selected = c.defeated, onClick = { onChange(CombatEngine.setDefeated(doc, c.id, !c.defeated)) }, label = { Text("Derrotado") })
    }
    if (c.kind != CombatantKind.PC) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(onClick = { onEdit(c.id, false) }) { Text("Editar esta criatura") }
            TextButton(onClick = { onChange(CombatEngine.setReserve(doc, c.id, true)) }) { Text("A reserva") }
        }
    }
}

/** «Ahora actúa» follows the turn by itself (D-0069 §10-11), with the encounter guide and the full block. */
@Composable
private fun GuideColumn(doc: CombatDocument, sources: CombatSources) {
    var fullBlock by remember { mutableStateOf(false) }
    val acting = doc.actingIds.mapNotNull(doc::combatant)
    val first = acting.firstOrNull()
    Text("Ahora actúa", style = MaterialTheme.typography.titleMedium)
    if (first == null) {
        Text("Nadie en turno.", style = MaterialTheme.typography.bodySmall)
    } else {
        Text(if (acting.size > 1) "${stemOf(first.label)} ×${acting.size}" else first.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        if (first.kind == CombatantKind.PC) {
            Text("Turno de un PJ: lo juega su jugador.", style = MaterialTheme.typography.bodySmall)
        } else {
            CreatureGuide(first, sources, fullBlock)
            TextButton(onClick = { fullBlock = !fullBlock }) { Text(if (fullBlock) "Ocultar bloque completo" else "Ver bloque completo") }
        }
    }
    HorizontalDivider()
    EncounterGuide(doc, sources)
}

@Composable
private fun CreatureGuide(c: Combatant, sources: CombatSources, fullBlock: Boolean) {
    val base = sources.base(c)
    val effective = c.effective(sources.packagesById)
    val e = c.edits
    @Composable
    fun part(title: String, text: String?, own: Boolean = false) {
        if (text.isNullOrBlank()) return
        Text(title, style = MaterialTheme.typography.labelLarge, color = if (own) OwnValueColor else MaterialTheme.colorScheme.onSurface)
        Text(text, style = MaterialTheme.typography.bodySmall, color = if (own) OwnValueColor else MaterialTheme.colorScheme.onSurface)
    }
    part("Tácticas", base?.tactics)
    if (e.attacks.isNotEmpty()) part("Ataques propios", e.attacks.joinToString("\n") { it.describe() }, own = true)
    part("Acciones", e.actions ?: base?.actions, own = e.actions != null)
    effective.addedPieces.forEach { piece -> part("+ ${piece.name}", piece.text.ifBlank { " " }, own = true) }
    effective.removals.forEach { Text(it, style = MaterialTheme.typography.bodySmall, textDecoration = TextDecoration.LineThrough) }
    val tweak = listOfNotNull(
        effective.attackBonusOverride?.let { "ataque ${signed(it)}" } ?: effective.attackBonusDelta.takeIf { it != 0 }?.let { "ataque ${signed(it)} al bono" },
        effective.damageDice?.let { "daño $it" },
        effective.damageBonusOverride?.let { "bono de daño ${signed(it)}" } ?: effective.damageBonusDelta.takeIf { it != 0 }?.let { "daño ${signed(it)}" },
    )
    if (tweak.isNotEmpty()) part("Ajuste", tweak.joinToString(" · "), own = true)
    if (!fullBlock) return
    HorizontalDivider()
    if (base == null) {
        Text("Sin bloque del bestiario (participante improvisado).", style = MaterialTheme.typography.bodySmall)
        return
    }
    val scores = e.abilityScores ?: base.abilityScores
    part("Defensa", listOfNotNull(effective.armorClass?.let { "CA $it" }, (e.armorClassDetails ?: base.armorClassDetails).ifBlank { null }, effective.maxHp?.let { "PV $it" }).joinToString(" · "))
    part("Velocidad", e.speed ?: base.speed, own = e.speed != null)
    part(
        "Características",
        "FUE ${scores.strength} · DES ${scores.dexterity} · CON ${scores.constitution} · INT ${scores.intelligence} · SAB ${scores.wisdom} · CAR ${scores.charisma}",
        own = e.abilityScores != null,
    )
    part("Salvaciones", base.savingThrows)
    part("Habilidades", base.skills)
    part("Resistencias e inmunidades", e.defenses ?: listOf(base.damageVulnerabilities, base.damageResistances, base.damageImmunities, base.conditionImmunities).filter { it.isNotBlank() }.joinToString(" · "), own = e.defenses != null)
    part("Sentidos", base.senses)
    part("Rasgos", e.traits ?: base.traits, own = e.traits != null)
    part("Conjuros", e.spells, own = true)
    part("Acciones adicionales", base.bonusActions)
    part("Reacciones", base.reactions)
    part("Acciones legendarias", base.legendaryActions)
}

// ---------------------------------------------------------------- Paso 4 · Editar una criatura (P32)

private enum class EditorSection(val label: String) {
    QUICK("Rápido"),
    ABILITIES("Características"),
    DEFENSE("Defensa"),
    ATTACKS("Ataques"),
    TRAITS("Rasgos y acciones"),
    SPELLS("Conjuros"),
    PACKAGES("Paquetes"),
    NAME("Nombre y nota"),
}

@Composable
private fun CreatureEditorDialog(
    doc: CombatDocument,
    id: Uuid,
    initialWhole: Boolean,
    sources: CombatSources,
    onChange: (CombatDocument) -> Unit,
    onUndo: () -> Unit,
    onSaveAsCreature: (String, CreaturePayload) -> Unit,
    onClose: () -> Unit,
) {
    val c = doc.combatant(id) ?: return
    val groupSize = doc.combatants.count { it.groupId == c.groupId }
    var whole by remember(id) { mutableStateOf(initialWhole && groupSize > 1) }
    var section by remember(id) { mutableStateOf(EditorSection.QUICK) }
    var saveName by remember { mutableStateOf<String?>(null) }
    val base = sources.base(c) ?: CreaturePayload()
    val e = c.edits
    fun edit(next: io.github.mrsimkin.dndcustomaid.shared.dm.CreatureEdits) = onChange(CombatEngine.setEdits(doc, id, next, whole))

    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(shape = MaterialTheme.shapes.large, modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(0.92f)) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Editar · ${if (whole) "${stemOf(c.label)} ×$groupSize" else c.label}", style = MaterialTheme.typography.titleLarge)
                        Text("Lo propio va en naranja. El bestiario no cambia.", style = MaterialTheme.typography.bodySmall, color = OwnValueColor)
                    }
                    if (groupSize > 1) {
                        FilterChip(selected = !whole, onClick = { whole = false }, label = { Text("Solo esta") })
                        FilterChip(selected = whole, onClick = { whole = true }, label = { Text("Los $groupSize del grupo") })
                    }
                }
                Row(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.width(190.dp).fillMaxHeight().dmScroll()) {
                        EditorSection.entries.forEach { s ->
                            val active = s == section
                            Text(
                                s.label,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (active) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent, MaterialTheme.shapes.small)
                                    .clickable { section = s }
                                    .padding(horizontal = 10.dp, vertical = 9.dp),
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight().dmScroll().padding(start = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        when (section) {
                            EditorSection.QUICK -> QuickTweaks(doc, c, whole, onChange)
                            EditorSection.ABILITIES -> AbilityEditor(base.abilityScores, e.abilityScores) { edit(e.copy(abilityScores = it)) }
                            EditorSection.DEFENSE -> {
                                val o = c.overrides
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    NumberField(o.armorClass, "CA", 80, own = o.armorClass != null) { onChange(CombatEngine.setOverrides(doc, id, o.copy(armorClass = it), whole)) }
                                    NumberField(o.maxHp, "PV máx.", 90, own = o.maxHp != null) { onChange(CombatEngine.setOverrides(doc, id, o.copy(maxHp = it), whole)) }
                                }
                                OwnText("Detalle de la CA", e.armorClassDetails, base.armorClassDetails) { edit(e.copy(armorClassDetails = it)) }
                                OwnText(
                                    "Resistencias e inmunidades",
                                    e.defenses,
                                    listOf(base.damageVulnerabilities, base.damageResistances, base.damageImmunities, base.conditionImmunities).filter { it.isNotBlank() }.joinToString(" · "),
                                ) { edit(e.copy(defenses = it)) }
                                OwnText("Velocidad", e.speed, base.speed) { edit(e.copy(speed = it)) }
                            }
                            EditorSection.ATTACKS -> AttackEditor(e.attacks, base.actions, sources) { edit(e.copy(attacks = it)) }
                            EditorSection.TRAITS -> {
                                OwnText("Rasgos", e.traits, base.traits, lines = 4) { edit(e.copy(traits = it)) }
                                OwnText("Acciones", e.actions, base.actions, lines = 5) { edit(e.copy(actions = it)) }
                            }
                            EditorSection.SPELLS -> OwnText("Conjuros", e.spells, "", lines = 4) { edit(e.copy(spells = it)) }
                            EditorSection.PACKAGES -> PackageChips(doc, c, whole, sources, onChange)
                            EditorSection.NAME -> {
                                TextFieldSmall(c.label, "Nombre", 280) { onChange(CombatEngine.setLabel(doc, id, it)) }
                                DmTextField(
                                    value = c.notes,
                                    onValueChange = { onChange(CombatEngine.setNotes(doc, id, it)) },
                                    label = { Text("Nota") },
                                    minLines = 3,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onUndo) { Text("↶ Deshacer cambios") }
                    TextButton(onClick = { onChange(CombatEngine.resetAll(doc, id, whole)) }) { Text("Volver al bestiario") }
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onClick = { saveName = "${stemOf(c.label)} (variante)" }) { Text("Guardar como criatura nueva") }
                    Button(onClick = onClose) { Text("Listo") }
                }
            }
        }
    }

    saveName?.let { name ->
        AlertDialog(
            onDismissRequest = { saveName = null },
            title = { Text("Guardar como criatura nueva") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Se crea una criatura nueva en el bestiario de la campaña, con lo propio de esta. La original no cambia.", style = MaterialTheme.typography.bodySmall)
                    TextFieldSmall(name, "Nombre", 300) { saveName = it }
                }
            },
            confirmButton = {
                Button(enabled = name.isNotBlank(), onClick = {
                    onSaveAsCreature(name.trim(), CombatEngine.variantPayload(sources.base(c), c, c.effective(sources.packagesById)))
                    saveName = null
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { saveName = null }) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun AbilityEditor(base: CreatureAbilityScores, own: CreatureAbilityScores?, onValue: (CreatureAbilityScores?) -> Unit) {
    val current = own ?: base
    fun set(next: CreatureAbilityScores) = onValue(next.takeIf { it != base })
    Text("Cambiar una característica no recalcula la CA ni los ataques.", style = MaterialTheme.typography.bodySmall)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        NumberField(current.strength, "FUE", 80, own = current.strength != base.strength) { set(current.copy(strength = it ?: base.strength)) }
        NumberField(current.dexterity, "DES", 80, own = current.dexterity != base.dexterity) { set(current.copy(dexterity = it ?: base.dexterity)) }
        NumberField(current.constitution, "CON", 80, own = current.constitution != base.constitution) { set(current.copy(constitution = it ?: base.constitution)) }
        NumberField(current.intelligence, "INT", 80, own = current.intelligence != base.intelligence) { set(current.copy(intelligence = it ?: base.intelligence)) }
        NumberField(current.wisdom, "SAB", 80, own = current.wisdom != base.wisdom) { set(current.copy(wisdom = it ?: base.wisdom)) }
        NumberField(current.charisma, "CAR", 80, own = current.charisma != base.charisma) { set(current.copy(charisma = it ?: base.charisma)) }
    }
    if (own != null) TextButton(onClick = { onValue(null) }) { Text("↺ Como en el bestiario") }
}

@Composable
private fun AttackEditor(attacks: List<CombatAttack>, baseActions: String, sources: CombatSources, onValue: (List<CombatAttack>) -> Unit) {
    fun update(index: Int, change: (CombatAttack) -> CombatAttack) = onValue(attacks.mapIndexed { i, a -> if (i == index) change(a) else a })
    attacks.forEachIndexed { index, attack ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextFieldSmall(attack.name, "Nombre", 170, own = true) { v -> update(index) { it.copy(name = v) } }
                    NumberField(attack.bonus, "Bono", 80, own = true) { v -> update(index) { it.copy(bonus = v) } }
                    TextFieldSmall(attack.damage, "Daño", 100, own = true) { v -> update(index) { it.copy(damage = v) } }
                    TextFieldSmall(attack.damageType, "Tipo", 120, own = true) { v -> update(index) { it.copy(damageType = v) } }
                    TextFieldSmall(attack.reach, "Alcance", 110, own = true) { v -> update(index) { it.copy(reach = v) } }
                }
                if (attack.notes.isNotBlank()) Text(attack.notes, style = MaterialTheme.typography.bodySmall)
                TextButton(onClick = { onValue(attacks.filterIndexed { i, _ -> i != index }) }) { Text("Quitar ataque") }
            }
        }
    }
    OutlinedButton(onClick = { onValue(attacks + CombatAttack(name = "Ataque nuevo")) }) { Text("＋ Agregar ataque") }
    val pieces = sources.packages.flatMap { p -> p.payload.pieces.filter { it.kind == PackagePieceKind.ACTION } }.distinctBy { it.name }
    if (pieces.isNotEmpty()) {
        Text("Desde el inventario de piezas", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            pieces.forEach { piece -> TextButton(onClick = { onValue(attacks + CombatAttack(name = piece.name, notes = piece.text)) }) { Text(piece.name) } }
        }
    }
    if (baseActions.isNotBlank()) {
        HorizontalDivider()
        Text("Del bestiario (referencia)", style = MaterialTheme.typography.titleSmall)
        Text(baseActions, style = MaterialTheme.typography.bodySmall)
    }
}

/** A text the combatant may own: shows the bestiary text until the DM writes its own (orange, ↺). */
@Composable
private fun OwnText(label: String, own: String?, base: String, lines: Int = 1, onValue: (String?) -> Unit) {
    val isOwn = own != null && own != base
    DmTextField(
        value = own ?: base,
        onValueChange = { onValue(it.takeIf { v -> v != base }) },
        label = { Text(label, color = if (isOwn) OwnValueColor else Color.Unspecified) },
        singleLine = lines == 1,
        minLines = lines,
        modifier = Modifier.fillMaxWidth(),
    )
    if (isOwn) TextButton(onClick = { onValue(null) }) { Text("↺ Como en el bestiario") }
}

// ---------------------------------------------------------------- Agregar

@Composable
private fun AddDialog(mode: AddMode, doc: CombatDocument, sources: CombatSources, onDismiss: () -> Unit, onChange: (CombatDocument) -> Unit) {
    var customName by remember { mutableStateOf("") }
    var customHp by remember { mutableStateOf("") }
    var customAc by remember { mutableStateOf("") }
    var customQty by remember { mutableStateOf("1") }
    when (mode) {
        AddMode.BESTIARY -> CreaturePicker(
            title = "Agregar del bestiario",
            creatures = sources.creatures,
            withQuantity = true,
            onDismiss = onDismiss,
            onPick = { creature, quantity -> onChange(CombatEngine.addCreature(doc, creature, quantity)) },
        )
        AddMode.ENCOUNTER -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Encuentro preparado") },
            text = {
                Column {
                    sources.encounters.forEach { encounter ->
                        TextButton(onClick = { onChange(CombatEngine.addEncounter(doc, encounter, sources.creaturesById)) }) {
                            Text("${encounter.item.displayName} (${encounter.payload.participants.sumOf { it.quantity }})")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
        )
        AddMode.CUSTOM -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Participante improvisado") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextFieldSmall(customName, "Nombre", 260) { customName = it }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextFieldSmall(customHp, "PV", 80, numeric = true) { customHp = it.filter(Char::isDigit).take(4) }
                        TextFieldSmall(customAc, "CA", 80, numeric = true) { customAc = it.filter(Char::isDigit).take(2) }
                        TextFieldSmall(customQty, "Cantidad", 90, numeric = true) { customQty = it.filter(Char::isDigit).take(2) }
                    }
                }
            },
            confirmButton = {
                Button(enabled = customName.isNotBlank(), onClick = {
                    onChange(CombatEngine.addCustom(doc, customName, customHp.toIntOrNull(), customAc.toIntOrNull(), (customQty.toIntOrNull() ?: 1).coerceIn(1, 50)))
                }) { Text("Agregar") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
        )
    }
}

@Composable
private fun CreaturePicker(
    title: String,
    creatures: List<CreatureContent>,
    withQuantity: Boolean,
    onDismiss: () -> Unit,
    onPick: (CreatureContent, Int) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    val normalized = query.trim().lowercase()
    val shown = creatures
        .filter { normalized.isEmpty() || it.item.displayName.lowercase().contains(normalized) || it.payload.challengeRating == normalized }
        .sortedBy { it.item.displayName.lowercase() }
        .take(40)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextFieldSmall(query, "Buscar (nombre o VD)", 220) { query = it }
                    if (withQuantity) TextFieldSmall(quantity, "Cantidad", 90, numeric = true) { quantity = it.filter(Char::isDigit).take(2) }
                }
                if (creatures.isEmpty()) Text("No hay criaturas en este aparato. Llegan al sincronizar.", style = MaterialTheme.typography.bodySmall)
                LazyColumn(modifier = Modifier.widthIn(max = 520.dp)) {
                    items(shown, key = { it.item.identity.id.toString() }) { creature ->
                        TextButton(onClick = { onPick(creature, (quantity.toIntOrNull() ?: 1).coerceIn(1, 50)) }) {
                            Text(
                                listOfNotNull(
                                    creature.item.displayName,
                                    creature.payload.challengeRating.takeIf { it.isNotBlank() }?.let { "VD $it" },
                                    creature.payload.armorClass?.let { "CA $it" },
                                    creature.payload.hitPoints?.let { "PV $it" },
                                ).joinToString(" · "),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } },
    )
}

// ---------------------------------------------------------------- Campos

@Composable
private fun NumberField(value: Int?, label: String, width: Int, own: Boolean = false, onValue: (Int?) -> Unit) {
    var text by remember(value) { mutableStateOf(value?.toString().orEmpty()) }
    DmTextField(
        value = text,
        onValueChange = { raw ->
            val cleaned = raw.filter { it.isDigit() || it == '-' }.take(4)
            text = cleaned
            if (cleaned.isEmpty()) onValue(null) else cleaned.toIntOrNull()?.let(onValue)
        },
        label = { Text(label, color = if (own) OwnValueColor else Color.Unspecified) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.width(width.dp),
    )
}

@Composable
private fun TextFieldSmall(value: String, label: String, width: Int, numeric: Boolean = false, own: Boolean = false, onValue: (String) -> Unit) {
    DmTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label, color = if (own) OwnValueColor else Color.Unspecified) },
        singleLine = true,
        keyboardOptions = if (numeric) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        modifier = Modifier.width(width.dp),
    )
}
