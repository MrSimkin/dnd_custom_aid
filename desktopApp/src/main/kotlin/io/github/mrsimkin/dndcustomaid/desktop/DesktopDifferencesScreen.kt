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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentChoice
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentConflictResolution
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentDifference
import io.github.mrsimkin.dndcustomaid.shared.hosted.DmContentSyncConflictReason

/**
 * «Diferencias que decidir» (owner's B, 2026-11-20). When the same thing changed here and on
 * another device, nothing is overwritten: the two versions sit side by side and the DM chooses.
 */
@Composable
internal fun DesktopDifferencesScreen(
    database: AppDatabase,
    onQaEvent: (String) -> Unit,
    nowEpochSeconds: () -> Long = { System.currentTimeMillis() / 1000L },
) {
    val resolution = remember(database) { DmContentConflictResolution(database) }
    var version by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf<String?>(null) }
    val differences = remember(version) { resolution.differences() }

    fun choose(difference: DmContentDifference, choice: DmContentChoice, said: String) {
        resolution.resolve(difference.contentId, choice, nowEpochSeconds())
        message = "«${difference.theirsName}»: $said"
        onQaEvent("Diferencias: ${choice.name} ${difference.contentId}")
        version += 1
    }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "Cuando algo cambió aquí y en otro aparato, no se pisa nada: eliges tú. " +
                "Si cambió en un solo lado, ese cambio entra solo y no aparece aquí.",
            style = MaterialTheme.typography.bodyMedium,
        )
        message?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }

        if (differences.isEmpty()) {
            EmptyList(
                searching = false,
                nothingYet = "No hay nada que decidir.",
                howToCreate = "Si alguna vez cambias lo mismo en Windows y en la tablet, aparecerá aquí para que elijas.",
            )
            return@Column
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(differences, key = { it.contentId.toString() }) { difference ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(difference.theirsName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(reasonText(difference), style = MaterialTheme.typography.bodySmall)
                        HorizontalDivider()
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Version(
                                title = "En este equipo",
                                lines = difference.mineLines,
                                highlight = difference.differingFields,
                                missing = "Lo borraste en este equipo.",
                                modifier = Modifier.weight(1f),
                            )
                            Version(
                                title = "En el otro aparato",
                                lines = difference.theirsLines,
                                highlight = difference.differingFields,
                                missing = "Lo borraron en el otro aparato.",
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { choose(difference, DmContentChoice.MINE, "queda lo de este equipo.") }) {
                                Text("Quedarme con lo de aquí")
                            }
                            OutlinedButton(onClick = { choose(difference, DmContentChoice.THEIRS, "queda lo del otro aparato.") }) {
                                Text("Quedarme con lo del otro")
                            }
                            OutlinedButton(onClick = { choose(difference, DmContentChoice.BOTH, "se guardaron las dos.") }) {
                                Text("Guardar las dos")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Version(
    title: String,
    lines: List<Pair<String, String>>,
    highlight: List<String>,
    missing: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        if (lines.isEmpty()) {
            Text(missing, style = MaterialTheme.typography.bodySmall)
            return@Column
        }
        lines.forEach { (label, value) ->
            val differs = label in highlight
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (differs) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    value,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (differs) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 4,
                )
            }
        }
    }
}

private fun reasonText(difference: DmContentDifference): String = when (difference.reason) {
    DmContentSyncConflictReason.LOCAL_AND_HOSTED_CHANGED ->
        "Cambió aquí y en el otro aparato. Lo distinto: " + difference.differingFields.joinToString(", ").ifBlank { "el contenido" } + "."
    DmContentSyncConflictReason.CHANGED_LOCALLY_DELETED_HOSTED ->
        "Lo cambiaste aquí y lo borraron en el otro aparato."
    DmContentSyncConflictReason.REJECTED -> "El servidor no aceptó el cambio."
    DmContentSyncConflictReason.IDENTITY_MISMATCH -> "Los dos aparatos no están de acuerdo en qué es este objeto."
    DmContentSyncConflictReason.INVALID_HOSTED_DOCUMENT -> "Lo que llegó del servidor no se pudo leer."
}
