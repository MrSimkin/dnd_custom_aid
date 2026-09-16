package io.github.mrsimkin.dndcustomaid.desktop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory

fun main() {
    val databaseHandle = DesktopDatabaseFactory().create()
    val campaignRepository = CampaignRepository(databaseHandle.database)

    try {
        application {
            Window(
                onCloseRequest = ::exitApplication,
                title = "D&D Custom Aid — Desktop",
            ) {
                MaterialTheme {
                    DesktopWorkbench(campaignRepository)
                }
            }
        }
    } finally {
        databaseHandle.close()
    }
}

private enum class DesktopDestination(
    val label: String,
) {
    DASHBOARD("Dashboard"),
    CAMPAIGNS("Campaigns"),
    PLAYER_CHARACTERS("Player Characters"),
    MEDIA_HANDOUTS("Media / Handouts"),
    MANAGERS("Managers"),
    COMBAT("Combat"),
    CAMPAIGN_ADMINISTRATION("Campaign Administration"),
    SYSTEM_ADMINISTRATION("System Administration"),
    EXPORT_BACKUP("Export / Backup"),
}

@Composable
private fun DesktopWorkbench(
    campaignRepository: CampaignRepository,
) {
    var destination by remember { mutableStateOf(DesktopDestination.DASHBOARD) }
    var campaigns by remember { mutableStateOf(campaignRepository.listCampaigns()) }
    var activeCampaign by remember { mutableStateOf(campaignRepository.activeCampaign()) }

    fun refreshCampaigns() {
        campaigns = campaignRepository.listCampaigns()
        activeCampaign = campaignRepository.activeCampaign()
    }

    fun activateCampaign(campaign: Campaign) {
        campaignRepository.setActiveCampaign(campaign.id)
        refreshCampaigns()
    }

    Scaffold(
        topBar = {
            WorkbenchTopBar(activeCampaign = activeCampaign)
        },
        bottomBar = {
            WorkbenchStatusBar(
                campaignCount = campaigns.size,
                activeCampaign = activeCampaign,
            )
        },
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            WorkbenchNavigation(
                selected = destination,
                onSelect = { destination = it },
            )

            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp),
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(24.dp),
            ) {
                when (destination) {
                    DesktopDestination.DASHBOARD -> DashboardScreen(
                        campaigns = campaigns,
                        activeCampaign = activeCampaign,
                        onOpenCampaigns = { destination = DesktopDestination.CAMPAIGNS },
                        onOpenAdministration = { destination = DesktopDestination.CAMPAIGN_ADMINISTRATION },
                    )

                    DesktopDestination.CAMPAIGNS -> CampaignsScreen(
                        campaigns = campaigns,
                        activeCampaign = activeCampaign,
                        onCreate = { name ->
                            val campaign = campaignRepository.createCampaign(name)
                            campaignRepository.setActiveCampaign(campaign.id)
                            refreshCampaigns()
                        },
                        onActivate = ::activateCampaign,
                    )

                    DesktopDestination.CAMPAIGN_ADMINISTRATION -> CampaignAdministrationScreen(
                        activeCampaign = activeCampaign,
                        onOpenCampaigns = { destination = DesktopDestination.CAMPAIGNS },
                    )

                    else -> DeferredDestinationScreen(destination)
                }
            }

            if (
                destination == DesktopDestination.DASHBOARD ||
                destination == DesktopDestination.CAMPAIGNS ||
                destination == DesktopDestination.CAMPAIGN_ADMINISTRATION
            ) {
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                )
                CampaignContextPanel(
                    campaigns = campaigns,
                    activeCampaign = activeCampaign,
                    onActivate = ::activateCampaign,
                )
            }
        }
    }
}

@Composable
private fun WorkbenchTopBar(
    activeCampaign: Campaign?,
) {
    TopAppBar(
        title = {
            Column {
                Text("D&D Custom Aid — DM Workbench")
                Text(
                    text = activeCampaign?.let { "Active campaign: ${it.name}" } ?: "No active campaign",
                    style = MaterialTheme.typography.caption,
                )
            }
        },
    )
}

