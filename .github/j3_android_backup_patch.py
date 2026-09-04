from pathlib import Path


def replace_exact(path: str, old: str, new: str, count: int = 1) -> None:
    file_path = Path(path)
    text = file_path.read_text()
    actual = text.count(old)
    if actual != count:
        raise RuntimeError(f"{path}: expected {count} occurrence(s), found {actual}")
    file_path.write_text(text.replace(old, new))


main = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt"
replace_exact(
    main,
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository\n",
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository\n"
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository\n",
)
replace_exact(
    main,
    "    private val characterRepository by lazy { CharacterRepository(database) }\n"
    "    private val characterClosureRepository by lazy { CharacterClosureRepository(database) }\n",
    "    private val characterRepository by lazy { CharacterRepository(database) }\n"
    "    private val characterBackupRepository by lazy { CharacterBackupRepository(database) }\n"
    "    private val characterClosureRepository by lazy { CharacterClosureRepository(database) }\n",
)
replace_exact(
    main,
    "                    characterRepository = characterRepository,\n"
    "                    characterClosureRepository = characterClosureRepository,\n",
    "                    characterRepository = characterRepository,\n"
    "                    characterBackupRepository = characterBackupRepository,\n"
    "                    characterClosureRepository = characterClosureRepository,\n",
)
replace_exact(
    main,
    "    characterRepository: CharacterRepository,\n"
    "    characterClosureRepository: CharacterClosureRepository,\n",
    "    characterRepository: CharacterRepository,\n"
    "    characterBackupRepository: CharacterBackupRepository,\n"
    "    characterClosureRepository: CharacterClosureRepository,\n",
)
replace_exact(
    main,
    "                CharacterListScreen(\n"
    "                    campaign = selectedCampaign,\n"
    "                    repository = characterRepository,\n",
    "                CharacterListScreen(\n"
    "                    campaign = selectedCampaign,\n"
    "                    repository = characterRepository,\n"
    "                    backupRepository = characterBackupRepository,\n",
    count=2,
)
replace_exact(
    main,
    "                CharacterEditorScreenV4(\n"
    "                    characterId = characterId,\n"
    "                    repository = characterRepository,\n",
    "                CharacterEditorScreenV4(\n"
    "                    characterId = characterId,\n"
    "                    repository = characterRepository,\n"
    "                    backupRepository = characterBackupRepository,\n",
)


