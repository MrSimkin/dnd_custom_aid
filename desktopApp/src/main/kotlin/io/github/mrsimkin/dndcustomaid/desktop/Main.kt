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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.mrsimkin.dndcustomaid.dmui.handCursor
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.campaign.CampaignRepository
import io.github.mrsimkin.dndcustomaid.shared.content.FileMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.db.DesktopDatabaseFactory
import kotlinx.coroutines.launch
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File

fun main() {
    val databaseFactory = DesktopDatabaseFactory()
    val databaseHandle = databaseFactory.create()
    val dataDirectory = DesktopDatabaseFactory.defaultDatabaseFile().parentFile
    val campaignRepository = CampaignRepository(databaseHandle.database)
    val creatureManagerController = DesktopCreatureManagerController(databaseHandle.database)
    val npcManagerController = DesktopNpcManagerController(databaseHandle.database)
    val homebrewRuleManagerController = DesktopHomebrewRuleManagerController(databaseHandle.database)
    val placeManagerController = DesktopPlaceManagerController(databaseHandle.database)
    val pcManagerController = DesktopPcManagerController(databaseHandle.database)
    // Base para DM, package 4: images are stored next to the database, by checksum.
    val mediaBlobs = FileMediaBlobStore(File(dataDirectory, "media"))
    val handoutManagerController = DesktopHandoutManagerController(databaseHandle.database, mediaBlobs)
    // Redesign R3: dungeons are objects with a plan of zones and passages.
    val dungeonEditorController = DesktopDungeonEditorController(databaseHandle.database, mediaBlobs)
    // Base para DM: the live desks, shared with the Android tablet (module dmUi).
    val contentLinksController = DesktopContentLinksController(databaseHandle.database, mediaBlobs)
    val preferencesStore = DesktopPreferencesStore()
    // Owner decision D2: the account is asked for at start and remembered (DPAPI on Windows).
    val hostedAuthController = DesktopHostedAuthController(vault = defaultDesktopCredentialVault(dataDirectory))
    val hostedCampaignController = DesktopHostedCampaignAdministrationController(
        database = databaseHandle.database,
        accessTokens = hostedAuthController,
        mediaBlobs = mediaBlobs,
    )
    val dmPlatform = DesktopDmPlatform(
        DesktopDmKeyValueStore(File(dataDirectory, "dm_workspace.properties")),
        rules = { question, passages -> hostedCampaignController.answerRules(question, passages) },
    )
    val accountSession = DesktopAccountSession(hostedAuthController, hostedCampaignController, dmPlatform.store)

    try {
        application {
            Window(
                onCloseRequest = ::exitApplication,
                title = "D&D Custom Aid",
            ) {
                var preferences by remember { mutableStateOf(preferencesStore.load()) }

                DesktopAppTheme(preferences) {
                    Surface(color = MaterialTheme.colors.background, modifier = Modifier.fillMaxSize()) {
                        DesktopAccountGateScreen(session = accountSession, onExit = ::exitApplication) {
                            ProvideDesktopContentLinks(contentLinksController) {
                                DesktopWorkbench(
                                    database = databaseHandle.database,
                                    mediaBlobs = mediaBlobs,
                                    dmPlatform = dmPlatform,
                                    campaignRepository = campaignRepository,
                                    creatureManagerController = creatureManagerController,
                                    npcManagerController = npcManagerController,
                                    homebrewRuleManagerController = homebrewRuleManagerController,
                                    placeManagerController = placeManagerController,
                                    handoutManagerController = handoutManagerController,
                                    dungeonEditorController = dungeonEditorController,
                                    pcManagerController = pcManagerController,
                                    accountSession = accountSession,
                                    hostedCampaignController = hostedCampaignController,
                                    preferences = preferences,
                                    onPreferencesChange = { updated ->
                                        preferences = updated
                                        preferencesStore.save(updated)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    } finally {
        hostedCampaignController.close()
        hostedAuthController.close()
        databaseHandle.close()
    }
}

/**
 * The left menu of the App de PC, in the order the DM works: prepare, play, consult.
 * Redesign R1/R2 (owner observations P3, P6, G1, G3): names a DM recognizes, one line of purpose each.
 */
internal enum class DesktopDestination(val label: String, val purpose: String, val lower: Boolean = false) {
    HOME("Inicio", "Tu campaña abierta y lo que más usas."),
    DM_DESK("Escritorio DM", "Las mesas en vivo: escena, dungeon, combate y la pantalla del DM."),
    PREPARE("Preparar", "Todo lo que armas antes de jugar: lugares, PNJ, aventuras, dungeons y encuentros."),
    CHARACTERS("Personajes", "Los PJ de la campaña, su jugador y su hoja."),
    RULES("Reglas", "El SRD para leer y tu homebrew, en dos orígenes separados."),
    HANDOUTS("Handouts y mapas", "Imágenes para mostrar a los jugadores y mapas con marcas."),
    CAMPAIGNS("Campañas", "Tus campañas y tu rol en cada una."),
    PLAYERS("Jugadores de la campaña", "Quién está en la campaña y su estado.", lower = true),
    SETTINGS("Configuración", "Tema, letra y tamaño de la App.", lower = true),
    DIAGNOSTICS("Diagnóstico", "Datos técnicos para cuando algo falla.", lower = true),
}

@Composable
private fun DesktopWorkbench(
    database: AppDatabase,
    mediaBlobs: MediaBlobStore,
    dmPlatform: DesktopDmPlatform,
    campaignRepository: CampaignRepository,
    creatureManagerController: DesktopCreatureManagerController,
    npcManagerController: DesktopNpcManagerController,
    homebrewRuleManagerController: DesktopHomebrewRuleManagerController,
    placeManagerController: DesktopPlaceManagerController,
    handoutManagerController: DesktopHandoutManagerController,
    dungeonEditorController: DesktopDungeonEditorController,
    pcManagerController: DesktopPcManagerController,
    accountSession: DesktopAccountSession,
    hostedCampaignController: DesktopHostedCampaignAdministrationController,
    preferences: DesktopPreferences,
    onPreferencesChange: (DesktopPreferences) -> Unit,
) {
    var destination by remember { mutableStateOf(DesktopDestination.HOME) }
    var campaigns by remember { mutableStateOf(campaignRepository.listCampaigns()) }
    var activeCampaign by remember { mutableStateOf(campaignRepository.activeCampaign()) }
    var qaEvents by remember { mutableStateOf(listOf("Inicio de la App de PC")) }

    fun logQa(message: String) {
        qaEvents = (qaEvents + message).takeLast(100)
    }

    fun refreshCampaigns() {
        campaigns = campaignRepository.listCampaigns()
        activeCampaign = campaignRepository.activeCampaign()
    }

    LaunchedEffect(accountSession.campaignsVersion) { refreshCampaigns() }

    fun activateCampaign(campaign: Campaign) {
        campaignRepository.setActiveCampaign(campaign.id)
        refreshCampaigns()
        logQa("Campaña abierta: ${campaign.name} (${campaign.id})")
    }

    fun selectDestination(next: DesktopDestination) {
        if (next != destination) {
            destination = next
            logQa("Navegación: ${next.label}")
        }
    }

    val access = activeCampaign?.let { accountSession.access(it.id) }

    // The Escritorio DM takes the whole window, like on the tablet; «← Mesa de trabajo» comes back here.
    val deskCampaign = activeCampaign
    if (destination == DesktopDestination.DM_DESK && deskCampaign != null && access != DesktopCampaignAccess.PLAYER) {
        DesktopDmWorkspaceScreen(
            campaign = deskCampaign,
            database = database,
            mediaBlobs = mediaBlobs,
            platform = dmPlatform,
            hostedController = hostedCampaignController,
            onExit = { selectDestination(DesktopDestination.HOME) },
            onOpenAppSettings = { selectDestination(DesktopDestination.SETTINGS) },
        )
        return
    }

    Column(Modifier.fillMaxSize()) {
        DesktopAccountBar(session = accountSession, campaignName = activeCampaign?.name, access = access)
        DesktopAccountMessage(accountSession)
        Row(modifier = Modifier.fillMaxSize()) {
            WorkbenchNavigation(
                selected = destination,
                density = preferences.workspaceDensity,
                onSelect = ::selectDestination,
            )
            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(desktopSpacing(24.dp)),
            ) {
                val campaign = activeCampaign
                val dmOnly = destination == DesktopDestination.PREPARE || destination == DesktopDestination.DM_DESK
                when {
                    dmOnly && campaign == null -> ChooseCampaignFirst(destination) { selectDestination(DesktopDestination.CAMPAIGNS) }
                    dmOnly && campaign != null && access == DesktopCampaignAccess.PLAYER ->
                        NotDmNotice(campaignName = campaign.name, session = accountSession, what = destination.label)
                    else -> when (destination) {
                        DesktopDestination.HOME -> HomeScreen(
                            activeCampaign = activeCampaign,
                            access = access,
                            accountSession = accountSession,
                            onOpen = ::selectDestination,
                        )
                        DesktopDestination.CAMPAIGNS -> CampaignsScreen(
                            campaigns = campaigns,
                            activeCampaign = activeCampaign,
                            accountSession = accountSession,
                            onActivate = ::activateCampaign,
                            onOpenDesk = { activateCampaign(it); selectDestination(DesktopDestination.DM_DESK) },
                            onOpenCharacters = { activateCampaign(it); selectDestination(DesktopDestination.CHARACTERS) },
                        )
                        DesktopDestination.CHARACTERS -> DesktopPcManagerScreen(
                            controller = pcManagerController,
                            hostedController = hostedCampaignController,
                            activeCampaign = activeCampaign,
                            onQaEvent = ::logQa,
                        )
                        DesktopDestination.HANDOUTS -> DesktopHandoutManagerScreen(
                            controller = handoutManagerController,
                            activeCampaign = activeCampaign,
                            onQaEvent = ::logQa,
                        )
                        DesktopDestination.PREPARE -> DesktopPrepareScreen(
                            creatureController = creatureManagerController,
                            npcController = npcManagerController,
                            homebrewRuleController = homebrewRuleManagerController,
                            placeController = placeManagerController,
                            dungeonController = dungeonEditorController,
                            database = database,
                            activeCampaign = activeCampaign,
                            onQaEvent = ::logQa,
                        )
                        DesktopDestination.RULES -> DesktopRulesScreen(dmPlatform, database, accountSession, activeCampaign)
                        DesktopDestination.PLAYERS -> DesktopCampaignPlayersScreen(
                            activeCampaign = activeCampaign,
                            access = access,
                            accountSession = accountSession,
                            hostedController = hostedCampaignController,
                            onOpenCampaigns = { selectDestination(DesktopDestination.CAMPAIGNS) },
                            onQaEvent = ::logQa,
                        )
                        DesktopDestination.SETTINGS -> DesktopApplicationSettingsScreen(
                            preferences = preferences,
                            onPreferencesChange = onPreferencesChange,
                        )
                        DesktopDestination.DIAGNOSTICS -> QaDiagnosticsScreen(
                            campaigns = campaigns,
                            activeCampaign = activeCampaign,
                            destination = destination,
                            preferences = preferences,
                            accountSession = accountSession,
                            events = qaEvents,
                        )
                        // Reached only without a campaign (handled above).
                        DesktopDestination.DM_DESK -> ChooseCampaignFirst(destination) { selectDestination(DesktopDestination.CAMPAIGNS) }
                    }
                }
            }
        }
    }
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
            val (main, lower) = DesktopDestination.entries.partition { !it.lower }
            main.forEach { NavigationEntry(it, it == selected, onSelect) }
            Spacer(Modifier.weight(1f))
            Divider()
            lower.forEach { NavigationEntry(it, it == selected, onSelect) }
        }
    }
}

@Composable
private fun NavigationEntry(destination: DesktopDestination, selected: Boolean, onSelect: (DesktopDestination) -> Unit) {
    if (selected) {
        Button(onClick = { onSelect(destination) }, modifier = Modifier.fillMaxWidth().handCursor()) {
            Text(destination.label)
        }
    } else {
        TextButton(onClick = { onSelect(destination) }, modifier = Modifier.fillMaxWidth().handCursor()) {
            Text(destination.label)
        }
    }
}

@Composable
private fun ChooseCampaignFirst(destination: DesktopDestination, onOpenCampaigns: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp))) {
        WorkbenchHeading(title = destination.label, subtitle = destination.purpose)
        Text("Primero abre una campaña: ${destination.label} trabaja sobre la campaña abierta.")
        Button(onClick = onOpenCampaigns) { Text("Ir a Campañas") }
    }
}

/** «Inicio»: the open campaign, your role and the three things a DM does most. */
@Composable
private fun HomeScreen(
    activeCampaign: Campaign?,
    access: DesktopCampaignAccess?,
    accountSession: DesktopAccountSession,
    onOpen: (DesktopDestination) -> Unit,
) {
    val scope = rememberCoroutineScope()
    Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp))) {
        WorkbenchHeading(title = "Inicio", subtitle = DesktopDestination.HOME.purpose)
        if (activeCampaign == null) {
            Card(elevation = 2.dp, modifier = Modifier.widthIn(max = 760.dp)) {
                Column(Modifier.padding(desktopSpacing(20.dp)), verticalArrangement = Arrangement.spacedBy(desktopSpacing(10.dp))) {
                    Text("Aún no abres una campaña", style = MaterialTheme.typography.h6)
                    Text("Elige una de tus campañas o crea una nueva. Al crearla, quedas como su DM.")
                    Button(onClick = { onOpen(DesktopDestination.CAMPAIGNS) }) { Text("Ir a Campañas") }
                }
            }
            return@Column
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(desktopSpacing(10.dp))) {
            Text(activeCampaign.name, style = MaterialTheme.typography.h5, fontWeight = FontWeight.Bold)
            access?.let { AccessChip(it) }
        }
        if (access == DesktopCampaignAccess.PLAYER) {
            NotDmNotice(campaignName = activeCampaign.name, session = accountSession, what = "El Escritorio DM")
            return@Column
        }
        Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp))) {
            BigAction("Abrir Escritorio DM", "Las mesas en vivo", primary = true) { onOpen(DesktopDestination.DM_DESK) }
            BigAction("Preparar", "Lugares, PNJ, aventuras, dungeons") { onOpen(DesktopDestination.PREPARE) }
            BigAction("⟳ Sincronizar", accountSession.syncStatus()) { scope.launch { accountSession.synchronize() } }
        }
        Text(
            "Lo que preparas aquí llega a la tablet al sincronizar, y lo que cambias en la tablet llega aquí.",
            style = MaterialTheme.typography.body2,
        )
    }
}

