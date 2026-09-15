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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
    private val hostedCampaignBootstrapController by lazy {
        AndroidHostedCampaignBootstrapController(database)
    }
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
        super.onDestroy()
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

    Box(modifier = Modifier.fillMaxSize()) {
        when (screen) {
            AppScreen.CAMPAIGNS -> CampaignScreen(
                repository = campaignRepository,
                hostedBootstrap = hostedCampaignBootstrapController,
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
                                screenName = AppScreen.CHARACTERS.name
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

@Composable
private fun CampaignScreen(
    repository: CampaignRepository,
    hostedBootstrap: AndroidHostedCampaignBootstrapController,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var campaigns by remember { mutableStateOf(repository.listCampaigns()) }
    var activeCampaignId by remember { mutableStateOf(repository.activeCampaign()?.id) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var hostedRefreshing by remember { mutableStateOf(false) }

    fun idleHostedStatus(): String {
        val pending = hostedBootstrap.pendingMutationCount()
        return when {
            hostedBootstrap.hasRememberedSession() && pending > 0 ->
                "Sesión hospedada disponible. Hay $pending cambio(s) local(es) pendiente(s) de sincronizar."
            hostedBootstrap.hasRememberedSession() ->
                "Sesión hospedada disponible. Puedes sincronizar las campañas con el servidor."
            pending > 0 ->
                "Sin sesión hospedada. $pending cambio(s) local(es) permanecen guardados y pendientes."
            else ->
                "Sin sesión hospedada en este dispositivo."
        }
    }

    var hostedStatus by remember { mutableStateOf(idleHostedStatus()) }

    fun reload() {
        campaigns = repository.listCampaigns()
        activeCampaignId = repository.activeCampaign()?.id
    }

    fun hostedCampaignSummary(outcome: AndroidHostedCampaignBootstrapOutcome.Success): String =
        when {
            outcome.hostedCampaignCount == 0 ->
                "No hay campañas hospedadas para esta cuenta."
            outcome.conflictCount == 0 ->
                "${outcome.appliedCampaignCount} campaña(s) conciliada(s) desde el servidor."
            else ->
                "${outcome.appliedCampaignCount} campaña(s) conciliada(s); " +
                    "${outcome.conflictCount} conflicto(s) local(es) fueron preservados sin sobrescribir."
        }

    fun applyHostedOutcome(
        outcome: AndroidHostedCampaignBootstrapOutcome,
        focusedCreation: AndroidQueuedHostedCampaignCreation? = null,
    ) {
        when (outcome) {
            is AndroidHostedCampaignBootstrapOutcome.Success -> {
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

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(appSpacingV4(8.dp)),
                        ) {
                            Text(
                                text = "Servidor",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = hostedStatus,
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Button(
                                onClick = { syncHostedCampaigns() },
                                enabled = !hostedRefreshing,
                            ) {
                                Text(if (hostedRefreshing) "Sincronizando…" else "Sincronizar con servidor")
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
