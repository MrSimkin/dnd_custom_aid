package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryContent
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind
import io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryPayload
import io.github.mrsimkin.dndcustomaid.shared.content.RulesOrigin
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.RulesOption
import io.github.mrsimkin.dndcustomaid.shared.dm.SRD_CATALOG_FILES
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdCatalog
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdCorpus
import io.github.mrsimkin.dndcustomaid.shared.dm.SrdLayout
import io.github.mrsimkin.dndcustomaid.shared.dm.parseSrdCatalog
import io.github.mrsimkin.dndcustomaid.shared.dm.rulesOptions
import io.github.mrsimkin.dndcustomaid.shared.dm.searchSrd
import io.github.mrsimkin.dndcustomaid.shared.spine.RevisionDecision
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.Uuid

/*
 * «Reglas» (redesign R4, owner observation G4): the SRD to read, in lists and as full text, and
 * the DM's homebrew next to it as a separate origin (D13). The same screen on the tablet and on
 * Windows. Every homebrew family has its place even while empty («Aquí va…»).
 */

/** Loads the SRD catalogs once per process, off the main thread. */
internal object SrdCatalogAssets {
    private var catalogs: List<SrdCatalog>? = null

    suspend fun catalogs(platform: DmPlatform): List<SrdCatalog> = catalogs ?: withContext(Dispatchers.Default) {
        SRD_CATALOG_FILES.map { parseSrdCatalog(platform.readSrd(it)) }.also { catalogs = it }
    }
}

/** What each homebrew family is for, shown while it is still empty. */
internal fun RulesEntryKind.placeholder(): String = when (this) {
    RulesEntryKind.SPECIES -> "Aquí van tus razas propias, como el genasi de agua con tus reglas."
    RulesEntryKind.SUBSPECIES -> "Aquí van las subrazas y semirrazas: una raza principal con rasgos de otra."
    RulesEntryKind.CLASS -> "Aquí van tus clases propias o adaptadas."
    RulesEntryKind.SUBCLASS -> "Aquí van subclases, dominios, patrones y sendas: de tu manual o adaptadas."
    RulesEntryKind.BACKGROUND -> "Aquí van tus trasfondos, como Demonólogo / celestólogo."
    RulesEntryKind.FEAT -> "Aquí van tus dotes y dones."
    RulesEntryKind.SPELL -> "Aquí van los conjuros de tu Tomo de Magia que no están en el SRD."
    RulesEntryKind.WEAPON -> "Aquí van tus armas propias."
    RulesEntryKind.ARMOR -> "Aquí van tus armaduras propias."
    RulesEntryKind.ITEM -> "Aquí van objetos comunes y equipo propio."
    RulesEntryKind.MAGIC_ITEM -> "Aquí van tus objetos mágicos."
    RulesEntryKind.LEGENDARY_ITEM -> "Aquí van las armas y objetos de leyenda, con sus poderes y rituales de despertar."
    RulesEntryKind.DEITY -> "Aquí van tus dioses, iglesias y dominios."
    RulesEntryKind.RULE -> "Aquí van reglas sueltas de tu manual."
}

private enum class RulesTab(val label: String) {
    SRD_5E("SRD 5e"),
    SRD_55E("SRD 5.5e"),
    HOMEBREW("Homebrew"),
    TEXT("Texto completo del SRD"),
}

