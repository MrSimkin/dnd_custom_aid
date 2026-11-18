package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.uuid.Uuid

/**
 * «Cambios del paquete» (owner point 12): what the package brings that you do not have, and where
 * it disagrees with what you edited. Nothing changes until you mark it and say so.
 */
@Composable
internal fun DesktopBundleChangesDialog(
    comparison: BundleComparison,
    onDismiss: () -> Unit,
    onUseTheirs: (Set<Uuid>) -> Unit,
) {
    var chosen by remember { mutableStateOf(setOf<Uuid>()) }
    val actionable = comparison.news + comparison.different

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambios del paquete") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(comparison.spanishSummary(), style = MaterialTheme.typography.bodyMedium)
                if (actionable.isEmpty()) {
                    Text("Lo que tienes ya está al día con este paquete.", style = MaterialTheme.typography.bodyMedium)
                    return@Column
                }
                Text(
                    "Marca lo que quieras traer del paquete. Lo que no marques se queda como lo tienes tú.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { chosen = actionable.map { it.id }.toSet() }) { Text("Marcar todo") }
                    TextButton(onClick = { chosen = emptySet() }) { Text("Desmarcar todo") }
                }
                LazyColumn(Modifier.heightIn(max = 360.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    items(actionable, key = { it.id.toString() }) { change ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Checkbox(
                                checked = change.id in chosen,
                                onCheckedChange = { marked -> chosen = if (marked) chosen + change.id else chosen - change.id },
                            )
                            Column(Modifier.weight(1f)) {
                                Text(change.name, fontWeight = FontWeight.SemiBold)
                                Text(
                                    when (change.kind) {
                                        BundleChangeKind.NUEVO -> "No lo tienes todavía"
                                        BundleChangeKind.DIFERENTE ->
                                            "Distinto de lo tuyo" + change.fields.take(4).joinToString(", ", prefix = " · ").takeIf { change.fields.isNotEmpty() }.orEmpty()
                                        BundleChangeKind.IGUAL -> "Igual"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(enabled = chosen.isNotEmpty(), onClick = { onUseTheirs(chosen) }) {
                Text("Usar lo del paquete en lo marcado")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Mantener lo mío") } },
    )
}
