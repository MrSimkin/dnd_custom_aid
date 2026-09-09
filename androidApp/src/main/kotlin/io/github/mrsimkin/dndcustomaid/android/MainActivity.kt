package io.github.mrsimkin.dndcustomaid.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterBackupRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterClosureRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterDirectoryRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AndroidDatabaseFactory
import kotlin.uuid.Uuid

class MainActivity : ComponentActivity() {
    private val database by lazy {
        AndroidDatabaseFactory(applicationContext).create()
    }

    private val campaignRepository by lazy { CampaignRepository(database) }
    private val characterRepository by lazy { CharacterRepository(database) }
    private val characterDirectoryRepository by lazy { CharacterDirectoryRepository(database, characterRepository) }
    private val characterBackupRepository by lazy { CharacterBackupRepository(database) }
    private val characterClosureRepository by lazy { CharacterClosureRepository(database) }
    private val characterSuccessorRepository by lazy { CharacterSuccessorRepository(database) }
    private val uiPreferencesStore by lazy { UiPreferencesStore(applicationContext) }
    private val characterNavigationPreferenceStore by lazy { CharacterNavigationPreferenceStore(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var preferences by remember { mutableStateOf(uiPreferencesStore.load()) }

            fun updatePreferences(updated: UiPreferences) {
                preferences = updated
                uiPreferencesStore.save(updated)
            }

            DndCustomAidTheme(preferences = preferences) {
                DndCustomAidApp(
                    campaignRepository = campaignRepository,
                    characterRepository = characterRepository,
                    characterDirectoryRepository = characterDirectoryRepository,
                    characterBackupRepository = characterBackupRepository,
                    characterClosureRepository = characterClosureRepository,
                    characterSuccessorRepository = characterSuccessorRepository,
                    characterNavigationPreferenceStore = characterNavigationPreferenceStore,
                    preferences = preferences,
                    onPreferencesChange = ::updatePreferences,
                )
            }
        }
    }
}

private enum class AppScreen {
    CAMPAIGNS,
    CHARACTERS,
    CHARACTER_EDITOR,
}

@Composable
private fun DndCustomAidApp(
    campaignRepository: CampaignRepository,
    characterRepository: CharacterRepository,
    characterDirectoryRepository: CharacterDirectoryRepository,
    characterBackupRepository: CharacterBackupRepository,
    characterClosureRepository: CharacterClosureRepository,
    characterSuccessorRepository: CharacterSuccessorRepository,
    characterNavigationPreferenceStore: CharacterNavigationPreferenceStore,
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
) {
    var screenName by rememberSaveable { mutableStateOf(AppScreen.CHARACTERS.name) }
    var selectedCharacterId by rememberSaveable { mutableStateOf<String?>(null) }
    var showSettings by rememberSaveable { mutableStateOf(false) }

    val screen = runCatching { AppScreen.valueOf(screenName) }.getOrDefault(AppScreen.CHARACTERS)

    BackHandler(enabled = showSettings) {
        showSettings = false
    }
    BackHandler(enabled = !showSettings && screen == AppScreen.CAMPAIGNS) {
        selectedCharacterId = null
        screenName = AppScreen.CHARACTERS.name
    }
    BackHandler(enabled = !showSettings && screen == AppScreen.CHARACTER_EDITOR) {
        selectedCharacterId = null
        screenName = AppScreen.CHARACTERS.name
    }

    val directory: @Composable () -> Unit = {
        CharacterDirectoryScreen(
            campaignRepository = campaignRepository,
            repository = characterRepository,
            directoryRepository = characterDirectoryRepository,
            backupRepository = characterBackupRepository,
            onOpenCampaigns = {
                selectedCharacterId = null
                screenName = AppScreen.CAMPAIGNS.name
            },
            onOpenSettings = { showSettings = true },
            onEdit = { character ->
                selectedCharacterId = character.id.toString()
                screenName = AppScreen.CHARACTER_EDITOR.name
            },
        )
    }

    when (screen) {
        AppScreen.CAMPAIGNS -> CampaignScreen(
            repository = campaignRepository,
            onBack = {
                selectedCharacterId = null
                screenName = AppScreen.CHARACTERS.name
            },
            onOpenSettings = { showSettings = true },
        )

        AppScreen.CHARACTERS -> directory()

        AppScreen.CHARACTER_EDITOR -> {
            val characterId = selectedCharacterId?.let { runCatching { Uuid.parse(it) }.getOrNull() }
            if (characterId == null) {
                directory()
            } else {
                CharacterPcSettingsStateProviderV4(
                    characterId = characterId,
                    characterRepository = characterRepository,
                    successorRepository = characterSuccessorRepository,
                ) {
                    CharacterEditorScreenV4(
                        characterId = characterId,
                        repository = characterRepository,
                        backupRepository = characterBackupRepository,
                        closureRepository = characterClosureRepository,
                        navigationPreferenceStore = characterNavigationPreferenceStore,
                        preferences = preferences,
                        onPreferencesChange = onPreferencesChange,
                        onOpenApplicationSettings = { showSettings = true },
                        onBack = {
                            selectedCharacterId = null
                            screenName = AppScreen.CHARACTERS.name
                        },
                    )
                }
            }
        }
    }

    if (showSettings) {
        AppSettingsDialog(
            preferences = preferences,
            onPreferencesChange = onPreferencesChange,
            onDismiss = { showSettings = false },
        )
    }
}

@Composable
private fun CampaignScreen(
    repository: CampaignRepository,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    var campaigns by remember { mutableStateOf(repository.listCampaigns()) }
    var activeCampaignId by remember { mutableStateOf(repository.activeCampaign()?.id) }
    var showCreateDialog by remember { mutableStateOf(false) }

    fun reload() {
        campaigns = repository.listCampaigns()
        activeCampaignId = repository.activeCampaign()?.id
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Text("+")
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
                    .widthIn(max = 720.dp)
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        StableBackIconButton(
                            onClick = onBack,
                            contentDescription = "Volver a personajes",
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(appSpacingV4(2.dp)),
                        ) {
                            Text(
                                text = "Campañas",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Text(
                                text = "Administra campañas y elige la campaña activa.",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        StableSettingsIconButton(onClick = onOpenSettings)
                    }
                }

                if (campaigns.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no hay campañas. Usa + para crear una.",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                } else {
                    items(
                        items = campaigns,
                        key = { campaign -> campaign.id.toString() },
                    ) { campaign ->
                        CampaignCard(
                            campaign = campaign,
                            isActive = campaign.id == activeCampaignId,
                            onSelect = {
                                repository.setActiveCampaign(campaign.id)
                                reload()
                            },
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCampaignDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                repository.createCampaign(name)
                reload()
                showCreateDialog = false
            },
        )
    }
}

@Composable
private fun CampaignCard(
    campaign: Campaign,
    isActive: Boolean,
    onSelect: () -> Unit,
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
        ) {
            RadioButton(
                selected = isActive,
                onClick = onSelect,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(appSpacingV4(1.dp)),
            ) {
                Text(
                    text = campaign.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (isActive) {
                    Text(
                        text = "Campaña activa",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateCampaignDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    val normalizedName = name.trim()

    AlertDialog(
        modifier = Modifier.imePadding().navigationBarsPadding(),
        onDismissRequest = onDismiss,
        title = { Text("Nueva campaña") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la campaña") },
                singleLine = true,
            )
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name) },
                enabled = normalizedName.isNotEmpty(),
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}
