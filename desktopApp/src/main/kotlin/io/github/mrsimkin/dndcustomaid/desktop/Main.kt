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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import kotlin.math.roundToInt

fun main() {
    val databaseFactory = DesktopDatabaseFactory()
    val databaseHandle = databaseFactory.create()
    val campaignRepository = CampaignRepository(databaseHandle.database)
    val preferencesStore = DesktopPreferencesStore()

    try {
        application {
            Window(
                onCloseRequest = ::exitApplication,
                title = "D&D Custom Aid — Desktop",
            ) {
                var preferences by remember { mutableStateOf(preferencesStore.load()) }

                DesktopAppTheme(preferences) {
                    DesktopWorkbench(
                        campaignRepository = campaignRepository,
                        preferences = preferences,
                        onPreferencesChange = { updated ->
                            preferences = updated
                            preferencesStore.save(updated)
                        },
                    )
                }
            }
        }
    } finally {
        databaseHandle.close()
    }
}

private enum class DesktopDestination(val label: String) {
    DASHBOARD("Dashboard"),
    CAMPAIGNS("Campañas"),
    PLAYER_CHARACTERS("Personajes jugadores"),
    MEDIA_HANDOUTS("Medios / Handouts"),
    MANAGERS("Gestores"),
    COMBAT("Combate"),
    CAMPAIGN_ADMINISTRATION("Administración de campaña"),
    SYSTEM_ADMINISTRATION("Administración del sistema"),
    EXPORT_BACKUP("Exportar / Backup"),
    APPLICATION_SETTINGS("Configuración"),
    QA_DIAGNOSTICS("QA / Diagnóstico"),
}

@Composable
private fun DesktopWorkbench(
    campaignRepository: CampaignRepository,
    preferences: DesktopPreferences,
    onPreferencesChange: (DesktopPreferences) -> Unit,
) {
    var destination by remember { mutableStateOf(DesktopDestination.DASHBOARD) }
    var campaigns by remember { mutableStateOf(campaignRepository.listCampaigns()) }
    var activeCampaign by remember { mutableStateOf(campaignRepository.activeCampaign()) }
    var qaEvents by remember { mutableStateOf(listOf("Inicio del workbench Desktop")) }

    fun logQa(message: String) {
        qaEvents = (qaEvents + message).takeLast(100)
    }

    fun refreshCampaigns() {
        campaigns = campaignRepository.listCampaigns()
        activeCampaign = campaignRepository.activeCampaign()
    }

    fun activateCampaign(campaign: Campaign) {
        campaignRepository.setActiveCampaign(campaign.id)
        refreshCampaigns()
        logQa("Campaña activa: ${campaign.name} (${campaign.id})")
    }

    fun selectDestination(next: DesktopDestination) {
        if (next != destination) {
            destination = next
            logQa("Navegación: ${next.label}")
        }
    }

    Scaffold(
        topBar = { WorkbenchTopBar(activeCampaign = activeCampaign) },
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
                density = preferences.workspaceDensity,
                onSelect = ::selectDestination,
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
                    .padding(desktopSpacing(24.dp)),
            ) {
                when (destination) {
                    DesktopDestination.DASHBOARD -> DashboardScreen(
                        campaigns = campaigns,
                        activeCampaign = activeCampaign,
                        onOpenCampaigns = { selectDestination(DesktopDestination.CAMPAIGNS) },
                        onOpenAdministration = { selectDestination(DesktopDestination.CAMPAIGN_ADMINISTRATION) },
                    )

                    DesktopDestination.CAMPAIGNS -> CampaignsScreen(
                        campaigns = campaigns,
                        activeCampaign = activeCampaign,
                        onCreate = { name ->
                            val campaign = campaignRepository.createCampaign(name)
                            campaignRepository.setActiveCampaign(campaign.id)
                            refreshCampaigns()
                            logQa("Campaña local creada: ${campaign.name} (${campaign.id})")
                        },
                        onActivate = ::activateCampaign,
                    )

                    DesktopDestination.CAMPAIGN_ADMINISTRATION -> CampaignAdministrationScreen(
                        activeCampaign = activeCampaign,
                        onOpenCampaigns = { selectDestination(DesktopDestination.CAMPAIGNS) },
                    )

                    DesktopDestination.APPLICATION_SETTINGS -> ApplicationSettingsScreen(
                        preferences = preferences,
                        onPreferencesChange = onPreferencesChange,
                    )

                    DesktopDestination.QA_DIAGNOSTICS -> QaDiagnosticsScreen(
                        campaigns = campaigns,
                        activeCampaign = activeCampaign,
                        destination = destination,
                        preferences = preferences,
                        events = qaEvents,
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
                    density = preferences.workspaceDensity,
                    onActivate = ::activateCampaign,
                )
            }
        }
    }
}

