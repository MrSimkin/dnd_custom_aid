package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import io.github.mrsimkin.dndcustomaid.shared.content.CampaignSearchResult
import io.github.mrsimkin.dndcustomaid.shared.content.DEFAULT_SEARCH_FAMILIES
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.content.SEARCHABLE_FAMILIES
import io.github.mrsimkin.dndcustomaid.shared.content.spanishFamilyLabel
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase

/**
 * One search for the whole campaign (owner, 2026-11-18, «K»). Each result says what it is, one
 * line of what it says and where it lives, and «Qué se busca» leaves families out — the house
 * rules are out by default, because there are hundreds of them.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DesktopCampaignSearchBar(
    database: AppDatabase,
    campaign: Campaign,
    onOpen: (CampaignSearchResult) -> Unit,
) {
    var query by remember(campaign.id) { mutableStateOf("") }
    var families by remember(campaign.id) { mutableStateOf(DEFAULT_SEARCH_FAMILIES) }
    var showFilters by remember(campaign.id) { mutableStateOf(false) }
    val search = remember(database) { CampaignSearch(database) }
    val results = remember(query, families, campaign.id) {
        if (query.trim().length < 2) emptyList() else search.search(campaign.id, query, families)
    }

    Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(6.dp))) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Buscar en toda la campaña") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
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
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SEARCHABLE_FAMILIES.forEach { family ->
                    FilterChip(
                        selected = family in families,
                        onClick = { families = if (family in families) families - family else families + family },
                        label = { Text(spanishFamilyLabel(family)) },
                    )
                }
            }
        }
        if (results.isNotEmpty()) {
            LazyColumn(
                Modifier.heightIn(max = 320.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(results, key = { it.id.toString() }) { result ->
                    Card(Modifier.fillMaxWidth().clickable { onOpen(result) }) {
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
                                Text(result.name, fontWeight = FontWeight.SemiBold)
                                if (result.origin.isNotBlank()) {
                                    Text("· en ${result.origin}", style = MaterialTheme.typography.bodySmall)
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
    }
}

/** Which section of Preparar holds a result. */
internal fun sectionOf(family: ReusableContentFamily): PrepareSection? = when (family) {
    ReusableContentFamily.NPC -> PrepareSection.NPCS
    ReusableContentFamily.PLACE -> PrepareSection.PLACES
    ReusableContentFamily.SCENE -> PrepareSection.SCENES
    ReusableContentFamily.CREATURE -> PrepareSection.CREATURES
    ReusableContentFamily.ENCOUNTER -> PrepareSection.ENCOUNTERS
    ReusableContentFamily.ZONE, ReusableContentFamily.DUNGEON -> PrepareSection.DUNGEONS
    ReusableContentFamily.NOTE -> PrepareSection.NOTES
    ReusableContentFamily.HOMEBREW_RULE -> PrepareSection.HOUSE_RULES
    ReusableContentFamily.PACKAGE -> PrepareSection.PACKAGES
    else -> null
}
