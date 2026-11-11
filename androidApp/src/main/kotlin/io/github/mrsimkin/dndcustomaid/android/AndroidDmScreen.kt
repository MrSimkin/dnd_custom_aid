package io.github.mrsimkin.dndcustomaid.android

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.PartyLens
import io.github.mrsimkin.dndcustomaid.shared.dm.PartyLensMember
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
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

/*
 * Base para DM, package 3: Pantalla del DM.
 *
 * Hojas de PJ, Vista Grupo (P9), Referencia rápida (P10) and Consultar reglas. Consultar reglas
 * searches the in-App SRD offline (P4); the AI answer with citation is package 8.
 */

/** Loads the SRD assets once per process; parsing 3 MB of JSON stays off the main thread. */
internal object SrdAssets {
    @Volatile private var quick: SrdQuickReference? = null
    @Volatile private var corpora: List<SrdCorpus>? = null

    suspend fun quickReference(context: Context): SrdQuickReference = quick ?: withContext(Dispatchers.IO) {
        parseSrdQuickReference(read(context, "srd/quick_reference_es.json")).also { quick = it }
    }

    suspend fun corpora(context: Context): List<SrdCorpus> = corpora ?: withContext(Dispatchers.IO) {
        listOf("srd/srd_5_1_es.json", "srd/srd_5_2_1_es.json").map { parseSrdCorpus(read(context, it)) }.also { corpora = it }
    }

    private fun read(context: Context, path: String): String =
        context.assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
}

/** The campaign's rules version for the Referencia rápida, remembered per campaign on this device. */
internal class DmScreenPreferences(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("dnd_custom_aid_dm_screen", Context.MODE_PRIVATE)

    fun campaignVersion(campaignId: Uuid): SrdVersion =
        SrdVersion.fromKey(prefs.getString("srd_version_$campaignId", null)) ?: SrdVersion.SRD_5_2_1

    fun setCampaignVersion(campaignId: Uuid, version: SrdVersion) {
        prefs.edit().putString("srd_version_$campaignId", version.key).apply()
    }
}

private enum class DmScreenTab(val label: String) {
    SHEETS("Hojas de PJ"),
    PARTY("Vista Grupo"),
    QUICK("Referencia rápida"),
    RULES("Consultar reglas"),
}

@Composable
internal fun DmScreen(
    campaign: Campaign,
    database: AppDatabase,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCharacter: (CharacterSheet) -> Unit,
) {
    var tabName by rememberSaveable { mutableStateOf(DmScreenTab.SHEETS.name) }
    val tab = DmScreenTab.valueOf(tabName)
    val characters = remember(campaign.id) { CharacterRepository(database).listCharacters(campaign.id) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
            ) {
                StableBackIconButton(onClick = onBack, contentDescription = "Volver al Escritorio DM")
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pantalla del DM", style = MaterialTheme.typography.headlineMedium)
                    Text(campaign.name, style = MaterialTheme.typography.titleMedium)
                }
                StableSettingsIconButton(onClick = onOpenSettings)
            }
            PrimaryTabRow(selectedTabIndex = tab.ordinal) {
                DmScreenTab.entries.forEach { entry ->
                    Tab(
                        selected = entry == tab,
                        onClick = { tabName = entry.name },
                        text = { Text(entry.label) },
                    )
                }
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Box(modifier = Modifier.widthIn(max = 1100.dp).fillMaxSize().padding(horizontal = 14.dp)) {
                    when (tab) {
                        DmScreenTab.SHEETS -> PcSheetsTab(characters, onOpenCharacter)
                        DmScreenTab.PARTY -> PartyLensTab(database, characters)
                        DmScreenTab.QUICK -> QuickReferenceTab(campaign.id)
                        DmScreenTab.RULES -> RulesSearchTab()
                    }
                }
            }
        }
    }
}

@Composable
private fun PcSheetsTab(characters: List<CharacterSheet>, onOpenCharacter: (CharacterSheet) -> Unit) {
    if (characters.isEmpty()) {
        EmptyNote("Esta campaña aún no tiene PJ en este aparato. Llegan al sincronizar.")
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
    ) {
        items(characters, key = { it.id.toString() }) { character ->
            Card(onClick = { onOpenCharacter(character) }, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(12.dp)),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(character.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = character.classes.joinToString(" / ") { "${it.name} ${it.level}" }.ifBlank { "Sin clase registrada" },
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text("CA ${character.armorClass}", style = MaterialTheme.typography.titleSmall)
                    Text("PV máx. ${character.maxHp}", style = MaterialTheme.typography.titleSmall)
                    TextButton(onClick = { onOpenCharacter(character) }) { Text("Abrir hoja") }
                }
            }
        }
    }
}

