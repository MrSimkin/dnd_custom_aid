package io.github.mrsimkin.dndcustomaid.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.RadioButton
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
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AndroidDatabaseFactory
import kotlin.uuid.Uuid

class MainActivity : ComponentActivity() {
    private val database by lazy {
        AndroidDatabaseFactory(applicationContext).create()
    }

    private val campaignRepository by lazy { CampaignRepository(database) }
    private val characterRepository by lazy { CharacterRepository(database) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                DndCustomAidApp(campaignRepository, characterRepository)
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
) {
    var screen by remember { mutableStateOf(AppScreen.CAMPAIGNS) }
    var selectedCampaign by remember { mutableStateOf<Campaign?>(null) }
    var selectedCharacterId by remember { mutableStateOf<Uuid?>(null) }

    when (screen) {
        AppScreen.CAMPAIGNS -> CampaignScreen(
            repository = campaignRepository,
            onOpenCharacters = { campaign ->
                selectedCampaign = campaign
                selectedCharacterId = null
                screen = AppScreen.CHARACTERS
            },
        )

        AppScreen.CHARACTERS -> {
            val campaign = selectedCampaign
            if (campaign == null) {
                screen = AppScreen.CAMPAIGNS
            } else {
                CharacterListScreen(
                    campaign = campaign,
                    repository = characterRepository,
                    onBack = {
                        selectedCampaign = null
                        screen = AppScreen.CAMPAIGNS
                    },
                    onEdit = { characterId ->
                        selectedCharacterId = characterId
                        screen = AppScreen.CHARACTER_EDITOR
                    },
                )
            }
        }

        AppScreen.CHARACTER_EDITOR -> {
            val characterId = selectedCharacterId
            if (characterId == null) {
                screen = AppScreen.CHARACTERS
            } else {
                CharacterEditorScreen(
                    characterId = characterId,
                    repository = characterRepository,
                    onBack = {
                        selectedCharacterId = null
                        screen = AppScreen.CHARACTERS
                    },
                )
            }
        }
    }
}

@Composable
private fun CampaignScreen(
    repository: CampaignRepository,
    onOpenCharacters: (Campaign) -> Unit,
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
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Campañas",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Text(
                            text = "Elige la campaña que quieres usar en este dispositivo.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
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
                            onOpenCharacters = { onOpenCharacters(campaign) },
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
    onOpenCharacters: () -> Unit,
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RadioButton(
                selected = isActive,
                onClick = onSelect,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
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
            if (isActive) {
                TextButton(onClick = onOpenCharacters) {
                    Text("Personajes")
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
