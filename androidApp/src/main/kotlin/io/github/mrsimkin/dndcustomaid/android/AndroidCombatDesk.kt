package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContent
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.EncounterContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.PackageContent
import io.github.mrsimkin.dndcustomaid.shared.content.PackageContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.COMBAT_CONDITIONS
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatDocument
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatOverrides
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatStatus
import io.github.mrsimkin.dndcustomaid.shared.dm.Combatant
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatantKind
import io.github.mrsimkin.dndcustomaid.shared.dm.DmCombatRepository
import io.github.mrsimkin.dndcustomaid.shared.dm.effective
import kotlin.random.Random
import kotlin.uuid.Uuid

/*
 * Base para DM, package 5: Mesa de Combate on the tablet (P11-P14, P19; D-0068 §4, §13).
 * Every change is saved at once on the device; nothing is applied automatically.
 */

private fun CombatStatus.spanishLabel(): String = when (this) {
    CombatStatus.PREPARING -> "En preparación"
    CombatStatus.ACTIVE -> "En curso"
    CombatStatus.ENDED -> "Terminado"
}

private fun nowSeconds(): Long = System.currentTimeMillis() / 1000

/** Everything the combat screen reads from the campaign, loaded once per screen. */
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
    val packages: List<PackageContent> = PackageContentRepository(database, reusable).let { repo ->
        repo.listCampaign(campaignId) + (accountId?.let(repo::listPersonal) ?: emptyList())
    }
    val packagesById = packages.associateBy { it.item.identity.id }
    val pcs = CharacterRepository(database).listCharacters(campaignId)
}

@Composable
internal fun CombatDeskScreen(
    campaign: Campaign,
    database: AppDatabase,
    accountId: Uuid?,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val repository = remember { DmCombatRepository(database) }
    val sources = remember(campaign.id) { CombatSources(database, campaign.id, accountId) }
    var combats by remember(campaign.id) { mutableStateOf(repository.list(campaign.id)) }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = combats.firstOrNull { it.id.toString() == selectedId }

    fun save(doc: CombatDocument) {
        repository.save(doc, nowSeconds())
        combats = repository.list(campaign.id)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            ) {
                StableBackIconButton(
                    onClick = { if (selected != null) selectedId = null else onBack() },
                    contentDescription = if (selected != null) "Volver a los combates" else "Volver al Escritorio DM",
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text("Mesa de Combate", style = MaterialTheme.typography.headlineMedium)
                    Text(selected?.let { "${campaign.name} · ${it.name}" } ?: campaign.name, style = MaterialTheme.typography.titleMedium)
                }
                StableSettingsIconButton(onClick = onOpenSettings)
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Box(modifier = Modifier.widthIn(max = 1200.dp).fillMaxSize().padding(horizontal = 14.dp)) {
                    if (selected == null) {
                        CombatList(
                            combats = combats,
                            onOpen = { selectedId = it.id.toString() },
                            onCreate = {
                                val created = CombatEngine.addPcs(
                                    CombatEngine.new(campaign.id, "Combate ${combats.size + 1}", nowSeconds()),
                                    sources.pcs,
                                )
                                save(created)
                                selectedId = created.id.toString()
                            },
                            onDelete = { doc ->
                                repository.delete(doc.id)
                                combats = repository.list(campaign.id)
                            },
                        )
                    } else {
                        CombatView(doc = selected, sources = sources, onChange = ::save)
                    }
                }
            }
        }
    }
}

@Composable
private fun CombatList(
    combats: List<CombatDocument>,
    onOpen: (CombatDocument) -> Unit,
    onCreate: () -> Unit,
    onDelete: (CombatDocument) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp))) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp))) {
                Button(onClick = onCreate) { Text("Nuevo combate") }
                Text(
                    "Empieza en «Preparar encuentro»: agrega criaturas, ajusta y luego «Empezar combate».",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        if (combats.isEmpty()) {
            item { Text("Aún no hay combates en esta campaña.", style = MaterialTheme.typography.bodyMedium) }
        }
        items(combats, key = { it.id.toString() }) { doc ->
            Card(onClick = { onOpen(doc) }, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(doc.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            listOfNotNull(
                                doc.status.spanishLabel(),
                                if (doc.status == CombatStatus.ACTIVE) "ronda ${doc.round}" else null,
                                "${doc.combatants.size} participantes",
                            ).joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    if (doc.status == CombatStatus.ENDED) TextButton(onClick = { onDelete(doc) }) { Text("Borrar") }
                    TextButton(onClick = { onOpen(doc) }) { Text("Abrir") }
                }
            }
        }
    }
}

