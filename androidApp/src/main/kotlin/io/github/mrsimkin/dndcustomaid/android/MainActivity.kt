package io.github.mrsimkin.dndcustomaid.android

import io.github.mrsimkin.dndcustomaid.shared.hosted.spanishSummary
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterProvenanceRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterSuccessorRepository
import io.github.mrsimkin.dndcustomaid.shared.db.AndroidDatabaseFactory
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.content.FileMediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.spine.CampaignRole
import kotlinx.coroutines.launch
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
    private val characterProvenanceRepository by lazy { CharacterProvenanceRepository(database) }
    // Base para DM, package 4: handout images live in the App's private files, by checksum.
    private val mediaBlobs by lazy { FileMediaBlobStore(java.io.File(filesDir, "media")) }
    private val hostedCampaignBootstrapController by lazy {
        AndroidHostedCampaignBootstrapController(database, mediaBlobs)
    }
    private val hostedAuthController by lazy { AndroidHostedAuthController() }
    private val appEntryStore by lazy { AppEntryStore(applicationContext) }
    private val uiPreferencesStore by lazy { UiPreferencesStore(applicationContext) }
    private val hapticPreferencesStore by lazy { CharacterHapticPreferencesStore(applicationContext) }
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
                CharacterHapticSettingsProviderV4(store = hapticPreferencesStore) {
                    DndCustomAidApp(
                        database = database,
                        mediaBlobs = mediaBlobs,
                        hostedAuthController = hostedAuthController,
                        entryStore = appEntryStore,
                        campaignRepository = campaignRepository,
                        hostedCampaignBootstrapController = hostedCampaignBootstrapController,
                        characterRepository = characterRepository,
                        characterDirectoryRepository = characterDirectoryRepository,
                        characterBackupRepository = characterBackupRepository,
                        characterClosureRepository = characterClosureRepository,
                        characterSuccessorRepository = characterSuccessorRepository,
                        characterProvenanceRepository = characterProvenanceRepository,
                        characterNavigationPreferenceStore = characterNavigationPreferenceStore,
                        preferences = preferences,
                        onPreferencesChange = ::updatePreferences,
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        hostedCampaignBootstrapController.close()
        hostedAuthController.close()
        super.onDestroy()
    }
}

private enum class AppScreen {
    ENTRY,
    DM_DESK,
    CAMPAIGNS,
    CHARACTERS,
    CHARACTER_EDITOR,
    RULES,
    PLAYER_HANDOUTS,
}

