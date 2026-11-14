package io.github.mrsimkin.dndcustomaid.android

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDecodeResult
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDirectoryRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterStatus
import io.github.mrsimkin.dndcustomaid.shared.character.characterListClassSummary
import io.github.mrsimkin.dndcustomaid.shared.character.characterListFreshnessLabel
import kotlin.uuid.Uuid

@Composable
internal fun CharacterDirectoryScreen(
    campaignRepository: CampaignRepository,
    repository: CharacterRepository,
    directoryRepository: CharacterDirectoryRepository,
    backupRepository: CharacterBackupRepository,
    onOpenCampaigns: () -> Unit,
    onOpenSettings: () -> Unit,
    onEdit: (CharacterSheet) -> Unit,
) {
    var campaigns by remember { mutableStateOf(campaignRepository.listCampaigns()) }
    var characters by remember { mutableStateOf(directoryRepository.listAllCharacters()) }
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var showImportCampaignDialog by rememberSaveable { mutableStateOf(false) }
    var pendingImportCampaignId by rememberSaveable { mutableStateOf<String?>(null) }
    var importedCharacterId by rememberSaveable { mutableStateOf<String?>(null) }
    var importedCharacterName by rememberSaveable { mutableStateOf<String?>(null) }
    var importedCampaignName by rememberSaveable { mutableStateOf<String?>(null) }
    var importError by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val nowEpochSeconds = System.currentTimeMillis() / 1000L

    fun reload() {
        campaigns = campaignRepository.listCampaigns()
        characters = directoryRepository.listAllCharacters()
    }

    val campaignById = campaigns.associateBy { it.id }
    val defaultCampaignId = campaignRepository.activeCampaign()?.id
        ?.takeIf { it in campaignById }
        ?: campaigns.firstOrNull()?.id

    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val destinationCampaignId = pendingImportCampaignId
            ?.let { runCatching { Uuid.parse(it) }.getOrNull() }
        pendingImportCampaignId = null

        if (uri != null && destinationCampaignId != null) {
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
                                destinationCampaignId = destinationCampaignId,
                                importedAtEpochSeconds = System.currentTimeMillis() / 1000L,
                            )
                        }.onSuccess { imported ->
                            importedCharacterId = imported.character.id.toString()
                            importedCharacterName = imported.character.name
                            importedCampaignName = campaignById[destinationCampaignId]?.name
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
            FloatingActionButton(
                onClick = {
                    if (campaigns.isEmpty()) onOpenCampaigns() else showCreateDialog = true
                },
            ) {
                StableAddIcon(
                    contentDescription = if (campaigns.isEmpty()) "Crear campaña" else "Añadir personaje",
                )
            }
        },
    ) { scaffoldPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(scaffoldPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 900.dp)
                    .fillMaxSize()
                    .padding(horizontal = appSpacingV4(7.dp)),
                contentPadding = PaddingValues(
                    top = appSpacingV4(5.dp),
                    bottom = appSpacingV4(84.dp),
                ),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(5.dp)),
            ) {
                item(key = "character-directory-header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
                    ) {
                        Text(
                            "Personajes",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.headlineMedium,
                            maxLines = 1,
                        )
                        TextButton(onClick = onOpenCampaigns) { Text("Campañas") }
                        TextButton(
                            onClick = { showImportCampaignDialog = true },
                            enabled = campaigns.isNotEmpty(),
                        ) { Text("Importar") }
                        StableSettingsIconButton(onClick = onOpenSettings)
                    }
                }

                when {
                    campaigns.isEmpty() -> item(key = "character-directory-no-campaigns") {
                        CharacterUsefulEmptyState(
                            title = "Sin campañas",
                            message = "Crea una campaña para poder crear o importar personajes.",
                            onAdd = onOpenCampaigns,
                            addLabel = "Ir a campañas",
                        )
                    }

                    characters.isEmpty() -> item(key = "character-directory-empty") {
                        CharacterUsefulEmptyState(
                            title = "Sin personajes",
                            message = "Aún no hay personajes. Puedes crear uno en cualquiera de tus campañas.",
                            onAdd = { showCreateDialog = true },
                            addLabel = "Añadir personaje",
                        )
                    }

                    else -> items(characters, key = { it.id.toString() }) { character ->
                        CharacterDirectoryCard(
                            character = character,
                            campaignName = campaignById[character.campaignId]?.name ?: "Campaña no disponible",
                            nowEpochSeconds = nowEpochSeconds,
                            onClick = { onEdit(character) },
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog && campaigns.isNotEmpty() && defaultCampaignId != null) {
        CreateCharacterInCampaignDialog(
            campaigns = campaigns,
            defaultCampaignId = defaultCampaignId,
            onDismiss = { showCreateDialog = false },
            onCreate = { campaignId, name, start ->
                val created = repository.createCharacter(campaignId, name)
                val character = start?.let { repository.saveCharacter(it.applyTo(created)) } ?: created
                reload()
                showCreateDialog = false
                onEdit(character)
            },
        )
    }

    if (showImportCampaignDialog && campaigns.isNotEmpty() && defaultCampaignId != null) {
        ChooseCharacterCampaignDialog(
            title = "Importar respaldo",
            message = "Elige la campaña donde se creará la copia importada.",
            campaigns = campaigns,
            defaultCampaignId = defaultCampaignId,
            onDismiss = { showImportCampaignDialog = false },
            onContinue = { campaignId ->
                showImportCampaignDialog = false
                pendingImportCampaignId = campaignId.toString()
                importLauncher.launch(
                    arrayOf("application/json", "text/plain", "application/octet-stream"),
                )
            },
        )
    }

    importedCharacterId?.let { id ->
        val name = importedCharacterName.orEmpty().ifBlank { "Personaje importado" }
        val campaignName = importedCampaignName.orEmpty().ifBlank { "la campaña seleccionada" }
        AlertDialog(
            onDismissRequest = {
                importedCharacterId = null
                importedCharacterName = null
                importedCampaignName = null
            },
            title = { Text("Respaldo importado") },
            text = {
                Text("Se creó una copia local nueva de $name en $campaignName. No se reemplazó ningún personaje existente.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        val imported = runCatching { Uuid.parse(id) }.getOrNull()?.let(repository::character)
                        importedCharacterId = null
                        importedCharacterName = null
                        importedCampaignName = null
                        imported?.let(onEdit)
                    },
                ) { Text("Abrir personaje") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        importedCharacterId = null
                        importedCharacterName = null
                        importedCampaignName = null
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
private fun CharacterDirectoryCard(
    character: CharacterSheet,
    campaignName: String,
    nowEpochSeconds: Long,
    onClick: () -> Unit,
) {
    val race = character.background.race.trim().ifBlank { "Sin registrar" }
    val classSummary = characterListClassSummary(character.classes)
    val freshness = characterListFreshnessLabel(character.updatedAtEpochSeconds, nowEpochSeconds)

    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = appSpacingV4(7.dp),
                    vertical = appSpacingV4(5.dp),
                ),
            verticalArrangement = Arrangement.spacedBy(appSpacingV4(1.dp)),
        ) {
            Text(
                character.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "Raza: $race · $classSummary",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                "$campaignName · ${directoryStatusLabel(character.status)} · $freshness",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CreateCharacterInCampaignDialog(
    campaigns: List<Campaign>,
    defaultCampaignId: Uuid,
    onDismiss: () -> Unit,
    onCreate: (Uuid, String, CharacterStartingChoices?) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var campaignIdText by rememberSaveable { mutableStateOf(defaultCampaignId.toString()) }
    val selectedCampaign = campaigns.firstOrNull { it.id.toString() == campaignIdText } ?: campaigns.first()
    val normalizedName = name.trim()
    var start by remember { mutableStateOf(CharacterStartingChoices()) }

    CharacterImeSafeEditorDialog(
        title = "Nuevo personaje",
        onCancel = onDismiss,
        onSave = { onCreate(selectedCampaign.id, normalizedName, start.takeIf { !it.isEmpty }) },
        saveLabel = "Crear",
        saveEnabled = normalizedName.isNotEmpty(),
    ) {
        CharacterCompactOutlinedTextFieldV4(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre del personaje") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        CampaignSelectorV4(
            campaigns = campaigns,
            selected = selectedCampaign,
            onSelect = { campaignIdText = it.id.toString() },
        )
        CharacterInlineValidationMessage(
            if (name.isNotEmpty() && normalizedName.isEmpty()) "Escribe un nombre para crear el personaje." else null,
        )
        // Redesign R5 (D8): start from the SRD, then the homebrew; everything stays editable afterwards.
        Text("Empezar desde el SRD o tu homebrew (opcional)", style = MaterialTheme.typography.titleSmall)
        Text(
            listOfNotNull(
                start.species?.let { "Especie: ${it.name}" },
                start.characterClass?.let { "Clase: ${it.name} ${start.level}" },
                start.subclass?.let { "Subclase: ${it.name}" },
                start.background?.let { "Trasfondo: ${it.name}" },
            ).joinToString(" · ").ifBlank { "Sin elegir: el personaje empieza en blanco." },
            style = MaterialTheme.typography.bodySmall,
        )
        RulesPickButton(
            kinds = listOf(io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.SPECIES),
            label = "📖 Especie",
            onPick = { start = start.copy(species = it) },
        )
        RulesPickButton(
            kinds = listOf(io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.CLASS),
            label = "📖 Clase",
            onPick = { start = start.copy(characterClass = it) },
        )
        if (start.characterClass != null) {
            CharacterCompactOutlinedTextFieldV4(
                value = start.level.toString(),
                onValueChange = { value -> start = start.copy(level = value.filter(Char::isDigit).toIntOrNull()?.coerceIn(1, 20) ?: 1) },
                label = { Text("Nivel") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            RulesPickButton(
                kinds = listOf(io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.SUBCLASS),
                label = "📖 Subclase",
                onPick = { start = start.copy(subclass = it) },
            )
        }
        RulesPickButton(
            kinds = listOf(io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryKind.BACKGROUND),
            label = "📖 Trasfondo",
            onPick = { start = start.copy(background = it) },
        )
    }
}

@Composable
private fun ChooseCharacterCampaignDialog(
    title: String,
    message: String,
    campaigns: List<Campaign>,
    defaultCampaignId: Uuid,
    onDismiss: () -> Unit,
    onContinue: (Uuid) -> Unit,
) {
    var campaignIdText by rememberSaveable { mutableStateOf(defaultCampaignId.toString()) }
    val selectedCampaign = campaigns.firstOrNull { it.id.toString() == campaignIdText } ?: campaigns.first()

    CharacterImeSafeEditorDialog(
        title = title,
        onCancel = onDismiss,
        onSave = { onContinue(selectedCampaign.id) },
        saveLabel = "Continuar",
    ) {
        Text(message, style = MaterialTheme.typography.bodySmall)
        CampaignSelectorV4(
            campaigns = campaigns,
            selected = selectedCampaign,
            onSelect = { campaignIdText = it.id.toString() },
        )
    }
}

@Composable
private fun CampaignSelectorV4(
    campaigns: List<Campaign>,
    selected: Campaign,
    onSelect: (Campaign) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
    ) {
        Text("Campaña", style = MaterialTheme.typography.labelSmall)
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    selected.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                campaigns.forEach { campaign ->
                    DropdownMenuItem(
                        text = {
                            Text(campaign.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        },
                        onClick = {
                            onSelect(campaign)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

private fun directoryStatusLabel(status: CharacterStatus): String = when (status) {
    CharacterStatus.ACTIVE -> "Activo"
    CharacterStatus.INACTIVE -> "Inactivo"
    CharacterStatus.RETIRED -> "Retirado"
    CharacterStatus.DEAD -> "Muerto"
}


/** What «Nuevo personaje» picked from the SRD or the homebrew (redesign R5, owner decision D8). */
data class CharacterStartingChoices(
    val species: io.github.mrsimkin.dndcustomaid.shared.dm.RulesOption? = null,
    val characterClass: io.github.mrsimkin.dndcustomaid.shared.dm.RulesOption? = null,
    val level: Int = 1,
    val subclass: io.github.mrsimkin.dndcustomaid.shared.dm.RulesOption? = null,
    val background: io.github.mrsimkin.dndcustomaid.shared.dm.RulesOption? = null,
) {
    val isEmpty: Boolean get() = species == null && characterClass == null && background == null

    fun applyTo(sheet: io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet): io.github.mrsimkin.dndcustomaid.shared.character.CharacterSheet {
        fun family(option: io.github.mrsimkin.dndcustomaid.shared.dm.RulesOption) = when {
            option.origin != io.github.mrsimkin.dndcustomaid.shared.content.RulesOrigin.SRD -> io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily.CUSTOM
            option.edition == "5e" -> io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily.DND_5E
            option.edition == "5.5e" -> io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily.DND_5_5E
            else -> io.github.mrsimkin.dndcustomaid.shared.character.CharacterRulesFamily.UNSPECIFIED
        }
        val classes = characterClass?.let { picked ->
            listOf(
                io.github.mrsimkin.dndcustomaid.shared.character.CharacterClassLevel(
                    id = Uuid.random(),
                    name = picked.name,
                    level = level,
                    hitDieSides = picked.fields["dado_de_golpe"]?.removePrefix("d")?.toIntOrNull() ?: 8,
                    hitDiceRemaining = level,
                    sortOrder = 0,
                    rulesFamily = family(picked),
                    source = picked.source,
                    subclassName = subclass?.name,
                    subclassSource = subclass?.source,
                    subclassRulesFamily = subclass?.let(::family) ?: family(picked),
                ),
            )
        } ?: sheet.classes
        return sheet.copy(
            classes = classes,
            background = sheet.background.copy(
                name = background?.name ?: sheet.background.name,
                race = species?.name ?: sheet.background.race,
            ),
        )
    }
}
