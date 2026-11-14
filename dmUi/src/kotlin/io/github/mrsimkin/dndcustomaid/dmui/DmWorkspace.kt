package io.github.mrsimkin.dndcustomaid.dmui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.focusable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.mrsimkin.dndcustomaid.shared.campaign.Campaign
import io.github.mrsimkin.dndcustomaid.shared.character.CharacterRepository
import io.github.mrsimkin.dndcustomaid.shared.content.HandoutContentRepository
import io.github.mrsimkin.dndcustomaid.shared.content.MediaBlobStore
import io.github.mrsimkin.dndcustomaid.shared.db.AppDatabase
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatDocument
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatEngine
import io.github.mrsimkin.dndcustomaid.shared.dm.CombatStatus
import io.github.mrsimkin.dndcustomaid.shared.dm.DmCombatRepository
import io.github.mrsimkin.dndcustomaid.shared.dm.effective
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

/*
 * Base para DM · Escritorio DM (approved design DM_UX_APROBADA.md §1, owner decisions P28-P31).
 *
 * A fixed side rail with every open desk (one tap to switch; each desk remembers where it was),
 * the overview with a card per desk, tools that open as a side panel over the current desk, and
 * the sync status. Settings offer top tabs instead of the rail. Phones get a bottom bar.
 */

/** Which desk is on screen. Encoded as text so it survives rotation and is remembered per campaign. */
internal sealed interface DeskRef {
    val key: String

    data object Overview : DeskRef { override val key = "overview" }
    data object DmScreen : DeskRef { override val key = "dm" }
    data object Scene : DeskRef { override val key = "scene" }
    data object Dungeon : DeskRef { override val key = "dungeon" }
    data class Combat(val id: Uuid) : DeskRef { override val key = "combat:$id" }

    companion object {
        fun parse(key: String?): DeskRef = when {
            key == null -> Overview
            key == "dm" -> DmScreen
            key == "scene" -> Scene
            key == "dungeon" -> Dungeon
            key.startsWith("combat:") -> runCatching { Combat(Uuid.parse(key.removePrefix("combat:"))) }.getOrDefault(Overview)
            else -> Overview
        }
    }
}

/** What the side panel shows (P30): a tool, a PC quick sheet, a reference card or a full sheet. */
internal sealed interface DeskPanel {
    data object Rules : DeskPanel
    data object Handouts : DeskPanel
    data object Notes : DeskPanel
    data object Clocks : DeskPanel
    data class PcQuickSheet(val characterId: Uuid) : DeskPanel
    data class PcFullSheet(val characterId: Uuid) : DeskPanel
    data class ReferenceCard(val cardId: String) : DeskPanel
}

internal enum class DmNavStyle(val label: String) {
    RAIL("Barra lateral"),
    TOP_TABS("Pestañas arriba"),
}

/** Per-device presentation state (D-0068 §2: kept apart from shared campaign state). */
internal class DmWorkspacePreferences(private val store: DmKeyValueStore) {
    var navStyle: DmNavStyle
        get() = runCatching { DmNavStyle.valueOf(store.getString("nav_style") ?: "") }.getOrDefault(DmNavStyle.RAIL)
        set(value) = store.putString("nav_style", value.name)

    fun lastDesk(campaignId: Uuid): String? = store.getString("last_desk_$campaignId")

    fun setLastDesk(campaignId: Uuid, key: String) = store.putString("last_desk_$campaignId", key)

    var lastSyncEpochSeconds: Long
        get() = store.getString("last_sync")?.toLongOrNull() ?: 0L
        set(value) = store.putString("last_sync", value.toString())

    fun recentReferenceCards(): List<String> = store.getString("recent_reference").orEmpty().split(',').filter { it.isNotBlank() }

    fun pushRecentReferenceCard(id: String) {
        val list = (listOf(id) + recentReferenceCards().filter { it != id }).take(4)
        store.putString("recent_reference", list.joinToString(","))
    }
}