@Composable
private fun CombatView(doc: CombatDocument, sources: CombatSources, onChange: (CombatDocument) -> Unit) {
    var expandedId by rememberSaveable(doc.id.toString()) { mutableStateOf<String?>(null) }
    var showAdd by rememberSaveable { mutableStateOf(false) }

    LazyColumn(contentPadding = PaddingValues(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp))) {
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    listOfNotNull(doc.status.spanishLabel(), if (doc.status == CombatStatus.ACTIVE) "Ronda ${doc.round}" else null).joinToString(" · "),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
                when (doc.status) {
                    CombatStatus.PREPARING -> Button(onClick = { onChange(CombatEngine.start(doc)) }) { Text("Empezar combate") }
                    CombatStatus.ACTIVE -> {
                        Button(onClick = { onChange(CombatEngine.nextTurn(doc)) }) { Text("Siguiente turno") }
                        OutlinedButton(onClick = { onChange(CombatEngine.end(doc)) }) { Text("Terminar combate") }
                    }
                    CombatStatus.ENDED -> {}
                }
                if (doc.status != CombatStatus.ENDED) {
                    OutlinedButton(onClick = { onChange(CombatEngine.rollInitiative(doc, Random.Default)) }) { Text("Tirar iniciativa (criaturas)") }
                    OutlinedButton(onClick = { showAdd = true }) { Text("Agregar") }
                    if (sources.pcs.any { pc -> doc.combatants.none { it.characterId == pc.id } }) {
                        OutlinedButton(onClick = { onChange(CombatEngine.addPcs(doc, sources.pcs)) }) { Text("Agregar PJ") }
                    }
                }
            }
        }
        items(doc.turnOrder, key = { it.id.toString() }) { combatant ->
            CombatantRow(
                doc = doc,
                combatant = combatant,
                sources = sources,
                expanded = expandedId == combatant.id.toString(),
                onToggleExpand = { expandedId = if (expandedId == combatant.id.toString()) null else combatant.id.toString() },
                onChange = onChange,
            )
        }
        if (doc.reserve.isNotEmpty()) {
            item { Text("Reserva", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp)) }
            items(doc.reserve, key = { "r" + it.id.toString() }) { combatant ->
                CombatantRow(
                    doc = doc,
                    combatant = combatant,
                    sources = sources,
                    expanded = expandedId == combatant.id.toString(),
                    onToggleExpand = { expandedId = if (expandedId == combatant.id.toString()) null else combatant.id.toString() },
                    onChange = onChange,
                )
            }
        }
    }

    if (showAdd) {
        AddCombatantsDialog(doc = doc, sources = sources, onDismiss = { showAdd = false }, onChange = { onChange(it); showAdd = false })
    }
}

