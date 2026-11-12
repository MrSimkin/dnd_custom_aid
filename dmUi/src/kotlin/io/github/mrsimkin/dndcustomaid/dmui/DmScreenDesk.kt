package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProficiencyType
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.SkillKey
import io.github.mrsimkin.dndcustomaid.shared.character.characterAbilityAbbreviation
import io.github.mrsimkin.dndcustomaid.shared.character.characterSkillSpanishLabel
import io.github.mrsimkin.dndcustomaid.shared.character.passiveInsight
import io.github.mrsimkin.dndcustomaid.shared.character.passiveInvestigation
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.HostedRulesPassage
import io.github.mrsimkin.dndcustomaid.shared.dm.PartyLens
import io.github.mrsimkin.dndcustomaid.shared.dm.PartyLensMember
import io.github.mrsimkin.dndcustomaid.shared.dm.PartyLensRowKind
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdCorpus
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdQuickReference
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdSearchHit
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdVersion
import io.github.mrsimkin.dndcustomaid.shared.dm.parseSrdCorpus
import io.github.mrsimkin.dndcustomaid.shared.dm.parseSrdQuickReference
import io.github.mrsimkin.dndcustomaid.shared.dm.partyLens
import io.github.mrsimkin.dndcustomaid.shared.dm.searchSrd
import io.github.mrsimkin.dndcustomaid.shared.dm.spanishLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

/*
 * Base para DM · Pantalla del DM (approved design DM_UX_APROBADA.md §2; D-0069 §3, P4, P9, P10).
 *
 * Left: the group sheet (compact PC cards or the Vista Grupo table). Right: «Pregunta una regla…»
 * and the ten quick-reference cards. A PC's quick sheet, its read-only full sheet and every
 * reference card open in the desk's side panel.
 */

/** Loads the SRD assets once per process; parsing 3 MB of JSON stays off the main thread. */
internal object SrdAssets {
    private var quick: SrdQuickReference? = null
    private var corpora: List<SrdCorpus>? = null

    suspend fun quickReference(platform: DmPlatform): SrdQuickReference = quick ?: withContext(Dispatchers.Default) {
        parseSrdQuickReference(platform.readSrd("quick_reference_es.json")).also { quick = it }
    }

    suspend fun corpora(platform: DmPlatform): List<SrdCorpus> = corpora ?: withContext(Dispatchers.Default) {
        listOf("srd_5_1_es.json", "srd_5_2_1_es.json").map { parseSrdCorpus(platform.readSrd(it)) }.also { corpora = it }
    }
}

/** The campaign's rules version for the reference, remembered per campaign on this device. */
internal class DmScreenPreferences(private val store: DmKeyValueStore) {
    fun campaignVersion(campaignId: Uuid): SrdVersion =
        SrdVersion.fromKey(store.getString("srd_version_$campaignId")) ?: SrdVersion.SRD_5_2_1

    fun setCampaignVersion(campaignId: Uuid, version: SrdVersion) {
        store.putString("srd_version_$campaignId", version.key)
    }
}

private fun partyLensFor(database: AppDatabase, characters: List<CharacterSheet>): PartyLens {
    val closures = CharacterClosureRepository(database)
    return partyLens(
        members = characters.map { sheet -> PartyLensMember(sheet, runCatching { closures.state(sheet.id).senses }.getOrDefault(emptyList())) },
        formatFeet = ::formatFeet,
    )
}