@Composable
private fun BigAction(title: String, subtitle: String, primary: Boolean = false, onClick: () -> Unit) {
    val content: @Composable () -> Unit = {
        Column(Modifier.padding(vertical = 10.dp, horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.subtitle1, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.caption)
        }
    }
    if (primary) {
        Button(onClick = onClick, modifier = Modifier.widthIn(min = 200.dp).height(76.dp).handCursor()) { content() }
    } else {
        OutlinedButton(onClick = onClick, modifier = Modifier.widthIn(min = 200.dp).height(76.dp).handCursor()) { content() }
    }
}

/**
 * «Campañas»: one stable list with your role in each (owner observation G3), the same on the
 * tablet. A new campaign is created on the server with you as its DM.
 */
@Composable
private fun CampaignsScreen(
    campaigns: List<Campaign>,
    activeCampaign: Campaign?,
    accountSession: DesktopAccountSession,
    onActivate: (Campaign) -> Unit,
    onOpenDesk: (Campaign) -> Unit,
    onOpenCharacters: (Campaign) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var newCampaignName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp)),
    ) {
        WorkbenchHeading(title = "Campañas", subtitle = DesktopDestination.CAMPAIGNS.purpose)

        Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
            Row(
                modifier = Modifier.padding(desktopSpacing(16.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp)),
            ) {
                TextField(
                    value = newCampaignName,
                    onValueChange = { newCampaignName = it },
                    label = { Text("Nombre de la nueva campaña") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                Button(
                    onClick = {
                        val name = newCampaignName
                        scope.launch { if (accountSession.createCampaign(name)) newCampaignName = "" }
                    },
                    enabled = newCampaignName.isNotBlank() && !accountSession.busy,
                ) {
                    Text(if (accountSession.busy) "Creando…" else "＋ Nueva campaña")
                }
            }
        }

        if (campaigns.isEmpty()) {
            Text("Aún no tienes campañas. Crea una arriba: quedarás como su DM.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp)),
            ) {
                items(items = campaigns, key = { it.id.toString() }) { campaign ->
                    val isActive = campaign.id == activeCampaign?.id
                    val access = accountSession.access(campaign.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .handCursor()
                            .clickable { onActivate(campaign) },
                        elevation = if (isActive) 6.dp else 1.dp,
                    ) {
                        Column(Modifier.padding(desktopSpacing(16.dp)), verticalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(desktopSpacing(10.dp))) {
                                Text(
                                    text = campaign.name,
                                    style = MaterialTheme.typography.subtitle1,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                )
                                AccessChip(access)
                                if (isActive) Text("· abierta", style = MaterialTheme.typography.caption)
                                Spacer(Modifier.weight(1f))
                                if (access == DesktopCampaignAccess.PLAYER) {
                                    OutlinedButton(onClick = { onOpenCharacters(campaign) }) { Text("Ver mis PJ") }
                                } else {
                                    Button(onClick = { onOpenDesk(campaign) }) { Text("Abrir Escritorio DM") }
                                }
                            }
                            when (access) {
                                DesktopCampaignAccess.PLAYER -> Text(
                                    "Con ${accountSession.email ?: "esta cuenta"} eres Jugador aquí. Para prepararla o dirigirla, entra con la cuenta que es DM.",
                                    style = MaterialTheme.typography.caption,
                                )
                                DesktopCampaignAccess.LOCAL_ONLY -> Text(
                                    "Esta campaña está solo en este equipo: no llega a la tablet.",
                                    style = MaterialTheme.typography.caption,
                                )
                                DesktopCampaignAccess.DM -> Unit
                            }
                        }
                    }
                }
            }
        }
    }
}