@Composable
private fun WorkbenchNavigation(
    selected: DesktopDestination,
    onSelect: (DesktopDestination) -> Unit,
) {
    Surface(
        elevation = 2.dp,
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight(),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Workspace",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            )

            DesktopDestination.entries.forEach { destination ->
                if (destination == selected) {
                    Button(
                        onClick = { onSelect(destination) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(destination.label)
                    }
                } else {
                    TextButton(
                        onClick = { onSelect(destination) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(destination.label)
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardScreen(
    campaigns: List<Campaign>,
    activeCampaign: Campaign?,
    onOpenCampaigns: () -> Unit,
    onOpenAdministration: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        WorkbenchHeading(
            title = "Dashboard",
            subtitle = "Desktop campaign workspace overview",
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(
                title = "Local campaigns",
                value = campaigns.size.toString(),
            )
            SummaryCard(
                title = "Active campaign",
                value = activeCampaign?.name ?: "None",
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp),
            elevation = 2.dp,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Wave 5 workbench",
                    style = MaterialTheme.typography.h6,
                )
                Text(
                    "This first Desktop slice uses the Shared campaign repository and persistent local SQLite state. " +
                        "Hosted Desktop sync and moderation actions remain separate follow-up work.",
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onOpenCampaigns) {
                        Text("Open campaigns")
                    }
                    if (activeCampaign != null) {
                        Button(onClick = onOpenAdministration) {
                            Text("Open campaign administration")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
) {
    Card(
        modifier = Modifier.widthIn(min = 220.dp, max = 360.dp),
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(title, style = MaterialTheme.typography.caption)
            Text(value, style = MaterialTheme.typography.h6)
        }
    }
}

@Composable
private fun CampaignsScreen(
    campaigns: List<Campaign>,
    activeCampaign: Campaign?,
    onCreate: (String) -> Unit,
    onActivate: (Campaign) -> Unit,
) {
    var newCampaignName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        WorkbenchHeading(
            title = "Campaigns",
            subtitle = "Create and select the local campaign context used by the Desktop workbench.",
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 2.dp,
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TextField(
                    value = newCampaignName,
                    onValueChange = { newCampaignName = it },
                    label = { Text("Campaign name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                Button(
                    onClick = {
                        onCreate(newCampaignName)
                        newCampaignName = ""
                    },
                    enabled = newCampaignName.isNotBlank(),
                ) {
                    Text("Create campaign")
                }
            }
        }

        if (campaigns.isEmpty()) {
            Text("No local campaigns yet. Create one above to establish the Desktop campaign context.")
        } else {
            Text(
                text = "Local campaigns",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = campaigns,
                    key = { it.id.toString() },
                ) { campaign ->
                    val isActive = campaign.id == activeCampaign?.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onActivate(campaign) },
                        elevation = if (isActive) 6.dp else 1.dp,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = campaign.name,
                                    style = MaterialTheme.typography.subtitle1,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                )
                                Text(
                                    text = "ID ${campaign.id.toString().take(8)}…",
                                    style = MaterialTheme.typography.caption,
                                )
                            }
                            Text(if (isActive) "ACTIVE" else "Select")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CampaignAdministrationScreen(
    activeCampaign: Campaign?,
    onOpenCampaigns: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        WorkbenchHeading(
            title = "Campaign Administration",
            subtitle = "Campaign-centric administration workspace",
        )

        if (activeCampaign == null) {
            Card(elevation = 2.dp) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text("No active campaign is selected.")
                    Button(onClick = onOpenCampaigns) {
                        Text("Choose a campaign")
                    }
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 2.dp,
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(activeCampaign.name, style = MaterialTheme.typography.h6)
                    Text("Campaign ID: ${activeCampaign.id}")
                    Divider()
                    Text(
                        "This package establishes the Campaign Administration workspace and campaign context. " +
                            "Membership invitations, Kick/Ban/Unban controls and hosted administration are intentionally deferred " +
                            "to their own bounded follow-up package.",
                    )
                }
            }
        }
    }
}

@Composable
private fun CampaignContextPanel(
    campaigns: List<Campaign>,
    activeCampaign: Campaign?,
    onActivate: (Campaign) -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight(),
        elevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Campaign context",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = activeCampaign?.name ?: "No active campaign",
                style = MaterialTheme.typography.body1,
            )
            if (activeCampaign != null) {
                Text(
                    text = activeCampaign.id.toString(),
                    style = MaterialTheme.typography.caption,
                )
            }

            Divider()

            Text(
                text = "Switch campaign",
                style = MaterialTheme.typography.subtitle2,
            )

            if (campaigns.isEmpty()) {
                Text("No campaigns available.", style = MaterialTheme.typography.caption)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(
                        items = campaigns,
                        key = { it.id.toString() },
                    ) { campaign ->
                        TextButton(
                            onClick = { onActivate(campaign) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                if (campaign.id == activeCampaign?.id) {
                                    "${campaign.name} • active"
                                } else {
                                    campaign.name
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeferredDestinationScreen(
    destination: DesktopDestination,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        WorkbenchHeading(
            title = destination.label,
            subtitle = "Reserved Desktop workbench destination",
        )
        Card(elevation = 2.dp) {
            Text(
                text = "The workbench navigation is established, but ${destination.label} is outside the current bounded package.",
                modifier = Modifier.padding(20.dp),
            )
        }
    }
}

@Composable
private fun WorkbenchHeading(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.body1,
        )
    }
}

@Composable
private fun WorkbenchStatusBar(
    campaignCount: Int,
    activeCampaign: Campaign?,
) {
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Local campaign store: ready",
                style = MaterialTheme.typography.caption,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$campaignCount campaign${if (campaignCount == 1) "" else "s"} • " +
                    (activeCampaign?.let { "active: ${it.name}" } ?: "no active campaign"),
                style = MaterialTheme.typography.caption,
            )
        }
    }
}
