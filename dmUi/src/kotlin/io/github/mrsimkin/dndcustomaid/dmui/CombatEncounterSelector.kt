package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContent
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContent
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.EncounterDraft
import io.github.mrsimkin.dndcustomaid.shared.dm.EncounterDraftEntry
import io.github.mrsimkin.dndcustomaid.shared.dm.saveEncounterDraft
import kotlin.uuid.Uuid

/*
 * Owner observation E1 on 0.7.0-preqa.4: «La seleccion de encuentros preparados no puede ser una
 * ventana poptup, debe haber una ventana intermedia selector, para poder improvisar y modificar una,
 * o crear un encuentro nuevo».
 *
 * «Elegir encuentro»: a step of the combat desk (not a pop-up). On the left the campaign's
 * encounters; on the right the chosen one as a draft the DM changes before it enters the combat.
 * Using it never changes the saved encounter; saving changes or saving as new are their own buttons.
 */

@Composable
internal fun EncounterSelector(
    database: AppDatabase,
    campaignId: Uuid,
    sources: CombatSources,
    /** True when the combat has no creatures yet (a new combat): the skip button reads «improvisar». */
    startingCombat: Boolean,
    onUse: (EncounterDraft, Map<Uuid, CreatureContent>) -> Unit,
    onSkip: () -> Unit,
    onSaved: () -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var draft by remember { mutableStateOf<EncounterDraft?>(null) }
    // SRD monsters adopted while editing become campaign creatures; keep them for this screen.
    var adopted by remember { mutableStateOf(mapOf<Uuid, CreatureContent>()) }
    val creatures = sources.creaturesById + adopted
    var message by remember { mutableStateOf<String?>(null) }
    val base = draft?.baseId?.let(sources.encountersById::get)

    fun open(encounter: EncounterContent?) {
        draft = encounter?.let(EncounterDraft::fromEncounter) ?: EncounterDraft()
        message = null
    }

    fun save(asNew: Boolean) {
        val current = draft ?: return
        message = runCatching { saveEncounterDraft(database, campaignId, current, asNew, nowEpochSeconds()) }
            .fold(
                onSuccess = { saved ->
                    draft = EncounterDraft.fromEncounter(saved)
                    onSaved()
                    if (asNew) "«${saved.item.displayName}» quedó como encuentro nuevo de la campaña." else "Cambios guardados en «${saved.item.displayName}»."
                },
                onFailure = { it.message ?: "No se pudo guardar el encuentro." },
            )
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 760.dp
        if (wide) {
            Row(Modifier.fillMaxSize()) {
                EncounterList(
                    sources = sources, creatures = creatures, query = query, onQuery = { query = it },
                    selectedId = draft?.baseId, newSelected = draft != null && draft?.baseId == null,
                    startingCombat = startingCombat, onOpen = ::open, onSkip = onSkip,
                    modifier = Modifier.weight(0.9f).fillMaxHeight(),
                )
                Column(Modifier.weight(1.4f).fillMaxHeight()) {
                    DraftEditor(
                        draft = draft, base = base, creatures = creatures, sources = sources, message = message,
                        onDraft = { draft = it; message = null },
                        onAdopted = { c -> adopted = adopted + (c.item.identity.id to c) },
                        onUse = { d -> onUse(d, creatures) },
                        onSave = ::save,
                        onBack = null,
                    )
                }
            }
        } else if (draft == null) {
            EncounterList(
                sources = sources, creatures = creatures, query = query, onQuery = { query = it },
                selectedId = null, newSelected = false,
                startingCombat = startingCombat, onOpen = ::open, onSkip = onSkip,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            DraftEditor(
                draft = draft, base = base, creatures = creatures, sources = sources, message = message,
                onDraft = { draft = it; message = null },
                onAdopted = { c -> adopted = adopted + (c.item.identity.id to c) },
                onUse = { d -> onUse(d, creatures) },
                onSave = ::save,
                onBack = { draft = null },
            )
        }
    }
}

@Composable
private fun EncounterList(
    sources: CombatSources,
    creatures: Map<Uuid, CreatureContent>,
    query: String,
    onQuery: (String) -> Unit,
    selectedId: Uuid?,
    newSelected: Boolean,
    startingCombat: Boolean,
    onOpen: (EncounterContent?) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier,
) {
    val needle = query.trim().lowercase()
    val shown = sources.encounters
        .filter { needle.isEmpty() || it.item.displayName.lowercase().contains(needle) || summaryOf(it, creatures).lowercase().contains(needle) }
        .sortedBy { it.item.displayName.lowercase() }
    Column(modifier.dmScroll().padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Elegir encuentro", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            "Elige uno preparado y cámbialo antes de usarlo, arma uno nuevo, o sigue sin encuentro.",
            style = MaterialTheme.typography.bodySmall,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { onOpen(null) }) { Text("＋ Encuentro nuevo") }
            OutlinedButton(onClick = onSkip) { Text(if (startingCombat) "Sin encuentro: improvisar ▸" else "← Volver al combate") }
        }
        DmTextField(query, onQuery, label = { Text("Buscar encuentro o criatura") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Text("Encuentros de la campaña (${sources.encounters.size})", style = MaterialTheme.typography.titleSmall)
        if (sources.encounters.isEmpty()) {
            Text("La campaña aún no tiene encuentros. Arma uno con «＋ Encuentro nuevo».", style = MaterialTheme.typography.bodySmall)
        } else if (shown.isEmpty()) {
            Text("Ningún encuentro coincide con «$query».", style = MaterialTheme.typography.bodySmall)
        }
        if (newSelected) Text("✎ Encuentro nuevo (sin guardar)", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        shown.forEach { encounter ->
            val selected = encounter.item.identity.id == selectedId
            Card(
                onClick = { onOpen(encounter) },
                border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                colors = CardDefaults.cardColors(),
                modifier = Modifier.fillMaxWidth().handCursor(),
            ) {
                Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(encounter.item.displayName, fontWeight = FontWeight.SemiBold)
                    Text(summaryOf(encounter, creatures), style = MaterialTheme.typography.bodySmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun DraftEditor(
    draft: EncounterDraft?,
    base: EncounterContent?,
    creatures: Map<Uuid, CreatureContent>,
    sources: CombatSources,
    message: String?,
    onDraft: (EncounterDraft) -> Unit,
    onAdopted: (CreatureContent) -> Unit,
    onUse: (EncounterDraft) -> Unit,
    onSave: (asNew: Boolean) -> Unit,
    onBack: (() -> Unit)?,
) {
    var picking by remember { mutableStateOf(false) }
    var customName by remember { mutableStateOf("") }
    var customAc by remember { mutableStateOf("") }
    var customHp by remember { mutableStateOf("") }
    var customQty by remember { mutableStateOf("1") }
    Column(Modifier.fillMaxSize().dmScroll().padding(10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        onBack?.let { back -> TextButton(onClick = back) { Text("← Encuentros") } }
        if (draft == null) {
            Text("Elige un encuentro de la lista para verlo y cambiarlo, o toca «＋ Encuentro nuevo».", style = MaterialTheme.typography.bodyMedium)
            return@Column
        }
        val changed = draft.differsFrom(base)
        DmTextField(draft.name, { onDraft(draft.copy(name = it)) }, label = { Text("Nombre del encuentro") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Text(
            when {
                base == null -> "Encuentro nuevo: todavía no está guardado."
                changed -> "Cambiado: «Usar en este combate» no toca el encuentro guardado."
                else -> "Tal como está guardado en la campaña."
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (changed || base == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text("Criaturas (${draft.creatureCount})", style = MaterialTheme.typography.titleMedium)
        if (draft.entries.isEmpty()) Text("Sin criaturas todavía: agrégalas abajo.", style = MaterialTheme.typography.bodySmall)
        draft.entries.forEach { entry -> DraftLine(entry, creatures, onDraft = { onDraft(it(draft)) }) }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedButton(onClick = { picking = true }) { Text("＋ Del bestiario o del SRD") }
        }
        Text("Improvisada", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            TextFieldSmall(customName, "Nombre", 200) { customName = it }
            TextFieldSmall(customAc, "CA", 70, numeric = true) { customAc = it.filter(Char::isDigit).take(2) }
            TextFieldSmall(customHp, "PV", 80, numeric = true) { customHp = it.filter(Char::isDigit).take(4) }
            TextFieldQuantity(customQty) { customQty = it }
            OutlinedButton(enabled = customName.isNotBlank(), onClick = {
                onDraft(draft.plusImprovised(customName, customAc.toIntOrNull(), customHp.toIntOrNull(), (customQty.toIntOrNull() ?: 1).coerceIn(1, 50)))
                customName = ""; customAc = ""; customHp = ""; customQty = "1"
            }) { Text("Agregar") }
        }
        HorizontalDivider()
        Button(enabled = draft.entries.isNotEmpty(), onClick = { onUse(draft) }, modifier = Modifier.fillMaxWidth()) { Text("Usar en este combate ▸") }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            if (base != null) {
                OutlinedButton(enabled = changed && draft.name.isNotBlank(), onClick = { onSave(false) }) { Text("Guardar cambios en el encuentro") }
            }
            OutlinedButton(enabled = draft.entries.isNotEmpty() && draft.name.isNotBlank() && (base == null || draft.name.trim() != base.item.displayName || changed), onClick = { onSave(true) }) {
                Text(if (base == null) "Guardar encuentro" else "Guardar como encuentro nuevo")
            }
        }
        if (base != null && draft.name.trim() == base.item.displayName) {
            Text("Para guardarlo como nuevo, cámbiale el nombre.", style = MaterialTheme.typography.bodySmall)
        }
        message?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary) }
        base?.payload?.let { payload ->
            val guide = listOf(payload.summary, payload.dmGuidance).filter { it.isNotBlank() }.joinToString("\n\n")
            if (guide.isNotBlank()) {
                HorizontalDivider()
                Text("Guía del encuentro", style = MaterialTheme.typography.titleSmall)
                Text(guide, style = MaterialTheme.typography.bodySmall, maxLines = 12, overflow = TextOverflow.Ellipsis)
            }
        }
    }
    if (picking) {
        CreaturePicker(
            title = "Agregar al encuentro",
            creatures = sources.creatures,
            adopt = { monster -> sources.adopt(monster).also(onAdopted) },
            withQuantity = true,
            onDismiss = { picking = false },
            onPick = { creature, quantity ->
                onAdopted(creature)
                onDraft(draft!!.plusCreature(creature, quantity))
                picking = false
            },
        )
    }
}

@Composable
private fun DraftLine(entry: EncounterDraftEntry, creatures: Map<Uuid, CreatureContent>, onDraft: ((EncounterDraft) -> EncounterDraft) -> Unit) {
    val creature = entry.creatureId?.let(creatures::get)
    val name = entry.label.ifBlank { creature?.item?.displayName ?: "Criatura" }
    val ac = creature?.payload?.armorClass ?: entry.armorClass
    val hp = creature?.payload?.hitPoints ?: entry.hitPoints
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Column(Modifier.weight(1f)) {
                Text(name + if (entry.improvised) " (improvisada)" else "", fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(listOfNotNull(ac?.let { "CA $it" }, hp?.let { "PV $it" }).joinToString(" · ").ifBlank { "Sin CA ni PV" } + if (entry.reserve) " · queda en la Reserva del combate" else "", style = MaterialTheme.typography.bodySmall)
            }
            TextButton(enabled = entry.quantity > 1, onClick = { onDraft { it.withQuantity(entry.key, entry.quantity - 1) } }) { Text("−") }
            Text("×${entry.quantity}", modifier = Modifier.width(36.dp), fontWeight = FontWeight.Bold)
            TextButton(enabled = entry.quantity < 50, onClick = { onDraft { it.withQuantity(entry.key, entry.quantity + 1) } }) { Text("＋") }
            FilterChip(selected = entry.reserve, onClick = { onDraft { it.withReserve(entry.key, !entry.reserve) } }, label = { Text(if (entry.reserve) "✓ En reserva" else "Reserva") })
            TextButton(onClick = { onDraft { it.without(entry.key) } }) { Text("Quitar") }
        }
    }
}

private fun summaryOf(encounter: EncounterContent, creatures: Map<Uuid, CreatureContent>): String =
    encounter.payload.participants.joinToString(" · ") { p ->
        val name = p.label.ifBlank { p.sourceContentId?.let(creatures::get)?.item?.displayName ?: "Criatura" }
        "${p.quantity} × $name"
    }.ifBlank { "Sin criaturas" }