/** «Reglas» (redesign R4): the SRD to read and the DM's homebrew library, shared with the tablet. */
@Composable
private fun DesktopRulesScreen(
    platform: DesktopDmPlatform,
    database: AppDatabase,
    accountSession: DesktopAccountSession,
    activeCampaign: Campaign?,
) {
    androidx.compose.material3.MaterialTheme(colorScheme = desktopDmColorScheme(), typography = desktopDmTypography()) {
        androidx.compose.runtime.CompositionLocalProvider(io.github.mrsimkin.dndcustomaid.dmui.LocalDmPlatform provides platform) {
            io.github.mrsimkin.dndcustomaid.dmui.RulesLibraryScreen(
                platform = platform,
                database = database,
                ownerAccountId = accountSession.accountId,
                campaignId = activeCampaign?.id,
            )
        }
    }
}

@Composable
private fun QaDiagnosticsScreen(
    campaigns: List<Campaign>,
    activeCampaign: Campaign?,
    destination: DesktopDestination,
    preferences: DesktopPreferences,
    accountSession: DesktopAccountSession,
    events: List<String>,
) {
    val snapshot = buildQaSnapshot(
        campaigns = campaigns,
        activeCampaign = activeCampaign,
        destination = destination,
        preferences = preferences,
        accountSession = accountSession,
        events = events,
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(desktopSpacing(16.dp)),
    ) {
        item { WorkbenchHeading(title = "Diagnóstico", subtitle = DesktopDestination.DIAGNOSTICS.purpose) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(desktopSpacing(8.dp))) {
                Button(onClick = { copyToClipboard(snapshot) }) { Text("Copiar diagnóstico") }
                Text(
                    "También puedes seleccionar el texto a mano.",
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = 2.dp) {
                SelectionContainer {
                    Text(snapshot, modifier = Modifier.padding(desktopSpacing(18.dp)), style = MaterialTheme.typography.body2)
                }
            }
        }
        item {
            Text("El diagnóstico nunca incluye claves ni códigos de acceso.", style = MaterialTheme.typography.caption)
        }
    }
}

