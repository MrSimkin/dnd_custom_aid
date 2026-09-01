package io.github.mrsimkin.dndcustomaid.android

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import kotlin.uuid.Uuid

@Composable
internal fun CharacterListScreen(
    campaign: Campaign,
    repository: CharacterRepository,
    onBack: () -> Unit,
    onEdit: (Uuid) -> Unit,
) {
    var characters by remember(campaign.id) { mutableStateOf(repository.listCharacters(campaign.id)) }
    var showCreateDialog by remember { mutableStateOf(false) }

    fun reload() {
        characters = repository.listCharacters(campaign.id)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                StableAddIcon(contentDescription = "Añadir personaje")
            }
        },
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 900.dp)
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        StableBackIconButton(onClick = onBack, contentDescription = "Volver a campañas")
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(1.dp),
                        ) {
                            Text("Personajes", style = MaterialTheme.typography.headlineMedium)
                            Text(campaign.name, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }

                if (characters.isEmpty()) {
                    item {
                        Text("Aún no hay personajes en esta campaña. Usa el botón Añadir para crear uno.")
                    }
                } else {
                    items(characters, key = { it.id.toString() }) { character ->
                        CharacterCard(character = character, onClick = { onEdit(character.id) })
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCharacterDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                val character = repository.createCharacter(campaign.id, name)
                reload()
                showCreateDialog = false
                onEdit(character.id)
            },
        )
    }
}

@Composable
private fun CharacterCard(
    character: CharacterSheet,
    onClick: () -> Unit,
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(character.name, style = MaterialTheme.typography.titleMedium)
            val classSummary = if (character.classes.isEmpty()) {
                "Sin clase registrada"
            } else {
                character.classes.joinToString(" / ") { "${it.name} ${it.level}" }
            }
            Text(classSummary, style = MaterialTheme.typography.bodyMedium)
            Text(
                "${statusLabel(character.status)} · Nivel total ${character.totalLevel}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun CreateCharacterDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Nuevo personaje") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del personaje") },
                singleLine = true,
            )
        },
        confirmButton = {
            Button(onClick = { onCreate(name) }, enabled = name.trim().isNotEmpty()) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        },
    )
}

private fun statusLabel(status: CharacterStatus): String = when (status) {
    CharacterStatus.ACTIVE -> "Activo"
    CharacterStatus.INACTIVE -> "Inactivo"
    CharacterStatus.RETIRED -> "Retirado"
    CharacterStatus.DEAD -> "Muerto"
}
