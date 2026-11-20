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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.content.CampaignSearch
import io.github.mrsimkin.dndcustomaid.shared.content.CampaignSearchDetail
import io.github.mrsimkin.dndcustomaid.shared.content.CampaignSearchResult
import io.github.mrsimkin.dndcustomaid.shared.content.DEFAULT_SEARCH_FAMILIES
import io.github.mrsimkin.dndcustomaid.shared.content.SEARCHABLE_FAMILIES
import io.github.mrsimkin.dndcustomaid.shared.content.spanishFamilyLabel
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.SceneIndex
import kotlin.uuid.Uuid

/*
 * «🔎 Buscar» on the tablet (owner's I, 2026-11-20). Until now the tablet could only search inside
 * the Mesa de Escena, so a creature, an encounter, a handout, a note or a house rule could only be
 * found by remembering which desk held it.
 *
 * This is the same search as the PC («resultados resumidos con origen», owner 2026-11-18): each
 * result says what it is, one line of what it says and where it lives, and «Qué se busca» leaves
 * families out — the house rules are out by default, because there are hundreds of them.
 *
 * The PC jumps to the result's section of «Preparar». The tablet has no «Preparar», so it reads the
 * result here, and what the Mesa de Escena can hold («PNJ», «Lugar», «Zona», «Escena») also has
 * «Abrir en la Mesa de Escena».
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SearchDesk(
    campaign: Campaign,
    database: AppDatabase,
    version: Int,
    onOpenInSceneDesk: (Uuid) -> Unit,
) {
    val index = remember(campaign.id, version) { SceneIndex.load(database, campaign.id) }
    val search = remember(database) { CampaignSearch(database) }
    val details = remember(database) { CampaignSearchDetail(database) }
    var query by remember(campaign.id) { mutableStateOf("") }
    var families by remember(campaign.id) { mutableStateOf(DEFAULT_SEARCH_FAMILIES) }
    var showFilters by remember(campaign.id) { mutableStateOf(false) }
    var openResult by remember(campaign.id) { mutableStateOf<CampaignSearchResult?>(null) }
    val results = remember(query, families, campaign.id, version) {
        if (query.trim().length < 2) emptyList() else search.search(campaign.id, query, families)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).fillMaxHeight().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            DmTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar en toda la campaña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { showFilters = !showFilters }) {
                    Text(if (showFilters) "▾ Qué se busca" else "▸ Qué se busca")
                }
                if (query.trim().length >= 2) {
                    Text(
                        if (results.isEmpty()) "Nada coincide." else "${results.size} resultado(s)",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            if (showFilters) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    SEARCHABLE_FAMILIES.forEach { family ->
                        FilterChip(
                            selected = family in families,
                            onClick = { families = if (family in families) families - family else families + family },
                            label = { Text(spanishFamilyLabel(family)) },
                        )
                    }
                }
            }
            if (query.trim().length < 2) {
                Text(
                    "Escribe al menos dos letras. Busca por nombre y por lo que dice la ficha.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(results, key = { it.id.toString() }) { result ->
                    Card(
                        Modifier.fillMaxWidth().clickable { openResult = result },
                    ) {
                        Column(Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.extraSmall,
                                ) {
                                    Text(
                                        spanishFamilyLabel(result.family),
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    )
                                }
                                Text(result.name, fontWeight = FontWeight.SemiBold, maxLines = 1)
                                if (result.origin.isNotBlank()) {
                                    Text("· en ${result.origin}", style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                }
                            }
                            if (result.summary.isNotBlank()) {
                                Text(result.summary, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                            }
                        }
                    }
                }
            }
        }
        Surface(tonalElevation = 1.dp, modifier = Modifier.weight(1.4f).fillMaxHeight()) {
            val shown = openResult
            if (shown == null) {
                Box(Modifier.fillMaxSize()) {
                    Text(
                        "Toca un resultado para leerlo aquí.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(24.dp),
                    )
                }
            } else {
                val lines = remember(shown.id, version) { details.lines(shown.id, shown.family) }
                Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(shown.name, style = MaterialTheme.typography.titleLarge)
                    Text(
                        spanishFamilyLabel(shown.family) + shown.origin.takeIf { it.isNotBlank() }?.let { " · en $it" }.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                    if (index.kindOf(shown.id) != null) {
                        Button(onClick = { onOpenInSceneDesk(shown.id) }) { Text("Abrir en la Mesa de Escena") }
                    }
                    if (lines.isEmpty()) {
                        Text("Esta ficha todavía no tiene nada escrito.", style = MaterialTheme.typography.bodyMedium)
                    }
                    lines.forEach { line ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(line.label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                            Text(line.text, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
