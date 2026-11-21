package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.dm.COMBAT_CONDITIONS
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatDocument
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.Combatant
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatantKind
import io.github.mrsimkin.dndcustomaid.shared.dm.describe
import io.github.mrsimkin.dndcustomaid.shared.dm.effective
import kotlin.uuid.Uuid
// Refactor F3: the combat desk's live step (turn order, the acting target, the guide), moved out of CombatDesk.kt.

@Composable
internal fun LiveStep(
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

    val order: @Composable (Modifier) -> Unit = { columnModifier ->
        Column(modifier = columnModifier) {
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
                        DropdownMenuItem(text = { Text("Elegir o armar encuentro") }, onClick = { addMenu = false; onAdd(AddMode.ENCOUNTER) })
                        DropdownMenuItem(text = { Text("Bestiario") }, onClick = { addMenu = false; onAdd(AddMode.BESTIARY) })
                        DropdownMenuItem(text = { Text("Improvisado") }, onClick = { addMenu = false; onAdd(AddMode.CUSTOM) })
                    }
                }
            }
        }
    }
    val target: @Composable (Modifier) -> Unit = { columnModifier ->
        Column(
            modifier = columnModifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (selected == null) Text("Toca a alguien del orden.") else LiveTarget(doc, selected, sources, onChange, onChangeQuiet, onEdit)
        }
    }
    val guide: @Composable (Modifier) -> Unit = { columnModifier ->
        Column(
            modifier = columnModifier,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            GuideColumn(doc, sources)
        }
    }

    // Tablet QA of E1: three columns were too narrow on a tablet, so names piled up letter by letter.
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth >= 820.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                order(Modifier.weight(0.95f).fillMaxHeight().padding(10.dp))
                target(Modifier.weight(1.15f).fillMaxHeight().dmScroll().padding(10.dp))
                guide(Modifier.weight(1f).fillMaxHeight().dmScroll().padding(10.dp))
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                order(Modifier.weight(1f).fillMaxHeight().padding(10.dp))
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().dmScroll().padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    target(Modifier)
                    HorizontalDivider()
                    guide(Modifier)
                }
            }
        }
    }
}

@Composable
internal fun OrderRow(c: Combatant, maxHp: Int?, acting: Boolean, selected: Boolean, onClick: () -> Unit) {
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
internal fun LiveTarget(
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
internal fun GuideColumn(doc: CombatDocument, sources: CombatSources) {
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
internal fun CreatureGuide(c: Combatant, sources: CombatSources, fullBlock: Boolean) {
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