@Composable
private fun CombatantRow(
    doc: CombatDocument,
    combatant: Combatant,
    sources: CombatSources,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onChange: (CombatDocument) -> Unit,
) {
    val effective = combatant.effective(sources.packagesById)
    val active = doc.activeCombatantId == combatant.id
    var hpAmount by remember(combatant.id) { mutableStateOf("") }
    val colors = if (active) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    } else {
        CardDefaults.cardColors()
    }
    Card(modifier = Modifier.fillMaxWidth(), colors = colors) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp))) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp))) {
                NumberField(
                    value = combatant.initiative,
                    label = "Inic.",
                    width = 76,
                    onValue = { onChange(CombatEngine.setInitiative(doc, combatant.id, it)) },
                )
                if (combatant.kind != CombatantKind.PC) {
                    TextButton(onClick = { onChange(CombatEngine.rollInitiative(doc, Random.Default, combatant.id)) }) { Text("Tirar") }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        combatant.label,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (combatant.defeated) TextDecoration.LineThrough else null,
                    )
                    Text(
                        listOfNotNull(
                            when (combatant.kind) { CombatantKind.PC -> "PJ"; CombatantKind.CREATURE -> "Criatura"; CombatantKind.CUSTOM -> "Improvisado" },
                            effective.armorClass?.let { "CA $it" },
                            if (combatant.concentrating) "Concentrado" else null,
                            combatant.conditions.takeIf { it.isNotEmpty() }?.joinToString(", "),
                            effective.packages.takeIf { it.isNotEmpty() }?.joinToString(", ") { "+" + it.item.displayName },
                        ).joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                if (combatant.kind == CombatantKind.PC) {
                    // P11: an optional reference note, never required and never the PC sheet.
                    TextFieldSmall(value = combatant.pcHpNote, label = "PV (nota)", width = 120) { onChange(CombatEngine.setPcHpNote(doc, combatant.id, it)) }
                } else {
                    Text(
                        "${combatant.currentHp ?: "—"} / ${effective.maxHp ?: "—"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (combatant.currentHp == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    )
                    TextFieldSmall(value = hpAmount, label = "PV", width = 70, numeric = true) { hpAmount = it.filter(Char::isDigit).take(4) }
                    TextButton(onClick = {
                        hpAmount.toIntOrNull()?.let { onChange(CombatEngine.damage(doc, combatant.id, it)) }
                        hpAmount = ""
                    }) { Text("Daño") }
                    TextButton(onClick = {
                        hpAmount.toIntOrNull()?.let { onChange(CombatEngine.heal(doc, combatant.id, it, effective.maxHp)) }
                        hpAmount = ""
                    }) { Text("Curar") }
                }
                TextButton(onClick = onToggleExpand) { Text(if (expanded) "Menos" else "Más") }
            }
            if (expanded) CombatantDetails(doc, combatant, sources, onChange)
        }
    }
}