@Composable
internal fun DmScreenDesk(campaign: Campaign, database: AppDatabase, onOpenPanel: (DeskPanel) -> Unit) {
    val platform = LocalDmPlatform.current
    val characters = remember(campaign.id) { CharacterRepository(database).listCharacters(campaign.id) }
    val lens = remember(characters) { partyLensFor(database, characters) }
    var tableView by rememberSaveable { mutableStateOf(false) }
    val screenPrefs = remember { DmScreenPreferences(platform.store) }
    val workspacePrefs = remember { DmWorkspacePreferences(platform.store) }
    var version by remember(campaign.id) { mutableStateOf(screenPrefs.campaignVersion(campaign.id)) }
    var quick by remember { mutableStateOf<SrdQuickReference?>(null) }
    var recent by remember { mutableStateOf(workspacePrefs.recentReferenceCards()) }
    LaunchedEffect(Unit) { quick = SrdAssets.quickReference(platform) }

    fun openCard(id: String) {
        workspacePrefs.pushRecentReferenceCard(id)
        recent = workspacePrefs.recentReferenceCards()
        onOpenPanel(DeskPanel.ReferenceCard(id))
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1.25f).fillMaxHeight().dmScroll().padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Grupo · ${characters.size} PJ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                FilterChip(selected = !tableView, onClick = { tableView = false }, label = { Text("Fichas") })
                Box(modifier = Modifier.width(6.dp))
                FilterChip(selected = tableView, onClick = { tableView = true }, label = { Text("Vista Grupo") })
            }
            when {
                characters.isEmpty() -> Text("Esta campaña aún no tiene PJ en este aparato. Llegan al sincronizar.", style = MaterialTheme.typography.bodyMedium)
                tableView -> PartyLensTable(lens)
                else -> characters.sortedBy { it.name.lowercase() }.forEach { pc ->
                    PcGroupCard(pc, lens, onOpen = { onOpenPanel(DeskPanel.PcQuickSheet(pc.id)) })
                }
            }
            if (!tableView && characters.isNotEmpty()) {
                Text("Toca un PJ para ver su hoja rápida al costado.", style = MaterialTheme.typography.bodySmall)
            }
        }
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight().dmScroll().padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth().clickable { onOpenPanel(DeskPanel.Rules) },
            ) {
                Text("🔎 Pregunta una regla…", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Referencia rápida", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                SrdVersion.entries.forEach { option ->
                    FilterChip(
                        selected = version == option,
                        onClick = {
                            version = option
                            screenPrefs.setCampaignVersion(campaign.id, option)
                        },
                        label = { Text(option.edition) },
                    )
                    Box(modifier = Modifier.width(4.dp))
                }
            }
            quick?.let { reference ->
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    reference.cards.forEach { card ->
                        Card(onClick = { openCard(card.id) }, modifier = Modifier.width(150.dp)) {
                            Text(card.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(10.dp))
                        }
                    }
                }
                val recentCards = recent.mapNotNull { id -> reference.cards.firstOrNull { it.id == id } }
                if (recentCards.isNotEmpty()) {
                    Text("Consultadas hace poco", style = MaterialTheme.typography.titleSmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        recentCards.forEach { card -> TextButton(onClick = { openCard(card.id) }) { Text(card.title) } }
                    }
                }
            } ?: Text("Cargando el SRD…", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun PcGroupCard(pc: CharacterSheet, lens: PartyLens, onOpen: () -> Unit) {
    val column = lens.columns.indexOfFirst { it.characterId == pc.id }
    fun best(kind: PartyLensRowKind) = lens.rows.firstOrNull { it.kind == kind }?.cells?.getOrNull(column)?.best == true
    fun text(kind: PartyLensRowKind) = lens.rows.firstOrNull { it.kind == kind }?.cells?.getOrNull(column)?.text.orEmpty()

    @Composable
    fun stat(label: String, kind: PartyLensRowKind) {
        val highlight = best(kind)
        Text(
            "$label ${text(kind)}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
            color = if (highlight) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .background(if (highlight) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small)
                .padding(horizontal = 6.dp, vertical = 2.dp),
        )
    }
    Card(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row {
                Text(pc.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(pc.classes.joinToString(" / ") { "${it.name} ${it.level}" }.ifBlank { "Sin clase" }, style = MaterialTheme.typography.bodySmall)
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                stat("CA", PartyLensRowKind.ARMOR_CLASS)
                stat("PV máx", PartyLensRowKind.MAX_HIT_POINTS)
                stat("Perc", PartyLensRowKind.PASSIVE_PERCEPTION)
                stat("Persp", PartyLensRowKind.PASSIVE_INSIGHT)
                stat("Inv", PartyLensRowKind.PASSIVE_INVESTIGATION)
                stat("", PartyLensRowKind.SPEED)
                text(PartyLensRowKind.SENSES).takeIf { it != "—" }?.let { Text("👁 $it", style = MaterialTheme.typography.bodySmall) }
            }
        }
    }
}

@Composable
private fun PartyLensTable(lens: PartyLens) {
    val labelWidth = 150.dp
    val columnWidth = 130.dp
    Column(modifier = Modifier.horizontalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row {
            Box(modifier = Modifier.width(labelWidth))
            lens.columns.forEach { Text(it.name, style = MaterialTheme.typography.titleSmall, modifier = Modifier.width(columnWidth).padding(horizontal = 6.dp)) }
        }
        HorizontalDivider()
        lens.rows.forEach { row ->
            Row {
                Text(row.kind.spanishLabel(), fontWeight = FontWeight.Medium, modifier = Modifier.width(labelWidth).padding(vertical = 6.dp))
                row.cells.forEach { cell ->
                    Text(
                        cell.text,
                        fontWeight = if (cell.best) FontWeight.Bold else FontWeight.Normal,
                        color = if (cell.best) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .width(columnWidth)
                            .padding(2.dp)
                            .background(if (cell.best) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background, MaterialTheme.shapes.small)
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                    )
                }
            }
            HorizontalDivider()
        }
        Text("Datos fijos de cada hoja. Resaltado: el mejor valor de la fila.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp))
    }
}


@Composable
private fun SheetLine(label: String, value: String) {
    if (value.isBlank()) return
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(label, style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(130.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
    HorizontalDivider()
}

@Composable
internal fun PcQuickSheetPanel(database: AppDatabase, characterId: Uuid, onOpenFull: () -> Unit) {
    val pc = remember(characterId) { CharacterRepository(database).character(characterId) }
    val senses = remember(characterId) { runCatching { CharacterClosureRepository(database).state(characterId).senses }.getOrDefault(emptyList()) }
    if (pc == null) {
        Text("Este PJ ya no está en el aparato.")
        return
    }
    Column(modifier = Modifier.fillMaxSize().dmScroll().padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(pc.name, style = MaterialTheme.typography.titleLarge)
        Text(pc.classes.joinToString(" / ") { "${it.name} ${it.level}" }, style = MaterialTheme.typography.bodySmall)
        SheetLine("CA · PV máx · Velocidad", "${pc.armorClass} · ${pc.maxHp} · ${formatFeet(pc.speed)}")
        SheetLine(
            "Salvaciones destacadas",
            CharacterAbility.entries.filter { a -> pc.savingThrows.any { it.ability == a && it.proficient } }
                .joinToString(" · ") { "${characterAbilityAbbreviation(it)} ${signed(pc.savingThrowTotal(it))}" },
        )
        SheetLine("Pasivas", "Percepción ${pc.passivePerception} · Perspicacia ${pc.passiveInsight} · Investigación ${pc.passiveInvestigation}")
        SheetLine("Sentidos", senses.sortedBy { it.sortOrder }.joinToString(", ") { s -> s.rangeFeet?.let { "${s.name} ${formatFeet(it)}" } ?: s.name })
        SheetLine("Idiomas", pc.proficiencies.filter { it.type == CharacterProficiencyType.LANGUAGE }.joinToString(", ") { it.name })
        SheetLine(
            "Ataques principales",
            pc.combatEntries.sortedBy { it.sortOrder }.take(4).joinToString("\n") { e ->
                listOfNotNull(e.name, e.attackModifier?.let(::signed), e.damageEffect.takeIf { it.isNotBlank() }).joinToString(" · ")
            },
        )
        SheetLine("Rasgos clave", pc.traits.take(6).joinToString(" · ") { it.name })
        pc.spellSaveDc?.let { SheetLine("Conjuros", "CD $it" + (pc.spellAttackModifier?.let { m -> " · ataque ${signed(m)}" } ?: "")) }
        TextButton(onClick = onOpenFull) { Text("Abrir hoja completa (lectura) ▸") }
    }
}

@Composable
internal fun PcFullSheetPanel(database: AppDatabase, characterId: Uuid) {
    val pc = remember(characterId) { CharacterRepository(database).character(characterId) }
    if (pc == null) {
        Text("Este PJ ya no está en el aparato.")
        return
    }
    Column(modifier = Modifier.fillMaxSize().dmScroll().padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(pc.name, style = MaterialTheme.typography.titleLarge)
        Text("Solo lectura. Para cambiar la hoja, el jugador la edita en su App.", style = MaterialTheme.typography.bodySmall)
        Text("Características", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CharacterAbility.entries.forEach { a ->
                Text(
                    "${characterAbilityAbbreviation(a)} ${pc.abilityScore(a)} (${signed(pc.abilityModifier(a))})",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.small).padding(horizontal = 6.dp, vertical = 3.dp),
                )
            }
        }
        Text("Salvaciones", style = MaterialTheme.typography.titleSmall)
        Text(CharacterAbility.entries.joinToString(" · ") { "${characterAbilityAbbreviation(it)} ${signed(pc.savingThrowTotal(it))}" }, style = MaterialTheme.typography.bodyMedium)
        Text("Habilidades", style = MaterialTheme.typography.titleSmall)
        Text(SkillKey.entries.joinToString(" · ") { "${characterSkillSpanishLabel(it)} ${signed(pc.skillTotal(it))}" }, style = MaterialTheme.typography.bodyMedium)
        if (pc.combatEntries.isNotEmpty()) {
            Text("Ataques", style = MaterialTheme.typography.titleSmall)
            pc.combatEntries.sortedBy { it.sortOrder }.forEach { e ->
                Text(listOfNotNull(e.name, e.attackModifier?.let(::signed), e.damageEffect.takeIf { it.isNotBlank() }, e.rangeText).joinToString(" · "), style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (pc.spells.isNotEmpty()) {
            Text("Conjuros", style = MaterialTheme.typography.titleSmall)
            pc.spells.groupBy { it.level }.toSortedMap().forEach { (level, spells) ->
                Text("${if (level == 0) "Trucos" else "Nivel $level"}: ${spells.joinToString(", ") { it.name }}", style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (pc.traits.isNotEmpty()) {
            Text("Rasgos", style = MaterialTheme.typography.titleSmall)
            pc.traits.forEach { t ->
                Text(buildAnnotatedString {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold)); append(t.name); pop()
                    if (t.description.isNotBlank()) append(". ${t.description}")
                }, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (pc.inventoryItems.isNotEmpty()) {
            Text("Equipo", style = MaterialTheme.typography.titleSmall)
            Text(pc.inventoryItems.joinToString(" · ") { if (it.quantity > 1) "${it.name} ×${it.quantity}" else it.name }, style = MaterialTheme.typography.bodyMedium)
        }
        val others = pc.proficiencies.filter { it.type != CharacterProficiencyType.LANGUAGE }
        if (others.isNotEmpty()) {
            Text("Competencias", style = MaterialTheme.typography.titleSmall)
            Text(others.joinToString(" · ") { it.name }, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
internal fun ReferenceCardPanel(campaignId: Uuid, cardId: String) {
    val platform = LocalDmPlatform.current
    val prefs = remember { DmScreenPreferences(platform.store) }
    var quick by remember { mutableStateOf<SrdQuickReference?>(null) }
    var showingOther by remember(cardId) { mutableStateOf(false) }
    LaunchedEffect(Unit) { quick = SrdAssets.quickReference(platform) }
    val reference = quick ?: run {
        Text("Cargando el SRD…", modifier = Modifier.padding(vertical = 12.dp))
        return
    }
    val card = reference.cards.firstOrNull { it.id == cardId } ?: return
    val campaignVersion = prefs.campaignVersion(campaignId)
    val shown = if (showingOther) campaignVersion.other else campaignVersion
    Column(modifier = Modifier.fillMaxSize().dmScroll().padding(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(card.title, style = MaterialTheme.typography.titleLarge)
                Text(if (showingOther) "${shown.spanishLabel} · otra versión" else shown.spanishLabel, style = MaterialTheme.typography.bodySmall)
            }
            TextButton(onClick = { showingOther = !showingOther }) {
                Text(if (showingOther) "Volver a ${campaignVersion.edition}" else "Ver en ${campaignVersion.other.edition}")
            }
        }
        card.sections(shown).forEach { section ->
            HorizontalDivider()
            Text("${section.heading} · p. ${section.page}", style = MaterialTheme.typography.titleSmall)
            SrdParagraphs(section.text)
        }
        Text(reference.attribution(shown), style = MaterialTheme.typography.labelSmall)
    }
}

/** «Pregunta una regla»: offline word search over both SRDs (P4); the AI answer is package 8. */
@Composable
internal fun RulesPanel(campaignId: Uuid, onOpenCard: (String) -> Unit) {
    val platform = LocalDmPlatform.current
    var query by rememberSaveable { mutableStateOf("") }
    var searched by rememberSaveable { mutableStateOf("") }
    var corpora by remember { mutableStateOf<List<SrdCorpus>?>(null) }
    var quick by remember { mutableStateOf<SrdQuickReference?>(null) }
    var hits by remember { mutableStateOf<List<SrdSearchHit>>(emptyList()) }
    var answer by remember { mutableStateOf<String?>(null) }
    var asking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val campaignVersion = remember(campaignId) { DmScreenPreferences(platform.store).campaignVersion(campaignId) }
    LaunchedEffect(Unit) {
        quick = SrdAssets.quickReference(platform)
        corpora = SrdAssets.corpora(platform)
    }
    LaunchedEffect(searched, corpora) {
        val loaded = corpora ?: return@LaunchedEffect
        answer = null
        // The campaign's version first (P4); the other one stays one tap away below.
        hits = if (searched.isBlank()) emptyList() else withContext(Dispatchers.Default) {
            searchSrd(loaded, searched).sortedBy { it.version != campaignVersion }
        }
    }
    val cited = hits.take(RULES_PASSAGES_FOR_AI)
    LazyColumn(contentPadding = PaddingValues(vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DmTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Pregunta una regla") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.weight(1f),
                )
                Button(onClick = { searched = query.trim() }, enabled = corpora != null && query.isNotBlank()) { Text(if (corpora == null) "…" else "Buscar") }
            }
        }
        item { Text("Busca palabras en el SRD de la App, sin internet. Cada resultado cita versión y página.", style = MaterialTheme.typography.bodySmall) }
        if (searched.isBlank()) {
            quick?.let { reference ->
                item { Text("Referencia rápida", style = MaterialTheme.typography.titleSmall) }
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        reference.cards.forEach { card -> TextButton(onClick = { onOpenCard(card.id) }) { Text(card.title) } }
                    }
                }
            }
        } else if (corpora != null && hits.isEmpty()) {
            item { Text("No encontré «$searched» en el SRD. Prueba con otras palabras.") }
        } else if (hits.isNotEmpty() && platform.rulesAiAvailable) {
            // Package 8: the AI answers only from the passages below and cites them [1], [2]… (P4).
            item {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(enabled = !asking, onClick = {
                        asking = true
                        answer = null
                        scope.launch {
                            answer = runCatching {
                                platform.answerRules(
                                    searched,
                                    cited.map { HostedRulesPassage(it.version.key, it.page, it.heading.orEmpty(), it.paragraph) },
                                )
                            }.getOrElse { "La respuesta con IA no está disponible ahora. Los fragmentos del SRD de abajo siguen sirviendo." }
                            asking = false
                        }
                    }) { Text(if (asking) "Preguntando…" else "Responder con IA") }
                    Text("Usa solo los fragmentos [1]–[${cited.size}].", style = MaterialTheme.typography.bodySmall)
                }
            }
            answer?.let { text ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("Respuesta (IA) · revisa el texto citado", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Text(text, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
        items(hits.withIndex().toList()) { (i, hit) ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        listOfNotNull(if (i < cited.size) "[${i + 1}]" else null, hit.version.spanishLabel, "p. ${hit.page}", hit.heading).joinToString(" · "),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(hit.paragraph, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

/** How many SRD passages travel with a rules question (the Worker accepts up to 8). */
private const val RULES_PASSAGES_FOR_AI = 6

@Composable
internal fun SrdParagraphs(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        text.split('\n').filter { it.isNotBlank() }.forEach { paragraph ->
            val lead = paragraph.substringBefore(". ", missingDelimiterValue = "")
            if (lead.isNotEmpty() && lead.length <= 40 && lead.first().isUpperCase()) {
                Text(buildAnnotatedString {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold)); append("$lead."); pop()
                    append(" "); append(paragraph.substringAfter(". "))
                }, style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(paragraph, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
