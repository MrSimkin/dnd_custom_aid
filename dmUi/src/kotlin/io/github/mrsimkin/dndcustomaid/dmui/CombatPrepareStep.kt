package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterResolvedD20Roll
import io.github.mrsimkin.dndcustomaid.shared.character.resolveCharacterD20Roll
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatDocument
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatOverrides
import io.github.mrsimkin.dndcustomaid.shared.dm.Combatant
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatantKind
import kotlin.random.Random
import kotlin.uuid.Uuid
// Refactor F3: the combat desk's preparation step (groups, selection, quick tweaks, initiative), moved out of CombatDesk.kt.

@Composable
internal fun PrepareStep(
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

    val participants: @Composable (Modifier) -> Unit = { columnModifier ->
        Column(
            modifier = columnModifier,
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
    }
    val detail: @Composable (Modifier) -> Unit = { columnModifier ->
        Column(
            modifier = columnModifier,
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
    }
    val adding: @Composable (Modifier) -> Unit = { columnModifier ->
        Column(
            modifier = columnModifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Agregar", style = MaterialTheme.typography.titleMedium)
            OutlinedButton(onClick = { onAdd(AddMode.ENCOUNTER) }, modifier = Modifier.fillMaxWidth()) { Text("Elegir o armar encuentro") }
            OutlinedButton(onClick = { onAdd(AddMode.BESTIARY) }, modifier = Modifier.fillMaxWidth()) { Text("Bestiario (nombre o VD)") }
            OutlinedButton(onClick = { onAdd(AddMode.CUSTOM) }, modifier = Modifier.fillMaxWidth()) { Text("Improvisado") }
            if (sources.pcs.any { pc -> doc.combatants.none { it.characterId == pc.id } }) {
                OutlinedButton(onClick = { onChange(CombatEngine.addPcs(doc, sources.pcs)) }, modifier = Modifier.fillMaxWidth()) { Text("PJ que faltan") }
            }
            // Agent check of 0.7.0-preqa.4: the start button sat below a long encounter guide and had to
            // be found by scrolling; it now comes right after «Agregar».
            Spacer(Modifier.height(4.dp))
            Button(enabled = doc.turnOrder.isNotEmpty(), onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text("Empezar combate ▸") }
            Text("Primero se pide la iniciativa, una sola vez.", style = MaterialTheme.typography.bodySmall)
            HorizontalDivider()
            EncounterGuide(doc, sources)
        }
    }

    // Tablet QA of E1: three columns on a tablet left each one so narrow that names piled up letter
    // by letter. Narrow screens get two columns, with what you are adjusting under «Agregar».
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val column = Modifier.fillMaxHeight().dmScroll().padding(10.dp)
        if (maxWidth >= 820.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                participants(Modifier.weight(1f).then(column))
                detail(Modifier.weight(1.25f).then(column))
                adding(Modifier.weight(0.9f).then(column))
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                participants(Modifier.weight(1f).then(column))
                Column(
                    modifier = Modifier.weight(1f).then(column),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    adding(Modifier)
                    HorizontalDivider()
                    detail(Modifier)
                }
            }
        }
    }
}

/**
 * A narrow button for a participant row. Tablet QA of E1: the normal buttons took so much of the
 * narrow column that the creature's name was drawn one letter per line.
 */
@Composable
internal fun CompactRowButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier = Modifier.widthIn(min = 34.dp).height(36.dp),
    ) { Text(text) }
}

@Composable
internal fun GroupBlock(
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
                    CompactRowButton(if (expanded) "▾" else "▸", onClick = onToggleExpand)
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
                    CompactRowButton("−", enabled = group.size > 1) { onQuantity(group.size - 1) }
                    CompactRowButton("+", enabled = group.size < 50) { onQuantity(group.size + 1) }
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
internal fun SelectionPanel(
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
            adopt = sources::adopt,
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
internal fun QuickTweaks(doc: CombatDocument, target: Combatant, whole: Boolean, onChange: (CombatDocument) -> Unit) {
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
internal fun PackageChips(doc: CombatDocument, target: Combatant, whole: Boolean, sources: CombatSources, onChange: (CombatDocument) -> Unit) {
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
internal fun EncounterGuide(doc: CombatDocument, sources: CombatSources) {
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

@Composable
internal fun InitiativeDialog(
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
internal fun DieNote(c: Combatant, visibleDice: Boolean) {
    val die = c.initiativeDie ?: return
    if (!visibleDice) return
    Text("d20 $die ${signed(c.dexterityModifier)}", style = MaterialTheme.typography.bodySmall)
}