private fun buildQaSnapshot(
    campaigns: List<Campaign>,
    activeCampaign: Campaign?,
    destination: DesktopDestination,
    preferences: DesktopPreferences,
    accountSession: DesktopAccountSession,
    events: List<String>,
): String = buildString {
    appendLine("D&D Custom Aid — App de PC · diagnóstico")
    appendLine("SO: ${System.getProperty("os.name")} ${System.getProperty("os.version")} (${System.getProperty("os.arch")})")
    appendLine("Java: ${System.getProperty("java.version")}")
    appendLine("Base local: ${DesktopDatabaseFactory.defaultDatabaseFile().absolutePath}")
    appendLine("Preferencias: ${DesktopPreferencesStore.defaultPreferencesFile()}")
    appendLine("Servidor: ${DesktopHostedDevelopmentEnvironment.HOSTED_API_BASE_URL}")
    appendLine("Cuenta: ${if (accountSession.email != null) "abierta" else "sin cuenta"}${if (accountSession.offline) " · sin conexión" else ""}")
    appendLine("Recuerda la cuenta: ${if (accountSession.canRemember) "sí" else "no"}")
    appendLine("Pantalla actual: ${destination.label}")
    appendLine("Campañas: ${campaigns.size}")
    appendLine("Campaña abierta: ${activeCampaign?.name ?: "ninguna"}")
    appendLine("ID campaña abierta: ${activeCampaign?.id ?: "n/a"}")
    appendLine("Rol: ${activeCampaign?.let { accountSession.access(it.id).label } ?: "n/a"}")
    appendLine(
        "UI: tema=${preferences.themeChoice.label}; fuente=${preferences.fontChoice.label}; " +
            "texto=${preferences.fontScalePercent}%; espacios=${preferences.spacingScalePercent}%; " +
            "densidad=${preferences.workspaceDensity.label}",
    )
    appendLine("Eventos:")
    if (events.isEmpty()) appendLine("- ninguno") else events.forEach { appendLine("- $it") }
}