/** The kinds the SRD catalogs bring, in the order a player builds a character. */
private val SRD_KINDS = listOf(
    RulesEntryKind.SPECIES, RulesEntryKind.SUBSPECIES, RulesEntryKind.CLASS, RulesEntryKind.SUBCLASS,
    RulesEntryKind.BACKGROUND, RulesEntryKind.FEAT, RulesEntryKind.SPELL, RulesEntryKind.WEAPON, RulesEntryKind.ARMOR,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RulesLibraryScreen(
    platform: DmPlatform,
    database: AppDatabase,
    /** The DM's account: homebrew is kept in their personal library, for every campaign. */
    ownerAccountId: Uuid?,
    /** Without an account, homebrew is kept in the open campaign. */
    campaignId: Uuid?,
    onBack: (() -> Unit)? = null,
    /** The account's e-mail, so the DM knows whose personal library is shown. */
    ownerLabel: String? = null,
    modifier: Modifier = Modifier,
) {
    var tab by remember { mutableStateOf(RulesTab.SRD_55E) }
    var catalogs by remember { mutableStateOf<List<SrdCatalog>?>(null) }
    var corpora by remember { mutableStateOf<List<SrdCorpus>?>(null) }
    var layouts by remember { mutableStateOf<List<SrdLayout>>(emptyList()) }
    LaunchedEffect(Unit) { catalogs = SrdCatalogAssets.catalogs(platform) }
    LaunchedEffect(Unit) { layouts = SrdLayoutAssets.layouts(platform) }
    LaunchedEffect(tab) { if (tab == RulesTab.TEXT && corpora == null) corpora = SrdAssets.corpora(platform) }

    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DmScreenHeader(
            title = "Reglas",
            subtitle = "El SRD para leer y tu homebrew, en dos orígenes separados.",
            onBack = onBack,
            help = "«SRD 5e» y «SRD 5.5e» muestran las reglas oficiales gratuitas por tipo: razas, clases, conjuros, armas… " +
                "«Homebrew» es tu biblioteca: lo que agregues aquí aparece después del SRD cuando eliges algo para un PJ. " +
                "«Texto completo del SRD» busca en todo el documento y lo lee página a página.",
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            RulesTab.entries.forEach { candidate ->
                FilterChip(selected = tab == candidate, onClick = { tab = candidate }, label = { Text(candidate.label) }, modifier = Modifier.handCursor())
            }
        }
        HorizontalDivider()
        val loaded = catalogs
        when (tab) {
            RulesTab.SRD_5E, RulesTab.SRD_55E -> if (loaded == null) {
                Text("Cargando el SRD…")
            } else {
                val catalog = loaded.first { it.edition == if (tab == RulesTab.SRD_5E) "5e" else "5.5e" }
                SrdBrowser(catalog, layouts.firstOrNull { it.edition == catalog.edition })
            }
            RulesTab.HOMEBREW -> HomebrewLibrary(database, ownerAccountId, campaignId, ownerLabel)
            RulesTab.TEXT -> SrdTextReader(corpora, layouts)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SrdBrowser(catalog: SrdCatalog, layout: SrdLayout?) {
    var kind by remember(catalog.version) { mutableStateOf(RulesEntryKind.CLASS) }
    var query by remember { mutableStateOf("") }
    var selected by remember(catalog.version, kind) { mutableStateOf<RulesOption?>(null) }
    val options = remember(catalog.version, kind, query) { rulesOptions(kind, listOf(catalog), emptyList(), query = query) }

    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SRD_KINDS.filter { catalog.of(it).isNotEmpty() }.forEach { candidate ->
            FilterChip(
                selected = kind == candidate,
                onClick = { kind = candidate },
                label = { Text("${candidate.plural} (${catalog.of(candidate).size})") },
                modifier = Modifier.handCursor(),
            )
        }
    }
    OptionsAndDetail(
        options = options, query = query, onQuery = { query = it }, selected = selected, onSelect = { selected = it },
        formatted = { option -> layout?.entry(option.kind.name, option.name) },
    )
    Text(catalog.attribution, style = MaterialTheme.typography.labelSmall)
}

@Composable
private fun OptionsAndDetail(
    options: List<RulesOption>,
    query: String,
    onQuery: (String) -> Unit,
    selected: RulesOption?,
    onSelect: (RulesOption) -> Unit,
    /** The SRD entry with its layout (headings, tables), when there is one. */
    formatted: (RulesOption) -> String? = { null },
) {
    Row(Modifier.fillMaxWidth().heightIn(min = 300.dp, max = 2000.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.width(300.dp).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            DmTextField(value = query, onValueChange = onQuery, label = { Text("Buscar") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(options, key = { "${it.origin}-${it.edition}-${it.name}" }) { option ->
                    val isSelected = option == selected
                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth().handCursor().clickable { onSelect(option) },
                    ) {
                        Column(Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(option.name, fontWeight = FontWeight.SemiBold)
                            val line = listOfNotNull(
                                option.parent.takeIf { it.isNotBlank() },
                                option.fields["nivel"]?.let { if (it == "0") "truco" else "nivel $it" },
                                option.fields["escuela"],
                                option.fields["dano"],
                                option.fields["ca"]?.let { "CA $it" },
                            ).joinToString(" · ")
                            if (line.isNotBlank()) Text(line, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        Card(Modifier.weight(1f).fillMaxHeight()) {
            Column(Modifier.fillMaxSize().dmScroll().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (selected == null) {
                    Text("Elige algo de la lista para leerlo completo.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Text(selected.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${selected.origin.label} · ${selected.source}", style = MaterialTheme.typography.labelMedium)
                    // Agent check of 0.7.0-preqa.4: a spell's formatted text repeats these data line by
                    // line, so its plain list is shown only without it; labels keep their accents.
                    val md = formatted(selected)
                    if (md == null || selected.kind != io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.SPELL) {
                        selected.fields.filterValues { it.isNotBlank() }.forEach { (key, value) ->
                            Text("${srdFieldLabel(key)}: $value", style = MaterialTheme.typography.bodyMedium)
                        }
                        HorizontalDivider()
                    }
                    if (md != null) SrdMarkdown(md) else Text(selected.body.ifBlank { selected.summary }, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SrdTextReader(corpora: List<SrdCorpus>?, layouts: List<SrdLayout>) {
    if (corpora == null) {
        Text("Cargando el texto del SRD…")
        return
    }
    var edition by remember { mutableStateOf(1) }
    var page by remember(edition) { mutableStateOf(1) }
    var query by remember { mutableStateOf("") }
    val corpus = corpora[edition]
    val hits = remember(query, edition) { if (query.length >= 3) searchSrd(listOf(corpus), query, limit = 40) else emptyList() }
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        corpora.forEachIndexed { index, c ->
            FilterChip(selected = edition == index, onClick = { edition = index }, label = { Text("SRD ${c.version}") }, modifier = Modifier.handCursor())
        }
    }
    Row(Modifier.fillMaxWidth().heightIn(min = 300.dp, max = 2000.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(Modifier.width(300.dp).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            DmTextField(value = query, onValueChange = { query = it }, label = { Text("Buscar en el SRD") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (query.length in 1..2) Text("Escribe al menos 3 letras.", style = MaterialTheme.typography.bodySmall)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                items(hits) { hit ->
                    Surface(shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth().handCursor().clickable { page = hit.page }) {
                        Column(Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(listOfNotNull("Página ${hit.page}", hit.heading).joinToString(" · "), fontWeight = FontWeight.SemiBold)
                            Text(hit.paragraph, style = MaterialTheme.typography.bodySmall, maxLines = 3)
                        }
                    }
                }
            }
        }
        Card(Modifier.weight(1f).fillMaxHeight()) {
            Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { page = (page - 1).coerceAtLeast(1) }, modifier = Modifier.handCursor()) { Text("◀") }
                    Text("Página $page de ${corpus.pages.size}", fontWeight = FontWeight.SemiBold)
                    OutlinedButton(onClick = { page = (page + 1).coerceAtMost(corpus.pages.size) }, modifier = Modifier.handCursor()) { Text("▶") }
                }
                Box(Modifier.weight(1f).dmScroll()) {
                    val md = layouts.firstOrNull { it.version == corpus.version }?.page(page)
                    if (md != null) SrdMarkdown(md) else Text(corpus.pages.firstOrNull { it.page == page }?.text.orEmpty(), style = MaterialTheme.typography.bodyMedium)
                }
                Text(corpus.attribution, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HomebrewLibrary(database: AppDatabase, ownerAccountId: Uuid?, campaignId: Uuid?, ownerLabel: String?) {
    val repository = remember(database) { RulesEntryContentRepository(database) }
    var version by remember { mutableStateOf(0) }
    val entries = remember(version, ownerAccountId, campaignId) {
        val personal = ownerAccountId?.let { runCatching { repository.listPersonal(it) }.getOrDefault(emptyList()) }.orEmpty()
        val campaign = campaignId?.let { repository.listCampaign(it) }.orEmpty()
        (personal + campaign).distinctBy { it.item.identity.id }
    }
    var kind by remember { mutableStateOf<RulesEntryKind?>(null) }
    var editing by remember { mutableStateOf<RulesEntryContent?>(null) }
    var creating by remember { mutableStateOf(false) }
    val chosen = kind

    if (chosen == null) {
        Text(
            when {
                ownerAccountId != null && ownerLabel != null -> "Biblioteca personal de $ownerLabel: sirve en todas sus campañas, más el homebrew de la campaña abierta."
                ownerAccountId != null -> "Tu biblioteca personal: sirve en todas tus campañas, más el homebrew de la campaña abierta."
                else -> "Sin cuenta, tu homebrew se guarda en la campaña abierta."
            },
            style = MaterialTheme.typography.bodySmall,
        )
        if (entries.isEmpty()) {
            Text(
                "Todavía no hay homebrew aquí. Un paquete cargado con otra cuenta deja su homebrew en esa cuenta: entra con ella y sincroniza para verlo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            RulesEntryKind.entries.forEach { candidate ->
                val count = entries.count { it.payload.kind == candidate }
                Card(Modifier.width(240.dp).heightIn(min = 96.dp).handCursor().clickable { kind = candidate }) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${candidate.plural} · $count", fontWeight = FontWeight.Bold)
                        Text(if (count == 0) candidate.placeholder() else entries.filter { it.payload.kind == candidate }.take(3).joinToString(", ") { it.item.displayName }, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        return
    }

    val current = editing
    if (creating || current != null) {
        HomebrewEditor(
            kind = chosen,
            existing = current,
            canSave = ownerAccountId != null || campaignId != null,
            onCancel = { creating = false; editing = null },
            onSave = { name, payload ->
                val now = nowEpochSeconds()
                if (current == null) {
                    if (ownerAccountId != null) {
                        runCatching { repository.createPersonal(ownerAccountId, name, payload, now) }
                            .recoverCatching { campaignId?.let { repository.createCampaign(it, name, payload, now) } ?: throw it }
                    } else if (campaignId != null) {
                        repository.createCampaign(campaignId, name, payload, now)
                    }
                } else {
                    repository.update(current.item.identity.id, current.item.identity.revision, name, payload, now) is RevisionDecision.Accepted
                }
                creating = false
                editing = null
                version++
            },
        )
        return
    }

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TextButton(onClick = { kind = null }, modifier = Modifier.handCursor()) { Text("← Homebrew") }
        Text(chosen.plural, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Button(onClick = { creating = true }, modifier = Modifier.handCursor()) { Text("＋ Nuevo") }
    }
    val own = entries.filter { it.payload.kind == chosen }
    if (own.isEmpty()) {
        Text(chosen.placeholder(), style = MaterialTheme.typography.bodyMedium)
        return
    }
    var selected by remember(chosen) { mutableStateOf<RulesOption?>(null) }
    var query by remember(chosen) { mutableStateOf("") }
    val options = rulesOptions(chosen, emptyList(), own, query = query)
    OptionsAndDetail(options = options, query = query, onQuery = { query = it }, selected = selected, onSelect = { selected = it })
    selected?.let { option ->
        own.firstOrNull { it.item.displayName == option.name }?.let { entry ->
            TextButton(onClick = { editing = entry }, modifier = Modifier.handCursor()) { Text("Editar «${entry.item.displayName}»") }
        }
    }
}

@Composable
private fun HomebrewEditor(
    kind: RulesEntryKind,
    existing: RulesEntryContent?,
    canSave: Boolean,
    onCancel: () -> Unit,
    onSave: (String, RulesEntryPayload) -> Unit,
) {
    var name by remember { mutableStateOf(existing?.item?.displayName.orEmpty()) }
    var edition by remember { mutableStateOf(existing?.payload?.edition.orEmpty()) }
    var parent by remember { mutableStateOf(existing?.payload?.parent.orEmpty()) }
    var summary by remember { mutableStateOf(existing?.payload?.summary.orEmpty()) }
    var body by remember { mutableStateOf(existing?.payload?.body.orEmpty()) }
    var fields by remember { mutableStateOf(existing?.payload?.fields.orEmpty().entries.joinToString("\n") { "${it.key}: ${it.value}" }) }
    var source by remember { mutableStateOf(existing?.payload?.source.orEmpty()) }

    Column(Modifier.fillMaxWidth().dmScroll(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(if (existing == null) "${kind.label} nueva" else "Editar ${kind.label.lowercase()}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        DmTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("" to "Ambas ediciones", "5e" to "5e", "5.5e" to "5.5e").forEach { (value, label) ->
                FilterChip(selected = edition == value, onClick = { edition = value }, label = { Text(label) })
            }
        }
        if (kind == RulesEntryKind.SUBCLASS || kind == RulesEntryKind.SUBSPECIES) {
            DmTextField(
                value = parent,
                onValueChange = { parent = it },
                label = { Text(if (kind == RulesEntryKind.SUBCLASS) "Clase a la que pertenece" else "Raza principal") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        DmTextField(value = summary, onValueChange = { summary = it }, label = { Text("En una línea") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        DmTextField(value = body, onValueChange = { body = it }, label = { Text("Texto completo") }, minLines = 6, modifier = Modifier.fillMaxWidth())
        DmTextField(
            value = fields,
            onValueChange = { fields = it },
            label = { Text("Datos (uno por línea, «nombre: valor», p. ej. «daño: 1d8 contundente»)") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )
        DmTextField(value = source, onValueChange = { source = it }, label = { Text("De dónde viene (manual, página)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        if (!canSave) Text("Entra con tu cuenta o abre una campaña para guardar homebrew.", color = MaterialTheme.colorScheme.error)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                enabled = canSave && name.isNotBlank(),
                onClick = {
                    val parsed = fields.lines().mapNotNull { line ->
                        val (key, value) = line.split(":", limit = 2).takeIf { it.size == 2 } ?: return@mapNotNull null
                        key.trim().lowercase().replace(' ', '_').takeIf { it.isNotEmpty() }?.let { it to value.trim() }
                    }.toMap()
                    onSave(name.trim(), RulesEntryPayload(kind, edition, parent.trim(), summary.trim(), body.trim(), parsed, source.trim()))
                },
                modifier = Modifier.handCursor(),
            ) { Text("Guardar") }
            TextButton(onClick = onCancel, modifier = Modifier.handCursor()) { Text("Cancelar") }
        }
    }
}

/** The SRD data keys as the reader sees them, with their accents (agent check of 0.7.0-preqa.4). */
private val SRD_FIELD_LABELS = mapOf(
    "alcance" to "Alcance", "ca" to "CA", "categoria" to "Categoría", "clases" to "Clases",
    "componentes" to "Componentes", "dado_de_golpe" to "Dado de golpe", "dano" to "Daño",
    "duracion" to "Duración", "escuela" to "Escuela", "fuerza" to "Fuerza", "maestria" to "Maestría",
    "nivel" to "Nivel", "peso" to "Peso", "precio" to "Precio", "propiedades" to "Propiedades",
    "ritual" to "Ritual", "sigilo" to "Sigilo", "tiempo" to "Tiempo de lanzamiento",
)

internal fun srdFieldLabel(key: String): String =
    SRD_FIELD_LABELS[key] ?: key.replace('_', ' ').replaceFirstChar { it.uppercase() }