@Composable
private fun DndCustomAidApp(
    database: AppDatabase,
    mediaBlobs: MediaBlobStore,
    hostedAuthController: AndroidHostedAuthController,
    entryStore: AppEntryStore,
    campaignRepository: CampaignRepository,
    hostedCampaignBootstrapController: AndroidHostedCampaignBootstrapController,
    characterRepository: CharacterRepository,
    characterDirectoryRepository: CharacterDirectoryRepository,
    characterBackupRepository: CharacterBackupRepository,
    characterClosureRepository: CharacterClosureRepository,
    characterSuccessorRepository: CharacterSuccessorRepository,
    characterProvenanceRepository: CharacterProvenanceRepository,
    characterNavigationPreferenceStore: CharacterNavigationPreferenceStore,
    preferences: UiPreferences,
    onPreferencesChange: (UiPreferences) -> Unit,
) {
    val initialDmCampaignId = remember {
        entryStore.lastDmCampaignId?.takeIf { id ->
            hostedCampaignBootstrapController.hasRememberedSession() &&
                campaignRepository.campaign(id) != null &&
                campaignRoleFor(database, id, entryStore.accountId) == CampaignRole.DM
        }
    }
    val initialScreen = remember {
        when {
            !hostedCampaignBootstrapController.hasRememberedSession() && !entryStore.localOnlyChosen -> AppScreen.ENTRY
            initialDmCampaignId != null -> AppScreen.DM_DESK
            else -> AppScreen.CHARACTERS
        }
    }
    var screenName by rememberSaveable { mutableStateOf(initialScreen.name) }
    var dmCampaignIdText by rememberSaveable { mutableStateOf(initialDmCampaignId?.toString()) }
    var selectedCharacterId by rememberSaveable { mutableStateOf<String?>(null) }
    // Where Back from a PC sheet returns: the player's PC list or the Pantalla del DM.
    var editorReturnName by rememberSaveable { mutableStateOf(AppScreen.CHARACTERS.name) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    // Redesign A7: the campaign whose shared handouts are open.
    var handoutsCampaignIdText by rememberSaveable { mutableStateOf<String?>(null) }

    val screen = runCatching { AppScreen.valueOf(screenName) }.getOrDefault(AppScreen.CHARACTERS)

    BackHandler(enabled = showSettings) {
        showSettings = false
    }
    BackHandler(enabled = !showSettings && screen == AppScreen.PLAYER_HANDOUTS) {
        screenName = AppScreen.CAMPAIGNS.name
    }
    BackHandler(enabled = !showSettings && screen == AppScreen.RULES) {
        screenName = AppScreen.CAMPAIGNS.name
    }
    BackHandler(enabled = !showSettings && screen == AppScreen.CAMPAIGNS) {
        selectedCharacterId = null
        screenName = AppScreen.CHARACTERS.name
    }
    // Entry reached from «Entrar con mi cuenta»: Back returns to the campaigns, not out of the App.
    BackHandler(enabled = !showSettings && screen == AppScreen.ENTRY && entryStore.localOnlyChosen) {
        screenName = AppScreen.CAMPAIGNS.name
    }
    BackHandler(enabled = !showSettings && screen == AppScreen.DM_DESK) {
        entryStore.lastDmCampaignId = null
        screenName = AppScreen.CAMPAIGNS.name
    }
    BackHandler(enabled = !showSettings && screen == AppScreen.CHARACTER_EDITOR) {
        selectedCharacterId = null
        screenName = editorReturnName
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
                editorReturnName = AppScreen.CHARACTERS.name
                screenName = AppScreen.CHARACTER_EDITOR.name
            },
        )
    }

    // Redesign R5 (D8, D9): every «Añadir» of the PC editor can pick from the SRD, then the homebrew.
    val rulesContext = androidx.compose.ui.platform.LocalContext.current
    val rulesPlatform = remember { AndroidDmPlatform(rulesContext, hostedCampaignBootstrapController) }
    val rulesSources = remember {
        RulesSources(
            loadCatalogs = { io.github.mrsimkin.dndcustomaid.dmui.SrdCatalogAssets.catalogs(rulesPlatform) },
            homebrew = {
                val repository = io.github.mrsimkin.dndcustomaid.shared.content.RulesEntryContentRepository(database)
                val personal = entryStore.accountId?.let { runCatching { repository.listPersonal(it) }.getOrDefault(emptyList()) }.orEmpty()
                val campaign = campaignRepository.activeCampaign()?.id?.let { repository.listCampaign(it) }.orEmpty()
                (personal + campaign).distinctBy { it.item.identity.id }
            },
        )
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalRulesSources provides rulesSources,
        io.github.mrsimkin.dndcustomaid.dmui.LocalDmPlatform provides rulesPlatform,
    ) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (screen) {
            AppScreen.ENTRY -> AppEntryScreen(
                authController = hostedAuthController,
                onSignedIn = { email ->
                    entryStore.localOnlyChosen = false
                    entryStore.accountEmail = email
                    val outcome = hostedCampaignBootstrapController.refresh()
                    if (outcome is AndroidHostedCampaignBootstrapOutcome.Success) {
                        entryStore.accountId = outcome.accountId
                    }
                    screenName = AppScreen.CAMPAIGNS.name
                },
                onContinueWithoutAccount = {
                    entryStore.localOnlyChosen = true
                    screenName = AppScreen.CHARACTERS.name
                },
            )

            AppScreen.DM_DESK -> {
                val campaign = dmCampaignIdText
                    ?.let { runCatching { Uuid.parse(it) }.getOrNull() }
                    ?.let(campaignRepository::campaign)
                if (campaign == null) {
                    LaunchedEffect(Unit) { screenName = AppScreen.CAMPAIGNS.name }
                } else {
                    AndroidDmWorkspaceScreen(
                        campaign = campaign,
                        database = database,
                        mediaBlobs = mediaBlobs,
                        hostedBootstrap = hostedCampaignBootstrapController,
                        accountId = entryStore.accountId,
                        onExitToCampaigns = {
                            entryStore.lastDmCampaignId = null
                            screenName = AppScreen.CAMPAIGNS.name
                        },
                        onOpenAppSettings = { showSettings = true },
                    )
                }
            }

            AppScreen.CAMPAIGNS -> CampaignScreen(
                database = database,
                repository = campaignRepository,
                hostedBootstrap = hostedCampaignBootstrapController,
                entryStore = entryStore,
                onBack = {
                    selectedCharacterId = null
                    screenName = AppScreen.CHARACTERS.name
                },
                onOpenSettings = { showSettings = true },
                onOpenCampaign = { campaign, role ->
                    campaignRepository.setActiveCampaign(campaign.id)
                    if (role == CampaignRole.DM) {
                        entryStore.lastDmCampaignId = campaign.id
                        dmCampaignIdText = campaign.id.toString()
                        screenName = AppScreen.DM_DESK.name
                    } else {
                        entryStore.lastDmCampaignId = null
                        selectedCharacterId = null
                        screenName = AppScreen.CHARACTERS.name
                    }
                },
                onSignIn = { screenName = AppScreen.ENTRY.name },
                onOpenRules = { screenName = AppScreen.RULES.name },
                onOpenHandouts = { campaign ->
                    handoutsCampaignIdText = campaign.id.toString()
                    screenName = AppScreen.PLAYER_HANDOUTS.name
                },
                onSignOut = {
                    hostedAuthController.signOut()
                    entryStore.clearAccount()
                    screenName = AppScreen.ENTRY.name
                },
            )

            AppScreen.CHARACTERS -> directory()

            AppScreen.PLAYER_HANDOUTS -> {
                val handoutsCampaign = handoutsCampaignIdText
                    ?.let { runCatching { Uuid.parse(it) }.getOrNull() }
                    ?.let(campaignRepository::campaign)
                if (handoutsCampaign == null) {
                    LaunchedEffect(Unit) { screenName = AppScreen.CAMPAIGNS.name }
                } else {
                    AndroidSharedHandoutsScreen(
                        campaignName = handoutsCampaign.name,
                        campaignId = handoutsCampaign.id,
                        hostedBootstrap = hostedCampaignBootstrapController,
                        onBack = { screenName = AppScreen.CAMPAIGNS.name },
                    )
                }
            }

            AppScreen.RULES -> AndroidRulesLibraryScreen(
                database = database,
                hostedBootstrap = hostedCampaignBootstrapController,
                accountId = entryStore.accountId,
                campaignId = campaignRepository.activeCampaign()?.id,
                onBack = { screenName = AppScreen.CAMPAIGNS.name },
            )

            AppScreen.CHARACTER_EDITOR -> {
                val characterId = selectedCharacterId?.let { runCatching { Uuid.parse(it) }.getOrNull() }
                if (characterId == null) {
                    directory()
                } else {
                    CharacterPcSettingsStateProviderV4(
                        characterId = characterId,
                        characterRepository = characterRepository,
                        closureRepository = characterClosureRepository,
                        successorRepository = characterSuccessorRepository,
                        provenanceRepository = characterProvenanceRepository,
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
                                screenName = editorReturnName
                            },
                        )
                    }
                }
            }
        }

        if (showSettings) {
            AppSettingsScreen(
                preferences = preferences,
                onPreferencesChange = onPreferencesChange,
                onDismiss = { showSettings = false },
            )
        }
    }
    }
}