@Composable
private fun CombatantDetails(doc: CombatDocument, combatant: Combatant, sources: CombatSources, onChange: (CombatDocument) -> Unit) {
    val effective = combatant.effective(sources.packagesById)
    val groupSize = doc.combatants.count { it.groupId == combatant.groupId }
    var wholeGroup by remember(combatant.id) { mutableStateOf(false) }
    var substitute by remember { mutableStateOf(false) }
    val creature = combatant.sourceContentId?.let(sources.creaturesById::get)

    HorizontalDivider()
    Text("Estados", style = MaterialTheme.typography.titleSmall)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        COMBAT_CONDITIONS.forEach { condition ->
            FilterChip(
                selected = condition in combatant.conditions,
                onClick = { onChange(CombatEngine.toggleCondition(doc, combatant.id, condition)) },
                label = { Text(condition) },
            )
        }
        FilterChip(selected = combatant.concentrating, onClick = { onChange(CombatEngine.setConcentrating(doc, combatant.id, !combatant.concentrating)) }, label = { Text("Concentración") })
        FilterChip(selected = combatant.defeated, onClick = { onChange(CombatEngine.setDefeated(doc, combatant.id, !combatant.defeated)) }, label = { Text("Derrotado") })
    }

    if (combatant.kind != CombatantKind.PC) {
        HorizontalDivider()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Ajustes rápidos", style = MaterialTheme.typography.titleSmall, modifier = Modifier.weight(1f))
            if (groupSize > 1) {
                CharacterCompactCheckboxItemV4(
                    checked = wholeGroup,
                    onCheckedChange = { wholeGroup = it },
                    label = "A todo el grupo ($groupSize)",
                )
            }
        }
        val o = combatant.overrides
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            NumberField(o.armorClass, "CA", 80) { onChange(CombatEngine.setOverrides(doc, combatant.id, o.copy(armorClass = it), wholeGroup)) }
            NumberField(o.maxHp, "PV máx.", 90) { onChange(CombatEngine.setOverrides(doc, combatant.id, o.copy(maxHp = it), wholeGroup)) }
            NumberField(o.attackBonus, "Bono ataque", 110) { onChange(CombatEngine.setOverrides(doc, combatant.id, o.copy(attackBonus = it), wholeGroup)) }
            TextFieldSmall(o.damageDice.orEmpty(), "Dados daño", 120) { onChange(CombatEngine.setOverrides(doc, combatant.id, o.copy(damageDice = it.ifBlank { null }), wholeGroup)) }
            NumberField(o.damageBonus, "Bono daño", 100) { onChange(CombatEngine.setOverrides(doc, combatant.id, o.copy(damageBonus = it), wholeGroup)) }
            if (combatant.hitDice.isNotBlank()) {
                TextButton(onClick = { onChange(CombatEngine.rollHp(doc, combatant.id, Random.Default)) }) { Text("Tirar PV (${combatant.hitDice})") }
            }
        }
        if (sources.packages.isNotEmpty()) {
            Text("Paquetes", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                sources.packages.forEach { pkg ->
                    val applied = pkg.item.identity.id in combatant.packageIds
                    FilterChip(
                        selected = applied,
                        onClick = {
                            onChange(
                                if (applied) CombatEngine.removePackage(doc, combatant.id, pkg, wholeGroup)
                                else CombatEngine.applyPackage(doc, combatant.id, pkg, wholeGroup),
                            )
                        },
                        label = { Text(pkg.item.displayName) },
                    )
                }
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TextButton(onClick = { onChange(CombatEngine.duplicate(doc, combatant.id)) }) { Text("Duplicar") }
            if (sources.creatures.isNotEmpty()) TextButton(onClick = { substitute = true }) { Text("Cambiar por otra criatura") }
            TextButton(onClick = { onChange(CombatEngine.setReserve(doc, combatant.id, !combatant.reserve)) }) {
                Text(if (combatant.reserve) "Sacar de la reserva" else "Pasar a la reserva")
            }
            TextButton(onClick = { onChange(CombatEngine.resetPatches(doc, combatant.id, wholeGroup)) }) { Text("Quitar ajustes y paquetes") }
            TextButton(onClick = { onChange(CombatEngine.remove(doc, combatant.id)) }) { Text("Quitar del combate") }
        }
    } else {
        TextButton(onClick = { onChange(CombatEngine.remove(doc, combatant.id)) }) { Text("Quitar del combate") }
    }

    // Quick reference: the stat block text plus what packages add or take away.
    val reference = buildList {
        creature?.payload?.let { p ->
            if (p.speed.isNotBlank()) add("Velocidad: ${p.speed}")
            if (p.traits.isNotBlank()) add("Rasgos: ${p.traits}")
            if (p.actions.isNotBlank()) add("Acciones: ${p.actions}")
            if (p.bonusActions.isNotBlank()) add("Acciones adicionales: ${p.bonusActions}")
            if (p.reactions.isNotBlank()) add("Reacciones: ${p.reactions}")
            if (p.tactics.isNotBlank()) add("Tácticas: ${p.tactics}")
        }
        effective.addedPieces.forEach { add("+ ${it.name}${if (it.text.isNotBlank()) ": ${it.text}" else ""}") }
        val bonus = effective.attackBonusOverride?.let { "bono de ataque $it" } ?: effective.attackBonusDelta.takeIf { it != 0 }?.let { "bono de ataque ${if (it > 0) "+" else ""}$it" }
        val damage = listOfNotNull(
            effective.damageDice,
            effective.damageBonusOverride?.let { "bono de daño $it" } ?: effective.damageBonusDelta.takeIf { it != 0 }?.let { "daño ${if (it > 0) "+" else ""}$it" },
        ).joinToString(", ")
        if (bonus != null || damage.isNotBlank()) add("Ajuste: " + listOfNotNull(bonus, damage.ifBlank { null }).joinToString(", "))
    }
    if (reference.isNotEmpty() || effective.removals.isNotEmpty()) {
        HorizontalDivider()
        Text("Referencia", style = MaterialTheme.typography.titleSmall)
        reference.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
        effective.removals.forEach { Text(it, style = MaterialTheme.typography.bodySmall, textDecoration = TextDecoration.LineThrough) }
    }

    if (substitute) {
        CreaturePicker(
            title = "Cambiar «${combatant.label}» por…",
            creatures = sources.creatures,
            withQuantity = false,
            onDismiss = { substitute = false },
            onPick = { creature, _ ->
                onChange(CombatEngine.substitute(doc, combatant.id, creature))
                substitute = false
            },
        )
    }
}

