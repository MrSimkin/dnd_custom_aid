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
import io.github.mrsimkin.dndcustomaid.shared.db.AndroidDatabaseFactory

class MainActivity : ComponentActivity() {
    private val campaignRepository by lazy {
        CampaignRepository(
            database = AndroidDatabaseFactory(applicationContext).create(),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CampaignScreen(campaignRepository)
            }
        }
    }
}

@Composable
private fun CampaignScreen(repository: CampaignRepository) {
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
                            text = "Campaigns",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Text(
                            text = "Choose the campaign you want to use on this device.",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }

                if (campaigns.isEmpty()) {
                    item {
                        Text(
                            text = "No campaigns yet. Use + to create one.",
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
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            RadioButton(
                selected = isActive,
                onClick = onSelect,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = campaign.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (isActive) {
                    Text(
                        text = "Active campaign",
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
        onDismissRequest = onDismiss,
        title = { Text("New campaign") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Campaign name") },
                singleLine = true,
            )
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name) },
                enabled = normalizedName.isNotEmpty(),
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