character_ui = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterUi.kt"
replace_exact(
    character_ui,
    "package io.github.mrsimkin.dndcustomaid.android\n\n",
    "package io.github.mrsimkin.dndcustomaid.android\n\n"
    "import androidx.activity.compose.rememberLauncherForActivityResult\n"
    "import androidx.activity.result.contract.ActivityResultContracts\n",
)
replace_exact(
    character_ui,
    "import androidx.compose.material3.Card\n"
    "import androidx.compose.material3.FloatingActionButton\n",
    "import androidx.compose.material3.AlertDialog\n"
    "import androidx.compose.material3.Button\n"
    "import androidx.compose.material3.Card\n"
    "import androidx.compose.material3.FloatingActionButton\n",
)
replace_exact(
    character_ui,
    "import androidx.compose.material3.MaterialTheme\n"
    "import androidx.compose.material3.OutlinedTextField\n"
    "import androidx.compose.material3.Scaffold\n"
    "import androidx.compose.material3.Text\n",
    "import androidx.compose.material3.MaterialTheme\n"
    "import androidx.compose.material3.OutlinedButton\n"
    "import androidx.compose.material3.OutlinedTextField\n"
    "import androidx.compose.material3.Scaffold\n"
    "import androidx.compose.material3.Text\n"
    "import androidx.compose.material3.TextButton\n",
)
replace_exact(
    character_ui,
    "import androidx.compose.ui.Alignment\n"
    "import androidx.compose.ui.Modifier\n",
    "import androidx.compose.ui.Alignment\n"
    "import androidx.compose.ui.Modifier\n"
    "import androidx.compose.ui.platform.LocalContext\n",
)
replace_exact(
    character_ui,
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository\n",
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec\n"
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupDecodeResult\n"
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository\n"
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository\n",
)
replace_exact(
    character_ui,
    "    campaign: Campaign,\n"
    "    repository: CharacterRepository,\n"
    "    onBack: () -> Unit,\n",
    "    campaign: Campaign,\n"
    "    repository: CharacterRepository,\n"
    "    backupRepository: CharacterBackupRepository,\n"
    "    onBack: () -> Unit,\n",
)
replace_exact(
    character_ui,
    "    var characters by remember(campaign.id) { mutableStateOf(repository.listCharacters(campaign.id)) }\n"
    "    var showCreateDialog by remember { mutableStateOf(false) }\n\n"
    "    fun reload() {\n"
    "        characters = repository.listCharacters(campaign.id)\n"
    "    }\n",
    "    var characters by remember(campaign.id) { mutableStateOf(repository.listCharacters(campaign.id)) }\n"
    "    var showCreateDialog by remember { mutableStateOf(false) }\n"
    "    var importedCharacterId by remember(campaign.id) { mutableStateOf<String?>(null) }\n"
    "    var importedCharacterName by remember(campaign.id) { mutableStateOf<String?>(null) }\n"
    "    var importError by remember(campaign.id) { mutableStateOf<String?>(null) }\n"
    "    val context = LocalContext.current\n\n"
    "    fun reload() {\n"
    "        characters = repository.listCharacters(campaign.id)\n"
    "    }\n\n"
    "    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->\n"
    "        if (uri != null) {\n"
    "            val raw = runCatching {\n"
    "                context.contentResolver.openInputStream(uri)?.use { input ->\n"
    "                    input.bufferedReader(Charsets.UTF_8).use { it.readText() }\n"
    "                } ?: error(\"No input stream\")\n"
    "            }.getOrElse {\n"
    "                importError = \"No se pudo leer el archivo seleccionado.\"\n"
    "                null\n"
    "            }\n"
    "            if (raw != null) {\n"
    "                when (val decoded = CharacterBackupCodec.decode(raw)) {\n"
    "                    is CharacterBackupDecodeResult.Failure -> importError = decoded.error.message\n"
    "                    is CharacterBackupDecodeResult.Success -> {\n"
    "                        runCatching {\n"
    "                            backupRepository.importAsCopy(\n"
    "                                document = decoded.document,\n"
    "                                destinationCampaignId = campaign.id,\n"
    "                                importedAtEpochSeconds = System.currentTimeMillis() / 1000L,\n"
    "                            )\n"
    "                        }.onSuccess { imported ->\n"
    "                            importedCharacterId = imported.character.id.toString()\n"
    "                            importedCharacterName = imported.character.name\n"
    "                            importError = null\n"
    "                            reload()\n"
    "                        }.onFailure {\n"
    "                            importError = \"No se pudo restaurar el respaldo como una copia nueva.\"\n"
    "                        }\n"
    "                    }\n"
    "                }\n"
    "            }\n"
    "        }\n"
    "    }\n",
)
replace_exact(
    character_ui,
    "                }\n\n"
    "                if (characters.isEmpty()) {\n",
    "                }\n\n"
    "                item(key = \"character-backup-import\") {\n"
    "                    Row(\n"
    "                        modifier = Modifier.fillMaxWidth(),\n"
    "                        horizontalArrangement = Arrangement.End,\n"
    "                    ) {\n"
    "                        OutlinedButton(\n"
    "                            onClick = {\n"
    "                                importLauncher.launch(\n"
    "                                    arrayOf(\"application/json\", \"text/plain\", \"application/octet-stream\"),\n"
    "                                )\n"
    "                            },\n"
    "                        ) {\n"
    "                            Text(\"Importar respaldo\")\n"
    "                        }\n"
    "                    }\n"
    "                }\n\n"
    "                if (characters.isEmpty()) {\n",
)
replace_exact(
    character_ui,
    "    if (showCreateDialog) {\n"
    "        CreateCharacterDialog(\n"
    "            onDismiss = { showCreateDialog = false },\n"
    "            onCreate = { name ->\n"
    "                val character = repository.createCharacter(campaign.id, name)\n"
    "                reload()\n"
    "                showCreateDialog = false\n"
    "                onEdit(character.id)\n"
    "            },\n"
    "        )\n"
    "    }\n"
    "}\n\n"
    "@Composable\n"
    "private fun CharacterCard(\n",
    "    if (showCreateDialog) {\n"
    "        CreateCharacterDialog(\n"
    "            onDismiss = { showCreateDialog = false },\n"
    "            onCreate = { name ->\n"
    "                val character = repository.createCharacter(campaign.id, name)\n"
    "                reload()\n"
    "                showCreateDialog = false\n"
    "                onEdit(character.id)\n"
    "            },\n"
    "        )\n"
    "    }\n\n"
    "    importedCharacterId?.let { id ->\n"
    "        val name = importedCharacterName.orEmpty().ifBlank { \"Personaje importado\" }\n"
    "        AlertDialog(\n"
    "            onDismissRequest = {\n"
    "                importedCharacterId = null\n"
    "                importedCharacterName = null\n"
    "            },\n"
    "            title = { Text(\"Respaldo importado\") },\n"
    "            text = {\n"
    "                Text(\"Se creó una copia local nueva de $name en esta campaña. No se reemplazó ningún personaje existente.\")\n"
    "            },\n"
    "            confirmButton = {\n"
    "                Button(\n"
    "                    onClick = {\n"
    "                        importedCharacterId = null\n"
    "                        importedCharacterName = null\n"
    "                        runCatching { Uuid.parse(id) }.getOrNull()?.let(onEdit)\n"
    "                    },\n"
    "                ) { Text(\"Abrir personaje\") }\n"
    "            },\n"
    "            dismissButton = {\n"
    "                TextButton(\n"
    "                    onClick = {\n"
    "                        importedCharacterId = null\n"
    "                        importedCharacterName = null\n"
    "                    },\n"
    "                ) { Text(\"Cerrar\") }\n"
    "            },\n"
    "        )\n"
    "    }\n\n"
    "    importError?.let { message ->\n"
    "        AlertDialog(\n"
    "            onDismissRequest = { importError = null },\n"
    "            title = { Text(\"No se pudo importar\") },\n"
    "            text = { Text(message) },\n"
    "            confirmButton = {\n"
    "                TextButton(onClick = { importError = null }) { Text(\"Cerrar\") }\n"
    "            },\n"
    "        )\n"
    "    }\n"
    "}\n\n"
    "@Composable\n"
    "private fun CharacterCard(\n",
)


