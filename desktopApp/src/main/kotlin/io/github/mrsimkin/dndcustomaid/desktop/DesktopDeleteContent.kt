package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * «Borrar» for what you prepare (owner, 2026-11-18). It always asks first and says where the thing
 * goes, because deleting also removes it from the tablet the next time you sync.
 */
@Composable
internal fun DeleteContentButton(name: String, what: String, onDelete: () -> Unit) {
    var asking by remember { mutableStateOf(false) }
    TextButton(onClick = { asking = true }) { Text("Borrar") }
    if (asking) {
        AlertDialog(
            onDismissRequest = { asking = false },
            title = { Text("¿Borrar $what?") },
            text = {
                Text(
                    "«${name.ifBlank { "Sin nombre" }}» se va de la campaña, y también de la tablet la próxima vez que sincronices. " +
                        "Queda en «Preparar → Papelera» por si te arrepientes.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(onClick = { asking = false; onDelete() }) { Text("Sí, borrar") }
            },
            dismissButton = { TextButton(onClick = { asking = false }) { Text("Cancelar") } },
        )
    }
}
