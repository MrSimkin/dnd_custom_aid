package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.hosted.SyncStatus

/**
 * «Estado de la sincronización» (owner's D, 2026-11-20). One «Sincronizar» runs everything; this
 * says in plain words what is still waiting, instead of leaving the DM guessing.
 */
@Composable
internal fun DesktopSyncStatusCard(
    status: SyncStatus,
    nowEpochSeconds: Long,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(desktopSpacing(16.dp)), verticalArrangement = Arrangement.spacedBy(desktopSpacing(6.dp))) {
            Text("Estado de la sincronización", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(status.spanishSummary(nowEpochSeconds), style = MaterialTheme.typography.bodyMedium)

            if (!status.nothingToDo) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Line("Cambios de personajes esperando", status.pendingPcChanges)
                    Line("Cambios de lo preparado esperando", status.pendingDmChanges)
                    Line("Diferencias que decidir", status.differencesToDecide, "Preparar → Diferencias que decidir")
                    Line("Cambios que el servidor no aceptó", status.blockedChanges)
                    Line("Imágenes que faltan en este equipo", status.missingImages)
                }
            }
            Text(
                "«⟳ Sincronizar», arriba, hace todo de una vez: la cuenta y las campañas, los personajes, " +
                    "lo preparado y sus imágenes.",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun Line(label: String, count: Int, where: String? = null) {
    if (count <= 0) return
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("$count", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
        Text(label, style = MaterialTheme.typography.bodySmall)
        where?.let { Text("· $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
    }
}