editor = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt"
replace_exact(
    editor,
    "package io.github.mrsimkin.dndcustomaid.android\n\n"
    "import androidx.activity.compose.BackHandler\n",
    "package io.github.mrsimkin.dndcustomaid.android\n\n"
    "import androidx.activity.compose.BackHandler\n"
    "import androidx.activity.compose.rememberLauncherForActivityResult\n"
    "import androidx.activity.result.contract.ActivityResultContracts\n",
)
replace_exact(
    editor,
    "import androidx.compose.ui.Modifier\n"
    "import androidx.compose.ui.geometry.Offset\n",
    "import androidx.compose.ui.Modifier\n"
    "import androidx.compose.ui.geometry.Offset\n"
    "import androidx.compose.ui.platform.LocalContext\n",
)
replace_exact(
    editor,
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility\n",
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterAbility\n"
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupCodec\n"
    "import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository\n",
)
replace_exact(
    editor,
    "    characterId: Uuid,\n"
    "    repository: CharacterRepository,\n"
    "    closureRepository: CharacterClosureRepository,\n",
    "    characterId: Uuid,\n"
    "    repository: CharacterRepository,\n"
    "    backupRepository: CharacterBackupRepository,\n"
    "    closureRepository: CharacterClosureRepository,\n",
)
replace_exact(
    editor,
    "    var confirmUnsavedLeave by rememberSaveable(characterId.toString(), \"unsaved-leave\") { mutableStateOf(false) }\n"
    "    var leaveAfterSave by rememberSaveable(characterId.toString(), \"leave-after-save\") { mutableStateOf(false) }\n\n"
    "    val combatEntries = remember(combatDraftJson) { combatEntriesFromJsonV4(combatDraftJson) }\n",
    "    var confirmUnsavedLeave by rememberSaveable(characterId.toString(), \"unsaved-leave\") { mutableStateOf(false) }\n"
    "    var leaveAfterSave by rememberSaveable(characterId.toString(), \"leave-after-save\") { mutableStateOf(false) }\n"
    "    var backupExportMessage by rememberSaveable(characterId.toString(), \"backup-export-message\") { mutableStateOf<String?>(null) }\n"
    "    val context = LocalContext.current\n"
    "    val backupExportLauncher = rememberLauncherForActivityResult(\n"
    "        ActivityResultContracts.CreateDocument(\"application/json\"),\n"
    "    ) { uri ->\n"
    "        if (uri != null) {\n"
    "            val result = runCatching {\n"
    "                val document = backupRepository.exportCharacter(\n"
    "                    characterId = characterId,\n"
    "                    exportedAtEpochSeconds = System.currentTimeMillis() / 1000L,\n"
    "                )\n"
    "                val encoded = CharacterBackupCodec.encode(document)\n"
    "                val output = requireNotNull(context.contentResolver.openOutputStream(uri, \"wt\"))\n"
    "                output.bufferedWriter(Charsets.UTF_8).use { writer -> writer.write(encoded) }\n"
    "            }\n"
    "            backupExportMessage = if (result.isSuccess) {\n"
    "                \"Respaldo exportado correctamente.\"\n"
    "            } else {\n"
    "                \"No se pudo escribir el respaldo en el archivo seleccionado.\"\n"
    "            }\n"
    "        }\n"
    "    }\n\n"
    "    val combatEntries = remember(combatDraftJson) { combatEntriesFromJsonV4(combatDraftJson) }\n",
)
replace_exact(
    editor,
    "            onOpenSupercompact = { showSupercompact = true },\n"
    "            onOpenApplicationSettings = onOpenApplicationSettings,\n",
    "            onOpenSupercompact = { showSupercompact = true },\n"
    "            backupExportEnabled = !hasUnsavedChanges,\n"
    "            onExportBackup = {\n"
    "                val safeBase = stored.name.trim()\n"
    "                    .ifBlank { \"personaje\" }\n"
    "                    .replace(Regex(\"[^\\\\p{L}\\\\p{N}._-]+\"), \"_\")\n"
    "                    .trim('_')\n"
    "                    .take(48)\n"
    "                    .ifBlank { \"personaje\" }\n"
    "                backupExportLauncher.launch(\"${safeBase}_respaldo_dnd-custom-aid.json\")\n"
    "            },\n"
    "            onOpenApplicationSettings = onOpenApplicationSettings,\n",
)
replace_exact(
    editor,
    "    if (confirmDisableSpellcasting) {\n",
    "    backupExportMessage?.let { message ->\n"
    "        AlertDialog(\n"
    "            onDismissRequest = { backupExportMessage = null },\n"
    "            title = { Text(\"Respaldo local\") },\n"
    "            text = { Text(message) },\n"
    "            confirmButton = {\n"
    "                TextButton(onClick = { backupExportMessage = null }) { Text(\"Cerrar\") }\n"
    "            },\n"
    "        )\n"
    "    }\n\n"
    "    if (confirmDisableSpellcasting) {\n",
)


