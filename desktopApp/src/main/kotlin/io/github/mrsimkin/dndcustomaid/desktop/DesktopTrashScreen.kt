package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.content.ContentTrash
import io.github.mrsimkin.dndcustomaid.shared.content.ReusableContentFamily
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.spine.ContentScope

/**
 * «Papelera» (owner, 2026-11-18). What you delete in Preparar waits here. Bringing it back makes a
 * new object with the same content and its links, because a deletion already told the other
 * devices that the old one is gone and must never come back on its own.
 */
@Composable
internal fun DesktopTrashScreen(
    database: AppDatabase,
    activeCampaign: Campaign?,
    onQaEvent: (String) -> Unit,
    nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    if (activeCampaign == null) {
        Text("Abre una campaña para ver su papelera.")
        return
    }
    val trash = remember(database) { ContentTrash(database) }
    var version by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    val deleted = remember(version, activeCampaign.id) { trash.list(ContentScope.Campaign(activeCampaign.id)) }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "Lo que borras en Preparar queda aquí. Recuperarlo lo devuelve a la campaña con su contenido y sus enlaces.",
            style = MaterialTheme.typography.bodyMedium,
        )
        message?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
        if (deleted.isEmpty()) {
            EmptyList(
                searching = false,
                nothingYet = "La papelera está vacía.",
                howToCreate = "Cuando borres un PNJ, un lugar, una escena, una criatura o un encuentro, lo vas a encontrar aquí.",
            )
            return@Column
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(deleted, key = { it.item.identity.id.toString() }) { entry ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(entry.item.displayName, fontWeight = FontWeight.SemiBold)
                            Text(
                                familyLabel(entry.item.family) + " · borrado el " + spanishDate(entry.deletedAtEpochSeconds),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        Button(onClick = {
                            val restored = trash.restore(entry.item.identity.id, nowEpochSeconds())
                            message = "«${restored.displayName}» volvió a la campaña."
                            onQaEvent("Papelera: recuperado ${restored.identity.id}")
                            version += 1
                        }) { Text("Recuperar") }
                    }
                }
            }
        }
    }
}

private fun familyLabel(family: ReusableContentFamily): String = when (family) {
    ReusableContentFamily.NPC -> "PNJ"
    ReusableContentFamily.PLACE -> "Lugar"
    ReusableContentFamily.SCENE -> "Escena"
    ReusableContentFamily.CREATURE -> "Criatura"
    ReusableContentFamily.ENCOUNTER -> "Encuentro"
    ReusableContentFamily.ZONE -> "Zona"
    ReusableContentFamily.DUNGEON -> "Dungeon"
    ReusableContentFamily.HANDOUT -> "Handout"
    ReusableContentFamily.NOTE -> "Nota"
    ReusableContentFamily.PACKAGE -> "Paquete"
    ReusableContentFamily.HOMEBREW_RULE -> "Regla de la casa"
    ReusableContentFamily.RULES_ENTRY -> "Regla"
}

private fun spanishDate(epochSeconds: Long): String = formatPcEpoch(epochSeconds)