@Composable
private fun CampaignScreen(
    database: AppDatabase,
    repository: CampaignRepository,
    hostedBootstrap: AndroidHostedCampaignBootstrapController,
    entryStore: AppEntryStore,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCampaign: (Campaign, CampaignRole?) -> Unit,
    onSignIn: () -> Unit,
    onOpenRules: () -> Unit,
    onOpenHandouts: (Campaign) -> Unit,
    onSignOut: suspend () -> Unit,
) {
    var accountId by remember { mutableStateOf(entryStore.accountId) }
    var signingOut by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var campaigns by remember { mutableStateOf(repository.listCampaigns()) }
    var activeCampaignId by remember { mutableStateOf(repository.activeCampaign()?.id) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var hostedRefreshing by remember { mutableStateOf(false) }

    fun idleHostedStatus(): String {
        val pending = hostedBootstrap.pendingMutationCount()
        return when {
            hostedBootstrap.hasRememberedSession() && pending > 0 ->
                "Hay $pending cambio(s) de esta tablet esperando para sincronizar."
            hostedBootstrap.hasRememberedSession() ->
                "Sincronizar trae lo que preparaste en Windows y envía lo de esta tablet."
            pending > 0 ->
                "Sin cuenta abierta. $pending cambio(s) siguen guardados en esta tablet."
            else ->
                "Sin cuenta abierta: el Escritorio DM y la sincronización necesitan tu cuenta."
        }
    }

    var hostedStatus by remember { mutableStateOf(idleHostedStatus()) }

    // Redesign R7: the DM loads the party of a content bundle into one campaign on this tablet.
    val bundleContext = androidx.compose.ui.platform.LocalContext.current
    var bundleCampaignId by remember { mutableStateOf<Uuid?>(null) }
    val bundleLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.OpenDocument(),
    ) { uri ->
        val target = bundleCampaignId
        if (uri == null || target == null) return@rememberLauncherForActivityResult
        hostedStatus = runCatching {
            val text = bundleContext.contentResolver.openInputStream(uri)!!.use { it.readBytes().decodeToString() }
            val bundle = io.github.mrsimkin.dndcustomaid.shared.content.parseContentBundle(text)
            io.github.mrsimkin.dndcustomaid.shared.content.ContentBundleImporter(database, null) { System.currentTimeMillis() / 1000L }
                .load(bundle, target, io.github.mrsimkin.dndcustomaid.shared.content.ContentBundleParts.CHARACTERS, entryStore.accountId) { null }
                .spanishSummary()
        }.getOrElse { "No se pudo leer el paquete: ${it.message}" }
        campaigns = repository.listCampaigns()
    }

    fun reload() {
        campaigns = repository.listCampaigns()
        activeCampaignId = repository.activeCampaign()?.id
    }

    fun hostedCampaignSummary(outcome: AndroidHostedCampaignBootstrapOutcome.Success): String =
        when {
            outcome.hostedCampaignCount == 0 ->
                "No hay campañas hospedadas para esta cuenta."
            outcome.conflictCount == 0 ->
                "${outcome.appliedCampaignCount} campaña(s) conciliada(s); " +
                    "${outcome.hostedPcCount} PC hospedado(s) revisado(s)."
            else ->
                "${outcome.appliedCampaignCount} campaña(s) conciliada(s); " +
                    "${outcome.campaignConflictCount} conflicto(s) de campaña y " +
                    "${outcome.pcConflictCount} conflicto(s) de PC fueron preservados sin sobrescribir."
        }

    fun applyHostedOutcome(
        outcome: AndroidHostedCampaignBootstrapOutcome,
        focusedCreation: AndroidQueuedHostedCampaignCreation? = null,
    ) {
        when (outcome) {
            is AndroidHostedCampaignBootstrapOutcome.Success -> {
                outcome.accountId?.let {
                    entryStore.accountId = it
                    accountId = it
                }
                reload()
                val campaignSummary = hostedCampaignSummary(outcome)
                if (focusedCreation != null) {
                    hostedStatus = when (hostedBootstrap.mutationState(focusedCreation.mutationId)) {
                        AndroidHostedMutationState.ACKNOWLEDGED ->
                            "«${focusedCreation.campaign.name}» quedó guardada localmente y confirmada por el servidor. $campaignSummary"
                        AndroidHostedMutationState.READY ->
                            "«${focusedCreation.campaign.name}» quedó guardada localmente. Su envío sigue pendiente para reintentar. $campaignSummary"
                        AndroidHostedMutationState.BLOCKED ->
                            "«${focusedCreation.campaign.name}» quedó guardada localmente, pero el servidor bloqueó su envío. La copia local no fue eliminada. $campaignSummary"
                    }
                } else {
                    val deliveryParts = mutableListOf<String>()
                    if (outcome.acknowledgedMutationCount > 0) {
                        deliveryParts += "${outcome.acknowledgedMutationCount} cambio(s) local(es) confirmado(s) por el servidor."
                    }
                    if (outcome.retryableMutationCount > 0) {
                        deliveryParts += "${outcome.retryableMutationCount} cambio(s) siguen pendientes para reintentar."
                    }
                    if (outcome.blockedMutationCount > 0) {
                        deliveryParts += "${outcome.blockedMutationCount} cambio(s) quedaron bloqueados y requieren revisión."
                    }
                    deliveryParts += campaignSummary
                    outcome.dmContent?.let { deliveryParts += it.spanishSummary() }
                    if (outcome.dmContentError != null) {
                        deliveryParts += "El contenido del DM no se pudo sincronizar esta vez; queda guardado para reintentar."
                    }
                    hostedStatus = deliveryParts.joinToString(" ")
                }
            }

            AndroidHostedCampaignBootstrapOutcome.NoRememberedSession -> {
                hostedStatus = if (focusedCreation == null) {
                    idleHostedStatus()
                } else {
                    "«${focusedCreation.campaign.name}» quedó guardada localmente y pendiente. Vuelve a autenticarte para enviarla al servidor."
                }
            }

            is AndroidHostedCampaignBootstrapOutcome.Failure -> {
                if (focusedCreation == null) {
                    hostedStatus = outcome.message
                } else {
                    hostedStatus = when (hostedBootstrap.mutationState(focusedCreation.mutationId)) {
                        AndroidHostedMutationState.ACKNOWLEDGED ->
                            "«${focusedCreation.campaign.name}» fue recibida por el servidor, pero no se pudo completar la lectura de confirmación. ${outcome.message}"
                        AndroidHostedMutationState.READY ->
                            "«${focusedCreation.campaign.name}» quedó guardada localmente y pendiente para reintentar. ${outcome.message}"
                        AndroidHostedMutationState.BLOCKED ->
                            "«${focusedCreation.campaign.name}» quedó guardada localmente, pero su envío está bloqueado. ${outcome.message}"
                    }
                }
            }
        }
    }

    fun syncHostedCampaigns(focusedCreation: AndroidQueuedHostedCampaignCreation? = null) {
        if (hostedRefreshing) {
            if (focusedCreation != null) {
                hostedStatus =
                    "«${focusedCreation.campaign.name}» quedó guardada localmente. Ya hay una sincronización en curso; " +
                    "si este cambio no entra en ella, permanecerá pendiente para el próximo intento."
            }
            return
        }

        coroutineScope.launch {
            hostedRefreshing = true
            try {
                applyHostedOutcome(
                    outcome = hostedBootstrap.refresh(),
                    focusedCreation = focusedCreation,
                )
            } finally {
                hostedRefreshing = false
            }
        }
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
                                text = "Tus campañas",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Text(
                                text = "Abre una campaña: como DM entras al Escritorio DM; como jugador, a tus PJ.",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        TextButton(onClick = onOpenRules) { Text("📖 Reglas") }
                        StableSettingsIconButton(onClick = onOpenSettings)
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                        ) {
                            Text(
                                text = "Tu cuenta",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            if (hostedBootstrap.hasRememberedSession()) {
                                Text(
                                    text = "Entraste como ${entryStore.accountEmail ?: "tu cuenta"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            Text(
                                text = hostedStatus,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            if (hostedBootstrap.hasRememberedSession()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Button(
                                        onClick = { syncHostedCampaigns() },
                                        enabled = !hostedRefreshing && !signingOut,
                                    ) {
                                        Text(if (hostedRefreshing) "Sincronizando…" else "Sincronizar")
                                    }
                                    TextButton(
                                        onClick = {
                                            signingOut = true
                                            coroutineScope.launch {
                                                try {
                                                    onSignOut()
                                                } finally {
                                                    signingOut = false
                                                }
                                            }
                                        },
                                        enabled = !hostedRefreshing && !signingOut,
                                    ) {
                                        Text("Cambiar usuario")
                                    }
                                    TextButton(
                                        onClick = {
                                            signingOut = true
                                            coroutineScope.launch {
                                                try {
                                                    onSignOut()
                                                } finally {
                                                    signingOut = false
                                                }
                                            }
                                        },
                                        enabled = !hostedRefreshing && !signingOut,
                                    ) {
                                        Text("Cerrar sesión")
                                    }
                                }
                            } else {
                                Button(onClick = onSignIn) {
                                    Text("Entrar con mi cuenta")
                                }
                            }
                        }
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
                        val role = campaignRoleFor(database, campaign.id, accountId)
                        CampaignCard(
                            campaign = campaign,
                            isActive = campaign.id == activeCampaignId,
                            role = role,
                            signedIn = hostedBootstrap.hasRememberedSession(),
                            accountEmail = entryStore.accountEmail,
                            onOpenHandouts = { onOpenHandouts(campaign) },
                            onLoadParty = {
                                bundleCampaignId = campaign.id
                                bundleLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                            },
                            onSelect = {
                                repository.setActiveCampaign(campaign.id)
                                reload()
                            },
                            onOpen = { onOpenCampaign(campaign, role) },
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
                val queued = hostedBootstrap.createCampaignLocally(name)
                reload()
                showCreateDialog = false

                if (hostedBootstrap.hasRememberedSession()) {
                    hostedStatus = "«${queued.campaign.name}» se creó localmente. Sincronizando con el servidor…"
                    syncHostedCampaigns(queued)
                } else {
                    hostedStatus =
                        "«${queued.campaign.name}» se creó localmente y quedó pendiente de envío. " +
                        "Vuelve a autenticarte para sincronizarla con el servidor."
                }
            },
        )
    }
}

@Composable
private fun CampaignCard(
    campaign: Campaign,
    isActive: Boolean,
    role: CampaignRole?,
    signedIn: Boolean,
    accountEmail: String?,
    onOpenHandouts: () -> Unit,
    onLoadParty: () -> Unit,
    onSelect: () -> Unit,
    onOpen: () -> Unit,
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
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
                        text = "Campaña abierta",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            if (role != null) {
                CampaignRoleBadge(role)
            } else {
                Text("Solo en esta tablet", style = MaterialTheme.typography.labelMedium)
            }
            if (role != null && signedIn) {
                TextButton(onClick = onOpenHandouts) { Text("🖼 Handouts") }
            }
            if (role == CampaignRole.DM) {
                TextButton(onClick = onLoadParty) { Text("📦 PJ de un paquete") }
            }
            TextButton(onClick = onOpen) {
                Text(if (role == CampaignRole.DM) "Abrir Escritorio DM" else "Ver mis PJ")
            }
        }
        // Owner decision D1: the DM desk opens only with the DM's account, and the App says why.
        val explanation = when {
            role == CampaignRole.PLAYER ->
                "Con ${accountEmail ?: "esta cuenta"} eres Jugador aquí. El Escritorio DM se abre con la cuenta que es DM de esta campaña: usa «Cambiar usuario»."
            role == null && signedIn ->
                "Esta campaña está solo en esta tablet: tu cuenta no figura en ella en el servidor."
            role == null ->
                "Entra con tu cuenta para ver tu rol en esta campaña."
            else -> null
        }
        explanation?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
            )
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
            CharacterCompactOutlinedTextFieldV4(
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