@Composable
private fun PartyLensTab(database: AppDatabase, characters: List<CharacterSheet>) {
    val lens: PartyLens = remember(characters) {
        val closures = CharacterClosureRepository(database)
        partyLens(
            members = characters.map { sheet ->
                PartyLensMember(sheet, runCatching { closures.state(sheet.id).senses }.getOrDefault(emptyList()))
            },
            formatFeet = ::formatCharacterDistanceFeetV4,
        )
    }
    if (lens.columns.isEmpty()) {
        EmptyNote("Sin PJ en esta campaña todavía.")
        return
    }
    val labelWidth = 170.dp
    val columnWidth = 150.dp
    Column(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .horizontalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
    ) {
        Row {
            Box(modifier = Modifier.width(labelWidth))
            lens.columns.forEach { column ->
                Text(
                    text = column.name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.width(columnWidth).padding(horizontal = 6.dp),
                )
            }
        }
        HorizontalDivider()
        lens.rows.forEach { row ->
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = row.kind.spanishLabel(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(labelWidth).padding(vertical = 6.dp),
                )
                row.cells.forEach { cell ->
                    val highlight = if (cell.best) {
                        Modifier.background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small)
                    } else {
                        Modifier
                    }
                    Text(
                        text = cell.text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (cell.best) FontWeight.Bold else FontWeight.Normal,
                        color = if (cell.best) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .width(columnWidth)
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                            .then(highlight)
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                    )
                }
            }
            HorizontalDivider()
        }
        Text(
            text = "Datos fijos de cada hoja. Resaltado: el mejor valor de la fila.",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun QuickReferenceTab(campaignId: Uuid) {
    val context = LocalContext.current
    val preferences = remember { DmScreenPreferences(context) }
    var quick by remember { mutableStateOf<SrdQuickReference?>(null) }
    var campaignVersion by remember(campaignId) { mutableStateOf(preferences.campaignVersion(campaignId)) }
    var selectedId by rememberSaveable { mutableStateOf("estados") }
    var showingOther by remember(selectedId) { mutableStateOf(false) }

    LaunchedEffect(Unit) { quick = SrdAssets.quickReference(context) }
    val reference = quick ?: run {
        EmptyNote("Cargando el SRD…")
        return
    }
    val card = reference.cards.firstOrNull { it.id == selectedId } ?: reference.cards.first()
    val shown = if (showingOther) campaignVersion.other else campaignVersion

    LazyColumn(
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            ) {
                Text("Versión de la campaña:", style = MaterialTheme.typography.bodyMedium)
                SrdVersion.entries.forEach { version ->
                    FilterChip(
                        selected = campaignVersion == version,
                        onClick = {
                            campaignVersion = version
                            preferences.setCampaignVersion(campaignId, version)
                            showingOther = false
                        },
                        label = { Text(version.spanishLabel) },
                    )
                }
            }
        }
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
            ) {
                reference.cards.forEach { entry ->
                    FilterChip(
                        selected = entry.id == card.id,
                        onClick = { selectedId = entry.id },
                        label = { Text(entry.title) },
                    )
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(card.title, style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = if (showingOther) "${shown.spanishLabel} · otra versión" else shown.spanishLabel,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        TextButton(onClick = { showingOther = !showingOther }) {
                            Text(if (showingOther) "Volver a ${campaignVersion.edition}" else "Ver en ${campaignVersion.other.edition}")
                        }
                    }
                    card.sections(shown).forEach { section ->
                        HorizontalDivider()
                        Text(
                            text = "${section.heading} · p. ${section.page}",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        SrdParagraphs(section.text)
                    }
                }
            }
        }
        item {
            Text(
                text = reference.attribution(shown),
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun RulesSearchTab() {
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var searched by rememberSaveable { mutableStateOf("") }
    var corpora by remember { mutableStateOf<List<SrdCorpus>?>(null) }
    var hits by remember { mutableStateOf<List<SrdSearchHit>>(emptyList()) }

    LaunchedEffect(Unit) { corpora = SrdAssets.corpora(context) }
    LaunchedEffect(searched, corpora) {
        val loaded = corpora ?: return@LaunchedEffect
        hits = if (searched.isBlank()) emptyList() else withContext(Dispatchers.Default) { searchSrd(loaded, searched) }
    }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
    ) {
        item {
            Text(
                text = "Busca palabras en el SRD de la App, sin internet. Cada resultado cita su versión y su página.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            ) {
                CharacterCompactOutlinedTextFieldV4(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Qué regla buscas") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.weight(1f),
                )
                Button(onClick = { searched = query.trim() }, enabled = corpora != null && query.isNotBlank()) {
                    Text(if (corpora == null) "Cargando…" else "Buscar")
                }
            }
        }
        if (searched.isNotBlank() && corpora != null && hits.isEmpty()) {
            item { EmptyNote("No encontré «$searched» en el SRD. Prueba con otras palabras.") }
        }
        items(hits) { hit ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp)),
                ) {
                    Text(
                        text = listOfNotNull(hit.version.spanishLabel, "p. ${hit.page}", hit.heading).joinToString(" · "),
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(hit.paragraph, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SrdParagraphs(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(appSpacingV4(4.dp))) {
        text.split('\n').filter { it.isNotBlank() }.forEach { paragraph ->
            val lead = paragraph.substringBefore(". ", missingDelimiterValue = "")
            // SRD paragraphs often open with a bold term ("Velocidad 0. Tu velocidad…"); keep that cue.
            if (lead.isNotEmpty() && lead.length <= 40 && lead.first().isUpperCase()) {
                Text(
                    text = androidx.compose.ui.text.buildAnnotatedString {
                        pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
                        append("$lead.")
                        pop()
                        append(" ")
                        append(paragraph.substringAfter(". "))
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                Text(paragraph, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun EmptyNote(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 16.dp))
}