private fun copyToClipboard(text: String) {
    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)
}

/** Short help for each screen (redesign A14, decision D14): three or four lines, opened on demand. */
internal val SCREEN_HELP: Map<String, String> = mapOf(
    "Inicio" to "1) Arriba ves la campaña abierta y tu rol. 2) «Abrir Escritorio DM» lleva a las mesas en vivo. " +
        "3) «Preparar» es donde armas lugares, PNJ, aventuras y dungeons. 4) «⟳ Sincronizar» envía lo de este equipo a la tablet y trae lo de la tablet.",
    "Campañas" to "1) Cada campaña muestra tu rol: DM, Jugador o «Solo en este equipo». 2) Toca una para abrirla. " +
        "3) «＋ Nueva campaña» la crea en el servidor contigo como DM. 4) Si eres Jugador, la App te dice qué cuenta usar.",
    "Preparar" to "1) Cada tarjeta es algo que preparas antes de jugar. 2) Dentro: busca, crea y edita a la derecha. " +
        "3) Los vínculos (dónde está, quién atiende, qué escena) se eligen de listas. 4) Todo llega a la tablet al sincronizar.",
    "Jugadores de la campaña" to "1) Quién está en la campaña abierta y su estado. 2) Solo el DM ve esta lista. " +
        "3) Expulsar o bloquear pide confirmación y no borra personajes.",
    "Diagnóstico" to "Datos técnicos para cuando algo falla. «Copiar diagnóstico» los copia para enviarlos; nunca incluyen claves.",
)

@Composable
internal fun WorkbenchHeading(title: String, subtitle: String, help: String? = SCREEN_HELP[title]) {
    var showHelp by remember(title) { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(desktopSpacing(4.dp))) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(desktopSpacing(12.dp))) {
            Text(text = title, style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)
            if (help != null) {
                TextButton(onClick = { showHelp = !showHelp }, modifier = Modifier.handCursor()) { Text("? ¿Cómo se usa?") }
            }
        }
        Text(text = subtitle, style = MaterialTheme.typography.body1)
        if (showHelp && help != null) {
            Surface(color = MaterialTheme.colors.secondary.copy(alpha = 0.12f), shape = MaterialTheme.shapes.small) {
                Text(help, style = MaterialTheme.typography.body2, modifier = Modifier.padding(desktopSpacing(10.dp)))
            }
        }
    }
}

private fun DesktopWorkspaceDensity.navigationWidth(): Dp = when (this) {
    DesktopWorkspaceDensity.COMFORTABLE -> 250.dp
    DesktopWorkspaceDensity.BALANCED -> 230.dp
    DesktopWorkspaceDensity.COMPACT -> 210.dp
    DesktopWorkspaceDensity.DENSE -> 195.dp
}