@Composable
private fun AddCombatantsDialog(doc: CombatDocument, sources: CombatSources, onDismiss: () -> Unit, onChange: (CombatDocument) -> Unit) {
    var mode by rememberSaveable { mutableStateOf("bestiary") }
    var customName by remember { mutableStateOf("") }
    var customHp by remember { mutableStateOf("") }
    var customAc by remember { mutableStateOf("") }
    var customQty by remember { mutableStateOf("1") }

    if (mode == "bestiary") {
        CreaturePicker(
            title = "Agregar del bestiario",
            creatures = sources.creatures,
            withQuantity = true,
            onDismiss = onDismiss,
            onPick = { creature, quantity -> onChange(CombatEngine.addCreature(doc, creature, quantity)) },
            extra = {
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (sources.encounters.isNotEmpty()) TextButton(onClick = { mode = "encounter" }) { Text("Desde un encuentro preparado") }
                    TextButton(onClick = { mode = "custom" }) { Text("Improvisado (nombre libre)") }
                }
            },
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (mode == "encounter") "Desde un encuentro preparado" else "Participante improvisado") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp))) {
                if (mode == "encounter") {
                    sources.encounters.forEach { encounter ->
                        TextButton(onClick = { onChange(CombatEngine.addEncounter(doc, encounter, sources.creaturesById)) }) {
                            Text("${encounter.item.displayName} (${encounter.payload.participants.sumOf { it.quantity }})")
                        }
                    }
                } else {
                    TextFieldSmall(customName, "Nombre", 260) { customName = it }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextFieldSmall(customHp, "PV", 80, numeric = true) { customHp = it.filter(Char::isDigit).take(4) }
                        TextFieldSmall(customAc, "CA", 80, numeric = true) { customAc = it.filter(Char::isDigit).take(2) }
                        TextFieldSmall(customQty, "Cantidad", 90, numeric = true) { customQty = it.filter(Char::isDigit).take(2) }
                    }
                }
            }
        },
        confirmButton = {
            if (mode == "custom") {
                Button(
                    enabled = customName.isNotBlank(),
                    onClick = {
                        onChange(CombatEngine.addCustom(doc, customName, customHp.toIntOrNull(), customAc.toIntOrNull(), (customQty.toIntOrNull() ?: 1).coerceIn(1, 50)))
                    },
                ) { Text("Agregar") }
            }
        },
        dismissButton = { TextButton(onClick = { mode = "bestiary" }) { Text("Volver") } },
    )
}

@Composable
private fun CreaturePicker(
    title: String,
    creatures: List<CreatureContent>,
    withQuantity: Boolean,
    onDismiss: () -> Unit,
    onPick: (CreatureContent, Int) -> Unit,
    extra: @Composable () -> Unit = {},
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
            Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp))) {
                extra()
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

@Composable
private fun NumberField(value: Int?, label: String, width: Int, onValue: (Int?) -> Unit) {
    var text by remember(value) { mutableStateOf(value?.toString().orEmpty()) }
    CharacterCompactOutlinedTextFieldV4(
        value = text,
        onValueChange = { raw ->
            val cleaned = raw.filter { it.isDigit() || it == '-' }.take(4)
            text = cleaned
            if (cleaned.isEmpty()) onValue(null) else cleaned.toIntOrNull()?.let(onValue)
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.width(width.dp),
    )
}

@Composable
private fun TextFieldSmall(value: String, label: String, width: Int, numeric: Boolean = false, onValue: (String) -> Unit) {
    CharacterCompactOutlinedTextFieldV4(
        value = value,
        onValueChange = onValue,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = if (numeric) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        modifier = Modifier.width(width.dp),
    )
}
