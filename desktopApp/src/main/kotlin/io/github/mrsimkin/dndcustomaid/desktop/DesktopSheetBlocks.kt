package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/*
 * Owner points 6 and 10 of the critical review («se abre como la ficha que verás en la mesa, con
 * “Editar” por bloque» and «dos vistas por objeto»): what you prepare opens as the card you will
 * read at the table, and only the block you touch turns into fields.
 */

/**
 * One block of a table card: a title, what it says, and «Editar» to change just this part.
 * While [editing] it shows [edit] with «Listo»; otherwise [read].
 */
@Composable
internal fun SheetBlock(
    title: String,
    editing: Boolean,
    onEdit: () -> Unit,
    onDone: () -> Unit,
    read: @Composable () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                if (editing) {
                    TextButton(onClick = onDone) { Text("Listo") }
                } else {
                    TextButton(onClick = onEdit) { Text("Editar") }
                }
            }
            read()
        }
    }
}

/** One line of a card: its label and what it says. Nothing is drawn when there is nothing to say. */
@Composable
internal fun SheetLine(label: String, value: String) {
    if (value.isBlank()) return
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

/** What a block says when every line of it is still empty. */
@Composable
internal fun SheetEmpty(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}