@Composable
private fun WorkbenchTopBar(activeCampaign: Campaign?) {
    TopAppBar(
        title = {
            Column {
                Text("D&D Custom Aid — Mesa de trabajo DM")
                Text(
                    text = activeCampaign?.let { "Campaña activa: ${it.name}" } ?: "Sin campaña activa",
                    style = MaterialTheme.typography.caption,
                )
            }
        },
    )
}

@Composable
private fun WorkbenchNavigation(
    selected: DesktopDestination,
    density: DesktopWorkspaceDensity,
    onSelect: (DesktopDestination) -> Unit,
) {
    Surface(
        elevation = 2.dp,
        modifier = Modifier
            .width(density.navigationWidth())
            .fillMaxHeight(),
    ) {
        Column(
            modifier = Modifier.padding(desktopSpacing(12.dp)),
            verticalArrangement = Arrangement.spacedBy(desktopSpacing(4.dp)),
        ) {
            Text(
                text = "Espacio de trabajo",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    horizontal = desktopSpacing(8.dp),
                    vertical = desktopSpacing(8.dp),
                ),
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
    Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp))) {
        WorkbenchHeading(
            title = "Dashboard",
            subtitle = "Resumen del espacio de trabajo de campañas.",
        )

        Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp))) {
            SummaryCard("Campañas locales", campaigns.size.toString())
            SummaryCard("Campaña activa", activeCampaign?.name ?: "Ninguna")
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp),
            elevation = 2.dp,
        ) {
            Column(
                modifier = Modifier.padding(desktopSpacing(20.dp)),
                verticalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp)),
            ) {
                Text("Paquete Wave 5", style = MaterialTheme.typography.h6)
                Text(
                    "Este primer corte de Desktop usa el repositorio Shared de campañas y estado local persistente en SQLite. " +
                        "La sincronización alojada de Desktop y las acciones de moderación siguen siendo trabajo posterior separado.",
                )
                Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                    Button(onClick = onOpenCampaigns) { Text("Abrir campañas") }
                    if (activeCampaign != null) {
                        Button(onClick = onOpenAdministration) {
                            Text("Abrir administración de campaña")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String) {
    Card(
        modifier = Modifier.widthIn(min = 220.dp, max = 360.dp),
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier.padding(desktopSpacing(16.dp)),
            verticalArrangement = Arrangement.spacedBy(desktopSpacing(6.dp)),
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
        verticalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp)),
    ) {
        WorkbenchHeading(
            title = "Campañas",
            subtitle = "Crea y selecciona el contexto de campaña local usado por el workbench Desktop.",
        )

        Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
            Row(
                modifier = Modifier.padding(desktopSpacing(16.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp)),
            ) {
                TextField(
                    value = newCampaignName,
                    onValueChange = { newCampaignName = it },
                    label = { Text("Nombre de campaña") },
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
                    Text("Crear campaña")
                }
            }
        }

        if (campaigns.isEmpty()) {
            Text("Aún no hay campañas locales. Crea una arriba para establecer el contexto Desktop.")
        } else {
            Text("Campañas locales", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp)),
            ) {
                items(items = campaigns, key = { it.id.toString() }) { campaign ->
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
                                .padding(desktopSpacing(16.dp)),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = campaign.name,
                                    style = MaterialTheme.typography.subtitle1,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                )
                                Text("ID ${campaign.id.toString().take(8)}…", style = MaterialTheme.typography.caption)
                            }
                            Text(if (isActive) "ACTIVA" else "Seleccionar")
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
    Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp))) {
        WorkbenchHeading(
            title = "Administración de campaña",
            subtitle = "Espacio de administración centrado en la campaña.",
        )

        if (activeCampaign == null) {
            Card(elevation = 2.dp) {
                Column(
                    modifier = Modifier.padding(desktopSpacing(20.dp)),
                    verticalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp)),
                ) {
                    Text("No hay una campaña activa seleccionada.")
                    Button(onClick = onOpenCampaigns) { Text("Elegir campaña") }
                }
            }
        } else {
            Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                Column(
                    modifier = Modifier.padding(desktopSpacing(20.dp)),
                    verticalArrangement = Arrangement.spacedBy(desktopSpacing(10.dp)),
                ) {
                    Text(activeCampaign.name, style = MaterialTheme.typography.h6)
                    Text("ID de campaña: ${activeCampaign.id}")
                    Divider()
                    Text(
                        "Este paquete establece el espacio de Administración de campaña y su contexto. " +
                            "Invitaciones, Kick/Ban/Unban y administración alojada se mantienen deliberadamente " +
                            "para paquetes posteriores acotados.",
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
    density: DesktopWorkspaceDensity,
    onActivate: (Campaign) -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(density.contextPanelWidth())
            .fillMaxHeight(),
        elevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(desktopSpacing(16.dp)),
            verticalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp)),
        ) {
            Text("Contexto de campaña", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
            Text(activeCampaign?.name ?: "Sin campaña activa", style = MaterialTheme.typography.body1)
            if (activeCampaign != null) {
                SelectionContainer {
                    Text(activeCampaign.id.toString(), style = MaterialTheme.typography.caption)
                }
            }

            Divider()
            Text("Cambiar campaña", style = MaterialTheme.typography.subtitle2)

            if (campaigns.isEmpty()) {
                Text("No hay campañas disponibles.", style = MaterialTheme.typography.caption)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(desktopSpacing(4.dp))) {
                    items(items = campaigns, key = { it.id.toString() }) { campaign ->
                        TextButton(
                            onClick = { onActivate(campaign) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                if (campaign.id == activeCampaign?.id) {
                                    "${campaign.name} • activa"
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
private fun ApplicationSettingsScreen(
    preferences: DesktopPreferences,
    onPreferencesChange: (DesktopPreferences) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(desktopSpacing(18.dp)),
    ) {
        item {
            WorkbenchHeading(
                title = "Configuración de la aplicación",
                subtitle = "Preferencias de este dispositivo, alineadas con los conceptos del Player Android donde aplican.",
            )
        }
        item {
            SettingPercentSlider(
                label = "Tamaño de texto",
                value = preferences.fontScalePercent,
                options = DESKTOP_FONT_SCALE_OPTIONS,
                onSelect = { onPreferencesChange(preferences.copy(fontScalePercent = it)) },
                detail = "Ajusta la escala tipográfica del workbench sin cambiar los datos de la campaña.",
            )
        }
        item {
            SettingPercentSlider(
                label = "Densidad de espacios",
                value = preferences.spacingScalePercent,
                options = DESKTOP_SPACING_SCALE_OPTIONS,
                onSelect = { onPreferencesChange(preferences.copy(spacingScalePercent = it)) },
                detail = "50–90% = más denso · 100% = equilibrado · 110–150% = más espacioso.",
            )
        }
        item {
            SettingSelector(
                label = "Fuente",
                value = preferences.fontChoice.label,
                options = DesktopFontChoice.entries,
                optionLabel = { it.label },
                onSelect = { onPreferencesChange(preferences.copy(fontChoice = it)) },
            )
        }
        item {
            SettingSelector(
                label = "Tema",
                value = preferences.themeChoice.label,
                options = DesktopThemeChoice.entries,
                optionLabel = { it.label },
                onSelect = { onPreferencesChange(preferences.copy(themeChoice = it)) },
            )
        }
        item {
            SettingSelector(
                label = "Densidad del espacio de trabajo",
                value = preferences.workspaceDensity.label,
                options = DesktopWorkspaceDensity.entries,
                optionLabel = { it.label },
                onSelect = { onPreferencesChange(preferences.copy(workspaceDensity = it)) },
            )
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                Column(
                    modifier = Modifier.padding(desktopSpacing(18.dp)),
                    verticalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp)),
                ) {
                    Text("Vista previa", style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
                    Text("Alyra Voss · Maga 7", style = MaterialTheme.typography.h6)
                    Text("CD 15 · CA 17 · 1d20 + 7")
                    Text(
                        "${preferences.themeChoice.label} · ${preferences.fontChoice.label} · " +
                            "Texto ${preferences.fontScalePercent}% · Espacios ${preferences.spacingScalePercent}% · " +
                            preferences.workspaceDensity.label,
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingPercentSlider(
    label: String,
    value: Int,
    options: List<Int>,
    onSelect: (Int) -> Unit,
    detail: String,
) {
    val currentIndex = options.indexOf(value).coerceAtLeast(0)
    Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
        Column(
            modifier = Modifier.padding(desktopSpacing(18.dp)),
            verticalArrangement = Arrangement.spacedBy(desktopSpacing(6.dp)),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, fontWeight = FontWeight.Bold)
                Text("$value%")
            }
            Slider(
                value = currentIndex.toFloat(),
                onValueChange = { rawIndex ->
                    val index = rawIndex.roundToInt().coerceIn(options.indices)
                    val selected = options[index]
                    if (selected != value) onSelect(selected)
                },
                valueRange = 0f..options.lastIndex.toFloat(),
                steps = (options.size - 2).coerceAtLeast(0),
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${options.first()}%", style = MaterialTheme.typography.caption)
                Text("${options.last()}%", style = MaterialTheme.typography.caption)
            }
            Text(detail, style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
private fun <T> SettingSelector(
    label: String,
    value: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
        Row(
            modifier = Modifier.padding(desktopSpacing(18.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontWeight = FontWeight.Bold)
                Text(value, style = MaterialTheme.typography.body1)
            }
            Box {
                Button(onClick = { expanded = true }) { Text("Cambiar") }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                onSelect(option)
                                expanded = false
                            },
                        ) {
                            Text(optionLabel(option))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QaDiagnosticsScreen(
    campaigns: List<Campaign>,
    activeCampaign: Campaign?,
    destination: DesktopDestination,
    preferences: DesktopPreferences,
    events: List<String>,
) {
    val snapshot = buildQaSnapshot(
        campaigns = campaigns,
        activeCampaign = activeCampaign,
        destination = destination,
        preferences = preferences,
        events = events,
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp)),
    ) {
        item {
            WorkbenchHeading(
                title = "QA / Diagnóstico",
                subtitle = "Datos de apoyo para pruebas. Este panel no sustituye el logging técnico del sistema.",
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                Button(onClick = { copyToClipboard(snapshot) }) { Text("Copiar diagnóstico") }
                Text(
                    "Los datos también se pueden seleccionar manualmente.",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                SelectionContainer {
                    Text(
                        snapshot,
                        modifier = Modifier.padding(desktopSpacing(18.dp)),
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }
        item {
            Text(
                "El registro de sesión es deliberadamente acotado y se reinicia al cerrar la aplicación.",
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

private fun buildQaSnapshot(
    campaigns: List<Campaign>,
    activeCampaign: Campaign?,
    destination: DesktopDestination,
    preferences: DesktopPreferences,
    events: List<String>,
): String = buildString {
    appendLine("D&D Custom Aid — Desktop QA")
    appendLine("Paquete: Wave 5 Desktop shell")
    appendLine("SO: ${System.getProperty("os.name")} ${System.getProperty("os.version")} (${System.getProperty("os.arch")})")
    appendLine("Java: ${System.getProperty("java.version")}")
    appendLine("Base local: ${DesktopDatabaseFactory.defaultDatabaseFile().absolutePath}")
    appendLine("Preferencias: ${DesktopPreferencesStore.defaultPreferencesFile()}")
    appendLine("Destino actual: ${destination.label}")
    appendLine("Campañas locales: ${campaigns.size}")
    appendLine("Campaña activa: ${activeCampaign?.name ?: "ninguna"}")
    appendLine("ID campaña activa: ${activeCampaign?.id ?: "n/a"}")
    appendLine(
        "UI: tema=${preferences.themeChoice.label}; fuente=${preferences.fontChoice.label}; " +
            "texto=${preferences.fontScalePercent}%; espacios=${preferences.spacingScalePercent}%; " +
            "densidad=${preferences.workspaceDensity.label}",
    )
    appendLine("Eventos de sesión:")
    if (events.isEmpty()) {
        appendLine("- ninguno")
    } else {
        events.forEach { appendLine("- $it") }
    }
}

private fun copyToClipboard(text: String) {
    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
}

@Composable
private fun DeferredDestinationScreen(destination: DesktopDestination) {
    Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp))) {
        WorkbenchHeading(
            title = destination.label,
            subtitle = "Destino reservado del workbench Desktop.",
        )
        Card(elevation = 2.dp) {
            Text(
                text = "La navegación del workbench está establecida, pero ${destination.label} queda fuera del paquete acotado actual.",
                modifier = Modifier.padding(desktopSpacing(20.dp)),
            )
        }
    }
}

@Composable
private fun WorkbenchHeading(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(4.dp))) {
        Text(text = title, style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
        Text(text = subtitle, style = MaterialTheme.typography.body1)
    }
}

@Composable
private fun WorkbenchStatusBar(
    campaignCount: Int,
    activeCampaign: Campaign?,
) {
    BottomAppBar(backgroundColor = MaterialTheme.colors.surface, elevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = desktopSpacing(16.dp)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Almacén local de campañas: listo", style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$campaignCount ${if (campaignCount == 1) "campaña" else "campañas"} • " +
                    (activeCampaign?.let { "activa: ${it.name}" } ?: "sin campaña activa"),
                style = MaterialTheme.typography.caption,
            )
        }
    }
}

private fun DesktopWorkspaceDensity.navigationWidth(): Dp = when (this) {
    DesktopWorkspaceDensity.COMFORTABLE -> 250.dp
    DesktopWorkspaceDensity.BALANCED -> 220.dp
    DesktopWorkspaceDensity.COMPACT -> 195.dp
    DesktopWorkspaceDensity.DENSE -> 175.dp
}

private fun DesktopWorkspaceDensity.contextPanelWidth(): Dp = when (this) {
    DesktopWorkspaceDensity.COMFORTABLE -> 320.dp
    DesktopWorkspaceDensity.BALANCED -> 280.dp
    DesktopWorkspaceDensity.COMPACT -> 250.dp
    DesktopWorkspaceDensity.DENSE -> 225.dp
}