internal fun syncAgoText(lastSync: Long): String {
    if (lastSync <= 0L) return "Sin sincronizar"
    val minutes = (nowEpochSeconds() - lastSync) / 60
    return when {
        minutes < 1 -> "Sincronizado recién"
        minutes < 60 -> "Sincronizado hace $minutes min"
        minutes < 60 * 24 -> "Sincronizado hace ${minutes / 60} h"
        else -> "Sincronizado hace ${minutes / (60 * 24)} d"
    }
}

/**
 * The DM's Escritorio for one campaign, shared by the Android tablet and the Windows app.
 * [onSync] runs «☁ Sincronizar» with the platform's own server connection.
 */
@Composable
fun DmWorkspaceScreen(
    campaign: Campaign,
    database: AppDatabase,
    mediaBlobs: MediaBlobStore,
    platform: DmPlatform,
    accountId: Uuid?,
    onSync: suspend (report: (String) -> Unit) -> DmSyncResult,
    onExitToCampaigns: () -> Unit,
    onOpenAppSettings: () -> Unit,
) {
    val deskContext = remember(campaign.id) { mutableStateOf<DeskContext?>(null) }
    CompositionLocalProvider(LocalDmPlatform provides platform, LocalDeskContext provides deskContext) {
        DmWorkspaceContent(campaign, database, mediaBlobs, accountId, onSync, onExitToCampaigns, onOpenAppSettings)
    }
}

