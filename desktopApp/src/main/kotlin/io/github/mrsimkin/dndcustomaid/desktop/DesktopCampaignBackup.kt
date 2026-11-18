package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.content.CampaignArchiveService
import io.github.mrsimkin.dndcustomaid.shared.content.parseCampaignArchive
import io.github.mrsimkin.dndcustomaid.shared.content.writeCampaignArchive
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import java.io.File

/**
 * «Copia de seguridad» (owner, 2026-11-18): saving the open campaign to a file of your own, and
 * bringing it back here or on another computer. Recovering never overwrites: it adds the objects.
 */
@Composable
internal fun DesktopCampaignBackupCard(
    database: AppDatabase,
    campaign: Campaign,
    onMessage: (String) -> Unit,
    chooseFile: (save: Boolean, suggestedName: String?) -> File? = { save, suggested ->
        chooseFileWithExplorer(
            title = if (save) "Guardar la copia de seguridad" else "Elegir una copia de seguridad",
            purpose = "backups",
            campaignKey = null,
            extensions = listOf("json"),
            save = save,
            suggestedName = suggested,
        )
    },
    nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    val service = remember(database) { CampaignArchiveService(database) }
    var busy by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(desktopSpacing(16.dp)), verticalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
            Text("Copia de seguridad de «${campaign.name}»", style = MaterialTheme.typography.titleMedium)
            Text(
                "Guarda en un archivo tuyo todo lo que tiene la campaña: lugares, PNJ, escenas, dungeons, " +
                    "encuentros, criaturas, handouts, notas y los PJ, con sus enlaces. Sirve para no perder el " +
                    "trabajo si pasa algo con este equipo, y para llevar la campaña a otro computador.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                OutlinedButton(enabled = !busy, onClick = {
                    busy = true
                    val result = runCatching {
                        val now = nowEpochSeconds()
                        val archive = service.save(campaign.id, now)
                        val name = "copia-" + campaign.name.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-') +
                            "-" + formatPcEpoch(now).take(10) + ".json"
                        val file = chooseFile(true, name) ?: return@runCatching null
                        file.writeText(writeCampaignArchive(archive))
                        "Copia guardada en ${file.name}: ${archive.spanishSummary()}."
                    }
                    busy = false
                    result.getOrElse { "No se pudo guardar la copia: ${it.message}" }?.let(onMessage)
                }) { Text("Guardar copia de seguridad…") }

                OutlinedButton(enabled = !busy, onClick = {
                    busy = true
                    val result = runCatching {
                        val file = chooseFile(false, null) ?: return@runCatching null
                        val archive = parseCampaignArchive(file.readText())
                        val report = service.restoreInto(archive, campaign.id, nowEpochSeconds())
                        "De «${archive.campaignName}»: ${report.spanishSummary()}"
                    }
                    busy = false
                    result.getOrElse { "No se pudo recuperar: ${it.message}" }?.let(onMessage)
                }) { Text("Recuperar desde una copia…") }
            }
            Text(
                "Al recuperar, lo que trae la copia se agrega a la campaña abierta; nada de lo que ya tienes se pisa.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
