package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureAbilityScores
import io.github.mrsimkin.dndcustomaid.shared.content.CreatureContent
import io.github.mrsimkin.dndcustomaid.shared.content.CreaturePayload
import io.github.mrsimkin.dndcustomaid.shared.content.PackagePieceKind
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatAttack
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatDocument
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.effective
import kotlin.uuid.Uuid
// Refactor F3: editing and adding creatures in a combat, and its small fields, moved out of CombatDesk.kt.

internal enum class EditorSection(val label: String) {
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
internal fun CreatureEditorDialog(
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
internal fun AbilityEditor(base: CreatureAbilityScores, own: CreatureAbilityScores?, onValue: (CreatureAbilityScores?) -> Unit) {
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
internal fun AttackEditor(attacks: List<CombatAttack>, baseActions: String, sources: CombatSources, onValue: (List<CombatAttack>) -> Unit) {
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
internal fun OwnText(label: String, own: String?, base: String, lines: Int = 1, onValue: (String?) -> Unit) {
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

@Composable
internal fun AddDialog(mode: AddMode, doc: CombatDocument, sources: CombatSources, onDismiss: () -> Unit, onChange: (CombatDocument) -> Unit) {
    var customName by remember { mutableStateOf("") }
    var customHp by remember { mutableStateOf("") }
    var customAc by remember { mutableStateOf("") }
    var customQty by remember { mutableStateOf("1") }
    when (mode) {
        AddMode.BESTIARY -> CreaturePicker(
            title = "Agregar criaturas",
            creatures = sources.creatures,
            adopt = sources::adopt,
            withQuantity = true,
            onDismiss = onDismiss,
            onPick = { creature, quantity -> onChange(CombatEngine.addCreature(doc, creature, quantity)) },
        )
        // Owner observation E1: encounters are chosen in the «Elegir encuentro» step, not in a pop-up.
        AddMode.ENCOUNTER -> Unit
        AddMode.CUSTOM -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Participante improvisado") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    TextFieldSmall(customName, "Nombre", 260) { customName = it }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextFieldSmall(customHp, "PV", 80, numeric = true) { customHp = it.filter(Char::isDigit).take(4) }
                        TextFieldSmall(customAc, "CA", 80, numeric = true) { customAc = it.filter(Char::isDigit).take(2) }
                        TextFieldQuantity(customQty) { customQty = it }
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
internal fun CreaturePicker(
    title: String,
    creatures: List<CreatureContent>,
    withQuantity: Boolean,
    onDismiss: () -> Unit,
    onPick: (CreatureContent, Int) -> Unit,
    adopt: ((io.github.mrsimkin.dndcustomaid.shared.dm.SrdMonster) -> CreatureContent)? = null,
) {
    var query by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    // QA 0.7.0-preqa.2 (C1, D6): the campaign's creatures first, then every SRD monster.
    val platform = LocalDmPlatform.current
    var srd by remember { mutableStateOf<List<io.github.mrsimkin.dndcustomaid.shared.dm.SrdMonster>>(emptyList()) }
    var fromSrd by remember { mutableStateOf(creatures.isEmpty()) }
    LaunchedEffect(Unit) { if (adopt != null) srd = SrdMonsterAssets.monsters(platform) }
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
                    if (withQuantity) TextFieldQuantity(quantity) { quantity = it }
                }
                if (adopt != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(selected = !fromSrd, onClick = { fromSrd = false }, label = { Text("De la campaña (${creatures.size})") })
                        FilterChip(selected = fromSrd, onClick = { fromSrd = true }, label = { Text("📖 Del SRD (${srd.size})") })
                    }
                }
                if (fromSrd && adopt != null) {
                    val monsters = io.github.mrsimkin.dndcustomaid.shared.dm.searchSrdMonsters(srd, query)
                    if (srd.isEmpty()) Text("Cargando los monstruos del SRD…", style = MaterialTheme.typography.bodySmall)
                    LazyColumn(modifier = Modifier.widthIn(max = 520.dp)) {
                        items(monsters, key = { "${it.edition}-${it.name}" }) { monster ->
                            TextButton(onClick = { onPick(adopt(monster), (quantity.toIntOrNull() ?: 1).coerceIn(1, 50)) }) {
                                Text(
                                    listOfNotNull(
                                        monster.name,
                                        "SRD ${monster.edition}",
                                        monster.payload.challengeRating.takeIf { it.isNotBlank() }?.let { "VD $it" },
                                        monster.payload.armorClass?.let { "CA $it" },
                                        monster.payload.hitPoints?.let { "PV $it" },
                                    ).joinToString(" · "),
                                )
                            }
                        }
                    }
                    return@Column
                }
                if (creatures.isEmpty()) Text("La campaña todavía no tiene criaturas: elige del SRD o prepáralas en la App de PC.", style = MaterialTheme.typography.bodySmall)
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
internal fun NumberField(value: Int?, label: String, width: Int, own: Boolean = false, onValue: (Int?) -> Unit) {
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

/**
 * The «Cantidad» box. Tablet QA of E1: it started with «1», so typing 3 left ×13. Touching the box
 * empties it, and leaving it empty puts the 1 back, so what you type is the quantity.
 */
@Composable
internal fun TextFieldQuantity(value: String, width: Int = 90, onValue: (String) -> Unit) {
    var hasFocus by remember { mutableStateOf(false) }
    DmTextField(
        value = value,
        onValueChange = { onValue(it.filter(Char::isDigit).take(2)) },
        label = { Text("Cantidad") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.width(width.dp).onFocusChanged { state ->
            if (state.isFocused && !hasFocus) {
                hasFocus = true
                if (value == "1") onValue("")
            } else if (!state.isFocused && hasFocus) {
                hasFocus = false
                if (value.isBlank()) onValue("1")
            }
        },
    )
}

@Composable
internal fun TextFieldSmall(value: String, label: String, width: Int, numeric: Boolean = false, own: Boolean = false, onValue: (String) -> Unit) {
    DmTextField(
        value = value,
        onValueChange = onValue,
        label = { Text(label, color = if (own) OwnValueColor else Color.Unspecified) },
        singleLine = true,
        keyboardOptions = if (numeric) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        modifier = Modifier.width(width.dp),
    )
}