@Composable
private fun DmWorkspaceContent(
    campaign: Campaign,
    database: AppDatabase,
    mediaBlobs: MediaBlobStore,
    accountId: Uuid?,
    onSync: suspend (report: (String) -> Unit) -> DmSyncResult,
    onExitToCampaigns: () -> Unit,
    onOpenAppSettings: () -> Unit,
) {
    val platform = LocalDmPlatform.current
    val prefs = remember { DmWorkspacePreferences(platform.store) }
    val scope = rememberCoroutineScope()
    var deskKey by rememberSaveable(campaign.id.toString()) { mutableStateOf(prefs.lastDesk(campaign.id) ?: DeskRef.Overview.key) }
    var panel by remember { mutableStateOf<DeskPanel?>(null) }
    // «Atrás»: the desks visited before this one, and the panels opened from inside a panel.
    var history by remember(campaign.id) { mutableStateOf(listOf<String>()) }
    var panelHistory by remember { mutableStateOf(listOf<DeskPanel>()) }
    var navStyle by remember { mutableStateOf(prefs.navStyle) }
    var showDeskSettings by remember { mutableStateOf(false) }
    var version by remember { mutableIntStateOf(0) }
    var syncing by remember { mutableStateOf(false) }
    var syncProgress by remember { mutableStateOf<String?>(null) }
    var lastSync by remember { mutableStateOf(prefs.lastSyncEpochSeconds) }
    var syncMessage by remember { mutableStateOf<String?>(null) }

    val combatRepository = remember { DmCombatRepository(database) }
    val combats = remember(version, campaign.id) { combatRepository.list(campaign.id) }
    val dungeonSubtitle = remember(version, campaign.id) { dungeonSubtitle(database, campaign.id) }
    val openCombats = combats.filter { it.status != CombatStatus.ENDED }.sortedBy { it.createdAtEpochSeconds }
    var desk = DeskRef.parse(deskKey)
    if (desk is DeskRef.Combat && openCombats.none { it.id == (desk as DeskRef.Combat).id }) desk = DeskRef.Overview

    fun open(target: DeskRef) {
        if (target.key != deskKey) history = (history + deskKey).takeLast(30)
        deskKey = target.key
        prefs.setLastDesk(campaign.id, target.key)
    }

    fun openPanel(next: DeskPanel) {
        val current = panel
        panelHistory = if (current != null && current != next) (panelHistory + current).takeLast(20) else emptyList()
        panel = next
    }

    fun togglePanel(next: DeskPanel) {
        panelHistory = emptyList()
        panel = if (panel == next) null else next
    }

    fun closePanel() {
        panel = null
        panelHistory = emptyList()
    }

    fun panelBack() {
        panel = panelHistory.lastOrNull()
        panelHistory = panelHistory.dropLast(1)
    }

    /** «← Atrás», Esc and Alt+←: the panel first, then the previous desk, then out of the DM desks. */
    fun goBack() {
        when {
            panel != null -> if (panelHistory.isNotEmpty()) panelBack() else closePanel()
            history.isNotEmpty() -> {
                val previous = history.last()
                history = history.dropLast(1)
                deskKey = previous
                prefs.setLastDesk(campaign.id, previous)
            }
            else -> onExitToCampaigns()
        }
    }

    fun newCombat() {
        val pcs = CharacterRepository(database).listCharacters(campaign.id)
        val created = CombatEngine.addPcs(CombatEngine.new(campaign.id, "Combate ${combats.size + 1}", nowEpochSeconds()), pcs)
        combatRepository.save(created, nowEpochSeconds())
        version += 1
        open(DeskRef.Combat(created.id))
    }

    fun sync() {
        if (syncing) return
        syncing = true
        scope.launch {
            try {
                val outcome = onSync { syncProgress = it }
                if (outcome.synced) {
                    prefs.lastSyncEpochSeconds = nowEpochSeconds()
                    lastSync = prefs.lastSyncEpochSeconds
                }
                syncMessage = outcome.message
                version += 1
            } finally {
                syncing = false
                syncProgress = null
            }
        }
    }

    var showDeskHelp by remember { mutableStateOf(false) }
    val deskTitle = when (desk) {
        DeskRef.Overview -> "Escritorio"
        DeskRef.DmScreen -> "Pantalla del DM"
        DeskRef.Scene -> "Mesa de Escena"
        DeskRef.Dungeon -> "Mesa de Dungeon"
        is DeskRef.Combat -> openCombats.firstOrNull { it.id == (desk as DeskRef.Combat).id }?.name ?: "Combate"
    }

    val content: @Composable () -> Unit = {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (platform.isDesktop) {
                    TextButton(onClick = ::goBack, modifier = Modifier.handCursor()) { Text("← Atrás") }
                }
                Text(deskTitle, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                TextButton(onClick = { showDeskHelp = !showDeskHelp }, modifier = Modifier.handCursor()) { Text("? ¿Cómo se usa?") }
                TextButton(onClick = { showDeskSettings = true }) { Text("⚙") }
            }
            if (showDeskHelp) {
                androidx.compose.material3.Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                ) {
                    Text(deskHelp(desk), style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(10.dp))
                }
            }
            Box(modifier = Modifier.fillMaxSize()) {
                when (val current = desk) {
                    DeskRef.Overview -> OverviewDesk(
                        campaign = campaign,
                        database = database,
                        combats = openCombats,
                        version = version,
                        onOpen = ::open,
                        onNewCombat = ::newCombat,
                        onOpenPanel = ::openPanel,
                    )
                    DeskRef.DmScreen -> DmScreenDesk(
                        campaign = campaign,
                        database = database,
                        onOpenPanel = ::openPanel,
                    )
                    DeskRef.Scene -> SceneDesk(
                        campaign = campaign,
                        database = database,
                        mediaBlobs = mediaBlobs,
                        version = version,
                    )
                    DeskRef.Dungeon -> DungeonDesk(
                        campaign = campaign,
                        database = database,
                        version = version,
                        onChanged = { version += 1 },
                        onStartCombat = { combat ->
                            combatRepository.save(combat, nowEpochSeconds())
                            version += 1
                            open(DeskRef.Combat(combat.id))
                        },
                    )
                    is DeskRef.Combat -> CombatDesk(
                        campaign = campaign,
                        database = database,
                        accountId = accountId,
                        combatId = current.id,
                        onChanged = { version += 1 },
                        onEnded = {
                            version += 1
                            open(DeskRef.Overview)
                        },
                    )
                }
            }
        }
    }

    val keys = remember { FocusRequester() }
    if (platform.isDesktop) {
        LaunchedEffect(Unit) { runCatching { keys.requestFocus() } }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (!platform.isDesktop) Modifier else Modifier
                    .focusRequester(keys)
                    .onPreviewKeyEvent { event ->
                        val back = event.type == KeyEventType.KeyDown &&
                            (event.key == Key.Escape || (event.isAltPressed && event.key == Key.DirectionLeft))
                        if (back) goBack()
                        back
                    }
                    .focusable(),
            ),
        color = MaterialTheme.colorScheme.background,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val compact = maxWidth < 600.dp
            val panelContent: @Composable (DeskPanel) -> Unit = { shown ->
                DeskPanelContent(
                    panel = shown,
                    campaign = campaign,
                    database = database,
                    mediaBlobs = mediaBlobs,
                    version = version,
                    onChanged = { version += 1 },
                    onOpenPanel = ::openPanel,
                    onClose = ::closePanel,
                    onBack = if (panelHistory.isNotEmpty()) ::panelBack else null,
                )
            }
            when {
                compact -> Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        content()
                        panel?.let { shown ->
                            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) { panelContent(shown) }
                        }
                    }
                    PhoneDeskBar(
                        desk = desk,
                        openCombats = openCombats,
                        onOpen = { panel = null; open(it) },
                        onRules = { togglePanel(DeskPanel.Rules) },
                        onHandouts = { togglePanel(DeskPanel.Handouts) },
                        onExit = onExitToCampaigns,
                    )
                }
                navStyle == DmNavStyle.TOP_TABS -> Column(modifier = Modifier.fillMaxSize()) {
                    DeskTabs(
                        campaign = campaign,
                        desk = desk,
                        openCombats = openCombats,
                        syncText = if (syncing) syncProgress ?: "Sincronizando…" else syncAgoText(lastSync),
                        onOpen = ::open,
                        onNewCombat = ::newCombat,
                        onPanel = ::togglePanel,
                        onSync = ::sync,
                        onExit = onExitToCampaigns,
                    )
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) { content() }
                        panel?.let { SidePanel { panelContent(it) } }
                    }
                }
                else -> Row(modifier = Modifier.fillMaxSize()) {
                    DeskRail(
                        campaign = campaign,
                        dungeonSubtitle = dungeonSubtitle,
                        desk = desk,
                        openCombats = openCombats,
                        syncText = if (syncing) syncProgress ?: "Sincronizando…" else syncAgoText(lastSync),
                        onOpen = ::open,
                        onNewCombat = ::newCombat,
                        onPanel = ::togglePanel,
                        onSync = ::sync,
                        onExit = onExitToCampaigns,
                    )
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) { content() }
                    panel?.let { SidePanel { panelContent(it) } }
                }
            }
        }
    }

    syncMessage?.let { message ->
        AlertDialog(
            onDismissRequest = { syncMessage = null },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = { syncMessage = null }) { Text("Entendido") } },
        )
    }

    if (showDeskSettings) {
        AlertDialog(
            onDismissRequest = { showDeskSettings = false },
            title = { Text("Escritorio") },
            text = {
                Column {
                    Text("Cómo se muestran las mesas", style = MaterialTheme.typography.titleSmall)
                    DmNavStyle.entries.forEach { style ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().clickable {
                                navStyle = style
                                prefs.navStyle = style
                            },
                        ) {
                            RadioButton(selected = navStyle == style, onClick = { navStyle = style; prefs.navStyle = style })
                            Text(style.label)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showDeskSettings = false }) { Text("Cerrar") } },
            dismissButton = {
                TextButton(onClick = {
                    showDeskSettings = false
                    onOpenAppSettings()
                }) { Text("Configuración general") }
            },
        )
    }
}