pc_settings = "androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterPcSettingsClosureV4.kt"
replace_exact(
    pc_settings,
    "    onClosureStateChange: (CharacterClosureState) -> Unit,\n"
    "    onOpenSupercompact: () -> Unit,\n"
    "    onOpenApplicationSettings: () -> Unit,\n",
    "    onClosureStateChange: (CharacterClosureState) -> Unit,\n"
    "    onOpenSupercompact: () -> Unit,\n"
    "    backupExportEnabled: Boolean,\n"
    "    onExportBackup: () -> Unit,\n"
    "    onOpenApplicationSettings: () -> Unit,\n",
)
replace_exact(
    pc_settings,
    "                item(key = \"pc-settings-entries\") {\n"
    "                    PcSettingsPairClosureV4(\n"
    "                        wide = wide,\n"
    "                        first = {\n"
    "                            NavigationSettingCardClosureV4(\n"
    "                                title = \"Vista supercompacta\",\n"
    "                                description = \"Abre la vista experimental de consulta rápida para comprobar densidad y utilidad en teléfono y tablet.\",\n"
    "                                actionLabel = \"Abrir vista\",\n"
    "                                onClick = onOpenSupercompact,\n"
    "                            )\n"
    "                        },\n"
    "                        second = {\n"
    "                            NavigationSettingCardClosureV4(\n"
    "                                title = \"Configuración de la aplicación\",\n"
    "                                description = \"Tema, tipografía y escala de texto siguen siendo preferencias globales de la aplicación.\",\n"
    "                                actionLabel = \"Abrir configuración\",\n"
    "                                onClick = onOpenApplicationSettings,\n"
    "                            )\n"
    "                        },\n"
    "                    )\n"
    "                }\n",
    "                item(key = \"pc-settings-entries\") {\n"
    "                    PcSettingsPairClosureV4(\n"
    "                        wide = wide,\n"
    "                        first = {\n"
    "                            NavigationSettingCardClosureV4(\n"
    "                                title = \"Vista supercompacta\",\n"
    "                                description = \"Abre la vista experimental de consulta rápida para comprobar densidad y utilidad en teléfono y tablet.\",\n"
    "                                actionLabel = \"Abrir vista\",\n"
    "                                onClick = onOpenSupercompact,\n"
    "                            )\n"
    "                        },\n"
    "                        second = {\n"
    "                            NavigationSettingCardClosureV4(\n"
    "                                title = \"Configuración de la aplicación\",\n"
    "                                description = \"Tema, tipografía y escala de texto siguen siendo preferencias globales de la aplicación.\",\n"
    "                                actionLabel = \"Abrir configuración\",\n"
    "                                onClick = onOpenApplicationSettings,\n"
    "                            )\n"
    "                        },\n"
    "                    )\n"
    "                }\n\n"
    "                item(key = \"pc-settings-backup\") {\n"
    "                    NavigationSettingCardClosureV4(\n"
    "                        title = \"Respaldo local\",\n"
    "                        description = if (backupExportEnabled) {\n"
    "                            \"Exporta a un archivo la última versión guardada de este personaje. El archivo puede importarse luego como una copia local nueva.\"\n"
    "                        } else {\n"
    "                            \"Guarda o descarta los cambios pendientes antes de exportar un respaldo.\"\n"
    "                        },\n"
    "                        actionLabel = \"Exportar respaldo\",\n"
    "                        enabled = backupExportEnabled,\n"
    "                        onClick = onExportBackup,\n"
    "                    )\n"
    "                }\n",
)
replace_exact(
    pc_settings,
    "private fun NavigationSettingCardClosureV4(\n"
    "    title: String,\n"
    "    description: String,\n"
    "    actionLabel: String,\n"
    "    onClick: () -> Unit,\n"
    ") {\n"
    "    PcSettingCardClosureV4(title = title, description = description) {\n"
    "        TextButton(onClick = onClick) { Text(actionLabel) }\n"
    "    }\n"
    "}\n",
    "private fun NavigationSettingCardClosureV4(\n"
    "    title: String,\n"
    "    description: String,\n"
    "    actionLabel: String,\n"
    "    enabled: Boolean = true,\n"
    "    onClick: () -> Unit,\n"
    ") {\n"
    "    PcSettingCardClosureV4(title = title, description = description) {\n"
    "        TextButton(onClick = onClick, enabled = enabled) { Text(actionLabel) }\n"
    "    }\n"
    "}\n",
)
