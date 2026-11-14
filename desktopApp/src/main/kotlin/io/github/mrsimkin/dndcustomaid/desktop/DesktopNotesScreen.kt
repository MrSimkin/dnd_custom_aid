package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import io.github.mrsimkin.dndcustomaid.shared.content.NoteContent
import io.github.mrsimkin.dndcustomaid.shared.content.NoteContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.NoteContextKind
import io.github.mrsimkin.dndcustomaid.shared.content.NotePayload
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase

/**
 * «Preparar → Notas» (redesign A9, owner observation N1): every note of the campaign in one
 * place, grouped by where it was written (a zone, a place, a scene, a PNJ, a dungeon or the
 * campaign). Notes written at the table on the tablet arrive here when syncing.
 */
@Composable
internal fun DesktopNotesScreen(database: AppDatabase, activeCampaign: Campaign?) {
    val campaign = activeCampaign ?: run {
        Text("Primero abre una campaña: las notas son de la campaña abierta.")
        return
    }
    val repository = remember(database) { NoteContentRepository(database) }
    var version by remember { mutableStateOf(0) }
    val notes = remember(campaign.id, version) { repository.listCampaign(campaign.id) }
    var newText by remember { mutableStateOf("") }
    val now = { System.currentTimeMillis() / 1000L }

    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = newText,
                onValueChange = { newText = it },
                label = { Text("Nota nueva para la campaña") },
                modifier = Modifier.weight(1f),
            )
            Button(enabled = newText.isNotBlank(), onClick = {
                repository.create(campaign.id, NotePayload(newText.trim(), NoteContextKind.CAMPAIGN, null, campaign.name), now())
                newText = ""
                version++
            }) { Text("Guardar") }
        }
        if (notes.isEmpty()) {
            Text("Aún no hay notas. Las que escribas en la mesa (📝 Notas) aparecen aquí, agrupadas por dónde las escribiste.")
            return
        }
        val groups = notes.groupBy { note ->
            val kind = note.payload.contextKind.label
            if (note.payload.contextKind == NoteContextKind.CAMPAIGN) "Campaña" else "$kind · ${note.payload.contextLabel.ifBlank { "sin nombre" }}"
        }.toSortedMap(compareBy { if (it == "Campaña") "" else it.lowercase() })
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            groups.forEach { (group, items) ->
                item(key = "group-$group") {
                    Text("$group (${items.size})", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
                }
                items(items, key = { it.item.identity.id.toString() }) { note ->
                    NoteCard(note = note, onSave = { text ->
                        repository.update(note.item.identity.id, note.item.identity.revision, note.payload.copy(text = text), now())
                        version++
                    }, onDelete = {
                        repository.tombstone(note.item.identity.id, note.item.identity.revision, now())
                        version++
                    })
                }
            }
        }
    }
}

@Composable
private fun NoteCard(note: NoteContent, onSave: (String) -> Unit, onDelete: () -> Unit) {
    var text by remember(note.item.identity.id, note.item.identity.revision) { mutableStateOf(note.payload.text) }
    var confirmDelete by remember { mutableStateOf(false) }
    Card(elevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (text.trim() != note.payload.text && text.isNotBlank()) {
                    Button(onClick = { onSave(text.trim()) }) { Text("Guardar cambios") }
                }
                if (!confirmDelete) {
                    TextButton(onClick = { confirmDelete = true }) { Text("Borrar") }
                } else {
                    Text("¿Borrar esta nota?", style = MaterialTheme.typography.body2)
                    Button(onClick = onDelete) { Text("Borrar") }
                    TextButton(onClick = { confirmDelete = false }) { Text("Cancelar") }
                }
            }
        }
    }
}