@Composable
private fun RailItem(title: String, subtitle: String? = null, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                MaterialTheme.shapes.small,
            )
            .handCursor()
            .clickable(onClick = onClick)
            .heightIn(min = 44.dp)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        subtitle?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

internal fun combatRailSubtitle(doc: CombatDocument): String = when (doc.status) {
    CombatStatus.PREPARING -> "preparando"
    CombatStatus.ACTIVE -> {
        // A group with one shared initiative acts as one turn (P33): «Goblin ×3».
        val acting = doc.actingIds.mapNotNull(doc::combatant)
        "ronda ${doc.round}" + when {
            acting.size > 1 -> " · ${acting.first().label.replace(Regex("""\s+\d+$"""), "")} ×${acting.size}"
            acting.size == 1 -> " · ${acting.first().label}"
            else -> ""
        }
    }
    CombatStatus.ENDED -> "terminado"
}

@Composable
private fun DeskRail(
    campaign: Campaign,
    dungeonSubtitle: String,
    desk: DeskRef,
    openCombats: List<CombatDocument>,
    syncText: String,
    onOpen: (DeskRef) -> Unit,
    onNewCombat: () -> Unit,
    onPanel: (DeskPanel) -> Unit,
    onSync: () -> Unit,
    onExit: () -> Unit,
) {
    var menu by remember { mutableStateOf(false) }
    // The rail says where the Mesa de Escena was left (§1: each desk remembers where it was).
    val sceneSubtitle = SceneDeskPreferences(LocalDmPlatform.current.store, campaign.id).openName ?: "buscar o recorrer"
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.width(196.dp).fillMaxHeight()) {
        Column(
            modifier = Modifier.fillMaxHeight().dmScroll().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TextButton(onClick = onExit, modifier = Modifier.handCursor()) { Text(LocalDmPlatform.current.exitLabel, maxLines = 1) }
            Text(campaign.name, style = MaterialTheme.typography.labelMedium, maxLines = 2, modifier = Modifier.padding(horizontal = 10.dp))
            RailItem("🏠 Escritorio", "vista general", desk == DeskRef.Overview) { onOpen(DeskRef.Overview) }
            HorizontalDivider()
            RailItem("Pantalla del DM", null, desk == DeskRef.DmScreen) { onOpen(DeskRef.DmScreen) }
            RailItem("Escena", sceneSubtitle, desk == DeskRef.Scene) { onOpen(DeskRef.Scene) }
            RailItem("Dungeon", dungeonSubtitle, desk == DeskRef.Dungeon) { onOpen(DeskRef.Dungeon) }
            HorizontalDivider()
            openCombats.forEach { doc ->
                RailItem("⚔ ${doc.name}", combatRailSubtitle(doc), desk == DeskRef.Combat(doc.id)) { onOpen(DeskRef.Combat(doc.id)) }
            }
            Box {
                TextButton(onClick = { menu = true }) { Text("＋ Nueva mesa") }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("Nuevo combate") }, onClick = { menu = false; onNewCombat() })
                    DropdownMenuItem(text = { Text("Otra Dungeon (en la Mesa de Dungeon, «＋ Otra»)") }, onClick = { menu = false; onOpen(DeskRef.Dungeon) })
                }
            }
            Spacer(modifier = Modifier.heightIn(min = 12.dp))
            HorizontalDivider()
            RailItem("📖 Reglas", null, false) { onPanel(DeskPanel.Rules) }
            RailItem("🖼 Handouts", null, false) { onPanel(DeskPanel.Handouts) }
            RailItem("📝 Notas", null, false) { onPanel(DeskPanel.Notes) }
            RailItem("⏱ Relojes", null, false) { onPanel(DeskPanel.Clocks) }
            RailItem("☁ Sincronizar", syncText, false, onSync)
        }
    }
}

@Composable
private fun DeskTabs(
    campaign: Campaign,
    desk: DeskRef,
    openCombats: List<CombatDocument>,
    syncText: String,
    onOpen: (DeskRef) -> Unit,
    onNewCombat: () -> Unit,
    onPanel: (DeskPanel) -> Unit,
    onSync: () -> Unit,
    onExit: () -> Unit,
) {
    @Composable
    fun tab(label: String, selected: Boolean, onClick: () -> Unit) {
        Text(
            label,
            style = MaterialTheme.typography.titleSmall,
            color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface, MaterialTheme.shapes.small)
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 10.dp),
        )
    }
    Surface(tonalElevation = 1.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onExit) { Text("←") }
            tab("🏠", desk == DeskRef.Overview) { onOpen(DeskRef.Overview) }
            tab("Pantalla del DM", desk == DeskRef.DmScreen) { onOpen(DeskRef.DmScreen) }
            tab("Escena", desk == DeskRef.Scene) { onOpen(DeskRef.Scene) }
            tab("Dungeon", desk == DeskRef.Dungeon) { onOpen(DeskRef.Dungeon) }
            openCombats.forEach { doc -> tab("⚔ ${doc.name} · ${combatRailSubtitle(doc)}", desk == DeskRef.Combat(doc.id)) { onOpen(DeskRef.Combat(doc.id)) } }
            TextButton(onClick = onNewCombat) { Text("＋ Combate") }
            Spacer(modifier = Modifier.width(16.dp))
            TextButton(onClick = { onPanel(DeskPanel.Rules) }) { Text("📖") }
            TextButton(onClick = { onPanel(DeskPanel.Handouts) }) { Text("🖼") }
            TextButton(onClick = { onPanel(DeskPanel.Notes) }) { Text("📝") }
            TextButton(onClick = { onPanel(DeskPanel.Clocks) }) { Text("⏱") }
            TextButton(onClick = onSync) { Text("☁ $syncText") }
            Text(campaign.name, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun PhoneDeskBar(
    desk: DeskRef,
    openCombats: List<CombatDocument>,
    onOpen: (DeskRef) -> Unit,
    onRules: () -> Unit,
    onHandouts: () -> Unit,
    onExit: () -> Unit,
) {
    var menu by remember { mutableStateOf(false) }
    Surface(tonalElevation = 3.dp) {
        Row(modifier = Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.SpaceAround) {
            TextButton(onClick = { onOpen(DeskRef.Overview) }) { Text(if (desk == DeskRef.Overview) "● 🏠" else "🏠") }
            TextButton(onClick = onRules) { Text("📖") }
            TextButton(onClick = onHandouts) { Text("🖼") }
            Box {
                TextButton(onClick = { menu = true }) { Text("☰ Mesas") }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(text = { Text("Pantalla del DM") }, onClick = { menu = false; onOpen(DeskRef.DmScreen) })
                    DropdownMenuItem(text = { Text("Escena") }, onClick = { menu = false; onOpen(DeskRef.Scene) })
                    DropdownMenuItem(text = { Text("Dungeon") }, onClick = { menu = false; onOpen(DeskRef.Dungeon) })
                    openCombats.forEach { doc ->
                        DropdownMenuItem(text = { Text("⚔ ${doc.name}") }, onClick = { menu = false; onOpen(DeskRef.Combat(doc.id)) })
                    }
                    DropdownMenuItem(text = { Text("← Campañas") }, onClick = { menu = false; onExit() })
                }
            }
        }
    }
}

@Composable
private fun SidePanel(content: @Composable () -> Unit) {
    Surface(tonalElevation = 3.dp, modifier = Modifier.width(400.dp).fillMaxHeight()) { content() }
}

@Composable
private fun OverviewDesk(
    campaign: Campaign,
    database: AppDatabase,
    combats: List<CombatDocument>,
    version: Int,
    onOpen: (DeskRef) -> Unit,
    onNewCombat: () -> Unit,
    onOpenPanel: (DeskPanel) -> Unit,
) {
    val pcs = remember(version, campaign.id) { CharacterRepository(database).listCharacters(campaign.id) }
    val sceneSubtitle = SceneDeskPreferences(LocalDmPlatform.current.store, campaign.id).openName
    val dungeonSubtitle = remember(version, campaign.id) { dungeonSubtitle(database, campaign.id) }
    val bestPerception = pcs.maxByOrNull { it.passivePerception }
    val packages = remember(version, campaign.id) {
        io.github.mrsimkin.dndcustomaid.shared.content.PackageContentRepository(database).listCampaign(campaign.id).associateBy { it.item.identity.id }
    }

    @Composable
    fun deskCard(title: String, lines: List<String>, highlighted: Boolean = false, onClick: () -> Unit) {
        Card(
            onClick = onClick,
            modifier = Modifier.width(260.dp).heightIn(min = 110.dp),
            colors = if (highlighted) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors(),
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                lines.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }
    }

    // Redesign A6 (owner observation G1): what a DM does live at the table, one big tap each.
    @Composable
    fun quick(label: String, primary: Boolean = false, onClick: () -> Unit) {
        val modifier = Modifier.width(170.dp).heightIn(min = 64.dp).handCursor()
        if (primary) {
            androidx.compose.material3.Button(onClick = onClick, modifier = modifier) { Text(label, style = MaterialTheme.typography.titleMedium) }
        } else {
            androidx.compose.material3.OutlinedButton(onClick = onClick, modifier = modifier) { Text(label, style = MaterialTheme.typography.titleMedium) }
        }
    }

    Column(modifier = Modifier.fillMaxSize().dmScroll().padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("En la mesa, a un toque", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            quick("⚔ Iniciativa", primary = true) { onNewCombat() }
            quick("🎭 Escena") { onOpen(DeskRef.Scene) }
            quick("🏰 Dungeon") { onOpen(DeskRef.Dungeon) }
            quick("🛡 Grupo") { onOpen(DeskRef.DmScreen) }
            quick("📖 Reglas") { onOpenPanel(DeskPanel.Rules) }
            quick("📝 Notas") { onOpenPanel(DeskPanel.Notes) }
            quick("🖼 Handouts") { onOpenPanel(DeskPanel.Handouts) }
            quick("⏱ Relojes") { onOpenPanel(DeskPanel.Clocks) }
        }
        Text("Tus mesas", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            deskCard(
                "Pantalla del DM",
                listOfNotNull(
                    "${pcs.size} PJ",
                    bestPerception?.let { "Mejor Percepción pasiva ${it.passivePerception} (${it.name})" },
                ),
            ) { onOpen(DeskRef.DmScreen) }
            deskCard("Escena", listOf(sceneSubtitle?.let { "Último: $it" } ?: "Buscar PNJ, tiendas, lugares y escenas")) { onOpen(DeskRef.Scene) }
            deskCard("Dungeon", listOf(dungeonSubtitle.replaceFirstChar { it.uppercase() })) { onOpen(DeskRef.Dungeon) }
            combats.forEach { doc ->
                val wounded = doc.combatants.count { c ->
                    val max = c.effective(packages).maxHp
                    c.currentHp != null && max != null && !c.defeated && c.currentHp!! * 2 < max
                }
                deskCard(
                    "⚔ ${doc.name}",
                    listOfNotNull(
                        combatRailSubtitle(doc).replaceFirstChar { it.uppercase() },
                        "${doc.combatants.size} participantes",
                        wounded.takeIf { it > 0 }?.let { "$it con menos de la mitad de PV" },
                    ),
                    highlighted = doc.status == CombatStatus.ACTIVE,
                ) { onOpen(DeskRef.Combat(doc.id)) }
            }
            deskCard("＋ Nueva mesa", listOf("Combate: desde un encuentro, del bestiario o improvisado")) { onNewCombat() }
        }
    }
}

@Composable
private fun UpcomingDesk(title: String, lines: List<String>, packageNumber: Int) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("$title · en construcción", style = MaterialTheme.typography.titleMedium)
        Text("Llega con el paquete $packageNumber, según el diseño aprobado:", style = MaterialTheme.typography.bodyMedium)
        lines.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
    }
}

@Composable
private fun DeskPanelContent(
    panel: DeskPanel,
    campaign: Campaign,
    database: AppDatabase,
    mediaBlobs: MediaBlobStore,
    version: Int,
    onChanged: () -> Unit,
    onOpenPanel: (DeskPanel) -> Unit,
    onClose: () -> Unit,
    onBack: (() -> Unit)?,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            // Back to the panel this one was opened from (full sheet → quick sheet, card → rules).
            if (onBack != null) TextButton(onClick = onBack, modifier = Modifier.handCursor()) { Text("←") } else Box(modifier = Modifier.width(10.dp))
            Text(
                when (panel) {
                    DeskPanel.Rules -> "📖 Reglas"
                    DeskPanel.Handouts -> "🖼 Handouts"
                    DeskPanel.Notes -> "📝 Notas"
                    DeskPanel.Clocks -> "⏱ Relojes"
                    is DeskPanel.PcQuickSheet -> "Hoja rápida"
                    is DeskPanel.PcFullSheet -> "Hoja completa (lectura)"
                    is DeskPanel.ReferenceCard -> "Referencia rápida"
                },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = onClose) { Text("Cerrar ✕") }
        }
        HorizontalDivider()
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
            when (panel) {
                DeskPanel.Rules -> RulesPanel(campaign.id, onOpenCard = { onOpenPanel(DeskPanel.ReferenceCard(it)) })
                DeskPanel.Handouts -> {
                    var shareVersion by remember { mutableStateOf(0) }
                    val handouts = remember(campaign.id, shareVersion) { HandoutContentRepository(database).listCampaign(campaign.id) }
                    Column(modifier = Modifier.dmScroll().padding(vertical = 10.dp)) {
                        Text(
                            "Toca una imagen para mostrarla a pantalla completa. «Enviar a los jugadores» la hace aparecer en su App al sincronizar.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        HandoutStrip(handouts, mediaBlobs, onShare = { handout, shared ->
                            HandoutContentRepository(database).update(
                                handout.item.identity.id,
                                handout.item.identity.revision,
                                handout.item.displayName,
                                handout.payload.copy(sharedWithPlayers = shared),
                                nowEpochSeconds(),
                            )
                            shareVersion++
                            onChanged()
                        })
                    }
                }
                DeskPanel.Notes -> NotesPanel(campaign, database, version, onChanged)
                DeskPanel.Clocks -> ClocksPanel(campaign, database, version, onChanged)
                is DeskPanel.PcQuickSheet -> PcQuickSheetPanel(database, panel.characterId, onOpenFull = { onOpenPanel(DeskPanel.PcFullSheet(panel.characterId)) })
                is DeskPanel.PcFullSheet -> PcFullSheetPanel(database, panel.characterId)
                is DeskPanel.ReferenceCard -> ReferenceCardPanel(campaign.id, panel.cardId)
            }
        }
    }
}

/** «turno 3 · Catacumbas» for the open exploration, or «explorar». */
internal fun dungeonSubtitle(database: AppDatabase, campaignId: Uuid): String =
    io.github.mrsimkin.dndcustomaid.shared.dm.DmDungeonRepository(database).list(campaignId).firstOrNull { !it.ended }
        ?.let { "turno ${it.turn} · ${it.dungeon}" } ?: "explorar"


/** Short help for each desk (redesign A14, decision D14). */
internal fun deskHelp(desk: DeskRef): String = when (desk) {
    DeskRef.Overview -> "1) Cada tarjeta es una mesa: toca una para abrirla. 2) La barra lateral cambia de mesa en un toque. " +
        "3) Abajo: 📖 Reglas, 🖼 Handouts, 📝 Notas, ⏱ Relojes y ☁ Sincronizar. 4) «＋ Nueva mesa» abre un combate."
    DeskRef.DmScreen -> "1) Una ficha por PJ con CA, PV, pasivas, velocidad y quién lo juega; lo mejor del grupo va resaltado. " +
        "2) «Vista Grupo» lo muestra como tabla. 3) Toca un PJ para su hoja rápida. 4) La referencia rápida abre reglas a un toque."
    DeskRef.Scene -> "1) Busca un PNJ, lugar o escena por nombre, papel o idea. 2) Toca una ficha para leerla; «📌» la fija. " +
        "3) Desde una ficha, sus vínculos abren lo relacionado. 4) «← Atrás» vuelve a la ficha anterior."
    DeskRef.Dungeon -> "1) Elige el dungeon y la zona de entrada y toca «Empezar». 2) Cada PJ elige su actividad de un toque. " +
        "3) «Cerrar turno» avanza 10 minutos, relojes y registro. 4) Toca una zona del mapa para su ficha; el grupo se arrastra."
    is DeskRef.Combat -> "1) Prepara: agrega PJ y criaturas. 2) Iniciativa: tira o escribe. 3) En curso: un toque por daño, curación o estado. " +
        "4) «Deshacer» corrige el último paso y «Terminar» cierra el combate."
}
