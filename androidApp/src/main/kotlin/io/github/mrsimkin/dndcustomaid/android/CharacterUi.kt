package io.github.mrsimkin.dndcustomaid.android

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDecodeResult
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureState
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.characterListClassSummary
import io.github.mrsimkin.dndcustomaid.shared.character.characterListFreshnessLabel
import kotlin.uuid.Uuid

@Composable
internal fun CharacterListScreen(
    campaign: Campaign,
    repository: CharacterRepository,
    backupRepository: CharacterBackupRepository,
    closureRepository: CharacterClosureRepository,
    onBack: () -> Unit,
    onEdit: (Uuid) -> Unit,
) {
    var characters by remember(campaign.id) { mutableStateOf(repository.listCharacters(campaign.id)) }
    var closureStates by remember(campaign.id) {
        mutableStateOf(
            characters.associate { character ->
                character.id to closureRepository.state(character.id)
            },
        )
    }
    var showCreateDialog by remember { mutableStateOf(false) }
    var importedCharacterId by remember(campaign.id) { mutableStateOf<String?>(null) }
    var importedCharacterName by remember(campaign.id) { mutableStateOf<String?>(null) }
    var importError by remember(campaign.id) { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val nowEpochSeconds = System.currentTimeMillis() / 1000L

    fun reload() {
        characters = repository.listCharacters(campaign.id)
        closureStates = characters.associate { character ->
            character.id to closureRepository.state(character.id)
        }
    }

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val raw = runCatching {
                context.contentResolver.openInputStream(uri)?.use { input ->
                    input.bufferedReader(Charsets.UTF_8).use { it.readText() }
                } ?: error("No input stream")
            }.getOrElse {
                importError = "No se pudo leer el archivo seleccionado."
                null
            }
            if (raw != null) {
                when (val decoded = CharacterBackupCodec.decode(raw)) {
                    is CharacterBackupDecodeResult.Failure -> importError = decoded.error.message
                    is CharacterBackupDecodeResult.Success -> {
                        runCatching {
                            backupRepository.importAsCopy(
                                document = decoded.document,
                                destinationCampaignId = campaign.id,
                                importedAtEpochSeconds = System.currentTimeMillis() / 1000L,
                            )
                        }.onSuccess { imported ->
                            importedCharacterId = imported.character.id.toString()
                            importedCharacterName = imported.character.name
                            importError = null
                            reload()
                        }.onFailure {
                            importError = "No se pudo restaurar el respaldo como una copia nueva."
                        }
                    }
                }
            }
        }
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

                item(key = "character-backup-import") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        OutlinedButton(
                            onClick = {
                                importLauncher.launch(
                                    arrayOf("application/json", "text/plain", "application/octet-stream"),
                                )
                            },
                        ) {
                            Text("Importar respaldo")
                        }
                    }
                }

                if (characters.isEmpty()) {
                    item {
                        CharacterUsefulEmptyState(
                            title = "Sin personajes",
                            message = "Aún no hay personajes en esta campaña.",
                            onAdd = { showCreateDialog = true },
                            addLabel = "Añadir personaje",
                        )
                    }
                } else {
                    items(characters, key = { it.id.toString() }) { character ->
                        CharacterCard(
                            character = character,
                            closureState = closureStates[character.id] ?: CharacterClosureState(),
                            nowEpochSeconds = nowEpochSeconds,
                            onClick = { onEdit(character.id) },
                        )
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

    importedCharacterId?.let { id ->
        val name = importedCharacterName.orEmpty().ifBlank { "Personaje importado" }
        AlertDialog(
            onDismissRequest = {
                importedCharacterId = null
                importedCharacterName = null
            },
            title = { Text("Respaldo importado") },
            text = {
                Text("Se creó una copia local nueva de $name en esta campaña. No se reemplazó ningún personaje existente.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        importedCharacterId = null
                        importedCharacterName = null
                        runCatching { Uuid.parse(id) }.getOrNull()?.let(onEdit)
                    },
                ) { Text("Abrir personaje") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        importedCharacterId = null
                        importedCharacterName = null
                    },
                ) { Text("Cerrar") }
            },
        )
    }

    importError?.let { message ->
        AlertDialog(
            onDismissRequest = { importError = null },
            title = { Text("No se pudo importar") },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = { importError = null }) { Text("Cerrar") }
            },
        )
    }
}

@Composable
private fun CharacterCard(
    character: CharacterSheet,
    closureState: CharacterClosureState,
    nowEpochSeconds: Long,
    onClick: () -> Unit,
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CharacterPortraitThumbnailV4(
                uriRef = closureState.portraitRef,
                characterName = character.name,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(character.name, style = MaterialTheme.typography.titleMedium)
                Text(characterListClassSummary(character.classes), style = MaterialTheme.typography.bodyMedium)
                Text(
                    "${statusLabel(character.status)} · Nivel total ${character.totalLevel}",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    characterListFreshnessLabel(character.updatedAtEpochSeconds, nowEpochSeconds),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CharacterPortraitThumbnailV4(
    uriRef: String?,
    characterName: String,
) {
    val context = LocalContext.current
    val bitmap = remember(uriRef) {
        uriRef?.let { raw ->
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(raw))?.use { input ->
                    BitmapFactory.decodeStream(input)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    Surface(
        modifier = Modifier
            .size(64.dp)
            .clip(MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Retrato de $characterName",
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = characterName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "—",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CreateCharacterDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    val normalizedName = name.trim()

    CharacterImeSafeEditorDialog(
        title = "Nuevo personaje",
        onCancel = onDismiss,
        onSave = { onCreate(normalizedName) },
        saveLabel = "Crear",
        saveEnabled = normalizedName.isNotEmpty(),
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del personaje") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        CharacterInlineValidationMessage(
            if (name.isNotEmpty() && normalizedName.isEmpty()) "Escribe un nombre para crear el personaje." else null,
        )
    }
}

private fun statusLabel(status: CharacterStatus): String = when (status) {
    CharacterStatus.ACTIVE -> "Activo"
    CharacterStatus.INACTIVE -> "Inactivo"
    CharacterStatus.RETIRED -> "Retirado"
    CharacterStatus.DEAD -> "Muerto"
}
